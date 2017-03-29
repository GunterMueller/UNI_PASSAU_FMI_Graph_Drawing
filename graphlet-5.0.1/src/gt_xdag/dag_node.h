/* This software is distributed under the Lesser General Public License */
//
// dag_node.cpp
//
// This file defines the class DAG_Node.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_xdag/dag_node.h,v $
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


#ifndef DAG_NODE_H
#define DAG_NODE_H


#include <gt_base/Graphlet.h>

#include <GTL/graph.h>


// ========================================
//
// class DAG_Node
//
// ========================================

class DAG_Node
{

    GT_BASE_CLASS (DAG_Node);
    
    GT_VARIABLE(node, owner);
    
//    GT_VARIABLE(double, xcoord);
    GT_VARIABLE(double, ycoord);
    GT_VARIABLE(double, width);
    GT_VARIABLE(double, height);
    GT_VARIABLE(double, top);
    GT_VARIABLE(double, bottom);
    GT_VARIABLE(bool, is_dummy);
    GT_VARIABLE(bool, active);

    GT_VARIABLE_DECLARE(double, xcoord);
    GT_VARIABLE_GET(xcoord, xcoord);
    public:
    virtual void xcoord (double param) {
	the_old_xcoord = the_xcoord;
	the_xcoord = param;
    }
    GT_VARIABLE(double, old_xcoord);

public:

    DAG_Node ();
    DAG_Node (const DAG_Node& dn);
    virtual ~DAG_Node ();
    
    friend ostream& operator<< (ostream& os, const DAG_Node& dn);
    friend istream& operator>> (istream& is, DAG_Node& dn);
    friend int compare (DAG_Node* const& dn1, DAG_Node* const& dn2);
    double distance (const DAG_Node& dn) const;
};

//
// compare_x
//
// compare function for sorting by x-coordinates
//

struct compare_x : public greater<class DAG_Node *> {
    bool operator()(DAG_Node* dn1, DAG_Node* dn2) {
	return dn1->xcoord() == dn2->xcoord() ? 
	    dn1->old_xcoord() < dn2->old_xcoord() : dn1->xcoord() < dn2->xcoord();
    }
};

// struct compare_x : public binary_function<DAG_Node*, DAG_Node*, bool> {
//     bool operator()(DAG_Node* dn1, DAG_Node* dn2) {
// 	return dn1->xcoord() == dn2->xcoord() ? 
// 	    dn1->old_xcoord() < dn2->old_xcoord() : dn1->xcoord() < dn2->xcoord();
//     }
// };

#endif
