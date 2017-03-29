/* This software is distributed under the Lesser General Public License */
#ifndef GT_GRAPHLET_H
#define GT_GRAPHLET_H

//
// Graphlet.h
//
// This file defines the class Graphlet.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Graphlet.h,v $
// $Author: himsolt $
// $Revision: 1.5 $
// $Date: 1999/03/05 20:43:51 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet project
//


//
// Some standard includes
//

#include <cassert>
#include <cstdio>
#include <cmath>
#include <climits>

#include <fstream>

#include <list>
#include <map>
#include <string>

#include <GTL/GTL.h>

//
// Configuration related data
//

#include "config.h"

//
// Windows compatibility
//

#ifndef MAXDOUBLE
#define MAXDOUBLE HUGE_VAL
#endif

#ifndef DBL_MIN
#define DBL_MIN MINDOUBLE
#endif

#ifndef DBL_MAX
#define DBL_MAX MAXDOUBLE
#endif

#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif

#ifndef M_PI_2
#define M_PI_2 1.57079632679489661923
#endif

#ifdef _WINDOWS
#  ifdef GT_GRAPHLET_DLL
#    undef GT_GRAPHLET_DLL
#    define GT_GRAPHLET_DLL __declspec(dllexport)
#  else 
#    define GT_GRAPHLET_DLL __declspec(dllimport) 
#  endif
#else
#  define GT_GRAPHLET_DLL
#endif

//
// Some standard macros
//

enum GT_Status {
    GT_OK = 0,
    GT_ERROR = 1
};


class GT_Copy {

    int the_type;

public:

    // Note: shallow is not used.

    enum type {
	shallow = 0 | 0x0,
	deep = 1 | 0x0,
	deep_from_parent = deep | 0x2,
	shallow_from_parent = shallow | 0x2,
	deep_update_from_parent = deep | 0x4,
	shallow_update_from_parent = shallow | 0x4
    };

    GT_Copy ();
    GT_Copy (int);
    
    inline bool is_deep ();
    inline bool is_copy_from_parent ();
    inline bool is_update_from_parent ();
};


inline GT_Copy::GT_Copy()
{
    the_type = 0;
}

inline GT_Copy::GT_Copy (int t)
{
    the_type = t;
}

inline bool GT_Copy::is_deep ()
{
    return (the_type & 0x1) != 0;
}

inline bool GT_Copy::is_copy_from_parent ()
{
    return (the_type & 0x2) != 0;
}

inline bool GT_Copy::is_update_from_parent ()
{
    return (the_type& 0x4) != 0;
}


//
// Small floating value to shield against precision problems ... rude guess !
//

const double GT_epsilon = 0.0001;


//////////////////////////////////////////
//
// Common class definitions
//
//////////////////////////////////////////


//
// Common defs for all classes (currently unused)
//


#define GT_CLASS_COMMON(Class)			\
private:					\
    typedef Class Class##Dummy

//
// Base class
//

#define GT_BASE_CLASS(Class)			\
GT_CLASS_COMMON(Class)



//
// Derived class
//

#define GT_CLASS(Class,Baseclass)		\
private:					\
typedef Baseclass baseclass;			\
GT_CLASS_COMMON(Class)

	
//
// Template class
//

	
#define GT_TEMPLATE_CLASS(Class, Baseclass)	\
private:					\
typedef Baseclass baseclass;			\
GT_CLASS_COMMON (Class)


//
// Class implementation 
//

#define GT_CLASS_IMPLEMENTATION(Class)
#define GT_TEMPLATE_CLASSIMPL(Class)


//////////////////////////////////////////	
//
// Automatic variable declaration
//
//////////////////////////////////////////	


//
// Auxiliary Definitions
//
	
#define GTI_PRIVATE_VARIABLE(name) the_##name
#define GTI_TYPE_OF(name) GTI_typeof_##name


//
// Declaration
//
	
#define GT_VARIABLE_DECLARE(type,variable)	\
private:					\
type GTI_PRIVATE_VARIABLE (variable);		\
typedef type GTI_typeof_##variable


//
// Access
//
	
#define GT_VARIABLE_GET(variable,name)		\
public:						\
GTI_TYPE_OF(name) name() const			\
{						\
    return GTI_PRIVATE_VARIABLE (variable);	\
}


//
// Access to complex Variables
//
	

#define GT_COMPLEX_VARIABLE_GET(variable,name)	\
public:						\
const GTI_TYPE_OF(name)& name() const		\
{						\
    return GTI_PRIVATE_VARIABLE (variable);	\
}


//
// Set
//

#define GT_VARIABLE_SET(variable,name)		\
public:						\
virtual void name (GTI_TYPE_OF(name) param)	\
{						\
    GTI_PRIVATE_VARIABLE (variable) = param;	\
}						\


//
// Set (Complex)
//

#define GT_COMPLEX_VARIABLE_SET(variable,name)		\
public:							\
virtual void name (const GTI_TYPE_OF(name)& param)	\
{							\
    GTI_PRIVATE_VARIABLE (variable) = param;		\
}



#define GT_VARIABLE(type,variable)			\
GT_VARIABLE_DECLARE (type,variable);			\
GT_VARIABLE_SET (variable,variable)			\
GT_VARIABLE_GET (variable,variable)

#define GT_COMPLEX_VARIABLE(type,variable)		\
GT_VARIABLE_DECLARE (type,variable);			\
GT_COMPLEX_VARIABLE_SET (variable,variable)		\
GT_COMPLEX_VARIABLE_GET (variable,variable)


//////////////////////////////////////////
//
// Graphlet is the mother of all classes ...
//
//////////////////////////////////////////

#include "Key.h"
#include "Keymapper.h"
#include "Keys.h"
#include "Id.h"
#include "Error.h"


#if defined(WIN32)
#include "Device.h"
#include "UIObject.h"
#endif


class GT_Parser;
class GT_GML;

class GT
{
    int the_draw_edges_above;

    GT_BASE_CLASS (GT);
    
public:

    // Constructor

    GT();
    virtual ~GT();

    static void init();
	
    //
    // String utilites
    //
	
    static char* strsave (const char* s, int max_length = 0);
    static bool streq (const char *s1, const char *s2);

    static string format(const char* form, ...);
    
    //
    // open / close file (with error handling)
    //

    FILE* fopen (const char *filename, const char* mode = "r");
    void fclose (FILE* file);

    GT_Id id;
    GT_Error error;
    GT_Parser* parser;
    GT_Keymapper keymapper;
    GT_GML* gml;

    // Accessors

    inline bool draw_edges_above () const;
    virtual void draw_edges_above (bool new_value);
    friend class GT_Graphscript;
};


inline bool GT::draw_edges_above () const
{
    return the_draw_edges_above != 0;
}


extern GT* graphlet;


#endif
