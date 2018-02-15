package org.graffiti.util.xml;

import java.lang.reflect.Field;

import org.graffiti.util.ext.Classes;
import org.graffiti.util.ext.Fields;
import org.graffiti.util.ext.Strings;

/**
 * An element of an {@link XmlDocument}. Instances of this class are typically
 * created by an {@link XmlElementFactory} and its attributes are set by an
 * {@link XmlHandler}.
 * <p>
 * This class uses reflection to set public fields in subclasses. For every
 * attribute <i>(name,value)</i>, where <i>name</i> is the {@link QualifiedName}
 * of the attribute and <i>value</i> its value, a subclass can provide a public
 * field whose name matches {@link QualifiedName#toFieldName(String)}. A call to
 * <code>set(name,value)</code> will set the field to the given
 * <code>value</code>.
 * <p>
 * <b>Note</b>: To be created by an xml-element-factory, subclasses have to
 * provide a public default-constructor.
 * 
 * @author Harald Frankenberger
 * @see XmlElementFactory
 * @see XmlHandler#startElement(String, String, String, org.xml.sax.Attributes)
 * @see #set(String, String)
 */
public class XmlElement extends CallbackHandler {

    private final String name;

    /**
     * Creates a new xml-element with its {@link Class#getSimpleName()} as its
     * name.
     * 
     */
    public XmlElement() {
        name = getClass().getSimpleName();
    }

    /**
     * Creates a new xml-element with the given name.
     * 
     * @param name
     *            the name of this xml-element.
     */
    public XmlElement(String name) {
        this.name = name;
    }

    /**
     * Sets the given attribute to the given value. This method uses reflection
     * to set public fields on this element. First the given
     * <code>attribute</code> name is converted to a field-name by a call to
     * {@link QualifiedName#toFieldName(String)}. If a field with this
     * field-name exists it is set to the given <code>value</code>; otherwise it
     * is ignored.
     * 
     * @param attribute
     *            the attribute to set
     * @param value
     *            the new value of the given attribute
     */
    public void set(String attribute, String value) {
        String name = QualifiedName.toFieldName(attribute);
        Field field = Classes.findField(this.getClass(), name);
        if (field != null) {
            Fields.set(field, this, value);
        }
    }

    /**
     * Processes the given character-data for this element. This implementation
     * is empty.
     * 
     * @param chars
     *            the character-data to process
     */
    public void characterData(String chars) {

    }

    /**
     * Processes this element with the given processor. This implementation is
     * empty.
     * 
     * @param processor
     *            the processor to use to process this xml-element.
     */
    public void processWith(Object processor) {

    }

    /**
     * Returns a string representation of this xml-element.
     * 
     * @return a string representation of this xml-element
     */
    @Override
    public String toString() {
        return Strings.append("XmlElement<", name, ">");
    }

    /**
     * Returns the value of the given attribute; throws
     * {@link NoSuchAttributeException} if the attribute does not exist on this
     * element. This implementation uses the same reflection-scheme as
     * {@link #set(String, String)}.
     * 
     * @param attribute
     *            the qualified name of the attribute whose value is requested.
     * @return the value of the given attribute
     * @throws NoSuchAttributeException
     *             if the attribute does not exist on this element.
     * @see #set(String, String)
     */
    public String get(String attribute) {
        String name = QualifiedName.toFieldName(attribute);
        Field field = Classes.findField(this.getClass(), name);
        if (field == null)
            throw new NoSuchAttributeException(toString()
                    + " does not define the attribute " + name);
        return Fields.get(field, this);
    }
}
