// =============================================================================
//
//   ConfigurationPanel.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.graffiti.plugins.views.fast.FastViewPlugin;
import org.graffiti.plugins.views.fast.opengl.OpenGLConfiguration;
import org.graffiti.util.Pair;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class ConfigurationPanel extends JPanel {
    /**
     * 
     */
    private static final long serialVersionUID = 8566504646839043860L;
    private static final int GAP = 5;
    private static final String FAA_OFF = FastViewPlugin
            .getString("fastview.engines.opengl.config.buffers.off");
    private static final String FAA_NUM = FastViewPlugin
            .getString("fastview.engines.opengl.config.buffers.num");

    private static class FAAComboAdapter {
        private int sampleBuffers;
        private String description;

        public FAAComboAdapter(int sampleBuffers) {
            this.sampleBuffers = sampleBuffers;
            if (sampleBuffers == 0) {
                description = FAA_OFF;
            } else {
                description = String.format(FAA_NUM, sampleBuffers);
            }
        }

        public int getSampleBuffers() {
            return sampleBuffers;
        }

        @Override
        public String toString() {
            return description;
        }
    };

    public ConfigurationPanel() {
        final OpenGLConfiguration config = OpenGLConfiguration.get();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(Component.LEFT_ALIGNMENT);
        setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        add(new JLabel(FastViewPlugin
                .getString("fastview.engines.opengl.config.effect"),
                SwingConstants.LEFT));
        add(Box.createRigidArea(new Dimension(0, GAP)));
        final JCheckBox aaPointsCheckbox = new JCheckBox(FastViewPlugin
                .getString("fastview.engines.opengl.config.aapoints"));
        aaPointsCheckbox.setAlignmentX(Component.LEFT_ALIGNMENT);
        aaPointsCheckbox.setSelected(config.isAntialiasingPoints());
        add(aaPointsCheckbox);
        final JCheckBox aaLinesCheckbox = new JCheckBox(FastViewPlugin
                .getString("fastview.engines.opengl.config.aalines"));
        aaLinesCheckbox.setAlignmentX(Component.LEFT_ALIGNMENT);
        aaLinesCheckbox.setSelected(config.isAntialiasingLines());
        add(aaLinesCheckbox);
        final JCheckBox aaPolygonsCheckbox = new JCheckBox(FastViewPlugin
                .getString("fastview.engines.opengl.config.aapolygons"));
        aaPolygonsCheckbox.setAlignmentX(Component.LEFT_ALIGNMENT);
        aaPolygonsCheckbox.setSelected(config.isAntialiasingPolygons());
        add(aaPolygonsCheckbox);
        add(Box.createRigidArea(new Dimension(0, GAP)));
        JPanel fullscreenAAPanel = new JPanel(new BorderLayout(GAP, GAP));
        fullscreenAAPanel.add(new JLabel(FastViewPlugin
                .getString("fastview.engines.opengl.config.fullscreenaa")),
                BorderLayout.WEST);
        Pair<Vector<FAAComboAdapter>, FAAComboAdapter> faaPair = createFAAVector(config
                .getSampleBuffers());
        final JComboBox fullscreenAACombo = new JComboBox(faaPair.getFirst());
        fullscreenAACombo.setEditable(false);
        fullscreenAACombo.setSelectedItem(faaPair.getSecond());
        fullscreenAAPanel.add(fullscreenAACombo, BorderLayout.CENTER);
        fullscreenAAPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(fullscreenAAPanel);

        add(Box.createRigidArea(new Dimension(0, GAP)));
        final JCheckBox noDirectDrawCheckbox = new JCheckBox(FastViewPlugin
                .getString("fastview.engines.opengl.config.noddraw"));
        noDirectDrawCheckbox.setAlignmentX(Component.LEFT_ALIGNMENT);
        noDirectDrawCheckbox.setSelected(config.isNoDirectDraw());
        add(noDirectDrawCheckbox);

        validate();
        aaPointsCheckbox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                config.setAntialiasingPoints(aaPointsCheckbox.isSelected());
            }
        });
        aaLinesCheckbox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                config.setAntialiasingLines(aaLinesCheckbox.isSelected());
            }
        });
        aaPolygonsCheckbox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                config.setAntialiasingPolygons(aaPolygonsCheckbox.isSelected());
            }
        });
        fullscreenAACombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                config.setSampleBuffers(((FAAComboAdapter) fullscreenAACombo
                        .getSelectedItem()).getSampleBuffers());
            }
        });
        noDirectDrawCheckbox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                config.setNoDirectDraw(noDirectDrawCheckbox.isSelected());
            }
        });
    }

    private Pair<Vector<FAAComboAdapter>, FAAComboAdapter> createFAAVector(
            int sampleBuffers) {
        Vector<FAAComboAdapter> vector = new Vector<FAAComboAdapter>();
        FAAComboAdapter off = new FAAComboAdapter(0);
        vector.add(off);
        FAAComboAdapter current = off;
        int[] values = new int[] { 2, 4, 8, 16 };
        for (int value : values) {
            FAAComboAdapter adapter = new FAAComboAdapter(value);
            vector.add(adapter);
            if (value == sampleBuffers) {
                current = adapter;
            }
        }
        return Pair.create(vector, current);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
