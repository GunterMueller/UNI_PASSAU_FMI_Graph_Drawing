/* This software is distributed under the Lesser General Public License */
//
// Tcl.cc
//
// This file implements the class GT_Tcl.cc, an interface to Tcl.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_tcl/Tcl.cpp,v $
// $Author: himsolt $
// $Revision: 1.4 $
// $Date: 1999/03/05 20:45:56 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet Project
//

#include "Tcl_Graph.h"
#include "Tcl_Command.h"


GT_Tcl::GT_Tcl ()
{
    return;
}



GT_Tcl::~GT_Tcl ()
{

    return;
}


//////////////////////////////////////////
//
// The format_value functions are used to return a parameter with
// the get or configure commands, e.g.
//
// $graph configure -parameter
// $graph get -parameter
//
// The first form returns {-parameter value}, while the second
// form returns just value. This is controlled by the parameter
// mode, which may have galues get and configure.
//
// Parameters are (formatted, parameter, value, mode) and the
// output is appended to formatted. parameter is of type GT_Key.
//
//////////////////////////////////////////


int GT_Tcl::format_value (Tcl_DString& formatted,
    const GT_Key& key,
    const int i,
    const Configure_Mode mode,
    char* insert_prefix)
{
    if (mode.is_configure()) {

	Tcl_DString tmp;
	Tcl_DStringInit (&tmp);
	if (insert_prefix != 0) {
	    Tcl_DStringAppend (&tmp, insert_prefix, strlen(insert_prefix));
	}
	Tcl_DStringAppend (&tmp, key.name().c_str(), key.name().length());

	char buffer [int_string_length];
	sprintf (buffer, "%d", i);
	Tcl_DStringAppendElement (&tmp, buffer);

	Tcl_DStringAppendElement (&formatted, Tcl_DStringValue (&tmp));
	Tcl_DStringFree (&tmp);

    } else {
	char buffer [int_string_length];
	sprintf (buffer, "%d", i);
	Tcl_DStringAppend (&formatted, buffer, strlen(buffer));
    }

    return TCL_OK;
}


int GT_Tcl::format_value (Tcl_DString& formatted,
    const GT_Key& key,
    const bool b,
    const Configure_Mode mode,
    char* insert_prefix)
{
    if (mode.is_configure()) {

	Tcl_DString tmp;
	Tcl_DStringInit (&tmp);
	if (insert_prefix != 0) {
	    Tcl_DStringAppend (&tmp, insert_prefix, strlen(insert_prefix));
	}
	Tcl_DStringAppend (&tmp, key.name().c_str(), key.name().length());

	char buffer [bool_string_length];
	sprintf (buffer, "%d", b ? 1 : 0);
	Tcl_DStringAppendElement (&tmp, buffer);

	Tcl_DStringAppendElement (&formatted, Tcl_DStringValue (&tmp));
	Tcl_DStringFree (&tmp);

    } else {
	char buffer [bool_string_length];
	sprintf (buffer, "%d", b ? 1 : 0);
	Tcl_DStringAppend (&formatted, buffer, strlen(buffer));
    }

    return TCL_OK;
}


int GT_Tcl::format_value (Tcl_DString& formatted,
    const GT_Key& key,
    const double d,
    const Configure_Mode mode,
    char* insert_prefix)
{
    if (mode.is_configure()) {

	Tcl_DString tmp;
	Tcl_DStringInit (&tmp);
	if (insert_prefix != 0) {
	    Tcl_DStringAppend (&tmp, insert_prefix, strlen(insert_prefix));
	}
	Tcl_DStringAppend (&tmp, key.name().c_str(), key.name().length());

	char buffer [double_string_length];
	sprintf (buffer, "%g", d);
	Tcl_DStringAppendElement (&tmp, buffer);

	Tcl_DStringAppendElement (&formatted, Tcl_DStringValue (&tmp));
	Tcl_DStringFree (&tmp);

    } else {
	char buffer [double_string_length];
	sprintf (buffer, "%g", d);
	Tcl_DStringAppend (&formatted, buffer, strlen(buffer));
    }

    return TCL_OK;
}


int GT_Tcl::format_value (Tcl_DString& formatted,
    const GT_Key& key,
    const string& s,
    const Configure_Mode mode,
    char* insert_prefix)
{
    if (mode.is_configure()) {

	Tcl_DString tmp;
	Tcl_DStringInit (&tmp);
	if (insert_prefix != 0) {
	    Tcl_DStringAppend (&tmp, insert_prefix, strlen(insert_prefix));
	}
	Tcl_DStringAppend (&tmp, key.name().c_str(), key.name().length());
	Tcl_DStringAppendElement (&tmp, s.c_str());
	Tcl_DStringAppendElement (&formatted, Tcl_DStringValue (&tmp));
	Tcl_DStringFree (&tmp);

    } else {
	if (s.length() > 0) {
	    Tcl_DStringAppend (&formatted, s.c_str(), s.length());
	}
    }

    return TCL_OK;
}


int GT_Tcl::format_value (Tcl_DString& formatted,
    const GT_Key& key,
    const GT_Key& k,
    const Configure_Mode mode,
    char* insert_prefix)
{
    if (mode.is_configure()) {

	Tcl_DString tmp;
	Tcl_DStringInit (&tmp);
	if (insert_prefix != 0) {
	    Tcl_DStringAppend (&tmp, insert_prefix, strlen(insert_prefix));
	}
	Tcl_DStringAppend (&tmp, key.name().c_str(), key.name().length());
	if (k.active()) {
	    Tcl_DStringAppendElement (&tmp, k.name().c_str());
	} else {
	    Tcl_DStringAppendElement (&tmp, "");
	}
	Tcl_DStringAppendElement (&formatted, Tcl_DStringValue (&tmp));
	Tcl_DStringFree (&tmp);

    } else {
	if (k.active()) {
	    Tcl_DStringAppend (&formatted, k.name().c_str(),
		k.name().length());
	}
    }

    return TCL_OK;
}



int GT_Tcl::format_value (Tcl_DString& formatted,
    const GT_List_of_Attributes* attrs,
    GT_List_of_Attributes::const_iterator it,
    const GT_Tcl::Configure_Mode mode,
    char* insert_prefix)
{
    int code = TCL_OK;

    if ((*it)->is_int()) {

	int i;
	(*it)->value_int(i);
	code = GT_Tcl::format_value (formatted, (*it)->key(), i, mode,
	    insert_prefix);

    } else if ((*it)->is_double()) {

	double d;
	(*it)->value_double(d);
	code = GT_Tcl::format_value (formatted, (*it)->key(), d, mode,
	    insert_prefix);

    } else if ((*it)->is_string()) {

	string s;
	(*it)->value_string(s);
	code = GT_Tcl::format_value (formatted, (*it)->key(), s, mode,
	    insert_prefix);

    } else if  ((*it)->is_list()) {
	GT_List_of_Attributes* attrs2;
	(*it)->value_list(attrs2);
	GT_List_of_Attributes::const_iterator it2;

	if (mode.is_configure()) {

	    Tcl_DString prefix;
	    Tcl_DStringInit (&prefix);
	    Tcl_DStringAppend (&prefix,
		insert_prefix, strlen (insert_prefix));
	    Tcl_DStringAppend (&prefix,
		(*it)->key().name().c_str(),
		strlen((*it)->key().name().c_str()));
	    Tcl_DStringAppend (&prefix,
		".", 1);

	    for (it2 = attrs2->begin();
		 code == TCL_OK && it2 != attrs2->end();
		 ++it2) {
		code = format_value (formatted, attrs2, it2, mode,
		    Tcl_DStringValue (&prefix));
	    }

	    Tcl_DStringFree (&prefix);

	} else {
	    for (it2 = attrs2->begin();
		 code == TCL_OK && it2 != attrs2->end();
		 ++it2) {
		code = format_value (formatted, attrs2, it2, mode,
		    insert_prefix);
	    }
	}
    }

    return code;
}

//////////////////////////////////////////
//
// Wrappers for TCL Calls
//
//////////////////////////////////////////

char* GT_Tcl::SetVar2 (Tcl_Interp *interp,
    const string& array_name, const string& index,
    const string& new_value,
    int flags)
{
    char* value = Tcl_SetVar2 (interp,
	const_cast<char*>(array_name.c_str()),
	const_cast<char*>(index.c_str()),
	const_cast<char*>(new_value.c_str()),
	flags);
    if (value == NULL) {
	string msg = string ("Cannot set ") +
	    string (array_name) +
	    string (index);
	Tcl_AddErrorInfo (interp, const_cast<char*>(msg.c_str()));
    }

    return value;
}


Tcl_Command GT_Tcl::CreateCommand (Tcl_Interp *interp,
    const string& name,
    Tcl_CmdProc* proc,
    void *client_data,
    Tcl_CmdDeleteProc* delete_proc)
{
    Tcl_Command cmd = Tcl_CreateCommand (interp,
	const_cast<char*>(name.c_str()),
	proc,
	(ClientData)client_data,
	delete_proc);

    if (cmd == NULL) {
	string msg = string ("Cannot create command ") + string (name);
	Tcl_AddErrorInfo (interp, const_cast<char*>(msg.c_str()));		
    }

    return cmd;
}




//
// Split List
//

int GT_Tcl::split_list (Tcl_Interp* interp, const char* name,
    int& argc, char**& argv)
{
    return Tcl_SplitList (interp, (char*)name, &argc, &argv);
}


int GT_Tcl::split_list (Tcl_Interp* interp, const char* name,
    list<string>& splitted)
{
    int argc;
    char** argv;    
    int code = GT_Tcl::split_list (interp, name, argc, argv);    
    if (code == TCL_ERROR) {
	return TCL_ERROR;
    }

    for (int i=0; i<argc; i++) {
	splitted.push_back (string(argv[i]));
    }

    Tcl_Free ((char*)argv);
    
    return TCL_OK;
}


//////////////////////////////////////////
//
//  Tcl representations of a graph/node/edge
//
//////////////////////////////////////////


bool GT_Tcl::is_gt_object (const char* name)
{
    return (strncmp (name,"GT:",3) == 0);
}


string GT_Tcl::gt (const GT_Graph& g)
{
    return GT::format("GT:%d", g.gt().id());
}

string GT_Tcl::gt (const GT_Graph& g, const node n)
{
    return GT::format("GT:%d", g.gt(n).id());
}

string GT_Tcl::gt (const GT_Graph& g, const edge e)
{
    return GT::format("GT:%d", g.gt(e).id());
}



//////////////////////////////////////////
//
// Convert C++/LEDA object to Tcl compatible strings
//
//////////////////////////////////////////


string GT_Tcl::tcl (const GT_Graph& g)
{
    return GT::format("GT:%d", g.gt().id());
}

string GT_Tcl::tcl (const GT_Graph& g, const node n)
{
    return GT::format("GT:%d", g.gt(n).id());
}

string GT_Tcl::tcl (const GT_Graph& g, const edge e)
{
    return GT::format("GT:%d", g.gt(e).id());
}


void GT_Tcl::tcl (const GT_Graph& g,
    string& result)
{
    result = GT::format("GT:%d", g.gt().id());
}

void GT_Tcl::tcl (const GT_Graph& g, const node n,
    string& result)
{
    result = GT::format("GT:%d", g.gt(n).id());
}

void GT_Tcl::tcl (const GT_Graph& g, const edge e,
    string& result)
{
    result = GT::format("GT:%d", g.gt(e).id());
}


//
// list<string>
//

void GT_Tcl::tcl (const list<string>& strings, string& result)
{
    int argc = strings.size();
    char** argv = new char* [argc];
    
    list<string>::const_iterator it;
    list<string>::const_iterator end = strings.end();
    int i = 0;
    for (it = strings.begin(); it != end; ++it)
    {
	argv[i] = const_cast<char*>(it->c_str());
	i++;
    }

    char* merged = Tcl_Merge (argc, argv);
    delete[] argv;
    
    result = string (merged);
}


string GT_Tcl::tcl (const list<string>& strings)
{
    string result;
    tcl (strings, result);
    return result;
}


//
// list<node>
//

void GT_Tcl::tcl (const GT_Graph& g, const list<node>& nodes,
    string& result)
{
    bool first = true;

    list<node>::const_iterator it;
    list<node>::const_iterator end = nodes.end();
    for (it = nodes.begin(); it != end; ++it)
    {
	if (first) {
	    result += GT_Tcl::gt (g, *it);
	    first = false;
	} else {
	    result += " " + GT_Tcl::gt (g, *it);
	}
    }
}


string GT_Tcl::tcl (const GT_Graph& g, const list<node>& nodes)
{
    string result;
    tcl (g, nodes, result);
    return result;

}


//
// list<edge>
//

void GT_Tcl::tcl (const GT_Graph& g, const list<edge>& edges,
    string& result)
{
    bool first = true;

    list<edge>::const_iterator it;
    list<edge>::const_iterator end = edges.end();

    for (it = edges.begin(); it != end; ++it)
    {
	if (first) {
	    result += GT_Tcl::gt (g, *it);
	    first = false;
	} else {
	    result += " " + GT_Tcl::gt (g, *it);
	}
    }
}


string GT_Tcl::tcl (const GT_Graph& g, const list<edge>& edges)
{
    string result;
    tcl (g, edges, result);
    return result;

}


//
// int
//

void GT_Tcl::tcl (const int i,
    string& result)
{
    result = GT::format("%d", i);
}

string GT_Tcl::tcl (const int i)
{
    return GT::format("%d", i);
}


//
// list<int>
//

void GT_Tcl::tcl (const list<int>& integers,
    string& result)
{
    bool first = true;
    list<int>::const_iterator it;
    list<int>::const_iterator end = integers.end();

    for (it = integers.begin(); it != end; ++it)
    {
	if (first) {
	    result += GT::format("%d ", *it);
	    first = false;
	} else {
	    result += GT::format(" %d", *it);
	}
    }
}


string GT_Tcl::tcl (const list<int>& integers)
{
    string result;
    tcl (integers, result);
    return result;

}


//
// double
//

void GT_Tcl::tcl (const double d,
    string& result)
{
    result = GT::format("%f", d);
}

string GT_Tcl::tcl (const double d)
{
    return GT::format("%f", d);
}


//
// list<double>
//

void GT_Tcl::tcl (const list<double>& doubles,
    string& result)
{
    bool first = true;
    
    list<double>::const_iterator it;
    list<double>::const_iterator end = doubles.end();

    for (it = doubles.begin(); it != end; ++it)
    {
	if (first) {
	    result += GT::format("%f", *it);
	    first = false;
	} else {
	    result += GT::format(" %f", *it);
	}
    }
}


string GT_Tcl::tcl (const list<double>& doubles)
{
    string result;
    tcl (doubles, result);
    return result;

}


//
// Merge List
//

string GT_Tcl::merge (int& argc, char**& argv)
{
    char* merged = Tcl_Merge (argc, argv);
    return string (merged);
}


string GT_Tcl::merge (const list<string>& splitted)
{
    int argc = splitted.size();
    char** argv = new char* [argc];
    
    list<string>::const_iterator it;
    list<string>::const_iterator end = splitted.end();
    int i = 0;
    for (it = splitted.begin(); it != end; ++it)
    {
	argv[i] = const_cast<char*>((*it).c_str());
	i++;
    }

    char* merged = Tcl_Merge (argc, argv);
    delete[] argv;
    
    return string (merged);
}


//////////////////////////////////////////
//
// int GT_Tcl::get_int (GT_Tcl_info& info, const char* s, int& result)
// int GT_Tcl::get_double (GT_Tcl_info& info, const char* s, double& result);
// int GT_Tcl::get_boolean (GT_Tcl_info& info, const char* s, bool& result);
//
//////////////////////////////////////////


int GT_Tcl::get_int (GT_Tcl_info& info, const char* s, int& result)
{
    return Tcl_GetInt (info.interp(), (char*)s, &result);
}


int GT_Tcl::get_double (GT_Tcl_info& info, const char* s, double& result)
{
    return Tcl_GetDouble (info.interp(), (char*)s, &result);
}


int GT_Tcl::get_boolean (GT_Tcl_info& info, const char* s, bool& result)
{
    int int_result;
    int code = Tcl_GetBoolean (info.interp(), (char*)s, &int_result);
    if (code == TCL_OK) {
	result = (int_result == 0) ? false : true;
    }
    return code;
}


int GT_Tcl::get_boolean (GT_Tcl_info& info, const char* s, int& result)
{
    return Tcl_GetBoolean (info.interp(), (char*)s, &result);
}
