# Website Image Extractor

This program is a crude way to extract and download images from the HTML of various websites.  Given a URL, the program scans the HTML for that page and looks for image links and other image references.  Then, the list of images is presented to the user and unwanted images can be removed.

Images can be selected using pattern recognition chosen by the user.  Images can be automatically selected or deselected from the master list of images using keyword recognition (explained in detail below). 

Additionally, the order in which the images should be downloaded can be changed (for images that need to be named in sequential order).  Before downloading the images, various output file types can be selected and custom image naming schemes can be used (explained in detail below).

Currently, the program does not support extracting images from pages which use JavaScript or other languages to render images after the page is loaded; only image URLs found in the HTML code of the given URL can be downloaded.

---

## Usage instructions can be found at [Ben's Universe](https://www.bensuniverse.com/software/website-image-extractor/)

---

## Other Information

#### This program creates no configuration file and no data is stored

#### Libraries Used
- [FlatLaf Dark](https://www.formdev.com/flatlaf/) used for the look and feel of GUI
- [WebP ImageIO Core](https://github.com/nintha/webp-imageio-core) to support downoading WebP files

#### Technical Information
- Java 8 or higher required