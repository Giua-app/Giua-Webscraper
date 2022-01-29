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

package com.giua.testclasses;

import com.giua.objects.*;
import com.giua.pages.*;
import com.giua.utils.JsonBuilder;
import com.giua.webscraper.GiuaScraper;

import java.util.*;

import static java.lang.System.nanoTime;

class TestClasses {

    private static GiuaScraper gS;
    private static String user = "";
    private static String password = "";
    public static boolean logEnabled = true;
    public static boolean speedTest = false;
    public static int speedTestAmount = 5;

    //Main function, only used on the console version for testing
    public static void main(String[] args) throws Exception {
        try {
            user = args[0];
            password = args[1];
        } catch (Exception e) {

            Scanner sc = new Scanner(System.in);

            if (user.equals("") && password.equals("")) {
                logln("Please enter username: ");
                user = sc.nextLine();
                logln("Password: ");
                password = sc.nextLine();
            }
        }

        try {
            GiuaScraper.setSiteURL(args[2]);
        } catch (Exception ignored) {
        }

        try {
            logEnabled = Boolean.parseBoolean(args[3]);
        } catch (Exception ignored) {
        }

        try {
            speedTest = Boolean.parseBoolean(args[4]);
        } catch (Exception ignored) {
        }

        try {
            speedTestAmount = Integer.parseInt(args[5]);
        } catch (Exception ignored) {
        }

        GiuaScraper.setDebugMode(logEnabled);

        //testAll(); //Chiamando questo metodo vengono effettuati i test di praticamente tutte le funzioni fondamentali e dello scraping della libreria
        startLogin();
        testNewsletters(false);
    }

    private static void logln(String msg) {
        if (logEnabled) {
            System.out.println(msg);
        }
    }

    private static void println(String msg) {
        if (!speedTest) {
            System.out.println(msg);
        }
    }

    //region Funzioni
    /*
    private static void testDownload() {
        try {
            DownloadedFile downloadedFile = gS.download("/circolari/download/393/0");
            FileOutputStream out = new FileOutputStream("downloadtest." + downloadedFile.fileExtension);
            out.write(downloadedFile.data);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
*/
    private static void testNews(boolean forceRefresh) {
        logln("Get news");
        HomePage homePage = gS.getHomePage(forceRefresh);
        logln("News:");
        List<News> allNews = homePage.getAllNewsFromHome();
        for (News news : allNews) {
            logln(news.toString());
        }
    }

    private static void testVotes(boolean forceRefresh) {
        logln("Get votes");
        VotesPage votesPage = gS.getVotesPage(forceRefresh);
        Map<String, List<Vote>> votes = votesPage.getAllVotes();
        for (String m : votes.keySet()) {
            logln(m + ": " + votes.get(m).toString());
        }
        logln("Get votes with subject filter");
        List<List<Vote>> filterVotes = votesPage.getAllVotes("Italiano");
        for (Vote vote : filterVotes.get(0)) { //Prendo i voti del primo quadrimestre
            logln(vote.toString());
            logln(vote.quarterly);
        }
    }

    private static void testAlerts(boolean forceRefresh) {
        /**logln("Get avvisi");
        List<Alert> allAvvisi = gS.getAlertsPage(forceRefresh).getAllAlerts(1);
        for (Alert a : allAvvisi) {
            logln(a.toString());
        }
        logln("Marking first alert as read");
         gS.getAlertsPage(false).markAlertAsRead(allAvvisi.get(0));
         logln("Get first alert with filter");
         logln(gS.getAlertsPage(false).getAllAlertsWithFilters(false, "g").get(0).toString());
         logln("Get details of first alert");
         allAvvisi.get(0).getDetailsToString(gS);
         logln(allAvvisi.get(0).toString());
         */
        logln("Test notifiche \r\n");
        AlertsPage aP = new AlertsPage(gS);

        List<Alert> newAlerts = aP.getAllAlertsWithFilters(false, "per la materia");
        List<Alert> oldAlerts = newAlerts.subList(3, newAlerts.size() - 1);
        List<Alert> o = aP.getAlertsToNotify(oldAlerts);
    }

    private static void testAgendaPage(boolean forceRefresh) {
        logln("Get homeworks");
        List<AgendaObject> allAgendaObject = gS.getAgendaPage(forceRefresh).getAllAgendaObjectsWithoutDetails(null);
        for (AgendaObject a : allAgendaObject) {
            logln(a.toString());
            logln(a.getRepresentingClass().toString());
        }
        //logln(gS.getHomework("2021-03-07").toString());
    }

    /*private static void testTests(boolean forceRefresh) {
        logln("Get tests");
        List<Test> allTests = gS.getPinBoardPage(forceRefresh).getAllTestsWithoutDetails(null);
        for (Test a : allTests) {
            logln(a.toString());
        }
        //logln(gS.getTest("2021-03-07").toString());
    }*/

    private static void testNewsletters(boolean forceRefresh) {
        logln("Get newsletters");
        List<Newsletter> allNewsletters = gS.getNewslettersPage(forceRefresh).getAllNewsletters(1);
        for (Newsletter a : allNewsletters) {
            logln(a.toString());
        }
        logln("Marking first newsletter as read");
        gS.getNewslettersPage(forceRefresh).markNewsletterAsRead(allNewsletters.get(0));
        logln("First newsletter has attachments?");
        logln(String.valueOf(allNewsletters.get(0).attachmentsUrl != null));
        if (allNewsletters.get(0).attachmentsUrl != null) {
            logln("Get first attachment url");
            logln(String.valueOf(allNewsletters.get(0).attachmentsUrl));
        }
        logln("Get newsletters with a filter");
        logln(gS.getNewslettersPage(forceRefresh).getAllNewslettersWithFilter(false, "", "f").toString());
    }

    public static void testLessons(boolean forceRefresh) {
        logln("Get lessons");
        List<Lesson> lessons = gS.getLessonsPage(forceRefresh).getAllLessonsFromDate("2021-01-12");
        for (Lesson a : lessons) {
            logln(a.toString());
        }
        logln("Get activities second lesson");
        logln(lessons.get(0).activities);
    }

    public static void testArgumentsActivities(boolean forceRefresh) {
        logln("Get arguments and activities");
        List<Lesson> lessons = gS.getArgumentsActivitiesPage(forceRefresh).getAllLessonsOfSubject("Informatica");
        for (Lesson a : lessons) {
            logln(a.toString());
        }
    }

    public static void testDocuments(boolean forceRefresh) {
        logln("Get documents");
        List<Document> documents = gS.getDocumentsPage(forceRefresh).getDocuments();
        for (Document doc : documents)
            logln(doc.toString());
    }

    public static void testAuthorization(boolean forceRefresh) {
        logln("Get authorization");
        Authorization authorization = gS.getAuthorizationsPage(forceRefresh).getAuthorizations();
        logln(authorization.toString());
    }

    public static void testNotes(boolean forceRefresh) {
        logln("Get disciplinary notes");
        logln("Non funzionante");
        /*List<DisciplinaryNotices> allDN = gS.getDisciplinaryNotesPage(forceRefresh).getAllDisciplinaryNotices();
        for (DisciplinaryNotices a : allDN) {
            logln(a.toString());
        }*/
    }

    public static void testAbsences(boolean forceRefresh) {
        logln("Get absences");
        AbsencesPage absencesPage = gS.getAbsencesPage(forceRefresh);
        List<Absence> allAbsences = absencesPage.getAllAbsences();
        for (Absence a : allAbsences) {
            logln(a.toString());
        }
        logln(absencesPage.getAllExtraInfo());
    }

    public static void testReportCard(boolean forceRefresh) {
        logln("Get report card");

        ReportcardPage reportcardPage= gS.getReportCardPage(forceRefresh);
        ReportCard rC=reportcardPage.getReportcard(ReportcardPage.lastYear);
        logln(rC.toString());
        rC=reportcardPage.getReportcard(ReportcardPage.firstQuaterly);
        logln(rC.toString());
        rC=reportcardPage.getReportcard(ReportcardPage.secondQuaterly);
        logln(rC.toString());
        rC=reportcardPage.getReportcard(ReportcardPage.finalExams);
        logln(rC.toString());

        /*
        for (String m : rC.allVotes.keySet())
            logln(m + ": " + rC.allVotes.get(m).toString());
        if(!rC.allDebts.equals(null))
            for (String m : rC.allDebts.keySet())
                logln(m + ": " + rC.allDebts.get(m).toString());
         */
     }

    private static void startLogin() {
        startLogin(null);
    }

    private static void startLogin(com.giua.utils.LoggerManager lm) {
        gS = new GiuaScraper(user, password, true, lm);
        gS.login();

        //Document doc = gS.getPage("");
        logln("Account type: " + gS.getUserTypeEnum());
        logln("Ultimo accesso: " + gS.getHomePage(false).getLastAccessTime().toString());
    }
    //endregion


    private static void testSpeed() {

        logEnabled = false;
        List<Long> tSite = new Vector<>();
        List<Long> tInternet = new Vector<>();
        List<Long> tPhase1 = new Vector<>();
        List<Long> tPhase2 = new Vector<>();
        List<Long> tPhase3 = new Vector<>();
        List<Long> tPhasesTot = new Vector<>();
        List<Long> tJsonBuilder = new Vector<>();
        List<Long> tJsonParser = new Vector<>();
        int errors = 0;

        LoggerManager loggerManager = new LoggerManager("GiuaScraper-silent");

        System.out.println("----  AVVIO SPEEDTEST PER SCRAPER ----");
        System.out.println("Legenda:  |   LOGIN    |  CACHE   | SESSIONE |");
        System.out.println("          [############|##########|##########]");

        int i = 0;

        while (i < speedTestAmount) {
            long t1;
            long t2;

            System.out.println("Test " + (i + 1) + "/" + speedTestAmount);
            System.out.print("Progress: [");

            try {
                t1 = nanoTime();
                GiuaScraper.isMyInternetWorking();
                System.out.print("#");
                t2 = nanoTime();

                tInternet.add((t2 / 1000000) - (t1 / 1000000));

                t1 = nanoTime();
                GiuaScraper.isSiteWorking();
                System.out.print("#");
                t2 = nanoTime();

                tSite.add((t2 / 1000000) - (t1 / 1000000));

                /////////////////////////////////////////////////////////////////////
                //NO CACHE
                //In questa prima parte vengono generate tutte le cose mentre nella seconda viene usata la cache


                t1 = nanoTime();
                startLogin(loggerManager);

                testNews(true);
                System.out.print("#");
                testVotes(true);
                System.out.print("#");
                testAlerts(true);
                System.out.print("#");
                testAgendaPage(true);
                System.out.print("#");
                testNewsletters(true);
                System.out.print("#");
                testLessons(true);
                System.out.print("#");
                testReportCard(true);
                System.out.print("#");
                testNotes(true);
                System.out.print("#");
                testAbsences(true);
                System.out.print("#");

                t2 = nanoTime();
                tPhase1.add((t2 / 1000000) - (t1 / 1000000));


                ////////////////////////////////////////////////////////////
                //CACHE


                t1 = nanoTime();
                System.out.print("|");
                testNews(false);
                System.out.print("#");
                testVotes(false);
                System.out.print("#");
                testAlerts(false);
                System.out.print("#");
                testAgendaPage(false);
                System.out.print("#");
                testNewsletters(false);
                System.out.print("#");
                testLessons(false);
                System.out.print("#");
                testReportCard(false);
                System.out.print("#");
                testNotes(false);
                System.out.print("#");
                testAbsences(false);
                System.out.print("#");

                t2 = nanoTime();
                tPhase2.add((t2 / 1000000) - (t1 / 1000000));


                t1 = nanoTime();
                String phpsessid = gS.getCookie();

                gS = new GiuaScraper(user, password, phpsessid, true, loggerManager);
                gS.login();

                System.out.print("|");
                testNews(true);
                System.out.print("#");
                testVotes(true);
                System.out.print("#");
                testAlerts(true);
                System.out.print("#");
                testAgendaPage(true);
                System.out.print("#");
                testNewsletters(true);
                System.out.print("#");
                testLessons(true);
                System.out.print("#");
                testReportCard(true);
                System.out.print("#");
                testNotes(true);
                System.out.print("#");
                testAbsences(true);
                System.out.print("#");

                t2 = nanoTime();
                tPhase3.add((t2 / 1000000) - (t1 / 1000000));

                tPhasesTot.add(tPhase1.get(i) + tPhase2.get(i) + tPhase3.get(i));
                System.out.println("]");
            } catch (Exception e){
                System.out.println("/!\\]");
                System.out.println("Error: " + e.getMessage());
                tInternet.add(-1L);
                tSite.add(-1L);
                tPhase1.add(-1L);
                tPhase2.add(-1L);
                tPhase3.add(-1L);
                tPhasesTot.add(-1L);
                errors += 1;
            }

            i++;
        }
        i = 0;

        System.out.println("----  AVVIO SPEEDTEST PER JSON BUILDER ----");
        System.out.println("Legenda:  | Scrittura |");
        System.out.println("          [###########]");

        while (i < speedTestAmount) {
            long t1;
            long t2;


            System.out.println("Test " + (i + 1) + "/" + speedTestAmount);
            System.out.print("Progress: [");

            try {
                JsonBuilder jb = new JsonBuilder("test.json", gS);

                t1 = nanoTime();
                startLogin(loggerManager);

                jb.writeDocuments(gS.getDocumentsPage(false).getDocuments());
                System.out.print("#");
                jb.writeObservations(gS.getObservationsPage(false).getAllObservations());
                System.out.print("#");
                jb.writeAlerts(gS.getAlertsPage(false).getAllAlerts(1));
                System.out.print("#");
                jb.writeAgendaObjects(gS.getAgendaPage(false).getAllAgendaObjectsWithoutDetails(null));
                System.out.print("#");
                jb.writeNews(gS.getHomePage(false).getAllNewsFromHome());
                System.out.print("#");
                jb.writeMaintenance(gS.getMaintenanceInfo());
                System.out.print("#");
                jb.writeLessons(gS.getLessonsPage(false).getAllLessonsFromDate(new Date()));
                System.out.print("#");
                jb.writeAbsences(gS.getAbsencesPage(false).getAllAbsences());
                System.out.print("#");
                jb.writeDisciplinaryNotices(gS.getDisciplinaryNotesPage(false).getAllDisciplinaryNotices());
                System.out.print("#");
                jb.writeDocuments(gS.getDocumentsPage(false).getDocuments());
                System.out.print("#");

                t2 = nanoTime();
                tJsonBuilder.add((t2 / 1000000) - (t1 / 1000000));
                System.out.println("]");
            } catch (Exception e) {
                System.out.println("/!\\]");
                System.out.println("Error: " + e.getMessage());
                tInternet.add(-1L);
                tSite.add(-1L);
                tPhase1.add(-1L);
                tPhase2.add(-1L);
                tPhase3.add(-1L);
                tPhasesTot.add(-1L);
                errors += 1;
            }

            i++;
        }

        System.out.println("\n\n/---------------------LISTS----------------------------");


        Long tInternetAdd = 0L;
        System.out.print("|    Check Internet:                 ");
        for (Long value : tInternet) {
            tInternetAdd += value;
            System.out.print(value + "ms ; ");
        }
        System.out.print("\n");


        Long tSiteAdd = 0L;
        System.out.print("|    Check Site:                     ");
        for( Long value : tSite){
            tSiteAdd += value;
            System.out.print(value + "ms ; ");
        }
        System.out.print("\n");


        Long tPhase1Add = 0L;
        System.out.print("|    Fase 1 (login iniziale):        ");
        for( Long value : tPhase1){
            tPhase1Add += value;
            System.out.print(value + "ms ; ");
        }
        System.out.print("\n");


        Long tPhase2Add = 0L;
        System.out.print("|    Fase 2 (cache):                 ");
        for( Long value : tPhase2){
            tPhase2Add += value;
            System.out.print(value + "ms ; ");
        }
        System.out.print("\n");


        Long tPhase3Add = 0L;
        System.out.print("|    Fase 3 (riutilizzo sessione):   ");
        for (Long value : tPhase3) {
            tPhase3Add += value;
            System.out.print(value + "ms ; ");
        }
        System.out.print("\n");

        Long tJsonBuilder3Add = 0L;
        System.out.print("|    Json Builder (scrittura):       ");
        for (Long value : tJsonBuilder) {
            tJsonBuilder3Add += value;
            System.out.print(value + "ms ; ");
        }
        System.out.print("\n");


        Long tPhasesTotAdd = 0L;
        System.out.print("|    Tempo Totale impiegato:         ");
        for (Long value : tPhasesTot) {
            tPhasesTotAdd += value;
            System.out.print(value + "ms ; ");
        }
        System.out.print("\n");

        System.out.println("\\--------------------------------------------------------");



        System.out.println("\n\n");
        System.out.println("/---------------------FINAL RESULTS " + speedTestAmount + " TESTS-----------------------");
        System.out.println("|    Check Internet:                 " + tInternetAdd / speedTestAmount + "ms");
        System.out.println("|    Check Site:                     " + tSiteAdd / speedTestAmount + "ms");
        System.out.println("|    Fase 1 (login iniziale):        " + tPhase1Add / speedTestAmount + "ms");
        System.out.println("|    Fase 2 (cache):                 " + tPhase2Add / speedTestAmount + "ms");
        System.out.println("|    Fase 3 (riutilizzo sessione):   " + tPhase3Add / speedTestAmount + "ms");
        System.out.println("|");
        System.out.println("|    Tempo impiegato mediamente:     " + tPhasesTotAdd / speedTestAmount + "ms");
        System.out.println("|    Tempo impiegato totalmente:     " + tPhasesTotAdd / 1000 + "s");
        System.out.println("\\--------------------------------------------------------");
        if(errors != 0) {
            System.err.println("Attenzione! " + errors + " su " + speedTestAmount + " hanno fallito con errore");
        }



    }


    private static void testAll() {

        if(speedTest){
            System.out.println("--------STARTING SPEED TEST-----");
            testSpeed();
            return;
        }

        long t1;
        long t2;
        long tSite;

        long tPhase1 = 0;
        long tPhase2 = 0;
        long tPhase3 = 0;

        t1 = System.currentTimeMillis();
        logln("My internet work: " + GiuaScraper.isMyInternetWorking());
        t2 = System.currentTimeMillis();
        logln("Tempo: " + (t2 - t1));
        t1 = System.currentTimeMillis();
        logln("The site work: " + GiuaScraper.isSiteWorking());
        t2 = System.currentTimeMillis();
        logln("Tempo: " + (t2 - t1));
        tSite = t2 - t1;

        /////////////////////////////////////////////////////////////////////
        //NO CACHE
        //In questa prima parte vengono generate tutte le cose mentre nella seconda viene usata la cache

        System.out.println("\n\n----------------------Phase 1 - Testing all webscraper functions-----------------------------\n\n");

        t1 = nanoTime();
        startLogin();

        System.out.println("\n-------------------\nConnecting to " + GiuaScraper.getSiteURL() + "\n-------------------\n");

        System.out.println("--------NEWS--------");
        testNews(true);

        System.out.println("--------VOTI--------");
        testVotes(true);

        System.out.println("--------AVVISI---------");
        testAlerts(true);

        System.out.println("--------AGENDA--------");
        testAgendaPage(true);

        System.out.println("--------CIRCOLARI--------");
        testNewsletters(true);

        System.out.println("--------LEZIONI--------");
        testLessons(true);

        System.out.println("--------PAGELLA--------");
        System.out.println("Non disponibile");
        //testReportCard(true);

        System.out.println("--------NOTE--------");
        testNotes(true);

        System.out.println("--------ASSENZE--------");
        testAbsences(true);

        System.out.println("--------AUTORIZZAZIONI--------");
        testAuthorization(true);

        System.out.println("--------DOCUMENTI--------");
        testDocuments(true);

        System.out.println("--------ARGOMENTI E ATTIVITA--------");
        testArgumentsActivities(true);

        t2 = nanoTime();
        tPhase1 = t2 - t1;
        System.out.println("---------------------------------------------------");
        System.out.println("Tempo: " + tPhase1);
        System.out.println("---------------------------------------------------");


        ////////////////////////////////////////////////////////////
        //CACHE

        System.out.println("\n\n----------------------Phase 2 - Testing cache-----------------------------\n\n");

        t1 = nanoTime();

        //gS.setSiteURL("https://registroasiaiai.giua.edu.it");

        //Document doc = gS.getPage("");
        System.out.println("Account type: " + gS.getUserTypeEnum());
        logln("Ultimo accesso: " + gS.getHomePage(false).getLastAccessTime().toString());

        System.out.println("\n-------------------\nConnecting to " + GiuaScraper.getSiteURL() + "\n-------------------\n");

        System.out.println("--------NEWS--------");
        testNews(false);

        System.out.println("--------VOTI--------");
        testVotes(false);

        System.out.println("--------AVVISI---------");
        testAlerts(false);

        System.out.println("--------AGENDA--------");
        testAgendaPage(true);

        System.out.println("--------CIRCOLARI--------");
        testNewsletters(false);

        System.out.println("--------LEZIONI--------");
        testLessons(false);

        System.out.println("--------PAGELLA--------");
        System.out.println("Non disponibile");
        //testReportCard(false);

        System.out.println("--------NOTE--------");
        testNotes(false);

        System.out.println("--------ASSENZE--------");
        testAbsences(false);

        System.out.println("--------AUTORIZZAZIONI--------");
        testAuthorization(false);

        System.out.println("--------DOCUMENTI--------");
        testDocuments(false);

        System.out.println("--------ARGOMENTI E ATTIVITA--------");
        testArgumentsActivities(false);

        t2 = nanoTime();
        tPhase2 = t2 - t1;
        System.out.println("---------------------------------------------------");
        System.out.println("Tempo: " + t2 + "-" + t1);
        System.out.println("---------------------------------------------------");


        System.out.println("\n\n----------------------Phase 3 - Testing login with valid session-----------------------------\n\n");


        t1 = nanoTime();
        System.out.println("Logout...");
        String phpsessid = gS.getCookie();

        gS = new GiuaScraper(user, password, phpsessid, true, null);
        gS.login();
        System.out.println("Created new gS variable");


        System.out.println("Account type: " + gS.getUserTypeEnum());
        logln("Ultimo accesso: " + gS.getHomePage(false).getLastAccessTime().toString());

        System.out.println("\n-------------------\nConnecting to " + GiuaScraper.getSiteURL() + "\n-------------------\n");

        System.out.println("--------NEWS--------");
        testNews(true);

        System.out.println("--------VOTI--------");
        testVotes(true);

        System.out.println("--------AVVISI---------");
        testAlerts(true);

        System.out.println("--------AGENDA--------");
        testAgendaPage(true);

        System.out.println("--------CIRCOLARI--------");
        testNewsletters(true);

        System.out.println("--------LEZIONI--------");
        testLessons(true);

        System.out.println("--------PAGELLA--------");
        //testReportCard(true);

        System.out.println("--------NOTE--------");
        testNotes(true);

        System.out.println("--------ASSENZE--------");
        testAbsences(true);

        System.out.println("--------AUTORIZZAZIONI--------");
        testAuthorization(true);

        System.out.println("--------DOCUMENTI--------");
        testDocuments(true);

        System.out.println("--------ARGOMENTI E ATTIVITA--------");
        testArgumentsActivities(true);

        t2 = nanoTime();
        tPhase3 = t2 - t1;
        System.out.println("---------------------------------------------------");
        System.out.println("Tempo: " + tPhase3);
        System.out.println("---------------------------------------------------");

        System.out.println("\n\n");
        System.out.println("/---------------------RESULTS----------------------------");
        System.out.println("|    Fase 1 (login iniziale):        " + (tPhase1 / 1000000) + "ms");
        System.out.println("|    Fase 2 (cache):                 " + (tPhase2 / 1000000) + "ms");
        System.out.println("|    Fase 3 (riutilizzo sessione):   " + (tPhase3 / 1000000) + "ms");
        System.out.println("|");
        System.out.println("|    Totale:                         " + (tPhase1 / 1000000 + tPhase2 / 1000000 + tPhase3 / 1000000) + "ms");
        System.out.println("\\--------------------------------------------------------");
    }


    private static class LoggerManager extends com.giua.utils.LoggerManager {

        public LoggerManager(String tag) {
            super(tag);
        }

        @Override
        protected void saveToData(Log log) {
            //Do nothing
        }
    }
}