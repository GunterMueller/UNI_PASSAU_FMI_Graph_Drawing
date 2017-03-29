/* (C) Michael Himsolt, Universitaet Passau 1986-1994	*/
/* To be included in GraphEd 3.*			*/
#include <sgraph/std.h>
#include <sgraph/sgraph.h>
#include <sgraph/slist.h>
#include <sgraph/graphed.h>

#include <xview/xview.h>
#include <xview/panel.h>
#include <xview/frame.h>
#include <xview/notice.h>
#include "fileselector/fileselect.h"
#include "simple_fs.h"
#include "graphed_subwindows.h"

static	Fs_item	fileselector = (Fs_item)NULL;
static	void			(*current_procedure)();
static	char			*current_filename;
static	SFS_load_or_store	current_load_or_store;


static void	local_load(char *dir, char *file);
static void	local_store(char *dir, char *file);


static	void	call_simple_fileselector (Sgraph_proc_info info)
{
	current_procedure (info->sgraph, current_filename);
}

#if FALSE
static void	nf_stdinout(Panel_item item, Event *event)
{
	fls_close( fileselector );
}
#endif

static void	local_load(char *dir, char *file)
{
	char		name[WDLEN+FNLEN+1];
	
	if (!strcmp( dir, "" ) && !strcmp( file, "NOTHING SELECTED" )){
		unlock_user_interface(); /* Abort selected */
		return;
	}
	
	if (!strcmp( dir, "" )) {
		if( !strcmp( file, "" ) ) {
			unlock_user_interface(); /* fileselector gone */
			return;
		} else {
			strcpy( name, file );
		}
	} else {
		if (!strcmp( dir, "/" )) {
			sprintf( name, "/%s", file );
		} else {
			sprintf(name, "%s/%s", dir, file);
		}
	}

	current_filename = name;
	unlock_user_interface(); /* fileselector done */

	switch (current_load_or_store) {
	    case SFS_LOAD_SGRAPH :
		call_sgraph_proc (call_simple_fileselector, NULL);
		break;
	    case SFS_LOAD :
		current_procedure (current_filename);
		break;
            default: break;
	}
}


static void	local_store(char *dir, char *file)
{
	char		name[WDLEN+FNLEN+1];
	
	if (!strcmp( dir, "" ) && !strcmp( file, "NOTHING SELECTED" )){
		unlock_user_interface(); /* Abort selected */
		return;
	}
	
	if (!strcmp( dir, "" )) {
		if( !strcmp( file, "" ) ) {
			unlock_user_interface(); /* fileselector gone */
			return;
		} else {
			strcpy( name, file );
		}
	} else {
		if (!strcmp( dir, "/" )) {
			sprintf( name, "/%s", file );
		} else {
			sprintf(name, "%s/%s", dir, file);
		}
	}
	
	if (strcmp( name, "")  && file_exists(name)  && 
	    notice_prompt (base_frame, NULL,
			NOTICE_MESSAGE_STRINGS,
				"Overwrite Existing file ?",
				NULL,
			NOTICE_BUTTON_YES,	"Ok",
			NOTICE_BUTTON_NO,	"Cancel",
			NULL) == NOTICE_NO) {
		unlock_user_interface(); /* fileselector done */
		return;
	}

	current_filename = name;
	unlock_user_interface(); /* fileselector done */

	switch (current_load_or_store) {
	    case SFS_STORE_SGRAPH :
		call_sgraph_proc (call_simple_fileselector, NULL);
		break;
	    case SFS_STORE :
		current_procedure (current_filename);
		break;
            default: break;
	}
}


static	int	created = FALSE;


void	init_simple_fileselector (void)
{
	if( !created ) {
		created = TRUE;
		fileselector = fls_create();
	}

}


void		show_simple_selection_subframe (SFS_load_or_store load_or_store, void (*proc) ())
{
	init_simple_fileselector ();
	
	lock_user_interface ();
	
	if (load_or_store == SFS_LOAD || load_or_store == SFS_LOAD_SGRAPH) {
		fls_set_info( fileselector, " << LOAD >>" );
		fileselect( fileselector, 0, local_load );
	} else {
		fls_set_info( fileselector, " << STORE >>");
		fileselect( fileselector, 0, local_store );
	}
	current_procedure = proc;
	current_load_or_store = load_or_store;
}
