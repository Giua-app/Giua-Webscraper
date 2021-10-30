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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class LoggerManager {
    protected String tag;
    protected List<Log> logs;
    public String filePath;
    protected SimpleDateFormat logDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public LoggerManager(String tag) {
        this.tag = tag;
        logs = new Vector<>();
        filePath = "out.log";
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
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));

            writer.append(log.toString());

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Log> getLogs() {
        return logs;
    }

    //$ - categoria
    //# - fine log
    // Esempio: tag$tipo$date$text#
    public void parseLogsFrom(String logs) {
        String[] logsOb = logs.split("#");


        for (String s : logsOb) {
            String[] logsSub = s.split("\\$");

            try {
                this.logs.add(new Log(logsSub[0], logsSub[1], logDateFormat.parse(logsSub[2]), logsSub[3]));
            } catch (Exception e) {
                e.printStackTrace();
                //android.util.Log.e("LogDog", "ERRORE CRITICO! LOGDOG PARSING FALLITO", e);
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
            return this.tag + "$" + this.type + "$" + logDateFormat.format(this.date) + "$" + this.text + "#";
        }
    }

    public interface OnSaveLogListener {
        void onSaveLog(Log log);
    }
}


