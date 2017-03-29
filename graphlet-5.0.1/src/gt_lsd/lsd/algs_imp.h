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
// Function-prototypes, which were not declared in the
// algorithms export-header, are declared here to assure
// proper calls from LSD
//
///////////////////////////////////////////////////////////

#ifndef _ALGS_IMP_H
#define _ALGS_IMP_H

// LEDA_SLE() calls the STRAIGHT_LINE_EMBEDDING-allgorithm of LEDA
// function defined in file "leda_sle.cc"
// of the /lsd/ directory
void LEDA_SLE(Sgraph_proc_info info);

	
// lsd_calls_layout_reingold_tilford() is needed since the pure
// algorithm is declared static in its module.
// function defined in file "lsd_rf_sf.c"
// of the tree_layout_rt/ directory
void lsd_calls_layout_reingold_tilford (Sgraph_proc_info info);

// set_gem_default_config() is used to initialize the gem-springembedder.
// Caution! gem needs exactly ONE call of gem_init_graph() before
// it can be called one or more times!
// function defined in file "lsd_gem_panel.c"
// of the gem/ directory
void set_gem_default_config(void);

#endif
