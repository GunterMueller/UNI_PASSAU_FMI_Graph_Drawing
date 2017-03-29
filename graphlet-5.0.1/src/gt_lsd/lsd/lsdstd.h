/* This software is distributed under the Lesser General Public License */
// 
// L      S      D
// Leda & Sgraph Do it
//
// an interface to run
// Sgraph-alorithms on LEDA-graph-structures

// Author: Dirk Heider
// email: heider@fmi.uni-passau.de

///////////////////////////////////////////////////////////
// MODULE DESCRIPTION
//
// standardheader for the LSD-header
//
///////////////////////////////////////////////////////////

#ifndef LSDSTD_H
#define LSDSTD_H

// standard includes
#include <iostream>
#include <fstream>
#include <stdio.h>

// include header for Generic Attributes
// ATTENTION: GA.h must NEVER be included AFTER Sgraph-headers,
// redefinitionmessages are the result - but I don`t know why.

// WA; 21.5.96
#include <gt_base/Graph.h>

// include header of LEDA`s graphalgorithms
// #include <LEDA/graph_alg.h>

// include headers for Sgraph access; because Sgraph is actually written in
// C, we have to mark its functions as <extern "C"> so that the linker
// can find them.

#include <sgraph/std.h>
#include <sgraph/sgraph.h>
#include <sgraph/slist.h>


#include <stdarg.h>
#include <sgraph/sgraph_interface.h>

// fake some GraphEd globals
#include "ge_dummy.h"

// include the LSD-headers
#include "lsd_mref.h"
#include "ls_assoc.h"
#include "lsd.h"
#include "trace.h"     // helps tracing & debugging

// include the export-header of the algorithms running under LSD
// #include <tree_layout_walker/tree_layout_walker_export.h>
// #include <tree_layout_walker/walker_checks.h>

// #include <sugiyama/sugiyama_export.h>

// #include <springembedder_rf/springembedder_rf_export.h>
// #include <springembedder_rf/rf_checks.h>

// #include <springembedder_kamada/springembedder_kamada_export.h>

// #include <gem/gem_export.h>

// #include <minimal_bends_layout/minimal_bends_layout_export.h>

// #include <tunkelang/tunkelang_export.h>

// include an additional header for miscellaneous algorithm-definitions
#include "algs_imp.h"


#endif

