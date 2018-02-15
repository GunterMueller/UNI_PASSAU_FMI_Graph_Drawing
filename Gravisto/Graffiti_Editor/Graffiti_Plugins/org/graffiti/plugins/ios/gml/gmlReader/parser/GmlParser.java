// =============================================================================
//
//   GmlParser.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GmlParser.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.gml.gmlReader.parser;

import java.io.FileReader;
import java.io.InputStream;

import org.graffiti.plugins.ios.gml.gmlReader.gml.Gml;

/**
 * This class provides a public interface for accessing and running the parser.
 * 
 * @author ruediger
 */
public class GmlParser {

    /**
     * Constructs a new parser.
     */
    public GmlParser() {
    }

    /**
     * Runs the parser on a list of files specified by their file name.
     * 
     * @param files
     *            the filenames which to parse.
     * 
     * @return the read in Gml graphs.
     * 
     * @throws Exception
     *             what? how to handle?
     */
    public Gml[] parse(String[] files) throws Exception {
        Gml[] g = new Gml[files.length];

        // run the parser for each of the given sorce files
        for (int i = 0; i < files.length; ++i) {
            System.out.println("reading file " + files[i] + " ...");

            parser p = new parser(new Yylex(new FileReader(files[i])));

            // parser p = new Yylex(new FileReader(files[i]));
            // parser p = new parser(new FileInputStream(files[i]));
            // parser p = new parser();
            p.parse();
            g[i] = p.getGraph();
            assert g[i] != null;
        }

        return g;
    }

    /**
     * Runs the parser on the specified <code>InputStream</code> and returns the
     * read in GML declarations.
     * 
     * @param is
     *            the <code>InputStream</code> from which to parse.
     * 
     * @return the read in Gml graph.
     * 
     * @throws Exception
     *             any exception that occurred during parsing.
     */
    public Gml parse(InputStream is) throws Exception {
        parser p = new parser(new Yylex(is));

        // parser p = new parser(is);
        p.parse();

        return p.getGraph();
    }

    /**
     * Runs the parser on a file specified by its name.
     * 
     * @param filename
     *            the name of the file to be parsed.
     * 
     * @return the read in <code>Gml</code> object.
     * 
     * @throws Exception
     *             what? how to handle?
     */
    public Gml parse(String filename) throws Exception {
        String[] s = new String[1];
        s[0] = filename;

        Gml[] g = parse(s);
        assert g.length == 1;

        return g[0];
    }

    /**
     * Parse from stdin.
     * 
     * @return the read in Gml graph.
     * 
     * @throws Exception
     *             any exception that occurred during parsing.
     */
    public Gml parse() throws Exception {
        // run the parser from stdin
        System.out.println("reading from stdin...");

        parser p = new parser(new Yylex(System.in));

        // parser p = new parser(System.in);
        p.parse();

        return p.getGraph();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
