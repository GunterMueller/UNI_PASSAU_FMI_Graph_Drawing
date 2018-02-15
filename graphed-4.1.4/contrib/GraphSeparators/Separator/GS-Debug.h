#ifndef GS_DEBUG
#define GS_DEBUG

#include <Separator.h>
#include <Interface.h>
#include <stdio.h>

Global void  PrintMultiListsSize (Sgraph currGraph);
Global void  PrintAdjacencyList  (Sgraph currGraph);
Global void  Print5x5Grid        (Sgraph currGraph);
Global void  PrintNodeSlist      (char *InfoString, Slist nodeList, bool wait);
Global void  PrintEdgeSlist      (char *InfoString, Slist edgeList, bool wait);
Global void  PrintNodeInfo       (Snode currNode);
Global void  PrintEdgeInfo       (Sedge currEdge);
  
#endif  
