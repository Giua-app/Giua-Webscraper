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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.giua.objects.*;
import com.giua.webscraper.GiuaScraper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class JsonBuilder {
    BufferedWriter writer;
    ObjectMapper objectMapper;
    JsonGenerator jsonGenerator;
    int jsonVer = 2;
    LoggerManager lm;

    /**
     * Il JsonBuilder ti permette di creare un json pronto a contenere dati dagli oggetti di GiuaScraper
     *
     * @param path Percorso dove scrivere il file (il file viene creato appena istanziata questa classe)
     * @param gS   Istanza {@link GiuaScraper} per ottenere il {@link LoggerManager}
     * @throws IOException se ci sono errori nella scrittura
     */
    public JsonBuilder(String path, GiuaScraper gS) throws IOException {
        objectMapper = new ObjectMapper();
        writer = new BufferedWriter(new FileWriter(path));
        lm = gS.getLoggerManager();
        initializeJsonGenerator();
    }

    /**
     * Serve a inizializzare il JsonGenerator e a scrivere la versione insieme alla data di creazione del json
     *
     * @throws IOException
     */
    private void initializeJsonGenerator() throws IOException {
        lm.d("Serializzazione json inizializzata. Versione: " + jsonVer);
        jsonGenerator = new JsonFactory().createGenerator(writer);
        jsonGenerator.enable(JsonWriteFeature.ESCAPE_NON_ASCII.mappedFeature());
        jsonGenerator.enable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
        jsonGenerator.setPrettyPrinter(new DefaultPrettyPrinter());

        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("version", jsonVer);
        Date calendar = Calendar.getInstance().getTime();
        jsonGenerator.writeStringField("create_date", calendar.toString());
    }

    /**
     * Salva il json nel file indicato e chiude JsonGenerator
     * ATTENZIONE: Una volta salvato, questa classe deve essere ricreata per generare altri json
     *
     * @throws IOException
     */
    public void saveJson() throws IOException {
        lm.d("Salvataggio json...");
        jsonGenerator.writeEndObject();
        jsonGenerator.flush();
        jsonGenerator.close();
        lm.d("Scrittura json su file completato con successo. Libero memoria");
        jsonGenerator = null;
        writer = null;
        objectMapper = null;
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


    public void writeNewsletters(List<Newsletter> newsletters) throws IOException {
        jsonGenerator.writeArrayFieldStart("newsletters");
        objectMapper.writeValue(jsonGenerator, newsletters);
        jsonGenerator.writeEndArray();
    }

    public void writeAlerts(List<Alert> alerts) throws IOException {
        jsonGenerator.writeArrayFieldStart("alerts");
        objectMapper.writeValue(jsonGenerator, alerts);
        jsonGenerator.writeEndArray();
    }

    public void writeVotes(Map<String, List<Vote>> votes) throws IOException {
        jsonGenerator.writeArrayFieldStart("votes");
        objectMapper.writeValue(jsonGenerator, votes);
        jsonGenerator.writeEndArray();
    }

    public void writeAbsences(List<Absence> absences) throws IOException {
        jsonGenerator.writeArrayFieldStart("absences");
        objectMapper.writeValue(jsonGenerator, absences);
        jsonGenerator.writeEndArray();
    }

    public void writeDisciplinaryNotices(List<DisciplinaryNotices> dn) throws IOException {
        jsonGenerator.writeArrayFieldStart("disciplinary_notices");
        objectMapper.writeValue(jsonGenerator, dn);
        jsonGenerator.writeEndArray();
    }

    public void writeDocuments(List<Document> documents) throws IOException {
        jsonGenerator.writeArrayFieldStart("documents");
        objectMapper.writeValue(jsonGenerator, documents);
        jsonGenerator.writeEndArray();
    }

    public void writeHomeworks(List<Homework> homeworks) throws IOException {
        jsonGenerator.writeArrayFieldStart("homeworks");
        objectMapper.writeValue(jsonGenerator, homeworks);
        jsonGenerator.writeEndArray();
    }

    public void writeLessons(List<Lesson> lessons) throws IOException {
        jsonGenerator.writeArrayFieldStart("lessons");
        objectMapper.writeValue(jsonGenerator, lessons);
        jsonGenerator.writeEndArray();
    }

    public void writeMaintenance(Maintenance maintenance) throws IOException {
        jsonGenerator.writeArrayFieldStart("maintenance");
        objectMapper.writeValue(jsonGenerator, maintenance);
        jsonGenerator.writeEndArray();
    }

    public void writeNews(List<News> news) throws IOException {
        jsonGenerator.writeArrayFieldStart("news");
        objectMapper.writeValue(jsonGenerator, news);
        jsonGenerator.writeEndArray();
    }

    public void writeObservations(List<Observations> observations) throws IOException {
        jsonGenerator.writeArrayFieldStart("observations");
        objectMapper.writeValue(jsonGenerator, observations);
        jsonGenerator.writeEndArray();
    }

    public void writeReportCards(List<ReportCard> reportCards) throws IOException {
        jsonGenerator.writeArrayFieldStart("report_cards");
        objectMapper.writeValue(jsonGenerator, reportCards);
        jsonGenerator.writeEndArray();
    }

    public void writeTests(List<Test> tests) throws IOException {
        jsonGenerator.writeArrayFieldStart("tests");
        objectMapper.writeValue(jsonGenerator, tests);
        jsonGenerator.writeEndArray();
    }

    public void writeAgendaObjects(List<AgendaObject> agendaObjects) throws IOException {
        jsonGenerator.writeArrayFieldStart("agenda_objects");
        objectMapper.writeValue(jsonGenerator, agendaObjects);
        jsonGenerator.writeEndArray();
    }
}
