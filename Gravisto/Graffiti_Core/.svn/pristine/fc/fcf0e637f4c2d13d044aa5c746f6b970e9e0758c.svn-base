package de.uni_passau.fim.br.planarity.mdeque;

import de.uni_passau.fim.br.planarity.graph.Vertex;
import de.uni_passau.fim.br.planarity.gravisto.Tabulator;

public class EdgeNode extends DequeNode {
    private final Vertex source;
    
    private final Vertex target;
    
    public EdgeNode(Vertex source, Vertex target) {
        this.source = source;
        this.target = target;
        extracted = false;
    }
    
    @Override
    public LockResult triggerExtraction(int timeStamp, DequeNode child) throws DequeException {
        Tabulator.out.println("extracting " + this + "...");
        if (!extracted && parent != null) {
            parent.triggerExtraction(timeStamp, this);
        }
        return null;
    }

    
    @Override
    protected void releaseAll() {
        extracted = true;
        Tabulator.out.println("release " + this);
    }

    public void mark() {
        if (parent != null) {
            parent.mark(this);
        }
    }

    @Override
    public String toString() {
        return source + " -- " + target;
    }

    @Override
    public boolean isEmpty() {
        return extracted;
    }
}
