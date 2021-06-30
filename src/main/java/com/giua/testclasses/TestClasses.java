package com.giua.testclasses;

import com.giua.objects.*;
import com.giua.webscraper.GiuaScraper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static java.lang.System.nanoTime;

class TestClasses {

    private static GiuaScraper gS;
    private static String user = "";
    private static String password = "";

    private static void makeLogin() {
        gS = new GiuaScraper(user, password, true);    //togliere "phpsessid" per fare il login con username e password e lasciarlo per usare direttamente quel cookie
        gS.login();
    }

    private static void testDownload() {
        try {
            FileOutputStream out = new FileOutputStream("downloadtest.pdf");
            out.write(gS.download("/circolari/download/393/0"));
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void testNews(boolean forceRefresh) {
        System.out.println("Get news");
        List<News> allNews = gS.getAllNewsFromHome(forceRefresh);
        for (News news : allNews) {
            System.out.println(news.toString());
        }
    }

    private static void testVotes(boolean forceRefresh) {
        System.out.println("Get votes");
        Map<String, List<Vote>> votes = gS.getAllVotes(forceRefresh);
        for (String m : votes.keySet()) {
            System.out.println(m + ": " + votes.get(m).toString());
        }
        System.out.println(votes.get("Ed. civica").get(0).allToString());
        System.out.println(votes.get("Ed. civica").get(1).allToString());
    }

    private static void testAlerts(boolean forceRefresh) {
        System.out.println("Get avvisi");
        List<Alert> allAvvisi = gS.getAllAlerts(1, forceRefresh);
        for (Alert a : allAvvisi) {
            System.out.println(a.toString());
        }
        allAvvisi.get(0).getDetails(gS);
    }

    private static void testHomeworks(boolean forceRefresh) {
        System.out.println("Get homeworks");
        List<Homework> allHomework = gS.getAllHomeworks(null, forceRefresh);
        for (Homework a : allHomework) {
            System.out.println(a.toString());
        }
        System.out.println(gS.getHomework("2021-05-28").toString());
    }

    private static void testTests(boolean forceRefresh) {
        System.out.println("Get tests");
        List<Test> allTests = gS.getAllTests(null, forceRefresh);
        for (Test a : allTests) {
            System.out.println(a.toString());
        }
        System.out.println(gS.getTest("2021-05-18").toString());
    }

    private static void testNewsletters(boolean forceRefresh) {
        System.out.println("Get newsletters");
        List<Newsletter> allNewsletters = gS.getAllNewsletters(0, forceRefresh);
        for (Newsletter a : allNewsletters) {
            System.out.println(a.toString());
        }
        System.out.println(allNewsletters.get(0).attachments != null);
    }

    public static void testLessons(boolean forceRefresh) {
        System.out.println("Get lessons");
        List<Lesson> lessons = gS.getAllLessons("2021-05-22", forceRefresh);
        for (Lesson a : lessons) {
            System.out.println(a.toString());
        }
        System.out.println(lessons.get(2).activities);
    }

    public static void testReportCard(boolean forceRefresh) {
        System.out.println("Get report card");
        ReportCard reportCard = gS.getReportCard(false, forceRefresh);
        if (reportCard.exists) {
            for (String a : reportCard.allVotes.keySet()) {
                System.out.println(a);
            }
        }
    }

    public static void testNotes(boolean forceRefresh) {
        System.out.println("Get disciplinary notes");
        List<DisciplNotice> allDN = gS.getAllDisciplNotices(forceRefresh);
        for (DisciplNotice a : allDN) {
            System.out.println(a.toString());
        }
    }

    public static void testAbsences(boolean forceRefresh) {
        System.out.println("Get absences");
        List<Absence> allAbsences = gS.getAllAbsences(forceRefresh);
        for (Absence a : allAbsences) {
            System.out.println(a.toString());
        }
    }

    private static void testAll() {

        long t1;
        long t2;

        long tPhase1 = 0;
        long tPhase2 = 0;
        long tPhase3 = 0;

        t1 = System.currentTimeMillis();
        System.out.println("My internet work: " + GiuaScraper.isMyInternetWorking());
        t2 = System.currentTimeMillis();
        System.out.println("Tempo: " + (t2 - t1));
        t1 = System.currentTimeMillis();
        System.out.println("The site work: " + GiuaScraper.isSiteWorking());
        t2 = System.currentTimeMillis();
        System.out.println("Tempo: " + (t2 - t1));

        /////////////////////////////////////////////////////////////////////
        //NO CACHE
        //In questa prima parte vengono generate tutte le cose mentre nella seconda viene usata la cache

        System.out.println("\n\n----------------------Phase 1 - Testing all webscraper functions-----------------------------\n\n");

        t1 = nanoTime();
        gS = new GiuaScraper(user, password, true);    //togliere "phpsessid" per fare il login con username e password e lasciarlo per usare direttamente quel cookie
        gS.login();

        //Document doc = gS.getPage("");
        System.out.println("Account type: " + gS.getUserType());

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
        System.out.println("Account type: " + gS.getUserType());

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

        t2 = nanoTime();
        tPhase2 = t2 - t1;
        System.out.println("---------------------------------------------------");
        System.out.println("Tempo: " + t2 + "-" + t1);
        System.out.println("---------------------------------------------------");


        System.out.println("\n\n----------------------Phase 3 - Testing login with valid session-----------------------------\n\n");


        t1 = nanoTime();
        System.out.println("Logout...");
        String sessid = gS.getSessionCookie();

        gS = new GiuaScraper(user, password, sessid, true);
        System.out.println("Created new gS variable");


        System.out.println("Account type: " + gS.getUserType());

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

        GiuaScraper.setDebugMode(true);
        GiuaScraper.setSiteURL("http://hiemvault.ddns.net:9090");

        Scanner sc = new Scanner(System.in);
        if (user.equals("") && password.equals("")) {
            System.out.println("Please enter username: ");
            user= sc.nextLine();
            System.out.println("Password: ");
            password= sc.nextLine();
        }

        testAll();        //Chiamando questo metodo vengono effettuati i test di praticamente tutte le funzioni fondamentali e dello scraping della libreria
    }
}
