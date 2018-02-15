package org.graffiti.util.xml;

import org.graffiti.util.ext.ClassName;
import org.graffiti.util.ext.Classes;

/**
 * Creates {@link XmlElement}s. An xml-element-factory is created for a package
 * that contains the classes to load dynamically while parsing an xml-file. This
 * class uses some naming-conventions formalized by
 * {@link QualifiedName#toSimpleClassName(String)}.
 * 
 * @see QualifiedName
 * @see XmlElement
 * @author Harald Frankenberger
 */
public class XmlElementFactory {

    private final String packageName;

    /**
     * Creates an xml-element-factory for the given package-name.
     * 
     * @param packageName
     *            the package containing the xml-elements to instantiate.
     */
    public XmlElementFactory(String packageName) {
        this.packageName = packageName;
    }

    /**
     * Instantiates a new {@link XmlElement} from this factory's package. This
     * implementation will first try to load the class whose name is obtained by
     * invoking {@link QualifiedName#toSimpleClassName(String)} on
     * <code>qualifiedName</code>. If no such class exists, the fallback
     * strategy is to return a new {@link XmlElement} with its name set to the
     * binary-class-name of the class that should have been instantiated.
     * 
     * @param qualifiedName
     *            the qualified name of the {@link XmlElement} to instantiate
     * @return a new {@link XmlElement} from this factory's package
     * @see QualifiedName#toSimpleClassName(String)
     * @see XmlElement#XmlElement(String)
     */
    public XmlElement newXmlElement(String qualifiedName) {
        String className = QualifiedName.toSimpleClassName(qualifiedName);
        Class<?> elementClass = ClassName.findInPackage(className, packageName);
        if (elementClass != null)
            return (XmlElement) Classes.newInstance(elementClass);
        else
            return new XmlElement(QualifiedName
                    .toSimpleClassName(qualifiedName));
    }

}
