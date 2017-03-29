
/* ************************************************************************** */
/* **                                                                      ** */
/* ** Filename: WOODSMOD.H                                                 ** */
/* **                                                                      ** */
/* ** ******************************************************************** ** */
/* **                                                                      ** */
/* ** Description: This header file contains the structures and funtions   ** */
/* **              needed for the planar drawing algorithm by Donald R.    ** */
/* **              Woods                                                   ** */
/* ** Structures: woods_node_attr contains a field for a pointer to the    ** */
/* **                             ordered edge list. first_up_edge,        ** */
/* **                             last_up_edge, first_down_edge,           ** */
/* **                             last_down_edge are pointer to the list   ** */
/* **                             elements that contain the respective     ** */
/* **                             edges. left_sideway_edge and             ** */
/* **                             right_sideway_edge are the horizontal    ** */
/* **                             edges if existing (This structure is     ** */
/* **                             necessary for handling sideway edges)    ** */
/* **             ord_attr is used as attribute for the slist that         ** */
/* **                      represents the ordinates (level) of the nodes   ** */
/* **                      (node)                                          ** */
/* **             main_attr is used as attribute for the slist that        ** */
/* **                       represents the main working data structure     ** */
/* **                       The field upnode contains the node that is     ** */
/* **                       adjacent to the edge (field edge) and has a    ** */
/* **                       higher st-number than downnode (field          ** */
/* **                       downnode). ordinate is the ordinate of upnode  ** */
/* **             bend_attr is used as attribute for the slist that is     ** */
/* **                       representing the edge attribute. It stores the ** */
/* **                       relative coordinates of the edge bends         ** */
/* **                                                                      ** */
/* ** Function: maxwoods(g,firstedge,st_num,x_grid,y_grid) is the main     ** */
/* **                       function of the Woods-algorithm                ** */
/* **                       g: Sgraph whose node attribute points to the   ** */
/* **                          ordered edge list                           ** */
/* **                       firstedge: the edge that is in the edge list   ** */
/* **                                  of the node with st-number 1. The   ** */
/* **                                  other node has the maximum          ** */
/* **                                  st-number                           ** */
/* **                       st_num: slist that contains the st-numbers and ** */
/* **                               the respective nodes                   ** */
/* **                       x_grid: horizontal distance between two        ** */
/* **                               lattice points                         ** */
/* **                       y_grid: vertical distance between two lattice  ** */
/* **                               points                                 ** */
/* **                                                                      ** */
/* ** Date: 18.5.1994                                                      ** */
/* **                                                                      ** */
/* ************************************************************************** */
/*                                                                            */

#define SET 0
#define WORK 1


typedef struct woods_node_attr
{
    Slist embed_list;
    Slist first_up_edge,
          last_up_edge,
          first_down_edge,
          last_down_edge;
    Sedge left_sideway_edge,
          right_sideway_edge;
}
*Woods_node_attr;

#define GET_EMBED_LIST(n) (attr_data_of_type(n,Woods_node_attr)->embed_list)

typedef struct ord_attr
{
    Snode   node;
    int     level;
}
*Ord_attr;

typedef struct main_attr
{
    Snode   upnode;
    int     ordinate;
    Snode   downnode;
    Sedge   edge;
}
*Main_attr;


typedef struct bend_attr
{
    int     x;
    int     y;
}
*Bend_attr;


extern void maxwoods(Sgraph g, Sedge co_firstedge, Slist st_num, int x_gridsize, int y_gridsize);


/*                                                                            */
/* ************************************************************************** */
/* **                     END OF FILE: WOODSMOD.H                          ** */
/* ************************************************************************** */

