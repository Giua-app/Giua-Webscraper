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

import com.giua.objects.News;
import com.giua.webscraper.GiuaScraper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class HomePage implements IPage {
    private GiuaScraper gS;
    private Document doc;

    public HomePage(GiuaScraper gS) {
        this.gS = gS;
        refreshPage();
    }

    @Override
    public void refreshPage() {
        doc = gS.getPage(UrlPaths.HOME_PAGE);
    }

    /**
     * Permette di ottenere le news dalla home
     *
     * @return Una lista di stringhe contenenti le news
     */
    public List<News> getAllNewsFromHome() {
        List<News> returnAllNews = new Vector<>();
        Element els = doc.getElementsByClass("panel-body").get(0);
        Elements allNewsHTML = els.children();

        for (Element news : allNewsHTML) {
            String url = news.child(0).child(0).attr("href");
            returnAllNews.add(new News(news.text(), url));
        }

        return returnAllNews;
    }

    public Date getLastAccessTime() {
        Element top = doc.getElementsByClass("panel-title").get(0).child(1);
        String date = top.text().substring(16);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        try {
            return format.parse(date);
        } catch (ParseException e) {
            return null; //Trovare un modo migliore per gestire errore
        }
    }

    /**
     * Restituisce il numero di avvisi da leggere preso dalle notizie
     * nella home
     * <p>
     * Per ottenere il numero di avvisi nuove basta memorizzare il risultato
     * di questa funzione (valore1), poi richiamarla un altra volta (valore2)
     * e fare la differenza valore2 - valore1.
     *
     * @return numero di avvisi da leggere
     */
    public int checkForAlertsUpdate(boolean forceRefresh) {
        List<News> news = getAllNewsFromHome();
        String text;

        for (News nw : news) {
            if (nw.newsText.contains("avvisi")) {
                text = nw.newsText;
                text = text.split("nuovi")[0].split("presenti")[1].charAt(1) + "";

                return Integer.parseInt(text);
            } else if (nw.newsText.contains("avviso")) {
                return 1;
            }
        }

        return 0;
    }
}