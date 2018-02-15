// =============================================================================
//
//   StartStep.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.compactor;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class StartStep extends Step {
    private LevelNode level;
    
    public StartStep(LevelNode level) {
        this.level = level;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void contract() {
        if (level.northLevel != null) {
            level.northLevel.southLevel = level.southLevel;
        } else {
            /// New firstLevel
        }
        
        if (level.southLevel != null) {
            level.southLevel.northLevel = level.northLevel;
        }
    }
}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
