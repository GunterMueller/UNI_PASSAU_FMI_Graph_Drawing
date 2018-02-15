/******************************************************************************/
/*                                                                            */
/*    SUNInterface.h                                                          */
/*                                                                            */
/******************************************************************************/
/*  Implementation of the interface of the Graph-Separator package to GraphEd.*/
/******************************************************************************/
/*  Owner    :  Harald Lauer                                                  */
/*  Created  :  11.06.1994                                                    */
/*  Modified :  11.06.1994                                                    */
/*  History  :  Version:    Changes:                                          */
/*              1.0         First Revision                                    */
/******************************************************************************/

#ifdef SUN_VERSION
#ifndef  SUN_INTERFACE
#define  SUN_INTERFACE

/******************************************************************************/
/*  Includes                                                                  */
/******************************************************************************/

#include "Interface.h"
#include <xview/xview.h>
/*#include <xview/panel.h>*/

#ifdef ANSI_HEADERS_OFF
void MenuGS ();
#endif 

#ifdef ANSI_HEADERS_ON
void MenuGS (Menu menu, Menu_item menu_item);
#endif

#endif
#endif

/******************************************************************************/
/*  End of  SUNInterface.h                                                    */
/******************************************************************************/
