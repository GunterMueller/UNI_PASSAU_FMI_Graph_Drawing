/************************************************************************************************/
/*											      	*/
/*				     FILE: lpr_optimal_layout				      	*/
/*											     	*/
/*	Hier werden s"amtliche Kostenfunktionen berechnet, bis hin zu den optimalen           	*/
/*      Layouts. 										*/
/************************************************************************************************/


#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>

#include "lp_edgeline.h"

#include "lpr_nnode.h"
#include "lpr_ggraph.h"
#include "lpr_hhierarchie.h"

#include "lpr_optimal_layout.h"



/****************************************************************************************/
/*											*/
/* 	Einige Funktionen zum Umgang mit der Struktur lpr_cost_c.			*/
/*											*/
/****************************************************************************************/

/******************************************************************************************
Function	: create_lpr_cost_c_list
Input		: lpr_Graph son, int number_of_father_prods
Output	: lpr_Cost_c_list
Description	: Erzeugt ein solches Objekt. son und number_of_father_prods wird ben"otigt,
		  um zu wissen, wieviel Speicher das Array cost_c ben"otigt.
******************************************************************************************/

lpr_Cost_c_list create_lpr_cost_c_list(lpr_Graph son, int number_of_father_prods)
{
	lpr_Cost_c_list new;

	new = ( lpr_Cost_c_list ) mymalloc(sizeof(struct lpr_cost_c_list ));
	new->son_prod 	= son;
	new->pre		= new;
	new->suc		= new;
	new->cost_c		= (int*)mycalloc( son->number_of_iso_prods * number_of_father_prods, sizeof(int) );
	return new;
}

/******************************************************************************************
Function	: add_cost_c_to_lpr_cost_c_list
Input		: pr_Cost_c_list list, new
Output	: lpr_Cost_c_list
Description : H"angt an eine evtl. bestehende Liste vom Typ lpr_cost_c_list ein neues Ele-
		  ment an.
******************************************************************************************/

lpr_Cost_c_list add_cost_c_to_lpr_cost_c_list(lpr_Cost_c_list list, lpr_Cost_c_list new)
{
	lpr_Cost_c_list last;

	if (list == NULL)
		return new;
	else
	{
		last      = list->pre;
		last->suc = new;
		new->pre  = last;
		list->pre = new;
		new->suc  = list;
		return list;
	}
}

/******************************************************************************************
Function	: free_lpr_cost_c_list
Input		: pr_Cost_c_list list
Output	: void 
Description : L"oscht eine evtl. existierende Liste vom Typ lpr_cost_c_list.
******************************************************************************************/

void free_lpr_cost_c_list(lpr_Cost_c_list list)
{
	lpr_Cost_c_list  cur = list, last;

	
	if (list != NULL)
	{
		list->pre->suc = NULL;			/* F"ur Terminierung sorgen	*/
		while (cur != NULL)			/* und durchlaufen		*/
		{
			last = cur;
			cur = cur->suc;
			free(last->cost_c);		/* Gib auch das Array frei.	*/
			free(last);
		}
	}
}

/******************************************************************************************
Function	: get_lpr_cost_c_list
Input		: lpr_Graph father_prod, son_prod 
Output	: lpr_Cost_c_list
Description : Holt aus den cost_c-Listen der father_prod den entsprechenden Eintrag, der
		  father_prod und son_prod verbindet.
******************************************************************************************/

lpr_Cost_c_list get_lpr_cost_c_list(lpr_Graph father_prod, lpr_Graph son_prod)
{
	lpr_Cost_c_list cur = father_prod->cost_c;
	
	while ( cur->son_prod != son_prod )		/* Durchlaufe die Liste, bis	*/
		cur = cur->suc;				/* der son_prod-Eintrag ge-	*/
							/* funden.			*/
	return cur;
}

/******************************************************************************************
Function   : compute_cost_function_c0
Input      : lpr_Graph
Output     : void
Description: von der "ubergebenen Produktion wird die
	     Kostenfunktion c0 berechnet.
******************************************************************************************/

void compute_cost_function_c0(lpr_Graph prod)
{
	int 		costs = 0, bends, eh, index;
	lpr_Edgelist 	cur_edgelist;
	lpr_Nodelist    cur_nodelist;

	prod->cost_c0_array = (int *) mymalloc(prod->number_of_iso_prods * sizeof(int));
	for (index = 0; index < prod->number_of_iso_prods; index++)                      			/* Berechne c0 f"ur alle Layouts */
													     				/* dieser Produktion             */
	{
		FOR_LPR_NODELIST(prod->nodes, cur_nodelist)								/* Durchlaufe die Knoten der Pro-*/
			FOR_LPR_RHS_EDGES(cur_nodelist->node->source_edges, cur_edgelist)				/* duktion um an die Kanten zu   */
				bends = (int)(cur_edgelist->edge->array_of_iso_edge_pointers[index]->bends);	/* kommen.Hole Knicke des Kanten-*/
				eh    = (int)cur_edgelist->edge->EH_number;				     		/* layouts und die EH-Number.    */
				costs+= bends * eh;							     			/* Summiere Kosten auf.          */
			END_FOR_LPR_RHS_EDGES(cur_nodelist->node->source_edges, cur_edgelist);
		END_FOR_LPR_NODELIST(prod->nodes, cur_nodelist);
		prod->cost_c0_array[index] = costs;
/*		printf("\r\nLayout %d: c0(pl0) = %d", index, prod->cost_c0_array[index]);	*/
		costs = 0;
	}
}


/******************************************************************************************	
Function   : compute_summand
Input      : lpr_Edge father_edge, son_edge;
	     int father_layout_nr, son_layout_nr;
Output     : int
Description: Hier wird ein Summand aus der Kostenfunktion c berechnet.( wird also f"ur
	     compute_cost_function_c_on_layouts ben"otigt ) 
******************************************************************************************/
int compute_summand(lpr_Edge father_edge, lpr_Edge son_edge, int father_layout_nr, int son_layout_nr)
{
	int   son_dir,father_dir, c = 0;
	lpr_Bridge_hierarchie bh;

	son_dir		     = first_dir(son_edge->array_of_iso_edge_pointers[son_layout_nr]->edge);	/* Hole die Source-Dir von son_edge       */
	father_dir	     = last_dir(father_edge->array_of_iso_edge_pointers[father_layout_nr]->edge); 	/* und die Target-Dir von father_edge     */

	c+=Xi(father_dir,son_dir);									  			/* Werte Xi daf"ur aus und summiere auf   */
	c+=son_edge->array_of_iso_edge_pointers[son_layout_nr]->bends;                              	/* Hole Knicke im Layout und summiere auf */
	bh=(lpr_Bridge_hierarchie) get_bridge_hierarchy(father_edge,son_edge);				  	/* Hole die BH#(father_edge, son_edge)    */
	c*=(int)(bh->number);										  			/* und multipliziere c damit              */

	return c;
}


/******************************************************************************************
Function   : compute_cost_function_c_on_layouts
Input      : lpr_Graph son , father;
Output     : void
Description: Berechnet die Kostenfunktion c f"ur alle Layouts der "ubergebenen Pro-
	     duktionen.
******************************************************************************************/

void compute_cost_function_c_on_layouts(lpr_Graph son, lpr_Graph father)
{
	lpr_Edgelist 	cur_embeddinglist1, cur_embeddinglist2, cur_edgelist;
	lpr_Nodelist    	cur_nodelist;
	int 			c = 0, bends, eh;
	int             	iso_prods_of_son, iso_prods_of_father, index1, index2;
	lpr_Cost_c_list	new_cost_c;

	iso_prods_of_son    = son->number_of_iso_prods;          					/* Hole die Anzahl isomorpher Layouts der 	*/
	iso_prods_of_father = father->number_of_iso_prods;						/* beiden Produktionen 					*/

	
	new_cost_c = create_lpr_cost_c_list( son, iso_prods_of_father );
	father->cost_c = add_cost_c_to_lpr_cost_c_list(father->cost_c, new_cost_c ); 
	
	for(index1 = 0; index1 < iso_prods_of_son; index1++)						/* Durchlaufe alle Layouts der son-Produktion	*/						
	{
		for(index2 = 0; index2 < iso_prods_of_father; index2++)				/* und alle der father-Produktion        		*/
		{
			c = 0;                                                          		/* Initialisiere die Kosten mit 0          	*/
	 		FOR_LPR_IN_CONN_REL(son->IN_embeddings, cur_embeddinglist1)     		/* Zun"achst f"ur die IN_CONN_RELS von son  	*/       
			{
				FOR_LPR_IN_CONN_REL(father->IN_embeddings, cur_embeddinglist2) 	/* und allen Kanten von  father        		*/
					c+=compute_summand(cur_embeddinglist2->edge,cur_embeddinglist1->edge, index2, index1);
				END_FOR_LPR_IN_CONN_REL(father->IN_embeddings, cur_embeddinglist2); /* berechne jeweils einen Summanden und summiere auf */

				FOR_LPR_OUT_CONN_REL(father->OUT_embeddings, cur_embeddinglist2)
					c+=compute_summand(cur_embeddinglist2->edge,cur_embeddinglist1->edge, index2, index1);
				END_FOR_LPR_OUT_CONN_REL(father->OUT_embeddings, cur_embeddinglist2);
		
				FOR_LPR_NODELIST(father->nodes, cur_nodelist)
					FOR_LPR_RHS_EDGES(cur_nodelist->node->source_edges, cur_edgelist)
						c+=compute_summand(cur_edgelist->edge,cur_embeddinglist1->edge, index2, index1);
					END_FOR_LPR_RHS_EDGES(cur_nodelist->node->source_edges, cur_edgelist);
				END_FOR_LPR_NODELIST(father->nodes, cur_nodelist);
			}
			END_FOR_LPR_IN_CONN_REL(son->IN_embeddings, cur_embeddinglist1);

			FOR_LPR_OUT_CONN_REL(son->OUT_embeddings, cur_embeddinglist1)    		/* das gleiche f"ur die OUT_CONN_RELS von son	*/    
			{
				FOR_LPR_IN_CONN_REL(father->IN_embeddings, cur_embeddinglist2)
					c+=compute_summand(cur_embeddinglist2->edge,cur_embeddinglist1->edge, index2, index1);
				END_FOR_LPR_IN_CONN_REL(father->IN_embeddings, cur_embeddinglist2);

				FOR_LPR_OUT_CONN_REL(father->OUT_embeddings, cur_embeddinglist2)
					c+=compute_summand(cur_embeddinglist2->edge,cur_embeddinglist1->edge, index2, index1);
				END_FOR_LPR_OUT_CONN_REL(father->OUT_embeddings, cur_embeddinglist2);
		
				FOR_LPR_NODELIST(father->nodes, cur_nodelist)
					FOR_LPR_RHS_EDGES(cur_nodelist->node->source_edges, cur_edgelist)
						c+=compute_summand(cur_edgelist->edge,cur_embeddinglist1->edge, index2, index1);
					END_FOR_LPR_RHS_EDGES(cur_nodelist->node->source_edges, cur_edgelist);
				END_FOR_LPR_NODELIST(father->nodes, cur_nodelist);
			}
			END_FOR_LPR_OUT_CONN_REL(son->OUT_embeddings, cur_embeddinglist1);

			FOR_LPR_NODELIST(son->nodes, cur_nodelist)                         	/* Hier wird die zweite Summe von c berechnet   */
 				FOR_LPR_RHS_EDGES(cur_nodelist->node->source_edges, cur_edgelist)	/* also f"ur alle RHS_EDGES berechne die Kosten */
					bends = (int)(cur_edgelist->edge->array_of_iso_edge_pointers[index1]->bends); /* wie in c0              	*/
					eh    = (int)cur_edgelist->edge->EH_number;
					c+= bends * eh;
				END_FOR_LPR_RHS_EDGES(cur_nodelist->node->source_edges, cur_edgelist);
			END_FOR_LPR_NODELIST(son->nodes, cur_nodelist);

			*((int*)new_cost_c->cost_c + (index2 * iso_prods_of_son) + (index1)) = c;
		}
	}	
}

/******************************************************************************************
Function    : compute_cost_function_c 
Input       : lpr_Node node; lpr_Graph father;
Output      : void
Description : Berechnet die Kostenfunktion f"ur den vollst"andigen Ablei-
	      tungsbaum, indem jeweils compute_cost_function_c_on_layouts
	      aufgerufen wird und mit Rekursion weiter abgestiegen wird.
******************************************************************************************/
	      
void compute_cost_function_c(lpr_Node node, lpr_Graph father)
{
	lpr_Nodelist cur_nodelist;
	lpr_Graph    lpr_graph = node->applied_production;

	if (father != NULL)
		compute_cost_function_c_on_layouts(lpr_graph, father);          			/* rufe compute_cost_function_c_on_layouts auf  */
	FOR_LPR_NODELIST(lpr_graph->nodes, cur_nodelist)						/* und gehe dann ggfs. weiter im Ableitungsbaum */
		if (cur_nodelist->node->applied_production != NULL)
			compute_cost_function_c(cur_nodelist->node, lpr_graph);
	END_FOR_LPR_NODELIST(lpr_graph->nodes, cur_nodelist);
}				
		


/******************************************************************************************
Function   : compute_cost_functions
Input      : lpr_Node root;
Output     : void
Description: Dient zum Aufruf aller Funktionen, die eine Kostenfunktion
	     berechnen.
******************************************************************************************/	     
void compute_cost_functions_c_and_c0(lpr_Node root)
{
	compute_cost_function_c0(root->applied_production); /* Berechne Kostenfunktion c0 */
	compute_cost_function_c(root, NULL);                /* Berechne Kostenfunktion c  */
}


/******************************************************************************************
Function   : create_lpr_Cost_layout_to_production
Input      : void 
Output     : lpr_Cost_layout_to_production
Description: Erzeugt ein Objekt vom Typ lpr_cost_layout_to_production und
	       gibt einen Zeiger darauf zur"uck.
******************************************************************************************/	     
lpr_Cost_layout_to_production create_lpr_Cost_layout_to_production(void)
{
	lpr_Cost_layout_to_production new;

	new = (lpr_Cost_layout_to_production) mymalloc(sizeof(struct lpr_cost_layout_to_production));
	new->prod = NULL;
	new->cost = 0;
	new->pre  = new;
	new->suc  = new;

	return new;
}

/************************************************************************
Function   : append_lpr_Cost_layout_to_production
Input      : lpr_Cost_layout_to_production list, new; 
Output     : lpr_Cost_layout_to_production
Description: H"angt ein Objekt vom Typ lpr_cost_layout_to_production an
		 eine solche Liste an und gibt einen Zeiger auf die Liste zu-
		 r"uck.
*************************************************************************/	     
lpr_Cost_layout_to_production append_lpr_Cost_layout_to_production(lpr_Cost_layout_to_production list, lpr_Cost_layout_to_production new)
{
	lpr_Cost_layout_to_production last;

	if (list == NULL)
		return new;
	else
	{
		last      = list->pre;
		last->suc = new;
		new->pre  = last;
		list->pre = new;
		new->suc  = list;
		return list;
	}
}

/************************************************************************
Function   : create_lpr_Layouts_for_set_stern
Input      : void 
Output     : lpr_Layouts_for_set_stern
Description: Erzeugt ein Objekt vom Typ lpr_layouts_for_set_stern und
	       gibt einen Zeiger darauf zur"uck.
*************************************************************************/	     
lpr_Layouts_for_set_stern create_lpr_Layouts_for_set_stern(void)
{
	lpr_Layouts_for_set_stern new;

	new = (lpr_Layouts_for_set_stern) mymalloc(sizeof(struct lpr_layouts_for_set_stern));
	new->prod = NULL;
	new->set  = NULL;
	new->pre  = new;
	new->suc  = new;

	return new;
}

/************************************************************************
Function   : append_lpr_Layouts_for_set_stern
Input      : lpr_Layouts_for_set_stern list, new; 
Output     : lpr_Layouts_for_set_stern
Description: H"angt ein Objekt vom Typ lpr_Layouts_for_set_stern an
		 eine solche Liste an und gibt einen Zeiger auf die Liste zu-
		 r"uck.
*************************************************************************/	     
lpr_Layouts_for_set_stern append_lpr_Layouts_for_set_stern(lpr_Layouts_for_set_stern list, lpr_Layouts_for_set_stern new)
{
	lpr_Layouts_for_set_stern last;

	if (list == NULL)
		return new;
	else
	{
		last      = list->pre;
		last->suc = new;
		new->pre  = last;
		list->pre = new;
		new->suc  = list;
		return list;
	}
}


/******************************************************************************************
Function	: compute_optimal_production_layout_sets_on_layouts
Input		: lpr_Graph prod
Output		: void
Description	: Enspricht der Funktion compute_optimal_layout_set' aus der Theorie. Hier
		  werden die Menge set* und die Kostenfunktionen c* und C* berechnet. set*
		  ist als ein Array von Objekten des Typs lpr_Layouts_for_set_stern imple-
		  mentiert, in denen f"ur jede Produktion in einem Bitarray die Zugeh"orig-
		  keit eines Layouts zur Menge durch eine 1 markiert ist.
******************************************************************************************/
void compute_optimal_production_layout_sets_on_layouts(lpr_Graph prod)
{
	int 				father_index, sum, son_index, min, merke, iso_prods_of_son, iso_prods_of_father;
	lpr_Nodelist 			cur_nodelist;
	lpr_Graph 			son; 
	lpr_Cost_layout_to_production 	cost_layout_to_production, father_list;
	lpr_Layouts_for_set_stern 	layout_set;
	lpr_Cost_c_list		cost_c_array;

	
	FOR_LPR_NODELIST(prod->nodes, cur_nodelist)							/* Es wird rekursiv durch den Ableitungsbaum 		*/
		if (cur_nodelist->node->applied_production != NULL)					/* gelaufen, und zwar Bottom-Up              		*/
			compute_optimal_production_layout_sets_on_layouts(cur_nodelist->node->applied_production);
	END_FOR_LPR_NODELIST(prod->nodes, cur_nodelist);

	iso_prods_of_father = prod->number_of_iso_prods;						/* Merke die Anzahl isom. Layouts der Vaterproduktion */
	for(father_index = 0; father_index < iso_prods_of_father; father_index++)		/* Durchlaufe alle Vaterlayouts				*/
	{
		sum = 0;
		FOR_LPR_NODELIST(prod->nodes, cur_nodelist)						/* Durchlaufe alle S"ohne der Vaterproduktion		*/
			if ((son = cur_nodelist->node->applied_production) != NULL)
			{
				iso_prods_of_son = son->number_of_iso_prods;				/* Merke die Anzahl isom. Layouts der Sohnproduktion 	*/
				for (son_index = 0; son_index < iso_prods_of_son; son_index++)	/* und durchlaufe diese Layouts				*/
				{
					cost_c_array = get_lpr_cost_c_list( prod, son );
					merke = *(cost_c_array->cost_c + (father_index * iso_prods_of_son) + (son_index));
					merke+= son->cost_C_stern_array[son_index];
					if (son_index == 0) min = merke;
					if (merke < min) min = merke;						/* suche das Layout mit minimalen Kosten			*/
				}
				cost_layout_to_production = create_lpr_Cost_layout_to_production();
				cost_layout_to_production->prod  = son;					/* und speichere diese Kosten in c* ab.			*/

				cost_layout_to_production->cost  = min;
				if (prod->cost_c_stern_array == NULL)
					prod->cost_c_stern_array = (lpr_Cost_layout_to_production *) mycalloc(iso_prods_of_father, sizeof(lpr_Cost_layout_to_production));
				father_list = prod->cost_c_stern_array[father_index];			/* Gleichzeitig wird die Menge von Layouts mit den	*/
															/* minimalen Kosten in set* gespeichert			*/
				prod->cost_c_stern_array[father_index] = append_lpr_Cost_layout_to_production(father_list,cost_layout_to_production);

				if (prod->set_stern_array == NULL)
					prod->set_stern_array = (lpr_Layouts_for_set_stern *) mycalloc(iso_prods_of_father, sizeof(lpr_Layouts_for_set_stern));
				layout_set = create_lpr_Layouts_for_set_stern();
				layout_set->prod = son;
				layout_set->set  = (int*) mycalloc(iso_prods_of_son, sizeof(int));
				for (son_index = 0; son_index < iso_prods_of_son; son_index++)
				{
					cost_c_array = get_lpr_cost_c_list( prod, son );
					merke = *(cost_c_array->cost_c + (father_index * iso_prods_of_son) + (son_index));
					merke+= son->cost_C_stern_array[son_index];
					if ( min == merke )
						layout_set->set[son_index] = 1;	
				}
				prod->set_stern_array[father_index] = append_lpr_Layouts_for_set_stern(prod->set_stern_array[father_index], layout_set);
				sum+= min;										/* addiere diese Kosten auf					*/
			}
		END_FOR_LPR_NODELIST(prod->nodes, cur_nodelist);
		if (prod->cost_C_stern_array == NULL)
			prod->cost_C_stern_array = (int *) mycalloc (iso_prods_of_father, sizeof(int));	
		prod->cost_C_stern_array[father_index] = (int)sum;					/* sum wird in C* abgelegt					*/
	}			
}

/********************************************************************************
Function	: compute_optimal_production_layout_sets
Input		: lpr_Node root
Output		: void
Description	: Enspricht der Funktion compute_optimal_production_layout_sets.
		  Ruft compute_optimal_production_layout_sets_on_layouts auf, und
		  berechnet ausserdem die Menge set** und C**. Von hier aus wird
		  auch eine Testausgabe aufgerufen.
********************************************************************************/		
void compute_optimal_production_layout_sets(lpr_Node root)
{
	int index, merke, min;
	lpr_Graph prod = root->applied_production;

	compute_optimal_production_layout_sets_on_layouts(prod);		/* Rekursion anstossen			*/
	for (index = 0; index < prod->number_of_iso_prods; index++)		/* C* wird hier noch nachgebessert 	*/
		prod->cost_C_stern_array[index]+=prod->cost_c0_array[index];

	for (index = 0; index < prod->number_of_iso_prods; index++)		/* Berechne C**				*/
	{
		merke = prod->cost_C_stern_array[index];
		if (index == 0)  min = merke;
		if (merke < min) min = merke;
	}
	prod->cost_C_2stern_array = min;

	for (index = 0; index < prod->number_of_iso_prods; index++)		/* und set**				*/
	{
		merke = prod->cost_C_stern_array[index];
		if (merke == min)
		{
			if (prod->set_2stern_array == NULL)
				prod->set_2stern_array = (int *) mycalloc(prod->number_of_iso_prods, sizeof(int));
			*(prod->set_2stern_array + index) = 1;
		}
	}
}

/********************************************************************************
Function	: lpr_free_lpr_cost_layout_to_production
Input		: lpr_Cost_layout_to_production list
Output		: void
Description	: Gibt den Speicher f"ur diese Liste wieder frei
********************************************************************************/		
void lpr_free_lpr_cost_layout_to_production(lpr_Cost_layout_to_production list)
{
	lpr_Cost_layout_to_production  cur_cost = list, last;

	
	if (list != NULL)
	{
		list->pre->suc = NULL;			/* F"ur Terminierung sorgen	*/
		while (cur_cost != NULL)		/* und durchlaufen		*/
		{
			last = cur_cost;
			cur_cost = cur_cost->suc;
			free(last);
		}
	}
}


/********************************************************************************
Function	: lpr_free_lpr_layouts_for_set_stern
Input		: lpr_Layouts_for_set_stern list
Output		: void
Description	: Gibt den Speicher f"ur diese Liste wieder frei
********************************************************************************/		
void lpr_free_lpr_layouts_for_set_stern(lpr_Layouts_for_set_stern list)
{
	lpr_Layouts_for_set_stern  cur_layout = list, last;

	
	if (list != NULL)
	{
		list->pre->suc = NULL;			/* F"ur Terminierung sorgen	*/
		while (cur_layout != NULL)		/* und durchlaufen		*/
		{
			last = cur_layout;
			cur_layout = cur_layout->suc;
			free(last);
		}
	}
}

/*************************************************************************************************
Function	: lpr_free_costfunctions
Input		: lpr_Node node
Output		: void
Description	: Gibt den von den Kostenfunktionen belegten Speicher in lpr_Graph wieder frei.
*************************************************************************************************/
void delete_all_costfunctions(lpr_Node node)
{
	lpr_Graph 	prod = node->applied_production;
	lpr_Nodelist 	cur_nodelist;
	int		index;
    
	free_lpr_cost_c_list(prod->cost_c); 		prod->cost_c = NULL;		/* Freigeben und Nullsetzen 						*/
	free(prod->cost_c0_array); 		prod->cost_c0_array 		= NULL;
	free(prod->cost_C_stern_array); 	prod->cost_C_stern_array 	= NULL;
	prod->cost_C_2stern_array = 0;
	free(prod->set_2stern_array); 		prod->set_2stern_array 	= NULL;
	
	for (index = 0; index < prod->number_of_iso_prods; index++)				/* Da set* und c* als Arrays von Listen implementiert 	*/
	{													/* sind, werden zuerst die Listen freigegeben und dann	*/
		if (prod->cost_c_stern_array != NULL)						/* die Arrays gel"oscht				     		 	*/
			lpr_free_lpr_cost_layout_to_production(prod->cost_c_stern_array[index]);
		if (prod->set_stern_array != NULL)
			lpr_free_lpr_layouts_for_set_stern(prod->set_stern_array[index]);
	}
	free(prod->cost_c_stern_array);		prod->cost_c_stern_array 	= NULL;
	free(prod->set_stern_array);		prod->set_stern_array		= NULL;

	FOR_LPR_NODELIST(prod->nodes, cur_nodelist)						/* Rekursiv Durchlaufen um an jede Produktion zu kommen	*/
		if (cur_nodelist->node->applied_production != NULL)
			delete_all_costfunctions(cur_nodelist->node);
	END_FOR_LPR_NODELIST(prod->nodes, cur_nodelist);	
}


/*******************************************************************************
Function	: compute_optimal_production_layouts_rest
Input		: lpr_Graph prod; int offset; 
Output		: void 
Description	: Enspricht der Funktion compute_optimal_production_layouts'
		  aus der Theorie. Hier werden die optimalen Layouts der 
		  Produktionen aus set* ausgew"ahlt und in prod->optimal_layout
		  abgelegt. offset gibt dabei den Index im array_of_iso_prod_pointers 
		  des f"ur prod als optimal gew"ahlten Layouts an. Das dient dazu,
		  dass man nicht mehr nach dem richtigen prod->set_stern_array zu
		  suchen braucht, sondern direkt zugreifen kann.
*******************************************************************************/
void compute_optimal_production_layouts_rest(lpr_Graph prod, int offset)
{
	int index;
	lpr_Graph son;
	lpr_Nodelist cur_nodelist;
	lpr_Layouts_for_set_stern best_layout_set_of_father, cur_layout_set;

	if (prod->set_stern_array != NULL)
	{
		best_layout_set_of_father = prod->set_stern_array[offset];    					/* Hole Liste set*(pl*(prod), ? )       */			
		FOR_LPR_NODELIST(prod->nodes, cur_nodelist)								/* und laufe durch den Ableitungsbaum   */
			if ( ((son = cur_nodelist->node->applied_production) != NULL) &&
			     (!cur_nodelist->node->leaf)					)
			{
				FOR_LPR_LAYOUTS_FOR_SET_STERN(best_layout_set_of_father, cur_layout_set)	/* suche richtige Prod. Damit ist       */
					if (cur_layout_set->prod == son) break;						/* cur_layout_set = set*(pl*(prod),son) */
				END_FOR_LPR_LAYOUTS_FOR_SET_STERN(best_layout_set_of_father, cur_layout_set);
				for(index = 0; index < son->number_of_iso_prods; index++)				/* Durchlaufe das Bitarray              */
					if (cur_layout_set->set[index] == 1) 						/* und suche eingetragene Layouts       */
					{
						son->optimal_layout = son->array_of_iso_prod_pointers[index];	/* W"ahle erstes als optimales          */
						break;	
					}
				compute_optimal_production_layouts_rest( son, index);					/* und weiter nach unten im Baum        */
			}
		END_FOR_LPR_NODELIST(prod->nodes, cur_nodelist);
	}
}			


/*******************************************************************************
Function	: compute_optimal_production_layouts
Input		: lpr_Node node 
Output		: void 
Description	: Enspricht der Funktion compute_optimal_production_layouts
		  aus der Theorie. Hier wird das optimale Layout f"ur die
		  erste Produktion im Ableitungsbaum berechnet und dann die
		  Funktion compute_optimal_production_layouts_rest aufgerufen,
		  die den Rest erledigt, also alle anderen Layouts berechnet.
*******************************************************************************/
void compute_optimal_production_layouts(lpr_Node node)
{
	int index;
	Graph optimal_layout;
	lpr_Graph prod = node->applied_production;

	for (index = 0; index < prod->number_of_iso_prods; index++)				/* Durchlaufe die Menge set**, in der optimale 	*/
		if (prod->set_2stern_array[index] == 1)						/* Layouts f"ur die erste Produktion liegen    	*/
		{
			optimal_layout = prod->array_of_iso_prod_pointers[index];		/* Nimm das erstbeste 					*/
			break;
		}
	prod->optimal_layout = optimal_layout;							/* Abspeichern in lpr_Graph				*/
	compute_optimal_production_layouts_rest( prod , index);				/* und Rekursion anstossen				*/
}

/*******************************************************************************
Function	: testprint_Costs
Input		: lpr_Graph prod
Output		: void
Description	: Gibt das Ergebnis der ganzen Berechnungen von Kosten und Layouts
		  auf die Shell via printf aus.
*******************************************************************************/

void testprint_Costs(lpr_Graph prod)
{
	int index, counter, i;
	lpr_Nodelist cur_nodelist;
	lpr_Cost_layout_to_production cost_layout_to_production, cur_cost;
	lpr_Layouts_for_set_stern cur_set;

	printf("\r\n**************************************************\r\n");
	
	if (prod->cost_C_stern_array != NULL)
	{
		for(index = 0; index < prod->number_of_iso_prods; index++)
			printf("C*(pl%d) = %d   ",index, prod->cost_C_stern_array[index]);
	}

	printf("\r\n");
	if (prod->cost_c_stern_array != NULL)
	{
		for(index = 0; index < prod->number_of_iso_prods; index++)
		{
			if ((cost_layout_to_production = prod->cost_c_stern_array[index]) != NULL)
			{
				counter = 0;
				FOR_LPR_COST_LAYOUT_TO_PRODUCTION(cost_layout_to_production, cur_cost)
					printf("\r\nc*(PL%d,p%d) = %d  ", index, counter, cur_cost->cost);
					counter++;
				END_FOR_LPR_COST_LAYOUT_TO_PRODUCTION(cost_layout_to_production, cur_cost);
			}
		}
		
	}
	printf("\r\n");
	if (prod->set_stern_array != NULL)
	{
		for(index = 0; index < prod->number_of_iso_prods; index++)
		{
			if (prod->set_stern_array[index] != NULL)
			{
				counter = 0;
				FOR_LPR_LAYOUTS_FOR_SET_STERN(prod->set_stern_array[index], cur_set)
					printf("\r\nset*(PL%d,p%d) = { ", index,counter);
					for(i = 0; i < cur_set->prod->number_of_iso_prods; i++)
						if (cur_set->set[i] == 1)
							printf("PL%d (Adr:%p)", i, (void *)cur_set->prod->array_of_iso_prod_pointers[i]);
					printf("}");
					counter++;
				END_FOR_LPR_LAYOUTS_FOR_SET_STERN(prod->set_stern_array[index], cur_set);
			}
		}
	}

	printf("\r\nOptimal Layout: %p", (void *) prod->optimal_layout);

	FOR_LPR_NODELIST(prod->nodes, cur_nodelist)
		if (cur_nodelist->node->applied_production != NULL)
			testprint_Costs(cur_nodelist->node->applied_production);
	END_FOR_LPR_NODELIST(prod->nodes, cur_nodelist);
}


