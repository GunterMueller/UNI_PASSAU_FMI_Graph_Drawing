#ifndef __LPR_PLRS_H__
#define __LPR_PLRS_H__
/******************************************************************

				File: lpr_plr_system.h
				
	Hier sind die Strukturen definiert, die zum Speichern eines
	PLRS-Systems und den Dependendy-Listen ben"otigt werden. Am				
	Ende des Files sind die extern deklarierten Funktionen von
	lpr_plr_system.c aufgef"uhrt.
	
******************************************************************/
	


/******************************************************************
Typ		: plrs_node	
Description	: Typ zur Speicherung eines Knotens im plrs-System.
		  Die Struktur des entstehenden Graphen entspricht
		  der Graphed-Struktur f"ur Graphen.
Entries	: value		: Gibt die x- oder y-Koordinate an.
		  h_value		: Hilfseintrag zur Knoten-Groessen-Minimierung
		  side		: Hilfseintrag zum Testen.
		  is_x		: 1 falls Knoten in X, 0 falls in Y
		  in_edges		: wie im Graphed
		  out_edges		: wie im Graphed
		  iso			: zum Kopieren
		  pre			: wie im Graphed
		  suc			: wie im Graphed
		  graphed_node	: Hilfseintrag zum Testen
*******************************************************************/
typedef struct plrs_node
{
	int			value;
	int			h_value;	
	int 			side;
	int			is_x;
	int 			new_node;
	struct plrs_edge 	*in_edges;
	struct plrs_edge 	*out_edges;
	char			info[100];
	struct plrs_node	*iso;	
	int			px;
	int			py;
	int			visited;
	struct plrs_node	*pre;
	struct plrs_node	*suc;
	
	struct node		*graphed_node;

}
	* plrs_Node;

/*******************************************************************
					Makros
*******************************************************************/
#define	FOR_PLRS_NODES(gv_head, gv)  \
	{ if ( ((gv) = (gv_head)) != (plrs_Node)NULL ) do {
#define END_FOR_PLRS_NODES(gv_head, gv)  \
	} while( ((gv) = (gv)->suc ) != (gv_head) ); }



/******************************************************************
Typ		: plrs_edge	
Description	: Typ zur Speicherung einer Kante im plrs-System.
		  Die Struktur des entstehenden Graphen entspricht
		  der Graphed-Struktur f"ur Graphen.
Entries	: source		: wie im Graphed
		  target		: wie im Graphed
		  length		: L"ange der Kante
		  iso			: zum Kopieren
		  source_pre	: wie im Graphed
		  source_suc	: wie im Graphed
		  target_pre	: wie im Graphed
		  target_suc	: wie im Graphed
		  visited		: Hilfseintrag
*******************************************************************/
typedef struct	plrs_edge
{
	struct plrs_node	*source;
	struct plrs_node	*target;
	int			length;
	struct plrs_edge	*iso;

	struct plrs_edge	*source_pre;	
	struct plrs_edge	*source_suc;

	struct plrs_edge	*target_pre;
	struct plrs_edge	*target_suc;

	int			visited;
}
	*plrs_Edge;

/*******************************************************************
					Makros
*******************************************************************/
#define	FOR_PLRS_EDGE_SOURCE(node, edge)                                    \
	{ if (((edge) = ((node)->out_edges)) != (plrs_Edge)NULL) do {
#define	END_FOR_PLRS_EDGE_SOURCE(node, edge)                                \
	} while (((edge) = (edge)->source_suc) != ((node)->out_edges)); }

#define	FOR_PLRS_EDGE_TARGET(node, edge)                                    \
	{ if (((edge) = ((node)->in_edges)) != (plrs_Edge)NULL) do {
#define	END_FOR_PLRS_EDGE_TARGET(node, edge)                                \
	} while (((edge) = (edge)->target_suc) != ((node)->in_edges)); }



/******************************************************************
Typ		: plr_system
Description	: Typ zur Speicherung eines PLR-Systems, bestehend aus
		  dem X- und Y-Graphen sowie den Dependency-Listen.
		  Letztere sind allerdings schon bei lpr_edge gespeichert.
Entries	: x_graph	: x-Dependency-Graph
		  y_graph	: y-Dependency-Graph
*******************************************************************/
typedef struct plr_system
{
	struct plrs_node	*x_graph;
	struct plrs_node	*y_graph;
}
	* plr_System;




/******************************************************************
Typ		: dependency_list	
Description	: Typ zur Speicherung einer Dependency-Liste. "Ahnlich
		  wie eine lpr_Nodelist.
Entries	: node	: der gespeicherte plrs-Knoten
		  pre		: Vorg"anger
		  suc		: Nachfolger
*******************************************************************/
typedef struct dependency_list
{
	struct plrs_node		*node;
	struct dependency_list 	*pre;
	struct dependency_list 	*suc;
} * Dependency_list;


/*******************************************************************
					Makros
*******************************************************************/
#define	FOR_DEP_LIST( nl_head, nl )						\
		{if (((nl) = (nl_head)) != (Dependency_list)NULL) do {
#define END_FOR_DEP_LIST( nl_head, nl )					\
		} while (((nl) = (nl)->suc) != (nl_head)); }


/******************************************************************
Typ		: plrs_nodelist	
Description	: Typ zur Speicherung einer plrs_Node-Liste. "Ahnlich
		  wie eine lpr_Nodelist.
Entries	: node	: der gespeicherte plrs-Knoten
		  pre		: Vorg"anger
		  suc		: Nachfolger
*******************************************************************/
typedef struct plrs_nodelist
{
	struct plrs_node		*node;
	struct plrs_nodelist 	*pre;
	struct plrs_nodelist 	*suc;
} * plrs_Nodelist;


/*******************************************************************
					Makros
*******************************************************************/
#define	FOR_PLRS_NODELIST( nl_head, nl )						\
		{if (((nl) = (nl_head)) != (plrs_Nodelist)NULL) do {
#define END_FOR_PLRS_NODELIST( nl_head, nl )					\
		} while (((nl) = (nl)->suc) != (nl_head)); }





#endif
