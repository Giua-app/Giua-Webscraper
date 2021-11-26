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

import com.giua.utils.JsonBuilder;

public class Homework {
    public final String day;        //usato per trovare quale compito interessa
    public final String month;
    public final String year;
    public final String date;
    public final String subject;
    public final String creator;
    public final String details;
    public final boolean exists;

    public Homework(String day, String month, String year, String date, String subject, String creator, String details, boolean exists) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.date = date;
        this.subject = subject;
        this.creator = creator;
        this.details = details;
        this.exists = exists;
    }

    public String toString() {
        return this.date + "; " + this.creator + "; " + this.subject + "; " + this.details + "; " + this.exists;
    }

    public boolean equals(Homework homework) {
        return this.day.equals(homework.day) && this.month.equals(homework.month)
                && this.year.equals(homework.year) && this.date.equals(homework.date)
                && this.subject.equals(homework.subject) && this.creator.equals(homework.creator)
                && this.details.equals(homework.details) && this.exists == homework.exists;
    }

    public String toJson() {
        return "";
        /*return new JsonBuilder("[{")
                .addValue("day", this.day)
                .addValue("month", this.month)
                .addValue("year", this.year)
                .addValue("date", this.date)
                .addValue("subject", this.subject)
                .addValue("creator", this.creator)
                .addValue("details", JsonBuilder.escape(this.details))
                .addValue("exists", this.exists)
                .build("}]");*/
    }

    /*public List<String> compare(Homework homework) {
        List<String> differences = new Vector<>();

        if(this.equals(homework)){
            return differences;
        }

        if(!this.day.equals(homework.day)){
            differences.add("day");
        }
        if(!this.month.equals(homework.month)){
            differences.add("month");
        }
        if(!this.year.equals(homework.year)){
            differences.add("year");
        }
        if(!this.date.equals(homework.date)){
            differences.add("date");
        }
        if(!this.subject.equals(homework.subject)){
            differences.add("subject");
        }
        if(!this.creator.equals(homework.creator)){
            differences.add("creator");
        }
        if(!this.details.equals(homework.details)){
            differences.add("details");
        }
        if(this.exists != homework.exists){
            differences.add("exists");
        }
        return differences;
    }*/

}