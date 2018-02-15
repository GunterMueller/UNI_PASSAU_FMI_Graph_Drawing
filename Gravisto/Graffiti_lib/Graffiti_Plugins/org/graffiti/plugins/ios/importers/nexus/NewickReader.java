package org.graffiti.plugins.ios.importers.nexus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;

import org.graffiti.graph.Graph;
import org.graffiti.plugin.io.AbstractInputSerializer;
import org.graffiti.plugin.io.InputSerializer;

/**
 * This class implements the InputSerializer interface to parse and load files
 * in the Newick Tree Format.
 */
public class NewickReader extends AbstractInputSerializer implements
        NexusParsingConstants, InputSerializer {

    /** The extensions of files containing trees in the Newick format. */
    private final String[] EXTENSIONS = { ".tre" };

    /**
     * {@inheritDoc}
     */
    @Override
    public void read(InputStream in, Graph g) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        NexusTokenIterator tokenIterator = new NexusTokenIterator(br);

        boolean createTree = true;
        NewickTree tree = null;

        while (tokenIterator.hasNext()) {
            Token token = tokenIterator.next();

            if (token.getType() == TokenTypes.COMMAND_DELIMITER) {
                createTree = true;
            } else {
                if (createTree) {
                    tree = new NewickTree(g);
                    createTree = false;
                }

                try {
                    tree.parseToken(token);
                } catch (ParseException e) {
                    throw new IOException(TREE_PARSE_ERROR + " "
                            + e.getMessage());
                }
            }
        }

        in.close();
    }

    /**
     * Returns the extensions of files containing trees in the Newick format.
     * 
     * @return Array of Strings containing the extensions of Newick tree files.
     */
    public String[] getExtensions() {
        return EXTENSIONS;
    }

    public String getName() {
        return "Newick Importer";
    }
}
