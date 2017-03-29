/* (C) Universitaet Passau 1986-1994 */
/*1994-91 von Stefan Jockenhoevel und Gerd Nienhaus.*/

#include "paths.h"
#include STDH
#include SGRAPHH
#include SLISTH
#include GRAPHEDH
#include "PossibleConvexDraw.h"
#include "connectivity_tests_old/zusammen.h"
#include "ConvexTest.h"
#include "DrawConvex.h"

#include "values.h"
#include "math.h"
  
/********************************************************************************/                                                  


void DrawGraphConvexStructur(Sgraph_proc_info info)
{
     Sgraph  TheGraph;

     TheGraph = info->sgraph;

     if (TheGraph == empty_sgraph || TheGraph->nodes == empty_node) {
     	/* skip */
     } else if (TheGraph->directed) {
	if (!IsStrongConnected(TheGraph)) {
	    error ("Graph is not strong connected.\n");
	    return;
	}
     } else {
	if (!IsConnected(TheGraph)) {
	    error ("Graph is not connected.\n");
	    return;
	} else if (!IsBiConnected(TheGraph)) {
	    error ("Graph is not biconnected.\n");
	    return;
	}
     }

     if(DrawConvexPossible(TheGraph))
       {
       
        info->recompute = TRUE;

        ExtendFacialCycle(TheGraph,ConvexityTest(TheGraph),FALSE);
        
       } 

    info->recenter = TRUE;
  }


/********************************************************************************/


void DrawGraphConvexEditable(Sgraph_proc_info info)
{
  Sgraph  TheGraph;

  TheGraph = info->sgraph;

  if (TheGraph == empty_sgraph || TheGraph->nodes == empty_node) {
    /* skip */
  } else if (TheGraph->directed) {
    if (!IsStrongConnected(TheGraph)) {
      error ("Graph is not strong connected.\n");
      return;
    }
  } else {
    if (!IsConnected(TheGraph)) {
      error ("Graph is not connected.\n");
      return;
    } else if (!IsBiConnected(TheGraph)) {
      error ("Graph is not biconnected.\n");
      return;
    }
  }

  if(DrawConvexPossible(TheGraph)) {
        
    info->recompute = TRUE;
       
    ExtendFacialCycle(TheGraph,ConvexityTest(TheGraph),TRUE);
       
    {
      /* Hack inserted by MH to adjust radius so that minimal edge length = grid */
      Snode node;
      Sedge edge;
      int   dist_square;
      int   min_length_square = MAXINT-1;
      int   sum_x = 0, sum_y = 0;
      int   center_x = 0, center_y = 0;
      int   number_of_nodes = 0;
      double enlarge_by;
	 
      for_all_nodes (TheGraph, node) {
	   
	for_sourcelist (node,edge) if (edge->snode != edge->tnode) {
	  dist_square = (edge->snode->x - edge->tnode->x) * (edge->snode->x - edge->tnode->x) +
	    (edge->snode->y - edge->tnode->y) * (edge->snode->y - edge->tnode->y);
	  if ((dist_square > 0) && (dist_square < min_length_square)) {
	    min_length_square = dist_square;
	  }
	} end_for_sourcelist (node,edge);
	   
	sum_x += node->x;
	sum_y += node->y;
	number_of_nodes ++;
	   
      } end_for_all_nodes (TheGraph, node);
	 
      enlarge_by = (double)convex_draw_settings.grid / sqrt((double)min_length_square);
      center_x = sum_x / number_of_nodes;
      center_y = sum_y / number_of_nodes;
	 
      for_all_nodes (TheGraph, node) {
	node->x = (node->x - center_x) * enlarge_by + center_x;
	node->y = (node->y - center_y) * enlarge_by + center_y;
      } end_for_all_nodes (TheGraph, node);

    }
  }
     
  info->recenter = TRUE;
     
}

void	call_convex_layout_structure (Sgraph_proc_info info)
{
	DrawGraphConvexStructur(info);
}



void	call_convex_layout (Sgraph_proc_info info)
{
	DrawGraphConvexEditable(info);
}

