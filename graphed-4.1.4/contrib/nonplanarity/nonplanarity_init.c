{

/****************************************************************************\
 *                                                                          *
 *  nonplanarity_init.c                                                     *
 *  -------------------                                                     *
 *                                                                          *
 *  author:  a.j. winter (11027)  06/93.                                    *
 *                                                                          *
\****************************************************************************/


	Menu	MaxPlanarSubgraphMenu, CrossingNumberMenu, ThicknessMenu;
	Menu	nonplanarity_submenu;

        extern  MaxPlanarSettings 	currMaxPlanarSettings;


	MaxPlanarSubgraphMenu = graphed_create_submenu ();

/*      here initialise settings 					    */
        currMaxPlanarSettings = create_and_init_maxplanar_settings(); 

	add_entry_to_menu (MaxPlanarSubgraphMenu,
		"Jayakumar",
		MenuMaxPlanarExecuteJayakumar);
	add_entry_to_menu (MaxPlanarSubgraphMenu,
		"Greedy (determin.)",
		MenuMaxPlanarExecuteGreedy);
	add_entry_to_menu (MaxPlanarSubgraphMenu,
		"Greedy (random.)",
		MenuMaxPlanarExecuteRandomizedGreedy);
	add_entry_to_menu (MaxPlanarSubgraphMenu,
		"Random-Graph Test",
		MenuMaxPlanarExecuteRandomizedGraphTest);
	add_entry_to_menu (MaxPlanarSubgraphMenu,
		"settings ...",
		MenuMaxPlanarSettings);

	ThicknessMenu = graphed_create_submenu ();
	add_entry_to_menu (ThicknessMenu,
		"run",
		MenuThicknessExecute);
/*	add_entry_to_menu (ThicknessMenu,
		"settings ...",
		MenuThicknessSettings); */

	CrossingNumberMenu = graphed_create_submenu ();
	add_entry_to_menu (CrossingNumberMenu,
		"Complete Embedding",
		MenuCrossingNumberExecuteCompleteEmbedding);
	add_entry_to_menu (CrossingNumberMenu,
		"Bipartite Embedding",
		MenuCrossingNumberExecuteBipartiteEmbedding);
	add_entry_to_menu (CrossingNumberMenu,
		"Convex Draw",
		MenuCrossingNumberExecuteConvexDraw);
	add_entry_to_menu (CrossingNumberMenu,
		"Chrobak Payne",
		MenuCrossingNumberExecuteChrobakPayne);
	add_entry_to_menu (CrossingNumberMenu,
		"Chrobak Payne Asslia",
		MenuCrossingNumberExecuteChrobakPayneAsslia);
	add_entry_to_menu (CrossingNumberMenu,
		"Spring Embedder (R&F)",
		MenuCrossingNumberExecuteSpringRF);
	add_entry_to_menu (CrossingNumberMenu,
		"Spring Embedder (Kamada)",
		MenuCrossingNumberExecuteSpringKamada);
/*	add_entry_to_menu (CrossingNumberMenu,
		"Naive Embedding (not yet!)",
		MenuCrossingNumberExecuteNaiveEmbedding); */
	add_entry_to_menu (CrossingNumberMenu,
		"count edge crossings",
		MenuCrossingNumberCountCrossings);
/*	add_entry_to_menu (CrossingNumberMenu,
		"settings ...",
		MenuCrossingNumberSettings); */


	add_to_tools_menu ("PQ Planarity-Test", MenuPQPlanarityTest);

	nonplanarity_submenu = graphed_create_pin_menu ("Non Planarity");

 	add_menu_to_menu (nonplanarity_submenu,
			  "Maximum planar subgraph",
			  MaxPlanarSubgraphMenu);
 	add_menu_to_menu (nonplanarity_submenu,
			  "Thickness",
			  ThicknessMenu);
 	add_menu_to_menu (nonplanarity_submenu,
			  "Crossingnumber",
			  CrossingNumberMenu);

	add_entry_to_menu (nonplanarity_submenu,
			   "Biconnected Components",
			   MenuBiConnComp);
	add_entry_to_menu (nonplanarity_submenu,
			    "Reset labels and attributes",
			    MenuResetGraph);

	add_menu_to_tools_menu ("Non-Planarity Tools", nonplanarity_submenu);

}
