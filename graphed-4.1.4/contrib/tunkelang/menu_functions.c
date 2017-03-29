/* ------------------------------------------------------------------ */
/* Part responsible for creating the Tunkelang-menu-window            */
/* ------------------------------------------------------------------ */

#include <xview/xview.h>
#include <xview/frame.h>
#include <xview/panel.h>
#include <xview/window.h>

#include <sys/types.h>
#include <sys/times.h>

#include "glob_var_for_algo.h"
#include "help_for_algo.h"
#include "tunkelang_main.h"
#include "create_animation_files.h"
#include "sgraph_pre_and_post_processing.h"
#include "help_general.h"
#include <graphed/load.h>
#include <sgraph/graphed.h>

/* Globally needed variables in this module ... */
#define frame_x_width 515
#define frame_y_width 240

Frame frame;

Panel panel;

Panel_item edge_length_panel_item;
Panel_item crossings_panel_item;
Panel_item scan_corners_panel_item;
Panel_item animation_panel_item;
Panel_item image_slider_panel_item;
Panel_item randomization_panel_item;
Panel_item recursion_depth_panel_item;
Panel_item message_panel_item;
Panel_item desired_minimum_edge_length_panel_item;

Display    *display;

bool frame_active=FALSE;

int animation_buffer_on=0;

Notify_func old_ctrl_c_fct;
/* --------------------------------------------- */


/* ********************************************* */
/*  Update the Tunkelang-Window by displaying    */
/*  the given message                            */
/* ********************************************* */
void display_the_message(char *string)
{
 xv_set(
	message_panel_item,
	PANEL_LABEL_BOLD,TRUE,
	PANEL_LABEL_STRING,string,
	NULL
       );
 XFlush(display);
}

/* ********************************************* */
/* This procedure is called from run after       */
/* reading the needed parameters from the        */
/* Tunkelang-Window (run is called after the     */
/* run-button has been pressed)                  */
/* ********************************************* */
void tunkelang(Sgraph_proc_info info)
{ 
struct tms *buffer; 
int    STARTING_TIME_ALGO;
int    FINISHING_TIME_ALGO;
Sgraph sgraph;
 sgraph=info->sgraph;

 if((sgraph==empty_sgraph)||(sgraph->nodes==(Snode)NULL)) 
 {
  error("\nAn empty graph does not make sense!\n"); 
  return;
 }
 printf("-------------------------------------------------------------------------------\n");
 message("\nAll graphs will internally be connected.\nThis will have effects on the result.\nInternal edges are therefore shown in the animation.\n");

 if((!animation_buffer_on)&&(animation))
 { 
  drawing_area=info->new_buffer=create_buffer();
  animation_buffer_on=1;
  message("\nThe new window is for animation-purposes.\nThe animation will run in the LEFT UPPER CORNER!\n");
  bell();
 }

 if(animation) xv_set(image_slider_panel_item,PANEL_VALUE,1,NULL);

 renumber_the_nodes_starting_at_one(sgraph);
 make_straight_line_edges(sgraph);

 build_internal_data_structure(sgraph); 

 buffer=(struct tms*)malloc(sizeof(struct tms));
 if(!buffer)                                
  no_memory=1;

 if(!no_memory)
 {
  times(buffer);
  STARTING_TIME_ALGO = buffer->tms_utime;    

  run_tunkelang();    

  times(buffer);
  FINISHING_TIME_ALGO = buffer->tms_utime;    
  printf("CPU - TIME USED FOR ALGORITHM ALONE (WITH IMAGE CREATION):  %f s\n",(float)(FINISHING_TIME_ALGO-STARTING_TIME_ALGO)/60.0); 
 }
 else 
 {
  display_the_message("STATUS: not enough memory!");
  bell();
  quit_the_algorithm=1;
 }

 if(!quit_the_algorithm) 
 {
  update_coords_of(sgraph); 

/*IF YOU WANT TO SEE THE ORDER IN WHICH THE NODES HAVE BEEN PLACED IN THE FINAL LAYOUT,
  THEN INCLUDE THE FOLLOWING LINES (NOTE: IN THE ANIMATION YOU WILL SEE THE ORDER ANYWAY):
  for_all_nodes(sgraph,node)
  {
   string=(char *) malloc(10);
   sprintf(string,"%d",rank_of_node[node->nr]); 
   set_nodelabel(node,string);
  } end_for_all_nodes(sgraph,node);
*/
  display_the_message("STATUS: algorithm terminated");
 } 
 else if(!no_memory)
 {
  save_image(0);
  display_the_message("STATUS: aborted, current graph saved in image-directory as 0.img");
 }
 
 if(not_all_images_saved) error("\nNot all images could be saved.\n");
  
 
 free(buffer);
 free_my_own_structures(); 

 info->no_changes           =FALSE;
 info->no_structure_changes =TRUE;
 info->repaint              =TRUE;
 info->recenter             =TRUE;
 info->recompute            =TRUE;
 info->new_selected         =SGRAPH_SELECTED_SAME;
 quit_the_algorithm         =0;

 printf("-------------------------------------------------------------------------------\n");
}


/* ********************************************* */
/* Reset normal CTRL-C-mode and destroy the      */
/* window                                        */
/* ********************************************* */
void quit(Panel panel, Panel_item panel_item)
{
 notify_set_signal_func(frame,old_ctrl_c_fct,SIGINT,NOTIFY_ASYNC);
 frame_active=FALSE;
 xv_destroy_safe(frame);
}

/* ********************************************* */
/* Function is called when the image-slider is   */
/* set by hand to load an animation-image        */
/* ********************************************* */  
void get_desired_image(Panel_item item, int value, Event *event)
{
FILE *file_ptr;
int  number_of_images;
int  image_to_show;
char *imagenumber=(char *)malloc(4);
char name_with_path[80];
 
 if(animation_buffer_on)
 {
  strcpy(name_with_path,image_directory);
  strcat(name_with_path,"image_info");
 
  file_ptr=fopen(name_with_path,"r");
  if(file_ptr!=NULL) 
  { 
   fscanf(file_ptr,"%d",&number_of_images); 
   fclose(file_ptr); 
  }
  else   
  {
   display_the_message("STATUS: image_info-file not available!"); 
   stepwise_image=image_to_show;
   bell();
   return;
  }

  image_to_show=maximum(1,a_to_b_like_x_to_d(value,xv_get(image_slider_panel_item,PANEL_MAX_VALUE),number_of_images));

  sprintf(imagenumber,"%d",image_to_show);
  strcat(imagenumber,".img");
  strcpy(name_with_path,image_directory);
  strcat(name_with_path,imagenumber);

  file_ptr=fopen(name_with_path,"r");
  if(!file_ptr) 
  { 
   display_the_message("STATUS: image not available!"); 
   bell();
   stepwise_image=image_to_show;
   return; 
  }
  else 
  {
   load_from_file(drawing_area,name_with_path,LOAD_ANY_FILE);
   fclose(file_ptr); 
   stepwise_image=image_to_show;
  }
 }
 else 
 {
  display_the_message("STATUS: first save an animation!");
  xv_set(image_slider_panel_item,PANEL_VALUE,1,NULL);
 }
}


/* ********************************************* */
/* This procedure plays the animation-file       */
/* that is it loads one graph after the other    */
/* into  the animation-window                    */
/* ********************************************* */
void animate_layout(Panel panel, Panel_item panel_item)
{
int number_of_images,i,slider_value;
char *imagenumber=(char *)malloc(6);
char name_with_path[80];
FILE *file_ptr;
int an_error=0;
int image_to_show;

 if(animation_buffer_on)
 {
  strcpy(name_with_path,image_directory);
  strcat(name_with_path,"image_info");

  file_ptr=fopen(name_with_path,"r");
  if(!file_ptr) an_error=1;
  else 
  {
   fscanf(file_ptr,"%d",&number_of_images); 
   fclose(file_ptr);
  }


  if(!an_error)
  {
   image_to_show=maximum(1,stepwise_image);
   display_the_message("STATUS: running animation");
   for(i=image_to_show;i<=number_of_images;i++)
   {      
    strcpy(name_with_path,image_directory);
    sprintf(imagenumber,"%d",i);
    strcat(imagenumber,".img");
    strcat(name_with_path,imagenumber);

    file_ptr=fopen(name_with_path,"r");

    if(file_ptr)
    {
     fclose(file_ptr);
     load_from_file(drawing_area,name_with_path,LOAD_ANY_FILE);
    }
    else
    {
     printf("Image %s not available!\n",name_with_path);
     display_the_message("STATUS: next image not available!");
     bell();
     stepwise_image=i-1;
     return;
    }
    

    if(i%10==0)
    {
     slider_value=a_to_b_like_x_to_d(i,number_of_images,xv_get(image_slider_panel_item,PANEL_MAX_VALUE)); 
     xv_set(image_slider_panel_item,PANEL_VALUE,slider_value,NULL);
    }

    if(quit_the_algorithm) break;
   }
   stepwise_image=i;

   if(i==number_of_images) xv_set(image_slider_panel_item,PANEL_VALUE,PANEL_MAX_VALUE,NULL);
   else                    
   {
    slider_value=a_to_b_like_x_to_d(i,number_of_images,xv_get(image_slider_panel_item,PANEL_MAX_VALUE)); 
    xv_set(image_slider_panel_item,PANEL_VALUE,slider_value,NULL);
   } 
  
   if(!quit_the_algorithm) display_the_message("STATUS: animation terminated");
   else
   {
    display_the_message("STATUS: animation aborted");
    quit_the_algorithm=0;
   }

  }

  else 
  {
   display_the_message("STATUS: image_info-file not available!");
   bell();
  }
 }

 else display_the_message("STATUS: first save an animation!");
}
  
/* ********************************************* */
/* Load the next image if it is available        */
/* ********************************************* */
void stepwise_forward(Panel panel, Panel_item panel_item)
{
char *imagenumber=(char *)malloc(6);
FILE *file_ptr;
int number_of_images;
int slider_value;
char name_with_path[80];

 if(animation_buffer_on)
 {
   stepwise_image++;

   strcpy(name_with_path,image_directory);
   strcat(name_with_path,"image_info");

   file_ptr=fopen(name_with_path,"r");
   if(!file_ptr)
   {
    display_the_message("STATUS: image_info-file not available!");
    bell();
    return;
   }
   fscanf(file_ptr,"%d",&number_of_images);
   fclose(file_ptr);

   strcpy(name_with_path,image_directory);
   sprintf(imagenumber,"%d",stepwise_image);
   strcat(imagenumber,".img");
   strcat(name_with_path,imagenumber);

   file_ptr=fopen(name_with_path,"r");
   if((!file_ptr)||(stepwise_image>number_of_images)) 
   {
    display_the_message("STATUS: image is not available!");
    bell();
    stepwise_image--;
   }
   else 
   {
    load_from_file(drawing_area,name_with_path,LOAD_ANY_FILE);
    fclose(file_ptr);
    slider_value=a_to_b_like_x_to_d(stepwise_image,number_of_images,xv_get(image_slider_panel_item,PANEL_MAX_VALUE));
    xv_set(image_slider_panel_item,PANEL_VALUE,slider_value,NULL);
   }
 }
 else display_the_message("STATUS: first save an animation!");
}


/* ********************************************* */
/* Load the previous image if it is available    */
/* ********************************************* */
void stepwise_backward(Panel panel, Panel_item panel_item)
{
char *imagenumber=(char *)malloc(6);
FILE *file_ptr;
int number_of_images;
int slider_value;
char name_with_path[80];

 if(animation_buffer_on)
 {   
   stepwise_image--;

   strcpy(name_with_path,image_directory);
   strcat(name_with_path,"image_info"); 

   file_ptr=fopen(name_with_path,"r");
   if(!file_ptr)
   {
    display_the_message("STATUS: image_info-file not available!");
    bell();
    return;
   }
   fscanf(file_ptr,"%d",&number_of_images);
   fclose(file_ptr);

   strcpy(name_with_path,image_directory);
   sprintf(imagenumber,"%d",stepwise_image);
   strcat(imagenumber,".img");
   strcat(name_with_path,imagenumber);

   file_ptr=fopen(name_with_path,"r");
   if((!file_ptr)||(stepwise_image<1)) 
   {
    display_the_message("STATUS: image is not available!");
    bell();
    stepwise_image++;
   }
   else 
   {
    load_from_file(drawing_area,name_with_path,LOAD_ANY_FILE);
    fclose(file_ptr);
    if (stepwise_image==1) slider_value=1;
    else    
     slider_value=a_to_b_like_x_to_d(stepwise_image,number_of_images,xv_get(image_slider_panel_item,PANEL_MAX_VALUE));
    xv_set(image_slider_panel_item,PANEL_VALUE,slider_value,NULL);
   }
 }
 else display_the_message("STATUS: first save an animation!");
}


/* ********************************************* */
/* Get the parameters from the window and call   */
/* tunkelang                                     */
/* ********************************************* */
void run(Panel panel, Panel_item panel_item)
{
 unsigned check_box_cut_value; 
 unsigned check_box_animation_value;
 unsigned check_box_randomization_value;
 unsigned check_box_scan_corners_value;

 /* 
   l is the desired standard edge length.

   node_edge_weight is the weight for distances between nodes and non-incident edges.
   It is set to l^2 (heuristics!).

   the weight for edge length is set implicitely to 1.0, this is why node_node_weight, 
   the weigth for repulsive forces between nodes, is set to l^4. Then the optimum edge length will be l.

   crossing_weight is set very high, so only improvements in edge crossings are allowed.
 */
 
 l=xv_get(edge_length_panel_item,PANEL_VALUE);
 if(l<2)  l=2;

 width_of_cell=l;
/*
 node_edge_weight=node_node_weight=(float)l*(float)l;

 node_node_weight=node_node_weight*node_node_weight;
*/
 node_node_weight=(float)(l*l*l*l);

 node_edge_weight=(float)(l*l);

 crossing_weight=1.0e+15;

 recursion_depth              = xv_get(recursion_depth_panel_item,PANEL_VALUE);
 minimum_edge_length          = xv_get(desired_minimum_edge_length_panel_item,PANEL_VALUE);

 check_box_cut_value          =(unsigned)xv_get(crossings_panel_item,PANEL_VALUE);
 check_box_animation_value    =(unsigned)xv_get(animation_panel_item,PANEL_VALUE);
 check_box_randomization_value=(unsigned)xv_get(randomization_panel_item,PANEL_VALUE);
 check_box_scan_corners_value =(unsigned)xv_get(scan_corners_panel_item,PANEL_VALUE);

 switch(check_box_cut_value)
 {
  case 0: count_cuts=1; cuts_at_end=0; break;
  case 1: count_cuts=0; cuts_at_end=1; break;
  case 2: count_cuts=0; cuts_at_end=0; break;
 } 


 switch(check_box_animation_value)
 {
  case 0: animation=1; break;
  case 1: animation=0; break;
 } 

 switch(check_box_randomization_value)
 {
  case 0: randomize=1; break;
  case 1: randomize=0; break;
 } 

 switch(check_box_scan_corners_value)
 {
  case 0: scan_corners=1; break;
  case 1: scan_corners=0; break;
 } 
 
 /*
 printf("Edgelength is %d, recursion_depth is %d, minimum_edge_length is %d\n",l,recursion_depth,minimum_edge_length);
 */

 old_ctrl_c_fct=notify_set_signal_func(frame,(Notify_func)breakhandler,SIGINT,NOTIFY_ASYNC);
 call_sgraph_proc(tunkelang, NULL);
}

/* ********************************************* */
/* This is the procedure which is called when    */
/* "Tunkelangs Layout ..." is chosen in          */
/* GraphEd                                       */
/* ********************************************* */
void tunkelang_layout(Menu menu, Menu_item menu_item)
{
 if(frame_active) xv_set(frame,XV_SHOW,TRUE,FRAME_CLOSED,FALSE,NULL);
      
 else
 {
  frame_active=TRUE;


  frame=(Frame)xv_create(
                         XV_NULL,
			 FRAME,
			 XV_WIDTH, frame_x_width,
			 XV_HEIGHT,frame_y_width,
			 FRAME_LABEL,"TUNKELANG'S ALGORITHM",
			 NULL
			);

  panel=(Panel)xv_create(
			 frame,
			 PANEL,
			 NULL
			);

  edge_length_panel_item=(Panel_item)xv_create(
					       panel,PANEL_SLIDER,
                                               XV_X,10,
                                               XV_Y,10,
					       PANEL_LABEL_STRING,"Quality parameter (small = fast)",
					       PANEL_MIN_VALUE,2,
					       PANEL_VALUE,4,
					       PANEL_MAX_VALUE,20,
					       PANEL_SLIDER_WIDTH,203,
					       PANEL_SLIDER_END_BOXES,TRUE,
					       PANEL_SHOW_RANGE,FALSE,
					       PANEL_NOTIFY_LEVEL,PANEL_DONE,
					       PANEL_SHOW_VALUE,TRUE,
					       NULL
					      );

  recursion_depth_panel_item=(Panel_item)xv_create(
						   panel,PANEL_SLIDER,
						   XV_X,10,
                                                   XV_Y,30,
						   PANEL_LABEL_STRING,"Max. recursion depth (small = fast)",
						   PANEL_MIN_VALUE,5,
						   PANEL_VALUE,1000,
						   PANEL_MAX_VALUE,1000,
						   PANEL_SLIDER_WIDTH,185,
						   PANEL_SLIDER_END_BOXES,TRUE,
						   PANEL_SHOW_RANGE,FALSE,
						   PANEL_NOTIFY_LEVEL,PANEL_DONE,
						   PANEL_SHOW_VALUE,TRUE,
						   NULL
						  );

  randomization_panel_item=(Panel_item)xv_create(
						 panel,PANEL_CHECK_BOX,
					         XV_X,10,
                                                 XV_Y,50,
						 PANEL_CHOOSE_ONE,TRUE,
						 PANEL_LABEL_STRING,"Randomize adjacency lists",
						 PANEL_CHOICE_STRINGS,"YES","NO (USE STRATEGY)",NULL,
						 PANEL_VALUE,1,
						 NULL
						);

  crossings_panel_item=(Panel_item)xv_create(
					     panel,PANEL_CHECK_BOX,
					     XV_X,10,
                                             XV_Y,70,
					     PANEL_CHOOSE_ONE,TRUE,
					     PANEL_LABEL_STRING,"Crossings (slows down)",
					     PANEL_CHOICE_STRINGS,"ALWAYS","FOR END-FINE-TUNING","NEVER",NULL,
					     PANEL_VALUE,2,
					     NULL
					    );

  scan_corners_panel_item=(Panel_item)xv_create(
						 panel,PANEL_CHECK_BOX,
         					 XV_X,10,
                                                 XV_Y,90,
						 PANEL_CHOOSE_ONE,TRUE,
						 PANEL_LABEL_STRING,"Scan corners if crossings is 'always' (slow)",
						 PANEL_CHOICE_STRINGS,"YES","NO",NULL,
						 PANEL_VALUE,1,
						 NULL
						);
  

  animation_panel_item=(Panel_item)xv_create(
					     panel,PANEL_CHECK_BOX,
					     XV_X,10,
                                             XV_Y,110,
					     PANEL_CHOOSE_ONE,TRUE,
					     PANEL_LABEL_STRING,"Create animation file of next run (very slow)",
					     PANEL_CHOICE_STRINGS,"YES","NO",NULL,
					     PANEL_VALUE,1,
					     NULL
					    );

  desired_minimum_edge_length_panel_item=(Panel_item)xv_create(
					       		       panel,PANEL_SLIDER,
							       XV_X,10,
                                                               XV_Y,135,
					       		       PANEL_LABEL_STRING,"Minimum edge length (0 = auto)",
					                       PANEL_MIN_VALUE,0,
					                       PANEL_VALUE,0,
					                       PANEL_MAX_VALUE,300,
					                       PANEL_SLIDER_WIDTH,205,
					                       PANEL_SLIDER_END_BOXES,TRUE,
					                       PANEL_SHOW_RANGE,FALSE,
					                       PANEL_NOTIFY_LEVEL,PANEL_DONE,
					                       PANEL_SHOW_VALUE,TRUE,
					                       NULL
					                      ); 



  message_panel_item=(Panel_item)xv_create(
					   panel,PANEL_MESSAGE,
      					   XV_X,10,
                                           XV_Y,160,
					   PANEL_LABEL_BOLD,TRUE,
					   PANEL_LABEL_STRING,"STATUS: ALGORITHM HAS NOT BEEN RUN YET",
					   NULL
					  );

  image_slider_panel_item=(Panel_item)xv_create(
						panel,PANEL_SLIDER,
						XV_X,10,
                                                XV_Y,185,
						PANEL_LABEL_STRING,"Select image:",
						PANEL_MIN_VALUE,1,
						PANEL_VALUE,1,
						PANEL_MAX_VALUE,1000,
						PANEL_SLIDER_WIDTH,200,
						PANEL_TICKS,10,
						PANEL_SLIDER_END_BOXES,TRUE,
						PANEL_SHOW_RANGE,FALSE,
						PANEL_NOTIFY_LEVEL,PANEL_DONE,
						PANEL_SHOW_VALUE,FALSE,
						PANEL_NOTIFY_PROC,get_desired_image,
						NULL
					       );

  xv_create(
	    panel,PANEL_BUTTON,
	    XV_X,350,
            XV_Y,185,
	    PANEL_NOTIFY_PROC,animate_layout,
	    PANEL_LABEL_STRING,"play",
	    NULL
	   ); 

  xv_create(
	    panel,PANEL_BUTTON,
	    XV_X,400,
            XV_Y,185,
	    PANEL_NOTIFY_PROC,stepwise_forward,
	    PANEL_LABEL_STRING,">",
	    NULL
	   );   

  xv_create(
	    panel,PANEL_BUTTON,
            XV_X,430,
            XV_Y,185,
	    PANEL_NOTIFY_PROC,stepwise_backward,
	    PANEL_LABEL_STRING,"<",
	    NULL
	   );  

  xv_create(
	    panel,PANEL_BUTTON,
            XV_X,10,
            XV_Y,215,
	    PANEL_NOTIFY_PROC,run,
	    PANEL_LABEL_STRING,"run algorithm",
	    NULL
	   ); 
  
  xv_create(
	    panel,PANEL_BUTTON,
            XV_X,150,
            XV_Y,215,
	    PANEL_NOTIFY_PROC,quit,
	    PANEL_LABEL_STRING,"quit",
	    NULL
	   );  

  xv_set(frame,XV_SHOW,TRUE,NULL);

  window_fit(frame);

  display=(Display *)xv_get(frame,XV_DISPLAY);
 }
}

