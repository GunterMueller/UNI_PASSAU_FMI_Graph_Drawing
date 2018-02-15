package org.graffiti.plugins.ios.importers.ontology.xml;

import org.graffiti.util.xml.XmlElement;

/**
 * The own:MinCardinality element.
 * 
 * @author Harald Frankenberger
 */
public class OwlMinCardinality extends XmlElement {

    private Integer value = null;

    /**
     * Notifies this element that its cardinality has been parsed.
     * 
     * @param chars
     *            the cardinality of this element encoded as a string
     */
    @Override
    public void characterData(String chars) {
        value = Integer.parseInt(chars);
    }

    /**
     * Returns the min-cardinality this element represents.
     * 
     * @return the min-cardinality this element represents.
     */
    public int value() {
        return value;
    }

}
