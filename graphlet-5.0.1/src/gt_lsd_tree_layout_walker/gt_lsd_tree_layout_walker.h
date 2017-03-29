/* This software is distributed under the Lesser General Public License */
#ifndef GT_LSD_TREE_LAYOUT_WALKER_H
#define GT_LSD_TREE_LAYOUT_WALKER_H

//
// lsd_main.h
//

// Walter Bachl: 25.6.96
// Basic definitions to call sgraph-algorithms
// Implementation: main.cc


//
// Initialization procedure, Tcl/Tk naming conventions
//

extern "C" {
    int Gt_lsd_tree_layout_walker_Init (Tcl_Interp* interp);
}


//////////////////////////////////////////////////////////////////////
//
// Tree Layout Walker
//
//////////////////////////////////////////////////////////////////////


class GT_Tree_Walker : public GT_Algorithm
{

protected:
    int the_vertical, the_sibling, the_subtree;  // Parameters set in Dialog
    list<node> the_nodes;
    list<edge> the_edges;

public:
    GT_Tree_Walker (const string& name);
    virtual ~GT_Tree_Walker ();
    
    int sibling_separation () {
	return the_sibling;
    }
    int vertical_separation () {
	return the_vertical;
    }
    int subtree_separation () {
	return the_subtree;
    }

    void sibling_separation (int s) {
	 the_sibling = s;
    }
    void vertical_separation (int s) {
	 the_vertical = s;
    }
    void subtree_separation (int s) {
	 the_subtree = s;
    }

    virtual int run (GT_Graph& g);
    virtual int check (GT_Graph& g, string& message);
};



class GT_Tcl_Tree_Walker : public GT_Tcl_Algorithm<GT_Tree_Walker>
{
public:
    GT_Tcl_Tree_Walker (const string& name);
    virtual ~GT_Tcl_Tree_Walker ();
    
    virtual int parse (GT_Tcl_info& info, int& index, GT_Tcl_Graph* g);

    virtual int check (GT_Graph& g, string& message) {
	int code = GT_Tree_Walker::check (g, message);

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
