/**********************************************************************/
/*                                                                    */
/*                                                                    */
/*                SPRINGEMBEDDER-LAYOUT ALGORITHM                     */
/*                                                                    */
/*                                                                    */
/*                 programmed by Claus-Dieter Ruscha                  */
/*                                                                    */
/*                               at                                   */
/*                                                                    */
/*                      University  of Passsau                        */
/*                                                                    */
/*                       Prof. Dr. Brandenburg                        */
/*                                                                    */
/*                             1992                                   */
/*                                                                    */
/*                                                                    */
/*                     based on a paper by                            */
/*                                                                    */
/*              Thomas Fruchterman and Edward Reingold                */
/*                                                                    */
/*                                                                    */
/*                                                                    */
/*		some features / polishing by M. Himsolt               */
/*                                                                    */
/**********************************************************************/


#include <std.h>
#include <sgraph.h>
#include <slist.h>
#include <graphed.h>
#include <math.h>

#include "springembedder_rf_export.h"

#define PROC void
#define MAIN void
#define FUNC /**/
#define mark(node) attr_flags(node)
#define weight(x) (*(attr_data_of_type (x, float*)))
#define has_reflexive_edge attr_flags
#define reflexive_edge(edge) (edge->snode->nr == edge->tnode->nr)
#define for_some_nodes(from_node, to_node)     do {
#define end_for_some_nodes(from_node, to_node) } while ( ((from_node)=(from_node)->suc) != (to_node)->suc )
#define for_all_nodes_exept_last_node(graph, node)     if ( ((node)=(graph->nodes)) != (Snode)nil)  do {
#define end_for_all_nodes_exept_last_node(graph, node) } while ( ((node)=(node)->suc) != graph->nodes->pre )
#define empty_string(string) (*(string) == '\0')





/**********************************************************************/
/*                                                                    */
/*             globale Variable und Datenstrukturen                   */
/*                                                                    */
/**********************************************************************/


typedef struct 
{       float x, y;
} vektor;





/**********************************************************************/
/*                                                                    */
/*                    P r o z e d u r e n                             */
/*                                                                    */
/**********************************************************************/



PROC unmark_all_nodes (Sgraph graph)
     /*****************************************************************/
     /* setzt die Markierungen aller Knoten von graph auf FALSE       */
     /*****************************************************************/
                  
{    Snode  node;
     for_all_nodes( graph, node )
     {   mark( node ) = FALSE;
     }   end_for_all_nodes( graph, node ); 
}


PROC mark_connected_nodes (Sgraph graph, Snode node)
     /*****************************************************************/
     /* markiert in graph die mit node durch Kanten verbundene Knoten */
     /*****************************************************************/
                  
                 
{    Sedge  edge;
     if ( mark( node ) == FALSE )
     {  mark( node ) = TRUE;
        for_sourcelist( node, edge )
        {   mark_connected_nodes( graph, edge->tnode );
        }   end_for_sourcelist( node, edge );
        if ( graph->directed ) 
        {  for_targetlist( node, edge )
           {   mark_connected_nodes( graph, edge->snode );
           }   end_for_targetlist( node, edge );
        }     
     }       
}


FUNC bool is_connected (Sgraph graph)
     /*****************************************************************/
     /* ueberprueft, ob graph zusammenhaengend ist (oder aus mehreren */
     /* untereinander nicht verbundenen Teilgraphen besteht)          */
     /*****************************************************************/
                  
{    Snode  node;
     bool   connected;
     unmark_all_nodes( graph );
     mark_connected_nodes( graph, first_node_in_graph( graph ) );
     connected = TRUE;
     for_all_nodes( graph, node )
     {   if ( ! mark( node ) )
            connected = FALSE;
     }   end_for_all_nodes( graph, node );
     return connected;
}


FUNC bool multiple_connected_nodes (Sgraph graph)
     /*****************************************************************/
     /* ueberprueft, of graph Knoten besitzt, die durch mehr als eine */
     /* Kante verbunden sind. Eventuell mehrfach vorhandene reflexive */
     /* Kanten werden -obwohl ihre Existenz semantisch nicht sinnvoll */
     /* ist - dabei nicht reklamiert, da sie den Ablauf des Algorth.  */  
     /* nicht stoeren                                                 */
     /*****************************************************************/
                  
{    Snode  node;
     Sedge  edge;
     bool multiple_connected = FALSE;
     int node_index = 0;
     unmark_all_nodes( graph );
     for_all_nodes( graph, node )
     {   node_index++;
	 for_sourcelist( node, edge )
	 {   if ( ! reflexive_edge( edge ) )
             {  if ( mark( edge->tnode ) == node_index )
	           multiple_connected = TRUE;
                else
	           mark( edge->tnode ) = node_index;
             }
         }   end_for_sourcelist( node, edge );
         if (graph->directed)
         {  for_targetlist( node, edge )
 	    {   if ( ! reflexive_edge( edge ) )
                {  if ( mark( edge->snode ) == node_index )
                      multiple_connected = TRUE;
                   else
                      mark( edge->snode ) = node_index;
                }
            }   end_for_targetlist( node, edge );
	 }
     }   end_for_all_nodes( graph, node );
     return multiple_connected;
}


FUNC bool weights_succesfully_read (Sgraph graph, int *enough_memory)
     /*****************************************************************/
     /* liest die Gewichte der Kanten aus dem Feld edge->label in das */
     /* attrs - Feld der Kanten ein, ueberprueft dabei deren Korrekt- */
     /* heit und gibt in der Variablen enough_memory an, ob genug     */
     /* Speicherplatz dabei vorhanden war                             */
     /*****************************************************************/
                  
                           
{    Snode  node;
     Sedge  edge;
     float  *pfloat;
     bool succesfully_read = TRUE;
     for_all_nodes( graph, node ) {

	     pfloat = (float*) malloc( (unsigned) (sizeof(float)) );
             if ( pfloat != nil ) {
		 if ( (node->label == nil) || empty_string(node->label) ) {
		     *pfloat = 1.0;
		 } else {
		     *pfloat = (float) atof(node->label );
		     if (*pfloat == 0.0) {
			 *pfloat = 1.0;
		     }
		 } 
		 set_nodeattrs(node, make_attr(ATTR_DATA, (char*) pfloat));
             } else {
		 *enough_memory = FALSE;
		 succesfully_read = FALSE; 
             }

	 for_sourcelist( node, edge) {

	     pfloat = (float*) malloc( (unsigned) (sizeof(float)) );
             if ( pfloat != nil ) {
		 if ( (edge->label == nil) || empty_string(edge->label) ) {
		     *pfloat = 1.0;
		 } else {
		     *pfloat = (float) atof( edge->label );
		     if (*pfloat == 0.0) {
			 *pfloat = 1.0;
		     }
		 } 
		 set_edgeattrs (edge, make_attr(ATTR_DATA, (char*) pfloat));
             } else {
		 *enough_memory = FALSE;
		 succesfully_read = FALSE; 
             }

         } end_for_sourcelist( node, edge );
     } end_for_all_nodes( graph, node );

     return succesfully_read;
}


FUNC bool neutral_weights_succesfully_written(Sgraph graph)
     /*****************************************************************/
     /* falls draw_weighted_layout (im Fenster) auf off gesetzt wird  */
     /* wird in das attrs-Feld der Kanten der neutrale Wert 1 geschr. */
     /*****************************************************************/
                  
{    Snode  node;
     Sedge  edge;
     float  *pfloat;
     bool succesfully_written = TRUE;

     for_all_nodes (graph, node) {

	 pfloat = (float*) malloc ((unsigned)(sizeof(float)));
	 if ( pfloat != nil ) {
	     *pfloat = 1.0;
	     set_nodeattrs (node, make_attr(ATTR_DATA, (char*) pfloat));
	 } else {
	     succesfully_written = FALSE; 
	 }

	 for_sourcelist (node, edge) {
	     pfloat = (float*) malloc ((unsigned)(sizeof(float)));
             if ( pfloat != nil ) {
		 *pfloat = 1.0;
		 set_edgeattrs (edge, make_attr(ATTR_DATA, (char*) pfloat));
             } else {
                succesfully_written = FALSE; 
	     }
         } end_for_sourcelist (node, edge);
     } end_for_all_nodes (graph, node);

     return succesfully_written;
}


FUNC bool is_correct (Sgraph graph)
     /*****************************************************************/
     /* ueberprueft, ob graph in einer korrekten Form vorliegt, um    */   
     /* von dem Nature-Algorithmus bearbeitet zu werden               */ 
     /*****************************************************************/
                  
{
	bool	correct = FALSE,
		enough_memory = TRUE;

	if ( graph == empty_graph || graph->nodes == nil ) {

 		error ( "No graph selected\n" );

	} else if (!is_connected(graph)) {

		error ( "Graph is not connected\n" );

	} else if (multiple_connected_nodes(graph)) {

		error ( "Graph contains a multiple edge\n" );

	} else if (springembedder_rf_settings.draw_weighted &&
	           !weights_succesfully_read(graph,&enough_memory)) {
	
		if (!enough_memory ) {
			error ( "Spring embedder : not enough memory avaiable\n" );
		} else {
			warning ( "Spring embedder : one or several edge weights are incorrect\n" );
		}

	} else if (!springembedder_rf_settings.draw_weighted &&
	           !neutral_weights_succesfully_written(graph)) {

		error ( "Springembedder: not enough memory avaiable !\n" );
	
	} else {
		correct = TRUE;
	}

	return correct;
}	 


Local	int	node_is_fixed (Snode node)
{
/*
  return (node->label != NULL) && (!strcmp(node->label,"fix"));
*/
    return FALSE;
}



PROC straighten_edges (Sgraph sgraph)
     /*****************************************************************/
     /* "biegt" eventuell vorhandene nicht geradlienig verlaufende    */
     /* Kanten im Graphen gerade, da der Nature-Algorithmus gerad-    */
     /* lienig verlaufende Kanten vorraussetzt. Reflexive Kanten wer- */
     /* den dabei nicht begradigt. Knoten, die reflexive Kanten ent-  */
     /* halten, werden als solche markiert, um spaeter schneller die  */
     /* reflexiven Kanten wiederzufinden.                             */
     /*****************************************************************/
                   
{    Snode  snode;
     Sedge  sedge;
     Graphed_edge gedge;
     Edgeline edgeline, straight_edgeline;
     int x1, y1, x2, y2;
     unmark_all_nodes( sgraph );
     for_all_nodes (sgraph, snode) {
	 for_sourcelist (snode, sedge) {
	     if (!reflexive_edge (sedge)) {
		 gedge = graphed_edge (sedge);
		 edgeline = (Edgeline) edge_get (gedge, EDGE_LINE);
		 if (!is_single_edgeline (edgeline)) {
		     /* free_edgeline( edgeline ); */
		     x1 = snode_x (snode); x2 = sedge->tnode->x;
		     y1 = snode_y (snode); y2 = sedge->tnode->y; 
		     straight_edgeline = add_to_edgeline (new_edgeline (x1, y1), x2,y2);
		     edge_set (graphed_edge (sedge), EDGE_LINE, straight_edgeline, 0);
		 }
             } else {
		 has_reflexive_edge (snode) = TRUE;
	     }
	 } end_for_sourcelist( snode, sedge);

	 if (sgraph->directed) {
	     for_targetlist (snode, sedge) {
		 if (!reflexive_edge (sedge)) {
		     gedge = graphed_edge( sedge );
		     edgeline = (Edgeline) edge_get (gedge, EDGE_LINE);
		     if (!is_single_edgeline(edgeline)) {
			 /* free_edgeline(edgeline); */
			 x1 = snode_x(snode); x2 = sedge->snode->x;
			 y1 = snode_y(snode); y2 = sedge->snode->y; 
			 straight_edgeline = add_to_edgeline (new_edgeline(x1,y1), x2,y2);
			 edge_set (graphed_edge(sedge), EDGE_LINE, straight_edgeline, 0);
		     }
                 }
	     } end_for_targetlist( snode, sedge );
	 }
     } end_for_all_nodes( sgraph, snode );
}


PROC number_nodes (Sgraph graph)
     /*****************************************************************/
     /* nummeriert die Knoten von graph durck                         */
     /*****************************************************************/
                  
{    Snode  node;
     int    number=0;
     for_all_nodes( graph, node )
     {   node->nr = number++;
     }   end_for_all_nodes ( graph, node );
}


FUNC int number_of_nodes (Sgraph graph)
     /*****************************************************************/
     /* stellt fest, wieviel Knoten graph bestitzt                    */
     /*****************************************************************/
                  
{    Snode  node;
     int n;
     n = 0;
     for_all_nodes( graph, node )
     {   n++;
     }   end_for_all_nodes( graph, node );
     return n;
}


FUNC vektor *init_node_positions (Sgraph graph, int N, int *enough_memory)
     /*****************************************************************/
     /* gibt ein Pointer auf ein Array der Laenge N mit Eintraegen    */
     /* vom Typ vektor zurueck, indem die Koordinaten der Knoten von  */
     /* graph stehen und ordnet jedem 'node' von 'graph' einen Index  */
     /* i zu, so dass gilt : node_positions[i] gehoert zum i-ten      */
     /* 'node' der Knotenliste (wird fuer compute_forces benoetigt)   */
     /*****************************************************************/    
                  
              
                           
{
	Snode  node;
	vektor *node_positions;
	int    i;
	
	node_positions = (vektor*) malloc( (unsigned) ( N * sizeof( vektor ) ) );
	
	if ( node_positions == nil )
        {  *enough_memory = FALSE;
	}
        else
        {  i = 0;
           for_all_nodes( graph, node )
           {   node_positions[i].x = snode_x( node );
               node_positions[i].y = snode_y( node );
               mark( node ) = i;
               i++; 
           }   end_for_all_nodes( graph, node );
	};
	return node_positions;
}


FUNC int* init_fixed_nodes (Sgraph graph, int N, int *enough_memory)
{
    Snode  node;
    int *fixed_nodes;
    int    i;
	
    fixed_nodes = (int*) malloc( (unsigned) (N*sizeof(int)));
	
    if (fixed_nodes == nil) {
	*enough_memory = FALSE;
    } else {
	i = 0;
	for_all_nodes( graph, node ) {
	    fixed_nodes[i] = node_is_fixed(node);
	    i++; 
	} end_for_all_nodes( graph, node );
    }

    return fixed_nodes;
}


PROC init_null (vektor *forces, int N)
     /*****************************************************************/
     /* initialisiert das Array forces der Laenge N vom Typ vektor    */
     /* mit null - Werten                                             */
     /*****************************************************************/
                    
           
{    int i;
     for ( i = 0; i < N; i++ )
     {   forces[i].x = 0.0;
         forces[i].y = 0.0;
     }
}


FUNC vektor *init_forces (int N, int *enough_memory)
     /*****************************************************************/
     /* gibt ein Pointer auf ein Array der Laenge N mit Eintraegen    */
     /* vom Typ vektor zurueck, die alle Koordinaten den Wert 0       */
     /* haben                                                         */
     /*****************************************************************/
           
                         
{    vektor *forces;
     forces = (vektor*) malloc( (unsigned) ( N * sizeof( vektor ) ) );
     if ( forces == nil )
     {  *enough_memory = FALSE;
     }
     else /* else-Zweig nicht unbedingt erforderlich */
        init_null( forces, N );
     return forces;
}


FUNC  float vek_abs (vektor v)
     /*****************************************************************/
     /* berechnet die euklidische Norm fuer ein Element von Typ       */
     /* vektor, die ja der Laenge des vektors entspricht              */
     /*****************************************************************/
               
{     return   hypot( v.x, v.y ); /* hypot( x, y ) = sqrt( x*x + y*y ) */
}


FUNC float compute_max_force (vektor *forces, int N, int *fixed_nodes)
     /*****************************************************************/
     /* bestimmt die groesste Kraft aus dem Array forces der Laenge N */
     /*****************************************************************/
                    
           
                      
{    float abs_force, max_abs_force;
     int i;

     max_abs_force = 0;

     for (i = 0; i<N; i++) if (!fixed_nodes[i]) {
	 abs_force = vek_abs (forces[i]);
         if (abs_force > max_abs_force) {
	     max_abs_force = abs_force;
	 }
     }

     return max_abs_force;
}
 
   
FUNC vektor add (vektor v1, vektor v2)
     /*****************************************************************/
     /* addiert zwei Elemente von Type vektor komponentenweise        */
     /*****************************************************************/
                   
{    vektor v;
     v.x = ( v1.x + v2.x );
     v.y = ( v1.y + v2.y );
     return v;
}


FUNC vektor sub (vektor v1, vektor v2)
     /*****************************************************************/
     /* substrahiert zwei Elemente von Type vektor komponentenweise   */
     /*****************************************************************/
                   
{    vektor v;
     v.x = ( v1.x - v2.x );
     v.y = ( v1.y - v2.y );
     return v;
}


FUNC vektor sca_diff (vektor v, float scalar)
     /*****************************************************************/
     /* diffidiert ein Element vom Typ vektor mit einem Element vom   */
     /* Typ float durch uebliche komponentenweise Division            */
     /*****************************************************************/
              
                  
{    v.x = v.x / scalar;
     v.y = v.y / scalar;
     return v;
}


FUNC vektor center (vektor *node_positions, int N)
     /*****************************************************************/
     /* berechnet den Mittelpunkt der Knotenpositionen aus dem Array  */
     /* node_positions der Laenge N                                   */
     /*****************************************************************/
                            
           
{    vektor center; 
     int i;
     center.x = 0.0;
     center.y = 0.0;
     for ( i = 0; i < N; i++ )
         center = add( center, node_positions[i] );
     return sca_diff( center, ( (float) N ) ); 
}


PROC compute_forces (float optimal_distance, vektor *forces, vektor *node_positions, Sgraph graph)
     /*****************************************************************/
     /* berechnet die Kraefte, die an den Knoten des Graphen graph    */
     /* wirken. Dabei werden die Koordinaten der Knoten im Array      */
     /* node_position uebergeben. Das Ergebnis wird im Array forces   */
     /* zurueckgegeben. Die Berechnung der Kraefte ist nach Meinung   */
     /* des Programmierers sehr gut optimiert.                        */
     /*****************************************************************/
                             
                                     
                  
{    Snode  node1,node2;
     Sedge  edge;
     vektor attractive_force,  repulsive_force, force, delta;
     float  attractive_factor, repulsive_factor;
     float  sum_sqr_delta_xy, sqr_optimal_distance = optimal_distance * optimal_distance;
     int    node_index_1, node_index_2;

     for_all_nodes (graph, node1) {

         /* compute edge forces */
         node_index_1 = node1->nr;
         for_sourcelist (node1, edge) {
	     if (!reflexive_edge (edge) && (graph->directed || node1->nr < edge->tnode->nr)) {
		 node_index_2 = edge->tnode->nr;
		 delta.x = node_positions[node_index_2].x - node_positions[node_index_1].x;
		 delta.y = node_positions[node_index_2].y - node_positions[node_index_1].y;
		 /*
		 if (node_positions[node_index_2].x > node_positions[node_index_1].x) {
		     delta.x -=
			 (int)node_get(graphed_node(node1), NODE_WIDTH) / 2 +
			 (int)node_get(graphed_node(edge->tnode), NODE_WIDTH) / 2;
		 } else {
		     delta.x +=
			 (int)node_get(graphed_node(node1), NODE_WIDTH) / 2 +
			 (int)node_get(graphed_node(edge->tnode), NODE_WIDTH) / 2;
		 }
		 if (node_positions[node_index_2].y > node_positions[node_index_1].y) {
		     delta.y -=
			 (int)node_get(graphed_node(node1), NODE_HEIGHT) / 2 +
			 (int)node_get(graphed_node(edge->tnode), NODE_HEIGHT) / 2;
		 } else {
		     delta.y +=
			 (int)node_get(graphed_node(node1), NODE_HEIGHT) / 2 +
			 (int)node_get(graphed_node(edge->tnode), NODE_HEIGHT) / 2;
		 }
		 */
		 if ((delta.x == 0) && (delta.y == 0)) {
		     /* Prevent 0 distance, which is considered unnatural */
		     delta.x = 1;
		     delta.y = 1;
		 } 
		 sum_sqr_delta_xy = delta.x * delta.x + delta.y * delta.y;
#ifdef DEBUG
fprintf (stderr, "------------------------------\n");
fprintf (stderr, "[%f %f] [%f %f]\n",
	 node_positions[node_index_1].x, node_positions[node_index_1].y,
	 node_positions[node_index_2].x, node_positions[node_index_2].y);
#endif
#ifdef DEBUG
fprintf (stderr, "delta %f, sqr delta %f\n", sqrt(sum_sqr_delta_xy), sum_sqr_delta_xy);
fprintf (stderr, "optim %f, sqr optim %f\n", sqrt(optimal_distance), optimal_distance);
#endif
		 attractive_factor  = sqrt(sum_sqr_delta_xy) / ( weight(edge) * optimal_distance );
		 attractive_force.x = attractive_factor * delta.x;
		 attractive_force.y = attractive_factor * delta.y;
#ifdef DEBUG
fprintf (stderr, "a factor %f\n", attractive_factor);
fprintf (stderr, "a force %f %f\n", attractive_force.x, attractive_force.y);
#endif
		 repulsive_factor  = weight(edge) * weight(edge) *
		     sqr_optimal_distance / sum_sqr_delta_xy
		     - 1.0;
		 repulsive_force.y = repulsive_factor * delta.y;
		 repulsive_force.x = repulsive_factor * delta.x;
#ifdef DEBUG
fprintf (stderr, "r factor %f\n", repulsive_factor);
fprintf (stderr, "r force %f %f\n", repulsive_force.x, repulsive_force.y);
#endif
		 force.x = attractive_force.x - repulsive_force.x;
		 force.y = attractive_force.y - repulsive_force.y;

		 forces[node_index_1].x += force.x;
		 forces[node_index_1].y += force.y;
		 forces[node_index_2].x -= force.x;
		 forces[node_index_2].y -= force.y;

             }
         } end_for_sourcelist( node1, edge );

     } end_for_all_nodes (graph, node1);

     for_all_nodes (graph, node1) {
	 node_index_1 = node1->nr;
	 for_all_nodes (graph, node2) {
	     if (node1->nr < node2->nr) {
		 node_index_2 = node2->nr;
		 delta.x = node_positions[node_index_2].x - node_positions[node_index_1].x;
		 delta.y = node_positions[node_index_2].y - node_positions[node_index_1].y;
		 /*
		 if (node_positions[node_index_2].x > node_positions[node_index_1].x) {
		     delta.x -=
			 (int)node_get(graphed_node(node1), NODE_WIDTH) / 2 +
			 (int)node_get(graphed_node(node2), NODE_WIDTH) / 2;
		 } else {
		     delta.x +=
			 (int)node_get(graphed_node(node1), NODE_WIDTH) / 2 +
			 (int)node_get(graphed_node(node2), NODE_WIDTH) / 2;
		 }
		 if (node_positions[node_index_2].y > node_positions[node_index_1].y) {
		     delta.y -=
			 (int)node_get(graphed_node(node1), NODE_HEIGHT) / 2 +
			 (int)node_get(graphed_node(node2), NODE_HEIGHT) / 2;
		 } else {
		     delta.y +=
			 (int)node_get(graphed_node(node1), NODE_HEIGHT) / 2 +
			 (int)node_get(graphed_node(node2), NODE_HEIGHT) / 2;
		 }
		 */
		 if ((delta.x == 0) && (delta.y == 0)) {
		     /* Prevent 0 distance, which is considered unnatural */
		     delta.x = 1;
		     delta.y = 1;
		 }
		 repulsive_factor  = sqr_optimal_distance / (delta.x * delta.x + delta.y * delta.y);
		 repulsive_force.x = repulsive_factor * delta.x;
		 repulsive_force.y = repulsive_factor * delta.y;
#ifdef DEBUG
fprintf (stderr, "r factor %f\n", repulsive_factor);
fprintf (stderr, "r force %f %f\n", repulsive_force.x, repulsive_force.y);
fprintf (stderr, "------------------------------\n");
#endif
		 forces[node_index_1].x -= repulsive_force.x;
		 forces[node_index_1].y -= repulsive_force.y;
		 forces[node_index_2].x += repulsive_force.x;
		 forces[node_index_2].y += repulsive_force.y;
	     }
	 } end_for_all_nodes (graph, node2);

     } end_for_all_nodes (graph, node1);

}

    
PROC compute_new_node_positions (vektor *node_positions, vektor *forces, int N, float temperature, Sgraph graph)

     /*****************************************************************/
     /* berechnet entsrechend der an den Knoten wirkenden Kraefte     */
     /* (forces) und der maximal zulaessigen Verschiebung der Knoten  */
     /* (temperature) die neuen Knotenpositionen                      */
     /*****************************************************************/
                                     
           
                       
                  

{
    vektor shift;
    float abs_force;
    int i;
    Snode node;

    i = 0;
    for_all_nodes (graph, node) {
	if (!node_is_fixed(node)) {
#ifdef DEBUG
fprintf (stderr, "------------------------------\n");
fprintf (stderr, "[%f %f] --> \n", node_positions[i].x, node_positions[i].y);
#endif
	    abs_force = vek_abs (forces[i]);
	    if ( abs_force < temperature) {
		shift.x = forces[i].x;
		shift.y = forces[i].y;
	    } else {
		shift.x = forces[i].x / abs_force * temperature;
		shift.y = forces[i].y / abs_force * temperature;
	    }
	    node_positions[i].x += shift.x;
	    node_positions[i].y += shift.y;
#ifdef DEBUG
fprintf (stderr, "[%f %f]\n", node_positions[i].x, node_positions[i].y);
fprintf (stderr, "------------------------------\n");
#endif
	}
	i ++;
    } end_for_all_nodes (graph, node);
}

static int   cool_phase;
static float cool_max_force_1,
             cool_max_force_2,
             cool_max_force_3,
             cool_max_force_4;

FUNC float cool (float max_force)
     /*****************************************************************/
     /* berechnet abhaengig von max_force die fuer diesen Schleichen- */
     /* durchlauf maximal zulaessige Verschiebung (temperature) der   */
     /* Knoten. Nachdem der Graph angefangen hat zu schwingen, wird   */
     /* in Phase 2 die maximale zulaessige Verschiebung erheblich     */
     /* niedriger gesetzt, so das eine Schwingung nicht mehr auftritt.*/
     /*****************************************************************/
                     
{
     static float temperature;
     cool_max_force_4 = cool_max_force_3;
     cool_max_force_3 = cool_max_force_2;
     cool_max_force_2 = cool_max_force_1;
     cool_max_force_1 = max_force;

     /* Test ob Graph schwingt */
     if ( (cool_max_force_4 != 0.0) &&
          (fabs((cool_max_force_1 - cool_max_force_3) / cool_max_force_1) < springembedder_rf_settings.vibration) &&
          (fabs((cool_max_force_2 - cool_max_force_4) / cool_max_force_2) < springembedder_rf_settings.vibration) )
     {  cool_phase = 2;
     }
     if ( cool_phase == 1 )
     {  temperature = sqrt( max_force );
     }
     else 
     {  /* cool_phase 2 */
        temperature = sqrt( max_force ) / 15;
     }
     return temperature;
}


PROC write_node_positions_back_to_graph (vektor *node_positions, int N, Sgraph graph)
     /*****************************************************************/
     /* schreibt die Koordinaten der Knoten des S-Graphen graph zu-   */
     /* rueckt in den entsprechenden Graphed-Graphen.                 */
     /* Besitzt ein Knoten reflexive Kanten so werden                 */
     /* diese mit dem Knoten mitverschoben.                           */
     /*****************************************************************/

                            
           
                  
{    Snode  node;
     int i = 0;
     vektor old_node_position, new_node_position;
     Slist list_of_nodes = empty_slist;
     Graphed_group g;
     

     for_all_nodes( graph, node ) {

         old_node_position.x = node->x;
         old_node_position.y = node->y;
         new_node_position.x = (int)(node_positions[i].x + 0.5);
         new_node_position.y = (int)(node_positions[i].y + 0.5);

	 node->x = new_node_position.x;
	 node->y = new_node_position.y;
	 node_set (graphed_node (node), ONLY_SET, NODE_POSITION,
		   (int)(new_node_position.x),
		   (int)(new_node_position.y),
		   0);

         list_of_nodes = add_to_slist (list_of_nodes, make_attr (ATTR_DATA, (char *)node));
         i++;
     }   end_for_all_nodes( graph, node );

     g = create_graphed_group_from_slist (list_of_nodes);
     group_set (g, RESTORE_IT, 0);
     force_repainting();
     
     free_group (g);
     free_slist (list_of_nodes);
}


MAIN call_springembedder_rf (Sgraph_proc_info info)
     /*****************************************************************/
     /*               Hauptporzedur des Algorithmus                   */
     /* Der Algorithmus wurde im wesentlichen nach einer Arbeit von   */
     /* T.Fruchterman und E. Reingold ( University of Illionis )      */
     /* implementiert. Das Abruchkriterium der Schleife und die max.  */
     /* Verschiebung der Knoten wurde so geaendert, dass eine be-     */
     /* liebig genaue Berechnung der Knotenkoordinaten moeglich ist   */   
     /*****************************************************************/
                           
{
     Sgraph graph;
     int	N; /* Anzahl der Knoten des von GraphEd uebergebenen Graphen */
     float	temperature; /* max. Verschiebung eines Knotens in einem Schritt */
     vektor	*node_positions, *forces;
     int	*fixed_nodes;
     bool	enough_memory = TRUE;
     int	iteration_count = 0;
     int	animation_iteration_count = 0;
     float	optimal_distance, max_force;
     cool_phase       = 1;
     cool_max_force_1 = 0.0;
     cool_max_force_2 = 0.0,
     cool_max_force_3 = 0.0,
     cool_max_force_4 = 0.0;
	
     optimal_distance = (float)springembedder_rf_settings.opt_distance;
     graph = info->sgraph;

     if (springembedder_rf_settings.animation)
        dispatch_user_action (UNSELECT);
     
     if (is_correct (graph)) { 
	 straighten_edges (graph);
	 number_nodes (graph); 
	 N = number_of_nodes(graph);
	 node_positions = init_node_positions( graph, N, &enough_memory );
	 forces         = init_forces( N, &enough_memory );
	 fixed_nodes    = init_fixed_nodes (graph, N, &enough_memory);

	 if ( ! enough_memory ) {
	     error ("Springembedder : not enough memory avaiable !\n"); 
	 } else {

	     init_null( forces, N ); 
	     compute_forces (optimal_distance, forces, node_positions, graph );
	     max_force = compute_max_force (forces, N, fixed_nodes);
		   
	     while (max_force > springembedder_rf_settings.max_force  &&
		    iteration_count < springembedder_rf_settings.max_iterations) {

		 init_null (forces,N);
                 compute_forces (optimal_distance, forces, node_positions, graph);
                 max_force   = compute_max_force(forces, N, fixed_nodes);
                 temperature = cool (max_force);
                 compute_new_node_positions (node_positions, forces, N, temperature, graph);
	
		 if (springembedder_rf_settings.animation &&
		     animation_iteration_count >= springembedder_rf_settings.animation_intervals - 1) {
		     write_node_positions_back_to_graph ( node_positions, N, graph );
		     animation_iteration_count = 0;
                 }
                 iteration_count ++;
                 animation_iteration_count ++;
	     }

	     write_node_positions_back_to_graph ( node_positions, N, graph );
	     free( (char*) node_positions ); free( (char*) forces );
	 } 
     }
     info->no_changes = TRUE;
}


MAIN call_do_nothing_springembedder_rf (void)
     /*****************************************************************/
     /* Nur notwendig um im file springembedder_sf in der Prozedur    */
     /* char *springembedder_menu_callback_proc ( menu, menu_item )   */
     /* einen Wert zurueckzugeben.                                    */
     /*****************************************************************/
{
    ;
}


MAIN call_fast_springembedder_rf (Sgraph_proc_info info)
     /*****************************************************************/
     /* Normalversion des Algorithmus. Zwischenergebnisse werden      */
     /* nicht angezeigt.                                              */
     /*****************************************************************/
                           
{    springembedder_rf_settings.animation = FALSE;
     call_springembedder_rf (info);
}


MAIN call_animation_springembedder_rf (Sgraph_proc_info info)
     /*****************************************************************/
     /* Animationsvariante des Algorithmus. Zwischenergebnisse werden */
     /* in den Graphen zurueckgeschrieben und der veraenderte Graph   */
     /* wird im Fenster neu gezeichnet.                               */
     /*****************************************************************/
                           
{    springembedder_rf_settings.animation = TRUE;
     call_springembedder_rf (info);
}
