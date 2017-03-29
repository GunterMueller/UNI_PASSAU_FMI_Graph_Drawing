/****************************************************************************\
 *                                                                          *
 *  random.c    	                                                    *
 *  --------	                                                     	    *
 *                                                                          *
 *  author:  a.j. winter (11027)  06/93.                                    *
 *                                                                          *
\****************************************************************************/



#include <stdlib.h>
#include <time.h>
#include <math.h>
#include <string.h>

#if defined SUN_VERSION
#include <std.h>
#include <slist.h>
#include <sgraph.h>
#include <sgraph/random.h>
#else
#include <sgraph\std.h>
#include <sgraph\slist.h>
#include <sgraph\sgraph.h>
#include <sgraph\random.h>
#endif


#include "sattrs.h"

/****************************************************************************\
 *                                                                          *
 *  Global	void	init_random_number_generator()                      *
 *  --------------------------------------------------                      *
 *                                                                          *
 *  author:  a.j. winter (11027)  06/93.                                    *
 *                                                                          *
 ****************************************************************************
 *                                                                          *
 *  parameters: ---						            *
 *                                                                          *
 *  returns   : ---							    *
 *                                                                          *
 *  call from : this module (this is a local function)                      *
 *                                                                          *
 *  uses      : random number generator (system independent ?) 		    *
 *                                                                          *
 *  task      : initialize the random number generator, depending on the    *
 *		system time (therefor 'stdlib.h' and 'time.h' are included.)*
 *                                                                          *
\****************************************************************************/

Global	void	init_random_number_generator(void)
{
#  ifdef SUN_VERSION
   srandom ((int) time (NULL));
#  else
   randomize();
#  endif
}


/****************************************************************************\
 *                                                                          *
 *  Global	Slist	get_slist_elem_by_random(Slist, int) 	            *
 *  --------------------------------------------------------                *
 *                                                                          *
 *  author:  a.j. winter (11027)  06/93.                                    *
 *                                                                          *
 ****************************************************************************
 *                                                                          *
 *  parameters: list of type Slist, number of elements in list of type int ,*
 *                                                                          *
 *  returns   : element of list of type Slist, that was chosen by the 	    *
 *		random number generator.                                    *
 *                                                                          *
 *  call from : this module (this is a local function)                      *
 *                                                                          *
 *  uses      : random number generator (system independent ?)		    *
 *                                                                          *
 *  task      : random number generator determines a number that is in the  *
 *		range of number of elements in the given list.              *
 *		finally the element that is refered  to is returned.	    *
 *                                                                          *
\****************************************************************************/

Global	Slist	get_slist_elem_by_random(Slist list, int elements)
{
   int	elem_number, count;
   Slist	elem;

   if (isempty(list)) {
      return empty_slist;
   } else {
      if (elements == 1) {
	 return list;
      } else {
/*       random returns a value between 0 and arg-1			    */
#	 if defined SUN_VERSION
	 elem_number = random() % elements;
#	 else
	 elem_number = random(elements);
#	 endif
	 count = 0;
	 elem = list;
	 while (count < elem_number) {
	    elem = elem->suc;
	    count++;
	 } /* endwhile */

	 return elem;
      } /* endif */
   } /* endif */
}


Global	Sgraph	create_randomized_graph(int max_random_nodes)
{
   Sgraph	graph;
   Snode	node, adjNode1, adjNode2;
   Sedge	edge;
   int		count, nodeCount, edgeCount;
   int		adjNode1Nr, adjNode2Nr;

#  if defined SUN_VERSION
   nodeCount = random() % max_random_nodes;
#  else
   nodeCount = random(max_random_nodes);
#  endif
   nodeCount += 3;

   graph = empty_sgraph;

   if (nodeCount > 0) {
      graph = make_graph(empty_attrs);
      if (graph == empty_sgraph) {
	 return empty_sgraph;
      } /* endif */

      graph->directed = FALSE;

      count = 0;
      while (count < nodeCount) {
	 count++;
	 node = make_node(graph, empty_attrs);
	 node->nr = count;
      } /* endwhile */

/*    create between n and n*(n-1)/2 edges				    */
#     if defined SUN_VERSION
      edgeCount = random() % ((nodeCount * (nodeCount-1) / 2) - nodeCount);
#     else
/*    edgeCount = random((nodeCount * (nodeCount-1) / 2) - nodeCount); */
      edgeCount = random(2*nodeCount) + 3*nodeCount;
#     endif
      edgeCount += nodeCount;

      count = 0;
      while (count < edgeCount) {
	 count++;
#        if defined SUN_VERSION
	 adjNode1Nr = random() % nodeCount;
	 adjNode2Nr = random() % nodeCount;
#        else
	 adjNode1Nr = random(nodeCount);
	 adjNode2Nr = random(nodeCount);
#        endif

	 adjNode1Nr++;
	 adjNode2Nr++;

	 if (adjNode1Nr != adjNode2Nr) {
	    for_all_nodes(graph, node) {
	       if (node->nr == adjNode1Nr) {
		  adjNode1 = node;
	       } /* endif */
	       if (node->nr == adjNode2Nr) {
		  adjNode2 = node;
	       } /* endif */
	    } end_for_all_nodes(graph, node);

	    for_sourcelist(adjNode1, edge) {
	       if (edge->tnode->nr == adjNode2Nr) {
		  adjNode2Nr = 0;
		  break;
	       } /* endif */
	    } end_for_sourcelist(adjNode1, edge);

	    if (adjNode2Nr != 0) {
	       edge = make_edge(adjNode1, adjNode2, empty_attrs);
	    } /* endif */
	 } /* endif */

      } /* endwhile */
   } /* endif */

   return graph;
}



