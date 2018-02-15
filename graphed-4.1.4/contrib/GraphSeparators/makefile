# (C) Universitaet Passau, Michael Himsolt 1986-1991
#########################################################################
#									#
#			    GRAPHED MAKEFILE				#
#									#
#########################################################################


#
# Directories
#
GRAPHEDDIR = ../..

# Macros as supplied by Harald Lauer
GS_PATH     =.
GS_MISC     =Separator
GS_BF       =BruteForce
GS_GREEDY   =Greedy
GS_KL       =Kernighan-Lin
GS_PLAISTED =Plaisted
GS_ID       =IncreaseDegree
GS_FM       =Fiduccia-Mattheyses

V_MACRO      = SUN_VERSION

#
# Sourcefiles
#
HSOURCES = Interface.h                   \
         SUNInterface.h                  \
         GraphSeparators_export.h        \
         CheckConsistency.h              \
         GraphGenerator.h                \
         ${GS_MISC}/Separator.h          \
         ${GS_MISC}/AttrsQueue.h         \
         ${GS_MISC}/AttrsStack.h         \
         ${GS_BF}/BruteForce.h           \
         ${GS_GREEDY}/Greedy.h           \
         ${GS_KL}/Kernighan-Lin.h        \
         ${GS_FM}/Fiduccia-Mattheyses.h  \
         ${GS_PLAISTED}/Plaisted.h       \
         ${GS_PLAISTED}/MaxFlow.h        \
         ${GS_ID}/IncreaseDegree.h       \
         ${GS_MISC}/GS-Debug.h		 \
         ${GS_MISC}/FlowMatch.h 

#         NeXTInterface.h


CSOURCES = Interface.c                   \
         SUNInterface.c                  \
         CheckConsistency.c              \
         GraphGenerator.c                \
         ${GS_MISC}/Separator.c          \
         ${GS_MISC}/AttrsQueue.c         \
         ${GS_MISC}/AttrsStack.c         \
         ${GS_BF}/BruteForce.c           \
         ${GS_GREEDY}/Naive.c            \
         ${GS_GREEDY}/Greedy.c           \
         ${GS_KL}/Kernighan-Lin.c        \
         ${GS_KL}/KL-Exchange.c          \
         ${GS_FM}/Fiduccia-Mattheyses.c  \
         ${GS_PLAISTED}/Plaisted.c       \
         ${GS_PLAISTED}/PlaistedGraph.c  \
         ${GS_PLAISTED}/PlaistedStar.c   \
         ${GS_PLAISTED}/MaxFlow.c        \
         ${GS_ID}/IncreaseDegree.c       \
         ${GS_ID}/Matching.c             \
         ${GS_ID}/ID-Algorithms.c        \
         ${GS_MISC}/GS-Debug.c

#         NeXTInterface.c
#         ${GS_MISC}/FlowMatch.c


MYFILES = $(HSOURCES) $(CSOURCES)

MODULE=GraphSeparators
include ../makefile.template
include $(GRAPHEDDIR)/makefile.template

CCINCLUDE  += -I${GS_PATH} -I${GS_MISC} -I${GS_BF} -I${GS_KL} -I${GS_GREEDY} -I${GS_PLAISTED} -I${GS_ID} -I${GS_FM}
CFLAGS += -D${V_MACRO} -DANIMATION_ON -DANSI_HEADERS_ON -DDEBUG_OFF

include makedepend
