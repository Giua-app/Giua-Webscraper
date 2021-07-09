package com.giua.objects;

import java.io.Serializable;
import java.util.Date;

public class Maintenance implements Serializable{
    public Date start;
    public Date end;
    public Boolean isActive;
    public Boolean shouldBeActive;
    public Boolean exist;

    public Maintenance(Date start, Date end, Boolean isActive, Boolean shouldBeActive, Boolean exist) {
        this.start = start;
        this.end = end;
        this.isActive = isActive;
        this.shouldBeActive = shouldBeActive;
        this.exist = exist;
    }

    public String toString() {
        if(this.exist == false){
            return "Esiste? " + this.exist + " Inizio: " + "null" + " Fine: " + "null " +
                    " In corso? " + this.isActive + " Dovrebbe essere in corso? " + this.shouldBeActive;
        }

        return "Esiste? " + this.exist + " Inizio: " + this.start.toString() + " Fine: " + this.end.toString() +
                " In corso? " + this.isActive + " Dovrebbe essere in corso? " + this.shouldBeActive;
    }
}
