/***************************************************************/
/*                                                             */
/*  filename:  globdefs.h                                      */
/*  filetype:  Header-File                                     */
/*  author:    Christian Ramsauer                              */
/*  date:      31.01.1994                                      */
/*                                                             */
/***************************************************************/


/* Label for an edge of the triangulation */
#define TRIANGULATION_EDGELABEL "@"


/* Label for a marked edge */
#define EDGEMARKER "#"


/* Label for a marked node of Type 1 */
#define NODEMARKER_1 "@"

/* Label for a marked node of Type 2 */
#define NODEMARKER_2 "#"




/* defines whether there is an Output of the Charakteristics or not */
#define OUTPUT_INFORMATION 1


/* defines wherter the inputgraph is tested for correctness or not */
/* Handle this Parameter with care                                 */
#define CHECK_THE_INPUTGRAPH 1

 
/* Defines infinity and zero */
#define POSITIV_DOUBLE_INFINITLY 10000000000.0
#define NEGATIV_DOUBLE_INFINITLY -10000000000.0
#define NULL_DOUBLE 0.0


/* Define for an Angle of 60 degree */
#define DEGREE_60 acos(1)/3.0


/***************************************************************/
