/*
 * Giua Webscraper library
 * A webscraper of the online school workbook giua@school
 * Copyright (C) 2021 - 2021 Hiem, Franck1421 and contributors
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

import com.giua.objects.Activity;
import com.giua.objects.Homework;
import com.giua.objects.Test;
import com.giua.webscraper.GiuaScraper;
import com.giua.webscraper.GiuaScraperDemo;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.Vector;

public class PinBoardPage implements IPage{
    private GiuaScraper gS;
    /**
     * Indica SOLO la prima pagina quindi quella del mese attuale
     */
    private Document doc;

    public PinBoardPage(GiuaScraper gS) {
        this.gS = gS;
        refreshPage();
    }


    @Override
    public void refreshPage() {
        doc = gS.getPage(UrlPaths.PINBOARD_PAGE);
    }


    /**
     * Ottiene tutti i {@link Test} del mese specificato. Se {@code date} è {@code null} ottieni quelli del mese attuale ma SENZA dettagli,
     * altrimenti quelli della data specificata.
     *
     * @param date         puo essere {@code null}. Formato: anno-mese
     * @return Lista di {@link Test} del mese specificato oppure del mese attuale
     */
    public List<Test> getAllTestsWithoutDetails(String date) {
        if (gS.isDemoMode())
            return GiuaScraperDemo.getAllTestsWithoutDetails();

        List<Test> allTests = new Vector<>();
        Document doc = this.doc;
        if (date != null) {
            doc = gS.getPage("genitori/eventi/" + date);
        }

        Elements testsHTML = doc.getElementsByClass("btn btn-xs btn-primary gs-button-remote");
        for (Element testHTML : testsHTML) {
            if (!testHTML.text().equals("Verifiche"))
                continue;
            assert testHTML.parent() != null;
            assert testHTML.parent().parent() != null;
            String[] hrefSplit = testHTML.attributes().get("data-href").split("/");
            String dateFromhref = hrefSplit[4];
            allTests.add(new Test(
                    dateFromhref.split("-")[2],
                    dateFromhref.split("-")[1],
                    dateFromhref.split("-")[0],
                    dateFromhref,
                    "",
                    "",
                    "",
                    true
            ));
        }
        return allTests;
    }


    /**
     * Restituisce una lista di tutti i {@link Test} di una determinata data con anche i loro dettagli
     *
     * @param date Formato: anno-mese-giorno
     * @return Una lista di tutti i {@link Test} della data specificata se esiste, altrimenti una lista vuota
     */
    public List<Test> getTest(String date) {
        if (gS.isDemoMode())
            return GiuaScraperDemo.getTest(date);

        List<Test> allTests = new Vector<>();
        if (!this.doc.baseUri().equals(GiuaScraper.getSiteURL() + "genitori/eventi/dettagli/" + date + "/V")) {
            doc = gS.getPage("genitori/eventi/dettagli/" + date + "/V");
        }
        Elements testGroupsHTML = doc.getElementsByClass("alert alert-info gs-mt-0 gs-mb-2 gs-pt-2 gs-pb-2 gs-pr-2 gs-pl-2");
        try {
            for (Element testGroupHTML : testGroupsHTML) {
                String subject = testGroupHTML.child(0).text().split(": ")[1];
                String creator = testGroupHTML.child(1).text().split(": ")[1];
                String details = testGroupHTML.child(2).text();

                allTests.add(new Test(
                        date.split("-")[2],
                        date.split("-")[1],
                        date.split("-")[0],
                        date,
                        subject,
                        creator,
                        details,
                        true
                ));
            }

            return allTests;
        } catch (IndexOutOfBoundsException e) {        //Non ci sono verifiche in questo giorno
            return new Vector<>();
        }
    }

    /**
     * Ottiene tutti i {@link com.giua.objects.Activity} del mese specificato. Se {@code date} è {@code null} ottieni quelli del mese attuale ma SENZA dettagli,
     * altrimenti quelli della data specificata.
     *
     * @param date puo essere {@code null}. Formato: anno-mese
     * @return Lista di {@link Test} del mese specificato oppure del mese attuale
     */
    public List<Activity> getAllActivitiesWithoutDetails(String date) {
        if (gS.isDemoMode())
            return GiuaScraperDemo.getAllActivitiesWithoutDetails();

        List<Activity> allActivities = new Vector<>();
        Document doc = this.doc;
        if (date != null) {
            doc = gS.getPage("genitori/eventi/" + date);
        }

        Elements activitiesHTML = doc.getElementsByClass("btn btn-xs btn-primary gs-button-remote");
        for (Element activityHTML : activitiesHTML) {
            if (!activityHTML.text().equals("Attività"))
                continue;
            assert activityHTML.parent() != null;
            assert activityHTML.parent().parent() != null;
            String[] hrefSplit = activityHTML.attributes().get("data-href").split("/");
            String dateFromhref = hrefSplit[4];
            allActivities.add(new Activity(
                    dateFromhref.split("-")[2],
                    dateFromhref.split("-")[1],
                    dateFromhref.split("-")[0],
                    dateFromhref,
                    "",
                    "",
                    true
            ));
        }
        return allActivities;
    }


    /**
     * Restituisce una lista di tutti i {@link Activity} di una determinata data con anche i loro dettagli
     *
     * @param date Formato: anno-mese-giorno
     * @return Una lista di tutti i {@link Activity} della data specificata se esiste, altrimenti una lista vuota
     */
    public List<Activity> getActivity(String date) {
        if (gS.isDemoMode())
            return GiuaScraperDemo.getActivity(date);

        List<Activity> allActivities = new Vector<>();
        Document doc = gS.getPage("genitori/eventi/dettagli/" + date + "/A");
        Elements activityGroupsHTML = doc.getElementsByClass("alert alert-info gs-mt-0 gs-mb-2 gs-pt-2 gs-pb-2 gs-pr-2 gs-pl-2");
        try {
            for (Element activityGroupHTML : activityGroupsHTML) {
                String creator = activityGroupHTML.child(1).text().split(": ")[1];
                String details = activityGroupHTML.child(0).text();

                allActivities.add(new Activity(
                        date.split("-")[2],
                        date.split("-")[1],
                        date.split("-")[0],
                        date,
                        creator,
                        details,
                        true
                ));
            }

            return allActivities;
        } catch (IndexOutOfBoundsException e) {        //Non ci sono verifiche in questo giorno
            return new Vector<>();
        }
    }


    /**
     * Ottiene tutti i {@link Homework} del mese specificato. Se {@code date} è {@code null} ottieni quelli del mese attuale ma SENZA dettagli,
     * altrimenti quelli della data specificata.
     * Serve solo a capire in quali giorni ci sono compiti.
     *
     * @param date puo essere {@code null}. Formato: anno-mese
     * @return Lista di Homework del mese specificato oppure del mese attuale
     */
    public List<Homework> getAllHomeworksWithoutDetails(String date) {
        if (gS.isDemoMode())
            return GiuaScraperDemo.getAllHomeworksWithoutDetails();

        List<Homework> allHomeworks = new Vector<>();
        Document doc = this.doc;
        if (date != null) {
            doc = gS.getPage("genitori/eventi/" + date);
        }

        Elements homeworksHTML = doc.getElementsByClass("btn btn-xs btn-default gs-button-remote");
        for (Element homeworkHTML : homeworksHTML) {
            assert homeworkHTML.parent() != null;
            assert homeworkHTML.parent().parent() != null;
            String[] hrefSplit = homeworkHTML.attributes().get("data-href").split("/");
            String dateFromhref = hrefSplit[4];
            allHomeworks.add(new Homework(
                    dateFromhref.split("-")[2],
                    dateFromhref.split("-")[1],
                    dateFromhref.split("-")[0],
                    dateFromhref,
                    "",
                    "",
                    "",
                    true
            ));
        }

        return allHomeworks;
    }

    /**
     * Restituisce una lista di tutti gli {@link Homework} di una determinata data con anche i loro dettagli
     *
     * @param date Formato: anno-mese-giorno
     * @return Una lista di tutti gli {@link Homework} della data specificata se esiste, altrimenti una lista vuota
     */
    public List<Homework> getHomework(String date) {
        if (gS.isDemoMode())
            return GiuaScraperDemo.getHomework(date);

        List<Homework> allHomeworksInThisDate = new Vector<>();
        if (!this.doc.baseUri().equals(GiuaScraper.getSiteURL() + "genitori/eventi/dettagli/" + date + "/P")) {
            doc = gS.getPage("genitori/eventi/dettagli/" + date + "/P");
        }

        Elements homeworkGroupsHTML = doc.getElementsByClass("alert alert-info gs-mt-0 gs-mb-2 gs-pt-2 gs-pb-2 gs-pr-2 gs-pl-2");
        try {
            for (Element homeworkGroupHTML : homeworkGroupsHTML) {
                String subject = homeworkGroupHTML.child(0).text();
                String creator = homeworkGroupHTML.child(1).text().split(": ")[1];
                String details = homeworkGroupHTML.child(2).text();

                allHomeworksInThisDate.add(new Homework(
                        date.split("-")[2],
                        date.split("-")[1],
                        date.split("-")[0],
                        date,
                        subject,
                        creator,
                        details,
                        true
                ));
            }

            return allHomeworksInThisDate;
        } catch (NullPointerException | IndexOutOfBoundsException e) {        //Non ci sono compiti in questo giorno
            return new Vector<>();
        }
    }


}