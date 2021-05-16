package com.giua.webscraper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

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

public class App {

	public static class Avviso {    //Da usare come una struttura di C#
		public final String stato;
		public final String data;
		public final String destinatari;
		public final String oggetto;
		public String dettagli;
		public String creatore;
		public final int id;		//Indica quanto e' lontano dal primo avviso partendo dall'alto

		Avviso(String stato, String data, String destinatari, String oggetto, int id) {    //Costruttore (suona malissimo in italiano)
			this.stato = stato;
			this.data = data;
			this.destinatari = destinatari;
			this.oggetto = oggetto;
			this.id = id;
		}

		public String getDetails(){		//carica i dettagli e l'autore dell'avviso simulando il click su Visualizza
			Document allAvvisiHTML = getPage("https://registro.giua.edu.it/genitori/avvisi");
			Document dettagliAvvisoHTML = getPage("https://registro.giua.edu.it" + allAvvisiHTML.getElementsByClass("label label-default").get(this.id).parent().parent().child(4).child(0).attributes().get("data-href"));
			this.dettagli = dettagliAvvisoHTML.getElementsByClass("gs-text-normal").get(0).text();
			this.creatore = dettagliAvvisoHTML.getElementsByClass("text-right gs-text-normal").get(0).text();
			return this.dettagli;
		}
	}

	// Insert user and password of the account
	private static String user = "";
	private static String password = "";

	private static Map<String, String> PHPSESSID = null;
	private static String CSRFToken = null;


	public static Document getPage(String url) {
		try {

			if (!checkLogin()) {
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
		Elements allAvvisiStatusHTML = doc.getElementsByClass("label label-default");

		int i = 0;
		for (Element el : allAvvisiStatusHTML) {
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
			String voteAsString = voteHTML.text(); //prende il voto
			if (voteAsString.length() > 0) {    //Gli asterischi sono caratteri vuoti
				String subject = voteHTML.parent().parent().child(0).text(); //prende il nome della materia
				if (returnVotes.containsKey(subject)) {
					List<String> tempList = returnVotes.get(subject); //uso questa variabile come appoggio per poter modificare la lista di voti di quella materia
					tempList.add(voteAsString);
				} else {
					returnVotes.put(subject, new Vector<String>() {{
						add(voteAsString);    //il voto lo aggiungo direttamente
					}});
				}
			}
		}

		return returnVotes;
	}

	public static Boolean checkLogin() {
		try {

			if (PHPSESSID == null) {
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
	public static void login(String username, String password) {
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


		/*Elements table_div = page.getElementsByTag("tbody"); //Table
		//print("-------HTML TABLE------\n\n" + table_div.toString() + "\n-----\n");		Dovremmo togliere questa parte perche da errore quando checkLogin() da false
		Elements riga_div = table_div.first().getElementsByTag("tr"); //sub-table*/
	}
}