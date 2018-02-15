package de.uni_passau.fim.br.planarity.mdeque;

import static de.uni_passau.fim.br.planarity.mdeque.Side.LEFT;
import static de.uni_passau.fim.br.planarity.mdeque.Side.NONE;
import static de.uni_passau.fim.br.planarity.mdeque.Side.RIGHT;

class TwoRegister {
    private DequeNode left;
    
    private DequeNode right;
    
    public void release(DequeNode node) {
        if (node == left) {
            left = null;
        } else if (node == right) {
            right = null;
        }
    }
    
    public Side find(DequeNode node) {
        if (node == left) {
            return LEFT;
        } else if (node == right) {
            return RIGHT;
        } else {
            return NONE;
        }
    }
    
    public DequeNode get(Side side) {
        if (side == LEFT) {
            return left;
        } else if (side == RIGHT) {
            return right;
        } else {
            throw new IllegalArgumentException();
        }
    }
    
    public void put(Side side, DequeNode node) {
        assert find(node) == NONE;
        
        if (side == LEFT) {
            left = node;
        } else if (side == RIGHT) {
            right = node;
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public String toString() {
        return "<" + left + " | " + right + ">";
    }
}
