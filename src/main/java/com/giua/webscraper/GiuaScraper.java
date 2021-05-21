package com.giua.webscraper;

import com.giua.objects.*;

import java.io.IOException;
import java.util.*;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/* -- Giua Webscraper alpha 0.6.x -- */
// Tested with version 1.2.x and 1.3.0 of giua@school
public class GiuaScraper extends GiuaScraperExceptions
{
	// Insert user and password of the account
	private String user = "";

	public void setUser(String u){
		user = u;
	}

	private String password = "";

	public void setPassword(String p){
		password = p;
	}

	//URL del registro
	public static final String SiteURL = "https://registro.giua.edu.it";

	private Map<String, String> PHPSESSID = null; //TODO: modificare la variabile in modo che contenga solo il cookie che ci interessa e non tutti
	private String CSRFToken = null;

	public GiuaScraper(String user, String password){
		this.user = user;
		this.password = password;
	}

	public Document getPage(String url) {
		try {

			if(!checkLogin()) {
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
			// Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Boolean checkLogin() {
		try {

			if(PHPSESSID == null) {
				return false; //Not logged in
			}

			Connection.Response res = Jsoup.connect(SiteURL + "/login/form/")
				    .method(Method.POST)
				    .cookies(PHPSESSID)
				    .execute();

			Document doc = res.parse();

			Elements logout_button = doc.getElementsByAttributeValue("title", "Esci dal Registro Elettronico");

			//The login screen is the most simple to scrape information from, in this case we search
			//for the log out button, if it doesn't exist we are not logged in.
			// Since we have already loaded the login page, this process is very fast
			return logout_button.size() > 0; //ritorna true se sei loggato altrimenti false


		} catch (IOException e) {
			// Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}


	//The most important function, it handles the login process
	public void login(String username, String password)
	{
		try {

			//print("login: First connection (login form)");

			Connection.Response res = Jsoup.connect(SiteURL + "/login/form")
            	    .method(Method.GET)
            	    .execute();

            	//Document doc = res.parse();
            	PHPSESSID = res.cookies();



			//print("login: Second connection (authenticate)");
			Connection.Response res2 = Jsoup.connect(SiteURL + "/ajax/token/authenticate")
					.cookies(PHPSESSID)
					.method(Method.GET)
					.ignoreContentType(true)
					.execute();

			print("login: get csrf token");


			String csrfString = res2.body().split("\":\"")[1];
			CSRFToken = csrfString.substring(0, csrfString.length()-2);		//prende solo il valore del csrf

			//print("Page content: " + res2.body());
			print("login: CSRF Token: " + CSRFToken);

			//print("login: Third connection (login form)");
			Connection.Response res3 = Jsoup.connect(SiteURL + "/login/form/")
					.data("_username", username, "_password", password, "_csrf_token", CSRFToken, "login", "")
					.cookies(PHPSESSID)
					.method(Method.POST)
					.execute();

			PHPSESSID = res3.cookies();
			System.out.printf("login: Cookie: %s\n", PHPSESSID);

			Document doc2 = res3.parse();


			if(PHPSESSID.isEmpty()){
				Elements err = doc2.getElementsByClass("alert alert-danger"); //prendi errore dal sito
				throw new SessionCookieEmpty("Session cookie empty, login unsuccessful. Site says: " + err.text());
			}

			print("login: Logged in as " + username + " with account type " + getUserType(doc2));


			//print("HTML: " + doc2);


		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void print(String string) {
		System.out.println(string);
		//WHY THE FUCK IS THE PRINTING FUCTION SO LONG???
	}


	public static String getUserType(Document doc){
		//final Document doc = getPage(SiteURL + "/");
		//TODO: Forse è un pò troppo eccessivo caricare una pagina ogni volta che si vuole il tipo di account
		//TODO: quindi per ora lo lascio commentato
		final Elements elm = doc.getElementsByClass("col-sm-5 col-xs-8 text-right");
		String text = elm.get(0).getElementsByTag("a").text();
		final String[] text2 = text.split("\\(");
		text = text2[1].replaceAll("\\)", "");
		return text;
	}
}