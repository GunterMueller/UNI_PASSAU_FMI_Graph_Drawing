#include <sgraph/std.h>
#include <sgraph/sgraph.h>
#include <sgraph/slist.h>
#include <sgraph/graphed.h>
#include <sgraph/random.h>

#include <graphed/simple_fs.h>
#include <graphed/util.h>
#include <utils/external_program_caller.h>

#include <graphed/existing_extensions.h>

#include <xview/xview.h>
#include <xview/notify.h>


#ifdef EXTENSION_springembedder_rf
#include <springembedder_rf/springembedder_rf_export.h>
#endif
#ifdef DIFF_4_0_21_TO_22
#ifdef EXTENSION_planarity_ht
#include <planarity_ht/planarity_ht_export.h>
#endif
#endif

static Sgraph create_ring_sgraph (Sgraph g, int size)
{
  Snode      previous_node = empty_snode,
             new_node = empty_snode;
  int        i;
  Attributes empty_attrs;

  empty_attrs = make_attr (ATTR_DATA, NULL);

  /* Create the first node */
  new_node = make_node (g, empty_attrs);
  previous_node = new_node;

  /* Create all following nodes */
  for (i=1; i<size; i++) {
    new_node = make_node (g, empty_attrs);
    (void) make_edge (previous_node, new_node, empty_attrs);
    previous_node = new_node;
  }
  make_edge (new_node, first_node_in_graph(g), empty_attrs);

  return g;
}


static void call_create_ring_sgraph (Sgraph_proc_info info, char *size_ptr)
{
  int    size = atoi(size_ptr);

/* dispatch_user_action (UNSELECT); */

  if (info->sgraph != empty_sgraph) {
    while (info->sgraph->nodes != empty_snode) {
      remove_node (info->sgraph->nodes);
    }
  } else {
    info->sgraph = make_graph (make_attr(ATTR_DATA, NULL));
  }

  create_ring_sgraph (info->sgraph, size);
}


Global void call_set_dummy_coordinates_and_labels (Sgraph_proc_info info)
{
  Sgraph g;
  Snode  n;
  Sedge  e;
  char   buffer[100];

  g = info->sgraph;
  if (g == empty_sgraph) {
    return;
  }

  for_all_nodes (g, n) {

    sprintf (buffer, "%d", n->nr);
    set_nodelabel (n, strsave (buffer));
    set_node_xy (n, random()%1000, random()%1000);

    for_sourcelist (n, e) {
      sprintf (buffer, "%d->%d", e->snode->nr, e->tnode->nr);
      set_edgelabel (e, strsave(buffer));
    } end_for_sourcelist (n, e);

  } end_for_all_nodes (g, n);

  info->recenter = TRUE;
}


Global void call_fit_nodes_to_text (Sgraph_proc_info info)
{
  Sgraph g;
  Snode  n;

  g = info->sgraph;
  if (g == empty_sgraph) {
    return;
  }

  for_all_nodes (g, n) {
    fit_node_to_text (graphed_node(n));
  } end_for_all_nodes (g, n);

  info->recompute = TRUE;
}


Global void menu_test_test_proc (Menu menu, Menu_item menu_item)
{
  call_sgraph_proc (call_create_ring_sgraph, "10");
  call_sgraph_proc (call_set_dummy_coordinates_and_labels, (char *)0);
  call_sgraph_proc (call_fit_nodes_to_text, (char *)0);
#ifdef EXTENSION_springembedder_rf
  call_sgraph_proc (call_fast_springembedder_rf, (char *)0);
#endif

  dispatch_user_action (SELECT_ALL);
  dispatch_user_action (CENTER_SELECTION);
}



static void make_test3_node_attrs (Snode node)
{
  set_nodeattrs (node, make_attr (ATTR_DATA, "this node attr assigned by test 3"));
  fprintf (stderr,
	   "Node attr assigned test3, nr : %d current key : %s\n",
	   node->nr,
	   node->attrs_key);
}


static void make_test3_edge_attrs (Sedge edge)
{
  set_edgeattrs (edge, make_attr (ATTR_DATA, "this edge attr assigned by test 3"));
  fprintf (stderr,
	   "Edge attr assigned test3, nr : %d-%d current key : %s\n",
	   edge->snode->nr, edge->tnode->nr,
	   edge->attrs_key);
}


static void remove_test3_node_attrs (Snode node)
{
  fprintf (stderr,
	   "Node attr removed test3, nr : %d current key : %s\n",
	   node->nr,
	   node->attrs_key);
}


static void remove_test3_edge_attrs (Sedge edge)
{
  fprintf (stderr,
	   "Edge attr removed test3, nr : %d-%d current key : %s\n",
	   edge->snode->nr, edge->tnode->nr,
	   edge->attrs_key);
}


static void remove_test3_graph_attrs (Sgraph graph)
{
  fprintf (stderr,
	   "Graph attr removed test3, current key : %s\n",
	   graph->attrs_key);
}


#ifndef DIFF_4_0_21_TO_22
#include "planarity_ht/planarity_ht_export.h"
#endif

static void call_test3_proc (Sgraph_proc_info info, char *key)
{
  Snode      n;
  Sedge      e;

#if !defined(DIFF_4_0_21_TO_22) || defined(EXTENSION_planarity_ht)
  if (info->sgraph != empty_sgraph) {

    restore_sgraph_attrs (info->sgraph, key,
      make_test3_node_attrs, make_test3_edge_attrs,
      TRUE);

    fprintf (stderr, "\n");
    for_all_nodes (info->sgraph, n) {
      fprintf (stderr, "%d : %s\n", n->nr, attr_data(n));
      for_sourcelist (n, e) {
	fprintf (stderr, "%d-%d : %s\n", e->snode->nr, e->tnode->nr, attr_data(e));
      } end_for_sourcelist (n, e);
    } end_for_all_nodes (info->sgraph, n);

    save_sgraph_attrs (info->sgraph, key);

    switch(planarity(info->sgraph)) {
      case SUCCESS : message("graph is planar.\n");
	break;
      case NONPLANAR : message("graph is nonplanar.\n");
	break;
      case SELF_LOOP : message("graph contains self-loops.\n");
	break;
      case MULTIPLE_EDGE : message("graph contains multiple edges.\n");
	break;
      case NO_MEM : message("not enough memory.\n");
	break;
      }

    restore_sgraph_attrs (info->sgraph, key, NULL, NULL, TRUE);

    fprintf (stderr, "\n");
    for_all_nodes (info->sgraph, n) {
      fprintf (stderr, "%d : %s\n", n->nr, attr_data(n));
      for_sourcelist (n, e) {
	fprintf (stderr, "%d-%d : %s\n", e->snode->nr, e->tnode->nr, attr_data(e));
      } end_for_sourcelist (n, e);
    } end_for_all_nodes (info->sgraph, n);

    fprintf (stderr, "\n");

    info->sgraph->remove_node_proc = remove_test3_node_attrs;
    info->sgraph->remove_edge_proc = remove_test3_edge_attrs;
    info->sgraph->remove_graph_proc = remove_test3_graph_attrs;

    save_sgraph_attrs    (info->sgraph, key);
  }
#endif
}


Global void menu_test3_proc (Menu menu, Menu_item menu_item)
{
  call_sgraph_proc (call_test3_proc, "42");
}



static void call_test4_proc (Sgraph_proc_info info, char *key)
{
  Snode      n;

  if (info->sgraph != empty_sgraph) {

    restore_sgraph_attrs (info->sgraph, key, NULL, NULL, TRUE);

    fprintf (stderr, "\nTest 4\n");
    for_all_nodes (info->sgraph, n) {
      fprintf (stderr, "%d : %s\n", n->nr, n->attrs_key);
    } end_for_all_nodes (info->sgraph, n);

    for_all_nodes (info->sgraph, n) {
      set_nodeattrs (n, make_attr (ATTR_DATA, "this data assigned by test 4"));
    } end_for_all_nodes (info->sgraph, n);

    fprintf (stderr, "\n");
    for_all_nodes (info->sgraph, n) {
      fprintf (stderr, "%d : %s\n", n->nr, attr_data(n));
    } end_for_all_nodes (info->sgraph, n);

    fprintf (stderr, "\n");

    save_sgraph_attrs    (info->sgraph, key);
  }
}


Global void menu_test4_proc (Menu menu, Menu_item menu_item)
{
  call_sgraph_proc (call_test4_proc, "43");
}


static Sgraph_proc_info saved_info;

Notify_value animate (void)
{
  Snode n;
  Sedge e;
  static int i = 0;
  static int j = 0;

  if (saved_info->sgraph != empty_sgraph) {

    int counter = 0;

    for_all_nodes (saved_info->sgraph, n) {

      for_targetlist (n,e) {

	if (j == counter) {
	  if ((int)edge_get(graphed_edge(e), EDGE_TYPE) == find_edgetype("#solid")) {
	    edge_set (graphed_edge(e), EDGE_TYPE, find_edgetype("#dotted"), 0);
	  } else {
	    edge_set (graphed_edge(e), EDGE_TYPE, find_edgetype("#solid"), 0);
	  }
	}

	counter ++;

      } end_for_targetlist (n,e);

    } end_for_all_nodes (saved_info->sgraph, n);

    force_repainting ();
    i++;
    j = (j+1) % counter;
  }
  return NOTIFY_DONE;
}


static void call_test5_proc (Sgraph_proc_info info, char *key)
{
#if FALSE
  Snode n;
  Sedge e;
  int i, j;
#endif

  struct itimerval timer;
  extern Frame base_frame;

  saved_info = info;

  timer.it_value.tv_usec = 900000;
  timer.it_interval.tv_usec = 100000;
  notify_set_itimer_func (base_frame, (Notify_func)animate, ITIMER_REAL, &timer, NULL);

#if FALSE
  if (info->sgraph != empty_sgraph) {

    for (i=0; i<100; i++) {
      for_all_nodes (info->sgraph, n) {
/*
	node_set (graphed_node(n), NODE_POSITION, n->x+1, n->y+1, 0);
	set_node_xy (n, n->x+1, n->y+1);
*/
	for_targetlist (n,e) {
	  edge_set (graphed_edge(e), EDGE_TYPE, find_edgetype("#dotted"), 0);
	} end_for_targetlist (n,e);

	force_repainting ();
	for (j=0; j<1000; j++) {
	  fprintf (stderr, "Do samma !\n");
	}

	for_targetlist (n,e) {
	  edge_set (graphed_edge(e), EDGE_TYPE, find_edgetype("#solid"), 0);
	} end_for_targetlist (n,e);

	force_repainting ();
	for (j=0; j<1000; j++) {
	  fprintf (stderr, "Etz samma do!\n");
	}

      } end_for_all_nodes (info->sgraph, n);
      force_repainting ();
    }
  }
#endif
}


Global void menu_test5_proc (Menu menu, Menu_item menu_item)
{
  call_sgraph_proc (call_test5_proc, "43");
}



Local Edgeline merge_edgeline (Edgeline new_edge_line, Edgeline old_edge_line)
{
	Edgeline el;

	for_edgeline (new_edge_line, el) {
		if (old_edge_line == (Edgeline)NULL) {
			old_edge_line = new_edgeline (
				edgeline_x (el), edgeline_y (el));
		} else {
			old_edge_line = add_to_edgeline (old_edge_line,
				edgeline_x (el), edgeline_y (el)); 
		}
	} end_for_edgeline (new_edge_line, el);

	return old_edge_line;

} /* end of merge_edgeline */


static void call_test6_proc (Sgraph_proc_info info, char *key)
{
  Snode		n, node, right_node, left_node;
  Sedge		e, left_edge, right_edge, new_edge;
  Edgeline 	left_edge_line, right_edge_line, left_right_edge_line;

  if (info->sgraph == empty_sgraph || info->sgraph->nodes == NULL) {
    return;
  }

  node = empty_snode;
  left_node  = empty_snode;
  right_node = empty_snode;
  left_edge = empty_sedge;
  right_edge = empty_sedge;

  for_all_nodes (info->sgraph, n) {
    if (!strcmp(n->label, "m")) {
      node = n;
    } else if (!strcmp(n->label, "l")) {
      left_node = n;
    } else if (!strcmp(n->label, "r")) {
      right_node = n;
    }
  } end_for_all_nodes (info->sgraph, n);
  

  for_sourcelist (node, e) {
    if (e->tnode == right_node) {
      right_edge = e;
    }
    if (!info->sgraph->directed && e->tnode == left_node) {
      left_edge = e;
    }
  } end_for_sourcelist (node, e);

  if (info->sgraph->directed) for_targetlist (node, e) {
    if (e->snode == left_node) {
      left_edge = e;
    }
  } end_for_targetlist (node, e);
  
  if (left_edge == empty_sedge || right_edge == empty_sedge) {
    return;
  }


  left_edge_line = (Edgeline) edge_get
    (graphed_edge(get_unique_edge_handle(left_edge)),EDGE_LINE);  
  right_edge_line = (Edgeline) edge_get
    (graphed_edge(get_unique_edge_handle(right_edge)),EDGE_LINE);  
  left_right_edge_line = (Edgeline)NULL;
  
  left_right_edge_line = merge_edgeline
    (left_edge_line, left_right_edge_line);
  left_right_edge_line = merge_edgeline
    (right_edge_line, left_right_edge_line);
  

  new_edge = make_edge (left_node, right_node,
			make_attr(ATTR_DATA,NULL));
  create_graphed_edge_from_sedge (get_unique_edge_handle (new_edge));  
  edge_set(graphed_edge(get_unique_edge_handle(new_edge)),
	   EDGE_LINE, left_right_edge_line->suc,
	   NULL);
}


Global void menu_test6_proc (Menu menu, Menu_item menu_item)
{
  call_sgraph_proc (call_test6_proc, "43");
}
