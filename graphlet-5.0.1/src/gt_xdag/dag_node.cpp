/* This software is distributed under the Lesser General Public License */
//
// dag_node.cpp
//
// This file implements the class DAG_Node.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_xdag/dag_node.cpp,v $
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


#include "dag_node.h"


// ========================================
//
// class DAG_Node
//
// ========================================

//
// constructor
//

DAG_Node::DAG_Node ()
{
    the_xcoord = the_ycoord = the_width = the_height = 0.0;
    the_top = the_bottom = 0.0;
    the_is_dummy = the_active = false;

    the_old_xcoord = 0.0;
}


DAG_Node::DAG_Node (const DAG_Node& dn)
{
    the_owner = dn.owner();
    the_xcoord = dn.xcoord();
    the_ycoord = dn.ycoord();
    the_width  = dn.width();
    the_height = dn.height();
    the_top    = dn.top();
    the_bottom = dn.bottom();
    the_is_dummy = dn.is_dummy();
    the_active = dn.active();

    the_old_xcoord = dn.old_xcoord();
}


//
// destructor
//

DAG_Node::~DAG_Node ()
{
}


//
// operator<<
//
// outputs the dag node properties and shows if n is a dummy and/or active
//


ostream& operator<< (ostream& os, const DAG_Node& dn)
{
    return os << (dn.is_dummy() ? " (*) " : " (-) ")
	      << "x=" << dn.xcoord() << ", y=" << dn.ycoord() 
	      << ", w=" << dn.width()  << ", h=" << dn.height() 
	      << ", top=" << dn.top()  << ", bottom=" << dn.bottom()
	      << (dn.active() ? ", active" : ", inactive");
}


//
// operator>>
//
// This input operator is unused.
//

istream& operator>> (istream& is, DAG_Node& /* dn */)
{
    return is;
}


//
// compare
//
// compare if two DAG_Nodes are identical
//

int compare (DAG_Node* const& dn1, DAG_Node* const& dn2)
{
    int result = 0;
    if (dn1 < dn2) result = -1;
    if (dn1 > dn2) result =  1;

    return result;
}



// int compare_x (DAG_Node* const& dn1, DAG_Node* const& dn2)
// {
//     int result = 0;
//     double diff = dn1->xcoord() - dn2->xcoord();
//     if (diff < 0) result = -1;
//     if (diff > 0) result =  1;

//     if (result == 0) {
// 	// compare the old x-coordinates if xcoords are identical
// 	diff = dn1->old_xcoord() - dn2->old_xcoord();
// 	if (diff < 0) result = -1;
// 	if (diff > 0) result =  1;
//     }

//     return result;
// }


//
// distance
//
// This function computes the distance between two nodes, i.e. the
// difference from the right border of the left node to the left border
// of the right node.
//

double DAG_Node::distance (const DAG_Node& dn) const
{
    return ( dn.xcoord() - xcoord() - (dn.width() + width())/2.0 );
}
