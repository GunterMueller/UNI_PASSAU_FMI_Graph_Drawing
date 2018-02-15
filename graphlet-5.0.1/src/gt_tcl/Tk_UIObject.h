/* This software is distributed under the Lesser General Public License */
#ifndef GT_TK_UIOBJECT_H
#define GT_TK_UIOBJECT_H

//
// Tk_UIObject.h
//
// This file defines the class GT_Tk_UIObject.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_tcl/Tk_UIObject.h,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:46:47 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet Project
//


#include <gt_base/UIObject.h>


class GT_Tk_UIObject : public GT_UIObject
{
    GT_CLASS (GT_Tk_UIObject, GT_UIObject);

protected:	
    GT_Tk_Id_List* the_tk_items;
	
public:

    GT_Tk_UIObject (GT_Tk_Device* const device);
    virtual ~GT_Tk_UIObject ();

    // id makes access to the id of the object
    virtual int id () const = 0;

    //
    // Tk_UIObject virtuals
    //

    virtual bool create ();
    virtual bool move (const double move_x, const double move_y);
    virtual bool update (bool force = false);
    virtual bool update_attrs (bool force = false);
    virtual bool update_coords (bool force = false);
    virtual bool del ();

    virtual bool raise (const char* tag = 0);
    virtual bool lower (const char* tag = 0);
    
    virtual void make_create_cmd (string& cmd);
    virtual void make_tags_cmd (string& cmd,
	bool force = false) = 0;
    virtual void make_move_cmd (
	const double move_x,
	const double move_y,
	string& cmd);
    virtual void make_update_attrs_cmd (string& cmd,
	bool force = false);
    virtual void make_update_coords_cmd (string& cmd,
	bool may_move_or_scale,
	bool force = false);
    virtual void make_delete_cmd (string& cmd, int uid);

    virtual void make_mark_cmd (string& cmd,
	const string& selection,
	GT_UIObject::Marker_type type = GT_UIObject::frame) = 0;
    virtual void make_update_mark_cmd (string& cmd,
	const string& selection,
	GT_UIObject::Marker_type type = GT_UIObject::frame) = 0;
    virtual void make_del_mark_cmd (string& cmd,
	const string& selection);
    virtual bool mark (const string& selection,
	Marker_type marker = frame);
    virtual bool update_mark (const string& selection);
    virtual bool del_mark (const string& selection);
    
    virtual bool tcl_eval (const string& cmd);

    //
    // Helpers
    //
	
    void coords_box (const GT_Rectangle& center,
	double x[], double y[]);
    void coords_diamond (const GT_Rectangle& center,
	double x[], double y[]);

    GT_Tk_Device* tk_device () const;
};


#endif
