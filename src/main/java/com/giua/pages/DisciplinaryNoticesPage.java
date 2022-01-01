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

import java.util.List;
import java.util.Vector;

public class DisciplinaryNoticesPage implements IPage {
    private GiuaScraper gS;
    private Document doc;

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

        for (Element el : allDisciplNoticeTBodyHTML) {
            String quarterly = el.parent().child(0).text();

            for (Element el2 : el.children()) {
                String author1 = el2.child(2).child(1).text();
                String author2;
                try {
                    author2 = el2.child(3).child(1).text();
                } catch (IndexOutOfBoundsException e) {
                    author2 = "";
                }

                el2.child(2).child(1).remove();
                allDisciplNotices.add(new DisciplinaryNotices(
                        el2.child(0).text(),
                        el2.child(1).text(),
                        el2.child(2).text(),
                        el2.child(3).text(),
                        author1,
                        author2,
                        quarterly));
            }
        }
        return allDisciplNotices;
    }
}