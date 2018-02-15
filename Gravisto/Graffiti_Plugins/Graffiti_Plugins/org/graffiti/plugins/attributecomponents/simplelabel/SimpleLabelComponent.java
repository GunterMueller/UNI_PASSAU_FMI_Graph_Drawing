// =============================================================================
//
//   SimpleLabelComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: SimpleLabelComponent.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.attributecomponents.simplelabel;

import org.graffiti.graphics.GraphicAttributeConstants;

/**
 * This component represents a label for a node or an edge. It is displayed via
 * a JTextField.
 * 
 * @version $Revision: 5766 $
 */
public class SimpleLabelComponent extends LabelComponent implements
        GraphicAttributeConstants {

    // /** The <code>TextField</code> used to display the label. */
    // private JTextField textField;

    /**
     * 
     */
    private static final long serialVersionUID = -1208880928573528262L;

    /**
     * Instantiates an <code>LabelComponent</code>
     */
    public SimpleLabelComponent() {
        super();

        // done in recreate:
        // this.labelPanel = new JTextField(DEFAULT_WIDTH);
    }

    // /**
    // * Constructs a new <code>SimpleLabelComponent</code>
    // *
    // * @param shift specifies the shift needed to calculate the absolute
    // * coordinates from the relative ones this component uses.
    // */
    // public SimpleLabelComponent(Point shift) {
    // super(shift);
    // }
    // /**
    // * Called when a graphics attribute of the attribute represented by this
    // * component has changed.
    // *
    // * @param attr the attribute that has triggered the event.
    // */
    // public void attributeChanged(Attribute attr) throws
    // ShapeNotFoundException {
    // if (attr.getId().equals(POSITION)) {
    //            
    // repaint();
    // } else if (attr.getId().equals(TEXTCOLOR)) {
    //
    // repaint();
    // } else {
    // this.recreate();
    // }
    // }
    // /**
    // * Returns a graphic component for label of graph elements
    // *
    // * @return
    // */
    // protected JPanel getPanelForLabel() {
    // JPanel panel = new JPanel();
    // String labelText = this.labelAttr.getLabel();
    // textField = new JTextField
    // (labelText, labelText.length());
    // // TODO: might be editable and has to change labelAttribute
    // textField.setEditable(false);
    // textField.setHorizontalAlignment(JTextField.CENTER);
    // textField.setBorder(BorderFactory.createEmptyBorder());
    // textField.setSelectionColor(null);
    //		
    // // TODO: check the +2
    // textField.setSize((int)textField.getPreferredSize().getWidth()+2,
    // (int)textField.getPreferredSize().getHeight());
    // textField.setBackground(new Color(0f, 0f, 0f, 0f));
    // textField.setForeground
    // (this.labelAttr.getTextcolor().getColor());
    // panel.add(textField);
    // panel.setSize(textField.getSize());
    // panel.setPreferredSize(textField.getPreferredSize());
    // return panel;
    // }
    // like this already implemented in AttributeComponent
    // /**
    // * Paints the node contained in this component.
    // *
    // * @see javax.swing.JComponent#paintComponent(Graphics)
    // */
    // public void paintComponent(Graphics g) {
    // super.paintComponent(g);
    // // empty
    // }
    // hmmmmm
    // public boolean contains(int x, int y) {
    // return this.textField.contains(x, y);
    // }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
