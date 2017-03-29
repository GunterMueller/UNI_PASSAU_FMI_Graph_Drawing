/****************************************************************/
/*                                                              */
/*   Datei: bend_m.c                                            */
/*                                                              */
/*   bend_main                                                  */
/*   Main Function for  Minimum Number of Bend                  */
/*                                                              */
/*                                                              */
/*                                                              */
/****************************************************************/

#include "minimal_bends_layout2_export.h"
#include "algorithms.h"



/***************************************/
 void minimal_bends_proc(Sgraph_proc_info info)
{
    Sgraph      sGraph;
    SFaceList   sFaceList;
    Snetwork    network;
    SGrid       sGrid;

    /*initialize variables*/
    sFaceList = NULL;
    sGraph    = info->sgraph;

    if(sGraph->directed==true)
    {
	error("this algorithm doesn`t work\non directed graphs");
	return;
    }

    if(embed(sGraph)!=0)
    {
        error("nonplanar graph\n");
	return;
    }

    /* insert attribut structure into all nodes and edges */
    insert_attributes(sGraph);

    sFaceList = find_faces(sGraph);
    if(sFaceList->InnerFaces == NULL)
    {
        error("graph must have at least one inner face\n");
	return;
    }
    network = TransformToNetwork(sGraph, sFaceList);
    reset_edge_visited(sGraph);
    if(MinCostFlow(network)==FALSE)
    {
	error("unable to optimize\n");
        return;
    }
    set_gridpoint_null(sGraph);
    build_ort_rep(network, sFaceList);
    reset_edge_visited(sGraph);
    sGrid = grid_embed(sFaceList);
    reset_edge_visited(sGraph);
    change_graph(sFaceList, sGraph);
    free_data_structures(network, sFaceList, sGrid);
    free_attributes(sGraph); 
}
