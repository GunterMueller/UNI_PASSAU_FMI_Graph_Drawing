/* This software is distributed under the Lesser General Public License */
//==========================================================================
//
//   rotate.h 
//
//==========================================================================
// $Id: Tcl_Rotate_Command.h,v 1.1 1998/09/15 20:26:37 forster Exp $

#ifndef GT_ROTATE_H
#define GT_ROTATE_H

#include <gt_base/Graphlet.h>
#include <gt_base/Point.h>
#include "Tcl_Command.h"

class GT_Tcl_Rotate_Command : public GT_Tcl_Command
{
    GT_CLASS(GT_Tcl_Rotate_Command, GT_Tcl_Command);
    
public:
    GT_Tcl_Rotate_Command(const string &name);
    
    virtual int cmd_parser (GT_Tcl_info& info, int& index);
    int rotate (GT_Tcl_Graph *g, const list<node> &nodes,
		const GT_Point &center, double angle);
};	

#endif // GT_ROTATE_H

//--------------------------------------------------------------------------
//   end of file
//--------------------------------------------------------------------------
