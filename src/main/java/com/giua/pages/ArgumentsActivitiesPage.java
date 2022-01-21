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
import com.giua.utils.GiuaScraperUtils;
import com.giua.webscraper.GiuaScraper;
import com.giua.webscraper.GiuaScraperExceptions;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class ArgumentsActivitiesPage implements IPage{
    private GiuaScraper gS;
    private Document doc;

    public ArgumentsActivitiesPage(GiuaScraper gS) {
        this.gS = gS;
        refreshPage();
    }


    @Override
    public void refreshPage() {
        doc = gS.getPage(UrlPaths.ARGUMENTS_ACTIVITIES_PAGE);
    }

    /**
     * Ottiene tutte le lezioni di una determinata materia
     *
     * @param subjectName Il nome della materia che corrisponda con i nomi del sito
     * @return Una List delle {@link Lesson} di una determinata materia
     */
    public List<Lesson> getAllLessonsOfSubject(String subjectName) {
        List<Lesson> returnLesson = new Vector<>();
        boolean foundSubject = false;

        try {
            Elements allSubjectsHTML = doc.getElementsByAttributeValue("aria-labelledby", "gs-dropdown-menu").get(0).children();

            for (Element subjectHTML : allSubjectsHTML) {
                if (subjectHTML.text().equals(subjectName)) {
                    doc = gS.getPage(subjectHTML.child(0).attr("href").substring(1));
                    foundSubject = true;
                    break;
                }
            }

            if (!foundSubject) {
                throw new GiuaScraperExceptions.SubjectNameInvalid("Subject " + subjectName + " not found in genitori/argomenti");
            }

            Elements allLessonsHTML = doc.getElementsByTag("tbody");

            for (Element element : allLessonsHTML) {
                Elements lessonsHTML = element.children();
                for (Element lessonHTML : lessonsHTML) {
                    String rawHref = GiuaScraperUtils.convertGlobalPathToLocal(lessonHTML.child(0).child(0).attr("href"));
                    Date date;
                    if (!rawHref.equals("")) {   //Lezione normale
                        date = Lesson.dateFormat.parse(rawHref.substring(18, 28));
                        returnLesson.add(new Lesson(
                                date,
                                "",
                                subjectName,
                                lessonHTML.child(1).text(),
                                lessonHTML.child(2).text(),
                                true
                        ));
                    } else {    //Lezione presente nello stesso giorno della precedente
                        date = returnLesson.get(returnLesson.size() - 1).date;
                        returnLesson.add(new Lesson(
                                date,
                                "",
                                subjectName,
                                lessonHTML.child(0).text(),
                                lessonHTML.child(1).text(),
                                true
                        ));
                    }
                }
            }
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            returnLesson.add(new Lesson(new Date(), "", subjectName, "", "", false));
        } catch (ParseException e) {
            throw new IllegalArgumentException("Impossibile fare parsing della stringa per una data", e);
        }

        return returnLesson;
    }
}