// =============================================================================
//
//   IOManager.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: IOManager.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.managers;

import javax.swing.JFileChooser;

import org.graffiti.managers.pluginmgr.PluginManagerListener;
import org.graffiti.plugin.io.InputSerializer;
import org.graffiti.plugin.io.OutputSerializer;

/**
 * Handles the editor's IO serializers.
 * 
 * @version $Revision: 5767 $
 */
public interface IOManager extends PluginManagerListener {

    /**
     * Registers the given input serializer.
     * 
     * @param i
     *            the new serializer to add.
     */
    void addInputSerializer(InputSerializer i);

    /**
     * Adds the given <code>IOManagerListener</code> to the list of io manager
     * listeners.
     * 
     * @param ioManagerListener
     *            the listener to add.
     */
    void addListener(IOManagerListener ioManagerListener);

    /**
     * Registers the given output serializer.
     * 
     * @param o
     *            the new serializer to add.
     */
    void addOutputSerializer(OutputSerializer o);

    /**
     * Creates an instance of an input serializer from the given extension.
     * 
     * @return DOCUMENT ME!
     */
    InputSerializer getInputSerializer(String ext)
            throws IllegalAccessException, InstantiationException;

    /**
     * Modifies the given file chooser by registering file extensions from the
     * input serializers.
     * 
     * @return DOCUMENT ME!
     */
    JFileChooser createOpenFileChooser();

    /**
     * Creates an instance of an output serializer from the given extension.
     * 
     * @return DOCUMENT ME!
     */
    OutputSerializer getOutputSerializer(String ext)
            throws IllegalAccessException, InstantiationException;

    /**
     * Creates and returns a file open chooser dialog with the registered file
     * extensions from the output serializers.
     * 
     * @return DOCUMENT ME!
     */
    JFileChooser createSaveFileChooser();

    /**
     * Returns <code>true</code>, if the io manager has a registered input
     * manager.
     * 
     * @return <code>true</code>, if the io manager has a registered input
     *         manager.
     */
    boolean hasInputSerializer();

    /**
     * Returns <code>true</code>, if the io manager has a registered output
     * manager.
     * 
     * @return <code>true</code>, if the io manager has a registered output
     *         manager.
     */
    boolean hasOutputSerializer();

    /**
     * Returns <code>true</code>, if the given io manager listener was in the
     * list of listeners and could be removed.
     * 
     * @param l
     *            the io manager listener to remove.
     * 
     * @return DOCUMENT ME!
     */
    boolean removeListener(IOManagerListener l);

    /**
     * Interfaces an io manager listener.
     * 
     * @version $Revision: 5767 $
     */
    public interface IOManagerListener {
        /**
         * Called, if an input serializer is added to the io manager.
         * 
         * @param is
         *            the input serializer, which was added to the manager.
         */
        void inputSerializerAdded(InputSerializer is);

        /**
         * Called, if an output serializer ist added to the io manager.
         * 
         * @param os
         *            the output serializer, which was added to the manager.
         */
        void outputSerializerAdded(OutputSerializer os);
    }

    public void storeSelectedFolder(String path);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
