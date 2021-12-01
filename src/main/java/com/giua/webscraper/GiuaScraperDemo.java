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

package com.giua.webscraper;

import com.giua.objects.*;
import org.jsoup.nodes.Document;

import java.util.*;

//FIXME
public class GiuaScraperDemo {

    public static Authorization getAutorizations() {
        return new Authorization("08:40", "12:20");
    }

    public static List<Observations> getAllObservations() throws GiuaScraperExceptions.UnsupportedAccount {
        List<Observations> obs = new Vector<>();
        obs.add(new Observations("2021-11-18", "Scienze", "Clara Loggia", "L'alunno è stato bravo", "Primo Quadrimestre"));
        obs.add(new Observations("2021-11-18", "Storia", "Taziano Napolitani", "L'alunno ha fatto un brutto compito di storia", "Secondo Quadrimestre"));
        return obs;

    }

    public static List<Absence> getAllAbsences() {
        List<Absence> absences = new Vector<>();
        absences.add(new Absence("2021-10-28", "Assenza", "", true, true, "/genitori/giustifica/assenza/4"));
        absences.add(new Absence("2021-10-29", "Ritardo (09:30)", "", false, false, "/genitori/giustifica/ritardo/3"));
        absences.add(new Absence("2021-10-29", "Uscita anticipata (11:30)", "", true, false, "/genitori/giustifica/ritardo/3"));
        return absences;

    }

    public static List<News> getAllNewsFromHome() {
        List<News> news = new Vector<>();
        news.add(new News("Sono presenti 2 nuove circolari da leggere:", "/circolari/genitori"));
        news.add(new News("È prevista una verifica per i prossimi giorni:", "/agenda/eventi"));
        news.add(new News("È presente un compito assegnato per domani:", "/agenda/eventi"));
        news.add(new News("Sono presenti 2 nuovi avvisi da leggere:", "/bacheca/avvisi"));
        return news;

    }

    public static List<DisciplinaryNotices> getAllDisciplNotices() {
        List<DisciplinaryNotices> disciplNotices = new Vector<>();
        disciplNotices.add(new DisciplinaryNotices("2021-10-28", "Nota individuale", "Usato la penna blu invece di quella nera per scrivere il propri nome", "Espulsione dalla scuola", "Quartilla Costa", "Quartilla Costa", "Primo quadrimestre"));
        disciplNotices.add(new DisciplinaryNotices("2021-10-23", "Nota di classe", "Gli alunni mi guardano mentre spiego", "Espulsione dalla scuola per tutta la classe", "Quartilla Costa", "Quartilla Costa", "Secondo quadrimestre"));
        return disciplNotices;

    }

    public static ReportCard getReportCard() {
        Map<String, List<String>> votes = new HashMap<>();
        votes.put("Italiano", List.of("7", "8"));
        votes.put("Matematica", List.of("8", "8"));
        votes.put("Fisica", List.of("1", "16"));
        votes.put("Informatica", List.of("10", "8"));
        votes.put("Geografia", List.of("5", "8"));
        votes.put("Sistemi", List.of("6", "8"));
        votes.put("Scienze", List.of("4", "8"));
        return new ReportCard("Primo quadrimestre", votes, "AMMESSO", "2", true);


    }

    public static List<Alert> getAllAlerts() throws IndexOutOfBoundsException {
        List<Alert> alerts = new Vector<>();
        alerts.add(new Alert("LETTO", "2021-11-19", "Tutti", "Uscita anticipata", "", 1, new Vector<>(), "La classe uscira alle 10:00", "Leopoldo Piccio", "Comunicazione generica"));
        alerts.add(new Alert("DA LEGGERE", "2021-11-19", "Tutti", "Entrata anticipata", "", 1, new Vector<>(), "La classe entrerà alle 07:20", "Leopoldo Piccio", "Comunicazione generica"));
        return alerts;

    }

    public static List<Newsletter> getAllNewsletters() throws IndexOutOfBoundsException {
        List<Newsletter> newsletters = new Vector<>();
        newsletters.add(new Newsletter("DA LEGGERE", 32, "2021-11-21", "Il ritorno della circolare circolosa (piccolo pezzo di storia)", "", new Vector<>(), 0));
        newsletters.add(new Newsletter("LETTA", 32, "2021-11-21", "Questa circolare è rotonda", "", new Vector<>(), 0));
        return newsletters;

    }

    public static List<Newsletter> getAllNewslettersWithFilter() {
        List<Newsletter> newsletters = new Vector<>();
        newsletters.add(new Newsletter("DA LEGGERE", 32, "2021-11-21", "Il ritorno della circolare circolosa (piccolo pezzo di storia)", "", new Vector<>(), 0));
        newsletters.add(new Newsletter("LETTA", 32, "2021-11-21", "Questa circolare è rotonda", "", new Vector<>(), 0));
        return newsletters;

    }

    public static List<Homework> getHomework(String date) {
        List<Homework> homeworkList = new Vector<>();
        if (date.equals("2021-11-02")) {
            homeworkList.add(new Homework("2", "11", "2021", "2021-11-02", "Italiano", "Mario Ginnasio", "Studiare da pagina 100 a pagina 120", true));
            homeworkList.add(new Homework("2", "11", "2021", "2021-11-02", "Storia", "Mario Ginnasio", "Studiare da pagina 80 a pagina 100", true));
        } else
            homeworkList.add(new Homework("6", "11", "2021", "2021-11-06", "Geografia", "Mario Ginnasio", "Studiare da pagina 80 a pagina 100", true));
        return homeworkList;

    }

    public static List<Homework> getAllHomeworksWithoutDetails() {
        List<Homework> homeworkList = new Vector<>();
        homeworkList.add(new Homework("2", "11", "2021", "2021-11-02", "", "", "", true));
        homeworkList.add(new Homework("6", "11", "2021", "2021-11-06", "", "", "", true));
        return homeworkList;

    }

    public static List<Test> getTest(String date) {
        List<Test> tests = new Vector<>();
        if (date.equals("2021-11-02"))
            tests.add(new Test("2", "11", "2021", "2021-11-02", "Storia", "Mario Ginnasio", "Epoca medievale", true));
        else
            tests.add(new Test("12", "11", "2021", "2021-11-12", "Italiano", "Mario Ginnasio", "Poeti medievali", true));
        return tests;

    }

    public static List<Test> getAllTestsWithoutDetails() {
        List<Test> tests = new Vector<>();
        tests.add(new Test("2", "11", "2021", "2021-11-02", "Storia", "Mario Ginnasio", "Epoca medievale", true));
        tests.add(new Test("12", "11", "2021", "2021-11-12", "Italiano", "Mario Ginnasio", "Poeti medievali", true));
        return tests;
    }

    public static Map<String, List<Vote>> getAllVotes() {
        Map<String, List<Vote>> votes = new HashMap<>();
        List<Vote> itaVotes = new Vector<>();
        itaVotes.add(new Vote("9-", "10 Ottobre", "Scritto", "Poeti medievali", "L'alunno e' stato bravo", "Primo Quadrimestre", false));
        itaVotes.add(new Vote("2+", "11 Ottobre", "Orale", "Poeti medievali parte 2", "", "Primo Quadrimestre", false));
        itaVotes.add(new Vote("4", "12 Ottobre", "Scritto", "", "", "Primo Quadrimestre", false));
        itaVotes.add(new Vote("7", "13 Ottobre", "Pratico", "", "E' stato giudizioso (non so piu cosa scrivere)", "Primo Quadrimestre", false));
        itaVotes.add(new Vote("*", "14 Ottobre", "Scritto", "Poeti medievali la vendetta", "L'alunno e' stato bravo", "Secondo Quadrimestre", true));
        itaVotes.add(new Vote("4", "15 Ottobre", "Scritto", "Poeti medievali la vendetta 2", "Bravo!", "Secondo Quadrimestre", false));
        itaVotes.add(new Vote("2", "16 Ottobre", "Scritto", "Poeti medievali la vendetta 3 ", "", "Secondo Quadrimestre", false));
        itaVotes.add(new Vote("9", "17 Ottobre", "Scritto", "Poeti medievali la vendetta 4", "", "Secondo Quadrimestre", false));
        votes.put("Italiano", itaVotes);
        List<Vote> storiaVotes = new Vector<>();
        storiaVotes.add(new Vote("9-", "18 Ottobre", "Scritto", "Poeti medievali", "L'alunno e' stato bravo", "Primo Quadrimestre", false));
        storiaVotes.add(new Vote("2+", "18 Ottobre", "Orale", "Poeti medievali parte 2", "", "Primo Quadrimestre", false));
        storiaVotes.add(new Vote("4", "18 Ottobre", "Scritto", "", "", "Primo Quadrimestre", false));
        storiaVotes.add(new Vote("7", "18 Ottobre", "Pratico", "", "E' stato giudizioso (non so piu cosa scrivere)", "Primo Quadrimestre", false));
        storiaVotes.add(new Vote("*", "19 Ottobre", "Scritto", "Poeti medievali la vendetta", "L'alunno e' stato bravo", "Secondo Quadrimestre", true));
        storiaVotes.add(new Vote("4", "19 Ottobre", "Scritto", "Poeti medievali la vendetta 2", "Bravo!", "Secondo Quadrimestre", false));
        storiaVotes.add(new Vote("2", "19 Ottobre", "Scritto", "Poeti medievali la vendetta 3 ", "", "Secondo Quadrimestre", false));
        storiaVotes.add(new Vote("9", "20 Ottobre", "Scritto", "Poeti medievali la vendetta 4", "", "Secondo Quadrimestre", false));
        votes.put("Italiano", storiaVotes);
        return votes;
    }

    public static List<Lesson> getAllLessonsOfSubject() {
        List<Lesson> lessons = new Vector<>();
        lessons.add(new Lesson("2021-11-01", "08:30-09:30", "Informatica", "Programmazione c#", "", true));
        lessons.add(new Lesson("2021-11-03", "09:30-10:30", "Informatica", "Programmazione c#", "Laboratorio", true));
        lessons.add(new Lesson("2021-10-02", "10:30-11:30", "Informatica", "", "Guardato un film", true));
        return lessons;
    }

    public static List<Lesson> getAllLessons() {
        List<Lesson> lessons = new Vector<>();
        lessons.add(new Lesson("2021-11-01", "08:30-09:30", "Informatica", "Programmazione c#", "", true));
        lessons.add(new Lesson("2021-11-01", "09:30-10:30", "Informatica", "Programmazione c#", "Laboratorio", true));
        lessons.add(new Lesson("2021-11-01", "10:30-11:30", "Storia", "", "Guardato un film", true));
        lessons.add(new Lesson("2021-11-01", "11:30-12:30", "Scienze", "La Terra", "", true));
        return lessons;
    }

    public static Date getLastAccessTime() {
        return new Date();
    }

    public static Document getPage(String page) {
        return new Document(GiuaScraper.getSiteURL() + "/" + page);
    }

    public static Document getPageNoCookie(String page) {
        return new Document(GiuaScraper.getSiteURL() + "/" + page);
    }

    public static Document getExtPage(String url) {
        return new Document(url);
    }

    public static boolean isMaintenanceActive() {
        return false;
    }

    public static Boolean checkLogin() {
        return true;
    }

    public static boolean isSessionValid() {
        return true;
    }

    public static String loadUserFromDocument() {
        return "DEMO";
    }

    public static GiuaScraper.userTypes getUserTypeEnum() {
        return GiuaScraper.userTypes.DEMO;
    }

    public static List<Activity> getAllActivitiesWithoutDetails() {
        //TODO
        return new Vector<>();
    }

    public static List<Activity> getActivity(String date) {
        //TODO
        return new Vector<>();
    }
}
