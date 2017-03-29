/* (C) Universitaet Passau 1986-1991 */
%{/* GraphEd Source, 1986-1991 by Michael Himsolt	*/
/**********************************************************************/
/*									*/
/*				sanner.l				*/
/*									*/
/************************************************************************/
/*									*/
/*	Dieses Modul enthaelt den Scanner fuer den Parser in parser.y.	*/
/*									*/
/*	Der eigentliche C-Sourcecode (der den Scanner yylex() enthaelt)	*/
/*	wird von dem Scannergerator lex erzeugt.			*/
/*									*/
/************************************************************************/

#include "misc.h"
#include "load.h"

#include "graph.h"	/* Fuer y.tab.h	*/
#include "group.h"	/* ---  "   ---	*/


/************************************************************************/
/*									*/
/*			GLOBALE FUNKTIONEN				*/
/*									*/
/************************************************************************/
/*									*/
/*	int	yylex()		[Nach Durchlauf von lex]		*/
/*									*/
/************************************************************************/


#include "y.tab.h"
%}
%p 5000
%a 5000

digit		[0-9]
number		-?{digit}+
floatnumber		-?{digit}+\.{digit}+

left_brace		\{
dollar			\$
no_dollar		[^\$]
right_brace		\}
no_right_brace		[^\}]
escaped_brace		(\\{right_brace}|\\{left_brace})

begin_graphed_attributes	{left_brace}{dollar}
end_graphed_attributes	{dollar}{right_brace}

quotation_mark			\"
no_quotation_mark		[^\"]
escaped_character		\\.
no_quotation_mark_or_backslash	[^"\\]
white_space			[ \t]
newline				"\n"

%%
{left_brace}({no_dollar}|{escaped_brace})({escaped_brace}|{no_right_brace})*{right_brace}	{

			/* Kommentar : { irgendwas }, aber nicht {$	*/
			
		}

{number}	{
			/* Ganze Zahl (auch negativ !) */
			

			yylval.nr = atoi (yytext);
			return T_NUMBER;
		}

{floatnumber}	{
			/* Ganze Zahl (auch negativ !) */
			

			yylval.floatnr = atof (yytext);
			return T_FLOATNUMBER;
		}

{quotation_mark}({escaped_character}|{no_quotation_mark_or_backslash})*{quotation_mark}	{

			/* Identifier = "text", wobei ein '"' in text	*/
			/* durch ESCAPE_CHARACTER ('\') maskiert werden	*/
			/* kann						*/
			
			yylval.text = remove_escape_characters_from_text
					(&(yytext[1]), yyleng-2);
					/* yytext[1], yyleng-2, um die	*/
					/* " abzufangen			*/
					
			return T_IDENTIFIER;
		}

{begin_graphed_attributes}	return T_BEGIN_GRAPHED_ATTRIBUTES_DELIMETER;
{end_graphed_attributes}	return T_END_GRAPHED_ATTRIBUTES_DELIMETER;

GRAPH		return T_GRAPH;
Graph		return T_GRAPH;
PROD		return T_PROD;
Prod		return T_PROD;
END		return T_END;
End		return T_END;
"="		/* compatibility with old versions */;
";"		return (';');
","		return (',');
"["		return ('[');
"]"		return (']');
INIT		return T_INIT;
Init		return T_INIT;
GRAPHS		return T_GRAPHS;
Graphs		return T_GRAPHS;

DIRECTED	return T_DIRECTED;
Directed	return T_DIRECTED;
UNDIRECTED	return T_UNDIRECTED;
Undirected	return T_UNDIRECTED;

Font:		return T_SCANNER_FONT;
NodeType:	return T_SCANNER_NODETYPE;
EdgeType:	return T_SCANNER_EDGETYPE;
DIR		return T_DIR;
Dir:		return T_DIR;
NSTYL		return T_NODESTYLE;
NodeStyle:	return T_NODESTYLE;
ESTYL		return T_EDGESTYLE;
EdgeStyle:	return T_EDGESTYLE;

NP			return T_NODEPLACE;
NodePosition:		return T_NODEPLACE;
NS			return T_NODESIZE;
NodeSize:		return T_NODESIZE;
NEI			return T_NODE_EDGE_INTERFACE;
NodeEdgeInterface:	return T_NODE_EDGE_INTERFACE;
NLP			return T_NODELABEL_PLACEMENT;
NodeLabelPlacement:	return T_NODELABEL_PLACEMENT;
NLV			return T_NODELABEL_VISIBILITY;
NodeLabelVisibility:	return T_NODELABEL_VISIBILITY;
NFI			return T_NODEFONT_INDEX;
NodeFontIndex:		return T_NODEFONT_INDEX;
NTI			return T_NODETYPE_INDEX;
NodeTypeIndex:		return T_NODETYPE_INDEX;

EL			return T_EDGELINE_POINTS;
EdgeLine:		return T_EDGELINE_POINTS;
EFI			return T_EDGEFONT_INDEX;
EdgeFontIndex:		return T_EDGEFONT_INDEX;
ETI			return T_EDGETYPE_INDEX;
EdgeTypeIndex:		return T_EDGETYPE_INDEX;
ELV			return T_EDGELABEL_VISIBILITY;
EdgeLabelVisibility:	return T_EDGELABEL_VISIBILITY;
ELS			return T_EDGELABEL_SIZE;
EdgeLabelSize:		return T_EDGELABEL_SIZE;
AL			return T_ARROWLENGTH;
ArrowLength:		return T_ARROWLENGTH;
AA			return T_ARROWANGLE;
ArrowAngle:		return T_ARROWANGLE;

COL			return T_COLOR;
Color:			return T_COLOR;

GRAGRA			return T_GRAGRA;
GraGra:			return T_GRAGRA;
GEMBED			return T_GLOBAL_EMBEDDING;
GlobalEmbed:		return T_GLOBAL_EMBEDDING;
EMBMATCH		return T_EMBED_MATCH_ATTR;
EmbeddingMatch:		return T_EMBED_MATCH_ATTR;

WA			return T_WORKING_AREA_SIZE;
WorkingAreaSize:	return T_WORKING_AREA_SIZE;
WAWIN			return T_WORKING_AREA_WINDOW;
WorkingAreaWindow:	return T_WORKING_AREA_WINDOW;
SC			return T_SCROLL_OFFSET;
ScrollOffset:		return T_SCROLL_OFFSET;
GR			return T_GRIDWIDTH;
GridWidth:		return T_GRIDWIDTH;
SMP			return T_SELECT_IF_MULTIPLE_PICKED; /* obsolete */

DERIVATION_SEQUENCE	return T_START_LOAD_DERIVATION_SEQUENCE;
DerivationSequence:	return T_START_LOAD_DERIVATION_SEQUENCE;
UseGraGra:		return T_USEGRAGRA;
Startnode:		return T_STARTNODE;
Apply:			return T_APPLY;
Node:			return T_NODE;
Production:		return T_PRODUCTION;


[a-zA-Z_:][a-zA-Z0-9_:]*	{ yylval.text = strsave(yytext); return T_KEY; }

{newline}	{ 
			lex_input_file_linenumber++;
			/* Ansonsten ueberlesen			*/
		}
{white_space}	{
			/* White space (tab,blank) ueberlesen	*/
		}
.		{
			return yytext[0];
			/*  = alles hier nicht aufgefuehrte	*/
		}
%%
