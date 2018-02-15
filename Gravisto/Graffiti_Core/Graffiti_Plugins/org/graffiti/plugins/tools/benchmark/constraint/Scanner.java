// =============================================================================
//
//   Scanner.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.constraint;

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
            .compile("\\s*(\\&\\&|\\&|\\|\\||\\||\\<\\=\\>|\\<\\>|\\=\\>|\\<\\=|\\<|\\>\\=|\\>|\\=\\=|\\=|\\^|\\~|\\!\\=|\\!|\\(|\\)|\\{|\\}|\\,).*");
    private static final Pattern SECOND_PATTERN = Pattern
            .compile("\\s*(and|or|xor|equiv|not|implies|in|[a-zA-Z0-9\\.\\-_]+)(?:[^a-zA-Z0-9\\.\\-_].*)?");
    private static Map<String, Integer> map;

    private String string;

    public Scanner(String string) {
        this.string = string;
        if (map == null) {
            map = new HashMap<String, Integer>();
            map.put("and", Symbols.AND);
            map.put("or", Symbols.OR);
            map.put("xor", Symbols.XOR);
            map.put("equiv", Symbols.EQUIV);
            map.put("not", Symbols.NOT);
            map.put("implies", Symbols.IMPLIES);
            map.put("in", Symbols.IN);
            map.put("&", Symbols.AND);
            map.put("&&", Symbols.AND);
            map.put("|", Symbols.OR);
            map.put("||", Symbols.OR);
            map.put("~", Symbols.NOT);
            map.put("!", Symbols.NOT);
            map.put("=>", Symbols.IMPLIES);
            map.put("<=>", Symbols.EQUIV);
            map.put("^", Symbols.XOR);
            map.put("=", Symbols.EQ);
            map.put("==", Symbols.EQ);
            map.put("!=", Symbols.NEQ);
            map.put("<>", Symbols.NEQ);
            map.put("<", Symbols.LT);
            map.put(">", Symbols.GT);
            map.put("<=", Symbols.LEQ);
            map.put(">=", Symbols.GEQ);
            map.put(",", Symbols.KOMMA);
            map.put("(", Symbols.LPAREN);
            map.put(")", Symbols.RPAREN);
            map.put("{", Symbols.LSETPAREN);
            map.put("}", Symbols.RSETPAREN);
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
        else
            return new Symbol(Symbols.STRING, next);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
