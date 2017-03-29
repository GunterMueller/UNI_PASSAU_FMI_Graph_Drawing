/* This software is distributed under the Lesser General Public License */
//
// GT_Tcl_Graph_configure.cpp
// 
// This module implements the class GT_Tcl_Graph
//
// $Source: /home/br/CVS/graphlet/src/gt_tcl/Tcl_Graph_configure.cpp,v $
// $Author: himsolt $
// $Revision: 1.9 $
// $Date: 1999/06/08 13:53:06 $
// $Locker:  $
// $State: Exp $
// ----------------------------------------------------------------------
//
// (C) University of Passau 1995-1999, Graphlet Project
//

#include "Tcl_Graph.h"
#include <gt_base/NEI.h>
#include <gt_base/Key_description.h>

#include <gt_base/Attribute_int.h>
#include <gt_base/Attribute_double.h>
#include <gt_base/Attribute_string.h>
#include <gt_base/Attribute_list.h>

#include <tk.h>

//////////////////////////////////////////
//
// GT_Tcl_Graph::configure_cmd
//
//////////////////////////////////////////



typedef enum {
    spec_plain, spec_graphics, spec_label_graphics
} Spec;


static bool is_attr (const char* s)
{
    return (s != 0) && (s[0] == '-' || s[0] == '.');
}


static bool is_spec (const char* s, Spec& spec)
{
    if (s != 0) {
	if (GT::streq (s, "plain")) {
	    spec = spec_plain;
	    return true;
	} else if (GT::streq (s, "graphics")) {
	    spec = spec_graphics;
	    return true;
	} else if (GT::streq (s, "label_graphics")) {
	    spec = spec_label_graphics;
	    return true;
	} else {
	    return false;
	}
    } else {
	return false;
    }
}


inline bool get_attribute (GT_Key key, GT_Key option, int code, bool& found)
{
    if (code == TCL_OK && (key == option || key == GT_Keys::empty)) {
	found = true;
	return true;
    } else {
	return false;
    }

    return false;
}


inline bool set_attribute (GT_Key key, GT_Key option, int code, bool& found)
{
    if (code == TCL_OK && key == option) {
	found = true;
	return true;
    }

    return false;
}


int GT_Tcl_Graph::configure_cmd (
    GT_Tcl_info &info,
    int argc,
    GT_Tcl::Configure_Mode mode)
{
    int code = TCL_OK;

    Tcl_DString result;
    Tcl_DStringInit (&result);

    list<GT_Tcl_Graph*> graphs;
    list<node> nodes;
    list<edge> edges;
    list<GT_Key> node_styles;
    list<GT_Key> edge_styles;

    Spec spec = spec_plain;

    if (!info.exists (argc)) {

	// GET
	graphs.push_back (this);
	spec = spec_plain;
	mode.get();

    } else {

	if (is_attr (info[argc])) {

	    graphs.push_back (this);
	    spec = spec_plain;

	    if (info.is_last_arg (argc)) {
		// GET attr
		mode.get();
	    } else {
		// SET (attr value)*
		mode.set();
	    }

	} else if (is_spec (info[argc], spec)) {

	    graphs.push_back(this);
	    argc ++;

	    if (!info.exists (argc)) {
		// GET spec
		mode.get();
	    } else if (info.is_last_arg (argc)) {
		// GET spec attr
		mode.get();
	    } else {
		// SET spec (attr value)*
		mode.set();
	    }

	} else {

	    code = info.parse (info[argc], this,
		graphs, nodes, edges,
		node_styles, edge_styles);
	    if (code != TCL_OK) {
		return TCL_ERROR;
	    }

	    argc ++;

	    if (!info.exists (argc)) {

		// GET objects
		spec = spec_plain;
		mode.get();

	    } else if (is_spec (info[argc], spec)) {

		argc ++;

		if (!info.exists (argc)) {
		    // GET objects spec
		    mode.get();
		} else if (info.is_last_arg (argc)) {
		    // GET objects spec attr
		    mode.get();
		} else {
		    // SET objects spec (attr value)*
		    mode.set();
		}

	    } else if (is_attr (info[argc])) {

		if (info.is_last_arg (argc)) {
		    // GET objects attr
		    mode.get();
		} else {
		    // SET objects (attr value)*
		    mode.set();
		}

	    } else {
	
		info.msg ("Something's wrong - perhaps a missing character");
		return TCL_ERROR;

	    }
	}
    }

    list<GT_Graph_Attributes*> graph_attrs;
    list<GT_Node_Attributes*> node_attrs;
    list<GT_Edge_Attributes*> edge_attrs;
    list<GT_Common_Graphics*> graphics;
    list<GT_Common_Graphics*> label_graphics;

    GT_Tcl_Graph* g;
    node n;
    edge e;
    GT_Key style;

    // ! gt() ist nicht g() ! beabsichtigt
    list<GT_Tcl_Graph*>::const_iterator it_g;
    list<GT_Tcl_Graph*>::const_iterator it_g_end = graphs.end();

    for (it_g = graphs.begin(); it_g != it_g_end; ++it_g)
    {
	g = *it_g;
	switch (spec) {
	    case spec_plain:
		graph_attrs.push_back (&gt());
		break;
	    case spec_graphics:
		graphics.push_back (gt().graphics());
		break;
	    case spec_label_graphics:
		label_graphics.push_back (gt().label_graphics());
		break;
	}
    }

    list<node>::const_iterator it_n;
    list<node>::const_iterator it_n_end = nodes.end();

    for (it_n = nodes.begin(); it_n != it_n_end; ++it_n)
    {
	n = *it_n;
	switch (spec) {
	    case spec_plain:
		node_attrs.push_back (&gt(n));
		break;
	    case spec_graphics:
		graphics.push_back (gt(n).graphics());
		break;
	    case spec_label_graphics:
		label_graphics.push_back (gt(n).label_graphics());
		break;
	}
    }

    list<edge>::const_iterator it_e;
    list<edge>::const_iterator it_e_end = edges.end();

    for (it_e = edges.begin(); it_e != it_e_end; ++it_e)
    {
	e = *it_e;
	switch (spec) {
	    case spec_plain:
		edge_attrs.push_back (&gt(e));
		break;
	    case spec_graphics:
		graphics.push_back (gt(e).graphics());
		break;
	    case spec_label_graphics:
		label_graphics.push_back (gt(e).label_graphics());
		break;
	}
    }

    list<GT_Key>::const_iterator it_s;
    list<GT_Key>::const_iterator it_s_end = node_styles.end();

    for (it_s = node_styles.begin(); it_s != it_s_end; ++it_s)
    {
	style = * it_s;

	switch (spec) {
	    case spec_plain:
		node_attrs.push_back (node_style(style));
		break;
	    case spec_graphics:
		graphics.push_back (node_style(style)->graphics());
		break;
	    case spec_label_graphics:
		label_graphics.push_back (node_style(style)->label_graphics());
		break;
	}
    }

    it_s_end = edge_styles.end();

    for (it_s = edge_styles.begin(); it_s != it_s_end; ++it_s)
    {
	style = * it_s;

	switch (spec) {
	    case spec_plain:
		edge_attrs.push_back (edge_style(style));
		break;
	    case spec_graphics:
		graphics.push_back (edge_style(style)->graphics());
		break;
	    case spec_label_graphics:
		label_graphics.push_back (edge_style(style)->label_graphics());
		break;
	}
    }


    bool found = false;

    if (mode.is_get()) {

	GT_Key option;
	if (info.exists (argc)) {
	    option = info (argc);
	} else {
	    option = GT_Keys::empty;
	}

	list<Tcl_DString*> subresults;

	GT_Graph_Attributes* ga;
	list<GT_Graph_Attributes*>::const_iterator it_ga;
	list<GT_Graph_Attributes*>::const_iterator it_ga_end =
	    graph_attrs.end();
	for (it_ga = graph_attrs.begin(); it_ga != it_ga_end; ++it_ga)
	{
	    ga = *it_ga;
	    found = false;
	    Tcl_DString* subresult = new Tcl_DString;
	    subresults.push_back (subresult);
	    Tcl_DStringInit (subresult);
	    code = graphconfigure_get (*ga, option, *subresult, mode, found);
	    if (code != TCL_OK) {
		return code;
	    }
	    if (!found) {
		info.msg (string ("Unknown option ") + option.name());
		return TCL_ERROR;
	    }
	}

	GT_Node_Attributes* na;
	list<GT_Node_Attributes*>::const_iterator it_na;
	list<GT_Node_Attributes*>::const_iterator it_na_end = node_attrs.end();
	for (it_na = node_attrs.begin(); it_na != it_na_end; ++it_na)
	{
	    na = *it_na;
	    found = false;
	    Tcl_DString* subresult = new Tcl_DString;
	    subresults.push_back (subresult);
	    Tcl_DStringInit (subresult);
	    code = nodeconfigure_get (*na, option, *subresult, mode, found);
	    if (code != TCL_OK) {
		return code;
	    }
	    if (!found) {
		info.msg (string ("Unknown option ") + option.name());
		return TCL_ERROR;
	    }
	}

	GT_Edge_Attributes* ea;
	list<GT_Edge_Attributes*>::const_iterator it_ea;
	list<GT_Edge_Attributes*>::const_iterator it_ea_end = edge_attrs.end();
	for (it_ea = edge_attrs.begin(); it_ea != it_ea_end; ++it_ea)
	{
	    ea = *it_ea;
	    found = false;
	    Tcl_DString* subresult = new Tcl_DString;
	    subresults.push_back (subresult);
	    Tcl_DStringInit (subresult);
	    code = edgeconfigure_get (*ea, option, *subresult, mode, found);
	    if (code != TCL_OK) {
		return code;
	    }
	    if (!found) {
		info.msg (string ("Unknown option ") + option.name());
		return TCL_ERROR;
	    }
	}

	GT_Common_Graphics* cg;
	list<GT_Common_Graphics*>::const_iterator it_cg;
	list<GT_Common_Graphics*>::const_iterator it_cg_end = graphics.end();
	for (it_cg = graphics.begin(); it_cg != it_cg_end; ++it_cg)
	{
	    cg = *it_cg;
	    found = false;
	    Tcl_DString* subresult = new Tcl_DString;
	    subresults.push_back (subresult);
	    Tcl_DStringInit (subresult);
	    code = graphicsconfigure_get (*cg, option, *subresult, mode,
		found);
	    if (code != TCL_OK) {
		return code;
	    }
	    if (!found) {
		info.msg (string ("Unknown option ") + option.name());
		return TCL_ERROR;
	    }
	}

	GT_Common_Graphics* label_cg;
	it_cg_end = label_graphics.end();
	for (it_cg = label_graphics.begin(); it_cg != it_cg_end; ++it_cg)
	{
	    label_cg = *it_cg;
	    found = false;
	    Tcl_DString* subresult = new Tcl_DString;
	    subresults.push_back (subresult);
	    Tcl_DStringInit (subresult);
	    code = graphicsconfigure_get (*label_cg, option, *subresult, mode,
		found);
	    if (code != TCL_OK) {
		return code;
	    }
	    if (!found) {
		info.msg (string ("Unknown option ") + option.name());
		return TCL_ERROR;
	    }
	}

	if (subresults.size() == 1) {
	    Tcl_DStringAppend (&result,
		Tcl_DStringValue (subresults.front()),
		Tcl_DStringLength (subresults.front()));
	    Tcl_DStringFree (subresults.front());
	    delete subresults.front();
	} else if (subresults.size() > 1){
	    Tcl_DString* s;
	    list<Tcl_DString*>::const_iterator it;
	    list<Tcl_DString*>::const_iterator end = subresults.end();
	    for (it = subresults.begin(); it != end; ++it)
	    {
		s = *it;
		Tcl_DStringAppendElement (&result, Tcl_DStringValue (s));
		Tcl_DStringFree (s);
		delete s;
	    }
	} else {
	    // Empty list of objects
	}

    } else {

	while (info.exists (argc+1)) {

	    GT_Key option = info (argc++);
	    char* value = info [argc++];

	    GT_Graph_Attributes* ga;
	    list<GT_Graph_Attributes*>::const_iterator it_ga;
	    list<GT_Graph_Attributes*>::const_iterator it_ga_end =
		graph_attrs.end();
	    for (it_ga = graph_attrs.begin(); it_ga != it_ga_end; ++it_ga)
	    {
		ga = *it_ga;
		found = false;
		code = graphconfigure_set (info, *ga, option, value, found);
		if (code != TCL_OK) {
		    return code;
		}
		if (!found) {
		    info.msg (string ("Unknown option ") + option.name());
		    return TCL_ERROR;
		}
	    }

	    GT_Node_Attributes* na;
	    list<GT_Node_Attributes*>::const_iterator it_na;
	    list<GT_Node_Attributes*>::const_iterator it_na_end =
		node_attrs.end();
	    for (it_na = node_attrs.begin(); it_na != it_na_end; ++it_na)
	    {
		na = *it_na;
		found = false;
		code = nodeconfigure_set (info, *na, option, value, found);
		if (code != TCL_OK) {
		    return code;
		}
		if (!found) {
		    info.msg (string ("Unknown option ") + option.name());
		    return TCL_ERROR;
		}
	    }

	    GT_Edge_Attributes* ea;
	    list<GT_Edge_Attributes*>::const_iterator it_ea;
	    list<GT_Edge_Attributes*>::const_iterator it_ea_end =
		edge_attrs.end();
	    for (it_ea = edge_attrs.begin(); it_ea != it_ea_end; ++it_ea)
	    {
		ea = *it_ea;
		found = false;
		code = edgeconfigure_set (info, *ea, option, value, found);
		if (code != TCL_OK) {
		    return code;
		}
		if (!found) {
		    info.msg (string ("Unknown option ") + option.name());
		    return TCL_ERROR;
		}
	    }

	    GT_Common_Graphics* cg;
	    list<GT_Common_Graphics*>::const_iterator it_cg;
	    list<GT_Common_Graphics*>::const_iterator it_cg_end =
		graphics.end();
	    for (it_cg = graphics.begin(); it_cg != it_cg_end; ++it_cg)
	    {
		cg = *it_cg;
		found = false;
		code = graphicsconfigure_set (info, *cg, option, value, found);
		if (code != TCL_OK) {
		    return code;
		}
		if (!found) {
		    info.msg (string ("Unknown option ") + option.name());
		    return TCL_ERROR;
		}
	    }

	    GT_Common_Graphics* label_cg;
	    it_cg_end = label_graphics.end();
	    for (it_cg = label_graphics.begin(); it_cg != it_cg_end; ++it_cg)
	    {
		label_cg = *it_cg;
		found = false;
		code = graphicsconfigure_set (info, *label_cg, option, value,
		    found);
		if (code != TCL_OK) {
		    return code;
		}
		if (!found) {
		    info.msg (string ("Unknown option ") + option.name());
		    return TCL_ERROR;
		}
	    }

	}
    }

    if (((!node_styles.empty()) || (!edge_styles.empty())) && mode.is_set()) {
	update_styles ();
    }

    info.msg (Tcl_DStringValue(&result));

    return code;
}



//////////////////////////////////////////
//
// GT_Tcl_Graph::common_configure_set
// GT_Tcl_Graph::common_configure_get
//
//////////////////////////////////////////


int GT_Tcl_Graph::common_configure_set (GT_Tcl_info& tcl_info,
    GT_Common_Attributes& attrs, const GT_Key& key, char* value,
    bool& found)
{
    int code = TCL_OK;

    if (set_attribute (key, GT_Keys::option_label, code, found)) {

	attrs.label (value);

    } else if (set_attribute (key, GT_Keys::option_name, code, found)) {

	attrs.name (value);

    } else if (set_attribute (key, GT_Keys::option_label_anchor, code, found)){

	attrs.label_anchor (graphlet->keymapper.add(value));

    } else if (set_attribute (key, GT_Keys::option_visible, code, found)) {

	int visible;
	code = Tcl_GetInt (tcl_info.interp(), value, &visible);
	if (code == TCL_OK) {
	    attrs.visible (visible);
	}

    } else if (code == TCL_OK && (key.name())[0] == '.') {

	GT_List_of_Attributes::iterator lookup;
	GT_List_of_Attributes* a = &attrs;

	const list<GT_Key>& path = key.description()->path();
	list<GT_Key>::const_iterator it;
	for (it = path.begin(); it != path.end(); ++it) {
	    lookup = a->find ((*it));
	    if (lookup != a->end() && (*lookup)->is_list()) {
		(*lookup)->value_list(a);
	    } else if (it != --(path.end())) {
		GT_List_of_Attributes* new_list = new GT_List_of_Attributes;
		GT_Attribute_list* new_attribute =
		    new GT_Attribute_list (*it, new_list);
		a->push_back (new_attribute);
		a = new_list;
	    }
	}

	found = true;
	    
	int i;
	double d;
	if (tcl_info.parse (value, i) != TCL_ERROR) {
	    code = TCL_OK;
	    if (lookup == a->end()) {
		a->push_back (new GT_Attribute_int (path.back(),
		    i));
	    } else if ((*lookup)->is_int()) {
		(static_cast<GT_Attribute_int* const>(*lookup))->
		    value (i);
	    }  else if ((*lookup)->is_double()) {
		(static_cast<GT_Attribute_double* const>(*lookup))->
		    value (double(i));
	    } else {
		delete (*lookup);
		a->erase (lookup);
		a->push_back (new GT_Attribute_int (path.back(),
		    i));
	    }
	} else if (tcl_info.parse (value, d) != TCL_ERROR) {
	    code = TCL_OK;
	    if (lookup == a->end()) {
		a->push_back (new GT_Attribute_double (path.back(),
		    d));
	    } else if ((*lookup)->is_double()) {
		(static_cast<GT_Attribute_double* const>(*lookup))->
		    value (d);
	    } else {
		delete (*lookup);
		a->erase (lookup);
		a->push_back (new GT_Attribute_double (path.back(),
		    d));
	    }
	} else {
	    code = TCL_OK;
	    if (lookup == a->end()) {
		a->push_back (new GT_Attribute_string (path.back(),
		    value));
	    } else if ((*lookup)->is_string()) {
		(static_cast<GT_Attribute_string* const>(*lookup))->
		    value (value);
	    } else {
		delete (*lookup);
		a->erase (lookup);
		a->push_back (new GT_Attribute_string (path.back(),
		    value));
	    }
	}
    }

    return code;	
}


int GT_Tcl_Graph::common_configure_get (
    const GT_Common_Attributes& attrs, const GT_Key& key, Tcl_DString& result,
    const GT_Tcl::Configure_Mode mode,
    bool& found)
{
    int code = TCL_OK;
	
    if (get_attribute (key, GT_Keys::option_id, code, found)) {		
	code = GT_Tcl::format_value (result,
	    GT_Keys::option_id, attrs.id(),
	    mode);
    }

//     if (get_attribute (key, GT_Keys::option_uid, code, found)) {
// 	code = GT_Tcl::format_value (result, GT_Keys::option_uid,
// 	    string ("GT:%d", attrs.uid()),
// 	    mode);
//     }

//     if (get_attribute (key, GT_Keys::option_label_uid, code, found)) {
// 	code = GT_Tcl::format_value (result, GT_Keys::option_label_uid,
// 	    string ("GT:%d", attrs.label_uid()),
// 	    mode);
//     }
	
    if (get_attribute (key, GT_Keys::option_label, code, found)) {
	code = GT_Tcl::format_value (result,
	    GT_Keys::option_label, attrs.label(),
	    mode);	
    }
	
    if (get_attribute (key, GT_Keys::option_name, code, found)) {
	code = GT_Tcl::format_value (result,
	    GT_Keys::option_name, attrs.name(),
	    mode);	
    }
	
    if (get_attribute (key, GT_Keys::option_label_anchor, code, found)) {
	code = GT_Tcl::format_value (result,
	    GT_Keys::option_label_anchor, attrs.label_anchor(),
	    mode);
    }
	
    if (get_attribute (key, GT_Keys::option_visible, code, found)) {
	code = GT_Tcl::format_value (result,
	    GT_Keys::option_visible, attrs.visible(),
	    mode);
    }

    if ((key.name())[0] == '.') {
	// Found Attribute

	GT_List_of_Attributes const* a = &attrs;
	GT_List_of_Attributes::const_iterator lookup;

	const list<GT_Key>& path = key.description()->path();
	list<GT_Key>::const_iterator it;
	for (it = path.begin(); it != path.end(); ++it) {
	    lookup = a->find ((*it));
	    if (lookup != a->end() && (*lookup)->is_list()) {
		(*lookup)->value_list(a);
	    } else {
		++it;
		break;
	    }
	}

	if (it == path.end() && lookup != a->end()) {
	    found = true;
	    if (path.begin() != path.end()) {
		code = GT_Tcl::format_value (result, a, lookup, mode, ".");
	    } else {
		for (lookup = a->begin();
		     code == TCL_OK && lookup != a->end();
		     ++lookup) {
		    code = GT_Tcl::format_value (result, a, lookup, mode, ".");
		}
	    }
	} else {
	    found = false;
	    code = TCL_ERROR;
	}
    }

    return code;
}


//////////////////////////////////////////
//
// GT_Tcl_Graph::graphconfigure_set
// GT_Tcl_Graph::graphconfigure_get
//
//////////////////////////////////////////


int GT_Tcl_Graph::graphconfigure_set (GT_Tcl_info& info,
    GT_Graph_Attributes& attrs, const GT_Key& key, char* value,
    bool& found)
{
    int code = TCL_OK;

    if (set_attribute (key, GT_Keys::option_directed, code, found)) {
		
	int directed;
	code = Tcl_GetBoolean (info.interp(), value, &directed);
	if (code == TCL_ERROR) {
	    return code;
	}

	code = run_hooks (pre_directed_hook, directed ? "1" : "0");
	if (code == TCL_ERROR) {
	    return code;
	}

	if (directed) {
	    leda().make_directed ();
	} else {
	    leda().make_undirected ();
	}

	code = run_hooks (post_directed_hook, directed ? "1" : "0");
	if (code == TCL_ERROR) {
	    return code;
	}

    }


    if (!found && code == TCL_OK) {
	code = common_configure_set (info, attrs,
	    key, value, found);
    }

    if (!found && code == TCL_OK) {
	code = graphicsconfigure_set (info, *(attrs.graphics()),
	    key, value, found);
    }
	
    return code;	
}



int GT_Tcl_Graph::graphconfigure_get (
    const GT_Graph_Attributes& attrs, const GT_Key& key, Tcl_DString& result,
    const GT_Tcl::Configure_Mode mode,
    bool& found)
{
    int code = TCL_OK;
    bool get_all = (key == GT_Keys::empty);
	
    if (get_attribute (key, GT_Keys::option_directed, code, found)) {
	code = GT_Tcl::format_value (result,
	    GT_Keys::option_directed, leda().is_directed() ? true : false,
	    mode);
    }
	
    if (!found || (get_all && code == TCL_OK)) {		
	code = common_configure_get (attrs, key, result,
	    mode, found);
    }

    if (!found || (get_all && code == TCL_OK)) {
	code = graphicsconfigure_get (*(attrs.graphics()), key, result,
	    mode, found);
    }
	
    return code;
}



//////////////////////////////////////////
//
// GT_Tcl_Graph::nodeconfigure_set
// GT_Tcl_Graph::nodeconfigure_get
//
//////////////////////////////////////////


int GT_Tcl_Graph::nodeconfigure_set (GT_Tcl_info& info,
    GT_Node_Attributes& node_attrs, const GT_Key& key, char* value,
    bool& found)
{
    int code = TCL_OK;

    if (set_attribute (key, GT_Keys::option_ports, code, found)) {
	
	GT_Ports ports;
	
	list<string> list_of_ports;
	code = GT_Tcl::split_list (info.interp(), value, list_of_ports);
	if (code != TCL_OK) {
	    return code;
	}

	for (list<string>::const_iterator port_it = list_of_ports.begin();
	     port_it != list_of_ports.end();
	     ++port_it) {

	    list<string> port_details;
	    code = GT_Tcl::split_list (info.interp(),
		(*port_it).c_str(), port_details);
	    if (code != TCL_OK) {
		return code;
	    }
	    if (port_details.size() != 3) {
		info.msg (GT::format("%s is not a valid port",
		    (*port_it).c_str()));
		return TCL_ERROR;
	    }

	    list<string>::const_iterator port_details_it =
		port_details.begin();
	    string name;
	    double x;
	    double y;

	    name = *port_details_it;
	    ++port_details_it;
	    code = info.parse ((*port_details_it).c_str(), x);
	    if (code != TCL_OK) {
		return code;
	    }
	    ++port_details_it;
	    code = info.parse ((*port_details_it).c_str(), y);
	    if (code != TCL_OK) {
		return code;
	    }

	    ports.push_back (GT_Port (name, x,y));
	    
	}

	node_attrs.ports (ports);
    }
    
    // Walter: edgeanchor
    if (set_attribute (key, GT_Keys::option_default_function, code, found)) {
	node_attrs.node_nei()->default_function (
	    graphlet->keymapper.add(value));
    }
    // end NEI

    if (!found && code == TCL_OK) {
	code = common_configure_set (info,
	    node_attrs, key, value,
	    found);
    }

    if (!found && code == TCL_OK) {
	code = graphicsconfigure_set (info,
	    *node_attrs.graphics(), key, value,
	    found);
    }

    return code;
}



int GT_Tcl_Graph::nodeconfigure_get (
    const GT_Node_Attributes& node_attrs,
    const GT_Key& key, Tcl_DString& result,
    const GT_Tcl::Configure_Mode mode,
    bool& found)
{
    bool get_all = (key == GT_Keys::empty);	
    int code = TCL_OK;	

    if (get_attribute (key, GT_Keys::option_ports, code, found)) {

	list<string> ports;

	list<GT_Port>::const_iterator port_it;
	for (port_it = node_attrs.ports().begin();
	     port_it != node_attrs.ports().end();
	     ++port_it) {
	    list<string> port_details;
	    port_details.push_back (port_it->name().name());
	    port_details.push_back (GT_Tcl::tcl (port_it->x()));
	    port_details.push_back (GT_Tcl::tcl (port_it->y()));
	    ports.push_back (GT_Tcl::tcl (port_details));
	}

	code = GT_Tcl::format_value (result, GT_Keys::option_ports,
	    GT_Tcl::tcl(ports), mode);
    }

    // Walter: edgeanchor
    if (get_attribute (key, GT_Keys::option_default_function, code, found)) {
	code = GT_Tcl::format_value (result, GT_Keys::option_default_function,
	    node_attrs.node_nei()->default_function(), mode);
    }
    // end Walter
	

    if (!found || (get_all && code == TCL_OK)) {		
	code = common_configure_get (node_attrs, key, result,
	    mode, found);
    }

    if (!found || (get_all && code == TCL_OK)) {
	code = graphicsconfigure_get (*node_attrs.graphics(), key, result,
	    mode, found);
    }
	
    return code;
}



//////////////////////////////////////////
//
// GT_Tcl_Graph::edgeconfigure_set
// GT_Tcl_Graph::edgeconfigure_get
//
//////////////////////////////////////////



int GT_Tcl_Graph::edgeconfigure_set (GT_Tcl_info& info,
    GT_Edge_Attributes& edge_attrs, const GT_Key& key, char* value,
    bool& found)
{
    int code = TCL_OK;

    if (set_attribute (key, GT_Keys::option_source_port,
	code, found)) {

	edge_attrs.source_port (graphlet->keymapper.add (value));

    } else if (set_attribute (key, GT_Keys::option_target_port,
	code, found)) {

	edge_attrs.target_port (graphlet->keymapper.add (value));

    } else if (set_attribute (key, GT_Keys::option_source_function,
	code, found)) {

	GT_Key function = graphlet->keymapper.add (value);
	edge_attrs.edge_nei()->set_EA_default_function (function, GT_Source);

    } else if (set_attribute (key, GT_Keys::option_target_function,
	code, found)) {

	GT_Key function = graphlet->keymapper.add (value);
	edge_attrs.edge_nei()->set_EA_default_function (function, GT_Target);

    } else if (set_attribute (key, GT_Keys::option_default_function,
	code, found)) {

	GT_Key function = graphlet->keymapper.add (value);
	edge_attrs.edge_nei()->set_EA_default_function (function, GT_Target);
	edge_attrs.edge_nei()->set_EA_default_function (function, GT_Source);

    } else if (set_attribute (key, GT_Keys::option_delta_x_source,
	code, found)) {

	double delta;		
	code = Tcl_GetDouble(info.interp(), value, &delta);
	if (code == TCL_OK) {
	    edge_attrs.edge_nei()->d_x_source (delta);
	}                       

    } else if (set_attribute (key, GT_Keys::option_delta_y_source,
	code, found)) {

	double delta;
	code = Tcl_GetDouble(info.interp(), value, &delta);
	if (code == TCL_OK) {
	    edge_attrs.edge_nei()->d_y_source (delta);
	}                       

    } else if (set_attribute (key, GT_Keys::option_delta_x_target,
	code, found)) {

	double delta;
	code = Tcl_GetDouble(info.interp(), value, &delta);
	if (code == TCL_OK) {
	    edge_attrs.edge_nei()->d_x_target (delta);
	}                       

    } else if (set_attribute (key, GT_Keys::option_delta_y_target,
	code, found)) {

	double delta;
	code = Tcl_GetDouble(info.interp(), value, &delta);
	if (code == TCL_OK) {
	    edge_attrs.edge_nei()->d_y_target (delta);
	}                       

    } else if (set_attribute (key, GT_Keys::option_label_anchor_bend,
	code, found)) {

	int bend;
	code = Tcl_GetInt(info.interp(), value, &bend);
	edge_attrs.label_anchor_bend (bend);

    } else if (set_attribute (key, GT_Keys::option_label_anchor_x,
	code, found)) {

	double x;
	code = Tcl_GetDouble(info.interp(), value, &x);
	if (code == TCL_OK) {
	    edge_attrs.label_anchor_x (x);
	}                       

    } else if (set_attribute (key, GT_Keys::option_label_anchor_y,
	code, found)) {

	double y;
	code = Tcl_GetDouble(info.interp(), value, &y);
	if (code == TCL_OK) {
	    edge_attrs.label_anchor_y (y);
	}                       

    }

    if (!found && code == TCL_OK) {
	code = common_configure_set (info,
	    edge_attrs, key, value,
	    found);
    }

    if (!found && code == TCL_OK) {
	code = graphicsconfigure_set (info,
	    *edge_attrs.graphics(), key, value,
	    found);
    }
	
    return code;
}


		
int GT_Tcl_Graph::edgeconfigure_get (
    const GT_Edge_Attributes& edge_attrs,
    const GT_Key& key,Tcl_DString& result,
    const GT_Tcl::Configure_Mode mode,
    bool& found)
{
    bool get_all = (key == GT_Keys::empty);
    int code = TCL_OK;

    if (get_attribute (key, GT_Keys::option_source_port, code, found)) {
	code = GT_Tcl::format_value (result,
	    GT_Keys::option_source_port, edge_attrs.source_port(),
	    mode);
    }

    if (get_attribute (key, GT_Keys::option_target_port, code, found)) {
	code = GT_Tcl::format_value (result,
	    GT_Keys::option_target_port, edge_attrs.target_port(),
	    mode);
    }

    if (get_attribute (key, GT_Keys::option_source, code, found)) {

	if (edge_attrs.source() != node()) {
	    string source = GT::format("GT:%d", gt(edge_attrs.source()).id());
	    code = GT_Tcl::format_value (result,
		GT_Keys::option_source, source,
		mode);
	}

    }

    if (get_attribute (key, GT_Keys::option_target, code, found)) {

	if (edge_attrs.target() != node()) {
	    string target = GT::format("GT:%d", gt(edge_attrs.target()).id());
	    code = GT_Tcl::format_value (result,
		GT_Keys::option_target, target,
		mode);
	}
    }

    // Walter: edge anchor
    if (get_attribute (key, GT_Keys::option_source_function, code, found)) {
	code = GT_Tcl::format_value (result, GT_Keys::option_source_function,
	    edge_attrs.edge_nei()->source_function(), mode);
    }

    if (get_attribute (key, GT_Keys::option_target_function, code, found)) {
	code = GT_Tcl::format_value (result, GT_Keys::option_target_function,
	    edge_attrs.edge_nei()->target_function(), mode);
    }
	
    if (get_attribute (key, GT_Keys::option_delta_x_source, code, found)) {
	code = GT_Tcl::format_value (result, GT_Keys::option_delta_x_source,
	    edge_attrs.edge_nei()->get_EA_x(GT_Source), mode);
    }
	
    if (get_attribute (key, GT_Keys::option_delta_y_source, code, found)) {
	code = GT_Tcl::format_value (result, GT_Keys::option_delta_y_source,
	    edge_attrs.edge_nei()->get_EA_y(GT_Source), mode);
    }
	
    if (get_attribute (key, GT_Keys::option_delta_x_target, code, found)) {
	code = GT_Tcl::format_value (result, GT_Keys::option_delta_x_target,
	    edge_attrs.edge_nei()->get_EA_x(GT_Target), mode);
    }
	
    if (get_attribute (key, GT_Keys::option_delta_y_target, code, found)) {
	code = GT_Tcl::format_value (result, GT_Keys::option_delta_y_target,
	    edge_attrs.edge_nei()->get_EA_y(GT_Target), mode);
    }
    // End NEI
	
    if (get_attribute (key, GT_Keys::option_label_anchor_bend, code, found)) {
	code = GT_Tcl::format_value (result, GT_Keys::option_label_anchor_bend,
	    edge_attrs.label_anchor_bend(), mode);
    }
	
    if (get_attribute (key, GT_Keys::option_label_anchor_x, code, found)) {
	code = GT_Tcl::format_value (result, GT_Keys::option_label_anchor_x,
	    edge_attrs.label_anchor_x(), mode);
    }
	
    if (get_attribute (key, GT_Keys::option_label_anchor_y, code, found)) {
	code = GT_Tcl::format_value (result, GT_Keys::option_label_anchor_y,
	    edge_attrs.label_anchor_y(), mode);
    }

    if (!found || (get_all && code == TCL_OK)) {		
	code = common_configure_get (edge_attrs, key, result,
	    mode, found);
    }

    if (!found || (get_all && code == TCL_OK)) {
	code = graphicsconfigure_get (*edge_attrs.graphics(), key, result,
	    mode, found);
    }
	
    return code;
}



//////////////////////////////////////////
//
// GT_Tcl_Graph::graphicsconfigure_set
// GT_Tcl_Graph::graphicsconfigure_get
//
//////////////////////////////////////////


int GT_Tcl_Graph::graphicsconfigure_set (GT_Tcl_info& tcl_info,
    GT_Common_Graphics& graphics, const GT_Key& key, char* value,
    bool& found)
{
    int code = TCL_OK;

    //
    // Common
    //
	
    if (set_attribute (key, GT_Keys::option_type, code, found)) {

	GT_Key type = graphlet->keymapper.add (value);
	if (type == GT_Keys::type_arc ||
	    type == GT_Keys::type_bitmap ||
	    type == GT_Keys::type_image ||
	    type == GT_Keys::type_line ||
	    type == GT_Keys::type_oval ||
	    type == GT_Keys::type_polygon ||
	    type == GT_Keys::type_rectangle ||
	    type == GT_Keys::type_text) 
	{
	    graphics.type (type);
	} else {
	    tcl_info.msg ("Illegal argument for -type: must be one of arc,bitmap,image,line,oval,polygon,rectangle,text");
	    code = TCL_ERROR;
	}
		
    } else if (set_attribute (key, GT_Keys::option_w, code, found)) {
		
	double w;
	code = Tcl_GetDouble (tcl_info.interp(), value, &w);
	if (code == TCL_OK) {
	    if (w > GT_epsilon) {
		graphics.w (w);
	    } else {
		graphics.w (GT_epsilon);
	    }
	}
		
    } else if (set_attribute (key, GT_Keys::option_h, code, found)) {
		
	double h;
	code = Tcl_GetDouble (tcl_info.interp(), value, &h);
	if (code == TCL_OK) {
	    if (h > GT_epsilon) {
		graphics.h (h);
	    } else {
		graphics.h (GT_epsilon);
	    }
	}
			
    } else if (set_attribute (key, GT_Keys::option_x, code, found)) {

	double x;
	code = Tcl_GetDouble (tcl_info.interp(), value, &x);
	if (code == TCL_OK) {
	    graphics.x (x);
	}
		
    } else if (set_attribute (key, GT_Keys::option_y, code, found)) {
		
	double y;
	code = Tcl_GetDouble (tcl_info.interp(), value, &y);
	if (code == TCL_OK) {
	    graphics.y (y);
	}
		
    } else if (set_attribute (key, GT_Keys::option_fill, code, found)) {
		
	graphics.fill (graphlet->keymapper.add(value));
		
    } else if (set_attribute (key, GT_Keys::option_outline, code, found)) {
		
	graphics.outline (graphlet->keymapper.add(value));
		
    } else if (set_attribute (key, GT_Keys::option_stipple, code, found)) {
		
	graphics.stipple (graphlet->keymapper.add(value));
		
    } else if (set_attribute (key, GT_Keys::option_anchor, code, found)) {
		
	graphics.anchor (graphlet->keymapper.add(value));
		
    } else if (set_attribute (key, GT_Keys::option_width, code, found)) {
		
	double width;
	code = Tcl_GetDouble (tcl_info.interp(), value, &width);
	if (code == TCL_OK) {
	    graphics.width (width);
	}
		
    }

    // Arc

    else if (set_attribute (key, GT_Keys::option_extent, code, found)) {
		
	double extent;
	code = Tcl_GetDouble (tcl_info.interp(), value, &extent);
	if (code == TCL_OK) {
	    graphics.extent (extent);
	}
		
    } else if (set_attribute (key, GT_Keys::option_start, code, found)) {
		
	double start;
	code = Tcl_GetDouble (tcl_info.interp(), value, &start);
	if (code == TCL_OK) {
	    graphics.start (start);
	}
		
    } else if (set_attribute (key, GT_Keys::option_style, code, found)) {
		
	graphics.style (graphlet->keymapper.add(value));
    }

    //
    // Bitmap
    //
	
    else if (set_attribute (key, GT_Keys::option_foreground, code, found)) {
		
	graphics.foreground (graphlet->keymapper.add(value));
		
    } else if (set_attribute (key, GT_Keys::option_background, code, found)) {
		
	graphics.background (graphlet->keymapper.add(value));

    } else if (set_attribute (key, GT_Keys::option_bitmap, code, found)) {
		
	graphics.bitmap (graphlet->keymapper.add(value));
		
    }

    //
    // Line
    //
	
    else if (set_attribute (key, GT_Keys::option_arrow, code, found)) {

	graphics.arrow (graphlet->keymapper.add(value));
		
    } else if (set_attribute (key, GT_Keys::option_arrowshape, code, found)) {

	char** list_argv;
	int list_argc;

	code = Tcl_SplitList (tcl_info.interp(), value,
	    &list_argc, &list_argv);
	if (code != TCL_OK) {
	    Tcl_Free ((char*)list_argv);
	    tcl_info.msg ("Error splitting arrowshape");
	    return code;
	} else if (list_argc != 3) {
	    Tcl_Free ((char*)list_argv);
	    tcl_info.msg ("Arrowshape needs exactly three elements");
	    return TCL_ERROR;
	}

	double touching_length;
	code = Tcl_GetDouble (tcl_info.interp(), list_argv[0],
	    &touching_length);
	if (code == TCL_OK) {
	    graphics.arrowshape_touching_length (touching_length);
	}

	double overall_length;
	code = Tcl_GetDouble (tcl_info.interp(), list_argv[1],
	    &overall_length);
	if (code == TCL_OK) {
	    graphics.arrowshape_overall_length (overall_length);
	}

	double width;
	code = Tcl_GetDouble (tcl_info.interp(), list_argv[2], &width);
	if (code == TCL_OK) {
	    graphics.arrowshape_width (width);
	}

	Tcl_Free ((char*)list_argv);
		
    }  else if (set_attribute (key, GT_Keys::option_arrowshape_touching_length,
	code, found)) {

	double touching_length;
	code = Tcl_GetDouble (tcl_info.interp(), value, &touching_length);
	if (code == TCL_OK) {
	    graphics.arrowshape_touching_length (touching_length);
	}
		
    }  else if (set_attribute (key, GT_Keys::option_arrowshape_overall_length,
	code, found)) {

	double overall_length;
	code = Tcl_GetDouble (tcl_info.interp(), value, &overall_length);
	if (code == TCL_OK) {
	    graphics.arrowshape_overall_length (overall_length);
	}
		
    }  else if (set_attribute (key, GT_Keys::option_arrowshape_width,
	code, found)) {

	double width;
	code = Tcl_GetDouble (tcl_info.interp(), value, &width);
	if (code == TCL_OK) {
	    graphics.arrowshape_width (width);
	}
		
    } else if (set_attribute (key, GT_Keys::option_capstyle, code, found)) {

	int unused_capstyle;
	code = Tk_GetCapStyle (tcl_info.interp(), value, &unused_capstyle);
	if (code != TCL_ERROR) {
	    graphics.capstyle (graphlet->keymapper.add(value));
	}
		
    } else if (set_attribute (key, GT_Keys::option_joinstyle, code, found)) {

	int unused_joinstyle;
	code = Tk_GetJoinStyle (tcl_info.interp(), value, &unused_joinstyle);
	if (code != TCL_ERROR) {
	    graphics.joinstyle (graphlet->keymapper.add(value));
	}
		
    }
	
    //
    // Image
    //
	
    else if (set_attribute (key, GT_Keys::option_image, code, found)) {

	graphics.image (graphlet->keymapper.add(value));
		
    }

    //
    // Polygon
    //
	
    else if (set_attribute (key, GT_Keys::option_smooth, code, found)) {
		
	int int_smooth;
	code = Tcl_GetBoolean (tcl_info.interp(), value, &int_smooth);
	if (code == TCL_OK) {
	    graphics.smooth (int_smooth == 0 ? false : true);
	}
		
    } else if (set_attribute (key, GT_Keys::option_splinesteps, code, found)) {
		
	int splinesteps;
	code = Tcl_GetInt (tcl_info.interp(), value, &splinesteps);
	if (code == TCL_OK) {
	    graphics.splinesteps (splinesteps);
	}
		
    }

    //
    // Text
    //

    else if (set_attribute (key, GT_Keys::option_justify, code, found)) {

	graphics.justify (graphlet->keymapper.add(value));

    } else if (set_attribute (key, GT_Keys::option_font, code, found)) {

	graphics.font (graphlet->keymapper.add(value));

    } else if (set_attribute (key, GT_Keys::option_font_size, code, found)) {

	int size;
	code = Tcl_GetInt (tcl_info.interp(), value, &size);
	if (code == TCL_OK) {
	    graphics.font_size (size);
	}

    } else if (set_attribute (key, GT_Keys::option_font_style, code, found)) {

	graphics.font_style (graphlet->keymapper.add(value));

    }

    //
    // Line
    //
	
    else if (set_attribute (key, GT_Keys::option_line, code, found)) {
		
	char** list_argv;
	int list_argc;
		
	//
	// Split the list
	//
		
	code = Tcl_SplitList (tcl_info.interp(), value,
	    &list_argc, &list_argv);
	if (code != TCL_OK) {
	    tcl_info.msg ("Error splitting line");
	    return code;
	}
		
	//
	// Test & go
	//
		
	if (list_argc % 2 != 0) {
	    tcl_info.msg ("List for -line has an uneven number of entries");
	    Tcl_Free ((char*)list_argv);
	    return TCL_ERROR;
	}

	GT_Polyline points;
	for (int i = 0; i+1 < list_argc; i += 2) {
				
	    double x,y;
	    
	    code = Tcl_GetDouble (tcl_info.interp(), list_argv[i], &x);
	    if (code != TCL_OK) {
		return code;
	    }				
	    code = Tcl_GetDouble (tcl_info.interp(), list_argv[i+1], &y);
	    if (code != TCL_OK) {
		return code;
	    }
				
	    points.push_back (GT_Point (x,y));
	}
			
	Tcl_Free ((char*)list_argv);
	graphics.line (points);
		
    }
	
    return code;
}



int GT_Tcl_Graph::graphicsconfigure_get (
    const GT_Common_Graphics& graphics, const GT_Key& key,Tcl_DString& result,
    const GT_Tcl::Configure_Mode mode,
    bool& found)
{
    int code = TCL_OK;

    //
    // Common
    //
	
    if (get_attribute (key, GT_Keys::option_type, code, found)) {
	if (graphics.type().active()) {
	    code = GT_Tcl::format_value (result, GT_Keys::option_type,
		graphics.type(),
		mode);
	} else {
	    code = GT_Tcl::format_value (result, GT_Keys::option_type,
		GT_Keys::type_rectangle,
		mode);
	}
    }
    if (get_attribute (key, GT_Keys::option_w, code, found)) {
	code = GT_Tcl::format_value (result, GT_Keys::option_w,
	    graphics.w(),
	    mode);
    }
    if (get_attribute (key, GT_Keys::option_h, code, found)) {
	code = GT_Tcl::format_value (result, GT_Keys::option_h,
	    graphics.h(),
	    mode);
    }
    if (get_attribute (key, GT_Keys::option_x, code, found)){
	code = GT_Tcl::format_value (result, GT_Keys::option_x,
	    graphics.x(),
	    mode);
    }
    if (get_attribute (key, GT_Keys::option_y, code, found)) {
	code = GT_Tcl::format_value (result, GT_Keys::option_y,
	    graphics.y(),
	    mode);
    }

    if (get_attribute (key, GT_Keys::option_fill, code, found)) {
	code = GT_Tcl::format_value (result, GT_Keys::option_fill,
	    graphics.fill(),
	    mode);
    }
	
    if (get_attribute (key, GT_Keys::option_outline, code, found)) {
	code = GT_Tcl::format_value (result, GT_Keys::option_outline,
	    graphics.outline(),
	    mode);
    }
	
    if (get_attribute (key, GT_Keys::option_stipple, code, found)) {
	code = GT_Tcl::format_value (result, GT_Keys::option_stipple,
	    graphics.stipple(),
	    mode);
    }
	
    if (get_attribute (key, GT_Keys::option_anchor, code, found)) {
	code = GT_Tcl::format_value (result, GT_Keys::option_anchor,
	    graphics.anchor(),
	    mode);
    }

    if (get_attribute (key, GT_Keys::option_width, code, found)) {
	code = GT_Tcl::format_value (result, GT_Keys::option_width,
	    graphics.width(),
	    mode);
    }


    //
    // Arc
    //

    if (get_attribute (key, GT_Keys::option_extent, code, found)) {
	code = GT_Tcl::format_value (result, GT_Keys::option_extent,
	    graphics.extent(),
	    mode);
    }

    if (get_attribute (key, GT_Keys::option_start, code, found)) {
	code = GT_Tcl::format_value (result, GT_Keys::option_start,
	    graphics.start(),
	    mode);
    }
	
    if (get_attribute (key, GT_Keys::option_style, code, found)) {
	code = GT_Tcl::format_value (result, GT_Keys::option_style,
	    graphics.style(),
	    mode);
    }
	
    //
    // Bitmap
    //
	
    if (get_attribute (key, GT_Keys::option_bitmap, code, found)) {
	code = GT_Tcl::format_value (result, GT_Keys::option_bitmap,
	    graphics.bitmap(),
	    mode);
    }

    if (get_attribute (key, GT_Keys::option_foreground, code, found)) {
	code = GT_Tcl::format_value (result, GT_Keys::option_foreground,
	    graphics.foreground(),
	    mode);
    }
	
    if (get_attribute (key, GT_Keys::option_background, code, found)) {
	code = GT_Tcl::format_value (result, GT_Keys::option_background,
	    graphics.background(),
	    mode);
    }
	
    //
    // Image
    //
	
    if (get_attribute (key, GT_Keys::option_image, code, found)) {
	code = GT_Tcl::format_value (result, GT_Keys::option_image,
	    graphics.image(),
	    mode);
    }

    //
    // Line
    //
	
    if (get_attribute (key, GT_Keys::option_arrow, code, found)) {
	code = GT_Tcl::format_value (result, GT_Keys::option_arrow,
	    graphics.arrow(),
	    mode);
    }

    if (get_attribute (key, GT_Keys::option_arrowshape, code, found)) {

	list<double> arrowshape;
	arrowshape.push_back (graphics.arrowshape_touching_length());
	arrowshape.push_back (graphics.arrowshape_overall_length());
	arrowshape.push_back (graphics.arrowshape_width());

 	code = GT_Tcl::format_value (result, GT_Keys::option_arrowshape,
 	    GT_Tcl::tcl (arrowshape),
 	    mode);
    }

    if (get_attribute (key, GT_Keys::option_arrowshape_touching_length,
	code, found)) {
	code = GT_Tcl::format_value (result,
	    GT_Keys::option_arrowshape_touching_length,
	    graphics.arrowshape_touching_length(),
	    mode);
    }

    if (get_attribute (key, GT_Keys::option_arrowshape_overall_length,
	code, found)) {
	code = GT_Tcl::format_value (result,
	    GT_Keys::option_arrowshape_overall_length,
	    graphics.arrowshape_overall_length(),
	    mode);
    }

    if (get_attribute (key, GT_Keys::option_arrowshape_width, code, found)) {
	code = GT_Tcl::format_value (result, GT_Keys::option_arrowshape_width,
	    graphics.arrowshape_width(),
	    mode);
    }

    if (get_attribute (key, GT_Keys::option_capstyle, code, found)) {
	code = GT_Tcl::format_value (result, GT_Keys::option_capstyle ,
	    graphics.capstyle(),
	    mode);
    }

    if (get_attribute (key, GT_Keys::option_joinstyle, code, found)) {
	code = GT_Tcl::format_value (result, GT_Keys::option_joinstyle,
	    graphics.joinstyle(),
	    mode);
    }


    //
    // Polygon
    //

    if (get_attribute (key, GT_Keys::option_smooth, code, found)) {
	code = GT_Tcl::format_value (result,  GT_Keys::option_smooth,
	    graphics.smooth(),
	    mode);
    }
	
    if (get_attribute (key, GT_Keys::option_splinesteps, code, found)) {
	code = GT_Tcl::format_value (result, GT_Keys::option_splinesteps,
	    graphics.splinesteps(),
	    mode);
    }

    //
    // Text
    //
	
    if (get_attribute (key, GT_Keys::option_justify, code, found)) {
	code = GT_Tcl::format_value (result, GT_Keys::option_justify,
	    graphics.justify(),
	    mode);
    }

    if (get_attribute (key, GT_Keys::option_font, code, found)) {
	code = GT_Tcl::format_value (result, GT_Keys::option_font,
	    graphics.font(),
	    mode);
    }

    if (get_attribute (key, GT_Keys::option_font_size, code, found)) {
	code = GT_Tcl::format_value (result, GT_Keys::option_font_size,
	    graphics.font_size(),
	    mode);
    }

    if (get_attribute (key, GT_Keys::option_font_style, code, found)) {
	code = GT_Tcl::format_value (result, GT_Keys::option_font_style,
	    graphics.font_style(),
	    mode);
    }

    //
    // Line
    //
	
    if (get_attribute (key, GT_Keys::option_line, code, found)) {

	Tcl_DString line_string;
	Tcl_DStringInit (&line_string);

	GT_Point p;
	list<GT_Point>::const_iterator it;
	list<GT_Point>::const_iterator end = graphics.line().end();
	for (it = graphics.line().begin(); it != end; ++it)
	{
	    p = *it;
	    char buffer [GT_Tcl::double_string_length];
	    sprintf (buffer, "%g", p.x());
	    Tcl_DStringAppendElement (&line_string, buffer);
	    sprintf (buffer, "%g", p.y());
	    Tcl_DStringAppendElement (&line_string, buffer);
	}

	if (mode.is_configure()) {
	    Tcl_DString tmp;
	    Tcl_DStringInit (&tmp);
	    Tcl_DStringAppend (&tmp, GT_Keys::option_line.name().c_str(),
		GT_Keys::option_line.name().length());
	    Tcl_DStringAppendElement (&tmp, 
		Tcl_DStringValue(&line_string));
	    Tcl_DStringAppendElement (&result, Tcl_DStringValue (&tmp));
	    Tcl_DStringFree (&tmp);
	} else {
	    Tcl_DStringAppend (&result,
		Tcl_DStringValue (&line_string),
		Tcl_DStringLength (&line_string));
	}

	Tcl_DStringFree (&line_string);

	code = TCL_OK;
    }

    return code;
}
