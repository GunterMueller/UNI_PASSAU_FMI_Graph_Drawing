package org.graffiti.plugins.ios.importers.nexus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.graffiti.graph.Graph;
import org.graffiti.plugin.io.AbstractInputSerializer;
import org.graffiti.plugin.io.InputSerializer;

/**
 * This class implements the interface to invoke the reading of Nexus files.
 */
public class NexusReader extends AbstractInputSerializer implements
        InputSerializer, NexusParsingConstants {
    /** The supported extension. */
    private final String[] EXTENSIONS = { ".nex", ".nxs" };

    /** The token Iterator, which splits the incoming file. */
    private NexusTokenIterator tokenIterator;

    private NexusCommandIterator commandIterator;

    /** The graph in which the parsed tree is to be saved. */
    private Graph graph;

    /**
     * Returns the supported extensions.
     * 
     * @return The supported Nexus file extensions
     */
    public String[] getExtensions() {
        return EXTENSIONS;
    }

    @Override
    public void read(InputStream in, Graph g) throws IOException {
        this.graph = g;

        // initialize NexusReader
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        this.tokenIterator = new NexusTokenIterator(br);
        this.commandIterator = new NexusCommandIterator(tokenIterator);
        this.commandIterator.enableSpecialTreeBlockParsing(true);

        this.parse();

        in.close();
    }

    private void parse() throws IOException {
        boolean firstBlock = true;

        while (tokenIterator.hasNext()) {
            List<Token> command = commandIterator.getNextCommand();

            if (firstBlock && !command.isEmpty()) {
                Token first = command.get(0);

                if (first.compareToLabel(NEXUS_FILE_DESCRIPTOR)) {
                    command.remove(0);
                }
                firstBlock = false;
            }

            if (command.size() != 2
                    || !command.get(0).compareToLabel(BLOCK_BEGIN))
                throw new IOException(FILE_PARSE_ERROR);

            parseBlock(command.get(1));

        }

    }

    /**
     * Throws an IOException, if a given Token is not of a given TokenType.
     * 
     * @param t
     *            the Token that is to be tested
     * @param type
     *            the Token type
     */
    private void assertTokenType(Token t, TokenTypes type) throws IOException {
        if (t == null || t.getType() != type)
            throw new IOException(FILE_PARSE_ERROR);
    }

    private void parseTreeBlock() throws IOException {
        Map<String, String> translationMap = null;

        boolean endOfBlockReached = false;

        // command loop
        while (!endOfBlockReached) {
            List<Token> command = commandIterator.getNextCommand();

            if (command.size() >= 1) {
                Token commandName = command.get(0);

                if (command.size() > 2
                        && commandName.compareToLabel(COMMAND_TREE)) {
                    command.remove(0);

                    assertTokenType(command.get(0), TokenTypes.LABEL);
                    command.remove(0);

                    assertTokenType(command.get(0), TokenTypes.ASSIGNMENT);
                    command.remove(0);
                    NewickTree treeParser = new NewickTree(graph,
                            translationMap);

                    for (Token t : command) {
                        try {
                            treeParser.parseToken(t);
                        } catch (ParseException e) {
                            throw new IOException(
                                    "Input file cannot be parsed: "
                                            + e.getMessage());
                        }
                    }
                } else if (commandName.compareToLabel(COMMAND_TRANSLATION)) {
                    command.remove(0);
                    translationMap = new TreeMap<String, String>();

                    Token source = null;
                    Token target = null;

                    Iterator<Token> it = command.iterator();
                    while (it.hasNext()) {
                        if (source == null) {
                            source = it.next();
                        } else if (target == null) {
                            target = it.next();
                        } else {
                            Token delimiter = it.next();
                            assertTokenType(delimiter,
                                    TokenTypes.NEW_NODE_START);

                            addLabelTranslation(translationMap, source, target);

                            source = null;
                            target = null;
                        }

                    }

                    if (source != null && target != null) {
                        addLabelTranslation(translationMap, source, target);
                    }
                }
                if (command.size() == 1
                        && commandName.compareToLabel(BLOCK_END)) {
                    endOfBlockReached = true;
                }
            }
        }
    }

    private void addLabelTranslation(Map<String, String> translationMap,
            Token source, Token target) throws IOException {
        assertTokenType(source, TokenTypes.LABEL);
        assertTokenType(target, TokenTypes.LABEL);

        String sourceLabel = source.getValue();
        String targetLabel = target.getValue();

        translationMap.put(sourceLabel, targetLabel);
    }

    /**
     * Basically ignores the block.
     */
    private void parseUnknownBlock() throws IOException {
        boolean endOfBlockReached = false;
        while (!endOfBlockReached) {
            List<Token> command = commandIterator.getNextCommand();

            if (command.size() == 1 && command.get(0).compareToLabel(BLOCK_END)) {
                endOfBlockReached = true;
            }
        }
    }

    private void parseBlock(Token blockCommand) throws IOException {
        assertTokenType(blockCommand, TokenTypes.LABEL);

        if (blockCommand.compareToLabel(BLOCK_NAME_TREES)) {
            parseTreeBlock();
        } else {
            parseUnknownBlock();
        }
    }

    public String getName() {
        return "Nexus Importer";
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
