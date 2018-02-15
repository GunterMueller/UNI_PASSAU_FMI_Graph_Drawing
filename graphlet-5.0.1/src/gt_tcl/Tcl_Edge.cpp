/* This software is distributed under the Lesser General Public License */
// ---------------------------------------------------------------------
// Tcl_Edge.cc
// 
// Memberfunctions for the GT_TclGraph class.
// ---------------------------------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_tcl/Tcl_Edge.cpp,v $
// $Author: himsolt $
// $Revision: 1.4 $
// $Date: 1999/06/24 11:10:33 $
// $Locker:  $
// $State: Exp $
// ----------------------------------------------------------------------
//
// (C) University of Passau 1995-1999, Graphlet Project
//

// Font stuff (tk.h must be included first because it uses the name list)
#include <tk.h>

#include "Tcl_Graph.h"

#include "Tcl_Edge.h"

#include <gt_base/Id.h>
#include "Graphscript.h"
#include "Tk_UIEdge.h"
#include "Tk_UILabel.h"

// Walter
#include <gt_base/NEI.h>

//////////////////////////////////////////
//
// GT_Tcl_Graph::create_edge_cmd
// GT_Tcl_Graph::new_edge_cmd
//
//////////////////////////////////////////


int GT_Tcl_Graph::create_edge_cmd (GT_Tcl_info& info,
    int argc)
{
    const char* usage =
	"*graph* create edge ?source? source ?target? target\n";
    int code = TCL_ERROR;
	
    if (info.args_left (argc) == 3 || info.args_left (argc) == 1) {

	bool short_arguments = false;
	if (info.args_left (argc) == 3) {
	    short_arguments = false;
	} else {
	    short_arguments = true;
	}
	
	//
	// Check syntax
	//
		
	if (!short_arguments) {
	    
	    if (!GT::streq (info[argc + 0], "source") ||
		!GT::streq (info[argc + 2], "target")) {

		info.msg (usage);
		return TCL_ERROR;
	    }
	}

	//
	// Find source and target
	//

	int source_index = short_arguments ? (argc+0) : (argc+1);
	node source;
	code = info.parse (info[source_index], this, source);
	if (code != TCL_OK) {
	    return code;
	}

	int target_index = short_arguments ? (argc+1) : (argc+3);
	node target;
	code = info.parse (info[target_index], this, target);
	if (code != TCL_OK) {
	    return code;
	}

	code = run_hooks (pre_new_edge_hook, source, target);
	if (code == TCL_ERROR) {
	    return code;
	}

	edge e = leda().new_edge (source, target);

	//
	// Note: line assignment in gt is
	// preliminary -- MH
	//

	GT_Edge_Attributes& gt_attrs = gt(e);

// 	gt_attrs.source (source);
// 	gt_attrs.target (target);
		
	GT_Polyline points;
	points.push_back (gt(source).graphics()->center());
	points.push_back (gt(target).graphics()->center());
	gt_attrs.graphics()->line (points);
			
	//
	// return the id of the edge.
	//

	code = run_hooks (post_new_edge_hook, e);
	if (code == TCL_ERROR) {
	    return code;
	} else {
	    string s = GT::format("GT:%d", gt_attrs.id());
	    info.msg (s);
	}

	return code;
		
    } else {
	info.msg (graphlet->error.msg (GT_Error::wrong_number_of_args));
	return TCL_ERROR;
    }
}



//////////////////////////////////////////
//
// Copy Edge
//
//////////////////////////////////////////

int GT_Tcl_Graph::copy_edge_cmd (GT_Tcl_info& info, int argc)
{
    //
    // Get the edge
    //

    if (info.args_left (argc) >= 2) { // edge source target ?graph?
	
	edge old_edge;
	int code = info.parse (info[argc], this, old_edge);
	if (code != TCL_OK) {
	    return code;
	};
	argc ++;
	
	GT_Tcl_Graph* into;
	if (info.exists (argc+2)) {
	    code = info.parse (info[argc+2], into);
	    if (code == TCL_ERROR) {
		return code;
	    }
	} else {
	    into = this;
	}
	
	node new_source;
	code = info.parse (info[argc], into, new_source);
	if (code != TCL_OK) {
	    return code;
	};
	argc ++;
	
	node new_target;
	code = info.parse (info[argc], into, new_target);
	if (code != TCL_OK) {
	    return code;
	};
	argc ++;
	
	// Check for more parameters. The parameter at argc is
	// the (optional) target graph and has already been
	// examined.
	
	if (info.exists (argc+1)) { // Enough is enough
	    info.msg (GT_Error::wrong_number_of_args);
	    return TCL_ERROR;
	}
	
	code = run_hooks (pre_copy_edge_hook, old_edge);
	if (code == TCL_ERROR) {
	    return code;
	}

	edge copied = copy (old_edge, new_source, new_target, *into);

	code = into->run_hooks (post_copy_edge_hook, copied);
	if (code == TCL_ERROR) {
	    return code;
	} else {	    
	    info.msg (GT_Tcl::gt(*into, copied));	    
	    return TCL_OK;
	}

    } else {
	info.msg (GT_Error::wrong_number_of_args);
	return TCL_ERROR;
    }
}



//////////////////////////////////////////
//
// GT_Tcl_Graph::draw (e)
//
//////////////////////////////////////////


int GT_Tcl_Graph::draw (edge e, bool force)
{
    return baseclass::draw (e, force);
}



//
// draw customization
//


GT_UIObject* GT_Tcl_Graph::new_uiobject (GT_Device* device,
    GT_Graph& g, edge e)
{
    return new GT_Tk_UIEdge ((GT_Tk_Device*)device, g, e);
}


GT_UIObject* GT_Tcl_Graph::new_uiobject_label (GT_Device* device,
    GT_Graph& g, edge e)
{
    return new GT_Tk_UIEdgelabel ((GT_Tk_Device*)device, g, e);
};


//////////////////////////////////////////
//
// Draw utilities
//
//////////////////////////////////////////


void GT_Tcl_Graph::update (edge e)
{
    GT_Graph::update(e);
}


void GT_Tcl_Graph::update_coordinates (edge e)
{
    baseclass::update_coordinates (e);
}


void GT_Tcl_Graph::update_label (edge e)
{
    baseclass::update_label (e);

    GT_Edge_Attributes& attrs = gt(e);
    GT_Common_Graphics& graphics = *(attrs.graphics());
    GT_Common_Graphics& label_graphics = *(attrs.label_graphics());
    
    //
    // Adjust size to label
    //

    if (attrs.is_changed (GT_Common_Attributes::tag_label) ||
	label_graphics.is_changed (GT_Common_Graphics::tag_font) ||
	label_graphics.is_changed (GT_Common_Graphics::tag_font_size) ||
	label_graphics.is_changed (GT_Common_Graphics::tag_font_style)) {

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

	} else {
	    label_graphics.w (0.0);
	    label_graphics.h (0.0);	    
	}

    }
}


void GT_Tcl_Graph::update_label_coordinates (edge e)
{
    baseclass::update_label_coordinates (e);
}
