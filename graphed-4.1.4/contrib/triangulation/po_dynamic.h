/***************************************************************/
/*                                                             */
/*  filename:  po_dynam.h                                      */
/*  filetype:  Header-File                                     */
/*  author:    Christian Ramsauer                              */
/*  date:      31.01.1994                                      */
/*                                                             */
/***************************************************************/

typedef enum {
  maxlength,
  minlength,
  minmaxlength,
  maxminlength,
  balancedlength,
  minmaxangle,
  maxminangle,
  balancedangle,
  minmaxsurface,
  maxminsurface,
  balancedsurface,
  minmaxcircumcircle,
  maxmincircumcircle,
  balancedcircumcircle,
  bushy,
  thin,
  minmaxexz,
  maxmaxexz,
  mindualheight,
  getpolygonsurface
} polygon_triangulation_dynamic;

extern double standarddynamic_polygontriangulation(Sgraph inputgraph, polygon_triangulation_dynamic typ);

extern int modifieddynamic_polygontriangulation(Sgraph inputgraph, polygon_triangulation_dynamic typ);
extern void calculating_the_values_for_the_subpolygons_modified(polygon_triangulation_dynamic typ, int **V, int **C, double **Q, int n);


/***************************************************************/

