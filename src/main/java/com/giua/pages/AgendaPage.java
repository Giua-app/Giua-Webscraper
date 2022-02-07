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

import com.giua.objects.Activity;
import com.giua.objects.AgendaObject;
import com.giua.objects.Homework;
import com.giua.objects.Test;
import com.giua.objects.Meet;
import com.giua.utils.GiuaScraperUtils;
import com.giua.webscraper.GiuaScraper;
import com.giua.webscraper.GiuaScraperDemo;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.Vector;

public class AgendaPage implements IPage {
    private GiuaScraper gS;
    /**
     * Indica SOLO la prima pagina quindi quella del mese attuale
     */
    private Document doc;

    public AgendaPage(GiuaScraper gS) {
        this.gS = gS;
        refreshPage();
    }


    @Override
    public void refreshPage() {
        doc = gS.getPage(UrlPaths.PINBOARD_PAGE);
    }


    /**
     * Restituisce una lista di tutti i {@link Test} di una determinata data con anche i loro dettagli
     *
     * @param date Formato: anno-mese-giorno
     * @return Una lista di tutti i {@link Test} della data specificata se esiste, altrimenti una lista vuota
     */
    public List<Test> getTests(String date) {
        if (gS.isDemoMode())
            return GiuaScraperDemo.getTest(date);

        List<Test> allTests = new Vector<>();
        Document _doc = gS.getPage("genitori/eventi/dettagli/" + date+"/V");
        Elements testGroupsHTML = _doc.getElementsByClass("alert alert-info gs-mt-0 gs-mb-2 gs-pt-2 gs-pb-2 gs-pr-2 gs-pl-2");
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
     * Restituisce una lista di tutti i {@link Activity} di una determinata data con anche i loro dettagli
     *
     * @param date Formato: anno-mese-giorno
     * @return Una lista di tutti i {@link Activity} della data specificata se esiste, altrimenti una lista vuota
     */
    public List<Activity> getActivities(String date) {
        if (gS.isDemoMode())
            return GiuaScraperDemo.getActivity(date);

        List<Activity> allActivities = new Vector<>();
        Document _doc = gS.getPage("genitori/eventi/dettagli/" + date + "/A");
        Elements activityGroupsHTML = _doc.getElementsByClass("alert alert-info gs-mt-0 gs-mb-2 gs-pt-2 gs-pb-2 gs-pr-2 gs-pl-2");
        try {
            for (Element activityGroupHTML : activityGroupsHTML) {
                String creator = activityGroupHTML.child(1).text();
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
     * Restituisce una lista di tutti i {@link Meet} di una determinata data con anche i loro dettagli
     *
     * @param date Formato: anno-mese-giorno
     * @return Una lista di tutti i {@link Meet} della data specificata se esiste, altrimenti una lista vuota
     */
    public List<Meet> getMeets(String date) {
        if (gS.isDemoMode())
            return GiuaScraperDemo.getMeets();

        List<Meet> allMeets = new Vector<>();
        Document _doc = gS.getPage("genitori/eventi/dettagli/" + date + "/C");
        Elements meetGroupsHTML = _doc.getElementsByClass("modal-body");
        try {
            for (Element meetGroupHTML : meetGroupsHTML) {
                String creator = meetGroupHTML.child(0).getElementsByClass("gs-text-normal gs-big").text();
                String details = meetGroupHTML.child(0).getElementsByClass("gs-text-normal").get(2).text();
                String period=meetGroupHTML.child(0).getElementsByClass("gs-text-normal").get(1).text();
                allMeets.add(new Meet(
                        date.split("-")[2],
                        date.split("-")[1],
                        date.split("-")[0],
                        date,
                        period,
                        creator,
                        details,
                        true
                ));
            }

            return allMeets;
        } catch (IndexOutOfBoundsException e) {        //Non ci sono colloqui in questo giorno
            return new Vector<>();
        }
    }

    /**
     * Restituisce una lista di tutti gli {@link Homework} di una determinata data con anche i loro dettagli
     *
     * @param date Formato: anno-mese-giorno
     * @return Una lista di tutti gli {@link Homework} della data specificata se esiste, altrimenti una lista vuota
     */
    public List<Homework> getHomeworks(String date) {
        if (gS.isDemoMode())
            return GiuaScraperDemo.getHomework(date);

        List<Homework> allHomeworksInThisDate = new Vector<>();
        Document _doc = gS.getPage("genitori/eventi/dettagli/" + date + "/P");

        Elements homeworkGroupsHTML = _doc.getElementsByClass("alert alert-info gs-mt-0 gs-mb-2 gs-pt-2 gs-pb-2 gs-pr-2 gs-pl-2");
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

    /**
     * Ottiene tutti i {@link AgendaObject} del mese specificato. Se {@code date} è {@code null} ottieni quelli del mese attuale ma SENZA dettagli,
     * altrimenti quelli della data specificata.
     * ATTENZIONE: Utilizza un richiesta HTTP nel caso di date diverso da null
     *
     * @param date indica il mese e l'anno nel formato: anno-mese.
     *             Può essere {@code null} per indicare di prendere gli oggetti dal mese attuale
     * @return Lista di {@link AgendaObject} del mese specificato oppure del mese attuale
     */
    public List<AgendaObject> getAllAgendaObjectsWithoutDetails(String date) {
        if (gS.isDemoMode())
            return GiuaScraperDemo.getAllAgendaObjects();

        List<AgendaObject> allAgendaObjects = new Vector<>();
        Document doc = this.doc;
        if (date != null) {
            doc = gS.getPage("genitori/eventi/" + date);
        }

        //Include le attività, le verifiche e i colloqui
        Elements agendaObjectsHTML = doc.getElementsByClass("btn btn-xs btn-primary gs-button-remote");
        //Aggiungo i compiti
        agendaObjectsHTML.addAll(doc.getElementsByClass("btn btn-xs btn-default gs-button-remote"));

        for (Element agendaObjectHTML : agendaObjectsHTML) {
            AgendaObject agendaObject;
            String objectType = agendaObjectHTML.text();

            if (objectType.equals("Attività"))
                agendaObject = getActivityFromHTML(agendaObjectHTML);
            else if (objectType.equals("Verifiche"))
                agendaObject = getTestFromHTML(agendaObjectHTML);
            else if (objectType.equals("Compiti"))
                agendaObject = getHomeworkFromHTML(agendaObjectHTML);
            else if (objectType.equals("Colloqui"))
                agendaObject = getMeetFromHTML(agendaObjectHTML);
            else {
                String[] hrefSplit = agendaObjectHTML.attributes().get("data-href").split("/");
                String dateFromhref = hrefSplit[4];
                agendaObject = new AgendaObject(
                        dateFromhref.split("-")[2],
                        dateFromhref.split("-")[1],
                        dateFromhref.split("-")[0],
                        dateFromhref
                );
            }
            allAgendaObjects.add(agendaObject);

        }
        return allAgendaObjects;
    }

    private Meet getMeetFromHTML(Element meetHTML) {
        String url = meetHTML.attributes().get("data-href");
        String[] hrefSplit = GiuaScraperUtils.convertGlobalPathToLocal(url).split("/");
        String dateFromhref = hrefSplit[3];
        return new Meet(
                dateFromhref.split("-")[2],
                dateFromhref.split("-")[1],
                dateFromhref.split("-")[0],
                dateFromhref,
                "",
                "",
                "",
                true
        );
    }

    private Activity getActivityFromHTML(Element activityHTML) {
        String url = activityHTML.attributes().get("data-href");
        String[] hrefSplit = GiuaScraperUtils.convertGlobalPathToLocal(url).split("/");
        String dateFromhref = hrefSplit[3];
        return new Activity(
                dateFromhref.split("-")[2],
                dateFromhref.split("-")[1],
                dateFromhref.split("-")[0],
                dateFromhref,
                "",
                "",
                true
        );
    }

    private Test getTestFromHTML(Element testHTML) {
        String url = testHTML.attributes().get("data-href");
        String[] hrefSplit = GiuaScraperUtils.convertGlobalPathToLocal(url).split("/");
        String dateFromhref = hrefSplit[3];
        return new Test(
                dateFromhref.split("-")[2],
                dateFromhref.split("-")[1],
                dateFromhref.split("-")[0],
                dateFromhref,
                "",
                "",
                "",
                true
        );
    }

    private Homework getHomeworkFromHTML(Element homeworkHTML) {
        String url = homeworkHTML.attributes().get("data-href");
        String[] hrefSplit = GiuaScraperUtils.convertGlobalPathToLocal(url).split("/");
        String dateFromhref = hrefSplit[3];
        return new Homework(
                dateFromhref.split("-")[2],
                dateFromhref.split("-")[1],
                dateFromhref.split("-")[0],
                dateFromhref,
                "",
                "",
                "",
                true
        );
    }
}