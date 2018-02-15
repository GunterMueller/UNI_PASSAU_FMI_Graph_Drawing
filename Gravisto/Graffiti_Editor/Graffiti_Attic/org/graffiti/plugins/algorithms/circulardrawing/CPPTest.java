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
public class CPPTest {

    public static void main(String[] args) {

        Graph graph = new AdjListGraph();

        int[] proz = { 2, 3, 4, 5 };
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
                                + "cppTestErgebnisse/" + dirName
                                + "Runtime.dat");
                PrintWriter writer = new PrintWriter(new BufferedWriter(
                        fileWriter));
                // writer.println("# NodeNumber " + " EdgeNumber " + " Circular
                // " + " cpp 1.It" + " cpp 2.It" + " cpp 3.It" + " cpp 4.It" + "
                // cpp5.It" );
                // writer.println("# NodeNumber " + " EdgeNumber " + " Circular
                // " + " " + " clockwise" + " anticlockwise" + " descanding" + "
                // ascanding " + " random" );
                // writer.println("# ----------- " + " ---------- " + "
                // --------- " + " --------- " + "-------------" + " ----------"
                // + " --------- " + " --------");
                // writer.println("# NodeNumber " + " EdgeNumber " + " Circular
                // " + " Cpp");
                // writer.println("# ----------- " + " ---------- " + "
                // --------- " + " ---- " );

                writer.flush();
                int size = 0;
                int i = 0;
                if (prozent == 1) {
                    size = allFile.length;
                    i = 20;
                } else if (prozent == 2) {
                    size = 80;
                    i = 10;
                } else if (prozent == 3) {
                    size = 65;
                    i = 6;
                } else if (prozent == 4) {
                    size = 60;
                    i = 5;
                } else if (prozent == 5) {
                    size = 50;
                    i = 4;
                }
                for (; i < size; i++) {
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
                    circularConst.activateRuntime();
                    // Ausf�hrung des Algorithmus Circular
                    // circularConst.activateCrossEnum();
                    circularConst.activateAlgorithm("0");
                    long cTime = System.currentTimeMillis();
                    circular.execute();

                    Time cTimer = new Time(System.currentTimeMillis() - cTime);
                    Graph circularLayout = (Graph) circular.getResult()
                            .getResult().get("circularLayout");
                    circular.reset();
                    CircularPostprocessing cpp = new CircularPostprocessing();
                    // System.out.println("die liste nach der Circular hat l�nge
                    // " + l.size());
                    cpp.attach(circularLayout);
                    try {
                        cpp.check();
                    } catch (PreconditionException ex) {
                        ex.printStackTrace();
                    }
                    // Clockwise

                    CircularConst.SELECT_CPP = 0;
                    circularConst.activateCpp(0);
                    long cppTime = System.currentTimeMillis();
                    cpp.execute();
                    Time cppTimer = new Time(System.currentTimeMillis()
                            - cppTime);
                    // Integer crossAC =
                    // (Integer)cpp.getResult().getResult().get("crossAfterCircular");
                    // Integer crossAPP =
                    // (Integer)cpp.getResult().getResult().get("crossAfterPP");
                    cpp.reset();
                    /*
                     * // Anticlockwise CircularConst.SELECT_CPP = 1;
                     * circularConst.activateCpp(1); cpp = new
                     * CircularPostprocessing(); cpp.attach(circularLayout);
                     * cpp.execute(); Integer crossAPPAnticlock =
                     * (Integer)cpp.getResult().getResult().get("crossAfterPP");
                     * cpp.reset(); // Descanding degree
                     * CircularConst.SELECT_CPP = 2;
                     * circularConst.activateCpp(2); cpp = new
                     * CircularPostprocessing(); cpp.attach(circularLayout);
                     * cpp.execute(); Integer crossAPPDesc =
                     * (Integer)cpp.getResult().getResult().get("crossAfterPP");
                     * cpp.reset(); // Ascanding degree CircularConst.SELECT_CPP
                     * = 3; circularConst.activateCpp(3); cpp = new
                     * CircularPostprocessing(); cpp.attach(circularLayout);
                     * cpp.execute(); Integer crossAPPAsc =
                     * (Integer)cpp.getResult().getResult().get("crossAfterPP");
                     * cpp.reset(); // Random node ordering
                     * CircularConst.SELECT_CPP = 4;
                     * circularConst.activateCpp(4); cpp = new
                     * CircularPostprocessing(); cpp.attach(circularLayout);
                     * cpp.execute(); Integer crossAPPRandom =
                     * (Integer)cpp.getResult().getResult().get("crossAfterPP");
                     * System.out.println("crossAPPRandom ist " +
                     * crossAPPRandom); cpp.reset();
                     */
                    /*
                     * Graph cppLayout =
                     * (Graph)cpp.getResult().getResult().get("circularLayout");
                     * 
                     * cpp = new CircularPostprocessing();
                     * cpp.attach(circularLayout); long cppTime =
                     * System.currentTimeMillis(); cpp.execute(); Time cppTimer
                     * = new Time(System.currentTimeMillis() - cppTime);
                     * //Integer cross =
                     * (Integer)cpp.getResult().getResult().get
                     * ("crossAfterCircular"); //Integer crossAPP1 =
                     * (Integer)cpp.getResult().getResult().get("crossAfterPP");
                     * cpp.reset();
                     */
                    /*
                     * writer.println(nodeNumber + " " + edgeNumber + " " + " "
                     * + 100 + " " + new
                     * Double(((double)crossAPP.intValue()/crossAC
                     * .intValue()*100)).toString() + " " + new
                     * Double(((double)crossAPPAnticlock
                     * .intValue()/crossAC.intValue()*100)).toString() + " " +
                     * new
                     * Double(((double)crossAPPDesc.intValue()/crossAC.intValue
                     * ()*100)).toString() + " " + new
                     * Double(((double)crossAPPAsc
                     * .intValue()/crossAC.intValue()*100)).toString() + " " +
                     * new
                     * Double(((double)crossAPPRandom.intValue()/crossAC.intValue
                     * ()*100)).toString());
                     */

                    writer.println(nodeNumber + " " + edgeNumber + " "
                            + cTimer.getTime() + " " + cppTimer.getTime());

                    writer.flush();

                    file.delete();
                    graph = null;
                } // end of For
                writer.close();
            } // End Of try
            catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        } // End of �ussere ForSchleife
    }// End of main
}
