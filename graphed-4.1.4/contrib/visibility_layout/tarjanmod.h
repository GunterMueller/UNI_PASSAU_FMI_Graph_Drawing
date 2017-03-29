/****************************************************************************/
/***                                                                      ***/
/*** Filename: TARJANMOD.H                                                ***/
/***                                                                      ***/
/****************************************************************************/

/*-------------------------- Strukturdefinitionen --------------------------*/ 

typedef struct                /* dient als Koordinatentupel */
        {
        int x,y;              /* X und Y-Koordinate */
        } x_y;
typedef x_y *X_Y;
 
typedef struct       /* dient als Koordinatentupel */
	{
	double x,y;  /* X und Y-Koordinate, diesmal aber als Floating Zahlen */
	} dx_y;
typedef dx_y *DX_Y;

typedef struct graph_attr
        {	
        Sgraph  dual_graph;   /* zeigt auf den Dualgraphen */
	Snode	start;	      /* rettet den ersten Knoten im Originalgraph */
        int     width;        /* die Breite */
        int     heigth;       /* die Hoehe */
        int     max_nr;       /* die hoechste Knotennummer = T-Knotennummer */
	Sedge	dummy_edge;   /* zeigt auf die Dummykante bei LargeFace */
        } 
*Graph_attr;

typedef struct node_attr
        {
        Slist   edges;     /* die Kantenliste um v fuer die Einbettung */
        DX_Y    dstart;    /* linkes Koordinatenpaar von v */
        DX_Y    dend;      /* rechtes Koordinatenpaar von v */
        X_Y     start;     /* linkes Koordinatenpaar von v */
        X_Y     end;       /* rechtes Koordinatenpaar von v */
        int     data;      /* Markierungsflag */
        int     oldnr;     /* alte Knotennummer */
        int     strong;    /* Markierung fuer 0.5 Offset bei S-Visibility */
        double  width;     /* Breite des Knoten */
        double  quotient;  /* Faktor um zur endgueltigen Integerbreite zu kommen */
        int   	up;        /* Zahl der nach oben laufenden Kanten */
        int     down;      /* Zahl der nach unten laufenden Kanten */
        } 
*Node_attr;

typedef struct edge_attr
        {
        Snode   left_face;   /* linkes Gebiet von e */
        Snode   right_face;  /* rechtes Gebiet von e */
        Sedge   dual_edge;   /* die duale Kante von e */
        DX_Y    dstart;      /* unteres Koordinatenpaar von e */
        DX_Y    dend;        /* oberes Koordinatenpaar von e */
        X_Y     start;       /* unteres Koordinatenpaar von e */
        X_Y     end;         /* oberes Koordinatenpaar von e */
        int     data;        /* Markierungsflag */
        double  width;       /* Breite des Kante */
	int     type;        /* Kantentyp */
	Snode   source;      /* der Ursprung der augerichteten Kante */
	Snode   target;      /* das Ziel der augerichteten Kante */
        } 
*Edge_attr;

/*-------------------------- Makrodefinitionen -----------------------------*/

#define TEST_UNIQUE_EDGE(e) ((e->snode->graph->directed)||(unique_edge(e)))

#define OTHER_NODE(n,e) \
        (iif(e->snode == n,e->tnode,e->snode))
#define HIGH_NODE(e) \
	( iif(e->snode->y < e->tnode->y,e->tnode,e->snode))
#define LOW_NODE(e) \
	( iif(e->snode->y < e->tnode->y,e->snode,e->tnode))

#define XY_GRAPH_ATTRS(g) \
        (attr_data_of_type(g,Graph_attr))
#define XY_GRAPH_GRAPH(g) \
        (XY_GRAPH_ATTRS(g)->dual_graph)
#define XY_GRAPH_HEIGTH(g) \
        (XY_GRAPH_ATTRS(g)->heigth)
#define XY_GRAPH_WIDTH(g) \
        (XY_GRAPH_ATTRS(g)->width)
#define XY_GRAPH_MAX_NR(g) \
        (XY_GRAPH_ATTRS(g)->max_nr)
#define XY_GRAPH_DUMMY(g) \
        (XY_GRAPH_ATTRS(g)->dummy_edge)
#define XY_GRAPH_START(g) \
        (XY_GRAPH_ATTRS(g)->start)

#define XY_NODE_ATTRS(n) \
        (attr_data_of_type(n,Node_attr))
#define XY_NODE_EDGES(n) \
        (XY_NODE_ATTRS(n)->edges)
#define XY_NODE_DATA(n) \
        (XY_NODE_ATTRS(n)->data)
#define XY_NODE_OLDNR(n) \
        (XY_NODE_ATTRS(n)->oldnr)
#define XY_NODE_STRONG(n) \
        (XY_NODE_ATTRS(n)->strong)
#define XY_NODE_START(n) \
        (XY_NODE_ATTRS(n)->start)
#define XY_NODE_END(n) \
        (XY_NODE_ATTRS(n)->end)
#define XY_NODE_DSTART(n) \
	( XY_NODE_ATTRS(n)->dstart )
#define XY_NODE_DEND(n) \
	( XY_NODE_ATTRS(n)->dend )
#define XY_NODE_WIDTH(n) \
	( XY_NODE_ATTRS(n)->width )
#define XY_NODE_QUOT(n) \
	( XY_NODE_ATTRS(n)->quotient )
#define XY_NODE_UP(n) \
	( XY_NODE_ATTRS(n)->up )
#define XY_NODE_DOWN(n) \
	( XY_NODE_ATTRS(n)->down )

#define XY_EDGE_ATTRS(e) \
        (attr_data_of_type(e,Edge_attr))
#define XY_EDGE_LNODE(e) \
        (XY_EDGE_ATTRS(e)->left_face)
#define XY_EDGE_RNODE(e) \
        (XY_EDGE_ATTRS(e)->right_face)
#define XY_EDGE_EDGE(e) \
        (XY_EDGE_ATTRS(e)->dual_edge)
#define XY_EDGE_DATA(e) \
        (XY_EDGE_ATTRS(e)->data)
#define XY_EDGE_START(e) \
        (XY_EDGE_ATTRS(e)->start)
#define XY_EDGE_END(e) \
        (XY_EDGE_ATTRS(e)->end) 
#define XY_EDGE_DSTART(e) \
	( XY_EDGE_ATTRS(e)->dstart ) 
#define XY_EDGE_DEND(e) \
	( XY_EDGE_ATTRS(e)->dend )
#define XY_EDGE_WIDTH(e) \
	( XY_EDGE_ATTRS(e)->width )
#define XY_EDGE_TYPE(e) \
        (XY_EDGE_ATTRS(e)->type)  
#define XY_EDGE_SOURCE(e) \
        (XY_EDGE_ATTRS(e)->source)  
#define XY_EDGE_TARGET(e) \
        (XY_EDGE_ATTRS(e)->target)  

/*   der Kantentyp  */
#define DUMMY	0
#define REALLY	1

/*   der Kantentyp im cylindric layout */
#define NORMAL		1
#define BACKEDGE	2
#define TEST		3

/*   der Zeichentyp  */
#define TARJAN		0
#define TAMASSIA	1

/*   der Polylinetyp  */
#define CENTER	0
#define BETTER	1

/*   ST-Edge Option  */
#define ANYEDGE	0
#define CLEVER	1
#define BEST	2

/*   mit und ohne Largest Face */
#define ANYFACE		0
#define LARGESTFACE 	1

/*   zum Berechnen der GraphEd Koordinaten */
#define	SET	1
#define NODE	2
#define EDGE	3

/*  der Name des Algorithmus */
#define OTTEN	1
#define TAMAS	2
#define TARJ	3
#define WISMATH 4

extern int init_node_and_edges(Sgraph g, int face, int greedy, int stedge);
extern void tarjan_draw(Sgraph g, int face, int mode, int verbose);
extern void tarjan_set(Sgraph g, int x_gridsize, int y_gridsize, int node_height, int verbose);
extern void free_node_and_edges(Sgraph g);
extern void polyline(Sgraph g, int mode, int x_gridsize, int y_gridsize, int node_height);
extern void my_st_number(Sgraph graph);
extern Sgraph  lface(Sgraph g);

/****************************************************************************/
/***                      END OF FILE: TARJANMOD.H                        ***/
/****************************************************************************/
