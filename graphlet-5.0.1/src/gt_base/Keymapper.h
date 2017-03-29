/* This software is distributed under the Lesser General Public License */
#ifndef GT_KEYMAPPER_H
#define GT_KEYMAPPER_H

//
// Keymapper.h
//
// This module defines the class GT_Keymapper.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Keymapper.h,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:44:05 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet project
//


#include <map>

#ifndef GT_KEY_H
#include "Key.h"
#endif


//////////////////////////////////////////
//
// GT_Keymapper
//
//////////////////////////////////////////



class GT_Keymapper {

private:
	
    map<string,GT_Key> the_keys;

protected:
    void split (const GT_Key key, list<GT_Key>& path);

public:

    GT_Keymapper();
    virtual ~GT_Keymapper();
	
    GT_Key add (const string& s);
    GT_Key add (const char* s);
    
    friend ostream& operator<< (ostream &out, const GT_Keymapper& key);
};

ostream& operator<< (ostream &out, const GT_Keymapper& key);


#endif
