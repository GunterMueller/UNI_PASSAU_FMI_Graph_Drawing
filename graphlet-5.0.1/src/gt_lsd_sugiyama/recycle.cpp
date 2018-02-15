/* This software is distributed under the Lesser General Public License */
/* (C) Universitaet Passau 1986-1992 */
/******************************************************************************/
/*                                                                            */
/*                      R  E  C  Y  C  L  E  .  C                             */
/*                                                                            */
/******************************************************************************/


#include "sgraph/std.h"
#include "sgraph/sgraph.h"
#include "sgraph/slist.h"



struct node_info {
    Snode node;
    int outdegree, indegree, labelnr, diff, marked;
    struct node_info *next;
};


struct node_info *info_list;

/* die beiden folgenden Funktionen dienen zum Aufbau und initialisieren von   */
/* info_list (mit Hilfe dieser Liste werden den einzelnen Knoten Nummern ge-  */
/* geben, mit deren Hilfe man dann die up-arcs bestimmen kann)		      */

/********************** Hilfsfunktion zum Aufbau von info_list ****************/
Local void insert (Snode node, int out, int in)
{
    struct node_info *new_n;
    new_n = (struct node_info *)malloc(sizeof(struct node_info));

    new_n->node      = node;
    new_n->outdegree = out;
    new_n->indegree  = in;
    new_n->labelnr   = 0;
    new_n->diff      = 0;
    new_n->marked    = 0;
    new_n->next      = info_list;  

    info_list        = new_n;	
}
/******************************************************************************/


/**************************** Aufbau von info_list ****************************/
Global void init_worklist (Sgraph gra)
{
    Sedge e;
    Snode n;
    int length_out, length_in;

    info_list = NULL;

    for_all_nodes ( gra, n )
 	{
	    length_out = 0;
	    /* zaehle die Ausgaenge des Knotens                           */
	    for_sourcelist( n ,e )
		{
		    length_out++;
		} end_for_sourcelist( n, e );

    		/* zaehle die Eingaenge des Knotens                           */
    		length_in  = 0;
		for_targetlist( n ,e )
		    {
			length_in++;
		    } end_for_targetlist( n, e );

		    /* einfuegen in info_list                                     */
		    insert( n, length_out, length_in );
	} end_for_all_nodes ( gra, n );
}
/******************************************************************************/





/* Hilfsfunktionen zum Bestimmen des "bestmoeglichsten" Knotens; "bestmoeg-   */
/* lichst" heisst, dass von denjenigen Knoten K mit der geringsten Eingangs-  */
/* anzahl derjenige ausgewaehlt wird, der innerhalb von K die geringste       */
/* Eingangszahl hat                                                           */


/*************************** Liste loeschen ***********************************/
Local void delete_list (struct node_info *list)
{
    struct node_info *kill;

    while( list != NULL )
    {
	kill = list;
	list = list->next;
#if __SUNPRO_CC == 0x401
	free ((char*) kill);
#else
	free (kill);
#endif
    }
}
/******************************************************************************/


/********* pruefen, ob ein Knoten in einer Liste ist ; Rueckgabe ist  *********/
/********* ein Zeiger auf den Knoten in der Liste                     *********/
Local struct node_info *look (struct node_info *list, Snode node)
{
    struct node_info *help_list;
    int is_in = 0;

    help_list = list;
    while( (help_list != NULL) && (is_in == 0) )
    {
	if( node == help_list->node ) 	is_in = 1;
	else				help_list = help_list->next;
    }
	
    if( is_in == 0 )			return( NULL );
    else					return( help_list );
}
/******************************************************************************/


/*********** zaehle Ausgaenge eines Knotens (Zielknoten nicht markiert) *******/
Local int count_out (Snode node)
{
    int out = 0;
    Snode target;
    Sedge edge;
    struct node_info *target_in_info_list;

    for_sourcelist( node, edge )
	{
	    target = edge->tnode;
	    /* suche nun entsprechenden Knoten in der info_list	      */
	    target_in_info_list = look( info_list, target );
	    if( target_in_info_list->marked == 0 )		
		out++;
	} end_for_sourcelist( node, edge );
	return( out );
}
/******************************************************************************/


/*********** zaehle Eingaenge eines Knotens (Quellknoten nicht markiert) ******/
Local int count_in (Snode node)
{
    int in = 0;
    Snode source;
    Sedge edge;
    struct node_info *source_in_info_list;

    for_targetlist( node, edge )
	{
	    source = edge->snode;
	    /* suche nun entsprechenden Knoten in der info_list	      */
	    source_in_info_list = look( info_list, source );
	    if( source_in_info_list->marked == 0 )   
		in++;
	} end_for_targetlist( node, edge );
	return( in );
}
/******************************************************************************/


/********* Bestimmung der niedrigsten Eingangszahl eines Knotens **************/
Local int get_min_in (struct node_info *list)
{
    int min = 10000, in;
    struct node_info *help_list;

    help_list = list;
    while( help_list != NULL )
    {
	if( help_list->marked != 1 )
	{
	    /* zaehle die Eingaenge aus nicht markierten Knoten   */
	    in = count_in( help_list->node );
	    if( in < min )
		min = in;
	}
	help_list = help_list->next;
    }
    return( min );
}
/******************************************************************************/


/********* Bestimmung der hoechsten Ausgangszahl eines Knotens ****************/
Local int get_max_out (struct node_info *list)
{
    int max = -10000, out;
    struct node_info *help_list;

    help_list = list;
    while( help_list != NULL )
    {
	if( help_list->marked != 1 )
	{
	    /* zaehle Ausgaenge an nicht markierte Knoten         */
	    out = count_out( help_list->node );
	    if( (help_list->marked != 1) && (out > max) )
		max = out;
	}
	help_list = help_list->next;
    }
    return( max );
}
/******************************************************************************/


/******** Erzeugen einer zusaetzlichen Liste, in der nur Knoten stehen, *******/
/******** die die vorher bestimmte Eingangszahl besitzen                *******/
Local struct node_info *make_min_in_list (struct node_info *list, int in)
{
    struct node_info *new_n, *help_list, *first_n = NULL;
    int out;

    help_list = list;
    while( help_list != NULL )
    {
	if( help_list->marked != 1 )
	{ 
	    if( count_in( help_list->node ) == in )
	    {
		out = count_out( help_list->node );
		new_n = (struct node_info *)malloc(sizeof(struct node_info));
			
		new_n->node      = help_list->node;
		new_n->outdegree = out;
		new_n->indegree  = in;
		new_n->labelnr   = 0;
		new_n->marked    = 0;
		new_n->diff      = 0;
		new_n->next      = first_n;

		first_n          = new_n;
	    }
	}
	help_list = help_list->next;
    }
    return( first_n );
}
/******************************************************************************/


/******** Erzeugen einer zusaetzlichen Liste, in der nur Knoten stehen, *******/
/******** die die vorher bestimmte Ausgangszahl besitzen                *******/
Local struct node_info *make_max_out_list (struct node_info *list, int out)
{
    struct node_info *new_n, *help_list, *first_n = NULL;
    int in;

    help_list = list;
    while( help_list != NULL )
    {
	if( help_list->marked != 1 ) 
	{ 
	    if( count_out( help_list->node ) == out )
	    {
		in = count_in( help_list->node );
		new_n = (struct node_info *)malloc(sizeof(struct node_info));
			
		new_n->node      = help_list->node;
		new_n->outdegree = out;
		new_n->indegree  = in;
		new_n->labelnr   = 0;
		new_n->marked    = 0;
		new_n->diff      = 0;
		new_n->next      = first_n;

		first_n          = new_n;
	    }
	}
	help_list = help_list->next;
    }
    return( first_n );
}
/******************************************************************************/


/******* zaehle die Ausgaenge eines Knotens, die nur auf Knoten  einer ********/
/******* bestimmten Liste  zeigen                                      ********/
Local int come_out (struct node_info *list, Snode node)
{
    int out = 0;
    struct node_info *contains;
    Sedge edge;

    for_sourcelist( node, edge )
	{
	    contains = look( list, edge->tnode );
	    if( contains != NULL )	out++;
	}end_for_sourcelist( node, edge );
	return( out );
}
/******************************************************************************/


/*********** zaehle die Eingaenge eines Knotens, die nur von Knoten  **********/
/*********** einer bestimmten Liste  kommen                          **********/
Local int come_in (struct node_info *list, Snode node)
{
    int in = 0;
    struct node_info *contains;
    Sedge edge;

    for_targetlist( node, edge )
	{
	    contains = look( list, edge->snode );
	    if( contains != NULL )	in++;
	} end_for_targetlist( node, edge );
	return( in );
}
/******************************************************************************/


/***** trage die Differenz der Zahl der Ein- und Ausgaenge in Liste ein *******/
Local void write_diff_in_list (struct node_info *list)
{
    struct node_info *help_list;
    Snode actual_node;
    int node_in, node_out;

    help_list = list;
    while( help_list != NULL )
    {
	if( help_list->marked != 1 )
	{
	    actual_node = help_list->node;
	    node_in     = come_in  ( list, actual_node );
	    node_out    = come_out ( list, actual_node );
	    help_list->diff = ( node_out - node_in );
	}
	help_list       = help_list->next;
    }
}
/******************************************************************************/


/**** bestimme die groesste Differenz von Ein- und Ausgaengen in Liste ********/
Local int get_max_diff (struct node_info *list)
{
    int max = -20000;
    struct node_info *help_list;
	
    help_list = list;
    while( help_list != NULL )
    {
	if( (help_list->diff > max) && (help_list->marked != 1) )
	    max = help_list->diff;
	help_list = help_list->next;
    }
    return( max );
}
/******************************************************************************/


/***************** suche erstbesten Knoten mit max_diff ***********************/
Local struct node_info *get_node_with_max_diff (struct node_info *list, int max_diff)
{
    struct node_info *node_in_info_list, *help_list_node;
    int control = 0;

    help_list_node = list;
    /* suche erstbesten Knoten in der uebergebenen Liste mit max_diff     */
    while( control == 0 )
    {
	if( help_list_node->diff == max_diff )  
	    control = 1;
	else	
	    help_list_node = help_list_node->next;
    }

    /* suche nun den Knoten mit max_diff, den ich gerade in Liste gefunden 
       habe, in der urspruenglichen info_list                             */
    node_in_info_list = look( info_list, help_list_node->node );
    return( node_in_info_list );
}
/******************************************************************************/


/*************** Loesche die Markierung bei den Knoten ************************/
Local void demark_nodes (struct node_info *list)
{
    struct node_info *help_list = list;

    while( help_list != NULL )
    {
	help_list->marked = 0;
	help_list = help_list->next;
    }
}
/******************************************************************************/

#if 0
/********* Hilfsaussgabe1 zur Kontrolle ***************************************/
Local void gib_liste_aus (struct node_info *list, int choose)
{
    struct node_info *help_list = list;
    int i = 1;

    if( choose == 0 ) 	printf("info_list: \n");
    while( help_list != NULL )
    {
	printf("%d.Knoten:%s , indegree:%d , outdegree:%d , labelnr:%d \n",
	    i, help_list->node->label, help_list->indegree,
	    help_list->outdegree, help_list->labelnr );
	i++;
	help_list = help_list->next;
    }
    printf("\n");
}
/******************************************************************************/
#endif


/************* suche "bestmoeglichsten" Knoten ( eigene Methode ) *************/
Local struct node_info *search_own_method (struct node_info *list)
{
    struct node_info *choosen_node, *possible_nodes;
    int min_in, max_diff;

    /* bestimme zunaechst die minimale Eingangszahl eines Knotens         */
    min_in = get_min_in ( list );

    /* bestimme die Knoten mit obiger minimaler Eingangszahl              */
    possible_nodes = make_min_in_list ( list, min_in );

    /* schreibe in die soeben bestimmte Liste die Differenz zwischen Aus- 
       und Eingaengen aller Kanten, die nur possible_nodes betreffen      */
    write_diff_in_list( possible_nodes );

    /* bestimme die maximale Differenz von Ein- und Ausgaengen            */
    max_diff = get_max_diff( possible_nodes );

    /* suche den Knoten mit soeben bestimmter maximaler Differenz         */
    choosen_node = get_node_with_max_diff( possible_nodes, max_diff );

    /* loesche die Liste der moeglichen Knoten     			      */
    delete_list( possible_nodes );

    return( choosen_node );
}
/******************************************************************************/


/************* suche "bestmoeglichsten" Knoten ( Greedy-Methode ) *************/
Local struct node_info *search_greedy (struct node_info *list)
{
    struct node_info *choosen_node, *possible_nodes;
    int max_out, max_diff;

    /* bestimme zunaechst die maximale Ausgangszahl eines Knotens         */
    max_out = get_max_out ( list );

    /* bestimme die Knoten mit obiger maximaler Ausgangszahl              */
    possible_nodes = make_max_out_list ( list, max_out );

    /* schreibe in die soeben bestimmte Liste die Differenz zwischen Aus- 
       und Eingaengen aller Kanten, die nur possible_nodes betreffen      */
    write_diff_in_list( possible_nodes );

    /* bestimme die maximale Differenz von Ein- und Ausgaengen            */
    max_diff = get_max_diff( possible_nodes );

    /* suche den Knoten mit soeben bestimmter maximaler Differenz         */
    choosen_node = get_node_with_max_diff( possible_nodes, max_diff );

    /* loesche die Liste der moeglichen Knoten     			      */
    delete_list( possible_nodes );

    return( choosen_node );
}
/******************************************************************************/


/******** folgende Funktionen sind Hilfsfunktionen fuer search_dandC **********/

/******** trage fuer die gegebene Liste beliebige Labelnr. ein ****************/
Local void arbitrarily_label (struct node_info *given_list, int nr)
                             
    /* Anzahl der Listenelemente				      */
{
    struct node_info *help_list, *node_in_info_list;
    Snode node;

    /* gebe den gegebenen Knoten eine beliebige Labelnr. groessergleich nr*/
    help_list = given_list;
    while( help_list != NULL )
    {
	node = help_list->node;
	/* trage in info_list den Wert ein			      */
	node_in_info_list = look( info_list, node );
	node_in_info_list->labelnr = nr;
	nr++;
	help_list = help_list->next;
    }
    delete_list( given_list );
}
/******************************************************************************/


/*********************** zaehle Knoten einer Liste ****************************/
Local int count_nodes_from_list (struct node_info *list)
{
    struct node_info *help_list = list;
    int count = 0;

    while( help_list != NULL )
    {
	count++;
	help_list = help_list->next;
    }
    return( count );
}
/******************************************************************************/


/********** existieren Kanten zwischen den gegebenen Knoten *******************/
Local int found_edges (struct node_info *list)
{
    struct node_info *help_list = list;
    Snode source;
    Sedge edge;
    int found = 0;

    while( (help_list != NULL) && (found != 1) )
    {
	source = help_list->node;
	for_sourcelist( source, edge )
	    {
		/* gehe Zielknotenliste von source durch	      */
		if( edge != NULL )
		{
		    if( look( list, edge->tnode ) != NULL )
			found = 1;
		}
	    } end_for_sourcelist( source, edge );
	    help_list = help_list->next;
    }
	
    if( found == 0 ) 	return( 0 );
    else			return( 1 );
}
/******************************************************************************/	
		

/************** konstruiere Teilliste aus gegebener Liste *********************/
Local struct node_info *make_new_list (struct node_info *list, int marked)
                       
    /* wenn marked = 1, so bilde Teilliste aus markierten Knoten  */
    /* wenn marked = 0, so bilde Teilliste aus nichtmarkierten Kn.*/
{
    struct node_info *help_list = list, *first = NULL, *new_element;

    if( marked == 0 )
    {
	while( help_list != NULL )
	{
	    if( help_list->marked == 0 )
	    {
		new_element = (struct node_info *)malloc(sizeof(struct node_info));	
		new_element->node 	= help_list->node;
		new_element->outdegree  = 0;
		new_element->indegree   = 0;
		new_element->labelnr    = 0;
		new_element->diff	= 0;
		new_element->marked	= 0;
		new_element->next	= first;

		first = new_element;
	    }
	    help_list = help_list->next;
	}
    }
    else
    {
	while( help_list != NULL )
	{
	    if( help_list->marked == 1 )
	    {
		new_element = (struct node_info *)malloc(sizeof(struct node_info));	
		new_element->node 	= help_list->node;
		new_element->outdegree  = 0;
		new_element->indegree   = 0;
		new_element->labelnr    = 0;
		new_element->diff	= 0;
		new_element->marked	= 0;
		new_element->next	= first;

		first = new_element;
	    }
	    help_list = help_list->next;
	}
    } 
    return( first );
}
/******************************************************************************/
			

/******************** loesche Knoten aus Liste ********************************/
Local struct node_info *sugiyama_delete_node (struct node_info *list, Snode del_node)
{
    struct node_info *help_list = list, *before_node;
    int found = 0;

    while( (help_list != NULL) && (found != 1) )
    {
	before_node = help_list;
	help_list   = help_list->next;
	/* falls zu loeschender Knoten das erste Element der Liste ist*/
	if( before_node->node == del_node )	
	{
	    list  = list->next;
#if __SUNPRO_CC == 0x401
	    free ((char*)(before_node));
#else
	    free (before_node);
#endif
	    found = 1;	
	}
	/* zu loeschender Knoten nicht erstes Element		      */
	if( help_list->node == del_node )
	{
	    before_node->next = help_list->next;
#if __SUNPRO_CC == 0x401
	    free ((char*) help_list);
#else
	    free (help_list);
#endif
	    found = 1;
	}
    }

    return( list );
}
/******************************************************************************/

Local void dandC (struct node_info *list, int i);

/**** suche Knoten mit der groessten Ausgangsanzahl und gib ihm den ***********/
/**** Label nr; loesche dann diesen Knoten aus der Liste und rufe   ***********/
/**** mit der restlichen Liste dandC wieder auf                     ***********/
Local void dandC_odd (struct node_info *list, int i, int nr)
                       
    /* i beliebiger Parameter, nr ist Anzahl der Listenelemente   */
{
    struct node_info *max_out_node, *max_out_node_in_info_list;

    /* suche "bestmoeglichsten" Knoten in der uebergebenen Liste	      */
    max_out_node = search_greedy( list );
    /* suche den soeben bestimmten Knoten in der Originalliste            */
    max_out_node_in_info_list = look( info_list, max_out_node->node );
    max_out_node_in_info_list->labelnr = (i + nr) - 1;
    /* loesche nun max_node aus list				      */
    list = sugiyama_delete_node( list, max_out_node->node );
    dandC( list, i );
}
/******************************************************************************/
	

/**** Teile gegebene Liste in 2 Listen gleicher Laenge auf: die Listen    *****/
/**** werden so konstruiert, dass in der ersten Liste alle Knoten eine    *****/
/**** groessere Ausgangszahl wie die Knoten in der zweiten Liste haben.   *****/
/**** Beide Listen werden dann wieder dandC uebergeben   	          *****/
Local void dandC_not_odd (struct node_info *list, int i, int nr)
                       
    /* i beliebiger Parameter, nr ist Anzahl der Listenelemente   */
{
    struct node_info *big_out_node, *big_out_node_in_info_list;
    struct node_info *new_list_big, *new_list_small;
    int stop = 1;

    /* markiere die Knoten mit den greosseren Ausgangsanzahlen	      */
    while( stop <= (nr/2) )
    {
	big_out_node = search_greedy( list );
	big_out_node_in_info_list = look( list, big_out_node->node );
	big_out_node_in_info_list->marked = 1;
	stop++;
    }

    new_list_big   = make_new_list( list, 1 );
    new_list_small = make_new_list( list, 0 );
    delete_list( list );
    dandC( new_list_big, i + (nr/2) );
    dandC( new_list_small, i );
}
/******************************************************************************/


/*************** verteile Labels mit dandC-Methode ****************************/
Local void dandC (struct node_info *list, int i)
{
    int exist, nr;

    /* existieren zwischen den vorliegenden Knoten Kanten		      */
    exist = found_edges( list );
    if( exist == 0 )	arbitrarily_label( list, i );
    else
    {
	/* bestimme Knotenanzahl				      */
	nr = count_nodes_from_list( list );
	/* bei gerader Knotenanzahl				      */
	if( (nr/2)*2 == nr )		dandC_not_odd( list, i, nr );
	/* bei ungerader Knotenanzahl				      */
	else				dandC_odd( list, i, nr );
    }
}
/******************************************************************************/


/************************* mache Kopie einer Liste ****************************/
Local struct node_info *copy_list (struct node_info *list)
{
    struct node_info *new_list, *first = NULL, *help_list = list;

    while( help_list != NULL )
    {
	new_list =(struct node_info *)malloc(sizeof(struct node_info ));
	new_list->node      = help_list->node;
	new_list->outdegree = help_list->outdegree;
	new_list->indegree  = help_list->indegree;
	new_list->diff      = 0;
	new_list->labelnr   = 0;
	new_list->marked    = 0;
	new_list->next	    = first;

	first     = new_list;
	help_list = help_list->next;
    }
    return( first );
}
/******************************************************************************/


/************** Rahmenfunktion fuer dandC *************************************/
Local void search_dandC (struct node_info *list)
{
    struct node_info *copy_info_list;

    /* brauche Kopie, da uebergebene Liste immer geloescht wird	      */
    copy_info_list = copy_list( list );
    dandC( copy_info_list, 1 );		/* i = 1		      */
}
/******************************************************************************/



/********* jedem Knoten eine Labelnummer zuordnen, damit spaeter die  *********/
/********* up-arcs festgestelt werden koennen                         *********/
Local void put_labels (struct node_info *list, int choose)
{
    int label;
    struct node_info *best_node;

    /* eigene Methode						      */
    if( choose == 0 )
    {
	label = count_nodes_from_list( list );
	while( label >= 1 )
	{
	    best_node = search_own_method( info_list );
	    best_node->labelnr = label;
	    best_node->marked = 1;
	    label--; 
	}
    }

    /* Greedy-Methode						      */
    if( choose == 1 )
    {
	label = count_nodes_from_list( list );
	while( label >= 1 )
	{
	    best_node = search_greedy( info_list );
	    best_node->labelnr = label;
	    best_node->marked = 1;
	    label--; 
	}
    }	

    /* DandC-Methode						      */
    if( choose == 2)	search_dandC( list );
	
    demark_nodes( list );
}
/******************************************************************************/


/* allen Knoten wurde nun eine Labelnr. zugewiesen; im folgenden vergleiche   */
/* ich nun die Zielknotenlabelnr. mit der Quellknotenlabelnummer: ein up_arc  */
/* liegt vor, wenn die Zielknotenlabelnr. groesser ist; in diesem Fall drehe  */
/* ich die Pfeilspitze um u. merke mir die Kante in einer Liste, um am        */
/* Schluss wieder die richtige Pfeilrichtung setzen zu koennen                */

typedef struct node_pair {
    Snode s_node, t_node;
    Sedge real_edge;
} *arc;


/********************** Pfeilspitzen umdrehen *********************************/
Local void turn_arc_direction (Snode source, Snode target, Sedge orig_edge)
{
    Snode help_node;
    Sedge edge, new_edge;

    /* suche zuerst zugehoerige Kante, um sie dann zu loeschen            */
    for_sourcelist( source, edge )
	{	
	    help_node = edge->tnode;
	    if( help_node == target )	break;
	
	} end_for_sourcelist( source, edge );

	/* loesche Kante						      */
	/* free(attr_data(edge)); nicht original kante freigeben!!! */
	remove_edge( edge );
	
	/* fuege jetzt die Kante in umgekehrter Richtung in den Graphen ein   */
	/* in attributes steht ein Zeiger auf die Originalkante	 	      */
	new_edge = make_edge( target, source,
	    make_attr( ATTR_DATA, (char *) orig_edge ));

	/* biege Zeiger der Originalkante auf die neue Kopie um		      */
	set_edgeattrs( orig_edge, make_attr( ATTR_DATA, (char *) new_edge ));

}
/******************************************************************************/

#if 0
/********* Hilfsaussgabe3 zur Kontrolle ***************************************/
Local void gib_arc_liste_aus (Slist list)
{
    Slist l;
    arc pair;
    int i = 1;

    for_slist( list, l )
	{
	    pair = attr_data_of_type( l, arc );
	    printf("%d . Paar:  Quellknoten: %s ,   Zielknoten: %s \n",
		i, pair->s_node->label, pair->t_node->label );
	    i++;
	} end_for_slist( list, l );
}
/******************************************************************************/
#endif


/************ mache node_pair Element *****************************************/
Global arc make_node_pair (Snode source, Snode target, Sedge edge)
{
    struct node_pair *new_pair;
    new_pair = (struct node_pair *)malloc(sizeof(struct node_pair));

    new_pair->s_node    = source;
    new_pair->t_node    = target;
    new_pair->real_edge = edge;

    return( new_pair );
}
/******************************************************************************/
	

/***************** mache Graph ohne Zyklen ************************************/
Local Slist change_arcs (struct node_info *list)
{
    int source_labelnr;
    struct node_info *help_list, *target_in_info_list;
    Snode source_node, target_node;
    Sedge edge;
    Slist save_arcs_slist = empty_slist;
    arc pair;

    help_list = list;
    while( help_list != NULL )
    {
	source_node    = help_list->node;
	source_labelnr = help_list->labelnr;

	for_sourcelist( source_node, edge ) 
	    {
		/* vgl. nun die Zielknotenlabelnr. mit der des Quell-
		   knotens                                            */
		target_node = edge->tnode;
		target_in_info_list = look( info_list, target_node );

		if( target_in_info_list->labelnr > source_labelnr )
		{
		    /* speichere Kante u. Zeiger auf Originalkante*/
		    pair = make_node_pair(source_node, target_node, 
			attr_data_of_type( edge, Sedge));
		    save_arcs_slist = add_to_slist( save_arcs_slist,
			make_attr(ATTR_DATA, (char*)pair));
		}
	    } end_for_sourcelist( source_node, edge );

	    help_list = help_list->next;
    }

#if FALSE
    /* zur Kontrolle 						      */
    gib_arc_liste_aus( save_arcs_slist );
#endif

    return( save_arcs_slist );
}
/******************************************************************************/


/************ alle angegeben Kanten (=arcs_slist) umdrehen ********************/
Local void turn (Slist arc_slist)
{
    Slist l;
    Snode source, target;
    Sedge edge;
    arc pair;

    /* durchlaufe einfach die Liste und drehe mit turn_arc_direction die  */
    /* Pfeilspitze um						      */
    for_slist( arc_slist, l )
	{
	    pair   = attr_data_of_type(l, arc);
	    source = pair->s_node;
	    target = pair->t_node;
	    edge   = pair->real_edge;
	    turn_arc_direction( source, target, edge );
	} end_for_slist( arc_slist, l );
}
/******************************************************************************/	

	 
/**************************** GESAMTFUNKTION  1.Teil **************************/
Global Slist decycle (Sgraph gra, int choose)
{ 
    Slist save_arcs;
	
    /* Arbeitsliste initialisieren					      */
    init_worklist( gra );

    /* Knoten mit Labelnummer versehen				      */
    put_labels( info_list, choose );
  	
#if FALSE
    /* zur Kontrolle						      */
    gib_liste_aus( info_list, 0 );
#endif

    /* up_arcs bestimmen 			                              */
    save_arcs = change_arcs( info_list );

    /* Pfeilspitzen umdrehen					      */
    if( save_arcs != NULL )		turn( save_arcs );

    return( save_arcs );

}
/******************************************************************************/



/*********************************************************************************************/
Local void orig_edge_to_copy (Sgraph graph, Sedge new_copy_edge)
{
    Snode copy_source, copy_target, node;
    Sedge edge;
	
    copy_source = new_copy_edge->snode;
    copy_target = new_copy_edge->tnode;

    for_all_nodes( graph, node )
	{
	    if ((attr_data_of_type( node, Snode ) == copy_source) ||
		(attr_data_of_type( node, Snode ) == copy_target))  
	    {
		for_sourcelist( node, edge )
		    {
			if ((attr_data_of_type( edge->tnode, Snode ) == copy_target) ||
			    (attr_data_of_type( edge->tnode, Snode ) == copy_source))
			{
			    set_edgeattrs(edge,
				make_attr(ATTR_DATA,(char *)new_copy_edge ));
			}
		    } end_for_sourcelist( node, edge );
	    }
	} end_for_all_nodes( graph, node );
}
/*********************************************************************************************/
		


/********************** Pfeilspitzen umdrehen *********************************/
/* benoetige hier eine leicht modifierte Fassung von turn_arc_direction, da   */
/* ich ansonsten die Attribute ueberschreiben wuerde		 	      */
Local void turn_back_arc_direction (Snode source, Snode target, Sedge orig_edge, Sgraph orig_g)
{
    Sedge edge, new_edge;
    Attributes attr;

    edge = attr_data_of_type(orig_edge, Sedge);
    source = edge->snode;
    target = edge->tnode;

    /*  in attr stehen die Koordinaten fuer die Kante		      */
    attr = edge->attrs;
    /* loesche Kante						      */
    remove_edge( edge );

    /* fuege jetzt die Kante in umgekehrter Richtung in den Graphen ein   */
    new_edge = make_edge( target, source, attr);

    /* biege Zeiger der Originalkanten auf die neue Kopie um	      */
    orig_edge_to_copy( orig_g, new_edge );

}
/******************************************************************************/


/**************************** GESAMTFUNKTION  2.Teil **************************/
Global void rechange_arcs (Slist list, Sgraph orig_g)
{
    Slist l;
    arc pair;
    Snode source, target;
    Sedge edge;

    for_slist( list, l )
	{
	    pair   = attr_data_of_type( l, arc );
	    source = pair->s_node;
	    target = pair->t_node;
	    edge   = pair->real_edge;
	    turn_back_arc_direction( target, source, edge, orig_g );
	} end_for_slist( list, l );
}
/******************************************************************************/


/******************* mache Kopie eines Graphen ********************************/
/* 5.5.93: kopiere Kanten, die vom selben Knoten zum selben Zielknoten gehen  */
/* nur ein einziges mal							      */
Global Sgraph make_copy_of_sgraph (Sgraph g)
{
    Sgraph new_graph = empty_sgraph;
    Snode node, new_node, target, new_source, new_target, help_node;
    Sedge edge, new_edge, help_edge ;
    Attributes save_attrs;
    int exist;

    /* initialisiere zunaechst einen leeren Graphen			      */
    save_attrs = g->attrs;
    new_graph  = make_graph( save_attrs );
    new_graph->directed = g->directed;

    /* kopiere zuerst die Knoten					      */
    for_all_nodes( g, node )
	{
	    save_attrs = node->attrs;
	    new_node   = make_node( new_graph, save_attrs );
	    set_nodelabel(new_node, node->label);

	    /* Zeiger vom alten Knoten auf den Neuen		      */
	    set_nodeattrs(node, make_attr(ATTR_DATA, (char *) new_node));

	} end_for_all_nodes( g, node );

	/* kopiere jetzt die Kanten					      */
	for_all_nodes( g, node )
	    {
		for_sourcelist( node, edge )
		    {
			save_attrs = edge->attrs;
			target     = edge->tnode;

			new_source = attr_data_of_type( node, Snode );
			new_target = attr_data_of_type( target, Snode );

			/* pruefe, ob Kante bereits existiert		      */
			exist = 0;
			for_all_nodes( new_graph, help_node )
			    {
				for_sourcelist( help_node, help_edge )
				    {
					if ((help_node == new_source) && (help_edge->tnode == new_target))
					{
					    exist = 1;
					}
					if (exist == 1) break;
				    } end_for_sourcelist ( help_node, help_edge );
				    if (exist == 1) break;
			    } end_for_all_nodes(new_graph, help_node);
		
			
			    if (new_source != new_target) 
			    {
				
				if (exist == 0)
				{
				    /* fuege eine neue Kante in den neuen Graphen ein */
				    new_edge = make_edge(new_source,new_target, save_attrs);

				    /* Zeiger von der alten Kante auf ihre Kopie	  */
				    set_edgeattrs(edge, 
					make_attr( ATTR_DATA, (char *)new_edge ));
			
				    /* Zeiger von der neuen Kante auf das Original	  */
				    set_edgeattrs(new_edge, 
					make_attr( ATTR_DATA, (char *)edge ));
				}
				else
				{
				    /* Zeiger von der alten Kante auf ihre Kopie	  */
				    set_edgeattrs(edge, 
					make_attr( ATTR_DATA, (char *)help_edge ));
				}
			    }
			    exist = 0;

		    } end_for_sourcelist( node, edge );
	    } end_for_all_nodes( g, node );

	    return( new_graph );
}
/******************************************************************************/


/****************** aendere Reihenfolge im Attribut ***************************/
Local void modify_attr(Sedge edge)
{
    int *pos, *new_pos, p, s, t = 0, count = 0;
	
    /* in pos stehen die Stuetzpunkte fuer die langen Geraden	      */
    pos = attr_data_of_type( edge, int * );
	
    /* zaehle zunaechst die Anzahl der Stuetzpunkte			      */
    for( p=0; pos[p] != 0; p += 1 )     count ++;

    new_pos = (int *)calloc( count + 2, sizeof(int));

    new_pos[0]       = pos[count-2];
    new_pos[1]       = pos[count-1];
    new_pos[count-2] = pos[0];
    new_pos[count-1] = pos[1];
    new_pos[count]   = 0;

    s = count-3;
    t = 2;
    /* beachte: erst x-Wert, dann y-Wert   			              */
    for( p=s; p>=3; p -= 2 )
    {
	new_pos[t] = pos[p-1];	/* x-Wert */
	new_pos[t+1] = pos[p];	/* y-Wert */
	t += 2;
    }
    set_edgeattrs( edge, make_attr( ATTR_DATA, (char *)new_pos ));
	
}
/******************************************************************************/
		

/***** drehe die Reihenfolge der Stuetzpunkte bei Aufwaertspfeilen um *********/
Global void change_order_pos (Sgraph graph, Slist up_arcs)
{
    Slist l;
    Snode source, target, node;
    Sedge edge;
    arc pair;

    for_slist( up_arcs, l )
	{
	    pair   = attr_data_of_type( l, arc );
	    source = pair->s_node;
	    target = pair->t_node;

	    for_all_nodes( graph, node )
		{
		    if( node == source )
		    {
			for_sourcelist( node, edge )
			    {
				if( edge->tnode == target )
				{
				    modify_attr( edge );
				}
			    } end_for_sourcelist( node, edge );
		    }
		} end_for_all_nodes( graph, node );
	} end_for_slist( up_arcs, l );
	free_slist( up_arcs );
}
/******************************************************************************/







		




	
