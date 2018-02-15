package org.graffiti.plugins.ios.gml.gmlReader.parser;

import java_cup.runtime.Symbol;

/** 
 * Class Scanner provides an interface to the lexer for the GML file
 * format. This class is primarily designated for developing and
 * debugging purposes. 
 * 
 * @author R&uuml;diger Schnoy
 */
class Scanner {

    public static void main(String argv[]) throws java.io.IOException {
        Yylex yy = new Yylex(System.in);
        // Symbol t;
        // while ((t = yy.yylex()) != null)
        //    System.out.print(t + " ");
        Symbol t;
        do {
	   t = yy.next_token();
	   System.out.print(t);
	} while (t.sym != sym.EOF);
        System.out.println();
    }

}

%%

%{

    /** Contains the number of nested comments. */
    private int comments = 0;
  
    /** Constructs a new Symbol from a given token. */
    private Symbol token(int k) {
        return new Symbol(k, yyline + 1, yychar);
    }

    /** Constructs a new Symbol from a given token and the associated
     *  value. */
    private Symbol token(int k, Object value) {
        return new Symbol(k, yyline + 1, yychar, value);
    }
 
%}

%type java_cup.runtime.Symbol
%char
%line
%char
%cup
%eofval{
    return new Symbol(sym.EOF, null);
%eofval}

dot        = \.
plus       = \+
minus      = \-
hashmark   = \#
lbrace     = \[
rbrace     = \]
quotes     = \"

delim      = [ \t\r\n]
whitespace = {delim}+
digit      = [0-9]
sign       = {plus}|{minus}
alpha      = [a-zA-Z_]
alphanum   = {alpha}|{digit}
asciired   = [^\"]*
instring   = {asciired}|&{alphanum}*;
mantissa   = ((e|E){sign}{digit}+)?

comment    = {hashmark}.*

key        = {alpha}({alphanum})*

%state STRING

%%

{whitespace}			{ /* nothing to be done */ }
<YYINITIAL>{comment}		{ /* nothing to be done */ }

"graph"				{ return token(sym.GRAPH); }
"node"				{ return token(sym.NODE); }
"edge"				{ return token(sym.EDGE); }
"node_style"			{ return token(sym.NODESTYLE); }
"edge_style"			{ return token(sym.EDGESTYLE); }

{lbrace}			{ return token(sym.LBRACE); }
{rbrace}			{ return token(sym.RBRACE); }

{sign}?{digit}?{digit}?{digit}?{digit}?{digit}?{digit}?{digit}?{digit}?{digit}			{ return token(sym.INTEGER, new
				      Integer(yytext())); }
				      
{sign}?{digit}{digit}{digit}{digit}{digit}{digit}{digit}{digit}{digit}{digit}+ { return token(sym.INTEGER, new Integer(0)); }

{sign}?{digit}+{dot}{digit}+{mantissa}
				{ return token(sym.REAL, new
				      Double(yytext())); }

<YYINITIAL>{quotes}{quotes}	{ return token(sym.STRING, ""); }
<YYINITIAL>{quotes}		{ yybegin(STRING); }
<STRING>{instring}*		{ return token(sym.STRING, yytext()); }
<STRING>{quotes}		{ yybegin(YYINITIAL); }

{key}				{ return token(sym.KEY, yytext()); }

.				{ System.err.println("\nUnmatched " 
					+ "input (line " + (yyline + 1) 
					+ " column " + (yychar + 1) 
					+ "): \"" + yytext() + "\""); }

