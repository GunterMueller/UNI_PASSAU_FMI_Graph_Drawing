/* This software is distributed under the Lesser General Public License */
//
// Graphlet.cpp
//
// This file implements the class Graphlet.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Graphlet.cpp,v $
// $Author: himsolt $
// $Revision: 1.3 $
// $Date: 1999/03/05 20:43:50 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet project
//

#include <cstdarg>

#include "Graphlet.h"
#include "GML.h"
#include "Parser.h"

GT_CLASS_IMPLEMENTATION (GT);


GT::GT()
{
    the_draw_edges_above = 1;
    parser = new GT_Parser;
    gml = new GT_GML;
}

GT::~GT()
{
    delete parser;
    delete gml;
}


void GT::init ()
{
    graphlet = new GT;
    GT_Keys::init();
}


//
// Global variable graphlet
//

GT* graphlet;


//
// Accessors
//

void GT::draw_edges_above (bool new_value)
{
    the_draw_edges_above = (new_value != 0) ? true : false;
}


//
// Tools
//

bool GT::streq (const char *s1, const char *s2)
{
    return !strcmp (s1,s2);
}


//
// strsave (s) returns a copy of s allocated with malloc. An
// optional parameters controls the length of the string.
//


char* GT::strsave (const char* s, int max_length)
{
    assert (s != 0);

    int length = 0;
    char* new_string = 0;
    
    if (max_length == 0) {
        
        length = strlen (s);
        new_string = (char*) malloc (length+1);
        assert (new_string != 0);
        strcpy (new_string, s);
        
    } else {

        length = max_length;
        new_string = (char*) malloc (length+1);
        assert (new_string != 0);
        strncpy (new_string, s, length);
    }
    
    new_string[length] = '\0';

    return new_string;
}

string GT::format(const char* form, ...)
{
    va_list arg_list;
    va_start(arg_list, form);

    char buf[1024];
    vsprintf(buf, form, arg_list);
    return string(buf);

    va_end(arg_list);
}


//
// File utilities
//

FILE* GT::fopen (const char *filename, const char* mode)
{
    return ::fopen (filename, mode);
}

void GT::fclose (FILE* file)
{
    ::fclose (file);
}
