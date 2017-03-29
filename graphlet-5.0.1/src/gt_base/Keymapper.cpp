/* This software is distributed under the Lesser General Public License */
//
// Keymapper.cpp
//
// This module defines the class GT_Keymapper
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Keymapper.cpp,v $
// $Author: himsolt $
// $Revision: 1.3 $
// $Date: 1999/03/05 20:44:04 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet project
//

#include "Graphlet.h"

#include "Key_description.h"


//////////////////////////////////////////
//
// class GT_Keymapper
//
//////////////////////////////////////////


GT_Keymapper::GT_Keymapper()
{
}


GT_Keymapper::~GT_Keymapper()
{
}

GT_Key GT_Keymapper::add (const char* s)
{
    return add (string(s));
}


GT_Key GT_Keymapper::add (const string& s)
{
    if (the_keys.find(s) != the_keys.end()) {

        return the_keys[s];

    } else {

	GT_Key new_key (new GT_Key_description(s));

	// Examine key
	if (s.length() > 0) {

	    char front = s[0];
	    new_key.the_key->the_visible = (front != '@');
	    new_key.the_key->the_safe = (!(('A' <= front) && (front <= 'Z')));
			
	    new_key.the_key->the_ispath = (front == '.');
	    if (front == '.') {
		new_key.the_key->the_ispath = (true);
		split (new_key, new_key.the_key->the_path);
	    }
	}
		
        the_keys[s] = new_key;

        return new_key;
    }
};


void GT_Keymapper::split (const GT_Key key, list<GT_Key>& path)
{
    unsigned start; // overread the first '.';
    int end;

    if (key.description()->ispath()) {
	start = 1;
    } else {
	start = 0;
    }
	
    const string& name = key.description()->name();
    while (start < name.length()) {
		
	end = name.find ('.',start);
	if (end == -1) {
	    end = name.length() + 1; // virtual '.' at end of string
	}
		
	path.push_back (add (name.substr(start,end-start)));

	start = end+1;
    }		
}



ostream& operator<< (ostream &out, const GT_Key_description& key)
{
    out << "Name: " << key.name() << endl;
    out << "visible: " << key.visible() << endl;
    out << "safe: " << key.safe() << endl;
    out << "ispath: " << key.ispath() << endl;
	
    if (key.ispath()) {
		
	out << "Path description ";
	
	const list<GT_Key> &path = key.path();
	for(list<GT_Key>::const_iterator it = path.begin();
	    it != path.end(); ++it)
	{
	    out  << " . " << it->name();
	}
	out << endl;
    }

    return out;
}

ostream& operator<< (ostream &out, const GT_Keymapper& /* keymapper */)
{
    return out;
}
