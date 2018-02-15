// =============================================================================
//
//   MiniSat.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.stackqueuelayout.minisat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class MiniSat {
    private static final String EXECUTABLE = MiniSat.class.getResource(
            "MiniSat_v1.14_linux").getPath();

    private PrintStream printStream;

    public CnfFormula createFormula() {
        return new CnfFormula(this);
    }

    public void setVerboseOutput(PrintStream printStream) {
        this.printStream = printStream;
    }

    protected String solve(String input) throws InterruptedException {
        try {
            File inputFile = File.createTempFile("gravistoMiniSat", ".cnf");
            FileWriter fw = new FileWriter(inputFile);
            BufferedWriter writer = new BufferedWriter(fw);
            writer.append(input);
            writer.flush();
            writer.close();
            fw.close();

            File outputFile = File.createTempFile("gravistoMiniSat", ".txt");

            exec(inputFile, outputFile);
            
            FileReader fr = new FileReader(outputFile);

            BufferedReader reader = new BufferedReader(fr);

            String line = reader.readLine();

            if (line != null) {
                if (line.equals("UNSAT")) {
                    line = null;
                } else if (line.equals("SAT")) {
                    line = reader.readLine();
                } else {
                    throw new RuntimeException("Unexpected minisat output format.");
                }
            } else {
                throw new RuntimeException("Unexpected minisat output format.");
            }
            
            reader.close();
            fr.close();
            inputFile.delete();
            outputFile.delete();
            return line;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void exec(File inputFile, File outputFile) throws IOException,
            InterruptedException {
        Process process = Runtime.getRuntime().exec(
                new String[] { EXECUTABLE, inputFile.getCanonicalPath(),
                        outputFile.getCanonicalPath() });
        
        InputStreamReader isr = new InputStreamReader(process.getInputStream());
        BufferedReader reader = new BufferedReader(isr);

        if (printStream != null) {
            String line;
            while ((line = reader.readLine()) != null) {
                printStream.println(line);
            }
        }
        
        process.waitFor();
        reader.close();
        isr.close();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
