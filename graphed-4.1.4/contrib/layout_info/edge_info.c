#include <sgraph/std.h>
#include <sgraph/sgraph.h>
#include <sgraph/slist.h>
#include <sgraph/graphed.h>

#include <math.h>

#include "layout_info_export.h"

Local double variance(Sgraph sgraph);
void OutputAllEdgeLengths(Sgraph sgraph);

#undef DEBUG

/* length_of_edge liefert die Laenge der uebergebenen Kante zurueck. */

Local double length_of_edge(Sedge edge)
{
  double edge_length=0;
  int previous_x,previous_y, current_x, current_y;
  Edgeline line, el;

  line = (Edgeline)edge_get(graphed_edge(edge),EDGE_LINE);
  for_edgeline(line,el) {
    if(el == line /* first_point */) {  
/*
      previous_x=edgeline_x(el);
      previous_y=edgeline_y(el);
*/
      previous_x = (sedge_real_source(edge))->x;
      previous_y = (sedge_real_source(edge))->y;
    } else if (el != line->pre /* last_point */) { 
      current_x = edgeline_x(el);
      current_y = edgeline_y(el);
      edge_length += sqrt(pow((double)(current_x - previous_x),2.0) +
			  pow((double)(current_y - previous_y),2.0));
      previous_x=edgeline_x(el);
      previous_y=edgeline_y(el);        
    } else {
      current_x=(sedge_real_target(edge))->x;
      current_y=(sedge_real_target(edge))->y;
      edge_length += sqrt(pow((double)(current_x - previous_x),2.0) +
			  pow((double)(current_y - previous_y),2.0));
    }
  } end_for_edgeline(line, el);
  return(edge_length);
}


/* compute_edge_length darf erst aufgerufen werden, wenn die Kantenattribute */
/* vom Typ MyEdgeAttrs angelegt wurden.                                      */

/* compute_edge_length speichert im attrs-Feld jeder Kante ihre Laenge ab.   */

Global void compute_edge_length(Sgraph sgraph)
{
  Snode node;
  Sedge edge;

  if(sgraph==empty_sgraph)
    error("No graph selected.\n");
  else if(sgraph->nodes==empty_node)
    error("Graph is empty.\n");
  else if(countEdges(sgraph)==0)
    error("Graph contains no edges.\n");
  else
    {
    for_all_nodes(sgraph,node)
      {
      for_sourcelist(node,edge)
        { 
        attr_data_of_type(edge,MyEdgeAttrs)->length=length_of_edge(edge);
        } end_for_sourcelist(node,edge);
      } end_for_all_nodes(sgraph,node);
    }
}


/* max_edge_length liefert die Laenge der laengsten Kante im Graphen    */
/* zurueck bzw. 0, falls der Graph leer ist oder keine Kanten enthaelt. */

Global double max_edge_length(Sgraph sgraph)
{
  double edge_length,max=0;
  Snode node;
  Sedge edge;

  if(sgraph==empty_sgraph)
    {
    error("No graph selected.\n");
    return(0.0);
    }
  else if(sgraph->nodes==empty_node)
    {
    error("Graph is empty.\n");
    return(0.0);
    }
  else
    {
    if(countEdges(sgraph)==0)
      { error("Graph contains no edges.\n");
        return(0.0);
      }
    for_all_nodes(sgraph,node)
      {
      for_sourcelist(node,edge)
      if(sgraph->directed || unique_edge(edge))
        {
        edge_length=(attr_data_of_type(edge,MyEdgeAttrs))->length;
        if(edge_length>max) max=edge_length;
        } end_for_sourcelist(node,edge);
      } end_for_all_nodes(sgraph,node);
    return(max);
    }
}   


/* min_edge_length liefert die Laenge der kuerzesten Kante im Graphen   */
/* zurueck bzw. 0, falls der Graph leer ist oder keine Kanten enthaelt. */

Global double min_edge_length(Sgraph sgraph)
{
  double edge_length,min=0;
  Snode node;
  Sedge edge;

  if(sgraph==empty_sgraph)
    {
    error("No graph selected.\n");
    return(0.0);
    }
  else if(sgraph->nodes==empty_node)
    {
    error("Graph is empty.\n");
    return(0.0);
    }
  else
    {
    if(countEdges(sgraph)==0)
      { error("Graph contains no edges.\n");
        return(0.0);
      }
    for_all_nodes(sgraph,node)
      {
      for_sourcelist(node,edge)
      if(sgraph->directed || unique_edge(edge))
        {
        edge_length=(attr_data_of_type(edge,MyEdgeAttrs))->length;
        if(edge_length==0.0)
          { 
          return(0);
	  }
        if(min==0) 
          min=edge_length;
        else if(edge_length<min) 
          min=edge_length;
        } end_for_sourcelist(node,edge);
      } end_for_all_nodes(sgraph,node);
    return(min);
    }
}


Global double average_edge_length(Sgraph sgraph)
{
  double edge_length,sum=0;
  int	 count_edges = 0;
  Snode node;
  Sedge edge;

  if(sgraph==empty_sgraph)
    {
    error("No graph selected.\n");
    return(0.0);
    }
  else if(sgraph->nodes==empty_node)
    {
    error("Graph is empty.\n");
    return(0.0);
    }
  else
    if(countEdges(sgraph)==0)
      { error("Graph contains no edges.\n");
        return(0.0);
      }

  for_all_nodes(sgraph,node) {
    for_sourcelist(node,edge) if(sgraph->directed || unique_edge(edge)) {
      edge_length=(attr_data_of_type(edge,MyEdgeAttrs))->length;
      sum += edge_length;
      count_edges ++;
    } end_for_sourcelist(node,edge);
  } end_for_all_nodes(sgraph,node);

  return (sum / count_edges);
}


/* variance berechnet die Varianz der Kanten im Graphen. Fuer den      */
/* leeren bzw. kantenlosen Graphen wird 0 zurueckgegeben. Die          */
/* Varianz berechnet sich fuer gerichtete und ungerichtete             */
/* Graphen nach der gleichen Formel: Varianz(X) = E(X*X) - [E(X)*E(X)].*/ 
  
Local double variance(Sgraph sgraph)
{
  double edge_length,sum1=0,sum2=0,var=0;
  int edge_count=0;
  Snode node;
  Sedge edge;

  if(sgraph==empty_sgraph)
    {
    error("No graph selected.\n");
    return(0.0);
    }
  else if(sgraph->nodes==empty_node)
    {
    error("Graph is empty.\n");
    return(0.0);
    }
  else
    {
    edge_count=countEdges(sgraph);
    if(edge_count==0)
      { error("Graph contains no edges.\n");
        return(0.0);
      }
    for_all_nodes(sgraph,node)
      {
      for_sourcelist(node,edge)
      if(sgraph->directed || unique_edge(edge))
        {  
        edge_length=(attr_data_of_type(edge,MyEdgeAttrs))->length;
        sum1+=edge_length * edge_length;
        sum2+=edge_length; 
        } end_for_sourcelist(node,edge);
      } end_for_all_nodes(sgraph,node);
    var=sum1 / edge_count - (sum2*sum2) / (edge_count*edge_count); /* edge_count>0 */    
    return(var);
    }
}


/* insert fuegt die uebergebene Kante vor dem Listenelement ein. */

Local void insert(Edge_List edge_list, Sedge edge, int is_source)
{
  Edge_List elem;

  elem=(Edge_List)malloc(sizeof(struct layout_info_edge_list));
  elem->edge=edge;
  elem->is_source=is_source;        
  elem->suc=edge_list;
  elem->pre=edge_list->pre;
  edge_list->pre=elem;
  elem->pre->suc=elem;
}


/* insert_sortlist fuegt die Kanten eines Knotens sortiert im Uhrzeigersinn*/
/* ein (beginnend ab der Position (-1,0) relativ zum Knotenmittelpunkt).   */
/* Kanten, die aus dem Knoten hinausfuehren, werden markiert und nur       */
/* fuer diese wird die Liste der Winkel berechnet. Die Funktion liefert 1  */
/* zurueck, falls der Listenanfang zurueckgesetzt werden muss.             */

Local int insert_sortlist(Edge_List edge_list, Sedge edge, int is_source, Snode node)
{ 
  Edgeline el1,el2;
  int x1,y1,x2,y2;
  Edge_List list;
  double len1,len2;

  if(edge_list->edge==NULL)
    {
    edge_list->edge=edge;  
    edge_list->is_source=is_source;
    edge_list->angle=0;
    edge_list->pre=edge_list;
    edge_list->suc=edge_list;
    }
  else 
    {
    el2=(Edgeline)edge_get(graphed_edge(edge),EDGE_LINE);
    switch(is_source)    
      {
      case 1:  x2=edgeline_x(el2) - node->x;
               y2=edgeline_y(el2) - node->y;
               break;
      case 0:  x2=edgeline_x(el2->pre) - node->x;
               y2=edgeline_y(el2->pre) - node->y;
      default: break;
      }
    list=edge_list;
    do
      {
      el1=(Edgeline)edge_get(graphed_edge(list->edge),EDGE_LINE);
      switch(list->is_source)    
        {
        case 1:  x1=edgeline_x(el1) - node->x;
                 y1=edgeline_y(el1) - node->y;
                 break;
        case 0:  x1=edgeline_x(el1->pre) - node->x;
                 y1=edgeline_y(el1->pre) - node->y;
        default: break;
        } 
      if(x1==x2 && ((y1>=0 && y2>=0) || (y1<=0 && y2<=0))) 
        {
        switch(is_source)    
          {
          case 1:  x2=edgeline_x(el2->suc) - node->x;
                   y2=edgeline_y(el2->suc) - node->y;
                   break;
          case 0:  x2=edgeline_x(el2->pre->pre) - node->x;
                   y2=edgeline_y(el2->pre->pre) - node->y;
          default: break;
	  }
        switch(list->is_source)    
          {
          case 1:  x1=edgeline_x(el1->suc) - node->x;
                   y1=edgeline_y(el1->suc) - node->y;
                   break;
          case 0:  x1=edgeline_x(el1->pre->pre) - node->x;
                   y1=edgeline_y(el1->pre->pre) - node->y;
          default: break;
          }
        len2=sqrt(pow((double)x2,2.0)+pow((double)y2,2.0));
        len1=sqrt(pow((double)x1,2.0)+pow((double)y1,2.0));
        x1=(int)(floor((double)x1*len2+0.5)); 
        y1=(int)(floor((double)y1*len2+0.5));
        x2=(int)(floor((double)x2*len1+0.5)); 
        y2=(int)(floor((double)y2*len1+0.5));
        }        
      if((y2<=0 && (y1>0 || x2<x1)) || (y2>0 && y1>0 && x2>x1))
        {
        insert(list,edge,is_source);
        if(list==edge_list)
          return(1);
        else
          return(0); 
        }        
      list=list->suc;        
      }
    while(list!=edge_list);
    insert(list,edge,is_source);
    }
  return(0);
}


/* angles berechnet den nicht notwendig spitzen Winkel zwischen den */
/* Strecken [erster uebergebener Punkt, Mittelpunkt] und           */
/* [Mittelpunkt, zweiter uebergebener Punkt] (im Uhrzeigersinn).   */

Local double angles(int first_x, int first_y, int middle_x, int middle_y, int second_x, int second_y)
{
  double pi=atan(1.0)*4.0,angle1,angle2,result;

  if(first_x==second_x && first_y==second_y)
     return(0.0);                 /* oder 2.0*pi */
  first_x=first_x-middle_x;
  first_y=first_y-middle_y;
  second_x=second_x-middle_x;
  second_y=second_y-middle_y;
  angle1=acos((double)first_x /
         sqrt(pow((double)first_x,2.0) + pow((double)first_y,2.0))); 
  angle2=acos((double)second_x /
         sqrt(pow((double)second_x,2.0) + pow((double)second_y,2.0)));
  if(first_y>0)
    angle1=2.0*pi-angle1;
  if(second_y>0)
    angle2=2.0*pi-angle2;
  result=angle1-angle2;
  if(result>=0)
    return(result);
  else 
    return(2.0*pi+result);
}


Local void print_edge_attrs(Sedge edge)
{
#ifdef DEBUG
  Angles_List elem;
  printf("Kante <%s>, Kantenlaenge: %f\n",edge->label,attr_data_of_type(edge,MyEdgeAttrs)->length);
  elem=attr_data_of_type(edge,MyEdgeAttrs)->angles_list;
  if(elem)
    printf("Winkel entlang dieser Kante:\n");
  while(elem)
    {
    printf("%f\n",elem->angle);
    elem=elem->suc;
    }
#endif
}


Local void print_node_attrs(Snode node)
{
#ifdef DEBUG
  Edge_List start,elem;
  
  printf("Winkel um Knoten <%s>:\n",node->label);
  elem=attr_data_of_type(node,MyNodeAttrs)->edge_list;
  start=elem;
  printf("%f\n",elem->angle);
  elem=elem->suc;
  while(elem!=start)
    {
    printf("%f\n",elem->angle);
    elem=elem->suc;
    }
#endif
}


/* list_of_angles_in_edge berechnet die entlang der Kante, auf die edge_ptr */
/* verweist, auftretenden Winkel.                                           */

Local void list_of_angles_in_edge(Edge_List edge_ptr)
{
  Angles_List angles_list,list;
  Edgeline el,el_loop;
  int count_loop=0;  

  angles_list=(Angles_List)malloc(sizeof(struct angles_list));
  angles_list->suc=NULL; 
  list=angles_list;
  el=(Edgeline)edge_get(graphed_edge(edge_ptr->edge),EDGE_LINE);
  for_edgeline(el,el_loop)
    {
    if(!count_loop)
      count_loop=1;
    else if(count_loop==1)
      count_loop=2;
    else
      {
      if(count_loop==2)
	{ /* Die Edgeline muss mindestens 3 Punkte enthalten, wenn */
          /* sie Knickpunkte enthaelt.                             */
        count_loop=3;
        angles_list=(Angles_List)malloc(sizeof(struct angles_list));
        angles_list->suc=NULL; 
        list=angles_list;
        }
      else
	{
        list->suc=(Angles_List)malloc(sizeof(struct angles_list));  
        list=list->suc;
        list->suc=NULL;
        }
        list->angle=angles(edgeline_x(el_loop),edgeline_y(el_loop),
                  edgeline_x(el_loop->pre),edgeline_y(el_loop->pre),
                  edgeline_x(el_loop->pre->pre),edgeline_y(el_loop->pre->pre));
      }
    } end_for_edgeline(el,el_loop);
    if(count_loop<3)
      free(angles_list);
    else
      attr_data_of_type(edge_ptr->edge,MyEdgeAttrs)->angles_list=angles_list;
}


/* list_of_angles_around_node berechnet alle Winkel zwischen benachbarten */
/* Kanten eines Knotens.                                                  */
/* Die edge_list eines Knotens enthaelt die Kanten dieses Knotens im Uhr- */
/* zeigersinn sortiert.                                                   */

Local void list_of_angles_around_node(Edge_List edge_list, Snode node)
{
  Edge_List list,next;
  Edgeline el,el_next;
  double sum=0.0;  

  list=edge_list;
  el_next=(Edgeline)edge_get(graphed_edge(list->edge),EDGE_LINE);
  do
    {
    next=list->suc;
    if(length_of_edge(list->edge))
      {
      while(!length_of_edge(next->edge)) /* Kanten der Laenge 0 ueberspringen */
        next=next->suc;
      el=el_next;
      el_next=(Edgeline)edge_get(graphed_edge(next->edge),EDGE_LINE);
      if(list->is_source)
        {
        if(next->is_source)
          list->angle=angles(edgeline_x(el->suc),edgeline_y(el->suc),
                             node->x,node->y,
                             edgeline_x(el_next->suc),edgeline_y(el_next->suc));
        else
          list->angle=angles(edgeline_x(el->suc),edgeline_y(el->suc),
                             node->x,node->y,
                             edgeline_x(el_next->pre->pre),edgeline_y(el_next->pre->pre));
        }
      else
	{
        if(next->is_source)
          list->angle=angles(edgeline_x(el->pre->pre),edgeline_y(el->pre->pre),
                             node->x,node->y,
                             edgeline_x(el_next->suc),edgeline_y(el_next->suc));
        else
          list->angle=angles(edgeline_x(el->pre->pre),edgeline_y(el->pre->pre),
                             node->x,node->y,
                             edgeline_x(el_next->pre->pre),edgeline_y(el_next->pre->pre));
	}
      sum+=list->angle;
      }
    list=next;
    }
  while(next!=edge_list);
  if(!sum) 
    list->pre->angle=atan(1.0)*8.0;
  attr_data_of_type(node,MyNodeAttrs)->edge_list=edge_list;
}


/* compute_angles darf nur aufgerufen werden, wenn die Kantenattribute  */
/* vom Typ MyEdgeAttrs angelegt worden sind.                            */

/* compute_angles berechnet die im Graphen auftretenden Winkel. Dazu    */
/* werden die Kanten jedes Knotens im Uhrzeigersinn in der edge_list    */
/* sortiert und die Zwischenwinkel berechnet. Der rechts zur Blick-     */
/* richtung des jeweiligen Kantenabschnitts befindliche Winkel wird im  */
/* Attributfeld angles_list eingefuegt. Die Liste der Winkel des Kanten-*/
/* zugs wird (auch im ungerichteten Fall) nur fuer den tatsaechlichen   */
/* Quellknoten berechnet.                                               */ 

Global void compute_angles(Sgraph sgraph)
{
  Snode node;
  Sedge edge; 
  Edge_List edge_ptr_list,edge_ptr;

  if(sgraph==empty_sgraph)
    error("No graph selected.\n");
  else if(sgraph->nodes==empty_node)
    error("Graph is empty.\n");
  else if(countEdges(sgraph)==0)
    error("Graph contains no edges.\n");
  else
    {
    for_all_nodes(sgraph,node)
      {
      edge_ptr_list=(Edge_List)malloc(sizeof(struct layout_info_edge_list));
      edge_ptr_list->edge=NULL;
      edge_ptr_list->is_source=0;
      edge_ptr_list->angle=0;
      edge_ptr_list->pre=edge_ptr_list;
      edge_ptr_list->suc=edge_ptr_list;
      for_sourcelist(node,edge)
	{ 
        if(attr_data_of_type(edge,MyEdgeAttrs)->length)
          {      
          if(!attr_data_of_type(edge,MyEdgeAttrs)->undir_loop)
            /* Falls das Flag bereits gesetzt wurde, ist diese Kante */
            /* schon bearbeitet.                                     */
	    {
            if(node==(Snode)sedge_real_source(edge))
	      {
              if(!sgraph->directed && node==(Snode)sedge_real_target(edge))
                {                                       /* unger. Schleife */
                /* insert_sortlist liefert 1 zurueck, falls der Listen- */
                /* anfang vorgesetzt werden muss.                       */
                if(insert_sortlist(edge_ptr_list,edge,1,node)) /* Kante wird zweimal eingefuegt in die Liste, einmal mit, einmal ohne Markierung is_source */
                  edge_ptr_list=edge_ptr_list->pre;          
                if(insert_sortlist(edge_ptr_list,edge,0,node))
                  edge_ptr_list=edge_ptr_list->pre;
                attr_data_of_type(edge,MyEdgeAttrs)->undir_loop=1;
                }
              else if(insert_sortlist(edge_ptr_list,edge,1,node))
                  edge_ptr_list=edge_ptr_list->pre;
	      }
            else if(insert_sortlist(edge_ptr_list,edge,0,node))
              edge_ptr_list=edge_ptr_list->pre;
            }
	  }
        } end_for_sourcelist(node,edge);
      if(sgraph->directed)
        for_targetlist(node,edge)
          {
          if(attr_data_of_type(edge,MyEdgeAttrs)->length)
	    {
            if(insert_sortlist(edge_ptr_list,edge,0,node))
              edge_ptr_list=edge_ptr_list->pre;
	    }
          } end_for_targetlist(node,edge); 
      if(edge_ptr_list->edge)
	{
        list_of_angles_around_node(edge_ptr_list,node);
        print_node_attrs(node);
        }
      edge_ptr=edge_ptr_list;
      do
	{ 
        if(edge_ptr->edge && edge_ptr->is_source)
	  {
          list_of_angles_in_edge(edge_ptr);
          print_edge_attrs(edge_ptr->edge);
	  }
        edge_ptr=edge_ptr->suc;
        }
      while(edge_ptr!=edge_ptr_list);
      if(!edge_ptr_list->edge)
        free(edge_ptr_list);
      } end_for_all_nodes(sgraph,node);       
    }
}


/* Kanten- und Knotenattribute werden initialisiert und fuer den Graph berechnet. */

void compute_my_attrs(Sgraph sgraph)
{
  Snode node;
  Sedge edge;
  MyEdgeAttrs edge_attrs;
  MyNodeAttrs node_attrs;

  if(sgraph==empty_sgraph)
    error("Es wurde kein Graph selektiert.\n");
  else if(sgraph->nodes==empty_node)
    error("Der Graph ist leer.\n");
  else if(countEdges(sgraph)==0)
    error("Der Graph enthaelt keine Kanten.\n");
  else
    {
    for_all_nodes(sgraph,node)
      {
      node_attrs=(MyNodeAttrs)malloc(sizeof(struct mynodeattrs));
      node_attrs->edge_list=NULL;
      set_nodeattrs(node,make_attr(ATTR_DATA,(char *)node_attrs));
      for_sourcelist(node,edge) if (sgraph->directed || unique_edge(edge)) { 
        edge_attrs=(MyEdgeAttrs)malloc(sizeof(struct myedgeattrs));
        edge_attrs->length=0.0;
        edge_attrs->undir_loop=0;
        edge_attrs->angles_list=NULL;
        set_edgeattrs(edge,make_attr(ATTR_DATA,(char *)edge_attrs));
        } end_for_sourcelist(node,edge);
      } end_for_all_nodes(sgraph,node);
    compute_edge_length(sgraph); /* compute_edge_length darf erst aufgerufen */
                                 /* werden, wenn die Kantenattribute vom Typ */
                                 /* MyEdgeAttrs angelegt wurden.             */
#ifdef DEBUG
  printf("Ausgabe der Winkel um einen Knoten erfolgt im Uhrzeigersinn\n");
  printf("ab der ersten Kante auf oder nach Position (-1,0) relativ zum\n"); 
  printf("Knotenmittelpunkt.\n");
  printf("Kanten werden im Uhrzeigersinn sortiert ab Position (-1,0)\n");
  printf("ausgegeben. Winkel entlang des Kantenzugs sind gegen den\n");
  printf("Uhrzeigersinn berechnet.\n");
#endif
    compute_angles(sgraph); /* compute_angles darf nur aufgerufen werden, wenn */
                          /* die Kantenattribute vom Typ MyEdgeAttrs und die */
                          /* Knotenattribute vom Typ MyNodeAttrs angelegt    */
                          /* wurden.                                         */
    }
}



EdgeLengths ComputeEdgeLengths (Sgraph sgraph)
{
  Snode node;
  Sedge edge;
  MyEdgeAttrs edge_attrs;
   EdgeLengths edge_lengths;

  edge_lengths.shortest = 0.0;
  edge_lengths.longest  = 0.0;
  edge_lengths.variance = 0.0;
  edge_lengths.average  = 0.0;
  edge_lengths.ratio    = 0.0;

  if(sgraph==empty_sgraph)
    error("No graph selected.\n");
  else if(sgraph->nodes==empty_node)
    error("Graph is empty.\n");
  else if(countEdges(sgraph)==0)
    error("Graph contains no edges.\n");
  else
    {
    for_all_nodes(sgraph,node)
      {
      for_sourcelist(node,edge) if (sgraph->directed || unique_edge(edge)) { 
        edge_attrs=(MyEdgeAttrs)malloc(sizeof(struct myedgeattrs));
        edge_attrs->length=0.0;
        edge_attrs->undir_loop=0;
        edge_attrs->angles_list=NULL;
        set_edgeattrs(edge,make_attr(ATTR_DATA,(char *)edge_attrs));
      } end_for_sourcelist(node,edge);
    } end_for_all_nodes(sgraph,node);
    compute_edge_length(sgraph); /* compute_edge_length darf erst aufgerufen */
                                 /* werden, wenn die Kantenattribute vom Typ */
                                 /* MyEdgeAttrs angelegt wurden.             */
    edge_lengths.shortest = min_edge_length(sgraph);
    edge_lengths.longest  = max_edge_length(sgraph);
    edge_lengths.variance = variance(sgraph);
    edge_lengths.average   = average_edge_length(sgraph);
    if (edge_lengths.shortest > 0.0) {
      edge_lengths.ratio = edge_lengths.longest / edge_lengths.shortest;
    }
  }
  return edge_lengths;
}




Local int	nr_of_bends_of_edgeline(Edgeline line)
{
  Edgeline el;
  int      count;

  count = 0;
  for_edgeline(line,el) {
    count ++;
  } end_for_edgeline(line,el);
  return count-2; /* subtract 2 for the first and last point */
}


Global int count_nr_of_bends (Sgraph sgraph)
{
  int	count;
  Snode node;
  Sedge edge;

  count=0;
  if(sgraph==empty_sgraph)
    error("No graph selected.\n");
  else if(sgraph->nodes==empty_node)
    error("Graph is empty.\n");
  else {

    for_all_nodes(sgraph,node) {
      for_sourcelist(node,edge)	
      if(sgraph->directed || unique_edge(edge))
	  {
	  count += nr_of_bends_of_edgeline ((Edgeline)edge_get(graphed_edge(edge), EDGE_LINE));
      } end_for_sourcelist(node,edge);
    } end_for_all_nodes(sgraph,node);

  }

  return count;
}


Global void output_edge_lengths (Sgraph sgraph)
{
  Snode node;
  Sedge edge;
  int first = TRUE;

  for_all_nodes(sgraph,node) {
    for_sourcelist(node,edge) {
      if(sgraph->directed || unique_edge(edge)) {
	if (first) {
	  message ("%f", attr_data_of_type(edge,MyEdgeAttrs)->length);
	  first = FALSE;
	} else {
	  message ("\t%f", attr_data_of_type(edge,MyEdgeAttrs)->length);
	}
      }
    } end_for_sourcelist(node,edge);
  } end_for_all_nodes (sgraph, node);
}


void OutputAllEdgeLengths (Sgraph sgraph)
{
  Snode node;
  Sedge edge;
  MyEdgeAttrs edge_attrs;
  MyNodeAttrs node_attrs;

  if(sgraph==empty_sgraph)
    error("No graph selected.\n");
  else if(sgraph->nodes==empty_node)
    error("Graph is empty.\n");
  else if(countEdges(sgraph)==0)
    error("Graph contains no edges.\n");
  else
    {
    for_all_nodes(sgraph,node)
      {
      node_attrs=(MyNodeAttrs)malloc(sizeof(struct mynodeattrs));
      node_attrs->edge_list=NULL;
      set_nodeattrs(node,make_attr(ATTR_DATA,(char *)node_attrs));
      for_sourcelist(node,edge) if (sgraph->directed || unique_edge(edge)) {
        edge_attrs=(MyEdgeAttrs)malloc(sizeof(struct myedgeattrs));
        edge_attrs->length=0.0;
        edge_attrs->undir_loop=0;
        edge_attrs->angles_list=NULL;
        set_edgeattrs(edge,make_attr(ATTR_DATA,(char *)edge_attrs));
      } end_for_sourcelist(node,edge);
    } end_for_all_nodes(sgraph,node);
  }
  compute_edge_length(sgraph);
  countNodes(sgraph);
  countEdges(sgraph);

  message ("Edge lengths              :\t");
  output_edge_lengths (sgraph);
  message ("\n");
}
