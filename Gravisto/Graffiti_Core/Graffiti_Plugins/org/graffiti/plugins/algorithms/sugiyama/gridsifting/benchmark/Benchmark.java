package org.graffiti.plugins.algorithms.sugiyama.gridsifting.benchmark;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.graffiti.graph.FastGraph;
import org.graffiti.graph.Graph;
import org.graffiti.plugin.algorithm.Algorithm;
import org.graffiti.plugin.algorithm.AlgorithmResult;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringParameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.plugins.algorithms.sugiyama.compactor.CompactorAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.crossmin.BaryCenter;
import org.graffiti.plugins.algorithms.sugiyama.crossmin.DummyCrossMin;
import org.graffiti.plugins.algorithms.sugiyama.crossmin.global.GlobalSifting;
import org.graffiti.plugins.algorithms.sugiyama.decycling.DummyDecycling;
import org.graffiti.plugins.algorithms.sugiyama.gridsifting.UniversalSiftingAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.layout.DummyLayout;
import org.graffiti.plugins.algorithms.sugiyama.levelling.CoffmanGraham;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;
import org.graffiti.plugins.ios.gml.gmlReader.GmlReader;
import org.graffiti.plugins.ios.hbgf.HbgfReader;


public class Benchmark {
    
    public static interface Configuration {
        Algorithm getLevellingAlgorithm(long seed);
        Algorithm getCrossminAlgorithm();
    }
    
    public static abstract class OtherConfiguration implements Configuration {
        @Override
        public Algorithm getLevellingAlgorithm(long seed) {
            CoffmanGraham coffmanGraham = new CoffmanGraham();
            Parameter<?>[] parameters = coffmanGraham.getAlgorithmParameters();
            ((IntegerParameter) parameters[0]).setValue(1);
            coffmanGraham.setAlgorithmParameters(parameters);
            return coffmanGraham;
        }
    }
    
    public static abstract class USConfiguration implements Configuration {
        public Algorithm getLevellingAlgorithm(long seed) {
            Algorithm algorithm = new UniversalSiftingAlgorithm();
            Parameter<?>[] parameters = algorithm.getParameters();
            
            // seed
            ((StringParameter) parameters[0]).setValue(String.valueOf(seed));
            
            // rounds
            ((IntegerParameter) parameters[3]).setValue(8);
            
            // initial levelling
            ((StringSelectionParameter) parameters[4]).setValue("CoffmanGraham");
            
            // level width for CoffmanGraham
            ((IntegerParameter) parameters[5]).setValue(1);
            
            // level radius
            ((IntegerParameter) parameters[6]).setValue(getLevelRadius());
            
            algorithm.setParameters(parameters);
            return algorithm;
        }
        
        public Algorithm getCrossminAlgorithm() {
            Algorithm algorithm = new DummyCrossMin();
            return algorithm;
        }
        
        protected abstract int getLevelRadius();
    }
    
    private static final String GRAPH_DIR = "/tmp/gleissne/randomPA2";
    private static final String RESULT_FILE = "/tmp/gleissne/results.csv";
    private static final String HBGF_FILE = "/tmp/gleissne/randomPA2/hbgf.hbgf";
    //private static final Pattern DENSITY_PATTERN = Pattern.compile("-d([\\d\\.\\,]+)");
    private static final Pattern INSTANCE_PATTERN = Pattern.compile("-i([\\d\\,]+)");
    
    
    private static final Pattern CONFIG_PATTERN = Pattern.compile("([^\\t]+)\\t.*");
    
//    private static Pattern RANDOM_PA_PATTERN = Pattern.compile(".*\\_n(\\d+)\\_e(\\d+)\\_i(\\d+)\\.gml");
    
    private static Pattern RANDOM2_PA_PATTERN = Pattern.compile(".*\\_n(\\d+)\\_e(\\d+)\\_i(\\d+)\\.gml");
    
//    private static final Pattern ROME_PATTERN = Pattern.compile(".*grafo(\\d).*\\.gml"); 
    
    private HashMap<String, Configuration> configurations;
    
    private HashSet<String> finishedConfigurationStrings;
    
    private PrintWriter writer;
    
    private String currentConfigurationString;
    
//    private Set<Double> densities;
    private Set<Integer> instances;
    
    public static void main(String[] args) {
        Benchmark benchmark = new Benchmark(args);
        
        benchmark.add("BC", new OtherConfiguration() {
            @Override
            public Algorithm getCrossminAlgorithm() {
                BaryCenter baryCenter = new BaryCenter();
                Parameter<?>[] parameters = baryCenter.getAlgorithmParameters();
                ((IntegerParameter) parameters[0]).setValue(400);
                baryCenter.setAlgorithmParameters(parameters);
                return baryCenter;
            }
        });
        
        benchmark.add("GlS", new OtherConfiguration() {
            @Override
            public Algorithm getCrossminAlgorithm() {
                GlobalSifting globalSifting = new GlobalSifting();
                Parameter<?>[] parameters = globalSifting.getAlgorithmParameters();
                ((IntegerParameter) parameters[1]).setValue(400);
                globalSifting.setAlgorithmParameters(parameters);
                return globalSifting;
            }
        });
        
        benchmark.add("GrS3", new USConfiguration() {
            @Override
            protected int getLevelRadius() {
                return 3;
            }
        });
        
        benchmark.add("GrS10", new USConfiguration() {
            @Override
            protected int getLevelRadius() {
                return 10;
            }
        });
        
        benchmark.add("GrS21", new USConfiguration() {
            @Override
            protected int getLevelRadius() {
                return 21;
            }
        });
        
        benchmark.add("GrS*", new USConfiguration() {
            @Override
            public int getLevelRadius() {
                return 1000000;
            }
        });
        
        benchmark.execute(new File(GRAPH_DIR), new File(RESULT_FILE));
    }
    
    public Benchmark(String[] args) {
        configurations = new LinkedHashMap<String, Benchmark.Configuration>();
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
    
    public void add(String id, Configuration configuration) {
        configurations.put(id, configuration);
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
                
                if (!m.matches()) continue;
                
//                int nodeCount = Integer.valueOf(m.group(1));
//                int edgeCount = Integer.valueOf(m.group(2));
//                
//                if (!densities.contains(edgeCount / (double) nodeCount)) continue;
                
                int index = Integer.valueOf(m.group(3));
                
                if (!instances.contains(index)) continue;
                
//                Matcher m = ROME_PATTERN.matcher(file.getName());
//                
//                if (!m.matches()) continue;
//                
//                int index = Integer.valueOf(m.group(1));
//                
//                if (!instances.contains(index)) continue;
                
                for (Map.Entry<String, Configuration> entry : configurations.entrySet()) {
                    execute(file, entry, writer);
                }
            }
            
            writer.println("! STOP " + timestamp());
            
            writer.close();
            writer = null;
        } catch (Throwable t) {
            if (writer != null) {
                writer.println("!![");
             
                if (currentConfigurationString != null) {
                    writer.print("Exception on processing ");
                    writer.print(currentConfigurationString);
                    writer.println(':');
                }
                
                t.printStackTrace(writer);
                writer.println("!!]");
                writer.println("! STOP " + timestamp());
                writer.close();
                writer = null;
            }
            
            t.printStackTrace();
        }
    }
    
    private void execute(File file, Map.Entry<String, Configuration> entry, PrintWriter writer) throws IOException, PreconditionException {
        currentConfigurationString = file.getName() + "_" + entry.getKey();
        
        if (finishedConfigurationStrings.contains(currentConfigurationString)) {
            return;
        }
        
        long startTime = System.nanoTime();
        
        Configuration configuration = entry.getValue();
        
        StringBuilder line = new StringBuilder(currentConfigurationString);
        line.append('\t').append(file.getName());
        line.append('\t').append(entry.getKey());
        
        FileInputStream in = new FileInputStream(file);
        GmlReader reader = new GmlReader();
        Graph graph = new FastGraph();
        reader.read(in, graph);
        graph.setString("hbgf", HBGF_FILE);
        graph.setBoolean(SugiyamaConstants.PATH_HASBEENDECYCLED, true);
        
        int nodeCount = graph.getNumberOfNodes();
        int edgeCount = graph.getNumberOfEdges();
        double density = edgeCount / (double) nodeCount;
        
        System.out.print("Processing " + currentConfigurationString + "...");
        
        line.append('\t').append(nodeCount);
        line.append('\t').append(edgeCount);
        line.append('\t').append(density);
        Random random = new Random(currentConfigurationString.hashCode());
        
        SugiyamaBenchmarkAdapter sugiyama = new SugiyamaBenchmarkAdapter();
        
        Map<String, Algorithm> map = new HashMap<String, Algorithm>();
        map.put("decycling", new DummyDecycling());
        map.put("levelling", configuration.getLevellingAlgorithm(random.nextLong()));
        map.put("crossmin", configuration.getCrossminAlgorithm());
        map.put("layout", new DummyLayout());
        
        sugiyama.setNestedAlgorithms(map);
        sugiyama.reset();
        Parameter<?>[] parameters = sugiyama.getAlgorithmParameters();
        sugiyama.setAlgorithmParameters(parameters);
        sugiyama.attach(graph);
        sugiyama.check();
        sugiyama.execute();
        
        AlgorithmResult result = sugiyama.getResult();
        Map<String, Object> results = result.getResult();
        
        line.append('\t').append(results.get(SugiyamaData.INITIAL_CROSSING_COUNT));
        line.append('\t').append(results.get(SugiyamaData.CROSSING_COUNT));
        line.append('\t').append(results.get(SugiyamaBenchmarkAdapter.CROSSMIN_TIME_KEY));
        
        Object levelCountObj = results.get(SugiyamaBenchmarkAdapter.LEVEL_COUNT_KEY);
        int levelCount = levelCountObj == null ? nodeCount : (Integer) levelCountObj;


        int levelReduction = 0;
        
        if (levelCountObj != null) {
            graph = new FastGraph();
            HbgfReader reader2 = new HbgfReader();
            in = new FileInputStream(new File(HBGF_FILE));
            reader2.read(in, graph);
            
            CompactorAlgorithm compactor = new CompactorAlgorithm();
            parameters = compactor.getParameters();
            compactor.setParameters(parameters);
            compactor.reset();
            compactor.attach(graph);
            compactor.check();
            compactor.execute();
        }
        
        line.append('\t').append(levelCount);
        line.append('\t').append(levelCount - levelReduction);
        
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
}
