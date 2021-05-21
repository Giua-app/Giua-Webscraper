package com.giua.objects;

import com.giua.webscraper.GiuaScraper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public class Homework{
    public String day;		//usato per trovare quale compito interessa
    public String date;
    public String subject;
    public String creator;
    public String details;

    Homework(String day, String date, String subject, String creator, String details){
        this.day = day;
        this.date = date;
        this.subject = subject;
        this.creator = creator;
        this.details = details;
    }

    public String toString() {
        return this.date + "; " + this.creator + "; " + this.subject + "; " + this.details;
    }

    public static List<Homework> getAllHomeworks(GiuaScraper gS){
        List<Homework> allHomeworks = new Vector<>();
        Document doc = gS.getPage(GiuaScraper.SiteURL + "/genitori/eventi");
        Elements homeworksHTML = doc.getElementsByClass("btn btn-xs btn-default gs-button-remote");
        for(Element homeworkHTML: homeworksHTML){
            Document detailsHTML = gS.getPage(GiuaScraper.SiteURL + "" + homeworkHTML.attributes().get("data-href"));
            String subject = detailsHTML.getElementsByClass("gs-big").get(0).text();
            String creator = detailsHTML.getElementsByClass("gs-text-normal").get(1).text().split(": ")[1];
            String details = detailsHTML.getElementsByClass("gs-text-normal gs-pt-3 gs-pb-3").get(0).text();

            allHomeworks.add(new Homework(
                    homeworkHTML.parent().parent().text(),
                    homeworkHTML.attributes().get("data-href").split("/")[4],
                    subject,
                    creator,
                    details
            ));
        }

        return allHomeworks;
    }

    public static Homework EmptyHomework(String date){
        return new Homework(
                date.split("-")[2],
                date,
                "",
                "",
                "No compiti"
        );
    }

    //Restituisce il compito di una determinata data. Data deve essere cosi: anno-mese-giorno
    public static Homework getHomework(String date, GiuaScraper gS){
        Document doc = gS.getPage(GiuaScraper.SiteURL + "/genitori/eventi/dettagli/" + date + "/P");
        try {
            String subject = doc.getElementsByClass("gs-big").get(0).text();
            String creator = doc.getElementsByClass("gs-text-normal").get(1).text().split(": ")[1];
            String details = doc.getElementsByClass("gs-text-normal gs-pt-3 gs-pb-3").get(0).text();

            return new Homework(
                    date.split("-")[2],
                    date,
                    subject,
                    creator,
                    details
            );
        } catch (NullPointerException e){		//Non ci sono compiti in questo giorno
            return EmptyHomework(date);
        }
    }
}