/* (C) Universitaet Passau 1986-1994 */
/* GraphEd Source, 1986-1994 by Michael Himsolt	*/
#ifndef	MISC_HEADER
#define	MISC_HEADER


#include <stdio.h>
#include <string.h>

#ifndef MALLOC_HEADER
#define MALLOC_HEADER
#include <malloc.h>
#endif

#include <math.h>
#include <xview/base.h>
#include <xview/rect.h>

#include <sys/param.h>
#include <assert.h>
#include <stdarg.h>

#include "error.h"
#include "config.h"

/** ----------------- von fb hinzugefuegt     Anfang ----------------- **/

#include <X11/Xlib.h>
#include <X11/X.h>
#include <xview/font.h>
#include <X11/Xproto.h>

struct pm_subregion
{
  struct {int x; int y; } size;
  struct {int x; int y; } pos;
};

/** ----------------- von fb hinzugefuegt      Ende ------------------ **/


extern	char	*getenv(const char *); /* Don't know where it is defined	*/

/************************************************************************/
/*									*/
/*			Naming Conventions (excerpt)			*/
/*									*/
/*	- All type Names start with capital letter			*/
/*	- _string(s) are string representations, mostly for enumeration	*/
/*	  types								*/
/*									*/
/************************************************************************/

 
/************************************************************************/
/*			FONTS						*/
/************************************************************************/


typedef	struct	graphed_font
{
	Xv_Font xvFont;
	char	*id;
	char	*name;
	int	used;
}
	*Graphed_font;


/************************************************************************/
/*			NODETYPES					*/
/************************************************************************/


typedef	struct	nodetypeimage
{
	int			sx,sy;
	Pixmap			pm;
	int			used;
	struct	nodetypeimage	*pre, *suc;
}
	*Nodetypeimage;

typedef	struct	nodetype
{
	Nodetypeimage	images;

	Pixmap          pm;  /** neu von fb  **/
	char		*filename;
	int		used;
	int		is_system;
	void		(*adjust_func)();	/* Valid only if	*/

	void		(*pm_paint_func)();	/* is_system = TRUE	*/
	void		(*ps_paint_func)();	/* PS_FELSBERG	*/
	void		(*laser_paint_func)();	/*			*/
}
	*Nodetype;


/************************************************************************/
/*		Edgetypes						*/
/************************************************************************/


typedef	struct	edgetype
{
	Pixmap  	        pm;
	char			*pattern;	/* PS_FELSBERG */
	char			*filename;
	int			used;
}
	*Edgetype;



/************************************************************************/
/*			SCALING						*/
/************************************************************************/


typedef	enum {
	SCALE_16_16,
	SCALE_32_32,
	SCALE_64_64,
	SCALE_96_96,
	SCALE_128_128,
	SCALE_192_192,
	SCALE_256_256,
	SCALE_384_384,
	SCALE_512_512,
	SCALE_IDENTITY,
	SCALE_DOWN_XY,
	SCALE_DOWN_X,
	SCALE_DOWN_Y,
	SCALE_UP_XY,
	SCALE_UP_X,
	SCALE_UP_Y,
	SCALE_SQUARE_X,
	SCALE_SQUARE_Y,
	NUMBER_OF_SCALINGS
}
	Scaling;
	
extern	char	*scaling_strings[];
extern	char	*scaling_strings_for_cycle[];
extern	void	scale (Scaling scaling, int *x, int *y);
extern	Scaling	size_to_scale (int size);

extern	void	constrain_to_grid_in_one_dimension (int width, int *x);

/************************************************************************/
/*			DIVERSA (MISC)					*/
/************************************************************************/

#ifndef iif
#define iif(b,e1,e2) ( (b) ? (e1) : (e2) )
#endif
#ifndef maximum
#define	maximum(x,y) iif( (x) > (y), (x), (y))
#endif
#ifndef minimum
#define	minimum(x,y) iif( (x) < (y), (x), (y))
#endif
#ifndef sgn
#define	sgn(x) iif((x)>0, 1, iif((x)==0, 0, -1))
#endif
#ifndef dist_2
#define	dist_2(x1,y1,x2,y2) (((x1)-(x2))*((x1)-(x2)) + ((y1)-(y2))*((y1)-(y2)))
				/* Square of euclidian distance	*/
#endif

typedef enum { NODE,   EDGE   }  Node_or_edge;
typedef enum { SOURCE, TARGET }  Source_or_target;
typedef enum { LOAD,   STORE  }  Load_or_store;

extern	char	**split_string(char *string);
extern	void	free_lines (char **lines);

extern	int	find_min_distance_between_pointclusters (int *p1_x, int *p1_y, int n1, int *p2_x, int *p2_y, int n2, int *m1, int *m2);
extern	void	write_quoted_text                       (register FILE *file, register char *text);
extern	char	*remove_escape_characters_from_text     (register char *text, register int length);
extern	char	*remove_control_chars_from_string       (char *string);

extern	void	display_files                           (char *name);
extern	int	check_file_is_single                    (char *filename);
extern	int	file_is_readable			(char *filename);
extern	int	file_exists                             (char *filename);
extern	char	*file_exists_somewhere                  (char *filename, char *path);
extern	char	*get_existing_fileselector_startup_filename(void);

extern	char	*mymalloc                               (unsigned int size);
extern	char	*mycalloc                               (unsigned int n, unsigned int size);
extern	void	myfree(); /* kein typ wegen warnings ;-) */

extern	void	constrain_8				(int orgx, int orgy, int *x, int *y);
extern	void	constrain_to_grid			(int width, int *x, int *y);
extern	int	rad_to_deg                              (float rad);
extern	float	deg_to_rad                              (int deg);
extern	char	*int_to_ascii                           (int i);
extern	char	*float_to_ascii                         (float f);

extern	int	ticks		(void);
extern	int	directory_exists			(char *filename);


extern	char	*strsave (char *s);	/* Deklariert in sgraph/std.h	*/

extern	int	bell(void);		/* Deklariert in main.c */
extern	int	buffer_is_changed (int buffer); /* aus buffer.c */


/*	Transformation bool --> 0,1     TRUE --> 1,  FALSE --> 0	*/
/*	und zurueck							*/
/*	sowie Liste aus Werten x,y gemaess Ordnung von b1,b2		*/


#define	bool_to_int(b)	iif ((b) == TRUE, 1, 0)
#define int_to_bool(n)	iif ((n) == 1, TRUE, FALSE)

#define	boolean_ordered_list(x,b1, y,b2)             \
	iif (bool_to_int(b1) < bool_to_int(b2), (x), (y)),  \
	iif (bool_to_int(b1) < bool_to_int(b2), (y), (x))

#endif

extern	void	calculate_subwindow_position(int subwindow_width, int subwindow_height, int selection_x, int selection_y, int selection_width, int selection_height, int *x, int *y);
