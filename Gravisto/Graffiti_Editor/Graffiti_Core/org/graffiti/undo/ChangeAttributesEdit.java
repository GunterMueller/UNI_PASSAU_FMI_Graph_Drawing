// =============================================================================
//
//   ChangeAttributesEdit.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ChangeAttributesEdit.java 5779 2010-05-10 20:31:37Z gleissner $

package org.graffiti.undo;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.GraphElement;
import org.graffiti.util.logging.GlobalLoggerSetting;

/**
 * ChangeAttributesEdit
 * 
 * @author wirch
 * @version $Revision: 5779 $
 */
public class ChangeAttributesEdit extends GraffitiAbstractUndoableEdit {

    /**
     * 
     */
    private static final long serialVersionUID = -1742665358856330660L;

    /** The logger for the current class. */
    private static final Logger logger = Logger
            .getLogger(ChangeAttributesEdit.class.getName());
    
    static {
        logger.setLevel(GlobalLoggerSetting.LOGGER_LEVEL);
    }

    /** map from an attribute to its old value */
    private Map<Attribute, Object> attributeToOldValueMap;

    /**
     * Creates a new <code>AttributeChangeEdit</code> object.
     * 
     * @param attributeToOldValueMap
     *            map between an attribute and its old value.
     * @param geMap
     *            map between the old graph elements and the new ones.
     */
    public ChangeAttributesEdit(Map<Attribute, Object> attributeToOldValueMap,
            Map<GraphElement, GraphElement> geMap) {
        super(geMap);
        this.attributeToOldValueMap = attributeToOldValueMap;

        logger.finer("Attribute to old Value Map contains "
                + attributeToOldValueMap.size() + " key-value-entries.");
    }

    /**
     * Creates a new <code>AttributeChangeEdit</code> object. It is usefull if
     * only one attribute such as coordinate of a bend has been changed.
     * 
     * @param attribute
     *            the changed attribute.
     * @param geMap
     *            map between the old graph elements and the new ones.
     */
    public ChangeAttributesEdit(Attribute attribute,
            Map<GraphElement, GraphElement> geMap) {
        super(geMap);
        this.attributeToOldValueMap = new HashMap<Attribute, Object>();
        this.attributeToOldValueMap.put(attribute, ((Attribute) attribute
                .copy()).getValue());
    }

    /**
     * @see javax.swing.undo.UndoableEdit#getPresentationName()
     */
    @Override
    public String getPresentationName() {
        String name = "";

        if (attributeToOldValueMap.size() == 1) {
            name = coreBundle.getString("undo.changeAttribute")
                    + " "
                    + attributeToOldValueMap.keySet().iterator().next()
                            .getName();
        } else if (attributeToOldValueMap.size() > 1) {
            if (allAttributesEqualPath(attributeToOldValueMap.keySet())) {
                name = coreBundle.getString("undo.changeAttribute")
                        + " "
                        + attributeToOldValueMap.keySet().iterator().next()
                                .getName()
                        + " "
                        + coreBundle.getString("undo.items").replace("%d",
                                String.valueOf(attributeToOldValueMap.size()));
            } else {
                name = coreBundle.getString("undo.changeAttributes");

            }
        }
        return name;
    }

    /**
     * Compares the attributes in the set using their path. If the path is the
     * same for all attributes, <code>true></code> is returned, else
     * <code>false</code>.
     * 
     * @param attributes
     *            the attributes to compare
     * @return <code>true</code> if all attributes have the same path, else
     *         <code>false</code>
     */
    private boolean allAttributesEqualPath(Set<Attribute> attributes) {
        Attribute attr = null;
        for (Attribute a : attributes) {
            if (attr == null) {
                attr = a;
            } else if (attr.getPath().compareTo(a.getPath()) != 0)
                return false;
        }
        return true;
    }

    /*
     * @see org.graffiti.undo.GraffitiAbstractUndoableEdit#execute()
     */
    @Override
    public void execute() {
        // do nothing
    }

    /**
     * @see javax.swing.undo.UndoableEdit#redo()
     */
    @Override
    public void redo() {
        super.redo();
        changeValues();
    }

    /**
     * @see javax.swing.undo.UndoableEdit#undo()
     */
    @Override
    public void undo() {
        super.undo();
        changeValues();
    }

    /**
     * Changes attribute value to the old ones during undo or redo operations.
     */
    private void changeValues() {
        Object newValue = null;
        Object oldValue = null;

        /*
         * maps from an old attribute: attribute belonged to a deleted graph
         * element - to a new possibly created attribute at a new graph element
         */
        HashMap<Attribute, Attribute> attributesMap = new LinkedHashMap<Attribute, Attribute>();

        for (Attribute attribute : attributeToOldValueMap.keySet()) {
            GraphElement newGraphElement = getCurrentGraphElement((GraphElement) attribute
                    .getAttributable());

            logger.finer("The new graph element of the old attribute is "
                    + newGraphElement.toString());
            logger.finer("Attributable of this old attribute is "
                    + attribute.getAttributable().toString());
            logger.finer("The path of the old attribute is "
                    + attribute.getPath());

            Attribute newAttribute;

            try {
                // DEBUG:
                newAttribute = newGraphElement
                        .getAttribute(attribute.getPath());
                logger.finer("The path of the new attribute is "
                        + newAttribute.getPath());

                if (attribute == newAttribute) {
                    newValue = attributeToOldValueMap.get(attribute);

                    // TODO:fix finally the access to the attribute values
                    // over the getValue().
                    // It is currently only a temporary solution for nonfixed
                    // access.
                    oldValue = ((Attribute) attribute.copy()).getValue();

                    // oldValue = attribute.getValue().;
                    attribute.setValue(newValue);

                    attributeToOldValueMap.put(attribute, oldValue);
                } else {
                    attributesMap.put(attribute, newAttribute);
                    logger.info("New attribute is recognized.");
                }
            } catch (AttributeNotFoundException e) {
                logger.finer("Attribute with path " + attribute.getPath()
                        + " not found at " + newGraphElement.toString());
            }
        }

        if (!attributesMap.isEmpty()) {
            logger.finer("attributesMap isn't empty.");

            for (Attribute attribute : attributesMap.keySet()) {
                newValue = attributeToOldValueMap.get(attribute);
                logger.finer("The old attribute is of type "
                        + attribute.getClass().toString());
                logger.finer("The new Value is " + newValue);

                Attribute newAttribute = attributesMap.get(attribute);
                logger.finer("The old attribute is of type "
                        + newAttribute.getClass().toString());

                // TODO:fix finally the access to the attribute values
                // over the getValue().
                // It is currently only a temporary solution for nonfixed
                // access.
                oldValue = ((Attribute) newAttribute.copy()).getValue();

                // oldValue = newAttribute.getValue();
                logger.finer("The old value is " + oldValue);
                newAttribute.setValue(newValue);
                logger.finer("The saved value of new attribute is "
                        + newAttribute.getValue().toString());

                attributeToOldValueMap.remove(attribute);
                attributeToOldValueMap.put(newAttribute, oldValue);
            }
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
