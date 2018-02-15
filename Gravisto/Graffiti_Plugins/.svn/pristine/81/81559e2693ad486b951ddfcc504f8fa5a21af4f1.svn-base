// =============================================================================
//
//   GraphChoosingPanel.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.isomorphism;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.plugin.parameter.ObjectParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.session.EditorSession;
import org.graffiti.session.Session;

/**
 * This class realises a JPanel that contains a list of checkboxes, one checkbox
 * for each graph that is currently opened in the Gravisto editor.
 * <p>
 * According to a parameter specified by the algorithm, during the execution of
 * which this panel was opened, it lets you choose boxes only up to a certain
 * number.
 * <p>
 * A method is provided that returns the chosen graphs.
 * 
 * @author mary-k
 * @version $Revision$ $Date$
 */
public class GraphChoosingPanel extends JPanel implements ActionListener {

    /**
     * 
     */
    private static final long serialVersionUID = 7571957412451790833L;

    /**
     * A list of the checkboxes with the graphs.
     */
    private LinkedList<JCheckBox> checkboxList = new LinkedList<JCheckBox>();

    /**
     * This parameter counts the number of currently chosen checkboxes.
     */
    private int checkedBoxes = 0;

    private Vector<Session> sessions;

    private IsomorphismParameterDialog parent;

    private AbstractIsomorphism algorithmType;

    /**
     * Costructs a new <code>GraphChoosingPanel</code>.
     * 
     * @param parent
     *            The dialog in which this panel appears.
     * @param algo
     *            The algorithm, during the execution of which this panel was
     *            opened.
     */
    public GraphChoosingPanel(IsomorphismParameterDialog parent,
            AbstractIsomorphism algo) {
        GraffitiSingleton single = GraffitiSingleton.getInstance();
        // attention: PatternSessions are ignored here!!
        sessions = single.getMainSessions();
        algorithmType = algo;
        this.parent = parent;
        JPanel graphList = new JPanel();
        JCheckBox checkbox;
        int numberOfGraphs = sessions.size();
        graphList.setLayout(new GridLayout(numberOfGraphs, 1));
        for (Session ses : sessions) {
            checkbox = new JCheckBox(((EditorSession) ses)
                    .getFileNameAsString()
                    + " " + ses.getId());
            checkbox.addActionListener(this);
            checkboxList.add(checkbox);
            graphList.add(checkbox);
        }
        JScrollPane scrollPane = new JScrollPane(graphList);
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Returns the graphs that have been chosen by the user. These should be
     * exactly as much as defined by the algorithm, during the execution of
     * which this panel was opened, say n. If too small a number of graphs was
     * chosen, this method returns an array with length n that contains only
     * <code>null</code> elements.
     * 
     * @return an array that contains the chosen graphs (in form of
     *         ObjectParameters). If a wrong number of graphs was chosen, all
     *         entries in the array are <code>null</code>.
     */
    public Parameter<?>[] getUpdatedParameters() {
        if (checkedBoxes == algorithmType.GRAPHS_TO_BE_CHOSEN) {
            Parameter<?>[] graphs = new Parameter[algorithmType.GRAPHS_TO_BE_CHOSEN];
            int arrayIndex = 0;
            for (JCheckBox box : checkboxList) {
                if (box.isSelected()) {
                    String graphName = box.getText();
                    for (Session ses : sessions) {
                        if (graphName.equals(((EditorSession) ses)
                                .getFileNameAsString()
                                + " " + ses.getId())) {
                            graphs[arrayIndex] = new ObjectParameter(ses
                                    .getGraph(), graphName,
                                    "description: Graph no. "
                                            + (arrayIndex + 1)
                                            + " to be tested");
                            arrayIndex++;
                        }
                    }
                }
            }
            return graphs;
        } else
            return new Parameter[algorithmType.GRAPHS_TO_BE_CHOSEN];
    }

    /**
     * Returns the number of currently checked boxes.
     * 
     * @return number of checked boxes.
     */
    public int getCheckedBoxes() {
        return checkedBoxes;
    }

    /**
     * Is executed when one of the checkboxes is clicked. It lets the user
     * choose only a certain number, say n, of graphs at a time (defined by the
     * algorithm, during the execution of which this panel was opened). I.e.
     * when the nth checkbox is clicked, all other checkboxes will be
     * deactivated.
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        JCheckBox source = (JCheckBox) e.getSource();
        if (source.isSelected()) {
            checkedBoxes++;
        } else {
            checkedBoxes--;
        }
        if (checkedBoxes == algorithmType.GRAPHS_TO_BE_CHOSEN) {
            for (JCheckBox box : checkboxList) {
                if (!box.isSelected()) {
                    box.setEnabled(false);
                }
            }
            parent.setOkEnabled(true);
        } else {
            for (JCheckBox box : checkboxList) {
                box.setEnabled(true);
            }
            parent.setOkEnabled(false);
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
