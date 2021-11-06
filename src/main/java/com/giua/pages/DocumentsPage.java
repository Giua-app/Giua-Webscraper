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

import com.giua.webscraper.GiuaScraper;
import com.giua.webscraper.GiuaScraperExceptions;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

public class DocumentsPage implements IPage {
    private GiuaScraper gS;
    private Document doc;

    public DocumentsPage(GiuaScraper gS) {
        this.gS = gS;
        refreshPage();
    }


    @Override
    public void refreshPage() {
        doc = gS.getPage(UrlPaths.DOCUMENTS_PAGE);
    }

    /**
     * Ottiene una lista di {@code Document}
     * @return Una lista di {@code Document}
     */
    public List<com.giua.objects.Document> getDocuments() {
        List<com.giua.objects.Document> returnAllDocuments = new Vector<>();
        Elements allDocumentsHTML = doc.getElementsByTag("tbody");
        if (!allDocumentsHTML.isEmpty())
            allDocumentsHTML = allDocumentsHTML.get(0).children();
        else {
            return returnAllDocuments;
        }

        for (Element documentHTML : allDocumentsHTML) {
            returnAllDocuments.add(new com.giua.objects.Document(
                    documentHTML.child(0).text(),
                    documentHTML.child(1).text().split(" - ")[0].split("Classe: ")[1],
                    documentHTML.child(1).text().split(" - ")[2].split(" Materia: ")[0],
                    documentHTML.child(1).text().split(" - ")[1].replace(" ", ""),
                    documentHTML.child(3).child(0).attr("href")
            ));
        }

        return returnAllDocuments;

    }

    private String getDocumentsToken() {
        return Objects.requireNonNull(doc.getElementById("documento__token")).attr("value");
    }

    /**
     * Ottiene una lista di {@code Document} con i filtri indicati nei parametri
     * ATTENZIONE: Fa una richiesta POST HTTP
     *
     * @param filterType Indica con un numero che filtro si vuole applicare:
     *                   0 - tutti i documenti;
     *                   1 - solo da leggere;
     *                   2 - programmi svolti;
     *                   3 - documenti del 15 maggio;
     *                   4 - altro
     * @param filterText Indica il testo da filtrare
     * @return Una lista di {@code Document}
     */
    public List<com.giua.objects.Document> getDocumentsWithFilter(int filterType, String filterText) {
        if (gS.isMaintenanceActive())
            throw new GiuaScraperExceptions.MaintenanceIsActiveException("The website is in maintenance");
        List<com.giua.objects.Document> returnAllDocuments = new Vector<>();
        Document doc = null;

        String filter;
        if (filterType == 1)
            filter = "X";
        else if (filterType == 2)
            filter = "P";
        else if (filterType == 3)
            filter = "M";
        else if (filterType == 4)
            filter = "G";
        else
            filter = "";

        try {
            doc = gS.getSession().newRequest()
                    .url(GiuaScraper.getSiteURL() + "/documenti/bacheca")
                    .data("documento[tipo]", filter)
                    .data("documento[titolo]", filterText)
                    .data("documento[_token]", getDocumentsToken())
                    .post();
        } catch (IOException e) {
            if (!GiuaScraper.isSiteWorking()) {
                throw new GiuaScraperExceptions.SiteConnectionProblems("Can't get page because the website is down, retry later", e);
            }
            e.printStackTrace();
        }
        Elements allDocumentsHTML = Objects.requireNonNull(doc).getElementsByTag("tbody");
        if (!allDocumentsHTML.isEmpty())
            allDocumentsHTML = allDocumentsHTML.get(0).children();
        else {
            return returnAllDocuments;
        }

        for (Element documentHTML : allDocumentsHTML) {
            returnAllDocuments.add(new com.giua.objects.Document(
                    documentHTML.child(0).text(),
                    documentHTML.child(1).text().split(" - ")[0].split(" Classe: ")[0],
                    documentHTML.child(1).text().split(" - ")[2].split(" Materia: ")[0],
                    documentHTML.child(1).text().split(" - ")[1].replace(" ", ""),
                    documentHTML.child(3).child(0).attr("href")
            ));
        }

        return returnAllDocuments;

    }
}