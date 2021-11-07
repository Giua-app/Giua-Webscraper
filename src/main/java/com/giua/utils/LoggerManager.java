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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class LoggerManager {
    protected String tag;
    protected List<Log> logs;
    protected SimpleDateFormat logDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public LoggerManager(String tag) {
        this.tag = tag;
        logs = new Vector<>();
    }

    public void d(String text) {
        Date now = Calendar.getInstance().getTime();
        Log log = new Log(tag, "DEBUG", now, text);
        logs.add(log);
        saveToData(log);
    }

    public void w(String text) {
        Date now = Calendar.getInstance().getTime();
        Log log = new Log(tag, "WARNING", now, text);
        logs.add(log);
        saveToData(log);
    }

    public void e(String text) {
        Date now = Calendar.getInstance().getTime();
        Log log = new Log(tag, "ERROR", now, text);
        logs.add(log);
        saveToData(log);
    }

    protected void saveToData(Log log) {
        String color = "";

        if (log.type.equals("WARNING"))
            color = "\u001B[33m";
        if (log.type.equals("ERROR"))
            color = "\u001B[31m";

        System.out.println(log.tag + " | " + color + log.type + ": " + log.text + "\u001B[0m");
    }

    public List<Log> getLogs() {
        return logs;
    }

    //$ - categoria
    //# - fine log

    /**
     * Converte una stringa di Logs in dei {@link Log} e sovrascrive
     * lo storage interno dell'istanza {@link LoggerManager} corrente con i nuovi {@link Log}.
     * <br><br>
     * La stringa deve essere formattata come questo esempio: <br>
     * {@code TAG$TIPO$DATE$TESTO#} <br>
     * dove {@code $} indica la fine di una categoria
     * mentre {@code #} indica la fine del log
     *
     * @param logs dei {@link Log} formattati in stringa
     */
    public void parseLogsFrom(String logs) {
        String[] logsOb = logs.split("#"); //Separazione dei log

        this.logs = new Vector<>();
        for (String s : logsOb) {
            String[] logsSub = s.split("\\$"); //Separazione categorie

            logsSub[0] = Log.unescape(logsSub[0]);
            logsSub[1] = Log.unescape(logsSub[1]);
            logsSub[2] = Log.unescape(logsSub[2]);
            logsSub[3] = Log.unescape(logsSub[3]);

            try {
                this.logs.add(new Log(logsSub[0], logsSub[1], logDateFormat.parse(logsSub[2]), logsSub[3]));
            } catch (ParseException e) {
                this.logs.add(new Log("Logger Manager", "ERROR", new Date(), "Errore nella lettura della data sul prossimo log con tag: " + logsSub[0]));
                this.logs.add(new Log(logsSub[0], logsSub[1], new Date(0), logsSub[3]));
            } catch (Exception e) {
                this.logs.add(new Log("Logger Manager", "ERROR", new Date(), "Errore nella lettura del log: " + s));
            }

        }
    }

    public static class Log {
        public String tag;
        public String type;
        public Date date;
        public String text;
        SimpleDateFormat logDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        public Log(String tag, String type, Date date, String text) {
            this.tag = tag;
            this.type = type;
            this.date = date;
            this.text = text;
        }

        @Override
        public String toString() {
            return escape(this.tag) + "$" + escape(this.type) + "$" + escape(logDateFormat.format(this.date)) + "$" + escape(this.text) + "#";
        }

        public static String escape(String raw) {
            String escaped = raw;
            escaped = escaped.replace("$", "\\u0024");
            escaped = escaped.replace("#", "\\u0023");
            return escaped;
        }

        public static String unescape(String escaped) {
            String unescaped = escaped;
            unescaped = unescaped.replace("\\u0024", "$");
            unescaped = unescaped.replace("\\u0023", "#");
            return unescaped;
        }

    }
}


