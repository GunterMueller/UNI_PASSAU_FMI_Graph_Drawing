// =============================================================================
//
//   Console.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.scripting;

/**
 * Classes implementing {@code Console} provide a logical console for scripting.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public interface Console {
    /**
     * Adds the specified line to the console input. If the accumulated input
     * consists in a complete command, that command is executed and its result
     * is passed to the specified result callback.
     * 
     * @param line
     *            the line to add to the console input.
     * @param resultCallback
     *            recieves the result if a command is executed.
     * @return if the accumulated input consists in a complete command.
     */
    public boolean addLine(String line, ResultCallback resultCallback);

    /**
     * Clears the buffer accumulating the console input until it consists in a
     * full command.
     */
    public void reset();
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
