// =============================================================================
//
//   WayControl.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.compactor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class WayControl {
    private static class DynamicBarrier {
        protected AnyNode north;
        protected AnyNode south;
        
        public DynamicBarrier(AnyNode north, AnyNode south) {
            this.north = north;
            this.south = south;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof DynamicBarrier)) return false;
            
            DynamicBarrier other = (DynamicBarrier) obj;
            
            return north.index == other.north.index && south.index == other.south.index;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return Integer.rotateLeft(north.index, 16) ^ south.index;
        }
    }
    
    private Map<LevelNode, Set<DynamicBarrier>> staticBarriers;
    private Map<LevelNode, Set<DynamicBarrier>> dynamicBarriers;
    
    public WayControl() {
        staticBarriers = new HashMap<LevelNode, Set<DynamicBarrier>>();
        dynamicBarriers = new HashMap<LevelNode, Set<DynamicBarrier>>();
    }
    
    public void add(LevelNode northLevel, AnyNode north, AnyNode south) {
        Set<DynamicBarrier> set = dynamicBarriers.get(northLevel);
        
        if (set == null) {
            set = new HashSet<DynamicBarrier>();
            dynamicBarriers.put(northLevel, set);
        }
        
        set.add(new DynamicBarrier(north, south));
    }
    
    public void remove(LevelNode northLevel, AnyNode north, AnyNode south) {
        Set<DynamicBarrier> set = dynamicBarriers.get(northLevel);
        set.remove(new DynamicBarrier(north, south));
    }
    
    public void save() {
        for (Map.Entry<LevelNode, Set<DynamicBarrier>> entry : dynamicBarriers.entrySet()) {
            Set<DynamicBarrier> set = staticBarriers.get(entry.getKey());
            
            if (set == null) {
                staticBarriers.put(entry.getKey(), entry.getValue());
            } else {
                set.addAll(entry.getValue());
            }
        }
        
        dynamicBarriers.clear();
    }
    
    public Range getStaticNorthRange(LevelNode currentLevel, AnyNode currentNode) {
        LevelNode northLevel = currentLevel.northLevel;
        if (northLevel == null) return null;
        
        RealNode minNode = northLevel.first.east;
        RealNode maxNode = northLevel.last;
        
        if (currentNode.getType() != NodeType.Start) {
            RealNode ln = (RealNode) currentNode;
            
            while (ln != null) {
                RealNode[] north = ln.getNorth();
                
                int len = north.length;
                
                if (len != 0) {
                    RealNode node = north[len - 1];
                    
                    if (node.x > minNode.x) minNode = node;
                }

                ln = ln.west;
            }
        }
        
        RealNode rn = currentNode.east;
        
        while (rn != null) {
            RealNode[] north = rn.getNorth();
            
            int len = north.length;
            
            if (len != 0) {
                RealNode node = north[0].west;
                
                if (node == null) return null;
                
                if (node.x < maxNode.x) maxNode = node;
                
                if (minNode.x > maxNode.x) return null;
            }
            
            rn = rn.east;
        }
        
        Set<DynamicBarrier> barrierSet = staticBarriers.get(northLevel);
        
        if (barrierSet != null) {
            for (DynamicBarrier barrier : staticBarriers.get(northLevel)) {
                if (barrier.south.x < currentNode.x) {
                    RealNode node = barrier.north.east;
                    
                    if (node == null) return null;
                    
                    if (node.x > minNode.x) minNode = node;
                } else if (barrier.south.x > currentNode.x) {
                    AnyNode anyNode = barrier.north;
                    if (anyNode.getType() == NodeType.Start) return null;
                    
                    RealNode node = ((RealNode) anyNode).west;
                    
                    if (node == null) return null;
                    
                    if (node.x < maxNode.x) maxNode = node;
                    
                    if (minNode.x > maxNode.x) return null;
                }
            }
        }
        
        if (minNode.x > maxNode.x) {
            return null;
        } else {
            return new Range(minNode, maxNode);
        }
    }
    
    public boolean isNorthReachable(LevelNode northLevel, AnyNode fromNode, AnyNode toNode) {
        Set<DynamicBarrier> barrierSet = dynamicBarriers.get(northLevel);
        
        if (barrierSet != null) {
            for (DynamicBarrier barrier : dynamicBarriers.get(northLevel)) {
                if (barrier.south.x < fromNode.x) {
                    RealNode node = barrier.north.east;
                    
                    if (node == null || node.x > toNode.x) return false;
                } else if (barrier.south.x > fromNode.x) {
                    AnyNode anyNode = barrier.north;
                    
                    if (anyNode.getType() == NodeType.Start) return false;
                    
                    RealNode node = ((RealNode) anyNode).west;
                    
                    if (node == null || node.x < toNode.x) return false;
                }
            }
        }
        
        return true;
    }
    
    public Range getStaticSouthRange(LevelNode currentLevel, AnyNode currentNode) {
        LevelNode southLevel = currentLevel.southLevel;
        if (southLevel == null) return null;
        
        RealNode minNode = southLevel.first.east;
        RealNode maxNode = southLevel.last;
        
        if (currentNode.getType() != NodeType.Start) {
            RealNode ln = (RealNode) currentNode;
            
            while (ln != null) {
                RealNode[] south = ln.getSouth();
                
                int len = south.length;
                
                if (len != 0) {
                    RealNode node = south[len - 1];
                    
                    if (node.x > minNode.x) minNode = node;
                }

                ln = ln.west;
            }
        }
        
        RealNode rn = currentNode.east;
        
        while (rn != null) {
            RealNode[] south = rn.getSouth();
            
            int len = south.length;
            
            if (len != 0) {
                RealNode node = south[0].west;
                
                if (node == null) return null;
                
                if (node.x < maxNode.x) maxNode = node;
                
                if (minNode.x > maxNode.x) return null;
            }
            
            rn = rn.east;
        }
        
        Set<DynamicBarrier> barrierSet = staticBarriers.get(currentLevel);
        
        if (barrierSet != null) {
            for (DynamicBarrier barrier : barrierSet) {
                if (barrier.north.x < currentNode.x) {
                    RealNode node = barrier.south.east;
                    
                    if (node == null) return null;
                    
                    if (node.x > minNode.x) minNode = node;
                } else if (barrier.north.x > currentNode.x) {
                    AnyNode anyNode = barrier.south;
                    if (anyNode.getType() == NodeType.Start) return null;
                    
                    RealNode node = ((RealNode) anyNode).west;
                    
                    if (node == null) return null;
                    
                    if (node.x < maxNode.x) maxNode = node;
                    
                    if (minNode.x > maxNode.x) return null;
                }
            }
        
        }
        
        if (minNode.x > maxNode.x) {
            return null;
        } else {
            return new Range(minNode, maxNode);
        }
    }
    
    public boolean isSouthReachable(LevelNode northLevel, AnyNode fromNode, AnyNode toNode) {
        Set<DynamicBarrier> barrierSet = dynamicBarriers.get(northLevel);
        
        if (barrierSet != null) {
            for (DynamicBarrier barrier : dynamicBarriers.get(northLevel)) {
                if (barrier.north.x < fromNode.x) {
                    RealNode node = barrier.south.east;
                    
                    if (node == null || node.x > toNode.x) return false;
                } else if (barrier.north.x > fromNode.x) {
                    AnyNode anyNode = barrier.south;
                    
                    if (anyNode.getType() == NodeType.Start) return false;
                    
                    RealNode node = ((RealNode) anyNode).west;
                    
                    if (node == null || node.x < toNode.x) return false;
                }
            }
        }
        
        return true;
    }
}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
