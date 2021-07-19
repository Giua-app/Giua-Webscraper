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
import java.util.List;
import java.util.Map;

public class ReportCard{
    public final boolean isFirstQuarterly;
    public final Map<String, List<String>> allVotes;
    public final boolean exists;

    public ReportCard(boolean isFirstQuarterly, Map<String, List<String>> allVotes, boolean exists) {
        this.isFirstQuarterly = isFirstQuarterly;
        this.allVotes = allVotes;
        this.exists = exists;
    }

    public String toString() {
        return String.valueOf(isFirstQuarterly);
    }
}
