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

    Lesson(String date, String time, String subject, String arguments, String activities){
        this.date = date;
        this.time = time;
        this.subject = subject;
        this.arguments = arguments;
        this.activities = activities;
    }

    //Ritorna le lezioni di un dato giorno
    public static List<Lesson> getLesson(String date, GiuaScraper gS){ //date deve essere tipo: 2021-05-21
        Document doc = gS.getPage(GiuaScraper.SiteURL + "/genitori/lezioni/" + date);
        List<Lesson> returnLesson = new Vector<>();

        Elements allLessonsHTML = doc.getElementsByTag("tbody").get(0).children();

        for(Element lessonHTML: allLessonsHTML){
            returnLesson.add(new Lesson(
                    date,
                    lessonHTML.child(0).text(),
                    lessonHTML.child(1).text(),
                    lessonHTML.child(2).text(),
                    lessonHTML.child(3).text()
            ));
        }

        return returnLesson;
    }

    public String toString(){
        return this.date + "; " + this.time + "; " + this.subject + "; " + this.arguments + "; " + this.activities;
    }

}
