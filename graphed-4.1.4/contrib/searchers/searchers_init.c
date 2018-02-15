/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	init_user_menu

arguments   :	void	

return()    :	void

description :	initializing GraphEd`s menu with name and
		starting function

use	    :	used by GraphEd on a menu-event

restrictions:	only called by GraphEd itself

bugs	    :	none

*******************************************************************************/

{
/******************************************************************/
/* add all algorithms to the algorithm list, no ordering required */
/******************************************************************/
        add_to_search_algorithms( "tree", TreeInitAndTest, TreeSearch, TreeFree,
		OPTIMUM, TRUE, TRUE, TRUE, TRUE );
        add_to_search_algorithms( "d-dimensional grid", GridInitAndTest, GridSearch, NULL,
                OPTIMUM, TRUE, TRUE, TRUE, TRUE );
        add_to_search_algorithms( "Kn,m", KnmInitAndTest, KnmSearch, NULL,
                OPTIMUM, TRUE, TRUE, TRUE, TRUE );
        add_to_search_algorithms( "complete", CompleteInitAndTest, CompleteSearch, NULL,
                OPTIMUM, TRUE, TRUE, TRUE , TRUE);
	add_to_search_algorithms( "moving separator", NULL, MovingSepSearch, NULL,
		HEURISTIC, TRUE, TRUE, TRUE, TRUE );
	add_to_search_algorithms( "min component separator", NULL, MinComponentLogSepSearch, NULL,
		HEURISTIC, TRUE, TRUE, TRUE, TRUE );
        add_to_search_algorithms( "mininum degree", NULL, SmallestDegreeSearch, NULL,
                HEURISTIC, TRUE, TRUE, TRUE, TRUE );
        add_to_search_algorithms( "brute force", NULL, BruteForceSearch, NULL,
                HEURISTIC, TRUE, TRUE, TRUE, TRUE );
        add_to_search_algorithms( "lower bound (clique,mindegree)", NULL,
		CliqueSearch, NULL, APPROXIMATION, FALSE, TRUE, TRUE, TRUE );
        add_to_search_algorithms( "lower bound (spanning tree)", NULL,
		SubgraphSearch, NULL, APPROXIMATION, FALSE, TRUE, TRUE, TRUE );
        add_to_search_algorithms( "lower bound (minor)", NULL,
		MinorSearch, NULL, APPROXIMATION, FALSE, TRUE, TRUE, TRUE );
        add_to_search_algorithms( "lower bound (size graph)", NULL,
		NodesAndEdgesSearch, NULL, APPROXIMATION, FALSE, TRUE, TRUE, TRUE );
        add_to_search_algorithms( "tree (upper bound)", App2TreeInitAndTest,
		App2TreeSearch, NULL, APPROXIMATION, FALSE, TRUE, TRUE, TRUE );
        add_to_search_algorithms( "d grid (upper bound)", AppGridInitAndTest,
		AppGridSearch, NULL, APPROXIMATION, FALSE, TRUE, TRUE, TRUE );
        add_to_search_algorithms( "tree", AppTreeInitAndTest, AppTreeSearch,
		AppTreeFree, APPROXIMATION, TRUE, TRUE, TRUE, TRUE );

/************************************************************/
/* add a single menu to the user button, see GraphEd manual */
/************************************************************/
	add_to_goodies_menu ( "Searching on graphs ...", WNsearchWindow );

}
