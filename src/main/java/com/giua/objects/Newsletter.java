package com.giua.objects;

import com.giua.webscraper.GiuaScraper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public class Newsletter {

    public final String status;
    public final String date;
    public final String newslettersObject;
    public final String detailsUrl;
    public final String number;
    public final List<String> attachments;
    public final int page;
    public final int id;		//Indica quanto e' lontano dalla prima circolare

    Newsletter(String status, String numebr, String date, String newslettersObject, String detailsUrl, List<String> attachments, int id, int page) {
        this.status = status;
        this.date = date;
        this.newslettersObject = newslettersObject;
        this.detailsUrl = detailsUrl;
        this.number = numebr;
        this.attachments = attachments;
        this.id = id;
        this.page = page;
    }

    public boolean isRead(){
        return this.status.equals("LETTA");
    }

    private static List<String> attachmentsUrls(Element el){
        Elements els = el.parent().siblingElements().get(3).child(1).children();
        List<String> r = new Vector<>();
        if(els.size() > 2){     //Ci sono allegati
            Elements allAttachmentsHTML = els.get(1).child(0).children();

            for(Element attachment: allAttachmentsHTML){
                r.add(GiuaScraper.SiteURL + attachment.child(1).attr("href"));
            }
        } else {        //Non ha allegati
            return null;
        }

        return r;
    }

    //Ritorna una lista di Newsletter con tutti i loro componenti di una determinata pagina
    public static List<Newsletter> getAllNewsletters(int page, GiuaScraper gS) {
        List<Newsletter> allCirculars = new Vector<>();
        Document doc = gS.getPage(GiuaScraper.SiteURL + "/circolari/genitori/" + page);
        Elements allNewslettersLettiStatusHTML = doc.getElementsByClass("label label-default");
        Elements allNewslettersDaLeggereStatusHTML = doc.getElementsByClass("label label-warning");

        int i = 0;
        for (Element el : allNewslettersLettiStatusHTML) {
            allCirculars.add(new Newsletter(el.text(),
                    el.parent().parent().child(1).text(),
                    el.parent().parent().child(2).text(),
                    el.parent().parent().child(3).text(),
                    GiuaScraper.SiteURL + "" + el.parent().parent().child(4).child(1).child(0).child(0).child(0).getElementsByClass("btn btn-xs btn-primary gs-ml-3").get(0).attr("href"),
                    attachmentsUrls(el),
                    i,
                    page
            ));
            i++;
        }
        for (Element el : allNewslettersDaLeggereStatusHTML) {
            allCirculars.add(new Newsletter(el.text(),
                    el.parent().parent().child(1).text(),
                    el.parent().parent().child(2).text(),
                    el.parent().parent().child(3).text(),
                    GiuaScraper.SiteURL + "" + el.parent().parent().child(4).child(1).child(0).child(0).child(0).getElementsByClass("btn btn-xs btn-primary gs-ml-3").get(0).attr("href"),
                    attachmentsUrls(el),
                    i,
                    page
            ));
            i++;
        }

        return allCirculars;
    }

    public String toString(){
        return this.status + "; " + this.number + "; " + this.date + "; " + this.newslettersObject + "; " + this.detailsUrl + "; " + ((this.attachments != null) ? this.attachments.get(0) : "null");
    }
}
