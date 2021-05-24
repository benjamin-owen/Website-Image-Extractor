package com.bensuniverse.WebsiteImageExtractor;

import javax.swing.*;
import java.util.ArrayList;

public class AutoCheckbox {

    public ArrayList<JCheckBox> getCheckboxes(ArrayList<String> urls, String include, String exclude) {

        ArrayList<JCheckBox> checkboxes = new ArrayList<JCheckBox>();

        boolean missed = false;
        boolean found = false;

        // loop through each URL in URL list
        for (String currentURL : urls) {

            System.out.println("Current url: " + currentURL);

            missed = false;

            // will change missed = true if the string DOES NOT contain ALL of the include strings
            for (String istr : include.split(",")) {

                System.out.println("Current istr: " + istr);
                System.out.println("Current missed: " + missed);

                if (!currentURL.contains(istr.trim()) && !istr.equals(""))
                    missed = true;

            }

            found = false;

            // will change found = true if the string DOES contain ONE of the exclude strings
            for (String estr : exclude.split(",")) {

                System.out.println("Current estr: " + estr);
                System.out.println("Current found: " + found);

                if (currentURL.contains(estr.trim()) && !estr.equals(""))
                    found = true;

            }

            // if file name met all qualifications, add checked box (unchecked otherwise)
            if (!missed && !found)
                checkboxes.add(new JCheckBox(currentURL, true));
            else
                checkboxes.add(new JCheckBox(currentURL, false));

        }

        // return JCheckBox list
        return checkboxes;
    }
}
