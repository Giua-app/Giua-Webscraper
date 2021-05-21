package com.giua.testclasses;

import com.giua.objects.*;
import com.giua.webscraper.GiuaScraper;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

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

        System.out.println("\n-------------------\nConnecting to " + GiuaScraper.SiteURL + "\n-------------------\n");


        /*System.out.println("--------VOTI--------");

        System.out.println("Get votes");
        Map<String, List<Vote>> votes = Vote.getAllVotes(gS);
        for(String m: votes.keySet()){
            System.out.println(m + ": " + votes.get(m).toString());
        }
        System.out.println(votes.get("Ed. civica").get(0).allToString());
        System.out.println(votes.get("Ed. civica").get(1).allToString());

        System.out.println("--------AVVISI---------");

        System.out.println("Get avvisi");
        List<Alert> allAvvisi = Alert.getAllAvvisi(1, gS);
        for(Alert a: allAvvisi){
            System.out.println(a.toString());
        }

		System.out.println("--------COMPITI--------");
		System.out.println("Get homeworks");
		List<Homework> allHomework = Homework.getAllHomeworks(gS);
		for(Homework a: allHomework){
			System.out.println(a.toString());
		}
		System.out.println(Homework.getHomework("2021-05-28", gS).toString());

		System.out.println("--------VERIFICHE--------");
		System.out.println("Get tests");
		List<Test> allTests = Test.getAllTestsWithoutDetails(gS);
		for(Test a: allTests){
			System.out.println(a.toString());
		}
		System.out.println(Test.getTest("2021-05-18", gS).toString());*/

		System.out.println("--------CIRCOLARI--------");
		System.out.println("Get tests");
		List<Newsletter> allNewsletters = Newsletter.getAllNewsletters(2, gS);
		/*for(Newsletter a: allNewsletters){
			System.out.println(a.toString());
		}*/
        System.out.println(allNewsletters.get(5).attachments.get(0));
        System.out.println(allNewsletters.get(5).attachments.get(1));

    }
}
