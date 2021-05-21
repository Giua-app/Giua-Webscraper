package com.giua.objects;

import com.giua.webscraper.GiuaScraper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public class Alert {
    public final String status;
    public final String date;
    public final String receivers;
    public final String objectAvviso;
    public String details;
    public String creator;
    public final int page;
    public final int id;        //Indica quanto e' lontano dal primo avviso partendo dall'alto
    public boolean isDetailed;

    Alert(String status, String data, String destinatari, String oggetto, int id, int page) {
        this.status = status;
        this.date = data;
        this.receivers = destinatari;
        this.objectAvviso = oggetto;
        this.id = id;
        this.page = page;
        this.isDetailed = false;
    }

    public String getDetails(GiuaScraper gS) {        //carica i dettagli e l'autore dell'avviso simulando il click su Visualizza
        Document allAvvisiHTML = gS.getPage(GiuaScraper.SiteURL + "/genitori/avvisi/" + page);
        Document dettagliAvvisoHTML = gS.getPage(GiuaScraper.SiteURL + "" + allAvvisiHTML.getElementsByClass("label label-default").get(this.id).parent().parent().child(4).child(0).attributes().get("data-href"));
        this.details = dettagliAvvisoHTML.getElementsByClass("gs-text-normal").get(0).text();
        this.creator = dettagliAvvisoHTML.getElementsByClass("text-right gs-text-normal").get(0).text();
        this.isDetailed = true;
        return this.details;
    }

    public boolean isRead() {
        return this.status.equals("LETTA");
    }

    //Ritorna una lista di Avvisi con tutti i loro componenti ma senza i dettagli
    public static List<Alert> getAllAvvisi(int page, GiuaScraper gS) {
        if(page < 0){throw new IndexOutOfBoundsException("Un indice di pagina non puo essere 0 o negativo");}
        List<Alert> allAvvisi = new Vector<Alert>();
        Document doc = gS.getPage(GiuaScraper.SiteURL + "/genitori/avvisi/" + page);
        Elements allAvvisiLettiStatusHTML = doc.getElementsByClass("label label-default");
        Elements allAvvisiDaLeggereStatusHTML = doc.getElementsByClass("label label-warning");

        int i = 0;
        for (Element el : allAvvisiLettiStatusHTML) {
            allAvvisi.add(new Alert(el.text(),
                    el.parent().parent().child(1).text(),
                    el.parent().parent().child(2).text(),
                    el.parent().parent().child(3).text(),
                    i,
                    page
            ));
            i++;
        }
        for (Element el : allAvvisiDaLeggereStatusHTML) {
            allAvvisi.add(new Alert(el.text(),
                    el.parent().parent().child(1).text(),
                    el.parent().parent().child(2).text(),
                    el.parent().parent().child(3).text(),
                    i,
                    page
            ));
            i++;
        }

        return allAvvisi;
    }

    public String toString() {
        if (!this.isDetailed)
            return this.status + "; " + this.date + "; " + this.receivers + "; " + this.objectAvviso;
        else
            return this.status + "; " + this.date + "; " + this.receivers + "; " + this.objectAvviso + "; " + this.creator + "; " + this.details;
    }
}
