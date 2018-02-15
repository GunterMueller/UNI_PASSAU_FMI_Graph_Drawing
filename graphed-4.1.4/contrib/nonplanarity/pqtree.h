/* pqtree.h */

#ifndef PQTREE
#define PQTREE
#endif

#define UNDEFINED   (-1)

#define LEAF    (0)
#define P_NODE  (1)
#define Q_NODE  (2)

#define EMPTY   (0)
#define PARTIAL (1)
#define FULL    (2)

#define DELETED                 (3)

#define LEAF_IN_GRAPH           (4)
#define LEAF_NOT_IN_GRAPH       (5)

#define PREFERRED               (6)
#define NEW_PERTINENT           (7)

#define next_label(l)   l++
#define prev_label(l)   l--

#define UNMARKED    (-1)
#define QUEUED      (1)
#define BLOCKED     (2)
#define UNBLOCKED   (3)

#define NOORDER	    (0)
#define ASCENDING   (1)
#define DESCENDING  (2)

#define NO_TYPE         (0)
#define B_TYPE          (1)
#define W_TYPE          (2)
#define H_TYPE          (3)
#define A_TYPE          (4)

#define P_L		(0)
#define P_R		(1)

#define INFINITE	(999)

typedef struct _pqtree
{
   short             type;   /* P_NODE, Q_NODE, LEAF */
   short             label;  /* EMPTY, FULL, PARTIAL */
   short	     bubble; /* UNMARKED, QUEUED, BLOCKED, UNBLOCKED */
   short             marker; /* free usable */
   short             info;   /* free usable */   
   struct _pqtree   *parent,
		    *leftmost_child, *rightmost_child,
		    *left_sibling, *right_sibling;
/* fuer p-nodes dienen die left- und right-sibling felder der kindknoten    *
 * der zyklischen verkettung, zur traversierung der kindknoten              */
/* in q-nodes sind fuer innere kindknoten beide siblings belegt, fuer       *
 * aeussere jeweils nur das entsprechende                                   */
   int               child_count;  
   short             immed_siblings;
/* fuer kinder von p-nodes ist immed_siblings 0, von q-nodes 1 oder 2       */
   int               pertinent_child_count;
   int               pertinent_leaf_count;
   int               full_child_count;
   struct _pqtree   *full_child, *full_succ;
   int               partial_child_count;
   struct _pqtree   *partial_child, *partial_succ;
   int               entry;   /* corresponding to node_stnumber(graph_node) */
/* Snode             graph_node; */
   Sedge             corresponding_edge;  /* edge entering graphnode of leaf */
   Slist	     block_node;
/* values needed for maximal planarization */
   int               mpg_type;  /* B_ -, W_ -, H_ -, or A_TYPE */
   int		     d, l, n, m,
		     b, w, h, a;
   struct _pqtree       *h_child1, *h_child2, *a_child;
} *PQtree;

#define NEW_PQtree  (PQtree) malloc(sizeof(struct _pqtree))
#define FREE_PQtree(pqt)    free(pqt)

#define empty_pqtree    (PQtree)NULL

#define pqtree_type(pqt)                    (pqt->type)
#define pqtree_label(pqt)                   (pqt->label)
#define pqtree_bubble(pqt)                  (pqt->bubble)
#define pqtree_marker(pqt)                  (pqt->marker)
#define pqtree_mpg_info(pqt)                (pqt->info)
#define pqtree_parent(pqt)                  (pqt->parent)
#define pqtree_leftmost_child(pqt)          (pqt->leftmost_child)
#define pqtree_rightmost_child(pqt)         (pqt->rightmost_child)
#define pqtree_left_sibling(pqt)            (pqt->left_sibling)
#define pqtree_right_sibling(pqt)           (pqt->right_sibling)
#define pqtree_child_count(pqt)             (pqt->child_count)
#define pqtree_immed_siblings(pqt)          (pqt->immed_siblings)
#define pqtree_pertinent_child_count(pqt)   (pqt->pertinent_child_count)
#define pqtree_pertinent_leaf_count(pqt)    (pqt->pertinent_leaf_count)
#define pqtree_full_child_count(pqt)        (pqt->full_child_count)
#define pqtree_full_child(pqt)              (pqt->full_child)
#define pqtree_full_succ(pqt)               (pqt->full_succ)
#define pqtree_partial_child_count(pqt)     (pqt->partial_child_count)
#define pqtree_partial_child(pqt)           (pqt->partial_child)
#define pqtree_partial_succ(pqt)            (pqt->partial_succ)
#define pqtree_entry(pqt)                   (pqt->entry)
/* #define pqtree_graph_node(pqt)           (pqt->graph_node) */
#define pqtree_corresponding_edge(pqt)      (pqt->corresponding_edge)
#define pqtree_block_node(pqt)              (pqt->block_node)
#define pqtree_mpg_type(pqt)           	    (pqt->mpg_type)
#define pqtree_d(pqt)              	    (pqt->d)
#define pqtree_l(pqt)              	    (pqt->l)
#define pqtree_n(pqt)              	    (pqt->n)
#define pqtree_m(pqt)              	    (pqt->m)
#define pqtree_b(pqt)              	    (pqt->b)
#define pqtree_w(pqt)              	    (pqt->w)
#define pqtree_h(pqt)              	    (pqt->h)
#define pqtree_a(pqt)              	    (pqt->a)
#define pqtree_h_child1(pqt)                (pqt->h_child1)
#define pqtree_h_child2(pqt)                (pqt->h_child2)
#define pqtree_a_child(pqt)                 (pqt->a_child)

#define pqtree_empty_child_count(pqt)       (pqtree_child_count(pqt) - \
                                             (pqtree_full_child_count(pqt) + \
                                              pqtree_partial_child_count(pqt)))
                           


#define for_all_children(pqt, chl) \
    { if (((chl) = (pqt)->leftmost_child) != (PQtree)NULL) do {
#define end_for_all_children(pqt, chl) \
    } while (((chl) = (chl)->right_sibling) != (pqt)->leftmost_child); }

#define for_reversed_children(pqt, chl) \
    { if (((chl) = (pqt)->rightmost_child) != (PQtree)NULL) do {
#define end_for_reversed_children(pqt, chl) \
    } while (((chl) = (chl)->left_sibling) != (pqt)->rightmost_child); }

#define first_pqtree(l)     attr_data_of_type(l, PQtree)
#define top_pqtree(l)       attr_data_of_type(l, PQtree)

#define pqtree_in_slist(l)    attr_data_of_type(l,PQtree)
/* weitere macros zur Typ-unabhaengigen Behandlung von Slists in sattrs.h */












