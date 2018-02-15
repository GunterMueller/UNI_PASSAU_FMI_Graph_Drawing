/* (C) Universitaet Passau 1986-1994 */
typedef struct komp_list {
	char			*komponent;
	struct komp_list	*next;
} *Komp_list;


extern void		dispose_komplist(Komp_list k);
extern Komp_list	filename_komponents(char *name);
extern Komp_list	rexp_komponents(char *name);
extern char		*get_name_of_lists(Komp_list nkl, Komp_list ekl);


