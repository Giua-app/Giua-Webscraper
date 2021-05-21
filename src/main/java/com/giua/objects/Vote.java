package com.giua.objects;

import com.giua.webscraper.GiuaScraper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public class Vote{
    public final String value;
    public final boolean isFirstQuarterly;
    public final boolean isAsterisk;
    public final String date;
    public final String judgement;
    public final String testType;
    public final String arguments;

    Vote(String value, String date, String testType, String arguments, String judgement, boolean isFirstQuarterly, boolean isAsterisk){
        this.value = value;
        this.date = date;
        this.testType = testType;
        this.arguments = arguments;
        this.judgement = judgement;
        this.isFirstQuarterly = isFirstQuarterly;
        this.isAsterisk = isAsterisk;
    }

    //Deve essere usata solo da getAllVotes e serve a gestire quei voti che non hanno alcuni dettagli
    private static String getDetailOfVote(Element e, int index){
        try {
            return e.siblingElements().get(e.elementSiblingIndex()).child(0).child(0).child(index).text().split(": ")[1];
        } catch (Exception err){
            return "";
        }
    }

    //Ritorna una mappa fatta in questo modo: {"italiano": [tutti voti italiano], ...}
    public static Map<String, List<Vote>> getAllVotes(GiuaScraper gS) {

        Map<String, List<Vote>> returnVotes = new HashMap<>();
        Document doc = gS.getPage(GiuaScraper.SiteURL + "/genitori/voti");
        Elements votesHTML = doc.getElementsByAttributeValue("title", "Informazioni sulla valutazione");

        for (final Element voteHTML : votesHTML) {
            final String voteAsString = voteHTML.text(); //prende il voto
            final String materiaName = voteHTML.parent().parent().child(0).text(); //prende il nome della materia
            final String voteDate = getDetailOfVote(voteHTML, 0);
            final String type = getDetailOfVote(voteHTML, 1);
            final String args = getDetailOfVote(voteHTML, 2);
            final String judg = getDetailOfVote(voteHTML, 3);
            final boolean isFirstQuart = voteHTML.parent().parent().parent().parent().getElementsByTag("caption").get(0).text().equals("Primo Quadrimestre");

            if (voteAsString.length() > 0) {    //Gli asterischi sono caratteri vuoti
                if (returnVotes.containsKey(materiaName)) {			//Se la materia esiste gia aggiungo solamente il voto
                    List<Vote> tempList = returnVotes.get(materiaName); //uso questa variabile come appoggio per poter modificare la lista di voti di quella materia
                    tempList.add(new Vote(voteAsString, voteDate, type, args, judg, isFirstQuart, false));
                } else {
                    returnVotes.put(materiaName, new Vector<Vote>() {{
                        add(new Vote(voteAsString, voteDate, type, args, judg, isFirstQuart, false));    //il voto lo aggiungo direttamente
                    }});
                }
            } else {		//e' un asterisco
                if(returnVotes.containsKey(materiaName)){
                    returnVotes.get(materiaName).add(new Vote("", voteDate, type, args, judg, isFirstQuart, true));
                } else {
                    returnVotes.put(materiaName, new Vector<Vote>() {{
                        add(new Vote("", voteDate, type, args, judg, isFirstQuart, true));
                    }});
                }
            }
        }

        return returnVotes;
    }

    //Mette anche i dettagli nella stringa
    public String allToString() {
        if (this.isAsterisk) {
            return "*; " + this.date + "; " + this.testType + "; " + this.arguments + "; " + this.judgement;
        } else {
            return this.value + "; " + this.date + "; " + this.testType + "; " + this.arguments + "; " + this.judgement;
        }
    }

    public String toString(){
        return (this.isAsterisk) ? "*" : this.value;
    }
}
