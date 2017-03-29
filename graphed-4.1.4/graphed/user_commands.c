/* (C) Universitaet Passau 1986-1994 */
/* GraphEd Source, 1986-1994 by Michael Himsolt	*/
/************************************************************************/
/*									*/
/*			GraphEd User Commands				*/
/*									*/
/************************************************************************/


#include "misc.h"
#include "graph.h"
#include "graphed_subwindows.h"
#include "user_header.h"


#define INCLUDE_SGRAPH
#ifdef INCLUDE_SGRAPH
#include "sgraph/std.h"
#include "sgraph/sgraph.h"
#include "sgraph/slist.h"
#include "sgraph/graphed.h"
#include "graphed_sgraph_interface.h"

extern void graphed_main(int argc, char **argv);


static	void dummy_sgraph_proc (Sgraph_proc_info info)
{
}


static	void dummy (Menu menu, Menu menu_item)
{
	call_sgraph_proc (dummy_sgraph_proc, NULL);
}


void	init_user_menu (void)
{
	add_to_user_menu ("User-defined extensions may be included here", dummy);
}


int main(int argc, char **argv)
{
	graphed_main (argc, argv);
	exit(0);
}


#endif

