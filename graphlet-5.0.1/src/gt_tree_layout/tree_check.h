/* This software is distributed under the Lesser General Public License */
//***********************************************************//
// tree_check.h                                              //
//                                                           //
// This file checks preconditions for tree algorithms.       //
//                                                           //
//                                                           //
// Author: Walter Bachl                                      //
//                                                           //
// (C) University of Passau 1995-1999, graphlet Project      //
//                                                           //
//***********************************************************//
// $Source: /home/br/CVS/graphlet/src/gt_tree_layout/tree_check.h,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:47:30 $
// $Locker:  $
// $State: Exp $

#ifndef TREE_CHECK_H
#define TREE_CHECK_H

extern bool unique_root (const GT_Graph& gt, string &error_msg, list<node> &nodes);
extern bool connected (const GT_Graph& gt, string &error_msg, list<node> &nodes);
extern bool no_superfluous_edge (const GT_Graph& gt, string &error_msg, 
    list<edge> &edges);
extern bool edge_orientation (const GT_Graph& gt, string &error_msg, 
    list<node> &nodes);

extern bool check_tree (const GT_Graph& gt, string &error_msg, 
    list<node> &nodes, list<edge> &edges);
extern bool check_directed_tree (const GT_Graph& gt, string &error_msg, 
    list<node> &nodes, list<edge> &edges);

#endif
