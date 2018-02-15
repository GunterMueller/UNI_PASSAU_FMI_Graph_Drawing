//   PSTricksSerializer.java

package org.graffiti.plugins.ios.exporters.pstricks;

import java.io.IOException;
import java.io.OutputStream;

import org.graffiti.graph.Graph;
import org.graffiti.plugin.io.AbstractOutputSerializer;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;

/**
 * @author lessenic
 * @version $Revision$ $Date$
 */
public class PSTricksSerializer extends AbstractOutputSerializer {

    private String[] exporters = { "Gravisto-style Exporter", "Simple Exporter" };

    private String exporter;

    /*
     * @see org.graffiti.plugin.io.Serializer#getExtensions()
     */
    @Override
    public String[] getExtensions() {
        return new String[] { ".tex" };
    }

    /*
     * @see org.graffiti.plugin.Parametrizable#getName()
     */
    @Override
    public String getName() {
        return "PSTricks Exporter";
    }

    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        StringSelectionParameter exporter = new StringSelectionParameter(
                exporters, "Exporter", "Which Exporter should be used?");
        return new Parameter[] { exporter };
    }

    @Override
    protected void setAlgorithmParameters(Parameter<?>[] params) {
        exporter = ((StringSelectionParameter) params[0]).getValue();
    }

    /*
     * @see org.graffiti.plugin.io.OutputSerializer#write(java.io.OutputStream,
     * org.graffiti.graph.Graph)
     */
    @Override
    public void write(OutputStream stream, Graph g) throws IOException {
        PSTricksExporter pstricks;

        if (exporter.equals(exporters[0])) {
            pstricks = new EnhancedPSTricksExporter();
        } else {
            pstricks = new SimplePSTricksExporter();
        }

        pstricks.write(stream, g);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
