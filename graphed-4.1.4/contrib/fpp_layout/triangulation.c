/* (C) Universitaet Passau 1986-1994 */
/*********************************************************************
 **                                                                 **
 **     In diesem File ist die Prozedur enthalten, die die          **
 **     Triangulierung ausfuehrt. Daneben stehen hier noch          **
 **     einige Prozeduren, die fuer das gesamte Programm            **
 **     oefter gebraucht werden.                                    **
 **                                                                 **
 *********************************************************************/

#include "decl.h"





struct fpp_node *search_node (int n)
			/* Sucht den Knoten mit der Nummer "n" in der */
			/* Adjazenzliste.                             */
      

{
	struct fpp_node *pgraph;
	int i;
	pgraph = graph;
	for (i=0; ((i <= gsize) && (pgraph != NULL) && (pgraph->number != n)); i++)
		pgraph = pgraph->next;
	return (pgraph);
	
} /* search_node */




void fill_neighbors (void)
			/* Weist den Nachbarn ihre Knoteninformationen zu. */

{
	int i;
	struct fpp_node *hgraph;
	struct nlist *hneigh;
	hgraph = graph;
	for (i=0; (hgraph != NULL); hgraph = hgraph->next, i++) {
		hneigh = hgraph->neigh;
		while (hneigh != NULL) {
			hneigh->node = search_node(hneigh->nr);
			hneigh = hneigh->next;
		};
	};
	
} /* fill_neighbors */
	
	
	

void add_edge (Sgraph sgraph, int nr1, int nr2)
			/* Fuegt die Kante von Knoten "nr1" nach Knoten "nr2" */
			/* in die Datenstrukturen ein.                        */
              
             

{	
	struct fpp_node *nd1, *nd2, *snd;
	struct nlist *nei1, *nei2, *hnei;
	Snode n, snode, tnode;
	Sedge sedge;
	
	
	for_all_nodes (sgraph, n)
		if (n->nr == nr1) {
			snode = n;
		};
		if (n->nr == nr2) {
			tnode = n;
		};
	end_for_all_nodes (sgraph, n);
	
	sedge = make_edge (snode, tnode, make_attr (ATTR_DATA, NULL));
	edge_set (create_graphed_edge_from_sedge (sedge), 
		EDGE_LINE, add_to_edgeline (new_edgeline (snode->x, snode->y), tnode->x, tnode->y),
		EDGE_ARROW_LENGTH, 0,
		EDGE_TYPE, 2,
		EDGE_COLOR, 13,
		0);
	force_repainting ();
	

	
	snd = graph;
	while ((snd != NULL) && (snd->number != nr2) && (snd->number != nr1)) {
		snd = snd->next;
	};
	if (snd->number == nr1) {
		nd1 = snd;
	}
	else {
		nd2 = snd;
	};
	
	while ((snd != NULL) && (snd->next != NULL) && 
	      (snd->next->number != nr1) && (snd->next->number != nr2)) {
		snd = snd->next;
	};
	if ((snd != NULL) && (snd->next != NULL)) {
		snd = snd->next;
	};
	
	if ((snd != NULL) && (snd->number == nr1)) {
		nd1 = snd;
	}
	else {
		if (snd != NULL) {
			nd2 = snd;
		};
	};
	
	
	if ((nd1 != NULL) && (nd2 != NULL)) {
		(nd1->degree)++;
		(nd2->degree)++;
	
		nei1 = (struct nlist *) malloc(sizeof(struct nlist));
		nei1->nr = nr2;
		nei1->orig = 0;
		nei1->next = nd1->neigh;
		nei1->angle = angle (nd1, nd2);
		hnei = nd1->clockw;
		
		while ((hnei->angle < nei1->angle) && (hnei != nd1->co_clockw)) {
			hnei = hnei->clockw;
		};
		
		if (hnei->angle < nei1->angle) {
			hnei = hnei->clockw;
		};
		
		nei1->co_clockw = hnei->co_clockw;
		nei1->clockw = hnei;
		hnei->co_clockw = nei1;
		nei1->co_clockw->clockw = nei1;
	
		if (hnei == nd1->clockw) {
			if (hnei->angle < nei1->angle) {
				nd1->co_clockw = nei1;
			}
			else {
				nd1->clockw = nei1;
			};
		};
	
		nd1->neigh = nei1;
	
	
		nei2 = (struct nlist *) malloc(sizeof(struct nlist));
		nei2->nr = nr1;
		nei2->orig = 0;
		nei2->next = nd2->neigh;

		nei2->angle = angle (nd2, nd1);
		hnei = nd2->clockw;
		
		while ((hnei->angle < nei2->angle) && (hnei != nd2->co_clockw)) {
			hnei = hnei->clockw;
		};
		
		if (hnei->angle < nei2->angle) {
			hnei = hnei->clockw;
		};
		
		nei2->co_clockw = hnei->co_clockw;
		nei2->clockw = hnei;
		hnei->co_clockw = nei2;
		nei2->co_clockw->clockw = nei2;

		if (hnei == nd2->clockw) {
			if (hnei->angle < nei2->angle) {
				nd2->co_clockw = nei2;
			}
			else {
				nd2->clockw = nei2;
			};
		};
	
		nd2->neigh = nei2;

		nei1->node = nd2;
		nei2->node = nd1;
	};
	
} /* add_edge */





void triangulation (Sgraph sgraph)
			/* Hauptprozedur fuer die Triangulierung. */
              

{
	struct polygon *pl;
	
	if (gsize > 2) {
		regularisation (sgraph);
		
		if (plan == FALSE) {
			return;
		};
		
	};
	
	decomposition (sgraph);
	
	if (plan == FALSE) {
		return;
	};
		
	pl = polygonlist;
	
	while (pl != NULL) {
		polygon_triangulation (sgraph, pl);
		pl = pl->nextpoly;
	};
	
	make_exterior_face_path ();
	
	free_polygon (polygonlist);
	
} /* triangulation */
	



void color_switch (Sgraph sgraph)
			/* Prozedur fuer den Menuepunkt "hide/show added edges". */
              

{
	Sedge e;
	Snode n;
		
	for_all_nodes (sgraph, n) {
		for_sourcelist (n, e) {
			if ((int) edge_get (graphed_edge (e), EDGE_COLOR) == 13) {
				edge_set (graphed_edge (e), EDGE_COLOR, 0, 0);
			}
			else {
				if ((int) edge_get (graphed_edge (e), EDGE_COLOR) == 0) {
					edge_set (graphed_edge (e), EDGE_COLOR, 13, 0);
				};
			};
		} end_for_sourcelist (n, e);
	} end_for_all_nodes (sgraph, n);
	
} /* color_switch */


void color_remove(Sgraph sgraph)
			/* Prozedur fuer den Menuepunkt "remove added edges". */
			/* Added MH 6/10/91	*/
              

{
	Sedge e;
	Snode n;
	Slist	to_remove, l;

	to_remove = empty_slist;

	for_all_nodes (sgraph, n) {
		for_sourcelist (n, e) {
			if (((int) edge_get (graphed_edge (e), EDGE_COLOR) == 13) ||
			    ((int) edge_get (graphed_edge (e), EDGE_COLOR) == 0)) {
				if (sgraph->directed || unique_edge(e)) {
					to_remove = add_to_slist (to_remove,
						make_attr (ATTR_DATA, (char *)e));
				}
			}
		} end_for_sourcelist (n, e);
	} end_for_all_nodes (sgraph, n);

	for_slist (to_remove, l) {
		remove_edge (attr_data_of_type (l, Sedge));
	} end_for_slist (to_remove, l);
} /* color_switch */




void free_polygon (struct polygon *p)
			/* Gibt den Speicherplatz von Polygon "p" frei. */
                  

{
	if (p->nextpoly != NULL) {
		free_polygon (p->nextpoly);
	}
	else {
		if (p->firstdesc != NULL) {
			free_polynode (p->firstnode, p);
		};
		
		free (p);
	};
	
} /* free_polygon */




void free_polynode (struct polynode *pn, struct polygon *p)
			/* Gibt den Speicherplatz eines Knotens "pn" des */
			/* Polygons "p" frei.                            */
                    
                  

{
	if ((pn->next != NULL) && (pn->next != p->firstnode)) {
		free_polynode (pn->next, p);
	}
	else {
		free (pn);
	};
	
} /* free_polynode */
