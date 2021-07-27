package com.bensuniverse.WebsiteImageExtractor;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageObject implements Serializable {

    private String url_short;
    private String url;

    public ImageObject(String url_short, String url) {

        this.url_short = url_short;
        this.url = url;

    }

    public String toString() {

        return this.url_short;

    }

    public URL getURL() {
        try {

            return new URL(this.url);

        } catch (MalformedURLException e) {

            e.printStackTrace();
            return null;

        }
    }
}
