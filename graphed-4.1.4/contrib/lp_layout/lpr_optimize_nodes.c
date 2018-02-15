/******************************************************************************************/
/*															*/
/*						FILE: lpr_optimize_nodes.c					*/
/*															*/
/* 	In diesem Abschnitt werden einige Optimierungen von Knotenh"ohen und Knotenbreiten	*/
/* 	vorgenommen, die allerdings nicht mehr Bestand der Theorie sind. Hierbei bilden 	*/
/* 	optimize_nodes und ...2 einen Optimierungsalgorithmus, ebenso optimize_nodes3_hor	*/
/*	und optimize_nodes3_ver.										*/
/*															*/
/******************************************************************************************/

#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <sys/syscall.h>
#include <ctype.h>


#include "lpr_nnode.h"
#include "lpr_ggraph.h"
#include "lpr_optimal_layout.h"
#include "lpr_lrp.h"
#include "lpr_apply_production.h"
#include "lpr_glr_system.h"
#include "lpr_plr_system.h"
#include "lpr_compute_pos_ass.h"
#include "lpr_compute_layout.h"


/*************************************************************************************
Function	: optimize_nodes
Input		: plr_System system, lpr_Node node
Output	: void
Description	: Voraussetzung f"ur optimize_nodes2 ist es, dass Kanten die bez"uglich
		  ihres Layouts in der Produktion in Bezug auf einen Knoten exakt gegen-
		  "uberliegen, dies im Resultatsgraphen ebenso tun. Das muss aber nach
		  der Theorie nicht der Fall sein. Deshalb werden hier die Restriktionen
		  genau dieser Kanten gegenseitig vererbt, womit der gew"unschte Effekt
		  erreicht ist.
*************************************************************************************/
void optimize_nodes(plr_System system, lpr_Node node)
{
	lpr_Nodelist 	cur_nodelist;
	lpr_Graph 		prod = node->applied_production;
	plrs_Node		q2, q4;
	plrs_Node		first1, first2, last1, last2;
	lpr_Edgelist	cur_lrs_edge1, cur_lrs_edge2, cur_ehs_edge1, cur_ehs_edge2;
	int			segments1, segments2;

	FOR_LPR_NODELIST ( prod->nodes, cur_nodelist )												/* Zun"achst Bottom-Up-Durchlauf	*/
		if ( cur_nodelist->node->applied_production != NULL )
			optimize_nodes( system, cur_nodelist->node );
	END_FOR_LPR_NODELIST ( prod->nodes, cur_nodelist );

	FOR_LPR_NODELIST ( prod->nodes, cur_nodelist )												/* Betrachte jetzt alle terminalen	*/
		if ( cur_nodelist->node->applied_production == NULL )										/* Knoten bzw. deren Kanten, die im	*/
		{																		/* Resultatsgraphen liegen.		*/
			FOR_LPR_EDGELIST( cur_nodelist->node->source_edges, cur_lrs_edge1 )						/* Vergleiche zun"achst alle ausl.	*/
			   if ( cur_lrs_edge1->edge->EH != NULL )											/* Kanten eines Knoten untereinan-	*/
			   {																	/* der.					*/
				FOR_LPR_EDGELIST ( cur_lrs_edge1->edge->EH->edges, cur_ehs_edge1 )
				{
					FOR_LPR_EDGELIST( cur_nodelist->node->source_edges, cur_lrs_edge2 )
						if ( cur_lrs_edge1->edge != cur_lrs_edge2->edge )
						{
						   if ( cur_lrs_edge2->edge->EH != NULL )
						   {
							FOR_LPR_EDGELIST ( cur_lrs_edge2->edge->EH->edges, cur_ehs_edge2 )
							{
								q2 = cur_ehs_edge1->edge->S_list->node;
								q4 = cur_ehs_edge2->edge->S_list->node;
																				/* Falls beide hor. bzw. ver. und 	*/
								if ( ( ( q2->is_x && q4->is_x ) || ( !q2->is_x && !q4->is_x ) ) &&	/* ihre Startwerte gleich sind, so	*/
									( cur_lrs_edge1->edge->start_value == cur_lrs_edge2->edge->start_value ) )
								{												/* vererbe wechselseitig deren Re-	*/
									first1 = cur_lrs_edge1->edge->pes_array[0]->edge->S_list->node;	/* striktionen. Bei Kanten, die	*/
									last1 = cur_lrs_edge1->edge->pes_array[0]->pre->edge->S_list->node;
																				/* sich aufspalten, nimm jeweils 	*/
									first2 = cur_lrs_edge2->edge->pes_array[0]->edge->S_list->node;	/* erste und letzte El. in der Seq.	*/
									last2 = cur_lrs_edge2->edge->pes_array[0]->pre->edge->S_list->node;
									
									inherit_all_target_edges( first1, first2 );				/* Vererbe!					*/
									inherit_all_source_edges( last1, last2 );
								}
							}
							END_FOR_LPR_EDGELIST ( cur_lrs_edge2->edge->EH->edges, cur_ehs_edge2 );
						   }
						}
					END_FOR_LPR_EDGELIST( cur_nodelist->node->source_edges, cur_lrs_edge2 );
				}
				END_FOR_LPR_EDGELIST ( cur_lrs_edge1->edge->EH->edges, cur_ehs_edge1 );
			   }
			END_FOR_LPR_EDGELIST( cur_nodelist->node->source_edges, cur_lrs_edge1 );

			FOR_LPR_EDGELIST( cur_nodelist->node->target_edges, cur_lrs_edge1 )						/* Nun f"ur auslaufenden Kanten 	*/
			   if ( cur_lrs_edge1->edge->EH != NULL )											/* nach dem gleichen Schema.		*/
			   {
				FOR_LPR_EDGELIST ( cur_lrs_edge1->edge->EH->edges, cur_ehs_edge1 )
				{
					FOR_LPR_EDGELIST( cur_nodelist->node->target_edges, cur_lrs_edge2 )
						if ( cur_lrs_edge1->edge != cur_lrs_edge2->edge )
						{
						   if ( cur_lrs_edge2->edge->EH != NULL )
						   {
							FOR_LPR_EDGELIST ( cur_lrs_edge2->edge->EH->edges, cur_ehs_edge2 )
							{
								q2 = cur_ehs_edge1->edge->S_list->pre->node;
								q4 = cur_ehs_edge2->edge->S_list->pre->node;
							
								if ( ( ( q2->is_x && q4->is_x ) || ( !q2->is_x && !q4->is_x ) ) && 
								     ( cur_ehs_edge1->edge->end_value == cur_ehs_edge2->edge->end_value ) )
								{
									
									segments1 = get_optimal_edge_of_lpr_edge(prod,cur_lrs_edge1->edge)->bends;
									segments2 = get_optimal_edge_of_lpr_edge(prod,cur_lrs_edge2->edge)->bends;

									first1 = cur_lrs_edge1->edge->pes_array[segments1]->edge->S_list->pre->node;								
									last1 = cur_lrs_edge1->edge->pes_array[segments1]->pre->edge->S_list->pre->node;

									first2 = cur_lrs_edge2->edge->pes_array[segments2]->edge->S_list->pre->node;								
									last2 = cur_lrs_edge2->edge->pes_array[segments2]->pre->edge->S_list->pre->node;

									inherit_all_target_edges( first1, first2 );
									inherit_all_source_edges( last1, last2 );
								}	
							}
							END_FOR_LPR_EDGELIST ( cur_lrs_edge2->edge->EH->edges, cur_ehs_edge2 );
						   }
						}
					END_FOR_LPR_EDGELIST( cur_nodelist->node->target_edges, cur_lrs_edge2 );
				}
				END_FOR_LPR_EDGELIST ( cur_lrs_edge1->edge->EH->edges, cur_ehs_edge1 );
			   }
			END_FOR_LPR_EDGELIST( cur_nodelist->node->target_edges, cur_lrs_edge1 );

			FOR_LPR_EDGELIST( cur_nodelist->node->source_edges, cur_lrs_edge1 )					/* und schliesslich f"ur die ein- und die	*/
			   if ( cur_lrs_edge1->edge->EH != NULL )										/* auslaufenden Kanten.				*/
			   {
				FOR_LPR_EDGELIST ( cur_lrs_edge1->edge->EH->edges, cur_ehs_edge1 )
				{
					FOR_LPR_EDGELIST( cur_nodelist->node->target_edges, cur_lrs_edge2 )
						if ( cur_lrs_edge1->edge != cur_lrs_edge2->edge )
						{
						   if ( cur_lrs_edge2->edge->EH != NULL )
						   {
							FOR_LPR_EDGELIST ( cur_lrs_edge2->edge->EH->edges, cur_ehs_edge2 )
							{
								q2 = cur_ehs_edge1->edge->S_list->node;
								q4 = cur_ehs_edge2->edge->S_list->pre->node;

								if ( ( ( q2->is_x && q4->is_x ) || ( !q2->is_x && !q4->is_x ) ) && 
								     ( cur_ehs_edge1->edge->start_value == cur_ehs_edge2->edge->end_value ) )
								{
									
									segments2 = get_optimal_edge_of_lpr_edge(prod,cur_lrs_edge2->edge)->bends;

									first1 = cur_lrs_edge1->edge->pes_array[0]->edge->S_list->node;								
									last1 = cur_lrs_edge1->edge->pes_array[0]->pre->edge->S_list->node;

									first2 = cur_lrs_edge2->edge->pes_array[segments2]->edge->S_list->pre->node;								
									last2 = cur_lrs_edge2->edge->pes_array[segments2]->pre->edge->S_list->pre->node;

							
									inherit_all_target_edges( first1, first2 );
									inherit_all_source_edges( last1, last2 );
								} 
							}
							END_FOR_LPR_EDGELIST ( cur_lrs_edge2->edge->EH->edges, cur_ehs_edge2 );
						   }
						}
					END_FOR_LPR_EDGELIST( cur_nodelist->node->target_edges, cur_lrs_edge2 );
				}
				END_FOR_LPR_EDGELIST ( cur_lrs_edge1->edge->EH->edges, cur_ehs_edge1 );
			   }
			END_FOR_LPR_EDGELIST( cur_nodelist->node->source_edges, cur_lrs_edge1 );


		}
	END_FOR_LPR_NODELIST ( prod->nodes, cur_nodelist );
}


/*************************************************************************************
Function	: optimize_nodes2
Input		: plr_System system, lpr_Node node
Output	: void
Description	: Hier wird anhand des Restriktionensystems eine Optimierung der Knoten-
		  h"ohen und -breiten vorgenommen. Dazu wird die Minimalanforderung f"ur
		  eine dieser Gr"ossen berechnet und darauf die ausschlaggebende Restrik-
		  tion in ihrem Wert vergr"ossert, womit auch die Abst"ande minimiert wer-
		  den.
*************************************************************************************/
void optimize_nodes2(plr_System system, lpr_Node node)
{
	lpr_Nodelist 	cur_nodelist;
	lpr_Graph 		prod = node->applied_production;
	plrs_Node		left, down;
	int			min;
	plrs_Edge		cur_plrs_edge;

	FOR_LPR_NODELIST ( prod->nodes, cur_nodelist )										/* Zun"achst wieder Bottom-Up-Durchlauf	*/
		if ( cur_nodelist->node->applied_production != NULL )
			optimize_nodes2( system, cur_nodelist->node );
	END_FOR_LPR_NODELIST ( prod->nodes, cur_nodelist );

	FOR_LPR_NODELIST ( prod->nodes, cur_nodelist )
		if ( cur_nodelist->node->applied_production == NULL )								/* Betrachte alle terminalen Knoten.	*/
		{
			left = cur_nodelist->node->left;										/* Bestimme f"ur den linken Rand das Mi-	*/
			if ( left != NULL )												/* nimum der Differenz zwischen Zielposi-	*/
			{															/* tion und Restriktionenl"ange.		*/
				min  = left->out_edges->target->px - left->out_edges->length;
				FOR_PLRS_EDGE_SOURCE( left, cur_plrs_edge )
					if ( cur_plrs_edge->target->px - cur_plrs_edge->length < min ) 
						min = cur_plrs_edge->target->px - cur_plrs_edge->length;
				END_FOR_PLRS_EDGE_SOURCE( left, cur_plrs_edge );
				FOR_PLRS_EDGE_TARGET( left, cur_plrs_edge )							/* Setze jetzt die Restriktion, die f"ur 	*/
					if ( cur_plrs_edge->source->px + cur_plrs_edge->length == left->px )		/* die akt. Position gesorgt hat, neu, 	*/
						cur_plrs_edge->length = cur_plrs_edge->length + min - left->px;		/* sodass die ausl. Restriktionen gerade	*/
				END_FOR_PLRS_EDGE_TARGET( left, cur_plrs_edge );						/* noch erf"ullt werden k"onnen.		*/
			}
			down = cur_nodelist->node->down;										/* Das gleiche geschieht mit dem unteren	*/
			if ( down != NULL )												/* Rand.						*/
			{
				min  = down->out_edges->target->py - down->out_edges->length;
				FOR_PLRS_EDGE_SOURCE( down, cur_plrs_edge )
					if (  cur_plrs_edge->target->py - cur_plrs_edge->length < min ) 
						min = cur_plrs_edge->target->py - cur_plrs_edge->length;
				END_FOR_PLRS_EDGE_SOURCE( down, cur_plrs_edge );
				FOR_PLRS_EDGE_TARGET( down, cur_plrs_edge )
					if ( cur_plrs_edge->source->py + cur_plrs_edge->length == down->py )
						cur_plrs_edge->length = cur_plrs_edge->length + min - down->py;
				END_FOR_PLRS_EDGE_TARGET( down, cur_plrs_edge );
			}
		}
	END_FOR_LPR_NODELIST ( prod->nodes, cur_nodelist );
}			


/*************************************************************************************
Function	: optimize_nodes3_width
Input		: plr_System system, lpr_Node node, int lpr_grid
Output	: void
Description	: Diese und die folgende Optimierung basieren nicht wie zuvor auf dem GLRS
		  sondern rechnen auf den bereits ermittelten Koordinaten der lpr-Knoten.
		  In dieser Funktion wird die Breite eines Knoten optimiert. Dazu werden
		  alle Kanten eines Knoten, die bei diesem mit einem vertikalen Segment be-
		  ginnen oder enden, betrachtet. Zur Optimierung des linken Rands wird die
		  x-Koordinate des Segments mit minimalen Abstand zum linken Rand berechnet
		  und darauf die Koordinate dieses Rands als dieses Minimum - lpr_grid ge-
		  setzt. F"ur den rechten Rand wird analog ein Maximum berechnet, woraus
		  sich dessen neue Koordinate als Maximum + lpr_grid ergibt. Existieren
		  keine Kanten, die zur Berechnung eines Maximums beitragen, so kann der
		  rechte Rand soweit verschoben werden, dass der Knoten Einheitsbreite hat.
*************************************************************************************/
void optimize_nodes3_width(plr_System system, lpr_Node node, int lpr_grid)
{
	lpr_Nodelist 	cur_nodelist;
	lpr_Graph 		prod = node->applied_production;
	lpr_Edgelist	cur_edgelist, cur_eh_edgelist;
	plrs_Node		plrs_node;
	int			min, max;

	FOR_LPR_NODELIST ( prod->nodes, cur_nodelist )									/* Bottom-Up-Durchlauf.				*/
		if ( cur_nodelist->node->applied_production != NULL )
			optimize_nodes3_width( system, cur_nodelist->node, lpr_grid );
	END_FOR_LPR_NODELIST ( prod->nodes, cur_nodelist );

	FOR_LPR_NODELIST ( prod->nodes, cur_nodelist )									/* Betrachte die Kanten im Resultats-	*/
		if ( cur_nodelist->node->applied_production == NULL )							/* graphen.						*/
		{
			min = cur_nodelist->node->right->px;								/* Setze das Minimum auf den rechten Rand	*/

			FOR_LPR_EDGELIST( cur_nodelist->node->source_edges, cur_edgelist )
				if ( cur_edgelist->edge->EH != NULL )
				{ 
					FOR_LPR_EDGELIST( cur_edgelist->edge->EH->edges, cur_eh_edgelist )	/* Zun"achst die ausl. Kanten.		*/
						plrs_node = cur_eh_edgelist->edge->S_list->node;
						if (( plrs_node->is_x == 1 ) && ( plrs_node->px < min ))		/* Beginnt die Kante vertikal und liegt	*/
							min = plrs_node->px;							/* sie vor dem Minimum, so wird ihre	*/
					END_FOR_LPR_EDGELIST( cur_edgelist->edge->EH->edges, cur_eh_edgelist );	/* Koordinate neues Minimum.			*/
				}
			END_FOR_LPR_EDGELIST( cur_nodelist->node->source_edges, cur_edgelist );

			FOR_LPR_EDGELIST( cur_nodelist->node->target_edges, cur_edgelist )			/* Genauso f"ur die einl. Kanten.		*/
				if ( cur_edgelist->edge->EH != NULL )
				{ 
					FOR_LPR_EDGELIST( cur_edgelist->edge->EH->edges, cur_eh_edgelist )
						plrs_node = cur_eh_edgelist->edge->S_list->pre->node;
						if (( plrs_node->is_x == 1 ) && ( plrs_node->px < min ))
							min = plrs_node->px;
					END_FOR_LPR_EDGELIST( cur_edgelist->edge->EH->edges, cur_eh_edgelist );
				}
			END_FOR_LPR_EDGELIST( cur_nodelist->node->target_edges, cur_edgelist );

			if ( min < cur_nodelist->node->right->px )							/* Setze die Koordinate des linken Rands	*/
				cur_nodelist->node->left->px = min - lpr_grid;						/* neu.						*/



			max = cur_nodelist->node->left->px;									/* Das gleiche nochmal f"ur den rechten	*/
																	/* Rand.						*/
			FOR_LPR_EDGELIST( cur_nodelist->node->source_edges, cur_edgelist )
				if ( cur_edgelist->edge->EH != NULL )
				{ 
					FOR_LPR_EDGELIST( cur_edgelist->edge->EH->edges, cur_eh_edgelist )
						plrs_node = cur_eh_edgelist->edge->S_list->node;
						if (( plrs_node->is_x == 1 ) && ( plrs_node->px > max ))
							max = plrs_node->px;
					END_FOR_LPR_EDGELIST( cur_edgelist->edge->EH->edges, cur_eh_edgelist );
				}
			END_FOR_LPR_EDGELIST( cur_nodelist->node->source_edges, cur_edgelist );

			FOR_LPR_EDGELIST( cur_nodelist->node->target_edges, cur_edgelist )
				if ( cur_edgelist->edge->EH != NULL )
				{ 
					FOR_LPR_EDGELIST( cur_edgelist->edge->EH->edges, cur_eh_edgelist )
						plrs_node = cur_eh_edgelist->edge->S_list->pre->node;
						if (( plrs_node->is_x == 1 ) && ( plrs_node->px > max ))
							max = plrs_node->px;
					END_FOR_LPR_EDGELIST( cur_edgelist->edge->EH->edges, cur_eh_edgelist );
				}
			END_FOR_LPR_EDGELIST( cur_nodelist->node->target_edges, cur_edgelist );

			if ( max > cur_nodelist->node->left->px )
				cur_nodelist->node->right->px = max + lpr_grid;	
			else																/* Kein Kanten zur Maximumberechnung	*/
				cur_nodelist->node->right->px = cur_nodelist->node->left->px + 2*lpr_grid;			/* vorhanden, dann verschiebe rechten	*/
																			/* Rand bis zur Einheitsbreite.		*/
		}
	END_FOR_LPR_NODELIST ( prod->nodes, cur_nodelist );
}

/*************************************************************************************
Function	: optimize_nodes3_height
Input		: plr_System system, lpr_Node node, int lpr_grid
Output	: void
Description	: Diese Funktion arbeitet analog zur vorherigen. s. o.
*************************************************************************************/
void optimize_nodes3_height(plr_System system, lpr_Node node, int lpr_grid)
{
	lpr_Nodelist 	cur_nodelist;
	lpr_Graph 		prod = node->applied_production;
	lpr_Edgelist	cur_edgelist, cur_eh_edgelist;
	plrs_Node		plrs_node;
	int			min, max;

	FOR_LPR_NODELIST ( prod->nodes, cur_nodelist )
		if ( cur_nodelist->node->applied_production != NULL )
			optimize_nodes3_height( system, cur_nodelist->node, lpr_grid );
	END_FOR_LPR_NODELIST ( prod->nodes, cur_nodelist );

	FOR_LPR_NODELIST ( prod->nodes, cur_nodelist )
		if ( cur_nodelist->node->applied_production == NULL )
		{
			min = cur_nodelist->node->up->py;

			FOR_LPR_EDGELIST( cur_nodelist->node->source_edges, cur_edgelist )
				if ( cur_edgelist->edge->EH != NULL )
				{ 
					FOR_LPR_EDGELIST( cur_edgelist->edge->EH->edges, cur_eh_edgelist )
						plrs_node = cur_eh_edgelist->edge->S_list->node;
						if (( plrs_node->is_x == 0 ) && ( plrs_node->py < min ))
							min = plrs_node->py;
					END_FOR_LPR_EDGELIST( cur_edgelist->edge->EH->edges, cur_eh_edgelist );
				}
			END_FOR_LPR_EDGELIST( cur_nodelist->node->source_edges, cur_edgelist );

			FOR_LPR_EDGELIST( cur_nodelist->node->target_edges, cur_edgelist )
				if ( cur_edgelist->edge->EH != NULL )
				{ 
					FOR_LPR_EDGELIST( cur_edgelist->edge->EH->edges, cur_eh_edgelist )
						plrs_node = cur_eh_edgelist->edge->S_list->pre->node;
						if (( plrs_node->is_x == 0 ) && ( plrs_node->py < min ))
							min = plrs_node->py;
					END_FOR_LPR_EDGELIST( cur_edgelist->edge->EH->edges, cur_eh_edgelist );
				}
			END_FOR_LPR_EDGELIST( cur_nodelist->node->target_edges, cur_edgelist );

			if ( min < cur_nodelist->node->up->py )
				cur_nodelist->node->down->py = min - lpr_grid;
					



			max = cur_nodelist->node->down->py;

			FOR_LPR_EDGELIST( cur_nodelist->node->source_edges, cur_edgelist )
				if ( cur_edgelist->edge->EH != NULL )
				{ 
					FOR_LPR_EDGELIST( cur_edgelist->edge->EH->edges, cur_eh_edgelist )
						plrs_node = cur_eh_edgelist->edge->S_list->node;
						if (( plrs_node->is_x == 0 ) && ( plrs_node->py > max ))
							max = plrs_node->py;
					END_FOR_LPR_EDGELIST( cur_edgelist->edge->EH->edges, cur_eh_edgelist );
				}
			END_FOR_LPR_EDGELIST( cur_nodelist->node->source_edges, cur_edgelist );

			FOR_LPR_EDGELIST( cur_nodelist->node->target_edges, cur_edgelist )
				if ( cur_edgelist->edge->EH != NULL )
				{ 
					FOR_LPR_EDGELIST( cur_edgelist->edge->EH->edges, cur_eh_edgelist )
						plrs_node = cur_eh_edgelist->edge->S_list->pre->node;
						if (( plrs_node->is_x == 0 ) && ( plrs_node->py > max ))
							max = plrs_node->py;
					END_FOR_LPR_EDGELIST( cur_edgelist->edge->EH->edges, cur_eh_edgelist );
				}
			END_FOR_LPR_EDGELIST( cur_nodelist->node->target_edges, cur_edgelist );

			if ( max > cur_nodelist->node->down->py )
				cur_nodelist->node->up->py = max + lpr_grid;	
			else
				cur_nodelist->node->up->py =  cur_nodelist->node->down->py + 2*lpr_grid;
		}
	END_FOR_LPR_NODELIST ( prod->nodes, cur_nodelist );
}
			
	
