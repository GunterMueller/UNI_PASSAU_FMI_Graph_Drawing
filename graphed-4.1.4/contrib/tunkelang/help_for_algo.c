#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <math.h>
 
#include <graphed/error.h>

#include "glob_var_for_algo.h"
#include "allocation_procs.h"
#include "help_general.h"
#include "sgraph_pre_and_post_processing.h"

void zoom_the_graph(int drawing_area_xsize, int drawing_area_ysize, int x_offset, int y_offset, int minimum_edge_length)
{
int    minx,maxx,miny,maxy;
int    xsize, ysize;
float  xfactor,yfactor,factor;
int    i,j;
float shortest_edge=1.0e+30,current_edge;
struct kante *pointer;

        maxx=minx=real_x_coord[number_of_node_with_rank[1]];

	maxy=miny=real_y_coord[number_of_node_with_rank[1]];

 	for(j=2;j<=placed_nodes;j++)
	{
          i=number_of_node_with_rank[j];

	  if(real_x_coord[i]<minx) minx=real_x_coord[i];
	  if(real_x_coord[i]>maxx) maxx=real_x_coord[i];

	  if(real_y_coord[i]<miny) miny=real_y_coord[i];
	  if(real_y_coord[i]>maxy) maxy=real_y_coord[i];
	}

	if(miny<0) ysize=-miny+maxy;
        else       ysize= maxy-miny;


	if(minx<0) xsize=-minx+maxx;
	else       xsize= maxx-minx;

	if(xsize!=0) xfactor=(float)drawing_area_xsize/(float)xsize;
	else xfactor=10000000;

	if(ysize!=0) yfactor=(float)drawing_area_ysize/(float)ysize;
	else yfactor=10000000;

        /* We use the same factor in x- as in y-direction so that there will be no distortions */
	if(xfactor<yfactor) factor=xfactor;
	else                factor=yfactor;
        

        if((minimum_edge_length>0)&&(edgelist!=NULL))
        {
         pointer=edgelist;
         while(pointer!=NULL)
         {
          
          current_edge=sqrt((double)
                                   (
                                    (real_x_coord[pointer->von]-real_x_coord[pointer->nach])*
			            (real_x_coord[pointer->von]-real_x_coord[pointer->nach])+
                                    (real_y_coord[pointer->von]-real_y_coord[pointer->nach])*
			            (real_y_coord[pointer->von]-real_y_coord[pointer->nach])
                                   )
                           );
          if(current_edge<shortest_edge) shortest_edge=current_edge;
          pointer=pointer->next;
         }
         
         factor=((float)minimum_edge_length)/shortest_edge;
        }

	for(j=1;j<=placed_nodes;j++)
	{
	 i=number_of_node_with_rank[j];

	 zoomed_x_coord[i]= (int) ((real_x_coord[i]-minx)*factor)+x_offset;
	 zoomed_y_coord[i]= (int) ((real_y_coord[i]-miny)*factor)+y_offset;
	}
}

Local float compute_node_arc(int n, int i, int j)
{
float xn,yn,xi,yi,xj,yj,projx,projy,abst_vektorx,abst_vektory;
float buffer,a,d_square;
float d_min_square=((float)(l*l))/(4.0*4.0); /* The minimum edge-node distance should be l/4. */

  xn=real_x_coord[n];
  yn=real_y_coord[n];
  
  xi=real_x_coord[i];
  yi=real_y_coord[i];

  xj=real_x_coord[j];
  yj=real_y_coord[j];


 /*
   Fall 1: die punkte i und j sind gleich => abstand von n 
   zur geraden (im quadrat)=abstand von n zu i bzw. j (im quadrat)
 */
 if(xi==xj && yi==yj) d_square=(xi-xn)*(xi-xn)+(yi-yn)*(yi-yn);


 else
 {
  /*
   Fall 2: i und j sind nicht gleich, aber die gerade durch
   i und j ist senkrecht => wir drehen alle punkte um 90 grad und machen
   unten weiter. der abstand hat sich dadurch nicht veraendert.
   (x,y) ---> (-y,x)
  */
  if(xi==xj)
  {
   buffer=xn;
   xn=-yn;
   yn=buffer;

   buffer=xi;
   xi=-yi;
   yi=buffer;
   buffer=xj;
   xj=-yj;
   yj=buffer;
  }
  /* 
    Allgemeiner Fall: (falls i!=j und gerade durch i und j nicht senkrecht)
    Idee: zunaechst translation, so dass i der ursprung
    dann: sei b der vektor vom ursprung zu j (in dieser richtung!)
          sei c der vektor vom ursprung zu n (in dieser richtung!)
          projeziere c auf b (es gilt: proj(c)=(<c,b>/<b,b>))*b)
          der faktor <c,b>/<b,b> werde mit a abgekuerzt.
          falls a<0, so gilt: dist(n,gerade)=dist(n,i)
          falls a>1, so gilt: dist(n,gerade)=dist(n,j)
          sonst (0<= a <=1):  dist(n,gerade)=||c-proj(c)||
          in allen faellen nehmen wir das abstandsquadrat.                   
  */
  xn=xn-xi; /* vektor c */
  yn=yn-yi;

  xj=xj-xi; /* vektor b */
  yj=yj-yi;

  a=(xn*xj+yn*yj)/(xj*xj+yj*yj);


  if(a<0) d_square=xn*xn+yn*yn; /* Beachte: i=(0,0) */
  else
  {
   if(a>1) d_square=(xj-xn)*(xj-xn)+(yj-yn)*(yj-yn);
   else
   {
    projx=a*xj;
    projy=a*yj;

    abst_vektorx=xn-projx;
    abst_vektory=yn-projy;
    d_square    =abst_vektorx*abst_vektorx+abst_vektory*abst_vektory;
   }
  }
 }

 if(d_square<d_min_square) return(1.0e-03); 
 /* Return a very low value, so the costs will be very high for such a low distance */

 else return(d_square);
}


Local int compute_cut(int i, int j, int k, int l)
{
int det1,det2,sgn;
 
 /* If a node belongs to both edges, there is no cut per definition*/
 if((i==k)||(i==l)||(j==k)||(j==l)) return(0);

 det1=det(real_x_coord[i],real_y_coord[i],real_x_coord[j],real_y_coord[j],real_x_coord[k],real_y_coord[k]);
 det2=det(real_x_coord[i],real_y_coord[i],real_x_coord[j],real_y_coord[j],real_x_coord[l],real_y_coord[l]);

 sgn=signum(det1)*signum(det2);

 if(sgn==-1)
 {
  det1=det(real_x_coord[k],real_y_coord[k],real_x_coord[l],real_y_coord[l],real_x_coord[i],real_y_coord[i]);
  det2=det(real_x_coord[k],real_y_coord[k],real_x_coord[l],real_y_coord[l],real_x_coord[j],real_y_coord[j]);
  sgn=signum(det1)*signum(det2);

  if(sgn==1) return(0);
  else       return(1);
 }
 else
 {
  if(sgn==1) return(0);
  else     /* sgn==0 */
  {
   if(det1+det2==0)   /* all four points on one line */
   {
    if(
        maximum(real_x_coord[i],real_x_coord[j])<minimum(real_x_coord[k],real_x_coord[l]) ||
        minimum(real_x_coord[i],real_x_coord[j])>maximum(real_x_coord[k],real_x_coord[l]) ||
        maximum(real_y_coord[i],real_y_coord[j])<minimum(real_y_coord[k],real_y_coord[l]) ||
        minimum(real_y_coord[i],real_y_coord[j])>maximum(real_y_coord[k],real_y_coord[l]) 
      )
         return(0);   /* no overlap */
    else return(1);   /* overlap    */
   }
   else               /* exactly 3 points on one line */
   {
    det1=det(real_x_coord[k],real_y_coord[k],real_x_coord[l],real_y_coord[l],real_x_coord[i],real_y_coord[i]);
    det2=det(real_x_coord[k],real_y_coord[k],real_x_coord[l],real_y_coord[l],real_x_coord[j],real_y_coord[j]);
    sgn=signum(det1)*signum(det2);

    if(sgn==1) return(0);
    else       return(1);
   }
  }
 }
}

Local int number_of_crossings(int i, int j, int cell_width)
{
int c_x_i=real_x_coord[i]/cell_width;
int c_y_i=real_y_coord[i]/cell_width;
int c_x_j=real_x_coord[j]/cell_width;
int c_y_j=real_y_coord[j]/cell_width;


int lowest_x_cell_nr =minimum(c_x_i,c_x_j);
int highest_x_cell_nr=maximum(c_x_i,c_x_j);
int lowest_y_cell_nr =minimum(c_y_i,c_y_j);
int highest_y_cell_nr=maximum(c_y_i,c_y_j);

 
int n,m,k,l,crossings=0;
struct kante *list_elt;


 make_edge_count_unambigious++;

 for(n=lowest_x_cell_nr;n<=highest_x_cell_nr;n++)
 {
  for(m=lowest_y_cell_nr;m<=highest_y_cell_nr;m++)
  {
   list_elt=edgelist_matrix[n][m];

   while(list_elt!=NULL)
   {
    k=list_elt->von;
    l=list_elt->nach;

    if( (which_edges_have_been_counted[k][l]!=make_edge_count_unambigious) && compute_cut(i,j,k,l) )
    {
     which_edges_have_been_counted[k][l]=which_edges_have_been_counted[l][k]=make_edge_count_unambigious;
     crossings++;
    }
    list_elt=list_elt->next;
   }

  }
 }

return(crossings);
}


float compute_cost(int node_number, int *cuts)
{
struct nachfolger *edges_to_node;
struct kante      *edge;

int               i,j;
float             node_node_value=0.0;
int               edge_length_value=0;
int               x_dist,y_dist,dist;
int               number_of_cuts;
float             cut_value=0.0;
float             node_edge_value=0.0;


/* Compute the number of cuts if necessary ********************************************** */
(*cuts)=0;
if(count_cuts ||(cuts_at_end && end_fine_tuning) )
{
 edges_to_node=actual_adjacency_list[node_number]; /* For all existing edges from node node_number */
 number_of_cuts=0;

 while(edges_to_node!=NULL)
 {
  j=edges_to_node->nummer;
   
  number_of_cuts=number_of_cuts+number_of_crossings(node_number,j,width_of_cell);

  edges_to_node=edges_to_node->next;
 }
 (*cuts)=number_of_cuts;
 cut_value=crossing_weight*(*cuts);
}
/* ************************************************************************************** */

/* Compute the value for node-edge-distance  if necessary ******************************* */
if( (count_cuts || cuts_at_end) && end_fine_tuning ) 
{
 edge=edgelist;

 /* Compute the value for node node_number and the non-incident edges */
 while(edge!=NULL)
 {
  if((edge->von!=node_number)&&(edge->nach!=node_number))
  {
   node_edge_value=node_edge_value+1.0/compute_node_arc(node_number,edge->von,edge->nach);
   /* printf("Betrachte Kante %d-%d und Real_Adjacency_List %d\n",edge->von,edge->nach,node_number); */
  }
  edge=edge->next;
 }

 /* Compute the distance from the other nodes to the edges incident to node node_number */
 edges_to_node=actual_adjacency_list[node_number];
 while(edges_to_node!=NULL)
 {
  for(j=1;j<=placed_nodes;j++)
  {
   i=number_of_node_with_rank[j];
   if((i!=node_number)&&(i!=edges_to_node->nummer))
   {
    node_edge_value=node_edge_value+1.0/compute_node_arc(i,node_number,edges_to_node->nummer);
    /* printf("Betrachte Kante %d-%d und Knoten %d\n",node_number,edges_to_node->nummer,i); */
   }
  }
  edges_to_node=edges_to_node->next;
 }

 node_edge_value=node_edge_value*node_edge_weight;
}
/* ************************************************************************************** */



/* Compute the value for node-node-distance and edge-length ***************************** */
for(j=1;j<=placed_nodes;j++)
{
 i=number_of_node_with_rank[j];

 if((i!=node_number))
 {
  x_dist=real_x_coord[i]-real_x_coord[node_number];
  x_dist=x_dist*x_dist;

  y_dist=real_y_coord[i]-real_y_coord[node_number];
  y_dist=y_dist*y_dist;

  dist=x_dist+y_dist;

  if(dist==0) return(1.0e+30);

  node_node_value=node_node_value+1.0/dist;

  if(adjacency_matrix[i][node_number])
  {
   edge_length_value=edge_length_value+dist;
  }
 }
}
node_node_value=node_node_value*node_node_weight;
/* ************************************************************************************** */


return(node_node_value + edge_length_value + cut_value + node_edge_value);
}




void insert_edge_into_hash_table(int i, int j, int cell_width)
{
struct kante *e;

int n,m;

int c_x_i=real_x_coord[i]/cell_width;
int c_y_i=real_y_coord[i]/cell_width;
int c_x_j=real_x_coord[j]/cell_width;
int c_y_j=real_y_coord[j]/cell_width;


int lowest_x_cell_nr =minimum(c_x_i,c_x_j);
int highest_x_cell_nr=maximum(c_x_i,c_x_j);
int lowest_y_cell_nr =minimum(c_y_i,c_y_j);
int highest_y_cell_nr=maximum(c_y_i,c_y_j);

 for(n=lowest_x_cell_nr;n<=highest_x_cell_nr;n++)
 {
  for(m=lowest_y_cell_nr;m<=highest_y_cell_nr;m++)
  {
   /* printf("Content of cell [%d][%d]\n",n,m); print_cell(n,m); */
   e=(struct kante *)malloc(sizeof(struct kante));
   if(e==NULL) printf("Problems with memory\n");

   e->von=i;
   e->nach=j;
   e->next=edgelist_matrix[n][m];
   edgelist_matrix[n][m]=e;
   /* printf("Inserting edge %d-%d into cell [%d][%d]\n",i,j,n,m); */
  }
 }
}


void update_hash_table_for_edge(int node1, int node2, int node1_x_old, int node1_y_old, int node1_x_new, int node1_y_new, int cell_width)
{
struct kante *actual,*previous;
int n,m;

int c_x_node1_old=(node1_x_old)/cell_width;
int c_y_node1_old=(node1_y_old)/cell_width;
int c_x_node1_new=(node1_x_new)/cell_width;
int c_y_node1_new=(node1_y_new)/cell_width;

int c_x_node2=(real_x_coord[node2])/cell_width;
int c_y_node2=(real_y_coord[node2])/cell_width;

int lowest_x_cell_nr_old=minimum(c_x_node1_old,c_x_node2);
int lowest_y_cell_nr_old=minimum(c_y_node1_old,c_y_node2);

int highest_x_cell_nr_old=maximum(c_x_node1_old,c_x_node2);
int highest_y_cell_nr_old=maximum(c_y_node1_old,c_y_node2);


int lowest_x_cell_nr_new=minimum(c_x_node1_new,c_x_node2);
int lowest_y_cell_nr_new=minimum(c_y_node1_new,c_y_node2);

int highest_x_cell_nr_new=maximum(c_x_node1_new,c_x_node2);
int highest_y_cell_nr_new=maximum(c_y_node1_new,c_y_node2);

int low_x_common =maximum(lowest_x_cell_nr_old,lowest_x_cell_nr_new);
int high_x_common=minimum(highest_x_cell_nr_old,highest_x_cell_nr_new);

int low_y_common =maximum(lowest_y_cell_nr_old,lowest_y_cell_nr_new);
int high_y_common=minimum(highest_y_cell_nr_old,highest_y_cell_nr_new);

/*
int update_normal=(highest_x_cell_nr_old-lowest_x_cell_nr_old+1)*(highest_y_cell_nr_old-lowest_y_cell_nr_old+1)+
                  (highest_x_cell_nr_new-lowest_x_cell_nr_new+1)*(highest_y_cell_nr_new-lowest_y_cell_nr_new+1); 
*/

/* we have to remove the edge node1-node2 from the part of the hash-table 'old_cells-common_cells' */
for(n=lowest_x_cell_nr_old;n<=low_x_common-1;n++)
{
 for(m=lowest_y_cell_nr_old;m<=highest_y_cell_nr_old;m++)
 {
   actual=edgelist_matrix[n][m];
   /* printf("Content of cell [%d][%d]\n",n,m); print_cell(n,m); */
   while( 
          (!( (actual->von==node1)&&(actual->nach==node2) )) &&
          (!( (actual->von==node2)&&(actual->nach==node1) ))
        )
   {
     previous=actual;
     actual=actual->next;
   }
   if(actual==edgelist_matrix[n][m]) edgelist_matrix[n][m]=actual->next;
   else                            previous->next=actual->next;
   free(actual);  
 }
}

for(n=high_x_common+1;n<=highest_x_cell_nr_old;n++)
{
 for(m=lowest_y_cell_nr_old;m<=highest_y_cell_nr_old;m++)
 {
   actual=edgelist_matrix[n][m];
   /* printf("Content of cell [%d][%d]\n",n,m); print_cell(n,m); */
   while( 
          (!( (actual->von==node1)&&(actual->nach==node2) )) &&
          (!( (actual->von==node2)&&(actual->nach==node1) ))
        )
   {
     previous=actual;
     actual=actual->next;
   }
   if(actual==edgelist_matrix[n][m]) edgelist_matrix[n][m]=actual->next;
   else                            previous->next=actual->next;
   free(actual);  
 }
}

if(lowest_y_cell_nr_old<=low_y_common-1)
{
 for(n=low_x_common;n<=high_x_common;n++)
 {
  for(m=lowest_y_cell_nr_old;m<=low_y_common-1;m++)
  {
    actual=edgelist_matrix[n][m];
    /* printf("Content of cell [%d][%d]\n",n,m); print_cell(n,m); */
    while( 
           (!( (actual->von==node1)&&(actual->nach==node2) )) &&
           (!( (actual->von==node2)&&(actual->nach==node1) ))
         )
    {
      previous=actual;
      actual=actual->next;
    }
    if(actual==edgelist_matrix[n][m]) edgelist_matrix[n][m]=actual->next;
    else                            previous->next=actual->next;
    free(actual);  
  }
 }
}

if(high_y_common+1<=highest_y_cell_nr_old)
{
 for(n=low_x_common;n<=high_x_common;n++)
 {
  for(m=high_y_common+1;m<=highest_y_cell_nr_old;m++)
  {
    actual=edgelist_matrix[n][m];
    /* printf("Content of cell [%d][%d]\n",n,m); print_cell(n,m); */
    while( 
           (!( (actual->von==node1)&&(actual->nach==node2) )) &&
           (!( (actual->von==node2)&&(actual->nach==node1) ))
         )
    {
      previous=actual;
      actual=actual->next;
    }
    if(actual==edgelist_matrix[n][m]) edgelist_matrix[n][m]=actual->next;
    else                            previous->next=actual->next;
    free(actual);  
  }
 }
}

/* We have to insert the edge node1-node2 into the part of the hash table 'new_cells-common_common_cells' */
for(n=lowest_x_cell_nr_new;n<=low_x_common-1;n++)
{
 for(m=lowest_y_cell_nr_new;m<=highest_y_cell_nr_new;m++)
 {
  actual=(struct kante *)malloc(sizeof(struct kante));
  if(actual==NULL) printf("Problems with memory\n");

  actual->von=node1;
  actual->nach=node2;
  actual->next=edgelist_matrix[n][m];
  edgelist_matrix[n][m]=actual;  
 }
}

for(n=high_x_common+1;n<=highest_x_cell_nr_new;n++)
{
 for(m=lowest_y_cell_nr_new;m<=highest_y_cell_nr_new;m++)
 {
  actual=(struct kante *)malloc(sizeof(struct kante));
  if(actual==NULL) printf("Problems with memory\n");

  actual->von=node1;
  actual->nach=node2;
  actual->next=edgelist_matrix[n][m];
  edgelist_matrix[n][m]=actual;   
 }
}

if(lowest_y_cell_nr_new<=low_y_common-1)
{
 for(n=low_x_common;n<=high_x_common;n++)
 {
  for(m=lowest_y_cell_nr_new;m<=low_y_common-1;m++)
  {
  actual=(struct kante *)malloc(sizeof(struct kante));
  if(actual==NULL) printf("Problems with memory\n");

  actual->von=node1;
  actual->nach=node2;
  actual->next=edgelist_matrix[n][m];
  edgelist_matrix[n][m]=actual; 
  }
 }
}

if(high_y_common+1<=highest_y_cell_nr_new)
{
 for(n=low_x_common;n<=high_x_common;n++)
 {
  for(m=high_y_common+1;m<=highest_y_cell_nr_new;m++)
  {
  actual=(struct kante *)malloc(sizeof(struct kante));
  if(actual==NULL) printf("Problems with memory\n");

  actual->von=node1;
  actual->nach=node2;
  actual->next=edgelist_matrix[n][m];
  edgelist_matrix[n][m]=actual;   
  }
 }
}
 /* printf("Saved %d insertions and deletions.\n",update_normal-update_counter); */
}


Local void update_adjlist(int node_number, int neighbor_number)
{
struct nachfolger *new_element;
int coin;

 new_element=(struct nachfolger *)malloc(sizeof(struct nachfolger));
 new_element->nummer=node_number;

 if(randomize) coin=(rand()%17)%2;

 if((!randomize)||(actual_adjacency_list[neighbor_number]==NULL)||(randomize&&coin))
 {
  new_element->next=actual_adjacency_list[neighbor_number];
  actual_adjacency_list[neighbor_number]=new_element;
 }
 else
 {
  new_element->next=actual_adjacency_list[neighbor_number]->next;
  actual_adjacency_list[neighbor_number]->next=new_element;
 }



 new_element=(struct nachfolger *)malloc(sizeof(struct nachfolger));
 new_element->nummer=neighbor_number;

 if(randomize) coin=(rand()%17)%2;

 if((!randomize)||(actual_adjacency_list[node_number]==NULL)||(randomize&&coin))
 {
  new_element->next=actual_adjacency_list[node_number];
  actual_adjacency_list[node_number]=new_element;
 }
 else
 {
  new_element->next=actual_adjacency_list[node_number]->next;
  actual_adjacency_list[node_number]->next=new_element;
 }
}


Local void update_edgelist(int node_number, int neighbor_number)
{
struct kante *new_element;

 new_element=(struct kante *)malloc(sizeof(struct kante));
 new_element->von=node_number;
 new_element->nach=neighbor_number;
 new_element->next=edgelist;
 edgelist=new_element;
}


void update_structures(int node_number)
{
struct nachfolger *pointer;

 pointer=real_adjacency_list[node_number];

 while(pointer!=NULL)
 {
  if(node_is_already_placed[pointer->nummer])
  {
   update_adjlist(node_number,pointer->nummer);
   update_edgelist(node_number,pointer->nummer);
  }
  pointer=pointer->next;
 }
}


int initialize_variables_and_structures(void)
{
int i,j;
int hash_table_x_size=virtual_x_size/width_of_cell;
int hash_table_y_size=virtual_y_size/width_of_cell;

 no_memory=0;

 adjacency_matrix                  =integer_matrix(1,nodes,1,nodes);
 which_edges_have_been_counted     =integer_matrix(1,nodes,1,nodes);
 adjacency_matrix_for_saved_graph  =integer_matrix(1,nodes,1,nodes);

 edgelist_matrix                   =matrix_of_pointer_to_edgelists(0,hash_table_x_size,0,hash_table_y_size);

 real_adjacency_list               =vector_of_adjacency_lists(1,nodes);
 actual_adjacency_list             =vector_of_adjacency_lists(1,nodes);

 iso_node                          =vector_of_snodes(1,nodes);

 real_x_coord                      =integer_vector(1,nodes);
 real_y_coord                      =integer_vector(1,nodes);
 zoomed_x_coord                    =integer_vector(1,nodes);
 zoomed_y_coord                    =integer_vector(1,nodes);
 number_of_node_with_rank          =integer_vector(1,nodes);
 rank_of_node                      =integer_vector(1,nodes);
 height_value                      =integer_vector(1,nodes);
 node_is_already_placed            =integer_vector(1,nodes);
 is_visited                        =integer_vector(1,nodes);
 degree                            =integer_vector(1,nodes);
 degree_to_placed_nodes            =integer_vector(1,nodes);

 if(no_memory) return(0);
 

 cut_number                 =0;
 iterations                 =0;
 placed_nodes               =0;
 edges                      =0;
 make_edge_count_unambigious=0;
 quit_the_algorithm         =0;
 not_all_images_saved       =0;

 if(animation)
 {
  number_of_image_to_save   =1;
  stepwise_image            =0;
 }

 edgelist=NULL;

 for(i=1;i<=nodes-1;i++)
 {
  for(j=i+1;j<=nodes;j++)
  {
   adjacency_matrix[i][j]=adjacency_matrix[j][i]=0;
  }
 }

 for(i=1;i<=nodes;i++) 
 { 
  real_adjacency_list[i]=NULL; 
  actual_adjacency_list[i]=NULL; 
  node_is_already_placed[i]=0; 
  adjacency_matrix[i][i]=0;
 }

 if(count_cuts+cuts_at_end>0)
 {
  for(i=0;i<=hash_table_x_size;i++)
  {
   for(j=0;j<=hash_table_y_size;j++)
   {
    edgelist_matrix[i][j]=NULL;
   }
  }
 }

 animation_not_possible=0;
 if(access(".",02)==-1) 
 {
  printf("Cannot write into current directory!\n");
  if(access("/tmp/",02)==-1)
  {
   printf("Also cannot write into image-directory\n");
   printf("Animations are not possible!");
   error("\nAnimation will not be possible!\n");
   animation_not_possible=1;
  }
  else strcpy(image_directory,"/tmp/");
 }
 else strcpy(image_directory,"./"); 

 printf("The image-directory is %s\n",image_directory);

 return(1);  
}

void free_my_own_structures(void)
{
struct nachfolger *pointer1,*pointer2;
struct kante      *pointer3,*pointer4;
int i,j,overall_entries=0;
int hash_table_x_size=virtual_x_size/width_of_cell;
int hash_table_y_size=virtual_y_size/width_of_cell;



 free_integer_matrix(adjacency_matrix,1,nodes,1);
 free_integer_matrix(which_edges_have_been_counted,1,nodes,1);
 free_integer_matrix(adjacency_matrix_for_saved_graph,1,nodes,1);

 free_vector_of_snodes(iso_node,1);

 free_integer_vector(real_x_coord,1);
 free_integer_vector(real_y_coord,1);
 free_integer_vector(zoomed_x_coord,1);
 free_integer_vector(zoomed_x_coord,1);

 free_integer_vector(number_of_node_with_rank,1);
 free_integer_vector(rank_of_node,1);
 free_integer_vector(height_value,1);
 free_integer_vector(node_is_already_placed,1);
 free_integer_vector(is_visited,1);
 free_integer_vector(degree,1);
 free_integer_vector(degree_to_placed_nodes,1);
 

 for(i=1;i<=nodes;i++)
 {
  pointer1=pointer2=real_adjacency_list[i];
  while(pointer1!=NULL)
  {
   pointer2=pointer1->next;
   free(pointer1);
   pointer1=pointer2;
  }

  pointer1=pointer2=actual_adjacency_list[i];
  while(pointer1!=NULL)
  {
   pointer2=pointer1->next;
   free(pointer1);
   pointer1=pointer2;
  }
 }
 free_vector_of_adjacency_lists(real_adjacency_list,1);
 free_vector_of_adjacency_lists(actual_adjacency_list,1);


 i=0;
 pointer3=pointer4=edgelist;
 while(pointer3!=NULL)
 {
  i++;
  pointer4=pointer3->next;
  free(pointer3);
  pointer3=pointer4;
 }
 printf("Number of edges in edgelist is %d, number of edges is %d\n",i,edges);
 
 if(count_cuts+cuts_at_end>0)
 {
  for(i=1;i<=hash_table_x_size;i++)
    {
   for(j=1;j<=hash_table_y_size;j++)
   {
    pointer3=pointer4=edgelist_matrix[i][j];
     
    /* if(pointer3!=NULL) printf("Edge cell [%d][%d] not empty.\n",i,j); */
    while(pointer3!=NULL)
    {
     pointer4=pointer3->next;
     /* printf("The edge %d-%d\n",pointer3->von,pointer3->nach); */
     free(pointer3);
     pointer3=pointer4;
     overall_entries++;
    }
   } /*  of for j */
  } /*   of for i */  printf("%d overall edge entries, that is every edge is represented %f times.\n",overall_entries,((float)overall_entries)/(float)edges);
  free_matrix_of_pointer_to_edgelists(edgelist_matrix,0,hash_table_x_size,0);
 }
}


int build_internal_data_structure(Sgraph sgraph)
{
int degree_counter;
struct nachfolger *neu;
Snode node;
Sedge edge;
Slist l;
int coin;
int directed=sgraph->directed;
Slist added_edges=empty_slist;

  nodes=0;
  for_all_nodes(sgraph,node)
  {
   nodes++;
  } end_for_all_nodes(sgraph,node);

  if(!initialize_variables_and_structures()) return(0);

  make_connected(sgraph,&added_edges); 

  if(added_edges!=empty_slist)
   warning("The graph is not connected.\nEdges have been introduced internally!\n");


  /* Copy sgraph_structure to my structure                                        */
  for_all_nodes(sgraph,node)
  {
    degree_counter=0;
    for_sourcelist(node,edge)
    {
     /* ignore self-loops and multiple edges                                      */
     if((node->nr!=edge->tnode->nr)&&(adjacency_matrix[node->nr][edge->tnode->nr]==0))
     {
      neu=(struct nachfolger *) malloc(sizeof(struct nachfolger));
      if(!neu) return(0);
      neu->nummer=edge->tnode->nr;

      if(randomize) coin=(rand()%17)%2; /* mod 2 alone does not randomize: 0 1 0 1 ... */

      if((!randomize)||(real_adjacency_list[node->nr]==NULL)||(randomize && coin))
      {
       neu->next=real_adjacency_list[node->nr];
       real_adjacency_list[node->nr]=neu;
      }
      else
      {
       neu->next=real_adjacency_list[node->nr]->next;
       real_adjacency_list[node->nr]->next=neu;
      }
      adjacency_matrix[node->nr][edge->tnode->nr]=1;
      edges++; degree_counter++;
     
     
      if(directed && (adjacency_matrix[edge->tnode->nr][node->nr]==0))
      {
       neu=(struct nachfolger *) malloc(sizeof(struct nachfolger));
       if(!neu) return(0); 
       neu->nummer=node->nr;
 
       if(randomize) coin=(rand()%17)%2; /* mod 2 alone does not randomize: 0 1 0 1 ... */

       if( (!randomize)||(real_adjacency_list[edge->tnode->nr]==NULL)||(randomize && coin))
       {
        neu->next=real_adjacency_list[edge->tnode->nr];
        real_adjacency_list[edge->tnode->nr]=neu;
       }
       else
       {
        neu->next=real_adjacency_list[edge->tnode->nr]->next;
        real_adjacency_list[edge->tnode->nr]->next=neu;
       }
       adjacency_matrix[edge->tnode->nr][node->nr]=1;
       edges++; degree_counter++;
      }
     } /* of if not self-loop and not multiple edge ... */
    } end_for_sourcelist(node,edge);
    degree[node->nr]=degree_counter;
  } end_for_all_nodes(sgraph,node);

  edges=edges/2;

  printf("Number of nodes in internal structure           : %d\n",nodes);
  printf("Number of undirected edges in internal structure: %d\n",edges);
  /* printf("Internal structure is:\n"); show_struct(); */

  if(added_edges!=empty_slist)
  {
   for_slist(added_edges,l)
   {
    remove_edge(attr_data_of_type(l,Sedge));
   } end_for_slist(added_edges,l);
   free_slist(added_edges);
  }
  
  return(1);
}
