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
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.Serializable;

public class Absence implements Serializable {
    public String date;
    public String type;
    public String notes;
    public Boolean isJustified;
    public String justifyUrl;


    public Absence(String date, String type, String notes, Boolean isJustified, String justifyUrl) {
        this.date = date;
        this.type = type;
        this.notes = notes;
        this.isJustified = isJustified;
        this.justifyUrl = justifyUrl;
    }

    public String toString() {
        return this.date + " ; " + this.type + " ; " + this.notes + " ; Gia Giustificato? " + this.isJustified + " ; " + this.justifyUrl;
    }

}
