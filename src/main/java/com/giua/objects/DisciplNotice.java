package com.giua.objects;

import java.io.Serializable;

public class DisciplNotice implements Serializable {
    public String date;
    public String type;
    public String details;
    public String countermeasures;
    public String authorOfDetails;
    public String authorOfCountermeasures;

    public DisciplNotice(String date, String type, String details, String countermeasures, String authorOfDetails, String authorOfCountermeasures) {
        this.date = date;
        this.type = type;
        this.details = details;
        this.countermeasures = countermeasures;
        this.authorOfDetails = authorOfDetails;
        this.authorOfCountermeasures = authorOfCountermeasures;
    }

    public String toString() {
        return this.date + " ; " + this.type + " ; " + this.authorOfDetails + ": " + this.details + " ; " + this.authorOfCountermeasures + ": " + this.countermeasures;
    }
}
