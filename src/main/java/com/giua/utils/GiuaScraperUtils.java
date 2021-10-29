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

import com.giua.objects.Homework;
import com.giua.objects.Test;

import java.util.List;
import java.util.Vector;

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


    /**
     * Fa il confronto tra due homework e restituisce gli homework diversi/nuovi
     * <p>
     * Attenzione: per evitare di spammare il sito con richieste, questa
     * funzione non prende i dettagli dei homework, quindi non può distinguere
     * tra più homework nello stesso giorno
     *
     * @param oldHomework Homeworks vecchi con cui controllare
     * @param newHomework Homeworks nuovi
     * @return Una lista di homework diversi/nuovi
     */
    public List<Homework> compareHomeworks(List<Homework> oldHomework, List<Homework> newHomework) {
        List<Homework> homeworkDiff = new Vector<>();

        if (!oldHomework.get(0).month.equals(newHomework.get(0).month) && !oldHomework.get(1).month.equals(newHomework.get(1).month)) {
            //logln("Il mese dei compiti è diverso!");
        }


        for (int i = 0; i < newHomework.size(); i++) {
            try {
                if (!newHomework.get(i).day.equals(oldHomework.get(i).day) && !newHomework.get(i).date.equals(oldHomework.get(i).date)) {
                    homeworkDiff.add(newHomework.get(i));
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                homeworkDiff.add(newHomework.get(i));
            }
        }

        return homeworkDiff;
    }

    /**
     * Fa il confronto tra due test e restituisce i test diversi/nuovi
     * <p>
     * Attenzione: per evitare di spammare il sito con richieste, questa
     * funzione non prende i dettagli dei test, quindi non può distinguere
     * tra più test nello stesso giorno
     *
     * @param oldTest Test vecchi con cui controllare
     * @param newTest Test nuovi
     * @return Una lista di test diversi/nuovi
     */
    public List<Test> compareTests(List<Test> oldTest, List<Test> newTest) {
        List<Test> testDiff = new Vector<>();

        if (!oldTest.get(0).month.equals(newTest.get(0).month) && !oldTest.get(1).month.equals(newTest.get(1).month)) {
            //logln("Il mese delle verifiche è diverso!");
        }


        for (int i = 0; i < newTest.size(); i++) {
            try {
                if (!newTest.get(i).day.equals(oldTest.get(i).day) && !newTest.get(i).date.equals(oldTest.get(i).date)) {
                    testDiff.add(newTest.get(i));
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                testDiff.add(newTest.get(i));
            }
        }

        return testDiff;
    }
}
