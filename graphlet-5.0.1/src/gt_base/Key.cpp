/* This software is distributed under the Lesser General Public License */
//
// Key.cpp
//
// This module defines the class GT_Key
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Key.cpp,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:43:57 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet project
//

#include "Graphlet.h"

#include "Key_description.h"
#include "Key.h"


//////////////////////////////////////////
//
// Class GT_Key
//
//////////////////////////////////////////


GT_Key::GT_Key () :
	the_key (0)
{
}


GT_Key::GT_Key (GT_Key_description* k) :
	the_key (k)
{
}


GT_Key::~GT_Key () {
}



const bool GT_Key::active() const
{
    return defined() &&	(the_key != GT_Keys::def.the_key);
}


const string& GT_Key::name() const
{
    assert (the_key != 0);
    return the_key->name();
}


// int Hash (const GT_Key k)
// {
//     return Hash ((void*)(k.description()));
// }


//
// The next two operators are neccessary for the new LEDA
//
// << outputs the name of the key.
// >> is a dummy.
//

ostream& operator<< (ostream &out, const GT_Key& key)
{
    assert (key.description());
    
    return out << *(key.description());
}


istream& operator>> (istream &in, const GT_Key& /* key */)
{
    // unused
    return in;
}
