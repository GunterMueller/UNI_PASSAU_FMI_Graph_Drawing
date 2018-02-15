/* This software is distributed under the Lesser General Public License */
#ifndef GT_TK_UILABEL_H
#define GT_TK_UILABEL_H

//
// Tk_UILabel.h
//
// This file defines the class GT_Tk_UILabel.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_tcl/Tk_UILabel.h,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:46:40 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet Project
//


#include "Tk_UIObject.h"


class GT_Tk_UILabel : public GT_Tk_UIObject
{
    typedef GT_Tk_UIObject baseclass;

public:
    GT_Tk_UILabel (GT_Tk_Device* device);
    virtual ~GT_Tk_UILabel ();

    virtual GT_Common_Attributes* attrs () const = 0;
    virtual int id () const;    

    virtual void make_create_cmd (string& cmd);
    virtual void make_update_coords_cmd (string& cmd,
	bool may_move_or_scale,
	bool force = false);
    virtual void make_update_attrs_cmd (string& cmd,
	bool force = false);
    virtual void make_tags_cmd (string& cmd,
	bool force = false);

    virtual void make_mark_cmd (string& cmd,
	const string& selection,
	GT_UIObject::Marker_type type = GT_UIObject::frame);
    virtual void make_update_mark_cmd (string& cmd,
	const string& selection,
	GT_UIObject::Marker_type type = GT_UIObject::frame);
};



//
// Label classes for graph, node, edge
//


class GT_Tk_UIGraphlabel : public GT_Tk_UILabel
{
    typedef GT_Tk_UILabel baseclass;

    GT_Graph_Attributes* the_graph_attrs;
    
public:
    GT_Tk_UIGraphlabel (GT_Tk_Device* device,
	GT_Graph& g);
    virtual ~GT_Tk_UIGraphlabel ();

    virtual const GT_Key& type () const;
    virtual GT_Common_Graphics* graphics() const;
    virtual GT_Common_Attributes* attrs () const;
};


class GT_Tk_UINodelabel : public GT_Tk_UILabel
{
    typedef GT_Tk_UILabel baseclass;

    GT_Node_Attributes* the_node_attrs;
    
public:
    GT_Tk_UINodelabel (GT_Tk_Device* device,
	GT_Graph& g, const node n);
    virtual ~GT_Tk_UINodelabel ();

    virtual const GT_Key& type () const;
    virtual GT_Common_Graphics* graphics() const;
    virtual GT_Common_Attributes* attrs () const;
};


class GT_Tk_UIEdgelabel : public GT_Tk_UILabel
{
    typedef GT_Tk_UILabel baseclass;

    GT_Edge_Attributes* the_edge_attrs;
    
public:
    GT_Tk_UIEdgelabel (GT_Tk_Device* device,
	GT_Graph& g, const edge e);
    virtual ~GT_Tk_UIEdgelabel ();

    virtual const GT_Key& type () const;
    virtual GT_Common_Graphics* graphics() const;
    virtual GT_Common_Attributes* attrs () const;
};


#endif
