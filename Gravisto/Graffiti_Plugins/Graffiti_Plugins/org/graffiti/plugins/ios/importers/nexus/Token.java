package org.graffiti.plugins.ios.importers.nexus;

/**
 * A Token represents the smallest unit of a Nexus File. It consists of a
 * TokenType and an optional value.
 * 
 * @see TokenTypes
 */
public class Token {

    /** The type of this Token. */
    private TokenTypes type;

    /** The characters of this Token. */
    private StringBuilder value;

    /**
     * Constructor.
     * 
     * @param type
     *            The TokenType of this object.
     */
    public Token(TokenTypes type) {
        this.type = type;
    }

    /**
     * Adds a single char to the end of the value.
     * 
     * @param c
     *            The character to be added.
     */
    public void addChar(char c) {
        if (this.value == null) {
            this.value = new StringBuilder();
        }
        this.value.append(c);

    }

    /**
     * Returns the TokenType of this Token.
     * 
     * @return The TokenType of this Token.
     */
    public TokenTypes getType() {
        return type;
    }

    /**
     * Returns the previously set value of this Token. The value can be set by
     * adding chars in the method <code>addChar</code>.
     * 
     * @return The value of this Token. If no value has been set, an empty
     *         String is returned.
     */
    public String getValue() {
        if (this.value != null)
            return value.toString();
        else
            return "";
    }

    /**
     * Compares this Token with a String. Returns true, if this Token is a Label
     * and is equal to the String using case insensitive comparison.
     * 
     * @param label
     *            The String with which the Label of the Token is to be
     *            compared.
     * @return true, if this Token is a Label and is equal to the String using
     *         case insensitive comparison.
     */
    public boolean compareToLabel(String label) {
        if (getType() != TokenTypes.LABEL || value == null)
            return false;
        return (getValue().equalsIgnoreCase(label));
    }
}
