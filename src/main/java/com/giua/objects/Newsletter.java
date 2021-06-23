package com.giua.objects;

import java.io.Serializable;
import java.util.List;

public class Newsletter implements Serializable {

    public final String status;
    public final String date;
    public final String newslettersObject;
    public final String detailsUrl;
    public final String number;
    public final List<String> attachments;
    public final int page;

    public Newsletter(String status, String number, String date, String newslettersObject, String detailsUrl, List<String> attachments, int page) {
        this.status = status;
        this.date = date;
        this.newslettersObject = newslettersObject;
        this.detailsUrl = detailsUrl;
        this.number = number;
        this.attachments = attachments;
        this.page = page;
    }

    public boolean isRead(){
        return this.status.equals("LETTA");
    }

    public String toString(){
        return this.status + "; " + this.number + "; " + this.date + "; " + this.newslettersObject + "; " + this.detailsUrl + "; " + ((this.attachments != null) ? this.attachments.get(0) : "null");
    }
}
