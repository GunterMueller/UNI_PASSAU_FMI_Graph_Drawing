// =============================================================================
//
//   MaximizeManager.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: MaximizeManager.java 5779 2010-05-10 20:31:37Z gleissner $

package org.graffiti.util;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.beans.PropertyVetoException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import org.graffiti.core.Bundle;
import org.graffiti.util.logging.GlobalLoggerSetting;

/**
 * Manages a desktop containing {@link org.graffiti.util.MaximizeFrame}s or
 * {@link javax.swing.JInternalFrame} with a
 * {@link org.graffiti.util.MaximizeLayout}. If the selected internal frame is
 * maximized, its toolbar buttons are displayed in an associated
 * {@link javax.swing.JMenuBar}, because the original buttons are hidden by
 * MaximizeLayout. To use this class simply instantiate an object with the
 * corresponding desktop and menuBar and use MaximizeFrame instead of
 * JInternalFrame. Use the {@link MaximizeManager#dispose()} method to stop
 * displaying the buttons.
 * 
 * @author Michael Forster
 * @version $Revision: 5779 $ $Date: 2009-10-23 13:07:21 +0200 (Fr, 23 Okt 2009)
 *          $
 * 
 * @see org.graffiti.util.MaximizeFrame
 * @see org.graffiti.util.MaximizeLayout
 */
public class MaximizeManager extends InternalFrameAdapter implements
        ActionListener, ComponentListener, ContainerListener {

    /** The logger for the current class. */
    private static final Logger logger = Logger.getLogger(MaximizeManager.class
            .getName());
    
    static {
        logger.setLevel(GlobalLoggerSetting.LOGGER_LEVEL);
    }

    /** Empty space in the menuBar to make the buttons aligned to the right */
    private Component menuBarGlue;

    /** The menuBar button to close maximized internal frames */
    private JButton closeButton;

    /** The menuBar button to iconify maximized internal frames */
    private JButton iconifyButton;

    /** The menuBar button to restore maximized internal frames */
    private JButton restoreButton;

    /** The associated desktop */
    private JDesktopPane desktop;

    /** The associated menu bar */
    private JMenuBar menuBar;

    /**
     * Flag that indicates whether the menu bar buttons have already been
     * created
     */
    private boolean menuBarInitialized = false;

    /**
     * Creates a new MaximizeManager object and associates it to a desktop and a
     * menu bar.
     * 
     * @param desktop
     *            The associated desktop
     * @param menuBar
     *            The associated menuBar
     */
    public MaximizeManager(JDesktopPane desktop, JMenuBar menuBar) {
        this.desktop = desktop;
        this.menuBar = menuBar;

        desktop.addContainerListener(this);
    }

    /*
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent event) {
        try {
            JInternalFrame selectedFrame = desktop.getSelectedFrame();

            if (selectedFrame != null) {
                if (event.getSource() == restoreButton) {
                    selectedFrame.setMaximum(false);
                } else if (event.getSource() == iconifyButton) {
                    selectedFrame.setIcon(true);
                } else if (event.getSource() == closeButton) {
                    selectedFrame.doDefaultCloseAction();
                }
            }

            updateButtons();
        } catch (PropertyVetoException e) {
            // ignore: this should not happen, and if it does, we cannot do
            // anything about it.
            logger
                    .log(
                            Level.WARNING,
                            "unexpected exception while pressing internal frame button",
                            e);
        }
    }

    /*
     * @see
     * java.awt.event.ContainerListener#componentAdded(java.awt.event.ContainerEvent
     * )
     */
    public void componentAdded(ContainerEvent e) {
        if (!menuBarInitialized) {
            initMenuBar();
        }

        Component child = e.getChild();

        if (child instanceof JInternalFrame) {
            JInternalFrame frame = (JInternalFrame) child;
            frame.addInternalFrameListener(this);
            frame.addComponentListener(this);
        }
    }

    /*
     * @seejava.awt.event.ComponentListener#componentHidden(java.awt.event.
     * ComponentEvent)
     */
    public void componentHidden(ComponentEvent e) {
    }

    /*
     * @see
     * java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent
     * )
     */
    public void componentMoved(ComponentEvent e) {
    }

    /*
     * @seejava.awt.event.ContainerListener#componentRemoved(java.awt.event.
     * ContainerEvent)
     */
    public void componentRemoved(ContainerEvent e) {
        Component child = e.getChild();

        if (child instanceof JInternalFrame) {
            JInternalFrame frame = (JInternalFrame) child;
            frame.removeInternalFrameListener(this);
            frame.removeComponentListener(this);
        }

        updateButtons();
    }

    /*
     * @seejava.awt.event.ComponentListener#componentResized(java.awt.event.
     * ComponentEvent)
     */
    public void componentResized(ComponentEvent e) {
        updateButtons();
    }

    /*
     * @see
     * java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent
     * )
     */
    public void componentShown(ComponentEvent e) {
    }

    /**
     * Remove the buttons from the menu bar, remove all listeners and therefore
     * make this object eligible for garbage collection.
     */
    public void dispose() {
        if (menuBarGlue != null) {
            menuBar.remove(menuBarGlue);
            menuBarGlue = null;
        }

        if (iconifyButton != null) {
            menuBar.remove(iconifyButton);
            iconifyButton.removeActionListener(this);
            iconifyButton = null;
        }

        if (restoreButton != null) {
            menuBar.remove(restoreButton);
            restoreButton.removeActionListener(this);
            restoreButton = null;
        }

        if (closeButton != null) {
            menuBar.remove(closeButton);
            closeButton.removeActionListener(this);
            closeButton = null;
        }

        desktop.removeContainerListener(this);
    }

    /*
     * @see
     * javax.swing.event.InternalFrameListener#internalFrameActivated(javax.
     * swing.event.InternalFrameEvent)
     */
    @Override
    public void internalFrameActivated(InternalFrameEvent e) {
        updateButtons();
    }

    /*
     * @see
     * javax.swing.event.InternalFrameListener#internalFrameDeiconified(javax
     * .swing.event.InternalFrameEvent)
     */
    @Override
    public void internalFrameDeiconified(InternalFrameEvent e) {
        updateButtons();
    }

    /**
     * Add the buttons to the associated menu bar.
     */
    private void initMenuBar() {
        if (menuBar != null) {
            menuBarGlue = menuBar.add(Box.createHorizontalGlue());

            Bundle bundle = Bundle.getCoreBundle();

            iconifyButton = new JButton(bundle.getIcon("internalFrame.iconify"));
            iconifyButton.setBorder(null);
            iconifyButton.addActionListener(this);
            menuBar.add(iconifyButton);

            restoreButton = new JButton(bundle.getIcon("internalFrame.restore"));
            restoreButton.setBorder(null);
            restoreButton.addActionListener(this);
            menuBar.add(restoreButton);

            closeButton = new JButton(bundle.getIcon("internalFrame.close"));
            closeButton.setBorder(null);
            closeButton.addActionListener(this);
            menuBar.add(closeButton);

            updateButtons();
        }

        menuBarInitialized = true;
    }

    /**
     * Hide or restore the menu bar buttons depending on the currently selected
     * frame.
     */
    private void updateButtons() {
        JInternalFrame frame = desktop.getSelectedFrame();

        boolean maximized = (frame != null) && frame.isMaximum();

        iconifyButton.setVisible(maximized && frame.isIconifiable());
        restoreButton.setVisible(maximized);
        closeButton.setVisible(maximized && frame.isClosable());
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
