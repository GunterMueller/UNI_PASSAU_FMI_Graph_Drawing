package org.graffiti.util.xml;

import org.graffiti.util.ext.Strings;

/**
 * Extension methods for {@link String}s that represent qualified names of
 * xml-elements. A qualified name consists of a namespace-prefix and an id
 * separated by a colon.
 * 
 * @author Harald Frankenberger
 */
public class QualifiedName {

    private static final char COLON = ':';

    private QualifiedName() {
    }

    /**
     * Returns the id of this qualified name; i.e. the part after the colon
     * separating namespace and id.
     * 
     * @param this_
     *            this qualified name
     * @return the id of this qualified name
     */
    public static String id(String this_) {
        return Strings.substringAfter(this_, COLON);
    }

    /**
     * Returns the namespace-prefix of this qualified name; i.e. the part before
     * the colon separating namespace and id.
     * 
     * @param this_
     *            this qualified name
     * @return the namespace-prefix of this qualified name
     */
    public static String namespace(String this_) {
        return Strings.substringBefore(this_, COLON);
    }

    /**
     * Converts this qualified name to a field name. This implementation
     * concatenates namespace and id and capitalizes the id's first character.
     * 
     * @param this_
     *            this qualified name
     * @return this qualified name as a field name
     */
    public static String toFieldName(String this_) {
        return Strings.append(namespace(this_), Strings
                .capitalizeFirstCharacter(id(this_)));
    }

    /**
     * Converts this qualified-name to a class-name. Capitalizes the first
     * characters of the namespace-prefix and id of this qualified name and
     * concatenates them.
     * 
     * @param this_
     *            this qualified name
     * @return this qualified name as a simple class name
     */
    public static String toSimpleClassName(String this_) {
        String part1 = Strings.capitalizeFirstCharacter(namespace(this_));
        String part2 = Strings.capitalizeFirstCharacter(id(this_));
        return Strings.append(part1, part2);
    }

}
