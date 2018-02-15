// =============================================================================
//
//   PsTricksOutput.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.output;

import java.util.Map;

import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.plugins.ios.exporters.pstricks.PSTricksSerializer;
import org.graffiti.plugins.tools.benchmark.BenchmarkException;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class PsTricksOutput extends GraphOutput {
    public PsTricksOutput() {
        super(new PSTricksSerializer());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(int sourceCount) throws BenchmarkException {
        int exporter = 0;

        if (options != null) {
            for (Map.Entry<String, String> entry : options.entrySet()) {
                String key = entry.getKey();
                if (key.equals("exporter")) {
                    try {
                        exporter = Integer.valueOf(entry.getValue());
                    } catch (NumberFormatException e) {
                        throw new BenchmarkException("error.intFormat", entry
                                .getValue());
                    }
                } else
                    throw new BenchmarkException("error.unknownOutputOption",
                            key);
            }
        }

        PSTricksSerializer psts = (PSTricksSerializer) serializer;
        Parameter<?>[] params = psts.getAlgorithmParameters();
        StringSelectionParameter ssp = (StringSelectionParameter) params[0];
        ssp.setSelectedValue(exporter);
        psts.setParameters(params);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
