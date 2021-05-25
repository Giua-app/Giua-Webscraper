package com.giua.testclasses;

import com.giua.objects.*;
import com.giua.webscraper.GiuaScraper;
import org.jsoup.nodes.Document;

import java.util.*;

class TestClasses {
    //Main function, only used on the console version for testing
    public static void main(String[] args) {

        String user = "";
        String password = "";

        Scanner sc= new Scanner(System.in);
        if(user.equals("") && password.equals("")){
            System.out.println("Please enter username: ");
            user= sc.nextLine();
            System.out.println("Password: ");
            password= sc.nextLine();
        }

        GiuaScraper gS = new GiuaScraper(user, password);
        gS.login();

        //Document doc = gS.getPage("");
        //System.out.println("FUCK U"+gS.getUserType(doc));

        System.out.println("\n-------------------\nConnecting to " + GiuaScraper.SiteURL + "\n-------------------\n");


        System.out.println("--------VOTI--------");

        System.out.println("Get votes");
        Map<String, List<Vote>> votes = gS.getAllVotes();
        for(String m: votes.keySet()){
            System.out.println(m + ": " + votes.get(m).toString());
        }
        System.out.println(votes.get("Ed. civica").get(0).allToString());
        System.out.println(votes.get("Ed. civica").get(1).allToString());

        System.out.println("--------AVVISI---------");

        System.out.println("Get avvisi");
        List<Alert> allAvvisi = gS.getAllAlerts(1);
        for(Alert a: allAvvisi){
            System.out.println(a.toString());
        }
        allAvvisi.get(0).getDetails(gS);

		System.out.println("--------COMPITI--------");

		System.out.println("Get homeworks");
		List<Homework> allHomework = gS.getAllHomeworks(null);
		for(Homework a: allHomework){
			System.out.println(a.toString());
		}
		System.out.println(gS.getHomework("2021-05-28").toString());

		System.out.println("--------VERIFICHE--------");

		System.out.println("Get tests");
		List<Test> allTests = gS.getAllTests(null);
		for(Test a: allTests){
			System.out.println(a.toString());
		}
		System.out.println(gS.getTest("2021-05-18").toString());

		System.out.println("--------CIRCOLARI--------");

		System.out.println("Get tests");
		List<Newsletter> allNewsletters = gS.getAllNewsletters(2);
		for(Newsletter a: allNewsletters){
			System.out.println(a.toString());
		}
        System.out.println(allNewsletters.get(5).attachments.get(0));

        System.out.println("--------LEZIONI--------");

        System.out.println("Get lessons");
        List<Lesson> lessons = gS.getAllLessons("2021-05-22");
		for(Lesson a: lessons){
			System.out.println(a.toString());
		}
        System.out.println(lessons.get(2).activities);

    }
}
