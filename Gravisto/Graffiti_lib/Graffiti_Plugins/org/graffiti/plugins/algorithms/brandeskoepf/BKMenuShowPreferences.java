//==============================================================================
//
//   BKMenuShowPreferences.java
//
//   Copyright (c) 2001-2003 Graffiti Team, Uni Passau
//
//==============================================================================
// $Id: BKMenuShowPreferences.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.brandeskoepf;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.WindowConstants;

import org.graffiti.help.HelpContext;
import org.graffiti.plugin.actions.GraffitiAction;

/**
 * This class realises the options windows
 * 
 * @author Florian Fischer
 */
public class BKMenuShowPreferences extends GraffitiAction implements
        ActionListener {
    // ~ Static fields/initializers
    // =============================================

    /**
     * 
     */
    private static final long serialVersionUID = 2969754491182067555L;

    /** All the input fields */
    private static ArrayList<JComponent> myFields;

    /** The main frame */
    private static JFrame frame;

    // ~ Constructors
    // ===========================================================

    /**
     * Creates a new BKMenuShowPreferences object.
     * 
     * @param name
     *            The name of this window
     */
    public BKMenuShowPreferences(String name) {
        super(name, null);

        myFields = new ArrayList<JComponent>();

        // the fields for tab 2
        for (int i = 0; i < 11; i++) {
            myFields.add(new JTextField());
        }

        // the fields for tab 3
        for (int i = 11; i < 17; i++) {
            myFields.add(new JSpinner());
        }

        // The layout management in tab 1
        String[] calibrationModes = { "Align center to 0",
                "Align to assignment of smallest width", "balance center to 0" };
        String[] assignmentModes = { "Average median", "Average of all" };
        String[] drawModes = { "B/K step 1", "B/K step 2", "B/K step 3",
                "B/K step 4", "Horizontal layout", "Radial layout" };
        String[] samplingPointsModes = { "fixed", "dynamic" };
        String[] computeLongSpanEdges = { "compute long span edges",
                "don't compute long span edges" };

        JComboBox calibrationRollUp = new JComboBox(calibrationModes);
        calibrationRollUp.setSelectedIndex(1);

        JComboBox assignmentRollUp = new JComboBox(assignmentModes);
        assignmentRollUp.setSelectedIndex(1);

        JComboBox drawRollUp = new JComboBox(drawModes);
        drawRollUp.setSelectedIndex(4);

        JComboBox samplingPointsRollUp = new JComboBox(samplingPointsModes);
        samplingPointsRollUp.setSelectedIndex(0);

        JComboBox computeLongRollUp = new JComboBox(computeLongSpanEdges);
        computeLongRollUp.setSelectedIndex(0);

        myFields.add(drawRollUp);
        myFields.add(calibrationRollUp);
        myFields.add(assignmentRollUp);
        myFields.add(samplingPointsRollUp);
        myFields.add(computeLongRollUp);
    }

    // ~ Methods
    // ================================================================

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    @Override
    public boolean isEnabled() {
        return super.enabled;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    @Override
    public HelpContext getHelpContext() {
        return null;
    }

    /**
     * Show the preference window
     * 
     * @param e
     *            The action event
     */
    public void actionPerformed(ActionEvent e) {
        System.out.println(e.getActionCommand());

        if (e.getActionCommand() == "Options") {
            drawPrefWindow();
        } else if (e.getActionCommand() == "ok") {
            setPrefValues();
            frame.setVisible(false);
            frame.dispose();
        } else if (e.getActionCommand() == "cancel") {
            frame.setVisible(false);
            frame.dispose();
        } else if (e.getActionCommand() == "default") {
            BKConst.setDefault();
            frame.setVisible(false);
            frame.dispose();

            drawPrefWindow();
        }
    }

    /**
     * This function sets all the values of the actual window after a user
     * OK-click
     */
    private static void setPrefValues() {
        BKConst.setPATH_LEVEL(((JTextField) myFields.get(0)).getText());
        BKConst.setPATH_ORDER(((JTextField) myFields.get(1)).getText());
        BKConst.setPATH_DUMMY(((JTextField) myFields.get(2)).getText());
        BKConst.setPATH_COORD_X(((JTextField) myFields.get(3)).getText());
        BKConst.setPATH_COORD_Y(((JTextField) myFields.get(4)).getText());
        BKConst.setPATH_SHAPE(((JTextField) myFields.get(5)).getText());
        BKConst.setPATH_CUTEDGE(((JTextField) myFields.get(6)).getText());
        BKConst.setPATH_BENDS(((JTextField) myFields.get(7)).getText());
        BKConst.setBEND_I(((JTextField) myFields.get(8)).getText());
        BKConst.setPATH_SHAPE_POLY(((JTextField) myFields.get(9)).getText());
        BKConst.setPATH_SHAPE_SMOOTH(((JTextField) myFields.get(10)).getText());

        BKConst.setMINDIST(((Integer) ((JSpinner) myFields.get(11)).getValue())
                .doubleValue());
        BKConst.setRADIAL_MINDIST(((Integer) ((JSpinner) myFields.get(12))
                .getValue()).doubleValue());
        BKConst.setLEFT_DIST(((Integer) ((JSpinner) myFields.get(13))
                .getValue()).doubleValue());
        BKConst
                .setTOP_DIST(((Integer) ((JSpinner) myFields.get(14))
                        .getValue()).doubleValue());
        BKConst.setLEVEL_DIST(((Integer) ((JSpinner) myFields.get(15))
                .getValue()).doubleValue());
        BKConst.setRADIAL_LEVEL_DIST(((Integer) ((JSpinner) myFields.get(16))
                .getValue()).doubleValue());

        BKConst.setDRAW(((JComboBox) myFields.get(17)).getSelectedIndex());
        BKConst.setCALIBRATION(((JComboBox) myFields.get(18))
                .getSelectedIndex());
        BKConst.setCOORD_ASSIGNMENT(((JComboBox) myFields.get(19))
                .getSelectedIndex());
        BKConst.setSAMPLING_TYPE(((JComboBox) myFields.get(20))
                .getSelectedIndex());
        BKConst.setCOMPUT_LONG_SPAN_EDGES(((JComboBox) myFields.get(21))
                .getSelectedIndex());
    }

    /**
     * Simple add method for components to a JPanel. Here a BoxLayout is used as
     * a sample for positioning the components.
     * 
     * @param panel
     *            The panel into which some javax.swing.JComponent
     *            (java.awt.Component) objects are placed.
     * @param i
     *            The number of the input field
     */
    private void addComponents2Panel(JPanel panel, int i) {
        // Create extra JPanel for components - That way two layouts can be
        // nested. The panel argument has some layout - the new butonPanel
        // will can get another layout for the components...
        JPanel buttonPanel = new JPanel();

        // Sample BoxLayout. Easier to use than GridBagLayout but almost same
        // possibilities.
        BoxLayout boxLay = new BoxLayout(buttonPanel, BoxLayout.X_AXIS);
        buttonPanel.setLayout(boxLay);

        JLabel label = null;

        // tab 2
        if (i < 11) {
            // Sample JComponents; Text: JTextField
            JTextField spinner = (JTextField) myFields.get(i);

            switch (i) {
            case 0:
                label = new JLabel("Level number");
                spinner.setText(BKConst.getPATH_LEVEL());

                break;

            case 1:
                label = new JLabel("Order number");
                spinner.setText(BKConst.getPATH_ORDER());

                break;

            case 2:
                label = new JLabel("Dummy info");
                spinner.setText(BKConst.getPATH_DUMMY());

                break;

            case 3:
                label = new JLabel("X-coordinate");
                spinner.setText(BKConst.getPATH_COORD_X());

                break;

            case 4:
                label = new JLabel("Y-coordinate");
                spinner.setText(BKConst.getPATH_COORD_Y());

                break;

            case 5:
                label = new JLabel("Shape offset");
                spinner.setText(BKConst.getPATH_SHAPE());

                break;

            case 6:
                label = new JLabel("Cut Edge");
                spinner.setText(BKConst.getPATH_CUTEDGE());

                break;

            case 7:
                label = new JLabel("Bends");
                spinner.setText(BKConst.getPATH_BENDS());

                break;

            case 8:
                label = new JLabel("Bend-attribute name");
                spinner.setText(BKConst.getBEND_I());

                break;

            case 9:
                label = new JLabel("Edge shape polyline");
                spinner.setText(BKConst.getPATH_SHAPE_POLY());

                break;

            case 10:
                label = new JLabel("Edge shape smooth line");
                spinner.setText(BKConst.getPATH_SHAPE_SMOOTH());

                break;
            }

            // Resizing a component to preferred size overrides the default
            // values
            // which sometimes are not appropriate for intended use.
            spinner.setPreferredSize(new java.awt.Dimension(300, 20));
            label.setPreferredSize(new java.awt.Dimension(200, 20));

            // This adds empty space - the size is defined by argument
            // Dimension.
            // buttonPanel.add (Box.createRigidArea(new
            // java.awt.Dimension(100,0)));
            // After the empty space add the JLabel component;
            buttonPanel.add(label);

            // After the JLabel component add 50 pixels space into x direction;
            buttonPanel.add(Box.createRigidArea(new java.awt.Dimension(20, 0)));

            // Add the next component after the spacing.
            buttonPanel.add(spinner);

            // buttonPanel.add (Box.createRigidArea(new
            // java.awt.Dimension(200,0)));
            // Add the buttonPanel to the panel argument (-> two nested layouts)
            // JPanel one has GridLayout with 5 rows.
            // JPanel two has FlowLayout.
            // notwendig f�r die Schachtelung und
            panel.add(buttonPanel);
        }

        // tab 3
        else if ((i > 10) && (i < 17)) {
            // Sample JComponents; Text: JTextField
            JSpinner spinner = (JSpinner) myFields.get(i);

            switch (i) {
            case 11:
                label = new JLabel("Node distance horizontal");
                spinner.setValue(new Integer((new Double(BKConst.getMINDIST()))
                        .intValue()));

                break;

            case 12:
                label = new JLabel("Node distance radial");
                spinner.setValue(new Integer((new Double(BKConst
                        .getRADIAL_MINDIST())).intValue()));

                break;

            case 13:
                label = new JLabel("Distance from the left border");
                spinner.setValue(new Integer(
                        (new Double(BKConst.getLEFT_DIST())).intValue()));

                break;

            case 14:
                label = new JLabel("Distance form the top border");
                spinner.setValue(new Integer(
                        (new Double(BKConst.getTOP_DIST())).intValue()));

                break;

            case 15:
                label = new JLabel("Level distance horizontal");
                spinner.setValue(new Integer((new Double(BKConst
                        .getLEVEL_DIST())).intValue()));

                break;

            case 16:
                label = new JLabel("Initial level distance radial");
                spinner.setValue(new Integer((new Double(BKConst
                        .getRADIAL_LEVEL_DIST())).intValue()));

                break;
            }

            // Resizing a component to preferred size overrides the default
            // values
            // which sometimes are not appropriate for intended use.
            spinner.setPreferredSize(new java.awt.Dimension(100, 20));
            label.setPreferredSize(new java.awt.Dimension(200, 20));

            // This adds empty space - the size is defined by argument
            // Dimension.
            // buttonPanel.add (Box.createRigidArea(new
            // java.awt.Dimension(100,0)));
            // After the empty space add the JLabel component;
            buttonPanel.add(label);

            // After the JLabel component add 50 pixels space into x direction;
            buttonPanel.add(Box.createRigidArea(new java.awt.Dimension(20, 0)));

            // Add the next component after the spacing.
            buttonPanel.add(spinner);
            buttonPanel
                    .add(Box.createRigidArea(new java.awt.Dimension(200, 0)));

            // Add the buttonPanel to the panel argument (-> two nested layouts)
            // JPanel one has GridLayout with 5 rows.
            // JPanel two has FlowLayout.
            // notwendig f�r die Schachtelung und
            panel.add(buttonPanel);
        }

        // tab 1
        else {
            // Sample JComponents; Text: JTextField
            JComboBox combo = (JComboBox) myFields.get(i);

            switch (i) {
            case 17:
                label = new JLabel("Draw mode");
                combo.setSelectedIndex(BKConst.getDRAW());

                break;

            case 18:
                label = new JLabel("Calibration mode");
                combo.setSelectedIndex(BKConst.getCALIBRATION());

                break;

            case 19:
                label = new JLabel("Coordinate assignment mode");
                combo.setSelectedIndex(BKConst.getCOORD_ASSIGNMENT());

                break;

            case 20:
                label = new JLabel("Type of computing sampling points");
                combo.setSelectedIndex(BKConst.getSAMPLING_TYPE());

                break;

            case 21:
                label = new JLabel("Long span edges");
                combo.setSelectedIndex(BKConst.getCOMPUT_LONG_SPAN_EDGES());

                break;
            }

            // Resizing a component to preferred size overrides the default
            // values
            // which sometimes are not appropriate for intended use.
            combo.setPreferredSize(new java.awt.Dimension(300, 20));
            label.setPreferredSize(new java.awt.Dimension(200, 20));

            // This adds empty space - the size is defined by argument
            // Dimension.
            // buttonPanel.add (Box.createRigidArea(new
            // java.awt.Dimension(100,0)));
            // After the empty space add the JLabel component;
            buttonPanel.add(label);

            // After the JLabel component add 50 pixels space into x direction;
            buttonPanel.add(Box.createRigidArea(new java.awt.Dimension(20, 0)));

            // Add the next component after the spacing.
            buttonPanel.add(combo);

            // buttonPanel.add(Box.createRigidArea(new java.awt.Dimension(80,
            // 0)));
            // Add the buttonPanel to the panel argument (-> two nested layouts)
            // JPanel one has GridLayout with 5 rows.
            // JPanel two has FlowLayout.
            // notwendig f�r die Schachtelung und
            panel.add(buttonPanel);
        }
    }

    /**
     * This function draws the window
     */
    private void drawPrefWindow() {
        // Instantiate a frame as main window component;
        frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        // frame.getContentPane().setLayout(new GridBagLayout());
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

        JPanel tabsPanel = new JPanel();
        tabsPanel.setLayout(new SpringLayout());

        // Instantiate a JTabbedPane visual object as next
        // layer container.
        JTabbedPane tPane = new JTabbedPane();

        JPanel buttonPanel = new JPanel();

        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        JButton defaultButton = new JButton("Default");

        okButton.setActionCommand("ok");
        cancelButton.setActionCommand("cancel");
        defaultButton.setActionCommand("default");

        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        defaultButton.addActionListener(this);

        /*
         * For the JTabbedPane container create two JPanel objects as lowest
         * level container, that will hold the visual control components like
         * buttons...
         */

        // First JPanel for JTabbedPane number one;
        JPanel tab1Panel = new JPanel();
        tab1Panel.setAutoscrolls(true);

        // Second JPanel for JTabbedPane number two without
        // explicit layout.
        JPanel tab2Panel = new JPanel();

        // Second JPanel for JTabbedPane number two without
        // explicit layout.
        JPanel tab3Panel = new JPanel();

        // Add two sample components to the JPanel for tabbed
        // pane number one.
        for (int i = 0; i < 11; i++) {
            addComponents2Panel(tab1Panel, i);
        }

        // Add three sample components to the JPanel for
        // tabbed pane number two.
        for (int i = 11; i < 17; i++) {
            addComponents2Panel(tab2Panel, i);
        }

        // Add three sample components to the JPanel for
        // tabbed pane number three.
        for (int i = 17; i < 22; i++) {
            addComponents2Panel(tab3Panel, i);
        }

        // Realize both JPanels by adding them two the tabbed
        // pane.
        tPane.addTab("Algorithm internals", tab3Panel);
        tPane.addTab("Gravisto paths", tab1Panel);
        tPane.addTab("Distances", tab2Panel);

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(defaultButton);

        // mainPanel.add(tabsPanel);
        // mainPanel.add(buttonPanel);
        // Add the JTabbedPane object to top level container
        // JFrame.
        // frame.getContentPane().add(tPane);
        tPane.setPreferredSize(new java.awt.Dimension(600, 350));
        buttonPanel.setPreferredSize(new java.awt.Dimension(600, 50));

        mainPanel.add(tPane);
        mainPanel.add(buttonPanel);

        frame.getContentPane().add(mainPanel);

        // frame.getContentPane().add(buttonPanel);
        // Set window (JFrame) size.
        frame.setSize(600, 400);
        frame.setResizable(false);

        // A MUST! Explicit call to show, because default value for visible
        // of a window is FALSE.
        // Alternate possibility: frame.setVisible(true);
        frame.setVisible(true);

        // Define the closing behaviour of this frame.
        // If not defined closing the frame will not end the invoked JVM.
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent closing) {
                closing.getWindow().setVisible(false);
                closing.getWindow().dispose();
            }
        });
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
