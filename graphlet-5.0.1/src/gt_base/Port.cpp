/* This software is distributed under the Lesser General Public License */
//
// Port.cpp
//
// This file implements the class GT_Port.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Port.cpp,v $
// $Author: himsolt $
// $Revision: 1.3 $
// $Date: 1999/03/05 20:44:42 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet Project
//

#include "Graphlet.h"

#include "Port.h"


//////////////////////////////////////////
//
// Constructor & Destructor
//
//////////////////////////////////////////


GT_Port::GT_Port ()
{
}


GT_Port::GT_Port (GT_Key name, double x, double y) :
	GT_Point (x,y), the_name (name)
{
}


GT_Port::GT_Port (const string& name, double x, double y) :
	GT_Point (x,y)
{
    the_name = graphlet->keymapper.add (name);
}



GT_Port::~GT_Port ()
{
}



//////////////////////////////////////////
//
// Accessors
//
//////////////////////////////////////////


void GT_Port::name (GT_Key new_name)
{
    the_name = new_name;
}


//////////////////////////////////////////
//
// Auxiliary Functions
//
//////////////////////////////////////////

bool operator== (const GT_Port& p1, const GT_Port& p2)
{
    return p1.GT_Point::operator== (p2) && p1.the_name == p2.the_name;
}
