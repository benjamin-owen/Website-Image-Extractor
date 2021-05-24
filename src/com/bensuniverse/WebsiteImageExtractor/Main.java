package com.bensuniverse.WebsiteImageExtractor;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        // set look and feel
        FlatDarkLaf.install();

        // use system look and feel
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        // initialize JFrame with title passed and set visibility to true
        new MainWindow("Website Image Extractor").setVisible(true);

    }
}
