/* This software is distributed under the Lesser General Public License */
//
// Tk_UINode.cc
//
// This file implements the class GT_Tk_UINode.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_tcl/Tk_UILabel.cpp,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:46:38 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet Project
//

#include "Tcl_Graph.h"

#include "Tk_UILabel.h"


//////////////////////////////////////////
//
// Constructors and Destructors
//
//////////////////////////////////////////


GT_Tk_UILabel::GT_Tk_UILabel (GT_Tk_Device* device) :
	GT_Tk_UIObject (device)
{
}		


GT_Tk_UILabel::~GT_Tk_UILabel ()
{
}



//////////////////////////////////////////
//
// id
//
//////////////////////////////////////////


int GT_Tk_UILabel::id () const
{
    return attrs()->id();
}



//////////////////////////////////////////
//
// make_create_cmd
//
//////////////////////////////////////////


void GT_Tk_UILabel::make_update_coords_cmd (string& cmd,
    bool /* may_move_or_scale */,
    bool force)
{
    const GT_Common_Graphics* cg = graphics();

    if (!cg->is_changed (GT_Common_Graphics::tag_geometry) && !force) {
	cmd = "";
	return;
    }
    
    const int x = int(device()->translate_x (graphics()->x()));
    const int y = int(device()->translate_y (graphics()->y()));
    
    cmd += GT::format ("%d %d", x,y);
}



void GT_Tk_UILabel::make_create_cmd (string& cmd)
{
    if (!graphics()->is_initialized (GT_Common_Graphics::tag_type)) {
	graphics()->type (GT_Keys::type_text);
    }
	
    baseclass::make_create_cmd (cmd);

    return;
}



void GT_Tk_UILabel::make_tags_cmd (string& cmd,
    bool /* force */)
{
    cmd = GT::format("%s GT:%d GT:%d GT_text",
	type().name().c_str(),
	attrs()->id(),
	graphics()->uid());
}


void GT_Tk_UILabel::make_update_attrs_cmd (string& cmd,
    bool force)
{
    baseclass::make_update_attrs_cmd (cmd, force);

    if (attrs() != 0 &&
	attrs()->is_changed (GT_Common_Attributes::tag_label)) {
	
	const string& label = attrs()->label();
    
	char* newstring = new char [
	    label.length()*2 + 1 + strlen(" -text {}")];
	strcpy (newstring, " -text \"");
	char* n = newstring + strlen(" -text \"");
	
	const char* c = label.c_str();
	
	while (*c != '\0') {
	    if (*c == '$' || *c == '\\' || *c == '"' ||
		*c == '{' || *c == '}' ||
		*c == '[' || *c == ']') {
		*n++ = '\\';
	    }
	    *n++ = *c;
	    c++;
	}
	*n++ = '"';
	*n++ = '\0';
	    
	cmd += newstring;
	delete newstring;
    }
}


//////////////////////////////////////////
//
// Marks
//
//////////////////////////////////////////


void GT_Tk_UILabel::make_update_mark_cmd (string& /* cmd */,
    const string& /* selection */,
    Marker_type /* type */)
{
    // NOT USED
}


void GT_Tk_UILabel::make_mark_cmd (string& /* cmd */,
    const string& /* selection */,
    Marker_type /* type */)
{
    // NOT USED
}



//////////////////////////////////////////
//
// Graph Label
//
//////////////////////////////////////////


GT_Tk_UIGraphlabel::GT_Tk_UIGraphlabel (GT_Tk_Device* device, GT_Graph& g) :
	GT_Tk_UILabel (device),
	the_graph_attrs (&(g.gt()))
{
}


GT_Tk_UIGraphlabel::~GT_Tk_UIGraphlabel ()
{
}


const GT_Key& GT_Tk_UIGraphlabel::type() const
{
    return GT_Keys::uiobject_graph_label;
}


GT_Common_Attributes* GT_Tk_UIGraphlabel::attrs() const
{
    return the_graph_attrs;
}


GT_Common_Graphics* GT_Tk_UIGraphlabel::graphics() const
{
    return the_graph_attrs->label_graphics();
}



//////////////////////////////////////////
//
// Node Label
//
//////////////////////////////////////////


GT_Tk_UINodelabel::GT_Tk_UINodelabel (GT_Tk_Device* device,
    GT_Graph& g, const node n) :
	GT_Tk_UILabel (device),
	the_node_attrs (&(g.gt(n)))
{
}


GT_Tk_UINodelabel::~GT_Tk_UINodelabel ()
{
}


const GT_Key& GT_Tk_UINodelabel::type() const
{
    return GT_Keys::uiobject_node_label;
}


GT_Common_Attributes* GT_Tk_UINodelabel::attrs() const
{
    return the_node_attrs;
}


GT_Common_Graphics* GT_Tk_UINodelabel::graphics() const
{
    return the_node_attrs->label_graphics();
}



//////////////////////////////////////////
//
// Edge Label
//
//////////////////////////////////////////


GT_Tk_UIEdgelabel::GT_Tk_UIEdgelabel (GT_Tk_Device* device,
    GT_Graph& g, const edge e) :
	GT_Tk_UILabel (device),
	the_edge_attrs (&(g.gt(e)))
{
}


GT_Tk_UIEdgelabel::~GT_Tk_UIEdgelabel ()
{
}


const GT_Key& GT_Tk_UIEdgelabel::type() const
{
    return GT_Keys::uiobject_edge_label;
}


GT_Common_Attributes* GT_Tk_UIEdgelabel::attrs() const
{
    return the_edge_attrs;
}


GT_Common_Graphics* GT_Tk_UIEdgelabel::graphics() const
{
    return the_edge_attrs->label_graphics();
}
