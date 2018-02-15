/* Structures, internally needed functions and externally visible bfs() */

#include <stdio.h>
#include <malloc.h>
 
#include "glob_var_for_algo.h"

struct schlange
{
 int nummer;
 int hoehe;
 struct schlange *pre;
 struct schlange *suc;
};

int empty(struct schlange *queue)
{
 return(queue==NULL);
}


void print_queue(struct schlange *queue)
{
struct schlange *actual;

printf("The status of the queue:\n");

 if(!empty(queue))
 {
	actual=queue;
	do
	{
	 printf("Element %d has height %d\n",actual->nummer,actual->hoehe);
	 actual=actual->suc;
	} while(actual!=queue);
 }
 else printf("Queue is empty.\n");

printf("End of queue\n");
}


void free_queue(struct schlange **queue)
{
struct schlange *actual;

 if((*queue)->suc==*queue) free(queue);

 else
 {
	actual=(*queue);
	actual->pre->suc=actual->suc;
	actual->suc->pre=actual->pre;
	(*queue)=(*queue)->suc;
	free(actual);
	free_queue(queue);
 }
}


void enqueue(struct schlange **queue, int number, int height)
{
struct schlange *schlangenelement;

 schlangenelement=(struct schlange *)malloc(sizeof(struct schlange));
 if(schlangenelement==NULL) printf("Memory fault in enqueue ...\n");

 schlangenelement->nummer=number;
 schlangenelement->hoehe=height;

 if(empty(*queue))
 {
  schlangenelement->pre=schlangenelement->suc=schlangenelement;
  *queue=schlangenelement;
 }

 else
 {
  schlangenelement->suc=(*queue);
  schlangenelement->pre=(*queue)->pre;
  (*queue)->pre->suc=schlangenelement;
  (*queue)->pre=schlangenelement;
 }
}



struct schlange *dequeue(struct schlange **queue)
{
struct schlange *element;

 if((*queue)->pre==(*queue))
 {
  element=*queue;
  *queue=NULL;
 }

 else
 {
  element=(*queue);
  (*queue)->pre->suc=(*queue)->suc;
  (*queue)->suc->pre=(*queue)->pre;
  (*queue)=(*queue)->suc;
 }

 return(element);
}


int bfs(int i, unsigned short flag)
{
int               k=1,j,buffer,l;
int               max_height=1,max_degree_to_placed_nodes,max_index;
struct schlange   *queue=NULL;
struct schlange   *act_node;
struct nachfolger *pointer;
int               actual_height;
int               first_with_actual_height, last_with_actual_height=0;


 for(j=1;j<=nodes;j++) is_visited[j]=0;

 enqueue(&queue,i,1);
 is_visited[i]=1;

 while(!empty(queue))
 {
  act_node=dequeue(&queue);

  number_of_node_with_rank[k]=act_node->nummer;
  rank_of_node[act_node->nummer]=k;
  k++;
  height_value[act_node->nummer]=act_node->hoehe;

  pointer=real_adjacency_list[act_node->nummer];

  while(pointer!=NULL)
  {
   if(!is_visited[pointer->nummer])
   {
    is_visited[pointer->nummer]=1;
    enqueue(&queue,pointer->nummer,act_node->hoehe+1);
    if(act_node->hoehe+1>max_height) max_height=act_node->hoehe+1;
   }
   pointer=pointer->next;
  }
  free(act_node);
 }
 
 if(flag) 
 /* 
   Sorting the elements with same height wrt degree to already 
   placed nodes and then wrt real degree 
 */
 {
  /* 
    printf("array:\n"); 
    for(l=1;l<=nodes;l++) printf("%d ",number_of_node_with_rank[l]); 
    printf("\n\n"); 
  */

  rank_of_node[number_of_node_with_rank[1]]=1;
  for(i=2;i<=nodes;i++) rank_of_node[number_of_node_with_rank[i]]=nodes+1;

  for(actual_height=1;actual_height<=max_height;actual_height++) 
  /* for every block of nodes with same height */
  {
   /* compute the indices of the current block ....  */
   i=first_with_actual_height=last_with_actual_height+1;         
   while(
         (i<=nodes-1) &&
         (height_value[number_of_node_with_rank[i+1]]==actual_height)
        ) i++;
   last_with_actual_height=i;
   /*
   printf("These are the nodes with height %d\n",actual_height);
   for(i=first_with_actual_height;i<=last_with_actual_height;i++) 
    printf("%d ",number_of_node_with_rank[i]);
   printf("\n");
   */
   if(actual_height==1) k=2;                        
   /* the next node to find is node with ordernumber 2    */

   else                 k=first_with_actual_height; 
   /* the next node to find is the first in the new block */
   
   /* 
     the position to fill is position j; 
     among the nodes in the current block find the node   
     with highest degree to already placed nodes and among 
     those the one with highest degree  
   */
   for(j=k;j<=last_with_actual_height;j++)
   {
    /* printf("Trying to fill position %d\n",j); */
    
    for(l=j;l<=last_with_actual_height;l++) 
     degree_to_placed_nodes[number_of_node_with_rank[l]]=0;

    max_degree_to_placed_nodes=0;

    for(i=j;i<=last_with_actual_height;i++)
    {
     pointer=real_adjacency_list[number_of_node_with_rank[i]];
     while(pointer!=NULL)
     {
      if(rank_of_node[pointer->nummer]<j) 
       degree_to_placed_nodes[number_of_node_with_rank[i]]++;
      pointer=pointer->next;
     }
     /* 
       printf("Node %d has degree_to_placed_nodes %d\n",number_of_node_with_rank[i],degree_to_placed_nodes[number_of_node_with_rank[i]]); 
     */

     if(
        (degree_to_placed_nodes[number_of_node_with_rank[i]]>max_degree_to_placed_nodes)             ||
        (
         (degree_to_placed_nodes[number_of_node_with_rank[i]]==max_degree_to_placed_nodes     ) &&
         (degree[number_of_node_with_rank[i]]>degree[number_of_node_with_rank[max_index]])
        )
       )
     {
      max_degree_to_placed_nodes=degree_to_placed_nodes[number_of_node_with_rank[i]];
      max_index=i;
     }
    }
    /* 
      printf("node %d, position %d has degree_to_placed_node %d (maximal)\n",number_of_node_with_rank[max_index],max_index,max_degree_to_placed_nodes);
      printf("Now exchanging position %d and %d\n",max_index,j); 
    */

    buffer=number_of_node_with_rank[j];
    number_of_node_with_rank[j]=number_of_node_with_rank[max_index];
    number_of_node_with_rank[max_index]=buffer;
    rank_of_node[number_of_node_with_rank[j]]=j;

    /* 
      printf("array:\n"); 
      for(l=1;l<=nodes;l++) 
       printf("%d, rank %d| ",number_of_node_with_rank[l],rank_of_node[number_of_node_with_rank[l]]); 
      printf("\n\n"); 
    */
   }
  }
  /*
  for(i=1;i<=nodes;i++)
  {
   j=number_of_node_with_rank[i];
   if((height_value[j]>1)&&(height_value[j]>height_value[number_of_node_with_rank[i-1]])) 
    printf("\n");
   printf("Height: %d  Rank: %d  nodenumber %d  degree_to_already_placed_nodes: %d  degree %d\n",height_value[j],rank_of_node[j],j,degree_to_placed_nodes[j],degree[j]); 
  }
  */
 }

 return(max_height);
}
