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
import com.giua.utils.GiuaScraperUtils;
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
            final String quart = voteHTML.parent().parent().parent().parent().getElementsByTag("caption").get(0).text();

            if (voteAsString.length() > 0) {    //Gli asterischi sono caratteri vuoti
                if (returnVotes.containsKey(materiaName)) {            //Se la materia esiste gia aggiungo solamente il voto
                    List<Vote> tempList = returnVotes.get(materiaName); //uso questa variabile come appoggio per poter modificare la lista di voti di quella materia
                    tempList.add(new Vote(voteAsString, voteDate, type, args, judg, quart, false));
                } else {
                    returnVotes.put(materiaName, new Vector<Vote>() {{
                        add(new Vote(voteAsString, voteDate, type, args, judg, quart, false));    //il voto lo aggiungo direttamente
                    }});
                }
            } else {        //è un asterisco
                if (returnVotes.containsKey(materiaName)) {
                    returnVotes.get(materiaName).add(new Vote("", voteDate, type, args, judg, quart, true));
                } else {
                    returnVotes.put(materiaName, new Vector<>() {{
                        add(new Vote("", voteDate, type, args, judg, quart, true));
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

    /**
     * Ottiene tutti i voti di una materia specifica con una richiesta HTTP
     *
     * @param filterSubject ATTENZIONE: il nome deve essere identico a quello del registro nella sezione voti
     * @return Una {@code List} ordinata per quadrimestre contenente i voti di quel quadrimestre.
     * Per esempio per ottenere la {@code List} dei voti del secondo quadrimestre (se esiste): lista.get(1)
     */
    public List<List<Vote>> getAllVotes(String filterSubject) {
        Elements els = doc.getElementsByClass("dropdown-menu").get(3).children();
        Document votesDoc = null;

        for (Element el : els) {
            if (el.text().trim().equalsIgnoreCase(filterSubject.trim())) {
                votesDoc = gS.getPage(el.child(0).attr("href"));
            }
        }

        if (votesDoc != null) {
            Elements allQuartersHTML = votesDoc.getElementsByTag("tbody");
            if (allQuartersHTML.isEmpty())
                return new Vector<>();
            else {
                int nQuarter = allQuartersHTML.size();

                List<List<Vote>> allVotes = new Vector<>();
                for (int i = 0; i < nQuarter; i++) {
                    allVotes.add(new Vector<>());
                    for (Element el : allQuartersHTML.get(i).children()) {
                        allVotes.get(i).add(new Vote(
                                el.child(3).text(),
                                el.child(0).text(),
                                el.child(1).text(),
                                el.child(2).text(),
                                el.child(4).text(),
                                GiuaScraperUtils.getQuarterName(i + 1),
                                el.child(3).text().equals("")
                        ));
                    }
                }

                return allVotes;
            }

        } else
            return new Vector<>();
    }

    /**
     * Ottiene la media di tutti i voti di tutti i quadrimestri e li ritorna in un {@code float[]}
     *
     * @return la media dei voti dei quadrimestri in ordine. Quindi al primo indice (valore 0) si troverà
     * la media dei voti del primo quadrimestre, al secondo indice (valore 1) la media dei voti del secondo
     * quadrimestre e cosi via.
     */
    public float[] getAllQuartersMeans() {
        Map<String, List<Vote>> allVotes = getAllVotes();
        VariableArray allMeans = new VariableArray();
        VariableArray votesCounter = new VariableArray();

        for (String subject : allVotes.keySet()) {
            for (Vote vote : allVotes.get(subject)) {
                int index = vote.quarterlyToInt() - 1;

                float currentMean = allMeans.get(index);
                if (currentMean != -1f)
                    allMeans.set(index, currentMean + vote.toFloat());
                else
                    allMeans.set(index, vote.toFloat());

                float currentCounter = votesCounter.get(index);
                if (currentCounter != -1f)
                    votesCounter.set(index, currentCounter + 1);
                else
                    votesCounter.set(index, 1);
            }
        }

        for (int i = 0; i < allMeans.length; i++) {
            float currentMean = allMeans.get(i);
            if (currentMean != -1f)
                allMeans.set(i, currentMean / votesCounter.get(i));
            else
                allMeans.set(i, -1f);
        }

        return allMeans.array;
    }

    /**
     * Ottieni la media dei voti richiesti.
     *
     * @param votes {@code List} dei voti di cui fare la media
     * @return La media dei voti
     */
    public float getMeanOf(List<Vote> votes) {
        float mean = 0f;
        int counter = 0;

        for (Vote vote : votes) {
            mean += vote.toFloat();
            counter++;
        }

        return mean / counter;
    }

    /**
     * Ottieni la media dei voti richiesti SOLO del quadrimestre specificato
     *
     * @param votes     {@code List} dei voti di cui fare la media
     * @param quarterly il quadrimestre a cui fare riferimento
     * @return La media dei voti di quel quadrimestre. Ritorna -1f se non sono stati trovati
     * voti di quel quadrimestre
     */
    public float getMeanOf(List<Vote> votes, int quarterly) {
        float mean = 0f;
        int counter = 0;

        for (Vote vote : votes) {
            if (vote.quarterlyToInt() == quarterly) {
                mean += vote.toFloat();
                counter++;
            }
        }

        if (counter == 0)
            return -1f;

        return mean / counter;
    }
}

/**
 * Questa classe permette di avere un array variabile mantenendo comunque l' accesso,
 * sia in lettura che scrittura, dei singoli elementi come se si stesse utilizzando
 * un array normale
 */
class VariableArray {
    public float[] array;
    public int length;

    VariableArray() {
        array = new float[0];
        length = 0;
    }

    /**
     * Imposta un valore
     *
     * @param index l'indice in cui mettere il valore
     * @param value il valore
     */
    public void set(int index, float value) {
        if (index < array.length && index >= 0)
            array[index] = value;
        else if (index >= array.length) {
            addIndexes(index - array.length + 1);
            array[index] = value;
        }
    }

    /**
     * Ottiene il valore di una casella
     *
     * @param index l' indice a cui fare riferimento
     * @return il valore della casella in posizione {@code index}
     */
    public float get(int index) {
        if (index < array.length && index >= 0)
            return array[index];
        else
            return -1f;
    }

    /**
     * Aggiunge nuovi indici all' array e quindi lo ridimensiona
     *
     * @param indexes quanti nuovi indici aggiungere
     */
    private void addIndexes(int indexes) {
        float[] newArray = new float[array.length + indexes];
        length = array.length + indexes;

        for (int i = 0; i < array.length; i++) {
            newArray[i] = array[i];
        }

        array = newArray;
    }

}
