package org.graffiti.plugins.tools.benchmark.generators;

import java.util.Random;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.graph.Graph;
import org.graffiti.plugins.tools.benchmark.sampler.AssignmentList;
import org.graffiti.plugins.tools.benchmark.sampler.ConstantSampler;
import org.graffiti.plugins.tools.benchmark.sampler.RandomAssignment;

public class Scrap {
    public static void ex(int n1, int n2, int k) {
        Graph graph = GraffitiSingleton.getInstance().getMainFrame()
                .getActiveEditorSession().getGraph();
        TwoLayerGraphGenerator tgg = new TwoLayerGraphGenerator();
        AssignmentList li = new AssignmentList();
        li.add(new RandomAssignment("firstSize", new ConstantSampler(8)));
        li.add(new RandomAssignment("secondSize", new ConstantSampler(8)));
        li.add(new RandomAssignment("edgeCount", new ConstantSampler(16)));
        tgg.setAssignments(li);
        tgg.generate(new Random(), graph);
    }
}
