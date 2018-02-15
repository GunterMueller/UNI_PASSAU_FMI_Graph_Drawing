/* This software is distributed under the Lesser General Public License */
//==========================================================================
//
//   scale.h 
//
//==========================================================================
// $Id: Tcl_Scale_Command.h,v 1.1 1998/09/15 20:26:37 forster Exp $

#ifndef GT_SCALE_H
#define GT_SCALE_H

#include <gt_base/Graphlet.h>
#include <gt_tcl/Tcl_Command.h>

class GT_Tcl_Scale_Command : public GT_Tcl_Command
{
    GT_CLASS(GT_Tcl_Scale_Command, GT_Tcl_Command);
    
public:
    GT_Tcl_Scale_Command(const string &name);
    
    virtual int cmd_parser (GT_Tcl_info& info, int& index);
    int scale (GT_Tcl_Graph *g, const list<node> &nodes,
	       double cx1, double cy1, double dx1, double dy1,
	       double cx2, double cy2, double dx2, double dy2);
};

#endif // GT_SCALE_H

//--------------------------------------------------------------------------
//   end of file
//--------------------------------------------------------------------------
