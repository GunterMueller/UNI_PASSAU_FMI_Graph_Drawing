/****************************************************************************\
 *                                                                          *
 *  nonplanarity_calls.c                                                    *
 *  --------------------                                                    *
 *                                                                          *
 *  author:  a.j. winter (11027)  06/93.                                    *
 *                                                                          *
\****************************************************************************/


#include <std.h>
#include <slist.h>
#include <sgraph.h>
#include <graphed.h>
#include <graphed_sgraph_interface.h>

#include <springembedder_rf/springembedder_rf_export.h>
#include <springembedder_kamada/springembedder_kamada_export.h>
#include <tree_layout_walker/tree_layout_walker_export.h>
#include <sugiyama/sugiyama_export.h>
#include <minimal_bends_layout/minimal_bends_layout_export.h>
#include <woods_planar_layout/woods_planar_layout_export.h>
#include <convex_layout/convex_layout_export.h>
#include <fpp_layout/fpp_layout_export.h>


#include "nonplanarity_export.h"
#include "nonplanarity_settings.h"


#include "sattrs.h"

#include "setattrsandlabels.h"

#include "stnumber.h"

#include "pqplanarity.h"

#include "maxplanarsubgraph.h"
#include "thickness.h"
#include "crossingnumber.h"

#include "sgraph/random.h"
#include "random.h"

#ifdef DEBUG
#include "sdebug.h"
#endif



Global	void	CallResizeGraphedNodes(Sgraph_proc_info info);



Global	int	SGRAPH_PROC_RETURN_CODE;
/* possible values for SGRAPH_PROC_RETURN_VALUE */
#define	OK	0
#define	CANCEL	-1

/* global variables, to be replaced by settings */

Global	MaxPlanarSettings	currMaxPlanarSettings;
Global	ThicknessSettings	currThicknessSettings;
Global	CrossingNumberSettings	currCrossingNumberSettings;





Global	char	buffer[255];
Global	FILE	*outfile;




/*****************************************************************************\
 *                                                                           *
 *  char *execute_user_menuI(char *menu, char *menu_item)                    *
 *  -----------------------------------------------------                    *
 *                                                                           *
 *  Autor:  a.j. winter (11027)  05/91.                                      *
 *                                                                           *
 *****************************************************************************
 *                                                                           *
 *  Eingabe: GraphEd-interne Datenstruktur                                   *
 *                                                                           *
 *  Ausgabe: ---                                                             *
 *                                                                           *
 *  Aufruf : in  init_user_menu (dieses Modul)                               *
 *           mit                                                             *
 *                                                                           *
 *  Aufgabe: uebergibt GraphEd den Namen der Prozedur, die sich hinter dem   *
 *           User-Menuepunkt I versteckt.                                    *
 *                                                                           *
\*****************************************************************************/

void MenuResetGraph (Menu menu, Menu_item menu_item)
{
  call_sgraph_proc (CallResetGraph, NULL);
}

void MenuBiConnComp (Menu menu, Menu_item menu_item)
{
  call_sgraph_proc (CallBiConnComp, NULL);
}

void MenuPQPlanarityTest (Menu menu, Menu menu_item)
{
  call_sgraph_proc (CallPQPlanarityTest, NULL);
}

void MenuMaxPlanarExecuteJayakumar (Menu menu, Menu_item menu_item)
{
/*  idea set here index for that algorithm that was chosen and then         *
 *  call a global function taking that index as parameter, this             *
 *  function will also be called, when the "GO"-button in the settings      *
 *  is hit, then also the index will be known for the algorithm to run      */

   call_sgraph_proc (CallMaxPlanarJayakumar, NULL);
   if (!currMaxPlanarSettings) {
       warning("\nno settings available !\n");
   } else {
      if ((currMaxPlanarSettings->create_new_window_for_mpg) &&
		(currMaxPlanarSettings->use_planar_embedding)) {
         call_sgraph_proc (DrawGraphConvexEditable /*Structur*/, NULL);
/*       call_sgraph_proc (CallResizeGraphedNodes, NULL);
         call_sgraph_proc (call_fpp_layout_asslia, NULL); */
      }
   } /* endif */
 }

void MenuMaxPlanarExecuteGreedy (Menu menu, Menu_item menu_item)
{
  call_sgraph_proc (CallMaxPlanarGreedy, NULL);
}

void MenuMaxPlanarExecuteRandomizedGreedy (Menu menu, Menu_item menu_item)
{
  call_sgraph_proc (CallMaxPlanarRandomizedGreedy, NULL);
}

void MenuMaxPlanarExecuteRandomizedGraphTest (Menu menu, Menu_item menu_item)
{
  call_sgraph_proc (CallMaxPlanarRandomizedGraphTest, NULL);
}


void MenuThicknessExecute (Menu menu, Menu_item menu_item)
{
  call_sgraph_proc (CallThickness, NULL);
}


void MenuCrossingNumberExecuteConvexDraw (Menu menu, Menu_item menu_item)
{
   call_sgraph_proc (CallCrossingNumber, NULL);

   if (SGRAPH_PROC_RETURN_CODE == OK) {
      call_sgraph_proc (DrawGraphConvexEditable, NULL);
      call_sgraph_proc (CallEmbedRemainingEdges, NULL);
   } /* endif */
}


void MenuCrossingNumberExecuteChrobakPayneAsslia (Menu menu, Menu_item menu_item)
{
   call_sgraph_proc (CallCrossingNumber, NULL);

   if (SGRAPH_PROC_RETURN_CODE == OK) {
      call_sgraph_proc (DrawGraphConvexEditable /*Structur*/, NULL);
      call_sgraph_proc (CallResizeGraphedNodes, NULL);
      call_sgraph_proc (call_fpp_layout_asslia, NULL);
      call_sgraph_proc (CallEmbedRemainingEdges, NULL);
   } /* endif */

}


void MenuCrossingNumberExecuteChrobakPayne (Menu menu, Menu_item menu_item)
{
   call_sgraph_proc (CallCrossingNumber, NULL);

   if (SGRAPH_PROC_RETURN_CODE == OK) {
      call_sgraph_proc (DrawGraphConvexEditable /*Structur*/, NULL);
      call_sgraph_proc (CallResizeGraphedNodes, NULL);
      call_sgraph_proc (call_fpp_layout, NULL);
      call_sgraph_proc (CallEmbedRemainingEdges, NULL);
   } /* endif */

}


void MenuCrossingNumberExecuteSpringKamada (Menu menu, Menu_item menu_item)
{
   call_sgraph_proc (CallCrossingNumberPrepare, NULL);

   if (SGRAPH_PROC_RETURN_CODE == OK) {
      call_sgraph_proc (call_springembedder_kamada, NULL);
      call_sgraph_proc (CallCountCrossingsInEmbedding, NULL);
   } /* endif */
}


void MenuCrossingNumberExecuteSpringRF (Menu menu, Menu_item menu_item)
{

   call_sgraph_proc (CallCrossingNumberPrepare, NULL);

   if (SGRAPH_PROC_RETURN_CODE == OK) {
      call_sgraph_proc (call_fast_springembedder_rf, NULL);
      call_sgraph_proc (CallCountCrossingsInEmbedding, NULL);
   } /* endif */
}


void MenuCrossingNumberExecuteNaiveEmbedding (Menu menu, Menu_item menu_item)
{

   call_sgraph_proc (CallCrossingNumberPrepare, NULL);

   if (SGRAPH_PROC_RETURN_CODE == OK) {
      call_sgraph_proc (CallCrossingNumberNaiveEmbedding, NULL);
      call_sgraph_proc (CallCountCrossingsInEmbedding, NULL);

   } /* endif */
}


void MenuCrossingNumberExecuteCompleteEmbedding (Menu menu, Menu_item menu_item)
{

   call_sgraph_proc (CallCrossingNumberPrepare, NULL);

   if (SGRAPH_PROC_RETURN_CODE == OK) {
      call_sgraph_proc (CallCrossingNumberCompleteEmbedding, NULL);
      call_sgraph_proc (CallCountCrossingsInEmbedding, NULL);

   } /* endif */
}


void MenuCrossingNumberExecuteBipartiteEmbedding (Menu menu, Menu_item menu_item)
{

   call_sgraph_proc (CallCrossingNumberBipartitePrepare, NULL);

   if (SGRAPH_PROC_RETURN_CODE == OK) {
      call_sgraph_proc (CallCrossingNumberBipartiteEmbedding, NULL);
      call_sgraph_proc (CallCountCrossingsInEmbedding, NULL);

   } /* endif */
}


void MenuCrossingNumberCountCrossings (Menu menu, Menu_item menu_item)
{

   call_sgraph_proc (CallCountCrossingsInEmbedding, NULL);
}





/****************************************************************************/
/*                                                                          */
/* Sgraph - Procedures,                                                     */
/* 	called by 'call_sgraph_proc(...)', chosen in 'user - menu'          */
/*                                                                          */
/****************************************************************************/


Global void CallResetGraph(Sgraph_proc_info info)
{

  if (info->sgraph) {
     if (!info->sgraph->directed) {
	if (info->sgraph->nodes) {
	   ClearLabels(info->sgraph); 
	   info->no_changes = FALSE;
/*         info->recompute  =  TRUE;  */
	   info->repaint    = TRUE;
/*	   clear_all_attributes(info->sgraph); */
	} else {
	   message("\ngraph is empty.\n");
	} /*endif */
     } else {
	message("\nThe graph is directed\n");
     } /* endif */
  } else {
     message("\nno graph found.\n");
  } /* endif */
}


Global void CallResizeGraphedNodes(Sgraph_proc_info info)
{
   Sgraph	g;
   Snode	n;

   
   g = info->sgraph;
/* dispatch_user_action(UNSELECT); */
   if (g != empty_sgraph) {
      for_all_nodes(g, n) {
         node_set(graphed_node(n), ONLY_SET, NODE_SIZE, 4, 4, 0); 
       } end_for_all_nodes(g, n);
   } /* endif */
}


Global void CallBiConnComp(Sgraph_proc_info info)
{
   Sgraph	graph;

   graph = info->sgraph;

  if (graph) {
     if (!graph->directed) {
	if (graph->nodes) {
#	   ifdef DEBUG 
	   outfile = fopen("graph.out", "w");
	   printGraph(outfile, graph);
	   fclose(outfile); 
#	   endif

/*	   ClearLabels(info->sgraph); */
	   create_and_init_all_attributes(graph);
/*	   PQ_Planarity_Test(info->sgraph); */
 	   Decompose_Graph_Into_Biconnected_Components(graph);

	   info->no_changes = FALSE;
/*         info->recompute  =  TRUE;  */
	   info->repaint    = TRUE;
	   clear_all_attributes(graph);
	} else {
	   message("\ngraph is empty.\n");
	} /*endif */
     } else {
	message("\nThe graph is directed\n");
     } /* endif */
  } else {
     message("\nno graph found.\n");
  } /* endif */
}


Global void CallPQPlanarityTest(Sgraph_proc_info info)
{
  Sgraph	graph;

  graph = info->sgraph;

  if (graph) {
     if (!graph->directed) {
	if (graph->nodes) {
	   make_new_node_labels(graph);
	   PQ_Planarity_Test(graph);
	   info->no_changes = TRUE;
	   info->recompute  = FALSE;
	   info->repaint    = FALSE;
	} else {
	   message("\ngraph is empty.\n");
	} /*endif */
     } else {
	message("\nThe graph is directed\n");
     } /* endif */
  } else {
     message("\nno graph found.\n");
  } /* endif */
}


Global void CallMaxPlanarJayakumar(Sgraph_proc_info info)
{
  Sgraph	graph;
  Sgraph	mpg;
  int		nodes, edges;

# ifdef DEBUG
  FILE		*out2file;
# endif

  graph = info->sgraph;

  if (graph) {
     if (!graph->directed) {
	if (graph->nodes) {
#	   ifdef DEBUG
	   out2file = fopen("graph.out", "w");
	   printGraph(out2file, graph);
	   fclose(out2file);
#	   endif

#	   ifdef DEBUG
	   outfile = fopen("maxplanar.out", "w");
#	   endif
/*         the following properties a assumed for a graph before it is *
 *         processed: *
 *          - no self-loops *
 *          - no multiple edges */

	    count_all_nodes_and_edges_in_graph(graph, &nodes, &edges);
   	    sprintf(buffer, "\n\ngraph has %2d nodes and %2d edges",
			nodes, edges); 
   	     message(buffer);


	    make_new_node_labels(graph);

	    mpg = maxplanarsubgraph(graph);

	    if (!mpg) {
		 message("\n\nmpg: graph is planar.  ");
		 if (!currMaxPlanarSettings) {
		    message("\nlost datastructure for settings !\n");
		 } else {
		    currMaxPlanarSettings->graph_is_already_planar = TRUE;
		 } /* endif */
	    } else {
		message("\n\nmpg: found nonplanar graph.  ");

		 if (!currMaxPlanarSettings) {
		    message("\nlost datastructure for settings !\n");
		 } else {
		    message("\ndeleted %d edges and re-inserted %d of them.",
			currMaxPlanarSettings->deleted_edge_count,
			currMaxPlanarSettings->re_inserted_edge_count);
		    if (currMaxPlanarSettings->create_new_window_for_mpg) {
		       dispatch_user_action(UNSELECT);
		       info->new_buffer = create_buffer();
		       info->new_sgraph = mpg;
		       info->new_selected = SGRAPH_SELECTED_NONE;

		       graph_set(
			  (Graph)create_graphed_graph_from_sgraph_in_buffer(
			     info->new_sgraph,info->new_buffer),
			  RESTORE_IT, 0);
		    } else {
		       make_edge_attributes_for_mpg(graph);
		       clear_all_labels(mpg);
		       remove_graph(mpg);
		    } /* endif */
		 } /* endif */
	    } /* endif */

	   info->no_changes = FALSE;
	   info->recompute  = FALSE;
	   info->repaint    = TRUE;
	} else {
	   message("\ngraph is empty.\n");
	} /*endif */
     } else {
	message("\nThe graph is directed\n");
     } /* endif */
  } else {
     message("\nno graph found.\n");
  } /* endif */
}


Global void CallMaxPlanarGreedy(Sgraph_proc_info info)
{
  Sgraph	graph;
  Sgraph	mpg;

# ifdef DEBUG
  FILE		*out2file;
# endif

  graph = info->sgraph;

  if (graph) {
     if (!graph->directed) {
	if (graph->nodes) {

#	   ifdef DEBUG
	   outfile = fopen("maxplanar.out", "w");
#	   endif
/*         the following properties a assumed for a graph before it is *
 *         processed: *
 *          - no self-loops *
 *          - no multiple edges */

	   make_new_node_labels(graph);

	      mpg = maxplanarsubgraph_greedy(graph);
	      if (!mpg) {
		 message("\n\nmpg: graph is planar.  ");
	      } else {
		 message("\n\nmpg: found nonplanar graph.  ");
		 if (!currMaxPlanarSettings) {
		    message("\nlost datastructure for settings !\n");
		 } else {
		    message("\ndeleted %d edges.",
			currMaxPlanarSettings->deleted_edge_count);
		    if (currMaxPlanarSettings->create_new_window_for_mpg) {
		       dispatch_user_action(UNSELECT);
		       info->new_buffer = create_buffer();
		       info->new_sgraph = mpg;
		       info->new_selected = SGRAPH_SELECTED_NONE;

		       graph_set(
			  (Graph)create_graphed_graph_from_sgraph_in_buffer(
			     info->new_sgraph,info->new_buffer),
			  RESTORE_IT, 0);
		    } else {
		       make_edge_attributes_for_mpg(graph);
		       clear_all_labels(mpg);
		       remove_graph(mpg);
		    } /* endif */
		 } /* endif */
	      } /* endif */


	   info->no_changes = FALSE;
           info->recompute  = FALSE;  
	   info->repaint    = TRUE;
	} else {
	   message("\ngraph is empty.\n");
	} /*endif */
     } else {
	message("\nThe graph is directed\n");
     } /* endif */
  } else {
     message("\nno graph found.\n");
  } /* endif */
}


Global void CallMaxPlanarRandomizedGreedy(Sgraph_proc_info info)
{
  Sgraph	graph;
  Sgraph	mpg;
  int		count, edgeCount, maxEdgeCount, it;

  graph = info->sgraph;

  if (graph) {
     if (!graph->directed) {
	if (graph->nodes) {

/*         the following properties a assumed for a graph before it is *
 *         processed: *
 *          - no self-loops *
 *          - no multiple edges */

	   make_new_node_labels(graph);

	   edgeCount = count_edges_in_graph(graph); 
	   message("\nfound %2d edges in graph", edgeCount); 

	   count = 0;
	   maxEdgeCount = 0;
	   it = 5;
           if (!currMaxPlanarSettings) {
	      warning("\nno settings availabale !\n");
	   } else {
	      it = currMaxPlanarSettings->iterations_for_randomized_greedy;
	   }

	   while (count < it) {
	      mpg = maxplanarsubgraph_randomized_greedy(graph);
	      if (!mpg) {
		 message("\n\nmpg: graph is planar.  ");
		 break;
	      } else {
		 edgeCount = count_edges_in_graph(mpg);
		 if (edgeCount > maxEdgeCount) {
		    message("\n\nmpg: found better maximal planar subgraph !");
		    make_edge_attributes_for_mpg(graph);
		    maxEdgeCount = edgeCount;
		    force_repainting();
		 } else {
		    message(".");
		 } /* endif */
		 remove_graph(mpg);
	      } /* endif */
	      count++;
	   } /* endwhile */


	   info->no_changes = FALSE;
	   info->recompute  = FALSE;
	   info->repaint    = TRUE;
	} else {
	   message("\ngraph is empty.\n");
	} /* endif */
     } else {
	message("\nThe graph is directed\n");
     } /* endif */
  } else {
     message("\nno graph found.\n");
  } /* endif */
}


Global void CallMaxPlanarRandomizedGraphTest(Sgraph_proc_info info)
{
  Sgraph	graph;
  Sgraph	mpg;
  int		count, nodeCount, edgeCount; 

# ifdef DEBUG
  FILE		*out2file;
# endif

	       init_random_number_generator();
	       outfile = fopen("maxplanar.out", "w");
	       count = 0;
	       while (count < ITERATIONS) {
		  graph = create_randomized_graph(((count%10)+1)*10);
		  sprintf(buffer,"created %d graph\n", count);
		  message(buffer);
		  if (outfile) {
		  fprintf(outfile, "\n\ncreated %2d graph", count);
		  }
		  count_all_nodes_and_edges_in_graph(graph,
					&nodeCount, &edgeCount);
		  if (outfile) {
		  fprintf(outfile,
			" with %2d nodes and %2d edges",
			  nodeCount, edgeCount);
		  fflush(outfile);
		  }

		  mpg = maxplanarsubgraph(graph);
		  if (mpg != empty_graph) {
		     fprintf(outfile, "\nmpg: found nonplanar graph.");
		     count_all_nodes_and_edges_in_graph(mpg,
				 &nodeCount, &edgeCount);
		     if (outfile) {
		     fprintf(outfile,
				"\nmpg: found mpg with %2d nodes and %2d edges",
			  nodeCount, edgeCount);

/*		     printGraph(outfile, mpg); */

		     fprintf(outfile, "\nmpg: deleted %2d edges and re-inserted %2d of them",
			  currMaxPlanarSettings->deleted_edge_count,
			  currMaxPlanarSettings->re_inserted_edge_count);
		     fflush(outfile);
		     }
		     clear_all_labels(mpg);
		     remove_graph(mpg);
		  } else {
		     if (outfile) {
		     fprintf(outfile, "\nmpg: found planar graph.");
		     fflush(outfile);
		     }
		  } /* endif */
		  count++;
		  clear_all_labels(graph);
		  remove_graph(graph);
	       } /* endwhile */
	       sprintf(buffer, "\niterations complete.\n");
	       message(buffer);

	       if (outfile) {
	       fclose(outfile);
	       }

}




Global void CallThickness(Sgraph_proc_info info)
{
  Sgraph	graph, g;
  Slist		layers, Sg;
  int		layerCount;
#ifdef DEBUG
  int 		c;
#endif

  graph = info->sgraph;

  if (graph) {
     if (!graph->directed) {
	if (graph->nodes) {
#	   ifdef DEBUG
	   outfile = fopen("graph.out", "w");
	   printGraph(outfile, graph);
	   fclose(outfile); 
#	   endif


/*	   create slist of graphs, one for each layer and *
 *	   mark corresponding edges in input graph according to layer */
/*	   at first copy inputgraph G to G'and leave it than alone until *
 *	   layers are determined. */
/*	   begin with G' and find the mpg of G', then delete the *
 *	   corresponding edges of mpg in G', while G' itself is nonplanar *
 *	   if G' is planar, it is the last layer for thickness */
/*	   if input_graph is planar then return empty_slist */

	   make_new_node_labels(graph);

	   layerCount = thickness(graph, &layers);

	   if (layerCount == 1) {
	      message("\n\nthickness: graph is planar.\n");

	      info->no_changes = TRUE;
              info->recompute  = FALSE;
	      info->repaint    = TRUE;
	   } else {  
	      message("\n\nthickness: found %2d layers.\n", layerCount);
#		 ifdef DEBUG
	      for_slist(layers, Sg) {
		 g = sgraph_in_slist(Sg);
   		 c = attr_flags(g);
		 message("\nfound layer number %2d", c); 
	      } end_for_slist(layers, Sg);
#		 endif
	      make_edge_labels_for_layers(graph); 
/*	      make_edge_attributes_for_layers(info->sgraph); */
	      for_slist(layers, Sg) {
		 g = sgraph_in_slist(Sg);
		 clear_all_labels(g);
		 remove_graph(g);
	      } end_for_slist(layers, Sg);

	      info->no_changes = FALSE;
              info->recompute  = FALSE;
	      info->repaint    = TRUE;
	   } /* endif */

	} else {
	   message("\ngraph is empty.\n");
	} /*endif */
     } else {
	message("\nThe graph is directed\n");
     } /* endif */
  } else {
     message("\nno graph found.\n");
  } /* endif */
}


Global void CallCrossingNumber(Sgraph_proc_info info)
{
  Sgraph	graph;
  Sgraph	mpg;
/*  int           crossCount; */
  Slist		edgeList;
/*  int		dummyres;*/

# ifdef DEBUG
  FILE		*out2file;
  Slist		Selem;
  int		edgeCount;
# endif

  graph = info->sgraph;
  SGRAPH_PROC_RETURN_CODE = CANCEL;

  if (graph) {
     if (!graph->directed) {
	if (graph->nodes) {
	   dispatch_user_action(UNSELECT);
#	   ifdef DEBUG
	   out2file = fopen("graph.out", "w");
	   printGraph(out2file, graph);
	   fclose(out2file);
#	   endif

#	   ifdef DEBUG
	   outfile = fopen("maxplanar.out", "w");
#	   endif
/*         the following properties a assumed for a graph before it is *
 *         processed: (checked in call to maxplanarsubgraph) *
 *          - no self-loops *
 *          - no multiple edges */
/*	   crossCount = crossingnumber(graph); */
	
	   SGRAPH_PROC_RETURN_CODE = OK;

	   make_new_node_labels(graph);

	   mpg = maxplanarsubgraph(graph);

           if (!mpg /*crossCount == 0 */) {
	      message("\n\ncrossno: graph is planar.  ");

	      info->no_changes 	    		= FALSE;
	      info->no_structure_changes	= TRUE;
	      info->recompute  	    		= TRUE;
     	      info->repaint   	    		= TRUE;
	      info->recenter	 		= TRUE;
	   } else {
	      message("\n\ncrossno: found nonplanar graph.  ");
	      edgeList = get_edges_not_in_mpg(graph);

#	      ifdef DEBUG
   	      for_slist(edgeList, Selem) {
      	         edgeCount++;
	      } end_for_slist(edgeList, Selem);
              message ("\nembed: found %d edges to embed.", edgeCount);
#	      endif

	      set_graphattrs(mpg, make_attr(ATTR_DATA, (char *) edgeList));

#		 ifdef DEBUG
	         {
		   FILE		*outfile;

		   outfile = fopen("mpg.out", "w");
	           printGraph(outfile, mpg);
		   fclose(outfile);
	         }
#		 endif

/*	         make_edge_attributes_for_mpg(graph); */
/*	         draw the graph once again by embedding the mpg planar and *
 *	         inserting the remaining edges with as few crossings as *
 *		 possible */

	         info->new_buffer = create_buffer();
	         info->new_sgraph = mpg;
	         info->new_selected = SGRAPH_SELECTED_NONE;

	         graph_set(
		    (Graph)create_graphed_graph_from_sgraph_in_buffer(
		       info->new_sgraph,info->new_buffer), 
		    RESTORE_IT, 0);

/*	         dummyres = woods_planar_layout(info->new_sgraph); */

/*               sugiyama(info->new_sgraph, 64, 64); */

	         info->no_changes 	    	= FALSE;
	         info->no_structure_changes 	= FALSE;
		 info->recompute  	    	= TRUE;
     	         info->repaint   	    	= TRUE;
	         info->recenter	 	= TRUE;


	   } /* endif */
	} else {
	   message("\ngraph is empty.\n");
	} /*endif */
     } else {
	message("\nThe graph is directed\n");
     } /* endif */
  } else {
     message("\nno graph found.\n");
  } /* endif */
}


Global void CallCrossingNumberNaiveEmbedding(Sgraph_proc_info info)
{
  Sgraph	graph;
  int           crossings;

  graph = info->sgraph;
  SGRAPH_PROC_RETURN_CODE = CANCEL;

  if (graph) {
     if (!graph->directed) {
	if (graph->nodes) {

	   SGRAPH_PROC_RETURN_CODE = OK;

	   crossings = crossingnumber_naive_embedding(graph);
	   message ("\nembed: found %d crossings while embedding edges.",
		    crossings);

	   info->no_changes 	    		= FALSE;
	   info->no_structure_changes		= TRUE;
	   info->recompute  	    		= TRUE;
	   info->repaint   	    		= TRUE;
	   info->recenter	 		= TRUE;

	} else {
	   message("\ngraph is empty.\n");
	} /*endif */
     } else {
	message("\nThe graph is directed\n");
     } /* endif */
  } else {
     message("\nno graph found.\n");
  } /* endif */
}



Global	void	CallCrossingNumberBipartiteEmbedding (Sgraph_proc_info info)
{
  Sgraph	graph;
  int           crossings;

  graph = info->sgraph;
  SGRAPH_PROC_RETURN_CODE = CANCEL;

  if (graph) {
     if (!graph->directed) {
	if (graph->nodes) {

	   SGRAPH_PROC_RETURN_CODE = OK;

	   crossings = crossingnumber_bipartite_embedding(graph);
/*	   message ("\nembed: found %d crossings while embedding edges.",
		    crossings); */

	   info->no_changes 	    		= FALSE;
	   info->no_structure_changes		= TRUE;
	   info->recompute  	    		= TRUE;
	   info->repaint   	    		= TRUE;
	   info->recenter	 		= TRUE;

	} else {
	   message("\ngraph is empty.\n");
	} /*endif */
     } else {
	message("\nThe graph is directed\n");
     } /* endif */
  } else {
     message("\nno graph found.\n");
  } /* endif */
}



Global	void	CallCrossingNumberCompleteEmbedding (Sgraph_proc_info info)
{
  Sgraph	graph;
  int           crossings;

  graph = info->sgraph;
  SGRAPH_PROC_RETURN_CODE = CANCEL;

  if (graph) {
     if (!graph->directed) {
	if (graph->nodes) {

	   SGRAPH_PROC_RETURN_CODE = OK;

	   crossings = crossingnumber_complete_embedding(graph);
/*	   message ("\nembed: found %d crossings while embedding edges.",
		    crossings); */

  
	   info->no_changes 	    		= FALSE;
	   info->no_structure_changes		= TRUE;
	   info->recompute  	    		= TRUE;
	   info->repaint   	    		= TRUE;
	   info->recenter	 		= TRUE;

	} else {
	   message("\ngraph is empty.\n");
	} /*endif */
     } else {
	message("\nThe graph is directed\n");
     } /* endif */
  } else {
     message("\nno graph found.\n");
  } /* endif */
}




Global void CallCrossingNumberPrepare(Sgraph_proc_info info)
{
  Sgraph	graph, graph2;
  int		Property;


  graph = info->sgraph;
  SGRAPH_PROC_RETURN_CODE = CANCEL;

  if (graph) {
     if (!graph->directed) {
	if (graph->nodes) {
	   make_new_node_labels(graph);
	   create_and_init_all_attributes(graph);

	   Property = pq_planarity(graph);
	   if (Property == MULTI_EDGE) {
	      message("\ngraph has multiple edges.\n");
	      clear_all_attributes(graph);
	      return;
	   } else if (Property == LOOP) {
	      message("\ngraph has self loops.\n");
	      clear_all_attributes(graph);
	      return;
	   } /* endif */

	   SGRAPH_PROC_RETURN_CODE = OK;

	   if (Property == TRUE) {
	      message("\n\ncrossno: graph is planar.  ");
	      clear_all_attributes(graph);
	   } else {
	      message("\n\ncrossno: found nonplanar graph.  ");
	      clear_all_attributes(graph);
	      graph2 = copy_graph_without_attrs(graph);


/*	         make_edge_attributes_for_mpg(graph); */
/*	         draw the graph once again by embedding the mpg planar and *
 *	         inserting the remaining edges with as few crossings as *
 *		 possible */

		 info->new_buffer = create_buffer();
		 info->new_sgraph = graph2;
		 info->new_selected = SGRAPH_SELECTED_NONE;

		 graph_set(
		    (Graph)create_graphed_graph_from_sgraph_in_buffer(
		       info->new_sgraph,info->new_buffer),
		    RESTORE_IT, 0);

		 info->no_changes 	    	= FALSE;
		 info->no_structure_changes 	= FALSE;
		 info->recompute  	    	= TRUE;
		 info->repaint   	    	= TRUE;
		 info->recenter	 		= TRUE;
	   } /* endif */

	} else {
	   message("\ngraph is empty.\n");
	} /*endif */
     } else {
	message("\nThe graph is directed\n");
     } /* endif */
  } else {
     message("\nno graph found.\n");
  } /* endif */
}


Global void CallCrossingNumberBipartitePrepare(Sgraph_proc_info info)
{
  Sgraph	graph, graph2;
  Snode		n;
  int		Property;


  graph = info->sgraph;
  SGRAPH_PROC_RETURN_CODE = CANCEL;

  if (graph) {
     if (!graph->directed) {
	if (graph->nodes) {
	   make_new_node_labels(graph);
	   create_and_init_all_attributes(graph);

	   Property = pq_planarity(graph);
	   if (Property == MULTI_EDGE) {
	      message("\ngraph has multiple edges.\n");
	      clear_all_attributes(graph);
	      return;
	   } else if (Property == LOOP) {
	      message("\ngraph has self loops.\n");
	      clear_all_attributes(graph);
	      return;
	   } /* endif */


	   if (Property == TRUE) {
	      message("\n\ncrossno: graph is planar.  ");
	      clear_all_attributes(graph);
	   } else {
	      message("\n\ncrossno: found nonplanar graph.  ");
	      clear_all_attributes(graph);
/*            expect no attributes assigned to nodes and edges in graph	*/
/*            reset node_flags to UNVISITED 			*/
   	      for_all_nodes(graph, n) {
                 attr_flags(n) = UNVISITED;
              } end_for_all_nodes(graph, n);

 	      if (!is_bipartite(graph)) {
                 message("\ngraph is not bipartite.");
	      } else {

	         SGRAPH_PROC_RETURN_CODE = OK;

	         graph2 = copy_graph_with_flags(graph);


/*	         make_edge_attributes_for_mpg(graph); */
/*	         draw the graph once again by embedding the mpg planar and *
 *	         inserting the remaining edges with as few crossings as *
 *		 possible */

		 info->new_buffer = create_buffer();
		 info->new_sgraph = graph2;
		 info->new_selected = SGRAPH_SELECTED_NONE;

		 graph_set(
		    (Graph)create_graphed_graph_from_sgraph_in_buffer(
		       info->new_sgraph,info->new_buffer),
		    RESTORE_IT, 0);

		 info->no_changes 	    	= FALSE;
		 info->no_structure_changes 	= FALSE;
		 info->recompute  	    	= TRUE;
		 info->repaint   	    	= TRUE;
		 info->recenter	 		= TRUE;
	      } /* endif */

      		for_all_nodes(graph, n) {
	 	   attr_data(n) = NULL;
      		} end_for_all_nodes(graph, n);


	   } /* endif */
	} else {
	   message("\ngraph is empty.\n");
	} /*endif */
     } else {
	message("\nThe graph is directed\n");
     } /* endif */
  } else {
     message("\nno graph found.\n");
  } /* endif */
}


Global void	CallEmbedRemainingEdges(Sgraph_proc_info info)
{
   Sgraph graph;
   int		crossings, nodes, edges;

/* int		edgeCount;
   Slist	edgeList, Selem; */

   graph = info->sgraph;
/* edgeList = (Slist) attr_data_of_type(info->sgraph, Slist);
   edgeCount = 0;
   for_slist(edgeList, Selem) {
      edgeCount++;
   } end_for_slist(edgeList, Selem);
   message ("\nembed: found %d edges to embed.", edgeCount); 		    */

   crossings = embed_remaining_edges(graph);
   message ("\nembed: found %d crossings while embedding edges.", crossings);

/* delete the following both lines in afinal version 			    */
   crossings = count_all_edge_crossings_in_graph(graph, &nodes, &edges);
   message ("\nembed: found %d crossings in given embedding.", crossings);
   message ("\n\tgraph has %d nodes and %d edges.", nodes, edges);

           dispatch_user_action(UNSELECT);

   info->no_changes 	    	= FALSE;
   info->no_structure_changes 	= FALSE;
   info->recompute  	    	= TRUE;
   info->repaint   	    	= TRUE;
   info->recenter	 	= TRUE;

}   



Global void	CallCountCrossingsInEmbedding(Sgraph_proc_info info)
{
   Sgraph graph;
   int		crossings, nodes, edges;

   graph = info->sgraph;
/* make_new_node_labels(graph); */


   crossings = count_all_edge_crossings_in_graph(graph, &nodes, &edges);
   message ("\nembed: found %d crossings in given embedding.", crossings);
   message ("\n\tgraph has %d nodes and %d edges.", nodes, edges);

           dispatch_user_action(UNSELECT);

   info->no_changes 	    	= FALSE;
   info->no_structure_changes 	= FALSE;
   info->recompute  	    	= TRUE;
   info->repaint   	    	= TRUE;
   info->recenter	 	= TRUE;

}


