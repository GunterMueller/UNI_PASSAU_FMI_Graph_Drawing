/* This software is distributed under the Lesser General Public License */
#include "glob_var_for_algo.h"
#include <sgraph/graphed.h>
#include <stdlib.h>

void shake(Sgraph_proc_info info)
{
Sgraph sgraph;
Snode  node;

 sgraph=info->sgraph;

 if((sgraph==empty_sgraph)||(sgraph->nodes==NULL)) return;
 
 for_all_nodes(sgraph,node)
 {
  node->x=rand()%(graphed_xsize+1);
  node->y=rand()%(graphed_ysize+1);
 } end_for_all_nodes(sgraph,node);

 info->no_changes=FALSE;
 info->no_structure_changes=TRUE;
 info->recenter=TRUE;
 info->recompute=FALSE;
 info->new_selected=SGRAPH_SELECTED_SAME;
 info->repaint=TRUE;
}

/*
void shake_graph(Menu menu, Menu_item menu_item)
{
call_sgraph_proc(shake, NULL);
}
*/
