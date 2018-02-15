/* This software is distributed under the Lesser General Public License */
//***********************************************************//
// leveling.h                                                //
//                                                           //
// This file contains a structure definition for the global  //
// leveling.                                                 //
//                                                           //
//                                                           //
// Source: /home/br/CVS/graphlet/src/gt_algorithms/          //
//                 tree_layout/leveling.h                    //
// Author: Wetzel Sabine                                     //
//                                                           //
// (C) University of Passau 1995-1999, graphlet Project      //
//                                                           //
//***********************************************************//
// $Source: /home/br/CVS/graphlet/src/gt_tree_layout/leveling.h,v $
// $Author: himsolt $
// $Revision: 1.3 $
// $Date: 1999/03/05 20:47:10 $
// $Locker:  $
// $State: Exp $

typedef struct{
	double node_height;
	double max_chan_height;
}HeightVec;



// output-functions for LEDA //
ostream& operator <<(ostream& out, const HeightVec)
{
	return out;
}

istream& operator >>(istream& in, const HeightVec)
{
	return in;
}

