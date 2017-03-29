/* (C) Universitaet Passau 1986-1994 */
/* GraphEd Source, 1986-1994 by Michael Himsolt	*/
#ifndef	TYPE_HEADER
#define	TYPE_HEADER

typedef struct system_nodetype {
  char	*name;
  void	(*adjust_func)();
  void	(*pm_paint_func)();
  void	(*ps_paint_func)();		/* PS_FELSBERG */
}
  *System_nodetype;

extern	System_nodetype	new_system_nodetype (void);
extern	int		add_system_nodetype (System_nodetype new);


typedef  struct	{
  char	*name;
  char	*pattern;
}
  System_edgetype;


extern	int		insert_nodetype      (char *filename, int insert_position);
extern	int		insert_edgetype      (char *filename, int insert_position);
extern	int		delete_nodetype      (int delete_position);
extern	int		delete_edgetype      (int delete_position);

extern	Nodetype	get_current_nodetype (void);
extern	Edgetype	get_current_edgetype (void);

extern	Nodetype	use_nodetype        (int nodetype_index);
extern	Edgetype	use_edgetype        (int edgetype_index);
extern	void		unuse_nodetype      (Nodetype nodetype);
extern	void		unuse_edgetype      (Edgetype edgetype);
extern	Nodetypeimage	use_nodetypeimage   (Nodetype type, int w, int h);
extern	void		unuse_nodetypeimage (Nodetype type, Nodetypeimage image);

extern	int		get_nodetype_index  (Nodetype nodetype);
extern	int		get_edgetype_index  (Edgetype edgetype);
extern	int		find_nodetype       (char *filename);
extern	int		find_edgetype       (char *filename);
extern	Nodetype	get_nodetype	    (int index);
extern	Edgetype	get_edgetype	    (int index);
extern  int	        write_edgetypes     (FILE *file);
extern  void	        install_current_edgetype    (void);
extern  void    	install_current_nodetype    (void);
extern  int     	add_edgetype                (char *filename);
extern  int	        add_nodetype                (char *filename);
 
extern	void		init_types (void);

extern	int		write_nodetypes (FILE *file);


extern	char		**get_nodetypelist_for_cycle (void);
extern	char		**get_edgetypelist_for_cycle (void);

#endif
