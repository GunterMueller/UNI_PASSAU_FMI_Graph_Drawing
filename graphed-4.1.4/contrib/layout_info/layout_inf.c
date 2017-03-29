#include <sgraph/std.h>
#include <sgraph/sgraph.h>
#include <sgraph/slist.h>
#include <sgraph/graphed.h>
#include <math.h>
#include "layout_info_export.h"


#undef DEBUG

int	layout_info_error_check (Sgraph sgraph)
{
  if (sgraph == empty_graph || sgraph->nodes == empty_node) {
    error ("No graph selected\n");
    return FALSE;
  } else if (countEdges (sgraph) ==0) {
    error ("Graph has no edges\n");
    return FALSE;
  }
  return TRUE;
}


Layout_info  layout_info (Sgraph sgraph)
{
  Layout_info info;
  Sgraph      dualgraph;

/*edge_attrs (sgraph);*/

  dualgraph = compute_inner_faces_and_angles (sgraph);

  info.number_of_nodes = countNodes (sgraph);
  info.number_of_edges = countEdges (sgraph);
  info.size            = info.number_of_nodes + info.number_of_edges;

  info.area           = AreaOfDrawing (sgraph);
  info.density         = info.area.used / info.size;

  info.node_distances  = ComputeNodeDistances(sgraph);
  
  info.number_of_bends     = count_nr_of_bends(sgraph);
  info.number_of_crossings = nr_of_crossings(sgraph);
  info.edge_lengths        = ComputeEdgeLengths (sgraph);
  if (info.number_of_crossings == 0) {
    info.angles = ComputeAngleInfo (dualgraph);
    info.faces  = ComputeFaceInfo (dualgraph);
  }

  remove_mydualnodeattrs(dualgraph);
  if(dualgraph)
    remove_graph(dualgraph);  

  return info;
}


void call_layout_info (Sgraph_proc_info info)
{
  Layout_info linfo;

  if (!layout_info_error_check (info->sgraph)) {
    return;
  }

  linfo = layout_info (info->sgraph);

  message ("Number of nodes           :\t%d\n",
	   linfo.number_of_nodes);
  message ("Number of edges           :\t%d\n",
	   linfo.number_of_edges);
  message ("Size of graph             :\t%d\n",
	   linfo.size);

  message ("Area used                :\t%d\n",
	   (int)linfo.area.used);
  message ("Area per node            :\t%d\n",
	   (int)linfo.density);
  message ("Area per node ratio      :\t%f\n",
	   (double)linfo.density / (double)linfo.area.used);

  message ("Node distances - shortest :\t%f\n",
	   linfo.node_distances.shortest);
  message ("Node distances - average  :\t%f\n",
	   linfo.node_distances.average);
  message ("Node distances - longest  :\t%f\n",
	   linfo.node_distances.longest);
  message ("Node distances - ratio    :\t%f\n",
	   (double)linfo.node_distances.longest /
	   (double)linfo.node_distances.shortest);
  message ("Node distances - deviation:\t%f\n",
	   sqrt(linfo.node_distances.variance));

  message ("Number of bends           :\t%d\n",
	   linfo.number_of_bends);
  message ("Number of crossings       :\t%d\n",
	   linfo.number_of_crossings);

  message ("Edge length - shortest    :\t%f\n",
	   linfo.edge_lengths.shortest);
  message ("Edge length - average     :\t%f\n",
	   linfo.edge_lengths.average);
  message ("Edge length - longest     :\t%f\n",
	   linfo.edge_lengths.longest);
  message ("Edge length - ratio       :\t%f\n",
	   (double)linfo.edge_lengths.longest /
	   (double)linfo.edge_lengths.shortest);
  message ("Edge length - deviation   :\t%f\n",
	   sqrt(linfo.edge_lengths.variance));

  if (linfo.number_of_crossings == 0) {

    message ("Angles - smallest         :\t%f\n",
	     linfo.angles.min);
    message ("Angles - average          :\t%f\n",	
	     linfo.angles.average);
    message ("Angles - longest          :\t%f\n",
	     linfo.angles.max);
    message ("Angles - ratio            :\t%f\n",
	     (double)linfo.angles.max / (double)linfo.angles.min);
    message ("Angles - deviation        :\t%f\n",
	     sqrt(linfo.angles.variance));

    message ("Faces - smallest          :\t%f\n",
	     linfo.faces.min);
    message ("Faces - average           :\t%f\n",	
	     linfo.faces.average);
    message ("Faces - largest           :\t%f\n",
	     linfo.faces.max);
    message ("Faces - ratio             :\t%f\n",
	     (double)linfo.faces.max / (double)linfo.faces.min);
    message ("Faces - deviation         :\t%f\n",
	     sqrt(linfo.faces.variance));
  }
}


void menu_layout_info (void)
{
  message ("-----------------------------------\n");
  call_sgraph_proc(call_layout_info, (char *)0);
}
