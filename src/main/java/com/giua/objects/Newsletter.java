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

    public Newsletter(String status, String numebr, String date, String newslettersObject, String detailsUrl, List<String> attachments, int id, int page) {
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

    public String toString(){
        return this.status + "; " + this.number + "; " + this.date + "; " + this.newslettersObject + "; " + this.detailsUrl + "; " + ((this.attachments != null) ? this.attachments.get(0) : "null");
    }
}
