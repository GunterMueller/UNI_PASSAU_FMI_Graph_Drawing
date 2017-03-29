#include <std.h>
#include <sgraph.h>
#include <slist.h>
#include <graphed.h>
#include "layout_info_export.h"


Global void remove_mydualnodeattrs(Sgraph dualgraph)
{
  Snode node;
  Cycle_List cycle_list,c_list;
  Angles_List angles_list,a_list;

  if(dualgraph)
    for_all_nodes(dualgraph,node)
        {
        cycle_list=attr_data_of_type(node,MyDualNodeAttrs)->cycle_list;
        while(cycle_list && cycle_list->suc!=cycle_list)
	  {
          c_list=cycle_list->pre;
          c_list->pre->suc=cycle_list;
          cycle_list->pre=c_list->pre;
          free(c_list);        
          }
        if(cycle_list)
          free(cycle_list);
        angles_list=attr_data_of_type(node,MyDualNodeAttrs)->angles_list;
          while(angles_list)
	    {
            a_list=angles_list->suc;
            free(angles_list);
            angles_list=a_list;
	    }
        free(attr_data_of_type(node,MyDualNodeAttrs));
        } end_for_all_nodes(dualgraph,node);
}


Global void remove_myedgeattrs(Sgraph sgraph)
{
  Snode snode;
  Sedge sedge;
  Angles_List angles_list,list;

  if(sgraph)
    for_all_nodes(sgraph,snode)
        {
        for_sourcelist(snode,sedge)
          { 
          angles_list=attr_data_of_type(sedge,MyEdgeAttrs)->angles_list;
          while(angles_list)
	    {
            list=angles_list->suc;
            free(angles_list);
            angles_list=list;
	    }
          free(attr_data_of_type(sedge,MyEdgeAttrs));
          } end_for_sourcelist(snode,sedge);
        } end_for_all_nodes(sgraph,snode);
}
  

Global void remove_mynodeattrs(Sgraph sgraph)
{
  Snode snode;
  Edge_List edge_list,list;

  if(sgraph)
    for_all_nodes(sgraph,snode)
        {
        edge_list=attr_data_of_type(snode,MyNodeAttrs)->edge_list;
        while(edge_list && edge_list->suc!=edge_list)
	  {
          list=edge_list->pre;
          list->pre->suc=edge_list;
          edge_list->pre=list->pre;
          free(list);        
  	  }
        if(edge_list)
          free(edge_list);
        free(attr_data_of_type(snode,MyNodeAttrs));
        } end_for_all_nodes(sgraph,snode);
}


Global void remove_all_my_attrs(Sgraph sgraph)
{
    remove_myedgeattrs(sgraph);
    remove_mynodeattrs(sgraph);
}
  



