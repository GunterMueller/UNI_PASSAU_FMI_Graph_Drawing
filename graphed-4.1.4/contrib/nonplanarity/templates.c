/****************************************************************************\
 *                                                                          *
 *  template.c                                                              *
 *  ----------                                                              *
 *                                                                          *
 *  author:  a.j. winter (11027)  04/93.                                    *
 *                                                                          *
\****************************************************************************/


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

#include "pqhandle.h"


#if defined SUN_VERSION
Local PQtree create_collector_for_full_children(PQtree X);
Local PQtree create_collector_for_empty_children(PQtree X);
Local void reverse_children_of_PQnode(PQtree root);
Local int check_oneway_order(PQtree X);
Local bool check_twoway_order(PQtree X);
PQtree merge_Q_nodes(PQtree left, PQtree right);
Local void discard_partial_child(PQtree Q_root, PQtree Q_child, int reverse_flag);
#else
Local PQtree create_collector_for_full_children(PQtree);
Local PQtree create_collector_for_empty_children(PQtree);
Local void reverse_children_of_PQnode(PQtree);
Local int check_oneway_order(PQtree);
Local bool check_twoway_order(PQtree);
PQtree merge_Q_nodes(PQtree, PQtree);
Local void discard_partial_child(PQtree, PQtree, int);
#endif

/* recall: if any error occurs using this module it is probably *
 * that any order on order PQ-nodes is assumed, that is actually not given */
/* see further functions and procedures for details */
/* especially: what is the order of children in partial-children *
 * (may partial children again have partial children, must this testing *
 * performed recursively, or may it be assumed that the grandchildren of *
 * any node are already in a correct order, by any previous *
 * pattern-matching step */

/* further question: need the flags *
 * persistent_child_count, persistent_leaf_count be concerned, when *
 * deleting any child of any node, i.e. does the child-node pass *
 * these values on to its father ??? *
 * or are these values only needed for the previous BUBBLE step */


/* notice that all root nodes will be returned by the order *
 *  empty children...partial children(*)...full children *
 * (*) (may be recursively arranged children) (could be really important) *
 * the only exception allowed is for the pertinent subtree, that may *
 * contain more than one partial child, because when merging two partial *
 * children it is not possible to maintain that order. *
 * but in all (most) templates this order is assumed. */



#ifdef DEBUG
   extern FILE *pqfile;
#endif


Local PQtree create_collector_for_full_children(PQtree X)
{
   PQtree   NEW_P, child, full_child;

/* create new P_NODE that will collect all full children of X as own */
/* children and delete them from childlist of X so that all full children */
/* are consecutive in P_node */
/* if there is only one child in X it will be deleted from X and returned */

#  ifdef DEBUG
   fprintf(pqfile, 
	   "\n  entering full_collector for %d with %d full children",
	   pqtree_entry(X), pqtree_full_child_count(X));
   fflush(pqfile);
#  endif


   if (pqtree_full_child_count(X) == 1) {
      full_child = pqtree_full_child(X);

      pqtree_left_sibling(pqtree_right_sibling(full_child)) =
		     pqtree_left_sibling(full_child);
      pqtree_right_sibling(pqtree_left_sibling(full_child)) =
                     pqtree_right_sibling(full_child);

      pqtree_child_count(X)--;
      pqtree_full_child_count(X) = 0;
      pqtree_full_child(X) = empty_pqtree;

      if (full_child == pqtree_leftmost_child(X)) {
         pqtree_leftmost_child(X) =
            pqtree_right_sibling(full_child);
      } else if (full_child == pqtree_rightmost_child(X)) {
         pqtree_rightmost_child(X) =
            pqtree_left_sibling(full_child);
      } /* endif */
#     ifdef DEBUG
      fprintf(pqfile, "\n  leaving full_collector, returning %d",
	      pqtree_entry(full_child));
      fflush(pqfile);
#     endif

      return full_child;
   } /* endif */

   NEW_P = Init_PQnode(P_NODE);
   pqtree_label(NEW_P) = FULL;
/* remove full children in child-list of X */
/* removal of children may not be performed in a for_all_children-loop */
/* full children may be removed without saving them, because they are  */
/* included in the full-children-list in X */
   child = pqtree_leftmost_child(X);
   while (child != pqtree_rightmost_child(X)) {
      if (pqtree_label(child) == FULL) {
         pqtree_left_sibling(pqtree_right_sibling(child)) =
                     pqtree_left_sibling(child);
	 pqtree_right_sibling(pqtree_left_sibling(child)) =
                     pqtree_right_sibling(child);
         pqtree_child_count(X)--;
         full_child = child;
         child = pqtree_right_sibling(child);
         enqueue_child(NEW_P, full_child);
      } else {
	 child = pqtree_right_sibling(child);
      } /* endif */
   } /* endwhile */
   if (pqtree_label(child) == FULL) {
      pqtree_left_sibling(pqtree_right_sibling(child)) =
                  pqtree_left_sibling(child);
      pqtree_right_sibling(pqtree_left_sibling(child)) =
		  pqtree_right_sibling(child);
      pqtree_rightmost_child(X) = pqtree_left_sibling(child);
      pqtree_child_count(X)--;
      enqueue_child(NEW_P, child);
   } /* endif */

   pqtree_leftmost_child(X) =
	  pqtree_right_sibling(pqtree_rightmost_child(X));

   pqtree_full_child_count(X) = 0;
   pqtree_full_child(X) = empty_pqtree;
#  ifdef DEBUG
   fprintf(pqfile, "\n  leaving full_collector, returning %d",
	   pqtree_entry(NEW_P));
   fflush(pqfile);
#  endif

   return NEW_P;
}


Local PQtree create_collector_for_empty_children(PQtree X)
{
   PQtree   NEW_P, child, empty_child;

/* create new P_NODE that will be son of X and that will collect */
/* all empty children of X as own children so that all empty children */
/* are consecutive */
/* if there is only one child in X it will be deleted from X and returned */
 
#  ifdef DEBUG
   fprintf(pqfile, 
           "\n  entering empty_collector for %d with %d empty children",
           pqtree_entry(X), pqtree_empty_child_count(X));
   fflush(pqfile);
#  endif

   if (pqtree_empty_child_count(X) == 1) {
      for_all_children(X, child) {
         if (pqtree_label(child) == EMPTY) {
            empty_child = child;
	 }  /* endif */
      } end_for_all_children(X, child);

      pqtree_left_sibling(pqtree_right_sibling(empty_child)) =
                     pqtree_left_sibling(empty_child);
      pqtree_right_sibling(pqtree_left_sibling(empty_child)) =
                     pqtree_right_sibling(empty_child);

      pqtree_child_count(X)--;
      if (empty_child == pqtree_leftmost_child(X)) {
         pqtree_leftmost_child(X) =
            pqtree_right_sibling(empty_child);
      } else if (empty_child == pqtree_rightmost_child(X)) {
         pqtree_rightmost_child(X) =
	    pqtree_left_sibling(empty_child);
      } /* endif */

#     ifdef DEBUG
      fprintf(pqfile, "\n  leaving empty_collector, returning %d",
              pqtree_entry(empty_child));
      fflush(pqfile);
#     endif

      return empty_child;
   } /* endif */

   NEW_P = Init_PQnode(P_NODE);
   pqtree_label(NEW_P) = EMPTY;
/* remove empty children in child-list of X */
/* removal of children may not be performed in a for_all_children-loop */
/* empty children may not be removed without saving them, because they are  */
/* not included in any list of X */
   child = pqtree_leftmost_child(X);
   while (child != pqtree_rightmost_child(X)) {
      if (pqtree_label(child) == EMPTY) {
	 pqtree_left_sibling(pqtree_right_sibling(child)) =
                     pqtree_left_sibling(child);
         pqtree_right_sibling(pqtree_left_sibling(child)) =
                     pqtree_right_sibling(child);
         pqtree_child_count(X)--;
         empty_child = child;
         child = pqtree_right_sibling(child);
	 enqueue_child(NEW_P, empty_child);
      } else {
         child = pqtree_right_sibling(child);
      } /* endif */
   } /* endwhile */
   if (pqtree_label(child) == EMPTY) {
      pqtree_left_sibling(pqtree_right_sibling(child)) =
		  pqtree_left_sibling(child);
      pqtree_right_sibling(pqtree_left_sibling(child)) =
                  pqtree_right_sibling(child);
      pqtree_rightmost_child(X) = pqtree_left_sibling(child);
      pqtree_child_count(X)--;
      enqueue_child(NEW_P, child);
   } /* endif */

   pqtree_leftmost_child(X) =
          pqtree_right_sibling(pqtree_rightmost_child(X));

#  ifdef DEBUG
   fprintf(pqfile, "\n  leaving empty_collector, returning %d",
           pqtree_entry(NEW_P));
   fflush(pqfile);
#  endif
   return NEW_P;
}


Local void reverse_children_of_PQnode(PQtree root)
{
   PQtree   child, help_child;

   child = pqtree_leftmost_child(root);
   do {
      help_child = child;
      child = pqtree_right_sibling(child);
      pqtree_right_sibling(help_child) = pqtree_left_sibling(help_child);
      pqtree_left_sibling(help_child) = child;
   } while (child != pqtree_leftmost_child(root)); /* enddo */
   pqtree_leftmost_child(root) = pqtree_rightmost_child(root);
   pqtree_rightmost_child(root) = child;
}


Local int check_oneway_order(PQtree X)
{
   PQtree   child;
   short    akt_label;
   bool     is_ordered;
/* only check consecutivity of children nodes by the following order:
 *    empty children as leftmost *
 *    partial children (suppose children of partial node are already *
 *         given in the correct order, by any previous reduce-step) *
 *         if not so, check children of partial node recursively ???? *
 *    full children as rightmost */

/* any of the above given kinds of children may be missing */

/* for Q-node it is possible that the order is exactly reversed *
 * then order children according to the above order (i.e. reverse Q-node) */
/* this option should not be excessively used, for this reason it *
 * will only be activated if it is necessary */

/* check for correct order */
   is_ordered = true;
   akt_label = EMPTY;

   for_all_children (X, child) {
      if (pqtree_label(child) != akt_label) {
/*       label of child changed */
	 while ((akt_label != pqtree_label(child))
	     && (akt_label != FULL)) {
	    akt_label = next_label(akt_label);
         } /* endwhile */
         if (pqtree_label(child) != akt_label) {
/*          label of child is not the next in label-order */
	    is_ordered = false;
         } /* endif */
      } /* endif */
   } end_for_all_children (X, child);

   if (is_ordered) {
      return ASCENDING;
   } /* endif */

/* check for inverse order */
   is_ordered = true;
   akt_label = FULL;

   for_all_children (X, child) {
      if (pqtree_label(child) != akt_label) {
/*       label of child changed */
         while ((akt_label != pqtree_label(child))
             && (akt_label != EMPTY)) {
            akt_label = prev_label(akt_label);
         } /* endwhile */
         if (pqtree_label(child) != akt_label) {
/*          label of child is not the previous in label-order */
            is_ordered = false;
         } /* endif */
      } /* endif */
   } end_for_all_children (X, child);
 
   if (is_ordered) {
      return DESCENDING;
   } /* endif */


/* children of node must be reversed */
/* should this case actually occur ??? - wait and see if necessary */
/* reverse_children_of_PQnode(X); */

   return NOORDER;
}


Local bool check_twoway_order(PQtree X)
{
   PQtree   child;
   short    partial_seen;
/* only check consecutivity of children nodes by the following order:
 *    empty children as leftmost *
 *    partial child (suppose children of partial node are already *
 *         given in the correct order, by any previous reduce-step) *
 *         if not so, check children of partial node recursively ???? *
 *    full children *
 *    partial child with its children in descending order *
 *    empty_children as rightmost */

/* any of the above given kinds of children except partial children may be *
 * missingthe question is, wether the children of the partial nodes are *
 * already in the needed order, or if there must be performed a recursive *
 * step to order them ???? */

/* the idea is to mark the partial children and to check the children *
 * between them, resp. to the left and to the right of them: *
 * in detail: *
 * take the leftmost child, there may be 0 or more empty children before *
 * the first partial child (which of the two is the leftmost ???? ) is met *
 * between the two partial children there may be only full children *
 * and finally to the right of the right partial child (question see above) *
 * there may be only empty children */

/* notice again that there is no recursive testing of *
 * the partial children regarding their order of empty and full children */

/* note further that there need not be exactly two partial children, *
 * in fact there need not be any partial child at all */

/* check for correct order */
/* partial_seen serves as a kind of set of states for a Finite Automata */
   partial_seen = 0;

   if ((pqtree_label(pqtree_leftmost_child(X)) == FULL) ||
       (pqtree_label(pqtree_rightmost_child(X)) == FULL)) {
      return false;
   } /* endif */

   for_all_children (X, child) {
      if (partial_seen == 0) {
	 if ((pqtree_label(child) == PARTIAL) ||
             (pqtree_label(child) == FULL)) {
            partial_seen = 1;
	 } /* endif */
      } else if (partial_seen == 1) {
	 if ((pqtree_label(child) == PARTIAL) ||
	     (pqtree_label(child) == EMPTY)) {
            partial_seen = 2;
	 } /* endif */
      } else if (partial_seen == 2) {
	 if (pqtree_label(child) != EMPTY) {
	    return false;
	 } /* endif */
      } /* endif */
   } end_for_all_children(X, child); 

   return true;
}


PQtree merge_Q_nodes(PQtree left, PQtree right)
{
/* a proper call with only Q-nodes affected is assumed */

/* since in principle empty children are put left to full ones in any node *
 * a special care is necessary when merging to Q-nodes, *
 * so that full children remain or become consecutive in that new Q-node */
/* in particular: the order in the second Q-node, given by the *
 * sibling-pointers must be reversed */

   PQtree   child;

#  ifdef DEBUG
   fprintf(pqfile, "\n  entering merge Q-Nodes");
   fflush(pqfile);
#  endif
    if (right == NULL) {
#      ifdef DEBUG
       fprintf(pqfile, "\n  leaving merge Q-Nodes");
       fflush(pqfile);
#      endif
       return left;
    } else if (left == NULL) {
#      ifdef DEBUG
       fprintf(pqfile, "\n  leaving merge Q-Nodes");
       fflush(pqfile);
#      endif
       return right;
    } else {
       if (check_oneway_order(left) != ASCENDING) {
	  reverse_children_of_PQnode(left);
       }
       if (check_oneway_order(right) != DESCENDING) {
	  reverse_children_of_PQnode(right);
       }
#      ifdef DEBUG
       fprintf(pqfile, "\n     reversed Q-Nodes if necessary");
       fflush(pqfile);
#      endif

       for_all_children(right, child) {
	  pqtree_parent(child) = left;
       } end_for_all_children(right, child);
       pqtree_child_count(pqtree_parent(left))--;
       pqtree_partial_child_count(pqtree_parent(left))--;
       pqtree_partial_child(pqtree_parent(left)) = left;
       pqtree_partial_succ(left) = empty_pqtree;
       pqtree_full_succ(left)    = empty_pqtree;  /* was NULL */

       pqtree_left_sibling(pqtree_right_sibling(right)) = pqtree_left_sibling(right);
       pqtree_right_sibling(pqtree_left_sibling(right)) = pqtree_right_sibling(right);
       if (right == pqtree_rightmost_child(pqtree_parent(right))) {
          pqtree_rightmost_child(pqtree_parent(right)) =
             pqtree_left_sibling(right);
       } /* endif */
       if (right == pqtree_leftmost_child(pqtree_parent(right))) {
          pqtree_leftmost_child(pqtree_parent(right)) =
             pqtree_right_sibling(right);
       } /* endif */

       pqtree_child_count(left) = pqtree_child_count(left) +
                                  pqtree_child_count(right);

/*     pqtree_pertinent_child_count(left) =
          pqtree_pertinent_child_count(left) +
          pqtree_pertinent_child_count(right);

       pqtree_pertinent_leaf_count(left) =
          pqtree_pertinent_leaf_count(left) +
	  pqtree_pertinent_leaf_count(right);

       pqtree_full_child_count(left) =
          pqtree_full_child_count(left) +
          pqtree_full_child_count(right);

       pqtree_partial_child_count(left) =
	  pqtree_partial_child_count(left) +
          pqtree_partial_child_count(right);
*/
/*     child = pqtree_partial_child(left);
       if (child != NULL) {
          ...
       } else {
	  ...
       } * endif *
       while (pqtree_partial_succ(child) != empty_pqtree) {
          child = pqtree_partial_succ(child);
       } * endwhile *
       pqtree_partial_succ(child) = pqtree_partial_child(right);

       child = pqtree_full_child(left);
       if (child != NULL) {
          ...
       } else {
          ...
       } * endif *
       while (pqtree_full_succ(child) != empty_pqtree) {
	  child = pqtree_full_succ(child);
       } * endwhile *
       pqtree_full_succ(child) = pqtree_full_child(right);
*/
       pqtree_right_sibling(pqtree_rightmost_child(left)) =
	  pqtree_leftmost_child(right);
       pqtree_immed_siblings(pqtree_rightmost_child(left))++;
       pqtree_left_sibling(pqtree_leftmost_child(right)) =
	  pqtree_rightmost_child(left);
       pqtree_immed_siblings(pqtree_leftmost_child(right))++;
       pqtree_right_sibling(pqtree_rightmost_child(right)) =
          pqtree_leftmost_child(left);
       pqtree_left_sibling(pqtree_leftmost_child(left)) =
          pqtree_rightmost_child(right);
       pqtree_rightmost_child(left) = pqtree_rightmost_child(right);
 
       FREE_PQtree(right);
#      ifdef DEBUG
       fprintf(pqfile, "\n  leaving merge Q-Nodes");
       fflush(pqfile);
#      endif
       return left;
    } /* endif */
}


Local void discard_partial_child(PQtree Q_root, PQtree Q_child, int reverse_flag)
                        
   	                 /* value computed by check_oneway_order for Q_root */
{
/* put all children of the Q_child in the given or reversed order between *
 * the empty children and the full children of Q_root */
 

   PQtree child, left_sibling, right_sibling;
   int		reverse_child_flag;

/* there is only one partial_child in Q_root to be discarded per step */
#  ifdef DEBUG
   fprintf(pqfile, 
           "\n  entering discard_partial_child for father %d with child %d",
           pqtree_entry(Q_root), pqtree_entry(Q_child));
   fflush(pqfile);
#  endif
   for_all_children(Q_child, child) {
      pqtree_parent(child) = Q_root;
   } end_for_all_children(Q_child, child);
   pqtree_partial_child(Q_root) = pqtree_partial_succ(Q_child);
   pqtree_partial_child_count(Q_root)--;
   pqtree_full_child_count(Q_root) =
      pqtree_full_child_count(Q_root) +
      pqtree_full_child_count(Q_child);
   child = pqtree_full_child(Q_child);
/* since Q_child is partial, child is surely not NULL */
   while (pqtree_full_succ(child) != empty_pqtree) {
      child = pqtree_full_succ(child);
   } /* endwhile */
   pqtree_full_succ(child) = pqtree_full_child(Q_root);
   pqtree_full_child(Q_root) = pqtree_full_child(Q_child);
   pqtree_child_count(Q_root) =
      pqtree_child_count(Q_root) + pqtree_child_count(Q_child) - 1;

/*       pqtree_pertinent_child_count(left) =     ??????
	  pqtree_pertinent_child_count(left) +
          pqtree_pertinent_child_count(right);

       pqtree_pertinent_leaf_count(left) =
          pqtree_pertinent_leaf_count(left) +
          pqtree_pertinent_leaf_count(right); */

   left_sibling  = pqtree_left_sibling(Q_child);
   right_sibling = pqtree_right_sibling(Q_child);
   if (reverse_flag != NOORDER) {
      reverse_child_flag = check_oneway_order(Q_child);
      if (reverse_child_flag != reverse_flag) {
	 reverse_children_of_PQnode(Q_child);
      } /* endif */

   } else {
      reverse_child_flag = check_oneway_order(Q_child);
      if ((reverse_child_flag == ASCENDING) &&
	  (pqtree_label(right_sibling) != FULL) &&
	  (pqtree_label(right_sibling) != PARTIAL)) {
	 reverse_children_of_PQnode(Q_child);
      } else if ((reverse_child_flag == DESCENDING) &&
		 (pqtree_label(left_sibling) != FULL) &&
		 (pqtree_label(left_sibling) != PARTIAL)) {
	 reverse_children_of_PQnode(Q_child);
      } /* endif */
   } /* endif */
   pqtree_right_sibling(left_sibling) = pqtree_leftmost_child(Q_child);
   pqtree_left_sibling(pqtree_leftmost_child(Q_child)) = left_sibling;
   pqtree_left_sibling(right_sibling) = pqtree_rightmost_child(Q_child);
   pqtree_right_sibling(pqtree_rightmost_child(Q_child)) = right_sibling;

   pqtree_immed_siblings(pqtree_leftmost_child(Q_child)) =
     pqtree_immed_siblings(Q_child);
   pqtree_immed_siblings(pqtree_rightmost_child(Q_child)) =
     pqtree_immed_siblings(Q_child);
   if (Q_child == pqtree_leftmost_child(Q_root)) {
      pqtree_leftmost_child(Q_root) = pqtree_leftmost_child(Q_child);
      pqtree_immed_siblings(pqtree_rightmost_child(Q_child)) = 2;

   } /* endif */
   if (Q_child == pqtree_rightmost_child(Q_root)) {
      pqtree_rightmost_child(Q_root) = pqtree_rightmost_child(Q_child);
      pqtree_immed_siblings(pqtree_leftmost_child(Q_child)) = 2;

   } /* endif */


/* for_all_children(Q_root, child) {
      pqtree_immed_siblings(child) = 2;
   } end_for_all_children(Q_root, child);
   pqtree_immed_siblings(pqtree_leftmost_child(Q_root))  = 1;
   pqtree_immed_siblings(pqtree_rightmost_child(Q_root)) = 1; */

   FREE_PQtree(Q_child);
#  ifdef DEBUG
   fprintf(pqfile, "\n  leaving discard_partial_child");
   fflush(pqfile);
#  endif
}
 
 

Global bool TEMPLATE_L1(PQtree X)
{
   if (pqtree_type(X) != LEAF) {
      return false;
   } /* endif */
#  ifdef DEBUG
   fprintf(pqfile, "\nmatching X (%d) with template L1",
           pqtree_entry(X));
#  endif

/* kann X schon FULL sein ????  */
   pqtree_label(X) = FULL;
   if (pqtree_parent(X) != empty_pqtree) {
      pqtree_full_child_count(pqtree_parent(X))++;
      pqtree_full_succ(X) = pqtree_full_child(pqtree_parent(X));
      pqtree_full_child(pqtree_parent(X)) = X;
      return true;
  } /* endif */

# ifdef DDEBUG
  message("\nmatch with template L1 complete.");
# endif
  return true;
}


Global bool TEMPLATE_P0(PQtree X)
{
/* markiere P-node mit nur Empty kindern EMPTY */
   if (pqtree_type(X) != P_NODE) {
      return false;
   } /* endif */
#  ifdef DEBUG
   fprintf(pqfile, "\nmatching X (%d) with template P0",
           pqtree_entry(X));
#  endif

   if (pqtree_empty_child_count(X) == pqtree_child_count(X)) {
     pqtree_label(X) = EMPTY;
#    ifdef DDEBUG
     message("\nmatch with template P0 complete.");
#    endif
     return true;
   } /* endif */
   return false;
}


Global bool TEMPLATE_P1(PQtree X)
{
   if (pqtree_type(X) != P_NODE) {
      return false;
   } /* endif */
#  ifdef DEBUG
   fprintf(pqfile, "\nmatching X (%d) with template P1",
           pqtree_entry(X));
#  endif

   if (pqtree_full_child_count(X) == pqtree_child_count(X)) {
      pqtree_label(X) = FULL;
      if (pqtree_parent(X) != empty_pqtree) {
         pqtree_full_child_count(pqtree_parent(X))++;
         pqtree_full_succ(X) = pqtree_full_child(pqtree_parent(X));
         pqtree_full_child(pqtree_parent(X)) = X;
      } /* endif */
#     ifdef DDEBUG
      message("\nmatch with template P1 complete.");
#     endif
      return true;
   } /* endif */
   return false;
}


Global bool TEMPLATE_P2(PQtree X)
{
   PQtree NEW_P;
   
   if (pqtree_type(X) != P_NODE) {
      return false;
   } /* endif */
#  ifdef DEBUG
   fprintf(pqfile, "\nmatching X (%d) with template P2",
           pqtree_entry(X));
#  endif

   if (pqtree_partial_child_count(X) > 0) {
      return false;
   } else if (pqtree_full_child_count(X) == 0 ) {
      return false;
   } else if (pqtree_empty_child_count(X) == 0) {
      return false;
   } else if (pqtree_full_child_count(X) == 1) {
#     ifdef DEBUG
      message("\nmatch with template P2..1 complete.");
#     endif
      return true;
   } else {
      NEW_P = create_collector_for_full_children(X);
      enqueue_child(X, NEW_P);

/*    markers and counters for X need not to be set since X is root */

#    ifdef DDEBUG
     message("\nmatch with template P2..2 complete.");
#    endif
     return true;
   } /* endif */

/* something went wrong */
/* return false; */
}


Global bool TEMPLATE_P3(PQtree X)
{
   PQtree   FULL_ROOT, EMPTY_ROOT;

   if (pqtree_type(X) != P_NODE) {
      return false;
   } /* endif */
#  ifdef DEBUG
   fprintf(pqfile, "\nmatching X (%d) with template P3",
           pqtree_entry(X));
#  endif

   if (pqtree_partial_child_count(X) > 0) {
      return false;
   } else if (pqtree_full_child_count(X) == 0 ) {
      return false;
   } else if (pqtree_empty_child_count(X) == 0) {
      return false;
   } else {

      pqtree_type(X) = Q_NODE;
      pqtree_label(X) = PARTIAL;
      if (pqtree_full_child_count(X) > 1) {
         FULL_ROOT = create_collector_for_full_children(X);
         enqueue_child(X, FULL_ROOT);
      } /* endif */

      if (pqtree_empty_child_count(X) > 1) {
	 EMPTY_ROOT = create_collector_for_empty_children(X);
         enqueue_child(X, EMPTY_ROOT);
      } /* endif */

/*    TEST: pqtree_child_count(X) muss anschliessend 2 sein */
/*    every child of X must have entry 1 in pqtree_immed_siblings, */
/*    since there was a change from P-node to Q-node and X does only have */
/*    two children */
      pqtree_immed_siblings(pqtree_leftmost_child(X))  = 1;
      pqtree_immed_siblings(pqtree_rightmost_child(X)) = 1;

/*    war ex p-node X schon als partial-node eingetragen */
/*    gebe an parent(X) zaehler fuer partial_child weiter */

      if (pqtree_parent(X) != empty_pqtree) {
         pqtree_partial_child_count(pqtree_parent(X))++;
         pqtree_partial_succ(X) = pqtree_partial_child(pqtree_parent(X));
         pqtree_partial_child(pqtree_parent(X)) = X;
      } /* endif */

#     ifdef DDEBUG
      message("\nmatch with template P3 complete.");
#     endif
      return true;
   } /* endif */

/* something went wrong */
/* return false; */ 
}


Global bool TEMPLATE_P4(PQtree X)
{
   PQtree   FULL_ROOT, PARTIAL_ROOT,
	    child;

   if (pqtree_type(X) != P_NODE) {
      return false;
   } /* endif */
#  ifdef DEBUG
   fprintf(pqfile, "\nmatching X (%d) with template P4",
	   pqtree_entry(X));
#  endif

   if (pqtree_partial_child_count(X) != 1) {
      return false;
   } else if (pqtree_full_child_count(X) == 0) {
#     ifdef DEBUG
      message("\nmatch with template P4..1 complete.");
#     endif
      return true;
   } else {
      FULL_ROOT = create_collector_for_full_children(X);
      PARTIAL_ROOT = pqtree_partial_child(X);
      enqueue_child(PARTIAL_ROOT, FULL_ROOT);
/*    all full-children of X (if there were any) have been moved to *
 *    the only partial-child (Q-node) of X */
      if (pqtree_empty_child_count(X) == 0) {
/*       if there is no empty child of X, the partial Q-node is the only *
 *       child of X, and this means that X itself needs to be removed and *
 *       instead its partial child will be new pertinent root. */
/*       but since X is not a variable parameter it may not be deleted and
 *       in its place its child will be removed and the children of the
 *       child will be children directly of the root. for this purpose all *
 *       counters and pointers of the root need to be set. *
 *       the disadvantage is, that all parent-entries of the new children *
 *       of the root do not remain valid. */
         pqtree_type(X) = Q_NODE;
         pqtree_label(X) = PARTIAL;
	 pqtree_marker(X) = UNMARKED;
         pqtree_leftmost_child(X) = pqtree_leftmost_child(PARTIAL_ROOT);
         pqtree_rightmost_child(X) = pqtree_rightmost_child(PARTIAL_ROOT);
         pqtree_child_count(X) = pqtree_child_count(PARTIAL_ROOT);
         pqtree_pertinent_child_count(X) = 
            pqtree_pertinent_child_count(PARTIAL_ROOT);
         pqtree_pertinent_leaf_count(X) =
	    pqtree_pertinent_leaf_count(PARTIAL_ROOT);
         pqtree_full_child_count(X) = pqtree_full_child_count(PARTIAL_ROOT);
         pqtree_full_child(X) = pqtree_full_child(PARTIAL_ROOT);
         pqtree_partial_child_count(X) = 
            pqtree_partial_child_count(PARTIAL_ROOT);  
         pqtree_partial_child(X) = pqtree_partial_child(PARTIAL_ROOT);
         pqtree_entry(X) = pqtree_entry(PARTIAL_ROOT);
	 for_all_children(X, child) {
            pqtree_parent(child) = X;
         } end_for_all_children(X, child);
         FREE_PQtree(PARTIAL_ROOT);
      } /* endif */

#     ifdef DDEBUG
      message("\nmatch with template P4..2 complete.");
#     endif
      return true;
   } /* endif */

/* something went wrong */
/* return false; */

}


Global bool TEMPLATE_P5(PQtree X)
{
   PQtree   FULL_ROOT, EMPTY_ROOT, PARTIAL_ROOT,
            child;

   if (pqtree_type(X) != P_NODE) {
      return false;
   } /* endif */
#  ifdef DEBUG
   fprintf(pqfile, "\nmatching X (%d) with template P5",
           pqtree_entry(X));
#  endif

   if (pqtree_partial_child_count(X) != 1) {
      return false;
   } else {
      if (pqtree_full_child_count(X) > 0) {
         FULL_ROOT = create_collector_for_full_children(X);
         PARTIAL_ROOT = pqtree_partial_child(X);
         enqueue_child(PARTIAL_ROOT, FULL_ROOT);
      } /* endif */

      if (pqtree_empty_child_count(X) > 0) {
	 EMPTY_ROOT = create_collector_for_empty_children(X);
         PARTIAL_ROOT = pqtree_partial_child(X);
         enqueue_child(PARTIAL_ROOT, EMPTY_ROOT);
      } /* endif */

/*    the partial Q-node is the only child of X, and this means that * 
 *    X itself needs to be removed and *
 *    instead its partial child will be new pertinent root. */
/*    but since X is not a variable parameter it may not be deleted and
 *    in its place its child will be removed and the children of the
 *    child will be children directly of the root. for this purpose all *
 *    counters and pointers of the root need to be set. *
 *    the disadvantage is, that all parent-entries of the new children *
 *    of the root do not remain valid. */
      pqtree_type(X) = Q_NODE;
      pqtree_label(X) = PARTIAL;
      pqtree_marker(X) = UNMARKED;
      pqtree_leftmost_child(X) = pqtree_leftmost_child(PARTIAL_ROOT);
      pqtree_rightmost_child(X) = pqtree_rightmost_child(PARTIAL_ROOT);
      pqtree_child_count(X) = pqtree_child_count(PARTIAL_ROOT);
      pqtree_pertinent_child_count(X) =
	 pqtree_pertinent_child_count(PARTIAL_ROOT);
      pqtree_pertinent_leaf_count(X) =
         pqtree_pertinent_leaf_count(PARTIAL_ROOT);
      pqtree_full_child_count(X) = pqtree_full_child_count(PARTIAL_ROOT);
      pqtree_full_child(X) = pqtree_full_child(PARTIAL_ROOT);
      pqtree_partial_child_count(X) =
         pqtree_partial_child_count(PARTIAL_ROOT);
      pqtree_partial_child(X) = pqtree_partial_child(PARTIAL_ROOT);
/*    pqtree_entry(X) = pqtree_entry(PARTIAL_ROOT); */
      for_all_children(X, child) {
         pqtree_parent(child) = X;
      } end_for_all_children(X, child);
      FREE_PQtree(PARTIAL_ROOT);

      if (pqtree_parent(X) != empty_pqtree) {
         pqtree_partial_child_count(pqtree_parent(X))++;
         pqtree_partial_succ(X) = pqtree_partial_child(pqtree_parent(X));
         pqtree_partial_child(pqtree_parent(X)) = X;
      } /* endif */

#     ifdef DDEBUG
      message("\nmatch with template P5 complete.");
#     endif
      return true;
   } /* endif */

/* something went wrong */
/* return false; */
}


Global bool TEMPLATE_P6(PQtree X)
{
   PQtree   FULL_ROOT, PARTIAL_ROOT, 
	    PARTIAL1_ROOT, PARTIAL2_ROOT,
	    child;

   if (pqtree_type(X) != P_NODE) {
      return false;
   } /* endif */
#  ifdef DEBUG
   fprintf(pqfile, "\nmatching X (%d) with template P6",
           pqtree_entry(X));
#  endif

   if (pqtree_partial_child_count(X) != 2) {
      return false;
   } else {
      PARTIAL1_ROOT = pqtree_partial_child(X);
      PARTIAL2_ROOT = pqtree_partial_succ(PARTIAL1_ROOT);
/*    ist left_partial_root wirklich left von RIGHT_PARTIAL_ROOT ??? *
 *    wichtig beim mergen, sonst "von Hand" suchen */

      if (pqtree_full_child_count(X) > 0) {
         FULL_ROOT = create_collector_for_full_children(X);
	 enqueue_child(PARTIAL1_ROOT, FULL_ROOT);
      } /* endif */
      PARTIAL_ROOT = merge_Q_nodes(PARTIAL1_ROOT, PARTIAL2_ROOT);
      if (pqtree_empty_child_count(X) == 0) {

/*       the partial Q-node is the only child of X, and this means that *
 *       X itself needs to be removed and *
 *       instead its partial child will be new pertinent root. */
/*       but since X is not a variable parameter it may not be deleted and
 *       in its place its child will be removed and the children of the
 *       child will be children directly of the root. for this purpose all *
 *       counters and pointers of the root need to be set. *
 *       the disadvantage is, that all parent-entries of the new children *
 *       of the root do not remain valid. */
	 pqtree_type(X) = Q_NODE;
         pqtree_label(X) = PARTIAL;
         pqtree_marker(X) = UNMARKED;
         pqtree_leftmost_child(X) = pqtree_leftmost_child(PARTIAL_ROOT);
         pqtree_rightmost_child(X) = pqtree_rightmost_child(PARTIAL_ROOT);
         pqtree_child_count(X) = pqtree_child_count(PARTIAL_ROOT);
         pqtree_pertinent_child_count(X) =
	    pqtree_pertinent_child_count(PARTIAL_ROOT);
         pqtree_pertinent_leaf_count(X) =
            pqtree_pertinent_leaf_count(PARTIAL_ROOT);
         pqtree_full_child_count(X) = pqtree_full_child_count(PARTIAL_ROOT);
         pqtree_full_child(X) = pqtree_full_child(PARTIAL_ROOT);
         pqtree_partial_child_count(X) =
            pqtree_partial_child_count(PARTIAL_ROOT);
	 pqtree_partial_child(X) = pqtree_partial_child(PARTIAL_ROOT);
         pqtree_entry(X) = pqtree_entry(PARTIAL_ROOT);
         for_all_children(X, child) {
            pqtree_parent(child) = X;
         } end_for_all_children(X, child);
         FREE_PQtree(PARTIAL_ROOT);
      } /* endif */

#     ifdef DDEBUG
      message("\nmatch with template P6 complete.");
#     endif
      return true;
   } /* endif */

/* something went wrong */
/* return false; */

}


Global bool TEMPLATE_Q0(PQtree X)
{
/* markiere P-node mit nur Empty kindern EMPTY */
   if (pqtree_type(X) != Q_NODE) {
      return false;
   } /* endif */
#  ifdef DEBUG
   fprintf(pqfile, "\nmatching X (%d) with template Q0",
           pqtree_entry(X));
#  endif

   if (pqtree_empty_child_count(X) == pqtree_child_count(X)) {
     pqtree_label(X) = EMPTY;
#    ifdef DDEBUG
     message("\nmatch with template Q0 complete.");
#    endif
     return true;
   } /* endif */
   return false;
}


Global bool TEMPLATE_Q1(PQtree X)
{
   if (pqtree_type(X) != Q_NODE) {
      return false;
   } /* endif */
#  ifdef DEBUG
   fprintf(pqfile, "\nmatching X (%d) with template Q1",
           pqtree_entry(X));
#  endif

   if (pqtree_full_child_count(X) == pqtree_child_count(X)) {
      pqtree_label(X) = FULL;
      if (pqtree_parent(X) != empty_pqtree) {
         pqtree_full_child_count(pqtree_parent(X))++;
         pqtree_full_succ(X) = pqtree_full_child(pqtree_parent(X));
         pqtree_full_child(pqtree_parent(X)) = X;
      } /* endif */
#     ifdef DDEBUG
      message("\nmatch with template Q1 complete.");
#     endif
      return true;
   } /* endif */
   return false;
}


Global bool TEMPLATE_Q2(PQtree X)
{
   int returncode;

   if (pqtree_type(X) != Q_NODE) {
      return false;
   } /* endif */
#  ifdef DEBUG
   fprintf(pqfile, "\nmatching X (%d) with template Q2",
           pqtree_entry(X));
#  endif

   if (pqtree_partial_child_count(X) > 1) {
/*    Q_NODE in the pattern of this template may contain at most one *
 *    partial node */
      return false;
   } else if (pqtree_partial_child_count(X) == 0) {
/*    Q_NODE does not contain any partial node */
      if ((pqtree_empty_child_count(X) == 0) 
       || (pqtree_full_child_count(X) == 0)) {
/*       Q_NODE may not contain children of only one kind */
         return false;
      } else {
/*       Q_NODE contains both empty and full children */
/*       check consecutivity of both kinds of children in this Q_NODE */
/*       if this property is given, then let Q_NODE unchanged, return true */
/*       else return false */

	 returncode = check_oneway_order(X);
	 if (returncode != NOORDER) {
	    pqtree_label(X) = PARTIAL;
	    if (pqtree_parent(X) != empty_pqtree) {
	       pqtree_partial_child_count(pqtree_parent(X))++;
	       pqtree_partial_succ(X) = pqtree_partial_child(pqtree_parent(X));
	       pqtree_partial_child(pqtree_parent(X)) = X;
	    } /* endif */
#           ifdef DDEBUG
	    message("\nmatch with template Q2..1 complete.");
#           endif
	    return true;
	 } else {
	    return false;
	 } /* endif */
      } /* endif */
   } else {
/*    Q_NODE does contain exactly one partial node */
      returncode = check_oneway_order(X);
      if (returncode != NOORDER) {
/*       insert all children of the partial child in root and *
 *       then delete the the partial child. */
	 discard_partial_child(X, pqtree_partial_child(X), returncode);
	 pqtree_label(X) = PARTIAL;
	 if (pqtree_parent(X) != empty_pqtree) {
	    pqtree_partial_child_count(pqtree_parent(X))++;
	    pqtree_partial_succ(X) = pqtree_partial_child(pqtree_parent(X));
	    pqtree_partial_child(pqtree_parent(X)) = X;
	 } /* endif */
#        ifdef DDEBUG
	 message("\nmatch with template Q2..2 complete.");
#        endif
         return true;
      } else {
         return false;
      } /* endif */
   } /* endif */

/* something went wrong */
/* return false; */

}


Global bool TEMPLATE_Q3(PQtree X)
{
   PQtree   partial_child;
   if (pqtree_type(X) != Q_NODE) {
      return false;
   } /* endif */
#  ifdef DEBUG
   fprintf(pqfile, "\nmatching X (%d) with template Q3",
           pqtree_entry(X));
#  endif

   if (pqtree_partial_child_count(X) > 2) {
      return false;
   } /* endif */

   if (check_twoway_order(X)) {
      while (pqtree_partial_child_count(X) > 0) {
	 partial_child = pqtree_partial_child(X);
	 discard_partial_child(X, partial_child, NOORDER);
      } /* endwhile */
      pqtree_label(X) = PARTIAL;
      if (pqtree_parent(X) != empty_pqtree) {
	 pqtree_partial_child_count(pqtree_parent(X))++;
	 pqtree_partial_succ(X) = pqtree_partial_child(pqtree_parent(X));
	 pqtree_partial_child(pqtree_parent(X)) = X;
      } /* endif */
#     ifdef DDEBUG
      message("\nmatch with template Q3 complete.");
#     endif
      return true;
   } else {
	 return false;
   } /* endif */


/* something went wrong */
/* return false; */

}



