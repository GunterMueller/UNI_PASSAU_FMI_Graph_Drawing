/* This software is distributed under the Lesser General Public License */
#ifndef GT_UIOBJECT_H
#define GT_UIOBJECT_H

//
// UIObject.h
//
// This file defines the class GT_UIObject.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/UIObject.h,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:45:04 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet Project
//

class GT_Device;
class GT_Common_Graphics;


class GT_UIObject {
	
    GT_BASE_CLASS (GT_UIObject);

    GT_Device* the_device;

public:

    //
    // Constructor & Destructor
    //

    GT_UIObject (GT_Device* const device);
    virtual ~GT_UIObject ();

    //
    // Accessories
    //

    inline const GT_Device* GT_UIObject::device() const;

    //
    // Interface
    //

    virtual const GT_Key& type () const = 0;
    virtual GT_Common_Graphics* graphics() const = 0;

    virtual bool create () = 0;
    virtual bool move (const double move_x, const double move_y) = 0;
    virtual bool update (bool force = false) = 0;
    virtual bool update_attrs (bool force = false) = 0;
    virtual bool update_coords (bool force = false) = 0;
    virtual bool del () = 0;

    virtual bool raise (const char* tag = 0) = 0;
    virtual bool lower (const char* tag = 0) = 0;

    // Marker type

public:
    enum Marker_type {
	unmarked = 0,
	frame = 1,
	blocks = 2,
	quick = 3,
	nr_of_marker_types = quick
    };
protected:
    int the_marker_type;

public:
    virtual bool mark (const string& selection,
	Marker_type marker = frame);
    virtual bool update_mark (const string& selection);
    virtual bool del_mark (const string& selection);
};


inline const GT_Device* GT_UIObject::device() const
{
    return the_device;
}

#endif
