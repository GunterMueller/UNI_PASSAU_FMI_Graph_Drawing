{
#include <xview/xview.h>

	Menu	menu;

	tarjan_settings = init_tarjan_settings();

	menu = graphed_create_pin_menu ("planar / visibility layouts");

	add_entry_to_menu (menu,
		"Otten and van Wijk layout",
		menu_otten_planar_layout);
	add_entry_to_menu (menu,
		"Tarjan layout",
		menu_tarjan_planar_layout);
	add_entry_to_menu (menu,
		"Tamassia layout",
		menu_tarjan2_planar_layout);
	add_entry_to_menu (menu,
		"Tamassia w-visibility layout",
		menu_tamassia_w_planar_layout);
	add_entry_to_menu (menu,
		"Tamassia e-visibility layout",
		menu_tamassia_e_planar_layout);
	add_entry_to_menu (menu,
		"Tamassia s-visibility layout",
		menu_tamassia_s_planar_layout);
	add_entry_to_menu (menu,
		"Wismath layout",
		menu_wismath_planar_layout);
	add_entry_to_menu (menu,
		"cylindric layout",
		menu_cylinder_planar_layout);
	add_entry_to_menu (menu,
		"settings ...",
		menu_tarjan_planar_layout_settings);

	add_menu_to_layout_menu ("planar / visibility layouts", menu);
}
