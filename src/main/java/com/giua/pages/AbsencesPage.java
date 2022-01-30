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

import com.giua.objects.Absence;
import com.giua.utils.GiuaScraperUtils;
import com.giua.webscraper.GiuaScraper;
import com.giua.webscraper.GiuaScraperDemo;
import com.giua.webscraper.GiuaScraperExceptions;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.Vector;

public class AbsencesPage implements IPage {
    private GiuaScraper gS;
    private Document doc;
    private Element tbodyGlobalSituation;

    public AbsencesPage(GiuaScraper gS) {
        this.gS = gS;
        refreshPage();
    }


    @Override
    public void refreshPage() {
        doc = gS.getPage(UrlPaths.ABSENCES_PAGE);
        tbodyGlobalSituation = doc.getElementById("gs-giustificazioni").previousElementSibling().child(1);
    }

    /**
     * Permette di giustificare una assenza da un account genitore.
     *
     * @param ab     l'assenza da giustificare
     * @param type   il tipo di assenza, può anche essere vuoto (""):
     *               1 - Motivi di salute;
     *               2 - Esigenze di famiglia;
     *               3 - Problemi di trasporto;
     *               4 - Attività sportiva;
     *               5 - Problemi di connessione nella modalità a distanza;
     *               9 - Altro
     * @param reason la motivazione dell'assenza
     */
    public void justifyAbsence(Absence ab, String type, String reason) {
        if (gS.getUserTypeEnum() != GiuaScraper.userTypes.PARENT) {
            //GiuaScraper.logErrorLn("justifyAbsence: Tipo account non supportato, impossibile giustificare");
            throw new GiuaScraperExceptions.UnsupportedAccount("Può giustificare solo il genitore!");
        }
        try {
            if (ab.justifyUrl.equals(""))
                return;
            if (ab.justifyUrl.contains("assenza")) {
                gS.getSession().newRequest()
                        .url(GiuaScraper.getSiteURL() + "/" + ab.justifyUrl)
                        .data("giustifica_assenza[tipo]", type, "giustifica_assenza[motivazione]", reason, "giustifica_assenza[submit]", "")
                        .post();
            } else if (ab.justifyUrl.contains("ritardo")) {
                gS.getSession().newRequest()
                        .url(GiuaScraper.getSiteURL() + "/" + ab.justifyUrl)
                        .data("giustifica_ritardo[tipo]", type, "giustifica_ritardo[motivazione]", reason, "giustifica_ritardo[submit]", "")
                        .post();
            }
        } catch (Exception e) {
            //GiuaScraper.logErrorLn("Qualcosa è andato storto");
            e.printStackTrace();
        }
    }

    /**
     * Permette di cancellare una giustificazione di una assenza da un account genitore.
     *
     * @param ab l' assenza a cui togliere la giustificazione
     */
    public void deleteJustificationAbsence(Absence ab) {
        if (gS.getUserTypeEnum() != GiuaScraper.userTypes.PARENT) {
            //GiuaScraper.logErrorLn("justifyAbsence: Tipo account non supportato, impossibile giustificare");
            throw new GiuaScraperExceptions.UnsupportedAccount("Può giustificare solo il genitore!");
        }
        try {
            if (ab.justifyUrl.equals(""))
                return;
            if (ab.justifyUrl.contains("assenza")) {
                gS.getSession().newRequest()
                        .url(GiuaScraper.getSiteURL() + "/" + ab.justifyUrl)
                        .data("giustifica_assenza[tipo]", "", "giustifica_assenza[motivazione]", "", "giustifica_assenza[delete]", "")
                        .post();
            } else if (ab.justifyUrl.contains("ritardo")) {
                gS.getSession().newRequest()
                        .url(GiuaScraper.getSiteURL() + "/" + ab.justifyUrl)
                        .data("giustifica_ritardo[tipo]", "", "giustifica_ritardo[motivazione]", "", "giustifica_ritardo[delete]", "")
                        .post();
            }
        } catch (Exception e) {
            //GiuaScraper.logErrorLn("Qualcosa è andato storto");
            e.printStackTrace();
        }
    }


    /**
     * Permette di ottenere tutte le assenze presenti
     * @return Una lista di Absence
     */
    public List<Absence> getAllAbsences() {
        if (gS.isDemoMode()) {
            return GiuaScraperDemo.getAllAbsences();
        }
        List<Absence> allAbsences = new Vector<>();

        Elements allAbsencesTBodyHTML = doc.getElementsByClass("table table-bordered table-hover table-striped");
        allAbsencesTBodyHTML.remove(0); //Rimuovi tabella "Da giustificare" (oppure quella "Situazione globale")

        for (Element el : allAbsencesTBodyHTML) {
            el = el.child(2);
            for (Element el2 : el.children()) {
                String urlJ = "";

                Elements button = el2.child(3).getElementsByClass("btn btn-primary btn-xs gs-button-remote");
                boolean isJustified = false;
                boolean isModificable = false;

                if (!button.isEmpty()) {    //Controlla se esiste il bottone Giustifica
                    urlJ = GiuaScraperUtils.convertGlobalPathToLocal(button.first().attr("data-href"));
                    //isJustified = false;
                    //isModificable = false;
                } else {
                    button = el2.child(3).getElementsByClass("btn btn-default btn-xs gs-button-remote");
                    if (!button.isEmpty()) {    //Controlla se esiste il bottone Modifica
                        urlJ = GiuaScraperUtils.convertGlobalPathToLocal(button.first().attr("data-href"));
                        isJustified = true;
                        isModificable = true;
                    } else {
                        button = el2.child(3).getElementsByClass("label label-danger");
                        if (!button.isEmpty())    //Controlla se cè il testo "Da giustificare"
                            isJustified = false;
                        else
                            isJustified = true;
                            //isModificable = false;
                    }
                }

                allAbsences.add(new Absence(el2.child(0).text(), el2.child(1).text(), el2.child(2).text(), isJustified, isModificable, urlJ));
            }
        }

        return allAbsences;
    }

    /**
     * Ottiene il numero di giorni di assenza
     */
    public String getAbsencesDayCount(){
        return tbodyGlobalSituation.child(0).child(1).text();
    }

    /**
     * Ottiene il numero di ritardi brevi
     */
    public String getShortDelaysCount(){
        return tbodyGlobalSituation.child(1).child(1).text();
    }

    /**
     * Ottiene il numero di ritardi
     */
    public String getDelaysCount(){
        return tbodyGlobalSituation.child(2).child(1).text();
    }

    /**
     * Ottiene il numero delle uscite anticipate
     */
    public String getEarlyExitsCount(){
        return tbodyGlobalSituation.child(3).child(1).text();
    }

    /**
     * Ottiene le ore toatli di assenza
     */
    public String getTotalHourOfAbsences(){
        return tbodyGlobalSituation.child(4).child(1).text();
    }

    /**
     * Ottiene tutte le info extra
     * @return Una stringa contenente per ordine:
     * Numero giorni di assenza, Numero ritardi brevi, Numero di ritardi, Numero di uscite anticipate, Totale ore di assenza
     * Ogni valore e' diviso da " ; " senza le virgolette
     */
    public String getAllExtraInfo(){
        return getAbsencesDayCount() + " ; "
                + getShortDelaysCount() + " ; "
                + getDelaysCount() + " ; "
                + getEarlyExitsCount() + " ; "
                + getTotalHourOfAbsences();
    }
}
