/* This software is distributed under the Lesser General Public License */
/* 
 *  parser for the GML-file-format specified in:
 *  Michael Himsolt, GML: Graph Modelling Language, 21.01.1997  
 */ 

#include <cstdio>
#include <cstdlib>
#include <cassert>
#include <cstring>
#include "Graphlet.h"

#include "Attributes.h"
#include "List_of_Attributes.h"
#include "Attribute_int.h"
#include "Attribute_double.h"
#include "Attribute_string.h"
#include "Attribute_list.h"

#include "gml_parser.h"
#include "Parser.h"

GT_List_of_Attributes* GT_parse_list (struct GML_pair* pairs)
{
    GT_List_of_Attributes* top = new GT_List_of_Attributes;
    GT_List_of_Attributes* sublist;
    GT_Attribute_Base* a = 0;
    
    while (pairs) {
	switch (pairs->kind) {
	    case GML_INT:
		a = new GT_Attribute_int (
		    graphlet->keymapper.add (pairs->key),
		    pairs->value.integer);
		top->push_back (a);
		break;

	    case GML_DOUBLE:
		a = new GT_Attribute_double (
		    graphlet->keymapper.add (pairs->key),
		    pairs->value.floating);
		top->push_back (a);
		break;

	    case GML_STRING:
		a = new GT_Attribute_string (
		    graphlet->keymapper.add (pairs->key),
		    pairs->value.str);
		top->push_back (a);
		break;

	    case GML_LIST:
		sublist = GT_parse_list (pairs->value.list);

		a = new GT_Attribute_list (
		    graphlet->keymapper.add (pairs->key),
		    sublist);
		top->push_back (a);
		break;

	    default:
		break;
	}
	
	pairs = pairs->next;
    }
    
    return top;
}

GT_List_of_Attributes* GT_GML_parser (FILE* source, GML_stat* stat) {

    struct GML_pair* pairs = GML_parser (source, stat, 0);
    if (stat->err.err_num == GML_OK) {
	GT_List_of_Attributes* top = GT_parse_list (pairs);
	GML_free_list (pairs, 0);
	return top;
    } else {
	GML_free_list (pairs, 0);
	return 0;
    }
}
	

