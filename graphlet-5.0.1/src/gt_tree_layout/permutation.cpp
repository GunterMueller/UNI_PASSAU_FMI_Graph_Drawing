/* This software is distributed under the Lesser General Public License */
//***********************************************************//
// permutation.cpp                                           //
//                                                           //
// This file implements the permutation of the sons.         //
//                                                           //
//                                                           //
// Source: /home/br/CVS/graphlet/src/gt_algorithms/          //
//                 tree_layout/permutation.cpp               //
// Author: Wetzel Sabine                                     //
//                                                           //
// (C) University of Passau 1995-1999, graphlet Project      //
//                                                           //
//***********************************************************//
// $Source: /home/br/CVS/graphlet/src/gt_tree_layout/permutation.cpp,v $
// $Author: himsolt $
// $Revision: 1.7 $
// $Date: 1999/03/05 20:47:16 $
// $Locker:  $
// $State: Exp $

#include <gt_base/Graphlet.h>
#include <GTL/graph.h>
#include "tree_structure.h"
#include "permutation.h"
#include <gt_base/NEI.h>
// #include <vector>
// #include <algo.h>


//*************************************************************//
// predefinitions                                              //
//*************************************************************//

bool compute_direction(GT_TreeAlgo* treealgo);
void determine_undirected_out_edges(GT_TreeAlgo* treealgo, node v, edge* edge_to_v);
class TreeCompare;



//*************************************************************//
// function: compute_order_of_sons                             //
// description:                                                //
//                                                             //
//                                                             //
//                                                             //
//                                                             //
//                                                             //
//*************************************************************//
bool compute_direction(GT_TreeAlgo* treealgo)
{
      if(treealgo->get_permutation() == KeepIt){
        return false;
      }
      return true;
//       if((treealgo->get_permutation() == KeepIt) && 
//          ((treealgo->get_direction() == TopBottom) || 
// 	  (treealgo->get_direction() == RightLeft))){
//         return false;
//       }
//       if((treealgo->get_permutation() == MirrorIt) && 
//          ((treealgo->get_direction() == BottomTop) || 
// 	  (treealgo->get_direction()  == LeftRight))){
//         return false;
//       }
//       return true;
}


//********************************************************************//
// class:       TreeCompare                                           //
// description: TreeCompare is the class containing the compare       //
//        function used by the 'forall_sorted_out_edges'-macro. It    //
//        is written specially for the tree algorithm.                //
//        The function operator determines if edge e1 is bigger than  //
//        edge e2.                                                    //
//                                                                    //
// call:  forall_sorted_out_edges(e, n, TreeCompare(n, gt_graph, dir) //
//                                                                    //
// input: two edges e1 and e2 which are outgoing edges of n           //
//                                                                    //
//        direction = true  -> clockwise orientation                  //
//        direction = false -> couterclockwise orientation            //
//                                                                    //
//        We determine the clippoint of the incoming edge of the      //
//        given node (if indeg = 0, the reference point is set North) // 
//        and the clippoints of the two given edges. All these points //
//        are connected with the center of n. After that, we          //
//        determine the angle beteen the line to the reference point  //
//        and the line to the clippoint of e1 and the same for the    //
//        clippoint of e2.                                            //
//        We compare the angles and return the value according to     //
//        the given direction.                                        //
//                                                                    //
//        Example: If the direction is clockwise, e1 has a smaller    //
//                 angle than e2 and vice versa for couterclockwise.  //
//                                                                    //
//                                                                    //
//               clip_e2      clip_ref                                //
//             ---------x-------x-------------                        //
//            |          \     /              |                       //
//            |           \   /               |                       //
//            |            \ /                |                       //
//            |             o ----------------x clip_e1               //
//            |                               |                       //
//            |                               |                       //
//            |  n                            |                       //
//             -------------------------------                        //
//                                                                    //
//********************************************************************//

// class TreeCompare : public binary_function <edge, edge, bool> { 
//  private:
//   node n;
//   GT_Graph* gt_graph;
//   edge* in_edge;
//   bool direction; // true: clockwise, false: couterclockwise
//   GT_TreeAlgo* treealgo;
// public:

//   TreeCompare(node act_node, GT_Graph* g,  bool dir, GT_TreeAlgo* ta){
//     n = act_node;
//     gt_graph = g;
//     direction = dir;
//     treealgo = ta;
//   };

//   bool operator() (edge e1, edge e2) {
//     double alpha_e1;
//     double alpha_e2;
//     GT_Point clip_ref;

//     if(n.indeg() != 0){
//       edge in = *(n.in_edges_begin());
//       clip_ref = gt_graph->gt(in).edge_nei()->clip_edge(GT_Target);
//     } else{
//       switch(treealgo->get_direction()){
//       case TopBottom:
// 	//clip_ref is North
// 	clip_ref.x(gt_graph->gt(n).graphics()->x());
// 	clip_ref.y(gt_graph->gt(n).graphics()->y() - gt_graph->gt(n).graphics()->h()/2);
// 	break;
//       case LeftRight:
// 	//clip_ref is West
// 	clip_ref.x(gt_graph->gt(n).graphics()->x() - gt_graph->gt(n).graphics()->w()/2);
// 	clip_ref.y(gt_graph->gt(n).graphics()->y());
// 	break;
//       case BottomTop:
// 	//clip_ref is South
// 	clip_ref.x(gt_graph->gt(n).graphics()->x());
// 	clip_ref.y(gt_graph->gt(n).graphics()->y() + gt_graph->gt(n).graphics()->h()/2);
// 	break;
//       case RightLeft:
// 	//clip_ref is East
// 	clip_ref.x(gt_graph->gt(n).graphics()->x() + gt_graph->gt(n).graphics()->w()/2);
// 	clip_ref.y(gt_graph->gt(n).graphics()->y());
// 	break;
//       }
//     }

//     GT_Point clip_e1 = gt_graph->gt(e1).edge_nei()->clip_edge(GT_Source);
//     GT_Point clip_e2 = gt_graph->gt(e2).edge_nei()->clip_edge(GT_Source);

//     const double origin_x = gt_graph->gt(n).graphics()->x();
//     const double origin_y = gt_graph->gt(n).graphics()->y();

// //     cout << "clip_ref: (" << clip_ref.x() << "," << clip_ref.y() << ")" << endl;
// //     cout << "Clippoint von Kante e1 zu Knoten " << gt_graph->gt(e1.target()).label();
// //     cout << ": (" << clip_e1.x() << "," << clip_e1.y() << ")" << endl;
// //     cout << "Clippoint von Kante e2 zu Knoten " << gt_graph->gt(e2.target()).label();
// //     cout << ": (" << clip_e2.x() << "," << clip_e2.y() << ")" << endl;
// //     cout << "origin: " << origin_x << " " <<origin_y << endl;


//     // angle of the line [origin, clip_ref] with the horicontal line from origin
//     double dx_ref = (clip_ref.x() - origin_x);
//     double dy_ref = (clip_ref.y() - origin_y);
//     double a_ref = atan2(dy_ref, dx_ref) + 3.1415927;

//     double dx_e1 = (clip_e1.x() - origin_x);
//     double dy_e1 = (clip_e1.y() - origin_y);
//     double a_e1 = atan2(dy_e1, dx_e1) + 3.1415927;

//     double dx_e2 = (clip_e2.x() - origin_x);
//     double dy_e2 = (clip_e2.y() - origin_y);
//     double a_e2 = atan2(dy_e2, dx_e2) + 3.1415927;

// //     cout << "dx_ref: " << dx_ref << endl;
// //     cout << "dy_ref: " << dy_ref << endl;
// //     cout << "a_ref: " << a_ref << endl;
// //     cout << "a_ref (in Grad): " << 360.0/6.2831853 * a_ref << endl;
// //     cout << "dx_e1: " << dx_e1 << endl;
// //     cout << "dy_e1: " << dy_e1 << endl;
// //     cout << "a_e1 (in Grad): " << 360.0/6.2831853 * a_e1 << endl;
// //     cout << "dx_e2: " << dx_e2 << endl;
// //     cout << "dy_e2: " << dy_e2 << endl;
// //     cout << "a_e2 (in Grad): " << 360.0/6.2831853 * a_e2 << endl;

//     if(a_ref <= a_e1){
//       alpha_e1 = a_e1 - a_ref;
//     }
//     else{
//       alpha_e1 = 6.2831853+ a_e1 - a_ref;      
//     }

//     if(a_ref <= a_e2){
//       alpha_e2 = a_e2 - a_ref;
//     }
//     else{
//       alpha_e2 = 6.2831853+ a_e2 - a_ref;      
//     }

// //     cout << "alpha_e1 (in Grad): " << 360.0/6.2831853 * alpha_e1 << endl;
// //     cout << "alpha_e2 (in Grad): " << 360.0/6.2831853 * alpha_e2 << endl;

//     if(direction == false){
//       return (alpha_e1 > alpha_e2);
//     }
//     else{
//       return (alpha_e1 < alpha_e2);
//     }
//   }

// };



//SABINE: Ersatzklasse, die auf die Koordinaten der Knoten eingeht. Auch nicht optimal.
// Vielleicht sollte man als Referenzpunkt nicht den Vaterknoten nehmen, sondern
// North, West, East oder South, je nach der Zeichenrichtung (genauso wird auch verfahren
// falls kein Vater existiert! -> auch nicht gut!!!!

//********************************************************************//
// class:       TreeCompare                                           //
// description: TreeCompare is the class containing the compare       //
//        function used by the 'forall_sorted_out_edges'-macro. It    //
//        is written specially for the tree algorithm.                //
//        The function operator determines if edge e1 is bigger than  //
//        edge e2.                                                    //
//                                                                    //
// call:  forall_sorted_out_edges(e, n, TreeCompare(n, gt_graph, dir) //
//                                                                    //
// input: two edges e1 and e2 which are outgoing edges of n           //
//                                                                    //
//        direction = true  -> clockwise orientation                  //
//        direction = false -> couterclockwise orientation            //
//                                                                    //
//        We determine the coordinates of the two sons and a refer-   //
//        ence point. This reference point depends on the drawing     //
//        direction (f.e. it is North of the node n iff the dir. is   //
//        TopBottom, West for LeftRight, ...). All these points       //
//        are connected with the center of n. After that, we          //
//        determine the angle beteen the line to the reference point  //
//        and the line to son1 and the same for son2.                 //
//        We compare the angles and return the value according to     //
//        the given direction.                                        //
//                                                                    //
//        Example: If the direction is clockwise, e1 has a smaller    //
//                 angle than e2 and vice versa for couterclockwise.  //
//                                                                    //
//                                                                    //
//                            ref                                     //
//             ---------------x---------------                        //
//            |               |               |                       //
//            |               |               |          son1         //
//            |               |               |          -----        //
//            |               o ------------------------|--x  |       //
//            |              /                |          -----        //
//            |             /                 |                       //
//            |  n         /                  |                       //
//             -----------/-------------------                        //
//                       /                                            //
//                   ---/-                                            //
//                  |  x  | son2                                      //
//                   -----                                            //
//********************************************************************//
class TreeCompare : public binary_function <edge, edge, bool> { 
 private:
  node n;
  GT_Graph* gt_graph;
  edge* in_edge;
  bool direction; // true: clockwise, false: couterclockwise
  GT_TreeAlgo* treealgo;
public:

  TreeCompare(node act_node, GT_Graph* g,  bool dir, GT_TreeAlgo* ta, edge* in){
    n = act_node;
    gt_graph = g;
    direction = dir;
    treealgo = ta;
    in_edge = in;
  };

    bool operator() (edge e1, edge e2) {
	double alpha_e1;
	double alpha_e2;
	GT_Point ref;
	GT_Point son1;
	GT_Point son2;

// 	cout << "Knoten n = (" << gt_graph->gt(n).graphics()->x() << "," << gt_graph->gt(n).graphics()->y() << ")" << endl;
    
	if(treealgo->get_graph()->attached()->is_undirected() == true){
	    if(in_edge != 0) {
// 		cout << "1. Fall" << endl;
		if((*in_edge).source() != n){
		    ref.x(gt_graph->gt((*in_edge).source()).graphics()->x());
		    ref.y(gt_graph->gt((*in_edge).source()).graphics()->y());
// 		    cout << "Referenzpunkt (1): " << gt_graph->gt((*in_edge).source()).label() << endl;
		} else {
		    ref.x(gt_graph->gt((*in_edge).target()).graphics()->x());
		    ref.y(gt_graph->gt((*in_edge).target()).graphics()->y());
// 		    cout << "Referenzpunkt (2): " << gt_graph->gt((*in_edge).target()).label() << endl;
		}		    
	    } else {
// 		cout << "2. Fall" << endl;
		switch(treealgo->get_direction()){
		    case TopBottom:
			//ref is North
			ref.x(gt_graph->gt(n).graphics()->x());
			ref.y(gt_graph->gt(n).graphics()->y() - gt_graph->gt(n).graphics()->h()/2);
			break;
		    case LeftRight:
			//ref is West
			ref.x(gt_graph->gt(n).graphics()->x() - gt_graph->gt(n).graphics()->w()/2);
			ref.y(gt_graph->gt(n).graphics()->y());
			break;
		    case BottomTop:
			//ref is South
			ref.x(gt_graph->gt(n).graphics()->x());
			ref.y(gt_graph->gt(n).graphics()->y() + gt_graph->gt(n).graphics()->h()/2);
			break;
		    case RightLeft:
			//ref is East
			ref.x(gt_graph->gt(n).graphics()->x() + gt_graph->gt(n).graphics()->w()/2);
			ref.y(gt_graph->gt(n).graphics()->y());
			break;
		}
	    }
	}
	if(treealgo->get_graph()->attached()->is_directed() == true){
	    if(n.indeg() != 0){
		edge in = *(n.in_edges_begin());
		ref.x(gt_graph->gt(in.source()).graphics()->x());
		ref.y(gt_graph->gt(in.source()).graphics()->y());
	    } else{
		switch(treealgo->get_direction()){
		    case TopBottom:
			//ref is North
			ref.x(gt_graph->gt(n).graphics()->x());
			ref.y(gt_graph->gt(n).graphics()->y() - gt_graph->gt(n).graphics()->h()/2);
			break;
		    case LeftRight:
			//ref is West
			ref.x(gt_graph->gt(n).graphics()->x() - gt_graph->gt(n).graphics()->w()/2);
			ref.y(gt_graph->gt(n).graphics()->y());
			break;
		    case BottomTop:
			//ref is South
			ref.x(gt_graph->gt(n).graphics()->x());
			ref.y(gt_graph->gt(n).graphics()->y() + gt_graph->gt(n).graphics()->h()/2);
			break;
		    case RightLeft:
			//ref is East
			ref.x(gt_graph->gt(n).graphics()->x() + gt_graph->gt(n).graphics()->w()/2);
			ref.y(gt_graph->gt(n).graphics()->y());
			break;
		}
	    }
	}
    
// 	cout << "vor Koordinatenbestimmung" << endl;
	son1.x(gt_graph->gt(n.opposite(e1)).graphics()->x());
	son1.y(gt_graph->gt(n.opposite(e1)).graphics()->y());
	son2.x(gt_graph->gt(n.opposite(e2)).graphics()->x());
	son2.y(gt_graph->gt(n.opposite(e2)).graphics()->y());
// 	cout << "n: " << gt_graph->gt(n).label()<< endl;
// 	cout << "1. Sohn: " << gt_graph->gt(n.opposite(e1)).label()<< endl;
// 	cout << "2. Sohn: " << gt_graph->gt(n.opposite(e2)).label()<< endl;
// 	cout << "ref.x: " << ref.x() << endl;
// 	cout << "ref.y: " << ref.y() << endl;
// 	cout << "son1.x: " << son1.x() << endl;
// 	cout << "son1.y: " << son1.y() << endl;
// 	cout << "son2.x: " << son2.x() << endl;
// 	cout << "son2.y: " << son2.y() << endl;
// 	cout << "nach Koordinatenbestimmung" << endl;
	
	const double origin_x = gt_graph->gt(n).graphics()->x();
	const double origin_y = gt_graph->gt(n).graphics()->y();
	
	// angle of the line [origin, ref] with the horicontal line from origin
	double dx_ref = (ref.x() - origin_x);
	double dy_ref = (ref.y() - origin_y);
	double a_ref = atan2(dy_ref, dx_ref) + 3.1415927;
	
	double dx_e1 = (son1.x() - origin_x);
	double dy_e1 = (son1.y() - origin_y);
	double a_e1 = atan2(dy_e1, dx_e1) + 3.1415927;
	
	double dx_e2 = (son2.x() - origin_x);
	double dy_e2 = (son2.y() - origin_y);
	double a_e2 = atan2(dy_e2, dx_e2) + 3.1415927;

//     cout << "dx_ref: " << dx_ref << endl;
//     cout << "dy_ref: " << dy_ref << endl;
//     cout << "a_ref: " << a_ref << endl;
//     cout << "a_ref (in Grad): " << 360.0/6.2831853 * a_ref << endl;
//     cout << "dx_e1: " << dx_e1 << endl;
//     cout << "dy_e1: " << dy_e1 << endl;
//     cout << "a_e1 (in Grad): " << 360.0/6.2831853 * a_e1 << endl;
//     cout << "dx_e2: " << dx_e2 << endl;
//     cout << "dy_e2: " << dy_e2 << endl;
//     cout << "a_e2 (in Grad): " << 360.0/6.2831853 * a_e2 << endl;
	

	// compute the angle to the reference point
	if(a_ref <= a_e1){
	    alpha_e1 = a_e1 - a_ref;
	}
	else{
	    alpha_e1 = 6.2831853+ a_e1 - a_ref;      
	}
	
	if(a_ref <= a_e2){
	    alpha_e2 = a_e2 - a_ref;
	}
	else{
	    alpha_e2 = 6.2831853+ a_e2 - a_ref;      
	}
	
	if(direction == false){
	    return (alpha_e1 > alpha_e2);
	}
	else{
	    return (alpha_e1 < alpha_e2);
	}
    }
};

//*************************************************************//
// function: compute_order_of_sons                             //
// description:                                                //
//                                                             //
//                                                             //
//                                                             //
//                                                             //
//                                                             //
//*************************************************************//

void GT_TreeAlgo::compute_order_of_sons()
{
  GT_Graph* gt_graph;
  graph* l_graph;
  node v;

  gt_graph = this->get_graph();
  l_graph = gt_graph->attached();
  
//   cout << "vor compute_direction" << endl;
  bool dir = compute_direction(this);
//   cout << "nach compute_direction" << endl;
  if(l_graph->is_directed() == true){
    forall_nodes(v, *l_graph){
      //collect all outgoing edges of v
      //     vector<edge> edge_list(v.out_edges_begin(), v.out_edges_end());
      vector<edge> edge_list;
      for (list<edge>::const_iterator i = v.out_edges_begin();
	   i != v.out_edges_end();
	   ++i) {
        edge_list.push_back(*i);
      }
      // sort them if necessary
//       cout << "vor Permutation" << endl;
      if(this->get_permutation() != Insertion){
	sort (edge_list.begin(), edge_list.end(), TreeCompare(v, gt_graph, dir, this,0));
      }
//       cout << "nach Permutation" << endl;
      this->set_order(v, edge_list);
    }
  } else {
    node root = this->get_root();
//     cout << this->get_root() << endl;
//     cout << "root1: " << gt_graph->gt(root).label() << endl;
//     cout << "vor determine_undirected_out_edges" << endl;
//     cout << "root2: " << this->get_graph()->gt(root).label() << endl;
    determine_undirected_out_edges(this,root,0);
//     cout << "nach determine_undirected_out_edges" << endl;
  }

  
//   forall_nodes(v, *l_graph){
//     cout << "Nachbarliste von Knoten " <<  this->get_graph()->gt(v).label() << " ist: ";
//     vector<edge> edge_list = this->get_order(v);
//     for(vector<edge>::iterator it2 = edge_list.begin(); it2 != edge_list.end(); ++it2){
//       cout << this->get_graph()->gt(v.opposite(*it2)).label() << " ";
//     }
//     cout << endl;
//   }
//   cout << "nach order" << endl;
}

void determine_undirected_out_edges(GT_TreeAlgo* treealgo, node v, edge* edge_to_v){
  vector<edge> edge_list;
  node::adj_edges_iterator it, end;
  edge e;

//   cout << "---- aktueller Knoten: " << treealgo->get_graph()->gt(v).label() << endl;
  it = v.adj_edges_begin();
  end = v.adj_edges_end();
  while (it != end){
    e = *it;
//     cout << "  edge_to_v: " << *edge_to_v << endl;
 //    cout << "  e: " <<e << endl;
//     cout << "  Nachbar: " << treealgo->get_graph()->gt(v.opposite(e)).label() << endl;
    if(edge_to_v == 0){
//       cout << "edge_to_v == 0" << endl;
      edge_list.push_back(e);
      determine_undirected_out_edges(treealgo, v.opposite(e), &e);
    } else {
//       cout << "edge_to_v != 0" << endl;
      if(e != *edge_to_v){
//       cout << "e != *edge_to_v" << endl;
	edge_list.push_back(e);
	determine_undirected_out_edges(treealgo, v.opposite(e), &e);
      }
    }
    ++it;
  }
  // sort them if necessary
//   cout << "vor Permutation" << endl;
  if(treealgo->get_permutation() != Insertion){
//       cout << "Sortieren" << endl;
      sort (edge_list.begin(), edge_list.end(), 
	  TreeCompare(v, treealgo->get_graph(), compute_direction(treealgo), treealgo, edge_to_v));
  }
//   cout << "nach Permutation" << endl;
  treealgo->set_order(v, edge_list);
}
