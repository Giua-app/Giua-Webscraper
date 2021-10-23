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

import com.giua.objects.Alert;
import com.giua.objects.News;
import com.giua.webscraper.GiuaScraper;
import com.giua.webscraper.GiuaScraperDemo;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.Vector;

public class AlertsPage implements IPage {
    private GiuaScraper gS;
    private Document doc;

    public AlertsPage(GiuaScraper gS) {
        this.gS = gS;
        refreshPage();
    }


    @Override
    public void refreshPage() {
        doc = gS.getPage(UrlPaths.ALERTS_PAGE);
    }

    /**
     * Ritorna una lista di {@code Alert} senza {@code details} e {@code creator}.
     * Per generare i dettagli {@link Alert#getDetails(GiuaScraper)}
     *
     * @param page         La pagina da cui prendere gli avvisi. Deve essere maggiore di 0.
     * @param forceRefresh Ricarica effettivamente tutti i voti
     * @return Lista di Alert
     * @throws IndexOutOfBoundsException Se {@code page} è minore o uguale a 0.
     */
    public List<Alert> getAllAlerts(int page, boolean forceRefresh) throws IndexOutOfBoundsException {
        if (gS.isDemoMode()) {
            return GiuaScraperDemo.getAllAlerts();
        }
        if (page < 0) {
            throw new IndexOutOfBoundsException("Un indice di pagina non puo essere 0 o negativo");
        }
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
}
