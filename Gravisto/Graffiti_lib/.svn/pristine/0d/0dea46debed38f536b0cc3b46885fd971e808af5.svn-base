package quoggles.stdboxes.output;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import quoggles.auxiliary.InsetLineBorder;
import quoggles.boxes.IBox;
import quoggles.constants.IBoxConstants;
import quoggles.constants.ITypeConstants;
import quoggles.representation.DefaultBoxRepresentation;
import quoggles.representation.DefaultIO;

/**
 * Represents the box.
 */
public class BoolPredicateEnd_Rep extends DefaultBoxRepresentation {

    /**
     * @param representedBox
     */
    public BoolPredicateEnd_Rep(IBox representedBox) {
        super(representedBox);
        outputsPos = new Point[]{ };
    }


    /**
     * @see quoggles.representation.IBoxRepresentation#updateGraphicalRep()
     */
    public void updateGraphicalRep() {
        graphicalRep.removeAll();

        inputLabel = new JLabel(INPUTLABEL + ITypeConstants.intStringMap.get(
            new Integer(box.getInputTypes()[0])));
        inputLabel.setHorizontalAlignment(SwingConstants.CENTER);
        inputLabel.setSize(inputLabel.getPreferredSize());
        inputLabel.setPreferredSize(inputLabel.getPreferredSize());
        inputLabel.setAlignmentX(0.5f);

        classLabel = new JLabel(getIBox().getId());
        classLabel.setHorizontalAlignment(SwingConstants.CENTER);
        classLabel.setSize(classLabel.getPreferredSize());
        classLabel.setPreferredSize(classLabel.getPreferredSize());
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
                    + classLabel.getPreferredSize().height + 3);
        inputIdPanel.setSize(size);
        inputIdPanel.setPreferredSize(size);
        inputIdPanel.setOpaque(true);
        inputIdPanel.setBorder(
            new InsetLineBorder(Color.BLACK, 2, new Insets(1, 10, 1, 10)));

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

        DefaultIO pio = new DefaultIO();
        inputPanel.add(pio);
        int iSize = pio.getPreferredSize().width;
        inputPanel.setPreferredSize(new Dimension
            (iSize, inputIdPanel.getPreferredSize().height));
        inputPanel.setOpaque(false);
        
        graphicalRep.removeAll();
        graphicalRep.setLayout(new BoxLayout(graphicalRep, BoxLayout.X_AXIS));
        graphicalRep.add(inputPanel);
        graphicalRep.add(inputIdPanel);
        graphicalRep.setOpaque(false);

        size = new Dimension(
            inputPanel.getPreferredSize().width
                + inputIdPanel.getPreferredSize().width + 15,
            Math.max(
                inputPanel.getPreferredSize().height,
                inputIdPanel.getPreferredSize().height));
        graphicalRep.setSize(size);
        graphicalRep.setPreferredSize(size);
    }
}
