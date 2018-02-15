/************************************************************************************************/
/*												*/
/*					FILE: lpr_top_down_cost_opt.c				*/
/*												*/
/*		Hier befindet sich die Hauptfunktion, zum Algorithmus top-down-cost-		*/
/*		optimization.									*/
/*												*/
/************************************************************************************************/

#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include "draw.h"
#include <sys/syscall.h>
#include <ctype.h>

#include <X11/Xos.h>

#include <xview/xview.h>
#include <xview/panel.h>
#include <xview/icon.h>
#include <xview/notify.h>

#include "lp_subframe.h"

#include "lpr_nnode.h"
#include "lpr_ggraph.h"
#include "lpr_hhierarchie.h"
#include "lpr_optimal_layout.h"
#include "lpr_lrp.h"
#include "lpr_apply_production.h"
#include "lpr_glr_system.h"
#include "lpr_compute_pos_ass.h"
#include "lpr_compute_layout.h"
#include "lpr_optimize_nodes.h"

/*************************************************************************************************
Function	: top_down_cost_optimization
Input		:
Output	: void
Description : Entspricht der gleichnamigen Funktion aus der Theorie.
*************************************************************************************************/
void top_down_cost_optimization(lpr_Node root, lpr_Node head)
{
	int lpr_grid;
	plr_System	glrs;



	delete_all_hierarchies(root);							/* L"osche alle Hierarchiemengen		 		*/
	compute_hierarchy_sets_and_numbers_of_RHS_edges(root);  			/* Berechne die Hierarchiemengen von RHS-Edges neu           	*/
	compute_hierarchy_sets_and_numbers_of_embeddings(root); 			/* Berechne die Hierarchiemengen von Einbettungsregeln neu   	*/
 	compute_bridge_hierarchy_set_numbers(root, NULL);       			/* Berechne die Br"uckenhierarchiemengen neu                 	*/

	delete_all_costfunctions(root);			    				/* Freigabe des Speichers vor Neuberechnung der Kosten       	*/
	compute_cost_functions_c_and_c0(root);		    				/* Berechne die Kostenfunktionen                             	*/
	compute_optimal_production_layout_sets(root);           			/* Berechne set*, set**, c*, C* und C**                      	*/
	compute_optimal_production_layouts(root);		    			/* Berechne die optimalen Layouts f"ur die jeweilige Prod.   	*/

	srand((unsigned int)time(NULL));						/* Initialisiere Zufallsgenerator f"ur sp"atere freie Auswahl	*/
	compute_layout_restriction_par( root , NULL);           			/* Berechne die Parameter des LRS		 		*/
	if (( lpr_grid = get_gridwidth(wac_buffer)) == 0 )				/* Falls kein Grid eingestellt ist, setze standardm"assig auf	*/
		lpr_grid = 16;								/* 32.							 	*/
	glrs = compute_lr_system( root , lpr_grid);

								 			/* Das glr-System f"ur den Resultatsgraphen wird berechnet	*/
	compute_position_assignment ( glrs );						/* und daf"ur die Positionszuweisung vorgenommen.		*/

	if( LP_WIN.min_nodes )
	{
		optimize_nodes( glrs, root );						/* Optimiere die Knotenausmasse.				*/
		compute_position_assignment ( glrs );     
		optimize_nodes2( glrs, root );
		compute_position_assignment ( glrs ); 

		optimize_nodes3_width( glrs, root, lpr_grid );
		optimize_nodes3_height( glrs, root, lpr_grid ); 
	}

	compute_layout( root );								/* Zum Schluss wird das Layout berechnet.			*/

}
