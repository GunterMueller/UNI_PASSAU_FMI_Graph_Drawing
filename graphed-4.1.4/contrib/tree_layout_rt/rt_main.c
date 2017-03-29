/* (C) Universitaet Passau 1986-1994 */
/**********************************************************************************************/
/*                                                                                            */
/* FILE: rt_main.c                                                                            */
/* =====                                                                                      */
/*                                                                                            */
/* Inhalt:  Das Hauptprogramm fuer rt.                                                        */
/*                                                                                            */
/* Bemerkung:                                                                                 */
/*                                                                                            */
/*   enthaltene Funktionen: 'print_tree (t)'                                                  */
/*                          'print_graph_attributes (file,g)'                                 */
/*                          'print_node_attributes (file,n)'                                  */
/*                          'print_edge_attibutes (file,e)'                                   */
/*                          'free_bin_tree (t)'                                               */
/*                          'init_var (ms,cot,mfd,le,ppu)'                                    */
/*                          'atoi (s)'                                                        */
/*                          'main (argc,argv)'                                                */
/*                                                                                            */
/*   benutzte Funktionen:  'load_graph ()'                                                    */
/*                         'is_binary_tree ()'                                                */
/*                         'calc_rt_tree ()'                                                  */
/*                         'modified_rt_algorithm ()'                                         */
/*                         'calc_new_layout ()'                                               */
/*                         'print_graph ()'                                                   */
/*                                                                                            */
/*   benutzte Variablen:  'input_graph'                                                       */
/*                        'tree'                                                              */
/*                        'rt_error'                                                          */
/*                        'nr_of_rt_error'                                                    */
/*                        'minsep'                                                            */
/*                        'cor_of_tree'                                                       */
/*                        'max_feasible_difference'                                           */
/*                        'lower_equal'                                                       */
/*                        'grid_size'                                                         */
/*                        'profit'                                                            */
/*                        'down'                                                              */
/*                        'rt_window'                                                         */
/*                                                                                            */
/**********************************************************************************************/



#include "std.h"
#include "sgraph.h"
#include "slist.h"
#include "graphed.h"

#include "rt_def.h"

extern int	saved_rt_minsep;
extern int	saved_rt_correction_of_tree;
extern int	saved_rt_feasible_difference;
extern int	saved_rt_lower_equal;
extern int	saved_rt_pixel_per_unit;
extern	void	show_rt_subframe (void);


void print_tree (Bin_Tree t)
           
/**********************************************************************************************/
/* Eine Hilfsprozedur, die die Entwicklung des Programms erleichtert hat. Sie gibt die Daten  */
/* eines Baumes aus.                                                                          */
/**********************************************************************************************/
{
  if (t != empty_bin_tree)
    {
      printf ("%10d%10d%10d%10d%10d%10d%10d%10d%2s",
              t->node->nr,t->xcoord,t->ycoord,
                          t->offset,
                          t->width_left,t->width_right,
                          t->node->x,t->node->y,"\n");
      print_tree (t->left_son);
      print_tree (t->right_son);
    }
}




/**********************************************************************************************/
/* Hier stehen die Ausgabefunktionen fuer Graph-, Knoten- und Kantenattribute, die die Funk-  */
/* tion 'print_graph ()' als Parameter benoetigt.                                             */
/**********************************************************************************************/

Global	void	print_graph_attributes (FILE *file, Sgraph g)
{
}


Global	void	print_node_attributes (FILE *file, Snode n)
{
	if (n->x > 0 && n->y > 0)
		fprintf (file, "{$ %d %d $}", n->x, n->y);
}


Global	void	print_edge_attributes (FILE *file, Sedge e)
{
}




void free_bin_tree (Bin_Tree t)
           
/**********************************************************************************************/
/* Rekursives Loeschen des Baumes t. Diese wichtige Prozedure sollte bei einer Anpassung an   */
/* GRAPHED nicht vergessen werde !!!                                                          */
/**********************************************************************************************/
{
  Bin_Tree l,r;

  if (t != empty_bin_tree)
    {
      l = t->left_son;
      r = t->right_son;
      t->left_son = nil;
      t->right_son = nil;
      t->father = nil;

      free_bin_tree (l);
      free_bin_tree (r);
      
      free (t);
    }
}




void init_var (int ms, int cot, int mfd, int le, int ppu)
                      
/**********************************************************************************************/
/* Initialisierung der globalen Variablen gemaess der Eingabeparameter. Bei falsch gesetzten  */
/* Parametern werden die Fehlervariablen entsprechend belegt. UNBEDINGT WICHTIG!              */
/**********************************************************************************************/
{
  rt_error = false;
  nr_of_rt_error = 0;

  minsep = ms;
  rt_error = (ms < 1 ? true : false);
  if (!rt_error)
    {
      cor_of_tree = cot;
      max_feasible_difference = mfd;
      rt_error = (mfd < 2 ? true : false);
      if (!rt_error)
        {
          lower_equal = le;
/*
          switch (ppu)
            {
               case 1 : {
                          grid_size = 8;
                          break;
                        }
               case 2 : {
                          grid_size = 16;
                          break;
                        }
               case 3 : {
                          grid_size = 32;
                          break;
                        }
               case 4 : {
                          grid_size = 64;
                          break;
                        }
               case 5 : {
                          grid_size = 128;
                          break;
                        }
               default : {
                           rt_error = true;
                           nr_of_rt_error = 13;
                         }
            }
*/
          grid_size = ppu;
          
          input_graph = empty_graph;
          profit = 1;
          down = true;
   
          rt_window.min_x = rt_window.min_y = 65535;
          rt_window.max_x = rt_window.max_y = 0;
        }
      else
        {
          nr_of_rt_error = 12;
        }
    }
  else
    {
      nr_of_rt_error = 11;
    }
}




void tree_layout_reingold_tilford (Sgraph graph, int grid)
      	      
   	     
/**********************************************************************************************/
/* Steuerung des Programmablaufs:                                                             */
/*   1) Bearbeitung der Kommandozeile bei Aufruf des Programms                                */
/*   2) Initialisierung der globalen Variablen                                                */
/*   3) Einbettung des eingelesen Graphen in die Datenstruktur 'Bin_Tree' in mehreren Schrit- */
/*      ten.                                                                                  */
/*   4) Berechnung des Layouts                                                                */
/*   5) Speichern des Layouts                                                                 */
/*   6) Loeschen des Baumes                                                                   */
/**********************************************************************************************/
{
   int ms,cot,mfd,le,help,ppu;

   ms  = saved_rt_minsep;
   cot = saved_rt_correction_of_tree;
   mfd = saved_rt_feasible_difference;
   le  = saved_rt_lower_equal;
   ppu = saved_rt_pixel_per_unit;
   
   cot = (cot == 0 ? false : true);
   le  = (le == 0 ? false : true);
      
/*init_var (ms,cot,mfd,le,ppu); */
  init_var (ms,cot,mfd,le, grid);  

  if (!rt_error)
    {
       input_graph = graph;

       if (input_graph != empty_sgraph)
         {
           help = is_binary_tree (input_graph);
           if (help)
             {
               if (input_graph->directed)
                 {
                   tree = calc_rt_tree (input_graph);
                   if (!rt_error)
                     {
                       modified_rt_algorithm (tree);
                       calc_new_layout (tree);
                       free_bin_tree (tree);
                     }
                   else
                     {
                       handling_of_rt_errors ();
                     }
                 }
               else
                 {
                   rt_error = true;
                   nr_of_rt_error = 100;
                   handling_of_rt_errors ();
                 }
             }
           else
             {
               handling_of_rt_errors ();
             }
         }
       else
         {
           rt_error = true;
           nr_of_rt_error = 1;
           handling_of_rt_errors ();
         }
    }     
  else
    handling_of_rt_errors ();
}


/************************************************************************/
/*									*/
/*			    Aufruf vom Menue aus			*/
/*									*/
/************************************************************************/


static	void		layout_reingold_tilford (Sgraph_proc_info info)
{

	info->no_structure_changes = TRUE;
	info->save_selection       = TRUE;
		
	if (info->sgraph == empty_sgraph) {
	
		bell ();
		
	} else if (!info->sgraph->directed) {
	
		error ("Graph is not directed\n");
		
	} else {
	
		tree_layout_reingold_tilford (info->sgraph,
			iif (get_gridwidth(wac_buffer) == 0,
			     get_current_node_width (),
			     get_gridwidth(wac_buffer)));
			     
		info->recenter = TRUE;
	}
}


#include <xview/xview.h>

void	menu_layout_reingold_tilford (Menu menu, Menu_item menu_item)
    		      		/* The menu from which it is called	*/
    		           	/* The menu item from ...		*/
{	
	if (event_ctrl_is_down ((Event*)menu_get (menu, MENU_FIRST_EVENT, 0))) {
		show_rt_subframe ();
	} else {
		call_sgraph_proc (layout_reingold_tilford, NULL);
	}
}

