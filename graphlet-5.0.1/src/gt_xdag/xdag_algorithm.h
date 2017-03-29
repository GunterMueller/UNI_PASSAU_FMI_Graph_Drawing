/* This software is distributed under the Lesser General Public License */
//
// xdag_algorithm.h
//
// This file defines the two classes GT_Extended_DAG_Algorithm and
// GT_Tcl_Extended_DAG_Algorithm which implement the interface
// between C++ and Tcl for the extended dag algorithm.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_xdag/xdag_algorithm.h,v $
// $Author: himsolt $
// $Revision: 1.1.1.1 $
// $Date: 1998/08/27 17:19:31 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1997, Graphlet Project
//
//     Author: Harald Mader (mader@fmi.uni-passau.de)
//


#ifndef EXTENDED_DAG_ALGORITHM_H
#define EXTENDED_DAG_ALGORITHM_H


// ========================================
//
// class GT_Extended_DAG_Algorithm
//
// ========================================

class GT_Extended_DAG_Algorithm : public GT_Algorithm
{

    GT_CLASS (GT_Extended_DAG_Algorithm, GT_Algorithm);
    
    GT_VARIABLE (int, min_node_node_distance);
    GT_VARIABLE (int, min_node_edge_distance);
    GT_VARIABLE (int, min_edge_edge_distance);
    GT_VARIABLE (int, default_edge_length);
    GT_VARIABLE (int, animation);
    GT_VARIABLE (int, iterations_crossing_reduction);
    GT_VARIABLE (int, last_phase_crossing_reduction);
    GT_VARIABLE (int, iterations_node_positioning);
    GT_VARIABLE (int, last_phase_node_positioning);

    // the following two options are only temporary
    GT_VARIABLE (int, protocol);
    GT_VARIABLE (int, stepping);

public:

    GT_Extended_DAG_Algorithm (const string& name);
    virtual ~GT_Extended_DAG_Algorithm ();

    virtual int run (GT_Graph& g);
    virtual int check (GT_Graph& g, string& message);

};


// ========================================
//
// class GT_Tcl_Extended_DAG_Algorithm
//
// ========================================

class GT_Tcl_Extended_DAG_Algorithm :
    public GT_Tcl_Algorithm<GT_Extended_DAG_Algorithm> 
{
    
public:

    GT_Tcl_Extended_DAG_Algorithm (const string& name);
    virtual ~GT_Tcl_Extended_DAG_Algorithm ();

    virtual int parse (GT_Tcl_info& info, int& index, GT_Tcl_Graph* g);

    // The following two methods need not be defined.
    // However, if they are, they override the according
    // methods in the class GT_Extended_DAG_Algorithm,
    // i.e. they are executed instead the above ones.

    virtual int run (GT_Graph &g);
    // virtual int check (GT_Graph& g, string& message);
};


#endif
