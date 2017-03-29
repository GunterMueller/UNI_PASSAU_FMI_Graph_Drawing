/* This software is distributed under the Lesser General Public License */
// 
// L      S      D
// Leda & Sgraph Do it
//

// Author: Dirk Heider
// email: heider@fmi.uni-passau.de

///////////////////////////////////////////////////////////
// MODULE DESCRIPTION
//
// (see headerfile)
//
///////////////////////////////////////////////////////////

// LSD-standard includes
#include "lsdstd.h"

// globals:

DEEP deep;

bool my_trace = FALSE;
bool my_fct_trace = FALSE;

ostream& operator<< (ostream& out, DEEP& deep_object)
{
	for (int count = 0; count < deep_object.deepness; count++)
	{
		cout << "  ";
	}
	return out;
}
