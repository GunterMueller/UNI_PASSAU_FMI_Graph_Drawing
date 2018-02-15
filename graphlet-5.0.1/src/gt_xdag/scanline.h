/* This software is distributed under the Lesser General Public License */
//
// scanline.h
//
// This file defines the class Scanline.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_xdag/scanline.h,v $
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


#ifndef SCANLINE_H
#define SCANLINE_H


#include <gt_base/Graphlet.h>

#include <GTL/graph.h>
#include <GTL/node_map.h>
#include <list>
#include <iostream>
#include "dag_node.h"


//
// predefinitions
//

class Level;


// ============================================================================
//
// class Scanline
//
// This class stores a sorted list of all levels of the graph. For a better
// access to the data of the graph's nodes it also contains a reference to
// the DAG's node map.
//
// ============================================================================

class Scanline
{
    GT_BASE_CLASS (Scanline);

    GT_VARIABLE (graph*, attached);

private:
    vector<Level*> the_list;
    node_map<DAG_Node>* the_dag_node;

public:

    //
    // Iterators
    //

    typedef vector<Level*>::iterator iterator; 
    typedef vector<Level*>::reverse_iterator reverse_iterator;

    iterator begin() {return the_list.begin();}
    iterator end() {return the_list.end();}
    reverse_iterator rbegin() {return the_list.rbegin();}
    reverse_iterator rend() {return the_list.rend();}

    //
    // constructors and destructors
    //

    Scanline ();
    Scanline (graph* g, node_map<DAG_Node>* dn_map);
    virtual ~Scanline ();

    //
    // accessors for member variables
    //

    const DAG_Node& dag_node (node n) const  { return (*the_dag_node)[n]; }
    DAG_Node& dag_node (node n)              { return (*the_dag_node)[n]; }

    //
    // necessary for LEDA
    //

    friend ostream& operator<< (ostream& os, const Scanline& sl);
    friend istream& operator>> (istream& is, Scanline& sl);

    //
    // other public methods
    //

    void new_level (double y);
    void sort_levels ();
    void build (int really);

    //
    // two functions for finding an according level
    //

    Level* find_level (double ycoord);
    Level* find_level (int nr);
};


#endif
