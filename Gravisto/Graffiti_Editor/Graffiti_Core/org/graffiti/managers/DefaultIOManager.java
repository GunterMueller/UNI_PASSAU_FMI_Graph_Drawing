// =============================================================================
//
//   DefaultIOManager.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DefaultIOManager.java 5779 2010-05-10 20:31:37Z gleissner $

package org.graffiti.managers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.graffiti.core.GenericFileFilter;
import org.graffiti.core.MultiFileFilter;
import org.graffiti.managers.pluginmgr.PluginDescription;
import org.graffiti.plugin.GenericPlugin;
import org.graffiti.plugin.io.InputSerializer;
import org.graffiti.plugin.io.OutputSerializer;
import org.graffiti.util.logging.GlobalLoggerSetting;

/**
 * Handles the editor's IO serializers.
 * 
 * @version $Revision: 5779 $
 */
public class DefaultIOManager implements IOManager {

    /** The logger for the current class. */
    private static final Logger logger = Logger
            .getLogger(DefaultIOManager.class.getName());
            
    static {
        logger.setLevel(GlobalLoggerSetting.LOGGER_LEVEL);
    }

    /** The description of the default file type */
    private static final String DEFAULT_FILE_DESCRIPTION = "*.graphml";

    /** The map from name of extension to its input serializer. */
    private HashMap<String, InputSerializer> inputSerializer;

    /** The map from name of extension to its output serializer. */
    private HashMap<String, OutputSerializer> outputSerializer;

    /** The file chooser used to open and save graphs. */
    private JFileChooser fc;

    /** The list of listeners. */
    private List<IOManagerListener> listeners;

    protected Preferences uiPrefs;

    /**
     * Constructs a new io manager.
     */
    public DefaultIOManager(Preferences prefs) {
        inputSerializer = new HashMap<String, InputSerializer>();
        outputSerializer = new HashMap<String, OutputSerializer>();
        this.listeners = new LinkedList<IOManagerListener>();
        uiPrefs = prefs;
    }

    /*
     * @see
     * org.graffiti.managers.IOManager#addInputSerializer(org.graffiti.plugin
     * .io.InputSerializer)
     */
    public void addInputSerializer(InputSerializer is) {
        for (String ext : is.getExtensions()) {
            inputSerializer.put(ext, is);
            logger.info(ext + " added in the input"
                    + " serializer map with the" + " associated class "
                    + is.getClass().getName());
        }

        fireInputSerializerAdded(is);
    }

    /*
     * @see
     * org.graffiti.managers.IOManager#addListener(org.graffiti.managers.IOManager
     * .IOManagerListener)
     */
    public void addListener(IOManagerListener ioManagerListener) {
        listeners.add(ioManagerListener);
    }

    /*
     * @see
     * org.graffiti.managers.IOManager#addOutputSerializer(org.graffiti.plugin
     * .io.OutputSerializer)
     */
    public void addOutputSerializer(OutputSerializer os) {
        for (String ext : os.getExtensions()) {
            outputSerializer.put(ext, os);
            logger.info(ext + " added in the output"
                    + " serializer map with the" + " associated class "
                    + os.getClass().getName());
        }

        fireOutputSerializerAdded(os);
    }

    /*
     * @see
     * org.graffiti.managers.IOManager#createInputSerializer(java.lang.String)
     */
    public InputSerializer getInputSerializer(String ext)
            throws IllegalAccessException, InstantiationException {
        return inputSerializer.get(ext);
    }

    private JFileChooser buildFileChooser() {
        String lastFolder = uiPrefs.get("lastFolder", "");
        return new JFileChooser(lastFolder);
    }

    public void storeSelectedFolder(String path) {
        uiPrefs.put("lastFolder", path);
    }

    /*
     * @see org.graffiti.managers.IOManager#createOpenFileChooser()
     */
    public JFileChooser createOpenFileChooser() {
        if (fc == null) {
            fc = buildFileChooser();
        }

        fc.resetChoosableFileFilters();

        MultiFileFilter multiFilter = new MultiFileFilter();
        for (String string : inputSerializer.keySet()) {
            multiFilter.addExtension(string);
        }
        multiFilter.setDescription("All Gravisto Files");
        fc.addChoosableFileFilter(multiFilter);
        for (String string : inputSerializer.keySet()) {
            GenericFileFilter singleFilter = new GenericFileFilter(string);
            fc.addChoosableFileFilter(singleFilter);
        }
        fc.setFileFilter(multiFilter);
        return fc;
    }

    /*
     * @see
     * org.graffiti.managers.IOManager#createOutputSerializer(java.lang.String)
     */
    public OutputSerializer getOutputSerializer(String ext)
            throws IllegalAccessException, InstantiationException {
        return outputSerializer.get(ext);
    }

    /*
     * @see org.graffiti.managers.IOManager#createSaveFileChooser()
     */
    public JFileChooser createSaveFileChooser() {
        if (fc == null) {
            fc = buildFileChooser();
        }

        for (FileFilter ff : fc.getChoosableFileFilters()) {
            fc.removeChoosableFileFilter(ff);
        }

        GenericFileFilter defaultFilter = null;

        for (String s : outputSerializer.keySet()) {
            GenericFileFilter filter = new GenericFileFilter(s);
            fc.addChoosableFileFilter(filter);

            if (filter.getDescription().equals(DEFAULT_FILE_DESCRIPTION)) {
                defaultFilter = filter;
            }
        }

        if (defaultFilter != null) {
            fc.setFileFilter(defaultFilter);
        }

        return fc;
    }

    /*
     * @see org.graffiti.managers.IOManager#hasInputSerializer()
     */
    public boolean hasInputSerializer() {
        return !inputSerializer.isEmpty();
    }

    /*
     * @see org.graffiti.managers.IOManager#hasOutputSerializer()
     */
    public boolean hasOutputSerializer() {
        return !outputSerializer.isEmpty();
    }

    /*
     * @see
     * org.graffiti.managers.pluginmgr.PluginManagerListener#pluginAdded(org
     * .graffiti.plugin.GenericPlugin,
     * org.graffiti.managers.pluginmgr.PluginDescription)
     */
    public void pluginAdded(GenericPlugin plugin, PluginDescription desc) {
        // register add input serializers
        for (InputSerializer is : plugin.getInputSerializers()) {
            addInputSerializer(is);
        }

        // register all output serializers
        for (OutputSerializer os : plugin.getOutputSerializers()) {
            addOutputSerializer(os);
        }
    }

    /*
     * @see
     * org.graffiti.managers.IOManager#removeListener(org.graffiti.managers.
     * IOManager.IOManagerListener)
     */
    public boolean removeListener(IOManagerListener l) {
        return listeners.remove(l);
    }

    /**
     * Returns a string representation of the io manager. Useful for debugging.
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "inputSerializer: " + inputSerializer + " outputSerializer: "
                + outputSerializer;
    }

    /**
     * Informs every registered io manager listener about the addition of the
     * given input serializer.
     * 
     * @param is
     *            the input serializer, which was added.
     */
    private void fireInputSerializerAdded(InputSerializer is) {
        for (IOManagerListener l : listeners) {
            l.inputSerializerAdded(is);
        }
    }

    /**
     * Informs every output serializer about the addition of the given output
     * serializer.
     * 
     * @param os
     *            the output serializer, which was added.
     */
    private void fireOutputSerializerAdded(OutputSerializer os) {
        for (IOManagerListener l : listeners) {
            l.outputSerializerAdded(os);
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
