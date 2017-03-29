#if defined SUN_VERSION
#include <std.h>
#include <slist.h>
#include <sgraph.h>
#else
#include <sgraph\std.h>
#include <sgraph\slist.h>
#include <sgraph\sgraph.h>
#endif


#include "sattrs.h"
#include "pqtree.h"
#include "sdebug.h"

Global void  printGraph (FILE *file, Sgraph g)
{
  Snode n;
  Sedge e;

  fprintf (file, "\nGRAPH = %d\n",
           g->directed);
  for_all_nodes (g,n) {
    fprintf (file, "node %d (%3d/%3d) ", n->nr, n->x, n->y);
/*  fprintf (file, "\"%s\" ",  n->label); */

    fprintf (file, "\n   edges to: ");
    for_sourcelist (n,e) {
       if (g->directed ||
          (e->snode->nr > e->tnode->nr)) {
        /* print edge       */
        /* Caution : in undirected graphs, each*/
	/* edge is printed only once !    */
/*     if (e->snode->nr < e->tnode->nr) { */

	  fprintf (file,"%d  ", e->tnode->nr);
/*        fprintf (file," \"%s\" ", e->label); */
/*     } else {
          fprintf (file,"re %d  ", e->tnode->nr);
          fprintf (file," \"%s\" ", e->label); */

       } /* endif */
    } end_for_sourcelist (n,e);
    fprintf (file, ";\n\n");
  } end_for_all_nodes (g,n);
  fprintf (file, "END\n");
  fflush(file);
}



Global void  printGraphWithAttrs (FILE *file, Sgraph g)
{
  Snode n;
  Sedge e;
/*Slist l; */

  fprintf (file, "\nGRAPH \"%s\" = %s\n",
           g->label, (g->directed) ? ("DIRECTED") : ("UNDIRECTED"));
  for_all_nodes (g,n) {
    fprintf (file,
	     "node %d (%3d/%3d) visited:%2d  dfnumber:%2d  compnumber %2d  Lvalue:%2d\n",
	     n->nr, n->x, n->y, node_visited(n), node_dfnumber(n),
	     node_compnumber(n), node_Lvalue(n));
    fprintf (file,"          bcc property: %2d  stnumber:%2d  ",
		   node_bcc_property(n), 
		   node_stnumber(n));
    fprintf (file, "\"%s\" ",  n->label);

    fprintf (file, "\n   edges to: ");
    for_sourcelist (n,e) {
/*      if (g->directed ||
	  (e->snode->nr < e->tnode->nr)) {*/
	/* print edge       */
	/* Caution : in undirected graphs, each*/
	/* edge is printed only once !    */
	if (e->snode->nr < e->tnode->nr) {
	   fprintf (file,"%d ", e->tnode->nr);
	   fprintf (file,"\"%s\" ", e->label);
	} else {
	   fprintf (file,"re %d ", e->tnode->nr);
	   fprintf (file,"\"%s\" ", e->label);
	} /* endif */
	fprintf (file, "bicomp: %d  ", edge_marker(e));
	if (!edge_in_mpg(e)) {
	   fprintf (file, "DELETED  ");
	} /* endif */
    } end_for_sourcelist (n,e);
    fprintf (file, ";\n\n");
  } end_for_all_nodes (g,n);
  fprintf (file, "END\n");
  fflush(file);
}


Global void print_PQtree(FILE *file, PQtree tree)
{
   PQtree child;

   if (pqtree_parent(tree) == NULL) {  /* tree ist root */
      fprintf(file, "\nPQ-Tree:");
      fflush(file);
   } /* endif */

   fprintf(file, "\nPQ-Node vom Typ ");
   if (pqtree_type(tree) == LEAF) {
      fprintf(file, "LEAF  ");
   } else if (pqtree_type(tree) == P_NODE) {
      fprintf(file, "P_NODE");
   } else if (pqtree_type(tree) == Q_NODE) {
      fprintf(file, "Q_NODE");
   } else {
      fprintf(file, "unknown"); 
   } /* endif */

   if (pqtree_parent(tree) != empty_pqtree) {
      fprintf(file, "  Entry: %2d  (Father: %d)  ",
              pqtree_entry(tree), pqtree_entry(pqtree_parent(tree)));
   } else {
      fprintf(file, "  Entry: %2d  (ROOT)  ",
              pqtree_entry(tree));
   } /* endif */

   if (pqtree_label(tree) == EMPTY) {
      fprintf(file, "\n            Label:  EMPTY        ");
   } else if (pqtree_label(tree) == PARTIAL) {
      fprintf(file, "\n            Label:  PARTIAL      ");
   } else if (pqtree_label(tree) == FULL) {
      fprintf(file, "\n            Label:  FULL         ");
   } else {
      fprintf(file, "\n            Label:  UNDEFINED    ");
   } /* endif */

   if (pqtree_mpg_type(tree) == W_TYPE) {
      fprintf(file, "  MPG_TYPE: W_TYPE");
   } else if (pqtree_mpg_type(tree) == B_TYPE) {
      fprintf(file, "  MPG_TYPE: B_TYPE");
   } else if (pqtree_mpg_type(tree) == H_TYPE) {
      fprintf(file, "  MPG_TYPE: H_TYPE");
   } else if (pqtree_mpg_type(tree) == A_TYPE) {
      fprintf(file, "  MPG_TYPE: A_TYPE");
   } else {
      fprintf(file, "  MPG_TYPE: UNDEFINED");
   } /* endif */

   if (pqtree_marker(tree) == BLOCKED) {
      fprintf(file, "\n            Marker: BLOCKED     ");
   } else if (pqtree_marker(tree) == UNBLOCKED) {
      fprintf(file, "\n            Marker: UNBLOCKED   ");
   } else if (pqtree_marker(tree) == QUEUED) {
      fprintf(file, "\n            Marker: QUEUED      ");
   } else if (pqtree_marker(tree) == DELETED) {
      fprintf(file, "\n            Marker: DELETED     ");
   } else if (pqtree_marker(tree) == UNMARKED) {
      fprintf(file, "\n            Marker: UNMARKED    ");
   } else {
      fprintf(file, "\n            Marker: UNDEFINED   ");
   } /* endif */

   if (pqtree_leftmost_child(tree) != empty_pqtree) {
      fprintf(file, "lmc: %d   ", pqtree_entry(pqtree_leftmost_child(tree)));
   } else {
      fprintf(file, "lmc: none   ");
   } /* endif */

   if (pqtree_rightmost_child(tree) != empty_pqtree) {
      fprintf(file, "rmc: %d", pqtree_entry(pqtree_rightmost_child(tree)));
   } else {
      fprintf(file, "rmc: none");
   } /* endif */

   fprintf(file,
      "\n            #Children: %2d  Im.Sibl: %d  Pertcc: %2d  Pertlc: %2d",
      pqtree_child_count(tree), pqtree_immed_siblings(tree),
      pqtree_pertinent_child_count(tree), pqtree_pertinent_leaf_count(tree));
   fprintf(file,
      "\n            #full ch: %d  partial ch: %d  empty ch: %d",
      pqtree_full_child_count(tree), pqtree_partial_child_count(tree),
      pqtree_empty_child_count(tree));
   fprintf(file, "\nChild list:");
   for_all_children(tree, child) {
      fprintf(file, "  %2d", pqtree_entry(child));
   } end_for_all_children(tree, child);
   fprintf(file, "\nFull Child list:");
   child = pqtree_full_child(tree);
   while (child != empty_pqtree) {
      fprintf(file, "  %2d", pqtree_entry(child));
      child = pqtree_full_succ(child);
   } /* endwhile */
   fprintf(file, "\nPartial Child list:");
   child = pqtree_partial_child(tree);
   while (child != empty_pqtree) {
      fprintf(file, "  %2d", pqtree_entry(child));
      child = pqtree_partial_succ(child);
   } /* endwhile */
   fprintf(file, "\n");
   for_all_children(tree, child) {
      print_PQtree(file, child);
   } end_for_all_children(tree, child);
   fflush(file);
}



Global Sgraph  loadGraph (FILE *file)
{
  Sgraph  g;
  Snode n, helpn;
  Sedge e;
  int   ret, helpnr;


  ret = fscanf (file, "GRAPH = ");
  if (ret != EOF) {
     g = make_graph(empty_attrs);
     fscanf (file, "%d\n", &(g->directed));
  } /* endif */

  ret = fscanf (file, "node %d ", &helpnr);
  while ((ret != EOF) && (ret > 0)) {
     n = make_node(g, empty_attrs);
     n->nr = helpnr;
     fscanf (file, "(%3d/%3d) ", &(n->x), &(n->y));

     fscanf (file, "edges to:");
     ret = fscanf (file,"%d ", &helpnr);
     while ((ret != EOF) && (ret > 0)) {
	for_all_nodes(g, helpn) {
	   if (helpnr == helpn->nr) {
	      break;
	   } /* endif */
	} end_for_all_nodes(g, helpn);
	e = make_edge(n, helpn , empty_attrs);
	ret = fscanf (file,"%d  ", &helpnr);
     } /* endwhile */
     ret = fscanf (file, ";\n\nnode %d ", &helpnr);
  } /* endwhile */

  fscanf (file, "END\n");
  return g;
}




