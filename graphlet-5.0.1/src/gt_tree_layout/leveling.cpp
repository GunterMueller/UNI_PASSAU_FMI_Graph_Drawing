/* This software is distributed under the Lesser General Public License */
//***********************************************************//
// leveling.cpp                                              //
//                                                           //
// This file implements the leveling (y_coordinates) of      //
// all nodes.                                                //
//                                                           //
//                                                           //
// Source: /home/br/CVS/graphlet/src/gt_algorithms/          //
//                 tree_layout/leveling.cpp                  //
// Author: Wetzel Sabine                                     //
//                                                           //
// (C) University of Passau 1995-1999, graphlet Project      //
//                                                           //
//***********************************************************//
// $Source: /home/br/CVS/graphlet/src/gt_tree_layout/leveling.cpp,v $
// $Author: himsolt $
// $Revision: 1.5 $
// $Date: 1999/03/05 20:47:08 $
// $Locker:  $
// $State: Exp $

#include <gt_base/Graphlet.h>

#include <GTL/graph.h>
#include <cmath>

#include "tree_structure.h"
#include "leveling.h"
#include "permutation.h"

//**************************************************************//
// predefinitions                                               //
//**************************************************************//
void global_leveling(GT_TreeAlgo* tree, node root);

void determine_max_height(GT_TreeAlgo* tree, vector<HeightVec>* max_height, node father, int level);

void set_global_levels(GT_TreeAlgo* tree, vector<HeightVec>* max_height, node father, int level, double cur_height, double vert_node_node_dist);


void local_leveling(GT_TreeAlgo* tree, node v, double depth);


//**************************************************************//
// function: compute_levels                                     //
// description: This function computes the y-coordinates of     //
//    all nodes (called leveling). The computation depends      //
//    on the request of the user:                               //
//    leveling = global -> the nodes in all subtrees which can  //
//                         be placed on the same level get the  //
//                         same y-coordinate,f.e.               //
//                                                              //
//                                   --             <- 0        //
//                                  |  |                        //
//                                   --                         //
//                                /       \                     //
//                             -          ----      <- 1        //
//                            | |        |    |                 //
//                             -         |    |                 //
//                                       |    |                 //
//                           /  \        |    |                 //
//                                        ----                  //
//                                       /   \                  //
//                         -     -       -    -     <- 2        //
//                        | |   | |     | |  | |                //
//                         -     -       -    -                 //
//                                                              //
//                                                              //
//    leveling = local ->  all sons of one node are on the same //
//                         level, e.g.                          //
//                                                              //
//                 0 ->              --             <- 0        //
//                                  |  |                        //
//                                   --                         //
//                                /       \                     //
//                 1->         -          ----      <- 1        //
//                            | |        |    |                 //
//                             -         |    |                 //
//                           /   \       |    |                 //
//                 2->     -     -       |    |                 //
//                        | |   | |       ----                  //
//                         -     -       /   \                  //
//                                       -    -     <- 2        //
//                                      | |  | |                //
//                                       -    -                 //
//                                                              //
//    The leveling begins with the value 0.0 for the root. The  //
//    coordinates which we compute belong to the lefmost-       //
//    uppermost point of a node. Note, that in the Graphlet     //
//    system, the reference point is always the center of the   //
//    node. But for several reasons (f.e. the computation of    //
//    the contour is easier), we prefere the leftmost-uppermost //
//    point as the reference.                                   //
//**************************************************************//

void GT_TreeAlgo::compute_levels()
{
    node root_node;
    double height = 0.0;
	
    root_node = this->get_root();
    height = this->get_graph()->gt(root_node).graphics()->h();
	
    switch(this->get_leveling()){
	case Global:
	    //set the level of root
	    this->set_node_y_coord(root_node, 0.0);
	    global_leveling(this, root_node);
	    break;

	case Local:
	    //set the level of root
	    this->set_node_y_coord(root_node, 0.0);
	    //depth = height of root
	    local_leveling(this, root_node, height);
	    break;
    }
}



//**************************************************************//
// function: global_leveling                                    //
// description: This function computes the global leveling.     //
//       This function is not so simple as the local leveling.  //
//       Reason: We cannot compute the y-coords in one step     //
//               because we do not know the height of all nodes //
//               of the same level when traversing recursively  //
//               the tree.                                      //
//       The determination of the y-coords is done in two steps://
//       (1) Determine the maximal height of all nodes in one   //
//           level. The heights are stored in the array         //
//           max_height. The array can maximally be as long as  //
//           the number od nodes in the graph.                  //
//           Therefore, the root has level 1, the sons of the   //
//           root level 2 and so on. (determine_max_height)     //
//       (2) A second recursive traverse through the tree.      //
//           The counter level counts the level in which the    //
//           node must be placed and the y-coordinate is the    //
//           entry in the array + the coordinate of the parent- //
//           level + node_node_distance. (set_global_levels)    //
//                                                              //
// more information: see 'compute_levels'                       //
//**************************************************************//
void global_leveling(GT_TreeAlgo* tree, node root)
{
    int i;
    GT_Graph* gt_graph;
    graph* l_graph;
    double vert_node_node = 0.0;

    gt_graph = tree->get_graph();
    l_graph = gt_graph->attached();
    vert_node_node = (double)(tree->get_vert_node_node_dist());

    //get memory for the array
    vector<HeightVec> max_height(l_graph->number_of_nodes()+1);
    //SABINE: ueberpruefe, ob LEDA das array schon auf 0.0 initialisiert
    for (i=0; i<= l_graph->number_of_nodes(); i++){
	max_height[i].node_height = 0.0;
	max_height[i].max_chan_height = 0.0;
    }

    //fill the max_height array
    determine_max_height(tree, &max_height, root, 1);

    //determine the levels
    set_global_levels(tree, &max_height, root, 1, 0.0, vert_node_node);
}

//*************************************************************//
// function: determine_max_height                              //
// description: Determine the maximal height of all nodes in   //
//              one level. For more information see 'global_   //
//              leveling'.                                     //
//*************************************************************//
void determine_max_height(GT_TreeAlgo* tree, vector<HeightVec>* max_height, node father, int level)
{
    GT_Graph* gt_graph;
    double height_of_father = 0.0;
    int nr_of_sons = 0;
    double vert_node_node = 0.0;
    double channel_dist = 0.0;

    double distance = 0.0;
    double channel_height = 0.0;

    edge e;
    node son;

    gt_graph = tree->get_graph();

    height_of_father = gt_graph->gt(father).graphics()->h();
    vert_node_node = (double)(tree->get_vert_node_node_dist());
    distance = vert_node_node;
	
    if(tree->get_routing() == Orthogonal){
	nr_of_sons = tree->get_order(father).size();
	//reduce the number of channels (which is normally equivalent to
	//the number of sons) if Median is choosen or if the number of
	//sons is equal to 2.
	if(tree->get_father_place() == Median){
//	    nr_of_sons = (int)floor(nr_of_sons/2);
	    nr_of_sons = nr_of_sons/2;
	}
	else{
	  if(nr_of_sons == 2){
	    nr_of_sons = 1;
	  }
	}
	channel_dist = (double)(tree->get_channel_dist());
		
	//reserve channel_height pixels for the distance between 
	//the channels. Note that that the pixel for the edge
	//is included in the channel_heigth
	channel_height = (nr_of_sons - 1)*channel_dist;
	distance = vert_node_node + channel_height;

    }
    if((*max_height)[level].node_height +
	(*max_height)[level].max_chan_height <
	height_of_father + distance){
	(*max_height)[level].node_height = height_of_father;
	(*max_height)[level].max_chan_height = distance;
    }


    //determine the heigth of all sons!
     tree_forall_sorted_out_edges(e, father,tree){
	son = father.opposite(e);
	determine_max_height(tree, max_height, son, level+1);
    }	
}

//*************************************************************//
// function: set_global_levels                                 //
// description: Set the y-coordinate of all nodes, depending   //
//         to the level of the node and the value in           //
//         max_height. For more information see 'global_       //
//         veling'.                                            //
//*************************************************************//
void set_global_levels(GT_TreeAlgo* tree, vector<HeightVec>* max_height, node father, int level, double cur_height, double vert_node_node_dist)
{
    edge e;
    node son;
    double height_father = 0.0;

    height_father = tree->get_graph()->gt(father).graphics()->h();
    
    //set the y-coord of the father-node
    switch(tree->get_orientation()){
	case Top:
	    tree->set_node_y_coord(father, cur_height);
	    break;
	case Middle:
	    tree->set_node_y_coord(father, cur_height +
		((*max_height)[level].node_height -
		    height_father)/2 );
	    break;
	case Bottom:
	    tree->set_node_y_coord(father, cur_height +
		(*max_height)[level].node_height -
		height_father);
	    break;
    }    
	
    cur_height = cur_height + (*max_height)[level].node_height +
	(*max_height)[level].max_chan_height;
	
     tree_forall_sorted_out_edges(e, father,tree){
	son = father.opposite(e);
	set_global_levels(tree, max_height, son, level+1, cur_height,
	    vert_node_node_dist);
    }		
}


	

//**************************************************************//
// function: local_leveling                                     //
// description: This function computes the local leveling.      //
// more information: see 'compute_levels'                       //
//                                                              //
//           ---                                                //
//          |   |                                               //
//           ---                                                //
//           ||              node_node/2                        //
//        ---  ----          channels                           //
//       |         |         node_node/2                        //
//     ----        --                                           //
//    |    |      |  |                                          //
//     ----        --                                           //
//**************************************************************//
void local_leveling(GT_TreeAlgo* tree, node v, double depth)
{
    GT_Graph* gt_graph;
    graph* l_graph;
    int nr_of_sons = 0;
    edge e;
    node son;
    double vert_node_node = 0.0;
    double channel_dist = 0.0;
    double height_son = 0.0;
    double channel_height = 0.0;
    double distance = 0.0;
    double max_height = 0.0;

    gt_graph = tree->get_graph();
    l_graph = gt_graph->attached();

    nr_of_sons = tree->get_order(v).size();

    //reduce the number of channels (which is normally equivalent to
    //the number of sons) if Median is choosen or if the number of
    //sons is equal to 2.
    if(tree->get_father_place() == Median){
//	nr_of_sons = (int)floor(nr_of_sons/2);
	nr_of_sons = nr_of_sons/2;
    }
    else{
      if(nr_of_sons == 2){
	nr_of_sons = 1;
      }
    }
	
    vert_node_node = (double)(tree->get_vert_node_node_dist());
    channel_dist = (double)(tree->get_channel_dist());

    //determine the maximum of the heights of all sons
     tree_forall_sorted_out_edges(e, v, tree){
	son = v.opposite(e);
	height_son = gt_graph->gt(son).graphics()->h();
	if(height_son > max_height){
	    max_height = height_son;
	}
    }
	

    tree_forall_sorted_out_edges(e, v, tree){
	son = v.opposite(e);
	height_son = gt_graph->gt(son).graphics()->h();

	distance = vert_node_node;
		
	//we only have to reserve channels if we have to make an
	//orthogonal drawing!
	if(tree->get_routing() == Orthogonal){
	  //reserve channel_dist pixels for the distance between 
	  //the channels
 	    channel_height = (nr_of_sons - 1)*channel_dist;
	    distance = vert_node_node + channel_height;
	}
	//set y_coord
	switch(tree->get_orientation()){
	    case Top:
		tree->set_node_y_coord(son, depth + distance);
		distance = distance + height_son;
		break;
	    case Bottom:
		tree->set_node_y_coord(son, depth + distance +
		    max_height - height_son);
		distance = distance + max_height;
		break;
	    case Middle:
		tree->set_node_y_coord(son, depth + distance +
		    (max_height - height_son)/2);
		distance = distance + max_height;
		break;
	}
		
	//recursive call of function local_leveling
	local_leveling(tree, son, depth+distance);
    }
}



//*************************************************************//
// function: compute_deepest_y_coord                           //
// description: This function sets the y_coord of the deepest  //
//              reachable node beginning at v. This infor-     //
//              mation is needed for the computation of the    //
//              contour. An example is given below:            //
//                             --                              //
//                            |1 |                             //
//                             --                              //
//                           /   \                             //
//                         -       ------                      //
//                        |2|     |  3   |                     //
//                 y1 ->   -      |      |                     //
//                                 ------                      //
//                                 /   \                       //
//                                -    --                      //
//                               |4|  |5 |                     //
//                               | |   --      <- y_2          //
//                                -            <- y_3          //
//                                                             //
//              The coordinate for node 1 is y3,               //
//                                      2    y1,               //
//                                      3    y3,               //
//                                      4    y3,               //
//                                      5    y2.               //
//*************************************************************//
void GT_TreeAlgo::compute_deepest_y_coord(node father)
{
    GT_Graph* gt_graph;
    graph* l_graph;
    edge e;
    node son;
	
    double depth = 0.0;
	
    gt_graph = this->get_graph();
    l_graph = gt_graph->attached();
	
    //if the father node has no son (so it is a leaf)
    if(this->get_order(father).empty() == true){
	//depth is the y-coord of the node + height 
	depth = get_node_y_coord(father) +
	    gt_graph->gt(father).graphics()->h();
    }
    else{
	//determine the maximum of the depth of the sons
	tree_forall_sorted_out_edges(e, father, this){
	    son = father.opposite(e);
	    compute_deepest_y_coord(son);
	}
	depth = 0.0;
	tree_forall_sorted_out_edges(e, father, this){
	    son = father.opposite(e);
	    if(depth < this->get_deepest_y(son)){
		depth = this->get_deepest_y(son);
	    }
	}
    }
    set_deepest_y(father, depth);		
}
