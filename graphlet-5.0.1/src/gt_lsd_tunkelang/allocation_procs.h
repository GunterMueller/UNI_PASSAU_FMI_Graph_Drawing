/* This software is distributed under the Lesser General Public License */
/* This is the header-file for allocation and deallocation                  */


extern int               **integer_matrix(int nrl, int nrh, int ncl, int nch);

extern struct kante      ***matrix_of_pointer_to_edgelists(int nrl,int nrh, int ncl, int nch);

extern struct nachfolger **vector_of_adjacency_lists(int nl, int nh);

extern Snode             *vector_of_snodes(int nl, int nh);

extern int               *integer_vector(int nl, int nh);


extern void free_vector_of_adjacency_lists(struct nachfolger **vector, int nl);

extern void free_vector_of_snodes(Snode *vector, int nl);

extern void free_integer_vector(int *v,int nl);

extern void free_integer_matrix(int **m, int nrl, int nrh, int ncl);

extern void free_matrix_of_pointer_to_edgelists(struct kante ***m, int nrl, int nrh, int ncl);
