#include "lpr_plrs.h"


/************************************************************************************************/
/*												*/
/*	Spezielle Datenstrukturen fuer Parsing mit LRS						*/
/*												*/
/************************************************************************************************/

/************************************************************************************************/
/*												*/
/*	Datenstrukturen fuer interne Repraesentation eines Graphen				*/
/*	als Derivation_tree									*/
/*												*/
/************************************************************************************************/

/*************************************************************************************************
	genauere Spezifikation der Knoten
*************************************************************************************************/

typedef enum
{
	lpr_LHS_NODE,					/* Knoten wurde abgeleitet		*/
	lpr_NORMAL_NODE					/* Normaler Knoten in Graph		*/
							/* also in RHS, nicht abgeleitet	*/
}
	lpr_NODETYPE;

/*************************************************************************************************
	genauere Spezifikation der Kanten
*************************************************************************************************/

typedef enum
{
	lpr_RHS_EDGE,					/* Normale Kante in rechter Seite	*/
	lpr_IN_CONN_REL,				/* IN_embedding				*/
	lpr_OUT_CONN_REL				/* OUT_embedding			*/
}
	lpr_EDGETYPE;


/************************************************************************************************/
/*      Struktur zum Speicher einer Bridgehierarchy-Number von zwei Kanten                      */
/************************************************************************************************/

typedef struct lpr_bridge_hierarchie
{
	int                       number;    	/* Bridgehierarchy-Number von der Kante die auf diese      */
	struct lpr_edge          *bridge_edge;  /* Struktur zeigt und dieser Kante                         */
	struct lpr_bridge_hierarchie *suc;      /* Zeiger auf n"achste Bridgehierarchy                     */
	struct lpr_bridge_hierarchie *pre;      /* Zeiger auf vorherige Bridgehierarchy                    */
} *lpr_Bridge_hierarchie;

/********************	Durchlaufen Bridge-Hierarchies in der Liste  *********************/
#define	FOR_LPR_BRIDGE_HIERARCHIE( el_head, el )					\
		{ if (((el) = (el_head)) != (lpr_Bridge_hierarchie)NULL) do {
#define	END_FOR_LPR_BRIDGE_HIERARCHIE( el_head, el )					\
		} while (((el) = (el)->suc) != (el_head)); }

/************************************************************************************************/
/*	lpr_edge entspricht edge in graphed							*/
/*	- Verkettung der Kanten ueber eigene Datenstruktur lpr_edgelist				*/
/************************************************************************************************/

typedef	struct	lpr_edge
{
	lpr_EDGETYPE 			edge_type;				/* Art der Kante					*/
	struct lpr_node			*source;				/* Quellknoten					*/
	struct lpr_node			*target;				/* Zieknoten					*/
	char*					source_label;			/* In Kopie von Graphed_Prod haben ...	*/
	char*					target_label;			/* Embedding-Regeln entweder kein Source	*/
											/* oder kein Target; also notwendig		*/
											/* damit diese vollstaendig sind		*/
	struct edge				*GRAPH_iso;				/* Entsprechung im Graph			*/
	struct edge				*PROD_iso;				/* Entsprechung in der Produktion		*/
	struct lpr_iso_edge    		**array_of_iso_edge_pointers;	/* Zeiger auf ein Array isom. Kanten    	*/
	struct lpr_hierarchie		*generated_edges;			/* Kante ist Embedding-edge. Datenstruk-	*/
											/* tur zeigt auf alles, was daraus im 	*/
											/* NAECHSTEN Ableitungsschritt entstand	*/
	struct lpr_hierarchie		*following_edges;			/* Kante war RHS_Edge; Source oder		*/
											/* Target wurde abgeleitet --> es exis-	*/
											/* tiert Stufe spaeter eine entsprech.	*/

	struct lpr_hierarchie  		*EH; 	                        /* Edge hierarchy set von edge          	*/
	int                     	EH_number;				/* | EH | von edge                      	*/
	struct lpr_bridge_hierarchie	*bridge_numbers;	        	/* F"ur Bridgehierarchy-Sets            	*/
	char*					label;				/* Name der Kante					*/

    											/* F"ur extended hierarchy              	*/
	struct lpr_edge        		*pred;                  	/* Vorg"anger der Br"ucke lpr_edge      	*/   
	struct lpr_edge        		*last;                  	/* letztes Element von history(lpr_edge)	*/

	struct lpr_edgelist    		**pes_array;			/* Pes f"ur jedes Segment der Kante		*/
	struct lpr_track_ass_des	*ta_td;				/* Track-Assignment und -Description	*/
	struct dependency_list		*S_list;				/* Dependency-Liste, Theorie: S		*/
	struct dependency_list		*S2_list;				/* Dependency-Liste, Theorie: S''		*/
	int					start_value;			
	int					end_value;
}	*lpr_Edge;

typedef	struct	lpr_edgelist
{
	struct	lpr_edge		*edge;			/* Kante zum Listenelement		*/
	struct	lpr_edgelist	*pre;			/* Vorherige Kante			*/
	struct	lpr_edgelist	*suc;			/* Naechste Kante			*/
}	*lpr_Edgelist;



/*****************************************************************************************/
/* Struktur zur Speicherung des Trackassignments und der Trackdescription einer Kante    */
/*											 */
/* Erl"auterungen: Falls der Kantenverlauf durch einen (oder sogar zwei Tracks) ver-	 */
/* l"auft, so wird td1 und tn1 (und td2 und tn2 ) gesetzt. ta_type gibt dem Kantenver-	 */
/* lauf eine Nummer. Eine Kante die gerade durchl"auft, also keinen Track benutzt, hat	 */
/* ta_type = 0, eine mit einem Track und Kantenverlauf im Uhrzeigersinn ta_type = 1, im	 */
/* Gegenuhrzeigersinn ta_type = 2. Eine mit zwei benutzten Tracks und Kantenverlauf im	 */
/* Uhrzeigersinn ta_type = 3, und eine im Gegenuhrzeigersinn ta_type = 4. node dient zur */
/* Angabe der Produktion zu der dieses ta_td geh"ort.					 */
/*****************************************************************************************/
typedef struct lpr_track_ass_des
{
	int					td1;		/* Falls ex. Richtung im 1. Track		*/
	int					td2;		/* Falls ex. Richtung im 2. Track		*/
	int					tn1;		/* Falls ex. Nummer des Tracks, in dem	*/
	int					tn2;		/* das 1. bzw. 2. verl"auft.			*/
	int					ta_type;	/* Typ des Track-Assignments			*/
	struct lpr_node			*node;	/* Zugeh"origkeit zur entspr. Prod.		*/
	struct plrs_node			*oc;		/* siehe add_dependency_sequences		*/
	struct lpr_track_ass_des	*pre;	
	struct lpr_track_ass_des	*suc;
} * lpr_Track_ass_des;

/* Zum Durchlaufen einer Liste vom Typ lpr_Track_ass_des */
#define	FOR_LPR_TRACK_ASS_DES( head, e )						\
		{ if (((e) = (head)) != (lpr_Track_ass_des)NULL) do {
#define	END_FOR_LPR_TRACK_ASS_DES( head, e )					\
		} while (((e) = (e)->suc) != (head)); }

/************************************************************************************************/
/*	 lpr_iso_edge dient zum abspeichern der verschiedenen Layouts von Kanten bei isomorphen */
/*	 Produktionen. Auf eine Verkettung wird verzichtet, da diese Layouts in lpr_edge als ein*/
/* 	 Array gespeichert werden.								*/
/************************************************************************************************/
typedef struct lpr_iso_edge
{
	struct edge        *edge;                       /* Layout der Kante                     */
	int                bends;     			/* Anzahl Knicke bei diesem Layout      */
} *lpr_Iso_edge;

/******					Makros					 	   ******/

/******	Durchlaufen ALLER Kanten in der Liste						   ******/
#define	FOR_LPR_EDGELIST( el_head, el )						\
		{ if (((el) = (el_head)) != (lpr_Edgelist)NULL) do {
#define	END_FOR_LPR_EDGELIST( el_head, el )					\
		} while (((el) = (el)->suc) != (el_head)); }


/******	Durchlaufen aller Kanten in der Liste, die IN_EMBEDDING Regeln repraesentieren	   ******/
#define FOR_LPR_IN_CONN_REL( el_head, el )					\
		FOR_LPR_EDGELIST( el_head, el )					\
		if ( (el)->edge->edge_type == lpr_IN_CONN_REL ) {
#define END_FOR_LPR_IN_CONN_REL( el_head, el )					\
		} END_FOR_LPR_EDGELIST( el_head, el ); 


/******	Durchlaufen aller Kanten in der Liste, die OUT_EMBEDDING Regeln repraesentieren	   ******/
#define FOR_LPR_OUT_CONN_REL( el_head, el )					\
		FOR_LPR_EDGELIST( el_head, el )					\
		if ( (el)->edge->edge_type == lpr_OUT_CONN_REL ) {
#define END_FOR_LPR_OUT_CONN_REL( el_head, el )					\
		} END_FOR_LPR_EDGELIST( el_head, el ); 


/******	Durchlaufen aller Kanten in der Liste, die RHS_EDGES repraesentieren		   ******/
#define FOR_LPR_RHS_EDGES( el_head, el )					\
		FOR_LPR_EDGELIST( el_head, el )					\
		if ( (el)->edge->edge_type == lpr_RHS_EDGE ) {
#define END_FOR_LPR_RHS_EDGES( el_head, el )					\
		} END_FOR_LPR_EDGELIST( el_head, el ); 


/************************************************************************************************/
/*	lpr_node entspricht node in graphed							*/
/*	- Verkettung durch eigene Datenstruktur lpr_Nodelist					*/
/************************************************************************************************/

typedef	struct	lpr_node
{
	lpr_NODETYPE		node_type;				/* Typ des Knoten				*/
	struct node			*GRAPH_iso;			/* Entsprechung im Graphen			*/
	struct node			*PROD_iso;			/* Entsprechung in der Produktion		*/
	struct node            	**array_of_iso_node_pointers; 		/* Array isom. Knoten             		*/
 	struct lpr_graph		*applied_production;		/* Im Knoten abgeleitete Produktion		*/
	int				is_terminal_node;		/* Markierung: im Graphen sichtbar		*/
	struct lpr_graph		*graph;				/* Zu welcher Prod Knoten gehoert		*/
	char*				label;				/* Name der Produktion				*/
	struct lpr_edgelist	*source_edges;				/* Kanten mit Knoten als Quelle			*/
	struct lpr_edgelist	*target_edges;				/* Kanten mit Knoten als Ziel			*/
									/* letzte beiden aus RHS			*/
	struct lpr_edgelist	*later_source_edges;			/* Kanten mit Knoten als Quelle			*/
	struct lpr_edgelist	*later_target_edges;			/* Kanten mit Knoten als Ziel			*/
									/* letzte beiden spaeter entstanden		*/

	struct plrs_node		*left;				/* Zeiger auf PLRS-Knoten			*/
	struct plrs_node		*right;
	struct plrs_node		*up;
	struct plrs_node		*down;
	struct plrs_node		*bleft;				/* Zeiger auf box-PLRS-Knoten			*/
	struct plrs_node		*bright;
	struct plrs_node		*bup;
	struct plrs_node		*bdown;
	struct lpr_node		*father;				/* Brauchen wir wenn was verschwinden soll	*/

	/************************/
	/* HIERARCHY		*/
	/************************/					/* Necessary for reduce, open and delete	*/

	int	flag;
	int	big;
	int	leaf;	
	
}	*lpr_Node;


typedef	struct	lpr_nodelist
{
	struct	lpr_node	*node;			/* Knoten zum Listenelement		*/
	struct	lpr_nodelist	*pre;			/* Vorgaenger in Liste			*/
	struct	lpr_nodelist	*suc;			/* Nachfolger in Knotenliste		*/
}	*lpr_Nodelist;

/******					Makros						   ******/

/******	Zum Durchlaufen ALLER Knoten in der Knotenliste					   ******/
#define	FOR_LPR_NODELIST( nl_head, nl )						\
		{if (((nl) = (nl_head)) != (lpr_Nodelist)NULL) do {
#define END_FOR_LPR_NODELIST( nl_head, nl )					\
		} while (((nl) = (nl)->suc) != (nl_head)); }


/******	Zum Durchlaufen aller Knoten in der Knotenliste, die ABGELEITET wurden		   ******/
#define FOR_LPR_LHS_NODES( nl_head, nl )					\
		FOR_LPR_NODELIST( nl_head, nl )					\
		if ( (nl)->node->node_type == lpr_LHS_NODE ) {
#define END_FOR_LPR_LHS_NODES( nl_head, nl )					\
		} END_FOR_LPR_NODELIST( nl_head, nl );


/******	Zum Durchlaufen aller Knoten die in der RHS liegen und nicht abgeleitet wurden	   ******/
#define	FOR_LPR_NORMAL_NODES( nl_head, nl )					\
		FOR_LPR_NODELIST( nl_head, nl )					\
		if( (nl)->node->node_type == lpr_NORMAL_NODE ) {
#define END_FOR_LPR_NORMAL_NODES( nl_head, nl )					\
		} END_FOR_LPR_NODELIST( nl_head, nl );


/************************************************************************************************/
/*	lpr_graph entspricht Produktion in graphed								*/
/************************************************************************************************/

typedef	struct	lpr_graph
{
	struct	lpr_nodelist			*nodes;				/* Menge der Knoten in Prod			*/
	struct	lpr_edgelist			*IN_embeddings;			/* Kanten von IN_embedding Regeln		*/
	struct	lpr_edgelist			*OUT_embeddings;			/* Kanten von OUT_embedding Regeln		*/
	struct	graph					*PROD_iso;				/* Entsprechende Produktion in graphed	*/
	struct	graph					*GRAPH_iso;				/* Abgeleiteter Graph				*/
	struct  graph           			**array_of_iso_prod_pointers; /* Array von Zeigern, die auf iso-      	*/
							      					/* morphe Prod.layouts zeigen           	*/

	/* Strukturelemente zur Speicherung der berechneten Kostenfunktionen: 								*/
	struct lpr_cost_c_list                   	*cost_c;         			/* Array in dem die Kosten c liegen     	*/
	int                     			*cost_c0_array;         	/* Array in dem die Kosten c0 liegen    	*/
	struct lpr_cost_layout_to_production 	**cost_c_stern_array; 		/* Array zum Speichern von c*           	*/
	int                     			*cost_C_stern_array;
	int                    				cost_C_2stern_array;
	struct lpr_layouts_for_set_stern 		**set_stern_array;
	int                     			*set_2stern_array;
	struct graph					*optimal_layout;

	int      						number_of_iso_prods;    	/* Anzahl isom. Layouts                 	*/

	/* Strukturelemente zur Speicherung der LRS-Parameter: 										*/
	struct lpr_edgelist				*in_con_seq[4];			/* Sequentialisierung der In-Conn-Rels  	*/
	struct lpr_edgelist				*out_con_seq[4];			/* Sequentialisierung der Out-Conn-Rels 	*/
	struct lpr_track_sharing    			*ts_array[4];			/* Array von Tracksharing-Listen        	*/
	int							tn[4];				/* Track-Numbers f"ur jede Seite        	*/
	struct plrs_node					**track_segments[4];		/* Array von Track-Segm. f"ur alle Seiten	*/

}	*lpr_Graph;


/************************************************************************************************************/
/* 	Struktur zum Speichern der Kostenfunktion c										*/
/************************************************************************************************************/

typedef struct lpr_cost_c_list
{
	int			 *cost_c;
	struct lpr_graph 	 *son_prod;
	struct lpr_cost_c_list *pre;
	struct lpr_cost_c_list *suc;
}	*lpr_Cost_c_list;

/*************************************************************************************************/
/* Struktur zur Speicherung des Tracksharings einer Seite und eines Tracks. Falls nur eine Kante */
/* in diesem Track verl"auft, so ist lpr_edge1 gesetzt, falls zwei, dann beide.	track_number gibt*/
/* die Nummer des Tracks an, um den es sich handelt.						 */
/*************************************************************************************************/
typedef struct lpr_track_sharing
{
	struct lpr_edge 			*edge1;
	struct lpr_edge 			*edge2;
	int					track_number;
	struct lpr_track_sharing 	*pre;
	struct lpr_track_sharing 	*suc;
} * lpr_Track_sharing;

/* Zum Durchlaufen einer solchen Liste */						
#define	FOR_LPR_TS( head, el )					\
		{ if (((el) = (head)) != (lpr_Track_sharing)NULL) do {
#define	END_FOR_LPR_TS( head, el )					\
		} while (((el) = (el)->suc) != (head)); }


/************************************************************************************************/
/* Struktur zum Speichern der Menge set*. In lpr_Graph existiert ein Zeiger auf ein Array von   */
/* Objekten dieser Struktur nach den Layouts indiziert. Diese Arrays enthalten Listen in denen  */
/* in prod eingetragen ist um welchen Sohn es sich handelt und in set wird in einem Bitarray    */
/* festgehalten, welche Layouts zu set* geh"oren.                                               */
/************************************************************************************************/
typedef struct lpr_layouts_for_set_stern
{
	struct lpr_graph				*prod;
	int						*set;
	struct lpr_layouts_for_set_stern 	*pre;
	struct lpr_layouts_for_set_stern 	*suc;
} *lpr_Layouts_for_set_stern;

#define	FOR_LPR_LAYOUTS_FOR_SET_STERN( el_head, el )					\
		{ if (((el) = (el_head)) != (lpr_Layouts_for_set_stern)NULL) do {
#define	END_FOR_LPR_LAYOUTS_FOR_SET_STERN( el_head, el )					\
		} while (((el) = (el)->suc) != (el_head)); }



/************************************************************************************************/
/*	Wird benoetigt fuer Kantengeschichten C und H aus Theorie				*/
/************************************************************************************************/



typedef	struct	lpr_hierarchie
{
	struct	lpr_edgelist	*edges;
}	*lpr_Hierarchie;


/*************************************************************************************************
   Struktur zur Speicherung der Kostenfunktion c*. In lpr_Graph wird ein Array gespeichert, das
   jedem Layout einer Vaterproduktion eine Objekt der Struktur lpr_cost_layout_to_production zu-
   ordnet, also die Kosten der Vaterlayouts zu einer Sohnproduktion.                            
*************************************************************************************************/


typedef struct lpr_cost_layout_to_production
{
	struct lpr_graph 					*prod;
	int							cost;
	struct lpr_cost_layout_to_production 	*pre;
	struct lpr_cost_layout_to_production 	*suc;
}	*lpr_Cost_layout_to_production;

#define	FOR_LPR_COST_LAYOUT_TO_PRODUCTION( el_head, el )					\
		{ if (((el) = (el_head)) != (lpr_Cost_layout_to_production)NULL) do {
#define	END_FOR_LPR_COST_LAYOUT_TO_PRODUCTION( el_head, el )					\
		} while (((el) = (el)->suc) != (el_head)); }

















