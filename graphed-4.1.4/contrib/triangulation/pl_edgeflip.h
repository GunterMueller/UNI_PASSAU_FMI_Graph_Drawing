/***************************************************************/
/*                                                             */
/*  filename:  pl_edgeflip.c                                   */
/*  filetype:  Header-File                                     */
/*  author:    Christian Ramsauer                              */
/*  date:      31.01.1994                                      */
/*                                                             */
/***************************************************************/


typedef enum {
  ef_minlength,
  ef_minmaxdegree,
  ef_mostlyequalsurface,
  ef_minmaxangle
} edgeflip_criterion;

extern void plan_edgeflip(Sgraph inputgraph, edgeflip_criterion ef_criterion);

/***************************************************************/
