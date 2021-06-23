package com.giua.objects;

import com.giua.webscraper.GiuaScraper;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.Serializable;

public class Absence implements Serializable {
    public String date;
    public String type;
    public String notes;
    public Boolean isJustified;
    public String justifyUrl;


    public Absence(String date, String type, String notes, Boolean isJustified, String justifyUrl) {
        this.date = date;
        this.type = type;
        this.notes = notes;
        this.isJustified = isJustified;
        this.justifyUrl = justifyUrl;
    }

    public String toString() {
        return this.date + " ; " + this.type + " ; " + this.notes + " ; Gia Giustificato? " + this.isJustified + " ; " + this.justifyUrl;
    }

}
