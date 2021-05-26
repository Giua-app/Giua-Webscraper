package com.giua.objects;

public class Test{
    public final String day;		//usato per trovare quale verifica interessa
    public final String date;
    public final String subject;
    public final String creator;
    public final String details;
    public final boolean exists;

    public Test(String day, String date, String subject, String creator, String details, boolean exists){
        this.day = day;
        this.date = date;
        this.subject = subject;
        this.creator = creator;
        this.details = details;
        this.exists = exists;
    }

    public String toString() {
        return this.date + "; " + this.creator + "; " + this.subject + "; " + this.details + "; " + this.exists;
    }
}
