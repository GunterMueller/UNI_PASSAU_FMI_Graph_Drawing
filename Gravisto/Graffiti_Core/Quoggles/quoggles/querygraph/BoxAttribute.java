/*
 * 
 */
package quoggles.querygraph;

import org.graffiti.attributes.AbstractAttribute;
import org.graffiti.attributes.IllegalIdException;
import org.graffiti.event.AttributeEvent;
import org.graffiti.plugin.XMLHelper;
import org.graffiti.plugin.parameter.Parameter;

import quoggles.boxes.IBox;
import quoggles.representation.BoxRepresentation;
import quoggles.stdboxes.connectors.OneOneConnector_Box;
import quoggles.stdboxes.connectors.OneOneConnector_Rep;
import quoggles.stdboxes.input.Input_Box;

/**
 *
 */
public class BoxAttribute extends AbstractAttribute {

    private IBox value;
    
    /**
     * @param id
     * @throws IllegalIdException
     */
    public BoxAttribute(String id) throws IllegalIdException {
        super(id);
    }

    /**
     * @param id
     * @param iBox
     * @throws IllegalIdException
     */
    public BoxAttribute(String id, IBox iBox) throws IllegalIdException {
        super(id);
        value = iBox;
    }

    /**
     * @see org.graffiti.attributes.AbstractAttribute#doSetValue(java.lang.Object)
     */
    protected void doSetValue(Object val) throws IllegalArgumentException {
        try {
            value = (IBox)val;
        } catch(ClassCastException cce) {
            throw new IllegalArgumentException("Invalid value type.");
        }
    }

    /**
     * 
     * @param iBox
     */
    public void setIBox(IBox iBox) {
        AttributeEvent ae = new AttributeEvent(this);
        callPreAttributeChanged(ae);
        this.value = iBox;
        callPostAttributeChanged(ae);
    }
    
    /**
     * 
     * @return
     */
    public IBox getIBox() {
        return value;
    }
    
    /**
     * @see org.graffiti.attributes.Attribute#setDefaultValue()
     */
    public void setDefaultValue() {
        value = null;
    }

    /**
     * @see org.graffiti.plugin.Displayable#getValue()
     */
    public Object getValue() {
        return value;
    }

    /**
     * @see org.graffiti.core.DeepCopy#copy()
     */
    public Object copy() {
        return new BoxAttribute(this.getId(), this.value);
    }

    /**
     * @see org.graffiti.attributes.Attribute#toString(int)
     */
    public String toString(int n)
    {
        return getSpaces(n) + getId() + " = \"" + value + "\"";
    }

    /**
     * @see org.graffiti.plugin.Displayable#toXMLString()
     */
    public String toXMLString() {
        StringBuffer valString = new StringBuffer();

        valString.append("<box classname=\\\"" + value.getClass().getName());

        if (value instanceof Input_Box) {
            valString.append("\\\" name=\\\"" + ((Input_Box)value).getBoxName());
        }

        valString.append("\\\">" + XMLHelper.getDelimiter());

        Parameter[] params = value.getParameters();
        if (params.length > 0) {
            valString.append(XMLHelper.spc(6) + "<parameters>" +
                XMLHelper.getDelimiter());
        }
        
        for (int i = 0; i < params.length; i++) {
            if (params[i] != null) {
                valString.append(XMLHelper.spc(8) +  params[i].toXMLString() +
                    XMLHelper.getDelimiter());
            }
        }
        if (params.length > 0) {
            valString.append(XMLHelper.spc(6) + "</parameters>" +
                XMLHelper.getDelimiter());
        }
    
        BoxRepresentation boxRep = value.getGraphicalRepresentation()
            .getRepresentation();
        valString.append(XMLHelper.spc(2) + "<geometry x=\\\"" + 
            boxRep.getX() + "\\\" y=\\\"" +
            boxRep.getY() + "\\\" w=\\\"" + boxRep.getWidth() + "\\\" h=\\\"" +
            boxRep.getHeight());
        if (value instanceof OneOneConnector_Box) {
            valString.append("\\\" fromTopLeft=\\\"" + 
                (((OneOneConnector_Rep.MyBoxRepresentation) 
                    value.getGraphicalRepresentation().getRepresentation())
                        .getDrawFromTL() ? "true" : "false"));
        }    
        valString.append("\\\"/>" + XMLHelper.getDelimiter());

        valString.append(XMLHelper.spc(4) + "</box>");
        
        return getStandardXML(valString.toString());
    }
}
