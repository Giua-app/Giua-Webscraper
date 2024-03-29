/*
 * Giua Webscraper library
 * A webscraper of the online school workbook giua@school
 * Copyright (C) 2021 - 2022 Hiem, Franck1421 and contributors
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

import com.giua.objects.Maintenance;
import com.giua.pages.*;
import com.giua.utils.GiuaScraperUtils;
import com.giua.utils.LoggerManager;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

/* -- Giua Webscraper BETA -- */
// Tested with version 1.4.0 of giua@school
public class GiuaScraper extends GiuaScraperExceptions {

	//region Variabili globali
	private final String user;
	private String realUsername;
	private final String password;
	private String userType = "";
	private static String globalSiteUrl = "https://registro.giua.edu.it";    //URL per tutti gli oggetti GiuaScraper
	private String privateSiteUrl = "";    //URL per un solo oggetto GiuaScraper. Se impostato prevale su quello globale
	private static boolean debugMode;
	final public boolean cacheable;        //Indica se si possono utilizzare le cache
	private String PHPSESSID = "";
	private Connection session;
	private long lastGetPageTime = 0;
	private final boolean demoMode;
	private static LoggerManager lm = new LoggerManager("GiuaScraper");

	public enum userTypes {
		STUDENT,
        PARENT,
        TEACHER,
        ADMIN,
        PRINCIPAL,
        ATA,
        DEMO
    }


	//region Cache
	private AbsencesPage absencesPageCache = null;
	private AlertsPage alertsPageCache = null;
	private ArgumentsActivitiesPage argumentsActivitiesPageCache = null;
	private AuthorizationsPage authorizationsPageCache = null;
	private DocumentsPage documentsPageCache = null;
	private HomePage homePageCache = null;
	private InterviewsPage interviewsPageCache = null;
	private LessonsPage lessonsPageCache = null;
	private NewslettersPage newslettersPageCache = null;
	private DisciplinaryNoticesPage disciplinaryNotesPageCache = null;
	private ObservationsPage observationsPageCache = null;
	private AgendaPage agendaPageCache = null;
	private ReportcardPage reportCardPageCache = null;
	private VotesPage votesPageCache = null;
	private ProfilePage profilePageCache = null;
	private Document getPageCache = null;
	//endregion

	//endregion

	//region Metodi getter e setter

	/**
	 * Permette di ottenere il {@link LoggerManager} usato da GiuaScraper
	 * Utile nel caso GiuaScraper abbia un LoggerManager personalizzato impostato
	 *
	 * @return {@link LoggerManager} usato da questa istanza
	 */
	public LoggerManager getLoggerManager() {
		return lm;
	}

	/**
	 * Permette di settare l'URL globale del registro
	 *
	 * @param newUrl formattato come "https://example.com"
	 */
	public static void setGlobalSiteUrl(String newUrl) {
		if (!newUrl.endsWith("/"))
			GiuaScraper.globalSiteUrl = newUrl;
		else
			GiuaScraper.globalSiteUrl = newUrl.substring(0, newUrl.length() - 1);    //Togli l'ultimo / dal nuovo url
	}

	/**
	 * Permette di ottenere l'URL globale del registro
	 *
	 * @return l'URL del registro formattato come "https://example.com"
	 */
	public static String getGlobalSiteUrl() {
		return GiuaScraper.globalSiteUrl;
	}

	/**
	 * Permette di settare l'URL privato del registro
	 *
	 * @param newUrl formattato come "https://example.com"
	 */
	public void setPrivateSiteUrl(String newUrl) {
		if (!newUrl.endsWith("/"))
			privateSiteUrl = newUrl;
		else
			privateSiteUrl = newUrl.substring(0, newUrl.length() - 1);    //Togli l'ultimo / dal nuovo url
	}

	/**
	 * Permette di ottenere l'URL globale del registro
	 *
	 * @return l'URL del registro formattato come "https://example.com"
	 */
	public String getPrivateSiteUrl() {
		return privateSiteUrl;
	}

	/**
	 * Ottieni l'URL che si dovrebbe utilizzare.<br>
	 * Se impostato verrò ritornato l'URL privato altrimenti quello globale.
	 *
	 * @return l'URL da utilizzare: globale o privato
	 */
	public String getSiteUrl() {
		if (privateSiteUrl.equals(""))
			return getGlobalSiteUrl();
		else
			return getPrivateSiteUrl();
	}

	/**
	 * Permette di ottenere il cookie della sessione "PHPSESSID"
	 *
	 * @return il cookie "PHPSESSID"
	 */
	public String getCookie() {
		return PHPSESSID;
	}

	/**
	 * Ottiene il nome utente utilizzato per loggarsi
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Ottiene il nome utente reale della persona se caricato.
	 */
	public String getRealUsername(){
		return realUsername;
	}

	public Connection getSession(){
		return session;
	}

	public boolean isDemoMode(){
		return demoMode;
	}

	//endregion

	//region Costruttori della classe

    /**
     * Costruttore della classe {@link GiuaScraper} che permette lo scraping della pagina del Giua
     *
     * @param user     es. nome.utente.f1
     * @param password password
     * @param newLm    istanza {@link LoggerManager} che lo scraper deve usare per scrivere i suoi log.
     *                 Mettere {@code null} per usare il LoggerManager di default che stampa i log su console
     */
    public GiuaScraper(String user, String password, LoggerManager newLm) {
        if (newLm != null) {
            lm = newLm;
        }
        this.user = user;
        this.password = password;
        this.cacheable = true;
        this.demoMode = false;
        lm.d("---GiuaScraper avviato---");
        initiateSession();
    }


    /**
     * Costruttore della classe {@link GiuaScraper} che permette lo scraping della pagina del Giua
     *
     * @param user      es. nome.utente.f1
     * @param password  password
     * @param cacheable true se deve usare la cache, false altrimenti
     * @param newLm     istanza {@link LoggerManager} che lo scraper deve usare per scrivere i suoi log.
     *                  Mettere {@code null} per usare il LoggerManager di default che stampa i log su console
     */
    public GiuaScraper(String user, String password, boolean cacheable, LoggerManager newLm) {
        if (newLm != null) {
            lm = newLm;
        }
        this.user = user;
        this.password = password;
        this.cacheable = cacheable;
        this.demoMode = false;
        lm.d("---GiuaScraper avviato---");
        initiateSession();
    }

    /**
     * Puoi usare questo per fare il login diretto con il phpsessid. Nel caso sia invalido, il login verrà
     * effettuato con le credenziali
     *
     * @param user      es. nome.utente.f1
     * @param password  password
     * @param newCookie il cookie della sessione
     * @param cacheable true se deve usare la cache, false altrimenti
     * @param newLm     istanza {@link LoggerManager} che lo scraper deve usare per scrivere i suoi log.
     *                  Mettere {@code null} per usare il LoggerManager di default che stampa i log su console
     */
    public GiuaScraper(String user, String password, String newCookie, boolean cacheable, LoggerManager newLm) {
        if (newLm != null) {
            lm = newLm;
        }
        this.user = user;
        this.password = password;
        this.cacheable = cacheable;
        this.demoMode = false;
        lm.d("---GiuaScraper avviato---");
        PHPSESSID = newCookie;
        initiateSession(newCookie);
    }

    /**
     * Costruttore della classe {@link GiuaScraper} che permette lo scraping della pagina del Giua
     *
     * @param user      es. nome.utente.f1
     * @param demoMode  Indica se la modalità demo è attiva o no. Nella demo mode vengono generati dati non basati sulla realtà.
     *                  In questa modalità si tiene conto come giorno attuale il 01-11-2021
     * @param cacheable true se deve usare la cache, false altrimenti
     * @param newLm     istanza {@link LoggerManager} che lo scraper deve usare per scrivere i suoi log.
     *                  Mettere {@code null} per usare il LoggerManager di default che stampa i log su console
     */
    public GiuaScraper(String user, String password, boolean cacheable, boolean demoMode, LoggerManager newLm) {
        if (newLm != null) {
            lm = newLm;
        }
        this.user = user;
        this.password = password;
        this.cacheable = cacheable;
        this.demoMode = demoMode;
        lm.d("---GiuaScraper avviato---");
        initiateSession();
    }

    /**
     * Puoi usare questo per fare il login diretto con il phpsessid. Nel caso sia invalido, il login verrà
     * effettuato con le credenziali
     *
     * @param user      es. nome.utente.f1
     * @param demoMode  Indica se la modalità demo è attiva o no. Nella demo mode vengono generati dati non basati sulla realtà.
     *                  In questa modalità si tiene conto come giorno attuale il 01-11-2021
     * @param newCookie il cookie della sessione
     * @param cacheable true se deve usare la cache, false altrimenti
     * @param newLm     istanza {@link LoggerManager} che lo scraper deve usare per scrivere i suoi log.
     *                  Mettere {@code null} per usare il LoggerManager di default che stampa i log su console
     */
    public GiuaScraper(String user, String password, String newCookie, boolean cacheable, boolean demoMode, LoggerManager newLm) {
        if (newLm != null) {
            lm = newLm;
        }
        this.user = user;
        this.password = password;
        this.cacheable = cacheable;
        this.demoMode = demoMode;
        lm.d("---GiuaScraper avviato---");
        PHPSESSID = newCookie;
        initiateSession(newCookie);
    }

    /**
     * Puoi usare questo per fare il login diretto con il phpsessid. Nel caso sia invalido, il login verrà
     * effettuato con le credenziali
     *
     * @param user      es. nome.utente.f1
     * @param password  password
     * @param newCookie il cookie della sessione
     * @param newLm     istanza {@link LoggerManager} che lo scraper deve usare per scrivere i suoi log.
     *                  Mettere {@code null} per usare il LoggerManager di default che stampa i log su console
     */
    public GiuaScraper(String user, String password, String newCookie, LoggerManager newLm) {
        if (newLm != null) {
            lm = newLm;
        }
        this.user = user;
		this.password = password;
		this.cacheable = true;
		this.demoMode = false;
		lm.d("---GiuaScraper avviato---");
		PHPSESSID = newCookie;
		initiateSession(newCookie);
	}

	//endregion

	//region Metodi pages

	/**
	 * Ottiene la pagina dei voti
	 */
	public VotesPage getVotesPage(boolean forceRefresh) {
		if (votesPageCache == null || forceRefresh) {
			if (cacheable) {
				votesPageCache = new VotesPage(this);
				return votesPageCache;
			} else
				return new VotesPage(this);
		} else
			return votesPageCache;
	}

	public HomePage getHomePage(boolean forceRefresh) {
		if (homePageCache == null || forceRefresh) {
			if (cacheable) {
				homePageCache = new HomePage(this);
				return homePageCache;
			} else
				return new HomePage(this);
		} else
			return homePageCache;
	}

	public LessonsPage getLessonsPage(boolean forceRefresh) {
		if (lessonsPageCache == null || forceRefresh) {
			if (cacheable) {
				lessonsPageCache = new LessonsPage(this);
				return lessonsPageCache;
			} else
				return new LessonsPage(this);
		} else
			return lessonsPageCache;
	}

	public ArgumentsActivitiesPage getArgumentsActivitiesPage(boolean forceRefresh) {
		if (argumentsActivitiesPageCache == null || forceRefresh) {
			if (cacheable) {
				argumentsActivitiesPageCache = new ArgumentsActivitiesPage(this);
				return argumentsActivitiesPageCache;
			} else
				return new ArgumentsActivitiesPage(this);
		} else
			return argumentsActivitiesPageCache;
	}

	public DocumentsPage getDocumentsPage(boolean forceRefresh) {
		if (documentsPageCache == null || forceRefresh) {
			if (cacheable) {
				documentsPageCache = new DocumentsPage(this);
				return documentsPageCache;
			} else
				return new DocumentsPage(this);
		} else
			return documentsPageCache;
	}

	public AuthorizationsPage getAuthorizationsPage(boolean forceRefresh) {
		if (authorizationsPageCache == null || forceRefresh) {
			if (cacheable) {
				authorizationsPageCache = new AuthorizationsPage(this);
				return authorizationsPageCache;
			} else
				return new AuthorizationsPage(this);
		} else
			return authorizationsPageCache;
	}

	public AlertsPage getAlertsPage(boolean forceRefresh) {
		if (alertsPageCache == null || forceRefresh) {
			if (cacheable) {
				alertsPageCache = new AlertsPage(this);
				return alertsPageCache;
			} else
				return new AlertsPage(this);
		} else
			return alertsPageCache;
	}

	public AbsencesPage getAbsencesPage(boolean forceRefresh) {
		if (absencesPageCache == null || forceRefresh) {
			if (cacheable) {
				absencesPageCache = new AbsencesPage(this);
				return absencesPageCache;
			} else
				return new AbsencesPage(this);
		} else
			return absencesPageCache;
	}

	public DisciplinaryNoticesPage getDisciplinaryNotesPage(boolean forceRefresh) {
		if (disciplinaryNotesPageCache == null || forceRefresh) {
			if (cacheable) {
				disciplinaryNotesPageCache = new DisciplinaryNoticesPage(this);
				return disciplinaryNotesPageCache;
			} else
				return new DisciplinaryNoticesPage(this);
		} else
			return disciplinaryNotesPageCache;
	}

	public NewslettersPage getNewslettersPage(boolean forceRefresh) {
		if (newslettersPageCache == null || forceRefresh) {
			if (cacheable) {
				newslettersPageCache = new NewslettersPage(this);
				return newslettersPageCache;
			} else
				return new NewslettersPage(this);
		} else
			return newslettersPageCache;
	}

	public AgendaPage getAgendaPage(boolean forceRefresh) {
		if (agendaPageCache == null || forceRefresh) {
			if (cacheable) {
				agendaPageCache = new AgendaPage(this);
				return agendaPageCache;
			} else
				return new AgendaPage(this);
		} else
			return agendaPageCache;
	}

	public ObservationsPage getObservationsPage(boolean forceRefresh) {
		if (observationsPageCache == null || forceRefresh) {
			if (cacheable) {
				observationsPageCache = new ObservationsPage(this);
				return observationsPageCache;
			} else
				return new ObservationsPage(this);
		} else
			return observationsPageCache;
	}

	public ReportcardPage getReportCardPage(boolean forceRefresh) {
		if (reportCardPageCache == null || forceRefresh) {
			if (cacheable) {
				reportCardPageCache = new ReportcardPage(this);
				return reportCardPageCache;
			} else
				return new ReportcardPage(this);
		} else
			return reportCardPageCache;
	}

	public InterviewsPage getInterviewsPage(boolean forceRefresh) {
		if (interviewsPageCache == null || forceRefresh) {
			if (cacheable) {
				interviewsPageCache = new InterviewsPage(this);
				return interviewsPageCache;
			} else
				return new InterviewsPage(this);
		} else
			return interviewsPageCache;
	}

	public ProfilePage getProfilePage(boolean forceRefresh) {
		if (profilePageCache == null || forceRefresh) {
			if (cacheable) {
				profilePageCache = new ProfilePage(this);
				return profilePageCache;
			} else
				return new ProfilePage(this);
		} else
			return profilePageCache;
	}

	//endregion

	/**
	 * Ottiene il banner della login page se presente
	 *
	 * @return Il testo del banner (in html).
	 * Ritorna vuoto se non c'è banner
	 */
	public String getLoginPageBanner() {
		Document doc = getPageNoCookie(""); //pagina di login
		Element els;

		try {
			els = doc.getElementsByClass("alert alert-warning gs-mb-2 gs-ml-3 gs-mr-3").get(0);
		} catch (IndexOutOfBoundsException e) {
			return "";
		}

		return els.child(0).html();
	}

	public static String getSchoolName() throws IOException {
		Document doc = Jsoup.newSession()
				.url(GiuaScraper.getGlobalSiteUrl() + "/login/form/")
				.method(Method.GET)
				.get(); //pagina di login
		Element els;

		els = doc.getElementsByClass("hidden-xs gs-big").get(0); //nome scuola abbreviato

		if (els.text().split("'").length > 1)
			return els.text().split("'")[1];

		return "";
	}

/*
	//region Controllo aggiornamenti oggetti

	/**
	 * Controlla se ci sono nuove verifiche presenti
	 *
	 * Attenzione: questa funzione non prende le informazioni
	 * dalle news, ma dalla pagina Agenda
	 *
	 * @param yearmonth Anno-Mese in cui controllare
	 * @return Una lista di Test nuovi
	 *0/
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
	 *0/
	public List<Homework> checkForHomeworksUpdate(String yearmonth) {
		List<Homework> cache = allHomeworksCache;
		List<Homework> homework = getAllHomeworksWithoutDetails(yearmonth, true);

		return compareHomeworks(cache, homework);

	}


	//region ReportCard



	//endregion

	 */


	//region Funzioni fondamentali

	public static boolean isGoogleLoginAvailable() {
		Document doc = null;
		try {
			doc = Jsoup.connect(GiuaScraper.getGlobalSiteUrl() + "/").get();
		} catch (IOException e) {
			if (isSiteWorking())
				throw new SiteConnectionProblems("Can't get page because the website is down, retry later", e);
		}
		return !doc.getElementsByAttributeValue("href", "/login/gsuite").isEmpty();
	}


	private void initiateSession() {
		session = null; //Per sicurezza azzeriamo la variabile
		session = Jsoup.newSession();
		lm.d("Nuova sessione creata");
	}

    private void initiateSession(String cookie) {
        session = null; //Per sicurezza azzeriamo la variabile
        session = Jsoup.newSession().cookie("PHPSESSID", cookie);
        lm.d("Nuova sessione da cookie creata");
    }

    public boolean isMaintenanceScheduled() {
        Document doc = getPage("login/form/");
        Elements els = doc.getElementsByClass("col-sm-12 bg-danger gs-mb-4 text-center");

        if (!els.isEmpty()) {
			lm.d("Trovata manutenzione programmata");
            return true;
        }
        lm.d("Nessuna manutenzione programmata trovata");
        return false;
    }

    public boolean isMaintenanceActive() {
        if (demoMode)
            return GiuaScraperDemo.isMaintenanceActive();
        Document doc = getPage("login/form/");
        Elements loginForm = doc.getElementsByAttributeValue("name", "login_form");
        Elements topBar = doc.getElementsByClass("col-sm-6");

        if (loginForm.isEmpty() && !topBar.isEmpty()) {
			lm.w("Manutenzione del registro in corso");
			return true;
		}

		lm.d("Nessuna manutenzione in corso trovata");
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


        //logln("getMaintenanceInfo: Non formattate: Inizio " + start + " | Fine " + end);

        //Crea dei format per fare il parsing di quelle stringhe
        SimpleDateFormat format1 = new SimpleDateFormat(" HH:mm  dd/MM/yyyy  ");
        SimpleDateFormat format2 = new SimpleDateFormat(" HH:mm  dd/MM/yyyy");

        Date startDate;
        Date endDate;
        try {
            startDate = format1.parse(start);
            endDate = format2.parse(end);
        } catch (Exception e) {
            throw new MaintenanceHasEmptyDates("Maintenance info did not find dates");
        }

        //logln("getMaintenanceInfo: formattate: Inizio " + startDate.toString() + " | Fine " + endDate.toString());

        Date currentDate = new Date();
        boolean isActive = false;
        boolean shouldBeActive = false;

        if (currentDate.after(startDate) && currentDate.before(endDate)) {
			lm.d("Secondo l'orario di inizio la manutenzione dovrebbe essere in corso");
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
	 * Non c'è bisogno di mettere gli "/" all'inizio dell'url
	 *
	 * @param url percorso della risorsa nel sito.
	 * @return Un oggetto {@code DownloadedFile}
	 */
	public DownloadedFile download(String url) {
		url = GiuaScraperUtils.convertGlobalPathToLocal(url);
		lm.d("Eseguo download di " + getSiteUrl() + url);
		try {
			if (url.startsWith("/"))
				url = url.substring(1);

			Connection.Response r = session.newRequest()
					.url(getSiteUrl() + "/" + url)
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
	 * @param page il percorso della pagina nel registro. Es: bacheca/circolari/
	 * @return Una pagina HTML come {@link Document}
	 * @throws MaintenanceIsActiveException La manutenzione è attiva e non si può richiedere la pagina indicata
	 * @throws SiteConnectionProblems       Il sito ha dei problemi di connessione
	 */
	public Document getPage(String page) throws MaintenanceIsActiveException, SiteConnectionProblems, YourConnectionProblems {
		page = GiuaScraperUtils.convertGlobalPathToLocal(page);
		if (demoMode)
			return GiuaScraperDemo.getPage(page);
		try {
			if (page.startsWith("/"))
				page = page.substring(1);
			/*boolean shouldHandleAccounts = true;

			if (page.equals("GIUASCRAPER_INTERNAL_REQUEST_HOME_WITHOUT_HANDLE_ACCOUNTS")) {
				page = "";
				shouldHandleAccounts = false;
			}*/


			//Se l'url è uguale a quello della richiesta precendente e l'ultima richiesta è stata fatta meno di 500ms fa allora usa la cache
			if (getPageCache != null && (getSiteUrl() + "/" + page).equals(getPageCache.location()) && System.nanoTime() - lastGetPageTime < 500_000_000) {
				lm.d("getPage: Rilevata richiesta uguale in meno di 500ms. Uso cache");
				return getPageCache;
			}

			if (page.equals("login/form/")) {
				return getPageNoCookie("login/form/");
			} else {
				if (isMaintenanceActive())
					throw new MaintenanceIsActiveException("The website is in maintenance");

				Connection.Response response = session.newRequest()
						.url(getSiteUrl() + "/" + page)
						.method(Method.GET)
						.execute();

				lm.d("getPage: " + getSiteUrl() + "/" + page + " caricato");

				Document doc = response.parse();

				if (page.equals("")) //if (page.equals("") && shouldHandleAccounts)
					doc = handleAccountWithMultipleStudents(doc);

				if (doc.getElementsByClass("col-sm-5 col-xs-8 text-right").isEmpty()) {    //Se vero il cookie è scaduto o non siamo loggati
					try {
						lm.d("getPage: Il cookie è scaduto o non siamo loggati, provo a riloggarmi");
						login();    //Prova a riloggarti
						lm.d("getPage: Re-Login eseguito correttamente");
						return getPageWithNoReLogin(page);    //Uso getPageWithNoReLogin per evitare una ricorsione infinita
					} catch (Exception e) {
						lm.e("Impossibile eseguire login da getPage: " + e.getMessage());
						throw new NotLoggedIn("Hai richiesto una pagina del registro senza essere loggato!");    //Qualsiasi errore accada vuol dire che non siamo riusciti a riloggarci
					}
				}
				getPageCache = doc;
				lastGetPageTime = System.nanoTime();
				return doc;
			}

        } catch (IOException e) {
            if (!isSiteWorking()) {
                throw new SiteConnectionProblems("Can't get page because the website is down, retry later", e);
			}
			e.printStackTrace();
		}
		//Qui ci si arriva solo in rari casi di connessioni molto lente
		lm.w("getPage: Nessuna pagina o errore ottenuto, qualcosa non va! Connessione lenta forse?");
		throw new YourConnectionProblems("Can't get page because the connection is too slow");
	}

	/**
	 * Ottiene la pagina HTML specificata dalla variabile {@code SiteURL} senza rieseguire
	 * il login in caso di cookie non valido
	 * Non c'e' bisogno di inserire {@code /} prima di un URL
	 *
	 * @param page il percorso della pagina nel registro. Es: bacheca/circolari/
	 * @return Una pagina HTML come {@link Document}
	 * @throws MaintenanceIsActiveException La manutenzione è attiva e non si può richiedere la pagina indicata
	 * @throws SiteConnectionProblems       Il sito ha dei problemi di connessione
	 */
	public Document getPageWithNoReLogin(String page) throws MaintenanceIsActiveException, SiteConnectionProblems {
		page = GiuaScraperUtils.convertGlobalPathToLocal(page);
		if (demoMode)
			return GiuaScraperDemo.getPage(page);
		try {
			if (page.startsWith("/"))
				page = page.substring(1);

			//Se l'url è uguale a quello della richiesta precendente e l'ultima richiesta è stata fatta meno di 500ms fa allora usa la cache
			if (getPageCache != null && (getSiteUrl() + "/" + page).equals(getPageCache.location()) && System.nanoTime() - lastGetPageTime < 500000000) {
				lm.d("getPageWithNoReLogin: Rilevata richiesta uguale in meno di 500ms. Uso cache");
				return getPageCache;
			}

			if (page.equals("login/form/")) {
				return getPageNoCookie("login/form/");
			} else {
				if (isMaintenanceActive())
					throw new MaintenanceIsActiveException("The website is in maintenance");

				Connection.Response response = session.newRequest()
						.url(getSiteUrl() + "/" + page)
						.method(Method.GET)
						.execute();

				lm.d("getPageWithNoReLogin: " + getSiteUrl() + "/" + page + " caricato");

				Document doc = response.parse();

				getPageCache = doc;
				lastGetPageTime = System.nanoTime();
				return doc;
			}

		} catch (IOException e) {
			if (!isSiteWorking()) {
				throw new SiteConnectionProblems("Can't get page because the website is down, retry later", e);
			}
			e.printStackTrace();
		}
		//Qui ci si arriva solo in rari casi di connessioni molto lente
		lm.w("getPage: Nessuna pagina o errore ottenuto, qualcosa non va! Connessione lenta forse?");
		throw new YourConnectionProblems("Can't get page because the connection is too slow");
	}

	/**
	 * Ottiene una pagina del registro senza usare il cookie quindi non controlla nemmeno se si è loggati
	 *
	 * @param page il percorso della pagina nel registro. Es: bacheca/circolari/
	 * @return La pagina che cercata
	 */
	public Document getPageNoCookie(String page) {
		page = GiuaScraperUtils.convertGlobalPathToLocal(page);
		if (demoMode)
			return GiuaScraperDemo.getPage(page);
		try {
			if (page.startsWith("/"))
				page = page.substring(1);

			Document doc = Jsoup.connect(getSiteUrl() + "/" + page)
					.get();

			lm.d("getPageNoCookie: " + getSiteUrl() + "/" + page + " caricato");

			return doc;

		} catch (IOException e) {
			if (!isSiteWorking()) {
				throw new SiteConnectionProblems("Can't get page because the website is down, retry later", e);
			}
			e.printStackTrace();
		}

		lm.w("getPage: Nessuna pagina o errore ottenuto, qualcosa non va! Connessione lenta forse?");
		throw new YourConnectionProblems("Can't get page because the connection is too slow");
	}

	/**
	 * Ottiene la pagina HTML specificata da un URL esterna al sito
	 *
	 * @param url l' url da cui prendere la pagina
	 * @return Una pagina HTML come {@link Document}
	 */
	public static Document getExtPage(String url) {
		try {

			Document doc = Jsoup.connect(url)
					.get();

			lm.d("getExtPage: pagina esterna " + url + " caricata");

			return doc;

		} catch (IOException e) {
			if(!isMyInternetWorking()){
				throw new YourConnectionProblems("Your internet may not work properly", e);
			}
		}

		return new Document(url);    //Non si dovrebbe mai verificare
	}

	/**
	 * Controlla se si è loggati dentro il registro
	 *
	 * @return true se e' loggato altrimenti false
	 * @deprecated usa {@link #isSessionValid} invece
	 */
	@Deprecated
    public Boolean checkLogin() {
        if (demoMode)
            return GiuaScraperDemo.checkLogin();
		try {
			Connection.Response res = session.newRequest()
                    .url(getSiteUrl())
                    .execute();

            //Il registro risponde alla richiesta GET all'URL https://registro.giua.edu.it
            //con uno statusCode pari a 302 se non sei loggato altrimenti risponde con 200
            //Attenzione: Il sito ritorna 200 anche quando è in manutenzione!
            lm.d("checkLogin: Registro risponde con codice " + res.statusCode() + ": " + (res.statusCode() != 302 ? "utente non loggato" : "utente loggato"));
			return res.statusCode() != 302;

		} catch (IOException e) {
			if (!isSiteWorking()) {
				throw new SiteConnectionProblems("Can't check login because the site is down, retry later", e);
			}
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Serve a controllare se la sessione è valida.
	 * Prende la pagina home e cerca il tipo dell' account se cè siamo loggati altrimenti no
	 *
	 * @param phpsessid il cookie da controllare
	 * @return true se la sessione è valida e quindi siamo loggati, false altrimenti
	 */
	public boolean isSessionValid(String phpsessid) {
		if (demoMode)
			return GiuaScraperDemo.isSessionValid();
		try {
			Document doc = Jsoup.connect(getSiteUrl())
					.cookie("PHPSESSID", phpsessid)
					.get();

			//lm.d("isSessionValid: Cerco di ottenere il tipo di account");

			if (realUsername != null && realUsername.equals(""))
				loadUserFromDocument(doc);
			// --- Ottieni tipo account
			final Elements elm = doc.getElementsByClass("col-sm-5 col-xs-8 text-right");
			userType = elm.text().split(".+\\(|\\)")[1];

		} catch (Exception e) {
			if (!isSiteWorking()) {
				throw new SiteConnectionProblems("Can't connect to website while checking the cookie. Please retry later", e);
			}

			//Se c'è stato un errore di qualunque tipo, allora non siamo riusciti ad ottenere il tipo
			// e quindi, la sessione non è valida
			lm.w("isSessionValid: Sessione non valida. Causa: " + e.getMessage());
			return false;
		}
		// Al contrario, se non ci sono errori (quindi siamo dentro la home) allora la sessione è valida
		//lm.d("isSessionValid: Sessione valida. Tipo account ottenuto: " + userType);
		return true;
	}

	/**
	 * @param doc Pagina home del registro
	 * @return
	 * @throws IOException
	 */
	private Document handleAccountWithMultipleStudents(Document doc) throws IOException {
		Element accounts = doc.getElementById("login_profilo_profilo");
		if (accounts == null) {
			lm.d("Questo account non ha più studenti collegati");
			return doc;
		}

		lm.w("Questo account ha più studenti collegati, scelgo automaticamente l'username corrente");

		Elements labels = accounts.getElementsByTag("label");

		if (labels.isEmpty()) {
			lm.e("Errore critico: nessuna label trovata nel dialogo per la scelta dell'account");
			return doc;
		}

		String profileId = "-1";
		String token = Objects.requireNonNull(doc.getElementById("login_profilo__token")).attr("value");

		for (Element accountLabel : labels) {
			if (accountLabel.text().contains(user)) {
				profileId = accountLabel.child(0).attr("value");
			}
		}

		if (profileId.equals("-1") || profileId.equals("")) {
			lm.e("Errore: non sono riuscito a trovare l'id dell'account");
			return doc;
		}

		Document doc2 = session.newRequest()
				.url(getSiteUrl() + "/login/profilo")
				.data("login_profilo[profilo]", profileId, "login_profilo[submit]", "", "login_profilo[_token]", token)
				.post();

		lm.d("Login tramite scelta account completato con successo");

		return doc2;
	}

	/**
	 * Ottiene tutti gli username dal dialogo della scelta dell'account
	 * (compare nella home se l'account corrente ha più di un studente collegato)
	 *
	 * @return ritorna null se l'account corrente non è un genitore, ritorna una lista vuota se non trova gli account
	 * @throws IOException
	 */
	public List<String> getAllUsernamesFromAccountDialog() throws IOException {
		if (getUserTypeEnum() == userTypes.PARENT) {

			//Document doc = getPage("GIUASCRAPER_INTERNAL_REQUEST_HOME_WITHOUT_HANDLE_ACCOUNTS");
			Document doc = session.newRequest() //in teoria se è scaduta la sessione questo darà errore
					.url(getSiteUrl())
					.get();

			Element accounts = doc.getElementById("login_profilo_profilo");

			if (accounts != null) {
				List<String> allUsernames = new Vector<>();
				lm.w("Questo account ha più studenti collegati, ottengo tutti gli account collegati");
				Elements labels = accounts.getElementsByTag("label");

				if (labels.isEmpty()) {
					lm.e("Errore: nessuna label trovata nel dialogo per la scelta dell'account");
					return new Vector<>();
				}

				//String profileId;
				//String token = Objects.requireNonNull(doc.getElementById("login_profilo__token")).attr("value");

				for (Element accountLabel : labels) {
					String username = accountLabel.text().split("\\(")[1].replace(")", "");
					//profileId = accountLabel.child(0).attr("value");

					allUsernames.add(username);
				}

				return allUsernames;
			}
			lm.w("Questo account non ha più studenti collegati");
			return new Vector<>();
		}
		return null;
	}

	/**
	 * La funzione per loggarsi effettivamente. Genera un phpsessid e un csrftoken per potersi loggare.
	 *
	 * @throws UnableToLogin                Il login è andato male e il sito non ha detto cosa è andato storto
	 * @throws MaintenanceIsActiveException La manutenzione è attiva e non ci si può loggare
	 * @throws SessionCookieEmpty           Il login è andato storto e il sito ha detto cosa è andato storto
	 */
	public void login() throws UnableToLogin, MaintenanceIsActiveException, SessionCookieEmpty {
		lm.d("login: richiesta procedura di login");
		if (demoMode)
			return;
		try {
			if (isMaintenanceActive())
				throw new MaintenanceIsActiveException("You can't login while the maintenace is active");

			lm.d("login: Controllo validità sessione");
			if (isSessionValid(PHPSESSID)) {
				//Il cookie esistente è ancora valido, niente login.
				lm.d("login: Sessione ancora valida, ignoro...");
				return;
			}
			lm.d("login: Sessione non valida, ne creo una nuova");
			initiateSession();

			//logln("login: First connection (login form)");

			Document firstRequestDoc = session.newRequest()
					.url(getSiteUrl() + "/login/form/")
					.get();

			//logln("login: Second connection (authenticate)");

			Document doc = session.newRequest()
					.url(getSiteUrl() + "/ajax/token/authenticate")
					.ignoreContentType(true)
					.get();

			//logln("\n\nCSRF HTML: \n" + doc + "\n\n");

			String CSRFToken = doc.body().toString().split(".+\":\"|\".")[1];        //prende solo il valore del csrf

			lm.d("login: Token CSRF: " + CSRFToken);

			//logln("login: Third connection (login form)");

			doc = session.newRequest()
					.url(getSiteUrl() + "/login/form/")
					.data("_username", this.user, "_password", this.password, "_csrf_token", CSRFToken, "login", "")
					.post();
			Elements err;
			if (isGoogleLoginAvailable())        //prendi errore dal sito
				err = doc.getElementsByClass("alert alert-danger gs-mt-4 gs-mb-4 gs-big");
			else
				err = doc.getElementsByClass("alert alert-danger");

			if (!err.isEmpty()) {
				throw new SessionCookieEmpty("Session cookie empty, login unsuccessful. Site says: " + err.text(), err.text());
			} else {
				PHPSESSID = session.cookieStore().getCookies().get(0).getValue();
				if (isSessionValid(PHPSESSID)) {
					lm.d("login: Cookie: " + PHPSESSID);
					lm.d("Login riuscito con account " + this.user);
				} else {
					throw new UnableToLogin("Login unsuccessful, and the site didn't give an error message. Please check the site from your web browser");
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
			Jsoup.connect("https://www.google.it").method(Method.POST).execute();
			return true;
		} catch (IOException io){
			return false;
		}
	}

	public static boolean isSiteWorking() {
		try {
			Jsoup.connect(GiuaScraper.getGlobalSiteUrl()).method(Method.GET).execute();
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
		if (demoMode)
			return GiuaScraperDemo.loadUserFromDocument();

		return loadUserFromDocument(getPage(""));
	}

	/**
	 * Prende il nome utente dalla pagina. Utilizza il {@code Document} passato come parametro.
	 *
	 * @return Il nome utente.
	 */
	public String loadUserFromDocument(Document doc) {
		if (demoMode)
			return GiuaScraperDemo.loadUserFromDocument();
		realUsername = doc.getElementsByClass("col-sm-5 col-xs-8 text-right").get(0).text().split(" [(]")[0];
		return realUsername;
	}

	public userTypes getUserTypeEnum() {
		if (demoMode)
			return GiuaScraperDemo.getUserTypeEnum();
		String text;
		try {
			if (userType.equals("")) {
				final Document doc = getPage("");
				final Elements elm = doc.getElementsByClass("col-sm-5 col-xs-8 text-right");
				text = elm.text().split(".+\\(|\\)")[1];
				userType = text;
				//return text;
			}
		} catch (Exception e) {
			throw new UnableToGetUserType("Unable to get user type, are we not logged in?", e);
		}

		if(userType.equals("Genitore")){
			return userTypes.PARENT;
		}
		if (userType.equals("Studente")) {
			return userTypes.STUDENT;
		}
		if (userType.equals("Dirigente")) {
			return userTypes.PRINCIPAL;
		}
		if (userType.equals("Amministratore")) {
			return userTypes.ADMIN;
		}

		//non dovrebbe mai accadere
		throw new UnableToGetUserType("Unable to parse userType to userTypes enum because it's unknown");
	}

	/**
	 * Ottieni tipo di utente
	 * ATTENZIONE: Se il tipo utente non è già stato caricato cerca di ottenerlo con una richiesta HTTP
	 *
	 * @return il tipo di utente come stringa
	 */
	public String getUserTypeString() {
		String text;
		try {
			if (userType.equals("")) {
				final Document doc = getPage("");
				final Elements elm = doc.getElementsByClass("col-sm-5 col-xs-8 text-right");
				text = elm.text().split(".+\\(|\\)")[1];
				userType = text;
				return text;
			} else {
				return userType;
			}
		} catch (Exception e) {
			throw new UnableToGetUserType("Unable to get user type, are we not logged in?", e);
		}
	}

    public void clearCache() {
        lm.d("Pulizia cache pages in corso...");
        absencesPageCache = null;
        alertsPageCache = null;
        argumentsActivitiesPageCache = null;
        authorizationsPageCache = null;
        documentsPageCache = null;
		homePageCache = null;
		interviewsPageCache = null;
		lessonsPageCache = null;
		newslettersPageCache = null;
		disciplinaryNotesPageCache = null;
		observationsPageCache = null;
		agendaPageCache = null;
		reportCardPageCache = null;
		votesPageCache = null;
	}

	//endregion

	//region Funzioni di debug

	public static void setDebugMode(boolean mode) {
		GiuaScraper.debugMode = mode;
	}

	public static boolean getDebugMode() {
		return GiuaScraper.debugMode;
	}

	//endregion
}
