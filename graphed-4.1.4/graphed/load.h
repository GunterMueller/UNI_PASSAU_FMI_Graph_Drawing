/* (C) Universitaet Passau 1986-1994 */
/* GraphEd Source, 1986-1994 by Michael Himsolt	*/
#ifndef LOAD_HEADER
#define	LOAD_HEADER

typedef	enum {
	LOAD_GRAPH          = 1,
	LOAD_GRAPHS         = LOAD_GRAPH << 1,
	LOAD_INITIALIZATION = LOAD_GRAPHS << 1,
	LOAD_DERIVATION     = LOAD_INITIALIZATION << 1
}
	Load_filetype;

#define LOAD_NOTHING		((Load_filetype)0)
#define LOAD_ANY_GRAPH_FILE	(LOAD_GRAPH | LOAD_GRAPHS)
#define LOAD_ANY_FILE		(LOAD_GRAPH | LOAD_GRAPHS | LOAD_DERIVATION)

extern	Load_filetype	filetype_last_loaded;

extern	Load_filetype	load_from_file (int buffer, char *filename, Load_filetype filetype);
extern	void		set_lex_input (FILE *file);

extern	int	lex_input_file_linenumber;
extern	int	overwrite_state;

extern	int	load_buffer;

/*yylex und yyparse geloescht*/

#endif
