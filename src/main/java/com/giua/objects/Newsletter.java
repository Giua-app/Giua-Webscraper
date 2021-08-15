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

import java.util.List;

public class Newsletter{

    public final String status;
    public final String date;
    public final String newslettersObject;
    public final String detailsUrl;
    public final String number;
    public final List<String> attachments;
    public final int page;

    public Newsletter(String status, String number, String date, String newslettersObject, String detailsUrl, List<String> attachments, int page) {
        this.status = status;
        this.date = date;
        this.newslettersObject = newslettersObject;
        this.detailsUrl = detailsUrl;
        this.number = number;
        this.attachments = attachments;
        this.page = page;
    }

    public boolean isRead() {
        return this.status.equals("LETTA");
    }

    public String toString() {
        return this.status + "; " + this.number + "; " + this.date + "; " + this.newslettersObject + "; " + this.detailsUrl + "; " + ((this.attachments != null) ? this.attachments.get(0) : "null");
    }

    /**
     * Le differenze possibili delle {@code Newsletter} sono:
     * status, date, object, detailsUrl, number, attachments, page
     *
     * @param newsletter2 Newsletter da confrontare
     * @return Una lista delle differenze
     */
    /*public List<String> compareWith(Newsletter newsletter2) {
        List<String> differences = new Vector<>();

        if(this.equals(newsletter2)){
            return differences;
        }

        if(!this.status.equals(newsletter2.status)){
            differences.add("status");
        }
        if(!this.date.equals(newsletter2.date)){
            differences.add("date");
        }
        if(!this.newslettersObject.equals(newsletter2.newslettersObject)){
            differences.add("object");
        }
        if(!this.detailsUrl.equals(newsletter2.detailsUrl)){
            differences.add("detailsUrl");
        }
        if(!this.number.equals(newsletter2.number)){
            differences.add("number");
        }
        if(this.attachments != newsletter2.attachments){
            differences.add("attachments");
        }
        if(this.page != newsletter2.page){
            differences.add("page");
        }
        return differences;
    }*/
    public boolean equals(Newsletter newsletter2) {
        //true se uguale, false altrimenti
        return this.status.equals(newsletter2.status) && this.number.equals(newsletter2.number)
                && this.date.equals(newsletter2.date) && this.newslettersObject.equals(newsletter2.newslettersObject)
                && this.detailsUrl.equals(newsletter2.detailsUrl) && this.attachments == newsletter2.attachments
                && this.page == newsletter2.page;
    }

    public String toJSON(){

        StringBuilder ris = new StringBuilder("[{" +
                "\"status\":\"" + this.status + "\"" +
                ",\"date\":\"" + this.date + "\"" +
                ",\"object\":\"" + GiuaScraper.escape(this.newslettersObject) + "\"" +
                ",\"detailsUrl\":\"" + this.detailsUrl + "\"" +
                ",\"number\":" + this.number +
                ",\"page\":" + this.page +
                ",\"attachments\":[");

        if(this.attachments != null){
            //aggiungi attachments

            ris.append("\"").append(this.attachments.get(0)).append("\"");

            for(int i=1;i < this.attachments.size();i++){
                ris.append(",\"")
                        .append(this.attachments.get(i))
                        .append("\"");
            }
        }


        ris.append("]}]");

        return ris.toString();
    }
}
