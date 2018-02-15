//=============================================================================
//
// YagiMenu.java
//
// Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//=============================================================================
//$Id: YagiMenu.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.inspectors.yagi;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JCheckBoxMenuItem;

import org.graffiti.plugin.gui.GraffitiMenu;

/**
 * Provides a menu "Inspector" where the user can switch between basic and
 * expert mode.
 */
public class YagiMenu extends GraffitiMenu implements ItemListener {

    /**
     * 
     */
    private static final long serialVersionUID = -8958495893465386364L;

    Preferences prefs = Preferences.userNodeForPackage(getClass());

    private final String EXPERT_MODE = "expertMode";

    /**
     * Constructs a new YagiMenu with a menu item that lets the user switch
     * between basic or expert mode.
     * 
     * @param itemListener
     *            the listener that reacts on changes (selection or deselection)
     *            of the checkBoxMenuItem
     */
    public YagiMenu(ItemListener itemListener) {
        super();
        this.setName("Inspector");
        this.setText("Inspector");
        this.setEnabled(true);
        this.setMnemonic(KeyEvent.VK_I);
        JCheckBoxMenuItem item = new JCheckBoxMenuItem("Expert mode");
        item.setMnemonic(KeyEvent.VK_E);
        item.addItemListener(itemListener);
        item.addItemListener(this);
        this.add(item);
        item.setSelected(prefs.getBoolean(EXPERT_MODE, true));
    }

    /**
     * @see org.graffiti.plugin.gui.GraffitiComponent#getPreferredComponent()
     */
    @Override
    public String getPreferredComponent() {
        return "menu.options";
    }

    public void itemStateChanged(ItemEvent e) {
        int state = e.getStateChange();
        prefs.putBoolean(EXPERT_MODE, state == ItemEvent.SELECTED);
        try {
            prefs.flush();
        } catch (BackingStoreException ex) {
            // do nothing
        }
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
