/******************************************************************************/
/*                                                                            */
/* FILE: VISIBILITY_INIT.C                                                    */
/*                                                                            */
/* Beschreibung: hier werden das Submenue "Visibility Representations" und    */
/*               die darin enthaltenen Menuepunkte erzeugt                    */
/*                                                                            */
/******************************************************************************/



#include <xview/xview.h>
#include <xview/panel.h>



{
   Menu visibility_menu;

   visibility_layout2_settings = init_visibility_layout2_settings ();
  
   visibility_menu = graphed_create_pin_menu ("planar / visibility layouts 2");

   add_entry_to_menu (visibility_menu,
                     "RT weak-visibility",
                      menu_RT_w_visibility);
   add_entry_to_menu (visibility_menu,
                     "TT weak-visibility",
                      menu_TT_w_visibility);
   add_entry_to_menu (visibility_menu,
                     "TT epsilon-visibility",
                      menu_TT_epsilon_visibility);
   add_entry_to_menu (visibility_menu,
                     "Kant weak-visibility",
                      menu_Kant_w_visibility);
   add_entry_to_menu (visibility_menu,
                     "Nummenmaa weak-visibility",
                      menu_Nummenmaa_w_visibility);
   add_entry_to_menu (visibility_menu,
                     "tree-strong-visibility",
                      menu_tree_s_visibility);
   add_entry_to_menu (visibility_menu,
                     "settings ...",
                      menu_visibility_layout2_settings);

   add_menu_to_layout_menu ("planar / visibility layouts 2",
                          visibility_menu);
}



/******************************************************************************/
/*                                                                            */
/*                       END OF FILE: VISIBILITY_INIT.C                       */
/*                                                                            */
/******************************************************************************/

