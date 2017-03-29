/* This software is distributed under the Lesser General Public License */
//***********************************************************//
// shift.cpp                                                 //
//                                                           //
// This file implements the algorithm for shifting subtrees. //
// The son-subtrees are arranged over each other and then    //
// shifted in x-direction.                                   //
//                                                           //
//                                                           //
// Source: /home/br/CVS/graphlet/src/gt_algorithms/          //
//                 tree_layout/shift.cpp                     //
// Author: Wetzel Sabine                                     //
//                                                           //
// (C) University of Passau 1995-1999, graphlet Project      //
//                                                           //
//***********************************************************//
// $Source: /home/br/CVS/graphlet/src/gt_tree_layout/shift.cpp,v $
// $Author: himsolt $
// $Revision: 1.7 $
// $Date: 1999/03/05 20:47:19 $
// $Locker:  $
// $State: Exp $

#include <gt_base/Graphlet.h>

#include <GTL/graph.h>
#include <math.h>
#include "tree_structure.h"
#include "shift.h"
#include "permutation.h"

//**************************************************************//
// predefinitions                                               //
//**************************************************************//

Contour* update_right_contour(GT_TreeAlgo* treealgo, list<node>& sons);
double determine_offset(GT_TreeAlgo* treealgo, Contour* right,
    Contour* left);
double vertical_shift(GT_TreeAlgo* treealgo, double offset, Contour* right, Contour* left);

double absolute(double x);


//**************************************************************//
// function: shift_subtree                                      //
// description: This function shifts the subtrees of a father   //
//      in x-direction.                                         //
//      All sons of the father are stored in a list. While      //
//      running through this list, we always get the left       //
//      contour of a subtree. The right contour is updated      //
//      after each step.                                        //
//                                                              //
//      The delta_x with which we have to shift the "right sons"//
//      is determined by the function determine_offset. We      //
//      compare the updated right contour of the left son with  //
//      the left contour of the right son (this is the son      //
//      which should be shifted). For more information see      //
//      the description of "determine_offset".                  //
//      (SABINE:)                                               //
//      Note that we have to update the contour of the          //
//      subtrees, the x-coordinate and the edgeanchors of the   //
//      shifted subtree. This is a big difference to the        //
//      TR-algorithm which is the basic of this algorithm.      //
//**************************************************************//

void GT_TreeAlgo::shift_subtrees(node father)
{
    GT_Graph* gt_graph;
    graph* l_graph;
    edge e;
    node son;
    node first;
    list<node> sons;
    //the sons for the update_right_contour-function
    list<node> worn_out_sons;
    Contour* left_contour = 0;
    Contour* right_contour = 0;
    double offset = 0.0;

    gt_graph = this->get_graph();
    l_graph = gt_graph->attached();

    //we don't have to shift subtrees, if father is a leaf
    if(this->get_order(father).empty() == true){
	return;
    }
	
    //compute a list of all son-trees of father

    tree_forall_sorted_out_edges(e, father, this){
	sons.push_back (father.opposite(e));
    }

    //every son is pushed in the list worn_out_sons, this means the
    //sons are twisted in the list (because we have to compute the
    //right contour)
    first = sons.front ();
    sons.pop_front ();
    worn_out_sons.push_front (first);
    right_contour = this->get_node_info(first)->get_right_contour();

    list<node>::iterator it;
    list<node>::iterator end = sons.end ();
   
    for (it = sons.begin (); it != end; ++it) {
	son = *it;
	left_contour = this->get_node_info(son)->get_left_contour();
//    		cout << endl << "---------------------------------------" << endl;
//    		cout << "- Verschiebung von Knoten: " << gt_graph->gt(son).label() << endl;
//    		cout << "rechte Kontour: " << endl;
//    		right_contour->print_contour();
//    		cout << endl;
//    		cout << "linke Kontour: " << endl;
//    		left_contour->print_contour();
//    		cout << endl;
	offset = determine_offset(this, right_contour, left_contour);
//     		cout << "insgesamte Verschiebung: " << offset << endl;	
	//SABINE: versuche diese Funktion zu vermeiden, da sie die
	//Laufzeit des Algorithmus zerstoert!
	this->update_x_coords(son, offset);
	if(this->get_routing() == Orthogonal){
	    this->update_edges_to_sons(son, offset);
	}

	//update contours of this son
	this->get_node_info(son)->get_left_contour()->update_contour(offset);
	this->get_node_info(son)->get_right_contour()->update_contour(offset);
//    		cout << "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%: " << endl;
//    		cout << "neue rechte Kontour: " << endl;
//    		this->get_node_info(son)->get_right_contour()->print_contour();
//    		cout << endl;
//    		cout << "neue linke Kontour: " << endl;
//    		this->get_node_info(son)->get_left_contour()->print_contour();
//    		cout << endl;
//    		cout << "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%: " << endl;
	worn_out_sons.push_front (son);
	right_contour = update_right_contour(this, worn_out_sons);
//    		cout << "rechte Kontour nach Update: " << endl;
//    		right_contour->print_contour();
//    		cout << endl;
//    		cout << "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%: " << endl;
    }
}


//*************************************************************//
// function: determine offset                                  //
// description: In the first step, we successively run through //
//       two adjacent contours. During this step, we determine //
//       the value with which we have to shift the right son.  //
//                                                             //
//       Note that "left" is the left contour of the right     //
//       son and "right" is the right contour of the left son. //
//                                                             //
//                     |                                       //
//             ----x   |       y--                             //
//            |    |   |       |  |                            //
//            |    |   |       yy-                             //
//             --x-x   |       /  \                            //
//               |     |   y---   --                           //
//              --x    |   |   | |  |                          //
//             |  |    |   y---   --                           //
//              --x    |                                       //
//                     |                                       //
//                                                             //
//       Note that the two subtrees are first of all lying     //
//       over each other. The right contour is named by x,     //
//       the left by y.                                        //
//       Here is a smaller example for the concrete algo:      //
//                                                             //
//               ----x x1                                      //
//      oooooooo|oooo|oooooooy--ooooooooooooo                  //
//              |    |      |   |                              //
//               ----x       ---                               //
//                                                             //
//       We determine the intersection between the o-line      //
//       and the line xx. Note that we only have to look at    //
//       the first x (x1). The upper x is the con of x1, the   //
//       corresponding x is saved in cor of x1. After the      //
//       computation of "intersection", we determine the       //
//       new offset by a simple algebraical computation.       //
//       This offset-determination is done for every ConPoint  //
//       in both contours (so we do not only compare from      //
//       the left contour to the right, but also vice versa).  //
//                                                             //
//       The delta_x with which we have to shift the second    //
//       subtree is the maximum of all new_offsets.            //
//                                                             //
//       There is also a second offset determination step. As  //
//       we only look at the x-coordinates up to now, it is    //
//       possible, that the nodes are placed within a (very)   //
//       small horizontal node-node-distance, f.e.:            //
//          ...         --                                     //
//              \      |  |                                    //
//              ---     --                                     //
//             |   |    /    ...                               //
//              -x-o   o                                       //
//               x----------                                   //
//              |           |                                  //
//               -----------                                   //
//                                                             //
//       The horizontal distance (between the two points x)    //
//       is very small in this example, but we don't need to   //
//       shift the second subree because the vertical distance //
//       (between the two points o) is big enough (more than   //
//       node-node-distance). In this second step, we ensure,  //
//       that the horizontal node-node-distance is observed.   //
//       Therefore we determine the distance (dist) between    //
//       the two actual ConPoints (left_elem, right_elem) by   //
//       Pythagoras: dist^2 = (x2-x1)^2 + (y2-y1)^2 where:     //
//                                                             //
//                         -----                               //
//                        |     |                              //
//                 (x2,y2)o-----                               //
//                       /                                     //
//                      /           where left_elem = (x2,y2)  //
//                     / dist             right_elem = (x1,y1) //
//                    /                                        //
//              -----o(x1, y1)                                 //
//             |     |                                         //
//              -----                                          //
//       If the distance is smaller than node-node, we have to //
//       add an offset sec_off which is given by:              //
//                       -------------------------             //
//           sec_off = -/ node-node^2 - (y2-y1)^2              //
//                                                             //
//       This second step is implemented in vertical_shift.    //
//*************************************************************//
double determine_offset(GT_TreeAlgo* treealgo, Contour* right, Contour* left)
{
    double hor_node_node_dist = 0.0;
    double node_edge_dist = 0.0;
    double quota = 0.0;
    
    double offset = 0.0;
    bool offset_initialized = 0;
    double sec_off = 0.0;
    double new_offset = 0.0;
    double left_con_y = 0.0;
    double left_cor_y = 0.0;
    double right_con_y = 0.0;
    double right_cor_y = 0.0;
    double succ_left_con_y = 0.0;
    int length_left = 0;
    int length_right = 0;
    ConPoint* left_elem;
    ConPoint* right_elem;

    hor_node_node_dist = treealgo->get_hor_node_node_dist();
    node_edge_dist = treealgo->get_node_edge_dist();

    const list<ConPoint*>& llist = left->get_contour ();
    const list<ConPoint*>& rlist = right->get_contour ();
    length_left = llist.size();
    length_right = rlist.size();
    
    list<ConPoint*>::const_iterator left_it = llist.begin ();
    list<ConPoint*>::const_iterator left_end = llist.end ();
    list<ConPoint*>::const_iterator right_it = rlist.begin ();
    list<ConPoint*>::const_iterator right_end = rlist.end ();
    right_elem = *right_it;
    left_elem = *left_it;

    if (length_left > 1) {
	++left_it;
    }

    while((*left_it)->get_con_y() < right_elem->get_con_y()) {
	left_elem = *left_it;
	++left_it;

	if (left_it == left_end) {
	    left_it = llist.begin ();
	}

	--length_left;
    }

    if (length_right > 1) {
	++right_it;
    }

    while((*right_it)->get_con_y() < left_elem->get_con_y()) {
	right_elem = *right_it;
	++right_it;

	if (right_it == right_end) {
	    right_it = rlist.begin ();
	}

	--length_right;
    }

    //determine offset
    while((length_left > 0) && (length_right > 0)){
	//determine the "should be" -distance

	if(left_elem->get_point_type() == Edge ||
	    left_elem->get_point_type() == Bend ||
	    right_elem->get_point_type() == Edge ||
	    right_elem->get_point_type() == Bend){
	    quota = node_edge_dist * hor_node_node_dist;
	}
	else{
	    quota = hor_node_node_dist;
	}


	left_con_y = left_elem->get_con_y();
	left_cor_y = left_elem->get_cor_y();
	right_con_y = right_elem->get_con_y();
	right_cor_y = right_elem->get_cor_y();

	if(left_con_y >= right_con_y && left_con_y <= right_cor_y){
	  // if the y-coordinate of the left point is inside the interval [right_con_y, right_cor_y]
	  // we have to compare the point of the left contour with the straight line of the right contour
	    GT_Point intersection = right->get_intersection_point(
		left_elem->get_con_point(), *right_elem);

//   	    cout << "intersection.x:" << intersection.x() << endl;
//   	    cout << "intersection.y:" << intersection.y() << endl;
	    
	    new_offset = intersection.x() + quota
		- left_elem->get_con_x();

//   	    cout << "* neuer offset: " << new_offset << endl;

	    if(offset_initialized == 0){
		offset = new_offset;
		offset_initialized = 1;
	    }
	    if((new_offset > offset) && (offset_initialized == 1)){
		offset = new_offset;
	    }
	} else if (right_con_y >= left_con_y && right_con_y <= left_cor_y){
	  // if the y-coordinate of the right point is inside the interval [left_con_y, left_cor_y]
	  // we have to compare the point of the right contour with the straight line of the left contour
	  GT_Point intersection = left->get_intersection_point(
		right_elem->get_con_point(), *left_elem);
	    
//   	    cout << "intersection.x:" << intersection.x() << endl;
//   	    cout << "intersection.y:" << intersection.y() << endl;
	    
	    new_offset = right_elem->get_con_x() + quota
		- intersection.x();
	    
//   	    cout << "* neuer offset: " << new_offset << endl;

	    if(offset_initialized == 0){
		offset = new_offset;
		offset_initialized = 1;
	    }
	    if((new_offset > offset) && (offset_initialized == 1)){
		offset = new_offset;
	    }		
	    // if none of these two cases is valid: we have reached the end of one of the contours
	    // and there is no need to shift any more
	}



	// Which contour do we have to propagate? Left side if the the y-coordinate of the 
	// successor in the left contour is in the interval [right_con_y, right_cor_y].
	// If this is not the case, we always propagate the right contour. At this point
	// a cyclic propagation is important.
	succ_left_con_y = (*left_it)->get_con_y();

	if((succ_left_con_y >= right_con_y) && (succ_left_con_y <= right_cor_y)){
	    left_elem = *left_it;
	    ++left_it;
	    --length_left;

	    if (left_it == left_end) {
		left_it = llist.begin ();
 	    }
	} else{
	    right_elem = *right_it;
	    ++right_it;
	    --length_right;

	    if (right_it == right_end) {
		right_it = rlist.begin ();
	    }
	    
	}
    }

    sec_off = vertical_shift(treealgo, offset, right, left); 
//    	cout << "* 2. offset: " << sec_off << endl;

    return offset + sec_off;
}

//*************************************************************//
// function: vertical_shift                                    //
// description: See description of determine_offset-function   //
//       (second step).                                        //
//*************************************************************//
double vertical_shift(GT_TreeAlgo* treealgo, double offset, Contour* right, Contour* left)
{
    double hor_node_node_dist = 0.0;
    double ver_node_node_dist = 0.0;
    double node_edge_dist = 0.0;
    double quota = 0.0;
    double sec_off = 0.0;
    list<ConPoint*> right_list;
    list<ConPoint*> left_list;
    ConPoint* right_elem;
    ConPoint* left_elem;
    double left_x = 0.0;
    double right_x = 0.0;
    double left_y = 0.0;
    double right_y = 0.0;
    int left_type = 0;
    int right_type = 0;
    //is the quadrat of the dist explained in the header
    double dist = 0.0;
    double new_offset = 0.0;
    double quota_square = 0.0;
    //for inaccuracy of doubles
    double epsilon = 0.001;


    hor_node_node_dist = treealgo->get_hor_node_node_dist();
    ver_node_node_dist = treealgo->get_vert_node_node_dist();
    node_edge_dist = treealgo->get_node_edge_dist();
    
    right_list = right->get_contour();
    left_list = left->get_contour();

    list<ConPoint*>::iterator left_it;
    list<ConPoint*>::iterator left_end = left_list.end ();
    list<ConPoint*>::iterator right_it;
    list<ConPoint*>::iterator right_end = right_list.end ();

    for (right_it = right_list.begin (); right_it != right_end; ++right_it) {
	right_elem = *right_it;

	for (left_it = left_list.begin (); left_it != left_end; ++left_it) {
	    left_elem = *left_it;
	    left_x = left_elem->get_con_x() + offset;
	    right_x = right_elem->get_con_x();
	    left_y = left_elem->get_con_y();
	    right_y = right_elem->get_con_y();

	    if(absolute(left_y - right_y) <= 2*ver_node_node_dist){
		left_type = left_elem->get_point_type();
		right_type = right_elem->get_point_type();
		dist = square(left_x - right_x) + square(left_y - right_y);

		if(left_type == Bend || left_type == Edge ||
		    right_type == Bend || right_type == Edge){
		    quota = node_edge_dist * hor_node_node_dist;
		}
		else{
		    quota = hor_node_node_dist;
		}

// 	    cout << endl << "Vergleich der Knoten (" << right_x << ", " << right_y << ") und (" << left_x << ", " << left_y << ": " << endl << "* Soll: " << quota << endl << "* Ist: "<< sqrt(dist) << endl;
	    
		quota_square = square(quota);
		if(dist < quota_square - epsilon ||
		    ((left_x < right_x) && 
			(absolute(left_y - right_y) < quota))){
//  		cout << "*** Addieren eines offsets ***" << endl;
		    new_offset = sqrt(quota_square -
			square(left_y - right_y)) + right_x - left_x;
		    if(sec_off < new_offset){
			sec_off = new_offset;
		    }
		}
	    }
	}
    }
    
    return sec_off;
}


//*************************************************************//
// function: update_right_contour                              //
// description: This function computes the new right contour   //
//       Therefore, we take the right contour of the rightmost //
//       son and determine its contour. This contour is the    //
//       new contour + the rest of the right contour of the    //
//       second rightmost son. The rightmost son is the son    //
//       which was shifted by the last step. E.g.:             //
//                                                             //
//               father                                        //
//               -----                                         //
//              |     |                                        //
//               -----                                         //
//                 |       \                                   //
//                --           ---                             //
//               |  |         |   |   <- just shifted          //
//      ...      |  |          ---                             //
//               |  |         rightmost                        //
//                --                                           //
//             second                                          //
//             rightmost                                       //
//                                                             //
//                                                             //
//        Note that the second rightmost son has already       //
//        the complete right contour, this means that if the   //
//        sons to the left of the second rightmost son are     //
//        "deep'er" than the second rightmost son, the contour //
//        was already updated in the previous step, for example//
//                                                             //
//               -----                                         //
//              |     |                                        //
//               -----                                         //
//             /   |       \                                   //
//        --      --o          ---n                            //
//       |  |    |  o         |   n   <- just shifted          //
//        --     |  o          ---n                            //
//         |     |  on        rightmost                        //
//       ---o     --on                                         //
//      |   on                                                 //
//       ---on                                                 //
//                                                             //
//         The right contour of the second rightmost son is    //
//         denoted by o, the new contour by n.                 //
//                                                             //
//         Therefore, the we only have to copy the right       //
//         contour of the rightest most son and add some       //
//         new points above for the right contour of the       //
//         father node.                                        //
//                                                             //
//*************************************************************//
Contour* update_right_contour(GT_TreeAlgo* treealgo, list<node>& sons)
{
    Contour* right = 0;
    Contour* son_right = 0;
    Contour* rest = 0;
    node right_most;
    node son;
    double depth = 0.0;
    
    right_most = sons.front ();
    depth = treealgo->get_deepest_y(right_most);
    right = treealgo->get_node_info(right_most)->get_right_contour();

    son = *(++sons.begin());
    son_right = treealgo->get_node_info(son)->get_right_contour();
    
    if (son_right->get_contour().back()->get_con_y() > depth) {
	rest = treealgo->get_node_info(son)->get_right_contour()
	    ->get_rest_after_y(treealgo, depth);
	right->append_contour(rest);
    }
    
    return right;
}


//*************************************************************//
// function: square                                            //
// description: Computes x^2.                                  //
//*************************************************************//
double square(double x)
{
    return x*x;
}

//*************************************************************//
// function: absolute                                          //
// description: Computes |x|.                                  //
//*************************************************************//
double absolute(double x)
{
    if(x < 0){
	return -x;
    }
    else{
	return x;
    }
}
