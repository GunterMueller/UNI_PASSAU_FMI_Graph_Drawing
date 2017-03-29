BEGIN {

  FS="[ 	]+";

  printf ("GRAPH \"%s\" = %s\n", name, directed);

  if (unitx == zerox) {
    zerox=0; unitx=1000
  }
  if (unity == zeroy) {
    zeroy=0; unity=1000
  }
  if (unitz == zeroz) {
    zeroz=0; unitz=1000
  }
}


{
  n = $1;

  if (dimension == 0) {
    x[n] = zerox + unitx*rand();
    y[n] = zeroy + unity*rand();
    z[n] = zeroz + unitz*rand();
  } else if (dimension == 2) {
    x[n] = zerox + $2 * unitx
    y[n] = zeroy + $3 * unity
    z[n] = zeroz + (unitz-zeroz)*rand();
  } else if (dimension == 3) {
    x[n] = zerox + $2 * unitx
    y[n] = zeroy + $3 * unity
    z[n] = zeroz + $3 * unitz
  }

  adjacencies[n] = "";
  for (i=2+dimension; i<=NF; i++) {
    adjacencies[n] = adjacencies[n]"<"$i">"
  }

}


END {

#  for (n in adjacencies) if (adjacencies[n] != "") {
  for (n in adjacencies) {
    printf ("%d ", n);
    printf ("{$ %d %d $} ", x[n], y[n]);

    if (LabelNodeWithNumber == 1) {
      printf ("\"%d\"\n", n);
    } else {
      printf ("\"\"\n");
    }

    split (adjacencies[n], edges, "[<>]");

    for (e in edges) if (edges[e] != "") {

      if ((directed == "DIRECTED") || (n < edges[e])) {
	printf (" %d ", edges[e]);
	if (LabelEdgeWithNumber == 1) {
	  if (directed == "DIRECTED") {
	    printf ("\"%d->%d\"\n", n, edges[e]);
	  } else {
	    printf ("\"%d-%d\"\n", n, edges[e]);
	  }
	} else {
	  printf ("\"\"\n");
	}
      }
    }

    printf (";\n");

  }

  print "END";
}
