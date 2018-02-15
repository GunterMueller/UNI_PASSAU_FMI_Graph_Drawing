#include <std.h>
#include <sgraph.h>
#include <slist.h>
#include <graphed.h>
#include <math.h>
#include "layout_info_export.h"


Global void output_node_degrees (Sgraph sgraph);
Global void output_all_faces (Sgraph dualgraph);
Global void OutputAllEdgeLengths(Sgraph sgraph);
Global void output_all_angles (Sgraph dualgraph);

#undef DEBUG

void	call_output_long_statistics (Sgraph_proc_info info)
{
  Sgraph dualgraph; 

  dualgraph = compute_inner_faces_and_angles (info->sgraph);

  OutputAllEdgeLengths (info->sgraph);
  output_node_degrees (info->sgraph);
  output_all_faces (dualgraph);
  output_all_angles (dualgraph);

  remove_mydualnodeattrs(dualgraph);
  if(dualgraph)
    remove_graph(dualgraph);  
}


/* countNodes berechnet die Anzahl der Knoten im Graphen. */

Global int countNodes(Sgraph sgraph)
{  
  int count=0;
  Snode node;

  if(sgraph==empty_sgraph) {
    return(0);
  } else if(sgraph->nodes==empty_node) {
    return(0);
  } else {
    for_all_nodes(sgraph,node) {
      count++;
    } end_for_all_nodes(sgraph,node);
    return(count);
  }
} 


/* countEdges berechnet die Anzahl der Kanten im Graphen. */

Global int countEdges(Sgraph sgraph)
{  
  int count=0;
  Snode node;
  Sedge edge;

  if(sgraph==empty_sgraph) {
    return(0);
 } else if(sgraph->nodes==empty_node) {
   return(0);
 } else {
   for_all_nodes(sgraph,node) {
     for_sourcelist(node,edge)
       if(sgraph->directed || unique_edge(edge)) {
	 count++;
       } end_for_sourcelist(node,edge);
   } end_for_all_nodes(sgraph,node);
   return(count);
 }
}


#include <xview/xview.h>


Global Area_used AreaOfDrawing (Sgraph sgraph)
{  
  extern  Rect	compute_rect_around_graph();
  Graphed_graph	ggraph=NULL;
  Rect          r;
  Area_used    area_used;

  if(sgraph)
  ggraph = graphed_graph (sgraph);

  area_used.width = 0.0;
  area_used.height = 0.0;
  area_used.used = 0.0;

  if (ggraph != (Graphed_graph)NULL) {
    r = compute_rect_around_graph (ggraph);
    area_used.width = r.r_width;
    area_used.height = r.r_height;
    area_used.used = r.r_width * r.r_height;
  }

  return area_used;
}


Global NodeDistances ComputeNodeDistances (Sgraph sgraph)
{
  Snode         node, n;
  double        dist;
  double	sum_average, sum_average_square;
  NodeDistances distances;
  int		count_pairwise_dist;

  distances.shortest = -1.0;
  distances.longest  = 0.0;
  distances.variance = 0.0;
  distances.average  = 0.0;
  distances.ratio    = 0.0;

  sum_average = 0.0;
  sum_average_square = 0.0;
  count_pairwise_dist = 0;

  if(sgraph)
  for_all_nodes (sgraph, node) {
    for_all_nodes (sgraph, n) {      
      if (n < node) {
        count_pairwise_dist++;
	dist = sqrt((double)((n->x - node->x) * (n->x - node->x) +
                             (n->y - node->y) * (n->y - node->y)));
        if(distances.shortest<0.0 || distances.shortest > dist) {
	  distances.shortest = dist;
	}
	if (distances.longest < dist) {
	  distances.longest = dist;
	}
	sum_average += dist;
	sum_average_square += dist*dist;
      }
    } end_for_all_nodes (sgraph, n);
  } end_for_all_nodes (sgraph, node);
  
  if(distances.shortest<0.0) {
    distances.shortest=0.0;
  }

  if (distances.shortest > 0.0) { 
    distances.ratio = distances.longest / distances.shortest;
  }

  if(count_pairwise_dist) {
    distances.average = sum_average / count_pairwise_dist;
    distances.variance += sum_average_square / count_pairwise_dist - pow ((sum_average/count_pairwise_dist), 2.0);
  } else { 
    distances.average = distances.shortest;
    distances.variance=0.0;
  }

  return distances;
}




Global void output_node_degrees (Sgraph sgraph)
{
  Snode node;
  Sedge edge;
  int first = TRUE;

  message ("Node degrees              :\t");
  if(sgraph)
  for_all_nodes (sgraph,node) {

    int degree = 0;
    
    for_sourcelist (node,edge) {
      degree ++;
    } end_for_sourcelist (node,edge);
    if (sgraph->directed) for_targetlist (node, edge) {
      degree ++;
    } end_for_targetlist (node, edge);
    
    if (first) {
      message ("%d", degree);
      first = FALSE;
    } else {
      message ("\t%d", degree);
    }
   
  } end_for_all_nodes (sgraph, node);
  message ("\n");
}
