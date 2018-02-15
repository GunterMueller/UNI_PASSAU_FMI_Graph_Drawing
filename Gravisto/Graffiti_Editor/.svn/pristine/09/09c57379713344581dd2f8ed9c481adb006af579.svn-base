package de.uni_passau.fim.br.planarity.gravisto;

import java.io.IOException;
import java.io.PrintStream;

import org.graffiti.plugins.algorithms.planarity.PlanarityAlgorithm;
import org.graffiti.plugins.ios.exporters.graphml.GraphMLWriter;

import de.uni_passau.fim.br.planarity.PlanarityTest;
import de.uni_passau.fim.br.planarity.graph.Graph;

public class Tabulator {
    public static PrintStream out;
    static {
        try {
            out = new PrintStream("/dev/null");
//            out = System.out;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static int CURRENT;
    
	public static void main(String[] args) {
		new Tabulator().start();
	}
	
	public void start() {
		Generator generator = new Generator(1234);
		int failures = 0;
		final int instanceCount = 800000;
		int planarCount = 0;
		for (int i = 0; i < instanceCount; i++) {
		    CURRENT = i;
			Graph graph = generator.create(8);
//			if (i != 164) continue;
			org.graffiti.graph.Graph gg = Converter.convert(graph);
			PlanarityAlgorithm algo = new PlanarityAlgorithm();
			algo.attach(gg);
			PlanarityTest test = new PlanarityTest(graph);
			long grStartTime = System.nanoTime();
			boolean gravistoPlanar = algo.isPlanar();
			long grTime = System.nanoTime() - grStartTime; 
			long deStartTime = System.nanoTime();
			boolean mdPlanar = test.test();
			long deTime = System.nanoTime() - deStartTime;
			double factor = grTime / (double) deTime;
//			System.out.println(String.format("%.6f\t\t", factor) + (grTime > deTime ? "SUCCESS" : "FAILURE"));
			if (grTime > deTime) {
			    failures++;
			}
			//System.out.print(gravistoPlanar + "(" + (grTime / 1000000000.0) + ") | " + mdPlanar + "(" + (deTime / 1000000000.0) + ")\t\t");
			if (gravistoPlanar == mdPlanar) {
				Tabulator.out.println("SUCCESS");
			} else {
				System.out.println("FAILURE");
				System.out.println("i = " + i);
				System.out.println("planar(Gravisto) = " + gravistoPlanar + "; planar(MD) = " + mdPlanar);
				GraphMLWriter writer = new GraphMLWriter();
				try {
				    writer.write(Converter.convert(graph), "/tmp/failure.graphml");
				} catch (IOException e) {
				    throw new RuntimeException(e);
				}
				System.exit(0);
			}
			if (mdPlanar) {
			    planarCount++;
			}
			
			if (i % 1000 == 0) {
			    System.out.println(String.format("%d / %d   (planar: %f%%)", i, instanceCount, 100.0 * planarCount / (i + 1)));
			}
			if (!gravistoPlanar && mdPlanar) {
//				for (EdgeReport report : test.getReports()) {
//					System.out.println(report);
//				}
			}
		}
		System.out.println(failures + "/20000 Failures");
	}
}
