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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.giua.objects.*;
import com.giua.webscraper.GiuaScraper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class JsonHelper {

    static boolean debugMode;
    static int jsonVer = 2;
    static LoggerManager lm;


    public JsonHelper(GiuaScraper gS) {
        debugMode = GiuaScraper.getDebugMode();
        lm = gS.getLoggerManager();
    }

    public JsonHelper() {
        debugMode = GiuaScraper.getDebugMode();
        lm = new LoggerManager("JsonHelper");
    }

    private JsonNode getRootNode(String jsonData) {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode rootNode = null;
        try {
            rootNode = objectMapper.readTree(jsonData);
        } catch (IOException e) {
            lm.e("JsonHelper: Impossibile leggere json");
            e.printStackTrace();
        }
        int ver = Objects.requireNonNull(rootNode).findPath("version").asInt();
        if (ver != jsonVer) {
            lm.w("Versione json (" + ver + ") non è uguale al attuale versione (" + jsonVer + ")");
        }

        return rootNode;
    }


    //region FUNZIONI PER PARSING DI OGGETTI DA FILE O STRINGA JSON

    public List<Newsletter> parseJsonForNewsletters(Path path){

        String json = "";
        try {
            json = Files.readString(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return parseJsonForNewsletters(json);
    }

    public List<Newsletter> parseJsonForNewsletters(String json) {
        List<Newsletter> returnNewsletters = new Vector<>();
        Iterator<JsonNode> newsletters = getRootNode(json).findPath("newsletters").get(0).iterator();

        int i = 0;
        lm.d("De-serializzazione JSON per Newsletters in corso...");
        while (newsletters.hasNext()) {
            JsonNode newsletter = newsletters.next();

            String status = newsletter.findPath("status").asText();
            String date = newsletter.findPath("date").asText();
            String object = newsletter.findPath("object").asText();
            int number = newsletter.findPath("number").asInt();
            int page = newsletter.findPath("page").asInt();
            String details = newsletter.findPath("detailsUrl").asText();

            //Controlla se ci sono allegati
            Iterator<JsonNode> attachmentsNode = newsletter.findPath("attachments").elements();
            List<String> attachments = new Vector<>();
            while(attachmentsNode.hasNext()){
                attachments.add(attachmentsNode.next().asText());
            }


            returnNewsletters.add(new Newsletter(status, number, date, object, details, attachments, page));
            i++;
        }
        lm.d("De-serializzazione di " + i + " Newsletters completata");
        return returnNewsletters;
    }


    public Map<String, List<Vote>> parseJsonForVotes(Path path){

        String json = "";
        try {
            json = Files.readString(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return parseJsonForVotes(json);
    }

    public Map<String, List<Vote>> parseJsonForVotes(String json) {
        Map<String, List<Vote>> returnVote = new HashMap<>();
        JsonNode votesNode = getRootNode(json).findPath("votes").get(0);

        Iterator<String> subject = votesNode.fieldNames();

        int iSubject = 0;
        int iVotes = 0;
        lm.d("De-serializzazione JSON per Votes in corso...");
        while (subject.hasNext()) {
            String subjectName = subject.next();
            Iterator<JsonNode> subjectVotes = votesNode.get(subjectName).iterator();

            List<Vote> votes = new Vector<>();
            while (subjectVotes.hasNext()) {
                JsonNode vote = subjectVotes.next();

                String value = vote.findPath("value").asText();
                String quart = vote.findPath("Quarterly").asText();
                boolean isAsterisk = vote.findPath("isAsterisk").asBoolean();
                String date = vote.findPath("date").asText();
                String judgement = vote.findPath("judgement").asText();
                String type = vote.findPath("type").asText();
                String arguments = vote.findPath("arguments").asText();

                votes.add(new Vote(value, date, type, arguments, judgement, quart, isAsterisk));

                iVotes++;
            }
            iSubject++;
            returnVote.put(subjectName, votes);
        }
        lm.d("De-serializzazione di " + iSubject + " materie e " + iVotes + " voti completata");
        return returnVote;
    }


    public List<Alert> parseJsonForAlerts(Path path){

        String json = "";
        try {
            json = Files.readString(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return parseJsonForAlerts(json);
    }

    public List<Alert> parseJsonForAlerts(String json) {
        List<Alert> returnAlerts = new Vector<>();
        Iterator<JsonNode> alerts = getRootNode(json).findPath("alerts").get(0).iterator();

        int i = 0;
        //non dovrebbe MAI raggiungere 50 (perchè il massimo di avvisi in una pagina sono 19)
        //ma nel caso succeda qualcosa almeno ferma il loop
        lm.d("De-serializzazione JSON per Alerts in corso...");
        while (alerts.hasNext()) {
            JsonNode alert = alerts.next();

            String status = alert.findPath("status").asText();
            String date = alert.findPath("date").asText();
            String receivers = alert.findPath("receivers").asText();
            String object = alert.findPath("object").asText();
            int page = alert.findPath("page").asInt();
            String detailsUrl = alert.findPath("detailsUrl").asText();
            String details = alert.findPath("details").asText();
            String creator = alert.findPath("creator").asText();
            String type = alert.findPath("type").asText();
            boolean isDetailed = alert.findPath("isDetailed").asBoolean();



            if(!isDetailed){
                //Alert non dettagliata
                returnAlerts.add(new Alert(status, date, receivers, object, detailsUrl, page));
            } else {
                //Alert dettagliata

                //Controlla gli allegati
                Iterator<JsonNode> attachmentsNode = alert.findPath("attachmentUrls").elements();
                List<String> attachments = new Vector<>();
                while(attachmentsNode.hasNext()){
                    attachments.add(attachmentsNode.next().asText());
                }

                returnAlerts.add(new Alert(status, date, receivers, object, detailsUrl, page, attachments, details, creator, type));
            }

            i++;
        }
        lm.d("De-serializzazione di " + i + " Alerts completata");
        return returnAlerts;
    }


    public List<Absence> parseJsonForAbsences(Path path) {

        String json = "";
        try {
            json = Files.readString(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return parseJsonForAbsences(json);
    }

    public List<Absence> parseJsonForAbsences(String json) {
        List<Absence> returnAbsences = new Vector<>();
        Iterator<JsonNode> absences = getRootNode(json).findPath("absences").get(0).iterator();

        int i = 0;
        lm.d("De-serializzazione JSON per Absences in corso...");
        while (absences.hasNext()) {
            JsonNode absence = absences.next();

            String date = absence.findPath("date").asText();
            String type = absence.findPath("type").asText();
            String notes = absence.findPath("notes").asText();
            boolean isJustified = absence.findPath("isJustified").asBoolean();
            boolean isModificable = absence.findPath("isModificable").asBoolean();
            String justifyUrl = absence.findPath("justifyUrl").asText();


            returnAbsences.add(new Absence(date, type, notes, isJustified, isModificable, justifyUrl));

            i++;
        }
        lm.d("De-serializzazione di " + i + " Absences completata");
        return returnAbsences;
    }


    public List<Lesson> parseJsonForLessons(Path path) {

        String json = "";
        try {
            json = Files.readString(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return parseJsonForLessons(json);
    }

    public List<Lesson> parseJsonForLessons(String json) {
        List<Lesson> returnLessons = new Vector<>();
        Iterator<JsonNode> lessons = getRootNode(json).findPath("lessons").get(0).iterator();

        int i = 0;
        lm.d("De-serializzazione JSON per Lessons in corso...");
        while (lessons.hasNext()) {
            JsonNode lesson = lessons.next();

            Date date = new Date(lesson.findPath("date").asLong());
            String time = lesson.findPath("time").asText();
            String subject = lesson.findPath("subject").asText();
            String arguments = lesson.findPath("arguments").asText();
            String activities = lesson.findPath("activities").asText();
            boolean exist = lesson.findPath("exist").asBoolean();
            boolean isError = lesson.findPath("isError").asBoolean();

            returnLessons.add(new Lesson(date, time, subject, arguments, activities, exist, isError));

            i++;
        }
        lm.d("De-serializzazione di " + i + " Lessons completata");
        return returnLessons;
    }


    //endregion

}
