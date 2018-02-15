#ifndef __LP_GRAPH_H__
#define __LP_GRAPH_H__
/************************************************************************/
/*									*/
/*	Datenstrukturen fuer Area-Optimierung: Extern			*/
/*	Datenstrukturen fuer Reduction-System: Extern			*/
/*									*/
/************************************************************************/

#include "lpa_graph.h"
#include "lpr_graph.h"

/******************** Exports der Funktionen, die im Orginal graphed aufgerufen werden ******/
#include "lp_lgg_to_graphed.h"

/****************************************************************************************/
/*											*/
/*	Einige Konstanten und Enumerationen			 			*/
/*											*/
/****************************************************************************************/

#define L_side		0
#define U_side		1
#define R_side		2
#define D_side		3

#define L_dir		0
#define U_dir		1
#define R_dir		2
#define D_dir		3

#define UNDEFINED	-1

/*************************************************************************
Type		: enum lgg_Preconditions
Description	: Struktur zur Einteilung der Voraussetzungen, die ein Layout-
		  algorithmus an eine Layout-Graph-Grammatik stellt. 
		  WICHTIG: Bei n Neueintragungen muss PRECONDITION_COUNTER um 
		  n erh"oht werden.
*************************************************************************/
typedef enum
{
	BNLC_GRAMMAR,					/* No Connected nonterminals 				*/	
	DIFF_EMBEDDINGS,				/* No difference between the embedding edges		*/
	RECTANGULAR_EDGELINES,				/* Tja							*/
	GRID_LAYOUT,					/* grid layout production				*/
	UNIT_LAYOUT,					/* unit layout production				*/
	INTERSECTING_LINE,				/* Line does intersect node				*/
	LINE_OUT_OF_PROD,				/* Line is getting out of production 			*/
	DEGENERATED_EDGE,				/* Edge is degenerated					*/
	CONFLUENT,
	NEIGHBORHOODPRESERVING,
	SINGLE_SIDED,
	BORDER_GAP_FULLFILLED,				/* Hat alles von der Box Mindestabstand			*/
	NODE_DISTANCE,					/* Beruehren sich Knoten				*/
	EDGE_OVERLAPPING,				/* Zwei Kanten liegen uebereinander 			*/
	NON_TERM_UNIT,	
	ENCE_1_GRAMMAR,
	SIMPLE_EMBEDDINGS,
	PRECONDITION_COUNTER				/* Dummy fuer Laenge					*/
}
	lgg_Preconditions;	 			/* Acht Preconditions in lgg_Preconditions		*/

/******************************************************************
top_sort_rec_type

	used in top_sort_ref and tree_top_sort_ref
	to see * ref is pointing to a lp_edgeline
	       * ref is pointing to a node
******************************************************************/

typedef enum
{
	LP_NODE, 
	LP_EDGELINE
}
	top_sort_rec_type;

/***************************************************************
Tree_rec_type

	Used in tree_ref to see if the corresponding element in
	the Production is a node or an edge
***************************************************************/

typedef enum
	{
		TREE_NODE,
		HISTORY_ELEM
	}  Tree_rec_type;

/***************************************************************
Tree_edge_rec_type

	Used in tree_edge_ref to see of what kind the corresponding 
	edge in the production is
***************************************************************/

typedef enum
	{
		RHS_EDGE,
		IN_CONN_REL,
		OUT_CONN_REL
	}  Tree_edge_rec_type;


/************************************************************************/
/*	Eine Kopie einer edgeline um darauf rumzurechnen 		*/
/************************************************************************/

typedef	struct	lp_edgeline
{
	int				x, y;
	struct lp_edgeline		*pre;
	struct lp_edgeline 		*suc;
	struct tree_lp_edgeline		*iso;
}
	*lp_Edgeline;

#define	for_lp_edgeline(el_head,el) \
	{ if (((el) = (el_head)) != (lp_Edgeline)NULL) do {
#define	end_for_lp_edgeline(el_head,el) \
	} while (((el) = (el)->suc) != (el_head)); }

#include "lp_test.h"

/****************************************************************************************/
/*	top_sort_ref									*/
/****************************************************************************************/

/******************************************************************
reference

	used in top_sort_ref to have the possibility to point to a
	node or lp_edgeline
******************************************************************/

typedef	union
	{
		struct node		*node;
		struct lp_edgeline 	*lp_edgeline;
	} reference;

/******************************************************************
top_sort_ref

	used in every production to have a topological sorting
	of all elements of the production in x- and y-direction
******************************************************************/

typedef struct	top_sort_rec
{
	struct top_sort_rec		*next_x;	/* pointer to next top_sort_ref in x_sorting 		*/
	struct top_sort_rec		*next_y;	/* pointer to next top_sort_ref in y_sorting 		*/
	struct top_sort_rec		*first_x;	/* pointer to first top_sort_ref with same x_coordinat 	*/
	struct top_sort_rec		*first_y;	/* pointer to first top_sort_ref with same y_coordinat 	*/
	struct tree_top_sort_rec 	*iso;		/* pointer to corresponding sorting in derivation net	*/
	top_sort_rec_type		type;		/* type of element to whitch ref points			*/
	reference			ref;		/* pointer to element in production			*/
}	*top_sort_ref;

/******************************************************************
Makros for going through top_sort_ref in x and y sorting
******************************************************************/

#define	for_top_sort_ref_x(ts_head, ts)  \
	{ if ( ((ts) = (ts_head)) != (top_sort_ref)NULL ) do {
#define end_for_top_sort_ref_x(ts_head, ts)  \
	} while( ((ts) = (ts)->next_x ) != (ts_head) ); }

#define	for_top_sort_ref_y(ts_head, ts)  \
	{ if ( ((ts) = (ts_head)) != (top_sort_ref)NULL ) do {
#define end_for_top_sort_ref_y(ts_head, ts)  \
	} while( ((ts) = (ts)->next_y ) != (ts_head) ); }

/****************************************************************************************/
/*	tree_top_sort_ref								*/
/****************************************************************************************/

/******************************************************************
tree_lp_edgeline_ref

	used in tree top sort ref to have the possibility to from
	the topological sorting of the derivation net to the
	corresponding lp_edgeline point in the production, in the
	graph or to ?????????????????????????????
******************************************************************/

typedef	struct	tree_lp_edgeline
{
	int				x, y;		/* coord. of lp_edgeline point				*/
	struct tree_lp_edgeline		*pre;		/* pre of this lp_edgeline point			*/
	struct tree_lp_edgeline 	*suc;		/* suc of this lp_edgeline point			*/
	struct lp_edgeline		*prod_iso;	/* pointer to corresponding lp_edgeline in production	*/
	struct tree_top_sort_rec	*tree_iso;	/* ????????????????????					*/
}
	*tree_lp_edgeline_ref;

/******************************************************************
tree_reference

	used in tree_top_sort_ref to point either to a tree_node_rec
	or to a tree_lp_edgeline
******************************************************************/

typedef	union
	{
		struct tree_node_rec		*node;
		struct tree_lp_edgeline 	*tree_lp_edgeline;
	} tree_reference;

/******************************************************************
tree_top_sort_ref

	used to have a topological sorting in the derivation net
	of ????????????????????????
******************************************************************/

typedef struct	tree_top_sort_rec
{
	struct tree_top_sort_rec	*next_x;	/* pointer to next tree_top_sort_ref in x-sorting 	*/
	struct tree_top_sort_rec	*next_y;	/* pointer to next tree_top_sort_ref in y-sorting 	*/
	struct tree_top_sort_rec	*first_x;	/* pointer to first tree_top_sort_ref with same x-coord.*/
	struct tree_top_sort_rec	*first_y;	/* pointer to first tree_top_sort_ref with same y-coord.*/
	struct top_sort_rec		*iso;		/* pointer to corresponding top_sort_ref of a production*/
	top_sort_rec_type		type;		/* type of element to whitch ref points			*/
	tree_reference			ref;		/* pointer to corresponding element			*/

	/************************/
	/*	ATTRIBUTES	*/
	/************************/

	int	mL, mLS, mB, mBS;

}	*tree_top_sort_ref;

/**************************************************************
history_ref

	Is part of every edge in a graph, to see which edges has
	been before this edge
**************************************************************/

/* Kantengeschichte */
typedef struct  history_rec
{
	struct history_rec	*pre;
	struct history_rec	*suc;
	struct tree_rec		*element;
}
	*history_ref;


#define	for_history_ref(hs_head, hs) 			 \
	{ if ( ((hs) = (hs_head)) != (history_ref)NULL ) do {
#define end_for_history_ref(hs_head, hs)  \
	} while( ((hs) = (hs)->suc ) != (hs_head) ); }

/******************************************************************
dir_ref

	used in the derivation net as the direction sequence
	for every embedding edge
******************************************************************/

typedef	struct dir_rec
{
	int		dir;
	struct dir_rec	*pre;
	struct dir_rec 	*suc;
}	*dir_ref;

/******************************************************************
side_ref

	used in the derivation net as the side sequence
	for every embedding edge
******************************************************************/

typedef	struct side_rec
{
	int		side;
	struct side_rec	*pre;
	struct side_rec *suc;
}	*side_ref;


#define	for_dir_ref(hs_head, hs)  \
	{ if ( ((hs) = (hs_head)) != (dir_ref)NULL ) do {
#define end_for_dir_ref(hs_head, hs)  \
	} while( ((hs) = (hs)->suc ) != (hs_head) ); }

#define	for_side_ref(hs_head, hs)  \
	{ if ( ((hs) = (hs_head)) != (side_ref)NULL ) do {
#define end_for_side_ref(hs_head, hs)  \
	} while( ((hs) = (hs)->suc ) != (hs_head) ); }

/*****************************************************************
interval_rec

	The left and right point on a track, whitch one embedding 
	edge needs
*****************************************************************/

typedef	struct interval_rec
{
	int		left, right;
}	
interval_rec;

/*****************************************************************
conn_ref

	List of corresponding nodes to connection relations in
	the derivation net used ???????????????
*****************************************************************/

typedef	struct conn_rec
{
	struct tree_edge_rec	*conn_rel;
	struct conn_rec		*pre;
	struct conn_rec 	*suc;
}	*conn_ref;


#define	for_conn_ref(hs_head, hs)  \
	{ if ( ((hs) = (hs_head)) != (conn_ref)NULL ) do {
#define end_for_conn_ref(hs_head, hs)  \
	} while( ((hs) = (hs)->suc ) != (hs_head) ); }

/**************************************************************
level_ref

	???????????????????
**************************************************************/

typedef	struct level_rec
{
	struct conn_rec		*conn_rels;
	int			level_number;
	struct level_rec	*pre;
	struct level_rec	*suc;
}	*level_ref;

/*************************************************************
history_edge_ref

*************************************************************/

/* Kante im Ableitungsnetz */
typedef struct 	history_edge_rec
{
	struct tree_rec	*source;
	struct tree_rec	*target;
	struct history_edge_rec	*out_pre;
	struct history_edge_rec	*out_suc;
	struct history_edge_rec	*in_pre;
	struct history_edge_rec	*in_suc;
	struct edge		*graphed_iso;

	/************************/
	/*	ATTRIBUTE 	*/
	/************************/

	int	edge_split_nr;
}
	*history_edge_ref;
 
/******************************************************************
tree_node_ref

	corresponding node in the derivation net to a graph node
******************************************************************/

typedef struct 	tree_node_rec
{
	struct tree_rec 		*first_son;			/* Wie gehts nach unten weiter */
	struct node			*prod_iso;			/* Entsprechung in der Produktion */
	struct graph			*used_prod;			/* Wenn abgeleitet mit welcher Produktion */
	struct tree_top_sort_rec	*first_x;
	struct tree_top_sort_rec	*first_y;
	struct node			*graph_iso;			/* Entsprechung im Graphen */

	struct multi_edge		*multi_edges;			/* Elements for multi_lp */

	/* Attributes */
	struct list_of_multi_lp		*LP_costs;
	int				LHS_costs;
	int				LP_COSTS;
	struct lp_set			*LP_set;
	


	/***********************************************************************************
	BEACHTE: Ist eine Produktion A im i-ten Arrayfeld abgelegt, so muss gelten:
		Bei allen Soehnen sind die zur Produktion A gehoerenden Knoten
			ebenfalls im i-ten Arrayfeld abgelegt.
	***********************************************************************************/

	struct	lpa_array_of_productions	*possible_productions;	/* Wir brauchen ein array der isomorphen, hier anwendbaren Produktionen */
									/* mit allen notwendigen Verwaltungsinformationen */
	struct	lpa_array_of_nodes		*possible_nodes;	/* Array aller Knoten, die aufgrund isomorpher Produktionen hier ein- */
									/* gesetzt werden koennten. */
	struct	lpa_upper_prod_array		*area_structures;	/* Datenstrukturen, die zum berechnen des area-optimalen Layout */
									/* notwendig sind; */
	struct	node				*new_graph_node;	/* Knoten im neu erzeugten Graph */

	/************************/
	/* HIERARCHY		*/
	/************************/

	int	flag;
	int	big;
	int	leaf;	

	/************************/
	/*	ATTRIBUTE 	*/
	/************************/

	int	 	orientation_type_costs[9];
	int		orientation_type;
	int		max_ord[4];
	conn_ref	tsncr[4];
	level_ref	tncr[4];
	int		track_quantity[4];
	int		W, H;
	int		W1, H1;
	int		w, h;
	int		x1, y1, x2, y2;

}	
	*tree_node_ref;

/*********************************************************************************
tree_dege_ref

	Corresponding node in the derivation net to every edge
	of a graph
*********************************************************************************/

typedef struct 	tree_edge_rec
{
	Tree_edge_rec_type	type;		/* type of corresponding edge			*/
	struct edge		*prod_iso;	/* pointer to corresponding edge in production	*/
	struct history_edge_rec	*out_edges;	/* Geschichte unterhalb 			*/
	struct history_edge_rec *in_edges;	/* Geschichte oberhalb 				*/
	struct tree_lp_edgeline	*tree_line;	/* corresponding element for edgeline in tree	*/
	struct tree_rec		*target;	/* */

	/************************/
	/*	ATTRIBUTE 	*/
	/************************/

	int			split_nr;
	int			split[4];
	int			source_dir;
	int			target_dir;
	dir_ref			directions[4];
	side_ref		sides[4];
	int			lp_ord;
	int			clock_wise[4];
	interval_rec		interval[4][4];
	interval_rec		track_interval[4];
	int			track_number[4];
	struct lp_edgeline	*line;

}	
	*tree_edge_ref;

#define	for_net_out_edges(e_head, e)  \
	{ if ( ((e) = (e_head)) != (history_edge_ref)NULL ) do {
#define end_for_net_out_edges(e_head, e)  \
	} while( ((e) = (e)->out_suc ) != (e_head) ); }

#define	for_net_in_edges(e_head, e)  \
	{ if ( ((e) = (e_head)) != (history_edge_ref)NULL ) do {
#define end_for_net_in_edges(e_head, e)  \
	} while( ((e) = (e)->in_suc) != (e_head) ); }


/*********************************************************************************
tree_ref

	"head" of every node in the derivation net
*********************************************************************************/

typedef struct 	tree_rec
{
	/* Knoten- oder Kantenpunkt beschreibung */

	Tree_rec_type	tree_rec_type;		/* type of tree_rec				*/

	struct tree_rec		*father;	/* father in derivation net (derivated node)	*/
	struct tree_rec		*next_brother;	/* next element of the corresponding production	*/

	union
	{
		struct tree_node_rec	*node;		/* pointer to corresponding derivation	*/
		struct tree_edge_rec	*history_elem;	/* ... net structure			*/
	}
	tree_rec;

	int			hierarchy_level;/* level in derivation net			*/

	struct lp_of_father	*multi_edge;	/* pointer to multi elements upside		*/

}
	*tree_ref;

#define for_tree_rec(tr_head, tr)		\
	{(tr) = (tr_head);			\
	while( (tr) != (tree_ref)NULL ) {
#define end_for_tree_rec(tr_head, tr)	\
	;(tr) = (tr)->next_brother; }} 

/****************************************************************************************/
/* LGG Multi: BEGIN									*/
/****************************************************************************************/

typedef	struct	lp_set
{
	struct	list_of_multi_lp	*target;
	struct  lp_set			*next;
}	*Lp_set;

/*********************************************************************************
	Liste vom multi_lp
*********************************************************************************/

typedef	struct	list_of_multi_lp
{
	int			OPTIMAL_COSTS;	/* page 57, Def. 4.3.5 2), C*			*/
	int			OPTIMAL_ARRAY[9];
	struct multi_edge	*list;		/* first of corresponding productions		*/
	struct list_of_multi_lp	*next;		/* next brother					*/
	int			LP_costs;
	int			LP_COSTS;
}	*List_of_multi_lp;

#define for_lp_costs(lm_head, lm)			\
	{(lm) = (lm_head);				\
	while( (lm) != (List_of_multi_lp)NULL ) {
#define end_for_lp_costs(lm_head, lm)			\
	;(lm) = (lm)->next; }} 

/**********************************************************************************
	Liste aller multi_lp_listen vom Vater aus
**********************************************************************************/

typedef struct	multi_edge
{
	struct lp_of_father	*lps_of_father;	/* first of upper part of one derivation	*/
	struct multi_edge	*next;		/* next brother					*/
}	*Multi_edge;

#define for_multi_edge(me_head, me)			\
	{(me) = (me_head);				\
	while( (me) != (Multi_edge)NULL ) {
#define end_for_multi_edge(me_head, me)			\
	;(me) = (me)->next; }} 

/**********************************************************************************
	Liste aller Productionen aus LP_SET 
	mit Attributen 
**********************************************************************************/
 
typedef	struct	lp_of_son
{
	struct graph		*production;	/* corresponding production 			*/
	int			costs;		/* page 56, Def. 4.3.3, c  			*/
	struct list_of_multi_lp	*LP_COSTS;
	struct lp_of_son	*next;		/* next brother 				*/
	int			orientation_type_costs[9];
	int			orientation_type_set[8];
	int			lp_costs;
}	*Lp_of_son;

#define for_lower_part(lp_head, lp)			\
	{(lp) = (lp_head);				\
	while( (lp) != (Lp_of_son)NULL ) {
#define end_for_lower_part(lp_head, lp)			\
	;(lp) = (lp)->next; }} 

/**********************************************************************************
	Liste von lp_e_LP
	set*
	Liste aller Productionen ( lower part ), die minimale Kosten haben
**********************************************************************************/

typedef	struct	opt_lp_of_son
{
	struct lp_of_son	*target;	/* pointer to corresponding element		*/
	struct opt_lp_of_son	*next;		/* next brother					*/
}	*Opt_lp_of_son;

/**********************************************************************************
	Verweis auf Liste aller Produktionen
	aus einem Set
**********************************************************************************/

typedef	struct	lp_of_father
{
	struct graph		*production;	/* corresponding production 			*/
	struct lp_of_son	*LP_set;	/* lower part of multi_lp 			*/
	int			optimal_costs;	/* page 57, Def. 4.3.4 1), c*			*/
	struct opt_lp_of_son	*optimal_set;	/* page 57, Def. 4.3.5 1), set*			*/
	struct tree_node_rec	*father;	/* father in the derivation net 		*/
	struct tree_rec		*son;		/* son in the derivation net 			*/
	struct lp_of_father	*next;		/* next brother 				*/
	int			lp_set_costs;
	struct opt_lp_of_son	*lp_set;
}	*Lp_of_father;

#define for_upper_part(up_head, up)			\
	{(up) = (up_head);				\
	while( (up) != (Lp_of_father)NULL ) {
#define end_for_upper_part(up_head, up)			\
	;(up) = (up)->next; }} 


/********************************************************************************/
/*										*/
/*	LGG Parsing						*/
/*										*/
/********************************************************************************/


/********************************************************************************/

typedef	struct	lpp_parsing_element
{
	char*				label;			/* Name der zugehoerigen Produktion				*/
	struct	edge_list		*source_edges;		/* Kanten, die vom Parsing_element weggehen			*/
	struct	edge_list		*target_edges;		/* Kanten, die zum Parsing_element hingehen			*/
	struct	nodelist		*nodes;			/* Knoten des Graphen, die durch Parsing_element abgedeckt werden*/
	struct	derivation		*derivations;		/* Liste der Produktionen, mit denen das pe gebildet wird	*/
	int				hierarchy_level;	/* Auf welcher Stufe wurde pe geparst ( parsing )		*/
	struct	node			*graph_iso;		/* Wird gesetzt, wenn pe einen Terminalknoten repraesetiert	*/
	struct	tree_rec		*tree_ref_iso;		/* Entsprechender tree_ref im derivation_tree			*/
	int				has_big_nodelist;
	int				is_in_table;
	struct	lpp_parsing_element	*was_marked_by;

	/****** Attribute ******/

}	*lpp_Parsing_element;	/*** ACHTUNG:	LPP unbedingt noetig, da sonst Konflikt mit Lamshoft ***/

/********************************************************************************/

typedef	struct	derivation
{
	struct	set_of_parsing_elements	*derivation_nodes;	/* Liste der pe, die in der Produktion stecken			*/
	struct	graph			*used_prod;		/* Zeiger auf die zugehoerige Produktion			*/
	struct	derivation		*next;			/* naechstes Element der Liste					*/
	struct	lpp_parsing_element	*pe;
	int				is_in_table;
	/****** Attribute ******/

	struct attributes_head		*attributes_table_down;	/* Table mit dieser derivation als e (Theorie Seite 4ff.)	*/
	struct	attributes_head		*attributes_table_up;	/* Table mit dieser derivation als e' (Theorie Seite 4ff.)	*/

}	*Derivation;

#define	for_derivation( de_head, cur )			\
	{ (cur) = (de_head);				\
	while( (cur) != (Derivation)NULL ) {
#define	end_for_derivation( de_head, cur )		\
	(cur) = (cur)->next; }}

/********************************************************************************/

typedef	struct	set_of_parsing_elements
{
	struct	lpp_parsing_element	*pe;			/* Zeiger auf das zugehoerige pe				*/
	struct  node			*production_iso;	/* Entsprechender Knoten in der verwendeten Produktion		*/
	struct	tree_rec		*tree_ref_iso;		/* Entsprechender tree_ref im derivation_tree			*/
	struct	set_of_parsing_elements	*next;			/* Nachfolger in der Liste					*/

	/****** Attribute ******/

}	*Set_of_parsing_elements;


#define	for_set_of_parsing_elements( pe_head, pe )	\
	{(pe) = (pe_head);				\
	while( (pe) != (Set_of_parsing_elements)NULL ) {
#define	end_for_set_of_parsing_elements( pe_head, pe )	\
	(pe) = (pe)->next; }}

/********************************************************************************/

typedef	struct	nodelist
{
	struct	node			*node;			/* Zeiger auf Graphed-Knoten ausserhalb				*/
	struct	edge			*edge;			/* Entsprechende Kante dieser Einbettungsregel			*/
	struct	nodelist		*next;			/* Naechstes Element						*/
}	*Nodelist;


#define	for_nodelist( nl_head, cur )			\
	{(cur) = (nl_head);				\
	while( (cur) != (Nodelist)NULL ) {
#define end_for_nodelist( nl_head, cur )		\
	(cur) = (cur)->next; }}

/********************************************************************************/

typedef	struct	set_of_nodelist
{
	struct	nodelist		*list;			/* Zeiger auf Knoten ausserhalb der Produktion			*/
	int				is_in_embedding;
	char*				edgelabel;		/* label der Kante						*/
	char*				nodelabel;		/* label des Knoten ausserhalb					*/
	struct	set_of_nodelist		*next;
}	*Set_of_nodelist;

#define	for_set_of_nodelist( nl_head, cur )		\
	{(cur) = (nl_head);				\
	while( (cur) != (Set_of_nodelist)NULL) {
#define end_for_set_of_nodelist( nl_head, cur )		\
	(cur) = (cur)->next; }}

/********************************************************************************/

typedef	struct	int_list
{
	int				integer;
	struct	int_list		*next;
}	*Int_list;

#define	for_int_list( il_head, il )			\
	{(il) = (il_head);				\
	while( (il) != (Int_list)NULL) {
#define end_for_int_list( il_head, il )			\
	(il) = (il)->next; }}

/*******************************************************************************/

typedef	struct	edge_list
{
	struct	lpp_parsing_element	*source;
	struct	lpp_parsing_element	*target;
	char*				label;
	struct	edge			*production_iso;
	struct	edge_list		*next;
}	*Edge_list;

#define lpp_for_edgelist( el_head, el )			\
	{(el) = (el_head);				\
	while( (el) != (Edge_list)NULL) {
#define	end_lpp_for_edgelist( el_head, el )			\
	(el) = (el)->next; }}


/*******************************************************************************/

typedef	struct	list_of_set_of_pe
{
	struct	set_of_parsing_elements		*set;
	int					node_nr;
	struct	list_of_set_of_pe		*next;
}	*List_of_set_of_pe;

/********************************************************************************

	Datenstrukturen fuer die Attributberechnung beim Parsing

********************************************************************************/

/*******************************************************************************/

typedef	struct attributes_head
{
	int				is_same;		/* Marke, selbes pe, aber andere Ableitung			*/
	struct derivation		*upper_edge;		/* Kante 'oben' im derivation_table				*/
	struct derivation		*lower_edge;		/* Kante 'unten' im derivation_table				*/
	struct attributes_head		*next_for_upper;	/* Zeiger von oben "attributes_table_down"			*/
	struct attributes_head		*next_for_lower;	/* Zeiger von unten "attributes_table_up"			*/
	struct attributes_ref_list	*upper_productions;	/* Fuer jede Produktion oben eine solche Datenstruktur		*/
	struct set_of_parsing_elements	*below_derivated_set;	/* Diese Struktur wird benoetigt zur Attributberechnung		*/

	/****** Attribute ******/

	struct optimal_edges		*optimal_set;		/* Alles, was unten optimal weitergeht				*/

}	*Attributes_head;


#define for_attributes_head_from_top( ah, cur )			\
	{(cur) = (ah);						\
	while( (cur) != (Attributes_head)NULL ) {

#define end_for_attributes_head_from_top( ah, cur )		\
	(cur) = (cur)->next_for_upper; }}


#define for_attributes_head_from_bottom( ah, cur )			\
	{(cur) = (ah);						\
	while( (cur) != (Attributes_head)NULL ) {

#define end_for_attributes_head_from_bottom( ah, cur )		\
	(cur) = (cur)->next_for_lower; }}


/*******************************************************************************/

typedef struct attributes_ref_list
{
	int				is_same;		/* Marke, selbes pe, aber andere Ableitung			*/
	struct attributes_ref		*refs_between_prods;	/* Eine solche Datenstruktur fuer jede Kombination von		*/
								/* dieser Produktion oben und einer unten			*/
	struct attributes_ref		*optimal_attributes_ref;
	struct attributes_ref_list	*next;			/* naechste Produktion oben					*/
	struct attributes_ref_list	*same_upper_prod_in_next; /* Naechste Kante unten, gleiche Produktion oben		*/
	struct node			*graphed_iso;		/* Zeichnung							*/
	struct attributes_head		*attr_head;

	/****** Attribute ******/

	int				little_c_star;
	int				c_0;
	int				big_c_star;

}	*Attributes_ref_list;



#define for_attributes_ref_list( ar, cur )		\
	{(cur) = (ar);					\
	while( (cur) != (Attributes_ref_list)NULL) {

#define end_for_attributes_ref_list( ar, cur )		\
	(cur) = (cur)->next; }}


#define for_same_upper_production( ar, cur )		\
	{(cur) = (ar);					\
	while( (cur) != (Attributes_ref_list)NULL ) {

#define end_for_same_upper_production( ar, cur )	\
	(cur) = (cur)->same_upper_prod_in_next; }}

/*******************************************************************************/

typedef struct attributes_ref
{
	struct graph			*upper_prod;		/* Verweis auf entsprechende Produktion oben			*/
	struct graph			*lower_prod;		/* Verweis auf entsprechende Produktion unten			*/
	struct attributes_ref		*next;
	struct attributes_ref_list	*same_prod_lower;	/* wird benoetigt zur Berechnung von  big_c_star		*/
	struct attributes_ref_list	*attr_list;		/* damit man in der Attributberechnung rueckwaerts gehen kann	*/
	struct	attributes_ref		*next_optimal_ref;	/* Zum Umwandeln in Baum; gleiche Prod oben, naechster Knoten,	*/
								/* der in der Prod abgeleitet wird				*/

	/****** Attribute ******/

	int				little_c;

}	*Attributes_ref;

#define for_attributes_ref( ar, cur )			\
	{(cur) = (ar);					\
	while( (cur) != (Attributes_ref)NULL) {

#define end_for_attributes_ref( ar, cur )		\
	(cur) = (cur)->next; }}


/********************************************************************************
Hier einige Strukturen, die in Orginal-Graphed-Strukturen eingebunden werden,
damit eine kleinere Schnittstelle entsteht.
Neuer Name = lp_ && Graphed-Name
********************************************************************************/


/********************************************************************************
********************************************************************************/

typedef	struct
{
	struct tree_rec 		*tree_iso;		/* Pointer to corresponding node in derivation net */

	struct node			*multi_iso;		/* Kopierzeiger zum Einrichten der multi_iso Zeiger */
	struct node			*multi_pre;		/* Verkettung der Knoten in isomorphen Produktionen */
	struct node			*multi_suc;		/* Verkettung der Knoten in isomorphen Produktionen */

	struct lpp_parsing_element	*pars_iso;
	int				has_a_mark;

	struct	lpr_node		*copy_iso;		/* Notwendig zum erzeugen einer Kopie in lpr Struktur */
	struct	lpr_node		*corresponding_lpr_node;/* Von Graph nach Baum in lpr Struktur */

	struct	node			*iso_in_area_opt;	/* Zum Zeichnen der Kanten bei Areaopt. */
}
	Lp_node;


/* Zum Durchlaufen der isomorphen Knoten in isomorphen Produktionen */

#define for_node_multi_suc( node, cur_node )				\
	{ if ( ((cur_node) = (node)) != empty_node) do {
#define end_for_node_multi_suc( node, cur_node )			\
	} while ( ((cur_node) = (cur_node)->lp_node.multi_suc) != (node)); }


/********************************************************************************
********************************************************************************/


typedef	struct
{
	lp_Edgeline		lp_line;
	lp_Edgeline		old_lp_line;
	int			lp_ord;
	int			side;

	struct edge		*iso;

	struct tree_rec		*tree_iso;
	struct history_rec	*history;
	struct edge		*multi_iso;
	struct edge		*multi_pre;
	struct edge		*multi_suc;
}
	Lp_edge;


/* Zum Durchlaufen der isomorphen Kanten in isomorphen Produktionen */

#define for_edge_multi_suc( edge, cur_edge )				\
	{ if ( ((cur_edge) = (edge)) != empty_edge) do {
#define end_for_edge_multi_suc( edge, cur_edge )			\
	} while ( ((cur_edge) = (cur_edge)->lp_edge.multi_suc) != (edge)); }


/********************************************************************************
********************************************************************************/
/*** Haengt an jedem hierarchischen Graph. Ist Liste aller Produktionen, die fuer
	diesen Graph verwendet wurden mit der Zeit der 1. Benutzung	***/
typedef	struct	prod_list
{
	struct	graph			*prod;
	struct	prod_list		*next;
	int				first_used;
}	*Prod_list;

typedef	struct
{
	struct tree_rec 		*derivation_net;
	int				current_size;						/* Fuer Vergroeserung beim Layout ausrechnen	*/
	int				properties_array[ PRECONDITION_COUNTER ];		/* Bit-Array f"ur die Eigenschaften LGG-Prod.	*/

	struct graph			*multi_pre;		/* Verzeigerung von isomorphen Produktionen					*/
	struct graph			*multi_suc;

	int				node_nr;
	int				save_node_nr;
	int				edge_nr;
	struct	set_of_nodelist		*embedding_rules;
	struct	lpp_parsing_element	*table;

	struct	lpr_node		*LRS_graph;

	int				changed;		/* Flag, das anzeigt, dass vom Benutzer eine Veraenderung eines hierarchischen	*/
								/* Graphen oder einer Produktion vorgenommen wurde, die potentiell einen	*/
								/* Absturz verursachen kann (genau die Aktionen, wo was verschwindet oder	*/
								/* erzeugt wird - auch polyline Points)						*/
	int				reduced;		/* Zeigt an dass Aktion reduce oder delete auf graph angewendet wurde		*/
								/* Auf Graphen mit gesetztem Flag darf nur noch topdown area opt oder graphed	*/
								/* Standard angewendet werden							*/
	int				creation_time;		/* Wann wurde der Graph erzeugt. Wird benoetigt um zu verhindern, dass eine Prod*/
								/* Waehrend der Bearbeitung mit eines hierarchischen Graphen veraendert wird 	*/
	int				hierarchical_graph;	/* Ist das ein hierarchischer Graph ( also mit Netz )				*/
	int				dependency_visible;	/* Ist dependency Sichtbar (muss vor Winnie geloescht werden)			*/
	char*				disposed;		/* Setze beim Erzeugen auf nondisposed, beim loeschen auf _____________		*/
}
	Lp_graph;


/* Zum Durchlaufen der isomorphen Produktionen */

#define for_graph_multi_suc( graph, cur_graph )				\
	{ if ( ((cur_graph) = (graph)) != empty_graph) do {
#define end_for_graph_multi_suc( graph, cur_graph )			\
	} while ( ((cur_graph) = (cur_graph)->lp_graph.multi_suc) != (graph)); }



/********************************************************************************
********************************************************************************/

typedef	struct
{
	struct	top_sort_rec		*first_x;
	struct	top_sort_rec		*first_y;

	int	max_ord[4];

}
	Lp_nce1_gragra;


/*******************************************************************************/
/*** einige globale Variablen						     ***/
/*******************************************************************************/

int	create_net_edges;
int	node_test;
int	create_by_derivation;
int	current_size;	/* Gibt Groesse an, die sich durch vergroesern bzw. verkleinern ergibt. wichtig in lp_7_pass		*/
int	grammar_preconditions[PRECONDITION_COUNTER];


/********************************************************************************
Das hier muesste eigentlich alles nach state.h
********************************************************************************/
typedef enum
{
	GRAPHED_STANDARD,
	TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP,
	TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP_B,
	TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP,
	TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI,
	TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI_ORIENTED,
	TOP_DOWN_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING,
	TOP_DOWN_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING_REDRAW,
	BOTTOM_UP_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP,
	BOTTOM_UP_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP_B,
	BOTTOM_UP_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP,
	BOTTOM_UP_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING,
	ALGORITHM_NUMBER
}
	LGG_Algorithms;

#define current_attribute       (get_current_attribute_type())


typedef	struct
{
	LGG_Algorithms	LGG_Algorithm;
	int		gragra_properties[ PRECONDITION_COUNTER ];
	int		production_deleted;
}
	Lp_graph_state;

/********************************************************************************
Welcher Algorithmus benoetigt welche Grammatik-Eigenschaften
********************************************************************************/

extern	int Algorithm_preconditions[ ALGORITHM_NUMBER ] [ PRECONDITION_COUNTER ];

/********************************************************************************
Makro zur Initialisierung des Feldes Grammar_preconditions
********************************************************************************/
#define lp_init_grammar_preconditions													\
																	\
int	Algorithm_preconditions[ ALGORITHM_NUMBER ][ PRECONDITION_COUNTER ] = 								\
	{																\
		{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },	/* GRAPHED_STANDARD						*/	\
		{ 0, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1 },	/* TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP		*/	\
		{ 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1 },	/* TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP_B		*/	\
		{ 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 0, 0, 1, 1 },	/* TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP			*/	\
		{ 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 0, 0, 1, 1 },	/* TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI		*/	\
		{ 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 0, 0, 1, 1 },	/* TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI_ORIENTED	*/	\
		{ 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 1, 1 },	/* TOP_DOWN_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING		*/	\
		{ 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 1, 1 },	/* TOP_DOWN_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING_REDRAW	*/	\
		{ 0, 1, 1, 1, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1 },	/* BOTTOM_UP_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP		*/	\
		{ 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1 },	/* BOTTOM_UP_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP_B		*/	\
		{ 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 0, 1, 1, 1 },	/* BOTTOM_UP_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP		*/	\
		{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 } 	/* BOTTOM_UP_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING		*/	\
	};

#endif
