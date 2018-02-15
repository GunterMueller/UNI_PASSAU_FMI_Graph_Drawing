// =============================================================================
//
//   GravistoUri.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.label;

import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class GravistoAttributeUri {
    // TODO browsable nodes
    private static final Pattern PATTERN = Pattern
            .compile("gravisto\\-attribute:\\.#([a-zA-Z_0-9\\.]+)");
    public static final String SCHEME = "gravisto-attribute";

    private String attributePath;

    public GravistoAttributeUri(String uri) throws URISyntaxException {
        Matcher matcher = PATTERN.matcher(uri);
        // TODO more verbosity in reporting the reason
        if (!matcher.matches())
            throw new URISyntaxException(uri, "");
        attributePath = matcher.group(1);
    }

    public String getAttributePath() {
        return attributePath;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
