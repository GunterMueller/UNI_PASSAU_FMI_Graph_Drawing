/* This software is distributed under the Lesser General Public License */
//
// region.cpp
//
// This file implements the class Region.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_xdag/region.cpp,v $
// $Author: himsolt $
// $Revision: 1.1.1.1 $
// $Date: 1998/08/27 17:19:30 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1997, Graphlet Project
//
//     Author: Harald Mader (mader@fmi.uni-passau.de)
//


#include "region.h"
#include "dag_node.h"


// ============================================================================
//
// class Region
//
// ============================================================================

//
// constructor
//
// create a new region with dn as its only node
//

Region::Region()
{
    force(0.0);
}


Region::Region(DAG_Node* dn)
{
    push_back(dn);
    force(0.0);
}


//
// destructor
//

Region::~Region ()
{
}


//
// operator<<
//
// outputs the region's data (size and force)
//

ostream& operator<< (ostream& os, const Region& reg)
{
    return os << "(size=" << reg.size() << ", force=" << reg.force() << ")";
}


//
// operator>>
//
// This input operator is unused.
//

istream& operator>> (istream& is, Region& /*reg*/)
{
    return is;
}


//
// compare
//
// compare function for regions
//

int compare (Region* const& reg1, Region* const& reg2)
{
    int result = 0;
    if (reg1 < reg2) result = -1;
    if (reg1 > reg2) result =  1;

    return result;
}


//
// colorize
//
// color all nodes of the region with the given color
//

void Region::colorize (GT_Graph* g, GT_Key color)
{
    DAG_Node* dn;

    list<DAG_Node*>::iterator it;
    list<DAG_Node*>::iterator e = end();

    for (it = begin(); it != e; ++it) {
	dn = *it;
	g->gt(dn->owner()).graphics()->fill(color);
	g->draw(dn->owner());
    }

//     forall (dn, *this) {
// 	g->gt(dn->owner()).graphics()->fill(color);
// 	g->draw(dn->owner());
//     }
}


//
// shift
//
// move all nodes of the region 
//

void Region::shift (double delta)
{
    DAG_Node* dn;
    list<DAG_Node*>::iterator it;
    list<DAG_Node*>::iterator e = end();

    for (it = begin(); it != e; ++it) {
	dn = *it;
	dn->xcoord(dn->xcoord()+delta);
    }

//     forall (dn, *this) {
// 	dn->xcoord(dn->xcoord()+delta);
//     }
}


