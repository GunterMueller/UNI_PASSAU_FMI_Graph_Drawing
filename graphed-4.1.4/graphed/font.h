/* (C) Universitaet Passau 1986-1994 */
/* GraphEd Source, 1986-1994 by Michael Himsolt	*/
#ifndef	FONT_HEADER
#define	FONT_HEADER

#include "misc.h"

extern	int		insert_font (char *name, char *font_id, int insert_position);
extern	void		install_current_nodefont (void);
extern	void		install_current_edgefont (void);

extern	int		add_font    (char *name, char *font_id);
extern	int		delete_font (int delete_position);

extern	Graphed_font	get_current_nodefont (void);
extern	Graphed_font	get_current_edgefont (void);

extern	Graphed_font	use_font       (int font_index);
extern	void		unuse_font     (Graphed_font font);
extern	int		get_font_index (Graphed_font font);
extern	int		find_font      (char *name, char *font_id);

extern	void		init_fonts  (void);
extern	void		write_fonts (FILE *file);

extern	char		**get_fontlist_for_cycle (void);

#endif
