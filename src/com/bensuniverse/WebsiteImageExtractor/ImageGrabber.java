package com.bensuniverse.WebsiteImageExtractor;

import javax.imageio.ImageIO;
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

    public ArrayList<String> getImages(String url, boolean full_URL) {

        // list to contain image URLs
        ArrayList<String> urls = new ArrayList<String>();

        // avoid HTTP 403 errors
        try {

            // open URL simulating a web browser to avoid HTTP 403 error
            URLConnection connection = new URL(url).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.connect();

            // get HTML source
            BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));

            urls = new ArrayList<String>();
            String line;
            while ((line = r.readLine()) != null) {

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
                    if (str.toLowerCase().contains("http") && extensions.stream().anyMatch(s -> str.toLowerCase().contains(s)) && !str.contains("=")) {

                        // add line to ArrayList (will be the image url)
                        if (full_URL)
                            urls.add(str);
                        else
                            urls.add(str.substring(str.lastIndexOf("/") + 1));

                    }
                }
            }

            // print found image URLs (debugging purposes)
            for (String tempurl : urls) {

                System.out.println(tempurl);

            }

        } catch (IOException e) {

            // problem opening URL
            System.out.println("Invalid URL!");

        }

        // remove duplicate entries from URL list
//        urls = new ArrayList<String>(new LinkedHashSet<String>(urls));
//        ArrayList urls_nodup = new ArrayList<String>();
//        for (String url_current : urls) {
//
//            try {
//                if (!urls_nodup.contains(url.substring(':'))) {
//
//                    urls_nodup.add(url_current);
//
//                }
//            } catch (StringIndexOutOfBoundsException e) {
//
//                // do nothing
//
//            }
//        }

        // return list of URLs
        return urls;

    }

    public URL getURLfromShortened(ArrayList<String> urls, ArrayList<String> urls_short, String url_short) {

        for (String temp : urls) {

            System.out.print("  $$" + temp);

        }
        System.out.println();

        for (String temp : urls_short) {

            System.out.print("  %%" + temp);

        }
        System.out.println();

        System.out.println(url_short);

        // use parallel lists to return the full URL from the index of the shortened one
        String full_URL = urls.get(urls_short.indexOf(url_short));
        URL url = null;

        try {

            url = new URL(full_URL);

        } catch (MalformedURLException e) {

            e.printStackTrace();

        }

        return url;

    }

    public BufferedImage getImageFromURL(URL url, boolean resize, int max_width) {

        BufferedImage image = null;
        BufferedImage resized = null;
        try {

            // open URL connection (spoof browser to avoid HTTP 403 errors)
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.connect();

//            System.out.println("# " + url.toString());

            try {

                // read image from URL
                image = ImageIO.read(connection.getInputStream());

            } catch (Exception e) {

                // if some error prevents image from working
                image = ImageIO.read(getClass().getResource("/file_not_found.jpg"));
                image = resize(image, max_width);

            }

            // resize image preview
            if (resize)
                resized = resize(image, max_width);
        }
        catch (IOException e) {

//            System.out.println("> " + url.toString());
            e.printStackTrace();

            try {

                // if some error prevents URL from working correctly
                image = ImageIO.read(getClass().getResource("/file_not_found.jpg"));
                image = resize(image, max_width);

                return image;

            } catch (IOException e2) {

                // should never happen, rip if it does
                e2.printStackTrace();

            }
        }

        // return resized image if true, full size image if false
        if (resize)
            return resized;
        else
            return image;

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

    public void downloadImages(ArrayList<String> urls, String file_location, boolean rename, String prefix, String extension, int start_index, JProgressBar progress_bar, JPanel panel, JLabel status) {

        // get number of digits of total image count
        int prefixNum = (int) Math.log10(urls.size());
        System.out.println("prefixNum: " + prefixNum);

        // loop through each index of image list
        for (int i = 0; i < urls.size(); i++) {
            System.out.println("i: " + i);

            String url = urls.get(i);

            URL currentURL = null;
            try {
                currentURL = new URL(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            // get image from URL
            BufferedImage currentImg = getImageFromURL(currentURL, false, 1);

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
            double progress = (i * 100.0) / urls.size();
            System.out.println(i + " | " + urls.size() + " | " + "Progress: " + progress);

            // set progress bar value
            progress_bar.setValue((int) progress);
            progress_bar.setString((int) progress + "%");

            // set status text with current progress
            status.setText("Downloading..." + i + "/" + urls.size());

            // update panel with progress bar and status text so it refreshes
            panel.updateUI();

        }
    }
}
