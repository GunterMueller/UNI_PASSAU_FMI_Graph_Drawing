package org.graffiti.plugins.ios.importers.nexus;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

/**
 * TODO
 */
public class NexusTokenIterator implements Iterator<Token> {

    /** The assignment character */
    private static final char ASSIGNMENT = '=';

    /** The character signaling the end of an command. */
    private static final char COMMAND_END = ';';

    /** The character that starts a comment. */
    private static final char COMMENT_START = '[';

    /** The character that ends a comment. */
    private static final char COMMENT_END = ']';

    /** The character that denotes the beginning and the end of a label. */
    private static final char LABEL_QUOTES = '\'';

    /** The character that denotes the beginning of a Node in a Newick tree. */
    private static final char NODE_START = '(';

    /** The character that denotes the end of a Node in a Newick tree. */
    private static final char NODE_END = ')';

    /** The character that separates one Node from another in a Newick tree. */
    private static final char NODE_DELIMITER = ',';

    /**
     * The character that denotes that an attribute is following in a Newick
     * tree.
     */
    private static final char ATTRIBUTE_DELIMITER = ':';

    /** TODO */
    private final int NEXT_CHAR_UNSET = -2;

    /** TODO */
    private char[] sortedBreakingChars = { ASSIGNMENT, ATTRIBUTE_DELIMITER,
            COMMAND_END, COMMENT_START, COMMENT_END, NODE_DELIMITER, NODE_END,
            NODE_START, ' ', '\t' };

    /** TODO */
    private boolean ignoreNewLine = false;

    /**
     * Token cache. This is the Token returned by next().
     */
    private Token nextToken = null;

    /**
     * The BufferedReader that contains the
     */
    private BufferedReader inputStream;

    /**
     * The next character that is not yet parsed.
     */
    private int nextChar = NEXT_CHAR_UNSET;

    public NexusTokenIterator(BufferedReader bufferedReader) {
        this.inputStream = bufferedReader;

        Arrays.sort(sortedBreakingChars);
    }

    public void setIgnoreNewLine(boolean ignoreNewLine) {
        this.ignoreNewLine = ignoreNewLine;
    }

    public boolean hasNext() {
        if (this.nextToken == null) {
            this.nextToken = this.next();
        }

        return (this.nextToken != null);
    }

    public Token next() {
        // return previously cached Token
        if (this.nextToken != null) {
            Token token = this.nextToken;
            this.nextToken = null;
            return token;
        }

        // read first character of file
        if (nextChar == NEXT_CHAR_UNSET) {
            try {
                nextChar = inputStream.read();
            } catch (IOException e) {
                return null;
            }
        }

        ParseState state = ParseState.STANDARD;
        // Token this.nextToken = null;

        while (state != ParseState.END) {
            // breaking condition: end of file reached
            if (nextChar == -1) {
                break;
            }

            char current = (char) nextChar;

            if (state == ParseState.STANDARD) {
                if (this.nextToken != null && isBreakingChar(current)) {
                    break;
                }

                state = parseStandardState(current);
            } else if (state == ParseState.COMMENT) {
                state = parseCommentState(current);
            } else if (state == ParseState.LABEL) {
                state = parseLabelState(current);
            }

            // read next character
            try {
                this.nextChar = inputStream.read();
            } catch (IOException e) {
                return null;
            }
        }

        return this.nextToken;
    }

    /**
     * This method is not implemented.
     * 
     * @throws UnsupportedOperationException
     *             Is thrown, if this method is called.
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }

    private ParseState parseStandardState(char current) {
        ParseState newState = ParseState.STANDARD;

        if (current == COMMENT_START) {
            newState = ParseState.COMMENT;
        } else if (current == COMMAND_END) {
            this.nextToken = new Token(TokenTypes.COMMAND_DELIMITER);
            newState = ParseState.END;
        } else if (current == ASSIGNMENT) {
            this.nextToken = new Token(TokenTypes.ASSIGNMENT);
            newState = ParseState.END;
        } else if (current == NODE_START) {
            this.nextToken = new Token(TokenTypes.SUBTREE_START);
            newState = ParseState.END;
        } else if (current == NODE_END) {
            this.nextToken = new Token(TokenTypes.SUBTREE_END);
            newState = ParseState.END;
        } else if (current == NODE_DELIMITER) {
            this.nextToken = new Token(TokenTypes.NEW_NODE_START);
            newState = ParseState.END;
        } else if (current == ATTRIBUTE_DELIMITER) {
            this.nextToken = new Token(TokenTypes.ATTRIBUTE_DELIMITER);
            newState = ParseState.END;
        } else if (current == LABEL_QUOTES) {
            this.nextToken = new Token(TokenTypes.LABEL);
            newState = ParseState.LABEL;
        } else if (current == '\n' || current == '\r') {
            if (!ignoreNewLine && this.nextToken != null) {
                newState = ParseState.END;
            }
        } else if (current != ' ' && current != '\t') {
            // Parse Label
            if (this.nextToken == null) {
                this.nextToken = new Token(TokenTypes.LABEL);
            }
            this.nextToken.addChar(current);
        }
        return newState;
    }

    private ParseState parseCommentState(char current) {
        ParseState newState = ParseState.COMMENT;

        // ignore comment
        if (current == COMMENT_END) {
            newState = ParseState.STANDARD;
        }
        return newState;
    }

    private ParseState parseLabelState(char current) {
        ParseState newState = ParseState.LABEL;

        if (current == LABEL_QUOTES) {
            // parseMoreChars
            newState = ParseState.END;
        } else if (current == '\r' || current == '\n') {
            if (!ignoreNewLine) {
                // TODO?
            }
        } else {
            this.nextToken.addChar(current);
        }
        return newState;
    }

    private boolean isBreakingChar(char c) {
        int index = Arrays.binarySearch(sortedBreakingChars, c);
        return (index >= 0);
    }

}
