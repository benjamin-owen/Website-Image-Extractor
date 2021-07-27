package com.bensuniverse.WebsiteImageExtractor;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragSource;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

public class ImageList extends JFrame {

    JList<String> list;
    DefaultListModel<ImageObject> list_model;

    // pass image URLs and return pane with image list
    public JScrollPane getImageList(ArrayList<ImageObject> images) {

        list_model = createListModel(images);
        list = new JList(list_model);
        list.setDragEnabled(true);
        list.setDropMode(DropMode.INSERT);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.setBackground(null);

        list.setTransferHandler(new ListItemTransferHandler());

        JScrollPane pane = new JScrollPane(list);
        pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        pane.setPreferredSize(new Dimension(355, 395));
        pane.setBorder(null);

        return pane;

    }

    public JList getList() {

        return list;

    }

    public DefaultListModel getModel() {

        return list_model;

    }

    @SuppressWarnings("serial")
    class ListItemTransferHandler extends TransferHandler {
        protected final DataFlavor localObjectFlavor;
        protected int[] indices;
        protected int addIndex = -1; // Location where items were added
        protected int addCount; // Number of items added.

        public ListItemTransferHandler() {
            super();
            // localObjectFlavor = new ActivationDataFlavor(
            //   Object[].class, DataFlavor.javaJVMLocalObjectMimeType, "Array of items");
            localObjectFlavor = new DataFlavor(Object[].class, "Array of items");
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            JList<?> source = (JList<?>) c;
            c.getRootPane().getGlassPane().setVisible(true);

            indices = source.getSelectedIndices();
            Object[] transferedObjects = source.getSelectedValuesList().toArray(new Object[0]);
            // return new DataHandler(transferedObjects, localObjectFlavor.getMimeType());
            return new Transferable() {
                @Override public DataFlavor[] getTransferDataFlavors() {
                    return new DataFlavor[] {localObjectFlavor};
                }
                @Override public boolean isDataFlavorSupported(DataFlavor flavor) {
                    return Objects.equals(localObjectFlavor, flavor);
                }
                @Override public Object getTransferData(DataFlavor flavor)
                        throws UnsupportedFlavorException, IOException {
                    if (isDataFlavorSupported(flavor)) {
                        return transferedObjects;
                    } else {
                        throw new UnsupportedFlavorException(flavor);
                    }
                }
            };
        }

        @Override
        public boolean canImport(TransferSupport info) {
            return info.isDrop() && info.isDataFlavorSupported(localObjectFlavor);
        }

        @Override
        public int getSourceActions(JComponent c) {
            Component glassPane = c.getRootPane().getGlassPane();
            glassPane.setCursor(DragSource.DefaultMoveDrop);
            return MOVE; // COPY_OR_MOVE;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean importData(TransferSupport info) {
            TransferHandler.DropLocation tdl = info.getDropLocation();
            if (!canImport(info) || !(tdl instanceof JList.DropLocation)) {
                return false;
            }

            JList.DropLocation dl = (JList.DropLocation) tdl;
            JList target = (JList) info.getComponent();
            DefaultListModel listModel = (DefaultListModel) target.getModel();
            int max = listModel.getSize();
            int index = dl.getIndex();
            index = index < 0 ? max : index; // If it is out of range, it is appended to the end
            index = Math.min(index, max);

            addIndex = index;

            try {
                Object[] values = (Object[]) info.getTransferable().getTransferData(localObjectFlavor);
                for (int i = 0; i < values.length; i++) {
                    int idx = index++;
                    listModel.add(idx, values[i]);
                    target.addSelectionInterval(idx, idx);
                }
                addCount = values.length;
                return true;
            } catch (UnsupportedFlavorException | IOException ex) {
                ex.printStackTrace();
            }

            return false;
        }

        @Override
        protected void exportDone(JComponent c, Transferable data, int action) {
            c.getRootPane().getGlassPane().setVisible(false);
            cleanup(c, action == MOVE);
        }

        private void cleanup(JComponent c, boolean remove) {
            if (remove && Objects.nonNull(indices)) {
                if (addCount > 0) {
                    // https://github.com/aterai/java-swing-tips/blob/master/DragSelectDropReordering/src/java/example/MainPanel.java
                    for (int i = 0; i < indices.length; i++) {
                        if (indices[i] >= addIndex) {
                            indices[i] += addCount;
                        }
                    }
                }
                JList source = (JList) c;
                DefaultListModel model = (DefaultListModel) source.getModel();
                for (int i = indices.length - 1; i >= 0; i--) {
                    model.remove(indices[i]);
                }
            }

            indices = null;
            addCount = 0;
            addIndex = -1;
        }
    }

//    private class MyMouseAdapter extends MouseInputAdapter {
//        private boolean mouseDragging = false;
//        private int dragSourceIndex;
//
//        @Override
//        public void mousePressed(MouseEvent e) {
//            if (SwingUtilities.isLeftMouseButton(e)) {
//                dragSourceIndex = list.getSelectedIndex();
//                mouseDragging = true;
//            }
//        }
//
//        @Override
//        public void mouseReleased(MouseEvent e) {
//            mouseDragging = false;
//        }
//
//        @Override
//        public void mouseDragged(MouseEvent e) {
//            if (mouseDragging) {
//                int currentIndex = list.locationToIndex(e.getPoint());
//                if (currentIndex != dragSourceIndex) {
//                    int dragTargetIndex = list.getSelectedIndex();
//                    ImageObject dragElement = list_model.get(dragSourceIndex);
//                    list_model.remove(dragSourceIndex);
//                    list_model.add(dragTargetIndex, dragElement);
//                    dragSourceIndex = currentIndex;
//                }
//            }
//        }
//    }

    private DefaultListModel<ImageObject> createListModel(ArrayList<ImageObject> listElements) {
        DefaultListModel<ImageObject> listModel = new DefaultListModel<ImageObject>();
        for (ImageObject element : listElements) {
            listModel.addElement(element);
        }
        return listModel;
    }
}