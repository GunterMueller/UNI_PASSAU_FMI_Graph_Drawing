package tests.graffiti.plugins.algorithms.mst;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeConsumer;
import org.graffiti.attributes.AttributeExistsException;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.AttributeTypesManager;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.FieldAlreadySetException;
import org.graffiti.attributes.NoCollectionAttributeException;
import org.graffiti.attributes.UnificationException;
import org.graffiti.event.ListenerManager;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.GraphElementNotFoundException;
import org.graffiti.graph.Node;

public class GraphStub implements Graph {
    private List<Node> nodes = new java.util.ArrayList<Node>();
    private List<Edge> edges = new java.util.ArrayList<Edge>();
    private int maxNodeId = -1;
    private boolean[][] containsEdge = {};

    public GraphStub() {
    }

    public GraphStub(int noOfNodes) {
        containsEdge = new boolean[noOfNodes][noOfNodes];
    }

    public void addAttributeConsumer(AttributeConsumer attConsumer)
            throws UnificationException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public Edge addEdge(Node source, Node target, boolean directed)
            throws GraphElementNotFoundException {
        edges.add(new EdgeStub(source, target, directed));
        return edges.get(edges.size() - 1);
    }

    public Edge addEdge(Node source, Node target, boolean directed,
            CollectionAttribute col) throws GraphElementNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public Edge addEdgeCopy(Edge edge, Node source, Node target) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public void addGraph(Graph g) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public Node addNode() {
        if (maxNodeId + 1 >= containsEdge.length)
            throw new IllegalStateException();
        maxNodeId++;
        nodes.add(new NodeStub(this, maxNodeId));
        return nodes.get(nodes.size() - 1);
    }

    public Node addNode(CollectionAttribute col) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public Node addNodeCopy(Node node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public void clear() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public boolean containsEdge(Edge e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public boolean containsNode(Node n) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public Edge createEdge(Node source, Node target, boolean directed,
            CollectionAttribute col) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public Node createNode(CollectionAttribute col) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public void deleteEdge(Edge e) throws GraphElementNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public void deleteNode(Node n) throws GraphElementNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public AttributeTypesManager getAttTypesManager() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public Collection<Edge> getEdges() {
        return edges;
    }

    public Collection<Edge> getEdges(Node n1, Node n2) {
        Collection<Edge> e1 = new java.util.ArrayList<Edge>(n1.getAllOutEdges());
        e1.retainAll(n2.getAllOutEdges());
        return e1;
    }

    public Iterator<Edge> getEdgesIterator() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public Collection<GraphElement> getGraphElements() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public Iterator<Node> getNodesIterator() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public int getNumberOfDirectedEdges() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public int getNumberOfEdges() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public int getNumberOfNodes() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public int getNumberOfUndirectedEdges() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public boolean isDirected() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    public boolean isModified() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public boolean isUndirected() {
        for (Edge e : edges)
            if (e.isDirected())
                return false;
        return true;
    }

    public boolean removeAttributeConsumer(AttributeConsumer attConsumer) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public void setDirected(boolean directed) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public void setDirected(boolean directed, boolean adjustArrows) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public void setModified(boolean modified) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public void addAttribute(Attribute attr, String path)
            throws AttributeExistsException, NoCollectionAttributeException,
            FieldAlreadySetException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public void addBoolean(String path, String id, boolean value)
            throws NoCollectionAttributeException, AttributeExistsException,
            FieldAlreadySetException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public void addByte(String path, String id, byte value)
            throws NoCollectionAttributeException, AttributeExistsException,
            FieldAlreadySetException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public void addDouble(String path, String id, double value)
            throws NoCollectionAttributeException, AttributeExistsException,
            FieldAlreadySetException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public void addFloat(String path, String id, float value)
            throws NoCollectionAttributeException, AttributeExistsException,
            FieldAlreadySetException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public void addInteger(String path, String id, int value)
            throws NoCollectionAttributeException, AttributeExistsException,
            FieldAlreadySetException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public void addLong(String path, String id, long value)
            throws NoCollectionAttributeException, AttributeExistsException,
            FieldAlreadySetException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public void addShort(String path, String id, short value)
            throws NoCollectionAttributeException, AttributeExistsException,
            FieldAlreadySetException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public void addString(String path, String id, String value)
            throws NoCollectionAttributeException, AttributeExistsException,
            FieldAlreadySetException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public void changeBoolean(String path, boolean value)
            throws AttributeNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public void changeByte(String path, byte value)
            throws AttributeNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public void changeDouble(String path, double value)
            throws AttributeNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public void changeFloat(String path, float value)
            throws AttributeNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public void changeInteger(String path, int value)
            throws AttributeNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public void changeLong(String path, long value)
            throws AttributeNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public void changeShort(String path, short value)
            throws AttributeNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public void changeString(String path, String value)
            throws AttributeNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public Attribute getAttribute(String path)
            throws AttributeNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public CollectionAttribute getAttributes() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public boolean getBoolean(String path) throws AttributeNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public byte getByte(String path) throws AttributeNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public double getDouble(String path) throws AttributeNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public float getFloat(String path) throws AttributeNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public int getInteger(String path) throws AttributeNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public ListenerManager getListenerManager() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public long getLong(String path) throws AttributeNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public short getShort(String path) throws AttributeNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public String getString(String path) throws AttributeNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public void removeAttribute(String path) throws AttributeNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public void setBoolean(String path, boolean value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public void setByte(String path, byte value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public void setDouble(String path, double value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public void setFloat(String path, float value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public void setInteger(String path, int value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public void setLong(String path, long value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public void setShort(String path, short value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public void setString(String path, String value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public Object copy() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public boolean containsAttribute(String path) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
