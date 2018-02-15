
/* ************************************************************************** */
/* **                                                                      ** */
/* ** Filename: DFSMOD.H                                                   ** */
/* **                                                                      ** */
/* ** ******************************************************************** ** */
/* **                                                                      ** */
/* ** Description: This header-file provides the functions and structures  ** */
/* **              needed for the dfs                                      ** */
/* ** Macros: TEST_UNIQUE_EDGE(e) is a macro that is used in all parts of  ** */
/* **         the project and is used to get a unique reference for edges  ** */
/* **         in undirected graphs                                         ** */
/* **         The other macros don't need to be explained                  ** */
/* **                                                                      ** */
/* ** Structures: node_attr contains a pointer to the ordered edge list    ** */
/* **                       around the node (the list is created by embed  ** */
/* **                       and then linked to embed_edges) and a pointer  ** */
/* **                       to the structure node_state, that contains the ** */
/* **                       dfs information                                ** */
/* **             node_state contains a field for the dfs-number (dfs_num) ** */
/* **                        one for the lowpoint (low_pt) and one to      ** */
/* **                        remember whether a node has been visited or   ** */
/* **                        not (marked)                                  ** */
/* **             edge_state contains two fields to show that a edge is a  ** */
/* **                        back-edge (is_back_edge) or a tree-edge       ** */
/* **                        (is_tree_edge). face_id is unused. traversed  ** */
/* **                        is used to remember in which direction a edge ** */
/* **                        has been traversed. The field findlabel is    ** */
/* **                        used to mark a edge so that it could be found ** */
/* **                        in the edge list of the opposit node          ** */
/* ** Functions: opposit_node(n,e) returns the node on the other side of   ** */
/* **                              the edge e                              ** */
/* **            free_dfs_graph_attributes(g) frees the structures         ** */
/* **                                     node_state, node_attr and        ** */
/* **                                     edge_state. New node attribute   ** */
/* **                                     is a pointer to the ordered edge ** */
/* **                                     list.                            ** */
/* **            dfs_main(g) makes use of the functions in BISECMOD.H to   ** */
/* **                        find the largest face and bisect it (perhaps  ** */
/* **                        by a dummy edge). The dfs information is      ** */
/* **                        needed for computing the st-numbering         ** */
/* **                                                                      ** */
/* ** Date: 18.5.1994                                                      ** */
/* **                                                                      ** */
/* ************************************************************************** */
/*                                                                            */


#define TEST_UNIQUE_EDGE(e) ((e->snode->graph->directed)||(unique_edge(e)))

#define NO  0
#define YES 1	
#define PRE 1	
#define SUC 2	
#define IAM 1	


/*                                                                            */
/* For the nodes                                                              */
/*                                                                            */

typedef struct node_state
{
	int dfs_num;
	int low_pt;
    int marked;
}
*Node_state;

typedef struct node_attr
{
	Slist      embed_edges;
	Node_state dfs_state;
}
*Node_attr;

#define get_dfs_state(n) ((attr_data_of_type(n,Node_attr)) -> dfs_state)
#define get_embed_edges(n) ((attr_data_of_type(n,Node_attr)) -> embed_edges)
#define MARK_NODE_FACE_ID(n) ((get_dfs_state(n)->marked)=1)


/*                                                                            */
/* For the edges                                                              */
/*                                                                            */

typedef struct edge_state
{
	int is_tree_edge;
	int is_back_edge;
	int face_id;
	int traversed;
	int findlabel;
}
*Edge_state;

#define MARK_PRE_TRAVERSED(e)  (((attr_data_of_type(e,Edge_state))->traversed)=PRE)
#define MARK_SUC_TRAVERSED(e)  (((attr_data_of_type(e,Edge_state))->traversed)=SUC)
#define MARK_FINDLABEL(e)      (((attr_data_of_type(e,Edge_state))->findlabel)=IAM)
#define UNMARK_FINDLABEL(e)    (((attr_data_of_type(e,Edge_state))->findlabel)=NO )
#define MARK_EDGE_FACE_ID(e)   (((attr_data_of_type(e,Edge_state))->face_id  )=1  )


/*                                                                            */
/* Functions                                                                  */
/*                                                                            */

extern Snode opposit_node(Snode n, Sedge e);

extern void free_dfs_graph_attributes(Sgraph g);

extern Sedge dfs_main(Sgraph g);


/*                                                                            */
/* ************************************************************************** */
/* **                      END OF FILE: DFSMOD.H                           ** */
/* ************************************************************************** */

