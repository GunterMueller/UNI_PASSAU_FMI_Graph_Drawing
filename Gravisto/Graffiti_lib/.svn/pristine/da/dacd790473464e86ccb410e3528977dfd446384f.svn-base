package quoggles.representation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.graffiti.plugin.parameter.Parameter;

import quoggles.auxiliary.InsetLineBorder;
import quoggles.auxiliary.Util;
import quoggles.boxes.IBox;
import quoggles.constants.IBoxConstants;
import quoggles.constants.ITypeConstants;

/**
 * 
 */
public class DefaultBoxRepresentation implements IBoxRepresentation {

    protected final String INPUTLABEL = "In: ";

    protected final String OUTPUTLABEL = "Out: ";

    /** The IBox this class represents. */
    protected IBox box;

    protected BoxRepresentation graphicalRep;

    protected Parameter[] parameters;

    protected Point2D[] inputsPos;

    protected Point2D[] outputsPos;

    protected JLabel inputLabel;

    protected JLabel outputLabel;
    
    protected JLabel classLabel;

    
    /**
     * This constructor resets <code>graphicalRep</code>, uses 
     * <code>getParameters()</code> to get the parameters and finally calls
     * <code>updateGraphicalRep()</code>.
     * 
     * @param representedBox
     */
    public DefaultBoxRepresentation(IBox representedBox) {
        box = representedBox;
        
        graphicalRep = new BoxRepresentation(this);

        parameters = box.getParameters();

        inputsPos = new Point2D[] { new Point2D.Double(0.0d, 0.5d)};
        outputsPos = new Point2D[] { new Point2D.Double(1.0d, 0.5d)};

        updateGraphicalRep();
    }

    
    /**
     * Default implementation; uses 
     * <code>createStandardPanel(BoxRepresentation)</code>
     * 
     * @see quoggles.representation.IBoxRepresentation#updateGraphicalRep()
     * @see createStandardPanel(BoxRepresentation)
     */
    public void updateGraphicalRep() {
        graphicalRep.removeAll();
        graphicalRep = createStandardPanel(graphicalRep);
    }

    /**
     * According to the value of the second parameter tells the box about the
     * change or not.
     * Calls <code>updateInputOutput()</code>.
     */
    public void setParameters(Parameter[] params, boolean fromBox) {
        parameters = params;
        if (!fromBox) {
            box.setParameters(params, true);
        }
        updateInputOutput();
    }

    //    /**
    //     * Same as calling <code>setParameters(params, false)</code>.
    //     */
    //    public void setParameters(IParameter[] params) {
    //        setParameters(params, false);
    //        updateInputOutput();
    //    }

    public final BoxRepresentation createStandardPanel
        (BoxRepresentation boxRep) {

        JPanel pan = new JPanel();
        pan.setPreferredSize(new Dimension(0, 0));
        pan.setOpaque(false);
        return standardizeBoxRep(boxRep, pan);
    }

    /**
     * Makes all boxes look similar: Adds input and output specifications, sets
     * background color, sizes, etc. Adds small components representing input
     * and output places.
     * The given BoxRepresentation is cleared, modified and returned.
     * 
     * @param panel BoxRepresentation that will be enriched with content
     * @param boxContents the contents that will appear in the center of the 
     * box
     * @return the (modified) panel
     */
    public final BoxRepresentation standardizeBoxRep
        (BoxRepresentation panel, JComponent boxContents) {

//        StringBuffer ioList = new StringBuffer();
        int nrInputs = box.getNumberOfInputs();
//        if (nrInputs > 1) {
//            ioList.append("<html>" + INPUTLABEL);
//        } else if (nrInputs > 0){
//            ioList.append(INPUTLABEL);
//        }
//
//        for (int i = 0; i < nrInputs -1; i++) {
//            ioList.append(ITypeConstants.intStringMap.get(
//                new Integer(box.getInputTypes()[i])));
//            ioList.append("//<p style=\"margin-left:15pt\">");
//        }
//        if (nrInputs >= 1) {
//            ioList.append(ITypeConstants.intStringMap.get(
//                new Integer(box.getInputTypes()[nrInputs -1])));
//        }
//        if (nrInputs > 1) {
//            ioList.append("</html>");
//        }
        
        inputLabel = new JLabel();
//        inputLabel.setText(ioList.toString());
        inputLabel.setHorizontalAlignment(SwingConstants.CENTER);
        inputLabel.setAlignmentX(0.5f);

//        ioList = new StringBuffer();
        int nrOutputs = box.getNumberOfOutputs();
//        if (nrOutputs > 1) {
//            ioList.append("<html>" + OUTPUTLABEL);
//        } else if (nrOutputs > 0){
//            ioList.append(OUTPUTLABEL);
//        }
//        for (int i = 0; i < nrOutputs -1; i++) {
//            ioList.append(ITypeConstants.intStringMap.get(
//                new Integer(box.getOutputTypes()[i])));
//            ioList.append("//<p style=\"margin-left:15pt\">");
//        }
//        if (nrOutputs >= 1) {
//            ioList.append(ITypeConstants.intStringMap.get(
//                new Integer(box.getOutputTypes()[nrOutputs -1])));
//        }
//        if (nrOutputs > 1) {
//            ioList.append("</html>");
//        }

        outputLabel = new JLabel();
//        outputLabel.setText(ioList.toString());
        outputLabel.setHorizontalAlignment(SwingConstants.CENTER);
        outputLabel.setAlignmentX(0.5f);
        

        updateInputOutput();


        inputLabel.setSize(inputLabel.getPreferredSize());
        inputLabel.setPreferredSize(inputLabel.getPreferredSize());
        outputLabel.setSize(outputLabel.getPreferredSize());
        outputLabel.setPreferredSize(outputLabel.getPreferredSize());




        classLabel = new JLabel(getIBox().getId());
        classLabel.setHorizontalAlignment(SwingConstants.CENTER);
        classLabel.setSize(classLabel.getPreferredSize());
        classLabel.setForeground(IBoxConstants.CLASS_LABEL_COLOR);
        classLabel.setAlignmentX(0.5f);

        JPanel inputIdPanel = new JPanel();
        inputIdPanel.setLayout(new BoxLayout(inputIdPanel, BoxLayout.Y_AXIS));
        inputIdPanel.add(classLabel);
        inputIdPanel.add(inputLabel);
        Dimension size =
            new Dimension(
                Math.max(
                    inputLabel.getPreferredSize().width,
                    classLabel.getPreferredSize().width) + 10,
                inputLabel.getPreferredSize().height
                    + classLabel.getPreferredSize().height);
        inputIdPanel.setSize(size);
        inputIdPanel.setPreferredSize(size);
        inputIdPanel.setOpaque(false);

        JPanel boxPanel = new JPanel();
        boxPanel.setLayout(new BoxLayout(boxPanel, BoxLayout.Y_AXIS));
        boxPanel.add(inputIdPanel);
////        JPanel contentPanel = new JPanel();
////        contentPanel.setOpaque(false);
////        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        boxPanel.add(Box.createVerticalGlue());
        boxPanel.add(boxContents);
        boxPanel.add(Box.createVerticalGlue());
////        boxPanel.add(boxContents);
////        boxPanel.add(contentPanel, BorderLayout.CENTER);
        boxPanel.add(outputLabel);

        size = new Dimension(
            Math.min(
                IBoxConstants.MAX_BOX_WIDTH,
                Util.max(
                    boxContents.getPreferredSize().width,
                    outputLabel.getPreferredSize().width,
                    inputIdPanel.getPreferredSize().width) + 20),
            boxContents.getPreferredSize().height
                + outputLabel.getPreferredSize().height
                + inputIdPanel.getPreferredSize().height + 2);
        boxPanel.setSize(size);
        boxPanel.setPreferredSize(size);
        boxPanel.setBackground(IBoxConstants.BOX_BACKGROUND);
        boxPanel.setBorder(
            new InsetLineBorder(Color.BLACK, 2, new Insets(1, 10, 1, 10)));
        boxPanel.setOpaque(true);

//        DefaultIO input = new DefaultIO();
//        DefaultIO output = new DefaultIO();

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        for (int i = 0; i < nrInputs -1; i++) {
            inputPanel.add(new DefaultIO());
            inputPanel.add(Box.createVerticalGlue());
        }
        int iSize = 0;
        if (nrInputs >= 1) {
            DefaultIO pio = new DefaultIO();
            inputPanel.add(pio);
            iSize = pio.getPreferredSize().width;
        }
        inputPanel.setPreferredSize(new Dimension
            (iSize, boxPanel.getPreferredSize().height));
        
        JPanel outputPanel = new JPanel();
        outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.Y_AXIS));
        for (int i = 0; i < nrOutputs -1; i++) {
            outputPanel.add(new DefaultIO());
            outputPanel.add(Box.createVerticalGlue());
        }
        int oSize = 0;
        if (nrOutputs >= 1) {
            DefaultIO pio = new DefaultIO();
            outputPanel.add(pio);
            oSize = pio.getPreferredSize().width;
        }
        outputPanel.setPreferredSize(new Dimension
            (oSize, boxPanel.getPreferredSize().height));
        
        panel.removeAll();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        inputPanel.setOpaque(false);
        outputPanel.setOpaque(false);
        panel.add(inputPanel);
        panel.add(boxPanel);
        panel.add(outputPanel);
        panel.setOpaque(false);

        size = new Dimension(
            inputPanel.getPreferredSize().width
                + boxPanel.getPreferredSize().width
                + outputPanel.getPreferredSize().width,
            Util.max(
                inputPanel.getPreferredSize().height,
                boxPanel.getPreferredSize().height,
                outputPanel.getPreferredSize().height));
        panel.setSize(size);
        panel.setPreferredSize(size);

        return panel;
    }

    /**
     * @see quoggles.representation.IBoxRepresentation#updateId()
     */
    public void updateId() {
        if (classLabel != null) {
            classLabel.setText(getIBox().getId());
        }
    }
    
    /**
     * After a change in the parameters, the input / output type may have
     * changed. This method takes care of changing the labels in the box.
     */
    protected void updateInputOutput() {
        if (inputLabel != null) {
            StringBuffer ioList = new StringBuffer();
            int nrInputs = box.getNumberOfInputs();
            if (nrInputs > 1) {
                ioList.append("<html>" + INPUTLABEL);
            } else if (nrInputs == 1) {
                ioList.append(INPUTLABEL);
            }

            for (int i = 0; i < nrInputs -1; i++) {
                ioList.append(ITypeConstants.intStringMap.get(
                    new Integer(box.getInputTypes()[i])));
                ioList.append("//<p style=\"margin-left:15pt\">");
            }
            if (nrInputs >= 1) {
                ioList.append(ITypeConstants.intStringMap.get(
                    new Integer(box.getInputTypes()[nrInputs -1])));
            }
            if (nrInputs > 1) {
                ioList.append("</html>");
            }

            inputLabel.setText(ioList.toString());
        }
        if (outputLabel != null) {
            StringBuffer ioList = new StringBuffer();
            int nrOutputs = box.getNumberOfOutputs();
            if (nrOutputs > 1) {
                ioList.append("<html>" + OUTPUTLABEL);
            } else if (nrOutputs == 1) {
                ioList.append(OUTPUTLABEL);
            }
            for (int i = 0; i < nrOutputs -1; i++) {
                ioList.append(ITypeConstants.intStringMap.get(
                    new Integer(box.getOutputTypes()[i])));
                ioList.append("//<p style=\"margin-left:15pt\">");
            }
            if (nrOutputs >= 1) {
                ioList.append(ITypeConstants.intStringMap.get(
                    new Integer(box.getOutputTypes()[nrOutputs -1])));
            }
            if (nrOutputs > 1) {
                ioList.append("</html>");
            }

            outputLabel.setText(ioList.toString());
        }
    }

    /**
     * @see quoggles.representation.IBoxRepresentation#getRepresentation()
     */
    public BoxRepresentation getRepresentation() {
        if (graphicalRep == null) {
            throw new RuntimeException("DefaultBoxRepresentation (" +
                this.getClass().getName() + "): graphicalRep must never be"
                + " null.");
//            graphicalRep = createStandardPanel();
        }
        return graphicalRep;
    }

    /**
     * @see quoggles.representation.IBoxRepresentation#getIBox()
     */
    public IBox getIBox() {
        return box;
    }

    /**
     * @see quoggles.representation.IBoxRepresentation#getOtherRelOutputPos()
     */
    public Point2D[] getRelOutputsPos() {
        int nrOutputs = getIBox().getNumberOfOutputs();
        if (nrOutputs == 0) {
            return null;
        } else
        if (nrOutputs == 1) {
            return outputsPos;
        } else {
            Point2D[] outputs = new Point2D[nrOutputs];
            outputs[0] = new Point2D.Double(1d, 0.01d);
            outputs[nrOutputs-1] = new Point2D.Double(1d, 0.98d);
            for (int i = 1; i < nrOutputs - 1; i++) {
                outputs[i] = new Point2D.Double(1d, i * 0.97d / (nrOutputs-1d) + 0.01);
            }
            return outputs;
        }
    }

    /**
     * @see quoggles.representation.IBoxRepresentation#getOtherRelInputPos()
     */
    public Point2D[] getRelInputsPos() {
        int nrInputs = getIBox().getNumberOfInputs();
        if (nrInputs == 0) {
            return null;
        } else
        if (nrInputs == 1) {
            return inputsPos;
        } else {
            Point2D[] inputs = new Point2D[nrInputs];
            inputs[0] = new Point2D.Double(0d, 0.01d);
            inputs[nrInputs-1] = new Point2D.Double(0d, 0.98d);
            for (int i = 1; i < nrInputs - 1; i++) {
                inputs[i] = new Point2D.Double(0d, i * 0.97d / (nrInputs-1d) + 0.01);
            }
            return inputs;
        }
    }
    
    /**
     * Used to make the system repaint the box representation, for example
     * after the number of inputs / outputs has changed.
     * 
     * @param e an event whose data is used to generate other events
     */
    public final void repaintBoxRep(ActionEvent e) {
        getRepresentation().getParent().getMouseListeners()[0]
            .mousePressed(new MouseEvent(getRepresentation().getParent(), 
                e.getID(), e.getWhen(), InputEvent.BUTTON1_DOWN_MASK, 
                getRepresentation().getX() + 10, 
                getRepresentation().getY() + 10, 1, false));
        getRepresentation().getParent().getMouseMotionListeners()[0]
            .mouseDragged(new MouseEvent(getRepresentation().getParent(), 
                e.getID(), e.getWhen(), InputEvent.BUTTON1_DOWN_MASK, 
                getRepresentation().getX() + 10, 
                getRepresentation().getY() + 10, 1, false));
        getRepresentation().getParent().getMouseListeners()[0]
            .mouseReleased(new MouseEvent(getRepresentation().getParent(), 
                e.getID(), e.getWhen(), InputEvent.BUTTON1_DOWN_MASK, 
                getRepresentation().getX() + 10, 
                getRepresentation().getY() + 10, 1, false));
    }

}
