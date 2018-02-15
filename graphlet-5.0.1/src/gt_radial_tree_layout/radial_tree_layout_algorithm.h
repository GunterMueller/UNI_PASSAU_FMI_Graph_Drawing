/* This software is distributed under the Lesser General Public License */

#ifndef GT_RADIAL_LAYOUT_IMPLEMENTATION_H
#define GT_RADIAL_LAYOUT_IMPLEMENTATION_H

#include <gt_base/Graphlet.h>

#include <gt_tcl/Tcl_Graph.h>
#include <gt_tcl/Tcl_Algorithm.h>

#include <GTL/graph.h>
#include <stack>


// enumeration type 
// for different node shapes 
// the algorithm is defined for 

enum GeometricNodeShape {
  Circle,
  Rectangle,
  Oval,
  Other
};


// various ways of selecting the root of a free tree

enum RootSelection {
  RandomNode,
  BiggestNode,
  GraphCenter,
  SourceNode
};


// possible ways of enforcing corradial nodes

enum CorradialNodes {
  None,
  Children,
  Level
};

enum EadesEscEdgeTreatment {
  Exclude,
  Variant180,
  Risiko
};




// this class contains the actual algorithm

class Layout_Radial_Algorithm_Implementation
{



 private:

// for testing only - shall be removed
//  int hshift;
//  int vshift;



  GT_Graph* my_graph;

// current version presumes uniform node shape all over my_graph

  GeometricNodeShape shape_of_nodes;

  RootSelection how_to_select_root;

  node root;

  double parent_child_distance;    // minimum possible distance

    bool avoid_escaping_edges;   // Eades' conjecture ...

  bool avoid_collinear_families;

  double padding_factor;  // horizontal padding between wedges, 1 = no space
                                                      

  bool automatic_expansion;   // automatic adaption of inadequate parameters

  int distance_rigid_flexible_flag;  // distance proportional to node size ?

  int padding_type; // original approach was variable padding

  int max_number_central_iterations;

  CorradialNodes enforce_corradial_nodes;

  bool center_parent_over_children;  // optional for circle nodes

  bool center_children_under_parent; // optional for circle nodes

  bool fill_space;  // variant of global symmetry

  bool eades; // Tano only

  bool eades2;

  bool eades_straight;

  bool eades_pi;

  bool debug;  // Tano only

  bool circumcircle_transformation;  // remember for retransformation


// actual parameters

bool Eades;

EadesEscEdgeTreatment Eades_avoid_crossing_edges;

bool Eades_border_leaves;


 public:
  
  Layout_Radial_Algorithm_Implementation();

  virtual ~Layout_Radial_Algorithm_Implementation();

//  void set_hshift(int horizontal);
//  void set_vshift(int vertical);

  void set_how_to_select_root(int root_selection);

  void set_parent_child_distance(int distance);

  void set_padding_factor(int pf);

  void set_automatic_expansion(int yn);

  void set_distance_rigid_flexible_flag(int yn);

  void set_padding_type(int yn);

  void set_avoid_escaping_edges(int yn);

  void set_avoid_collinear_families(int yn);

  void set_center_parent(int yn);

  void set_center_children(int yn);

  void set_fill_space(int yn);

  void set_enforce_corradial_nodes(int which);

  void set_eades(int d);

  void set_eades2(int d);

  void set_eades_straight(int d);

  void set_eades_pi(int d);

  
  
  void set_Eades(int d);
  void set_Eades_avoid_crossing_edges(int d);
  void set_Eades_border_leaves(int d);





  void set_debug(int d);

  void set_graph(GT_Graph* new_graph);

  void determine_shape_of_nodes();

  node find_biggest_node();

  void determine_root();

  void redirect_edges();

  virtual void now_just_do_it();

  bool set_options_require_circle_nodes();

  void digraph();



// for testing Eades' algorithm

  void DrawSubTree1(node v, double rho, double alpha1, double alpha2);

  node_map<double> eades_polar_radius;

  node_map<double> eades_polar_angle;

  node_map<double> eades_width;

  int eades_global_height_of_tree;  // height of entire tree 

  void DrawSubTree2(node v, double rho, double alpha1, double alpha2);

// for finding chains in Eades' algorithm

  stack<node> ChainFrom;
  stack<node> ChainTo;
  bool Chaining;

// Eades final version

  void RadialDrawTree1(node v, double rho, double alpha1, double alpha2);

  void RadialDrawTree2(node v, double rho, double alpha1, double alpha2);



// Eades end


};



#endif
