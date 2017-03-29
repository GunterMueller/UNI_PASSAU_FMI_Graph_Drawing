/* This software is distributed under the Lesser General Public License */
#ifndef GT_TCL_ALGORITHM_H
#define GT_TCL_ALGORITHM_H

//
// Algorithm.h
//
// This file defines the class GT_Tcl_Algorithm.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_tcl/Tcl_Algorithm.h,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:46:01 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet Project
//


#include <gt_base/Algorithm.h>

#include "Tcl_Command.h"

class Tcl_Info;

class GT_Tcl_Algorithm_Command : public GT_Tcl_Command {

    GT_COMPLEX_VARIABLE (string, result);

    GT_VARIABLE (bool, reset_before_run);
    GT_VARIABLE (bool, might_change_structure);
    GT_VARIABLE (bool, might_change_coordinates);

public:
    
    GT_Tcl_Algorithm_Command (const string&  name);
    ~GT_Tcl_Algorithm_Command ();

    //
    // Argument parsing
    //
	
    virtual int algorithm_parser (GT_Tcl_info& info, int& index,
	GT_Algorithm& algorithm);
    virtual int parse (GT_Tcl_info& info, int& index,
	GT_Tcl_Graph* g);

    //
    // Utility methods for result management
    //
    
    virtual void result (const GT_Graph& g);
    virtual void result (const GT_Graph& g, const node n);
    virtual void result (const GT_Graph& g, const edge e);

    virtual void result (const GT_Graph& g, const list<node>& nodes);
    virtual void result (const GT_Graph& g, const list<edge>& edges);
    virtual void result (const int i);
    virtual void result (const list<int>& integers);
    virtual void result (const double d);
    virtual void result (const list<double>& doubles);

    virtual void result (const GT_Graph& g,
	const char* name, const node_map<int>& array);
    virtual void result (const GT_Graph& g,
	const char* name, const edge_map<int>& array);
    virtual void result (const GT_Graph& g,
	const char* name, const node_map<double>& array);
    virtual void result (const GT_Graph& g,
	const char* name, const edge_map<double>& array);
    virtual void result (const GT_Graph& g,
	const char* name, const node_map<string>& array);
    virtual void result (const GT_Graph& g,
	const char* name, const edge_map<string>& array);
};



template<class Algorithm>
class GT_Tcl_Algorithm : public GT_Tcl_Algorithm_Command,
			 public Algorithm
{

public:

    //
    // Constructor and Destructor
    //
    GT_Tcl_Algorithm (const string&  name);
    ~GT_Tcl_Algorithm ();
    
    virtual int cmd_parser (GT_Tcl_info& info, int& index);
};


template<class Algorithm>
GT_Tcl_Algorithm<Algorithm>::GT_Tcl_Algorithm (const string& name) :
	GT_Tcl_Algorithm_Command (name),
	Algorithm (name)
{
}


template<class Algorithm>
GT_Tcl_Algorithm<Algorithm>::~GT_Tcl_Algorithm ()
{
}


//
// cmd_parser redefines GT_Tcl_Command::cmd_parser
//

template<class Algorithm>
int GT_Tcl_Algorithm<Algorithm>::cmd_parser (GT_Tcl_info& info, int& index)
{
    return GT_Tcl_Algorithm_Command::algorithm_parser (info, index, *this);
}


#endif
