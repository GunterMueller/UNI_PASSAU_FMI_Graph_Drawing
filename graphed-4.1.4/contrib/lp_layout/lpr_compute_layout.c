/************************************************************************************************/
/*																*/
/*					FILE: lpr_compute_layout.c							*/
/*																*/
/*	Hier wird anhand des berechneten GLRS des Resultatsgraphen das endg"ultige Layout		*/
/* 	berechnet.														*/
/*																*/
/************************************************************************************************/

#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>

#include "lpr_nnode.h"
#include "lpr_eedge.h"
#include "lpr_ggraph.h"
#include "lpr_lrp.h"
#include "lp_edgeline.h"


/************************************************************************************************
Function	: compute_layout
Input		: lpr_Node node
Output	: void
Description	: Entspricht der Funktion compute_layout aus der Theorie. Allerdings ist die Funktion
		  stark an die Graphed-Strukturen angepasst.
************************************************************************************************/
void compute_layout(lpr_Node node)
{
	lpr_Graph 		prod = node->applied_production;
	lpr_Nodelist	cur_nodelist;
	lpr_Edgelist	cur_edgelist, cur_eh_edgelist;
	lpr_Node		lpr_node;
	lpr_Edge 		lpr_edge;

	Node			graphed_node;
	Edgeline		graphed_line;
	lp_Edgeline		line;

	Dependency_list	cur_dep;

	int			switcher;
	int			x_pos, y_pos, x_size, y_size;

	
	FOR_LPR_NODELIST( prod->nodes, cur_nodelist )							/* Zun"achst die Positionszu-	*/
		if ( cur_nodelist->node->applied_production == NULL )					/* weisung und Gr"ossenberech-	*/
		{											/* nung f"ur die Knoten.	*/
			lpr_node = cur_nodelist->node;
			x_pos  = ( lpr_node->left->px + lpr_node->right->px ) / 2;
			y_pos  = ( lpr_node->down->py + lpr_node->up->py ) / 2;
			x_size = lpr_node->right->px - lpr_node->left->px;
			y_size = lpr_node->up->py - lpr_node->down->py;

			graphed_node = lpr_node->GRAPH_iso;						/* Jetzt bekommt der Graphed-	*/
			node_set(graphed_node, ONLY_SET, NODE_POSITION, x_pos, y_pos, 0);		/* Knoten seine neuen Koordi-	*/
			node_set(graphed_node, ONLY_SET, NODE_SIZE, x_size, y_size, 0);			/* naten zugewiesen.		*/
			node_set(graphed_node, ONLY_SET, NODE_NEI, NO_NODE_EDGE_INTERFACE, 0 );
		}
		else
			compute_layout( cur_nodelist->node );						/* Falls Knoten nicht terminal	*/
	END_FOR_LPR_NODELIST( prod->nodes, cur_nodelist );						/* dann weiter im Top-Down-	*/
													/* Durchlauf.			*/

	FOR_LPR_NODELIST( prod->nodes, cur_nodelist );							/* Jetzt zu den Kanten.		*/
		FOR_LPR_EDGELIST( cur_nodelist->node->source_edges, cur_edgelist )
			if (( cur_edgelist->edge->edge_type == lpr_RHS_EDGE ) && ( cur_edgelist->edge->EH != NULL ))
			{
				FOR_LPR_EDGELIST ( cur_edgelist->edge->EH->edges, cur_eh_edgelist )	/* Berechne alle Kanten aus 	*/
					lpr_edge = cur_eh_edgelist->edge;				/* aus EH(RHS_EDGE).		*/
					line = NULL;							/* Initialisiere neue Graphed-	*/			
					if ( lpr_edge->S_list->node->is_x == 1 )			/* Kante.			*/
					{								/* Falls die Linie hor. be-	*/
						switcher = 0;						/* ginnt, nimm x vom 1. und 	*/
						FOR_DEP_LIST( lpr_edge->S_list, cur_dep )		/* y vom 2. El. der S-liste.	*/

							if ( cur_dep == lpr_edge->S_list )
							{
								if ( switcher == 1 )			/* 3. und y vom 2., usw. je-	*/
								{					/* weils abwechselnd.		*/
									line = add_to_lp_edgeline( line, -1 , cur_dep->node->py );
								}
								else
								{
									line = add_to_lp_edgeline( line, cur_dep->node->px, -1 );
								}
								switcher = 1 - switcher;
							}
							else
							{
								if ( switcher == 1 )			/* 3. und y vom 2., usw. je-	*/
								{
									line = add_to_lp_edgeline( line, cur_dep->pre->node->px , cur_dep->node->py );
								}					/* weils abwechselnd.		*/
								else
								{
									line = add_to_lp_edgeline( line, cur_dep->node->px, cur_dep->pre->node->py );
								}
								switcher = 1 - switcher;
							}	

						END_FOR_DEP_LIST( lpr_edge->S_list, cur_dep );


						if ( switcher == 0 )					/* 3. und y vom 2., usw. je-	*/
						{
							line = add_to_lp_edgeline( line, -1 , lpr_edge->S_list->pre->node->py );
						}							/* weils abwechselnd.		*/
						else
						{
							line = add_to_lp_edgeline( line, lpr_edge->S_list->pre->node->px, -1 );
						}
						switcher = 1 - switcher;

					}
					else								/* Falls die Linie ver. be-	*/
					{								/* ginnt, nimm x vom 2. und 	*/

						switcher = 1;						/* ginnt, nimm x vom 1. und 	*/
						FOR_DEP_LIST( lpr_edge->S_list, cur_dep )		/* y vom 2. El. der S-liste.	*/

							if ( cur_dep == lpr_edge->S_list )
							{
								if ( switcher == 1 )			/* 3. und y vom 2., usw. je-	*/
								{
									line = add_to_lp_edgeline( line, -1 , cur_dep->node->py );
								}					/* weils abwechselnd.		*/
								else
								{
									line = add_to_lp_edgeline( line, cur_dep->node->px, -1 );
								}
								switcher = 1 - switcher;
							}
							else
							{
								if ( switcher == 1 )			/* 3. und y vom 2., usw. je-	*/
								{
									line = add_to_lp_edgeline( line, cur_dep->pre->node->px , cur_dep->node->py );
								}					/* weils abwechselnd.		*/
								else
								{
									line = add_to_lp_edgeline( line, cur_dep->node->px, cur_dep->pre->node->py );
								}
								switcher = 1 - switcher;
							}	

						END_FOR_DEP_LIST( lpr_edge->S_list, cur_dep );


						if ( switcher == 0 )					/* 3. und y vom 2., usw. je-	*/
						{
							line = add_to_lp_edgeline( line, -1 , lpr_edge->S_list->pre->node->py );
						}							/* weils abwechselnd.		*/
						else
						{
							line = add_to_lp_edgeline( line, lpr_edge->S_list->pre->node->px, -1 );
						}
						switcher = 1 - switcher;
					}


					if ( lpr_edge->S_list == lpr_edge->S_list->suc )		/* Falls Kanten nur aus einem Seg-	*/
													/* ment besteht,			*/
						if ( lpr_edge->S_list->node->is_x == 1 )		/* und diese Kante vertikal l"auft	*/
							if ( lpr_edge->source->down->py < lpr_edge->target->down->py )
													/* falls sie nach oben l"auft, dann	*/
							{
								line->y = lpr_edge->source->up->py;	/* setze oberen Rand der Quelle als	*/
								line->suc->y = lpr_edge->target->down->py;
							}
							else						/* y-Koordinate.			*/
							{
								line->y = lpr_edge->source->down->py;	/* sonst den unteren.			*/
								line->suc->y = lpr_edge->target->up->py;
							}
						else							/* l"auft sie horizontal und nach	*/	
							if ( lpr_edge->source->left->px < lpr_edge->target->left->px )
													/* sonst den linken Rand.		*/
							{
								line->x = lpr_edge->source->right->px;
								line->suc->x = lpr_edge->target->left->px;
							}
							else
							{
								line->x = lpr_edge->source->left->px;
								line->suc->x = lpr_edge->target->right->px;
							}
					else
					{
						if ( lpr_edge->S_list->node->is_x == 1 )
							if ( line->suc->y < lpr_edge->source->down->py )
								line->y = lpr_edge->source->down->py;
							else
								line->y = lpr_edge->source->up->py;
						else
							if ( line->suc->x < lpr_edge->source->left->px )
								line->x = lpr_edge->source->left->px;
							else
								line->x = lpr_edge->source->right->px;

						if ( lpr_edge->S_list->pre->node->is_x == 1 )
							if ( line->pre->pre->y < lpr_edge->target->down->py )
								line->pre->y = lpr_edge->target->down->py;
							else
								line->pre->y = lpr_edge->target->up->py;
						else
							if ( line->pre->pre->x < lpr_edge->target->left->px )
								line->pre->x = lpr_edge->target->left->px;
							else
								line->pre->x = lpr_edge->target->right->px;
					}		
							
								
	
					if ( line != NULL )						/* Wurde eine Linie erzeugt,	*/
					{								/* wird sie noch an den Enden	*/
						clip_lp_edgeline(line->suc, line, lpr_edge->GRAPH_iso->source);
													/* abgeschnitten, und dann auf*/
						clip_lp_edgeline(line->pre->pre, line->pre, lpr_edge->GRAPH_iso->target);
													/* die Graphed-Line kopiert.	*/
						graphed_line = cp_lp_edgeline_to_edgeline( line );
	
						edge_set(lpr_edge->GRAPH_iso, ONLY_SET, EDGE_LINE, graphed_line, 0 );
					}
				END_FOR_LPR_EDGELIST ( cur_edgelist->edge->EH->edges, cur_eh_edgelist );
			}
		END_FOR_LPR_EDGELIST( cur_nodelist->node->source_edges, cur_edgelist );
	END_FOR_LPR_NODELIST( prod->nodes, cur_nodelist );

}

