/* This software is distributed under the Lesser General Public License */
//***********************************************************//
// father_place.cpp                                          //
//                                                           //
// This file determines the x-coordinate of the father node. //
//                                                           //
//                                                           //
// Source: /home/br/CVS/graphlet/src/gt_algorithms/          //
//                 tree_layout/father_place.cpp              //
// Author: Wetzel Sabine                                     //
//                                                           //
// (C) University of Passau 1995-1999, graphlet Project      //
//                                                           //
//***********************************************************//
// $Source: /home/br/CVS/graphlet/src/gt_tree_layout/father_place.cpp,v $
// $Author: himsolt $
// $Revision: 1.5 $
// $Date: 1999/03/05 20:47:03 $
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
// function: father_placement                                  //
// description: there are 3 different (user defined) possi-    //
//        bilities for the father placement:                   //
//        - center: the father is placed in the middle between //
//                  the leftmost and the rightmost son         //
//        - baricenter: the x-coordinates of every son is      //
//             added; this sum is divided by the number of     //
//             sons                                            //
//        - median: If we have an odd number of suns, the      //
//             father is placed over the son in the middle,    //
//             otherwise it is placed between the two middle   //
//             sons                                            //
//        Note that the function father_placement only changes //
//        the x-coordinate of the father.                      //
//*************************************************************//
void GT_TreeAlgo::father_placement(node father)
{
    GT_Graph* gt_graph;
    graph* l_graph;
    edge e;
    list<node> sons;
    node son;
    node first;
    node last;
    double father_width = 0.0;
    double son_x = 0.0;
    double son_width = 0.0;
    double first_x = 0.0;
    double last_x = 0.0;
    double last_width = 0.0;
    double width = 0.0;
    double sum = 0.0;
    double number = 0.0;

    int nr_of_sons = 0;
    node son1;
    node son2;
    int i = 0;
    double son1_x = 0.0;
    double son1_width = 0.0;
    double son2_x = 0.0;
    double son2_width = 0.0;


    gt_graph = this->get_graph();
    l_graph = gt_graph->attached();

    //if the father-node is a leaf, we don't need a father-placement
    if(this->get_order(father).empty() == true){
	return;
    }

    tree_forall_sorted_out_edges(e, father,this){
	sons.push_back(father.opposite(e));
	++nr_of_sons;
    }

    father_width = gt_graph->gt(father).graphics()->w();
	
    //if father has only one son, we place him at the same position
    //as the son

    if(nr_of_sons == 1){
	son = sons.front ();
	son_x = this->get_node_x_coord(son);
	son_width = gt_graph->gt(son).graphics()->w();
	this->set_node_x_coord(father, son_x + son_width/2 - father_width/2);
	return;
    }

    list<node>::iterator it;
    list<node>::iterator end;
    
    switch(this->get_father_place()){
	// placement = 0 -> center
	case Center:
	    first = sons.front();
	    last = sons.back();
	    first_x = this->get_node_x_coord(first);
	    last_width = gt_graph->gt(last).graphics()->w();
	    last_x = this->get_node_x_coord(last)
		+ last_width;
	    this->set_node_x_coord(father, ((first_x + last_x -father_width)/2));
	    break;
	    // placement = 1 -> baricenter
	case Barycenter:
	    end = sons.end ();

	    for (it = sons.begin (); it != end; ++it) {
		son = *it;
		width = gt_graph->gt(son).graphics()->w();
		sum = sum + this->get_node_x_coord(son) + width/2;
		number = number + 1;
	    }

	    this->set_node_x_coord(father, (sum/number - father_width/2));
	    break;
	case Median:
	    son1 = sons.front ();
	    sons.pop_front ();
	    son2 = sons.front ();
	    sons.pop_front ();
	    
	    for(i=2; i<=int(nr_of_sons/2); i++){
		son1 = son2;
		son2 = sons.front ();
		sons.pop_front ();
	    }

	    son1_x = this->get_node_x_coord(son1);
	    son1_width = gt_graph->gt(son1).graphics()->w();
	    son2_x = this->get_node_x_coord(son2);
	    son2_width = gt_graph->gt(son2).graphics()->w();
		
	    if(nr_of_sons % 2 == 1){
		//son1 contains the middle son
		this->set_node_x_coord(father,
		    son2_x + son2_width/2 - father_width/2);
	    }
	    else{
		//place the father in the middle of son1 and son2
		this->set_node_x_coord(father,
		    (son1_x+son1_width/2+son2_x+son2_width/2)/2
		    - father_width/2);
	    }
	    break;
    }
}
