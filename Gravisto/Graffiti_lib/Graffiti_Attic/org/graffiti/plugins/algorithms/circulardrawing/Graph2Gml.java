package org.graffiti.plugins.algorithms.circulardrawing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.StringCharacterIterator;
import java.util.StringTokenizer;

/**
 * Created on Sep 17, 2005
 */
public class Graph2Gml {

    String nameEingabedatei;

    StringCharacterIterator StrChIt;

    String nameAusgabedatei;

    String zeile, newline;

    File eingabedatei;

    File ausgabedatei;

    FileReader fr;

    FileWriter fw;

    BufferedReader br;

    BufferedWriter bw;

    int NrNodes = 0;

    int NrEdges = 0;

    int NodeCounter = 0;

    int EdgeCounter = 0;

    int FileEndingLength = 1;

    public Graph2Gml(String nameEingabedatei, String nameAusgabedatei) {
        this.nameEingabedatei = nameEingabedatei;
        this.nameAusgabedatei = nameAusgabedatei;
    }

    public String getOutputFileName() {
        return nameAusgabedatei;
    }

    public void toConvert() {
        newline = System.getProperty("line.separator");

        try {

            StrChIt = new StringCharacterIterator(nameEingabedatei);
            StrChIt.last();
            while (StrChIt.current() != new String(".").charAt(0)) {
                StrChIt.previous();
                FileEndingLength++;
            }

            nameAusgabedatei = new String(nameEingabedatei.toCharArray(), 0,
                    new String(nameEingabedatei).length() - FileEndingLength);

            nameAusgabedatei = nameAusgabedatei + ".gml";

            eingabedatei = new File(nameEingabedatei);
            ausgabedatei = new File(nameAusgabedatei);
            fr = new FileReader(eingabedatei);
            fw = new FileWriter(ausgabedatei);
            br = new BufferedReader(fr);
            bw = new BufferedWriter(fw);

            bw.write("graph [" + newline);
            bw.write("directed 0" + newline);

            zeile = br.readLine();

            StringTokenizer strTok = new StringTokenizer(zeile);
            NrNodes = new Integer(strTok.nextToken()).intValue();
            NrEdges = new Integer(strTok.nextToken()).intValue();

            for (int i = 0; i < NrNodes; i++) {

                bw.write("node [id " + i + "]" + newline);

            }

            zeile = br.readLine();

            while (zeile != null) {

                strTok = new StringTokenizer(zeile);

                while (strTok.hasMoreTokens()) {
                    int targetId = ((new Integer(strTok.nextToken()).intValue()) - 1);
                    bw.write("edge [" + " id " + EdgeCounter + "  source "
                            + NodeCounter + "  target " + targetId + " ]"
                            + newline);
                    EdgeCounter++;
                }

                NodeCounter++;

                zeile = br.readLine();
            }

            bw.write("]");
            nameEingabedatei = null;
            br.close();
            bw.close();
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            System.out.println("Usage:\n");
            System.out.println("GRAPH2GML filename");
            System.out
                    .println("\tfilename:\tName of file in .graph-format to convert");
        } catch (FileNotFoundException fnfe) {
            System.out.println("Habe gefangen: " + fnfe);
        } catch (IOException ioe) {
            System.out.println("Habe gefangen: " + ioe);
        }
    }
}
