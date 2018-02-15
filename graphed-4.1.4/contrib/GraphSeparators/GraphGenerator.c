/******************************************************************************/
/*                                                                            */
/*    GraphGenerator.c                                                        */
/*                                                                            */
/******************************************************************************/
/*  Functions for creation of certain graph-classes:                          */
/*    - Random graphs (also complete graphs)                                  */
/*    - General trees (also binary trees)                                     */
/*    - Grid graphs (random and complete)                                     */
/*    - Euclidean graphs                                                      */
/*  The generated graphs do not contain any attributes.                       */
/******************************************************************************/
/*  Owner    :  Harald Lauer                                                  */
/*  Created  :  15.08.1994                                                    */
/*  Modified :  15.08.1994                                                    */
/*  History  :  Version:    Changes:                                          */
/*              1.0         First Revision                                    */
/******************************************************************************/

/******************************************************************************/
/*  Includes                                                                  */
/******************************************************************************/

#include "GraphGenerator.h"
#include "sgraph/random.h"

/******************************************************************************/
/*  Definitions                                                               */
/******************************************************************************/

double  MaxRand = 2147483647.0;       /* == pow ((double) 2, (double) 31) - 1 */

/******************************************************************************/
/*  Function prototypes                                                       */
/******************************************************************************/

#ifdef ANSI_HEADERS_OFF
Local void  CreateNextLevel    ();
Local double  ComputeDistance  ();
#endif

#ifdef ANSI_HEADERS_ON
Local void  CreateNextLevel    (Sgraph  currGraph, 
                                Snode   rootNode, 
                                int     Levels, 
                                double  subProb, 
                                double  sonProb, 
                                int     MaxSons, 
                                int     currLevel);
Local double  ComputeDistance  (Snode  sourceNode, Snode  targetNode);
#endif

/******************************************************************************/
/*  Generator of complete graphs.                                             */
/******************************************************************************/

/******************************************************************************/
/*    GenerateRandomGraph                                                     */
/*----------------------------------------------------------------------------*/
/*  Generates the random graph with the specified numer of nodes. Each node   */
/*  is connected to another node in the graph with the specified probability. */
/*  If Prob equals one, the generated graph is complete.                      */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  Size   Number of Nodes in the generated graph.            */
/*                  Prob   Probability for the existence of edge.             */
/*  Return value :  The new Sgraph.                                           */
/******************************************************************************/
Global Sgraph  GenerateRandomGraph  (int Size, double Prob)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Sgraph    newGraph   = make_graph (EMPTY_ATTR);
  Snode     sourceNode = empty_snode;
  Snode     targetNode = empty_snode;
  int       currNumber = 0;
  
  /****************************************************************************/
  /*  Initialize the random number generator.                                 */
  /****************************************************************************/
  
  srandom (time (NULL));

  /****************************************************************************/
  /*  Create all neccessary nodes and edges.                                  */
  /****************************************************************************/
  
  for (currNumber = 0; currNumber < Size; currNumber++)
  {
    sourceNode = make_node (newGraph, EMPTY_ATTR);

    for_all_nodes (newGraph, targetNode)
    {
      if ((sourceNode != targetNode) && (((double)random())/MaxRand <= Prob))
      {
        make_edge (sourceNode, targetNode, EMPTY_ATTR);
      } /* endif */
    }  end_for_all_nodes (newGraph, targetNode);
  }  /* endfor */

  /****************************************************************************/
  /*  Return the generated graph.                                             */
  /****************************************************************************/
  
  return  newGraph;

}  /* End of GenerateRandomGraph */

/******************************************************************************/
/*  Generator of trees.                                                       */
/******************************************************************************/

/******************************************************************************/
/*    GenerateTree                                                            */
/*----------------------------------------------------------------------------*/
/*  Generates a tree with the specified number of levels. A subtree is        */
/*  created with probability Prob. The generated tree is complete, if Prob    */
/*  equals one. The number of sons of a node is bounded by MaxSons and the    */
/*  tree is binary if MaxSons equals 2.                                       */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  Levels    Number of levels in the generated graph.        */
/*                  subProb   Probabilty to create a subtree.                 */
/*                  sonProb   Probabilty to create a son-node.                */
/*                  MaxSons   Maximum number of sons of a node in the tree.   */
/*  Return value :  The new Sgraph.                                           */
/******************************************************************************/
Global Sgraph  GenerateTree  (int Levels, double subProb, double sonProb, int MaxSons)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Sgraph  newGraph = make_graph (EMPTY_ATTR);
  Snode   rootNode = make_node (newGraph, EMPTY_ATTR);

  /****************************************************************************/
  /*  Initialize the random number generator.                                 */
  /****************************************************************************/
  
  srandom (time (NULL));

  /****************************************************************************/
  /*  Recursively create the necessary levels.                                */
  /****************************************************************************/
  
  CreateNextLevel (newGraph, rootNode, Levels, subProb, sonProb, MaxSons, 1);

  /****************************************************************************/
  /*  Return the generated graph.                                             */
  /****************************************************************************/
  
  return  newGraph;

}  /* End of GenerateTree */

/******************************************************************************/
/*    CreateNextLevel                                                         */
/*----------------------------------------------------------------------------*/
/*  Generates the subtree of a tree with the specified number of levels.      */
/*  The root of the tree is the specified node.                               */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph   Current tree to generate.                     */
/*                  rootNode    Root of the subtree to generate.              */
/*                  Levels      Number of levels in the generated graph.      */
/*                  subProb     Probabilty to create a subtree.               */
/*                  sonProb     Probabilty to create a son-node.              */
/*                  MaxSons     Maximum number of sons of a node in the tree. */
/*                  currLevel   Current generated level.                      */
/*  Return value :  The new Sgraph.                                           */
/******************************************************************************/
Local void  CreateNextLevel  (Sgraph currGraph, 
                              Snode rootNode, 
                              int Levels, 
                              double subProb, 
                              double sonProb, 
                              int MaxSons, 
                              int currLevel)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Snode  newNode    = empty_snode;
  int    currNumber = 0;
  
  /****************************************************************************/
  /*  Create all neccessary nodes and edges.                                  */
  /****************************************************************************/
  
  for (currNumber = 0; currNumber < MaxSons; currNumber++)
  {
    if (((double)random())/MaxRand <= sonProb)
    {
      newNode = make_node (currGraph, EMPTY_ATTR);
      make_edge (rootNode, newNode, EMPTY_ATTR);
      
      if ((((double)random())/MaxRand <= subProb) && (currLevel < Levels))
      {
        CreateNextLevel (currGraph, 
                         newNode, 
                         Levels, 
                         subProb, 
                         sonProb, 
                         MaxSons, 
                         currLevel + 1);
      }  /*endif */
    }  /* endif */
  }  /* endfor */

}  /* End of CreateNextLevel */

/******************************************************************************/
/*  Generator for grid graphs.                                                */
/******************************************************************************/

/******************************************************************************/
/*    GenerateGridGraph                                                       */
/*----------------------------------------------------------------------------*/
/*  Generates a subgraph of a infinite grid in the plane with the specified   */
/*  size. A grid-edge is inserted into the graph with the specified proba-    */
/*  bility. Coordinates are generated for the nodes.                          */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  Xsize   Number of nodes in the x-direction.               */
/*                  Ysize   Number of nodes in the y-direction.               */
/*                  Prob    Probability for the existence of a grid edge.     */
/*  Return value :  The new Sgraph.                                           */
/******************************************************************************/
Global Sgraph  GenerateGridGraph  (int Xsize, int Ysize, double Prob)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Sgraph     newGraph   = make_graph (EMPTY_ATTR);
  Snode      newNode    = empty_snode;
  Snode      oldNode    = empty_snode;
  AQueuePtr  currYLevel = InitAQueue ();
  AQueuePtr  oldYLevel  = InitAQueue ();
  AQueuePtr  tempLevel  = NULL;
  Attributes oldAttr;
  int        currY      = 0;
  int        currX      = 0;

  /****************************************************************************/
  /*  Initialize the random number generator.                                 */
  /****************************************************************************/
  
  srandom (time (NULL));

  /****************************************************************************/
  /*  Generate the grid.                                                      */
  /****************************************************************************/
  
  for (currY = 1; currY <= Ysize; currY++)
  {
    for (currX = 1; currX <= Xsize; currX++)
    {
      newNode = make_node (newGraph, EMPTY_ATTR);
      EnAQueue (currYLevel, CREATE_NODESET_ENTRY (newNode));
      
      newNode->x = currX;
      newNode->y = currY;
      
      if ((oldNode != empty_snode) && (((double)random())/MaxRand <= Prob))
      {
        make_edge (newNode, oldNode, EMPTY_ATTR);
      }  /* endif */
      
      if (!AQUEUE_IS_EMPTY(oldYLevel))
      {
        oldAttr = DeAQueue (oldYLevel);
        
        if (((double)random())/MaxRand <= Prob)
        {
          make_edge (newNode, (Snode) oldAttr.value.data, EMPTY_ATTR);
        }  /* endif */
      }  /* endif */
      
      oldNode = newNode;
    }  /* endfor */
    
    tempLevel  = oldYLevel;
    oldYLevel  = currYLevel;
    currYLevel = tempLevel;
    EmptyAQueue (currYLevel);
    oldNode  = empty_snode;
  }  /* endfor */

  /****************************************************************************/
  /*  Clean up                                                                */
  /****************************************************************************/
  
  RemoveAQueue (currYLevel);
  RemoveAQueue (oldYLevel);
  
  /****************************************************************************/
  /*  Return the generated graph.                                             */
  /****************************************************************************/
  
  return  newGraph;

}  /* End of GenerateGridGraph */

/******************************************************************************/
/*  Generator of euclidean graphs                                             */
/******************************************************************************/

/******************************************************************************/
/*    GenerateEuclideanGraph                                                  */
/*----------------------------------------------------------------------------*/
/*  Generates a graph according to the following scheme:                      */
/*  1)  Generate the specified number of nodes and assign to each an x- and   */
/*      y-coordinate between 0 and 1000.                                      */
/*  2)  Connect each two nodes with euclidean distance less the specified     */
/*      distance.                                                             */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  Size       Number of nodes in the generated graph.        */
/*                  Distance   Maximum distance at which nodes are connected. */
/*  Return value :  The new Sgraph.                                           */
/******************************************************************************/
Global Sgraph  GenerateEuclideanGraph  (int Size, double Distance)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Sgraph    newGraph   = make_graph (EMPTY_ATTR);
  Snode     sourceNode = empty_snode;
  Snode     targetNode = empty_snode;
  int       currNumber = 0;
  
  /****************************************************************************/
  /*  Initialize the random number generator.                                 */
  /****************************************************************************/
  
  srandom (time (NULL));

  /****************************************************************************/
  /*  Create all neccessary nodes and edges.                                  */
  /****************************************************************************/
  
  for (currNumber = 0; currNumber < Size; currNumber++)
  {
    sourceNode = make_node (newGraph, EMPTY_ATTR);
    
    sourceNode->x = (int) (1000.0 *((double)random()) / MaxRand);
    sourceNode->y = (int) (1000.0 *((double)random()) / MaxRand);

    for_all_nodes (newGraph, targetNode)
    {
      if ((sourceNode != targetNode) &&
          (ComputeDistance (sourceNode, targetNode) <= Distance))
      {
        make_edge (sourceNode, targetNode, EMPTY_ATTR);
      } /* endif */
    }  end_for_all_nodes (newGraph, targetNode);
  }  /* endfor */

  /****************************************************************************/
  /*  Return the generated graph.                                             */
  /****************************************************************************/
  
  return  newGraph;

}  /* End of GenerateEuclideanGraph */

/******************************************************************************/
/*    ComputeDistance                                                         */
/*----------------------------------------------------------------------------*/
/*  Computes the euclidean distance between the two specified nodes.          */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  sourceNode   First node.                                  */
/*                  targetNode   Second node.                                 */
/*  Return value :  The computed distance.                                    */
/******************************************************************************/
Local double  ComputeDistance  (Snode sourceNode, Snode targetNode)
{
  /****************************************************************************/
  /*  Compute the distance between the two nodes.                             */
  /****************************************************************************/
  
  return  sqrt(pow ((double) (targetNode->x - sourceNode->x), (double) 2.0) +
               pow ((double) (targetNode->y - sourceNode->y), (double) 2.0));

}  /* End of ComputeDistance */

/******************************************************************************/
/*  End of  GraphGenerator.c                                                  */
/******************************************************************************/
