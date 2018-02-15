package org.graffiti.plugins.algorithms.circulardrawing;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Time;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * @author demirci Created on Jul 27, 2005
 */
public class TableOfStatistics extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = -8881771534526077588L;

    private boolean DEBUG = false;

    private Object[][] data1;

    private JTable table;

    /**
     * Comment for <code>circularConst</code>
     * 
     * @see org.graffiti.plugins.algorithms.circulardrawing.CircularConst
     */
    private CircularConst circularConst = new CircularConst();

    /**
     * Konstruktur.
     */
    public TableOfStatistics() {
        super(new GridLayout(1, 0));

        String[] columnNames = { "Algorithm", "# Nodes", "# Edges",
                "# Crossing", "Runtime [ms]" };

        Object[][] data = { { "Circular", "", "", "", "" },
                { "CircularPP", "", "", "", "" },
                { "Circular I", "", "", "", "" },
                { "CircularPP", "", "", "", "" },
                { "Circular II", "", "", "", "" },
                { "CircularPP", "", "", "", "" } };
        this.data1 = data;
        table = new JTable(data1, columnNames);
        table.setPreferredScrollableViewportSize(new Dimension(500, 100));

        if (DEBUG) {
            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    printDebugData(table);
                }
            });
        }

        // Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);

        // Add the scroll pane to this panel.
        add(scrollPane);
    }

    /**
     * @param table
     *            output of the table contains
     */
    private void printDebugData(JTable table) {
        int numRows = table.getRowCount();
        int numCols = table.getColumnCount();
        javax.swing.table.TableModel model = table.getModel();

        System.out.println("Value of data: ");
        for (int i = 0; i < numRows; i++) {
            System.out.print("    row " + i + ":");
            for (int j = 0; j < numCols; j++) {
                System.out.print("  " + model.getValueAt(i, j));
            }
            System.out.println();
        }
        System.out.println("--------------------------");
    }

    /**
     * Output of the data on gui.
     */
    public void showData() {
        BufferedReader br;
        File file;
        try {
            // file = new
            // File("/home/cip/demirci/workspace/benchmarkResult/statistic.txt");
            file = new File(
                    "/home/cip/demirci/workspace/Graffiti_Plugins/org/graffiti/"
                            + "plugins/algorithms/circulardrawing/statistic.txt");
            br = new BufferedReader(new FileReader(file));
            int i = 1, t = 1, s = 1;
            int j = 0, k = 2, l = 4;
            int numRows = table.getRowCount();
            int numCols = table.getColumnCount();
            javax.swing.table.TableModel model = table.getModel();
            if (CircularConst.SESSION_STATE == 1) {
                while (br.ready()) {
                    String line = br.readLine();
                    if (line.equals("#circular")) {
                        while (br.ready()) {
                            line = br.readLine();
                            model.setValueAt(line, j, i);
                            if (i == 3) {
                                break;
                            }
                            i++;
                        }
                        Long time = new Long(
                                ((Time) CircularConst.ALGO_RUNTIME[0])
                                        .getTime());
                        model.setValueAt(time, 0, 4);
                    }
                    if (line.equals("#circularI")) {
                        while (br.ready()) {
                            line = br.readLine();
                            model.setValueAt(line, k, t);
                            if (t == 3) {
                                break;
                            }
                            t++;
                        }
                        Long time = new Long(
                                ((Time) CircularConst.ALGO_RUNTIME[1])
                                        .getTime());
                        model.setValueAt(time, 2, 4);
                    }
                    if (line.equals("#circularII")) {
                        while (br.ready()) {
                            line = br.readLine();
                            model.setValueAt(line, l, s);
                            if (s == 3) {
                                break;
                            }
                            s++;
                        }
                        Long time = new Long(
                                ((Time) CircularConst.ALGO_RUNTIME[2])
                                        .getTime());
                        model.setValueAt(time, 4, 4);
                    }
                }
            }
            if (circularConst.getAlgorithm(0) == "1") {
                model.setValueAt(CircularConst.CPP_DATA[0], 1, 1);
                model.setValueAt(CircularConst.CPP_DATA[1], 1, 2);
                model.setValueAt(CircularConst.CPP_DATA[2], 1, 3);
                model.setValueAt(CircularConst.CPP_DATA[3], 1, 4);
            } else if (circularConst.getAlgorithm(1) == "1") {
                model.setValueAt(CircularConst.CPPI_DATA[0], 3, 1);
                model.setValueAt(CircularConst.CPPI_DATA[1], 3, 2);
                model.setValueAt(CircularConst.CPPI_DATA[2], 3, 3);
                model.setValueAt(CircularConst.CPPI_DATA[3], 3, 4);
            } else if (circularConst.getAlgorithm(2) == "1") {
                model.setValueAt(CircularConst.CPPII_DATA[0], 5, 1);
                model.setValueAt(CircularConst.CPPII_DATA[1], 5, 2);
                model.setValueAt(CircularConst.CPPII_DATA[2], 5, 3);
                model.setValueAt(CircularConst.CPPII_DATA[3], 5, 4);
            }
        } catch (IOException io) {
            System.out.println("the statistic file is not exist!.");
        }
    }

    public void deleteData() {
        int numRows = table.getRowCount();
        int numCols = table.getColumnCount();
        javax.swing.table.TableModel model = table.getModel();

        System.out.println("Value of data: ");
        for (int i = 0; i < numRows; i++) {
            System.out.print("    row " + i + ":");
            for (int j = 1; j < numCols; j++) {
                System.out.print("  " + model.getValueAt(i, j));
                model.setValueAt(null, i, j);
            }
            System.out.println();
        }
        System.out.println("--------------------------");
    }

    /**
     * Create the GUI and show it. For thread safety, this method should be
     * invoked from the event-dispatching thread.
     */
    private static void createAndShowGUI() {
        // Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        // Create and set up the window.
        JFrame frame = new JFrame("Statistic of the Algorithm");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create and set up the content pane.
        TableOfStatistics newContentPane = new TableOfStatistics();
        newContentPane.setOpaque(true); // content panes must be opaque
        frame.setContentPane(newContentPane);

        // Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
