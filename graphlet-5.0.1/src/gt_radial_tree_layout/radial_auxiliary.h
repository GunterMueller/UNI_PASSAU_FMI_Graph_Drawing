/* This software is distributed under the Lesser General Public License */

// auxiliary stuff

// here we shall put all functions and classes
// which are neither methods nor friends of
// Layout_Radial_Algorithm_Implementation


#ifndef GT_RADIAL_AUXILIARY_H
#define GT_RADIAL_AUXILIARY_H

#include <GTL/graph.h>

// breadth first search
// takes a graph and a node 
// gives list of reachable nodes in bfs order

extern list<node> my_BFS(graph * g, node start);


// depth first search
// we need some kind of preorder traversal
// but we have unordered trees
// so preorder is not unique and equals to dfs

extern list<node> my_DFS(graph * g, node start);

#endif




// class for definition of polar coordinates
// to permit more natural expression of radial layout
// method for transformation in cartesian coordinates is provided


class radial_polar_coordinate
{

 private:

  double center_x, center_y ;

  double angle;
  double radius;


 public:

  radial_polar_coordinate();

  void set_center(double  x, double y);
  void set_angle(double a) {angle = a;};
  void set_radius(double r) {radius = r;};

  double get_angle() {return angle;};
  double get_radius() {return radius;};

  double cartesian_x();
  double cartesian_y();

};



// a simple class for storing information
// about an annulus wedge opened by a subtree

// most methods are inline


class radial_annulus_wedge
{
 private:

  double from, to;

 public:

  radial_annulus_wedge();

  void set_from(double x) {from = x;};
  void set_to(double x) {to = x;};

  double get_from() {return from;};
  double get_to() {return to;};

  double size() {return (to-from);};
};




// a simple test if two tree nodes are siblings

bool siblings(graph * g, node v, node w);

// returns parent node in directed tree

node father(graph * g, node v);



// a function we need for determining the position of node v

// it returns the most recently processed sibling of node v
// or, if none such exists, the most recently processed
// sibling of a predecessor of v
// or, if none such exists, it returns the root

node auxiliary_node(graph * g,node root, node v, list<node> order);



// a function which takes a list of possibly multiple nodes
// and returns it with all but the firstmost occurrence
// of each node deleted

// sorry, no polymorphism :(

list<node> delete_multiple_nodes(list<node> original);
			    

// maximum

double max(double x, double y);


// random integer between 1 and N

int randint(int N);





// this is the well-known algorithm
// for determining the graph-theoretic center
// of a tree

// this function had the side effect
// to insert all reverse edges into the graph
// new version keeps track of inserted edges and does the cleaning up

node center_of_tree(graph * g);



// find the source of a graph
// e.g. the (only) node with indegree = 0

// current version doesn't report an error in case of multiple sources

node source_of_graph(graph * g);



// returns the child of root
// whose subtree contains v

// for sake of totality, returns root for root

node root_of_subtree(graph * g, node root, node v);





// returns the path of nodes from v to root
// be aware : path_to_root(root) is <root> !!

list<node> path_to_root(graph * g, node root, node v);

// uses path_to_root for computing level of node v in tree
// root has level zero

int level_in_tree(graph * g, node root, node v);






// returns TRUE if v does NOT occur after position pos in nodelist nlist

// bool does_not_occur_after(node v, int pos, list<node> nlist);





// an object of this class stores geometric information about a node
// which will be temporarily changed if set options require circle nodes

class node_image
{
 private:

  double width, heigth;

  bool circle, rectangle, oval;

 public:

  node_image();

  void store_width(double w) {width = w;};
  void store_heigth(double h) {heigth = h;};

  double get_width() {return width;};
  double get_heigth() {return heigth;};

  void set_circle() {circle = true;rectangle = false;oval = false;};
  void set_rectangle() {rectangle = true;circle = false;oval = false;};
  void set_oval() {oval = true;circle = false;rectangle = false;};
  // set_other is new 22/08/97
  void set_other() {oval = false; circle = false; rectangle = false;};


  double compute_circumcircle_radius();
};









// possible positions of a geometric object
// in relation to a 2-dimensional coordinate system

// currently, positions involving the center are not supported

// for a 2-dimensional object
// simple directions N,S,W,E
// indicate positions overlapping 2 quadrants
// composite directions NW,SW,NE,SE
// indicate positions in one exclusive quadrant



enum QuadStatus {
  N,S,W,E,
  NW,SW,NE,SE,
  ILLEGAL_QuadStatus
};



// computes QuadStatus of a rectangle in a cartesian system

QuadStatus quadstatus_of_rectangle(
				   double centerX,
				   double centerY,
				   double width,
				   double height,
				   double cosystemcenterX,
				   double cosystemcenterY);




// simple function
// for sake of uniformity

// QuadStatus of a polar ray

// ray is defined by its angle

// composite QuadStatus means inside a quadrant
// simple QuadStatus means border of quadrant

QuadStatus quadstatus_of_ray(double angle);





// compute QuadStatus of a point
// relative to a rectangle

// oriented QuadStatus
// i.e. spinned by algorithm directionality

// see my thesis for illustration

QuadStatus oriented_quadstatus_to_rectangle(double pointx, double pointy,
					    double recenterx, double recentery,
					    double width, double height);




// well known formula from linear algebra & analytic geometry
// angle between 2 vectors

double orthogonal_angle(double vector1x, double vector1y,
			double vector2x, double vector2y);






// according to Eades' original algorithm

int width_of_tree(graph * g, node root, node v);


// variation w2 from my thesis

double width2_of_tree(graph * g, node root, node v);


// variation w3 from my thesis

// double width3_of_tree(graph * g, node root, node v);

// Eades' h function
// computes heigth of subtree

int eades_h(graph * g, node root, node v);
