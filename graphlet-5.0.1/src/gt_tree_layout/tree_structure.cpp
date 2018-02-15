/* This software is distributed under the Lesser General Public License */
//***********************************************************//
// tree_structure.cpp                                        //
//                                                           //
// This file implements the data structures of the tree      //
// algorithm.                                                //
//                                                           //
//                                                           //
// Source: /home/br/CVS/graphlet/src/gt_algorithms/          //
//                 tree_layout/tree_structure.cpp            //
// Author: Wetzel Sabine                                     //
//                                                           //
// (C) University of Passau 1995-1999, graphlet Project      //
//                                                           //
//***********************************************************//
// $Source: /home/br/CVS/graphlet/src/gt_tree_layout/tree_structure.cpp,v $
// $Author: himsolt $
// $Revision: 1.7 $
// $Date: 1999/03/05 20:47:32 $
// $Locker:  $
// $State: Exp $

#include "tree_structure.h"
 #include "permutation.h"
#include <iostream>
// #include "LEDA/vector.h"

//*******************************************************************//
//*******************************************************************//
//*                                                                 *//
//*               Member functions of class GT_TreeAlgo             *//
//*                                                                 *//
//*******************************************************************//
//*******************************************************************//


//*******************************************************************//
//                             constructor                           //
//*******************************************************************//
GT_TreeAlgo::GT_TreeAlgo()
{
    leveling = 0;
    orientation = 0;
    direction = 0;
    routing = 0;
    father_place = 0;
    permutation = 0;
    permutation = 0;
    vert_node_node_dist = 10;
    hor_node_node_dist = 10;
    node_edge_dist = 0.5;
    channel_dist = 0;
    edge_connection = 0.7;
    bend_reduction = 0;
    edge_connection_for_bend = 0.7;

    my_graph = 0;
}

//*******************************************************************//
//                             destructor                            //
//*******************************************************************//
GT_TreeAlgo::~GT_TreeAlgo()
{
    node v;

    ConPoint* con;
    Contour* contour;

    forall_nodes(v, *(my_graph->attached())){
	delete(my_node_info[v]);
	my_node_info[v] = 0;
    }

    while (!con_point_list.empty()) {
	con = con_point_list.front();
	con_point_list.pop_front();
	delete(con);
    }
	
    while (!list_of_contours.empty ()) {
	contour = list_of_contours.front();
	list_of_contours.pop_front();
	delete(contour);
    }
}

//*******************************************************************//
//                             set-functions                         //
//*******************************************************************//
void GT_TreeAlgo::set_options(int lev, int orien, int dir, int rout, int father, int perm, int vert_node_node, int hor_node_node, int node_edge, int channel, int connect, int bend, int connect_bend)
{
    leveling = lev;
    orientation = orien;
    direction = dir;
    routing = rout;
    father_place = father;
    permutation = perm;
    vert_node_node_dist = vert_node_node;
    hor_node_node_dist = hor_node_node;
    node_edge_dist = (double)node_edge/100.0;
    channel_dist = channel;
    edge_connection = (double)connect/100.0;
    bend_reduction = bend;
    edge_connection_for_bend = (double)connect_bend/100.0;
}
void GT_TreeAlgo::set_root(node new_root)
{
    root = new_root;
}

void GT_TreeAlgo::set_graph (GT_Graph* new_graph)
{
    node n;
    graph *l_graph;
	
    my_graph = new_graph;
    //set tree_info for all nodes
    l_graph = new_graph->attached();
    my_node_info.init(*l_graph);

    forall_nodes(n, *l_graph){
	my_node_info[n] = new TreeInfo();
    }
}

void GT_TreeAlgo::set_node_y_coord(node v, double y)
{
    TreeInfo* info;

    info = this->get_node_info(v);
    info->set_y_coord(y);
}

void GT_TreeAlgo::set_node_x_coord(node v, double x)
{
    TreeInfo* info;

    info = this->get_node_info(v);
    info->set_x_coord(x);
}

void GT_TreeAlgo::set_deepest_y(node v, double depth)
{
    TreeInfo* info;

    info = this->get_node_info(v);
    info->set_deepest_y_coord(depth);
}

void GT_TreeAlgo::set_order(node v, vector<edge> ord)
{
    TreeInfo* info;

    info = this->get_node_info(v);
    info->set_order_info(ord);
}

void GT_TreeAlgo::insert_con_point(ConPoint* con)
{
    con_point_list.push_front (con);
}

void GT_TreeAlgo::insert_contour(Contour* contour)
{
    list_of_contours.push_front (contour);
}

//*******************************************************************//
//                             get-functions                         //
//*******************************************************************//
int GT_TreeAlgo::get_leveling()
{
    return leveling;
}

int GT_TreeAlgo::get_orientation()
{
    return orientation;
}

int GT_TreeAlgo::get_direction()
{
    return direction;
}

int GT_TreeAlgo::get_routing()
{
    return routing;
}

int GT_TreeAlgo::get_father_place()
{
    return father_place;
}

int GT_TreeAlgo::get_permutation()
{
    return permutation;
}

int GT_TreeAlgo::get_vert_node_node_dist()
{
    return vert_node_node_dist;
}

int GT_TreeAlgo::get_hor_node_node_dist()
{
    return hor_node_node_dist;
}

double GT_TreeAlgo::get_node_edge_dist()
{
    return node_edge_dist;
}

int GT_TreeAlgo::get_channel_dist()
{
    return channel_dist;
}

double GT_TreeAlgo::get_edge_connection()
{
    return edge_connection;
}

int GT_TreeAlgo::get_bend_reduction()
{
    return bend_reduction;
}

double GT_TreeAlgo::get_edge_connection_for_bend()
{
    return edge_connection_for_bend;
}

node GT_TreeAlgo::get_root()
{
    return root;
}

GT_Graph* GT_TreeAlgo::get_graph()
{
    return my_graph;
}

TreeInfo* GT_TreeAlgo::get_node_info(node v)
{
    return my_node_info[v];
}


double GT_TreeAlgo::get_node_y_coord(node v)
{
    TreeInfo* info;

    info = this->get_node_info(v);
    return info->get_y_coord();
}

double GT_TreeAlgo::get_node_x_coord(node v)
{
    TreeInfo* info;

    info = this->get_node_info(v);
    return info->get_x_coord();
}

vector<edge> GT_TreeAlgo::get_order(node v)
{
    TreeInfo* info;

    info = this->get_node_info(v);
    return info->get_order_info();
}

double GT_TreeAlgo::get_deepest_y(node v)
{
    TreeInfo* info;

    info = this->get_node_info(v);
    return info->get_deepest_y_coord();
}


//*******************************************************************//
//                     essential tree-functions                      //
//*******************************************************************//
// this functions are defined in their corresponding files!

// void GT_TreeAlgo::compute_levels();
// void GT_TreeAlgo:: tree_algorithm();
// void GT_TreeAlgo::compute_optimal_permutation(node father);
// void GT_TreeAlgo::shift_x_coordinates();
// void GT_TreeAlgo::father_placement(node father);
// void GT_TreeAlgo::compute_edgeanchor(node father);
// void GT_TreeAlgo::();


//*******************************************************************//
//                          update-functions                         //
//*******************************************************************//
void GT_TreeAlgo::update_x_coords(node father, double offset)
{
    edge e;
    node son;
    graph* l_graph;

    l_graph = my_graph->attached();
    tree_forall_sorted_out_edges(e, father, this){
	son = father.opposite(e);
	this->update_x_coords(son, offset);
    }
    this->set_node_x_coord(father, this->get_node_x_coord(father) + offset);

    //update the contour
    // 	this->get_node_info(father)->get_left_contour()->update_contour(offset);
    // 	this->get_node_info(father)->get_right_contour()->update_contour(offset);
    // 	cout << "----------**************----------------" << endl;
    // 	cout << "linke Contour" << endl;
    // 	this->get_node_info(father)->get_left_contour()->print_contour();
    // 	cout << endl << "rechte Contour" << endl;
    // 	this->get_node_info(father)->get_right_contour()->print_contour();
    // 	cout << "----------**************----------------" << endl;
}

void GT_TreeAlgo::update_edges_to_sons(node father, double offset)
{
    GT_Graph* gt_graph;
    graph* l_graph;
    edge e;
    GT_Polyline line;
    GT_Point bend;
    GT_Polyline::iterator it;
    GT_Polyline::iterator end;

    gt_graph = this->get_graph();
    l_graph = gt_graph->attached();

    tree_forall_sorted_out_edges(e, father, this){
	line = gt_graph->gt(e).graphics()->line();
	end = line.end();

	for (it = line.begin(); it != end; ++it) {
	    bend = *it;
	    bend.x(bend.x()+offset);
	    *it = bend;
	}

	gt_graph->gt(e).graphics()->line(line);		
	this->update_edges_to_sons(father.opposite(e), offset);
    }
}


//*******************************************************************//
//                          print-functions                          //
//*******************************************************************//
void GT_TreeAlgo::print_treeinfo()
{
    graph* l_graph;
    node v;

    l_graph = my_graph->attached();

    //  	cout << "TREEINFO:" << endl;

    if(this == 0){
	return;
    }

    forall_nodes(v, *l_graph){
	if(this->get_node_info(v) != 0){
// 	    cout << "x-Koordinate von Knoten mit Markierung ";
// 	    cout << my_graph->gt(v).label();
// 	    cout << " ist ";
// 	    cout << this->get_node_x_coord(v);
// 	    cout << endl;
			
// 	    cout << "y-Koordinate von Knoten mit Markierung ";
// 	    cout << my_graph->gt(v).label();
// 	    cout << " ist ";
// 	    cout << this->get_node_y_coord(v);
// 	    cout << endl;

	    vector<edge>::iterator it;
	    cout << "Ordnung der Soehne des Knotens " << my_graph->gt(v).label() << ": " << endl;
	    vector<edge> edge_order = this->get_order(v);
	    for (it = edge_order.begin(); it != edge_order.end(); ++it){
	      cout << my_graph->gt((*it).target()).label() << "  ";
	    }
	    cout << endl;
			
	    //  			cout << "Linke Contour von Knoten ";
	    //  			cout << my_graph->gt(v).label();
	    //  			cout << ": ";
	    //  			this->get_node_info(v)->get_left_contour()->print_contour();	
	    //  			cout << endl;

	    //  			cout << "Rechte Contour von Knoten ";
	    //  			cout << my_graph->gt(v).label();
	    //  			cout << ": ";
	    //  			this->get_node_info(v)->get_right_contour()->print_contour();	
	    cout << endl;
	}
    }
}

		


//*******************************************************************//
//*******************************************************************//
//*                                                                 *//
//*               Member functions of class TreeInfo                *//
//*                                                                 *//
//*******************************************************************//
//*******************************************************************//


//*******************************************************************//
//                             constructors                          //
//*******************************************************************//
TreeInfo::TreeInfo()
{
    left_contour = 0;
    right_contour = 0;
    x_coord = 0.0;
    y_coord = 0.0;
    number_of_sons = 0;
    area_of_subtree = 0.0;
    deepest_y_coord = 0.0;
}

//*******************************************************************//
//                             destructor                            //
//*******************************************************************//
TreeInfo::~TreeInfo()
{
}

//*******************************************************************//
//                             "get"-routines                        //
//*******************************************************************//

Contour* TreeInfo::get_left_contour()
{
    return left_contour;
}


Contour* TreeInfo::get_right_contour()
{
    return right_contour;
}

double TreeInfo::get_x_coord()
{
    return x_coord;
}

double TreeInfo::get_y_coord()
{
    return y_coord;
}

GT_Point TreeInfo::get_edgeanchor()
{
    return edgeanchor;
}

int TreeInfo::get_nr_of_sons()
{
    return number_of_sons;
}

double TreeInfo::get_area_of_subtree()
{
    return area_of_subtree;
}

double TreeInfo::get_deepest_y_coord()
{
    return deepest_y_coord;
}

vector<edge> TreeInfo::get_order_info()
{
  return edge_order;
}


//*******************************************************************//
//                             "set"-routines                        //
//*******************************************************************//

void TreeInfo::set_left_contour(Contour* left)
{
    left_contour = left;
}

void TreeInfo::set_right_contour(Contour* right)
{
    right_contour = right;
}

void TreeInfo::set_x_coord(double x)
{
    x_coord = x;
}

void TreeInfo::set_y_coord(double y)
{
    y_coord = y;
}

void TreeInfo::set_edgeanchor(GT_Point anchor)
{
    edgeanchor = anchor;
}

void TreeInfo::set_nr_of_sons(int number)
{
    number_of_sons = number;
}

void TreeInfo::set_area_of_subtree(double area)
{
    area_of_subtree = area;
}

void TreeInfo::set_deepest_y_coord(double dep)
{
    deepest_y_coord = dep;
}

void TreeInfo::set_order_info(vector<edge> ord)
{
  edge_order = ord;
}



//*******************************************************************//
//*******************************************************************//
//*                                                                 *//
//*               Member functions of class Contour                 *//
//*                                                                 *//
//*******************************************************************//
//*******************************************************************//


//*******************************************************************//
//                             constructor                           //
//*******************************************************************//
Contour::Contour()
{
}

//*******************************************************************//
//                             destructor                            //
//*******************************************************************//
Contour::~Contour()
{
    // we save the ConPoints in a special list for better deletion.
    // (because one ConPoint can be used in several Contours)
}

//*******************************************************************//
//                             "get"-routines                        //
//*******************************************************************//
list<ConPoint*> Contour::get_contour()
{
    return contour_list;
}

// the old get_succ-function is not working because of a bug in LEDA's
// "search" function.
// Example: The first call of get_succ had the following output:
//               OLD: 0x351820
//               old item 0x2bd360
//               Succ 0x2bd36c
//               new conpoint 0x351940
//          The second call was with the new conpoint of the first call, but:
//               OLD: 0x351940
//               old item 0x2bd360
//               Succ 0x2bd36c
//               new conpoint 0x351940
//          The old item is in both cases the same but OLD was different.
//          I also checked the list. It was ok and every list member was
//          different to each other.
//          We save now the list_item in the ConPoint-Class to avoid the
//          search function.
//
// This is not necessary any more -> You have to define the compare
// function as we defined it below, then it works.
// ConPoint* Contour::get_succ(ConPoint* old)
// {
//     list<ConPoint*>::iterator it = contour_list.search (old);
//     return get_succ (it);
// //     return contour_list.contents(contour_list.cyclic_succ(contour_list.search(old)));
// }

// ConPoint* Contour::get_succ(list<ConPoint*>::const_iterator it)
// {
//     ++it;

//     if (it == contour_list.end()) {
// 	it = contour_list.begin ();
//     }
    
//     return *it;
//     return contour_list.contents(contour_list.cyclic_succ(it));
// }

// 
// RAITNER:
// ========
// I think/hope this will not be used anymore

// ConPoint* Contour::get_succ(ConPoint* old)
// {
// 	list_item old_item = old->get_list_item();
// 	return contour_list.inf(contour_list.cyclic_succ(old_item));
// }


//*************************************************************//
// function:                                                   //
// description: determines the intersection point x between the//
//              horizontal line through p1 and the line        //
//              connecting con_point and cor_point of p2:      //
//                                                             //
//                                   o p2.con_point            //
//                 p1               /                          //
//                 o---------------x---------------------      //
//                                /                            //
//                               /                             //
//                              o p2.cor_point                 //
//                                                             //
//                                                             //
//*************************************************************//
GT_Point Contour::get_intersection_point(GT_Point p1, ConPoint p2)
{
    GT_Point p;
	
    GT_Point con = p2.get_con_point();
    GT_Point cor = p2.get_cor_point();

//     cout << "Vergleiche Punkt (" << p1.x() << "," << p1.y() << ") mit der Linie (" << con.x() << "," << con.y() << ") -> (" << cor.x() << "," << cor.y() << ")" << endl;

    GT_Line l1(con, cor);
    GT_Line l2(p1, 0);         //direction 0

    l1.intersection(l2, p);

    return p;
}

//*************************************************************//
// function: get_rest_after_y                                  //
// description: This function computes the contour list as a   //
//              part of a given contour. The new contour       //
//              begins at a specified y-value.                 //
//                                                             //
//                           x---     x: old contour           //
//                           |   |    y: new contour           //
//                           x-x-                              //
//                            /  \                             //
//             y ------------o----\---------------             //
//                          /      \                           //
//                    ox-----      --                          //
//                     |     |    |  |                         //
//                    ox-----      --                          //
//                                                             //
//*************************************************************//
Contour* Contour::get_rest_after_y(GT_TreeAlgo* treealgo, double y)
{
    Contour* n_con = new Contour();
    ConPoint* con_point = 0;
    ConPoint* conpoint = new ConPoint();
    ConPoint* old_con_point = 0;
    double y_coord = 0.0;
    bool y_is_equal = 0;

    //insert conpoint in con_point_list
    treealgo->insert_con_point(conpoint);
    //insert new contour into list_of_contours
    treealgo->insert_contour(n_con);
	
    //run through list and determine if the y-coord of the node
    //is less or equal the given y

    list<ConPoint*>::iterator it;
    list<ConPoint*>::iterator end = contour_list.end();

    for (it = contour_list.begin (); it != end; ++it) {
	con_point = *it;
	y_coord = con_point->get_con_y();

	if(y_coord == y){
	    y_is_equal = 1;
	    if(con_point->get_cor_y() > y){
	      n_con->append_contour_point(con_point);
	    }
	}
	if(y_coord > y){
	    // 			n_con_empty = 0;
	    n_con->append_contour_point(con_point);
	}
    }

    //we have to determine the intersection point of the given y
    //in order to get the new first contour point (if y_is_equal = 0)

    if(y_is_equal == 0){
	//determine the ConPoint which has the maximal y-coordinate smaller
	//than y

	for (it = contour_list.begin (); it != end; ++it) {
	    con_point = *it;
	    
	    if (con_point->get_con_y() < y) {
		old_con_point = con_point;
	    }
	}

	assert(old_con_point != 0);
	//the new ConPoint is the intersection of the line between con
	//and cor of old_con_point and the horicontal line through y
	GT_Point ref_point(0.0, y);
	GT_Point intersec = this->get_intersection_point(ref_point, *old_con_point);
	
	conpoint->set_con_point(intersec);
	conpoint->set_cor_point(old_con_point->get_cor_point());
	n_con->push_contour_point(conpoint);
    }
    return n_con;
}



//*******************************************************************//
//                             "set"-routines                        //
//*******************************************************************//
void Contour::set_contour(list<ConPoint*> contour)
{
    contour_list = contour;
}

//con is the last element of the list
void Contour::append_contour_point(ConPoint* con)
{
    contour_list.push_back (con);
}

//con ist the first element of the list
void Contour::push_contour_point(ConPoint* con)
{
    contour_list.push_front (con);
}

//SABINE, 21.1.
//append (at the end!) a Contour rest to a given contour
//and return the combined list (Contour).
// Contour* Contour::append_contour(Contour* rest)
// {
// 	Contour* combined = new Contour();
// 	list<ConPoint*> list1;
// 	list<ConPoint*> list2;
// // 	ConPoint* con;
// // 	list_item it;
	
// 	list1 = this->get_contour();
// 	list2 = rest->get_contour();
// 	list1.conc(list2);

// // 	forall_items(it, list1){
// // 		con = list1.inf(it);
// // 		con->set_list_item(it);
// // 	}

// 	combined->set_contour(list1);
// 	return combined;
// }

void Contour::append_contour(Contour* rest)
{
    list<ConPoint*> rest_list = rest->get_contour();
    contour_list.splice (contour_list.end(), rest_list, 
	rest_list.begin(), rest_list.end());
}	


//*******************************************************************//
//                        update-routines                            //
//*******************************************************************//
void Contour::update_contour(double offset)
{
    list<ConPoint*>::iterator it;
    list<ConPoint*>::iterator end = contour_list.end();
    ConPoint* con;

    for (it = contour_list.begin (); it != end; ++it) {
	con = *it;
	con->update_conpoint(offset);
	con->update_corpoint(offset);
    }	
}



//*******************************************************************//
//                             print-routines                        //
//*******************************************************************//
void Contour::print_contour()
{

    if(this == 0){
	return;
    }

    list<ConPoint*>::iterator it;
    list<ConPoint*>::iterator end = contour_list.end();
    ConPoint* con;

    cout << "[";
    for (it = contour_list.begin (); it != end; ++it) {
	con = *it;
	cout << "(";
	// 		cout << "**" << con->get_list_item() << "**";
	cout << con->get_con_x() << ",";
	cout << con->get_con_y() << ";";
	cout << con->get_cor_x() << ",";
	cout << con->get_cor_y();
	cout << ")";
    }
    cout << "]" << endl;
}

//*******************************************************************//
//*******************************************************************//
//*                                                                 *//
//*               Member functions of class ConPoint                *//
//*                                                                 *//
//*******************************************************************//
//*******************************************************************//

//*******************************************************************//
//                             constructor                           //
//*******************************************************************//
ConPoint::ConPoint()
{
    point_type = 0;
}

//*******************************************************************//
//                             destructor                            //
//*******************************************************************//
ConPoint::~ConPoint()
{
}

//*******************************************************************//
//                             "get"-routines                        //
//*******************************************************************//
GT_Point ConPoint::get_con_point()
{
    return con_point;
}

GT_Point ConPoint::get_cor_point()
{
    return cor_point;
}

double ConPoint::get_con_x()
{
    return con_point.x();
}

double ConPoint::get_con_y()
{
    return con_point.y();
}

double ConPoint::get_cor_x()
{
    return cor_point.x();
}

double ConPoint::get_cor_y()
{
    return cor_point.y();
}
int ConPoint::get_point_type()
{
    return point_type;
}
GT_Line ConPoint::get_line()
{
    GT_Line con_cor(con_point, cor_point);
    return con_cor;
}


//*******************************************************************//
//                             "set"-routines                        //
//*******************************************************************//

void ConPoint::set_con_point(double x, double y)
{
    con_point = GT_Point(x, y);
}

void ConPoint::set_con_point(const GT_Point& p)
{
    con_point = p;
}

void ConPoint::set_cor_point(double x, double y)
{
    cor_point = GT_Point(x, y);
}

void ConPoint::set_cor_point(const GT_Point& p)
{
    cor_point = p;
}

void ConPoint::set_point_type(int type)
{
    point_type = type;
}

void ConPoint::set_points(double con_x, double con_y, double cor_x, double cor_y)
{
    ConPoint c_point;
	
    c_point.set_con_point(con_x, con_y);
    c_point.set_cor_point(cor_x, cor_y);
}



//*******************************************************************//
//                            update-routines                        //
//*******************************************************************//

void ConPoint::update_conpoint(double offset)
{
    double x_value = 0.0;
    double y_value = 0.0;
	
    x_value = con_point.x()+offset;
    y_value = con_point.y();

    this->set_con_point(x_value,y_value);
}

void ConPoint::update_corpoint(double offset)
{
    double x_value = 0.0;
    double y_value = 0.0;
	
    x_value = cor_point.x()+offset;
    y_value = cor_point.y();

    this->set_cor_point(x_value,y_value);
}









//***********************************************************//
// additional functions for LEDA                             //
//***********************************************************//
// int compare (const ConPoint &p1, const ConPoint &p2)
// {
// 	return 0;
// }

// int compare (const ConPoint p1, const ConPoint p2)
// {
// 	return 0;
// }

int compare(ConPoint* const& p1, ConPoint* const& p2)
{
    if(p1 == p2){
	return 0;
    }
    else{
	return 1;
    }
}

// output-functions for LEDA //
ostream& operator <<(ostream& out, const ConPoint& /* p */)
{
    return out;
}

istream& operator >> (istream& in , const ConPoint& /* p */)
{
    return in;
}


