#include <sgraph/std.h>
#include <sgraph/slist.h>
#include <sgraph/sgraph.h>
#include <sgraph/sgragra.h>
#include <sgraph/graphed.h>

#include <xview/xview.h>
#include <xview/panel.h>

#include "misc.h"
#include "main_sf.h"
#include "types.h"

#include "lp_main.h"



int	SUBFRAME_VISIBLE = FALSE;

/*****************************************************************************
Die Zwei grundlegenden Funktionen, die von aussen aufzurufen sind.
Der Unterschied: Es werden zwei verschiedene Flags gesetzt.
ORGINAL_LAMSHOFT = TRUE: Alles was mit Layoutalgorithmen zu tun hat, wird
			 am Bildschirm nicht dargestellt. Diese Dtaenstrukturen
			 werden nicht erzeugt
ORGINAL_LAMSHOFT = FALSE:Alles was mit Graph-Layout zu tun hat, kommt dazu.

Die Funktionen:
	create_lamshoft_parser
	create_parser_with_layout_extensions
*****************************************************************************/

void	create_lamshoft_parser(Menu menu, Menu_item menu_item)
{
	ORGINAL_LAMSHOFT	= TRUE;


	if( SUBFRAME_VISIBLE &&
	    (ORGINAL_LAMSHOFT != LAST_CREATED_FOR_LAMSHOFT) )
	{
		error( "LGG-Parser window must be closed before.\n" );
		ORGINAL_LAMSHOFT	= FALSE;
	}
	else
	{
		WIN_create_parser_subframe( menu, menu_item );
	}

}



void	create_parser_with_layout_extensions(Menu menu, Menu_item menu_item)
{
	ORGINAL_LAMSHOFT	= FALSE;


	if( SUBFRAME_VISIBLE &&
	    (ORGINAL_LAMSHOFT != LAST_CREATED_FOR_LAMSHOFT) )
	{
		error( "Parser window must be closed before.\n" );
		ORGINAL_LAMSHOFT	= TRUE;
	}
	else
	{
		WIN_create_parser_subframe( menu, menu_item );
	}
}

