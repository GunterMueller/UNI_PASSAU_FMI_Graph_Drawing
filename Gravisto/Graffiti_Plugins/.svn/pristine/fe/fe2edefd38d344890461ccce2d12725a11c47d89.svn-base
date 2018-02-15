package org.graffiti.plugins.scripting;

import java.awt.Color;

/**
 * The type of output to a console.
 * 
 * @author Andreas Glei&szlig;ner
 */
public enum ConsoleOutput {
    /**
     * Intended output of the program.
     */
    Standard(Color.BLACK),

    /**
     * Echo of user commands.
     */
    User(Color.BLUE),

    /**
     * Error message.
     */
    Error(Color.RED);

    /**
     * The default color for this kind of output.
     */
    private Color defaultColor;

    /**
     * Constructs a {@code ConsoleOutput}.
     * 
     * @param defaultColor
     *            the default color for the kind of output to construct.
     */
    private ConsoleOutput(Color defaultColor) {
        this.defaultColor = defaultColor;
    }

    /**
     * Returns the default color for this kind of output.
     * 
     * @return the default color for this kind of output.
     */
    public Color getDefaultColor() {
        return defaultColor;
    }
}
