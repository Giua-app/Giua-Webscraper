/*
 * Giua Webscraper library
 * A webscraper of the online school workbook giua@school
 * Copyright (C) 2021 - 2021 Hiem, Franck1421 and contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see https://www.gnu.org/licenses/.
 */


package com.giua.webscraper;

import com.giua.objects.*;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/* -- Giua Webscraper ALPHA -- */
// Tested with version 1.2.x and 1.4.0 of giua@school
public class GiuaScraper extends GiuaScraperExceptions {

	//region Variabili globali
	private String user;
	private final String password;
	private String userType = "";
	private static String SiteURL = "https://registro.giua.edu.it";    //URL del registro
	private static boolean debugMode;
	final public boolean cacheable;        //Indica se si possono utilizzare le cache
	private String PHPSESSID = "";
	private Connection session;
	private long lastGetPageTime = 0;

	public enum userTypes{
		STUDENT,
		PARENT,
		TEACHER,
		ADMIN,
		PRINCIPAL,
		ATA
	}


	//region Cache
	private Map<String, List<Vote>> allVotesCache = null;
	private List<Newsletter> allNewslettersCache = null;
	private List<Alert> allAlertsCache = null;
	private List<Test> allTestsCache = null;
	private List<Homework> allHomeworksCache = null;
	private List<Lesson> allLessonsCache = null;
	private ReportCard reportCardCache = null;
	private List<DisciplNotice> allDisciplNoticesCache = null;
	private List<Absence> allAbsencesCache = null;
	private List<News> allNewsFromHomeCache = null;
	private Document getPageCache = null;
	//endregion

	//endregion

	//region Metodi getter e setter

	/**
	 * Permette di settare l'URL del registro
	 *
	 * @param newurl formattato come "https://example.com"
	 */
	public static void setSiteURL(String newurl) {
		GiuaScraper.SiteURL = newurl;
	}

	/**
	 * Permette di ottenere l'URL del registro
	 *
	 * @return l'URL del registro formattato come "https://example.com"
	 */
	public static String getSiteURL() {
		return GiuaScraper.SiteURL;
	}

    /**
     * Permette di ottenere il cookie della sessione "PHPSESSID"
     *
     * @return il cookie "PHPSESSID"
     */
	public String getCookie() {
		//FIXME: quando viene usata nell app è come se la sessione non avesse cookie salvati, comunque funziona bene anche ritornando il PHPSESSID
		//return session.cookieStore().getCookies().get(0).getValue();
		return PHPSESSID;
	}

    /**
     * Permette di ottenere il nome utente
     *
     * @return user
     */
	public String getUser() {
		return user;
	}

	//endregion

	//region Costruttori della classe

	/**
	 * Costruttore della classe {@link GiuaScraper} che permette lo scraping della pagina del Giua
	 *
	 * @param user es. nome.utente.f1
	 * @param password
	 */
	public GiuaScraper(String user, String password) {
		this.user = user;
		this.password = password;
		this.cacheable = true;
		logln("GiuaScraper: started");
		initiateSession();
	}


	/**
	 * Costruttore della classe {@link GiuaScraper} che permette lo scraping della pagina del Giua
	 *
	 * @param user es. nome.utente.f1
	 * @param password
	 * @param cacheable true se deve usare la cache, false altrimenti
	 */
	public GiuaScraper(String user, String password, boolean cacheable){
		this.user = user;
		this.password = password;
		this.cacheable = cacheable;
		logln("GiuaScraper: started");
		initiateSession();
	}

	/**
	 * Puoi usare questo per fare il login diretto con il phpsessid. Nel caso sia invalido, il login verrà
	 * effettuato con le credenziali
	 * @param user es. nome.utente.f1
	 * @param password
	 * @param newCookie il cookie della sessione
	 * @param cacheable true se deve usare la cache, false altrimenti
	 */
	public GiuaScraper(String user, String password, String newCookie, boolean cacheable) {
		this.user = user;
		this.password = password;
		this.cacheable = cacheable;
		logln("GiuaScraper: started");
		PHPSESSID = newCookie;
		initiateSession(newCookie);
	}


	/**
	 * Puoi usare questo per fare il login diretto con il phpsessid. Nel caso sia invalido, il login verrà
	 * effettuato con le credenziali
	 * @param user es. nome.utente.f1
	 * @param password
	 * @param newCookie il cookie della sessione
	 */
	public GiuaScraper(String user, String password, String newCookie) {
		this.user = user;
		this.password = password;
		this.cacheable = true;
		logln("GiuaScraper: started");
		PHPSESSID = newCookie;
		initiateSession(newCookie);
	}

	//endregion

	//region Funzioni per ottenere dati dal registro


	/**
	 * Permette di controllare se ci sono assenze o ritardi da giustificare
	 * dalle news
	 *
	 * @return true se ci sono assenze o ritardi da giustificare, altrimenti false
	 */
	public boolean checkForAbsenceUpdate(){
		List<News> news = getAllNewsFromHome(true);

		for(News nw : news){
			if(nw.newsText.contains("assenze")){
				return true;
			}
		}
		return false;
	}


	/**
	 * Restituisce il numero di circolari da leggere preso dalle notizie
	 * nella home
	 *
	 * Per ottenere il numero di circolari nuove basta memorizzare il risultato
	 * di questa funzione (valore1), poi richiamarla un altra volta (valore2)
	 * e fare la differenza valore2 - valore1.
	 *
	 * @return numero di circolari da leggere
	 */
	public int checkForNewsletterUpdate(){
		List<News> news = getAllNewsFromHome(true);
		String text;
		int num = 0;

		for(News nw : news){
			if(nw.newsText.contains("circolari")){
				text = nw.newsText;
				text = text.split("nuove")[0].split("presenti")[1].charAt(1) + "";

				num = Integer.parseInt(text);
			} else if(nw.newsText.contains("circolare")){
				num = 1;
			}
		}

		return num;
	}

	/**
	 * Restituisce il numero di avvisi da leggere preso dalle notizie
	 * nella home
	 *
	 * Per ottenere il numero di avvisi nuove basta memorizzare il risultato
	 * di questa funzione (valore1), poi richiamarla un altra volta (valore2)
	 * e fare la differenza valore2 - valore1.
	 *
	 * @return numero di avvisi da leggere
	 */
	public int checkForAlertsUpdate(){
		List<News> news = getAllNewsFromHome(true);
		String text;
		int num = 0;

		for(News nw : news){
			if(nw.newsText.contains("avvisi")){
				text = nw.newsText;
				text = text.split("nuovi")[0].split("presenti")[1].charAt(1) + "";

				num = Integer.parseInt(text);
			} else if(nw.newsText.contains("avviso")){
				num = 1;
			}
		}

		return num;
	}


	/**
	 * Controlla se ci sono nuove verifiche presenti
	 *
	 * Attenzione: questa funzione non prende le informazioni
	 * dalle news, ma dalla pagina Agenda
	 *
	 * @param yearmonth Anno-Mese in cui controllare
	 * @return Una lista di Test nuovi
	 */
	public List<Test> checkForTestsUpdate(String yearmonth) {
		List<Test> cache = allTestsCache;
		List<Test> test = getAllTestsWithoutDetails(yearmonth,true);

		return compareTests(cache, test);

	}

	/**
	 * Controlla se ci sono nuove compiti presenti
	 *
	 * Attenzione: questa funzione non prende le informazioni
	 * dalle news, ma dalla pagina Agenda
	 *
	 * @param yearmonth Anno-Mese in cui controllare
	 * @return Una lista di Homework nuovi
	 */
	public List<Homework> checkForHomeworksUpdate(String yearmonth) {
		List<Homework> cache = allHomeworksCache;
		List<Homework> homework = getAllHomeworksWithoutDetails(yearmonth,true);

		return compareHomeworks(cache, homework);

	}

	/**
	 * Fa il confronto tra due homework e restituisce gli homework diversi/nuovi
	 *
     * Attenzione: per evitare di spammare il sito con richieste, questa
     * funzione non prende i dettagli dei homework, quindi non può distinguere
     * tra più homework nello stesso giorno
     *
	 * @param oldHomework Homeworks vecchi con cui controllare
	 * @param newHomework Homeworks nuovi
	 * @return Una lista di homework diversi/nuovi
	 */
	public List<Homework> compareHomeworks(List<Homework> oldHomework, List<Homework> newHomework) {
		List<Homework> homeworkDiff = new Vector<>();

		if(!oldHomework.get(0).month.equals(newHomework.get(0).month) && !oldHomework.get(1).month.equals(newHomework.get(1).month)){
			logln("Il mese dei compiti è diverso!");
		}


		for(int i = 0; i < newHomework.size(); i++){
			try {
				if (!newHomework.get(i).day.equals(oldHomework.get(i).day) && !newHomework.get(i).date.equals(oldHomework.get(i).date)) {
					homeworkDiff.add(newHomework.get(i));
				}
			} catch (ArrayIndexOutOfBoundsException e){
				homeworkDiff.add(newHomework.get(i));
			}
		}

		return homeworkDiff;
	}

    /**
     * Fa il confronto tra due test e restituisce i test diversi/nuovi
     *
     * Attenzione: per evitare di spammare il sito con richieste, questa
     * funzione non prende i dettagli dei test, quindi non può distinguere
     * tra più test nello stesso giorno
     *
     * @param oldTest Test vecchi con cui controllare
     * @param newTest Test nuovi
     * @return Una lista di test diversi/nuovi
     */
	public List<Test> compareTests(List<Test> oldTest, List<Test> newTest) {
		List<Test> testDiff = new Vector<>();

		if(!oldTest.get(0).month.equals(newTest.get(0).month) && !oldTest.get(1).month.equals(newTest.get(1).month)){
			logln("Il mese delle verifiche è diverso!");
		}


		for(int i = 0; i < newTest.size(); i++){
			try {
				if (!newTest.get(i).day.equals(oldTest.get(i).day) && !newTest.get(i).date.equals(oldTest.get(i).date)) {
					testDiff.add(newTest.get(i));
				}
			} catch (ArrayIndexOutOfBoundsException e){
				testDiff.add(newTest.get(i));
			}
		}

		return testDiff;
	}


	//region Absence

	/**
	 * Permette di giustificare una assenza da un account genitore.
	 *
	 * @param ab     l'assenza da giustificare
	 * @param type   il tipo di assenza (numerico, per ora metti solo "1")
	 * @param reason la motivazione dell'assenza
	 */
	public void justifyAbsence(Absence ab, String type, String reason) {
		//TODO: permettere di modificare assenza gia giustificata
		//TODO: sistemare la stringa type
		if (getUserType() != userTypes.PARENT) {
			logErrorLn("justifyAbsence: Tipo account non supportato, impossibile giustificare");
			throw new UnsupportedAccount("Può giustificare solo il genitore!");
		}
		try {
			session.newRequest()
					.url(GiuaScraper.SiteURL + ab.justifyUrl)
					.data("giustifica_assenza[tipo]", type, "giustifica_assenza[motivazione]", reason, "giustifica_assenza[submit]", "")
					.post();
		} catch (Exception e) {
			logErrorLn("Qualcosa è andato storto");
			e.printStackTrace();
		}
	}



	/**
	 * Permette di ottenere tutte le assenze presenti
	 *
	 * @param forceRefresh
	 * @return Una lista di Absence
	 */
	public List<Absence> getAllAbsences(boolean forceRefresh) {
		if(allAbsencesCache == null || forceRefresh) {
			List<Absence> allAbsences = new Vector<>();
			Document doc = getPage("genitori/assenze/");

			Elements allAbsencesTBodyHTML = doc.getElementsByTag("tbody");
			allAbsencesTBodyHTML.remove(0); //Rimuovi tabella "Da giustificare" (oppure quella "Situazione globale")

			//Se non la troviamo vuol dire che prima abbiamo cancellato la tabella "Situazione globale", e quindi la tabella da giustificare non esiste
			try{ allAbsencesTBodyHTML.remove(0); }
			catch (Exception e) {
				logln("getAllAbsences: Tabella 'Da giustificare' non presente. Le assenze sono tutte giustificate");
			}


			for (Element el : allAbsencesTBodyHTML) {


				for (Element el2 : el.children()){
					String urlJ = "niente url";

					Elements button = el2.child(3).getElementsByClass("btn btn-primary btn-xs gs-button-remote");

					if(!button.isEmpty()){
						urlJ = button.first().attr("data-href");
					}

					allAbsences.add(new Absence(el2.child(0).text(), el2.child(1).text(), el2.child(2).text(), button.isEmpty(), urlJ));
				}
			}

			if (cacheable) {
				allAbsencesCache = allAbsences;
			}
			return allAbsences;
		} else {
			return allAbsencesCache;
		}
	}

	//#endregione

	//region News From Home

	/**
	 * Permette di ottenere le news dalla home
	 *
	 * @return Una lista di stringhe contenenti le news
	 */
	public List<News> getAllNewsFromHome(boolean forceRefresh) {
		if (allNewsFromHomeCache == null || forceRefresh) {
			List<News> returnAllNews = new Vector<>();
			Document doc = getPage("");
			Element els = doc.getElementsByClass("panel-body").get(0);
			Elements allNewsHTML = els.children();

			for (Element news : allNewsHTML) {
				String url = news.child(0).child(0).attr("href");
				returnAllNews.add(new News(news.text(), url));
			}

			if (cacheable) {
				allNewsFromHomeCache = returnAllNews;
			}
			return returnAllNews;

		} else {
			return allNewsFromHomeCache;
		}
	}

	//endregion

	//region DisciplNotices

	/**
	 * Permette di ottenere tutte le note presenti
	 *
	 * @param forceRefresh
	 * @return Una lista di DisciplNotice
	 */
	public List<DisciplNotice> getAllDisciplNotices(boolean forceRefresh) {
		if (allDisciplNoticesCache == null || forceRefresh) {
			List<DisciplNotice> allDisciplNotices = new Vector<>();
			Document doc = getPage("genitori/note/");
			Elements allDisciplNoticeTBodyHTML = doc.getElementsByTag("tbody");

			for (Element el : allDisciplNoticeTBodyHTML) {


				for (Element el2 : el.children()){
					//logln(" -------\n" + el2.toString());
					//logln(el2.child(0).text() + " ; " + el2.child(1).text() + " ; " + el2.child(2).text() + " ; " + el2.child(3).text());
					String author1 = el2.child(2).child(1).text();
					String author2;
					try{ author2 = el2.child(3).child(1).text(); } catch (IndexOutOfBoundsException e){
						author2 = ""; }

					el2.child(2).child(1).remove();
					allDisciplNotices.add(new DisciplNotice(el2.child(0).text(),
							el2.child(1).text(),
							el2.child(2).text(),
							el2.child(3).text(),
							author1,
							author2));
				}
			}

			if (cacheable) {
				allDisciplNoticesCache = allDisciplNotices;
			}
			return allDisciplNotices;
		} else {
			return allDisciplNoticesCache;
		}
	}

	//#endregion

	//region ReportCard

	/**
	 * Ti da una {@code ReportCard} del quadrimestre indicato
	 *
	 * @param firstQuarterly
	 * @param forceRefresh
	 * @return La pagella del quadrimestre indicato
	 */
	public ReportCard getReportCard(boolean firstQuarterly, boolean forceRefresh) {
		if (reportCardCache == null || forceRefresh) {
			ReportCard returnReportCard;
			Map<String, List<String>> returnReportCardValue = new HashMap<>();
			Document doc;
			if (firstQuarterly)
				doc = getPage("genitori/pagelle/P");
			else
				doc = getPage("genitori/pagelle/F");

			Elements elements = doc.getElementsByTag("tr");

			if (elements.size() == 0)
				return new ReportCard(firstQuarterly, null, false);

			elements.remove(0);

			for (Element e : elements) {
				String subject = e.child(0).text();
				String absentTime = "";
				String vote = e.child(1).text();
				if (e.children().size() == 3)
					absentTime = e.child(2).text();
				List<String> pairValue = new Vector<>();
				pairValue.add(vote);
				pairValue.add(absentTime);

				returnReportCardValue.put(subject, pairValue);
			}

			returnReportCard = new ReportCard(firstQuarterly, returnReportCardValue, true);

			if (cacheable)
				reportCardCache = returnReportCard;

			return returnReportCard;
		} else
			return reportCardCache;

	}

	//#endregion

	//region Alerts

	/**
	 * Ritorna una lista di {@code Alert} senza {@code details} e {@code creator}.
	 * Per generare i dettagli {@link Alert#getDetails(GiuaScraper)}
	 *
	 * @param page         La pagina da cui prendere gli avvisi. Deve essere maggiore di 0.
	 * @param forceRefresh Ricarica effettivamente tutti i voti
	 * @return Lista di Alert
	 * @throws IndexOutOfBoundsException Se {@code page} è minore o uguale a 0.
	 */
	public List<Alert> getAllAlerts(int page, boolean forceRefresh) throws IndexOutOfBoundsException {
		if (allAlertsCache == null || forceRefresh) {
			if (page < 0) {
				throw new IndexOutOfBoundsException("Un indice di pagina non puo essere 0 o negativo");
			}
			List<Alert> allAlerts = new Vector<>();
			Document doc = getPage("genitori/avvisi/" + page);
			Elements allAlertsHTML = doc.getElementsByTag("tbody").get(0).children();

			for (Element alertHTML : allAlertsHTML) {
				allAlerts.add(new Alert(
						alertHTML.child(0).text(),
						alertHTML.child(1).text(),
						alertHTML.child(2).text(),
						alertHTML.child(3).text(),
						alertHTML.child(4).child(0).attr("data-href"),
						page
				));
			}

			if (cacheable) {
				allAlertsCache = allAlerts;
			}
			return allAlerts;
		} else {
			return allAlertsCache;
		}
	}

	//#endregion

	//region Newsletter

	/**
	 * Serve solo a {@code #getAllNewsletters} per prendere gli allegati dalle circolari
	 *
	 * @param el
	 * @return Lista di Stringa con tutti gli URL degli allegati
	 */
	private List<String> attachmentsUrls(Element el) {
		Elements els = el.child(1).children();
		List<String> r = new Vector<>();
		if (els.size() > 2) {     //Ci sono allegati
			Elements allAttachments = els.get(1).child(0).children();
			for (Element attachment : allAttachments) {
				r.add(attachment.child(1).attr("href"));
			}
		} else {        //Non ha allegati
			return null;
		}

		return r;
	}

	private String getNewsletterFilterToken() {
		return Objects.requireNonNull(getPage("circolari/genitori").getElementById("circolari_genitori__token")).attr("value");
	}

	/**
	 * Serve ad ottenere tutte le {@link Newsletter} della pagina specificata
	 *
	 * @param page         La pagina da cui prendere gli avvisi. Deve essere maggiore di 0.
	 * @param forceRefresh Ricarica effettivamente tutti i voti
	 * @return Lista di NewsLetter contenente tutte le circolari della pagina specificata
	 * @throws IndexOutOfBoundsException Se {@code page} è minore o uguale a 0.
	 */
	public List<Newsletter> getAllNewsletters(int page, boolean forceRefresh) throws IndexOutOfBoundsException {
		if (allNewslettersCache == null || forceRefresh) {
			if (page < 0) {
				throw new IndexOutOfBoundsException("Un indice di pagina non puo essere 0 o negativo");
			}
			List<Newsletter> allNewsletters = new Vector<>();
			try {
				Document doc = getPage("circolari/genitori/" + page);

				Elements allNewslettersStatusHTML = doc.getElementsByClass("table table-bordered table-hover table-striped gs-mb-4").get(0).children().get(1).children();

				for (Element el : allNewslettersStatusHTML) {
					allNewsletters.add(new Newsletter(
							el.child(0).text(),
							Integer.parseInt(el.child(1).text()),
							el.child(2).text(),
							el.child(3).text(),
							el.child(4).child(1).child(0).child(0).child(0).getElementsByClass("btn btn-xs btn-primary gs-ml-3").get(0).attr("href"),
							attachmentsUrls(el.child(4)),
							page));
				}

				if (cacheable) {
					allNewslettersCache = allNewsletters;
				}
				return allNewsletters;
			} catch (IndexOutOfBoundsException | NullPointerException e) {
				return allNewsletters;
			}
		} else {
			return allNewslettersCache;
		}
	}

	/**
	 * Serve ad ottenere tutte le {@link Newsletter} della pagina specificata con i filtri specificati.
	 * Le stringhe possono anche essere lasciate vuote.
	 *
	 * @param onlyNotRead  {@code true} per avere solo le circolari non lette
	 * @param date         Mettere la data del mese nel formato: anno-mese
	 * @param text         Il testo da cercare tra le circolari
	 * @param page         Indica a quale pagina andare. Le pagine partono da 1
	 * @param forceRefresh Ricarica effettivamente tutti i voti
	 * @return Lista di NewsLetter contenente tutte le circolari della pagina specificata
	 */
	public List<Newsletter> getAllNewslettersWithFilter(boolean onlyNotRead, String date, String text, int page, boolean forceRefresh) {
		if (allNewslettersCache == null || forceRefresh) {
			List<Newsletter> allNewsletters = new Vector<>();
			try {

				Document doc = session.newRequest()
						.url(GiuaScraper.SiteURL + "/circolari/genitori/" + page)
						.data("circolari_genitori[visualizza]", onlyNotRead ? "D" : "P")
						.data("circolari_genitori[mese]", date)
						.data("circolari_genitori[oggetto]", text)
						.data("circolari_genitori[submit]", "")
						.data("circolari_genitori[_token]", getNewsletterFilterToken())
						.post();

				Elements allNewslettersStatusHTML = doc.getElementsByClass("table table-bordered table-hover table-striped gs-mb-4").get(0).children().get(1).children();

				for (Element el : allNewslettersStatusHTML) {
					allNewsletters.add(new Newsletter(
							el.child(0).text(),
							Integer.parseInt(el.child(1).text()),
							el.child(2).text(),
							el.child(3).text(),
							el.child(4).child(1).child(0).child(0).child(0).getElementsByClass("btn btn-xs btn-primary gs-ml-3").get(0).attr("href"),
							attachmentsUrls(el.child(4)),
							page));
				}

				if (cacheable) {
					allNewslettersCache = allNewsletters;
				}
				return allNewsletters;

			} catch (NullPointerException | IndexOutOfBoundsException e) {
				return new Vector<>();
			} catch (Exception e) {
				if (!isSiteWorking()) {
					throw new SiteConnectionProblems("Can't get page because the website is down, retry later", e);
				}
				e.printStackTrace();
			}
			return new Vector<>();
		} else {
			return allNewslettersCache;
		}
	}

	//#endregion

	//region Homework

	/**
	 * Restituisce una lista di tutti gli {@link Homework} di una determinata data con anche i loro dettagli
	 *
	 * @param date Formato: anno-mese-giorno
	 * @return Una lista di tutti gli {@link Homework} della data specificata se esiste, altrimenti una lista vuota
	 */
	public List<Homework> getHomework(String date) {
		List<Homework> allHomeworksInThisDate = new Vector<>();
		Document doc = getPage("genitori/eventi/dettagli/" + date + "/P");
		Elements homeworkGroupsHTML = doc.getElementsByClass("alert alert-info gs-mt-0 gs-mb-2 gs-pt-2 gs-pb-2 gs-pr-2 gs-pl-2");
		try {
			for (Element homeworkGroupHTML : homeworkGroupsHTML) {
				String subject = homeworkGroupHTML.child(0).text();
				String creator = homeworkGroupHTML.child(1).text().split(": ")[1];
				String details = homeworkGroupHTML.child(2).text();

				allHomeworksInThisDate.add(new Homework(
						date.split("-")[2],
						date.split("-")[1],
						date.split("-")[0],
						date,
						subject,
						creator,
						details,
						true
				));
			}

			return allHomeworksInThisDate;
		} catch (NullPointerException | IndexOutOfBoundsException e) {        //Non ci sono compiti in questo giorno
			return new Vector<>();
		}
	}

	/**
	 * Ottiene tutti i {@link Homework} del mese specificato se {@code date} e' {@code null} altrimenti quelli del mese attuale ma SENZA dettagli.
	 * Serve solo a capire in quali giorni ci sono compiti.
	 * @param date puo essere {@code null}. Formato: anno-mese
	 * @param forceRefresh Ricarica effettivamente tutti i compiti
	 * @return Lista di Homework del mese specificato oppure del mese attuale
	 */
	public List<Homework> getAllHomeworksWithoutDetails(String date, boolean forceRefresh) {
		if (allHomeworksCache == null || forceRefresh) {
			List<Homework> allHomeworks = new Vector<>();
			Document doc = (date == null) ? getPage("genitori/eventi") : getPage("genitori/eventi/" + date); //Se date e' null getPage del mese attuale
			Elements homeworksHTML = doc.getElementsByClass("btn btn-xs btn-default gs-button-remote");
			for (Element homeworkHTML : homeworksHTML) {
				assert homeworkHTML.parent() != null;
				assert homeworkHTML.parent().parent() != null;
				String[] hrefSplit = homeworkHTML.attributes().get("data-href").split("/");
				String dateFromhref = hrefSplit[4];
				allHomeworks.add(new Homework(
						dateFromhref.split("-")[2],
						dateFromhref.split("-")[1],
						dateFromhref.split("-")[0],
						dateFromhref,
						"",
						"",
						"",
						true
				));
			}

			if (cacheable) {
				allHomeworksCache = allHomeworks;
			}
			return allHomeworks;
		} else {
			return allHomeworksCache;
		}
	}

	//#endregion

	//region Test

	/**
	 * Restituisce una lista di tutti i {@link Test} di una determinata data con anche i loro dettagli
	 *
	 * @param date Formato: anno-mese-giorno
	 * @return Una lista di tutti i {@link Test} della data specificata se esiste, altrimenti una lista vuota
	 */
	public List<Test> getTest(String date) {
		List<Test> allTests = new Vector<>();
		Document doc = getPage("genitori/eventi/dettagli/" + date + "/V");
		Elements testGroupsHTML = doc.getElementsByClass("alert alert-info gs-mt-0 gs-mb-2 gs-pt-2 gs-pb-2 gs-pr-2 gs-pl-2");
		try {
			for (Element testGroupHTML : testGroupsHTML) {
				String subject = testGroupHTML.child(0).text().split(": ")[1];
				String creator = testGroupHTML.child(1).text().split(": ")[1];
				String details = testGroupHTML.child(2).text();

				allTests.add(new Test(
						date.split("-")[2],
						date.split("-")[1],
						date.split("-")[0],
						date,
						subject,
						creator,
						details,
						true
				));
			}

			return allTests;
		} catch (IndexOutOfBoundsException e) {        //Non ci sono verifiche in questo giorno
			return new Vector<>();
		}
	}

	/**
	 * Ottiene tutti i {@link Test} del mese specificato se {@code date} e' {@code null} altrimenti quelli del mese attuale ma SENZA dettagli.
	 * Serve solo a capire in quali giorni ci sono verifiche.
	 * @param date puo essere {@code null}. Formato: anno-mese
	 * @param forceRefresh Ricarica effettivamente tutti le verifiche
	 * @return Lista di {@link Test} del mese specificato oppure del mese attuale
	 */
	public List<Test> getAllTestsWithoutDetails(String date, boolean forceRefresh){
		if(allTestsCache == null || forceRefresh) {
			List<Test> allTests = new Vector<>();
			Document doc = (date == null) ? getPage("genitori/eventi") : getPage("genitori/eventi/" + date); //Se date e' null getPage del mese attuale
			Elements testsHTML = doc.getElementsByClass("btn btn-xs btn-primary gs-button-remote");
			for (Element testHTML : testsHTML) {

				assert testHTML.parent() != null;
				assert testHTML.parent().parent() != null;
				String[] hrefSplit = testHTML.attributes().get("data-href").split("/");
				String dateFromhref = hrefSplit[4];
				allTests.add(new Test(
						dateFromhref.split("-")[2],
						dateFromhref.split("-")[1],
						dateFromhref.split("-")[0],
						dateFromhref,
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

	//#endregion

	//region Vote

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
				assert voteHTML.parent() != null;
				assert voteHTML.parent().parent() != null;
				assert voteHTML.parent().parent().parent() != null;
				assert voteHTML.parent().parent().parent().parent() != null;

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

			if (cacheable) {
				allVotesCache = returnVotes;
			}
			return returnVotes;
		} else {
			return allVotesCache;
		}
	}

	//endregion

	//region Lesson

	/**
	 * Ottiene tutte le lezioni di una determinata materia
	 *
	 * @param subjectName  Il nome della materia che corrisponda con i nomi del sito
	 * @param forceRefresh Ricarica effettivamente tutte le lezioni
	 * @return Una List delle {@link Lesson} di una determinata materia
	 */
	public List<Lesson> getAllLessonsOfSubject(String subjectName, boolean forceRefresh) {
		if (allLessonsCache == null || forceRefresh) {
			Document doc = getPage("genitori/argomenti");
			List<Lesson> returnLesson = new Vector<>();
			boolean foundSubject = false;

			try {
				Elements allSubjectsHTML = doc.getElementsByAttributeValue("aria-labelledby", "gs-dropdown-menu").get(0).children();

				for (Element subjectHTML : allSubjectsHTML) {
					if (subjectHTML.text().equals(subjectName)) {
						doc = getPage(subjectHTML.child(0).attr("href").substring(1));
						foundSubject = true;
						break;
					}
				}

				if (!foundSubject) {
					throw new SubjectNameInvalid("Subject " + subjectName + " not found in genitori/argomenti");
				}

				Elements allLessonsHTML = doc.getElementsByTag("tbody");

				for (int i = 0; i < allLessonsHTML.size(); i++) {
					Elements lessonsHTML = allLessonsHTML.get(i).children();
					for (Element lessonHTML : lessonsHTML) {
						returnLesson.add(new Lesson(
								lessonHTML.child(0).child(0).attr("href").substring(18, 27),
								"",
								subjectName,
								lessonHTML.child(1).text(),
								lessonHTML.child(2).text(),
								true
						));
					}
				}
			} catch (IndexOutOfBoundsException | NullPointerException e) {
				returnLesson.add(new Lesson("", "", subjectName, "", "", false));
			}

			if (cacheable) {
				allLessonsCache = returnLesson;
			}
			return returnLesson;
		} else {
			return allLessonsCache;
		}
	}

	/**
	 * Ottiene tutte le lezioni di un dato giorno
	 *
	 * @param date         Formato: anno-mese-giorno
	 * @param forceRefresh Ricarica effettivamente tutte le lezioni
	 * @return Una List delle {@link Lesson} di un dato giorno
	 */
	public List<Lesson> getAllLessons(String date, boolean forceRefresh) {
		if (allLessonsCache == null || forceRefresh) {
			Document doc = getPage("genitori/lezioni/" + date);
			List<Lesson> returnLesson = new Vector<>();

			try {
				Elements allLessonsHTML = doc.getElementsByTag("tbody").get(0).children();

				for (Element lessonHTML : allLessonsHTML) {
					returnLesson.add(new Lesson(
							date,
							lessonHTML.child(0).text(),
							lessonHTML.child(1).text(),
							lessonHTML.child(2).text(),
							lessonHTML.child(3).text(),
							true
					));
				}
			} catch (IndexOutOfBoundsException | NullPointerException e) {
				returnLesson.add(new Lesson(date, "", "", "", "", false));
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

	//endregion

	//region Funzioni fondamentali




	/*public List<Newsletter> loadDataFromJson(String jsonData){
		//read json file data to String
		//byte[] jsonData = Files.readAllBytes(Paths.get("employee.txt"));

		ObjectMapper objectMapper = new ObjectMapper();

		JsonNode rootNode = null;
		try {
			rootNode = objectMapper.readTree(jsonData);
		} catch (IOException e) {
			logln("loadDataFromJSON: Impossibile leggere json");
			e.printStackTrace();
		}
		JsonNode version = rootNode.path("version");

		return new JsonHelper().parseJsonForNewsletters(rootNode);
	}*/

	/*public String saveDataToJson(){
		logln("saveDataToJSON: Salvo i dati su json");
		StringBuilder json = new StringBuilder("{\"version\":1,"); //Versione del json: 1 (aumentare se si cambia qualcosa)
		Date calendar = Calendar.getInstance().getTime();
		json.append("\"create_date\":\"").append(calendar).append("\",");

		List<Newsletter> newsletters = getAllNewsletters(0, true);
		List<Alert> alerts = getAllAlerts(0, true);
		/*for (Alert a : alerts){
			a.getDetails(GiuaScraper.this);
		}*
		Map<String, List<Vote>> votes = getAllVotes(true);

		//region Newsletters json
		json.append("\"newsletters\":[{")
				.append("\"0\":")
				.append(newsletters.get(0).toJSON());
		for(int i=1;i < newsletters.size();i++){
			json.append(",\"").append(i).append("\":")
					.append(newsletters.get(i).toJSON());
		}
		json.append("}]");
		//endregion

		json.append(",");

		//region Votes json
		json.append("\"votes\":[{");
		for(String str : votes.keySet()){
			//Materia
			json.append("\"").append(str).append("\":[{")
					.append("\"0\":")
					.append(votes.get(str).get(0).toJSON());

			for(int i=1;i < votes.get(str).size();i++){
				//Voto
				json.append(",\"").append(i).append("\":")
						.append(votes.get(str).get(i).toJSON());
			}
			//Fine di una materia
			json.append("}],");
		}
		json.deleteCharAt(json.length()-1); //Cancella la virgola dell'ultima materia

		json.append("}]");
		//endregion

		json.append(",");

		//region Alerts json
		json.append("\"alerts\":[{")
				.append("\"0\":")
				.append(alerts.get(0).toJSON());
		for(int i=1;i < alerts.size();i++){
			json.append(",\"").append(i).append("\":")
					.append(alerts.get(i).toJSON());
		}
		json.append("}]");
		//endregion

		json.append("}");
		logln(json.toString());
		logln("saveDataToJSON: Salvataggio completato");
		return json.toString();
	}*/

	public static String escape(String raw) {
		String escaped = raw;
		escaped = escaped.replace("\\", "\\\\");
		escaped = escaped.replace("\"", "\\\"");
		escaped = escaped.replace("\b", "\\b");
		escaped = escaped.replace("\f", "\\f");
		escaped = escaped.replace("\n", "\\n");
		escaped = escaped.replace("\r", "\\r");
		escaped = escaped.replace("\t", "\\t");
		//escaped = escaped.replace("\"", "\\u0022");
		return escaped;
	}

	private void initiateSession() {
		session = null; //Per sicurezza azzeriamo la variabile
		logln("initSession: creating new session");
		session = Jsoup.newSession();
	}

	private void initiateSession(String cookie) {
		session = null; //Per sicurezza azzeriamo la variabile
		logln("initSession: creating new session from cookie");
		session = Jsoup.newSession().cookie("PHPSESSID", cookie);
	}

	public boolean isMaintenanceScheduled() {
		Document doc = getPage("login/form/");
		Elements els = doc.getElementsByClass("col-sm-12 bg-danger gs-mb-4 text-center");

		if (!els.isEmpty()) {
			logln("isMaintenanceScheduled: Manutenzione programmata trovata");
			return true;
		}
		logln("isMaintenanceScheduled: Manutenzione non trovata");
		return false;
	}

	public boolean isMaintenanceActive() {
		Document doc = getPage("login/form/");
		Elements loginForm = doc.getElementsByAttributeValue("name", "login_form");

		if (loginForm.isEmpty()) {
			logln("isMaintenanceActive: Manutenzione attiva");
			return true;
		}

		logln("isMaintenanceActive: Manutenzione non attiva");
		return false;
	}

	public Maintenance getMaintenanceInfo() {
		Maintenance maintenance;

		if (!isMaintenanceScheduled()) {
			maintenance = new Maintenance(new Date(), new Date(), false, false, false);
			return maintenance;
		}

		Document doc = getPage("login/form/");
		Elements maintenanceElm = doc.getElementsByClass("col-sm-12 bg-danger gs-mb-4 text-center");
		Elements dateEl = maintenanceElm.get(0).getElementsByTag("strong");

		// Togli dalla scritta tutto tranne le date
		String dateTxt = dateEl.get(0).text();
		String[] a = dateTxt.split("ore");
		String start = a[1].replace("del", "").replace("alle", "");
		String end = a[2].replace("del", "");


		logln("getMaintenanceInfo: Non formattate: Inizio " + start + " | Fine " + end);

		//Crea dei format per fare il parsing di quelle stringhe
		SimpleDateFormat format1 = new SimpleDateFormat(" HH:mm  dd/MM/yyyy  ");
		SimpleDateFormat format2 = new SimpleDateFormat(" HH:mm  dd/MM/yyyy");

		Date startDate;
		Date endDate;
		try {
			startDate = format1.parse(start);
			endDate = format2.parse(end);
		} catch (Exception e) {
			//TODO: throw new errore che ci sono date sbagliate oppure improvvisamente non ci sono piu date
			return null;
		}

		logln("getMaintenanceInfo: formattate: Inizio " + startDate.toString() + " | Fine " + endDate.toString());

		Date currentDate = new Date();
		boolean isActive = false;
		boolean shouldBeActive = false;

		if (currentDate.after(startDate) && currentDate.before(endDate)) {
			logln("getMaintenanceInfo: Secondo l'orario la manutenzione dovrebbe essere attiva");
			shouldBeActive = true;
		}

		if (isMaintenanceActive()) {
			isActive = true;
		}

		maintenance = new Maintenance(startDate, endDate, isActive, shouldBeActive, true);

		return maintenance;
	}

	/**
	 * Effettua il download di una risorsa di qualunque tipo dal registro
	 *
	 * @param url
	 * @return Un oggetto {@code DownloadedFile}
	 */
	public DownloadedFile download(String url) {
		try {
			Connection.Response r = session.newRequest()
					.url(GiuaScraper.SiteURL + url)
					.ignoreContentType(true)
					.execute();

			return new DownloadedFile(Objects.requireNonNull(r.header("Content-Disposition")).split("[.]")[1], r.bodyAsBytes());
		} catch (Exception e) {
			if (!isSiteWorking()) {
				throw new SiteConnectionProblems("Can't get page because the website is down, retry later");
			}
			e.printStackTrace();
		}
		return new DownloadedFile("", new byte[0]);
	}

	/**
	 * Ottiene la pagina HTML specificata dalla variabile {@code SiteURL}
	 * Non c'e' bisogno di inserire {@code /} prima di un URL
	 *
	 * @param page
	 * @return Una pagina HTML come {@link Document}
	 */
	public Document getPage(String page) {
		try {
			//Se l'url è uguale a quello della richiesta precendente e l'ultima richiesta è stata fatta meno di 500ms fa allora usa la cache
			if (page.startsWith("/"))
				page = page.substring(1);
			if (getPageCache != null && (GiuaScraper.SiteURL + "/" + page).equals(getPageCache.location()) && System.nanoTime() - lastGetPageTime < 500000000)
				return getPageCache;
			if (page.equals("login/form/")) {
				return getPageNoCookie("login/form/");
			} else {
				if (isMaintenanceActive()) {
					throw new MaintenanceIsActiveException("The website is in maintenance");
				}

				log("getPage: Getting page " + GiuaScraper.SiteURL + "/" + page);

				Connection.Response response = session.newRequest()
						.url(GiuaScraper.SiteURL + "/" + page)
						.method(Method.GET)
						.execute();

				Document doc = response.parse();

				logln("\t Done!");

				if (response.statusCode() == 302)
					throw new NotLoggedIn("Hai richiesto una pagina del registro senza essere loggato!");

				if (doc == null)
					return new Document(GiuaScraper.SiteURL + "/" + page);

				getPageCache = doc;
				lastGetPageTime = System.nanoTime();
				return doc;
			}

		} catch (Exception e) {
			if (!isSiteWorking()) {
				throw new SiteConnectionProblems("Can't get page because the website is down, retry later", e);
			}
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Ottiene una pagina del registro senza usare il cookie quindi non controlla nemmeno se si è loggati
	 *
	 * @param page
	 * @return La pagina che cercata
	 */
	public Document getPageNoCookie(String page) {
		try {

			log("getPageNoCookie: Getting page " + GiuaScraper.SiteURL + "/" + page);

			Document doc = Jsoup.connect(GiuaScraper.SiteURL + "/" + page)
					.get();

			logln("\t Done!");

			if (doc == null)
				return new Document(GiuaScraper.SiteURL + "/" + page);
			return doc;

		} catch (Exception e) {
			if (!isSiteWorking()) {
				throw new SiteConnectionProblems("Can't get page because the website is down, retry later", e);
			}
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Ottiene la pagina HTML specificata da un URL esterna al sito del Giua
	 *
	 * @param url
	 * @return Una pagina HTML come {@link Document}
	 */
	public Document getExtPage(String url) {
		try {
			log("getExtPage: Getting external page " + url);

			Document doc = Jsoup.connect(url)
					.get();

			logln("\t Done!");
			if (doc == null)
				return new Document(url);
			return doc;

		} catch (IOException e) {
			if(!isMyInternetWorking()){
				throw new YourConnectionProblems("Your internet may not work properly", e);
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
			log("checkLogin: the site answered with status code: ");
			Connection.Response res = session.newRequest()
					.url(GiuaScraper.SiteURL)
					.execute();

			//Il registro risponde alla richiesta GET all'URL https://registro.giua.edu.it
			//con uno statusCode pari a 302 se non sei loggato altrimenti risponde con 200
			//Attenzione: Il sito ritorna 200 anche quando il PHPSESSID non è valido!
			//Attenzione 2: Il sito ritorna 200 anche quando è in manutenzione!
			logln("\t" + res.statusCode());
			return res.statusCode() != 302;

		} catch (Exception e) {
			if(!isSiteWorking()){
				throw new SiteConnectionProblems("Can't check login because the site is down, retry later", e);
			}
			e.printStackTrace();
			return null;
		}
	}

	public boolean isSessionValid(String phpsessid){

		//Funzione indipendente. Non usa ne getPage ne altro

		try {
			if (isMaintenanceActive())
				throw new MaintenanceIsActiveException("You can't login while the maintenace is active");

			Document doc = Jsoup.connect(GiuaScraper.SiteURL)
					.cookie("PHPSESSID", phpsessid)
					.get();

			// --- Ottieni tipo account
			final Elements elm = doc.getElementsByClass("col-sm-5 col-xs-8 text-right");
			userType = elm.text().split(".+\\(|\\)")[1];

		} catch (Exception e) {
			if(!isSiteWorking()){
				throw new SiteConnectionProblems("Can't connect to website while checking the cookie. Please retry later", e);
			}

			//Se c'è stato un errore di qualunque tipo, allora non siamo riusciti ad ottenere il tipo
			// e quindi, la sessione non è valida
			return false;
		}
		// Al contrario, se non ci sono errori (quindi siamo dentro la home) allora la sessione è valida
		return true;
	}

	/**
	 * La funzione per loggarsi effetivamente. Genera un phpsessid e un csrftoken per potersi loggare.
	 *
	 * @throws UnableToLogin                Il login è andato male e il sito non ha detto cosa è andato storto
	 * @throws MaintenanceIsActiveException La manutenzione è attiva e non ci si può loggare
	 * @throws SessionCookieEmpty           Il login è andato storto e il sito ha detto cosa è andato storto
	 */
	public void login() throws UnableToLogin, MaintenanceIsActiveException, SessionCookieEmpty {
		try {

			if (isSessionValid(PHPSESSID)) {
				//Il cookie esistente è ancora valido, niente login.
				logln("login: Session still valid, ignoring");
			} else {
				logln("login: Session expired, creating a new one");
				initiateSession();

				//logln("login: First connection (login form)");

				Document firstRequestDoc = session.newRequest()
						.url(GiuaScraper.SiteURL + "/login/form/")
						.get();

				//Reimplementato isMaintenaceActive() per utilizzare la richiesta precedente e fare una richiesta HTTP in meno
				Elements loginForm = firstRequestDoc.getElementsByAttributeValue("name", "login_form");
				if (loginForm.isEmpty())
					throw new MaintenanceIsActiveException("You can't login while the maintenace is active");

				//logln("login: Second connection (authenticate)");

				Document doc = session.newRequest()
						.url(GiuaScraper.SiteURL + "/ajax/token/authenticate")
						.ignoreContentType(true)
						.get();

				//logln("\n\nCSRF HTML: \n" + doc + "\n\n");

				logln("login: get csrf token");

				String CSRFToken = doc.body().toString().split(".+\":\"|\".")[1];        //prende solo il valore del csrf

				logln("login: CSRF Token: " + CSRFToken);

				//logln("login: Third connection (login form)");

				doc = session.newRequest()
						.url(GiuaScraper.SiteURL + "/login/form/")
						.data("_username", this.user, "_password", this.password, "_csrf_token", CSRFToken, "login", "")
						.post();

				Elements err = doc.getElementsByClass("alert alert-danger"); //prendi errore dal sito
				if (!err.isEmpty()) {
					throw new SessionCookieEmpty("Session cookie empty, login unsuccessful. Site says: " + err.text(), err.text());
				} else {
					PHPSESSID = session.cookieStore().getCookies().get(0).getValue();
					if(isSessionValid(PHPSESSID)) {
						logln("login: Cookie: " + PHPSESSID);
						logln("login: Logged in as " + this.user);
					} else {
						throw new UnableToLogin("Login unsuccessful, and the site didn't give an error message. Please check the site from your web browser");
					}
				}
			}



		} catch (IOException e) {
			if (!isSiteWorking()) {
				throw new SiteConnectionProblems("Can't log in because the site is down, retry later", e);
			} else {
				throw new UnableToLogin("Something unexpected happened", e);
			}
		}
	}

	public static boolean isMyInternetWorking(){
		try {
			Jsoup.connect("https://www.google.it").method(Method.GET).execute();
			return true;
		} catch (IOException io){
			return false;
		}
	}

	public static boolean isSiteWorking(){
		try {
			Jsoup.connect(GiuaScraper.SiteURL).method(Method.GET).execute();    //Se la richiesta impiega più di 5 secondi
			return true;
		} catch (IOException io) {
			if (isMyInternetWorking()) {
				return false;
			} else {
				throw new YourConnectionProblems("Your internet may not work properly", io);
			}
		}
	}

	/**
	 * Prende il nome utente dalla pagina. Utilizza una chiamata a getPage
	 * @return Il nome utente.
	 */
	public String loadUserFromDocument() {
		final Document doc = getPage("");
		user = doc.getElementsByClass("col-sm-5 col-xs-8 text-right").get(0).text().split(" [(]")[0];
		return user;
	}

	/**
	 * Prende il nome utente dalla pagina. Utilizza il {@code Document} passato come parametro.
	 * @return Il nome utente.
	 */
	public String loadUserFromDocument(Document doc) {
		user = doc.getElementsByClass("col-sm-5 col-xs-8 text-right").get(0).text().split(" [(]")[0];
		return user;
	}

	public userTypes getUserType() {
		String text;
		try {
			if (userType.equals("")) {
				final Document doc = getPage("");
				final Elements elm = doc.getElementsByClass("col-sm-5 col-xs-8 text-right");
				text = elm.text().split(".+\\(|\\)")[1];
				userType = text;
				//return text;
			} else {
				//return userType;
			}
		} catch (Exception e) {
			throw new UnableToGetUserType("unable to get user type, are we not logged in?", e);
		}

		if(userType.equals("Genitore")){
			return userTypes.PARENT;
		}
		if(userType.equals("Studente")){
			return userTypes.STUDENT;
		}
		if(userType.equals("Dirigente")){
			return userTypes.PRINCIPAL;
		}
		if(userType.equals("Amministratore")){
			return userTypes.ADMIN;
		}

		//non dovrebbe mai accadere
		throw new UnableToGetUserType("unable to parse userType to userTypes enum because it's unknown");
	}

	public void clearCache() {
		allVotesCache = null;
		allNewslettersCache = null;
		allAlertsCache = null;
		allTestsCache = null;
		allHomeworksCache = null;
		allLessonsCache = null;
		reportCardCache = null;
		allDisciplNoticesCache = null;
		allAbsencesCache = null;
		allNewsFromHomeCache = null;
	}

	//endregion

	//region Funzioni di debug

	/**
	 * Stampa una stringa e va a capo.
	 */
	protected static void logln(Object message) {
		if (GiuaScraper.debugMode)
			System.out.println(message);
	}

	/**
	 * Stampa una stringa.
	 */
	protected static void log(Object message) {
		if (GiuaScraper.debugMode)
			System.out.print(message);
	}

	/**
	 * Stampa una stringa come un errore, quindi rosso.
	 */
	public static void logError(Object message) {
		if (GiuaScraper.debugMode)
			System.err.print(message);
	}

	/**
	 * Stampa una stringa come un errore e va a capo.
	 */
	public static void logErrorLn(Object message) {
		if (GiuaScraper.debugMode)
			System.err.println(message);
	}

	public static void setDebugMode(boolean mode) {
		GiuaScraper.debugMode = mode;
	}

	public static boolean getDebugMode() {
		return GiuaScraper.debugMode;
	}

	//endregion
}
