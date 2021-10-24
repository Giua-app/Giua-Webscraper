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

package com.giua.utils;

public class GiuaScraperUtils {

    /**
     * Ottieni il numero del quadrimestre dalla stringa.
     * Esempio: quarter:"Primo quadrimestre" return:1
     *
     * @param quarter il quadrimestre
     * @return numero da 1 a 5 (estremi compresi) se il quadrimestre viene riconosciuto, -1 altrimenti
     */
    public static int quarterlyToInt(String quarter) {
        if (quarter.contains("Primo"))
            return 1;
        if (quarter.contains("Secondo"))
            return 2;
        if (quarter.contains("Terzo"))
            return 3;
        if (quarter.contains("Quarto"))
            return 4;
        if (quarter.contains("Quinto"))
            return 5;
        return -1;
    }
}
