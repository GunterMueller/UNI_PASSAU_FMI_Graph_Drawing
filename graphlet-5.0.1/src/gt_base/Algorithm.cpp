/* This software is distributed under the Lesser General Public License */
//
// Algorithm.cpp
//
// This file implements the class GT_Algorithm.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Algorithm.cpp,v $
// $Author: himsolt $
// $Revision: 1.3 $
// $Date: 1999/06/24 11:13:03 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet Project
//

#include "Graphlet.h"
#include "Graph.h"

#include "Algorithm.h"


//
// Constructor and Destructor for class GT_Algorithm
//

GT_Algorithm::GT_Algorithm (const string& name)
{
    the_name = name;
}


GT_Algorithm::~GT_Algorithm ()
{
}
	

void GT_Algorithm::reset ()
{
    // Do nothing;
}


void GT_Algorithm::name (const string& n)
{
    the_name = n;
}



//////////////////////////////////////////
//
// Utilities
//
//////////////////////////////////////////


//
// find_self_loop (g)
//

edge GT_Algorithm::find_self_loop (GT_Graph& g)
{
    edge e;
    forall_edges (e, g.gtl()) {
	if (e.source() == e.target()) {
	    return e;
	}
    }

    return edge();
}


//
//  void GT_Algorithm::adjust_coordinates (GT_Graph& g,
//      double min_x,
//      double min_y) const
//


void GT_Algorithm::adjust_coordinates (GT_Graph& g,
    double min_x,
    double min_y) const
{
    double shift_x = 0;
    double shift_y = 0;
	
    node n;
    forall_nodes (n, g.gtl()) {
		
	double x = g.gt(n).graphics()->x();
	double y = g.gt(n).graphics()->y();
	double w = g.gt(n).graphics()->w();
	double h = g.gt(n).graphics()->h();
	const double width = g.gt(n).graphics()->width();
	
	if (width == 0.0) {
	    w += 1.0;
	    h += 1.0;
	} else {
	    w += width;
	    h += width;
	}
	
	if ((x-w/2) + shift_x < min_x) {
	    shift_x = min_x - (x-w/2);
	}
	if ((y-h/2) + shift_y < min_y) {
	    shift_y = min_y - (y-h/2);
	}

    }

    edge e;
    forall_edges (e, g.gtl()) {

	const GT_Polyline& line = g.gt(e).graphics()->line();
	const double width = g.gt(e).graphics()->width();	
	
	for(GT_Polyline::const_iterator it = line.begin();
	    it != line.end(); ++it)
	{
	    const GT_Point& p = *it;

	    double x = p.x();
	    double y = p.y();
	    if (width == 0.0) {
		x += 1.0;
		y += 1.0;
	    } else {
		x += width;
		y += width;
	    }
	    
	    if (x + shift_x < min_x) {
		shift_x = min_x - x;
	    }
	    if (y + shift_y < min_y) {
		shift_y = min_y - y;
	    }
	}
    }

    if (fabs (shift_x) >= GT_epsilon || fabs(shift_y) >= GT_epsilon) {
	forall_nodes (n, g.gtl()) {
	    g.gt(n).graphics()->move (GT_Point(shift_x, shift_y));
	}
	forall_edges (e, g.gtl()) {
	    g.gt(e).graphics()->move (GT_Point(shift_x, shift_y));
	}
    }
}



//
// static bool remove_all_bends (GT_Graph& g)
//
// Removes all bends in edges of g. Returns true if a bend is
// found, false otherwise.
//


bool GT_Algorithm::remove_all_bends (GT_Graph& g)
{
    bool found_a_bend = false;
    
    edge e;
    forall_edges (e, g.gtl()) {

	if (g.gt(e).graphics()->line().size() > 2) {

	    found_a_bend = true;
	    
	    node s = e.source();
	    node t = e.target();
	
	    list<GT_Point> line;
	    line.push_back (g.gt(s).graphics()->center());
	    line.push_back (g.gt(t).graphics()->center());
	
	    g.gt(e).graphics()->line (line);
	}
    }

    return found_a_bend;
}



//
// GT_No_Algorithm
//


int GT_No_Algorithm::run (GT_Graph& /* g */)
{
    return GT_OK; 
}


int GT_No_Algorithm::check (GT_Graph& /* g */, string& message)
{
    message = "Dont worry, be happy";
    return GT_OK;
}
