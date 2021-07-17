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

import java.io.Serializable;

public class Vote implements Serializable {
    public final String value;
    public final boolean isFirstQuarterly;
    public final boolean isAsterisk;
    public final String date;
    public final String judgement;
    public final String testType;
    public final String arguments;

    public Vote(String value, String date, String testType, String arguments, String judgement, boolean isFirstQuarterly, boolean isAsterisk) {
        this.value = value;
        this.date = date;
        this.testType = testType;
        this.arguments = arguments;
        this.judgement = judgement;
        this.isFirstQuarterly = isFirstQuarterly;
        this.isAsterisk = isAsterisk;
    }

    //Mette anche i dettagli nella stringa
    public String allToString() {
        if (this.isAsterisk) {
            return "*; " + this.date + "; " + this.testType + "; " + this.arguments + "; " + this.judgement;
        } else {
            return this.value + "; " + this.date + "; " + this.testType + "; " + this.arguments + "; " + this.judgement;
        }
    }

    public String toString(){
        return (this.isAsterisk) ? "*" : this.value;
    }
}
