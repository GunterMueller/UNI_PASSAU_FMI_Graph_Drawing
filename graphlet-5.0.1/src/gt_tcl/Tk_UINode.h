/* This software is distributed under the Lesser General Public License */
#ifndef GT_TK_UINODE_H
#define GT_TK_UINODE_H

//
// Tk_UINode.h
//
// This file defines the class GT_Tk_UINode.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_tcl/Tk_UINode.h,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:46:43 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet Project
//


#include "Tk_UIObject.h"


class GT_Tk_UINode : public GT_Tk_UIObject
{
    GT_CLASS (GT_Tk_UINode, GT_Tk_UIObject);

    GT_Node_Attributes* the_node_attrs;
	
public:
    
    GT_Tk_UINode (GT_Tk_Device* device,
	GT_Graph& g,
	const node n);	
    virtual ~GT_Tk_UINode ();

    virtual const GT_Key& type () const;
    virtual GT_Common_Graphics* graphics() const;
    virtual int id () const;

    virtual void make_create_cmd (string& cmd);
    virtual void make_tags_cmd (string& cmd,
	bool force = false);

    virtual void make_mark_cmd (string& cmd,
	const string& selection,
	GT_UIObject::Marker_type type = GT_UIObject::frame);
    virtual void make_update_mark_cmd (string& cmd,
	const string& selection,
	GT_UIObject::Marker_type type = GT_UIObject::frame);
    virtual bool update (bool force = false);

    // Helpers

protected:
    void coords_frame (const GT_Common_Graphics* cg, double marker_width,
	int x[2],
	int y[2]);

};


#endif
