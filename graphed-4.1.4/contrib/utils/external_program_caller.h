typedef struct graphed_cep_info {
	char	*programname;
	char	*write_file_name;
	char	*read_file_name;
	char	*write_file_name_switch;
	char	*read_file_name_switch;

	int	(*write_file_proc)();
	int	(*read_file_proc)();

	int	remove_files;
}
	Graphed_cep_info;

extern	int			graphed_standard_write_file (char *filename);
extern	int			graphed_standard_read_file (char *filename);
extern	int			run_external_program (Graphed_cep_info *cep_info);
extern	Graphed_cep_info	new_graphed_cep_info (void);
