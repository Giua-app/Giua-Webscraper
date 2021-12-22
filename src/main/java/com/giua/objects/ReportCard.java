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

import java.util.List;
import java.util.Map;

public class ReportCard {
    public String quarterly;
    public String finalResult;
    public String credits;

    /**
     * Una map la cui chiave è la materia e come contenuto una lista di stringhe contente all'indice 0 il voto e all'indice 1 le ore di assenza
     **/
    public  Map<String, List<String>> allVotes;
    /**
     * Una map la cui chiave è la materia e come contenuto una lista di stringhe contente all'indice 0 gli argomenti da recuperare e all'indice 1 la modalità di recupero
     **/
    public  Map<String, List<String>> allDebts;

    public final boolean exists;
    public String mean;

    public ReportCard(String quarterly, Map<String, List<String>> allVotes, String finalResult, String credits, Map<String, List<String>> allDebts, String mean, boolean exists) {
        this.quarterly = quarterly;
        this.allVotes = allVotes;
        this.exists = exists;
        this.finalResult = finalResult;
        this.credits = credits;
        this.allDebts=allDebts;
        this.mean=mean;
    }

    public String toString() {
        return "Quadrimestre: "+quarterly+"; \r\n" +
                "Voti: "+allVotes+"; \r\n" +
                "Esito: "+finalResult+"; \r\n" +
                "Crediti: "+credits+"; \r\n" +
                "Media: "+mean+"; \r\n" +
                "Debiti: "+allDebts+"; \r\n" +
                "Esiste: "+exists+"; \r\n";
    }
}
