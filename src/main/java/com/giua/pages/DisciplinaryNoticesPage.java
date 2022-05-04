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

import com.giua.objects.DisciplinaryNotices;
import com.giua.webscraper.GiuaScraper;
import com.giua.webscraper.GiuaScraperDemo;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class DisciplinaryNoticesPage implements IPage {
    private GiuaScraper gS;
    private Document doc;

    private int maxQuarterly = -1;
    private final List<String> allQuarterlyNames = new Vector<>();

    public DisciplinaryNoticesPage(GiuaScraper gS) {
        this.gS = gS;
        refreshPage();
    }


    @Override
    public void refreshPage() {
        doc = gS.getPage(UrlPaths.DISCIPLINARY_NOTICES_PAGE);
    }

    /**
     * Permette di ottenere tutte le note presenti
     *
     * @return Una lista di DisciplNotice
     */
    public List<DisciplinaryNotices> getAllDisciplinaryNotices() {
        if (gS.isDemoMode())
            return GiuaScraperDemo.getAllDisciplNotices();

        List<DisciplinaryNotices> allDisciplNotices = new Vector<>();
        Elements allDisciplNoticeTBodyHTML = doc.getElementsByTag("tbody");
        int quarterlyCounter = allDisciplNoticeTBodyHTML.size();

        maxQuarterly = quarterlyCounter;
        getAllQuarterlyNames(); //Aggiorna i nomi dei quadrimestri

        for (Element el : allDisciplNoticeTBodyHTML) {
            for(Element note: el.children()){
                String countermeasures; String authorOfCountermeasures;
                try{
                    countermeasures=note.child(3).text().split("\\(")[0];
                    authorOfCountermeasures=note.child(3).text().split("\\(")[1].replace(")","");
                }catch (IndexOutOfBoundsException e){
                    countermeasures="";
                    authorOfCountermeasures="";
                }
                allDisciplNotices.add(new DisciplinaryNotices(
                        note.child(0).text(),
                        note.child(1).text(),
                        note.child(2).text().split("\\(")[0],
                        countermeasures,
                        note.child(2).text().split("\\(")[1].replace(")",""),
                        authorOfCountermeasures,
                        quarterlyCounter));
            }

            quarterlyCounter--;
        }
        return allDisciplNotices;
    }

    public List<String> getAllQuarterlyNames(){
        if (allQuarterlyNames.size() > 0) return allQuarterlyNames;

        Elements allTbody = doc.getElementsByTag("tbody");

        if (allTbody.size() == 0) return new Vector<>();

        for (Element tbody : allTbody) {
            final String quarterlyName = tbody.parent().child(0).text();
            allQuarterlyNames.add(quarterlyName);
        }

        Collections.reverse(allQuarterlyNames);

        return allQuarterlyNames;
    }

    public int getMaxQuarterly(){return maxQuarterly;}
}