/* This software is distributed under the Lesser General Public License */
#ifndef GT_SHUTTLE_H
#define GT_SHUTTLE_H

//
// Shuttle.h
//
// This file defines the class GT_Shuttle
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Shuttle.h,v $
// $Author: himsolt $
// $Revision: 1.3 $
// $Date: 1999/06/24 11:13:07 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet Project
//


class GT_Graph;


class GT_Shuttle {

    GT_BASE_CLASS (GT_Shuttle);

protected:
    GT_Graph* the_graph;
	
public:
	
    GT_Shuttle () {
	the_graph = 0;
    }

    virtual void attach(GT_Graph* g)
    {
	the_graph = g;
    }

    virtual void attach(GT_Graph& g)
    {
	the_graph = &g;
    }

    GT_Graph* attached ()
    {
	return the_graph;
    }

    virtual graph& gtl() = 0;
};


#endif
