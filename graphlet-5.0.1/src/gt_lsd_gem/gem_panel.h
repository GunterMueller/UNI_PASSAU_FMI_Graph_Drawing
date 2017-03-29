/* This software is distributed under the Lesser General Public License */
#ifndef GEM_PANEL_H
#define GEM_PANEL_H

extern void read_config(void);
extern int do_random, check_quality;

extern void menu_gem_show_subframe(Menu menu, Menu_item menu_item);
extern void show_gem_subframe(void);
extern void set_gem_default_config( double insert_max_temp, double insert_start_temp,
	double insert_final_temp, double insert_max_iter, double
		insert_gravity, double insert_oscilation, double
		insert_rotation, double insert_shake,
	int	insert_skip,
	double	arrange_max_temp, double arrange_start_temp, double
		arrange_final_temp, double arrange_max_iter, double
		arrange_gravity, double arrange_oscilation, double
		arrange_rotation, double arrange_shake,
	int	arrange_skip,
	double	optimize_max_temp, double optimize_start_temp, double
		optimize_final_temp, double optimize_max_iter, double
		optimize_gravity, double optimize_oscilation, double
		optimize_rotation, double optimize_shake,
	int	optimize_skip,
	int the_random, int quality, int edgelen);

#endif
