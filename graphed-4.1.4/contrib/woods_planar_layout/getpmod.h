
/* ************************************************************************** */
/* **                                                                      ** */
/* ** Filename: GETPMOD.H                                                  ** */
/* **                                                                      ** */
/* ** ******************************************************************** ** */
/* **                                                                      ** */
/* ** Description: This file is included by STNUMMOD.C to find the paths   ** */
/* **              for computing the st-numbers                            ** */
/* **                                                                      ** */
/* ** Functions: mark_old_node(n) marks the node n as old node             ** */
/* **                             (marked = 1)                             ** */
/* **            mark_old_edge(e) marks the edge e as old edge             ** */
/* **                             (is_tree_edge = 1 and is_back_edge = 1)  ** */
/* **            get_path(n) first looks for a path via tree-edges and     ** */
/* **                        returns a Slist that contains the path. If    ** */
/* **                        no such path does exist it loks for a path    ** */
/* **                        via one back-edge and than following          ** */
/* **                        tree-edges                                    ** */
/* **                                                                      ** */
/* ** Date: 18.5.1994                                                      ** */
/* **                                                                      ** */
/* ************************************************************************** */
/*                                                                            */


extern void mark_old_node(Snode n);

extern void mark_old_edge(Sedge e);

extern Slist get_path(Snode v);


/*                                                                            */
/* ************************************************************************** */
/* **                       END OF FILE: GETPMOD.H                         ** */
/* ************************************************************************** */

