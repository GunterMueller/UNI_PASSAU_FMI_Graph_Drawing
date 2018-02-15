// =============================================================================
//
//   StackQueueButton.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.stackqueuelayout;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class StackQueueButton extends JButton {
    /**
     * 
     */
    private static final long serialVersionUID = -7393702994936124040L;
    private StackQueueSat sat;

    public StackQueueButton(int stackCount, int queueCount) {
        setText(createText(stackCount, queueCount));
        
        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                executeSatAction();
            }
        });
        
        final JPopupMenu popupMenu = new JPopupMenu();
        
        JMenuItem allPermutationsItem = new JMenuItem("All Permutations");
        allPermutationsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    System.out.println(sat.calculateAllPermuations());
                } catch (InterruptedException ee) {
                }
            }
        });
        popupMenu.add(allPermutationsItem);
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showPopupIfApt(e);
            }
            
            private void showPopupIfApt(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    popupMenu.show(StackQueueButton.this, e.getX(), e.getY());
                }
            }
        });
    }

    private static String createText(int stackCount, int queueCount) {
        StringBuilder builder = new StringBuilder();
        addRep(builder, stackCount, "S");
        addRep(builder, queueCount, "Q");
        return builder.toString();
    }

    private static void addRep(StringBuilder builder, int count,
            String structString) {
        if (count > 0) {
            if (builder.length() > 0) {
                builder.append("/");
            }
            if (count > 1) {
                builder.append(count);
            }
            builder.append(structString);
        }
    }

    public void set(boolean isReady, StackQueueSat sat) {
        this.sat = sat;
        setEnabled(isReady && sat != null);
        if (isReady) {
            setBackground(sat == null ? Color.RED : Color.GREEN);
        } else {
            setBackground(Color.GRAY);
        }
    }

    private void executeSatAction() {
        if (sat != null) {
            sat.apply();
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
