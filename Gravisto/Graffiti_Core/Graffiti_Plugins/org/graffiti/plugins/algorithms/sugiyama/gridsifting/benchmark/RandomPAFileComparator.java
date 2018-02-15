package org.graffiti.plugins.algorithms.sugiyama.gridsifting.benchmark;

import java.io.File;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RandomPAFileComparator implements Comparator<File> {

    private static final Pattern FILE_PATTERN =
        Pattern.compile("randomPA_n(\\d{3})_e(\\d{4})_i(\\d)\\.gml");
    
    @Override
    public int compare(File o1, File o2) {
        String n1 = o1.getName();
        String n2 = o2.getName();
        Matcher m1 = FILE_PATTERN.matcher(n1);
        Matcher m2 = FILE_PATTERN.matcher(n2);
        if (!m1.matches()) {
            if (!m2.matches()) {
                return n1.compareTo(n2);
            } else {
                return -1;
            }
        } else {
            if (!m2.matches()) {
                return 1;
            }
        }
        
        int nodeCount1 = Integer.valueOf(m1.group(1));
        int nodeCount2 = Integer.valueOf(m2.group(1));
        int edgeCount1 = Integer.valueOf(m1.group(2));
        int edgeCount2 = Integer.valueOf(m2.group(2));
        int density1 = edgeCount1 * 5 / nodeCount1;
        int density2 = edgeCount2 * 5/ nodeCount2;
        
        if (density1 < density2) {
            return -1;
        } else if (density1 > density2) {
            return 1;
        }
        
        if (nodeCount1 < nodeCount2) {
            return -1;
        } else if (nodeCount1 > nodeCount2) {
            return 1;
        }
        
        int index1 = Integer.valueOf(m1.group(3));
        int index2 = Integer.valueOf(m2.group(3));
        
        if (index1 < index2) {
            return -1;
        } else if (index1 > index2) {
            return 1;
        }
        
        return 0;
    }
}
