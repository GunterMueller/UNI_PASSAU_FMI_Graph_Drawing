package de.uni_passau.fim.br.planarity.mdeque;

public enum Side {
    NONE,
    LEFT,
    RIGHT;
    
    public Side mutate(Boolean flip) {
        return flip ? flip() : this;
    }
    
    public Side flip() {
        switch (this) {
        case NONE:
            return NONE;
        case LEFT:
            return RIGHT;
        case RIGHT:
            return LEFT;
        default:
            throw new AssertionError();
        }
    }
}
