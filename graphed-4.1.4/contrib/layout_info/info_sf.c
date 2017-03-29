#include <xview/xview.h>
#include <xview/panel.h>
#include <xview/notice.h>

#include <std.h>
#include <sgraph.h>
#include <slist.h>

#include <graphed.h>
#include <gridder.h>
#include <user.h>
#include <graphed_pin_sf.h>
#include <math.h>

#include "layout_info_export.h"

static void create_info_subframe(void);
static void info_sf_set(Panel_item item, Event *event);
static void info_sf_done(Panel_item item, Event *event);
void save_info_settings(void);
int layout_info_error_check (Sgraph sgraph);

Layout_info linfo;

static	Graphed_pin_subframe	info_sf=(Graphed_pin_subframe)NULL;

static	Panel_item	info_nr_of_nodes_value,
			info_nr_of_edges_value,
			info_size_value,
			info_area_used_value,
			info_width_value,
			info_height_value,
			info_density_value,
			info_nr_of_bends_value,
			info_nr_of_crossings_value,

			info_node_distances,
			info_node_distances_value_min,
			info_node_distances_value_ave,
			info_node_distances_value_max,
			info_node_distances_value_dev,
			info_node_distances_value_rat,

			info_edge_lengths,
			info_edge_lengths_value_min,
			info_edge_lengths_value_ave,
			info_edge_lengths_value_max,
			info_edge_lengths_value_dev,
			info_edge_lengths_value_rat,

			info_angles,
			info_angles_value_min,
			info_angles_value_ave,
			info_angles_value_max,
			info_angles_value_dev,
			info_angles_value_rat,

			info_faces,
			info_faces_value_min,
			info_faces_value_ave,
			info_faces_value_max,
			info_faces_value_dev,
			info_faces_value_rat;

Info_settings info_settings;


Info_settings init_info_settings(void)
{ 
  Info_settings settings;

  settings.dummy = 42;

  return settings;
}


static void create_info_subframe(void)
{
   int row_count = 0;

   if(info_sf==(Graphed_pin_subframe)NULL) {
     info_sf=new_graphed_pin_subframe((Frame)0);
   }

   info_sf->set_proc=info_sf_set;
   info_sf->done_proc=info_sf_done;

   info_sf->set_label = "Update";

   graphed_create_pin_subframe(info_sf,"Layout Information");
   xv_set (info_sf->panel,
	PANEL_LAYOUT, PANEL_HORIZONTAL,
	0);

#define COLUMN1 15
#define COLUMN2 25
#define COLUMN3 35
#define COLUMN4 45
#define COLUMN5 55
#define COLUMN6 65


   row_count+=1; 
   info_nr_of_nodes_value=xv_create(
             info_sf->panel,     PANEL_TEXT,
             PANEL_LABEL_STRING, "nodes",
             XV_X,               xv_col(info_sf->panel,0),
             XV_Y,               xv_row(info_sf->panel,row_count),
             PANEL_VALUE_DISPLAY_LENGTH,10,
             PANEL_READ_ONLY,    TRUE,
             PANEL_CLIENT_DATA,  info_sf->frame,
             0);

   info_nr_of_edges_value=xv_create(
             info_sf->panel,     PANEL_TEXT,
             PANEL_LABEL_STRING, "edges",
             PANEL_VALUE_DISPLAY_LENGTH,10,
             PANEL_READ_ONLY,    TRUE,
             PANEL_CLIENT_DATA,  info_sf->frame,
             0);

   info_size_value=xv_create(
             info_sf->panel,     PANEL_TEXT,
             PANEL_LABEL_STRING, "sum",
             PANEL_VALUE_DISPLAY_LENGTH,10,
             PANEL_READ_ONLY,    TRUE,
             PANEL_CLIENT_DATA,  info_sf->frame,
             0);

   row_count+=1; 
   info_width_value=xv_create(
             info_sf->panel,     PANEL_TEXT,
	     PANEL_LABEL_STRING, "width",
             XV_X,               xv_col(info_sf->panel,0),
             XV_Y,               xv_row(info_sf->panel,row_count),
             PANEL_VALUE_DISPLAY_LENGTH,10,
             PANEL_READ_ONLY,    TRUE,
             PANEL_CLIENT_DATA,  info_sf->frame,
             0);
        
   info_height_value=xv_create(
             info_sf->panel,     PANEL_TEXT,
	     PANEL_LABEL_STRING, "height",
             PANEL_VALUE_DISPLAY_LENGTH,10,
             PANEL_READ_ONLY,    TRUE,
             PANEL_CLIENT_DATA,  info_sf->frame,
             0);
        
   info_area_used_value=xv_create(
             info_sf->panel,     PANEL_TEXT,
	     PANEL_LABEL_STRING, "area",
             PANEL_VALUE_DISPLAY_LENGTH,10,
             PANEL_READ_ONLY,    TRUE,
             PANEL_CLIENT_DATA,  info_sf->frame,
             0);
        
   info_density_value=xv_create(
             info_sf->panel,     PANEL_TEXT,
	     PANEL_LABEL_STRING, "per node",
             PANEL_VALUE_DISPLAY_LENGTH,10,
             PANEL_READ_ONLY,    TRUE,
             PANEL_CLIENT_DATA,  info_sf->frame,
             0);

   row_count+=1; 
   info_nr_of_bends_value=xv_create(
             info_sf->panel,     PANEL_TEXT,
	     PANEL_LABEL_STRING, "bends",
             XV_X,               xv_col(info_sf->panel,0),
             XV_Y,               xv_row(info_sf->panel,row_count),
             PANEL_VALUE_DISPLAY_LENGTH,10,
             PANEL_READ_ONLY,    TRUE,
             PANEL_CLIENT_DATA,  info_sf->frame,
             0);
  
   row_count+=1; 
   info_nr_of_crossings_value=xv_create(
             info_sf->panel,     PANEL_TEXT,
	     PANEL_LABEL_STRING, "crossings",
             XV_X,               xv_col(info_sf->panel,0),
             XV_Y,               xv_row(info_sf->panel,row_count),
             PANEL_VALUE_DISPLAY_LENGTH,10,
             PANEL_READ_ONLY,    TRUE,
             PANEL_CLIENT_DATA,  info_sf->frame,
             0);

   row_count+=1; 
   xv_create(info_sf->panel,     PANEL_MESSAGE,
             PANEL_LABEL_STRING, "data",
	     PANEL_LABEL_BOLD,   TRUE,
             XV_X,               xv_col(info_sf->panel,0),
             XV_Y,               xv_row(info_sf->panel,row_count),
             0);

   xv_create(info_sf->panel,     PANEL_MESSAGE,
	     PANEL_LABEL_BOLD,   TRUE,
             PANEL_LABEL_STRING, "minimum",
             XV_X,               xv_col(info_sf->panel, COLUMN1),
             0);

   xv_create(info_sf->panel,     PANEL_MESSAGE,
	     PANEL_LABEL_BOLD,   TRUE,
             PANEL_LABEL_STRING, "average",
             XV_X,               xv_col(info_sf->panel, COLUMN2),
             0);

   xv_create(info_sf->panel,     PANEL_MESSAGE,
	     PANEL_LABEL_BOLD,   TRUE,
             PANEL_LABEL_STRING, "maximum",
             XV_X,               xv_col(info_sf->panel, COLUMN3),
             0);

   xv_create(info_sf->panel,     PANEL_MESSAGE,
	     PANEL_LABEL_BOLD,   TRUE,
             PANEL_LABEL_STRING, "deviation",
             XV_X,               xv_col(info_sf->panel, COLUMN4),
             0);

   xv_create(info_sf->panel,     PANEL_MESSAGE,
             PANEL_LABEL_STRING, "max/min",
	     PANEL_LABEL_BOLD,   TRUE,
             XV_X,               xv_col(info_sf->panel, COLUMN5),
             0);

   row_count+=1; 
   info_node_distances=xv_create(
             info_sf->panel,     PANEL_MESSAGE,
             PANEL_LABEL_STRING, "node distances",
	     PANEL_LABEL_BOLD,   TRUE,
             XV_X,               xv_col(info_sf->panel,0),
             XV_Y,               xv_row(info_sf->panel,row_count),
             0);

   info_node_distances_value_min=xv_create(
             info_sf->panel,     PANEL_TEXT,
             XV_X,               xv_col(info_sf->panel, COLUMN1),
             PANEL_VALUE_DISPLAY_LENGTH,10,
             PANEL_READ_ONLY,    TRUE,
             PANEL_CLIENT_DATA,  info_sf->frame,
             0);

   info_node_distances_value_ave=xv_create(
             info_sf->panel,     PANEL_TEXT,
             XV_X,               xv_col(info_sf->panel, COLUMN2),
             PANEL_VALUE_DISPLAY_LENGTH,10,
             PANEL_READ_ONLY,    TRUE,
             PANEL_CLIENT_DATA,  info_sf->frame,
             0);

   info_node_distances_value_max=xv_create(
             info_sf->panel,     PANEL_TEXT,
             XV_X,               xv_col(info_sf->panel, COLUMN3),
             PANEL_VALUE_DISPLAY_LENGTH,10,
             PANEL_READ_ONLY,    TRUE,
             PANEL_CLIENT_DATA,  info_sf->frame,
             0);

   info_node_distances_value_dev=xv_create(
             info_sf->panel,     PANEL_TEXT,
             XV_X,               xv_col(info_sf->panel, COLUMN4),
             PANEL_VALUE_DISPLAY_LENGTH,10,
             PANEL_READ_ONLY,    TRUE,
             PANEL_CLIENT_DATA,  info_sf->frame,
             0);

   info_node_distances_value_rat=xv_create(
             info_sf->panel,     PANEL_TEXT,
             XV_X,               xv_col(info_sf->panel, COLUMN5),
             PANEL_VALUE_DISPLAY_LENGTH,10,
             PANEL_READ_ONLY,    TRUE,
             PANEL_CLIENT_DATA,  info_sf->frame,
             0);

   row_count+=1; 
   info_edge_lengths=xv_create(
             info_sf->panel,     PANEL_MESSAGE,
             PANEL_LABEL_STRING, "edge lengths",
	     PANEL_LABEL_BOLD,   TRUE,
             XV_X,               xv_col(info_sf->panel,0),
             XV_Y,               xv_row(info_sf->panel,row_count),
             0);

   info_edge_lengths_value_min=xv_create(
             info_sf->panel,     PANEL_TEXT,
             XV_X,               xv_col(info_sf->panel, COLUMN1),
             PANEL_VALUE_DISPLAY_LENGTH,10,
             PANEL_READ_ONLY,    TRUE,
             PANEL_CLIENT_DATA,  info_sf->frame,
             0);

   info_edge_lengths_value_ave=xv_create(
             info_sf->panel,     PANEL_TEXT,
             XV_X,               xv_col(info_sf->panel, COLUMN2),
             PANEL_VALUE_DISPLAY_LENGTH,10,
             PANEL_READ_ONLY,    TRUE,
             PANEL_CLIENT_DATA,  info_sf->frame,
             0);

   info_edge_lengths_value_max=xv_create(
             info_sf->panel,     PANEL_TEXT,
             XV_X,               xv_col(info_sf->panel, COLUMN3),
             PANEL_VALUE_DISPLAY_LENGTH,10,
             PANEL_READ_ONLY,    TRUE,
             PANEL_CLIENT_DATA,  info_sf->frame,
             0);

   info_edge_lengths_value_dev=xv_create(
             info_sf->panel,     PANEL_TEXT,
             XV_X,               xv_col(info_sf->panel, COLUMN4),
             PANEL_VALUE_DISPLAY_LENGTH,10,
             PANEL_READ_ONLY,    TRUE,
             PANEL_CLIENT_DATA,  info_sf->frame,
             0);

   info_edge_lengths_value_rat=xv_create(
             info_sf->panel,     PANEL_TEXT,
             XV_X,               xv_col(info_sf->panel, COLUMN5),
             PANEL_VALUE_DISPLAY_LENGTH,10,
             PANEL_READ_ONLY,    TRUE,
             PANEL_CLIENT_DATA,  info_sf->frame,
             0);

   row_count+=1; 
   info_angles=xv_create(
             info_sf->panel,     PANEL_MESSAGE,
	     PANEL_LABEL_BOLD,   TRUE,
             PANEL_LABEL_STRING, "angles",
             XV_X,               xv_col(info_sf->panel,0),
             XV_Y,               xv_row(info_sf->panel,row_count),
             0);

   info_angles_value_min=xv_create(
             info_sf->panel,     PANEL_TEXT,
             XV_X,               xv_col(info_sf->panel, COLUMN1),
             PANEL_VALUE_DISPLAY_LENGTH,10,
             PANEL_READ_ONLY,    TRUE,
             PANEL_CLIENT_DATA,  info_sf->frame,
             0);

   info_angles_value_ave=xv_create(
             info_sf->panel,     PANEL_TEXT,
             XV_X,               xv_col(info_sf->panel, COLUMN2),
             PANEL_VALUE_DISPLAY_LENGTH,10,
             PANEL_READ_ONLY,    TRUE,
             PANEL_CLIENT_DATA,  info_sf->frame,
             0);

   info_angles_value_max=xv_create(
             info_sf->panel,     PANEL_TEXT,
             XV_X,               xv_col(info_sf->panel, COLUMN3),
             PANEL_VALUE_DISPLAY_LENGTH,10,
             PANEL_READ_ONLY,    TRUE,
             PANEL_CLIENT_DATA,  info_sf->frame,
             0);

   info_angles_value_dev=xv_create(
             info_sf->panel,     PANEL_TEXT,
             XV_X,               xv_col(info_sf->panel, COLUMN4),
             PANEL_VALUE_DISPLAY_LENGTH,10,
             PANEL_READ_ONLY,    TRUE,
             PANEL_CLIENT_DATA,  info_sf->frame,
             0);

   info_angles_value_rat=xv_create(
             info_sf->panel,     PANEL_TEXT,
             XV_X,               xv_col(info_sf->panel, COLUMN5),
             PANEL_VALUE_DISPLAY_LENGTH,10,
             PANEL_READ_ONLY,    TRUE,
             PANEL_CLIENT_DATA,  info_sf->frame,
             0);

   row_count ++;
   info_faces=xv_create(
             info_sf->panel,     PANEL_MESSAGE,
             PANEL_LABEL_STRING, "faces",
	     PANEL_LABEL_BOLD,   TRUE,
             XV_X,               xv_col(info_sf->panel,0),
             XV_Y,               xv_row(info_sf->panel,row_count),
             0);

   info_faces_value_min=xv_create(
             info_sf->panel,     PANEL_TEXT,
             XV_X,               xv_col(info_sf->panel, COLUMN1),
             PANEL_VALUE_DISPLAY_LENGTH,10,
             PANEL_READ_ONLY,    TRUE,
             PANEL_CLIENT_DATA,  info_sf->frame,
             0);

   info_faces_value_ave=xv_create(
             info_sf->panel,     PANEL_TEXT,
             XV_X,               xv_col(info_sf->panel, COLUMN2),
             PANEL_VALUE_DISPLAY_LENGTH,10,
             PANEL_READ_ONLY,    TRUE,
             PANEL_CLIENT_DATA,  info_sf->frame,
             0);

   info_faces_value_max=xv_create(
             info_sf->panel,     PANEL_TEXT,
             XV_X,               xv_col(info_sf->panel, COLUMN3),
             PANEL_VALUE_DISPLAY_LENGTH,10,
             PANEL_READ_ONLY,    TRUE,
             PANEL_CLIENT_DATA,  info_sf->frame,
             0);

   info_faces_value_dev=xv_create(
             info_sf->panel,     PANEL_TEXT,
             XV_X,               xv_col(info_sf->panel, COLUMN4),
             PANEL_VALUE_DISPLAY_LENGTH,10,
             PANEL_READ_ONLY,    TRUE,
             PANEL_CLIENT_DATA,  info_sf->frame,
             0);

   info_faces_value_rat=xv_create(
             info_sf->panel,     PANEL_TEXT,
             XV_X,               xv_col(info_sf->panel, COLUMN5),
             PANEL_VALUE_DISPLAY_LENGTH,10,
             PANEL_READ_ONLY,    TRUE,
             PANEL_CLIENT_DATA,  info_sf->frame,
             0);

   window_fit(info_sf->panel);
   window_fit(info_sf->frame);
}


int showing_info_subframe(void)
{
  return showing_graphed_pin_subframe(info_sf);
}


static	void	call_info_sf_do (Sgraph_proc_info info)
{

#define BUFFER_LENGTH 20

  char	str_nr_of_nodes[BUFFER_LENGTH],
	str_nr_of_edges[BUFFER_LENGTH],
	str_size[BUFFER_LENGTH],
	str_nr_of_bends[BUFFER_LENGTH],
	str_nr_of_crossings[BUFFER_LENGTH],
	str_density[BUFFER_LENGTH],
	str_width[BUFFER_LENGTH],
	str_height[BUFFER_LENGTH],
	str_area_used[BUFFER_LENGTH],

	str_node_dist_shortest[BUFFER_LENGTH],
	str_node_dist_longest[BUFFER_LENGTH],
	str_node_dist_average[BUFFER_LENGTH],
	str_node_dist_dev[BUFFER_LENGTH],
	str_node_dist_rat[BUFFER_LENGTH],

	str_edge_average[BUFFER_LENGTH],
	str_edge_shortest[BUFFER_LENGTH], 
	str_edge_longest[BUFFER_LENGTH],
	str_edge_dev[BUFFER_LENGTH],
	str_edge_rat[BUFFER_LENGTH],

	str_angles_min[BUFFER_LENGTH],
	str_angles_ave[BUFFER_LENGTH],
	str_angles_max[BUFFER_LENGTH],
	str_angles_dev[BUFFER_LENGTH],
	str_angles_rat[BUFFER_LENGTH],

	str_faces_min[BUFFER_LENGTH],
	str_faces_ave[BUFFER_LENGTH],
	str_faces_max[BUFFER_LENGTH],
	str_faces_dev[BUFFER_LENGTH],
	str_faces_rat[BUFFER_LENGTH];

  Layout_info linfo;


  if (!layout_info_error_check (info->sgraph)) {
    return;
  }

  str_nr_of_nodes[0]='\0';
  str_nr_of_edges[0]='\0';
  str_size[0]='\0';
  str_nr_of_bends[0]='\0';
  str_nr_of_crossings[0]='\0';
  str_density[0]='\0';
  str_width[0]='\0';
  str_height[0]='\0';
  str_area_used[0]='\0';

  str_node_dist_shortest[0]='\0';
  str_node_dist_longest[0]='\0';
  str_node_dist_average[0]='\0';
  str_node_dist_dev[0]='\0';
  str_node_dist_rat[0]='\0';

  str_edge_average[0]='\0';
  str_edge_shortest[0]='\0';
  str_edge_longest[0]='\0';
  str_edge_dev[0]='\0';
  str_edge_rat[0]='\0';

  str_angles_min[0]='\0';
  str_angles_ave[0]='\0';
  str_angles_max[0]='\0';
  str_angles_dev[0]='\0';
  str_angles_rat[0]='\0';

  str_faces_min[0]='\0';
  str_faces_ave[0]='\0';
  str_faces_max[0]='\0';  
  str_faces_dev[0]='\0';
  str_faces_rat[0]='\0';

  linfo = layout_info (info->sgraph);

  sprintf(str_nr_of_nodes, "%d", linfo.number_of_nodes);
  sprintf(str_nr_of_edges, "%d", linfo.number_of_edges);
  sprintf(str_size, "%d", linfo.size);

  sprintf(str_width,"%.0f",linfo.area.width);
  sprintf(str_height,"%.0f",linfo.area.height);
  sprintf(str_area_used,"%.0f",linfo.area.used);
  sprintf(str_density,"%.0f",linfo.density);

  sprintf(str_node_dist_shortest,"%.0f",linfo.node_distances.shortest);
  sprintf(str_node_dist_average,"%.0f",linfo.node_distances.average);
  sprintf(str_node_dist_longest,"%.0f",linfo.node_distances.longest);
  sprintf(str_node_dist_dev,"%.0f",sqrt(linfo.node_distances.variance));
  sprintf(str_node_dist_rat,"%.2f",linfo.node_distances.ratio);

  sprintf(str_nr_of_bends,"%d",linfo.number_of_bends);
  sprintf(str_nr_of_crossings,"%d",linfo.number_of_crossings);

  sprintf(str_edge_shortest,"%.0f",linfo.edge_lengths.shortest);
  sprintf(str_edge_average,"%.0f",linfo.edge_lengths.average);
  sprintf(str_edge_longest,"%.0f",linfo.edge_lengths.longest);
  sprintf(str_edge_dev,"%.0f",sqrt(linfo.edge_lengths.variance));
  sprintf(str_edge_rat,"%.2f",linfo.edge_lengths.ratio);

  if (linfo.number_of_crossings == 0) {

    sprintf(str_angles_min,"%.0f",linfo.angles.min/M_PI * 180.0);
    sprintf(str_angles_ave,"%.0f",linfo.angles.average/M_PI * 180.0);
    sprintf(str_angles_max,"%.0f",linfo.angles.max/M_PI * 180.0);
    sprintf(str_angles_dev,"%.0f",sqrt(linfo.angles.variance)/M_PI * 180.0);
    sprintf(str_angles_rat,"%.2f",linfo.angles.ratio);

    sprintf(str_faces_min,"%.0f",linfo.faces.min);
    sprintf(str_faces_ave,"%.0f",linfo.faces.average);
    sprintf(str_faces_max,"%.0f",linfo.faces.max);
    sprintf(str_faces_dev,"%.0f",sqrt(linfo.faces.variance));
    sprintf(str_faces_rat,"%.2f",linfo.faces.ratio);

  }

  xv_set(info_nr_of_nodes_value, PANEL_VALUE, str_nr_of_nodes, 0);  
  xv_set(info_nr_of_edges_value, PANEL_VALUE, str_nr_of_edges, 0);
  xv_set(info_size_value, PANEL_VALUE, str_size, 0);

  xv_set(info_width_value,  PANEL_VALUE, str_width, 0);
  xv_set(info_height_value, PANEL_VALUE, str_height, 0);
  xv_set(info_area_used_value, PANEL_VALUE, str_area_used, 0);
  xv_set(info_density_value, PANEL_VALUE, str_density, 0);

  xv_set(info_nr_of_bends_value,      PANEL_VALUE, str_nr_of_bends, 0);
  xv_set(info_nr_of_crossings_value,  PANEL_VALUE, str_nr_of_crossings, 0);

  xv_set(info_node_distances_value_min, PANEL_VALUE, str_node_dist_shortest,0);
  xv_set(info_node_distances_value_ave, PANEL_VALUE, str_node_dist_average, 0);
  xv_set(info_node_distances_value_max, PANEL_VALUE, str_node_dist_longest, 0);
  xv_set(info_node_distances_value_dev, PANEL_VALUE, str_node_dist_dev, 0);
  xv_set(info_node_distances_value_rat, PANEL_VALUE, str_node_dist_rat, 0);

  xv_set(info_edge_lengths_value_min, PANEL_VALUE, str_edge_shortest, 0);
  xv_set(info_edge_lengths_value_ave, PANEL_VALUE, str_edge_average, 0);
  xv_set(info_edge_lengths_value_max, PANEL_VALUE, str_edge_longest, 0);
  xv_set(info_edge_lengths_value_dev, PANEL_VALUE, str_edge_dev, 0);
  xv_set(info_edge_lengths_value_rat, PANEL_VALUE, str_edge_rat, 0);

  xv_set(info_angles_value_min, PANEL_VALUE, str_angles_min, 0);
  xv_set(info_angles_value_ave, PANEL_VALUE, str_angles_ave, 0);
  xv_set(info_angles_value_max, PANEL_VALUE, str_angles_max, 0);
  xv_set(info_angles_value_dev, PANEL_VALUE, str_angles_dev, 0);
  xv_set(info_angles_value_rat, PANEL_VALUE, str_angles_rat, 0);

  xv_set(info_faces_value_min, PANEL_VALUE, str_faces_min, 0);
  xv_set(info_faces_value_ave, PANEL_VALUE, str_faces_ave, 0);
  xv_set(info_faces_value_max, PANEL_VALUE, str_faces_max, 0);
  xv_set(info_faces_value_dev, PANEL_VALUE, str_faces_dev, 0);
  xv_set(info_faces_value_rat, PANEL_VALUE, str_faces_rat, 0);
}


void show_info_subframe(void)
{
  if(!showing_info_subframe())
    create_info_subframe();

  compute_subwindow_position_at_graph_of_current_selection(info_sf->frame);
  xv_set(info_sf->frame,
	 WIN_SHOW,TRUE,
	 0);
  info_sf->showing=TRUE;
  call_sgraph_proc (call_info_sf_do, NULL);
}



static void info_sf_set(Panel_item item, Event *event)
{
  save_info_settings();
  call_sgraph_proc (call_info_sf_do, NULL);
}


static void info_sf_done(Panel_item item, Event *event)
{
  save_info_settings();
  info_sf->showing=FALSE;
}


void save_info_settings(void)
{
  if(showing_info_subframe()) {
    ;
  }
}


void menu_info_show_subframe(Menu menu, Menu menu_item)
{
  save_info_settings();
  show_info_subframe();
}
