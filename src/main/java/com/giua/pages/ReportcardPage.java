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

import com.giua.objects.ReportCard;
import com.giua.utils.GiuaScraperUtils;
import com.giua.utils.LoggerManager;
import com.giua.webscraper.GiuaScraper;
import com.giua.webscraper.GiuaScraperDemo;
import com.giua.webscraper.GiuaScraperExceptions;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class ReportcardPage implements IPage{
    private final GiuaScraper gS;
    private Document doc;
    private final LoggerManager lm;
    public static String firstQuaterly="Primo Quadrimestre";
    public static String secondQuaterly="Scrutinio Finale";
    public static String lastYear="A.S. Precedente";
    public static String finalExams="Scrutinio esami giudizio sospeso";

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
     * @return un oggetto {@link ReportCard}
     */
    public ReportCard getReportcard(String quaterlyName){
        if (gS.isDemoMode()) {
            return GiuaScraperDemo.getReportCard();
        }
        ReportCard returnRc;
        String quarterly=null;
        Map<String, List<String>> page=new HashMap<>();
        Map<String, List<String>> allVotes=new HashMap<>();
        String finalResult= null;
        String mean=null;
        String credits=null;
        Map<String, List<String>>allDebts=new HashMap<>();

        if(quaterlyName.equals("A.S. Precedente"))
            return getOldYearReportCard();
        try {
            //quadrimestre
            Elements els = doc.getElementsByClass("dropdown-menu").get(3).children();

            for (Element el : els) {
                if (el.text().trim().equalsIgnoreCase(quaterlyName.trim())) {
                    quarterly=el.text();
                    doc = gS.getPage(el.child(0).attr("href"));
                    break;
                }
            }
            if(GiuaScraperUtils.convertGlobalPathToLocal(gS.getSiteUrl(),false).equals("/genitori/pagelle"))
                    return new ReportCard(false);   //la pagella non è presente nel sito

            //scrutini
            if(doc!=null) {
                Elements allSubjectsHTML = doc.getElementsByTag("tbody").get(0).children();

                for (Element element : allSubjectsHTML) {
                    List <String> list = new Vector<>();
                    list.add(element.child(1).text());
                    list.add(element.child(2).text());
                    page.put(element.child(0).text(), list);
                }
                allVotes=page;
                page=new HashMap<>();
            }
        } catch (IndexOutOfBoundsException ignored) {}
        //esito e crediti
        if(!quaterlyName.equals(firstQuaterly)) { //i debiti sono presenti solo nel primo quadrimestre
            try {
                finalResult = doc.getElementsByClass("alert alert-success").get(0).child(0).text().split(":")[1].trim();
                mean=doc.getElementsByClass("alert alert-success").get(0).child(1).text().split(":")[1].trim();
                credits= doc.getElementsByClass("alert alert-success").get(0).child(3).text().split(":")[1].trim();
            } catch (NullPointerException | IndexOutOfBoundsException e) {
                try {
                    finalResult = doc.getElementsByClass("alert alert-warning").get(0).child(0).text().split(":")[1].trim();
                    mean=getCalculatedMean(allVotes);
                } catch (NullPointerException | IndexOutOfBoundsException E) {
                    try {
                        finalResult = doc.getElementsByClass("alert alert-danger").get(0).child(0).text().split(":")[1].trim();
                        mean=getCalculatedMean(allVotes);
                    } catch (NullPointerException | IndexOutOfBoundsException ignored) {}
                }
            }
        }
        //debiti
        else{
            mean=getCalculatedMean(allVotes);
            try{
                Elements allDebtsHTML = doc.getElementsByTag("tbody").get(1).children();
                for (Element element : allDebtsHTML) {
                    List <String> list = new Vector<>();
                    list.add(element.child(1).text());
                    list.add(element.child(2).text());
                    page.put(element.child(0).text(), list);
                }
                allDebts=page;
            }catch (IndexOutOfBoundsException e){
                allDebts=null;
            }
        }
        returnRc=new ReportCard(quarterly, allVotes, finalResult, credits, allDebts, mean, true);
        return returnRc;
    }

    /**
     * Questa funzione è creata per ottenere solo ed escusivamente la pagella dell'anno scolastico precedente
     * a causa di alcune sue differenze strutturali rispetto alle altre pagelle.
     * @return un oggetto {@link ReportCard}
     */
    private ReportCard getOldYearReportCard(){
        if (gS.isDemoMode()) {
            return GiuaScraperDemo.getReportCard();
        }
        ReportCard returnRc;
        String quarterly=null;
        Map<String, List<String>> page;
        Map<String, List<String>> allVotes=new HashMap<>();
        String finalResult= null;
        String mean=null;
        String credits=null;

        try {
            //quadrimestre
            Elements els = doc.getElementsByClass("dropdown-menu").get(3).children();

            for (Element el : els) {
                if (el.text().trim().equalsIgnoreCase("A.S. Precedente")) {
                    quarterly=el.text();
                    doc = gS.getPage(el.child(0).attr("href"));
                    break;
                }
            }
            //scrutini
            if(doc!=null) {
                Elements allSubjectsHTML = doc.getElementsByTag("tbody").get(0).children();
                page=new HashMap<>();
                for (Element element : allSubjectsHTML) {
                    List <String> list = new Vector<>();
                    list.add(element.child(1).text());
                    page.put(element.child(0).text(), list);
                }
                allVotes=page;
            }
        } catch (IndexOutOfBoundsException ignored) {}
        //esito e crediti
        try {
              finalResult = doc.getElementsByClass("alert alert-success text-center gs-mt-4").get(0).child(0).text().split(":")[1].split(" ")[1].trim();
              credits = doc.getElementsByClass("alert alert-success text-center gs-mt-4").get(0).child(0).text().split(":")[2].trim();
              mean=getCalculatedMean(allVotes);
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            /*TODO controllare le pagelle dei bocciati e dei rimandati*/
              try {
                  finalResult = doc.getElementsByClass("alert alert-warning text-center gs-mt-4").get(0).child(0).text().split(":")[1].split(" ")[1].trim();
                  mean=getCalculatedMean(allVotes);
            } catch (NullPointerException | IndexOutOfBoundsException E) {
                try {
                    finalResult = doc.getElementsByClass("alert alert-danger text-center gs-mt-4").get(0).child(0).text().split(":")[1].split(" ")[1].trim();
                    mean=getCalculatedMean(allVotes);
                } catch (NullPointerException | IndexOutOfBoundsException ignored) {}
            }
        }
        returnRc = new ReportCard(quarterly, allVotes, finalResult, credits, null, mean, true);
        return returnRc;
    }

    /**
     * Ottieni la media dei voti calcolata.
     * ATTENZIONE: i giudizi (Es. Ottimo) non vengono contati nella media
     *
     * @return La media dei voti come un {@code float}
     */
    public String getCalculatedMean(Map<String, List<String>> allVotes) {

        float mean = 0f;

        for (String subject : allVotes.keySet()) {
            if(!subject.equals("Religione Cattolica o attività alternative")){
                List<String> s = allVotes.get(subject);
                float vote;
                vote = Float.parseFloat(s.get(0));
                mean += vote;
            }
        }
        return String.valueOf( mean / allVotes.keySet().size());
    }
    public int getReligionVote(String sVote){
        Map<String,Integer> religionVotes=new HashMap<>();
        religionVotes.put("Ottimo", 10);
        religionVotes.put("Distinto",9);
        religionVotes.put("Buono",8);
        religionVotes.put("Discreto",7);
        religionVotes.put("Sufficiente",6);
        religionVotes.put("Insufficiente",5);
        religionVotes.put("Scarso",4);
        religionVotes.put("--",-1);//L'alunno non fa religione
        return religionVotes.get(sVote);
    }
}