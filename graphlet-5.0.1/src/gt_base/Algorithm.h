/* This software is distributed under the Lesser General Public License */
#ifndef GT_ALGORITHM_H
#define GT_ALGORITHM_H

//
// Algorithm.h
//
// This file implements the class GT_Algorithm which is a
// standardized class for implementing Algorithms.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Algorithm.h,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:42:50 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet Project
//


//
// class GT_Algorithm
//
// This is an abstract base class.
//

class GT_Algorithm
{
    string the_name;
    
public:

    GT_Algorithm (const string& name);
    virtual ~GT_Algorithm ();
    
    inline const string& name () const;
    virtual void name (const string&);

    virtual int run (GT_Graph& g) = 0;
    virtual int check (GT_Graph& g, string& message) = 0;
    virtual void reset ();
    
    //
    // Utilities
    //
	
    static edge find_self_loop (GT_Graph& g);
    static bool remove_all_bends (GT_Graph& g);
    virtual void adjust_coordinates (GT_Graph& g,
	double min_x = 0,
	double min_y = 0) const;
};


inline const string& GT_Algorithm::name () const
{
    return the_name;
}



//
// class GT_No_Algorithm
//
// This is a dummy algorithm which does noything.
//



class GT_No_Algorithm : public GT_Algorithm
{
    GT_CLASS (GT_No_Algorithm, GT_Algorithm);

public:
    virtual int run (GT_Graph& g);
    virtual int check (GT_Graph& g, string& message);

    GT_No_Algorithm () : GT_Algorithm ("No")
    {
    }
	
    ~GT_No_Algorithm ()
    {
    }
	
};


#endif
