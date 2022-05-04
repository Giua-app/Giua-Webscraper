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

package com.giua.objects;

public class Vote{
    public final String value;
    public final int quarterly;
    public final boolean isAsterisk;
    public final String date;
    public final String judgement;
    public final String testType;
    public final String arguments;
    public final boolean isRelevantForMean;

    public Vote(String value, String date, String testType, String arguments, String judgement, int quarterly, boolean isAsterisk, boolean isRelevantForMean) {
        this.value = value;
        this.date = date;
        this.testType = testType;
        this.arguments = arguments;
        this.judgement = judgement;
        this.quarterly = quarterly;
        this.isAsterisk = isAsterisk;
        this.isRelevantForMean = isRelevantForMean;
    }

    //Mette anche i dettagli nella stringa
    public String allToString() {
        if (this.isAsterisk) {
            return "*; " + this.date + "; " + this.testType + "; " + this.arguments + "; " + this.judgement;
        } else {
            return this.value + "; " + this.date + "; " + this.testType + "; " + this.arguments + "; " + this.judgement;
        }
    }

    public String toString() {
        return (this.isAsterisk) ? "*" : this.value;
    }

    public float toFloat() {
        if (isAsterisk)
            return -1f;

        char lastChar = value.charAt(value.length() - 1);
        if (lastChar == '+')
            return (value.length() == 2) ? Character.getNumericValue(value.charAt(0)) + 0.15f : Integer.parseInt(value.substring(0, 2)) + 0.15f;

        else if (lastChar == '-')
            return (value.length() == 2) ? Character.getNumericValue(value.charAt(0)) - 1 + 0.85f : Integer.parseInt(value.substring(0, 2)) - 1 + 0.85f;

        else if (lastChar == '½')
            return (value.length() == 2) ? Character.getNumericValue(value.charAt(0)) + 0.5f : Integer.parseInt(value.substring(0, 2)) + 0.5f;

        else
            return Integer.parseInt(value);
    }

    @Override
    public boolean equals(Object obj) {
        boolean isInstanceOfVote = obj instanceof Vote;

        if (!isInstanceOfVote) return false;

        Vote otherVote = (Vote) obj;

        return value.equals(otherVote.value) &&
                date.equals(otherVote.date) &&
                quarterly == otherVote.quarterly &&
                testType.equals(otherVote.testType) &&
                arguments.equals(otherVote.arguments) &&
                judgement.equals(otherVote.judgement) &&
                isAsterisk == otherVote.isAsterisk &&
                isRelevantForMean == otherVote.isRelevantForMean;
    }
}
