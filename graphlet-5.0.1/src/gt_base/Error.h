/* This software is distributed under the Lesser General Public License */
#ifndef GT_ERROR_H
#define GT_ERROR_H

//
// Error.h
//
// This file defines the class GT_Error
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Error.h,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:43:32 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet Project
//


class GT_Error
{
    map<int, string> error_dict;
	
public:
    string msg (const int msg_id);
    string msg (const int msg_id, const string& text);
    string msg (const int msg_id, const int number);
	
    GT_Error();
    virtual ~GT_Error();

    enum  {
	wrong_number_of_args,
	wrong_keyword,
	wrong_int_val,
	wrong_double_val,
	wrong_id,
	internal_error,
	no_id,
	no_command,
	id_exists,
	name_exists,
	id_greater_zero,
	fileopen_error,
	no_graph,
	no_filename,
	no_canvas
    };


};

#endif
