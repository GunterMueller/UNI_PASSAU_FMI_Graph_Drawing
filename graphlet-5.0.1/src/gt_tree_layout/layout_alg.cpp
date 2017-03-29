/* This software is distributed under the Lesser General Public License */
//***********************************************************//
// layout_alg.cpp                                            //
//                                                           //
// This file implements the algorithm for the tree layout.   //
//                                                           //
//                                                           //
// Source: /home/br/CVS/graphlet/src/gt_algorithms/          //
//                 tree_layout/layout_alg.cpp                //
// Author: Wetzel Sabine                                     //
//                                                           //
// (C) University of Passau 1995-1999, graphlet Project      //
//                                                           //
//***********************************************************//
// $Source: /home/br/CVS/graphlet/src/gt_tree_layout/layout_alg.cpp,v $
// $Author: himsolt $
// $Revision: 1.5 $
// $Date: 1999/03/05 20:47:05 $
// $Locker:  $
// $State: Exp $

#include <iostream>

#include <gt_base/Graphlet.h>

#include <GTL/graph.h>

#include "tree_structure.h"
#include "permutation.h"
//*************************************************************//
// predefinitions                                              //
//*************************************************************//
void layout_tree(GT_TreeAlgo* tree, node father);


//*************************************************************//
// function: tree_algorithm                                    //
// description: memberfunction of the class GT_TreeAlgo;       //
//              this function calls the recursive function     //
//              layout_tree;                                   //
//                                                             //
//*************************************************************//

void GT_TreeAlgo::tree_algorithm()
{
    layout_tree(this, this->get_root());

    this->transform_gt_coordinates();
}

//*************************************************************//
// function: layout_tree                                       //
// description: recursive function for determing the x-offsets //
//              of every node in the tree;                     //
//              contains the following function calls:         //
//              - compute_optimal_permutation                  //
//              - shift_x_coordinates                          //
//              - father_placement                             //
//              - shift_subtrees_node_edge if there is a       //
//                node_edge intersection                       //
//              - orthogonal_routing                           //
//              - compute_edgeanchor                           //
//              - compute_contour                              //
//*************************************************************//

void layout_tree(GT_TreeAlgo* tree, node father)
{
    GT_Graph* gt_graph;
    graph* l_graph;
    edge e;
    node son;
	
    gt_graph = tree->get_graph();
    l_graph = gt_graph->attached();

    //layout all sons
    tree_forall_sorted_out_edges(e, father,tree){
	son = father.opposite(e);
	layout_tree(tree, son);
    }

//      cout << "==============================" << endl;
//      cout << "Behandlung von Knoten " << gt_graph->gt(father).label() << endl << endl;
//     //permutation of the subtrees if necessary
//     if(tree->get_permutation() == Enabled){
// 	tree->compute_optimal_permutation(father);
//     }
    //initialize contour, if father has no sons
    if(tree->get_order(father).empty() == true)
    {
	tree->initialize_contour(father);
    }
//     cout << "nach initialize_contour" << endl;
//     tree->print_treeinfo();
	
    //shift the subtrees
    tree->shift_subtrees(father);
//     cout << "nach shift_subtrees" << endl;
//     tree->print_treeinfo();

    //place the father
    tree->father_placement(father);
//     cout << "nach father_placement" << endl;
//     tree->print_treeinfo();


    if(tree->get_routing() == Orthogonal){
	//compute orthogonal routing between father and all sons
	tree->orthogonal_routing(father);
//     cout << "nach orthogonal_routing" << endl;
    }
    else{
	//compute the edgeanchor in the father node
 	tree->compute_edgeanchor(father);
//     cout << "nach compute_edgeanchor" << endl;
	// cout << "compute_edgeanchor" << endl;
	// tree->print_treeinfo();
    }	

//     cout << "vor node-edge-shift" << endl;

#ifdef NODE_EDGE_SHIFT
    //remove node_edge_intersections
    if(tree->get_routing() == Straightline &&
	tree->get_orientation() != Top){
	bool node_edge = 0;
	//node_edge = 1 if there is an intersection
// 	cout << "erster Aufruf von determine_node_edge_inters." << endl;
	node_edge = tree->determine_node_edge_intersection(father);
	//try to shift max. 20 times; if there is an intersection after
	//that, do not replace the father node after the 21 time.
	int counter = 20;
	
 	if(node_edge == 1){
 	    tree->shift_subtrees_node_edge(father, counter);
//     cout << "nach shift_subtrees_node_edge" << endl;
 	}
#ifdef SHOW_NODE_EDGE
	cout << endl << endl << endl;
#endif
    }
#endif
//     cout << "nach node-edge-shift" << endl;

    //compute the left and right contour of the subtree
    tree->compute_contour(father);
//     cout << "nach compute_contour" << endl;
    //     cout << "compute_contour" << endl;
    //     tree->print_treeinfo();

}
