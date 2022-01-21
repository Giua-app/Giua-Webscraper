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

package com.giua.utils;

import com.giua.objects.Homework;
import com.giua.objects.Test;
import com.giua.objects.Vote;
import com.giua.webscraper.GiuaScraper;

import java.net.MalformedURLException;
import java.net.URL;
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
     * Ottieni il nome del quadrimestre dal suo numero. Es:
     * n vale 1 output vale "Primo Quadrimestre"
     *
     * @param n Il numero del quadrimestre (parte da 1 e arriva sino a 5)
     * @return Una stringa con il nome del quadrimestre. Se il numero non viene riconosciuto viene restituita una
     * stringa vuota. Es: "Primo quadrimestre"
     */
    public static String getQuarterName(int n) {
        switch (n) {
            case 1:
                return "Primo Quadrimestre";
            case 2:
                return "Secondo Quadrimestre";
            case 3:
                return "Terzo Quadrimestre";
            case 4:
                return "Quarto Quadrimestre";
            case 5:
                return "Quinto Quadrimestre";
            default:
                return "";
        }
    }

    /**
     * Ottieni il nome del quadrimestre dal suo numero. Es:
     * n vale 1 output vale "1° Quadrimestre"
     *
     * @param n Il numero del quadrimestre (parte da 1 e arriva sino a 5)
     * @return Una stringa con il nome del quadrimestre e il suo numero. Se il numero in input non viene riconosciuto viene restituita una
     * stringa vuota. Es: "1° quadrimestre"
     */
    public static String getQuarterNameWithNumbers(int n) {
        switch (n) {
            case 1:
                return "1° Quadrimestre";
            case 2:
                return "2° Quadrimestre";
            case 3:
                return "3° Quadrimestre";
            case 4:
                return "4° Quadrimestre";
            case 5:
                return "5° Quadrimestre";
            default:
                return "";
        }
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
    public static List<Homework> compareHomeworks(List<Homework> oldHomework, List<Homework> newHomework) {
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
    public static List<Test> compareTests(List<Test> oldTest, List<Test> newTest) {
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

    /**
     * Ottieni una lista di {@link Vote} contenente le differenze delle due liste
     *
     * @param firstList  La prima lista
     * @param secondList La seconda lista
     * @return La lista di {@link Vote} con i {@link Vote} diversi
     */
    public static List<Vote> compareListVote(List<Vote> firstList, List<Vote> secondList) {
        List<Vote> difference = new Vector<>();

        for (Vote vote : secondList) {
            if (!firstList.contains(vote))
                difference.add(vote);
        }

        return difference;
    }

    /**
     * Ottieni una lista di {@code int} contenente gli indici della seconda lista in cui è stata trovata una differenza
     * con la prima lista
     *
     * @param firstList  La prima lista
     * @param secondList La seconda lista
     * @return La lista di {@code int} contenente gli indici
     */
    public static List<Integer> getListVoteIndexDifferences(List<Vote> firstList, List<Vote> secondList) {
        List<Integer> difference = new Vector<>();

        int index = 0;
        for (Vote vote : secondList) {
            if (!firstList.contains(vote))
                difference.add(index);
            index++;
        }

        return difference;
    }

    /**
     * Converte i caratteri considerati illegali in JSON o altro
     *
     * @param raw String da convertire
     * @return String con caratteri convertiti
     */
    public static String escapeString(String raw) {
        String escaped = raw;
        escaped = escaped.replace("\\", "\\\\");
        escaped = escaped.replace("\"", "\\\"");
        escaped = escaped.replace("\b", "\\b");
        escaped = escaped.replace("\f", "\\f");
        escaped = escaped.replace("\n", "\\n");
        escaped = escaped.replace("\r", "\\r");
        escaped = escaped.replace("\t", "\\t");
        //escaped = escaped.replace("\"", "\\u0022");
        return escaped;
    }

    /**
     * Converte i path di un url da path globale a locale<br>
     * <br>
     * Con path globale si intende il path usato per raggiungere il
     * registro elettronico.<br>Esempio: https://registro.com/public/circolari/genitori in questo caso
     * "/public/circolari/genitori" è il path globale
     * <br>
     * Con path locale si intende il path usato per raggiungere le varie pagine del registro<br>
     * Esempio: https://registro.com/public/circolari/genitori in questo caso "/circolari/genitori" è il path locale
     * <br><br>
     * <p>
     * Esempi d'uso di questa funzione:<br>
     * In ogni esempio, il path globale è "/public"<br>
     * 1)
     * <blockquote><pre>
     *     String url = "https://registro.com/public/circolari/genitori"
     *     String path = convertGlobalPathToLocal(url)
     * </pre>
     * </blockquote>
     * path equivale "circolari/genitori"<br><br>
     * 2)
     * <blockquote><pre>
     *     String url = "/public/circolari/genitori"
     *     String path = convertGlobalPathToLocal(url)
     * </pre>
     * </blockquote>
     * path equivale "circolari/genitori"<br><br>
     * 3)
     * <blockquote><pre>
     *     String url = "/public/circolari/genitori"
     *     String path = convertGlobalPathToLocal(url)
     * </pre>
     * </blockquote>
     * path equivale "circolari/genitori"<br><br>
     * 4)
     * <blockquote><pre>
     *     String url = "/public/circolari/genitori"
     *     String path = convertGlobalPathToLocal(url, false)
     * </pre>
     * </blockquote>
     * path equivale "/circolari/genitori"<br><br>
     *
     * @param url               url da convertire, può essere parziale (es. /public/voti) oppure completo (es. https://registro.com/public/voti)
     * @param shouldRemoveSlash indica se la funzione deve rimuovere gli slash ("/") all'inizio del path una volta convertito.
     *                          Se non indicato verrà eseguita la funzione {@link #convertGlobalPathToLocal(String)}
     * @return {@code String} con path locale
     */
    public static String convertGlobalPathToLocal(String url, boolean shouldRemoveSlash) {
        if (url.startsWith("/"))
            url = url.substring(1);

        String globalPath = null;
        try {
            globalPath = new URL(GiuaScraper.getSiteURL()).getPath();
        } catch (MalformedURLException e) {
            //NON DOVREBBE MAI SUCCEDERE
            //Se succede vuol dire che SiteURL non è un URL valido!
        }

        if (!globalPath.equals("") && url.startsWith(globalPath.substring(1)))
            url = url.split(globalPath.substring(1))[1];


        if (shouldRemoveSlash && url.startsWith("/"))
            url = url.substring(1);

        return url;
    }

    /**
     * Converte i path di un url da path globale a locale.
     * Questa funzione di default rimuove gli slash ("/") all'inizio del path una volta convertito
     *
     * <p>Per maggiori info e istruzioni vedere {@link #convertGlobalPathToLocal(String, boolean)}</p>
     *
     * @param url url da convertire, può essere parziale (es. /public/voti) oppure completo (es. https://registro.com/public/voti)
     * @return {@code String} con path locale
     * @see #convertGlobalPathToLocal(String, boolean)
     */
    public static String convertGlobalPathToLocal(String url) {
        return convertGlobalPathToLocal(url, true);
    }
}
