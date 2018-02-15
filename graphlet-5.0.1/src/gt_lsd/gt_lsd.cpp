/* This software is distributed under the Lesser General Public License */
// 
// L      S      D
// Leda & Sgraph Do it
//
//
// Author: Dirk Heider
// email: heider@fmi.uni-passau.de
// Changes and adjustment to graphlet: Walter Bachl
// Additional Changes: Michael Himsolt
//

///////////////////////////////////////////////////////////
//
// Main Programm to call the algorithms which are
// implemented in sgraph.
//
///////////////////////////////////////////////////////////

#include <gt_base/Graphlet.h>

#include <gt_tcl/Tcl.h>
#include <gt_tcl/Tcl_Algorithm.h>
#include <gt_tcl/Graphscript.h>
#include <gt_base/NEI.h>

// LSD standard includes
#include "lsd/lsdstd.h"
#include "gt_lsd.h"

//-----------------------------------------------------------------------------
// hide_multiedges
// hide_selfloops
// Functions are from Robert Schirmer.
// ----------------------------------------------------------------------------

void hide_selfloops (graph *g, list<edge> &edges)
{
    edge e;
    forall_edges (e, *g) {
        if (e.source() == e.target()) {
            edges.push_back(e);
        }
    }

    list<edge>::iterator it = edges.begin();
    list<edge>::iterator end = edges.end();
    
    while (it != end) {
 	g->hide_edge (*it);
	++it;
    }
}

void hide_multiedges (graph *g, list<edge> &edges) {
    edge e1, e2;

    list<edge>::const_iterator it;
    list<edge>::const_iterator tmp;
    list<edge>::const_iterator end = g->edges_end();
    
    for (it = g->edges_begin(); it != end; ++it) {
	
	//
	// search next edge (forward) with same set of source and target
	// and add it to edges
	//
    
	e1 = *it;
	tmp = it;
	++tmp;

	while (tmp != end) {
	    e2 = *tmp;
	    
	    if (e1.target() == e2.target() && e1.source() == e2.source() ||
		e1.target() == e2.source() && e1.source() == e2.target()) {
		edges.push_back (e2);
		break;
	    }
	    
	    ++tmp;
	}	
    }

    //
    // hide edges    
    //

    it = edges.begin();
    end = edges.end();
    
    while (it != end) {
 	g->hide_edge (*it);
	++it;
    }
}


// void hide_multiedges (graph *g, list<edge> &edges)
// {
//     node n;
//     edge e1;
//     edge e2;

//     edge_map<int> multitude (*g, 1);
    
//     node::out_edges_iterator out_it;
//     node::out_edges_iterator out_tmp;
//     node::out_edges_iterator out_end;
//     node::in_edges_iterator in_it;
//     node::in_edges_iterator in_end;
//     node::adj_edges_iterator adj_it;
//     node::adj_edges_iterator adj_tmp;
//     node::adj_edges_iterator adj_end;
    

//     if (g->is_directed()) {
// 	forall_nodes (n, *g) {
	    
// 	    out_end = n.out_edges_end();

// 	    // in leda the adj edges are the outedges	    
// 	    for (out_it = n.out_edges_begin(); out_it != out_end; ++out_it) {
// 		e1 = *out_it;

// 		if (multitude[e1] == 1) {
// 		    out_tmp = out_it;
// 		    ++out_tmp;

// 		    while (out_tmp != n.out_edges_end()) {
// 			e2 = *out_tmp;
		    
// 			if (e1.target() == e2.target()) {
// 			    multitude[e2] = 0;
// 			    edges.push_back(e2);
// 			}
			
// 			++out_tmp;
// 		    }

// 		    // also check the combination between an in and outedge
		    
// 		    in_end = n.in_edges_end ();
		    
// 		    for (in_it = n.in_edges_begin(); in_it != in_end; ++in_it) {
// 			e2 = *in_it;
			
// 			if (e1.target() == e2.source()) {
// 			    multitude[e2] = 0;
// 			    edges.push_back(e2);
// 			}
// 		    }
// 		}
// 	    }
// 	}

//     } else {

// 	forall_nodes (n, *g) {
// 	    adj_end = n.adj_edges_end();
	    
// 	    for (adj_it = n.adj_edges_begin(); adj_it != adj_end; ++adj_it) {
// 		e1 = *adj_it;

// 		if (multitude[e1] == 1) {		    
// 		    adj_tmp = adj_it;
// 		    ++adj_tmp;

// 		    while (adj_tmp != adj_end) {
// 			e2 = *adj_tmp;
			
// 			if ((n.opposite(e1) == n.opposite(e2)) &&
// 			    (multitude[e2] == 1)) {
// 			    multitude[e2] = 0;
// 			    edges.push_back(e2);
// 			}
// 		    }
// 		}
// 	    }
// 	}
//     }

//     // hide edges
    
//     list<edge>::iterator it = edges.begin();
//     list<edge>::iterator end = edges.end();
    
//     while (it != end) {
//  	g->hide_edge (*it);
// 	++it;
//     }
// }

//////////////////////////////////////////////
//
// Main Initialization function for interface
//
// Tcl/Tk naming conventions
//
//////////////////////////////////////////////

int Gt_lsd_Init (Tcl_Interp* interp)
{
    int code = TCL_OK;

    char version[(2+2+2) + 2 + 1];
    sprintf (version, "%d.%d.%d",
	GT_MAJOR_VERSION, GT_MINOR_VERSION, GT_MINI_VERSION);
    code = Tcl_PkgProvide (interp, "Gt_lsd", version);
    if (code != TCL_OK) {
	return code;
    }
    return code;
}
