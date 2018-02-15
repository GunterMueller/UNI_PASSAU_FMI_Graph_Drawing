/* This software is distributed under the Lesser General Public License */
#ifndef GT_DEVICE_H
#define GT_DEVICE_H

//
// Device.h
//
// This file defines the classes GT_Device.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Device.h,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:43:25 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet Project
//


class GT_UIObject;

class GT_Device {
    
    map<int, GT_UIObject*> the_objects;
    string the_name;
    double the_scale_x;
    double the_scale_y;

public:
	
    GT_Device (const string& name,
	double scale_x = 1.0,
	double scale_y = 1.0);
    virtual ~GT_Device ();
    
    const string& name() const;
    virtual void name (const string&);

    double scale_x() const;
    double scale_y() const;
    virtual void scale_x (double);
    virtual void scale_y (double);
    virtual void scale (double, double);

    GT_UIObject* insert (int id, GT_UIObject* uiobject);
    GT_UIObject* get (int id) const;
    bool del (int id);
    virtual bool del_full (int id);
	
    bool defined (int id) const;

    double translate_x (double x) const;
    double translate_y (double y) const;
    double translate_x_reverse (double x) const;
    double translate_y_reverse (double y) const;

    void translate (double x[], double y[], int n) const;
    void translate (double from_x[], double from_y[],
	double to_x[], double to_y[],
	int n) const;
};


inline const string& GT_Device::name() const
{
    return the_name;
}


inline double GT_Device::scale_x() const
{
    return the_scale_x;
}


inline double GT_Device::scale_y() const
{
    return the_scale_y;
}


#endif
