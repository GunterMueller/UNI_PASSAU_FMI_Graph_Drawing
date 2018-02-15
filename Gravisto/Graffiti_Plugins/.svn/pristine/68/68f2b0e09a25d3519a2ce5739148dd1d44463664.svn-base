package org.graffiti.plugins.algorithms.circulardrawing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Time;

import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Graph;
import org.graffiti.plugin.algorithm.PreconditionException;

/**
 * @author demirci Created on Sep 17, 2005
 */
public class AlgorithmsTest {

    public static void main(String[] args) {

        Graph graph = new AdjListGraph();

        int[] proz = { 1, 2, 3, 4, 5 };
        for (int j = 0; j < proz.length; j++) {
            int prozent = proz[j];

            try {
                String dirName1 = new Integer(prozent).toString();
                String dirName2 = "prozDichteGraphen";
                String dirName = dirName1 + dirName2;
                File dir = new File("/home/cip/demirci/"
                        + "Graffiti_CircularDrawing/generatedTestGraphs2/"
                        + dirName);
                File outputDir = new File("/home/cip/demirci/"
                        + "Graffiti_CircularDrawing/generatedTestGraphs2/");
                File[] allFile = dir.listFiles();
                // File for runtime analyse
                FileWriter fileWriter = new FileWriter(
                        "/home/cip/demirci/Graffiti_CircularDrawing/"
                                + "cppTestErgebnisse/" + dirName + "Corss.dat");
                PrintWriter writer = new PrintWriter(new BufferedWriter(
                        fileWriter));
                writer.println("# NodeNumber " + " EdgeNumber " + " Circular "
                        + " Circular1" + " Circular2" + " DFSCircular"
                        + " cicularPathL" + " dfsPathL");
                writer.println("# ----------- " + " ---------- "
                        + " --------- " + " --------- " + "-----------"
                        + " --------" + " ------------- " + " --------");
                writer.flush();

                for (int i = 0; i < allFile.length; i++) {
                    CircularConst circularConst = new CircularConst();
                    File nextFile = allFile[i];
                    String fileName = dir.toString() + "/" + nextFile.getName();
                    String outputFileName = outputDir.toString() + "/"
                            + nextFile.getName();
                    Graph2Gml graph2Gml = new Graph2Gml(fileName,
                            outputFileName);
                    graph2Gml.toConvert();

                    GmlReader gr = new GmlReader();
                    String data = graph2Gml.getOutputFileName();
                    File file = new File(data);

                    InputStream is = null;

                    try {
                        is = new FileInputStream(file);
                    } catch (FileNotFoundException exp) {
                        exp.printStackTrace();
                    }

                    try {
                        graph = gr.read(is);
                    } catch (IOException exp) {
                        exp.printStackTrace();
                    }

                    Circular circular = new Circular();
                    int nodeNumber = graph.getNumberOfNodes();
                    int edgeNumber = graph.getNumberOfEdges();
                    circular.attach(graph);

                    try {
                        circular.check();
                    } catch (PreconditionException e) {
                        e.printStackTrace();
                    }

                    CircularConst.TEST = 1;
                    // circularConst.activateRuntime();
                    // Ausf�hrung des Algorithmus Circular
                    circularConst.activateCrossEnum();
                    circularConst.activateAlgorithm("0");
                    long cTime = System.currentTimeMillis();
                    circular.execute();
                    // Integer cCross =
                    // (Integer)(circular.getResult().getResult().get("numberOfCrossing"));
                    int cCross = circular.getNumberOfCrossing();
                    int cirPathL = ((Integer) (circular.getResult().getResult()
                            .get("cirPathL"))).intValue();
                    // take the end time
                    Time cTimer = new Time(System.currentTimeMillis() - cTime);
                    circular.reset();

                    // Ausf�hrung des Algorithmus Circular1
                    circularConst.activateAlgorithm("1");
                    long c1Time = System.currentTimeMillis();
                    circular.execute();
                    // Integer c1Cross =
                    // (Integer)(circular.getResult().getResult().get("numberOfCrossing"));
                    int c1Cross = circular.getNumberOfCrossing();
                    // take the end time
                    Time c1Timer = new Time(System.currentTimeMillis() - c1Time);
                    circular.reset();

                    // Ausf�hrung des Algorithmus Circular2
                    circularConst.activateAlgorithm("2");
                    long c2Time = System.currentTimeMillis();
                    circular.execute();
                    // Integer c2Cross =
                    // (Integer)(circular.getResult().getResult().get("numberOfCrossing"));
                    int c2Cross = circular.getNumberOfCrossing();
                    // take the end time
                    Time c2Timer = new Time(System.currentTimeMillis() - c2Time);
                    circular.reset();

                    // Ausf�hrung des Algorithmus DFSCircular
                    circularConst.activateAlgorithm("3");
                    long dfsTime = System.currentTimeMillis();
                    circular.execute();
                    int dfsPathL = ((Integer) (circular.getResult().getResult()
                            .get("cirPathL"))).intValue();
                    // Integer dfsCross =
                    // (Integer)(circular.getResult().getResult().get("numberOfCrossing"));
                    int dfsCross = circular.getNumberOfCrossing();
                    // take the end time
                    Time dfsTimer = new Time(System.currentTimeMillis()
                            - dfsTime);
                    circular.reset();

                    /*
                     * writer.println(nodeNumber + " " + edgeNumber + " " +
                     * cTimer.getTime() + " " + c1Timer.getTime() + " " +
                     * c2Timer.getTime() + " " + dfsTimer.getTime() );
                     * writer.flush();
                     */

                    writer.println(nodeNumber + " " + edgeNumber + " " + cCross
                            + " " + c1Cross + " " + c2Cross + " " + dfsCross
                            + " " + cirPathL + " " + dfsPathL);
                    writer.flush();

                    file.delete();
                } // end of For
                writer.close();
            } // End Of try
            catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        } // End of �ussere ForSchleife
    }// End of main
}
