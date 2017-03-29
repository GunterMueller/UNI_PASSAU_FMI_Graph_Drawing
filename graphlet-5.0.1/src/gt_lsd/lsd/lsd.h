/* This software is distributed under the Lesser General Public License */
// 
// L      S      D
// Leda & Sgraph Do it
//
// an interface to run
// Sgraph-alorithms on LEDA-graph-structures

// Author: Dirk Heider
// email: heider@fmi.uni-passau.de

///////////////////////////////////////////////////////////
// MODULE DESCRIPTION
//
// The class LSD builds the interface to call Sgraph-based
// algorithms on GT-graphs.
// LSD inherits GT-access-methos from class LSD_GT and
// the possibility of associate "LEDA <=> Sgraph" from
// the class LEDA_Sgraph_association.
// It allows a application to send a GT-graph from outside
// or to order a GT-graph to be loaded from a file in
// GraphEd-format.
// By calling LSD to start a Sgraph-algorithm, the GT-graph
// is copied to a Sgraph-graph, the algorithsm is executed
// and the Sgraph is copied back into the GT-structure, if
// needed.
// Also, there are methods to secify a selection and to get
// new selection done by Sgraph.
// See file main.cc for a demonstartion of LSDs application.
///////////////////////////////////////////////////////////

#ifndef LSD_H
#define LSD_H

// LSD-standard includes
#include "lsdstd.h"


#define EMPTY_ATTR make_attr(ATTR_DATA, 0)

// model structures based on Sgraphs "Sgraph_selected"
// and "Sgraph_selection", but this time based on LEDAs nodes,
// edges and lists of nodes (referred to as "group" in the Sgraph-context)

typedef Sgraph_selected LEDA_selected;
	
typedef struct
{
	node        lnode;
	edge        ledge;
	list<node>* lgroup;
	
} LEDA_selection;

////////////////////////////////////////////////////////////////////////////
// the main class.
// WA: 23.5.96: changed GA_graph-structures to GT_Graph-structures
//              tidy up

class LSD : public LEDA_Sgraph_association
{
	///////////////////////////////////////////////////////
	// friend declarations
	
	friend ostream& operator<< (ostream& out, LSD& lsd_object);
	
	friend void lsd_make_node_proc(Snode node);
	friend void lsd_make_edge_proc(Sedge edge);
	friend void lsd_remove_graph_proc(Sgraph graph);
	friend void lsd_remove_edge_proc(Sedge edge);
	friend void lsd_remove_node_proc(Snode node);

	///////////////////////////////////////////////////////
	// methods
	
  public:
	
	///////////////////////////////////////////////////////
	// constructors & destructors
	LSD();
	virtual ~LSD();
	
	///////////////////////////////////////////////////////
	// service-methods

	// call a Sgraph-algorithm on the GT-graph "the_gt_graph"
	void callSgraph(void (*proc) (Sgraph_proc_info info));

	// New functions used instead of call_sgraph
	void init_call();
	void clean_up();
	
	// WA CH
	// call a GT-algorithm on the GT-graph "the_gt_graph"
	// CAUTION! No work on a Sgraph-counterpart can be done -
	// This method can only be used for algorithms based
	// on GT or LEDA operations.
	void callGT(void (*proc) (Sgraph_proc_info proc_info));
	
	// set "the_proc_info" to the default-settings
	void reset_proc_info(void)
	    { the_proc_info = default_sgraph_proc_info(); }

	// set the selection within "the_proc_info"
	void set_selection(LEDA_selected selected,
		LEDA_selection selection);
	
	// get the new selection from "the_proc_info"
	// Caution! Test selected to reassure that selection
	// has a valid content.
	void get_new_selection(LEDA_selected& selected,
		LEDA_selection& selection);
	
	///////////////////////////////////////////////////////
	// getting & setting members

	void      gt_graph(GT_Graph *g) { the_gt_graph = g; }
	GT_Graph* gt_graph()            { return (the_gt_graph); }
	
	LSD*      lsd(void)             { return this; }

	struct sgraph_proc_info* proc_info(void) {return &the_proc_info; }
	
	Sgraph sgraph(void) { return the_sgraph; }

    // return the static members to some graphed-dummyies
	// (Annotation: see member declaration below)

	static int gridwidth(void) {return the_gridwidth; }
    static int current_node_width(void) {return the_current_node_width; }
    static int current_node_height(void) {return the_current_node_height; }

	///////////////////////////////////////////////////////
	// private methods
	
private:

	// clear the LSD-object: delete the Sgraph and the
	// association between LEDA and Sgraph
	void clear(void);

	// test some preconditions
	bool prepare_call(void (*proc) (Sgraph_proc_info proc_info));
	bool prepare_call();

	// show some algorithmresults for debug purpose
	void evaluate_call(void);

	// create default proc_info-structure
	struct sgraph_proc_info	default_sgraph_proc_info (void);

	// create a dummy Sgraph
	void make_dummy_sgraph(void);
	
	// copy the GT_graph to a sgraph 
	void copy_gtgraph_to_sgraph(void);
	// copy the Sgraph-structure to a GT-graph
	void copy_sgraph_to_gtgraph(void);
	
	// copy the edgeline from/to GT
	void GT_2_edgeline(edge ledge, Sedge sedge); //node& source_node);
	void edgeline_2_GT(Sedge sedge, edge ledge);

	// copy "the_selection" to the selection in Sgraphs "the_proc_info"
	void set_sgraph_selection(void);
	
	// get "the_new_selection" from the new selection in
	// Sgraphs "the_proc_info"
	void get_sgraph_new_selection(void);

    // free the seletion-groups
	void free_selections(void);

    // set the static members
	void set_statics(void);

    // get a new number for a GT-nodenumbering
	int get_new_node_number(void);

	// procedures called by Sgraph via friend-functions (s.a.):
	void make_node_proc(Snode node);
	void make_edge_proc(Sedge edge);
	void remove_graph_proc(Sgraph graph);
	void remove_edge_proc(Sedge edge);
	void remove_node_proc(Snode node);

	// convert a simple LEDA-graph to a GT-graph
	GT_Graph* make_GT_graph(graph *leda_graph);
	
	// reduce a GT-graph to a LEDA-Graph
	graph* make_LEDA_graph(GT_Graph *gt_graph);

	///////////////////////////////////////////////////////
	// declare a assign- & a copy-operator as private methods
	// but dont define them - so it is impossible to missuse
	// default-operators created by the compiler.
	
	const LSD& operator=(const LSD& lsd_object);
	LSD(const LSD& lsd_object);
	
	///////////////////////////////////////////////////////
	// members
	
private:
	
	// the graph-structures
	Sgraph   the_sgraph;
	GT_Graph *the_gt_graph;

	// the Sgraph_proc_info-structure
	struct sgraph_proc_info	the_proc_info;
	// the selection-informations for the input
	LEDA_selected  the_selected;
	LEDA_selection the_selection;
	// the new selection-informations from the algorithm
	LEDA_selected  the_new_selected;
	LEDA_selection the_new_selection;
	
	// who possesses the GT-graph? if its me, i have
	// to delete it on my own, otherwise i must NOT
	// delete it.
	bool the_gt_graph_is_mine;
	// automaticly delete & create the LEDA-counterparts of
	// Sgraph-nodes and -edges?
	bool update_gt_counterpart;

    // Some graphed-dummyfunctions exist (get_gridwidth(),
	// get_current_node_width() and get_current_node_height()),
    // which have no parameters from which the actual affected
	// LSD-instance (i.e. the affected graphs) can be retrieved.
	// To make a serious implemetation possible, we decided to
	// model the values retrieved from this functions by static
	// members.
	// CAUTION: if more than one instance of LSD exists, the
	// static variables have the values from that instance, which
	// called set_statics() the last time.

	static int the_gridwidth;
    static int the_current_node_width;
    static int the_current_node_height;

};

#endif // LSD_H






