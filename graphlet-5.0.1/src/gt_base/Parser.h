/* This software is distributed under the Lesser General Public License */
#ifndef GT_PARSER_H
#define GT_PARSER_H

//
// Parser.h
//
// This file dcefines the classes GT_Parser and GT_Parser_yacc.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Parser.h,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:44:33 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet Project
//


//////////////////////////////////////////
//
// class GT_Parser
//
// This is the pure virtual baseclass for implementing parsers.
//
//////////////////////////////////////////

class GT_List_of_Attributes;
class GT_Attribute_Base;


class GT_Parser {

    GT_BASE_CLASS (GT_Parser);
    
public:
    GT_Parser();
    virtual ~GT_Parser();

    virtual GT_List_of_Attributes* parser (const char* filename = 0);

protected:
    GT_List_of_Attributes* the_parsed_attrs;
    int the_line_number;
    int the_column_number;
    string the_error_message;
    bool the_error_while_parsing;

public:
    inline GT_List_of_Attributes* parsed_attrs ();
    inline int line_number() const;
    inline int column_number() const;
    inline const string& error_message() const;
    inline bool error_while_parsing() const;

   //void GML_to_Graphlet (struct GML_pair* list, GT_List_of_Attributes* top);
};	



inline GT_List_of_Attributes* GT_Parser::parsed_attrs ()
{
    return the_parsed_attrs;
}


inline int GT_Parser::line_number() const
{
    return the_line_number;
}


inline int GT_Parser::column_number() const
{
    return the_column_number;
}


inline const string& GT_Parser::error_message() const
{
    return the_error_message;
}


inline bool GT_Parser::error_while_parsing() const
{
    return the_error_while_parsing;
}



//////////////////////////////////////////
//
// class GT_Parser_yacc
//
// gone for good
//
//////////////////////////////////////////

// class GT_Parser_yacc : public GT_Parser {

//     GT_CLASS (GT_Parser_yacc, GT_Parser);

// public:
//     GT_Parser_yacc();
//     ~GT_Parser_yacc();
	
//     virtual GT_List_of_Attributes* parser (const char* filename = 0);
//     virtual int error (int line_number, const char *message);
// };



#endif
