/* (C) University of Passau 1991 */

#include <stdlib.h>

#include <std.h>
#include <sgraph.h>
#include <slist.h>

void print_graph_attributes (FILE *file, Sgraph g)
{
}

void print_node_attributes(FILE *file, Snode n)
{
	fprintf (file, "{$ %d %d $}", n->x, n->y);
}


void print_edge_attributes(FILE *file, Sedge e)
{
}


/* ====================================================================== */

typedef struct {

  int		dimension;
  int		start;

  double	zerox, zeroy, zeroz;
  double	unitx, unity, unitz;
  double	maxx, maxy, maxz;
}
Options;

Options	option;

#define node_nr(n) (attr_flags((n)))
#define USAGE "USAGE: %s: [-0] [-2] [-3] files\n"

int main (int argc, char **argv)
{
  Sgraph g;
  Snode	n;
  Sedge	e;

  int argument;
  int maxx=0, maxy=0;
  int maxnr=0, minnr=(2<<29);

  int count, i, size;
  int use_given_numbering;

  option.dimension = 0;
  option.zerox = 0.0;   option.zeroy = 0.0;   option.zeroz = 0.0;
  option.unitx = 0.001; option.unity = 0.001; option.unitz = 0.001;
  option.maxx  = 0.0;   option.maxy  = 0.0;   option.maxz  = 0.0;
  option.start = 1;


  argument = 1;
  while (argc > 1) {
    if (!strcmp(argv[argument], "-0")) {
      option.dimension = 0;
      argc --;
      argument ++;
    } else if (!strcmp(argv[argument], "-2")) {
      option.dimension = 2;
      argc --;
      argument ++;
    } else if (!strcmp(argv[argument], "-3")) {
      option.dimension = 3;
      argc --;
      argument ++;

    } else if (!strcmp(argv[argument], "-zerox") && argc > 2) {
      option.zerox = atof(argv[argument+1]);
      argc -= 2;
      argument += 2;
    } else if (!strcmp(argv[argument], "-unitx") && argc > 2) {
      option.unitx = atof(argv[argument+1]);
      argc -= 2;
      argument += 2;

    } else if (!strcmp(argv[argument], "-zeroy") && argc > 2) {
      option.zeroy = atof(argv[argument+1]);
      argc -= 2;
      argument += 2;
    } else if (!strcmp(argv[argument], "-unity") && argc > 2) {
      option.unity = atof(argv[argument+1]);
      argc -= 2;
      argument += 2;

    } else if (!strcmp(argv[argument], "-zeroz") && argc > 2) {
      option.zeroz = atof(argv[argument+1]);
      argc -= 2;
      argument += 2;
    } else if (!strcmp(argv[argument], "-unitz") && argc > 2) {
      option.unitz = atof(argv[argument+1]);
      argc -= 2;
      argument += 2;

    } else if (!strcmp(argv[argument], "-maxx") && argc > 2) {
      option.maxx = atof(argv[argument+1]);
      argc -= 2;
      argument += 2;
    } else if (!strcmp(argv[argument], "-maxy") && argc > 2) {
      option.maxy = atof(argv[argument+1]);
      argc -= 2;
      argument += 2;
    } else if (!strcmp(argv[argument], "-maxz") && argc > 2) {
      option.maxz = atof(argv[argument+1]);
      argc -= 2;
      argument += 2;

    } else if (!strcmp(argv[argument], "-start") && argc > 2) {
      option.start = atoi(argv[argument+1]);
      argc -= 2;
      argument += 2;

    } else if (argv[argument][0] == '-') {
      fprintf (stderr, USAGE, argv[0]);
      exit (1);
    }
  }


  g = load_graph(); /* Load a graph in GraphEd format */


  if (g != empty_graph)  {

    size = 0;
    for_all_nodes (g, n) {
      maxnr = maximum (maxnr, n->nr);
      minnr = minimum (minnr, n->nr);
      maxx = maximum(n->x, maxx);
      maxy = maximum(n->y, maxy);
      size ++;
    } end_for_all_nodes (g, n);

    if ((size > 0) && (size == maxnr - minnr+1)) {
      /* Continuous numbering available. Use the original numbers */
      use_given_numbering = TRUE;
    } else {
      /* Continuous numbering not available. Use artificial numbers */
      use_given_numbering = FALSE;
    }


    count = option.start;
    for_all_nodes (g, n) {

      if (use_given_numbering) {
	set_nodeattrs (n, make_attr (ATTR_FLAGS, n->nr));
      } else {
	set_nodeattrs (n, make_attr (ATTR_FLAGS, count++));
      }

      if (option.maxx != 0.0) {
	n->x = (double)n->x / (double)maxx * option.maxx / option.unitx;
      }
      if (option.maxy != 0.0) {
	n->y = (double)n->y / (double)maxy * option.maxy / option.unity;
      }
    } end_for_all_nodes (g, n);


    for (i = option.start; i < option.start + size; i++) {

      for_all_nodes (g, n) {
	if (node_nr(n) == i) {
	  break;
	}
      } end_for_all_nodes (g, n);
	
      printf ("%d", node_nr(n));

      switch (option.dimension) {
      case 0:
	break;
      case 2:
	printf (" %f %f",
		option.zerox + option.unitx * (double)n->x,
		option.zeroy + option.unity * (double)n->y);
	break;
      case 3:
	printf (" %f %f %f",
		option.zerox + option.unitx * (double)n->x,
		option.zeroy + option.unity * (double)n->y,
		option.zeroz + option.unitz * 0.0);
	break;
      }

      for_sourcelist (n, e) {
	printf (" %d", node_nr(e->tnode));
      } end_for_sourcelist (n, e);

      printf ("\n");

    }
  }
  exit (0);
}
