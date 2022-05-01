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

import com.giua.utils.GiuaScraperUtils;

public class Observations {

    public final String date;
    public final String subject;
    public final String teacher;
    public final String observations;
    public final int quarterly;

    public Observations(String date, String subject, String teacher, String observations, int quarterly) {
        this.date = date;
        this.subject = subject;
        this.teacher = teacher;
        this.observations = observations;
        this.quarterly = quarterly;
    }

    public String toString() {
        return "Observations{" +
                "date='" + date + '\'' +
                ", subject='" + subject + '\'' +
                ", teacher='" + teacher + '\'' +
                ", observations='" + observations + '\'' +
                ", quarterly='" + quarterly + '\'' +
                '}';
    }
}
