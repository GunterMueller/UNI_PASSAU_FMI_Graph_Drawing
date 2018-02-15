package quoggles.changeboxes.changeattributes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

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
 * Input: <code>List</code> of <code>Attributables</code><p>
 * <code>Collection</code> of values<p>
 * Output: the same <code>List</code> of <code>Attributables</code>
 * where an attribute of each of them has been assigned the corresponding
 * value from the second input
 */
public class ChangeAttributes_Box extends Box {

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
            
    private Collection attblsList;

    private Collection attrValuesList;
    
    
    /**
     *
     */
    public ChangeAttributes_Box() {
        parameters = new Parameter[]{ attrType, attrPath };
    }
    
    
    /**
     * @see quoggles.boxes.IBox#execute()
     */
    public void execute() throws QueryExecutionException {
        super.execute();
        
        if (attblsList.size() != attrValuesList.size()) {
            throw new InvalidInputException(getId() +
                " needs two Lists of equal size as input.");
        }
        
        String path = ((StringParameter)parameters[1]).getString();
        int lastSepPos = path.lastIndexOf(Attribute.SEPARATOR);
        String id = path;
        if (lastSepPos < 0) {
            path = "";
        } else {
            id = path.substring(lastSepPos + 1);
            path = path.substring(0, lastSepPos);
        }
        
        Iterator attblIt = attblsList.iterator();
        for (Iterator it = attrValuesList.iterator(); it.hasNext();) {
            Object obj = it.next();
            try {
                Attributable attbl = (Attributable)attblIt.next();
                String type = 
                    ((OptionParameter)parameters[0]).getValue().toString();
                Attribute attr = Util.getFreshAttribute(id, obj, attbl, type);
                try {
                    attbl.addAttribute(attr, path);
                } catch (AttributeExistsException aee) {
//                    attbl.removeAttribute(path + Attribute.SEPARATOR + id);
//                    attbl.addAttribute(attr, path);
                    Attribute theAttr = attbl.getAttribute(path + Attribute.SEPARATOR + id);
                    if (theAttr instanceof StringAttribute) {
                        theAttr.setValue(obj.toString());
                    } else if (theAttr instanceof IntegerAttribute && 
                            !(obj instanceof Integer)) {
                        theAttr.setValue(new Integer(obj.toString()));
                    } else if (theAttr instanceof DoubleAttribute && 
                            !(obj instanceof Double)) {
                        theAttr.setValue(new Double(obj.toString()));
                    } else {
                        theAttr.setValue(obj);
                    }
                }
            } catch (ClassCastException cce) {
                throw new InvalidInputException(getId() +
                    ": First input must be a List of Attributables.");
            } catch (Exception e) {
                throw new QueryExecutionException(getId() +
                    ": Could not change attribute: " + e);
            }
        }
        
//        if (attblsList.size() == 1) {
//            outputs = new Attributable[]{ (Attributable)attblsList.get(0) };
//        } else {
            outputs = new Collection[] { attblsList };
//        }
    }

    /**
     * @see quoggles.boxes.IBox#setInputs(java.lang.Object[])
     */
    public void setInputs(Object[] inputs) throws InvalidInputException {
        super.setInputs(inputs);
        
        if (inputs[0] instanceof Collection) {
            attblsList = (Collection)inputs[0];
        } else {
            attblsList = new ArrayList(1);
            attblsList.add(inputs[0]);
        }
        if (inputs[1] instanceof Collection) {
            attrValuesList = (Collection)inputs[1];
        } else {
            attrValuesList = new ArrayList(1);
            attrValuesList.add(inputs[1]);
        }
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
            ITypeConstants.ATTRIBUTABLES, ITypeConstants.GENERAL };
    }

    /**
     * @see quoggles.boxes.IBox#getOutputTypes()
     */
    public int[] getOutputTypes() {
        return new int[] { ITypeConstants.ATTRIBUTABLES };
    }

    /**
     * @see quoggles.boxes.IBox#getGraphicalRepresentation()
     */
    public IBoxRepresentation getGraphicalRepresentation() {
        if (iBoxGRep == null || 
            !(iBoxGRep instanceof ChangeAttributes_Rep)) {

            iBoxGRep = new ChangeAttributes_Rep(this);
        }

        return iBoxGRep;
    }

}
