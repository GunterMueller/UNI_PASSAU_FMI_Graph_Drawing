/***************************************************************/
/*                                                             */
/*  filename:  miscmath_utility.c                              */
/*  filetype:  C-Code                                          */
/*  author:    Christian Ramsauer                              */
/*  date:      31.01.1994                                      */
/*                                                             */
/***************************************************************/
/*                                                             */
/*  description:                                               */
/*    This file contains several mathematical and geometric    */
/*    functions.                                               */
/*                                                             */
/*  imports:                                                   */
/*    -                                                        */
/*                                                             */
/*  exports:                                                   */
/*    double edgelength();                                     */
/*    double trianglesurface();                                */
/*    void triangleangles();                                   */
/*    double circumcircleradius();                             */
/*    double angle_ccw();                                      */
/*    double angle_();                                         */
/*    int left_or_right_turn();                                */
/*    int test_for_an_intersection();                          */
/*                                                             */
/***************************************************************/


/***************************************************************/
/*                                                             */
/*                      include section                        */
/*                                                             */
/***************************************************************/

#include <math.h>
#include <std.h>
#include <sgraph.h>
#include <slist.h>
#include <graphed.h>
#include "globaldefinitions.h"
#include "miscmath_utility.h"

/***************************************************************/
/*                                                             */
/*               calculating the edgelength                    */  
/*                                                             */
/***************************************************************/

double edgelength(int x1, int y1, int x2, int y2)
{
  double value;

  value=sqrt(((double)(x1)-(double)(x2))*
             ((double)(x1)-(double)(x2))+
             ((double)(y1)-(double)(y2))*
             ((double)(y1)-(double)(y2)));
  return(value);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*            calculating the surface of a triangle            */  
/*                                                             */
/***************************************************************/

double trianglesurface(int x1, int y1, int x2, int y2, int x3, int y3)
{
  double edgelength_a,edgelength_b,edgelength_c,help,surface;
  
  edgelength_a=edgelength(x1,y1,x2,y2);
  edgelength_b=edgelength(x2,y2,x3,y3);
  edgelength_c=edgelength(x3,y3,x1,y1);
  help=(edgelength_a+edgelength_b+edgelength_c)/2.0;
  surface=sqrt(abs(help*
                   (help-edgelength_a)*
                   (help-edgelength_b)*
                   (help-edgelength_c)));
  return(surface);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*             calculating the angles of a triangle            */  
/*                                                             */
/***************************************************************/

void triangleangles(int xa, int ya, int xb, int yb, int xc, int yc, double *alpha, double *beta, double *gamma)
{
  double a,b,c;

  a=edgelength(xb,yb,xc,yc);
  b=edgelength(xa,ya,xc,yc);
  c=edgelength(xa,ya,xb,yb);
  *alpha=(double)acos((b*b+c*c-a*a)/(2.0*b*c));
  *beta=(double)acos((a*a+c*c-b*b)/(2.0*a*c));
  *gamma=(double)acos((a*a+b*b-c*c)/(2.0*a*b)); 
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*   calculating the radius of the circumcircle of a triangle  */  
/*                                                             */
/***************************************************************/

double circumcircleradius(int xa, int ya, int xb, int yb, int xc, int yc)
{
  double ur,a,b,c;

  a=edgelength(xb,yb,xc,yc);
  b=edgelength(xa,ya,xc,yc);
  c=edgelength(xa,ya,xb,yb);
  ur=0.25*a*b*c/trianglesurface(xa,ya,xb,yb,xc,yc);   
  
  return(ur);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*   calculating the angle between the edges (A,B) and (B,C)   */
/*   in counterclockwise direction.                            */
/*                                                             */
/***************************************************************/

double angle_ccw(Snode A, Snode B, Snode C)
{
  double a1,a2,b1,b2,angle;

  a1=(double)(C->x-B->x);
  a2=(double)(C->y-B->y);
  b1=(double)(A->x-B->x);
  b2=(double)(A->y-B->y);
  angle=acos((a1*b1+a2*b2)/sqrt(a1*a1+a2*a2)/sqrt(b1*b1+b2*b2));
  if (left_or_right_turn(A->x,A->y,B->x,B->y,C->x,C->y)==1)
    angle=2.0*acos(-1.0)-angle;

  return(angle);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*   calculating the angle between the edges (A,B) and (B,C).  */
/*                                                             */
/***************************************************************/

double angle_(Snode A, Snode B, Snode C)
{
  double a1,a2,b1,b2,angle;

  a1=(double)(C->x-B->x);
  a2=(double)(C->y-B->y);
  b1=(double)(A->x-B->x);
  b2=(double)(A->y-B->y);
  angle=acos((a1*b1+a2*b2)/sqrt(a1*a1+a2*a2)/sqrt(b1*b1+b2*b2));
/*  if (left_or_right_turn(A->x,A->y,B->x,B->y,C->x,C->y)==1)
    angle=2.0*acos(-1.0)-angle;*/

  return(angle);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*  returns, whether three points (x1,y1), (x2,y2) and (x3,y3) */
/*  for a left- or rightturn.                                  */
/*     1 = leftturn                                            */
/*     0 = points lie on a common line                         */
/*    -1 = rightturn                                           */
/*                                                             */
/***************************************************************/

int left_or_right_turn(int x1, int y1, int x2, int y2, int x3, int y3)
{
  int det;
  det= - x1*y2 + x2*y1 - x2*y3 + x3*y2 - x3*y1 + x1*y3;
  if (det==0) return(0);
  if (det>0) return(1);
  return(-1);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*  the function tests the two edges (a,b) and (c,d) for an    */
/*  intersection. there are 5 possible cases:                  */
/*                                                             */
/*    0: no intersection                                       */
/*    1: intersection is a common starting or ending point     */
/*    2: normal intersection                                   */
/*    3: intersection is a single starting or ending point     */
/*    4: the edges overlap                                     */
/*                                                             */
/***************************************************************/

int test_for_an_intersection(int a_x, int a_y, int b_x, int b_y, int c_x, int c_y, int d_x, int d_y)
{
  double r,s;

  if (!((a_x==b_x && a_y==b_y) || (c_x==d_x && c_y==d_y)))
  {
    if ((b_x-a_x)*(d_y-c_y) - (b_y-a_y)*(d_x-c_x)==0)
    {
      if ((a_y-c_y)*(b_x-a_x) + (c_x-a_x)*(b_y-a_y)==0)
      {
        if (a_y==c_y)
        {
          if (maximum(a_x,b_x)>=minimum(c_x,d_x)
              &&
              minimum(a_x,b_x)<=maximum(c_x,d_x))
          {
            if (maximum(a_x,b_x)==minimum(c_x,d_x)
                ||
                minimum(a_x,b_x)==maximum(c_x,d_x))
              return(1);
            else
              return(4);
          }
          return(0);
        }
        else
        {
          if (a_x==b_x)
          {
            if (maximum(a_y,b_y)>=minimum(c_y,d_y)
                &&
                minimum(a_y,b_y)<=maximum(c_y,d_y))
            {
              if (maximum(a_y,b_y)==minimum(c_y,d_y)
                  ||
                  minimum(a_y,b_y)==maximum(c_y,d_y))
                return(1);
              else
                return(4);
            }
            return(0);
          }
          else
          {
            if (((a_x==c_x && a_y==c_y) ||
                 (a_x==d_x && a_y==d_y) ||
                 (b_x==c_x && b_y==c_y) ||
                 (b_x==d_x && b_y==d_y)) &&
                (maximum(a_x,b_x)<=minimum(c_x,d_x)
                 ||
                 minimum(a_x,b_x)>=maximum(c_x,d_x)))
             return(1);
           if (minimum(a_x,b_x)<maximum(c_x,d_x)
              &&
              maximum(a_x,b_x)>minimum(c_x,d_x))
            return(4);
          }
        }
      }
      return(0);
    }
    else
    {
      s=((double)((a_y-c_y)*(b_x-a_x) + (c_x-a_x)*(b_y-a_y))) /
        ((double)((b_x-a_x)*(d_y-c_y) - (b_y-a_y)*(d_x-c_x)));
      if (b_x-a_x!=0)
        r=((double)(c_x - a_x + s*(d_x-c_x))) / ((double)(b_x-a_x));
      else
        r=((double)(c_y - a_y + s*(d_y-c_y))) / ((double)(b_y-a_y));
      if ((r==0 || r==1) && (s==0 || s==1))
        return(1);
      else
        if ((s==1 && 0<r && r<1)
            ||
            (s==0 && 0<r && r<1)
            ||
            (r==1 && 0<s && s<1)
            ||
            (r==0 && 0<s && s<1))
          return(3);
        else
          if (0<r && r<1 && 0<s && s<1)
            return(2);
          else
            return(0);
    }
  }
  else
    if (a_x==b_x && a_y==b_y
        &&
        !(c_x==d_x && c_y==d_y))
    {
      if(d_x==c_x)
      {
        if (a_x==c_x && (c_y==a_y || d_y==a_y))
          return(1);
        else
          if (a_x==c_x && minimum(c_y,d_y)<=a_y && maximum(c_y,d_y)>=a_y)
            return(2);
          else
            return(0);
      }
      else
        if (c_y==d_y)
        {
          if (a_y==c_y && (c_x==a_x || d_x==a_x))
            return(1);
          else
            if (a_y==c_y && minimum(c_x,d_x)<=a_x && maximum(c_x,d_x)>=a_x)
              return(2);
            else
              return(0);
         }
      r=((double)(a_x-c_x)) / ((double)(d_x-c_x));
      s=((double)(a_y-c_y)) / ((double)(d_y-c_y));
      if(r==s)
      {
        if(r==0 || r==1)
          return(1);
        else if(0<r && r<1)
          return(2);
      }
      return(0);
    }
    else
      if(!(a_x==b_x && a_y==b_y) && c_x==d_x && c_y==d_y)
      {
        if(b_x==a_x)
        {
          if(a_x==c_x && (a_y==c_y || b_y==c_y))
            return(1);
          else
            if(a_x==c_x && minimum(a_y,b_y)<=c_y && maximum(a_y,b_y)>=c_y)
              return(2);
            else
              return(0);
        }
        else
          if(a_y==b_y)
          {
            if(a_y==c_y && (a_x==c_x || b_x==c_x))
              return(1);
            else
              if(a_y==c_y && minimum(a_x,b_x)<=c_x && maximum(a_x,b_x)>=c_x)
                return(2);
              else
                return(0);
          }
        r=((double)(c_x-a_x)) / ((double)(b_x-a_x));
        s=((double)(c_y-a_y)) / ((double)(b_y-a_y));
        if(r==s)
        {
          if(r==0 || r==1)
            return(1);
          else
            if(0<r && r<1)
              return(2);
        }
        return(0);
      }
  return(0);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*                end of miscmath_utility.c                    */
/*                                                             */
/***************************************************************/
