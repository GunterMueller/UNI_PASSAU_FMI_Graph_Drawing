package quoggles.changeboxes.changeattribute;

import org.graffiti.attributes.Attributable;
import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeExistsException;
import org.graffiti.attributes.DoubleAttribute;
import org.graffiti.attributes.IntegerAttribute;
import org.graffiti.attributes.StringAttribute;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringParameter;

import quoggles.auxiliary.Util;
import quoggles.boxes.Box;
import quoggles.constants.IBoxConstants;
import quoggles.constants.ITypeConstants;
import quoggles.exceptions.InvalidInputException;
import quoggles.exceptions.QueryExecutionException;
import quoggles.parameters.OptionParameter;
import quoggles.representation.IBoxRepresentation;

/**
 * Input: One <code>Attributable</code>.<p>
 * Output: The input <code>Attributable</code> where the specified attribute
 * has been assigned the value from the second input
 */
public class ChangeAttribute_Box extends Box {

    private Object[] types = new String[]
        { ITypeConstants.LABEL_STR,
          ITypeConstants.STRING_STR, ITypeConstants.INTEGER_STR,
          ITypeConstants.DOUBLE_STR, ITypeConstants.BOOLEAN_STR,
          ITypeConstants.OBJECT_STR, ITypeConstants.COLLECTION_STR };
    
    private OptionParameter attrType = 
        new OptionParameter(types, 0, 
            IBoxConstants.ATTR_TYPE, 
            "The type of the attribute to add");
        
    private StringParameter attrPath = 
        new StringParameter("label",
            IBoxConstants.ATTR_PATH, 
            "The path and id of the (new) attribute");
            
    private Attributable attributable;

    private Object attrValue;
    
    
    /**
     *
     */
    public ChangeAttribute_Box() {
        parameters = new Parameter[]{ attrType, attrPath };
    }
    
   
    /**
     * @see quoggles.boxes.IBox#execute()
     */
    public void execute() throws QueryExecutionException {
        super.execute();
        
        String path = ((StringParameter)parameters[1]).getString();
        int lastSepPos = path.lastIndexOf(Attribute.SEPARATOR);
        String id = path;
        if (lastSepPos < 0) {
            path = "";
        } else {
            id = path.substring(lastSepPos + 1);
            path = path.substring(0, lastSepPos);
        }
        
        try {
            String type = 
                ((OptionParameter)parameters[0]).getValue().toString();
            Attribute attr = 
                Util.getFreshAttribute(id, attrValue, attributable, type);
            try {
                attributable.addAttribute(attr, path);
            } catch (AttributeExistsException aee) {
                    Attribute theAttr = attributable
                        .getAttribute(path + Attribute.SEPARATOR + id);
                    if (theAttr instanceof StringAttribute) {
                        theAttr.setValue(attrValue.toString());
                    } else if (theAttr instanceof IntegerAttribute && 
                            !(attrValue instanceof Integer)) {
                        theAttr.setValue(new Integer(attrValue.toString()));
                    } else if (theAttr instanceof DoubleAttribute && 
                            !(attrValue instanceof Double)) {
                        theAttr.setValue(new Double(attrValue.toString()));
                    } else {
                        theAttr.setValue(attrValue);
                    }
//                attributable.getAttribute(path + Attribute.SEPARATOR + id)
//                    .setValue(attrValue);
            }
        } catch (Exception e) {
            throw new QueryExecutionException(getId() + ": " + e);
        }
        
        outputs = new Attributable[] { attributable };
    }

    /**
     * @see quoggles.boxes.IBox#setInputs(java.lang.Object[])
     */
    public void setInputs(Object[] inputs) throws InvalidInputException {
        super.setInputs(inputs);
        
        if (inputs[0] instanceof Attributable) {
            attributable = (Attributable)inputs[0];
        } else {
            throw new InvalidInputException(getId() +
                " needs an Attributable as first input.");
        }
        
        attrValue = inputs[1];
    }

    /**
     * @see quoggles.boxes.IBox#getNumberOfInputs()
     */
    public int getNumberOfInputs() {
        return 2;
    }

    /**
     * @see quoggles.boxes.IBox#getInputTypes()
     */
    public int[] getInputTypes() {
        return new int[] { 
            ITypeConstants.ATTRIBUTABLE, ITypeConstants.GENERAL };
    }

    /**
     * @see quoggles.boxes.IBox#getOutputTypes()
     */
    public int[] getOutputTypes() {
        return new int[] { ITypeConstants.ATTRIBUTABLE };
    }

    /**
     * @see quoggles.boxes.IBox#getGraphicalRepresentation()
     */
    public IBoxRepresentation getGraphicalRepresentation() {
        if (iBoxGRep == null || 
            !(iBoxGRep instanceof ChangeAttribute_Rep)) {

            iBoxGRep = new ChangeAttribute_Rep(this);
        }

        return iBoxGRep;
    }

}
