package org.graffiti.plugins.ios.importers.ontology.xml.util;

import org.graffiti.util.ext.Arrays;
import org.graffiti.util.ext.ParameterList;

/**
 * Extension methods for {@link String}s that model resource identifiers in the
 * resource description framework (RDF). Those strings are uris following the
 * pattern:
 * 
 * <pre>
 * protocol://part_1/part_2/.../part_n#fragment
 * </pre>
 * 
 * The fragment-part is optional.
 * 
 * @author Harald Frankenberger
 */
public class ResourceString {

    private ResourceString() {
    }

    private static String[] parts(String this_) {
        return this_.split("[:/#]");
    }

    /**
     * Extracts an id from this resource-string. In this context the last part
     * of this resource-string or its fragment are considered ids for the
     * resource this string describes.
     * 
     * @param this_
     *            this resource-string.
     * @return the id part of this resource-string
     */
    public static String extractId(String this_) {
        ParameterList.checkNotNull(this_);
        String[] parts = parts(this_);
        return Arrays.last(parts);
    }
}
