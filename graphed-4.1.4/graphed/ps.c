/*#define SCREEN_DEBUG*/
/************************************************************************/
/*									*/
/*	(C) Universitaet Passau 1994					*/
/*	GraphEd Source, 1994 by Marc Felsberg				*/
/*									*/
/*	ps.c								*/
/*									*/
/************************************************************************/


#include <stdio.h>
#include <string.h>
#include <time.h>
#include <stdarg.h>

#include <xview/svrimage.h>

#include "sgraph/std.h"
#include "misc.h"
#include "graph.h"

#include "ps.h"
#include "repaint.h"

#include <X11/Xlib.h>
#include <X11/Xutil.h>

#define PS_UNITS_IN_MM 2.835

/************************************************************************/
/*									*/
/*	Lokale Typen und Variablen					*/
/*									*/
/************************************************************************/
/*									*/
/*	static  PsPoint PageSizes[] =					*/
/*									*/
/************************************************************************/


static PsTupel PageSizes[PS_MAX_PAGE_SIZE] = {
  {   0.0	,    0.0 },	/* DUMMY  */
  { 210.0	,  297.0 },	/* CUSTOM */
  { 840.0	, 1188.0 },	/* A0     */
  { 594.0	,  840.0 },	/* A1     */
  { 420.0	,  594.0 },	/* A2     */
  { 297.0	,  420.0 },	/* A3     */
  { 210.0	,  297.0 },	/* A4     */
  { 148.5	,  210.0 },	/* A5     */
  { 105.0	,  148.5 },	/* A6     */
  { 74.25	,  105.0 },	/* A7     */
  { 182.0	,  257.0 },	/* B5     */
  { 217.6	,  281.6 },	/* LETTER */
  { 217.6	,  358.4 }	/* LEGAL  */
};


static char* GlobalStuff[] = {
  "%%%%Global Variables and Procedures\n",
  "/ge_line { /y2 exch def /x2 exch def /y1 exch def /x1 exch def",
  " newpath x1 y2 moveto x2 y2 rlineto } def\n",
  "/ge_box { /dy exch def /dx exch def /y exch def /x exch def",
  " newpath x y moveto dx 0 rlineto 0 dy rlineto dx neg 0 rlineto closepath } def\n",
  "/ge_circle { /r exch def /y exch def /x exch def",
  " newpath x y r 0 360 arc } def\n",
  "/ge_ellipse { /r exch def /y exch def /x exch def",
  " y x atan rotate 1 r r mul x x mul y y mul add sub sqrt r div scale",
  " newpath 0 0 r 0 360 arc } def\n",
  "/ge_inv { 1 -1 scale } def\n",
#ifndef NO_OUTLINE_FONT
  "/myshow { gsave dup false charpath 1 setgray 2 setlinewidth stroke ",
  "grestore show } def\n",
#endif
  ""
};


/************************************************************************/
/*									*/
/*	Lokale Funktionen						*/
/*									*/
/************************************************************************/
/*									*/
/*	static	double	mm2inch(double)					*/
/*	static	double	inch2mm(double)					*/
/*	static	double	mm2ps(double)					*/
/*	static	double	ps2mm(double)					*/
/*	static	double	min(double, double)				*/
/*	static	void	CopyPrimData(PrimData, PrimData)		*/
/*	static	void	PrintColor(PsPage, PsColor)			*/
/*	static	void	PrintChangedPrimData(PsPage)			*/
/*	static	void	ps_set_page_defaults(PsPage)			*/
/*	static	void	ps_set_primdata_defaults(PsPage)		*/
/*	static	void	ps_write_page_footer(PsPage)			*/
/*	static	void	ps_put_prim_on_page(PsPage)			*/
/*									*/
/************************************************************************/

/*
static	double	mm2inch(double x) {return x/2.56;}
static	double	inch2mm(double x) {return x*2.56;}
static	double	mm2ps(double x) {return x*28.125;}
static	double	ps2mm(double x) {return x/28.125;}
*/
static	double	min(double x, double y) {return x < y ? x : y;}


static	void	PrintColor(PsPage *pspage, PsColor *color)
{
  int	prec = pspage->precision;

  if(!pspage) return;

  if(pspage->has_rgb) {
    fprintf(pspage->file, "%.*f %.*f %.*f setrgbcolor ",
	    prec, color->red,
	    prec, color->green,
	    prec, color->blue);
  } else {
    fprintf(pspage->file, "%.*f setgray ",
	    prec, color->gray);
  }
}


static	void	PrintChangedPrimData(PsPage *pspage)
{
  PsBool	weiter;
  int	i, j, prec = pspage->precision;

  if(!pspage) return;

  if(pspage->linewidth != pspage->pd->linewidth)
    fprintf(pspage->file, "%.*f setlinewidth ",
	    prec, pspage->pd->linewidth);
  if(strcmp(pspage->linestyle, pspage->pd->linestyle) != 0) {
    for(i=0, weiter=PS_TRUE; i<pspage->linestyle_entries_used && weiter; i++)
      if(strcmp(pspage->pd->linestyle, pspage->linestylebib[i].name) == 0) {
	fprintf(pspage->file, "ge_lstyle%d ", i);
	weiter=PS_FALSE;
      }
    if(weiter) {
      fprintf(pspage->file, "[ ");
      for(j=0; pspage->pd->linestyle[j]; j++)
	fprintf(pspage->file, "%d ", (int)pspage->pd->linestyle[j]);
      fprintf(pspage->file, "] 0 setdash ");
    }
  }
  if((strcmp(pspage->font, pspage->pd->font) != 0) ||
     (pspage->fontsize != pspage->pd->fontsize))
    fprintf(pspage->file, "/%s findfont %f scalefont setfont ",
	    pspage->pd->font, pspage->pd->fontsize);
  if((pspage->color->gray != pspage->pd->color->gray)  ||
     (pspage->color->red   != pspage->pd->color->red)   ||
     (pspage->color->green != pspage->pd->color->green) ||
     (pspage->color->blue  != pspage->pd->color->blue))
    PrintColor(pspage, pspage->pd->color);
  fprintf(pspage->file, "%.*f %.*f translate ",
	  prec, pspage->pd->pts[0].x + pspage->prim_offset.x,
	  prec, pspage->pd->pts[0].y + pspage->prim_offset.y);
  if(pspage->pd->rotation != 0.0)
    fprintf(pspage->file, "%.*f rotate ",
	    prec, pspage->pd->rotation);
  if((pspage->pd->scaling.x == 1.0) && (pspage->pd->scaling.y == 1.0)) {
  } else if((pspage->pd->scaling.x == -pspage->pd->scaling.y) &&
	    ((pspage->pd->scaling.x == 1.0) || (pspage->pd->scaling.y == 1.0))) {
    fprintf(pspage->file, "ge_inv ");
  } else {
    fprintf(pspage->file, "%f %f scale ",
	    pspage->pd->scaling.x,
	    pspage->pd->scaling.y);
  }
}


static	void	ps_set_page_defaults(PsPage *pspage)
{
  if(!pspage) return;

  strcpy(pspage->creator		, "GraphEd");
  strcpy(pspage->title		, pspage->filename);
  /*	strcpy(pspage->creation_date	, ctime((time_t*)time(0));*/
  strcpy(pspage->fileopenmode	, "w+");

  pspage->page_size_type		= PS_A4;
  pspage->page_size.x		= PageSizes[pspage->page_size_type].x;
  pspage->page_size.y		= PageSizes[pspage->page_size_type].y;
  pspage->page_orientation	= PS_PORTRAIT;
  pspage->page_scaling.x		= 1.0;
  pspage->page_scaling.y		= 1.0;

  pspage->precision		= 2;
  pspage->has_rgb			= FALSE;

  pspage->linewidth		= 0.0;
  strcpy(pspage->linestyle	, "");
  pspage->filled			= FALSE;
  strcpy(pspage->font		, "Courier");
  pspage->fontsize		= 16.0;
  pspage->color->gray		= 0.0;
  pspage->color->red		= 0.0;
  pspage->color->green		= 0.0;
  pspage->color->blue		= 0.0;

  pspage->frame_origin.x		= 25.0;
  pspage->frame_origin.y		= 25.0;
  pspage->frame_size.x		= PageSizes[pspage->page_size_type].x - 50.0;
  pspage->frame_size.y		= PageSizes[pspage->page_size_type].y - 50.0;
  pspage->frame_linewidth		= 0.0;
  pspage->frame_color->gray	= pspage->color->gray;
  pspage->frame_color->red	= pspage->color->red;
  pspage->frame_color->green	= pspage->color->green;
  pspage->frame_color->blue	= pspage->color->blue;
  pspage->frame_visible		= FALSE;

  pspage->prim_offset.x		= 0.0;
  pspage->prim_offset.y		= 0.0;
	
  pspage->linestyle_entries_used	= 0;
  pspage->image_entries_used	= 0;
}


static	void	ps_set_primdata_defaults(PsPage *pspage)
{
  if(!pspage) return;

  pspage->pd->rotation		= 0.0;
  pspage->pd->scaling.x		= 1.0;
  pspage->pd->scaling.y		= 1.0;
  pspage->pd->linewidth		= pspage->linewidth;
  strcpy(pspage->pd->linestyle	, pspage->linestyle);
  pspage->pd->filled		= pspage->filled;
  strcpy(pspage->pd->font		, pspage->font);
  pspage->pd->fontsize		= pspage->fontsize;
  pspage->pd->color->gray		= pspage->color->gray;
  pspage->pd->color->red		= pspage->color->red;
  pspage->pd->color->green	= pspage->color->green;
  pspage->pd->color->blue		= pspage->color->blue;

  switch(pspage->pd->primtype) {
  case PS_TEXT:
    strcpy(pspage->pd->text		, "");
    break;
  case PS_IMAGE:
    strcpy(pspage->pd->image.name	, "");
    pspage->pd->image.size_x	= 0;
    pspage->pd->image.size_y	= 0;
    pspage->pd->image.depth		= 1;
    break;
  case PS_INSTRUCTION:
    strcpy(pspage->pd->instruction	, "");
    break;
  default:
    break;
  }
}


void	ps_write_page_header(PsPage *pspage)
{
  PsBool	weiter;
  int	i, j, prec = pspage->precision;

  if(!pspage) return;

  fprintf(pspage->file, "%%!PS-Adobe-2.0 EPSF-2.0\n");
  fprintf(pspage->file, "%%%%BoundingBox: %d %d %d %d\n",
	  0,			/* (int)(pspage->frame_origin.x * PS_UNITS_IN_MM), */
	  0,			/* (int)(pspage->frame_origin.y * PS_UNITS_IN_MM), */
	  (int)(pspage->frame_size.x * PS_UNITS_IN_MM),
	  (int)(pspage->frame_size.y * PS_UNITS_IN_MM));
  fprintf(pspage->file, "%%%%Title: %s\n", pspage->title);
  fprintf(pspage->file, "%%%%Creator: %s\n", pspage->creator);
  fprintf(pspage->file, "%%%%Pages: %d\n", 1);
  /*	fprintf(pspage->file, "%%%%CreationDate: %s\n", pspage->creation_date); ToDp */
  fprintf(pspage->file, "%%%%EndComments\n");

  /* Globale Variablen und Prozeduren im PS-Programm */

  for(i=0; GlobalStuff[i][0]; i++)
    fprintf(pspage->file, GlobalStuff[i]);

  for(i=0; i<pspage->linestyle_entries_used; i++) {
    fprintf(pspage->file, "/ge_lstyle%d { [ ", i);
    for(j=0; pspage->linestylebib[i].pattern[j]; j++)
      fprintf(pspage->file, "%d ", (int)pspage->linestylebib[i].pattern[j]);
    fprintf(pspage->file, "] 0 setdash } def\n");
  }

  for(i=0; i<pspage->image_entries_used; i++) {
    char	*string;
    fprintf(pspage->file,
	    "/ge_pic%d { %d %d %d [%d 0 0 %d 0 0] {<\n",
	    i,
	    pspage->imagebib[i].size_x,
	    pspage->imagebib[i].size_y,
	    pspage->imagebib[i].depth,
	    pspage->imagebib[i].size_x,
	    pspage->imagebib[i].size_y);
    string = pspage->imagebib[i].pattern;
    fprintf (pspage->file, "%.64s\n", string);
    while ((int)strlen(string) > 64) {
      string += 64;
      fprintf (pspage->file, "%.64s\n", string);
    }
    fprintf(pspage->file, ">} image } def\n");
  }

  /* Setup Frame and Paint Window */

  fprintf(pspage->file, "%%%%Page: 1\n");
  fprintf(pspage->file, "%f %f scale %.*f %.*f translate",
	  PS_UNITS_IN_MM, PS_UNITS_IN_MM, 
	  prec, pspage->frame_origin.x,
	  prec, pspage->frame_origin.y);
    
  if(!pspage->frame_visible) {
    pspage->frame_color->gray = 1.0;
    pspage->frame_color->red = 1.0;
    pspage->frame_color->green = 1.0;
    pspage->frame_color->blue = 1.0;
  }
  fprintf(pspage->file, "\ngsave %.*f setlinewidth ",
	  prec, pspage->frame_linewidth);
  PrintColor(pspage, pspage->frame_color);
  fprintf(pspage->file, "-0.5 -0.5 %.*f %.*f ge_box stroke grestore ",
	  prec, pspage->frame_size.x + 1.0,
	  prec, pspage->frame_size.y + 1.0);
  fprintf(pspage->file, "\n-0.5 -0.5 %.*f %.*f ge_box clip\n",
	  prec, pspage->frame_size.x + 1.0,
	  prec, pspage->frame_size.y + 1.0);

  /* Setup Graphics State */

  fprintf(pspage->file, "%%%%GraphicState\n");
  PrintColor(pspage, pspage->color);
  fprintf(pspage->file, "0 setlinecap %.*f setlinewidth 0 setlinejoin 10 setmiterlimit [] 0 setdash ",
	  prec, pspage->linewidth);
  if(pspage->page_orientation == PS_PORTRAIT)
    fprintf(pspage->file, "0 %.*f translate %f %f scale ",
	    prec, pspage->frame_size.y,
	    pspage->page_scaling.x,
	    -pspage->page_scaling.y);
  else
    fprintf(pspage->file, "-90 rotate %f %f scale ",
	    -pspage->page_scaling.x,
	    pspage->page_scaling.y);
  if(strcmp(pspage->linestyle, "") != 0) {
    for(i=0, weiter=PS_TRUE; i<pspage->linestyle_entries_used && weiter; i++)
      if(strcmp(pspage->linestyle, pspage->linestylebib[i].name) == 0) {
	fprintf(pspage->file, "ge_lstyle%d ", i);
	weiter=PS_FALSE;
      };
    if(weiter) {
      fprintf(pspage->file, "[ ");
      for(j=0; pspage->linestyle[j]; j++)
	fprintf(pspage->file, "%d ", (int)pspage->linestyle[j]);
      fprintf(pspage->file, "] 0 setdash ");
    }
  }
  fprintf(pspage->file, "/%s findfont %f scalefont setfont\n",
	  pspage->font, pspage->fontsize);

  fprintf(pspage->file, "%%%%Graph\n");
}


static	void	ps_write_page_footer(PsPage *pspage)
{
  if(!pspage) return;

  fprintf(pspage->file, "showpage\n");
  fprintf(pspage->file, "%%%%EOF\n");
}


static	void	ps_put_prim_on_page(PsPage *pspage)
{
  PsBool	weiter;
  int	i, prec = pspage->precision;

  if(!pspage) return;

  switch(pspage->pd->primtype) {
  case PS_ELLIPSE:
    pspage->pd->pts[0].x = (pspage->pd->pts[0].x + pspage->pd->pts[1].x) / 2;
    pspage->pd->pts[0].y = (pspage->pd->pts[0].y + pspage->pd->pts[1].y) / 2;
    pspage->pd->pts[1].x = pspage->pd->pts[0].x - pspage->pd->pts[1].x;
    pspage->pd->pts[1].y = pspage->pd->pts[0].y - pspage->pd->pts[1].y;
    break;
  default:
    break;
  }

  fprintf(pspage->file, "gsave ");

  PrintChangedPrimData(pspage);

  switch(pspage->pd->primtype) {
  case PS_DOT:
    fprintf(pspage->file, "0 0 1 ge_circle fill ");
    break;
  case PS_LINE:
    fprintf(pspage->file, "0 0 %.*f %.*f ge_line %s ",
	    prec, pspage->pd->pts[1].x - pspage->pd->pts[0].x,
	    prec, pspage->pd->pts[1].y - pspage->pd->pts[0].y,
	    pspage->pd->filled ? "fill" : "stroke");
    break;
  case PS_POLYLINE:
    fprintf(pspage->file, "newpath 0 0 moveto ");
    for(i=1; i<pspage->pd->nr_of_pts; i++)
      fprintf(pspage->file, "%.*f %.*f rlineto ",
	      prec, pspage->pd->pts[i].x - pspage->pd->pts[i-1].x,
	      prec, pspage->pd->pts[i].y - pspage->pd->pts[i-1].y);
    fprintf(pspage->file, "%s ", pspage->pd->filled ? "fill" : "stroke");
    break;
  case PS_BOX:
    fprintf(pspage->file, "0 0 %.*f %.*f ge_box %s ",
	    prec, pspage->pd->size.x,
	    prec, pspage->pd->size.y,
	    pspage->pd->filled ? "fill" : "stroke");
    break;
  case PS_POLYGON:
    fprintf(pspage->file, "newpath 0 0 moveto ");
    for(i=1; i<pspage->pd->nr_of_pts; i++)
      fprintf(pspage->file, "%.*f %.*f rlineto ",
	      prec, pspage->pd->pts[i].x - pspage->pd->pts[i-1].x,
	      prec, pspage->pd->pts[i].y - pspage->pd->pts[i-1].y);
    fprintf(pspage->file, "0 0 lineto %s ", pspage->pd->filled ? "fill" : "stroke");
    break;
  case PS_ARC:
    fprintf(pspage->file, "newpath 0 0 %.*f %.*f %.*f arc %s ",
	    prec, pspage->pd->radius,
	    prec, pspage->pd->angle1,
	    prec, pspage->pd->angle2,
	    pspage->pd->filled ? "fill" : "stroke");
    break;
  case PS_CIRCLE:
    fprintf(pspage->file, "0 0 %.*f ge_circle %s ",
	    prec, pspage->pd->radius,
	    pspage->pd->filled ? "fill" : "stroke");
    break;
  case PS_ELLIPSE:
    fprintf(pspage->file, "%.*f %.*f %.*f ge_ellipse %s ",
	    prec, pspage->pd->pts[1].x,
	    prec, pspage->pd->pts[1].y,
	    prec, pspage->pd->radius,
	    pspage->pd->filled ? "fill" : "stroke");
    break;
  case PS_TEXT:
    {
      /* Changed by MH Jul 4 1994 to escape certain */
      /* reserved characters                        */

      int i, len;

      fprintf(pspage->file, "0 0 moveto (");
      if (pspage->pd->text != NULL) {
	len = strlen(pspage->pd->text);

	for (i=0; i<len; i++) {
	  switch (pspage->pd->text[i]) {
	  case '(':
	    fputs ("\\(", pspage->file);
	    break;
	  case ')':
	    fputs ("\\)", pspage->file);
	    break;
	  case '\\':
	    fputs ("\\\\", pspage->file);
	    break;
	  default:
	    fputc (pspage->pd->text[i], pspage->file);
	    break;
	  }
	}
      }
#ifdef NO_OUTLINE_FONT
      fprintf(pspage->file, ") show ");
#else
      fprintf(pspage->file, ") myshow ");
#endif
    }
    break;
  case PS_IMAGE:
    for(i=0, weiter = TRUE; (i<pspage->image_entries_used) && weiter; i++) {
      if(strcmp(pspage->imagebib[i].name, pspage->pd->image.name) == 0) {
	fprintf(pspage->file, "ge_pic%d ", i);
	weiter = FALSE;
      }
    }
    if(weiter)
      fprintf(pspage->file, "%d %d %d [%d 0 0 %d 0 0] {<%s>} image ",
	      pspage->pd->image.size_x, pspage->pd->image.size_y,
	      pspage->pd->image.depth,
	      pspage->pd->image.size_x, pspage->pd->image.size_y,
	      pspage->pd->image.pattern);
    break;
  case PS_INSTRUCTION:
    fprintf(pspage->file, "%s ", pspage->pd->instruction);
    break;
  default:
    break;
  }

  fprintf(pspage->file, "grestore\n");
}


/************************************************************************/
/*									*/
/*	Globale Funktionen						*/
/*									*/
/************************************************************************/
/*									*/
/*	PsPage	ps_page_create(char*, ...)				*/
/*	void	ps_page_set(PsPage, ...)				*/
/*	void	ps_write_page_header(PsPage)				*/
/*	void	ps_page_close(PsPage)					*/
/*	void	ps_paint(PsPage, PsPrimType, ...)			*/
/*									*/
/************************************************************************/


PsPage*	ps_page_create(char *filename, PsPageAttr pspageattr, ...)
{
  PsPage*		pspage;
  va_list		ap;
  int		i;
  PsBool		frame_by_margin = PS_FALSE,
  scale_to_fit = PS_FALSE;
  double		frame_margin_left=25, frame_margin_right=25,
  frame_margin_top=25, frame_margin_bottom=25,
  scale_to_fit_left, scale_to_fit_top,
  scale_to_fit_width, scale_to_fit_height;

  pspage				= (PsPage*)malloc(sizeof(PsPage));
  /* pspage->file 			= fopen(filename, pspage->fileopenmode); */
  pspage->color			= (PsColor*)malloc(sizeof(PsColor));
  pspage->frame_color		= (PsColor*)malloc(sizeof(PsColor));
  pspage->pd			= (PsPrimData*)malloc(sizeof(PsPrimData));
  pspage->pd->image.pattern	= (char*)malloc(PS_MAX_IMAGE_SIZE);
  pspage->pd->color		= (PsColor*)malloc(sizeof(PsColor));

  strcpy(pspage->filename, filename);
  ps_set_page_defaults(pspage);

  va_start(ap, pspageattr);

  do switch(pspageattr) {

  case PS_CREATOR:
    strcpy(pspage->creator		, va_arg(ap, char*));
    break;
  case PS_CREATION_DATE:
    strcpy(pspage->creation_date	, va_arg(ap, char*));
    break;
  case PS_TITLE:
    strcpy(pspage->title		, va_arg(ap, char*));
    break;
  case PS_FILE_OPEN_MODE:
    strcpy(pspage->fileopenmode	, va_arg(ap, char*));
    break;

  case PS_PAGE_SIZE_TYPE:
    pspage->page_size_type		= va_arg(ap, PsPageSize);
    pspage->page_size.x		= PageSizes[pspage->page_size_type].x;
    pspage->page_size.y		= PageSizes[pspage->page_size_type].y;
    break;
  case PS_PAGE_SIZE_XY:
    pspage->page_size_type		= PS_CUSTOM;
    pspage->page_size.x		= va_arg(ap, double);
    pspage->page_size.y		= va_arg(ap, double);
    break;
  case PS_PAGE_SIZE_X:
    pspage->page_size.x		= va_arg(ap, double);
    break;
  case PS_PAGE_SIZE_Y:
    pspage->page_size.y		= va_arg(ap, double);
    break;
  case PS_PAGE_ORIENTATION:
    pspage->page_orientation	= va_arg(ap, PsPageOrientation);
    break;
  case PS_SCALE_TO_FIT:
    scale_to_fit			= va_arg(ap, PsBool);
    scale_to_fit_left		= va_arg(ap, double);
    scale_to_fit_top		= va_arg(ap, double);
    scale_to_fit_width		= va_arg(ap, double);
    scale_to_fit_height		= va_arg(ap, double);
    break;
  case PS_PAGE_SCALING_XY:
    pspage->page_scaling.x		= va_arg(ap, double);
    pspage->page_scaling.y		= va_arg(ap, double);
    break;
  case PS_PAGE_SCALING_X:
    pspage->page_scaling.x		= va_arg(ap, double);
    break;
  case PS_PAGE_SCALING_Y:
    pspage->page_scaling.y		= va_arg(ap, double);
    break;

  case PS_FRAME_ORIGIN_XY:
    pspage->frame_origin.x		= va_arg(ap, double);
    pspage->frame_origin.y		= va_arg(ap, double);
    frame_by_margin			= PS_FALSE;
    break;
  case PS_FRAME_ORIGIN_X:
  case PS_FRAME_XOFF:
    pspage->frame_origin.x		= va_arg(ap, double);
    frame_by_margin			= PS_FALSE;
    break;
  case PS_FRAME_ORIGIN_Y:
  case PS_FRAME_YOFF:
    pspage->frame_origin.y		= va_arg(ap, double);
    frame_by_margin			= PS_FALSE;
    break;
  case PS_FRAME_SIZE_XY:
    pspage->frame_size.x		= va_arg(ap, double);
    pspage->frame_size.y		= va_arg(ap, double);
    frame_by_margin			= PS_FALSE;
    break;
  case PS_FRAME_SIZE_X:
  case PS_FRAME_WIDTH:
    pspage->frame_size.x		= va_arg(ap, double);
    frame_by_margin			= PS_FALSE;
    break;
  case PS_FRAME_SIZE_Y:
  case PS_FRAME_HEIGHT:
    pspage->frame_size.y		= va_arg(ap, double);
    frame_by_margin			= PS_FALSE;
    break;
  case PS_FRAME_MARGIN_LEFT:
    frame_margin_left		= va_arg(ap, double);
    frame_by_margin			= PS_TRUE;
    break;
  case PS_FRAME_MARGIN_RIGHT:
    frame_margin_right		= va_arg(ap, double);
    frame_by_margin			= PS_TRUE;
    break;
  case PS_FRAME_MARGIN_TOP:
    frame_margin_top		= va_arg(ap, double);
    frame_by_margin			= PS_TRUE;
    break;
  case PS_FRAME_MARGIN_BOTTOM:
    frame_margin_bottom		= va_arg(ap, double);
    frame_by_margin			= PS_TRUE;
    break;
  case PS_FRAME_LINEWIDTH:
    pspage->frame_linewidth		= va_arg(ap, double);
    break;
  case PS_FRAME_VISIBLE:
    pspage->frame_visible		= va_arg(ap, PsBool);
    break;
  case PS_FRAME_COLOR:
    pspage->frame_color->gray	= va_arg(ap, double);
    break;
  case PS_FRAME_RGBCOLOR:
    pspage->frame_color->red	= va_arg(ap, double);
    pspage->frame_color->green	= va_arg(ap, double);
    pspage->frame_color->blue	= va_arg(ap, double);
    break;

  case PS_DEFAULT_PRECISION:
    pspage->precision		= va_arg(ap, int);
    break;
  case PS_HAS_RGB:
    pspage->has_rgb			= va_arg(ap, PsBool);
    break;

  case PS_DEFAULT_LINEWIDTH:
    pspage->linewidth		= va_arg(ap, double);
    break;
  case PS_DEFAULT_LINESTYLE:
    strcpy(pspage->linestyle	, va_arg(ap, char*));
    break;
  case PS_DEFAULT_FILLED:
    pspage->filled			= va_arg(ap, PsBool);
    break;
  case PS_DEFAULT_FONT:
    strcpy(pspage->font		, va_arg(ap, char*));
    break;
  case PS_DEFAULT_FONTSIZE:
    pspage->fontsize		= va_arg(ap, double); /* ps2mm(va_arg(ap, double)); */
    break;
  case PS_DEFAULT_COLOR:
    pspage->color->gray		= va_arg(ap, double);
    break;
  case PS_DEFAULT_RGBCOLOR:
    pspage->color->red		= va_arg(ap, double);
    pspage->color->green		= va_arg(ap, double);
    pspage->color->blue		= va_arg(ap, double);
    break;

  case PS_LINESTYLE_DEF:
    strcpy(pspage->linestylebib[pspage->linestyle_entries_used].name, va_arg(ap, char*));
    strcpy(pspage->linestylebib[pspage->linestyle_entries_used].pattern, va_arg(ap, char*));
    pspage->linestyle_entries_used++;
    break;
  case PS_IMAGE_DEF:
    {
      PsImage* temp = &(pspage->imagebib[pspage->image_entries_used]);
      strcpy(temp->name	, va_arg(ap, char*));
      temp->size_x		= va_arg(ap, int);
      temp->size_y		= va_arg(ap, int);
      temp->depth		= va_arg(ap, int);
      temp->pattern             = va_arg(ap, char*); /* allready alocated (FH)*/
/*
      temp->pattern		= (char*)malloc((temp->size_x * temp->size_y * temp->depth) / 4 + 2);
      strcpy(temp->pattern	, va_arg(ap, char*));
*/
      pspage->image_entries_used++;
    }
    break;

  default:
    break;

  } while((pspageattr = va_arg(ap, PsPageAttr)) != 0);

  va_end(ap);

  if(frame_by_margin) {
    pspage->frame_origin.x = frame_margin_left;
    pspage->frame_origin.y = frame_margin_bottom;
    pspage->frame_size.x = pspage->page_size.x - frame_margin_left - frame_margin_right;
    pspage->frame_size.y = pspage->page_size.y - frame_margin_top - frame_margin_bottom;
  };
  if(scale_to_fit) {
    if(pspage->page_orientation == PS_PORTRAIT) {
      pspage->page_scaling.x = pspage->page_scaling.y =
	min((pspage->frame_size.x) / scale_to_fit_width,
	    (pspage->frame_size.y) / scale_to_fit_height);
      pspage->prim_offset.x = -scale_to_fit_left;
      pspage->prim_offset.y = -scale_to_fit_top;
    } else {
      pspage->page_scaling.x = pspage->page_scaling.y =
	min((pspage->frame_size.x) / scale_to_fit_height,
	    (pspage->frame_size.y) / scale_to_fit_width);
      pspage->prim_offset.x = -scale_to_fit_left;
      pspage->prim_offset.y = -scale_to_fit_top;
    };
  } else {
    pspage->prim_offset.x = -scale_to_fit_left;
    pspage->prim_offset.y = -scale_to_fit_top;
  };
    
  if((pspage->file = fopen(filename, pspage->fileopenmode)) == NULL) {
    free(pspage->pd->color);
    free(pspage->pd->image.pattern);
    free(pspage->pd);
    for(i=0; i<pspage->image_entries_used; i++)
      free(pspage->imagebib[i].pattern);
    free(pspage->frame_color);
    free(pspage->color);
    free(pspage);
    return (PsPage*)NULL;
  } else {
    return pspage;
  }
}


void	ps_page_set(PsPage *pspage, PsPageAttr	pspageattr, ...)
{
  va_list		ap;
  PsBool		frame_by_margin = PS_FALSE,
  scale_to_fit = PS_FALSE,
  scale_attr_used = PS_FALSE;
  double		frame_margin_left=25, frame_margin_right=25,
  frame_margin_top=25, frame_margin_bottom=25,
  scale_to_fit_left, scale_to_fit_top,
  scale_to_fit_width, scale_to_fit_height;

  va_start(ap, pspageattr);

  do switch(pspageattr) {

  case PS_CREATOR:
    strcpy(pspage->creator		, va_arg(ap, char*));
    break;
  case PS_CREATION_DATE:
    strcpy(pspage->creation_date	, va_arg(ap, char*));
    break;
  case PS_TITLE:
    strcpy(pspage->title		, va_arg(ap, char*));
    break;
  case PS_FILE_OPEN_MODE:
    strcpy(pspage->fileopenmode	, va_arg(ap, char*));
    break;

  case PS_PAGE_SIZE_TYPE:
    pspage->page_size_type		= va_arg(ap, PsPageSize);
    pspage->page_size.x		= PageSizes[pspage->page_size_type].x;
    pspage->page_size.y		= PageSizes[pspage->page_size_type].y;
    break;
  case PS_PAGE_SIZE_XY:
    pspage->page_size_type		= PS_CUSTOM;
    pspage->page_size.x		= va_arg(ap, double);
    pspage->page_size.y		= va_arg(ap, double);
    break;
  case PS_PAGE_SIZE_X:
    pspage->page_size.x		= va_arg(ap, double);
    break;
  case PS_PAGE_SIZE_Y:
    pspage->page_size.y		= va_arg(ap, double);
    break;
  case PS_PAGE_ORIENTATION:
    pspage->page_orientation	= va_arg(ap, PsPageOrientation);
    break;
  case PS_SCALE_TO_FIT:
    scale_attr_used			= PS_TRUE;
    scale_to_fit			= va_arg(ap, PsBool);
    scale_to_fit_left		= va_arg(ap, double);
    scale_to_fit_top		= va_arg(ap, double);
    scale_to_fit_width		= va_arg(ap, double);
    scale_to_fit_height		= va_arg(ap, double);
    break;
  case PS_PAGE_SCALING_XY:
    pspage->page_scaling.x		= va_arg(ap, double);
    pspage->page_scaling.y		= va_arg(ap, double);
    break;
  case PS_PAGE_SCALING_X:
    pspage->page_scaling.x		= va_arg(ap, double);
    break;
  case PS_PAGE_SCALING_Y:
    pspage->page_scaling.y		= va_arg(ap, double);
    break;

  case PS_FRAME_ORIGIN_XY:
    pspage->frame_origin.x		= va_arg(ap, double);
    pspage->frame_origin.y		= va_arg(ap, double);
    frame_by_margin			= PS_FALSE;
    break;
  case PS_FRAME_ORIGIN_X:
  case PS_FRAME_XOFF:
    pspage->frame_origin.x		= va_arg(ap, double);
    frame_by_margin			= PS_FALSE;
    break;
  case PS_FRAME_ORIGIN_Y:
  case PS_FRAME_YOFF:
    pspage->frame_origin.y		= va_arg(ap, double);
    frame_by_margin			= PS_FALSE;
    break;
  case PS_FRAME_SIZE_XY:
    pspage->frame_size.x		= va_arg(ap, double);
    pspage->frame_size.y		= va_arg(ap, double);
    frame_by_margin			= PS_FALSE;
    break;
  case PS_FRAME_SIZE_X:
  case PS_FRAME_WIDTH:
    pspage->frame_size.x		= va_arg(ap, double);
    frame_by_margin			= PS_FALSE;
    break;
  case PS_FRAME_SIZE_Y:
  case PS_FRAME_HEIGHT:
    pspage->frame_size.y		= va_arg(ap, double);
    frame_by_margin			= PS_FALSE;
    break;
  case PS_FRAME_MARGIN_LEFT:
    frame_margin_left		= va_arg(ap, double);
    frame_by_margin			= PS_TRUE;
    break;
  case PS_FRAME_MARGIN_RIGHT:
    frame_margin_right		= va_arg(ap, double);
    frame_by_margin			= PS_TRUE;
    break;
  case PS_FRAME_MARGIN_TOP:
    frame_margin_top		= va_arg(ap, double);
    frame_by_margin			= PS_TRUE;
    break;
  case PS_FRAME_MARGIN_BOTTOM:
    frame_margin_bottom		= va_arg(ap, double);
    frame_by_margin			= PS_TRUE;
    break;
  case PS_FRAME_LINEWIDTH:
    pspage->frame_linewidth		= va_arg(ap, double);
    break;
  case PS_FRAME_VISIBLE:
    pspage->frame_visible		= va_arg(ap, PsBool);
    break;
  case PS_FRAME_COLOR:
    pspage->frame_color->gray	= va_arg(ap, double);
    break;
  case PS_FRAME_RGBCOLOR:
    pspage->frame_color->red	= va_arg(ap, double);
    pspage->frame_color->green	= va_arg(ap, double);
    pspage->frame_color->blue	= va_arg(ap, double);
    break;

  case PS_DEFAULT_PRECISION:
    pspage->precision		= va_arg(ap, int);
    break;
  case PS_HAS_RGB:
    pspage->has_rgb			= va_arg(ap, PsBool);
    break;

  case PS_DEFAULT_LINEWIDTH:
    pspage->linewidth		= va_arg(ap, double);
    break;
  case PS_DEFAULT_LINESTYLE:
    strcpy(pspage->linestyle	, va_arg(ap, char*));
    break;
  case PS_DEFAULT_FILLED:
    pspage->filled			= va_arg(ap, PsBool);
    break;
  case PS_DEFAULT_FONT:
    strcpy(pspage->font		, va_arg(ap, char*));
    break;
  case PS_DEFAULT_FONTSIZE:
    pspage->fontsize		= va_arg(ap, double); /* ps2mm(va_arg(ap, double)); */
    break;
  case PS_DEFAULT_COLOR:
    pspage->color->gray		= va_arg(ap, double);
    break;
  case PS_DEFAULT_RGBCOLOR:
    pspage->color->red		= va_arg(ap, double);
    pspage->color->green		= va_arg(ap, double);
    pspage->color->blue		= va_arg(ap, double);
    break;

  case PS_LINESTYLE_DEF:
    strcpy(pspage->linestylebib[pspage->linestyle_entries_used].name, va_arg(ap, char*));
    strcpy(pspage->linestylebib[pspage->linestyle_entries_used].pattern, va_arg(ap, char*));
    pspage->linestyle_entries_used++;
    break;
  case PS_IMAGE_DEF:
    {
      PsImage* temp = &(pspage->imagebib[pspage->image_entries_used]);
      strcpy(temp->name	, va_arg(ap, char*));
      temp->size_x		= va_arg(ap, int);
      temp->size_y		= va_arg(ap, int);
      temp->depth		= va_arg(ap, int);
      temp->pattern		= va_arg(ap, char*);/* allready allocated (FH)*/
/*
      temp->pattern		= (char*)malloc((temp->size_x * temp->size_y * temp->depth) / 4 + 2);
      strcpy(temp->pattern	, va_arg(ap, char*));
*/
      pspage->image_entries_used++;
    }
    break;

  default:
    break;

  } while((pspageattr = va_arg(ap, PsPageAttr)) != 0);

  va_end(ap);

  if(frame_by_margin) {
    pspage->frame_origin.x = frame_margin_left;
    pspage->frame_origin.y = frame_margin_bottom;
    pspage->frame_size.x = pspage->page_size.x - frame_margin_left - frame_margin_right;
    pspage->frame_size.y = pspage->page_size.y - frame_margin_top - frame_margin_bottom;
  };
  if(scale_attr_used && scale_to_fit) {
    if(pspage->page_orientation == PS_PORTRAIT) {
      pspage->page_scaling.x = pspage->page_scaling.y =
	min((pspage->frame_size.x) / scale_to_fit_width,
	    (pspage->frame_size.y) / scale_to_fit_height);
    } else {
      pspage->page_scaling.x = pspage->page_scaling.y =
	min((pspage->frame_size.x) / scale_to_fit_height,
	    (pspage->frame_size.y) / scale_to_fit_width);
    }
  }
}


void	ps_page_close(PsPage *pspage)
{
  int	i;

  if(!pspage) return;

  ps_write_page_footer(pspage);
		
  fclose(pspage->file);

  free(pspage->pd->color);
  free(pspage->pd->image.pattern);
  free(pspage->pd);
  for(i=0; i<pspage->image_entries_used; i++)
    free(pspage->imagebib[i].pattern);
  free(pspage->frame_color);
  free(pspage->color);
  free(pspage);
}


void	ps_paint(PsPage *pspage, PsPrimType psprimtype, PsPrimAttr psprimattr, ...)
{
  va_list		ap;
  int		i;

  if(!pspage) return;
	
  pspage->pd->primtype = psprimtype;

  ps_set_primdata_defaults(pspage);

  va_start(ap, psprimattr);

  do switch(psprimattr) {

  case PS_PRIM_LOCATION_XY:
    pspage->pd->pts[0].x        = va_arg(ap, double);
    pspage->pd->pts[0].y        = va_arg(ap, double);
    pspage->pd->nr_of_pts       = 1;
    break;
  case PS_PRIM_LOCATION_X:
    pspage->pd->pts[0].x        = va_arg(ap, double);
    pspage->pd->nr_of_pts       = 1;
    break;
  case PS_PRIM_LOCATION_Y:
    pspage->pd->pts[0].y        = va_arg(ap, double);
    pspage->pd->nr_of_pts       = 1;
    break;
  case PS_PRIM_POINTS:
    pspage->pd->nr_of_pts       = va_arg(ap, int);
    for(i=0; i<pspage->pd->nr_of_pts; i++) {
      pspage->pd->pts[i].x = va_arg(ap, double);
      pspage->pd->pts[i].y = va_arg(ap, double);
    };
    break;
  case PS_PRIM_SIZE_XY:
    pspage->pd->size.x          = va_arg(ap, double);
    pspage->pd->size.y          = va_arg(ap, double);
    break;
  case PS_PRIM_SIZE_X:
  case PS_PRIM_WIDTH:
    pspage->pd->size.x          = va_arg(ap, double);
    break;
  case PS_PRIM_SIZE_Y:
  case PS_PRIM_HEIGHT:
    pspage->pd->size.y          = va_arg(ap, double);
    break;
  case PS_PRIM_RADIUS:
    pspage->pd->radius          = va_arg(ap, double);
    break;
  case PS_PRIM_ANGLE1:
    pspage->pd->angle1          = va_arg(ap, double);
    break;
  case PS_PRIM_ANGLE2:
    pspage->pd->angle2          = va_arg(ap, double);
    break;
  case PS_PRIM_TEXT:
    strcpy(pspage->pd->text     , va_arg(ap, char*));
    break;

  case PS_PRIM_ROTATION:
    pspage->pd->rotation        = va_arg(ap, double);
    break;
  case PS_PRIM_SCALING_XY:
    pspage->pd->scaling.x       = va_arg(ap, double);
    pspage->pd->scaling.y       = va_arg(ap, double);
    break;
  case PS_PRIM_SCALING_X:
    pspage->pd->scaling.x       = va_arg(ap, double);
    break;
  case PS_PRIM_SCALING_Y:
    pspage->pd->scaling.y       = va_arg(ap, double);
    break;
  case PS_PRIM_LINEWIDTH:
    pspage->pd->linewidth       = va_arg(ap, double);
    break;
  case PS_PRIM_LINESTYLE:
    strcpy(pspage->pd->linestyle , va_arg(ap, char*));
    break;
  case PS_PRIM_FILLED:
    pspage->pd->filled          = va_arg(ap, PsBool);
    break;
  case PS_PRIM_FONT:
    strcpy(pspage->pd->font     , va_arg(ap, char*));
    break;
  case PS_PRIM_FONTSIZE:
    pspage->pd->fontsize        = va_arg(ap, double); /* ps2mm(va_arg(ap, double)); */
    break;
  case PS_PRIM_COLOR:
    pspage->pd->color->gray     = va_arg(ap, double);
    break;
  case PS_PRIM_RGBCOLOR:
    pspage->pd->color->red      = va_arg(ap, double);
    pspage->pd->color->green    = va_arg(ap, double);
    pspage->pd->color->blue     = va_arg(ap, double);
    break;

  case PS_IMAGE_NAME:
    strcpy(pspage->pd->image.name   , va_arg(ap, char*));
    break;
  case PS_IMAGE_SIZE_XY:
    pspage->pd->image.size_x        = va_arg(ap, int);
    pspage->pd->image.size_y        = va_arg(ap, int);
    break;
  case PS_IMAGE_SIZE_X:
    pspage->pd->image.size_x        = va_arg(ap, int);
    break;
  case PS_IMAGE_SIZE_Y:
    pspage->pd->image.size_y        = va_arg(ap, int);
    break;
  case PS_IMAGE_DEPTH:
    pspage->pd->image.depth         = va_arg(ap, int);
    break;
  case PS_IMAGE_STRING:
    strcpy(pspage->pd->image.pattern, va_arg(ap, char*));
    break;

  default:
    break;

  } while((psprimattr = va_arg(ap, PsPrimAttr)) != 0);

  va_end(ap);

  ps_put_prim_on_page(pspage);
}


/************************************************************************/
/*									*/
/*	Globale Funktionen (Graphed Interface)				*/
/*									*/
/************************************************************************/
/*									*/
/*	void	ps_paint_node(PsPage, Node, Rect*)			*/
/*	void	ps_paint_edge(PsPage, Edge, Rect*)			*/
/*									*/
/*	char*	pat2str(short*)						*/
/*	char*	pixmap2str()						*/
/*									*/
/*	void	ps_paint_box_node(PsPage, Node				*/
/*	void	ps_paint_elliptical_node(PsPage, Node)			*/
/*	void	ps_paint_diamond_node(PsPage, Node)			*/
/*	void	ps_paint_pixmap_node(PsPage, Node)			*/
/*									*/
/************************************************************************/


void	ps_paint_node(PsPage *pspage, Node node, Rect *rect)
{
  int	line;
  double	fontsize;
  char	*fontsize_in_fontname;

  if(!pspage) return;

  if(rect_intersectsrect(&(node->full_box), rect)) {
    node->type->ps_paint_func(pspage, node);
    if(node->label.visible &&
       node->label.text_to_draw != NULL &&
       rect_intersectsrect(&(node->label.box), rect)) {
#ifdef NO_OUTLINE_FONT
      ps_paint(pspage, PS_BOX,	/* white background box for text */
	       PS_PRIM_LOCATION_X,	(double)node->label.box.r_left,
	       PS_PRIM_LOCATION_Y,	(double)node->label.box.r_top,
	       PS_PRIM_WIDTH,		(double)node->label.box.r_width,
	       PS_PRIM_HEIGHT,		(double)node->label.box.r_height,
	       PS_PRIM_COLOR,		1.0,
	       PS_PRIM_FILLED,		TRUE,
	       NULL);
#endif
      fontsize_in_fontname = strrchr(node->label.font->name, '-');
      if (fontsize_in_fontname == NULL) {
	fontsize_in_fontname = strrchr(node->label.font->name, '.');
      }
      if (fontsize_in_fontname != NULL) {
	fontsize = atof (fontsize_in_fontname+1);
	if (fontsize == 0.0) {
	  fontsize = 12.0;
	}
      } else {
	fontsize = 12.0;
      }

      fontsize = (int) xv_get (node->label.font->xvFont, FONT_SIZE);

      for(line=0; node->label.text_to_draw[line] != NULL; line++)
	ps_paint(pspage, PS_TEXT,
		 PS_PRIM_LOCATION_X,	(double)node->label.x,
		 PS_PRIM_LOCATION_Y,	(double)node->label.y + line*node->label.line_height,
		 PS_PRIM_SCALING_Y,	-1.0,
		 PS_PRIM_TEXT,		node->label.text_to_draw[line],
		 /* nni: X-Portierung abwarten
		    PS_PRIM_FONT,		node->label.font->...
		    PS_PRIM_FONTSIZE,	(double)node->label->...
		    PS_PRIM_COLOR,		0.0,
		    */
		 PS_PRIM_FONTSIZE,	fontsize,
		 NULL);
    }

  }
}


void	ps_paint_edge(PsPage *pspage, Edge edge, Rect *rect)
{
  Edgeline	el;
  int		line;
  double	fontsize;
  char		*fontsize_in_fontname;

  if(!pspage) return;

  if (edge->type->pattern != NULL && edge->type->pattern[0] == 0) {
    return;
  }

  if(rect_intersectsrect(&(edge->box), rect)) {
    if((el = edge->line) != (Edgeline)NULL) do {
      if(rect_intersectsrect(&(el->box), rect))
	ps_paint(pspage, PS_POLYLINE,
		 PS_PRIM_POINTS,		2,
		 (double)el->x,		(double)el->y,
		 (double)el->suc->x,	(double)el->suc->y,
		 PS_PRIM_LINESTYLE,	edge->type->filename,
		 /* nni: X-Portierung abwarten
		    PS_PRIM_COLOR,		0.0,
		    */
		 NULL);
      el = el->suc;
    } while(el->suc != edge->line);
    if(edge->source->graph->directed && edge->arrow.length > 0 &&
       rect_intersectsrect(&(edge->arrow.box), rect)) {
      ps_paint(pspage, PS_POLYLINE,
	       PS_PRIM_POINTS,	3,
	       (double)edge->arrow.x0,	(double)edge->arrow.y0,
	       (double)edge->arrow.x1,	(double)edge->arrow.y1,
	       (double)edge->arrow.x2,	(double)edge->arrow.y2,
	       /* nni: X-Portierung abwarten
		  PS_PRIM_COLOR,	0.0,
		  */
	       NULL);
    }
    if(edge->label.visible &&
       edge->label.text_to_draw &&
       rect_intersectsrect(&(edge->label.box), rect)) {
      
      fontsize_in_fontname = strrchr(edge->label.font->name, '-');
      if (fontsize_in_fontname == NULL) {
	fontsize_in_fontname = strrchr(edge->label.font->name, '.');
      }
      if (fontsize_in_fontname != NULL) {
	fontsize = atof (fontsize_in_fontname+1);
	if (fontsize == 0.0) {
	  fontsize = 12.0;
	}
      } else {
	fontsize = 12.0;
      }
      
      fontsize = (int) xv_get (edge->label.font->xvFont, FONT_SIZE);
#ifdef NO_OUTLINE_FONT

      ps_paint(pspage, PS_BOX,	/* white background box for text */
	       PS_PRIM_LOCATION_X,	(double)edge->label.box.r_left,
	       PS_PRIM_LOCATION_Y,	(double)edge->label.box.r_top,
	       PS_PRIM_WIDTH,		(double)edge->label.box.r_width,
	       PS_PRIM_HEIGHT,		(double)edge->label.box.r_height,
	       PS_PRIM_COLOR,		1.0,
	       PS_PRIM_FILLED,		TRUE,
	       NULL);
 #endif
     line = 0;
      ps_paint(pspage, PS_TEXT,
	       PS_PRIM_LOCATION_X,	(double)edge->label.x,
	       PS_PRIM_LOCATION_Y,	(double)edge->label.y - line*edge->label.line_height,
	       PS_PRIM_SCALING_Y,	-1.0,
	       PS_PRIM_TEXT,		edge->label.text_to_draw[line],
	       /* nni: X-Portierung abwarten
		  PS_PRIM_FONT,		edge->label.font->...
		  PS_PRIM_FONTSIZE,	(double)edge->label->...
		  PS_PRIM_COLOR,		0.0,
		  */
	       PS_PRIM_FONTSIZE,	fontsize,
	       NULL);
    }
  }
}


char*	pat2str(char *pat)
{
  int	i=0;
  char*	str = (char*)malloc(PS_MAX_STRING_LENGTH);
	
  if(pat == NULL) {
    return "";
  } else {	
    while((str[i] = pat[i])) i++;
    return str;
  }
}


int	reverse_inverse_trim_bits (unsigned short x)
{
  register int		i;
  register unsigned	short	rev;
  register unsigned	short	bit = 1;

  rev = 0;
  for (i=0; i<sizeof(short)*8; i++) {
    rev = rev | (x & bit);
    bit = bit<<1;
  }
	
  return (~rev) & 0xffff;
}

#ifdef OLD_SVI2STR
char*	svi2str(Server_image svi)
{
#ifndef DIFF_4_0_21_TO_22
#define	PAD_BITS_PIXRECT 16
#endif
#define	PAD_BITS_POSTSCRIPT 8

  int	width  = (int)xv_get(svi, XV_WIDTH);
  int	height = (int)xv_get(svi, XV_HEIGHT);
  int	depth  = (int)xv_get(svi, SERVER_IMAGE_DEPTH);
#ifndef DIFF_4_0_21_TO_22
  int	padded_width = width + width % PAD_BITS_PIXRECT;
  int	padded_height = height + height % PAD_BITS_PIXRECT;

  unsigned short*	image;
  char*  		str;
  int			x,y, characters;

  image = (unsigned short *)xv_get(svi, SERVER_IMAGE_BITS);
  printf ("svi2pat width : %d, height : %d, depth: %d\n", width, height, depth);
  if(image == NULL) {
    printf ("no IMAGE_BITS\n");
    return strsave("0");
  } else {
    printf ("%d\n", image[0]);
    str = (char*)malloc ((padded_width*padded_height*depth*2)/8+2);
    
    characters = 0;
    for (y=0; y<height; y++) {
      for (x=0; x<padded_width; x += PAD_BITS_PIXRECT) {
	sprintf(str+characters, "%04x",
		(int)(reverse_inverse_trim_bits (image[(y*padded_width + x)/PAD_BITS_PIXRECT])));
	characters += 4;
      }
    }
    str[characters] = '\0';
    return str;
  }
#else
  int	padded_width = width + width % PAD_BITS_POSTSCRIPT;
  int	padded_height = height + height % PAD_BITS_POSTSCRIPT;

  Pixmap	image_pm;
  XImage*	image;
  char*  	str;
  int		x,y, characters;

  return strsave("0");

#if FALSE
  image_pm = (Pixmap)xv_get(svi, SERVER_IMAGE_X_BITS);

  if ((image_pm = xv_get (svi, SERVER_IMAGE_PIXMAP)) == NULL) {
    return strsave("0");
  } else {
    str = (char*)malloc ((padded_width*padded_height*depth*2)/1+2);
    image = XGetImage(,
		      image_pm,
		      0,0,	
		      width, height,
		      depth,
		      XYPixmap);

    characters = 0;
    for (y=0; y<height; y++) {
      for (x=0; x<width; x ++) {
	sprintf(str+characters, "%01x",
		(int)XGetPixel(image, x, y));
	characters += 1;
      }
    }
    str[characters] = '\0';
    return str;
  };
#endif
#endif
}
#else

/* Translate pixmap direct to Postscript pattern (FH)*/
/* use first bit-plane only (b/w)                    */

char * pm2str (Pixmap pm, int width, int height)
{
    XImage * image;
    char * pattern;
    char hex[3];
    int bytewidth, i, j;
    int x, y;
    unsigned char byte;

    image = XGetImage(display, pm, 0, 0, width, height, 1, XYPixmap);
    bytewidth = (width + 7) / 8;
    pattern = (char *) malloc (bytewidth*height*3); /* 3 Zeichen pro Byte */
    for (i=0, y=0; y<height; y++)
    {
	for (x=0; x<bytewidth; x++)
	{
	    byte=0;
	    j = (width-(x<<3))-1;
	    if (j>7) j=7;
            for (;j>=0;j--)
            {
		byte = byte>>1; /* Bit 1 pos. nach rechts schieben */
		if (0==XGetPixel(image, (x<<3)+j, y))
		{
		    byte+=128; /* Bit links einfuegen */
		}
            }
	    sprintf (hex, "%02X", byte);
	    pattern[i]=hex[0];
            pattern[i+1]=hex[1];
	    pattern[i+2]=32; /* Leerzeichen */
	    i+=3;
	}
    }
    pattern[i-1]=0;
    XDestroyImage(image);
    return pattern;
}
#endif

/************************************************************************/
/*									*/
/*	Ende								*/
/*									*/
/************************************************************************/
