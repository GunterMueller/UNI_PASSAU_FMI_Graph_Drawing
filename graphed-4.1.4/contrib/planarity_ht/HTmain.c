/* (C) Universitaet Passau 1986-1994 */
/* Hauptprogramm */

#include "std.h"
#include "sgraph.h"
#include "slist.h"
#include "graphed.h"

#include "planarity_ht_export.h"

extern	void	dualgraph(Sgraph_proc_info info);

#define ENDPOINT(node,edge) ((node) == (edge)-> tnode ? (edge)-> snode : (edge)-> tnode)

/* ========================================================================= */
  
static	void	 planarity_test(Sgraph_proc_info info)
{ 
  switch(planarity(info->sgraph))
    {
      case SUCCESS       : message("graph is planar.\n");
                           break;
      case NONPLANAR     : message("graph is nonplanar.\n");
                           break;
      case SELF_LOOP     : message("graph contains self-loops.\n");
                           break;
      case MULTIPLE_EDGE : message("graph contains multiple edges.\n");
                           break;
      case NO_MEM        : message("not enough memory.\n");
                           break;
    }
}
  
/* ====================================================================== */

static	void	write_graph(Sgraph graph)
{ Snode node;
  Slist list,l;

  message("ordered adjacency lists:\n");
  for_all_nodes(graph,node)
    {
      message("node <%s> : edges to",node->label);
      list = attr_data_of_type(node,Slist);
      for_slist(list,l)
        message(" <%s>",ENDPOINT(node,attr_data_of_type(l,Sedge))->label);
      end_for_slist(list,l)
      message("\n");
    }
  end_for_all_nodes(graph,node)
}
 
/* ---------------------------------------------------------------------- */ 

static	void	 embedding (Sgraph_proc_info info)
{ 
  switch(embed(info-> sgraph))
    {
      case SUCCESS       : message("graph is planar.\n");
                           write_graph(info->sgraph);
                           break;
      case NONPLANAR     : message("graph is nonplanar.\n");
                           break;
      case SELF_LOOP     : message("graph contains self-loops.\n");
                           break;
      case MULTIPLE_EDGE : message("graph contains multiple edges.\n");
                           break;
      case NO_MEM        : message("not enough memory.\n");
                           break;
    }
}
  
void dualgraph (Sgraph_proc_info info)
{
   Sgraph graph, DualGraph;
   graph = info->sgraph;
   switch(embed(graph))
     {
      case SELF_LOOP :     message("graph contains self-loops\n");
                           break;
      case MULTIPLE_EDGE : message("graph contains multiple edges\n");
                           break;
      case NONPLANAR     : message("graph is nonplanar\n");
                           break;
      case NO_MEM        : message("not enough memory\n");
                           break;
      case SUCCESS       : DualGraph = dual(graph);
                           info->new_sgraph = DualGraph;
                           break;
      default : break;
      }
  }

/* ====================================================================== */

/* Einfuegen eigener Menueeintraege ins Hauptprogramm */
    
void menu_planarity_ht (Menu menu, Menu_item menu_item)
{
  call_sgraph_proc (planarity_test, NULL);
}
  
void menu_planarity_ht_embedding (Menu menu, Menu_item menu_item)
{
  call_sgraph_proc (embedding, NULL);
}

void menu_planarity_ht_dualgraph (Menu menu, Menu_item menu_item)
{
  call_sgraph_proc (dualgraph, NULL);
}

