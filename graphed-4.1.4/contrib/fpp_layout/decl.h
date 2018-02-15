/* (C) Universitaet Passau 1986-1994 */
/****************************************************************
 **                                                            **
 **     In diesem File werden die benoetigten Fremddateien     **
 **     eingebunden. Sonst enthaelt es saemtliche Prozedur-    **
 **     koepfe des Programms, die Deklarationen der Typen      **
 **     und globalen Variablen.                                **
 **                                                            **
 ****************************************************************/


/*#include <stdio.h>
#include <math.h>*/
#include <string.h>
#include <ctype.h>
#include <std.h>
#include <sgraph.h>
#include <slist.h>
#include <graphed.h>
#include <xview/xview.h>
#include <xview/panel.h>

#ifndef NULL
#define NULL 0
#endif
#define ABS(x)	((x) < 0 ? (-(x)) : (x))

/****************************************************************
 **                                                            **
 **     Die Typen des Programms:                               **
 **                                                            **
 ****************************************************************/

struct nlist {
	int nr;
	int visited;      /* Marke fuer Kanten, die bereits fuer ein Polygon abgespeichert wurde. */
	int orig;         /* nr Bildet mit dem adjazenten Knoten eine Kante des Original-Graphen. */
	int exterior2;    /* Marke fuer Kante d. Randes vor der Dreieckszerlegung (exterior2 == 1). */
	double angle;     /* Winkel zum adjazenten Knoten (0 <= angle < 2*M_PI). */
	double angle2;    /* Winkel zur Bestimmung von ein- und ausgehenden Kanten. */
	struct fpp_node *node;
	struct nlist *clockw;     /* Im Uhrzeigersinn folgende Nachbarkante. */
	struct nlist *co_clockw;  /* Im Gegenuhrzeigersinn folgende Nachbarkante. */
	struct nlist *s_next; /* Naechster in der zugehoerigen geordnerten Liste (up, dn). */
	struct nlist *next;
};



struct fpp_node {
	int number;
	int x;
	int y;
	int degree;               /* Grad des Knotens. */
	int up_degree;            /* Anzahl der Nachbarn ueber dem Knoten. */
	int dn_degree;            /* Anzahl der Nachbarn unter dem Knoten. */
	int kind;                 /* Art des Knotens bzgl. Regularisation. */
	struct nlist *neigh;      /* Nachbarn (Kanten) des Knoten. */
	struct nlist *clockw;     /* Im Uhrzeigersinn geordnete Kanten. */
	struct nlist *co_clockw;  /* Im Gegenuhrzeigersinn geordnete Kanten. */
	struct nlist *up;         /* Liste der Nachbarn ueber dem Knoten (Uhrzeigersinn). */
	struct nlist *dn;         /* Liste der Nachbarn unter dem Knoten (Gegenuhrzeigers.). */
	double dist;              /* Abstand des Knotens zur "Kontur". */
	int label;
	int x_offset;
	int y_coord;
	struct fpp_node  *placenext;  /* Naechster Knoten der kanonischen Nummerierung. */
	struct fpp_node  *placelast;  /* Letzter Knoten der kanonischen Nummerierung. */
	int processed;            /* Marke der bereits behandelten Knoten in der kan. Numm. */
	int in_cn_list;           /* Marke fuer Elemente der kan. Nummerierungsliste. */
	struct fpp_node  *left;
	struct fpp_node  *right;
	int exterior;              /* Marke fuer Knoten auf der Huelle (exterior == 1). */
	struct fpp_node  *exnext;      /* Naechster Knoten auf der Huelle (Uhrzeigersinn). */
	struct fpp_node  *exlast;      /* Letzter Knoten auf der Huelle. */
	int in;                    /* Zeigt die Seite des "Inneren" des Graphen. */
	int top;                   /* Zeigt, ob d. Knoten auf der li. od. re. Haelfte d. Huelle liegt. */
	struct fpp_node  *ascend;
	struct fpp_node  *descend;
	struct fpp_node  *next;
};


struct location_edge {
	struct fpp_node      *rel_node;
	struct fpp_node          *node1;
	struct fpp_node          *node2;
	struct location_edge *next;
	struct location_edge *last;
};


struct polynode {               /* Knoten eines Polygons. */
	struct fpp_node       *pnode;
	struct polynode   *next;
	struct polynode   *last;
	int               kind;     /* Gibt die Situation d. Knotens f. die Regularisierung an. */
	struct polynode *ascend;    /* Aufsteigende Y-Liste. */
	struct polynode   *descend; /* Absteigende Y-Liste. */
};


struct polygon {                /* Element der Polygonliste. */
	int             nr;
	struct polynode *firstnode;
	struct polynode *firstasc;
	struct polynode *firstdesc;
	struct polygon  *nextpoly;
};


struct  fpp_edge {
	int    node1;
	int    node2;
	int    x1;
	int    x2;
	int    y1;
	int    y2;
	int    length;
	struct fpp_edge   *next;
};


/****************************************************************
 **                                                            **
 **     Die Prozedurkoepfe des Programms:                      **
 **                                                            **
 ****************************************************************/
 
struct fpp_node *search_node (int n);
void fill_neighbors (void);
extern void add_to_user_menu (char *string, GraphEd_Menu_Proc proc);

int fpp_window (void);


void take_graph (Sgraph sgraph);
void sort_neighbors (void);
void sort_neighbors2 (void);
double angle (struct fpp_node *node1, struct fpp_node *node2);

void sort_y_coordinates (void);
void regularisation (Sgraph sgraph);
int at_left_of_edge (struct fpp_node *v, struct fpp_node *n1, struct fpp_node *n2);
void init_location_list (struct fpp_node *n);
void insert_edges_in_location_list (struct fpp_node *n, struct location_edge *ll, int direction);
struct location_edge *delete_edges_in_location_list (struct fpp_node *n, struct location_edge *ll);
void regular_one_direction (Sgraph sgraph, struct fpp_node *n, int direction);
void exterior_face_situation (void);
int exterior_edge (struct fpp_node *n, int direction);

void decomposition (Sgraph s);
void mark_exterior_face (void);
void store_polygon (struct fpp_node *nd, struct nlist *nei);
struct nlist *left_neigh_at_nei (struct fpp_node *nd, struct nlist *nei);
struct fpp_node *min_node (struct fpp_node *node);
struct fpp_node *max_node (struct fpp_node *node);
void merge_chains_of_polygon (struct polygon *plist);
void adjust_grid (void);
int adjust_grid_cp (void);
void find_origin (Sgraph sgraph);

void triangulation (Sgraph sgraph);
void polygon_triangulation (Sgraph sgraph, struct polygon *plist);
int convex_angle (int right_chain, struct fpp_node *v, struct fpp_node *n1, struct fpp_node *n2);


void add_edge (Sgraph sgraph, int nr1, int nr2);
void color_switch (Sgraph sgraph);
void initialisation (void);
int is_neighbor (struct fpp_node *contnode, struct fpp_node *testnode);
void contour_neighbors (struct fpp_node *testnode);
void canonical_numbering (void);
int placing_test (struct nlist *neighbor, struct fpp_node *process);
void nejia_assila_idea (int sum);
void stretching (int kind);
void accumulate_offsets (struct fpp_node *htree, int offset);
void fpp_algorithm (int kind);

void change_y_coord_with_y (void);
void make_row (void);
double bound (struct fpp_node *n, struct fpp_node *n1, struct fpp_node *n2, double np);
void compress (Sgraph s, int kind);
double test_exterior_nodes (struct fpp_node *firstdesc, struct fpp_node *h_row, double newpoint, int sec);
void update_exterior_face_situation (int sec);
void change_coordinates (void);
void zoom (Sgraph s);
void store_last_graph (void);
int test_changes (void);
void make_exterior_face_path (void);
void make_the_graph (Sgraph sgraph);
void find_grid_origin (void);

void free_the_graph (struct fpp_node *nd);
void free_neigh (struct nlist *ng);
void free_polygon (struct polygon *p);
void free_polynode (struct polynode *pn, struct polygon *p);




/*********************************************************
 **                                                     **
 **    Die globale Variablen des Programms:             **
 *                                                      **
 *********************************************************/
 
struct fpp_node *graph, *pgraph, *lastgra,
            *firstdesc, *firstasc, 
            *cn_list,        /* Liste der kanonischen Numerierung. */
            *first, *second, *third, 
            *place, *minele, *help1, *help2,
            *bintree, *htree, *firstcont, *lastcont, *prelast,
            *knode1, *knode2, *fc, *lc, *pl;
 
/*struct nlist *h;             Hilfsliste. */ 
            
int gsize;                  /* Anzahl der Knoten. */
int no_graph;
int grid;                   /* Gitterweite. */
int plan;                   /* Marke zum Pruefen der planaren Einbettung. */
int new_zero_x, new_zero_y; /* Koordinaten des neuen "Nullpunkts". */
int k, l, m, n, count;
int pcounter;

struct fpp_edge    *trian, *ht, testedge, minedge;

struct nlist *hneigh, *hneigh1, *hneigh2;

struct location_edge *location_list;

struct polygon *polygonlist, *plist, *exterior;


#include "fpp_layout_export.h"
