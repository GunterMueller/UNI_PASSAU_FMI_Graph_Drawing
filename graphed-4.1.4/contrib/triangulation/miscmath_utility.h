/***************************************************************/
/*                                                             */
/*  filename:  miscmath_utility.h                              */
/*  filetype:  Header-File                                     */
/*  author:    Christian Ramsauer                              */
/*  date:      31.01.1994                                      */
/*                                                             */
/***************************************************************/


extern double edgelength(int x1, int y1, int x2, int y2);
extern double trianglesurface(int x1, int y1, int x2, int y2, int x3, int y3);
extern void triangleangles(int xa, int ya, int xb, int yb, int xc, int yc, double *alpha, double *beta, double *gamma);
extern double circumcircleradius(int xa, int ya, int xb, int yb, int xc, int yc);
extern double angle_ccw(Snode A, Snode B, Snode C);
extern double angle_(Snode A, Snode B, Snode C);
extern int left_or_right_turn(int x1, int y1, int x2, int y2, int x3, int y3);
extern int test_for_an_intersection(int a_x, int a_y, int b_x, int b_y, int c_x, int c_y, int d_x, int d_y);

/***************************************************************/
