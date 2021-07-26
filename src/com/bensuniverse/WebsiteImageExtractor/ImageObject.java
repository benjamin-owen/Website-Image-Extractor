package com.bensuniverse.WebsiteImageExtractor;

public class ImageObject {

    private String url_short;
    private String url;

    public ImageObject(String url_short, String url) {

        this.url_short = url_short;
        this.url = url;

    }

    public String toString() {

        return this.url_short;

    }

    public String getUrl() {

        return this.url;

    }
}
