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

package com.giua.testclasses;

import com.giua.objects.*;
import com.giua.webscraper.DownloadedFile;
import com.giua.webscraper.GiuaScraper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import static java.lang.System.nanoTime;

class TestClasses {

    private static GiuaScraper gS;
    private static String user = "";
    private static String password = "";
    public static boolean logEnabled = true;
    public static boolean speedTest = false;
    public static int speedTestAmount = 5;

    private static void logln(String msg){
        if (logEnabled) {
            System.out.println(msg);
        }
    }

    private static void println(String msg){
        if (!speedTest) {
            System.out.println(msg);
        }
    }
    
    //region Funzioni
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

    private static void testNews(boolean forceRefresh) {
        logln("Get news");
        List<News> allNews = gS.getAllNewsFromHome(forceRefresh);
        for (News news : allNews) {
            logln(news.toString());
        }
    }

    private static void testVotes(boolean forceRefresh) {
        logln("Get votes");
        Map<String, List<Vote>> votes = gS.getAllVotes(forceRefresh);
        for (String m : votes.keySet()) {
            logln(m + ": " + votes.get(m).toString());
        }
        logln(votes.get("Ed. civica").get(0).allToString());
        logln(votes.get("Ed. civica").get(1).allToString());
    }

    private static void testAlerts(boolean forceRefresh) {
        logln("Get avvisi");
        List<Alert> allAvvisi = gS.getAllAlerts(1, forceRefresh);
        for (Alert a : allAvvisi) {
            logln(a.toString());
        }
        allAvvisi.get(0).getDetails(gS);
        logln(allAvvisi.get(0).toString());
    }

    private static void testHomeworks(boolean forceRefresh) {
        logln("Get homeworks");
        List<Homework> allHomework = gS.getAllHomeworksWithoutDetails("2021-03", forceRefresh);
        for (Homework a : allHomework) {
            logln(a.toString());
        }
        logln(gS.getHomework("2021-03-07").toString());
    }

    private static void testTests(boolean forceRefresh) {
        logln("Get tests");
        List<Test> allTests = gS.getAllTestsWithoutDetails("2021-03", forceRefresh);
        for (Test a : allTests) {
            logln(a.toString());
        }
        logln(gS.getTest("2021-03-07").toString());
    }

    private static void testNewsletters(boolean forceRefresh) {
        logln("Get newsletters");
        List<Newsletter> allNewsletters = gS.getAllNewsletters(0, forceRefresh);
        for (Newsletter a : allNewsletters) {
            logln(a.toString());
        }
        logln(String.valueOf(allNewsletters.get(0).attachments != null));
        logln(gS.getAllNewslettersWithFilter(false, "2020-09", "Comunicazione", 1, true).toString());
    }

    public static void testLessons(boolean forceRefresh) {
        logln("Get lessons");
        List<Lesson> lessons = gS.getAllLessons("2021-05-22", forceRefresh);
        for (Lesson a : lessons) {
            logln(a.toString());
        }
        logln(lessons.get(2).activities);
        logln(gS.getAllLessonsOfSubject("Informatica", true).get(0).toString());
    }

    public static void testReportCard(boolean forceRefresh) {
        logln("Get report card");
        ReportCard reportCard = gS.getReportCard(false, forceRefresh);
        if (reportCard.exists) {
            logln(reportCard.toString());
        }
    }

    public static void testDocuments(boolean forceRefresh) {
        logln("Get documents");
        List<Document> documents = gS.getDocuments(forceRefresh);
        for (Document doc : documents)
            logln(doc.toString());
    }

    public static void testAutorization(boolean forceRefresh) {
        logln("Get autorization");
        Autorization autorization = gS.getAutorizations(forceRefresh);
        logln(autorization.toString());
    }

    public static void testNotes(boolean forceRefresh) {
        logln("Get disciplinary notes");
        List<DisciplNotice> allDN = gS.getAllDisciplNotices(forceRefresh);
        for (DisciplNotice a : allDN) {
            logln(a.toString());
        }
    }

    public static void testAbsences(boolean forceRefresh) {
        logln("Get absences");
        List<Absence> allAbsences = gS.getAllAbsences(forceRefresh);
        for (Absence a : allAbsences) {
            logln(a.toString());
        }
    }

    private static void startLogin() {
        gS = new GiuaScraper(user, password, true);
        gS.login();

        //Document doc = gS.getPage("");
        logln("Account type: " + gS.getUserTypeEnum());
    }
    //endregion
    
    
    
    
    private static void testSpeed(){

        logEnabled = false;
        List<Long> tSite = new Vector<>();
        List<Long> tInternet = new Vector<>();
        List<Long> tPhase1 = new Vector<>();
        List<Long> tPhase2 = new Vector<>();
        List<Long> tPhase3 = new Vector<>();
        List<Long> tPhasesTot = new Vector<>();
        int errors = 0;


        int i = 0;

        while(i < speedTestAmount) {



            long t1;
            long t2;

            System.out.println("Test " + (i+1) + "/" + speedTestAmount);
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
                startLogin();

                testNews(true);
                System.out.print("#");
                testVotes(true);
                System.out.print("#");
                testAlerts(true);
                System.out.print("#");
                testHomeworks(true);
                System.out.print("#");
                testTests(true);
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
                testHomeworks(false);
                System.out.print("#");
                testTests(false);
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

                gS = new GiuaScraper(user, password, phpsessid, true);
                gS.login();

                System.out.print("|");
                testNews(true);
                System.out.print("#");
                testVotes(true);
                System.out.print("#");
                testAlerts(true);
                System.out.print("#");
                testHomeworks(true);
                System.out.print("#");
                testTests(true);
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

        System.out.println("\n\n/---------------------LISTS----------------------------");


        Long tInternetAdd = 0L;
        System.out.print("|    Check Internet:                 ");
        for( Long value : tInternet){
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
        for( Long value : tPhase3){
            tPhase3Add += value;
            System.out.print(value + "ms ; ");
        }
        System.out.print("\n");


        Long tPhasesTotAdd = 0L;
        System.out.print("|    Tempo Totale impiegato:         ");
        for( Long value : tPhasesTot){
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

        System.out.println("--------COMPITI--------");
        testHomeworks(true);

        System.out.println("--------VERIFICHE--------");
        testTests(true);

        System.out.println("--------CIRCOLARI--------");
        testNewsletters(true);

        System.out.println("--------LEZIONI--------");
        testLessons(true);

        System.out.println("--------PAGELLA--------");
        testReportCard(true);

        System.out.println("--------NOTE--------");
        testNotes(true);

        System.out.println("--------ASSENZE--------");
        testAbsences(true);

        System.out.println("--------AUTORIZZAZIONI--------");
        testAutorization(true);

        System.out.println("--------DOCUMENTI--------");
        testDocuments(true);

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

        System.out.println("\n-------------------\nConnecting to " + GiuaScraper.getSiteURL() + "\n-------------------\n");

        System.out.println("--------NEWS--------");
        testNews(false);

        System.out.println("--------VOTI--------");
        testVotes(false);

        System.out.println("--------AVVISI---------");
        testAlerts(false);

        System.out.println("--------COMPITI--------");
        testHomeworks(false);

        System.out.println("--------VERIFICHE--------");
        testTests(false);

        System.out.println("--------CIRCOLARI--------");
        testNewsletters(false);

        System.out.println("--------LEZIONI--------");
        testLessons(false);

        System.out.println("--------PAGELLA--------");
        testReportCard(false);

        System.out.println("--------NOTE--------");
        testNotes(false);

        System.out.println("--------ASSENZE--------");
        testAbsences(false);

        System.out.println("--------AUTORIZZAZIONI--------");
        testAutorization(false);

        System.out.println("--------DOCUMENTI--------");
        testDocuments(false);

        t2 = nanoTime();
        tPhase2 = t2 - t1;
        System.out.println("---------------------------------------------------");
        System.out.println("Tempo: " + t2 + "-" + t1);
        System.out.println("---------------------------------------------------");


        System.out.println("\n\n----------------------Phase 3 - Testing login with valid session-----------------------------\n\n");


        t1 = nanoTime();
        System.out.println("Logout...");
        String phpsessid = gS.getCookie();

        gS = new GiuaScraper(user, password, phpsessid, true);
        gS.login();
        System.out.println("Created new gS variable");


        System.out.println("Account type: " + gS.getUserTypeEnum());

        System.out.println("\n-------------------\nConnecting to " + GiuaScraper.getSiteURL() + "\n-------------------\n");

        System.out.println("--------NEWS--------");
        testNews(true);

        System.out.println("--------VOTI--------");
        testVotes(true);

        System.out.println("--------AVVISI---------");
        testAlerts(true);

        System.out.println("--------COMPITI--------");
        testHomeworks(true);

        System.out.println("--------VERIFICHE--------");
        testTests(true);

        System.out.println("--------CIRCOLARI--------");
        testNewsletters(true);

        System.out.println("--------LEZIONI--------");
        testLessons(true);

        System.out.println("--------PAGELLA--------");
        testReportCard(true);

        System.out.println("--------NOTE--------");
        testNotes(true);

        System.out.println("--------ASSENZE--------");
        testAbsences(true);

        System.out.println("--------AUTORIZZAZIONI--------");
        testAutorization(true);

        System.out.println("--------DOCUMENTI--------");
        testDocuments(true);

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

    //Main function, only used on the console version for testing
    public static void main(String[] args) {

        /*try {
            user = args[0];
            password = args[1];
        } catch(Exception e) {

            Scanner sc = new Scanner(System.in);

            if (user.equals("") && password.equals("")) {
                logln("Please enter username: ");
                user = sc.nextLine();
                logln("Password: ");
                password = sc.nextLine();
            }
        }

        try {
            logEnabled = Boolean.parseBoolean(args[2]);
        } catch (Exception ignored) {
        }

        try {
            speedTest = Boolean.parseBoolean(args[3]);
        } catch (Exception ignored) {
        }

        try {
            speedTestAmount = Integer.parseInt(args[4]);
        } catch (Exception ignored) {
        }

        GiuaScraper.setDebugMode(logEnabled);
        //GiuaScraper.setSiteURL("https://registro.giua.edu.it");
        GiuaScraper.setSiteURL("http://hiemvault.ddns.net:9090");

        startLogin();
        //gS = new GiuaScraper(user, password, true);
        //testAll(); //Chiamando questo metodo vengono effettuati i test di praticamente tutte le funzioni fondamentali e dello scraping della libreria

        System.out.println(gS.getNearHomeworks(true));
        System.out.println(gS.getNearTests(true));*/

        //logln(gS.getAllObservations(true).toString());


        /*List<Newsletter> nl = gS.getAllNewsletters(0, false);
        List<Alert> al = gS.getAllAlerts(0, false);
        Map<String, List<Vote>> vot = gS.getAllVotes(false);
        List<Homework> hw = gS.getAllHomeworksWithoutDetails("2021-03", false);

        al.get(0).getDetails(gS);


        //logln(nl.toJSON());
        //logln(gS.getAllVotes(false).get("Italiano").get(0).toJSON());

        long t1 = nanoTime();
        try {
            new JsonHelper().saveNewslettersToFile("newsletters.json",nl);
            new JsonHelper().saveVotesToFile("votes.json",vot);
            new JsonHelper().saveAlertsToFile("alerts.json", al);
            new JsonHelper().saveHomeworksToFile("homeworks.json", hw);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long t2 = nanoTime();

        logln("---- HO IMPIEGATO " + (t2 / 1000000 - t1 / 1000000) + "ms");

        t1 = nanoTime();
        List<Newsletter> robe = new JsonHelper().parseJsonForNewsletters(Paths.get("newsletters.json"));
        Map<String, List<Vote>> votesOut = new JsonHelper().parseJsonForVotes(Paths.get("votes.json"));
        List<Alert> alertsOut = new JsonHelper().parseJsonForAlerts(Paths.get("alerts.json"));
        t2 = nanoTime();

        logln("---- HO IMPIEGATO " + (t2 / 1000000 - t1 / 1000000) + "ms");

        for(Newsletter n : robe){
            logln(n.toString());
        }

        for (String m : votesOut.keySet()) {
            logln(m + ": " + votesOut.get(m).toString());
        }

        for (Alert a : alertsOut) {
            logln(a.toString());
        }

        /*JsonHelper jsonHelper = new JsonHelper();
        jsonHelper.saveVotesToString(vot);*/


    }
}
