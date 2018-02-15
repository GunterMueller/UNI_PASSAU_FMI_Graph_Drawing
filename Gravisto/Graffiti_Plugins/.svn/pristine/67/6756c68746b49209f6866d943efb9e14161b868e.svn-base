package org.graffiti.plugins.algorithms.brandeskoepf;

import java.awt.Color;
import java.awt.event.WindowAdapter;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;

public class JTabbedPaneExample extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = -7850853203378053093L;

    public static void main(String[] args) {

        // Instantiate a frame as main window component;
        JFrame frame = new JFrame();
        // Instantiate a JTabbedPane visual object as next
        // layer container.
        JTabbedPane tPane = new JTabbedPane();

        /*
         * For the JTabbedPane container create two JPanel objects as lowest
         * level container, that will hold the visual control components like
         * buttons...
         */

        // First JPanel for JTabbedPane number one;
        JPanel tab1Panel = new JPanel();
        tab1Panel.setBackground(java.awt.Color.WHITE);

        // A simple GridLayout for JPanel one - 5 rows, 1 column;
        // GridLayout layout4PanelOne = new GridLayout(1,3);
        // tab1Panel.setLayout(layout4PanelOne);

        // Second JPanel for JTabbedPane number two without
        // explicit layout. Therefore it has an implicit
        // layout of type FlowLayout;
        JPanel tab2Panel = new JPanel();
        tab2Panel.setBackground(java.awt.Color.WHITE);

        // Add two sample components to the JPanel for tabbed
        // pane number one.
        addComponents2Panel(tab1Panel);
        addComponents2Panel(tab1Panel);

        // Add three sample components to the JPanel for
        // tabbed pane number two.
        addComponents2Panel(tab2Panel);
        addComponents2Panel(tab2Panel);
        addComponents2Panel(tab2Panel);

        // Realize both JPanels by adding them two the tabbed
        // pane.
        tPane.addTab("Tab holding JPanel one", tab1Panel);
        tPane.addTab("Tab holding JPanel two", tab2Panel);

        // Add the JTabbedPane object to top level container
        // JFrame.
        frame.getContentPane().add(tPane);

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

    /**
     * Simple add method for components to a JPanel. Here a BoxLayout is used as
     * a sample for positioning the components.
     * 
     * @param panel
     *            The panel into which some javax.swing.JComponent
     *            (java.awt.Component) objects are placed.
     */
    static void addComponents2Panel(JPanel panel) {

        // Create extra JPanel for components - That way two layouts can be
        // nested. The panel argument has some layout - the new butonPanel
        // will can get another layout for the components...

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);

        // Sample BoxLayout. Easier to use than GridBagLayout but almost same
        // possibilities.
        BoxLayout boxLay = new BoxLayout(buttonPanel, BoxLayout.X_AXIS);
        buttonPanel.setLayout(boxLay);

        // Sample JComponents; Text: JTextField
        JSpinner spinner = new JSpinner();
        JLabel label = new JLabel("Label");

        // Resizing a component to preferred size overrides the default values
        // which sometimes are not appropriate for intended use.
        spinner.setPreferredSize(new java.awt.Dimension(50, 20));

        // This adds empty space - the size is defined by argument Dimension.
        // buttonPanel.add (Box.createRigidArea(new java.awt.Dimension(100,0)));
        // After the empty space add the JLabel component;
        buttonPanel.add(label);
        // After the JLabel component add 50 pixels space into x direction;
        buttonPanel.add(Box.createRigidArea(new java.awt.Dimension(50, 0)));
        // Add the next component after the spacing.
        buttonPanel.add(spinner);
        buttonPanel.add(Box.createRigidArea(new java.awt.Dimension(200, 0)));
        // Add the buttonPanel to the panel argument (-> two nested layouts)
        // JPanel one has GridLayout with 5 rows.
        // JPanel two has FlowLayout.
        // notwendig f�r die Schachtelung und
        panel.add(buttonPanel);
    }

    public void setDefault(JSpinner spinner) {
        spinner.setValue(new Integer(100));
    }

}
