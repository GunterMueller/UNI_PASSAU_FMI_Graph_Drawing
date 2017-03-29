/*************************************************************/
/*                                                           */
/* FILE: VISIBILITY_EXPORTS.H                                */
/*                                                           */
/* Beschreibung: extern sichtbare Deklarationen              */
/*                                                           */
/*************************************************************/

#include <sgraph_interface.h>

typedef enum {        /* Verwendung der ausgewaehlten Knoten */
           u_and_v,   /* beim Algorithmus von Nummenmaa      */
           u_and_w
} Nummen_nodes;

typedef enum {   /* st-Auswahl */
           maximal_degree,
           minimal_degree,
           maximal_sum_of_degrees,
           minimal_sum_of_degrees
} ST_nodes;

typedef struct visibility_layout2_settings {
           Nummen_nodes nummen_nodes; /* ausgewaehlte Knoten beim Algorithmus von Numm. */
           bool         compression; /* Compression anwenden? */
           ST_nodes     st_nodes; /* Auswahlkriterium */
           int          vertical_distance;
           int          horizontal_distance;
           int          height;
           int          defaults_y_distance;
           int          defaults_x_distance;
           int          defaults_height;
} Visibility2_Settings;


extern Visibility2_Settings visibility_layout2_settings;

extern Visibility2_Settings init_visibility_layout2_settings (void);
extern void save_visibility_layout2_settings (void);
extern void show_visibility_subframe (void *done_proc);

extern void  call_RT_weak_visibility (Sgraph_proc_info info);
extern void  call_TT_weak_visibility (Sgraph_proc_info info);
extern void  call_TT_epsilon_visibility (Sgraph_proc_info info);
extern void  call_Kant_weak_visibility (Sgraph_proc_info info);
extern void  call_Nummenmaa_weak_visibility (Sgraph_proc_info info);
extern void  call_tree_strong_visibility (Sgraph_proc_info info);
extern GraphEd_Menu_Proc menu_RT_w_visibility;
extern GraphEd_Menu_Proc menu_TT_w_visibility;
extern GraphEd_Menu_Proc menu_TT_epsilon_visibility;
extern GraphEd_Menu_Proc menu_Kant_w_visibility;
extern GraphEd_Menu_Proc menu_Nummenmaa_w_visibility;
extern GraphEd_Menu_Proc menu_tree_s_visibility;
extern GraphEd_Menu_Proc menu_visibility_layout2_settings;


/*
Nummen_nodes nummen_nodes;
bool         compression;
ST_nodes     st_nodes;
int          horizontal_distance,vertical_distance,height;
*/


/*************************************************************/
/*                                                           */
/*            END OF FILE: VISIBILITY_EXPORTS.H              */
/*                                                           */
/*************************************************************/
