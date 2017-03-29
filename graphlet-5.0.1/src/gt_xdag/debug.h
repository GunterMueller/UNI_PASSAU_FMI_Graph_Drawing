/* This software is distributed under the Lesser General Public License */
//
// debug.h
//
// This is only a temporary file for debugging purposes 
// and is to be  removed after finishing the project.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_xdag/debug.h,v $
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


#ifndef MY_DEBUG_H
#define MY_DEBUG_H

#include <GTL/GTL.h>

extern int tracing;

extern void debug (string text);
extern void debug_in (string text);
extern void debug_out (string text);

#endif
