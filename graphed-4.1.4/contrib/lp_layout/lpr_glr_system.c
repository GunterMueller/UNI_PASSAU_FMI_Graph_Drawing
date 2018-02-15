/******************************************************************************/
/*											      	*/
/*				     FILE: lpr_glr_system.c				   	*/
/*											      	*/
/*		Hier wird das Layout-Restriktionen-Systems des End-Graphen      	*/
/*          berechnet. Die Hauptfunktion steht am Ende des Files.			*/
/*											      	*/
/******************************************************************************/


#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <string.h>
#include "user_header.h"

#include <ctype.h>

#include "lp_general_functions.h"
#include "lpr_nnode.h"
#include "lpr_eedge.h"
#include "lpr_ggraph.h"
#include "lpr_lrp.h"
#include "lpr_plr_system.h"
#include "lpr_seq_plrs.h"





/***************************************************************************
Function	: add_track_segments
Input		: lpr_Node node
Output	: void
Description	: Entspricht der gleichnamigen Funktion aus der Theorie.
***************************************************************************/
void add_track_segments(lpr_Node node, plr_System system)
{
	int 		side, index;
	lpr_Graph 	prod = node->applied_production;
	plrs_Node	track_segment;
	
	for( side = 0; side < 4; side++ )													/* Durchlaufe alle Seiten.			*/
	{
		if ( prod->tn[side] > 1 )
		{
			prod->track_segments[side] = (plrs_Node *) mycalloc( prod->tn[side] - 1, sizeof( plrs_Node ));	/* Erzeuge Track-Segments entspr. der An-	*/
			for ( index = 0; index < prod->tn[side] - 1; index++ )							/* zahl der Tracks einer Seite.		*/
			{																/* Durchlaufe alle Tracks einer Seite und	*/
				track_segment = create_plrs_node();										/* erzeuge jeweils einen dazwischenlieg. 	*/
				strcpy(track_segment->info, "Tr:");
				strcat(track_segment->info, timos_itoa(side));
				strcat(track_segment->info, ":");
				strcat(track_segment->info, timos_itoa(index));

				track_segment->side = side;											/* Knoten. Speichere diesen im lpr_Graph	*/
				prod->track_segments[side][index] = track_segment;							/* und falls side rechts oder links im X-	*/
				if ((side % 2) == 0)												/* Graphen, sonst im Y-Graphen des "uber-	*/
				{															/* gebenen Systems.					*/
					system->x_graph = add_plrs_node( system->x_graph, track_segment );
					track_segment->is_x = 1;
				}
				else
				{
					system->y_graph = add_plrs_node( system->y_graph, track_segment );
					track_segment->is_x = 0;
				}
			}
		}
	}
}


/***************************************************************************
Function	: insert_channel_segment
Input		: lpr_Node node, plr_System system, plrs_Node plrs_node,
		  int track, side, lpr_grid
Output	: void
Description	: Entspricht der gleichnamigen Funktion aus der Theorie.
***************************************************************************/
void insert_channel_segment(lpr_Node node, plr_System system, plrs_Node plrs_node, int track, int side, int lpr_grid)
{
	lpr_Graph 	prod = node->applied_production;
	plrs_Edge	plrs_edge;

	if ( prod->tn[side] > 1 )															/* Falls die Track-Nr. dieser Seite	*/
	{																			/* gr"osser als 1 stelle Kante zwi-	*/
		if ( side == L_side )															/* den Track-Sep.-Segments und dem	*/
		{																		/* Track-Segment bzw. zwischen left */
			if ( track == 1 ) 														/* und bleft und dem Tracksegment	*/
			{																	/* her.					*/
				plrs_edge = create_plrs_edge( prod->track_segments[side][0], 0,  plrs_node );				/* Hier also zwischen dem 0-ten 	*/
				add_plrs_edge( plrs_edge );												/* Track-Sep.-Segment und dem Track	*/
				plrs_edge = create_plrs_edge( plrs_node, lpr_grid, node->bleft );						/* und zwischen dem Track und bleft.*/
				add_plrs_edge( plrs_edge );
			}
			else if ( track == prod->tn[side] )												/* Hier zwischen left und dem Track-*/
			{																	/* segment und zwischen dem Track-	*/
				plrs_edge = create_plrs_edge( node->left, 0,  plrs_node );							/* segment und dem n-ten Track-Sep.-*/
				add_plrs_edge( plrs_edge );												/* Segment.					*/
				plrs_edge = create_plrs_edge( plrs_node, lpr_grid, prod->track_segments[side][track - 2] );
				add_plrs_edge( plrs_edge );
			}
			else
			{
				plrs_edge = create_plrs_edge( prod->track_segments[side][track - 1], 0,  plrs_node );		/* Hier zwischen dem davorliegenden	*/
				add_plrs_edge( plrs_edge );												/* Track-Sep.-Segment und dem Track-*/
				plrs_edge = create_plrs_edge( plrs_node, lpr_grid, prod->track_segments[side][track - 2] );	/* Segment und dem danachliegenden	*/
				add_plrs_edge( plrs_edge );												/* und dem Track-Segment.		*/
			}
		}
		else if ( side == R_side )														/* F"ur die anderen Seiten analog in*/	
		{																		/* Abh"angigkeit von der Seite.	*/
			if ( track == 1 ) 
			{
				plrs_edge = create_plrs_edge( plrs_node, 0, prod->track_segments[side][0] );
				add_plrs_edge( plrs_edge );
				plrs_edge = create_plrs_edge( node->bright, lpr_grid, plrs_node);
				add_plrs_edge( plrs_edge );
			}
			else if ( track == prod->tn[side] )
			{
				plrs_edge = create_plrs_edge( plrs_node, 0,  node->right );
				add_plrs_edge( plrs_edge );
				plrs_edge = create_plrs_edge( prod->track_segments[side][track - 2],  lpr_grid, plrs_node );
				add_plrs_edge( plrs_edge );
			}
			else
			{
				plrs_edge = create_plrs_edge( plrs_node, 0, prod->track_segments[side][track - 1]);
				add_plrs_edge( plrs_edge );
				plrs_edge = create_plrs_edge(prod->track_segments[side][track - 2], lpr_grid, plrs_node );
				add_plrs_edge( plrs_edge );
			}
		}
		else if ( side == D_side ) 
		{
			if ( track == 1 )
			{
				plrs_edge = create_plrs_edge( prod->track_segments[side][0], 0,  plrs_node );
				add_plrs_edge( plrs_edge );
				plrs_edge = create_plrs_edge( plrs_node, lpr_grid, node->bdown );
				add_plrs_edge( plrs_edge );
			}
			else if ( track == prod->tn[side] )
			{
				plrs_edge = create_plrs_edge( node->down, 0,  plrs_node );
				add_plrs_edge( plrs_edge );
				plrs_edge = create_plrs_edge( plrs_node, lpr_grid, prod->track_segments[side][track - 2] );
				add_plrs_edge( plrs_edge );
			}
			else
			{
				plrs_edge = create_plrs_edge( prod->track_segments[side][track - 1], 0,  plrs_node );
				add_plrs_edge( plrs_edge );
				plrs_edge = create_plrs_edge( plrs_node, lpr_grid, prod->track_segments[side][track - 2] );
				add_plrs_edge( plrs_edge );
			}
		}
		else if ( side == U_side ) 
		{
			if ( track == 1 )
			{
				plrs_edge = create_plrs_edge( plrs_node, 0, prod->track_segments[side][0] );
				add_plrs_edge( plrs_edge );
				plrs_edge = create_plrs_edge( node->bup, lpr_grid, plrs_node);
				add_plrs_edge( plrs_edge );
			}
			else if ( track == prod->tn[side] )
			{
				plrs_edge = create_plrs_edge( plrs_node, 0,  node->up );
				add_plrs_edge( plrs_edge );
				plrs_edge = create_plrs_edge( prod->track_segments[side][track - 2],  lpr_grid, plrs_node );
				add_plrs_edge( plrs_edge );
			}
			else
			{
				plrs_edge = create_plrs_edge( plrs_node, 0, prod->track_segments[side][track - 1]);
				add_plrs_edge( plrs_edge );
				plrs_edge = create_plrs_edge(prod->track_segments[side][track - 2], lpr_grid, plrs_node );
				add_plrs_edge( plrs_edge );
			}
		}
	}
	else														/* Falls keine Track-Sep.-Segments	*/
	{														/* existieren jeweils eine Kante	*/
		if ( side == L_side )										/* zwischen left und Track und zwi-	*/
		{													/* Track und bleft.			*/
			plrs_edge = create_plrs_edge( node->left, 0, plrs_node );
			add_plrs_edge( plrs_edge );
			plrs_edge = create_plrs_edge( plrs_node, lpr_grid, node->bleft );
			add_plrs_edge( plrs_edge );
		}
		if ( side == R_side )										/* Wie zuvor in Abh"angigkeit von	*/
		{													/* Seite analog f"ur alle Seiten	*/
			plrs_edge = create_plrs_edge( node->bright, lpr_grid, plrs_node );
			add_plrs_edge( plrs_edge );
			plrs_edge = create_plrs_edge( plrs_node, 0, node->right );
			add_plrs_edge( plrs_edge );
		}
		if ( side == U_side )
		{
			plrs_edge = create_plrs_edge( node->bup, lpr_grid, plrs_node );
			add_plrs_edge( plrs_edge );
			plrs_edge = create_plrs_edge( plrs_node, 0, node->up );
			add_plrs_edge( plrs_edge );
		}
		if ( side == D_side )
		{
			plrs_edge = create_plrs_edge( node->down, 0, plrs_node );
			add_plrs_edge( plrs_edge );
			plrs_edge = create_plrs_edge( plrs_node, lpr_grid, node->bdown );
			add_plrs_edge( plrs_edge );
		}
	}
			
}			
			

		
/***************************************************************************
Function	: add_dependency_sequences
Input		: lpr_Node 	node, plr_System 	system, int	lpr_grid
Output	: void
Description	: Entspricht der gleichnamigen Funktion aus der Theorie.
***************************************************************************/
void add_dependency_sequences(lpr_Node node, plr_System system, int lpr_grid)
{
	lpr_Graph 		prod = node->applied_production;
	lpr_Edgelist 	cur_edgelist, in_cons, out_cons, cons, pes_list, cur_pes;
	lpr_Nodelist	cur_nodelist;
	lpr_Track_ass_des	track_ass;
	plrs_Node		plrs_node, plrs_node1, plrs_node2;
	plrs_Edge		plrs_edge, cur_plrs_edge;
	int			side, track, segments, is_in_con;
	lpr_Track_sharing ts;
	Dependency_list	new_dep_entry;

	in_cons 	= copy_lpr_edgelist( prod->IN_embeddings );							/* Verkette die ein- und auslaufenden Ein-*/				
	out_cons	= copy_lpr_edgelist( prod->OUT_embeddings );							/* bettungsregeln tempor"ar zum einmaligen*/
	cons		= add_edgelist_to_lpr_edgelist( in_cons, out_cons );						/* Durchlauf.					*/
	FOR_LPR_EDGELIST(cons, cur_edgelist)
		if ( cur_edgelist->edge->edge_type == lpr_IN_CONN_REL )	is_in_con = 1;			/* Merke den Typ der Einbettungsregel.	*/
		else	is_in_con = 0;
		if ( is_in_con )													/* Hole die richtige Sequentialisierungs-	*/
			pes_list = cur_edgelist->edge->pes_array[0];							/* liste.						*/
		else
		{
			segments =  get_optimal_edge_of_lpr_edge( prod, cur_edgelist->edge )->bends + 1;
			pes_list = cur_edgelist->edge->pes_array[segments - 1];
		}
		FOR_LPR_EDGELIST( pes_list, cur_pes )									/* und durchlaufe diese.			*/
			track_ass = get_track_ass_des( cur_pes->edge, prod );						/* Welches Track-Assignment haben wir ?	*/
			if ( track_ass->ta_type == 0 )									/* L"auft gerade durch, dann vererbe alle	*/
			{														/* ein- und auslaufenden Kanten.		*/
				if ( is_in_con )
				{
					plrs_node = cur_pes->edge->S_list->pre->node;
					FOR_PLRS_EDGE_TARGET(cur_pes->edge->S2_list->node, cur_plrs_edge)  
						plrs_edge = create_plrs_edge( cur_plrs_edge->source, cur_plrs_edge->length, plrs_node );
						add_plrs_edge( plrs_edge );
					END_FOR_PLRS_EDGE_TARGET(cur_pes->edge->S2_list->node, cur_plrs_edge);
					FOR_PLRS_EDGE_SOURCE(cur_pes->edge->S2_list->node, cur_plrs_edge)  
						plrs_edge = create_plrs_edge(  plrs_node, cur_plrs_edge->length, cur_plrs_edge->target );
						add_plrs_edge( plrs_edge );
					END_FOR_PLRS_EDGE_SOURCE(cur_pes->edge->S2_list->node, cur_plrs_edge);

					plrs_node = cur_pes->edge->S2_list->node;
					remove_plrs_node_from_graph ( plrs_node, system );				/* und setze die S-Liste neu zuammen.	*/
					cur_pes->edge->S2_list = free_first_of_dep_list( cur_pes->edge->S2_list );
					cur_pes->edge->end_value = cur_edgelist->edge->end_value;

					cur_pes->edge->S_list = add_dep_to_dep_list( cur_pes->edge->S_list, cur_pes->edge->S2_list );
				}
				else
				{
					plrs_node = cur_pes->edge->S_list->node;

					FOR_PLRS_EDGE_TARGET(cur_pes->edge->S2_list->pre->node, cur_plrs_edge)  
						plrs_edge = create_plrs_edge( cur_plrs_edge->source, cur_plrs_edge->length, plrs_node );
						add_plrs_edge( plrs_edge );
					END_FOR_PLRS_EDGE_TARGET(cur_pes->edge->S2_list->pre->node, cur_plrs_edge);
					FOR_PLRS_EDGE_SOURCE(cur_pes->edge->S2_list->pre->node, cur_plrs_edge)  
						plrs_edge = create_plrs_edge(  plrs_node, cur_plrs_edge->length, cur_plrs_edge->target );
						add_plrs_edge( plrs_edge );
					END_FOR_PLRS_EDGE_SOURCE(cur_pes->edge->S2_list->pre->node, cur_plrs_edge);

					plrs_node = cur_pes->edge->S2_list->pre->node;
					remove_plrs_node_from_graph ( plrs_node, system );
					cur_pes->edge->S2_list = free_last_of_dep_list( cur_pes->edge->S2_list );

					cur_pes->edge->start_value = cur_edgelist->edge->start_value;
					cur_pes->edge->S_list = add_dep_to_dep_list( cur_pes->edge->S2_list, cur_pes->edge->S_list );
				}
			}
			else														/* Bei einem aufwendigeren Track-Assignment	*/
			{														/* f"uge auf der richtigen Seite im richtigen	*/
				if ( is_in_con )											/* Track die entsprechenden Kanten ein.		*/
				{
					if (( track_ass->ta_type == 1 ) || ( track_ass->ta_type == 3 )) side = (track_ass->td1 + 3) % 4;
					if (( track_ass->ta_type == 2 ) || ( track_ass->ta_type == 4 )) side = (track_ass->td1 + 1) % 4;
					insert_channel_segment( node, system, cur_pes->edge->S_list->pre->node, track_ass->tn1, side, lpr_grid );
				}
				else
				{
					if ( track_ass->ta_type == 1 ) side = (track_ass->td1 + 3) % 4;
					if ( track_ass->ta_type == 2 ) side = (track_ass->td1 + 1) % 4;
					if ( track_ass->ta_type == 3 ) side = (track_ass->td2 + 3) % 4;
					if ( track_ass->ta_type == 4 ) side = (track_ass->td2 + 1) % 4;
					if ( track_ass->ta_type <= 2 )
						insert_channel_segment( node, system, cur_pes->edge->S_list->node, track_ass->tn1, side, lpr_grid );
					else
						insert_channel_segment( node, system, cur_pes->edge->S_list->node, track_ass->tn2, side, lpr_grid );
				}
				if (( track_ass->ta_type == 3 ) || ( track_ass->ta_type == 4 ))			/* Ben"otigt das Track-Assignment sogar 2 Tracks*/
				{													/* dann erzeuge ein Channel-Segment, und f"uge 	*/
					if ( is_in_con )										/* ebenfalls entsprechend ein.			*/
					{
						if ( track_ass->ta_type == 3 ) side = ( side + 1 ) % 4;
						else side = ( side + 3 ) % 4;
					}
					else
					{
						if ( track_ass->ta_type == 3 ) side = ( side + 3 ) % 4;
						else side = ( side + 1 ) % 4;
					}
					
					if (( side == L_side ) || ( side == R_side ))
					{
						plrs_node = create_plrs_node();
						strcpy(plrs_node->info, "Ch");
						plrs_node->is_x = 1;
						system->x_graph = add_plrs_node( system->x_graph, plrs_node );
					}
					else
					{
						plrs_node = create_plrs_node();
						strcpy(plrs_node->info, "Ch");
						plrs_node->is_x = 0;
						system->y_graph = add_plrs_node( system->y_graph, plrs_node );
					}
					if ( is_in_con )
						insert_channel_segment( node, system, plrs_node, track_ass->tn2, side, lpr_grid );
					else
						insert_channel_segment( node, system, plrs_node, track_ass->tn1, side, lpr_grid );

					
					new_dep_entry = create_dep_list_with_node( plrs_node ); 				/* Speichere es ausserdem in der S-liste.	*/
					if ( is_in_con )
						cur_pes->edge->S_list = add_dep_to_dep_list( cur_pes->edge->S_list, new_dep_entry );
					else
						cur_pes->edge->S_list = add_dep_to_dep_list( new_dep_entry, cur_pes->edge->S_list );
				}
					
				if ( is_in_con )												/* und setze diese neu zusammen.		*/
				{
					cur_pes->edge->S_list = add_dep_to_dep_list( cur_pes->edge->S_list, cur_pes->edge->S2_list);
					cur_pes->edge->end_value = cur_edgelist->edge->end_value;
				}
				else
				{
					cur_pes->edge->S_list = add_dep_to_dep_list( cur_pes->edge->S2_list, cur_pes->edge->S_list);
					cur_pes->edge->start_value = cur_edgelist->edge->start_value;
				}
			}
		END_FOR_LPR_EDGELIST( pes_list, cur_pes );
	END_FOR_LPR_EDGELIST(cons, cur_edgelist);
	free_lpr_edgelist( cons );

	for (side = 0; side < 4; side++)												/* Schliesslich m"ussen noch die Kanten	*/
		for ( track = 0; track < prod->tn[side]; track++ )								/* zwischen den parallel verlaufenden Seg-*/
		{																/* menten von Kanten, die gemeinsam einen	*/
			ts = get_track_sharing( prod, side, track + 1);								/* Track benutzen, hergestellt werden.	*/
			if ( ts->edge2 != NULL )
			{
				if ( ts->edge1->last->edge_type == lpr_IN_CONN_REL )						/* Bestimme diese Segmente und ziehe die 	*/
					plrs_node1 = ts->edge1->S2_list->node;							/* Kante.						*/
				else
					plrs_node1 = ts->edge1->S2_list->pre->node;
				if ( ts->edge2->last->edge_type == lpr_IN_CONN_REL )
					plrs_node2 = ts->edge2->S2_list->node;
				else
					plrs_node2 = ts->edge2->S2_list->pre->node;

				plrs_edge = create_plrs_edge( plrs_node1, lpr_grid, plrs_node2 );
				add_plrs_edge( plrs_edge );	
			}
		}
	
	FOR_LPR_NODELIST( prod->nodes, cur_nodelist )										/* Bei Kanten der rechten Seite hat sich	*/
		FOR_LPR_EDGELIST( cur_nodelist->node->source_edges, cur_edgelist )					/* nichts ge"andert. Diese erhalten als	*/
			if (cur_edgelist->edge->edge_type == lpr_RHS_EDGE )							/* S-Liste die S''-Liste.			*/
			{
				FOR_LPR_EDGELIST(cur_edgelist->edge->pes_array[0], cur_pes )
					cur_pes->edge->S_list = cur_pes->edge->S2_list;
					cur_pes->edge->start_value = cur_edgelist->edge->start_value;
					cur_pes->edge->end_value = cur_edgelist->edge->end_value;
				END_FOR_LPR_EDGELIST(cur_edgelist->edge->pes_array[0], cur_pes );
			}
		END_FOR_LPR_EDGELIST( cur_nodelist->node->source_edges, cur_edgelist );
	END_FOR_LPR_NODELIST( prod->nodes, cur_nodelist );
}


/***************************************************************************
Function	: compute_lr_system_on_prods
Input		: lpr_Node node, plrs_System father_system, int lpr_grid
Output	: void
Description : Entspricht der Funktion compute_layout_restriction_system' aus
		  der Theorie.
***************************************************************************/
void 	compute_lr_system_on_prods(lpr_Node node, plr_System father_system, int lpr_grid)
{
	lpr_Graph 		prod = node->applied_production;
	lpr_Nodelist 	cur_nodelist;
	plr_System		prod_system;
	plrs_Nodelist	VX = NULL, VY = NULL, new_entry, cur_plrs_nodelist;
	plrs_Node		cur_plrs_node, last1;
	plrs_Edge		plrs_edge;

	if (prod != NULL && !node->leaf)
	{
		prod_system = compute_plr_system( node, lpr_grid );					/* Erstelle das plr-System		*/				

		FOR_PLRS_NODES( prod_system->x_graph, cur_plrs_node )					/* Merke vor der Sequentialisierung	*/
			if ( cur_plrs_node->side == -1 )							/* alle Kantensegmente, die sp"ater	*/
			{												/* "uberfl"ussig sind.	Diese sind	*/
				new_entry = create_plrs_nodelist_with_node( cur_plrs_node );	/* erkennbar an Seite -1, denn alle	*/
				VX = add_plrs_nodelist_to_plrs_nodelist( VX, new_entry );		/* alle Knotensegmente haben eine 	*/
			}												/* definierte Seite.			*/
		END_FOR_PLRS_NODES( prod_system->x_graph, cur_plrs_node );
		FOR_PLRS_NODES( prod_system->y_graph, cur_plrs_node )
			if ( cur_plrs_node->side == -1 )
			{
				new_entry = create_plrs_nodelist_with_node( cur_plrs_node );
				VY = add_plrs_nodelist_to_plrs_nodelist( VY, new_entry );
			}
		END_FOR_PLRS_NODES( prod_system->y_graph, cur_plrs_node );


		sequentialize_plrs(prod, prod_system, lpr_grid );					/* Sequentialisiere alle Kanten	*/

		FOR_PLRS_NODELIST( VX, cur_plrs_nodelist )						/* L"osche die eben gemerkten aus 	*/
			remove_plrs_node_from_graph( cur_plrs_nodelist->node, prod_system );	/* den Graphen.				*/
		END_FOR_PLRS_NODELIST( VX, cur_plrs_nodelist );
		free_plrs_nodelist( VX );									/* und gib diese Listen frei.		*/

		FOR_PLRS_NODELIST( VY, cur_plrs_nodelist )
			remove_plrs_node_from_graph( cur_plrs_nodelist->node, prod_system );
		END_FOR_PLRS_NODELIST( VY, cur_plrs_nodelist );
		free_plrs_nodelist( VY );

		last1 = father_system->x_graph->pre;							/* Vereinige das bereits berechnete	*/
		father_system->x_graph->pre = prod_system->x_graph->pre;				/* System mit dem der angew. Prod.	*/
		prod_system->x_graph->pre->suc = father_system->x_graph;
		last1->suc = prod_system->x_graph;
		prod_system->x_graph->pre = last1;

		last1 = father_system->y_graph->pre;
		father_system->y_graph->pre = prod_system->y_graph->pre;
		prod_system->y_graph->pre->suc = father_system->y_graph;
		last1->suc = prod_system->y_graph;
		prod_system->y_graph->pre = last1;

		plrs_edge = create_plrs_edge( node->left, 0, node->bleft );				/* Ziehe Kanten um, die Verbingung	*/
		add_plrs_edge( plrs_edge );									/* herzustellen.				*/
		plrs_edge = create_plrs_edge( node->bright, 0, node->right );
		add_plrs_edge( plrs_edge );
		
		plrs_edge = create_plrs_edge( node->down, 0, node->bdown );
		add_plrs_edge( plrs_edge );
		plrs_edge = create_plrs_edge( node->bup, 0, node->up );
		add_plrs_edge( plrs_edge );

		add_track_segments( node, father_system );						/* F"uge jetzt notwendige Track-Sep.*/

						/* Segments ein.				*/
		add_dependency_sequences( node, father_system, lpr_grid );				/* und verdrahte dann die Systeme.	*/



		FOR_LPR_NODELIST( prod->nodes, cur_nodelist )						/* Top-down-Durchlauf.			*/
			compute_lr_system_on_prods( cur_nodelist->node, prod_system, lpr_grid );
		END_FOR_LPR_NODELIST( prod->nodes, cur_nodelist );
	}
}




/***************************************************************************
Function	: free_complete_lrs
Input		: lpr_Node node
Output	: void
Description : L"oscht den X- und Y-Graphen des letzten Resultatsgraphen und 
		  st"osst den rekursiven Durchlauf zum L"oschen der Zeiger auf
		  diese Graphen und die jeweiligen S- und S''-Listen an.
***************************************************************************/
void free_complete_lrs(lpr_Node node)
{
	lpr_Graph prod = node->applied_production;
	
	free_plrs_nodes_with_edges(prod->nodes->node->left);						/* L"osche die X- und Y-PLRS-Graphen	*/
	free_plrs_nodes_with_edges(prod->nodes->node->down);
	free_plr_system(node );											/* und setzte jetzt alle Zeiger zur"uck.	*/
}
	

/***************************************************************************
Function	: compute_lr_system
Input		: lpr_Node node, int lpr_grid
Output	: void
Description : Entspricht der Funktion compute_layout_restriction_system aus
		  der Theorie.
***************************************************************************/
plr_System compute_lr_system(lpr_Node node, int lpr_grid)
{
	plrs_Node	x_nodes = NULL, y_nodes = NULL, plrs_node1, plrs_node2;
	plrs_Edge	plrs_edge;
	plr_System	ini_glrs;

		
	free_complete_lrs( node );						/* L"osche zuerst alle ex. plrs-System im */
										/* Ableitungsbaum.				*/

	plrs_node1 = create_plrs_node();				/* Erzeuge die initialen PLRS-Graphen 	*/
	strcpy(plrs_node1->info, "I:0");
	plrs_node1->side = L_side;				
	plrs_node1->is_x	= 1;										
	x_nodes = plrs_node1;
	node->left = plrs_node1;

	plrs_node2 = create_plrs_node();
	strcpy(plrs_node2->info, "I:2");
	plrs_node2->side = R_side;
	plrs_node2->is_x	= 1;										
	node->right = plrs_node2;

	x_nodes = add_plrs_node( x_nodes, plrs_node2 );

	plrs_edge = create_plrs_edge( plrs_node1, 2*lpr_grid, plrs_node2 );
	add_plrs_edge( plrs_edge );


	plrs_node1 = create_plrs_node();
	strcpy(plrs_node1->info, "I:3");
	plrs_node1->side = D_side;
	plrs_node1->is_x = 0;						
	node->down = plrs_node1;
	y_nodes = plrs_node1;

	plrs_node2 = create_plrs_node();				
	strcpy(plrs_node2->info, "I:1");
	plrs_node2->side = U_side;					
	plrs_node2->is_x = 0;						
	node->up = plrs_node2;									

	y_nodes = add_plrs_node( y_nodes, plrs_node2 );

	plrs_edge = create_plrs_edge( plrs_node1, 2*lpr_grid, plrs_node2 );
	add_plrs_edge( plrs_edge );

	ini_glrs = create_plr_system();								/* Erzeuge das initiale glr-System		*/
	ini_glrs->x_graph = x_nodes;
	ini_glrs->y_graph = y_nodes;

	compute_lr_system_on_prods( node , ini_glrs, lpr_grid);				/* Hier geht's dann richtig los.		*/
	return ini_glrs;
}



	
