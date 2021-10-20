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

import com.giua.objects.Vote;
import com.giua.webscraper.GiuaScraper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class VotesPage implements IPage {
    private GiuaScraper gS;
    private String filterSubject;
    private Document doc;

    public VotesPage(GiuaScraper gS) {
        this.gS = gS;
        refreshPage();
    }

    @Override
    public void refreshPage() {
        doc = gS.getPage(UrlPaths.VOTES_PAGE);
    }

    /**
     * Ottiene tutti i voti di tutte le materie. I voti per materia sono già in ordine di pubblicazione,
     * il primo è l' ultimo pubblicato
     */
    public Map<String, List<Vote>> getAllVotes() {
        Map<String, List<Vote>> returnVotes = new HashMap<>();
        Elements votesHTML = doc.getElementsByAttributeValue("title", "Informazioni sulla valutazione");

        for (final Element voteHTML : votesHTML) {
            assert voteHTML.parent() != null;
            assert voteHTML.parent().parent() != null;
            assert voteHTML.parent().parent().parent() != null;
            assert voteHTML.parent().parent().parent().parent() != null;

            final String voteAsString = voteHTML.text(); //prende il voto
            final String materiaName = voteHTML.parent().parent().child(0).text(); //prende il nome della materia
            final String voteDate = getDetailOfVote(voteHTML, 0);
            final String type = getDetailOfVote(voteHTML, 1);
            final String args = getDetailOfVote(voteHTML, 2);
            final String judg = getDetailOfVote(voteHTML, 3);
            final boolean isFirstQuart = voteHTML.parent().parent().parent().parent().getElementsByTag("caption").get(0).text().equals("Primo Quadrimestre");

            if (voteAsString.length() > 0) {    //Gli asterischi sono caratteri vuoti
                if (returnVotes.containsKey(materiaName)) {            //Se la materia esiste gia aggiungo solamente il voto
                    List<Vote> tempList = returnVotes.get(materiaName); //uso questa variabile come appoggio per poter modificare la lista di voti di quella materia
                    tempList.add(new Vote(voteAsString, voteDate, type, args, judg, isFirstQuart, false));
                } else {
                    returnVotes.put(materiaName, new Vector<Vote>() {{
                        add(new Vote(voteAsString, voteDate, type, args, judg, isFirstQuart, false));    //il voto lo aggiungo direttamente
                    }});
                }
            } else {        //è un asterisco
                if (returnVotes.containsKey(materiaName)) {
                    returnVotes.get(materiaName).add(new Vote("", voteDate, type, args, judg, isFirstQuart, true));
                } else {
                    returnVotes.put(materiaName, new Vector<>() {{
                        add(new Vote("", voteDate, type, args, judg, isFirstQuart, true));
                    }});
                }
            }
        }

        return returnVotes;
    }

    /**
     * Serve a gestire quei voti che non hanno alcuni dettagli
     *
     * @param e
     * @param index
     * @return Stringa contenente i dettagli di quel voto
     */
    private String getDetailOfVote(Element e, int index) {
        try {
            return e.siblingElements().get(e.elementSiblingIndex()).child(0).child(0).child(index).text().split(": ")[1];
        } catch (Exception err) {
            return "";
        }
    }

}
