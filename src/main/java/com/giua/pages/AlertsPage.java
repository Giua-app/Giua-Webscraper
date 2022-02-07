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

import com.giua.objects.Alert;
import com.giua.utils.LoggerManager;
import com.giua.webscraper.GiuaScraper;
import com.giua.webscraper.GiuaScraperDemo;
import com.giua.webscraper.GiuaScraperExceptions;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

public class AlertsPage implements IPage {
    private GiuaScraper gS;
    private Document doc;   //ATTENZIONE: doc rappresenta solo la prima pagina
    private final LoggerManager lm;

    public AlertsPage(GiuaScraper gS) {
        this.gS = gS;
        lm = new LoggerManager("AlertsPage");
        refreshPage();
    }


    @Override
    public void refreshPage() {
        doc = gS.getPage(UrlPaths.ALERTS_PAGE);
        resetFiltersAndRefreshPage();
    }

    public void resetFiltersAndRefreshPage() {
        try {
            doc = gS.getSession().newRequest()
                    .url(GiuaScraper.getSiteURL() + "/" + UrlPaths.ALERTS_PAGE)
                    .data("bacheca_avvisi_genitori[visualizza]", "T")
                    .data("bacheca_avvisi_genitori[oggetto]", "")
                    .data("bacheca_avvisi_genitori[submit]", "")
                    .data("bacheca_avvisi_genitori[_token]", getFilterToken())
                    .post();
        } catch (IOException e) {
            if (!GiuaScraper.isSiteWorking()) {
                throw new GiuaScraperExceptions.SiteConnectionProblems("Can't get page because the website is down, retry later", e);
            }
            e.printStackTrace();
        }
    }

    /**
     * Ritorna una lista di {@code Alert} senza {@code details} e {@code creator}.
     * Per generare i dettagli {@link Alert#getDetails(GiuaScraper)}
     * ATTENZIONE: Utilizza una richiesta HTTP
     *
     * @param page La pagina da cui prendere gli avvisi. Deve essere maggiore di 0.
     * @return Lista di Alert
     * @throws IndexOutOfBoundsException Se {@code page} è minore o uguale a 0.
     */
    public List<Alert> getAllAlerts(int page) throws IndexOutOfBoundsException {
        if (gS.isDemoMode()) {
            return GiuaScraperDemo.getAllAlerts();
        }
        if (page < 0) {
            throw new IndexOutOfBoundsException("Un indice di pagina non puo essere negativo");
        }
        Document doc = gS.getPage(UrlPaths.ALERTS_PAGE + "/" + page);
        List<Alert> allAlerts = new Vector<>();
        Elements allAlertsHTML = doc.getElementsByTag("tbody");
        if (allAlertsHTML.isEmpty())
            return allAlerts;
        allAlertsHTML = allAlertsHTML.get(0).children();

        for (Element alertHTML : allAlertsHTML) {
            allAlerts.add(new Alert(
                    alertHTML.child(0).text(),
                    alertHTML.child(1).text(),
                    alertHTML.child(2).text(),
                    alertHTML.child(3).text(),
                    alertHTML.child(4).child(0).attr("data-href"),
                    page
            ));
        }
        return allAlerts;
    }

    /**
     * Ritorna una lista di {@code Alert} senza {@code details} e {@code creator}.
     * Per generare i dettagli {@link Alert#getDetails(GiuaScraper)}
     * Una volta applicato il filtro {@link #getAllAlerts(int)} ritornerà i risultati con il filtro.
     * Utilizzare {@link #resetFiltersAndRefreshPage()} per resettare il filtro
     * ATTENZIONE: Utilizza una richiesta HTTP
     *
     * @param onlyNotRead true per ottenere soltanto gli avvisi non letti, false altrimenti
     * @param text        Il testo da filtrare
     * @return Lista di Alert
     * @throws IndexOutOfBoundsException Se {@code page} è minore o uguale a 0.
     */
    public List<Alert> getAllAlertsWithFilters(boolean onlyNotRead, String text) throws IndexOutOfBoundsException {
        if (gS.isDemoMode()) {
            return GiuaScraperDemo.getAllAlerts();
        }
        List<Alert> allAlerts = new Vector<>();

        try {
            Document newDoc = gS.getSession().newRequest()
                    .url(GiuaScraper.getSiteURL() + "/" + UrlPaths.ALERTS_PAGE)
                    .data("bacheca_avvisi_genitori[visualizza]", onlyNotRead ? "D" : "T")
                    .data("bacheca_avvisi_genitori[oggetto]", text)
                    .data("bacheca_avvisi_genitori[submit]", "")
                    .data("bacheca_avvisi_genitori[_token]", getFilterToken())
                    .post();


            Elements allAlertsHTML = newDoc.getElementsByTag("tbody");
            if (allAlertsHTML.isEmpty())
                return allAlerts;
            allAlertsHTML = allAlertsHTML.get(0).children();

            for (Element alertHTML : allAlertsHTML) {
                allAlerts.add(new Alert(
                        alertHTML.child(0).text(),
                        alertHTML.child(1).text(),
                        alertHTML.child(2).text(),
                        alertHTML.child(3).text(),
                        alertHTML.child(4).child(0).attr("data-href"),
                        1
                ));
            }
        } catch (IOException e) {
            if (!GiuaScraper.isSiteWorking()) {
                throw new GiuaScraperExceptions.SiteConnectionProblems("Can't get page because the website is down, retry later", e);
            }
            e.printStackTrace();
        }
        return allAlerts;
    }


    /**
     * Ottieni la differenza degli avvisi tra gli {@code oldAlerts} e gli avvisi nuovi. Serve SOLO per notificare
     * i nuovi compiti e le nuove verifiche
     * @param oldAlerts Una lista degli avvisi già notificati
     * @return Una lista di avvisi nuovi da notificare
     *//*
    public List<Alert> getNewAlertsFromOldAlerts(List<Alert> oldAlerts) {
        if (!oldAlerts.isEmpty()) {
            lm.d("Alerts vecchie:");
            for (Alert alert : oldAlerts) {
                lm.d(alert.toString());
            }
        }

        List<Alert> newAlerts = getAllAlertsWithFilters(false, "per la materia");
        List<Alert> temp = new Vector<>();

        lm.d("Ottenuto " + newAlerts.size() + " alerts nuove con filtro");
        for (Alert alert : newAlerts) {
            lm.d(alert.toString());
        }

        lm.d("");
        Date date;
        /**
         * codice di debug per inserire una data qualsiasi
         * SimpleDateFormat dF = new SimpleDateFormat("dd/MM/yyyy");
        try {
            date = dF.parse("20/11/2021");
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }*//*
        date=new Date();    //per inserire una data manualmente col codice qui sopra, commentare questa riga

        
        for(int i=0; i<newAlerts.size();i++){
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date alertDate=new Date();
            try {
                alertDate = dateFormat.parse(newAlerts.get(i).date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(date.before(alertDate) ||date.equals(alertDate)){ //TODO: controllare se notifica anche i compiti assegnati il giorno odierno
                lm.d("Aggiungo " + newAlerts.get(i).object+ " del giorno "+ newAlerts.get(i).date);
                temp.add(newAlerts.get(i));
            }
        }
        lm.d("");
        newAlerts = temp;
        temp=new Vector<>();
        if(!oldAlerts.isEmpty()){
            int index=0;
            while (newAlerts.get(index).object==oldAlerts.get(index).object){
                index++;
            }
            int l=0;
            for(int i = index; i < newAlerts.size(); i++){
                lm.d("Controllo alert "+ newAlerts.get(i).object+" con "+ oldAlerts.get(i-l).object);
                if(newAlerts.get(i).object!=oldAlerts.get(i-l).object){
                    lm.d("Aggiungo "+ newAlerts.get(i).object);
                    temp.add(newAlerts.get(i));
                    l++;
                }
            }
            newAlerts = temp;
            oldAlerts=newAlerts;
            lm.d("new alerts finali:");
            for (Alert alert : newAlerts) {
                lm.d(alert.toString());
            }
        }
        return newAlerts;
    }*/

    /**
     * Ottieni la differenza degli avvisi tra gli {@code oldAlerts} e gli avvisi nuovi. Serve SOLO per notificare
     * i nuovi compiti e le nuove verifiche
     *
     * @param oldAlerts Una lista degli avvisi già notificati
     * @return Una lista di avvisi nuovi da notificare
     */
    public List<Alert> getAlertsToNotify(List<Alert> oldAlerts) {
        List<Alert> newAlerts = getAllAlertsWithFilters(false, "per la materia");
        List<Alert> returnDifference = new Vector<>(newAlerts);
        if (oldAlerts.size() == 0)
            return newAlerts;   //Ritorno newAlerts perché è la loro differenza

        for (Alert newAlert : newAlerts) {
            for (Alert oldAlert : oldAlerts) {
                if (newAlert.toStringWithoutStatus().equals(oldAlert.toStringWithoutStatus()))
                    returnDifference.remove(newAlert);
            }
        }

        return returnDifference;
    }

    private String getFilterToken() {
        return doc.getElementById("bacheca_avvisi_genitori__token").attr("value");
    }

    /**
     * Segna l' avviso come già letto
     * ATTENZIONE: Usa una richiesta HTTP
     *
     * @param alert L' avviso da segnare come letto
     */
    public void markAlertAsRead(Alert alert) {
        try {
            gS.getSession().newRequest()
                    .url(GiuaScraper.getSiteURL() + alert.detailsUrl)
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .maxBodySize(1)
                    .execute();
        } catch (IOException e) {
            if (!GiuaScraper.isSiteWorking()) {
                throw new GiuaScraperExceptions.SiteConnectionProblems("Can't get page because the website is down, retry later", e);
            }
            e.printStackTrace();
        }
    }
}