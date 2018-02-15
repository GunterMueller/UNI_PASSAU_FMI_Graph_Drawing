/* (C) Universitaet Passau 1986-1994 */
/******************************************************************************/
/*                                                                            */
/*                      U N D I R E C T E D  .  C                             */
/*                                                                            */
/******************************************************************************/


#include "sgraph/std.h"
#include "sgraph/sgraph.h"
#include "sgraph/slist.h"
#include "sgraph/sgraph_interface.h"

/* erzeuge in diesem Modul eine gerichtete Kopie eines ungerichteten Graphen, */
/* damit der Graph dem "Sugiyama-Algorithmus" uebergeben werden kann;	      */
/* am Ende "Sugiyama-Algorithmus" muss natuerlich wieder der gerichtete Graph */
/* vorhanden sein							      */


/********************** mache gerichtete Kopie eines Graphen ******************/
Global Sgraph make_directed_copy_of_sgraph (Sgraph g)
{
	Sgraph new_graph = empty_sgraph;
        Snode node, new_node, target, new_source, new_target, real_source;
	Sedge edge, new_edge;
	Attributes save_attrs;

	/* initialisiere zunaechst einen leeren Graphen			      */
	save_attrs = g->attrs;
	new_graph  = make_graph( save_attrs );
	new_graph->directed = TRUE;

	/* kopiere zuerst die Knoten					      */
	for_all_nodes( g, node )
	{
		save_attrs = node->attrs;
		new_node   = make_node( new_graph, save_attrs );
		set_nodelabel(new_node, node->label);

		/* Zeiger vom alten Knoten auf den Neuen		      */
		set_nodeattrs(node, make_attr(ATTR_DATA, (char *) new_node));

	} end_for_all_nodes( g, node );

	/* kopiere jetzt die Kanten					      */
	for_all_nodes( g, node )
	{
		for_sourcelist( node, edge )
		{
			save_attrs = edge->attrs;
			target     = edge->tnode;

			/*bestimme interne Kantenrichtung (obwohl ungerichtet)*/
			real_source = (Snode)sedge_real_source ( edge );

			/*mit dies. Abfrage wird jede Kante nur einmal erfasst*/
			if( (real_source == node ) && (target != node))
			{
				new_source = attr_data_of_type( node, Snode );
				new_target = attr_data_of_type( target, Snode );
		
				/* fuege neue Kante in den neuen Graphen ein  */
				new_edge = make_edge(new_source, 
							new_target, save_attrs);

				/* Zeiger von der alten Kante auf ihre Kopie  */
                        	set_edgeattrs(edge, 
				     make_attr( ATTR_DATA, (char *)new_edge ));
			
				/* Zeiger von der neuen Kante auf das Original*/
                        	set_edgeattrs(new_edge, 
				         make_attr( ATTR_DATA, (char *)edge ));
			}

		} end_for_sourcelist( node, edge );

	} end_for_all_nodes( g, node );

	return( new_graph );
}
/******************************************************************************/


