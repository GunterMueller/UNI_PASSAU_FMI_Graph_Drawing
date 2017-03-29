/* This software is distributed under the Lesser General Public License */
/* (C) Universitaet Passau 1986-1994 */
#include <sgraph/std.h>
#include <sgraph/sgraph.h>
#include <sgraph/slist.h>
#include <sgraph/graphed.h>
#include "springembedder_kamada_export.h"

void springembedder_kamada_menu_proc (Menu menu, Menu_item menu_item)
{
   call_sgraph_proc (call_springembedder_kamada, NULL);
}
