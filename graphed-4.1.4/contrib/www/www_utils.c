#include <sgraph/std.h>
#include <sgraph/sgraph.h>
#include <sgraph/slist.h>
#include <sgraph/graphed.h>

#include <xview/xview.h>

#include "www_export.h"
#include <utils/external_program_caller.h>

/****************************************************************/
/*								*/
/*			    The Interface			*/
/*								*/
/****************************************************************/

/* some constants ...		*/

#define node_nr(n) (attr_flags((n)))
#define BUFSIZE 1024
#if defined (WWW_SNOOP) || defined (DIFF_4_0_21_TO_22)
#define WWW_VIEWER "netscape"

#define WWW_VIEWER_BEGIN "gnudoit '(w3-fetch"
#define WWW_VIEWER_END ")'"
#else
#define WWW_VIEWER "xmosaic"
#endif

void	call_www_open (Sgraph_proc_info info)
{
  Snode node;
#ifdef OBSOLETE
  Graphed_cep_info cep_info;
#endif
  char buffer [BUFSIZE];
#if defined (WWW_SNOOP) || defined (DIFF_4_0_21_TO_22)
  char *c;
#endif

  if (info->selected == SGRAPH_SELECTED_SNODE &&
      info->selection.snode->label != NULL) {

    node = info->selection.snode;
    
#ifdef OBSOLETE

    cep_info = new_graphed_cep_info();
    sprintf (buffer, "%s %s", WWW_VIEWER, node->label);
    cep_info.programname = buffer;

    run_external_program (&cep_info);

#endif

#if defined (WWW_SNOOP) || defined (DIFF_4_0_21_TO_22)
    if (strstr (node->label, ":") == NULL) {
      sprintf (buffer,
	       "%s \"http://www.uni-passau.de/%s\"%s",
               WWW_VIEWER_BEGIN,
               node->label,
               WWW_VIEWER_END);
    } else {
      sprintf (buffer,
	       "%s \"%s\"%s",
               WWW_VIEWER_BEGIN,
               node->label
               WWW_VIEWER_END);
fprintf (stderr, "%s\n", buffer);
    for (c=buffer; *c != '\0'; c++) {
      if (*c == '\n') {
          *c = '/';
      } else if (*c == ':' && (c+1) == ' ') {
        *(c+1) = '/';
      }
    }
fprintf(stderr, "%s\n", buffer);
#else
    if (strstr (node->label, "://") == NULL) {
      sprintf (buffer,
	       "%s http://www.uni-passau.de/%s\n", WWW_VIEWER, node->label);
    } else {
      sprintf (buffer,
	       "%s %s\n", WWW_VIEWER, node->label);
    }
#endif
    system(buffer);

  } else {

    error ("No node selected\n");

  }
}


void	menu_www_open (Menu menu, Menu menu_item)
{
  call_sgraph_proc (call_www_open, NULL);
}

/************************************************************************/
/*									*/
/*				Remove Trees				*/
/*									*/
/************************************************************************/

void	call_remove_trees (Sgraph_proc_info info)
{
  Sgraph web;
  Snode n;
  Snode leaf;
  int nodes_removed = 0;

  if (info->sgraph != NULL) {

    web = info->sgraph;

    do {
      leaf = empty_snode;
      for_all_nodes (web, n) {
	if ( /* condition 1 : no outgoing edges, 1 inconimg edge */
	    ((n->slist == empty_sedge) &&
	     (n->tlist != empty_sedge) && (n->tlist == n->tlist->tsuc)) ||
	    /* condition 2 : 1 incoming edge, 1 outgoing edge , reverse */
	    ((n->slist != empty_sedge) && (n->slist == n->slist->ssuc) &&
	     (n->tlist != empty_sedge) && (n->tlist == n->tlist->tsuc) &&
	     (n->slist->tnode == n->tlist->snode))
	    ) {
	  leaf = n;
	  break;
	}
      } end_for_all_nodes (web, n);
      if (leaf != empty_snode) {
	remove_node (leaf);
	nodes_removed ++;
      }
    } while (leaf != empty_snode);

    message ("Tree nodes removed: %d\n", nodes_removed);

  } else {

    error ("No graph selected\n");

  }
}


void	menu_remove_trees (Menu menu, Menu menu_item)
{
  call_sgraph_proc (call_remove_trees, NULL);
}

#if defined (WWW_SNOOP) || defined (DIFF_4_0_21_TO_22)

/************************************************************************/
/*									*/
/*				Remove Trees				*/
/*									*/
/************************************************************************/

#include <sys/stat.h>
#include <sys/param.h>
#include <sugiyama/sugiyama_export.h>

#include <xview/xview.h> /* for gridder.h */
#include <xview/panel.h> /* for gridder.h */
#include <graphed/gridder.h>


Local	void	 call_www_layout (Sgraph_proc_info info)
{
    Sgraph	graph;
    Snode	node;
    Sugiyama_settings sugiyama_settings;

    graph = info->sgraph;
    if (graph != empty_sgraph) {
	dispatch_user_action (SELECT_ALL);
	remove_all_self_loops_in_graph (graphed_graph(graph));
	remove_all_multiple_edges_in_graph (graphed_graph(graph));
	for_all_nodes (info->sgraph, node) {
	    if (graphed_node(node) != NULL) {
		fit_node_to_text (graphed_node (node));
	    }
	} end_for_all_nodes (info->sgraph, node);
	graph_set (graphed_graph(info->sgraph), RESTORE_IT);

	sugiyama_settings = init_sugiyama_settings ();
	sugiyama_settings.size_defaults_x = GRIDDER_DISTANCE_15_LARGEST_SIZE;
	sugiyama_settings.size_defaults_y = GRIDDER_DISTANCE_15_LARGEST_SIZE;
	sugiyama_settings.vertical_distance = recompute_gridder_size (
		NULL,
		sugiyama_settings.size_defaults_y,
		GRIDDER_HEIGHT);
	sugiyama_settings.horizontal_distance = recompute_gridder_size (
		NULL,
		sugiyama_settings.size_defaults_x,
		GRIDDER_WIDTH);
	sugiyama_layout (info->sgraph, sugiyama_settings);

	info->selected = SGRAPH_SELECTED_NOTHING;
	info->recompute = TRUE;
	info->recenter = TRUE;
    }
}

Local	void	www_snooper (void)
{
    struct	stat		buf1, buf2;
    int			       	stat1, stat2;
    FILE			*file;

    char 			filename[MAXPATHLEN];
    char			filename_snooper[MAXPATHLEN];
    char			filename_read[MAXPATHLEN];
    static	time_t		last_snoop_time = 0;

    sprintf (filename, "%s/%s.g", getenv("HOME"), "www/history");
    sprintf (filename_snooper, "%s.in", filename);
    sprintf (filename_read, "%s.read", filename);

    stat1 = stat (filename, &buf1);
    stat2 = stat (filename_snooper, &buf2);

    if (stat1 == -1 || stat2 == -1) {

	return;

    } else if (buf2.st_ctime > last_snoop_time) {

	last_snoop_time = buf2.st_ctime;
	dispatch_user_action (BASIC_LOAD, wac_buffer, filename);
	if ((file = fopen (filename_read, "w")) != NULL) {
	    fclose (file);
	}
	call_sgraph_proc (call_www_layout, NULL);
    }

}


void	menu_www_snooper (Menu menu, Menu_item menu_item)
{
    extern Frame base_frame;
    static struct itimerval timer;
    static int www_snooper_installed = FALSE;

    timer.it_value.tv_usec = 100000;
    timer.it_interval.tv_usec = 100000;

    notify_set_itimer_func (base_frame,
			    (Notify_func)www_snooper,
			    ITIMER_REAL,
			    &timer,
			    NULL);
}

#endif
