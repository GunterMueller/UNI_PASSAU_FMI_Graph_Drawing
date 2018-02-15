package org.graffiti.plugins.algorithms.sugiyama.gridsifting.benchmark;

import java.io.IOException;


public class Starter {
    public static void main(String[] args) throws IOException {
        if (args.length > 0 && args[0].equals("-generate")) {
            String[] args2 = new String[args.length - 1];
            System.arraycopy(args, 1, args2, 0, args.length - 1);
            RandomPA.main(args2);
        } else {
            Benchmark.main(args);
        }
    }
}
