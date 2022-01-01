/*
 * Giua Webscraper library
 * A webscraper of the online school workbook giua@school
 * Copyright (C) 2021 - 2022 Hiem, Franck1421 and contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see https://www.gnu.org/licenses/.
 */

package com.giua.pages;

import com.giua.objects.Lesson;
import com.giua.webscraper.GiuaScraper;
import com.giua.webscraper.GiuaScraperDemo;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class LessonsPage implements IPage{
    private GiuaScraper gS;
    private Document doc;

    public LessonsPage(GiuaScraper gS) {
        this.gS = gS;
        refreshPage();
    }


    @Override
    public void refreshPage() {
        doc = gS.getPage(UrlPaths.LESSONS_PAGE);
    }

    /**
     * Ottiene tutte le lezioni di un dato giorno
     *
     * @param date Formato: anno-mese-giorno
     * @return Una List delle {@link Lesson} di un dato giorno
     */
    public List<Lesson> getAllLessonsFromDate(String date) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");

        Date parseDate;
        try {
            parseDate = format1.parse(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Impossibile fare parsing della stringa per una data", e);
        }

        return getAllLessonsFromDate(parseDate);
    }

    /**
     * Ottiene tutte le lezioni di un dato giorno
     *
     * @param pDate {@link Date} del giorno
     * @return Una List delle {@link Lesson} di un dato giorno
     */
    public List<Lesson> getAllLessonsFromDate(Date pDate) {
        if (gS.isDemoMode())
            return GiuaScraperDemo.getAllLessons();
        String date = Lesson.dateFormat.format(pDate);

        if (!this.doc.baseUri().equals(GiuaScraper.getSiteURL() + "genitori/lezioni/" + date)) {
            doc = gS.getPage("genitori/lezioni/" + date);
        }
        List<Lesson> returnLesson = new Vector<>();

        try {
            Elements allLessonsHTML = doc.getElementsByTag("tbody").get(0).children();

            for (Element lessonHTML : allLessonsHTML) {
                returnLesson.add(new Lesson(
                        pDate,
                        lessonHTML.child(0).text(),
                        lessonHTML.child(1).text(),
                        lessonHTML.child(2).text(),
                        lessonHTML.child(3).text(),
                        true
                ));
            }
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            if(!doc.getElementsByClass("alert alert-warning").isEmpty())
                returnLesson.add(new Lesson(pDate, "", "",
                        doc.getElementsByClass("alert alert-warning").first().text(), "", false, true));

            else
                returnLesson.add(new Lesson(pDate, "", "", "", "", false));
        }

        return returnLesson;
    }
}