#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include <string.h>
#include "user_header.h"

#include "lp_general_functions.h"

/****************************************************************************************/
/*											*/
/*	File mit ein paar grundlegenden Funktionen, die von mehreren Algorithmen	*/
/*	benoetigt werden.								*/
/*											*/
/****************************************************************************************/

/*****************************************************************************************
function:	timos_reverse
Input:	char* s

	Reverse s

Output:	s
*****************************************************************************************/

char	*timos_reverse	(char *s)
{
	int	c, i, j;

	for ( i = 0, j = strlen(s)-1; i < j; i++, j-- )
	{
		c = s[i];
		s[i] = s[j];
		s[j] = c;
	}
	if ( strlen(s) == 0 ) return( NULL );
	else	return( s );
}

/*****************************************************************************************
function:	dec_length
Input:	int n

Output:	length of n
*****************************************************************************************/

int	dec_length	(int n)
{
	int	result = 0;

	while ( n > 0 )
	{
		result++;
		n = n / 10;
	}
	return( result );
}

/*****************************************************************************************
function:	timos_itoa
Input:	int n

	Convert n to a string s

Output:	s
*****************************************************************************************/

char	*timos_itoa	(int n)
{
	int i, sign;
	int l = dec_length(n);
	char	*s;

	l++;
	if (n<0) l++;
	s = (char *) malloc( l);

	if ( (sign = n) < 0 ) n = -n;
	i = 0;
	do 
	{
		s[i++] = n % 10 + '0';
	}
	while ( (n /= 10) > 0 );
	if ( sign < 0 ) s[i++] = '-';
	s[i] = '\0';
	return( timos_reverse(s) );
}

/*****************************************************************************************
function:	mem_copy_string
Input:	char* string

	Erzeugt eine Kopie von string

Output:	Kopie

*****************************************************************************************/

char*	mem_copy_string(char *string)
{
	int	string_length;
	char*	result;

	if( !string )
	{
		return( NULL );
	}

	string_length = strlen( string );

	result = (char*)mymalloc( string_length+1 );

	strcpy( result, string );

	return( result );
}

/*********************************************************************************
function 	MsgBox
Input:	char* Msg, int	what_buttons

	Gibt eine Nachricht in einem extra Fenster aus

output:	---
*********************************************************************************/

void	MsgBox(char *Msg, int what_buttons)
{
	if( what_buttons == CMD_OK )
	{
		notice_prompt (base_frame, NULL,		/*fisprompt*/
					NOTICE_MESSAGE_STRINGS,	Msg, NULL,
					NOTICE_BUTTON_YES,	"Ok",
					NULL);
	}
}


/*********************************************************************************
function	compute_graph

	Find the selected Graph 

Output:	Pointer to selected graph
*********************************************************************************/

Graph	compute_graph(void)
{
	Graph			graph = empty_graph;
			
	if (something_picked)
	{ 
		switch (picked_object->what) 
		{
		    	case NODE_PICKED :
				graph = picked_object->which.node->graph;
				break;
		    	case EDGE_PICKED :
				graph = picked_object->which.edge->source->graph;
				break;
		    	case GROUP_PICKED :
		    		if (group_nodes_are_all_of_same_graph (picked_object->which.group)) 
				{
					graph = picked_object->which.group->node->graph;
				} 
				else 
				{
					MsgBox( "Please give a graph.", CMD_OK );
					graph = empty_graph;
				}
				break;
		}
		if (graph != empty_graph)
		{
			if ( graph->is_production )
			{
				MsgBox( "This function needs a graph.", CMD_OK );
				return( NULL );
			}

			return( graph );
		}	
		return( NULL );
	}
	else
	{
		MsgBox( "Nothing selected.", CMD_OK );
		return( NULL );
	}
}

/*****************************************************************
function:	restore_graph
Input:	Graph graph

	Restore nodes and edges of graph
*****************************************************************/

void	restore_graph(Graph graph)
{
	Group	group_of_graph;

	group_set (group_of_graph = make_group_of_graph (graph), RESTORE_IT, 0);
	free_group (group_of_graph);
		
}

