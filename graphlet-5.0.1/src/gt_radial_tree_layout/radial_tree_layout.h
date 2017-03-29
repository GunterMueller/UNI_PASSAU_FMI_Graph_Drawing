/* This software is distributed under the Lesser General Public License */
#ifndef GT_RADIAL_LAYOUT_H
#define GT_RADIAL_LAYOUT_H


//
// class GT_Layout_Radial_Algorithm
//

class GT_Layout_Radial_Algorithm : public GT_Algorithm {

    GT_CLASS (GT_Layout_Radial_Algorithm, GT_Algorithm);

  protected:

    //  int hshift;
    //  int vshift;
    GT_VARIABLE (int, root_selection); 
    GT_VARIABLE (int, parent_child_distance); 
    GT_VARIABLE (int, menu_padding_factor); 
    GT_VARIABLE (int, automatic_expansion); 
    GT_VARIABLE (int, distance_rigid_flexible_flag); 
    GT_VARIABLE (int, padding_type); 
    GT_VARIABLE (int, avoid_escaping_edges); 
    GT_VARIABLE (int, avoid_collinear_families); 
    GT_VARIABLE (int, center_parent); 
    GT_VARIABLE (int, center_children); 
    GT_VARIABLE (int, fill_space); 
    GT_VARIABLE (int, enforce_corradiality); 
    GT_VARIABLE (int, Eades); 
    GT_VARIABLE (int, Eades_avoid_crossing_edges); 
    GT_VARIABLE (int, Eades_border_leaves); 


  protected:

    // these are no longer supported

    int eades;

    int eades2;

    int eades_straight;

    int eades_pi;

    int debug;

    // nodes and edges which are selected in the check-routine
    list<node> the_nodes;
    list<edge> the_edges;


  public:

    GT_Layout_Radial_Algorithm (const string& name);
    virtual ~GT_Layout_Radial_Algorithm ()
	{
	}

    virtual int run (GT_Graph& g);

    virtual int check (GT_Graph& g, string& message);

};    



//
// class GT_Tcl_Layout_Radial_Algorithm
//



class GT_Tcl_Layout_Radial_Algorithm :
    public GT_Tcl_Algorithm<GT_Layout_Radial_Algorithm>
{

  public:

    GT_Tcl_Layout_Radial_Algorithm (const string& name);
    virtual ~GT_Tcl_Layout_Radial_Algorithm ();
  
    virtual int parse (GT_Tcl_info& info, int& index, GT_Tcl_Graph* g);
    virtual int check (GT_Graph& g, string& message) {
	int code = GT_Layout_Radial_Algorithm::check (g, message);
	if (!the_nodes.empty()) {
	    result (g, the_nodes);
	}
	if (!the_edges.empty()) {
	    result (g, the_edges);
	}

	if (the_nodes.empty() && the_edges.empty()) {
	    result (" ");
	}

	the_nodes.clear();
	the_edges.clear();
 	return code;
    } 
};

#endif

