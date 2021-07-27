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

    // used GUI declarations
    private JPanel main_panel;
    private JTextField url;
    private JProgressBar main_progress_bar;
    private JButton download_button;
    private JButton grab_image_button;
    private JLabel image_preview;
    private JScrollPane middle_scroll_pane;
    private JPanel middle_panel;
    private JTextField file_prefix_textarea;
    private JSpinner start_index_spinner;
    private JTextField included_strings_textfield;
    private JTextField excluded_strings_textfield;
    private JButton update_selected_button;
    private JComboBox extension_combobox;
    private JPanel right_panel;
    private JLabel status_label;
    private JTextField folder_location_textfield;
    private JButton browse_button;
    private JScrollPane left_scroll_pane;
    private JButton delete_selected_items_button;

    // unused GUI declarations
    private JLabel enter_url_label;
    private JLabel extension_label;
    private JLabel image_preview_label;
    private JLabel select_images_label;
    private JLabel auto_select_settings_label;
    private JLabel including_label;
    private JLabel excluding_label;
    private JLabel image_output_settings_label;
    private JLabel file_prefix_label;
    private JLabel start_index_label;
    private JLabel folder_location_label;
    private JSeparator separator_02;
    private JSeparator separator_01;
    private JPanel left_panel;
    private JCheckBox file_rename_checkbox;
    private JButton select_all_button;
    private JPanel checkbox_button_panel;
    private JLabel image_name_label;
    private JLabel image_resolution_label;
    private JLabel image_name;
    private JLabel image_res;

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

        // set JScrollPane scroll speed
        left_scroll_pane.getVerticalScrollBar().setUnitIncrement(16);
        middle_scroll_pane.getVerticalScrollBar().setUnitIncrement(16);

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
        folder_location_textfield.setText(output_folder + "/");
        if (output_folder.equals("/")) // root dir
            folder_location_textfield.setText("/");

        // "Grab Images" button listener
        grab_image_button.addActionListener(new ActionListener() {
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
                        ig.downloadImages(images, output_folder, file_rename_checkbox.isSelected(), file_prefix_textarea.getText(), extension_combobox.getSelectedItem().toString(), (int) start_index_spinner.getValue(), main_progress_bar, right_panel, status_label);

                        // when finished, set status text and fill progress bar
                        status_label.setText("Done!");
                        main_progress_bar.setValue(100);
                        main_progress_bar.setString("100%");

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
                file_chooser.setCurrentDirectory(new File(folder_location_textfield.getText()));

//                file_chooser.setCurrentDirectory(new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI())); // open in current folder

                if (file_chooser.showDialog(MainWindow.getFrames()[0], "Select") == JFileChooser.APPROVE_OPTION) { // if user selects a file

                    File file = file_chooser.getSelectedFile(); // get current "folder"
                    output_folder = file.getAbsolutePath(); // append last directory to path
                    output_folder = file.getAbsolutePath().equals("/") ? output_folder : output_folder + "/"; // don't append '/' to path if in root dir
                    folder_location_textfield.setText(output_folder); // set text field = new file path

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
        delete_selected_items_button.addActionListener(new ActionListener() {
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
        file_rename_checkbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // disable naming fields if unselected
                boolean selected = file_rename_checkbox.isSelected();

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
                int width = (int) (main_panel.getWidth() * 0.3);
                left_panel.setSize(width, main_panel.getHeight());
                left_panel.updateUI();

                try {

                    image_preview.setIcon(new ImageIcon((BufferedImage) ig.getImageFromURL(selected_image, true, width)[0]));
                    main_panel.updateUI();

                } catch (NullPointerException e) {

                    System.out.println("No image selected!");

                }
            }
        });

        // other JPanel properties
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(main_panel);
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
            middle_panel.remove(0);

        } catch (ArrayIndexOutOfBoundsException e) {

            // nothing in panel (first run of program)

        }

        // empty list
        if (images.size() == 0) {

            image_preview.setIcon(null);

        }

        // set other miscellaneous variables
        main_progress_bar.setValue(0);
        main_progress_bar.setString("0%");
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
}