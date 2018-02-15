/* (C) Universitaet Passau 1986-1994 */
/**********************************************************************************************/
/*                                                                                            */
/* FILE: rt_def.h                                                                             */
/* =====                                                                                      */
/*                                                                                            */
/* Inhalt:  Die Typen und Variablendeklarationen fuer den                                     */
/*          modifizierten RT-Algorithmus                                                      */
/*                                                                                            */
/* Bemerkung:  Laden nach 'std.h' und 'sgraph.h'                                              */
/*                                                                                            */
/**********************************************************************************************/


typedef struct bin_tree {
  struct bin_tree        *left_son, *right_son;   /* Soehne des Knoten                        */
  struct bin_tree        *father;                 /* Vater des Knoten                         */
  int                    xcoord,ycoord;           /* RT-Koordinaten des Knoten                */
  int                    offset;                  /* hor. Entfernung zu den Soehnen           */
  int                    width_left,width_right;  /* Platzbedarf der Teilbaeume               */
  Snode                  node;                    /* zugeh. Knoten im Eingabegraphen          */
  bool                   thread;                  /* wird gesetzt, falls der Knoten ein Blatt */
                                                  /* ist, aber 'left_son' bzw. 'right_son'    */
                                                  /* als Konturzeiger im Algorithmus benutzt  */
                                                  /* wird.                                    */
  int                    old_offset;              /* Sicherung bei Korrektur                  */
  bool                   old_thread;              /* Sicherung bei Korrektur                  */
  struct bin_tree        *l_thread, *r_thread;    /* Sicherung bei Korrektur                  */
} *Bin_Tree;



typedef struct extreme {
  struct bin_tree        *link;
  int                    off;
  int                    level;
} Extreme;



typedef struct window {
  int                    min_x,max_x;
  int                    min_y,max_y;
} RT_Window;




#define empty_bin_tree ((Bin_Tree) nil)
#define new_bin_tree() ((Bin_Tree) malloc(sizeof(struct bin_tree)))



#include <connectivity_tests_old/zusammen.h> /* fuer IsConnected */

extern bool is_binary_tree (Sgraph g);                     /* Definiert in rt_verify.c                */
extern Bin_Tree calc_rt_tree (Sgraph g);                   /* Definiert in rt_convert.c               */
extern void setup (Bin_Tree t, int level, Extreme *rmost, Extreme *lmost, int call_correction);                              /* Definiert in rt_alg.c                   */
extern void modified_rt_algorithm (Bin_Tree t);              /* ----------"----------                   */
extern void calc_new_layout (Bin_Tree t);                    /* Definiert in rt_new_layout.c            */
extern void handling_of_rt_errors (void);                 /* Definiert in rt_err.c                   */
extern void calc_width_rel (Bin_Tree t, int *pw);                     /* Definiert in rt_cor.c                   */
extern void correct_if_necessary (Bin_Tree t, int level, Extreme *rmost, Extreme *lmost);               /* ----------"----------                   */



extern void print_tree (Bin_Tree t);



Global Sgraph input_graph;                 /* Der Graph im Sgraph - Format                    */
Global Bin_Tree tree;                      /* Der Binaerbaum                                  */
Global bool rt_error;                         /* Fehleranzeige                                   */
Global int nr_of_rt_error;                    /* Welcher Fehler ?!                               */
Global bool down;                          /* Richtung des Baumes: == true, von der Wurzel    */
                                           /*                               weg               */
                                           /*                      == false, zur Wurzel hin   */
Global RT_Window rt_window;                   /* Ausmasse des Baumes nach RT-Koordinaten         */
                                           /* Die beiden Fenster werden zur Erstellung des    */
                                           /* neuen Layouts benoetigt.                        */



Global int minsep;                         /* Mindestabstand Wurzel <-> Sohn                  */
Global int max_feasible_difference;        /* Maxixmal zulaessiger Unterschied im Platzbedarf */
                                           /* zwischen linkem und rechtem Teilbaum. Bei       */
                                           /* groeserem Unterschied wird, falls gewuenscht    */
                                           /* der Baum korrigiert.                            */
Global int lower_equal;                    /* == true, Korrektur des Baumes falls keine Ver-  */
                                           /*          schlechterung in der Breite.           */
                                           /* == false, Korrektur des Baumes falls die Breite */
                                           /*           des Baumes abnimmt.                   */
Global int profit;                         /* Gewuenschte offset-Aenderung bei der Korrektur  */
Global int cor_of_tree;                    /* Korrektur gefordert ?                           */
Global int grid_size;                      /* Anzahl der Pixel pro Einheit                    */
