// =============================================================================
//
//   ProgressOutput.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.output;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Pattern;

import org.graffiti.plugins.tools.benchmark.Assignment;
import org.graffiti.plugins.tools.benchmark.BenchmarkException;
import org.graffiti.plugins.tools.benchmark.Data;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class ProgressOutput extends BenchmarkOutput {
    private static final Pattern ZERO_PATTERN = Pattern.compile("0+");
    private int lastProgress;
    private int configurationCount;
    private boolean knowsConfigurationCount;
    private double sourceShare;
    private String reverseIndex;

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(int sourceCount) throws BenchmarkException {
        try {
            reverseIndex = null;
            if (options != null) {
                reverseIndex = options.get("reverseIndex");
            }

            lastProgress = -1;
            configurationCount = 0;
            knowsConfigurationCount = false;
            sourceShare = sourceCount > 0 ? 1.0 / sourceCount : 1.0;
            printProgress(0);
        } catch (IOException e) {
            throw new BenchmarkException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postConfig(Data data, Assignment assignment)
            throws IOException, BenchmarkException {
        if (!knowsConfigurationCount) {
            if (ZERO_PATTERN.matcher(data.getSourceIndex()).matches()) {
                configurationCount++;
            } else {
                knowsConfigurationCount = true;
            }
        }
        if (knowsConfigurationCount) {
            printProgress((int) ((Integer.parseInt(data.getSourceIndex()) + (Integer
                    .parseInt(assignment.getConfigurationIndex()) + 1)
                    / (double) configurationCount)
                    * sourceShare * 100));
        }
    }

    private void printProgress(int percent) throws IOException {
        if (percent > lastProgress) {
            StringBuilder builder = new StringBuilder();
            if (percent != 0 && reverseIndex != null) {
                if (reverseIndex.isEmpty()) {
                    /*
                     * for (int i = 0; i < 67; i++) { builder.append('\b'); }
                     * //builder.append("\u001bM");
                     */
                    builder.append('\r');
                } else {
                    builder.append(reverseIndex);
                }
            }
            builder.append(String.format("Progress: %3d%% [", percent));
            for (int i = 0; i < percent / 2; i++) {
                builder.append("=");
            }
            for (int i = percent / 2; i < 50; i++) {
                builder.append(" ");
            }
            builder.append("]");
            PrintWriter pw = new PrintWriter(getOut(true), true);
            pw.print(builder.toString());
            if (reverseIndex == null) {
                pw.println();
            }

            lastProgress = percent;

            if (lastProgress == 100) {
                pw.println();
            }

            pw.flush();
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
