
/* ************************************************************************** */
/* **                                                                      ** */
/* ** Filename: STNUMMOD.H                                                 ** */
/* **                                                                      ** */
/* ** ******************************************************************** ** */
/* **                                                                      ** */
/* ** Description: This header-file provides the structure for the Slist,  ** */
/* **              that contains the st-numbering and the function that    ** */
/* **              returns a pointer to the st-numbering Slist             ** */
/* **                                                                      ** */
/* ** Structure: st_attr contains a field for the node and one for the     ** */
/* **            st-number of that node                                    ** */
/* **                                                                      ** */
/* ** Function: st_number(g,firstedge) computes the st-numbers for the     ** */
/* **           nodes of g and begins it's path-finding with firstedge     ** */
/* **                                                                      ** */
/* ** Date: 18.5.1994                                                      ** */
/* **                                                                      ** */
/* ************************************************************************** */
/*                                                                            */


typedef struct st_attr
{
	Snode node;
	int st_nr;
} 
*St_attr;


extern Slist st_number(Sgraph g, Sedge firstedge);

extern void free_st_number(Slist st_num);

/*                                                                            */
/* ************************************************************************** */
/* **                       END OF FILE: STNUMMOD.H                        ** */
/* ************************************************************************** */

