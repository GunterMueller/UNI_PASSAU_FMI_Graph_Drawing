package org.graffiti.plugins.ios.gml.gmlReader.parser;

import java_cup.runtime.Symbol;

/**
 * Class Scanner provides an interface to the lexer for the GML file format.
 * This class is primarily designated for developing and debugging purposes.
 * 
 * @author R&uuml;diger Schnoy
 */
class Scanner {
    public static void main(String argv[]) throws java.io.IOException {
        Yylex yy = new Yylex(System.in);
        // Symbol t;
        // while ((t = yy.yylex()) != null)
        // System.out.print(t + " ");
        Symbol t;
        do {
            t = yy.next_token();
            System.out.print(t);
        } while (t.sym != sym.EOF);
        System.out.println();
    }
}

class Yylex implements java_cup.runtime.Scanner {
    private final int YY_BUFFER_SIZE = 512;
    private final int YY_F = -1;
    private final int YY_NO_STATE = -1;
    private final int YY_NOT_ACCEPT = 0;
    @SuppressWarnings("unused")
    private final int YY_START = 1;
    private final int YY_END = 2;
    private final int YY_NO_ANCHOR = 4;
    private final int YY_BOL = 128;
    private final int YY_EOF = 129;

    /** Contains the number of nested comments. */
    @SuppressWarnings("unused")
    private int comments = 0;

    /** Constructs a new Symbol from a given token. */
    private Symbol token(int k) {
        return new Symbol(k, yyline + 1, yychar);
    }

    /**
     * Constructs a new Symbol from a given token and the associated value.
     */
    private Symbol token(int k, Object value) {
        return new Symbol(k, yyline + 1, yychar, value);
    }

    private java.io.BufferedReader yy_reader;
    private int yy_buffer_index;
    private int yy_buffer_read;
    private int yy_buffer_start;
    private int yy_buffer_end;
    private char yy_buffer[];
    private int yychar;
    private int yyline;
    private boolean yy_at_bol;
    private int yy_lexical_state;

    Yylex(java.io.Reader reader) {
        this();
        if (null == reader)
            throw (new Error("Error: Bad input stream initializer."));
        yy_reader = new java.io.BufferedReader(reader);
    }

    Yylex(java.io.InputStream instream) {
        this();
        if (null == instream)
            throw (new Error("Error: Bad input stream initializer."));
        yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(
                instream));
    }

    private Yylex() {
        yy_buffer = new char[YY_BUFFER_SIZE];
        yy_buffer_read = 0;
        yy_buffer_index = 0;
        yy_buffer_start = 0;
        yy_buffer_end = 0;
        yychar = 0;
        yyline = 0;
        yy_at_bol = true;
        yy_lexical_state = YYINITIAL;
    }

    @SuppressWarnings("unused")
    private boolean yy_eof_done = false;
    private final int STRING = 1;
    private final int YYINITIAL = 0;
    private final int yy_state_dtrans[] = { 0, 18 };

    private void yybegin(int state) {
        yy_lexical_state = state;
    }

    private int yy_advance() throws java.io.IOException {
        int next_read;
        int i;
        int j;

        if (yy_buffer_index < yy_buffer_read)
            return yy_buffer[yy_buffer_index++];

        if (0 != yy_buffer_start) {
            i = yy_buffer_start;
            j = 0;
            while (i < yy_buffer_read) {
                yy_buffer[j] = yy_buffer[i];
                ++i;
                ++j;
            }
            yy_buffer_end = yy_buffer_end - yy_buffer_start;
            yy_buffer_start = 0;
            yy_buffer_read = j;
            yy_buffer_index = j;
            next_read = yy_reader.read(yy_buffer, yy_buffer_read,
                    yy_buffer.length - yy_buffer_read);
            if (-1 == next_read)
                return YY_EOF;
            yy_buffer_read = yy_buffer_read + next_read;
        }

        while (yy_buffer_index >= yy_buffer_read) {
            if (yy_buffer_index >= yy_buffer.length) {
                yy_buffer = yy_double(yy_buffer);
            }
            next_read = yy_reader.read(yy_buffer, yy_buffer_read,
                    yy_buffer.length - yy_buffer_read);
            if (-1 == next_read)
                return YY_EOF;
            yy_buffer_read = yy_buffer_read + next_read;
        }
        return yy_buffer[yy_buffer_index++];
    }

    private void yy_move_end() {
        if (yy_buffer_end > yy_buffer_start
                && '\n' == yy_buffer[yy_buffer_end - 1]) {
            yy_buffer_end--;
        }
        if (yy_buffer_end > yy_buffer_start
                && '\r' == yy_buffer[yy_buffer_end - 1]) {
            yy_buffer_end--;
        }
    }

    private boolean yy_last_was_cr = false;

    private void yy_mark_start() {
        int i;
        for (i = yy_buffer_start; i < yy_buffer_index; ++i) {
            if ('\n' == yy_buffer[i] && !yy_last_was_cr) {
                ++yyline;
            }
            if ('\r' == yy_buffer[i]) {
                ++yyline;
                yy_last_was_cr = true;
            } else {
                yy_last_was_cr = false;
            }
        }
        yychar = yychar + yy_buffer_index - yy_buffer_start;
        yy_buffer_start = yy_buffer_index;
    }

    private void yy_mark_end() {
        yy_buffer_end = yy_buffer_index;
    }

    private void yy_to_mark() {
        yy_buffer_index = yy_buffer_end;
        yy_at_bol = (yy_buffer_end > yy_buffer_start)
                && ('\r' == yy_buffer[yy_buffer_end - 1]
                        || '\n' == yy_buffer[yy_buffer_end - 1]
                        || 2028/* LS */== yy_buffer[yy_buffer_end - 1] || 2029/* PS */== yy_buffer[yy_buffer_end - 1]);
    }

    private java.lang.String yytext() {
        return (new java.lang.String(yy_buffer, yy_buffer_start, yy_buffer_end
                - yy_buffer_start));
    }

    @SuppressWarnings("unused")
    private int yylength() {
        return yy_buffer_end - yy_buffer_start;
    }

    private char[] yy_double(char buf[]) {
        int i;
        char newbuf[];
        newbuf = new char[2 * buf.length];
        for (i = 0; i < buf.length; ++i) {
            newbuf[i] = buf[i];
        }
        return newbuf;
    }

    private final int YY_E_INTERNAL = 0;
    @SuppressWarnings("unused")
    private final int YY_E_MATCH = 1;
    private java.lang.String yy_error_string[] = { "Error: Internal error.\n",
            "Error: Unmatched input.\n" };

    private void yy_error(int code, boolean fatal) {
        java.lang.System.out.print(yy_error_string[code]);
        java.lang.System.out.flush();
        if (fatal)
            throw new Error("Fatal Error.\n");
    }

    private int[][] unpackFromString(int size1, int size2, String st) {
        int colonIndex = -1;
        String lengthString;
        int sequenceLength = 0;
        int sequenceInteger = 0;

        int commaIndex;
        String workString;

        int res[][] = new int[size1][size2];
        for (int i = 0; i < size1; i++) {
            for (int j = 0; j < size2; j++) {
                if (sequenceLength != 0) {
                    res[i][j] = sequenceInteger;
                    sequenceLength--;
                    continue;
                }
                commaIndex = st.indexOf(',');
                workString = (commaIndex == -1) ? st : st.substring(0,
                        commaIndex);
                st = st.substring(commaIndex + 1);
                colonIndex = workString.indexOf(':');
                if (colonIndex == -1) {
                    res[i][j] = Integer.parseInt(workString);
                    continue;
                }
                lengthString = workString.substring(colonIndex + 1);
                sequenceLength = Integer.parseInt(lengthString);
                workString = workString.substring(0, colonIndex);
                sequenceInteger = Integer.parseInt(workString);
                res[i][j] = sequenceInteger;
                sequenceLength--;
            }
        }
        return res;
    }

    private int yy_acpt[] = {
    /* 0 */YY_NOT_ACCEPT,
    /* 1 */YY_NO_ANCHOR,
    /* 2 */YY_NO_ANCHOR,
    /* 3 */YY_NO_ANCHOR,
    /* 4 */YY_NO_ANCHOR,
    /* 5 */YY_NO_ANCHOR,
    /* 6 */YY_NO_ANCHOR,
    /* 7 */YY_NO_ANCHOR,
    /* 8 */YY_NO_ANCHOR,
    /* 9 */YY_NO_ANCHOR,
    /* 10 */YY_NO_ANCHOR,
    /* 11 */YY_NO_ANCHOR,
    /* 12 */YY_NO_ANCHOR,
    /* 13 */YY_NO_ANCHOR,
    /* 14 */YY_NO_ANCHOR,
    /* 15 */YY_NO_ANCHOR,
    /* 16 */YY_NO_ANCHOR,
    /* 17 */YY_NO_ANCHOR,
    /* 18 */YY_NO_ANCHOR,
    /* 19 */YY_NO_ANCHOR,
    /* 20 */YY_NOT_ACCEPT,
    /* 21 */YY_NO_ANCHOR,
    /* 22 */YY_NO_ANCHOR,
    /* 23 */YY_NO_ANCHOR,
    /* 24 */YY_NO_ANCHOR,
    /* 25 */YY_NO_ANCHOR,
    /* 26 */YY_NO_ANCHOR,
    /* 27 */YY_NO_ANCHOR,
    /* 28 */YY_NO_ANCHOR,
    /* 29 */YY_NO_ANCHOR,
    /* 30 */YY_NO_ANCHOR,
    /* 31 */YY_NO_ANCHOR,
    /* 32 */YY_NO_ANCHOR,
    /* 33 */YY_NO_ANCHOR,
    /* 34 */YY_NO_ANCHOR,
    /* 35 */YY_NOT_ACCEPT,
    /* 36 */YY_NO_ANCHOR,
    /* 37 */YY_NO_ANCHOR,
    /* 38 */YY_NO_ANCHOR,
    /* 39 */YY_NO_ANCHOR,
    /* 40 */YY_NOT_ACCEPT,
    /* 41 */YY_NO_ANCHOR,
    /* 42 */YY_NO_ANCHOR,
    /* 43 */YY_NO_ANCHOR,
    /* 44 */YY_NO_ANCHOR,
    /* 45 */YY_NO_ANCHOR,
    /* 46 */YY_NO_ANCHOR,
    /* 47 */YY_NO_ANCHOR,
    /* 48 */YY_NO_ANCHOR,
    /* 49 */YY_NO_ANCHOR,
    /* 50 */YY_NO_ANCHOR,
    /* 51 */YY_NO_ANCHOR,
    /* 52 */YY_NO_ANCHOR,
    /* 53 */YY_NO_ANCHOR,
    /* 54 */YY_NO_ANCHOR,
    /* 55 */YY_NO_ANCHOR,
    /* 56 */YY_NO_ANCHOR,
    /* 57 */YY_NO_ANCHOR,
    /* 58 */YY_NO_ANCHOR,
    /* 59 */YY_NO_ANCHOR,
    /* 60 */YY_NO_ANCHOR,
    /* 61 */YY_NO_ANCHOR,
    /* 62 */YY_NO_ANCHOR,
    /* 63 */YY_NO_ANCHOR,
    /* 64 */YY_NO_ANCHOR,
    /* 65 */YY_NO_ANCHOR,
    /* 66 */YY_NO_ANCHOR,
    /* 67 */YY_NO_ANCHOR,
    /* 68 */YY_NO_ANCHOR,
    /* 69 */YY_NO_ANCHOR,
    /* 70 */YY_NO_ANCHOR,
    /* 71 */YY_NO_ANCHOR,
    /* 72 */YY_NO_ANCHOR,
    /* 73 */YY_NO_ANCHOR,
    /* 74 */YY_NO_ANCHOR,
    /* 75 */YY_NO_ANCHOR,
    /* 76 */YY_NO_ANCHOR,
    /* 77 */YY_NO_ANCHOR,
    /* 78 */YY_NO_ANCHOR,
    /* 79 */YY_NO_ANCHOR,
    /* 80 */YY_NO_ANCHOR,
    /* 81 */YY_NO_ANCHOR,
    /* 82 */YY_NO_ANCHOR,
    /* 83 */YY_NO_ANCHOR,
    /* 84 */YY_NO_ANCHOR,
    /* 85 */YY_NO_ANCHOR,
    /* 86 */YY_NO_ANCHOR,
    /* 87 */YY_NO_ANCHOR,
    /* 88 */YY_NO_ANCHOR,
    /* 89 */YY_NO_ANCHOR,
    /* 90 */YY_NO_ANCHOR,
    /* 91 */YY_NO_ANCHOR,
    /* 92 */YY_NO_ANCHOR,
    /* 93 */YY_NO_ANCHOR,
    /* 94 */YY_NO_ANCHOR,
    /* 95 */YY_NO_ANCHOR,
    /* 96 */YY_NO_ANCHOR,
    /* 97 */YY_NO_ANCHOR,
    /* 98 */YY_NO_ANCHOR,
    /* 99 */YY_NO_ANCHOR,
    /* 100 */YY_NO_ANCHOR,
    /* 101 */YY_NO_ANCHOR,
    /* 102 */YY_NO_ANCHOR };
    private int yy_cmap[] = unpackFromString(
            1,
            130,
            "3:9,4,1,3:2,1,3:18,4,3,26,2,3:7,21,3,22,24,3,23:10,3:7,27:4,25,27:21,19,3,2"
                    + "0,3,14,3,7,27:2,12,13,27,5,9,27:3,18,27,10,11,8,27,6,15,16,27:4,17,27,3:5,0"
                    + ":2")[0];

    private int yy_rmap[] = unpackFromString(
            1,
            103,
            "0,1,2,3,1,4,1:3,5,1,6,7,8,9:3,10,11,1,12,13,14,15,16:2,17,1,18,19,16:3,20,1"
                    + "6,21,22,10,23,24,23,25,16,26,27,28,29,16,30,31,20,32,33,34,35,32,36,37,38,3"
                    + "9,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,9,58,59,60,61,62,63"
                    + ",9,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80")[0];

    private int yy_nxt[][] = unpackFromString(
            81,
            28,
            "1,2,3,4,2,5,85:4,70,85:2,74,85:5,6,7,8,22,26,4,85,9,85,-1:29,2,-1:2,2,-1:25"
                    + ",3:26,-1:5,85,76,85:12,-1:4,78,-1,85,-1,85,-1:26,10,-1:14,35,-1:8,40,11,-1,"
                    + "35,-1:7,85:9,80,85:4,-1:4,78,-1,85,-1,85,-1:5,85:9,95,85:4,-1:4,78,-1,85,-1"
                    + ",85,-1:5,85:14,-1:4,78,-1,85,-1,85,-1:23,17,20,-1:3,1,21,34:2,21,87,34:4,72"
                    + ",34:2,75,34:5,24,25,42,39,46,34:2,19,34,-1:23,11,-1:5,21,34:2,21,34:21,-1,3"
                    + "4,-1:23,26,-1:9,85:8,12,85:5,-1:4,78,-1,85,-1,85,-1,34:25,-1,34,-1:23,101,2"
                    + "0,-1:4,34:13,93,34:11,-1,34,-1,34:13,96,34:11,-1,34,-1,34:22,33,44,34,-1,34"
                    + ",-1:21,27,-1:11,85:8,13,85:5,-1:4,78,-1,85,-1,85,-1:23,38,-1:5,34:22,46,34:"
                    + "2,-1,34,-1:5,85:4,14,85:9,-1:4,78,-1,85,-1,85,-1,34:12,54,34:8,55,43,34,54,"
                    + "-1,34,-1,34:22,43,34:2,-1,34,-1:5,85:8,15,85:5,-1:4,78,-1,85,-1,85,-1,34:22"
                    + ",102,44,34,-1,34,-1,34:12,28,34:12,-1,34,-1:5,85:8,16,85:5,-1:4,78,-1,85,-1"
                    + ",85,-1,34:22,51,34:2,-1,34,-1,34:12,29,34:12,-1,34,-1,34:8,30,34:16,-1,34,-"
                    + "1,34:20,47,34:4,-1,34,-1,34:12,31,34:12,-1,34,-1,34:12,32,34:12,-1,34,-1:5,"
                    + "85:7,23,85:6,-1:4,78,-1,85,-1,85,-1:23,37,20,-1:4,34:11,48,34:13,-1,34,-1,3"
                    + "4:22,50,44,34,-1,34,-1:5,36,85:13,-1:4,78,-1,85,-1,85,-1,34:4,52,34:20,-1,3"
                    + "4,-1:5,85:3,41,85:10,-1:4,78,-1,85,-1,85,-1,34:7,53,34:17,-1,34,-1:5,85:13,"
                    + "45,-1:4,78,-1,85,-1,85,-1,34:17,56,34:7,-1,34,-1:5,85:13,49,-1:4,78,-1,85,-"
                    + "1,85,-1,34:17,57,34:7,-1,34,-1:5,85:6,58,85:7,-1:4,78,-1,85,-1,85,-1:23,59,"
                    + "20,-1:4,34:10,60,34:14,-1,34,-1,34:22,61,44,34,-1,34,-1:5,85:7,62,85:6,-1:4"
                    + ",78,-1,85,-1,85,-1,34:11,63,34:13,-1,34,-1:5,85:2,64,85:11,-1:4,78,-1,85,-1"
                    + ",85,-1,34:6,65,34:18,-1,34,-1,34:16,67,34:8,-1,34,-1:5,85:10,82,85:3,-1:4,7"
                    + "8,-1,85,-1,85,-1,34:16,69,34:8,-1,34,-1:5,85:11,83,85:2,-1:4,78,-1,85,-1,85"
                    + ",-1:5,85:12,66,85,-1:4,78,-1,85,-1,85,-1:5,85:12,68,85,-1:4,78,-1,85,-1,85,"
                    + "-1:23,71,20,-1:4,34:5,77,34:19,-1,34,-1,34:22,73,44,34,-1,34,-1:5,85:11,84,"
                    + "85:2,-1:4,78,-1,85,-1,85,-1,34:15,79,34:9,-1,34,-1,34:15,81,34:9,-1,34,-1:2"
                    + "3,86,20,-1:4,34:14,90,34:10,-1,34,-1,34:22,88,44,34,-1,34,-1:5,85:10,89,85:"
                    + "3,-1:4,78,-1,85,-1,85,-1,34:14,91,34:10,-1,34,-1:23,92,20,-1:4,34:22,94,44,"
                    + "34,-1,34,-1:23,97,20,-1:4,34:22,98,44,34,-1,34,-1:23,99,20,-1:4,34:22,100,4"
                    + "4,34,-1,34");

    public java_cup.runtime.Symbol next_token() throws java.io.IOException {
        int yy_lookahead;
        int yy_anchor = YY_NO_ANCHOR;
        int yy_state = yy_state_dtrans[yy_lexical_state];
        int yy_next_state = YY_NO_STATE;
        int yy_last_accept_state = YY_NO_STATE;
        boolean yy_initial = true;
        int yy_this_accept;

        yy_mark_start();
        yy_this_accept = yy_acpt[yy_state];
        if (YY_NOT_ACCEPT != yy_this_accept) {
            yy_last_accept_state = yy_state;
            yy_mark_end();
        }
        while (true) {
            if (yy_initial && yy_at_bol) {
                yy_lookahead = YY_BOL;
            } else {
                yy_lookahead = yy_advance();
            }
            yy_next_state = YY_F;
            yy_next_state = yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];
            if (YY_EOF == yy_lookahead && true == yy_initial)
                return new Symbol(sym.EOF, null);
            if (YY_F != yy_next_state) {
                yy_state = yy_next_state;
                yy_initial = false;
                yy_this_accept = yy_acpt[yy_state];
                if (YY_NOT_ACCEPT != yy_this_accept) {
                    yy_last_accept_state = yy_state;
                    yy_mark_end();
                }
            } else {
                if (YY_NO_STATE == yy_last_accept_state)
                    throw (new Error("Lexical Error: Unmatched Input."));
                else {
                    yy_anchor = yy_acpt[yy_last_accept_state];
                    if (0 != (YY_END & yy_anchor)) {
                        yy_move_end();
                    }
                    yy_to_mark();
                    switch (yy_last_accept_state) {
                    case 1:

                    case -2:
                        break;
                    case 2: { /* nothing to be done */
                    }
                    case -3:
                        break;
                    case 3: { /* nothing to be done */
                    }
                    case -4:
                        break;
                    case 4: {
                        System.err.println("\nUnmatched " + "input (line "
                                + (yyline + 1) + " column " + (yychar + 1)
                                + "): \"" + yytext() + "\"");
                    }
                    case -5:
                        break;
                    case 5: {
                        return token(sym.KEY, yytext());
                    }
                    case -6:
                        break;
                    case 6: {
                        return token(sym.LBRACE);
                    }
                    case -7:
                        break;
                    case 7: {
                        return token(sym.RBRACE);
                    }
                    case -8:
                        break;
                    case 8: {
                        return token(sym.INTEGER, new Integer(yytext()));
                    }
                    case -9:
                        break;
                    case 9: {
                        yybegin(STRING);
                    }
                    case -10:
                        break;
                    case 10: {
                        return token(sym.STRING, "");
                    }
                    case -11:
                        break;
                    case 11: {
                        return token(sym.REAL, new Double(yytext()));
                    }
                    case -12:
                        break;
                    case 12: {
                        return token(sym.NODE);
                    }
                    case -13:
                        break;
                    case 13: {
                        return token(sym.EDGE);
                    }
                    case -14:
                        break;
                    case 14: {
                        return token(sym.GRAPH);
                    }
                    case -15:
                        break;
                    case 15: {
                        return token(sym.NODESTYLE);
                    }
                    case -16:
                        break;
                    case 16: {
                        return token(sym.EDGESTYLE);
                    }
                    case -17:
                        break;
                    case 17: {
                        return token(sym.INTEGER, new Integer(0));
                    }
                    case -18:
                        break;
                    case 18: {
                        return token(sym.STRING, yytext());
                    }
                    case -19:
                        break;
                    case 19: {
                        yybegin(YYINITIAL);
                    }
                    case -20:
                        break;
                    case 21: { /* nothing to be done */
                    }
                    case -21:
                        break;
                    case 22: {
                        System.err.println("\nUnmatched " + "input (line "
                                + (yyline + 1) + " column " + (yychar + 1)
                                + "): \"" + yytext() + "\"");
                    }
                    case -22:
                        break;
                    case 23: {
                        return token(sym.KEY, yytext());
                    }
                    case -23:
                        break;
                    case 24: {
                        return token(sym.LBRACE);
                    }
                    case -24:
                        break;
                    case 25: {
                        return token(sym.RBRACE);
                    }
                    case -25:
                        break;
                    case 26: {
                        return token(sym.INTEGER, new Integer(yytext()));
                    }
                    case -26:
                        break;
                    case 27: {
                        return token(sym.REAL, new Double(yytext()));
                    }
                    case -27:
                        break;
                    case 28: {
                        return token(sym.NODE);
                    }
                    case -28:
                        break;
                    case 29: {
                        return token(sym.EDGE);
                    }
                    case -29:
                        break;
                    case 30: {
                        return token(sym.GRAPH);
                    }
                    case -30:
                        break;
                    case 31: {
                        return token(sym.NODESTYLE);
                    }
                    case -31:
                        break;
                    case 32: {
                        return token(sym.EDGESTYLE);
                    }
                    case -32:
                        break;
                    case 33: {
                        return token(sym.INTEGER, new Integer(0));
                    }
                    case -33:
                        break;
                    case 34: {
                        return token(sym.STRING, yytext());
                    }
                    case -34:
                        break;
                    case 36: {
                        return token(sym.KEY, yytext());
                    }
                    case -35:
                        break;
                    case 37: {
                        return token(sym.INTEGER, new Integer(yytext()));
                    }
                    case -36:
                        break;
                    case 38: {
                        return token(sym.REAL, new Double(yytext()));
                    }
                    case -37:
                        break;
                    case 39: {
                        return token(sym.STRING, yytext());
                    }
                    case -38:
                        break;
                    case 41: {
                        return token(sym.KEY, yytext());
                    }
                    case -39:
                        break;
                    case 42: {
                        return token(sym.INTEGER, new Integer(yytext()));
                    }
                    case -40:
                        break;
                    case 43: {
                        return token(sym.REAL, new Double(yytext()));
                    }
                    case -41:
                        break;
                    case 44: {
                        return token(sym.STRING, yytext());
                    }
                    case -42:
                        break;
                    case 45: {
                        return token(sym.KEY, yytext());
                    }
                    case -43:
                        break;
                    case 46: {
                        return token(sym.INTEGER, new Integer(yytext()));
                    }
                    case -44:
                        break;
                    case 47: {
                        return token(sym.REAL, new Double(yytext()));
                    }
                    case -45:
                        break;
                    case 48: {
                        return token(sym.STRING, yytext());
                    }
                    case -46:
                        break;
                    case 49: {
                        return token(sym.KEY, yytext());
                    }
                    case -47:
                        break;
                    case 50: {
                        return token(sym.INTEGER, new Integer(yytext()));
                    }
                    case -48:
                        break;
                    case 51: {
                        return token(sym.REAL, new Double(yytext()));
                    }
                    case -49:
                        break;
                    case 52: {
                        return token(sym.STRING, yytext());
                    }
                    case -50:
                        break;
                    case 53: {
                        return token(sym.STRING, yytext());
                    }
                    case -51:
                        break;
                    case 54: {
                        return token(sym.STRING, yytext());
                    }
                    case -52:
                        break;
                    case 55: {
                        return token(sym.STRING, yytext());
                    }
                    case -53:
                        break;
                    case 56: {
                        return token(sym.STRING, yytext());
                    }
                    case -54:
                        break;
                    case 57: {
                        return token(sym.STRING, yytext());
                    }
                    case -55:
                        break;
                    case 58: {
                        return token(sym.KEY, yytext());
                    }
                    case -56:
                        break;
                    case 59: {
                        return token(sym.INTEGER, new Integer(yytext()));
                    }
                    case -57:
                        break;
                    case 60: {
                        return token(sym.STRING, yytext());
                    }
                    case -58:
                        break;
                    case 61: {
                        return token(sym.INTEGER, new Integer(yytext()));
                    }
                    case -59:
                        break;
                    case 62: {
                        return token(sym.KEY, yytext());
                    }
                    case -60:
                        break;
                    case 63: {
                        return token(sym.STRING, yytext());
                    }
                    case -61:
                        break;
                    case 64: {
                        return token(sym.KEY, yytext());
                    }
                    case -62:
                        break;
                    case 65: {
                        return token(sym.STRING, yytext());
                    }
                    case -63:
                        break;
                    case 66: {
                        return token(sym.KEY, yytext());
                    }
                    case -64:
                        break;
                    case 67: {
                        return token(sym.STRING, yytext());
                    }
                    case -65:
                        break;
                    case 68: {
                        return token(sym.KEY, yytext());
                    }
                    case -66:
                        break;
                    case 69: {
                        return token(sym.STRING, yytext());
                    }
                    case -67:
                        break;
                    case 70: {
                        return token(sym.KEY, yytext());
                    }
                    case -68:
                        break;
                    case 71: {
                        return token(sym.INTEGER, new Integer(yytext()));
                    }
                    case -69:
                        break;
                    case 72: {
                        return token(sym.STRING, yytext());
                    }
                    case -70:
                        break;
                    case 73: {
                        return token(sym.INTEGER, new Integer(yytext()));
                    }
                    case -71:
                        break;
                    case 74: {
                        return token(sym.KEY, yytext());
                    }
                    case -72:
                        break;
                    case 75: {
                        return token(sym.STRING, yytext());
                    }
                    case -73:
                        break;
                    case 76: {
                        return token(sym.KEY, yytext());
                    }
                    case -74:
                        break;
                    case 77: {
                        return token(sym.STRING, yytext());
                    }
                    case -75:
                        break;
                    case 78: {
                        return token(sym.KEY, yytext());
                    }
                    case -76:
                        break;
                    case 79: {
                        return token(sym.STRING, yytext());
                    }
                    case -77:
                        break;
                    case 80: {
                        return token(sym.KEY, yytext());
                    }
                    case -78:
                        break;
                    case 81: {
                        return token(sym.STRING, yytext());
                    }
                    case -79:
                        break;
                    case 82: {
                        return token(sym.KEY, yytext());
                    }
                    case -80:
                        break;
                    case 83: {
                        return token(sym.KEY, yytext());
                    }
                    case -81:
                        break;
                    case 84: {
                        return token(sym.KEY, yytext());
                    }
                    case -82:
                        break;
                    case 85: {
                        return token(sym.KEY, yytext());
                    }
                    case -83:
                        break;
                    case 86: {
                        return token(sym.INTEGER, new Integer(yytext()));
                    }
                    case -84:
                        break;
                    case 87: {
                        return token(sym.STRING, yytext());
                    }
                    case -85:
                        break;
                    case 88: {
                        return token(sym.INTEGER, new Integer(yytext()));
                    }
                    case -86:
                        break;
                    case 89: {
                        return token(sym.KEY, yytext());
                    }
                    case -87:
                        break;
                    case 90: {
                        return token(sym.STRING, yytext());
                    }
                    case -88:
                        break;
                    case 91: {
                        return token(sym.STRING, yytext());
                    }
                    case -89:
                        break;
                    case 92: {
                        return token(sym.INTEGER, new Integer(yytext()));
                    }
                    case -90:
                        break;
                    case 93: {
                        return token(sym.STRING, yytext());
                    }
                    case -91:
                        break;
                    case 94: {
                        return token(sym.INTEGER, new Integer(yytext()));
                    }
                    case -92:
                        break;
                    case 95: {
                        return token(sym.KEY, yytext());
                    }
                    case -93:
                        break;
                    case 96: {
                        return token(sym.STRING, yytext());
                    }
                    case -94:
                        break;
                    case 97: {
                        return token(sym.INTEGER, new Integer(yytext()));
                    }
                    case -95:
                        break;
                    case 98: {
                        return token(sym.INTEGER, new Integer(yytext()));
                    }
                    case -96:
                        break;
                    case 99: {
                        return token(sym.INTEGER, new Integer(yytext()));
                    }
                    case -97:
                        break;
                    case 100: {
                        return token(sym.INTEGER, new Integer(yytext()));
                    }
                    case -98:
                        break;
                    case 101: {
                        return token(sym.INTEGER, new Integer(yytext()));
                    }
                    case -99:
                        break;
                    case 102: {
                        return token(sym.INTEGER, new Integer(yytext()));
                    }
                    case -100:
                        break;
                    default:
                        yy_error(YY_E_INTERNAL, false);
                    case -1:
                    }
                    yy_initial = true;
                    yy_state = yy_state_dtrans[yy_lexical_state];
                    yy_next_state = YY_NO_STATE;
                    yy_last_accept_state = YY_NO_STATE;
                    yy_mark_start();
                    yy_this_accept = yy_acpt[yy_state];
                    if (YY_NOT_ACCEPT != yy_this_accept) {
                        yy_last_accept_state = yy_state;
                        yy_mark_end();
                    }
                }
            }
        }
    }
}
