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

import com.giua.objects.interviews.BookingOption;
import com.giua.objects.interviews.Interview;
import com.giua.objects.interviews.SentInterview;
import com.giua.webscraper.GiuaScraper;
import com.giua.webscraper.GiuaScraperExceptions;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

public class InterviewsPage implements IPage{
    private final GiuaScraper gS;
    private Document doc;

    public InterviewsPage(GiuaScraper gS) {
        this.gS = gS;
        refreshPage();
    }


    @Override
    public void refreshPage() {
        doc = gS.getPage(UrlPaths.INTERVIEWS_PAGE);
    }

    /**
     * Ottieni tutti i colloqui prenotabili
     *
     * @return
     */
    public List<Interview> getAllInterviews() {
        if (gS.getUserTypeEnum() != GiuaScraper.userTypes.PARENT)
            throw new GiuaScraperExceptions.UnsupportedAccount("Only PARENT account type can request interviews page");

        List<Interview> returnValue = new Vector<>();
        Elements allInterviewsHTML = doc.getElementsByClass("table table-bordered table-hover table-striped").get(2).children();
        for (Element el : allInterviewsHTML) {
            returnValue.add(new Interview(
                    el.child(0).text(),
                    el.child(1).text(),
                    el.child(2).text(),
                    el.child(3).attr("href")
            ));
        }

        return returnValue;
    }

    /**
     * Ottieni i colloqui prenotati
     */
    public List<SentInterview> getAllSentInterviews() {
        if (gS.getUserTypeEnum() != GiuaScraper.userTypes.PARENT)
            throw new GiuaScraperExceptions.UnsupportedAccount("Only PARENT account type can request interviews page");

        List<SentInterview> returnValue = new Vector<>();
        Elements allInterviewsHTML = doc.getElementsByClass("table table-bordered table-hover").get(2).children();
        for (Element el : allInterviewsHTML) {
            if (el.child(3).children().size() > 1)
                returnValue.add(new SentInterview(
                        el.child(0).text(),
                        el.child(1).text(),
                        el.child(2).text(),
                        el.child(3).text(),
                        el.child(3).child(1).attr("data-href")
                ));
        }

        return returnValue;
    }

    /**
     * Cancella un colloquio prenotato.
     * ATTENZIONE: utilizza una richiesta HTTP
     *
     * @param sentInterview il colloquio prenotato da cancellare
     */
    public void cancelBookedInterview(SentInterview sentInterview) {
        gS.getPage(sentInterview.cancelLink);
    }

    /**
     * Ottiene le opzioni per poter giustificare.
     * ATTENZIONE: utilizza una richiesta HTTP
     *
     * @param interview Il colloquio a cui fare riferimento
     * @return una lista delle opzioni
     */
    public List<BookingOption> getBookingOptions(Interview interview) {
        Document document = gS.getPage(interview.bookingLink);
        List<BookingOption> options = new Vector<>();
        Elements elements = document.getElementById("colloqui_prenota_data").children();

        for (Element el : elements) {
            options.add(new BookingOption(
                    el.text(),
                    el.child(0).child(0).attr("value")
            ));
        }

        return options;
    }

    /**
     * Prenota un colloquio
     *
     * @param interview             il colloquio da prenotare
     * @param selectedBookingOption l'opzione selezionata per poter prenotare il colloquio
     * @throws GiuaScraperExceptions.SiteConnectionProblems
     */
    public void bookInterview(Interview interview, BookingOption selectedBookingOption) throws GiuaScraperExceptions.SiteConnectionProblems {
        try {
            gS.getSession()
                    .url(GiuaScraper.getSiteURL() + interview.bookingLink)
                    .data("colloqui_prenota[data]", selectedBookingOption.value)
                    .data("colloqui_prenota[submit]", "")
                    .data("colloqui_prenota[_token]", getSubmitToken())
                    .post();
        } catch (IOException e) {
            if (!GiuaScraper.isSiteWorking()) {
                throw new GiuaScraperExceptions.SiteConnectionProblems("Can't get page because the website is down, retry later", e);
            }
        }
    }

    private String getSubmitToken() {
        return doc.getElementById("colloqui_prenota__token").attr("value");
    }
}