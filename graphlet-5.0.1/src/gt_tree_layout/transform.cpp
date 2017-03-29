/* This software is distributed under the Lesser General Public License */
//***********************************************************//
// transform.cpp                                             //
//                                                           //
// This file transforms the computed coordinates into GT-    //
// coordinates and shifts the tree if we have negative       //
// coordinates.                                              //
//                                                           //
//                                                           //
// Source: /home/br/CVS/graphlet/src/gt_algorithms/          //
//                 tree_layout/transform.cpp                 //
// Author: Wetzel Sabine                                     //
//                                                           //
// (C) University of Passau 1995-1999, graphlet Project      //
//                                                           //
//***********************************************************//
// $Source: /home/br/CVS/graphlet/src/gt_tree_layout/transform.cpp,v $
// $Author: himsolt $
// $Revision: 1.6 $
// $Date: 1999/03/05 20:47:23 $
// $Locker:  $
// $State: Exp $

#include <gt_base/Graphlet.h>
#include <gt_base/Graph.h>

#include <gt_base/NEI.h>

#include <GTL/graph.h>

#include "tree_structure.h"
#include "permutation.h"

//*************************************************************//
// predefinitions                                              //
//*************************************************************//
void negate_coordinates(GT_TreeAlgo* treealgo, graph* l_graph, GT_Graph* gt_graph);
void shift_coordinates(graph* l_graph, GT_Graph* gt_graph);
void set_x_y(GT_TreeAlgo* treealgo, graph* l_graph, GT_Graph* gt_graph);
void set_w_h(GT_TreeAlgo* treealgo, graph* l_graph, GT_Graph* gt_graph);
void set_bend(GT_TreeAlgo* treealgo, graph* l_graph, GT_Graph* gt_graph);
void set_eas(GT_TreeAlgo* treealgo, graph* l_graph, GT_Graph* gt_graph);
void set_ea_straight(GT_TreeAlgo* treealgo, graph* l_graph, GT_Graph* gt_graph, node father, node* grandpa);
void set_ea_orth(graph* l_graph, GT_Graph* gt_graph);
void reduce_bends(GT_TreeAlgo* treealgo, node father);

//*************************************************************//
// function: transform_gt_coordinates                          //
// description: This function transforms the internal coordi-  //
//              nates into GT-coordinates.                     //
//    case direction == TopBottom:                             //
//       * set x and y coordinate: gt_x = intern_x + w/2       //
//                                 gt_y = intern_y + h/2       //
//       * shift the graph into the positive range             //
//       * set the edgeanchors:                                //
//           - orthogonal: very easy; bends are correct ->     //
//                         set_EA_orthogonal                   //
//           - straightline: Source -> computed edgeanchor     //
//                           Target -> south                   //
//       * somtimes bend reduction                             //
//                                                             //
//    case direction == BottomTop:                             //
//       * set x and y coordinate: gt_x = intern_x + w/2       //
//                                 gt_y = intern_y + h/2       //
//       * negate y-coord of nodes and bends                   //
//       * shift the graph into the positive range             //
//       * set the edgeanchors:                                //
//           - orthogonal: very easy; bends are correct ->     //
//                         set_EA_orthogonal                   //
//           - straightline: Source -> negated  edgeanchor     //
//                           Target -> north                   //
//       * somtimes bend reduction                             //
//                                                             //
//    case direction == LeftRight:                             //
//       * set x and y coordinate: gt_x = intern_y + w/2       //
//                                 gt_y = intern_x + h/2       //
//       * switch width and heigth of every node               //
//       * shift the graph into the positive range             //
//       * set the edgeanchors:                                //
//           - orthogonal: very easy; bends are correct ->     //
//                         set_EA_orthogonal                   //
//           - straightline: Source -> edgeachor.y, ea.x       //
//                           Target -> west                    //
//       * somtimes bend reduction                             //
//                                                             //
//    case direction == RightLeft:                             //
//       * set x and y coordinate: gt_x = intern_y + w/2       //
//                                 gt_y = intern_x + h/2       //
//       * switch width and heigth of every node               //
//       * negate x-coord of nodes and bends                   //
//       * shift the graph into the positive range             //
//       * set the edgeanchors:                                //
//           - orthogonal: very easy; bends are correct ->     //
//                         set_EA_orthogonal                   //
//           - straightline: Source -> -ea.y, -ea.x            //
//                           Target -> east                    //
//       * somtimes bend reduction                             //
//*************************************************************//

void GT_TreeAlgo::transform_gt_coordinates()
{
    GT_Graph* gt_graph;
    graph* l_graph;

    gt_graph = this->get_graph();
    l_graph = gt_graph->attached();

   //set the x and y coordinates in the GT_Graph
    set_x_y(this, l_graph, gt_graph);

    //set the width and height for LeftRight and RightLeft direction
    set_w_h(this, l_graph, gt_graph);

    //set the bends for orthogonal drawing
    if(this->get_routing() == Orthogonal){
      set_bend(this, l_graph, gt_graph);
    }

    //negate all coordinates for BottomTop and RightLeft
    negate_coordinates(this, l_graph, gt_graph);

    //shift the coordinates into the positive 
    shift_coordinates(l_graph, gt_graph);

    //set the edgeanchors
    set_eas(this, l_graph, gt_graph);
    
     if(this->get_bend_reduction() == Enabled){
 	reduce_bends(this, this->get_root());
     }
}


//*************************************************************//
// function: negate_coordinates                                //
// description: see description in gt_transform_coordinates    //
//*************************************************************//
void negate_coordinates(GT_TreeAlgo* treealgo, graph* l_graph, GT_Graph* gt_graph)
{
  node v;
  edge e;
  double x_coord = 0.0;
  double y_coord = 0.0;
  GT_Polyline line;
  GT_Point bend;
  GT_Polyline::iterator it;
  GT_Polyline::iterator end;

  switch(treealgo->get_direction()){
  case BottomTop: {
    //negate y coordinate
    forall_nodes(v, *l_graph){
      y_coord = gt_graph->gt(v).graphics()->y();
      gt_graph->gt(v).graphics()->y(-y_coord);
    }

    if(treealgo->get_permutation() != Insertion){
      //negate x coordinate
      forall_nodes(v, *l_graph){
	x_coord = gt_graph->gt(v).graphics()->x();
	gt_graph->gt(v).graphics()->x(-x_coord);
      }
    }
    //negate bends
    forall_edges(e, *l_graph){
      line = gt_graph->gt(e).graphics()->line();
      end = line.end ();

      for (it = line.begin (); it != end; ++it) {
 	  bend = *it;
 	  bend.y (-bend.y());
	  if(treealgo->get_permutation() != Insertion){
	    bend.x (-bend.x());
	  }
 	  *it = bend;
// 	  (*it).y (- (*it).y());
      }

      gt_graph->gt(e).graphics()->line(line);
    }
    break; }
  case RightLeft: {
    //negate x coordinate
    forall_nodes(v, *l_graph){
      x_coord = gt_graph->gt(v).graphics()->x();
      gt_graph->gt(v).graphics()->x(-x_coord);
    }

    //negate bend
    forall_edges(e, *l_graph){
      line = gt_graph->gt(e).graphics()->line();
      end = line.end ();

      for (it = line.begin (); it != end; ++it) {
	  bend = *it;
	  bend.x (-bend.x());
	  *it = bend;
// 	  (*it).x (- (*it).x());
      }
      gt_graph->gt(e).graphics()->line(line);
    }
    break; }
  case LeftRight: {
    if(treealgo->get_permutation() != Insertion){
      //negate y coordinate
      forall_nodes(v, *l_graph){
	y_coord = gt_graph->gt(v).graphics()->y();
	gt_graph->gt(v).graphics()->y(-y_coord);
      }

      //negate bend
      forall_edges(e, *l_graph){
	line = gt_graph->gt(e).graphics()->line();
	end = line.end ();

	for (it = line.begin (); it != end; ++it) {
	  bend = *it;
	  bend.y(-bend.y());
	  *it = bend;
// 	  (*it).x (- (*it).x());
	}
	gt_graph->gt(e).graphics()->line(line);
      }
    }
    break; }
  }
}


 //*************************************************************//
// function: set_x_y                                           //
// description: see description in gt_transform_coordinates    //
//*************************************************************//
void set_x_y(GT_TreeAlgo* treealgo, graph* l_graph, GT_Graph* gt_graph)
{
  node v;
  double x_coord = 0.0;
  double y_coord = 0.0;
  double width = 0.0;
  double height = 0.0;
  

  switch(treealgo->get_direction()){
  case TopBottom: {
    forall_nodes(v, *l_graph){
      x_coord = treealgo->get_node_x_coord(v);
      y_coord = treealgo->get_node_y_coord(v);
      width = gt_graph->gt(v).graphics()->w();
      height = gt_graph->gt(v).graphics()->h();
      gt_graph->gt(v).graphics()->x(x_coord + width/2);
      gt_graph->gt(v).graphics()->y(y_coord + height/2);
    }
    break; }
  case BottomTop: {
    //same as TopBottom
    forall_nodes(v, *l_graph){
      x_coord = treealgo->get_node_x_coord(v);
      y_coord = treealgo->get_node_y_coord(v);
      width = gt_graph->gt(v).graphics()->w();
      height = gt_graph->gt(v).graphics()->h();
      gt_graph->gt(v).graphics()->x(x_coord + width/2);
      gt_graph->gt(v).graphics()->y(y_coord + height/2);
    }
    break; }
  case LeftRight: {
    forall_nodes(v, *l_graph){
      x_coord = treealgo->get_node_x_coord(v);
      y_coord = treealgo->get_node_y_coord(v);
      width = gt_graph->gt(v).graphics()->w();
      height = gt_graph->gt(v).graphics()->h();
      gt_graph->gt(v).graphics()->y(x_coord + width/2);
      gt_graph->gt(v).graphics()->x(y_coord + height/2);
    }
    break; }
  case RightLeft: {
    //same as LeftRight
    forall_nodes(v, *l_graph){
      x_coord = treealgo->get_node_x_coord(v);
      y_coord = treealgo->get_node_y_coord(v);
      width = gt_graph->gt(v).graphics()->w();
      height = gt_graph->gt(v).graphics()->h();
      gt_graph->gt(v).graphics()->y(x_coord + width/2);
      gt_graph->gt(v).graphics()->x(y_coord + height/2);
    }
    break; }
  }
}


//*************************************************************//
// function: shift_coordinates                                 //
// description: see description in gt_transform_coordinates    //
//*************************************************************//
void shift_coordinates(graph* l_graph, GT_Graph* gt_graph)
{
    node v;
    double x_coord = 0.0;
    double y_coord = 0.0;
    double shift_x = 0.0;
    double shift_y = 0.0;
    edge e;
    GT_Polyline line;
    GT_Point bend;
    GT_Polyline::iterator it;
    GT_Polyline::iterator end;

    //determine x-shift and y-shift value (we do not want to have
    //negative x and y coordinates)
    //determine the value with which we have to shift the tree
    //so that every node has a positive x- and y-coordinate
    forall_nodes(v, *l_graph){
	x_coord = gt_graph->gt(v).graphics()->x() -
	  gt_graph->gt(v).graphics()->w()/2;
	if(x_coord < 0 && x_coord < shift_x){
	    shift_x = x_coord;
	}
	y_coord = gt_graph->gt(v).graphics()->y() -
	  gt_graph->gt(v).graphics()->h()/2;
	if(y_coord < 0 && y_coord < shift_y){
	    shift_y = y_coord;
	}
    }
    //the graph should have minimal x = 10 and minimal y=10
    shift_x = shift_x - 10;
    shift_y = shift_y - 10;

    //shift x and y values
    forall_nodes(v, *l_graph){
      x_coord = gt_graph->gt(v).graphics()->x();
      y_coord = gt_graph->gt(v).graphics()->y();
      gt_graph->gt(v).graphics()->x(x_coord - shift_x);   
      gt_graph->gt(v).graphics()->y(y_coord - shift_y);
    }


    forall_edges(e, *l_graph){
      //shift bends
      line = gt_graph->gt(e).graphics()->line();
      end = line.end ();

      for (it = line.begin (); it != end; ++it) {
	  bend = *it;
	  bend.x(bend.x() - shift_x);
	  bend.y(bend.y() - shift_y);
	  *it = bend;
// 	  (*it).x ((*it).x() - shift_x);
// 	  (*it).y ((*it).y() - shift_y);	  
      }

      gt_graph->gt(e).graphics()->line(line);
    }
}


//*************************************************************//
// function: set_w_h                                           //
// description: see description in gt_transform_coordinates    //
//*************************************************************//
void set_w_h(GT_TreeAlgo* treealgo, graph* l_graph, GT_Graph* gt_graph)
{
  node v;
  double height = 0.0;
  double width = 0.0;

  if(treealgo->get_direction() == LeftRight || 
     treealgo->get_direction() == RightLeft){
    //re-change width and height of all nodes
    forall_nodes(v, *(l_graph)){
      height = gt_graph->gt(v).graphics()->h();
      width = gt_graph->gt(v).graphics()->w();
      gt_graph->gt(v).graphics()->h(width);
      gt_graph->gt(v).graphics()->w(height);
    }
  }
}

//*************************************************************//
// function: set_bend                                          //
// description: see description in gt_transform_coordinates    //
//*************************************************************//
void set_bend(GT_TreeAlgo* treealgo, graph* l_graph, GT_Graph* gt_graph)
{
  edge e;
  GT_Polyline line;
  GT_Point bend;
  GT_Polyline::iterator it;
  GT_Polyline::iterator end;
  double buffer;


   if(treealgo->get_direction() == LeftRight ||
      treealgo->get_direction() == RightLeft){
  //  if(treealgo->get_direction() == LeftRight){
    forall_edges(e, *l_graph){
      line = gt_graph->gt(e).graphics()->line();
      end = line.end ();

      for (it = line.begin (); it != end; ++it) {
	  bend = *it;
	  buffer = bend.x();
	  bend.x (bend.y());
	  bend.y(buffer);
	  *it = bend;
// 	  buffer = (*it).x();
// 	  (*it).x ((*it).y());
// 	  (*it).y (buffer);
      }

      gt_graph->gt(e).graphics()->line(line);
    }
  }
}

//*************************************************************//
// function: set_eas                                           //
// description: see description in gt_transform_coordinates    //
//*************************************************************//
void set_eas(GT_TreeAlgo* treealgo, graph* l_graph, GT_Graph* gt_graph)
{
  switch(treealgo->get_routing()){
  case Straightline:
    set_ea_straight(treealgo, l_graph, gt_graph, treealgo->get_root(),0);
    break;
  case Orthogonal:
    set_ea_orth(l_graph, gt_graph);
    break;
  }
}

//*************************************************************//
// function: set_ea_straight                                   //
// description: see description in gt_transform_coordinates    //
//*************************************************************//
void set_ea_straight(GT_TreeAlgo* treealgo, graph* l_graph, GT_Graph* gt_graph, node father, node* grandpa)
{
  edge e;
  GT_Key myanchor;

  switch(treealgo->get_direction()){
  case TopBottom: { myanchor = GT_Keys::anchor_s; break;}
  case BottomTop: { myanchor = GT_Keys::anchor_n; break;}
  case LeftRight: { myanchor = GT_Keys::anchor_w; break;}
  case RightLeft: { myanchor = GT_Keys::anchor_e; break;}
  }
//   cout << "-----------------------------------" << endl;

  if(grandpa == 0){
    tree_forall_sorted_out_edges(e, father, treealgo){
//   cout << "Edgeanchor von Kante " << treealgo->get_graph()->gt(e).label() <<" setzen" << endl;
      if(e.source() == father){
// 	  cout << "1. Fall" << endl;
	GT_Point edgeanchor = treealgo->get_node_info(e.source())->get_edgeanchor();
	switch(treealgo->get_direction()){
	    case TopBottom: { gt_graph->gt(e).edge_nei()->set_EA(GT_Source, edgeanchor.x(), edgeanchor.y()); break;}
	    case BottomTop: { gt_graph->gt(e).edge_nei()->set_EA(GT_Source, -edgeanchor.x(), -edgeanchor.y()); break;}
	    case LeftRight: { gt_graph->gt(e).edge_nei()->set_EA(GT_Source, edgeanchor.y(), edgeanchor.x()); break;}
	    case RightLeft: { gt_graph->gt(e).edge_nei()->set_EA(GT_Source, -edgeanchor.y(), -edgeanchor.x()); break;}
	}	
	gt_graph->gt(e).edge_nei()->set_EA(GT_Target, myanchor);
	set_ea_straight(treealgo, l_graph, gt_graph, e.target(), &father);
      } else {
// 	  cout << "2 Fall" << endl;
	GT_Point edgeanchor = treealgo->get_node_info(e.target())->get_edgeanchor();
	switch(treealgo->get_direction()){
	    case TopBottom: { gt_graph->gt(e).edge_nei()->set_EA(GT_Target, edgeanchor.x(), edgeanchor.y()); break;}
	    case BottomTop: { gt_graph->gt(e).edge_nei()->set_EA(GT_Target, -edgeanchor.x(), -edgeanchor.y()); break;}
	    case LeftRight: { gt_graph->gt(e).edge_nei()->set_EA(GT_Target, edgeanchor.y(), edgeanchor.x()); break;}
	    case RightLeft: { gt_graph->gt(e).edge_nei()->set_EA(GT_Target, -edgeanchor.y(), -edgeanchor.x()); break;}
	}	
	gt_graph->gt(e).edge_nei()->set_EA(GT_Source, myanchor);
	set_ea_straight(treealgo, l_graph, gt_graph, e.source(), &father);
      }
    }
  } else {
    tree_forall_sorted_out_edges(e, father, treealgo){
//   cout << "Edgeanchor von Kante " << treealgo->get_graph()->gt(e).label() <<" setzen" << endl;
      if(e.source() == father && e.target() != *grandpa){
// 	  cout << "3. Fall" << endl;
	GT_Point edgeanchor = treealgo->get_node_info(e.source())->get_edgeanchor();
	switch(treealgo->get_direction()){
	    case TopBottom: { gt_graph->gt(e).edge_nei()->set_EA(GT_Source, edgeanchor.x(), edgeanchor.y()); break;}
	    case BottomTop: { gt_graph->gt(e).edge_nei()->set_EA(GT_Source, -edgeanchor.x(), -edgeanchor.y()); break;}
	    case LeftRight: { gt_graph->gt(e).edge_nei()->set_EA(GT_Source, edgeanchor.y(), edgeanchor.x()); break;}
	    case RightLeft: { gt_graph->gt(e).edge_nei()->set_EA(GT_Source, -edgeanchor.y(), -edgeanchor.x()); break;}
	}	
	gt_graph->gt(e).edge_nei()->set_EA(GT_Target, myanchor);
	set_ea_straight(treealgo, l_graph, gt_graph, e.target(), &father);
      } else {
	if(e.source() != *grandpa){
// 	  cout << "4. Fall" << endl;
	  GT_Point edgeanchor = treealgo->get_node_info(e.target())->get_edgeanchor();
	switch(treealgo->get_direction()){
	    case TopBottom: { gt_graph->gt(e).edge_nei()->set_EA(GT_Target, edgeanchor.x(), edgeanchor.y()); break;}
	    case BottomTop: { gt_graph->gt(e).edge_nei()->set_EA(GT_Target, -edgeanchor.x(), -edgeanchor.y()); break;}
	    case LeftRight: { gt_graph->gt(e).edge_nei()->set_EA(GT_Target, edgeanchor.y(), edgeanchor.x()); break;}
	    case RightLeft: { gt_graph->gt(e).edge_nei()->set_EA(GT_Target, -edgeanchor.y(), -edgeanchor.x()); break;}
	}	
	  gt_graph->gt(e).edge_nei()->set_EA(GT_Source, myanchor);
	  set_ea_straight(treealgo, l_graph, gt_graph, e.source(), &father);
	}
      }
    }
  }
}
// void set_ea_straight(GT_TreeAlgo* treealgo, graph* l_graph, GT_Graph* gt_graph)
// {
//   edge e;

//   switch(treealgo->get_direction()){
//   case TopBottom: {
//     forall_edges(e, *l_graph){
//       GT_Point edgeanchor = treealgo->get_node_info(
// 	   e.source())->get_edgeanchor();
//       gt_graph->gt(e).edge_nei()->set_EA(GT_Source,
// 	   edgeanchor.x(), edgeanchor.y());
//       gt_graph->gt(e).edge_nei()->set_EA(GT_Target,
// 	   GT_Keys::anchor_s);
      
//     }
//     break; }
//   case BottomTop: {
//     forall_edges(e, *l_graph){
//       GT_Point edgeanchor = treealgo->get_node_info(
// 	   e.source())->get_edgeanchor();
//       gt_graph->gt(e).edge_nei()->set_EA(GT_Source,
// 	   -edgeanchor.x(), -edgeanchor.y());
//       gt_graph->gt(e).edge_nei()->set_EA(GT_Target,
// 	   GT_Keys::anchor_n);
      
//     }
//     break; }
//   case LeftRight: {
//     forall_edges(e, *l_graph){
//       GT_Point edgeanchor = treealgo->get_node_info(
// 	   e.source())->get_edgeanchor();
//       gt_graph->gt(e).edge_nei()->set_EA(GT_Source,
// 	   edgeanchor.y(), edgeanchor.x());
//       gt_graph->gt(e).edge_nei()->set_EA(GT_Target,
// 	   GT_Keys::anchor_w);
      
//     }
//     break; }
//   case RightLeft: {
//     forall_edges(e, *l_graph){
//       GT_Point edgeanchor = treealgo->get_node_info(
// 	   e.source())->get_edgeanchor();
//       gt_graph->gt(e).edge_nei()->set_EA(GT_Source,
// 	   -edgeanchor.y(), -edgeanchor.x());
//       gt_graph->gt(e).edge_nei()->set_EA(GT_Target,
// 	   GT_Keys::anchor_e);
      
//     }
//     break; }
//   }
// }

//*************************************************************//
// function: set_ea_orth                                       //
// description: see description in gt_transform_coordinates    //
//*************************************************************//
void set_ea_orth(graph* l_graph, GT_Graph* gt_graph)
{
  edge e;
  forall_edges(e, *l_graph){
    gt_graph->gt(e).edge_nei()->set_EA_connect_orthogonal();
  }
}

//*************************************************************//
// function: reduce_bends                                      //
// description: see description in gt_transform_coordinates    //
//*************************************************************//
void reduce_bends(GT_TreeAlgo* treealgo, node father) {
    GT_Graph* gt_graph;
    graph* l_graph;
    list<edge> edge_list;
    edge e;
    node son;
    node first_son;
    node last_son;
    double father_x = 0.0;
    double father_w = 0.0;
    double first_x = 0.0;
    double last_x = 0.0;
    double father_y = 0.0;
    double father_h = 0.0;
    double first_y = 0.0;
    double last_y = 0.0;
    double connect = 0.0;
    GT_Polyline line;
    list<edge>::iterator it;
    list<edge>::iterator end;
    
    gt_graph = treealgo->get_graph();
    l_graph = gt_graph->attached();
    
    tree_forall_sorted_out_edges(e, father, treealgo){
	son = father.opposite(e);
	reduce_bends(treealgo, son);
	edge_list.push_back(e);
    }
    
    if(treealgo->get_order(father).size() <= 1){
	return;
    }
	
    first_son = father.opposite(edge_list.front());
    last_son = father.opposite(edge_list.back());
    connect = treealgo->get_edge_connection_for_bend();

    if(treealgo->get_direction() == TopBottom ||
       treealgo->get_direction() == BottomTop){
	father_x = gt_graph->gt(father).graphics()->x();
	father_w = gt_graph->gt(father).graphics()->w();
	first_x = gt_graph->gt(first_son).graphics()->x();
	last_x = gt_graph->gt(last_son).graphics()->x();

        // It is possible that (because of the permutation) the x-coordinate of the first 
        // son is bigger than those of the second son. In this case we change first_x and last_x.
        if(first_x > last_x){
          double h = first_y;
          first_x = last_x;
	  last_x = h;
        }
   
	//check whether we can connect orthogonal
	if((first_x >= father_x - (connect*father_w)/2) &&
	    (last_x <= father_x + (connect*father_w)/2)){
	    end = edge_list.end();

	    for (it = edge_list.begin(); it != end; ++it) {
		e = *it;
		//delete bends

		line = gt_graph->gt(e).graphics()->line();
		if (line.size() > 2) {
		    line.erase (++line.begin(), --line.end());
		}

// 		line.push_back (gt_graph->gt(e).graphics()->line().front());
// 		line.push_back (gt_graph->gt(e).graphics()->line().back());

		gt_graph->gt(e).graphics()->line(line);

		//connect orthogonal
		switch(treealgo->get_direction()){
		case TopBottom:
		  gt_graph->gt(e).edge_nei()->set_EA(
		    GT_Target, GT_Keys::anchor_s);
		  break;
		case BottomTop:
		  gt_graph->gt(e).edge_nei()->set_EA(
		    GT_Target, GT_Keys::anchor_n);
		  break;
		}
	    
		line.back() = gt_graph->gt(e).edge_nei()->
		    convert_anchor_to_coordinates (GT_Target);
		gt_graph->gt(e).graphics()->line (line);
		gt_graph->gt(e).edge_nei()->set_EA_orthogonal(GT_Source);
// 		line.pop_back ();
// 		line.pop_back ();
	    }
	}
    }

    else{
	father_y = gt_graph->gt(father).graphics()->y();
	father_h = gt_graph->gt(father).graphics()->h();
	first_y = gt_graph->gt(first_son).graphics()->y();
	last_y = gt_graph->gt(last_son).graphics()->y();
    
        // It is possible that (because of the permutation) the y-coordinate of the first 
        // son is bigger than those of the second son. In this case we change first_y and last_y.
        if(first_y > last_y){
          double h = first_y;
          first_y = last_y;
          last_y = h;
        }

	//check whether we can connect orthogonal
	if((first_y >= father_y - (connect*father_h)/2) &&
	    (last_y <= father_y + (connect*father_h)/2)){
	    end = edge_list.end();

	    for (it = edge_list.begin(); it != end; ++it) {
		e = *it;

		//delete bends
		line = gt_graph->gt(e).graphics()->line();
		if (line.size() > 2) {
		    line.erase (++line.begin(), --line.end());
		}
// 		line.push_back (gt_graph->gt(e).graphics()->line().front());
// 		line.push_back (gt_graph->gt(e).graphics()->line().back());
		gt_graph->gt(e).graphics()->line(line);

		//connect orthogonal
		switch(treealgo->get_direction()){
		case LeftRight:
		  gt_graph->gt(e).edge_nei()->set_EA(
		    GT_Target, GT_Keys::anchor_w);
		  break;
		case RightLeft:
		  gt_graph->gt(e).edge_nei()->set_EA(
		    GT_Target, GT_Keys::anchor_e);
		  break;
		}
	    
		line.back () = gt_graph->gt(e).edge_nei()->
		    convert_anchor_to_coordinates (GT_Target);
		gt_graph->gt(e).graphics()->line (line);
		gt_graph->gt(e).edge_nei()->set_EA_orthogonal(GT_Source);
// 		line.pop_back();
// 		line.pop_back ();
	    }
	}
	
    }
}

