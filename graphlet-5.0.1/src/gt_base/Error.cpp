/* This software is distributed under the Lesser General Public License */
//
// Error.cc
//
// This file implements the class GT_Error.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Error.cpp,v $
// $Author: himsolt $
// $Revision: 1.3 $
// $Date: 1999/03/05 20:43:30 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet Project
//
//

#include "Graphlet.h"
#include <strstream>

GT_Error::GT_Error ()
{
    error_dict [GT_Error::wrong_number_of_args] = "Wrong number of arguments";
    error_dict [GT_Error::wrong_keyword] = "Wrong keyword";
    error_dict [GT_Error::wrong_int_val] = "Wrong integer value";
    error_dict [GT_Error::wrong_double_val] = "Wrong double value";
    error_dict [GT_Error::wrong_id] = "Wrong id";
    error_dict [GT_Error::internal_error] = "Internal error";
    error_dict [GT_Error::no_id] = "No such id";
    error_dict [GT_Error::no_command] = "No such command";
    error_dict [GT_Error::id_exists] = "Identifier already exists";
    error_dict [GT_Error::name_exists] = "Name already exists";
    error_dict [GT_Error::id_greater_zero] = "Identifier must be greater zero";
    error_dict [GT_Error::fileopen_error] = "Cannot open file";
    error_dict [GT_Error::no_graph] = "No graph exists";
    error_dict [GT_Error::no_filename] = "No filename specified";
    error_dict [GT_Error::no_canvas] = "No such canvas specified";
}

GT_Error::~GT_Error ()
{
}

string GT_Error::msg (const int msg_id)
{
    assert (error_dict.find(msg_id) != error_dict.end());
	
    return (error_dict[msg_id]);
}

string GT_Error::msg (const int msg_id, const string& text)
{
    assert (error_dict.find(msg_id) != error_dict.end());

    string s = error_dict[msg_id] + " '" + text + "' ";
    return s;
}


string GT_Error::msg (const int msg_id, const int number)
{
    assert (error_dict.find(msg_id) != error_dict.end());

    char buffer[20];
    sprintf (buffer, "%d", number);

    string s = error_dict[msg_id] + " '" + string(buffer) + "' ";	
    return (s);
}


