package org.graffiti.plugins.ios.importers.gml;

import java_cup.runtime.Symbol;

class Sample {
    public static void main(String argv[]) throws java.io.IOException {
        Yylex yy = new Yylex(System.in);
        // Yytoken t;
        // while ((t = yy.yylex()) != null) System.out.println(t);
        Symbol t;
        while ((t = yy.next_token()) != null) {
            System.out.print(t);
        }
        System.out.println();
    }
}

class Yylex implements java_cup.runtime.Scanner {
    private final int YY_BUFFER_SIZE = 512;

    private final int YY_F = -1;

    private final int YY_NO_STATE = -1;

    private final int YY_NOT_ACCEPT = 0;

    private final int YY_START = 1;

    private final int YY_END = 2;

    private final int YY_NO_ANCHOR = 4;

    private final int YY_BOL = 128;

    private final int YY_EOF = 129;

    private int nodeCount = 0;

    private int edgeCount = 0;

    private int graphicsCount = 0;

    private int pointCount = 0;

    private int lineCount = 0;

    private int stateBeforeGraphics = -1;

    private int stateBeforePoint = -1;

    private int stateBeforeLine = -1;

    private int ignoreCount = 0;

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

    private boolean yy_eof_done = false;

    private final int EDGESTYLE = 10;

    private final int EDGE = 3;

    private final int EDGELABELGRAPHICS = 8;

    private final int NODE = 2;

    private final int EMPTY = 11;

    private final int NODESTYLE = 9;

    private final int POINT = 6;

    private final int GRAPH = 1;

    private final int YYINITIAL = 0;

    private final int GRAPHICS = 4;

    private final int NODELABELGRAPHICS = 7;

    private final int LINE = 5;

    private final int yy_state_dtrans[] = { 0, 152, 154, 156, 158, 160, 162,
            164, 166, 168, 170, 78 };

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
    /* 7 */YY_END,
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
    /* 20 */YY_NO_ANCHOR,
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
    /* 35 */YY_NO_ANCHOR,
    /* 36 */YY_NO_ANCHOR,
    /* 37 */YY_NO_ANCHOR,
    /* 38 */YY_NO_ANCHOR,
    /* 39 */YY_NO_ANCHOR,
    /* 40 */YY_NO_ANCHOR,
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
    /* 81 */YY_NOT_ACCEPT,
    /* 82 */YY_NO_ANCHOR,
    /* 83 */YY_NO_ANCHOR,
    /* 84 */YY_NO_ANCHOR,
    /* 85 */YY_NO_ANCHOR,
    /* 86 */YY_NO_ANCHOR,
    /* 87 */YY_END,
    /* 88 */YY_NO_ANCHOR,
    /* 89 */YY_NO_ANCHOR,
    /* 90 */YY_NO_ANCHOR,
    /* 91 */YY_NO_ANCHOR,
    /* 92 */YY_NO_ANCHOR,
    /* 93 */YY_NO_ANCHOR,
    /* 94 */YY_NO_ANCHOR,
    /* 95 */YY_NO_ANCHOR,
    /* 96 */YY_NO_ANCHOR,
    /* 97 */YY_NOT_ACCEPT,
    /* 98 */YY_NO_ANCHOR,
    /* 99 */YY_NO_ANCHOR,
    /* 100 */YY_END,
    /* 101 */YY_NO_ANCHOR,
    /* 102 */YY_NO_ANCHOR,
    /* 103 */YY_NO_ANCHOR,
    /* 104 */YY_NOT_ACCEPT,
    /* 105 */YY_NO_ANCHOR,
    /* 106 */YY_NO_ANCHOR,
    /* 107 */YY_END,
    /* 108 */YY_NO_ANCHOR,
    /* 109 */YY_NO_ANCHOR,
    /* 110 */YY_NO_ANCHOR,
    /* 111 */YY_NOT_ACCEPT,
    /* 112 */YY_NO_ANCHOR,
    /* 113 */YY_NO_ANCHOR,
    /* 114 */YY_NO_ANCHOR,
    /* 115 */YY_NOT_ACCEPT,
    /* 116 */YY_NO_ANCHOR,
    /* 117 */YY_NO_ANCHOR,
    /* 118 */YY_NO_ANCHOR,
    /* 119 */YY_NOT_ACCEPT,
    /* 120 */YY_NO_ANCHOR,
    /* 121 */YY_NO_ANCHOR,
    /* 122 */YY_NOT_ACCEPT,
    /* 123 */YY_NO_ANCHOR,
    /* 124 */YY_NO_ANCHOR,
    /* 125 */YY_NOT_ACCEPT,
    /* 126 */YY_NO_ANCHOR,
    /* 127 */YY_NO_ANCHOR,
    /* 128 */YY_NOT_ACCEPT,
    /* 129 */YY_NO_ANCHOR,
    /* 130 */YY_NO_ANCHOR,
    /* 131 */YY_NOT_ACCEPT,
    /* 132 */YY_NO_ANCHOR,
    /* 133 */YY_NO_ANCHOR,
    /* 134 */YY_NOT_ACCEPT,
    /* 135 */YY_NO_ANCHOR,
    /* 136 */YY_NO_ANCHOR,
    /* 137 */YY_NOT_ACCEPT,
    /* 138 */YY_NO_ANCHOR,
    /* 139 */YY_NO_ANCHOR,
    /* 140 */YY_NOT_ACCEPT,
    /* 141 */YY_NO_ANCHOR,
    /* 142 */YY_NO_ANCHOR,
    /* 143 */YY_NOT_ACCEPT,
    /* 144 */YY_NO_ANCHOR,
    /* 145 */YY_NO_ANCHOR,
    /* 146 */YY_NOT_ACCEPT,
    /* 147 */YY_NO_ANCHOR,
    /* 148 */YY_NO_ANCHOR,
    /* 149 */YY_NOT_ACCEPT,
    /* 150 */YY_NO_ANCHOR,
    /* 151 */YY_NO_ANCHOR,
    /* 152 */YY_NOT_ACCEPT,
    /* 153 */YY_NO_ANCHOR,
    /* 154 */YY_NOT_ACCEPT,
    /* 155 */YY_NO_ANCHOR,
    /* 156 */YY_NOT_ACCEPT,
    /* 157 */YY_NO_ANCHOR,
    /* 158 */YY_NOT_ACCEPT,
    /* 159 */YY_NO_ANCHOR,
    /* 160 */YY_NOT_ACCEPT,
    /* 161 */YY_NO_ANCHOR,
    /* 162 */YY_NOT_ACCEPT,
    /* 163 */YY_NO_ANCHOR,
    /* 164 */YY_NOT_ACCEPT,
    /* 165 */YY_NO_ANCHOR,
    /* 166 */YY_NOT_ACCEPT,
    /* 167 */YY_NO_ANCHOR,
    /* 168 */YY_NOT_ACCEPT,
    /* 169 */YY_NO_ANCHOR,
    /* 170 */YY_NOT_ACCEPT,
    /* 171 */YY_NO_ANCHOR,
    /* 172 */YY_NO_ANCHOR,
    /* 173 */YY_NO_ANCHOR,
    /* 174 */YY_NO_ANCHOR,
    /* 175 */YY_NO_ANCHOR,
    /* 176 */YY_NO_ANCHOR,
    /* 177 */YY_NO_ANCHOR,
    /* 178 */YY_NO_ANCHOR,
    /* 179 */YY_NO_ANCHOR,
    /* 180 */YY_NO_ANCHOR,
    /* 181 */YY_NO_ANCHOR,
    /* 182 */YY_NO_ANCHOR,
    /* 183 */YY_NO_ANCHOR,
    /* 184 */YY_NO_ANCHOR,
    /* 185 */YY_NO_ANCHOR,
    /* 186 */YY_NO_ANCHOR,
    /* 187 */YY_NO_ANCHOR,
    /* 188 */YY_NO_ANCHOR,
    /* 189 */YY_NO_ANCHOR,
    /* 190 */YY_NO_ANCHOR,
    /* 191 */YY_NO_ANCHOR,
    /* 192 */YY_NOT_ACCEPT,
    /* 193 */YY_NO_ANCHOR,
    /* 194 */YY_NO_ANCHOR,
    /* 195 */YY_NO_ANCHOR,
    /* 196 */YY_NO_ANCHOR,
    /* 197 */YY_NO_ANCHOR,
    /* 198 */YY_NO_ANCHOR,
    /* 199 */YY_NO_ANCHOR,
    /* 200 */YY_NO_ANCHOR,
    /* 201 */YY_NO_ANCHOR,
    /* 202 */YY_NO_ANCHOR,
    /* 203 */YY_NO_ANCHOR,
    /* 204 */YY_NO_ANCHOR,
    /* 205 */YY_NO_ANCHOR,
    /* 206 */YY_NO_ANCHOR,
    /* 207 */YY_NO_ANCHOR,
    /* 208 */YY_NO_ANCHOR,
    /* 209 */YY_NO_ANCHOR,
    /* 210 */YY_NO_ANCHOR,
    /* 211 */YY_NO_ANCHOR,
    /* 212 */YY_NO_ANCHOR,
    /* 213 */YY_NO_ANCHOR,
    /* 214 */YY_NO_ANCHOR,
    /* 215 */YY_NO_ANCHOR,
    /* 216 */YY_NO_ANCHOR,
    /* 217 */YY_NO_ANCHOR,
    /* 218 */YY_NO_ANCHOR,
    /* 219 */YY_NO_ANCHOR,
    /* 220 */YY_NO_ANCHOR,
    /* 221 */YY_NO_ANCHOR,
    /* 222 */YY_NO_ANCHOR,
    /* 223 */YY_NO_ANCHOR,
    /* 224 */YY_NO_ANCHOR,
    /* 225 */YY_NO_ANCHOR,
    /* 226 */YY_NO_ANCHOR,
    /* 227 */YY_NO_ANCHOR,
    /* 228 */YY_NO_ANCHOR,
    /* 229 */YY_NO_ANCHOR,
    /* 230 */YY_NO_ANCHOR,
    /* 231 */YY_NO_ANCHOR,
    /* 232 */YY_NO_ANCHOR,
    /* 233 */YY_NO_ANCHOR,
    /* 234 */YY_NO_ANCHOR,
    /* 235 */YY_NO_ANCHOR,
    /* 236 */YY_NO_ANCHOR,
    /* 237 */YY_NO_ANCHOR,
    /* 238 */YY_NO_ANCHOR,
    /* 239 */YY_NO_ANCHOR,
    /* 240 */YY_NO_ANCHOR,
    /* 241 */YY_NO_ANCHOR,
    /* 242 */YY_NO_ANCHOR,
    /* 243 */YY_NO_ANCHOR,
    /* 244 */YY_NO_ANCHOR,
    /* 245 */YY_NO_ANCHOR,
    /* 246 */YY_NO_ANCHOR,
    /* 247 */YY_NO_ANCHOR,
    /* 248 */YY_NO_ANCHOR,
    /* 249 */YY_NO_ANCHOR,
    /* 250 */YY_NO_ANCHOR,
    /* 251 */YY_NO_ANCHOR,
    /* 252 */YY_NO_ANCHOR,
    /* 253 */YY_NO_ANCHOR,
    /* 254 */YY_NO_ANCHOR,
    /* 255 */YY_NO_ANCHOR,
    /* 256 */YY_NO_ANCHOR,
    /* 257 */YY_NO_ANCHOR,
    /* 258 */YY_NO_ANCHOR,
    /* 259 */YY_NO_ANCHOR,
    /* 260 */YY_NO_ANCHOR,
    /* 261 */YY_NO_ANCHOR,
    /* 262 */YY_NO_ANCHOR,
    /* 263 */YY_NO_ANCHOR,
    /* 264 */YY_NO_ANCHOR,
    /* 265 */YY_NO_ANCHOR,
    /* 266 */YY_NO_ANCHOR,
    /* 267 */YY_NO_ANCHOR,
    /* 268 */YY_NO_ANCHOR,
    /* 269 */YY_NO_ANCHOR,
    /* 270 */YY_NO_ANCHOR,
    /* 271 */YY_NO_ANCHOR,
    /* 272 */YY_NO_ANCHOR,
    /* 273 */YY_NO_ANCHOR,
    /* 274 */YY_NO_ANCHOR,
    /* 275 */YY_NO_ANCHOR,
    /* 276 */YY_NO_ANCHOR,
    /* 277 */YY_NO_ANCHOR,
    /* 278 */YY_NO_ANCHOR,
    /* 279 */YY_NO_ANCHOR,
    /* 280 */YY_NO_ANCHOR,
    /* 281 */YY_NO_ANCHOR,
    /* 282 */YY_NO_ANCHOR,
    /* 283 */YY_NO_ANCHOR,
    /* 284 */YY_NO_ANCHOR,
    /* 285 */YY_NO_ANCHOR,
    /* 286 */YY_NO_ANCHOR,
    /* 287 */YY_NO_ANCHOR,
    /* 288 */YY_NO_ANCHOR,
    /* 289 */YY_NO_ANCHOR,
    /* 290 */YY_NO_ANCHOR,
    /* 291 */YY_NO_ANCHOR,
    /* 292 */YY_NO_ANCHOR,
    /* 293 */YY_NO_ANCHOR,
    /* 294 */YY_NO_ANCHOR,
    /* 295 */YY_NO_ANCHOR,
    /* 296 */YY_NO_ANCHOR,
    /* 297 */YY_NO_ANCHOR,
    /* 298 */YY_NO_ANCHOR,
    /* 299 */YY_NO_ANCHOR,
    /* 300 */YY_NO_ANCHOR,
    /* 301 */YY_NO_ANCHOR,
    /* 302 */YY_NO_ANCHOR,
    /* 303 */YY_NO_ANCHOR,
    /* 304 */YY_NO_ANCHOR,
    /* 305 */YY_NO_ANCHOR,
    /* 306 */YY_NO_ANCHOR,
    /* 307 */YY_NO_ANCHOR,
    /* 308 */YY_NO_ANCHOR,
    /* 309 */YY_NO_ANCHOR,
    /* 310 */YY_NO_ANCHOR,
    /* 311 */YY_NO_ANCHOR,
    /* 312 */YY_NO_ANCHOR,
    /* 313 */YY_NO_ANCHOR,
    /* 314 */YY_NO_ANCHOR,
    /* 315 */YY_NO_ANCHOR,
    /* 316 */YY_NO_ANCHOR,
    /* 317 */YY_NO_ANCHOR,
    /* 318 */YY_NO_ANCHOR,
    /* 319 */YY_NO_ANCHOR,
    /* 320 */YY_NO_ANCHOR,
    /* 321 */YY_NO_ANCHOR,
    /* 322 */YY_NO_ANCHOR,
    /* 323 */YY_NO_ANCHOR,
    /* 324 */YY_NO_ANCHOR,
    /* 325 */YY_NO_ANCHOR,
    /* 326 */YY_NO_ANCHOR,
    /* 327 */YY_NO_ANCHOR,
    /* 328 */YY_NO_ANCHOR,
    /* 329 */YY_NO_ANCHOR,
    /* 330 */YY_NO_ANCHOR,
    /* 331 */YY_NO_ANCHOR,
    /* 332 */YY_NO_ANCHOR,
    /* 333 */YY_NO_ANCHOR,
    /* 334 */YY_NO_ANCHOR,
    /* 335 */YY_NO_ANCHOR,
    /* 336 */YY_NO_ANCHOR,
    /* 337 */YY_NO_ANCHOR,
    /* 338 */YY_NO_ANCHOR,
    /* 339 */YY_NO_ANCHOR,
    /* 340 */YY_NO_ANCHOR,
    /* 341 */YY_NO_ANCHOR,
    /* 342 */YY_NO_ANCHOR,
    /* 343 */YY_NO_ANCHOR,
    /* 344 */YY_NO_ANCHOR,
    /* 345 */YY_NO_ANCHOR,
    /* 346 */YY_NO_ANCHOR,
    /* 347 */YY_NO_ANCHOR,
    /* 348 */YY_NO_ANCHOR,
    /* 349 */YY_NO_ANCHOR,
    /* 350 */YY_NO_ANCHOR,
    /* 351 */YY_NO_ANCHOR,
    /* 352 */YY_NO_ANCHOR,
    /* 353 */YY_NO_ANCHOR,
    /* 354 */YY_NO_ANCHOR,
    /* 355 */YY_NO_ANCHOR,
    /* 356 */YY_NO_ANCHOR,
    /* 357 */YY_NO_ANCHOR,
    /* 358 */YY_NO_ANCHOR,
    /* 359 */YY_NO_ANCHOR,
    /* 360 */YY_NO_ANCHOR,
    /* 361 */YY_NO_ANCHOR,
    /* 362 */YY_NO_ANCHOR,
    /* 363 */YY_NO_ANCHOR,
    /* 364 */YY_NO_ANCHOR,
    /* 365 */YY_NO_ANCHOR,
    /* 366 */YY_NO_ANCHOR,
    /* 367 */YY_NO_ANCHOR,
    /* 368 */YY_NO_ANCHOR,
    /* 369 */YY_NO_ANCHOR,
    /* 370 */YY_NO_ANCHOR,
    /* 371 */YY_NO_ANCHOR,
    /* 372 */YY_NO_ANCHOR,
    /* 373 */YY_NO_ANCHOR,
    /* 374 */YY_NO_ANCHOR,
    /* 375 */YY_NO_ANCHOR,
    /* 376 */YY_NO_ANCHOR,
    /* 377 */YY_NO_ANCHOR,
    /* 378 */YY_NO_ANCHOR,
    /* 379 */YY_NO_ANCHOR,
    /* 380 */YY_NO_ANCHOR,
    /* 381 */YY_NO_ANCHOR,
    /* 382 */YY_NO_ANCHOR,
    /* 383 */YY_NO_ANCHOR,
    /* 384 */YY_NO_ANCHOR,
    /* 385 */YY_NO_ANCHOR,
    /* 386 */YY_NO_ANCHOR,
    /* 387 */YY_NO_ANCHOR };

    private int yy_cmap[] = unpackFromString(
            1,
            130,
            "15:9,5,2,15,5,1,15:18,5,15,14,3,15:2,4,15:4,9,15,9,15:2,10:10,15,19,15:5,47"
                    + ":2,31,47,11,47,40,47:4,39,47,12,47:12,7,16,8,15,37,15,13,29,35,28,30,44,20,"
                    + "36,27,46,45,17,26,34,23,24,21,32,25,18,22,33,43,41,38,42,15:5,0,6")[0];

    private int yy_rmap[] = unpackFromString(
            1,
            388,
            "0,1:3,2,3,4,5,1,4,6,4,7,4:5,1:2,8,9,4:3,1:2,4,1:2,4:3,1:2,4:5,10,4:4,11,4:1"
                    + "9,1:4,4:3,1:6,12,1:2,2,13:2,1,14,15,1,16,13,4,13:5,17,18,19,20,21,13,22,13,"
                    + "16:2,23,13,24,25,26,27,28,13,24,29,30,31,32,33,34,35,36,37,38,39,40,41,42,4"
                    + "3,44,45,46,47,48,49,50,51,52,53,54,55,31,22,56,57,58,59,60,36,61,62,63,64,6"
                    + "5,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,9"
                    + "0,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,109,110,11"
                    + "1,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126,127,128,129,1"
                    + "30,131,132,133,134,135,136,137,138,139,140,141,142,143,144,145,146,147,148,"
                    + "149,150,151,152,153,154,155,156,157,158,159,160,161,162,163,164,165,166,167"
                    + ",168,169,170,171,172,173,174,175,176,177,178,179,180,181,182,183,184,185,18"
                    + "6,187,188,189,190,191,192,193,194,195,196,197,198,199,200,201,202,203,204,2"
                    + "05,206,207,208,209,210,211,212,213,214,215,216,217,218,219,220,221,222,223,"
                    + "224,225,226,227,228,229,230,231,232,233,234,235,236,237,238,239,240,241,242"
                    + ",243,244,245,246,247,248,249,250,251,252,253,254,255,256,257,258,259,260,26"
                    + "1,262,263,264,265,266,267,268,269,270,271,272,273,274,275,276,277,278,279,2"
                    + "80,281,282,283,284,285,286,287,288,289,290,291,292,293,294,295,296,297,298")[0];

    private int yy_nxt[][] = unpackFromString(
            299,
            48,
            "1,2,3,4,84,2,1,84:2,98,5,6,189,6,105,84:2,289,6,84,291,6:4,293,6,86,6:3,352"
                    + ",6,354,6,356,6:12,-1:49,7,87,81:3,87,81:41,-1:3,97:3,-1,97:3,240,97:37,-1:1"
                    + "0,6:4,-1:3,6:2,-1,6:28,-1:2,87,-1:55,10,128,-1:18,128,-1:27,6:4,-1:3,6:2,-1"
                    + ",6:7,205,6:20,-1:10,6:4,-1:3,6:2,-1,6:17,334,6:10,-1:10,6:4,-1:3,6:2,-1,6:1"
                    + "7,335,6:10,-1:10,6:4,-1:3,6:2,-1,6:7,273,6:20,-1:10,6:4,-1:3,6,283,-1,6:16,"
                    + "284,6:11,1,82,83,96,103,82,1,79,80,110,85,103,191,103,114,103:2,290,103:2,2"
                    + "92,103:6,118,103:3,353,103,355,103,357,103:12,-1,103:5,-1:3,103:39,-1,103:2"
                    + ",121:3,-1,97:2,121,242,121:37,-1:10,6:4,-1:3,6:2,-1,6:8,9,6:19,-1,104:3,111"
                    + ",104,-1,104:7,8,104,115,104:31,-1,100,107,96:3,87,81:2,96:39,-1:10,10,-1:47"
                    + ",5,-1:47,6:2,90,6,-1:3,6:2,-1,6:28,-1,103,107,103:3,-1:3,103:39,-1:10,102,-"
                    + "1:47,6:4,-1:3,11,6,-1,6:28,-1,114:3,294,114,-1,104:2,114:5,101,114,127,114:"
                    + "31,-1,103:5,-1:3,103,109,130,103:18,130,103:17,-1,103:5,-1:3,103,85,103:37,"
                    + "-1:13,119,-1:3,122,-1:2,122,125,-1:36,6:4,-1:3,6:2,-1,6:16,12,6:11,-1,104:3"
                    + ",111,104,-1,104:7,88,104,115,104:31,-1:10,6:4,-1:3,6:2,-1,6:10,13,6:17,-1,1"
                    + "03:5,-1:3,103,117,103:37,-1,103:5,-1:3,103:19,89,103:19,-1:24,134,-1,137,-1"
                    + ":31,6:4,-1:3,6:2,-1,6:12,14,6:15,-1,103:5,-1:3,103,109,103:37,-1:18,140,-1:"
                    + "39,6:4,-1:3,6:2,-1,6:14,15,6:13,-1,103:5,-1:3,103:3,113,103:35,-1:22,192,-1"
                    + ":35,6:4,-1:3,6,16,-1,6:28,-1,114:3,294,114,-1,104:2,114:5,108,114,127,114:3"
                    + "1,-1:9,143,102,-1:47,6:4,-1:3,6:2,-1,6:5,17,6:22,-1,103:5,-1:3,142,117,103:"
                    + "37,-1:9,143,10,-1:47,6:4,-1:3,6:2,-1,6:10,20,6:17,-1,103:5,-1:3,142,109,103"
                    + ":37,-1:23,146,-1:34,6:4,-1:3,6:2,-1,6:10,21,6:17,-1,103:5,-1:3,103:8,91,103"
                    + ":30,-1:24,140,-1:33,6:4,-1:3,6:2,-1,6:8,22,6:19,-1,103:5,-1:3,103:27,92,103"
                    + ":11,-1:19,104,-1:38,6:4,-1:3,6:2,-1,6:10,23,6:17,-1:10,6:4,-1:3,6:2,-1,6:10"
                    + ",24,6:17,-1,103:5,-1:3,103:23,93,103:15,-1:25,140,-1:32,6:4,-1:3,6:2,-1,6:5"
                    + ",27,6:22,-1,103:5,-1:3,103:25,94,103:13,-1:10,6:4,-1:3,6,30,-1,6:28,-1,103:"
                    + "5,-1:3,103:9,95,103:29,1,2,3,4,84,2,1,18,19,98,5,6,189,6,105,84:2,289,6,84,"
                    + "291,6:4,293,6,86,368,6,255,352,6,354,257,356,6:12,-1:10,6:4,-1:3,6:2,-1,6:1"
                    + "0,31,6:17,1,2,3,4,84,2,1,25,26,98,5,6,189,6,105,84:2,289,6,84,291,6:6,86,6:"
                    + "3,352,6,354,6,356,6:3,386,6:8,-1:10,6:4,-1:3,6:2,-1,6:5,32,6:22,1,2,3,4,84,"
                    + "2,1,28,29,98,5,6,189,6,105,84:2,289,337,84,291,6:4,338,6,86,6:3,352,6,354,6"
                    + ",356,6:3,387,6:8,-1:10,6:4,-1:3,6:2,-1,6:10,41,6:17,1,2,3,4,84,2,1,33,34,98"
                    + ",5,6,189,311,105,84:2,289,266,84,291,6:2,361,312,267,6,243,35,340,341,352,6"
                    + ",342,6,343,36,6,37,268,6,38,39,40,269,6,362,6,-1:10,6:4,-1:3,6:2,-1,6:10,42"
                    + ",6:17,1,2,3,4,84,2,1,65,66,98,5,6,189,6,105,84:2,289,6,84,291,6:3,312,6:2,8"
                    + "6,6:3,352,6,354,6,356,6:12,-1:10,6:4,-1:3,6,43,-1,6:28,1,2,3,4,84,2,1,67,68"
                    + ",98,5,6,189,6,105,84:2,289,6,84,291,6:6,86,6:3,352,6,354,6,356,6:2,69,6:2,7"
                    + "0,71,6:5,-1:10,6:4,-1:3,44,6,-1,6:28,1,2,3,4,84,2,1,72,73,98,5,6,189,6,105,"
                    + "84:2,289,6,84,291,6:6,86,6:3,352,6,354,6,356,6:12,-1:10,6:4,-1:3,6:2,-1,6:2"
                    + "3,45,6:4,1,2,3,4,84,2,1,74,75,98,5,6,189,6,105,84:2,289,6,84,291,6:6,86,6:3"
                    + ",352,6,354,6,356,6:12,-1:10,6:4,-1:3,6,46,-1,6:28,1,2,3,4,84,2,1,76,84,98,5"
                    + ",6,189,6,105,84:2,289,6,84,291,6:6,86,6:3,352,6,354,6,356,6:12,-1:10,6:4,-1"
                    + ":3,6,47,-1,6:28,1,2,3,4,84,2,1,77,84,98,5,6,189,6,105,84:2,289,6,84,291,6:6"
                    + ",86,6:3,352,6,354,6,356,6:12,-1:10,6:4,-1:3,6:2,-1,6:10,48,6:17,-1:10,6:4,-"
                    + "1:3,6:2,-1,6:16,49,6:11,-1:10,6:4,-1:3,6:2,-1,6:12,50,6:15,-1:10,6:4,-1:3,6"
                    + ":2,-1,6:16,51,6:11,-1:10,6:4,-1:3,6:2,-1,6:4,52,6:23,-1:10,6:4,-1:3,6,53,-1"
                    + ",6:28,-1:10,6:4,-1:3,6:2,-1,6:10,54,6:17,-1:10,6:4,-1:3,6:2,-1,6:10,55,6:17"
                    + ",-1:10,6:4,-1:3,6:2,-1,6:10,56,6:17,-1:10,6:4,-1:3,6:2,-1,6:18,57,6:9,-1:10"
                    + ",6:4,-1:3,6:2,-1,6:10,58,6:17,-1:10,6:4,-1:3,59,6,-1,6:28,-1:10,6:4,-1:3,6:"
                    + "2,-1,6:8,60,6:19,-1:10,6:4,-1:3,6:2,-1,6:10,61,6:17,-1:10,6:4,-1:3,6:2,-1,6"
                    + ":8,62,6:19,-1:10,6:4,-1:3,6:2,-1,6:8,63,6:19,-1:10,6:4,-1:3,6:2,-1,6:5,64,6"
                    + ":22,-1:3,97:3,-1,97:3,188,131,97:18,131,97:17,-1:10,6:3,99,-1:3,6:2,-1,6:28"
                    + ",-1,103:2,121:3,-1,97:2,121,190,133,121:18,133,121:17,-1,103:5,-1:3,103:4,1"
                    + "24,103:34,-1:23,149,-1:34,6:4,-1:3,6:2,-1,6:10,106,6:17,-1,103:5,-1:3,103:2"
                    + "1,136,103:17,-1:10,6:4,-1:3,6:2,-1,6:4,112,6:23,-1,103:5,-1:3,103:15,139,10"
                    + "3:23,-1:10,6:4,-1:3,116,6,-1,6:28,-1,103:5,-1:3,103:10,114,103:28,-1:10,6:4"
                    + ",-1:3,6:2,-1,6:3,120,6:24,-1,103:5,-1:3,103:14,145,103:24,-1:10,6:4,-1:3,6:"
                    + "2,-1,6:3,123,6:24,-1,103:5,-1:3,103:14,148,103:24,-1:10,6:4,-1:3,6:2,-1,6:1"
                    + "4,126,6:13,-1,103:5,-1:3,103:25,151,103:13,-1:10,6:4,-1:3,6:2,-1,6:15,129,6"
                    + ":12,-1:10,6:4,-1:3,6:2,-1,132,6:27,-1:10,6:4,-1:3,6:2,-1,6:8,135,6:19,-1:10"
                    + ",6:4,-1:3,6:2,-1,6:10,138,6:17,-1:10,6:4,-1:3,141,6,-1,6:28,-1:10,6:4,-1:3,"
                    + "144,6,-1,6:28,-1:10,6:4,-1:3,6:2,-1,6:15,147,6:12,-1:10,6:4,-1:3,6:2,-1,6:1"
                    + "0,150,6:17,-1:10,6:4,-1:3,6:2,-1,6:15,153,6:12,-1:10,6:4,-1:3,6:2,-1,6:15,1"
                    + "55,6:12,-1:10,6:4,-1:3,6:2,-1,6:4,157,6:23,-1:10,6:3,222,-1:3,6:2,-1,6:7,31"
                    + "8,6:10,197,6:9,-1:10,6:4,-1:3,6:2,-1,6:14,159,6:13,-1:10,6:4,-1:3,6:2,-1,6:"
                    + "12,373,6,161,6:13,-1:10,6:4,-1:3,163,6,-1,6:28,-1:10,6:4,-1:3,6:2,-1,6:3,16"
                    + "5,6:24,-1:10,6:4,-1:3,6:2,-1,6:14,167,6:13,-1:10,6:4,-1:3,6:2,-1,6:12,169,6"
                    + ":15,-1:10,6:4,-1:3,6:2,-1,171,6:27,-1:10,6:4,-1:3,6,172,-1,6:28,-1:10,6:4,-"
                    + "1:3,6:2,-1,6:3,173,6:24,-1:10,6:4,-1:3,6,174,-1,6:28,-1:10,6:3,175,-1:3,6:2"
                    + ",-1,6:28,-1:10,6:4,-1:3,6:2,-1,6:14,176,6:13,-1:10,6:4,-1:3,6:2,-1,6:14,177"
                    + ",6:13,-1:10,6:4,-1:3,178,6,-1,6:28,-1:10,6:4,-1:3,179,6,-1,6:28,-1:10,6:4,-"
                    + "1:3,6:2,-1,6:24,180,6:3,-1:10,6:4,-1:3,181,6,-1,6:28,-1:10,6:4,-1:3,6:2,-1,"
                    + "6:7,182,6:20,-1:10,6:3,183,-1:3,6:2,-1,6:28,-1:10,6:4,-1:3,184,6,-1,6:28,-1"
                    + ":10,6:4,-1:3,6:2,-1,6:14,185,6:13,-1:10,6:4,-1:3,6:2,-1,6:14,186,6:13,-1:10"
                    + ",6:4,-1:3,6:2,-1,6:4,187,6:23,-1:3,97:3,-1,97:3,188,97:37,-1:10,6:4,-1:3,6:"
                    + "2,-1,6:9,193,6:18,-1,103:2,121:3,-1,97:2,121,190,121:37,-1:10,6:4,-1:3,6:2,"
                    + "-1,6:6,272,6,9,6:19,-1,103:5,-1:3,103:20,194,103:18,-1:10,6:3,195,-1:3,6:2,"
                    + "-1,6:28,-1,103:5,-1:3,103:4,196,103:34,-1:10,6:4,-1:3,6:2,-1,6:18,197,6:9,-"
                    + "1,103:5,-1:3,103:9,198,103:29,-1:10,6:4,-1:3,6,199,-1,6:28,-1,103:5,-1:3,10"
                    + "3:15,198,103:23,-1:10,6:4,-1:3,6:2,-1,6:7,201,6:20,-1,103:5,-1:3,103:9,200,"
                    + "103:29,-1:10,6:4,-1:3,6:2,-1,6:10,203,6:17,-1,103:5,-1:3,103:18,202,103:20,"
                    + "-1:10,6:4,-1:3,6:2,-1,6:8,206,6:19,-1,103:5,-1:3,103:21,204,103:17,-1:10,6:"
                    + "4,-1:3,6:2,-1,6:3,207,6:24,-1,103:5,-1:3,103:16,198,103:22,-1:10,6:4,-1:3,6"
                    + ",208,-1,6:28,-1:10,6:4,-1:3,6:2,-1,6:18,209,6:9,-1:10,6:4,-1:3,6:2,-1,6:18,"
                    + "210,6:9,-1:10,6:4,-1:3,6:2,-1,6:7,211,6:20,-1:10,6:4,-1:3,6:2,-1,212,6:27,-"
                    + "1:10,6:4,-1:3,6:2,-1,6:12,213,6:15,-1:10,6:4,-1:3,6:2,-1,6:7,214,6:20,-1:10"
                    + ",6:4,-1:3,6:2,-1,6:18,215,6:9,-1:10,6:4,-1:3,6,216,-1,6:4,381,6,314,6:21,-1"
                    + ":10,6:4,-1:3,6:2,-1,6:7,217,6:20,-1:10,6:4,-1:3,6:2,-1,6:3,218,6:3,219,6:20"
                    + ",-1:10,6:4,-1:3,6:2,-1,6:12,220,6:15,-1:10,6:4,-1:3,6:2,-1,6:7,221,6:20,-1:"
                    + "10,6:3,223,-1:3,6:2,-1,6:28,-1:10,6:4,-1:3,6:2,-1,6:8,224,6:19,-1:10,6:4,-1"
                    + ":3,6:2,-1,6:16,225,6:11,-1:10,6:4,-1:3,6:2,-1,6:3,226,6:24,-1:10,6:4,-1:3,6"
                    + ":2,-1,6:6,227,6:21,-1:10,6:4,-1:3,6:2,-1,6:10,228,6:17,-1:10,6:4,-1:3,6:2,-"
                    + "1,6:7,229,6:20,-1:10,6:4,-1:3,6:2,-1,6:4,230,6:23,-1:10,6:4,-1:3,6:2,-1,6:9"
                    + ",231,6:18,-1:10,6:4,-1:3,6:2,-1,6:7,232,6:20,-1:10,6:4,-1:3,6:2,-1,6:18,233"
                    + ",6:9,-1:10,6:3,234,-1:3,6:2,-1,6:28,-1:10,6:4,-1:3,6:2,-1,6:10,235,6:17,-1:"
                    + "10,6:4,-1:3,6:2,-1,6:18,236,6:9,-1:10,6:4,-1:3,6:2,-1,6:2,237,6:25,-1:10,6:"
                    + "4,-1:3,6:2,-1,6:2,238,6:25,-1:10,6:4,-1:3,6:2,-1,6:10,239,6:17,-1:10,6:3,24"
                    + "1,-1:3,6:2,-1,6:28,-1,103:5,-1:3,103:4,244,103:34,-1:10,6:4,-1:3,6:2,-1,6:1"
                    + "2,245,6:15,-1,103:5,-1:3,103:23,246,103:15,-1:10,6:4,-1:3,6,247,-1,6:28,-1,"
                    + "103:5,-1:3,103:4,296,103:3,248,103:2,248,333,103:26,-1:10,6:3,249,-1:3,6:2,"
                    + "-1,6:28,-1,103:5,-1:3,103:15,304,103,250,103:21,-1:10,6:4,-1:3,6:2,-1,6:5,2"
                    + "51,6:22,-1,103:5,-1:3,103:4,252,103:34,-1:10,6:4,-1:3,6:2,-1,6:6,253,6:21,-"
                    + "1,103:5,-1:3,103:16,254,103:22,-1:10,6:4,-1:3,6:2,-1,6:15,259,6:12,-1,103:5"
                    + ",-1:3,103:17,256,103:21,-1:10,6:4,-1:3,6,260,-1,6:28,-1,103:5,-1:3,103:14,2"
                    + "58,103:24,-1:10,6:4,-1:3,6,261,-1,6:28,-1,103:5,-1:3,103:14,248,103:24,-1:1"
                    + "0,6:4,-1:3,6:2,-1,6:16,262,6:11,-1:10,6:4,-1:3,6:2,-1,6:12,263,6:15,-1:10,6"
                    + ":4,-1:3,6:2,-1,6:2,264,6:25,-1:10,6:4,-1:3,6:2,-1,6:16,265,6:11,-1:10,6:4,-"
                    + "1:3,6:2,-1,6:12,270,6,313,6:13,-1:10,6:4,-1:3,6:2,-1,6:3,271,6:24,-1:10,6:4"
                    + ",-1:3,6:2,-1,6:15,274,6:12,-1:10,6:4,-1:3,6:2,-1,6:3,275,6:24,-1:10,6:4,-1:"
                    + "3,6,276,-1,6:28,-1:10,6:4,-1:3,6,277,-1,6:28,-1:10,6:4,-1:3,278,6,-1,6:28,-"
                    + "1:10,6:4,-1:3,6:2,-1,6:4,279,6:23,-1:10,6:4,-1:3,6:2,-1,6:7,280,6:20,-1:10,"
                    + "6:4,-1:3,6,281,-1,6:28,-1:10,6:4,-1:3,6,282,-1,6:28,-1:10,6:4,-1:3,6,285,-1"
                    + ",6:28,-1:10,6:4,-1:3,6:2,-1,6:3,286,6:24,-1:10,6:4,-1:3,6:2,-1,6:3,287,6:24"
                    + ",-1:10,6:4,-1:3,6,288,-1,6:28,-1:10,6:4,-1:3,6:2,-1,6:10,295,6:17,-1,103:5,"
                    + "-1:3,103:21,298,103:17,-1:10,6:4,-1:3,6:2,-1,6:12,297,6:15,-1,103:5,-1:3,10"
                    + "3:23,300,103:15,-1:10,6:4,-1:3,6:2,-1,6:6,299,6:21,-1,103:5,-1:3,103:17,302"
                    + ",103:21,-1:10,6:4,-1:3,6:2,-1,6:10,301,6:17,-1,103:5,-1:3,103:13,306,103:25"
                    + ",-1:10,6:4,-1:3,6:2,-1,6:5,303,6:22,-1:10,6:4,-1:3,6:2,-1,6:5,305,6:22,-1:1"
                    + "0,6:4,-1:3,6:2,-1,6:4,307,6:23,-1:10,6:3,308,-1:3,6:2,-1,6:28,-1:10,6:4,-1:"
                    + "3,6:2,-1,6:3,309,6:24,-1:10,6:4,-1:3,6:2,-1,6:4,310,6:23,-1:10,6:3,377,-1:3"
                    + ",6:2,-1,6:7,315,6:20,-1:10,6:4,-1:3,6:2,-1,6:21,316,6:6,-1:10,6:4,-1:3,6:2,"
                    + "-1,6:7,345,6:2,328,6:17,-1:10,6:3,363,-1:3,6:2,-1,6:3,330,6:24,-1:10,6:4,-1"
                    + ":3,6,317,-1,6:28,-1:10,6:4,-1:3,6:2,-1,6:5,319,6:22,-1:10,6:4,-1:3,6:2,-1,6"
                    + ":5,320,6:22,-1:10,6:4,-1:3,6:2,-1,6:5,321,6:22,-1:10,6:4,-1:3,6:2,-1,6:5,32"
                    + "2,6:22,-1:10,6:4,-1:3,6:2,-1,6:12,323,6:15,-1:10,6:4,-1:3,6:2,-1,6:12,324,6"
                    + ":15,-1:10,6:4,-1:3,6:2,-1,6:5,325,6:22,-1:10,6:4,-1:3,6:2,-1,6:12,326,6:15,"
                    + "-1,103:5,-1:3,103:23,327,103:15,-1:10,6:4,-1:3,6:2,-1,6:10,328,6:17,-1,103:"
                    + "5,-1:3,103:21,329,103:17,-1:10,6:4,-1:3,6:2,-1,6:3,330,6:24,-1,103:5,-1:3,1"
                    + "03:14,331,103:24,-1:10,6:4,-1:3,6:2,-1,6:12,332,6:15,-1:10,6:3,336,-1:3,6:2"
                    + ",-1,6:28,-1:10,6:3,339,-1:3,6:2,-1,6:28,-1:10,6:4,-1:3,6:2,-1,6:2,344,6:25,"
                    + "-1:10,6:4,-1:3,6:2,-1,6:2,346,371,6:24,-1:10,6:4,-1:3,6:2,-1,6:4,347,6:23,-"
                    + "1:10,6:4,-1:3,6:2,-1,6:14,348,6:13,-1:10,6:4,-1:3,6:2,-1,349,6:27,-1:10,6:4"
                    + ",-1:3,6:2,-1,350,6:27,-1:10,6:4,-1:3,6:2,-1,6:10,351,6:17,-1:10,6:4,-1:3,6:"
                    + "2,-1,6:7,358,6:20,-1:10,6:4,-1:3,6:2,-1,6:12,359,6:15,-1:10,6:4,-1:3,6:2,-1"
                    + ",6:12,360,6:15,-1:10,6:4,-1:3,6:2,-1,6:7,364,6:20,-1:10,6:4,-1:3,6:2,-1,6:2"
                    + "5,365,6:2,-1:10,6:4,-1:3,6:2,-1,6:10,366,6:17,-1:10,6:4,-1:3,6:2,-1,6:14,36"
                    + "7,6:13,-1:10,6:4,-1:3,6:2,-1,6:20,369,6:7,-1:10,6:4,-1:3,6:2,-1,6:20,370,6:"
                    + "7,-1:10,6:4,-1:3,6:2,-1,6:15,372,6:12,-1:10,6:4,-1:3,6:2,-1,6:7,374,6:20,-1"
                    + ":10,6:4,-1:3,375,6,-1,6:28,-1:10,6:4,-1:3,376,6,-1,6:28,-1:10,6:4,-1:3,378,"
                    + "6,-1,6:28,-1:10,6:4,-1:3,6:2,-1,6:10,379,6:17,-1:10,6:4,-1:3,6:2,-1,6:10,38"
                    + "0,6:17,-1:10,6:4,-1:3,6:2,-1,6:9,382,6:18,-1:10,6:4,-1:3,6:2,-1,6:9,383,6:1"
                    + "8,-1:10,6:3,384,-1:3,6:2,-1,6:28,-1:10,6:3,385,-1:3,6:2,-1,6:28");

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
                    case 2: { /* ignore white space */
                    }
                    case -3:
                        break;
                    case 3: { /* System.out.println(); */
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
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -7:
                        break;
                    case 7: {
                        System.out.println(yytext()); /* ignore comments */
                    }
                    case -8:
                        break;
                    case 8: {
                        return new Symbol(sym.STRING, yytext().substring(1,
                                yytext().length() - 1));
                    }
                    case -9:
                        break;
                    case 9: {
                        return new Symbol(sym.ID);
                    }
                    case -10:
                        break;
                    case 10: {
                        return new Symbol(sym.REAL, new Double(yytext()));
                    }
                    case -11:
                        break;
                    case 11: {
                        return new Symbol(sym.LABEL);
                    }
                    case -12:
                        break;
                    case 12: {
                        yybegin(GRAPH);
                        return new Symbol(sym.GRAPH);
                    }
                    case -13:
                        break;
                    case 13: {
                        return new Symbol(sym.GRAPHICS_STYLE);
                    }
                    case -14:
                        break;
                    case 14: {
                        return new Symbol(sym.CREATOR);
                    }
                    case -15:
                        break;
                    case 15: {
                        return new Symbol(sym.VERSION);
                    }
                    case -16:
                        break;
                    case 16: {
                        return new Symbol(sym.COMMENT);
                    }
                    case -17:
                        break;
                    case 17: {
                        stateBeforeGraphics = yy_lexical_state;
                        yybegin(GRAPHICS);
                        // System.out.println("\nentering graphics state.");
                        return new Symbol(sym.GRAPHICS);
                    }
                    case -18:
                        break;
                    case 18: {
                        return new Symbol(sym.SBRACE);
                    }
                    case -19:
                        break;
                    case 19: {
                        return new Symbol(sym.CBRACE);
                    }
                    case -20:
                        break;
                    case 20: {
                        yybegin(EDGE);
                        return new Symbol(sym.EDGE);
                    }
                    case -21:
                        break;
                    case 21: { /* System.out.println(" node"); */
                        yybegin(NODE);
                        return new Symbol(sym.NODE);
                    }
                    case -22:
                        break;
                    case 22: {
                        return new Symbol(sym.DIRECTED);
                    }
                    case -23:
                        break;
                    case 23: {
                        yybegin(EDGESTYLE);
                        // return new Symbol(sym.EDGE_STYLE);
                    }
                    case -24:
                        break;
                    case 24: {
                        yybegin(NODESTYLE);
                        // return new Symbol(sym.NODE_STYLE); }
                    }
                    case -25:
                        break;
                    case 25: {
                        nodeCount++;
                        return new Symbol(sym.SBRACE);
                    }
                    case -26:
                        break;
                    case 26: {
                        nodeCount--;
                        if (nodeCount == 0) {
                            yybegin(GRAPH);
                        }
                        return new Symbol(sym.CBRACE);
                    }
                    case -27:
                        break;
                    case 27: { // ignore for now
                        yybegin(NODELABELGRAPHICS);
                    }
                    case -28:
                        break;
                    case 28: {
                        edgeCount++;
                        return new Symbol(sym.SBRACE);
                    }
                    case -29:
                        break;
                    case 29: {
                        edgeCount--;
                        if (edgeCount == 0) {
                            yybegin(GRAPH);
                        }
                        return new Symbol(sym.CBRACE);
                    }
                    case -30:
                        break;
                    case 30: {
                        return new Symbol(sym.TARGET);
                    }
                    case -31:
                        break;
                    case 31: {
                        return new Symbol(sym.SOURCE);
                    }
                    case -32:
                        break;
                    case 32: { // ignore for now
                        yybegin(EDGELABELGRAPHICS);
                    }
                    case -33:
                        break;
                    case 33: {
                        graphicsCount++;
                        return new Symbol(sym.SBRACE);
                    }
                    case -34:
                        break;
                    case 34: {
                        graphicsCount--;
                        if (graphicsCount == 0) {
                            yybegin(stateBeforeGraphics);
                            // System.out.println("\nleaving graphics ("
                            // + stateBeforeGraphics
                            // + ").");
                        }
                        return new Symbol(sym.CBRACE);
                    }
                    case -35:
                        break;
                    case 35: {
                        return new Symbol(sym.GRAPHICS_D);
                    }
                    case -36:
                        break;
                    case 36: {
                        return new Symbol(sym.GRAPHICS_H);
                    }
                    case -37:
                        break;
                    case 37: {
                        return new Symbol(sym.GRAPHICS_Y);
                    }
                    case -38:
                        break;
                    case 38: {
                        return new Symbol(sym.GRAPHICS_X);
                    }
                    case -39:
                        break;
                    case 39: {
                        return new Symbol(sym.GRAPHICS_Z);
                    }
                    case -40:
                        break;
                    case 40: {
                        return new Symbol(sym.GRAPHICS_W);
                    }
                    case -41:
                        break;
                    case 41: {
                        return new Symbol(sym.GRAPHICS_TYPE);
                    }
                    case -42:
                        break;
                    case 42: {
                        stateBeforeLine = yy_lexical_state;
                        yybegin(LINE);
                        return new Symbol(sym.GRAPHICS_LINE);
                    }
                    case -43:
                        break;
                    case 43: {
                        return new Symbol(sym.GRAPHICS_FONT);
                    }
                    case -44:
                        break;
                    case 44: {
                        return new Symbol(sym.GRAPHICS_FILL);
                    }
                    case -45:
                        break;
                    case 45: {
                        return new Symbol(sym.GRAPHICS_ARROW);
                    }
                    case -46:
                        break;
                    case 46: {
                        stateBeforePoint = yy_lexical_state;
                        yybegin(POINT);
                        return new Symbol(sym.GRAPHICS_POINT);
                    }
                    case -47:
                        break;
                    case 47: {
                        return new Symbol(sym.GRAPHICS_START);
                    }
                    case -48:
                        break;
                    case 48: {
                        return new Symbol(sym.GRAPHICS_IMAGE);
                    }
                    case -49:
                        break;
                    case 49: {
                        return new Symbol(sym.GRAPHICS_WIDTH);
                    }
                    case -50:
                        break;
                    case 50: {
                        return new Symbol(sym.GRAPHICS_ANCHOR);
                    }
                    case -51:
                        break;
                    case 51: {
                        return new Symbol(sym.GRAPHICS_SMOOTH);
                    }
                    case -52:
                        break;
                    case 52: {
                        return new Symbol(sym.GRAPHICS_BITMAP);
                    }
                    case -53:
                        break;
                    case 53: {
                        return new Symbol(sym.GRAPHICS_EXTENT);
                    }
                    case -54:
                        break;
                    case 54: {
                        return new Symbol(sym.GRAPHICS_OUTLINE);
                    }
                    case -55:
                        break;
                    case 55: {
                        return new Symbol(sym.GRAPHICS_STIPPLE);
                    }
                    case -56:
                        break;
                    case 56: {
                        return new Symbol(sym.GRAPHICS_VISIBLE);
                    }
                    case -57:
                        break;
                    case 57: {
                        return new Symbol(sym.GRAPHICS_JUSTIFY);
                    }
                    case -58:
                        break;
                    case 58: {
                        return new Symbol(sym.GRAPHICS_CAPSTYLE);
                    }
                    case -59:
                        break;
                    case 59: {
                        return new Symbol(sym.GRAPHICS_ARROW_TAIL);
                    }
                    case -60:
                        break;
                    case 60: {
                        return new Symbol(sym.GRAPHICS_ARROW_HEAD);
                    }
                    case -61:
                        break;
                    case 61: {
                        return new Symbol(sym.GRAPHICS_JOINSTYLE);
                    }
                    case -62:
                        break;
                    case 62: {
                        return new Symbol(sym.GRAPHICS_BACKGROUND);
                    }
                    case -63:
                        break;
                    case 63: {
                        return new Symbol(sym.GRAPHICS_FOREGROUND);
                    }
                    case -64:
                        break;
                    case 64: {
                        return new Symbol(sym.GRAPHICS_SPLINESTEPS);
                    }
                    case -65:
                        break;
                    case 65: {
                        lineCount++;
                        return new Symbol(sym.SBRACE);
                    }
                    case -66:
                        break;
                    case 66: {
                        lineCount--;
                        if (lineCount == 0) {
                            yybegin(stateBeforeLine);
                        }
                        return new Symbol(sym.CBRACE);
                    }
                    case -67:
                        break;
                    case 67: {
                        pointCount++;
                        return new Symbol(sym.SBRACE);
                    }
                    case -68:
                        break;
                    case 68: {
                        pointCount--;
                        if (pointCount == 0) {
                            yybegin(stateBeforePoint);
                        }
                        return new Symbol(sym.CBRACE);
                    }
                    case -69:
                        break;
                    case 69: {
                        return new Symbol(sym.POINT_Y);
                    }
                    case -70:
                        break;
                    case 70: {
                        return new Symbol(sym.POINT_X);
                    }
                    case -71:
                        break;
                    case 71: {
                        return new Symbol(sym.POINT_Z);
                    }
                    case -72:
                        break;
                    case 72: {
                    }
                    case -73:
                        break;
                    case 73: {
                        yybegin(NODE);
                    }
                    case -74:
                        break;
                    case 74: {
                    }
                    case -75:
                        break;
                    case 75: {
                        yybegin(EDGE);
                    }
                    case -76:
                        break;
                    case 76: {
                        ignoreCount = 1;
                        System.out.println("in nodestyle sbrace");
                        yybegin(EMPTY);
                        // return new Symbol(sym.SBRACE);}
                    }
                    case -77:
                        break;
                    case 77: {
                        ignoreCount = 1;
                        System.out.println("in edgestyle sbrace");
                        yybegin(EMPTY);
                        // return new Symbol(sym.SBRACE);}
                    }
                    case -78:
                        break;
                    case 78: {
                        System.out.println("in empty empty");
                    }
                    case -79:
                        break;
                    case 79: {
                        ignoreCount += 1;
                        System.out.println("in empty sbrace");
                        // yybegin(EMPTY);
                        // return new Symbol(sym.SBRACE); }
                    }
                    case -80:
                        break;
                    case 80: {
                        ignoreCount -= 1;
                        if (ignoreCount == 0) {
                            yybegin(GRAPH);
                            System.out.println("in empty cbrace, begin graph");
                            // return new Symbol(sym.CBRACE);
                        } else {
                            System.out.println("in empty cbrace");
                        }
                        ;
                        // return new Symbol(sym.CBRACE);}
                    }
                    case -81:
                        break;
                    case 82: { /* ignore white space */
                    }
                    case -82:
                        break;
                    case 83: { /* System.out.println(); */
                    }
                    case -83:
                        break;
                    case 84: {
                        System.out.println("\nUnmatched input: " + yytext()
                                + " in line " + (yyline + 1));
                    }
                    case -84:
                        break;
                    case 85: {
                        return new Symbol(sym.INTEGER, new Integer(yytext()));
                    }
                    case -85:
                        break;
                    case 86: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -86:
                        break;
                    case 87: {
                        System.out.println(yytext()); /* ignore comments */
                    }
                    case -87:
                        break;
                    case 88: {
                        return new Symbol(sym.STRING, yytext().substring(1,
                                yytext().length() - 1));
                    }
                    case -88:
                        break;
                    case 89: {
                        return new Symbol(sym.ID);
                    }
                    case -89:
                        break;
                    case 90: {
                        return new Symbol(sym.REAL, new Double(yytext()));
                    }
                    case -90:
                        break;
                    case 91: {
                        return new Symbol(sym.LABEL);
                    }
                    case -91:
                        break;
                    case 92: {
                        yybegin(GRAPH);
                        return new Symbol(sym.GRAPH);
                    }
                    case -92:
                        break;
                    case 93: {
                        return new Symbol(sym.CREATOR);
                    }
                    case -93:
                        break;
                    case 94: {
                        return new Symbol(sym.VERSION);
                    }
                    case -94:
                        break;
                    case 95: {
                        return new Symbol(sym.COMMENT);
                    }
                    case -95:
                        break;
                    case 96: {
                        System.out.println("in empty empty");
                    }
                    case -96:
                        break;
                    case 98: {
                        System.out.println("\nUnmatched input: " + yytext()
                                + " in line " + (yyline + 1));
                    }
                    case -97:
                        break;
                    case 99: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -98:
                        break;
                    case 100: {
                        System.out.println(yytext()); /* ignore comments */
                    }
                    case -99:
                        break;
                    case 101: {
                        return new Symbol(sym.STRING, yytext().substring(1,
                                yytext().length() - 1));
                    }
                    case -100:
                        break;
                    case 102: {
                        return new Symbol(sym.REAL, new Double(yytext()));
                    }
                    case -101:
                        break;
                    case 103: {
                        System.out.println("in empty empty");
                    }
                    case -102:
                        break;
                    case 105: {
                        System.out.println("\nUnmatched input: " + yytext()
                                + " in line " + (yyline + 1));
                    }
                    case -103:
                        break;
                    case 106: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -104:
                        break;
                    case 107: {
                        System.out.println(yytext()); /* ignore comments */
                    }
                    case -105:
                        break;
                    case 108: {
                        return new Symbol(sym.STRING, yytext().substring(1,
                                yytext().length() - 1));
                    }
                    case -106:
                        break;
                    case 109: {
                        return new Symbol(sym.REAL, new Double(yytext()));
                    }
                    case -107:
                        break;
                    case 110: {
                        System.out.println("in empty empty");
                    }
                    case -108:
                        break;
                    case 112: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -109:
                        break;
                    case 113: {
                        return new Symbol(sym.REAL, new Double(yytext()));
                    }
                    case -110:
                        break;
                    case 114: {
                        System.out.println("in empty empty");
                    }
                    case -111:
                        break;
                    case 116: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -112:
                        break;
                    case 117: {
                        return new Symbol(sym.REAL, new Double(yytext()));
                    }
                    case -113:
                        break;
                    case 118: {
                        System.out.println("in empty empty");
                    }
                    case -114:
                        break;
                    case 120: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -115:
                        break;
                    case 121: {
                        System.out.println("in empty empty");
                    }
                    case -116:
                        break;
                    case 123: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -117:
                        break;
                    case 124: {
                        System.out.println("in empty empty");
                    }
                    case -118:
                        break;
                    case 126: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -119:
                        break;
                    case 127: {
                        System.out.println("in empty empty");
                    }
                    case -120:
                        break;
                    case 129: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -121:
                        break;
                    case 130: {
                        System.out.println("in empty empty");
                    }
                    case -122:
                        break;
                    case 132: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -123:
                        break;
                    case 133: {
                        System.out.println("in empty empty");
                    }
                    case -124:
                        break;
                    case 135: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -125:
                        break;
                    case 136: {
                        System.out.println("in empty empty");
                    }
                    case -126:
                        break;
                    case 138: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -127:
                        break;
                    case 139: {
                        System.out.println("in empty empty");
                    }
                    case -128:
                        break;
                    case 141: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -129:
                        break;
                    case 142: {
                        System.out.println("in empty empty");
                    }
                    case -130:
                        break;
                    case 144: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -131:
                        break;
                    case 145: {
                        System.out.println("in empty empty");
                    }
                    case -132:
                        break;
                    case 147: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -133:
                        break;
                    case 148: {
                        System.out.println("in empty empty");
                    }
                    case -134:
                        break;
                    case 150: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -135:
                        break;
                    case 151: {
                        System.out.println("in empty empty");
                    }
                    case -136:
                        break;
                    case 153: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -137:
                        break;
                    case 155: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -138:
                        break;
                    case 157: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -139:
                        break;
                    case 159: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -140:
                        break;
                    case 161: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -141:
                        break;
                    case 163: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -142:
                        break;
                    case 165: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -143:
                        break;
                    case 167: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -144:
                        break;
                    case 169: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -145:
                        break;
                    case 171: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -146:
                        break;
                    case 172: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -147:
                        break;
                    case 173: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -148:
                        break;
                    case 174: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -149:
                        break;
                    case 175: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -150:
                        break;
                    case 176: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -151:
                        break;
                    case 177: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -152:
                        break;
                    case 178: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -153:
                        break;
                    case 179: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -154:
                        break;
                    case 180: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -155:
                        break;
                    case 181: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -156:
                        break;
                    case 182: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -157:
                        break;
                    case 183: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -158:
                        break;
                    case 184: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -159:
                        break;
                    case 185: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -160:
                        break;
                    case 186: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -161:
                        break;
                    case 187: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -162:
                        break;
                    case 188: {
                        return new Symbol(sym.INTEGER, new Integer(yytext()));
                    }
                    case -163:
                        break;
                    case 189: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -164:
                        break;
                    case 190: {
                        return new Symbol(sym.INTEGER, new Integer(yytext()));
                    }
                    case -165:
                        break;
                    case 191: {
                        System.out.println("in empty empty");
                    }
                    case -166:
                        break;
                    case 193: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -167:
                        break;
                    case 194: {
                        System.out.println("in empty empty");
                    }
                    case -168:
                        break;
                    case 195: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -169:
                        break;
                    case 196: {
                        System.out.println("in empty empty");
                    }
                    case -170:
                        break;
                    case 197: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -171:
                        break;
                    case 198: {
                        System.out.println("in empty empty");
                    }
                    case -172:
                        break;
                    case 199: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -173:
                        break;
                    case 200: {
                        System.out.println("in empty empty");
                    }
                    case -174:
                        break;
                    case 201: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -175:
                        break;
                    case 202: {
                        System.out.println("in empty empty");
                    }
                    case -176:
                        break;
                    case 203: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -177:
                        break;
                    case 204: {
                        System.out.println("in empty empty");
                    }
                    case -178:
                        break;
                    case 205: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -179:
                        break;
                    case 206: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -180:
                        break;
                    case 207: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -181:
                        break;
                    case 208: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -182:
                        break;
                    case 209: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -183:
                        break;
                    case 210: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -184:
                        break;
                    case 211: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -185:
                        break;
                    case 212: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -186:
                        break;
                    case 213: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -187:
                        break;
                    case 214: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -188:
                        break;
                    case 215: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -189:
                        break;
                    case 216: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -190:
                        break;
                    case 217: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -191:
                        break;
                    case 218: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -192:
                        break;
                    case 219: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -193:
                        break;
                    case 220: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -194:
                        break;
                    case 221: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -195:
                        break;
                    case 222: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -196:
                        break;
                    case 223: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -197:
                        break;
                    case 224: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -198:
                        break;
                    case 225: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -199:
                        break;
                    case 226: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -200:
                        break;
                    case 227: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -201:
                        break;
                    case 228: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -202:
                        break;
                    case 229: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -203:
                        break;
                    case 230: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -204:
                        break;
                    case 231: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -205:
                        break;
                    case 232: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -206:
                        break;
                    case 233: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -207:
                        break;
                    case 234: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -208:
                        break;
                    case 235: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -209:
                        break;
                    case 236: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -210:
                        break;
                    case 237: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -211:
                        break;
                    case 238: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -212:
                        break;
                    case 239: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -213:
                        break;
                    case 240: {
                        return new Symbol(sym.INTEGER, new Integer(yytext()));
                    }
                    case -214:
                        break;
                    case 241: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -215:
                        break;
                    case 242: {
                        return new Symbol(sym.INTEGER, new Integer(yytext()));
                    }
                    case -216:
                        break;
                    case 243: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -217:
                        break;
                    case 244: {
                        System.out.println("in empty empty");
                    }
                    case -218:
                        break;
                    case 245: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -219:
                        break;
                    case 246: {
                        System.out.println("in empty empty");
                    }
                    case -220:
                        break;
                    case 247: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -221:
                        break;
                    case 248: {
                        System.out.println("in empty empty");
                    }
                    case -222:
                        break;
                    case 249: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -223:
                        break;
                    case 250: {
                        System.out.println("in empty empty");
                    }
                    case -224:
                        break;
                    case 251: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -225:
                        break;
                    case 252: {
                        System.out.println("in empty empty");
                    }
                    case -226:
                        break;
                    case 253: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -227:
                        break;
                    case 254: {
                        System.out.println("in empty empty");
                    }
                    case -228:
                        break;
                    case 255: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -229:
                        break;
                    case 256: {
                        System.out.println("in empty empty");
                    }
                    case -230:
                        break;
                    case 257: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -231:
                        break;
                    case 258: {
                        System.out.println("in empty empty");
                    }
                    case -232:
                        break;
                    case 259: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -233:
                        break;
                    case 260: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -234:
                        break;
                    case 261: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -235:
                        break;
                    case 262: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -236:
                        break;
                    case 263: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -237:
                        break;
                    case 264: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -238:
                        break;
                    case 265: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -239:
                        break;
                    case 266: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -240:
                        break;
                    case 267: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -241:
                        break;
                    case 268: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -242:
                        break;
                    case 269: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -243:
                        break;
                    case 270: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -244:
                        break;
                    case 271: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -245:
                        break;
                    case 272: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -246:
                        break;
                    case 273: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -247:
                        break;
                    case 274: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -248:
                        break;
                    case 275: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -249:
                        break;
                    case 276: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -250:
                        break;
                    case 277: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -251:
                        break;
                    case 278: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -252:
                        break;
                    case 279: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -253:
                        break;
                    case 280: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -254:
                        break;
                    case 281: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -255:
                        break;
                    case 282: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -256:
                        break;
                    case 283: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -257:
                        break;
                    case 284: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -258:
                        break;
                    case 285: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -259:
                        break;
                    case 286: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -260:
                        break;
                    case 287: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -261:
                        break;
                    case 288: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -262:
                        break;
                    case 289: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -263:
                        break;
                    case 290: {
                        System.out.println("in empty empty");
                    }
                    case -264:
                        break;
                    case 291: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -265:
                        break;
                    case 292: {
                        System.out.println("in empty empty");
                    }
                    case -266:
                        break;
                    case 293: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -267:
                        break;
                    case 294: {
                        System.out.println("in empty empty");
                    }
                    case -268:
                        break;
                    case 295: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -269:
                        break;
                    case 296: {
                        System.out.println("in empty empty");
                    }
                    case -270:
                        break;
                    case 297: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -271:
                        break;
                    case 298: {
                        System.out.println("in empty empty");
                    }
                    case -272:
                        break;
                    case 299: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -273:
                        break;
                    case 300: {
                        System.out.println("in empty empty");
                    }
                    case -274:
                        break;
                    case 301: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -275:
                        break;
                    case 302: {
                        System.out.println("in empty empty");
                    }
                    case -276:
                        break;
                    case 303: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -277:
                        break;
                    case 304: {
                        System.out.println("in empty empty");
                    }
                    case -278:
                        break;
                    case 305: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -279:
                        break;
                    case 306: {
                        System.out.println("in empty empty");
                    }
                    case -280:
                        break;
                    case 307: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -281:
                        break;
                    case 308: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -282:
                        break;
                    case 309: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -283:
                        break;
                    case 310: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -284:
                        break;
                    case 311: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -285:
                        break;
                    case 312: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -286:
                        break;
                    case 313: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -287:
                        break;
                    case 314: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -288:
                        break;
                    case 315: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -289:
                        break;
                    case 316: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -290:
                        break;
                    case 317: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -291:
                        break;
                    case 318: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -292:
                        break;
                    case 319: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -293:
                        break;
                    case 320: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -294:
                        break;
                    case 321: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -295:
                        break;
                    case 322: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -296:
                        break;
                    case 323: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -297:
                        break;
                    case 324: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -298:
                        break;
                    case 325: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -299:
                        break;
                    case 326: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -300:
                        break;
                    case 327: {
                        System.out.println("in empty empty");
                    }
                    case -301:
                        break;
                    case 328: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -302:
                        break;
                    case 329: {
                        System.out.println("in empty empty");
                    }
                    case -303:
                        break;
                    case 330: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -304:
                        break;
                    case 331: {
                        System.out.println("in empty empty");
                    }
                    case -305:
                        break;
                    case 332: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -306:
                        break;
                    case 333: {
                        System.out.println("in empty empty");
                    }
                    case -307:
                        break;
                    case 334: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -308:
                        break;
                    case 335: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -309:
                        break;
                    case 336: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -310:
                        break;
                    case 337: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -311:
                        break;
                    case 338: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -312:
                        break;
                    case 339: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -313:
                        break;
                    case 340: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -314:
                        break;
                    case 341: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -315:
                        break;
                    case 342: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -316:
                        break;
                    case 343: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -317:
                        break;
                    case 344: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -318:
                        break;
                    case 345: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -319:
                        break;
                    case 346: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -320:
                        break;
                    case 347: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -321:
                        break;
                    case 348: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -322:
                        break;
                    case 349: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -323:
                        break;
                    case 350: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -324:
                        break;
                    case 351: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -325:
                        break;
                    case 352: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -326:
                        break;
                    case 353: {
                        System.out.println("in empty empty");
                    }
                    case -327:
                        break;
                    case 354: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -328:
                        break;
                    case 355: {
                        System.out.println("in empty empty");
                    }
                    case -329:
                        break;
                    case 356: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -330:
                        break;
                    case 357: {
                        System.out.println("in empty empty");
                    }
                    case -331:
                        break;
                    case 358: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -332:
                        break;
                    case 359: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -333:
                        break;
                    case 360: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -334:
                        break;
                    case 361: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -335:
                        break;
                    case 362: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -336:
                        break;
                    case 363: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -337:
                        break;
                    case 364: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -338:
                        break;
                    case 365: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -339:
                        break;
                    case 366: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -340:
                        break;
                    case 367: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -341:
                        break;
                    case 368: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -342:
                        break;
                    case 369: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -343:
                        break;
                    case 370: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -344:
                        break;
                    case 371: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -345:
                        break;
                    case 372: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -346:
                        break;
                    case 373: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -347:
                        break;
                    case 374: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -348:
                        break;
                    case 375: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -349:
                        break;
                    case 376: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -350:
                        break;
                    case 377: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -351:
                        break;
                    case 378: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -352:
                        break;
                    case 379: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -353:
                        break;
                    case 380: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -354:
                        break;
                    case 381: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -355:
                        break;
                    case 382: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -356:
                        break;
                    case 383: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -357:
                        break;
                    case 384: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -358:
                        break;
                    case 385: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -359:
                        break;
                    case 386: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -360:
                        break;
                    case 387: {
                        return new Symbol(sym.KEY, yytext());
                    }
                    case -361:
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
