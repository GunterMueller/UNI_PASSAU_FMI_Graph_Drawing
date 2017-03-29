/* This software is distributed under the Lesser General Public License */
#ifndef GT_KEY_H
#define GT_KEY_H

//
// Key.h
//
// This module defines the class GT_Key.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Key.h,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:43:58 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet project
//


class GT_Key_description;

class GT_Key {

private:
    GT_Key_description* the_key;

public:
	
    GT_Key ();
    GT_Key (GT_Key_description* k);
    ~GT_Key ();

    inline const bool defined() const;
    const bool active() const;

    const string& name() const;
    inline const GT_Key_description* description () const;

    inline const bool operator== (const GT_Key& other_key) const;
    inline const bool operator!= (const GT_Key& other_key) const;

    friend class GT_Keymapper;
    // friend ostream& operator<< (ostream &out, const GT_Keymapper& keymapper);
    friend bool operator<(GT_Key,GT_Key);
};

extern ostream& operator<< (ostream &out, const GT_Key&);
extern istream& operator>> (istream &in, const GT_Key&);

//
// Inlined utility Methods
//


const bool GT_Key::defined() const
{
    return (the_key != 0);
}


const GT_Key_description* GT_Key::description () const
{
    return the_key;
}


const bool GT_Key::operator== (const GT_Key& other_key) const
{
    return (the_key == other_key.the_key);
}


const bool GT_Key::operator!= (const GT_Key& other_key) const
{
    return (the_key != other_key.the_key);
}


//
// Hash for GT_Key's (experimental)
//

// extern int Hash (const GT_Key k);


inline bool operator<(GT_Key k1, GT_Key k2)
{
    return k1.the_key < k2.the_key; 
}



#endif
