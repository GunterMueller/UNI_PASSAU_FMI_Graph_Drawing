/* This software is distributed under the Lesser General Public License */
#include "radial_tree_layout_algorithm.h"
#include "radial_auxiliary.h"

#include <gt_base/Point.h>
#include <gt_base/Segment.h>
#include <gt_base/Circle.h>

#include <GTL/node_map.h>

#include <cmath>

#include <iostream>

#include <list>
#include <queue>
#include <vector>
#include <map>

// predefinitions

// list<node> my_BFS(graph * g, node s);

float area_of_node(float w, float h, GeometricNodeShape shape);

double compute_polar_radius(double pr_father, double w_father, double w_node, double distance, int rigid_flexible, double avg_area, double node_area,
    GeometricNodeShape shape,double h_father, double pang_father, double h_node,
    double centerX, double centerY);

// double compute_polar_angle(double sup_angle, double span_angle);

double compute_polar_angle(double sup_angle, double r, double w, double h, double padfac, GeometricNodeShape shape);

double compute_polar_wedge_to(double sup_angle, double r, double w, double h, double padfac, GeometricNodeShape shape, double polar_ang, double layoutcX, double layoutcY);

double compute_max_polar_angle_anti_esc(double poto_r, double poto_ang, double poto_w, double poto_h, double fath_r, double fath_ang, double v_r, GeometricNodeShape shape, double layoutx, double layouty);

double compute_increased_polar_radius(double spang, double w, double h, double padfac);		// no longer used !!

double recompute_polar_radius(double inner_rad, double inner_ang, double sup_ang, double w, double h, double lcenx, double lceny);

double recompute_polar_angle(double inner_rad, double inner_ang, double sup_ang, double w, double h, double polar_rad);

double compute_min_anti_collinear_family_angle(double node_polr, double sibling_polr, double sibling_polang, double sibling_w, double sibling_h, double father_polr, double father_polang, GeometricNodeShape shape);

bool edge_node_intersection(double edge_from_x, double edge_from_y, double edge_to_x, double edge_to_y, double node_x, double node_y, double node_w, double node_h, GeometricNodeShape shape);

double length_of_edge_node_intersection(double edge_from_x, double edge_from_y, double edge_to_x, double edge_to_y, double node_x, double node_y, double node_w, double node_h, GeometricNodeShape shape);












// constructor

Layout_Radial_Algorithm_Implementation::Layout_Radial_Algorithm_Implementation()
{
    //	hshift = 0 ;
    //	vshift = 0 ;

    how_to_select_root = GraphCenter ;

    parent_child_distance = 10 ;

    avoid_escaping_edges = false;

    avoid_collinear_families = false;

    padding_factor = 2;

    automatic_expansion = true;

    distance_rigid_flexible_flag = 0;

    enforce_corradial_nodes = None ;

    max_number_central_iterations = 12;

    center_parent_over_children = true;

    center_children_under_parent = true;

    eades = false;

    eades2 = false;

    eades_straight = false;

    eades_pi = false;

    debug = false;

    Eades = false ;
    Eades_avoid_crossing_edges = Risiko ;
    Eades_border_leaves = false ;

    circumcircle_transformation = false;
}




// destructor

Layout_Radial_Algorithm_Implementation::~Layout_Radial_Algorithm_Implementation()
{
}



// methods for setting parameters
// shall be replaced by standard accessors


void Layout_Radial_Algorithm_Implementation::set_graph(GT_Graph* new_graph)
{
    my_graph = new_graph;
}


void Layout_Radial_Algorithm_Implementation::set_how_to_select_root(int s)
{
    if (s == 0)
	how_to_select_root = RandomNode;
    else if (s == 1)
	how_to_select_root = BiggestNode;
    else if (s == 2)
	how_to_select_root = GraphCenter;
    else if (s == 3)
	how_to_select_root = SourceNode;
}


void Layout_Radial_Algorithm_Implementation::set_parent_child_distance(int d)
{
    parent_child_distance = d;

	
}

void Layout_Radial_Algorithm_Implementation::set_padding_factor(int pf)
{
    //	padding_factor = pf / 4.0 + 3 / 4.0 ;	// min display value 1
    padding_factor = pf / 5.0 + 1.0 ;	// min display value 0
}

void Layout_Radial_Algorithm_Implementation::set_automatic_expansion(int yn)
{
    if (yn == 0)
	automatic_expansion = true;
    else if (yn == 1)
	automatic_expansion = false;
}

void Layout_Radial_Algorithm_Implementation::set_distance_rigid_flexible_flag(int yn)
{
    distance_rigid_flexible_flag = yn;
}

void Layout_Radial_Algorithm_Implementation::set_padding_type(int yn)
{
    padding_type = yn;
	
}

void Layout_Radial_Algorithm_Implementation::set_avoid_escaping_edges(int yn)
{
    if (yn == 0)
	avoid_escaping_edges = true;
    else if (yn == 1)
	avoid_escaping_edges = false;
}

void Layout_Radial_Algorithm_Implementation::set_avoid_collinear_families(int yn)
{
    if (yn == 0)
	avoid_collinear_families = true;
    else if (yn == 1)
	avoid_collinear_families = false;

}




void Layout_Radial_Algorithm_Implementation::set_center_parent(int yn)
{
    if (yn == 0)
	center_parent_over_children = true;
    else if (yn == 1)
	center_parent_over_children = false;
}

void Layout_Radial_Algorithm_Implementation::set_center_children(int yn)
{
    if (yn == 0)
	center_children_under_parent = true;
    else if (yn == 1)
	center_children_under_parent = false;
}



void Layout_Radial_Algorithm_Implementation::set_fill_space(int yn)
{
    if (yn == 0)
	fill_space = true;
    else if (yn == 1)
	fill_space = false;
}








void Layout_Radial_Algorithm_Implementation::set_enforce_corradial_nodes(int i)
{
    if (i == 0)
	enforce_corradial_nodes = None;
    else if (i == 1)
	enforce_corradial_nodes = Children;
    else if (i == 2)
	enforce_corradial_nodes = Level;
}


void Layout_Radial_Algorithm_Implementation::set_debug(int d)
{
    if (d == 0)
	debug = true;
    else if (d == 1)
	debug = false;
}

void Layout_Radial_Algorithm_Implementation::set_eades(int d)
{
    if (d == 0)
	eades = true;
    else if (d == 1)
	eades = false;

	
}

void Layout_Radial_Algorithm_Implementation::set_eades2(int d)
{
    if (d == 0)
	eades2 = true;
    else if (d == 1)
	eades2 = false;

	
}



void Layout_Radial_Algorithm_Implementation::set_eades_straight(int d)
{
    if (d == 0)
	eades_straight = true;
    else if (d == 1)
	eades_straight = false;

	
}


void Layout_Radial_Algorithm_Implementation::set_eades_pi(int d)
{
    if (d == 0)
	eades_pi = true;
    else if (d == 1)
	eades_pi = false;


}


void Layout_Radial_Algorithm_Implementation::set_Eades(int d)
{
    if (d == 0)
	Eades = false;
    else if (d == 1)
	Eades = true;

}

void Layout_Radial_Algorithm_Implementation::set_Eades_avoid_crossing_edges(int d)
{
    if (d == 0)
	Eades_avoid_crossing_edges = Exclude;
    else if (d == 1)
	Eades_avoid_crossing_edges = Variant180;
    else if (d == 2)
	Eades_avoid_crossing_edges = Risiko;

}

void Layout_Radial_Algorithm_Implementation::set_Eades_border_leaves(int d)
{
    if (d == 0)
	Eades_border_leaves = true;
    else if (d == 1)
	Eades_border_leaves = false;

}




// method for determining geometric shape of nodes
// current version presumes UNIFORM shape all over my_graph

void Layout_Radial_Algorithm_Implementation::determine_shape_of_nodes()
{
    node n = my_graph->attached()->choose_node();
    const GT_Node_Attributes& att =  my_graph->gt(n);

    if (att.graphics()->type() == GT_Keys::type_oval) {
	if (att.graphics()->h() == att.graphics()->w()) {
	    shape_of_nodes = Circle;
	} else {
	    shape_of_nodes = Oval;
	}    
    } else if (att.graphics()->type() == GT_Keys::type_rectangle) {
	shape_of_nodes = Rectangle;
    } else {
	shape_of_nodes = Other;
    }

    // as circle is a specialization of oval
    // to determine circle shape we have to check ALL nodes

    if (shape_of_nodes == Circle) {
	forall_nodes(n,*(my_graph->attached())) {
	    if (my_graph->gt(n).graphics()->w() != my_graph->gt(n).graphics()->h())
		shape_of_nodes = Oval;
	}
    }
}




// last modification 22/08/97
// shape_of_nodes == Other enforces standard version for Circle nodes

bool Layout_Radial_Algorithm_Implementation::set_options_require_circle_nodes()
{
    return (enforce_corradial_nodes != None
	|| center_parent_over_children
	|| center_children_under_parent
	|| avoid_collinear_families
	|| shape_of_nodes == Other); 
}

// method for redirecting all edges of a free tree
// after its root has been chosen

// the algorithm is based on breadth first search

void Layout_Radial_Algorithm_Implementation::redirect_edges()
{

    // precondition 
    // here we shall make an appropriate assertion
    // about tree property

    // trivial cases : 
    // for less than two nodes nothing has to be done

    if (my_graph->attached()->number_of_nodes() < 2)
	return;

    // prepare graph for BFS making it strongly connected

    //	edge e_minus;
    list<edge> prov_inserted_rev_edges; // shall then be deleted

    prov_inserted_rev_edges = my_graph->attached()->insert_reverse_edges();

    //cout << "rev edges inserted" << endl;

    // execute BFS from designated root and retrieve list of nodes


    list<node> visiting_order = my_BFS(my_graph->attached(), root);

    // now we shall delete all misdirected edges

    node current = visiting_order.front();
    visiting_order.pop_front();

    edge e;
    graph* g = my_graph->attached();
    list<edge> to_be_deleted;

    while (!(visiting_order.empty())) { 
	forall_in_edges(e,current) {
	    if (find (visiting_order.begin(), visiting_order.end(), e.source()) 
		!= visiting_order.end()) {
		to_be_deleted.push_back (e);
	    }
	}

	current = visiting_order.front();
	visiting_order.pop_front ();
    }

    while (!to_be_deleted.empty()) {
	e = to_be_deleted.front();
	to_be_deleted.pop_front();
	
	g->del_edge (e);
    }

}




// method for determining the root

void Layout_Radial_Algorithm_Implementation::determine_root()
{

    if (how_to_select_root == RandomNode)

	//		root = my_graph->attached()->choose_node();  // not random

	root = my_graph->attached()->choose_node();

    // 	root = (my_graph->attached()->all_nodes()).contents((my_graph->attached()->all_nodes()).get_item(randint(my_graph->attached()->number_of_nodes())-1));


    else if (how_to_select_root == BiggestNode)

	root = this->find_biggest_node();

    else if (how_to_select_root == GraphCenter)
		
	root = center_of_tree(my_graph->attached());

    else if (how_to_select_root == SourceNode)
		
	root = source_of_graph(my_graph->attached());

    // now we must appropriately redirect all edges 

    this->redirect_edges();
}



// method for directing an undirected graph
// shall be called if graphlet passes an undirected graph

void Layout_Radial_Algorithm_Implementation::digraph()
{
    if (debug) cout << "digraph method called" << endl;
	
    (my_graph->attached())->make_directed();
}




// method for actually running the algorithm

void Layout_Radial_Algorithm_Implementation::now_just_do_it()
{
    if (debug) cout << "now_just_do_it entered" << endl;
	
    node v;

    // these are for quick testing only
    // nothing may be assumed about their value
    // unless immediately after an assignment
    double testdouble;
    int testint;
    node testnode;
    list<node> testlistnode;

    // possibly shapes have been changed interactively 

    this->determine_shape_of_nodes();

    // it may be necessary to update root
    // (e.g. node sizes may have changed)

    this->determine_root();

    // if special options set require circle nodes
    // but another shape is actually used

    // here temporarily modified node geometry information shall be stored

    node_map<node_image> original_node_geometry(*(my_graph->attached()));

    // last modification 22/08/97
    // this piece of code has been extended to work correctly for Other nodes
    // Other nodes enforce standard version in Circle mode

    if (this->set_options_require_circle_nodes()
	&& shape_of_nodes != Circle)

    {
	// store extension and shape of actual nodes

	forall_nodes(v,*(my_graph->attached())) {
	    original_node_geometry[v].store_width(my_graph->gt(v).graphics()->w());
	    original_node_geometry[v].store_heigth(my_graph->gt(v).graphics()->h());

	    // shape
	    if (my_graph->gt(v).graphics()->type() == GT_Keys::type_oval
		&& (my_graph->gt(v).graphics()->w() == my_graph->gt(v).graphics()->h()))
		original_node_geometry[v].set_circle();

	    else if (my_graph->gt(v).graphics()->type() == GT_Keys::type_rectangle)
		original_node_geometry[v].set_rectangle();
		
	    else if (my_graph->gt(v).graphics()->type() == GT_Keys::type_oval)
		original_node_geometry[v].set_oval();
		
	    else	original_node_geometry[v].set_other();	// new 22/08/97
	}

	// node transformation

	// replace each node by its circumcircle

	forall_nodes(v,*(my_graph->attached())) {
	    testdouble = original_node_geometry[v].compute_circumcircle_radius();
	    my_graph->gt(v).graphics()->w (2*testdouble);
	    my_graph->gt(v).graphics()->h (2*testdouble);
	}

	// set central control parameter to circle nodes
	// note that we don't change graphic attribute type of individual nodes

	shape_of_nodes = Circle;

	// remember 

	circumcircle_transformation = true;
    }



    // current version doesn't provide special treatment of oval nodes
    // thus if oval shape has been determined we use their circumrectangles

    if (shape_of_nodes == Oval)
	shape_of_nodes = Rectangle;






    // constant or variable padding style ?
    // padding_type is the switch

    // reverse transformation : get display value

    double constant_padding_increment = 5 * (padding_factor - 1) * (1 - padding_type);

    padding_factor = padding_factor * padding_type - padding_type + 1 ;

    // if constant --> padding_style = 0 --> padding_factor = 1 (minimum)
    // --> constant_padding_increment = (display)

    // if variable --> padding_style = 1 --> padding_factor (unchanged)
    // --> constant_padding_increment = 0

    /*
      cout << "padding info" << endl;
      cout << "padding_factor " << padding_factor << endl;
      cout << "padding increment " << constant_padding_increment << endl;
    */


    // for circle shape the padding factor is included in the formulae

    // for general shape we use a different approach :
    // the algorithm is run with expanded nodes
    // and before unpacking nodes are shrunken to their original size

    if (shape_of_nodes != Circle)
    {

	// expand nodes
	
	forall_nodes(v,*(my_graph->attached())) {
	    testdouble = my_graph->gt(v).graphics()->w();
	    my_graph->gt(v).graphics()->w (testdouble * padding_factor);
	    testdouble = my_graph->gt(v).graphics()->h();
	    my_graph->gt(v).graphics()->h (testdouble * padding_factor);
	}
    }


    // padding_increment has uniform treatment for ALL shapes of nodes

    forall_nodes(v,*(my_graph->attached())) {
	testdouble = my_graph->gt(v).graphics()->w();
	my_graph->gt(v).graphics()->w (testdouble + constant_padding_increment);
	testdouble = my_graph->gt(v).graphics()->h();
	my_graph->gt(v).graphics()->h (testdouble + constant_padding_increment);
    }

    // padding_factor may shrink by way of automatic expansion
    // so we must save its original value to be able to undo node growth
    // otherwise node size will grow with each algorithm run !

    double initial_padding_factor = padding_factor;


    // give nodes some margin

    // was proportional to node size

    /*
      double margin_of_node_size = 1.00;

      if (padding_factor > 1)
      {
      margin_of_node_size = 1.1;
      forall_nodes(v,*(my_graph->attached()))
      {
      testdouble = my_graph->gt(v).graphics()->w();
      my_graph->gt(v).graphics()->w (testdouble * margin_of_node_size);
      testdouble = my_graph->gt(v).graphics()->h();
      my_graph->gt(v).graphics()->h (testdouble * margin_of_node_size);
      }
      }
    */

    // fixed margin

    /*

      double margin_of_node_size = 0;

      if (padding_factor > 1)
      {
      margin_of_node_size = 2;
      forall_nodes(v,*(my_graph->attached()))
      {
      testdouble = my_graph->gt(v).graphics()->w();
      my_graph->gt(v).graphics()->w (testdouble + margin_of_node_size);
      testdouble = my_graph->gt(v).graphics()->h();
      my_graph->gt(v).graphics()->h (testdouble + margin_of_node_size);
      }
      }


    */






    // the algorithm iterates over the tree in DFS
    // drawing_order is the full dfs-path with multiple (!) visits 
    // reduced_drawing_order is the dfs-node-permutation

// #error Hier liegt ein Fehler:
    list<node> drawing_order, reduced_drawing_order;
    drawing_order = my_DFS(my_graph->attached(), root);
    reduced_drawing_order = delete_multiple_nodes(drawing_order);

    // cout << "dfs drawing order computed" << endl;







    // TEST : visualize reduced_drawing_order labeling nodes accordingly
	
    //     unsigned int testcounter = 0;
    //     char testlabel[100];			// sprintf doesn't know string
    //     while (testcounter < reduced_drawing_order.size())
    //     { 
    // 	v = reduced_drawing_order.contents(reduced_drawing_order.get_item(testcounter));
    // 	//		testlabel = testlabel + "*";	// unary numbers ...
    // 	//		sprintf(testlabel, "N%d", testcounter);	// now decimal :)
    // 	sprintf(testlabel, "%d", testcounter); // without prefix "N"
    // 	my_graph->gt(v).label (testlabel);
    // 	if (!debug) my_graph->gt(v).label ("");
    // 	testcounter++;
    //     };			// end of TEST





    // here the algorithm shall compute the polar coordinates of each node

    node_map<radial_polar_coordinate> position(*(my_graph->attached()));


    // this awkward piece of code shall be replaced by a real initialization
    // center the polar system at current root

    forall_nodes(v,*(my_graph->attached())) {
	position[v].set_center(my_graph->gt(root).graphics()->x(),
	    my_graph->gt(root).graphics()->y());
    } 

    // here the algorithm shall compute the angle
    // of the wedge spanned by the subtree rooted at each node

    node_map<radial_annulus_wedge> wedge(*(my_graph->attached()));
	
    // here we shall store the escaping edge correction factor
    // applied to the polar radius 

    node_map<double> escaping_edge_correction(*(my_graph->attached()));

    // initial value is 1

    forall_nodes(v, *(my_graph->attached())) {
	escaping_edge_correction[v] = 1;
    }

    // here we compute the average area of all nodes in the graph
    // some functions (especially compute_polar_radius with flexible
    // distances set) need this value

    double average_node_area = 0;

    forall_nodes(v, *my_graph->attached()) {
	average_node_area +=
	area_of_node(my_graph->gt(v).graphics()->w(), my_graph->gt(v).graphics()->h(), 
	    shape_of_nodes);
    }

    average_node_area /= my_graph->attached()->number_of_nodes();

    // currently, this value is not used
    // however it's a potentially useful offspring
    // of the nodes_at_level dictionary

    int heigth_of_tree = 0;


    // this dictionary stores for each value 0 ... heigth_of_tree
    // the list of nodes belonging to this level in the tree
    // note that root is defined to be level 0

    map < int , list<node> >		nodes_at_level;

    // initialization of nodes_at_level
    // iterate over all nodes of the tree and put them into appropriate lists
    
    map<int, list<node> >::iterator it, nend;
    

    forall_nodes(v, *my_graph->attached()) {

	// compute heigth of v in tree
	testint = level_in_tree (my_graph->attached(),root,v);

	// update heigth of tree
	if (testint > heigth_of_tree) heigth_of_tree = testint;

	// look up current dictionary item for key testint
	it = nodes_at_level.find (testint);

	// if item already exists, insert node v into its value
	if (it != nodes_at_level.end())	{
	    (*it).second.push_back (v);
	} else {
	    // otherwise, insert item <testint,<v>> into dictionary
	    nodes_at_level[testint] = list<node> (1, v);
	}
    }

    // TEST nodes_at_level

    if (debug) {
	cout << "heigth of tree " << heigth_of_tree << endl;

	for (testint = 0; testint <= heigth_of_tree ; testint++) {
	    cout << "level " << testint << endl;
	    it = nodes_at_level.find(testint);
	    cout << "nodes ";
	    testlistnode = (*it).second;
	    while (!(testlistnode.empty()))
	    {
		testnode = testlistnode.front();
		testlistnode.pop_front();
		cout << my_graph->gt(testnode).label() << " ";
	    }
	    cout << endl;
	}
    }


    // copy heigth of tree into class variable
    // making it accessible to other methods

    eades_global_height_of_tree = heigth_of_tree;


    // this shall tell us, for each one node,
    // which was the most recent node that
    // updated (dominated) its wedge_to

    // this feature is no longer supported

    node_map<node> wedge_to_dominated_by(*(my_graph->attached()));

    // at the beginning we presume
    // that each one node determines its wedge_to

    forall_nodes(v,*(my_graph->attached())) {
	wedge_to_dominated_by[v] = v;
    }


    /*
      // test auxiliary_node & drawing_order

      for (testint = 0; testint < drawing_order.size(); testint++)
      {testnode = drawing_order.contents(drawing_order.item(testint));
      testnode2 = auxiliary_node(my_graph->attached(), root, testnode, reduced_drawing_order);
      cout << "node " << my_graph->gt(testnode).label() << "aux " << my_graph->gt(testnode2).label() << endl;
      }			// end of test

    */

    // now we are ready for the central iteration
    // traversing drawing_order

    // note that some nodes may (must) be visited several times !

    node mr_node;
    //     int node_counter = 0;
    double new_radius, new_angle;
    double new_from, new_to;
    double max_father_son_span, actual_father_son_span;

    // escaping edge formula - shall be hidden inside a function
    // double testdoubleH1, testdoubleANG1, testdoubleH2, testdoubleH1COMP;

    // these are used in the automatic expansion after the central iteration

    double overall_wedge_required = 0;
    double correction_factor;
    double adapted_parent_child_distance, adapted_padding_factor;

    // these are used for escaping edge correction

    list<node> possibly_clashing_nodes;
    node pot_obstacle;
    double max_polar_angle_anti_esc;
    double esc_correction_factor;

    // ugly, have been hidden
    // double testBETA, testGAMMA, testGAMMACOMP, testANGMIN, testH1;

    // these are used for optional enforcement of corradial levels

    int enforce_corradiality_at_level = 0;
    vector<double> max_rad_level(heigth_of_tree + 1);  
    // otherwise error for 1-node tree


    // used for centering children under parent

    list<node> all_nodes_in_bfs;
    node node_whose_children_shall_be_centered;
    list<node> children_to_center;
    double buffer_between_children = 0;
    node current_child_to_center;
    double supporting_angle;

    // used for collinear family handling

    double minimum_colfam_angle_required;
    node most_recently_placed_sibling;



    int central_iteration_counter = 0;

  CENTRAL_ITERATION:					// LABEL

    // automatic expansion
    // returns here
    // if the layout
    // is too tight

    // optional enforcement
    // of corradial levels
    // returns here once
    // for each level

							
    if (debug) {
	cout << "central iteration #" << central_iteration_counter++ << endl;

	cout << "parent_child_distance " << parent_child_distance << endl;
	cout << "padding_factor " << padding_factor << endl;
    }

    // we repeat these initializations at the beginning of the central iteration
    // as it might be executed several times for automatic expansion (and other)

    forall_nodes(v,*(my_graph->attached())) {
	wedge[v].set_from(0); wedge[v].set_to(0);
	position[v].set_radius(0); position[v].set_angle(0);
    }


    list<node>::iterator father_pos;
    list<node>::iterator lit, my_it, my_end;
    list<node>::iterator end = drawing_order.end();


    for (lit = drawing_order.begin(); lit != end; ++lit) {
	v = *lit;
	
	// current node is v
	//cout << "current node " << my_graph->gt(v).label() << endl;


      POLAR_RADIUS_COMPUTATION:				// label
	// we jump here
	// after an
	// escaping edge 
	// correction

	if (debug) cout << "current node " << my_graph->gt(v).label() << endl;

	// determine radial distance from root

	if (v != root) {
	    if (debug) cout << "start polar radius computation" << endl;

	    // now inside function compute_polar_radius
	    //		new_radius = position[father(my_graph->attached(),v)].get_radius() + my_graph->gt(father(my_graph->attached(),v)).graphics()->w() + 10 + my_graph->gt(v).graphics()->w() ;

	    new_radius = compute_polar_radius(
		position[father(my_graph->attached(),v)].get_radius(), 
		my_graph->gt(father(my_graph->attached(),v)).graphics()->w() / 2, 
		my_graph->gt(v).graphics()->w() / 2, 
		parent_child_distance, 
		distance_rigid_flexible_flag,  
		average_node_area, 
		area_of_node(my_graph->gt(v).graphics()->w(),my_graph->gt(v).graphics()->h(), shape_of_nodes),
		shape_of_nodes,
		my_graph->gt(father(my_graph->attached(),v)).graphics()->h(),
		position[father(my_graph->attached(),v)].get_angle(),
		my_graph->gt(v).graphics()->h(),
		position[root].cartesian_x(),
		position[root].cartesian_y());

	    if (debug)		cout << new_radius << " for node " << my_graph->gt(v).label() << endl;

	    // escaping edge correction !!

	    new_radius = new_radius * escaping_edge_correction[v];

	    if (debug)		cout << "escaping edge correction gives new radius " << new_radius << endl;		






	    // if this OPTION is selected
	    // before actually assigning new_radius	
	    // as polar radius of node v we must check
	    // what radius WOULD be assigned to any sibling of v
	    // and set new_radius to the maximum

	    // note that this does NOT work for rectangular nodes !!

	    if ((enforce_corradial_nodes == Children) && (shape_of_nodes == Circle))
	    {
		node::adj_nodes_iterator it, end;
		for(it = father(my_graph->attached(),v).adj_nodes_begin(),
		    end = father(my_graph->attached(),v).adj_nodes_end();
		    it != end; ++it)
		{
		    testnode = *it;

		    testdouble = compute_polar_radius(
			position[father(my_graph->attached(),testnode)].get_radius(), 
			my_graph->gt(father(my_graph->attached(),testnode)).graphics()->w() / 2, 
			my_graph->gt(testnode).graphics()->w() / 2, 
			parent_child_distance, 
			distance_rigid_flexible_flag,  
			average_node_area, 
			area_of_node(my_graph->gt(testnode).graphics()->w(),my_graph->gt(testnode).graphics()->h(), shape_of_nodes),
			shape_of_nodes,
			my_graph->gt(father(my_graph->attached(),testnode)).graphics()->h(),
			position[father(my_graph->attached(),testnode)].get_angle(),
			my_graph->gt(testnode).graphics()->h(),
			position[root].cartesian_x(),
			position[root].cartesian_y());

		    new_radius = max(testdouble, new_radius);
		}

		if (debug)			cout << "corradial siblings enforcement has increased radius to " << new_radius << endl;
	    };		






	    // if OPTION "enforce corradial levels" is selected
	    // we may be in one of the enforcing central iterations
		
	    if ((enforce_corradial_nodes == Level) && (shape_of_nodes == Circle) && (enforce_corradiality_at_level > 0))
	    {
			
		// if v is a node of level 
		// enforce_corradiality_at_level
		// or BELOW (otherwise later enforcing iterations
		//	will re-compute "natural" polar radius)

		if (level_in_tree(my_graph->attached(),root,v) <= enforce_corradiality_at_level)

		{
		    // adapt its polar radius
		    // to maximum of appropriate (!) tree level

		    new_radius = max_rad_level[level_in_tree(my_graph->attached(),root,v) - 1];

		    if (debug)				cout << "level corradiality enforcement has increased polar radius to " << new_radius << endl;
		}

	    }






	    // now really assign new_radius to node v

	    position[v].set_radius(new_radius);
	};




	// determine radial angle

	//POLAR_ANGLE_COMPUTATION:		// old label
	// after an escaping edge correction
	// we jumped back here

	if (v != root)
	{
	    if (debug) cout << "start angle computation" << endl;


	    /* these are for detailed tracing

	       cout << "aux-node is " << my_graph->gt(auxiliary_node(my_graph->attached(),root,v,reduced_drawing_order)).label();
	       testdouble = wedge[auxiliary_node(my_graph->attached(),root,v,reduced_drawing_order)].get_to();  // was drawing_order
	       cout << "aux-wedge-to computed to " << testdouble <<endl;
	       testdouble = my_graph->gt(v).graphics()->w() / sqrt(new_radius * new_radius - my_graph->gt(v).graphics()->w() * my_graph->gt(v).graphics()->w());
	       cout << "atan-arg computed to " << testdouble << endl;
	       cout << "with arg of sqrt = " << new_radius * new_radius << " - " << my_graph->gt(v).graphics()->w() << " ^ 2" << endl ;

	    */


		
	    // now inside function compute_polar_angle
	    //		new_angle = wedge[auxiliary_node(my_graph->attached(),root,v,reduced_drawing_order)].get_to() + atan(my_graph->gt(v).graphics()->w() / sqrt(new_radius * new_radius - my_graph->gt(v).graphics()->w() * my_graph->gt(v).graphics()->w())); // used, erroneously, drawing_order

	    // obsolete version
	    //		new_angle = compute_polar_angle(wedge[auxiliary_node(my_graph->attached(),root,v,reduced_drawing_order)].get_to(),   atan(my_graph->gt(v).graphics()->w() / sqrt(new_radius * new_radius - my_graph->gt(v).graphics()->w() * my_graph->gt(v).graphics()->w())));  // too big !

	    // compute_polar_angle now is given all relevant geometric and layout data
	    // and hides the formula details		

	    new_angle = compute_polar_angle(wedge[auxiliary_node(my_graph->attached(),root,v,reduced_drawing_order)].get_to(),   new_radius,   my_graph->gt(v).graphics()->w(),   my_graph->gt(v).graphics()->h(),   padding_factor, shape_of_nodes);





	    // in case of rectangular nodes
	    // polar radius and polar angle as computed yet
	    // are intermediate values which must be transformed

	    if (shape_of_nodes == Rectangle)
	    {
		position[v].set_radius(recompute_polar_radius(new_radius, new_angle, wedge[auxiliary_node(my_graph->attached(),root,v,reduced_drawing_order)].get_to(), my_graph->gt(v).graphics()->w(), my_graph->gt(v).graphics()->h(), position[root].cartesian_x(), position[root].cartesian_y() ));			
		new_angle = recompute_polar_angle(new_radius, new_angle, wedge[auxiliary_node(my_graph->attached(),root,v,reduced_drawing_order)].get_to(), my_graph->gt(v).graphics()->w(), my_graph->gt(v).graphics()->h(), position[v].get_radius());
	    }









	    // collinear family handling


	    // catches a rare exception (edge hits sibling)
	    // can be outcommented without major problems



	    if (avoid_collinear_families && 
		root != auxiliary_node(my_graph->attached(),root,v,reduced_drawing_order) &&	// catch error due to implementation of siblings
		siblings(my_graph->attached(), v, auxiliary_node(my_graph->attached(),root,v,reduced_drawing_order)))

		// if most recently placed sibling of v exists
		// auxiliary_node will return it

	    {


		// use it to compute the minimum angle needed for avoiding
		// collinear families

		most_recently_placed_sibling = auxiliary_node(my_graph->attached(),root,v,reduced_drawing_order);
	
		minimum_colfam_angle_required = compute_min_anti_collinear_family_angle(
		    position[v].get_radius(),
		    position[most_recently_placed_sibling].get_radius(),
		    position[most_recently_placed_sibling].get_angle(),
		    my_graph->gt(most_recently_placed_sibling).graphics()->w(),
		    my_graph->gt(most_recently_placed_sibling).graphics()->h(),
		    position[father(my_graph->attached(),v)].get_radius(),
		    position[father(my_graph->attached(),v)].get_angle(),
		    shape_of_nodes);

		if (new_angle < position[father(my_graph->attached(),v)].get_angle() 
		    + minimum_colfam_angle_required)
		    if (debug)
		    {		
			cout << "if there is a collinear family intersection" << endl;
			cout << "colfam formula suggests polar angle " << position[father(my_graph->attached(),v)].get_angle() + minimum_colfam_angle_required << endl;
		    }
	
		position[v].set_angle(new_angle);	// anticipate it
		// to make available
		// coordinates

		// to see if there is actually a colfam intersection
		// we employ traditional computational geometry

	      COLFAM_ITERATION:		// jump here from colfam iteration end

		if (edge_node_intersection(
		    position[father(my_graph->attached(),v)].cartesian_x(),
		    position[father(my_graph->attached(),v)].cartesian_y(),
		    position[v].cartesian_x(),
		    position[v].cartesian_y(),
		    position[most_recently_placed_sibling].cartesian_x(),
		    position[most_recently_placed_sibling].cartesian_y(),
		    my_graph->gt(most_recently_placed_sibling).graphics()->w(),
		    my_graph->gt(most_recently_placed_sibling).graphics()->h(),
		    shape_of_nodes))
		{

		    /*			if (debug)
					{ 
					cout << "INTERSECTION" << endl;
					cout << "polar angle would be " << new_angle << endl;
					cout << "colfam formula yields polar angle " << position[father(my_graph->attached(),v)].get_angle() + minimum_colfam_angle_required << endl;
					}
			
					new_angle = (position[father(my_graph->attached(),v)].get_angle() + minimum_colfam_angle_required);			
		    */

		    // due to problems with minimum_colfam_angle_required
		    // now a heuristic approach is prefered :
		
		    // get length of intersection segment			

		    testdouble = length_of_edge_node_intersection(
			position[father(my_graph->attached(),v)].cartesian_x(),
			position[father(my_graph->attached(),v)].cartesian_y(),
			position[v].cartesian_x(),
			position[v].cartesian_y(),
			position[most_recently_placed_sibling].cartesian_x(),
			position[most_recently_placed_sibling].cartesian_y(),
			my_graph->gt(most_recently_placed_sibling).graphics()->w(),
			my_graph->gt(most_recently_placed_sibling).graphics()->h(),
			shape_of_nodes);

		    // put it in relation to maximum intersection segment
		    // quotient can range from zero to one		

		    // warning : this line is specific for circle nodes

		    testdouble /= 2* my_graph->gt(most_recently_placed_sibling).graphics()->w();

		    // use this value to heuristically compute
		    // polar angle increment
			
		    // warning : this line is specific for circles

		    testdouble *= atan(my_graph->gt(most_recently_placed_sibling).graphics()->w() / sqrt( position[most_recently_placed_sibling].get_radius() * position[most_recently_placed_sibling].get_radius() - my_graph->gt(most_recently_placed_sibling).graphics()->w() * my_graph->gt(most_recently_placed_sibling).graphics()->w())) ;
			
		    if (debug)
		    { cout << "colfam heuristics suggest angle increment " << testdouble << endl;}

		    // update them both ... 

		    new_angle += testdouble;
		    position[v].set_angle(position[v].get_angle() + testdouble);

		    goto COLFAM_ITERATION;

		}
	    }



	    // end of collinear family handling






		


		
	    position[v].set_angle(new_angle);

	    if (debug)		cout << "angle set to " << new_angle << endl;
	};






	// "escaping edge" conjecture
	// expressed by Eades in his original paper

	// to guarantee planarity
	// an edge may not cross the inner circle it starts at

	// this is checked by comparing the polar angle difference
	// between father and son
	// to a maximum value depending on their polar radius

	// in case of illegal value
	// we maintain the condition
	// by augmenting the son's polar radius


	if ((v != root) && (father(my_graph->attached(),v) != root) && avoid_escaping_edges) 

	{	
		
	
	    // compute maximum angle difference

	    max_father_son_span = acos((position[father(my_graph->attached(),v)].get_radius()) / new_radius);

	    // compare with actual difference

	    actual_father_son_span = fabs(new_angle - position[father(my_graph->attached(),v)].get_angle());

	    if (actual_father_son_span > max_father_son_span)

	    {
		// ESCAPING EDGE
		if (debug)	cout << "ESCAPING EDGE " << my_graph->gt(father(my_graph->attached(),v)).label() << " --> " << my_graph->gt(v).label() << endl;




		
		// current approach to escaping edges is at follows :

		// escaping edges as defined by Eades are a
		// POTENTIAL problem

		// they CAN intersect with a predecessor node of v
		// so we check along the path from v to root

		possibly_clashing_nodes = path_to_root(my_graph->attached(), root, v);

		// these are the nodes we have to check

		// v and its parent are adjacent to the escaping edge
		// so we discard them
		// (remember that path length is at least 3 nodes
		// otherwise we won't enter this program branch)

		possibly_clashing_nodes.pop_front();
		possibly_clashing_nodes.pop_front();

		// 		if (debug)	cout << "potential intersection with : " ;
		// 		end = possibly_clashing_nodes.end();

		// 		for (it = possibly_clashing_nodes.begin (); 
		// 		for (testcounter = 0; testcounter < possibly_clashing_nodes.length(); testcounter++)
		// 		    if (debug)			cout << my_graph->gt(possibly_clashing_nodes.contents(possibly_clashing_nodes.get_item(testcounter))).label() << " ";

		// 		if (debug)		cout << endl;

		// now we identify all actual clashes

		// esc_relation_factor measures grade of clashing
		// and will be used for a local correction
		// clear it
		esc_correction_factor = 1;

		// for each possibly clashing node "pot_obstacle"
		// we compute the maximum legal polar angle of v

		while (!(possibly_clashing_nodes.empty()))
		{
		    pot_obstacle = possibly_clashing_nodes.front();
		    possibly_clashing_nodes.pop_front();

		    max_polar_angle_anti_esc = compute_max_polar_angle_anti_esc(
			position[pot_obstacle].get_radius(),
			position[pot_obstacle].get_angle(),
			my_graph->gt(pot_obstacle).graphics()->w(),
			my_graph->gt(pot_obstacle).graphics()->h(),
			position[father(my_graph->attached(),v)].get_radius(),
			position[father(my_graph->attached(),v)].get_angle(),
			position[v].get_radius(),
			shape_of_nodes,
			position[root].cartesian_x(),
			position[root].cartesian_y());


		    if (debug)			cout << "checking potential obstacle " << my_graph->gt(pot_obstacle).label() << endl;

		    if (debug)			cout << "max polar angle of " << my_graph->gt(v).label() <<  " is " << max_polar_angle_anti_esc << endl;

		
		    if (position[v].get_angle() > max_polar_angle_anti_esc)

		    {
			
			// CLASH !!!!
			// escaping edge father(v) -> v
			// intersects node pot_obstacle

			if (debug)			cout << "node " << my_graph->gt(pot_obstacle).label() << " is hit !!" << endl;

			// illegal relation of angles causes clash
			// maximum illegal relation is stored in
			// esc_correction_factor

			if (position[v].get_angle() / max_polar_angle_anti_esc > esc_correction_factor)
			{
			    esc_correction_factor = position[v].get_angle() / max_polar_angle_anti_esc;
			    if (debug)				cout << "esc_correction_factor for edge " << my_graph->gt(father(my_graph->attached(),v)).label() << " -> " << my_graph->gt(v).label() << " is " << esc_correction_factor << endl;
			}

		    }
		
		
		}


		// after checking all potential clashes 
		// compare esc_correction_factor to its neutral initial value
		// to see if there have been collisions

		if (esc_correction_factor > 1.0)
		{
		    // the polar radius of father(v) shall be increased by this factor
		    // that is, we shall multiply the central escaping_edge_correction
		    // of father(v) by this factor

		    // give it some margin ...
		    esc_correction_factor = esc_correction_factor * 1.075;

		    // we need to return to the first occurrence 
		    // of father(v) in drawing_order
		    // and by the way initialize everything

		    // node_counter indicates actual position in drawing_order

		    mr_node = father(my_graph->attached(),v); 
		    my_end = drawing_order.end();
		    father_pos = find (drawing_order.begin(), my_end, mr_node);
		    assert (father_pos != my_end);

		    for (my_it = father_pos; my_it != lit; ++my_it) {
			testnode = *my_it;
			// reset layout data of testnode
			position[testnode].set_radius(0);
			position[testnode].set_angle(0);
			wedge[testnode].set_from(0);
			wedge[testnode].set_to(0);			    
		    }

		    testnode = *lit;
		    position[testnode].set_radius(0);
		    position[testnode].set_angle(0);
		    wedge[testnode].set_from(0);
		    wedge[testnode].set_to(0);			    
		    
		    testnode = *father_pos;

		    // INCREMENT its escaping edge correction 
		    if (debug) {
			cout << "escaping edge correction :" << endl;
			cout << "increase escaping edge correction of node " << my_graph->gt(testnode).label() << endl;
			cout << "from " << escaping_edge_correction[testnode] << " to " << escaping_edge_correction[testnode] * esc_correction_factor << endl;}


		    escaping_edge_correction[testnode] = escaping_edge_correction[testnode] * esc_correction_factor;

		    // now we are ready to link back into the central iteration

		    // invariant : v is the node indicated by node_counter
		    v = testnode;
		    lit = father_pos;

		    goto POLAR_RADIUS_COMPUTATION;
		
		}
	    }
	}





	// determine annulus wedge

	if (v != root) {			

	    if (debug)	cout << "start wedge computation" << endl;
	
	    // note that we make use of the drawing_order
	    // to progressively update the size of a wedge

	    // compute lower bound of wedge

	    new_from = wedge[auxiliary_node(my_graph->attached(),root,v,reduced_drawing_order)].get_to(); // used, erroneously, drawing_order

	    wedge[v].set_from(new_from);

	    if (debug) cout << "wedge-from set to " << new_from << endl ;



	    // compute upper bound of wedge

	    // first we consider the node
	    // as if it was a leaf
	
	    // now we use compute_polar_wedge_to()
	    //	new_to = wedge[v].get_from() + 2*atan(my_graph->gt(v).graphics()->w() / sqrt(new_radius * new_radius - my_graph->gt(v).graphics()->w() * my_graph->gt(v).graphics()->w()));  // too big !

	    // there was a devious bug in the rectangle version
	    // second parameter was "new_radius"

	    new_to =	compute_polar_wedge_to(wedge[v].get_from(),   position[v].get_radius(),  my_graph->gt(v).graphics()->w(),   my_graph->gt(v).graphics()->h(),   padding_factor,         // extented interface
		shape_of_nodes, position[v].get_angle(), position[root].cartesian_x(), position[root].cartesian_y()); 

	    wedge[v].set_to(new_to);

	    /* for detailed tracing
	       cout << "considering node as leaf" << endl;
	       cout << "wedge-to = " << new_to << endl;
	       // cout << "!!! using new_radius " << new_radius << endl;
	    */	

	    // now we compare it with what's required by yet processed children

	    new_to = wedge[v].get_from();

	    node::out_edges_iterator it, end;
	    for(it = v.out_edges_begin(), end = v.out_edges_end();
		it != end; ++it)
	    {
		new_to += wedge[v.opposite(*it)].size();
	    }

	    /* for detailed tracing
	       cout << "considering its children" << endl ;
	       cout << "alternative wedge_to " << new_to << endl ;
	    */

	    // if wedge_to required by yet processed children
	    // is the bigger one
	    // then the most recently visited child of v is responsible

	    // that child is the immediate predecessor of v in drawing_order

	    father_pos = lit;

	    if (new_to > wedge[v].get_to()) {
		++father_pos;
		assert (father_pos != drawing_order.end());
		wedge_to_dominated_by[v] = *father_pos;
	    }

	    // now choose the bigger one

	    new_to = max(new_to, wedge[v].get_to());  



	    wedge[v].set_to(new_to);

	    if (debug)
		cout << "resulting wedge_to = " << new_to << endl ;
	    // cout << "wedge_to dominated by " << my_graph->gt(wedge_to_dominated_by[v]).label() << endl;

	}
	


    }



    // end of central iteration

    //					end of central iteration










    // before actually drawing the tree
    // we check on its polar coordinates
    // if the overall wedge required does not exceed the plain 

    overall_wedge_required = 0;

    node::adj_nodes_iterator adj_it, adj_end;
    for(adj_it = root.adj_nodes_begin(), adj_end = root.adj_nodes_end();
	adj_it != adj_end; ++adj_it)
    {
	overall_wedge_required += wedge[*adj_it].size();
    }
    
    if (debug) {
	cout << "overall wedge required is " << overall_wedge_required << endl;
	cout << "max legal is " << 2 * M_PI << endl;}

    // we use the relation between them to propose adequate layout parameters

    correction_factor = overall_wedge_required / (2*M_PI);

    if (debug) cout << "correction factor " << correction_factor << endl;
	




    if (debug) {

	// *******************************************************************
	// this piece of code has been used for computing some analytic tables
	// delta is for parent_child_distance
	// see chapter "implementation" of my thesis for details
	// quick & dirty code !

	if (correction_factor > 1)
	{
	    cout << "correction factor : " << correction_factor << endl;
	    cout << "actual delta value : " << parent_child_distance << endl;

	    double delta_upper_bound = 0;
	
	    double delta_Rhat;

	

	    node delta_node;
	    node delta_node_pred;

	    forall_nodes(delta_node, *(my_graph->attached()))
		{
		    if (delta_node != root)
		    {
			delta_Rhat = sqrt(
			    my_graph->gt(delta_node).graphics()->w() * my_graph->gt(delta_node).graphics()->w() / 4 
			    +
			    ((my_graph->gt(delta_node).graphics()->w()/2) /
				tan( (1/correction_factor) * atan(
				    (my_graph->gt(delta_node).graphics()->w()/2) / sqrt(position[delta_node].get_radius() * position[delta_node].get_radius() - my_graph->gt(delta_node).graphics()->w() * my_graph->gt(delta_node).graphics()->w() / 4)))
			     )
			    *
			    ((my_graph->gt(delta_node).graphics()->w()/2) /
				tan( (1/correction_factor) * atan(
				    (my_graph->gt(delta_node).graphics()->w()/2) / sqrt(position[delta_node].get_radius() * position[delta_node].get_radius() - my_graph->gt(delta_node).graphics()->w() * my_graph->gt(delta_node).graphics()->w() / 4)))
			     )
			
			    );

			for (delta_node_pred = delta_node ; delta_node_pred != root ; delta_node_pred = father(my_graph->attached(),delta_node_pred))
			{
			    delta_Rhat -= my_graph->gt(delta_node_pred).graphics()->w(); 
			}

			delta_Rhat += my_graph->gt(delta_node).graphics()->w()/2;
			delta_Rhat -= my_graph->gt(root).graphics()->w()/2;

			delta_Rhat /= level_in_tree(my_graph->attached(),root,delta_node);
		    }
		
		    delta_upper_bound = max(delta_upper_bound, delta_Rhat);		

		}

	    cout << "upper delta bound : " << delta_upper_bound << endl;

	}
 

	// end of delta analysis ***

    }



	

    if ((overall_wedge_required > 2*M_PI) && automatic_expansion)
    {	
	adapted_parent_child_distance = parent_child_distance * correction_factor 
	    * 1.075;	// give it some margin ...
	adapted_padding_factor = 1 + (padding_factor - 1) / sqrt(correction_factor);	// prevent padding factor from being adapted too rapidly

	// note that padding_factor may NEVER have a value lower than 1
	if (debug) {
	    cout << "try parent_child_distance " << adapted_parent_child_distance << endl;
	    cout << "try padding_factor " << adapted_padding_factor << endl;}
	// was > 1.01 --> peril of crossings !!
	if ((correction_factor > 1.0) && 
	    (central_iteration_counter < max_number_central_iterations))
	{
	    parent_child_distance = adapted_parent_child_distance;
	    padding_factor = adapted_padding_factor;
	    goto CENTRAL_ITERATION;
	}
    }


    // if OPTION "enforcement of corradial levels" has been selected
    // this program branch will be executed once for each value of
    // enforce_corradiality_at_level between zero and heigth_of_tree - 1

    if ((enforce_corradial_nodes == Level) && (shape_of_nodes == Circle) 
	&& (enforce_corradiality_at_level < heigth_of_tree))

    {
	// we need heigth_of_tree central iterations for this !

	enforce_corradiality_at_level++;

	// determine maximum polar radius assigned to any node on this level

	it = nodes_at_level.find (enforce_corradiality_at_level);

	// 	testdic_item = nodes_at_level.lookup(enforce_corradiality_at_level);

	// this dictionary item MUST exist 
	//	(trees can't have empty intermediate levels)

	// get list of all nodes at level enforce_corradiality_at_level
	
	testlistnode = (*it).second;

	// 	testlistnode = nodes_at_level.inf(testdic_item);

	// find maximum radius

	max_rad_level[enforce_corradiality_at_level - 1] = 0;

	while (!(testlistnode.empty()))
	{
	    testnode = testlistnode.front();
	    testlistnode.pop_front();
	    max_rad_level[enforce_corradiality_at_level - 1] =						max(max_rad_level[enforce_corradiality_at_level - 1],
		position[testnode].get_radius());
	}

	if (debug)	cout << "max polar radius at level " << enforce_corradiality_at_level << " is " << max_rad_level[enforce_corradiality_at_level- 1] << endl;

	// central_iteration_counter bounds number of jumps to label
	// CENTRAL_ITERATION made from the automatic expansion

	// corradial levels enforcement acts after automatic expansion

	// increasing the polar radius of some nodes can only reduce
	// the overall space required by the layout
	// so automatic expansion shall not be needed any more

	goto CENTRAL_ITERATION;

    }

    //
    // OPTION center children
    // 

    if (center_children_under_parent && shape_of_nodes == Circle) {
	
	// to work correctly
	wedge[root].set_to(2 * M_PI);

	// build list of all nodes in BFS order
	all_nodes_in_bfs.clear();
	nend = nodes_at_level.end();

	for (it = nodes_at_level.begin(); it != nend; ++it) {
		// append list of nodes at level testint
		all_nodes_in_bfs.splice(all_nodes_in_bfs.end(), (*it).second);
	}

	    // now center each node's children	

	while (!(all_nodes_in_bfs.empty())) {
	    node_whose_children_shall_be_centered = all_nodes_in_bfs.front();
	    all_nodes_in_bfs.pop_front();

	    children_to_center.clear();
	    copy(node_whose_children_shall_be_centered.adj_nodes_begin(),
		 node_whose_children_shall_be_centered.adj_nodes_end(),
		 back_inserter(children_to_center));

	    // order list of children up to their polar angles
	    // using insertion sort ... (quick & dirty)
	    
	    testlistnode.clear();
	    
	    while (!(children_to_center.empty())) {
		testnode = children_to_center.front();
		children_to_center.pop_front();
		
		my_end = testlistnode.end();
		my_it = testlistnode.begin();
		
		while (my_it != my_end && 
		    position[*my_it].get_angle() < position[testnode].get_angle()) {
		    
		    ++my_it;
		}

		    // 		for (testint = 0;
		    // 		     (testint < testlistnode.size()) && (position[testlistnode.contents(testlistnode.get_item(testint))].get_angle() < position[testnode].get_angle());
		    // 		     testint++) {
		    // 		    ;
		    // 		}
		    // check inserted Michael Himsolt, 5 Jan 97 after
		    // much debugging

		testlistnode.insert (my_it, testnode);

		    // 		if (testint > 0) {
		    // 		    testlistnode.insert (testnode,
		    // 			testlistnode.get_item(--testint),after);
		    // 		}
	    }

	    children_to_center = testlistnode;

	    // reset buffer
	    buffer_between_children = 0;

	    // compute overall wedge size required by all children

	    my_it = children_to_center.begin();
	    my_end = children_to_center.end();

	    while (my_it != my_end) {
		testdouble += wedge[*my_it].size();
		++my_it;
	    }
	    
	    // if this is less than their father's wedge ...

	    if (testdouble < wedge[node_whose_children_shall_be_centered].size()) {
		// compute buffer to insert between children
		// new solution
		buffer_between_children = (wedge[node_whose_children_shall_be_centered].size() - testdouble) / (children_to_center.size());
	    }


	    // new solution
	    // +new is an even finer modification of the new solution

	    supporting_angle = wedge[node_whose_children_shall_be_centered].get_from();

	    while(!(children_to_center.empty())) {
		current_child_to_center = children_to_center.front();
		children_to_center.pop_front();

		testdouble = compute_polar_angle(supporting_angle + 0.5*buffer_between_children,position[current_child_to_center].get_radius(),my_graph->gt(current_child_to_center).graphics()->w(),my_graph->gt(current_child_to_center).graphics()->h(),padding_factor,shape_of_nodes);	
		
		position[current_child_to_center].set_angle(testdouble);

		if (!fill_space)
		    wedge[current_child_to_center].set_to(  supporting_angle + wedge[current_child_to_center].size() + 0.5*buffer_between_children  );
		// factor 0.5 is +new

		if (fill_space)		// VARIANT
		    wedge[current_child_to_center].set_to(  supporting_angle + wedge[current_child_to_center].size() + buffer_between_children  );

		if (!fill_space)
		    wedge[current_child_to_center].set_from( supporting_angle   + 0.5*buffer_between_children);  // summand 0.5*... is +new

		if (fill_space)		// VARIANT : wedge incorporates buffer
		    wedge[current_child_to_center].set_from( supporting_angle );
	
		if (!fill_space)
		    supporting_angle = wedge[current_child_to_center].get_to() +0.5*buffer_between_children; // summand 0.5*... is +new

	
		if (fill_space)		// VARIANT : don't waste buffer
		    supporting_angle = wedge[current_child_to_center].get_to();
	    }
	}
    }

    // if OPTION center parent node over its children has been selected
    
    if (center_parent_over_children && shape_of_nodes == Circle) {
	//
	// for every node (except root) having children
	//

	forall_nodes(v, *(my_graph->attached())) {
	    if (v != root && v.outdeg() > 0)
		position[v].set_angle( 
		    0.5 * ( wedge[v].get_from() + wedge[v].get_to() ));
	    // simpler & finer			
	}
    }

    // if circumcircle_transformation has been made
    // we have to undo it 
    // returning nodes to their original extension

    if (circumcircle_transformation) {
	forall_nodes(v,*(my_graph->attached())) {
	    my_graph->gt(v).graphics()->w (original_node_geometry[v].get_width());
	    my_graph->gt(v).graphics()->h (original_node_geometry[v].get_heigth());
	}
    }


    if (shape_of_nodes != Circle) {
	forall_nodes(v,*(my_graph->attached())) {
	    testdouble = my_graph->gt(v).graphics()->w();
	    my_graph->gt(v).graphics()->w (testdouble / initial_padding_factor);
	    testdouble = my_graph->gt(v).graphics()->h();
	    my_graph->gt(v).graphics()->h (testdouble / initial_padding_factor);
	}
    }
    
    // padding_increment has uniform treatment for ALL shapes of nodes
    
    // however, if a circumcircle transformation has taken place
    // nodes have already been restored to their original size

    if (!circumcircle_transformation) {
	forall_nodes(v,*(my_graph->attached())) {
	    testdouble = my_graph->gt(v).graphics()->w();
	    my_graph->gt(v).graphics()->w (testdouble - constant_padding_increment);
	    testdouble = my_graph->gt(v).graphics()->h();
	    my_graph->gt(v).graphics()->h (testdouble - constant_padding_increment);
	}
    }




    // quick & dirty ... translate new options into old (=working) ones ...

	// clear all
    eades = false; eades2 = false; eades_pi = false; eades_straight = false;

    if (Eades && Eades_border_leaves) { 
	eades = false; 
	eades2 = true;
    }
    
    if (Eades && !Eades_border_leaves) { 
	eades = true; 
	eades2 = false; 
    }

    if (Eades_avoid_crossing_edges == Variant180) { 
	eades_pi = true;
    }

    
    // if special option EADES has been selected

    if (eades && !eades2 && !eades_straight) {

	// cout << "special option EADES has been selected" << endl;

	// initialize node maps

	eades_width.init(*(my_graph->attached()));
	eades_polar_radius.init(*(my_graph->attached()));
	eades_polar_angle.init(*(my_graph->attached()));


	// test & compute width --> Eades' original is width_of_tree 
	// variations are available and documented in my thesis

	forall_nodes(v,*(my_graph->attached())) {
	    eades_width[v] = width2_of_tree(my_graph->attached(),root,v);
	}

	// shrink nodes to "irrelevant" size
	// root shall be a little larger
	/*
	  forall_nodes(v,*(my_graph->attached()))
	  {	
	  my_graph->gt(v).graphics()->w (3);
	  my_graph->gt(v).graphics()->h (3);
	  }
	  my_graph->gt(root).graphics()->w (15);
	  my_graph->gt(root).graphics()->h (15);
	*/

	// call DrawSubTree1 according to Eades
	// to re-compute positions

	this->DrawSubTree1(root, 0, 0, 2*M_PI);

	// copy positions 

	forall_nodes(v,*(my_graph->attached())) {
	    position[v].set_radius(eades_polar_radius[v]);
	    position[v].set_angle(eades_polar_angle[v]);
	}
    }

    // if special option EADES2 has been selected

    if (eades2 && !eades && !eades_straight) {

	// cout << "special option EADES2 has been selected" << endl;

	// initialize node maps

	eades_width.init(*(my_graph->attached()));
	eades_polar_radius.init(*(my_graph->attached()));
	eades_polar_angle.init(*(my_graph->attached()));


	// test & compute width --> Eades' original is width_of_tree 
	// variations are available and documented in my thesis

	forall_nodes(v,*(my_graph->attached())) {
	    eades_width[v] = width2_of_tree(my_graph->attached(),root,v);
	}

	// shrink nodes
	/*
	  forall_nodes(v,*(my_graph->attached()))
	  {
	  my_graph->gt(v).graphics()->w (3);
	  my_graph->gt(v).graphics()->h (3);
	  }
	  my_graph->gt(root).graphics()->w (15);
	  my_graph->gt(root).graphics()->h (15);
	*/


	// call DrawSubTree2 according to Eades
	// to re-compute positions

	this->DrawSubTree2(root, 0, 0, 2*M_PI);

	// copy positions 

	forall_nodes(v,*(my_graph->attached())) {
	    position[v].set_radius(eades_polar_radius[v]);
	    position[v].set_angle(eades_polar_angle[v]);
	}
    }


    forall_nodes(v,*(my_graph->attached())) {
	my_graph->gt(v).graphics()->x	(position[v].cartesian_x());
	my_graph->gt(v).graphics()->y	(position[v].cartesian_y());
    }
}






// these are the functions used to compute
// the actual coordinates in the layout

// they don't appear in radial_tree_layout_algorithm.h
// as their only legal use shall be HERE



// compute polar radius
// current version for circle nodes only

double compute_polar_radius(
    double polar_radius_of_father,
    double width_of_father,		// actually HALF the width
    double width_of_node,		// as original interface was for circle
    double father_son_distance,
    int rigid_flexible_flag,
    double avg_node_area,
    double node_area,
    GeometricNodeShape shape,
    double height_of_father,
    double polar_angle_of_father,
    double height_of_son,
    double center_of_layoutX,
    double center_of_layoutY) {

    double result;

    if (shape == Circle)		// not too difficult

    {

	// compute effective value of father_son_distance

	// if rigid_flexible_flag is set 1
	// employ a function with returns
	// the base value for nodes of average area
	// and converges to double base value for very large nodes

	father_son_distance =
	    (1 - rigid_flexible_flag) * father_son_distance 
	    +	rigid_flexible_flag *
	    (4 * father_son_distance / M_PI)
	    * atan( node_area  / avg_node_area );

	// now compute polar radius of son

	result = polar_radius_of_father 
	    + width_of_father
	    + father_son_distance
	    + width_of_node;

    }


    else if (shape == Rectangle)		// somewhat tricky

    {

	// old circle oriented interface reads HALF the width
	width_of_father *= 2;
	
	// first, determine QuadStatus of father

	radial_polar_coordinate position_of_father;
	position_of_father.set_center(center_of_layoutX,center_of_layoutY);
	position_of_father.set_angle(polar_angle_of_father);
	position_of_father.set_radius(polar_radius_of_father);
	
	QuadStatus quadstatus_of_father = quadstatus_of_rectangle(
	    position_of_father.cartesian_x(),
	    position_of_father.cartesian_y(),
	    width_of_father,
	    height_of_father,
	    center_of_layoutX,
	    center_of_layoutY);



	// second, compute according polar radius

	// note that this is not the actual polar radius of the node
	// but an intermediate value ("inner radius")

	// the 16 cases correspond to different relevant points of father
	// and are illustrated in my thesis

	// for each composite QuadStatus (NE,SE,NW,SW)
	// there are 3 cases
	// which depend on orientation of layout center and relevant diagonal

	// we introduce the LEDA "point" data type for this computation

	// for each simple QuadStatus (N,E,S,W)
	// select maximum distance of relevant points of father

	// a special case arises when father == root

	GT_Point layout_center_point(center_of_layoutX,center_of_layoutY);
	GT_Point father_rectangle_SW_point(position_of_father.cartesian_x() - 0.5*width_of_father, position_of_father.cartesian_y() - 0.5*height_of_father);
	GT_Point father_rectangle_SE_point(position_of_father.cartesian_x() + 0.5*width_of_father, position_of_father.cartesian_y() - 0.5*height_of_father);
	GT_Point father_rectangle_NW_point(position_of_father.cartesian_x() - 0.5*width_of_father, position_of_father.cartesian_y() + 0.5*height_of_father);
	GT_Point father_rectangle_NE_point(position_of_father.cartesian_x() + 0.5*width_of_father, position_of_father.cartesian_y() + 0.5*height_of_father);

	// introducing some auxiliary values to simplify formulae

	int orientation_to_diagonal;
	double aux_angle;		
	const double hdiagonal = 0.5*sqrt(width_of_father*width_of_father + height_of_father*height_of_father);	// half length of father rectangle diagonal



	// case NE	(could call subcases "NEE", "NNE", "NE!")
	
	// subcases left & right differ in aux_angle
	// subcase collinear is trivial
	
	if (quadstatus_of_father == NE)
	{
	    // relevant diagonal is SW -> NE
		
	    orientation_to_diagonal = orientation(
		father_rectangle_SW_point,
		father_rectangle_NE_point,
		layout_center_point);

	    if (orientation_to_diagonal == +1)	// left
	    {
		aux_angle = 1.5*M_PI - (M_PI/2 - polar_angle_of_father) - atan(height_of_father/width_of_father);  // this was a BUG (M_PI - polar ...
		result = sqrt(polar_radius_of_father * polar_radius_of_father + hdiagonal * hdiagonal - 2 * polar_radius_of_father * hdiagonal * cos(aux_angle));
	    }

	    else if (orientation_to_diagonal == -1)		// right
	    {
		aux_angle = 1.5*M_PI - polar_angle_of_father - atan(width_of_father/height_of_father);
		result = sqrt(polar_radius_of_father * polar_radius_of_father + hdiagonal * hdiagonal - 2 * polar_radius_of_father * hdiagonal * cos(aux_angle));
	    }

	    else if (orientation_to_diagonal == 0)		// collinear
	    {
		result = polar_radius_of_father + hdiagonal;
	    }				
	}	
	

	
	// case SE

	if (quadstatus_of_father == SE)
	{
	    // relevant diagonal is NW -> SE

	    orientation_to_diagonal = orientation(
		father_rectangle_NW_point,
		father_rectangle_SE_point,
		layout_center_point);

	    if (orientation_to_diagonal == +1)		// left
	    {
		aux_angle = 1.5*M_PI - (2*M_PI - polar_angle_of_father) - atan(width_of_father/height_of_father);
		result = sqrt(polar_radius_of_father * polar_radius_of_father + hdiagonal * hdiagonal - 2 * polar_radius_of_father * hdiagonal * cos(aux_angle));
	    }

	    else if (orientation_to_diagonal == -1)		// right
	    {
		aux_angle = 1.5*M_PI - (polar_angle_of_father - 1.5*M_PI) - atan(height_of_father/width_of_father);
		result = sqrt(polar_radius_of_father * polar_radius_of_father + hdiagonal * hdiagonal - 2 * polar_radius_of_father * hdiagonal * cos(aux_angle));
	    }
		
	    else if (orientation_to_diagonal == 0)		// collinear
	    {
		result = polar_radius_of_father + hdiagonal;
	    }
	}



	// case NW

	if (quadstatus_of_father == NW)
	{
	    // relevant diagonal is SE -> NW

	    orientation_to_diagonal = orientation(
		father_rectangle_SE_point,
		father_rectangle_NW_point,
		layout_center_point);

	    if (orientation_to_diagonal == +1)		// left
	    {
		aux_angle = 1.5*M_PI - (M_PI - polar_angle_of_father) - atan(width_of_father/height_of_father);
		result = sqrt(polar_radius_of_father * polar_radius_of_father + hdiagonal * hdiagonal - 2 * polar_radius_of_father * hdiagonal * cos(aux_angle));
	    }

	    else if (orientation_to_diagonal == -1)		// right
	    {
		aux_angle = 1.5*M_PI - (polar_angle_of_father - M_PI/2) - atan(height_of_father/width_of_father);
		result = sqrt(polar_radius_of_father * polar_radius_of_father + hdiagonal * hdiagonal - 2 * polar_radius_of_father * hdiagonal * cos(aux_angle));
	    }
		
	    else if (orientation_to_diagonal == 0)
	    {
		result = polar_radius_of_father + hdiagonal;
	    }
	}


	// case SW

	if (quadstatus_of_father == SW)
	{
	    // relevant diagonal is NE -> SW

	    orientation_to_diagonal = orientation(
		father_rectangle_NE_point,
		father_rectangle_SW_point,
		layout_center_point);

	    if (orientation_to_diagonal == +1)		// left
	    {
		aux_angle = 1.5*M_PI - (1.5*M_PI - polar_angle_of_father) - atan(height_of_father/width_of_father);
		result = sqrt(polar_radius_of_father * polar_radius_of_father + hdiagonal * hdiagonal - 2 * polar_radius_of_father * hdiagonal * cos(aux_angle));
	    }
		
	    else if (orientation_to_diagonal == -1)		// right
	    {
		aux_angle = 1.5*M_PI - (polar_angle_of_father - M_PI) - atan(width_of_father/height_of_father);
		result = sqrt(polar_radius_of_father * polar_radius_of_father + hdiagonal * hdiagonal - 2 * polar_radius_of_father * hdiagonal * cos(aux_angle));
	    }

	    else if (orientation_to_diagonal == 0)		// collinear
	    {
		result = polar_radius_of_father + hdiagonal;
	    }
	}

	

	// remaining 4 cases for simple QuadStatus

	// each one involves 2 relevant father points

	double aux_angle1, aux_angle2;

	// case E

	if (quadstatus_of_father == E)
	{
	    aux_angle1 = 1.5*M_PI - (M_PI/2 - polar_angle_of_father) - atan(height_of_father/width_of_father);
	    aux_angle2 = 1.5*M_PI - polar_angle_of_father - atan(height_of_father/width_of_father); //maybe this should be M_PI - ...
	    result = max(sqrt(polar_radius_of_father * polar_radius_of_father + hdiagonal * hdiagonal - 2 * polar_radius_of_father * hdiagonal * cos(aux_angle1)) , sqrt(polar_radius_of_father * polar_radius_of_father + hdiagonal * hdiagonal - 2 * polar_radius_of_father * hdiagonal * cos(aux_angle2)));
	}


	// case S

	if (quadstatus_of_father == S)
	{
	    aux_angle1 = 1.5*M_PI - (polar_angle_of_father - M_PI) - atan(width_of_father/height_of_father);
	    aux_angle2 = 1.5*M_PI - (2*M_PI - polar_angle_of_father) - atan(width_of_father/height_of_father);
	    result = max(sqrt(polar_radius_of_father * polar_radius_of_father + hdiagonal * hdiagonal - 2 * polar_radius_of_father * hdiagonal * cos(aux_angle1)) , sqrt(polar_radius_of_father * polar_radius_of_father + hdiagonal * hdiagonal - 2 * polar_radius_of_father * hdiagonal * cos(aux_angle2)));
	}
	

	// case W

	if (quadstatus_of_father == W)
	{
	    aux_angle1 = 1.5*M_PI - (polar_angle_of_father - M_PI/2) - atan(height_of_father/width_of_father);
	    aux_angle2 = 1.5*M_PI - (1.5*M_PI - polar_angle_of_father) - atan(height_of_father/width_of_father);
	    result = max(sqrt(polar_radius_of_father * polar_radius_of_father + hdiagonal * hdiagonal - 2 * polar_radius_of_father * hdiagonal * cos(aux_angle1)) , sqrt(polar_radius_of_father * polar_radius_of_father + hdiagonal * hdiagonal - 2 * polar_radius_of_father * hdiagonal * cos(aux_angle2)));
	}



	// case N

	if (quadstatus_of_father == N)
	{
	    aux_angle1 = 1.5*M_PI - (M_PI - polar_angle_of_father) - atan(width_of_father/height_of_father);
	    aux_angle2 = 1.5*M_PI - polar_angle_of_father - atan(width_of_father/height_of_father);
	    result = max(sqrt(polar_radius_of_father * polar_radius_of_father + hdiagonal * hdiagonal - 2 * polar_radius_of_father * hdiagonal * cos(aux_angle1)) , sqrt(polar_radius_of_father * polar_radius_of_father + hdiagonal * hdiagonal - 2 * polar_radius_of_father * hdiagonal * cos(aux_angle2)));
	}


	// special case

	if (quadstatus_of_father == ILLEGAL_QuadStatus)

	    // father must be root 
	    // circumcircle it !

	{
	    result = hdiagonal;
	}
	





	// third, final computations
	
	father_son_distance =
	    (1 - rigid_flexible_flag) * father_son_distance 
	    +	rigid_flexible_flag *
	    (4 * father_son_distance / M_PI)
	    * atan( node_area  / avg_node_area );

	result += father_son_distance;

    }

    return result;



    height_of_son = height_of_son;	// avoid compiler complaint
}




// compute polar angle
// old version for circle nodes only
// this version is obsolete
/*
  double compute_polar_angle(
  double supporting_angle,
  double spanned_angle)
  {
  double result;

  result = supporting_angle + spanned_angle;

  return result;
  }*/



// expanded version handles rectangles, too

double compute_polar_angle(
    double supporting_angle,
    double polar_radius,
    double w,		// width
    double h,		// height
    double pad,		// padding factor 1 = no space
	
    GeometricNodeShape shape)	// expanded interface
{

    double result;


    if (shape == Circle)

    {

	double node_radius = w/2 ;

	// here we have a potential problem when the padding factor is too high
	// as this will cause atan("positive infinite") -> M_PI/2
	// or, worse, sqrt("negative")
	// so pad is re-assigned a legal value

	if (polar_radius * polar_radius - pad * pad * node_radius * node_radius <= 0)
	    pad = (polar_radius / node_radius) * 0.95 ;


	result = supporting_angle +
	    atan(pad * node_radius / sqrt(polar_radius * polar_radius - pad * pad * node_radius * node_radius));
		
    }


    if (shape == Rectangle)

    {
	
	// note that for rectangular nodes 
	// polar computation is in 2 steps
	
	// inside compute_polar_angle and compute_polar_radius
	// we compute intermediate coordinates
	// which refer to socalled "inner orbit" (see my thesis for details)

	// there are 12 cases
	// according to QuadStatus of supporting_angle
	
	// simple QuadStatus implies unique position

	// composite QuadStatus implies two different kinds of position
	// called "vertex touch" and "edge touch"

	
	QuadStatus quadstatus_of_support = quadstatus_of_ray(supporting_angle);

	// simple QuadStatus
	// implies 4 cases of "vertex touch"

	// case E implies SW-vertex touch

	if (quadstatus_of_support == E)
	    return 0;

	// case N implies SE-vertex touch

	if (quadstatus_of_support == N)
	    return M_PI/2;

	// case W implies NE-vertex touch

	if (quadstatus_of_support == W)
	    return M_PI;

	// case S implies NW-vertex touch
	
	if (quadstatus_of_support == S)
	    return 1.5*M_PI;


	// composite QuadStatus

	// each major case contains two subcases 
	// according to dimensions of rectangle

	// in case of "vertex touch" result refers to "inner radius"
	// in case of "edge touch" result points to adjacent quadrant

	double critical_width, critical_height;
	
	// case NE

	if (quadstatus_of_support == NE)
	{

	    critical_width = polar_radius * tan(M_PI/2 - supporting_angle);

	    // this was a bug				tan(M_PI - supporting_angle);

	    if (w <= critical_width)		// SW-vertex touch
	    {
		result = supporting_angle + asin(w * sin(supporting_angle) / polar_radius);
	    }
	    else				// S-edge touch
	    {
		result = M_PI;
	    }
	}	


	// case NW

	if (quadstatus_of_support == NW)
	{

	    critical_height = polar_radius * tan(M_PI - supporting_angle);

	    if (h <= critical_height)		// SE-vertex touch
	    {
		result = supporting_angle + asin(h * sin(supporting_angle - M_PI/2) / polar_radius);
	    }
	    else				// E-edge touch
	    {
		result = 1.5*M_PI;
	    }
	}	


	// case SW

	if (quadstatus_of_support == SW)
	{

	    critical_width = polar_radius * tan(1.5*M_PI - supporting_angle);
	    if (w <= critical_width)		// NE-vertex touch
	    {
		result = supporting_angle + asin(w * sin(supporting_angle - M_PI) / polar_radius);
	    }
	    else				// N-edge touch
	    {
		result = 0;
	    }
	}


	// case SE

	if (quadstatus_of_support == SE)
	{

	    critical_height = polar_radius * tan(2*M_PI - supporting_angle);
	    if (h <= critical_height)		// NW-vertex touch
	    {
		result = supporting_angle + asin(h * sin(supporting_angle - 1.5*M_PI) / polar_radius);
	    }
	    else				// W-edge touch
	    {
		result = M_PI/2;
	    }
	}
	


    }

    return result;

}








// this function may seem redundant but there will be major differences
// when rectangles are introduced

// current version is for circles only

double compute_polar_wedge_to(
    double supporting_angle,
    double polar_radius,
    double w,		// width
    double h,		// height
    double pad,		// padding factor 1 = no space

    GeometricNodeShape shape,	// extended interface
    double polar_angle,
    double layout_centerX,
    double layout_centerY)
{

    double result;


    if (shape == Circle)

    {

	double node_radius = w/2 ;

	// same check as in compute_polar_angle
	if (polar_radius * polar_radius - pad * pad * node_radius * node_radius <= 0)
	    pad = (polar_radius / node_radius) * 0.95 ;


	result = supporting_angle + 
	    2 * atan(pad * node_radius / sqrt(polar_radius * polar_radius - pad * pad * node_radius * node_radius));
		
	// former computation was ok until colfam option has been introduced
	// if polar angle has changed, it must be used :
	
	result = polar_angle +
	    atan(pad * node_radius / sqrt(polar_radius * polar_radius - pad * pad * node_radius * node_radius));




    }



    else if (shape == Rectangle)

    {

	// polar coordinates could be used
	// but we shall employ cartesian coordinates

	radial_polar_coordinate center_of_rectangle;
	double center_of_rectangleX, center_of_rectangleY;

	center_of_rectangle.set_center(layout_centerX, layout_centerY);
	center_of_rectangle.set_radius(polar_radius);
	center_of_rectangle.set_angle(polar_angle);

	center_of_rectangleX = center_of_rectangle.cartesian_x();
	center_of_rectangleY = center_of_rectangle.cartesian_y();



	// there are 8 cases according to the node's QuadStatus
	
	QuadStatus quad_status_of_node = quadstatus_of_rectangle(center_of_rectangleX, center_of_rectangleY, w, h, layout_centerX, layout_centerY);


	// each QuadStatus implies different pair of spanning points

	double spanning1x, spanning1y, spanning2x, spanning2y;

	if (quad_status_of_node == W)
	{
	    spanning1x = center_of_rectangleX + w/2; // flipped bug
	    spanning1y = center_of_rectangleY - h/2;
	    spanning2x = center_of_rectangleX + w/2; // flipped bug
	    spanning2y = center_of_rectangleY + h/2;
	}

	
		
	if (quad_status_of_node == N)
	{
	    spanning1x = center_of_rectangleX + w/2;
	    spanning1y = center_of_rectangleY - h/2;
	    spanning2x = center_of_rectangleX - w/2;
	    spanning2y = center_of_rectangleY - h/2;
	}


	if (quad_status_of_node == E)
	{
	    spanning1x = center_of_rectangleX - w/2; // flipped bug
	    spanning1y = center_of_rectangleY + h/2;
	    spanning2x = center_of_rectangleX - w/2; // flipped bug
	    spanning2y = center_of_rectangleY - h/2;
	}


	if (quad_status_of_node == S)
	{
	    spanning1x = center_of_rectangleX - w/2;
	    spanning1y = center_of_rectangleY + h/2;
	    spanning2x = center_of_rectangleX + w/2;
	    spanning2y = center_of_rectangleY + h/2;
	}


	if (quad_status_of_node == NE)
	{
	    spanning1x = center_of_rectangleX + w/2;
	    spanning1y = center_of_rectangleY - h/2;
	    spanning2x = center_of_rectangleX - w/2;
	    spanning2y = center_of_rectangleY + h/2;
	}


	if (quad_status_of_node == NW)
	{
	    spanning1x = center_of_rectangleX + w/2;
	    spanning1y = center_of_rectangleY + h/2;
	    spanning2x = center_of_rectangleX - w/2;
	    spanning2y = center_of_rectangleY - h/2;
	}

	
	if (quad_status_of_node == SW)
	{
	    spanning1x = center_of_rectangleX - w/2;
	    spanning1y = center_of_rectangleY + h/2;
	    spanning2x = center_of_rectangleX + w/2;
	    spanning2y = center_of_rectangleY - h/2;
	}


	if (quad_status_of_node == SE)
	{
	    spanning1x = center_of_rectangleX - w/2;
	    spanning1y = center_of_rectangleY - h/2;
	    spanning2x = center_of_rectangleX + w/2;
	    spanning2y = center_of_rectangleY + h/2;
	}



	// now compute angle included between
	// spanning point 1, center of layout, spanning point 2

	// we employ a formula from linear algebra & analytic geometry
	// scalar product = product of norms * cos(angle)

	double vector1x = spanning1x - layout_centerX;
	double vector1y = spanning1y - layout_centerY;
	double vector2x = spanning2x - layout_centerX;
	double vector2y = spanning2y - layout_centerY;

	result = supporting_angle +	acos(
	    (vector1x * vector2x + vector1y * vector2y) /
	    ( sqrt(vector1x*vector1x + vector1y*vector1y)
		*sqrt(vector2x*vector2x + vector2y*vector2y)));




			


    }


    return result;

}





// this function is used for computation of 
// maximum allowed polar angle in an escaping edge situation

// the escaping edge is from father_v to v
// the potential clashing node is pot_obstacle

double compute_max_polar_angle_anti_esc(
    double pot_obstacle_polar_radius,
    double pot_obstacle_polar_angle,
    double pot_obstacle_width,
    double pot_obstacle_height,
    double father_v_polar_radius,
    double father_v_polar_angle,
    double v_polar_radius,
    GeometricNodeShape shape,
    double layoutcenterx, double layoutcentery)
{

    double result;

    if (shape == Circle)

    {

	// this is the circle version
	// the formulae are the result of a geometric argument in my thesis

	double testH1, testANGMIN, testGAMMACOMP, testGAMMA, testBETA;

	testH1 = sqrt(pot_obstacle_polar_radius * pot_obstacle_polar_radius + father_v_polar_radius * father_v_polar_radius - 2 * pot_obstacle_polar_radius * father_v_polar_radius * cos(father_v_polar_angle - pot_obstacle_polar_angle));

	testANGMIN = asin(0.5 * pot_obstacle_width / testH1);

	testGAMMACOMP = asin(pot_obstacle_polar_radius * sin(father_v_polar_angle - pot_obstacle_polar_angle) / testH1);

	testGAMMA = testANGMIN - testGAMMACOMP;

	testBETA = asin(sin(testGAMMA) * father_v_polar_radius / v_polar_radius);

	result = M_PI - testBETA - testGAMMA + father_v_polar_angle;

	return result;

    }

	
	

    else if (shape == Rectangle)

    {

	// first, determine orientation of father versus potential obstacle

	radial_polar_coordinate pot_obstacle_position;
	pot_obstacle_position.set_radius(pot_obstacle_polar_radius);
	pot_obstacle_position.set_angle(pot_obstacle_polar_angle);
	pot_obstacle_position.set_center(layoutcenterx, layoutcentery);

	radial_polar_coordinate father_position;
	father_position.set_radius(father_v_polar_radius);
	father_position.set_angle(father_v_polar_angle);
	father_position.set_center(layoutcenterx, layoutcentery);

	
	QuadStatus orientation_father_to_obstacle = 
	    oriented_quadstatus_to_rectangle(father_position.cartesian_x(), father_position.cartesian_y(), pot_obstacle_position.cartesian_x(), pot_obstacle_position.cartesian_y(), pot_obstacle_width, pot_obstacle_height);

	
	// second, determine relevant obstacle contact point
	// see my thesis for illustration

	double obstacle_contactx, obstacle_contacty;

	double w = pot_obstacle_width;
	double h = pot_obstacle_height;

	if (orientation_father_to_obstacle == N			// major case
	    || orientation_father_to_obstacle == NW)	// minor case
	{
	    obstacle_contactx = pot_obstacle_position.cartesian_x() - w/2;
	    obstacle_contacty = pot_obstacle_position.cartesian_y() + h/2;
	}

	if (orientation_father_to_obstacle == E			// major case
	    || orientation_father_to_obstacle == NE)	// minor case
	{
	    obstacle_contactx = pot_obstacle_position.cartesian_x() + w/2;
	    obstacle_contacty = pot_obstacle_position.cartesian_y() + h/2;
	}

	if (orientation_father_to_obstacle == S			// major case
	    || orientation_father_to_obstacle == SE)	// minor case
	{
	    obstacle_contactx = pot_obstacle_position.cartesian_x() + w/2;
	    obstacle_contacty = pot_obstacle_position.cartesian_y() - h/2;
	}

	if (orientation_father_to_obstacle == W			// major case
	    || orientation_father_to_obstacle == SW)	// minor case
	{
	    obstacle_contactx = pot_obstacle_position.cartesian_x() - w/2;
	    obstacle_contacty = pot_obstacle_position.cartesian_y() - h/2;
	}


	GT_Point obstacle_contact_point(obstacle_contactx, obstacle_contacty);

	
	// third, compute orientation of obstacle contact point
	// to segment root --> center of father
	
	GT_Point	root_point(layoutcenterx, layoutcentery);
	GT_Point	father_point(father_position.cartesian_x(), father_position.cartesian_y());

	int contact_orientation = orientation(root_point,
	    father_point,
	    obstacle_contact_point);


	// finally, compute maximum allowed polar angle

	// again, see my thesis for geometric illustration
	// somewhat cryptic names refer to my original paper ...
	// in my thesis : red = rho && green = gamma
	
	double green_angle = orthogonal_angle(
	    layoutcenterx - father_position.cartesian_x(),
	    layoutcentery - father_position.cartesian_y(),
	    obstacle_contactx - father_position.cartesian_x(),
	    obstacle_contacty - father_position.cartesian_y());


	double red_angle = M_PI - green_angle - asin(father_v_polar_radius * sin(green_angle) / v_polar_radius);


	
	if (contact_orientation == +1)		// left
		
	    result = father_v_polar_angle + red_angle;

	else if (contact_orientation == -1)	// right

	    result = father_v_polar_angle + 2*M_PI - red_angle;

	else					// collinear

	    result = father_v_polar_angle + M_PI;
	

	
	return result;	

    }



    return result;	// avoid compiler complaint
}









// this function is used for layout correction & optimization
// it computes the polar radius necessary
// to achieve the given polar spanning angle 

// current version is for circles only

// this function is no longer used !!!!!!!!!!!!!!

double compute_increased_polar_radius(
    double spanning_angle,
    double w,		// width
    double h,		// heigth
    double pad)		// padding-factor
{

    double result;

    double node_radius = w/2;

    result = sqrt( pad * pad * node_radius * node_radius +
	pow( (pad * node_radius) / tan(spanning_angle/2), 2));

    return result;


    h = h;	// avoid compiler complaint
}





// method for finding biggest (area) node

node Layout_Radial_Algorithm_Implementation::find_biggest_node()
{
    node v, biggest;

    biggest = my_graph->attached()->choose_node();

    forall_nodes(v, *(my_graph->attached()))
	{
	    if (area_of_node(my_graph->gt(v).graphics()->w(),
		my_graph->gt(v).graphics()->h(), shape_of_nodes) >
		area_of_node(my_graph->gt(biggest).graphics()->w(),
		    my_graph->gt(biggest).graphics()->h(), shape_of_nodes))
		biggest = v;
	}

    return biggest;
}



// function which is given all relevant geometric data
// to compute the area of a node
// without having access to class Layout_Radial_Algorithm_Implementation

// has been extended 22/08/97

float area_of_node(float width, float height, GeometricNodeShape shape)
{
    if (shape == Circle)				// width == height
	return 0.25 * width * height * M_PI ;
    else if (shape == Rectangle)
	return width * height ;
    else if (shape == Oval)				// width != height
	return 0.25 * width * height * M_PI ;
    else if (shape == Other)
	return width * height ;

    return 0;
}







/*			*************************
			SOFTWARE MAINTENANCE NOTE
			*************************

	function recompute_polar_radius 
	has some problems with inner angles exceeding 2 * M_PI
	as its case statements don't always handle them correctly

	thus rectangular layout with automatic expansion OFF
	can lead to chaotic drawings

	automatic expansion ON shall never yield polar angles 
	significantly beyond 2 * M_PI thus the case statements
	of the function have been extended to correctly
	handle inner angles up to 5/2 * M_PI

	this should prevent trouble in all (???) realistic cases
	otherwise a more elaborated case statement shall be
	supplied ...

*/











// transformation of intermediate polar coordinates 
// for rectangle nodes 
// into actual polar coordinates 

double recompute_polar_radius(
    double inner_radius,	// in my thesis : preradius
    double inner_angle,	// im my thesis : preangle
    double supporting_angle,
    double w,
    double h,
    double center_of_layoutX,
    double center_of_layoutY)
{
    // we use LEDA data type "point" 
    // for computing the center of the rectangle
    // and thus the euclidian distance from the layout center

    GT_Point layout_center(center_of_layoutX,center_of_layoutY);

    double rec_centerX, rec_centerY;	// to be computed

    // cases reflect compute_polar_angle for rectangles

    QuadStatus quadstatus_of_support = quadstatus_of_ray(supporting_angle);


    // simple QuadStatus : 4 cases

	// case E

    if (quadstatus_of_support == E)
    {
	rec_centerX = center_of_layoutX + inner_radius + w/2 ;
	rec_centerY = center_of_layoutY + h/2 ;
    }

    // case N

    if (quadstatus_of_support == N)
    {
	rec_centerX = center_of_layoutX - w/2 ;
	rec_centerY = center_of_layoutY + inner_radius + h/2 ;
    }
	
    // case W

    if (quadstatus_of_support == W)
    {
	rec_centerX = center_of_layoutX - inner_radius - w/2 ;
	rec_centerY = center_of_layoutY - h/2 ;
    }

    // case S

    if (quadstatus_of_support == S)
    {
	rec_centerX = center_of_layoutX + w/2;
	rec_centerY = center_of_layoutY - inner_radius - h/2 ;
    }



	

    // composite QuadStatus

	// each case has 2 subcases

    double critical_width, critical_height;

    // case NE

    if (quadstatus_of_support == NE)
		
	if ((inner_angle <= M_PI/2)		// SW-vertex touch

	    // see maintenance note for expanded condition 

	    || (inner_angle - 2*M_PI < M_PI/2
		&& inner_angle - 2*M_PI > 0))
	{
	    rec_centerX = center_of_layoutX + inner_radius * cos(inner_angle) + w/2;	
	    rec_centerY = center_of_layoutY + inner_radius * sin(inner_angle) + h/2;		

	    // old computation - overly complicated
	    //			rec_centerX = center_of_layoutX + sqrt((inner_radius*inner_radius) / (1+tan(inner_angle)*tan(inner_angle))) + w/2;
	    //			rec_centerY = center_of_layoutY + sqrt((inner_radius*inner_radius) / (1+1/(tan(inner_angle)*tan(inner_angle)))) + h/2;
	}
	else					// S-edge touch
	{
	    critical_width = inner_radius * tan(M_PI/2 - supporting_angle);		

	    // this was a bug					tan(M_PI - ...

	    rec_centerX = center_of_layoutX + critical_width - w/2;
	    rec_centerY = center_of_layoutY + inner_radius + h/2;
	}


    // case NW

    if (quadstatus_of_support == NW)

	if (inner_angle <= M_PI)		// SE-vertex touch
	{
	    rec_centerX = center_of_layoutX - inner_radius * cos(M_PI - inner_angle) - w/2;
	    rec_centerY = center_of_layoutY + inner_radius * sin(M_PI - inner_angle) + h/2;

	    // old computation - overly complicated
	    //			rec_centerX = center_of_layoutX - sqrt((inner_radius*inner_radius) / (1+tan(M_PI-inner_angle)*tan(M_PI-inner_angle))) - w/2;
	    //			rec_centerY = center_of_layoutY + sqrt((inner_radius*inner_radius) / (1+1/(tan(M_PI-inner_angle)*tan(M_PI-inner_angle)))) + h/2;
	}
	else					// E-edge touch
	{
	    critical_height = inner_radius * tan(M_PI - supporting_angle);
	    rec_centerX = center_of_layoutX - inner_radius - w/2;
	    rec_centerY = center_of_layoutY + critical_height -h/2;
	}



    // case SW

    if (quadstatus_of_support == SW)

	if (inner_angle != 0)			// NE-vertex touch
	{
	    rec_centerX = center_of_layoutX - inner_radius * cos(inner_angle - M_PI) - w/2;
	    rec_centerY = center_of_layoutY - inner_radius * sin(inner_angle - M_PI)- h/2;

	    // old computation - overly complicated
	    //			rec_centerX = center_of_layoutX - sqrt((inner_radius*inner_radius) / (1+tan(inner_angle-M_PI)*tan(inner_angle-M_PI))) - w/2;
	    //			rec_centerY = center_of_layoutY - sqrt((inner_radius*inner_radius) / (1+1/(tan(inner_angle-M_PI)*tan(inner_angle-M_PI)))) - h/2;
	}
	else					// N-edge touch
	{
	    critical_width = inner_radius * tan(1.5*M_PI - supporting_angle);
	    rec_centerX = center_of_layoutX - critical_width + w/2;
	    rec_centerY = center_of_layoutY - inner_radius - h/2;
	}

    // case SE

    if (quadstatus_of_support == SE)

	if (inner_angle >= 1.5*M_PI)		// NW-vertex touch
	{
	    rec_centerX = center_of_layoutX + inner_radius * cos(2*M_PI - inner_angle) + w/2;
	    rec_centerY = center_of_layoutY - inner_radius * sin(2*M_PI - inner_angle) - h/2;

	    // old computation - overly complicated
	    //			rec_centerX = center_of_layoutX + sqrt((inner_radius*inner_radius) / (1+tan(2*M_PI-inner_angle)*tan(2*M_PI-inner_angle))) + w/2;
	    //			rec_centerY = center_of_layoutY - sqrt((inner_radius*inner_radius) / (1+1/(tan(2*M_PI-inner_angle)*tan(2*M_PI-inner_angle)))) - h/2;
	}
	else					// W-edge touch
	{
	    critical_height = inner_radius * tan(2*M_PI - supporting_angle);
	    rec_centerX = center_of_layoutX + inner_radius + w/2;
	    rec_centerY = center_of_layoutY - critical_height +h/2;
	}




		
    GT_Point rec_center(rec_centerX,rec_centerY);


// cout << "recomputed polar radius to " << layout_center.distance(rec_center) << endl;

    return layout_center.distance(rec_center);

}










// for rectangular nodes
// this function computes actual polar angle
// given intermediate radius & angle and actual polar radius

double recompute_polar_angle(
    double inner_radius,
    double inner_angle,
    double supporting_angle,
    double w,
    double h,
    double polar_radius)
{

    double result;


    // the 12 cases correspond to compute_polar_angle

    QuadStatus quadstatus_of_support = quadstatus_of_ray(supporting_angle);

    // simple QuadStatus
    // implies 4 cases of vertex touch

    double hdiagonal = 0.5*sqrt(w*w+h*h);	// node half diagonal

    // case E

    if (quadstatus_of_support == E)
	result = asin(hdiagonal * sin(M_PI-atan(h/w)) / polar_radius);

    // case N

    if (quadstatus_of_support == N)
	result = asin(hdiagonal * sin(M_PI-atan(w/h)) / polar_radius);

    // case W

    if (quadstatus_of_support == W)
	result = asin(hdiagonal * sin(M_PI-atan(h/w)) / polar_radius);

    // case S

    if (quadstatus_of_support == S)
	result = asin(hdiagonal * sin(M_PI-atan(w/h)) / polar_radius);


    // composite QuadStatus
    // each implies 2 subcases : vertex touch & edge touch
    // which are distinguished by previously computed "inner angle"
	
    // case NE

    if (quadstatus_of_support == NE)
    {

	if (inner_angle <= M_PI/2)		// SW-vertex touch
	{
	    result = supporting_angle + asin(sin(supporting_angle + atan(h/w)) * hdiagonal / polar_radius);
	}
	else					// S-edge touch
	{
	    result = supporting_angle + asin(sin(supporting_angle + atan(h/w)) * hdiagonal / polar_radius);
	}
    }


    // case NW

    if (quadstatus_of_support == NW)
    {

	if (inner_angle <= M_PI)		// SE-vertex touch
	{
	    result = supporting_angle + asin(sin(supporting_angle - atan(h/w)) * hdiagonal / polar_radius);
	}
	else					// E-edge touch
	{
	    result = supporting_angle + asin(sin(supporting_angle - atan(h/w)) * hdiagonal / polar_radius);
	}

    }


    // case SW

    if (quadstatus_of_support == SW)
    {

	if (inner_angle != 0)			// NE-vertex touch
	{
	    result = supporting_angle + asin(sin(supporting_angle - M_PI + atan(h/w)) * hdiagonal / polar_radius);
	}
	else					// N-edge touch
	{
	    result = supporting_angle + asin(sin(supporting_angle - M_PI + atan(h/w)) * hdiagonal / polar_radius);
	}
    }

	

    // case SE

    if (quadstatus_of_support == SE)
    {

	if (inner_angle > M_PI/2)		// NW-vertex touch
	{
	    result = supporting_angle + asin(sin(supporting_angle - M_PI - atan(h/w)) * hdiagonal / polar_radius);
	}
	else					// W-edge touch
	{
	    result = supporting_angle + asin(sin(supporting_angle - M_PI - atan(h/w)) * hdiagonal / polar_radius);
	}
    }





    return result;


    inner_radius = inner_radius;	// avoid compiler complaint
}







// collinear families are explained in my thesis

double compute_min_anti_collinear_family_angle(
    double node_polar_radius,
    double sibling_polar_radius,
    double sibling_polar_angle,
    double sibling_w,
    double sibling_h,
    double father_polar_radius,
    double father_polar_angle,
    GeometricNodeShape shape)
{
    double result;

	

    if (shape == Circle)
    {

	double s = sqrt(father_polar_radius * father_polar_radius + sibling_polar_radius * sibling_polar_radius - 2 * father_polar_radius * sibling_polar_radius * cos(father_polar_angle - sibling_polar_angle));

	double epsilon = asin(sibling_w/(2*s));

	double beta = asin((sibling_polar_radius/s) * 
	    sin(sibling_polar_angle - father_polar_angle));

	double alpha = asin((father_polar_radius/node_polar_radius) *
	    sin(beta - epsilon));

	double gamma = M_PI - alpha - beta + epsilon;

	result = gamma;

    }





    return result;

    sibling_h = sibling_h;	// avoid compiler complaint
}
	





// check for intersection

bool edge_node_intersection(
    double edge_from_x,
    double edge_from_y,
    double edge_to_x,
    double edge_to_y,
    double node_x,
    double node_y,
    double node_w,
    double node_h,
    GeometricNodeShape shape)
{
    bool result;

    if (shape == Circle)
    {
		
	GT_Segment Iedge(GT_Point(edge_from_x, edge_from_y), GT_Point(edge_to_x, edge_to_y));
	GT_Circle Inode(node_x, node_y, node_w/2);
		
	if (!(Inode.intersection(Iedge))) result = false;
	else result = true;
    }

    return result;

    node_h = node_h;	// avoid compiler complaint
}






// compute length of intersection

// this function and the previous one should be united ...

double length_of_edge_node_intersection(
    double edge_from_x,
    double edge_from_y,
    double edge_to_x,
    double edge_to_y,
    double node_x,
    double node_y,
    double node_w,
    double node_h,
    GeometricNodeShape shape)
{
    double result = 0;
	
    list<GT_Point> endpoints;
	

    if (shape == Circle)
    {
		
	GT_Segment Iedge(GT_Point(edge_from_x, edge_from_y), GT_Point(edge_to_x, edge_to_y));
	GT_Circle Inode(node_x, node_y, node_w/2);
	
	double my_dist = Inode.distance_between_intersecting_points (Iedge);

	if (my_dist > 0) {
	    // real intersection, not tangential
	    result = my_dist;
	}

	// 	if ((Inode.intersection(Iedge)).length() == 2)

	// 	    // intersection (not tangential) 
			
	// 	{
	// 	    endpoints = Inode.intersection(Iedge);
	// 	    point1 = endpoints.pop();
	// 	    point2 = endpoints.pop();
	// 	    result = point1.distance(point2);
	// 	}
    }

    return result;



    node_h = node_h;	// avoid compiler complaint
}












// Eades' method

// first generalization concerns additive property of width function

// first extension finds chains 

// second extension limits wedge size to pi 


void Layout_Radial_Algorithm_Implementation::DrawSubTree1 (
    node v, double rho, double alpha1, double alpha2)
{
    double s, alpha;
    double sum_of_width_sons_of_v = 0;

    eades_polar_radius[v] = rho;
    eades_polar_angle[v] = 0.5*(alpha1 + alpha2);

    node::adj_nodes_iterator it;
    node::adj_nodes_iterator end = v.adj_nodes_end();
    
    for (it = v.adj_nodes_begin(); it != end; ++it) {
	sum_of_width_sons_of_v += eades_width[*it];
    }

    if (sum_of_width_sons_of_v != 0)
	s = (alpha2 - alpha1) / sum_of_width_sons_of_v;

    //s = (alpha2 - alpha1) / eades_width[v];

    alpha = alpha1;

    // START OF EXTENSION : limit wedge size to pi

    if (eades_pi && v != root && (alpha2 - alpha1) > M_PI)
    {
	// don't use it all

	// bigger alpha
	alpha = 0.5 * (alpha1 + alpha2	 - M_PI);

	// smaller s
	s = M_PI / sum_of_width_sons_of_v;
    }

    // END OF EXTENSION : limit wedge size to pi



    // START OF EXTENSION : no escaping edges

    if (Eades_avoid_crossing_edges == Exclude && v != root && (alpha2 - alpha1) > 2*acos(rho/(rho + parent_child_distance)))
    {
	// don't use it all

	// bigger alpha
	alpha = 0.5 * (alpha1 + alpha2 - 2*acos(rho/(rho + parent_child_distance)));

	// smaller s
	s = 2*acos(rho/(rho + parent_child_distance)) / sum_of_width_sons_of_v;
    }

    // END OF EXTENSION : no escaping edges


    /*
      // START OF EXTENSION : find chains

      int number_of_children = 0;
      forall_adj_nodes(u,v)
      number_of_children++;

      if (number_of_children == 1 && !Chaining)
      {
      Chaining = true;
      ChainFrom.push(father(my_graph->attached(),v));
      }

      if (number_of_children != 1 && Chaining)
      {
      Chaining = false;
      ChainTo.push(v);
      }

      // END OF EXTENSION : find chains
    */


    end = v.adj_nodes_end();
    
    for (it = v.adj_nodes_begin(); it != end; ++it) {
	this->DrawSubTree1(*it,rho + parent_child_distance, alpha, 
	    alpha + s * eades_width[*it]);
	alpha = alpha + s * eades_width[*it];
    }
}


// Eades' variant
// put leaves at the margin


void Layout_Radial_Algorithm_Implementation::DrawSubTree2(node v, double rho, double alpha1, double alpha2)
{
    double s, alpha;
    node u;
    double sum_of_width_sons_of_v = 0;

    eades_polar_radius[v] = rho;
    eades_polar_angle[v] = 0.5*(alpha1 + alpha2);
    node::adj_nodes_iterator it;
    node::adj_nodes_iterator end = v.adj_nodes_end();
    
    for (it = v.adj_nodes_begin(); it != end; ++it) {
	sum_of_width_sons_of_v += eades_width[*it];
    }

    if (sum_of_width_sons_of_v != 0)
	s = (alpha2 - alpha1) / sum_of_width_sons_of_v;

    //s = (alpha2 - alpha1) / eades_width[v];

    alpha = alpha1;

    // START OF EXTENSION : limit wedge size to pi

    if (eades_pi && v != root && (alpha2 - alpha1) > M_PI)
    {
	// don't use it all

	// bigger alpha
	alpha = 0.5 * (alpha1 + alpha2 - M_PI);

	// smaller s
	s = M_PI / sum_of_width_sons_of_v;
    }

    // END OF EXTENSION : limit wedge size to pi


    // START OF EXTENSION : no escaping edges

    if (Eades_avoid_crossing_edges == Exclude && v != root && (alpha2 - alpha1) > 2*acos(rho/(rho + parent_child_distance)))
    {
	// don't use it all

	// bigger alpha
	alpha = 0.5 * (alpha1 + alpha2 - 2*acos(rho/(rho + parent_child_distance)));

	// smaller s
	s = 2*acos(rho/(rho + parent_child_distance)) / sum_of_width_sons_of_v;
    }

    // END OF EXTENSION : no escaping edges
    
    end = v.adj_nodes_end();
    
    for (it = v.adj_nodes_begin(); it != end; ++it) {
	u = *it;
	this->DrawSubTree2(u,
	    parent_child_distance * (eades_global_height_of_tree - 
		eades_h(my_graph->attached(),root,u)), 
	    alpha, alpha + s * eades_width[u]);
	    alpha = alpha + s * eades_width[u];
    }
}









//			EADES METHODS

void Layout_Radial_Algorithm_Implementation::RadialDrawTree1(node v, double rho, double alpha1, double alpha2)
{
    // auxiliary variables

    double s, alpha;
    node u;
    double sum_of_width_sons_of_v = 0;

// position v

    eades_polar_radius[v] = rho;
    eades_polar_angle[v] = 0.5*(alpha1 + alpha2);

// compute cumulative width of sons of v
    node::adj_nodes_iterator it;
    node::adj_nodes_iterator end = v.adj_nodes_end();
    
    for (it = v.adj_nodes_begin(); it != end; ++it) {
	sum_of_width_sons_of_v += eades_width[*it];
    }

    // compute step 

    if (sum_of_width_sons_of_v != 0)
	s = (alpha2 - alpha1) / sum_of_width_sons_of_v;

// compute starting angle

    alpha = alpha1;


    // IF escaping edge treatment "Variant180" --> recompute s, alpha

    if (Eades_avoid_crossing_edges == Variant180 && v != root &&
	(alpha2 - alpha1) > M_PI)

    {

	alpha = 0.5 * (alpha1 + alpha2 + - M_PI);
	s = M_PI / sum_of_width_sons_of_v;

    }

    // IF escaping edge treatment "Exclude" --> recompute s, alpha

    if (Eades_avoid_crossing_edges == Exclude && v != root &&
	(alpha2 - alpha1 > 2*acos(rho/(rho + parent_child_distance))));
    // Eades' formula
    {

	alpha = 0.5 * (alpha1 + alpha2 - 2*acos(rho/(rho + parent_child_distance)));
	s = 2*acos(rho/(rho + parent_child_distance)) / sum_of_width_sons_of_v ;

    }


    end = v.adj_nodes_end();

    for (it = v.adj_nodes_begin(); it != end; ++it) {
	u = *it;
	this->RadialDrawTree1(u,rho + parent_child_distance, alpha, 
	    alpha + s * eades_width[u]);
	alpha = alpha + s * eades_width[u];
    }
}






void Layout_Radial_Algorithm_Implementation::RadialDrawTree2(node v, double rho, double alpha1, double alpha2)
{
    // auxiliary variables

    double s, alpha;
    node u;
    double sum_of_width_sons_of_v = 0;

// position v

    eades_polar_radius[v] = rho;
    eades_polar_angle[v] = 0.5*(alpha1 + alpha2);

// compute cumulative width of sons of v
    node::adj_nodes_iterator it;
    node::adj_nodes_iterator end = v.adj_nodes_end();
    
    for (it = v.adj_nodes_begin(); it != end; ++it) {
	sum_of_width_sons_of_v += eades_width[*it];
    }

    // compute step 

    if (sum_of_width_sons_of_v != 0)
	s = (alpha2 - alpha1) / sum_of_width_sons_of_v;

// compute starting angle

    alpha = alpha1;


    // IF escaping edge treatment "Variant180" --> recompute s, alpha

    if (Eades_avoid_crossing_edges == Variant180 && v != root &&
	(alpha2 - alpha1) > M_PI)

    {

	alpha = 0.5 * (alpha1 + alpha2 + - M_PI);
	s = M_PI / sum_of_width_sons_of_v;

    }

    // IF escaping edge treatment "Exclude" --> recompute s, alpha

    if (Eades_avoid_crossing_edges == Exclude && v != root &&
	(alpha2 - alpha1 > 2*acos(rho/(rho + parent_child_distance))));
    // Eades' formula
    {

	alpha = 0.5 * (alpha1 + alpha2 - 2*acos(rho/(rho + parent_child_distance
						     )));
	s = 2*acos(rho/(rho + parent_child_distance)) / sum_of_width_sons_of_v ;

    }


    end = v.adj_nodes_end();
    
    for (it = v.adj_nodes_begin(); it != end; ++it) {
	u = *it;
	this->RadialDrawTree2(u,
	    parent_child_distance * (eades_global_height_of_tree - 
		eades_h(my_graph->attached(),root,u)), 
	    alpha, 
	    alpha + s * eades_width[u]);
	alpha = alpha + s * eades_width[u];
    }
}




    
