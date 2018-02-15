

/* defines for NULL elements of lists or structures*/
#define NULL_EDGE   (Snetedge)0
#define NULL_LIST   (Sedgelist)0
#define NULL_NODE   (Snetnode)0
#define NULL_FACE   (SFace)0
#define NULL_ELEMENT (SFaceElement)0
#define NULL_SHAPE  (SShape)0
#define NULL_BLOCK  (SBlock)0
#define NULL_F_ELEM (SFaceElement)0
#define NULL_G_POINT_L (SGridPointList)0
#define NULL_G_LINE (SGridLine)0
#define NULL_LINE   (SGridLine)0
#define INFINITE 1000000000

#define ERROR_CORRUPTED_EDGE_LINE 1

#define VISITED 0x0100
#define DELETED 0x0001
#define UNVISITED_NODES 1
#define NO_UNVISITED_NODES 0
#define NO_BACK_EDGE 0
#define HELP_BACK_EDGE 1
#define NET_BACK_EDGE 2
#define VISITED_EDGE 1


/* defines used by grid embedding algorithm  */
#define RIGHT               0
#define FORWARD		    1
#define DOWN		    1
#define LEFT                2
#define UP		    3
#define BACKWARD	    3
#define NO_PATTERN          4
#define LEFT_RIGHT	    1
#define LEFT_RIGHT_RIGHT    3

/* defines for findface */
#define NOT_VISITED         0x0000
#define ONCE_VISITED        0x0001
#define TWICE_VISITED       0x0002


/* defines for permute the lists */
#define XLIST 0
#define YLIST 1

#define PERM_INIT   0
#define PERM_NEXT   1
#define PERM_RESET  2 
#define PERM_DELETE 3

