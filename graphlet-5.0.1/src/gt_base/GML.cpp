/* This software is distributed under the Lesser General Public License */
//
// GML.cc
//
// This file implements support for the GML file format.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/GML.cpp,v $
// $Author: himsolt $
// $Revision: 1.3 $
// $Date: 1999/03/05 20:43:33 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet project
//


#include "Graphlet.h"
#include "GML.h"

#include <GTL/gml_scanner.h>

#include <cctype>



//////////////////////////////////////////
//
// GT_GML
//
//////////////////////////////////////////


GT_GML::GT_GML() :
    the_version (2),
    escape_character ('\\'),
    begin_list_delimeter ('['),
    end_list_delimeter (']')
{
}


GT_GML::~GT_GML()
{
}


//
// Accessories
//

void GT_GML::version (int v)
{
    the_version = v;
}



//
// write_escaped writes a string; all non-printable characters
// (and " & < > ) are escaped.
//
// Taken from GraphEd source code, slightly adapted.
//

ostream& GT_GML::write_escaped (ostream &out, const char* text)
{
    // Taken from GraphEd 4.0 source code

    int i, length;

    length = strlen (text);
    for (i = 0; i<length; i++) {
	char character = text[i];
	if (!isascii(character)) {
	    //out << character_to_iso[character];
	    out << GML_table[character+256-160];
	} else {
	    switch (character) {
		case '"' :
		    out << "&quot;";
		    break;
		case '&' :
		    out << "&amp;";
		    break;
		case '<' :
		    out << "&lt;";
		    break;
		case '>' :
		    out << "&gt;";
		    break;
		default :
		    out << character;
		    break;
	    }
	}
    }
    return out;
}


//
// write_qoted writes a string sourronded by double quotes. All
// non-printable characters (and " & < > ) are escaped.
//

ostream& GT_GML::write_quoted (ostream &out, const char* text)
{
    out << '"';
    write_escaped (out, text);
    out << '"';

    return out;
}

ostream& GT_GML::write_quoted (ostream &out, const string &text)
{
    return write_quoted(out, text.c_str());
}
