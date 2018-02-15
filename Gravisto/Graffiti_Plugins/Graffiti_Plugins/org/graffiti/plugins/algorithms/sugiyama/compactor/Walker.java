// =============================================================================
//
//   Walker.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.compactor;

import java.util.LinkedList;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class Walker {
    private boolean[] visited;
    
    private LinkedList<Step> currentWalk;
    private LevelNode nextStartLevel;
    
    private WayControl wayControl;
    
    public Walker(LevelNode firstLevel, int nodesCount) {
        visited = new boolean[nodesCount];
        nextStartLevel = firstLevel;
        wayControl = new WayControl();
    }
    
    public LinkedList<Step> findWalk() {
        currentWalk = new LinkedList<Step>();
        boolean found = false;
        
        while (!found && nextStartLevel != null) {
            found = walk(nextStartLevel);
            if (found) currentWalk.addFirst(new StartStep(nextStartLevel));
            nextStartLevel = nextStartLevel.southLevel;
        }
        
        if (found) {
            wayControl.save();
            return currentWalk;
        } else {
            return null;
        }
    }
    
    private boolean walk(LevelNode currentLevel) {
        AnyNode currentNode = currentLevel.first;
        
        boolean found = tryNorth(currentLevel, currentNode);
        
        if (found) return true;
        
        found = tryEast(currentLevel, currentNode);
        
        if (found) return true;
        
        found = trySouth(currentLevel, currentNode);
        
        return found;
    }
    
    private boolean tryNorth(LevelNode currentLevel, AnyNode currentNode) {
        LevelNode northLevel = currentLevel.northLevel;
        
        Range range = wayControl.getStaticNorthRange(currentLevel, currentNode);
        
        if (range == null) return false;
        
        RealNode node = range.minNode;
        
        while (node != null && node.x <= range.maxNode.x) {
            if (!visited[node.index] && wayControl.isNorthReachable(northLevel, currentNode, node)) {
                boolean found = walkNorth(currentLevel, currentNode, node);
                if (found) return true;
            }
            node = node.east;
        }
        
        return false;
    }
    
    private boolean tryEast(LevelNode currentLevel, AnyNode currentNode) {
        RealNode toNode = currentNode.east;
        
        if (toNode.getType() == NodeType.Dummy && !visited[toNode.index]) {
            return walkEast(currentLevel, currentNode);
        } else {
            return false;
        }
    }
    
    private boolean trySouth(LevelNode currentLevel, AnyNode currentNode) {
        Range range = wayControl.getStaticSouthRange(currentLevel, currentNode);
        
        if (range == null) return false;
        
        RealNode node = range.maxNode;
        
        while (node != null && node.x >= range.minNode.x) {
            if (!visited[node.index] && wayControl.isSouthReachable(currentLevel, currentNode, node)) {
                boolean found = walkSouth(currentLevel, currentNode, node);
                if (found) return true;
            }
            node = node.west;
        }
        
        return false;
    }
    
    private boolean tryWest(LevelNode currentLevel, RealNode currentNode) {
        return false;//TODO
//        RealNode toNode = currentNode.west;
//        
//        if (toNode == null || visited[toNode.index]
//                || currentNode.getType() != NodeType.Dummy)
//            return false;
//        
//        return walkWest(currentLevel, (DummyNode) currentNode);
    }
    
    // walk* assume that the move is actually possible.
    
    private boolean walkNorth(LevelNode fromLevel, AnyNode fromNode, RealNode toNode) {
        LevelNode northLevel = fromLevel.northLevel;
        currentWalk.addLast(new NorthStep(fromNode, toNode));
        wayControl.add(northLevel, toNode, fromNode);
        visited[toNode.index] = true;
        
        if (toNode.east == null) {
            return true;
        }
        
        boolean found = tryWest(northLevel, toNode);
        
        if (found) return true;
        
        found = tryNorth(northLevel, toNode);
        
        if (found) return true;
        
        found = tryEast(northLevel, toNode);
        
        if (found) return true;
        
        wayControl.remove(northLevel, toNode, fromNode);
        currentWalk.removeLast();
        return false;
    }
    
    private boolean walkEast(LevelNode currentLevel, AnyNode fromNode) {
        DummyNode toNode = (DummyNode) fromNode.east;
        currentWalk.addLast(new EastStep(toNode));
        visited[toNode.index] = true;
        
        if (toNode.east == null) {
            return true;
        }
        
        boolean found = tryNorth(currentLevel, toNode);
        
        if (found) return true;
        
        found = tryEast(currentLevel, toNode);
        
        if (found) return true;
        
        found = trySouth(currentLevel, toNode);
        
        if (found) return true;
        
        currentWalk.removeLast();
        
        return false;
    }
    
    private boolean walkSouth(LevelNode fromLevel, AnyNode fromNode, RealNode toNode) {
        LevelNode southLevel = fromLevel.southLevel;
        currentWalk.addLast(new SouthStep(fromNode, toNode));
        wayControl.add(fromLevel, fromNode, toNode);
        visited[toNode.index] = true;
        
        if (toNode.east == null) {
            return true;
        }
        
        boolean found = tryEast(southLevel, toNode);
        
        if (found) return true;
        
        found = trySouth(southLevel, toNode);
        
        if (found) return true;
        
        found = tryWest(southLevel, toNode);
        
        if (found) return true;
        
        wayControl.remove(fromLevel, fromNode, toNode);
        currentWalk.removeLast();
        return false;
    }
    
//    private boolean walkWest(LevelNode currentLevel, DummyNode fromNode) {
//        RealNode toNode = fromNode.west;
//        currentWalk.addLast(new WestStep(fromNode));
//        visited[toNode.index] = true;
//        
//        boolean found = trySouth(currentLevel, toNode);
//        
//        if (found) return true;
//        
//        found = tryWest(currentLevel, toNode);
//        
//        if (found) return true;
//        
//        found = tryNorth(currentLevel, toNode);
//        
//        if (found) return true;
//        
//        currentWalk.removeLast();
//        
//        return false;
//    }
}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
