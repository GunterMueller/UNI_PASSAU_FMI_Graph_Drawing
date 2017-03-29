/* (C) Universitaet Passau 1986-1994 */
/************************************************************************
 * File    : gfunc.h							*
 * Aufgabe : Allgemeine Graf-Funktionen					*
 *									*
 * Autor   : Torsten Bachmann						*
 * Datum   : 11.12.89							*
 ************************************************************************/

#define G_Test_Stack_Overflow(ptr) \
				if ((ptr)==NULL) \
				{	fprintf(stderr,"Speicher voll"); \
					exit(3); \
				}

int	G_knoten_anzahl (Sgraph g);
int	G_kanten_anzahl (Snode knoten);
int	G_exist_edge (Snode knoten1, Snode knoten2);
Sedge	G_has_double_edges(Sgraph g);
