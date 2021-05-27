package com.bensuniverse.WebsiteImageExtractor;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;

public class Main {

    private final static String NAME = "Website Image Extractor";
    private final static String VERSION = "1.0";
    private final static String AUTHOR = "Benjamin Owen";
    private final static String DATE = "2021-05-26";

    public static void main(String[] args) {

        // set look and feel
        FlatDarkLaf.setup();

        // use system look and feel
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        // initialize JFrame with title passed and set visibility to true
        new MainWindow().setVisible(true);

    }

    public static String getName() {

        return NAME;

    }

    public static String getVersion() {

        return VERSION;

    }

    public static String getAuthor() {

        return AUTHOR;

    }

    public static String getDate() {

        return DATE;

    }
}
