#include "glob_var_for_algo.h"
#include "help_for_algo.h"
#include <sgraph/slist.h>  
#include <sgraph/graphed.h>


void my_dfs(Snode n, int directed)
{
Sedge e;
 
 attr_flags(n)=TRUE;
 for_sourcelist(n,e)
 {
  if(attr_flags(e->tnode)==FALSE) my_dfs(e->tnode,directed);
 } end_for_sourcelist(n,e);

 if(directed==1)
 {
  for_targetlist(n,e)
  {
   if(attr_flags(e->snode)==FALSE) my_dfs(e->snode,directed);
  } end_for_targetlist(n,e);
 }
}

void make_connected(Sgraph sgraph,Slist *list_of_edges)
{
Snode node,lastnode;
Sedge new_edge;
int   directed;

 if(sgraph->directed) directed=1;
 else                 directed=0;
 
 for_all_nodes(sgraph,node)
 {
  attr_flags(node) = FALSE;
 } end_for_all_nodes(sgraph,node);

 lastnode=NULL;
 for_all_nodes(sgraph,node)
 {
  if(!attr_flags(node)) 
  { 
   if(lastnode!=NULL) 
   {
    new_edge=make_edge(lastnode,node,make_attr(ATTR_DATA,NULL));   
    (*list_of_edges)=add_to_slist((*list_of_edges),make_attr(ATTR_DATA,(char *)new_edge));
   }
   lastnode=node;
   my_dfs(node,directed); 
  }
 } end_for_all_nodes(sgraph,node);
}

 



void renumber_the_nodes_starting_at_one(Sgraph sgraph)
{
Snode node;
int i=0;

 for_all_nodes(sgraph,node)
 {
  i++;
  node->nr=i;
} end_for_all_nodes(sgraph,node);

}

void update_coords_of(Sgraph sgraph)
{
Snode node;

  zoom_the_graph(graphed_xsize,graphed_ysize,15,15,minimum_edge_length);
  for_all_nodes(sgraph,node)
  {
   node->x=zoomed_x_coord[node->nr];
   node->y=zoomed_y_coord[node->nr];
  } end_for_all_nodes(sgraph,node);
}

void make_straight_line_edges(Sgraph sgraph)
{
Snode node;
Sedge edge;
Edgeline line,newline;

 for_all_nodes(sgraph,node)
 {
  for_sourcelist(node,edge)
  {
   line=(Edgeline)edge_get(graphed_edge(edge),EDGE_LINE);
   newline=new_edgeline(edgeline_x(line),edgeline_y(line));
   add_to_edgeline(newline,edgeline_x(line->pre),edgeline_y(line->pre));
   free_edgeline(line);
   edge_set(graphed_edge(edge),EDGE_LINE,newline,NULL);
  } end_for_sourcelist(node,edge);
 } end_for_all_nodes(sgraph,node);
}


