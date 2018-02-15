
/* ************************************************************************** */
/* **                                                                      ** */
/* ** Filename: BISECMOD.H                                                 ** */
/* **                                                                      ** */
/* ** ******************************************************************** ** */
/* **                                                                      ** */
/* ** Description: This header-file contains the structure and function to ** */
/* **              find and bisect the largest face in a planar graph      ** */
/* **                                                                      ** */
/* ** Structure: face_info contains fields to show the direction (PRE/SUC) ** */
/* **            in which the face has been found (direction), one to show ** */
/* **            the size of the face (size). Firstnode is a pointer to    ** */
/* **            first node of the face and firstedge is the first edge    ** */
/* **            (in the given direction) after firstnode. Thirdnode is a  ** */
/* **            pointer to the node in the middle of the face and         ** */
/* **            secondedge is the edge before thirdnode. Facenodes is a   ** */
/* **            pointer to a list of nodes arouns the face (currently     ** */
/* **            unused)                                                   ** */
/* **                                                                      ** */
/* ** Function: bisection_of_largest_face(g) finds the largest face in a   ** */
/* **           planar graph g with a ordered edge list and if this face   ** */
/* **           is not bisected (in the middle) a dummy-edge is inserted   ** */
/* **           to do this. The bisecting edge is returned                 ** */
/* **                                                                      ** */
/* ** Date: 18.5.1994                                                      ** */
/* **                                                                      ** */
/* ************************************************************************** */
/*                                                                            */


typedef struct face_info
{
    int   direction;
    int   size;
    Snode firstnode;
    Snode thirdnode;
    Sedge firstedge;
    Sedge secondedge;
    Slist facenodes;
}
*Face_info;

extern Sedge bisection_of_largest_face(Sgraph g);


/*                                                                            */
/* ************************************************************************** */
/* **                       END OF FILE: BISECMOD.H                        ** */
/* ************************************************************************** */

