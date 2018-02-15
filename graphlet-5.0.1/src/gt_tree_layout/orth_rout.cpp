/* This software is distributed under the Lesser General Public License */
//***********************************************************//
// orth_rout.cpp                                             //
//                                                           //
// This file implements the orthogonal routing between the   //
// father und his sons.                                      //
//                                                           //
//                                                           //
// Source: /home/br/CVS/graphlet/src/gt_algorithms/          //
//                 tree_layout/orth_rout.cpp                 //
// Author: Wetzel Sabine                                     //
//                                                           //
// (C) University of Passau 1995-1999, graphlet Project      //
//                                                           //
//***********************************************************//
// $Source: /home/br/CVS/graphlet/src/gt_tree_layout/orth_rout.cpp,v $
// $Author: himsolt $
// $Revision: 1.5 $
// $Date: 1999/03/05 20:47:14 $
// $Locker:  $
// $State: Exp $

#include <gt_base/Graphlet.h>

#include <GTL/graph.h>
#include "tree_structure.h"
#include "permutation.h"


//*************************************************************//
// predefinitions                                              //
//*************************************************************//


//*************************************************************//
// function: orthogonal_routing                                //
// description: For routing orthogonal, we determine the       //
//      polyline (GT_Polyline) for every edge in the tree.     //
//      Therefore, we have to insert 2 points (x) in every     //
//      list of GT_Points (=GT_Polyline), e.g.:                //
//                                                             //
//                ------                                       //
//               |      |                                      //
//                ------                                       //
//                 |                                           //
//       x1  x-----x  x2                                       //
//           |         ...                                     //
//           -                                                 //
//          | |                                                //
//           -                                                 //
//      Note, that the edgeanchors are set automatically in    //
//      the function compute_edgeanchor by the function        //
//      connect_orthogonal of the NEI.                         //
//                                                             //
//*************************************************************//
void GT_TreeAlgo::orthogonal_routing(node father)
{
    GT_Graph* gt_graph;
    graph* l_graph;
    int nr_of_sons = 0;
    double father_height = 0.0;
    double father_width = 0.0;
    double father_x = 0.0;
    double father_y = 0.0;
    double chan_begin = 0.0;
    double chan_dist = 0.0;
    double son_width = 0.0;
    double son_x = 0.0;
    double connect = 0.0;
    double free_width = 0.0;
    double rest_width = 0.0;
    double diff = 0.0;
    node son;
    edge e;
    int i = 0;
    double x_x1 = 0.0;
    double y_x1 = 0.0;
    double x_x2 = 0.0;
    double y_x2 = 0.0;
    GT_Point bend_x1;
    GT_Point bend_x2;
    GT_Point tmpPoint;
    GT_Polyline line;

    gt_graph = this->get_graph();
    l_graph = gt_graph->attached();

    nr_of_sons = this->get_order(father).size();

    //if a node has no sons or only one, we can return
    if(nr_of_sons <= 1){
	return;
    }
	
    father_height = gt_graph->gt(father).graphics()->h();
    father_width = gt_graph->gt(father).graphics()->w();
    father_x = this->get_node_x_coord(father);
    father_y = this->get_node_y_coord(father);
	
    chan_begin = this->get_vert_node_node_dist()/2 - 1;
    chan_dist = this->get_channel_dist();
    connect = this->get_edge_connection();

    // free_width: part of the father node where no edges start
    // rest_width: the part of the node where edges can start
    // if connect = 0, all edges fall onto each other
    if(connect == 0.0){
	free_width = father_width/2;
    }
    else{
	free_width = father_width * (1.0 - connect)/2;
    }
    rest_width = father_width - 2*free_width;

    //diff is the difference between 2 outgoing edges (in x-direction)
    if(connect == 0){
	diff = 0;
    }
    else{
	diff = rest_width/(double)(nr_of_sons - 1);
    }

    tree_forall_sorted_out_edges(e, father, this){
	son = father.opposite(e);
	son_x = this->get_node_x_coord(son);
	son_width = gt_graph->gt(son).graphics()->w();

	x_x1 = son_x + son_width/2;
	//the rightmost and the leftmost son-edges are in one
	//channel
	if(son_x + son_width/2 > father_x + father_width/2){
	    y_x1 = father_y + father_height + chan_begin +
		(nr_of_sons - i - 1)*chan_dist;
	}
	else{
	    y_x1 = father_y + father_height + chan_begin + i*chan_dist;
	}
	x_x2 = father_x + free_width + i*diff;
	y_x2 = y_x1;

	// raitner:
	// bend_x1 und bend_x2 aren't pointers any more, because they are only used 
	// temporary as arguments to the insert procedure of list<GT_point> below.
	// bend_x1 = new GT_Point();
	// bend_x2 = new GT_Point();

	bend_x1.x(x_x1);
	bend_x1.y(y_x1);
	bend_x2.x(x_x2);
	bend_x2.y(y_x2);

	line = gt_graph->gt(e).graphics()->line();
	//insert x1 first and than x2 (always at the second position)
	//->after the first element
	
	// raitner:
	// this is only the long form of: line.insert(bend_x1, line.first(), after);
	// but we dont have this insert in GTL.

	if(e.source() == father){
	  tmpPoint = line.front ();
	  line.pop_front ();
	  line.push_front (bend_x1);
	  line.push_front (bend_x2);
	  line.push_front (tmpPoint);
	} else {
	  tmpPoint = line.front ();
	  line.pop_front ();
	  line.push_front (bend_x2);
	  line.push_front (bend_x1);
	  line.push_front (tmpPoint);
	}

	gt_graph->gt(e).graphics()->line(line);

	i = i+1;
    }
}
