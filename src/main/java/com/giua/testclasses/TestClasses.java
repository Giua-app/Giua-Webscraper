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
import com.giua.webscraper.GiuaScraper;
import com.giua.webscraper.GiuaScraperExceptions;

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
    public static boolean logEnabled = true;

    private static void logln(String msg){
        if (logEnabled) {
            System.out.println(msg);
        }
    }

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
    }

    private static void testHomeworks(boolean forceRefresh) {
        logln("Get homeworks");
        List<Homework> allHomework = gS.getAllHomeworks(null, forceRefresh);
        for (Homework a : allHomework) {
            logln(a.toString());
        }
        logln(gS.getHomework("2021-05-28").toString());
    }

    private static void testTests(boolean forceRefresh) {
        logln("Get tests");
        List<Test> allTests = gS.getAllTests(null, forceRefresh);
        for (Test a : allTests) {
            logln(a.toString());
        }
        logln(gS.getTest("2021-05-18").toString());
    }

    private static void testNewsletters(boolean forceRefresh) {
        logln("Get newsletters");
        List<Newsletter> allNewsletters = gS.getAllNewsletters(0, forceRefresh);
        for (Newsletter a : allNewsletters) {
            logln(a.toString());
        }
        logln(String.valueOf(allNewsletters.get(0).attachments != null));
    }

    public static void testLessons(boolean forceRefresh) {
        logln("Get lessons");
        List<Lesson> lessons = gS.getAllLessons("2021-05-22", forceRefresh);
        for (Lesson a : lessons) {
            logln(a.toString());
        }
        logln(lessons.get(2).activities);
    }

    public static void testReportCard(boolean forceRefresh) {
        logln("Get report card");
        ReportCard reportCard = gS.getReportCard(false, forceRefresh);
        if (reportCard.exists) {
            for (String a : reportCard.allVotes.keySet()) {
                logln(a);
            }
        }
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
        logln("Account type: " + gS.getUserType());
    }


    private static void testAll() {

        long t1;
        long t2;

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
        gS.login();
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
        user = args[0];
        password = args[1];
        try {
            logEnabled = Boolean.parseBoolean(args[2]);
        } catch(Exception e) {
            logEnabled = true;
        }

        GiuaScraper.setDebugMode(logEnabled);

        //GiuaScraper.setSiteURL("http://hiemvault.ddns.net:9090");

        Scanner sc = new Scanner(System.in);
        if (user.equals("") && password.equals("")) {
            logln("Please enter username: ");
            user = sc.nextLine();
            logln("Password: ");
            password = sc.nextLine();
        }

        //FIXME: ATTENZIONE CI SONO ANCORA ERRORI IRRISOLTI NELLA IMPLEMENTAZIONE DELLE MANUTENZIONI
        testAll();        //Chiamando questo metodo vengono effettuati i test di praticamente tutte le funzioni fondamentali e dello scraping della libreria




        //startLogin();

        /*gS = new GiuaScraper("", "");

        logln(gS.getMaintenanceInfo().toString());
        logln(gS.isMaintenanceScheduled());
        logln(gS.getAllVotes(false));*/
    }
}
