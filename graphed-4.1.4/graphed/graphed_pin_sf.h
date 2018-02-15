typedef	struct	graphed_pin_subframe {

	Frame		frame;
	Panel		panel;

	Panel_item	set_button;
	Panel_item	reset_button;
	Panel_item	do_button;

	void	(*set_proc)();
	void	(*reset_proc)();
	void	(*do_proc)();
	void	(*done_proc)();
	void	(*graphed_done_proc)();

	int	showing;

	char	*set_label;
	char	*reset_label;
	char	*do_label;
}
	*Graphed_pin_subframe;

extern	Graphed_pin_subframe	new_graphed_pin_subframe     (Frame frame);
extern	Graphed_pin_subframe	graphed_create_pin_subframe  (Graphed_pin_subframe sf, char *label);
extern	int			showing_graphed_pin_subframe (Graphed_pin_subframe sf);

