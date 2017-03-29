#include "nodetypes/nodetypes.h"

void	init_system_nodetypes (void)
{
  init_box_nodetype ();
  init_double_box_nodetype ();
  init_black_box_nodetype ();

  init_diamond_nodetype ();
  init_double_diamond_nodetype ();
  init_black_diamond_nodetype ();

  init_elliptical_nodetype ();
  init_double_elliptical_nodetype ();
  init_black_elliptical_nodetype ();

  init_black_nodetype ();
  init_white_nodetype ();

  init_pixmap_nodetype ();
}
