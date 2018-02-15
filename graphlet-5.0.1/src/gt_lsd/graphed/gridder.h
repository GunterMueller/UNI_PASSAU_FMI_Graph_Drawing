/* This software is distributed under the Lesser General Public License */
/* Dummy-Header for LSD by Dirk Heider */

#ifndef _LSD

/*
if this dummy-header is included and _LSD is NOT defined,
then create an error to prevent illegal usage:
*/

DANGER! Including LSD-header without defining _LSD!

#endif

typedef enum {
        GRIDDER_DISTANCE_NONE,
        GRIDDER_DISTANCE_1_DEFAULT_SIZE,
        GRIDDER_DISTANCE_1_LARGEST_SIZE,
        GRIDDER_DISTANCE_15_DEFAULT_SIZE,
        GRIDDER_DISTANCE_15_LARGEST_SIZE,
        GRIDDER_DISTANCE_2_DEFAULT_SIZE,
        GRIDDER_DISTANCE_2_LARGEST_SIZE,
        GRIDDER_DISTANCE_3_DEFAULT_SIZE,
        GRIDDER_DISTANCE_3_LARGEST_SIZE,
        GRIDDER_DISTANCE_OTHER,
        NUMBER_OF_GRIDDER_DISTANCES
}
        Gridder_choices;
 
typedef enum {
        GRIDDER_WIDTH, GRIDDER_HEIGHT, GRIDDER_MAX_OF_BOTH, GRIDDER_MIN_OF_BOTH
}
        Gridder_width_or_height;
 
 
typedef struct  gridder {
        Panel_item              cycle, text;
        Gridder_width_or_height kind;
}
        *Gridder;
