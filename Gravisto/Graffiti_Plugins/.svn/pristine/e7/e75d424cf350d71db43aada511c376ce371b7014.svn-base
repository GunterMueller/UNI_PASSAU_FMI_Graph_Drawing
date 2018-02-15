package quoggles.stdboxes.input;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.graffiti.graph.Graph;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.selection.Selection;

import quoggles.auxiliary.InsetLineBorder;
import quoggles.boxes.Box;
import quoggles.boxes.IBox;
import quoggles.constants.IBoxConstants;
import quoggles.constants.ITypeConstants;
import quoggles.exceptions.BoxNotExecutedException;
import quoggles.exceptions.QueryExecutionException;
import quoggles.representation.BoxRepresentation;
import quoggles.representation.IBoxRepresentation;

/**
 * ( Input: set by the system; depends on the name of the box )<p>
 * Output: same as input<p>
 * 
 * Input box without incoming edges. Those boxes are displayed to the left and
 * they get their input by the system.
 */
public class Input_Box extends Box {

    /** Name of the box; indicates the type of its input */
    private String boxName;
        
        
    /**
     * Construct the input box.
     * 
     * @param text name of the box specifying the type of the input
     */
    public Input_Box(String text) {
        boxName = text;
    }

        
    /**
     * Returns the text string given to the constructor.
     * 
     * @return name of the box
     */
    public String getBoxName() {
        return boxName;
    }

    /**
     * @see quoggles.boxes.IBox#getGraphicalRepresentation()
     */
    public IBoxRepresentation getGraphicalRepresentation() {
        if (iBoxGRep == null || 
            !(iBoxGRep instanceof InputBoxRepresentation)) {

            String displayName = getBoxName();
            if (IBoxConstants.GRAPH_INPUT.equals(displayName)) {
                displayName = IBoxConstants.GRAPH_INPUT_DISPLAY;
            } else if (IBoxConstants.SELECTION_INPUT.equals(displayName)) {
                displayName = IBoxConstants.SELECTION_INPUT_DISPLAY;
            }
            
            iBoxGRep = new InputBoxRepresentation(this, displayName);
        }

        return iBoxGRep;
    }

    /**
     * Returns zero.
     * 
     * @see quoggles.boxes.IBox#getNumberOfInputs()
     */
    public int getNumberOfInputs() {
        return 0;
    }

    /**
     * Special set input method for <code>InputBox</code>. Using the standard 
     * one would make the system throw an <code>InvalidInputException</code> 
     * since an InputBox's <code>getNumberOfInputs</code> method returns 0.
     * 
     * @param input
     */
    public void setInputBoxInput(Object input) {
        this.inputs = new Object[]{ input };
    }
    
    /**
     * Overridden to prevent <code>InputNotSetException</code>.
     * @see quoggles.boxes.IBox#execute()
     */
    public void execute() throws QueryExecutionException {
        Collection outputCol = null;
        if (IBoxConstants.GRAPH_INPUT.equals(boxName)) {
            outputCol = ((Graph)inputs[0]).getGraphElements();
        } else if (IBoxConstants.SELECTION_INPUT.equals(boxName)) {
            outputCol = ((Selection)inputs[0]).getElements();
        } else {
            outputs = new Collection[]{ new ArrayList(0) };
            return;
        }
        outputs = new Collection[]{ outputCol };
    }
    
    /**
     * Overridden to prevent <code>InputNotSetException</code>.
     * @see quoggles.boxes.IBox#getOutputs()
     */
    public Object[] getOutputs() throws BoxNotExecutedException {
        return outputs;
    }

    /**
     * @see quoggles.boxes.IBox#getInputTypes()
     */
    public int[] getInputTypes() {
        if (IBoxConstants.GRAPH_INPUT.equals(boxName) ||
            IBoxConstants.SELECTION_INPUT.equals(boxName)) {
            return new int[] { ITypeConstants.GRAPH_ELEMENTS +
                ITypeConstants.GRAPH_ELEMENT };
        } else {
            return new int[] { ITypeConstants.GENERAL };
        }
    }


    /**
     * Representation of an input box
     */
    public class InputBoxRepresentation implements IBoxRepresentation {

        private BoxRepresentation boxRep;
        
        private IBox iBox;
        
        private Point2D outputPos = new Point2D.Double(1.0, 0.5);
        
        private String boxName = "";
        
        
        /**
         * Constructor of <code>InputBoxRepresentation</code>.
         * 
         * @param box
         * @param name name of the box. Used to decide what input it needs.
         * Use constants from IBoxConstants here.
         */
        public InputBoxRepresentation(IBox box, String name) {
            iBox = box;
            boxName = name;
            updateGraphicalRep();
        }


        /**
         * @see quoggles.representation.IBoxRepresentation#updateGraphicalRep()
         */
        public void updateGraphicalRep() {
            if (boxRep == null) {
                boxRep = new InputBox_Rep(this, boxName);
            } else {
                boxRep.removeAll();
                BoxRepresentation newBoxRep = new InputBox_Rep(this, boxName);
                
                boxRep.setBorder(newBoxRep.getBorder());
                boxRep.setLayout(new BorderLayout());
                boxRep.setOpaque(false);
                Component sPanel = newBoxRep.getComponent(0);
                boxRep.add(sPanel, BorderLayout.CENTER);
                boxRep.setSize(sPanel.getPreferredSize());
                boxRep.setPreferredSize(sPanel.getPreferredSize());
    
                boxRep.validate();
            }
        }

        /**
         * @see quoggles.representation.IBoxRepresentation#getRepresentation()
         */
        public BoxRepresentation getRepresentation() {
            return boxRep;
        }

        /**
         * Empty method.
         * 
         * @see quoggles.representation.IBoxRepresentation#
         * setParameters(quoggles.parameters.Parameter[], boolean)
         */
        public void setParameters(Parameter[] params, boolean fromBox) { }

        /**
         * @see quoggles.representation.IBoxRepresentation#getIBox()
         */
        public IBox getIBox() {
            return iBox;
        }

        /**
         * @see quoggles.representation.IBoxRepresentation#
         * getOtherRelOutputPos()
         */
        public Point2D[] getRelOutputsPos() {
            return new Point2D[]{ outputPos };
        }

        /**
         * @see quoggles.representation.IBoxRepresentation#
         * getOtherRelInputPos()
         */
        public Point2D[] getRelInputsPos() {
            return null;
        }

        /**
         * Empty (no ID displayed).
         * 
         * @see quoggles.representation.IBoxRepresentation#updateId()
         */
        public void updateId() { }
    }
    
    
    /**
     * Graphical representation of an input box.
     */
    public class InputBox_Rep extends BoxRepresentation {
        
        /**
         * Constructor. Displays a label.
         */
        public InputBox_Rep(IBoxRepresentation iBoxRep, String text) {
            super(iBoxRep);
            
            JPanel sPanel = new JPanel();
            sPanel.setLayout(new BorderLayout());
            sPanel.setOpaque(false);
            sPanel.setBackground(IBoxConstants.BOX_BACKGROUND);
            setBorder(new InsetLineBorder(Color.BLACK, 2));

            JLabel label = new JLabel(text);
            sPanel.add(label, BorderLayout.CENTER);

            Dimension size = new Dimension(
                IBoxConstants.DEFAULT_INPUTBOX_WIDTH,
                Math.max(IBoxConstants.DEFAULT_INPUTBOX_HEIGHT,
                    label.getPreferredSize().height + 15));
            sPanel.setSize(size);
            sPanel.setPreferredSize(size);
        
            setLayout(new BorderLayout());
            setOpaque(false);
            add(sPanel, BorderLayout.CENTER);
            setSize(sPanel.getPreferredSize());
            setPreferredSize(sPanel.getPreferredSize());

            validate();
        }
    }
    
}