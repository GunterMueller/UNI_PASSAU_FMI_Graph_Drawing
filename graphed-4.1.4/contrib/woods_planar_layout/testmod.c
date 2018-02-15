
/* ************************************************************************** */
/* **                                                                      ** */
/* ** Filename: TESTMOD.C                                                  ** */
/* **                                                                      ** */
/* ** ******************************************************************** ** */
/* **                                                                      ** */
/* ** Description: This module contains some test-functions (not needed to ** */
/* **              run the Woods-algorithm)                                ** */
/* **                                                                      ** */
/* ** Date: 18.5.1994                                                      ** */
/* **                                                                      ** */
/* ************************************************************************** */
/*                                                                            */

										   
#include "std.h"
#include "slist.h"
#include "sgraph.h"
#include "graphed.h"
#include "dfsmod.h"
#include "stnummod.h"
#include "woodsmod.h"
#include "testmod.h"


/* This function prints the information, dfs_main */ 
/* deliveres, on the GraphEd message-screen       */

void print_dfs_infos(Sgraph g, Sedge firstedge)
{
    Snode   n;
    Sedge   e;
    Node_state  helpnode;
    Edge_state  helpedge;

    for_all_nodes(g,n)
    {
        helpnode=get_dfs_state(n);
        message("Nr.: %d  dfs_num: %d  low_pt: %d  marked: %d \n",
                 n->nr,helpnode->dfs_num,helpnode->low_pt,helpnode->marked);
        for_sourcelist(n,e)
        {
            helpedge=attr_data_of_type(e,Edge_state);
            message("Ziel: %d  tree: %d  back: %d \n",
                     e->tnode->nr,helpedge->is_tree_edge,helpedge->is_back_edge);
        }
        end_for_sourcelist(n,e);
    }
    end_for_all_nodes(g,n);

    message("snodenr: %d  tnodenr: %d \n",
             firstedge->snode->nr,firstedge->tnode->nr);
}


/* This function printf's the node and edge attributes */

void print_all_attrs(Sgraph g)
{
    Snode   n;
    Sedge   e;
    Node_state  helpnode;
    Edge_state  helpedge;

    for_all_nodes(g,n)
    {
        helpnode = get_dfs_state(n);
        printf("nr: %d dfs: %d low: %d marked: %d \n",
                n->nr,helpnode->dfs_num,helpnode->low_pt,helpnode->marked);
        for_sourcelist(n,e)
        {
            helpedge = attr_data_of_type(e,Edge_state);
            printf("sou: %d tar: %d tree: %d back: %d \n",
                    n->nr,e->tnode->nr,helpedge->is_tree_edge,helpedge->is_back_edge);
        }
        end_for_sourcelist(n,e);

        if(n->graph->directed)
        {
            for_targetlist(n,e)
            {
                helpedge = attr_data_of_type(e,Edge_state);
                printf("sou: %d tar: %d tree: %d back: %d \n",
                        e->snode->nr,n->nr,helpedge->is_tree_edge, helpedge->is_back_edge);
            }
            end_for_targetlist(n,e);
        }
    }
    end_for_all_nodes(g,n);
}


/* This function printf's the edge-attribute only */

void print_edges(Snode n)
{
    Sedge       e;
    Edge_state  helpedge;

    for_sourcelist(n,e)
    {
        helpedge=attr_data_of_type(e,Edge_state);
        printf("%d %d \n",helpedge->is_tree_edge,helpedge->is_back_edge);
    }
    end_for_sourcelist(n,e);

    if(n->graph->directed)
    {
        for_targetlist(n,e)
        {
            helpedge=attr_data_of_type(e,Edge_state);
            printf("%d %d \n",helpedge->is_tree_edge,helpedge->is_back_edge);
        }
        end_for_targetlist(n,e);
    }
}


void print_st_num(Slist st_num)
{
	Slist	l;
	St_attr	helplist;

	for_slist(st_num,l)
	{
		helplist = attr_data_of_type(l,St_attr);
		printf("st-number: %d   node-number: %d \n",helplist->st_nr,helplist->node->nr);
	}
	end_for_slist(st_num,l);
}


char *int2char(int n)
{
	int c,i,j,sign;
	char s[3], *save;

	if((sign = n) < 0)
	{
		n = -n;
	}
	
	i=0;
	
	do
	{
		s[i++] = n%10 + '0';
	}
	while((n /=10) > 0);

	if(sign < 0)
	{
		s[i++] = '-';
	}
	
	s[i] = '\0';

	for(i=0,j=strlen(s)-1;i<j;i++,j--)
	{
		c=s[i];
		s[i]=s[j];
		s[j]=c;
	}

	save=(char *)malloc(strlen(s)+1);

	if(save != NULL)
	{
		strcpy(save,s);
	}
	
	return(save);
}


void set_st_as_label(Slist st_num)
{
	Slist l;
	St_attr helplist;
	Snode curr_node;
	int curr_st;
	char *label;	

	for_slist(st_num,l)
	{
		helplist=attr_data_of_type(l,St_attr);
		curr_node=helplist->node;
		curr_st  =helplist->st_nr;
		label=int2char(curr_st);
		set_nodelabel(curr_node,label);
	}
	end_for_slist(st_num,l);
}

void set_node_nr_as_label(Sgraph g)
{
	Snode n;
	char *label;

 	for_all_nodes(g,n)
     	{
		label = int2char(n->nr);
       		node_set(graphed_node(n),NODE_LABEL,label,0);
	}
	end_for_all_nodes(g,n);
}


void print_stack(Slist stack)
{
	Slist	l;
	Snode	helpstack;

	for_slist(stack,l)
	{
		helpstack = attr_data_of_type(l,Snode);
		printf("node-number: %d  \n",helpstack->nr);
	}
	end_for_slist(stack,l);
}



void print_path(Slist path)
{
	Slist	l;
	Snode	helppath;

	for_slist(path,l)
	{
		helppath = attr_data_of_type(l,Snode);
		printf("node-number: %d  \n",helppath->nr);
	}
	end_for_slist(path,l);
}


void test_edges(Sgraph g)
{
	Snode v;
	Sedge e;

	for_all_nodes(g,v)
	{
		for_sourcelist(v,e)
		{
			printf("??? \n");
		}
		end_for_sourcelist(v,e);
	}
	end_for_all_nodes(g,v);
}


void print_ord_list(Slist ordinate_list)
{
	Slist l;
	Ord_attr helplist;

	for_slist(ordinate_list,l)
	{
		helplist=attr_data_of_type(l,Ord_attr);
		printf("Node: %d   Level: %d \n",helplist->node->nr,
						 helplist->level);
	}
	end_for_slist(ordinate_list,l);
}


void print_edge_bends(Sgraph g)
{
	Snode n;
	Sedge e;
	Slist *helpedge;
	Slist helplist,l;
	Bend_attr helpbend;

	for_all_nodes(g,n)
	{
		for_sourcelist(n,e)
		{
			printf("new edge  \n");
			helpedge=attr_data_of_type(e,Slist *);
			helplist=*helpedge;
			for_slist(helplist,l)
			{
				helpbend=attr_data_of_type(l,Bend_attr);
				printf("x: %d  y: %d  \n",helpbend->x,helpbend->y);
			}
			end_for_slist(helplist,l);
		}
		end_for_sourcelist(n,e);
	}
	end_for_all_nodes(g,n);
}


  

void print_bends_list(Slist bends_list)
{
	Slist l;
	Bend_attr helpbend;

	for_slist(bends_list,l)
	{
		helpbend=attr_data_of_type(l,Bend_attr);
		printf("x: %d  y: %d  \n",helpbend->x,helpbend->y);
	}
	end_for_slist(bends_list,l);
}


void print_edgeline(Sedge e)
{
	Edgeline eline,l;

	eline = (Edgeline)edge_get(graphed_edge(e),EDGE_LINE);
	
	for_edgeline(eline,l)
	{
		printf("x: %d y: %d  \n",edgeline_x(l),edgeline_y(l));
	}
	end_for_edgeline(eline,l);
}


void print_pre_edgeline(Edgeline eline)
{
	Edgeline l;
	
	for_edgeline(eline,l)
	{
		printf("x: %d  y: %d  \n",edgeline_x(l),edgeline_y(l));
	}
	end_for_edgeline(eline,l);
}


void print_graphed_node_coord(Sedge e)
{
	int xs,ys,
	    xt,yt;
		
	xs = (int)node_get(graphed_node(e->snode),NODE_X);
	ys = (int)node_get(graphed_node(e->snode),NODE_Y);

	xt = (int)node_get(graphed_node(e->tnode),NODE_X);
	yt = (int)node_get(graphed_node(e->tnode),NODE_Y);

	printf("xs: %d ys: %d -- xt: %d yt: %d  \n",xs,ys,xt,yt);
}


void test_all_edges(Sgraph g)
{
    Snode n;
    Sedge e;
    Bend_attr helpbend;
    Slist *help, helplist,l;

    for_all_nodes(g,n)
    {
        for_sourcelist(n,e)
        {   
            printf("snode: %d tnode: %d \n",e->snode->nr,e->tnode->nr);

            help = attr_data_of_type(e,Slist *);
            helplist = *help;

            for_slist(helplist,l)
            { 
                helpbend = attr_data_of_type(l,Bend_attr);
                printf("    x: %d  y: %d \n",helpbend->x,helpbend->y);
            }
            end_for_slist(helplist,l);
        }
        end_for_sourcelist(n,e);
     }
     end_for_all_nodes(g,n);
}
            

void print_face(Slist face)
{
    Slist l;
    Snode node;

    for_slist(face,l)
    {
	node = attr_data_of_type(l,Snode);
	printf("Node-Nr.: %d  Face_Id.: %d \n",node->nr,
                                               get_dfs_state(node)->marked);
    }
    end_for_slist(face,l);
}


void print_edge_lists(Snode up_node, Snode down_node)
{
    Slist embed_list,l;
    Sedge e;

    embed_list = GET_EMBED_LIST(up_node);
    for_slist(embed_list,l)
    {
        e = attr_data_of_type(l,Sedge);
        printf("snode-nr.: %d  tnode-nr.: %d \n",e->snode->nr,e->tnode->nr);
    }
    end_for_slist(embed_list,l);

    embed_list = GET_EMBED_LIST(down_node);
    for_slist(embed_list,l)
    {
        e = attr_data_of_type(l,Sedge);
        printf("snode-nr.: %d  tnode-nr.: %d \n",e->snode->nr,e->tnode->nr);
    }
    end_for_slist(embed_list,l);
}

void print_up_down_edges(Slist first_up, Slist last_up, Slist first_down, Slist last_down)
{
    Sedge edge;

    printf("Up-down-edges: \n");

    if(first_up != NULL)
    {
        edge = attr_data_of_type(first_up,Sedge);
        printf("fu ! snode: %d  tnode: %d \n",edge->snode->nr,edge->tnode->nr);

        edge = attr_data_of_type(last_up,Sedge);
        printf("lu ! snode: %d  tnode: %d \n",edge->snode->nr,edge->tnode->nr);

        edge = attr_data_of_type(first_down,Sedge);
        printf("snode: %d  tnode: %d \n",edge->snode->nr,edge->tnode->nr);

        edge = attr_data_of_type(last_down,Sedge);
        printf("snode: %d  tnode: %d \n",edge->snode->nr,edge->tnode->nr);
    }
}


/*                                                                            */
/* ************************************************************************** */
/* **                       END OF FILE: TESTMOD.C                         ** */
/* ************************************************************************** */

