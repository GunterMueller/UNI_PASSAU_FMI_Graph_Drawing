/****************************************************************************\
 *                                                                          *
 *  reduce.c                                                                *
 *  --------                                                                *
 *                                                                          *
 *  author:  a.j. winter (11027)  04/93.                                    *
 *                                                                          *
\****************************************************************************/


/* the algorithms for handling and reducing in this file                    *
 * are taken from the following papers:                                     */



/* Ozawa, Takahashi: A GRAPH-PLANARIZATION ALGORITHM AND ITS APPLICATION    *
 *                   TO RANDOM GRAPHS in:                                   *
 * Graph Theory and Algorithms: Lect. Notes in Comp. Sc. 108 (1981) 95-107  */

/* Jayakumar, Thulasiraman, Swamy: O(n^2) ALGORITHMS FOR GRAPH              *
 *                                 PLANARIZATION in:                        */

/* Booth, Lueker: TESTING FOR THE CONSECUTIVE ONES PROPERTY, INTERVAL       *
 *                GRAPHS AND GRAPH PLANARITY USING PQ-TREE ALGORITHMS in:   *
 * Journal of Comp. and Syst. Sciences, Vol.13, No.3, (1976), 335-379       */


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
#include "templates.h"

#if defined DEBUG
#include "sdebug.h"
#endif


#if defined SUN_VERSION
Global	Slist	Search_Leaves_in_PQtree(PQtree tree, int number, Snode node, int *length);
Local	void	get_a_and_h_values(PQtree X);
#else
Global	Slist	Search_Leaves_in_PQtree(PQtree, int, Snode, int *);
Local	void	get_a_and_h_values(PQtree);
#endif


#define MPG
/*	PLANARITY_TEST */
#define MPG_JAYAKUMAR
/*	MPG_OZAWA */


extern char buffer[];
#ifdef DEBUG
extern FILE *pqfile;
#endif



/****************************************************************************\
 *                                                                          *
 *  Global Slist Search_Leaves_in_PQtree(tree, number, node, length)        *
 *  ----------------------------------------------------------------        *
 *                                                                          *
 *  author:  a.j. winter (11027)  05/91.                                    *
 *                                                                          *
 ****************************************************************************
 *                                                                          *
 *  parameters:                                                             *
 *                                                                          *
 *  returns   : ---                                                         *
 *                                                                          *
 *  call from :                                                             *
 *                                                                          *
 *  uses      :                                                             *
 *                                                                          *
 *  task      :                                                             *
 *                                                                          *
\****************************************************************************/

Global Slist Search_Leaves_in_PQtree(PQtree tree, int number, Snode node, int *length)
{
   Slist    PQleaf_list;
   PQtree   child;

/* #  ifdef DEBUG
   fprintf(pqfile, "\nentering Search_Leaves...");
   fflush(pqfile);
   #  endif */

   PQleaf_list = empty_slist;

   if (pqtree_type(tree) != LEAF) {
      pqtree_l(tree) = 0; /* compute number of descendant leaves */
      for_all_children(tree, child) {
	 PQleaf_list = concat_slists(
			Search_Leaves_in_PQtree(child, number,
						node, length),
			PQleaf_list);
	 pqtree_l(tree) = pqtree_l(tree) + pqtree_l(child);
      } end_for_all_children(tree, child);
   } else {
      if (pqtree_entry(tree) == number) {  /* pqtree_graph_node == node */
         PQleaf_list = enqueue(PQleaf_list, tree);
         (*length)++;
      } /* endif */
      pqtree_l(tree) = 1;  /* basis of recursion for descendant leaves */
   } /* endif */

   pqtree_bubble(tree) = UNMARKED;
   pqtree_marker(tree) = UNMARKED;
/* as default PQ-Nodes are marked EMPTY, *
 * full nodes are labeled in REDUCE */
   pqtree_label (tree) = EMPTY;  
   pqtree_pertinent_child_count(tree) = 0;
   pqtree_pertinent_leaf_count(tree) = 0;
   pqtree_full_child_count(tree) = 0;
   pqtree_full_child(tree) = empty_pqtree;
   pqtree_full_succ(tree) = empty_pqtree;
   pqtree_partial_child_count(tree) = 0;
   pqtree_partial_child(tree) = empty_pqtree;
   pqtree_partial_succ(tree) = empty_pqtree;

   pqtree_mpg_type(tree) = NO_TYPE;
   pqtree_n(tree) = 0;
   pqtree_m(tree) = 0;
   pqtree_b(tree) = pqtree_l(tree);  
/* default: node and all its descendant leaves are  EMPTY *
*  -> cost for making node type B (FULL) is cost of making *
*  all (EMPTY) descendant leaves type B (FULL) */
   pqtree_w(tree) = 0;
   pqtree_h(tree) = 0;
   pqtree_a(tree) = 0;
   pqtree_h_child1(tree) = empty_pqtree;
   pqtree_h_child2(tree) = empty_pqtree;
   pqtree_a_child(tree)  = empty_pqtree;

/* #  ifdef DEBUG
   fprintf(pqfile, "\nleaving Search_Leaves...");
   fflush(pqfile);
   #  endif */

   return PQleaf_list;
}


/****************************************************************************\
 *                                                                          *
 *  Global PQtree BUBBLE_PLANAR(T, S, S_LENGTH)                             *
 *  -------------------------------------------                             *
 *                                                                          *
 *  author:  a.j. winter (11027)  05/91.                                    *
 *                                                                          *
 ****************************************************************************
 *                                                                          *
 *  parameters:                                                             *
 *                                                                          *
 *  returns   : ---                                                         *
 *                                                                          *
 *  call from :                                                             *
 *                                                                          *
 *  uses      :                                                             *
 *                                                                          *
 *  task      :                                                             *
 *                                                                          *
\****************************************************************************/

Global PQtree   BUBBLE_PLANAR(PQtree T, Slist S, int S_LENGTH)
{
/* Globale Variablen */

   int     BLOCK_COUNT;
   int     BLOCKED_NODES;
   short   OFF_THE_TOP;

   Slist   QUEUE;
   Slist   BLOCK_LIST;

   Slist    Selem;
   Slist    BS, US;
   Slist    LIST, LEFT_LIST, RIGHT_LIST;
   PQtree   X, Y, Z;
   PQtree   Sibling;
   int      QUEUE_LENGTH, LIST_LENGTH,
	    BS_LENGTH, US_LENGTH,
	    LEFT_COUNT, RIGHT_COUNT;

/* initialize QUEUE to be empty */
   QUEUE            = empty_slist;
/* QUEUE_LENGTH     = 0; */
   BLOCK_COUNT      = 0;
   BLOCKED_NODES    = 0;
   OFF_THE_TOP      = 0;

/* initialize BLOCK_LIST to be empty */
   BLOCK_LIST	    = empty_slist;

#  ifdef DEBUG
   fprintf(pqfile, "\nentering BUBBLE");
#  endif
/* for X in S do place X onto QUEUE od; */
   for_slist(S, Selem) {
      X = pqtree_in_slist(Selem);
      QUEUE = enqueue(QUEUE, X);
/*    QUEUE_LENGTH++; */
   } end_for_slist(S, Selem);
   QUEUE_LENGTH = S_LENGTH;
#  ifdef DEBUG
   fprintf(pqfile, "\ncreated QUEUE with %d elements",
		    QUEUE_LENGTH);
#  endif
/* alternativ: add_slists(QUEUE, S); */

/* while #QUEUE + BLOCKCOUNT + OFF_THE_TOP > 1 do */
/* alternativ: while #QUEUE > 0 AND BLOCK_COUNT + OFF_THE_TOP > 0 */
   while (QUEUE_LENGTH + BLOCK_COUNT + OFF_THE_TOP > 1) {
/*    if #QUEUE = 0 then */
      if (isempty(QUEUE)) { /* if (QUEUE_LENGTH == 0) */
/*       T := T(empty_set, empty_set); */
	 T = empty_pqtree; /* ??? */
/*       exit from do */
	 break;
/*       alternativ: return T; bzw. return empty_pqtree */
/*    fi */
      }

/*    remove X from the front of QUEUE */
      X = first_pqtree(QUEUE);
      if (pqtree_parent(X) != empty_pqtree) {
#        ifdef DEBUG
	 fprintf(pqfile, "\nexamining node %d (father %d)",
		 pqtree_entry(X),
		 pqtree_entry(pqtree_parent(X)));
	 sprintf(buffer, "\nexamining node %d (father %d) in BUBBLE",
		 pqtree_entry(X),
		 pqtree_entry(pqtree_parent(X)));
	 message(buffer);
#        endif
      } else {
#        ifdef DEBUG
	 fprintf(pqfile, "\nexamining node %d (ROOT)",
		 pqtree_entry(X));
	 sprintf(buffer, "\nexamining node %d (ROOT) in BUBBLE",
		 pqtree_entry(X));
	 message(buffer);
#        endif
      } /* endif */
      QUEUE = rest(QUEUE);  /* QUEUE = dequeue(QUEUE); */
      QUEUE_LENGTH--;

/*    MARK(X) := "blocked"; */
      pqtree_bubble(X) = BLOCKED;
#     ifdef DEBUG
      fprintf(pqfile, "\nset node %d to BLOCKED (%d)",
	      pqtree_entry(X), pqtree_marker(X));
#     endif

/*    BS := {Y in IMMEDIATE_SIBLINGS(X): MARK(Y) = "blocked"}; */
/*    US := {Y in IMMEDIATE_SIBLINGS(X): MARK(Y) = "unblocked"}; */
      BS               = empty_slist;
      US               = empty_slist;
      BS_LENGTH        = 0;
      US_LENGTH        = 0;
      if (pqtree_immed_siblings(X) > 0) {
         if (X != pqtree_leftmost_child(pqtree_parent(X))) {
	    if (pqtree_bubble(pqtree_left_sibling(X)) == BLOCKED) {
               BS = enqueue(BS, pqtree_left_sibling(X));
	       BS_LENGTH++;
	    } else if (pqtree_bubble(pqtree_left_sibling(X)) == UNBLOCKED) {
	       US = enqueue(US, pqtree_left_sibling(X));
	       US_LENGTH++;
	    } /* endif */
	 } /* endif */

	 if (X != pqtree_rightmost_child(pqtree_parent(X))) {
	    if (pqtree_bubble(pqtree_right_sibling(X)) == BLOCKED) {
               BS = enqueue(BS, pqtree_right_sibling(X));
	       BS_LENGTH++;
            } else if (pqtree_bubble(pqtree_right_sibling(X)) == UNBLOCKED) {
	       US = enqueue(US, pqtree_right_sibling(X));
	       US_LENGTH++;
	    } /* endif */
	 } /* endif */
      } /* endif */
#     ifdef DEBUG
      fprintf(pqfile,"\n#imm sibl %d   #BS %d   #US %d",
			pqtree_immed_siblings(X),
			BS_LENGTH, US_LENGTH);
#     endif

/*    if (#US > 0) then */
      if (US_LENGTH > 0) {
/*       choose any Y in US; */
	 Y = first_pqtree(US);
/*       US = rest(US) ??? */

/*       PARENT(X) := PARENT(Y); */
	 pqtree_parent(X) = pqtree_parent(Y);
/*       MARK(X) := "unblocked"; */
	 pqtree_bubble(X) = UNBLOCKED;
/*    else */
      } else {
/*       if #IMMEDIATE_SIBLINGS(X) < 2 then */
	 if (pqtree_immed_siblings(X) < 2) {
/*          MARK(X) := "unblocked"; */
	    pqtree_bubble(X) = UNBLOCKED;
#           ifdef DEBUG
	    fprintf(pqfile, "\nset node %d to UNBLOCKED (%d)",
                pqtree_entry(X), pqtree_bubble(X));
#           endif
/*       fi */
	 } /* endif */
/*    fi */
      } /* endif */

/*    if (MARK(X) = "unblocked") then */
      if (pqtree_bubble(X) == UNBLOCKED) {
/*       Y := PARENT(X); */
         Y = pqtree_parent(X);
	 LIST = empty_slist;
         LIST_LENGTH = 0;

/*       if #BS > 0 */
	 if (BS_LENGTH > 0) {
/*          LIST := the maximal consecutive set of blocked siblings *
 *                  adjacent to X (either left or right); */

/*          count all blocked siblings "to the left" of X; */
	    LEFT_COUNT = 0;
	    LEFT_LIST = empty_slist;
            Sibling = X;
	    while (Sibling != pqtree_leftmost_child(pqtree_parent(X))) {
	       Sibling = pqtree_left_sibling(Sibling);
	       if (pqtree_bubble(Sibling) == BLOCKED) {
		  LEFT_LIST = enqueue(LEFT_LIST, Sibling);
		  LEFT_COUNT++;
               } else {
		  break;
	       } /* endif */
	    } /* endwhile */

/*          get all blocked siblings "to the right" of X; */
            RIGHT_COUNT = 0;
	    RIGHT_LIST = empty_slist;
            Sibling = X;
	    while (Sibling != pqtree_rightmost_child(pqtree_parent(X))) {
	       Sibling = pqtree_right_sibling(Sibling);
	       if (pqtree_bubble(Sibling) == BLOCKED) {
		  RIGHT_LIST = enqueue(RIGHT_LIST, Sibling);
		  RIGHT_COUNT++;
	       } else {
		  break;
	       } /* endif */
	    } /* endwhile */

	    if (LEFT_COUNT > RIGHT_COUNT) {
	       free_slist(RIGHT_LIST);
	       LIST = LEFT_LIST;
	       LIST_LENGTH = LEFT_COUNT;
	    } else {
	       free_slist(LEFT_LIST);
	       LIST = RIGHT_LIST;
               LIST_LENGTH = RIGHT_COUNT;
	    } /* endif */

/*          for Z in LIST do */
            for_slist(LIST, Selem) {
	       Z = pqtree_in_slist(Selem);
/*             MARK(Z) := "unblocked"; */
	       pqtree_bubble(Z) = UNBLOCKED;
#              ifdef DEBUG
	       fprintf(pqfile, "\nset node %d to UNBLOCKED (%d)",
		   pqtree_entry(Z), pqtree_bubble(Z));
#              endif
/*             PARENT(Z) := Y; */
	       pqtree_parent(Z) = Y;
/*             PERTINENT_CHILD_COUNT(Y) :=  PERTINENT_CHILD_COUNT(Y) + 1; */
	       pqtree_pertinent_child_count(Y)++;
	       pqtree_n(Y)++;
	       /* remove Z from BLOCK_LIST */
	       BLOCK_LIST = subtract_immediately_from_slist(
					BLOCK_LIST,
					pqtree_block_node(Z));
/*          od */
            } end_for_slist(LIST, Selem);
/*       fi */
         } /* endif */

/*       if Y = nil then */
	 if (Y == empty_pqtree) {
/*          OFF_THE_TOP := 1; */
	    OFF_THE_TOP = 1;
/*       else */
	 } else {
/*          PERTINENT_CHILD_COUNT(Y) := PERTINENT_CHILD_COUNT(Y) + 1; */
	    pqtree_pertinent_child_count(Y)++;
	    pqtree_n(Y)++;

/*          if MARK(Y) = "unmarked" then */
	    if (pqtree_bubble(Y) == UNMARKED) {
/*             place Y onto QUEUE; */
	       QUEUE = enqueue(QUEUE, Y);
               QUEUE_LENGTH++;
/*             MARK(Y) := "queued"; */
	       pqtree_bubble(Y) = QUEUED;
#              ifdef DEBUG
	       fprintf(pqfile, "\nset node %d to QUEUED (%d)",
		   pqtree_entry(Y), pqtree_bubble(Y));
#              endif
/*          fi */
            } /* endif */
/*       fi */
	 } /* endif */
/*       BLOCK_COUNT := BLOCK_COUNT - #BS; */
	 BLOCK_COUNT = BLOCK_COUNT - BS_LENGTH;
#        ifdef DEBUG
	 fprintf(pqfile, "\nchanging  BLOCK_COUNT to %d",
		BLOCK_COUNT);
#        endif
/*       BLOCKED_NODES := BLOCKED_NODES - #LIST; */
         BLOCKED_NODES = BLOCKED_NODES - LIST_LENGTH;
#        ifdef DEBUG
	 fprintf(pqfile, "\nchanging  BLOCKED_NODES to %d",
		BLOCKED_NODES);
#        endif
/*    else */
      } else {
/*       BLOCK_COUNT := BLOCK_COUNT + 1 - #BS; */
         BLOCK_COUNT = BLOCK_COUNT + 1 - BS_LENGTH;
#        ifdef DEBUG
	 fprintf(pqfile, "\nchanging  BLOCK_COUNT to %d",
		BLOCK_COUNT);
#        endif
/*       BLOCKED_NODES := BLOCKED_NODES + 1; */
         BLOCKED_NODES = BLOCKED_NODES + 1;
#        ifdef DEBUG
	 fprintf(pqfile, "\nchanging  BLOCKED_NODES to %d",
		BLOCKED_NODES);
#        endif
	 /* add X to BLOCK_LIST */
	BLOCK_LIST = enqueue(BLOCK_LIST, X);
	pqtree_block_node(X) = BLOCK_LIST->pre;
/*    fi */
      } /* endif */
#     ifdef DEBUG
      fflush(pqfile);
#     endif
/* od */
   } /* endwhile */

   if (BLOCK_COUNT == 1) {
#     ifdef DEBUG
      fprintf(pqfile, "\nfound BLOCK_LIST with %d elements: ", BLOCKED_NODES);
      for_slist(BLOCK_LIST, Selem) {
	 fprintf(pqfile, " blocknode %d  ",pqtree_in_slist(Selem)->entry);
      } end_for_slist(BLOCK_LIST, Selem);
#     endif
      if (BLOCKED_NODES > 1) {
/*       create dummy parent node for blocked nodes */
/*       note that the only template for which replacement will be performed *
 *       is template Q3 */
/*	 Z = Init_PQnode(Q_NODE); *
 *	 pqtree_entry(Z) = -9; */
/*       children stay children of their real (unknown since blocked) parent, *
 *	 i.e. they keep their real siblings as they are, for this reason the  *
 *	 siblings need to be buffered, when enqueuing them to their           *
 *	 dummy-parent and afterwards */
/*	 pqtree_child_count(Z) = BLOCKED_NODES; *
 *	 pqtree_pertinent_child_count(Z) = BLOCKED_NODES; */
/*       find  the leftmost blocked node */
/*	 for_slist(BLOCK_LIST, Selem) {
	    Y = pqtree_in_slist(Selem);
	    if (!(pqtree_bubble(pqtree_left_sibling(Y)) == BLOCKED)) {
	       break;
	    } * endif *
	 } end_for_slist(BLOCK_LIST, Selem);
	 pqtree_leftmost_child(Z) = Y;
	 do {
	    pqtree_parent(Y) = Z;
	    Y = pqtree_right_sibling(Y);
	 } while(pqtree_bubble(Y) == BLOCKED);
	 pqtree_rightmost_child(Z) = pqtree_left_sibling(Y); */
	 Y = pqtree_in_slist(BLOCK_LIST);
/*       this is certainly the point of the algorithm:
	 the parent of BLOCKED-Nodes is not defined */
/*       koennen nur blocked-Nodes pertinent_children sein, oder kann *
 *       der vater ausserdem noch andere kinder haben ? *
 *       entsprechend ..... = BLOCKED_NODES (oder ... += BLOCKED_NODES) */
	 pqtree_pertinent_child_count(pqtree_parent(Y)) += BLOCKED_NODES;
	 pqtree_n(pqtree_parent(Y)) += BLOCKED_NODES;
      } /* endif */
/*    destroy BLOCK_LIST */

   } else if (BLOCK_COUNT == 0) {
#     ifdef DEBUG
      fprintf(pqfile, "\nfound no BLOCK_LIST");
#     endif
   } else if (BLOCK_COUNT >= 2) {
#     ifdef DEBUG
      fprintf(pqfile, "\ntree not reducible due to BLOCK_COUNT");
#     endif
   } /* endif */
   /* other cases need not be handled */
#  ifdef DEBUG
   fprintf(pqfile, "\n");
   fflush(pqfile);
#  endif

   return T;
}


/****************************************************************************\
 *                                                                          *
 *  Global PQtree BUBBLE_MPG(T, S, S_LENGTH)                                *
 *  ----------------------------------------                                *
 *                                                                          *
 *  author:  a.j. winter (11027)  05/91.                                    *
 *                                                                          *
 ****************************************************************************
 *                                                                          *
 *  parameters:                                                             *
 *                                                                          *
 *  returns   : ---                                                         *
 *                                                                          *
 *  call from :                                                             *
 *                                                                          *
 *  uses      :                                                             *
 *                                                                          *
 *  task      :                                                             *
 *                                                                          *
\****************************************************************************/

Global PQtree   BUBBLE_MPG(PQtree T, Slist S, int S_LENGTH)
{
/* Globale Variablen */

   Slist   QUEUE;

   Slist    Selem;
   PQtree   X, Y, Z;
   Slist    US;
   int      QUEUE_LENGTH, US_LENGTH;
   int	    OFF_THE_TOP;


/* initialize QUEUE to be empty */
   QUEUE            = empty_slist;
/* QUEUE_LENGTH     = 0; */
   OFF_THE_TOP	    = 0;

#  ifdef DEBUG
   fprintf(pqfile, "\nentering BUBBLE");
#  endif
/* for X in S do place X onto QUEUE od; */
   for_slist(S, Selem) {
      X = pqtree_in_slist(Selem);
      QUEUE = enqueue(QUEUE, X);
/*    QUEUE_LENGTH++; */
   } end_for_slist(S, Selem);
   QUEUE_LENGTH = S_LENGTH;
#  ifdef DEBUG
   fprintf(pqfile, "\ncreated QUEUE with %d elements",
		    QUEUE_LENGTH);
#  endif
/* alternativ: add_slists(QUEUE, S); */

   while (QUEUE_LENGTH + OFF_THE_TOP > 1) { /* while (QUEUE_LENGTH == 0) */
/*    remove X from the front of QUEUE */
      X = first_pqtree(QUEUE);
#     ifdef DEBUG
      if (pqtree_parent(X) != empty_pqtree) {
	 fprintf(pqfile, "\nexamining node %d (father %d)",
		 pqtree_entry(X),
		 pqtree_entry(pqtree_parent(X)));
/*	 sprintf(buffer, "\nexamining node %d (father %d) in BUBBLE",
		 pqtree_entry(X),
		 pqtree_entry(pqtree_parent(X)));
	 message(buffer);  */

      } else {
	 fprintf(pqfile, "\nexamining node %d (ROOT)",
		 pqtree_entry(X));
/*	 sprintf(buffer, "\nexamining node %d (ROOT) in BUBBLE",
		 pqtree_entry(X));
	 message(buffer); */
      } /* endif */
#     endif
      QUEUE = rest(QUEUE);  /* QUEUE = dequeue(QUEUE); */
      QUEUE_LENGTH--;

/*    MARK(X) := "blocked"; */
      pqtree_bubble(X) = BLOCKED;
#     ifdef DEBUG
      fprintf(pqfile, "\nset node %d to BLOCKED (%d)",
	      pqtree_entry(X), pqtree_bubble(X));
#     endif

/*    US := {Y in IMMEDIATE_SIBLINGS(X): MARK(Y) = "unblocked"}; */
      US               = empty_slist;
      US_LENGTH        = 0;
      if (pqtree_immed_siblings(X) > 0) {
	 if (X != pqtree_leftmost_child(pqtree_parent(X))) {
	    if (pqtree_bubble(pqtree_left_sibling(X)) == UNBLOCKED) {
	       US = enqueue(US, pqtree_left_sibling(X));
	       US_LENGTH++;
	    } /* endif */
	 } /* endif */

	 if (X != pqtree_rightmost_child(pqtree_parent(X))) {
	    if (pqtree_bubble(pqtree_right_sibling(X)) == UNBLOCKED) {
	       US = enqueue(US, pqtree_right_sibling(X));
	       US_LENGTH++;
	    } /* endif */
	 } /* endif */
      } /* endif */
#     ifdef DEBUG
      fprintf(pqfile,"\n#imm sibl %d   #US %d",
	      pqtree_immed_siblings(X), US_LENGTH);
#     endif

/*    if (#US > 0) then */
      if (US_LENGTH > 0) {
/*       choose any Y in US; */
	 Y = first_pqtree(US);
/*       US = rest(US) ??? */
	 while (US_LENGTH > 0) {
	    US = rest(US);
	    US_LENGTH--;
	 } /* endwhile */

/*       PARENT(X) := PARENT(Y); */
	 pqtree_parent(X) = pqtree_parent(Y);
/*       MARK(X) := "unblocked"; */
	 pqtree_bubble(X) = UNBLOCKED;
/*    else */
      } else {
/*       if #IMMEDIATE_SIBLINGS(X) < 2 then */
	 if (pqtree_immed_siblings(X) < 2) {
/*          MARK(X) := "unblocked"; */
	    pqtree_bubble(X) = UNBLOCKED;
#           ifdef DEBUG
	    fprintf(pqfile, "\nset node %d to UNBLOCKED (%d)",
		pqtree_entry(X), pqtree_bubble(X));
#           endif
/*       fi */
	 } /* endif */
/*    fi */
      } /* endif */

      if (pqtree_bubble(X) == BLOCKED) {
	 Z = pqtree_left_sibling(X);
	 while (pqtree_parent(Z) == empty_pqtree) {
	    Z = pqtree_left_sibling(Z);
	 } /* endwhile */
	 pqtree_bubble(X) = UNBLOCKED;
	 pqtree_parent(X) = pqtree_parent(Z);
      } /* endwhile */

/*       Y := PARENT(X); */
	 Y = pqtree_parent(X);

/*       if Y = nil then */
	 if (Y == empty_pqtree) {
	    OFF_THE_TOP = 1;
	 } else {
/*          PERTINENT_CHILD_COUNT(Y) := PERTINENT_CHILD_COUNT(Y) + 1; */
	    pqtree_pertinent_child_count(Y)++;
	    pqtree_n(Y)++;

/*          if MARK(Y) = "unmarked" then */
	    if (pqtree_bubble(Y) == UNMARKED) {
/*             place Y onto QUEUE; */
	       QUEUE = enqueue(QUEUE, Y);
	       QUEUE_LENGTH++;
/*             MARK(Y) := "queued"; */
	       pqtree_bubble(Y) = QUEUED;
#              ifdef DEBUG
	       fprintf(pqfile, "\nset node %d to QUEUED (%d)",
		   pqtree_entry(Y), pqtree_bubble(Y));
#              endif
/*          fi */
	    } /* endif */
/*       fi */
	 } /* endif */
#     ifdef DEBUG
      fflush(pqfile);
#     endif
/* od */
   } /* endwhile */

   while (!isempty(QUEUE)) {
      QUEUE = rest(QUEUE);
   } /* endwhile */
#  ifdef DEBUG
   fprintf(pqfile, "\n");
   fflush(pqfile);
#  endif

   return T;
}


/****************************************************************************\
 *                                                                          *
 *  Global PQtree REDUCE(T, S, S_LENGTH)                                    *
 *  ------------------------------------                                    *
 *                                                                          *
 *  author:  a.j. winter (11027)  05/91.                                    *
 *                                                                          *
 ****************************************************************************
 *                                                                          *
 *  parameters:                                                             *
 *                                                                          *
 *  returns   : ---                                                         *
 *                                                                          *
 *  call from :                                                             *
 *                                                                          *
 *  uses      :                                                             *
 *                                                                          *
 *  task      :                                                             *
 *                                                                          *
\****************************************************************************/

Global PQtree   REDUCE(PQtree T, Slist S, int S_LENGTH)
{
/* Globale Variablen */

   Slist    QUEUE;
   int      QUEUE_LENGTH;
   Slist    Selem;
   PQtree   X, Y;

/* initialize QUEUE to empty */
   QUEUE 	= empty_slist;
/* QUEUE_LENGTH = 0;
   S_LENGTH     = 0; */

#  ifdef DEBUG
   fprintf(pqfile, "\nentering REDUCE");
   fflush(pqfile);
#  endif

/* for each leaf X in S do */
   for_slist(S, Selem) {
      X = pqtree_in_slist(Selem);
/*    place X on QUEUE */
      QUEUE = enqueue(QUEUE, X);
/*    QUEUE_LENGTH++;
      S_LENGTH++; */
/*    PERTINENT_LEAF_COUNT(X) := 1; */
      pqtree_pertinent_leaf_count(X) = 1;
/* od */
   } end_for_slist(S, Selem);
   QUEUE_LENGTH = S_LENGTH;
#  ifdef DEBUG
   fprintf(pqfile, "\ncreated QUEUE with %d elements",
                    QUEUE_LENGTH);
#  endif

/* while #QUEUE > 0 do */
   while (!isempty(QUEUE)) {  /* QUEUE_LENGTH > 0 */
/*    remove X from the front of QUEUE; */
      X = first_pqtree(QUEUE);
#     ifdef DEBUG
      fprintf(pqfile, "\n\nexamining node %d",pqtree_entry(X));
      print_PQtree(pqfile, X);
#     endif
      QUEUE = rest(QUEUE);  /* QUEUE = dequeue(QUEUE); */
      QUEUE_LENGTH--;

/*    if PERTINENT_LEAF_COUNT(X) < #S then */
      if (pqtree_pertinent_leaf_count(X) < S_LENGTH) {
/*       X is not ROOT(T,S) */
#        ifdef DEBUG
	 fprintf(pqfile, "\n\nX is not ROOT(T,S)");
#        endif

/*       Y := PARENT(X); */
	 Y = pqtree_parent(X);
/*       PERTINENT_LEAF_COUNT(Y) := PERTINENT_LEAF_COUNT(Y) + *
 *                                  PERTINENT_LEAF_COUNT(X);  */
	 pqtree_pertinent_leaf_count(Y) = pqtree_pertinent_leaf_count(Y) +
					  pqtree_pertinent_leaf_count(X);
/*       PERTINENT_CHILD_COUNT(Y) := PERTINENT_CHILD_COUNT(Y) - 1; */
	 pqtree_pertinent_child_count(Y)--;

/*       if PERTINENT_CHILD_COUNT(Y) = 0 then */
         if (pqtree_pertinent_child_count(Y) == 0) {
/*          place Y onto QUEUE */
            QUEUE = enqueue(QUEUE, Y);
	    QUEUE_LENGTH++;
/*       fi */
         } /* endif */

/*       if not TEMPLATE_L1(X) then */
         if (!TEMPLATE_L1(X)) {
#           ifdef DEBUG
            fprintf(pqfile, "\nno match of X (%d) with template L1",
		    pqtree_entry(X));
#           endif
/*       if not TEMPLATE_P1(X) then */
            if (!TEMPLATE_P1(X)) {
#              ifdef DEBUG
	       fprintf(pqfile, "\nno match of X (%d) with template P1",
                       pqtree_entry(X));
#              endif
/*       if not TEMPLATE_P3(X) then */
               if (!TEMPLATE_P3(X)) {
#                 ifdef DEBUG
                  fprintf(pqfile, "\nno match of X (%d) with template P3",
			  pqtree_entry(X));
#                 endif
/*       if not TEMPLATE_P5(X) then */
                  if (!TEMPLATE_P5(X)) {
#                    ifdef DEBUG
                     fprintf(pqfile, 
                             "\nno match of X (%d) with template P5",
                             pqtree_entry(X));
#                    endif
/*       if not TEMPLATE_Q1(X) then */
                     if (!TEMPLATE_Q1(X)) {
#                       ifdef DEBUG
			fprintf(pqfile,
                                "\nno match of X (%d) with template P5",
                                pqtree_entry(X));
#                       endif
/*       if not TEMPLATE_Q2(X) then */
                        if (!TEMPLATE_Q2(X)) {
#                          ifdef DEBUG
                           fprintf(pqfile,
				"\nno match of X (%d) with template P5",
                                pqtree_entry(X));
#                          endif
/*          T := T(empty_set, empty_set); */
			T = empty_pqtree;
/*          exit from do; */
                        break;
/*       fi */
			}
                     }
                  }
               }
	    }
         }
/*    else */
      } else {
/*       X is ROOT(T,S) */
#        ifdef DEBUG
         fprintf(pqfile, "\n\nX is ROOT(T,S)");
#        endif
/*       if not TEMPLATE_L1(X) then */
         if (!TEMPLATE_L1(X)) {
#         ifdef DEBUG
          fprintf(pqfile,
		  "\nno match of X (%d) with template L1",
                  pqtree_entry(X));
#          endif
/*       if not TEMPLATE_P0(X) then */
	  if (!TEMPLATE_P0(X)) {
#           ifdef DEBUG
           fprintf(pqfile,
                   "\nno match of X (%d) with template P0",
		   pqtree_entry(X));
#           endif
/*       if not TEMPLATE_P1(X) then */
           if (!TEMPLATE_P1(X)) {
#           ifdef DEBUG
            fprintf(pqfile,
                    "\nno match of X (%d) with template P1",
		    pqtree_entry(X));
#           endif
/*       if not TEMPLATE_P2(X) then */
            if (!TEMPLATE_P2(X)) {
#           ifdef DEBUG
	     fprintf(pqfile,
                     "\nno match of X (%d) with template P2",
                     pqtree_entry(X));
#           endif
/*       if not TEMPLATE_P4(X) then */
             if (!TEMPLATE_P4(X)) {
#           ifdef DEBUG
              fprintf(pqfile,
		      "\nno match of X (%d) with template P4",
                      pqtree_entry(X));
#           endif
/*       if not TEMPLATE_P6(X) then */
	      if (!TEMPLATE_P6(X)) {
#           ifdef DEBUG
               fprintf(pqfile,
                       "\nno match of X (%d) with template P6",
		       pqtree_entry(X));
#           endif
/*       if not TEMPLATE_Q1(X) then */
               if (!TEMPLATE_Q1(X)) {
#           ifdef DEBUG
                fprintf(pqfile,
                "\nno match of X (%d) with template Q1",
                pqtree_entry(X));
#           endif
/*       if not TEMPLATE_Q2(X) then */
                if (!TEMPLATE_Q2(X)) {
#           ifdef DEBUG
		 fprintf(pqfile,
                         "\nno match of X (%d) with template Q2",
                         pqtree_entry(X));
#           endif
/*       if not TEMPLATE_Q3(X) then */
                 if (!TEMPLATE_Q3(X)) {
#           ifdef DEBUG
                    fprintf(pqfile,
			    "\nno match of X (%d) with template Q3",
                            pqtree_entry(X));
#           endif
/*       T := T(empty_set, empty_set); */
		    T = empty_pqtree;
/*    fi */
                 }
                }
	       }
              }
             }
            }
	   }
          }
         }
/*       exit from do; */
	 break;
      } /* endif */
#     ifdef DEBUG
      fprintf(pqfile, "\n\nmatched node X (%d) after replacement",
	      pqtree_entry(X));
      print_PQtree(pqfile, X);
#     endif
/* od */
   } /* endwhile */

#  ifdef DEBUG
   fprintf(pqfile, "\n\nmatched pertinent root (%d) after replacement",
	   pqtree_entry(X));
   print_PQtree(pqfile, X);
#  endif
   return T;
}



/****************************************************************************\
 *                                                                          *
 *  Global bool  COMPUTE_MAXPLANAR_VALUES(S, S_LENGTH, DEL_STACK)           *
 *  -------------------------------------------------------------           *
 *                                                                          *
 *  author:  a.j. winter (11027)  05/91.                                    *
 *                                                                          *
 ****************************************************************************
 *                                                                          *
 *  parameters:                                                             *
 *                                                                          *
 *  returns   : ---                                                         *
 *                                                                          *
 *  call from :                                                             *
 *                                                                          *
 *  uses      :                                                             *
 *                                                                          *
 *  task      :                                                             *
 *                                                                          *
\****************************************************************************/

Global bool  COMPUTE_MAXPLANAR_VALUES(Slist S, int S_LENGTH, Slist *DEL_STACK)
{
   Slist	QUEUE;
   Slist	STACK;
   int		QUEUE_LENGTH;
   Slist	Selem;
   PQtree	X, Y;
   PQtree	S_Root;
   int		S_Type;


#  ifdef DEBUG
   fprintf(pqfile, "\nentering COMPUTE_VALUES");
   fflush(pqfile);
#  endif


/* initialize QUEUE to empty */
   QUEUE 	= empty_slist;
/* QUEUE_LENGTH = 0; */
   STACK        = empty_stack;
/* S_LENGTH	= 0; */

/* for each leaf X in S do */
   for_slist(S, Selem) {
      X = pqtree_in_slist(Selem);
/*    place X on QUEUE */      QUEUE = enqueue(QUEUE, X);
      STACK = push(STACK, X);
/*    QUEUE_LENGTH++;
      S_LENGTH++; */
      pqtree_m(X) = 1;
      pqtree_w(X) = 1;
/* od */
   } end_for_slist(S, Selem);
   QUEUE_LENGTH = S_LENGTH;
#  ifdef DEBUG
   fprintf(pqfile, "\ncreated QUEUE with %d elements",
                    QUEUE_LENGTH);
   fflush(pqfile);
#  endif

/* while #QUEUE > 0 do */
   while (!isempty(QUEUE)) {
/*    remove X from the front of QUEUE; */
      X = first_pqtree(QUEUE);
#     ifdef DEBUG
      fprintf(pqfile, "\n\nexamining node %d",pqtree_entry(X));
      fflush(pqfile);
#     endif
      QUEUE = rest(QUEUE);  /* QUEUE = dequeue(QUEUE); */
      QUEUE_LENGTH--;

/*    Y := PARENT(X); */
      Y = pqtree_parent(X);

#     ifdef DEBUG
      fprintf(pqfile,
	      "\nset values of X %2d (father %2d) to d %d  l %d  n %d  m %d",
	      pqtree_entry(X),
	      ((Y != empty_pqtree) ? pqtree_entry(Y) : 0),
	      pqtree_d(X), pqtree_l(X), pqtree_n(X), pqtree_m(X));
      fflush(pqfile);
#     endif

      pqtree_b(X) = pqtree_l(X) - pqtree_w(X);
      if (pqtree_b(X) == 0) {
/*       X is FULL */
/*       darf hier schon das eigentlich fuer den REDUCE-Step vorgesehene *
 *       feld label belegt werden ? */
/*       alternativ: benutze eigenes feld (muss noch geschaffen werden) ??? */
	 pqtree_label(X) = FULL;
         pqtree_h(X) = 0;
	 pqtree_h_child1(X) = pqtree_leftmost_child(X);
	 pqtree_marker(X) = P_R;
	 pqtree_a(X) = 0;
	 pqtree_h_child2(X) = pqtree_rightmost_child(X);
      } else if (pqtree_b(X) == pqtree_l(X)) {
/*       X is EMPTY */
/*       kann an sich nicht eintreten, da nur pertinent leaves und deren *
 *       vorgaenger im PQ-Tree in die QUEUE gestellt werden */
         pqtree_label(X) = EMPTY;
      } else {
/*       X is PARTIAL */
	 pqtree_label(X) = PARTIAL;

	 get_a_and_h_values(X);  /* version by Jayakumar et al  */
      } /* endif */

#     ifdef DEBUG
      fprintf(pqfile,
	      "\n                                  b %d  w %d  h %d  a %d",
	      pqtree_b(X), pqtree_w(X), pqtree_h(X), pqtree_a(X));
      fflush(pqfile);
#     endif

#     ifdef DEBUG
      if (pqtree_h_child1(X)) {
      fprintf(pqfile,
	      "\n   set h_child1 to  %d", pqtree_entry(pqtree_h_child1(X)));
      }                              
      if (pqtree_h_child2(X)) {
      fprintf(pqfile,
	      "\n   set h_child2 to  %d", pqtree_entry(pqtree_h_child2(X)));
      }                       fflush(pqfile);
      if (pqtree_a_child(X)) {
      fprintf(pqfile,
	      "\n   set a_child  to  %d", pqtree_entry(pqtree_a_child(X)));
      }                 
#     endif

/*    if PERTINENT_LEAF_COUNT(X) < #S then */
      if (pqtree_m(X) < S_LENGTH) {
/*       X is not ROOT(T,S) */
#        ifdef DEBUG
	 fprintf(pqfile, "\nX is not ROOT(T,S)\n");
         fflush(pqfile);
#        endif

	 pqtree_w(Y) = pqtree_w(Y) + pqtree_w(X);
         pqtree_m(Y) = pqtree_m(Y) + pqtree_m(X);
	 pqtree_n(Y)--;

/*       if n(Y) = 0 then */
	 if (pqtree_n(Y) == 0) {
/*          place Y onto QUEUE */
            QUEUE = enqueue(QUEUE, Y);
	    QUEUE_LENGTH++;
	    STACK = push(STACK, Y);
/*       fi */
	 } /* endif */
/*    else */
      } else {
#        ifdef DEBUG
	 fprintf(pqfile, "\nX is ROOT(T,S)\n");
	 fflush(pqfile);
#        endif
/*       an dieser Stelle sollte QUEUE empty sein und damit kann auf "break" *
 *       verzichtet werden, da die schleife nun verlassen wird */
	 S_Root = X;

/*       liegt Planaritaet vor, also ist der S_Root vom typ B, H oder A *
 *       wird das ergebnis zurueckgegeben, und der Baum reduziert, sonst *
 *       wird bis zu den blaettern, anhand des stacks durchgereicht, welche *
 *       Blaetter geloescht werden muessen */
	 if (pqtree_b(S_Root) == 0) {
            S_Type = B_TYPE;
	 } else if (pqtree_h(S_Root) == 0) {
            S_Type = H_TYPE;
         } else if (pqtree_a(S_Root) == 0) {
	    S_Type = A_TYPE;
	 } else if (pqtree_w(S_Root) == 0) {
            S_Type = W_TYPE;
         } else {
            S_Type = NO_TYPE;
	 } /* endif */
/*    fi */
      } /* endif */
/* od */
   } /* endwhile */

   if ((S_Type == B_TYPE) || (S_Type == H_TYPE) || (S_Type == A_TYPE)) {
/*    free STACK */
      while (!isempty(STACK)) {
	 STACK = pop(STACK);
      } /* endwhile */
      *DEL_STACK = STACK;
      return true;
   } /* endif */

   if (S_Type == W_TYPE) {
/*    strange, this case should never occur */
/*    free STACK */
      while (!isempty(STACK)) {
	 STACK = pop(STACK);
      } /* endwhile */
      *DEL_STACK = STACK;
      return false;
   } /* endif */

   *DEL_STACK = STACK;
   return false;
}


/****************************************************************************\
 *                                                                          *
 *  Global bool  COMPUTE_MAXPLANAR_VALUES2(S, S_LENGTH, DEL_STACK)           *
 *  -------------------------------------------------------------           *
 *                                                                          *
 *  author:  a.j. winter (11027)  05/93.                                    *
 *                                                                          *
 ****************************************************************************
 *                                                                          *
 *  parameters:                                                             *
 *                                                                          *
 *  returns   : ---                                                         *
 *                                                                          *
 *  call from :                                                             *
 *                                                                          *
 *  uses      :                                                             *
 *                                                                          *
 *  task      :                                                             *
 *                                                                          *
\****************************************************************************/

Global bool  COMPUTE_MAXPLANAR_VALUES2(Slist S, int S_LENGTH, Slist *DEL_STACK)
{
   Slist	QUEUE;
   Slist	STACK;
   int		QUEUE_LENGTH;
   Slist	Selem;
   PQtree	X, Y;
   PQtree	S_Root;
   int		S_Type;


#  ifdef DEBUG
   fprintf(pqfile, "\nentering COMPUTE_VALUES2");
   fflush(pqfile);
#  endif


/* initialize QUEUE to empty */
   QUEUE 	= empty_slist;
/* QUEUE_LENGTH = 0; */
   STACK        = empty_stack;
/* S_LENGTH	= 0; */

/* for each leaf X in S do */
   for_slist(S, Selem) {
      X = pqtree_in_slist(Selem);
/*    place X on QUEUE */      QUEUE = enqueue(QUEUE, X);
      STACK = push(STACK, X);
/*    QUEUE_LENGTH++;
      S_LENGTH++; */
      pqtree_m(X) = 1;  /* pertinent leaf count */
      pqtree_w(X) = 1;  /* #nodes to be changed to get W_TYPE */
      if (!edge_in_mpg(pqtree_corresponding_edge(X))) {
         pqtree_marker(X) = NEW_PERTINENT;
         pqtree_mpg_info(X) = LEAF_NOT_IN_GRAPH;
      } else {
         pqtree_marker(X) = PREFERRED;
         pqtree_mpg_info(X) = LEAF_IN_GRAPH;
      } /* endif */
/* od */
   } end_for_slist(S, Selem);
   QUEUE_LENGTH = S_LENGTH;
#  ifdef DEBUG
   fprintf(pqfile, "\ncreated QUEUE with %d elements",
                    QUEUE_LENGTH);
   fflush(pqfile);
#  endif

/* while #QUEUE > 0 do */
   while (!isempty(QUEUE)) {
/*    remove X from the front of QUEUE; */
      X = first_pqtree(QUEUE);
#     ifdef DEBUG
      fprintf(pqfile, "\n\nexamining node %d",pqtree_entry(X));
      fflush(pqfile);
#     endif
      QUEUE = rest(QUEUE);  /* QUEUE = dequeue(QUEUE); */
      QUEUE_LENGTH--;

/*    Y := PARENT(X); */
      Y = pqtree_parent(X);

#     ifdef DEBUG
      fprintf(pqfile,
	      "\nset values of X %2d (father %2d) to d %d  l %d  n %d  m %d",
	      pqtree_entry(X),
	      ((Y != empty_pqtree) ? pqtree_entry(Y) : 0),
	      pqtree_d(X), pqtree_l(X), pqtree_n(X), pqtree_m(X));
      fflush(pqfile);
#     endif

      pqtree_b(X) = pqtree_l(X) - pqtree_w(X);
      if (pqtree_b(X) == 0) {
/*       X is FULL */
/*       darf hier schon das eigentlich fuer den REDUCE-Step vorgesehene *
 *       feld label belegt werden ? */
/*       alternativ: benutze eigenes feld */
	 pqtree_label(X) = FULL;
         pqtree_h(X) = 0;
	 pqtree_a(X) = 0;
      } else if (pqtree_b(X) == pqtree_l(X)) {
/*       X is EMPTY */
/*       kann an sich nicht eintreten, da nur pertinent leaves und deren *
 *       vorgaenger im PQ-Tree in die QUEUE gestellt werden */
         pqtree_label(X) = EMPTY;
      } else {
/*       X is PARTIAL */
	 pqtree_label(X) = PARTIAL;

	 get_a_and_h_values(X);  /* version by Jayakumar et al  */
      } /* endif */

#     ifdef DEBUG
      fprintf(pqfile,
	      "\n                                  b %d  w %d  h %d  a %d",
	      pqtree_b(X), pqtree_w(X), pqtree_h(X), pqtree_a(X));
      fflush(pqfile);
#     endif

/*    if PERTINENT_LEAF_COUNT(X) < #S then */
      if (pqtree_m(X) < S_LENGTH) {
/*       X is not ROOT(T,S) */
#        ifdef DEBUG
	 fprintf(pqfile, "\nX is not ROOT(T,S)\n");
         fflush(pqfile);
#        endif

	 pqtree_w(Y) = pqtree_w(Y) + pqtree_w(X);
         pqtree_m(Y) = pqtree_m(Y) + pqtree_m(X);
	 pqtree_n(Y)--;

         if (pqtree_marker(X) == PREFERRED) {
            pqtree_marker(Y) = PREFERRED;
         } /* endif */

/*       if n(Y) = 0 then */
	 if (pqtree_n(Y) == 0) {
/*          place Y onto QUEUE */
            QUEUE = enqueue(QUEUE, Y);
	    QUEUE_LENGTH++;
	    STACK = push(STACK, Y);
/*       fi */
	 } /* endif */
/*    else */
      } else {
#        ifdef DEBUG
	 fprintf(pqfile, "\nX is ROOT(T,S)\n");
	 fflush(pqfile);
#        endif
/*       an dieser Stelle sollte QUEUE empty sein und damit kann auf "break" *
 *       verzichtet werden, da die schleife nun verlassen wird */
	 S_Root = X;

/*       liegt Planaritaet vor, also ist der S_Root vom typ B, H oder A *
 *       wird das ergebnis zurueckgegeben, und der Baum reduziert, sonst *
 *       wird bis zu den blaettern, anhand des stacks durchgereicht, welche *
 *       Blaetter geloescht werden muessen */
	 if (pqtree_b(S_Root) == 0) {
            S_Type = B_TYPE;
	 } else if (pqtree_h(S_Root) == 0) {
            S_Type = H_TYPE;
         } else if (pqtree_a(S_Root) == 0) {
	    S_Type = A_TYPE;
	 } else if (pqtree_w(S_Root) == 0) {
            S_Type = W_TYPE;
         } else {
            S_Type = NO_TYPE;
	 } /* endif */
/*    fi */
      } /* endif */
/* od */
   } /* endwhile */

   if ((S_Type == B_TYPE) || (S_Type == H_TYPE) || (S_Type == A_TYPE)) {
/*    free STACK */
      while (!isempty(STACK)) {
	 STACK = pop(STACK);
      } /* endwhile */
      *DEL_STACK = STACK;
      return true;
   } /* endif */

   if (S_Type == W_TYPE) {
/*    strange, this case should never occur */
/*    free STACK */
      while (!isempty(STACK)) {
	 STACK = pop(STACK);
      } /* endwhile */
      *DEL_STACK = STACK;
      return false;
   } /* endif */

   *DEL_STACK = STACK;
   return false;
}


/****************************************************************************\
 *                                                                          *
 *  Global void DELETE_MINIMUM_PERTINENT_LEAVES(STACK)                      *
 *  --------------------------------------------------                      *
 *                                                                          *
 *  author:  a.j. winter (11027)  05/91.                                    *
 *                                                                          *
 ****************************************************************************
 *                                                                          *
 *  parameters:                                                             *
 *                                                                          *
 *  returns   : ---                                                         *
 *                                                                          *
 *  call from :                                                             *
 *                                                                          *
 *  uses      :                                                             *
 *                                                                          *
 *  task      :                                                             *
 *                                                                          *
\****************************************************************************/

Global void	DELETE_MINIMUM_PERTINENT_LEAVES(Slist STACK)
{
   PQtree	S_Root, X, child;

/* arbeite nun den stack, also den PQ-Tree top-down ab *
 * und bestimme den type von jedem S-node */

/* um den Knoten ein label zu geben, : *
 * typ b, w, a bzw. h benutze das label feld *
 * (sonst in reduce benutzt) */

/* S_Type == NOORDER */
/* schaue nun, ob pertinent_root zu type H oder type A gemacht wird *
 * type B scheidet aus, da sonst leere blaetter des baums geloescht werden *
 * muessen. */
/* im algorithmus nach jayakumar sollen aber nur pertinent leaves geloescht *
 * werden. */
/* das minimum der beiden Werte h und a eines jeden PQ-Nodes entscheidet *
 * darueber zu welchem typ der Knoten gemacht werden soll. *
 * dadurch sind auch die typen der kinder des knotens (eindeutig) bestimmt */

   S_Root = top_pqtree(STACK);
   if (pqtree_w(S_Root) <= pqtree_a(S_Root)) {
      pqtree_mpg_type(S_Root) = W_TYPE;
   } else {
      pqtree_mpg_type(S_Root) = A_TYPE;
   } /* endif */

   while (!isempty(STACK)) {
      X = top_pqtree(STACK);
      STACK = pop(STACK);

/*    cases:                                                                 *
 *     - X is Type B, keep X and all its descendants (and mark them Type B)  *
 *     - else                                                                *
 *      - X is a leaf, then X is Type W and should be deleted                *
 *      - else                                                               *
 *      - X is Type W, all its pertinent children should me made             *
 *        Type W. if any of these nodes is a full node, the complete         *
 *        subtree rooted at that node must be deleted.                       *
 *      - X is Type H and a P-Node, then make the partial_child h_child1     *
 *        Type H, all full children Type B and all other partial children    *
 *        Type W (empty children are not regarded since they are not         *
 *        pertinent nodes).                                                  *
 *      - X is Type H and a Q-Node, then traverse the children from h_child1 *
 *        towards the rightmost child and determine the maximal consecutive  *
 *        sequence of pertinent children of P_L or P_R.                      *
 *        Then make all the nodes  in that sequence Type B. The rightmost    *
 *        node in in P_L or the leftmost node in P_R are made Type H,        *
 *        and all other pertinent children of X are made Type W.             *
 *      - X is Type A and a P-Node, then if a_child is not empty, then make  *
 *        a_child Type A and all other pertinent children Type W.            *
 *        Else (a_child is empty) then make h_child1 and h_child2 Type H,    *
 *        all full children of X Type B and all other partial chldren Type W.*
 *      - X is Type A and a Q-Node, if a_child is not empty, make a_child    *
 *        Type A and all other pertinent children Type W.                    *
 *        If a_child is empty, then traverse the children of X from h_child2 *
 *        towards the leftmost child and find the maximal consecutive        *
 *        sequence P_A of pertinent children of X. Then make all nodes in    *
 *        P_A Type B, the endmost nodes in P_A, if they are partial, Type H  *
 *        and all other pertinent children of X Type.                        */

      if (pqtree_type(X) == LEAF) {
/*       since this leaf is in the STACK, it is a pertinent leaf and *
 *       hence FULL */
	 if (pqtree_mpg_type(X) == W_TYPE) {
/*          DELETE PQ-Node and corresponding edge into X in graph */
	    edge_in_mpg(pqtree_corresponding_edge(X)) = FALSE;
	    pqtree_marker(X) = DELETED;
	    delete_leaf_in_proper_PQtree(X);
	 /* delete_child(X); */
	 } /* endif */

      } else {  /* pqtree_type(X) != LEAF */
	 if (pqtree_mpg_type(X) == W_TYPE) {
	    if (pqtree_label(X) == FULL) {
/*             DELETE the complete subtree rooted at X *
 *             and all corresponding edges to that subtree (or leaves ?) */
/*             this will be performed, since all pertinent children are *
 *               in the stack and have label full and will hence be deleted *
 *                in a future step */
/*                delete X and the corresponding edge */
/*                but note that the parent of X in the PQtree may already be *
 *                deleted, and this is a PQ-Tree of its own, rooted at X */
	    } /* endif */
	    pqtree_marker(X) = DELETED;
	    for_all_children(X, child) {
	       pqtree_mpg_type(child) = W_TYPE;
	    } end_for_all_children(X, child);

	 } else if (pqtree_mpg_type(X) == B_TYPE) {
	    for_all_children(X, child) {
	       pqtree_mpg_type(child) = B_TYPE;
	    } end_for_all_children(X, child);

	 } else if (pqtree_mpg_type(X) == H_TYPE) {
	    if (pqtree_type(X) == P_NODE) {
	       for_all_children(X, child) {
		  if (child == pqtree_h_child1(X)) {
		     pqtree_mpg_type(child) = H_TYPE;
		  } else {
		     if (pqtree_label(child) == FULL) {
			pqtree_mpg_type(child) = B_TYPE;
		     } else {
			pqtree_mpg_type(child) = W_TYPE;
		     } /* endif */
		  } /* endif */
	       } end_for_all_children(X, child);

	    } else if (pqtree_type(X) == Q_NODE) {
	       pqtree_mpg_type(pqtree_h_child1(X)) = H_TYPE;
	       if (pqtree_marker(X) == P_L) {
		  child = pqtree_h_child1(X);
		  while (child != pqtree_leftmost_child(X)) {
		     child = pqtree_left_sibling(child);
		     pqtree_mpg_type(child) = B_TYPE;
		  } /* endwhile */
		  child = pqtree_h_child1(X);
		  while (child != pqtree_rightmost_child(X)) {
		     child = pqtree_right_sibling(child);
		     pqtree_mpg_type(child) = W_TYPE;
		  } /* endwhile */
	       } else { /* pqtree_marker(X) == P_R */
		  child = pqtree_h_child1(X);
		  while (child != pqtree_rightmost_child(X)) {
		     child = pqtree_right_sibling(child);
		     pqtree_mpg_type(child) = B_TYPE;
		  } /* endwhile */
		  child = pqtree_h_child1(X);
		  while (child != pqtree_leftmost_child(X)) {
		     child = pqtree_left_sibling(child);
		     pqtree_mpg_type(child) = W_TYPE;
		  } /* endwhile */
	       } /* endif */
	    } /* endif */

	 } else if (pqtree_mpg_type(X) == A_TYPE) {
	    if (pqtree_type(X) == P_NODE) {
	       if (pqtree_a_child(X) != empty_pqtree) {
		  for_all_children(X, child) {
		     if (child == pqtree_a_child(X)) {
			pqtree_mpg_type(child) = A_TYPE;
		     } else {
			pqtree_mpg_type(child) = W_TYPE;
		     } /* endif */
		  } end_for_all_children(X, child);
	       } else {
		  for_all_children(X, child) {
		     if (child == pqtree_h_child1(X)) {
			pqtree_mpg_type(child) = H_TYPE;
		     } else if (child == pqtree_h_child2(X)) {
			pqtree_mpg_type(child) = H_TYPE;
		     } else {
			if (pqtree_label(child) == FULL) {
			   pqtree_mpg_type(child) = B_TYPE;
			} else {
			   pqtree_mpg_type(child) = W_TYPE;
			} /* endif */
		     } /* endif */
		  } end_for_all_children(X, child);
	       } /* endif */

	    } else if (pqtree_type(X) == Q_NODE) {
	       if (pqtree_a_child(X) != empty_pqtree) {
		  for_all_children(X, child) {
		     if (child == pqtree_a_child(X)) {
			pqtree_mpg_type(child) = A_TYPE;
		     } else {
			pqtree_mpg_type(child) = W_TYPE;
		     } /* endif */
		  } end_for_all_children(X, child);
	       } else {
		  bool	sequence;

		  child = pqtree_h_child2(X);
		  pqtree_mpg_type(child) = H_TYPE;
		  sequence = true;
		  while (child != pqtree_leftmost_child(X)) {
		     child = pqtree_left_sibling(child);
		     if (sequence) {
			if (pqtree_label(child) == FULL) {
			   pqtree_mpg_type(child) = B_TYPE;
			} else if (pqtree_label(child) == PARTIAL) {
			   pqtree_mpg_type(child) = H_TYPE;
			   sequence = false;
			} else {
			   pqtree_mpg_type(child) = W_TYPE;
			   sequence = false;
			} /* endif */
		     } else {
			pqtree_mpg_type(child) = W_TYPE;
		     } /* endif */
		  } /* endwhile */
		  child = pqtree_h_child2(X);
		  while (child != pqtree_rightmost_child(X)) {
		     child = pqtree_right_sibling(child);
		     pqtree_mpg_type(child) = W_TYPE;
		  } /* endwhile */
	       } /* endif */
	    } /* endif */

	 } /* endif */
      } /* endif */
   } /* endwhile */

#  ifdef DEBUG
   fflush(pqfile);
#  endif

}


/****************************************************************************\
 *                                                                          *
 *  Global PQtree   Reduce_Pertinent_Leaves_To_Unique_Leaf(S, S_LENGTH)     *
 *  -------------------------------------------------------------------     *
 *                                                                          *
 *  author:  a.j. winter (11027)  05/91.                                    *
 *                                                                          *
 ****************************************************************************
 *                                                                          *
 *  parameters:                                                             *
 *                                                                          *
 *  returns   : ---                                                         *
 *                                                                          *
 *  call from :                                                             *
 *                                                                          *
 *  uses      :                                                             *
 *                                                                          *
 *  task      :                                                             *
 *                                                                          *
\****************************************************************************/

Global PQtree   Reduce_Pertinent_Leaves_To_Unique_Leaf(Slist S, int S_LENGTH)
                    /* List of Leafs, for which reduction is performed */
                 
{
   PQtree   NewLeaf, child;

   if (S_LENGTH == 1) {
      NewLeaf = pqtree_in_slist(S);
      S = rest(S);  /* S <- empty_slist */
   } else {
/*    reduce all full (pertinent) leaves of the reduced tree by one leaf.   *
 *    this is possible, since as the tree is reduced all pertinent leaves   *
 *    become consecutive.                                                   *
 *    a little care must be taken, when shrinking the leaves:               *
 *     - P-Nodes with only one remaining child will be deleted              *
 *     - Q-Nodes with less than three children will be turned to a P-Node   */

      NewLeaf = pqtree_in_slist(S);
      S = rest(S);
      S_LENGTH--;

      while (S_LENGTH > 0) {
	 child = pqtree_in_slist(S);
	 S = rest(S);
	 delete_leaf_in_proper_PQtree(child);
	 S_LENGTH--;
      } /* endwhile */

/*    wenn alle S-nodes kinder vom selben vater sind, *
 *    genuegt, die folgende Sonderfallbetrachtung. *
 *    ist aber die voraussetzung falsch, muss diese betrachtung *
 *    fuer alle moeglichen vaeter durchgefuehrt werden *
 *    also innerhalb der while-schleife */
/*    ?????? */

 
   } /* endif */

   return NewLeaf;
}



# ifdef MPG_OZAWA
Local void      get_a_and_h_values(PQtree X)
{
   int          h, a_I, a_II;
   PQtree       child;

/*    compute h and a for X */
      h    = 0;
      a_I  = 0;
      a_II = 0;
      if (pqtree_type(X) == P_NODE) {
	 int min_wi_bi, maxval, maxval2, max_wi_ai;

	 maxval    = 0;
	 maxval2   = 0;
	 max_wi_ai = 0;
	 for_all_children(X, child) {
	    min_wi_bi = min(pqtree_b(child), pqtree_w(child));
	    h    = h    + min_wi_bi;
	    a_I  = a_I  + min_wi_bi;
	    a_II = a_II + pqtree_w(child);
	    if ((min_wi_bi - pqtree_h(child)) > maxval) {
	       maxval2 = maxval;
	       maxval  = min_wi_bi - pqtree_h(child);
	    } else if ((min_wi_bi - pqtree_h(child)) > maxval2) {
	       maxval2 = min_wi_bi - pqtree_h(child);
	    } /* endif */
	    if ((pqtree_w(child) - pqtree_a(child)) > max_wi_ai) {
	       max_wi_ai = pqtree_w(child) - pqtree_a(child);
	    } /* endif */
	 } end_for_all_children(X, child);
	 pqtree_h(X) = h    - maxval;
	 a_I 	     = a_I  - (maxval + maxval2);
	 a_II	     = a_II - max_wi_ai;
	 pqtree_a(X) = min(a_I, a_II);

      } else if (pqtree_type(X) == Q_NODE) {
	 int 	h1, h2, min_h1, min_h2,
		sum_bi, sum_wi, sumdown_bi, sumdown_wi,
		max_wi_ai, maxval,
		y, max_y, idx_y, z, max_z, idx_z,
		j, k;
	 Slist  valStack;

	 sum_bi = 0;
	 sum_wi = 0;
	 min_h1 = pqtree_h(pqtree_leftmost_child(X));
	 min_h2 = pqtree_h(pqtree_leftmost_child(X));
	 max_y = 0;
	 idx_y = 0;
	 max_wi_ai = 0;
	 valStack = empty_stack;
	 j = 0;

	 for_all_children(X, child) {
	    j++;
	    h1 = sum_wi - sum_bi - pqtree_b(child) + pqtree_h(child);
	    h2 = sum_bi - sum_wi - pqtree_w(child) + pqtree_h(child);
	    if (h1 < min_h1) {
	       min_h1 = h1;
	    } /* endif */
	    if (h2 < min_h2) {
	       min_h2 = h2;
	    } /* endif */
	    y = sum_bi - sum_wi + pqtree_b(child) - pqtree_h(child);
	    if (y > max_y) {
	       valStack = push_flag(valStack, max_y);
	       valStack = push_flag(valStack, idx_y);
	       max_y = y;
	       idx_y = j;
	    } /* endif */
	    if ((pqtree_w(child) - pqtree_a(child)) > max_wi_ai) {
	       max_wi_ai = pqtree_w(child) - pqtree_a(child);
	    } /* endif */
	    sum_bi += pqtree_b(child);
	    sum_wi += pqtree_w(child);
	 } end_for_all_children(X, child);
	 h = min(min_h1 + sum_bi, min_h2 + sum_wi);
	 pqtree_h(X) = h;

	 sumdown_bi = 0;
	 sumdown_wi = 0;
	 max_z = 0;
	 idx_z = 0;
	 maxval = 0;
	 child = pqtree_rightmost_child(X);
	 k = j;
	 while (child != pqtree_leftmost_child(X)) {
	    z = sumdown_bi - sumdown_wi + pqtree_b(child) - pqtree_h(child);
	    if (z > max_z) {
	       if (j > idx_y) {
		  max_z = z;
		  idx_z = j;
		  maxval = max_y + max_z;
	       } else {
                  if (k >= j) {
		     while (top_flag(valStack) >= j) {
		        valStack = pop(valStack); /* pop idx_y */
			valStack = pop(valStack); /* pop max_y */
		     } /* endwhile */
		     k = top_flag(valStack);
		     valStack = pop(valStack);
		     y = top_flag(valStack);
		     valStack = pop(valStack);
		  } /* endif */
		  if (y + z > maxval) {
		     max_y = y;
		     idx_y = k;
		     max_z = z;
		     idx_z = j;
		     maxval = max_y + max_z;
		  } /* endif */
	       } /* endif */
	    } /* endif */
	    sumdown_bi += pqtree_b(child);
	    sumdown_wi += pqtree_w(child);
	    j--;
	    child = pqtree_left_sibling(child);
	 } /* endwhile */
	 a_I 	     = sum_bi - maxval;
	 a_II	     = sum_wi - max_wi_ai;
	 pqtree_a(X) = min(a_I, a_II);
	 while (!isempty(valStack)) {
	    valStack = pop(valStack);
	 } /* endwhile */
      } else {

/*       pqtree_type(X) == LEAF */
/*       h and a remain 0 for leafs */
         pqtree_h(X) = 0;
	 pqtree_a(X) = 0;
      } /* endif */

   return;
}
#endif


#ifdef MPG_JAYAKUMAR
Local void      get_a_and_h_values(PQtree X)
{
/* the main idea of Jayakumar is, not to delete pertinent- and non-pertinent *
 * leaves, but only to delete pertinent leaves. *
 * for that reason partial and empty children, resp. cannot be made type B   */
   PQtree       child;



/*    compute h and a for X */

      if (pqtree_type(X) == P_NODE) {
/*       w- and b-values are already known */

/*       h-value: * 
 *       make the cheapest child type H, all full children type B *
 *       and all empty and partial children type W */
/*       a-value: * 
 *       either make two children type H, empty ones type W and *
 *       full ones type B *
 *       or make one child type A and all others type W */
      
/*       h = (w_i1 + ... + w_ik) - max(w_i - h_i)
            for all ij indicating PARTIAL children */
         int    w_h, w_a, max_w_h1, max_w_h2, max_w_a,
		h, a, a_I, a_II;
         PQtree max_h_child1, max_h_child2, max_a_child;
 
	 max_w_h1       = 0;
	 max_w_h2       = 0;
         max_w_a        = 0;
	 h              = 0;
         a              = 0;
         max_h_child1   = empty_pqtree;
	 max_h_child2   = empty_pqtree;
         max_a_child    = empty_pqtree;
         for_all_children(X, child) {
	    if (pqtree_label(child) == PARTIAL) {
               w_h = pqtree_w(child) - pqtree_h(child);
               w_a = pqtree_w(child) - pqtree_a(child);
	       h = h + pqtree_w(child);
               a = a + pqtree_w(child);
               if (w_h > max_w_h1) {
		  max_w_h2 = max_w_h1;
                  max_h_child2 = max_h_child1;
                  max_w_h1 = w_h;
		  max_h_child1 = child;
               } else if (w_h > max_w_h2) {
                  max_w_h2 = w_h;
		  max_h_child2 = child;
               } /* endif */
	       if (w_a > max_w_a) {
		  max_w_a = w_a;
                  max_a_child = child;
               } /* endif */
	    } else if (pqtree_label(child) == FULL) {
               w_a = pqtree_w(child) - pqtree_a(child);
               a = a + pqtree_w(child);
	       if (w_a > max_w_a) {
                  max_w_a = w_a;
                  max_a_child = child;
	       } /* endif */
	    } /* endif */
         } end_for_all_children(X, child);
	 pqtree_h(X) = h - max_w_h1;
         pqtree_h_child1(X) = max_h_child1;
         a_I  = a - max_w_a;
	 a_II = h - max_w_h1 - max_w_h2;
         if (a_I < a_II) {
            pqtree_a_child(X) = max_a_child;
	    pqtree_a(X) = a_I;
         } else {
            pqtree_h_child2(X) = max_h_child2;
	    pqtree_a(X) = a_II;
         } /* endif */

      } else if (pqtree_type(X) == Q_NODE) {
         int    sum_w_h1, sum_w_h2, w_h, w_a,
                sum_w, sum_w_h, 
		max_w_h, max_w_a,
                h, a_I, a_II;
         PQtree max_h_child1, max_h_child2, 
		max_h_child,  max_a_child;

	 sum_w          = 0;
	 sum_w_h1       = 0;
         sum_w_h2       = 0;
         sum_w_h        = 0;
	 max_w_h        = UNDEFINED;
	 max_w_a        = UNDEFINED;
	 max_h_child	= empty_pqtree;
	 max_h_child1   = empty_pqtree;
	 max_h_child2   = empty_pqtree;
         max_a_child    = empty_pqtree;

/*       the initialisation of max_h_child1 is for the case, that           *
 *       both endmost children of this Q-node are empty, but in this case   *
 *       this node may not be Type H, therefor infinite costs for           *
 *       h-value are appropriate                                            */
/*       max_h_child1   = pqtree_leftmost_child(X); */
	 for_all_children(X, child) {
	    if (pqtree_label(child) == EMPTY) {
	       break;
	    } /* endif */
	    sum_w_h1 = sum_w_h1 + pqtree_w(child) - pqtree_h(child);
	    max_h_child1 = child;
	    if (pqtree_label(child) == PARTIAL) {
	       break;
	    } /* endif */
	 } end_for_all_children(X, child);

	 for_reversed_children(X, child) {
	    if (pqtree_label(child) == EMPTY) {
	       break;
	    } /* endif */
	    sum_w_h2 = sum_w_h2 + pqtree_w(child) - pqtree_h(child);
	    max_h_child2 = child;
	    if (pqtree_label(child) == PARTIAL) {
	       break;
	    } /* endif */
	 } end_for_reversed_children(X, child);

	 for_all_children(X, child) {
	    if (pqtree_label(child) != EMPTY) {
	       sum_w = sum_w + pqtree_w(child);
	       w_a = pqtree_w(child) - pqtree_a(child);
	       w_h = pqtree_w(child) - pqtree_h(child);
	       if (w_a > max_w_a) {
		  max_w_a = w_a;
		  max_a_child = child;
	       } /* endif */
	       sum_w_h = sum_w_h + w_h;
	       if (sum_w_h > max_w_h) {
		  max_w_h = sum_w_h;
		  max_h_child = child;
	       } /* endif */
	       if (pqtree_label(child) == PARTIAL) {
		  sum_w_h = w_h;
	       } /* endif */
	    } else {
	       sum_w_h = 0;
	    } /* endif */
	 } end_for_all_children(X, child);

/*       suppose max_h_child1 is not initialised, then by testing it for *
 *       null, it could be seen that X may not be Type H,                *
 *       assign infinite costs to h-value                                */
	 h = sum_w;
	 if (sum_w_h2 > sum_w_h1) {
	    pqtree_h(X) = h - sum_w_h2;
	    pqtree_h_child1(X) = max_h_child2;
	    pqtree_marker(X) = P_R;
	 } else {
	    if (max_h_child1 == empty_pqtree) {
	       pqtree_h(X) = INFINITE;
	       pqtree_h_child1(X) = pqtree_leftmost_child(X);
	       pqtree_marker(X) = P_L;
	    } else {
	       pqtree_h(X) = h - sum_w_h1;
	       pqtree_h_child1(X) = max_h_child1;
	       pqtree_marker(X) = P_L;
	    } /* endif */
	 }

	 a_I = sum_w - max_w_a;
	 a_II = sum_w - max_w_h;
	 if (a_I < a_II) {
	    pqtree_a_child(X) = max_a_child;
	    pqtree_a(X) = a_I;
	 } else {
	    pqtree_h_child2(X) = max_h_child;
	    pqtree_a(X) = a_II;
	 } /* endif */

      } else {
/*       pqtree_type(X) == LEAF */
/*       h and a remain 0 for leafs */
	 pqtree_h(X) = 0;
         pqtree_a(X) = 0;
      } /* endif */

   return;
}
#endif







