#ifndef schnittlselle_h
#define schnittstelle_h

#include "mystd.h"
#include "mainwindow.h"
/***************************************************************************/
/***************************************************************************/
/*									   */
/*	  S C H N I T T S T E L L E    Z U M    M E N U E  		   */
/*									   */
/*  Modul        : schnittstelle.h					   */
/*  erstellt von : Nikolas Motte                                           */
/*  erstellt am  : 10.12.1992                                              */
/*                                                                         */
/***************************************************************************/
/***************************************************************************/
/*									   */
/*		Diese Modul dient zur Integration fremder Routinen 	   */
/*		Berechnung der Baumweite, bzw. Baumzerlegung von	   */
/*			Graphen.					   */
/*									   */
/*	Um neue Algorithmen in das menue des Hauptfensters aufzunehmen	   */
/*     mussen in die unten stehende Prozedur `init_treewidth_menu()' die   */
/*     der Name der Routine, sowie die Startadresse der Routine mit	   */
/*     `add_to_treewidth_menu("NAME",Prozedur);' dem Hauptprogramm ueber-  */
/*     geben werden. Die Prozedur wird dann mit einer `Tree_width_info'-   */
/*     Strucktur aufgerufen, die alle notwendigen informationen enth"alt.  */
/*									   */
/*     Die Prozedur wird nur aufgerufen, wenn der Graph ungerichtet,       */
/*     schleifenfrei und zusammenhaengend ist. Falls als Ausgabe eine	   */
/*     Baumzerlegung eingestellt ist, so muss der Graph auch noch gelabelt */
/*     sein.								   */
/*									   */
/*     Datenstruktur `Tree_width_info' :				   */
/*  typedef struct tree_width_info {
        struct sgraph_proc_info    sgraph_proc_info;
        int                        min_baumweite,max_baumweite;
        Sgraph                     new_sgraph;
        Algo_output_type           algo_output_type;
        bool			   errorflag;
	Slist			   teilgraphen;
       } *Tree_width_info;

*/
/*    algo_output_type : enth"alt den eingestellten Ausgabetyp             */
/*       	         entweder `BAUMBREITE' oder `BAUMZERLEGUNG'	   */
/*    sollte eine Routine nur die Baumweite berechnen so muss dieses Feld  */
/*    auf `BAUMWEITE' gestzt werden.					   */
/*									   */
/*    `errorflag' : Konnte aus irgendeinem Grund die Berechnung nicht      */
/*     abgeschlossen werden, so ist dieses Feld auf `true' zu setzen.      */
/*									   */
/*     `min_baumweite,max_baumweite' : enthalten am Ende die berechneten   */
/*					Ergebnisse, die ausgegeben sollen  */
/* 									   */
/*     `new_sgraph' : zur Rueckgabe der Baumzerlegung 			   */
/*									   */
/*     `sgraph_proc_info' : die Sgraph info_proc Strucktur (enthaelt u.a   */
/*				den aktuellen Graphen.)			   */
/*									   */
/*									   */
/*     Wurde `Baumweite' als Ausgabetyp gewaehlt so enthaelt `teilgraphen' */
/*	eine Zerlegung des Graphen in Zusammenhangkomponente (Sgraphen)    */
/*      Die Baumweite des Graphen ermittelt sich in diesem Fall aus dem    */
/*      Maximum ueber den Baumweiten der Teilgraphen und der ubergebenen   */
/*     `max_baumweite'.							   */
/*      Sonst enthaelt `teilgraphen' eine Slist mit genau einem Sgraphen,  */
/*      dem aktuellen Sgraphen (<=>  sgraph_proc_info->sgraph)		   */
/*									   */
/*     Die Ausgabe der Baumzerlegung oder der Baumweite uebernimmt wieder  */
/*     das Hauptprogramm.						   */
/*									   */
/***************************************************************************/
/***************************************************************************/
/*         Globale Prozedur						   */
/***************************************************************************/
/*									   */
/*	void 	init_treewidth_menu	()				   */
/*               zur Initialisierung des Haupfensters mit Fremdroutienen   */
/*									   */
/***************************************************************************/

extern void init_treewidth_menu(void);


typedef struct tree_width_info {
        Sgraph_proc_info    sgraph_proc_info;
        int                        min_baumweite,max_baumweite;
        Sgraph                     new_sgraph;
        enum ALGO_OUTPUT_TYPE      algo_output_type;
        Slist			   teilgraphen;
        bool			   errorflag;
       } *Tree_width_info;


#endif
