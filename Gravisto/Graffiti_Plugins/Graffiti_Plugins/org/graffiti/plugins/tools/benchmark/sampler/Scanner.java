// =============================================================================
//
//   Scanner.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.sampler;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java_cup.runtime.Symbol;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class Scanner implements java_cup.runtime.Scanner {
    private static final Pattern FIRST_PATTERN = Pattern
            .compile("\\s*([\\(\\)\\[\\]\\,\\=\\+\\-\\*\\/\\^]).*");
    private static final Pattern SECOND_PATTERN = Pattern
            .compile("\\s*([a-zA-Z0-9\\.\\-_]+)(?:[^a-zA-Z0-9\\.\\-_].*)?");
    private static Map<String, Integer> map;

    private String string;

    public Scanner(String string) {
        this.string = string;
        if (map == null) {
            map = new HashMap<String, Integer>();
            map.put("(", Symbols.LPAREN);
            map.put(")", Symbols.RPAREN);
            map.put("[", Symbols.LQUOTE);
            map.put("]", Symbols.RQUOTE);
            map.put(",", Symbols.KOMMA);
            map.put("=", Symbols.ASSIGN);
            map.put("+", Symbols.PLUS);
            map.put("-", Symbols.MINUS);
            map.put("*", Symbols.TIMES);
            map.put("/", Symbols.DIV);
            map.put("^", Symbols.EXP);
        }
    }

    private String nextString() {
        if (string.isEmpty())
            return null;
        Matcher m = FIRST_PATTERN.matcher(string);
        if (!m.matches()) {
            m = SECOND_PATTERN.matcher(string);
            if (!m.matches()) {
                System.out.println("No match in \"" + string + "\"!");
                return null;
            }
        }
        String result = m.group(1);
        string = string.substring(m.end(1));
        return result;
    }

    public Symbol next_token() {
        String next = nextString();
        if (next == null)
            return new Symbol(Symbols.EOF);
        Integer id = map.get(next);
        if (id != null)
            return new Symbol(id);
        else {
            try {
                return new Symbol(Symbols.NUMBER, Double.valueOf(next));
            } catch (NumberFormatException e) {
                return new Symbol(Symbols.ID, next);
            }
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
