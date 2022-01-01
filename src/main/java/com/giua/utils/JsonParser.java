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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.giua.objects.*;
import com.giua.webscraper.GiuaScraper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class JsonParser {

    static boolean debugMode;
    static int jsonVer = 2;
    static LoggerManager lm;


    public JsonParser(GiuaScraper gS) {
        debugMode = GiuaScraper.getDebugMode();
        lm = gS.getLoggerManager();
    }

    public JsonParser() {
        debugMode = GiuaScraper.getDebugMode();
        lm = new LoggerManager("JsonParser");
    }

    private JsonNode getRootNode(String jsonData) {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode rootNode = null;
        try {
            rootNode = objectMapper.readTree(jsonData);
        } catch (IOException e) {
            lm.e("JsonParser: Impossibile leggere json");
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
/*TODO per ora sto mettendo che tutti i voti fanno media ma qualcuno aggiunga il parsing anche di quello*/
                votes.add(new Vote(value, date, type, arguments, judgement, quart, isAsterisk, true));

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

    public List<Homework> parseJsonForHomeworks(Path path) {

        String json = "";
        try {
            json = Files.readString(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return parseJsonForHomeworks(json);
    }

    public List<Homework> parseJsonForHomeworks(String json) {
        List<Homework> returnHomeworks = new Vector<>();
        Iterator<JsonNode> homeworks = getRootNode(json).findPath("homeworks").get(0).iterator();

        int i = 0;
        lm.d("De-serializzazione JSON per Homeworks in corso...");
        while (homeworks.hasNext()) {
            JsonNode homework = homeworks.next();

            String day = homework.findPath("day").asText();
            String month = homework.findPath("month").asText();
            String year = homework.findPath("year").asText();
            String date = homework.findPath("date").asText();
            String subject = homework.findPath("subject").asText();
            String creator = homework.findPath("creator").asText();
            String details = homework.findPath("details").asText();
            boolean exists = homework.findPath("exists").asBoolean();

            returnHomeworks.add(new Homework(day, month, year, date, subject, creator, details, exists));

            i++;
        }
        lm.d("De-serializzazione di " + i + " Homeworks completata");
        return returnHomeworks;
    }

    public List<Test> parseJsonForTests(Path path) {

        String json = "";
        try {
            json = Files.readString(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return parseJsonForTests(json);
    }

    public List<Test> parseJsonForTests(String json) {
        List<Test> returnTests = new Vector<>();
        Iterator<JsonNode> tests = getRootNode(json).findPath("tests").get(0).iterator();

        int i = 0;
        lm.d("De-serializzazione JSON per Tests in corso...");
        while (tests.hasNext()) {
            JsonNode test = tests.next();

            String day = test.findPath("day").asText();
            String month = test.findPath("month").asText();
            String year = test.findPath("year").asText();
            String date = test.findPath("date").asText();
            String subject = test.findPath("subject").asText();
            String creator = test.findPath("creator").asText();
            String details = test.findPath("details").asText();
            boolean exists = test.findPath("exists").asBoolean();

            returnTests.add(new Test(day, month, year, date, subject, creator, details, exists));

            i++;
        }
        lm.d("De-serializzazione di " + i + " Tests completata");
        return returnTests;
    }


    public List<News> parseJsonForNews(Path path) {

        String json = "";
        try {
            json = Files.readString(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return parseJsonForNews(json);
    }

    public List<News> parseJsonForNews(String json) {
        List<News> returnTests = new Vector<>();
        Iterator<JsonNode> news = getRootNode(json).findPath("news").get(0).iterator();

        int i = 0;
        lm.d("De-serializzazione JSON per News in corso...");
        while (news.hasNext()) {
            JsonNode node = news.next();

            String newsText = node.findPath("newsText").asText();
            String url = node.findPath("url").asText();

            returnTests.add(new News(newsText, url));

            i++;
        }
        lm.d("De-serializzazione di " + i + " News completata");
        return returnTests;
    }

    public List<DisciplinaryNotices> parseJsonForDNotices(Path path) {

        String json = "";
        try {
            json = Files.readString(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return parseJsonForDNotices(json);
    }

    public List<DisciplinaryNotices> parseJsonForDNotices(String json) {
        List<DisciplinaryNotices> returnTests = new Vector<>();
        Iterator<JsonNode> notes = getRootNode(json).findPath("disciplinary_notices").get(0).iterator();

        int i = 0;
        lm.d("De-serializzazione JSON per DisciplinaryNotices in corso...");
        while (notes.hasNext()) {
            JsonNode node = notes.next();

            String date = node.findPath("date").asText();
            String type = node.findPath("type").asText();
            String details = node.findPath("details").asText();
            String countermeasures = node.findPath("countermeasures").asText();
            String authorOfDetails = node.findPath("authorOfDetails").asText();
            String authorOfCountermeasures = node.findPath("authorOfCountermeasures").asText();
            String quarterly = node.findPath("quarterly").asText();

            returnTests.add(new DisciplinaryNotices(date, type, details, countermeasures, authorOfDetails, authorOfCountermeasures, quarterly));

            i++;
        }
        lm.d("De-serializzazione di " + i + " DisciplinaryNotices completata");
        return returnTests;
    }

    public List<Observations> parseJsonForObservations(Path path) {

        String json = "";
        try {
            json = Files.readString(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return parseJsonForObservations(json);
    }

    public List<Observations> parseJsonForObservations(String json) {
        List<Observations> returnTests = new Vector<>();
        Iterator<JsonNode> obs = getRootNode(json).findPath("observations").get(0).iterator();

        int i = 0;
        lm.d("De-serializzazione JSON per Observations in corso...");
        while (obs.hasNext()) {
            JsonNode node = obs.next();

            String date = node.findPath("date").asText();
            String subject = node.findPath("subject").asText();
            String teacher = node.findPath("teacher").asText();
            String observations = node.findPath("observations").asText();
            String quarterly = node.findPath("quarterly").asText();

            returnTests.add(new Observations(date, subject, teacher, observations, quarterly));

            i++;
        }
        lm.d("De-serializzazione di " + i + " Observations completata");
        return returnTests;
    }

    public List<Document> parseJsonForDocuments(Path path) {

        String json = "";
        try {
            json = Files.readString(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return parseJsonForDocuments(json);
    }

    public List<Document> parseJsonForDocuments(String json) {
        List<Document> returnTests = new Vector<>();
        Iterator<JsonNode> docs = getRootNode(json).findPath("documents").get(0).iterator();

        int i = 0;
        lm.d("De-serializzazione JSON per Documents in corso...");
        while (docs.hasNext()) {
            JsonNode node = docs.next();

            String status = node.findPath("status").asText();
            String classroom = node.findPath("classroom").asText();
            String subject = node.findPath("subject").asText();
            String institute = node.findPath("institute").asText();
            String downloadUrl = node.findPath("downloadUrl").asText();

            returnTests.add(new Document(status, classroom, subject, institute, downloadUrl));

            i++;
        }
        lm.d("De-serializzazione di " + i + " Documents completata");
        return returnTests;
    }


    //endregion

}
