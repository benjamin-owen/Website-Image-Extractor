package com.bensuniverse.WebsiteImageExtractor;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class ImageList extends JFrame {

    JList<String> list;
    DefaultListModel<ImageObject> list_model;

    // pass image URLs and return pane with image list
    public JScrollPane getImageList(ArrayList<ImageObject> images) {

        list_model = createStringListModel(images);
        list = new JList(list_model);
        MyMouseAdapter adapter = new MyMouseAdapter();
        list.addMouseListener(adapter);
        list.addMouseMotionListener(adapter);
        list.setBackground(null);

        JScrollPane pane = new JScrollPane(list);
        pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        pane.setPreferredSize(new Dimension(385, 395));
        pane.setBorder(null);

        return pane;

    }

    public JList getList() {

        return list;

    }

    public DefaultListModel getModel() {

        return list_model;

    }

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
                    ImageObject dragElement = list_model.get(dragSourceIndex);
                    list_model.remove(dragSourceIndex);
                    list_model.add(dragTargetIndex, dragElement);
                    dragSourceIndex = currentIndex;
                }
            }
        }
    }

    private DefaultListModel<ImageObject> createStringListModel(ArrayList<ImageObject> listElements) {
        DefaultListModel<ImageObject> listModel = new DefaultListModel<ImageObject>();
        for (ImageObject element : listElements) {
            listModel.addElement(element);
        }
        return listModel;
    }
}