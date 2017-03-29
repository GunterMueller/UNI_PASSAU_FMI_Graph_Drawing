#include "GS-Debug.h"


Local void   PrintNodeSet            (NodeSet currSet, char *SetString);
Local void   PrintEdgeSet            (EdgeSet currSet, char *SetString);
Local char * BoolString              (bool boolValue);
Local void   DebugClearAndSetLabels  (Sgraph currGraph);

Global void PrintMultiListsSize (Sgraph currGraph)
{
  Snode  currNode = NULL;
  Sedge  currEdge = NULL;
  
  for_all_nodes (currGraph, currNode)
  {
    printf ("\nNode %d    - ", currNode->nr);
    
    for_sourcelist (currNode, currEdge)
    {
      printf ("(%d, %d) ", currEdge->tnode->nr, size_of_slist (MULTI_LIST (currEdge)));
    }  end_for_sourcelist (currNode, currEdge);
    
    if (currGraph->directed)
    {
      printf ("| ");
      
      for_targetlist (currNode, currEdge)
      {
      printf ("(%d, %d) ", currEdge->snode->nr, size_of_slist (MULTI_LIST (currEdge)));
      }  end_for_targetlist (currNode, currEdge);
    }  /* endif */
    
    printf ("\n");
  }  end_for_all_nodes (currGraph, currNode);
}

Global void PrintAdjacencyList (Sgraph currGraph)
{
  Snode  currNode = NULL;
  Sedge  currEdge = NULL;
  
  for_all_nodes (currGraph, currNode)
  {
    printf ("\nNode %d    - ", currNode->nr);
    
    for_sourcelist (currNode, currEdge)
    {
      printf ("%d ", currEdge->tnode->nr);
    }  end_for_sourcelist (currNode, currEdge);
    
    if (currGraph->directed)
    {
      printf ("| ");
      
      for_targetlist (currNode, currEdge)
      {
        printf ("%d ", currEdge->snode->nr);
      }  end_for_targetlist (currNode, currEdge);
    }  /* endif */
    
    printf ("\n");
  }  end_for_all_nodes (currGraph, currNode);
}

Global void  Print5x5Grid  (Sgraph currGraph)
{
  Snode  currNode  = NULL;
  int    nodeCount = 1; 
  
  DebugClearAndSetLabels (currGraph);
  
  for_all_nodes (currGraph, currNode)
  {
    if (currNode->label != NULL)
    {
      printf ("%s", currNode->label);
    }
    else
    {
      printf ("\n\tLabel is undefined !!\n\n");
      return;
    }  /* endif */
    
    if ((nodeCount) % 5)
    {
      printf ("  -  ");
    }
    else
    {
      printf ("\n\n");
    }  /* endif */

    nodeCount++;
    
  }  end_for_all_nodes (currGraph, currNode);
  
  printf ("\n");
}

Global void  PrintNodeSlist (char *InfoString, Slist nodeList, bool wait)
{
  Slist        currEntry   = empty_slist;
  static bool  enableWait  = TRUE; 
        char         input       = 'n';

  if (wait && enableWait) 
  {
    printf ("\n%s\n", InfoString);
    for_slist (nodeList, currEntry)
    {
      PrintNodeInfo (GET_NODESET_ENTRY (currEntry));
    }  end_for_slist (nodeList, currEntry);
    printf ("\n");
    printf ("Disable waiting ? (n) ");
    input = getchar();
    enableWait = (input != 'y');
        }
}

Global void  PrintEdgeSlist (char *InfoString, Slist edgeList, bool wait)
{
  Slist  currEntry = empty_slist;
  static bool  enableWait  = TRUE; 
        char         input       = 'n';

  if (wait && enableWait) 
  {
    printf ("\n%s\n", InfoString);
    for_slist (edgeList, currEntry)
    {
      PrintEdgeInfo (GET_EDGELIST_ENTRY (currEntry));
    }  end_for_slist (edgeList, currEntry);
    printf ("\n");
    printf ("Disable waiting ? (n) ");
    input = getchar();
    enableWait = (input != 'y');
        }
}

Global void  PrintNodeInfo (Snode currNode)
{
  printf ("Knoten:\n");
  if (currNode == NULL)
  {
    printf ("\tNULL\n");
  }
  else
  {
    printf ("\tnr=%d, (x,y)=(%d,%d)", 
      currNode->nr, currNode->x, currNode->y);
    if (currNode->label != NULL)
    {
      printf (", label=%s\n", currNode->label);
    }
    else
    {
      printf (", label=NULL\n");
    }
    printf ("\t\tVisited=%s, ", BoolString (IS_NODE_VISITED (currNode)));
    PrintNodeSet (PART_OF_NODE_SET (currNode), "PartOf");
    printf (", ");
    PrintNodeSet (VALID_NODE_SET (currNode), "Valid");
    printf (", Dummy=%s", BoolString (IS_DUMMY (currNode)));
    printf (", D=%f\n", D_VALUE (currNode));
  }
}

Global void  PrintEdgeInfo (Sedge currEdge)
{
  printf ("Kante:\n");
  if (currEdge == NULL)
  {
    printf ("\tNULL\n");
  }
  else
  {
    printf ("\tSource ");
    PrintNodeInfo (currEdge->snode);
    printf ("\tTarget ");
    PrintNodeInfo (currEdge->tnode);
    if (currEdge->label != NULL)
    {
      printf ("\tlabel=%s, ", currEdge->label);
    }
    else
    {
      printf ("\tlabel=NULL, ");
    }
    PrintEdgeSet (PART_OF_EDGE_SET (currEdge), "PartOf");
    printf (", ");
    PrintEdgeSet (VALID_EDGE_SET (currEdge), "Valid");
                printf ("\n");
  }
}

Local void  PrintNodeSet (NodeSet currSet, char *SetString)
{
  switch (currSet)
  {
    case NodeNone       :  
      printf ("%s=NodeNone", SetString);      break;
    case LeftSet        :  
      printf ("%s=LeftSet", SetString);       break;
    case RightSet       :  
      printf ("%s=RightSet", SetString);      break;
    case SeparatorNode  :  
      printf ("%s=SeparatorNode", SetString); break;
  }
}

Local void  PrintEdgeSet (EdgeSet currSet, char *SetString)
{
  switch (currSet)
  {
    case EdgeNone      :  
      printf ("%s=EdgeNone", SetString);      break;
    case A_Set         :  
      printf ("%s=A_Set", SetString);         break;
    case B_Set         :  
      printf ("%s=B_Set", SetString);         break;
    case Separator_Set :  
      printf ("%s=Separator_Set", SetString); break;
  }
}

Local char * BoolString  (bool boolValue)
{
  if (boolValue)
  {
    return "TRUE";
  }
  else
  {
    return "FALSE";
  }
}

Local void DebugClearAndSetLabels (Sgraph currGraph)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/

  Snode    currNode = NULL;
  Sedge    currEdge = NULL;
  EdgeSet  currSet  = EdgeNone;
  char     Label[20];

  /****************************************************************************/
  /*  Label the nodes and edges according to the result                       */
  /****************************************************************************/

  for_all_nodes (currGraph, currNode)
  {
    if (currNode->label != NULL)
    {
      free (currNode->label);
    }  /* endif */ 

    sprintf (Label, "%2i,%+.1f,", currNode->nr, D_VALUE(currNode));
    strcat (Label, PART_OF_NODE_SET(currNode) == LeftSet ? "A" : "B");
      
    set_nodelabel (currNode, strsave (Label));

    for_sourcelist (currNode, currEdge)
    {
      if (currEdge->label != NULL)
      {
        free (currEdge->label);
      }  /* endif */ 

      currSet = (PART_OF_NODE_SET(currNode) != 
                 PART_OF_NODE_SET(NEXT_NODE(currNode, currEdge))) ?
                   Separator_Set : EdgeNone;
      set_edgelabel (currEdge, currSet == Separator_Set ?
                                 SEPARATOR_LABEL : NO_LABEL);
    }  end_for_sourcelist (currNode, currEdge);
  }  end_for_all_nodes (currGraph, currNode);
}

