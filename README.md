# Website Image Extractor

This program is a crude way to extract and download images from the HTML of various websites.  Given a URL, the program scans the HTML for that page and looks for image links and other image references.  Then, the list of images is presented to the user and unwanted images can be removed.

Images can be selected using pattern recognition chosen by the user.  Images can be automatically selected or deselected from the master list of images using keyword recognition (explained in detail below). 

Additionally, the order in which the images should be downloaded can be changed (for images that need to be named in sequential order).  Before downloading the images, various output file types can be selected and custom image naming schemes can be used (explained in detail below).

Currently, the program does not support extracting images from pages which use JavaScript or other languages to render images after the page is loaded; only image URLs found in the HTML code of the given URL can be downloaded.

---

## Usage Instructions

<table>
<caption>GUI Fields and Descriptions</caption>
<tr>
<th width = "20%">Field</th>
<th width = "35%">Description</th>
<th width = "45%">Image</th>
</tr>

<tr>
<td>URL Entry</td>
<td>A URL must first be provided from which to extract the images from.  To do this, enter the URL in the top field and select the "Grab Images" button.  The images will then be extracted and the other fields in the program can be used for further manipulation.</td>
<td width = "33%"><img src = "https://www.bensuniverse.com/media/software/website-image-extractor/Website-Image-Extractor-url.jpg" alt = "Website Image Extractor URL entry"></td>
</tr>

<tr>
<td>Image Preview and Order</td>
<td>The leftmost panel in the GUI is reserved for image previews and manipulation of the order of images in the image list (center panel, explained in detail below).  After selecting an image from the dropdown, a preview of that image will appear in the space below the "Move up" and "Move down" buttons.  If the image URL is not valid (invalid URL or other issue), a "File not found" image will be displayed instead.

Using the "Move up" and "Move down" buttons, the current selected image (from the dropdown) will be moved up and down on the list of all images, respectively.  This is useful for images that need to be named in sequential order to correct any images that are not in the desired order.</td>
<td><img src = "https://www.bensuniverse.com/media/software/website-image-extractor/Website-Image-Extractor-image-preview.jpg" alt = "Image preview and order"></td>
</tr>

<tr>
<td>Image List</td>
<td>The center panel is used to list all of the extracted images and the order they will be downloaded/named in.  The only manipulation available in this panel is the ability to check/uncheck individual images to be marked for renaming/downloading.  The "Delete unchecked boxes" at the bottom of the panel will remove unchecked images from the list of images.</td>
<td><img src = "https://www.bensuniverse.com/media/software/website-image-extractor/Website-Image-Extractor-image-list.jpg" alt = "Image list"></td>
</tr>

<tr>
<td>Auto-Select Settings</td>
<td>The auto-select settings are used to check/uncheck images in the image list automatically based on keywords entered by the user.  The "Including" field (populated with a comma-separated list) will check all images in the list and check them <u>only if an image name contains <b>ALL</b> of the keywords in the list.</u>  The "excluding field" (populated with a comma-separated list) will uncheck all images in the list containing <u><b>ANY</b> of the keywords in the list.</u></td>
<td><img src = "https://www.bensuniverse.com/media/software/website-image-extractor/Website-Image-Extractor-auto-select.jpg" alt = "Auto-select settings"></td>
</tr>

<tr>
<td>Image Output Settings</td>
<td>The image output settings are used to determine the output image file name.  The "File prefix" field is added at the beginning of the image file name.  The "Start index" spinner determines which integer value to begin counting for sequential image names.  The "Extension" dropdown is used to select the image file type.

If you wish to preserve the original image file names, simply uncheck the "Enable custom file names" checkbox, and those fields will be disabled and ignored.

The "Browse" button can be used to select a different image output location (default is the same location that the program is running in).</td>
<td><img src = "https://www.bensuniverse.com/media/software/website-image-extractor/Website-Image-Extractor-image-output.jpg" alt = "Image output settings"></td>
</tr>

<tr>
<td>Download Images</td>
<td>Once all the settings are chosen and correct images are selected and in the correct order in the image list, use the "Download!" button to begin downloading all images.  The progress bar will show the current download progress and text will display how many images have been downloaded and the total number of images to download.</td>
<td><img src = "https://www.bensuniverse.com/media/software/website-image-extractor/Website-Image-Extractor-download.jpg" alt = "Download images"></td>
</tr>
</table>

---

## Other Information

#### This program creates no configuration file and no data is stored

#### Libraries Used
- [FlatLaf Dark](https://www.formdev.com/flatlaf/) used for the look and feel of GUI