package de.uni_passau.fim.br.planarity.mdeque;


public abstract class DequeNode {
    protected MergeNode parent;
    
    protected boolean extracted;
    
    public abstract LockResult triggerExtraction(int timeStamp, DequeNode child) throws DequeException;
    
    protected abstract void releaseAll();
    
    public abstract boolean isEmpty();
}
