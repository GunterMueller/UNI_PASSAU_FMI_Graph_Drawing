/****************************************************************************/
/***                                                                      ***/
/*** Filename: DUALMOD.H                                                  ***/
/***                                                                      ***/
/****************************************************************************/

/*-------------------------- Strukturdefinitionen --------------------------*/

typedef struct dual_graph_attr
        {	
        Sgraph  dual_graph;  /* Zeiger auf den Originalgraphen */
        int     heigth;      /* Hoehe des Dualgraphen */
        int     max_nr;      /* groesste Knotennummer */
        } 
*Dual_graph_attr;

typedef struct dual_node_attr
        {	
        Slist   node_boundary; /* die Knoten auf dem Rand des Gebietes */
        Slist   edge_boundary; /* die Kanten auf dem Rand des Gebietes */
        Snode   high;          /* Knoten mit hoechster Nummer auf dem Rand */
        Snode   low;           /* Knoten mit kleinster Nummer auf dem Rand */
        Slist   left_path;     /* linker Pfad von low nach high */
        Slist   right_path;    /* rechter Pfad von low nach high */
	Snode	father;        /* Vater im BFS */
	int     data;          /* Markierungsflag */
        int     type;          /* Typ im cylindric Layout */
        } 
*Dual_node_attr;

typedef struct dual_edge_attr
        {
	Snode   source;      /* der Ursprung der ausgerichteten Kante */
	Snode   target;      /* das Ziel der ausgerichteten Kante */
        Sedge   dual;        /* die Erzeugerkante im Originalgraph */
        } 
*Dual_edge_attr;
/*-------------------------- Makrodefinitionen -----------------------------*/

#define DUAL_GRAPH_ATTRS(g) \
        (attr_data_of_type(g,Dual_graph_attr))
#define DUAL_GRAPH_GRAPH(g) \
        (DUAL_GRAPH_ATTRS(g)->dual_graph )
#define DUAL_GRAPH_HEIGTH(g) \
        (DUAL_GRAPH_ATTRS(g)->heigth )
#define DUAL_GRAPH_MAX_NR(g) \
        (DUAL_GRAPH_ATTRS(g)->max_nr )

#define DUAL_NODE_ATTRS(n) \
        (attr_data_of_type(n,Dual_node_attr))
#define DUAL_NODE_BOUND(n) \
        (DUAL_NODE_ATTRS(n)->node_boundary )
#define DUAL_NODE_BOUNDEDGE(n) \
        (DUAL_NODE_ATTRS(n)->edge_boundary )
#define DUAL_NODE_HIGH(n) \
        (DUAL_NODE_ATTRS(n)->high)
#define DUAL_NODE_LOW(n) \
        (DUAL_NODE_ATTRS(n)->low)
#define DUAL_NODE_R_PATH(n) \
        (DUAL_NODE_ATTRS(n)->right_path)
#define DUAL_NODE_L_PATH(n) \
        (DUAL_NODE_ATTRS(n)->left_path)
#define DUAL_NODE_FATHER(n) \
        (DUAL_NODE_ATTRS(n)->father)
#define DUAL_NODE_DATA(n) \
        (DUAL_NODE_ATTRS(n)->data)
#define DUAL_NODE_TYPE(n) \
        (DUAL_NODE_ATTRS(n)->type)

#define DUAL_EDGE_ATTRS(e) \
        (attr_data_of_type(e,Dual_edge_attr)) 
#define DUAL_EDGE_SOURCE(e) \
        (DUAL_EDGE_ATTRS(e)->source)  
#define DUAL_EDGE_TARGET(e) \
        (DUAL_EDGE_ATTRS(e)->target)  
#define DUAL_EDGE_EDGE(e) \
        (DUAL_EDGE_ATTRS(e)->dual)     

extern int make_dual_graph(Sgraph g);
extern void free_dual_graph(Sgraph g);
extern int my_test_st_number(Sgraph graph);

/****************************************************************************/
/***                    END OF FILE: DUALMOD.H                            ***/
/****************************************************************************/
