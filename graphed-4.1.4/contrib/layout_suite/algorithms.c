#include "sgraph/std.h"
#include "sgraph/slist.h"
#include "sgraph/sgraph.h"
#include "sgraph/graphed.h"

#include <xview/xview.h>
#include <xview/panel.h>
#include "layout_suite_export.h"


LS_algorithm	new_ls_algorithm(void)
{
	LS_algorithm	algorithm;

	algorithm = (LS_algorithm) malloc(sizeof(struct ls_algorithm));

 	algorithm->name      = NULL;
	algorithm->full_name = NULL;

	algorithm->call_proc = (Pointer_to_procedure)NULL;
	algorithm->show_settings = (Pointer_to_procedure)NULL;
	algorithm->showing_settings = (Pointer_to_procedure)NULL;
	algorithm->done_settings = (Pointer_to_procedure)NULL;

	algorithm->settings = NULL;
	algorithm->active = 0;

	return algorithm;
}


LS_algorithm	ls_algorithm_create (void)
{
	LS_algorithm		algorithm;

	algorithm = new_ls_algorithm();
	layout_suite_settings.algorithms = add_to_slist (
		layout_suite_settings.algorithms,
		make_attr(ATTR_DATA, (char *)algorithm));

	return algorithm;
}



void		ls_algorithm_delete (LS_algorithm algorithm)
{
	Slist	l;
	int	found = FALSE;

	for_slist (layout_suite_settings.algorithms, l) {
		if (ls_list_algorithm(l) == algorithm) {
			found = TRUE;
			break;
		}
	} end_for_slist (layout_suite_settings.algorithms, l);

	if (found) {
		subtract_from_slist (layout_suite_settings.algorithms, l->attrs);
	}
}



void	ls_algorithm_set (LS_algorithm algorithm, ...)
{
	va_list			args;
	LS_algorithm_specs	arg;

	va_start(args, algorithm);
	
	while ((arg = va_arg(args, LS_algorithm_specs)) !=
	       END_OF_LS_ALGORITHM_SPECS) switch (arg) {

	    case LS_NAME :
		algorithm->name = va_arg(args, char *);
		break;

	    case LS_FULL_NAME :
		algorithm->full_name = va_arg(args, char *);
		break;

	    case LS_CALL_PROC :
		algorithm->call_proc = va_arg(args, Pointer_to_procedure);
		break;
		
	    case LS_TEST_PROC :
		algorithm->test_proc = va_arg(args, Pointer_to_procedure);
		break;
		
	    case LS_SETTINGS :
		algorithm->settings = va_arg(args, char *);
		break;

	    case LS_SHOW_SETTINGS :
		algorithm->show_settings = va_arg(args, Pointer_to_procedure);
		break;

	    case LS_SHOWING_SETTINGS :
		algorithm->showing_settings = va_arg(args, Pointer_to_procedure);
		break;

	    case LS_SETTINGS_DONE :
		algorithm->done_settings = va_arg(args, Pointer_to_procedure);
		break;

	    case LS_ACTIVE :
		algorithm->active = va_arg(args, bool);
		break;

	    case LS_ACTIVE_ITEM :
		algorithm->active_item = va_arg(args, Panel_item);
		break;

	    case LS_SETTINGS_ITEM :
		algorithm->settings_item = va_arg(args, Panel_item);
		break;

	    case LS_DO_ITEM :
		algorithm->do_item = va_arg(args, Panel_item);
		break;

	    default :
		break;

	}

	va_end(args);
}
