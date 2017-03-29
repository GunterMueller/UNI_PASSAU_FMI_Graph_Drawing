/* (C) Universitaet Passau 1986-1994 */

/* test.c  Beispielprogramm zum Test von connect1 */




#include <std.h>
#include <slist.h>
#include <sgraph.h> 
#include <graphed.h> 
#include <algorithms.h>

extern bool test_sgraph_strongly_biconnected(Sgraph g);

void	call_check_connectivity (Sgraph_proc_info sgraph_info)
{ 
	if  (test_sgraph_connected(sgraph_info->sgraph))
		message("The graph is connected.\n");
	else
		message("The graph is not connected.\n");
} 

void	call_menu_check_connectivity (Menu menu, Menu menu_item)
{ 
	call_sgraph_proc (call_check_connectivity, NULL); 
} 



 
 
void	call_check_strong_connectivity (Sgraph_proc_info sgraph_info)
{ 
	if  (test_sgraph_strongly_connected(sgraph_info->sgraph))
		message("The graph is strongly connected.\n");
	else
		message("The graph is not strongly connected.\n");
} 

void	call_menu_check_strong_connectivity (Menu menu, Menu_item menu_item)
{ 
	call_sgraph_proc (call_check_strong_connectivity, NULL); 
} 
 


void	call_check_biconnectivity (Sgraph_proc_info sgraph_info)
{ 
	if  (test_sgraph_biconnected(sgraph_info->sgraph))
		message("The graph is biconnected.\n");
	else
		message("The graph is not biconnected.\n");
} 

void	call_menu_check_biconnectivity (Menu menu, Menu_item menu_item)
{ 
	call_sgraph_proc (call_check_biconnectivity, NULL); 
} 
 
 

void	call_check_strong_biconnectivity (Sgraph_proc_info sgraph_info)
{ 
	if  (test_sgraph_strongly_biconnected(sgraph_info->sgraph))
		message("The graph is strongly biconnected.\n");
	else
		message("The graph is not strongly biconnected.\n");
} 

void	call_menu_check_strong_biconnectivity (Menu menu, Menu_item menu_item)
{ 
	call_sgraph_proc (call_check_strong_biconnectivity, NULL); 
} 
