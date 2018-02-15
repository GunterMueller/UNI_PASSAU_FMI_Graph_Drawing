package tests.graffiti.plugins.algorithms.mst;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeExistsException;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.FieldAlreadySetException;
import org.graffiti.attributes.NoCollectionAttributeException;
import org.graffiti.event.ListenerManager;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

public class NodeStub implements Node {

    private int id;

    private Collection<Edge> adjacentEdges = new java.util.ArrayList<Edge>();

    private Map<String, Attribute> attributes = new java.util.HashMap<String, Attribute>(
            20);

    private GraphStub owner = null;

    public NodeStub(Graph owner, int id) {
        this.id = id;
        this.owner = (GraphStub) owner;
    }

    public Collection<Edge> getAllInEdges() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public Collection<Node> getAllInNeighbors() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public Collection<Edge> getAllOutEdges() {
        return adjacentEdges;
    }

    public Collection<Node> getAllOutNeighbors() {
        Collection<Node> adjacentNodes = new java.util.ArrayList<Node>();
        for (Edge e : adjacentEdges)
            if (this.equals(e.getSource())) {
                adjacentNodes.add(e.getTarget());
            } else if (this.equals(e.getTarget())) {
                adjacentNodes.add(e.getSource());
            } else {
                assert false;
            }
        return adjacentNodes;
    }

    public Collection<Edge> getDirectedInEdges() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public Iterator<Edge> getDirectedInEdgesIterator() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public Collection<Edge> getDirectedOutEdges() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public Iterator<Edge> getDirectedOutEdgesIterator() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public Collection<Edge> getEdges() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public Iterator<Edge> getEdgesIterator() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public int getInDegree() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public Collection<Node> getInNeighbors() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public Iterator<Node> getInNeighborsIterator() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public Collection<Node> getNeighbors() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public Iterator<Node> getNeighborsIterator() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public int getOutDegree() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public Collection<Node> getOutNeighbors() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public Iterator<Node> getOutNeighborsIterator() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public Collection<Edge> getUndirectedEdges() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public Iterator<Edge> getUndirectedEdgesIterator() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public Collection<Node> getUndirectedNeighbors() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public Graph getGraph() {
        return owner;
    }

    public void remove() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public void addAttribute(Attribute attr, String path)
            throws AttributeExistsException, NoCollectionAttributeException,
            FieldAlreadySetException {
        StringBuffer key = new StringBuffer(path);
        if (path != "") {
            key.append(".");
        }
        key.append(attr.getId());
        if (attributes.get(key.toString()) != null)
            throw new AttributeExistsException(path);
        attributes.put(key.toString(), attr);
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
        if (!attributes.containsKey(path))
            throw new AttributeNotFoundException(path);
        return attributes.get(path);
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
        attributes.remove(path);
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

    public int getId() {
        return id;
    }

    public void addAdjacentEdge(EdgeStub e) {
        adjacentEdges.add(e);
    }

    public boolean containsAttribute(String path) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
