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

///////////////////////////////////////////////////////
// constructors & destructors

LEDA_Sgraph_association::LEDA_Sgraph_association()
{
	SNodeMap = new map<int,Snode> ();
	SEdgeMap = new map<int,Sedge> ();
}

LEDA_Sgraph_association::~LEDA_Sgraph_association()
{
	delete SNodeMap;
	delete SEdgeMap;
}

///////////////////////////////////////////////////////
// service-methods

void LEDA_Sgraph_association::clear_association_maps(void)
{
	ENTRY;
	
	LSD* act_lsd = lsd();
	Sgraph sgraph = act_lsd->sgraph();
	
	if (sgraph != empty_sgraph)
    {
		cerr << "can not clear_association_maps() "
			 << "if the_sgraph != empty_sgraph!" << endl;
		assert(sgraph == empty_sgraph);
    }

	delete SNodeMap;
	delete SEdgeMap;
	SNodeMap = new map<int,Snode>;
	SEdgeMap = new map<int,Sedge>;

	LEAVE;
}

//**************************************
// LEDA-node <=> SGraph-node
//**************************************

// associate the two nodes leda_node and snode. So we can retrieve
// each others counterpart later.
// In the Snode-structure, we (mis-)use the member "graphed" to
// establish a pointer to the LEDA-counterpart.
// To establish the reverse direction, we put a tuple
// (LEDA-nodeindex, pointer to snode) into a map.
// Precondition: none of them is yet associated. (checked)

#ifdef __GNU_C__
template class LSD_Meta_Reference<Snode, node>;
// template class LSD_Meta_Reference<Sedge, edge>;
#endif

void LEDA_Sgraph_association::set_associated_nodes(node leda_node, Snode snode)
{
    // Associated nodes MUST NOT be associated again!
    // Deassociate them first.
    assert(get_associated_snode(leda_node) == 0);
    assert(get_associated_lnode(snode) == node());
	
    (*SNodeMap)[leda_node.id()] = snode;

    snode->graphed = (char*) new LSD_Meta_Reference<Snode, node>(lsd(), snode, leda_node);
}

// get the Sgraph-node associated with leda_node
// if leda_node in not associated with any node, return 0.

Snode LEDA_Sgraph_association::get_associated_snode(node leda_node)
{
    map<int,Snode>::iterator it = SNodeMap->find(leda_node.id());

    if (it != SNodeMap->end()) {
	return (*it).second;
    } else {
	return (Snode) 0;
    }
}


// get the LEDA-node associated with snode
// if snode in not associated with any node, return 0.

node LEDA_Sgraph_association::get_associated_lnode(Snode snode)
{
    if (snode->graphed == 0) {
	return node();
    } else {
	return ((NodeRef*) snode->graphed)->ref();
    }
}


// de-associate two nodes, where one of them is leda_node
// precondition: leda_node is associated

void LEDA_Sgraph_association::deassociate_nodes(node leda_node)
{
    Snode snode = get_associated_snode(leda_node);

    if (snode) {
	delete (NodeRef*) snode->graphed;
	snode->graphed = 0;
    }

    SNodeMap->erase(leda_node.id());
}


// de-associate two nodes, where one of them is snode
// precondition: snode is associated

void LEDA_Sgraph_association::deassociate_nodes(Snode snode)
{
    node lnode = get_associated_lnode(snode);

    if (lnode != node()) {
	SNodeMap->erase(lnode.id());
    }
	
    delete (NodeRef*) snode->graphed;
    snode->graphed = 0;
}


//**************************************
// LEDA-edge <=> SGraph-edge
//**************************************

// associate the two edges leda_edge and sedge. So we can retrieve
// each others counterpart later.
// In the Sedge-structure, we (mis-)use the member "graphed" to
// establish a pointer to the LEDA-counterpart.
// To establish the reverse direction, we put a tuple
// (LEDA-edgeindex, pointer to sedge) into a map.
// Precondition: none of them is yet associated.

void LEDA_Sgraph_association::set_associated_edges(edge leda_edge, Sedge sedge)
{
    // Associated edges MUST NOT be associated again!
    // Deassociate them first.
    assert(get_associated_sedge(leda_edge) == 0);
    assert(get_associated_ledge(sedge) == edge());
		
    (*SEdgeMap)[leda_edge.id()] = sedge;

    sedge->graphed = (char*) new EdgeRef(lsd(), sedge, leda_edge);

    // in undirected graphs a undirected edge consists on two
    // edges, so we have to set the "graphed"-pointer
    // in both of them...
    if (sedge->snode->graph->directed == false) {
	// In the next step, a tricky detail of Sgraphs
	// implementation is uses since there was no other
	// way to set the "graphed"-pointer in both edges.
	// This "hack" is done with permission of M. Himsolt
		
	sedge->tsuc->graphed = sedge->graphed;		
    }
}

// get the Sgraph-edge associated with leda_edge
// if leda_edge in not associated with any edge, return 0.

Sedge LEDA_Sgraph_association::get_associated_sedge(edge leda_edge)
{
    map<int, Sedge>::iterator it = SEdgeMap->find (leda_edge.id());

    if (it != SEdgeMap->end()) {
	return (*it).second;
    } else {
	return (Sedge) 0;
    }
}

// get the LEDA-edge associated with sedge
// if sedge in not associated with any edge, return 0.

edge LEDA_Sgraph_association::get_associated_ledge(Sedge sedge)
{
    if (sedge->graphed == 0) {
	return edge();
    } else {
	return ((EdgeRef*) sedge->graphed)->ref();
    }
}

// de-associate two edges, where one of them is leda_edge
// precondition: leda_edge is associated
// (For information about "tsuc" s.a. "set_associated_edges()")

void LEDA_Sgraph_association::deassociate_edges(edge leda_edge)
{
	
    Sedge sedge = get_associated_sedge(leda_edge);
    
    if (sedge) {
	delete (EdgeRef*) sedge->graphed;
	sedge->graphed = 0;
		
	if (lsd()->sgraph()->directed == false) {
	    sedge->tsuc->graphed = 0;
	}
    }

    SEdgeMap->erase(leda_edge.id());
}

// de-associate two edgse, where one of them is sedge
// precondition: sedge is associated
// (For information about "tsuc" s.a. "set_associated_edges()")

void LEDA_Sgraph_association::deassociate_edges(Sedge sedge)
{
    edge ledge = get_associated_ledge(sedge);

    if (ledge != edge()) {
	SEdgeMap->erase(ledge.id());
    }
	
    delete (EdgeRef*) sedge->graphed;
    sedge->graphed = 0;
	
    if (lsd()->sgraph()->directed == false) {
	sedge->tsuc->graphed = 0;
    }
}



void LEDA_Sgraph_association::reset_associations (const graph& ledagraph)
{
//     node n;
//     edge e;

//     forall_nodes(n, ledagraph) {
// 	(*SNodeMap)[n.id()] = (Snode)0;
// 	forall_adj_edges (e, n) {
// 	    (*SEdgeMap)[e.id()] = (Sedge)0;
// 	}
//     }
     SNodeMap->erase (SNodeMap->begin(), SNodeMap->end());
     SEdgeMap->erase (SEdgeMap->begin(), SEdgeMap->end());
}
