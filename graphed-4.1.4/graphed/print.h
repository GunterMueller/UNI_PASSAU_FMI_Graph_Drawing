/* (C) Universitaet Passau 1986-1994 */
/* GraphEd Source, 1986-1994 by Michael Himsolt	*/
#ifndef PRINT_HEADER
#define PRINT_HEADER

typedef enum {
	OUTPUT_PS,
	OUTPUT_XBITMAP_FILE
}
	Print_device;

typedef	enum {
	AREA_VISIBLE,
	AREA_FULL
}
	Print_area;

typedef	enum {
	PRINT_COLOR,
	PRINT_NOCOLOR
}
	Print_color;


typedef	struct	print_settings	{

	Print_device	device;
	Print_area	area;
	Print_color	color;

	struct	{
		double		top, left;
		double		width, height;
	}
		frame;

	struct	{
		int	fit;
		double	scaling;
		int	frame_visible;
		int	orientation;
		double	margin_left,
			margin_right,
			margin_top,
			margin_bottom;
	}
		ps;
}
	Print_settings;


Print_settings	print_settings;

extern	Print_settings	init_print_settings (void);
extern	void		save_print_settings (void);
extern	void		show_print_subframe (void);

extern	Rect	compute_print_rect (int buffer, Print_area area);
extern	void	print_buffer (char *filename, int buffer, Print_settings settings);
extern	void	write_postscript_file (char *filename, int buffer, Rect print_rect, Print_settings settings);
extern	void	write_xbitmap_file (char *filename, int buffer, Rect rect, Print_settings settings);

#endif
