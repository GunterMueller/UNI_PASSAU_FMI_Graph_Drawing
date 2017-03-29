/****************************************************************************/
/***                                                                      ***/
/*** Filename: BLOCKMOD.H                                                 ***/
/***                                                                      ***/
/****************************************************************************/

/*-------------------------- Strukturdefinitionen --------------------------*/ 

typedef struct cyl_graph_attr
        {	
        Slist   cutnodes;     /* die Liste der Artikulationen */
        Slist   components;   /* die Liste der Bloecke */
        Sgraph  bab;          /* der Block-Artikulationsbaum */
        int     nr_of_comp;   /* die Anzahl der Bloecke */
        int     height;	      /* Hoehe des Graphen */
        int     width;        /* Breite des Graphen */
        } 
*Cyl_Graph_attr;

typedef struct  cyl_node_attr
        {	
        int     nr;           /* die alte Nummer */
        int     is_cut_node;  /* Flag, ob der Knoten eine Artikulation ist */
        Snode   p;            /* der Vorgaenger im DFS */
        int     L;	      /* Funktionwert im DFS */
        } 
*Cyl_Node_attr;

typedef struct  cyl_edge_attr
        {
        int     u;	      /* Marke im DFS */
        } 
*Cyl_Edge_attr;

typedef struct  bab_node_attr
        {	
        Sgraph  block;        /* die Sgraph-Struktur, falls v ein Block ist */
        Snode   cut;          /* die Snode-Struktur, falls v ein Cut ist */
        int     is_cut_node;  /* Flag, ob der Knoten eine Artikulation ist */
        int     leave;        /* Flag, ob v im Ba-B ein Blatt ist */
        } 
*Bab_Node_attr;

typedef struct  bab_edge_attr
        {
        int     marked;	      /* Marke im Pfadtest */
        } 
*Bab_Edge_attr;

/*-------------------------- Makrodefinitionen -----------------------------*/

#define OTHER_NODE(n,e) \
        (iif(e->snode == n,e->tnode,e->snode))

#define CY_GRAPH_ATTRS(g) \
        (attr_data_of_type(g,Cyl_Graph_attr))
#define CY_GRAPH_CUTS(g) \
        (CY_GRAPH_ATTRS(g)->cutnodes )
#define CY_GRAPH_COMP(g) \
        (CY_GRAPH_ATTRS(g)->components )
#define CY_GRAPH_GRAPH(g) \
        (CY_GRAPH_ATTRS(g)->bab )
#define CY_GRAPH_COMP_NR(g) \
        (CY_GRAPH_ATTRS(g)->nr_of_comp )
#define CY_GRAPH_WIDTH(g) \
        (CY_GRAPH_ATTRS(g)->width )
#define CY_GRAPH_HEIGHT(g) \
        (CY_GRAPH_ATTRS(g)->height )

#define CY_NODE_ATTRS(n) \
        (attr_data_of_type(n,Cyl_Node_attr))
#define CY_NODE_NR(n) \
        (CY_NODE_ATTRS(n)->nr )
#define CY_NODE_P(n) \
        (CY_NODE_ATTRS(n)->p )
#define CY_NODE_CUT(n) \
        (CY_NODE_ATTRS(n)->is_cut_node )
#define CY_NODE_L(n) \
        (CY_NODE_ATTRS(n)->L )

#define CY_EDGE_ATTRS(e) \
        (attr_data_of_type(e,Cyl_Edge_attr))
#define CY_EDGE_U(e) \
        (CY_EDGE_ATTRS(e)->u )

#define BAB_NODE_ATTRS(n) \
        (attr_data_of_type(n,Bab_Node_attr))
#define BAB_NODE_BLOCK(n) \
        (BAB_NODE_ATTRS(n)->block )
#define BAB_NODE_CUT(n) \
        (BAB_NODE_ATTRS(n)->cut )
#define BAB_NODE_IS_CUT(n) \
        (BAB_NODE_ATTRS(n)->is_cut_node )
#define BAB_NODE_LEAVE(n) \
        (BAB_NODE_ATTRS(n)->leave )  
 
#define BAB_EDGE_ATTRS(e) \
        (attr_data_of_type(e,Bab_Edge_attr))
#define BAB_EDGE_MARKED(e) \
        (BAB_EDGE_ATTRS(e)->marked )
                            
/*   mit und ohne Largest Face */
#define ANYFACE		0
#define LARGESTFACE 	1 

/* Typ des dualen Knotens im Cylindric Layout */
#define	FACE		0 
#define	F1		1 
#define	F2		2 
#define MARKED  	3  
#define UNORIENTED  	4    
                
extern void init_visinode_attrs(Snode n);
       
/****************************************************************************/
/***                      END OF FILE: BLOCKMOD.H                         ***/
/****************************************************************************/
