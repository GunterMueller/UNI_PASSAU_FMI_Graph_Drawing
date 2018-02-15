/************************************************************************/
/*									*/
/*	(C) Universitaet Passau 1994					*/
/*	GraphEd Source, 1994 by Marc Felsberg				*/
/*									*/
/*	ps.h								*/
/*									*/
/************************************************************************/


#ifndef PS_HEADER
#define PS_HEADER


#define PS_MAX_NR_OF_POINTS         100
#define PS_MAX_INSTRUCTION_LENGTH  1000
#define PS_MAX_STRING_LENGTH        100
#define PS_MAX_NR_OF_LINESTYLES     100
#define PS_MAX_NR_OF_IMAGES         100
#define PS_MAX_IMAGE_SIZE         65536


typedef int ps_dummy;

/************************************************************************/
/*									*/
/*	Aufzaehlungstypen	*/
/*									*/
/************************************************************************/
/*									*/
/*	typedef	enum	PsBool						*/
/*	typedef	enum	PsPageAttr					*/
/*	typedef	enum	PsPageSize					*/
/*	typedef	enum	PsPageOrientation				*/
/*	typedef	enum	PsPrimType					*/
/*	typedef	enum	PsPrimAtt					*/
/*									*/
/************************************************************************/
/*									*/
/*	Achtung:							*/
/*	es ist nicht sinnvoll PS_FRAME_MARGIN_* nach PS_PAGE_SIZE_* zu	*/
/*	setzen, da PS_FRAME_MARGIN_* Bezug auf die urspruenglichen	*/
/*	Werte nimmt. Der Margin wird immer vom Papierrand aus gemessen	*/
/*									*/
/************************************************************************/


typedef	enum {
	PS_FALSE,
	PS_TRUE
} PsBool;


typedef	enum {
	PS_NULL_PAGE_SIZE,	/*				*/

	PS_CUSTOM,		/*				*/
	PS_A0, PS_A1,		/*				*/
	PS_A2, PS_A3,		/*				*/
	PS_A4, PS_A5,		/*				*/
	PS_A6, PS_A7,		/*				*/
	PS_B5,			/*				*/
	PS_LETTER, PS_LEGAL,	/*				*/

	PS_MAX_PAGE_SIZE	/*				*/
} PsPageSize;


typedef	enum {
	PS_NULL_PAGE_ORIENTATION,/*				*/

	PS_PORTRAIT,		/*				*/
	PS_LANDSCAPE,		/*				*/

	PS_MAX_PAGE_ORIENTATION	/*				*/
} PsPageOrientation;


typedef	enum {
	PS_NULL_PRIM_TYPE,	/*				*/

	PS_DOT,			/*				*/
	PS_LINE,		/*				*/
	PS_POLYLINE,		/*				*/
	PS_BOX,			/*				*/
	PS_POLYGON,		/*				*/
	PS_ARC,			/*				*/
	PS_CIRCLE,		/*				*/
	PS_ELLIPSE,		/*				*/
	PS_TEXT,		/*				*/
	PS_IMAGE,		/*				*/
	PS_INSTRUCTION,		/*				*/

	PS_MAX_PRIM_TYPE	/*				*/
} PsPrimType;


typedef	enum {
	PS_NULL_PRIM_ATTR,	/*				*/

	PS_PRIM_LOCATION_XY,	/* double, double		*/
	PS_PRIM_LOCATION_X,	/* double			*/
	PS_PRIM_LOCATION_Y,	/* double			*/
	PS_PRIM_ROTATION,	/* double			*/
	PS_PRIM_SCALING_XY,	/* double, double		*/
	PS_PRIM_SCALING_X,	/* double			*/
	PS_PRIM_SCALING_Y,	/* double			*/
	PS_PRIM_POINTS,		/* int, double, double, ...	*/ /* int ist # der Punktpaare */
	PS_PRIM_SIZE_XY,	/* double, double		*/
	PS_PRIM_SIZE_X,		/* double			*/
	PS_PRIM_SIZE_Y,		/* double			*/
	PS_PRIM_WIDTH,		/* siehe PS_PRIM_SIZE_X		*/
	PS_PRIM_HEIGHT,		/* siehe PS_PRIM_SIZE_Y		*/
	PS_PRIM_RADIUS,		/* double			*/
	PS_PRIM_ANGLE1,		/* double			*/
	PS_PRIM_ANGLE2,		/* double			*/
	PS_PRIM_TEXT,		/* char*			*/

	PS_PRIM_LINEWIDTH,	/* double			*/
	PS_PRIM_LINESTYLE,	/* char*			*/
	PS_PRIM_FILLED,		/* PsBool			*/
	PS_PRIM_FONT,		/* char*			*/
	PS_PRIM_FONTSIZE,	/* double			*/
	PS_PRIM_COLOR,		/* double			*/
	PS_PRIM_RGBCOLOR,	/* double, double, double	*/

	PS_IMAGE_NAME,		/* char*			*/
	PS_IMAGE_SIZE_XY,	/* int, int			*/
	PS_IMAGE_SIZE_X,	/* int				*/
	PS_IMAGE_SIZE_Y,	/* int				*/
	PS_IMAGE_DEPTH,		/* int				*/
	PS_IMAGE_STRING,	/* char*			*/

	PS_MAX_PRIM_ATTR	/*				*/
} PsPrimAttr;


typedef	enum {
	PS_NULL_PAGE_ATTR,	/*				*/

	PS_CREATOR,		/* char*			*/
	PS_CREATION_DATE,	/* char*			*/
	PS_TITLE,		/* char*			*/
	PS_FILE_OPEN_MODE,	/* char*			*/

	PS_PAGE_SIZE_TYPE,	/* PsPageSize			*/
	PS_PAGE_SIZE_XY,	/* double, double		*/
	PS_PAGE_SIZE_X,		/* double			*/
	PS_PAGE_SIZE_Y,		/* double			*/
	PS_PAGE_ORIENTATION,	/* PsPageOrientation		*/
	PS_SCALE_TO_FIT,	/* PsBool, double, double	*/
	PS_PAGE_SCALING_XY,	/* double, double		*/
	PS_PAGE_SCALING_X,	/* double			*/
	PS_PAGE_SCALING_Y,	/* double			*/

	PS_FRAME_ORIGIN_XY,	/* double, double		*/
	PS_FRAME_ORIGIN_X,	/* double			*/
	PS_FRAME_ORIGIN_Y,	/* double			*/
	PS_FRAME_XOFF,		/* siehe PS_FRAME_ORIGIN_X	*/
	PS_FRAME_YOFF,		/* siehe PS_FRAME_ORIGIN_Y	*/
	PS_FRAME_SIZE_XY,	/* double, double		*/
	PS_FRAME_SIZE_X,	/* double			*/
	PS_FRAME_SIZE_Y,	/* double			*/
	PS_FRAME_WIDTH,		/* siehe PS_FRAME_SIZE_X	*/
	PS_FRAME_HEIGHT,	/* siehe PS_FRAME_SIZE_Y	*/
	PS_FRAME_MARGIN_LEFT,	/* double			*/
	PS_FRAME_MARGIN_RIGHT,	/* double			*/
	PS_FRAME_MARGIN_TOP,	/* double			*/
	PS_FRAME_MARGIN_BOTTOM,	/* double			*/
	PS_FRAME_LINEWIDTH,	/* double			*/
	PS_FRAME_VISIBLE,	/* PsBool			*/
	PS_FRAME_COLOR,		/* double			*/
	PS_FRAME_RGBCOLOR,	/* double, double, double	*/

	PS_DEFAULT_PRECISION,	/* int				*/
	PS_HAS_RGB,		/* PsBool			*/

	PS_DEFAULT_LINEWIDTH,	/* double			*/
	PS_DEFAULT_LINESTYLE,	/* char*			*/
	PS_DEFAULT_FILLED,	/* PsBool			*/
	PS_DEFAULT_FONT,	/* char*			*/
	PS_DEFAULT_FONTSIZE,	/* double			*/
	PS_DEFAULT_COLOR,	/* double			*/
	PS_DEFAULT_RGBCOLOR,	/* double, double, double	*/

	PS_LINESTYLE_DEF,	/* char*, char*			*/
	PS_IMAGE_DEF,		/* char*, int, int, int, char*	*/

	PS_MAX_PAGE_ATTR	/*				*/
} PsPageAttr;


/************************************************************************/
/*									*/
/*	Strukturen							*/
/*									*/
/************************************************************************/
/*									*/
/*	typedef	struct	PsPoint						*/
/*	typedef	struct	PsRectSize					*/
/*	typedef	struct	PsColor						*/
/*	typedef	struct	PsPrimData					*/
/*	typedef	struct	PsPage						*/
/*									*/
/************************************************************************/


typedef	struct {
	double	x, y;
} PsTupel;


typedef	struct {
	double	gray;
	double	red, green, blue;
} PsColor;


typedef	struct {
	char	name[PS_MAX_STRING_LENGTH];
	char	pattern[PS_MAX_STRING_LENGTH];
} PsLineStyle;


typedef	struct {
	char	name[PS_MAX_STRING_LENGTH];
	int	size_x, size_y;
	int	depth;
	char*	pattern;
} PsImage;


typedef	struct {
	PsPrimType	primtype;
	int		nr_of_pts;
	PsTupel		pts[PS_MAX_NR_OF_POINTS];
	PsTupel		size;
	double		radius;
	double		angle1, angle2;
	char		text[PS_MAX_STRING_LENGTH];
	PsImage		image;
	char		instruction[PS_MAX_INSTRUCTION_LENGTH];

	double		rotation;
	PsTupel		scaling;
	double		linewidth;
	char		linestyle[PS_MAX_STRING_LENGTH];
	PsBool		filled;
	char		font[PS_MAX_STRING_LENGTH];
	double		fontsize;
	PsColor*	color;
} PsPrimData;


typedef	struct {

	/* General Initialisations */

	char		creator[PS_MAX_STRING_LENGTH];
	char		title[PS_MAX_STRING_LENGTH];
	char		creation_date[PS_MAX_STRING_LENGTH];
	char		filename[PS_MAX_STRING_LENGTH];
	char		fileopenmode[PS_MAX_STRING_LENGTH];
	FILE*		file;

	PsPageSize	page_size_type;
	PsTupel		page_size;
	PsPageOrientation	page_orientation;
	PsTupel		page_scaling;

	PsTupel		frame_origin;
	PsTupel		frame_size;
	double		frame_linewidth;
	PsBool		frame_visible;
	PsColor*	frame_color;
	
	PsTupel		prim_offset;
	int		precision;	/* Default Precision */
	PsBool		has_rgb;

	/* PrimData Defaults */

	double		linewidth;
	char		linestyle[PS_MAX_STRING_LENGTH];
	PsBool		filled;
	char		font[PS_MAX_STRING_LENGTH];
	double		fontsize;
	PsColor*	color;

	/* LineStyles in PS-Format */

	int		linestyle_entries_used;
	PsLineStyle	linestylebib[PS_MAX_NR_OF_LINESTYLES];

	/* Bitmaps in PS-Format */

	int		image_entries_used;
	PsImage		imagebib[PS_MAX_NR_OF_IMAGES];

	/* PrimData Structure */

	PsPrimData*	pd;
} PsPage;


/************************************************************************/
/*									*/
/*	Funktionen							*/
/*									*/
/************************************************************************/
/*									*/
/*	extern	PsPage*	ps_page_create(char*, ...)			*/
/*	extern	void	ps_page_set(PsPage, ...)			*/
/*	extern	void	ps_write_page_header(PsPage, ...)		*/
/*	extern	void	ps_page_close(PsPage)				*/
/*	extern	void	ps_paint(PsPage, PsPrimType, ...)		*/
/*									*/
/*	extern	void	ps_paint_node(PsPage, Node, Rect*)		*/
/*	extern	void	ps_paint_edge(PsPage, Edge, Rect*)		*/
/*									*/
/*	extern	char*	pat2str(short*)					*/
/*	extern	char*	svi2str()					*/
/*									*/
/*	extern	void	ps_paint_box_node(PsPage, Node)			*/
/*	extern	void	ps_paint_elliptical_node(PsPage, Node)		*/
/*	extern	void	ps_paint_diamond_node(PsPage, Node)		*/
/*	extern	void	ps_paint_pixmap_node(PsPage, Node)		*/
/*									*/
/************************************************************************/


extern	PsPage*	ps_page_create(char *filename, PsPageAttr pspageattr, ...);

extern	void	ps_page_set(PsPage *pspage, PsPageAttr	pspageattr, ...);

extern	void	ps_write_page_header(PsPage *pspage);

extern	void	ps_page_close(PsPage *pspage);

extern	void	ps_paint(PsPage *pspage, PsPrimType psprimtype, PsPrimAttr psprimattr, ...);


extern	void	ps_paint_node(PsPage *pspage, Node node, Rect *rect);

extern	void	ps_paint_edge(PsPage *pspage, Edge edge, Rect *rect);


extern	char*	pat2str(char *pat);

#ifdef OLD_SVI2STR
extern	char*	svi2str(Server_image svi);
#else
extern	char*	pm2str(Pixmap pm, int width, int height);
#endif

extern	void	ps_paint_box_node(PsPage *pspage, Node node);

extern	void	ps_paint_elliptical_node(PsPage *pspage, Node node);

extern	void	ps_paint_diamond_node(PsPage *pspage, Node node);

extern	void	ps_paint_pixmap_node(PsPage *pspage, Node node);


/************************************************************************/
/*									*/
/*	Ende								*/
/*									*/
/************************************************************************/


#endif
