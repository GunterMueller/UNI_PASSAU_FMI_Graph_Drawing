/* This software is distributed under the Lesser General Public License */
//***********************************************************//
// edgeanchor.cpp                                            //
//                                                           //
// This file sets the edgeanchors, if the routing is         //
// straightline.                                             //
//                                                           //
//                                                           //
// Source: /home/br/CVS/graphlet/src/gt_algorithms/          //
//                 tree_layout/edgeanchor.cpp                //
// Author: Wetzel Sabine                                     //
//                                                           //
// (C) University of Passau 1995-1999, graphlet Project      //
//                                                           //
//***********************************************************//
// $Source: /home/br/CVS/graphlet/src/gt_tree_layout/edgeanchor.cpp,v $
// $Author: himsolt $
// $Revision: 1.6 $
// $Date: 1999/03/05 20:47:00 $
// $Locker:  $
// $State: Exp $

#include <gt_base/Graphlet.h>

#include <GTL/graph.h>

#include "tree_structure.h"
#include "permutation.h"
#include "edgeanchor.h"




//*************************************************************//
// predefinitions                                              //
//*************************************************************//
GT_Point compute_relative_edgeanchor(GT_Point anchor, double x, double y, double w, double h);


//*************************************************************//
// function: compute_edgeanchor                                //
// description: This function computes the edgeanchor for all  //
//       sons.                                                 //
//                                middle                       //
//                                 |                           //
//                                 |                           //
//                             X1->y               x           //
//            ---------------------Cy----------x----------     //
//           | father              | y     x              |    //
//           |                     |  yx                  |    //
//           |                 X2->x   y                  |    //
//           |                 x   |    y                 |    //
//            -------------A-------|----B----------------     //
//                     x           |      y                    //
//                 x               |       y                   //
//             x                   |        y                  //
//         x                                 y                //
//    --x---           --           ----------y-------------   //
//   |left  |         |  |         |right                   |  //
//    ------           --          |                        |  //
//                                  ------------------------   //
//                                                             //
//        A and B are the points determined by edge_connection.//
//        We are only allowed to place an edge in the region   //
//        between A and B.                                     //
//        C is the middle upper point of the father node.      //
//                                                             //
//        x is the line between the middle upper point of the  //
//        leftmost node and A.                                 //
//        y is the line between the middle upper point of the  //
//        rightmost node and B.                                //
//                                                             //
//        We have to determine the intersection points X1 and  //
//        X2, where X1 is the intersection of x and middle and //
//        X2 of y and middle.                                  //
//                                                            //
//        The x-coordinate of the edgeanchor is father_x +     //
//        father_w and the y-coordinate is the maximum ( 0 is  //
//        at the top of the window) of X1.y and X2.y,//
//        minimally C, maximally the south of the node.        //
//*************************************************************//
void GT_TreeAlgo::compute_edgeanchor(node father)
{
    GT_Graph* gt_graph;
    graph* l_graph;
	
    double father_x = 0.0;
    double father_y = 0.0;
    double father_w = 0.0;
    double father_h = 0.0;
    double left_x = 0.0;
    double left_y = 0.0;
    double left_w = 0.0;
    double left_h = 0.0;
    double right_x = 0.0;
    double right_y = 0.0;
    double right_w = 0.0;
    double right_h = 0.0;
    node left;
    node right;
    edge e;
    list<node> sons;
    int nr_of_sons = 0;
    double connect;

    gt_graph = this->get_graph();
    l_graph = gt_graph->attached();

    tree_forall_sorted_out_edges(e, father, this){
	sons.push_back(father.opposite(e));
	++nr_of_sons;
    }

    if(nr_of_sons == 0){
	return;
    }

//     cout << "***************" << endl;
//     cout << "Vaterknoten" << gt_graph->gt(father).label() << endl;
//     cout << "***************" << endl;
	
    father_x = this->get_node_x_coord(father);
    father_y = this->get_node_y_coord(father);
    father_w = gt_graph->gt(father).graphics()->w();
    father_h = gt_graph->gt(father).graphics()->h();
//     cout << "father_x: " << father_x << endl;
//     cout << "father_y: " << father_y << endl;
//     cout << "father_w: " << father_w << endl;
//     cout << "father_h: " << father_h << endl;
	
    if(nr_of_sons == 1){
	GT_Point edgeanchor(0,1);
	this->get_node_info(father)->set_edgeanchor(edgeanchor);
	return;
    }


    left = sons.front();
    right = sons.back();

//     cout << "linkester Sohn" << gt_graph->gt(left).label() << endl;
//     cout << "rechtester Sohn" << gt_graph->gt(right).label() << endl;

    left_x = this->get_node_x_coord(left);
    left_y = this->get_node_y_coord(left);
    left_w = gt_graph->gt(left).graphics()->w();
    left_h = gt_graph->gt(left).graphics()->h();
    right_x = this->get_node_x_coord(right);
    right_y = this->get_node_y_coord(right);
    right_w = gt_graph->gt(right).graphics()->w();
    right_h = gt_graph->gt(right).graphics()->h();
//     cout << "left_x: " << left_x << endl;
//     cout << "left_y: " << left_y << endl;
//     cout << "left_w: " << left_w << endl;
//     cout << "left_h: " << left_h << endl;
//     cout << "right_x: " << right_x << endl;
//     cout << "right_y: " << right_y << endl;
//     cout << "right_w: " << right_w << endl;
//     cout << "right_h: " << right_h << endl;

    connect = this->get_edge_connection();

    GT_Point A(father_x + (father_w - connect*father_w)/2,
	father_y + father_h);
    GT_Point B(father_x + (father_w + connect*father_w)/2,
	father_y + father_h);
    GT_Point left_anchor(left_x + left_w/2, left_y);
    GT_Point right_anchor(right_x + right_w/2, right_y);

//     cout << "A.x: " << A.x() << endl;
//     cout << "A.y: " << A.y() << endl;
//     cout << "B.x: " << B.x() << endl;
//     cout << "B.y: " << B.y() << endl;
//     cout << "left_anchor.x: " << left_anchor.x() << endl;
//     cout << "left_anchor.y: " << left_anchor.y() << endl;
//     cout << "right_anchor.x: " << right_anchor.x() << endl;
//     cout << "right_anchor.y: " << right_anchor.y() << endl;

    if(left_anchor.x() >= A.x()
	&& right_anchor.x() <= B.x()){
	GT_Point edgeanchor(0,-1);
	this->get_node_info(father)->set_edgeanchor(edgeanchor);
	return;
    }	    

    GT_Point middle_begin(father_x + father_w/2, father_y);
    GT_Point middle_end(father_x + father_w/2, father_y + father_h);

    GT_Line left_anchor_A(left_anchor, A);
    GT_Line right_anchor_B(right_anchor, B);
    GT_Line middle(middle_begin, middle_end);

    GT_Point X1 = get_intersection(left_anchor_A, middle);
    GT_Point X2 = get_intersection(right_anchor_B, middle);
//     cout << "X1.x: " << X1.x() << endl;
//     cout << "X1.y: " << X1.y() << endl;
//     cout << "X2.x: " << X2.x() << endl;
//     cout << "X2.y: " << X2.y() << endl;

    //north: if X1 and X2 are placed over the father_node
    //       or if X1 and X2 are under the sons
    if(X1.y() <= father_y && X2.y() <= father_y ||
	X1.y() >= left_y && X2.y() >= right_y){
//       cout << "1. Fall" << endl;
	GT_Point edgeanchor(0,-1);
	this->get_node_info(father)->set_edgeanchor(edgeanchor);
    }
    else{
	//south: if both (X1 and X2) are placed between father
	//       and sons
	if(X1.y() >= father_y + father_h
	    && X2.y() >= father_y + father_h){
//       cout << "2. Fall" << endl;
	    GT_Point edgeanchor(0,1);
	    this->get_node_info(father)->set_edgeanchor(edgeanchor);
	}
	else{
	    //if the left intersection point is not interesting
	    if(X1.y() >= left_y){
//       cout << "3. Fall" << endl;
		GT_Point edgeanchor(father_x + father_w/2, X2.y());
		edgeanchor = compute_relative_edgeanchor(edgeanchor,
		    father_x, father_y, father_w, father_h);
		this->get_node_info(father)->set_edgeanchor(
		    edgeanchor);
	    }
	    else{
		//if the right intersection point is not interesting
		if(X2.y() >= right_y){
//       cout << "4. Fall" << endl;
		    GT_Point edgeanchor(father_x + father_w/2, X1.y());
		    edgeanchor = compute_relative_edgeanchor(edgeanchor,
			father_x, father_y, father_w, father_h);
		    this->get_node_info(father)->set_edgeanchor(
			edgeanchor);
		}
		//both are inside the rectangle
		else{
//       cout << "5. Fall" << endl;

		    GT_Point edgeanchor(father_x + father_w/2,
			X1.y() > X2.y() ? X1.y() : X2.y());
// 		    cout << "edgeanchor: " << edgeanchor.x() << ", " << edgeanchor.y() << endl;
		    edgeanchor = compute_relative_edgeanchor(edgeanchor,
			father_x, father_y, father_w, father_h);
// 		    cout << "relativer edgeanchor: " << edgeanchor.x() << ", " << edgeanchor.y() << endl;
		    this->get_node_info(father)->set_edgeanchor(edgeanchor);
		}
	    }	
	}
    }
}

//*************************************************************//
// function: compute_relative_edgeanchor                       //
// description: Ghis function transforms an absolute edgeanchor//
//              into a relative edgeanchor.                    //
//*************************************************************//
GT_Point compute_relative_edgeanchor(GT_Point anchor, double x, double y, double w, double h)
{
    return GT_Point(
        2.0*(anchor.x() - (x+w/2)) / w,
        2.0*(anchor.y() - (y+h/2)) / h);
}

//*************************************************************//
// function: get_intersection                                  //
// description: This function determines the intersection      //
//              point between two lines.                       //
//*************************************************************//
GT_Point get_intersection(GT_Line l1, GT_Line l2)
{
    GT_Point p;
    
    l1.intersection(l2, p);
    return p;
}
