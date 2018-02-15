/* This software is distributed under the Lesser General Public License */
//==========================================================================
//
//   rotate.cpp 
//
//==========================================================================
// $Id: Tcl_Rotate_Command.cpp,v 1.1 1998/09/15 20:26:36 forster Exp $

#include "Tcl_Rotate_Command.h"
#include "Tcl_Graph.h"

GT_Tcl_Rotate_Command::GT_Tcl_Rotate_Command (const string &name) :
    GT_Tcl_Command (name, graphlet->keymapper.add (name))
{
}

int GT_Tcl_Rotate_Command::cmd_parser (GT_Tcl_info& info, int& index)
{
    int code;

    // Graph

    GT_Tcl_Graph *g;
    code = info.parse(index, g);
    if(code != TCL_OK)
	return code;
    index++;

    // Nodes
    
    list<node> nodes;
    code = info.parse(index, g, nodes);
    if(code != TCL_OK)
	return code;
    index++;

    // Center.x

    double center_x;
    code = info.parse(index, center_x);
    if(code != TCL_OK)
	return code;
    index++;

    // Center.y

    double center_y;
    code = info.parse(index, center_y);
    if(code != TCL_OK)
	return code;
    index++;

    // Angle

    double angle;
    code = info.parse(index, angle);
    if(code != TCL_OK)
	return code;
    index++;

    // invoke command

    return rotate(g, nodes, GT_Point(center_x, center_y), angle);
}

int GT_Tcl_Rotate_Command::rotate (GT_Tcl_Graph *g,
				   const list<node> &nodes,
				   const GT_Point &center,
				   double angle)
{
    list<node>::const_iterator it, end;
    for(it = nodes.begin(), end = nodes.end(); it != end; ++it)
    {
	GT_Node_Graphics *gr = g->gt(*it).graphics();
	GT_Point p = GT_Point(gr->x(), gr->y());

	p.rotate(center, angle);

	gr->x(p.x());
	gr->y(p.y());

	// rotate bends of outgoing edges if opposite node is in nodes
	
	edge e;
	forall_out_edges (e, *it)
	{
	    if (find (nodes.begin(), nodes.end(), it->opposite(e)) != nodes.end())
	    {
		GT_Polyline l = g->gt(e).graphics()->line();

		list<GT_Point>::iterator it, end;
		for(it = l.begin(), end = l.end(); it != end; ++it)
		{
		    it->rotate(center, angle);
		}
		
		g->gt(e).graphics()->line(l);
	    }
	}
    }

    
    return TCL_OK;
}

//--------------------------------------------------------------------------
//   end of file
//--------------------------------------------------------------------------
