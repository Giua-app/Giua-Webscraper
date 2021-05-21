package com.giua.objects;

import com.giua.webscraper.GiuaScraper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public class Test{
    public final String day;		//usato per trovare quale verifica interessa
    public final String date;
    public final String subject;
    public final String creator;
    public final String details;
    public final boolean exists;

    Test(String day, String date, String subject, String creator, String details, boolean exists){
        this.day = day;
        this.date = date;
        this.subject = subject;
        this.creator = creator;
        this.details = details;
        this.exists = exists;
    }

    public String toString() {
        return this.date + "; " + this.creator + "; " + this.subject + "; " + this.details + "; " + this.exists;
    }

    public static List<Test> getAllTestsWithoutDetails(GiuaScraper gS){
        List<Test> allTests = new Vector<>();
        Document doc = gS.getPage(GiuaScraper.SiteURL + "/genitori/eventi");
        Elements testsHTML = doc.getElementsByClass("btn btn-xs btn-primary gs-button-remote");
        for(Element testHTML: testsHTML){

            allTests.add(new Test(
                    testHTML.parent().parent().text(),
                    testHTML.attributes().get("data-href").split("/")[4],
                    "",
                    "",
                    "",
                    true
            ));
        }

        return allTests;
    }

    public static List<Test> getAllTests(GiuaScraper gS){		//Se ci sono molti elementi e quindi link potrebbe dare connection timed out.
        //Meglio utilizzare prima quello senza dettagli e poi andare a prendere la verifica singolarmente con getTest
        List<Test> allTests = new Vector<>();
        Document doc = gS.getPage(GiuaScraper.SiteURL + "/genitori/eventi");
        Elements testsHTML = doc.getElementsByClass("btn btn-xs btn-primary gs-button-remote");
        for(Element testHTML: testsHTML){
            Document detailsHTML = gS.getPage(GiuaScraper.SiteURL + "" + testHTML.attributes().get("data-href"));
            String subject = detailsHTML.getElementsByClass("gs-text-normal").get(0).text().split(": ")[1];
            String creator = detailsHTML.getElementsByClass("gs-text-normal").get(1).text().split(": ")[1];
            String details = detailsHTML.getElementsByClass("gs-text-normal gs-pt-3 gs-pb-3").get(0).text();

            allTests.add(new Test(
                    testHTML.parent().parent().text(),
                    testHTML.attributes().get("data-href").split("/")[4],
                    subject,
                    creator,
                    details,
                    true
            ));
        }

        return allTests;
    }

    public static Test EmptyTest(String date){
        return new Test(
                date.split("-")[2],
                date,
                "",
                "",
                "No verifiche",
                false
        );
    }

    //Restituisce il compito di una determinata data. Data deve essere cosi: anno-mese-giorno
    public static Test getTest(String date, GiuaScraper gS){
        Document doc = gS.getPage(GiuaScraper.SiteURL + "/genitori/eventi/dettagli/" + date + "/V");
        try {
            String subject = doc.getElementsByClass("gs-text-normal").get(0).text().split(": ")[1];
            String creator = doc.getElementsByClass("gs-text-normal").get(1).text().split(": ")[1];
            String details = doc.getElementsByClass("gs-text-normal gs-pt-3 gs-pb-3").get(0).text();

            return new Test(
                    date.split("-")[2],
                    date,
                    subject,
                    creator,
                    details,
                    true
            );
        } catch (IndexOutOfBoundsException e){		//Non ci sono verifiche in questo giorno
            return EmptyTest(date);
        }
    }
}
