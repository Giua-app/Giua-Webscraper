package com.giua.webscraper;

import java.io.IOException;
import java.util.*;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



/* Giua Webscraper 0.6.1
 *
 *  JSoup: library for webscraping
 *
 */
public class App 
{

	public static class Avviso {    //Da usare come una struttura di C#
		public final String status;
		public final String date;
		public final String receivers;
		public final String objectAvviso;
		public String details;
		public String creator;
		public final int id;		//Indica quanto e' lontano dal primo avviso partendo dall'alto
		public boolean isDetailed;

		Avviso(String status, String data, String destinatari, String oggetto, int id) {    //Costruttore (suona malissimo in italiano)
			this.status = status;
			this.date = data;
			this.receivers = destinatari;
			this.objectAvviso = oggetto;
			this.id = id;
			this.isDetailed = false;
		}

		public String getDetails(){		//carica i dettagli e l'autore dell'avviso simulando il click su Visualizza
			Document allAvvisiHTML = getPage("https://registro.giua.edu.it/genitori/avvisi");
			Document dettagliAvvisoHTML = getPage("https://registro.giua.edu.it" + allAvvisiHTML.getElementsByClass("label label-default").get(this.id).parent().parent().child(4).child(0).attributes().get("data-href"));
			this.details = dettagliAvvisoHTML.getElementsByClass("gs-text-normal").get(0).text();
			this.creator = dettagliAvvisoHTML.getElementsByClass("text-right gs-text-normal").get(0).text();
			this.isDetailed = true;
			return this.details;
		}

		public boolean isRead(){
			return this.status.equals("LETTA");
		}

		public String toString(){
			if(!this.isDetailed)
				return this.status + "; " + this.date + "; " + this.receivers + "; " + this.objectAvviso;
			else
				return this.status + "; " + this.date + "; " + this.receivers + "; " + this.objectAvviso + "; " + this.creator + "; " + this.details;
		}
	}

	// Insert user and password of the account
	private static String user = "***REMOVED***";
	private static String password = "***REMOVED***";

	private static Map<String, String> PHPSESSID = null;
	private static String CSRFToken = null;


	public static Document getPage(String url) {
		try {
			
			if(checkLogin() == false) {
				print("getPage: Not logged in");
				print("getPage: Calling login method");
				login(user, password);
			}

			print("getPage: Getting page...");

			Connection.Response res = Jsoup.connect(url)
					.method(Method.GET)
					.cookies(PHPSESSID)
					.execute();

			Document doc = res.parse();

			print("getPage: Done!");
			return doc;


		} catch (IOException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	//Ritorna una lista di Avvisi con tutti i loro componenti
	public static List<Avviso> getAllAvvisi() {
		List<Avviso> allAvvisi = new Vector<Avviso>();
		Document doc = getPage("https://registro.giua.edu.it/genitori/avvisi");
		Elements allAvvisiLettiStatusHTML = doc.getElementsByClass("label label-default");
		Elements allAvvisiDaLeggereStatusHTML = doc.getElementsByClass("label label-warning");

		int i = 0;
		for (Element el : allAvvisiLettiStatusHTML) {
			allAvvisi.add(new Avviso(el.text(),
					el.parent().parent().child(1).text(),
					el.parent().parent().child(2).text(),
					el.parent().parent().child(3).text(),
					i
					));
			i++;
		}
		for (Element el : allAvvisiDaLeggereStatusHTML) {
			allAvvisi.add(new Avviso(el.text(),
					el.parent().parent().child(1).text(),
					el.parent().parent().child(2).text(),
					el.parent().parent().child(3).text(),
					i
			));
			i++;
		}

		return allAvvisi;
	}

	//Ritorna una mappa fatta in questo modo: {"italiano": [tutti voti italiano], ...}
	public static Map<String, List<String>> getAllVotes() {
		//TODO: Aggiungere il supporto per gli asterischi
		//TODO: Distinguere voti del primo quadrimestre e del secondo

		Map<String, List<String>> returnVotes = new HashMap<>();
		Document doc = getPage("https://registro.giua.edu.it/genitori/voti");
		Elements votesHTML = doc.getElementsByAttributeValue("title", "Informazioni sulla valutazione");

		int totalVotes = votesHTML.size();
		for (Element voteHTML : votesHTML) {
			final String voteAsString = voteHTML.text(); //prende il voto
			if (voteAsString.length() > 0) {    //Gli asterischi sono caratteri vuoti
				String materiaName = voteHTML.parent().parent().child(0).text(); //prende il nome della materia
				if (returnVotes.containsKey(materiaName)) {
					List<String> tempList = returnVotes.get(materiaName); //uso questa variabile come appoggio per poter modificare la lista di voti di quella materia
					tempList.add(voteAsString);
				} else {
					returnVotes.put(materiaName, new Vector<String>() {{
						add(voteAsString);    //il voto lo aggiungo direttamente
					}});
				}
			}
		}

		return returnVotes;
	}

	public static Boolean checkLogin() {
		try {

			if(PHPSESSID == null) {
				return false; //Not logged in
			}

			Connection.Response res = Jsoup.connect("https://registro.giua.edu.it/login/form/")
				    .method(Method.POST)
				    .cookies(PHPSESSID)
				    .execute();

			Document doc = res.parse();

			Elements logout_button = doc.getElementsByAttributeValue("title", "Esci dal Registro Elettronico");

			//The login screen is the most simple to scrape information from, in this case we search
			//for the log out button, if it doesn't exist we are not logged in.
			// Since we have already loaded the login page, this process is very fast
			//logged out
			return logout_button.size() > 0; //ritorna true se sei loggato altrimenti false


		} catch (IOException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


	//The most important function, it handles the login process
	public static void login(String username, String password)
	{
		try {

			Connection.Response res = Jsoup.connect("https://registro.giua.edu.it/login/form")
            	    .method(Method.GET)
            	    .execute();

            	Document doc = res.parse();
            	PHPSESSID = res.cookies();

			print("login: First connection (login form)");
			System.out.printf("login: Title: %s\n", doc.title());
			System.out.printf("login: Cookie: %s\n", PHPSESSID);


			print("login: Second connection (authenticate)");
			Connection.Response res2 = Jsoup.connect("https://registro.giua.edu.it/ajax/token/authenticate")
					.cookies(PHPSESSID)
					.method(Method.GET)
					.ignoreContentType(true)
					.execute();

			print("login: get csrf token");

			CSRFToken = res2.body().split("\":\"", 0)[1].replaceAll("\"}", "");		//prende solo il valore del csrf

			//print("Page content: " + res2.body());
			print("login: CSRF Token: " + CSRFToken);

			print("login: Third connection (login form)");
			Connection.Response res3 = Jsoup.connect("https://registro.giua.edu.it/login/form/")
					.data("_username", username, "_password", password, "_csrf_token", CSRFToken, "login", "")
					.cookies(PHPSESSID)
					.method(Method.POST)
					.execute();

			PHPSESSID = res3.cookies();
			System.out.printf("login: Cookie: %s\n", PHPSESSID);

			//Document doc2 = res3.parse();
			//print("HTML: " + doc2);


		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void print(String string) {
		System.out.println(string);
		//WHY THE FUCK IS THE PRINTING FUCTION SO LONG???
	}



	//Main function, only used on the console version for testing
	public static void main(String[] args) {

		Scanner sc= new Scanner(System.in);
		if(user=="" && password==""){
			print("Please enter username: ");
			user= sc.nextLine();
			print("Password: ");
			password= sc.nextLine();
		}

		print("----FIRST LOGIN----\n");
		login(user, password);

		print("------ ARE WE LOGGED IN? ----- ");
		print(checkLogin().toString());

		print("----GET PAGE----\n");
		getPage("https://registro.giua.edu.it/genitori/voti");

		print("------ ARE WE LOGGED IN? ----- ");
		print(checkLogin().toString());


		Document page = getPage("https://registro.giua.edu.it/genitori/voti");

		print("Website title: " + page.title());

		print("--------VOTI--------");

		print("Get votes");
		Map<String, List<String>> votes = getAllVotes();
		for(Map.Entry m:votes.entrySet()){
			print(m.getKey()+" "+m.getValue());
		}

		print("--------AVVISI---------");

		print("Get avvisi");
		List<Avviso> allAvvisi = getAllAvvisi();
		for(Avviso a: allAvvisi){
			print(a.toString());
		}

		print("---------------------");

		/*
		Elements table_div = page.getElementsByTag("tbody"); //Table
		//print("-------HTML TABLE------\n\n" + table_div.toString() + "\n-----\n");
		Elements riga_div = table_div.first().getElementsByTag("tr"); //sub-table


		for (Element container : riga_div) {
			String materia = container.getElementsByTag("strong").first().text(); //Get the subject

			Elements voti_div = container.getElementsByTag("button"); //Get number from button
			Elements info_voti_div = container.getElementsByTag("div"); //Get information from the popup
			String voti_materia = null;
			//Integer i = 0;
			String info = null;

			print("\n" + materia);
			for (Element container_v : voti_div) {
				voti_materia = container_v.text();

				for (Element container_iv : info_voti_div) {
					info = container_iv.text();
				}

				print("Voto: " + voti_materia + "\n" + info);
			}



    		/*
    		for(String[] voto : voti_materia[]) {
    			print(materia + " - voto: " + voto + "\n" + info);
    		}*

		}*/
	}
}