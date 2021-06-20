package com.giua.objects;

import java.io.Serializable;

public class Homework implements Serializable {
    public final String day;        //usato per trovare quale compito interessa
    public final String date;
    public final String subject;
    public final String creator;
    public final String details;
    public final boolean exists;

    public Homework(String day, String date, String subject, String creator, String details, boolean exists) {
        this.day = day;
        this.date = date;
        this.subject = subject;
        this.creator = creator;
        this.details = details;
        this.exists = exists;
    }

    public String toString() {
        return this.date + "; " + this.creator + "; " + this.subject + "; " + this.details + "; " + String.valueOf(this.exists);
    }
}