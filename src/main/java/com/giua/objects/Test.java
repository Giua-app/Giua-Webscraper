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

public class Test extends AgendaObject {
    public final String subject;
    public final String creator;
    public final String details;
    public final boolean exists;

    public Test(String day, String month, String year, String date, String subject, String creator, String details, boolean exists) {
        super(day, month, year, date);
        this.subject = subject;
        this.creator = creator;
        this.details = details;
        this.exists = exists;
    }

    public String toString() {
        return this.date + "; " + this.creator + "; " + this.subject + "; " + this.details + "; " + this.exists;
    }
}
