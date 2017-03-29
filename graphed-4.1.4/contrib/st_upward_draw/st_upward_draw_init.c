/*===========================================================================*/
/*  
	 PROJECT	st_upward_draw

	 SYSTEM:       	GraphEd extension module
	 FILE:        	st_upward_draw_init.c
	 AUTHOR:       	Roland Stuempfl (diploma 1994)


	 Overview
	 ========
	 Source code of the second part of the local_main part of the graphed
	 extension module st_upward_draw, based on the algorithm upward_draw
	 presented by Guiseppe Di Battista and Roberto Tamassia, published
	 in "Algorithms For Plane Representations of Acyclic Digraphs",
	 Theoretical Computer Science 61 (1988), pp. 175-198.
	 In the following this work will be referenced as [DiBatTam88].
*/
/*===========================================================================*/

{
  Menu menu;

  st_settings = init_st_settings();

  menu = graphed_create_submenu ();

  add_entry_to_menu (menu,
		     "run algorithm",
		     menu_st_upward_draw_layout);
  add_entry_to_menu (menu,
		     "settings ...",
		     menu_st_upward_draw_layout_subframe);

  add_menu_to_layout_menu ("planar / st upward", menu);
}
