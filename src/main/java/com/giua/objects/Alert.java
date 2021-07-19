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

package com.giua.objects;

import com.giua.webscraper.GiuaScraper;
import org.jsoup.nodes.Document;

import java.io.Serializable;

public class Alert{
    public final String status;
    public final String date;
    public final String receivers;
    public final String objectAvviso;
    public String details;
    public String creator;
    public final int page;
    public final int id;        //Indica quanto e' lontano dal primo avviso partendo dall'alto
    public boolean isDetailed;

    public Alert(String status, String data, String destinatari, String oggetto, int id, int page) {
        this.status = status;
        this.date = data;
        this.receivers = destinatari;
        this.objectAvviso = oggetto;
        this.id = id;
        this.page = page;
        this.isDetailed = false;
    }

    public String getDetails(GiuaScraper gS) {        //carica i dettagli e l'autore dell'avviso simulando il click su Visualizza
        Document allAvvisiHTML = gS.getPage("genitori/avvisi/" + page);
        Document dettagliAvvisoHTML;
        if (isRead())
            dettagliAvvisoHTML = gS.getPage(allAvvisiHTML.getElementsByClass("label label-default").get(this.id).parent().parent().child(4).child(0).attributes().get("data-href").substring(1));
        else
            dettagliAvvisoHTML = gS.getPage(allAvvisiHTML.getElementsByClass("label label-warning").get(this.id).parent().parent().child(4).child(0).attributes().get("data-href").substring(1));

        this.details = dettagliAvvisoHTML.getElementsByClass("gs-text-normal").get(0).text();
        this.creator = dettagliAvvisoHTML.getElementsByClass("text-right gs-text-normal").get(0).text();
        this.isDetailed = true;
        return this.details;
    }

    public boolean isRead() {
        return this.status.equals("LETTO");
    }

    public String toString() {
        if (!this.isDetailed)
            return this.status + "; " + this.date + "; " + this.receivers + "; " + this.objectAvviso;
        else
            return this.status + "; " + this.date + "; " + this.receivers + "; " + this.objectAvviso + "; " + this.creator + "; " + this.details;
    }
}
