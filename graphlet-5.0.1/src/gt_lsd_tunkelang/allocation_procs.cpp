/* This software is distributed under the Lesser General Public License */
/* These are the functions for allocation and deallocation                  */
#include <malloc.h>

#include "glob_var_for_algo.h"


int **integer_matrix(int nrl, int nrh, int ncl, int nch)
{
    int i,**m;

    m=(int **)malloc((unsigned) (nrh-nrl+1)*sizeof(int*));
    if (!m) 
    {
	/* pri ntf("Allocation failure in integer_matrix()"); */
	no_memory=1;
    }

    m -= nrl;

    for(i=nrl;i<=nrh;i++) 
    {
	m[i]=(int *)malloc((unsigned) (nch-ncl+1)*sizeof(int));
	if (!m[i]) 
	{
	    /* pr intf("Allocation failure in integer_matrix()");*/
	    no_memory=1;
	}
	m[i] -= ncl;
    }
    return (m);
}

struct kante ***matrix_of_pointer_to_edgelists(int nrl,int nrh, int ncl, int nch)
{
    int i;
    struct kante ***matrix;

    matrix=(struct kante ***)malloc((unsigned) (nrh-nrl+1)*sizeof(struct kante **));
    if(!matrix) 
    {
	/*pr intf("Allocation failure in matrix_of_pointer_to edgelists()\n");*/
        no_memory=1;
    }

    matrix-=nrl;

    for(i=nrl;i<=nrh;i++)
    {
        matrix[i]=(struct kante**)malloc((unsigned)(nch-ncl+1)*sizeof(struct kante *));
        if(!matrix[i]) 
        {
	    /* pri ntf("Allocation failure in matrix_of_pointer_to edgelists()\n");*/
	    no_memory=1;
        }
        matrix[i]-=ncl;
    }
    return(matrix);
}



struct nachfolger  **vector_of_adjacency_lists(int nl, int nh)
{
    struct nachfolger **vector;

    vector=(struct nachfolger **)malloc((unsigned) (nh-nl+1)*sizeof(struct nachfolger *));
    if(!vector) 
    {
        /*pri ntf("Allocation failure in vector_of_adjacency_lists()\n");*/
        no_memory=1;
    }
    return(vector-nl);
}


Snode *vector_of_snodes(int nl, int nh)
{
    Snode *vector;
     
    vector=(Snode *)malloc((unsigned)(nh-nl+1)*sizeof(Snode));
    if(!vector) 
    {
	/*pri ntf("Allocation failure in vector_of_snodes()\n");*/
	no_memory=1;
    }
    return(vector-nl);
}


int *integer_vector(int nl, int nh)
{
    int *v;

    v=(int *)malloc((unsigned) (nh-nl+1)*sizeof(int));
    if (!v) 
    {
	/*pri ntf("Allocation failure in integer_vector()\n");*/
	no_memory=1;
    }
    return (v-nl);
}

void free_vector_of_adjacency_lists(struct nachfolger **vector, int nl)
{
#if __SUNPRO_CC == 0x401
    free((char*) (vector+nl));
#else
    free(vector+nl);
#endif
}

void free_vector_of_snodes(Snode *vector, int nl)
{
#if __SUNPRO_CC == 0x401
    free((char*) (vector+nl));
#else
    free(vector+nl);
#endif
}


void free_integer_vector(int *v,int nl)
{
    free((char*) (v+nl));
}

void free_integer_matrix(int **m, int nrl, int nrh, int ncl)
{
    int i;

    for(i=nrh;i>=nrl;i--) free((char*) (m[i]+ncl));
#if __SUNPRO_CC == 0x401
    free((char*) (m+nrl));
#else
    free(m+nrl);
#endif
}

void free_matrix_of_pointer_to_edgelists(struct kante ***m, int nrl, int nrh, int ncl)
{
    int i;

    for(i=nrh;i>=nrl;i--) {
#if __SUNPRO_CC == 0x401
	free((char*) (m[i]+ncl));
#else
	free(m[i]+ncl);
#endif
    }
#if __SUNPRO_CC == 0x401
    free((char*) (m+nrl));
#else
    free(m+nrl);
#endif
}
