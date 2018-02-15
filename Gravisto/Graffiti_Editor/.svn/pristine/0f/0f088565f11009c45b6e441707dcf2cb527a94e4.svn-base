package quoggles.stdboxes.complexfilter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;

import quoggles.boxes.IBox;
import quoggles.representation.DefaultBoxRepresentation;

/**
 *
 */
public class ComplexFilter_Rep extends DefaultBoxRepresentation 
    implements ActionListener, FocusListener {

//    private Point2D output1 = new Point2D.Double(1d, 0.5d);
//
//    private Point2D output2 = new Point2D.Double(0.5d, 1d);

    private JTextField orderText;


    public ComplexFilter_Rep(IBox representedBox) {
        super(representedBox);
    }

//    /**
//     * @see quoggles.representation.IBoxRepresentation
//     * #getOtherRelOutputPos()
//     */
//    public Point2D[] getRelOutputsPos() {
//        return new Point2D[]{ output1, output2 };
//    }

    /**
     * @see quoggles.representation.IBoxRepresentation#updateGraphicalRep()
     */
    public void updateGraphicalRep() {        
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JLabel label = new JLabel("<html>Predicate<p>(lower<p>output)<p>" +
            "is tested<p>for each<p>input<p>element</html>");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        
        orderText = new JTextField(parameters[0].getValue().toString());
        orderText.addActionListener(this);
        orderText.addFocusListener(this);
        panel.add(orderText, BorderLayout.SOUTH);

        panel.add(label, BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(10, 150));
        panel.setOpaque(false);

        graphicalRep = standardizeBoxRep(graphicalRep, panel);
        graphicalRep.validate();
    }
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        ((IntegerParameter)parameters[0])
            .setValue(new Integer(orderText.getText()));
        
        updateInputOutput();
    }

    /**
     * @see quoggles.representation.IBoxRepresentation#
     * setParameters(org.graffiti.plugin.parameter.Parameter[], boolean)
     */
    public void setParameters(Parameter[] params, boolean fromBox) {
        super.setParameters(params, fromBox);
        orderText.setText(
            ((IntegerParameter)parameters[0]).getValue().toString());
    }

    /**
     * Empty.
     * 
     * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
     */
    public void focusGained(FocusEvent e) { }

    /**
     * Similar to <code>actionPerformed</code>. Shows a possible change
     * in a parameter. Needed for text areas. 
     * 
     * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
     */
    public void focusLost(FocusEvent e) {
        Object src = e.getSource();
        if (orderText.equals(src)) {
            ((IntegerParameter)parameters[0])
                .setValue(new Integer(orderText.getText()));
        }
        
        updateInputOutput();
    }


//    class ComplexFilterRepresentation extends BoxRepresentation {
//    
//        /**
//         * Construct graphical representation.
//         */
//        public ComplexFilterRepresentation(IBoxRepresentation iBoxRep) {
//            super(iBoxRep);
//            
//            StringBuffer ioList = new StringBuffer();
//            ioList.append(INPUTLABEL);
//            ioList.append(ITypeConstants.intStringMap.get(
//                new Integer(box.getInputTypes()[0])));
//        
//            inputLabel = new JLabel(ioList.toString());
//            inputLabel.setHorizontalAlignment(SwingConstants.CENTER);
//            inputLabel.setSize(inputLabel.getPreferredSize());
//            inputLabel.setAlignmentX(0.5f);
//
//            ioList = new StringBuffer();
//                ioList.append(OUTPUTLABEL);
//                ioList.append(ITypeConstants.intStringMap.get(
//                    new Integer(box.getOutputTypes()[0])));
//
//            outputLabel = new JLabel(ioList.toString());
//            outputLabel.setHorizontalAlignment(SwingConstants.CENTER);
//            outputLabel.setSize(outputLabel.getPreferredSize());
//            outputLabel.setAlignmentX(0.5f);
//
//            classLabel = new JLabel(getIBox().getId());
//            classLabel.setHorizontalAlignment(SwingConstants.CENTER);
//            classLabel.setSize(classLabel.getPreferredSize());
//            classLabel.setForeground(IBoxConstants.CLASS_LABEL_COLOR);
//            classLabel.setAlignmentX(0.5f);
//
//            JPanel inputIdPanel = new JPanel();
//            inputIdPanel.setLayout(new BoxLayout(inputIdPanel, BoxLayout.Y_AXIS));
//            inputIdPanel.add(classLabel);
//            inputIdPanel.add(inputLabel);
//            Dimension size = new Dimension(
//                Math.max(
//                    inputLabel.getPreferredSize().width,
//                    classLabel.getPreferredSize().width) + 10,
//                inputLabel.getPreferredSize().height
//                    + classLabel.getPreferredSize().height);
//            inputIdPanel.setSize(size);
//            inputIdPanel.setPreferredSize(size);
//            inputIdPanel.setOpaque(false);
//
//            JPanel boxPanel = new JPanel();
//            boxPanel.setLayout(new BoxLayout(boxPanel, BoxLayout.Y_AXIS));
//            boxPanel.add(inputIdPanel);
//            boxPanel.add(Box.createVerticalGlue());
//            boxPanel.add(outputLabel);
//
//            size = new Dimension(
//                Math.max(
//                    outputLabel.getPreferredSize().width,
//                        inputIdPanel.getPreferredSize().width) + 20,
//                    outputLabel.getPreferredSize().height
//                        + inputIdPanel.getPreferredSize().height + 2);
//
//            boxPanel.setSize(size);
//            boxPanel.setPreferredSize(size);
//            boxPanel.setBackground(IBoxConstants.BOX_BACKGROUND);
//            boxPanel.setBorder(
//                new InsetLineBorder(Color.BLACK, 2, new Insets(1, 10, 1, 10)));
//            boxPanel.setOpaque(false);
//
//            JPanel inputPanel = new JPanel();
//            inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
//            DefaultIO pio = new DefaultIO();
//            inputPanel.add(pio);
//
//            inputPanel.setPreferredSize(new Dimension
//                (pio.getPreferredSize().width, 
//                 boxPanel.getPreferredSize().height));
//        
//            JPanel outputPanel = new JPanel();
//            outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.Y_AXIS));
//            pio = new DefaultIO();
//            outputPanel.add(pio);
//            outputPanel.setPreferredSize(new Dimension
//                (pio.getPreferredSize().width, 
//                 boxPanel.getPreferredSize().height));
//        
//            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
//
//            inputPanel.setOpaque(false);
//            outputPanel.setOpaque(false);
//            add(inputPanel);
//            add(boxPanel);
//            add(outputPanel);
//            setOpaque(false);
//
//            size = new Dimension(
//                inputPanel.getPreferredSize().width
//                    + boxPanel.getPreferredSize().width
//                    + outputPanel.getPreferredSize().width,
//                Util.max(
//                    inputPanel.getPreferredSize().height,
//                    boxPanel.getPreferredSize().height,
//                    outputPanel.getPreferredSize().height, 0));
//            setSize(size);
//            setPreferredSize(size);
//        }
//
//    }
}
