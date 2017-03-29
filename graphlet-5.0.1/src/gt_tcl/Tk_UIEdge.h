/* This software is distributed under the Lesser General Public License */
#ifndef GT_TK_UIEDGE_H
#define GT_TK_UIEDGE_H

//
// Tk_UIEdge.h
//
// This file defines the class GT_Tk_UIEdge.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_tcl/Tk_UIEdge.h,v $
// $Author: himsolt $
// $Revision: 1.3 $
// $Date: 1999/03/05 20:46:33 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet Project
//


#include "Tk_UIObject.h"


class GT_Tk_UIEdge : public GT_Tk_UIObject
{
    GT_CLASS (GT_Tk_UIEdge, GT_Tk_UIObject);

    GT_Edge_Attributes* the_edge_attrs;
    int the_line_length;

public:
	
    GT_Tk_UIEdge (GT_Tk_Device* device,
	GT_Graph& g, const edge e);	
    virtual ~GT_Tk_UIEdge ();
	
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
};


#endif
