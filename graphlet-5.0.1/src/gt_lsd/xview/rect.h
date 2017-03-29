/* This software is distributed under the Lesser General Public License */
/*
rect.h 

taken from XView's rect.h to fake Sgraph that XView is there...
*/

#ifndef xview_rect_DEFINED
#define xview_rect_DEFINED

/* fake XView's coord */

#ifndef coord
#define coord   short
#endif

/* fake XView's Rect */

typedef struct rect {
        coord   r_left, r_top;
        short   r_width, r_height;
} Rect;

#define rect_construct(r,x,y,w,h) \
        {(r)->r_left=(x);(r)->r_top=(y);(r)->r_width=(w);(r)->r_height=(h);}

#endif /* ~xview_rect_DEFINED */
