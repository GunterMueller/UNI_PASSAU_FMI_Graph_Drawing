package org.graffiti.plugins.ios.importers.nexus;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * The NexusCommandIterator provides functionality to combine the Tokens of a
 * NexusTokenIterator into a List of Tokens.
 */
public class NexusCommandIterator implements NexusParsingConstants {
    /** The {@link NexusTokenIterator} of this iterator. */
    private NexusTokenIterator tokenIterator;

    /** Indicates whether newlines are to be ignored. */
    private boolean ignoreNewlines = false;

    /**
     * Creates a new command iterator.
     * 
     * @param tokenIterator
     *            The TokenIterator, this command iterator is to use.
     */
    public NexusCommandIterator(NexusTokenIterator tokenIterator) {
        assert tokenIterator != null;

        this.tokenIterator = tokenIterator;
    }

    /**
     * Gets the next known command from tokenIterator. Known command names are
     * defined in the constant LEGAL_COMMAND_NAMES.
     * 
     * @return list of tokens as parsed in the command. Excluding the
     *         COMMAND_DELIMITER.
     */
    public List<Token> getNextCommand() throws IOException {
        List<Token> command = new LinkedList<Token>();
        boolean commandComplete = false;

        while (!commandComplete && tokenIterator.hasNext()) {
            Token current = tokenIterator.next();

            if (current.getType() == TokenTypes.COMMAND_DELIMITER) {
                commandComplete = true;
            } else {
                // special tree treatment: ignore newlines
                if (ignoreNewlines && command.isEmpty()
                        && current.compareToLabel(COMMAND_TREE)) {
                    tokenIterator.setIgnoreNewLine(true);
                }

                command.add(current);
            }
        }

        // parsing incomplete
        if (!commandComplete)
            throw new IOException(FILE_PARSE_ERROR);

        // special tree treatment: make sure newlines are no longer ignored
        tokenIterator.setIgnoreNewLine(false);

        return command;
    }

    /**
     * Enables the special treatment of tree blocks. If enabled newlines are
     * being ignored.
     * 
     * @param enable
     *            Set to <code>true</code>, if newlines should be ignored,
     *            <code>false</code> otherwise.
     */
    public void enableSpecialTreeBlockParsing(boolean enable) {
        ignoreNewlines = enable;
    }
}
