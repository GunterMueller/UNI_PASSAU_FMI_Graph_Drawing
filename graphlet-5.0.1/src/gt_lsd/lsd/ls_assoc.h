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
// LEDA and Sgraph are associated by maps.
// To make it possible to get the corresponding Sgraph-part of
// a LEDA edge or node and reverse,
// we have to store the association between these counterparts.
//
// In direction LEDA=>Sgraph this is done by maps (LEDA-
// hashtables) which retrieve the counterpart in expected O(1).
// These maps retrieve the Sgraph-pointer (to a node or an edge)
// for a given LEDA-Index (of a node or an edge).
//
// The reverse direction from Sgraph-parts to their corresponding
// LEDA-parts is stored directly in the "graphed" pointerfield
// of a Sgraphs node or edge.
// Since we always must know to which LSD-(interface)-instance
// a Sgraph-part belongs (because we have to call non-static
// methods of this instance) the "graphed"-pointer does NOT
// point directly onto the LEDA-part, but on a element referred to
// as "meta-reference" (class LSD_Meta_Reference).
// This meta-reference stores the reference to the LEDA-part
// AND a reference to our actual LSD-instance.
// So, by dereferencing in two steps, access is done in O(1).
//
// Furthermore, the "graphed"-pointer of the Sgraph-graph structure
// references the LSD-instance directly.
// 
///////////////////////////////////////////////////////////

#ifndef LS_ASSOZ_H
#define LS_ASSOZ_H

// LSD-standard includes
#include <lsd/lsdstd.h>

class LEDA_Sgraph_association
{
	///////////////////////////////////////////////////////
	// methods
public:

	///////////////////////////////////////////////////////
	// constructors & destructors
	
	LEDA_Sgraph_association();
	virtual ~LEDA_Sgraph_association();

	///////////////////////////////////////////////////////
	// service-methods

	void    clear_association_maps(void);
	
	void    set_associated_nodes(node leda_node, Snode snode);
	Snode   get_associated_snode(node leda_node);
	node    get_associated_lnode(Snode snode);
	void    deassociate_nodes(node leda_node);
	void    deassociate_nodes(Snode snode);

	void    set_associated_edges(edge leda_edge, Sedge sedge);
	Sedge   get_associated_sedge(edge leda_edge);
	edge    get_associated_ledge(Sedge sedge);
	void    deassociate_edges(edge leda_edge);
	void    deassociate_edges(Sedge sedge);
	
    //////////////////////////////////////////////
    // This method is needed because of an error in the leda class 'map'
    // If the map consists of more than 512 elements, then the 513
    // element is NOT inizialized to 'undefined'
    // 
    // MR: Since we don't use LEDA anymore we should be able to avoid this
    void reset_associations (const graph& ledagraph);
    // really ???
    //////////////////////////////////////////////

private:
	
	///////////////////////////////////////////////////////
	// declare a assign- & a copy-operator as private methods
	// but dont define them - so it is impossible to missuse
	// default-operators created by the compiler.
	
	const LEDA_Sgraph_association& operator= (
		const LEDA_Sgraph_association& ls_assoz_object);
	LEDA_Sgraph_association(
		const LEDA_Sgraph_association& ls_assoz_object);
	
	virtual LSD* lsd(void) = 0;

	///////////////////////////////////////////////////////
	// members
	
private:
	
	map<int,Snode> *SNodeMap;
	map<int,Sedge> *SEdgeMap;

};

#endif // LS_ASSOZ_H



