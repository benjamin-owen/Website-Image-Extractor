package com.bensuniverse.WebsiteImageExtractor;

import javax.swing.*;
import java.util.ArrayList;

public class AutoCheckbox {

    public ArrayList<Integer> getSelection(ArrayList<ImageObject> images, String include, String exclude) {

        ArrayList<Integer> selected_indicies = new ArrayList<Integer>();

        boolean missed = false;
        boolean found = false;

        // loop through each URL in URL list
        for (ImageObject currentImage : images) {

            System.out.println("Current url: " + currentImage.toString());

            missed = false;

            // will change missed = true if the string DOES NOT contain ALL of the include strings
            for (String istr : include.split(",")) {

                System.out.println("Current istr: " + istr);
                System.out.println("Current missed: " + missed);

                if (!currentImage.toString().contains(istr.trim()) && !istr.equals(""))
                    missed = true;

            }

            found = false;

            // will change found = true if the string DOES contain ONE of the exclude strings
            for (String estr : exclude.split(",")) {

                System.out.println("Current estr: " + estr);
                System.out.println("Current found: " + found);

                if (currentImage.toString().contains(estr.trim()) && !estr.equals(""))
                    found = true;

            }

            // if file name met all qualifications, add checked box (unchecked otherwise)
            if (!missed && !found)
                selected_indicies.add(images.indexOf(currentImage));

        }

        // return Integer list
        return selected_indicies;
    }
}
