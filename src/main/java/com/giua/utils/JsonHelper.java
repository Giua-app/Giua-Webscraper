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
import com.giua.objects.Alert;
import com.giua.objects.Newsletter;
import com.giua.objects.Vote;
import com.giua.webscraper.GiuaScraper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class JsonHelper {

    boolean debugMode;
    int jsonVer = 1;


    public JsonHelper() {
        debugMode = GiuaScraper.getDebugMode();
    }

    public JsonNode getRootNode(String jsonData){
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode rootNode = null;
        try {
            rootNode = objectMapper.readTree(jsonData);
        } catch (IOException e) {
            logln("loadDataFromJSON: Impossibile leggere json");
            e.printStackTrace();
        }
        return rootNode;
    }

    private StringBuilder writeJsonVerAndDate(){
        logln("writeJsonVerAndDate: JSON VERSIONE: " + jsonVer + " SCRITTURA IN CORSO");
        StringBuilder json = new StringBuilder("{\"version\":"+jsonVer+",");
        Date calendar = Calendar.getInstance().getTime();
        json.append("\"create_date\":\"").append(calendar).append("\",");
        return json;
    }

    private String writeJsonEOF(StringBuilder json){
        json.append("}");
        logln("JSON FINALE: " + json);
        logln("saveDataToJSON: Salvataggio completato");
        return json.toString();
    }

    public StringBuilder writeNewslettersToJson(StringBuilder json, List<Newsletter> newsletters){
        json.append("\"newsletters\":[{")
                .append("\"0\":")
                .append(newsletters.get(0).toJSON());
        for(int i=1;i < newsletters.size();i++){
            json.append(",\"").append(i).append("\":")
                    .append(newsletters.get(i).toJSON());
        }
        json.append("}]");
        return json;
    }

    public StringBuilder writeVotesToJson(StringBuilder json, Map<String, List<Vote>> votes){
        json.append("\"votes\":[{");
        for(String str : votes.keySet()){
            //Materia
            json.append("\"").append(str).append("\":[{")
                    .append("\"0\":")
                    .append(votes.get(str).get(0).toJSON());

            for(int i=1;i < votes.get(str).size();i++){
                //Voto
                json.append(",\"").append(i).append("\":")
                        .append(votes.get(str).get(i).toJSON());
            }
            //Fine di una materia
            json.append("}],");
        }
        json.deleteCharAt(json.length()-1); //Cancella la virgola dell'ultima materia

        json.append("}]");

        return json;
    }

    public StringBuilder writeAlertsToJson(StringBuilder json, List<Alert> alerts){
        json.append("\"alerts\":[{")
                .append("\"0\":")
                .append(alerts.get(0).toJSON());
        for(int i=1;i < alerts.size();i++){
            json.append(",\"").append(i).append("\":")
                    .append(alerts.get(i).toJSON());
        }
        json.append("}]");
        return json;
    }

    private void saveJsonStringToFile(String path, String string) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));

        ObjectMapper mapper = new ObjectMapper();
        Object jsonOb = mapper.readValue(string, Object.class);

        String pretty = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonOb);

        writer.write(pretty);
        writer.close();
    }


    public void saveNewslettersToFile(String path, List<Newsletter> newsletters) throws IOException {
        StringBuilder json = writeJsonVerAndDate();

        json = writeNewslettersToJson(json, newsletters);

        String finalJson = writeJsonEOF(json);
        saveJsonStringToFile(path, finalJson);
    }

    public void saveVotesToFile(String path, Map<String, List<Vote>> votes) throws IOException {
        StringBuilder json = writeJsonVerAndDate();

        json = writeVotesToJson(json, votes);

        String finalJson = writeJsonEOF(json);
        saveJsonStringToFile(path, finalJson);
    }

    public void saveAlertsToFile(String path, List<Alert> alerts) throws IOException {
        StringBuilder json = writeJsonVerAndDate();

        json = writeAlertsToJson(json, alerts);

        String finalJson = writeJsonEOF(json);
        saveJsonStringToFile(path, finalJson);
    }



    public List<Newsletter> parseJsonForNewsletters(Path path){

        String json = "";
        try {
            json = Files.readString(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return parseJsonForNewsletters(json);
    }

    public List<Newsletter> parseJsonForNewsletters(String json){
        List<Newsletter> returnNewsletters = new Vector<>();
        JsonNode rootNode = getRootNode(json);
        JsonNode newsletters = rootNode.findPath("newsletters");

        int i = 0;
        //non dovrebbe MAI raggiungere 50 (perchè il massimo di circolari in una pagina sono 20)
        //ma nel caso succeda qualcosa almeno ferma il loop
        log("parseJsonForNewsletters: Leggo json circolari");
        while(i < 50){
            JsonNode newsletter;
            newsletter = newsletters.findPath(""+i);

            if(newsletter.isMissingNode()){
                logln("\nparseJsonForNewsletters: Lettura circolari completata, ho letto " + i + " circolari");
                break;
            }

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
            log(".");
        }
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

    public Map<String, List<Vote>> parseJsonForVotes(String json){
        Map<String, List<Vote>> returnVote = new HashMap<>();
        JsonNode rootNode = getRootNode(json);
        JsonNode votesNode = rootNode.findPath("votes");

        JsonNode rootVotes = votesNode.get(0);
        Iterator<String> subject = rootVotes.fieldNames();


        logln("parseJsonForVotes: Leggo json voti...");
        while(subject.hasNext()){
            String subjectName = subject.next();

            List<Vote> votes = new Vector<>();
            int i=0;
            //non dovrebbe MAI raggiungere 50 (perchè è impossibile mettere 50 voti)
            //ma nel caso succeda qualcosa almeno ferma il loop
            while(i < 50){
                JsonNode vote = rootVotes.get(subjectName).findPath(""+i);

                if(vote.isMissingNode()){
                    //significa che abbiamo finito i voti
                    break;
                }

                String value = vote.findPath("value").asText();
                boolean isFirstQuarterly = vote.findPath("isFirstQuarterly").asBoolean();
                boolean isAsterisk = vote.findPath("isAsterisk").asBoolean();
                String date = vote.findPath("date").asText();
                String judgement = vote.findPath("judgement").asText();
                String type = vote.findPath("type").asText();
                String arguments = vote.findPath("arguments").asText();

                votes.add(new Vote(value, date, type, arguments, judgement, isFirstQuarterly, isAsterisk));

                i++;
            }
            returnVote.put(subjectName, votes);
        }
        logln("parseJsonForVotes: lettura completata");
        return returnVote;


        /*int i = 0;
        //non dovrebbe MAI raggiungere 50 (perchè il massimo di circolari in una pagina sono 20)
        //ma nel caso succeda qualcosa almeno ferma il loop
        log("loadDataFromJSON: Leggo json circolari");
        while(i < 50){
            JsonNode newsletter;
            newsletter = newsletters.findPath(""+i);

            if(newsletter.isMissingNode()){
                logln("\nloadDataFromJSON: Lettura circolari completata, ho letto " + i + " circolari");
                break;
            }

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
            log(".");
        }
        return returnNewsletters;*/
    }



    /**
     * Stampa una stringa e va a capo.
     */
    protected void logln(String message) {
        if (debugMode)
            System.out.println(message);
    }

    /**
     * Stampa una stringa.
     */
    protected void log(String message) {
        if (debugMode)
            System.out.print(message);
    }

}
