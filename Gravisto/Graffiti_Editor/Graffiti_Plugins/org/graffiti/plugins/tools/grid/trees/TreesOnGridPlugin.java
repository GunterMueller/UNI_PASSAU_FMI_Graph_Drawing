// =============================================================================
//
//   TreesOnGridPlugin.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.grid.trees;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.border.Border;

import org.graffiti.core.Bundle;
import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.plugin.EditorPluginAdapter;
import org.graffiti.plugin.gui.GraffitiComponent;
import org.graffiti.plugin.gui.GraffitiToolbar;
import org.graffiti.plugin.tool.ToolRegistry;
import org.graffiti.plugins.algorithms.hexagonalTrees.MoveSubtreesIn;
import org.graffiti.plugins.algorithms.hexagonalTrees.MoveSubtreesOut;
import org.graffiti.plugins.tools.scripted.ScriptedToolLoader;
import org.graffiti.plugins.views.fast.FastView;

/**
 * @author Marco Matzeder
 * @version $Revision$ $Date$
 */
public final class TreesOnGridPlugin extends EditorPluginAdapter {
    private static final String TOOLBAR_KEY = "toolbar";
    private GraffitiToolbar toolbar;
    
    /**
     * The Button captions
     */
    private static final String BUTTON_CIE_CAPTION_KEY = "button.contractIncomingEdge.caption";
    private static final String BUTTON_EIE_CAPTION_KEY = "button.extendIncomingEdge.caption";
    private static final String BUTTON_COE_CAPTION_KEY = "button.contractOutgoingEdges.caption";
    private static final String BUTTON_EOE_CAPTION_KEY = "button.extendOutgoingEdges.caption";
    

    /**
     * The algorithms
     */
    protected ContractIncomingEdge contractIncomingEdge;
    protected ContractIncomingEdge extendIncomingEdge;
    protected MoveSubtreesIn contractOutgoingEdges;
    protected MoveSubtreesOut extendOutgoingEdges;

    /**
     * The Buttons
     */
    private JButton contractIncomingEdgeButton;
    private JButton extendIncomingEdgeButton;
    private JButton contractOutgoingEdgesButton;
    private JButton extendOutgoingEdgesButton;

    
    public TreesOnGridPlugin() {
        contractIncomingEdge = new ContractIncomingEdge();
        extendIncomingEdge = new ExtendIncomingEdge();
        contractOutgoingEdges = new MoveSubtreesIn();
        extendOutgoingEdges = new MoveSubtreesOut();


        Bundle bundle = Bundle.getBundle(TreesOnGridPlugin.class);
        toolbar = new GraffitiToolbar(bundle.getString(TOOLBAR_KEY));
        toolbar.setToolTipText(bundle.getString(TOOLBAR_KEY));

        Border title = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                bundle.getString(TOOLBAR_KEY));
        toolbar.setBorder(BorderFactory.createCompoundBorder(title,
                BorderFactory.createEmptyBorder(0, 4, 0, 4)));

        
        /**
         * Contract outgoing edges (move subtrees in)
         */
        extendOutgoingEdgesButton = new JButton(
                bundle.getString(BUTTON_EOE_CAPTION_KEY));
        extendOutgoingEdgesButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                GraffitiSingleton.runAlgorithm(extendOutgoingEdges);
            }
        });
        toolbar.add(extendOutgoingEdgesButton);
        
        
        /**
         * Contract outgoing edges (move subtrees in)
         */
        contractOutgoingEdgesButton = new JButton(
                bundle.getString(BUTTON_COE_CAPTION_KEY));
        contractOutgoingEdgesButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                GraffitiSingleton.runAlgorithm(contractOutgoingEdges);
            }
        });
        toolbar.add(contractOutgoingEdgesButton);

        
        /**
         * Contract incoming edge
         */
        contractIncomingEdgeButton = new JButton(
                bundle.getString(BUTTON_CIE_CAPTION_KEY));
        contractIncomingEdgeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                GraffitiSingleton.runAlgorithm(contractIncomingEdge);
            }
        });
        toolbar.add(contractIncomingEdgeButton);
        
        /**
         * Extend incoming edge
         */
        extendIncomingEdgeButton = new JButton(
                bundle.getString(BUTTON_EIE_CAPTION_KEY));
        extendIncomingEdgeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GraffitiSingleton.runAlgorithm(extendIncomingEdge);
            }
        });
        toolbar.add(extendIncomingEdgeButton);


        // FastView.FAST_VIEW_FAMILY.add(contractIncomingEdgeAction);

        ToolRegistry registry = ToolRegistry.get();
        registry.registerTools(ScriptedToolLoader.loadTools(this,
                FastView.FAST_VIEW_FAMILY));

        guiComponents = new GraffitiComponent[] { toolbar };
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
