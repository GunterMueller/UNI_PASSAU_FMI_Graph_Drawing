/* This software is distributed under the Lesser General Public License */
//
// region.h
//
// This file defines the class Region.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_xdag/region.h,v $
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


#ifndef REGION_H
#define REGION_H


#include <gt_base/Graphlet.h>

#include <list>

// temporary include because of 'colorize'
#include <gt_base/Graph.h>


//
// predefinitions
//

class DAG_Node;


// ============================================================================
//
// class Region
//
// ============================================================================

class Region : public list<DAG_Node*>
{
    GT_BASE_CLASS (Region);

private:

    GT_VARIABLE(double, force);

public:

    //
    // constructors / destructor
    //

    Region ();
    Region (DAG_Node* dn);
    virtual ~Region ();

    //
    // for the new LEDA
    //

    friend ostream& operator<< (ostream& os, const Region& reg);
    friend istream& operator>> (istream& is, Region& reg);
    friend int compare (Region* const& reg1, Region* const& reg2);

    //
    // methods working on a region's nodes
    //

    void colorize (GT_Graph* g, GT_Key color);
    void shift (double delta);
};


#endif
