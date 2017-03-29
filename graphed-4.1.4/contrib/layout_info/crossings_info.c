#include <std.h>
#include <sgraph.h>
#include <slist.h>
#include <graphed.h>
#include <math.h>
#include "layout_info_export.h"


#undef DEBUG

/* crossing liefert 7 zurueck, falls sich zwei Kantenabschnitte im         */
/* Graphen (teilweise) ueberlappen, 6,5,4,3, falls der Schnittpunkt ein-   */
/* seitig Anfangs- oder Endpunkt ist, 2, falls es einen echten Schnittpunkt*/
/* gibt, 1, falls der Schnittpunkt gemeinsamer Anfangs- oder Endpunkt ist, */
/* 0, falls sich die Kanten nicht schneiden.                               */
/* Die uebergebenen Parameter sind die Koordinaten des Anfangs- und End-   */
/* punktes des ersten bzw. zweiten Kantenabschnitts.                       */

Local int crossing(int x11, int y11, int x12, int y12, int x21, int y21, int x22, int y22)
{ 
  double r,s;

  if(!((x11==x12 && y11==y12) || (x21==x22 && y21==y22))) /* zwei echte Streckenabschnitte */
    { 
      if((x12-x11)*(y22-y21) - (y12-y11)*(x22-x21)==0) /* parallel, Det.=0 */
	{
        if((y11-y21)*(x12-x11) + (x21-x11)*(y12-y11)==0) 
	  {
          if(y11==y21) /*Steigung 0 */
	    {
            if(maximum(x11,x12)>=minimum(x21,x22) && minimum(x11,x12)<=maximum(x21,x22))
              {
              if(maximum(x11,x12)==minimum(x21,x22) || minimum(x11,x12)==maximum(x21,x22))
                return(1); /* Der Anfangspunkt eines Kantenabschnitts faellt*/
                           /* mit dem Endpunkt des anderen zusammen.        */ 
              else 
                return(7); /* Ueberlappung */
	      }
            return(0); /* kein Schnittpunkt */
	    }
          else if(x11==x12) /*Steigung unendlich*/
            {
            if(maximum(y11,y12)>=minimum(y21,y22) && minimum(y11,y12)<=maximum(y21,y22))
              {
              if(maximum(y11,y12)==minimum(y21,y22) || minimum(y11,y12)==maximum(y21,y22))
                return(1);
              else 
                return(7);
	      }
            return(0);
	    }
          else 
            {
            if(((x11==x21 && y11==y21) ||

                (x11==x22 && y11==y22) ||
                (x12==x21 && y12==y21) ||
                (x12==x22 && y12==y22)) &&
               (maximum(x11,x12)<=minimum(x21,x22) || minimum(x11,x12)>=maximum(x21,x22)))
                return(1);  /* gemeinsamer Anfangs- oder Endpunkt */
            if(minimum(x11,x12)<maximum(x21,x22) && maximum(x11,x12)>minimum(x21,x22))
              return(7); /* Ueberlappung */
	    }
	  }
        return(0); /* kein Schnittpunkt */
        }
      else
	{
        s=((double)((y11-y21)*(x12-x11) + (x21-x11)*(y12-y11))) /
           ((double)((x12-x11)*(y22-y21) - (y12-y11)*(x22-x21)));
        if(x12-x11!=0)
          r=((double)(x21 - x11 + s*(x22-x21))) / ((double)(x12-x11));
        else
          r=((double)(y21 - y11 + s*(y22-y21))) / ((double)(y12-y11));
        if((r==0 || r==1) && (s==0 || s==1))
          return(1); /* gemeinsamer Anfangs- oder Endpunkt */
        else if(s==1 && 0<r && r<1) 
          return(6); /* Schnittpunkt liegt im Enpunkt des zweiten Kantenabschnitts */
        else if(s==0 && 0<r && r<1) 
          return(5); /* Schnittpunkt liegt im Anfangspunkt des zweiten Kantenabschnitts */
        else if(r==1 && 0<s && s<1) 
          return(4); /* Schnittpunkt liegt im Enpunkt des ersten Kantenabschnitts */
        else if(r==0 && 0<s && s<1) 
          return(3); /* Schnittpunkt liegt im Anfangspunkt des ersten Kantenabschnitts */
        else if(0<r && r<1 && 0<s && s<1)
          return(2); /* echter Schnittpunkt */
        else
          return(0); /* kein Schnittpunkt */
        }
    }
  else if(x11==x12 && y11==y12 && !(x21==x22 && y21==y22)) /* erster Kanten- */
         /* abschnitt ist nur ein Punkt, d.h. Kante der Laenge 0 */
    { 
    if(x22==x21) /* zweiter Kantenabschnitt hat Steigung unendlich */
      {
      if(x11==x21 && (y21==y11 || y22==y11))
        return(1); /* gemeinsamer Anfangs- oder Endpunkt */
      else if(x11==x21 && minimum(y21,y22)<=y11 && maximum(y21,y22)>=y11)
        return(2); /* echter Schnittpunkt */
      else
        return(0); /* kein Schnittpunkt */
      } 
    else if(y21==y22) /* zweiter Kantenabschnitt hat Steigung 0 */
      {
      if(y11==y21 && (x21==x11 || x22==x11))
        return(1); /* gemeinsamer Anfangs- oder Endpunkt */
      else if(y11==y21 && minimum(x21,x22)<=x11 && maximum(x21,x22)>=x11)
        return(2); /* echter Schnittpunkt */
      else
        return(0); /* kein Schnittpunkt */
      }
    r=((double)(x11-x21)) / ((double)(x22-x21));
    s=((double)(y11-y21)) / ((double)(y22-y21));
    if(r==s)
      {
      if(r==0 || r==1)
        return(1);
      else if(0<r && r<1)
        return(2);
      }
    return(0);
    }
  else if(!(x11==x12 && y11==y12) && x21==x22 && y21==y22) /* zweiter Kanten- */
         /* abschnitt ist nur ein Punkt, d.h. Kante der Laenge 0 */
    {
    if(x12==x11)
      {
      if(x11==x21 && (y11==y21 || y12==y21))
        return(1);
      else if(x11==x21 && minimum(y11,y12)<=y21 && maximum(y11,y12)>=y21)
        return(2);
      else
        return(0);
      } 
    else if(y11==y12)
      {
      if(y11==y21 && (x11==x21 || x12==x21))
        return(1);
      else if(y11==y21 && minimum(x11,x12)<=x21 && maximum(x11,x12)>=x21)
        return(2);
      else
        return(0);
      }
    r=((double)(x21-x11)) / ((double)(x12-x11));
    s=((double)(y21-y11)) / ((double)(y12-y11));
    if(r==s)
      {
      if(r==0 || r==1)
        return(1);
      else if(0<r && r<1)
        return(2);
      }
    return(0);
    }
  return(0); /* kein Schnittpunkt */
}


/* nr_of_crossings berechnet die Haeufigkeit paarweiser Ueberkreuzungen */
/* von Kanten im Graphen. Diese muss nicht identisch sein mit der       */
/* Anzahl der Kreuzungspunkte.                                          */ 

Global int nr_of_crossings(Sgraph sgraph)
{
  int previous1_x,previous1_y,previous2_x,previous2_y,
      first1=1,first2=1,count=0,count2=0,count4=0,
      param1,param2,param3,param4,param5,param6,param7,param8;
  Snode node1,node2;
  Sedge edge1,edge2;
  Edgeline el1,el2,el_start1,el_start2;

  if(sgraph==empty_sgraph)
    {
    error("No graph selected.\n");
    return(0);
    }
  else if(sgraph->nodes==empty_node)
    {
    error("Graph is empty.\n");
    return(0);
    }
  else
    {
    for_all_nodes(sgraph,node1)
      {
      for_sourcelist(node1,edge1)
      if(sgraph->directed || unique_edge(edge1))
        { 
        first1=1;
        el_start1=(Edgeline)edge_get(graphed_edge(edge1),EDGE_LINE);
        for_edgeline(el_start1,el1)
	  {
          if(!first1) /* Im ersten Durchlauf der aeusseren for-Schleife */
                      /* wird der Anfangspunkt des jeweils betrachteten */
                      /* Kantenabschnitts gespeichert.                  */
	    {
            for_all_nodes(sgraph,node2)
	      {
              for_sourcelist(node2,edge2)
              if(sgraph->directed || unique_edge(edge2))
	        {
                first2=1;
                el_start2=(Edgeline)edge_get(graphed_edge(edge2),EDGE_LINE);   
                for_edgeline(el_start2,el2)
		  {
                  if(!first2 && el1!=el2) /* nur paarweise verschiedene Kantenabschnitte */
                      /* Im ersten Durchlauf der inneren for-Schleife   */
                      /* wird der Anfangspunkt des jeweils betrachteten */
                      /* Kantenabschnitts gespeichert.                  */
	            {
                    if(el1->pre==el_start1)
                      {
                      param1=((Snode)sedge_real_source(edge1))->x;   
                      param2=((Snode)sedge_real_source(edge1))->y;
		      }
                    else
                      {
                      param1=previous1_x; 
                      param2=previous1_y;
		      }
                    if(el1->suc==el_start1)
                      {
                      param3=((Snode)sedge_real_target(edge1))->x;   
                      param4=((Snode)sedge_real_target(edge1))->y;
		      }
                    else
                      {
                      param3=edgeline_x(el1);
                      param4=edgeline_y(el1);
		      }
                    if(el2->pre==el_start2)
                      {
                      param5=((Snode)sedge_real_source(edge2))->x;   
                      param6=((Snode)sedge_real_source(edge2))->y;
		      }
                    else
                      {
                      param5=previous2_x; 
                      param6=previous2_y;
		      }
                    if(el2->suc==el_start2)
                      {
                      param7=((Snode)sedge_real_target(edge2))->x;   
                      param8=((Snode)sedge_real_target(edge2))->y;
		      }
                    else
                      {
                      param7=edgeline_x(el2);
                      param8=edgeline_y(el2);
		      }
                    switch(crossing(param1,param2,param3,param4,param5,
                                    param6,param7,param8))
                      {
                      case 7:  
/* Removed Michael Himsolt 6/8/93
                               info->new_selection.sedge=edge2;
                               info->new_selected=SGRAPH_SELECTED_SEDGE;
*/
                               break;
                      case 6:  if(el2->suc==el_start2)
                                 count++;
                               else
                                 count2++;
                               break;
                      case 5:  if(el2->pre==el_start2)
                                 count++;
                               else
                                 count2++;
                               break;
                      case 4:  if(el1->suc==el_start1)
                                 count++;
                               else
                                 count2++;
                               break;
                      case 3:  if(el1->pre==el_start1)
                                 count++;
                               else
                                 count2++;
                               break;
                      case 2:  count++;
                               break;
                      case 1:  if(!(el1->suc==el2 || el2->suc==el1))
                                 if(!((el1->pre==el_start1 && 
                                      ((el2->pre==el_start2 && 
                                       param1==param5 && param2==param6) ||
                                      (el2->suc==el_start2 &&
                                       param1==param7 && param2==param8))) ||
                                      (el1->suc==el_start1 && ((el2->pre==el_start2 &&
                                      param3==param5 && param4==param6) ||
                                       (el2->suc==el_start2 &&
                                       param3==param7 && param4==param8)))))
				   {
                                   if((el1->pre!=el_start1 && el2->pre!=el_start2 &&
                                      param1==param5 && param2==param6) ||
                                      (el1->pre!=el_start1 && el2->suc!=el_start2 &&
                                      param1==param7 && param2==param8) ||
                                      (el1->suc!=el_start1 && el2->pre!=el_start2 &&
                                      param3==param5 && param4==param6) ||
                                      (el1->suc!=el_start1 && el2->suc!=el_start2 &&
                                      param3==param7 && param4==param8))
                                     count4++;
                                   else 
                                     count2++;
				   }
                      default: break;
                      }
	            }
                  previous2_x=edgeline_x(el2);
                  previous2_y=edgeline_y(el2);
                  first2=0;
                  } end_for_edgeline(el_start2,el2);
                } end_for_sourcelist(node2,edge2);
              } end_for_all_nodes(sgraph,node2);
	    }
          previous1_x=edgeline_x(el1);
          previous1_y=edgeline_y(el1);
          first1=0;
          } end_for_edgeline(el_start1,el1);
        } end_for_sourcelist(node1,edge1);
      } end_for_all_nodes(sgraph,node1);
    return((count + count2/2 + count4/4)/2);
    /* Beachte: Wegen der zwei for-Schleifen wird prinzipiell jede    */
    /* Ueberkreuzung zweimal gefunden. Die speziellen Variable        */
    /* count2 beruecksichtigt den Fall, dass eine Kante a eine andere */
    /* b in einem Knickpunkt schneidet und im Algorithmus der Schnitt-*/
    /* punkt insgesamt viermal erfasst wird:                          */
    /*                                    a  _____________            */
    /*                                            /\                  */
    /*                                    b      /  \                 */
    /* Analog zaehlt count4 die Ueberkreuzungen im Fall der beider-   */
    /* seitigen Ueberschneidung in einem Knickpunkt:                  */
    /*                                    a      \  /                 */
    /*                                            \/                  */
    /*                                            /\                  */
    /*                                    b      /  \                 */
  }     
}







