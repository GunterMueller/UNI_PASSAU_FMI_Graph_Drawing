// =============================================================================
//
//   PluginManagerTest.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: PluginManagerTest.java 5773 2010-05-07 18:50:34Z gleissner $

package tests.graffiti.pluginmgr;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import junit.framework.TestCase;

import org.graffiti.managers.pluginmgr.DefaultPluginManager;
import org.graffiti.managers.pluginmgr.PluginManager;
import org.graffiti.managers.pluginmgr.PluginManagerException;

/**
 * Contains test cases for the <code>DefaultPluginManager</code> class.
 * 
 * @version $Revision: 5773 $
 */
public class PluginManagerTest extends TestCase {
    /** The test case's preferences. */
    Preferences prefs;

    /**
     * Constructs a new test case for the <code>DefaultPluginManager</code>
     * class.
     * 
     * @param name
     *            the name for the test case.
     */
    public PluginManagerTest(String name) {
        super(name);
    }

    /**
     * Main method for running all the test cases of this test class.
     * 
     * @param args
     *            DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(PluginManagerTest.class);
    }

    /**
     * Tests if an exception is thrown, if the <code>plugin.xml</code> file
     * could not be loaded.
     */
    public void testIOException() {
        PluginManager pm = new DefaultPluginManager(prefs);

        try {
            pm.createInstance(getClass().getResource("notExistingPlugin.xml"));
            fail("a PluginManagerException should have been thrown.");
        } catch (PluginManagerException pme) {
        }

        try {
            pm.createInstance(getClass().getResource("notExistingJarFile.jar"));
            fail("a PluginManagerException should have been thrown.");
        } catch (PluginManagerException pme) {
        }

        try {
            pm.createInstance(getClass().getResource("notExistingZipFile.zip"));
            fail("a PluginManagerException should have been thrown.");
        } catch (PluginManagerException pme) {
        }
    }

    /**
     * Tests the dtd validating mechanism.
     */
    public void testSAXException() {
        PluginManager pm = new DefaultPluginManager(prefs);

        try {
            pm.createInstance(getClass().getResource("plugin.xml"));
        } catch (PluginManagerException pme) {
            fail("this should not happen");
        }
    }

    /**
     * Tests if the plugin manager refuses to add a plugin twice.
     */
    public void testSamePluginTwice() {
        PluginManager pm = new DefaultPluginManager(prefs);

        try {
            pm.createInstance(getClass().getResource("plugin.xml"));
        } catch (PluginManagerException pme) {
            fail("this should not happen");
        }

        try { // to add the same plugin again

            pm.createInstance(getClass().getResource("plugin.xml"));
            fail("this should not happen");
        } catch (PluginManagerException pme) {
            // an exception is thrown, because this plugin is already in
            // the plugin manager's list of instanciated plugins.
        }
    }

    /**
     * Tests if the plugin manager refuses to add a plugin twice.
     */
    public void testSamePluginTwiceAndThenAnother() {
        PluginManager pm = new DefaultPluginManager(prefs);

        try {
            pm.createInstance(getClass().getResource("plugin.xml"));
        } catch (PluginManagerException pme) {
            fail("this should not happen");
        }

        try { // to add the same plugin again

            pm.createInstance(getClass().getResource("plugin.xml"));
            fail("this should not happen");
        } catch (PluginManagerException pme) {
            // an exception is thrown, because this plugin is already in
            // the plugin manager's list of instanciated plugins.
        }

        try { // to add the same plugin again

            pm.createInstance(getClass().getResource("plugin1.xml"));
        } catch (PluginManagerException pme) {
            fail("this should not happen");
        }
    }

    /**
     * Tests if the plugin manager refuses to add two plugins.
     */
    public void testTwoPlugins() {
        PluginManager pm = new DefaultPluginManager(prefs);

        try {
            pm.createInstance(getClass().getResource("plugin.xml"));
        } catch (PluginManagerException pme) {
            fail("this should not happen");
        }

        try { // to add the same plugin again

            pm.createInstance(getClass().getResource("plugin1.xml"));
        } catch (PluginManagerException pme) {
            fail("this should not happen");
        }
    }

    /**
     * Initializes a new graph for every test case.
     */
    @Override
    protected void setUp() {
        prefs = Preferences.userNodeForPackage(PluginManagerTest.class);

        try { // to remove the preferences for this test case.
            prefs.clear();
        } catch (BackingStoreException bse) {
        }
    }

    /**
     * Actions to be done at tear down.
     */
    @Override
    protected void tearDown() {
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
