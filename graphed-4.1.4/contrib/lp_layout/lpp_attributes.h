extern	Attributes_head		new_attributes_head(void);
extern	Attributes_head		add_to_attributes_head_from_top(Attributes_head list, Attributes_head new);
extern	Attributes_head		add_to_attributes_head_from_bottom(Attributes_head list, Attributes_head new);


extern	Attributes_ref_list	new_attributes_ref_list(void);
extern	Attributes_ref_list	add_to_attributes_ref_list(Attributes_ref_list list, Attributes_ref_list new);
extern	void			add_to_same_upper_prod_in_next(Attributes_ref_list first, Attributes_ref_list new);

extern	Attributes_ref		new_attributes_ref(void);
extern	Attributes_ref		add_to_attributes_ref(Attributes_ref list, Attributes_ref cur);

