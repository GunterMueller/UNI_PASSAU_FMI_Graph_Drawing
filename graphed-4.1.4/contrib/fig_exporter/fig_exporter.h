#define FONTPATH "/usr/lib/fonts/fixedwidthfonts/TTTT.S.XX"
#define MAX_FONT_TYPE 0
#define MAX_FONT_STYLE 1
#define MIN_FONT_SIZE 10
#define MAX_FONT_SIZE 24
#define build_fontname(s, type, style, size) \
  (sprintf(s, "cour.%c.%2d", style ? 'b' : 'r', size))

#define MAXFONTS 30

#define PS_FONT_COURIER 12
#define PS_FONT_COURIER_BOLD 14
#define UNKNOWN_FONT (-2)
#define DEFAULT_FIG_FONT 3 /* italic */
#define DEFAULT_FONT_SIZE 12
#define DEFAULT_FONT_NAME "12pt italic"
#define get_fig_font(type, style) \
  ((fig_exporter_options.use_ps_fonts) ? \
    ((style) ? PS_FONT_COURIER_BOLD : PS_FONT_COURIER) : \
    (style)+1)
#define get_fig_text_height(font_size) \
	((int)((font_size)/1.41))
#define get_fig_text_width(font_size) \
	((int)((font_size)/1.5)-1)
#define get_fig_text_step(font_size) \
	((int)((font_size)/1.2)+4)


#define	FIG_RESOL	80	/* don't change this value! */
#define FIG_COORD	2

#define arrow_style(thickness) (float)(thickness), (float)(thickness*4), \
			       (float)(thickness*8)

#define	TOP 1

#define		DEFAULT		      (-1)
#define		SOLID_LINE		0
#define		DASH_LINE		1
#define		DOTTED_LINE		2
#define		RUBBER_LINE		3
#define		PANEL_LINE		4

#define		BLACK			0
#define		WHITE			7

#define					T_ELLIPSE_BY_RAD	1
#define					T_ELLIPSE_BY_DIA	2
#define					T_CIRCLE_BY_RAD		3
#define					T_CIRCLE_BY_DIA		4

#define					UNFILLED	0
#define					WHITE_FILL	1
#define					BLACK_FILL	21

#define					T_POLYLINE	1
#define					T_BOX		2
#define					T_POLYGON	3
#define					T_ARC_BOX	4
#define					T_EPS_BOX	5

#define					T_LEFT_JUSTIFIED	0
#define					T_CENTER_JUSTIFIED	1
#define					T_RIGHT_JUSTIFIED	2

#define					RIGID_TEXT		1
#define					SPECIAL_TEXT		2
#define					PSFONT_TEXT		4
#define					HIDDEN_TEXT		8

#define		O_ELLIPSE		1
#define		O_POLYLINE		2
#define		O_SPLINE		3
#define		O_TEXT			4
#define		O_ARC			5
#define		O_COMPOUND		6
#define		O_END_COMPOUND		-O_COMPOUND
#define		O_ALL_OBJECT		99

#define         DEF_DASHLENGTH          4
#define         DEF_DOTGAP              3

