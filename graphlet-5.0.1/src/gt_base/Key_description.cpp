/* This software is distributed under the Lesser General Public License */
//
// Key_description.cpp
//
// This module defines the class GT_Key_description
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Key_description.cpp,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:44:00 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet project
//

#include "Graphlet.h"

#include "Key_description.h"


//////////////////////////////////////////
//
// class GT_Key_description
//
//////////////////////////////////////////


GT_Key_description::GT_Key_description (const string& name) :
	the_name (name),
	the_ispath (false),
	the_visible (true),
	the_safe (true)
{
}
	
GT_Key_description::GT_Key_description () :
	the_ispath (false),
	the_visible (true),
	the_safe (true)
{
}
	

GT_Key_description::~GT_Key_description () {
}




inline ostream& operator<< (ostream &out, const GT_Key_description* key)
{
    return out << (*key);
}
