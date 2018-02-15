#include <stdio.h>
#include <string.h>

#include "glob_var_for_algo.h"
#include "help_for_algo.h"

void print_node_attrs(FILE *file, Snode snode)
{
 fprintf(file,"{$ NP %d %d $} ",snode->x,snode->y);
}


Sgraph build_graph(void)
{
Sgraph sgraph=make_graph(make_attr(ATTR_DATA, NULL));
Snode node;
int i,j;
char *string;
struct nachfolger *pointer;

 for(i=1;i<=nodes-1;i++)
 {
  for(j=i+1;j<=nodes;j++)
  {
   adjacency_matrix_for_saved_graph[i][j]=adjacency_matrix_for_saved_graph[j][i]=0;
  }
 }
 for(i=1;i<=nodes;i++) adjacency_matrix_for_saved_graph[i][i]=0;

 sgraph->directed=FALSE;
 set_graphlabel(sgraph,"");

 zoom_the_graph(graphed_xsize,graphed_ysize,15,15,minimum_edge_length);

 /* Create the nodes         */
 for(j=1;j<=placed_nodes;j++)
 {
  i=number_of_node_with_rank[j];

  iso_node[i]=node=make_node(sgraph,make_attr(ATTR_DATA, NULL));

  node->nr=i;
  node->x=zoomed_x_coord[i];
  node->y=zoomed_y_coord[i];

  string=(char *) malloc(10);
  sprintf(string,"%d",j);
  set_nodelabel(node,string);
 }

 /* Create the edges         */
 for(j=1;j<=placed_nodes;j++)
 {
  i=number_of_node_with_rank[j];

  pointer=actual_adjacency_list[i];
  
  while(pointer!=NULL)
  {
   if(!(adjacency_matrix_for_saved_graph[i][pointer->nummer]))
   {
    adjacency_matrix_for_saved_graph[i][pointer->nummer]=adjacency_matrix_for_saved_graph[pointer->nummer][i]=1;
    set_edgelabel(make_edge(iso_node[i],iso_node[pointer->nummer],make_attr(ATTR_DATA, NULL)),"");
   }
   pointer=pointer->next;
  }

 }

 return(sgraph);
}


void save_image_info(int number)
{
FILE *file_ptr;
char name_with_path[1000];
 
 strcpy(name_with_path,image_directory);
 strcat(name_with_path,"image_info");
 
 file_ptr=fopen(name_with_path,"w");
 if(!file_ptr) 
 {
  not_all_images_saved=1;
  return;
 }
 fprintf(file_ptr,"%d\n",number);
 fclose(file_ptr);
}


void save_image(int image_number)
{
char *imagenumber=(char *)malloc(15);
Sgraph sgraph;
FILE *file_ptr;
char name_with_path[1000];

 if(animation_not_possible) return;

 strcpy(name_with_path,image_directory);
 sprintf(imagenumber,"%d",image_number);
 strcat(imagenumber,".img");
 strcat(name_with_path,imagenumber);
 
 sgraph=build_graph();

 file_ptr=fopen(name_with_path,"w");
 if(!file_ptr) 
 {
  not_all_images_saved=1;
  return;
 }
 print_graph(file_ptr,sgraph,NULL,print_node_attrs,NULL);
 fclose(file_ptr);
 if(image_number>0) save_image_info(image_number);
}

