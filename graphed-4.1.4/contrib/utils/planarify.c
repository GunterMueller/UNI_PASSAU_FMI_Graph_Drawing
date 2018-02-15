#include <sgraph/std.h>
#include <sgraph/sgraph.h>
#include <sgraph/slist.h>
#include <sgraph/graphed.h>
#include <sgraph/random.h>

#include <xview/xview.h>
#include <xview/notice.h>

#include "planarity_ht/planarity_ht_export.h" 

Global void call_planarify (Sgraph_proc_info info)
{
  Sgraph graph;
  Snode  node;
  Sedge  edge;
  int    cont = TRUE;
  
  graph = info->sgraph;
  if (graph == empty_sgraph) {
    warning ("empty_graph\n");
    return;
  }

  while (cont && graph->nodes != empty_snode) {

    switch (planarity(graph)) {
    case NONPLANAR :
      cont = TRUE;
      break;
    case SUCCESS :
    case SELF_LOOP :
    case MULTIPLE_EDGE :
    case NO_MEM :
      cont = FALSE;
      break;
    }

    if (cont) {

      Sedge rem = empty_sedge;
      int   count = 0;

      for_all_nodes (graph, node) {
	for_sourcelist (node, edge) {
	  count ++;
	} end_for_sourcelist (node, edge);
      } end_for_all_nodes (graph, node);

      for_all_nodes (graph, node) {
	for_sourcelist (node, edge) {
	  if ((random() % count) == 0) {
	    rem = edge;
	  }
	} end_for_sourcelist (node, edge);
      } end_for_all_nodes (graph, node);

      if (rem != empty_edge) {
	remove_edge (rem);
      }
    }
  }
}

Global void menu_planarify (Menu menu, Menu_item menu_item)
{
  extern Frame base_frame;

  if (NOTICE_YES ==
      notice_prompt (base_frame, NULL,
		     NOTICE_MESSAGE_STRINGS, "This procedure might delete some edges\n", NULL,
		     NOTICE_BUTTON_YES,	"Ok",
		     NOTICE_BUTTON_NO,	"Chancel",
		     NULL)) {
    call_sgraph_proc (call_planarify, "43");
  }
}


