/* This software is distributed under the Lesser General Public License */
//==========================================================================
//
//   scale.cpp 
//
//==========================================================================
// $Id: Tcl_Scale_Command.cpp,v 1.1 1998/09/15 20:26:37 forster Exp $

#include "Tcl_Scale_Command.h"
#include "Tcl_Graph.h"

GT_Tcl_Scale_Command::GT_Tcl_Scale_Command (const string &name) :
    GT_Tcl_Command (name, graphlet->keymapper.add (name))
{
}

int GT_Tcl_Scale_Command::cmd_parser (GT_Tcl_info& info, int& index)
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

    // cx1
    
    double cx1;
    code = info.parse(index, cx1);
    if(code != TCL_OK)
	return code;
    index++;

    // cy1
    
    double cy1;
    code = info.parse(index, cy1);
    if(code != TCL_OK)
	return code;
    index++;

    // dx1
    
    double dx1;
    code = info.parse(index, dx1);
    if(code != TCL_OK)
	return code;
    index++;

    // dy1
    
    double dy1;
    code = info.parse(index, dy1);
    if(code != TCL_OK)
	return code;
    index++;

    // cx2
    
    double cx2;
    code = info.parse(index, cx2);
    if(code != TCL_OK)
	return code;
    index++;

    // cy2
    
    double cy2;
    code = info.parse(index, cy2);
    if(code != TCL_OK)
	return code;
    index++;

    // dx2
    
    double dx2;
    code = info.parse(index, dx2);
    if(code != TCL_OK)
	return code;
    index++;

    // dy2
    
    double dy2;
    code = info.parse(index, dy2);
    if(code != TCL_OK)
	return code;
    index++;
    
    // invoke command

    return scale(g, nodes, cx1, cy1, dx1, dy1, cx2, cy2, dx2, dy2);
}
    

int GT_Tcl_Scale_Command::scale (GT_Tcl_Graph *g, const list<node> &nodes,
			     double cx1, double cy1, double dx1, double dy1,
			     double cx2, double cy2, double dx2, double dy2)
{
    list<node>::const_iterator it, end;
    for(it = nodes.begin(), end = nodes.end(); it != end; ++it)
    {
	GT_Node_Graphics *gr = g->gt(*it).graphics();

	double x = gr->x();
	double y = gr->y();
	double w = gr->w();
	double h = gr->h();

 	double rel_x = (x-cx1)/dx1;
	double rel_y = (y-cy1)/dy1;

	x = rel_x*dx2 + cx2;
	y = rel_y*dy2 + cy2;

	w = abs(w*dx2/dx1);
 	h = abs(h*dy2/dy1);

	gr->x(x);
	gr->y(y);
	gr->w(w);
	gr->h(h);

	// scale bends of outgoing edges if opposite node is in nodes
	
	edge e;
	forall_out_edges (e, *it)
	{
	    if (find (nodes.begin(), nodes.end(), it->opposite(e)) != nodes.end())
	    {
		GT_Polyline l = g->gt(e).graphics()->line();

		list<GT_Point>::iterator it, end;
		for(it = l.begin(), end = l.end(); it != end; ++it)
		{
		    x = it->x();
		    y = it->y();

		    rel_x = (x-cx1)/dx1;
		    rel_y = (y-cy1)/dy1;

		    x = rel_x*dx2 + cx2;
		    y = rel_y*dy2 + cy2;

		    it->x(x);
		    it->y(y);
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
