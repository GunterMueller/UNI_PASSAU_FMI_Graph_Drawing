package org.graffiti.plugins.tools.math;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Andreas Glei&szlig;ner
 */
public class PermutationNaming {
    
    int length;
    
    Map<String, Integer> ciMap;
    Map<Integer, String> icMap;
    
    public PermutationNaming(String identity) {
        String[] tokens = tokenize(identity);
        length = tokens.length;
        
        ciMap = new HashMap<String, Integer>();
        icMap = new HashMap<Integer, String>();
        
        for (int i = 0; i < length; i++) {
            if (ciMap.containsKey(tokens[i])) throw new IllegalArgumentException();
            ciMap.put(tokens[i], i);
            icMap.put(i, tokens[i]);
        }
    }
    
    public int getElement(String id) {
        return ciMap.get(id);
    }
    
    public String getElement(int i) {
        return icMap.get(i);
    }
    
    public Permutation get(String id) {
        String[] tokens = tokenize(id);
        if (tokens.length != length) throw new IllegalArgumentException();
        
        int[] array = new int[length];
        
        for (int i = 0; i < length; i++) {
            array[i] = ciMap.get(tokens[i]);
        }
        
        return new Permutation(true, array);
    }
    
    public String get(Permutation perm) {
        if (perm.getLength() != length) throw new IllegalArgumentException();
        
        StringBuilder builder = new StringBuilder("[");
        
        builder.append(icMap.get(perm.get(0)));
        
        for (int i = 1; i < length; i++) {
            builder.append(" ").append(icMap.get(perm.get(i)));
        }
        
        builder.append("]");
        
        return builder.toString();
    }
    
    public int getLength() {
        return length;
    }
    
    private String[] tokenize(String id) {
        return id.replaceAll("[\\[\\]]", "").split(" ");
    }
}
