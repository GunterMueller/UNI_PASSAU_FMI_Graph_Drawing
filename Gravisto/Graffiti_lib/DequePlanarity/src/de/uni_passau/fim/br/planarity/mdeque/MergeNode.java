package de.uni_passau.fim.br.planarity.mdeque;

import static de.uni_passau.fim.br.planarity.mdeque.Side.LEFT;
import static de.uni_passau.fim.br.planarity.mdeque.Side.NONE;
import static de.uni_passau.fim.br.planarity.mdeque.Side.RIGHT;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class MergeNode extends DequeNode {
    private String label;
    
    protected int childCount;
    
    private TwoRegister register;

    private Boolean flip;

    private Set<DequeNode> markedChildren;
    
    private int timeStamp;
    
    private LockResult cachedLockResult;

    public MergeNode(Collection<DequeNode> children, String label) {
        this.label = label;
        childCount = 0;
        register = new TwoRegister();

        for (DequeNode child : children) {
            if (child != null && !child.isEmpty()) {
                child.parent = this;
                childCount++;
            }
        }
    }


    private void release(DequeNode child) {
        register.release(child);
        
        child.releaseAll();
        
        childCount--;
    }
    
    @Override
    protected void releaseAll() {
        for (DequeNode node : markedChildren) {
            release(node);
        }
        extracted = true;
    }

    @Override
    public LockResult triggerExtraction(int timeStamp, DequeNode child) throws DequeException {
        if (extracted) return null;
        
        if (this.timeStamp != timeStamp) {
            this.timeStamp = timeStamp;
            cachedLockResult = null;
        }
        
        if (parent == null) {
            if (markedChildren != null && markedChildren.size() == childCount) {
                releaseAll();
                return null;
            } else {
                return lockAsRoot(child);
            }
        }
        
        LockResult result;

        cachedLockResult = null;// disable caching
        
        if (cachedLockResult == null) {
            result = parent.triggerExtraction(timeStamp, this);
            cachedLockResult = result != null ? result.dropMutableNode() : null;
        } else {
            result = cachedLockResult;
        }
        
        if (extracted) return null;
        
        // From now on we know that at least one of the children is not
        // extracted in this step.
        
        MergeNode mutableNode = null;
        
        if (flip == null) {
            flip = false;
            mutableNode = this;
        } else {
            mutableNode = result.mutableNode;
        }
        
        if (result.side == NONE) {
            return lockAsRoot(child);
        } else {
            return lock(child, result.side.mutate(flip), mutableNode);
        }
    }
    
    private LockResult lockAsRoot(DequeNode child) throws DequeException {
        if (childCount == 1) {
            return new LockResult(NONE, null);
        }
        
        // childCount >= 2
        
        if (markedChildren != null) {
            boolean childIsMarked = markedChildren.contains(child);
            
            DequeNode left = register.get(LEFT);
            DequeNode right = register.get(RIGHT);
            if (left != null && !markedChildren.contains(left) && right != null
                    && !markedChildren.contains(right)) {
                // Both sides are blocked.
                throw new DequeException();
            }
            
            for (DequeNode node : markedChildren) {
                release(node);
            }
            
            markedChildren = null;
            
            if (childIsMarked) {
                return null;
            } else if (childCount == 1) {
                return new LockResult(NONE, null);
            }
        }
        
        Side s = register.find(child);
        if (s == NONE) {
            s = register.find(null);
            if (s == NONE) {
                throw new DequeException();
            }
            register.put(s, child);
        }
        return new LockResult(s, null);
    }
    
    private LockResult lock(DequeNode child, Side effectiveSide, MergeNode mutableNode) throws DequeException {
        if (childCount == 1) {
            return new LockResult(effectiveSide, mutableNode);
        }
        
        // childCount >= 2
        
        if (markedChildren != null) {
            boolean childIsMarked = markedChildren.contains(child);
            
            Iterator<DequeNode> iter = markedChildren.iterator();
            while (iter.hasNext()) {
                DequeNode node = iter.next();
                
                Side s = register.find(node);
                if (s != NONE) {
                    if (s != effectiveSide) {
                        if (mutableNode != null) {
                            mutableNode.flip = true;
                            mutableNode = null;
                            effectiveSide = s;
                            cachedLockResult = cachedLockResult.flip();
                        } else {
                            throw new DequeException();
                        }
                    }
                    mutableNode = null;
                    iter.remove();
                    release(node);
                }
            }
            
            if (!markedChildren.isEmpty()) {
                if (register.get(effectiveSide) != null) {
                    if (mutableNode != null) {
                        mutableNode.flip = true;
                        mutableNode = null;
                        effectiveSide = effectiveSide.flip();
                        cachedLockResult = cachedLockResult.flip();
                        if (register.get(effectiveSide) != null) {
                            // Both sides are blocked by non-marked children.
                            throw new DequeException();
                        }
                    } else {
                        // The only possible side is blocked by a non-marked child.
                        throw new DequeException();
                    }
                } else if (register.get(effectiveSide.flip()) != null) {
                    // The effective side is free for extraction, but the other
                    // side is not, so you cannot flip later.
                    mutableNode = null;
                }
                
                for (DequeNode node : markedChildren) {
                    release(node);
                }
            }
            
            
            
            markedChildren = null;
            
            if (childIsMarked) {
                return null;
            } else if (childCount == 1) {
                return new LockResult(effectiveSide, mutableNode);
            }
        }
        
        
        Side s = register.find(child);
        if (s == NONE) {
            if (register.get(effectiveSide) != null) {
                if (mutableNode != null) {
                    mutableNode.flip = true;
                    mutableNode = null;
                    effectiveSide = effectiveSide.flip();
                    cachedLockResult = cachedLockResult.flip();
                    if (register.get(effectiveSide) != null) {
                        // Both sides are blocked.
                        throw new DequeException();
                    }
                } else {
                    // The only possible side is blocked.
                    throw new DequeException();
                }
            }
            register.put(effectiveSide, child);
        } else if (s != effectiveSide) {
            if (mutableNode != null) {
                mutableNode.flip = true;
                mutableNode = null;
                effectiveSide = effectiveSide.flip();
                cachedLockResult = cachedLockResult.flip();
            } else {
                throw new DequeException();
            }
        }
        
        return new LockResult(effectiveSide, null);
    }

    @Override
    public boolean isEmpty() {
        return childCount == 0;
    }

    protected void mark(DequeNode child) {
        if (markedChildren == null) {
            markedChildren = new HashSet<DequeNode>();
        }
        markedChildren.add(child);
        if (markedChildren.size() == childCount && parent != null) {
            parent.mark(this);
        }
    }

    @Override
    public String toString() {
        return label;
    }
}
