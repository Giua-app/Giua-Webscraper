package com.giua.objects;

import java.io.Serializable;

public class Lesson implements Serializable {
    public final String date;
    public final String time;
    public final String subject;
    public final String arguments;
    public final String activities;
    public final boolean exists;

    public Lesson(String date, String time, String subject, String arguments, String activities, boolean exists) {
        this.date = date;
        this.time = time;
        this.subject = subject;
        this.arguments = arguments;
        this.activities = activities;
        this.exists = exists;
    }

    public String toString(){
        return this.date + "; " + this.time + "; " + this.subject + "; " + this.arguments + "; " + this.activities + "; " + this.exists;
    }

}
