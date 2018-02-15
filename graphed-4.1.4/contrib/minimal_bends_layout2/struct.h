/****************************************************************/
/*                                                              */
/*   struct.h                                                   */
/*   include for function global types                          */
/*                                                              */
/*                                                              */
/*                                                              */
/****************************************************************/



typedef struct
minimal_bends_settings2 
{
	int grid;
	int grid_defaults;
}
*SMin_bends_settings;


/****************************************************************************/
/*																			*/
/* structure to hold the facelist                                           */
/* this structure is filled by findfaces                                    */
/*																			*/
/****************************************************************************/

typedef struct
    sFacelist
    {
        struct sFace *OuterFace;        
        struct sFace *InnerFaces;
    }
    *SFaceList;

typedef struct
    sFace
    {
        struct sFaceElement *elements;
	struct sFaceElement *connection; /* to neighbouring face */
        struct sblock       *blocklist;
	struct snetnode     *netnode;	/* corresponding node of the network, */
					/* filled by trans_to function */
        struct sgrid        *facegrid;  /* part of the grid used by face */
        int 		    linesno;   /* product of number of used x- and ylines */
        struct sFace        *pred;
        struct sFace        *succ;
    }
    *SFace;

typedef struct
    sFaceElement
    {
		Snode				snode; /* corresponding Sgraph node  */
		Snode				tnode; /* corresponding Sgraph node  */
	        Sedge				sedge; /*		"       Sgraph edge, */
								   /* with source snode  */
		struct sshape		*shape; /* shape for reconstructing the graph */
									/* out of the network */
        struct sFaceElement *pred;
        struct sFaceElement *succ;
    }
    *SFaceElement;


/****************************************************************************/
/*																			*/
/* structure used for min-cost-flow network                                 */
/*																			*/
/****************************************************************************/

typedef struct
    snetwork
    {
        struct snetnode *nodelist;
        struct snetedge *edgelist;
        struct snetnode *gsource;     /* global source of network */
        struct snetnode *gtarget;     /* global target of network */
		int 			flow;		 /* absolute flow of network, */
									 /* filled by trans_to function */
    }
    *Snetwork;


typedef struct
    snetedge
    {
        struct snetnode *snode;
        struct snetnode *tnode;
        int             capacity;
        int             cost;
        int		oldcost;   /* original cost of edge before transformation */
        int             flow;
		int 			state;		/* indicates if node is already processed */
		struct snetedge *father;	/* first edge in connection */
        struct snetedge *pred;
        struct snetedge *succ;
    }
    *Snetedge;

typedef struct
    sedgelist
    {
        struct snetedge     *edge;
        struct sedgelist    *pred;
        struct sedgelist    *succ;
    }
    *Sedgelist;

/* types of nodes used in the Snetnode struct member type   */
enum netnode { WAS_NODE,
               INNER_FACE,
               OUTER_FACE,
               G_SOURCE,
               G_TARGET,
               HELPNODE};

typedef struct
    snetnode
    {
		enum netnode		type; /* Why was I made? (Face, original node...) */
        union 
        {
            Snode           node;      /* pointer to this node */
            SFace           face;      /* respectively face    */ 
        }real;
        int                 distance;
	int                 olddistance;   /* original distance before cost transformation */
        int                 state;         /* incoming edge   */
        struct snetedge     *inedge;       /* outcoming edges */
        struct sedgelist    *outedgelist;
        struct snetnode     *pred;
        struct snetnode     *succ;
    }
    *Snetnode;

/****************************************************************************/
/*																			*/
/* structure for embedding network into the grid                            */
/*																			*/
/****************************************************************************/

typedef struct
    sgridline
    {
        int                     lineno;
	struct sgridpointlist	*points;  /* contains SGridPoints on the line */
	struct sgridlinelist	*minors;  /* lines left of or under the line */
   	struct sgridlinelist    *tminors;  /* minors including transitives */
	struct sgridlinelist    *faceminors; /* minors in the face */
	struct sgridlinelist    *majors;  /* lines right of or over the line */
	struct sgridlinelist    *clines;  /* lines crossing line */
    }
    *SGridLine;

typedef struct 
    sgridlinelist
    {
	struct sgridline	  *line;
        struct sgridlinelist  *pred;
        struct sgridlinelist  *succ;
    }
    *SGridLineList;

typedef struct 
    sfaceshape
    {
	struct sshape *shape;
        struct sgridlinelist  *xPos;
        struct sgridlinelist  *yPos;
        struct sfaceshape  *pred;
        struct sfaceshape  *succ;
    }
    *SFaceShape;

typedef struct
    sgrid
    {
	struct sgridlinelist *xlines;
	struct sgridlinelist *ylines;
    }
    *SGrid;

typedef struct
    sgridpointlist
    {
         struct sgridpoint      *point;
         struct sgridpointlist  *pred;
         struct sgridpointlist  *succ;
    }
    *SGridPointList;

typedef struct
    sgridpoint
    {
        struct sgridline *xline;
        struct sgridline *yline;
    }
    *SGridPoint;

typedef struct
    sshape
    {
		int 			angleno;	  /* angle to predecessing shape: */
									  /* RIGHT, LEFT, FORWARD, BACKWARD */
		int 			direction;	  /* RIGHT, LEFT, UP, DOWN */
        SGridPoint      sourcepoint;
		SGridPoint		targetpoint;
        struct sshape   *pred;
        struct sshape   *succ;
    }
    *SShape;

/* defines for nonrelatedlist */
#define _LESS  -1
#define _NONE   0
#define _HIGHER 1


typedef struct 
snonrelatedlist
   {
   SGridLine line1;
   SGridLine line2;
   int       relation;
   struct snonrelatedlist *succ;
   }
*SNonRelatedList;




/****************************************************************************/
/*																			*/
/* other general structures                                                 */
/*																			*/
/****************************************************************************/

typedef
    struct allocated
	{
	char *alloc;
    struct allocated *succ;
    struct allocated *pred;
	}
    * Sallocated;

typedef struct
    spoint
    {
        int x,
            y;
    }
    *Spoint;


/****************************************************************************/
/*																			*/
/* attribut structures for nodes and edges                                  */
/*																			*/
/****************************************************************************/


typedef struct
    sEdgeAttr
    {
        struct sFace *Face1;     /* first face it belonges to */
        struct sFace *Face2;     /* second face */
        Attributes   attrs;
        int          visited;

    }
    *SEdgeAttr;

typedef struct
    sNodeAttr
    {
		struct snetnode   *netnode; 	/* pointer to the created netnode */
										/* if it exists (weight < 4 ) */
        struct sgridpoint *gridpoint;
        Attributes        attrs;
    }
    *SNodeAttr;

/* macro defines for accessing attributes                                   */
#define EDGE_ATTR(a)  ((SEdgeAttr)attr_data(a))
#define NODE_ATTR(a)  ((SNodeAttr)attr_data(a))
#define NODE_ATTR_SLIST(a) ((Slist)(attr_data(NODE_ATTR(a))))
#define ATTRS_EDGE(a)  ((Sedge)attr_data(a))

/* travers through all edges */
#define for_all_edges(a,b)	{\
				    Snode n;\
				    Sedge e;\
				    for_all_nodes(a,n)\
			            {\
					b=n->slist;\
					e=b;\
					do\
					{

#define end_for_all_edges(a,b)  	b=b->ssuc;\
					}\
				    while((b!=e) && (b != (Sedge)NULL));\
				    }\
				    end_for_all_nodes(a,n);\
				}
