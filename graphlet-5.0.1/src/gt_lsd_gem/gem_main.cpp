/* This software is distributed under the Lesser General Public License */
#include <sgraph/sgraph.h>
#include <sgraph/sgraph_interface.h>
// #include <error.h>
#include "gem_main.h"
#include "gem_panel.h"
#include "interface.h"
#include "embedder.h"
#include "quality.h"


void call_gem(Sgraph_proc_info info)
{
  Sgraph graph;

  graph=info->sgraph;
  read_graphed(graph);
  if (number_vertices<1)
  {
    message("Empty graph?\n");
    return;
  }
  if (do_random){
	  randomize_graph(); }
  else if (i_finaltemp<i_starttemp){
	  insert();}
  if (a_finaltemp<a_starttemp) arrange(NULL);
  if (o_finaltemp<o_starttemp) optimize(NULL);

  if (check_quality)
  {
    quality();
	
/*
    message ("Graph name       |V|  |E| Diam  Nx  Qd    Qe    Qv    Qx    Q\n");
    message ("%.16s %3d %4d %3d%5ld  %5.3lf %5.3lf %5.3lf %5.3lf %5.3lf\n",
	       graph->label, (int) number_vertices, (int) number_edges, 
               (int) diam, (long) Nx, (double) Qd, (double) Qe, (double) Qv,
               (double) Qx, (double) Q);
*/
    message("\nSpring Embedder (Gem) Quality Check:\n");
    message("|V| : %5d      |E| : %5d\n", (int) number_vertices,
                                          (int) number_edges);
    message("Diam: %5d      Nx  : %5ld\n", (int) diam, (long) Nx);
    message("Qd  : %5.3lf      Qe  : %5.3lf\n", (double) Qd, (double) Qe);
    message("Qv  : %5.3lf      Qx  : %5.3lf\n", (double) Qv, (double) Qx);
    message("Q   : %5.3lf\n", (double) Q);

  }
  write_graphed();
 
  info->recompute   = TRUE;
  info->recenter    = TRUE;
}
