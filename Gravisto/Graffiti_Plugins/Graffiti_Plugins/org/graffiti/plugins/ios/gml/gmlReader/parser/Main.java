// =============================================================================
//
//   Main.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: Main.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.gml.gmlReader.parser;

import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * The main class for running the parser.
 */
class Main {

    /**
     * Runs the parser.
     * 
     * @param args
     *            the filenames which to parse.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            // run the parser from stdin
            System.out.println("reading from stdin...");

            try {
                parser p = new parser(new Yylex(System.in));
                p.parse();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // run the parser for each of the given sorce files
            for (int i = 0; i < args.length; ++i) {
                System.out.println("reading file " + args[i] + " ...");

                try {
                    parser p = new parser(new Yylex(new FileReader(args[i])));
                    p.parse();
                } catch (FileNotFoundException fnfe) {
                    System.err.println("Error: Could not find file " + args[i]
                            + ".");
                    System.exit(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("done.");
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
