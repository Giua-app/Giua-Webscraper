package com.giua.objects;

import java.io.Serializable;

public class DisciplNotice implements Serializable {
    public String date;
    public String type;
    public String details;
    public String countermeasures;
    public String author;

    public DisciplNotice(String date, String type, String details, String countermeasures, String author) {
        this.date = date;
        this.type = type;
        this.details = details;
        this.countermeasures = countermeasures;
        this.author = author;
    }

    public String toString() {
        return this.date + "; " + this.type + "; " + this.author + "; " + this.details + "; " + this.countermeasures;
    }
}
