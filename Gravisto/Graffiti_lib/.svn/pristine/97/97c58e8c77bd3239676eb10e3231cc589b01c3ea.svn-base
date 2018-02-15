package org.graffiti.plugins.algorithms.chebyshev;

import java.lang.reflect.Constructor;

class AlgorithmFactory<T extends AbstractSubAlgorithm> {
    private Constructor<? extends T> constructor;

    protected AlgorithmFactory(Constructor<? extends T> constructor) {
        this.constructor = constructor;
    }

    public T create(AuxGraph graph, AlgorithmParameters parameters,
            Object... arguments) {
        try {
            T algorithm = constructor.newInstance(arguments);
            algorithm.setGraphAndParameters(graph, parameters);
            algorithm.init();
            return algorithm;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
