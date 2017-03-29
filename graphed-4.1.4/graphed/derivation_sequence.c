/* (C) Universitaet Passau 1986-1994 */
/* GraphEd Source, 1986-1994 by Michael Himsolt	*/

#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include "user.h"
#include <ctype.h>

#include <sgraph/std.h>
#include <sgraph/slist.h>
#include <sgraph/sgraph.h>

#include "derivation.h"

/*************************************************************************

  This file contains procedures/functions to manage a derivation
  sequence.  A derivation sequence consists of three basic data
  structures :

  * Derivation_sequence

  This is the derivation seqence : graph grammars to use, (label
  of) startnode and list of derivations.

  * Gragra_textual_apply_form

  A derivation : (label of) production, (label of) node and
  history of the node

  * Gragra_textual_history_form

  A node history element : a (label of a) production and the
  (label of the) node in its right side which is replaced next.

  Note that *no* pointers into GraphEd data structures are used here.
  Derivation sequences can be stored to files and re-read.

*************************************************************************/

/************************************************************************/
/*									*/
/*			Gragra textual history form			*/
/*									*/
/************************************************************************/

Gragra_textual_history_form	ggthf_create (void)
{
	Gragra_textual_history_form ggthf;

	ggthf = (Gragra_textual_history_form) malloc (
		sizeof (struct gragra_textual_history_form));
	ggthf->node_of_right_side = NULL;
	ggthf->production = NULL;

	return ggthf;
}

void				ggthf_delete (Gragra_textual_history_form ggthf)
{
}


void	ggthf_set_right_side (Gragra_textual_history_form ggthf, char *label)
{
	ggthf->node_of_right_side = strsave (label);
}

void	ggthf_set_production (Gragra_textual_history_form ggthf, char *label)
{
	ggthf->production = strsave (label);
}


char	*ggthf_get_right_side (Gragra_textual_history_form ggthf)
{
	return ggthf->node_of_right_side;
}

char	*ggthf_get_production (Gragra_textual_history_form ggthf)
{
	return ggthf->production;
}


/************************************************************************/
/*									*/
/*			Gragra textual apply form			*/
/*									*/
/************************************************************************/


Gragra_textual_apply_form ggtaf_create (void)
{
	Gragra_textual_apply_form ggtaf;

	ggtaf = (Gragra_textual_apply_form) malloc (
		sizeof (struct gragra_textual_apply_form));
	ggtaf->number = 0;
	ggtaf->production = NULL;
	ggtaf->node = NULL;
	ggtaf->node_history = empty_slist;

	return ggtaf;
}


void	ggtaf_delete (Gragra_textual_apply_form ggtaf)
{
	;
}


void	ggtaf_set_number (Gragra_textual_apply_form ggtaf, int number)
{
	ggtaf->number = number;
}

void	ggtaf_set_production (Gragra_textual_apply_form ggtaf, char *production)
{
	ggtaf->production = strsave (production);
}

void	ggtaf_set_node (Gragra_textual_apply_form ggtaf, char *node)
{
	ggtaf->node = strsave (node);
}


void	ggtaf_add_node_history (Gragra_textual_apply_form ggtaf, char *right_side, char *production)
{
	Gragra_textual_history_form ggthf;

	ggthf = ggthf_create();
	ggthf_set_right_side (ggthf, right_side);
	ggthf_set_production (ggthf, production);

	ggtaf->node_history = add_to_slist (ggtaf->node_history,
		make_attr (ATTR_DATA, (char *)ggthf));
}


int	ggtaf_get_number (Gragra_textual_apply_form ggtaf)
{
	return ggtaf->number;
}

char	*ggtaf_get_production (Gragra_textual_apply_form ggtaf)
{
	return ggtaf->production;
}

char	*ggtaf_get_node (Gragra_textual_apply_form ggtaf)
{
	return ggtaf->node;
}

Slist	ggtaf_get_node_history (Gragra_textual_apply_form ggtaf)
{
	return ggtaf->node_history;
}


/************************************************************************/
/*									*/
/*			Derivation sequence				*/
/*									*/
/************************************************************************/

Derivation_sequence	ds_create (void)
{
	Derivation_sequence sequence;
	
	sequence = (Derivation_sequence) malloc (
		sizeof (struct derivation_sequence));
	sequence->files       = empty_slist;
	sequence->startnode   = NULL;
	sequence->apply_forms = empty_slist;

	return sequence;
}


void	ds_delete (Derivation_sequence sequence)
{
}


void	ds_add_file (Derivation_sequence sequence, char *filename)
{
	sequence->files = add_to_slist (
		sequence->files,
		make_attr (ATTR_DATA, strsave(filename)));
}


void	ds_set_startnode (Derivation_sequence sequence, char *label)
{
	sequence->startnode = strsave (label);
}


void	ds_add_apply_form (Derivation_sequence sequence, Gragra_textual_apply_form apply_form)
{
	sequence->apply_forms = add_to_slist (
		sequence->apply_forms,
		make_attr (ATTR_DATA, (char *)apply_form));
}


Slist	ds_get_files (Derivation_sequence sequence)
{
	return sequence->files;
}

char	*ds_get_startnode (Derivation_sequence sequence)
{
	return sequence->startnode;
}

Slist	ds_get_apply_forms (Derivation_sequence sequence)
{
	return sequence->apply_forms;
}
/************************************************************************/
/*									*/
/*		Print derivation sequence				*/
/*									*/
/************************************************************************/


void			ds_print (FILE *output, Derivation_sequence sequence)
{
	char				*file;
	Gragra_textual_apply_form	ggtaf;
	Gragra_textual_history_form	ggthf;
	int				count = 0;

	fprintf (output, "DERIVATION_SEQUENCE\n");

	for_ds_files (sequence, file) {
		fprintf (output, "UseGraGra: \"%s\"\n", file);
	} end_for_ds_files (sequence, file);

	fprintf (output, "Startnode: \"%s\"\n", ds_get_startnode (sequence));

	for_ds_apply_forms (sequence, ggtaf) {

		fprintf (output,
			"%d Apply:\n  Node: \"%s\"\n  Production: \"%s\"\n",
			count++,
			ggtaf_get_node (ggtaf),
			ggtaf_get_production (ggtaf));

		for_ggtaf_node_history (ggtaf, ggthf) {
			fprintf (output,
				"  [ Node: \"%s\" , Production: \"%s\" ]\n",
				ggthf_get_right_side(ggthf),
				ggthf_get_production(ggthf));
		} end_for_ggtaf_node_history (ggtaf, ggthf);

	} end_for_ds_apply_forms (sequence, ggtaf);

	fprintf (output, "END\n");
}


#include <xview/xview.h>
#include <graphed/simple_fs.h>

void	ds_store (char *filename)
{
	FILE	*file;
	Graph	graph;

	if ((file = fopen(filename, "w")) != (FILE *)NULL) {
		graph = get_picked_graph ();
		if (graph != empty_graph && graph->derivation_history != empty_sgraph) {
			/* ds_print (file, graph->derivation_history); */
		} else {
			error ("Graph has no derivation sequence\n");
		}
		fclose (file);

	} else {

		error ("Cannot open file %s for writing\n", filename);

	}
}


void menu_store_derivation_sequence (Menu menu, Menu_item menu_item)
{
	Graph	graph;

	graph = get_picked_graph ();
	if (graph != empty_graph && graph->derivation_history != empty_sgraph) {
		show_simple_selection_subframe (SFS_STORE, ds_store);
	} else {
		error ("Picked graph has no derivation tree\n");
	}
}
