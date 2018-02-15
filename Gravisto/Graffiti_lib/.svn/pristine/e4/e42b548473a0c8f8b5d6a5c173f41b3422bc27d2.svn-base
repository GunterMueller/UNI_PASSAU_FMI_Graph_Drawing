// =============================================================================
//
//   FontHandler.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

final class FontKey {
    private String name;
    private int style;
    private int size;

    FontKey(String name, int style, int size) {
        this.name = name;
        this.style = style;
        this.size = size;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof FontKey))
            return false;
        FontKey other = (FontKey) obj;
        return name.equals(other.name) && style == other.style
                && size == other.size;
    }

    @Override
    public int hashCode() {
        int hashCode = name.hashCode();
        hashCode ^= style;
        hashCode ^= size;
        return hashCode;
    }
}

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class FontManager<F extends FastFont> {
    protected Map<FontKey, F> map;

    protected FontManager() {
        map = new HashMap<FontKey, F>();
    }

    public final FastFont acquireFont(String name, int style, int size) {
        FontKey key = new FontKey(name, style, size);
        F fastFont = map.get(key);
        if (fastFont != null)
            return fastFont;
        fastFont = createFont(new Font(name, style, size));
        map.put(key, fastFont);
        return fastFont;
    }

    protected abstract F createFont(Font font);

    public final void reset() {
        for (Map.Entry<FontKey, F> entry : map.entrySet()) {
            onDeleteFont(entry.getValue());
        }
        map.clear();
    }

    protected void onDeleteFont(F font) {
    };
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
