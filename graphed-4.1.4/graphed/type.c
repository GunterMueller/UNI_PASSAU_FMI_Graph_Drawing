/* (C) Universitaet Passau 1986-1994 */
/* GraphEd Source, 1986-1994 by Michael Himsolt, Thomas Lamshoeft,	*/
/*                              Uwe Schnieders				*/
/************************************************************************/
/*									*/
/*				type.c					*/
/*									*/
/************************************************************************/
/*									*/
/*	Dieses Modul verwaltet Knoten- und Kantentypen.			*/
/*									*/
/************************************************************************/

#include "misc.h"
#include "graph.h"

#include <xview/panel.h>
#include <xview/icon_load.h>

#include "install.h"
#include "load.h"
#include "type.h"
#include "graphed_svi.h"
#include "paint.h"
#include "repaint.h"
#include "adjust.h"
#include "ps.h"	/* PS_FELSBERG */

#include "X11/Xutil.h"

extern void install_nodetypelist_in_group_subframe (char **list);
extern void install_nodetypelist_in_node_defaults_subframe (char **list);
extern void install_edgetypelist_in_group_subframe (char **list);
extern void install_edgetypelist_in_edge_defaults_subframe (char **list);


/************************************************************************/
/*									*/
/*			GLOBALE FUNKTIONEN				*/
/*									*/
/************************************************************************/
/*									*/
/*	int		insert_nodetype (filename, insert_position)	*/
/*	int		insert_edgetype (filename, insert_position)	*/
/*	int		add_nodetype    (filename)			*/
/*	int		add_edgetype    (filename)			*/
/*	int		delete_nodetype (delete_position)		*/
/*	int		delete_edgetype (delete_position)		*/
/*									*/
/*	Nodetype	get_current_nodetype ()				*/
/*	Edgetype	get_current_edgetype ()				*/
/*									*/
/*	Nodetype	use_nodetype (nodetype_index)			*/
/*	Edgetype	use_edgetype (edgetype_index)			*/
/*	void		unuse_nodetype (nodetype)			*/
/*	void		unuse_nodetype (nodetype)			*/
/*	Nodetypeimage	use_nodetypeimage   (type, sx,sy)		*/
/*	void		unuse_nodetypeimage (type, image)		*/
/*									*/
/*	int		get_nodetype_index (type)			*/
/*	int		get_edgetype_index (type)			*/
/*	int		find_nodetype (filename)			*/
/*	int		find_edgetype (filename)			*/
/*      Nodetype	get_nodetype  (index);				*/
/*									*/
/*	void		install_current_nodetype ()			*/
/*	void		install_current_edgetype ()			*/
/*									*/
/*	void		init_types ()					*/
/*	void		write_nodetypes (file)				*/
/*	void		write_edgetypes (file)				*/
/*									*/
/************************************************************************/



/************************************************************************/
/*									*/
/*			LOKALE VARIABLEN				*/
/*									*/
/************************************************************************/


static	struct	{
  int		number_of_nodetypes;
  Nodetype	list            [MAX_NODETYPE];
  char		*list_for_cycle [MAX_NODETYPE+3];
}
  nodetypes;

static	struct	{
  int		number_of_edgetypes;
  Edgetype	list            [MAX_EDGETYPE];
  char		*list_for_cycle [MAX_EDGETYPE+3];
}
  edgetypes;


static int number_of_system_nodetypes = 0;
static System_nodetype system_nodetypes[MAX_SYSTEM_NODETYPE];


static	char	pattern_very_short_dotted[] = { 1 , 1 , 0 };
static	char	pattern_short_dotted[]      = { 2 , 2 , 0 };
static	char	pattern_dotted[]            = { 3 , 4 , 0 };
static	char	pattern_dashed[]            = { 12 , 4 , 0 };
static	char	pattern_dashdot[]           = { 20 , 4 , 4 , 4 , 0 };
static	char	pattern_dashdotdotted[]     = { 12 , 4 , 4 , 4 , 4 , 4 , 0 };
static	char	pattern_longdashed[]        = { 26 , 6 , 0 };
static	char	pattern_invisible[]         = { 0 };


#define NUMBER_OF_SYSTEM_EDGETYPES 9

static System_edgetype system_edgetypes [NUMBER_OF_SYSTEM_EDGETYPES] = {
  {"#solid",           NULL},
  {"#veryshortdotted", pattern_very_short_dotted},
  {"#shortdotted",     pattern_short_dotted},
  {"#dotted",          pattern_dotted},
  {"#dashed",          pattern_dashed},
  {"#dashdotted",      pattern_dashdot},
  {"#dashdotdotted",   pattern_dashdotdotted},
  {"#longdashed",      pattern_longdashed},
  {"#invisible",       pattern_invisible}
};
	
	
/************************************************************************/
/*									*/
/*			LOKALE PROZEDUREN				*/
/*									*/
/************************************************************************/


void	install_nodetypelist_for_cycle (void);
void	install_edgetypelist_for_cycle (void);


/************************************************************************/
/*									*/
/*		KNOTEN- UND KANTENZEICHENTYPEN : DATENSTRUKTUR 		*/
/*									*/
/************************************************************************/
/*									*/
/*	So sieht ein Knotentyp aus  :					*/
/*									*/
/*	typedef	struct	nodetype					*/
/*	{								*/
/*			Nodetypeimage	images;				*/
/*			Pixrect	*pr;				*/
/*			char		*filename;			*/
/*			int		used;				*/
/*			int		is_system;			*/
/*			void		(*adjust_func)();		*/
/*			void		(*pr_paint_func)();		*/
/*			void		(*ps_paint_func)();		*/
/*	}								*/
/*		*Nodetype;						*/
/*									*/
/*									*/
/*	images		Liste mit Bildern dieses Knotentyps.		*/
/*			Details siehe Nodetypeimage.			*/
/*	pr		Pixrect mit dem (Muster-) Knotenbild		*/
/*	filename	Name des Knotentyps. Bei systemdefinierten	*/
/*			Knotentypen steht hier der Name in		*/
/*			system_nodetypes[?]->name, sonst der Dateiname	*/
/*			des Icons, aus dem der Knotentyp gewonnen wird.	*/
/*	used		Zaehler, wie oft type und type->images		*/
/*			verwendet werden. Loeschen des Knotentyps ist	*/
/*			nur gestattet, wenn used == 0.			*/
/*	is_system	Gibt an, ob es sich um einen vordefinierten	*/
/*			Knotentyp (TRUE) handelt oder um einen aus	*/
/*			Icon synthetisierten (s.u.).			*/
/*	pr_paint_func	Gibt bei vordefinierten Knotentypen die		*/
/*			Prozedur an, mit der dieser Knotentyp		*/
/*			auf ein Pixrect gezeichnet wird			*/
/*	adjust_func	Gibt bei vordefinierten Knotentypen die		*/
/*			Prozedur an, die Kanten an den Knoten anpasst.	*/
/*									*/
/*	Zur Unterscheidung vordefinierter - aus Icon gewonnener		*/
/*	Knotentyp :							*/
/*	Vordefinierte Knotentypen besitzen spezielle Prozeduren zum	*/
/*	Zeichnen und Anpassen von Kanten. Alle vordefinierten Knoten-	*/
/*	typen sind in einer Liste system_nodetypes abgespeichert; es	*/
/*	gibt insgesamt number_of_system_nodetypes von ihnen. Das	*/
/*	"Laden"	eines vordefinierten Knotentyps wird ueber die		*/
/*	"normale" Prozedur insert_nodetype (bzw. add_nodetype)		*/
/*	vorgenommen, wobei der Dateiname mit dem Zeichen		*/
/*	SYSTEM_TYPES_IDENTIFICATION_CHARACTER ('#') zu beginnen hat.	*/
/*	Aus Icons gewonnene Knotentypen sind de facto Pixelmuster.	*/
/*	Fuer sie sind keine speziellen Prozeduren zum Zeichnen und	*/
/*	Anpassen noetig. Das Laden erfolgt ueber insert_nodetype (bzw.	*/
/*	add_nodetype). Vergroessern und Verkleinern dieser Pixelmuster	*/
/*	wird dann durch Saklierung des "Musters" (type->pr)		*/
/*	bewerkstelligt.							*/
/*									*/
/*======================================================================*/
/*									*/
/*	typedef	struct	nodetypeimage {					*/
/*		int			sx,sy;				*/
/*		Pixrect		*pr;				*/
/*		int			used;				*/
/*		struct	nodetypeimage	*pre, *suc;			*/
/*	}								*/
/*		*Nodetypeimage;						*/
/*									*/
/*	sx,sy		Groesse von pr.					*/
/*	used		Zaehler, wie oft dieses Bild verwendet wird.	*/
/*			Ist used == 0, so wird das Bild geloescht !	*/
/*	pre, suc	alle images zu einem Knoten sind in einer	*/
/*			doppelt verketteten, nicht geschlossenen Liste	*/
/*			organisiert.					*/
/*									*/
/*	Um Speicherplatz und Rechenzeit zu sparen, werden alle Bilder	*/
/*	eines Knotentyps, die gleiche Groesse haben, nur einmal		*/
/*	erzeugt und ueber node->image angesprochen. Die Liste aller	*/
/*	Bilder befindet sich in type->images.				*/
/*									*/
/*======================================================================*/
/*									*/
/*	Liste der vordefinierten Knotentypen :				*/
/*									*/
/*	struct {							*/
/*		char	*name;			type->filename		*/
/*		void	(*adjust_func)();	type->adjust_func	*/
/*		void	(*pr_paint_func)();	type->pr_paint_func	*/
/*	}								*/
/*		system_nodetypes;					*/
/*									*/
/*	Zur Zeit verfuegbar sind :					*/
/*	- #box, mit adjust_to_box_node und paint_box_node		*/
/*	- #circle, mit adjust_to_elliptical_node und			*/
/*	  paint_elliptical_node						*/
/*	- #diamond, mit adjust_to_elliptical_node und			*/
/*	  paint_elliptical_node						*/
/*									*/
/*======================================================================*/
/*									*/
/*	So sieht ein Kantentyp aus :					*/
/*									*/
/*	typedef	struct	edgetype					*/
/*	{								*/
/*			Pixrect	*pr;				*/
/*			char		filename[FILENAMESIZE];		*/
/*			int		used;				*/
/*	}								*/
/*		*Edgetype;						*/
/*									*/
/*									*/
/*	pr		Pixrect mit dem Kantenbild, aus dem einmal	*/
/*			Informationen ueber Strichbreite und Strichform	*/
/*			gewonnen derden sollen.				*/
/*	filename	In diesem File steht das Icon, aus dem pr	*/
/*			geladen wird.					*/
/*	used		Sooft wird der Kantentyp verwendet. Ein Typ	*/
/*			darf nur geloescht werden, wenn used == 0.	*/
/*									*/
/*	Kantentypen sind bis jetzt fuer das Zeichnen ohne Bedeutung,	*/
/*	werden aber in der Hoffnung auf eine spaetere Verwendung	*/
/*	mitgefuehrt.							*/
/*									*/
/*======================================================================*/
/*									*/
/*	Alle Knoten- und Kantentypen sind in Listen abgespeichert :	*/
/*									*/
/*	static	struct	{						*/
/*			int	    number_of_nodetypes;		*/
/*			Nodetype    list            [MAX_NODETYPE];	*/
/*			char	    *list_for_cycle [MAX_NODETYPE+3];	*/
/*		}							*/
/*		nodetypes;						*/
/*									*/	
/*	static	struct	{						*/
/*			int	    number_of_edgetypes;		*/
/*			Edgetype    list            [MAX_EDGETYPE];	*/
/*			char	    *list_for_cycle [MAX_EDGETYPE+3];	*/
/*		}							*/
/*		edgetypes;						*/
/*									*/
/*									*/
/*	MAX_EDGETYPES		Maximalzahl an moeglichen Typen		*/
/*	MAX_NODETYPES		fuer Knoten bzw. Kanten; deklariert	*/
/*				in config.h			*/
/*	number_of_nodetypes	Anzahl der vorhandenen Typen,		*/
/*	number_of_edgetypes	durch MAX_NODETYPES bzw. MAX_EDGETYPES	*/
/*				beschraenkt				*/
/*	list			Liste der Knoten- bzw. Kantentypen	*/
/*	list_for_cycle		Liste, mit denen die Knoten- bzw.	*/
/*				Kantentypen in Panel_cycle's abgebildet	*/
/*				werden koennen. Details siehe unten.	*/
/*									*/
/*									*/
/*	ACHTUNG : Diese Tabelle ist ausserhalb von type.c nicht		*/
/*	sichtbar. Der Zugriff erfolgt von aussen ueber die Indices	*/
/*									*/
/*	current_nodetype_index	Index des aktuellen Knotentyps, MAKRO !	*/
/*	current_edgetype_index	Index des aktuellen Kantentyps, MAKRO !	*/
/*									*/
/*	Die Indices fuer Knoten- bzw. Kantentypen liegen in dem		*/
/*	Intervall [0..nodetypes.number_of_types-1] bzw.			*/
/*	          [0..edgetypes.number_of_types-1].			*/
/*	Die "Variablen" liegen in Wirklichkeit in graph_state		*/
/*	(-> graph.h)							*/
/*									*/
/*	DER INDEX DES CURRENT_NODE/EDGEFONT KANN SICH NACH INSERT_FONT	*/
/*	ODER DELETE_FONT AENDERN !					*/
/*									*/
/*									*/
/*	Format von list_for_cycle :					*/
/*									*/
/*	PANEL_CHOICE_IMAGES						*/
/*		.							*/
/*		.							*/
/*		.							*/
/*		.							*/
/*		(nodetype_images/edgetype_images)			*/
/*		.							*/
/*		.							*/
/*		.							*/
/*		0   (Ende Images)					*/
/*	0   (Ende Liste)						*/
/*									*/
/*	Diese Liste kann an ein PANEL_CYCLE als ATTR_LIST uebergeben	*/
/*	werden. Dann erscheinen die Bilder der Knotentypen bzw.		*/
/*	Kantentypen (pr in Nodetype bzw. Edgetype) im cycle-Menue.	*/
/*									*/
/************************************************************************/
/************************************************************************/
/*									*/
/*	KNOTEN- UND KANTENTYPEN EINFUEGEN UND LOESCHEN			*/
/*									*/
/************************************************************************/
/*									*/
/*	int	insert_nodetype (filename, insert_position)		*/
/*	int	insert_edgetype (filename, insert_position)		*/
/*									*/
/*	Fuegt einen Knoten- bzw. Kantentyp aus filename ein. Rueckgabe	*/
/*	ist der Index, an dem tatsaechlich eingefuegt wurde :		*/
/*	Strategie :							*/
/*	- gibt es den Typ mit filename schon, so melde nur seinen Index	*/
/*	  (insert_position wird in diesem Fall ausser Acht gelassen).	*/
/*	  Sonst :							*/
/*	- ist insert_postion < ...types.number_of_..._types, so fuege	*/
/*	  vor insert_position ein					*/
/*	- ist insert_position = types.number_of_types, so haenge an	*/
/*	  die Liste an.							*/
/*									*/
/*	Kann die Datei unter filename nicht gefunden werden und		*/
/*	ist filename kein absoluter Pfad, so wird in den im		*/
/*	Environment unter GRAPHED_INPUTS angebenenen Directories	*/
/*	nachgesehen							*/
/*									*/
/*	Falls schon zu viele Typen da sind oder das File nicht		*/
/*	geoeffnet werden kann, wird eine Fehlermeldung ausgegeben und	*/
/*	anstatt eines Index -1 zurueckgegeben.				*/
/*									*/
/*----------------------------------------------------------------------*/
/*									*/
/*	int	add_nodetype (filename)					*/
/*	int	add_edgetype (filename)					*/
/*									*/
/*	= insert_...type (filename, ...types.number_of_...types, d.h.	*/
/*	Anhangen an die Liste, wenn nicht schon vorhanden.		*/
/*									*/
/*----------------------------------------------------------------------*/
/*									*/
/*	int	delete_nodetype (delete_position)			*/
/*	int	delete_edgetype (delete_position)			*/
/*									*/
/*	Loescht den Knoten- bzw. Kantentyp mit Index delete_position.	*/
/*	Strategie :							*/
/*	- delete_position = current_...type_index > 0 :			*/
/*	  neuer current_...type_index wird der Knoten- bzw. Kantentyp,	*/
/*	  der vor delete_position liegt					*/
/*	- delete_position = current_...type_index = 0 :			*/
/*	  neuer current_...type_index wird der neue erste Knoten- bzw.	*/
/*	  Kantentyp.							*/
/*									*/
/*	Ist nur noch ein Typ uebrig, so darf dieser sinnvollerweise	*/
/*	nicht auch noch geloescht werden. Dann wird eine Fehlermeldung	*/
/*	aus- und FALSE zurueckgegeben, ansonsten natuerlich TRUE.	*/
/*									*/
/*----------------------------------------------------------------------*/
/*									*/
/*	ACHTUNG * WICHTIG * ACHTUNG * WICHTIG * ACHTUNG * WICHTIG	*/
/*									*/
/*	Alle Prozeduren lassen den current_nodetype bzw.		*/
/*	current_edgetype unveraendert. Dazu ist es notwendig, bei	*/
/*	insert_nodetype und delete_nodetype bzw. insert_edgetype und	*/
/*	delete_edgetype ggf. den current_nodetype_index bzw.		*/
/*	current_edgetype_index zu aendern.				*/
/*									*/
/************************************************************************/


int		insert_nodetype (char *filename, int insert_position)
{
  Nodetype	new_nodetype;
  char		*extended_filename;
  int		i;
  Pixmap          pm;
  int		x_hot, y_hot;
  unsigned int	width, height;
  int		value;


  if ((i = find_nodetype(filename)) != -1) {
    return i;
  }
  if (nodetypes.number_of_nodetypes == MAX_NODETYPE) {
    error ("Can't insert a new type - too many types\n");
    return (-1);
  }

  new_nodetype = (Nodetype) mymalloc(sizeof(struct nodetype));
  
  if (filename[0] == SYSTEM_TYPES_IDENTIFICATION_CHARACTER) {
    
    /* Lade Knotentyp aus system_nodetypes	*/
    
    int	found_system_nodetype = FALSE;
		
    for (i=0; !found_system_nodetype && i < number_of_system_nodetypes; i++)
      if (!strcmp (filename, system_nodetypes[i]->name)) {

	found_system_nodetype   = TRUE;
	new_nodetype->is_system = TRUE;
	new_nodetype->pm_paint_func  = system_nodetypes[i]->pm_paint_func;
	new_nodetype->ps_paint_func  = system_nodetypes[i]->ps_paint_func;
	new_nodetype->adjust_func = system_nodetypes[i]->adjust_func;
	new_nodetype->pm = XCreatePixmap(display,xwin,
					 (DEFAULT_ICON_WIDTH/2),
					 (DEFAULT_ICON_HEIGHT/2),
					 DEFAULT_ICON_DEPTH);
	if (new_nodetype->pm == (Pixmap)NULL) {
	  error ("Insufficient memory for nodetype\n");
	  return (-1);
	}

	/** das pixmap loeschen **/
	XSetFunction(display, pixmap_gc, GXclear);
	XFillRectangle(display, new_nodetype->pm, pixmap_gc,
		       0,0, (DEFAULT_ICON_WIDTH/2), (DEFAULT_ICON_HEIGHT/2));
	XSetFunction(display, pixmap_gc, GXcopy);
	paint_nodetype_on_pm (new_nodetype, new_nodetype->pm,
			      (DEFAULT_ICON_WIDTH/2),
			      (DEFAULT_ICON_HEIGHT/2),
			      DEFAULT_ICON_DEPTH);
	/** new_nodetype->pr->pr_size.x, new_nodetype->pr->pr_size.y, 1);**/
      }

    if (!found_system_nodetype) {
      myfree (new_nodetype);
      error ("Can't find system nodetype %s\n", filename);
      return (-1);
    }

  } else {

    new_nodetype->is_system = FALSE;
    extended_filename = file_exists_somewhere (filename,
      getenv ("GRAPHED_INPUTS"));
    new_nodetype->ps_paint_func = ps_paint_pixmap_node;

    if (extended_filename == NULL) {
      myfree (new_nodetype);
      error ("Cannot find file %s anywhere\n", filename);
      return (-1);
    } else { 
      
      value = XReadBitmapFile (display, xwin,
			       extended_filename,
			       &width,&height,
			       &(new_nodetype->pm),
			       &x_hot,&y_hot);
      
      if (value != BitmapSuccess || new_nodetype->pm == (Pixmap)NULL) {
	myfree (new_nodetype);
	error ("Cannot load nodetype\n");
	return (-1);
      }
    }
    
  }
	
  new_nodetype->images = (Nodetypeimage)NULL;
  new_nodetype->used = 0;
  strcpy (new_nodetype->filename = mymalloc(strlen(filename)+1), filename);

  for (i = nodetypes.number_of_nodetypes; i > insert_position; i--)
    nodetypes.list[i] = nodetypes.list[i-1];
  nodetypes.list[insert_position] = new_nodetype;
	
  for (i = nodetypes.number_of_nodetypes+3; i > insert_position+1; i--)
    nodetypes.list_for_cycle[i] = nodetypes.list_for_cycle[i-1];
  
  /** statt Pixrect nun ein Pixmap erzeugen **/
  pm = XCreatePixmap(display, xwin,
		     (DEFAULT_ICON_WIDTH/2),
		     (DEFAULT_ICON_HEIGHT/2),
		     DEFAULT_ICON_DEPTH);
  /** das pixmap loeschen **/
  XSetFunction(display, pixmap_gc, GXclear);
  XFillRectangle(display, pm, pixmap_gc,
		 0,0,
		 (DEFAULT_ICON_WIDTH/2),
		 (DEFAULT_ICON_HEIGHT/2));
  XSetFunction(display, pixmap_gc, GXcopy);
  paint_nodetype_on_pm (new_nodetype, pm,
			(DEFAULT_ICON_WIDTH/2),
			(DEFAULT_ICON_HEIGHT/2),
			DEFAULT_ICON_DEPTH);
  nodetypes.list_for_cycle[insert_position+1] = (char *)pm_to_svi(pm);

  nodetypes.number_of_nodetypes ++;

  install_nodetypelist_for_cycle ();
  if (current_nodetype_index >= insert_position) {
    set_current_nodetype (current_nodetype_index + 1);
  }
  for (i=0; i<NUMBER_OF_NODE_STYLES; i++) {
    Node_attributes	node_attr;
    node_attr = get_node_style (i);
    if (node_attr.type_index >= insert_position) {
      node_attr.type_index++;
      set_node_style (i, node_attr);
    }
  }

  return(insert_position);
}


int		insert_edgetype (char *filename, int insert_position)
{
  Edgetype		new_edgetype;
  int			i;

  if ((i = find_edgetype(filename)) != -1) {
    return i;
  }
  if (edgetypes.number_of_edgetypes == MAX_EDGETYPE) {
    error ("Can't insert a new type - too many types\n");
    return (-1);
  }

  new_edgetype = (Edgetype) mymalloc(sizeof(struct edgetype));

  if (filename[0] == SYSTEM_TYPES_IDENTIFICATION_CHARACTER) {

    int	found_system_edgetype = FALSE;
		
    for (i=0; !found_system_edgetype &&
	 i < NUMBER_OF_SYSTEM_EDGETYPES; i++) {
      if (!strcmp (filename, system_edgetypes[i].name)) {
	found_system_edgetype   = TRUE;
	
	strcpy (new_edgetype->filename = mymalloc(strlen(filename)+1),
		filename);
	new_edgetype->used = 0;
	new_edgetype->pattern = system_edgetypes[i].pattern;
	
	new_edgetype->pm = (Pixmap)XCreatePixmap(display,xwin,
						 (DEFAULT_ICON_WIDTH/2),
						 (DEFAULT_ICON_HEIGHT/2), 
						 DEFAULT_ICON_DEPTH);
	if (new_edgetype->pm == (Pixmap)NULL) {
	  error ("Insufficient memory for edgetype\n");
	  return (-1);
	}
	
	/** das pixmap loeschen **/
	XSetFunction(display,pixmap_gc,GXclear);
	XFillRectangle(display,new_edgetype->pm,pixmap_gc,
		       0,0,
		       (DEFAULT_ICON_WIDTH/2), (DEFAULT_ICON_HEIGHT/2));
	XSetFunction(display,pixmap_gc,GXcopy);
	paint_edgetype_on_pm (new_edgetype, new_edgetype->pm,
			      (DEFAULT_ICON_WIDTH/2),
			      (DEFAULT_ICON_HEIGHT/2),
			      DEFAULT_ICON_DEPTH);
      }
    }
    if (!found_system_edgetype) {
      myfree (new_edgetype);
      error ("Can't find system edgetype %s\n", filename);
      return (-1);
    }
	
  } else {
    /* Correction MH 12/6/89	*/
    return insert_edgetype ("#solid", insert_position);
/*	
   error("%s : \nNo user defined edgetypes implemented yet\n", filename);
   return(-1);
*/
  }
	
  for (i = edgetypes.number_of_edgetypes; i > insert_position; i--)
    edgetypes.list[i] = edgetypes.list[i-1];
  edgetypes.list[insert_position] = new_edgetype;

  for (i = edgetypes.number_of_edgetypes+3; i > insert_position+1; i--)
    edgetypes.list_for_cycle[i] = edgetypes.list_for_cycle[i-1];
  edgetypes.list_for_cycle[insert_position+1] =
    (char *)pm_to_svi(edgetypes.list[insert_position]->pm);

  edgetypes.number_of_edgetypes ++;

  install_edgetypelist_for_cycle ();
  if (current_edgetype_index >= insert_position)
    set_current_edgetype (current_edgetype_index + 1);
  for (i=0; i<NUMBER_OF_EDGE_STYLES; i++) {
    Edge_attributes	edge_attr;
    edge_attr = get_edge_style (i);
    if (edge_attr.type_index >= insert_position) {
      edge_attr.type_index++;
      set_edge_style (i, edge_attr);
    }
  }
  
  return(insert_position);
}


int	add_nodetype (char *filename)
{
  return	insert_nodetype (filename, nodetypes.number_of_nodetypes);
}


int	add_edgetype (char *filename)
{
	return	insert_edgetype (filename, edgetypes.number_of_edgetypes);
}



int	delete_nodetype (int delete_position)
{
  Nodetype	old_nodetype;
  int		i;

  if (nodetypes.number_of_nodetypes == 1) {
    error ("Only one type left - Can't delete it\n");
    return (FALSE);
  }
  if (nodetypes.list[delete_position]->used != 0) {
    error ("This nodetype is used - Can't delete it\n");
    return (FALSE);
  }
	
  old_nodetype = nodetypes.list[delete_position];
	
  for (i = delete_position+1; i < nodetypes.number_of_nodetypes; i++)
    nodetypes.list[i-1] = nodetypes.list[i];
	
  for (i = delete_position+2; i < nodetypes.number_of_nodetypes+3; i++)
    nodetypes.list_for_cycle[i-1] = nodetypes.list_for_cycle[i];

  nodetypes.number_of_nodetypes--;

  install_nodetypelist_for_cycle ();
  if (current_nodetype_index >= delete_position)
    set_current_nodetype (maximum (current_nodetype_index-1,0));
  for (i=0; i<NUMBER_OF_NODE_STYLES; i++) {
    Node_attributes	node_attr;
    node_attr = get_node_style (i);
    if (node_attr.type_index == delete_position) {
      node_attr.type_index = current_nodetype_index;
      set_node_style (i, node_attr);
    } else if (node_attr.type_index > delete_position) {
      node_attr.type_index = maximum (node_attr.type_index-1,0);
      set_node_style (i, node_attr);
    }
  }
  XFreePixmap(display,old_nodetype->pm);
  myfree       (old_nodetype->filename);
  myfree       (old_nodetype);

  return (TRUE);
}


int	delete_edgetype (int delete_position)
{
  Edgetype	old_edgetype;
  int		i;

  if (edgetypes.number_of_edgetypes == 1) {
    error ("Only one type left - Can't delete it\n");
    return (FALSE);
  }
  if (edgetypes.list[delete_position]->used != 0) {
    error ("This edgetype is used - Can't delete it\n");
    return (FALSE);
  }

  old_edgetype = edgetypes.list[delete_position];
  
  for (i = delete_position+1; i < edgetypes.number_of_edgetypes; i++) {
    edgetypes.list[i-1] = edgetypes.list[i];
  }
  
  for (i = delete_position+2; i < edgetypes.number_of_edgetypes+3; i++) {
    edgetypes.list_for_cycle[i-1] = edgetypes.list_for_cycle[i];
  }
  
  edgetypes.number_of_edgetypes--;

  install_edgetypelist_for_cycle ();
  if (current_edgetype_index >= delete_position) {
    set_current_edgetype (maximum (current_edgetype_index-1,0));
  }
  for (i=0; i<NUMBER_OF_EDGE_STYLES; i++) {
    Edge_attributes	edge_attr;
    edge_attr = get_edge_style (i);
    if (edge_attr.type_index == delete_position) {
      edge_attr.type_index = current_edgetype_index;
      set_edge_style (i, edge_attr);
    } else if (edge_attr.type_index > delete_position) {
      edge_attr.type_index = maximum (edge_attr.type_index-1,0);
      set_edge_style (i, edge_attr);
    }
  }

  XFreePixmap(display,old_edgetype->pm);
  myfree       (old_edgetype->filename);
  myfree       (old_edgetype);
  
  return (TRUE);
}
/************************************************************************/
/*									*/
/*		AKTUELLER KNOTEN- BZW. KANTENTYP			*/
/*									*/
/************************************************************************/
/*									*/
/*	Nodetype	get_current_nodetype ()				*/
/*	Edgetype	get_current_edgetype ()				*/
/*									*/
/*	Direct access to the current node- resp. edgetype		*/
/*									*/
/************************************************************************/


Edgetype	get_current_edgetype (void)
{
  return edgetypes.list [current_edgetype_index];
}


Nodetype	get_current_nodetype (void)
{
  return nodetypes.list [current_nodetype_index];
}

/************************************************************************/
/*									*/
/*		KNOTEN- BZW. KANTENTYPEN SUCHEN UND VERWENDEN		*/
/*									*/
/************************************************************************/
/*									*/
/*	Nodetype	use_nodetype   (nodetype_index)			*/
/*	Edgetype	use_edgetype   (edgetype_index)			*/
/*	void		unuse_nodetype (nodetype)			*/
/*	void		unuse_edgetype (edgetype)			*/
/*									*/
/*	Melden die Benutzung eines Knoten- bzw. Kantentyps an oder ab.	*/
/*	Der von use_nodetype bzw. use_edgetype zurueckgegebene Typ muss	*/
/*	bei unuse_nodetype bzw. unuse_edgetype als Argument uebergeben	*/
/*	werden.								*/
/*	use_nodetype bzw. use_edgetype zaehlt				*/
/*	...types.list[type_index]->used hoch, unuse_nodetype bzw.	*/
/*	unuse_edgetype zaehlt ...types.list[type_index]->used herunter.	*/
/*	Knoten- und Kantentypen koennen nur geloescht werden, wenn	*/
/*	used == 0.							*/
/*	unuse_nodetype und unuse_edgetype sind gegen NULL als Argument	*/
/*	und used == 0 unempfindlich.					*/
/*									*/
/*	ACHTUNG * WICHTIG * ACHTUNG * WICHTIG * ACHTUNG * WICHTIG	*/
/*									*/
/*	Soll ein Knoten- oder Kantentyp in einem Knoten bzw. einer	*/
/*	Kante verwendet werden, muss er unbedingt mit use_...type an-	*/
/*	und unuse_...type abgemeldet werden, da sonst eine		*/
/*	versehentliche Loeschung und damit Zugriff ueber einen		*/
/*	"dangling pointer" mit all seinen Konsequenzen ("core dumped")	*/
/*	moeglich ist !							*/
/*									*/
/*======================================================================*/
/*									*/
/*	Nodetypeimage	use_nodetypeimage   (type, w,h)			*/
/*	void		unuse_nodetypeimage (type, image)		*/
/*									*/
/*	Auch die Benutzung von Knotenbildern muss angemeldet werden.	*/
/*	w und h sind die Bildgroesse, type ist der Knotentyp, von dem	*/
/*	das Bild gemacht werden soll. Ansonsten gilt sinngemaess das	*/
/*	oben gesagte.							*/
/*									*/
/*======================================================================*/
/*									*/
/*	int	get_nodetype_index (nodetype)				*/
/*	int	get_edgetype_index (edgetype)				*/
/*									*/
/*	Gibt den Index des angegebenen Knoten- bzw. Kantentyps wieder.	*/
/*	DER INDEX KANN NACH INSERT_NODETYPE BZW. DELETE_NODETYPE	*/
/*	UNGUELTIG WERDEN.						*/
/*	Wenn nicht gefunden, Rueckgabe -1.				*/
/*									*/
/*======================================================================*/
/*									*/
/*	int	find_nodetype (filename)				*/
/*	int	find_edgetype (filename)				*/
/*									*/
/*	Sucht Knoten- bzw. Kantentyp mit dem Filenamen filename.	*/
/*	Rueckgabe nodetype_index bzw. edgetype_index oder -1, wenn	*/
/*	nicht gefunden.							*/
/*									*/
/*======================================================================*/
/*									*/
/*	Nodetype	get_nodetype (index)				*/
/*									*/
/*	Get nodetypes.list[index]. May NOT be used for entering a	*/
/*	nodetype in a node, only for reading the contents of a		*/
/*	nodetype.							*/
/*									*/
/************************************************************************/


Nodetype	use_nodetype (int nodetype_index)
{
  nodetypes.list[nodetype_index]->used ++;
  return nodetypes.list[nodetype_index];
}


void		unuse_nodetype (Nodetype nodetype)
{
  if (nodetype != (Nodetype)NULL) {
    nodetype->used --;
  }
}



Edgetype	use_edgetype (int edgetype_index)
{
  edgetypes.list[edgetype_index]->used ++;
  return edgetypes.list[edgetype_index];
}


void		unuse_edgetype (Edgetype edgetype)
{
  if (edgetype != (Edgetype)NULL) {
    edgetype->used --;
  }
}



Nodetypeimage	use_nodetypeimage (Nodetype type, int w, int h)
{
  register Nodetypeimage image;
  register Nodetypeimage last_image;
	
  image      = type->images;
  last_image = (Nodetypeimage)NULL;
  while (image != (Nodetypeimage)NULL && (image->sx != w || image->sy != h)) {
    last_image = image;
    image      = image->suc;
  }
	
  if (image == (Nodetypeimage)NULL) { /* make a new one */
    image = (Nodetypeimage)mymalloc (sizeof (struct nodetypeimage));
    image->sx = w;
    image->sy = h;
    image->pm = XCreatePixmap(display,xwin,w,h, DEFAULT_ICON_DEPTH);
    if (image->pm == (Pixmap)NULL) {
      fatal_error ("Insufficient memory for nodetypeimage\n");
    }
    /** das pixmap loeschen **/
    XSetFunction(display, pixmap_gc, GXclear);
    XFillRectangle(display, image->pm, pixmap_gc,
		   0,0,
		   w, h);
    XSetFunction(display,pixmap_gc,GXcopy);
    
    image->used = 0;
    if (last_image == (Nodetypeimage)NULL) { /* erster	*/
      image->pre   = (Nodetypeimage)NULL;
      image->suc   = type->images;
      if (image->suc != (Nodetypeimage)NULL)
	image->suc->pre = image;
      type->images = image;
    } else {
      image->pre      = last_image;
      image->suc      = last_image->suc;
      image->pre->suc = image;
      if (image->suc != (Nodetypeimage)NULL)
	image->suc->pre = image;
    }
    paint_nodetype_on_pm (type, image->pm, w,h, 1);
  }
  
  /* Sicherstellung, dass kein Typ geloescht wird,	*/
  /* wenn noch ein Bild von ihm im Umlauf ist		*/
  image->used ++;
  type->used  ++;
	
  return image;
}

void		unuse_nodetypeimage (Nodetype type, Nodetypeimage image)
{
  if (type != (Nodetype)NULL && image != (Nodetypeimage)NULL) {
    type->used  --;
    image->used --;
    if (image->used == 0) { /* remove */
      if (image == type->images) {
	type->images    = image->suc;
	if (image->suc != (Nodetypeimage)NULL)
	  image->suc->pre = (Nodetypeimage)NULL;
      } else {
	image->pre->suc = image->suc;
	if (image->suc != (Nodetypeimage)NULL)
	  image->suc->pre = image->pre;
      }
      XFreePixmap(display,image->pm);
      myfree (image);
    }
  }
}


int		get_nodetype_index (Nodetype nodetype)
{
  int	i;
	
  for (i=0; i < nodetypes.number_of_nodetypes; i++) {
    if (nodetype == nodetypes.list[i]) {
      return i;
    }
  }

  return -1;
}


int		get_edgetype_index (Edgetype edgetype)
{
  int	i;
	
  for (i=0; i < edgetypes.number_of_edgetypes; i++) {
    if (edgetype == edgetypes.list[i]) {
      return i;
    }
  }

  return -1;
}


int	find_nodetype (char *filename)
{
  int	i;
	
  for (i=0; i < nodetypes.number_of_nodetypes; i++) {
    if (!strcmp(nodetypes.list[i]->filename, filename)) {
      return i;
    }
  }

  return -1;
}


int	find_edgetype (char *filename)
{
  int	i;
  
  for (i=0; i < edgetypes.number_of_edgetypes; i++) {
    if (!strcmp(edgetypes.list[i]->filename, filename)) {
      return i;
    }
  }

  return -1;
}


Nodetype	get_nodetype (int index)
{
  if (index > nodetypes.number_of_nodetypes) {
    return (Nodetype)NULL;
  } else {
    return nodetypes.list[index];
  }
}


Edgetype	get_edgetype (int index)
{
  if (index > edgetypes.number_of_edgetypes) {
    return (Edgetype)NULL;
  } else {
    return edgetypes.list[index];
  }
}

/************************************************************************/
/*									*/
/*			LIST_FOR_CYCLE VERWALTEN			*/
/*									*/
/************************************************************************/
/*									*/
/*	static	void	install_nodetypelist_for_cycle ()		*/
/*	static	void	install_edgetypelist_for_cycle ()		*/
/*	void		install_current_nodetype       ()		*/
/*	void		install_current_edgetype       ()		*/
/*									*/
/*	Die install_... - Prozeduren setzen die				*/
/*	...types.list_for_cycle's bzw. current_nodetype_index bzw.	*/
/*	current_edgetype_index ueber weitere install_... - Prozeduren	*/
/*	aus anderen Modulen an den benoetigten Stellen der graphischen	*/
/*	Oberflaeche ein.						*/
/*									*/
/************************************************************************/


char	**get_nodetypelist_for_cycle (void)
{
  return nodetypes.list_for_cycle;
}


char	**get_edgetypelist_for_cycle (void)
{
  return edgetypes.list_for_cycle;
}


void	install_nodetypelist_for_cycle (void)
{
  install_nodetypelist_in_node_subframe          (nodetypes.list_for_cycle);
  install_nodetypelist_in_group_subframe         (nodetypes.list_for_cycle);
  install_nodetypelist_in_nodetype_selection     (nodetypes.list_for_cycle);
  install_nodetypelist_in_node_defaults_subframe (nodetypes.list_for_cycle);
}


void	install_edgetypelist_for_cycle (void)
{	
  install_edgetypelist_in_edge_subframe          (edgetypes.list_for_cycle);
  install_edgetypelist_in_group_subframe         (edgetypes.list_for_cycle);
  install_edgetypelist_in_edgetype_selection     (edgetypes.list_for_cycle);
  install_edgetypelist_in_edge_defaults_subframe (edgetypes.list_for_cycle);
}



void	install_current_nodetype (void)
{
  install_current_nodetype_in_nodetype_selection ();
}


void	install_current_edgetype (void)
{
  install_current_edgetype_in_edgetype_selection ();
}
/************************************************************************/
/*									*/
/*		KNOTEN- UND KANTENTYPEN INITIALISIEREN			*/
/*			KONFIGURATION ABSPEICHERN			*/
/*									*/
/************************************************************************/
/*									*/
/*	void		init_types ()					*/
/*									*/
/*	Initialisiert die Strukturen nodetypes und edgetypes.		*/
/*	Nur beim Hochfahren des Programms zu verwenden !		*/
/*									*/
/*======================================================================*/
/*									*/
/*	void		write_nodetypes (file)				*/
/*	void		write_edgetypes (file)				*/
/*									*/
/*	Schreibt die Liste der Knoten- bzw. Kantentypen auf file.	*/
/*	Die Datei muss offen sein und wird nicht geschlossen.		*/
/*	Das Format ist (natuerlich) voll kompatibel zu den		*/
/*	entsprechenden Teilen der Grammatik in parser.y.		*/
/*									*/
/************************************************************************/


System_nodetype	new_system_nodetype (void)
{
  System_nodetype new;

  new = (System_nodetype) malloc (sizeof(struct system_nodetype));

  new->name = NULL;
  new->adjust_func   = NULL;
  new->pm_paint_func = NULL;
  new->ps_paint_func = NULL;

  return new;
}


int	add_system_nodetype (System_nodetype new)
{
  if (number_of_system_nodetypes < MAX_SYSTEM_NODETYPE)  {
    system_nodetypes[number_of_system_nodetypes] = new;
    number_of_system_nodetypes ++;
  }

  return number_of_system_nodetypes;
}


void	init_types(void)
{
  nodetypes.number_of_nodetypes = 0;
  edgetypes.number_of_edgetypes = 0;
  nodetypes.list_for_cycle [0] = (char *)PANEL_CHOICE_IMAGES;
  nodetypes.list_for_cycle [1] = (char *)0;
  nodetypes.list_for_cycle [2] = (char *)0;
  edgetypes.list_for_cycle [0] = (char *)PANEL_CHOICE_IMAGES;
  edgetypes.list_for_cycle [1] = (char *)0;
  edgetypes.list_for_cycle [2] = (char *)0;
}


int	write_nodetypes (FILE *file)
{
  int	i;

  for (i=0; i < nodetypes.number_of_nodetypes; i++) {
    fprintf (file, "NodeType:\t%d\t", i);
    write_quoted_text (file, nodetypes.list[i]->filename);
    fprintf (file, "\n");
  }
  return TRUE;
}


int	write_edgetypes (FILE *file)
{
  int	i;

  for (i=0; i < edgetypes.number_of_edgetypes; i++) {
    fprintf (file, "EdgeType:\t%d\t", i);
    write_quoted_text (file, edgetypes.list[i]->filename);
    fprintf (file, "\n");
  }
  return TRUE;
}

