package quoggles.stdboxes.output;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.graffiti.plugin.parameter.Parameter;

import quoggles.QAssign;
import quoggles.auxiliary.InsetLineBorder;
import quoggles.boxes.IBox;
import quoggles.constants.IBoxConstants;
import quoggles.parameters.OptionParameter;
import quoggles.representation.DefaultBoxRepresentation;
import quoggles.representation.DefaultIO;

/**
 * Represents a normal output box.
 */
public class NormalOutput_Rep 
    extends DefaultBoxRepresentation 
    implements ActionListener {

    private JComboBox outPosCombo;

    private Point2D inputPos = new Point2D.Double(0.5, 0.0);
        
    private Point2D outputPos = new Point2D.Double(0.5, 0.0);

        
    /**
     * Construct representation (IO etc).
     * 
     * @param representedBox
     */
    public NormalOutput_Rep(IBox representedBox) {
        super(representedBox);
    }


    /**
     * @see quoggles.representation.IBoxRepresentation#updateGraphicalRep()
     */
    public void updateGraphicalRep() {
        graphicalRep.removeAll();
        
        graphicalRep.setOpaque(false);
        graphicalRep.setLayout(new BoxLayout(graphicalRep, BoxLayout.Y_AXIS));
//            Dimension size = new Dimension(70, 130);
//            setSize(size);
//            setPreferredSize(size);
//            setMaximumSize(size);
//            setMinimumSize(size);
    
        JPanel comboPanel = new JPanel();
        comboPanel.setOpaque(true);
        comboPanel.setLayout(new BoxLayout(comboPanel, BoxLayout.Y_AXIS));
        
        outPosCombo = new JComboBox(IBoxConstants.ROWNUMBERS);
        outPosCombo.setBackground(IBoxConstants.PARAM_BACKGROUND);
        if (parameters.length == 0) {
            outPosCombo.setEditable(false);
            outPosCombo.setSelectedIndex(0);
        } else {
            outPosCombo.setEditable(
                ((OptionParameter)parameters[0]).isEditable());
            outPosCombo.setSelectedItem(
                ((OptionParameter)parameters[0]).getValue());
        }
        outPosCombo.addActionListener(this);
    
        comboPanel.setBackground(IBoxConstants.BOX_BACKGROUND);
        comboPanel.setBorder(new InsetLineBorder(Color.BLACK, 2));
        
        classLabel = new JLabel(getIBox().getId());
        classLabel.setForeground(IBoxConstants.CLASS_LABEL_COLOR);
        classLabel.setHorizontalAlignment(SwingConstants.CENTER);
        classLabel.setSize(classLabel.getPreferredSize());
        classLabel.setAlignmentX(0.5f);
            
        comboPanel.add(classLabel);
        comboPanel.add(Box.createVerticalGlue());
        comboPanel.add(outPosCombo);
        
        DefaultIO input = new DefaultIO();
        Dimension size = new Dimension
            (IBoxConstants.IO_THICKNESS, 
             IBoxConstants.DEFAULT_OUTPUTBOX_IO_HEIGHT);
//                (int)(getPreferredSize().getHeight() - 
//                    label.getPreferredSize().getHeight()));
        input.setSize(size);
        input.setMinimumSize(size);
        input.setMaximumSize(size);
        input.setPreferredSize(size);
        graphicalRep.add(input);
        graphicalRep.add(comboPanel);
    
        size = new Dimension(comboPanel.getPreferredSize().width,
            size.height + comboPanel.getPreferredSize().height);
        graphicalRep.setPreferredSize(size);
        graphicalRep.setSize(size);
    }

    /**
     * Top center.
     * 
     * @see quoggles.representation.IBoxRepresentation
     * #getOtherRelOutputPos()
     */
    public Point2D[] getRelOutputsPos() {
        return new Point2D[]{ outputPos };
    }

    /**
     * Top center.
     * 
     * @see quoggles.representation.IBoxRepresentation
     * #getOtherRelInputPos()
     */
    public Point2D[] getRelInputsPos() {
        return new Point2D[]{ inputPos };
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        int oldPos = ((OptionParameter)parameters[0]).getOptionNr();
        QAssign.assignRow(oldPos, false);

        int newPos = outPosCombo.getSelectedIndex();
        if (QAssign.getRowAssignment(newPos)) {
            // already assigned by some other output box
            newPos = QAssign.getNextFreeRowNumber();
            outPosCombo.setSelectedIndex(newPos);
        }
                
        QAssign.assignRow(newPos, true);
        ((OptionParameter)parameters[0])
            .setValue(outPosCombo.getSelectedItem());
        updateInputOutput();
    }

    /**
     * @see quoggles.representation.IBoxRepresentation#setParameters(org.graffiti.plugin.parameter.Parameter[], boolean)
     */
    public void setParameters(Parameter[] params, boolean fromBox) {
        super.setParameters(params, fromBox);

//        if (!fromBox) {
            int newPos = ((OptionParameter)parameters[0]).getOptionNr();
            int oldPos = ((NormalOutput_Box)getIBox()).getLastRowNumber();
            if (newPos != oldPos) {
                // row number changed; reflect changes
                if (oldPos != -1) {
                    QAssign.assignRow(oldPos, false);
                }
                if (QAssign.getRowAssignment(newPos)) {
                    // already assigned by some other output box
                    newPos = QAssign.getNextFreeRowNumber();
//                    if (oldPos != newPos) {
//                        outPosCombo.setSelectedIndex(newPos);
//                    }
                }
                        
                if (oldPos != newPos) {
                    ((OptionParameter)parameters[0]).setValue(
                        ((OptionParameter)parameters[0]).getOptions()[newPos]);
                    outPosCombo.setSelectedIndex(newPos);
                    QAssign.assignRow(newPos, true);
                }
            }
            ((NormalOutput_Box)getIBox()).setLastRowNumber(newPos);
//        } else {
//            int newPos = ((OptionParameter)parameters[0]).getOptionNr();
//            //outPosCombo.setSelectedIndex(newPos);
//            ((NormalOutput_Box)getIBox()).setLastRowNumber(newPos);
//        }
    }

}
