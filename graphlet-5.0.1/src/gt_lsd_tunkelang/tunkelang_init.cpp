/* This software is distributed under the Lesser General Public License */
{
	add_to_layout_menu("Tunkelang's Layout ...",tunkelang_layout);
	add_to_goodies_menu("Random coordinates"   ,shake_graph);

	ls_algorithm_set (ls_algorithm_create(),
		LS_NAME,           "Tunkelang-10",
		LS_FULL_NAME,      "Tunkelang-10",
		LS_CALL_PROC,      call_tunkelang,
		LS_SETTINGS,       NULL,
		LS_SHOW_SETTINGS,  NULL,
		LS_ACTIVE,         TRUE,
		NULL);	
}
