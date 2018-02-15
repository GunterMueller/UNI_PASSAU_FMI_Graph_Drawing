#include "graph.h"
#include "std.h"

Global	File_attributes	new_file_attributes (void)
{
	File_attributes	a;

	a = (File_attributes) malloc (sizeof(struct file_attributes));
	return a;
}

Global	void	free_file_attributes (File_attributes list)
{
	File_attributes a, previous;

	a = list;
	while (a != NULL) {
		switch (a->kind) {
		    case FILE_KEY:
/* file_attributes.c:21: incompatible type for argument 1 of `free'
			free (a->value.key); */
			free (a->value.key.name);
			free_file_attributes (a->value.key.values);
			break;
		    case FILE_NUMBER:
			break;
		    case FILE_FLOATNUMBER:
			break;
		    case FILE_STRING:
			free (a->value.string);
			break;
		    case FILE_LIST:
			free_file_attributes (a->value.list);
			break;
		}

		previous = a;
		a = a->next;
		free (previous);
	}
}

Global	void	print_file_attributes (FILE *file, File_attributes list)
{
	File_attributes a;

	a = list;
	while (a != NULL) {
		switch (a->kind) {
		    case FILE_KEY:
			fprintf (file, "%s ", a->value.key.name);
			print_file_attributes (file, a->value.key.values);
			fprintf (file, "\n");
			break;
		    case FILE_NUMBER:
			fprintf (file, "%d ", a->value.number);
			break;
		    case FILE_FLOATNUMBER:
			fprintf (file, "%f ", a->value.floatnumber);
			break;
		    case FILE_STRING:
			fprintf (file, "\"%s\" ", a->value.string);
			break;
		    case FILE_LIST:
			fprintf (file, "[");
			print_file_attributes (file, a->value.list);
			fprintf (file, "]");
			break;
		}
		a = a->next;
	}
}
