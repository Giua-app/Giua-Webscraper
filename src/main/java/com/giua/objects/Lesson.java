package com.giua.objects;

import com.giua.webscraper.GiuaScraper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.Vector;

public class Lesson {
    public final String date;
    public final String time;
    public final String subject;
    public final String arguments;
    public final String activities;

    public Lesson(String date, String time, String subject, String arguments, String activities){
        this.date = date;
        this.time = time;
        this.subject = subject;
        this.arguments = arguments;
        this.activities = activities;
    }

    public String toString(){
        return this.date + "; " + this.time + "; " + this.subject + "; " + this.arguments + "; " + this.activities;
    }

}
