
#include <string.h>
#include <stdio.h>
#define bits_int 32
#define makearray(typ,maxindex) (typ*)calloc(((maxindex) > 1) ? (maxindex)+1 : 3,sizeof(typ))
#define cleararray(typ,name,maxindex) memset(name,0,sizeof(typ)*((maxindex)+1))
/*#define charray(typ,name,maxindex) check_array(name,maxindex,"typ","name");*/
/*#define chint(expr) printf("expr");printf(" = %i\n",expr)*/
#define mitrauf(v1,v2) if (v1 < (v2)) v1=v2 ;
#define mitrunter(v1,v2) if (v1 > (v2)) v1=v2;
#define swap(typ,v1,v2) {typ t=v1;v1=v2;v2=t;}

/*void check_array(name,maxindex,typ,n) void *name;int maxindex;char *typ,*n;
  {
  char ctlstr[6];int i;
  int *intptr=name;
  long int *longptr=name;
  short int *shortptr=name;
  unsigned *unsignedptr=name;
  float *floatptr=name;
  double *doubleptr=name;
  printf("%s = { ",n);
  for (i=1;i <= maxindex;i++)
    {
    if (! strcmp(typ,"int"))
      printf("%i ",intptr[i]);
    if (! strcmp(typ,"long int") || ! strcmp(typ,"long"))
      printf("%i ",longptr[i]);
    if (! strcmp(typ,"short int") || ! strcmp(typ,"short"))
      printf("%i ",shortptr[i]);
    if (! strcmp(typ,"float"))
      printf("%g ",floatptr[i]);
    if (! strcmp(typ,"double"))
      printf("%g ",doubleptr[i]);
    if (! strcmp(typ,"unsigned"))
      printf("%u ",unsignedptr[i]);
    }
  printf("}\n");
  }*/

int get_bit(int *vector, int nr)
{return((vector[nr/bits_int] >> (nr%bits_int)) & 1);}

void put_bit(int *vector, int nr, int value)
{
  if (value)
    vector[nr/bits_int] |= 1<<(nr%bits_int);
  else
    vector[nr/bits_int] &= ~(1<<(nr%bits_int));
  }

/* ende der standard-#defines */

#ifndef FLOAT_MAX 
#define FLOAT_MAX 1e9
#endif
#define max_nr_vvalues 3
#define max_nr_evalues 3
/*#define show(expr) printf("expr");show_graph(expr)*/


struct mfgraph 
  {
  int *v,*neighbor,*previous,*next,*degree_out,*degree_in       /*arrays*/
      ,new_v,new_e,nr_v,nr_e,nr_vvalues,nr_evalues,range_v,range_e,max_nr_v,max_nr_e   /*zaehler*/
      ,directed,multigraph,bipartite,tree_root;     /*statusbits*/
  float *vvalues[max_nr_vvalues],*evalues[max_nr_evalues];       /*ecken-,kantenbewertungen*/
  };
/* bei ungerichteten graphen werden die grade in 'degree_out[]' gespeichert */

  
struct mfgraph *init_graph(int maxnr_v, int maxnr_e, int nrvvalues, int nrevalues, int direct);
  /* erzeugt eine struktur graph mit den dazugehoerigen arrays und initialisiert alles */
void delete_mygraph(struct mfgraph *gptr);
void clear_graph(struct mfgraph *gptr);
  /* macht 'gptr' zu einem nullgraphen,loescht ihn aber nicht */
int insert_v(struct mfgraph *gptr, float *vvaluesptr);
  /*fuegt eine neue ecke in den graphen 'gptr' ein,liefert deren nummer zurueck.
    falls 'max_nr_v' ueberschritten wuerde,wird keine ecke eingefuegt,sondern 0 zurueckgeliefert.
    'vvaluesptr' zeigt auf eventuelle werte fuer diese neue ecke.*/
int insert_e(struct mfgraph *gptr, int from, int to, float *evaluesptr);
  /*fuegt in den graphen 'gptr' eine kante von der ecke 'from' zur ecke 'to' ein.
    falls 'max_nr_e' uebersch,blockstart=1ritten wuerde oder eine der ecken nicht ex. ,wird keine 
    kante eingefuegt,sondern 0 zurueckgeliefert.
    'evaluesptr' zeigt auf eventuelle werte fuer diese neue kante.*/
int delete_e(struct mfgraph *gptr, int from, int to);
  /*loescht die erste kante von der ecke 'from'zur ecke 'to',wenn vorhanden.
    liefert deren nummer,sonst 0.*/
int delete_v(struct mfgraph *gptr, int x);
  /*loescht die ecke 'x',wenn vorhanden,einschliesslich aller inzidenten kanten.
    liefert die anzahl der geloeschten kanten + 1 !!!!!! ,sonst 0 */


int kompl(int x)       
  /*liefert den zu 'x' komplementaeren index fuer die arrays 'neighbor','previous','next'*/
  {return ((x % 2) ? x+1 : x-1 );}


/* Implementationsteil */

struct mfgraph *init_graph(int maxnr_v, int maxnr_e, int nrvvalues, int nrevalues, int direct)
{
  struct mfgraph *gptr;int i;
  gptr=(struct mfgraph *) malloc(sizeof (struct mfgraph));
  gptr->v=makearray(int,maxnr_v);
  gptr->max_nr_v=maxnr_v;
  gptr->nr_v=0;
  gptr->new_v=1;
  gptr->range_v=0;
  gptr->nr_vvalues=nrvvalues;
  for (i=0;i<nrvvalues;i++)
    gptr->vvalues[i]=makearray(float,maxnr_v);
  gptr->directed=direct;
  gptr->degree_out=makearray(int,maxnr_v);
  if (direct)
    gptr->degree_in=makearray(int,maxnr_v);
  gptr->neighbor=makearray(int,2*maxnr_e);
  gptr->previous=makearray(int,2*maxnr_e);
  gptr->next=makearray(int,2*maxnr_e);
  gptr->max_nr_e=maxnr_e;
  gptr->nr_evalues=nrevalues;
  for (i=0;i<nrevalues;i++)
    gptr->evalues[i]=makearray(float,maxnr_e);
  gptr->nr_e=0;
  gptr->new_e=1;
  gptr->range_e=0;
  gptr->multigraph=0;
  gptr->bipartite=0;
  gptr->tree_root=0;
  return(gptr);
  }

void delete_mygraph(struct mfgraph *gptr)
{
  int i;
  free(gptr->degree_out);
  if (gptr->directed)
    free(gptr->degree_in);
  for (i=0;i < gptr->nr_evalues;i++)
    free(gptr->evalues[i]);
  for (i=0;i < gptr->nr_vvalues;i++)
    free(gptr->vvalues[i]);
  free(gptr->v);
  free(gptr->neighbor);free(gptr->previous);free(gptr->next);
  free(gptr);
  }

void clear_graph(struct mfgraph *gptr)
{
  gptr->nr_v=0;gptr->range_v=0;gptr->new_v=1;
  gptr->nr_e=0;gptr->range_e=0;gptr->new_e=1;    /* leere 'gptr' */
  cleararray(int,gptr->degree_out,gptr->max_nr_v);
  if (gptr->directed)
    cleararray(int,gptr->degree_in,gptr->max_nr_v);
  }

/*void show_graph(struct graph *gptr)
  {
  int i,j,h;
  getchar();
  printf("\n\nshow:\nmax_nr_v=%i\nrange_v=%i\nnr_v=%i\nnew_v=%i\ntree_root=%i\n",
         gptr->max_nr_v,gptr->range_v,gptr->nr_v,gptr->new_v,gptr->tree_root);
  printf("\nmax_nr_e=%i\nrange_e=%i\nnr_e=%i\nnew_e=%i\ndirected=%i\n",
         gptr->max_nr_e,gptr->range_e,gptr->nr_e,gptr->new_e,gptr->directed);
  for (i=1;i<=gptr->range_v;i++)
    {
    h=gptr->v[i];
    if (h>=0)
      {
      printf("\necke %i:",i);
      for (j=0;j<gptr->nr_vvalues;j++)
        printf("%f ",gptr->vvalues[j][i]);
      printf(",nachbarn ",i);
      while (h != 0)
        {
        printf("%i ",gptr->neighbor[h]);
        h=gptr->next[h];
        }
      }
    }
  for (i=1;i<=gptr->range_e;i++)
    {
    h=2*i;
    if (gptr->next[h] >= 0)
      {
      printf("\nkante %i:",i);
      for (j=0;j<gptr->nr_evalues;j++)
        printf("%f ",gptr->evalues[j][i]);
      if (gptr->neighbor[h] < 0)
        printf(",von %i nach %i",-gptr->neighbor[h],gptr->neighbor[h-1]);
      else
        printf(",von %i nach %i",gptr->neighbor[h],abs(gptr->neighbor[h-1]));        
      }
    }
  getchar();
  }*/

void ends(struct mfgraph *gptr, int e, int *from, int *to)
{
  if (gptr->neighbor[2*e] < 0)
    {
    *from=-gptr->neighbor[2*e];
    *to=gptr->neighbor[2*e-1];
    }
  else
    {
    *to=gptr->neighbor[2*e];
    *from=abs(gptr->neighbor[2*e-1]);
    }
  }

int insert_v(struct mfgraph *gptr, float *vvaluesptr)
{int h,i;
  if (gptr->nr_v < gptr->max_nr_v)
    {
    h=gptr->new_v;
    for (i=0;i<gptr->nr_vvalues;i++)
      gptr->vvalues[i][h]=vvaluesptr[i];
    if (gptr->range_v < h)
      {                                /*falls keine luecken im array 'gptr->v' ,dann vergroessern*/
      gptr->v[gptr->new_v++]=0;
      gptr->range_v+=1;
      }
    else
      {                                 /*sonst luecke auffuellen*/
      gptr->new_v=-gptr->v[h];
      gptr->v[h]=0;
      }
    gptr->nr_v++;
    return(h);
    }
  else
    return(0);
  }

int insert_e(struct mfgraph *gptr, int from, int to, float *evaluesptr)
{int e,i,bisheriger_listenanfang;
  from=abs(from);to=abs(to);
  if (gptr->v[from] >= 0 && gptr->v[to] >=0 && gptr->nr_e < gptr->max_nr_e)
    {
    e=gptr->new_e;
    for (i=0;i<gptr->nr_evalues;i++)
      gptr->evalues[i][e]=evaluesptr[i];
    if (gptr->range_e < e)
      {                                      /* falls keine luecken in den kanten-arrays, */
      gptr->new_e++;                          /* dann auffuellen */
      gptr->range_e++;
      }
    else
      gptr->new_e=-gptr->next[2*e];          /* sonst luecke auffuellen */

    bisheriger_listenanfang=gptr->v[from];    /* erweitere adjazenzliste der ecke 'from'*/
    gptr->next[2*e-1]=bisheriger_listenanfang;
    gptr->neighbor[2*e-1]=to;
    gptr->previous[2*e-1]=-from;
    gptr->previous[bisheriger_listenanfang]=2*e-1;
    gptr->v[from]=2*e-1;

    bisheriger_listenanfang=gptr->v[to];        /* erweitere adjazenzliste der ecke 'to'*/
    gptr->next[2*e]=bisheriger_listenanfang;
    gptr->neighbor[2*e]=(gptr->directed) ? -from : from; /* evtl. kante orientieren */
    gptr->previous[2*e]=-to;
    gptr->previous[bisheriger_listenanfang]=2*e;
    gptr->v[to]=2*e;
    gptr->nr_e++;
    if (gptr->directed)
      {
      gptr->degree_in[to]++;
      gptr->degree_out[from]++;
      }
    else
      {
      gptr->degree_out[from]++;
      gptr->degree_out[to]++;
      }
    return(e);
    }
  else
    return(0);
  }


void del(struct mfgraph *gptr, int x)
{int p=gptr->previous[x],s=gptr->next[x];
  gptr->previous[s]=p;
  if (p>0)
    gptr->next[p]=s;
  else
    gptr->v[-p]=s;
  }

int delete_e(struct mfgraph *gptr, int from, int to)
{
  int h,e;
  
  from=abs(from);
  to=abs(to);
  h=gptr->v[from];
  if (h >= 0)
    {
    while (h!=0 && abs(gptr->neighbor[h]) != to)
      h=gptr->next[h];
    if (h != 0)
      {
      del(gptr,h);
      del(gptr,kompl(h));
      e=(h+1)/2;                            /* 'e' ist nummer der geloeschten kante */
      gptr->next[2*e]=-gptr->new_e;
      gptr->new_e=e;
      gptr->nr_e--;
      if (gptr->directed)
        {
        gptr->degree_in[to]--;
        gptr->degree_out[from]--;
        }
      else
        {
        gptr->degree_out[from]--;
        gptr->degree_out[to]--;
        }
      return(e);
      }
    else 
      return(0);                             /* kante ex. nicht */
    }
  else
    return(0);                               /* ecke 'from' ex. nicht */
  }


int delete_v(struct mfgraph *gptr, int x)
{
  int h=gptr->v[x],h2,v,w,e_deleted=1;
  if (h < 0)
    return(0);    /* ecke 'x' ex. nicht */
  else
    {
    while (h>0)
      {
      h2=gptr->next[h];
      e_deleted++;
      ends(gptr,(h+1)/2,&v,&w);
      delete_e(gptr,v,w);
      h=h2;
      }
    gptr->v[x]=-gptr->new_v;
    gptr->new_v=x;
    gptr->nr_v--;
    return(e_deleted);
    }
  } 
     

int max_flow(struct mfgraph *gptr, int source, int target, float *flowptr);
  /* interpretiert die erste kantenbewertung von 'gptr' als kapazitaeten und erzeugt als
     zweite kantenbewertung einen maximalen fluss.
     liefert 0,wenn dies unmoeglich ist,sonst 1.algorithmus von dimic.*/

static struct mfgraph *xnetptr;
static int xpathlength,xsource_found,*xqueue,*xnumber;

float spielraum(struct mfgraph *gptr, int e)
{return (e > 0) ? gptr->evalues[0][e]-gptr->evalues[1][e] : gptr->evalues[1][-e];}
    
void max_flow_dfs(int v)
{
  int h,w,e;
  if (v == 1)
    {
    xsource_found=1;
    return;
    }
  xqueue[v]=1;
  for (h=xnetptr->v[v];h > 0 && ! xsource_found;h=xnetptr->next[h])
    {
    e=(h+1)/2;               /* 'e' ist nr. der gegangenen kante */
    w=xnetptr->neighbor[h];
    if (w < 0 && ! xqueue[abs(w)])    /*gehe nur entlang rueckwaertskanten */
      max_flow_dfs(-w);
    }
  if (xsource_found) 
    xnumber[++xpathlength]=e;
  }

int max_flow(struct mfgraph *gptr, int source, int target, float *flowptr)
{
  int target_net,bot,mid,ceiling,nr_layer,n,n2,nabs,v,w,h,i,e,e_net,nr_delete_phase,*org_e;
  float min_spielraum,spielraum_e;
  if (gptr->nr_evalues > 2)
    return 0;
  for (i=1;i<=gptr->range_e;i++)
    if (gptr->next[2*i] >= 0 && gptr->evalues[0][i] < 0)
      return 0;
  xnetptr=init_graph(gptr->nr_v,gptr->nr_e,0,0,1);
  xnumber=makearray(int,gptr->range_v);  
  xqueue=makearray(int,2*gptr->nr_v);   /* fuer die breitensuche,spaeter zum speichern der evtl. zu
                                       loeschenden ecken */
  org_e=makearray(int,gptr->nr_e);  /* speichert die nr. der orginalkante einer kante von 'netptr' */
  *flowptr=0;
  cleararray(float,gptr->evalues[1],gptr->range_e);    /*starte mit trivialem fluss */
  while (1)
    {
    mid=bot=ceiling=1;
    nr_layer=2;
    clear_graph(xnetptr);
    cleararray(int,xnumber,gptr->range_v);
    insert_v(xnetptr,NULL);
    xnumber[source]=1;           /* der erste layer besteht nur aus der ecke 'source' */
    xqueue[1]=source;
    do
      {      /*chint(nr_layer);*/
      while (bot<=mid)          /* bearbeite alle in 'queue' gespeicherten ecken von 'bot' bis 'mid'*/
        {
        h=gptr->v[xqueue[bot]];     
        while (h!=0)                          /* bearbeite alle nachbarn der ecke 'queue[bot]'*/
          {
          e=(h+1)/2;     /*chint(e);*/
          n=gptr->neighbor[h];
          nabs=abs(n);
          if (((n > 0 && gptr->evalues[0][e]-gptr->evalues[1][e] > 0) || /*falls vorwaertskante */
               (n < 0 && gptr->evalues[1][e]>0))                      /*falls rueckwaertskante */
              && (xnumber[nabs] > mid || xnumber[nabs] == 0))
            {                               /* useful kante gefunden,von 'queue[bot]' nach 'nabs' */
            /*printf("nuetzliche kante von %i nach %i gefunden\n",xqueue[bot],nabs);*/
            if (xnumber[nabs] == 0)
              {
              ceiling++;      /*chint(ceiling);*/
              n2=insert_v(xnetptr,NULL);
              if (n2!=ceiling)
                printf("etwas stimmt nicht");
              xnumber[nabs]=ceiling;
              xqueue[ceiling]=nabs;
              }
            else
              n2=xnumber[nabs];/*printf("ziehe kante in 'xnetptr' von %i nach %i\n",bot,n2);*/
            org_e[insert_e(xnetptr,bot,n2,NULL)]=(n > 0) ? e : -e ;
            }
          h=gptr->next[h];
          }
        bot++;     /*chint(bot);*/
        }
      mid=ceiling;nr_layer++;
      }              
    while (xnumber[target] == 0 && bot <= ceiling);
    target_net=xnumber[target];
    /*charray(int,org_e,xnetptr->nr_e);charray(int,xnumber,gptr->range_v);show(xnetptr);*/
    if (bot > ceiling)
      break;              /* kein layered network moeglich,der fluss ist max. */
  
    while (1)    /* suche zunehmende wege in 'netptr' */
      {
      cleararray(int,xqueue,xnetptr->nr_v);
      xsource_found=0;
      xpathlength=0;
      max_flow_dfs(target_net);  /* suche von 'target' rueckwaerts einen zunehmenden weg nach 'source' innerhalb 'netptr' */
      if (! xsource_found)
        break;              /*es ex. kein zunehmender weg */
      min_spielraum=FLOAT_MAX;      /* bestimme den spielraum entlang dieses weges */
      for (i=1;i <= xpathlength;i++)
        {
        spielraum_e=spielraum(gptr,org_e[xnumber[i]]);
        min_spielraum=(spielraum_e < min_spielraum) ? spielraum_e : min_spielraum;
        }
      *flowptr+=min_spielraum;
      bot=1;
      mid=0;
      cleararray(int,xqueue,2*gptr->nr_v);
      for (i=1; i<= xpathlength;i++)
        {                      /* verstaerke den fluss entlang des gefundenen weges,dessen kanten in 'xnumber' gespeichert
                                  sind.kanten,die nicht weiter verstaerkt werden koennen,werden in 'netptr' geloescht */
        e_net=xnumber[i];
        e=org_e[e_net];
        if (e > 0)        /* falls die richtung der kanten 'e_net' und 'abs(e)' uebereinstimmen */
          gptr->evalues[1][e]+=min_spielraum;
        else
          gptr->evalues[1][-e]-=min_spielraum;
        e=abs(e);
        if (spielraum(gptr,e) == 0)
          {                           /* loesche die kante 'e_net' in 'netptr',trage ihre endpunkte in 'xqueue' ein */
          ends(xnetptr,e_net,&v,&w);
          if (xqueue[mid-1] != v && v != 1)  /* steht 'v' schon in 'queue' ? */
            xqueue[++mid]=v;
          if (w != target_net)
            xqueue[++mid]=w;/*printf("loesche kante nr.%i von %i nach %i",e_net,v,w);*/
          delete_e(xnetptr,v,w);
          }
        }
      ceiling=mid;
      nr_delete_phase=1;
      cleararray(int,xnumber,xnetptr->nr_v);
      while (bot <= ceiling)
        {
        while (bot <= mid)
          {                    /*arbeite ecken in 'xvqueue' zwischen 'bot' und 'mid' ab,ausser '1' und
                                 'target_net',vermeide ueberlaeufe von 'xqueue' */
          v=xqueue[bot % (2*gptr->nr_v)];
          if (xnetptr->degree_in[v] == 0 || xnetptr->degree_out[v] == 0)
            {
            for (h=xnetptr->v[v];h>0;h=xnetptr->next[h])
              {
              w=abs(xnetptr->neighbor[h]);
              if (xnumber[w] != nr_delete_phase && w != 1 && w != target_net)
                xqueue[++ceiling % (2*gptr->nr_v)]=w;
              xnumber[w]=nr_delete_phase;
              }
            delete_v(xnetptr,v);
            }
          bot++;
          }
        mid=ceiling;nr_delete_phase++;
        }
      }
    }            
   
  free(org_e);free(xnumber);free(xqueue);delete_mygraph(xnetptr);
  return 1;
  }        
/*
-breitensuche zum erzeugen des gerichteten layered network,wenn kein network ex.,break
-kantenbewertung 'org_e[]' des network sind nummern der kanten in 'gptr';positiv,wenn die richtungen uebereinstimmen,sonst negativ
{
-tiefensuche zum finden von aufsteigenden wegen,starte bei 'target_net',gehe nur rueckwaerts,wenn angekommen,flag 'xsource_found' setzen und die gegangenen kanten in 'xnumber' speichern;wenn nicht erreichbar, break
--min aller dieser kanten bestimmen,reduzieren,dabei evtl. kanten loeschen und endpunkte in 'xqueue' speichern
- {ecken in 'xqueue' auf nullgrade pruefen,evtl deren nachbarn in 'xqueue' speichern und erstere loeschen}
}*/

int max_weight_matching(struct mfgraph *gptr, int **mateptr, int **mateedgeptr);
  /* interpretiert die erste kantenbewertung des graphen 'gptr' als gewichte und berechnet
     eine korrespondenz (matching) mit maximalem gewicht.'gptr->v[]` darf keine geloeschten
     ecken enthalten,d.h. es muss gelten :gptr->nr_v == gptr->range_v.die gewichte muessen 
     alle positiv sein.die funktion liefert 0,falls die berechnung der korrespondenz un-
     moeglich ist,sonst 1.der aufruf erfolgt durch `max_weight_matching(gptr,&mate,&mateedge)',
     ,danach zeigen 'mate' und 'mateedge' auf int-arrays;'mate[v]' liefert dann die partner-
     ecke der ecke `v`,`mateedge[v]` die kante dieser partnerbindung.
     falls 'mate[vertex] <= 0',so gehoert 'vertex' nicht zur korrespondenz.
     algorithmus von edmonds,johnson 1970.komplexitaet O(gptr->nr_v^3) !!
     theoretischer hintergrund: formuliere das problem als 'linear programing problem',
     und loese das dazu duale problem.
     literatur:minieka,optimization algorithms for networks and graphs.
  */

static int *xq,*xexplored,*xd2heap,*xd1heap,*xd2heaptop,*xd1heaptop,*xd2heapbot,*xd1heapbot,xnr_heaps,xnew_son,xartv,xresult,*xmate,*xmateedge,
        *xsonlist_start,*xcontains_zero_vertex,*xson_next,*xson_nr,*xcycleedge,xexwroot,xexroot,*xinner,*xouter,xrealv,
        *xpred,*xprededge,*xtreelist,xtreelistptr,*xstack,xstackptr,*xfather,xrange_son,xnewedge;
static float xd4,xspielraum,xmin_dual,*xdual,*xmin_vertex;
static struct mfgraph *xgraphptr;

int mwm_root(int v)
{
  int queuesize=0;
  while (xfather[v] > 0)
    {
    xq[++queuesize]=v;
    v=xfather[v];
    }
  while (--queuesize > 0)
    xfather[xq[queuesize]]=v;
  return v;
  }

float kantenspielraum(int e)
{
  float s;
  int w,v;
  ends(xgraphptr,e,&v,&w);
  s=xdual[v] + xdual[w] - xgraphptr->evalues[0][e];
  mitrauf(s,0);
  return s;
  }

void examine_neighbors(int exv)
{
  if (exv > xrealv)
    {
    int son;
    for (son=xsonlist_start[exv-xrealv];son > 0 && !xresult;son=xson_next[son])
      examine_neighbors(xson_nr[son]);
    }
  else
    {
    int h,exw;
    for (h=xgraphptr->v[exv];h > 0 && !xresult;h=xgraphptr->next[h])
      {
      xnewedge=(h+1)/2;    
      exw=abs(xgraphptr->neighbor[h]);
      xexwroot=mwm_root(exw);
      if (xexwroot != xexroot 
          && (!xexplored[xnewedge] || (xexplored[xnewedge] == 1 && get_bit(xouter,xexwroot)))
          && !get_bit(xinner,xexwroot))
        {
        xexplored[xnewedge]=(get_bit(xouter,xexwroot)) ? 2 : 1;
        xspielraum=kantenspielraum(xnewedge);
        if (xspielraum < 1e-7)  /* vorsicht wegen rundungsfehlern */
          if (get_bit(xouter,xexwroot))
            xresult=2;
          else
            if (xmate[xexwroot])          /*unmarkierte ecke 'xexwroot' gefunden */
              {
              int mate=xmate[xexwroot];
              put_bit(xinner,xexwroot,1);
              put_bit(xouter,mate,1);
              xmin_dual=(mate > xrealv) ? xmin_vertex[mate-xrealv] : xdual[mate];
              mitrunter(xd4,xmin_dual);
              xstack[++xstackptr]=mate;
              xpred[mate]=xexroot;
              xprededge[mate]=xnewedge;
              xtreelist[++xtreelistptr]=xexwroot;
              xtreelist[++xtreelistptr]=mate;
              }
            else
              xresult=1;
        else
          if ((!xfather[exv] || !xfather[xfather[exv]]) && (!xfather[exw] || !xfather[xfather[exw]])) /* betrachte nur ecken 'exv' und 'exw',die
													 hoechstens einmal verschachtelt sind */
            if (get_bit(xouter,xexwroot))
              xd2heap[++xd2heaptop[xnr_heaps]]=xnewedge;     /*lege die kante auf den passenden aktuellen heap */
            else
              if (!get_bit(xinner,xexwroot))
                xd1heap[++xd1heaptop[xnr_heaps]]=xnewedge;
        }
      }
    }
  }

int add_to_cycle(int v)
{

  int listpos=xnew_son;
/*  printf("ecke %i ",v);
    charray(int,xson_next,xrealv*3/2);*/
  xrange_son=(listpos > xrange_son) ? listpos : xrange_son;
  if (v > xrealv)
    {
    if (get_bit(xcontains_zero_vertex,v-xrealv))
      put_bit(xcontains_zero_vertex,xartv-xrealv,1);
    }
  else
    {
    mitrunter(xmin_vertex[xartv-xrealv],xdual[v]);
    if (xdual[v] < 1e-7)
      put_bit(xcontains_zero_vertex,xartv-xrealv,1);
    }      
  xfather[v]=xartv;
  xson_nr[listpos]=v;
  xnew_son=(xson_next[listpos] == 0) ? xrange_son+1 : xson_next[listpos];
			 /* printf("add_to_cycle,xnew_son=%i.\n",xnew_son);getchar();*/
  xson_next[listpos]=xnew_son;
  return listpos;
  }

void adjust_xfather(int v)
{
  xfather[v]=(v == xexwroot) ? 0 : xexwroot;
  if (v > xrealv)
    {
    int son;
    for (son=xsonlist_start[v-xrealv];son > 0;son=xson_next[son])
      adjust_xfather(xson_nr[son]);
    }
  }

int cycle_next(int pos)
{return (xson_next[pos] == 0) ? xsonlist_start[xartv-xrealv] : xson_next[pos];}

void mwm_heapify(int *heap, int bot, int top, int i)
{
  int temp,j=1-bot+2*i,k=2-bot+2*i;
  if (j <= top)
    {
    if (kantenspielraum(heap[i]) <= kantenspielraum(heap[j]))
      j=i;
    if (k <= top)
      if (kantenspielraum(heap[k]) < kantenspielraum(heap[j]))
        j=k;
    if (i != j)
      {
      temp=heap[i];heap[i]=heap[j];heap[j]=temp;
      mwm_heapify(heap,bot,top,j);
      }
    }
  }


int max_weight_matching(struct mfgraph *gptr, int **mateptr, int **mateedgeptr)
{
  int *succ,totalv,new_art,nr_art,range_art,startv,hitv,right,left,e,i,j,v,w,listpos;
  float max_evalue,d1,d2,d3,d;

  xgraphptr=gptr;
  xrealv=xgraphptr->nr_v;
  xartv=xrealv/2;
  totalv=xrealv+xartv;
  nr_art=xrealv;
  xq=makearray(int,10);
  xexplored=makearray(int,xgraphptr->range_e);
  xinner=makearray(int,totalv/bits_int);
  xouter=makearray(int,totalv/bits_int);
  xmate=makearray(int,totalv);
  xmateedge=makearray(int,totalv);
  xpred=makearray(int,totalv);
  xprededge=makearray(int,totalv);
  succ=makearray(int,totalv);
  xfather=makearray(int,totalv);
  xson_nr=makearray(int,totalv);
  xcycleedge=makearray(int,totalv);
  xson_next=makearray(int,totalv);
  xdual=makearray(float,totalv);
  xsonlist_start=makearray(int,xartv);
  xmin_vertex=makearray(float,xartv);
  xcontains_zero_vertex=makearray(int,xartv/bits_int);
  xd1heap=makearray(int,xgraphptr->nr_e);
  xd2heap=makearray(int,xgraphptr->nr_e);
  xd1heaptop=makearray(int,xrealv);
  xd1heapbot=makearray(int,xrealv);
  xd2heaptop=makearray(int,xrealv);
  xd2heapbot=makearray(int,xrealv);
  xtreelist=makearray(int,2*xrealv);
  xstack=makearray(int,xrealv/2+2);
  range_art=xrealv;
  new_art=range_art+1;
  xnew_son=1;
  xrange_son=0;
  max_evalue=0;
  for (e=1;e <= xgraphptr->range_e;e++)                                 /* bestimme groesstes kantengewicht */
    if (xgraphptr->next[2*e] >= 0 && xgraphptr->evalues[0][e] > max_evalue)
      max_evalue=xgraphptr->evalues[0][e];
  for (i=1;i <= xrealv;i++)
    xdual[i]=max_evalue/2;
  
search_startv:
  startv=1;
  while ((xmate[startv] > 0 || xdual[startv] == 0 || mwm_root(startv) > xrealv) && startv <= xrealv)
    startv++;
  if (startv > xrealv)
    goto expand_rest;
alternating_tree:
  cleararray(int,xexplored,xgraphptr->range_e);
  xpred[startv]=startv;       /* berechne d1,d2 mit heaps wie in heapsort,xd4 durch staendiges aktualisieren,d3 mit einer schleife */
  xstack[1]=startv;
  xstackptr=1;
  cleararray(int,xinner,totalv/bits_int);
  cleararray(int,xouter,totalv/bits_int);
  put_bit(xouter,startv,1);
  xtreelist[1]=startv;xtreelistptr=1;
  xnr_heaps=1;
  xd1heapbot[1]=1;xd1heaptop[1]=0;
  xd2heapbot[1]=1;xd2heaptop[1]=0;
  xd4=(startv > xrealv) ? xmin_vertex[startv-xrealv] : xdual[startv];
  
examine_xstack:
  while (xstackptr)
    {
    xexroot=xstack[xstackptr--];                    
    if (mwm_root(xexroot) != xexroot) 
      continue;
    xresult=0;                    /* xresult==1:nichtmarkierte,exponierte ecke gefunden,2:'xouter' ecke gefunden */
    examine_neighbors(xexroot);            
    if (xresult == 2)
      {
      /* finde odd cycle,gehe dazu abwechselnd von 'xexroot' und xexwroot' rueckwaerts */
      hitv=0;right=xexroot;left=xexwroot;succ[right]=1;succ[left]=1;
      for (i=0;! hitv;i++)
        {
        if (right != startv)
          if (succ[xpred[right]])
            hitv=xpred[right];
          else
            {
            succ[xpred[right]]=1;
            right=xpred[right];
            }
        if (left != startv)
          if (succ[xpred[left]])
            {
            hitv=xpred[left];
            succ[xpred[left]]=left;
            }
          else
            {
            succ[xpred[left]]=left;
            left=xpred[left];
            }
        }
      /* schaffe neue kuenstliche ecke mit nr. 'xartv' */
      xartv=new_art;
      /*printf("schaffe neue kuenstliche ecke mit nr.%i,bestehend aus ",xartv);*/
      nr_art++;
      mitrauf(range_art,nr_art);
      new_art=(nr_art == range_art) ? range_art+1 : -  xsonlist_start[xartv-xrealv];
      xsonlist_start[xartv-xrealv]=xnew_son;
      xdual[xartv]=0;
      xfather[xartv]=0;
      xmin_vertex[xartv-xrealv]=1e9;
      put_bit(xcontains_zero_vertex,xartv-xrealv,0); /* dieses bit wird evtl. in der funktion 'add_to_cycle' gesetzt */
      xtreelist[++xtreelistptr]=xartv;
      for (right=xexroot;right != hitv;right=xpred[right])
        {
        listpos=add_to_cycle(right);
        xcycleedge[listpos]=xmateedge[right];/*           printf(" kante %i ",xmateedge[right]);*/
        listpos=add_to_cycle(xmate[right]);
        xcycleedge[listpos]=xprededge[right];/*       printf(" kante %i ",xprededge[right]);*/
        }
      listpos=add_to_cycle(hitv);
      for (left=hitv;left != xexwroot;left=succ[left])
        {
        xcycleedge[listpos]=xprededge[succ[left]]; /*     printf(" kante %i ",xprededge[succ[left]]);*/
        listpos=add_to_cycle(xmate[succ[left]]);
        xcycleedge[listpos]=xmateedge[succ[left]];  /*    printf(" kante %i ",xmateedge[succ[left]]);*/
        listpos=add_to_cycle(succ[left]);
        }
      xcycleedge[listpos]=xnewedge;
      /*printf(" kante %i.\n",xnewedge);*/
      xson_next[listpos]=0;
      right=xexroot;left=xexwroot;          /* loesche 'succ' */
      while (i--)
        {
        succ[right]=0;
        succ[left]=0;
        right=xpred[right];
        left=xpred[left];
        };
      mitrunter(xd4,xmin_vertex[xartv-xrealv]);      /* aktualisiere xd4,da neue ecken im odd cycle die marke */
                                                     /* 'xouter' bekommen haben */
      if (hitv == startv)
        {
        startv=xartv;
        xmate[xartv]=0;
        goto alternating_tree;
        }
      else
        {
        put_bit(xinner,xartv,0);
        put_bit(xouter,xartv,1);
        right=xmate[hitv];
        xmate[xartv]=right;
        xmateedge[xartv]=xmateedge[hitv];
        xmate[right]=xartv;
        xpred[xartv]=xpred[hitv];
        xprededge[xartv]=xprededge[hitv];
        xstack[++xstackptr]=xartv;
        goto examine_xstack;
        }
      }
    if (xresult == 1) /* exponierte ecke 'xexwroot' gefunden */
      {
      j=xmate[xexroot];
      for (right=xexroot;right != startv;right=xpred[right])
        {
        int p=xpred[right],j2=j;
        j=xmate[p];
        xmate[j2]=p;xmateedge[j2]=xprededge[right];
        xmate[p]=j2;xmateedge[p]=xprededge[right];
        }
      xmate[xexwroot]=xexroot;xmateedge[xexwroot]=xnewedge;
      xmate[xexroot]=xexwroot;xmateedge[xexroot]=xnewedge;
      goto search_startv;
      }
    /* setze die konstruktion des alternierenden baumes fort mit den ecken in 'xstack' */    
    }
  /* der gefundene alternierende baum ist ein ungarischer baum */
  d3=1e9;                                                         /* bestimme d3 */
  for (i=xrealv+1;i <= range_art;i++)      
    if (xsonlist_start[i-xrealv] > 0 && xfather[i] == 0 && get_bit(xinner,i) && d3 > xdual[i]/2)
      {
      xartv=i;
      d3=xdual[i]/2;
      }
  for (i=(xd1heapbot[xnr_heaps]+xd1heaptop[xnr_heaps])/2 ; i >= xd1heapbot[xnr_heaps] ;i--)    /* bestimme d1 */
    mwm_heapify(xd1heap,xd1heapbot[xnr_heaps],xd1heaptop[xnr_heaps],i);
  d1=1e9;
  for (i=1;i <= xnr_heaps;i++)
    {                            /* aktualisiere den 'i'. heap */
    while (xd1heaptop[i] >= xd1heapbot[i])
      {
      ends(xgraphptr,xd1heap[xd1heapbot[i]],&v,&w);
      v=mwm_root(v);
      w=mwm_root(w);
      if ((!get_bit(xinner,v) && !get_bit(xouter,v)) || (!get_bit(xinner,w) && !get_bit(xouter,w))) /* ist einer der endpunkte der heapspitzen-kante*/
        break;		   		                	/* noch unmarkiert,dann kommt sie fuer 'd1' noch in frage */
      xd1heap[xd1heapbot[i]]=xd1heap[xd1heaptop[i]--];
      mwm_heapify(xd1heap,xd1heapbot[i],xd1heaptop[i],xd1heapbot[i]);
      }
    if (xd1heaptop[i] >= xd1heapbot[i])
      mitrunter(d1,kantenspielraum(xd1heap[xd1heapbot[i]]));
    }
  for (i=(xd2heapbot[xnr_heaps]+xd2heaptop[xnr_heaps])/2 ; i >= xd2heapbot[xnr_heaps] ;i--)    /* bestimme d2 */
    mwm_heapify(xd2heap,xd2heapbot[xnr_heaps],xd2heaptop[xnr_heaps],i);
  d2=1e9;
  for (i=1;i <= xnr_heaps;i++)
    {                            /* aktualisiere den 'i'. heap */
    while (xd2heaptop[i] >= xd2heapbot[i])
      {
      ends(xgraphptr,xd2heap[xd2heapbot[i]],&v,&w);
      if (mwm_root(v) != mwm_root(w))
        break;
      xd2heap[xd2heapbot[i]]=xd2heap[xd2heaptop[i]--];
      mwm_heapify(xd2heap,xd2heapbot[i],xd2heaptop[i],xd2heapbot[i]);
      }
    if (xd2heaptop[i] >= xd2heapbot[i])
      mitrunter(d2,kantenspielraum(xd2heap[xd2heapbot[i]])/2);
    }
  /* richte neuen heap ein fuer die folgenden kanten */
  xd1heapbot[xnr_heaps+1]=xd1heaptop[xnr_heaps]+1;xd1heaptop[xnr_heaps+1]=xd1heaptop[xnr_heaps];
  xd2heapbot[xnr_heaps+1]=xd2heaptop[xnr_heaps]+1;xd2heaptop[xnr_heaps+1]=xd2heaptop[xnr_heaps];
  xnr_heaps++;
  d=(d1 > d2) ? d2 : d1;
  d=(d > d3) ? d3 : d;
  d=(d > xd4) ? xd4 : d;
  if (d == 1e9)
    goto search_startv;

  /* aendere xduale variablen,benutze 'xtreelist' ,'succ[xtreelist[i]]' */

  for (i=1;i <= xtreelistptr;i++)
    {
    v=xtreelist[i];
    if (mwm_root(v) == v && !succ[v])    /* ecken,die in einen odd cycle gekommen sind,werden uebergangen */
      {
      succ[v]=1;         /* bearbeite ecke 'v=xtreelist[i]' */
      if (v > xrealv)
       {
       if (xsonlist_start[v-xrealv] > 0)
        {          
        int son;
        xdual[v] += (get_bit(xouter,v)) ? 2*d : -2*d;
        if (xmin_vertex[v-xrealv] != 1e9)
          xmin_vertex[v-xrealv]-=(get_bit(xouter,v)) ? d : -d;
        if (xmin_vertex[v-xrealv] < 1e-7)
          put_bit(xcontains_zero_vertex,v-xrealv,1);
        for (son=xsonlist_start[v-xrealv];son > 0;son=xson_next[son])
          if (xson_nr[son] <= xrealv)
            {
            xdual[xson_nr[son]]-=(get_bit(xouter,v)) ? d : -d;
            mitrunter(xd4,xdual[xson_nr[son]]);
            if (xdual[xson_nr[son]] == 0) 
              xexroot=v;
            }
        }
      }
      else
        {
        xdual[v]-=(get_bit(xouter,v)) ? d : -d;
        mitrunter(xd4,xdual[v]);
        if (xdual[v] == 0)
          xexroot=v;
        }
      }
    }
  for (i=1;i <= xtreelistptr;i++)
    succ[xtreelist[i]]=0;                 /*    charray(float,xdual,totalv);*/
  if (xd4 != 0)     /* aktualisiere heaps,entferne die kanten,deren spielraum 0 geworden ist */
    {
    for (i=1;i <= xnr_heaps;i++)
      {
      while (xd2heapbot[i] <= xd2heaptop[i])
        {
        e=xd2heap[xd2heapbot[i]];
        ends(xgraphptr,e,&v,&w);
        if (kantenspielraum(e) > 1e-7)
          break;
        xexplored[e]=1;
        j=(v > xrealv) ? w : v;   /* lege einen der endpunkte auf den stack,moeglichst eine echte ecke */
        if (!succ[j])                                 /* benutze 'succ' ,um zu verhindern,dass eine ecke mehrmals auf den stack gelegt wird */
          {
          succ[j]=1;
          xstack[++xstackptr]=j;
          }
        xd2heap[xd2heapbot[i]]=xd2heap[xd2heaptop[i]--];
        mwm_heapify(xd2heap,xd2heapbot[i],xd2heaptop[i],xd2heapbot[i]);
        }
      while (xd1heapbot[i] <= xd1heaptop[i])
        {
        e=xd1heap[xd1heapbot[i]];
        ends(xgraphptr,e,&v,&w);
        if (kantenspielraum(e) > 1e-7)
          break;
        xexplored[e]=0;
        if (get_bit(xouter,mwm_root(v)))
          {right=mwm_root(w);left=mwm_root(v);}
        else    
          {left=mwm_root(w);right=mwm_root(v);}
        if (!get_bit(xouter,right) && !get_bit(xinner,right) && !succ[left])
          {
          succ[left]=1;
          xstack[++xstackptr]=left;			 /* falls es noch einen unmarkierten endpunkt gibt,lege den (mit 'outer') markierten auf den stack */
          }
        xd1heap[xd1heapbot[i]]=xd1heap[xd1heaptop[i]--];
        mwm_heapify(xd1heap,xd1heapbot[i],xd1heaptop[i],xd1heapbot[i]);
        }
      }
    for (i=1;i <= xstackptr;i++)
      succ[xstack[i]]=0;
    if (d == d1 || d == d2)
      goto examine_xstack;
    /* d==d3,expandiere die kuenstliche ecke 'xartv' mit marke 'xinner',deren xduale variable 0 geworden ist */
    /* bilde das eindeutige matching auf diesem odd cycle,loesche alle(!) marken der ecken des odd cycle,
       lege 'xpred[xmate[xartv]]' auf den xstack,goto examine_xstack */
    /*printf("expandiere kuenstliche ecke nr.%i.\n",xartv);*/
    ends(xgraphptr,xmateedge[xartv],&v,&w);
    if (mwm_root(v) == xartv)
      swap(int,v,w);
    /* finde pos. der kuenstlichen ecke,die ecke 'w' enthaelt */
    for (i=xsonlist_start[xartv-xrealv];i > 0;i=xson_next[i])
      {
      listpos=i;xexwroot=xson_nr[i];
      xfather[xexwroot]=0;
      adjust_xfather(xexwroot);
      if (xexwroot == mwm_root(w))
        j=i;
      put_bit(xinner,xson_nr[i],0);
      put_bit(xouter,xson_nr[i],0);
      }
    /* gehe nochmals durch den odd cycle,starte nach ecke 'mwm_root(w)' */
    for (i=cycle_next(j);i != j;i=cycle_next(cycle_next(i)))
      {
      int n=xson_nr[cycle_next(i)],m=xson_nr[i];
      xmate[m]=n;xmateedge[m]=xcycleedge[i];
      xmate[n]=m;xmateedge[n]=xcycleedge[i];
      }
    xmate[mwm_root(w)]=mwm_root(v);xmateedge[mwm_root(w)]=xmateedge[xartv];
    xmate[mwm_root(v)]=mwm_root(w);
    xson_next[listpos]=xnew_son;
    xnew_son=xsonlist_start[xartv-xrealv];/*  printf("expand_cycle,xnew_son=%i.\n",xnew_son);getchar();*/
    xsonlist_start[xartv-xrealv]= - new_art;
    new_art=xartv;
    nr_art--;
    goto alternating_tree;
    }
  else             /*d==xd4,d.h.'xdual[xexroot]==0' */
    {
    j=xmate[xexroot];
    for (i=xexroot;i != startv;i=xpred[i])
      {
      int v1=j,v2=xpred[i];
      j=xmate[v2];
      xmate[v1]=v2;xmateedge[v1]=xprededge[i];
      xmate[v2]=v1;xmateedge[v2]=xprededge[i];
      }
    xmate[xexroot]=0;    /* vertex 'xexroot' becomes exposed,which is all right,because 'xdual[xexroot] == 0' */
    goto search_startv;
    }
expand_rest:
  
  /*printf("expand rest.\n");*/
  while (1)
    {
    /* suche kuenstliche ecke,die in keinem anderen odd cycle enthalten ist */
    for (xartv=xrealv+1;xartv <= range_art && !(xsonlist_start[xartv-xrealv] > 0 && xfather[xartv] == 0);xartv++);
    if (xartv > range_art) 
      break;
                             /*printf("expandiere kuenstliche ecke nr.%i.\n",xartv);*/
    if (xmate[xartv])
      {
      ends(xgraphptr,xmateedge[xartv],&v,&w);
      if (mwm_root(v) == xartv)
        swap(int,v,w);
      }
    else
      {
      for (i=xsonlist_start[xartv-xrealv];i > 0;i=xson_next[i])
        {
        j=xson_nr[i];     
        if ((j <= xrealv && xdual[j] < 1e-7) || (j > xrealv && get_bit(xcontains_zero_vertex,j-xrealv)))
          w=xson_nr[i];
        }
      xmate[w]=0;
      }    
    /* bilde matching auf dem odd cycle ohne ecke 'mwm_root(w)' */
    for (i=xsonlist_start[xartv-xrealv];i > 0;i=xson_next[i])
      {
      listpos=i;xexwroot=xson_nr[i];
      xfather[xexwroot]=0;
      adjust_xfather(xexwroot);
      if (xexwroot == mwm_root(w))
        j=i;
      put_bit(xinner,xson_nr[i],0);
      put_bit(xouter,xson_nr[i],0);
      }
    /* gehe nochmals durch den odd cycle,starte nach ecke 'mwm_root(w)' */
    for (i=cycle_next(j);i != j;i=cycle_next(cycle_next(i)))
      {
      int n=xson_nr[cycle_next(i)],m=xson_nr[i];
      xmate[m]=n;xmateedge[m]=xcycleedge[i];
      xmate[n]=m;xmateedge[n]=xcycleedge[i];
      }
    if (xmate[xartv])
      {
      xmate[mwm_root(w)]=mwm_root(v);xmateedge[mwm_root(w)]=xmateedge[xartv];
      xmate[mwm_root(v)]=mwm_root(w);
      }
    xsonlist_start[xartv-xrealv]=-1;
    }

  free(xq);
  free(xexplored);
  free(xmin_vertex);
  free(xcontains_zero_vertex);
  free(xd2heap);
  free(xd1heap);
  free(xd2heaptop);
  free(xd1heaptop);
  free(xd2heapbot);
  free(xd1heapbot);
  free(xsonlist_start);
  free(xson_next);
  free(xson_nr);
  free(xcycleedge);
  free(xinner);
  free(xouter);
  free(xpred);
  free(xprededge);
  free(xtreelist);
  free(xstack);
  free(xfather);
  free(xdual);
  free(succ);
  *mateptr=xmate;
  *mateedgeptr=xmateedge;
  return(1);
  }

typedef struct pair_of_edgevalues
  {
  float float1,float2;
  int nr; 	/* for internal use only */
  } *Pair_of_edgevalues;

struct mfgraph *sgraph_to_mygraph(Sgraph sgraph, int alg_nr, Snode sourcenode, Snode targetnode, int *sourceptr, int *targetptr, Sedge **sgraph_edges_ptr)
{
  Snode node;
  int nr_nodes,nr_edges,i;
  Sedge *sgraph_edges,edge;
  struct mfgraph *gptr;
  float ev[2];
  Pair_of_edgevalues pair;

  nr_nodes=0;nr_edges=0;
  for_all_nodes (sgraph,node)
    {
    nr_nodes++;			/*printf("untersuche node nr %i.\n",nr_nodes);*/
    if (alg_nr== 1 && node == sourcenode)
      *sourceptr=nr_nodes;     /*printf("sourcenode gefunden,nr=%i.\n",nr_nodes);*/
    if (alg_nr== 1 && node == targetnode)
      *targetptr=nr_nodes;     /*printf("targetnode gefunden,nr=%i.\n",nr_nodes);*/
    set_nodeattrs(node,make_attr(ATTR_FLAGS,nr_nodes));
    for_sourcelist (node,edge)
      {
      nr_edges++;
      pair=attr_data_of_type(edge,Pair_of_edgevalues);
      pair->nr=0;      
      }
    end_for_sourcelist (node,edge);
    }
  end_for_all_nodes (sgraph,node);
  nr_edges=(sgraph->directed) ? nr_edges : nr_edges/2;	/*printf("der graph hat %i kanten.\n",nr_edges);*/
  gptr=init_graph(nr_nodes,nr_edges,0,2,1);    /* create a graph 'gptr' with up to 'nr_nodes' and 'nr_edges' edges */
  sgraph_edges=makearray(Sedge,nr_edges);
  for (i=1;i<=nr_nodes;i++)
    insert_v(gptr,NULL);
  for_all_nodes (sgraph,node)
    {
    for_sourcelist (node,edge)
      {
      if (edge->snode == edge->tnode)   /*ignore self-loops */
        continue;
      pair=attr_data_of_type(edge,Pair_of_edgevalues);
      if (pair->nr > 0)   
        pair->float2=0; 		/*edge has already been found */
      else
        {
        ev[0]=pair->float1;ev[1]=0.0;
        pair->nr=insert_e(gptr,attr_flags(edge->snode),attr_flags(edge->tnode),ev);
        sgraph_edges[pair->nr]=edge;			/*printf("created %i. edge\n",pair->nr);*/
        }
      }
    end_for_sourcelist (node,edge);
    }
  end_for_all_nodes (sgraph,node);
  /*show_graph(gptr);*/
  *sgraph_edges_ptr=sgraph_edges;
  return gptr;
  }

void sgraph_max_weight_matching(Sgraph sgraph)
{
  struct mfgraph *gptr;
  Sedge *sgraph_edges;
  Pair_of_edgevalues pair;
  float sum;
  int *mate,*mateedge,i;

  gptr=sgraph_to_mygraph(sgraph,2,NULL,NULL,NULL,NULL,&sgraph_edges);
  max_weight_matching(gptr,&mate,&mateedge);
  sum =0;
  for (i=1;i <= gptr->range_v;i++)
    if (mate[i] > 0)
      if (mate[i] > i)
        {
        pair=attr_data_of_type(sgraph_edges[mateedge[i]],Pair_of_edgevalues);
        pair->float2=1.0;                       /* let 'float2' of all matching edges be 1.0 */
        sum+=gptr->evalues[0][mateedge[i]];
        }
  message("\nThe maximum weight matching\nhas a total weight of %g.\nMatching edges are represented by continuous lines,\nother edges by dotted lines.\n",sum);
  free(mate);free(mateedge);
  delete_mygraph(gptr);
  }

void sgraph_max_flow(Sgraph sgraph, Snode sourcenode, Snode targetnode)
{
  struct mfgraph *gptr;
  Sedge *sgraph_edges;
  int source,target,i;
  float flowsum;
  Pair_of_edgevalues pair;
  gptr=sgraph_to_mygraph(sgraph,1,sourcenode,targetnode,&source,&target,&sgraph_edges);
  max_flow(gptr,source,target,&flowsum);
  for (i=1;i <= gptr->nr_e;i++)
    {
    pair=attr_data_of_type(sgraph_edges[i],Pair_of_edgevalues);
    pair->float2=gptr->evalues[1][i];
    }
  message("The maximum flow from the source\nto the target amounts to %g.\n",flowsum);
  delete_mygraph(gptr);
  }



