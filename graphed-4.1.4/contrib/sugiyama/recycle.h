
struct node_info {
	Snode node;
	int outdegree, indegree, labelnr, diff, marked;
	struct node_info *next;
};


/* Aufbau von info_list */
extern void init_worklist (Sgraph gra);

/* GESAMTFUNKTION  1.Teil */
extern Slist decycle (Sgraph gra, int choose);

/* GESAMTFUNKTION  2.Teil */
extern void rechange_arcs (Slist list, Sgraph orig_g);

/* mache Kopie eines Graphen */
extern Sgraph make_copy_of_sgraph (Sgraph g);

/* drehe die Reihenfolge der Stuetzpunkte bei Aufwaertspfeilen um */
extern void change_order_pos (Sgraph graph, Slist up_arcs);


/* cycle.c */
extern bool find_cycles(Sgraph g);

/* hier.c */
extern void make_hierarchy(Sgraph g, int choose);
extern void add_dummies(Sgraph g);
extern void init_positions(Sgraph g);
extern void set_horizontal_positions(Sgraph g, Sgraph original, int distance);
extern void set_vertical_positions(Sgraph g, Sgraph original, int distance);
extern void remove_dummies(Sgraph g, Sgraph orig_g);

/* cros.c */
extern void reduce_crossings(Sgraph g, int choose);

/* pos.c */
extern void improve_positions(Sgraph g)
;
