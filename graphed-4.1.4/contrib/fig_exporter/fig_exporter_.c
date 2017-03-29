#include <stdlib.h>
#include <std.h>
#include <slist.h>
#include <sgraph.h>
#include <graphed.h>
#include <errno.h>
#include <math.h>
#include <string.h>
#include "fig_exporter.h"
#include "fig_exporter_export.h"


#define USE_UNDOCUMENTED_FEATURES

#ifdef USE_UNDOCUMENTED_FEATURES
#include <xview/xview.h>
#include <xview/panel.h>
#include "fileselector/fileselect.h"

extern Frame base_frame;
Local char output_file_name[WDLEN+FNLEN+1];
#else
#define output_file_name "graphed.fig"
#endif

Local FILE* output_file;

Local char fontpath[] = FONTPATH;

Local char *fontname;

Local struct font_info {
  int fig_font;
  int font_size;
} fonts[MAXFONTS];

Local int fig_font_flags;

Local int node_type_box, node_type_circle, node_type_diamond;

Local int edge_type_solid, edge_type_dashed, edge_type_dotted;

Local struct {
  bool use_graphed_arrows;

  bool simulate_graphed_lines;/* too difficult =>  not implem. */

  bool leave_out_unknown_node_styles;

  bool use_ps_fonts;
  bool use_unrescalable_fonts;
  bool special_text; /* e.g. labels contain LaTeX commands */
} fig_exporter_options = {
#ifdef USE_UNDOCUMENTED_FEATURES
  TRUE,
#else
  FALSE,
#endif
  FALSE, TRUE, FALSE, FALSE, TRUE};


Local int write_fig_prologue(void)
{
  return fprintf(output_file, "#FIG 2.1\n%d %d\n", FIG_RESOL, FIG_COORD);
}

Local int write_fig_box(int x1, int y1, int x2, int y2, int line_style, int thickness, int color, int depth, int area_fill, float style_val)
{
  return fprintf(output_file,
    "%d %d %d %d %d %d -1 %d %.3f -1 0 0\n\t %d %d %d %d %d %d %d %d %d %d 9999 9999\n",
    O_POLYLINE, T_BOX, line_style,thickness,color,depth, area_fill, style_val,
    x1,y1,x2,y1,x2,y2,x1,y2,x1,y1);
}

Local int write_fig_ellipse(int x, int y, int rad_x, int rad_y, int line_style, int thickness, int color, int depth, int area_fill, float style_val)
{
  return fprintf(output_file,
    "%d %d %d %d %d %d -1 %d %.3f 1 %.3f %d %d %d %d %d %d %d %d\n",
    O_ELLIPSE, (rad_x == rad_y) ? T_CIRCLE_BY_RAD : T_ELLIPSE_BY_RAD,
    line_style, thickness, color, depth, area_fill, style_val, 0.0,
    x, y, rad_x, rad_y, x, y, x+rad_x-1, y+rad_y-1);
}

Local int write_fig_polyline_style(int line_style, int thickness, int color, int depth, int area_fill, float style_val, int forward_arrow, int backward_arrow)
{
  return fprintf(output_file,
    "%d %d %d %d %d %d -1 %d %.3f -1 %d %d\n",
    O_POLYLINE, T_POLYLINE,
    line_style,thickness,color,depth, area_fill, style_val,
    forward_arrow, backward_arrow);
}

Local int write_fig_polygon_style(int line_style, int thickness, int color, int depth, int area_fill, float style_val, int forward_arrow, int backward_arrow)
{
  return fprintf(output_file,
    "%d %d %d %d %d %d -1 %d %.3f -1 %d %d\n",
    O_POLYLINE, T_POLYGON,
    line_style,thickness,color,depth, area_fill, style_val,
    forward_arrow, backward_arrow);
}

Local int write_fig_arrow_style(float thickness, float width, float height)
{
  return fprintf(output_file,
    "\t-1 -1 %.3f %.3f %.3f\n",
    thickness, width, height);
}

Local int write_fig_text(int justification, int font, int font_size, int color, int depth, float angle, int font_flags, int height, int x, int y, char *text)
{
  return fprintf(output_file,
    "%d %d %d %d -1 %d %d %.3f %d %d %d %d %d %s\1\n", O_TEXT,
    justification, font, font_size, color, depth, angle, font_flags,
    height, 1, x, y, text);
}

Local void export_node(Graphed_node gnode)
{
  int x  = (int) node_get(gnode, NODE_X);
  int y  = (int) node_get(gnode, NODE_Y);
  int h = (int) node_get(gnode, NODE_HEIGHT);
  int w = (int) node_get(gnode, NODE_WIDTH);
  int h2 = ((float)h+0.5) / 2;
  int w2 = ((float)w+0.5) / 2;
  char *label = (char *) node_get(gnode, NODE_LABEL);
  char *p, *lp;
  int i;
  int font_index;
  int node_nlp;
  int fig_text_height;
  int fig_text_width;
  int fig_text_step;
  int fig_text_justification;
  int nlines;
  int longest_line_len;
  struct font_info font;

  int node_type = (int) node_get(gnode, NODE_TYPE);

  if (node_type == node_type_circle)
    write_fig_ellipse(x,y,w2-1,h2-1,SOLID_LINE,1,DEFAULT,TOP,UNFILLED,0.0);
  else if (node_type == node_type_diamond) {
    write_fig_polygon_style(SOLID_LINE,1,DEFAULT,TOP,UNFILLED,0.0,FALSE,FALSE);
    fprintf(output_file, "\t %d %d %d %d %d %d %d %d %d %d 9999 9999\n",
	x-w2,y-1,x-1,y-h2,x-w2+w-2,y-1,x-1,y-h2+h-2,x-w2,y-1);
  } else {
    if (node_type != node_type_box && !fig_exporter_options.leave_out_unknown_node_styles) {
      warning("unknown or unimplemented nodetype - using box instead.\n");
    }
    if (node_type == node_type_box || !fig_exporter_options.leave_out_unknown_node_styles) {
     write_fig_box
       (x-w2,y-h2,x-w2+w-1,y-h2+h-1,SOLID_LINE,1,DEFAULT,TOP,UNFILLED,0.0);
    }
  }

  if (label && (bool)node_get(gnode, NODE_LABEL_VISIBILITY)) {
    label = strsave(label);
    longest_line_len = 0;
    for (nlines=1,lp=label,p=strchr(label,'\r'); p;p=strchr(p+1,'\r'),nlines++){
      *p = '\0';
      longest_line_len = maximum((int)strlen(lp), longest_line_len);
      lp = p+1;
    }
    if (*lp) {
      longest_line_len = maximum((int)strlen(lp), longest_line_len);
    } else {
      nlines--;
    }
    font_index = (int) node_get(gnode, NODE_FONT);
    if (font_index > MAXFONTS - 1 || fonts[font_index].fig_font==UNKNOWN_FONT)
    {
      warning("Unknown font... using %s\n", DEFAULT_FONT_NAME);
      font.fig_font = DEFAULT_FIG_FONT;
      font.font_size = DEFAULT_FONT_SIZE;
    } else {
      font = fonts[font_index];
    }
    fig_text_height = get_fig_text_height(font.font_size);
    fig_text_width = get_fig_text_width(font.font_size);
    fig_text_step = get_fig_text_step(font.font_size);
    fig_text_justification = T_LEFT_JUSTIFIED;
    node_nlp = (int) node_get(gnode, NODE_NLP);
    switch(node_nlp) {
    case NODELABEL_MIDDLE	:
      if (nlines == 1) {
	fig_text_justification = T_CENTER_JUSTIFIED;
	y = y + (int)(font.font_size / 2 / 1.6);
      } else {
	x = x - longest_line_len * fig_text_width / 2;
	y = y + 2 - (nlines-1) * fig_text_step / 2;
      }
      break;
    case NODELABEL_UPPERLEFT	:
      x = x - w2 + 2;
      y = y - h2 + fig_text_step - 2;
      break;
    case NODELABEL_UPPERRIGHT	:
      y = y - h2 + fig_text_step - 2;
      if (nlines == 1) {
	fig_text_justification = T_RIGHT_JUSTIFIED;
	x = x - w2 + w - 2;
      } else {
	x = x - w2 + w - 2 - longest_line_len * fig_text_width;
      }
      break;
    case NODELABEL_LOWERLEFT	:
      x = x - w2 + 2;
      y = y - h2 + h - 5;
      break;
    case NODELABEL_LOWERRIGHT	:
      y = y - h2 + h - 5;
      if (nlines == 1) {
	fig_text_justification = T_RIGHT_JUSTIFIED;
	x = x - w2 + w - 2;
      } else {
	x = x - w2 + w - 2 - longest_line_len * fig_text_width;
      }
      break;
     
    default			:
      warning("unknown node label placement: %d\n",
			(int) node_get(gnode, NODE_NLP));
    }
    for (i=1, p = label; i<=nlines; i++, p = p + strlen(p) + 1) {
      if (*p) {
	write_fig_text(fig_text_justification, font.fig_font, font.font_size,
	DEFAULT, TOP, 0.0, fig_font_flags, fig_text_height, x, y, p);
      }
      y += fig_text_step;
    }
    free(label);
  }
}

Local int export_edgeline(Edgeline edgeline)
{
  Edgeline el;
  int z=0;
  fprintf(output_file, "\t ");
  for_edgeline(edgeline, el) {
    fprintf(output_file, "%d %d ", edgeline_x(el), edgeline_y(el));
    z++;
  } end_for_edgeline(edgeline, el)
  fprintf(output_file, "9999 9999\n");
  return z;
}

Local void export_edge(Graphed_edge gedge, int with_arrow)
{
  char *label = (char *) edge_get(gedge, EDGE_LABEL);
  int font_index;
  int line_style;
  int fig_text_height;
  struct font_info font;
  Edgeline edgeline = (Edgeline) edge_get(gedge, EDGE_LINE);
  Edgeline el;
  int edgeline_length;
  int z=0;
  int x1,y1,x2,y2;
  float style_val = 0.0;

#ifdef USE_UNDOCUMENTED_FEATURES
  float arrow_length = (float) (int) edge_get(gedge, EDGE_ARROW_LENGTH);
  float arrow_angle = get_edge_attributes(gedge).arrow_angle;
#endif

  int edge_type = (int) edge_get(gedge, EDGE_TYPE);
  if (edge_type == edge_type_dashed) {
    line_style = DASH_LINE;	style_val = DEF_DASHLENGTH;
  } else if (edge_type == edge_type_dotted) {
    line_style = DOTTED_LINE;	style_val = DEF_DOTGAP;
  } else
    line_style = SOLID_LINE;

  write_fig_polyline_style(line_style,1,DEFAULT,TOP,0,style_val,
			with_arrow,FALSE);

  if (with_arrow) {
    if (fig_exporter_options.use_graphed_arrows) {
#ifdef USE_UNDOCUMENTED_FEATURES
      write_fig_arrow_style((float) 1,
	arrow_length * sin(arrow_angle) * 2,
        arrow_length * cos(arrow_angle));
#else
      warning("graphed style arrows not available... using fig style!\n");
      write_fig_arrow_style(arrow_style(1));
#endif
    } else {
      write_fig_arrow_style(arrow_style(1));
    }
  }

  edgeline_length = export_edgeline(edgeline);
  if (label && (bool)edge_get(gedge, EDGE_LABEL_VISIBILITY)) {
    font_index = (int) edge_get(gedge, EDGE_FONT);
    if (font_index > MAXFONTS - 1 || fonts[font_index].fig_font==UNKNOWN_FONT)
    {
      warning("Unknown font... using %s\n", DEFAULT_FONT_NAME);
      font.fig_font = DEFAULT_FIG_FONT;
      font.font_size = DEFAULT_FONT_SIZE;
    } else {
      font = fonts[font_index];
    }
    fig_text_height = get_fig_text_height(font.font_size);
    for_edgeline(edgeline, el) {
      z++;
      if (z > edgeline_length / 2) {
	x2 = edgeline_x(el);
	y2 = edgeline_y(el);
	write_fig_text(T_CENTER_JUSTIFIED, font.fig_font, font.font_size,
	  DEFAULT, TOP, 0.0, fig_font_flags, fig_text_height,
	  x1+(x2-x1)/2, y1+(y2-y1)/2 + (int)(font.font_size / 2 / 1.6),
	  label);
	return;
      }
      x1 = edgeline_x(el);
      y1 = edgeline_y(el);
    } end_for_edgeline(edgeline, el)
  }
}

Local void export_graph(Sgraph sgraph)
{
  Snode snode;
  Sedge sedge;
  bool directed = sgraph->directed;
  int type, style, size, font_index;

  node_type_box     = find_nodetype("#box");
  node_type_circle  = find_nodetype("#circle");
  node_type_diamond = find_nodetype("#diamond");

  edge_type_solid   = find_edgetype("#solid");
  edge_type_dashed  = find_edgetype("#dashed");
  edge_type_dotted  = find_edgetype("#dotted");

  fontname = strrchr(fontpath, '/')+1;

  fig_font_flags = 0;
  if (fig_exporter_options.use_ps_fonts) fig_font_flags |= PSFONT_TEXT;
  if (fig_exporter_options.use_unrescalable_fonts) fig_font_flags |= RIGID_TEXT;
  if (fig_exporter_options.special_text) fig_font_flags |= SPECIAL_TEXT;

  for (font_index = 0; font_index < MAXFONTS; font_index++) {
    fonts[font_index].fig_font = UNKNOWN_FONT;
  }
  for (type = 0; type <= MAX_FONT_TYPE; type++) {
    for (style = 0; style <= MAX_FONT_STYLE; style++) {
      for (size = MIN_FONT_SIZE; size <= MAX_FONT_SIZE; size++) {
	build_fontname(fontname, type, style, size);
	font_index = find_font(fontpath,fontname);
	if (font_index >= 0) {
	  if (font_index >= MAXFONTS) {
	    warning("too many fonts (>%d) - ignoring %s\n", MAXFONTS, fontname);
	  } else {
	    fonts[font_index].fig_font = get_fig_font(type, style);
	    fonts[font_index].font_size = size;
	  }
	}
      }
    }
  }

  write_fig_prologue();
  for_all_nodes(sgraph, snode) {
    export_node(graphed_node(snode));
    for_sourcelist(snode, sedge) {
      if (directed || unique_edge(sedge)) {
	export_edge(graphed_edge(sedge), directed);
      }
    } end_for_sourcelist(snode, sedge);
  } end_for_all_nodes(sgraph, snode);
}

Local void fig_exporter_sgraph_proc(Sgraph_proc_info info)
{
  if (info->sgraph != empty_sgraph) {
    if (!(output_file=fopen(output_file_name, "w"))) {
      error("could not open file %s for writing\n", output_file_name);
      return;
    }
    message("Exporting to file %s\n", output_file_name);
    export_graph(info->sgraph);
    fclose(output_file);
    message("done.\n");
    info->no_changes = TRUE;
    info->no_structure_changes = TRUE;
    info->save_selection = TRUE;
  } else {
    error ("No graph selected\n");
  }
}

#ifdef USE_UNDOCUMENTED_FEATURES

Local void nf_arrow_opt(Panel_item item, Event *event)
{
  if (!fig_exporter_options.use_graphed_arrows) {
    fig_exporter_options.use_graphed_arrows = TRUE;
    xv_set(item, PANEL_LABEL_STRING, "arrows: GraphEd", NULL);
  } else {
    fig_exporter_options.use_graphed_arrows = FALSE;
    xv_set(item, PANEL_LABEL_STRING, "arrows: xfig", NULL);
  }
}

Local void nf_node_opt(Panel_item item, Event *event)
{
  if (!fig_exporter_options.leave_out_unknown_node_styles) {
    fig_exporter_options.leave_out_unknown_node_styles = TRUE;
    xv_set(item, PANEL_LABEL_STRING, "unknown nodetypes: draw nothing", NULL);
  } else {
    fig_exporter_options.leave_out_unknown_node_styles = FALSE;
    xv_set(item, PANEL_LABEL_STRING, "unknown nodetypes: draw box instead", NULL);
  }
}

Local void nf_ps_opt(Panel_item item, Event *event)
{
  if (!fig_exporter_options.use_ps_fonts) {
    fig_exporter_options.use_ps_fonts = TRUE;
    xv_set(item, PANEL_LABEL_STRING, "fonts: PostScript", NULL);
  } else {
    fig_exporter_options.use_ps_fonts = FALSE;
    xv_set(item, PANEL_LABEL_STRING, "fonts: LaTeX", NULL);
  }
}

Local void nf_special_text_opt(Panel_item item, Event *event)
{
  if (!fig_exporter_options.special_text) {
    fig_exporter_options.special_text = TRUE;
    xv_set(item, PANEL_LABEL_STRING, "label treatment: LaTeX input", NULL);
  } else {
    fig_exporter_options.special_text = FALSE;
    xv_set(item, PANEL_LABEL_STRING, "label treatment: plain text", NULL);
  }
}

Local void fig_exporter_create_panel(Panel panel)
{
  Panel_item button;
  button=xv_create(panel, PANEL_BUTTON,
                  PANEL_NOTIFY_PROC,      nf_arrow_opt,
                  PANEL_LABEL_X,          xv_col(panel,5),
                  PANEL_LABEL_Y,          xv_row(panel,0),
                  NULL);
  fig_exporter_options.use_graphed_arrows = 
    !fig_exporter_options.use_graphed_arrows;
  nf_arrow_opt(button, (Event *)NULL);

  button=xv_create(panel, PANEL_BUTTON,
                  PANEL_NOTIFY_PROC,      nf_node_opt,
                  PANEL_LABEL_X,          xv_col(panel,25),
                  PANEL_LABEL_Y,          xv_row(panel,0),
                  NULL);
  fig_exporter_options.leave_out_unknown_node_styles = 
    !fig_exporter_options.leave_out_unknown_node_styles;
  nf_node_opt(button, (Event *)NULL);

  button=xv_create(panel, PANEL_BUTTON,
                  PANEL_NOTIFY_PROC,      nf_ps_opt,
                  PANEL_LABEL_X,          xv_col(panel,5),
                  PANEL_LABEL_Y,          xv_row(panel,1),
                  NULL);
  fig_exporter_options.use_ps_fonts = 
    !fig_exporter_options.use_ps_fonts;
  nf_ps_opt(button, (Event *)NULL);

  button=xv_create(panel, PANEL_BUTTON,
                  PANEL_NOTIFY_PROC,      nf_special_text_opt,
                  PANEL_LABEL_X,          xv_col(panel,25),
                  PANEL_LABEL_Y,          xv_row(panel,1),
                  NULL);
  fig_exporter_options.special_text = 
    !fig_exporter_options.special_text;
  nf_special_text_opt(button, (Event *)NULL);

  window_fit_height(panel);
}

Local Fs_item fsi = NULL;

Local Fs_item init_fsi(void) {
  Fs_item fsi;
  char *fname=buffer_get_filename(wac_buffer);
  char *dirname;
  char *basename;
  char *old_ext;
  if (!fname || !*fname) fname = "graphed";
  strcpy(output_file_name, fname);
  if ((basename = strrchr(output_file_name, '/'))) {
    dirname = output_file_name;
    *basename++ = '\0';
  } else {
    dirname = "";
    basename = output_file_name;
  }
  if ((old_ext = strrchr(basename, '.')))
    *old_ext = '\0';
  strcat(basename, ".fig");
  fsi = fls_create();
  fls_set_info(fsi, "<< EXPORT GRAPH IN FIG FORMAT >>");
  fls_set_extension(fsi, 1, "*.fig");
  fls_set_extension(fsi, 2, "*.gig");
  fls_set_current_extension(fsi, 1);
  fls_set_working_directory(fsi, dirname);
  fls_set_default_filename(fsi, basename);
  fls_set_user_panel_items_create_proc(fsi, fig_exporter_create_panel);
  return fsi;
}

Local void fig_exporter_doit(char *dir, char *file)
{
  if ((!strcmp( dir, "" ) && !strcmp( file, "NOTHING SELECTED" ))
    /* Abort has been selected */ || !strcmp( file, "" )) {
    unlock_user_interface();
    return;
  }
  sprintf(output_file_name,"%s/%s", dir, file);
  unlock_user_interface();
  call_sgraph_proc(fig_exporter_sgraph_proc, NULL);
}

Local bool fig_exporter_create_selector(void) {
  lock_user_interface();
  if (fsi == NULL) fsi=init_fsi();
  if (fls_busy(fsi)) {
    warning("Fileselector still active\n");
  } else {
    fileselect(fsi, base_frame, fig_exporter_doit);
  }
  return 0;
}

Global void fig_exporter_callback_proc(Menu menu, Menu_item menu_item)
{
  fig_exporter_create_selector();
}

#endif
