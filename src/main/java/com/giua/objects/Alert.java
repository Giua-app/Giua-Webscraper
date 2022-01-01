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

package com.giua.objects;

import com.giua.webscraper.GiuaScraper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Vector;

public class Alert {
    public String status;
    public final String date;
    public final String receivers;
    public final String object;
    public final int page;  //La pagina in cui si trova questo avviso
    public final String detailsUrl;
    public String details;
    public String creator;
    public String type;
    public List<String> attachmentUrls;
    public boolean isDetailed;  //Indica se per questo avviso sono stati caricati i dettagli
    public final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //es. 2021-10-22

    public Alert(String status, String date, String receivers, String object, String detailsUrl, int page) {
        this.status = status;
        this.date = date;
        this.receivers = receivers;
        this.object = object;
        this.page = page;
        this.detailsUrl = detailsUrl;
        this.isDetailed = false;
    }

    public Alert(String status, String date, String receivers, String object, String detailsUrl, int page, List<String> attachmentUrls, String details, String creator, String type) {
        this.status = status;
        this.date = date;
        this.receivers = receivers;
        this.object = object;
        this.page = page;
        this.detailsUrl = detailsUrl;
        this.isDetailed = true;
        this.attachmentUrls = attachmentUrls;
        this.details = details;
        this.creator = creator;
        this.type = type;
    }

    /**
     * Ottiene i dettagli, il tipo e il creatore dell'avviso con una richiesta HTTP
     *
     * @param gS istanza di {@link GiuaScraper} con il login già effettuato
     * @return Una Stringa contenente i dettagli dell'avviso
     */
    public String getDetailsToString(GiuaScraper gS) {
        if (!this.isDetailed) {
            Document detailsHTML = gS.getPage(detailsUrl);
            this.attachmentUrls = new Vector<>();
            this.details = detailsHTML.getElementsByClass("gs-text-normal").get(0).text();
            this.creator = detailsHTML.getElementsByClass("text-right gs-text-normal").get(0).text();

            Elements els = detailsHTML.getElementsByClass("gs-mt-2");
            this.type = "";        //Se nessuna delle prossime condizioni viene rispettata allora alertType vale una stringa vuota
            if (els.size() == 3 && els.get(2).text().split(": ").length > 1)
                this.type = els.get(2).text().split(": ")[1];
            else if (els.size() == 2 && els.get(1).text().split(": ").length > 1)
                this.type = els.get(1).text().split(": ")[1];

            Elements attachmentsHTML = detailsHTML.getElementsByClass("gs-ml-3");
            for (Element attachmentHTML : attachmentsHTML)
                this.attachmentUrls.add(attachmentHTML.attr("href"));

            this.isDetailed = true;
        }
        return this.details;
    }

    /**
     * Ottiene i dettagli, il tipo e il creatore dell'avviso con una richiesta HTTP
     *
     * @param gS istanza di {@link GiuaScraper} con il login già effettuato
     * @return Una Stringa contenente i dettagli dell'avviso
     */
    public String getDetails(GiuaScraper gS) {
        if (!this.isDetailed) {
            Document detailsHTML = gS.getPage(detailsUrl);
            this.attachmentUrls = new Vector<>();
            this.details = detailsHTML.getElementsByClass("gs-text-normal").get(0).html();
            this.creator = detailsHTML.getElementsByClass("text-right gs-text-normal").get(0).text();

            Elements els = detailsHTML.getElementsByClass("gs-mt-2");
            this.type = "";        //Se nessuna delle prossime condizioni viene rispettata allora alertType vale una stringa vuota
            if (els.size() == 3 && els.get(2).text().split(": ").length > 1)
                this.type = els.get(2).text().split(": ")[1];
            else if (els.size() == 2 && els.get(1).text().split(": ").length > 1)
                this.type = els.get(1).text().split(": ")[1];

            Elements attachmentsHTML = detailsHTML.getElementsByClass("gs-ml-3");
            for (Element attachmentHTML : attachmentsHTML)
                this.attachmentUrls.add(attachmentHTML.attr("href"));

            this.isDetailed = true;
        }
        return this.details;
    }

    public String getStatus() {
        return this.status;
    }

    /**
     * Modifica lo status assegnandogli il valore "LETTA"
     */
    public void markAsRead() {
        this.status = "LETTO";
    }

    public boolean isRead() {
        return this.status.equals("LETTO");
    }

    public String toString() {
        if (!this.isDetailed)
            return this.status + "; " + this.date + "; " + this.receivers + "; " + this.object;
        else
            return this.status + "; " + this.date + "; " + this.receivers + "; " + this.object + "; " + this.creator + "; " + this.details + "; " + this.type;
    }

    /**
     * Ritorna l' oggetto come stringa senza specificare lo stato. Serve SOLO per le notifiche.
     */
    public String toStringWithoutStatus() {
        if (!this.isDetailed)
            return this.date + "; " + this.receivers + "; " + this.object;
        else
            return this.date + "; " + this.receivers + "; " + this.object + "; " + this.creator + "; " + this.details + "; " + this.type;
    }

    public String toJSON() {
/*
        JsonBuilder jsonBuilder = new JsonBuilder("[{")
                .addValue("status", this.status)
                .addValue("date", this.date)
                .addValue("receivers", this.receivers)
                .addValue("object", (!this.object.isEmpty() ? JsonBuilder.escape(this.object) : ""))
                .addValue("page", this.page)
                .addValue("detailsUrl", this.detailsUrl)
                .addValue("details", (this.details != null ? JsonBuilder.escape(this.details) : ""))
                .addValue("creator", this.creator)
                .addValue("type", this.type)
                .addValue("isDetailed", this.isDetailed)
                .addCustomString(",\"attachmentUrls\":[");

        if (this.attachmentUrls != null && !this.attachmentUrls.isEmpty()) {
            //aggiungi attachments

            jsonBuilder.addCustomString("\"" + this.attachmentUrls.get(0) + "\"");

            for (int i = 1; i < this.attachmentUrls.size(); i++) {
                jsonBuilder.addCustomString(",\"" + this.attachmentUrls.get(i) + "\"");
            }
        }

        return jsonBuilder.build("]}]");*/
        return "";
    }

}
