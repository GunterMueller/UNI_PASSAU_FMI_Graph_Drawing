/* This software is distributed under the Lesser General Public License */
#ifndef GT_ID_H
#define GT_ID_H

//
// Id.h
//
// This file defines the class GT_Id.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Id.h,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:43:55 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet Project
//


class GT_Id
{
    GT_BASE_CLASS (GT_Id);

    int the_max_id;
	
public:
    const int next_id ();
    void adjust_maximum_id (const int id);
	
    GT_Id();
    virtual ~GT_Id();
};

#endif
