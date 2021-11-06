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

import com.giua.utils.GiuaScraperUtils;

public class DisciplinaryNotices {
    public final String date;
    public final String type;
    public final String details;
    public final String countermeasures;
    public final String authorOfDetails;
    public final String authorOfCountermeasures;
    public final String quarterly;

    public DisciplinaryNotices(String date, String type, String details, String countermeasures, String authorOfDetails, String authorOfCountermeasures, String quarter) {
        this.date = date;
        this.type = type;
        this.details = details;
        this.countermeasures = countermeasures;
        this.authorOfDetails = authorOfDetails;
        this.authorOfCountermeasures = authorOfCountermeasures;
        this.quarterly = quarter;
    }

    public int quarterlyToInt() {
        return GiuaScraperUtils.quarterlyToInt(quarterly);
    }

    public String toString() {
        return this.date + " ; " + this.type + " ; " + this.authorOfDetails + ": " + this.details + " ; " + this.authorOfCountermeasures + ": " + this.countermeasures;
    }
}
