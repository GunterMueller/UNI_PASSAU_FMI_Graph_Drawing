package de.chris.plugins.inputserializers.test;

import java_cup.runtime.Symbol;


%%


%{
    private int nodeCount = 0;
    private int edgeCount = 0;
%}

%cup
%line
%char
%state GRAPH NODE EDGE

ALPHA      = [a-zA-Z_]
DIGIT      = [0-9]
NEWLINE    = \n
WHITESPACE = [ \t\r\f]

SBRACE     = \[
CBRACE     = \]

INTEGER    = {DIGIT}+


%%


           {WHITESPACE}	{ }
           {NEWLINE}	{ }
<GRAPH>    {SBRACE}		{ return new Symbol(sym.SBRACE); }
<GRAPH>    {CBRACE}		{ return new Symbol(sym.CBRACE); }
           {INTEGER}	{
           					return new Symbol(sym.INTEGER,
           						new Integer(yytext()));
           				}
           "id"			{ return new Symbol(sym.ID); }
           "graph"		{
           					yybegin(GRAPH);
           					return new Symbol(sym.GRAPH);
           				}
<GRAPH>    "node"		{
							yybegin(NODE);
							return new Symbol(sym.NODE);
						}
<NODE>     {SBRACE}		{
							nodeCount++;
							return new Symbol(sym.SBRACE);
						}
<NODE>     {CBRACE}		{
							nodeCount--;
							if (nodeCount == 0)
							{
								yybegin(GRAPH);
							}
							return new Symbol(sym.CBRACE);
						}
<GRAPH>    "edge"		{
							yybegin(EDGE);
							return new Symbol(sym.EDGE);
						}
<GRAPH>    "directed"	{
							return new Symbol(sym.DIRECTED);
						}
<EDGE>     "source"		{ return new Symbol(sym.SOURCE); }

<EDGE>     "target"		{ return new Symbol(sym.TARGET); }
<EDGE>     {SBRACE}		{
							edgeCount++;
							return new Symbol(sym.SBRACE);
						}
<EDGE>     {CBRACE}		{
							edgeCount--;
							if (edgeCount == 0)
							{
								yybegin(GRAPH);
							}
							return new Symbol(sym.CBRACE);
						}
		   .			{
							System.out.println("\nUnmatched input: " +
								yytext() + " in line " + (yyline + 1));
						}
