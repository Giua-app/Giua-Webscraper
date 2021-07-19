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
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

/* -- Giua Webscraper ALPHA -- */
// Tested with version 1.2.x and 1.3.3 of giua@school
public class GiuaScraper extends GiuaScraperExceptions implements Serializable {

	//region Variabili globali
	private final String user;
	private final String password;
	private String userType = "";
	private static String SiteURL = "https://registro.giua.edu.it";    //URL del registro
	private static boolean debugMode;
	final public boolean cacheable;        //Indica se si possono utilizzare le cache
	public String PHPSESSID = "";
	private Connection session;

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
		return session.cookieStore().getCookies().get(0).getValue();
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
		logln("GiuaScraper started");
		initiateSession();
	}

	public GiuaScraper(String user, String password, boolean cacheable){
		this.user = user;
		this.password = password;
		this.cacheable = cacheable;
		logln("GiuaScraper started");
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
		logln("GiuaScraper started");
		PHPSESSID = newCookie;
		initiateSessionWithCookie(newCookie);
	}

	public GiuaScraper(String user, String password, String newCookie) {
		this.user = user;
		this.password = password;
		this.cacheable = true;
		logln("GiuaScraper started");
		PHPSESSID = newCookie;
		initiateSessionWithCookie(newCookie);
	}

	//endregion

	//region Funzioni per ottenere dati dal registro


    /**
     * Permette di giustificare una assenza da un account genitore.
     * @param ab l'assenza da giustificare
     * @param type il tipo di assenza (numerico, per ora metti solo "1")
     * @param reason la motivazione dell'assenza
     */
	public void justifyAbsence(Absence ab, String type, String reason) {
		//TODO: permettere di modificare assenza gia giustificata
		/*if(getUserType() != "Genitore"){
			logln("Tipo account non supportato, impossibile giustificare");
			return;
		}*/
		try {
			session.newRequest()
					.url(GiuaScraper.SiteURL + ab.justifyUrl)
					.data("giustifica_assenza[tipo]", type, "giustifica_assenza[motivazione]", reason, "giustifica_assenza[submit]", "")
					.post();
		} catch (Exception e){
			logErrorLn("Qualcosa è andato storto");
			e.printStackTrace();
		}
	}



	/**
	 * Permette di ottenere tutte le assenze presenti
	 *
	 *
	 * @param forceRefresh
	 * @return Una lista di Absence
	 */
	public List<Absence> getAllAbsences(boolean forceRefresh) {
		if(allAbsencesCache == null || forceRefresh) {
			List<Absence> allAbsences = new Vector<>();
			Document doc = getPage("genitori/assenze/");
			//Elements allAbsencesTables = doc.getElementsByClass("table table-bordered table-hover table-striped");
			Elements allAbsencesTBodyHTML = doc.getElementsByTag("tbody");
			allAbsencesTBodyHTML.remove(0); //Rimuovi tabella "Da giustificare" (oppure quella "Situazione globale")

			//Se non la troviamo vuol dire che prima abbiamo cancellato la tabella "Situazione globale", e quindi la tabella da giustificare non eiste
			try{ allAbsencesTBodyHTML.remove(0); }
			catch (Exception e) {
				logln("Tabella 'Da giustificare' non presente");
			}


			for (Element el : allAbsencesTBodyHTML) {


				for (Element el2 : el.children()){
					//logln(" -------\n" + el2.toString() + "\n -------\n");
					String urlJ = "niente url";
					//logln("Data: " + el2.child(0).text() + " Tipo: " + el2.child(1).text() + " Annotazioni: " + el2.child(2).text() + " Giustificazione: " + el2.child(3).text());

					Elements button = el2.child(3).getElementsByClass("btn btn-primary btn-xs gs-button-remote");

					if(!button.isEmpty()){
						urlJ = button.first().attr("data-href");
					}

					//el2.child(2).child(1).remove();
					allAbsences.add(new Absence(el2.child(0).text(), el2.child(1).text(), el2.child(2).text(), button.isEmpty(), urlJ));
				}
			}

			if(cacheable) {
				allAbsencesCache = allAbsences;
			}
			return allAbsences;
		} else {
			return allAbsencesCache;
		}
	}

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
					try{ author2 = el2.child(3).child(1).text(); }
					catch (IndexOutOfBoundsException e){
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

			if(cacheable) {
				allDisciplNoticesCache = allDisciplNotices;
			}
			return allDisciplNotices;
		} else {
			return allDisciplNoticesCache;
		}
	}

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
				String vote = e.child(0).text();
				String absentTime = e.child(0).text();
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

			Elements allNewslettersStatusHTML = doc.getElementsByClass("table table-bordered table-hover table-striped gs-mb-4").get(0).children().get(1).children();

			for (Element el : allNewslettersStatusHTML) {
				allNewsletters.add(new Newsletter(
						el.child(0).text(),
						el.child(1).text(),
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
		} catch (NullPointerException | IndexOutOfBoundsException e) {        //Non ci sono compiti in questo giorno
			return new Homework(                //Compito vuoto
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
			} catch (IndexOutOfBoundsException iobe) {
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

	//region Funzioni fondamentali

	private void initiateSession(){
		session = null; //Per sicurezza azzeriamo la variabile
		logln("initSession: creating new session");
		session = Jsoup.newSession();
	}

	private void initiateSessionWithCookie(String cookie){
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
		logln("getMaintenanceInfo: Manutenzione non trovata");
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


		logln("getMaintenanceInfo: " + start + "|" + end);

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

		logln("getMaintenanceInfo: " + startDate.toString() + " / " + endDate.toString());

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
	 * @return La risorsa scaricata come un array di byte
	 */
	public byte[] download(String url) {
		try {
			Connection.Response r = session.newRequest()
					.url(GiuaScraper.SiteURL + url)
					.ignoreContentType(true)
					.execute();

			return r.bodyAsBytes();
		} catch (IOException e) {
			e.printStackTrace();
			return new byte[0];
		}
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

			if (page.equals("login/form/")) {
				return getPageNoCookie(page);
			} else {
				if (!checkLogin()) {
					throw new NotLoggedIn("Please login before requesting this page");
				} else if (isMaintenanceActive()) {
					throw new MaintenanceIsActiveException("The website is in maintenance");
				}

				log("getPage: Getting page " + GiuaScraper.SiteURL + "/" + page);

				/*Connection.Response res = Jsoup.connect(GiuaScraper.SiteURL + "/" + page)
						.method(Method.GET)
						.cookie("PHPSESSID", PHPSESSID)
						.execute();*/

				Document doc = session.newRequest()
						.url(GiuaScraper.SiteURL + "/" + page)
						.get();

				logln("\t Done!");
				return doc;
			}

		} catch (Exception e) {
			if (!isSiteWorking()) {
				throw new SiteConnectionProblems("Can't get page because the website is down, retry later");
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

			Connection.Response res = Jsoup.connect(GiuaScraper.SiteURL + "/" + page)
					.method(Method.GET)
					.execute();

			Document doc = res.parse();

			logln("\t Done!");
			return doc;

		} catch (Exception e) {
			if (!isSiteWorking()) {
				throw new SiteConnectionProblems("Can't get page because the website is down, retry later");
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
			Connection.Response res = session.newRequest()
					.url(GiuaScraper.SiteURL)
					.execute();

			//Il registro risponde alla richiesta GET all'URL https://registro.giua.edu.it
			//con uno statusCode pari a 302 se non sei loggato altrimenti risponde con 200
			//Attenzione: Il sito ritorna 200 anche quando il PHPSESSID non è valido!
			//Attenzione 2: Il sito ritorna 200 anche quando è in manutenzione!
			logln("Calling checklogin() the site answered with status code: " + res.statusCode());
			return res.statusCode() != 302;

		} catch (Exception e) {
			if(!isSiteWorking()){
				throw new SiteConnectionProblems("Can't check login because the site is down, retry later");
			}
			e.printStackTrace();
			return null;
		}
	}

	public boolean isSessionValid(){

		//Funzione indipendente. Non usa ne getPage ne altro

		try {
			Document doc = session.newRequest()
					.url(GiuaScraper.SiteURL)
					.get();

			// --- Ottieni tipo account
			final Elements elm = doc.getElementsByClass("col-sm-5 col-xs-8 text-right");
			userType = elm.text().split(".+\\(|\\)")[1];

		} catch (Exception e) {
			if(!isSiteWorking()){
				throw new SiteConnectionProblems("Can't connect to website while checking the cookie. Please retry later");
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
	 */
	public void login() {
		try {

			if (isSessionValid()) {
				//Il cookie esistente è ancora valido, niente login.
				logln("login: Session still valid, ignoring");
			} else {
				logln("login: Session expired, creating a new one");
				initiateSession();

				//logln("login: First connection (login form)");

				session.newRequest()
						.url(GiuaScraper.SiteURL + "/login/form/")
						.get();

				//logln("\n\nHTML: \n" + doc + "\n\n");

				//Document doc = res.parse();
				//PHPSESSID = res.cookie("PHPSESSID");


				//logln("login: Second connection (authenticate)");

				Document doc = session.newRequest()
						.url(GiuaScraper.SiteURL + "/ajax/token/authenticate")
						.ignoreContentType(true)
						.get();

				//logln("\n\nHTML: \n" + doc + "\n\n");

				logln("login: get csrf token");

				String CSRFToken = doc.body().toString().split(".+\":\"|\".")[1];        //prende solo il valore del csrf

				//logln("Page content: " + res2.body());
				logln("login: CSRF Token: " + CSRFToken);

				//logln("login: Third connection (login form)");

				Connection.Response res3 = session.newRequest()
						.url(GiuaScraper.SiteURL + "/login/form/")
						.data("_username", this.user, "_password", this.password, "_csrf_token", CSRFToken, "login", "")
						.method(Method.POST)
						.execute();

				PHPSESSID = session.cookieStore().getCookies().get(0).getValue();
				logln("login: Cookie: " + PHPSESSID);

				//Document doc2 = res3.parse();

				if (PHPSESSID == null) {
					Elements err = doc.getElementsByClass("alert alert-danger"); //prendi errore dal sito
					if(err.isEmpty()){
						throw new UnableToLogin("Session cookie empty, login unsuccessful.\n WARNING! Site was unable to give an error message, its possible the login process is deactivated", null);
					}
					throw new SessionCookieEmpty("Session cookie empty, login unsuccessful. Site says: " + err.text());
				}
			}

			logln("login: Logged in as " + this.user + " with account type " + getUserType());


			//logln("HTML: " + doc2);


		} catch (IOException e) {
			if (!isSiteWorking()) {
				throw new SiteConnectionProblems("Can't log in because the site is down, retry later");
			} else {
				throw new UnableToLogin("Something unexpected happened", e);
			}
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
			Jsoup.connect(GiuaScraper.SiteURL).method(Method.GET).timeout(5000).execute();    //Se la richiesta impiega più di 5 secondi
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

	//endregion
}
