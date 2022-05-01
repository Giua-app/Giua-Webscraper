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

import com.giua.objects.Observations;
import com.giua.webscraper.GiuaScraper;
import com.giua.webscraper.GiuaScraperDemo;
import com.giua.webscraper.GiuaScraperExceptions;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.Vector;

public class ObservationsPage implements IPage {
    private GiuaScraper gS;
    private Document doc;

    private int maxQuarterly = -1;

    public ObservationsPage(GiuaScraper gS) {
        this.gS = gS;
        refreshPage();
    }


    @Override
    public void refreshPage() {
        doc = gS.getPage(UrlPaths.OBSERVATIONS_PAGE);
    }

    /**
     * Ottiene le osservazioni e le ritorna in una lista.
     * ATTENZIONE: Solo il genitore può accedervi
     *
     * @return Una lista di {@code Observations} contenente le osservazioni
     * @throws GiuaScraperExceptions.UnsupportedAccount Quando si è un genitore
     */
    public List<Observations> getAllObservations() throws GiuaScraperExceptions.UnsupportedAccount {
        if (gS.isDemoMode())
            return GiuaScraperDemo.getAllObservations();
        if (gS.getUserTypeEnum() != GiuaScraper.userTypes.PARENT)
            throw new GiuaScraperExceptions.UnsupportedAccount("Only PARENT account type can request observations page");

        List<Observations> returnAllObs = new Vector<>();
        Elements obsTables = doc.getElementsByTag("tbody"); //Primo e Secondo quadrimestre
        int quarterlyCounter = obsTables.size();

        maxQuarterly = quarterlyCounter;

        for (Element el : obsTables) {
            for (Element el2 : el.children()) {
                returnAllObs.add(new Observations(
                        el2.child(0).child(0).text(), //Data
                        el2.child(1).child(0).text(), //Materia
                        el2.child(1).child(2).text(), //Insegnante
                        el2.child(2).text(),          //Testo
                        quarterlyCounter                     //Quadrimestre
                ));

            }

            quarterlyCounter--;
        }

        return returnAllObs;
    }

    public int getMaxQuarterly(){return maxQuarterly;}
}