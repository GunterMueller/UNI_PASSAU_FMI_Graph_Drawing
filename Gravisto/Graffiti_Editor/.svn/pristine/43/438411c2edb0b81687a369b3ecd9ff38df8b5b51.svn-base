package org.graffiti.plugins.algorithms.sugiyama.gridsifting.benchmark;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.graffiti.plugin.algorithm.PreconditionException;


public class UpwardPlanarization {
    
    
    private static final String GRAPH_DIR = "/tmp/gleissne/randomPA2";
    private static final String RESULT_FILE = "/tmp/gleissne/results.csv";
//    private static final Pattern DENSITY_PATTERN = Pattern.compile("-d([\\d\\.\\,]+)");
    private static final Pattern INSTANCE_PATTERN = Pattern.compile("-i([\\d\\,]+)");
    
    private static final Pattern LOG_NODE_COUNT = Pattern.compile(".*\\\tnodes: ([^\\\t]*)\\\t.*");
    private static final Pattern LOG_EDGE_COUNT = Pattern.compile(".*\\\tarcs: ([^\\\t]*)\\\t.*");
    private static final Pattern LOG_CROSSING_COUNT = Pattern.compile(".*\\\tcrossings: ([^\\\t]*)\\\t.*");
    private static final Pattern LOG_LEVEL_COUNT = Pattern.compile(".*\\\tnumber of layers: ([^\\\t]*)\\\t.*");
    private static final Pattern LOG_TIME = Pattern.compile(".*\\\ttime upward planarization: ([^\\\t]*)\\\t.*");
    
    private static Pattern CONFIG_PATTERN = Pattern.compile("([^\\t]+)\\t.*");
    
//    private static Pattern RANDOM_PA_PATTERN = Pattern.compile(".*\\_n(\\d+)\\_e(\\d+)\\_i(\\d+)\\.gml");
    private static Pattern RANDOM2_PA_PATTERN = Pattern.compile(".*\\_n(\\d+)\\_e(\\d+)\\_i(\\d+)\\.gml");
    
//    private static final Pattern ROME_PATTERN = Pattern.compile(".*grafo(\\d).*\\.gml");
    
    private HashSet<String> finishedConfigurationStrings;
    
    private PrintWriter writer;
    
    private String currentConfigurationString;
    
//    private Set<Double> densities;
    private Set<Integer> instances;
    
    public static void main(String[] args) {
        UpwardPlanarization benchmark = new UpwardPlanarization(args);
        
        benchmark.execute(new File(GRAPH_DIR), new File(RESULT_FILE));
    }
    
    public UpwardPlanarization(String[] args) {
        finishedConfigurationStrings = new HashSet<String>();
//        densities = new HashSet<Double>();
        instances = new HashSet<Integer>();
        processArgs(args);
    }
    
    private void processArgs(String[] args) {
        for (String arg : args) {
//            Matcher m = DENSITY_PATTERN.matcher(arg);
//            
//            if (m.matches()) {
//                for (String densStr : m.group(1).split("\\,")) {
//                    densities.add(Double.valueOf(densStr));
//                }
//                
//                continue;
//            }
//            
            Matcher
            m = INSTANCE_PATTERN.matcher(arg);
            
            if (m.matches()) {
                for (String instStr : m.group(1).split("\\,")) {
                    instances.add(Integer.valueOf(instStr));
                }
                
                continue;
            }
        }
    }
    
    private void execute(File directory, File resultFile) {
        try {
            if (!directory.isDirectory()) throw new IllegalArgumentException();
            
            File[] files = directory.listFiles();
            
            System.out.print("Sorting files...");
            
            Arrays.sort(files, new RandomPAFileComparator());
            
            System.out.println(" Done.");
          
            determineFinishedConfigurations(resultFile);
            
            writer = new PrintWriter(new FileWriter(resultFile, true), true);
            
            writer.println("! LAYOUT id file config nodeCount edgeCount density initialCrossingCount crossingCount time levelCount compactedLevelCount");
            writer.println("! START " + timestamp());
            
            for (File file : files) {
                Matcher m = RANDOM2_PA_PATTERN.matcher(file.getName());
//                Matcher m = RANDOM_PA_PATTERN.matcher(file.getName());
                
                if (!m.matches()) continue;
                
//                int nodeCount = Integer.valueOf(m.group(1));
//                int edgeCount = Integer.valueOf(m.group(2));
//                
//                if (!densities.contains(edgeCount / (double) nodeCount)) continue;
//                
//                int index = Integer.valueOf(m.group(3));
                
                int index = Integer.valueOf(m.group(3));
                
                if (!instances.contains(index)) continue;
                
                execute(file, writer);
            }
            
            writer.println("! STOP " + timestamp());
            
            writer.close();
            writer = null;
        } catch (Throwable t) {
            PrintWriter out = new PrintWriter(System.out);
            out.println("!![");
         
            if (currentConfigurationString != null) {
                out.print("Exception on processing ");
                out.print(currentConfigurationString);
                out.println(':');
            }
            
            t.printStackTrace(writer);
            out.println("!!]");
            out.println("! STOP " + timestamp());
            out.close();
            
            t.printStackTrace();
        }
    }
    
    private void execute(File file, PrintWriter writer) throws IOException, PreconditionException {
        currentConfigurationString = file.getName() + "_" + "LFUP";
        
        if (finishedConfigurationStrings.contains(currentConfigurationString)) {
            return;
        }
        
        long startTime = System.nanoTime();
        
        
        StringBuilder line = new StringBuilder(currentConfigurationString);
        line.append('\t').append(file.getName());
        line.append('\t').append("LFUP");
        
//        FileInputStream in = new FileInputStream(file);
//        GmlReader reader = new GmlReader();
//        Graph graph = new FastGraph();
//        reader.read(in, graph);
//        graph.setString("hbgf", HBGF_FILE);
//        graph.setBoolean(SugiyamaConstants.PATH_HASBEENDECYCLED, true);
//        
//        int nodeCount = graph.getNumberOfNodes();
//        int edgeCount = graph.getNumberOfEdges();
//        double density = edgeCount / (double) nodeCount;
        
        System.out.print("Processing " + currentConfigurationString + "...");
        
        // Execute
        
        File logFile = new File("tmp.log");
        ProcessBuilder builder = new ProcessBuilder("/opt/wine-1.0/wine", "LFUP.exe", file.getCanonicalPath(), "tmp.gml", "tmp.log", "20");
        Process process = builder.start();
        while (true) {
            try {
                process.waitFor();
                break;
            } catch (InterruptedException e) {
                System.out.println("Interrupted: " + e);
            }
        }
        String logLine = extractLine(logFile);
        if (logFile.exists()) {
            logFile.delete();
        }
        //System.out.println("XX: " + logLine);
        
        int nodeCount = Integer.valueOf(extract(logLine, LOG_NODE_COUNT));
        int edgeCount = Integer.valueOf(extract(logLine, LOG_EDGE_COUNT));
        double density = edgeCount / (double) nodeCount;
        int crossingCount = Integer.valueOf(extract(logLine, LOG_CROSSING_COUNT));
        double time = Double.valueOf(extract(logLine, LOG_TIME));
        int levelCount = Integer.valueOf(extract(logLine, LOG_LEVEL_COUNT));
        
        line.append('\t').append(nodeCount);
        line.append('\t').append(edgeCount);
        line.append('\t').append(density);
        
        line.append('\t').append("NULL"); //results.get(SugiyamaData.INITIAL_CROSSING_COUNT));
        line.append('\t').append(crossingCount);
        line.append('\t').append(time);
        line.append('\t').append(levelCount);
        line.append('\t').append("NULL");//levelCount - levelReduction);
        
        writer.println(line);
        
        currentConfigurationString = null;
        System.out.println(String.format(" Done (%.2fs).", (System.nanoTime() - startTime) / 1000000000.0));
    }
    
    private void determineFinishedConfigurations(File resultFile) throws IOException {
        if (!resultFile.exists()) {
            resultFile.createNewFile();
        }
        
        BufferedReader reader = new BufferedReader(new FileReader(resultFile));
        
        String line;
        
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) continue;
            
            if (line.charAt(0) != '!') {
                Matcher m = CONFIG_PATTERN.matcher(line);
                if (m.matches()) {
                    finishedConfigurationStrings.add(m.group(1));
                }
            }
        }
        
        reader.close();
    }
    
    private String timestamp() {
        return String.format("%1$td.%1$tm.%1$tY %1$TT", new GregorianCalendar());
    }
    
    private String extractLine(File file) throws IOException {
        return new BufferedReader(new FileReader(file)).readLine();
    }
    
    private String extract(String str, Pattern pattern) {
        Matcher m = pattern.matcher(str);
        
        if (m.matches()) {
            return m.group(1);
        } else {
            throw new IllegalArgumentException("String \"" + str + "\" does not match pattern \"" + pattern.pattern() + "\"");
        }
    }
}
