/************************************************************************/
/*												*/
/*				FILE: lpr_compute_pos_ass.c				*/
/*												*/
/*	Hier befinden sich die Funktionen, die zum Berechnen des Posi-	*/
/*	tion-Assignments notwendig sind, also compute_position_assign-	*/
/*	ment und lpr_topological_sorting.						*/
/*												*/
/************************************************************************/


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

/*************************************************************************
Function	: lpr_topological_sorting
Input		: lrs_Node node, plrs_Nodelist list
Output	: plrs_Nodelist
Description	: Enspricht der Funktion topological_sorting aus der Theorie.
*************************************************************************/
plrs_Nodelist lpr_topological_sorting(plrs_Node node, plrs_Nodelist list)
{
	plrs_Edge		cur_plrs_edge;
	plrs_Nodelist	new_nodelist;

	node->visited = 1;										/* Markiere diesen Knoten als besucht.		*/
	
	FOR_PLRS_EDGE_SOURCE( node, cur_plrs_edge )						/* Falls die Ziele der auslaufenden Kanten	*/
		if ( cur_plrs_edge->target->visited == 0 )					/* noch nicht besucht wurden, suche weiter.	*/
			list = lpr_topological_sorting( cur_plrs_edge->target, list );
	END_FOR_PLRS_EDGE_SOURCE( node, cur_plrs_edge );

	new_nodelist = create_plrs_nodelist_with_node ( node );				/* F"uge den Knoten dann in die zu erzeugende	*/
	list = add_plrs_nodelist_to_plrs_nodelist( new_nodelist, list );			/* Liste ein.						*/

	return list;										
}  


/*************************************************************************
Function	: compute_position_assignment
Input		: plr_System system
Output	: void
Description	: Enspricht der Funktion compute_position_assignment aus der
		  Theorie.
*************************************************************************/
void compute_position_assignment(plr_System system)
{
	plrs_Nodelist 	list, cur_nodelist;
	plrs_Node	  	cur_plrs_node;
	plrs_Edge		cur_plrs_edge;
	int			max;


	list = NULL;
	FOR_PLRS_NODES( system->x_graph, cur_plrs_node )				/* Setze alle visited-Marken zur"uck.			*/
		cur_plrs_node->visited = 0;
	END_FOR_PLRS_NODES( system->x_graph, cur_plrs_node )
	list = lpr_topological_sorting( system->x_graph, list );				/* Erstelle die Liste f"ur den x-Graph.	*/
	list->node->px = 0;										/* und setze den ersten Knoten auf 0.	*/
	FOR_PLRS_NODELIST ( list->suc, cur_nodelist )						/* Davon ausgehenden werden dann alle 	*/
		max = 0;											/* weiteren Knoten gesetzt.			*/
		FOR_PLRS_EDGE_TARGET( cur_nodelist->node, cur_plrs_edge )			/* Dazu wird die max. L"ange der Kanten	*/
			if ( cur_plrs_edge->length + cur_plrs_edge->source->px > max )	/* zu dem akt. Knoten + die Pos. des Vor-	*/
				max = cur_plrs_edge->length + cur_plrs_edge->source->px;	/* g"angers benutzt.				*/
		END_FOR_PLRS_EDGE_TARGET( cur_nodelist->node, cur_plrs_edge );
		cur_nodelist->node->px = max;
	END_FOR_PLRS_NODELIST ( list->suc, cur_nodelist );
	free_plrs_nodelist( list );									/* L"osche tempor"are Liste.			*/

	list = NULL;		
	FOR_PLRS_NODES( system->y_graph, cur_plrs_node )
		cur_plrs_node->visited = 0;
	END_FOR_PLRS_NODES( system->y_graph, cur_plrs_node )

									/* Das gleiche f"ur die y-Koordinate.	*/
	list = lpr_topological_sorting( system->y_graph, list );;
	list->node->py = 0;
	FOR_PLRS_NODELIST ( list->suc, cur_nodelist )
		max = 0;
		FOR_PLRS_EDGE_TARGET( cur_nodelist->node, cur_plrs_edge )
			if ( cur_plrs_edge->length + cur_plrs_edge->source->py > max )
				max = cur_plrs_edge->length + cur_plrs_edge->source->py;
		END_FOR_PLRS_EDGE_TARGET( cur_nodelist->node, cur_plrs_edge );
		cur_nodelist->node->py = max;
	END_FOR_PLRS_NODELIST ( list->suc, cur_nodelist );
	free_plrs_nodelist( list );
}

 
