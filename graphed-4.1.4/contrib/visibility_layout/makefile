# (C) Universitaet Passau, Michael Himsolt 1986-1993
#########################################################################
#									#
#			    GRAPHED makeFILE				#
#									#
#########################################################################


#
# Directories
#
GRAPHEDDIR = ../..
#
# Sourcefiles
#
CSOURCES = tarjancall.c subwinmod.c tarjanmod.c dualmod.c listen.c stnumber.c initmod.c tamascall.c tamasmod.c cylicall.c blockmod.c cylimod.c wismathcall.c wismathmod.c ottenmod.c ottencall.c lfacemod.c
HSOURCES = $(MODULE)_export.h tarjanmod.h dualmod.h listen.h blockmod.h 


MYFILES = $(HSOURCES) $(CSOURCES)


include ../makefile.template
include $(GRAPHEDDIR)/makefile.template



it :	$(MODULE).o

include makedepend
