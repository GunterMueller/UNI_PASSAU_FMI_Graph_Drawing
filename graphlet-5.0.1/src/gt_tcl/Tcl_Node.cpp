/* This software is distributed under the Lesser General Public License */
// ---------------------------------------------------------------------
// Tcl_Node.cc
// 
// This module handles the node-functions of the GT_Tcl_Graph - class.
// ---------------------------------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_tcl/Tcl_Node.cpp,v $
// $Author: himsolt $
// $Revision: 1.8 $
// $Date: 1999/03/05 20:46:24 $
// $Locker:  $
// $State: Exp $
// ----------------------------------------------------------------------
//
// (C) University of Passau 1995-1999, Graphlet Project
//

// Font stuff (tk.h must be included first because it uses the name list)
#include <tk.h>

#include "Tcl_Graph.h"

#include "Tcl_Node.h"

#include <gt_base/Id.h>
#include "Graphscript.h"

#include "Tk_UINode.h"
#include "Tk_UILabel.h"

// Walter
#include <gt_base/NEI.h>



//////////////////////////////////////////
//
// GT_Tcl_Graph::create_node_cmd
//
//////////////////////////////////////////


int GT_Tcl_Graph::create_node_cmd (
    GT_Tcl_info& info,
    int /* argc */)
{    
    int code = run_hooks (pre_new_node_hook);
    if (code == TCL_ERROR) {
	return code;
    }

    node n = leda().new_node ();

    code = run_hooks (post_new_node_hook, n);
    if (code == TCL_ERROR) {
	return code;
    } else {
	string s = GT::format("GT:%d", gt(n).id());
	info.msg (s);
    }

    return code;
}



//////////////////////////////////////////
//
// Copy Node
//
//////////////////////////////////////////



int GT_Tcl_Graph::copy_node_cmd (GT_Tcl_info& info, int argc)
{
    //
    // Usage: $graph copynode node ?into? ?center_x center_y?
    //
    // Shortcuts for into: this, here
    //

    if (info.exists (argc)) {

	// Move to center is true iff center_x, center_y are given
	bool move_to_center = false;
	
	// nodes
	
	list<node> nodes;
	int code = info.parse (argc, this, nodes);
	if (code != TCL_OK) {
	    return code;
	}
	argc ++;
	
	// into (optional)
	
	GT_Tcl_Graph* into;
	if (info.exists (argc)) {
	    if (GT::streq (info[argc],"this") ||
		GT::streq (info[argc],"here")) {
		argc++;
		into = this;
	    } else {
		code = info.parse (argc, into);
		if (code == TCL_ERROR) {
		    return code;
		}
		argc ++;
	    }
	} else {
	    into = this;
	}

	// Coordinates (optional)
	
	double center_x = 0;
	double center_y = 0;
	if (info.args_left (argc, 1)) {
	    code = info.parse (argc, center_x);
	    if (code != TCL_OK) {
		return code;
	    }
	    argc ++;
	    code = info.parse (argc, center_y);
	    if (code != TCL_OK) {
		return code;
	    }
	    argc ++;
	    move_to_center = true;
	}
	
	if (info.exists (argc)) { // Enough is enough
	    info.msg (GT_Error::wrong_number_of_args);
	    return TCL_ERROR;
	}
	
	// edges is the list of copied edges
	list<edge> edges;
	node n;
	edge e;
	list<node>::const_iterator it_n;
	list<node>::const_iterator it_n_end = nodes.end();
	for (it_n = nodes.begin(); it_n != it_n_end; ++it_n)
	{
	    n = *it_n;
	    node::inout_edges_iterator it_e;
	    node::inout_edges_iterator it_e_end = n.inout_edges_end();
	    for (it_e = n.inout_edges_begin(); it_e != it_e_end; ++it_e)
	    {
		e = *it_e;
		node opposite = n.opposite(e);
		if ( (find(nodes.begin(), nodes.end(), opposite) != nodes.end())
		    && (n == e.source()) )
		{
		    edges.push_back (e);
		}

//		node opposite = leda().opposite (n,e);
//		if (nodes.search (opposite) != 0 && n == leda().source(e)) {
//		    edges.append (e);
	    }
	}

	// Copy operation
	list<node> copied_nodes;
	list<edge> copied_edges;

	if ( (!nodes.empty()) || (!edges.empty()) ) {

	    code = run_hooks (pre_copy_node_hook, nodes, edges);
	    if (code == TCL_ERROR) {
		return code;
	    }

	    if (!edges.empty()) {
		code = run_hooks (pre_copy_edge_hook, edges);
		if (code == TCL_ERROR) {
		    return code;
		}
	    }
	
	    node_map<node> copied (leda());
	    
	    // iterator already defined;
	    it_n_end = nodes.end();
	    for (it_n = nodes.begin(); it_n != it_n_end; ++it_n)
	    {
		n = *it_n;
		copied[n] = copy (n, *into);
		copied_nodes.push_back (copied[n]);
	    }

	    list<edge>::const_iterator it_e;
	    list<edge>::const_iterator it_e_end = edges.end();
	    for (it_e = edges.begin(); it_e != it_e_end; ++it_e)
	    {
		e = *it_e;
		node new_source = copied[e.source()];
		node new_target = copied[e.target()];
		edge copied = copy (e, new_source, new_target, *into);
		copied_edges.push_back (copied);
	    }

	    if (move_to_center &&
		( (!copied_nodes.empty()) || (!copied_edges.empty())) ) {
		
		GT_Rectangle nodes_edges_bbox;
		
		GT_Rectangle nodes_bbox;
		if (!copied_nodes.empty()) {
		    into->bbox (copied_nodes, nodes_bbox, false);
		}
		
		GT_Rectangle edges_bbox;
		if (!copied_edges.empty()) {
		    into->bbox (copied_edges, edges_bbox, false);
		}

		if (!copied_nodes.empty()) {
		    nodes_edges_bbox = nodes_bbox;
		    if (!copied_edges.empty()) {
			nodes_edges_bbox.union_with (edges_bbox);
		    }
		} else if (!copied_edges.empty()) {
		    nodes_edges_bbox = edges_bbox;
		}
		
		double move_x = center_x - nodes_edges_bbox.x();
		double move_y = center_y - nodes_edges_bbox.y();
	    
		into->baseclass::move_nodes (copied_nodes, move_x, move_y);
		node n;
		edge e;

		list<node>::const_iterator it_n;
		list<node>::const_iterator it_n_end = copied_nodes.end();
		for (it_n = copied_nodes.begin(); it_n != it_n_end; ++it_n)
		{
		    n = *it_n;
		    node::adj_edges_iterator it_e;
		    node::adj_edges_iterator it_e_end = n.adj_edges_end();
		    for (it_e = n.adj_edges_begin(); it_e != it_e_end; ++it_e)
		    {
			e = *it_e;
			node opposite = n.opposite(e);
			if ( (find(copied_nodes.begin(), copied_nodes.end(),
			    opposite) != copied_nodes.end()) &&
			    n == e.source())
			{
			    into->move_edge (e, move_x, move_y);
			}

//			node opposite = into->leda().opposite (n,e);
//			if (copied_nodes.search (opposite) != 0 &&
//			    n == into->leda().source(e)) {
//			    // Both endnodes are in the list
//			    into->move_edge (e, move_x, move_y);
		    }
		}
	    }

	    code = into->run_hooks (post_copy_node_hook, copied_nodes);
	    if (code == TCL_ERROR) {
		return code;
	    }

	    if (!copied_edges.empty()) {
		code =into->run_hooks (post_copy_edge_hook, copied_edges);
		if (code == TCL_ERROR) {
		    return code;
		}
	    }
	}

	list<string> tcl_result;
	tcl_result.push_back (GT_Tcl::tcl (*into, copied_nodes));
	tcl_result.push_back (GT_Tcl::tcl (*into, copied_edges));
	info.msg (GT_Tcl::merge (tcl_result));
	
	return TCL_OK;

    } else {
	info.msg (GT_Error::wrong_number_of_args);
	return TCL_ERROR;
    }
}



//////////////////////////////////////////
//
// GT_Tcl_Graph::cmd
//
//////////////////////////////////////////


int GT_Tcl_Graph::draw (node n, bool force)
{
    return baseclass::draw (n, force);
}



//
// draw customization
//


GT_UIObject* GT_Tcl_Graph::new_uiobject (GT_Device* device,
    GT_Graph& g, node n)
{
    return new GT_Tk_UINode ((GT_Tk_Device*)device, g, n);
}


GT_UIObject* GT_Tcl_Graph::new_uiobject_label (GT_Device* device,
    GT_Graph& g, node n)
{
    return new GT_Tk_UINodelabel ((GT_Tk_Device*)device, g, n);
}



//
// Draw utilities
//


void GT_Tcl_Graph::update (node n)
{
    baseclass::update (n);
}


void GT_Tcl_Graph::update_coordinates (node n)
{
    baseclass::update_coordinates (n);
}


void GT_Tcl_Graph::update_label (node n)
{
    baseclass::update_label (n);

    GT_Node_Attributes& attrs = gt(n);
    GT_Common_Graphics& graphics = *(attrs.graphics());
    GT_Common_Graphics& label_graphics = *(attrs.label_graphics());
    
    //
    // Adjust size to label
    //

    if (
	(attrs.is_changed (GT_Common_Attributes::tag_label) ||
	    label_graphics.is_changed (GT_Common_Graphics::tag_font) ||
	    label_graphics.is_changed (GT_Common_Graphics::tag_font_size) ||
	    label_graphics.is_changed (GT_Common_Graphics::tag_font_style))
	&&
	(!attrs.label_anchor().active() ||
	    attrs.label_anchor() == GT_Keys::anchor_center)
	) {

	if (attrs.label().length() > 0 && (!the_devices.empty()) ) {

	    Tcl_Interp* interp = the_graphscript->interp();
	    GT_Device* device = the_devices.front();

	    const char* font_name = label_graphics.font().active() ?
		label_graphics.font().name().c_str() :
		the_graphscript->option_default_font();
	    const char* font_style = label_graphics.font_style().active() ?
		label_graphics.font_style().name().c_str() :
		the_graphscript->option_default_font_style();
	    int font_size = label_graphics.font_size() > 0 ?
		label_graphics.font_size() :
		the_graphscript->option_default_font_size();
	    int int_font_size = int(device->translate_y (font_size));
	    if (int_font_size == 0) {
		int_font_size = 1;
	    }
	    
	    string font_cmd;
	    font_cmd = GT::format ("{%s} %d {%s}",
		font_name,
		int_font_size,
		font_style);
	    
	    Tk_Window tkwin;
	    tkwin = Tk_NameToWindow (interp,
		const_cast<char*>(device->name().c_str()),
		Tk_MainWindow (interp));
	
	    Tk_Font tkfont;
	    tkfont = Tk_GetFont (the_graphscript->interp(),
		tkwin,
		font_cmd.c_str());
	    
	    Tk_TextLayout text_layout;
	    int width;
	    int height;
	    
	    text_layout = Tk_ComputeTextLayout (
		tkfont,                  // tkfont,
		attrs.label().c_str(), // string
		attrs.label().length(),  // numChars
		0,                       // wrapLength
		TK_JUSTIFY_LEFT,         // justify,
		0,                       // flags,
		&width,
		&height);
	    
	    Tk_FreeTextLayout (text_layout);
	    Tk_FreeFont (tkfont);
	    
	    label_graphics.w (device->translate_x_reverse(width));
	    label_graphics.h (device->translate_y_reverse(height));
	    // label_graphics.reset_changed (GT_Common_Graphics::tag_geometry);

	    if (the_graphscript->option_adjust_size_to_label()) {
		graphics.w (device->translate_x_reverse (
		    width +
		    the_graphscript->option_adjust_size_to_label_gap_x()));
		graphics.h (device->translate_y_reverse (
		    height +
		    the_graphscript->option_adjust_size_to_label_gap_y()));
	    }

	} else {
	    label_graphics.w (0.0);
	    label_graphics.h (0.0);

	    if (the_graphscript->option_adjust_size_to_label()) {
// 		graphics.w (16.0);
// 		graphics.h (16.0);
	    }
	    
	}

    }
}


void GT_Tcl_Graph::update_label_coordinates (node n)
{
    baseclass::update_label_coordinates (n);
}



//////////////////////////////////////////
//
// GT_Tcl_Graph::nodemove_cmd
// GT_Tcl_Graph::move_node
//
//////////////////////////////////////////


int GT_Tcl_Graph::nodemove_cmd (
    GT_Tcl_info& info,
    int argc)
{
    int code;
    
    GT_Device* fast = 0;
    if (info.exists (argc+1) && GT::streq (info[argc], "-fast")) {

	argc ++;

	GT_Device* d;
	list<GT_Device*>::const_iterator it;
	list<GT_Device*>::const_iterator end = the_devices.end();
	for (it = the_devices.begin(); it != end; ++it)
	{
	    d = *it;
	    if (GT::streq (d->name().c_str(), info[argc])) {
		fast = d;
		break;
	    }
	}

	if (fast != 0) {
	    argc ++;
	} else {
	    info.msg (GT::format("Cannot find device %s", info[argc]));
	    return TCL_ERROR;
	}
    }
    
    if (info.args_left (argc, 2)) {

	//
	// Get the x and y values.
	//

	double x_diff;
	double y_diff;

	code = Tcl_GetDouble (info.interp(),
	    info[argc + 1],
	    &x_diff);

	if (code != TCL_OK) {
	    info.msg (GT_Error::wrong_double_val, info[argc+1]);
	    return code;
	}
	
	code = Tcl_GetDouble (info.interp(),
	    info[argc+2],
	    &y_diff);
	
	if (code != TCL_OK) {
	    info.msg (GT_Error::wrong_double_val,
		info[argc+2]);
	    return code;
	}
	
	//
	// Move each node in the listArgv
	//

	list<node> nodes;
	code = info.parse (argc, this, nodes);
	if (code != TCL_OK) {
	    return code;
	}
	argc ++;

	code = run_hooks (pre_draw_hook, nodes);
	if (code != TCL_OK) {
	    return code;
	}

	code = move_nodes (nodes, x_diff, y_diff, fast);
	if (code != TCL_OK) {
	    return code;
	}

	code = run_hooks (post_draw_hook, nodes);
	if (code != TCL_OK) {
	    return code;
	}

	return TCL_OK;

    } else {

	info.msg (GT_Error::wrong_number_of_args);
	return TCL_ERROR;

    }
	
    return TCL_OK;
}



int GT_Tcl_Graph::move_nodes (const list<node>& nodes,
    const double move_x,
    const double move_y,
    GT_Device* fast)
{
    int code = TCL_OK;

    node n;
    list<node>::const_iterator it_n;
    list<node>::const_iterator end_n = nodes.end();
    for (it_n = nodes.begin(); it_n != end_n; ++it_n)
    {
	n = *it_n;

	//
	// move the UIObjects associated with the nodes
	//
	
	const int node_uid = gt(n).graphics()->uid();
	
	GT_Device* device;
	list<GT_Device*>::const_iterator it_d;
	list<GT_Device*>::const_iterator end_d = the_devices.end();
	for (it_d = the_devices.begin(); it_d != end_d; ++it_d)
	{
	    device = *it_d;
	    if (device != fast) {
		GT_UIObject* uiobject = device->get (node_uid);
		if (uiobject != 0) {
		    if (uiobject->move (move_x, move_y) == false) {
			return TCL_ERROR;
		    }
		}
	    }
	}
	
	//
	// Change coordinates
	//
	
	baseclass::move_node (n, move_x, move_y);

	//
	// HERE comes the trick:
	// Since the position is already updated on the screen,
	// reset the change
	//
	
	gt(n).graphics()->reset_changed (GT_Common_Graphics::tag_geometry);
	gt(n).graphics()->old_center (gt(n).graphics()->center());
	
    }

    //
    // Update coordinates for the edges and draw them.
    //

    edge e;

    node::adj_edges_iterator it_e;
    node::adj_edges_iterator end_e;

    for (it_n = nodes.begin(); it_n != end_n; ++it_n)
    {
	n = *it_n;
	end_e = n.adj_edges_end();

	for (it_e = n.adj_edges_begin(); it_e != end_e; ++it_e)
	{
	    e = *it_e;
	    node opposite = n.opposite(e);
	    if ( (find(nodes.begin(), nodes.end(), opposite) != nodes.end()) 
		&& n == e.source())
	    {
		move_edge (e, move_x, move_y, fast);
	    }


//	    node opposite = leda().opposite (n,e);
//	    if (nodes.search (opposite) != 0 && n == leda().source(e)) {		
//		// Both endnodes are in the list
//
//		move_edge (e, move_x, move_y, fast);
	}
    }

    for (it_n = nodes.begin(); it_n != end_n; ++it_n)
    {
	n = *it_n;
	node::inout_edges_iterator it_e;
	node::inout_edges_iterator end_e = n.inout_edges_end();
	for (it_e = n.inout_edges_begin(); it_e != end_e; ++it_e)
	{
	    e = *it_e;

	    node opposite = n.opposite(e);

	    if ( find(nodes.begin(), nodes.end(), opposite) == nodes.end())
	    {
		// Only one endnode is in the list

		// Fake a geometry change
		gt(e).graphics()->set_changed (
		    GT_Common_Graphics::tag_geometry);

		code = draw (e);
		if (code == TCL_ERROR) {
		    return code;
		}
		
		code = update_marks (e);
		if (code == TCL_ERROR) {
		    return code;
		}
	    }

//	    node opposite = leda().opposite (n,e);
//	    if (nodes.search (opposite) == 0) {
	}
    }

    return code;
}



int GT_Tcl_Graph::move_edge (edge e,
    const double move_x,
    const double move_y,
    GT_Device* fast)
{

    //
    // move the UIObjects associated with the edges
    //
    
    const int edge_uid = gt(e).graphics()->uid();
    
    GT_Device* device;
    list<GT_Device*>::const_iterator it;
    list<GT_Device*>::const_iterator end = the_devices.end();
    for (it = the_devices.begin(); it != end; ++it)
    {
	device = *it;
	if (device != fast) {
	    GT_UIObject* uiobject = device->get (edge_uid);
	    if (uiobject != 0) {
		if (uiobject->move (move_x, move_y) == false) {
		    return TCL_ERROR;
		}
	    }
	}
    }

    //
    // This uses the same trick as above: now adjust the coordinates
    // and tell nobody what we have done ...
    //
    
    baseclass::move_edge (e, move_x, move_y, fast);
    gt(e).graphics()->reset_changed (GT_Common_Graphics::tag_geometry);
    gt(e).graphics()->old_center (gt(e).graphics()->center());

    return TCL_OK;
}
