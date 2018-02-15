// =============================================================================
//
//   GraffitiEditor.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraffitiEditor.java 5779 2010-05-10 20:31:37Z gleissner $

package org.graffiti.editor;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.graffiti.attributes.AttributeTypesManager;
import org.graffiti.core.Bundle;
import org.graffiti.managers.pluginmgr.DefaultPluginManager;
import org.graffiti.managers.pluginmgr.PluginDescription;
import org.graffiti.managers.pluginmgr.PluginManager;
import org.graffiti.managers.pluginmgr.PluginXMLParser;
import org.graffiti.util.logging.GlobalLoggerSetting;

/**
 * Contains the graffiti editor.
 * 
 * @version $Revision: 5779 $
 */
public class GraffitiEditor {
    /** The logger for the current class. */
    private static final Logger logger = Logger.getLogger(MainFrame.class
            .getName());
    
    static {
        logger.setLevel(GlobalLoggerSetting.LOGGER_LEVEL);
    }

    /** The preferences of the editor. */
    private Preferences prefs;

    /** The editor's attribute types manager. */
    private AttributeTypesManager attributeTypesManager;

    /** The editor's main frame. */
    private MainFrame mainFrame;

    /** The editor's plugin manager. */
    private PluginManager pluginManager;

    /**
     * Constructs a new instance of the editor.
     */
    public GraffitiEditor() {
        prefs = Preferences.userNodeForPackage(GraffitiEditor.class);

        try {
            UIManager.setLookAndFeel(LookAndFeel.valueOf(
                    prefs.node("ui").get("lookAndFeel",
                            LookAndFeel.SUN_CROSS_PLATFORM.toString()))
                    .getClassName());
        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception while setting look & feel", e);
        }

        // create splash screen.
        SplashScreen splashScreen = new SplashScreen();
        splashScreen.setVisible(true);

        // create an instance of the plugin manager.
        pluginManager = new DefaultPluginManager(prefs.node("pluginmgr"));

        // create an instance of the attribute types manager ...
        attributeTypesManager = new AttributeTypesManager();

        // ... and register this instance at the plugin manager
        pluginManager.addPluginManagerListener(attributeTypesManager);

        // construct and open the editor's main frame
        mainFrame = new MainFrame(pluginManager, prefs.node("ui"));

        try {
            // load configured plugins
            pluginManager.loadStartupPlugins(splashScreen);

            // if no plugins are configured, load the default plugins
            if (pluginManager.getPluginEntries().size() == 0) {
                JOptionPane
                        .showMessageDialog(
                                null,
                                "The default plugins will be loaded."
                                        + " You can add additional\nplugins using the menu \"Plugin\""
                                        + " -> \"Open plugin manager...\".",
                                "Welcome to Gravisto!",
                                JOptionPane.INFORMATION_MESSAGE);
                URL location = getClass().getResource("plugin.xml");

                PluginXMLParser parser = new PluginXMLParser();
                PluginDescription desc = parser.parse(location.openStream());

                pluginManager.loadPlugin(desc, location, Boolean.TRUE);
            }

            splashScreen.dispose();
        } catch (Exception pme) {
            pme.printStackTrace();
            splashScreen.dispose();
            showMessageDialog(pme.getMessage());
        }

        // add an empty editor session.
        // mainFrame.addSession(new EditorSession());
        mainFrame.setVisible(true);
    }

    /**
     * Disables DirectDraw if FastView OpenGL engine requires that.
     */
    private static void possiblyDisableDirectDraw() {
        try {
            Preferences prefs = Preferences.userRoot();
            if (prefs.nodeExists("org/graffiti/editor/pluginmgr/pluginPrefs/"
                    + "org/graffiti/plugins/views/fast/opengl/OpenGLPlugin")) {
                prefs = prefs
                        .node("org/graffiti/editor/pluginmgr/pluginPrefs/"
                                + "org/graffiti/plugins/views/fast/opengl/OpenGLPlugin");
                boolean noddraw = prefs.getBoolean("noddraw", false);
                if (noddraw) {
                    System.setProperty("sun.java2d.noddraw", "true");
                }
            }
        } catch (BackingStoreException e1) {
        }
    }

    /**
     * The editor's main method.
     * 
     * @param args
     *            the command line arguments.
     */
    public static void main(String[] args) {
        // Andreas Glei�ner: Using the FastView OpenGLEngine on Windows may
        // cause severe problems due to bugs in ATI drivers if DirectDraw is
        // enabled.
        possiblyDisableDirectDraw();

        // reading the logging config file
        try {
            LogManager.getLogManager().readConfiguration(
                    GraffitiEditor.class
                            .getResourceAsStream("Logging.properties"));
        } catch (IOException e) {
            logger.info("Start without specified logging properties");
        }

        GraffitiEditor e = new GraffitiEditor();

        // parse the command line arguments.
        e.parseCommandLineArguments(args);
    }

    /**
     * Parses the command line arguments passed to this class.
     * 
     * @param args
     *            the command line arguments passed to this class.
     */
    private void parseCommandLineArguments(String[] args) {
        for (String arg : args) {
            mainFrame.loadGraph(new File(arg));
        }
    }

    /**
     * Shows an arbitrary message dialog.
     * 
     * @param msg
     *            the message to be shown.
     */
    private void showMessageDialog(String msg) {
        JOptionPane
                .showMessageDialog(mainFrame, msg, Bundle.getCoreBundle()
                        .getString("message.dialog.title"),
                        JOptionPane.WARNING_MESSAGE);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
