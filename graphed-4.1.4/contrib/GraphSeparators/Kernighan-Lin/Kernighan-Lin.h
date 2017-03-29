/******************************************************************************/
/*                                                                            */
/*    Kernighan-Lin.h                                                         */
/*                                                                            */
/******************************************************************************/
/*  Implementation of the Kernighan-Lin graph-spearator heuristic.            */
/*  Functions to compute alpha edge separators.                               */
/******************************************************************************/
/*  Owner    :  Harald Lauer                                                  */
/*  Created  :  14.03.1994                                                    */
/*  Modified :  28.06.1994                                                    */
/*  History  :  Version:    Changes:                                          */
/*              1.4         Name change.                                      */
/*              1.3         Resolved naming collisions.                       */
/*              1.2         Interface changed (algInfo).                      */
/*              1.1         Randomized initial node set assignment.           */
/*              1.0         First Revision                                    */
/******************************************************************************/

#ifndef K_L
#define K_L

/******************************************************************************/
/*  Includes                                                                  */
/******************************************************************************/

#include <Separator.h>

/******************************************************************************/
/*  Data structure                                                            */
/******************************************************************************/

struct  _ExchangePair
{
  Snode  A_Max;   /* Node in the left set to be exchanged                     */
  Snode  B_Max;   /* Node in the right set to be exchanged                    */
  float  Gain;    /* Gain from the exchange: == D_a + D_b - 2 * c_ab          */
};
typedef  struct  _ExchangePair  ExchangePair;
typedef  ExchangePair           *ExchangePairPtr;

/******************************************************************************/
/*  Definitions                                                               */
/******************************************************************************/

#define  CREATE_EP_ENTRY(ep)  (make_attr(ATTR_DATA, (char *)(ep)))
#define  GET_EP_ENTRY(ep)     (attr_data_of_type((ep),ExchangePairPtr))
#define  A_NODE(ep)           (attr_data_of_type((ep),ExchangePairPtr)->A_Max)
#define  B_NODE(ep)           (attr_data_of_type((ep),ExchangePairPtr)->B_Max)
#define  GAIN(ep)             (attr_data_of_type((ep),ExchangePairPtr)->Gain)

/******************************************************************************/
/*  Function prototypes                                                       */
/******************************************************************************/

#ifdef  ANSI_HEADERS_OFF
Global void  KernighanLin  (/*algInfo, validSep*/);
Global void  KLExchange    (/*algInfo, validSep*/);
#endif

#ifdef  ANSI_HEADERS_ON
Global void  KernighanLin  (GSAlgInfoPtr  algInfo, bool  validSep);
Global void  KLExchange    (GSAlgInfoPtr  algInfo, bool  validSep);
#endif

#endif

/******************************************************************************/
/*  End of  Kernighan-Lin.h                                                   */
/******************************************************************************/
