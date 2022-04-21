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

import java.util.List;

public class Newsletter{

    private String status;
    public final String date;
    public final String object;
    public final String detailsUrl;
    public final int number;
    public final List<String> attachmentsUrl;
    public final int page;

    public Newsletter(String status, int number, String date, String object, String detailsUrl, List<String> attachmentsUrl, int page) {
        this.status = status;
        this.date = date;
        this.object = object;
        this.detailsUrl = detailsUrl;
        this.number = number;
        this.attachmentsUrl = attachmentsUrl;
        this.page = page;
    }

    public String getStatus() {
        return this.status;
    }

    public boolean isRead() {
        return this.status.equals("LETTA");
    }

    /**
     * Modifica lo status assegnandogli il valore "LETTA"
     */
    public void markAsRead() {
        this.status = "LETTA";
    }

    public String toString() {
        return this.status + "; " + this.number + "; " + this.date + "; " + this.object + "; " + this.detailsUrl + "; " + ((this.attachmentsUrl != null && !this.attachmentsUrl.isEmpty()) ? this.attachmentsUrl.get(0) : "null");
    }

    @Override
    public boolean equals(Object obj) {
        boolean isInstanceOfNewsletter = obj instanceof Newsletter;

        if (!isInstanceOfNewsletter) return false;

        Newsletter otherNewsletter = (Newsletter) obj;

        return this.number == otherNewsletter.number;
    }
}
