#ifdef UNIX5
#ifndef lint
static char version_id[] = "@(#) File : display.c 93/03/12  03:34:24  Version 1.3 Copyright (C) 1992/93 Schweikardt Andreas";
#endif
#endif 

/*******************************************************************************
*									       *
*									       *
*			SEARCH STRATEGIES ON GRAPHS			       *
*									       *
*									       *
*	Copyright (C) 1992/93 Andreas Schweikardt			       *
********************************************************************************




	File	:	display.c

	Date	:	93/03/12	(03:34:24)

	Version	:	1.3

	Author	:	Schweikardt, Andreas



Portability

	Language		:	C
	Operating System	:	Sun-OS (UNIX)
	User Interface (graphic):	
	Other			:	GraphEd & Sgraph


********************************************************************************


Layer   : 	

Modul   :	


********************************************************************************


Description of display.c :




********************************************************************************


Functions of display.c :



*******************************************************************************/


/******************************************************************************
*                                                                             *
*			standard includes				      *
*                                                                             *
*******************************************************************************/

#include <stdio.h>
#include <string.h>


/******************************************************************************
*                                                                             *
*			gui includes    	 			      *
*                                                                             *
*******************************************************************************/

#include <xview/xview.h>
#include <xview/frame.h>
#include <xview/panel.h>


/******************************************************************************
*                                                                             *
*			local includes		 			      *
*                                                                             *
*******************************************************************************/

#include <search/search.h>
#include <search/window.h>
#include <search/control.h>
#include <graph.h>

/******************************************************************************
*                                                                             *
*			local variables					      *
*                                                                             *
*******************************************************************************/

static char	*string = "1234567890"; /* used in a macro */


/******************************************************************************
*                                                                             *
*                                                                             *
*                       global functions		                      *
*                                                                             *
*                                                                             *
******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	WNreport

arguments   :	

return()    :	void

description :	display all the informations about the search (only if the
		values have changed !)

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	WNreport(void)
{

/**************************************************************************/
/* the number of searchers and the maximum number of searchers are always */
/* displayed, only if values have changed, this makes it a bit faster	  */
/* output is very time expensive 					  */
/**************************************************************************/
	WNreportSearchers();
	WNreportMaxSearchers();

/*****************************************************************************/
/* if the more-button many MORE useful information is diaplayed, but only if */	
/* ==> we can show it only if the output items exists 			     */
/*****************************************************************************/
	if ( COstatistics() )
	{
		WNreportMaxSteps();
		WNreportSteps();
		WNreportEdges();
		WNreportEdgesClear();
		WNreportEdgesNotClear();
		WNreportNodes();
		WNreportNodesSet();
		WNreportNodesNotSet();
		WNreportNodesUnset();
	}

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	WNreportOneItem

arguments   :	
   	type		name		description(-I/-O)
1.	Panel_item	item		the output panel-I
2.	unsigned int	number		value to be displayed-I

return()    :	void

description :	formatting the value 'number' and prints it to the panel item

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	WNreportOneItem(Panel_item item, unsigned int number)
{
	sprintf( string, "%5d", number ); /*formatting */

	xv_set( item, PANEL_LABEL_STRING, string, NULL ); /* and print */

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	WNshowInfo

arguments   :	
   	type		name		description(-I/-O)
1.	char*		string		info to be displayed-I

return()    :	void

description :	displays some useful(?) information about the algorithm
		itself, not only the search done by this algorithm

use	    :		

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	WNshowInfo(char *string)
{
	if ( *string != 0  ) /* 'string' == NULL := delete message */
	{

		xv_set( WNgetItem( WNITEM_INFO ),
			PANEL_LABEL_STRING,	string,
			NULL );
	}
	else
	{
		/* remove old string */
		xv_set( WNgetItem( WNITEM_INFO ),
			PANEL_LABEL_STRING,	"",
			NULL );
	}	

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	WNshowFilename

arguments   :	

return()    :	void

description :

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void 	WNshowFilename(void)
{

	char		*name,
			*delimiter,
			*last_dir_name,
			*last_name;

	name = (char *)buffer_get_filename(wac_buffer);

	if ( name != NULL )
	{
		if ( *name != 0 )
		{
			/* extract the last two names of the directory path */
			last_dir_name = name;
			last_name = name;
			delimiter = name;
			while( *delimiter != 0)
			{
				while( *delimiter != '/' && *delimiter != 0 )
					delimiter++;
				last_dir_name = last_name;
				last_name = name;
				if ( *delimiter != 0 )
					name = ++delimiter;
			}
			name = last_dir_name;
		}
		xv_set( WNgetItem( WNITEM_FILENAME ),
			PANEL_LABEL_STRING,	name,
			NULL );
	}
	else
	{
		/* graph doesn't have a name yet */
		xv_set( WNgetItem( WNITEM_FILENAME ),
			PANEL_LABEL_STRING,	"<no name>",
			NULL );
	}

	return;
}
/******************************************************************************/



/******************************************************************************
*		       [EOF] end of file display.c 
******************************************************************************/
