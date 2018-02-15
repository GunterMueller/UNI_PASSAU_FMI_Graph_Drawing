/* This software is distributed under the Lesser General Public License */
//
// dag.cpp
//
// This file implements the class Directed_Acyclic_Graph.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_xdag/dag.cpp,v $
// $Author: raitner $
// $Revision: 1.3 $
// $Date: 1999/01/03 16:05:44 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1997, Graphlet Project
//
//     Author: Harald Mader (mader@fmi.uni-passau.de)
//


#include "dag.h"
#include "level.h"
#include "scanline.h"
#include "ordergraph.h"
#include "region.h"

#include <math.h>

#include <gt_base/NEI.h>
#include <GTL/dfs.h>

//#include <LEDA/graph_alg.h>


// ============================================================================
//
// class Directed_Acyclic_Graph
//
// ============================================================================

//
// constructor
//
// store both GT graph and LEDA graph in the data structure and initialize 
// the maps for nodes an edges
//

Directed_Acyclic_Graph::Directed_Acyclic_Graph (GT_Graph* g) :
    threshold(1.0e-6)
{
    gt_graph(g);
    attached(g->attached());
    scanline(new Scanline(attached(), &the_dag_node));
    tcl_interpreter(0);
    min_x(0.0);
    min_y(0.0);
    directed(true);
    acyclic(true);

    dag_node().init(*attached());
    length().init(*attached());
    dummies().clear();
    reversed().clear();

    min_node_node_distance(0);
    min_node_edge_distance(0);
    min_edge_edge_distance(0);
    default_edge_length(0);
    animation(1);
    iterations_crossing_reduction(0);
    last_phase_crossing_reduction(0);
    iterations_node_positioning(0);
    last_phase_node_positioning(0);
    protocol(1);
    stepping(0);

    light_red   = graphlet->keymapper.add("#FFC0C0");
    light_green = graphlet->keymapper.add("#C0FFC0");
    light_blue  = graphlet->keymapper.add("#C0C0FF");
    dark_red    = graphlet->keymapper.add("#C00000");
    dark_green  = graphlet->keymapper.add("#00C000");
    dark_blue   = graphlet->keymapper.add("#0000C0");
}


//
// destructor
//
// get rid of all those things we're no longer in need of
//

Directed_Acyclic_Graph::~Directed_Acyclic_Graph ()
{
    delete scanline();
}


//
// initialize
//
// This function sets the parameters for the DAG algorithm and reads the
// informations contained in the graph into the local data structure.
//

void Directed_Acyclic_Graph::initialize (
    Tcl_Interp* interp,
    int nn_dist,
    int ne_dist,
    int ee_dist,
    int def_edge_len,
    int anim,
    int iter_crosred,
    int last_crosred,
    int iter_nodepos,
    int last_nodepos,
    int prot,
    int step)
{
    tcl_interpreter(interp);
    min_node_node_distance(nn_dist);
    min_node_edge_distance(ne_dist);
    min_edge_edge_distance(ee_dist);
    default_edge_length(def_edge_len);
    animation(anim);
    iterations_crossing_reduction(iter_crosred);
    last_phase_crossing_reduction(last_crosred);
    iterations_node_positioning(iter_nodepos);
    last_phase_node_positioning(last_nodepos);
    protocol(prot);
    stepping(step);

    read_node_properties();
    read_edge_lengths();
}


//
// generate_layout
//
// (try to) make a nice layout for the DAG
//

string Directed_Acyclic_Graph::generate_layout ()
{
    check();			// preparing stuff

    remove_bends();		// STEP 1: prepare the graph
    compute_levels();		// STEP 2: sort topologically
    eliminate_longspan_edges();	// STEP 3: create a proper hierarchy
    reduce_crossings();		// STEP 4: reduce number of edge crossings
    position_nodes();		// STEP 5: improve node positions
    remove_dummies();		// STEP 6: get rid of the dummies

    cleanup();			// finishing stuff

    return "ok";
}


//
// check
//
// This function checks if the graph is directed and acyclic. If one of
// these constraints is not fulfilled, we modify the graph to fit our
// needs.
//

void Directed_Acyclic_Graph::check ()
{
    edge e;
    node v, w;

    //
    // The graph is undirected. Let's make it directed!
    //

    if (attached()->is_undirected()) {
 	attached()->make_directed();
	directed(false);
	forall_edges (e, *attached()) {
	    gt_graph()->gt(e).graphics()->arrow(GT_Keys::arrow_none);
	}
    }

    //
    // The graph contains cycles. Let's get rid of them.
    //

    node_map<int> dfsnum(*attached()), compnum(*attached());

    if (! attached()->is_acyclic()) {
	acyclic(false);

	dfs d;
	d.scan_whole_graph (true);
	d.calc_comp_num(true);
	assert (d.check(*attached()));
	d.run(*attached());

	forall_edges (e, *attached()) {
	    v = e.source();
	    w = e.target();

	    if (d[w] < d[v] && d.comp_num(w) > d.comp_num(v)) {
		e.reverse ();
		reversed().push_back(e);
	    }
	}
	if (protocol()) {
	    cout << reversed().size() << " edges reversed" << endl;
	}
    }
}


//
// remove_bends
//
// In order to remove bends which could result from a previous layout from
// all edges we examine every edge of the graph and repeatedly delete the
// second GT_Point (=line[1]) from each GT_Polyline until there are only two
// GT_Points left on the GT_Polyline. These remaining GT_Points are exactly
// the edgeanchors of the source and the target node.
// Furthermore, we set edge anchors for all edges to the middle of the top
// resp. bottom.
//

void Directed_Acyclic_Graph::remove_bends ()
{
    if (protocol())  cout << "removing bends from edges ..." << endl;

    edge e;
    forall_edges (e, *attached()) {

	// remove bends
	GT_Polyline line = gt_graph()->gt(e).graphics()->line();
	while (line.size() > 2) {
	    line.erase (++line.begin());
// 	    line.del(line[1]);
	}
	gt_graph()->gt(e).graphics()->line(line);

	// set edge anchor (It is enough to do this for edges, since
	// default functions for edges have priority over the default
	// functions for adjacent nodes.)
	GT_Edge_NEI* edge_nei = gt_graph()->gt(e).edge_nei();
	edge_nei->set_EA(GT_Source, 0.0, 1.0);
	edge_nei->set_EA(GT_Target, 0.0, -1.0);
	edge_nei->set_EA_default_function(GT_Keys::empty_function, GT_Source);
	edge_nei->set_EA_default_function(GT_Keys::empty_function, GT_Target);

    }
}


//
// compute_levels
//
// This function computes the levels of the DAG. These levels are those
// coordinates on the y-axis where at least one node begins or ends, i.e. 
// the y-coordinates of the tops **AND** bottoms of nodes.
//

void Directed_Acyclic_Graph::compute_levels ()
{
    if (protocol())  cout << "computing levels ..." << endl;

    node_map<int> INDEG(*attached(),0);
    list<node>      ZEROINDEG;
    node v, w;

    // find all nodes that have no predecessors
    forall_nodes (v, *attached()) {
	if ( (INDEG[v]=v.indeg()) == 0 )
	    ZEROINDEG.push_back(v);
    }

    // visit all nodes that have no **unvisited** predecessors

    node::adj_nodes_iterator it, end;

    while (! ZEROINDEG.empty()) {
	v = ZEROINDEG.front();
	ZEROINDEG.pop_front();
	top(v, max_level_of_predecessors(v));
	bottom(v, top(v) + height(v));
	ycoord(v, top(v) + height(v)/2.0);
	scanline()->new_level(top(v)); 
	scanline()->new_level(bottom(v));

	// update the y-coordinate in the Graphlet data structure
	// now, because it's value will no longer change
	gt_graph()->gt(v).graphics()->y(ycoord(v));
	if (animation_is(A_node)) {
	    edge e;
	    forall_inout_edges (e,v) {
		gt_graph()->draw(e);
	    }
	    gt_graph()->gt(v).graphics()->fill(light_red);
	    gt_graph()->draw(v);
	    Tcl_Eval (tcl_interpreter(), "update");
	    gt_graph()->gt(v).graphics()->fill(GT_Keys::white);
	    gt_graph()->draw(v);
	}

	it = v.adj_nodes_begin();
	end = v.adj_nodes_end();
	
	while (it != end) {
	    w = *it;
	    if ((--INDEG[w]) == 0)
		ZEROINDEG.push_back(w);
	    ++it;
	}

// 	forall_adj_nodes (w,v) {
// 	    if (--INDEG[w] == 0)
// 		ZEROINDEG.push_back(w);
// 	}
    }
    
    // in order to paint the last node in white
    if (animation_is(A_node)) {
	Tcl_Eval (tcl_interpreter(), "update");
    }

    // and finally ... sort the levels by ycoord
    scanline()->sort_levels();

    if (animation_is(A_level_phase)) {
	draw();
	have_a_break();
    }
}


//
// eliminate_longspan_edges
//
// A short-span edge is an edge that connects two nodes situated on successive
// levels, whereas a long-span edge crosses at least one level. This function
// looks for long-span edges e and converts them so that they can be handled
// by our algorithm. A long-span edge e=(u,w) is replaced with a node v and
// two edges in_edge=(u,v) and out_edge=(v,w) restoring the previous connec-
// tion between u and w. v starts at the successor level of u's bottom level
// and ends at the predecessor level of w's top level. The width of v is the
// width of the long-span edge e being replaced.
//

void Directed_Acyclic_Graph::eliminate_longspan_edges ()
{
    if (protocol())  cout << "eliminating longspan edges ..." << endl;

    edge e, in_edge, out_edge;
    node u, v, w;
    int source_level, target_level;
    double top_v, bottom_v, width_v;

    // ATTENTION: We cannot run through the graph by using a forall_edges loop,
    // because in case of having to delete a long-span edge in the graph, LEDA 
    // is no longer able to pick up the successor edge in the loop. The reason 
    // for that is the way LEDA defines the forall_edges macro. Therefore we
    // use a while loop and get the successor of an edge before deleting the
    // edge itself.

    list<edge>::const_iterator it = attached()->edges_begin();
    list<edge>::const_iterator end = attached()->edges_end(); 
    list<edge>::iterator tmp;

    while (it != end) {
	e = *it;
	u = e.source();
	w = e.target();
	source_level = scanline()->find_level(bottom(u))->nr();
	target_level = scanline()->find_level(top(w))->nr();

	if (target_level - source_level > 1) {
	    top_v = scanline()->find_level(source_level+1)->ycoord();
	    bottom_v = scanline()->find_level(target_level-1)->ycoord();
	    width_v = gt_graph()->gt(e).graphics()->width();
 	    v = new_node(top_v, bottom_v, width_v);
 	    in_edge = new_edge(u, v, top_v-bottom(u), e);
 	    out_edge = new_edge(v, w, top(w)-bottom_v, e);
	    dummies().push_back(v);

	    // Before deleting the old edge 'e', we have to check, if
	    // 'e' is a reversed edge. If it is, we replace 'e' with
	    // 'in_edge' in the 'reversed' list.
	    tmp = find (reversed().begin(), reversed().end(), e);
	    
	    if (tmp != reversed().end()) {
		*tmp = in_edge;
	    }

	    ++it;
	    attached()->del_edge (e);

	    //
	    // The new dummy node gets the same x-coordinate as its
	    // predecessor. Then we pass the data to the Graphlet data
	    // structure (for later animation!!!)
	    // In a previous version we set the dummy's x-coordinate
	    // exactly in the middle between predecessor and successor,
	    // i.e. "xcoord(v,(xcoord(u)+xcoord(w))/2.0)", but that re-
	    // sulted in a worse layout, because the initial positions
	    // of the nodes are influencing their ordering on the levels.
	    //

	    xcoord(v,xcoord(u));
	    gt_graph()->gt(v).graphics()->x(xcoord(v));
	    gt_graph()->gt(v).graphics()->y(ycoord(v));
	    gt_graph()->gt(v).graphics()->w(width(v));
	    gt_graph()->gt(v).graphics()->h(height(v));

	    if (protocol()) {
		cout << "    inserted dummy node from level "
		     << source_level+1 << " (" << top_v << ")"
		     << " to level "
		     << target_level-1 << " (" << bottom_v << ")"
		     << endl;
	    }

	    if (animation_is(A_node)) {
		gt_graph()->draw(v);
		gt_graph()->draw(in_edge);
		gt_graph()->draw(out_edge);
		Tcl_Eval (tcl_interpreter(), "update");
		have_a_break();
	    }
	
	} else {
	    ++it;
	}
    }

//     e = attached()->first_edge();

//     while (e) {
// 	u = attached()->source(e);
// 	w = attached()->target(e);
// 	source_level = scanline()->find_level(bottom(u))->nr();
// 	target_level = scanline()->find_level(top(w))->nr();
// 	if (target_level - source_level > 1) {
// 	    top_v = scanline()->find_level(source_level+1)->ycoord();
// 	    bottom_v = scanline()->find_level(target_level-1)->ycoord();
// 	    width_v = gt_graph()->gt(e).graphics()->width();
//  	    v = new_node(top_v, bottom_v, width_v);
//  	    in_edge = new_edge(u, v, top_v-bottom(u), e);
//  	    out_edge = new_edge(v, w, top(w)-bottom_v, e);
// 	    dummies().append(v);

// 	    // Before deleting the old edge 'e', we have to check, if
// 	    // 'e' is a reversed edge. If it is, we replace 'e' with
// 	    // 'in_edge' in the 'reversed' list.
// 	    list_item found = reversed().search(e);
// 	    if (found) {
// 		reversed().assign(found, in_edge);
// 	    }

// 	    old_edge = e;
// 	    e = attached()->succ_edge(e);
// 	    attached()->del_edge(old_edge);

// 	    //
// 	    // The new dummy node gets the same x-coordinate as its
// 	    // predecessor. Then we pass the data to the Graphlet data
// 	    // structure (for later animation!!!)
// 	    // In a previous version we set the dummy's x-coordinate
// 	    // exactly in the middle between predecessor and successor,
// 	    // i.e. "xcoord(v,(xcoord(u)+xcoord(w))/2.0)", but that re-
// 	    // sulted in a worse layout, because the initial positions
// 	    // of the nodes are influencing their ordering on the levels.
// 	    //

// 	    xcoord(v,xcoord(u));
// 	    gt_graph()->gt(v).graphics()->x(xcoord(v));
// 	    gt_graph()->gt(v).graphics()->y(ycoord(v));
// 	    gt_graph()->gt(v).graphics()->w(width(v));
// 	    gt_graph()->gt(v).graphics()->h(height(v));

// 	    if (protocol()) {
// 		cout << "    inserted dummy node from level "
// 		     << source_level+1 << " (" << top_v << ")"
// 		     << " to level "
// 		     << target_level-1 << " (" << bottom_v << ")"
// 		     << endl;
// 	    }

// 	    if (animation_is(A_node)) {
// 		gt_graph()->draw(v);
// 		gt_graph()->draw(in_edge);
// 		gt_graph()->draw(out_edge);
// 		Tcl_Eval (tcl_interpreter(), "update");
// 		have_a_break();
// 	    }
// 	}
// 	else {
// 	    e = attached()->succ_edge(e);
// 	}
//     }
    animate(A_level_phase);
}


//
// reduce_crossings
//
// To reduce the number of edge crossings we execute successive up and down
// phases until the given number of iterations is reached. In these up and
// down phases we compute new horizontal positions for the nodes by calculat-
// ing their barycenters. We do *NOT* take care, if the number of crossings
// are reduced from one step to the next.
//
// This part of the algorithm offers big possibilities for improvement. There
// is to find a way to count the number of crossings and to only allow the
// switching of positions of two or more nodes, if the number of crossings
// decreases. This has not been done until now, because I found no proper
// way to count crossings.
// (HINT: You have to consider that crossings between edges and dummy nodes
// result in edge crossings in the final drawing.)
//

void Directed_Acyclic_Graph::reduce_crossings ()
{
    if (protocol())  cout << "reducing edge crossings ..." << endl;

    //
    // Before starting with the iterations for the crossing reduction
    // we have to construct all scanlines of the graph.
    //

    scanline()->build(protocol());

    //
    // And now comes the iterative part.
    //

    for (int i=1; i<=iterations_crossing_reduction(); i++) {

	//
	// first the down phase ...
	//

	if (protocol())  cout << "  down phase " << i << " ..." << endl;
	have_a_break();
 	crossing_reduction_down_phase();
	animate(A_phase);

	//
	// ... then the up phase
	//

	if (protocol())  cout << "  up phase " << i << " ..." << endl;
	have_a_break();
 	crossing_reduction_up_phase();
	animate(A_phase);

    }

    //
    // an additional down phase ?
    //

    if (last_phase_crossing_reduction() == 0) {

	if (protocol())  cout << "  down phase + ..." << endl;
	have_a_break();
 	crossing_reduction_down_phase();
	animate(A_phase);

    }
}


//
// position_nodes
//
// This function tries to improve the current node positions. This has to be
// done, because the last step we made was ordering and placing the nodes
// according to the order graph. That means that all nodes are now placed as 
// far to the left as possible. Therefore our layout is not balanced at all.
// The idea behind this node improvement is a pendulum. We consider the nodes
// as balls and the edges as strings. We begin by fixing the nodes of the
// first level "on the ceiling". The balls on the second level swing to a
// balanced position caused by gravity and depending on their connections to
// their predecessors. After finishing the second level we fix the nodes of
// the second level and repeat that procedure until we reach the bottom. Then
// we turn the system upside-down, i.e. we fix the nodes of the last level
// "on the ceiling" and repeat the steps described above until we reach the
// top again.
//

void Directed_Acyclic_Graph::position_nodes ()
{
    if (protocol())  cout << "positioning nodes ..." << endl;

    //
    // Before starting the iterations for the node positioning
    // we have to construct an order graph for our graph.
    //

//     Level* level;
    Order_Graph ograph(
	min_node_node_distance(),
	min_node_edge_distance(),
	min_edge_edge_distance());
    
    Scanline::iterator it;
    Scanline::iterator end = scanline()->end();

    for (it = scanline()->begin(); it != end; ++it) {
	ograph.include(*(*it));
    }	
    
//     forall (level, *scanline()) {
// 	ograph.include(*level);
//     }

    //
    // And now comes the iterative part.
    //

    for (int i=1; i<=iterations_node_positioning(); i++) {

	//
	// first the down phase ...
	//

	if (protocol())  cout << "  down phase " << i << " ..." << endl;
	have_a_break();
	node_positioning_down_phase(ograph);
	animate(A_phase);

	//
	// ... then the up phase
	//

	if (protocol())  cout << "  up phase " << i << " ..." << endl;
	have_a_break();
	node_positioning_up_phase(ograph);
	animate(A_phase);

    }

    //
    // an additional down phase ?
    //

    if (last_phase_node_positioning() == 0) {

	if (protocol())  cout << "  down phase + ..." << endl;
	have_a_break();
	node_positioning_down_phase(ograph);
	animate(A_phase);

    }
}


//
// remove_dummies
//
// This is the final step in our algorithm. We have to remove all dummy nodes
// in the graph and replace them and their in and out edges with a suitable
// edge. This is done by connecting the predecessor and the successor with a
// new edge and by adding two bends to this new edge at those y-coordinates
// where the dummy node has its top resp. bottom level. In case, that a dummy
// node has height 0 (i.e. top is equal bottom), we only insert *one* bend. 
//

void Directed_Acyclic_Graph::remove_dummies ()
{
    if (protocol())  cout << "removing dummy nodes ..." << endl;

    node n;
    edge e, in_edge, out_edge;
    GT_Polyline line;
    GT_Point *bend1, *bend2;
    list<edge>::iterator it, end = reversed().end();
    

    while (! dummies().empty()) {
	n = dummies().front();
	dummies().pop_front();
	in_edge = *(n.in_edges_begin());
	out_edge = *(n.out_edges_begin());

	assert (n.outdeg() == 1 && n.indeg() == 1);
	
// 	in_edge = attached()->first_in_edge(n);
// 	out_edge = attached()->first_adj_edge(n);

	// We have to guarantee that there is only one predecessor and only
// 	// one successor for each dummy node n.
// 	assert ( (in_edge==attached()->last_in_edge(n)) &&
// 	         (out_edge==attached()->last_adj_edge(n)) );

	e = new_edge(
	    in_edge.source(),
	    out_edge.target(),
	    length(in_edge) + height(n) + length(out_edge),
	    in_edge);

	bend1 = bend2 = 0;
	it = find (reversed().begin(),reversed().end(), in_edge);
	
	if (it != end) {
	    // If 'in_edge' is a reversed edge, we replace it with 'e'
	    // in the 'reversed' list. The bends have to de inserted in
	    // the *REVERSED* order, because the edge will be reversed
	    // once more in order to point into the right direction.
	    
	    *it = e;
	    if (top(n) != bottom(n)) {
		bend1 = new GT_Point(xcoord(n), bottom(n));
	    }
	    bend2 = new GT_Point(xcoord(n), top(n));
	}
	else {
	    bend1 = new GT_Point(xcoord(n), top(n));
	    if (top(n) != bottom(n)) {
		 bend2 = new GT_Point(xcoord(n), bottom(n));
	    }
	}
	
	
// 	list_item found = reversed().search(in_edge);
// 	if (found) {
// 	    // If 'in_edge' is a reversed edge, we replace it with 'e'
// 	    // in the 'reversed' list. The bends have to de inserted in
// 	    // the *REVERSED* order, because the edge will be reversed
// 	    // once more in order to point into the right direction.
// 	    reversed().assign(found, e);
// 	    if (top(n) != bottom(n)) {
// 		bend1 = new GT_Point(xcoord(n), bottom(n));
// 	    }
// 	    bend2 = new GT_Point(xcoord(n), top(n));
// 	}
// 	else {
// 	    bend1 = new GT_Point(xcoord(n), top(n));
// 	    if (top(n) != bottom(n)) {
// 		 bend2 = new GT_Point(xcoord(n), bottom(n));
// 	    }
// 	}


	line = gt_graph()->gt(e).graphics()->line();
	if (bend1) line.insert (++line.begin(), *bend1);
	if (bend2) line.insert (--line.end(),*bend2); 

// 	if (bend1) line.insert(*bend1, line.first(), after);
// 	if (bend2) line.insert(*bend2, line.last(), before);
	gt_graph()->gt(e).graphics()->line(line);

	attached()->del_edge(in_edge);
	attached()->del_edge(out_edge);
	attached()->del_node(n);
    }
}


//
// cleanup
//
// This function cleans up with all the stuff concerning undirected or
// cyclic graphs. We have to do this, because we modified such graphs
// in the beginning.
//

void Directed_Acyclic_Graph::cleanup ()
{
    edge e;
    node n;
    double x, y, smallest_x, smallest_y, delta_x, delta_y;
    GT_Polyline line;
    GT_Point bend;
//     list_item it;

    //
    // The graph contained cycles. Let's bring them back!
    //

    if (!acyclic()) {
	while (!reversed().empty()) {
	    e = reversed().front();
	    reversed().pop_front();
	    e.reverse();
// 	    attached()->rev_edge(e);
	    gt_graph()->gt(e).edge_nei()->set_EA(GT_Source, 0.0, -1.0);
	    gt_graph()->gt(e).edge_nei()->set_EA(GT_Target, 0.0, 1.0);
	}
    }

    //
    // The graph was undirected. Let's make it undirected, again!
    //

    if (!directed()) {
	attached()->make_undirected();
    }

    //
    // compute the smallest x- and y-coordinates ...
    //

    n = *attached()->nodes_begin();
    smallest_x = xcoord(n);
    smallest_y = ycoord(n);
    forall_nodes (n, *attached()) {
	if ( (x = xcoord(n)-width(n)/2.0) < smallest_x) {
	    smallest_x = x;
	}
	if ( (y = ycoord(n)-height(n)/2.0) < smallest_y) {
	    smallest_y = y;
	}
    }

    //
    // ... and shift the graph so that everything is visible
    //
    delta_x = delta_y = 0.0;
    if (smallest_x < 0) {
	delta_x = ceil(-smallest_x/100.0)*100.0;
    }
    if (smallest_y < 0) {
	delta_y = ceil(-smallest_y/100.0)*100.0;
    }
    
    list<GT_Point>::iterator it, end;
    
    
    if (delta_x != 0.0 || delta_y != 0.0) {
	forall_nodes (n, *attached()) {
	    xcoord(n, xcoord(n) + delta_x);
	    ycoord(n, ycoord(n) + delta_y);
	}
	forall_edges (e, *attached()) {
	    line = gt_graph()->gt(e).graphics()->line();
	    end = line.end();

	    for (it = line.begin(); it != end; ++it) {
		bend = *it;

// 	    forall_items (it, line) {
// 		bend = line.inf(it);
		bend.x(bend.x() + delta_x);
		bend.y(bend.y() + delta_y);
		*it = bend;

// 		line.assign(it, bend);
	    }
	    gt_graph()->gt(e).graphics()->line(line);
	}
    }

    update_all_nodes();
    draw();
}


///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////


//
// read_node_properties
//
// This function gets the node properties from the Graphlet data structure
// gt_graph and stores them in our local data structure.
//

void Directed_Acyclic_Graph::read_node_properties ()
{
    if (protocol())  cout << "reading node properties ..." << endl;

    // initialize min_x and min_y with the values of the first node
    node n = *attached()->nodes_begin();
    min_x(gt_graph()->gt(n).graphics()->x());
    min_y(gt_graph()->gt(n).graphics()->y());

    // traverse all nodes of the graph
    forall_nodes (n, *attached()) {
	dag_node(n).owner(n);
 	xcoord (n,gt_graph()->gt(n).graphics()->x());
 	ycoord (n,gt_graph()->gt(n).graphics()->y());
 	width  (n,gt_graph()->gt(n).graphics()->w());
 	height (n,gt_graph()->gt(n).graphics()->h());

	if (xcoord(n) < min_x()) {
	    min_x(xcoord(n));
	}
	if (ycoord(n) < min_y()) {
	    min_y(ycoord(n));
	}
    }
}


//
// read_edge_lengths
//
// This function gets the lengths of the edges from their labels. We read an 
// edge's label and try to interpret it as a variable of type double. If we
// find out that an edge's label which is not "0" is interpreted as value 0,
// we take the given default edge length for this edge.
//
// If you prefer a different way to enter edge lengths than to have the 
// edges' labels interpreted (e.g. by adding a way to enter constraints
// with the "Inspector"), you just have to modify this function to fit your 
// needs, and everything else works without any further modification.
//

void Directed_Acyclic_Graph::read_edge_lengths ()
{
    if (protocol())  cout << "reading edge lengths ..." << endl;

    edge e;
    forall_edges (e, *attached()) {
	double len = atof(gt_graph()->gt(e).label().c_str());
	if (len == 0.0) {
	    len = default_edge_length();
	}
	length (e, len);
	if (protocol()) {
	    cout << "    edge length for ("
		 << gt_graph()->gt(e.source()).label() << ","
		 << gt_graph()->gt(e.target()).label() << ")"
		 << " set to " << len << endl;
	}
    }
}


//
// max_level_of_predecessors
//
// This function computes the earliest possible y-coordinate for the top of a 
// given node v by taking a look at all of v's predecessors and adding the
// length of the connecting edge to the bottom coordinate of the predecessor. 
//

double Directed_Acyclic_Graph::max_level_of_predecessors (node v)
{
    double new_lev, lev = min_y();
    edge in_edge;

    forall_in_edges (in_edge, v) {
	new_lev = bottom(in_edge.source())+length(in_edge);
	if (lev < new_lev) {
	    lev = new_lev;
	}
    }

    return lev;
}


//
// new_node
//
// This function inserts a dummy node that is necessary to replace a long-span
// edge and sets the dummy's node properties to the given values. We insert
// the node here and update the values in our data structure. The x-coordinate
// is set to 0.0, because it is unimportant for the moment.
//

node Directed_Acyclic_Graph::new_node (double tp, double bot, double wid)
{
    node n = attached()->new_node();

    dag_node(n).owner(n);
    top    (n, tp);
    bottom (n, bot);
    width  (n, wid);
    height (n, bot - tp);
    xcoord (n, 0);
    ycoord (n, (tp+bot)/2.0);
    is_dummy (n, true);

    //
    // For better identification during debugging you can give successive
    // labels to the new dummy nodes, if you uncomment the following lines.
    //

//    string s("(%c)", dummies().length()+1-1+'a');
//    gt_graph()->gt(n).label(s);

    return n;
}


//
// new_edge
//
// This function inserts a new edge that connects a dummy node with the inci-
// dent nodes of its previous longspan edges. We install a clone of the old
// edge's attributes for the new edge, so that we can fall back on these
// values when drawing the layed out graph with the final edges. Moreover we
// set edgeanchors for source and target node and set the edge length in our
// internal DAG data structure.
//

edge Directed_Acyclic_Graph::new_edge (node source, node target,
    double len, edge old_edge)
{
    edge e = attached()->new_edge(source, target);
    length (e, len);

    GT_List_of_Attributes* old_edge_attrs =
	gt_graph()->gt(old_edge).clone(GT_Copy::deep);
    gt_graph()->attrs(e, (GT_Edge_Attributes*) old_edge_attrs);

    gt_graph()->gt(e).edge_nei()->set_EA(GT_Source, 0.0, 1.0);
    gt_graph()->gt(e).edge_nei()->set_EA(GT_Target, 0.0, -1.0);
    
    return e;
}


//
// crossing_reduction_down_phase
//
// This function traverses the graph - scanline by scanline, from the first to
// the last - and computes new x-coordinates for the nodes. On each scanline 
// we only compute new x-coordinates for active nodes. An 'active' node in the
// down phase is a node whose top level is equal to the level of the scanline.
// We don't consider the non-active nodes on a scanline, because their coordi-
// nates have already been computed on former scanlines. After that we have to
// adjust the nodes, because the computation of new x-coordinates may result 
// in intersections between nodes.
//

void Directed_Acyclic_Graph::crossing_reduction_down_phase ()
{
    node n;
    DAG_Node* dn;
    Level* level;
    Order_Graph ograph(
	min_node_node_distance(),
	min_node_edge_distance(),
	min_edge_edge_distance());
    
    Scanline::iterator it;
    Scanline::iterator end = scanline()->end();

    list<DAG_Node*>::iterator lev_it, lev_end;

    for (it = scanline()->begin(); it != end; ++it) {
	level = *it;
	mark_active_nodes(*level, Top);
	level->print_data(protocol());
	level->print_nodes(protocol(), "    next_line : ", gt_graph());
	lev_end = level->end(); 

	for (lev_it = level->begin(); lev_it != lev_end; ++lev_it) { 
	    dn = *lev_it;
	    if (dn->top() == level->ycoord()) {
		n = dn->owner();

		// 	forall_active_nodes (dn, *level, top) {
		n = dn->owner();
		//	    dn->old_xcoord(xcoord(n));
		xcoord(n,upper_barycenter(n));
		if (animation_is(A_node)) {
		    update_node(n);
		    gt_graph()->draw(n);
		    edge e;
		    forall_inout_edges (e,n) {
			gt_graph()->draw(e);
		    }
		    Tcl_Eval (tcl_interpreter(), "update");
		    have_a_break();
		}
	    }
	}

	adjust_nodes(*level, ograph);
	if (animation_is(A_node_level)) {
	    update_all_nodes();
	    draw();
	    have_a_break();
	}
	unmark_active_nodes(*level);
    }
}


//
// crossing_reduction_up_phase
//
// This function traverses the graph - scanline by scanline, from the last to
// the first - and computes new x-coordinates for the nodes. On each scanline
// we only compute new x-coordinates for active nodes. An 'active' node in the
// up phase is a node whose bottom level is equal to the level of the scanline.
// We don't consider the non-active nodes on a scanline, because their coordi-
// nates have already been computed on former scanlines. After that we have to
// adjust the nodes, because the computation of new x-coordinates may result 
// in intersections between nodes.
//

void Directed_Acyclic_Graph::crossing_reduction_up_phase ()
{
    node n;
    DAG_Node* dn;
    Level* level;
    Order_Graph ograph(
	min_node_node_distance(),
	min_node_edge_distance(),
	min_edge_edge_distance());

    Scanline::reverse_iterator it;
    Scanline::reverse_iterator end = scanline()->rend();

    list<DAG_Node*>::iterator lev_it, lev_end;

    for (it = scanline()->rbegin(); it != end; ++it) {
	level = *it;

//     forall_rev (level, *scanline()) {
	mark_active_nodes(*level, Bottom);
	level->print_data(protocol());
	level->print_nodes(protocol(), "    prev_line : ", gt_graph());
	lev_end = level->end(); 

	for (lev_it = level->begin(); lev_it != lev_end; ++lev_it) { 
	    dn = *lev_it;
	    if (dn->bottom() == level->ycoord()) {

// 	forall_active_nodes (dn, *level, bottom) {
		n = dn->owner();
//	    dn->old_xcoord(xcoord(n));
		xcoord(n,lower_barycenter(n));
		if (animation_is(A_node)) {
		    update_node(n);
		    gt_graph()->draw(n);
		    edge e;
		    forall_inout_edges (e,n) {
			gt_graph()->draw(e);
		    }
		    Tcl_Eval (tcl_interpreter(), "update");
		    have_a_break();
		}
	    }
	}
	adjust_nodes(*level, ograph);
	if (animation_is(A_node_level)) {
	    update_all_nodes();
	    draw();
	    have_a_break();
	}
	unmark_active_nodes(*level);
    }
}


//
// upper_barycenter
//
// This function computes the upper barycenter for a node v.
//

double Directed_Acyclic_Graph::upper_barycenter (node v)
{
    edge e;
    int count = 0;
    double bc = 0.0;
    
    forall_in_edges (e,v) {
// 	bc += xcoord(source(e));
	bc += xcoord (e.source());
	count++;
    }
    
    if (count)
	return bc/count;
    else
	return xcoord(v);
}


//
// lower_barycenter
//
// This function computes the lower barycenter for a node v.
//

double Directed_Acyclic_Graph::lower_barycenter (node v)
{
    int count = 0;
    double bc = 0.0;
    node::adj_nodes_iterator it = v.adj_nodes_begin();
    node::adj_nodes_iterator end = v.adj_nodes_end();
	
    while (it != end) {
	bc += xcoord(*it);
	count++;
	++it;
    }

//     forall_adj_nodes (w,v) {
// 	bc += xcoord(w);
// 	count++;
//     }
    
    if (count)
	return bc/count;
    else
	return xcoord(v);
}


//
// mark_active_nodes
//
// This function examines all nodes and tests if a node's top resp. bottom
// coordinate is equal to the level to which it belongs. If so, then the
// active flag is set to true, otherwise it is set to false. More important
// than setting the 'active' flag in this procedure is the coloring of the
// nodes, because they colors are needed to distinguish the active from the
// inactive nodes during animation.
//

void Directed_Acyclic_Graph::mark_active_nodes (const Level& level,
    Border_Flag border)
{
    DAG_Node* dn;
    GT_Key color;

    list<DAG_Node*>::const_iterator it;
    list<DAG_Node*>::const_iterator end = level.end();
    
    for (it = level.begin(); it != end; ++it) {
	dn = *it;

//     forall (dn, level) {

	if (border == Top) {
	    dn->active(dn->top()==level.ycoord());
	}
	else if (border == Bottom) {
	    dn->active(dn->bottom()==level.ycoord());
	}

	if (animation_is(A_node_level)) {
	    color = (dn->active() ? dark_red : light_red);
	    gt_graph()->gt(dn->owner()).graphics()->fill(color);
	    gt_graph()->draw(dn->owner());
	}

    }

    if (animation_is(A_node_level)) {
	Tcl_Eval (tcl_interpreter(), "update");
    }
}


//
// unmark_active_nodes
//
// This function sets the active flag back to false for all nodes on 'level'.
//

void Directed_Acyclic_Graph::unmark_active_nodes (const Level& level)
{
    DAG_Node* dn;
    list<DAG_Node*>::const_iterator it;
    list<DAG_Node*>::const_iterator end = level.end();
    
    for (it = level.begin(); it != end; ++it) {
	dn = *it;

//     forall (dn, level) {
	dn->active(false);
	if (animation_is(A_node_level)) {
	    gt_graph()->gt(dn->owner()).graphics()->fill(GT_Keys::white);
	    gt_graph()->draw(dn->owner());
	}
    }
}


//
// adjust_nodes
//
// This function computes new absolute x-coordinates for all nodes in the
// current order graph. First we sort the nodes on the current scanline 
// according to their x-coordinate. Then we insert (possibly new) relations
// between two successive nodes into the order graph. Finally, we execute
// a topsort algorithm on the order graph to get the new x-coordinates.
//

void Directed_Acyclic_Graph::adjust_nodes (
    Level& level, Order_Graph& ograph)
{
    level.print_nodes(protocol(), "    barycenter: ", gt_graph());

    // sort the nodes after computation of barycenters
    level.sort(compare_x());
//    sort(&level);
    
    level.print_nodes(protocol(), "    sorted    : ", gt_graph());

    // add this level to the existing order graph
    ograph.include(level);

    // reorder the nodes in the order graph
    ograph.reorder(min_x());

    level.print_nodes(protocol(), "    adjusted  : ", gt_graph());
    if (protocol())
	cout << "    -----------------------------------------------" << endl;
}


//
// sort
//
// This function sorts the nodes on level l by their x-coordinates. First we
// copy the list to a local array in order to have a better access to the
// stored data. After sorting the array we copy the array back to our original
// list. We have to do this in a special function, because LEDA sorts lists
// with the quicksort algorithms, and quicksort isn't stable as for the order
// of nodes that have the same value.
//

// void Directed_Acyclic_Graph::sort (Level* l)
// {
//     int len = l->length();
//     DAG_Node** A = new DAG_Node*[len];
//     DAG_Node** p = A;
//
//     list_item it;
//     forall_items (it, *l) {
// 	*p++ = (*l)[it];
//     }
//
// //     DAG_Node *tmp;
// //     for (int i=0; i<=len-2; i++) {
// // 	for (int j=i+1; j<=len-1; j++) {
// // 	    if (A[i]->xcoord()>A[j]->xcoord()) {
// // 		tmp = A[i]; A[i] = A[j]; A[j] = tmp;
// // 	    }
// // 	}
// //     }
//
//     int idx;
//     DAG_Node* tmp;
//
//     for (int i=0; i<=len-2; i++) {
// 	double min = A[idx=i]->xcoord();
// 	for (int j=i+1; j<=len-1; j++) {
// 	    if (A[j]->xcoord()<min) {
// 		min = A[idx=j]->xcoord();
// 	    }
// 	}
// 	if (idx > i) {
// 	    tmp = A[i]; A[i] = A[idx]; A[idx] = tmp;
// 	}
//     }
//
//     p = A;
//     forall_items (it, *l) {
// 	l->assign(it, *p++);
//     }
//
//     delete[] A;
// }


//
// node_positioning_down_phase
//
// This function implements the down phase of the pendulum method. Of course,
// we can not regard all nodes as isolated, because neighbored nodes, e.g,
// influence each other. Thus we must move them together. Therefore we have
// to create so called regions, i.e. sets of nodes that are influencing each
// other. We begin by creating a region for each node. Then we join depending
// regions until there is no change anylonger. Finally we move each region by
// an amount that depends of the members of the region and their connections
// to their predecessor.
//

void Directed_Acyclic_Graph::node_positioning_down_phase (
    const Order_Graph& ograph)
{
    Level* level;
    DAG_Node* dn;
    Region* reg;
    list<Region*> RL;

    Scanline::iterator it;
    Scanline::iterator end = scanline()->end();
    
    list<DAG_Node*>::iterator lev_it, lev_end;

    for (it = scanline()->begin(); it != end; ++it) {
	level = *it;

//     forall (level, *scanline()) {
	RL.clear();	
	lev_end = level->end(); 

	for (lev_it = level->begin(); lev_it != lev_end; ++lev_it) { 
	    dn = *lev_it; // create initial regions
	    if (dn->top() == level->ycoord()) {

// 	forall_active_nodes (dn, *level, top) {
		reg = new Region(dn);
		reg->force(upper_barycenter(dn->owner()) - dn->xcoord());
		RL.push_back(reg);
	    }
	}

	merge_dependent_regions(RL, *level);
	shift_regions(RL, ograph);
	if (animation_is(A_level)) {
	    update_all_nodes();
	    draw();
	    have_a_break();
	}
    }
}


//
// node_positioning_up_phase
//
// This function implements the up phase of pendulum method.
// (For a description: cf. "node_positioning_down_phase")
//

void Directed_Acyclic_Graph::node_positioning_up_phase (
    const Order_Graph& ograph)
{
    Level* level;
    DAG_Node* dn;
    Region* reg;
    list<Region*> RL;
    Scanline::reverse_iterator it;
    Scanline::reverse_iterator end = scanline()->rend();

    list<DAG_Node*>::iterator lev_it, lev_end;

    for (it = scanline()->rbegin(); it != end; ++it) {
	level = *it;

//     forall_rev (level, *scanline()) {
	RL.clear();	
	lev_end = level->end(); 

	for (lev_it = level->begin(); lev_it != lev_end; ++lev_it) { 
	    dn = *lev_it;
	    if (dn->bottom() == level->ycoord()) {

// 	forall_active_nodes (dn, *level, bottom) {
		reg = new Region(dn);
		reg->force(lower_barycenter(dn->owner()) - dn->xcoord());
		RL.push_back(reg);
	    }
	}
	merge_dependent_regions(RL, *level);
	shift_regions(RL, ograph);
	if (animation_is(A_level)) {
	    update_all_nodes();
	    draw();
	    have_a_break();
	}
    }
}


//
// merge_dependent_regions
//
// This function merges all those regions on the current level that depend
// on each other. We say that two region influence each other, if there is
// a collision concerning the positions to that both regions tend to move.
//

void Directed_Acyclic_Graph::merge_dependent_regions (
    list<Region*>& RL, const Level& level)
{
//     list_item item1, item2;
    list<Region*>::iterator it1, it2;
    Region *reg1, *reg2;
    double frc;
    int old_size;

    //
    // The following while-loop is going to be executed until the size
    // of RL no longer changes, i.e. until there are no more regions
    // influencing each other.
    //

    do {
	old_size = RL.size();
	if (protocol()) {
	    cout << level << ", contains " << old_size << " regions" << endl;
	}
	it1 = RL.begin();

// 	item1 = RL.first();
// 	while (item1) {

	while (it1 != RL.end()) {
	    it2 = it1;
	    ++it2;

// 	    item2 = RL.succ(item1);
	    
	    if (it2 != RL.end()) {
// 	    if (item2) {
// 		reg1 = RL[item1];
// 		reg2 = RL[item2];
		reg1 = *it1;
		reg2 = *it2;

		if (protocol()) {
// 		    cout << "    checking region " << RL.rank(reg1) << " "
// 			 << *reg1 << " and region " << RL.rank(reg2) << " "
// 			 << *reg2 << " ..." << endl;
		}

		if (animation_is(A_node)) {
		    reg1->colorize(gt_graph(), dark_green);
		    reg2->colorize(gt_graph(), dark_blue);
		    Tcl_Eval (tcl_interpreter(), "update");
		    have_a_break();
		}

		if (influencing(*reg1, *reg2, level)) {
		    frc = reg1->force() * reg1->size()
			+ reg2->force() * reg2->size();
		    reg1->splice (reg1->end(), *reg2);
// 		    reg1->conc(*reg2);
		    reg1->force(frc/reg1->size());
		    RL.erase (it2);
// 		    RL.del_item(item2);

// 		    if (protocol()) {
// 			cout << "... regions merged to new region "
// 			     << RL.rank(reg1) << " " << *reg1 << endl << endl;
// 		    }

		    if (animation_is(A_node)) {
			reg1->colorize(gt_graph(), dark_red);
			Tcl_Eval (tcl_interpreter(), "update");
			reg1->colorize(gt_graph(), GT_Keys::white);
			have_a_break();
		    }

		}
		else {
// 		    item1 = RL.succ(item1);
		    ++it1;

		    if (protocol()) {
			cout << "... regions are independent" << endl << endl;
		    }

		    if (animation_is(A_node)) {
			reg1->colorize(gt_graph(), GT_Keys::white);
			reg2->colorize(gt_graph(), GT_Keys::white);
			Tcl_Eval (tcl_interpreter(), "update");
			have_a_break();
		    }

		}
	    }
	    else {
// 		item1 = 0;		// in order to leave the inner loop
		break;
	    }
	}
    }  while ((signed) RL.size() != old_size);
    if (protocol()) {
	cout << "    -----------------------------------------------" << endl;
    }
}


//
// influencing
//
// This function checks, if two regions influence each other.
//
// A first attempt to implement this function was the following: We
// examine the rightmost node of reg1 and the leftmost node of reg2. If
// the distance between these two nodes is exactly the minimum distance
// (given by the user), then the nodes and accordingly the regions are
// touching; otherwise they do not. Of course, we have to take into
// consideration what type of nodes they are (dummy nodes or real nodes)
// in order to decide if their distance is minimum or not. This is done
// in function 'touching'. Regions that are found not touching are
// assumed to be independent.
// Unfortunately this idea did not work the way it should. Let us take a
// look at a tree and see what happens by running the algorithm with the
// idea described above: In an up phase every node -- as long as it is no
// leaf -- is put exactly at the barycenter of its sons, which produces
// a real nice balanced layout. The result is that the distance between
// nodes that are put in the middle of their sons is bigger than the
// minimum distance. However, this layout is destroyed in a successive
// down phase, because then the algorithm recognizes that sons of a
// common father have a distance bigger than "neccessary" and therefore
// regards these sons as not touching and thus not depending on each
// other.  As a result the nodes are approaching in the down phase and
// our nice layout is gone.
// Therefore our new idea is to check on neighborhood first. This is done
// in function 'neighboring' (two regions cannot influence each other if
// they are not neighboring!). Then, in function 'approaching', we
// examine whether the regions would become neighboring or would even get
// in conflict, if we gave way to the forces that act upon the regions.
// In that case we cannot handle these regions independently and there-
// fore have to merge them.
//

bool Directed_Acyclic_Graph::influencing (const Region& reg1,
    const Region& reg2, const Level& level) const
{
    //
    // First we examine if both regions are neighboring.
    //

    if (! neighboring(reg1, reg2, level)) {
	if (protocol()) cout << "    not neighboring ";
	return false;
    }
    else if (approaching(reg1, reg2)) {
	if (protocol()) cout << "    neighboring and approaching ";
	return true;
    }
    else {
	if (protocol()) cout << "    neighboring, but not approaching ";
	return false;
    }
}


//
// neighboring
//
// This functions checks if the two given regions reg1 and reg2 are
// neighbors or if there is a node between them. In the second case this
// node can only be an inactive node, i.e. a node whose top resp. bottom
// is on a different level, because otherwise that node would either be a
// member of reg1 or a member of reg2.
//
// Complexity: O(max. number of nodes per level),
//             because we have to search the list
//

bool Directed_Acyclic_Graph::neighboring (const Region& reg1,
    const Region& reg2, const Level& level) const
{
//     DAG_Node* rightmost = reg1.tail();
//     DAG_Node* leftmost = reg2.head();
    DAG_Node* rightmost = reg1.back();
    DAG_Node* leftmost = reg2.front();

    list<DAG_Node*>::const_iterator it = find (level.begin(), level.end(), rightmost);
//     list_item it = level.search(rightmost);
//     assert(it != 0);			// 'it' MUST be on the current level
    assert (it != level.end());
    
    list<DAG_Node*>::const_iterator succ_it = it;
    ++succ_it;
    
//     list_item succ_it = level.succ(it);
//     assert(succ_it != 0);		// 'it' MUST have right neighbor
    assert (succ_it != level.end());
    
    if (*succ_it == leftmost) {

//     if (level[succ_it] == leftmost) {
	return true;
    }
    else {
	return false;
    }
}


//
// approaching
//
// This function checks if two regions tend to approach each other when
// we give way to the forces that result from gravity. When we enter this
// function, we already checked that regions reg1 and reg2 are neigh-
// boring. If they were not neighbored, they could not collide, cause
// they were separated by at least one (inactive) node. If they are
// neighbored, we go on checking if the necessary minimum distance still
// remaines if we both each regions move to the point where they are
// driven to by their forces. In case there's enough space left, we
// return false, cause both regions are independent. Otherwise we return
// true.
//

bool Directed_Acyclic_Graph::approaching (const Region& reg1,
    const Region& reg2) const
{
//     DAG_Node* rightmost = reg1.tail();
//     DAG_Node* leftmost = reg2.head();
    DAG_Node* rightmost = reg1.back();
    DAG_Node* leftmost = reg2.front();

    if (rightmost->xcoord() + reg1.force() + rightmost->width()/2.0
	+ minimum_distance (*rightmost, *leftmost)
	<= leftmost->xcoord() + reg2.force() - leftmost->width()/2.0) {
	return false;
    }
    else {
	return true;
    }
}


//
// minimum_distance
//
// This function computes the minimum distance between the two nodes
// dn1 and dn2 which -- of course -- depends on the node types and
// the values specified by the user.
//

double Directed_Acyclic_Graph::minimum_distance (const DAG_Node& dn1,
    const DAG_Node& dn2) const
{
    if (dn1.is_dummy() && dn2.is_dummy())
	return min_edge_edge_distance();
    else if (dn1.is_dummy() || dn2.is_dummy())
	return min_node_edge_distance();
    else
	return min_node_node_distance();
}


//
// shift_regions
//
// This function shifts all regions on the current level according to its
// forces and the space that's left over by the rest of the graph. We only
// shift regions with forces stronger than 'threshold', because values
// smaller than that might result from rounding errors or might be too
// small to be worth moving.
//

void Directed_Acyclic_Graph::shift_regions (
    list<Region*>& RL, const Order_Graph& ograph)
{
    Region* reg;
    double dist, remainder;
    int i, nr_of_regions;
    double total_force, old_force;

    //
    // Before we start to shift the nodes we compute the sum of
    // forces of all regions in the list. This value is used as
    // a criterion to abort shifting.
    //

    total_force = 0.0;
    list<Region*>::iterator it;
    list<Region*>::iterator end = RL.end();
    
    for (it = RL.begin(); it != end; ++it) {
	reg = *it;

//     forall (reg, RL) {
	total_force += fabs(reg->force());
    }

    //
    // The following while-loop is now going to be executed until
    // the total force of RL no longer changes, i.e. until there are
    // no more regions that can be moved.
    //

    do {
	nr_of_regions = RL.size();
	old_force = total_force;
	for (i=1; i<=nr_of_regions; i++) {
// 	    reg = RL.pop();
	    reg = RL.front();
	    RL.pop_front();
	    
	    if (reg->force() < -threshold || reg->force() > threshold) {

		if (animation_is(A_node)) {
		    reg->colorize(gt_graph(), light_red);
		    Tcl_Eval (tcl_interpreter(), "update");
		    have_a_break();
		}

		dist = compute_shift(*reg, ograph);
		reg->shift(dist);
		remainder = reg->force() - dist;
		total_force -= fabs(dist);

		if (fabs(remainder) >= threshold) {
		    reg->force(remainder);
		    RL.push_back(reg);
		    if (protocol()) {
			cout << "    partial shift: " << dist << " units, "
			     << "remainder= " << remainder << endl;
		    }
		}
		else {
		    if (protocol()) {
			cout << "    full shift: " << dist << " units" << endl;
		    }
		}

		if (animation_is(A_node)) {
		    update_all_nodes();
		    draw();
		    reg->colorize(gt_graph(), GT_Keys::white);
		}
	    }
	}
    }  while (fabs(total_force-old_force) > threshold);
}


//
// compute_shift
//
// This function computes how much a given region can be shifted to the
// left resp. to the right. We begin by marking all nodes in the current
// region as active (for faster identification in the following function!).
// Then we invoke the function compute_max_left resp. compute_max_right 
// with a DAG_Node 'dn' and the intended shift 'dist' of its according
// region (dist is a call-by-reference para- meter!). After passing all
// nodes of the region to this function, the value 'dist' tells the most
// possible shift for that particular region.  Finally we remove the
// marks we made at the beginning.
//

double Directed_Acyclic_Graph::compute_shift (
    const Region& reg, const Order_Graph& ograph) const
{
    double dist = reg.force();
    
    list<DAG_Node*>::const_iterator it;
    list<DAG_Node*>::const_iterator end = reg.end();
    
    for (it = reg.begin(); it != end; ++it) {
//     forall (dn, reg) {			// mark all nodes of the region
// 	dn->active(true);
	(*it)->active(true);
    }

    if (dist < 0) {
	for (it = reg.begin(); it != end; ++it) {
// 	forall (dn, reg) {
	    ograph.compute_max_left_shift(*it, dist);
	}
    }
    else {
	for (it = reg.begin(); it != end; ++it) {
// 	forall (dn, reg) {
	    ograph.compute_max_right_shift(*it, dist);
	}
    }

    for (it = reg.begin(); it != end; ++it) {
//     forall (dn, reg) {			// unmark all nodes of the region
	(*it)->active(false);
    }
    
    return dist;
}


//
// animate
//
// This function does the animation according to the specified mode.
// We have four degrees of animation: 'none', 'phase', 'level', and 'node'.
// 'none' means that we draw the graph after finishing the whole algo-
// rithm; 'phase' causes the graph to be redrawn after each complete
// phase (up or down) has been finished; when specifying 'level' the
// graph is redrawn after each done level; and finally 'node' redraws
// our graph after moving each single node.
//

void Directed_Acyclic_Graph::animate (Animation_Mode mode)
{
    if (animation_is(mode)) {
	update_all_nodes();
	draw();
    }
}


//
// update_node
//
// This function writes the values of node n stored in the local DAG data
// structure back into the GT_Graph data structure for drawing. We do not
// write back width and height, because they never change. The y-coordinate
// will also never change when it is set. However, it is better to  update
// it, because interaction from the user could change modify the graph on
// the canvas during animation.
//

void Directed_Acyclic_Graph::update_node (node n)
{
    gt_graph()->gt(n).graphics()->x(xcoord(n));
    gt_graph()->gt(n).graphics()->y(ycoord(n));
}


//
// update_all_nodes
//
// This function writes the values of all nodes stored in the local DAG data
// structure back into the GT_Graph data structure for drawing.
//

void Directed_Acyclic_Graph::update_all_nodes ()
{
    node n;
    forall_nodes (n, *attached()) {
	update_node(n);
    }
}


//
// draw
//
// This function redraws the whole graph in the current state by invoking the
// Tcl procedure 'update'.
//

void Directed_Acyclic_Graph::draw ()
{
    gt_graph()->draw();
    Tcl_Eval (tcl_interpreter(), "update");
}


//
// have_a_break
//
// This is only a temporary function for making pauses at several points.
//

void Directed_Acyclic_Graph::have_a_break ()
{
    if (stepping()) {
	cout << "> ";
	system("stty -echo cbreak");
	// no more stepping when hitting key 'x'
	if (getchar() == 'x') {
	    stepping(0);
	}
	system("stty echo -cbreak");
	cout << "\b\b";
    }
}

