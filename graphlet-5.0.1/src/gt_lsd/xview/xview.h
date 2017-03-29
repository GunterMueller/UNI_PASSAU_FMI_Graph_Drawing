/* This software is distributed under the Lesser General Public License */
/*
xview.h

taken from XView's xview.h to fake Sgraph that XView is there...
*/

/* fake XView's Menu & Menu_item */

#ifndef xview_xview_DEFINED
#define xview_xview_DEFINED

typedef int Menu;
typedef int Menu_item;
typedef int Server_image;

#define menu_get(menu, attr, v1) 0
#define event_ctrl_is_down(event) FALSE

#include <xview/rect.h>

#endif /* ~xview_xview_DEFINED */
