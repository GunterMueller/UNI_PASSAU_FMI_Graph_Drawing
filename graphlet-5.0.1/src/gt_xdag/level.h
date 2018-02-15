/* This software is distributed under the Lesser General Public License */
//
// level.h
//
// This file defines the class Level.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_xdag/level.h,v $
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


#ifndef LEVEL_H
#define LEVEL_H


#include <gt_base/Graphlet.h>
// temporary include because of 'print_nodes'
#include <gt_base/Graph.h>

#include <list>
#include <iostream>


//
// predefinitions
//

class DAG_Node;


// ============================================================================
//
// class Level
//
// This class is used to store information about those horizontal lines
// (called levels or layers), where a node either begins or ends. Further
// each level contains a list of pointers to all those nodes that intersect
// this level. A level has the following members:
//  - ycoord tells us the height of the level. 
//    (The first level starts at height 0.)
//  - nr is a unique number for each level, i.e. a index.
// These numbers are calculated as follows: After all levels are computed, 
// we sort the list of levels. Then we give successive integer number to
// successive levels. Thus we have a very easy way to test later if an edge
// is a long-span or a short-span edge by simply comparing the numbers of
// the corresponding levels.
//
// ============================================================================

class Level : public list<DAG_Node*>
{
    GT_BASE_CLASS (Level);
    
    GT_VARIABLE (int, nr);
    GT_VARIABLE (double, ycoord);

public:

    Level ();
    Level (double y);
    virtual ~Level ();

    //
    // necessary for LEDA 
    //

    friend ostream& operator<< (ostream& os, const Level& lev);
    friend istream& operator>> (istream& is, Level& lev);
//     friend int compare (Level* const& lev1, Level* const& lev2);

    //
    // other public methods
    //

    void print_data (int really);
    void print_nodes (int really, char* s, GT_Graph* g);
};
    

#endif
