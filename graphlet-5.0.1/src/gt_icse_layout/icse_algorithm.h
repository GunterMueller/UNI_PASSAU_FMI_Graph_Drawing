/* This software is distributed under the Lesser General Public License */
//
// icse_algorithm.h
//
// This defines the class
// GT_Iterative_Constraint_Spring_Embedder_Algorithm
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_icse_layout/icse_algorithm.h,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:45:37 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, Graphlet Project
//

#ifndef ICSE_ALGORITHM_H
#define ICSE_ALGORITHM_H

//
// As the whole ICSE algorithm the interface of ICSE depends
// heavily on CFR. Parameter parsing is done there also.
//

#include "gt_cfr_layout/cfr_algorithm.h"

class GT_Iterative_Constraint_Spring_Embedder_Algorithm :
public GT_Layout_Constraint_Fruchterman_Reingold_Algorithm
{
  public:
    GT_Iterative_Constraint_Spring_Embedder_Algorithm(const string& name);
    virtual ~GT_Iterative_Constraint_Spring_Embedder_Algorithm() {};
    virtual int run (GT_Graph& g);
    virtual int check (GT_Graph& g, string& message);
};


class GT_Tcl_Iterative_Constraint_Spring_Embedder_Algorithm :
public GT_Tcl_Layout_Constraint_Fruchterman_Reingold_Algorithm
{
  public:
    GT_Tcl_Iterative_Constraint_Spring_Embedder_Algorithm(
	const string& name);
    virtual ~GT_Tcl_Iterative_Constraint_Spring_Embedder_Algorithm() {}
    virtual int parse (GT_Tcl_info& info, int& index, GT_Tcl_Graph* g);
    virtual int run(GT_Graph &g);
};

#endif



