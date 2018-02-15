/* This software is distributed under the Lesser General Public License */
//***********************************************************//
// contour.cpp                                               //
//                                                           //
// This file implements all algorithms for the contour.      //
//                                                           //
//                                                           //
// Source: /home/br/CVS/graphlet/src/gt_algorithms/          //
//                 tree_layout/contour.cpp                   //
// Author: Wetzel Sabine                                     //
//                                                           //
// (C) University of Passau 1995-1999, graphlet Project      //
//                                                           //
//***********************************************************//
// $Source: /home/br/CVS/graphlet/src/gt_tree_layout/contour.cpp,v $
// $Author: himsolt $
// $Revision: 1.7 $
// $Date: 1999/03/05 20:46:58 $
// $Locker:  $
// $State: Exp $
#include <gt_base/Graphlet.h>

#include "tree_structure.h"
#include "permutation.h"

//*************************************************************//
// predefinitions                                              //
//*************************************************************//

Contour* compute_left_contour(GT_TreeAlgo* treealgo, node father, list<node>& sons);
Contour* compute_right_contour(GT_TreeAlgo* treealgo, node father, node right_most);

//*************************************************************//
// function: initialize_contour                                //
// description: This function initializes the contour for a    //
//              given leaf.                                    //
//              The contour points of a leaf are the following://
//                                                             //
//          x1-> x---o      x: contour points of the left      //
//               |   |         contour                         //
//               |   |      o: contour points of the right     //
//          x2-> x---o         contour                         //
//*************************************************************//
void GT_TreeAlgo::initialize_contour(node leaf)
{
    double height = 0.0;
    double width = 0.0;
	
    Contour* left = new Contour();
    Contour* right = new Contour();
    ConPoint* point_x1 = new ConPoint();
    ConPoint* point_x2 = new ConPoint();
    ConPoint* point_o1 = new ConPoint();
    ConPoint* point_o2 = new ConPoint();

    width = this->get_graph()->gt(leaf).graphics()->w();
    height = this->get_graph()->gt(leaf).graphics()->h();

    //build the left contour
    //lower point (x2)
    point_x2->set_con_point(0.0, this->get_node_y_coord(leaf) + height);
    point_x2->set_cor_point(0.0, this->get_node_y_coord(leaf) + height);
    point_x2->set_point_type(NodeEnd);
    left->push_contour_point(point_x2);
    this->insert_con_point(point_x2);
    //upper point (x1)
    point_x1->set_con_point(0.0, this->get_node_y_coord(leaf));
    point_x1->set_cor_point(0.0, this->get_node_y_coord(leaf) + height);
    point_x1->set_point_type(NodeBegin);
    left->push_contour_point(point_x1);
    this->insert_con_point(point_x1);
	
    this->get_node_info(leaf)->set_left_contour(left);
    this->insert_contour(left);

    //build the right contour
    //lower point (o2)
    point_o2->set_con_point(width,
	this->get_node_y_coord(leaf) + height);
    point_o2->set_cor_point(width,
	this->get_node_y_coord(leaf) + height);
    point_o2->set_point_type(NodeEnd);
    right->push_contour_point(point_o2);
    this->insert_con_point(point_o2);
	
    //upper point (o1)
    point_o1->set_con_point(width,
	this->get_node_y_coord(leaf));
    point_o1->set_cor_point(width,
	this->get_node_y_coord(leaf) + height);
    point_o1->set_point_type(NodeBegin);
    right->push_contour_point(point_o1);
    this->insert_con_point(point_o1);
	
    this->get_node_info(leaf)->set_right_contour(right);
    this->insert_contour(right);
//     //Testausgabe der Kontouren
//     cout << endl << "***************************************" << endl;
//     GT_Graph* gt_graph = this->get_graph();     
//     cout << "- Kontour von Knoten: " << gt_graph->gt(leaf).label() << endl;
//     cout << "rechte Kontour: " << endl;
//     right->print_contour();
//     cout << endl;
//     cout << "linke Kontour: " << endl;
//     left->print_contour();
//     cout << endl << "***************************************" << endl;
}

//*************************************************************//
// function: compute_contour                                   //
// description: This function computes the new contour of a    //
//       father node.                                          //
//       We assume, that the information of the node place-    //
//       ment is already computed, f.e. the x-coordinate       //
//       (as an offset) of the father node, ... .              //
//                                                             //
//                                                             //
//                                                             //
//                                                             //
//                                                             //
//                    l-----r                                  //
//                    |     |                                  //
//                    l-l/r-r                                  //
//                                                             //
//                 /      |    \                               //
//                                                             //
//             l          o        r                           //
//            l o        o o      o r                          //
//           l   o      ooooo    o   r                         //
//          loooooo             o     r                        //
//                             l       r                       //
//                            l         r                      //
//                           l           r                     //
//                          looooooooooooor                    //
//                                                             //
//       The new left and right contour is in this example     //
//       the points indicated by l and r. Note, that we        //
//       have to use parts of the old contours plus some       //
//       additional points for the father node.                //
//*************************************************************//
void GT_TreeAlgo::compute_contour(node father)
{
    Contour* left = 0;;
    Contour* right = 0;;
    graph* l_graph;
    node son;
    list<node> sons_left;
    TreeInfo* info;

    l_graph = this->get_graph()->attached();

    // We don't have to compute the contour for leafs. They are
    // already initialized.
    if(this->get_order(father).empty() == true){
	return;
    }

    //iterate over all edges to the sons
//     list<edge>::const_iterator it, end; 

    //transform the edge list to the list of all nodes
//     for (it = father.out_edges_begin(), end = father.out_edges_end();
// 	 it != end; ++it)
//     {
// 	son = (*it).target();
// 	sons_left.push_back(son);
//     }

    edge e;
    tree_forall_sorted_out_edges(e, father, this){
      son = father.opposite(e);
      sons_left.push_back(son);
    }
	
    info = this->get_node_info(father);
	
//     cout << "vor compute_left_contour" << endl;
    left = compute_left_contour(this, father, sons_left);
//     cout << "nach compute_left_contour" << endl;
    info->set_left_contour(left);

    //son contains the rightmost son of father
//     cout << "vor compute_right_contour" << endl;
    right = compute_right_contour(this, father, son);
//     cout << "vor compute_right_contour" << endl;
    info->set_right_contour(right);

//     //Testausgabe der Kontouren
//     cout << endl << "***************************************" << endl;
//     GT_Graph* gt_graph = this->get_graph();     
//     cout << "- Kontour von Knoten: " << gt_graph->gt(son).label() << endl;
//     cout << "rechte Kontour: " << endl;
//     right->print_contour();
//     cout << endl;
//     cout << "linke Kontour: " << endl;
//     left->print_contour();
//     cout << endl << "***************************************" << endl;
}


//*************************************************************//
// function: compute_left_contour                              //
// description: This function computes the new left contour    //
//       of the tree beginning at the node father. Therefore,  //
//       we have to go through all sons of father and          //
//       determine whether this subtree is longer then the     //
//       existing ones. If this is true, a part of the contour //
//       of this subtree is appended to the contour. f.e.:     //
//                       x----------                           //
//                       x          |         x: left contour  //
//                       x----------                           //
//                      x      |     \                         //
//                     x\     / \     /\                       //
//                    x  \   /   \    --                       //
//                   x----  /     \                            //
//                         x       \                           //
//                        x         \                          //
//                       x           \                         //
//                      x-------------                         //
//                                                             //
//*************************************************************//

Contour* compute_left_contour(GT_TreeAlgo* treealgo, node father, list<node>& sons)
{
    Contour* left = 0;
    Contour* rest = 0;;
    node left_most;
    node son;
    double depth = 0.0;
    double cur_depth = 0.0;

    double height = 0.0;
    double width = 0.0;
    double father_x = 0.0;
    double father_y = 0.0;
    double left_most_x = 0.0;
    double left_most_y = 0.0;
    double left_most_width = 0.0;
    GT_Point anchor;
    ConPoint* point_x1 = new ConPoint();
    ConPoint* point_x2 = new ConPoint();
    ConPoint* point_x3 = new ConPoint();
    ConPoint* point_x4 = new ConPoint();
    ConPoint* point_x5 = new ConPoint();
    edge e;
    GT_Polyline line;
    GT_Point first_bend;
    double y_bends = 0.0;
    double x_x4 = 0.0;
    double x_x5 = 0.0;

    //insert ConPoints in con_point_list
    treealgo->insert_con_point(point_x1);
    treealgo->insert_con_point(point_x2);
    treealgo->insert_con_point(point_x3);
    treealgo->insert_con_point(point_x4);
    treealgo->insert_con_point(point_x5);
	
	
    //compute the left contour at the height of the sons
    //--------------------------------------------------
    //initialize the contour with the contour of the leftmost subtree
    left_most = sons.front();
    sons.pop_front ();
    depth = treealgo->get_deepest_y(left_most);
    left = treealgo->get_node_info(left_most)->get_left_contour();

    //determine the rest contour if necessary by going through
    //all remaining sons.
    list<node>::iterator it;
    list<node>::iterator end = sons.end ();

    for (it = sons.begin(); it != end; ++it) {
	son = *it;
	cur_depth = treealgo->get_deepest_y(son);
	//if cur_depth > depth, we have to add a part of the contour
	//of this node to the existing contour.
	if(cur_depth > depth){
	    rest = treealgo->get_node_info(son)->get_left_contour()->get_rest_after_y(treealgo, depth);
	    //SABINE, 21.1.
	    //  			left = left->append_contour(rest);
	    left->append_contour(rest);
	    depth = cur_depth;
	}
    }

    //append the contour points belonging to the father
    //-------------------------------------------------
    height = treealgo->get_graph()->gt(father).graphics()->h();
    width = treealgo->get_graph()->gt(father).graphics()->w();
	
    father_x = treealgo->get_node_x_coord(father);
    father_y = treealgo->get_node_y_coord(father);
    left_most_x = treealgo->get_node_x_coord(left_most);
    left_most_y = treealgo->get_node_y_coord(left_most);
    left_most_width = treealgo->get_graph()->gt(left_most).graphics()->w();

    if(treealgo->get_routing() == Straightline || sons.size()+1 <= 1){
	anchor = treealgo->get_node_info(father)->get_edgeanchor();
	GT_Point edgeanchor(father_x + width/2*(1.0+anchor.x()),
	    father_y + height/2*(1.0+anchor.y()));
	//we have to set three points (x1, x2, x3):
	//            x1-----
	//             |     |
	//            x2--x3-
	//where the cor point of x1 is x2, of x2 is x2 and of x3 is
	//the upper middle of the leftmost son. x3 is the edgeanchor
	//saved in father.
	point_x3->set_con_point(edgeanchor);
	point_x3->set_cor_point(left_most_x + left_most_width/2, left_most_y);
	point_x3->set_point_type(Edge);
	left->push_contour_point(point_x3);
	
	point_x2->set_con_point(father_x, father_y + height);
	point_x2->set_cor_point(father_x, father_y + height);
	point_x2->set_point_type(NodeEnd);
	left->push_contour_point(point_x2);
		
	point_x1->set_con_point(father_x, father_y);
	point_x1->set_cor_point(father_x, father_y + height);
	point_x1->set_point_type(NodeBegin);
	left->push_contour_point(point_x1);
    }
    else{
	//we have to place the bends of the leftmost (and rightmost)
	//edge from father to son
	//         x1---------                                     
	//         |          |                           
	//         x2--x3-----                           
	//             |..|                              
	//       x5----x4  ------------                  
	//       |                     |                 
	//     -----                  ---                
	//    |     |                |   |               
	//     -----                  ---                

	//get polyline discribing the leftmost edge
	edge cur_edge;
	if(treealgo->get_graph()->attached()->is_directed() == true){
	    e = *left_most.in_edges_begin();
	} else {
	    forall_adj_edges(cur_edge, left_most){
		if(left_most.opposite(cur_edge) == father){
		    e = cur_edge;
		} else{
// 		    cout << "Fehler" << endl;
		}
	    }
	}
	line = treealgo->get_graph()->gt(e).graphics()->line();
	first_bend = *(++line.begin());
	y_bends = first_bend.y();
	x_x4 = first_bend.x();
	x_x5 = left_most_x + left_most_width/2;
		
	point_x5->set_con_point(x_x5, y_bends);
	point_x5->set_cor_point(x_x5, left_most_y);
	point_x5->set_point_type(Bend);
	left->push_contour_point(point_x5);
		
	point_x4->set_con_point(x_x4, y_bends);
	point_x4->set_cor_point(x_x4, y_bends);
	//x4 is normally a bend, but we need this information
	//for the second step of the shifting.
	point_x4->set_point_type(Edge);
	left->push_contour_point(point_x4);

	point_x3->set_con_point(x_x4, father_y + height);
	point_x3->set_cor_point(x_x4, y_bends);
	point_x3->set_point_type(Edge);
	left->push_contour_point(point_x3);

	point_x2->set_con_point(father_x, father_y + height);
	point_x2->set_cor_point(father_x, father_y + height);
	point_x2->set_point_type(NodeEnd);
	left->push_contour_point(point_x2);
		
	point_x1->set_con_point(father_x, father_y);
	point_x1->set_cor_point(father_x, father_y + height);
	point_x1->set_point_type(NodeBegin);
	left->push_contour_point(point_x1);
    }
    return left;
}


//*************************************************************//
// function: compute_right_contour                             //
// description: This function computes the new right contour   //
//       of the tree beginning at the node father. We also     //
//       need the "x"-Contour, but we already computed this    //
//       contour in update_right_contour of shift.cc.          //
//                        ----------x                          //
//                       |          x      x: right contour    //
//                        ----------x                          //
//                      /      |     x                         //
//                     /\     / \     /x                       //
//                    /  \   /   \    --x                      //
//                    ----  /     x                            //
//                         /       x                           //
//                        /         x                          //
//                       /           x                         //
//                       -------------x                        //
//                                                             //
//       The right contour at the height of the sons is stored //
//       in the rightmost son. Therefore we don't need to      //
//       compute the rest as we did in compute_left_contour.   //
//       The update_right_contour is almost the same as the    //
//       compute_left_contour-function.                        //
//       For more information see update_right_contour in      //
//       shift.cpp.                                            //
//*************************************************************//

Contour* compute_right_contour(GT_TreeAlgo* treealgo, node father, node right_most)
{
    Contour* right = 0;

    double width = 0.0;
    double height = 0.0;
    double father_x = 0.0;
    double father_y = 0.0;
    double right_most_x = 0.0;
    double right_most_y = 0.0;
    double right_most_width = 0.0;
    int nr_of_sons = 0;
    GT_Point anchor;
    ConPoint* point_x1 = new ConPoint();
    ConPoint* point_x2 = new ConPoint();
    ConPoint* point_x3 = new ConPoint();
    ConPoint* point_x4 = new ConPoint();
    ConPoint* point_x5 = new ConPoint();
    list<edge> edge_list;
    edge e;
    GT_Polyline line;
    GT_Point first_bend;
    double y_bends = 0.0;
    double x_x4 = 0.0;
    double x_x5 = 0.0;

    //insert ConPoints in con_point_list
    treealgo->insert_con_point(point_x1);
    treealgo->insert_con_point(point_x2);
    treealgo->insert_con_point(point_x3);
    treealgo->insert_con_point(point_x4);
    treealgo->insert_con_point(point_x5);

    //compute the right contour at the height of the sons
    //---------------------------------------------------
    right = treealgo->get_node_info(right_most)->get_right_contour();

    //append the contour points belonging to the father
    //-------------------------------------------------
    width = treealgo->get_graph()->gt(father).graphics()->w();
    height = treealgo->get_graph()->gt(father).graphics()->h();
	
    father_x = treealgo->get_node_x_coord(father);
    father_y = treealgo->get_node_y_coord(father);
    right_most_x = treealgo->get_node_x_coord(right_most);
    right_most_y = treealgo->get_node_y_coord(right_most);
    right_most_width = treealgo->get_graph()->gt(right_most).graphics()->w();

    nr_of_sons = treealgo->get_order(father).size();

    if(treealgo->get_routing() == Straightline || nr_of_sons <= 1){
	anchor = treealgo->get_node_info(father)->get_edgeanchor();
	GT_Point edgeanchor(father_x + width/2*(1.0+anchor.x()),
	    father_y + height/2*(1.0+anchor.y()));
	//we have to set three points (x1, x2, x3):
	//              -----X1
	//             |     |
	//              --x3-x2
	//where the cor point of x1 is x2, of x2 is x2 and of x3 is
	//the upper middle of the rightmost son. x3 is the edgeanchor
	//saved in father.
	point_x3->set_con_point(edgeanchor);
	point_x3->set_cor_point(right_most_x + right_most_width/2, right_most_y);
	point_x3->set_point_type(Edge);
	right->push_contour_point(point_x3);
	
	point_x2->set_con_point(father_x + width, father_y + height);
	point_x2->set_cor_point(father_x + width, father_y + height);
	point_x2->set_point_type(NodeEnd);
	right->push_contour_point(point_x2);
	
	point_x1->set_con_point(father_x + width, father_y);
	point_x1->set_cor_point(father_x + width, father_y + height);
	point_x1->set_point_type(NodeBegin);
	right->push_contour_point(point_x1);
    }
    else{
	//we have to place the bends of the leftmost (and rightmost)
	//edge from father to son
	//          ----------x1                                   
	//         |          |                           
	//          ------x3--x2                         
	//             |..|                              
	//        -----   x4----------x5                 
	//       |                     |                 
	//     -----                  ---                
	//    |     |                |   |               
	//     -----                  ---                

	//get polyline discribing the rightmost edge
      edge cur_edge;
      if(treealgo->get_graph()->attached()->is_directed() == true){
 	e = *right_most.in_edges_begin();
      } else {
        forall_adj_edges(cur_edge, right_most){
	  if(right_most.opposite(cur_edge) == father){
	    e = cur_edge;
	  } else{
// 	    cout << "Fehler" << endl;
	  }
	}
      }
//       cout << "vor line" << endl;
//       cout << "Kante e geht von Knoten " << treealgo->get_graph()->gt(e.source()).label() << " nach " << treealgo->get_graph()->gt(e.target()).label() << endl;
	line = treealgo->get_graph()->gt(e).graphics()->line();
// 	cout << "nach line" << endl;

	first_bend = *(++line.begin());
	y_bends = first_bend.y();
	x_x4 = first_bend.x();
	x_x5 = right_most_x + right_most_width/2;
		
	point_x5->set_con_point(x_x5, y_bends);
	point_x5->set_cor_point(x_x5, right_most_y);
	point_x5->set_point_type(Bend);
	right->push_contour_point(point_x5);
		
	point_x4->set_con_point(x_x4, y_bends);
	point_x4->set_cor_point(x_x4, y_bends);
	point_x4->set_point_type(Edge);
	right->push_contour_point(point_x4);

	point_x3->set_con_point(x_x4, father_y + height);
	point_x3->set_cor_point(x_x4, y_bends);
	point_x3->set_point_type(Edge);
	right->push_contour_point(point_x3);

	point_x2->set_con_point(father_x + width, father_y + height);
	point_x2->set_cor_point(father_x + width, father_y + height);
	point_x2->set_point_type(NodeEnd);
	right->push_contour_point(point_x2);
	
	point_x1->set_con_point(father_x + width, father_y);
	point_x1->set_cor_point(father_x + width, father_y + height);
	point_x1->set_point_type(NodeBegin);
	right->push_contour_point(point_x1);
    }

    return right;	
}
