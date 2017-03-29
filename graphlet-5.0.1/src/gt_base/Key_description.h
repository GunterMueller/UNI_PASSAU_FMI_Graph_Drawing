/* This software is distributed under the Lesser General Public License */
#ifndef GT_KEY_DESCRIPTION_H
#define GT_KEY_DESCRIPTION_H

//
// Key_description.h
//
// This module defines the class GT_Key_description.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Key_description.h,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:44:02 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet project
//


//////////////////////////////////////////
//
// class GT_Key_description
//
//////////////////////////////////////////


class GT_Key_description {

private:

    string the_name;
    bool the_ispath;
    bool the_visible;
    bool the_safe;
    list<GT_Key> the_path;
	
public:
	
    GT_Key_description (const string& name);
    GT_Key_description ();	
    virtual ~GT_Key_description ();
    
    inline const string& name() const;
    inline bool ispath() const;
    inline bool visible() const;
    inline bool safe() const;
    inline const list<GT_Key>& path() const;

    //
    // Automatic conversion to string
    //

    inline operator const string& ();

    //
    // Friends
    //

    friend class GT_Keymapper; // can acces _ref
};

ostream& operator<< (ostream &out, const GT_Key_description& key);


//
// Accessories & Conversion Operators
//


inline const string& GT_Key_description::name() const
{
    return the_name;
}


inline bool GT_Key_description::ispath() const
{
    return the_ispath;
}


inline bool GT_Key_description::visible() const
{
    return the_visible;
}


inline bool GT_Key_description::safe() const
{
    return the_safe;
}


inline const list<GT_Key>& GT_Key_description::path() const
{
    return the_path;
}


inline GT_Key_description::operator const string& () {
    return the_name;
}


#endif
