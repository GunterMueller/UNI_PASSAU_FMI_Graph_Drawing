#ifdef UNIX5
#ifndef lint
static char version_id[] = "%Z% File : %M% %E%  %U%  Version %I% Copyright (C) 1992/93 Schweikardt Andreas";
#endif
#endif 

/*******************************************************************************
*									       *
*									       *
*			SEARCH STRATEGIES ON GRAPHS			       *
*									       *
*									       *
*	Copyright (C) 1992/93 Andreas Schweikardt			       *
********************************************************************************




        File    :       %M%

        Date    :       %E%     (%U%)

        Version :       %I%

        Author  :       Schweikardt, Andreas



Portability

	Language		:	C
	Operating System	:	Sun-OS (UNIX)
	User Interface (graphic):	
	Other			:	GraphEd & Sgraph


********************************************************************************


Layer   : 	control

Modul   :	programmer interface


********************************************************************************


Description of %M% :	
	a tool for the programmer to add in easy way new algorithms to
	the search module


********************************************************************************


Functions of %M% :

COresetAlgorithms	sets the current algorithm pointer to the beginning of
			algorithmlist
COnextAlgorithm		returns the name of the algorithm, traverses the list
COalgorithmFree		Free procedure of current algorithm
COalgorithmInitAndTest	...
COalgorithmSearch	...
COalgorithmAnimation	animation allowed for this algorithm and method ?
add_to_search_algorithms	add a new algorithm


*******************************************************************************/


/******************************************************************************
*                                                                             *
*                       standard includes                                     *
*                                                                             *
*******************************************************************************/

#include <stdio.h>
#include <string.h>


/******************************************************************************
*                                                                             *
*			GraphEd & Sgraph includes			      *
*                                                                             *
*******************************************************************************/

#include <sgraph/std.h>
#include <sgraph/slist.h>
#include <sgraph/sgraph.h>
#include <sgraph/graphed.h>


/******************************************************************************
*                                                                             *
*			local includes		 			      *
*                                                                             *
*******************************************************************************/

#include <search/search.h>
#include <search/control.h>


/******************************************************************************
*                                                                             *
*			local defines 			     	              *
*                                                                             *
*******************************************************************************/

#define	GET_ALGORITHM_INIT_AND_TEST	0
#define GET_ALGORITHM_SEARCH		1
#define GET_ALGORITHM_FREE		2


/******************************************************************************
*                                                                             *
*			local types		 			      *
*                                                                             *
*******************************************************************************/

/**************************************************************/
/* all the necessary information about an algorithm (search!) */
/**************************************************************/
typedef	struct Algorithms {

	char		*name;		/* name of the algorithm */

	VoidFunction	InitAndTest,	/* special class of graphs ? Initialize
					   the graph structure ? */
			Search,		/* the main search algorithm */
			Free;		/* anything to free ? */

	Strategy	strategy;	/* which class ? */

	bool		animation,	/* animation allowed ? */
			node_search,	/* algorithm also for NS ? */
			edge_search,	/*	...	      ES ? */
			mixed_search;	/*	...	      MS ? */

	struct Algorithms	*next;	/* next algorithm in list */

} Algorithms;


/******************************************************************************
*                                                                             *
*			local variables					      *
*                                                                             *
*******************************************************************************/

/***************************************/
/* a pointer to the list of algorithms */
/***************************************/
static Algorithms	*algorithms = (Algorithms *)NULL;

/***************************************************************/
/* pointer to the current algorithm, while traversing the list */
/***************************************************************/
static Algorithms	*current = (Algorithms *)NULL;



/******************************************************************************
*                                                                             *
*                                                                             *
*                        local functions		                      *
*                                                                             *
*                                                                             *
******************************************************************************/


/*******************************************************************************
*									       *
*			local function					       *
*									       *
********************************************************************************

name        :	COalgorithmProc

arguments   :	
   	type		name		description(-I/-O)
1.	int		nr		number of current algorithm-I
2.	int		which		which part of algorithm-I

return()    :	VoidFunction

description :	gets one of the three procedures of the algorithm

use	    :	

restrictions:	the argument 'which' is one of the above defined '#define'
		with GET_ALGORITHM_...

bugs	    :	none reported

*******************************************************************************/
static VoidFunction	COalgorithmProc(int nr, int which)
{
	Strategy	strategy = COstrategy();
	Method		method = COmethod();
	Algorithms	*ptr;
	int		index = 0;

/************************************************/
/* start at the beginning of the algorithm list */
/************************************************/
	ptr = algorithms;

/*******************************/
/* any algorithm in the list ? */
/*******************************/
	if ( ptr == (Algorithms *)NULL )
		return( (VoidFunction)NULL );

/***********************************************************************/
/* traverse the list, incremnent 'index' if we found an algorithm      */
/* according to current strategy, if 'index' equals the number 'nr' of */ 
/* current algorithm, we found it. Check if the algorithms applies to  */
/* the current method.						       */
/***********************************************************************/
	while ( ptr != (Algorithms *)NULL )	/* not end of list */
	{
	/*****************************************************************/
	/* does this algorithm (desribed in 'ptr') belong to the current */
	/* stratetgy ? 							 */
	/*****************************************************************/
		if ( ptr->strategy == strategy )
		{
		/*****************************************************/
		/* is the algorithm in 'ptr' the current algorithm ? */
		/*****************************************************/
			if ( index == nr )
			{
			/**************************************/
			/* applies it to the current method ? */
			/**************************************/
				if (   ( method == METHOD_NODE_SEARCH && ptr->node_search )
				     ||( method == METHOD_EDGE_SEARCH && ptr->edge_search )
				     ||( method == METHOD_MIXED_SEARCH && ptr->mixed_search ) )
				{
				/******************************/
				/* which of the three parts ? */
				/******************************/
					switch( which )
					{
					case GET_ALGORITHM_FREE:
						return( ptr->Free );
						break;
					case GET_ALGORITHM_INIT_AND_TEST:
						return( ptr->InitAndTest );
						break;
					case GET_ALGORITHM_SEARCH:
						return( ptr->Search );
						break;
					}
				}
				else
				{
					/* no such algorithm */
					return( (VoidFunction)NULL );
				}
			}
			/* it wasn't the current algorithm */
			index++;
		}	
		/* try the next algorithm in list */
		ptr = ptr->next;
	}

	/* such an algorithm does not exist in this list */
	return( (VoidFunction)NULL );
}
/******************************************************************************/
		
	
/******************************************************************************
*                                                                             *
*                                                                             *
*                       global functions		                      *
*                                                                             *
*                                                                             *
******************************************************************************/


/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COresetAlgorithms

arguments   :	

return()    :	void

description :	Points again to the beginning of the algorithm list

use	    :	before traversing the alg list

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	COresetAlgorithms(void)
{

/*****************************/
/* both variables are global */
/*****************************/
	current = algorithms;

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COnextAlgorithm

arguments   :	

return()    :	void

description :	fetchs the name of the next algorithm, but only with the
		current strategy

use	    :	to rebuilt the menu

restrictions:	

sideeffects :	steps to the next item in the algorithm list, changes the
		value of 'current'

bugs	    :	none reported

*******************************************************************************/
char		*COnextAlgorithm(void)
{
	Strategy	strategy = COstrategy();
	Algorithms	*return_algorithm;
	

	if ( current == (Algorithms *)NULL )
	{
	/*****************************************/
	/* no algorithm in the (remainding) list */
	/*****************************************/
		current = algorithms; /* do a reset */
		return( (char *)NULL );
	}

/***********************************************************/
/* search for the next algorithm with the current strategy */
/***********************************************************/
	while ( current->strategy != strategy )
	{
	/*************************************************************/
	/* wasn't the appropriate strategy, so step to the next item */
	/*************************************************************/
		current = current->next;
			
		if ( current == (Algorithms *)NULL )
		{
		/******************************/
		/* nothing found, end of list */
		/******************************/
			current = algorithms; /* and reset */
			return( (char *)NULL );
		}
	}
/*******************************************************/
/* store the found algortihm, current is manipulated ! */
/*******************************************************/
	return_algorithm = current;

/**********************************************/
/* step to the next item, (next call uses it) */
/**********************************************/
	current = current->next; 

	return( return_algorithm->name );
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COalgorithmAnimation

arguments   :	
   	type		name		description(-I/-O)
1.	int		nr		number of algorithm in menu-I

return()    :	bool

description :	does this algorithm support animation ?

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
bool	COalgorithmAnimation(int nr)
{
	Strategy	strategy = COstrategy();
	Method		method = COmethod();
	Algorithms	*ptr;
	int		index = 0;
/****************************************************/
/* set 'ptr' to the beginning of the algorithm list */
/****************************************************/
	ptr = algorithms;
/***************************************/
/* but does it contain any algorithm ? */
/***************************************/
	if ( ptr == (Algorithms *)NULL )
	{
	/****************************************************/
	/* no algorithm exists ==> no animation is possible */
	/****************************************************/
		return( FALSE );
	}

/*******************************************/
/* search the list while list is not empty */
/*******************************************/
	while ( ptr != (Algorithms *)NULL )
	{
	/***************************************************************/
	/* does the algorithm described in 'ptr' belong to the current */
	/* strategy ?						       */ 
	/***************************************************************/
		if ( ptr->strategy == strategy )
		{
		/************************************************************/
		/* found an algorithm with appropriate strategy, but is the */
		/* 'nr'-th algorithm ?					    */
		/************************************************************/
			if ( index == nr )
			{
			/**************************************************/
			/* this was THE algorithm, and does the animation */
			/* for the current method ?			  */
			/**************************************************/
				if (   ( method == METHOD_NODE_SEARCH && ptr->node_search )
				     ||( method == METHOD_EDGE_SEARCH && ptr->edge_search )
				     ||( method == METHOD_MIXED_SEARCH && ptr->mixed_search ) )
					return( ptr->animation );
				else
				{
				/**********************************************/
				/* the algorithm doesn't apply to the current */
				/* method ==> no animation !		      */
				/**********************************************/
					return( FALSE );
				}
			}
		/****************************************/
		/* the algorithm wasn't the 'nr'-th one */
		/****************************************/
			index++;
		}	
	/**************************************/
	/* try the next algorithm in the list */
	/**************************************/
		ptr = ptr->next;
	}

/*********************************************************************/
/* we didn't find the 'nr'-th algorithm of strategy ==> no algorithm */
/* ==> no animation 						     */
/*********************************************************************/
	return( FALSE );
}
/******************************************************************************/
		
	

/*******************************************************************************
*									       *
*			global functions of the same sort		       *
*									       *
********************************************************************************

names	    :	COalgorithmFree
		COalgorithmInitAndTest
		COalgorithmSearch

arguments   :	
   	type		name		description(-I/-O)
1.	int		nr		number of current algorithm-I

return()    :	VoidFunction

description :	see local description

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/

/*******************************************************************************

name        :	COalgortihmFree 

description :	gets the free procedure of the algorithm

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
VoidFunction	COalgorithmFree(int nr)
{
	return( COalgorithmProc( nr, GET_ALGORITHM_FREE ) );
}
	
/*******************************************************************************

name        :	COalgorithmSearch 

description :	gets the main procedure of the algorithm

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
VoidFunction	COalgorithmSearch(int nr)
{
	return( COalgorithmProc( nr, GET_ALGORITHM_SEARCH ) );
}
		
/*******************************************************************************

name        :	COalgorithmInitAndTest 

description :	gets the initial and graph test procedure of the algorithm

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
VoidFunction	COalgorithmInitAndTest(int nr)
{
	return( COalgorithmProc( nr, GET_ALGORITHM_INIT_AND_TEST ) );
}
	


/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	add_to_search_algorithms

arguments   :	
   	type		name		description(-I/-O)
1.	char*		naem		name of the game :-I  
2.	VoidFunction	proc_init_and_test	initial procedure-I
3.	VoidFunction	proc_search		main procedure-I
4.	VoidFunction	proc_free		free procedure-I
5.	Strategy	strategy		sort of algorithm-I
6.	bool		animation		animation possible ?-I
7.	bool		es			algorithm for edge search ?-I
8.	bool		ns			algorithm for node search ?-I
9.	bool		ms			algorithm for mixed search ?-I

return()    :	bool

description :	adds a new algorithm to the list

use	    :	

restrictions:	call before the search window is created !

bugs	    :	we have to report any error messages to stderr, because
		the GraphEd window hasn't opened yet

*******************************************************************************/
bool	add_to_search_algorithms(char *name, VoidFunction proc_init_and_test, VoidFunction proc_search, VoidFunction proc_free, Strategy strategy, int animation, int es, int ns, int ms)
{
	Algorithms	*new,
			*last;

/*************************/
/* check for a real name */
/*************************/
	if ( name == (char *)NULL )
	{
		fprintf( stderr, "\ncannot add nameless search algorithm\n" );
		return( FALSE );
	}
/******************************/
/* check for a non-empty name */
/******************************/
	else if ( *name == (char)0 )
	{
		fprintf( stderr, "\ncannot add nameless search algorithm\n" );
		return( FALSE );
	}
/******************************************************/
/* for one method at least the algorithm have to work */
/******************************************************/
	else if ( es == FALSE && ns == FALSE && ms == FALSE )
	{
	/******************************************************/
	/* algorithm seems to work for none of the methods,   */
	/* so what will that algorithm do ?		      */
	/* Well nothing, so we do not insert it into the list */
	/* (otherwise we will blame the other algorithms :-)  */
	/******************************************************/
		fprintf( stderr, "\nsearch algorithm %s don't work for any method\n", name );
		return( FALSE );
	}
/************************************/
/* does any search function exist ? */
/************************************/
	else if ( proc_search == (VoidFunction)NULL )
	{
	/******************************/
	/* do not add a null function */
	/******************************/
		fprintf( stderr, "\nsearch algorithm %s: no function specified\n", name );	
		return( FALSE );
	}

/**************************************************/
/* initiaize a new list element for the algorithm */
/**************************************************/
	new = (Algorithms *)malloc( sizeof( Algorithms ) );

	new->name = strsave( name );
	new->InitAndTest = proc_init_and_test;
	new->Search = proc_search;
	new->Free = proc_free;
	new->strategy = strategy;
	new->animation = animation;
	new->edge_search = es;
	new->node_search = ns;
	new->mixed_search = ms;
	new->next = (Algorithms *)NULL;

/************************************/
/* is the first entry in the list ? */
/************************************/
	if ( algorithms == (Algorithms *)NULL )
	{
		/* yes, it is :-) */
		algorithms = new;
	}
	else
	{
		last = algorithms;
	/*************************************************/
	/* append the 'new' entry at the end of the list */
	/*************************************************/
		while ( last->next != (Algorithms *)NULL )
		{
		/*************************************************************/
		/* is the name of the algorithm already used (same strategy) */
		/*************************************************************/
			if ( !strcmp( name, last->name )
			     && strategy == last->strategy )
			{
				fprintf( stderr, "choose different names for your search algorithms %s\n", name );
			}
			last = last->next;
		}

	/*********************/
	/* append at the end */
	/*********************/
		last->next = new;
	}

/**********************************/
/* done, algorithm has been added */
/**********************************/
	return( TRUE );

}
/******************************************************************************/



/******************************************************************************
*		       [EOF] end of file %M% 
******************************************************************************/
