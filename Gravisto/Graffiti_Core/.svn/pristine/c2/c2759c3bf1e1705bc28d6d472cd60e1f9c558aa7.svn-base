// =============================================================================
//
//   HbgfToLgfConverter.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.ios.hbgf;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class HbgfToLgfConverter {
    
    public static void main(String[] args) {
        try {
            new HbgfToLgfConverter().convert(new File(args[0]), new File(args[1]));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private class MiniNode implements Comparable<MiniNode> {
        private int phi;
        private int pi;
        private int index;
        
        private ArrayList<MiniNode> outNeighbors;
        
        private MiniNode(int phi, int pi) {
            this.phi = phi;
            this.pi = pi;
            outNeighbors = new ArrayList<MiniNode>();
        }
        
        private void addOut(MiniNode node) {
            outNeighbors.add(node);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int compareTo(MiniNode o) {
            return Integer.valueOf(pi).compareTo(o.pi);
        }
    }
    
    public void convert(File hbgfFile, File lgfFile) throws IOException {
        DataInputStream in = new DataInputStream(new FileInputStream(hbgfFile));
        String signature = in.readUTF();
        if (!signature.equals("hgbf") && !signature.equals("hbgf")) throw new IOException("Invalid signature");
        // String origName =
        in.readUTF();
        // int configIndex =
        in.readInt();
        int nodeCount = in.readInt();
        int edgeCount = in.readInt();
        
        SortedMap<Integer, ArrayList<MiniNode>> levels = new TreeMap<Integer, ArrayList<MiniNode>>();
        
        MiniNode[] realNodes = new MiniNode[nodeCount];
        
        for (int i = 0; i < nodeCount; i++) {
            int phi = in.readInt();
            int pi = in.readInt();
            
            MiniNode node = new MiniNode(phi, pi);
            realNodes[i] = node;
            
            ArrayList<MiniNode> level = levels.get(phi);
            
            if (level == null) {
                level = new ArrayList<MiniNode>();
                levels.put(phi, level);
            }
            
            level.add(node);
        }
        
        for (int i = 0; i < edgeCount; i++) {
            int sourceIndex = in.readInt();
            int targetIndex = in.readInt();
            int pi = in.readInt();
            MiniNode source = realNodes[sourceIndex];
            MiniNode target = realNodes[targetIndex];
            
            MiniNode lastNode = source;
            
            while (lastNode.phi < target.phi - 1) {
                int phi = lastNode.phi + 1;
                MiniNode dummyNode = new MiniNode(phi, pi);
                
                ArrayList<MiniNode> level = levels.get(phi);
                if (level == null) {
                    level = new ArrayList<MiniNode>();
                    levels.put(phi, level);
                }
                
                level.add(dummyNode);
                
                lastNode.addOut(dummyNode);
                lastNode = dummyNode;
            }
            
            lastNode.addOut(target);
        }
        
        for (ArrayList<MiniNode> level : levels.values()) {
            Collections.sort(level);
            
            int i = 0;
            
            for (MiniNode node : level) {
                node.index = i;
                i++;
            }
        }
        
        for (MiniNode node : realNodes) {
            Collections.sort(node.outNeighbors);
        }
        
        PrintWriter out = new PrintWriter(lgfFile);
        int levelCount = levels.size();
        out.println(levelCount);
        
        Iterator<ArrayList<MiniNode>> levelIterator = levels.values().iterator();
        
        for (int i = 0; i < levelCount - 1; i++) {
            ArrayList<MiniNode> level = levelIterator.next();
            int levelEdgeCount = 0;
            
            for (MiniNode node : level) {
                levelEdgeCount += node.outNeighbors.size();
            }
            
            out.println(levelEdgeCount);
        }
        
        for (ArrayList<MiniNode> level : levels.values()) {
            out.println(level.size());
        }
        
        for (ArrayList<MiniNode> level : levels.values()) {
            for (MiniNode source : level) {
                for (MiniNode target : source.outNeighbors) {
                    out.println(source.index + " " + target.index);
                }
            }
        }
        
        out.close();
    }
}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
