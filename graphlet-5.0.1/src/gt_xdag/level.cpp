/* This software is distributed under the Lesser General Public License */
//
// level.cpp
//
// This file implements the class Level.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_xdag/level.cpp,v $
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


#include "level.h"
#include "dag_node.h"


// ============================================================================
//
// class Level
//
// ============================================================================

//
// constructors
//

Level::Level ()
{
    nr(0);
    ycoord(0.0);
}


Level::Level (double y)
{
    nr(0);
    ycoord(y);
}


//
// destructor
//

Level::~Level ()
{
}


//
// operator<<
//
// outputs the level number and the corresponding y-coordinate
//

ostream& operator<< (ostream& os, const Level& l)
{
    return os << "    LEVEL " << l.nr() << " = " << l.ycoord();
}


//
// operator>>
//
// This input operator is unused.
//

istream& operator>> (istream& is, Level& /* l */)
{
    return is;
}


//
// compare
//
// compare function for sorting of list items
//

// int compare (Level* const& l1, Level* const& l2)
// {
//     int result = 0;
//     double diff = l1->ycoord()-l2->ycoord();
//     if (diff < 0) result = -1;
//     if (diff > 0) result =  1;

//     return result;
// }

//
// print_data
//
// print data about the level
//

void Level::print_data (int really)
{
    if (really) {
	cout << *this << endl;
    }
}


//
// print_nodes
//
// print information about the nodes on the level
//

void Level::print_nodes (int really, char* s, GT_Graph* g)
{
    if (really) {
	cout << s;
	DAG_Node* dn;
	
	list<DAG_Node*>::const_iterator it;
	list<DAG_Node*>::const_iterator e = end();
	
	for (it = begin(); it != e; ++it) {
	    dn = *it;
	    cout << g->gt(dn->owner()).label()
		 << ( dn->active() ? "+ (" : "- (" ) << dn->xcoord() << ")  ";
	}

// 	forall (dn, *this) {
// 	    cout << g->gt(dn->owner()).label()
// 		 << ( dn->active() ? "+ (" : "- (" ) << dn->xcoord() << ")  ";
// 	}
	cout << endl;
    }
}
