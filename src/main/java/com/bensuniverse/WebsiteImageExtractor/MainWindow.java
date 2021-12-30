package com.bensuniverse.WebsiteImageExtractor;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

public class MainWindow extends JFrame {

    // other variables declarations
    private ArrayList<ImageObject> images = new ArrayList<ImageObject>();

    private URL selected_image;

    private String output_folder;

    private final ImageGrabber ig = new ImageGrabber();
    private final AutoCheckbox ac = new AutoCheckbox();

    private final ImageList il = new ImageList();

    public MainWindow() {

        // set window title
        super(Main.getName() + " v" + Main.getVersion());

        // set layout
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(2, 5, 2, 5);

        /*
        *   TOP BAR (URL, GRAB IMAGES BUTTON) OBJECTS
        */

        // "Enter URL" text
        JLabel enter_url_label = new JLabel(bold("Enter URL:"), SwingConstants.RIGHT);
        c = updateConstraintsPosition(c, 0, 0, 2, 1);
        this.add(enter_url_label, c);

        // URL text field
        JTextField url = new JTextField();
        c = updateConstraintsPosition(c, 2, 0, 2, 1);
        this.add(url, c);

        // "Grab Images" button
        JButton grab_images_button = new JButton("Grab Images");
        c = updateConstraintsPosition(c, 4, 0, 2, 1);
        this.add(grab_images_button, c);

        /*
        *   IMAGE PREVIEW OBJECTS
        */

        // "Image Preview" text
        JLabel image_preview_label = new JLabel(bold("Image Preview:"));
        c = updateConstraintsPosition(c, 0, 1, 2, 1);
        this.add(image_preview_label, c);

        // "Name:" text
        JLabel image_name_label = new JLabel("Name:");
        c = updateConstraintsPosition(c, 0, 2, 1, 1);
        this.add(image_name_label, c);

        // image name label
        JLabel image_name = new JLabel();
        c = updateConstraintsPosition(c, 1, 2, 1, 1);
        this.add(image_name, c);

        // "Resolution:" text
        JLabel image_resolution_label = new JLabel("Resolution:");
        c = updateConstraintsPosition(c, 0, 3, 1, 1);
        this.add(image_resolution_label, c);

        // image resolution label
        JLabel image_resolution = new JLabel();
        c = updateConstraintsPosition(c, 1, 3, 1, 1);
        this.add(image_resolution, c);

        // image preview
        JScrollPane image_preview_pane = new JScrollPane();
        JLabel image_preview = new JLabel();
        image_preview_pane.add(image_preview);
        c = updateConstraintsPosition(c, 0, 4, 2, 13);
        this.add(image_preview_pane, c);

        /*
        *   IMAGE LIST OBJECTS
        */

        // "Image List:" text
        JLabel image_list_label = new JLabel(bold("Image List:"));
        c = updateConstraintsPosition(c, 2, 1, 2, 1);
        this.add(image_list_label, c);

        // image list pane
        JScrollPane image_list_pane = new JScrollPane();
        JPanel middle_panel = new JPanel();
        image_list_pane.add(middle_panel);
        c = updateConstraintsPosition(c, 2, 2, 2, 12);
        this.add(image_list_pane, c);

        // "Select All" button
        JButton select_all_button = new JButton("Select all");
        c = updateConstraintsPosition(c, 2, 14, 1, 1);
        this.add(select_all_button, c);

        // "Delete selected items" button
        JButton delete_button = new JButton("Delete selected items");
        c = updateConstraintsPosition(c, 3, 14, 1, 1);
        this.add(delete_button, c);

        /*
        *   AUTO SELECT SETTINGS OBJECTS
        */

        // "Auto-Select Settings:" text
        JLabel auto_select_settings_label = new JLabel(bold("Auto-Select Settings:"));
        c = updateConstraintsPosition(c, 4, 1, 2, 1);
        this.add(auto_select_settings_label, c);

        // "Including:" text
        JLabel included_label = new JLabel("Including:");
        c = updateConstraintsPosition(c, 4, 2, 1, 1);
        this.add(included_label, c);

        // included strings text field
        JTextField included_strings_textfield = new JTextField();
        c = updateConstraintsPosition(c, 5, 2, 1, 1);
        this.add(included_strings_textfield, c);

        // "Excluding:" text
        JLabel excluded_label = new JLabel("Excluding:");
        c = updateConstraintsPosition(c, 4, 3, 1, 1);
        this.add(excluded_label, c);

        // excluded strings text field
        JTextField excluded_strings_textfield = new JTextField();
        c = updateConstraintsPosition(c, 5, 3, 1, 1);
        this.add(excluded_strings_textfield, c);

        // "Update Selected Images" button
        JButton update_selected_button = new JButton("Update Selected Images");
        c = updateConstraintsPosition(c, 4, 4, 2, 1);
        this.add(update_selected_button, c);

        /*
        *   OUTPUT SETTINGS OBJECTS
        */

        // "Image Output Settings:" text
        JLabel image_output_settings_label = new JLabel(bold("Image Output Settings:"));
        c = updateConstraintsPosition(c, 4, 5, 2, 1);
        this.add(image_output_settings_label, c);

        // enable sequential file names checkbox
        JCheckBox sequential_file_names_checkbox = new JCheckBox("Enable sequential file names");
        sequential_file_names_checkbox.setSelected(true);
        c = updateConstraintsPosition(c, 4, 6, 2, 1);
        this.add(sequential_file_names_checkbox, c);

        // "File Prefix:" text
        JLabel file_prefix_label = new JLabel("File Prefix:");
        c = updateConstraintsPosition(c, 4, 7, 1, 1);
        this.add(file_prefix_label, c);

        // file prefix textarea
        JTextArea file_prefix_textarea = new JTextArea();
        c = updateConstraintsPosition(c, 5, 7, 1, 1);
        this.add(file_prefix_textarea, c);

        // "Start Index:" text
        JLabel start_index_label = new JLabel("Start Index:");
        c = updateConstraintsPosition(c, 4, 8, 1, 1);
        this.add(start_index_label, c);

        // start index spinner
        JSpinner start_index_spinner = new JSpinner();
        c = updateConstraintsPosition(c, 5, 8, 1, 1);
        this.add(start_index_spinner, c);

        // "Extension:" text
        JLabel extension_label = new JLabel("Extension:");
        c = updateConstraintsPosition(c, 4, 9, 1, 1);
        this.add(extension_label, c);

        // file extension dropdown
        String[] extensions = { ".png", ".jpg", ".gif" };
        JComboBox<String> extension_combobox = new JComboBox<String>(extensions);
        c = updateConstraintsPosition(c, 5, 9, 1, 1);
        this.add(extension_combobox, c);

        // "Location:" text
        JLabel location_label = new JLabel("Location:");
        c = updateConstraintsPosition(c, 4, 10, 1, 1);
        this.add(location_label, c);

        // location textfield
        JTextField location_textfield = new JTextField();
        c = updateConstraintsPosition(c, 5, 10, 1, 1);
        this.add(location_textfield, c);

        // "Browse" button
        JButton browse_button = new JButton("Browse");
        c = updateConstraintsPosition(c, 4, 11, 2, 1);
        this.add(browse_button, c);

        // horizontal separator
        c = updateConstraintsPosition(c, 4, 12, 2, 1);
        this.add(new JSeparator(), c);

        // "Download" button
        JButton download_button = new JButton("Download!");
        c = updateConstraintsPosition(c, 4, 13, 2, 1);
        this.add(download_button, c);

        // progress bar
        JProgressBar progress_bar = new JProgressBar();
        progress_bar.setStringPainted(true);
        c = updateConstraintsPosition(c, 4, 14, 2, 1);
        this.add(progress_bar, c);

        // status text
        JLabel status_label = new JLabel();
        c = updateConstraintsPosition(c, 4, 15, 2, 1);
        this.add(status_label, c);

        // set JScrollPane scroll speed
        image_preview_pane.getVerticalScrollBar().setUnitIncrement(16);
        image_list_pane.getVerticalScrollBar().setUnitIncrement(16);

        // set default selected index
        int selected_index = 1;

        // set default output folder to current working directory
        try {
            File jar_location = new File (Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            output_folder = jar_location.getAbsolutePath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
//        output_folder = System.getProperty("user.dir");
        location_textfield.setText(output_folder + "/");
        if (output_folder.equals("/")) // root dir
            location_textfield.setText("/");

        // "Grab Images" button listener
        grab_images_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                // get all image URLs from website, both with http:// and without main URL
                images = ig.getImages(url.getText());

                updateImageLists();

            }
        });

        // "Update Selected Images" button listener
        update_selected_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                // get list of indicies to select
                ArrayList<Integer> indicies_list = ac.getSelection(images, included_strings_textfield.getText(), excluded_strings_textfield.getText());
                int[] indicies = new int[indicies_list.size()];
                for (int i = 0; i < indicies.length; i++) {

                    indicies[i] = indicies_list.get(i);

                }
                il.getList().setSelectedIndices(indicies);

                // give list focus
                il.getList().requestFocusInWindow();
                middle_panel.updateUI();

            }
        });

        // "Download Images" button listener
        download_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                // download images in new thread to allow for progress bar updates and prevent frozen UI
                new Thread(new Runnable() {
                    public void run() {

                        // download all images
                        ig.downloadImages(images, output_folder, sequential_file_names_checkbox.isSelected(), file_prefix_textarea.getText(), extension_combobox.getSelectedItem().toString(), (int) start_index_spinner.getValue(), progress_bar, right_panel, status_label);

                        // when finished, set status text and fill progress bar
                        status_label.setText("Done!");
                        progress_bar.setValue(100);
                        progress_bar.setString("100%");

                    }
                }).start();
            }
        });

        // "Browse" button listener
        browse_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                JFileChooser file_chooser = new JFileChooser(); // create file chooser
                file_chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // only allow choosing directories
                file_chooser.setDialogTitle("Select Output Directory"); // set dialog title

                // set current file chooser directory to previously selected directory
                file_chooser.setCurrentDirectory(new File(location_textfield.getText()));

//                file_chooser.setCurrentDirectory(new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI())); // open in current folder

                if (file_chooser.showDialog(MainWindow.getFrames()[0], "Select") == JFileChooser.APPROVE_OPTION) { // if user selects a file

                    File file = file_chooser.getSelectedFile(); // get current "folder"
                    output_folder = file.getAbsolutePath(); // append last directory to path
                    output_folder = file.getAbsolutePath().equals("/") ? output_folder : output_folder + "/"; // don't append '/' to path if in root dir
                    location_textfield.setText(output_folder); // set text field = new file path

                }
            }
        });

        // "Select all" button listener
        select_all_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                il.getList().setSelectionInterval(0, il.getModel().getSize());
                il.getList().requestFocusInWindow();

            }
        });

        // "Delete selected items" button listener
        delete_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                // get selected values and remove them from images
                int[] to_remove = il.getList().getSelectedIndices();
                for (int i = to_remove.length - 1; i >= 0; i--) {

                    System.out.println("Removing: " + to_remove[i]);
                    images.remove(to_remove[i]);

                }

                updateImageLists();

            }
        });

        // "Enable custom file names" checkbox listener
        sequential_file_names_checkbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // disable naming fields if unselected
                boolean selected = sequential_file_names_checkbox.isSelected();

                file_prefix_label.setEnabled(selected);
                file_prefix_textarea.setEnabled(selected);
                start_index_label.setEnabled(selected);
                start_index_spinner.setEnabled(selected);
                extension_label.setEnabled(selected);
                extension_combobox.setEnabled(selected);

            }
        });

        // window resize listener
        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent componentEvent) {

                // update image preview
                int width = (int) (image_preview_pane.getWidth() * 0.3);
                // image_preview_panel.setSize(width, main_panel.getHeight());
                image_preview_pane.updateUI();

                try {

                    image_preview.setIcon(new ImageIcon((BufferedImage) ig.getImageFromURL(selected_image, true, width)[0]));
                    image_preview_pane.updateUI();

                } catch (NullPointerException e) {

                    System.out.println("No image selected!");

                }
            }
        });

        // other JPanel properties
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // this.setContentPane(main_panel);
        this.setSize(850, 550);
        this.setMinimumSize(new Dimension(850, 550));
        this.setLocationRelativeTo(null);

        // set JFrame icon
        try {

            this.setIconImage(new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource("/icon.png")))).getImage());

        } catch (IOException e) {

            e.printStackTrace();

        }

        // set URL text field to be selected
        url.requestFocusInWindow();
    }

    private void updateImageLists() {

        try {

            // remove existing box (old checkboxes)
            list_p.remove(0);

        } catch (ArrayIndexOutOfBoundsException e) {

            // nothing in panel (first run of program)

        }

        // empty list
        if (images.size() == 0) {

            image_preview.setIcon(null);

        }

        // set other miscellaneous variables
        progress_bar.setValue(0);
        progress_bar.setString("0%");
        status_label.setText("");
        included_strings_textfield.setText("");
        excluded_strings_textfield.setText("");
        file_prefix_textarea.setText("");
        start_index_spinner.setValue(0);

        // add checkboxes and update panel to refresh
        JScrollPane image_list = il.getImageList(images);

        // add image list to middle panel
        middle_panel.setLayout(new BorderLayout());
        middle_panel.add(image_list, BorderLayout.CENTER);

        // list selection listener
        il.getList().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {

                try {

                    selected_image = ((ImageObject) il.getList().getSelectedValue()).getURL();

                    int width = (int) (main_panel.getWidth() * 0.3);
                    left_panel.setSize((int) (width * 2), main_panel.getHeight());
                    System.out.println("Width (int): " + width);

                    // update image preview
                    Object[] image = ig.getImageFromURL(selected_image, true, width);
                    image_preview.setIcon(new ImageIcon((BufferedImage) image[0]));

                    String image_title = il.getList().getSelectedValue().toString();
                    if (image_title.length() > 25)
                        image_name.setText(image_title.substring(0, 25) + "...");
                    else
                        image_name.setText(image_title);
                    image_res.setText(image[1] + " x " + image[2]);
                    main_panel.updateUI();

                } catch (NullPointerException npe) {

                    main_panel.updateUI();

                }

            }
        });

        middle_panel.updateUI();

    }

    private String bold(String input) {

        StringBuilder sb = new StringBuilder();
        sb.append("<html><b>");
        sb.append(input);
        sb.append("</html></b>");
        return sb.toString();
        
    }

    private GridBagConstraints updateConstraintsPosition(GridBagConstraints c, int gridx, int gridy, int gridwidth, int gridheight) {

        c.gridx = gridx;
        c.gridy = gridy;
        c.gridwidth = gridwidth;
        c.gridheight = gridheight;

        return c;

    }
}