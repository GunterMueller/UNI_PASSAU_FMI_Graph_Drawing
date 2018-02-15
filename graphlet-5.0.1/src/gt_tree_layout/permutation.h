/* This software is distributed under the Lesser General Public License */
//***********************************************************//
// permutation.h                                             //
//                                                           //
// This file provides the tree_forall_out_edges-Macro.       //
//                                                           //
//                                                           //
// Source: /home/br/CVS/graphlet/src/gt_algorithms/          //
//                 tree_layout/permutation.h                 //
// Author: Wetzel Sabine                                     //
//                                                           //
// (C) University of Passau 1995-1999, graphlet Project      //
//                                                           //
//***********************************************************//
#ifndef PERMUTATION_H
#define PERMUTATION_H

#include <vector>
#include <algorithm>
#include <gt_base/Graphlet.h>
#include <gt_base/NEI.h>

#include <GTL/graph.h>

//********************************************************************//
// Macro for forall_sorted_out_edges. Should be placed somewhere      //
// else!!!                                                            //
//                                                                    //
// contains a hack for Microsoft Visual C++ 5.0, because code like    //
//                                                                    //
//   for(int i=0; i<10; ++i) { ... do something ... }                 //
//   for(int i=0; i<10; ++i) { ... do something again ... }           //
//                                                                    //
// is illegal with Microsoft Extensions enabled, but without Microsoft//
// Extensions, the Microsoft STL does not work :-(.                   //
// So we code the line number (__LINE__) into our loop variables.     //
//********************************************************************//

#define TREE_COMPARE_CONCAT(x,y) x##y
#define TREE_COMPARE_V(y) TREE_COMPARE_CONCAT(TREE_COMPARE_V,y)
#define TREE_COMPARE_it(y) TREE_COMPARE_CONCAT(TREE_COMPARE_it,y)

#define tree_forall_sorted_out_edges(__e,__n, __treealgo)                       \
   vector<edge> TREE_COMPARE_V(__LINE__) = __treealgo->get_order(__n);    \
   if (!TREE_COMPARE_V(__LINE__).empty()) (__e) = *(TREE_COMPARE_V(__LINE__).begin()); \
   for (vector<edge>::iterator TREE_COMPARE_it(__LINE__) = TREE_COMPARE_V(__LINE__).begin(); \
	TREE_COMPARE_it(__LINE__) != TREE_COMPARE_V(__LINE__).end(); \
	(__e) = *(++TREE_COMPARE_it(__LINE__)))

#endif
