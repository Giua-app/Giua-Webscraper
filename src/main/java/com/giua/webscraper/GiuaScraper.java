package com.giua.webscraper;

import com.giua.objects.*;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/* -- Giua Webscraper alpha 0.6.x -- */
// Tested with version 1.2.x and 1.3.0 of giua@school
public class GiuaScraper extends GiuaScraperExceptions implements Serializable
{
	private String user = "";
	public String getUser(){return user;}

	private String password = "";
	private String userType = "";

	//URL del registro
	public static final String SiteURL = "https://registro.giua.edu.it";

	private String PHPSESSID = null;
	private String CSRFToken = null;

	/**
	 * Costruttore della classe {@link GiuaScraper} che permette lo scraping della pagina del Giua
	 * @param user
	 * @param password
	 */
	public GiuaScraper(String user, String password){
		this.user = user;
		this.password = password;
		login();
	}


	/**
	 * Ritorna una lista di {@code Alert} senza {@code details} e {@code creator}.
	 * Per generare i dettagli {@link Alert#getDetails(GiuaScraper)}
	 * @param page La pagina da cui prendere gli avvisi
	 * @return Lista di Alert
	 * @throws IndexOutOfBoundsException
	 */
	public List<Alert> getAllAlerts(int page) {
		if(page < 0){throw new IndexOutOfBoundsException("Un indice di pagina non puo essere 0 o negativo");}
		List<Alert> allAvvisi = new Vector<Alert>();
		Document doc = getPage("genitori/avvisi/" + page);
		Elements allAvvisiLettiStatusHTML = doc.getElementsByClass("label label-default");
		Elements allAvvisiDaLeggereStatusHTML = doc.getElementsByClass("label label-warning");

		int i = 0;
		for (Element el : allAvvisiLettiStatusHTML) {
			allAvvisi.add(new Alert(el.text(),
					el.parent().parent().child(1).text(),
					el.parent().parent().child(2).text(),
					el.parent().parent().child(3).text(),
					i,
					page
			));
			i++;
		}
		for (Element el : allAvvisiDaLeggereStatusHTML) {
			allAvvisi.add(new Alert(el.text(),
					el.parent().parent().child(1).text(),
					el.parent().parent().child(2).text(),
					el.parent().parent().child(3).text(),
					i,
					page
			));
			i++;
		}

		return allAvvisi;
	}

	/**
	 * Serve solo a {@code #getAllNewsletters} per prendere gli allegati dalle circolari
	 * @param el
	 * @return Lista di Stringa con tutti gli URL degli allegati
	 */
	private List<String> attachmentsUrls(Element el){
		Elements els = el.parent().siblingElements().get(3).child(1).children();
		List<String> r = new Vector<>();
		if(els.size() > 2){     //Ci sono allegati
			Elements allAttachmentsHTML = els.get(1).child(0).children();

			for(Element attachment: allAttachmentsHTML){
				r.add(attachment.child(1).attr("href"));
			}
		} else {        //Non ha allegati
			return null;
		}

		return r;
	}

	/**
	 * Serve ad ottenere tutte le {@link Newsletter} della pagina specificata
	 * @param page
	 * @return Lista di NewsLetter contenente tutte le circolari della pagina specificata
	 */
	public List<Newsletter> getAllNewsletters(int page) {
		List<Newsletter> allCirculars = new Vector<>();
		Document doc = getPage("circolari/genitori/" + page);
		Elements allNewslettersLettiStatusHTML = doc.getElementsByClass("label label-default");
		Elements allNewslettersDaLeggereStatusHTML = doc.getElementsByClass("label label-warning");

		int i = 0;
		for (Element el : allNewslettersLettiStatusHTML) {
			allCirculars.add(new Newsletter(el.text(),
					el.parent().parent().child(1).text(),
					el.parent().parent().child(2).text(),
					el.parent().parent().child(3).text(),
					"" + el.parent().parent().child(4).child(1).child(0).child(0).child(0).getElementsByClass("btn btn-xs btn-primary gs-ml-3").get(0).attr("href"),
					attachmentsUrls(el),
					i,
					page
			));
			i++;
		}
		for (Element el : allNewslettersDaLeggereStatusHTML) {
			allCirculars.add(new Newsletter(el.text(),
					el.parent().parent().child(1).text(),
					el.parent().parent().child(2).text(),
					el.parent().parent().child(3).text(),
					"" + el.parent().parent().child(4).child(1).child(0).child(0).child(0).getElementsByClass("btn btn-xs btn-primary gs-ml-3").get(0).attr("href"),
					attachmentsUrls(el),
					i,
					page
			));
			i++;
		}

		return allCirculars;
	}

	/**
	 * Restituisce il {@link Homework} di una determinata data. Data deve essere cosi: anno-mese-giorno
	 * @param date Formato: anno-mese-giorno
	 * @return Il compito della data specificata se esiste, altrimenti un compito vuoto
	 */
	public Homework getHomework(String date){
		Document doc = getPage("genitori/eventi/dettagli/" + date + "/P");
		try {
			String subject = doc.getElementsByClass("gs-big").get(0).text();
			String creator = doc.getElementsByClass("gs-text-normal").get(1).text().split(": ")[1];
			String details = doc.getElementsByClass("gs-text-normal gs-pt-3 gs-pb-3").get(0).text();

			return new Homework(
					date.split("-")[2],
					date,
					subject,
					creator,
					details,
					true
			);
		} catch (NullPointerException e){		//Non ci sono compiti in questo giorno
			return new Homework(				//Compito vuoto
					date.split("-")[2],
					date,
					"",
					"",
					"No compiti",
					false
			);
		}
	}

	/**
	 * Ottiene tutti i {@link Homework} del mese specificato se {@code date} e' {@code null} altrimenti del mese attuale
	 * @param date puo essere {@code null}
	 * @return Lista di Homework del mese specificato oppure del mese attuale
	 */
	public List<Homework> getAllHomeworks(String date){
		List<Homework> allHomeworks = new Vector<>();
		Document doc = (date == null) ? getPage("genitori/eventi"): getPage("genitori/eventi/" + date); //Se date e' null getPage del mese attuale
		Elements homeworksHTML = doc.getElementsByClass("btn btn-xs btn-default gs-button-remote");
		for(Element homeworkHTML: homeworksHTML){
			Document detailsHTML = getPage("" + homeworkHTML.attributes().get("data-href").substring(1));
			String subject = detailsHTML.getElementsByClass("gs-big").get(0).text();
			String creator = detailsHTML.getElementsByClass("gs-text-normal").get(1).text().split(": ")[1];
			String details = detailsHTML.getElementsByClass("gs-text-normal gs-pt-3 gs-pb-3").get(0).text();

			allHomeworks.add(new Homework(
					homeworkHTML.parent().parent().text(),
					homeworkHTML.attributes().get("data-href").split("/")[4],
					subject,
					creator,
					details,
					true
			));
		}

		return allHomeworks;
	}

	/**
	 * Ottiene il {@link Homework} di una determinata data.
	 * @param date Formato: anno-mese-giorno
	 * @return Test di un determinato giorno
	 */
	public Test getTest(String date){
		Document doc = getPage("genitori/eventi/dettagli/" + date + "/V");
		try {
			String subject = doc.getElementsByClass("gs-text-normal").get(0).text().split(": ")[1];
			String creator = doc.getElementsByClass("gs-text-normal").get(1).text().split(": ")[1];
			String details = doc.getElementsByClass("gs-text-normal gs-pt-3 gs-pb-3").get(0).text();

			return new Test(
					date.split("-")[2],
					date,
					subject,
					creator,
					details,
					true
			);
		} catch (IndexOutOfBoundsException e){		//Non ci sono verifiche in questo giorno
			return new Test(						//Ritorna una verifica che non esiste
					date.split("-")[2],
					date,
					"",
					"",
					"No verifiche",
					false
			);
		}
	}

	/**
	 * Ottiene tutti i {@link Test} di una determinata data senza i dettagli
	 * @param date puo essere {@code null}
	 * @return Lista dei Test della data specificata o del mese attuale
	 */
	public List<Test> getAllTestsWithoutDetails(String date){
		List<Test> allTests = new Vector<>();
		Document doc = (date == null) ? getPage("genitori/eventi"): getPage("genitori/eventi/" + date); //Se date e' null getPage del mese attuale
		Elements testsHTML = doc.getElementsByClass("btn btn-xs btn-primary gs-button-remote");
		for(Element testHTML: testsHTML){

			allTests.add(new Test(
					testHTML.parent().parent().text(),
					testHTML.attributes().get("data-href").split("/")[4],
					"",
					"",
					"",
					true
			));
		}

		return allTests;
	}

	/**
	 * Se ci sono molti elementi e quindi link potrebbe dare connection timed out.
	 * Meglio utilizzare prima {@link #getAllTestsWithoutDetails(String)} e poi andare a prendere la verifica singolarmente con {@link #getTest(String)}
	 * @param date puo essere {@code null}
	 * @return Lista di Test con tutti i dettagli
	 */
	public List<Test> getAllTests(String date){
		List<Test> allTests = new Vector<>();
		Document doc = (date == null) ? getPage("genitori/eventi"): getPage("genitori/eventi/" + date); //Se date e' null getPage del mese attuale
		Elements testsHTML = doc.getElementsByClass("btn btn-xs btn-primary gs-button-remote");
		for(Element testHTML: testsHTML){
			Document detailsHTML = getPage("" + testHTML.attributes().get("data-href").substring(1));
			String subject = detailsHTML.getElementsByClass("gs-text-normal").get(0).text().split(": ")[1];
			String creator = detailsHTML.getElementsByClass("gs-text-normal").get(1).text().split(": ")[1];
			String details = detailsHTML.getElementsByClass("gs-text-normal gs-pt-3 gs-pb-3").get(0).text();

			allTests.add(new Test(
					testHTML.parent().parent().text(),
					testHTML.attributes().get("data-href").split("/")[4],
					subject,
					creator,
					details,
					true
			));
		}

		return allTests;
	}

	/**
	 * Deve essere usata solo da {@link #getAllVotes()} e serve a gestire quei voti che non hanno alcuni dettagli
	 * @param e
	 * @param index
	 * @return Stringa contenente i dettagli di quel voto
	 */
	private String getDetailOfVote(Element e, int index){
		try {
			return e.siblingElements().get(e.elementSiblingIndex()).child(0).child(0).child(index).text().split(": ")[1];
		} catch (Exception err){
			return "";
		}
	}

	/**
	 * Ottiene tutti i {@link Vote}
	 * @return {@code Map<String, List<Vote>>}. Esempio di come e' fatta: {"Italiano": [9,3,1,4,2], ...}
	 */
	public Map<String, List<Vote>> getAllVotes() {

		Map<String, List<Vote>> returnVotes = new HashMap<>();
		Document doc = getPage("genitori/voti");
		Elements votesHTML = doc.getElementsByAttributeValue("title", "Informazioni sulla valutazione");

		for (final Element voteHTML : votesHTML) {
			final String voteAsString = voteHTML.text(); //prende il voto
			final String materiaName = voteHTML.parent().parent().child(0).text(); //prende il nome della materia
			final String voteDate = getDetailOfVote(voteHTML, 0);
			final String type = getDetailOfVote(voteHTML, 1);
			final String args = getDetailOfVote(voteHTML, 2);
			final String judg = getDetailOfVote(voteHTML, 3);
			final boolean isFirstQuart = voteHTML.parent().parent().parent().parent().getElementsByTag("caption").get(0).text().equals("Primo Quadrimestre");

			if (voteAsString.length() > 0) {    //Gli asterischi sono caratteri vuoti
				if (returnVotes.containsKey(materiaName)) {			//Se la materia esiste gia aggiungo solamente il voto
					List<Vote> tempList = returnVotes.get(materiaName); //uso questa variabile come appoggio per poter modificare la lista di voti di quella materia
					tempList.add(new Vote(voteAsString, voteDate, type, args, judg, isFirstQuart, false));
				} else {
					returnVotes.put(materiaName, new Vector<Vote>() {{
						add(new Vote(voteAsString, voteDate, type, args, judg, isFirstQuart, false));    //il voto lo aggiungo direttamente
					}});
				}
			} else {		//e' un asterisco
				if(returnVotes.containsKey(materiaName)){
					returnVotes.get(materiaName).add(new Vote("", voteDate, type, args, judg, isFirstQuart, true));
				} else {
					returnVotes.put(materiaName, new Vector<Vote>() {{
						add(new Vote("", voteDate, type, args, judg, isFirstQuart, true));
					}});
				}
			}
		}

		return returnVotes;
	}

	/**
	 * Ottiene tutte le lezioni di un dato giorno
	 * @param date Formato: anno-mese-giorno
	 * @return Una List delle {@link Lesson} di un dato giorno
	 */
	public List<Lesson> getAllLessons(String date){ //date deve essere tipo: 2021-05-21
		Document doc = getPage("genitori/lezioni/" + date);
		List<Lesson> returnLesson = new Vector<>();

		Elements allLessonsHTML = doc.getElementsByTag("tbody").get(0).children();

		for(Element lessonHTML: allLessonsHTML){
			returnLesson.add(new Lesson(
					date,
					lessonHTML.child(0).text(),
					lessonHTML.child(1).text(),
					lessonHTML.child(2).text(),
					lessonHTML.child(3).text()
			));
		}

		return returnLesson;
	}

	/**
	 * Ottiene la pagina HTML specificata dalla variabile {@code SiteURL}
	 * Non c'e' bisogno di inserire {@code /} prima di un URL
	 * @param page
	 * @return Una pagina HTML come {@link Document}
	 */
	public Document getPage(String page) {
		try {

			if(!checkLogin() && !page.equals("") && !page.equals("login/form")) {
				throw new NotLoggedIn("Please login before requesting this page");
			}

			System.out.println("getPage: Getting page...");

			Connection.Response res = Jsoup.connect(SiteURL + "/" + page)
					.method(Method.GET)
					.cookie("PHPSESSID", PHPSESSID)
					.execute();

			Document doc = res.parse();

			System.out.println("getPage: Done!");
			return doc;


		} catch (IOException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Ottiene la pagina HTML specificata da un URL
	 * @param url
	 * @return Una pagina HTML come {@link Document}
	 */
	public Document getExtPage(String url) {
		try {

			System.out.println("getExtPage: Getting external page...");

			Connection.Response res = Jsoup.connect(url)
					.method(Method.GET)
					//.cookie("PHPSESSID", PHPSESSID)
					.execute();

			Document doc = res.parse();

			System.out.println("getExtPage: Done!");
			return doc;


		} catch (IOException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Controlla se si è loggati dentro il registro
	 * @return true se e' loggato altrimenti false
	 */
	public Boolean checkLogin() {
		try {
			if(PHPSESSID == null) {		//Non e loggato
					return false;
			}

			Connection.Response res = Jsoup.connect(SiteURL)
					.method(Method.GET)
					.cookie("PHPSESSID", PHPSESSID)
					.execute();

			//Il registro risponde alla richiesta GET all'URL https://registro.giua.edu.it
			//con uno statusCode pari a 302 se non sei loggato altrimenti risponde con 200
			return res.statusCode() != 302;

		} catch (IOException e) {
			// Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * La funzione per loggarsi effetivamente. Genera un phpsessid e un csrftoken per potersi loggare.
	 */
	public void login()
	{
		try {

			//System.out.println("login: First connection (login form)");

			Connection.Response res = Jsoup.connect(SiteURL + "/login/form")
            	    .method(Method.GET)
            	    .execute();

            	//Document doc = res.parse();
			PHPSESSID = res.cookie("PHPSESSID");



			//System.out.println("login: Second connection (authenticate)");
			Connection.Response res2 = Jsoup.connect(SiteURL + "/ajax/token/authenticate")
					.cookie("PHPSESSID", PHPSESSID)
					.method(Method.GET)
					.ignoreContentType(true)
					.execute();

			System.out.println("login: get csrf token");

			CSRFToken = res2.body().split(".+\":\"|\".")[1];;		//prende solo il valore del csrf

			//System.out.println("Page content: " + res2.body());
			System.out.println("login: CSRF Token: " + CSRFToken);

			//System.out.println("login: Third connection (login form)");
			Connection.Response res3 = Jsoup.connect(SiteURL + "/login/form/")
					.data("_username", this.user, "_password", this.password, "_csrf_token", this.CSRFToken, "login", "")
					.cookie("PHPSESSID", PHPSESSID)
					.method(Method.POST)
					.execute();

			PHPSESSID = res3.cookie("PHPSESSID");
			System.out.printf("login: Cookie: %s\n", PHPSESSID);

			Document doc2 = res3.parse();


			if(PHPSESSID == null){
				Elements err = doc2.getElementsByClass("alert alert-danger"); //prendi errore dal sito
				throw new SessionCookieEmpty("Session cookie empty, login unsuccessful. Site says: " + err.text());
			}

			System.out.println("login: Logged in as " + this.user + " with account type " + getUserType());


			//System.out.println("HTML: " + doc2);


		} catch (IOException e) {
			throw new UnableToLogin("Unable to login in, is the site down?", e);
		}
	}


	public String getUserType(){
		try{
			if(userType.equals("")){
				final Document doc = getPage(SiteURL);
				final Elements elm = doc.getElementsByClass("col-sm-5 col-xs-8 text-right");
				String text = elm.text().split(".+\\(|\\)")[1];
				userType = text;
				return text;
			} else {
				return  userType;
			}
		} catch (Exception e){
			throw new UnableToGetUserType("unable to get user type, are we not logged in?", e);
		}
	}
}
