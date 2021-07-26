package com.bensuniverse.WebsiteImageExtractor;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

public class MainWindow extends JFrame {

    // used GUI declarations
    private JPanel main_panel;
    private JTextField url;
    private JProgressBar main_progress_bar;
    private JButton download_button;
    private JButton grab_image_button;
    private JComboBox image_list_combobox;
    private JLabel image_preview;
    private JScrollPane middle_scroll_pane;
    private JPanel middle_panel;
    private JTextField file_prefix_textarea;
    private JSpinner start_index_spinner;
    private JTextField included_strings_textfield;
    private JTextField excluded_strings_textfield;
    private JButton update_checkboxes_button;
    private JComboBox extension_combobox;
    private JPanel right_panel;
    private JLabel status_label;
    private JTextField folder_location_textfield;
    private JButton browse_button;
    private JButton move_up_button;
    private JButton move_down_button;
    private JScrollPane left_scroll_pane;
    private JLabel current_position_label;
    private JButton delete_unchecked_boxes_button;

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
    private JLabel footer_label;
    private JCheckBox file_rename_checkbox;
    private JButton select_none_button;
    private JButton select_all_button;
    private JPanel checkbox_button_panel;

    // other variables declarations
    private ArrayList<String> srcs = new ArrayList<String>();
    private ArrayList<String> srcs_NOURL = new ArrayList<String>();

    private ArrayList<JCheckBox> images = new ArrayList<JCheckBox>();

    private String output_folder;

    private int selected_index;

    private ImageGrabber ig = new ImageGrabber();
    private AutoCheckbox ac = new AutoCheckbox();

    public MainWindow() {

        // set window title
        super(Main.getName());

        // set footer label text (version and other info)
        String footer_text = "Version " + Main.getVersion() + " | " + Main.getAuthor() + " | " + Main.getDate();
        footer_label.setText(footer_text);

        // set JScrollPane scroll speed
        left_scroll_pane.getVerticalScrollBar().setUnitIncrement(16);
        middle_scroll_pane.getVerticalScrollBar().setUnitIncrement(16);

        // try to keep JComboBox from expanding (30 characters)
        image_list_combobox.setPrototypeDisplayValue("123456789012345678901234567890");

        // set default selected index
        selected_index = 1;

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

        // get JComboBox model (only way to add/remove items)
        DefaultComboBoxModel model = (DefaultComboBoxModel) image_list_combobox.getModel();

        // "Grab Images" button listener
        grab_image_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                // reset existing list of images
                srcs = new ArrayList<String>();
                srcs_NOURL = new ArrayList<String>();

                // get all image URLs from website, both with http:// and without main URL
                ArrayList srcs_temp = ig.getImages(url.getText(), true);
                ArrayList srcs_NOURL_temp = ig.getImages(url.getText(), false);

                // remove duplicates from lists
                for (int i = 0; i < srcs_temp.size(); i++) {

                    if (!srcs_NOURL.contains(srcs_NOURL_temp.get(i))) {

                        srcs_NOURL.add((String) srcs_NOURL_temp.get(i));
                        srcs.add((String) srcs_temp.get(i));

                    }
                }

                // get list of checkboxes (already checked) based on user input in JTextFields
                images = ac.getCheckboxes(srcs_NOURL, included_strings_textfield.getText(), excluded_strings_textfield.getText());

                updateImageLists(model);

            }
        });

        // "Image List" JComboBox listener
        image_list_combobox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                try {

                    URL url = null;

                    // get selected image file name
                    String selected_url_short = image_list_combobox.getSelectedItem().toString();

                    // get full image URL from just image file name
                    url = ig.getURLfromShortened(srcs, srcs_NOURL, selected_url_short);

                    System.out.println("Image preview url: " + url);

                    // set image_preview (JLabel) icon to a preview of the selected image
                    int width = (int) (main_panel.getWidth() * 0.3);
//                    System.out.println("Width of panel: " + width);
                    image_preview.setIcon(new ImageIcon(ig.getImageFromURL(url, true, width)));

                    selected_index = image_list_combobox.getSelectedIndex();
                    current_position_label.setText("Current position: " + (selected_index + 1) + "/" + srcs_NOURL.size());

                    main_panel.updateUI();

                } catch (NullPointerException e) {

                    // probably refreshing list (new URL inputted)

                }
            }
        });

        // "Update Checkboxes" button listener
        update_checkboxes_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                // remove existing checkboxes
                middle_panel.remove(0);

                // get list of checkboxes (already checked) based on user input in JTextFields
                images = ac.getCheckboxes(srcs_NOURL, included_strings_textfield.getText(), excluded_strings_textfield.getText());

                // create vertical box (same as earlier, to place checkboxes in)
                Box box = Box.createVerticalBox();

                // loop through JCheckBox ArrayList
                for (JCheckBox checkbox : images) {

                    // add checkbox to box (may be checked)
                    box.add(checkbox);

                }

                // add box to panel and refresh
                middle_panel.add(box);
                middle_panel.updateUI();

            }
        });

        // "Download Images" button listener
        download_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                // will be used to get ONLY checked checkboxes
                ArrayList<String> checkedBoxes = new ArrayList<String>();

                    // loop through each JCheckBox in the previously populated JCheckBox ArrayList
                    for (int i = 0; i < ((Box) middle_panel.getComponent(0)).getComponentCount(); i++) {

                        // if checkbox is checked, add full URL to checkedBoxes ArrayList
                        System.out.println(((JCheckBox) ((Box) middle_panel.getComponent(0)).getComponent(i)).isSelected());
                        if (((JCheckBox) ((Box) middle_panel.getComponent(0)).getComponent(i)).isSelected())
                            checkedBoxes.add(ig.getURLfromShortened(srcs, srcs_NOURL, ((JCheckBox) ((Box) middle_panel.getComponent(0)).getComponent(i)).getText().toString()).toString());

                }

                // download images in new thread to allow for progress bar updates and prevent frozen UI
                new Thread(new Runnable() {
                    public void run() {

                        // download all images
                        ig.downloadImages(checkedBoxes, output_folder, file_rename_checkbox.isSelected(), file_prefix_textarea.getText(), extension_combobox.getSelectedItem().toString(), (int) start_index_spinner.getValue(), main_progress_bar, right_panel, status_label);

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

        // "Move up" button listener
        move_up_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                // get selected index of combo box
                int current_index = image_list_combobox.getSelectedIndex();
                System.out.println("current index: " + current_index);

                // make sure image isn't already at the top of the list
                if (current_index > 0) {

                    // get current values at index
                    String temp = srcs.get(current_index);
                    String temp_NOURL = srcs_NOURL.get(current_index);
                    JCheckBox temp_cb = ((JCheckBox) ((Box) middle_panel.getComponent(0)).getComponent(current_index));

                    // add value in front of above item
                    srcs.add(current_index - 1, temp);
                    srcs_NOURL.add(current_index - 1, temp_NOURL);
                    images.add(current_index - 1, temp_cb);

                    // remove old value
                    srcs.remove(current_index + 1);
                    srcs_NOURL.remove(current_index + 1);
                    images.remove(current_index + 1);

                    current_position_label.setText("Current position: " + current_index + "/" + srcs_NOURL.size());

                    updateImageLists(model);

                    image_list_combobox.setSelectedIndex(current_index - 1);

                }

            }
        });

        // "Move down" button listener
        move_down_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                // get selected index of combo box
                int current_index = image_list_combobox.getSelectedIndex();
                System.out.println("current index: " + current_index);

                // make sure image isn't already at the top of the list
                if (current_index < srcs_NOURL.size() - 1) {

                    // get current values at index
                    String temp = srcs.get(current_index);
                    String temp_NOURL = srcs_NOURL.get(current_index);
                    JCheckBox temp_cb = ((JCheckBox) ((Box) middle_panel.getComponent(0)).getComponent(current_index));

                    // add value after of below item
                    srcs.add(current_index + 2, temp);
                    srcs_NOURL.add(current_index + 2, temp_NOURL);
                    images.add(current_index + 2, temp_cb);

                    // remove old value
                    srcs.remove(current_index);
                    srcs_NOURL.remove(current_index);
                    images.remove(current_index);

                    current_position_label.setText("Current position: " + (current_index + 2) + "/" + srcs_NOURL.size());

                    updateImageLists(model);

                    image_list_combobox.setSelectedIndex(current_index + 1);

                }

            }
        });

        // "Select all" button listener
        select_all_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // loop through each JCheckBox in the previously populated JCheckBox ArrayList
                for (int i = 0; i < ((Box) middle_panel.getComponent(0)).getComponentCount(); i++) {

                    ((JCheckBox) ((Box) middle_panel.getComponent(0)).getComponent(i)).setSelected(true);

                }
            }
        });

        // "Select none" button listener
        select_none_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // loop through each JCheckBox in the previously populated JCheckBox ArrayList
                for (int i = 0; i < ((Box) middle_panel.getComponent(0)).getComponentCount(); i++) {

                    ((JCheckBox) ((Box) middle_panel.getComponent(0)).getComponent(i)).setSelected(false);

                }
            }
        });

        // "Delete unchecked boxes" button listener
        delete_unchecked_boxes_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                // remove all unchecked boxes
                ArrayList<String> remove_srcs = new ArrayList<String>();
                ArrayList<String> remove_srcs_NOURL = new ArrayList<String>();
                ArrayList<JCheckBox> remove_checkboxes = new ArrayList<JCheckBox>();

                // loop through each JCheckBox in the previously populated JCheckBox ArrayList
                for (int i = 0; i < ((Box) middle_panel.getComponent(0)).getComponentCount(); i++) {

                    // if checkbox is checked, add full URL to checkedBoxes ArrayList
                    System.out.println(((JCheckBox) ((Box) middle_panel.getComponent(0)).getComponent(i)).isSelected());
                    if (!((JCheckBox) ((Box) middle_panel.getComponent(0)).getComponent(i)).isSelected()) {

                        // remove unchecked items from all lists
                        remove_srcs.add(srcs.get(i));
                        remove_srcs_NOURL.add(srcs_NOURL.get(i));
                        remove_checkboxes.add(((JCheckBox) ((Box) middle_panel.getComponent(0)).getComponent(i)));

                    }
                }
                srcs.removeAll(remove_srcs);
                srcs_NOURL.removeAll(remove_srcs_NOURL);
                images.removeAll(remove_checkboxes);

                updateImageLists(model);

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

                // force image_list_combobox action listener
                image_list_combobox.setSelectedIndex(image_list_combobox.getSelectedIndex());

            }
        });

        // other JPanel properties
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(main_panel);
        this.setSize(850, 550);
        this.setMinimumSize(new Dimension(850, 550));
//        this.setResizable(false);
        this.setLocationRelativeTo(null);

        // set JFrame icon
        try {

            this.setIconImage(new ImageIcon(ImageIO.read(getClass().getResource("/icon.png"))).getImage());

        } catch (IOException e) {

            e.printStackTrace();

        }

        ArrayList<String> test = new ArrayList<String>();
        test.add("A");
        test.add("B");
        test.add("E");
        test.add("C");
        test.add("D");
        JScrollPane pane = new ImageList().getImageList(test);
        middle_panel.add(pane);

        // set URL text field to be selected
        url.requestFocusInWindow();
    }

    private void updateImageLists(DefaultComboBoxModel model) {

        // make a vertical box (will place JCheckBoxes in this)
        Box box = Box.createVerticalBox();

        // clear JComboBox (old image names)
        model.removeAllElements();

        // loop through all image file names
        for (String src : srcs_NOURL) {

            // add image name to JComboBox
            model.addElement(src);

        }

        // add each checkbox from images
        for (JCheckBox cb : images) {

            box.add(cb);

        }

        try {

            // remove existing box (old checkboxes)
            middle_panel.remove(0);

        } catch (ArrayIndexOutOfBoundsException e) {

            // nothing in panel (first run of program)

        }

        // empty list
        if (srcs.size() == 0) {

            current_position_label.setText("Current position: 0/0");
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

        // force image_list_combobox action listener
        image_list_combobox.setSelectedIndex(image_list_combobox.getSelectedIndex());

        // add checkboxes and update panel to refresh
        middle_panel.add(box);
        middle_panel.updateUI();

    }
}