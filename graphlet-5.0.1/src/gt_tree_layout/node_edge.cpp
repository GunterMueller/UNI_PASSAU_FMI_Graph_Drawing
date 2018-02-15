/* This software is distributed under the Lesser General Public License */
//***********************************************************//
// node_edge.cpp                                             //
//                                                           //
// This file implements the the vertical shifting of         //
// subtrees because of a node-edge intersection.             //
//                                                           //
//                                                           //
// Source: /home/br/CVS/graphlet/src/gt_algorithms/          //
//                 tree_layout/node_edge.cpp                 //
// Author: Wetzel Sabine                                     //
//                                                           //
// (C) University of Passau 1995-1999, graphlet Project      //
//                                                           //
//***********************************************************//
// $Source: /home/br/CVS/graphlet/src/gt_tree_layout/node_edge.cpp,v $
// $Author: himsolt $
// $Revision: 1.6 $
// $Date: 1999/03/05 20:47:12 $
// $Locker:  $
// $State: Exp $

#include <gt_base/Graphlet.h>

#include <GTL/graph.h>
#include <math.h>

#include "tree_structure.h"
#include "edgeanchor.h"
#include "shift.h"
#include "permutation.h"

//*************************************************************//
// predefinitions                                              //
//*************************************************************//
bool determine_intersection(GT_TreeAlgo* treealgo, edge e, node n, node father);
double determine_delta_x(GT_Point X1, GT_Point X2, GT_Point A, GT_Point B);
double length_between_points(GT_Point p1, GT_Point p2);
double determine_offset(GT_TreeAlgo* tree_algo,node first_son, node second_son, GT_Point edgeanchor, double distance, bool left, node father);


//*************************************************************//
// function: determine_node_edge_intersection                  //
// description: This function determines whether there is an   //
//     intersection between the edges (coming from father)     //
//     and the sons, e.g.:                                     //
//                    ----                                     //
//                   |    |                                    //
//                    ----                                     //
//                      x                                      //
//                      ---x-                                  //
//         ...         |     |x                                //
//                     |     |   x                             //
//                     |     |     --                          //
//                     |     |    |  |                         //
//                      -----      --                          //
//*************************************************************//
bool GT_TreeAlgo::determine_node_edge_intersection(node father)
{
    GT_Graph* gt_graph;
    graph* l_graph;
    edge e;
    node son;
    list<node> sons;
    bool node_edge = 0;

    gt_graph = this->get_graph();
    l_graph = gt_graph->attached();

    if(this->get_order(father).size() <= 1){
	return 0;
    }

    tree_forall_sorted_out_edges(e, father, this){
	sons.push_back (father.opposite(e));
    }

    list<node>::iterator it;
    list<node>::iterator end = sons.end ();

    tree_forall_sorted_out_edges(e, father, this){
#ifdef SHOW_NODE_EDGE_EXT
        cout << "Vaterkoordinaten: (" << this->get_node_x_coord(father) 
	     << "," << this->get_node_y_coord(father) << "); w = " 
	     << gt_graph->gt(father).graphics()->w() << "; h = " 
	     << gt_graph->gt(father).graphics()->h() << endl;
	cout << "Edgeanchor am Vaterknoten: (" 
	     << this->get_node_info(father)->get_edgeanchor().x() 
	     << "," 
	     << this->get_node_info(father)->get_edgeanchor().y() 
	     << ")" << endl;
 	cout << "------------------------------------" << endl;
       	cout << "* Kante: " << gt_graph->gt(e).label() << endl;
#endif
	for (it = sons.begin (); it != end; ++it) {
	    son = *it;
#ifdef SHOW_NODE_EDGE_EXT
 	  cout << " - Sohn: " << gt_graph->gt(son).label() << endl;
 	  cout << "Knotenkoordinaten: (" << this->get_node_x_coord(son) 
	       << "," << this->get_node_y_coord(son) << "); w = " 
	       << gt_graph->gt(son).graphics()->w() << "; h = " 
	       << gt_graph->gt(son).graphics()->h() << endl;
 	  cout << "Knoten an Kante: (" 
	       << this->get_node_x_coord(e.target()) 
	       << "," << this->get_node_y_coord(e.target()) 
	       << "); w = " 
	       << gt_graph->gt(e.target()).graphics()->w() 
	       << "; h = " 
	       << gt_graph->gt(e.target()).graphics()->h() 
	       << endl;
#endif
	    
	    node_edge = determine_intersection(this, e, son, father);
	    //if there is an intersection, we can return with true
	    if(node_edge == 1){
#ifdef SHOW_NODE_EDGE
	      cout << "There is an intersection!" << endl;
#endif
#ifdef SHOW_NODE_EDGE_EXT
 		cout << "   There is an intersection between the edge "
 		     << gt_graph->gt(e).label()
 		     << " and node " << gt_graph->gt(son).label()
		     << endl;
#endif
		return 1;
	    }
	}
    }

    //there is no intersection
    return 0;
}

//*************************************************************//
// function: shift_subtrees_node_edge                          //
// description: This function shifts all sons in x-direction   //
//      so that there is no node-edge intersection when        //
//      placing the father. Note that this step has to be      //
//      repeated several times because the father is moved     //
//      after one shifting (so that he is in the center, bary- //
//      center or median once more) and the edgeanchor is      //
//      recomputed. The shift_subtrees... is repeated at most  //
//      20 times (this is the counter in the layout_tree-      //
//      function in layout_alg.cpp). If there is an intersec-  //
//      tion after this last step when replacing the father    //
//      node, the shifting-function is called once more with-  //
//      out replacing the father and the edgeanchor. Thus, we  //
//      are able to guarantee no intersection by paying the    //
//      price of a wrong father placement. But the fault which //
//      is made is normally very small and can be omitted.     //
//                                                             //
//      The function itself:                                   //
//      Two lists are determined, one with the sons left of    //
//      the father node and the other with the sons right of   //
//      the father node. If there exists a son which has the   //
//      same x-coordinate as the father node, it is placed in  //
//      both list.                                             //
//      First of all, we determine if there is an intersection //
//      between the two middle nodes, more exactly between     //
//      each edge to a middle node and the other node and      //
//      vice versa.                                            //
//      After that, we check if there is an intersection       //
//      in either the left list or the right list.             //
//      In both cases, the shifting value delta_x is determined//
//      by the function determine_offset. If we are in the     //
//      left list, all sons left of the actual node are moved  //
//      to the left (the delta_x is negated), otherwise all    //
//      sons right to the actual node are moved to the right.  //
//      Note that we do round the delta_x-value before         //
//      shifting.                                              //
//      The determine_offset-function operates with a simple   //
//      "Strahlensatz" (sorry, i don't know the english word,  //
//      but look at the picture in the description of the      //
//      function.                                              //
//*************************************************************//
void GT_TreeAlgo::shift_subtrees_node_edge(node father, int counter)
{
    GT_Graph* gt_graph;
    graph* l_graph;
    edge e;
    node son;
    node left_son;
    node right_son;
    double left_son_y = 0.0;
    double right_son_y = 0.0;
    node first_son;
    node second_son;
    list<node> left_sons;
    list<node> right_sons;
    double father_x = 0.0;
    double father_y = 0.0;
    double father_w = 0.0;
    double father_h = 0.0;
    double son_x = 0.0;
    double son_w = 0.0;
    double delta_x = 0.0;
    double distance = 0.0;

    //    this->print_treeinfo();
    
    gt_graph = this->get_graph();
    l_graph = gt_graph->attached();

    father_x = this->get_node_x_coord(father);
    father_y = this->get_node_y_coord(father);
    father_w = gt_graph->gt(father).graphics()->w();
    father_h = gt_graph->gt(father).graphics()->h();

    
#ifdef SHOW_NODE_EDGE_EXT
    cout << "Vaterknoten: (" << father_x << ", " << father_y << "); w = "
	 << father_w << "; h = " << father_h << endl;
#endif
    
    distance = this->get_hor_node_node_dist() * this->get_node_edge_dist();

    //relative
    GT_Point anchor = this->get_node_info(father)->get_edgeanchor();
    //absolute
    GT_Point edgeanchor(father_x + father_w/2*(1.0+anchor.x()),
	father_y + father_h/2*(1.0+anchor.y()));

#ifdef SHOW_NODE_EDGE_EXT
    cout << "Edgeanchor bei Vater = (" << edgeanchor.x() << ", "
	 << edgeanchor.y() << ")" << endl;
#endif

    int nr_of_right_sons = 0;
    int nr_of_left_sons = 0;
    
    tree_forall_sorted_out_edges(e, father, this){
	son = father.opposite(e);
	son_x = this->get_node_x_coord(son);
	son_w = gt_graph->gt(son).graphics()->w();

	//the two "=" are important! If father and son are lying
	//on the same x-coord, son is inserted in both lists
	if(son_x + son_w/2 <= father_x + father_w/2){
	    left_sons.push_front (son);
	    ++nr_of_left_sons;
	}
	if(son_x + son_w/2 >= father_x + father_w/2){
	    right_sons.push_back (son);
	    ++nr_of_right_sons;
	}
    }

    //compare the two middle sons
    left_son = left_sons.front ();
    right_son = right_sons.front ();

    list<node>::iterator it;
    list<node>::iterator end;

    if(left_son != right_son){
#ifdef SHOW_NODE_EDGE
      cout << "VERGLEICH DER MITTLEREN BEIDEN SOEHNE" << endl;
#endif
	left_son_y = this->get_node_y_coord(left_son);
	right_son_y = this->get_node_y_coord(right_son);
	if(right_son_y < left_son_y){
	    //there can be an intersection between right_son and
	    //the edge to the left_son
	    delta_x = determine_offset(this, right_son, left_son,
		edgeanchor, distance, 1, father);
	    if(delta_x != 0.0){
		delta_x = ceil(delta_x);
		
		end = left_sons.end ();
		
		for (it = left_sons.begin (); it != end; ++it) {
		    son = *it;

#ifdef SHOW_NODE_EDGE
 		    cout << "Verschieben von Sohn "
 			 << gt_graph->gt(son).label()
 			 << " um " << - delta_x << endl;
#endif
		    this->update_x_coords(son, -delta_x);
		    this->get_node_info(son)->get_left_contour()
			->update_contour(-delta_x);
		    this->get_node_info(son)->get_right_contour()
			->update_contour(-delta_x);
		}
	    }
	}
	else{
	    //there can be an intersection between left_son and
	    //the edge to the right_son
	    delta_x = determine_offset(this, left_son, right_son,
		edgeanchor, distance, 0, father);
	    if(delta_x != 0.0){
	      delta_x = ceil(delta_x);
	      
	      end = right_sons.end ();
	      
	      for (it = right_sons.begin (); it != end; ++it) {
		  son = *it;

#ifdef SHOW_NODE_EDGE
 		    cout << "Verschieben von Sohn "
 			 << gt_graph->gt(son).label()
 			 << " um " << delta_x << endl;
#endif
		    this->update_x_coords(son, delta_x);
		    this->get_node_info(son)->get_left_contour()
			->update_contour(delta_x);
		    this->get_node_info(son)->get_right_contour()
			->update_contour(delta_x);
		}
	    }
	}  
    }
    

    while(nr_of_right_sons >= 2){
#ifdef SHOW_NODE_EDGE
      cout << "VERGLEICH DER RESTLICHEN SOEHNE" << endl;
#endif
	//determine if there is an intersection between the first
	//son of the list and the inedge of the second son
	first_son = right_sons.front ();
	right_sons.pop_front ();
	--nr_of_right_sons;
	second_son = right_sons.front ();
	delta_x = determine_offset(this, first_son, second_son,
	    edgeanchor, distance, 0, father);

	if(delta_x != 0.0){
	  delta_x = ceil(delta_x);
	  
	  end = right_sons.end ();

	  for (it = right_sons.begin(); it != end; ++it) {
	      son = *it;
#ifdef SHOW_NODE_EDGE
 		cout << "Verschieben von Sohn " 
		     << gt_graph->gt(son).label()
 		     << " um " << delta_x << endl;
#endif
		this->update_x_coords(son, delta_x);
		this->get_node_info(son)->get_left_contour()
		    ->update_contour(delta_x);
		this->get_node_info(son)->get_right_contour()
		    ->update_contour(delta_x);
	    }
	}
    }
    
    while(nr_of_left_sons >= 2){
	//determine if there is an intersection between the first
	//son of the list and the inedge of the second son
	first_son = left_sons.front ();
	left_sons.pop_front ();
	--nr_of_left_sons;
	second_son = left_sons.front ();
	
	delta_x = determine_offset(this, first_son, second_son,
	    edgeanchor, distance, 1, father);
	
	if(delta_x != 0.0){
	  delta_x = ceil(delta_x);

	  end = left_sons.end ();

	  for (it = left_sons.begin(); it != end; ++it) {
	      son = *it;
#ifdef SHOW_NODE_EDGE
 		cout << "Verschieben von Sohn " 
		     << gt_graph->gt(son).label()
 		     << " um " << - delta_x << endl;
#endif
		this->update_x_coords(son, -delta_x);
		this->get_node_info(son)->get_left_contour()
		    ->update_contour(-delta_x);
		this->get_node_info(son)->get_right_contour()
		    ->update_contour(-delta_x);
	    }
	}
    }

    //replace father and new call of shift-function
    if(counter >= 1){
	this->father_placement(father);
 	this->compute_edgeanchor(father);
    }
#ifdef SHOW_NODE_EDGE
    cout <<"******* Ueberpruefe auf erneuten Schnittpunkt ********" 
	 << endl;
#endif
   if(this->determine_node_edge_intersection(father) == 1
	&& counter != 0){
	//new call of shift_subtrees_node_edge(father);
#ifdef SHOW_NODE_EDGE
 	cout << "----------------------------------" << endl;
#endif
	this->shift_subtrees_node_edge(father, counter - 1);
    }
}



//*************************************************************//
// function: determine_intersection                            //
// description: This function determines whether there is an   //
//              intersection between a node and an edge or not.//
//              Therefore, we determine the intersection       //
//              (inter) between the line n1-n2 and the line    //
//              e1-e2. If inter is lying between the xcoord.   //
//              of n1 and the xcoord of n2, there is an        //
//              intersection.                                  //
//                                                             //
//              |father  |                                     //
//               --- e1--                                      //
//                    \                                        //
//                     \                                       //
//               n1-----o--n2           o: inter               //
//                | n    \ |                                   //
//                |       \                                    //
//                 ------- \                                   //
//                         -e2---                              //
//                        | son  |                             //
//                                                             //
//                                                             //
//              or there is an intersection like that:         //
//                                                             //
//                                                             //
//                           \   ---------                     //
//                            \ |      n  |                    //
//                             \|         |                    //
//                              \         |                    //
//                              |\        |                    //
//                               -\ ------                     //
//                                 \                           //
//                                  \                          //
//             this means that the y-coord of inter is between //
//             the y-coordinates of n                          //
//                                                             //
//             There is also an intersection if the edge is    //
//             almost touching the node. We compute the        //
//             "distance" which the edge must have at least.   //
//             This is the horizontal node-node-distance       //
//             multiplied with the node-edge-factor            //
//                                                             //
//                      /                                      //
//                     /                                       //
//                    / ----------                             //
//                   / |          |    in this case: inter-    //
//                  /  |          |                  section   //
//                      ----------                             //
//                 <-->                                        //
//                distance                                     //
//*************************************************************//
bool determine_intersection(GT_TreeAlgo* treealgo, edge e, node n, node father)
{
    GT_Graph* gt_graph;
    graph* l_graph;
    double distance = 0.0;
    double n_x = 0.0;
    double n_y = 0.0;
    double n_w = 0.0;
    double n_h = 0.0;
    double son_x = 0.0;
    double son_y = 0.0;
    double son_w = 0.0;
    double father_x = 0.0;
    double father_y = 0.0;
    double father_w = 0.0;
    double father_h = 0.0;
//     node father;
    node son;

    gt_graph = treealgo->get_graph();
    l_graph = gt_graph->attached();

    distance = treealgo->get_hor_node_node_dist() 
      * treealgo->get_node_edge_dist();

    n_x = treealgo->get_node_x_coord(n);
    n_y = treealgo->get_node_y_coord(n);
    n_w = gt_graph->gt(n).graphics()->w();
    n_h = gt_graph->gt(n).graphics()->h();
    

    GT_Point n1(n_x, n_y);
    GT_Point n2(n_x + n_w, n_y);
    GT_Line n1_to_n2(n1, n2);

    //e1 is the edgeanchor of the father-node and e2 is the middle
    //upper of the son-node (son)
//     father = e.source();
    if(treealgo->get_graph()->attached()->is_directed() == true){
	son = e.target();
    } else {
	if(e.target() == father){
	    son = e.source();
	} else {
	    son = e.target();
	}
    }
    //if son is the target of the edge, there is only the
    //"natural" intersection which is no intersection in our
    //specification
    if(n == son){
	return 0;
    }
    
    son_x = treealgo->get_node_x_coord(son);
    son_y = treealgo->get_node_y_coord(son);
    son_w = gt_graph->gt(son).graphics()->w();
    father_x = treealgo->get_node_x_coord(father);
    father_y = treealgo->get_node_y_coord(father);
    father_w = gt_graph->gt(father).graphics()->w();
    father_h = gt_graph->gt(father).graphics()->h();

    //relative edgeanchor
    GT_Point anchor = treealgo->get_node_info(father)
	->get_edgeanchor();
    // absolute edgeanchor
    GT_Point e1(father_x + father_w/2*(1.0+anchor.x()),
	father_y + father_h/2*(1.0+anchor.y()));
    GT_Point e2(son_x + son_w/2, son_y);
    GT_Line e1_to_e2(e1, e2);

    GT_Point inter = get_intersection(n1_to_n2, e1_to_e2);
#ifdef SHOW_NODE_EDGE_EXT
    cout << "Schnittpunkt ist: (" << inter.x() << ", " 
	 << inter.y() << ")" << endl;
#endif

    if(e2.y() < inter.y()){
      return 0;
    }
    if(son_x + son_w/2 < n_x + n_w/2){
	//son is left of n
	if(inter.x() > n_x - distance){
	    return 1;
	}
	else{
	    return 0;
	}
    }
    else{
	//son is right of n
	if(inter.x() < n_x + n_w + distance){
	    return 1;
	}
	else{
	    return 0;
	}
    }
}


//*************************************************************//
// function: determine_offset                                  //
// description: This function determines the points which      //
//       are given to the determine_delta_x-function and       //
//       returns the delta_x for the shifting procedure.       //
//*************************************************************//
double determine_offset(GT_TreeAlgo* tree_algo, node first_son, node second_son, GT_Point edgeanchor, double distance, bool left, node father)
{
    GT_Graph* gt_graph;
    graph* l_graph;
    double delta_x = 0.0;
    edge inedge;
    double first_x = 0.0;
    double first_y = 0.0;
    double first_w = 0.0;
    double second_x = 0.0;
    double second_y = 0.0;
    double second_w = 0.0;
    bool is_intersection = 0;

    gt_graph = tree_algo->get_graph();
    l_graph = gt_graph->attached();
    
//     //there is only one inedge, but leda only offers these
//     //functions.
//     list<edge> list_of_inedges;
//     list_of_inedges = l_graph->in_edges(second_son);
//     inedge = list_of_inedges.front ();

    if(tree_algo->get_graph()->attached()->is_directed() == true){
	inedge = *second_son.in_edges_begin();
    } else {
	edge e;
	forall_adj_edges(e, second_son){
	    if((e.target()==father && e.source()==second_son) ||
		(e.source()==father && e.target()==second_son)){
		inedge = e;
	    } else {
// 		cout << "Fehler" << endl;
	    }
	}
    }
    
    
    is_intersection = determine_intersection(tree_algo, inedge, first_son, father);
    if(is_intersection == 1){
#ifdef SHOW_NODE_EDGE
    cout << "Schnittpunkt zwischen Knoten " 
	 << gt_graph->gt(first_son).label() << " und Kante " 
	 << gt_graph->gt(inedge).label() << " existiert" << endl;
#endif
	first_x = tree_algo->get_node_x_coord(first_son);
	first_y = tree_algo->get_node_y_coord(first_son);
	first_w = gt_graph->gt(first_son).graphics()->w();
	
		    
#ifdef SHOW_NODE_EDGE_EXT
	double first_h = gt_graph->gt(first_son).graphics()->h();
	cout << "first: (" << first_x << ", " << first_y
	     << "); w = " << first_w << "; h = " << first_h << endl;
#endif
		    
	second_x = tree_algo->get_node_x_coord(second_son);
	second_y = tree_algo->get_node_y_coord(second_son);
	second_w = gt_graph->gt(second_son).graphics()->w();

		    
#ifdef SHOW_NODE_EDGE_EXT
	double second_h = gt_graph->gt(second_son).graphics()->h();
	cout << "second: (" << second_x << ", " << second_y
	     << "); w = " << second_w << "; h = " << second_h << endl;
#endif
	    
	GT_Point X2(second_x + second_w/2, second_y);

		    
#ifdef SHOW_NODE_EDGE_EXT
 	cout << "- X2 = (" << X2.x() << ", "<< X2.y()
 	     << ")" << endl;
#endif

		    
	GT_Point I = get_intersection(GT_Line(edgeanchor, X2),
	    GT_Line(GT_Point(first_x, first_y),
		GT_Point(first_x + first_w, first_y)));

		    
#ifdef SHOW_NODE_EDGE_EXT
 	cout << "- I = (" << I.x() << ", " << I.y() << ")" 
	     << endl;
#endif
				    
	if(left == 1){
	    GT_Point J(first_x - distance, first_y);
	    //determine delta_x
	    delta_x = determine_delta_x(edgeanchor, X2, I,J);
	    
#ifdef SHOW_NODE_EDGE_EXT
  	    cout << "- J = (" << J.x() << ", " << J.y()
  		 << ")" << endl;
#endif
	}
	else{
	    GT_Point J(first_x + first_w + distance, first_y);
	    //determine delta_x
	    delta_x = determine_delta_x(edgeanchor, X2, I,J);
	    
#ifdef SHOW_NODE_EDGE_EXT
	    cout << "- J = (" << J.x() << ", " << J.y()
 		 << ")" << endl;
#endif
	}
	

		    
#ifdef SHOW_NODE_EDGE
  	cout << "delta_x = " << delta_x << endl;
#endif
				    
    }
    return delta_x;
}
//*************************************************************//
// function: determine_delta_x                                 //
// description: This function determines the shifting-factor   //
//      between two nodes, i.e.                                //
//                                                             //
//               ---------------                               //
//              |         X1    |                              //
//               ----------o----                               //
//                           x y                               //
//                             x   y                           //
//                -------------I-o-    o J                     //
//               |                 x       y                   //
//               |                 | x X2      y               //
//               |                 |  -o-         -o-          //
//               |                 | |   |       |   |         //
//                -----------------   ---         ---          //
//                                                             //
//                                     |-----------|           //
//                                        delta_x              //
//                                                             //
//      There is an intersection between the edge x and the    //
//      big son -> shift the small sun by delta_x to the right //
//      The points X1, X2, I and J are determined by the upper //
//      function.                                              //
//*************************************************************//
double determine_delta_x(GT_Point X1, GT_Point X2, GT_Point A, GT_Point B)
{
    double delta_x = 0.0;

#ifdef SHOW_NODE_EDGE_EXT
    cout << "|I,J| = " << length_between_points(A, B) << endl;
    cout << "|X1,X2| = " << length_between_points(X1, X2) << endl;
    cout << "|X1,I| = " << length_between_points(X1, A) << endl;
#endif

    delta_x = length_between_points(A, B)
	* length_between_points(X1, X2)
	/ length_between_points(X1, A);

    return delta_x;
}


//*************************************************************//
// function: length_between_points                             //
// description: This functions determines the quad. length of  //
//       the segment between two given points.                 //
//*************************************************************//
double length_between_points(GT_Point p1, GT_Point p2)
{
    return sqrt(square(p1.x() - p2.x())
	+ square(p1.y() - p2.y()));
}
