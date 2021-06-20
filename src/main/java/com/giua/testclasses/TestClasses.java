package com.giua.testclasses;

import com.giua.objects.*;
import com.giua.webscraper.GiuaScraper;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

class TestClasses {
    //Main function, only used on the console version for testing
    public static void main(String[] args) {

        GiuaScraper.setDebugMode(true);
        String user = "";
        String password = "";

        Scanner sc= new Scanner(System.in);
        if(user.equals("") && password.equals("")){
            System.out.println("Please enter username: ");
            user= sc.nextLine();
            System.out.println("Password: ");
            password= sc.nextLine();
        }

        long t1;
        long t2;

        t1 = System.currentTimeMillis();
        System.out.println("My internet work: " + GiuaScraper.isMyInternetWorking());
        t2 = System.currentTimeMillis();
        System.out.println("Tempo: " + (t2-t1));
        t1 = System.currentTimeMillis();
        System.out.println("The site work: " + GiuaScraper.isSiteWorking());
        t2 = System.currentTimeMillis();
        System.out.println("Tempo: " + (t2-t1));

        /////////////////////////////////////////////////////////////////////
        //NO CACHE
        //In questa prima parte vengono generate tutte le cose mentre nella seconda viene usata la cache

        t1 = System.currentTimeMillis();
        GiuaScraper gS = new GiuaScraper(user, password, true);    //togliere "phpsessid" per fare il login con username e password e lasciarlo per usare direttamente quel cookie

        //Document doc = gS.getPage("");
        System.out.println("Account type: " + gS.getUserType());

        System.out.println("\n-------------------\nConnecting to " + GiuaScraper.SiteURL + "\n-------------------\n");


        System.out.println("--------VOTI--------");

        System.out.println("Get votes");
        Map<String, List<Vote>> votes = gS.getAllVotes(true);
        for(String m: votes.keySet()){
            System.out.println(m + ": " + votes.get(m).toString());
        }
        System.out.println(votes.get("Ed. civica").get(0).allToString());
        System.out.println(votes.get("Ed. civica").get(1).allToString());

        System.out.println("--------AVVISI---------");

        System.out.println("Get avvisi");
        List<Alert> allAvvisi = gS.getAllAlerts(1, true);
        for(Alert a: allAvvisi){
            System.out.println(a.toString());
        }
        allAvvisi.get(0).getDetails(gS);

		System.out.println("--------COMPITI--------");

		System.out.println("Get homeworks");
		List<Homework> allHomework = gS.getAllHomeworks(null, true);
		for(Homework a: allHomework){
			System.out.println(a.toString());
		}
		System.out.println(gS.getHomework("2021-05-28").toString());

		System.out.println("--------VERIFICHE--------");

		System.out.println("Get tests");
		List<Test> allTests = gS.getAllTests(null, true);
		for(Test a: allTests){
			System.out.println(a.toString());
		}
		System.out.println(gS.getTest("2021-05-18").toString());

		System.out.println("--------CIRCOLARI--------");

		System.out.println("Get tests");
		List<Newsletter> allNewsletters = gS.getAllNewsletters(2, true);
		for(Newsletter a: allNewsletters){
			System.out.println(a.toString());
		}
        System.out.println(allNewsletters.get(5).attachments != null);

        System.out.println("--------LEZIONI--------");

        System.out.println("Get lessons");
        List<Lesson> lessons = gS.getAllLessons("2021-05-22", true);
		for(Lesson a: lessons){
			System.out.println(a.toString());
		}
        System.out.println(lessons.get(2).activities);

        System.out.println("--------PAGELLA--------");
        System.out.println("Get report card");
        ReportCard reportCard = gS.getReportCard(false, true);
        for(String a: reportCard.allVotes.keySet()){
            System.out.println(a);
        }
        t2 = System.currentTimeMillis();
        System.out.println("---------------------------------------------------");
        System.out.println("Tempo: " + (t2-t1));
        System.out.println("---------------------------------------------------");


        ////////////////////////////////////////////////////////////
        //CACHE

        t1 = System.currentTimeMillis();

        //gS.setSiteURL("https://registroasiaiai.giua.edu.it");

        //Document doc = gS.getPage("");
        System.out.println("Account type: " + gS.getUserType());

        System.out.println("\n-------------------\nConnecting to " + GiuaScraper.SiteURL + "\n-------------------\n");


        System.out.println("--------VOTI--------");

        System.out.println("Get votes");
        Map<String, List<Vote>> votes2 = gS.getAllVotes(false);
        for(String m: votes2.keySet()){
            System.out.println(m + ": " + votes2.get(m).toString());
        }
        System.out.println(votes2.get("Ed. civica").get(0).allToString());
        System.out.println(votes2.get("Ed. civica").get(1).allToString());

        System.out.println("--------AVVISI---------");

        System.out.println("Get avvisi");
        List<Alert> allAvvisi2 = gS.getAllAlerts(1, false);
        for(Alert a: allAvvisi2){
            System.out.println(a.toString());
        }
        //allAvvisi.get(0).getDetails(gS);

        System.out.println("--------COMPITI--------");

        System.out.println("Get homeworks");
        List<Homework> allHomework2 = gS.getAllHomeworks(null, false);
        for(Homework a: allHomework2){
            System.out.println(a.toString());
        }
        //System.out.println(gS.getHomework("2021-05-28").toString());

        System.out.println("--------VERIFICHE--------");

        System.out.println("Get tests");
        List<Test> allTests2 = gS.getAllTests(null, false);
        for(Test a: allTests2){
            System.out.println(a.toString());
        }
        //System.out.println(gS.getTest("2021-05-18").toString());

        System.out.println("--------CIRCOLARI--------");

        System.out.println("Get newsletters");
        List<Newsletter> allNewsletters2 = gS.getAllNewsletters(2, false);
        for(Newsletter a: allNewsletters2){
            System.out.println(a.toString());
        }
        //System.out.println(allNewsletters2.get(5).attachments != null);

        System.out.println("--------LEZIONI--------");

        System.out.println("Get lessons");
        List<Lesson> lessons2 = gS.getAllLessons("2021-05-22", false);
        for(Lesson a: lessons2){
            System.out.println(a.toString());
        }
        //System.out.println(lessons2.get(2).activities);


        t2 = System.currentTimeMillis();
        System.out.println("---------------------------------------------------");
        System.out.println("Tempo: " + (t2-t1));
        System.out.println("---------------------------------------------------");






        System.out.println("Logout...");
        String sessid = gS.getSessionCookie();

        gS = new GiuaScraper(user, password, sessid, true);
        System.out.println("Created new gS variable");


        System.out.println("Account type: " + gS.getUserType());

        System.out.println("\n-------------------\nConnecting to " + GiuaScraper.SiteURL + "\n-------------------\n");


        System.out.println("--------VOTI--------");

        System.out.println("Get votes");
        Map<String, List<Vote>> votes3 = gS.getAllVotes(false);
        for(String m: votes2.keySet()){
            System.out.println(m + ": " + votes2.get(m).toString());
        }
        System.out.println(votes2.get("Ed. civica").get(0).allToString());
        System.out.println(votes2.get("Ed. civica").get(1).allToString());

        System.out.println("--------AVVISI---------");

        System.out.println("Get avvisi");
        List<Alert> allAvvisi3 = gS.getAllAlerts(1, false);
        for(Alert a: allAvvisi2){
            System.out.println(a.toString());
        }
        allAvvisi.get(0).getDetails(gS);

        System.out.println("--------COMPITI--------");

        System.out.println("Get homeworks");
        List<Homework> allHomework3 = gS.getAllHomeworks(null, false);
        for(Homework a: allHomework2){
            System.out.println(a.toString());
        }
        System.out.println(gS.getHomework("2021-05-28").toString());

        System.out.println("--------VERIFICHE--------");

        System.out.println("Get tests");
        List<Test> allTests3 = gS.getAllTests(null, false);
        for(Test a: allTests2){
            System.out.println(a.toString());
        }
        System.out.println(gS.getTest("2021-05-18").toString());

        System.out.println("--------CIRCOLARI--------");

        System.out.println("Get tests");
        List<Newsletter> allNewsletters3 = gS.getAllNewsletters(2, false);
        for(Newsletter a: allNewsletters2){
            System.out.println(a.toString());
        }
        System.out.println(allNewsletters2.get(5).attachments != null);

        System.out.println("--------LEZIONI--------");

        System.out.println("Get lessons");
        List<Lesson> lessons3 = gS.getAllLessons("2021-05-22", false);
        for(Lesson a: lessons2){
            System.out.println(a.toString());
        }
        System.out.println(lessons2.get(2).activities);

        System.out.println("--------PAGELLA--------");

        System.out.println("Get report card");
        ReportCard reportCard2 = gS.getReportCard(false, false);
        for(String a: reportCard.allVotes.keySet()){
            System.out.println(a);
        }
        t2 = System.currentTimeMillis();
        System.out.println("---------------------------------------------------");
        System.out.println("Tempo: " + (t2-t1));
        System.out.println("---------------------------------------------------");
    }
}
