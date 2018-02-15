#include <sgraph/std.h>
#include <sgraph/sgraph.h>
#include <sgraph/slist.h>
#include <sgraph/graphed.h>

#include <utils/utils_export.h>
#include <graphed/simple_fs.h>
#include <graphed/util.h>
#include <utils/external_program_caller.h>


Graphed_cep_info new_graphed_cep_info (void)
{
	Graphed_cep_info cep_info;

	cep_info.programname = NULL;
	cep_info.read_file_name ="graphed.in";
	cep_info.read_file_name_switch = ">";
	cep_info.write_file_name = "graphed.out";
	cep_info.write_file_name_switch = "<";

	cep_info.read_file_proc = NULL;
	cep_info.write_file_proc = NULL;

	cep_info.remove_files = TRUE;

	return cep_info;
}



Global	int	run_external_program (Graphed_cep_info *cep_info)
{
	char	buffer [1000];
	int	status;

	sprintf (buffer, "%s  %s %s  %s %s",
		cep_info->programname,
		cep_info->read_file_name_switch,
		cep_info->read_file_name,
		cep_info->write_file_name_switch,
		cep_info->write_file_name);

	if (cep_info->write_file_proc != NULL) {
		status = cep_info->write_file_proc (cep_info->write_file_name);
	}

	if (status) {

		system (buffer);
		dispatch_user_action (UNSELECT);

		if (cep_info->read_file_proc != NULL) {
			cep_info->read_file_proc (cep_info->read_file_name);
		}
	}

	return status;
}


Global	int	graphed_standard_write_file (char *filename)
{
	dispatch_user_action (BASIC_STORE, wac_buffer, filename);
	return TRUE;
}


Global	int	graphed_standard_read_file (char *filename)
{
	dispatch_user_action (BASIC_LOAD, wac_buffer, filename);
	return TRUE;
}


#include <graphed/existing_extensions.h>
#ifdef EXTENSION_tree_layout_walker
#include <tree_layout_walker/tree_layout_walker_export.h>
#endif

void	menu_test_call_external_program (Menu menu, Menu_item menu_item)
{
	Graphed_cep_info	cep_info;

	cep_info = new_graphed_cep_info ();

	cep_info.programname = strsave ("./foo");
	cep_info.write_file_proc = graphed_standard_write_file;
	cep_info.read_file_proc = graphed_standard_read_file;

	run_external_program (&cep_info);

#ifdef EXTENSION_tree_layout_walker
	call_sgraph_proc (call_fit_nodes_to_text, NULL);
#endif
	call_sgraph_proc (call_tree_layout_walker, NULL);
	menu_shrink_buffer (menu, menu_item);
}
