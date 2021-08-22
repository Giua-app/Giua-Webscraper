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
import com.giua.objects.Newsletter;
import com.giua.objects.Vote;
import com.giua.webscraper.GiuaScraper;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class JsonHelper {

    String jsonVer;
    boolean debugMode;


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
        return returnNewsletters;
    }


    public void parseJsonForVotes(Path path){

        String json = "";
        try {
            json = Files.readString(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //return parseJsonForVotes(json);
    }


    public void parseJsonForVotes(String json){
        List<Vote> returnVote = new Vector<>();
        JsonNode rootNode = getRootNode(json);
        JsonNode votesNode = rootNode.findPath("votes");

        Iterator<JsonNode> votes = votesNode.elements();
        while(votes.hasNext()){
            JsonNode v = votes.next();
            logln(v.asText());
        }


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
