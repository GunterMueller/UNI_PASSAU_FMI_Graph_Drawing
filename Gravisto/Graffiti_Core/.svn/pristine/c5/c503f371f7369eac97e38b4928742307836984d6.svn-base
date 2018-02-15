// =============================================================================
//
//   TreeJugglerPlugin.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ZoomPlugin.java 1018 2006-01-10 10:59:28Z forster $

package org.graffiti.plugins.tools.treeJuggler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.border.Border;

import org.graffiti.core.Bundle;
import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.plugin.EditorPluginAdapter;
import org.graffiti.plugin.gui.GraffitiComponent;
import org.graffiti.plugin.gui.GraffitiToolbar;
import org.graffiti.plugins.algorithms.hexagonalTrees.MoveSubtreesIn;
import org.graffiti.plugins.algorithms.hexagonalTrees.MoveSubtreesOut;
import org.graffiti.plugins.algorithms.treedrawings.DAGSplitter.DAGSplitter;
import org.graffiti.plugins.algorithms.treedrawings.RootChanger.RootChanger;
import org.graffiti.plugins.algorithms.treedrawings.TreeKNaryMaker.HelperNodeStripper;
import org.graffiti.plugins.algorithms.treedrawings.TreeKNaryMaker.TreeKNaryMaker;
import org.graffiti.plugins.algorithms.treedrawings.subtreeSeperation.LayoutRefresher;
import org.graffiti.plugins.algorithms.treedrawings.subtreeSeperation.hv.HVLayout;
import org.graffiti.plugins.algorithms.treedrawings.subtreeSeperation.tipover.TipoverLayout;

/**
 * This plugin contains the standard editing tools.
 * 
 * @version $Revision: 1018 $
 */
public class TreeJugglerPlugin extends EditorPluginAdapter {

    static DAGSplitter DAGSplitter = new DAGSplitter();

    static RootChanger rootChanger = new RootChanger();

    static TreeKNaryMaker treeKNaryMaker = new TreeKNaryMaker();

    static HelperNodeStripper helperNodeStripper = new HelperNodeStripper();

    static TipoverLayout tipoverLayout = new TipoverLayout();

    static HVLayout hvLayout = new HVLayout();

    static LayoutRefresher layoutRefresher = new LayoutRefresher();


    public TreeJugglerPlugin() {

        GraffitiToolbar treeJugglerToolbar = new GraffitiToolbar("TreeJuggler");
        treeJugglerToolbar.setToolTipText("Tree Juggler");

        Border title = BorderFactory.createTitledBorder(BorderFactory
                .createEtchedBorder(), "TreeJuggler");
        treeJugglerToolbar.setBorder(BorderFactory.createCompoundBorder(title,
                BorderFactory.createEmptyBorder(0, 4, 0, 4)));

        Bundle bundle = Bundle.getCoreBundle();
        ImageIcon DAGSplitterIcon = bundle.getIcon("treejuggler.dagsplitter");
        JButton DAGSplitterButton = new JButton(DAGSplitterIcon);
        DAGSplitterButton.setToolTipText("DAGSplitter [Alt+D]");
        DAGSplitterButton.setMnemonic(KeyEvent.VK_D);
        DAGSplitterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GraffitiSingleton.runAlgorithm(TreeJugglerPlugin.DAGSplitter);
            }
        });

        ImageIcon rootChangerIcon = bundle.getIcon("treejuggler.rootchanger");
        JButton rootChangerButton = new JButton(rootChangerIcon);
        rootChangerButton.setToolTipText("RootChanger [Alt+A]");
        rootChangerButton.setMnemonic(KeyEvent.VK_A);
        rootChangerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GraffitiSingleton.runAlgorithm(TreeJugglerPlugin.rootChanger);
            }
        });

        ImageIcon treeKNaryMakerIcon = bundle
                .getIcon("treejuggler.treeknarymaker");
        JButton treeKNaryMakerButton = new JButton(treeKNaryMakerIcon);
        treeKNaryMakerButton.setToolTipText("TreeKNaryMaker [Alt+K]");
        treeKNaryMakerButton.setMnemonic(KeyEvent.VK_K);
        treeKNaryMakerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GraffitiSingleton
                        .runAlgorithm(TreeJugglerPlugin.treeKNaryMaker);
            }
        });

        ImageIcon helperNodeStripperIcon = bundle
                .getIcon("treejuggler.helpernodestripper");
        JButton helperNodeStripperButton = new JButton(helperNodeStripperIcon);
        helperNodeStripperButton.setToolTipText("HelperNodeStripper [Alt+B]");
        helperNodeStripperButton.setMnemonic(KeyEvent.VK_B);

        helperNodeStripperButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GraffitiSingleton
                        .runAlgorithm(TreeJugglerPlugin.helperNodeStripper);
            }
        });

        ImageIcon tipoverIcon = bundle.getIcon("treejuggler.tipover");
        JButton tipoverButton = new JButton(tipoverIcon);
        tipoverButton.setToolTipText("TipoverLayout [Alt+T]");
        tipoverButton.setMnemonic(KeyEvent.VK_T);
        tipoverButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GraffitiSingleton.runAlgorithm(TreeJugglerPlugin.tipoverLayout);
            }
        });

        ImageIcon hvIcon = bundle.getIcon("treejuggler.hv");
        JButton hvButton = new JButton(hvIcon);
        hvButton.setToolTipText("HVLayout [Alt+H]");
        hvButton.setMnemonic(KeyEvent.VK_H);
        hvButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GraffitiSingleton.runAlgorithm(TreeJugglerPlugin.hvLayout);
            }
        });

        ImageIcon refreshIcon = bundle.getIcon("treejuggler.refresh");
        JButton refreshButton = new JButton(refreshIcon);
        refreshButton.setToolTipText("LayoutRefresher [Alt+R]");
        refreshButton.setMnemonic(KeyEvent.VK_R);

        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GraffitiSingleton
                        .runAlgorithm(TreeJugglerPlugin.layoutRefresher);
            }
        });

        treeJugglerToolbar.add(DAGSplitterButton);
        treeJugglerToolbar.add(rootChangerButton);
        treeJugglerToolbar.add(treeKNaryMakerButton);
        treeJugglerToolbar.add(helperNodeStripperButton);
        treeJugglerToolbar.add(tipoverButton);
        treeJugglerToolbar.add(hvButton);
        treeJugglerToolbar.add(refreshButton);

        guiComponents = new GraffitiComponent[1];
        guiComponents[0] = treeJugglerToolbar;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
