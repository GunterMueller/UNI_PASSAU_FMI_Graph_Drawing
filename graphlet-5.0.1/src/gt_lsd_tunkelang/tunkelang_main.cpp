/* This software is distributed under the Lesser General Public License */
#include "glob_var_for_algo.h"
#include "help_for_algo.h"
#include "create_animation_files.h"
#include "menu_functions.h"
#include "bfs.h"
#include "graphed/error.h"

int fine_tuning_at_node(int node_number, int l)
{
    int i,j;

    int fine_tuning_success=0;
    int weiter=1;


    int best_x=real_x_coord[node_number];
    int best_y=real_y_coord[node_number];

    int first_x=real_x_coord[node_number];
    int first_y=real_y_coord[node_number];

    int old_x;
    int old_y;

    int cuts_of_node;
    float best_cuts_of_node;

    float cost_of_node;
    float best_cost_of_node;

    struct nachfolger *pointer;

    cost_of_node=compute_cost(node_number,&cuts_of_node);

    cut_number=cut_number-cuts_of_node;

    best_cost_of_node=cost_of_node;
    best_cuts_of_node=cuts_of_node;



    while(weiter)
    {
	weiter=0;
	iterations++;

	if(quit_the_algorithm) 
	{
	    real_x_coord[node_number]=best_x;
	    real_y_coord[node_number]=best_y;
	    return(0);
	}

	old_x=best_x;
	old_y=best_y;

	for(i=-1;i<=1;i++)
	{
	    for(j=-1;j<=1;j++)
	    {
		if(!((i==0)&&(j==0)))
		{
		    real_x_coord[node_number]=old_x+i;
		    real_y_coord[node_number]=old_y+j;
 
		    if(
			(real_x_coord[node_number]>=0)&&(real_x_coord[node_number]<=virtual_x_size)&&
			(real_y_coord[node_number]>=0)&&(real_y_coord[node_number]<=virtual_y_size)
			)
		    {
			cost_of_node=compute_cost(node_number,&cuts_of_node);
			if(cost_of_node<best_cost_of_node)
			{
			    best_cost_of_node=cost_of_node;
			    best_cuts_of_node=cuts_of_node;

			    best_x=real_x_coord[node_number];
			    best_y=real_y_coord[node_number];

			    fine_tuning_success=1;
			    weiter=1;
			    if(animation) save_image(number_of_image_to_save++);
			}
		    }
		}
	    }
	}


	for(i=-1;i<=1;i++)
	{
	    for(j=-1;j<=1;j++)
	    {
		if(!((i==0)&&(j==0)))
		{
		    real_x_coord[node_number]=old_x+l*i;
		    real_y_coord[node_number]=old_y+l*j;

		    if(
			(real_x_coord[node_number]>=0)&&(real_x_coord[node_number]<=virtual_x_size)&&
			(real_y_coord[node_number]>=0)&&(real_y_coord[node_number]<=virtual_y_size)
			)
		    {
			cost_of_node=compute_cost(node_number,&cuts_of_node);
			if(cost_of_node<best_cost_of_node)
			{
			    best_cost_of_node=cost_of_node;
			    best_cuts_of_node=cuts_of_node;

			    best_x=real_x_coord[node_number];
			    best_y=real_y_coord[node_number];

			    fine_tuning_success=1;
			    weiter=1;
			    if(animation) save_image(number_of_image_to_save++);
			}
		    }
		}
	    }
	}
    }

    real_x_coord[node_number]=best_x;
    real_y_coord[node_number]=best_y;

    cut_number=cut_number+(int)best_cuts_of_node;


    if( (count_cuts || (cuts_at_end&&end_fine_tuning)) && fine_tuning_success )
    {
	pointer=actual_adjacency_list[node_number];
	while(pointer!=NULL)
	{
	    update_hash_table_for_edge(node_number,pointer->nummer,first_x,first_y,best_x,best_y,width_of_cell);
	    pointer=pointer->next;
	} 
    }

    return(fine_tuning_success);
}






void fine_tuning_at_neighbors_of_node(int node_number,int l, int depth)
{
    struct nachfolger *pointer;

    if(quit_the_algorithm) return;

    if(depth!=0)
    {
	pointer=actual_adjacency_list[node_number];

	while(pointer!=NULL)
	{
	    if(fine_tuning_at_node(pointer->nummer,l)) fine_tuning_at_neighbors_of_node(pointer->nummer,l,depth-1);   
	    pointer=pointer->next;
	}
    }
    /* else prin tf("At maximum depth in recursion.\n"); */
}

        



void place_node(int node_number, int st_node, int l)
{
    float best_cost = 0.0;
    float new_cost=0.0;
    int   cuts,best_cuts;

    int   best_x=0;
    int   best_y=0;
    int   old_best_x,old_best_y;
    float decrement_x,decrement_y;

    struct nachfolger *pointer;

    int bary_center_x      =0;
    int bary_center_y      =0;
    int placed_neighbors   =0;

    int beginning    =1;
    int corner_update=0;
    int ticks        =30;
    int corner_update_recursion_depth;
    int corner_update_big_steps;      
    int i,j;

    if(st_node==1)
    {
	real_x_coord[node_number]=virtual_x_size/2;
	real_y_coord[node_number]=virtual_y_size/2;
	if(animation) save_image(number_of_image_to_save++);
	return;
    }

    else
    {
	update_structures(node_number);

	pointer=actual_adjacency_list[node_number];

	while(pointer!=NULL)
	{
	    if(node_is_already_placed[pointer->nummer])
	    {
		placed_neighbors++;
		bary_center_x=bary_center_x+real_x_coord[pointer->nummer];
		bary_center_y=bary_center_y+real_y_coord[pointer->nummer];

		for(i=-1;i<=1;i++)
		{
		    for(j=-1;j<=1;j++)
		    {
			if(!( (i==0)&&(j==0) ))
			{
			    real_x_coord[node_number]=i+real_x_coord[pointer->nummer];
			    real_y_coord[node_number]=j+real_y_coord[pointer->nummer];

			    if(
				(real_x_coord[node_number]>=0)&&(real_x_coord[node_number]<=virtual_x_size)&&
				(real_y_coord[node_number]>=0)&&(real_y_coord[node_number]<=virtual_y_size)
				)
			    {
				if(beginning)
				{
				    beginning=0;
				    best_cost=compute_cost(node_number,&best_cuts);
				    best_x=real_x_coord[node_number];
				    best_y=real_y_coord[node_number];
				}

				else
				{
				    new_cost=compute_cost(node_number,&cuts);
				    if(new_cost<best_cost)
				    { 
					best_cost=new_cost;
					best_x=real_x_coord[node_number];
					best_y=real_y_coord[node_number];
					best_cuts=cuts;
				    }
				}
			    }
			}
		    }
		}


		for(i=-1;i<=1;i++)
		{
		    for(j=-1;j<=1;j++)
		    {
			if(!( (i==0)&&(j==0) ))
			{
			    real_x_coord[node_number]=i*l+real_x_coord[pointer->nummer];
			    real_y_coord[node_number]=j*l+real_y_coord[pointer->nummer];

			    if(
				(real_x_coord[node_number]>=0)&&(real_x_coord[node_number]<=virtual_x_size)&&
				(real_y_coord[node_number]>=0)&&(real_y_coord[node_number]<=virtual_y_size)
				)
			    {
				new_cost=compute_cost(node_number,&cuts);
				if(new_cost<best_cost)
				{
				    best_cost=new_cost;
				    best_x=real_x_coord[node_number];
				    best_y=real_y_coord[node_number];
				    best_cuts=cuts;
				}
			    }
			}
		    }
		}
	    } 
	    pointer=pointer->next;
	}
    }


    bary_center_x=bary_center_x/placed_neighbors;
    bary_center_y=bary_center_y/placed_neighbors;

    if(placed_neighbors>1)
    {
	for(i=-1;i<=1;i++)
	{
	    for(j=-1;j<=1;j++)
	    {
		real_x_coord[node_number]=bary_center_x+i;
		real_y_coord[node_number]=bary_center_y+j;


		if(
		    (real_x_coord[node_number]>=0)&&(real_x_coord[node_number]<=virtual_x_size)&&
		    (real_y_coord[node_number]>=0)&&(real_y_coord[node_number]<=virtual_y_size)
		    )
		{
		    new_cost=compute_cost(node_number,&cuts);
		    if(new_cost<best_cost)
		    { 
			best_cost=new_cost;
			best_x=real_x_coord[node_number];
			best_y=real_y_coord[node_number];
			best_cuts=cuts;
		    }
		}
	    }
	}

	for(i=-1;i<=1;i++)
	{
	    for(j=-1;j<=1;j++)
	    {
		if( !((i==0)&&(j==0)) )
		{
		    real_x_coord[node_number]=bary_center_x+l*i;
		    real_y_coord[node_number]=bary_center_y+l*j;
	   

		    if(
			(real_x_coord[node_number]>=0)&&(real_x_coord[node_number]<=virtual_x_size)&&
			(real_y_coord[node_number]>=0)&&(real_y_coord[node_number]<=virtual_y_size)
			)
		    {
			new_cost=compute_cost(node_number,&cuts);
			if(new_cost<best_cost)
			{
			    best_cost=new_cost;
			    best_x=real_x_coord[node_number];
			    best_y=real_y_coord[node_number];
			    best_cuts=cuts;
			}
		    }
		}
	    }
	}
    }

    if(count_cuts&&scan_corners&&(best_cuts>=1))
    {
	for(i=0;i<=2;i++)
	{
	    for(j=0;j<=2;j++)
	    {
		real_x_coord[node_number]=(i*virtual_x_size)/2;
		real_y_coord[node_number]=(j*virtual_y_size)/2;

		/* prin tf("Ecken-Koord: (%d/%d)\n",real_x_coord[node_number],real_y_coord[node_number]); */

		new_cost=compute_cost(node_number,&cuts);
		if(new_cost<best_cost)
		{
		    best_cost=new_cost;
		    best_x=real_x_coord[node_number];
		    best_y=real_y_coord[node_number];
		    best_cuts=cuts;
		    corner_update=1;
		    /*
		      pri ntf("Ecken-success1: (%d/%d)\n",best_x,best_y); 
		      pri ntf("Cut_Number= %d\n",best_cuts);
		      */
		}
	    }
	}
  
	if(corner_update)
	{
	    /* pri ntf("best_x %d best_y %d bc_x %d, bc_y %d\n",best_x,best_y,bary_center_x,bary_center_y); */
	    decrement_x=(best_x-bary_center_x)/(float)ticks;
	    decrement_y=(best_y-bary_center_y)/(float)ticks;

	    old_best_x=best_x;
	    old_best_y=best_y;
   
	    for(i=1;i<=ticks;i++)
	    {
		real_x_coord[node_number]=(int)(old_best_x-i*decrement_x);
		real_y_coord[node_number]=(int)(old_best_y-i*decrement_y);
     
		/* prin tf("Ecken2: (%d/%d)\n",real_x_coord[node_number],real_y_coord[node_number]); */

		new_cost=compute_cost(node_number,&cuts);

		/* pri ntf("Cut_Number: %d\n",cuts); */

		if(new_cost<=best_cost) 
		{
		    best_cost=new_cost;     
		    best_x=real_x_coord[node_number];
		    best_y=real_y_coord[node_number];
		    best_cuts=cuts;
		}
	    } 
	    /* pri ntf("Corner-scan successful. Coord: (%d,%d), barycenter is (%d,%d)\n",best_x,best_y,bary_center_x,bary_center_y);  */
	}
    } 
  
 
    real_x_coord[node_number]=best_x;
    real_y_coord[node_number]=best_y;

    cut_number=cut_number+best_cuts;

    if(count_cuts)
    {
	pointer=actual_adjacency_list[node_number];
	while(pointer!=NULL)
	{
	    insert_edge_into_hash_table(node_number,pointer->nummer,width_of_cell);
	    pointer=pointer->next;
	}
    }              

    if(animation) save_image(number_of_image_to_save++);

    /* if the corner-update was successful then allow higher recursion depth and bigger step:    */
    /* generally the edges from the placed node can be very long and we need more optimization   */
    if(corner_update) 
    {
	corner_update_recursion_depth=minimum(1000,3*recursion_depth);
	corner_update_big_steps      =minimum(50,3*l);

	fine_tuning_at_node(node_number,corner_update_big_steps);
	fine_tuning_at_neighbors_of_node(node_number,corner_update_big_steps,corner_update_recursion_depth); 
    }
                 
}


void run_tunkelang(void)
{
    int    min_max_dist=10000;
    int    max_dist_from_i;
    int    i,j;
    int    fine_tuning_success;
    int    actual_center = 0; // ??? MH, 6/29/96
    struct kante *pointer;

    for(i=1;i<=nodes;i++)
    {
	/*spri ntf(message_string,"STATUS: I/III  %d/%d",i,nodes);*/
        /* display_the_message(message_string); */
	/*    message(message_string);*/

	max_dist_from_i=bfs(i,0);
	if(max_dist_from_i<=min_max_dist)
	{
	    if(
		(i==1) ||
		(max_dist_from_i<min_max_dist) ||
		(degree[i]>degree[actual_center])
		)
	    {
		min_max_dist=max_dist_from_i;
		actual_center=i;
	    }
	}
        if(quit_the_algorithm) return;
    }
    bfs(actual_center,1-(unsigned short)randomize);


    end_fine_tuning=0;
    for(i=1;i<=nodes;i++)
    {
	/* spri ntf(message_string,"STATUS: II/III  %d/%d",i,nodes);*/
        /* display_the_message(message_string); */
	/*  message(message_string);*/

	node_is_already_placed[number_of_node_with_rank[i]]=1;
	placed_nodes++;
	place_node(number_of_node_with_rank[i],i,l);

	if(i>1) 
        {
	    fine_tuning_at_node(number_of_node_with_rank[i],l);
	    fine_tuning_at_neighbors_of_node(number_of_node_with_rank[i],l,recursion_depth);
        }

	/*if(count_cuts) prin tf("Number of cuts is: %d\n",cut_number);*/

        if(quit_the_algorithm) return;
    }
    /*if(animation) pri ntf("Fine-tuning begins with image %d\n",number_of_image_to_save);*/

    /* We need the filled hash-table if cuts are counted during end_fine_tuning only */
    if(cuts_at_end)
    {
	pointer=edgelist;
	while(pointer!=NULL)
	{
	    insert_edge_into_hash_table(pointer->von,pointer->nach,width_of_cell);
	    pointer=pointer->next;
	}
    }

    end_fine_tuning=1;
    for(j=1;j<=nodes;j++)
    {
	/*spri ntf(message_string,"STATUS: III/III  %d/%d",j,nodes);*/
	/* display_the_message(message_string); */
	/*message(message_string);*/

	i=number_of_node_with_rank[j];

	fine_tuning_success=fine_tuning_at_node(i,l);

	if(fine_tuning_success) fine_tuning_at_neighbors_of_node(i,l,recursion_depth);
  
	/*if(count_cuts) pri ntf("Number of cuts is: %d\n",cut_number);*/

	if(quit_the_algorithm) return;
    }

    /* if(count_cuts) pri ntf("\nNumber of cuts is: %d\n",cut_number);*/
 
    /* show_new_struct();                  */
    /* save_real_graphcoords("graph.bst"); */

    /*pri ntf("Fine-tuning-Iterations: %d\n",iterations*16);*/
}
