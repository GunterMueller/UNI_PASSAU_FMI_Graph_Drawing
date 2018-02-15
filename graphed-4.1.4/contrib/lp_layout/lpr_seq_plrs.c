#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>

#include "lpr_nnode.h"
#include "lpr_eedge.h"
#include "lpr_ggraph.h"
#include "lpr_lrp.h"
#include "lpr_plr_system.h"


void sequentialize_plrs(lpr_Graph prod, plr_System prod_system, int lpr_grid)
{
	lpr_Nodelist	cur_nodelist;
	lpr_Edgelist	cur_edgelist, cur_pes, in_cons, out_cons, cons;
	int			index, segments;
	Dependency_list	cur_dep_list, new_dep_entry;
	plrs_Node		plrs_node, last_node = NULL;
	plrs_Edge		cur_plrs_edge, plrs_edge;


	FOR_LPR_NODELIST( prod->nodes, cur_nodelist )										/* Durchlaufe die Kanten der rechten Seite.	*/
		FOR_LPR_EDGELIST( cur_nodelist->node->source_edges, cur_edgelist )
			if ( cur_edgelist->edge->edge_type == lpr_RHS_EDGE )
			{
				FOR_LPR_EDGELIST( cur_edgelist->edge->pes_array[0], cur_pes )				/* Setze die S2-Listen der pes-Kanten des er-	*/
					cur_pes->edge->S2_list = NULL;								/* sten Segments zur"uck.				*/
				END_FOR_LPR_EDGELIST( cur_edgelist->edge->pes_array[0], cur_pes );

				index = 0;													/* Ein Index wird mitgez"ahlt, um an die rich-	*/
				FOR_DEP_LIST( cur_edgelist->edge->S_list, cur_dep_list )					/* tige pes zu kommen.					*/
					last_node = NULL;
					FOR_LPR_EDGELIST( cur_edgelist->edge->pes_array[index], cur_pes )			/* Durchlaufe jetzt die pes				*/

						plrs_node = create_plrs_node();							/* Erzeuge eine Kopie f"ur den alten Knoten	*/
						strcpy(plrs_node->info, "S-E");							/* und speichere ihn entsprechend ab.		*/
						plrs_node->new_node = 1;

						new_dep_entry = create_dep_list_with_node( plrs_node ); 
						cur_pes->edge->S2_list = add_dep_to_dep_list( cur_pes->edge->S2_list, new_dep_entry );
	
						if ( cur_dep_list->node->is_x == 1 )
						{
							plrs_node->is_x = 1;
							prod_system->x_graph = add_plrs_node( prod_system->x_graph, plrs_node );
						}
						else
						{
							plrs_node->is_x = 0;
							prod_system->y_graph = add_plrs_node( prod_system->y_graph, plrs_node );
						}
	
						if ( cur_pes == cur_edgelist->edge->pes_array[index] )			/* Handelt es sich um den 1. im pes so erbt er	*/
						{												/* alle einlaufenden Kanten.				*/
							FOR_PLRS_EDGE_TARGET(cur_dep_list->node, cur_plrs_edge)  
								plrs_edge = create_plrs_edge( cur_plrs_edge->source, cur_plrs_edge->length, plrs_node );
								add_plrs_edge( plrs_edge );
							END_FOR_PLRS_EDGE_TARGET(cur_dep_list->node, cur_plrs_edge);
						}
					  	if ( cur_pes == cur_edgelist->edge->pes_array[index]->pre )			/* oder ist es der letzte so erbt er alle ausl.	*/
						{												/* Kanten.							*/		
							FOR_PLRS_EDGE_SOURCE(cur_dep_list->node, cur_plrs_edge)  
								plrs_edge = create_plrs_edge(  plrs_node, cur_plrs_edge->length, cur_plrs_edge->target );
								add_plrs_edge( plrs_edge );
							END_FOR_PLRS_EDGE_SOURCE(cur_dep_list->node, cur_plrs_edge);
						}
						if (last_node != NULL)
						{
							plrs_edge = create_plrs_edge( last_node, lpr_grid, plrs_node );
							add_plrs_edge( plrs_edge );
						}								
							
						last_node = plrs_node;
					END_FOR_LPR_EDGELIST( cur_edgelist->edge->pes_array[index], cur_pes );
					index++;
				END_FOR_DEP_LIST( cur_edgelist->edge->S_list, cur_dep_list );
			}
		END_FOR_LPR_EDGELIST( cur_nodelist->node->source_edges, cur_edgelist );
	END_FOR_LPR_NODELIST( prod->nodes, cur_nodelist );

	FOR_LPR_NODELIST( prod->nodes, cur_nodelist )							
		FOR_LPR_EDGELIST( cur_nodelist->node->source_edges, cur_edgelist )
			if ( cur_edgelist->edge->edge_type == lpr_RHS_EDGE )
			{
				free_dep_list( cur_edgelist->edge->S_list );							/* Gib die S-Listen frei. Diese werden gleich 	*/
				cur_edgelist->edge->S_list = NULL;									/* wieder anhand der S2-Listen neuberechnet.	*/
			}
		END_FOR_LPR_EDGELIST( cur_nodelist->node->source_edges, cur_edgelist )
	END_FOR_LPR_NODELIST( prod->nodes, cur_nodelist );

	in_cons 	= copy_lpr_edgelist( prod->IN_embeddings );								/* Das gleiche geschieht jetzt noch mit den 	*/
	out_cons	= copy_lpr_edgelist( prod->OUT_embeddings );								/* Einbettungsregeln. Verkette sie tempor"ar	*/
	cons	= add_edgelist_to_lpr_edgelist( in_cons, out_cons );								/* um eine Schleife zu sparen				*/

	FOR_LPR_EDGELIST( cons, cur_edgelist )								
		if ( cur_edgelist->edge->edge_type == lpr_IN_CONN_REL )
		{
			FOR_LPR_EDGELIST( cur_edgelist->edge->pes_array[0], cur_pes )
				cur_pes->edge->S2_list = NULL;
			END_FOR_LPR_EDGELIST( cur_edgelist->edge->pes_array[0], cur_pes );
		}
		else
		{
			segments = get_optimal_edge_of_lpr_edge( prod, cur_edgelist->edge)->bends + 1;
			FOR_LPR_EDGELIST( cur_edgelist->edge->pes_array[segments - 1], cur_pes )
				cur_pes->edge->S2_list = NULL;
			END_FOR_LPR_EDGELIST( cur_edgelist->edge->pes_array[segments - 1], cur_pes );
		}


		index = 0;
		FOR_DEP_LIST( cur_edgelist->edge->S_list, cur_dep_list )
			last_node = NULL;	  
			FOR_LPR_EDGELIST( cur_edgelist->edge->pes_array[index], cur_pes )
				
				plrs_node = create_plrs_node();
				strcpy(plrs_node->info, "S-C");
				plrs_node->new_node = 1;
				new_dep_entry = create_dep_list_with_node( plrs_node ); 
				cur_pes->edge->S2_list = add_dep_to_dep_list( cur_pes->edge->S2_list, new_dep_entry );

				if ( cur_dep_list->node->is_x == 1 )
				{
					plrs_node->is_x = 1;
					prod_system->x_graph = add_plrs_node( prod_system->x_graph, plrs_node );
				}
				else
				{
					plrs_node->is_x = 0;
					prod_system->y_graph = add_plrs_node( prod_system->y_graph, plrs_node );
				}

				if ( cur_pes == cur_edgelist->edge->pes_array[index] )
				{
					FOR_PLRS_EDGE_TARGET(cur_dep_list->node, cur_plrs_edge)  
						plrs_edge = create_plrs_edge( cur_plrs_edge->source, cur_plrs_edge->length, plrs_node );
						add_plrs_edge( plrs_edge );
					END_FOR_PLRS_EDGE_TARGET(cur_dep_list->node, cur_plrs_edge);
				}
				if ( cur_pes == cur_edgelist->edge->pes_array[index]->pre )
				{
					FOR_PLRS_EDGE_SOURCE(cur_dep_list->node, cur_plrs_edge)  
						plrs_edge = create_plrs_edge(  plrs_node, cur_plrs_edge->length, cur_plrs_edge->target );
						add_plrs_edge( plrs_edge );
					END_FOR_PLRS_EDGE_SOURCE(cur_dep_list->node, cur_plrs_edge);
				}
				if (last_node != NULL)
				{
					plrs_edge = create_plrs_edge( last_node, lpr_grid, plrs_node );
					add_plrs_edge( plrs_edge );
				}								
						
				last_node = plrs_node;
			END_FOR_LPR_EDGELIST( cur_edgelist->edge->pes_array[index], cur_pes );
			index++;
		END_FOR_DEP_LIST( cur_edgelist->edge->S_list, cur_dep_list );
	END_FOR_LPR_EDGELIST( cons, cur_edgelist );

	FOR_LPR_EDGELIST( cons, cur_edgelist )
			free_dep_list( cur_edgelist->edge->S_list );
			cur_edgelist->edge->S_list = NULL;
	END_FOR_LPR_EDGELIST( cons, cur_edgelist )

	free_lpr_edgelist( cons );
}		 
