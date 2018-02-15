# This software is distributed under the Lesser General Public License
#
# algorithms/layout_tree.tcl
#
# Sabine's marvellous tree layout algorithm.
#
###########################################
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/algorithms/layout_tree.tcl,v $
# $Author: himsolt $
# $Revision: 1.16 $
# $Date: 1999/04/10 15:27:10 $
# $Locker:  $
# $State: Exp $
#
###########################################
#
# (C) University of Passau 1995-1999, graphlet Project
#

package require Graphlet
package provide Graphscript [gt_version]

package require Gt_tree_layout

namespace eval GT {
    namespace export \
	action_layout_tree \
	action_layout_tree_options
}


##########################################
#
# Tree Layout
#
##########################################


proc GT::action_layout_tree { editor } {

    global GT GT_options GT_selection
    set graph $GT($editor,graph)

    GT::undo $editor attributes [$graph nodes] -x -y
    GT::undo $editor attributes [$graph edges] -line

    # is there a root for the undirected case?
    set selected $GT_selection($editor,selected,node)
    if {[llength $selected] == 1} {
	set length [string length $selected]
	set rootid [string range $selected 3 $length]
    } else {
	set rootid -1
	if {[llength $selected] > 1} {
	    GT::message $editor "You selected more than one node. Thus, we've chosen the graph theoretical center as root node."
	}
    }

    GT::excursion $editor \
	-check_selection 0 \
	-name "Tree Layout" \
	-command {
	    if [catch { 
		layout_extended_tr_tree $graph \
		    -leveling $GT_options(tree_tr_leveling) \
		    -orientation $GT_options(tree_tr_orientation) \
		    -direction $GT_options(tree_tr_direction) \
		    -routing $GT_options(tree_tr_routing) \
		    -father_place $GT_options(tree_tr_father_place) \
		    -permutation $GT_options(tree_tr_permutation) \
		    -father_son_dist $GT_options(tree_tr_vert_node_node) \
		    -son_son_dist $GT_options(tree_tr_hor_node_node) \
		    -node_edge_dist $GT_options(tree_tr_node_edge) \
		    -channel_width $GT_options(tree_tr_channel_width) \
		    -edge_connection $GT_options(tree_tr_edge_connection) \
		    -bend_reduction $GT_options(tree_tr_bend_reduction) \
		    -edge_connection_for_bend $GT_options(tree_tr_edge_connection_for_bend) \
		    -marked_root $GT_options(tree_tr_marked_root) \
		    -root_id $rootid } \
		error_message] {
		GT::select  $editor [lindex $error_message 1]
		tk_dialog .my_errormsg "Tree Layout" \
		    [lindex $error_message 0] error 0 "Ok"	
	    }	
	}
}

proc GT::action_layout_tree_options { editor } {

    global GT GT_options

    #------------------------------------------------------------------

    lappend options {
	tab "General"
    } {
	radio "Direction" tree_tr_direction
	"Drawing direction of the tree. Either from left to right or from top to bottom or from right to left or from bottom to top."
	{
	    {"Left/Right" 0}
	    {"Top/Bottom" 1}
	    {"Right/Left" 2}
	    {"Bottom/Top" 3}
	}
	
    } {
	radio "Routing" tree_tr_routing
	"Routing of the edges."
	{
	    {"Straightline" 0}
	    {"Orthogonal" 1}
	}
    } {
	radio "Leveling" tree_tr_leveling
	"'Global' aligns all nodes of the same level on a vertical (horizontal) line.
'Local' only the direct descendants of a node.
Note that only varying node sizes make the difference."
	{
	    {"Global" 0}
	    {"Local" 1}
	}
    } { 
	integer "Node-Node Distance (Father to Son)" tree_tr_vert_node_node
	"Minimal distance between father node and its sons (counted from the outline of the nodes)."
	-scale on
	-from 1
	-to 300
    } {
	integer "Node-Node Distance (Son to Son)" tree_tr_hor_node_node
	"Minimal distance between all nodes which are not in father-son relation (counted from the outline of the nodes)."
	-scale on
	-from 1
	-to 300
    } {
	radio "Order the Edges at a Node" tree_tr_permutation
	"Order of the son nodes in dependance of the incoming edge.
'As Shown' tries to keep the order of the sons.
'Reflect' reverses the order.
'According to Insertion' takes the order given by the insertion time."
        {
	    {"As Shown" 0}
	    {"Reflect" 1}
	    {"According to Insertion" 2}
	}
    } {
	endtab
    }

    #------------------------------------------------------------------

    lappend options {
	tab "Additional"
    } {
	radio "Orientation" tree_tr_orientation
	"Align the upper, the middle or the lower sides of the nodes on their (horizontal or vertical) line."
	{
	    {"Upper" 0}
	    {"Middle" 1}
	    {"Lower" 2}
	}
    } {
	radio "Father Placement" tree_tr_father_place
	"X-coordinate of the father node.
'Barycenter' positions the father at the barycenter of its descendants.
'Outer Middle' takes only the x-coordinates of the two outermost sons.
'Inner Middle' only those of the innermost son into account."
	{
	    {"Outer Middle" 0}
	    {"Barycenter" 1}
	    {"Inner Middle" 2}
	}
    } {
	integer "Node-Edge Distance (% of Node-Node Dist.)" tree_tr_node_edge
	"Minimal distance between nodes and edges."
	-scale on
	-from 0
	-to 100
    } {
	integer "Channel Width" tree_tr_channel_width
	"Distance between parallel lines in orthogonal layouts.
For 'bus layout' choose 0. The width of an edge is not taken into account."
	-scale on
	-from 0
	-to 20
    } {
	integer "Region for Edge Connection (%)" tree_tr_edge_connection
	"Portion of the side of a node, where the edges may leave the node.
For 'bus layout' choose 0."
	-scale on
	-from 0
	-to 100
    } {
	radio "Bend Reduction" tree_tr_bend_reduction
	"In orthogonal layouts, use the 'Region for Bend Reduction'
to reduce bends."
	{
	    {"Enabled" 0}
	    {"Disabled" 1}
	}
    } {
	integer "Region for Bend Reduction (%)" tree_tr_edge_connection_for_bend
	"Avoid Bends if the father node is reached inbetween this portion of his side.
'Bend Reduction' must be enabled."
	-scale on
	-from 1
	-to 100
    }  {
	radio "Root: Marked Node?" tree_tr_marked_root
	"For undirected graphs, take the marked node as root."
	{
	    {"Yes" 0}
	    {"No" 1}
	}
    } {
	endtab
    }

    #------------------------------------------------------------------

    if {$GT_options(expert)} {
	GT::create_tabwindow $editor \
	    layout_tree_options "Tree Layout Options" \
	    $options layout_tree
    } else {
	GT::create_tabwindow $editor \
	    layout_tree_options "Tree Layout Options" \
	    $options
    }
}
