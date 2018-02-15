/* This software is distributed under the Lesser General Public License */
//
// cfr_algorithm.h
//
// This defines the class
// GT_Layout_Constraint_Fruchterman_Reingold_Algorithm
// which is the interface to the Constraint Fruchterman
// Reingold Layout Algorithm.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_cfr_layout/cfr_algorithm.h,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:45:21 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, Graphlet Project
//

#ifndef CFR_ALGORITHM_H
#define CFR_ALGORITHM_H

#include "cfr_layout.h"

class GT_Layout_Constraint_Fruchterman_Reingold_Algorithm :
    public GT_Algorithm
{
    GT_CLASS (GT_Layout_Constraint_Fruchterman_Reingold_Algorithm,
	GT_Algorithm);
	
    GT_VARIABLE (int, animation);
    GT_VARIABLE (bool, colour_the_nodes);
    GT_VARIABLE (bool, constraint_forces);
    GT_VARIABLE (string, delimiter);
    GT_VARIABLE (double, minimal_distance);
    GT_VARIABLE (double, minimal_force);
    GT_VARIABLE (bool, new_bends);
    GT_VARIABLE (double, optimal_distance);
    GT_VARIABLE (double, phase1_damping);
    GT_VARIABLE (double, phase2_damping);
    GT_VARIABLE (double, phase3_damping);
    GT_VARIABLE (int, phase1_max_iteration);
    GT_VARIABLE (int, phase2_max_iteration);
    GT_VARIABLE (int, phase3_max_iteration);
    GT_VARIABLE (bool, random_placement);
    GT_VARIABLE (bool, respect_sizes);
    GT_VARIABLE (double, vibration_ratio);
    GT_VARIABLE (int, window_width);
    GT_VARIABLE (int, window_height);
    GT_VARIABLE (double, xoffset);
    GT_VARIABLE (double, yoffset);
	
    GT_VARIABLE (GT_Tcl_Graph*, tcl_graph);
    GT_VARIABLE (Tcl_Interp*, tcl_interp);
	
public:
    GT_Layout_Constraint_Fruchterman_Reingold_Algorithm(const string& name);
    virtual ~GT_Layout_Constraint_Fruchterman_Reingold_Algorithm() {};
    virtual int run (GT_Graph& g);
    virtual int check (GT_Graph& g, string& message);
    void set_cfr_parameters(FR_Constraint_Graph& fr_constraint_graph);
};


class GT_Tcl_Layout_Constraint_Fruchterman_Reingold_Algorithm :
    public GT_Tcl_Algorithm<GT_Layout_Constraint_Fruchterman_Reingold_Algorithm>
{
public:
    GT_Tcl_Layout_Constraint_Fruchterman_Reingold_Algorithm(
	const string& name);
    virtual ~GT_Tcl_Layout_Constraint_Fruchterman_Reingold_Algorithm() {}

    bool get_boolean(Tcl_Interp* interp, char* argv, int& code);
    double get_double(Tcl_Interp* interp, char* argv, int& code);
    int get_int(Tcl_Interp* interp, char* argv, int& code);
	
    virtual int parse (GT_Tcl_info& info, int& index, GT_Tcl_Graph* g);
    virtual int run(GT_Graph &g);
};

#endif



