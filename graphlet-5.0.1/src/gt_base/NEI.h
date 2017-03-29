/* This software is distributed under the Lesser General Public License */
#ifndef NEI_h
#define NEI_h 
// ---------------------------------------------------------------------
// NEI.h
// 
// Classes for the Node-Edge-Interface
// ---------------------------------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/NEI.h,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:44:26 $
// $Locker:  $
// $State: Exp $
// ----------------------------------------------------------------------
//
// (C) University of Passau 1995-1999, Graphlet Project, Walter Bachl
//

#include <vector>

class GT_Graph;


enum Side {
	GT_Source,
	GT_Target
};

enum Directions {
    GT_Top,
    GT_Bottom,
    GT_Left,
    GT_Right
};

// Functions defined in NEI.cpp

extern int reset_NEIs (GT_Graph &g);
extern double sqr (double x);

// ----------------------------------------------------------------------
// General:
//
// We define two classes NEI, one for a node, one for an edge.
// Each node and edge gets an corresponding NEI.
// The NEI for nodes is a collection of functions to manipulate the
// Edgeanchors of all adjacent edges.
// The NEI for edges contains the anchors for the edge. It also has
// some functions to manipulate the Edgeanchor.
// Both classes have a field for the current hook-up function for a
// redraw.

#include "edge_NEI.h"
#include "node_NEI.h"


#endif
