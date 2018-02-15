#include <std.h>
#include <sgraph.h>
#include <slist.h>
#include <graphed.h>
#include <math.h>
#include "layout_info_export.h"


#undef DEBUG

/* find_in_edge_list sucht in der edge_list des Knotens die */
/* uebergebene Kante mit dem Attribut is_source.            */

Local Edge_List find_in_edge_list(Snode node, Sedge edge, int is_source)
{
  Edge_List list;
  
  list=(attr_data_of_type(node,MyNodeAttrs))->edge_list;
  while(list->edge->attrs.value.data!=edge->attrs.value.data || list->is_source!=is_source)
    list=list->suc;  /* Abfrage auf unique_edge funktioniert leider nicht. */
  return(list);
}


/* not_elem liefert 1, falls elem noch nicht in der Liste vorhanden ist, */
/* sonst 0.                                                              */

Local int not_elem(Sedge elem, Cycle_List cycle_list)
{
  Cycle_List list;
  bool first=1;

  list=cycle_list;
  while(first || list!=cycle_list)
    {
    if(list->edge->attrs.value.data==elem->attrs.value.data)
      return(0);
    list=list->suc;
    first=0;
    }
  return(1);
}


/* cycle bestimmt (ausgehend von edge_list) die Liste der Kanten, */
/* die eine Flaeche umranden.                                     */

Local Cycle_List cycle(Snode node, Edge_List edge_list)
{
  Cycle_List cycle_list,elem,succ,list;
  Edge_List next;

  cycle_list=(Cycle_List)malloc(sizeof(struct cycle_list));

  cycle_list->edge=edge_list->edge;
  cycle_list->is_source=edge_list->is_source;
  cycle_list->suc=cycle_list;
  cycle_list->pre=cycle_list;
  next=(edge_list->is_source) ? /* Es wird der Nachfolger bestimmt in der */
                                /* edge_list des Knotens, in den die Kante*/
                                /* muendet bzw. von dem sie ausgeht. Die  */
                                /* Nachfolgekante ist der Nachbar der     */
                                /* Kante gegen den Uhrzeigersinn.     */
       find_in_edge_list(sedge_real_target(edge_list->edge),edge_list->edge,0)->pre :
       find_in_edge_list(sedge_real_source(edge_list->edge),edge_list->edge,1)->pre;
  list=cycle_list;
  while(not_elem(next->edge,cycle_list)) /* Die neu gefundene Kante wird */
    {                                    /* eingefuegt.                  */
    succ=list->suc;
    list->suc=(Cycle_List)malloc(sizeof(struct cycle_list));
    list->suc->pre=list;
    list=list->suc;
    list->edge=next->edge;
    list->is_source=next->is_source;
    list->suc=succ;
    succ->pre=list;      
    next=(next->is_source) ?
         find_in_edge_list(sedge_real_target(next->edge),next->edge,0)->pre :
         find_in_edge_list(sedge_real_source(next->edge),next->edge,1)->pre;
    }
  /* Abbruchbedingung: Eine Kante wurde bereits das zweite Mal im Zyklus */
  /* gefunden.                                                           */
  /* Ein richtiger Zyklus liegt nur dann vor, wenn die Anfangskante      */
  /* gleich der Endkante ist und auch der zugehoerige Knoten gleich ist. */
  /* Dies wird mit dem Attribut is_source sichergestellt.                */
  if(next->edge==edge_list->edge && next->is_source==edge_list->is_source)
    return(cycle_list); 
  else /* Es wurde kein Zyklus gefunden. Die angelegte Liste wird geloescht */ 
    {  /* und NULL zurueckgegeben.                                          */
    while(cycle_list->suc!=cycle_list)
      {
      elem=cycle_list->pre;
      elem->pre->suc=cycle_list;
      cycle_list->pre=elem->pre;
      free(elem);
      }
    free(cycle_list);       
    return(NULL);
    }
}


/* already_in_dualgraph liefert 1 zurueck, falls im Dualgraph der   */
/* Zyklus bereits gefunden wurde, sonst 0.                          */

Local int already_in_dualgraph(Sgraph dualgraph, Cycle_List cycle_list)
{
  Cycle_List list,elem;
  Snode node;
  bool first;

  for_all_nodes(dualgraph,node) /* Die Knoten des Dualgraphen enthalten */
                                /* die Zyklusliste. Der Zyklus ist bis  */
                                /* auf den Startknoten und die Durch-   */
                                /* laufrichtung eindeutig bestimmt.         */
    {
    first=1;
    list=(attr_data_of_type(node,MyDualNodeAttrs))->cycle_list;
    elem=list;
    while(elem->edge->attrs.value.data!=cycle_list->edge->attrs.value.data && (first || elem!=list))
      {
      first=0;
      elem=elem->suc;      
      }
    if(elem->edge->attrs.value.data==cycle_list->edge->attrs.value.data)
      {  
      list=elem;
      while(elem->pre!=list && elem->pre->edge->attrs.value.data==cycle_list->suc->edge->attrs.value.data)
        {
        elem=elem->pre;
        cycle_list=cycle_list->suc;
        }
      if(elem->pre==list && elem->pre->edge->attrs.value.data==cycle_list->suc->edge->attrs.value.data)
        return(1);
      elem=list;
      while(elem->suc!=list && elem->suc->edge->attrs.value.data==cycle_list->suc->edge->attrs.value.data)
	{
        elem=elem->suc;
        cycle_list=cycle_list->suc;
        }
      if(elem->suc==list && elem->suc->edge->attrs.value.data==cycle_list->suc->edge->attrs.value.data)
        return(1);
      }
    } end_for_all_nodes(dualgraph,node);
  return(0);
}


/* make_dot_list ermittelt die Eckpunkte der Flaeche in cycle_list.  */
/* Die Kanten in cycle_list bilden eine geschlossene Flaeche.        */
/* make_dot_list ermittelt aus den Anfangs-, Zwischen- und Endpunkten*/
/* die Punktkoordinaten fuer die dot_list.                           */

Local Dot_List make_dot_list(Cycle_List cycle_list)
{
  Cycle_List c_list;
  Dot_List d_list,succ;
  Edgeline el,el_start;
  bool first=1;

  c_list=cycle_list;
  while(first || c_list!=cycle_list)  /* Nur bei der ersten Kante wird der  */
    {                                 /* Anfangspunkt berechnet. Er stimmt  */
                                      /* sonst mit dem Endpunkt der Vor-    */
                                      /* gaengerkante im Zyklus ueberein und*/
                                      /* wird nicht doppelt erfasst.        */
    if(first)   /* Anfangs- und Endpunkte einer Kante stimmen mit den */
      {         /* jeweiligen Knotenkoordinaten ueberein.             */
      first=0;
      d_list=(Dot_List)malloc(sizeof(struct dot_list));
      if(c_list->is_source)
        {
        d_list->x=((Snode)sedge_real_source(cycle_list->edge))->x;
        d_list->y=((Snode)sedge_real_source(cycle_list->edge))->y;
        }
      else
        {
        d_list->x=((Snode)sedge_real_target(cycle_list->edge))->x;
        d_list->y=((Snode)sedge_real_target(cycle_list->edge))->y;
        }
      d_list->pre=d_list;
      d_list->suc=d_list;
      }
    if(c_list->is_source) /* d.h. die Kante wird mit ihrer intern */
      {                   /* gespeicherten Richtung im Kantenzug durchlaufen */   
      el_start=(Edgeline)edge_get(graphed_edge(c_list->edge),EDGE_LINE);
      el=el_start->suc;
      while(el->suc!=el_start) /* Einfuegen des naechsten Punktes */
	{
        succ=d_list->suc;
        d_list->suc=(Dot_List)malloc(sizeof(struct dot_list));
        d_list->suc->pre=d_list;
        d_list=d_list->suc;
        d_list->x=edgeline_x(el);
        d_list->y=edgeline_y(el);
        d_list->suc=succ;
        succ->pre=d_list;
        el=el->suc;
	}
      if(c_list->suc!=cycle_list) /* Der Endpunkt in cycle_list ist gleich  */
	{                         /* dem Anfangspunkt und wird nicht doppelt*/
        succ=d_list->suc;         /* in die dot_list aufgenommen.           */ 
        d_list->suc=(Dot_List)malloc(sizeof(struct dot_list));
        d_list->suc->pre=d_list;
        d_list=d_list->suc;
        d_list->suc=succ;
        d_list->suc=succ;
        succ->pre=d_list;
        d_list->x=((Snode)sedge_real_target(c_list->edge))->x;
        d_list->y=((Snode)sedge_real_target(c_list->edge))->y;
        }
      }
    else /* d.h. die Kante wird im Kantenzug entgegen ihrer intern */ 
      {  /* gespeicherten Richtung durchlaufen                     */ 
      el_start=((Edgeline)edge_get(graphed_edge(c_list->edge),EDGE_LINE))->pre;
      el=el_start->pre;
      while(el->pre!=el_start) /* Einfuegen des nachsten Punktes */
	{
        succ=d_list->suc;
        d_list->suc=(Dot_List)malloc(sizeof(struct dot_list));
        d_list->suc->pre=d_list;
        d_list=d_list->suc;
        d_list->x=edgeline_x(el);
        d_list->y=edgeline_y(el);
        d_list->suc=succ;
        succ->pre=d_list;
        el=el->pre;
	}
      if(c_list->suc!=cycle_list) /* Einfuegen des Endpunktes, falls dieser nicht */
        {                         /* gleich dem Anfangspunkt in cycle_list ist */
        succ=d_list->suc;
        d_list->suc=(Dot_List)malloc(sizeof(struct dot_list));
        d_list->suc->pre=d_list;
        d_list=d_list->suc;
        d_list->suc=succ;
        succ->pre=d_list;
        d_list->x=((Snode)sedge_real_source(c_list->edge))->x;
        d_list->y=((Snode)sedge_real_source(c_list->edge))->y;
        }      
      }
    c_list=c_list->suc;
    }
  return(d_list);
}


/* min_y_coord liefert einen Zeiger auf den (am weitesten links */
/* gelegenen) Punkt mit kleinster y-Koordinate.                 */

Local Dot_List min_y_coord(Dot_List dot_list)
{
  Dot_List list,min=dot_list;

  if(dot_list->suc)
    {
    list=dot_list->suc;
    while(list!=dot_list)
      {
      if(list->y<min->y || (list->y==min->y && list->x<min->x))
        min=list;
      list=list->suc;
      }
    }
  return(min);
}


double absolute(double x)
{
  if(x>=0) return(x); else return(-x);
}


/* insert_dot fuegt nach dem uebergebenen Punkt einen neuen auf der */
/* Verbindungslinie zum Nachbarn in der Hoehe y ein.                */

Local void insert_dot(Dot_List dot_list, double y)
{
  Dot_List dot;
  dot=(Dot_List)malloc(sizeof(struct dot_list));
  dot->y=y;
  dot->x=dot_list->x + 
         (y-dot_list->y)*(dot_list->suc->x - dot_list->x) /
         (dot_list->suc->y-dot_list->y); /* Berechnen der neuen x-Koord. */
  dot->pre=dot_list;
  dot->suc=dot_list->suc;
  dot_list->suc=dot;
  dot->suc->pre=dot;
}


/* make_trapezoid fuegt in die dot_list einen Punkt auf der laengeren */
/* Seitenlinie des Vierecks ein, so dass ein Trapez entsteht.         */
/* Das Trapez besteht dann aus den Punkten                            */
/*        dot_list ---------- dot_list->suc                           */
/*                /          \                                        */
/*               /            \                                       */ 
/* dot_list->pre                dot_list->suc->suc                    */  

Local void make_trapezoid(Dot_List dot_list)
{
  if(dot_list->pre->y>dot_list->suc->suc->y)
    insert_dot(dot_list->pre,dot_list->suc->suc->y);
  else
    insert_dot(dot_list->suc,dot_list->pre->y);   
}


/* trapezoid bestimmt den Flaecheninhalt des Trapezes: dot_list->pre, */
/* dot_list, dot_list->suc, dot_list->suc->suc, wobei zunaechst eine  */
/* horizontale Grundlinie erzeugt wird. In die dot_list wird also     */
/* u.U. ein neuer Punkt aufgenommen.                                  */

Local double trapezoid(Dot_List dot_list)
{
  if(dot_list->pre->y!=dot_list->suc->suc->y)
    make_trapezoid(dot_list);
  return(0.5*(absolute(dot_list->x - dot_list->suc->x) +
                     absolute(dot_list->pre->x - dot_list->suc->suc->x)) *
                     (dot_list->pre->y - dot_list->y));
}


/* make_triangle fuegt in die dot_list einen Punkt auf der laengeren     */
/* Seitenkante des Dreiecks ein, so dass eine horizontale Basis entsteht.*/
/* Das Dreieck besteht dann aus den Punkten                              */
/*                      dot_list                                         */
/*                          /\                                           */
/*                         /  \                                          */ 
/*            dot_list->pre    dot_list->suc                             */

Local void make_triangle(Dot_List dot_list)
{
  if(dot_list->pre->y>dot_list->suc->y)
    insert_dot(dot_list->pre,dot_list->suc->y);
  else
    insert_dot(dot_list,dot_list->pre->y);
}


/* triangle bestimmt den Flaecheninhalt des Dreiecks dot_list->pre,    */
/* dot_list, dot_list->suc, wobei zunaechst eine horizontale Basis     */
/* erzeugt wird. U.U. wird in die dot_list ein neuer Punkt aufgenommen.*/

Local double triangle(Dot_List dot_list)
{
  if(dot_list->pre->y!=dot_list->suc->y)
    make_triangle(dot_list);
  return(0.5*(absolute(dot_list->pre->x - dot_list->suc->x) *
                      (dot_list->pre->y - dot_list->y)));
}


/* not_convex liefert einen Zeiger auf den linksaeussersten     */
/* hoechsten Punkt, der die horizontale Grundlinie der Trapez-  */
/* bzw. Dreiecksflaeche durchbricht.                            */

Local Dot_List not_convex(Dot_List dot_list, int is_trap)
{
  Dot_List list,min=NULL;
  double min_y=0.0,x1,x2;

  if(is_trap)
    {  
    /* Es wird ueberprueft, ob ein Punkt der dot_list innerhalb des */
    /* Bereichs                                                     */
    /*         dot_list ----------- dot_list->suc                   */
    /*                 /           \                                */
    /*                /             \                               */
    /*               /               \                              */
    /*  dot_list->pre                 dot_list->suc->suc            */
    /*     min_y=min(dot_list->pre->y,dot_list->suc->suc->y)        */
    /* liegt, wobei die laengere der beiden seitlichen Trapezkanten */
    /* bis auf die Hoehe min_y verkuerzt wird.                      */
    if(dot_list->pre->y>dot_list->suc->suc->y)
      min_y=dot_list->suc->suc->y;
    else
      min_y=dot_list->pre->y;
    list=dot_list->suc->suc->suc;
    if(list!=dot_list)
      {
      while(list!=dot_list->pre)  
        {
        if(list->y<min_y)
          {
          x1=dot_list->pre->x + 
             (list->y-dot_list->pre->y)*(dot_list->x - 
             dot_list->pre->x) / (dot_list->y-dot_list->pre->y);
          x2=dot_list->suc->suc->x + 
             (list->y-dot_list->suc->suc->y)*(dot_list->suc->x - 
             dot_list->suc->suc->x) / 
             (dot_list->suc->y-dot_list->suc->suc->y);
          if((x1<x2 && x1<list->x && list->x<x2) ||
             (x2<x1 && x2<list->x && list->x<x1))
            if(list->y<min_y || (list->y==min_y && list->x<min->x))
              {
              min_y=list->y;
              min=list;
	      }
   	  }
        list=list->suc;
        }
      }    
    }
  else /* is_triangle */
    {
    /* Es wird ueberprueft, ob ein Punkt der dot_list innerhalb des  */
    /* Bereichs                                                      */
    /*                 dot_list                                      */
    /*                    /\                                         */
    /*                   /  \                                        */
    /*                  /    \                                       */
    /*     dot_list->pre      dot_list->suc                          */
    /*     min_y=min(dot_list->pre->y,dot_list->suc->y)              */
    /* liegt, wobei die laengere der beiden seitlichen Dreieckskanten*/
    /* bis auf die Hoehe min_y verkuerzt wird.                       */
    if(dot_list->pre->y>dot_list->suc->y)
      min_y=dot_list->suc->y;
    else
      min_y=dot_list->pre->y;
    list=dot_list->suc->suc;
    if(list!=dot_list)
      {
      while(list!=dot_list->pre)  
        {
        if(list->y<min_y)
          {
          x1=dot_list->pre->x + (int)(floor(0.5 +
             (double)((list->y-dot_list->pre->y)*(dot_list->x - 
             dot_list->pre->x)) / (double)(dot_list->y-dot_list->pre->y)));
          x2=dot_list->suc->x + (int)(floor(0.5 +
             (double)((list->y-dot_list->suc->y)*(dot_list->x - 
             dot_list->suc->x)) / 
             (double)(dot_list->y-dot_list->suc->y)));
          if((x1<x2 && x1<list->x && list->x<x2) ||
             (x2<x1 && x2<list->x && list->x<x1))
            if(list->y<min_y || (list->y==min_y && list->x<min->x))
              {
              min_y=list->y;
              min=list;
	      }
	  } 
        list=list->suc; 
        }
      }     
    }
  return(min);
}


/* top_trap berechnet die Trapezflaeche, die der Punkt in dot_list mit   */
/* den Nachbarn einschliesst, allerdings nur bis zur uebergebenen Hoehe. */
/* Die berechnete Flaeche ist dann folgende (mit @ markiert):            */
/*        dot_list ---------- dot_list->suc                              */
/*                /@@@@@@@@@@\                                           */
/*               /@@@@@@@@@@@@\                                          */ 
/*           y  ----------------                                         */
/*             /                \                                        */
/*            /                  dot_list->suc->suc                      */ 
/*  dot_list->pre                                                        */
/* Die dot_list wird anschliessend so veraendert, dass die Punkte        */
/* dot_list und dot_list->suc neu positioniert werden als Schnittpunkte  */
/* der Seitenkanten des Trapezes mit der horizontalen Gerade auf Hoehe y.*/

Local double top_trap(Dot_List dot_list, double y)
{
  double face=0.0,x1,x2; 

  if(dot_list->y<y)
    {
    x1=dot_list->pre->x + 
       (y-dot_list->pre->y)*(dot_list->x - dot_list->pre->x) / 
       (dot_list->y-dot_list->pre->y);
    x2=dot_list->suc->suc->x + 
       (y-dot_list->suc->suc->y)*(dot_list->suc->x - dot_list->suc->suc->x) / 
       (dot_list->suc->y-dot_list->suc->suc->y);
    face=0.5*((absolute(x1 - x2) + absolute(dot_list->x - dot_list->suc->x)) * 
         (y - dot_list->y));
    dot_list->x=x1;
    dot_list->y=y;
    dot_list->suc->x=x2;
    dot_list->suc->y=y;
    }
  return(face);
}


/* top_triangle berechnet die Dreiecksflaeche, die der Punkt in dot_list */
/* mit den Nachbarn einschliesst, allerdings nur bis zur uebergebenen    */
/* Hoehe.                                                                */
/* Die berechnete Flaeche ist dann folgende (mit @ markiert):            */
/*                 dot_list                                              */
/*                    /\                                                 */
/*                   /@@\                                                */
/*                  /@@@@\                                               */ 
/*           y  ----------------                                         */
/*                /        \                                             */
/*               /          dot_list->suc                                */ 
/*  dot_list->pre                                                        */
/* Die dot_list wird dann so veraendert, dass der Punkt dot_list durch   */
/* den Schnittpunkt der Dreieckskante [dot_list->pre,dot_list] mit der   */
/* horizontalen Gerade auf Hoehe y ersetzt wird und als Nachfolger der   */
/* Schnittpunkt der anderen Dreieckskante mit der Horizontalen eingefuegt*/
/* wird.                                                                 */

Local double top_triangle(Dot_List dot_list, double y)
{
  Dot_List succ;
  double face=0.0,x1,x2;

  if(dot_list->y<y)
    {
    x1=dot_list->pre->x + 
       (y-dot_list->pre->y)*(dot_list->x - dot_list->pre->x) / 
       (dot_list->y-dot_list->pre->y);
    x2=dot_list->suc->x + 
       (y-dot_list->suc->y)*(dot_list->x - dot_list->suc->x) / 
       (dot_list->y-dot_list->suc->y);
    face=0.5*(absolute(x1 - x2) * (y - dot_list->y));
    dot_list->x=x1;
    dot_list->y=y;
    succ=dot_list->suc;
    dot_list->suc=(Dot_List)malloc(sizeof(struct dot_list));
    dot_list->suc->pre=dot_list;
    dot_list->suc->suc=succ;
    dot_list->suc->x=x2;
    dot_list->suc->y=y;
    succ->pre=dot_list->suc;
    }
  return(face);
}


Local double inner_face(Dot_List *ptr_dot_list);

/* sweep_line berechnet die Flaeche zwischen den Verbindungskanten*/
/* des uebergebenen hoechsten Punktes zu benachbarten tiefer      */
/* gelegenen Punkten. Dabei wird die dot_list veraendert.         */

Local double sweep_line(Dot_List *ptr_dot_list)
{
  Dot_List dot_list,list,elem,new_elem,next,dot_list1,dot_list2;
  double face=0.0;

  dot_list=*ptr_dot_list,dot_list1,dot_list2;
  list=dot_list->suc;          
  if(list->y==dot_list->y)
    /* d.h. das Trapez hat die Form                                       */
    /*        dot_list ---------- dot_list->suc                           */
    /*                /          \                                        */
    /*               /            \                                       */ 
    /* dot_list->pre                dot_list->suc->suc                    */
    {
    while(list->suc->y==dot_list->y) /* Benachbarte innere Punkte auf gleicher*/
      {                              /* Hoehe werden geloescht.               */
      dot_list->suc=list->suc;
      list->suc->pre=dot_list;
      free(list);
      list=dot_list->suc;
      }
    if((elem=not_convex(dot_list,1))==NULL)
      {
      face=trapezoid(dot_list);
      dot_list->pre->suc=dot_list->suc->suc; /* Die beiden oberen Punkte */
      dot_list->suc->suc->pre=dot_list->pre; /* des Trapezes werden aus  */
      *ptr_dot_list=dot_list->pre;           /* der dot_list geloescht.  */
      free(dot_list->suc);
      free(dot_list);
      }
    else 
      {
      face=top_trap(dot_list,elem->y);     
      dot_list1=dot_list;
      dot_list2=dot_list->suc;
      /* Die dot_list wird geteilt und in zwei Zyklen ueberfuehrt, deren  */
      /* Flaechen rekursiv berechnet werden.                              */
      /* Das spezielle Element elem bildet die Nahtstelle der zwei Zyklen.*/
      dot_list1->suc=elem;
      next=elem->pre;
      elem->pre=dot_list1;
      new_elem=(Dot_List)malloc(sizeof(struct dot_list));
      new_elem->x=elem->x;
      new_elem->y=elem->y;
      new_elem->suc=dot_list2;
      new_elem->pre=next;
      dot_list2->pre=new_elem;
      next->suc=new_elem;
      face=face+inner_face(&dot_list1)+inner_face(&dot_list2);
      *ptr_dot_list=NULL;
      }   
    }
  else
    { 
    list=dot_list->pre;
    if(list->y==dot_list->y)
    /* d.h. das Trapez hat die Form                                       */
    /*        dot_list ---------- dot_list->pre                           */
    /*                /          \                                        */
    /*               /            \                                       */ 
    /* dot_list->suc                dot_list->pre->pre                    */   
      {
      while(list->pre->y==dot_list->y) 
        {
        dot_list->pre=list->pre;
        list->pre->suc=dot_list;
        free(list);
        list=dot_list->pre;
        }
      if((elem=not_convex(dot_list->pre,1))==NULL)
        {
        face=trapezoid(dot_list->pre);
        dot_list->pre->pre->suc=dot_list->suc; /* Die beiden oberen Punkte */
        dot_list->suc->pre=dot_list->pre->pre; /* des Trapezes werden aus  */
                                               /* der dot_list geloescht.  */
        *ptr_dot_list=dot_list->suc;
        free(dot_list->pre);
        free(dot_list);
        }
      else 
        {
        face=top_trap(dot_list->pre,elem->y);
        /* Die dot_list wird geteilt und in zwei Zyklen ueberfuehrt, deren  */
        /* Flaechen rekursiv berechnet werden.                              */
        /* Das spezielle Element elem bildet die Nahtstelle der zwei Zyklen.*/
        dot_list1=dot_list;
        dot_list2=dot_list->pre;
        dot_list1->pre=elem;
        next=elem->suc;
        elem->suc=dot_list1;
        new_elem=(Dot_List)malloc(sizeof(struct dot_list));
        new_elem->x=elem->x;
        new_elem->y=elem->y;
        new_elem->pre=dot_list2;
        new_elem->suc=next;
        dot_list2->suc=new_elem;
        next->pre=new_elem;
        face=face+inner_face(&dot_list1)+inner_face(&dot_list2);
        *ptr_dot_list=NULL;
	}
      }
    else /* Der hoechste Punkt ist eindeutig, die Nachbarn liegen tiefer. */
      {  /* Dreiecksfall!                                                 */
      if((elem=not_convex(dot_list,0))==NULL)
	{
        face=triangle(dot_list);
        dot_list->pre->suc=dot_list->suc; /* Die Dreiecksspitze wird aus */
        dot_list->suc->pre=dot_list->pre; /* der dot_list geloescht.     */
        *ptr_dot_list=dot_list->pre;
        free(dot_list);
	}
      else
	{ 
        face=top_triangle(dot_list,elem->y);
        dot_list1=*ptr_dot_list;
        dot_list2=dot_list1->suc;
        /* Die dot_list wird geteilt und in zwei Zyklen ueberfuehrt, deren  */
        /* Flaechen rekursiv berechnet werden.                              */
        /* Das spezielle Element elem bildet die Nahtstelle der zwei Zyklen.*/
        dot_list1->suc=elem;
        new_elem=(Dot_List)malloc(sizeof(struct dot_list));
        new_elem->x=elem->x;
        new_elem->y=elem->y;
        next=elem->pre;
        elem->pre=dot_list1;
        new_elem->suc=dot_list2;
        new_elem->pre=next;
        dot_list2->pre=new_elem;
        next->suc=new_elem;
        face=face+inner_face(&dot_list1)+inner_face(&dot_list2);
        *ptr_dot_list=NULL;
	}
      }
    }      
  return(face);
}


/* del_dot_list loescht die gesamte Liste. */

Local void del_dot_list(Dot_List *ptr_dot_list)
{
  Dot_List elem=(*ptr_dot_list)->pre;

  while(elem->suc!=elem)
    {
    elem->pre->suc=*ptr_dot_list;
    (*ptr_dot_list)->pre=elem->pre;
    free(elem);
    elem=(*ptr_dot_list)->pre;
    }  
  free(*ptr_dot_list);
  *ptr_dot_list=NULL;
}


/* all_same_y_coord liefert 1, falls alle Elemente der dot_list die */
/* gleiche y-Koordinate haben.                                      */

Local bool all_same_y_coord(Dot_List dot_list)
{
  Dot_List dot;

  dot=dot_list->suc;
  while(dot!=dot_list)
    {
    if(dot->y!=dot->pre->y)
      return(0);
    dot=dot->suc;
    }
  return(1);     
}


/* inner_face bestimmt die Flaeche, die die Punkte in dot_list aufspannen. */

Local double inner_face(Dot_List *ptr_dot_list)
{
  double face=0.0;

  while(*ptr_dot_list && (*ptr_dot_list)->suc->suc!=*ptr_dot_list) /* mind. 3 Punkte */
    {
    *ptr_dot_list=min_y_coord(*ptr_dot_list);
    if(all_same_y_coord(*ptr_dot_list))
      {
      del_dot_list(ptr_dot_list);
      return(face);                   
      }
    face+=sweep_line(ptr_dot_list);
    }
  if(*ptr_dot_list) 
    del_dot_list(ptr_dot_list);
  return(face);
}


/* copy erzeugt eine Kopie der uebergebenen Liste. NB: Die Liste darf */
/* nicht leer sein.                                                   */

Local Dot_List copy(Dot_List dot_list)
{
  Dot_List list,succ,elem=dot_list;

  list=(Dot_List)malloc(sizeof(struct dot_list));
  list->x=elem->x;
  list->y=elem->y;
  list->pre=list;
  list->suc=list;
  elem=elem->suc;
  while(elem!=dot_list)
    {
    succ=list->suc;
    list->suc=(Dot_List)malloc(sizeof(struct dot_list));
    list->suc->pre=list;
    list=list->suc;
    list->suc=succ;
    list->x=elem->x;
    list->y=elem->y;
    succ->pre=list;
    elem=elem->suc;
    }
  return(list);
}


Local void print_dualgraph(Sgraph dualgraph)
{
#ifdef DEBUG
  Snode node;
  Cycle_List c_list;
  Angles_List a_list;
  int first;

  for_all_nodes(dualgraph,node)
    {
    first=1;
    message("Face:\t%.2f\n",(attr_data_of_type(node,MyDualNodeAttrs))->face);
    c_list=attr_data_of_type(node,MyDualNodeAttrs)->cycle_list;
    message("Surrounding edges starting at edge <%s> of node <%s, nr %d>:",
            attr_data_of_type(node,MyDualNodeAttrs)->start_edge->label,
            attr_data_of_type(node,MyDualNodeAttrs)->start_node->label,
            attr_data_of_type(node,MyDualNodeAttrs)->start_node->nr);
    while(first || c_list!=attr_data_of_type(node,MyDualNodeAttrs)->cycle_list)
      {
      first=0;
      message("\t%s",c_list->edge->label);
      c_list=c_list->suc;
      }
    message("\n");
    a_list=attr_data_of_type(node,MyDualNodeAttrs)->angles_list;
    message("Inner angles in cycle:\n");
    while(a_list)
      {
      message("\t%.2f",a_list->angle);
      a_list=a_list->suc; 
      }
    message("\n");
    } end_for_all_nodes(dualgraph,node);
#endif
}


/* angles_double berechnet den nicht notwendig spitzen Winkel zwischen */
/* den Strecken [erster uebergebener Punkt, Mittelpunkt] und           */
/* [Mittelpunkt, zweiter uebergebener Punkt] (im Uhrzeigersinn) fuer   */
/* Argumente vom Typ double.                                           */

Local double angles_double(double first_x, double first_y, double middle_x, double middle_y, double second_x, double second_y)
{
  double pi=atan(1.0)*4.0,angle1,angle2,result;

  if(first_x==second_x && first_y==second_y)
     return(0.0);                 /* oder 2.0*pi */
  first_x=first_x-middle_x;
  first_y=first_y-middle_y;
  second_x=second_x-middle_x;
  second_y=second_y-middle_y;
  angle1=acos(first_x /
         sqrt(pow(first_x,2.0) + pow(first_y,2.0)));
  angle2=acos(second_x /
         sqrt(pow(second_x,2.0) + pow(second_y,2.0)));
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


/* inner_angles berechnet die Innenwinkel einer umrandeten Flaeche, */
/* die durch die dot_list gegeben ist.                              */

Local Angles_List inner_angles(Dot_List dot_list, int clocksense)
{
  Dot_List list=dot_list;
  Angles_List a_list,angles_list=NULL;
  double pi=atan(1.0)*4.0;

  angles_list=(Angles_List)malloc(sizeof(struct angles_list));
  angles_list->angle=clocksense ?
                       2.0*pi-angles_double(list->x,list->y,list->suc->x,list->suc->y,
                              list->suc->suc->x,list->suc->suc->y) :
                       angles_double(list->x,list->y,list->suc->x,
                                     list->suc->y,list->suc->suc->x,list->suc->suc->y);
  angles_list->suc=NULL;
  list=list->suc;
  a_list=angles_list;
  while(list!=dot_list)   
    {
    a_list->suc=(Angles_List)malloc(sizeof(struct angles_list));
    a_list=a_list->suc;
    a_list->angle=clocksense ?
                    2.0*pi-angles_double(list->x,list->y,list->suc->x,list->suc->y,
                                  list->suc->suc->x,list->suc->suc->y): 
                    angles_double(list->x,list->y,list->suc->x,list->suc->y,
                                  list->suc->suc->x,list->suc->suc->y);
    a_list->suc=NULL;
    list=list->suc;
    }
  return(angles_list);
}


/* common_edge bestimmt, ob die beiden Zyklen eine gemeinsame Kante */
/* besitzen.                                                        */

Local int common_edge(Cycle_List c_list1, Cycle_List c_list2)
{
  Cycle_List elem1,elem2;
  int first1,first2=1;

  elem1=c_list1;
  while(first1 || elem1!=c_list1)
    {
    first1=0;
    first2=1;
    elem2=c_list2;
    while(first2 || elem2!=c_list2)
      {
      first2=0;
      if(elem1->edge->attrs.value.data==elem2->edge->attrs.value.data)
        return(1);
      elem2=elem2->suc;
      }    
    elem1=elem1->suc;      
    }
  return(0);
}


/* Benachbarte Flaechen im Dualgraph werden durch eine ungerichtete */
/* Kante verbunden.                                                 */

Local void find_adjacencies(Sgraph dualgraph)
{
  Snode snode,tnode;

  if(dualgraph)
  for_all_nodes(dualgraph,snode)
    {
    for_all_nodes(dualgraph,tnode)
      {	
      if(snode!=tnode && 
         common_edge(attr_data_of_type(snode,MyDualNodeAttrs)->cycle_list,
                     attr_data_of_type(tnode,MyDualNodeAttrs)->cycle_list))
        make_edge(snode,tnode,make_attr(ATTR_DATA,(char *)NULL));
      } end_for_all_nodes(dualgraph,tnode);
    } end_for_all_nodes(dualgraph,snode);
}


/* compute_inner_faces_and_angles berechnet die umrandeten Flaechen */
/* eines Graphen mitsamt deren Innenwinkeln.                        */

Global Sgraph compute_inner_faces_and_angles(Sgraph sgraph)
{
  Sgraph dualgraph=0;
  Snode node;
  Edge_List edge_list,list;
  Cycle_List cycle_list,elem;
  Dot_List dot_list,dot_list_copy,hi_dot;
  MyDualNodeAttrs dualnodeattrs;
  double pi=atan(1.0)*4.0;
  bool first;

  if(sgraph==empty_sgraph)
    error("No graph selected.\n");
  else if(sgraph->nodes==empty_node)
    error("Graph is empty.\n");
  else if(countEdges(sgraph)==0)
    message("Graph contains no edges.\n");
  else if(nr_of_crossings(sgraph)>0) {
    ;
  } else {

    compute_my_attrs(sgraph);
    dualgraph=make_graph(make_attr(ATTR_DATA,NULL));
    dualgraph->directed=0;
    for_all_nodes(sgraph,node) {
      edge_list=(attr_data_of_type(node,MyNodeAttrs))->edge_list;
      if(edge_list!=NULL) {
        list=edge_list;
        first=1;
        while(first || list!=edge_list) {
          cycle_list=cycle(node,list);
          if(cycle_list) {
            if(!already_in_dualgraph(dualgraph,cycle_list)) { 
              dot_list=make_dot_list(cycle_list);
              dot_list_copy=copy(dot_list);
              dualnodeattrs=(MyDualNodeAttrs)malloc(sizeof(struct mydualnodeattrs));
              dualnodeattrs->face=inner_face(&dot_list_copy);
              dualnodeattrs->start_node=node;
              dualnodeattrs->start_edge=cycle_list->edge;
              dualnodeattrs->cycle_list=cycle_list;
              hi_dot=min_y_coord(dot_list);
              if(angles_double(hi_dot->pre->x,hi_dot->pre->y,hi_dot->x,hi_dot->y,
                        hi_dot->suc->x,hi_dot->suc->y)<=pi) /* wichtig wegen */
                                                            /* Umlaufsinn    */
                dualnodeattrs->angles_list=inner_angles(dot_list->suc,0);
              else
                dualnodeattrs->angles_list=inner_angles(dot_list->suc,1);
              make_node(dualgraph,make_attr(ATTR_DATA,(char *)dualnodeattrs));
              del_dot_list(&dot_list);
	    } else {
              while(cycle_list->suc!=cycle_list) {
                elem=cycle_list->pre;
                elem->pre->suc=cycle_list;
                cycle_list->pre=elem->pre;
                free(elem);
	      }
              free(cycle_list);               
	    }
	  }
          first=0;         
          list=list->suc;
	}
      } 
    } end_for_all_nodes(sgraph,node);
    print_dualgraph(dualgraph);
    find_adjacencies(dualgraph); 
  } 
  return(dualgraph);
}


/* maximum_face findet den Knoten, der die Gesamtflaeche enthaelt. */

Local Snode maximum_face(Sgraph dualgraph)
{
  Snode d_node,max=0;
  double max_face=0.0;
  
  if(dualgraph)
    {
    for_all_nodes(dualgraph,d_node)
      {
      if(max_face<(attr_data_of_type(d_node,MyDualNodeAttrs))->face)      
        {
        max_face=(attr_data_of_type(d_node,MyDualNodeAttrs))->face;
        max=d_node;
        }  
      } end_for_all_nodes(dualgraph,d_node);
    }
  return(max);
}     

/* min_max_angles berechnet den kleinsten und groessten Winkel im Dualgraphen. */

Angles_Value	ComputeAngleInfo (Sgraph dualgraph)
{
  Snode  d_node,max_face;
  double min = HUGE_VAL, max = 0.0,
         sum = 0.0,  sum_square = 0.0;
  Angles_List list;
  Angles_Value angles;
  int count_angles = 0;

  angles.min=0.0;
  angles.max=0.0;
  angles.variance = 0.0;
  angles.average = 0.0;
  angles.ratio = 0.0;
  if(!dualgraph)
    {
    return(angles);
    }

  max_face=maximum_face(dualgraph);
  for_all_nodes(dualgraph,d_node) {
    if(d_node!=max_face ||
       first_node_in_graph(dualgraph)==last_node_in_graph(dualgraph)) {
      list=(attr_data_of_type(d_node,MyDualNodeAttrs))->angles_list;      
      while(list) {
	count_angles ++;
	sum += list->angle;
	sum_square += list->angle * list->angle;
        if(list->angle<min)
          min=list->angle;
        if(list->angle>max)
          max=list->angle;
        list=list->suc;
      }
    }
  } end_for_all_nodes(dualgraph,d_node);

  if(min>=0.0) {
    angles.min=min;
    angles.max=max;
    angles.average = sum / count_angles;
    angles.variance = sum_square / count_angles - (sum*sum) / (count_angles * count_angles);
    if (min > 0.0) { angles.ratio = angles.max / angles.min; }
  }  

  return(angles);  
}


/* min_max_face berechnet die kleinste und groesste Flaeche im Dualgraphen. */

Faces_Value	ComputeFaceInfo (Sgraph dualgraph)
{
  Snode d_node,max_face;
  double min = HUGE_VAL, max = 0.0,
         sum = 0.0,  sum_square = 0.0;
  Faces_Value faces;  
  double face;  
  int count_faces = 0;

  faces.min = HUGE_VAL;
  faces.max = 0.0;
  faces.average = 0.0;
  faces.variance = 0.0;
  faces.ratio = 0.0;

  if(!dualgraph) {
    return(faces);
  }      

  max_face=maximum_face(dualgraph);
  for_all_nodes(dualgraph,d_node) {
    if(d_node!=max_face ||
       first_node_in_graph(dualgraph)==last_node_in_graph(dualgraph)) {
      face=(attr_data_of_type(d_node,MyDualNodeAttrs))->face;
      count_faces ++;
      sum += face;
      sum_square += face*face;
      if(face<min) {
        min=face;
      }
      if(face>max) {
        max=face;
      }
    }
  } end_for_all_nodes(dualgraph,d_node);

  if(min >= 0.0) {
    faces.min=min;
    faces.max=max;
    faces.average = sum / count_faces;
    faces.variance = sum_square / count_faces - (sum*sum) / (count_faces * count_faces);
    if (min > 0.0) { faces.ratio = faces.max / faces.min; }
  }  
  return(faces);  
}



Global void output_all_faces (Sgraph dualgraph)
{
  Snode  node;
  int    first = TRUE;

  double face;
  Snode  max_face;

  message ("Faces                     :\t");
  if(dualgraph) {

    max_face=maximum_face(dualgraph);

    for_all_nodes (dualgraph,node) {
      if (node != max_face ||
	  first_node_in_graph(dualgraph)==last_node_in_graph(dualgraph)){

	face = (attr_data_of_type(node,MyDualNodeAttrs))->face;
      
	if (first) {
	  message ("%f", face);
	  first = FALSE;
	} else {
	  message ("\t%f", face);
	}
      }
    } end_for_all_nodes (dualgraph, node);
  }

  message ("\n");
}



Global void output_all_angles (Sgraph dualgraph)
{
  Snode  node;
  int    first = TRUE;

  Angles_List  angles_list;
  Snode  max_face;

  message ("Angles                    :\t");
  if(dualgraph) {

    max_face=maximum_face(dualgraph);

    for_all_nodes (dualgraph,node) {
      if (node != max_face ||
	  first_node_in_graph(dualgraph)==last_node_in_graph(dualgraph)){

	angles_list = (attr_data_of_type(node,MyDualNodeAttrs))->angles_list;
      
	while (angles_list) {
	  if (first) {
	    message ("%f", angles_list->angle);
	    first = FALSE;
	  } else {
	    message ("\t%f", angles_list->angle);
	  }
	  angles_list = angles_list->suc;
	}
      }
    } end_for_all_nodes (dualgraph, node);
  }

  message ("\n");
}
