package com.giua.webscraper;

import com.giua.objects.*;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/* -- Giua Webscraper alpha 0.8.1 -- */
// Tested with version 1.2.x and 1.3.0 of giua@school
public class GiuaScraper extends GiuaScraperExceptions implements Serializable {

	//region Variabili globali
	private final String user;
	private final String password;
	private String userType = "";
	private String PHPSESSID = null;

	//region Cache
	private Map<String, List<Vote>> allVotesCache = null;
	private List<Newsletter> allNewslettersCache = null;
	private List<Alert> allAlertsCache = null;
	private List<Test> allTestsCache = null;
	private List<Homework> allHomeworksCache = null;
	private List<Lesson> allLessonsCache = null;
	private ReportCard reportCardCache = null;
	//endregion

	final public boolean cacheable;        //Indica se si possono utilizzare le cache

	public static String SiteURL = "https://registro.giua.edu.it";    //URL del registro
	private static boolean debugMode;

	//endregion

	//region Metodi getter e setter

	/**
	 * Permette di settare l'URL del registro
	 *
	 * @param newurl formattato come "https://example.com"
	 */
	public void setSiteURL(String newurl) {
		SiteURL = newurl;
	}

	/**
	 * Permette di ottenre l'URL del registro
	 *
	 * @return l'URL del registro formattato come "https://example.com"
	 */
	public String getSiteURL() {
		return SiteURL;
	}

	public String getSessionCookie() {
		return PHPSESSID;
	}

	public String getUser() {
		return user;
	}

	//endregion

	//region Costruttori della classe

	/**
	 * Costruttore della classe {@link GiuaScraper} che permette lo scraping della pagina del Giua
	 *
	 * @param user
	 * @param password
	 */
	public GiuaScraper(String user, String password) {
		this.user = user;
		this.password = password;
		this.cacheable = true;
		login();
	}

	public GiuaScraper(String user, String password, boolean cacheable){
		this.user = user;
		this.password = password;
		this.cacheable = cacheable;
		login();
	}

	/**
	 * Puoi usare questo per fare il login diretto con il phpsessid. Nel caso sia invalido, il login verrà
	 * effettuato con le credenziali
	 * @param user
	 * @param password
	 * @param phpsessid
	 * @param cacheable
	 */
	public GiuaScraper(String user, String password, String phpsessid, boolean cacheable) {
		this.user = user;
		this.password = password;
		this.cacheable = cacheable;
		this.PHPSESSID = phpsessid;
		login();
	}

	//endregion

	//region Funzioni per ottenere dati dal registro

	/**
	 * Ti da una {@code ReportCard} del quadrimestre indicato
	 *
	 * @param firstQuarterly
	 * @param forceRefresh
	 * @return La pagella del quadrimestre indicato
	 */
	public ReportCard getReportCard(boolean firstQuarterly, boolean forceRefresh) {
		if (reportCardCache == null || forceRefresh){
			ReportCard returnReportCard;
			Map<String, List<String>> returnReportCardValue = new HashMap<>();
			Document doc;
			if(firstQuarterly)
				doc = getPage("genitori/pagelle/P");
			else
				doc = getPage("genitori/pagelle/F");

			Elements elements = doc.getElementsByTag("tr");
			elements.remove(0);

			for(Element e: elements){
				String subject = e.child(0).text();
				String vote = e.child(0).text();
				String absentTime = e.child(0).text();
				List<String> pairValue = new Vector<>();
				pairValue.add(vote);
				pairValue.add(absentTime);

				returnReportCardValue.put(subject, pairValue);
			}

			returnReportCard = new ReportCard(firstQuarterly, returnReportCardValue);

			if(cacheable)
				reportCardCache = returnReportCard;

			return returnReportCard;
		} else
			return reportCardCache;

	}

	/**
	 * Ritorna una lista di {@code Alert} senza {@code details} e {@code creator}.
	 * Per generare i dettagli {@link Alert#getDetails(GiuaScraper)}
	 * @param page La pagina da cui prendere gli avvisi
	 * @param forceRefresh Ricarica effettivamente tutti i voti
	 * @return Lista di Alert
	 */
	public List<Alert> getAllAlerts(int page, boolean forceRefresh) {
		if(allAlertsCache == null || forceRefresh) {
			if (page < 0) {
				throw new IndexOutOfBoundsException("Un indice di pagina non puo essere 0 o negativo");
			}
			List<Alert> allAvvisi = new Vector<>();
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

			if(cacheable) {
				allAlertsCache = allAvvisi;
			}
			return allAvvisi;
		} else {
			return allAlertsCache;
		}
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
	 * @param forceRefresh Ricarica effettivamente tutti i voti
	 * @return Lista di NewsLetter contenente tutte le circolari della pagina specificata
	 */
	public List<Newsletter> getAllNewsletters(int page, boolean forceRefresh) {
		if(allNewslettersCache == null || forceRefresh) {
			List<Newsletter> allNewsletters = new Vector<>();
			Document doc = getPage("circolari/genitori/" + page);
			Elements allNewslettersLettiStatusHTML = doc.getElementsByClass("label label-default");
			Elements allNewslettersDaLeggereStatusHTML = doc.getElementsByClass("label label-warning");

			int i = 0;
			for (Element el : allNewslettersLettiStatusHTML) {
				allNewsletters.add(new Newsletter(el.text(),
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
				allNewsletters.add(new Newsletter(el.text(),
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

			if(cacheable) {
				allNewslettersCache = allNewsletters;
			}
			return allNewsletters;
		} else {
			return allNewslettersCache;
		}
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
	 * @param date puo essere {@code null}. Formato: anno-mese
	 * @param forceRefresh Ricarica effettivamente tutti i voti
	 * @return Lista di Homework del mese specificato oppure del mese attuale
	 */
	public List<Homework> getAllHomeworks(String date, boolean forceRefresh){
		if(allHomeworksCache == null || forceRefresh) {
			List<Homework> allHomeworks = new Vector<>();
			Document doc = (date == null) ? getPage("genitori/eventi") : getPage("genitori/eventi/" + date); //Se date e' null getPage del mese attuale
			Elements homeworksHTML = doc.getElementsByClass("btn btn-xs btn-default gs-button-remote");
			for (Element homeworkHTML : homeworksHTML) {
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
			if(cacheable) {
				allHomeworksCache = allHomeworks;
			}
			return allHomeworks;
		} else {
			return allHomeworksCache;
		}
	}

	/**
	 * Ottiene tutti i {@link Homework} del mese specificato se {@code date} e' {@code null} altrimenti del mese attuale senza dettagli
	 * @param date puo essere {@code null}. Formato: anno-mese
	 * @param forceRefresh Ricarica effettivamente tutti i voti
	 * @return Lista di Homework del mese specificato oppure del mese attuale
	 */
	public List<Homework> getAllHomeworksWithoutDetails(String date, boolean forceRefresh) {
		if (allHomeworksCache == null || forceRefresh) {
			List<Homework> allHomeworks = new Vector<>();
			Document doc = (date == null) ? getPage("genitori/eventi") : getPage("genitori/eventi/" + date); //Se date e' null getPage del mese attuale
			Elements homeworksHTML = doc.getElementsByClass("btn btn-xs btn-default gs-button-remote");
			for (Element homeworkHTML : homeworksHTML) {
				allHomeworks.add(new Homework(
						homeworkHTML.parent().parent().text(),
						homeworkHTML.attributes().get("data-href").split("/")[4],
						"",
						"",
						"",
						true
				));
			}

			if(cacheable) {
				allHomeworksCache = allHomeworks;
			}
			return allHomeworks;
		} else {
			return allHomeworksCache;
		}
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
	 * @param forceRefresh Ricarica effettivamente tutti i voti
	 * @return Lista dei Test della data specificata o del mese attuale
	 */
	public List<Test> getAllTestsWithoutDetails(String date, boolean forceRefresh){
		if(allTestsCache == null || forceRefresh) {
			List<Test> allTests = new Vector<>();
			Document doc = (date == null) ? getPage("genitori/eventi") : getPage("genitori/eventi/" + date); //Se date e' null getPage del mese attuale
			Elements testsHTML = doc.getElementsByClass("btn btn-xs btn-primary gs-button-remote");
			for (Element testHTML : testsHTML) {

				allTests.add(new Test(
						testHTML.parent().parent().text(),
						testHTML.attributes().get("data-href").split("/")[4],
						"",
						"",
						"",
						true
				));
			}

			if(cacheable) {
				allTestsCache = allTests;
			}
			return allTests;
		} else {
			return allTestsCache;
		}
	}

	/**
	 * Se ci sono molti elementi e quindi link potrebbe dare connection timed out.
	 * Meglio utilizzare prima {@link #getAllTestsWithoutDetails(String, boolean)} e poi andare a prendere la verifica singolarmente con {@link #getTest(String)}
	 * @param date puo essere {@code null}
	 * @param forceRefresh Ricarica effettivamente tutti i voti
	 * @return Lista di Test con tutti i dettagli
	 */
	public List<Test> getAllTests(String date, boolean forceRefresh){
		if(allTestsCache == null || forceRefresh) {
			List<Test> allTests = new Vector<>();
			Document doc = (date == null) ? getPage("genitori/eventi") : getPage("genitori/eventi/" + date); //Se date e' null getPage del mese attuale
			Elements testsHTML = doc.getElementsByClass("btn btn-xs btn-primary gs-button-remote");
			for (Element testHTML : testsHTML) {
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
			if(cacheable) {
				allTestsCache = allTests;
			}
			return allTests;
		} else {
			return allTestsCache;
		}
	}

	/**
	 * Deve essere usata solo da {@link #getAllVotes(boolean)} e serve a gestire quei voti che non hanno alcuni dettagli
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
	 * @param forceRefresh Ricarica effettivamente tutti i voti
	 * @return {@code Map<String, List<Vote>>}. Esempio di come e' fatta: {"Italiano": [9,3,1,4,2], ...}
	 */
	public Map<String, List<Vote>> getAllVotes(boolean forceRefresh) {
		if(allVotesCache == null || forceRefresh) {
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
					if (returnVotes.containsKey(materiaName)) {            //Se la materia esiste gia aggiungo solamente il voto
						List<Vote> tempList = returnVotes.get(materiaName); //uso questa variabile come appoggio per poter modificare la lista di voti di quella materia
						tempList.add(new Vote(voteAsString, voteDate, type, args, judg, isFirstQuart, false));
					} else {
						returnVotes.put(materiaName, new Vector<Vote>() {{
							add(new Vote(voteAsString, voteDate, type, args, judg, isFirstQuart, false));    //il voto lo aggiungo direttamente
						}});
					}
				} else {        //e' un asterisco
					if (returnVotes.containsKey(materiaName)) {
						returnVotes.get(materiaName).add(new Vote("", voteDate, type, args, judg, isFirstQuart, true));
					} else {
						returnVotes.put(materiaName, new Vector<Vote>() {{
							add(new Vote("", voteDate, type, args, judg, isFirstQuart, true));
						}});
					}
				}
			}

			if(cacheable) {
				allVotesCache = returnVotes;
			}
			return returnVotes;
		} else {
			return allVotesCache;
		}
	}

	/**
	 * Ottiene tutte le lezioni di un dato giorno
	 * @param date Formato: anno-mese-giorno
	 * @param forceRefresh Ricarica effettivamente tutte le lezioni
	 * @return Una List delle {@link Lesson} di un dato giorno
	 */
	public List<Lesson> getAllLessons(String date, boolean forceRefresh){
		if(allLessonsCache == null || forceRefresh) {
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

			if (cacheable) {
				allLessonsCache = returnLesson;
			}
			return returnLesson;
		} else {
			return allLessonsCache;
		}
	}

	//endregion

	//region Funzioni fondamentali

	/**
	 * Ottiene la pagina HTML specificata dalla variabile {@code SiteURL}
	 * Non c'e' bisogno di inserire {@code /} prima di un URL
	 *
	 * @param page
	 * @return Una pagina HTML come {@link Document}
	 */
	public Document getPage(String page) {
		try {

			if (!checkLogin() && !page.equals("") && !page.equals("login/form")) {
				throw new NotLoggedIn("Please login before requesting this page");
			}

			if (PHPSESSID == null) {
				PHPSESSID = "";
			} //Per risolvere errore strano che capita quando è null

			log("getPage: Getting page " + SiteURL + "/" + page);

			Connection.Response res = Jsoup.connect(SiteURL + "/" + page)
					.method(Method.GET)
					.cookie("PHPSESSID", PHPSESSID)
					.execute();

			Document doc = res.parse();

			logln("\t Done!");
			return doc;


		} catch (Exception e) {
			if(!isSiteWorking()){
				throw new SiteConnectionProblems("Can't get page because the website is down, retry later");
			}
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Ottiene la pagina HTML specificata da un URL esterna al sito del Giua
	 * @param url
	 * @return Una pagina HTML come {@link Document}
	 */
	public Document getExtPage(String url) {
		try {
			log("getExtPage: Getting external page " + url);

			Connection.Response res = Jsoup.connect(url)
					.method(Method.GET)
					.execute();

			Document doc = res.parse();

			logln("\t Done!");
			return doc;

		} catch (IOException e) {
			if(!isMyInternetWorking()){
				throw new InternetProblems("Your internet may not work properly");
			}
		}
		return null;
	}

	/**
	 * Controlla se si è loggati dentro il registro
	 * @return true se e' loggato altrimenti false
	 */
	public Boolean checkLogin() {
		try {
			if (PHPSESSID == null) {        //Non e loggato
				return false;
			}

			Connection.Response res = Jsoup.connect(SiteURL)
					.method(Method.GET)
					.cookie("PHPSESSID", PHPSESSID)
					.execute();

			//Il registro risponde alla richiesta GET all'URL https://registro.giua.edu.it
			//con uno statusCode pari a 302 se non sei loggato altrimenti risponde con 200
			//Attenzione: Il sito ritorna 200 anche quando il PHPSESSID non è valido!
			logln("Calling checklogin() the site answered with status code: " + res.statusCode());
			return res.statusCode() != 302;

		} catch (IOException e) {
			if(!isSiteWorking()){
				throw new SiteConnectionProblems("Can't check login because the site is down, retry later");
			}
			e.printStackTrace();
			return null;
		}
	}

	public boolean isCookieValid(String phpsessid){
		PHPSESSID = phpsessid;

		//Funzione indipendente. Non usa ne getPage ne altro

		try {
			Connection.Response res = Jsoup.connect(SiteURL)
					.method(Method.GET)
					.cookie("PHPSESSID", PHPSESSID)
					.execute();

			// --- Ottieni tipo account
			final Document doc = res.parse();
			final Elements elm = doc.getElementsByClass("col-sm-5 col-xs-8 text-right");
			userType = elm.text().split(".+\\(|\\)")[1];

		} catch (Exception e) {
			if(!isSiteWorking()){
				throw new SiteConnectionProblems("Can't connect to website while checking the cookie. Please retry later");
			}

			//Se c'è stato un errore di qualunque tipo, allora non siamo riusciti ad ottenere il tipo
			// e quindi, il cookie non è valido
			return false;
		}

		//return checkLogin();
		return true;
	}

	/**
	 * La funzione per loggarsi effetivamente. Genera un phpsessid e un csrftoken per potersi loggare.
	 */
	public void login() {
		try {

			if (isCookieValid(PHPSESSID)) {
				//Il cookie esistente è ancora valido, niente login.
				logln("login: Session still valid, ignoring");
			} else {
				logln("login: Session invalid, logging in...");

				//logln("login: First connection (login form)");

				Connection.Response res = Jsoup.connect(SiteURL + "/login/form")
						.method(Method.GET)
						.execute();

				//Document doc = res.parse();
				PHPSESSID = res.cookie("PHPSESSID");


				//logln("login: Second connection (authenticate)");
				Connection.Response res2 = Jsoup.connect(SiteURL + "/ajax/token/authenticate")
						.cookie("PHPSESSID", PHPSESSID)
						.method(Method.GET)
						.ignoreContentType(true)
						.execute();

				logln("login: get csrf token");

				String CSRFToken = res2.body().split(".+\":\"|\".")[1];        //prende solo il valore del csrf

				//logln("Page content: " + res2.body());
				logln("login: CSRF Token: " + CSRFToken);

				//logln("login: Third connection (login form)");
				Connection.Response res3 = Jsoup.connect(SiteURL + "/login/form/")
						.data("_username", this.user, "_password", this.password, "_csrf_token", CSRFToken, "login", "")
						.cookie("PHPSESSID", PHPSESSID)
						.method(Method.POST)
						.execute();

				PHPSESSID = res3.cookie("PHPSESSID");
				System.out.printf("login: Cookie: %s\n", PHPSESSID);

				Document doc2 = res3.parse();


				if (PHPSESSID == null) {
					Elements err = doc2.getElementsByClass("alert alert-danger"); //prendi errore dal sito
					throw new SessionCookieEmpty("Session cookie empty, login unsuccessful. Site says: " + err.text());
				}
			}

			logln("login: Logged in as " + this.user + " with account type " + getUserType());


			//logln("HTML: " + doc2);


		} catch (Exception e){
			if(!isSiteWorking()){
				throw new SiteConnectionProblems("Can't log in because the site is down, retry later");
			}
			throw new UnableToLogin("Something unexpected happened", e);
		}
	}

	public static boolean isMyInternetWorking(){
		try{
			Jsoup.connect("https://www.google.it").method(Method.GET).timeout(5000).execute();
			return true;
		} catch (IOException io){
			return false;
		}
	}

	public static boolean isSiteWorking(){
		try {
			Jsoup.connect(SiteURL).method(Method.GET).timeout(5000).execute();	//Se la richiesta impiega più di 5 secondi
			return true;
		} catch (IOException io){
			if(isMyInternetWorking()) {
				return false;
			} else {
				throw new InternetProblems("Your internet may not work properly");
			}
		}
	}

	public String getUserType(){
		try{
			if(userType.equals("")){
				final Document doc = getPage("");
				final Elements elm = doc.getElementsByClass("col-sm-5 col-xs-8 text-right");
				String text = elm.text().split(".+\\(|\\)")[1];
				userType = text;
				return text;
			} else {
				return userType;
			}
		} catch (Exception e) {
			throw new UnableToGetUserType("unable to get user type, are we not logged in?", e);
		}
	}

	public void clearCache() {
		allVotesCache = null;
		allNewslettersCache = null;
		allAlertsCache = null;
		allTestsCache = null;
		allHomeworksCache = null;
		allLessonsCache = null;
		reportCardCache = null;
	}

	//endregion

	//region Funzioni di debug

	/**
	 * Stampa una stringa e va a capo.
	 */
	protected static void logln(Object message) {
		if (debugMode)
			System.out.println(message);
	}

	/**
	 * Stampa una stringa.
	 */
	protected static void log(Object message) {
		if (debugMode)
			System.out.print(message);
	}

	public static void setDebugMode(boolean mode){
		debugMode = mode;
	}

	//endregion
}
