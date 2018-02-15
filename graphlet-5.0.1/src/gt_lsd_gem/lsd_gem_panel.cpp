/* This software is distributed under the Lesser General Public License */
#include <xview/xview.h>
#include <xview/panel.h>
#include <xview/notice.h>

#include <iostream.h>
#include <fstream.h>
#include <stdio.h>

#include <sgraph/sgraph.h>
#include <sgraph/sgraph_interface.h>

#include <graphed/graphed_pin_sf.h>
#include <graphed/error.h>

#include "gem_panel.h"
#include "gem_main.h"
#include "embedder.h"
#include "global.h"

int ELEN;

typedef struct {
	double	i_maxtemp, i_starttemp, i_finaltemp, i_maxiter, i_gravity,
		i_oscillation, i_rotation, i_shake, a_maxtemp, a_starttemp,
		a_finaltemp, a_maxiter, a_gravity, a_oscillation, a_rotation,
		a_shake, o_maxtemp, o_starttemp, o_finaltemp, o_maxiter,
		o_gravity, o_oscillation, o_rotation, o_shake;
	int	random, quality;
	int	no_insert, no_arrange, no_optimize;
} gem_config;


static gem_config akt_conf;
int do_random, check_quality;

/* Walter 8.7.96 Eingabe der Paramter von graphlet aus */

void set_gem_default_config(
	double insert_max_temp, double insert_start_temp,
	double insert_final_temp, double insert_max_iter,
	double insert_gravity, double insert_oscilation,
	double insert_rotation, double insert_shake,
	int	insert_skip,
	double	arrange_max_temp, double arrange_start_temp, double
		arrange_final_temp, double arrange_max_iter, double
		arrange_gravity, double arrange_oscilation, double
		arrange_rotation, double arrange_shake,
	int	arrange_skip,
	double	optimize_max_temp, double optimize_start_temp, double
		optimize_final_temp, double optimize_max_iter, double
		optimize_gravity, double optimize_oscilation, double
		optimize_rotation, double optimize_shake,
	int	optimize_skip,
	int	the_random, int quality, int default_edgelength)
{
  akt_conf.i_maxtemp		= insert_max_temp;
  akt_conf.i_starttemp		= insert_start_temp;
  akt_conf.i_finaltemp		= insert_final_temp;
  akt_conf.i_maxiter		= insert_max_iter;
  akt_conf.i_gravity		= insert_gravity;
  akt_conf.i_oscillation	= insert_oscilation;
  akt_conf.i_rotation		= insert_rotation;
  akt_conf.i_shake		= insert_shake;
  
  akt_conf.a_maxtemp		= arrange_max_temp;
  akt_conf.a_starttemp		= arrange_start_temp;
  akt_conf.a_finaltemp		= arrange_final_temp;
  akt_conf.a_maxiter		= arrange_max_iter;
  akt_conf.a_gravity		= arrange_gravity;
  akt_conf.a_oscillation	= arrange_oscilation;
  akt_conf.a_rotation		= arrange_rotation;
  akt_conf.a_shake		= arrange_shake;
  
  akt_conf.o_maxtemp		= optimize_max_temp;
  akt_conf.o_starttemp		= optimize_start_temp;
  akt_conf.o_finaltemp		= optimize_final_temp;
  akt_conf.o_maxiter		= optimize_max_iter;
  akt_conf.o_gravity		= optimize_gravity;
  akt_conf.o_oscillation	= optimize_oscilation;
  akt_conf.o_rotation		= optimize_rotation;
  akt_conf.o_shake		= optimize_shake;
  
  akt_conf.random		= the_random;
  akt_conf.quality		= quality;
  akt_conf.no_insert		= insert_skip;
  akt_conf.no_arrange		= arrange_skip;
  akt_conf.no_optimize		= optimize_skip;
  ELEN = default_edgelength;
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

void set_gem_default_config_old(void)
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

