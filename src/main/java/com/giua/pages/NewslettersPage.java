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

import com.giua.objects.Newsletter;
import com.giua.webscraper.GiuaScraper;
import com.giua.webscraper.GiuaScraperDemo;
import com.giua.webscraper.GiuaScraperExceptions;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.Objects;
import java.util.Vector;

public class NewslettersPage implements IPage{
    private GiuaScraper gS;
    private Document doc;

    public NewslettersPage(GiuaScraper gS) {
        this.gS = gS;
        refreshPage();
    }


    @Override
    public void refreshPage() {
        getAllNewslettersWithFilter(false, "", "", 1);
        doc = gS.getPage(UrlPaths.NEWSLETTERS_PAGE);
    }


    /**
     * Serve solo a {@code #getAllNewsletters} per prendere gli allegati dalle circolari
     *
     * @param el
     * @return Lista di Stringa con tutti gli URL degli allegati
     */
    private List<String> attachmentsUrls(Element el) {
        Elements els = el.child(1).children();
        List<String> r = new Vector<>();
        if (els.size() > 2) {     //Ci sono allegati
            Elements allAttachments = els.get(1).child(0).children();
            for (Element attachment : allAttachments) {
                r.add(attachment.child(1).attr("href"));
            }
        } else {        //Non ha allegati
            return null;
        }

        return r;
    }

    private String getNewsletterFilterToken() {
        return Objects.requireNonNull(doc.getElementById("circolari_genitori__token")).attr("value");
    }

    /**
     * Serve ad ottenere tutte le {@link Newsletter} della pagina specificata
     *
     * @param page La pagina da cui prendere gli avvisi. Deve essere maggiore di 0.
     * @return Lista di NewsLetter contenente tutte le circolari della pagina specificata
     * @throws IndexOutOfBoundsException Se {@code page} è minore o uguale a 0.
     */
    public List<Newsletter> getAllNewsletters(int page) throws IndexOutOfBoundsException {
        if (gS.isDemoMode())
            return GiuaScraperDemo.getAllNewsletters();

        if (page <= 0) {
            throw new IndexOutOfBoundsException("Un indice di pagina non puo essere 0 o negativo");
        }
        List<Newsletter> allNewsletters = new Vector<>();
        try {
            if (!this.doc.baseUri().equals(GiuaScraper.getSiteURL() + "circolari/genitori/" + page)) {
                doc = gS.getPage("circolari/genitori/" + page);
            }

            Elements allNewslettersStatusHTML = doc.getElementsByClass("table table-bordered table-hover table-striped gs-mb-4").get(0).children().get(1).children();

            for (Element el : allNewslettersStatusHTML) {
                allNewsletters.add(new Newsletter(
                        el.child(0).text(),
                        Integer.parseInt(el.child(1).text()),
                        el.child(2).text(),
                        el.child(3).text(),
                        el.child(4).child(1).child(0).child(0).child(0).getElementsByClass("btn btn-xs btn-primary gs-ml-3").get(0).attr("href"),
                        attachmentsUrls(el.child(4)),
                        page));
            }
            return allNewsletters;
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            return allNewsletters;
        }
    }

    /**
     * Serve ad ottenere tutte le {@link Newsletter} della pagina specificata con i filtri specificati.
     * Le stringhe possono anche essere lasciate vuote.
     * ATTENZIONE: Utilizza una richiesta HTTP
     *
     * @param onlyNotRead {@code true} per avere solo le circolari non lette
     * @param date        Mettere la data del mese nel formato: anno-mese
     * @param text        Il testo da cercare tra le circolari
     * @param page        Indica a quale pagina andare. Le pagine partono da 1
     * @return Lista di NewsLetter contenente tutte le circolari della pagina specificata
     */
    public List<Newsletter> getAllNewslettersWithFilter(boolean onlyNotRead, String date, String text, int page) {
        if (gS.isDemoMode())
            return GiuaScraperDemo.getAllNewslettersWithFilter();

        List<Newsletter> allNewsletters = new Vector<>();
        try {

            Document doc = gS.getSession().newRequest()
                    .url(GiuaScraper.getSiteURL() + "/" + UrlPaths.NEWSLETTERS_PAGE + "/" + page)
                    .data("circolari_genitori[visualizza]", onlyNotRead ? "D" : "P")
                    .data("circolari_genitori[mese]", date)
                    .data("circolari_genitori[oggetto]", text)
                    .data("circolari_genitori[submit]", "")
                    .data("circolari_genitori[_token]", getNewsletterFilterToken())
                    .post();

            Elements allNewslettersStatusHTML = doc.getElementsByClass("table table-bordered table-hover table-striped gs-mb-4").get(0).children().get(1).children();

            for (Element el : allNewslettersStatusHTML) {
                allNewsletters.add(new Newsletter(
                        el.child(0).text(),
                        Integer.parseInt(el.child(1).text()),
                        el.child(2).text(),
                        el.child(3).text(),
                        el.child(4).child(1).child(0).child(0).child(0).getElementsByClass("btn btn-xs btn-primary gs-ml-3").get(0).attr("href"),
                        attachmentsUrls(el.child(4)),
                        page));
            }

            return allNewsletters;

        } catch (NullPointerException | IndexOutOfBoundsException e) {
            return new Vector<>();
        } catch (Exception e) {
            if (!GiuaScraper.isSiteWorking()) {
                throw new GiuaScraperExceptions.SiteConnectionProblems("Can't get page because the website is down, retry later", e);
            }
            e.printStackTrace();
        }
        return new Vector<>();
    }

}