/******************************************************************************/
/*                                                                            */
/*    Fiduccia-Mattheyses.h                                                   */
/*                                                                            */
/******************************************************************************/
/*  Heuristic to compute an alpha-edge separator according to Fiduccia and    */
/*  Mattheyses.                                                               */
/******************************************************************************/
/*  Owner    :  Harald Lauer                                                  */
/*  Created  :  16.06.1994                                                    */
/*  Modified :  16.06.1994                                                    */
/*  History  :  Version:    Changes:                                          */
/*              1.0         First Revision                                    */
/******************************************************************************/

#ifndef  FID_MATT
#define  FID_MATT

/******************************************************************************/
/*  Includes                                                                  */
/******************************************************************************/

#include <Separator.h>

/******************************************************************************/
/*  Data structure                                                            */
/******************************************************************************/

struct  _BucketCell               /* Bucket containing unlocked nodes with    */
{                                 /* a specific D-value.                      */
  int    CellDValue;              /* D-value of the nodes in this bucket.     */
  Slist  UnlockedNodes;           /* All unlocked nodes with this D-value.    */
  
  struct _BucketCell  *PrevCell;  /* Previous cell in the list of buckets.    */
  struct _BucketCell  *SuccCell;  /* Next cell in the list of buckets.        */
};
typedef  struct _BucketCell   BucketCell;
typedef  BucketCell          *BucketCellPtr; 

struct  _Bucket              /* Store information on all unlocked nodes.      */
{
  BucketCellPtr  FirstCell;  /* Cell containing nodes yielding maximum gain.  */
  BucketCellPtr  LastCell;   /* Cell containing nodes yielding minimum gain.  */
  BucketCellPtr  MaxGain;    /* Nonempty bucket yielding maximum gain.        */
};
typedef  struct _Bucket   Bucket;
typedef  Bucket          *BucketPtr; 

struct  _PassInfo       /* Maintain data on the state of the pass.            */
{
  BucketPtr  BucketA;   /* Bucket containing unlocked nodes of the left set.  */
  BucketPtr  BucketB;   /* Bucket containing unlocked nodes of the right set. */
  int        ASize;     /* Number of locked nodes moved into the left set.    */
  int        BSize;     /* Number of locked nodes moved into the right set.   */
  Slist      LockedA;   /* Locked nodes moved into the left set.              */
  Slist      LockedB;   /* Locked nodes moved into the right set.             */
};
typedef  struct _PassInfo   PassInfo;
typedef  PassInfo          *PassInfoPtr;

struct  _MoveLogEntry   /* Entry in the ordered list of possible node moves.  */
{
  Snode    MovedNode;   /* Node to move.                                      */
  NodeSet  OldSet;      /* Node set in which MovedNode was.                   */
  int      Gain;        /* Gain obtained from the move operation.             */
};
typedef  struct _MoveLogEntry   MoveLogEntry;
typedef  MoveLogEntry          *MoveLogEntryPtr;

/******************************************************************************/
/*  Definitions                                                               */
/******************************************************************************/

#define  CREATE_LOG_ENTRY(l)    (make_attr (ATTR_DATA, (char *) (l)))

#define  GET_NODE(l)       (attr_data_of_type ((l), MoveLogEntryPtr)->MovedNode)
#define  GET_OLDSET(l)     (attr_data_of_type ((l), MoveLogEntryPtr)->OldSet)
#define  GET_GAIN(l)       (attr_data_of_type ((l), MoveLogEntryPtr)->Gain)

/******************************************************************************/
/*  Function prototypes                                                       */
/******************************************************************************/

#ifdef  ANSI_HEADERS_OFF
Global void  FiducciaMattheyses  (/*algInfo, validSep*/);
#endif

#ifdef  ANSI_HEADERS_ON
Global void  FiducciaMattheyses  (GSAlgInfoPtr  algInfo, bool  validSep);
#endif

#endif

/******************************************************************************/
/*  End of  Fiduccia-Mattheyses.h                                             */
/******************************************************************************/
