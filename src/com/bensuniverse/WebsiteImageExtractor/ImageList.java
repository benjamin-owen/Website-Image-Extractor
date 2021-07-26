package com.bensuniverse.WebsiteImageExtractor;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class ImageList extends JFrame {

    JList<String> list;
    DefaultListModel<String> list_model;
    
    public JScrollPane getImageList(ArrayList<String> images) {

        list_model = createStringListModel(images);
        list = new JList<String>(list_model);
        MyMouseAdapter adapter = new MyMouseAdapter();
        list.addMouseListener(adapter);
        list.addMouseMotionListener(adapter);
        list.setBackground(null);

        JScrollPane pane = new JScrollPane(list);
        pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        pane.setPreferredSize(new Dimension(385, 300));
        pane.setBorder(null);

        return pane;
    }

//    public ImageList() {
//        ArrayList<String> test = new ArrayList<String>();
//        test.add("A");
//        test.add("B");
//        test.add("E");
//        test.add("C");
//        test.add("D");
//        list_model = createStringListModel(test);
//        list = new JList<String>(list_model);
//        MyMouseAdapter adapter = new MyMouseAdapter();
//        list.addMouseListener(adapter);
//        list.addMouseMotionListener(adapter);
//
//        JScrollPane pane = new JScrollPane(list);
//        pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
//
//        JPanel content = new JPanel();
//        content.add(pane);
//        this.add(content);
//        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        this.pack();
//        this.setVisible(true);
//    }

    private class MyMouseAdapter extends MouseInputAdapter {
        private boolean mouseDragging = false;
        private int dragSourceIndex;

        @Override
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                dragSourceIndex = list.getSelectedIndex();
                mouseDragging = true;
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            mouseDragging = false;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (mouseDragging) {
                int currentIndex = list.locationToIndex(e.getPoint());
                if (currentIndex != dragSourceIndex) {
                    int dragTargetIndex = list.getSelectedIndex();
                    String dragElement = list_model.get(dragSourceIndex);
                    list_model.remove(dragSourceIndex);
                    list_model.add(dragTargetIndex, dragElement);
                    dragSourceIndex = currentIndex;
                }
            }
        }
    }

    private DefaultListModel<String> createStringListModel(ArrayList<String> listElements) {
//        final String[] listElements = new String[] { "Cat", "Dog", "Cow", "Horse", "Pig", "Monkey" };
        DefaultListModel<String> listModel = new DefaultListModel<String>();
        for (String element : listElements) {
            listModel.addElement(element);
        }
        return listModel;
    }
}