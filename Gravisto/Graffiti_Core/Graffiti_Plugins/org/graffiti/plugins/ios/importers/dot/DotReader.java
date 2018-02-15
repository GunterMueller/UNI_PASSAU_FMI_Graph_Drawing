// =============================================================================
//
//   GraphMLReader.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DotReader.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.importers.dot;

import java.io.IOException;
import java.io.InputStream;

import org.graffiti.graph.Graph;
import org.graffiti.plugin.io.AbstractInputSerializer;
import org.graffiti.plugin.io.InputSerializer;
import org.graffiti.plugins.ios.importers.dot.parser.DOTParser;
import org.graffiti.plugins.ios.importers.dot.parser.InterpreterVisitor;
import org.graffiti.plugins.ios.importers.dot.parser.ParseException;
import org.graffiti.plugins.ios.importers.dot.parser.SimpleNode;

/**
 * This class implements the interface to invoke the reading of dot files.
 * 
 * @author keilhaue
 */
public class DotReader extends AbstractInputSerializer implements
        InputSerializer {

    /** The parser for reading the dot input. */
    private DOTParser dotParser;

    /** The supported extension. */
    private String[] extensions = { ".dot" };

    /**
     * Constructs a new <code>DotReader</code>.
     */
    public DotReader() {
        super();
    }

    /*
     * 
     */
    public String[] getExtensions() {
        return this.extensions;
    }

    /*
     * 
     */
    @Override
    public void read(InputStream in, Graph g) throws IOException {
        this.dotParser = new DOTParser(in);
        try {
            SimpleNode root = this.dotParser.parse();
            InterpreterVisitor interpreter = new InterpreterVisitor();
            root.jjtAccept(interpreter, g);
        } catch (ParseException p) {
            throw new DotException(p);
        } finally {
            in.close();
        }

    }

    public String getName() {
        return "DOT Importer";
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
