/* pqhandle.c */

#if defined SUN_VERSION
#include <std.h>
#include <slist.h>
#include <sgraph.h>
#include <error.h>
#else
#include <sgraph\std.h>
#include <sgraph\slist.h>
#include <sgraph\sgraph.h>
#include <graphed\error.h>
#endif

#include "sattrs.h"
#include "pqtree.h"

#include "templates.h"

#if defined DEBUG
#include "sdebug.h"
extern FILE *pqfile;
#endif

#if defined SUN_VERSION
extern	PQtree	Init_PQnode(short int type);
extern	void	enqueue_child(PQtree t, PQtree l);
extern	PQtree	delete_child(PQtree child);
extern	void	free_complete_PQtree(PQtree tree);
#else
extern	PQtree	Init_PQnode(short);
extern	void	enqueue_child(PQtree, PQtree);
extern	PQtree	delete_child(PQtree);
extern	void	free_complete_PQtree(PQtree);
#endif

extern char buffer[];


Global PQtree Init_PQnode(short int type)
{
   PQtree   t;

   t                                = NEW_PQtree;
   pqtree_type(t)                   = type;
   pqtree_label(t)                  = EMPTY;
   pqtree_marker(t)                 = UNMARKED;
   pqtree_parent(t)                 = empty_pqtree;
   pqtree_leftmost_child(t)         = empty_pqtree;
   pqtree_rightmost_child(t)        = empty_pqtree;
   pqtree_left_sibling(t)           = empty_pqtree;
   pqtree_right_sibling(t)          = empty_pqtree;
   pqtree_child_count(t)            = 0;
   pqtree_immed_siblings(t)         = 0;
   pqtree_pertinent_child_count(t)  = 0;
   pqtree_pertinent_leaf_count(t)   = 0;
   pqtree_full_child_count(t)       = 0;
   pqtree_full_child(t)             = empty_pqtree;
   pqtree_full_succ(t)              = empty_pqtree;
   pqtree_partial_child_count(t)    = 0;
   pqtree_partial_child(t)          = empty_pqtree;
   pqtree_partial_succ(t)           = empty_pqtree;
   pqtree_entry(t)                  = UNDEFINED;
   pqtree_block_node(t)		    = empty_slist;
   pqtree_corresponding_edge(t)     = empty_sedge;
   pqtree_mpg_type(t)               = NO_TYPE;
   pqtree_d(t)			    = 0;
   pqtree_l(t)			    = 0;
   pqtree_n(t)			    = 0;
   pqtree_m(t)			    = 0;
   pqtree_b(t)			    = 0;
   pqtree_w(t)			    = 0;
   pqtree_h(t)			    = 0;
   pqtree_a(t)			    = 0;
   pqtree_h_child1(t)               = empty_pqtree;
   pqtree_h_child2(t)               = empty_pqtree;
   pqtree_a_child(t)                = empty_pqtree;

   return t;
}


Global void enqueue_child(PQtree t, PQtree l)
{
/* enqueue a child to a node of a PQ-Tree                                   */
/* this procedure does now maintain counters and pointers for               */
/* full_children and partial_children !!!                                   */
/* correct values for imeed_siblings (0 for children of P-nodes, the        */
/* corresponding value for children of Q-nodes) are inserted                */
/* notice that full_children are inserted "to the right" of empty children  */

   PQtree   help_child; 

#  ifdef DEBUG
/* fprintf(pqfile, "\n  entering enqueue_child %d in %d",                   *
 *         pqtree_entry(l), pqtree_entry(t));                               *
 * fflush(pqfile);                                                          */
#  endif

   pqtree_parent(l) = t;
   pqtree_child_count(t)++;
   if (pqtree_leftmost_child(t) == NULL) {
/*    this is the first child of the father                                 */
      pqtree_leftmost_child(t)  = l;
      pqtree_rightmost_child(t) = l;
      pqtree_immed_siblings(l)  = 0;
      pqtree_left_sibling(l)    = l;
      pqtree_right_sibling(l)   = l;
      pqtree_label(t)           = pqtree_label(l);
   } else {

/*    this is not the first child of the father                             */
/*    enqueue new children ordered by pqtree_label                          */
/*    to keep this procedure easy, insert empty children to the very left   *
 *    and full_children to the very right                                   */
      if (pqtree_label(l) == EMPTY) {
         if (pqtree_type(t) == Q_NODE) {
            pqtree_immed_siblings(l) = 1;
	    if (pqtree_label(pqtree_leftmost_child(t)) == EMPTY) {
	       pqtree_immed_siblings(pqtree_leftmost_child(t))++;
	       pqtree_right_sibling(l) = pqtree_leftmost_child(t);
	       pqtree_left_sibling(pqtree_leftmost_child(t)) = l;
	       pqtree_leftmost_child(t) = l;
	       pqtree_left_sibling(l) = pqtree_rightmost_child(t);
	       pqtree_right_sibling(pqtree_rightmost_child(t)) = l;
	    } else {
	       pqtree_immed_siblings(pqtree_rightmost_child(t))++;
	       pqtree_left_sibling(l) = pqtree_rightmost_child(t);
	       pqtree_right_sibling(pqtree_rightmost_child(t)) = l;
	       pqtree_rightmost_child(t) = l;
	       pqtree_right_sibling(l) = pqtree_leftmost_child(t);
	       pqtree_left_sibling(pqtree_leftmost_child(t)) = l;
	    } /* endif */
	 } else {
	    pqtree_immed_siblings(l) = 0;
	    pqtree_right_sibling(l) = pqtree_leftmost_child(t);
	    pqtree_left_sibling(pqtree_leftmost_child(t)) = l;
	    pqtree_leftmost_child(t) = l;
	    pqtree_left_sibling(l) = pqtree_rightmost_child(t);
	    pqtree_right_sibling(pqtree_rightmost_child(t)) = l;
         } /* endif */
         if (pqtree_label(t) == FULL) {
            pqtree_label(t) = PARTIAL;
	 } /* endif */
      } else if (pqtree_label(l) == FULL) {
         if (pqtree_type(t) == Q_NODE) {
	    pqtree_immed_siblings(l) = 1;
	    if (pqtree_label(pqtree_leftmost_child(t)) == FULL) {
	       pqtree_immed_siblings(pqtree_leftmost_child(t))++;
	       pqtree_right_sibling(l) = pqtree_leftmost_child(t);
	       pqtree_left_sibling(pqtree_leftmost_child(t)) = l;
	       pqtree_leftmost_child(t) = l;
	       pqtree_left_sibling(l) = pqtree_rightmost_child(t);
	       pqtree_right_sibling(pqtree_rightmost_child(t)) = l;
	    } else {
	       pqtree_immed_siblings(pqtree_rightmost_child(t))++;
	       pqtree_left_sibling(l) = pqtree_rightmost_child(t);
	       pqtree_right_sibling(pqtree_rightmost_child(t)) = l;
	       pqtree_rightmost_child(t) = l;
	       pqtree_right_sibling(l) = pqtree_leftmost_child(t);
	       pqtree_left_sibling(pqtree_leftmost_child(t)) = l;
	    } /* endif */
	 } else {
	    pqtree_immed_siblings(l) = 0;
	    pqtree_left_sibling(l) = pqtree_rightmost_child(t);
	    pqtree_right_sibling(pqtree_rightmost_child(t)) = l;
	    pqtree_rightmost_child(t) = l;
	    pqtree_right_sibling(l) = pqtree_leftmost_child(t);
	    pqtree_left_sibling(pqtree_leftmost_child(t)) = l;
	 } /* endif */
	 if (pqtree_label(t) == EMPTY) {
	    pqtree_label(t) = PARTIAL;
	 } /* endif */
      } else if (pqtree_label(l) == PARTIAL) {
         if (pqtree_label(t) == EMPTY) {
            if (pqtree_type(t) == Q_NODE) {
               pqtree_immed_siblings(l) = 1;
	       pqtree_immed_siblings(pqtree_rightmost_child(t))++;
            } else {
               pqtree_immed_siblings(l) = 0;
            } /* endif */
            pqtree_left_sibling(l) = pqtree_rightmost_child(t);
            pqtree_right_sibling(pqtree_rightmost_child(t)) = l;
            pqtree_rightmost_child(t) = l;
            pqtree_right_sibling(l) = pqtree_leftmost_child(t);
            pqtree_left_sibling(pqtree_leftmost_child(t)) = l;
            pqtree_label(t) = PARTIAL;
	 } else {
            help_child = pqtree_leftmost_child(t);
            while (pqtree_label(help_child) == EMPTY) {
	       help_child = pqtree_right_sibling(help_child);
            } /* endwhile */
            pqtree_right_sibling(pqtree_left_sibling(help_child)) = l;
            pqtree_left_sibling(l) = pqtree_left_sibling(help_child);
            pqtree_left_sibling(help_child) = l;
	    pqtree_right_sibling(l) = help_child;
            if (help_child == pqtree_leftmost_child(t)) {
               if (pqtree_type(t) == Q_NODE) {
                  pqtree_immed_siblings(l) = 1;
		  pqtree_immed_siblings(pqtree_leftmost_child(t))++;
               } else {
                  pqtree_immed_siblings(l) = 0;
               } /* endif */
               pqtree_leftmost_child(t) = l;
            } else {
               if (pqtree_type(t) == Q_NODE) {
                  pqtree_immed_siblings(l) = 2;
               } else {
                  pqtree_immed_siblings(l) = 0;
	       } /* endif */
            } /* endif */
            if (pqtree_label(t) == FULL) {
	       pqtree_label(t) = PARTIAL;
            } /* endif */
         } /* endif */
      } else {  /* pqtree_label(l) == UNDEFINED */
/*       order by pqtree_entry */
	 if (pqtree_entry(l) <= pqtree_entry(pqtree_leftmost_child(t))) {
            help_child = pqtree_leftmost_child(t);
            pqtree_leftmost_child(t) = l;
            pqtree_right_sibling(l) = help_child;
	    pqtree_left_sibling(l) = pqtree_left_sibling(help_child);
            pqtree_right_sibling(pqtree_left_sibling(help_child)) = l;
            pqtree_left_sibling(help_child) = l;
            if (pqtree_type(t) == Q_NODE) {
               pqtree_immed_siblings(l) = 1;
               pqtree_immed_siblings(help_child)++;
            } else {
               pqtree_immed_siblings(l) = 0;
            } /* endif */
         } else if (pqtree_entry(l) > pqtree_entry(pqtree_rightmost_child(t))) {
	    help_child = pqtree_rightmost_child(t);
            pqtree_rightmost_child(t) = l;
            pqtree_left_sibling(l) = help_child;
	    pqtree_right_sibling(l) = pqtree_right_sibling(help_child);
            pqtree_left_sibling(pqtree_right_sibling(help_child)) = l;
            pqtree_right_sibling(help_child) = l;
            if (pqtree_type(t) == Q_NODE) {
               pqtree_immed_siblings(l) = 1;
	       pqtree_immed_siblings(help_child)++;
            } else {
               pqtree_immed_siblings(l) = 0;
            } /* endif */
	 } else {
            help_child = pqtree_leftmost_child(t);
            while (pqtree_entry(l) > pqtree_entry(help_child)) {
               help_child = pqtree_right_sibling(help_child);
            } /* endwhile */
            pqtree_right_sibling(l) = help_child;
            pqtree_left_sibling(l) = pqtree_left_sibling(help_child);
            pqtree_right_sibling(pqtree_left_sibling(help_child)) = l;
            pqtree_left_sibling(help_child) = l;
            pqtree_immed_siblings(l)  = 0;
	    if (pqtree_type(t) == Q_NODE) {
               pqtree_immed_siblings(l) = 2;
            } else {
	       pqtree_immed_siblings(l) = 0;
            } /* endif */
         } /* endif */

      } /* endif */
   } /* endif */

   if (pqtree_label(l) == FULL) {
      pqtree_full_child_count(t)++;
      pqtree_full_succ(l) = pqtree_full_child(t);
      pqtree_full_child(t) = l;

   } else if (pqtree_label(l) == PARTIAL) {
      pqtree_partial_child_count(t)++;
      pqtree_partial_succ(l) = pqtree_partial_child(t);
      pqtree_partial_child(t) = l;
   } /* endif */

#  ifdef DEBUG
/* fprintf(pqfile, "\n  leaving enqueue_child %d in %d",                    *
 *         pqtree_entry(l), pqtree_entry(t));                               *
 * fflush(pqfile);                                                          */
#  endif
}


Global PQtree   delete_child(PQtree child)
{
/* this functions deletes a child from the childlist of its father.         *
 * the father is returned, if work is not successful (reason could be       *
 * that this function is called with root-node) NULL is returned,           *
 * indicating no success, since no father was found                         */
/* notice: it is not in the responsability of this function to take care    *
 * that every P-Node father that will be returned, has more than one child, *
 * and every Q-Node father even has more than two children.                 */

   PQtree   parent /*, help_child */;

   parent = pqtree_parent(child);
   if (parent != empty_pqtree) {
      if (pqtree_child_count(parent) == 1) {
         pqtree_leftmost_child(parent) = empty_pqtree;
         pqtree_rightmost_child(parent) = empty_pqtree;
	 pqtree_child_count(parent) = 0;
      } else {
         pqtree_right_sibling(pqtree_left_sibling(child)) =
            pqtree_right_sibling(child);
         pqtree_left_sibling(pqtree_right_sibling(child)) =
	    pqtree_left_sibling(child);
         if (child == pqtree_leftmost_child(parent)) {
            pqtree_leftmost_child(parent) = pqtree_right_sibling(child);
            if (pqtree_type(parent) == Q_NODE) {
	       pqtree_immed_siblings(pqtree_right_sibling(child))--;
            } /* endif */
         } else if (child == pqtree_rightmost_child(parent)) {
            pqtree_rightmost_child(parent) = pqtree_left_sibling(child);
            if (pqtree_type(parent) == Q_NODE) {
               pqtree_immed_siblings(pqtree_left_sibling(child))--;
            } /* endif */
         } /* endif */
         pqtree_left_sibling(child)     = empty_pqtree;
         pqtree_right_sibling(child)    = empty_pqtree;
         pqtree_immed_siblings(child)   = 0;
         pqtree_parent(child)           = empty_pqtree;
         pqtree_child_count(parent)--;
      } /* endif */

      pqtree_full_child(parent) = empty_pqtree;
      pqtree_full_child_count(parent) = 0;
      pqtree_partial_child(parent) = empty_pqtree;
      pqtree_partial_child_count(parent) = 0;
      pqtree_full_succ(child) = empty_pqtree;
      pqtree_partial_succ(child) = empty_pqtree;


/* if (pqtree_label(child) == FULL) {
 *    remove child from the full_children-list of father *
      pqtree_full_child_count(parent)--;
      if (child != pqtree_full_child(parent)) {
         help_child = pqtree_full_child(parent);
         while (pqtree_full_succ(help_child) != child) {
            help_child = pqtree_full_succ(help_child);
	 } * endwhile *
         pqtree_full_succ(help_child) = pqtree_full_succ(child);
      } else {
         pqtree_full_child(parent) = pqtree_full_succ(child);
      } * endif *
      pqtree_full_succ(child) = empty_pqtree;

   } else if (pqtree_label(child) == PARTIAL) {
 *    remove child from the partial_children-list of father *
      pqtree_partial_child_count(parent)--;
      if (child != pqtree_partial_child(parent)) {
         help_child = pqtree_partial_child(parent);
         while (pqtree_partial_succ(help_child) != child) {
            help_child = pqtree_partial_succ(help_child);
	 } * endwhile *
	 pqtree_partial_succ(help_child) = pqtree_partial_succ(child);
      } else {
         pqtree_partial_child(parent) = pqtree_partial_succ(child);
      } * endif *
      pqtree_partial_succ(child) = empty_pqtree;

   } * endif *
*/
   return parent;
   } else {
      return empty_pqtree;
   } /* endif */
}


Global void	delete_leaf_in_proper_PQtree(PQtree leaf)
{
   PQtree	parent, grand_parent, child;

   parent = pqtree_parent(leaf);
   if (parent != empty_pqtree) {
      if (pqtree_type(parent) == P_NODE) {
	 if (leaf == pqtree_leftmost_child(parent)) {
	    pqtree_leftmost_child(parent) = pqtree_right_sibling(leaf);
	 } else if (leaf == pqtree_rightmost_child(parent)) {
	    pqtree_rightmost_child(parent) = pqtree_left_sibling(leaf);
	 } /* endif */
	 pqtree_left_sibling(pqtree_right_sibling(leaf)) =
		pqtree_left_sibling(leaf);
	 pqtree_right_sibling(pqtree_left_sibling(leaf)) =
		pqtree_right_sibling(leaf);
	 pqtree_child_count(parent)--;

      } else { /* pqtree_type(parent) = Q_NODE */
	 if (leaf == pqtree_leftmost_child(parent)) {
	    pqtree_leftmost_child(parent) = pqtree_right_sibling(leaf);
	    pqtree_immed_siblings(pqtree_right_sibling(leaf))--;
	 } else if (leaf == pqtree_rightmost_child(parent)) {
	    pqtree_rightmost_child(parent) = pqtree_left_sibling(leaf);
	    pqtree_immed_siblings(pqtree_left_sibling(leaf))--;
	 } /* endif */
	 pqtree_left_sibling(pqtree_right_sibling(leaf)) =
		pqtree_left_sibling(leaf);
	 pqtree_right_sibling(pqtree_left_sibling(leaf)) =
		pqtree_right_sibling(leaf);
	 pqtree_child_count(parent)--;

	 if (pqtree_child_count(parent) == 2) {
	    pqtree_type(parent) = P_NODE;
	    pqtree_immed_siblings(pqtree_leftmost_child(parent)) = 0;
	    pqtree_immed_siblings(pqtree_rightmost_child(parent)) = 0;
	 } /* endif */

      } /* endif */
      pqtree_full_child_count(parent) 		= 0;
      pqtree_full_child(parent) 	  	= empty_pqtree;
      pqtree_partial_child_count(parent)	= 0;
      pqtree_partial_child(parent)		= empty_pqtree;
      FREE_PQtree(leaf);

      if (pqtree_child_count(parent) == 1) {
	 grand_parent = pqtree_parent(parent);
	 if (grand_parent != empty_pqtree) {
	    child = pqtree_leftmost_child(parent);
	    pqtree_parent(child) = grand_parent;
	    pqtree_left_sibling(child) = pqtree_left_sibling(parent);
	    pqtree_right_sibling(child) = pqtree_right_sibling(parent);
	    pqtree_right_sibling(pqtree_left_sibling(child)) = child;
	    pqtree_left_sibling(pqtree_right_sibling(child)) = child;
	    pqtree_immed_siblings(child) = pqtree_immed_siblings(parent);
	    if (parent == pqtree_leftmost_child(grand_parent)) {
	       pqtree_leftmost_child(grand_parent) = child;
	    } else if (parent == pqtree_rightmost_child(grand_parent)) {
	       pqtree_rightmost_child(grand_parent) = child;
	    } /* endif *//*          delete father */
	    pqtree_full_child_count(grand_parent) 	= 0;
	    pqtree_full_child(grand_parent) 	  	= empty_pqtree;
	    pqtree_partial_child_count(grand_parent) 	= 0;
	    pqtree_partial_child(grand_parent)	     	= empty_pqtree;
	    FREE_PQtree(parent);
	 } /* endif */
      } /* endif */
   } /* endif */
}

Global void free_complete_PQtree(PQtree tree)
{
	PQtree child, help_child;

     child = pqtree_leftmost_child(tree);
     if (child != empty_pqtree) {
	pqtree_right_sibling(pqtree_rightmost_child(tree)) = empty_pqtree;
	while (child != empty_pqtree) {
	   help_child = pqtree_right_sibling(child);
	   free_complete_PQtree(child);
	   child = help_child;
	} /* endwhile */
     } /* endif */
     FREE_PQtree(tree);
}



Global PQtree Create_Initial_PQtree(Snode node)
{
   PQtree   tree;

   if (node_stnumber(node) == 1) {
      tree                      = Init_PQnode(LEAF);
/*    pqtree_graph_node(tree)   = node; */
      pqtree_corresponding_edge(tree) = empty_sedge;
      pqtree_entry(tree)        = 1;
   } else {
/*    should never occur                                                    */
   } /* endif */

   return tree;
}


Global PQtree Create_Pnode_for_N(int number, Snode node)
{
   PQtree   tree, leaf;
   Snode    adj_node;
   Sedge    edge;

   if (node_stnumber(node) == number) {
      tree                      = Init_PQnode(P_NODE);
/*    pqtree_graph_node(tree)   = node; */
      pqtree_entry(tree)        = number;
      for_sourcelist(node, edge) {
         adj_node = edge->tnode;
/*       betrachte die Kanten als gerichtet bzgl. st-numerierung            */
         if (node_stnumber(node) < node_stnumber(adj_node)) {
            leaf                       = Init_PQnode(LEAF);
/*          pqtree_graph_node(leaf)    = adj_node; */
            pqtree_corresponding_edge(leaf) = edge;
	    pqtree_entry(leaf)         = node_stnumber(adj_node);
            enqueue_child(tree, leaf);
         } /* endif */
      } end_for_sourcelist(node, edge);
      if (pqtree_child_count(tree) == 1) {
         tree = delete_child(leaf);
         FREE_PQtree(tree);
	 return leaf;
      } /* endif */
      pqtree_d(tree) = pqtree_child_count(tree);
   } else {
/*    should never occur                                                    */
   } /* endif */ 

   return tree;
}


Global PQtree Replace_Leaf(PQtree root, PQtree leaf, PQtree tree)
{
/*   PQtree	help_node; */

/* needs the field corresponding edge of leaf to be copied *
 * to the field corresponding_edge of the root of the new subtree ???? */
/* NOTE: pqtree_entry(leaf) need not be the same as pqtree_entry(tree) *
 * since in create_P_node_for_N parents with only one child are discarded */

   if (leaf != root) {
      pqtree_parent(tree) = pqtree_parent(leaf);
      if (leaf == pqtree_left_sibling(leaf)) {
/*       leaf is only child                                                 */
#        ifdef DEBUG
	 fprintf(pqfile, "\nleaf is only child");
#        endif
	 pqtree_left_sibling(tree) = tree;
	 pqtree_right_sibling(tree) = tree;
      } else {
	 pqtree_left_sibling(tree) = pqtree_left_sibling(leaf);
	 pqtree_right_sibling(pqtree_left_sibling(tree)) = tree;
	 pqtree_right_sibling(tree) = pqtree_right_sibling(leaf);
	 pqtree_left_sibling(pqtree_right_sibling(tree)) = tree;
      } /* endif */
      pqtree_immed_siblings(tree) = pqtree_immed_siblings(leaf);
      if (leaf == pqtree_leftmost_child(pqtree_parent(leaf))) {
	 pqtree_leftmost_child(pqtree_parent(leaf)) = tree;
      } /* endif */
      if (leaf == pqtree_rightmost_child(pqtree_parent(leaf))) {
	 pqtree_rightmost_child(pqtree_parent(leaf)) = tree;
      } /* endif */
/*    need any properties be synthesized from child to parent node (root) ? */
/*    all necessary work is done in Search_leaves */
/*    if (pqtree_d(tree) > 0) {
         help_node = pqtree_parent(tree);
	 while (help_node != empty_pqtree) {
	    pqtree_l(help_node) = pqtree_l(help_node) + pqtree_d(tree) - 1;
            help_node = pqtree_parent(help_node);
         } * endwhile *
      } * endif */
   } else {
      root = tree;
   } /* endif */


   FREE_PQtree(leaf);

   return root;
}










