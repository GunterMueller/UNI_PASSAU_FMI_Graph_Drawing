/* This software is distributed under the Lesser General Public License */
//***********************************************************//
// tree_structure.h                                          //
//                                                           //
// This file implements the data structures of the tree      //
// algorithm.                                                //
//                                                           //
//                                                           //
// Source: /home/br/CVS/graphlet/src/gt_algorithms/          //
//                 tree_layout/tree_structure.h              //
// Author: Wetzel Sabine                                     //
//                                                           //
// (C) University of Passau 1995-1999, graphlet Project      //
//                                                           //
//***********************************************************//
// $Source: /home/br/CVS/graphlet/src/gt_tree_layout/tree_structure.h,v $
// $Author: himsolt $
// $Revision: 1.4 $
// $Date: 1999/03/05 20:47:34 $
// $Locker:  $
// $State: Exp $

#ifndef TREE_STRUCTURE_H
#define TREE_STRUCTURE_H

#include <gt_base/Graphlet.h>
#include <gt_base/Line.h>
#include <gt_base/Point.h>

#include <gt_tcl/Tcl_Graph.h>
#include <gt_tcl/Tcl_Algorithm.h>

#include <GTL/node_map.h>
#include <vector>

//********************************************************************//
//      define if debug information is shown or not                   //
//********************************************************************//

//#define SHOW_NODE_EDGE
#undef SHOW_NODE_EDGE
//#define SHOW_NODE_EDGE_EXT
#undef SHOW_NODE_EDGE_EXT
#define NODE_EDGE_SHIFT
//#undef NODE_EDGE_SHIFT

//********************************************************************//
//                         predeclarations                            //
//********************************************************************//
class GT_TreeAlgo;
class TreeInfo;
class Contour;
class ConPoint;

//********************************************************************//
// class:       TreeAlgo                                              //
// description: TreeAlgo is the class containing all options of the   //
//              tree algorithm, the graph as a GT_Graph and a         //
//              mapping of TreeInfo to all nodes.                     //
//              The memberfunctions are set- and get-functions for    //
//              the options and the graph and the functions which     //
//              represent the essential parts of the tree algorithm.  //
//                                                                    //
//              options: leveling, orientation, direction, routing,   //
//                       father_place, permutation, node_node_dist,   //
//                       node_edge_dist                               //
//              root: the root of the tree                            //
//              my_graph: the GT_Graph representing the tree          //
//              my_node_info: a mapping to every node in the tree;    //
//                   contains informations like x_coord, y_coord, ... //
//              con_point_list: list of all ConPoints appearing in the//
//                   contours. The problem was, that one ConPoint can //
//                   appear in several Contours. Therefore, we had    //
//                   some problems in deleting the ConPoints (free    //
//                   storage). If we insert a realy new ConPoint in   //
//                   any Contour, it is also inserted in the con_point//
//                   _list.                                           //
//********************************************************************//

class GT_TreeAlgo
{
private:
    int leveling;
    int orientation;
    int direction;
    int routing;
    int father_place;
    int permutation;
    int vert_node_node_dist;
    int hor_node_node_dist;
    double node_edge_dist;
    int channel_dist;
    //double for internal use, int for user
    double edge_connection;
    int bend_reduction;
    double edge_connection_for_bend;

    node root;
	
    GT_Graph* my_graph;
	
    node_map<TreeInfo*> my_node_info;
	
    list<ConPoint*> con_point_list;

    list<Contour*> list_of_contours;
	
	
public:
    // constructor //
    GT_TreeAlgo();

    // destructor //
    virtual ~GT_TreeAlgo();

    // set-functions //
    void set_options(int lev, int orien, int dir, int rout, int father, int perm, int vert_node_node, int hor_node_node, int node_edge, int channel, int connect, int bend, int connect_bend);
    void set_root(node new_root);
    void set_graph (GT_Graph* new_graph);
    void set_node_y_coord(node v, double y);
    void set_node_x_coord(node v, double x);
    void set_order(node v, vector<edge> ord);
    void set_deepest_y(node v, double depth);

    void insert_con_point(ConPoint* con);
    void insert_contour(Contour* contour);
	

    // get_functions //
    int get_leveling();
    int get_orientation();
    int get_direction();
    int get_routing();
    int get_father_place();
    int get_permutation();
    int get_vert_node_node_dist();
    int get_hor_node_node_dist();
    int get_channel_dist();
    double get_edge_connection();
    double get_node_edge_dist();
    int get_bend_reduction();
    double get_edge_connection_for_bend();
    node get_root();
    GT_Graph* get_graph();
    TreeInfo* get_node_info(node v);
    double get_node_y_coord(node v);
    double get_node_x_coord(node v);
    vector<edge> get_order(node v);
    double get_deepest_y(node v);

    // essential tree-functions //
    virtual void compute_levels();
    virtual void compute_deepest_y_coord(node father);
    virtual void tree_algorithm();
    virtual void compute_order_of_sons();
    virtual void shift_subtrees(node father);
    virtual void father_placement(node father);
    virtual bool determine_node_edge_intersection(node father);
    virtual void shift_subtrees_node_edge(node father, int counter);
    virtual void compute_edgeanchor(node father);
    virtual void orthogonal_routing(node father);
    virtual void initialize_contour(node leaf);
    virtual void compute_contour(node father);
    virtual void transform_gt_coordinates();

    //update-functions //
    void update_x_coords(node father, double offset);
    void update_edges_to_sons(node father, double offset);

    //print-functions //
    void print_treeinfo();
};

//********************************************************************//
// class:       TreeInfo                                              //
// description: TreeInfo is the class containing the informations for //
//              every node of a LEDA-Graph.                           //
//              left_/right_contour: is the outline of a subtree      //
//                        beginning at the specified node;            //
//  		         	  important when merging two subtrees.        //
//              x_coord : the offset which we have to add to get the  //
//                        x-coordinate of the node and its subtrees   //
//              SABINE: vielleicht brauche ich das gar nicht!!        //
//              number_of_sons: number of sons of this node (in the   //
//                        tree)                                       //
//                                                                    //
//              area_of_subtree: area of the subtree including begin- //
//                        ning at this node (inclusive the node);     //
//                        computed by width * length of the smallest  //
//                        rectangle containing the subtree            //
//              deepest_y_coord:                                      //
//                     euclidian depth of a tree beginning at this    //
//                     node.                                          //
//                     The value is set by the function compute_      //
//                     deepest_y_coord. for more information look     //
//                     in the description of this function.           //
//              edge_order:                                           //
//                     the order of the outgoing edges around this    //
//                     node.                                          //
//              edgeanchor: this is the edgeanchor of all sons of     //
//                     this node. note, that we save it with          //
//                     absolute coordinates (it is simpler to de-     //
//                     termine the contour then, ...)! Later, we      //
//                     have to transform it into an edgeanchor with   //
//                     coordinates relative to the center of the node.//
//********************************************************************//

class TreeInfo
{
private:
    Contour* left_contour;
    Contour* right_contour;
	
    double x_coord;
    double y_coord;

    GT_Point edgeanchor;

    int number_of_sons;
    double area_of_subtree;
    double deepest_y_coord;

    vector<edge> edge_order;
	
public:

    //constructors
    TreeInfo();
    //heute
    //TreeInfo(Contour left, Contour right);
	
    //destructor
    virtual ~TreeInfo();
	
    //"get"-routines
    Contour* get_left_contour();
    Contour* get_right_contour();
    double get_x_coord();
    double get_y_coord();
    GT_Point get_edgeanchor();
    int get_nr_of_sons();
    double get_area_of_subtree();
    double get_deepest_y_coord();
    vector<edge> get_order_info();
	
    //"set"-routines
    void set_left_contour(Contour* left);
    void set_right_contour(Contour* right);
    void set_x_coord(double x);
    void set_y_coord(double y);
    void set_edgeanchor(GT_Point anchor);
    void set_nr_of_sons(int number);
    void set_area_of_subtree(double area);
    void set_deepest_y_coord(double dep);
    void set_order_info(vector<edge> ord);
	
    //print-routines and "information"-routines
	
    void print_node_order(GT_Graph gt_graph);
};



//*************************************************************//
// class: Contour                                              //
// description: Contour is a collection of all contour points. //
//       It is used for the left and the right contour of      //
//       every subtree in the class TreeInfo.                  //
//       The member functions are explained (if necessary) in  //
//       tree_structure.cpp.                                   //
//       Note that a ConPoint can appear in several Contour-   //
//       lists (for better performance). Therefore, we save    //
//       the ConPoint in an additional list (con_point_list of //
//       GT_TreeAlgo) in order to be able to free the memory.  //
//*************************************************************//

class Contour
{
private:
    list<ConPoint*> contour_list;

public:
    // constructor //
    Contour();

    // destructor //
    virtual ~Contour();
	
    // get-functions //
    list<ConPoint*> get_contour();
    // is the first element if old is the last element in the list
//     ConPoint* get_succ(ConPoint* old);
//     ConPoint* get_succ(list<ConPoint*>::const_iterator& it);
    GT_Point get_intersection_point(GT_Point p1, ConPoint p2);
    //get the rest of the contour beginning at the y-coord
    Contour* get_rest_after_y(GT_TreeAlgo* treealgo, double y);

    // set-functions //
    void set_contour(list<ConPoint*> contour);
    void append_contour_point(ConPoint* con);
    void push_contour_point(ConPoint* con);
    // 	Contour* append_contour(Contour* rest);
    void append_contour(Contour* rest);

    // update-functions //
    void update_contour(double offset);

    //print-functions
    void print_contour();
};


//********************************************************************//
// class:       ConPoint                                              //
// description: ConPoint is the class containing one contour point    //
//              and its correspoinding point. There are three         //
//              different kinds of contour points. The first is:      //
//              (1)        x---           x: contour point            //
//                         |   |          o: corresponding point      //
//                         |   |                                      //
//                         o---                                       //
//                                                                    //
//              A second example would be:                            //
//              (2)         ---                                       //
//                         |   |     The point is contour point and   //
//                         |   |     corresponding point at the same  //
//                         x---      time.                            //
//                                                                    //
//                  We have choosen the same point for the corres-    //
//                  ponding point because of the fact                 //
//                                                                    //
//                                                                    //
//                                                                    //
//              The third possibility for the appearance of a contour //
//              if the following:                                     //
//              (3)         ---                                       //
//                         |   |                                      //
//                         |   |                                      //
//                          -x-                                       //
//                                                                    //
//                         /                                          //
//                      --o--                                         //
//                     |     |                                        //
//                      -----                                         //
//                                                                    //
//                  Attention: the contour point of the upper node    //
//                  is not always the middle of the side of the node, //
//                  but the intersection of the line (combining the   //
//                  focuspoint and the middle of the lower node) and  //
//                  and the outline of the node (which may be a       //
//                  rectangle, circle, ...).                         //
//                                                                    //
//              The reason for saving the contour like that is that   //
//              we have to compare two contours. We need less time    //
//              for comparing them in this data structure.            //
//              In the first case (1), the corresponding point is     //
//              not interesting, because they have the same x-coord.  //
//              In the third case (3), we have to combine the two     //
//              points by a line segment and determine the inter-     //
//              section of this line with the horizontal line through //
//              the contour-point of the other contour:               //
//                                                                    //
//                          ---                                       //
//                         |   |                                      //
//                         |   |                                      //
//                          -x-                                       //
//      ---------x----------+----------------                         //
//            |  |         /                                          //
//             --       --o--                                         //
//                     |     |                                        //
//                      -----                                         //
//              Then, we determine the distance between the two       //
//              points which must be bigger than the node-edge-       //
//              distance (or node_node_distance respectively).        //
//                                                                    //
//              point_type  = F if the line segment is a part of the  //
//                              edge (3)                              //
//                          = T if the line is part of the node (1+2) //
//                                                                    //
//              the_item: the list_item corresponding to this         //
//                    ConPoint. Needed because of a bug in LEDA's     //
//                    search-function (see comment nearby get_succ).  //
//                                                                    //
//********************************************************************//
class ConPoint
{
private:
    GT_Point con_point;
    GT_Point cor_point;
    int point_type;
    // 	list_item the_item;
public:
    // constructor //
    ConPoint();

    // destructor //
    virtual ~ConPoint();

    // get-functions //
    GT_Point get_con_point();
    GT_Point get_cor_point();
    double get_con_x();
    double get_con_y();
    double get_cor_x();
    double get_cor_y();
    int get_point_type();
    //get the line between the contour point and the corresponding point
    GT_Line get_line();
    // 	list_item get_list_item();

    // set-functions //
    void set_con_point(double x, double y);
    void set_con_point(const GT_Point& p);
    void set_cor_point(double x, double y);
    void set_cor_point(const GT_Point& p);
    void set_point_type(int type);
    void set_points(double con_x, double con_y, double cor_x, double cor_y);
    // 	void set_list_item(list_item it);

    // update-functions //
    void update_conpoint(double offset);
    void update_corpoint(double offset);
};


// output-functions for LEDA //
ostream& operator <<(ostream& out, const ConPoint& p);
istream& operator >> (istream& in , const ConPoint& p);


//enumeration types for switches
enum Leveling{
    Global,
    Local
};
enum Orientation{
    Top,
    Middle,
    Bottom
};
enum Direction{
    LeftRight,
    TopBottom,
    RightLeft,
    BottomTop
};
enum Routing{
    Straightline,
    Orthogonal
};
enum FatherPlace{
    Center,
    Barycenter,
    Median
};
enum Permutation{
    KeepIt,
    MirrorIt,
    Insertion
};
enum BendReduction{
    Enabled,
    Disabled
};

enum PointType{
    NodeBegin,
    NodeEnd,
    Edge,
    Bend
};

#endif

