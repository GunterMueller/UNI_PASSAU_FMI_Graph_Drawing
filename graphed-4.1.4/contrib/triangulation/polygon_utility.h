/***************************************************************/
/*                                                             */
/*  filename:  polygon_utility.h                               */
/*  filetype:  Header-File                                     */
/*  author:    Christian Ramsauer                              */
/*  date:      31.01.1994                                      */
/*                                                             */
/***************************************************************/


extern void polygontriangulation_info(Sgraph inputgraph);
extern int simplepolygon_test(Sgraph inputgraph);
extern void create_polygonvisibilityinformation(int **V, Snode *ND, int n);
extern void remove_outerpolygon(Sgraph inputgraph);
extern void getting_the_polygonnodes(Sgraph inputgraph, Snode *ND, int n);

/***************************************************************/
