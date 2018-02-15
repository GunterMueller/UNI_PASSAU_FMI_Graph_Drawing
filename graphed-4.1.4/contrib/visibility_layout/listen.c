/**************************************************************************/
/***                                                                    ***/
/*** Filename: LISTEN.C                                                 ***/
/***                                                                    ***/
/**************************************************************************/

#include "std.h"
#include "slist.h"
#include "sgraph.h"
#include "graphed.h"


Slist		reverse_node_list  (Slist list)
{
Slist		hilfe,new_list = NULL;
Snode		n;
if(!list)return(list);
for_slist(list,hilfe) {
	n = attr_data_of_type(hilfe,Snode);
	if(new_list != empty_slist)
		{
		new_list = add_immediately_to_slist(new_list,make_attr(ATTR_DATA,n));
		new_list = new_list->pre;
		}
	else new_list = add_immediately_to_slist(empty_slist,make_attr(ATTR_DATA,n));
} end_for_slist(list,hilfe)
return(new_list);
}

Slist		queue_node_in_list   (Slist list, Snode n)
{
/*message("%s queued\n",n->label);*/
return(add_immediately_to_slist(list,make_attr(ATTR_DATA,n)));
}

Slist		queue_edge_in_list   (Slist list, Sedge e)
{
return(add_immediately_to_slist(list,make_attr(ATTR_DATA,e)));
}

Slist		delete_edge_in_list   (Slist list, Sedge e)
{
Slist		new_list;
new_list = subtract_from_slist(list,make_attr(ATTR_DATA,e));
return(new_list);
}

Slist		queue_list_in_list   (Slist list, Slist l)
{
return(add_immediately_to_slist(list,make_attr(ATTR_DATA,l)));
}

Slist		push_edge_in_list   (Slist list, Sedge e)
{
/*message("edge from %d to %d pushed\n",e->snode->nr,e->tnode->nr);*/
if(list != empty_slist)
	{
	list = add_immediately_to_slist(list,make_attr(ATTR_DATA,e));
	list = list->pre;
	}
else list = add_to_slist(empty_slist,make_attr(ATTR_DATA,e));
return(list);
}

Slist		push_node_in_list   (Slist list, Snode n)
{
/*printf("node %d pushed\n",n->nr);*/
if(list != empty_slist)
	{
	list = add_immediately_to_slist(list,make_attr(ATTR_DATA,n));
	list = list->pre;
	}
else list = add_to_slist(empty_slist,make_attr(ATTR_DATA,n));
return(list);
}

Slist		push_once_node_in_list   (Slist list, Snode n)
{
/*printf("node %d pushed\n",n->nr);*/
if(list != empty_slist)
	{
	list = add_to_slist(list,make_attr(ATTR_DATA,n));
	list = list->pre;
	}
else list = add_to_slist(empty_slist,make_attr(ATTR_DATA,n));
return(list);
}

Slist		push_graph_in_list   (Slist list, Sgraph g)
{
/*message("node %d pushed\n",n->nr);*/
if(list != empty_slist)
	{
	list = add_immediately_to_slist(list,make_attr(ATTR_DATA,g));
	list = list->pre;
	}
else list = add_to_slist(empty_slist,make_attr(ATTR_DATA,g));
return(list);
}

Snode		pop_node_from_list    (Slist *List)
{
Snode 		n = NULL;
Slist		list;
list = *List;
if(*List == empty_slist)printf("Fehler bei pop_node_from_list\n");
else {
	n = attr_data_of_type(*List,Snode);
	/**List = subtract_from_slist(*List,make_attr(ATTR_DATA,n));*/
	*List = subtract_immediately_from_slist(list,list);
	/*printf("node %d gequeued\n",n->nr);*/
	}
return(n);
}

Sedge		pop_edge_from_list    (Slist *List)
{
Sedge 		e = NULL;
Slist		list;
list = *List;
if(*List == empty_slist)printf("Fehler bei pop_edge_from_list\n");
else {
	e = attr_data_of_type(*List,Sedge);
	/**List = subtract_from_slist(*List,make_attr(ATTR_DATA,n));*/
	*List = subtract_immediately_from_slist(list,list);
	}
return(e);
}

Slist		create_a_list  (void)
{
return(empty_slist);
}

int		is_empty_list   (Slist list)
{
return(list == empty_slist);
}

void		release_my_slist   (Slist *List)
{
free_slist(*List);
(*List) = NULL;
}

int		length_of_list(Slist list)
{
Slist		hilfe;
int len = 0;
for_slist(list,hilfe) {
	len++;
} end_for_slist(list,hilfe)
return(len);
}


/****************************************************************************/
/***                      END OF FILE: LISTEN.C                           ***/
/****************************************************************************/
