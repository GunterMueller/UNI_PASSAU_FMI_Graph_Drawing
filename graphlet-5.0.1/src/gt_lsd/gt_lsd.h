/* This software is distributed under the Lesser General Public License */
#ifndef GT_LSD_H
#define GT_LSD_H

//
// lsd_main.h
//

// Walter Bachl: 25.6.96
// Basic definitions to call sgraph-algorithms
// Implementation: main.cc


//
// Initialization procedure, Tcl/Tk naming conventions
//

extern "C" {
    int Gt_lsd_Init (Tcl_Interp* interp);
}

extern void hide_selfloops (graph *g, list<edge> &edges);
extern void hide_multiedges (graph *g, list<edge> &edges);

#endif
