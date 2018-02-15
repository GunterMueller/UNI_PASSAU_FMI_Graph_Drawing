/* This software is distributed under the Lesser General Public License */
#ifndef TCL_INFO_H
#define TCL_INFO_H

//
// Tcl_Info.h
//
// This file defines the class GT_Tcl_info
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_tcl/Tcl_Info.h,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:46:23 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet Project
//


class GT_Tcl_Graph;

class GT_Tcl_info
{
    GT_BASE_CLASS (GT_Tcl_info);
	
    GT_VARIABLE (Tcl_Interp*, interp);
    GT_VARIABLE (int, argc);
    GT_VARIABLE (char**, argv);
	
public:

    //
    // Constructors and Destructors
    //
	
    GT_Tcl_info();
    GT_Tcl_info (Tcl_Interp* interp, int argc, char** argv);
    virtual ~GT_Tcl_info();

    //
    // msg access
    //

    void msg (const string&);
    void msg (const char*);
    const string msg () const;

    //
    // argv access
    //
	
    const char* argv (const int i) const;
    char* argv (const int i);

    inline const char* operator[] (const int i) const;
    inline char* operator[] (const int i);

    inline operator Tcl_Interp* () {
	return the_interp;
    }
    const GT_Key operator() (const int i) const;
    GT_Key operator() (const int i);
    
    //
    // Is argument index
    // - is_last_arg:   the last argument
    // - exists:        does it exist at all ?
    //
    
    bool is_last_arg (const int index) const;
    bool exists (const int index) const;

    //
    // How many arguments are left
    //
    
    bool args_left (const int index, const int n, bool exact = true) const;
    int args_left (const int index) const;
    bool args_left_at_least (const int index, const int n)  const;
    bool args_left_exactly (const int index, const int n) const;
	
    //
    // some special access methods for msg ...
    //
	
    // string& msg(); // non-const access to msg

    void msg (const int error);
    void msg (const int error, const int i);
    void msg (const int error, const string& s);

    //
    // GT Prefix Management
    //
    
    int strip_GT_prefix (const char* s, int& stripped);
    int strip_GT_prefix (int index, int& stripped);

    //
    // Parser Utilities
    //
    
    int parse (int& index, int& result);
    int parse (const char* s, int& result);
    int parse (int& index, double& result);
    int parse (const char* s, double& result);
    int parse (int& index, bool& result);
    int parse (const char* s, bool& result);
    int parse (int& index, list<double>& l);
    int parse (const char* s, list<double>& l);

    int parse (int& index, GT_Tcl_Graph*& g);
    int parse (const char* s, GT_Tcl_Graph*& g);
    int parse (int& index, GT_Graph*& g);
    int parse (const char* s, GT_Graph*& g);

    int parse (int& index, const GT_Tcl_Graph* g, node& n);
    int parse (const char* s, const GT_Tcl_Graph* g, node& n);
    int parse (int& index, const GT_Tcl_Graph* g, edge& e);
    int parse (const char* s, const GT_Tcl_Graph* g, edge& e);
    int parse (int& index, const GT_Tcl_Graph* g, list<node>& nodes);
    int parse (const char* s, const GT_Tcl_Graph* g, list<node>& nodes);
    int parse (int& index, const GT_Tcl_Graph* g, list<edge>& edges);
    int parse (const char* s, const GT_Tcl_Graph* g, list<edge>& edges);

    int parse (int& index, const GT_Tcl_Graph* g,
	list<node>& nodes, list<edge>& edges);
    int parse (const char* s, const GT_Tcl_Graph* g,
	list<node>& nodes, list<edge>& edges);
//     int parse (const char* s, GT_Tcl_Graph* g,
// 	list<GT_Node_Attributes*>& node_attrs,
// 	list<GT_Edge_Attributes*>& edge_attrs);
//     int parse (int& index, GT_Tcl_Graph* g,
// 	list<GT_Node_Attributes*>& node_attrs,
// 	list<GT_Edge_Attributes*>& edge_attrs);

    // MONSTER Parse
    int parse (int& index, GT_Tcl_Graph* g,
	list<GT_Tcl_Graph*>& graphs,
	list<node>& nodes,
	list<edge>& edges,
	list<GT_Key>& node_styles,
	list<GT_Key>& edge_styles);
    int parse (const char* s, GT_Tcl_Graph* g,
	list<GT_Tcl_Graph*>& graphs,
	list<node>& nodes,
	list<edge>& edges,
	list<GT_Key>& node_styles,
	list<GT_Key>& edge_styles);


    int parse (int& index, const GT_Tcl_Graph* g,
	node_map<int>*& array);
    int parse (const char* s, const GT_Tcl_Graph* g,
	node_map<int>*& array);
    int parse (int& index, const GT_Tcl_Graph* g,
	edge_map<int>*& array);
    int parse (const char* s, const GT_Tcl_Graph* g,
	edge_map<int>*& array);

    int parse (int& index, const GT_Tcl_Graph* g,
	node_map<double>*& array);
    int parse (const char* s, const GT_Tcl_Graph* g,
	node_map<double>*& array);
    int parse (int& index, const GT_Tcl_Graph* g,
	edge_map<double>*& array);
    int parse (const char* s, const GT_Tcl_Graph* g,
	edge_map<double>*& array);

    int parse (int& index, const GT_Tcl_Graph* g,
	node_map<string>*& array);
    int parse (const char* s, const GT_Tcl_Graph* g,
	node_map<string>*& array);
    int parse (int& index, const GT_Tcl_Graph* g,
	edge_map<string>*& array);
    int parse (const char* s, const GT_Tcl_Graph* g,
	edge_map<string>*& array);

    int parse (int& index, const char* option, int& result,
	bool optional = true);
    int parse (int& index, const char* option, double& result,
	bool optional = true);
    int parse (int& index, const char* option, bool& result,
	bool optional = true);
    int parse (int& index, const char* option, string& result,
	bool optional = true);


    //
    // Utility methods for msg management
    //
    
    void msg (const GT_Graph& g);
    void msg (const GT_Graph& g, const node n);
    void msg (const GT_Graph& g, const edge e);

    void msg (const GT_Graph& g, const list<node>& nodes);
    void msg (const GT_Graph& g, const list<edge>& edges);
    void msg (const list<double>& doubles);
    void msg (const list<int>& integers);
};


const char* GT_Tcl_info::operator[] (const int i) const {
    return argv(i);
}


char* GT_Tcl_info::operator[] (const int i) {
    return argv(i);
}
    

#endif
