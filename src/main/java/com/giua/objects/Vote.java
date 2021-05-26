package com.giua.objects;

public class Vote{
    public final String value;
    public final boolean isFirstQuarterly;
    public final boolean isAsterisk;
    public final String date;
    public final String judgement;
    public final String testType;
    public final String arguments;

    public Vote(String value, String date, String testType, String arguments, String judgement, boolean isFirstQuarterly, boolean isAsterisk){
        this.value = value;
        this.date = date;
        this.testType = testType;
        this.arguments = arguments;
        this.judgement = judgement;
        this.isFirstQuarterly = isFirstQuarterly;
        this.isAsterisk = isAsterisk;
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
