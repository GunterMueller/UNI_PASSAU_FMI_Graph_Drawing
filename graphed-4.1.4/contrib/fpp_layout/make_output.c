/* (C) Universitaet Passau 1986-1994 */
/****************************************************************
 **                                                            **
 **     In "make_output.c" werden die neuen Koordinaten        **
 **     an die "GraphEd"-Datenstruktur uebergeben.             **
 **     Zusaetzlich stehen hier die Prozeduren, die den        **
 **     Speicherplatz der Adjazenzliste wieder freigeben.      **
 **                                                            **
 ****************************************************************/


#include "decl.h"



void make_the_graph (Sgraph sgraph)
			/* Uebergibt die neuen Koordinaten an "sgraph". */
              

{
	int zeronode, x_shift, y_shift;
	struct fpp_node *pgraph;
	Snode n;
	
	x_shift = 0;
	y_shift = 0;
	
		if (graph != NULL) {
			pgraph = graph;
			zeronode = 0;
			
			while (pgraph != NULL) {
				if ((pgraph != NULL) && (pgraph->x_offset == 0) && (pgraph->y_coord == 0)) {
					zeronode++;
				};
				
				pgraph = pgraph->next;
			};
			
			if ((zeronode > 1) || (plan == FALSE)) {
				error ("Planar embedding is wrong!");
				plan = FALSE;
				return;
			};
			
			pgraph = graph;
		
			for_all_nodes (sgraph, n) {
				n->x = (new_zero_x + (grid * pgraph->x_offset));
				
				if ((n->x < 0) && (n->x < x_shift)) {
					x_shift = n->x;
				};
				
				n->y = (new_zero_y - (grid * pgraph->y_coord));
				
				if ((n->y < 0) && (n->y < y_shift)) {
					y_shift = n->y;
				};
				
				if ((n != last_node_in_graph(sgraph)) && (pgraph->next != NULL)) {
					pgraph = pgraph->next;
				};
			} end_for_all_nodes (sgraph, n);
			
			
			if ((x_shift < 0) || (y_shift < 0)) {
				if (x_shift < 0) {
					x_shift = x_shift - grid - grid;
				};
				
				if (y_shift < 0) {
					y_shift = y_shift - grid - grid;
				};
				
				for_all_nodes (sgraph, n) {
					n->x = n->x - x_shift;
					n->y = n->y - y_shift;
				} end_for_all_nodes (sgraph, n);
			}; 
			
			
			if (graph != NULL) {
				free_the_graph (graph);
			};
		}
		else {
			return;
		};
	
	/* message ("Used grid size is %d x %d.\n", grid, grid); */

} /* make_the_graph */



void find_grid_origin (void)
			/*Legt den Ausgangspunkt zum Zeichnen auf das Gitter fest. */

{
	int zx, zy;
	
	zx = new_zero_x;
	zy = new_zero_y;
	
	if ((grid < zx) && (grid < zy) && (grid > 1)) {
		new_zero_x = grid;
		new_zero_y = grid;
	
		while (new_zero_x < zx) {
			new_zero_x = new_zero_x + grid;
		};
		
		while (new_zero_y < zy) {
			new_zero_y = new_zero_y + grid;
		};
		
		new_zero_y -= grid;
	};

} /* find_grid_zero */



void free_the_graph (struct fpp_node *nd)
			/* Gibt den Speicherplatz von Knoten "nd" frei. */
                

{	
	if (nd != NULL) {
		if (nd->next != NULL) {
			free_the_graph (nd->next);
		}
		else {
			if (nd->neigh != NULL) {
				free_neigh (nd->neigh);
			};
			
			free (nd);
		};
	};
}



void free_neigh (struct nlist *ng)
			/* Gibt den Speicherplatz von Nachbarknoten "ng" frei. */
                 

{
	if (ng != NULL) {
		if (ng->next != NULL) {
			free_neigh (ng->next);
		}
		else {
			free (ng);
		};
	};
}
