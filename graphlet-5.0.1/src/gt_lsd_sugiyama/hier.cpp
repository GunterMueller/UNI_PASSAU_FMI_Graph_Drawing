/* This software is distributed under the Lesser General Public License */
/* (C) Universitaet Passau 1986-1994 */
/*********************************************************************************/
/*                                                                               */
/*                             H  I  E  R  .  C                                  */
/*                                                                               */
/*********************************************************************************/

#include "sugiyama_export.h"
#include "sgraph/std.h"
#include "sgraph/sgraph.h"
#include "sgraph/slist.h"
#include "sgraph/graphed.h"
#include "math.h"

typedef struct node_list {
	Snode node;
	struct node_list *before, *next;
} *Nodelist;


Nodelist first_node;		/* Zeiger auf den Anfang der Knotenliste 	 */
Nodelist last_node;		/* Zeiger auf das Ende der Knotenliste		 */


/***************** Hilfsfunktion zum Aufbau einer Knotenliste  *******************/
/*************** die Knoten werden am ENDE der Liste angehaengt ******************/
Local void  append_node(Snode node)
{
  	Nodelist new_n;
  	new_n = (struct node_list *)malloc(sizeof(struct node_list));

  	new_n->node      = node;
  	new_n->next      = NULL;  

	if( first_node == NULL )
	{
		new_n->before = NULL;
		first_node    = new_n;
		last_node     = new_n;
	}
	else
	{
		new_n->before   = last_node;
		last_node->next = new_n;
		last_node       = new_n;
	}
	
}
/*********************************************************************************/


/************** bestimmen der Eingangszahl aller Knoten **************************/
Local void get_input_of_node (Sgraph g)
{
	Snode n;
	Sedge e;
	int length_in;

	/* bestimme von allen Knoten die Eingangsanzahl und schreibe das Ergebnis*/
	/* ins Attributfeld							 */
	for_all_nodes( g, n )
	{
		length_in  = 0;
		for_targetlist( n ,e )
		{
			length_in++;
		} end_for_targetlist( n, e );

		attr_flags( n ) = length_in;
	}end_for_all_nodes( g, n);
}
/*********************************************************************************/


/************************ TOPOLOGISCHES SORTIEREN ********************************/
Local Nodelist top_sort (Sgraph g)
{
	Snode n, target;
	Sedge edge;
	Nodelist help_node ;
 
	/*bestimmen der Eingangszahl aller Knoten				 */
	get_input_of_node ( g );
	
	/* konstruiere zunaechst eine Knotenliste, in der nur Knoten stehen, die */
	/* keine Eingaenge besitzen						 */
	for_all_nodes( g, n )
	{
		if( n->tlist == empty_edge )	
		{
			append_node( n );
		}
	} end_for_all_nodes( g, n );

	/* gehe die soeben erzeugte Knotenliste durch: erniedrige von jedem Ziel-*/
	/* knoten die Eingangsanzahl um Eins und ueberpruefe, ob danach die Ein- */
	/* gangsanzahl gleich Null ist; wenn ja, haenge den Knoten an den Schluss*/
	/* der Liste								 */
	help_node = first_node;
	while( help_node != NULL )
	{
		for_sourcelist(	help_node->node, edge )
		{
			target     = edge->tnode;
			/* erniedrige beim Zielknoten die Zahl der Eingaenge um 1*/
			attr_flags( target ) = 	attr_flags( target ) - 1;
			/* ANMERKUNG: Knoten koennen nicht mehrmals in die Liste */
			/* kommen						 */
			if( attr_flags( target ) == 0 )	
			{
				append_node( target );
			}
		} end_for_sourcelist( help_node->node, edge );
		help_node = help_node->next;
	}

	return( first_node );
}
/*********************************************************************************/


/*************************** verteile Levels *************************************/
Local void give_levels (Nodelist n_list)
{
	Sedge edge;
	Snode source, target;
	Nodelist kill;

	first_node = NULL;	/* Zeiger auf den Anfang der Knotenliste	 */
	last_node = NULL;	/* Zeiger auf das Ende der Knotenliste		 */

	while( n_list != NULL )
	{
		source = n_list->node;
		for_sourcelist( source, edge )
		{
			target    = edge->tnode;
			target->y = maximum( target->y, (source->y) + 1 );
			if( target->y > maxlevel)    maxlevel = target->y;
		} end_for_sourcelist( source, edge );
		
		kill   = n_list;
		n_list = n_list->next;
#if __SUNPRO_CC == 0x401
		free ((char*) kill);
#else
		free (kill);
#endif
	}

}
/*********************************************************************************/
/*********************************************************************************/
/*********************************************************************************/
				




/*********************************************************************************/
/*		 Implementierung des Coffman-Graham-Algorithmus			 */
/*                                                                               */
/*         VORAUSSETZUNG: Graph liegt in transitiv reduzierter Form vor          */
/*********************************************************************************/



/*** suche alle Knoten, die vom Quellknoten aus erreichbar sind (rekursiv)    ****/
Local Slist get_targets (Snode source, Slist t_list)
{
	Snode target;
	Sedge edge;

	for_sourcelist( source, edge )
	{
		target = edge->tnode;
		/* add_to_slist prueft automatisch, ob ein Element bereits in    */
		/* der Liste ist						 */
		t_list = add_to_slist( t_list, 
					  make_attr( ATTR_DATA, (char *) target ));
		get_targets( target, t_list );		
	} end_for_sourcelist( source, edge );

	return( t_list );
}
/*********************************************************************************/ 


/* Knoten, die in der Zielknotenliste stehen, werden zunaechst nicht als erreich-*/
/* bar eingestuft (koennen aber spaeter in die Liste aufgenommen werden)         */
Local Slist get_transitive_targets (Snode source)
{
	Snode target;
	Slist targetlist = empty_slist, actual_t_nodes, t_nodes = empty_slist;
	Sedge edge;

	for_sourcelist( source, edge )
	{
		target         = edge->tnode;

		/* erreichbare Knoten dieser Kante				 */
		actual_t_nodes = get_targets( target, targetlist );

		/* insgesamt bisher erreichbare Knoten dieses Quellknotens	 */
		t_nodes        = add_slists( t_nodes, actual_t_nodes );	

	} end_for_sourcelist( source, edge );

	return( t_nodes );
}
/*********************************************************************************/ 

/************** bilde Erreichbare-Knoten-Liste ***********************************/
Local void make_targetlist (Sgraph graph)
{
	Snode source;
	Slist reachable_targets, l;
	Snode node;

	for_all_nodes( graph, source )
	{
		reachable_targets = get_transitive_targets( source );
		set_nodeattrs( source, make_attr( ATTR_DATA, 
		
				      (char *)reachable_targets ));

		for_slist( reachable_targets, l )
		{	
			node         = attr_data_of_type( l, Snode );
		} end_for_slist( reachable_targets, l );

	} end_for_all_nodes( graph, source );
}
/*********************************************************************************/
	

/********************** mache transitiven Graphen ********************************/
/* Voraussetzung: keine Zyklen (ist erfuellt)					 */
Local void transitive_reduction (Sgraph graph)
{
	Snode node, target;
	Sedge edge;
	Slist reachable_targets, list;

	/* erzeuge Liste aller erreichbaren Knoten			 	 */
	make_targetlist( graph );
	/* jeder Knoten hat nun im Attributfeld die Liste seiner erreichbaren 	 */
	/* Knoten stehen							 */

	for_all_nodes( graph, node )
	{
		reachable_targets = attr_data_of_type( node, Slist );		

		for_sourcelist( node, edge )
		{
			target = edge->tnode;
			list = contains_slist_element( reachable_targets,
					make_attr( ATTR_DATA, (char *)target));
			/* ist Zielknoten in der Liste?				 */
			if( list != empty_slist ) 
			{
				/* wenn ja, so markiere die Kante		 */
				attr_flags( edge ) = 1;
			}
		} end_for_sourcelist( node, edge );
	
	} end_for_all_nodes( graph, node );

}
/*********************************************************************************/	


/********************** suche groesste Labelnummer *******************************/
Local int get_max_label (Slist node_list)
{
	int max_label = 0, actual_label;
	Slist l;
	Snode node;

	for_slist( node_list, l )
	{
		node         = attr_data_of_type( l, Snode );
		actual_label = attr_data_of_type( node, int );
	
		/* die x-Koordinate gibt an, ob der Knoten zulaessig ist 	 */
		if( (actual_label > max_label) && (node->x == 0 ) )	
						max_label = actual_label;
	} end_for_slist( node_list, l );

	return( max_label );
}
/*********************************************************************************/

				
/******************** suche Knoten mit uebergebenen Label ************************/
Local Snode get_node_with_max_label(int label, Slist list)
{
	Slist l;
	Snode node, searched_node;
	int actual_label;

	for_slist( list, l )
	{
		node         = attr_data_of_type( l, Snode );
		actual_label = attr_data_of_type( node, int );

		if( actual_label == label ) 	searched_node = node;
	} end_for_slist( list, l );

	return( searched_node );
}
/*********************************************************************************/


/********** ueberpruefe, ob die Zielknoten des uebergebenen Knotens alle *********/
/********** in der uebergebenen Liste stehen				 *********/
Local int proove_condition(Snode node, Slist list)
{
	Snode target;
	Sedge edge;
	Slist proove;
	int well = 1;

	/* falls Quellknoten keine Ausgaenge besitzt				 */
	if( node->slist ==  NULL ) 	well = 1;

	for_sourcelist( node, edge )
	{
		/* pruefe nur, wenn Kante nicht markiert ist			 */
		if( attr_data_of_type( edge, int ) != 1 )
		{
			target = edge->tnode;
			proove = contains_slist_element( list,
					make_attr( ATTR_DATA, (char *)target));
			/* ist Zielknoten nicht in der Liste?			 */
			if( proove == empty_slist ) 	well = 0;
		}
	} end_for_sourcelist( node, edge );

	return( well );
}
/*********************************************************************************/	
					

/************* markiere uebergebenen Knoten: setze x-Wert auf 1 ******************/
Local void mark_x (Snode node)
{
	node->x = 1;
}
/*********************************************************************************/


/*** waehle entsprechenden Knoten: Kriterium: hoechste Labelnummer		 */
/***				   Bedingung: Zielknoten muessen alle in der 	 */
/***					      zweiten uebergebenen Liste stehen  */
Local Snode get_special_node(Slist possible_nodes, Slist condition_list)
{
	int max_label, ok = 0;
	Snode node;

	while( ok != 1 )
	{
		/* hole groessten Label						 */
		max_label = get_max_label( possible_nodes );

		/* suche Knoten mit groesstem Label				 */
		node = get_node_with_max_label( max_label, possible_nodes );
	
		/* ueberpruefe Bedingung: Zielknoten von node muessen in         */
		/* condition_list stehen					 */
		ok        = proove_condition( node, condition_list );

		/* falls weitergesucht werden muss, darf node nicht noch einmal  */
		/* gewaehlt werden   -> Markieren von node			 */
		if( ok == 0 )	mark_x( node );
	}

	return( node );
}
/*********************************************************************************/


/************* loesche Markierungen in den x-Koordinaten *************************/
Local void delete_x_marks (Slist node_list)
{
	Slist l;
	Snode node;

	for_slist( node_list, l )
	{
		node    = attr_data_of_type( l, Snode );
		node->x = 0;
	} end_for_slist( node_list, l );
}
/*********************************************************************************/


/************** errechne "optimale"(subjektiv) Breite des Graphen ****************/
Local int get_best_width(int nr_of_nodes, int depth)
{
	int opt_width;

	/* je groesser das Verhaeltnis "depth" zu "width" wird, desto schmaler   */
	/* und tiefer wird der Graph						 */
	opt_width = (int)ceil( sqrt( ((double)sugiyama_settings.width*nr_of_nodes/depth) ) );		

	return( opt_width );
}
/*********************************************************************************/


/******** pruefen, ob Zielknoten von node im aktuellen Level sind ****************/
Local int targets_in_actual_level (Snode node, int act_level)
{
	Snode target;
	Sedge edge;
	int change = 0;

	for_sourcelist( node, edge )
	{
		if( attr_data_of_type( edge, int ) != 1 )
		{
			target = edge->tnode;
			if( target->y == act_level )	change = 1;
		}
	} end_for_sourcelist( node, edge );

	return( change );
}
/*********************************************************************************/


/**************** Zuordnung der Knoten in bestimmte Levels ***********************/
Local int put_first_levels (Slist node_list, Slist not_level_nodes)
{
	int levelnr = 1, max_node_in_level, count_nodes_in_level = 0;
	int node_nr, levelled_node_nr = 0, depth = 1, new_level;
	Snode actual_node;
	Slist levelled_nodes = empty_slist;

	node_nr = size_of_slist( node_list );

	/* bestimme die optimale Breite des Graphen				 */
	max_node_in_level = get_best_width( node_nr, depth );

	while( node_nr != levelled_node_nr )
	{
		actual_node = get_special_node( not_level_nodes, levelled_nodes );
	
 		/* Markierungen in den x_Koordinaten loeschen			 */
		delete_x_marks( not_level_nodes );
	
		/* pruefen, ob Zielknoten von actual_node im aktuellen Level sind*/
		new_level = targets_in_actual_level( actual_node, levelnr );

		/* Level entsprechend eintragen					 */
		if( (count_nodes_in_level < max_node_in_level)&&(new_level == 0) )
		{
			actual_node->y = levelnr;
			count_nodes_in_level++;
		}
		else
		{
			levelnr++;
			actual_node->y = levelnr;
			count_nodes_in_level = 1;
		}
		
		/* Listen aktualisieren						 */
		levelled_nodes = add_to_slist( levelled_nodes,
				   make_attr( ATTR_DATA, (char *) actual_node ));
		not_level_nodes = subtract_from_slist( not_level_nodes,
				   make_attr( ATTR_DATA, (char *) actual_node ));

		levelled_node_nr++;
	}
	
	maxlevel = levelnr;

	return( levelnr );
}
/*********************************************************************************/

/********************* berichtige Leveleintraege *********************************/
Local void put_right_levels(Sgraph g, int max)
{
	Snode node;
	int old_level;

	for_all_nodes( g, node )
	{
		/* Level 1 wird zu max_level, Level 2 zu max_level-1, usw	 */
		old_level = node->y;
		node->y   = (max - old_level);
	} end_for_all_nodes( g, node );
}
/*********************************************************************************/


/******************* ersten Labelwert zuteilen ***********************************/
Local void first_label (Sgraph g)
{
	Snode node;
	int ok = 0;

	for_all_nodes( g, node )
	{
		if( ok == 0 )
		{
			if( node->tlist == NULL )
			{
				set_nodeattrs( node, make_attr(ATTR_FLAGS, 1));
				ok = 1;
			}
		}
	} end_for_all_nodes( g, node );
}
/*********************************************************************************/


/************** bestimme Minimum der Labelwerte der Zielknoten *******************/
Local int get_min_label (Snode node)
{
	int min = 1000000;
	Sedge edge;
	Snode source;

	for_targetlist( node, edge )
	{
		if( attr_data_of_type( edge, int ) != 1 )
		{
			source = edge->snode;
			if( attr_data_of_type( source, int ) < min )
					min = attr_data_of_type( source, int );
		}
	} end_for_targetlist( node, edge );
	
	return( min );
}
/*********************************************************************************/


/*********************** Labelwerte zuteilen *************************************/
Local void labeling (Sgraph g)
{ 
	Snode node, label_node;
	Slist tmp;
	int label = 2, node_nr, min, act_min;
	
	/* Initialisierung							 */							
	for_all_nodes( g, node )
	{
		set_nodeattrs( node, make_attr(ATTR_FLAGS, 1000000));
		/* 1000000 entspricht unendlich	 */
	} end_for_all_nodes( g, node );

	first_label( g );

	tmp = make_slist_of_sgraph( g  );
	node_nr = size_of_slist( tmp );
	free_slist( tmp );

	if( node_nr > 1 )
	{
		while( label != node_nr )
		{
			min = 1000000;
			for_all_nodes( g, node )
			{	
				if( attr_data_of_type( node, int ) == 1000000 )
				{
					act_min = get_min_label( node );
					if( act_min <= min )
					{
						min        = act_min;
						label_node = node;
					}
				}
			} end_for_all_nodes( g, node );

			set_nodeattrs( label_node, make_attr(ATTR_FLAGS, label ));

			label++;
		}
	}
}
/*********************************************************************************/		


/**************** Coffman-Graham-Algorithmus *************************************/
Local void coffman_graham (Sgraph g)
{
	Slist all_nodes, not_levelled_nodes;
	int max_level;

	/* verteile die Labelwerte						 */
	labeling( g );

	/* initialisiere benoetigte Listen					 */
	all_nodes          = make_slist_of_sgraph( g );
	not_levelled_nodes = make_slist_of_sgraph( g );	

	/* verteile die Levels							 */
	max_level = put_first_levels( all_nodes, not_levelled_nodes );

	/* drehe die Levels um							 */
	put_right_levels( g, max_level );
}
/*********************************************************************************/
/*********************************************************************************/
/*********************************************************************************/


Local void set_level(Snode n, int i)
        
      

/* weist dem Knoten n das Level i zu, falls er nicht schon ein groesseres Level hat */
/* Die direkten Nachfolger von n bekommen rekursiv das Level i+1 */

{
	Sedge e;
	if (i > maxlevel)
		maxlevel = i;
	if (level(n) <= i)	
	{
		level(n) = i;
		for_sourcelist(n,e)
			set_level(e->tnode,i+1);
		end_for_sourcelist(n,e);
	}
}

					
Local void prepare(Sgraph g)
         

/* Initialisierungen im Graph g */

{
	Snode n;
	Sedge e;
	for_all_nodes(g,n)
		attr_flags(n) = 0;
		n->y = 0;
		for_sourcelist(n,e)
			attr_flags(e) = 0;
		end_for_sourcelist(n,e);
	end_for_all_nodes(g,n);
}


Local void prepare1(Sgraph g)
         

/* Initialisierungen im Graph g */

{
	Snode n;
	Sedge e;
	for_all_nodes(g,n)
		n->x = 0;
		for_sourcelist(n,e)
			set_edgeattrs( e, make_attr(ATTR_FLAGS, 0));
		end_for_sourcelist(n,e);
	end_for_all_nodes(g,n);
}


Local void prepare2(Sgraph g)
         

/* Initialisierungen im Graph g */

{
	Snode n;
	Sedge e;
	for_all_nodes(g,n)
	set_nodeattrs( n, make_attr(ATTR_FLAGS, 0));	
		for_sourcelist(n,e)
			set_edgeattrs( e, make_attr(ATTR_FLAGS, 0));	
		end_for_sourcelist(n,e);
	end_for_all_nodes(g,n);
}

Local void mark_as_dummy(Snode n)
{
	attr_flags(n) = 1;
}


Global int is_dummy(Snode node)
{
	return (attr_flags(node) == 1);
}


Local void mark(Sedge e)
        

/* markiert eine Kante (um sie spaeter zu loeschen) */

{
	attr_flags(e) = 1;
}


Local int marked(Sedge e)
{
	return(attr_flags(e) == 1);
}


Local void delete_marked_edges(Snode n)
{
	
	int weiter;
	Sedge e, e1;
	weiter = (((e1) = (n)->slist) != empty_edge);
	while (weiter) {
		e = e1;
		weiter = ((e1 = (e)->ssuc) != (n)->slist); 
		if (marked(e)) remove_edge(e);
		}		
}


Global void add_dummies(Sgraph g)
         

/* in die Kanten ueber mehrere Level werden Dummy-Knoten eingefuegt */

{
	Snode n, new_n;
	Sedge e, e1, e2;

	for_all_nodes(g,n)
		for_sourcelist(n,e)
			if(level(e->tnode)-level(n)>1)
				{
				new_n = make_node(g,make_attr(ATTR_DATA,NULL));
				mark_as_dummy(new_n);
				level(new_n)=level(n)+1;
				
				e1 = make_edge(n,new_n,make_attr(ATTR_DATA,NULL));
				e1->graphed = e->graphed;
				set_edgelabel(e1,e->label);
				
				e2 = make_edge(new_n,e->tnode,make_attr(ATTR_DATA,NULL));
				e2->graphed = e->graphed;
				set_edgelabel(e2,e->label);
				mark(e);

				}
			end_for_sourcelist(n,e);
		delete_marked_edges(n);
	end_for_all_nodes(g,n);
}

	
Global void init_positions(Sgraph g)
         

/* initialisiert die waagrechten Positionen der Knoten */
/* und berechnet die Anzahl der Knoten pro Level (nodes_of_level) */

{
	Snode n;
	
	/* nodes_of_level mit 0 initialisieren */
	{
		int i;
		for (i=0;i<SIZE; i++)
		nodes_of_level[i]=0;
	}

	for_all_nodes(g,n)
		n->x = (++(nodes_of_level[level(n)]));
	end_for_all_nodes(g,n);
}



/*********************************************************************************/
Global void make_hierarchy(Sgraph g, int choose)
{
	Nodelist nodes;
	Snode n;

	/* Topologisches Sortieren						 */
	if( choose == 1 )
	{
		prepare( g );
        	nodes = top_sort( g );
		give_levels( nodes );
	}
	
	/* Coffman_Graham							 */
	if( choose == 0 )
	{
		/* Initialisierung: n->x = 0					 */
		prepare1( g );

		/* bringe Graph in transitive reduzierte Form			 */
        	transitive_reduction( g );

		/* teile den Knoten Levels zu					 */
		coffman_graham( g /* , sugiyama_settings.width nur ein argument */ );

		/* bereite Graphen fuer die nachfolgenden Funktionen vor	 */
		prepare2( g );
	}

	/* Rekursives Verfahren							 */
	if( choose == 2 )
	{
		prepare( g );
		for_all_nodes (g,n)
		{
			if ((n->tlist) == empty_edge)		
				set_level(n,0);
		} end_for_all_nodes (g,n);
	}
}
/*********************************************************************************/	







	

/*********************************************************************************/
/*********************** maximale Breite des Graphen *****************************/
Local int get_max_size (Sgraph g)
{
	int max = 0;
	Snode node;

	for_all_nodes( g, node )
	{
		if( node->x > max)	max = node->x;
	} end_for_all_nodes( g, node );

	return( max );
}
/*********************************************************************************/


/*********************** "breitester" Knoten *************************************/
Local int get_max_width_in_actlevel(Sgraph g, Sgraph original, int horizontal)
{
	int act_width, max_width = 0;
	Snode node, orig_node, help_node;
	Graphed_node gnode;

	for_all_nodes( g, node )
	{
		if( (node->x == horizontal) && !(is_dummy(node)) )
		{
			/* benoetige echten Knoten, nicht die Kopie		 */
			for_all_nodes( original, orig_node )
			{
				help_node = attr_data_of_type( orig_node, Snode );
				if( help_node == node )	break;	
			} end_for_all_nodes( original, orig_node );

			gnode = graphed_node( orig_node );
			act_width = (int)(node_get( gnode, NODE_WIDTH ));

			if( act_width > max_width )	max_width = act_width;
		}
	} end_for_all_nodes( g, node );

	return( max_width );
}
/*********************************************************************************/


/*********************************************************************************/

#if FALSE
Global void set_horizontal_positions(Sgraph g, Sgraph original, int mult)
{
	Snode n;
	int horizontal = 1, max_horizontal, act_max_x = 0;
	int max_act_width = 0, max_old_width;

	max_horizontal = get_max_size( g );

	while( horizontal <= max_horizontal )
	{
		max_old_width = max_act_width;
		max_act_width = get_max_width_in_actlevel( g, original, horizontal );

		/* fuer den Fall, das nur Kanten in dieser Spur verlaufen	 */
		if( max_act_width <= 1 )	max_act_width = 32;
	
		for_all_nodes(g,n)
		{
			if( n->x == horizontal )
			{
				if( horizontal == 1 )
				{
					act_max_x = max_act_width + max_horizontal;
				}		
				n->x = mult*(act_max_x + max_old_width + max_act_width);
			}
		} end_for_all_nodes(g,n);

		act_max_x = act_max_x + max_old_width + max_act_width;
		
		horizontal++;
	}	

}

#endif


Global	void	set_horizontal_positions(Sgraph g, Sgraph original, int distance)
{
	Snode	n;
	int	horizontal = 1,
		max_horizontal,
		act_max_x = 0,
		max_act_width = 0;


	max_horizontal = get_max_size( g );

	act_max_x = get_max_width_in_actlevel(g,original,1) + max_horizontal;
	while( horizontal <= max_horizontal )
	{
		max_act_width = get_max_width_in_actlevel( g, original, horizontal );

		/* fuer den Fall, das nur Kanten in dieser Spur verlaufen	 */
		if( max_act_width <= 1 ) {
			max_act_width = distance;
		}
	
		for_all_nodes(g,n) if( n->x == horizontal ) {
			n->x = act_max_x + distance;
			/* n->x = act_max_x + max_act_width + distance; */
		} end_for_all_nodes(g,n);

		act_max_x = act_max_x + distance;
		/* act_max_x = act_max_x + max_act_width + distance; */
		
		horizontal++;
	}	

}


/*********************************************************************************/


/*********************** "hoechster" Knoten **************************************/
Local int get_max_heigth_in_actlevel(Sgraph g, Sgraph original, int levelnr)
{
	int act_heigth, max_heigth = 0;
	Snode node, orig_node, help_node;
	Graphed_node gnode;

	for_all_nodes( g, node )
	{
		if( (node->y == levelnr) && !(is_dummy(node)) )
		{
			/* benoetige echten Knoten, nicht die Kopie		 */
			for_all_nodes( original, orig_node )
			{
				help_node = attr_data_of_type( orig_node, Snode );
				if( help_node == node )	break;	
			} end_for_all_nodes( original, orig_node );

			gnode = graphed_node( orig_node );
			act_heigth = (int)(node_get( gnode, NODE_HEIGHT ));

			if( act_heigth > max_heigth )	max_heigth = act_heigth;
		}
	} end_for_all_nodes( g, node );

	return( max_heigth );
}
/*********************************************************************************/


/*********************************************************************************/
#if FALSE
Global void set_vertical_positions(Sgraph g, Sgraph original, int mult)
{
	Snode n;
	int levelnr = 0, act_max_y;
	int max_act_heigth = 0, max_old_heigth;

	while( levelnr <= maxlevel )
	{
		max_old_heigth = max_act_heigth;
		max_act_heigth = get_max_heigth_in_actlevel( g, original, levelnr );
	
		for_all_nodes(g,n)
		{
			if( n->y == levelnr )
			{	
				if( levelnr == 0 )
				{
					act_max_y = max_act_heigth + maxlevel;	
				}
				n->y = mult*(act_max_y + max_old_heigth + max_act_heigth);
			}
		} end_for_all_nodes(g,n);

		act_max_y = act_max_y + max_old_heigth + max_act_heigth;
		
		levelnr++;
	}
}
#endif
/*********************************************************************************/
/*********************************************************************************/


Global	void	set_vertical_positions(Sgraph g, Sgraph original, int distance)
{
	Snode n;
	int levelnr = 0, act_max_y;
	int max_act_heigth = 0;

	act_max_y = get_max_heigth_in_actlevel( g, original, 0) + maxlevel;
	while( levelnr <= maxlevel )
	{
		max_act_heigth = get_max_heigth_in_actlevel( g, original, levelnr );
	
		for_all_nodes(g,n) if( n->y == levelnr ) {	
			/* n->y = act_max_y + max_act_heigth + distance; */
			n->y = act_max_y + distance;
		} end_for_all_nodes(g,n);

		/* act_max_y = act_max_y + max_act_heigth + distance; */
		act_max_y = act_max_y + distance;
		
		levelnr++;
	}
}




/*********************************************************************************************/
/* schreibe in das Attributfeld der Originalkante einen Zeiger auf die Kopie der Kante; der  */
/* Funktion wird die Kopie uebergeben				KS 17/12/92		     */
/* 5.5.93: im Originalgraphen koennen mehrere Kanten vom selben Knoten auf denselben Ziel-   */
/* knoten gehen	(brauche nichts zu aendern, da alle Originalkanten auf die einzige Kopie     */
/* zeigen sollen								             */
Local void put_attr_on_orig_edge (Sgraph graph, Sedge new_copy_edge)
{
	Snode copy_source, copy_target, node;
	Sedge edge;
	
	copy_source = new_copy_edge->snode;
	copy_target = new_copy_edge->tnode;

	for_all_nodes( graph, node )
	{
		if( (attr_data_of_type( node, Snode ) == copy_source) ||
		    (attr_data_of_type( node, Snode ) == copy_target) )
		{
			for_sourcelist( node, edge )
			{
				if( (attr_data_of_type( edge->tnode, Snode ) == copy_target) ||
			            (attr_data_of_type( edge->tnode, Snode ) == copy_source) )
				{
					set_edgeattrs(edge,
						 make_attr(ATTR_DATA,(char *)new_copy_edge ));
				}
			} end_for_sourcelist( node, edge );
		}
	} end_for_all_nodes( graph, node );
}
/*********************************************************************************************/
		


Global void remove_dummies(Sgraph g, Sgraph orig_g)
                 

/* entfernt die Dummy-Knoten und stellt die langen Kanten wieder her, */
/* wobei die Positionen der Dummies in Attributes gespeichert werden  */

{
	Snode n, n1, n2;
	Sedge e, new_e;
	Slist remove_node_slist, l;
	int *pos;
	int i;

	/* Kantenattribute initialisieren */
	
	for_all_nodes(g,n)
		for_sourcelist(n,e)
			set_attr_data(e, NULL);
		end_for_sourcelist(n,e);
	end_for_all_nodes(g,n);

	/* Kanten ueber mehr als ein Level wiederherstellen */

	for_all_nodes(g,n)
		if (is_dummy(n) == FALSE)
		for_sourcelist(n,e)
			 if (attr_data_of_type(e, int *) == (int *)NULL)
				if (is_dummy(e->tnode))
			  	{
					pos = (int *)calloc(2*maxlevel+3, sizeof(int));
					pos[0] = n->x;
					pos[1] = n->y;
					i = 2;
					n1 = e->tnode;
					do 
					{	
					/* Positionen aller nachfolgenden dummy-Knoten speichern */  					
						n2 = n1->slist->tnode;
						pos[i++] = n1->x;
						pos[i++] = n1->y;
						n1 = n2;	
					} while (is_dummy(n2));	
					pos[i++] = n2->x;
					pos[i++] = n2->y;
					pos[i] = 0;
					new_e = make_edge(n,n2,make_attr(ATTR_DATA, NULL));

					/* muss hier jetzt wieder einen Zeiger der Originalkante */
					/* auf ihre (vorher geloeschte und soeben wieder er-     */
					/* zeugte) Kantenkopie herstellen          KS 17/12/92   */
					put_attr_on_orig_edge( orig_g, new_e );

					new_e->graphed = e->graphed;
					set_edgelabel(new_e,e->label);
					set_attr_data(new_e, pos);
				}
				else
				{
				
					pos = (int *)calloc(5, sizeof(int));
					pos[0] = n->x;
					pos[1] = n->y;
					n1 = e->tnode;
					pos[2] = n1->x;
					pos[3] = n1->y;
					pos[4] = 0;
					set_attr_data(e, pos);
				}
		end_for_sourcelist(n,e);
	end_for_all_nodes(g,n);
	
	/* dummy - Knoten loeschen, Slists MH 3/10/91 */

	remove_node_slist = empty_slist;
	for_all_nodes (g, n) {
		if (is_dummy(n)) {
			remove_node_slist = add_immediately_to_slist (remove_node_slist,
				make_attr (ATTR_DATA, (char *)n));
		}
	} end_for_all_nodes (g, n);
		
	if (remove_node_slist != empty_slist) {
		for_slist (remove_node_slist, l) {
			remove_node (attr_data_of_type (l, Snode));
		} end_for_slist (remove_node_slist, l);
		free_slist (remove_node_slist);
	}

}
/***** E n d e   H I E R . C *********************************************************/






