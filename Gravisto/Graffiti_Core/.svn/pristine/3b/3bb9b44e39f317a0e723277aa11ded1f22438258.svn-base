// =============================================================================
//
//   AlgorithmFactory.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.gridsifting;

import java.lang.reflect.Constructor;

import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
class AlgorithmFactory<T extends AbstractSubAlgorithm> {
    private Constructor<? extends T> constructor;

    protected AlgorithmFactory(Constructor<? extends T> constructor) {
        this.constructor = constructor;
    }

    public T create(BlockGraph graph, AlgorithmParameters parameters,
            SugiyamaData data, Object... arguments) {
        try {
            T algorithm = constructor.newInstance(arguments);
            algorithm.setGraphAndParameters(graph, parameters, data);
            algorithm.init();
            return algorithm;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
