package com.bensuniverse.WebsiteImageExtractor;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ImageGrabber {

    public ArrayList<ImageObject> getImages(String url) {

        // find index of third '/' in URL
        int slash_index = 0;
        int slash_count = 0;
        for (slash_index = 0; slash_index < url.length(); slash_index++) {

            if (url.charAt(slash_index) == '/')
                slash_count++;

            if (slash_count >= 3)
                break;

        }

        System.out.println("RAW URL: " + url);
        System.out.println("SLASH INDEX: " + slash_index);
        String guess_url = url.substring(url.indexOf("http"), slash_index);
        System.out.println("GUESS URL: " + guess_url);

        // list to contain image URLs
        ArrayList<String> images_dupechecker = new ArrayList<String>();
        ArrayList<ImageObject> images = new ArrayList<ImageObject>();

        // avoid HTTP 403 errors
        try {

            // open URL simulating a web browser to avoid HTTP 403 error
            URLConnection connection = new URL(url).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.connect();

            // get HTML source
            BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));

            String line;
            while ((line = r.readLine()) != null) {

                System.out.println("RAW LINE: " + line);

                // loop through each line in source code
                for (String str : line.split("\"")) {

                    // list of extensions to check
                    ArrayList<String> extensions = new ArrayList<String>();
                    extensions.add("png");
                    extensions.add("jpg");
                    extensions.add("jpeg");
                    extensions.add("gif");
                    extensions.add("tiff");
                    extensions.add("bmp");
                    extensions.add("webp");
                    extensions.add("heif");

                    // check if line of code references an image file
                    String finalStr = str;
                    if (extensions.stream().anyMatch(s -> finalStr.toLowerCase().contains(s)) && !str.contains("=")) {

                        if (!str.toLowerCase().contains("http")) {

                            // try adding guess url
                            str = guess_url + str;
                            System.out.println("MODIFIED: " + str);

                        }

                        // loop through extensions
                        for (String extension : extensions) {

                            // check if URL contains extension and make sure the extension is the end of the image
                            // this avoids many invalid images that have text after the extension (usually duplicates)
                            if (str.substring(str.lastIndexOf("/") + 1).contains(extension) &&
                            str.substring(str.indexOf(extension)).length() <= extension.length() &&
                            !images_dupechecker.contains(str.substring(str.lastIndexOf("/") + 1))) {

                                // add image to image list
                                images.add(new ImageObject(str.substring(str.lastIndexOf("/") + 1), str));
                                images_dupechecker.add(str.substring(str.lastIndexOf("/") + 1));

                            }
                        }
                    }
                }
            }

            for (ImageObject io : images) {

                System.out.println("Found image: " + io.toString());

            }

        } catch (IOException e) {

            // problem opening URL
            System.out.println("Invalid URL!");

        }

        // return list of URLs
        return images;

    }

    public Object[] getImageFromURL(URL url, boolean resize, int max_width) {

        BufferedImage image = null;
        BufferedImage resized = null;

        int width = 0;
        int height = 0;
        try {

            // open URL connection (spoof browser to avoid HTTP 403 errors)
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.connect();

//            System.out.println("# " + url.toString());

            try {

                // read image from URL
                image = ImageIO.read(connection.getInputStream());
                width = image.getWidth();
                height = image.getHeight();

            } catch (Exception e) {

                // if some error prevents image from working
                System.out.println("Error 01");
                image = ImageIO.read(getClass().getResource("/file_not_found.jpg"));
                image = resize(image, max_width);

            }

            // resize image preview
            if (resize)
                resized = resize(image, max_width);
        }
        catch (IOException e) {

//            System.out.println("> " + url.toString());
            System.out.println("Error 02");
            e.printStackTrace();

            try {

                // if some error prevents URL from working correctly
                image = ImageIO.read(getClass().getResource("/file_not_found.jpg"));
                image = resize(image, max_width);

                return new Object[]{image, width, height};

            } catch (IOException e2) {

                System.out.println("Error 03");
                // should never happen, rip if it does
                e2.printStackTrace();

            }
        }

        // return resized image if true, full size image if false
        if (resize)
            return new Object[]{resized, width, height};
        else
            return new Object[]{image, width, height};

    }

    private static BufferedImage resize(BufferedImage img, int max_width) {

        // get dimensions of image
        int width = img.getWidth();
        int height = img.getHeight();

        double aspect_ratio = (double) width / (double) height;
        System.out.println("Width: " + width);
        System.out.println("Height: " + height);
        System.out.println("Aspect ratio: " + aspect_ratio);

        // get desired dimension for image
        int final_height = (int) (max_width / aspect_ratio);

        // do some magic to stretch/compress image to given dimensions
        Image tmp = img.getScaledInstance(max_width, final_height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(max_width, final_height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return resized;

    }

    public void downloadImages(ArrayList<ImageObject> images, String file_location, boolean rename, String prefix, String extension, int start_index, JProgressBar progress_bar, JPanel panel, JLabel status) {

        // get number of digits of total image count
        int prefixNum = (int) Math.log10((start_index - 1) + images.size()); // (start_index + urls.size()) in order to add 0 when not starting at 0
        System.out.println("prefixNum: " + prefixNum);

        // loop through each index of image list
        for (int i = 0; i < images.size(); i++) {
            System.out.println("i: " + i);

            URL currentURL = images.get(i).getURL();

            // get image from URL
            BufferedImage currentImg = (BufferedImage) getImageFromURL(currentURL, false, 1)[0];

            // logic to add 0s for alphabetical purposes
            int currentNum = i + start_index;
            String indexPrefix = "";
            System.out.println("diff: " + (prefixNum - (int) Math.log10(i)));
            if (currentNum == 0) { // first image

                for (int j = 0; j < prefixNum; j++) {

                    indexPrefix += "0";

                }
            }

            for (int j = 0; j < (prefixNum - (int) Math.log10(currentNum)); j++) {

                indexPrefix += "0";

            }
            // use all given variables to create file output location name
            File outputFile;
            if (rename)
                outputFile = new File(file_location + prefix + indexPrefix + Integer.toString(i + start_index) + extension);
            else
                outputFile = new File(file_location + currentURL.toString().substring(currentURL.toString().lastIndexOf('/')));

            // save image to file
            try {

                if (extension.equalsIgnoreCase(".jpg"))
                    ImageIO.write(currentImg, "jpg", outputFile);
                else
                    ImageIO.write(currentImg, "png", outputFile);

            } catch (IOException e) {

                // my guess would be permission error here
                try {

                    Files.delete(Paths.get(outputFile.getAbsolutePath()));

                } catch (IOException ioException) {

                    ioException.printStackTrace();

                }

                System.out.println("IOException (probably no write permissions or invalid image)");

            }

            // get percentage progress
            double progress = (i * 100.0) / images.size();
            System.out.println(i + " | " + images.size() + " | " + "Progress: " + progress);

            // set progress bar value
            progress_bar.setValue((int) progress);
            progress_bar.setString((int) progress + "%");

            // set status text with current progress
            status.setText("Downloading..." + i + "/" + images.size());

            // update panel with progress bar and status text so it refreshes
            panel.updateUI();

        }
    }
}
