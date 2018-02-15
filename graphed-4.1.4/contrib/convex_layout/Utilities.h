/* (C) Universitaet Passau 1986-1994 */
/*1994-91 von Stefan Jockenhoevel and Gerd Nienhaus.*/

/*****************************************************************************************/
/*****************************************************************************************/
/**                                                                                     **/
/**                   MODULE Utilities.h                                                **/
/**                                                                                     **/
/*****************************************************************************************/
/*****************************************************************************************/

#include SGRAPHH

#ifndef UTILITIES_HEADER
#define UTILITIES_HEADER

#define talloc(type) (type *)malloc(sizeof(type))

#define EmptySnode ((Snode)NULL)

typedef struct sedgestack{
        Sedge              edge;
        struct sedgestack  *suc;
        } *SedgeStack;
        
#define EmptySedgeStack ((SedgeStack)NULL)



extern Snode GetTargetNode(Slist TheEdgelistElem);
extern Snode GetSourceNode(Slist TheEdgelistElem);
extern Snode TargetNodeGet(Slist TheEdgelistElem);
extern Snode SourceNodeGet(Slist TheEdgelistElem);

typedef struct edgestack{
        Slist   edgelistelem;
        struct edgestack  *suc;
        } *EdgeStack;
        
#define EmptyStack ((EdgeStack)NULL)
        
        
typedef enum{NEWNUM,NUMBER} AttrType;        
        
typedef struct point{
        int  x,y;
        } Point;

extern void AppendEdgeStack(EdgeStack *TheStack, Slist NewEdge);

extern void AppendSedgeStack(SedgeStack *TheStack, Sedge NewEdge);

#define PopEStack(EStack) ((EStack != EmptyStack) ? ( EStack = EStack->suc) : (EStack = EmptyStack)) 

extern Snode FindNode(Sgraph TheGraph, AttrType IdentAttr, int IdentNr);

extern Slist FindFacialCycle(Sgraph TheGraph);

extern Slist FindFacialCycle(Sgraph TheGraph);

extern void DrawNodeBetweenAB(Snode TheNode, Point A, Point B, double lambda);
        
#endif
