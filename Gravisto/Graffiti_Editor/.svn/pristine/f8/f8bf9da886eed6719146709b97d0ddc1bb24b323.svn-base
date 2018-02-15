// =============================================================================
//
//   DistributionFactory.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.sampler;

import java.util.HashMap;
import java.util.Map;

import org.graffiti.plugins.tools.math.BinomialDistribution;
import org.graffiti.plugins.tools.math.DiscreteUniformDistribution;
import org.graffiti.plugins.tools.math.Distribution;
import org.graffiti.plugins.tools.math.GeometricDistribution;
import org.graffiti.plugins.tools.math.IntegerInterval;
import org.graffiti.plugins.tools.math.UniformDistribution;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class DistributionFactory {
    private static final Map<String, DistributionFactory> map = new HashMap<String, DistributionFactory>();

    static {
        map.put("binomial", new DistributionFactory(2) {
            @Override
            protected Distribution<? extends Number> create(double[] args) {
                return new BinomialDistribution((int) args[0], args[1]);
            }
        });
        map.put("unif", new DistributionFactory(2) {
            @Override
            protected Distribution<? extends Number> create(double[] args) {
                return new UniformDistribution(args[0], args[1]);
            }
        });
        map.put("geometric", new DistributionFactory(1) {
            @Override
            protected Distribution<? extends Number> create(double[] args) {
                return new GeometricDistribution(Integer.MAX_VALUE >> 1,
                        args[0]);
            }
        });
        map.put("iunif", new DistributionFactory(2) {
            @Override
            protected Distribution<? extends Number> create(double[] args) {
                return new DiscreteUniformDistribution<Long>(
                        new IntegerInterval((long) args[0], (long) args[1]));
            }
        });
    }

    private Integer argCount;

    protected DistributionFactory() {
    }

    protected DistributionFactory(int argCount) {
        this.argCount = argCount;
    }

    public static DistributionFactory get(String id) {
        return map.get(id);
    }

    protected abstract Distribution<? extends Number> create(double[] args);

    public Distribution<? extends Number> createDistribution(double[] args) {
        if (argCount != null && argCount != args.length)
            throw new SamplingException("error.functionArgumentCount");
        return create(args);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
