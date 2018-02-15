// =============================================================================
//
//   TranslationProvider.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.view.interactive;

import java.util.MissingResourceException;

/**
 * {@code GestureFeedbackProvider}s implementing {@code TranslationProvider}
 * support the retrieval of localized strings.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public interface TranslationProvider extends GestureFeedbackProvider {
    /**
     * Returns a localized version of the string specified by {@code id}.
     * 
     * @param id
     *            the id of the string for which the localized version is to be
     *            returned.
     * @return a localized version of the string specified by {@code id}.
     * @throws MissingResourceException
     *             if no localized string for the given id can be found.
     */
    public String translate(String id);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
