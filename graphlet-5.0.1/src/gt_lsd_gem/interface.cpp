/* This software is distributed under the Lesser General Public License */
/* Gem - GraphEd Interface 1995 by Frank Heyder */

#include <sgraph/sgraph.h>
#include "global.h"

#define GRAPHED_GEM
#include "adtgraph.h"
#undef GRAPHED_GEM

#include "interface.h"

void write_graphed(void)
{
  vertex	v;

  for_all_vertices(v)
  {
    vi[v].snode->x = vi[v].pos.x;
    vi[v].snode->y = vi[v].pos.y;
  }
}

void read_graphed(Sgraph graph)
{
  Snode n;
  Sedge e;

  create_gem_graph(); /* init graph structures */
  /* knoten eintragen */
  for_all_nodes(graph, n)
  {
    insert_vertices(1);
    attr_int(n) = number_vertices;
    vi[number_vertices].pos.x = n->x;
    vi[number_vertices].pos.y = n->y;
    vi[number_vertices].snode = n;
  } end_for_all_nodes(graph, n);
  /* kanten eintragen */
  for_all_nodes(graph, n)
  {
    for_sourcelist(n, e)
    {
      insert_edge(attr_int(e->snode), attr_int(e->tnode));
    } end_for_sourcelist (n, e);
  } end_for_all_nodes(graph, n);
}
