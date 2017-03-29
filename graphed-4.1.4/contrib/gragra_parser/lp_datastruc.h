/************************* Grundlegende exportierte Funktionen **************************/

extern	char*			mem_copy_string_wa(char *string);
extern	void			free_copy_iso_in_lams_table(PE_set pe_set);

/******************************** LP_Parsing_element ************************************/
extern	LP_Parsing_element	create_LP_parsing_element(void);

extern	void			LP_parsing_element_SET_DERIVATIONS(LP_Parsing_element pe, LP_Derivation_list derivations);

extern	LP_Derivation_list	LP_parsing_element_GET_DERIVATIONS(LP_Parsing_element pe);
extern	void			free_lp_parsing_element_with_lower_part(LP_Parsing_element pe);


/****************************** LP_Parsing_element_list *********************************/
extern	LP_Parsing_element_list	create_lp_pe_list(void);

extern	void			lp_pe_list_SET_PE(LP_Parsing_element_list pe_list, LP_Parsing_element pe);
extern	void			LP_pe_list_SET_PROD_ISO(LP_Parsing_element_list pe_list, Snode node);

extern	LP_Parsing_element	lp_pe_list_GET_PE(LP_Parsing_element_list pe_list);
extern	Snode			LP_pe_list_GET_PROD_ISO(LP_Parsing_element_list pe_list);

extern	LP_Parsing_element_list	create_lp_pe_list_with_pe(LP_Parsing_element pe);
extern	LP_Parsing_element_list	add_pe_list_to_pe_list(LP_Parsing_element_list old_list, LP_Parsing_element_list new);
extern	LP_Parsing_element_list	add_pe_to_pe_list(LP_Parsing_element_list old_list, LP_Parsing_element new);

extern	void			free_lp_pe_list(LP_Parsing_element_list list);
extern	void			free_lp_pe_list_with_pe(LP_Parsing_element_list list);
extern	void			free_lp_pe_list_with_lower_part(LP_Parsing_element_list list);

/*********************************** LP_Derivation **************************************/
extern	LP_Derivation		create_lp_derivation(void);

extern	void			lp_derivation_SET_PARSING_ELEMENTS(LP_Derivation der, LP_Parsing_element_list pe_list);
extern	void			lp_derivation_SET_USED_PROD(LP_Derivation der, Sprod prod);

extern	LP_Parsing_element_list	lp_derivation_GET_PARSING_ELEMENTS(LP_Derivation der);
extern	Sprod			lp_derivation_GET_USED_PROD(LP_Derivation der);

extern	LP_Derivation		create_lp_derivation_with_pe_list(LP_Parsing_element_list list);

extern	void			free_lp_derivation_with_lower_part(LP_Derivation der);

extern	LP_Derivation		create_lp_derivation_of(PE_set parsing_elem_set);

/******************************** LP_Derivation_list ***********************************/
extern	LP_Derivation_list	create_lp_derivation_list(void);

extern	void			LP_der_list_SET_DERIVATION(LP_Derivation_list list, LP_Derivation der);

extern	LP_Derivation		LP_der_list_GET_DERIVATION(LP_Derivation_list list);

extern	LP_Derivation_list	create_lp_derivation_list_with_der(LP_Derivation der);

extern	void			free_lp_der_list_with_lower_part(LP_Derivation_list list);

extern	LP_Derivation_list	lp_der_list_add_list_to_list(LP_Derivation_list list, LP_Derivation_list new_elem);
extern	LP_Derivation_list	lp_der_list_add_derivation_to_list(LP_Derivation_list list, LP_Derivation new_elem);

/******************************** LP_copy_array ****************************************/
extern	LP_copy_array	create_lp_copy_array(void);
extern	LP_copy_array	append_to_lp_copy_array(LP_copy_array list, LP_copy_array new);
extern	void		free_lp_copy_array(LP_copy_array list);
extern	LP_copy_array	delete_from_copy_array(LP_copy_array array, Sprod prod, LP_Derivation der);

/******* Funktion speziell fuer isomorphe Produktionen Knoten verketten *******/

extern	void	append_node_isomorphism(PE_production head, PE_production iso_prod);
