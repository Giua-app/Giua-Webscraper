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

public class AgendaObject {
    public String day;        //usato per trovare quale verifica interessa
    public String month;
    public String year;
    public String date;

    public AgendaObject(String day, String month, String year, String date) {
        this.date = date;
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public String toString() {
        return "date: " + date + " ; " + "day: " + day + " ; " + "month: " + month + " ; " + "year: " + year;
    }

    public Class getRepresentingClass() {
        return this.getClass();
    }
}
