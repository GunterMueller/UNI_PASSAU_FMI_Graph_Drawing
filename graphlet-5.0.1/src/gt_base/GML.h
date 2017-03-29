/* This software is distributed under the Lesser General Public License */
#ifndef GT_GML_H
#define GT_GML_H

//
// Graphlet.h
//
// This file defines the class Graphlet.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/GML.h,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:43:35 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet project
//

class GT_GML {
	
    int the_version;

public:
    GT_GML();
    virtual ~GT_GML();

    const char escape_character;
    const char begin_list_delimeter;
    const char end_list_delimeter;

    ostream& write_escaped (ostream &out, const char* text);
    ostream& write_quoted (ostream &out, const char* text);
    ostream& write_quoted (ostream &out, const string &text);

    virtual void version (int v);
    inline int version () const;

    friend class GT_Graphscript;
};



inline int GT_GML::version () const
{
    return the_version;
}

#endif
