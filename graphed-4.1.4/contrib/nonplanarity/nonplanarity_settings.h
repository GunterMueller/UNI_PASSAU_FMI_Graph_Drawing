#ifndef NONPLANARITY_SETTINGS_H
#define NONPLANARITY_SETTINGS_H
/****************************************************************************\
 *                                                                          *
 *  nonplanarity_settings.h                                                 *
 *  -----------------------                                                 *
 *                                                                          *
 *  author:  a.j. winter (11027)  06/93.                                    *
 *                                                                          *
\****************************************************************************/

#define  MY_EDGESTYLE_SOLID 	0
#define  MY_EDGESTYLE_DASHED	1
#define  MY_EDGESTYLE_DOTTED	2

#define  MPG_JAYAKUMAR		1
#define  MPG_OZAWA		4
#define  MPG_GREEDY		2
#define  MPG_RANDOMIZED_GREEDY	3
#define  MPG_JAYAKUMAR_PRE	5
#define  MPG_JAYAKUMAR_GREEDY	6

#define	ITERATIONS	50
#define MAXMAX		100

typedef struct _maxplanarsettings {
	bool	graph_is_already_planar;
	bool	create_new_window_for_mpg;
	bool	use_planar_embedding;

	int	index_for_edgestyle;

	int	algorithm_to_run;

	int	deleted_edge_count;
	int	re_inserted_edge_count;

	int	iterations_for_randomized_greedy;
}	*MaxPlanarSettings;


typedef struct _thicknesssettings {
	bool	graph_is_already_planar;
	bool	create_new_window_for_mpg;
	bool	embed_each_mpg_planar;
	bool	mark_edges_with_label; /* either mark them with labels  *
					* or with style */

	int	index_for_edgestyle;
}	*ThicknessSettings;


typedef struct _crossingnumbersettings {
	bool	graph_is_already_planar;
	int	center_x;
	int	center_y;
	int	max_distance;

	int	algorithm_to_run;
}	*CrossingNumberSettings;




#define NEW_MAXPLANAR_SETTINGS (MaxPlanarSettings) \
				malloc(sizeof(struct _maxplanarsettings)) 


#ifndef SUN_VERSION
#define SUN_VERSION
#endif

extern	MaxPlanarSettings	create_and_init_maxplanar_settings(void);

#define ACCURACY_TRADEOFF(n)	(n < MAXMAX)

#endif
