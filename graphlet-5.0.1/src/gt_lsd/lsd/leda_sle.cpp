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
// The LEDA- STRAIGHT_LINE_EMBEDDING-algorithm
// is executed on the GA-Graph. The changes made are NOT
// reflected on the Sgraph-Counterpart. To let the changes
// take effect on the Sgraph-graph for a second algorithm,
// the GA-graph is copied to the Sgraph before running any
// further algorithm.
// To prepare the graph for STRAIGHT_LINE_EMBEDDING()
// LEDA's PLANAR() must be called for clockwise
// ordering of the edges.
///////////////////////////////////////////////////////////

/*

// LSD-standard includes
#include "lsdstd.h"
#include <GTL/graph_misc.h>
#include <vector>

static void delete_multiple_edges(graph& lgraph);

static node_map<int> node_xcoord;
static node_map<int> node_ycoord;

////////////////////////////////////////////////////////////////////////////
// WA: CH Wo wird der Algo (straight line embedding) aufgerufen?
//        Warum fuegt er Kanten ein, die er dann eh wieder loescht?
//

void LEDA_SLE(Sgraph_proc_info info)
{
    ENTRY;
    Sgraph           sgraph                 = info->sgraph;
    LSD*             lsd                    = (LSD*) sgraph->graphed;
    GT_Graph*        gt_graph               = lsd->gt_graph();	
    graph*           leda_graph             = gt_graph->attached();
	
    list<edge>       additional_edges;
    bool             edges_added            = false;
    bool             undirected;
    edge_map<edge> bi_edges(*leda_graph);
    list<GT_Point>   gt_line;

    edge             ledge;
    node             lnode, target;

    assert(sgraph);
    assert(lsd);
    assert(gt_graph);
    assert(leda_graph);
	
    // nothing changes in "the_sgraph", since only
    // algorithms on LEDA-graphs are used here:	
    info->no_changes = true;

    // sorry, to make a graph directed, it must be directed!
    if ((undirected = leda_graph->is_undirected()))	{
	leda_graph->make_directed();
    }
	
    // sorry, to compute LEDAs SLE, the graph MUST be bidirected!
    if (!leda_graph->is_bidirected(bi_edges))	{
	// WA CH Darf der wirklich einfach so die Kanten loeschen?
	// If some edges have a reverse yet, delete it. Otherwise, 
	// a reverse for a reverse edge will be inserted below.
	delete_multiple_edges(*leda_graph);
	// no bi-edges are there now. ->insert the reverse for every edge
	additional_edges = leda_graph->insert_reverse_edges();
	edges_added = true;
    }
    else {
    }

    // the graph given to STRAIGHT_LINE_EMBEDDING() has
    // to be a planar map, so we have to prepare it:	
    
    // MR: Ich hab eigentlich keine Lust eine Planaritaetstest zu programmieren :-)
    //
    //     if (!PLANAR(*leda_graph, true))	{
    // 	// cout << "LEDA-PLANAR() failed!" << endl;
    // 	LEAVE;
    // 	return;
    //     }
	
    TRACE("SLE");
    node_xcoord.init(*leda_graph);
    node_ycoord.init(*leda_graph);
    STRAIGHT_LINE_EMBEDDING(*leda_graph, node_xcoord, node_ycoord);

    forall_nodes(lnode, *leda_graph)	{
	// copy the x- and y-coordinates from the  nodearrays to
	// generic attributes of the GT-graph
	lsd->gt_graph()->gt(lnode).graphics()->x( (node_xcoord[lnode]) * 64 );
	lsd->gt_graph()->gt(lnode).graphics()->y( (node_ycoord[lnode]) * 64 );
    }
    TRACE("SLE o.k.");

    // set the edgelines to the new positions of their source & target nodes
    forall_nodes(lnode, *leda_graph)  {
	forall_out_edges(ledge, lnode)	{
	    //it is not necessary to delete the old edgeline!!!

	    // create the new edgeline: source (lnode is the source!)
	    gt_line.append(
		GT_Point( lsd->gt_graph()->gt(lnode).graphics()->x(),
		    lsd->gt_graph()->gt(lnode).graphics()->y() ) );
	    // and target position
	    target = lnode.opposite(ledge);
	    gt_line.append(
		GT_Point( lsd->gt_graph()->gt(target).graphics()->x(),
		    lsd->gt_graph()->gt(target).graphics()->y() ) );
	    // and set it
	    lsd->gt_graph()->gt(ledge).graphics()->line(gt_line);
	    gt_line.clear();
	}
    }

    list<edge>::iterator it;
    list<edge>::iterator end;

    if (edges_added)  {
	end = additional_edges.end();
	
	for (it = additional_edges.begin(); it != end; ++it) { 
	    leda_graph->del_edge(*it);
	}
	additional_edges.clear();
			
    }

    if (undirected)  {
	leda_graph->make_undirected();
    }
    LEAVE;
}

////////////////////////////////////////////////////////////////////////////
//

void delete_multiple_edges(graph& lgraph)
{
    list<edge> del_list;	
    edge_map<bool> deleted(lgraph, false);

    edge e, e1, e2;
    node n;

    forall_nodes(n, lgraph)  {
	forall_out_edges(e1, n)	{
	    if (! deleted[e1])	{
		forall_in_edges(e2, n)	{
		    if (! deleted[e2])	{
			if (e1.target() == e2.source())		{
			    deleted[e2] = true;
			}
		    }
		}
	    }
	}
    }
	
    forall_edges(e, lgraph)	{
	if (deleted[e])		{
	    del_list.push_back(e);			
	}
    }

    list<edge>::iterator it;
    list<edge>::iterator end = del_list.end();    

    for (it = del_list.begin(); it != end; ++it) {
	lgraph.del_edge(*it);
    }
}

*/
