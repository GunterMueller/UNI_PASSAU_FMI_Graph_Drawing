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
// (see headerfile)
//
///////////////////////////////////////////////////////////

// LSD-standard includes
#include "lsdstd.h"

int count_refs = 0;


// generate code for
// LSD_Meta_Reference<Sedge, edge> (EdgeRef) and
// LSD_Meta_Reference<Snode, node> (NodeRef):


////////////////////////////////////////////////////////////////////////////
// WA: the_real_source and the_real_target are necessary for undirected
//     graphs. If we create a polyline, then we have an implicit declaration
//     of a direction. In that case, we want to have the same direction as
//     in the associated sedge.
// WA: 24.5.96 changed GA_Graph to GT_Graph
//             differntiation between self-loop and normal edge eliminated

LSD_Meta_Edge_Reference::LSD_Meta_Edge_Reference(LSD* lsd_object,
    Sedge sedge, edge ledge)
	: LSD_Meta_Reference<Sedge, edge>(lsd_object, sedge,
	    ledge)
{
    int       gt_source_index, gt_target_index;
	
    gt_source_index = lsd()->gt_graph()->gt(ledge.source()).id();
    gt_target_index = lsd()->gt_graph()->gt(ledge.target()).id();
	
    the_el = 0;
	
    // Ann.: we gave every Snode the number of its GA-counterpart
	
    if (gt_source_index == sedge->snode->nr)   {
	// the target must be the other node
	assert(gt_target_index == sedge->tnode->nr);
				
	the_real_source = sedge->snode;
	the_real_target = sedge->tnode;
    } else {
	// the reverse order must be established
	assert(gt_source_index == sedge->tnode->nr);
	assert(gt_target_index == sedge->snode->nr);
			
	the_real_source = sedge->tnode;
	the_real_target = sedge->snode;
    }
}

LSD_Meta_Edge_Reference::~LSD_Meta_Edge_Reference()
{
    if (the_el)
    {
	free_edgeline(the_el);
	the_el = 0;
    }
}

void LSD_Meta_Edge_Reference::edgeline(Edgeline a_el)
{
    if (a_el != the_el)
    {
	free_edgeline(the_el);
	the_el = a_el;
    }
    else
    {
	if (a_el != 0) {
	    cerr << "the_el == a_el" << endl;
	}
    }
}
	



