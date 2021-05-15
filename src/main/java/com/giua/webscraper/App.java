package com.giua.webscraper;

import java.io.IOException;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



/* Giua Webscraper 0.6.1
 * 
 *  JSoup: library for webscraping
 *
 */
public class App 
{

	
	// Insert user and password of the account
	private static String user = ""; 
	private static String password = "";
	
	private static Map<String, String> PHPSESSID = null;
	private static String CSRFToken = null;
	

	public static Document getPage(String url) {
		try {
			
			if(checkLogin() == false) {
				print("getPage: Not logged in");
				print("getPage: Calling login method");
				login(user, password);
			}
			
			print("getPage: Getting page...");
			
			Connection.Response res = Jsoup.connect(url)
				    .method(Method.GET)
				    .cookies(PHPSESSID)
				    .execute();
			
			Document doc = res.parse();
			
			print("getPage: Done!");
			return doc;
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static Boolean checkLogin() {
		try {
			
			if(PHPSESSID == null) {
				return false; //Not logged in
			}
			
			Connection.Response res = Jsoup.connect("https://registro.giua.edu.it/login/form/")
				    .method(Method.POST)
				    .cookies(PHPSESSID)
				    .execute();
			
			Document doc = res.parse();
			
			Elements logout_button = doc.getElementsByAttributeValue("title", "Esci dal Registro Elettronico");
			
			//The login screen is the most simple to scrape information from, in this case we search
			//for the log out button, if it doesn't exist we are not logged in.
			// Since we have already loaded the login page, this process is very fast
			if(logout_button.toString() != "") {
				return true; //logged in
			} else {
				return false; //logged out
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	

	//The most important function, it handles the login process
	public static void login(String username, String password) 
	{
		try {
			
			Connection.Response res = Jsoup.connect("https://registro.giua.edu.it/login/form")
            	    .method(Method.GET)
            	    .execute();

            	Document doc = res.parse();
            	PHPSESSID = res.cookies();

            print("login: First connection (login form)");
            System.out.printf("login: Title: %s\n", doc.title());
            System.out.printf("login: Cookie: %s\n", PHPSESSID);
            
            
            print("login: Second connection (authenticate)");
            Connection.Response res2 = Jsoup.connect("https://registro.giua.edu.it/ajax/token/authenticate")
            		.cookies(PHPSESSID)
            	    .method(Method.GET)
            	    .ignoreContentType(true)
            	    .execute();
            
            print("login: get csrf token");
            
            String[] temp = res2.body().split(":", 0);
            String temp2 = temp[1].replaceAll("\"}", ""); //regex things idk
            CSRFToken = temp2.replaceAll("\"", "");
            
            
            
            //print("Page content: " + res2.body());
            print("login: CSRF Token: " + CSRFToken);
            
            print("login: Third connection (login form)");
            Connection.Response res3 = Jsoup.connect("https://registro.giua.edu.it/login/form/")
            	    .data("_username", username, "_password", password, "_csrf_token", CSRFToken, "login", "")
            	    .cookies(PHPSESSID)
                    .method(Method.POST)
            	    .execute();
            
            PHPSESSID = res3.cookies();
            System.out.printf("login: Cookie: %s\n", PHPSESSID);
            
            //Document doc2 = res3.parse();
            //print("HTML: " + doc2);
            
			
        } catch (IOException e) {
          e.printStackTrace();
        }
	}
	
	public static void print(String string) {
		System.out.println(string);
		//WHY THE FUCK IS THE PRINTING FUCTION SO LONG???
	}
	
	
	
	//Main function, only used on the console version for testing
    public static void main( String[] args )
    {
    	print("----FIRST LOGIN----\n");
    	login(user,password);
    	
    	print("------ ARE WE LOGGED IN? ----- ");
    	print(checkLogin().toString());
    	
    	print("----GET PAGE----\n");
    	getPage("https://registro.giua.edu.it/genitori/voti");
    	
    	print("------ ARE WE LOGGED IN? ----- ");
    	print(checkLogin().toString());
    	
    	
    	Document page = getPage("https://registro.giua.edu.it/genitori/voti");

    	print("Website title: " + page.title());
    	
    	
    	Elements table_div = page.getElementsByTag("tbody"); //Table
    	//print("-------HTML TABLE------\n\n" + table_div.toString() + "\n-----\n");
    	Elements riga_div = table_div.first().getElementsByTag("tr"); //sub-table
    	
    	
    	for (Element container : riga_div) {
    		String materia = container.getElementsByTag("strong").first().text(); //Get the subject
    		
    		Elements voti_div = container.getElementsByTag("button"); //Get number from button
    		Elements info_voti_div = container.getElementsByTag("div"); //Get information from the popup
    		String voti_materia = null;
    		//Integer i = 0;
    		String info = null;
    		
    		print("\n" + materia);
    		for(Element container_v : voti_div) {
    			voti_materia = container_v.text();
    			
    			for(Element container_iv : info_voti_div) {
        			info = container_iv.text();
        		}
    			
    			print("Voto: " + voti_materia + "\n" + info);
    		}
    		
    		
    		
    		/*
    		for(String[] voto : voti_materia[]) {
    			print(materia + " - voto: " + voto + "\n" + info);
    		}*/
    		
    		
    	}   
    }
    
}