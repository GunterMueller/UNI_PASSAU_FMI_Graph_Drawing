/* This software is distributed under the Lesser General Public License */
//
// debug.cpp
//
// This is only a temporary file for debugging purposes 
// and is to be  removed after finishing the project.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_xdag/debug.cpp,v $
// $Author: himsolt $
// $Revision: 1.1.1.1 $
// $Date: 1998/08/27 17:19:30 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1997, Graphlet Project
//
//     Author: Harald Mader (mader@fmi.uni-passau.de)
//

#include <iostream>
#include <stdio.h>
#include <stdlib.h>

#include "debug.h"


int tracing = 0,
    my_indent = 0;


void indent_debug ()
{
    for (int i=0; i<my_indent; i++) {
	cout << " ";
    }
}


void debug (string text)
{
    if (tracing) {
	indent_debug();
	cout << text.c_str() << endl;
    }
}


void debug_in (string text)
{
    if (tracing) {
	indent_debug();
	cout << "==> " << text.c_str() << endl;
    }
    my_indent+=4;
}


void debug_out (string text)
{
    my_indent-=4;
    if (tracing) {
 	indent_debug();
	cout << "<==" << text.c_str() << endl;
    }
}
