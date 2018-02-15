// =============================================================================
//
//   FastViewOptionsMenu.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import org.graffiti.plugin.gui.GraffitiMenu;
import org.graffiti.plugins.scripting.ScriptingEngine;
import org.graffiti.plugins.scripting.ScriptingRegistry;
import org.graffiti.plugins.scripting.ScriptingRegistryListener;
import org.graffiti.plugins.views.fast.FastViewPlugin;
import org.graffiti.plugins.views.fast.GlobalFastViewOptions;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class FastViewOptionsMenu extends GraffitiMenu {
    /**
     * 
     */
    private static final long serialVersionUID = 6868821005083977135L;
    private JMenu scriptingLanguageMenu;
    private ButtonGroup scriptingLanguageButtonGroup;

    /*
     * Maps from language id to radio menu item.
     */
    private Map<String, JRadioButtonMenuItem> scriptingLanguageMap;

    public FastViewOptionsMenu() {
        String caption = FastViewPlugin.getString("menu.options");
        setText(caption);

        initScriptingLanguage();
    }

    @Override
    public String getPreferredComponent() {
        return "menu.options";
    }

    private void initScriptingLanguage() {
        scriptingLanguageMenu = new JMenu(FastViewPlugin
                .getString("menu.options.scriptingLanguage"));

        add(scriptingLanguageMenu);

        scriptingLanguageButtonGroup = new ButtonGroup();
        scriptingLanguageMap = new HashMap<String, JRadioButtonMenuItem>();

        ScriptingRegistry registry = ScriptingRegistry.get();
        for (Map.Entry<String, ScriptingEngine> entry : registry.getEngines()) {
            addScriptingLanguage(entry.getKey(), entry.getValue().getName());
        }

        add(scriptingLanguageMenu);

        registry.addListener(new ScriptingRegistryListener() {
            public void engineRegistered(String id, ScriptingEngine engine) {
                addScriptingLanguage(id, engine.getName());
            }
        });

        GlobalFastViewOptions.get().addListener(
                new GlobalFastViewOptions.Listener() {
                    @Override
                    public void consoleLanguageChanged(String consoleLanguageId) {
                        JRadioButtonMenuItem item = scriptingLanguageMap
                                .get(consoleLanguageId);

                        if (item == null) {
                            // Deactivate all items.
                            for (JRadioButtonMenuItem item2 : scriptingLanguageMap
                                    .values()) {
                                item2.setSelected(false);
                            }
                        } else {
                            item.setSelected(true);
                        }
                    }
                });
    }

    private void addScriptingLanguage(final String id, String name) {
        JRadioButtonMenuItem item = new JRadioButtonMenuItem(name,
                GlobalFastViewOptions.get().getDefaultConsoleLanguage().equals(
                        id));
        scriptingLanguageMenu.add(item);
        scriptingLanguageButtonGroup.add(item);
        scriptingLanguageMap.put(id, item);

        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GlobalFastViewOptions.get().setDefaultConsoleLanguage(id);
            }
        });
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
