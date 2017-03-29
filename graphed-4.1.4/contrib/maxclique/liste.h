/* (C) Universitaet Passau 1986-1994 */
/************************************************************************
 * File    : liste.h							*
 * Aufgabe : Verwaltung der eigenen Zustandsmenge			*
 *									*
 * Autor   : Torsten Bachmann						*
 * Datum   : 18.11.89							*
 ************************************************************************/

typedef struct l_node_list
{       int	curlen;
	int	maxlen;
	Snode	*nodes;
}
	*L_node_list;


#define L_list_entry(list,nr) ((list->nodes)[nr])

#define L_for_node_list(list, nr)\
			if (list->curlen>0)\
			{       nr=-1; while(++nr < list->curlen) {
#define L_end_for_node_list(list, nr) }}


L_node_list	L_create_list (int size);
void		L_append (L_node_list list, Snode node);
void		L_delete_entry (L_node_list list, int nr);
void		L_move (L_node_list sourcelist, L_node_list destlist, int nr);
void		L_close_list (L_node_list list);
void		L_minimize_mem (L_node_list list);
