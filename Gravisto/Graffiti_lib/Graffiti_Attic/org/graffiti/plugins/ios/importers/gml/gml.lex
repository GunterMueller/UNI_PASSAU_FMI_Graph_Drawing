package org.graffiti.plugins.ios.importers.gml;

import java_cup.runtime.Symbol;

class Sample {

    public static void main(String argv[]) throws java.io.IOException {
	Yylex yy = new Yylex(System.in);
	// Yytoken t;
	// while ((t = yy.yylex()) != null) System.out.println(t);
	Symbol t;
	while ((t = yy.next_token()) != null) System.out.print(t);
	System.out.println();
    }
}

%%

%{
        private int nodeCount = 0;
        private int edgeCount = 0;
	private int graphicsCount = 0;
	private int pointCount = 0;
	private int lineCount = 0;
	private int stateBeforeGraphics = -1;
	private int stateBeforePoint = -1;
	private int stateBeforeLine = -1;
	private int ignoreCount = 0;
%}

%cup
%line
%char
%state GRAPH NODE EDGE GRAPHICS LINE POINT NODELABELGRAPHICS EDGELABELGRAPHICS
%state NODESTYLE EDGESTYLE EMPTY

ALPHA      = [a-zA-Z_]
DIGIT      = [0-9]
SIGN       = (\+|-)?
MANTISSA   = ([eE]{SIGN}({DIGIT}+))?
NEWLINE    = \n
WHITESPACE = [ \t\r\f]
REDASCII   = [\u0000-\u0021\u0023-\u0025\u0027-\u007F]*
IDENTIFIER = &({ALPHA}*);
INSTRING   = ({REDASCII}|\\\"|(&lt;)|(&gt;)|(&quot;)|(&apos;)|(&amp;))*

SBRACE     = \[
CBRACE     = \]
QUOTES     = "\""

KEY        = {ALPHA}({ALPHA}|{DIGIT})*
INTEGER    = {SIGN}{DIGIT}+
REAL       = ({SIGN}{DIGIT}+.{DIGIT}+{MANTISSA})|NaN
STRING     = {QUOTES}{INSTRING}{QUOTES}

BOOL01 = 0|1
BOOLT = 1
BOOLF = 0
BOOLEAN    = {BOOL01}|{BOOLT}|{BOOLF}

COMMENT    = \#.*$

NOBRACES  = [^\]\[]

%%

           {WHITESPACE}  { /* ignore white space */ }
           {NEWLINE}     { /* System.out.println(); */  }
           {COMMENT}     { System.out.println(yytext()); /* ignore comments */ }

<GRAPH>    {SBRACE}      { return new Symbol(sym.SBRACE); }
<GRAPH>    {CBRACE}      { return new Symbol(sym.CBRACE); }

           {INTEGER}     { return new Symbol(sym.INTEGER,
					     new Integer(yytext())); }

           {BOOL01}     { return new Symbol(sym.BOOL01,
					     new Integer(yytext())); }
           
	   {BOOLT}     { return new Symbol(sym.BOOLT, new Integer(1)); }
	   {BOOLF}     { return new Symbol(sym.BOOLT, new Integer(0)); }

           {REAL}        { return new Symbol(sym.REAL, new Double(yytext())); }
           {STRING}      { return new Symbol(sym.STRING,
					     yytext().substring(1, yytext().length() - 1)); }

           "id"          { return new Symbol(sym.ID); }

           "label"       { return new Symbol(sym.LABEL); }
           "Creator"     { return new Symbol(sym.CREATOR); }
           "version"     { return new Symbol(sym.VERSION); }
           "comment"     { return new Symbol(sym.COMMENT); }

           "graph"       { yybegin(GRAPH); return new Symbol(sym.GRAPH); }

<GRAPH>    "node"        { System.out.println("  node"); yybegin(NODE); 
                           return new Symbol(sym.NODE); }
<GRAPH>    "edge_style"  { yybegin(EDGESTYLE);
						   //return new Symbol(sym.EDGE_STYLE); 
						   }
<GRAPH>    "node_style"  { yybegin(NODESTYLE);
					       //return new Symbol(sym.NODE_STYLE); }
					       }

<NODESTYLE> {SBRACE}     { ignoreCount = 1; 
							System.out.println("in nodestyle sbrace"); 
						    yybegin(EMPTY); 
						    //return new Symbol(sym.SBRACE);}
						    }
<EDGESTYLE> {SBRACE}     { ignoreCount = 1; 
							System.out.println("in edgestyle sbrace"); 
						    yybegin(EMPTY); 
						    //return new Symbol(sym.SBRACE);}
						    }

<EMPTY> {SBRACE}         { ignoreCount += 1;
							System.out.println("in empty sbrace"); 
						    //yybegin(EMPTY);
							//return new Symbol(sym.SBRACE); }
							}
<EMPTY> {CBRACE}         { ignoreCount -= 1;
                           if (ignoreCount == 0) {
                               yybegin(GRAPH);
                               System.out.println("in empty cbrace, begin graph");
                               //return new Symbol(sym.CBRACE);
                           } else {
     						   System.out.println("in empty cbrace");
     					   }; 
   						   //return new Symbol(sym.CBRACE);}
   						   }
<EMPTY> {NOBRACES}* { System.out.println("in empty empty"); }


<NODE>     {SBRACE}      { nodeCount++;
 			   return new Symbol(sym.SBRACE); }
<NODE>     {CBRACE}      { nodeCount--;
                           if (nodeCount == 0) {
			       yybegin(GRAPH);
			   }
			   return new Symbol(sym.CBRACE); }
			   

<NODE>     "LabelGraphics" { // ignore for now 
					         yybegin(NODELABELGRAPHICS);}
<NODELABELGRAPHICS> {SBRACE} { }
<NODELABELGRAPHICS> {CBRACE} { yybegin(NODE); }
			   
<EDGE>     "LabelGraphics" { // ignore for now 
					         yybegin(EDGELABELGRAPHICS);}
<EDGELABELGRAPHICS> {SBRACE} { }
<EDGELABELGRAPHICS> {CBRACE} { yybegin(EDGE); }
			   

<GRAPH>    "edge"        { yybegin(EDGE);  return new Symbol(sym.EDGE); }
<GRAPH>    "directed"    { return new Symbol(sym.DIRECTED); }

<EDGE>     "source"      { return new Symbol(sym.SOURCE); }
<EDGE>     "target"      { return new Symbol(sym.TARGET); }
<EDGE>     {SBRACE}      { edgeCount++;
 			   return new Symbol(sym.SBRACE); }
<EDGE>     {CBRACE}      { edgeCount--;
                           if (edgeCount == 0) {
			       yybegin(GRAPH);
			   }
			   return new Symbol(sym.CBRACE); }

           "graphics"    { stateBeforeGraphics = yy_lexical_state;
	                   yybegin(GRAPHICS);
			   // System.out.println("\nentering graphics state.");
                           return new Symbol(sym.GRAPHICS); }

<GRAPHICS> {SBRACE}      { graphicsCount++;
 			   return new Symbol(sym.SBRACE); }
<GRAPHICS> {CBRACE}      { graphicsCount--;
                           if (graphicsCount == 0) {
			       yybegin(stateBeforeGraphics);
			       // System.out.println("\nleaving graphics ("
			       // + stateBeforeGraphics
			       // + ").");
			   }
			   return new Symbol(sym.CBRACE); }
<GRAPHICS> "x"           { return new Symbol(sym.GRAPHICS_X); }
<GRAPHICS> "y"           { return new Symbol(sym.GRAPHICS_Y); }
<GRAPHICS> "z"           { return new Symbol(sym.GRAPHICS_Z); }
<GRAPHICS> "w"           { return new Symbol(sym.GRAPHICS_W); }
<GRAPHICS> "h"           { return new Symbol(sym.GRAPHICS_H); }
<GRAPHICS> "d"           { return new Symbol(sym.GRAPHICS_D); }
<GRAPHICS> "Line"        { stateBeforeLine = yy_lexical_state;
                           yybegin(LINE);
                           return new Symbol(sym.GRAPHICS_LINE); }

<LINE>     {SBRACE}      { lineCount++;
 			   return new Symbol(sym.SBRACE); }
<LINE>     {CBRACE}      { lineCount--;
                           if (lineCount == 0) {
			       yybegin(stateBeforeLine);
			   }
 			   return new Symbol(sym.CBRACE);
                         }

<GRAPHICS, LINE> "point" { stateBeforePoint = yy_lexical_state;
                           yybegin(POINT);
                           return new Symbol(sym.GRAPHICS_POINT); }
<POINT>    {SBRACE}      { pointCount++;
 			   return new Symbol(sym.SBRACE); }
<POINT>    {CBRACE}      { pointCount--;
                           if (pointCount == 0) {
			       yybegin(stateBeforePoint);
			   }
 			   return new Symbol(sym.CBRACE);
                         }
<POINT>    "x"           { return new Symbol(sym.POINT_X); }
<POINT>    "y"           { return new Symbol(sym.POINT_Y); }
<POINT>    "z"           { return new Symbol(sym.POINT_Z); }


<GRAPHICS> "type"        { return new Symbol(sym.GRAPHICS_TYPE); }
<GRAPHICS> "visible"     { return new Symbol(sym.GRAPHICS_VISIBLE); }
<GRAPHICS> "fill"        { return new Symbol(sym.GRAPHICS_FILL); }
<GRAPHICS> "outline"     { return new Symbol(sym.GRAPHICS_OUTLINE); }
<GRAPHICS> "stipple"     { return new Symbol(sym.GRAPHICS_STIPPLE); }
<GRAPHICS> "anchor"      { return new Symbol(sym.GRAPHICS_ANCHOR); }
<GRAPHICS> "width"       { return new Symbol(sym.GRAPHICS_WIDTH); }
<GRAPHICS> "extent"      { return new Symbol(sym.GRAPHICS_EXTENT); }
<GRAPHICS> "start"       { return new Symbol(sym.GRAPHICS_START); }
<GRAPH, GRAPHICS, YYINITIAL> "style" { return new Symbol(sym.GRAPHICS_STYLE); }
<GRAPHICS> "background"  { return new Symbol(sym.GRAPHICS_BACKGROUND); }
<GRAPHICS> "foreground"  { return new Symbol(sym.GRAPHICS_FOREGROUND); }
<GRAPHICS> "bitmap"      { return new Symbol(sym.GRAPHICS_BITMAP); }
<GRAPHICS> "image"       { return new Symbol(sym.GRAPHICS_IMAGE); }
<GRAPHICS> "arrowhead"   { return new Symbol(sym.GRAPHICS_ARROW_HEAD); }
<GRAPHICS> "arrowtail"   { return new Symbol(sym.GRAPHICS_ARROW_TAIL); }
<GRAPHICS> "arrow"       { return new Symbol(sym.GRAPHICS_ARROW); }
<GRAPHICS> "capstyle"    { return new Symbol(sym.GRAPHICS_CAPSTYLE); }
<GRAPHICS> "joinstyle"   { return new Symbol(sym.GRAPHICS_JOINSTYLE); }
<GRAPHICS> "smooth"      { return new Symbol(sym.GRAPHICS_SMOOTH); }
<GRAPHICS> "splinesteps" { return new Symbol(sym.GRAPHICS_SPLINESTEPS); }
<GRAPHICS> "justify"     { return new Symbol(sym.GRAPHICS_JUSTIFY); }
<GRAPHICS> "font"        { return new Symbol(sym.GRAPHICS_FONT); }

           {KEY}         { return new Symbol(sym.KEY, yytext()); }

. { System.out.println("\nUnmatched input: " + yytext() + " in line "
		       + (yyline + 1)); }
