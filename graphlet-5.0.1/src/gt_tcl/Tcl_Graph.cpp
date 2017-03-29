/* This software is distributed under the Lesser General Public License */
//
// GT_Tcl_Graph.h
// 
// This module implements the class GT_Tcl_Graph
//
// $Source: /home/br/CVS/graphlet/src/gt_tcl/Tcl_Graph.cpp,v $
// $Author: himsolt $
// $Revision: 1.10 $
// $Date: 1999/07/25 10:48:21 $
// $Locker:  $
// $State: Exp $
// ----------------------------------------------------------------------
//
// (C) University of Passau 1995-1999, Graphlet Project
//

#include <tk.h> // Tk version

#include "Tcl_Graph.h"

#include <fstream> // graph printing I/O operations
#include <iostream>

#include <gt_base/Id.h>
#include <gt_base/GTL_Shuttle.h>
#include <gt_base/Parser.h>

#include "Graphscript.h"
#include "Tcl_Command.h"
#include "Tk_UIGraph.h"
#include "Tk_UILabel.h"


//////////////////////////////////////////
//
// Constructor
//
//////////////////////////////////////////

GT_Tcl_Graph::GT_Tcl_Graph () :
	the_graphscript (0),
	the_hook_return_code (TCL_OK)
{
    static struct {
	GT_Key* key; const char* value;
    } keys_init[] = {

	{ &GT_Tcl_Graph::pre_new_graph_hook, "pre_new_graph_hook" },
	{ &GT_Tcl_Graph::post_new_graph_hook, "post_new_graph_hook" },
	{ &GT_Tcl_Graph::pre_new_node_hook, "pre_new_node_hook" },
	{ &GT_Tcl_Graph::post_new_node_hook, "post_new_node_hook" },
	{ &GT_Tcl_Graph::pre_new_edge_hook, "pre_new_edge_hook" },
	{ &GT_Tcl_Graph::post_new_edge_hook, "post_new_edge_hook" },

	{ &GT_Tcl_Graph::pre_delete_graph_hook, "pre_delete_graph_hook" },
	{ &GT_Tcl_Graph::post_delete_graph_hook, "post_delete_graph_hook" },
	{ &GT_Tcl_Graph::pre_delete_node_hook, "pre_delete_node_hook" },
	{ &GT_Tcl_Graph::post_delete_node_hook, "post_delete_node_hook" },
	{ &GT_Tcl_Graph::pre_delete_edge_hook, "pre_delete_edge_hook" },
	{ &GT_Tcl_Graph::post_delete_edge_hook, "post_delete_edge_hook" },

	{ &GT_Tcl_Graph::pre_copy_graph_hook, "pre_copy_graph_hook" },
	{ &GT_Tcl_Graph::post_copy_graph_hook, "post_copy_graph_hook" },
	{ &GT_Tcl_Graph::pre_copy_node_hook, "pre_copy_node_hook" },
	{ &GT_Tcl_Graph::post_copy_node_hook, "post_copy_node_hook" },
	{ &GT_Tcl_Graph::pre_copy_edge_hook, "pre_copy_edge_hook" },
	{ &GT_Tcl_Graph::post_copy_edge_hook, "post_copy_edge_hook" },

	{ &GT_Tcl_Graph::pre_configure_graph_hook,
	  "pre_configure_graph_hook" },
	{ &GT_Tcl_Graph::post_configure_graph_hook,
	  "post_configure_graph_hook" },
	{ &GT_Tcl_Graph::pre_configure_node_hook,
	  "pre_configure_node_hook" },
	{ &GT_Tcl_Graph::post_configure_node_hook,
	  "post_configure_node_hook" },
	{ &GT_Tcl_Graph::pre_configure_edge_hook,
	  "pre_configure_edge_hook" },
	{ &GT_Tcl_Graph::post_configure_edge_hook,
	  "post_configure_edge_hook" },
	{ &GT_Tcl_Graph::pre_configure_style_hook,
	  "pre_configure_style_hook" },
	{ &GT_Tcl_Graph::post_configure_style_hook,
	  "post_configure_style_hook" },

	{ &GT_Tcl_Graph::pre_directed_hook, "pre_directed_hook" },
	{ &GT_Tcl_Graph::post_directed_hook, "post_directed_hook" },

	{ &GT_Tcl_Graph::pre_canvas_hook, "pre_canvas_hook" },
	{ &GT_Tcl_Graph::post_canvas_hook, "post_canvas_hook" },
	{ &GT_Tcl_Graph::pre_editor_hook, "pre_editor_hook" },
	{ &GT_Tcl_Graph::post_editor_hook, "post_editor_hook" },

// 	{ &GT_Tcl_Graph::pre_configure_hook, "pre_configure_hook" },
// 	{ &GT_Tcl_Graph::post_configure_hook, "post_configure_hook" },
// 	{ &GT_Tcl_Graph::pre_get_hook, "pre_get_hook" },
// 	{ &GT_Tcl_Graph::post_get_hook, "post_get_hook" },
// 	{ &GT_Tcl_Graph::pre_set_hook, "pre_set_hook" },
// 	{ &GT_Tcl_Graph::post_set_hook, "post_set_hook" },

	{ &GT_Tcl_Graph::pre_draw_hook, "pre_draw_hook" },
	{ &GT_Tcl_Graph::post_draw_hook, "post_draw_hook" },

	{ &GT_Tcl_Graph::pre_save_hook, "pre_save_hook" },
	{ &GT_Tcl_Graph::post_save_hook, "post_save_hook" },
	{ &GT_Tcl_Graph::pre_load_hook, "pre_load_hook" },
	{ &GT_Tcl_Graph::post_load_hook, "post_load_hook" },

	{ &GT_Tcl_Graph::pre_scale_hook, "pre_scale_hook" },
	{ &GT_Tcl_Graph::post_scale_hook, "post_scale_hook" },

// 	{ &GT_Tcl_Graph::pre_nodemove_hook, "pre_nodemove_hook" },
// 	{ &GT_Tcl_Graph::post_nodemove_hook, "post_nodemove_hook" },

// 	{ &GT_Tcl_Graph::pre_mark_hook, "pre_mark_hook" },
// 	{ &GT_Tcl_Graph::post_mark_hook, "post_mark_hook" },

	{ &GT_Tcl_Graph::pre_style_hook, "pre_style_hook" },
	{ &GT_Tcl_Graph::post_style_hook, "post_style_hook" },

	{ 0, 0 },
    };

    for (int i=0; keys_init[i].key != 0; i++) {
	*(keys_init[i].key) = graphlet->keymapper.add (keys_init[i].value);
    }

}


//
// GT_Tcl_Graph related keys
//

GT_Key GT_Tcl_Graph::pre_new_graph_hook;
GT_Key GT_Tcl_Graph::post_new_graph_hook;
GT_Key GT_Tcl_Graph::pre_new_node_hook;
GT_Key GT_Tcl_Graph::post_new_node_hook;
GT_Key GT_Tcl_Graph::pre_new_edge_hook;
GT_Key GT_Tcl_Graph::post_new_edge_hook;

GT_Key GT_Tcl_Graph::pre_delete_graph_hook;
GT_Key GT_Tcl_Graph::post_delete_graph_hook;
GT_Key GT_Tcl_Graph::pre_delete_node_hook;
GT_Key GT_Tcl_Graph::post_delete_node_hook;
GT_Key GT_Tcl_Graph::pre_delete_edge_hook;
GT_Key GT_Tcl_Graph::post_delete_edge_hook;

GT_Key GT_Tcl_Graph::pre_copy_graph_hook;
GT_Key GT_Tcl_Graph::post_copy_graph_hook;
GT_Key GT_Tcl_Graph::pre_copy_node_hook;
GT_Key GT_Tcl_Graph::post_copy_node_hook;
GT_Key GT_Tcl_Graph::pre_copy_edge_hook;
GT_Key GT_Tcl_Graph::post_copy_edge_hook;

GT_Key GT_Tcl_Graph::pre_configure_graph_hook;
GT_Key GT_Tcl_Graph::post_configure_graph_hook;
GT_Key GT_Tcl_Graph::pre_configure_node_hook;
GT_Key GT_Tcl_Graph::post_configure_node_hook;
GT_Key GT_Tcl_Graph::pre_configure_edge_hook;
GT_Key GT_Tcl_Graph::post_configure_edge_hook;
GT_Key GT_Tcl_Graph::pre_configure_style_hook;
GT_Key GT_Tcl_Graph::post_configure_style_hook;

GT_Key GT_Tcl_Graph::pre_directed_hook;
GT_Key GT_Tcl_Graph::post_directed_hook;

GT_Key GT_Tcl_Graph::pre_canvas_hook;
GT_Key GT_Tcl_Graph::post_canvas_hook;
GT_Key GT_Tcl_Graph::pre_editor_hook;
GT_Key GT_Tcl_Graph::post_editor_hook;

// GT_Key GT_Tcl_Graph::pre_configure_hook;
// GT_Key GT_Tcl_Graph::post_configure_hook;
// GT_Key GT_Tcl_Graph::pre_get_hook;
// GT_Key GT_Tcl_Graph::post_get_hook;
// GT_Key GT_Tcl_Graph::pre_set_hook;
// GT_Key GT_Tcl_Graph::post_set_hook;

GT_Key GT_Tcl_Graph::pre_draw_hook;
GT_Key GT_Tcl_Graph::post_draw_hook;

GT_Key GT_Tcl_Graph::pre_save_hook;
GT_Key GT_Tcl_Graph::post_save_hook;
GT_Key GT_Tcl_Graph::pre_load_hook;
GT_Key GT_Tcl_Graph::post_load_hook;

GT_Key GT_Tcl_Graph::pre_scale_hook;
GT_Key GT_Tcl_Graph::post_scale_hook;

// GT_Key GT_Tcl_Graph::pre_nodemove_hook;
// GT_Key GT_Tcl_Graph::post_nodemove_hook;

// GT_Key GT_Tcl_Graph::pre_mark_hook;
// GT_Key GT_Tcl_Graph::post_mark_hook;

GT_Key GT_Tcl_Graph::pre_style_hook;
GT_Key GT_Tcl_Graph::post_style_hook;


//////////////////////////////////////////
//
// Destructor
//
//////////////////////////////////////////

GT_Tcl_Graph::~GT_Tcl_Graph ()
{
}


//////////////////////////////////////////
//
// GT_Tcl_Graph::cmd
//
// Tcl handler for the command "graph".
//
//////////////////////////////////////////


int GT_Tcl_Graph::cmd  (ClientData client_data,
    Tcl_Interp* interp,
    int argc,
    char** argv)
{
    int code = TCL_OK;
    code = run_hooks (interp, code, "", "pre_new_graph_hook");
    if (code == TCL_ERROR) {
	return code;
    }

    //
    // Create a new GT_Tcl_Graph
    //
	
    GT_Tcl_Graph* g = new GT_Tcl_Graph;
    GT_Shuttle* gt_gtl_shuttle = new GT_GTL_Shuttle<graph>;
    g->attach (*gt_gtl_shuttle);
    g->the_graphscript = (GT_Graphscript*)client_data;
    g->new_graph();
    
    //
    // Create a new Tcl command
    //
    
    GT_Tcl_Graph_Command* command = new GT_Tcl_Graph_Command (
	GT::format ("GT:%d", g->gt().id()),
	*g);
    code = command->install (interp);

    // If more arguments follow, treat them like in the configure command
    if (argc > 1 && code != TCL_ERROR) {
	GT_Tcl_info info (interp, argc, argv);
	GT_Tcl::Configure_Mode m;
	m.configure();
	code = g->configure_cmd (info, 1, m);
    }

    if (code != TCL_ERROR) {
	code = g->run_hooks (post_new_graph_hook);
    }

    if (code != TCL_ERROR) {
	char graph_name [GT_Tcl::id_length];
	sprintf (graph_name, "GT:%d", g->gt().id());
	Tcl_SetResult (interp, graph_name, TCL_VOLATILE);
    }

    return code;
}


//////////////////////////////////////////
//
// GT_Tcl_Graph::new_graph
//
//////////////////////////////////////////


void GT_Tcl_Graph::new_graph ()
{
    pre_new_graph_handler ();
    post_new_graph_handler ();
}



//////////////////////////////////////////
//
// static_parser
//
// This is just a wrapper for GT_Tcl_Graph::parser
//
//////////////////////////////////////////


int GT_Tcl_Graph::static_parser (ClientData client_data,
    Tcl_Interp*  interp,
    int argc,
    char** argv)
{
    GT_Tcl_Graph* g = ((GT_Tcl_Graph_Command*)client_data)->get_tcl_graph();

    if (g == 0) {	
	string err (graphlet->error.msg (GT_Error::no_graph, argv[0]));
	Tcl_SetResult (interp, const_cast<char*>(err.c_str()), TCL_VOLATILE);
	return TCL_ERROR;
    } else {
	return g->parser (interp, argc, argv);
    }
}


//////////////////////////////////////////
//
// GT_Tcl_Graph::parser dispatches the command line arguments.
//
//////////////////////////////////////////


int GT_Tcl_Graph::parser (Tcl_Interp* interp, int argc, char** argv)
{
    if (argc < 2) {
	string err (graphlet->error.msg (GT_Error::wrong_number_of_args));
	Tcl_SetResult (interp, const_cast<char*>(err.c_str()), TCL_VOLATILE);
	return TCL_ERROR;
    }
		
    //
    // Initialize
    //
		
    GT_Tcl_info info (interp, argc, argv);

    // Initialize return code from hooks and handlers

    the_hook_return_code = TCL_OK;

    //
    // Parse the arguments
    //
	
    argc = 1;
    char* command = argv[argc++];

    int code = TCL_ERROR;
    if (strcmp (command, "create") == 0) {

	if (info.args_left (argc) >= 0) {

	    char* next_command = argv[argc++];
		
	    if (strcmp (next_command, "node") == 0) {
		code = create_node_cmd (info, argc);
	    } else if (strcmp (next_command, "edge") == 0) {
		code = create_edge_cmd (info, argc);
	    } else if (strcmp (next_command, "style") == 0) {
		code = new_style_cmd (info, argc);
	    } else {
		info.msg (GT_Error::no_command, next_command);
		code = TCL_ERROR;
	    }
			
	} else {
	    info.msg (GT_Error::wrong_number_of_args);
	    code = TCL_ERROR;
	}
    } else if (strcmp (command, "new_node") == 0) {
	code = create_node_cmd (info, argc);
    } else if (strcmp (command, "new_edge") == 0) {
	code = create_edge_cmd (info, argc);
    } else if (strcmp (command, "new_style") == 0) {
	code = new_style_cmd (info, argc);

    } else if (strcmp (command, "copy") == 0) {
	code = copy_cmd (info, argc);
    } else if (strcmp (command, "copynode") == 0) {
	code = copy_node_cmd (info, argc);
    } else if (strcmp (command, "copyedge") == 0) {
	code = copy_edge_cmd (info, argc);
	
    } else if (strcmp (command, "canvas") == 0 ||
	strcmp (command, "canvases") == 0) {
	code = canvas_cmd (info, argc);
    } else if (strcmp (command, "editor") == 0 ||
	strcmp (command, "editors") == 0) {
	code = editor_cmd (info, argc);
    } else if (strcmp (command, "configure") == 0) {
	GT_Tcl::Configure_Mode m;
	m.configure();
	code = configure_cmd (info, argc, m);
    } else if (strcmp (command, "set") == 0) {
	GT_Tcl::Configure_Mode m;
	m.set();
	code = configure_cmd (info, argc, m);
    } else if (strcmp (command, "get") == 0) {
	GT_Tcl::Configure_Mode m;
	m.get();
	code = configure_cmd (info, argc, m);
    } else if (strcmp (command, "draw") == 0) {
	code = draw_cmd (info, argc);
    } else if (strcmp (command, "print") == 0) {
	code = save_cmd (info, argc);
    } else if (strcmp (command, "save") == 0) {
	code = save_cmd (info, argc);
    } else if (strcmp (command, "load") == 0) {
	code = load_cmd (info, argc);
    } else if (strcmp (command, "delete") == 0) {
	code = delete_cmd (info, argc);
    } else if (strcmp (command, "scale") == 0) {
	code = scale_cmd (info, argc);
    } else if (strcmp (command, "nodemove") == 0) {
	code = nodemove_cmd (info, argc);
    } else if (strcmp (command, "nodes") == 0) {
	code = nodes_cmd (info, argc);
    } else if (strcmp (command, "edges") == 0) {
	code = edges_cmd (info, argc);
    } else if (strcmp (command, "bbox") == 0) {
	code = bbox_cmd (info, argc);
    } else if (strcmp (command, "isnode") == 0) {
	code = isnode_cmd (info, argc);
    } else if (strcmp (command, "isedge") == 0) {
	code = isedge_cmd (info, argc);
    } else if (strcmp (command, "typeof") == 0) {
	code = typeof_cmd (info, argc);
    } else if (strcmp (command, "translate") == 0) {
	code = translate_cmd (info, argc);
    } else if (strcmp (command, "mark") == 0) {
	code = mark_cmd (info, argc);
    } else if (strcmp (command, "style") == 0) {
	code = style_cmd (info, argc);

    } else if (strcmp (command, "graphconfigure") == 0) {
	GT_Tcl::Configure_Mode m;
	m.configure();
	code = configure_cmd (info, argc, m);
    } else if (strcmp (command, "nodeconfigure") == 0) {
	GT_Tcl::Configure_Mode m;
	m.configure();
	code = configure_cmd (info, argc, m);
    } else if (strcmp (command, "nodeget") == 0) {
	GT_Tcl::Configure_Mode m;
	m.get();
	code = configure_cmd (info, argc, m);
    } else if (strcmp (command, "edgeconfigure") == 0) {
	GT_Tcl::Configure_Mode m;
	m.configure();
	code = configure_cmd (info, argc, m);
    } else if (strcmp (command, "edgeget") == 0) {
	GT_Tcl::Configure_Mode m;
	m.get();
	code = configure_cmd (info, argc, m);
    } else if (strcmp (command, "edgedraw") == 0) {
	code = draw_cmd (info, argc);
    } else if (strcmp (command, "nodedraw") == 0) {
	code = draw_cmd (info, argc);
    } else if (strcmp (command, "nodedelete") == 0) {
	code = delete_cmd (info, argc);
    } else if (strcmp (command, "edgedelete") == 0) {
	code = delete_cmd (info, argc);

    } else {		
	info.msg (GT_Error::no_command, argv[1]);
	code = TCL_ERROR;
    }
		
    if (the_hook_return_code != TCL_ERROR) {
	return code;
    } else {
	return TCL_ERROR;
    }
}



//////////////////////////////////////////
//
// GT_Tcl_Graph::canvas_cmd
//
//////////////////////////////////////////


int GT_Tcl_Graph::canvas_cmd (
    GT_Tcl_info& info,
    int argc)
{
    if (info.exists (argc)) {

	list<string> canvas_list;
	int code = GT_Tcl::split_list (info.interp(),
	    info[argc],
	    canvas_list);
	if (code != TCL_OK) {
	    return code;
	}

	code = run_hooks (pre_canvas_hook, canvas_list);
	if (code == TCL_ERROR) {
	    return code;   
	}

	list<GT_Device*> new_devices;
	list<GT_Device*> existing_devices;
	list<GT_Device*> removed_devices;

	list<string>::const_iterator it_can;
	list<string>::const_iterator it_can_end = canvas_list.end();
	string canvas;

	for (it_can = canvas_list.begin(); it_can != it_can_end; ++it_can)
	{
	    canvas = *it_can;

	    //
	    // A canvas can have the form
	    // canvas
	    // { canvas scale }
	    // { canvas scale_x scale_y }
	    //

	    // Split the canvas
	    
	    list<string> canvas_details;
    
	    code = GT_Tcl::split_list (info.interp(), canvas.c_str(),
		canvas_details);
	    if (code != TCL_OK) {
		return code;
	    }

	    // Decode the splitted list
	    
	    string name;
	    double scale_x;
	    double scale_y;
	    bool change_scale;
	    
	    if (canvas_details.size() == 1) {
		
		name = canvas_details.front();
		change_scale = false;
		
	    } else if (canvas_details.size() == 2) {
		
		name = canvas_details.front();

		code = GT_Tcl::get_double (info,
		    (*(++canvas_details.begin())).c_str(), scale_x);
		if (code != TCL_OK) {
		    return code;
		}		
		if (scale_x == 0.0) {
		    scale_x = 1.0;
		}

		scale_y = scale_x;
		
		change_scale = true;
		
	    } else if (canvas_details.size() == 3) {
		
		name = canvas_details.front();

		code = GT_Tcl::get_double (info,
		    (*(++canvas_details.begin())).c_str(), scale_x);
		if (code != TCL_OK) {
		    return code;
		}
		if (scale_x == 0.0) {
		    scale_x = 1.0;
		}
		
		code = GT_Tcl::get_double (info,
		    (*(++(++canvas_details.begin()))).c_str(), scale_y);
		if (code != TCL_OK) {
		    return code;
		}
		if (scale_y == 0.0) {
		    scale_y = 1.0;
		}

		change_scale = true;
		
	    } else {
		info.msg ("Too many arguments for canvas");
		return TCL_ERROR;
	    }

	    // See wether the canvas is already there
	    
	    bool is_already_there = false;

	    list<GT_Device*>::const_iterator it_dev;
	    list<GT_Device*>::const_iterator it_dev_end = the_devices.end();
	    for (it_dev = the_devices.begin(); it_dev != it_dev_end; ++it_dev)
	    {
		if ((*it_dev)->name() == name) {
		    is_already_there = true;
		    break;
		}
	    }

	    GT_Device* device;
	    if (!is_already_there) {
		device = new_tcl_device (name);
		the_devices.push_back (device);
		new_devices.push_back (device);
	    } else {
		device = *it_dev;
		existing_devices.push_back (device);
	    }
	    
	    if (change_scale) {
		device->scale_x (scale_x);
		device->scale_y (scale_y);
	    }
	}
	

	//
	// collect deleted devices
	//

	list<GT_Device*>::const_iterator it_dev;
	list<GT_Device*>::const_iterator it_dev_end = the_devices.end();

	for (it_dev = the_devices.begin(); it_dev != it_dev_end; ++it_dev)
	{
	    GT_Device* device = *it_dev;
	    bool still_exists = false;
	    
	    list<GT_Device*>::const_iterator it_idev;
	    list<GT_Device*>::const_iterator it_idev_end = new_devices.end();

    	    for (it_idev = new_devices.begin(); it_idev != it_idev_end;
		++it_idev)
	    {
		if (*it_idev == device) {
		    still_exists = true;
		    break;
		}
	    }

	    it_idev_end = existing_devices.end();

	    for (it_idev = existing_devices.begin();
		it_idev != it_idev_end; ++it_idev)
	    {
		if (*it_idev == device) {
		    still_exists = true;
		    break;
		}
	    }

	    if (!still_exists) {
		removed_devices.push_back (device);
	    }

	}

	it_dev_end = removed_devices.end();
	for (it_dev = removed_devices.begin(); it_dev != it_dev_end; ++it_dev)
	{
	    list<GT_Device*>::iterator it_idev;
	    list<GT_Device*>::iterator it_idev_end = the_devices.end();

    	    for (it_idev = the_devices.begin(); it_idev != it_idev_end;
		++it_idev)
	    {
		if (*it_idev == *it_dev) {
		    delete *it_idev;
		    it_idev = the_devices.erase(it_idev);
		}
	    }
	}

	code = run_hooks (post_canvas_hook, canvas_list);
	if (code == TCL_ERROR) {
	    return code;   
	}

	return TCL_OK;
		
    } else {
	
	//
	// Get the canvases
	//

	list<string> canvases;
		
        list<GT_Device*>::const_iterator it_dev;
        list<GT_Device*>::const_iterator it_dev_end = the_devices.end();
	GT_Device* device;

        for (it_dev = the_devices.begin(); it_dev != it_dev_end;
    	    ++it_dev)
	{
	    device = *it_dev;

	    list<string> canvas_details;
	    canvas_details.push_back (device->name());
	    canvas_details.push_back (GT::format("%f", device->scale_x()));
	    canvas_details.push_back (GT::format("%f", device->scale_y()));
	    
	    canvases.push_back (GT_Tcl::merge (canvas_details));
//	    canvases += GT_Tcl::merge (canvas_details);
	}
		
	info.msg (GT_Tcl::merge(canvases));
	return TCL_OK;
    }
}


//
// device customization
//

GT_Tk_Device* GT_Tcl_Graph::new_tcl_device (const string& name)
{
    return new GT_Tk_Device (name, the_graphscript);
}



//////////////////////////////////////////
//
// GT_Tcl_Graph::editor_cmd
//
//////////////////////////////////////////


int GT_Tcl_Graph::editor_cmd (
    GT_Tcl_info& info,
    int argc)
{
    char* usage = "editor";

    if (info.exists (argc) && !info.is_last_arg (argc)) {
	info.msg (usage);
	return TCL_ERROR;
    }

    if (info.exists (argc)) {

	list<string> editor_list;
	int code = GT_Tcl::split_list (info.interp(), info[argc],
	    editor_list);
	if (code != TCL_OK) {
	    return code;
	}

	code = run_hooks (pre_editor_hook, editor_list);
	if (code == TCL_ERROR) {
	    return code;   
	}

	the_editors = editor_list;
	the_editors_plain = GT_Tcl::merge (editor_list);

	code = run_hooks (post_editor_hook, editor_list);
	if (code == TCL_ERROR) {
	    return code;   
	}

    } else {	

	info.msg (GT_Tcl::tcl(the_editors));
    }

    return TCL_OK;
}



//////////////////////////////////////////
//
// Copy Graph
//
//////////////////////////////////////////

int GT_Tcl_Graph::copy_cmd (GT_Tcl_info& info, int /* argc */)
{
    int code = run_hooks (pre_copy_graph_hook);
    if (code == TCL_ERROR) {
	return code;
    }

    // Not really implemented
    info.msg ("Not Implemented");
    return TCL_ERROR;

    code = run_hooks (post_copy_graph_hook);
    if (code == TCL_ERROR) {
	return code;
    }
}

//////////////////////////////////////////
//
// GT_Tcl_Graph::save_cmd
// GT_Tcl_Graph::print
//
//////////////////////////////////////////


int GT_Tcl_Graph::save_cmd (
    GT_Tcl_info& info,
    int argc)
{
    if (!info.exists (argc)) {
	info.msg (GT_Error::wrong_number_of_args);
	return TCL_ERROR;
    }

    if (attached() == 0) {
	info.msg (GT_Error::no_graph);
	return TCL_ERROR;				
    }
	
    //
    // Print to file
    //

    string command = info[argc];

    if (command == "-file" || command[0] != '-') {

	string filename;
	if (!info.exists (argc)) {
	    filename = "";
	} else if (info.is_last_arg (argc)) {
	    filename = info[argc];
	} else if (info.is_last_arg (argc+1)) {
	    filename = info[argc+1];
	} else {
	    info.msg (GT_Error::wrong_number_of_args);
	    return TCL_ERROR;
	}

	int code = run_hooks (pre_save_hook,
	    const_cast<char*>(filename.c_str()));
	if (code == TCL_ERROR) {
	    return code;
	}

	if (filename.length() > 0) {

	    ofstream outfile (filename.c_str());
	    if (outfile == 0) {
		info.msg (GT_Error::fileopen_error, filename);
		return TCL_ERROR;
	    }

	    print (outfile);
			
	} else {
	    print (cout);
	}

	code = run_hooks (post_save_hook, const_cast<char*>(filename.c_str()));
	if (code == TCL_ERROR) {
	    return code;
	}

    } else {
	info.msg (GT_Error::wrong_keyword, command);
	return TCL_ERROR;
    }

    return TCL_OK;
}


void GT_Tcl_Graph::print (ostream& out)
{
    out << *this << endl;
    return;
}



//////////////////////////////////////////
//
// GT_Tcl_Graph::load_cmd
//
//////////////////////////////////////////


int GT_Tcl_Graph::load_cmd (
    GT_Tcl_info& info,
    int argc)
{
    if (!info.exists (argc)) {
	info.msg (GT_Error::wrong_number_of_args);
	return TCL_ERROR;
    }

    string command = info[argc++];

    if (command == "-file") {

	if (!info.exists (argc)) {
	    info.msg (GT_Error::wrong_number_of_args);
	    return TCL_ERROR;
	}
		
	char* filename = info[argc];

	if (strlen(filename) > 0) {

	    ifstream infile (filename);
	    if (infile == 0) {
		info.msg (GT_Error::fileopen_error, info[argc]);
		return TCL_ERROR;
	    }
	    infile.close();
			
	    int code = run_hooks (pre_load_hook, filename);
	    if (code == TCL_ERROR) {
		return code;
	    }

	    GT_List_of_Attributes* parsed_attrs;
	    parsed_attrs = graphlet->parser->parser (filename);

	    if (!graphlet->parser->error_while_parsing()) {
		
		if (parsed_attrs != 0) {
		    if (attached() != 0) {
			while (gtl().nodes_begin() != gtl().nodes_end()) {
			    gtl().del_node (*gtl().nodes_begin());
			}
		    }
		    string message;
		    if (extract (parsed_attrs, message) != GT_OK) {
			info.msg (message);
			return TCL_ERROR;
		    }

		} else {
		    info.msg ("Empty file");
		    return TCL_ERROR;
		}
		
	    } else {
		info.msg (graphlet->parser->error_message());
		return TCL_ERROR;
	    }
			
	    code = run_hooks (post_load_hook, filename);
	    if (code == TCL_ERROR) {
		return code;
	    }

	} else {
	    info.msg (GT_Error::no_filename);
	    return TCL_ERROR;
	}

    } else {
	info.msg (GT_Error::wrong_keyword, command);
	return TCL_ERROR;
    }

    return TCL_OK;
}



//////////////////////////////////////////
//
// GT_Tcl_Graph::draw_cmd
// GT_Tcl_Graph::draw
//
//////////////////////////////////////////


int GT_Tcl_Graph::draw_cmd (GT_Tcl_info& info, int argc)
{
    //
    // Optional argument: -force bool
    //
    
    const char* usage = "?-force bool? ?nodes|edges?";

    bool force = false;
    int code = info.parse (argc, "-force", force);
    if (code != TCL_OK) {
	return code;
    }
    
    list<node> nodes;
    list<edge> edges;

    if (info.exists (argc)) {
	code = info.parse (argc, this, nodes, edges);
	if (code != TCL_OK) {
	    return code;
	}
	argc ++;
	if (info.exists (argc)) {
	    info.msg (usage);
	    return TCL_ERROR;
	}
    } else {
	nodes = leda().all_nodes();
	edges = leda().all_edges();
    }

    if ( (!nodes.empty()) || (!edges.empty()) ) {
	code = draw (nodes, edges, force);
    }

    return code;
}



//
// draw (compatibility methods)
//


int GT_Tcl_Graph::draw (const list<node>& nodes, const list<edge>& edges,
    bool force)
{
    //
    // If force is active, then we must reset all "changed" tags
    //

    if (force) {
	node n;
	forall_nodes (n, leda()) {
	    if (gt(n).is_initialized (GT_Common_Attributes::tag_label)) {
		gt(n).set_changed (GT_Common_Attributes::tag_label);
	    }
	    gt(n).graphics()->reset ();
	    gt(n).label_graphics()->reset ();
	}
	edge e;
	forall_edges (e, leda()) {
	    if (gt(e).is_initialized (GT_Common_Attributes::tag_label)) {
		gt(e).set_changed (GT_Common_Attributes::tag_label);
	    }
	    gt(e).graphics()->reset ();
	    gt(e).label_graphics()->reset ();
	}
    }
	
    int code = begin_draw();
    if (code == TCL_ERROR) {
	return code;
    }
    code = baseclass::draw (nodes, edges, force);
    if (code == TCL_ERROR) {
	return code;
    }
    
    code = end_draw();
    if (code == TCL_ERROR) {
	return code;
    }
	
    code = run_hooks (post_draw_hook, nodes, edges);
    if (code == TCL_ERROR) {
	return code;
    }

    return code;
}

int GT_Tcl_Graph::draw (bool force)
{
    return baseclass::draw (force);
}

//
// draw customization
//


GT_UIObject* GT_Tcl_Graph::new_uiobject (GT_Device* device, GT_Graph& g)
{
    return new GT_Tk_UIGraph ((GT_Tk_Device*)device, g);
}



GT_UIObject* GT_Tcl_Graph::new_uiobject_label (GT_Device* device, GT_Graph& g)
{
    return new GT_Tk_UIGraphlabel ((GT_Tk_Device*)device, g);
}



//
// Draw utilities
//


void GT_Tcl_Graph::update ()
{
    baseclass::update();
}


void GT_Tcl_Graph::update_coordinates ()
{
    baseclass::update_coordinates();
}


void GT_Tcl_Graph::update_label ()
{
    baseclass::update_label();
}


void GT_Tcl_Graph::update_label_coordinates ()
{
    baseclass::update_label_coordinates();
}


//////////////////////////////////////////
//
//  int GT_Tcl_Graph::begin_draw ()
//  int GT_Tcl_Graph::end_draw ()
//
//////////////////////////////////////////


int GT_Tcl_Graph::begin_draw ()
{
    return GT_Graph::begin_draw();
}


int GT_Tcl_Graph::end_draw ()
{
    return GT_Graph::end_draw();
}



//
// Delete a graph and all Tcl/Tk objects
//

int GT_Tcl_Graph::delete_cmd (
    GT_Tcl_info& info,
    int argc)
{
    const char* usage = "?nodes|edges?";
    int code = TCL_OK;

    graph& g = leda();
    list<node> nodes;
    list<edge> edges;
    bool delete_the_whole_graph;

    if (info.exists (argc)) {

	delete_the_whole_graph = false;

	code = info.parse (argc, this, nodes, edges);
	if (code != TCL_OK) {
	    return code;
	}

	argc ++;
	if (info.exists (argc)) {
	    info.msg (usage);
	    return TCL_ERROR;
	}

    } else {

	delete_the_whole_graph = true;
    }


    if (delete_the_whole_graph) {

	//
	// Delete all nodes in the graph
	//

	code = run_hooks (pre_delete_graph_hook);
	if (code == TCL_ERROR) {
	    return code;
	}

// 	while (g.number_of_nodes() > 0) {
// 	    g.del_node (g.first_node());
// 	}
	g.clear();
    
	code = run_hooks (post_delete_graph_hook);
	if (code == TCL_ERROR) {
	    return code;
	}

	//
	// Delete the associated Tcl command
	//
		
	Tcl_DeleteCommand (info.interp(), info[0]);

    } else {

	if (!edges.empty()) {

	    code = run_hooks (pre_delete_edge_hook, edges);
	    if (code == TCL_ERROR) {
		return code;
	    }

	    list<edge>::const_iterator it;
	    list<edge>::const_iterator end = edges.end();
	    for (it = edges.begin(); it != end; ++it)
	    {
		g.del_edge (*it);
	    }

	    code = run_hooks (post_delete_edge_hook);
	    if (code == TCL_ERROR) {
		return code;
	    }
	}

	if (!nodes.empty()) {

	    code = run_hooks (pre_delete_node_hook, nodes);
	    if (code == TCL_ERROR) {
		return code;
	    }

	    list<node>::const_iterator it;
	    list<node>::const_iterator end = nodes.end();
	    for (it = nodes.begin(); it != end; ++it)
	    {
		g.del_node (*it);
	    }

	    code = run_hooks (post_delete_node_hook);
	    if (code == TCL_ERROR) {
		return code;
	    }
	}

    }
	
    return code;
}



//////////////////////////////////////////
//
// scale_cmd (double by, vector& origin)
//
//////////////////////////////////////////


int GT_Tcl_Graph::scale_cmd (GT_Tcl_info& info, int argc)
{
    if (info.args_left (argc, 0)) {

	double by;
	int code = Tcl_GetDouble (info.interp(),
	    info[argc],
	    &by);
	if (code == TCL_ERROR) {
	    info.msg (GT_Error::wrong_double_val,
		info[argc+2]);
	    return TCL_ERROR;
	}

	GT_Point origin (0.0, 0.0);

	char by_string [GT_Tcl::double_string_length];
	sprintf (by_string, "%f", by);

	char origin_string [GT_Tcl::double_string_length * 2 + 1];
	sprintf (by_string, "%f %f", origin.x(), origin.y());

	code = run_hooks (pre_scale_hook, by_string, origin_string);
	if (code == TCL_ERROR) {
	    return code;
	}

	scale (by, origin);
	
	code = run_hooks (post_scale_hook, by_string, origin_string);
	if (code == TCL_ERROR) {
	    return code;
	}

    } else {

	info.msg (GT_Error::wrong_number_of_args);
	return TCL_ERROR;

    }
	
     return TCL_OK;
}


int GT_Tcl_Graph::scale (double by, GT_Point& origin)
{
    GT_Graph::scale (by, origin);

    graph& g = leda();
    node n;
    forall_nodes (n, g) {
	gt(n).graphics()->reset_changed (GT_Common_Graphics::tag_geometry);
	gt(n).graphics()->old_center (gt(n).graphics()->center());
    }
    edge e;
    forall_edges (e, g) {
	gt(e).graphics()->reset_changed (GT_Common_Graphics::tag_geometry);
	gt(e).graphics()->old_center (gt(e).graphics()->center());
    }

    return TCL_OK;
}


//////////////////////////////////////////
//
// Return a list of nodes or edges
//
//////////////////////////////////////////


int GT_Tcl_Graph::nodes_cmd (
    GT_Tcl_info& info,
    int argc)
{
    if (!info.exists (argc)) {
		
	info.msg (GT_Tcl::tcl (*this, leda().all_nodes()));
	return TCL_OK;
		
    } else {

	const string command = info[argc++];

	if (command == "-isolated" && !info.exists(argc)) {

	    graph& g = leda();
	    node n;
	    list<node> isolated;
	    forall_nodes (n, g) {
		if (n.degree() == 0) {
		    isolated.push_back (n);
		}
	    }
	    
	    info.msg (GT_Tcl::tcl (*this, isolated));
	    
	    return TCL_OK;
	    
	} else if (command == "-edge" && info.args_left(argc) == 0) {
	    
	    list<edge> edges;
	    int code = info.parse (argc, this, edges);
	    if (code != TCL_OK) {
		return code;
	    }
	    argc ++;
	    
	    list<node> nodes;

	    list<edge>::const_iterator it;
	    list<edge>::const_iterator end = edges.end();

	    for (it = edges.begin(); it != end; ++it)
	    {
		node source = it->source();
		list<node>::iterator nend = nodes.end();
		if ( find(nodes.begin(), nend, source) == nend )
		{
		    nodes.push_back(source);
		}
		node target = it->target();
		if ( find(nodes.begin(), nend, target) == nend )
		{
		    nodes.push_back(target);
		}
	    }

	    info.msg (GT_Tcl::tcl (*this, nodes));

	    return TCL_OK;
	    
	} else if (command == "-opposite" && info.args_left(argc) == 1) {

	    node n;
	    int code = info.parse (argc, this, n);
	    if (code != TCL_OK) {
		return code;
	    }
	    argc ++;
	    
	    edge e;
	    code = info.parse (argc, this, e);
	    if (code != TCL_OK) {
		return code;
	    }
	    argc ++;
	    
	    info.msg (GT::format ("GT:%d", gt(n.opposite(e)).id()));
	    
	    return TCL_OK;
	    
	} else if (command == "-adj" && info.args_left(argc) == 0) {

	    list<node> nodes;
	    int code = info.parse (argc, this, nodes);
	    if (code != TCL_OK) {
		return code;
	    }
	    argc ++;

	    list<node> adj;
	    edge e;
	    node n;
	    const graph& g = leda();
	    if (g.is_directed()) {
		list<node>::const_iterator it_n;
		list<node>::const_iterator end_n = nodes.end();
		for (it_n = nodes.begin(); it_n != end_n; ++it_n)
		{
		    n = *it_n;
		    node::inout_edges_iterator it_e;
		    node::inout_edges_iterator end_e = n.inout_edges_end();
		    for (it_e = n.inout_edges_begin(); it_e != end_e; ++it_e)
		    {
			e = *it_e;
			node opposite_n = n.opposite(e);
			if ( (find(adj.begin(), adj.end(), opposite_n)
				== adj.end()) &&
			     (find(nodes.begin(), nodes.end(), opposite_n)
				== nodes.end() ) )
			{
			    adj.push_back (opposite_n);
			}


//			node opposite_n = g.opposite (n,e);
//			if (adj.search (opposite_n) == 0 &&
//			    nodes.search (opposite_n) == 0) {
//			    adj.push_back (g.opposite (n,e));
		    }
		}
	    } else {
		list<node>::const_iterator it_n;
		list<node>::const_iterator end_n = nodes.end();
		for (it_n = nodes.begin(); it_n != end_n; ++it_n)
		{
		    n = *it_n;
		    node::adj_edges_iterator it_e;
		    node::adj_edges_iterator end_e = n.adj_edges_end();
		    for (it_e = n.adj_edges_begin(); it_e != end_e; ++it_e)
		    {
			e = *it_e;
			node opposite_n = n.opposite(e);
			if ( (find(adj.begin(), adj.end(), opposite_n)
				== adj.end()) &&
			     (find(nodes.begin(), nodes.end(), opposite_n)
				== nodes.end() ) )
			{
			    adj.push_back (opposite_n);
			}

//			node opposite_n = g.opposite (n,e);
//			if (adj.search (opposite_n) == 0 &&
//			    nodes.search (opposite_n) == 0) {
//			    adj.push_back (g.opposite (n,e));
		    }
		}
	    }

	    info.msg (GT_Tcl::tcl (*this, adj));

	    return TCL_OK;

	} else if (command == "-in" && info.args_left(argc) == 0) {

	    list<node> nodes;
	    int code = info.parse (argc, this, nodes);
	    if (code != TCL_OK) {
		return code;
	    }
	    argc ++;
	    
	    list<node> adj;
	    edge e;
	    node n;
	    const graph& g = leda();
	    if (g.is_directed()) {
		list<node>::const_iterator it_n;
		list<node>::const_iterator end_n = nodes.end();
		for (it_n = nodes.begin(); it_n != end_n; ++it_n)
		{
		    n = *it_n;
		    node::in_edges_iterator it_e;
		    node::in_edges_iterator end_e = n.in_edges_end();
		    for (it_e = n.in_edges_begin(); it_e != end_e; ++it_e)
		    {
			e = *it_e;
			node opposite_n = n.opposite(e);
			if ( (find(adj.begin(), adj.end(), opposite_n)
				== adj.end()) &&
			     (find(nodes.begin(), nodes.end(), opposite_n)
				== nodes.end() ) )
			{
			    adj.push_back (opposite_n);
			}

//			node opposite_n = g.opposite (n,e);
//			if (adj.search (opposite_n) == 0 &&
//			    nodes.search (opposite_n) == 0) {
//			    adj.push_back (g.opposite (n,e));
		    }
		}
	    }

	    info.msg (GT_Tcl::tcl (*this, adj));

	    return TCL_OK;

	} else if (command == "-out" && info.args_left(argc) == 0) {

	    list<node> nodes;
	    int code = info.parse (argc, this, nodes);
	    if (code != TCL_OK) {
		return code;
	    }
	    argc ++;
		
	    list<node> adj;
	    edge e;
	    node n;
	    const graph& g = leda();
	    if (g.is_directed()) {
		list<node>::const_iterator it_n;
		list<node>::const_iterator end_n = nodes.end();
		for (it_n = nodes.begin(); it_n != end_n; ++it_n)
		{
		    n = *it_n;
		    node::out_edges_iterator it_e;
		    node::out_edges_iterator end_e = n.out_edges_end();
		    for (it_e = n.out_edges_begin(); it_e != end_e; ++it_e)
		    {
			e = *it_e;
			node opposite_n = n.opposite(e);
			if ( (find(adj.begin(), adj.end(), opposite_n)
				== adj.end()) &&
			     (find(nodes.begin(), nodes.end(), opposite_n)
				== nodes.end() ) )
			{
			    adj.push_back (opposite_n);
			}

//			node opposite_n = g.opposite (n,e);
//			if (adj.search (opposite_n) == 0 &&
//			    nodes.search (opposite_n) == 0) {
//			    adj.push_back (g.opposite (n,e));
		    }
		}
	    }

	    info.msg (GT_Tcl::tcl (*this, adj));

	    return TCL_OK;

	} else {
	    info.msg (GT_Error::wrong_number_of_args);
	    return TCL_ERROR;
	}
    }
}


int GT_Tcl_Graph::edges_cmd (
    GT_Tcl_info& info,
    int argc)
{
    int code;

    enum {
	no_operation,
	all_edges,
	adj_edges,
	in_edges,
	out_edges,
	between_edges,
	multi_edges,
	embedding,
	inner
    } operation;
    
    list<node> nodes; // adj_edges
    node source; // between_edges
    node target; // between_edges

    operation = no_operation;
    if (!info.exists (argc)) {
		
	operation = all_edges;
		
    } else if (info.args_left (argc) == 0) {
		
	if (GT::streq (info[argc], "-multi")) {
	    
	    operation = multi_edges;
	    argc ++;
	    
	} else {
	    operation = adj_edges;
	}
		
    } else if (info.args_left (argc) == 1) {

	if (GT::streq (info[argc], "-node") ||
	    GT::streq (info[argc], "-adj")) {
	    
	    operation = adj_edges;
	    argc ++;

	} else if (GT::streq (info[argc], "-in")) {
		
	    operation = in_edges;
	    argc ++;
			
	} else if (GT::streq (info[argc], "-out")) {
		
	    operation = out_edges;
	    argc ++;
	    
	} else if (GT::streq (info[argc], "-embedding")) {
	    
	    operation = embedding;
	    argc ++;
	    
	} else if (GT::streq (info[argc], "-inner")) {
	    
	    operation = inner;
	    argc ++;
	    
	} else {
	    info.msg (GT_Error::wrong_number_of_args);
	    return TCL_ERROR;
	}
	
    } else if (info.args_left (argc) == 2) {

	if (GT::streq (info[argc], "-between")) {
		
	    operation = between_edges;
	    argc ++;
		
	} else if (GT::streq (info[argc], "-multi")) {
		
	    operation = multi_edges;
	    argc ++;
	    
	} else {
	    info.msg (GT_Error::wrong_number_of_args);
	    return TCL_ERROR;
	}
		
    } else {
	info.msg (GT_Error::wrong_number_of_args);
	return TCL_ERROR;
    }

    switch (operation) {
		
	case all_edges: {
	}
	break;
		
	case adj_edges:
	case in_edges:
	case out_edges:
	case embedding:
	case inner:
	{
	    int code = info.parse (argc, this, nodes);
	    if (code != TCL_OK) {
		return code;
	    }
	    argc ++;
	}
	break;

	case between_edges:
	case multi_edges: {
	    if (info.exists(argc)) {
		code = info.parse (argc, this, source);
		if (code != TCL_OK) {
		    return code;
		}
		argc ++;
		code = info.parse (argc, this, target);
		if (code != TCL_OK) {
		    return code;
		}
		argc ++;
	    } else {
		source = node();
		target = node();
	    }
	}
	break;

	default:
	    break;
    }

    //
    // Find the edges
    //
	
    list<edge> edges;
    const graph& g = leda();
    edge e;

    switch (operation) {
	 
	case all_edges: {
	    edges = g.all_edges();
	}
	break;
		
	case adj_edges: {
	    forall_edges (e, g) {
		if ( (find (nodes.begin(),nodes.end(),e.source())
			!= nodes.end()) ||
		     (find (nodes.begin(),nodes.end(),e.target())
			!= nodes.end()) )
		{
		    edges.push_back (e);
		}

//		if (nodes.search(g.source(e)) != 0 ||
//		    nodes.search(g.target(e)) != 0) {
	    }
	}
	break;
		
	case inner: {
	    forall_edges (e, g) {
		if ( (find (nodes.begin(),nodes.end(),e.source())
			!= nodes.end()) &&
		     (find (nodes.begin(),nodes.end(),e.target())
			!= nodes.end()) )
		{
		    edges.push_back (e);
		}

//		if (nodes.search(g.source(e)) != 0 &&
//		    nodes.search(g.target(e)) != 0) {
	    }
	}
	break;

	case embedding: {

	    forall_edges (e, g) {
		list<node>::iterator end = nodes.end();

		list<node>::iterator source_in_list =
		    find(nodes.begin(), end, e.source());
		list<node>::iterator target_in_list =
		    find(nodes.begin(), end, e.target());
		if ( (source_in_list == end &&
		      target_in_list != end)     ||
		     (source_in_list != end &&
		      target_in_list == end)     )
		{
		    edges.push_back (e);
		}
//		list_item source_in_list = nodes.search(g.source(e));
//		list_item target_in_list = nodes.search(g.target(e));
//		if ((source_in_list == 0 && target_in_list != 0) ||
//		    (source_in_list != 0 && target_in_list == 0)) {
	    }
	}
	break;
		
	case in_edges: {
	    forall_edges (e, g) {
		if ( (find(nodes.begin(), nodes.end(), e.target())
			!= nodes.end() ) &&
		     (find(edges.begin(), edges.end(), e) == edges.end()) )
		{
		    edges.push_back (e);
		}
//		if (nodes.search(g.target(e)) != 0 && edges.search(e) == 0) {
	    }
	}
	break;
		
	case out_edges: {
	    forall_edges (e, g) {
		if (find(nodes.begin(), nodes.end(), e.source()) != nodes.end())
		{
		    edges.push_back (e);
		}

//		if (nodes.search(g.source(e)) != 0) {
	    }
	}
	break;
		
	case multi_edges: {
	    if (source != node() && target != node()) {
		forall_edges (e, g) {
		    if (e.source() == source && e.target() == target) {
			edges.push_back (e);
		    } else if (g.is_undirected() &&
			e.source() == target && e.target() == source) {
			edges.push_back (e);
		    }
		}
	    } else {
		node n;
		edge e1;
		edge e2;
		forall_nodes (n, g) {
		    forall_adj_edges (e1, n) {
			node source1 = e1.source();
			node target1 = e1.target();
			forall_adj_edges (e2, n) {
			    if (e1 != e2) {
				node source2 = e2.source();
				node target2 = e2.target();
				if (source1 == source2 && target1 == target2) {
				    edges.push_back (e2);
				} else if (g.is_undirected() &&
				    source1 == target2 && target1 == source2) {
				    edges.push_back (e2);
				}
			    }
			}
		    }
		}
	    }
	}
	break;
	
	case between_edges: {
	    forall_edges (e, g) {
		if (e.source() == source && e.target() == target) {
		    edges.push_back (e);
		} else if (e.source() == target && e.target() == source) {
		    edges.push_back (e);
		}
	    }
	}
	break;

	default:
	    break;
    }
	
    info.msg (GT_Tcl::tcl (*this, edges));
    return TCL_OK;

}


//////////////////////////////////////////
//
// void GT_Tcl_Graph::bbox_cmd (GT_Tcl_info& info, int argc)
//
//////////////////////////////////////////


int GT_Tcl_Graph::bbox_cmd (GT_Tcl_info& info, int argc)
{
    const char* usage = "bbox ?-subgraph nodes? ?-node n ?-edge e?";

    if (!info.exists(argc)) {
	info.msg (usage);
	return TCL_ERROR;
    }
    
    if (GT::streq (info[argc], "-subgraph") ||
	GT::streq (info[argc], "-node") ||
	GT::streq (info[argc], "-edge"))
    {
	// Ignore old switches
	argc ++;    
	if (!info.exists(argc)) {
	    info.msg (usage);
	    return TCL_ERROR;
	}
    }

    list<node> nodes;
    list<edge> edges;
    int code = info.parse (argc, this, nodes, edges);
    if (code != TCL_OK) {
	return code;
    }
    argc ++;
	
    GT_Rectangle rect;
    if (!nodes.empty()) {
	bbox (nodes.front(), rect);
    } else if (!edges.empty()) {
	bbox (edges.front(), rect);
    }

    list<node>::const_iterator nit;
    for (nit = nodes.begin(); nit != nodes.end(); ++nit) {
	rect.union_with (bbox(*nit));
    }

    list<edge>::const_iterator eit;
    for (eit = edges.begin(); eit != edges.end(); ++eit)
    {
	rect.union_with (bbox(*eit));
    }
	
    info.msg (GT::format ("%f %f %f %f",
	rect.left(),  rect.top(),
	rect.right(), rect.bottom()));
	
    return TCL_OK;
}

    
//////////////////////////////////////////
//
// void GT_Tcl_Graph::isnode_cmd (GT_Tcl_info& info, int argc)
// void GT_Tcl_Graph::isedge_cmd (GT_Tcl_info& info, int argc)
// void GT_Tcl_Graph::typeof_cmd (GT_Tcl_info& info, int argc)
//
//////////////////////////////////////////


int GT_Tcl_Graph::isnode_cmd (GT_Tcl_info& info, int argc)
{
    if (!info.is_last_arg (argc)) {
	info.msg (GT_Error::wrong_number_of_args);
	return TCL_ERROR;
    }

    int code;
    node n;
    code = info.parse (argc, this, n);
    argc ++;
    
    info.msg ( (code != TCL_OK || n == node()) ? "0" : "1");
    return TCL_OK;
}


int GT_Tcl_Graph::isedge_cmd (GT_Tcl_info& info, int argc)
{
    if (!info.is_last_arg (argc)) {
	info.msg (GT_Error::wrong_number_of_args);
	return TCL_ERROR;
    }

    int code;
    edge e;
    code = info.parse (argc, this, e);
    argc ++;
    
    info.msg ( (code != TCL_OK || e == edge()) ? "0" : "1");
    return TCL_OK;
}


int GT_Tcl_Graph::typeof_cmd (GT_Tcl_info& info, int argc)
{
    if (!info.exists (argc)) {
	info.msg (GT_Error::wrong_number_of_args);
	return TCL_ERROR;
    }

    bool short_type;
    if (GT::streq (info[argc], "-short")) {
	short_type = true;
	argc++;
    } else {
	short_type = false;
    }
    
    if (!info.is_last_arg (argc)) {
	info.msg (GT_Error::wrong_number_of_args);
	return TCL_ERROR;
    }

    int id;
    int code = info.strip_GT_prefix (info[argc], id);
    if (code != TCL_OK) {
	return code;
    }

    // Try node

    node n = find_node (id);
    if (n != node()) {
	info.msg (short_type ? "node" : "node:node");
	return TCL_OK;	
    }

    n = find_node_with_uid (id);
    if (n != node()) {
	info.msg (short_type ? "node" : "node:node");
	return TCL_OK;	
    }

    n = find_node_with_label_uid (id);
    if (n != node()) {
	info.msg (short_type ? "node" : "node:label");
	return TCL_OK;	
    }
    
    // Try edge

    edge e = find_edge (id);
    if (e != edge()) {
	info.msg (short_type ? "edge" : "edge:edge");
	return TCL_OK;	
    }

    e = find_edge_with_uid (id);
    if (e != edge()) {
	info.msg (short_type ? "edge" : "edge:edge");
	return TCL_OK;	
    }

    e = find_edge_with_label_uid (id);
    if (e != edge()) {
	info.msg (short_type ? "edge" : "edge:label");
	return TCL_OK;	
    }

    info.msg("");
    
    return TCL_OK;
}


//////////////////////////////////////////
//
// void GT_Tcl_Graph::translate_cmd (GT_Tcl_info& info, int argc)
//
//////////////////////////////////////////


int GT_Tcl_Graph::translate_cmd (GT_Tcl_info& info, int argc)
{
    const char* usage =
	"Usage: translate canvas ?-reverse? ?-x coord?  ?-y coord?";
    int code;
    bool reverse = false;
    
    if (!info.exists (argc)) {
	info.msg (usage);
	return TCL_ERROR;
    }

    //
    // First argument: canvas
    //
    
    string canvas = info[argc++];
    
    bool found_device = false;
    GT_Device* dev = 0;
    list<GT_Device*>::const_iterator it;
    list<GT_Device*>::const_iterator end = the_devices.end();
    for (it = the_devices.begin(); it != end; ++it)
    {
	dev = *it;
	if (dev->name() == canvas) {
	    found_device = true;
	    break;
	}
    }
    if (!found_device) {
	info.msg (GT::format(
	    "%s is not a canvas for this graph",
	    canvas.c_str()));
	return TCL_ERROR;
    }

    //
    // Optional second argument: reverse
    //
    
    info.parse (argc, "-reverse", reverse);

    // Advance index & check wether argument exists
    if (!info.exists(argc)) {
	info.msg (usage);
	return TCL_ERROR;
    }

    //
    // Dispatch the remaining options
    // -x double
    // -y double
    //

    while (info.exists (argc)) {

	string option = info[argc];
	
	if (option == "-x" && info.exists (argc+1)) {

	    argc ++;
	    
	    double x;
	    code = info.parse (argc, x);
	    if (code != TCL_OK) {
		return TCL_ERROR;
	    }
	    argc ++;
	    
	    double translated;
	    if (!reverse) {
		translated = dev->translate_x (x);
	    } else {
		translated = dev->translate_x_reverse (x);
	    }

	    char buffer[GT_Tcl::double_string_length];
	    sprintf (buffer, "%f", translated);
	    Tcl_AppendElement (info.interp(), buffer);
	
	} else if (option == "-y" && info.exists (argc+1)) {

	    argc ++;
	    
	    double y;
	    code = info.parse (argc, y);
	    if (code != TCL_OK) {
		return TCL_ERROR;
	    }
	    argc ++;
	    
	    double translated;
	    if (!reverse) {
		translated = dev->translate_y (y);
	    } else {
		translated = dev->translate_y_reverse (y);
	    }

	    char buffer[GT_Tcl::double_string_length];
	    sprintf (buffer, "%f", translated);
	    Tcl_AppendElement (info.interp(), buffer);
	
	} else {

	    list<double> coords;
	    code = info.parse (argc, coords);
	    if (code != TCL_OK) {
		return TCL_ERROR;
	    }
	    argc ++;
	    
	    double d;
	    int i = 0;
	    list<double>::const_iterator it;
	    list<double>::const_iterator end = coords.end();
	    for (it = coords.begin(); it != end; ++it)
	    {
		d = *it;
		double translated;
		if (!reverse) {
		    if (i%2 == 0) {
			translated = dev->translate_x (d);
		    } else {
			translated = dev->translate_y (d);
		    }
		} else {
		    if (i%2 == 0) {
			translated = dev->translate_x_reverse (d);
		    } else {
			translated = dev->translate_y_reverse (d);
		    }
		}

		char buffer[GT_Tcl::double_string_length];
		sprintf (buffer, "%f", translated);
		Tcl_AppendElement (info.interp(), buffer);

		i++;
	    }
	}
    }

    return TCL_OK;
}


//////////////////////////////////////////
//
// int GT_Tcl_Graph::mark_cmd (GT_Tcl_info& info, int argc)
//
//////////////////////////////////////////


int GT_Tcl_Graph::mark_cmd (GT_Tcl_info& info, int argc)
{
    const char* usage = "Usage: mark create|update|delete editor selection ?nodes|edges?";
    enum { create, update, del } command = create;

    if (info.exists (argc)) {
	if (GT::streq (info[argc], "update") ||
	    GT::streq (info[argc], "remark")) {
	    command = update;
	    argc ++;
	} else if (GT::streq (info[argc], "delete")) {
	    command = del;
	    argc ++;
	} else if (GT::streq (info[argc], "create")) {
	    command = create;
	    argc ++;
	} else {
	    command = create;
	}
    }
    
    GT_UIObject::Marker_type type = GT_UIObject::frame;
    if (info.args_left (argc) >= 1 && GT::streq (info[argc], "-type")) {

	if (GT::streq (info[argc+1], "unmarked") ||
	    GT::streq (info[argc+1], "unmarked")) {
	    type = GT_UIObject::unmarked;
	    argc += 2;
	} else if (GT::streq (info[argc+1], "frame")) {
	    type = GT_UIObject::frame;
	    argc += 2;
	} else if (GT::streq (info[argc+1], "blocks")) {
	    type = GT_UIObject::blocks;
	    argc += 2;
	} else if (GT::streq (info[argc+1], "quick")) {
	    type = GT_UIObject::quick;
	    argc += 2;
	} else {
	    info.msg ("Usage: -type none|frame|blocks|quick");
	    return TCL_ERROR;
	}

    } else {
	type = GT_UIObject::frame;
    }


    if (info.args_left (argc) != 2) {
	info.msg (usage);
	return TCL_ERROR;
    }

    const string device_name = info[argc++];
    const string selection = info[argc++];

    list<node> nodes;
    list<edge> edges;

    int code = info.parse (argc, this, nodes, edges);
    if (code != TCL_OK) {
	return code;
    }
    argc ++;

 
    GT_Device* device;
    list<GT_Device*>::const_iterator it;
    list<GT_Device*>::const_iterator end = the_devices.end();
    for (it = the_devices.begin(); it != end; ++it)
    {
	device = *it;
	if (device->name() == device_name) {

	    node n;
	    list<node>::const_iterator it;
	    list<node>::const_iterator end = nodes.end();
	    for (it = nodes.begin(); it != end; ++it)
	    {
		n = *it;
		GT_UIObject* uinode = device->get (gt(n).graphics()->uid());
		if (uinode != 0) {
		    switch (command) {
			case update:
			    uinode->update_mark (selection);
			    break;
			case del:
			    uinode->del_mark (selection);
			    break;
			case create:
			    uinode->mark (selection, type);
			    break;
		    }
		}
	    }

	    edge e;
	    list<edge>::const_iterator it_e;
	    list<edge>::const_iterator end_e = edges.end();
	    for (it_e = edges.begin(); it_e != end_e; ++it_e)
	    {
		e = *it_e;
		GT_UIObject* uinode = device->get (gt(e).graphics()->uid());
		if (uinode != 0) {
		    switch (command) {
			case update:
			    uinode->update_mark (selection);
			    break;
			case del:
			    uinode->del_mark (selection);
			    break;
			case create:
			    uinode->mark (selection, type);
			    break;
		    }
		}
	    }

	}
    }


    return TCL_OK;
}



//////////////////////////////////////////
//
// int GT_Tcl_Graph::style_cmd (GT_Tcl_info& info, int argc)
//
//////////////////////////////////////////


int GT_Tcl_Graph::new_style_cmd (GT_Tcl_info& info, int argc)
{
    char* usage = "create style ?-node name? ?-edge name";

    if (!info.exists (argc+1)) {
	info.msg (usage);
	return TCL_ERROR;
    }

    if (GT::streq (info[argc], "-node")) {
	add_node_style (info[argc+1]);
	info.msg (info[argc+1]);
    } else if (GT::streq (info[argc], "-edge")) {
	add_edge_style (info[argc+1]);
	info.msg (info[argc+1]);
    }

    return TCL_OK;
}


int GT_Tcl_Graph::style_cmd (GT_Tcl_info& info, int argc)
{
    char* usage = "create style ?-default_node_style style? ?-default_node_style style? $graph style <<list of nodes and edges>> ?style?";

    if (info.exists (argc) &&
	GT::streq (info[argc], "-default_node_style")) {

	if (info.exists (argc+1)) {

	    GT_Key style = info(argc+1);

	    if (node_style(style) != 0) {
		default_node_style (style);
	    } else {
		info.msg (GT::format ("Node style \"%s\" does not exists.",
		    style.name().c_str()));
		return TCL_ERROR;
	    }

	    argc += 2;

	} else {
	    info.msg (default_node_style().name());
	    argc += 1;
	}


    } else if (info.exists (argc) &&
	GT::streq (info[argc], "-default_edge_style")) {

	if (info.exists (argc+1)) {

	    GT_Key style = info(argc+1);

	    if (edge_style(style) != 0) {
		default_edge_style (style);
	    } else {
		info.msg (GT::format ("Edge style \"%s\" does not exists.",
		    style.name().c_str()));
	    }

	    argc += 2;

	} else {
	    info.msg (default_node_style().name());
	    argc += 1;
	}

    } else if (info.exists (argc)) {

	list<node> nodes;
	list<edge> edges;
	int code = info.parse (argc, this, nodes, edges);
	if (code != TCL_OK) {
	    return code;
	}

	if (info.is_last_arg (argc)) {

	    char** names = (char**) Tcl_Alloc (
		(nodes.size() + edges.size()) * sizeof(char*));
	    int i = 0;
	    
	    node n;
	    list<node>::const_iterator it_n;
	    list<node>::const_iterator end_n = nodes.end();
	    for (it_n = nodes.begin(); it_n != end_n; ++it_n)
	    {
		n = *it_n;
		GT_Key style = node_find_style (n);
		if (style != GT_Keys::undefined) {
		    names[i++] = (char*)style.name().c_str();
		}
	    }

	    edge e;
	    list<edge>::const_iterator it_e;
	    list<edge>::const_iterator end_e = edges.end();
	    for (it_e = edges.begin(); it_e != end_e; ++it_e)
	    {
		e = *it_e;
		GT_Key style = edge_find_style (e);
		if (style != GT_Keys::undefined) {
		    names[i++] = (char*)style.name().c_str();
		}
	    }
	    
	    if (i>= 0) {
		char* list_of_names = Tcl_Merge(i, names);
		info.msg (list_of_names);
		Tcl_Free (list_of_names);
	    }

	    Tcl_Free ((char*)names);

	} else if (info.is_last_arg (argc+1)) {

	    GT_Key style = info(argc+1);

	    if (node_style(style) != 0) {
		node n;
		list<node>::const_iterator it_n;
		list<node>::const_iterator end_n = nodes.end();
		for (it_n = nodes.begin(); it_n != end_n; ++it_n)
		{
		    n = *it_n;
		    node_style (n, style);
		}
	    }

	    if (edge_style(style) != 0) {
		edge e;
		list<edge>::const_iterator it_e;
		list<edge>::const_iterator end_e = edges.end();
		for (it_e = edges.begin(); it_e != end_e; ++it_e)
		{
		    e = *it_e;
		    edge_style (e, style);
		}
	    }
	    

	} else {
	    info.msg (usage);
	    return TCL_ERROR;
	}

    } else {

	int count_node_styles = 0;
	int count_edge_styles = 0;

	GT_Key style;

//	forall_defined (style, the_node_styles) {
//	    count_node_styles ++;
//	}
	count_node_styles += the_node_styles.size();

//	forall_defined (style, the_edge_styles) {
//	    count_edge_styles ++;
//	}
	count_edge_styles += the_edge_styles.size();

	char** node_styles = (char**) Tcl_Alloc (
	    count_node_styles * sizeof(char));
	char** edge_styles = (char**) Tcl_Alloc (
	    count_edge_styles * sizeof(char));

	map<GT_Key, GT_Node_Attributes*>::const_iterator it_n;
	map<GT_Key, GT_Node_Attributes*>::const_iterator end_n
	    = the_node_styles.end();

	int n = 0;
	for (it_n = the_node_styles.begin(); it_n != end_n; ++it_n)
	{
	    style = (*it_n).first;
	    node_styles[n++] = const_cast<char*>(style.name().c_str());
	}
//	forall_defined (style, the_node_styles) {

	map<GT_Key, GT_Edge_Attributes*>::const_iterator it_e;
	map<GT_Key, GT_Edge_Attributes*>::const_iterator end_e =
	    the_edge_styles.end();

	int e = 0;
	for (it_e = the_edge_styles.begin(); it_e != end_e; ++it_e)
	{
	    style = (*it_n).first;
	    edge_styles[e++] = const_cast<char*>(style.name().c_str());
	}
//	forall_defined (style, the_edge_styles) {

	char* all_styles[2];
	all_styles[0] = Tcl_Merge (count_node_styles, node_styles);
	all_styles[1] = Tcl_Merge (count_edge_styles, edge_styles);

	char* list_of_all_styles = Tcl_Merge (2, all_styles);
	info.msg (list_of_all_styles);

	Tcl_Free (list_of_all_styles);
	Tcl_Free (all_styles[0]);
	Tcl_Free (all_styles[1]);
	Tcl_Free ((char*)node_styles);
	Tcl_Free ((char*)edge_styles);
    }
    
    return  TCL_OK;
}
