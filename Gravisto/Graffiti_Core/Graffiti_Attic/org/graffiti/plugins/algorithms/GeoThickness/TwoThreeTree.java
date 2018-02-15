/*
 * TwoThreeTree.java
 *
 * Copyright (c) 2001-2006 Gravisto Team, University of Passau
 *
 * Created on Aug 3, 2005
 *
 */

package org.graffiti.plugins.algorithms.GeoThickness;

import java.util.NoSuchElementException;

/**
 * @author ma
 * 
 *         An Implementation fo two-three-tree for geomtric thickness
 *         algorithmus
 */
class TwoThreeTree {
    /**
     * the root of the tree
     */
    Vertex root = null;

    GeometricAlgorithms geoAlgorithms = new GeometricAlgorithms();

    /**
     * search the Vertex in the tree
     * 
     * @param edge
     *            Edge index of the vertex
     * @return edge of class Edge
     */
    public LocalEdge search(LocalEdge edge) {
        if (root == null)
            throw new NoSuchElementException();

        return ((Leaf) root.search(edge)).edgeVertex.getEdge();
    }

    /**
     * insert a Vertex in the Tree
     * 
     * @param edgeVertex
     *            Edge index of the Vertex
     */
    public LocalEdge[] insert(EdgeVertex edgeVertex) {
        LocalEdge[] edges = new LocalEdge[2];

        if (root == null) {
            root = new Leaf(edgeVertex, null, null);
        } else {
            VertexWithMinWithWay newNode = root.insert(edgeVertex);

            edges = newNode.edges;

            if (newNode.vertex == null)
                return edges;

            InsideVertex newinVertex = new InsideVertex();

            // Vertex in right of the Tree
            if (newNode.way) {
                newinVertex.verLeft = root;
                newinVertex.verMiddle = newNode.vertex;
                newinVertex.verRight = null;
                newinVertex.indexLeft = newNode.minValue;
            } else {
                newinVertex.verLeft = newNode.vertex;
                newinVertex.verMiddle = root;
                newinVertex.verRight = null;
                newinVertex.indexLeft = ((Leaf) root).edgeVertex;
            }

            root = newinVertex;
        }

        return edges;
    }

    /**
     * delete a Vertex from the Tree
     * 
     * @param key
     *            Edge index of the tree
     */
    public LocalEdge[] delete(LocalEdge key) {
        if (root == null)
            throw new NoSuchElementException();

        LocalEdge[] result = root.delete(key);

        if (root instanceof Leaf) {
            root = null;
        } else if (((InsideVertex) root).verMiddle == null) {
            root = ((InsideVertex) root).verLeft;
        }

        return result;
    }

    /**
     * look for left node of current node
     * 
     * @param key
     *            Edge index of the tree
     */
    public LocalEdge[] searchLeftAndRight(LocalEdge key) {
        Leaf edge;
        LocalEdge[] resultEdge = new LocalEdge[2];

        if (root == null)
            throw new NoSuchElementException();

        edge = ((Leaf) root.search(key));

        if (edge != null) {
            if (edge.left != null) {
                resultEdge[0] = edge.left.edgeVertex.getEdge();
            }

            if (edge.right != null) {
                resultEdge[1] = edge.right.edgeVertex.getEdge();
            }
        }

        return resultEdge;
    }

    /**
     * change the position of two edges
     * 
     * @param left
     *            Edge
     * @param right
     *            Edge
     */
    public LocalEdge[] swapEdge(LocalEdge left, LocalEdge right) {
        LocalEdge[] edges = new LocalEdge[2];

        if (root == null)
            throw new NoSuchElementException();
        Leaf leftLeaf = ((Leaf) root.search(left));
        leftLeaf.edgeVertex.setEdge(right);
        leftLeaf.right.edgeVertex.setEdge(left);

        Leaf leftLeft = leftLeaf.left;
        Leaf rightRight = leftLeaf.right.right;

        if (leftLeft != null) {
            edges[0] = leftLeft.edgeVertex.getEdge();
        }

        if (rightRight != null) {
            edges[1] = rightRight.edgeVertex.getEdge();
        }

        return edges;
    }

    /***/
    @Override
    public String toString() {
        if (root == null)
            return "";
        else
            return root.toString();
    }

    /***/
    public void setGeoAlgorithms(GeometricAlgorithms geoAlgorithms) {
        this.geoAlgorithms = geoAlgorithms;
    }

    /**
     * look for the smallste Index on the tree
     * 
     * @param vertex
     *            Object of index
     * @return EdgeVertex
     */
    private EdgeVertex searchMinIndex(Vertex vertex) {
        if (vertex == null)
            return null;
        else if (vertex instanceof Leaf)
            return ((Leaf) vertex).edgeVertex;
        else
            return searchMinIndex(((InsideVertex) vertex).verLeft);
    }

    private String newString(LocalEdge edge) {
        return "(" + edge.getLeftX() + ", " + edge.getLeftY() + ")" + "   ("
                + edge.getRightX() + ", " + edge.getRightY() + ") -------";
    }

    /**
     * abstract class Vertex
     */
    abstract class Vertex {
        /**
         * search the Vertex in the tree
         * 
         * @param edge
         *            Edge index of the vertex
         * @return edge of class Edge
         */
        abstract Vertex search(LocalEdge edge);

        /**
         * insert a Vertex in the Tree
         * 
         * @param edgeVertex
         *            Edge index of the Vertex
         * @return a object of class VertexWithMinWithWay
         */
        abstract VertexWithMinWithWay insert(EdgeVertex edgeVertex);

        /**
         * delete a Vertex from the Tree
         * 
         * @param edge
         *            Edge index of the tree
         */
        abstract LocalEdge[] delete(LocalEdge edge);
    }

    /**
     * the Leaf of the tree
     */
    class Leaf extends Vertex {
        private EdgeVertex edgeVertex;

        private Leaf left;

        private Leaf right;

        public Leaf(EdgeVertex edgeVertex, Leaf left, Leaf right) {
            this.edgeVertex = edgeVertex;
            this.left = left;
            this.right = right;
        }

        @Override
        public String toString() {
            return "(" + edgeVertex.getEdge().getLeftX() + ", "
                    + edgeVertex.getEdge().getLeftY() + ")" + "   ("
                    + edgeVertex.getEdge().getRightX() + ", "
                    + edgeVertex.getEdge().getRightY() + ") -------";
        }

        /*
         * 
         * 
         * @see
         * org.graffiti.plugins.algorithms.GeoThickness.TwoThreeTree.Vertex#
         * search(double)
         */
        @Override
        public Vertex search(LocalEdge edge) {
            // TODO Auto-generated method stub
            if (this.edgeVertex.getEdge().equals(edge))
                return this;

            throw new NoSuchElementException();
        }

        /*
         * 
         * 
         * @see
         * org.graffiti.plugins.algorithms.GeoThickness.TwoThreeTree.Vertex#
         * insert(double,
         * org.graffiti.plugins.algorithms.GeoThickness.TwoThreeTree.Vertex,
         * java.lang.Object)
         */
        @Override
        public VertexWithMinWithWay insert(EdgeVertex edgeVertex) {
            // TODO Auto-generated method stub
            LocalEdge[] edges = new LocalEdge[2];

            if (this.edgeVertex.equals(edgeVertex))
                throw new RuntimeException("Wert bereits vorhanden");

            Vertex newNode;

            boolean way = geoAlgorithms.comp_sect(edgeVertex.getEdge(),
                    this.edgeVertex.getEdge());

            if (way) {
                newNode = new Leaf(edgeVertex, this, this.right);
                edges[0] = this.edgeVertex.getEdge();

                if (this.right != null) {
                    edges[1] = this.right.edgeVertex.getEdge();
                    this.right.left = ((Leaf) newNode);
                }

                this.right = ((Leaf) newNode);
            } else {
                newNode = new Leaf(edgeVertex, this.left, this);
                edges[1] = this.edgeVertex.getEdge();

                if (this.left != null) {
                    edges[0] = this.left.edgeVertex.getEdge();
                    this.left.right = ((Leaf) newNode);
                }

                this.left = ((Leaf) newNode);
            }

            return new VertexWithMinWithWay(newNode, edgeVertex, way, edges);
        }

        /*
         * 
         * 
         * @see
         * org.graffiti.plugins.algorithms.GeoThickness.TwoThreeTree.Vertex#
         * delete(double)
         */
        @Override
        public LocalEdge[] delete(LocalEdge edge) {
            // TODO Auto-generated method stub
            LocalEdge[] edges = new LocalEdge[2];

            if (this.left != null) {
                edges[0] = this.left.edgeVertex.getEdge();
            }

            if (this.right != null) {
                edges[1] = this.right.edgeVertex.getEdge();
            }

            if (this.edgeVertex.getEdge().equals(edge)) {
                if (this.left != null) {
                    this.left.right = this.right;
                }

                if (this.right != null) {
                    this.right.left = this.left;
                }
            } else
                throw new NoSuchElementException();
            return edges;
        }
    }

    /**
     * the inside Vertex of the tree
     */
    class InsideVertex extends Vertex {
        Vertex verLeft;

        Vertex verMiddle;

        Vertex verRight;

        EdgeVertex indexLeft;

        EdgeVertex indexRight;

        /*
         * 
         * 
         * @see
         * org.graffiti.plugins.algorithms.GeoThickness.TwoThreeTree.Vertex#
         * search(double)
         */
        @Override
        public Vertex search(LocalEdge edge) {
            if (geoAlgorithms.comp_sect(this.indexLeft.getEdge(), edge))
                return verLeft.search(edge);

            if ((verRight == null)
                    || (geoAlgorithms
                            .comp_sect(this.indexRight.getEdge(), edge)))
                return verMiddle.search(edge);

            return verRight.search(edge);
        }

        /*
         * 
         * 
         * @see
         * org.graffiti.plugins.algorithms.GeoThickness.TwoThreeTree.Vertex#
         * insert(double,
         * org.graffiti.plugins.algorithms.GeoThickness.TwoThreeTree.Vertex,
         * java.lang.Object)
         */
        @Override
        public VertexWithMinWithWay insert(EdgeVertex edgeVertex) {
            Vertex sohn;
            int index;
            LocalEdge newEdge = edgeVertex.getEdge();
            LocalEdge leftEdge = null;
            LocalEdge rightEdge = null;

            if (this.indexLeft != null) {
                leftEdge = this.indexLeft.getEdge();
            }

            if (this.indexRight != null) {
                rightEdge = this.indexRight.getEdge();
            }

            if (geoAlgorithms.comp_sect(leftEdge, newEdge)) {
                sohn = this.verLeft;
                index = 0;
            } else if ((this.verRight == null)
                    || (geoAlgorithms.comp_sect(rightEdge, newEdge))) {
                sohn = this.verMiddle;
                index = 1;
            } else {
                sohn = this.verRight;
                index = 2;
            }

            VertexWithMinWithWay result = sohn.insert(edgeVertex);

            if (result.vertex == null)
                return result;

            Vertex newSohn = result.vertex;
            EdgeVertex newValue = result.minValue;
            boolean right = result.way;
            LocalEdge[] edges = result.edges;

            if (!right) {
                index = -1;
            }

            if (this.verRight == null) {
                switch (index) {
                case -1:

                    EdgeVertex tmpVer = ((Leaf) this.verLeft).edgeVertex;
                    this.verRight = this.verMiddle;
                    this.indexRight = this.indexLeft;
                    this.verMiddle = this.verLeft;
                    this.indexLeft = tmpVer;
                    this.verLeft = newSohn;

                    break;

                case 0:
                    this.verRight = this.verMiddle;
                    this.indexRight = indexLeft;
                    this.verMiddle = newSohn;
                    this.indexLeft = newValue;

                    break;

                case 1:
                    this.verRight = newSohn;
                    this.indexRight = newValue;

                    break;
                }

                return new VertexWithMinWithWay(null, null, false, edges);
            } else {
                switch (index) {
                case -1:

                    EdgeVertex tmpIndex = ((Leaf) this.verLeft).edgeVertex;

                    return aufspalten(newSohn, this.verLeft, this.verMiddle,
                            this.verRight, tmpIndex, this.indexLeft,
                            this.indexRight, edges);

                case 0:
                    return aufspalten(this.verLeft, newSohn, this.verMiddle,
                            this.verRight, newValue, this.indexLeft,
                            this.indexRight, edges);

                case 1:
                    return aufspalten(this.verLeft, this.verMiddle, newSohn,
                            this.verRight, this.indexLeft, newValue,
                            this.indexRight, edges);

                default:
                    return aufspalten(this.verLeft, this.verMiddle,
                            this.verRight, newSohn, this.indexLeft,
                            this.indexRight, newValue, edges);
                }
            }
        }

        private VertexWithMinWithWay aufspalten(Vertex newVer0, Vertex newVer1,
                Vertex newVer2, Vertex newVer3, EdgeVertex newIndex1,
                EdgeVertex newIndex2, EdgeVertex newIndex3, LocalEdge[] edges) {
            this.verLeft = newVer0;
            this.verMiddle = newVer1;
            this.verRight = null;
            this.indexLeft = newIndex1;

            InsideVertex newNode = new InsideVertex();
            newNode.verLeft = newVer2;
            newNode.verMiddle = newVer3;
            newNode.verRight = null;
            newNode.indexLeft = newIndex3;

            return new VertexWithMinWithWay(newNode, newIndex2, true, edges);
        }

        /*
         * 
         * 
         * @see
         * org.graffiti.plugins.algorithms.GeoThickness.TwoThreeTree.Vertex#
         * delete(double)
         */
        @Override
        public LocalEdge[] delete(LocalEdge edge) {
            Vertex sohn;

            int index;

            if (geoAlgorithms.comp_sect(this.indexLeft.getEdge(), edge)) {
                // indexleft is right
                sohn = verLeft;
                index = 0;
            } else if ((this.verRight == null)
                    || (geoAlgorithms
                            .comp_sect(this.indexRight.getEdge(), edge))) {
                sohn = verMiddle;
                index = 1;
            } else {
                sohn = verRight;
                index = 2;
            }

            LocalEdge[] edges = sohn.delete(edge);

            if (sohn instanceof Leaf) {
                switch (index) {
                case 0:
                    this.verLeft = this.verMiddle;
                    this.indexLeft = this.indexRight;
                    this.verMiddle = this.verRight;
                    this.verRight = null;
                    return edges;

                case 1:
                    this.indexLeft = this.indexRight;
                    this.verMiddle = this.verRight;
                    this.verRight = null;
                    return edges;

                case 2:
                    this.verRight = null;
                    return edges;
                }
            }

            if (((InsideVertex) sohn).verMiddle == null) {
                // current node has one Sohn
                InsideVertex inVerLeft = (InsideVertex) this.verLeft;

                InsideVertex inVerMiddle = (InsideVertex) this.verMiddle;

                InsideVertex inVerRight = (InsideVertex) this.verRight;

                if ((index == 1) && (inVerLeft.verRight != null)) {
                    this.indexLeft = borrowFromLeft(inVerLeft, inVerMiddle);
                } else if ((index == 2) && (inVerMiddle.verRight != null)) {
                    this.indexRight = borrowFromLeft(inVerMiddle, inVerRight);
                } else if ((index == 0) && (inVerMiddle.verRight != null)) {
                    this.indexLeft = borrowFromRight(inVerLeft, inVerMiddle);
                } else if ((index == 1) && (inVerRight != null)
                        && (inVerRight.verRight != null)) {
                    this.indexRight = borrowFromRight(inVerMiddle, inVerRight);
                } else if (index == 0) {
                    mergeWithRight(inVerLeft, inVerMiddle);
                    indexLeft = indexRight;
                    verMiddle = verRight;
                    verRight = null;
                    indexRight = null;
                } else if (index == 1) {
                    mergeWithLeft(inVerLeft, inVerMiddle);
                    indexLeft = indexRight;
                    verMiddle = verRight;
                    verRight = null;
                    indexRight = null;
                } else {
                    mergeWithLeft(inVerMiddle, inVerRight);
                    verRight = null;
                    indexRight = null;
                }
            }

            if (index == 1) {
                this.indexLeft = searchMinIndex(this.verMiddle);
            } else if (index == 2) {
                this.indexRight = searchMinIndex(this.verRight);
            }

            return edges;
        }

        private EdgeVertex borrowFromLeft(InsideVertex nn, InsideVertex n) {
            // degree (kk)=3, degree(k)=1
            EdgeVertex result = nn.indexRight;
            n.verMiddle = n.verLeft;
            n.verLeft = nn.verRight;
            n.indexLeft = searchMinIndex(n.verMiddle);
            n.verRight = null;
            nn.verRight = null;
            nn.indexRight = null;
            return result;
        }

        private EdgeVertex borrowFromRight(InsideVertex n, InsideVertex nn) {
            EdgeVertex result = nn.indexLeft;
            n.verMiddle = nn.verLeft;
            n.indexLeft = searchMinIndex(n.verMiddle);
            nn.verLeft = nn.verMiddle;
            nn.verMiddle = nn.verRight;
            nn.indexLeft = nn.indexRight;
            n.verRight = null;
            nn.verRight = null;
            nn.indexRight = null;
            return result;
        }

        private void mergeWithRight(InsideVertex n, InsideVertex nn) {
            n.verMiddle = nn.verLeft;
            n.verRight = nn.verMiddle;
            n.indexLeft = searchMinIndex(n.verMiddle);
            n.indexRight = nn.indexLeft;
            nn = null;
        }

        private void mergeWithLeft(InsideVertex nn, InsideVertex n) {
            nn.verRight = n.verLeft;
            nn.indexRight = searchMinIndex(nn.verRight);
            n = null;
        }

        @Override
        public String toString() {
            if (verRight != null)
                return verLeft.toString() + "   indexLeft:"
                        + newString(indexLeft.getEdge()) + verMiddle.toString()
                        + "    indexRight:" + newString(indexRight.getEdge())
                        + verRight.toString();
            else
                return verLeft.toString() + "   indexLeft:"
                        + newString(indexLeft.getEdge()) + verMiddle.toString();
        }

    }

    /**
     * new vertex with way
     */
    class VertexWithMinWithWay {
        protected Vertex vertex;

        protected EdgeVertex minValue;

        protected boolean way;

        protected LocalEdge[] edges;

        public VertexWithMinWithWay(Vertex vertex, EdgeVertex value,
                boolean way, LocalEdge[] edges) {
            this.vertex = vertex;
            this.minValue = value;
            this.way = way;
            this.edges = edges;
        }
    }
}
