package de.chris.plugins.inputserializers.test;

import java_cup.runtime.Symbol;

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

    private int nodeCount = 0;
    private int edgeCount = 0;
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
    private final int EDGE = 3;
    private final int NODE = 2;
    private final int YYINITIAL = 0;
    private final int GRAPH = 1;
    private final int yy_state_dtrans[] = { 0, 25, 39, 40 };

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
    /* 19 */YY_NOT_ACCEPT,
    /* 20 */YY_NO_ANCHOR,
    /* 21 */YY_NOT_ACCEPT,
    /* 22 */YY_NO_ANCHOR,
    /* 23 */YY_NOT_ACCEPT,
    /* 24 */YY_NO_ANCHOR,
    /* 25 */YY_NOT_ACCEPT,
    /* 26 */YY_NO_ANCHOR,
    /* 27 */YY_NOT_ACCEPT,
    /* 28 */YY_NO_ANCHOR,
    /* 29 */YY_NOT_ACCEPT,
    /* 30 */YY_NO_ANCHOR,
    /* 31 */YY_NOT_ACCEPT,
    /* 32 */YY_NOT_ACCEPT,
    /* 33 */YY_NOT_ACCEPT,
    /* 34 */YY_NOT_ACCEPT,
    /* 35 */YY_NOT_ACCEPT,
    /* 36 */YY_NOT_ACCEPT,
    /* 37 */YY_NOT_ACCEPT,
    /* 38 */YY_NOT_ACCEPT,
    /* 39 */YY_NOT_ACCEPT,
    /* 40 */YY_NOT_ACCEPT,
    /* 41 */YY_NOT_ACCEPT,
    /* 42 */YY_NOT_ACCEPT,
    /* 43 */YY_NOT_ACCEPT,
    /* 44 */YY_NOT_ACCEPT,
    /* 45 */YY_NOT_ACCEPT,
    /* 46 */YY_NOT_ACCEPT,
    /* 47 */YY_NOT_ACCEPT,
    /* 48 */YY_NOT_ACCEPT,
    /* 49 */YY_NO_ANCHOR };
    private int yy_cmap[] = unpackFromString(1, 130,
            "20:9,1,2,20,1:2,20:18,1,20:15,5:10,20:33,3,20,4,20:3,10,20,16,7,15,20,8,12,"
                    + "6,20:4,13,14,11,20,9,18,17,19,20:10,0:2")[0];

    private int yy_rmap[] = unpackFromString(1, 50,
            "0,1:4,2,1:13,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,"
                    + "26,27,28,29,30,31,32,33")[0];

    private int yy_nxt[][] = unpackFromString(
            34,
            21,
            "1,2,3,4:2,5,20,4,22,4:12,-1:26,5,-1:25,21,-1:17,6,-1:24,23,-1:18,19,-1:23,7"
                    + ",-1:14,27,-1:14,1,2,3,8,9,5,20,24,22,4:4,26,4,28,4:5,-1:14,29,-1:15,32,-1:1"
                    + "8,31,-1:20,33,-1:23,41,-1:18,34,-1:27,35,-1:20,10,-1:20,11,-1:21,36,-1:21,3"
                    + "7,-1:18,38,-1:12,12,-1:13,1,2,3,13,14,5,20,4,22,4:12,1,2,3,15,16,5,20,4,22,"
                    + "4:8,30,49,4:2,-1:9,43,-1:30,44,-1:9,45,-1:21,46,-1:26,47,-1:21,48,-1:21,17,"
                    + "-1:18,18,-1:19,42,-1:6");

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
                return null;
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
                    case 2: {
                    }
                    case -3:
                        break;
                    case 3: {
                    }
                    case -4:
                        break;
                    case 4: {
                        System.out.println("\nUnmatched input: " + yytext()
                                + " in line " + (yyline + 1));
                    }
                    case -5:
                        break;
                    case 5: {
                        return new Symbol(sym.INTEGER, new Integer(yytext()));
                    }
                    case -6:
                        break;
                    case 6: {
                        return new Symbol(sym.ID);
                    }
                    case -7:
                        break;
                    case 7: {
                        yybegin(GRAPH);
                        return new Symbol(sym.GRAPH);
                    }
                    case -8:
                        break;
                    case 8: {
                        return new Symbol(sym.SBRACE);
                    }
                    case -9:
                        break;
                    case 9: {
                        return new Symbol(sym.CBRACE);
                    }
                    case -10:
                        break;
                    case 10: {
                        yybegin(NODE);
                        return new Symbol(sym.NODE);
                    }
                    case -11:
                        break;
                    case 11: {
                        yybegin(EDGE);
                        return new Symbol(sym.EDGE);
                    }
                    case -12:
                        break;
                    case 12: {
                        return new Symbol(sym.DIRECTED);
                    }
                    case -13:
                        break;
                    case 13: {
                        nodeCount++;
                        return new Symbol(sym.SBRACE);
                    }
                    case -14:
                        break;
                    case 14: {
                        nodeCount--;
                        if (nodeCount == 0) {
                            yybegin(GRAPH);
                        }
                        return new Symbol(sym.CBRACE);
                    }
                    case -15:
                        break;
                    case 15: {
                        edgeCount++;
                        return new Symbol(sym.SBRACE);
                    }
                    case -16:
                        break;
                    case 16: {
                        edgeCount--;
                        if (edgeCount == 0) {
                            yybegin(GRAPH);
                        }
                        return new Symbol(sym.CBRACE);
                    }
                    case -17:
                        break;
                    case 17: {
                        return new Symbol(sym.TARGET);
                    }
                    case -18:
                        break;
                    case 18: {
                        return new Symbol(sym.SOURCE);
                    }
                    case -19:
                        break;
                    case 20: {
                        System.out.println("\nUnmatched input: " + yytext()
                                + " in line " + (yyline + 1));
                    }
                    case -20:
                        break;
                    case 22: {
                        System.out.println("\nUnmatched input: " + yytext()
                                + " in line " + (yyline + 1));
                    }
                    case -21:
                        break;
                    case 24: {
                        System.out.println("\nUnmatched input: " + yytext()
                                + " in line " + (yyline + 1));
                    }
                    case -22:
                        break;
                    case 26: {
                        System.out.println("\nUnmatched input: " + yytext()
                                + " in line " + (yyline + 1));
                    }
                    case -23:
                        break;
                    case 28: {
                        System.out.println("\nUnmatched input: " + yytext()
                                + " in line " + (yyline + 1));
                    }
                    case -24:
                        break;
                    case 30: {
                        System.out.println("\nUnmatched input: " + yytext()
                                + " in line " + (yyline + 1));
                    }
                    case -25:
                        break;
                    case 49: {
                        System.out.println("\nUnmatched input: " + yytext()
                                + " in line " + (yyline + 1));
                    }
                    case -26:
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
