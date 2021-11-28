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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Lesson {
    public final Date date;
    public final String time;
    public final String subject;
    public final String arguments;
    public final String activities;
    public final boolean exists;
    public final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //es. 2021-10-22
    public final boolean isError;

    public Lesson(Date date, String time, String subject, String arguments, String activities, boolean exists) {
        this.date = date;
        this.time = time;
        this.subject = subject;
        this.arguments = arguments;
        this.activities = activities;
        this.exists = exists;
        isError=false;
    }

    public Lesson(Date date, String time, String subject, String arguments, String activities, boolean exists, boolean isError) {
        this.date = date;
        this.time = time;
        this.subject = subject;
        this.arguments = arguments;
        this.activities = activities;
        this.exists = exists;
        this.isError=isError;
    }

    public Lesson(String date, String time, String subject, String arguments, String activities, boolean exists) {
        try {
            this.date = dateFormat.parse(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Impossibile fare parsing della stringa per una data", e);
        }
        this.time = time;
        this.subject = subject;
        this.arguments = arguments;
        this.activities = activities;
        this.exists = exists;
        isError=false;
    }

    public String toString() {
        return dateFormat.format(date) + "; " + this.time + "; " + this.subject + "; " + this.arguments + "; " + this.activities + "; Esiste?" + this.exists + "; Errore?" + this.isError;
    }

    public String getDateToString() {
        return dateFormat.format(date);
    }

}
