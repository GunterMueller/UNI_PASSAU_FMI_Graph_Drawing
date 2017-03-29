/******************************************************************************/
/*                                                                            */
/*    GraphGenerator.h                                                        */
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

#include <Separator.h>
#include <AttrsQueue.h>

/******************************************************************************/
/*  Function prototypes                                                       */
/******************************************************************************/

#ifdef ANSI_HEADERS_OFF
Global Sgraph  GenerateRandomGraph     ();
Global Sgraph  GenerateTree            ();
Global Sgraph  GenerateGridGraph       ();
Global Sgraph  GenerateEuclideanGraph  ();
#endif

#ifdef ANSI_HEADERS_ON
Global Sgraph  GenerateRandomGraph     (int  Size, double Prob);
Global Sgraph  GenerateTree            (int     Levels, 
                                        double  subProb, 
                                        double  sonProb, 
                                        int     MaxSons);
Global Sgraph  GenerateGridGraph       (int  Xsize, int  Ysize, double  Prob);
Global Sgraph  GenerateEuclideanGraph  (int  Size, double  Distance);
#endif

/******************************************************************************/
/*  End of  GraphGenerator.h                                                  */
/******************************************************************************/
