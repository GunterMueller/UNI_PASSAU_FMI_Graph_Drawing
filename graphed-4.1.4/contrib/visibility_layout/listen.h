/**************************************************/
/*                                                */
/*                  LISTEN.H                      */
/*                                                */
/*  (Typdeklarationen fuer die Listen )           */
/*                                                */
/**************************************************/
 
extern 	Slist		queue_node_in_list(Slist list, Snode n);
extern 	Slist		queue_edge_in_list(Slist list, Sedge e);
extern 	Slist		delete_edge_in_list(Slist list, Sedge e);
extern 	Slist		queue_list_in_list(Slist list, Slist l);
extern 	Slist		push_edge_in_list(Slist list, Sedge e);
extern 	Slist		push_node_in_list(Slist list, Snode n);
extern 	Slist		push_once_node_in_list(Slist list, Snode n);
extern 	Slist		push_graph_in_list(Slist list, Sgraph g);
extern	Snode		*pop_node_from_list(Slist *List);
extern	Sedge		*pop_edge_from_list(Slist *List);
extern	Slist		create_a_list  (void);
extern	Slist		reverse_node_list (Slist list);
extern	int		is_empty_list   (Slist list);
extern	void		release_my_slist   (Slist *List);
extern	int		length_of_list(Slist list);

#define for_all_edges(list, e) \
	{ Slist Forhilf; \
	for_slist(list,Forhilf) { \
	e = attrs_data_of_type(Forhilf,Sedge);
#define end_for_all_elements(list,Forhilf) \
	} end_for_slist(list,Forhilf)  }

#define reverse_for_all_elements(list, element) \
        { Slist Forhilf; \
        Forhilf = list; \
        if ( list != empty_slist) do { element = Forhilf->attrs ;
#define reverse_end_for_all_elements(list) \
        } while ((Forhilf = Forhilf->pre) != list ) ; }


#define	LIST			Slist
#define INIT_LIST(li)   	(li) = create_a_list()
#define QUEUE_NODE(li,n)	(li) = queue_node_in_list( (li),(Snode)(n) )
#define QUEUE_EDGE(li,e)	(li) = queue_edge_in_list( (li),(Sedge)(e) )
#define QUEUE_LIST(li,l)	(li) = queue_list_in_list( (li),(Slist)(l) )
#define IS_EMPTY_LIST(li)	is_empty_list( li )
#define POP_NODE(li,n)		(n)  = (Snode)pop_node_from_list( &(li) )
#define POP_EDGE(li,e)		(e)  = (Sedge)pop_edge_from_list( &(li) )
#define PUSH_EDGE(li,e	)	(li) = push_edge_in_list( (li), (Sedge)(e) )
#define PUSH_NODE(li,n)		(li) = push_node_in_list( (li), (Snode)(n) )
#define PUSH_ONCE_NODE(li,n)	(li) = push_once_node_in_list( (li), (Snode)(n) )
#define PUSH_GRAPH(li,g)	(li) = push_graph_in_list( (li), (Sgraph)(g) )
#define CLEAR_LIST(li)  	release_my_slist( &(li) )
#define REVERSE_NODE_LIST(li)  (LIST)reverse_node_list( (li) )
#define LIST_LENGTH(li)  	length_of_list(li)
#define DELETE_EDGE(li,e)	(li) = delete_edge_in_list( (li),(Sedge)(e) )
