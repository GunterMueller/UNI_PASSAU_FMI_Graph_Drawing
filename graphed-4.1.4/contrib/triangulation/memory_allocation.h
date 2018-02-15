/***************************************************************/
/*                                                             */
/*  filename:  memory_allocation.h                             */
/*  filetype:  Header-File                                     */
/*  author:    Christian Ramsauer                              */
/*  date:      31.01.1994                                      */
/*                                                             */
/***************************************************************/


extern void print_memoryerrormessage(void);                         
extern int *allocating_a_1_dimensional_array_of_typ_int(int n);      
extern int **allocating_a_2_dimensional_array_of_typ_int(int n);     
extern int ***allocating_a_3_dimensional_array_of_typ_int(int n, int m, int k);    
extern double *allocating_a_1_dimensional_array_of_typ_double(int n);
extern double **allocating_a_2_dimensional_array_of_typ_double(int n);
extern Snode *allocating_a_1_dimensional_array_of_typ_Snode(int n);  
extern Sedge *allocating_a_1_dimensional_array_of_typ_Sedge(int n);  
extern void freeing_a_1_dimensional_array_of_typ_int(int *array, int n);         
extern void freeing_a_2_dimensional_array_of_typ_int(int **array, int n);         
extern void freeing_a_3_dimensional_array_of_typ_int(int ***array, int n, int m, int k);         
extern void freeing_a_1_dimensional_array_of_typ_double(double *array, int n);      
extern void freeing_a_2_dimensional_array_of_typ_double(double **array, int n);      
extern void freeing_a_1_dimensional_array_of_typ_Snode(Snode *array, int n);       
extern void freeing_a_1_dimensional_array_of_typ_Sedge(Sedge *array, int n);       

/***************************************************************/
