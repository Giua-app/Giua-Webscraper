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

public class JsonBuilder {
    private String completeString = "";
    private boolean firstValue = true;

    public JsonBuilder(String start) {
        completeString = start;
    }

    /**
     * Aggiunge una riga al JSON con una copia nome-valore.
     *
     * @param name  Il nome a cui verrà associato {@code value}
     * @param value Un oggetto qualsiasi che verrà associato a {@code name} e che verrà trasformato in una stringa
     * @return Il {@code JsonBuilder} attuale
     */
    public JsonBuilder addValue(String name, Object value) {
        if (firstValue) {
            completeString += String.format("\"%s\":\"%s\"", name, value);
            firstValue = false;
        } else
            completeString += String.format(",\"%s\":\"%s\"", name, value);
        return this;
    }

    /**
     * Aggiungi una stringa qualsiasi al JSON.
     *
     * @param s La stringa da aggiungere
     * @return Il {@code JsonBuilder} attuale
     */
    public JsonBuilder addCustomString(String s) {
        completeString += s;
        return this;
    }

    /**
     * Costruisci la stringa JSON aggiungendo {@code end} come fine.
     *
     * @param end La stringa da aggiungere alla fine
     * @return La stringa JSON creata
     */
    public String build(String end) {
        return completeString + end;
    }

    public static String escape(String raw) {
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
}
