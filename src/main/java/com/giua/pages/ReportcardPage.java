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

import com.giua.objects.ReportCard;
import com.giua.utils.LoggerManager;
import com.giua.webscraper.GiuaScraper;
import com.giua.webscraper.GiuaScraperDemo;
import com.giua.webscraper.GiuaScraperExceptions;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class ReportcardPage implements IPage{
    private GiuaScraper gS;
    private Document doc;
    private final LoggerManager lm;

    public ReportcardPage(GiuaScraper gS){
        this.gS = gS;
        lm = new LoggerManager("ReportcardPage");
        refreshPage();
    }
    @Override
    public void refreshPage() {
        doc = gS.getPage(UrlPaths.REPORTCARD_PAGE);
    }

    /**
     * permette  di ottenere la pagella di un periodo dell'anno scolastico o dell'anno precedente
     * @param quaterlyName nome del quadrimestre della pagella  desiserata
     * -----------------------------------------------------
     * ATTENZIONE: assegnargli solo le seguenti stringhe:
     * "A.S. Precedente"
     * "Primo Quadrimestre"
     * "Scrutinio Finale"
     * "Scrutinio esami giudizio sospeso"
     * -----------------------------------------------------
     * @return un oggetto {@link ReportCard}
     */
    public ReportCard getReportcard(String quaterlyName){
        ReportCard returnRc = new ReportCard();
        Map<String, List<String>> page=new HashMap<>();

        if(quaterlyName=="A.S. Precedente"){
            returnRc=getOldYearReportCard();
            return returnRc
        }

        if (gS.isDemoMode()) {
            return GiuaScraperDemo.getAllReportcard();
        }
        try {
            //quadrimestre
            Elements els = doc.getElementsByClass("dropdown-menu").get(3).children();

            for (Element el : els) {
                if (el.text().trim().equalsIgnoreCase(quaterlyName.trim())) {
                    returnRc.quarterly=el.text();
                    doc = gS.getPage(el.child(0).attr("href"));
                    break;
                }
            }
            //scrutini
            if(doc!=null) {
                Elements allSubjectsHTML = doc.getElementsByTag("tbody").get(0).children();

                for (Element element : allSubjectsHTML) {
                    List <String> list = new Vector<>();
                    list.add(element.child(1).text());
                    list.add(element.child(2).text());
                    page.put(element.child(0).text(), list);
                }
                returnRc.allVotes=page;
                page=new HashMap<>();
            }
        } catch (IndexOutOfBoundsException e) {}
        //esito e crediti
        if(quaterlyName!="Primo Quadrimestre") {
            try {
                returnRc.finalResult = doc.getElementsByClass("alert alert-success").get(0).child(0).text().split(":")[1].trim();
                returnRc.calculatedMean=doc.getElementsByClass("alert alert-success").get(0).child(1).text().split(":")[1].trim();
                returnRc.credits = doc.getElementsByClass("alert alert-success").get(0).child(3).text().split(":")[1].trim();
            } catch (NullPointerException | IndexOutOfBoundsException e) {
                try {
                    returnRc.finalResult = doc.getElementsByClass("alert alert-warning").get(0).child(0).text().split(":")[1].trim();
                } catch (NullPointerException | IndexOutOfBoundsException E) {
                    try {
                        returnRc.finalResult = doc.getElementsByClass("alert alert-danger").get(0).child(0).text().split(":")[1].trim();
                    } catch (NullPointerException | IndexOutOfBoundsException è) {}
                }
            }
        }
        //debiti
        else{
            try{
                Elements allDebtsHTML = doc.getElementsByTag("tbody").get(1).children();
                for (Element element : allDebtsHTML) {
                    List <String> list = new Vector<>();
                    list.add(element.child(1).text());
                    list.add(element.child(2).text());
                    page.put(element.child(0).text(), list);
                }
                returnRc.allDebts=page;
            }catch (NullPointerException e){}
        }
        return returnRc;
    }

    /**
     * Questa funzione è creata per ottenere soloed escusivamente la pagella dell'anno scolastico precedente
     * a causa di alcune sue differenze strutturali rispetto alle altre pagelle.
     * @return un oggetto {@link ReportCard}
     */
    private ReportCard getOldYearReportCard(){
        ReportCard returnRc = new ReportCard();
        if (gS.isDemoMode()) {
            return GiuaScraperDemo.getAllReportcard();
        }
        try {
            //quadrimestre
            doc = gS.getPage("/genitori/pagelle/A");
            returnRc.quarterly="A.S. Precedente";
            //scrutini
            if(doc!=null) {
                Elements allSubjectsHTML = doc.getElementsByTag("tbody").get(0).children();
                Map<String,List<String>> page=new HashMap<>();
                for (Element element : allSubjectsHTML) {
                    List <String> list = new Vector<>();
                    list.add(element.child(1).text());
                    page.put(element.child(0).text(), list);
                }
                returnRc.allVotes=page;
            }
        } catch (IndexOutOfBoundsException e) {}
        //esito e crediti
        try {
              returnRc.finalResult = doc.getElementsByClass("alert alert-success text-center gs-mt-4").get(0).child(0).text().split(":")[1].split(" ")[1].trim();
              returnRc.credits = doc.getElementsByClass("alert alert-success text-center gs-mt-4").get(0).child(0).text().split(":")[2].trim();
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            /**TODO controllare le pagelle dei bocciati e dei rimandati
             * try {
               returnRc.finalResult = doc.getElementsByClass("alert alert-warning").get(0).child(0).text().split(":")[1].trim();
            } catch (NullPointerException | IndexOutOfBoundsException E) {
                try {
                    returnRc.finalResult = doc.getElementsByClass("alert alert-danger").get(0).child(0).text().split(":")[1].trim();
                } catch (NullPointerException | IndexOutOfBoundsException è) {}
            }*/
        }
        return returnRc;
    }
}
