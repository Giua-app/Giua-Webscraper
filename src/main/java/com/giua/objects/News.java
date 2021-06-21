package com.giua.objects;

import java.io.Serializable;

public class News implements Serializable {
    public final String newsText;
    public final String url;

    public News(String newsText, String url) {
        this.newsText = newsText;
        this.url = url;
    }

    public String toString() {
        return newsText + "; " + url;
    }
}
