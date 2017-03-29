/* (C) Universitaet Passau 1986-1994 */
/************************************************************************/
/*									*/
/*                            M  A  I  N  .  C 				*/
/*									*/
/************************************************************************/


#include "sugiyama_export.h"
#include "recycle.h"


#include "sgraph/slist.h"
#include "sgraph/sgraph.h"
#include "sgraph/sgraph_interface.h"
#include "sgraph/graphed.h"
#include "sgraph/algorithms.h"

#include "utils/utils_export.h"

#include <xview/xview.h>

extern Sgraph make_directed_copy_of_sgraph(Sgraph g);

Global int maxlevel = 1;	/* Set to 1 to correct possible but in */
				/* starting, MH 12/10/91               */
Global int nodes_of_level[SIZE];


Local int node_nr (Sgraph g);


/* For compatibility only */
Global bool	sugiyama (Sgraph sgraph, int horizontal_distance, int vertical_distance)
{
	sugiyama_settings.horizontal_distance = horizontal_distance;
	sugiyama_settings.vertical_distance   = vertical_distance;

	sugiyama_layout (sgraph, sugiyama_settings);
	return TRUE;
}


Global bool	sugiyama_pure_algorithm (Sgraph g, Sgraph original_g)
{
	bool c;
	Slist up_arcs = empty_slist; /* Liste der umgedrehten Kanten	*/
	int	i;
	int	max_nodes_of_level;
	
	maxlevel = 2; /* Just a little workaround ... MH 12/10/91 */
	
	if (g != empty_graph)
	{
		c = find_cycles(g);
		if (c)
		{	
			up_arcs = decycle( g, sugiyama_settings.up );
		}
	
		make_hierarchy(g, sugiyama_settings.leveling);

		if (maxlevel > SIZE) {
			error ("Graph is too large\n");
			return FALSE;
		}
		else {
			if (maxlevel == 1) {
				error ("Graph has only one level\n");
				return FALSE;
			}
		}

		add_dummies(g);
		init_positions(g);

		max_nodes_of_level = 0;
		for (i=0; i<maxlevel; i++) {
			max_nodes_of_level = maximum (nodes_of_level[i],
				max_nodes_of_level);
		}
		if (max_nodes_of_level > SIZE) {
			message ("Graph is too wide\n");
			return FALSE;
		}

		reduce_crossings(g,
			sugiyama_settings.reduce_crossings_algorithm);
		improve_positions(g);
		set_horizontal_positions(g,
			original_g,
			sugiyama_settings.horizontal_distance);
		set_vertical_positions(g,
			original_g,
			sugiyama_settings.vertical_distance);
		remove_dummies(g, original_g);
		if(up_arcs != NULL) {
			rechange_arcs( up_arcs, original_g );
		}
		if(up_arcs != NULL) {
			change_order_pos ( g, up_arcs );
		}
	
		return TRUE;
	}
	else {
		return	FALSE;
	}
}

/************************************************************************/
/*									*/
/*			    sugiyama layout				*/
/*									*/
/************************************************************************/


void			call_sugiyama_layout (Sgraph_proc_info info)
{
	Sgraph	sgraph;

	sgraph = info->sgraph;

	info->recompute   = TRUE;
	info->recenter    = TRUE;

	if (sgraph == empty_sgraph) {
		error ("No graph selected\n");	
	} else if (sgraph->nodes == empty_node) {
		error ("Empty graph\n");	
	} else {
		sugiyama_layout (sgraph, sugiyama_settings);
	}

}

int	sugiyama_layout (Sgraph sgraph, Sugiyama_settings settings)
{
	Sgraph	save_sgraph;
	int	successful;
	Snode	n, node;
	Sedge	e, edge;


	sugiyama_settings = settings;

	if( node_nr( sgraph ) == 1 ) {
		return TRUE;
	}

	if( !sgraph->directed ){
		save_sgraph = make_directed_copy_of_sgraph( sgraph );
	} else {
		save_sgraph = make_copy_of_sgraph( sgraph );
	}

	successful = sugiyama_pure_algorithm (save_sgraph, sgraph);
	
	if (successful) {

		int	*pos, *new_pos, i, count, nr, s, t, fault;
		Edgeline	el;
		Snode target;

		for_all_nodes (sgraph, n) {
			node = attr_data_of_type( n, Snode );
			n->x = node->x;
			n->y = node->y;
		} end_for_all_nodes ( sgraph, n );

		for_all_nodes( sgraph, n ) {

		    count = 30;	
		    for_sourcelist (n, e) {

			target = e->tnode;
					
			if( (sgraph->directed) || (n == sedge_real_source(e))) {

				el = (Edgeline)NULL;
				if (n != target) {
				
					edge = attr_data_of_type( e, Sedge );
					pos = (attr_data_of_type(edge ,int *));


				} else {
					pos = (int *)calloc(9,sizeof(int));
					pos[0] = n->x;
					pos[1] = n->y;
					pos[2] = n->x - count + 25;
					pos[3] = n->y - count;
					pos[4] = n->x + count - 25;
					pos[5] = n->y - count;
					pos[6] = n->x;
					pos[7] = n->y; 
					pos[8] = 0;
					count = count + 15;
				}

				nr = 0;
				for (i=0; pos[i] != 0; i += 2) {
					nr += 2;
				}

				if (n->y > target->y) {

					fault = 0;
					/* wenn Fehler vorliegt */
					for (i=1; i<=nr-3; i += 2) {
						if (pos[i] < pos[i+2]) {
							fault = 1;
						}
					}

					if (fault == 1) {

						new_pos = (int *)calloc( nr + 2, sizeof(int));
						new_pos[0]       = pos[0];
						new_pos[1]       = pos[1];
						new_pos[nr-2] = pos[nr-2];
						new_pos[nr-1] = pos[nr-1];
						new_pos[nr]   = 0;
		
						s = nr - 3;
						t = 2;
						/* beachte: erst x-Wert, dann y-Wert   */
						for( i=s; i>=3; i -= 2 )
						{
							new_pos[t]   = pos[i-1];	/* x-Wert */
							new_pos[t+1] = pos[i];	        /* y-Wert */
							t += 2;
						}
							for (i=0; new_pos[i] != 0; i += 2)
							el = add_to_edgeline (el, new_pos[i], new_pos[i+1]);

					} else {

						for (i=0; pos[i] != 0; i += 2) {
							el = add_to_edgeline (el, pos[i], pos[i+1]);
						}
					}	

				} else {

					/* falsche Koordinaten beim Abwaertspfeil */
					if ((n->y < target->y) && (pos[1] > pos[nr-1])) {

						new_pos = (int *)calloc( nr + 2, sizeof(int));
						new_pos[0]       = pos[nr-2];
						new_pos[1]       = pos[nr-1];
						new_pos[nr-2]    = pos[0];
						new_pos[nr-1]    = pos[1];
						new_pos[nr]      = 0;
		
						s = nr - 3;
						t = 2;
						/* beachte: erst x-Wert, dann y-Wert   */
						for( i=s; i>=3; i -= 2 ) {
							new_pos[t] = pos[i-1];	/* x-Wert */
							new_pos[t+1] = pos[i];	/* y-Wert */
							t += 2;
						}
						for (i=0; new_pos[i] != 0; i += 2) {
							el = add_to_edgeline (el, new_pos[i], new_pos[i+1]);
						}

					} else {

						for (i=0; pos[i] != 0; i += 2) {
							el = add_to_edgeline (el, pos[i], pos[i+1]);
						}
					}	
				}
											
				edge_set (graphed_edge(e),
					ONLY_SET,
					EDGE_LINE,el->suc,
					0);
			}

 		    } end_for_sourcelist (n, e);
 	 
		    node_set (graphed_node(n),
			ONLY_SET,
			NODE_POSITION, n->x, n->y,
			0);

		} end_for_all_nodes (sgraph, n);
	}
	return successful;
}

Local int node_nr (Sgraph g)
{
	int nr = 0;
	Snode node;

	for_all_nodes( g, node )
	{
		nr++;
		if( nr > 1 ) break;
	} end_for_all_nodes( g, node );

	return( nr );
}


void menu_sugiyama_layout_subframe (Menu menu, Menu_item menu_item)
    		     		/* The menu from which it is called	*/
         	          	/* The menu item from ...		*/
{
	save_sugiyama_settings ();
	show_sugiyama_subframe (NULL);
}


void menu_sugiyama_layout (Menu menu, Menu_item menu_item)
    		     		/* The menu from which it is called	*/
         	          	/* The menu item from ...		*/
{
	save_sugiyama_settings ();
	call_sgraph_proc (call_sugiyama_layout, NULL);
}


int	sugiyama_left_to_right_layout (Sgraph sgraph, Sugiyama_settings settings)
{
	int tmp;
	Sugiyama_settings settings_backup;

	settings_backup = settings;

	/* Swap x and y */
	tmp = settings.vertical_distance;
	settings.vertical_distance = settings.horizontal_distance;
	settings.horizontal_distance = tmp;
	tmp = settings.size_defaults_y;
	settings.size_defaults_y = settings.size_defaults_x;
	settings.size_defaults_x = tmp;

	turn_right_sgraph (sgraph);
	swap_width_and_height_in_nodes (sgraph);
	save_sugiyama_settings();
	sugiyama_layout (sgraph, sugiyama_settings);
	turn_left_sgraph (sgraph);
	swap_width_and_height_in_nodes (sgraph);

	settings = settings_backup;

	return TRUE;
}


void	call_sugiyama_left_to_right_layout (Sgraph_proc_info info)
{

	if (info->sgraph == empty_sgraph) {
		error ("No graph selected\n");	
	} else if (info->sgraph->nodes == empty_node) {
		error ("Empty graph\n");	
	} else {
		sugiyama_left_to_right_layout (info->sgraph, sugiyama_settings);
	}
	info->recompute = TRUE;
	info->recenter  = TRUE;
}



void menu_sugiyama_left_to_right_layout (Menu menu, Menu_item menu_item)
    		     		/* The menu from which it is called	*/
         	          	/* The menu item from ...		*/
{
	save_sugiyama_settings ();
	call_sgraph_proc (call_sugiyama_left_to_right_layout, NULL);
}
