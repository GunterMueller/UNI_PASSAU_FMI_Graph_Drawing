package org.graffiti.plugins.algorithms.sugiyama.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * A <tt>NumberedTree</tt> is a tree structure which stores it's objects of the
 * parameterized type as leaves and numbers them serially starting with 0.<br>
 * Thereby inserting a value may change the numbers of all following nodes.<br>
 * It implements the <tt>Collection</tt> interface.
 */
public class NumberedTree<E> implements Collection<E> {

    /* should only be used for testing reasons */
    private static NumberedTree<?> mainTree = null;

    /*
     * maps a node of this tree to it's internal representation, a
     * NumberedTreeNode
     */
    private HashMap<E, NumberedTreeNode> nodeMap = new HashMap<E, NumberedTreeNode>();

    /*
     * maps the internal representations of the nodes to the nodes added to the
     * tree
     */
    private HashMap<NumberedTreeNode, E> invNodeMap = new HashMap<NumberedTreeNode, E>();

    private int size = 0;

    /* maps all nodes to the number of the leftmost leaf in their subtree */
    protected HashMap<NumberedTreeNode, Integer> numbers = new HashMap<NumberedTreeNode, Integer>();

    private InnerNumberedTreeNode root = null;

    /* link to the node with number 0 */
    private NumberedTreeNode first = null;

    /**
     * Adds the element at the end of the <tt>NumberedTree</tt>
     */
    public boolean add(E node) {
        // O(log(n))
        return add(node, size);
    }

    /**
     * Adds the node at the given position of the <tt>NumberedTree</tt> thereby
     * shifting the node previously at this position and all following nodes up
     * by 1.
     * 
     * @param node
     *            Node to insert
     * @param position
     *            Position of the new node
     * @return <tt>false</tt> if the node is <tt>null</tt> or already contained
     *         in the <tt>NumberedTree</tt> or if the position is lower than 0
     *         or bigger than size + 1.<br>
     *         <tt>true</tt> otherwise
     */
    public boolean add(E node, int position) {
        // O(log(n))
        assert node != null;
        assert 0 <= position;
        assert position <= size;

        if (nodeMap.containsKey(node))
            return false;

        NumberedTreeNode newNode = new NumberedTreeNode(this);
        nodeMap.put(node, newNode);
        invNodeMap.put(newNode, node);

        if (root == null) {
            root = new InnerNumberedTreeNode(true, null, this);
        }

        root.addNode(newNode, position);
        size++;
        resetNumbers();
        if (position == 0) {
            first = newNode;
        }
        return true;
    }

    /**
     * If any of the elements in collection is contained in the
     * <tt>NumberedTree</tt>, yet, then none is added and <code>false</code> is
     * returned.<br>
     * Otherwise all elements of the collection are added at the end of the
     * <tt>NumberedTree</tt> in the order used by the collection's iterator.
     */
    public boolean addAll(Collection<? extends E> c) {
        // O(k*log(n)), k=c.size
        boolean result = true;

        for (E newNode : c)
            if (nodeMap.containsKey(newNode))
                return false;
        for (E newNode : c) {
            result &= add(newNode);
        }

        return result;
    }

    /**
     * Clears the <tt>NumberedTree</tt> and deletes all it's elements.
     */
    public void clear() {
        // O(1)
        nodeMap = new HashMap<E, NumberedTreeNode>();
        invNodeMap = new HashMap<NumberedTreeNode, E>();
        root = null;
        first = null;
        size = 0;
    }

    /**
     * Returns <tt>true</tt> if the <tt>NumberedTree</tt> contains this object,
     * <tt>false</tt> otherwise.
     */
    public boolean contains(Object o) {
        // O(1)
        return nodeMap.containsKey(o);
    }

    /**
     * Returns <tt>true</tt> if the <tt>NumberedTree</tt> contains all objects
     * in the <tt>Collection</tt>, <tt>false</tt> otherwise.
     */
    public boolean containsAll(Collection<?> c) {
        // O(k), k=c.size
        for (Object o : c) {
            if (!contains(o))
                return false;
        }
        return true;
    }

    /**
     * Returns if the <tt>NumberedTree</tt> is empty.
     */
    public boolean isEmpty() {
        // O(1)
        return size == 0;
    }

    /**
     * Returns an <tt>Iterator</tt> of the objects stored in the tree.
     */
    public Iterator<E> iterator() {
        // O(n)
        LinkedList<E> resultList = new LinkedList<E>();

        if (size == 0)
            return resultList.iterator();

        NumberedTreeNode node = first;
        while (node != null) {
            resultList.add(invNodeMap.get(node));
            node = node.getNextNode();
        }
        return resultList.iterator();
    }

    /**
     * Removes the object from the <tt>NumberedTree</tt>. Returns <tt>true</tt>
     * if the object was contained in the <tt>NumberedTree</tt>, <tt>false</tt>
     * otherwise.
     */
    @SuppressWarnings("unchecked")
    public boolean remove(Object o) {
        // O(log n)
        if (!nodeMap.containsKey(o))
            return false;
        NumberedTreeNode node = nodeMap.get(o);

        int position = getNumber((E) o);
        if (position == 0) {
            first = (size > 1) ? first.getNextNode() : null;
        }

        node.getFather().removeLeave(node);
        size--;
        if (size == 0) {
            root = null;
        }

        invNodeMap.remove(node);
        nodeMap.remove(o);
        resetNumbers();
        return true;
    }

    /**
     * Removes all objects contained in the <tt>Collection</tt> from the
     * <tt>NumberedTree</tt>.<br>
     * Returns <tt>true</tt> if all elements have been in the
     * <tt>NumberedTree</tt>, <tt>false</tt> otherwise.
     */
    public boolean removeAll(Collection<?> c) {
        // O(k*log(n)), k = c.size
        boolean result = true;
        for (Object o : c) {
            result &= remove(o);
        }

        return result;
    }

    /**
     * This method is not supported by <tt>NumberedTree</tt> and will throw an
     * <tt>UnsupportedOperationException</tt> when called.
     */
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the number of elements contained in the <tt>NumberedTree</tt>.
     */
    public int size() {
        // O(1)
        return size;
    }

    /**
     * An array of <tt>Object</tt>s is return containing all data objects stored
     * in this tree.
     */
    public Object[] toArray() {
        // O(n)
        Object[] result = new Object[size];

        Iterator<E> it = iterator();
        int position = 0;
        while (it.hasNext()) {
            result[position] = it.next();
            position++;
        }
        return result;
    }

    /**
     * This method is not supported by <tt>NumberedTree</tt> and will throw an
     * <tt>UnsupportedOperationException</tt> when called.
     */
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }

    /**
     * Repairs the 2-3-tree structure so that no inner node has more three
     * children.
     * 
     * @param incorrectNode
     *            The node which has not 2 or 3 children.
     */
    void repairTree(InnerNumberedTreeNode incorrectNode) {
        // O(log(n))
        InnerNumberedTreeNode father = incorrectNode.getFather();

        if (father == null) {
            // incorrect node is root

            if (incorrectNode.size() == 4) {
                // split node and add a new root node

                father = new InnerNumberedTreeNode(false, null, this);
                root = father;
                InnerNumberedTreeNode newNode = new InnerNumberedTreeNode(
                        incorrectNode.containsLeeves(), father, this);
                incorrectNode.setFather(father);

                father.addFirst(incorrectNode);
                father.addLast(newNode);

                // move two children to the new node
                newNode.addFirst(incorrectNode.removeLast());
                newNode.addFirst(incorrectNode.removeLast());
            } else if (incorrectNode.size() == 1) {
                // if the root has only one child which is an inner node then
                // the root can be deleted and the child can be made the new
                // root
                if (!incorrectNode.containsLeeves()) {
                    InnerNumberedTreeNode newRoot = (InnerNumberedTreeNode) incorrectNode
                            .removeFirst();
                    root = newRoot;
                }
            }
        } else {
            // there is a father node and therefore at least one neighbor node

            if (incorrectNode.size() == 4) { // the incorrect node is too big
                if (father.size() == 2) {
                    // there are only two nodes so just split the incorrect one
                    InnerNumberedTreeNode newNode = new InnerNumberedTreeNode(
                            incorrectNode.containsLeeves(), father, this);

                    if (father.getInnerNode(0).size() == 4) {
                        father.addAt(1, newNode);
                    } else {
                        father.addLast(newNode);
                    }
                    // move two children to the new node
                    newNode.addFirst(incorrectNode.removeLast());
                    newNode.addFirst(incorrectNode.removeLast());
                } else {
                    // the father node has 3 children already

                    /*
                     * Try to balance the number of grand children of the
                     * children. This is possible if the three children have <=
                     * 9 grand children.
                     */
                    if (father.getInnerNode(0).size()
                            + father.getInnerNode(1).size()
                            + father.getInnerNode(2).size() <= 9) {

                        for (int i = 0; i < 2; i++) {
                            if (father.getInnerNode(i).size() <= 2) {
                                father.getInnerNode(i).addLast(
                                        father.getInnerNode(i + 1)
                                                .removeFirst());
                            } else if (father.getInnerNode(i).size() == 4) {
                                father.getInnerNode(i + 1).addFirst(
                                        father.getInnerNode(i).removeLast());
                            }
                        }
                    } else {
                        /*
                         * There have to be 10 grand children, so split the
                         * incorrect node. As the father has now 4 children call
                         * repairTree for the father recursively.
                         */
                        InnerNumberedTreeNode newNode = new InnerNumberedTreeNode(
                                incorrectNode.containsLeeves(), father, this);

                        if (father.getInnerNode(0).size() == 4) {
                            father.addAt(1, newNode);
                        } else if (father.getInnerNode(1).size() == 4) {
                            father.addAt(2, newNode);
                        } else {
                            father.addLast(newNode);
                        }
                        // move two children to the new node
                        newNode.addFirst(incorrectNode.removeLast());
                        newNode.addFirst(incorrectNode.removeLast());

                        repairTree(father);
                    }
                }
            } else if (incorrectNode.size() == 1) {// node too small
                /*
                 * Try to balance the number of grand children of the children.
                 * This is possible if the children have > 3 grand children. If
                 * there are only 3 grand children repairTree is called
                 * recursively.
                 */
                int numberOfGChildren = father.getInnerNode(0).size()
                        + father.getInnerNode(1).size();
                if (father.size() > 2) {
                    numberOfGChildren += father.getInnerNode(2).size();
                }

                switch (numberOfGChildren) {
                case 3:
                    // there are two nodes: 1 with 1 child, 1 with 2 children
                    // -> 1 with 3 children
                    int numberToMove = father.getInnerNode(1).size();
                    for (int i = 0; i < numberToMove; i++) {
                        father.getInnerNode(0).addLast(
                                father.getInnerNode(1).removeFirst());
                    }
                    father.removeLast();

                    repairTree(father);
                    break;

                case 4:
                    // there are two nodes: 1 with 3 children, 1 with 1 child
                    // -> 2 with 2 children
                    if (father.getInnerNode(0).size() == 1) {
                        father.getInnerNode(0).addLast(
                                father.getInnerNode(1).removeFirst());
                    } else {
                        father.getInnerNode(1).addFirst(
                                father.getInnerNode(0).removeLast());
                    }
                    break;
                case 5:
                    // there are 3 nodes: 2 with 2 children, 1 with 1 child
                    // -> 1 with 2 children, 1 with 3 children
                    father.getInnerNode(0).addLast(
                            father.getInnerNode(1).removeFirst());

                    father.getInnerNode(1).addLast(
                            father.getInnerNode(2).removeFirst());

                    if (father.getInnerNode(2).size() > 0) {
                        father.getInnerNode(1).addLast(
                                father.getInnerNode(2).removeFirst());
                    }

                    father.removeLast();
                    break;
                case 6:
                    // there are 3 nodes: 1 with 1 child, 1 with 2 children
                    // and 1 with 3 children
                    // -> 3 with 2 children
                case 7:
                    // there are 3 nodes: 1 with 1 child, 2 with 3 children
                    // -> 2 with 2 children, 1 with 3 children
                    for (int i = 0; i < 2; i++) {
                        if (father.getInnerNode(i).size() == 1) {
                            father.getInnerNode(i).addLast(
                                    father.getInnerNode(i + 1).removeFirst());
                        } else if (father.getInnerNode(i).size() >= 3) {
                            father.getInnerNode(i + 1).addFirst(
                                    father.getInnerNode(i).removeLast());
                        }
                    }
                    break;
                }
            }
        }
    }

    /*
     * Reset the HashMap of the node numbers.
     */
    protected void resetNumbers() {
        // O(1)
        // IncrementalSugiyama.debug("Numbers reset");
        numbers = new HashMap<NumberedTreeNode, Integer>();
    }

    /**
     * String representation of this tree.
     */
    @Override
    public String toString() {
        return (root == null) ? "Tree[void]" : "Tree[" + root + "]";
    }

    /*  ************* GETTER AND SETTER ********************************** */

    static NumberedTree<?> getMainTree() {
        return mainTree;
    }

    /**
     * Returns the number of the given data node.
     */
    public int getNumber(E node) {
        // O(1) - O(log n)
        assert nodeMap.get(node) != null;
        return nodeMap.get(node).getNumber();
    }

    /**
     * Returns the node following the given node.
     * 
     * @param node
     *            predecessor of the wanted node
     * @return node following the given node
     */
    public E getNext(E node) {
        // O(1)
        NumberedTreeNode ntnode = nodeMap.get(node);
        NumberedTreeNode next = (ntnode == null) ? null : ntnode.getNextNode();
        return (next == null) ? null : invNodeMap.get(next);
    }

    /**
     * Returns the node preceding the given node.
     * 
     * @param node
     *            Successor of the wanted node
     * @return Node preceding the given node
     */
    public E getPrev(E node) {
        // O(1)
        NumberedTreeNode ntnode = nodeMap.get(node);
        NumberedTreeNode next = (ntnode == null) ? null : ntnode.getPrevNode();
        return (next == null) ? null : invNodeMap.get(next);
    }

    E getNode(NumberedTreeNode node) {
        // O(1)
        return invNodeMap.get(node);
    }

    /**
     * Returns the first node.
     * 
     * @return First node if existing, <tt>null</tt> otherwise
     */
    public E getFirst() {
        // O(1)
        if (first != null)
            return invNodeMap.get(first);
        else
            return null;
    }

    /**
     * Returns the node at the given position.
     */
    public E get(int position) {
        // O(log(n))
        assert position >= 0;
        assert position < size();

        NumberedTreeNode node = root.getNode(position);
        return invNodeMap.get(node);
    }

    /*
     * Returns the HashMap with the numbers of the nodes.
     */
    protected HashMap<NumberedTreeNode, Integer> getNumbers() {
        // O(1)
        return numbers;
    }

    /* This method is only for testing the NumberedTree-Structure */
    public static void main(String[] args) {
        NumberedTree<String> tree = new NumberedTree<String>();
        mainTree = tree;

        /*
         * LinkedList<String> s1 = new LinkedList<String>(); s1.add("Karla");
         * s1.add("Peter"); s1.add("Lutz"); s1.add("Tom");
         */

        tree.add("Anton");
        System.out.println(tree);
        tree.add("Bernd");
        System.out.println(tree);
        tree.add("Christoph", 0);
        System.out.println(tree);
        tree.add("Daniel");
        System.out.println(tree);
        tree.add("Erich", 3);
        System.out.println(tree);
        tree.add("Fritz");
        System.out.println(tree);
        tree.add("Gerhard", 6);
        System.out.println(tree);
        tree.add("Heinrich", 1);
        System.out.println(tree);
        tree.add("Ingo");
        System.out.println(tree);
        tree.add("Josef");
        System.out.println(tree);
        tree.add("Karl");
        System.out.println(tree);
        tree.add("Lutz");
        System.out.println(tree);
        tree.add("Manfred");
        System.out.println(tree);
        tree.add("Nero");
        System.out.println(tree);
        tree.add("Otto");
        System.out.println(tree);
        tree.add("Paul");
        System.out.println(tree);
        tree.add("Quintus");
        System.out.println(tree);
        tree.add("Ralf");
        System.out.println(tree);
        tree.add("Sepp");
        System.out.println(tree);
        tree.add("Tim");
        System.out.println(tree);
        tree.add("Udo");
        System.out.println(tree);
        tree.add("Valentin");
        System.out.println(tree + "\n");

        Iterator<String> it = tree.iterator();

        System.out.print("ORDERED NAMES: ");
        while (it.hasNext()) {
            System.out.print(it.next() + ", ");
        }
        System.out.println("");

        tree.remove("Ingo");
        System.out.println(tree);
        tree.remove("Gerhard");
        System.out.println(tree);
        tree.remove("Sepp");
        System.out.println(tree);
        tree.remove("Tim");
        System.out.println(tree);
        tree.remove("Valentin");
        System.out.println(tree);
        tree.remove("Quintus");
        System.out.println(tree);
        tree.remove("Christoph");
        System.out.println(tree);

        LinkedList<String> delList = new LinkedList<String>();
        delList.add("Anton");
        delList.add("Karl");
        delList.add("Josef");
        delList.add("Bernd");

        tree.removeAll(delList);
        System.out.println(tree);
        tree.remove("Daniel");
        System.out.println(tree);
        tree.remove("Nero");
        System.out.println(tree);
        tree.remove("Erich");
        System.out.println(tree);
        tree.remove("Lutz");
        System.out.println(tree);
        tree.remove("Otto");
        System.out.println(tree);
        tree.remove("Manfred");
        System.out.println(tree);
        tree.remove("Ralf");
        System.out.println(tree);
        tree.remove("Udo");
        System.out.println(tree);
        tree.remove("Heinrich");
        System.out.println(tree);
        tree.remove("Fritz");
        System.out.println(tree);
        System.out.println("Tree is empty: " + tree.isEmpty());
        System.out.println("Tree contains 'Fritz': " + tree.contains("Fritz"));
        System.out.println("Tree contains 'Paul': " + tree.contains("Paul"));
        tree.remove("Paul");
        System.out.println(tree);
        System.out.println("Tree is empty: " + tree.isEmpty());

        // tree.addAll(s1);
    }
}
