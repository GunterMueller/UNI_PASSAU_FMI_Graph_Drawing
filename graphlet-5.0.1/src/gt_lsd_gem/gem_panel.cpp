/* This software is distributed under the Lesser General Public License */
#include <xview/xview.h>
#include <xview/panel.h>
#include <xview/notice.h>

#include <sgraph/sgraph.h>
#include <sgraph/sgraph_interface.h>

#include <graphed/graphed_pin_sf.h>
#include <graphed/error.h>

#include "gem_panel.h"
#include "gem_main.h"
#include "embedder.h"

#define PVDW 50
#define WIDTH 75
#define WIDTH1 80
#define HEIGHT 20
#define HSKIP 10

#define COL0 0
#define COL1 WIDTH1
#define COL2 (WIDTH1+WIDTH)
#define COL3 (WIDTH1+2*WIDTH)

#define ROW0 0
#define ROW1 HEIGHT
#define ROW2 (2*HEIGHT)
#define ROW3 (3*HEIGHT)
#define ROW4 (4*HEIGHT)
#define ROW5 (5*HEIGHT)
#define ROW6 (6*HEIGHT)
#define ROW7 (7*HEIGHT)
#define ROW8 (8*HEIGHT)
#define ROW9 (9*HEIGHT)
#define ROWA (10*HEIGHT+HSKIP)
#define ROWB (11*HEIGHT+HSKIP)
#define ROWC (12*HEIGHT+2*HSKIP)

#define ERR(x) {ok=FALSE; warning ("Spring Embedder (Gem): %s: value out of range.\n", x); }


extern Frame base_frame; /* kein include wegen overhead */
extern void compute_subwindow_position_at_graph_of_current_selection (Xv_Window window);
static Graphed_pin_subframe gem_subframe=NULL;


typedef struct {
	float	i_maxtemp, i_starttemp, i_finaltemp, i_maxiter, i_gravity,
		i_oscillation, i_rotation, i_shake, a_maxtemp, a_starttemp,
		a_finaltemp, a_maxiter, a_gravity, a_oscillation, a_rotation,
		a_shake, o_maxtemp, o_starttemp, o_finaltemp, o_maxiter,
		o_gravity, o_oscillation, o_rotation, o_shake;
	int	random, quality;
	int	no_insert, no_arrange, no_optimize;
} gem_config;

static gem_config akt_conf;

static Panel_item gem_sf_sett, gem_sf_maxtemp, gem_sf_starttemp,
                  gem_sf_finaltemp, gem_sf_maxiter, gem_sf_gravity,
                  gem_sf_oscillation, gem_sf_rotation, gem_sf_shake,
                  gem_sf_skip, gem_sf_iskip, gem_sf_askip, gem_sf_oskip;
static Panel_item gem_sf_insert, gem_sf_imaxtemp, gem_sf_istarttemp,
                  gem_sf_ifinaltemp, gem_sf_imaxiter, gem_sf_igravity,
                  gem_sf_ioscillation, gem_sf_irotation, gem_sf_ishake;
static Panel_item gem_sf_arrange, gem_sf_amaxtemp, gem_sf_astarttemp,
                  gem_sf_afinaltemp, gem_sf_amaxiter, gem_sf_agravity,
                  gem_sf_aoscillation, gem_sf_arotation, gem_sf_ashake;
static Panel_item gem_sf_optimize, gem_sf_omaxtemp, gem_sf_ostarttemp,
                  gem_sf_ofinaltemp, gem_sf_omaxiter, gem_sf_ogravity,
                  gem_sf_ooscillation, gem_sf_orotation, gem_sf_oshake;
static Panel_item gem_sf_random, gem_sf_quality, gem_sf_run, gem_sf_set,
                  gem_sf_reset, gem_sf_default; 

int do_random, check_quality;

static char *float_2_ascii(float f)
{
  static char buf[10];
  sprintf (buf, "%.2f", f);
  return buf;
}
static char *float_2_ascii0(float f)
{
  static char buf[10];
  sprintf (buf, "%.0f", f);
  return buf;
}

static void skip_notify_proc(Panel_item item, int value, Event *event)
{
  if (item == gem_sf_iskip)
  {
    if (value || xv_get(gem_sf_random, PANEL_VALUE))
    {
      xv_set(gem_sf_imaxtemp, PANEL_INACTIVE, TRUE, NULL);
      xv_set(gem_sf_istarttemp, PANEL_INACTIVE, TRUE, NULL);
      xv_set(gem_sf_ifinaltemp, PANEL_INACTIVE, TRUE, NULL);
      xv_set(gem_sf_imaxiter, PANEL_INACTIVE, TRUE, NULL);
      xv_set(gem_sf_igravity, PANEL_INACTIVE, TRUE, NULL);
      xv_set(gem_sf_ioscillation, PANEL_INACTIVE, TRUE, NULL);
      xv_set(gem_sf_irotation, PANEL_INACTIVE, TRUE, NULL);
      xv_set(gem_sf_ishake, PANEL_INACTIVE, TRUE, NULL);
    }
    else
    {
      xv_set(gem_sf_imaxtemp, PANEL_INACTIVE, FALSE, NULL);
      xv_set(gem_sf_istarttemp, PANEL_INACTIVE, FALSE, NULL);
      xv_set(gem_sf_ifinaltemp, PANEL_INACTIVE, FALSE, NULL);
      xv_set(gem_sf_imaxiter, PANEL_INACTIVE, FALSE, NULL);
      xv_set(gem_sf_igravity, PANEL_INACTIVE, FALSE, NULL);
      xv_set(gem_sf_ioscillation, PANEL_INACTIVE, FALSE, NULL);
      xv_set(gem_sf_irotation, PANEL_INACTIVE, FALSE, NULL);
      xv_set(gem_sf_ishake, PANEL_INACTIVE, FALSE, NULL);
    }
  }
  if (item == gem_sf_askip)
  {
    if (value)
    {
      xv_set(gem_sf_amaxtemp, PANEL_INACTIVE, TRUE, NULL);
      xv_set(gem_sf_astarttemp, PANEL_INACTIVE, TRUE, NULL);
      xv_set(gem_sf_afinaltemp, PANEL_INACTIVE, TRUE, NULL);
      xv_set(gem_sf_amaxiter, PANEL_INACTIVE, TRUE, NULL);
      xv_set(gem_sf_agravity, PANEL_INACTIVE, TRUE, NULL);
      xv_set(gem_sf_aoscillation, PANEL_INACTIVE, TRUE, NULL);
      xv_set(gem_sf_arotation, PANEL_INACTIVE, TRUE, NULL);
      xv_set(gem_sf_ashake, PANEL_INACTIVE, TRUE, NULL);
    }
    else
    {
      xv_set(gem_sf_amaxtemp, PANEL_INACTIVE, FALSE, NULL);
      xv_set(gem_sf_astarttemp, PANEL_INACTIVE, FALSE, NULL);
      xv_set(gem_sf_afinaltemp, PANEL_INACTIVE, FALSE, NULL);
      xv_set(gem_sf_amaxiter, PANEL_INACTIVE, FALSE, NULL);
      xv_set(gem_sf_agravity, PANEL_INACTIVE, FALSE, NULL);
      xv_set(gem_sf_aoscillation, PANEL_INACTIVE, FALSE, NULL);
      xv_set(gem_sf_arotation, PANEL_INACTIVE, FALSE, NULL);
      xv_set(gem_sf_ashake, PANEL_INACTIVE, FALSE, NULL);
    }
  }
  if (item == gem_sf_oskip)
  {
    if (value)
    {
      xv_set(gem_sf_omaxtemp, PANEL_INACTIVE, TRUE, NULL);
      xv_set(gem_sf_ostarttemp, PANEL_INACTIVE, TRUE, NULL);
      xv_set(gem_sf_ofinaltemp, PANEL_INACTIVE, TRUE, NULL);
      xv_set(gem_sf_omaxiter, PANEL_INACTIVE, TRUE, NULL);
      xv_set(gem_sf_ogravity, PANEL_INACTIVE, TRUE, NULL);
      xv_set(gem_sf_ooscillation, PANEL_INACTIVE, TRUE, NULL);
      xv_set(gem_sf_orotation, PANEL_INACTIVE, TRUE, NULL);
      xv_set(gem_sf_oshake, PANEL_INACTIVE, TRUE, NULL);
    }
    else
    {
      xv_set(gem_sf_omaxtemp, PANEL_INACTIVE, FALSE, NULL);
      xv_set(gem_sf_ostarttemp, PANEL_INACTIVE, FALSE, NULL);
      xv_set(gem_sf_ofinaltemp, PANEL_INACTIVE, FALSE, NULL);
      xv_set(gem_sf_omaxiter, PANEL_INACTIVE, FALSE, NULL);
      xv_set(gem_sf_ogravity, PANEL_INACTIVE, FALSE, NULL);
      xv_set(gem_sf_ooscillation, PANEL_INACTIVE, FALSE, NULL);
      xv_set(gem_sf_orotation, PANEL_INACTIVE, FALSE, NULL);
      xv_set(gem_sf_oshake, PANEL_INACTIVE, FALSE, NULL);
    }
  }
  if (item == gem_sf_random)
  {
    if (value || xv_get(gem_sf_iskip, PANEL_VALUE))
    {
      xv_set(gem_sf_imaxtemp, PANEL_INACTIVE, TRUE, NULL);
      xv_set(gem_sf_istarttemp, PANEL_INACTIVE, TRUE, NULL);
      xv_set(gem_sf_ifinaltemp, PANEL_INACTIVE, TRUE, NULL);
      xv_set(gem_sf_imaxiter, PANEL_INACTIVE, TRUE, NULL);
      xv_set(gem_sf_igravity, PANEL_INACTIVE, TRUE, NULL);
      xv_set(gem_sf_ioscillation, PANEL_INACTIVE, TRUE, NULL);
      xv_set(gem_sf_irotation, PANEL_INACTIVE, TRUE, NULL);
      xv_set(gem_sf_ishake, PANEL_INACTIVE, TRUE, NULL);
    }
    else
    {
      xv_set(gem_sf_imaxtemp, PANEL_INACTIVE, FALSE, NULL);
      xv_set(gem_sf_istarttemp, PANEL_INACTIVE, FALSE, NULL);
      xv_set(gem_sf_ifinaltemp, PANEL_INACTIVE, FALSE, NULL);
      xv_set(gem_sf_imaxiter, PANEL_INACTIVE, FALSE, NULL);
      xv_set(gem_sf_igravity, PANEL_INACTIVE, FALSE, NULL);
      xv_set(gem_sf_ioscillation, PANEL_INACTIVE, FALSE, NULL);
      xv_set(gem_sf_irotation, PANEL_INACTIVE, FALSE, NULL);
      xv_set(gem_sf_ishake, PANEL_INACTIVE, FALSE, NULL);
    }
  }
}

void read_config(void)
{
  i_maxtemp = float(akt_conf.i_maxtemp);
  i_starttemp = float(akt_conf.i_starttemp);
  i_finaltemp = float(akt_conf.i_finaltemp);
  i_maxiter = float(akt_conf.i_maxiter);
  i_gravity = float(akt_conf.i_gravity);
  i_oscillation = float(akt_conf.i_oscillation);
  i_rotation = float(akt_conf.i_rotation);
  i_shake = float(akt_conf.i_shake);
  a_maxtemp = float(akt_conf.a_maxtemp);
  a_starttemp = float(akt_conf.a_starttemp);
  a_finaltemp = float(akt_conf.a_finaltemp);
  a_maxiter = float(akt_conf.a_maxiter);
  a_gravity = float(akt_conf.a_gravity);
  a_oscillation = float(akt_conf.a_oscillation);
  a_rotation = float(akt_conf.a_rotation);
  a_shake = float(akt_conf.a_shake);
  o_maxtemp = float(akt_conf.o_maxtemp);
  o_starttemp = float(akt_conf.o_starttemp);
  o_finaltemp = float(akt_conf.o_finaltemp);
  o_maxiter = float(akt_conf.o_maxiter);
  o_gravity = float(akt_conf.o_gravity);
  o_oscillation = float(akt_conf.o_oscillation);
  o_rotation = float(akt_conf.o_rotation);
  o_shake = float(akt_conf.o_shake);
  do_random=akt_conf.random;
  check_quality=akt_conf.quality;
  if (akt_conf.no_insert) i_finaltemp=i_starttemp;
  if (akt_conf.no_arrange) a_finaltemp=a_starttemp;
  if (akt_conf.no_optimize) o_finaltemp=o_starttemp; 
}

void set_gem_default_config(void)
{
  akt_conf.i_maxtemp		= IMAXTEMPDEF;
  akt_conf.i_starttemp		= ISTARTTEMPDEF;
  akt_conf.i_finaltemp		= IFINALTEMPDEF;
  akt_conf.i_maxiter		= IMAXITERDEF;
  akt_conf.i_gravity		= IGRAVITYDEF;
  akt_conf.i_oscillation	= IOSCILLATIONDEF;
  akt_conf.i_rotation		= IROTATIONDEF;
  akt_conf.i_shake		= ISHAKEDEF;
  akt_conf.a_maxtemp		= AMAXTEMPDEF;
  akt_conf.a_starttemp		= ASTARTTEMPDEF;
  akt_conf.a_finaltemp		= AFINALTEMPDEF;
  akt_conf.a_maxiter		= AMAXITERDEF;
  akt_conf.a_gravity		= AGRAVITYDEF;
  akt_conf.a_oscillation	= AOSCILLATIONDEF;
  akt_conf.a_rotation		= AROTATIONDEF;
  akt_conf.a_shake		= ASHAKEDEF;
  akt_conf.o_maxtemp		= OMAXTEMPDEF;
  akt_conf.o_starttemp		= OSTARTTEMPDEF;
  akt_conf.o_finaltemp		= OFINALTEMPDEF;
  akt_conf.o_maxiter		= OMAXITERDEF;
  akt_conf.o_gravity		= OGRAVITYDEF;
  akt_conf.o_oscillation	= OOSCILLATIONDEF;
  akt_conf.o_rotation		= OROTATIONDEF;
  akt_conf.o_shake		= OSHAKEDEF;
  akt_conf.random		= FALSE;
  akt_conf.quality		= FALSE;
  akt_conf.no_insert		= FALSE;
  akt_conf.no_arrange		= FALSE;
  akt_conf.no_optimize		= FALSE;
}


static int get_gem_subframe_values(void)
{
  gem_config tmp;
  int ok;

  ok=TRUE;

/* Werte holen */
  tmp.i_maxtemp     = atof((char *)xv_get(gem_sf_imaxtemp,     PANEL_VALUE));
  tmp.i_starttemp   = atof((char *)xv_get(gem_sf_istarttemp,   PANEL_VALUE));
  tmp.i_finaltemp   = atof((char *)xv_get(gem_sf_ifinaltemp,   PANEL_VALUE));
  tmp.i_maxiter     = atof((char *)xv_get(gem_sf_imaxiter,     PANEL_VALUE));
  tmp.i_gravity     = atof((char *)xv_get(gem_sf_igravity,     PANEL_VALUE));
  tmp.i_oscillation = atof((char *)xv_get(gem_sf_ioscillation, PANEL_VALUE));
  tmp.i_rotation    = atof((char *)xv_get(gem_sf_irotation,    PANEL_VALUE));
  tmp.i_shake       = atof((char *)xv_get(gem_sf_ishake,       PANEL_VALUE));
  tmp.no_insert     = xv_get(gem_sf_iskip,        PANEL_VALUE);

  tmp.a_maxtemp     = atof((char *)xv_get(gem_sf_amaxtemp,     PANEL_VALUE));
  tmp.a_starttemp   = atof((char *)xv_get(gem_sf_astarttemp,   PANEL_VALUE));
  tmp.a_finaltemp   = atof((char *)xv_get(gem_sf_afinaltemp,   PANEL_VALUE));
  tmp.a_maxiter     = atof((char *)xv_get(gem_sf_amaxiter,     PANEL_VALUE));
  tmp.a_gravity     = atof((char *)xv_get(gem_sf_agravity,     PANEL_VALUE));
  tmp.a_oscillation = atof((char *)xv_get(gem_sf_aoscillation, PANEL_VALUE));
  tmp.a_rotation    = atof((char *)xv_get(gem_sf_arotation,    PANEL_VALUE));
  tmp.a_shake       = atof((char *)xv_get(gem_sf_ashake,       PANEL_VALUE));
  tmp.no_arrange    = xv_get(gem_sf_askip,        PANEL_VALUE);

  tmp.o_maxtemp     = atof((char *)xv_get(gem_sf_omaxtemp,     PANEL_VALUE));
  tmp.o_starttemp   = atof((char *)xv_get(gem_sf_ostarttemp,   PANEL_VALUE));
  tmp.o_finaltemp   = atof((char *)xv_get(gem_sf_ofinaltemp,   PANEL_VALUE));
  tmp.o_maxiter     = atof((char *)xv_get(gem_sf_omaxiter,     PANEL_VALUE));
  tmp.o_gravity     = atof((char *)xv_get(gem_sf_ogravity,     PANEL_VALUE));
  tmp.o_oscillation = atof((char *)xv_get(gem_sf_ooscillation, PANEL_VALUE));
  tmp.o_rotation    = atof((char *)xv_get(gem_sf_orotation,    PANEL_VALUE));
  tmp.o_shake       = atof((char *)xv_get(gem_sf_oshake,       PANEL_VALUE));
  tmp.no_optimize   = xv_get(gem_sf_oskip,        PANEL_VALUE);

  tmp.random        = xv_get(gem_sf_random,       PANEL_VALUE);
  tmp.quality       = xv_get(gem_sf_quality,      PANEL_VALUE);

/* Werte pruefen */
  if (tmp.i_maxtemp<0.01 || tmp.i_maxtemp>10.0) ERR("Insert MaxTemp");
  if (tmp.i_starttemp<0.01 || tmp.i_starttemp>10.0) ERR("Insert StartTemp");
  if (tmp.i_finaltemp<0.01 || tmp.i_finaltemp>0.5) ERR("Insert FinalTemp");
  if (tmp.i_maxiter<1.0 || tmp.i_maxiter>100.0) ERR("Insert MaxIter");
  if (tmp.i_gravity<0.0 || tmp.i_gravity>1.0) ERR("Insert Gravity");
  if (tmp.i_oscillation<0.0 || tmp.i_oscillation>2.0) ERR("Insert Oscillation");
  if (tmp.i_rotation<0.0 || tmp.i_rotation>2.0) ERR("Insert Rotation");
  if (tmp.i_shake<0.0 || tmp.i_shake>5.0) ERR("Insert Shake");

  if (tmp.a_maxtemp<0.01 || tmp.a_maxtemp>10.0) ERR("Arrange MaxTemp");
  if (tmp.a_starttemp<0.01 || tmp.a_starttemp>10.0) ERR("Arrange StartTemp");
  if (tmp.a_finaltemp<0.01 || tmp.a_finaltemp>10.0) ERR("Arrange FinalTemp");
  if (tmp.a_maxiter<1.0 || tmp.a_maxiter>100.0) ERR("Arrange MaxIter");
  if (tmp.a_gravity<0.0 || tmp.a_gravity>1.0) ERR("Arrange Gravity");
  if (tmp.a_oscillation<0.0 || tmp.a_oscillation>2.0) ERR("Arrange Oscillation");
  if (tmp.a_rotation<0.0 || tmp.a_rotation>2.0) ERR("Arrange Rotation");
  if (tmp.a_shake<0.0 || tmp.a_shake>5.0) ERR("Arrange Shake");

  if (tmp.o_maxtemp<0.01 || tmp.o_maxtemp>10.0) ERR("Optimize MaxTemp");
  if (tmp.o_starttemp<0.01 || tmp.o_starttemp>10.0) ERR("Optimize StartTemp");
  if (tmp.o_finaltemp<0.01 || tmp.o_finaltemp>10.0) ERR("Optimize FinalTemp");
  if (tmp.o_maxiter<1.0 || tmp.o_maxiter>100.0) ERR("Optimize MaxIter");
  if (tmp.o_gravity<0.0 || tmp.o_gravity>1.0) ERR("Optimize Gravity");
  if (tmp.o_oscillation<0.0 || tmp.o_oscillation>2.0) ERR("Optimize Oscillation");
  if (tmp.o_rotation<0.0 || tmp.o_rotation>2.0) ERR("Optimize Rotation");
  if (tmp.o_shake<0.0 || tmp.o_shake>5.0) ERR("Optimize Shake");

/* Werte sichern */
  if (!ok) /* fehlerhafte werte */
  {
    error ("Spring Embedder (Gem): values not set\n");
    return ok;
  }
  akt_conf.i_maxtemp=tmp.i_maxtemp;
  akt_conf.i_starttemp=tmp.i_starttemp;
  akt_conf.i_finaltemp=tmp.i_finaltemp;
  akt_conf.i_maxiter=tmp.i_maxiter;
  akt_conf.i_gravity=tmp.i_gravity;
  akt_conf.i_oscillation=tmp.i_oscillation;
  akt_conf.i_rotation=tmp.i_rotation;
  akt_conf.i_shake=tmp.i_shake;
  akt_conf.no_insert=tmp.no_insert;

  akt_conf.a_maxtemp=tmp.a_maxtemp;
  akt_conf.a_starttemp=tmp.a_starttemp;
  akt_conf.a_finaltemp=tmp.a_finaltemp;
  akt_conf.a_maxiter=tmp.a_maxiter;
  akt_conf.a_gravity=tmp.a_gravity;
  akt_conf.a_oscillation=tmp.a_oscillation;
  akt_conf.a_rotation=tmp.a_rotation;
  akt_conf.a_shake=tmp.a_shake;
  akt_conf.no_arrange=tmp.no_arrange;

  akt_conf.o_maxtemp=tmp.o_maxtemp;
  akt_conf.o_starttemp=tmp.o_starttemp;
  akt_conf.o_finaltemp=tmp.o_finaltemp;
  akt_conf.o_maxiter=tmp.o_maxiter;
  akt_conf.o_gravity=tmp.o_gravity;
  akt_conf.o_oscillation=tmp.o_oscillation;
  akt_conf.o_rotation=tmp.o_rotation;
  akt_conf.o_shake=tmp.o_shake;
  akt_conf.no_optimize=tmp.no_optimize;

  akt_conf.random=tmp.random;
  akt_conf.quality=tmp.quality;

  return ok;
}

static void set_gem_subframe_values(void)
{
  xv_set(gem_sf_imaxtemp,
	PANEL_VALUE, float_2_ascii(akt_conf.i_maxtemp),
	NULL);
  xv_set(gem_sf_istarttemp,
	PANEL_VALUE, float_2_ascii(akt_conf.i_starttemp),
	NULL);
  xv_set(gem_sf_ifinaltemp,
	PANEL_VALUE, float_2_ascii(akt_conf.i_finaltemp),
	NULL);
  xv_set(gem_sf_imaxiter,
	PANEL_VALUE, float_2_ascii0(akt_conf.i_maxiter),
	NULL);
  xv_set(gem_sf_igravity,
	PANEL_VALUE, float_2_ascii(akt_conf.i_gravity),
	NULL);
  xv_set(gem_sf_ioscillation,
	PANEL_VALUE, float_2_ascii(akt_conf.i_oscillation),
	NULL);
  xv_set(gem_sf_irotation,
	PANEL_VALUE, float_2_ascii(akt_conf.i_rotation),
	NULL);
  xv_set(gem_sf_ishake,
	PANEL_VALUE, float_2_ascii(akt_conf.i_shake),
	NULL);
  xv_set(gem_sf_iskip,
	PANEL_VALUE, akt_conf.no_insert,
	NULL);

  xv_set(gem_sf_amaxtemp,
	PANEL_VALUE, float_2_ascii(akt_conf.a_maxtemp),
	NULL);
  xv_set(gem_sf_astarttemp,
	PANEL_VALUE, float_2_ascii(akt_conf.a_starttemp),
	NULL);
  xv_set(gem_sf_afinaltemp,
	PANEL_VALUE, float_2_ascii(akt_conf.a_finaltemp),
	NULL);
  xv_set(gem_sf_amaxiter,
	PANEL_VALUE, float_2_ascii0(akt_conf.a_maxiter),
	NULL);
  xv_set(gem_sf_agravity,
	PANEL_VALUE, float_2_ascii(akt_conf.a_gravity),
	NULL);
  xv_set(gem_sf_aoscillation,
	PANEL_VALUE, float_2_ascii(akt_conf.a_oscillation),
	NULL);
  xv_set(gem_sf_arotation,
	PANEL_VALUE, float_2_ascii(akt_conf.a_rotation),
	NULL);
  xv_set(gem_sf_ashake,
	PANEL_VALUE, float_2_ascii(akt_conf.a_shake),
	NULL);
  xv_set(gem_sf_askip,
	PANEL_VALUE, akt_conf.no_arrange,
	NULL);

  xv_set(gem_sf_omaxtemp,
	PANEL_VALUE, float_2_ascii(akt_conf.o_maxtemp),
	NULL);
  xv_set(gem_sf_ostarttemp,
	PANEL_VALUE, float_2_ascii(akt_conf.o_starttemp),
	NULL);
  xv_set(gem_sf_ofinaltemp,
	PANEL_VALUE, float_2_ascii(akt_conf.o_finaltemp),
	NULL);
  xv_set(gem_sf_omaxiter,
	PANEL_VALUE, float_2_ascii0(akt_conf.o_maxiter),
	NULL);
  xv_set(gem_sf_ogravity,
	PANEL_VALUE, float_2_ascii(akt_conf.o_gravity),
	NULL);
  xv_set(gem_sf_ooscillation,
	PANEL_VALUE, float_2_ascii(akt_conf.o_oscillation),
	NULL);
  xv_set(gem_sf_orotation,
	PANEL_VALUE, float_2_ascii(akt_conf.o_rotation),
	NULL);
  xv_set(gem_sf_oshake,
	PANEL_VALUE, float_2_ascii(akt_conf.o_shake),
	NULL);
  xv_set(gem_sf_oskip,
	PANEL_VALUE, akt_conf.no_optimize,
	NULL);

  xv_set(gem_sf_random,
	PANEL_VALUE, akt_conf.random,
	NULL);
  xv_set(gem_sf_quality,
	PANEL_VALUE, akt_conf.quality,
	NULL);

  if (akt_conf.no_insert || akt_conf.random)
  {
    xv_set(gem_sf_imaxtemp, PANEL_INACTIVE, TRUE, NULL);
    xv_set(gem_sf_istarttemp, PANEL_INACTIVE, TRUE, NULL);
    xv_set(gem_sf_ifinaltemp, PANEL_INACTIVE, TRUE, NULL);
    xv_set(gem_sf_imaxiter, PANEL_INACTIVE, TRUE, NULL);
    xv_set(gem_sf_igravity, PANEL_INACTIVE, TRUE, NULL);
    xv_set(gem_sf_ioscillation, PANEL_INACTIVE, TRUE, NULL);
    xv_set(gem_sf_irotation, PANEL_INACTIVE, TRUE, NULL);
    xv_set(gem_sf_ishake, PANEL_INACTIVE, TRUE, NULL);
  }
  else
  {
    xv_set(gem_sf_imaxtemp, PANEL_INACTIVE, FALSE, NULL);
    xv_set(gem_sf_istarttemp, PANEL_INACTIVE, FALSE, NULL);
    xv_set(gem_sf_ifinaltemp, PANEL_INACTIVE, FALSE, NULL);
    xv_set(gem_sf_imaxiter, PANEL_INACTIVE, FALSE, NULL);
    xv_set(gem_sf_igravity, PANEL_INACTIVE, FALSE, NULL);
    xv_set(gem_sf_ioscillation, PANEL_INACTIVE, FALSE, NULL);
    xv_set(gem_sf_irotation, PANEL_INACTIVE, FALSE, NULL);
    xv_set(gem_sf_ishake, PANEL_INACTIVE, FALSE, NULL);
  }
  if (akt_conf.no_arrange)
  {
    xv_set(gem_sf_amaxtemp, PANEL_INACTIVE, TRUE, NULL);
    xv_set(gem_sf_astarttemp, PANEL_INACTIVE, TRUE, NULL);
    xv_set(gem_sf_afinaltemp, PANEL_INACTIVE, TRUE, NULL);
    xv_set(gem_sf_amaxiter, PANEL_INACTIVE, TRUE, NULL);
    xv_set(gem_sf_agravity, PANEL_INACTIVE, TRUE, NULL);
    xv_set(gem_sf_aoscillation, PANEL_INACTIVE, TRUE, NULL);
    xv_set(gem_sf_arotation, PANEL_INACTIVE, TRUE, NULL);
    xv_set(gem_sf_ashake, PANEL_INACTIVE, TRUE, NULL);
  }
  else
  {
    xv_set(gem_sf_amaxtemp, PANEL_INACTIVE, FALSE, NULL);
    xv_set(gem_sf_astarttemp, PANEL_INACTIVE, FALSE, NULL);
    xv_set(gem_sf_afinaltemp, PANEL_INACTIVE, FALSE, NULL);
    xv_set(gem_sf_amaxiter, PANEL_INACTIVE, FALSE, NULL);
    xv_set(gem_sf_agravity, PANEL_INACTIVE, FALSE, NULL);
    xv_set(gem_sf_aoscillation, PANEL_INACTIVE, FALSE, NULL);
    xv_set(gem_sf_arotation, PANEL_INACTIVE, FALSE, NULL);
    xv_set(gem_sf_ashake, PANEL_INACTIVE, FALSE, NULL);
  }
  if (akt_conf.no_optimize)
  {
    xv_set(gem_sf_omaxtemp, PANEL_INACTIVE, TRUE, NULL);
    xv_set(gem_sf_ostarttemp, PANEL_INACTIVE, TRUE, NULL);
    xv_set(gem_sf_ofinaltemp, PANEL_INACTIVE, TRUE, NULL);
    xv_set(gem_sf_omaxiter, PANEL_INACTIVE, TRUE, NULL);
    xv_set(gem_sf_ogravity, PANEL_INACTIVE, TRUE, NULL);
    xv_set(gem_sf_ooscillation, PANEL_INACTIVE, TRUE, NULL);
    xv_set(gem_sf_orotation, PANEL_INACTIVE, TRUE, NULL);
    xv_set(gem_sf_oshake, PANEL_INACTIVE, TRUE, NULL);
  }
  else
  {
    xv_set(gem_sf_omaxtemp, PANEL_INACTIVE, FALSE, NULL);
    xv_set(gem_sf_ostarttemp, PANEL_INACTIVE, FALSE, NULL);
    xv_set(gem_sf_ofinaltemp, PANEL_INACTIVE, FALSE, NULL);
    xv_set(gem_sf_omaxiter, PANEL_INACTIVE, FALSE, NULL);
    xv_set(gem_sf_ogravity, PANEL_INACTIVE, FALSE, NULL);
    xv_set(gem_sf_ooscillation, PANEL_INACTIVE, FALSE, NULL);
    xv_set(gem_sf_orotation, PANEL_INACTIVE, FALSE, NULL);
    xv_set(gem_sf_oshake, PANEL_INACTIVE, FALSE, NULL);
  }
}

void gem_sf_run_proc(Panel_item item, Event *event)
{
  if(get_gem_subframe_values())
  {
    read_config();
    call_sgraph_proc(call_gem, NULL);
  }
}

static void set_gem_values(Panel_item item, Event *event)
{
  set_gem_subframe_values();
}

static void set_gem_default(Panel_item item, Event *event)
{
  set_gem_default_config();
  set_gem_subframe_values();
}

static void get_gem_values(Panel_item item, Event *event)
{
  get_gem_subframe_values();
}

static void create_gem_subframe(void)
{
  if (gem_subframe == (Graphed_pin_subframe)NULL)
    gem_subframe=new_graphed_pin_subframe((Frame)0);
  graphed_create_pin_subframe(gem_subframe, "Spring Embedder (Gem)");

  gem_sf_sett = xv_create(gem_subframe->panel, PANEL_MESSAGE,
	PANEL_LABEL_STRING, "Settings",
	XV_WIDTH, WIDTH1,
	XV_X, COL0,
	XV_Y, ROW0,
	NULL);
  gem_sf_maxtemp = xv_create(gem_subframe->panel, PANEL_MESSAGE,
	PANEL_LABEL_STRING, "MaxTemp:",
/*	WIN_BELOW, gem_sf_sett, */
	XV_WIDTH, WIDTH1,
	XV_X, COL0,
	XV_Y, ROW1,
	NULL);
  gem_sf_starttemp = xv_create(gem_subframe->panel, PANEL_MESSAGE,
	PANEL_LABEL_STRING, "StartTemp:",
/*	WIN_BELOW, gem_sf_maxtemp, */
	XV_WIDTH, WIDTH1,
	XV_X, COL0,
	XV_Y, ROW2,
	NULL);
  gem_sf_finaltemp = xv_create(gem_subframe->panel, PANEL_MESSAGE,
	PANEL_LABEL_STRING, "FinalTemp:",
/*	WIN_BELOW, gem_sf_starttemp, */
	XV_WIDTH, WIDTH1,
	XV_X, COL0,
	XV_Y, ROW3,
	NULL);
  gem_sf_maxiter = xv_create(gem_subframe->panel, PANEL_MESSAGE,
	PANEL_LABEL_STRING, "MaxIter:",
/*	WIN_BELOW, gem_sf_finaltemp, */
	XV_WIDTH, WIDTH1,
	XV_X, COL0,
	XV_Y, ROW4,
	NULL);
  gem_sf_gravity = xv_create(gem_subframe->panel, PANEL_MESSAGE,
	PANEL_LABEL_STRING, "Gravity:",
/*	WIN_BELOW, gem_sf_maxiter, */
	XV_WIDTH, WIDTH1,
	XV_X, COL0,
	XV_Y, ROW5,
	NULL);
  gem_sf_oscillation = xv_create(gem_subframe->panel, PANEL_MESSAGE,
	PANEL_LABEL_STRING, "Oscillation:",
/*	WIN_BELOW, gem_sf_gravity, */
	XV_WIDTH, WIDTH1,
	XV_X, COL0,
	XV_Y, ROW6,
	NULL);
  gem_sf_rotation = xv_create(gem_subframe->panel, PANEL_MESSAGE,
	PANEL_LABEL_STRING, "Rotation:",
/*	WIN_BELOW, gem_sf_oscillation, */
	XV_WIDTH, WIDTH1,
	XV_X, COL0,
	XV_Y, ROW7,
	NULL);
  gem_sf_shake = xv_create(gem_subframe->panel, PANEL_MESSAGE,
	PANEL_LABEL_STRING, "Shake:",
/*	WIN_BELOW, gem_sf_rotation, */
	XV_WIDTH, WIDTH1,
	XV_X, COL0,
	XV_Y, ROW8,
	NULL);
  gem_sf_skip = xv_create(gem_subframe->panel, PANEL_MESSAGE,
	PANEL_LABEL_STRING, "Skip",
/*	WIN_BELOW, gem_sf_skip, */
	XV_WIDTH, WIDTH1,
	XV_X, COL0,
	XV_Y, ROW9,
	NULL);

  gem_sf_insert = xv_create(gem_subframe->panel, PANEL_MESSAGE,
	PANEL_LABEL_STRING, "Insert",
/*	WIN_RIGHT_OF, gem_sf_sett, */
	XV_WIDTH, WIDTH,
	XV_X, COL1,
	XV_Y, ROW0,
	NULL);
  gem_sf_imaxtemp = xv_create(gem_subframe->panel, PANEL_TEXT,
	PANEL_LABEL_STRING, "",
/*	WIN_BELOW, gem_sf_insert, */
	XV_WIDTH, WIDTH,
	PANEL_VALUE_DISPLAY_WIDTH, PVDW,
	XV_X, COL1,
	XV_Y, ROW1,
	NULL);
  gem_sf_istarttemp = xv_create(gem_subframe->panel, PANEL_TEXT,
	PANEL_LABEL_STRING, "",
/*	WIN_BELOW, gem_sf_imaxtemp, */
	XV_WIDTH, WIDTH,
	PANEL_VALUE_DISPLAY_WIDTH, PVDW,
	XV_X, COL1,
	XV_Y, ROW2,
	NULL);
  gem_sf_ifinaltemp = xv_create(gem_subframe->panel, PANEL_TEXT,
	PANEL_LABEL_STRING, "",
/*	WIN_BELOW, gem_sf_istarttemp, */
	XV_WIDTH, WIDTH,
	PANEL_VALUE_DISPLAY_WIDTH, PVDW,
	XV_X, COL1,
	XV_Y, ROW3,
	NULL);
  gem_sf_imaxiter = xv_create(gem_subframe->panel, PANEL_TEXT,
	PANEL_LABEL_STRING, "",
/*	WIN_BELOW, gem_sf_ifinaltemp, */
	XV_WIDTH, WIDTH,
	PANEL_VALUE_DISPLAY_WIDTH, PVDW,
	XV_X, COL1,
	XV_Y, ROW4,
	NULL);
  gem_sf_igravity = xv_create(gem_subframe->panel, PANEL_TEXT,
	PANEL_LABEL_STRING, "",
/*	WIN_BELOW, gem_sf_imaxiter, */
	XV_WIDTH, WIDTH,
	PANEL_VALUE_DISPLAY_WIDTH, PVDW,
	XV_X, COL1,
	XV_Y, ROW5,
	NULL);
  gem_sf_ioscillation = xv_create(gem_subframe->panel, PANEL_TEXT,
	PANEL_LABEL_STRING, "",
/*	WIN_BELOW, gem_sf_igravity, */
	XV_WIDTH, WIDTH,
	PANEL_VALUE_DISPLAY_WIDTH, PVDW,
	XV_X, COL1,
	XV_Y, ROW6,
	NULL);
  gem_sf_irotation = xv_create(gem_subframe->panel, PANEL_TEXT,
	PANEL_LABEL_STRING, "",
/*	WIN_BELOW, gem_sf_ioscillation, */
	XV_WIDTH, WIDTH,
	PANEL_VALUE_DISPLAY_WIDTH, PVDW,
	XV_X, COL1,
	XV_Y, ROW7,
	NULL);
  gem_sf_ishake = xv_create(gem_subframe->panel, PANEL_TEXT,
	PANEL_LABEL_STRING, "",
/*	WIN_BELOW, gem_sf_irotation, */
	XV_WIDTH, WIDTH,
	PANEL_VALUE_DISPLAY_WIDTH, PVDW,
	XV_X, COL1,
	XV_Y, ROW8,
	NULL);
  gem_sf_iskip = xv_create(gem_subframe->panel, PANEL_CHECK_BOX,
	PANEL_LABEL_STRING, "",
	PANEL_NOTIFY_PROC, skip_notify_proc,
/*	WIN_BELOW, gem_sf_ishake, */
	XV_WIDTH, WIDTH,
	XV_X, COL1,
	XV_Y, ROW9,
	NULL);

  gem_sf_arrange = xv_create(gem_subframe->panel, PANEL_MESSAGE,
	PANEL_LABEL_STRING, "Arrange",
/*	WIN_RIGHT_OF, gem_sf_insert, */
	XV_WIDTH, WIDTH,
	XV_X, COL2,
	XV_Y, ROW0,
	NULL);
  gem_sf_amaxtemp = xv_create(gem_subframe->panel, PANEL_TEXT,
	PANEL_LABEL_STRING, "",
/*	WIN_BELOW, gem_sf_arrange, */
	XV_WIDTH, WIDTH,
	PANEL_VALUE_DISPLAY_WIDTH, PVDW,
	XV_X, COL2,
	XV_Y, ROW1,
	NULL);
  gem_sf_astarttemp = xv_create(gem_subframe->panel, PANEL_TEXT,
	PANEL_LABEL_STRING, "",
/*	WIN_BELOW, gem_sf_amaxtemp, */
	XV_WIDTH, WIDTH,
	PANEL_VALUE_DISPLAY_WIDTH, PVDW,
	XV_X, COL2,
	XV_Y, ROW2,
	NULL);
  gem_sf_afinaltemp = xv_create(gem_subframe->panel, PANEL_TEXT,
	PANEL_LABEL_STRING, "",
/*	WIN_BELOW, gem_sf_astarttemp, */
	XV_WIDTH, WIDTH,
	PANEL_VALUE_DISPLAY_WIDTH, PVDW,
	XV_X, COL2,
	XV_Y, ROW3,
	NULL);
  gem_sf_amaxiter = xv_create(gem_subframe->panel, PANEL_TEXT,
	PANEL_LABEL_STRING, "",
/*	WIN_BELOW, gem_sf_afinaltemp, */
	XV_WIDTH, WIDTH,
	PANEL_VALUE_DISPLAY_WIDTH, PVDW,
	XV_X, COL2,
	XV_Y, ROW4,
	NULL);
  gem_sf_agravity = xv_create(gem_subframe->panel, PANEL_TEXT,
	PANEL_LABEL_STRING, "",
/*	WIN_BELOW, gem_sf_amaxiter, */
	XV_WIDTH, WIDTH,
	PANEL_VALUE_DISPLAY_WIDTH, PVDW,
	XV_X, COL2,
	XV_Y, ROW5,
	NULL);
  gem_sf_aoscillation = xv_create(gem_subframe->panel, PANEL_TEXT,
	PANEL_LABEL_STRING, "",
/*	WIN_BELOW, gem_sf_agravity, */
	XV_WIDTH, WIDTH,
	PANEL_VALUE_DISPLAY_WIDTH, PVDW,
	XV_X, COL2,
	XV_Y, ROW6,
	NULL);
  gem_sf_arotation = xv_create(gem_subframe->panel, PANEL_TEXT,
	PANEL_LABEL_STRING, "",
/*	WIN_BELOW, gem_sf_aoscillation, */
	XV_WIDTH, WIDTH,
	PANEL_VALUE_DISPLAY_WIDTH, PVDW,
	XV_X, COL2,
	XV_Y, ROW7,
	NULL);
  gem_sf_ashake = xv_create(gem_subframe->panel, PANEL_TEXT,
	PANEL_LABEL_STRING, "",
/*	WIN_BELOW, gem_sf_arotation, */
	XV_WIDTH, WIDTH,
	PANEL_VALUE_DISPLAY_WIDTH, PVDW,
	XV_X, COL2,
	XV_Y, ROW8,
	NULL);
  gem_sf_askip = xv_create(gem_subframe->panel, PANEL_CHECK_BOX,
	PANEL_LABEL_STRING, "",
	PANEL_NOTIFY_PROC, skip_notify_proc,
/*	WIN_BELOW, gem_sf_ashake, */
	XV_WIDTH, WIDTH,
	XV_X, COL2,
	XV_Y, ROW9,
	NULL);

  gem_sf_optimize = xv_create(gem_subframe->panel, PANEL_MESSAGE,
	PANEL_LABEL_STRING, "Optimize",
/*	WIN_RIGHT_OF, gem_sf_arrange, */
	XV_WIDTH, WIDTH,
	XV_X, COL3,
	XV_Y, ROW0,
	NULL);
  gem_sf_omaxtemp = xv_create(gem_subframe->panel, PANEL_TEXT,
	PANEL_LABEL_STRING, "",
/*	WIN_BELOW, gem_sf_optimite, */
	XV_WIDTH, WIDTH,
	PANEL_VALUE_DISPLAY_WIDTH, PVDW,
	XV_X, COL3,
	XV_Y, ROW1,
	NULL);
  gem_sf_ostarttemp = xv_create(gem_subframe->panel, PANEL_TEXT,
	PANEL_LABEL_STRING, "",
/*	WIN_BELOW, gem_sf_omaxtemp, */
	XV_WIDTH, WIDTH,
	PANEL_VALUE_DISPLAY_WIDTH, PVDW,
	XV_X, COL3,
	XV_Y, ROW2,
	NULL);
  gem_sf_ofinaltemp = xv_create(gem_subframe->panel, PANEL_TEXT,
	PANEL_LABEL_STRING, "",
/*	WIN_BELOW, gem_sf_ostarttemp, */
	XV_WIDTH, WIDTH,
	PANEL_VALUE_DISPLAY_WIDTH, PVDW,
	XV_X, COL3,
	XV_Y, ROW3,
	NULL);
  gem_sf_omaxiter = xv_create(gem_subframe->panel, PANEL_TEXT,
	PANEL_LABEL_STRING, "",
/*	WIN_BELOW, gem_sf_ofinaltemp, */
	XV_WIDTH, WIDTH,
	PANEL_VALUE_DISPLAY_WIDTH, PVDW,
	XV_X, COL3,
	XV_Y, ROW4,
	NULL);
  gem_sf_ogravity = xv_create(gem_subframe->panel, PANEL_TEXT,
	PANEL_LABEL_STRING, "",
/*	WIN_BELOW, gem_sf_omaxiter, */
	XV_WIDTH, WIDTH,
	PANEL_VALUE_DISPLAY_WIDTH, PVDW,
	XV_X, COL3,
	XV_Y, ROW5,
	NULL);
  gem_sf_ooscillation = xv_create(gem_subframe->panel, PANEL_TEXT,
	PANEL_LABEL_STRING, "",
/*	WIN_BELOW, gem_sf_ogravity, */
	XV_WIDTH, WIDTH,
	PANEL_VALUE_DISPLAY_WIDTH, PVDW,
	XV_X, COL3,
	XV_Y, ROW6,
	NULL);
  gem_sf_orotation = xv_create(gem_subframe->panel, PANEL_TEXT,
	PANEL_LABEL_STRING, "",
/*	WIN_BELOW, gem_sf_ooscillation, */
	XV_WIDTH, WIDTH,
	PANEL_VALUE_DISPLAY_WIDTH, PVDW,
	XV_X, COL3,
	XV_Y, ROW7,
	NULL);
  gem_sf_oshake = xv_create(gem_subframe->panel, PANEL_TEXT,
	PANEL_LABEL_STRING, "",
/*	WIN_BELOW, gem_sf_orotation, */
	XV_WIDTH, WIDTH,
	PANEL_VALUE_DISPLAY_WIDTH, PVDW,
	XV_X, COL3,
	XV_Y, ROW8,
	NULL);
  gem_sf_oskip = xv_create(gem_subframe->panel, PANEL_CHECK_BOX,
	PANEL_LABEL_STRING, "",
	PANEL_NOTIFY_PROC, skip_notify_proc,
/*	WIN_BELOW, gem_sf_oshake, */
	XV_WIDTH, WIDTH,
	XV_X, COL3,
	XV_Y, ROW9,
	NULL);

  gem_sf_random = xv_create(gem_subframe->panel, PANEL_CHECK_BOX,
	PANEL_LABEL_STRING, "Random Placement",
	PANEL_LABEL_WIDTH, WIDTH1+WIDTH,
	PANEL_NOTIFY_PROC, skip_notify_proc,
/*	WIN_BELOW, gem_sf_skip, */
	XV_X, COL0,
	XV_Y, ROWA,
	NULL);
  gem_sf_quality = xv_create(gem_subframe->panel, PANEL_CHECK_BOX,
	PANEL_LABEL_STRING, "Quality Check",
	PANEL_LABEL_WIDTH, WIDTH1+WIDTH,
/*	WIN_BELOW, gem_sf_random, */
	XV_X, COL0,
	XV_Y, ROWB,
	NULL);

  gem_sf_set = xv_create(gem_subframe->panel, PANEL_BUTTON,
	PANEL_LABEL_STRING, "Set",
	PANEL_NOTIFY_PROC, get_gem_values,
/*	WIN_BELOW, gem_sf_quality, */
	XV_WIDTH, WIDTH,
	XV_X, COL0,
	XV_Y, ROWC,
	NULL);
  gem_sf_reset = xv_create(gem_subframe->panel, PANEL_BUTTON,
	PANEL_LABEL_STRING, "Reset",
	PANEL_NOTIFY_PROC, set_gem_values,
/*	WIN_RIGHT_OF, gem_sf_set, */
	XV_WIDTH, WIDTH,
	XV_X, COL1,
	XV_Y, ROWC,
	NULL);
  gem_sf_default = xv_create(gem_subframe->panel, PANEL_BUTTON,
	PANEL_LABEL_STRING, "Default",
	PANEL_NOTIFY_PROC, set_gem_default,
/*	WIN_RIGHT_OF, gem_sf_set, */
	XV_WIDTH, WIDTH,
	XV_X, COL2,
	XV_Y, ROWC,
	NULL);
  gem_sf_run = xv_create(gem_subframe->panel, PANEL_BUTTON,
	PANEL_LABEL_STRING, "Run",
	PANEL_NOTIFY_PROC, gem_sf_run_proc,
/*	WIN_RIGHT_OF, gem_sf_default, */
	XV_WIDTH, WIDTH,
	XV_X, COL3,
	XV_Y, ROWC,
	NULL);

    window_fit(gem_subframe->panel);
  window_fit(gem_subframe->frame);
}

static int showing_gem_subframe()
{
  return showing_graphed_pin_subframe(gem_subframe);
}

void show_gem_subframe(void)
{
  if (!showing_gem_subframe()) create_gem_subframe();
  compute_subwindow_position_at_graph_of_current_selection(gem_subframe->frame);
  set_gem_subframe_values();
  xv_set(gem_subframe->frame, WIN_SHOW, TRUE, 0);
  gem_subframe->showing=TRUE;
}

void menu_gem_show_subframe(Menu menu, Menu_item menu_item)
{
  show_gem_subframe();
}
