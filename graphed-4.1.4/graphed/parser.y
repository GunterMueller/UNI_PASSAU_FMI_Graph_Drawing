/* (C) Universitaet Passau 1986-1991 */
%{/* GraphEd Source, 1986-1991 by Michael Himsolt	*/
/**********************************************************************/
/*									*/
/*				parser.y				*/
/*									*/
/************************************************************************/
/*									*/
/*	Dieses Modul enthaelt den Parser fuer GraphEd.			*/
/*	Geparst werden muessen :					*/
/*	- Graphen (inklusive GraphEd-interner Information)		*/
/*	- GRAPHED_INITIALISATION_FILE					*/
/*									*/
/*	Der eigentliche C-Sourcecode (der den Parser yyparse() enhaelt)	*/
/*	wird von dem Parsergenerator yacc erzeuggt.			*/
/*									*/
/************************************************************************/


#include "misc.h"
#include "graph.h"
#include "load.h"
#include "user.h"
#include "find.h"
#include "group.h"
#include "graphed_subwindows.h"

#include <sgraph/std.h>
#include <sgraph/slist.h>
#include <sgraph/random.h>
#include "derivation.h"

extern int  set_buffer_size (int buffer, int width, int height);
extern void set_canvas_window (int n, int x, int y, int width, int height);
extern void scroll_buffer (int buffer, int x, int y);
extern void ggtaf_set_number (Gragra_textual_apply_form ggtaf, int number);
extern void reset_buffer_has_changed (int buffer);
extern int yylex(void);
static void yyerror(char *string);

/************************************************************************/
/*									*/
/*			GLOBALE FUNKTIONEN				*/
/*									*/
/************************************************************************/
/*									*/
/*	int	yyparse ()		[Nach Durchlauf von YACC]	*/
/*									*/
/************************************************************************/


/************************************************************************/
/*									*/
/*		LOKALE FUNKTIONEN UND PROZEDUREN			*/
/*									*/
/************************************************************************/


static	void	yyerror   (char *string);
static	void	yywarning (char *string);

static	void	start_load_graph                 (void);
static	void	end_load_graph                   (void);


/************************************************************************/
/*									*/
/*			LOKALE VARIABLEN				*/
/*									*/
/************************************************************************/


static	Graph		current_graph      = empty_graph;    /*		*/
static	Node		current_node       = empty_node;     /* werden	*/
static	Edge		current_edge       = empty_edge;     /* gerade	*/
static	Edgeline	current_edgeline   = (Edgeline)NULL; /* geladen	*/
static	Edgeline	current_el_head    = (Edgeline)NULL; /*		*/
static	Node_attributes	current_node_attributes;
static	Edge_attributes	current_edge_attributes;
static	File_attributes	current_attributes;

static	Group		group_of_current_graph;

extern	Derivation_sequence		last_loaded_derivation_sequence;
static	Derivation_sequence		current_derivation_sequence;
static	Gragra_textual_apply_form	current_ggtaf;

static	int	loading_a_list_of_graphs = FALSE;
static	int	node_already_seen = FALSE;


/* Umrechnungstabellen fuer Indices (Noetig, da in der Datei die	*/
/* Reihenfolge ganz anders sein kann als in GraphEd)			*/

static	int	fontindex_translation     [MAX_FONTS];    /* = 0; */
static	int	nodetypeindex_translation [MAX_NODETYPE]; /* = 0; */
static	int	edgetypeindex_translation [MAX_EDGETYPE]; /* = 0; */

/* Zaehler : wie viele ... wurden geladen (zur Konsistenzpruefung)	*/

static	int	font_count     = 0;
static	int	nodetype_count = 0;
static	int	edgetype_count = 0;

/* Wurde ueberhaupt ein ... erfolgreich geladen ?			*/

static	int	any_font_successfully_loaded     = FALSE;
static	int	any_nodetype_successfully_loaded = FALSE;
static	int	any_edgetype_successfully_loaded = FALSE;

/* Makros, Range-Checking	*/

/*
#define	is_legal_coordinate(x,y)   (is_positive(x) && is_positive(y))
*/
/* Changed MH 25/9/91 to avoid flames */
#define	is_legal_coordinate(x,y)   TRUE

#define	is_legal_size(x,y)         (((x)>=0) && ((y)>=0))
#define is_legal_nodetype_index(i) (((i)>=0) && ((i)<nodetype_count))
#define is_legal_edgetype_index(i) (((i)>=0) && ((i)<edgetype_count))
#define is_legal_font_index(i)     (((i)>=0) && ((i)<font_count))
#define is_legal_color(i)          (((i)>=0) && ((i)<GRAPHED_COLORMAPSIZE))
#define is_legal_gragra_type(i)    (((i)>=0) && ((i)<NUMBER_OF_GRAGRA_TYPES))
#define	is_positive(x)             ((x) >= 0)

/* Falls graph_loading == TRUE, so wird gerade ein Graph geladen	*/
/* Wird gebraucht, um im Fehlerfall Konsistenz wiederherzustellen	*/
 
int	graph_loading = FALSE;

/* Stackgroesse fuer yacc : Maximal < 10000 Knoten zulaessig		*/
/* IST DIE YACC-STACKGROESSE NICHT AUSREICHEND, FOLGENDEN WERT ERHOEHEN	*/

#define	YYMAXDEPTH 10000

/* Das folgende Makro wird verwendet, um yyerror aufzurufen und dann	*/
/* den Parser abzubrechen (yyerror loescht den Graphen)			*/

#define	yacc_error(s)	{ yyerror(s); YYABORT; }

/************************************************************************/
/*									*/
/*			KONVENTIONEN					*/
/*									*/
/************************************************************************/
/*									*/
/*	Da Graphen auch von anderen Programmen erzeugt werden koennen,	*/
/*	wird im Parser mit Hilfe der obigen is_legel_... - Makros ein	*/
/*	Konsistenzcheck auf den Knoten- und Kantenattributen		*/
/*	vorgenommen. Nach Moeglichkeit werden Attribute, die "illegal"	*/
/*	sind, durch Defaultwerte ersetzt und mit yywarning eine Meldung	*/
/*	abgegeben.							*/
/*									*/
/*======================================================================*/
/*									*/
/*	Bei Syntax - und sonstigen schweren Fehlern, die einen Abbruch	*/
/*	des Parsens erzwingen (yacc_error), muss leider der bisher	*/
/*	eingegebene Graph aus Gruenden der Konsistenzerhaltung		*/
/*	(wegen der "vorwaerts" erzeugten Knoten, die keine Attribute	*/
/*	enthalten, was beim Zeichnen zum Absturz fuehren kann)		*/
/*	in yyerror geloescht werden.					*/
/*									*/
/*======================================================================*/
/*									*/
/*	yacc_error (string) ruft yyerror auf und bricht das Parsen	*/
/*	ab.								*/
/*									*/
/************************************************************************/
%}
%union	{	/* Stack fuer Parser	*/
	int		nr;      /* Integerwert		*/
	double		floatnr; /* Floating		*/
	char		*text;	 /* Stringwert		*/
	Group		group;	 /* Stringwert		*/
	File_attributes	attrs;
}
	
%token		T_GRAPH ';' ',' T_END T_GRAPHS T_INIT
%token		T_BEGIN_GRAPHED_ATTRIBUTES_DELIMETER T_END_GRAPHED_ATTRIBUTES_DELIMETER
%token		T_DIR T_DIRECTED T_UNDIRECTED
%token		T_NODESTYLE T_EDGESTYLE T_SCANNER_FONT T_SCANNER_NODETYPE T_SCANNER_EDGETYPE

%token		T_COLOR

%token		T_NODEPLACE T_NODESIZE T_NODEFONT_INDEX T_NODETYPE_INDEX
%token		T_NODELABEL_PLACEMENT T_NODE_EDGE_INTERFACE T_NODELABEL_VISIBILITY
%token		T_NODECOLOR

%token		T_EDGETYPE_INDEX T_EDGEFONT_INDEX T_EDGELINE_POINTS T_EDGELABEL_SIZE
%token		T_ARROWLENGTH T_ARROWANGLE T_EDGELABEL_VISIBILITY T_EDGECOLOR

%token		T_GRAGRA T_GLOBAL_EMBEDDING T_EMBED_MATCH_ATTR
%token		T_WORKING_AREA_SIZE T_SCROLL_OFFSET T_GRIDWIDTH T_WORKING_AREA_WINDOW

%token		T_SELECT_IF_MULTIPLE_PICKED T_PROD T_LEFT T_RIGHT T_EMBED T_MAPSTO_IN T_MAPSTO_OUT

%token		T_START_LOAD_DERIVATION_SEQUENCE
%token		T_USEGRAGRA T_STARTNODE
%token		T_APPLY T_NODE T_PRODUCTION

%token	<text>		T_KEY
%token	<nr>		T_NUMBER
%token	<floatnr>	T_FLOATNUMBER
%token	<text> 		T_IDENTIFIER

%type	<attrs>		any_attribute any_attributes value

%start		parser_start
%%
parser_start :	plain_graph
		{
			/* Hier besteht die Datei aus einem	*/
			/* einzelnen Graphen			*/

			int	nr;

			if ((nr = all_nodes_complete()) != -1) {
				warning ("Unspecified node %d\n", nr);
				yacc_error ("Not all Nodes specified\n");
			}

			filetype_last_loaded = LOAD_GRAPH;

		}

		| graphs
		{
			/* Datei besteht aus einer Liste von	*/
			/* Graphen				*/
				
			int	nr;

			if ((nr = all_nodes_complete()) != -1) {
				warning ("Unspecified node %d\n", nr);
				yacc_error ("Not all Nodes specified\n");
			}

			filetype_last_loaded = LOAD_GRAPHS;
		}

		| initialisation
		{
			filetype_last_loaded = LOAD_INITIALIZATION;
		}

		| start_load_derivation_sequence
		{
			filetype_last_loaded = LOAD_DERIVATION;
		}

		| error
		{
			filetype_last_loaded = LOAD_NOTHING;

			/* Syntax Error	*/
			YYABORT;
		}
		;



/************************************************************************/
/*									*/
/*			SIMPLE GRAPH					*/
/*									*/
/************************************************************************/


plain_graph :	/* REAL GRAPH	*/
		{
			start_load_graph ();
		}
		T_GRAPH
		{
			current_graph = create_graph (load_buffer);
			current_graph->is_production = FALSE;
		}
		graph_label
		directedness
		{
		  	font_count     = 0;
		  	nodetype_count = 0;
		  	edgetype_count = 0;
		  	any_font_successfully_loaded     = FALSE;
		  	any_nodetype_successfully_loaded = FALSE;
		  	any_edgetype_successfully_loaded = FALSE;

			current_attributes = NULL;
		}
		graph_attributes
		{
			/* print_file_attributes (stderr, current_attributes); */
			current_graph->file_attrs = current_attributes;
		}
		list_of_nodes
		T_END
		{
			end_load_graph ();
		}

		|

		/* GRAGRA PRODUCTION	*/
		{
			start_load_graph ();
		}
		T_PROD
		{
			current_graph = create_graph (load_buffer);
			current_graph->is_production = TRUE;
		}
		graph_label
		gragra_type
		directedness
		{
		  	font_count     = 0;
		  	nodetype_count = 0;
		  	edgetype_count = 0;
		  	any_font_successfully_loaded     = FALSE;
		  	any_nodetype_successfully_loaded = FALSE;
		  	any_edgetype_successfully_loaded = FALSE;

			current_attributes = NULL;
		}
		graph_attributes
		{
			/* print_file_attributes (stderr, current_attributes); */
			current_graph->file_attrs = current_attributes;
		}
		list_of_nodes
		T_END
		{
			end_load_graph ();
		}

		;


graph_label :	/* nothing or ... */
		{
			set_graph_label (current_graph, strsave(""));
		}
		|
		T_IDENTIFIER
		{
			set_graph_label (current_graph, $1);
		}


directedness :	/* nothing or ... */
		{
			set_graph_directedness (current_graph, TRUE);
		}

		| T_DIRECTED
		{
			set_graph_directedness (current_graph, TRUE);
		}

		| T_UNDIRECTED
		{
			set_graph_directedness (current_graph, FALSE);
		}
		;


gragra_type :	  /* nothing or ... */
		{
			current_graph->gra.type = ENCE_1;
		}

		| T_GRAGRA T_NUMBER
		{
			if (is_legal_gragra_type ($2)) {
				current_graph->gra.type = $2;
			} else {
				yywarning ("Illegal graph grammar type");
			}
		}
		;

/************************************************************************/
/*									*/
/*			LIST OF GRAPHS					*/
/*									*/
/************************************************************************/


graphs :	T_GRAPHS
		{
			loading_a_list_of_graphs = TRUE;
		  	font_count     = 0;
		  	nodetype_count = 0;
		  	edgetype_count = 0;
		  	any_font_successfully_loaded     = FALSE;
		  	any_nodetype_successfully_loaded = FALSE;
		  	any_edgetype_successfully_loaded = FALSE;

			current_attributes = NULL;
		}
		graph_attributes
		{
			free_file_attributes (current_attributes);
		}
		list_of_graphs
		{
			loading_a_list_of_graphs = FALSE;
		}
		T_END
		;

list_of_graphs :	/* nothing */
			| plain_graph list_of_graphs
			;

/************************************************************************/
/*									*/
/*			GRAPH ATTRIBUTES					*/
/*									*/
/************************************************************************/


graph_attributes:	/* nothing or */

		|

		T_BEGIN_GRAPHED_ATTRIBUTES_DELIMETER
		old_fontlist ';'
		old_nodetypelist ';'
		old_edgetypelist ';'
		graph_state_list ';'
		graphed_state_list
		T_END_GRAPHED_ATTRIBUTES_DELIMETER

		|

		T_BEGIN_GRAPHED_ATTRIBUTES_DELIMETER
		new_style_graph_attributes
		T_END_GRAPHED_ATTRIBUTES_DELIMETER
		graph_attributes
		;


new_style_graph_attribute :	
		  new_font
		| new_nodetype
		| new_edgetype
		| any_graph_state
		| any_graphed_state
		| unknown_attribute
		| ';'
		;

new_style_graph_attributes :	
		  /* nothing or */
		| new_style_graph_attribute new_style_graph_attributes
		;


/************************************************************************/
/*									*/
/*			LIST OF NODES					*/
/*									*/
/************************************************************************/


list_of_nodes :	  /* nothing or ... */
		| node list_of_nodes
		;

node :		node_nr
		{
			if (node_already_seen) {
				current_node_attributes =
					get_node_attributes (current_node);
			} else {
				current_node_attributes =
					get_node_style (NORMAL_NODE_STYLE);
				current_node_attributes.x = random() % 100;
				current_node_attributes.y = random() % 100;
			}

			current_attributes = NULL;
		}
		node_attributes
		{
			node_set (current_node, ONLY_SET,
				SET_NODE_ATTRIBUTES (current_node_attributes),
				NODE_POSITION,
					current_node_attributes.x,
					current_node_attributes.y,
				0);

			/* print_file_attributes (stderr, current_attributes); */
			current_node->file_attrs = current_attributes;
		}
		nodelabel
		{
		  current_node->loaded = TRUE;
		}
		list_of_edges
		{
		  current_node = empty_node;
		}
		;

node_nr :	T_NUMBER
		{
			current_node = get_node_with_number(current_graph, $1);
			if (contains_group_node (group_of_current_graph, current_node) == empty_group) {
				group_of_current_graph = add_immediately_to_group (
					group_of_current_graph, current_node);
				node_already_seen = FALSE;
			} else {
				node_already_seen = TRUE;
			}
		}
		;

nodelabel :	/* nothing */
		|
		T_IDENTIFIER
		{
		  if (strcmp ($1, "")) {
		    node_set (current_node, ONLY_SET,
			      NODE_LABEL, $1,
			      0);
		  }
		}
		;


/************************************************************************/
/*									*/
/*			NODE ATTRIBUTES					*/
/*									*/
/************************************************************************/


node_attributes :	/* nothing or ... */
			|
			T_BEGIN_GRAPHED_ATTRIBUTES_DELIMETER
			list_of_node_attributes
			T_END_GRAPHED_ATTRIBUTES_DELIMETER
			node_attributes
			|
			/* short version, compatibility */
			T_BEGIN_GRAPHED_ATTRIBUTES_DELIMETER
			nodeplace
			T_END_GRAPHED_ATTRIBUTES_DELIMETER
			;

list_of_node_attributes :	
			| any_node_attribute list_of_node_attributes
			;

any_node_attribute :	  T_NODESIZE nodesize
			| T_NODETYPE_INDEX nodetype_index
			| T_NODEFONT_INDEX nodelabel_font_index
			| T_NODELABEL_PLACEMENT nodelabel_placement
			| T_NODE_EDGE_INTERFACE node_edge_interface
			| T_NODELABEL_VISIBILITY nodelabel_visibility
			| T_NODEPLACE nodeplace
			| T_COLOR nodecolor
			| unknown_attribute
			| ';'
			;

nodeplace :		T_NUMBER T_NUMBER
			{
				if (is_legal_coordinate ($1, $2)) {
					current_node_attributes.x = $1;
					current_node_attributes.y = $2;
				} else {
					yacc_error ("Illegal nodeplace");
				}
			}
			;

nodesize :		T_NUMBER T_NUMBER
			{
				if (is_legal_size($1,$2)) {
					current_node_attributes.width  = $1;
					current_node_attributes.height = $2;
				} else {
					yywarning ("Illegal nodesize");
				}
			}
			;

nodetype_index :	T_NUMBER
			{
				if (is_legal_nodetype_index($1)) {
					current_node_attributes.type_index =
						nodetypeindex_translation[$1];
				} else {
					yywarning ("Illegal nodetype_index");
				}
			}
			;

nodelabel_font_index :	T_NUMBER
			{
				if (is_legal_font_index($1)) {
					current_node_attributes.font_index =
						fontindex_translation[$1];
				} else {
					yywarning ("Illegal font_index");
				}
			}
			;

nodelabel_placement :	T_NUMBER
			{
				if (is_legal_nodelabel_placement($1)) {
					current_node_attributes.nodelabel_placement = 
						(Nodelabel_placement)$1;
				} else {
					yywarning ("Illegal nodelabel_placement");
				}
			}
			;

node_edge_interface :	T_NUMBER
			{
				if (is_legal_node_edge_interface($1)) {
					current_node_attributes.node_edge_interface = 
						(Node_edge_interface)$1;
				} else {
					yywarning ("Illegal node_edge_interface");
				}
			}
			;

nodelabel_visibility :	T_NUMBER
			{
				current_node_attributes.label_visibility = $1;
			}
			;

nodecolor :		T_NUMBER
			{
				if (is_legal_color($1)) {
					current_node_attributes.color = $1;
				}
			}
			;

/************************************************************************/
/*									*/
/*			LIST OF EDGES					*/
/*									*/
/************************************************************************/


list_of_edges :		 ';'
			| edge list_of_edges
			;

edge :			targetnode_nr
			{
				current_edge_attributes = get_edge_style (
					NORMAL_EDGE_STYLE);
				current_el_head = (Edgeline)NULL;
				current_edgeline = current_el_head;

				current_attributes = NULL;
			}
			edge_attributes
			{
				if (current_el_head == empty_edgeline) {
					current_el_head = new_edgeline(
						node_x(current_edge->source),
						node_y(current_edge->source));
					current_edgeline = add_to_edgeline(
						current_el_head,
						node_x(current_edge->target),
						node_y(current_edge->target));
				}

			  	edge_set (current_edge, ONLY_SET,
			  		SET_EDGE_ATTRIBUTES (current_edge_attributes),
			  		EDGE_LINE, current_el_head,
			  		0);

				/* print_file_attributes (stderr, current_attributes); */
				current_edge->file_attrs = current_attributes;

			}
			edgelabel
			{
				current_edge = empty_edge;
			}
			;

targetnode_nr :		T_NUMBER
			{
				current_edge = create_edge (current_node,
					get_node_with_number (current_graph, $1));
			}
			;

edgelabel :		/* nothing or ... */
			|
			T_IDENTIFIER
			{
				if (strcmp ($1, "")) {
					edge_set (current_edge, ONLY_SET, EDGE_LABEL, $1, 0);
				}
			}
			;


/************************************************************************/
/*									*/
/*			EDGE ATTRIBUTES					*/
/*									*/
/************************************************************************/


edge_attributes:		/* nothing or ... */
			|
			T_BEGIN_GRAPHED_ATTRIBUTES_DELIMETER
			list_of_edge_attributes
			T_END_GRAPHED_ATTRIBUTES_DELIMETER
			edge_attributes
			;

list_of_edge_attributes :
			| any_edge_attribute list_of_edge_attributes
			;

any_edge_attribute :	  T_EDGETYPE_INDEX edgetype_index
			| T_EDGEFONT_INDEX edgelabel_font_index
			| T_EDGELINE_POINTS edgeline
			| T_EDGELABEL_VISIBILITY edgelabel_visibility
			| T_ARROWLENGTH arrowlength
			| T_ARROWANGLE arrowangle
			| T_COLOR edgecolor
			| unknown_attribute
			;

edgetype_index :	T_NUMBER
			{
				if (is_legal_edgetype_index($1)) {
					current_edge_attributes.type_index =
						edgetypeindex_translation[$1];
				} else {
					yywarning ("Illegal edgetype_index");
				}
			}
			;

edgelabel_font_index :	T_NUMBER
			{
				if (is_legal_font_index($1)) {
					current_edge_attributes.font_index =
						fontindex_translation[$1];
				} else {
				    yywarning ("Illegal font_index");
				}
			}
			;

edgelabel_visibility :	T_NUMBER
			{
				current_edge_attributes.label_visibility = $1;
			}

arrowlength :		T_NUMBER
			{
				if (is_positive ($1))
					current_edge_attributes.arrow_length =
						$1;
				else {
					yywarning ("Illegal arrowlength");
				}
			}

arrowangle :		T_NUMBER
			{
				current_edge_attributes.arrow_angle =
					deg_to_rad ($1);
			}

edgecolor :		T_NUMBER
			{
				if (is_legal_color ($1)) {
					current_edge_attributes.color = $1;
				}
			}


edgeline :		{
				current_edgeline = (Edgeline)NULL;
				current_el_head = (Edgeline)NULL;
			}
			edgeline_points
			;

edgeline_points :	  edgeline_point
			| edgeline_point edgeline_points
			;

edgeline_point :	T_NUMBER T_NUMBER
			{
				if (current_edgeline == (Edgeline)NULL) {
					current_el_head  = new_edgeline($1,$2);
					current_edgeline = current_el_head;
				} else {
					current_edgeline = add_to_edgeline (
						current_edgeline, $1, $2);
				}
			}
			;


/************************************************************************/
/*									*/
/*			LIST OF FONTS					*/
/*									*/
/************************************************************************/


old_fontlist :		  old_font
			| old_font old_fontlist
			;

old_font :		T_IDENTIFIER T_IDENTIFIER
			{
			    int	 i;
			    char buffer[FILENAMESIZE];
			
			    if ((i = add_font($1,$2)) != -1) {
				fontindex_translation [font_count] = i;
				any_font_successfully_loaded = TRUE;
			    } else {
				sprintf (buffer, "Can't get font %s\n", $1);
				yywarning (buffer);
				fontindex_translation [font_count] = 0;
			    }
			    font_count++;
			}
			;

new_font :		T_SCANNER_FONT T_NUMBER T_IDENTIFIER T_IDENTIFIER
			{
			    int	 i;
			    char buffer[FILENAMESIZE];
			
			    if ((i = add_font($3,$4)) != -1) {
				fontindex_translation [$2] = i;
				any_font_successfully_loaded = TRUE;
			    } else {
				sprintf (buffer, "Can't get font %s\n", $3);
				yywarning (buffer);
				fontindex_translation [$2] = 0;
			    }
			    font_count++;
			}
			;




/************************************************************************/
/*									*/
/*			LIST OF NODETYPES				*/
/*									*/
/************************************************************************/


old_nodetypelist :	  old_nodetype
			| old_nodetype old_nodetypelist
			;

old_nodetype :		T_IDENTIFIER
			{
			    int i;
			    char buffer[FILENAMESIZE];
			
			    if ((i = add_nodetype($1)) != -1) {
			        nodetypeindex_translation [nodetype_count] = i;
				any_nodetype_successfully_loaded = TRUE;
			    } else {
				sprintf (buffer,"Can't get nodetype %s\n",$1);
			        yywarning (buffer);
			        nodetypeindex_translation [nodetype_count] = 0;
			    }
			    nodetype_count++;
			}
			;

new_nodetype :		T_SCANNER_NODETYPE T_NUMBER T_IDENTIFIER
			{
			    int i;
			    char buffer[FILENAMESIZE];
			
			    if ((i = add_nodetype($3)) != -1) {
			        nodetypeindex_translation [$2] = i;
				any_nodetype_successfully_loaded = TRUE;
			    } else {
				sprintf (buffer,"Can't get nodetype %s\n",$3);
			        yywarning (buffer);
			        nodetypeindex_translation [$2] = 0;
			    }
			    nodetype_count++;
			}
			;


/************************************************************************/
/*									*/
/*			LIST OF EDGETYPES				*/
/*									*/
/************************************************************************/


old_edgetypelist :	  old_edgetype
			| old_edgetype old_edgetypelist
			;

old_edgetype :		T_IDENTIFIER
			{
			    int  i;
			    char buffer[FILENAMESIZE];
			
			    if ((i = add_edgetype($1)) != -1) {
			        edgetypeindex_translation [edgetype_count] = i;
				any_edgetype_successfully_loaded = TRUE;
			    } else {
				sprintf (buffer, "Can't get edgetype %s", $1);
			        yywarning (buffer);
			        edgetypeindex_translation [edgetype_count] = 0;
			    }
			    edgetype_count++;
			}
			;

new_edgetype :		T_SCANNER_EDGETYPE T_NUMBER T_IDENTIFIER
			{
			    int  i;
			    char buffer[FILENAMESIZE];
			
			    if ((i = add_edgetype($3)) != -1) {
			        edgetypeindex_translation [$2] = i;
				any_edgetype_successfully_loaded = TRUE;
			    } else {
				sprintf (buffer, "Can't get edgetype %s", $3);
			        yywarning (buffer);
			        edgetypeindex_translation [$2] = 0;
			    }
			    edgetype_count++;
			}
			;


/************************************************************************/
/*									*/
/*			GRAPH STATE LIST				*/
/*									*/
/************************************************************************/


graph_state_list :	any_graph_states
			;

any_graph_states :	  /* nothing or ... */
			| any_graph_state any_graph_states
			;

any_graph_state :	  T_DIR current_directedness
			| T_GRAGRA gragra
			| T_GLOBAL_EMBEDDING global_embedding
			| T_EMBED_MATCH_ATTR embed_match_attributes
			| T_EDGELABEL_SIZE current_edgelabel_size
			| nodestyle
			| edgestyle
			;

current_directedness :	T_NUMBER
			{
				if (overwrite_state) {
					set_current_directedness (
						int_to_bool($1));
				}
			}


gragra :		T_NUMBER T_IDENTIFIER T_IDENTIFIER
			{
				if (is_legal_gragra_type ($1)) {
					set_current_gragra_type ($1);
				} else {
					yywarning ("Illegal graph grammar type");
				}
				
				set_current_gragra_nonterminals ($2);
				set_current_gragra_terminals    ($3);
			}

global_embedding :	T_IDENTIFIER
			{
				set_global_embedding_name ($1);
			}

embed_match_attributes : T_NUMBER
			{
				set_embed_match_attributes ((unsigned)$1);
			}

current_edgelabel_size:	T_NUMBER T_NUMBER
			{
				if (is_legal_size ($1, $2))
					set_current_edgelabelsize ($1, $2);
				else {
					yywarning ("Illegal current_edgelabelsize");
				}
			}
			;

nodestyle :		T_NODESTYLE T_IDENTIFIER
			{
				current_node_attributes =
					get_node_style (NORMAL_NODE_STYLE);
			}
			node_attributes_list
			{
				if (overwrite_state && !strcmp($2, "normal")) {
					set_node_style (NORMAL_NODE_STYLE,
						current_node_attributes);
				
					set_current_nodesize (
						current_node_attributes.width,
						current_node_attributes.height);
					set_current_nodetype (
						current_node_attributes.type_index);
					set_current_nodefont (
						current_node_attributes.font_index);
					set_current_nodelabel_placement (
						current_node_attributes.nodelabel_placement);
					set_current_node_edge_interface (
						current_node_attributes.node_edge_interface);
					set_current_nodelabel_visibility (
						current_node_attributes.label_visibility);
					set_current_nodecolor (
						current_node_attributes.color);
				}

				if (overwrite_state && !strcmp($2, "gragra left side")) {
					set_node_style (LEFT_SIDE_NODE_STYLE,
						current_node_attributes);
				}

				if (overwrite_state && !strcmp($2, "gragra embed node")) {
					set_node_style (EMBED_NODE_STYLE,
						current_node_attributes);
				}
			}

node_attributes_list:	  '[' list_of_node_attributes ']'	/* New Style */
			| list_of_node_attributes ','		/* Old Style */


edgestyle :		T_EDGESTYLE T_IDENTIFIER
			{
				current_edge_attributes =
					get_edge_style (NORMAL_EDGE_STYLE);
			}
			edge_attributes_list
			{
				if (overwrite_state && !strcmp ($2, "normal")) {
					set_edge_style (NORMAL_EDGE_STYLE,
						current_edge_attributes);
							
					set_current_arrowlength (
						current_edge_attributes.arrow_length);
					set_current_arrowangle (
						current_edge_attributes.arrow_angle);
					set_current_edgetype (
						current_edge_attributes.type_index);
					set_current_edgefont (
						current_edge_attributes.font_index);
					set_current_edgelabel_visibility (
						current_edge_attributes.label_visibility);
					set_current_edgecolor (
						current_edge_attributes.color);
				}

				if (overwrite_state && !strcmp ($2, "gragra embed edge")) {
					set_edge_style (EMBED_EDGE_STYLE,
						current_edge_attributes);
				}
			}
			;

edge_attributes_list:	  '[' list_of_edge_attributes ']'	/* New Style */
			| list_of_edge_attributes ','		/* Old Style */


/************************************************************************/
/*									*/
/*			GRAPHED STATE LIST				*/
/*									*/
/************************************************************************/


graphed_state_list :	
			| any_graphed_state graphed_state_list
			;

any_graphed_state :	  T_WORKING_AREA_SIZE working_area_size
			| T_WORKING_AREA_WINDOW working_area_window_size
			| T_SCROLL_OFFSET scroll_offset
			| T_GRIDWIDTH gridwidth
			;

working_area_size :	T_NUMBER T_NUMBER
			{
				if (is_positive($1) && is_positive ($2)) {
					set_buffer_size (load_buffer, $1,$2);
					if (graphed_state.startup) {
						graphed_state.default_working_area_canvas_width  = $1;
						graphed_state.default_working_area_canvas_height = $2;
					}
				} else {
					yywarning ("Illegal working_area_size");
				}
			}
			;

working_area_window_size :	T_NUMBER T_NUMBER T_NUMBER T_NUMBER
			{
				if (is_positive($1) && is_positive ($2) && is_positive($3) && is_positive ($4)) {
					set_canvas_window (load_buffer, $1,$2, $3,$4);
					if (graphed_state.startup) {
						graphed_state.default_working_area_window_width  = $3;
						graphed_state.default_working_area_window_height = $4;
					}

				} else {
					yywarning ("Illegal working_area_window");
				}
			}
			;

scroll_offset :		T_NUMBER T_NUMBER
			{
				/* Changed MH 8/12/93 */

				if (is_positive($1) && is_positive ($2)) {
					if (canvases[load_buffer].canvas_seen_by_working_area_event_proc) {
						scroll_buffer (load_buffer, $1,$2);
					} else {
						canvases[load_buffer].startup_scroll_x = $1;
						canvases[load_buffer].startup_scroll_y = $2;
					}
				} else {
					yywarning ("Illegal scroll_offset");
				}
			}
			;

gridwidth :		T_NUMBER
			{
				if (is_positive($1)) {
					show_grid (load_buffer, $1);
				} else {
					yywarning ("Illegal gridwidth");
				}
			}
			;
/************************************************************************/
/*									*/
/*		GRAPHED INITIALIZATION FILE FORMAT			*/
/*									*/
/************************************************************************/

initialisation :	T_INIT
			{
				font_count     = 0;
				nodetype_count = 0;
				edgetype_count = 0;
			  	any_font_successfully_loaded     = FALSE;
			  	any_nodetype_successfully_loaded = FALSE;
			  	any_edgetype_successfully_loaded = FALSE;
			}
			old_fontlist ';'
			{
				if (!any_font_successfully_loaded) {
					yacc_error ("No initial fonts found");
				}
			}
			old_nodetypelist ';'
			{
				if (!any_nodetype_successfully_loaded) {
					yacc_error ("No initial nodetypes found");
				}
			}
			old_edgetypelist ';'
			{
				if (!any_edgetype_successfully_loaded) {
					yacc_error ("No initial edgetypes found");
				}
			}
			graph_state_list ';'
			graphed_state_list
			;

			|
			T_INIT
			{
				font_count     = 0;
				nodetype_count = 0;
				edgetype_count = 0;
			  	any_font_successfully_loaded     = FALSE;
			  	any_nodetype_successfully_loaded = FALSE;
			  	any_edgetype_successfully_loaded = FALSE;
			}
			new_style_graph_attributes
			{
				if (!any_font_successfully_loaded) {
					yacc_error ("No initial fonts found");
				}
				if (!any_nodetype_successfully_loaded) {
					yacc_error ("No initial nodetypes found");
				}
				if (!any_edgetype_successfully_loaded) {
					yacc_error ("No initial edgetypes found");
				}
			}
			;

/************************************************************************/
/*									*/
/*			GENERAL ATTRIBUTES				*/
/*									*/
/************************************************************************/


any_attributes :	
			{
				$$ = NULL;
			}
			| any_attribute any_attributes
			{
				$1->next = $2;
				$$ = $1;
			}
			;

unknown_attribute :	any_attribute
			{
				File_attributes	a;

				a = current_attributes;
				if (a != NULL) {
					while (a->next != NULL) {
						a = a->next;
					}
					a->next = $1;
				} else {
					current_attributes = $1;
				}
			}
			;

any_attribute :		T_KEY value
			{
				File_attributes	a;

				a = new_file_attributes ();
				a->kind = FILE_KEY;
				a->value.key.name = $1;
				a->value.key.values = $2;
				a->next = NULL;

				$$ = a;
			}
			;

value :			/* nothing */
			{
				$$ = NULL;
			}
			|
			T_NUMBER value
			{
				File_attributes	a;

				a = new_file_attributes ();
				a->kind = FILE_NUMBER;
				a->value.number = $1;
				a->next = $2;

				$$ = a;
			}
			|
			T_FLOATNUMBER value
			{
				File_attributes	a;

				a = new_file_attributes ();
				a->kind = FILE_NUMBER;
				a->value.floatnumber = $1;
				a->next = $2;

				$$ = a;
			}
			|
			T_IDENTIFIER value
			{
				File_attributes	a;

				a = new_file_attributes ();
				a->kind = FILE_STRING;
				a->value.string = $1;
				a->next = $2;

				$$ = a;
			}
			|
			'[' any_attributes ']' value
			{
				File_attributes	a;

				a = new_file_attributes ();
				a->kind = FILE_LIST;
				a->value.list = $2;
				a->next = $4;

				$$ = a;
			}
			;

/************************************************************************/
/*									*/
/*			DERIVATION SEQUENCE				*/
/*									*/
/************************************************************************/


start_load_derivation_sequence :
			T_START_LOAD_DERIVATION_SEQUENCE
			{
				current_derivation_sequence = ds_create ();
			}
			derivation_sequence
			T_END
			{
				/* ds_print (stderr, current_derivation_sequence); */
				last_loaded_derivation_sequence = current_derivation_sequence;
			}
			;

derivation_sequence :	/* empty or */
			| use_gragra derivation_sequence
			| startnode  derivation_sequence
			| apply_production  derivation_sequence
			;

use_gragra :		T_USEGRAGRA T_IDENTIFIER
			{
				ds_add_file (current_derivation_sequence, $2);
			}
			;

startnode :		T_STARTNODE T_IDENTIFIER
			{
				ds_set_startnode (current_derivation_sequence, $2);
			}
			;

apply_production :
			T_NUMBER
			T_APPLY
			{
				current_ggtaf = ggtaf_create();
				ggtaf_set_number (current_ggtaf, $1);
			}
			node_if_verbose T_IDENTIFIER
			{
				ggtaf_set_node (current_ggtaf, $5);

			}
			production_if_verbose T_IDENTIFIER
			{
				ggtaf_set_production (current_ggtaf, $8);
			}
			derivation_history_list
			{
				ds_add_apply_form (current_derivation_sequence, current_ggtaf);
			}
			;

derivation_history_list : /* empty or */
			| '[' node_if_verbose T_IDENTIFIER ',' production_if_verbose T_IDENTIFIER ']'
			{
				ggtaf_add_node_history (current_ggtaf, $3, $6);
			}
			  derivation_history_list
			;


node_if_verbose :	| T_NODE
			;
production_if_verbose :	| T_PRODUCTION
			;
%%/**********************************************************************/
/*									*/
/*			YACC-FEHLERMELDUNGEN				*/
/*									*/
/************************************************************************/
/*									*/
/*	int	yyerror (s)						*/
/*									*/
/*	Gibt ueber error die Fehlermeldung aus s mit Angabe der		*/
/*	Zeilennummer aus.						*/
/*	Der bisher eingelesene Graph muss leider wieder geloescht	*/
/*	werden, da bei den Knoten, die als Folge von Vorwaerts-		*/
/*	referenzen in der Liste der Kanten "zu frueh" erzeugt wurden,	*/
/*	keine Attribute gesetzt wurden (-> Inkonsistenzen !).		*/
/*									*/
/*----------------------------------------------------------------------*/
/*									*/
/*	int	yywarning (s)						*/
/*									*/
/*	Bibt ueber warning die Warnung aus s mit Angabe der Zeilen-	*/
/*	nummer aus.							*/
/*	Nach yywarning ist Weiterarbeiten moeglich.			*/
/*									*/
/************************************************************************/


static	void	yyerror (char *string)
{
	error ("line %d :\n%s\n", lex_input_file_linenumber, string);
	
	if (graph_loading) {
		/* These lines have to be consistent with end_load_graph */
		graph_loading = FALSE;
	}
	
	delete_graphs_in_buffer (load_buffer);
	reset_buffer_has_changed (load_buffer);
}



static	void	yywarning (char *string)
{
	warning ("line %d :\n%s\n", lex_input_file_linenumber, string);
}


/************************************************************************/
/*									*/
/*			HILFSPROZEDUREN					*/
/*									*/
/************************************************************************/


static	void	start_load_graph (void)
{
	graph_loading = TRUE;
	group_of_current_graph = empty_group;
	
	current_graph = empty_graph;
}


static	void	end_load_graph (void)
{
	if (group_of_current_graph != empty_group) {
		group_set  (group_of_current_graph, RESTORE_IT, 0);
		free_group (group_of_current_graph);
	}
	
	reset_graph_has_changed (current_graph);
	
	graph_loading = FALSE;
}
