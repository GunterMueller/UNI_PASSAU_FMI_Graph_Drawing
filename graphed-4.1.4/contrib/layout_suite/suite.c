/* (C) Universitaet Passau 1986-1994 */

#include "graphed/misc.h"
#include "graphed/graph.h"
#include "graphed/print.h"
#include "graphed/util.h"

#include "sgraph/std.h"
#include "sgraph/slist.h"
#include "sgraph/sgraph.h"
#include "sgraph/graphed.h"

#include <xview/xview.h>
#include <xview/panel.h>
/* #include <pixrect/pixrect_hs.h>*/
#include "layout_suite_export.h"
#include "layout_info/layout_info_export.h"
#include "graphed/existing_extensions.h"
#include "graphed/ps.h"

#include <sys/types.h>
#include <sys/stat.h>
#include <sys/time.h>
#include <sys/timeb.h>
#include <sys/resource.h>
#include <unistd.h>
#include <dirent.h>

#if !defined(_BSD_SOURCE) || !defined(RUSAGE_SELF)
#include <sys/procfs.h>
#endif

#ifndef _BSD_SOURCE
#include <fcntl.h>
#endif


Layout_suite_settings layout_suite_settings;


/****************************************************************/
/*								*/
/*	Auxiliary Procedures : label a graph by adding a	*/
/*	new node in the upper left corner			*/
/*								*/
/****************************************************************/

static	Snode	layout_suite_label_node = empty_snode;


void	call_label_graph (Sgraph_proc_info info)
{
	Sgraph	graph;
	Snode	node;
	Rect	rect;
	int	white_icon_index;

	graph = info->sgraph;

	if (graph == empty_sgraph || graph->nodes == empty_snode) {
		return;
	}

	rect = compute_rect_around_graph (graphed_graph (graph));
	node = make_node (graph, make_attr(ATTR_DATA,NULL));
	set_nodelabel (node, strsave (graph->label));
	set_node_xy (node, rect_left(&rect), rect_top(&rect)-30);

	white_icon_index = add_nodetype ("white.icon");
	node_set (create_graphed_node_from_snode (node),
		NODE_LABEL, strsave (node->label),
		NODE_TYPE, iif (white_icon_index != -1, white_icon_index, 0),
		NODE_SIZE, 30,100,
		0);
	fit_node_to_text (graphed_node(node));
	node_set (graphed_node(node), RESTORE_IT);

	info->recenter = TRUE;
	layout_suite_label_node = node;
}


void	call_unlabel_graph (Sgraph_proc_info info)
{
	remove_node (layout_suite_label_node);
	info->recenter = TRUE;
}


/****************************************************************/
/*								*/
/*	Auxiliary procedure : print the time used, base on	*/
/*	the rusage before and after				*/
/*								*/
/****************************************************************/


#if !defined(_BSD_SOURCE) || defined(PIOCUSAGE)

void            message_time_used (prusage_t prusage_before, prusage_t prusage_after)
{
        /*
         * Ported to SunOS 5.3
         * struct rusage -> prusage_t
         * ru_utime -> pr_utime
         * tv_usec -> tv_nsec
         */

        double  user_seconds_before, user_seconds_after;
        double  system_seconds_before, system_seconds_after;

        user_seconds_before   = prusage_before.pr_utime.tv_sec +
                               (prusage_before.pr_utime.tv_nsec/10000000) / 100.0;
        system_seconds_before = prusage_before.pr_stime.tv_sec +
                               (prusage_before.pr_stime.tv_nsec/10000000) / 100.0;
        user_seconds_after   = prusage_after.pr_utime.tv_sec +
                              (prusage_after.pr_utime.tv_nsec/10000000) / 100.0;
        system_seconds_after = prusage_after.pr_stime.tv_sec +
                              (prusage_after.pr_stime.tv_nsec/10000000) / 100.0;

        message ("User time used            :\t%.2f\n",
                user_seconds_after - user_seconds_before);
        message ("System time used          :\t%.2f\n",
                system_seconds_after - system_seconds_before);
        message ("Time used                 :\t%.2f\n",
                (user_seconds_after - user_seconds_before) +
                (system_seconds_after - system_seconds_before));
}

#else /* Original Solaris 1 Version */

void		message_time_used (struct rusage rusage_before, struct rusage rusage_after)
{
	double	user_seconds_before, user_seconds_after;
	double	system_seconds_before, system_seconds_after;

	user_seconds_before   = rusage_before.ru_utime.tv_sec +
	                        (rusage_before.ru_utime.tv_usec/10000) / 100.0;
	system_seconds_before = rusage_before.ru_stime.tv_sec +
	                        (rusage_before.ru_stime.tv_usec/10000) / 100.0;
	user_seconds_after   = rusage_after.ru_utime.tv_sec +
	                       (rusage_after.ru_utime.tv_usec/10000) / 100.0;
	system_seconds_after = rusage_after.ru_stime.tv_sec +
	                       (rusage_after.ru_stime.tv_usec/10000) / 100.0;

	message ("User time used            :\t%.2f\n",
		user_seconds_after - user_seconds_before);
	message ("System time used          :\t%.2f\n",
		system_seconds_after - system_seconds_before);
	message ("Time used                 :\t%.2f\n",
		(user_seconds_after - user_seconds_before) +
		(system_seconds_after - system_seconds_before));
}
#endif



/****************************************************************/
/*								*/
/*	Run layout suite for a particular algorithm		*/
/*								*/
/****************************************************************/

static	void	determine_layout_suite_directory (char *directory, char *buffer_name)
{
	char	*last_dot;
	char	buffer_filename[FILENAMESIZE];

	strcpy (buffer_filename, buffer_name);
	if (buffer_filename == NULL || !strcmp(buffer_filename, "")) {	
		sprintf (directory, ".");
	} else {

		last_dot = strrchr (buffer_filename, '.');

		if (last_dot != NULL) {		/* strip ending .* */
			*last_dot = '\0';
			sprintf (directory, "%s", buffer_filename);
		} else {
			sprintf (directory, "%s-dir", buffer_filename);
		}

		if (!directory_exists (directory) && mkdir (directory, 0755) == -1) {
			warning ("Cannot create directoy %s\n", directory);
			sys_error (errno);
			directory[0] = '\0';
		}
	}
}

/****************************************************************/
/*								*/
/*	Run layout suite for a particular algorithm		*/
/*								*/
/****************************************************************/


void		suite_do_algorithm (LS_algorithm algorithm, char *directory)
{
	char	filename_graph [FILENAMESIZE],
		filename_info  [FILENAMESIZE],
		filename_xbm   [FILENAMESIZE],
		filename_ps    [FILENAMESIZE];
	FILE	*info_file;

#if !defined(_BSD_SOURCE) || defined (PIOCUSAGE)
        int             fd;
        char            proc[255];
        prusage_t       prusage_before, prusage_after;
#else
        struct  rusage  rusage_before, rusage_after;
#endif /* SYSV || PIOCUSAGE */

	sprintf (filename_graph, "%s/%s.g",    directory, algorithm->name);
	sprintf (filename_info,  "%s/%s.info", directory, algorithm->name);
	sprintf (filename_xbm,   "%s/%s.ras",  directory, algorithm->name);
	sprintf (filename_ps,    "%s/%s.ps",   directory, algorithm->name);

	message ("--- %s ---\n", algorithm->full_name);

	if (layout_suite_settings.create_info_file) {

		info_file = fopen (filename_info, "w");
		if (info_file == (FILE *)NULL) {
			info_file = stdout;
		}
		bypass_messages_to_file (info_file);

		fprintf (info_file, "Algorithm                 :\t%s\n",
		                     algorithm->name);
	}

	
#if !defined(_BSD_SOURCE) || defined(PIOCUSAGE)
        sprintf(proc, "/proc/%d", (int)getpid());
        if ((fd = open(proc, O_RDONLY)) == -1) {
                error("open");
                /* ... */
        };

        if (ioctl(fd, PIOCUSAGE, &prusage_before) == -1) {
                error("ioctl");
                /* ... */
        };
#else
        getrusage (RUSAGE_SELF, &rusage_before);
#endif /* !defined(_BSD_SOURCE) || defined(PIOCUSAGE) */


	call_sgraph_proc (algorithm->call_proc, NULL);


#if !defined(_BSD_SOURCE) || defined(PIOCUSAGE)
        if (ioctl(fd, PIOCUSAGE, &prusage_after) == -1) {
                perror("ioctl");
                /* ... */
        };
        message_time_used (prusage_before, prusage_after);
#else
        getrusage (RUSAGE_SELF, &rusage_after);
        message_time_used (rusage_before, rusage_after);
#endif /* !defined(_BSD_SOURCE) || defined(PIOCUSAGE) */


	if (layout_suite_settings.create_info_file) {
#ifdef EXTENSION_layout_info
	 	call_sgraph_proc (call_layout_info, NULL);
		call_sgraph_proc (call_output_long_statistics, NULL);
#endif
		bypass_messages_to_file ((FILE*)NULL);
		if (info_file != (FILE *)NULL) {
			fclose (info_file);
		}
	}

	if (layout_suite_settings.create_graph_file) {
		/* dispatch_user_action (BASIC_STORE,
			wac_buffer,
			filename_graph); */
	}

	if (layout_suite_settings.save_and_reload &&
	    layout_suite_settings.label_graph) {
		call_sgraph_proc (call_label_graph, NULL);
	}

	if (layout_suite_settings.create_xbitmap_file) {
		write_xbitmap_file (
			filename_xbm,
			wac_buffer,
			compute_print_rect (wac_buffer, AREA_FULL),
			print_settings);
	}

	if (layout_suite_settings.create_postscript_file) {
		Print_settings	settings;
		Rect		print_rect;

		settings = init_print_settings();
		settings.device           = OUTPUT_PS;
		settings.area             = AREA_FULL;
		
		settings.ps.frame_visible = FALSE;
		settings.ps.orientation   = PS_PORTRAIT;
		settings.ps.fit           = FALSE;
		settings.ps.margin_left   = 0.0;
		settings.ps.margin_right  = 0.0;
		settings.ps.margin_top    = 0.0;
		settings.ps.margin_bottom = 0.0;

		print_rect = compute_print_rect (wac_buffer, settings.area);
		settings.frame.width  = (double)rect_width(&print_rect);
		settings.frame.height = (double)rect_height(&print_rect);

		write_postscript_file (filename_ps, wac_buffer, print_rect, settings);
	}

	if (layout_suite_settings.save_and_reload) {
		if (layout_suite_settings.label_graph) {
			call_sgraph_proc (call_unlabel_graph, NULL);
		}
		dispatch_user_action (LOAD_AGAIN_SILENT);
	}

}


void			layout_suite (Layout_suite_settings settings)
{
	Slist		l;
	LS_algorithm	algorithm;
	Graph		graph;
	char		directory[FILENAMESIZE];
	extern	Graph	get_picked_or_only_existent_graph();


	if (buffer_get_filename(wac_buffer) == NULL ||
	    !strcmp(buffer_get_filename(wac_buffer), "")) {
		error ("The layout suite needs a graph that is stored in a file");
		return;
	} else if (buffer_is_changed(wac_buffer)) {
		error ("The layout suite needs a graph that is stored in a file");
		return;
	}

	graph = get_picked_or_only_existent_graph ();

	if (graph == empty_graph) {
		error ("No graph selected\n");
	} else {

		determine_layout_suite_directory (
			directory, buffer_get_filename(wac_buffer));

		if (directory[0] == '\0') {
			error ("Layout suite : cannot create directory\n");
		} else for_slist (settings.algorithms, l) {
			algorithm = ls_list_algorithm (l);
			if (algorithm->active) {
				suite_do_algorithm (algorithm, directory);
			}
		} end_for_slist (settings.algorithms, l);
	}
}


void			single_layout_suite (LS_algorithm algorithm, Layout_suite_settings settings)
{
	Graph		graph;
	char		directory[FILENAMESIZE];
	extern	Graph	get_picked_or_only_existent_graph();

	graph = get_picked_or_only_existent_graph ();

	if (buffer_get_filename(wac_buffer) == NULL ||
	    !strcmp(buffer_get_filename(wac_buffer), "")) {
		error ("Layout suite : graph is not in a file\n");
		return;
	} else if (buffer_is_changed(wac_buffer)) {
		error ("Layout suite : buffer has changed since last save\n");
		return;
	}

	if (graph == empty_graph) {
		error ("No graph selected\n");
	} else if (algorithm->active) {
		determine_layout_suite_directory (
			directory, buffer_get_filename(wac_buffer));
		if (directory[0] == '\0') {
			error ("Layout suite : cannot create directory\n");
		} else {
			suite_do_algorithm (algorithm, directory);
		}
	}
}


void			layout_suite_all (Layout_suite_settings settings)
{
	DIR		*dirp;
	struct	dirent	*dp;
	char		*name;

	dirp = opendir(".");
	for (dp = readdir(dirp); dp != NULL; dp = readdir(dirp)) {
		name = dp->d_name;
		if ((int)strlen (name) > 2 &&
		    !strcmp (name+strlen(name)-2, ".g")) {
		 	dispatch_user_action (BASIC_LOAD, wac_buffer, name);
			layout_suite (settings);
		}
	}
	closedir (dirp);
}


void menu_layout_suite (Menu menu, Menu_item menu_item)
    		     		/* The menu from which it is called	*/
         	          	/* The menu item from ...		*/
{
	save_layout_suite_settings ();

	layout_suite(layout_suite_settings);

}


void menu_layout_suite_all (Menu menu, Menu_item menu_item)
    		     		/* The menu from which it is called	*/
         	          	/* The menu item from ...		*/
{
	save_layout_suite_settings ();

	layout_suite_all(layout_suite_settings);

}


void menu_layout_suite_subframe (Menu menu, Menu_item menu_item)
    		     		/* The menu from which it is called	*/
         	          	/* The menu item from ...		*/
{
	save_layout_suite_settings ();

	show_layout_suite_subframe (NULL);

}
