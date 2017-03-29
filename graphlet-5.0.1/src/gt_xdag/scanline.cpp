/* This software is distributed under the Lesser General Public License */
//
// scanline.cpp
//
// This file implements the class Scanline.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_xdag/scanline.cpp,v $
// $Author: raitner $
// $Revision: 1.2 $
// $Date: 1999/01/03 16:05:45 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1997, Graphlet Project
//
//     Author: Harald Mader (mader@fmi.uni-passau.de)
//

#include <algorithm>
#include "scanline.h"
#include "level.h"

// ============================================================================
//
// class Scanline
//
// ============================================================================

//
// constructors
//

Scanline::Scanline ()
{
    attached(0);
    the_dag_node = 0;
}


Scanline::Scanline (graph* g, node_map<DAG_Node>* dn_map)
{
    attached(g);
    the_dag_node = dn_map;
}


//
// destructor
//

Scanline::~Scanline ()
{
}


//
// operator<<
//
// outputs the scanlines, i.e. all levels belonging to the scanlines
//

ostream& operator<< (ostream& os, const Scanline& sl)
{
    Level* l;
    
    vector<Level*>::const_iterator it;
    vector<Level*>::const_iterator e = sl.the_list.end();
    
    for (it = sl.the_list.begin(); it != e; ++it) {
	l = *it;
	os << *l << endl;
    }

//     forall (l, sl) {
// 	os << *l << endl;
//     }

    return os;
}


//
// operator>>
//
// This input operator is unused.
//

istream& operator>> (istream& is, Scanline& /* sl */)
{
    return is;
}


//
// new_level
//
// add a new level to the scanline
//

void Scanline::new_level (double y)
{
    // create a new Level entry for the list
    vector<Level*>::iterator it = the_list.begin();
    vector<Level*>::iterator end = the_list.end();

    while (it != end) {
	if ((*it)->ycoord() == y) {
	    return;
	}
	++it;
    }

    the_list.push_back (new Level(y));

//     if (! search(l)) {
// 	append(l);
//     }
}


//
// sort_levels
//
// sort the scanline (after appending the last level) and
// give an index number to all of the levels
//


struct comp_level : public binary_function<Level*, Level*, bool> {
    bool operator()(Level* l1, Level* l2) {return l1->ycoord() < l2->ycoord();}
};

void Scanline::sort_levels ()
{
    sort (the_list.begin(), the_list.end(), comp_level());
    int count = 0;

    vector<Level*>::const_iterator it;
    vector<Level*>::const_iterator e = the_list.end();
    
    for (it = the_list.begin(); it != e; ++it) {
 	(*it)->nr(++count);
    }

//     forall (l, *this) {
//  	l->nr(++count);
//     }
}


//
// find
//
// This function searches the list for an entry with y-coordinate
// ycoord and returns a pointer to the found level.
//

Level* Scanline::find_level (double ycoord)
{
    // create a new Level for seeking in the list
//     Level* seek_level = new Level(ycoord);
    
    vector<Level*>::iterator it = the_list.begin();
    vector<Level*>::iterator end = the_list.end();

    while (it != end) {
	if ((*it)->ycoord() == ycoord) {
	    return *it;
	}
	++it;
    }

//     list_item found = search(seek_level);
//     delete seek_level;
//     assert(found != 0);

    assert(false);	
    return 0;

//     Level* level_found = (*this)[found];
//     return level_found;
}


//
// find
//
// This function returns a pointer to the level with number nr.
//

Level* Scanline::find_level (int nr)
{   
    assert ((unsigned int) nr  < the_list.size());
    return the_list[nr-1];
//     list_item found = (*this)[nr-1];
//     Level* level_found = (*this)[found];

//     return level_found;
}


//
// build
//
// This function builds the nodelists for each level by traversing all levels
// and assigning all nodes that intersect that level to the according list.
//

void Scanline::build (int really)
{
    Level* l;
    double y;
    node n;
    DAG_Node* dn;
    int total_entries=0;

    vector<Level*>::iterator it;
    vector<Level*>::iterator e = the_list.end();
    
    for (it = the_list.begin(); it != e; ++it) {
	l = *it;
	y = l->ycoord();
	forall_nodes (n, *attached()) {
	    dn = &dag_node(n);
	    if (dn->top()<=y && y<=dn->bottom()) {
		l->push_back(dn);
		total_entries++;
	    }
	}
    }	

//     forall (l, *this) {
// 	y = l->ycoord();
// 	forall_nodes (n, *attached()) {
// 	    dn = &dag_node(n);
// 	    if (dn->top()<=y && y<=dn->bottom()) {
// 		l->append(dn);
// 		total_entries++;
// 	    }
// 	}
//     }
    
    if (really) {
	cout << "    " << total_entries
 	     << " pointers to DAG_Nodes in all Levels" << endl;
    }
}


