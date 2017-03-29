#ifndef __LPA_GRAPH_H__
#define __LPA_GRAPH_H__

/************************************************************************/
/*									*/
/*	Datenstrukturen fuer Area-Optimierung				*/
/*									*/
/************************************************************************/

/*************************************************************************
Diese Datenstrukturen dienen zur sinnvollen Verwaltung von isomorphen
Produktionen mit fuer uns wichtigen Layout-Informationen.

LPA_Array_of_productions:
	Die Verwaltungseinheit, die notwendig ist, um das Array als eigen-
	staendige Struktur zu verwalten, da die Laenge gespeichert werden
	muss.

	-number: 	# der Elemente im Array
	-array_head:	-> auf erstes Element im Array

LPA_Productions:
	Das eigentliche Array der Produktionen mit den fuer uns wichtigen
	Layoutinformationen:

	-production	->zugehoerige Graphed-Produktion
	-width		Breite der Produktion
	-height		Hoehe der Produktion
	-skaled_width	Breite der Produktion / gridwidth
	-skaled_height	Hoehe der Porduktion / gridwidth
	-grid		Grid auf den die Produktion gezeichnet ist
	-x_dependency	->auf x_Abhaengigkeitsgraph der Produktion
	-y_dependency	->auf y_Abhaengigkeitsgraph der Produktion

*************************************************************************/

typedef	struct lpa_array_of_productions
{
	int			number;
	struct	lpa_production	*array_head;

}	*LPA_Array_of_productions;



typedef	struct	lpa_production
{
	struct	graph		*production;
	
	int			width, height,
				skaled_width, skaled_height,
				grid;

	struct	lpa_dependency	*x_dependency;
	struct	lpa_dependency	*y_dependency;

}	*LPA_Production;

/*************************************************************************
SIDE:
	Zur Spezifikation einer Seite

LPA_Dependency:
	Diese Datenstruktur speichert einen Abhaengigkeitsgraphen einer 
	Produktion in x- oder y-Richtung. Kann verwendet werden zum 
	genauen spezifizieren einer Produktion und zum Ausrechnen des 
	optimierten Layouts (dazu dienen alle Integer-Eintraege ausser
	`prod_coord`)

	-first_border	-> linke (obere) Seite des selben Knoten
	-next		-> auf naechstes Element ( Verkettung )
	-copy_iso	-> auf Kopie beim Kopieren um "first_border"
			   Problemlos zu generieren
	-prod_node	-> auf Entsprechung in der Produktion
	-graph_node	-> auf Entsprechung im Graphen
	-tree_node	-> auf entsprechenden Knoten im Ableitungsbaum
	-side		Seite auf der dieses Segment liegt
	-derivation	-> auf den LPA_Sizes, mit dem der Knoten abge-
			leitet wurde, wenn er abgeleitet wurde
	-der_nr		Nr des Array-Eintrags von derivation

	-prod_coord	Koordinate in der Produktion	
	-new_coord	Wo liegt diese Seite, wenn unten andere 
			Produktionen angewendet werden
	-old_coord_plus 
	-new_coord_plus	Dienen beide zum ausrechnen einer Verschiebung

	-skaled_...	Wie die entsprechenden Werte ohne skaled, jetzt
			jedoch Groessen bezogen auf grid

	-grid		Grid auf den die Produktion gezeichnet ist

*************************************************************************/

typedef	enum
{
	LEFT,
	RIGHT,
	UP,
	DOWN

}	SIDE;

typedef	struct	lpa_dependency	
{
	struct	lpa_dependency		*first_border,
					*next,
					*copy_iso;
	struct	node			*prod_node;
	struct	node			*graph_node;
	struct	tree_node_rec		*tree_node;
	SIDE				side;
	struct	lpa_sizes		*derivation;
	int				der_nr;

	int				prod_coord,
					new_coord,
					old_coord_plus,
					new_coord_plus,

					skaled_prod_coord,
					skaled_new_coord,
					skaled_old_coord_plus,
					skaled_new_coord_plus,

					grid;

}	*LPA_Dependency;

/*************************************************************************
Diese Datenstrukturen dienen zur sinnvollen Verwaltung von isomorphen
Knoten um schnellen Zugriff bei isomorphen Produktionen zu gewaehrleisten.

LPA_Array_of_nodes:
	Die Verwaltungseinheit, die notwendig ist, um das Array als eigen-
	staendige Struktur zu verwalten, da die Laenge gespeichert werden
	muss.

	-number: 	# der Elemente im Array
	-array_head:	-> auf erstes Element im Array

LPA_Node:
	Das eigentliche Array der Knoten:

	-node		-> auf zugehoerigen Graphed-Knoten

*************************************************************************/

typedef	struct lpa_array_of_nodes
{
	int			number;
	struct	lpa_node	*array_head;

}	*LPA_Array_of_nodes;



typedef	struct	lpa_node
{
	struct	node		*node;
	
}	*LPA_Node;

/*************************************************************************
Datenstruktur zur eigentlichen Attributberechnung. Haengt direkt an jedem
tree_node_ref, das mind. einen abgeleiteten Sohn hat.
AUSNAHME: An der Wurzel haengt diese Datenstruktur IMMER, auch wenn kein 
	  abgeleiteter Sohn existiert.

LPA_Upper_prod_array:
	Zur Verwaltung der Datenstrukturen als eigenstaendige Struktur,
	da array_laenge gespeichert werden muss

	-number		# der Elemente im Array
	array_head	-> auf erstes Element im Array

LPA_Upper_prod:
	Da immer eine Aufeinanderfolge von zwei Produktionen betrachtet
	werden muss, wird eine Struktur fuer oben, eine fuer unten
	benoetigt. Hier die Struktur fuer oben

	-lower_array	-> auf untere Struktur
	-der_nr		# moeglichen Ableitungen
	-node_nr	# abgeleitete Knoten in Soehnen
			( Untere Struktur als 2-dim. Feld, daher 2 * # )
	-tree_node	-> auf Baumelement, an dem Struktur haengt
	-prod_layout	-> Layoutinformation (Dependency)
	-prod_array_nr	Wievielter Eintrag in Array von Dependency
	-sizes		-> array der moeglichen Groessen
			(BEACHTE: Auch lower_array zeigt auf die selbe
				  Speicheradresse )
	-sizes_nr	# sizes

*************************************************************************/

typedef	struct	lpa_upper_prod_array
{
	int			number;
	struct	lpa_upper_prod	*array_head;

}	*LPA_Upper_prod_array;


typedef	struct	lpa_upper_prod
{
	struct	lpa_lower_prod	*lower_array;
	int			der_nr,
				node_nr;
	struct	tree_node_rec	*tree_node;
	struct	lpa_production	*prod_layout;
	int			prod_array_nr;
	struct	lpa_sizes	*sizes;
	int			sizes_nr;

}	*LPA_Upper_prod;

/*************************************************************************
Datenstruktur zur eigentlichen Attributberechnung. Haengt direkt an jeder
LPA_Upper_prod.
AUSNAHME: An der Wurzel kann diese Struktur fehlen, da Wurzel nicht 
	  zwingend einen abgeleiteten Sohn hat.

LPA_Lower_prod:
	Da immer eine Aufeinanderfolge von zwei Produktionen betrachtet
	werden muss, wird eine Struktur fuer oben, eine fuer unten
	benoetigt. Hier die Struktur fuer unten.
	BEACHTE: Zweidimensionales Array mit:
		 Reihen:	Jeweils genau eine Ableitung
		 Spalten:	Liste der moeglichen Ableitungen
	ACHTUNG: Dieses zwei-dimensionale Array wird intern als ein-
		 dimensionales Array realisiert. Deshalb ZUGRIFF IMMER NUR
		 UEBER MAKRO ADR.

	-upper_prod	-> obere Produktion
	-lower_prod	-> auf untere Produktion
	-same_lower	-> auf selbe Produktion eins tiefer
	-lower_nr	Wievielter Eintrag im array
	-sizes		-> array der moeglichen Groessen
			(BEACHTE: Auch lower_array zeigt auf die selbe
				  Speicheradresse )
	-sizes_nr	# sizes
	-what_sizes	Hilfsvariable um einfacher ueber alle moeglichen 
			Ableitungen (bzgl. Groessen) zu permutieren
			Ist Index welcher Eintrag im Sizes-array zur 
			Berechnung der weiteren Ableitung
	-finished	Beim durchpermutieren ueber Ableitungen damit fertig

*************************************************************************/

typedef	struct lpa_lower_prod
{
	struct	graph		*upper_prod,
				*lower_prod;
	struct	lpa_upper_prod	*same_lower;
	int			lower_nr;
	struct	lpa_sizes	*sizes;
	int			sizes_nr;
	int			what_sizes;
	int			finished;

}	*LPA_Lower_prod;


/*** Makro, um an richtigen Ort im Array zu kommen ***/
/*** x <=> Zeile, y <=> Spalte, z <=> Zeilenlaenge ***/

#define	ADR( x, y, z)  (int)((x)*(z) +(y))
 
/*************************************************************************
Datenstruktur zur eigentlichen Attributberechnung. Haengt direkt an jeder
LPA_Upper_prod und LPA_Lower_prod.
BEACHTE: &(LPA_Lower_prod->sizes) = &(LPA_Lower_prod->same_lower->sizes)
	 Beide zeigen also auf gleiche Struktur, waere also von 
	 LPA_Lower_prod aus unnoetig, machts aber vielleicht im Programm
	 kuerzer

LPA_Sizes:
	Ist ein Array aller moeglichen Groessen, die einem bestimmten, 
	abgeleiteten Knoten zugeordnet sein koennen.

	-width		Breite bei Ableitung mit dieser Moeglichkeit
	-height		Hoehe bei Ableitung mit dieser Moeglichkeit
	-skaled_...	Wie die entsprechenden Werte ohne skaled, jetzt
			jedoch Groessen bezogen auf grid
	-grid		Grid auf den die Produktion gezeichnet ist
	-prod_layout	-> auf entsprechende Dependency am tree_node
			ACHTUNG: Darf NIE veraendert werden.
	-prod_array_nr	Welcher Eintrag im Array von Layouts
	-x_dependency	-> auf eigene lokale x_dependency mit NEU
			berechneten Groessen
	-y_dependency	-> auf eigene lokale y_dependency mit NEU
			berechneten Groessen
	-is_in_opt	Zeigt fuer Kompaktifizieren an, ob im Optimalen

*************************************************************************/

typedef	struct	lpa_sizes
{
	int			width, height,
				skaled_width, skaled_height,
				grid;
	struct	lpa_production	*prod_layout;
	int			prod_array_nr;
	struct	lpa_dependency	*x_dependency;
	struct	lpa_dependency	*y_dependency;
	int			is_in_opt;

}	*LPA_Sizes;


#endif
