package de.uni_passau.fim.br.planarity.mdeque;

final class LockResult {
    public final Side side;
    
    public final MergeNode mutableNode;
    
    public LockResult(Side side, MergeNode mutableNode) {
        this.side = side;
        this.mutableNode = mutableNode;
    }
    
    public LockResult flip() {
        return new LockResult(side.flip(), mutableNode);
    }
    
//    public LockResult mutate(Boolean flip) {
//        return new LockResult(side.mutate(flip), mutableNode);
//    }
    
    public LockResult dropMutableNode() {
        return new LockResult(side, null);
    }
}
