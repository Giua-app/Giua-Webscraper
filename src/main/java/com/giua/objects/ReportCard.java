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
    public final String quarterly;
    public final String finalResult;
    public final String credits;

    /**
     * Una map la cui chiave è la materia e come contenuto una lista di stringhe contente all'indice 0 il voto e all'indice 1 le ore di assenza
     **/
    public final Map<String, List<String>> allVotes;

    public final boolean exists;
    private float calculatedMean = -1f;

    public ReportCard(String quarterly, Map<String, List<String>> allVotes, String finalResult, String credits, boolean exists) {
        this.quarterly = quarterly;
        this.allVotes = allVotes;
        this.exists = exists;
        this.finalResult = finalResult;
        this.credits = credits;
    }

    public String toString() {
        return finalResult + "; " + credits + "; " + quarterly + "; " + exists;
    }

    /**
     * Ottieni la media dei voti calcolata.
     * ATTENZIONE: i giudizi (Es. Ottimo) non vengono contati nella media
     *
     * @return La media dei voti come un {@code float}
     */
    public float getCalculatedMean() {
        if (calculatedMean != -1f) {
            float mean = 0f;
            int i = 0;  //contatore dei voti reali non conta i giudizi

            for (String subject : allVotes.keySet()) {
                List<String> s = allVotes.get(subject);
                String vote = s.get(0);
                float voteF = getFloatFromVote(vote);
                if (voteF != -1f) {
                    mean += voteF;
                    i++;
                }
            }

            calculatedMean = mean / allVotes.keySet().size();
        }
        return calculatedMean;
    }

    private float getFloatFromVote(String vote) {
        char lastChar = vote.charAt(vote.length() - 1);
        if (lastChar == '+')
            return (vote.length() == 2) ? Character.getNumericValue(vote.charAt(0)) + 0.15f : Integer.parseInt(vote.substring(0, 2)) + 0.15f;

        else if (lastChar == '-')
            return (vote.length() == 2) ? Character.getNumericValue(vote.charAt(0)) - 1 + 0.85f : Integer.parseInt(vote.substring(0, 2)) - 1 + 0.85f;

        else if (lastChar == '\u00BD') //1 / 2
            return (vote.length() == 2) ? Character.getNumericValue(vote.charAt(0)) + 0.5f : Integer.parseInt(vote.substring(0, 2)) + 0.5f;

        else {
            try {
                return Integer.parseInt(vote);
            } catch (NumberFormatException e) {  //Il voto è un giudizio
                return -1f;
            }
        }
    }

    public int quarterlyToInt() {
        return GiuaScraperUtils.quarterlyToInt(quarterly);
    }
}
