#ifndef __LP_DATASTRUCTURES_H__
#define __LP_DATASTRUCTURES_H__

	int	ORGINAL_LAMSHOFT;			/*** Bool, ob mit Layout-Strukturen 					***/
	int	LAST_CREATED_FOR_LAMSHOFT;		/*** Bool, ob letzte Zeichnung mit Layout-Erweiterungen 		***/
extern	int	SUBFRAME_VISIBLE;			/*** Sieht man das Ding ueberhaupt	(steht in lp_main.c)		***/

/****************************************************************************************/
/*											*/
/*	Datenstrukturen um aus dem geparsten Graphen mit Hilfe der Produktionen		*/
/*	den Graphen mit minimaler Flaeche neuzuzeichnen					*/
/*											*/
/*	Minimale Datenstruktur, da Parsen mit anderer (types.h) gemacht wird. Routinen	*/
/*	zur Erzeugung dieser Datenstruktur werden am ENDE des Parsens aufgerufen, um 	*/
/*	Kopie der Datenstrukturen zum Parsen zu erzeugen.				*/
/*											*/
/****************************************************************************************/

/****************************************************************************************/
/*											*/
/*		 LP_Parsing_element / LP_Parsing_element_list				*/
/*--------------------------------------------------------------------------------------*/
/*											*/
/*	LP_Parsing_element wird fuer jeden Knoten des zu parsenden Graphen angelegt	*/
/*	und dann fuer jede zusammengefasste Gruppe von Knoten.				*/
/*											*/
/*	LP_Parsing_element_list dient zur Verkettung von LP_Parsing_elementen.		*/
/*	(doppelt verkettet zyklisch)							*/
/*											*/
/****************************************************************************************/


typedef	struct	LP_parsing_element
{
	struct	LP_derivation_list	*derivations;			/* Alle Moeglichkeiten, dieses PE weiter abzuleiten */

	char*				label;				/* Theoretisch unnoetig, erleichtert aber das Fehlersuchen */

	struct	lp_upper_production	*layout_structures;		/* Datenstrukturen zum Layoutausrechnen */

	Snode				graph_iso,			/* Entsprechung bei der Neuzeichnung */

					prod_iso;			/* ???*/

	int				w,				/* Breite ???*/

					h;				/* Hoehe ???*/

	int				width,
					height;				/* Wenn ein terminaler Knoten im abgeleiteten Graphen entspricht,
									   dann dessen Groesse */

}	*LP_Parsing_element;



typedef	struct	LP_parsing_element_list
{

	struct	LP_parsing_element	*pe;				/* Zeiger auf Parsing_element */


	/* Verweis auf entsprechenden Knoten in der Produktion */
	/* Muss hier sein, da PE`s i.a. mehrfach verwendet werden */

		struct	snode			*prod_iso;
		struct	parsing_element		*lams_prod_iso;

	/* Verkettung */

		struct	LP_parsing_element_list	*pre;
		struct	LP_parsing_element_list	*suc;

}	*LP_Parsing_element_list;



/******************** Makros zum Durchlaufen der LP_Parsing_element_list ****************/

#define	FOR_LP_PARSING_ELEMENTS( head, cur )		\
	{ if( ((cur) = (head)) != (LP_Parsing_element_list)NULL) do {
#define END_FOR_LP_PARSING_ELEMENTS( head, cur )	\
	} while( ((cur) = (cur)->suc) != (head) ); }


/****************************************************************************************/
/*											*/
/*		LP_Derivation / LP_Derivation_list					*/
/*--------------------------------------------------------------------------------------*/
/*											*/
/*	LP_Derivation steht fuer jeweils eine Ableitung fuer ein LP_Parsing_element.	*/
/*	Es zeigt auf eine Liste von Parsing_elementen, die direkt im naechsten 		*/
/*	Ableitungsschritt aus dem abgeleiteten Parsing_element entsteht.		*/
/*											*/
/*	LP_Derivation_list dient zur Verkettung von LP_Derivation (doppelt verkettet	*/
/*	zyklisch ), da ja ein Parsing_element i.A. auf mehrere Arten abgeleitet werden	*/
/*	kann.										*/
/*											*/
/****************************************************************************************/

typedef	struct	LP_derivation
{
	/* Zeiger auf Liste von LP_Parsing_elementen */

		struct	LP_parsing_element_list	*parsing_elements;

	/* Parsing_element wurde abgeleitet; Verweis mit welcher Produktion */

		struct	sprod			*used_prod;
		struct	pe_production		*lams_prod;

		struct	lp_upper_production	*copy_iso;

		int				visited;

}	*LP_Derivation;


typedef	struct	LP_derivation_list
{
	/* Zeiger auf LP_derivation */

		struct	LP_derivation		*derivation;

	/* Verkettung */

		struct LP_derivation_list	*pre;
		struct LP_derivation_list	*suc;

}	*LP_Derivation_list;


/****** Makros zum Durchlaufen der LP_Derivation_list ******/

#define	FOR_LP_DERIVATIONS( head, cur )		\
	{ if( ((cur) = (head)) != (LP_Derivation_list)NULL) do {
#define END_FOR_LP_DERIVATIONS( head, cur )	\
	} while( ((cur) = (cur)->suc) != (head) ); }


/****************************************************************************************/
/*											*/
/* Datenstrukturen um die Kosten der verschiedenen Moeglichen Ableitungen auszurechnen.	*/
/* Haengt an jedem Parsing_element, das mindestens einen abgeleiteten Sohn hat (muss 	*/
/* also selbst abgeleitet worden sein).							*/
/*--------------------------------------------------------------------------------------*/
/*				Strukturbeschreibung					*/
/*											*/
/* (Beschreibung der einzelnen Strukturen: bei der jeweiligen Definition)		*/
/*											*/
/*											*/
/*											*/
/*			      (next)				  (next)		*/
/*	upper_production     -------->      upper_production     -------->		*/
/*	   |        |			       |        |				*/
/*	   |        ---->array_of_upper_p.     |        ---->array_of_upper_productions */
/*         |				       |					*/
/*	   |(lower_konbinations)	       |					*/
/*	   |				       |					*/
/*	   V				       V					*/
/*         lower_kombination  								*/
/*		|									*/
/*		|									*/
/*		|---->production_kombination						*/
/*		|	|								*/
/*		|	|								*/
/*		|	---->   production   ------>   production   ------>		*/
/*		|		     |							*/
/*		|		     ----> array_of_lower_productions			*/
/*		|									*/
/*		|---->production_kombination						*/
/*		|									*/
/*											*/
/*											*/
/****************************************************************************************/


/****************************************************************************************/
/*											*/
/*		 		lp_upper_production					*/
/*--------------------------------------------------------------------------------------*/
/*											*/
/* haengt an jedem PE, das einen abgeleiteten Sohn besitzt. Wird fuer jede moegliche 	*/
/* Ableitung des Parsing_elements angelegt. Repraesentant einer Menge von isomorphen	*/
/* Produktionen.									*/
/*											*/
/****************************************************************************************/

typedef	struct	lp_upper_production
{
	/****** Attribute ******/


	/****** Verkettung ******/

		struct	lp_upper_production		*suc;			/*** naechste Moeglichkeit das PE abzuleiten	***/
		struct	lp_upper_production		*pre;

	/****** Feld der Production_layouts ******/

		int					number_of_prod_layouts;	/*** Notwendig fuer Arrayverwaltung		***/
		struct	lp_array_of_productions		*production_layouts;	/*** array mit den layouts der Produktion	***/

	/****** Entsprechende derivation ******/

	 	struct	LP_derivation			*derivation;
	
}	*LP_upper_production;


/******************** Makros zum Durchlaufen der LP_upper_production ****************/

#define	FOR_LP_UPPER_PRODUCTION( head, cur )		\
	{ if( ((cur) = (head)) != (LP_upper_production)NULL) do {
#define END_FOR_LP_UPPER_PRODUCTION( head, cur )	\
	} while( ((cur) = (cur)->suc) != (head) ); }


/****************************************************************************************/
/*											*/
/*		 		lp_array_of_upper_productions				*/
/*--------------------------------------------------------------------------------------*/
/*											*/
/* Wird spaeter als array verwendet. Fuer jede der isomorphen Produktionen, die in	*/
/* einer Ableitung verwendet werden koennen, wird ein Arrayeintrag angelegt.		*/
/*											*/
/****************************************************************************************/

typedef	struct	lp_array_of_productions
{
	/****** Attribute ******/

		struct	lp_sizes_array			*SIZES;				/*** Feld der moeglichen optimalen Groessen	***/
		int					length_of_sizes;
		Sprod					prod_iso;			/*** Entsprechende Produktion			***/

	/****** Anfang der Liste der Moeglichen Ableitungen unten ******/

		struct lp_lower_derivation		*derivations_below;		/*** Liste der Ableitungen unten		***/

	/****** dependency ******/

		struct lp_dependency_graph		*x_dependency,
							*y_dependency;
	/****** Zeichnung ******/

		Snode					draw_iso;

}	*LP_array_of_productions;

/****************************************************************************************/
/*											*/
/*		 		lp_lower_derivation					*/
/*--------------------------------------------------------------------------------------*/
/*											*/
/* Kopf fuer jeweils eine Alternative der Ableitung unten. 				*/
/* Zeigt auf array der Produktionen, die angewendet werden muessen ueber den Zeiger	*/
/* 'Production_layouts'.								*/
/* Zeigt auf naechste Moegliche Ableitung mit 'next'. Dabei werden ALLE Moeglichkeiten	*/
/* in die Liste eingetragen, also Moeglichkeiten durch verschiedene Production_layouts	*/
/* fuer eine Produktion und verschiedene Moeglichkeiten der Ableitung der Knoten unten.	*/
/* Die Menge der moeglichen Groessen wird in sizes gespeichert (wenn 'alles unten auf	*/
/* oben' durchgefuehrt wird)								*/
/*											*/
/****************************************************************************************/

typedef	struct	lp_lower_derivation
{

	/****** Verkettung ******/

		struct	lp_lower_derivation		*pre;
		struct	lp_lower_derivation		*suc;

	/****** Array der Production_layouts fuer diese Moeglichkeit ******/

		int					number_of_productions;
		struct	lp_array_of_lower_productions	*productions;

	/****** Zeichnung ******/

		Snode					draw_iso;

}	*LP_lower_derivation;


/******************** Makros zum Durchlaufen der LP_lower_derivation ****************/

#define	FOR_LP_LOWER_DERIVATION( head, cur )		\
	{ if( ((cur) = (head)) != (LP_lower_derivation)NULL) do {
#define END_FOR_LP_LOWER_DERIVATION( head, cur )	\
	} while( ((cur) = (cur)->suc) != (head) ); }


/****************************************************************************************/
/*											*/
/*		 	lp_array_of_lower_productions					*/
/*--------------------------------------------------------------------------------------*/
/*											*/
/* Speicher fuer jeweils eine Production "unten". Wird als Array verwendet, um ALLE	*/
/* Produktionen gemeinsam zu haben, die notwendig sind, um EINEN Ableitungsschritt	*/
/* durchzufuehren. 									*/
/* Datenstruktur kennt dabei den Knoten, auf den sie angewendet werden muss.		*/
/* Zeiger 'father_node'.								*/
/* Ausserdem enthaelt sie einen Zeiger auf eine Liste auf die Entsprechung in den Be-	*/
/* rechnungsstrukturen eine Stufe weiter unten ('same_prod_lower') und kann damit auf	*/
/* die Menge der optimalen sizes* Zugreifen.						*/
/****************************************************************************************/

typedef	struct	lp_array_of_lower_productions
{
	/****** Knoten, in dem Produktion angewendet wird ******/

		struct	LP_parsing_element	*father_node;
		struct	parsing_element		*lams_prod_iso;	/* DIESER ZEIGER MUSS BLEIBEN (Siehe lp_create_layout_struc.c) */
		Snode				graphed_father;

	/****** Entsprechende derivation in unserer Datenstruktur ******/

		struct LP_derivation		*derivation;

	/****** Entsprechung eins weiter unten ******/

		struct lp_upper_production	*same_prod_lower;
		int				array_entry;

	/****** Attribute ******/

		int				w,
						h,		/*** Groesse der Produktion ***/
						entry_for_costs;/* Bei Kostenberechnung: Welcher Arrayeintrag */

	/****** Entsprechende Produktion ******/

		Sprod				production;

	/****** Dependency Graph, um beim Zeichnen Groessen zu kennen ******/

		struct	lp_dependency_graph	*x_dependency;
		struct	lp_dependency_graph	*y_dependency;
}	*LP_array_of_lower_productions;

/****************************************************************************************/
/*											*/
/*		 	lp_sizes_array							*/
/*--------------------------------------------------------------------------------------*/
/*											*/
/* Verwendet als array wird hier eine Liste von moeglichen Groessen gespeichert.	*/
/* Haengt dabei an lp_lower_derivation.							*/
/* 'is_in_optimal_sizes' ist dabei ein Flag, das in Moeglichkeiten mit optimaler	*/
/* Groesse auf TRUE gesetzt wird. Wird dann zum umkopieren in EIN Array mit allen 	*/
/* optimalen Moeglichkeiten genutzt.							*/
/* Diese Datenstruktur wird auch fuer das Array mit den optimalen Moeglichkeiten benutzt*/
/* Dieses haengt dann an lp_array_of_productions.					*/
/****************************************************************************************/

typedef	struct	lp_sizes_array
{
	/****** Optimale Groesse? ******/

		int					is_in_optimal_sizes;

	/****** Zeiger auf header mit passender Ableitung ******/

		struct	lp_lower_derivation		*used_derivation;

	/****** Zeiger auf die passende optimale Struktur unten mit der erzeugt wurde ******/

		int					nr_of_prods_below; /* Wieviele abgeleitete Knoten */
		struct	lp_sizes_ref			*used_productions;

	/****** Zeiger auf Datenstruktur an der sizes_array haengt (Wird beim Neuzeichnen benoetigt) ******/

		struct	lp_array_of_productions		*upper_prod_array;
		int					entry_in_upper_prod_array;

	/****** Attribute ******/

		int					w,
							h;

	/****** damit schoen gezeichnet werden kann ******/

		struct	lp_dependency_graph		*x_dependency,
							*y_dependency;
}	*LP_sizes_array;

/****************************************************************************************/
/*											*/
/*		 	lp_sizes_ref							*/
/*--------------------------------------------------------------------------------------*/
/* Ist eine Unterstruktur von lp_sizes_array. Hier steht genau wie das uebergeordnete	*/
/* lp_sizes_array aus lp_sizes_array's eine Stufe niedriger aufgebaut worden ist.	*/
/* Wird dann spaeter notwendig, um Groessenoptimal neuzuzeichnen.			*/
/****************************************************************************************/

typedef	struct	lp_sizes_ref
{
	struct	lp_sizes_array				*which_sizes_array;
	int						what_entry_in_array;

}	*LP_sizes_ref;	/****** Hier steht, wie spaeter weitergezeichnet werden muss ******/


/****************************************************************************************/
/*											*/
/* Folgende Datenstruktur wird nur benoetigt, um bequem die Datenstrukturen zum	Layout-	*/
/* berechnen erzeugen zu koennen							*/
/*											*/
/****************************************************************************************/


typedef	struct	lp_copy_array
{
	Sprod				prod;
	struct	LP_derivation		*derivation;
	struct	LP_parsing_element	*derivated_node;
	struct	LP_parsing_element_list	*pe_list;
	struct	lp_copy_array		*next;

}	*LP_copy_array;

/****************************************************************************************/
/*											*/
/* Die folgende Datenstruktur dient dazu bequem auszurechnen, wie gross der Graph wird	*/
/* wenn in einer Produktion alle ersetzten Knoten durch die neue Groesse ersetzt werden */
/*											*/
/****************************************************************************************/

typedef	enum		/* auf welcher Seite eines Knoten (auch LHS_Node) liegt Segment	*/
{
	LEFT,
	RIGHT,
	UP,
	DOWN

}	SIDE;

typedef	struct	lp_dependency_graph	/* Wenn x-Abh.: coord = x, y-Abh.: coord = y	*/
{
	struct	lp_dependency_graph		*first_border_part,
						*copy_iso,
						*next;
	Snode					node_in_prod;
	int					prod_nr; /* welcher Eintr. in Prod_array*/
	unsigned short				original_coord,
							/* Ab hier: Was ist, wenn Graphgroessen wichtig */
						original_coord_with_graph_sizes,
							/* Ab hier: Immer neu auszurechnende Attribute	*/
						new_coord;
	short					side;

}	*LP_dependency_graph;

#endif
