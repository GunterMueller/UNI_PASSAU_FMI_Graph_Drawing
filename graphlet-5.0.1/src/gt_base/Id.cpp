/* This software is distributed under the Lesser General Public License */
//
// Id.cc
//
// This file implements the class GT_Id.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Id.cpp,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:43:53 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet Project
//

#include "Graphlet.h"

#include "Id.h"


GT_Id::GT_Id()  : the_max_id (0)
{
    return;
}

GT_Id::~GT_Id()
{
}


const int GT_Id::next_id ()
{
    return the_max_id ++;
}



void GT_Id::adjust_maximum_id (const int id)
{
    if (the_max_id > id) {
	; // OK
    } else {
	the_max_id = id+1;
    }
}

