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
import java.util.Date;

public class Maintenance{
    public Date start;
    public Date end;
    public Boolean isActive;
    public Boolean shouldBeActive;
    public Boolean exist;

    public Maintenance(Date start, Date end, Boolean isActive, Boolean shouldBeActive, Boolean exist) {
        this.start = start;
        this.end = end;
        this.isActive = isActive;
        this.shouldBeActive = shouldBeActive;
        this.exist = exist;
    }

    public String toString() {
        if(this.exist == false){
            return "Esiste? " + this.exist + " Inizio: " + "null" + " Fine: " + "null " +
                    " In corso? " + this.isActive + " Dovrebbe essere in corso? " + this.shouldBeActive;
        }

        return "Esiste? " + this.exist + " Inizio: " + this.start.toString() + " Fine: " + this.end.toString() +
                " In corso? " + this.isActive + " Dovrebbe essere in corso? " + this.shouldBeActive;
    }
}
