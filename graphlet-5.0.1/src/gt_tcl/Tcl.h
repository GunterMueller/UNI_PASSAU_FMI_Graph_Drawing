/* This software is distributed under the Lesser General Public License */
#ifndef GT_TCL_H
#define GT_TCL_H

//
// Tcl.h
//
// This file defines Tcl/Tk utilities and the class GT_Tcl,
// which contains utilities for a Tcl interface.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_tcl/Tcl.h,v $
// $Author: himsolt $
// $Revision: 1.3 $
// $Date: 1999/03/05 20:45:57 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet Project
//

#ifdef _WIN32
// HAIL Microsoft
#define list GT_dummy_list
#endif

#include <tcl.h>

#ifdef _WIN32
#if __LEDA__  == 360
#define list leda_list
#else
#undef list
#endif
// HAIL LEDA
#endif

// #include <gt_base/Graph.h>

typedef list<int> GT_Tk_Id_List;

#ifndef GT_GRAPH_H
#include <gt_base/Graph.h>
#endif

class GT_Tcl_info;  // forward declaration
class GT_Tcl_Graph;  // forward declaration

class GT_Tcl
{

public:

    GT_Tcl();
    virtual ~GT_Tcl();

    class Configure_Mode {
	int the_mode;
	enum {
	    mode_get = 0x0,
	    mode_set = 0x1,
	    mode_configure_get = 0x2 | mode_get,
	    mode_configure_set = 0x2 | mode_set
	};
    public:

	inline Configure_Mode ();
	inline void configure ();
	inline void set ();
	inline void get ();
	inline bool is_get () const;
	inline bool is_set () const;
	inline bool is_configure () const;
    };
	
    enum Sizes {
	int_string_length = 16+1,
	double_string_length = 32+1,
	bool_string_length = 1+1,

	id_length = int_string_length+3
    };

    static int format_value (Tcl_DString& formatted,
	const GT_Key& key,
	const int i,
	const Configure_Mode mode,
	char* insert_prefix = 0);
    static int format_value (Tcl_DString& formatted,
	const GT_Key& key,
	const bool b,
	const Configure_Mode mode,
	char* insert_prefix = 0);
    static int format_value (Tcl_DString& formatted,
	const GT_Key& key,
	const double d,
	const Configure_Mode mode,
	char* insert_prefix = 0);
    static int format_value (Tcl_DString& formatted,
	const GT_Key& key,
	const string& s,
	const Configure_Mode mode,
	char* insert_prefix = 0);
    static int format_value (Tcl_DString& formatted,
	const GT_Key& key,
	const GT_Key& k,
	const Configure_Mode mode,
	char* insert_prefix = 0);
    static int format_value (Tcl_DString& formatted,
	const GT_List_of_Attributes* attrs,
	GT_List_of_Attributes::const_iterator it,
	const GT_Tcl::Configure_Mode mode,
	char* insert_prefix = 0);
	
    //
    // Convenient Wrappers for TCL Calls
    //
    // This list is by NO means complete; functions are added as needed
    //

    static char* SetVar2 (Tcl_Interp *interp,
	const string& array, const string& index,
	const string& new_value,
	int flags);

    static Tcl_Command CreateCommand (Tcl_Interp *interp,
	const string& name,
	Tcl_CmdProc* proc,
	void *client_data,
	Tcl_CmdDeleteProc* delete_proc);

    //
    // GT prefix management
    //

    static bool is_gt_object (const char* name);

    static string gt (const GT_Graph& g);
    static string gt (const GT_Graph& g, const node n);
    static string gt (const GT_Graph& g, const edge e);
	
    //
    // Data Structures -> Tcl Conversion
    //
    
    static string tcl (const GT_Graph& g);
    static void tcl (const GT_Graph& g, string& result);
    static string tcl (const GT_Graph& g, const node n);
    static void tcl (const GT_Graph& g, const node n, string& result);
    static string tcl (const GT_Graph& g, const edge e);
    static void tcl (const GT_Graph& g, const edge e, string& result);
    
    static string tcl (const list<string>& strings);
    static void tcl (const list<string>& strings, string& result);
    
    static string tcl (const int i);
    static void tcl (const int i, string& result);

    static string tcl (const list<int>& integers);
    static void tcl (const list<int>& integers, string& result);
    
    static string tcl (const double d);
    static void tcl (const double d, string& result);

    static string tcl (const list<double>& doubles);
    static void tcl (const list<double>& doubles, string& result);
    
    static string tcl (const GT_Graph& g, const list<node>& nodes);
    static void tcl (const GT_Graph& g, const list<node>& nodes,
	string& result);

    static string tcl (const GT_Graph& g, const list<edge>& edges);
    static void tcl (const GT_Graph& g, const list<edge>& edges,
	string& result);

    //
    // Tcl -> Data Structure Conversion
    //
    
    static int split_list (Tcl_Interp* info, const char* name,
	list<string>& splitted);
    static int split_list (Tcl_Interp* info, const char* name,
	int& argc, char**&argv);

    static string merge (const list<string>& splitted);
    static string merge (int& argc, char**&argv);
    
    // old procs (compatibility only)
    
    static int get_int (GT_Tcl_info& info, const char* s, int& result);
    static int get_double (GT_Tcl_info& info, const char* s, double& result);
    static int get_boolean (GT_Tcl_info& info, const char* s, bool& result);
    static int get_boolean (GT_Tcl_info& info, const char* s, int& result);
};


//
// class GT_Tcl::Configure_Mode
//

inline GT_Tcl::Configure_Mode::Configure_Mode ()
{
    the_mode = mode_get;
}

inline void GT_Tcl::Configure_Mode::configure ()
{
    the_mode |= 0x2;
}

inline void GT_Tcl::Configure_Mode::set ()
{
    the_mode |= 0x1;
}

inline void GT_Tcl::Configure_Mode::get ()
{
    the_mode &= ~(0x1);
}

inline bool GT_Tcl::Configure_Mode::is_get () const
{
    return (the_mode & 0x1) == 0;
}

inline bool GT_Tcl::Configure_Mode::is_set () const
{
    return (the_mode & 0x1) != 0;
}

inline bool GT_Tcl::Configure_Mode::is_configure () const
{
    return (the_mode & 0x2) != 0;
}
	
#endif
