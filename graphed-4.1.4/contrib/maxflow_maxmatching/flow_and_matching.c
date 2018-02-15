#include "sgraph/std.h"
#include "sgraph/slist.h"
#include "sgraph/sgraph.h"
#include "sgraph/graphed.h"
#include "flowmatch.h"


void	menu_maxflow_proc(Menu menu, Menu_item menu_item);  /* prototypen */
void	menu_matching_proc(Menu menu, Menu_item menu_item);

void sgraph_maxflow_proc(Sgraph_proc_info info);
void sgraph_matching_proc(Sgraph_proc_info info);

void adjust_labels(Sgraph_proc_info info, int alg_nr, Snode sourcenode, Snode targetnode, int defaultflag, float defaultvalue);

/*
void init_user_menu(void)
  {
  add_to_user_menu("Maxflow",menu_maxflow_proc);
  add_to_user_menu("MaxWeightMatch",menu_matching_proc);
  }
*/


void menu_maxflow_proc(Menu menu, Menu_item menu_item)
{
  call_sgraph_proc(sgraph_maxflow_proc, NULL);
  }

void menu_matching_proc(Menu menu, Menu_item menu_item)
{
  call_sgraph_proc(sgraph_matching_proc, NULL);
  }



void sgraph_maxflow_proc(Sgraph_proc_info info)  /* maxflow algorithm */
                        
  {
  Sgraph sgraph=info->sgraph; /* 'sgraph' is the graph selected by graphED */
  Snode node,sourcenode=NULL,targetnode=NULL;
  if (!sgraph->directed)
    {
    message("The Maxflow Algorithms only runs on directed graphs.\n");
    info->no_changes=1;
    info->no_structure_changes=1;
    return;
    }
  for_all_nodes (sgraph,node)
    {
    if (node->label != NULL && strcmp(node->label,"source") == 0)
      if (sourcenode == NULL)
        sourcenode=node;
      else
        {
        message("Please specify only one source node.\n");
        info->no_changes=1;
        info->no_structure_changes=1;
        return;
        }
    if (node->label != NULL && strcmp(node->label,"target") == 0)
      if (targetnode == NULL)
        targetnode=node;
      else
        {
        message("Please specify only one target node.\n");
        info->no_changes=1;
        info->no_structure_changes=1;
        return;
        }
    }
  end_for_all_nodes (sgraph,node);
  if (targetnode == NULL || sourcenode == NULL)
    {
    message("Please label one node 'source' and another node 'target'.\n");
    info->no_changes=1;
    info->no_structure_changes=1;
    return;
    }
  adjust_labels(info,1,sourcenode,targetnode,1,1.0); /* defaultflag=1,defaultvalue=1.0 */
  }

void sgraph_matching_proc(Sgraph_proc_info info)  /* matching algorithm */
                        
  {
  adjust_labels(info,2,NULL,NULL,1,1.0); /* defaultflag=1,defaultvalue=1.0 */
  }

void adjust_labels(Sgraph_proc_info info, int alg_nr, Snode sourcenode, Snode targetnode, int defaultflag, float defaultvalue)
{
  Sgraph sgraph=info->sgraph; /* 'sgraph' is the graph selected by graphED */
  Snode node;
  Sedge edge;
  float value;
  int warned;
  char numberstring[15];
  Pair_of_edgevalues pair;
  /* check edge labels,they must represent values between 1e6 and 1e-6 */
  for_all_nodes (sgraph,node)
    {
    for_sourcelist (node,edge)
      {
      set_edgeattrs(edge,make_attr(ATTR_DATA,NULL));
      if (alg_nr == 1 && edge->label != NULL && edge->label[0] == ',')
        edge->label[0]=0;        /* delete old flow values */
      if (edge->label == NULL || edge->label[0] == 0)
        {
        if (!defaultflag)
          {
          message("The Algorithm requires edge labels\nrepresenting float numbers between 1e-7 and 1e7.\n");
          info->new_selected=SGRAPH_SELECTED_SEDGE;
          info->new_selection.sedge=edge;
          return;
          } 
        }
      else
        {
        value=0.0;
        sscanf(edge->label,"%g",&value);
        if (value <= 1e-7 || value > 1e7)
          {
          message("All edge labels must represent\nfloat numbers between 1e-7 and 1e7.\n");
          info->new_selected=SGRAPH_SELECTED_SEDGE;
          info->new_selection.sedge=edge;
          return;
          }
        }
      }
    end_for_sourcelist (node,edge);
    }
  end_for_all_nodes (sgraph,node);

  /* now we have a valid sgraph.*/
  warned=0;
  for_all_nodes (sgraph,node)
    {
    for_sourcelist (node,edge)
      {
      if (attr_data(edge) == NULL) /* we found a new edge */
        {    /* assign a pair of edge values as the attribute of `edge` */
        set_edgeattrs(edge,make_attr(ATTR_DATA,malloc(sizeof(struct pair_of_edgevalues))));
        pair=attr_data_of_type(edge,Pair_of_edgevalues);
        if (edge->label == NULL || edge->label[0] == 0)
          {
          pair->float1=defaultvalue;
          if (!warned)
            {
            message("Assigning default value 1.0 to\nunlabelled edges ...\n");
            info->new_selected=SGRAPH_SELECTED_SEDGE;
            info->new_selection.sedge=edge;
            warned=1;
            }
          }
        else
          {
          sscanf(edge->label,"%g",&(pair->float1));
          sprintf(edge->label,"%g",pair->float1);
          }
        pair->float2=0.0;
        }
      }
    end_for_sourcelist (node,edge);
    }
  end_for_all_nodes (sgraph,node);
  if (alg_nr == 1)
    sgraph_max_flow(sgraph,sourcenode,targetnode);
  else
    sgraph_max_weight_matching(sgraph);  
  for_all_nodes (sgraph,node)
    {
    for_sourcelist (node,edge)
      {
      if (attr_data(edge) != NULL) /* we found a new edge */
        {
        pair=attr_data_of_type(edge,Pair_of_edgevalues);
        sprintf(numberstring,",%g",pair->float2);
        if (alg_nr == 1)
          if (edge->label == NULL)
            edge->label=strdup(numberstring);
          else
            strcat(edge->label,numberstring);
        else
          if (pair->float2 == 1.0)
            edge_set(graphed_edge(edge),EDGE_TYPE,0,0);
          else
            edge_set(graphed_edge(edge),EDGE_TYPE,2,0);
        free(pair);
        set_edgeattrs(edge,make_attr(ATTR_DATA,NULL));
        }
      }
    end_for_sourcelist (node,edge);
    }
  end_for_all_nodes (sgraph,node);
  info->no_structure_changes=1;
  info->repaint=1;
  }
