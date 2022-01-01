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

import com.giua.objects.Authorization;
import com.giua.webscraper.GiuaScraper;
import com.giua.webscraper.GiuaScraperDemo;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class AuthorizationsPage implements IPage{
    private GiuaScraper gS;
    private Document doc;

    public AuthorizationsPage(GiuaScraper gS){
        this.gS = gS;
        refreshPage();
    }

    @Override
    public void refreshPage() {
        doc = gS.getPage(UrlPaths.AUTHORIZATIONS_PAGE);
    }

    public Authorization getAuthorizations() {
        if (gS.isDemoMode())
            return GiuaScraperDemo.getAutorizations();
        Elements textsHTML = doc.getElementsByClass("gs-text-normal gs-big");
        String entry = textsHTML.get(0).text();
        String exit = textsHTML.get(1).text();

        return new Authorization(entry, exit);
    }
}