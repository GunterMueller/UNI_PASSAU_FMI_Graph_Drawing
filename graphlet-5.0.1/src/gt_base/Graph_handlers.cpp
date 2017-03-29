/* This software is distributed under the Lesser General Public License */
//
// Graph_handlers.cpp
//
// This module implements the handlers in the class GT_Graph.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Graph_handlers.cpp,v $
// $Author: himsolt $
// $Revision: 1.4 $
// $Date: 1999/06/24 11:13:05 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, Graphlet Project
//

#include "Graphlet.h"
#include "Graph.h"
#include "GTL_Shuttle.h"
#include "Device.h"
#include "UIObject.h"
#include "NEI.h"



//////////////////////////////////////////
//
// GT_Graph::pre_new_graph_handler ()
// GT_Graph::post_new_graph_handler ()
// GT_Graph::pre_clear_handler ()
// GT_Graph::post_clear_handler ()
//
//////////////////////////////////////////


void GT_Graph::pre_new_graph_handler ()
{
}


void GT_Graph::post_new_graph_handler ()
{
}


void GT_Graph::pre_clear_handler ()
{
    //
    // call node, edge handlers
    //

    graph& g = gtl();

    edge e;
    forall_edges (e, g) {
	pre_del_edge_handler (e);
    }
	
    node n;
    forall_nodes (n, g) {
	pre_del_node_handler (n);
    }

    int uid = the_graph_attrs->graphics()->uid();
    int label_uid = the_graph_attrs->label_graphics()->uid();

    for(list<GT_Device*>::const_iterator it = the_devices.begin();
	it != the_devices.end(); ++it)
    {
	GT_Device* device = *it;

	device->del_full (the_graph_attrs->id());

	GT_UIObject* uiobject;
	uiobject = device->get (uid);
	if (uiobject != 0) {
	    delete uiobject;
	    device->del (uid);
	}
	
	uiobject = device->get (label_uid);
	if (uiobject != 0) {
	    delete uiobject;
	    device->del (label_uid);
	}
    }

    if (the_graph_attrs != 0) {

	the_graph_attrs->clear();

	if (the_graph_attrs->graphics() == 0) {
	    the_graph_attrs->graphics (0);
	    the_graph_attrs->label_graphics (0);
	}

	if (the_graph_attrs->label_graphics() == 0) {
	    the_graph_attrs->graphics (new_graph_graphics());
	    the_graph_attrs->label_graphics (new_graph_label_graphics());
	}
    }
}


void GT_Graph::post_clear_handler ()
{
}



//////////////////////////////////////////
//
// void GT_Graph::pre_new_node_handler ()
// void GT_Graph::post_new_node_handler (node n)
//
//////////////////////////////////////////


void GT_Graph::pre_new_node_handler ()
{
}


void GT_Graph::post_new_node_handler (node n)
{
    if (new_node_attributes_template != 0) {

	GT_List_of_Attributes* cloned;
//	if (new_node_attributes_template->g() != 0 &&
//	    new_node_attributes_template->g()->attached() != &(gtl())) {
	if (new_node_attributes_template->g() != 0) {
	    cloned = new_node_attributes_template->clone (
		GT_Copy (GT_Copy::deep));
	} else {
	    cloned = new_node_attributes_template->clone (
		GT_Copy (GT_Copy::deep_from_parent));
	}
	attrs (n, (GT_Node_Attributes*)cloned);

    } else {
	attrs (n, new_node_attributes ());
    }
}


//////////////////////////////////////////
//
// void GT_Graph::pre_new_edge_handler (node n1, node n2)
// void GT_Graph::post_new_edge_handler (edge e)
//
//////////////////////////////////////////


void GT_Graph::pre_new_edge_handler (node /* n1 */, node /* n2 */)
{
}


void GT_Graph::post_new_edge_handler (edge e)
{
    if (new_edge_attributes_template != 0) {

	GT_List_of_Attributes* cloned;
//	if (new_edge_attributes_template->g() != 0 &&
//	    new_edge_attributes_template->g()->attached() != &(gtl())) {
	if (new_edge_attributes_template->g() != 0) {
	    cloned = new_edge_attributes_template->clone (
		GT_Copy (GT_Copy::deep));
	} else {
	    cloned = new_edge_attributes_template->clone (
		GT_Copy (GT_Copy::deep_from_parent));
	}
	attrs (e, (GT_Edge_Attributes*)cloned);

    } else {
	attrs (e, new_edge_attributes());
    }
}



//////////////////////////////////////////
//
// void GT_Graph::pre_del_node_handler ()
// void GT_Graph::post_del_node_handler (node n)
//
//////////////////////////////////////////


void GT_Graph::pre_del_node_handler (node n)
{
    GT_Node_Attributes& attrs = *(the_node_attrs[n]);

    //
    // Clean up in devices
    //

    int node_uid = attrs.graphics()->uid();
    int label_uid = attrs.label_graphics()->uid();

    for(list<GT_Device*>::const_iterator it = the_devices.begin();
	it != the_devices.end(); ++it)
    {
	GT_Device* device = *it;

	device->del_full (attrs.id());

	GT_UIObject* uiobject;
	uiobject = device->get (node_uid);
	if (uiobject != 0) {
	    delete uiobject;
	    device->del (node_uid);
	}

	uiobject = device->get (label_uid);
	if (uiobject != 0) {
	    delete uiobject;
	    device->del (label_uid);
	}
    }

    if (the_node_attrs[n] != 0) {
	delete the_node_attrs[n];
	the_node_attrs[n] = 0;
    }
}


void GT_Graph::post_del_node_handler ()
{
}



//////////////////////////////////////////
//
// void GT_Graph::pre_del_edge_handler (edge e)
// void GT_Graph::post_del_edge_handler (node, node)
//
//////////////////////////////////////////


void GT_Graph::pre_del_edge_handler (edge e)
{
    GT_Edge_Attributes& attrs = *(the_edge_attrs[e]);

    int edge_uid = attrs.graphics()->uid();
    int label_uid = attrs.label_graphics()->uid();

    for(list<GT_Device*>::const_iterator it = the_devices.begin();
	it != the_devices.end(); ++it)
    {
	GT_Device* device = *it;

	device->del_full (attrs.id());

	GT_UIObject* uiobject;
	uiobject = device->get (edge_uid);
	if (uiobject != 0) {
	    delete uiobject;
	    device->del (edge_uid);
	}
	
	uiobject = device->get (label_uid);
	if (uiobject != 0) {
	    delete uiobject;
	    device->del (label_uid);
	}
    }

    if (the_edge_attrs[e] != 0) {
	delete the_edge_attrs[e];
	the_edge_attrs[e] = 0;
    }
}


void GT_Graph::post_del_edge_handler (node /* source */, node /* target */)
{
}



//////////////////////////////////////////
//
// void GT_Graph::pre_move_edge_handler (edge, node, node)
// void GT_Graph::post_move_edge_handler (edge, node, node)
//
//////////////////////////////////////////


void GT_Graph::pre_move_edge_handler (edge,node,node)
{
}


void GT_Graph::post_move_edge_handler (edge,node,node)
{
}



//////////////////////////////////////////
//
// void GT_Graph::pre_hide_edge_handler (edge)
// void GT_Graph::post_hide_edge_handler (edge)
//
//////////////////////////////////////////


void GT_Graph::pre_hide_edge_handler (edge)
{
}


void GT_Graph::post_hide_edge_handler (edge)
{
}


void GT_Graph::pre_restore_edge_handler (edge)
{
}


void GT_Graph::post_restore_edge_handler (edge)
{
}



//////////////////////////////////////////
//
// void GT_Graph::touch (node, const string&)
// void GT_Graph::touch (edge, const string&)
//
// void GT_Graph::comment (const string&)
//
// bool GT_Graph::query ()
// bool GT_Graph::query (node, node)
// bool GT_Graph::query (edge)
//
//////////////////////////////////////////


void GT_Graph::touch (node /* n */, const string& /* s */)
{
}


void GT_Graph::touch (edge /* e */, const string& /* s */)
{
}


void GT_Graph::comment (const string& /* s */)
{
}


bool GT_Graph::query ()
{
    return true;
}


bool GT_Graph::query (node /* n1 */, node /* n2 */)
{
    return true;
}


bool GT_Graph::query (edge /* e */)
{
    return true;
}
