// =============================================================================
//
//      HighDimEmbed.java
//
//      Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: HighDimEmbedAlgorithm.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.HighDimEmbed;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.graffiti.event.AttributeEvent;
import org.graffiti.graph.Node;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.selection.Selection;

/**
 * @author Harald Aigner
 * 
 * 
 * 
 *         A grapgh drawing algorithm based on Yehuda Koren�s method using 1)
 *         pivot node choosing by k-center-problem 2) hgh-dimensional embedding
 *         3) reprojection into low-dimensional space by a) using the covariance
 *         matrix b) computing the eigenvectors 4) drawing graph along new
 *         axises represented by eigenvectors
 */

public class HighDimEmbedAlgorithm extends AbstractAlgorithm {

    /** Set of nodes selected by user */
    private Selection selection;

    /** Value for the graph output scale */
    private Integer DEFAULTSCALE = new Integer(100);

    /** Dimensions for the High-Embedding inter step */
    private Integer DEFAULTHIGHEMBEDDIM = new Integer(2);

    /** true if Pivot Nodes should be labeled ascending to selecting */
    private boolean PIVOTLABELING = false;

    /** true if Dikstra should be used instead of BFS */
    private boolean DIJKSTRA = false;

    /** min dimensions of high embedding step */
    private Integer HIGHEMBEDDIMMIN = new Integer(1);

    /** Dimensions to be computed for output as default */
    private Integer DEFAULTTARGETDIM = new Integer(2);

    /** min dimension of drawing is 1 dimension */
    private Integer TARGETDIMMIN = new Integer(1);

    /** max dimensions for both high-embedding and output */
    private Integer DIMMAX = new Integer(Integer.MAX_VALUE);

    /** Distance from window rim in output graph */
    private int DistanceFromRim = 20;

    /** DOCUMENT ME! */
    // private int MAX_NODES = 20;

    /** Parameter array */
    private Parameter<?>[] Params;

    /** Parameters */
    private IntegerParameter hEDParams;

    private IntegerParameter tDParams;

    private IntegerParameter gsParams;

    private BooleanParameter pvLabelParams;

    private BooleanParameter DijkstraParams;

    /** general node ids */
    private HashMap<Node, Integer> NodeIdsGlob;

    /** general BFS instance */
    private BFS bfs = new BFS();

    private DijkstraAlgorithm Dijkstra = new DijkstraAlgorithm();

    /** Label color for Pivot Nodes in RGB-Value */
    private ColorAttribute colAttr = new ColorAttribute("", 0, 0, 0, 255);

    /** List of Nodes to calculate remaining nodes of non-processed graphs * */
    private List<Node> remainingNodes;

    // private int TempAmountNodes;

    /**
     * Constructs a new instance.
     */
    public HighDimEmbedAlgorithm() {
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "High-Dimensional Embedder";
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
        selection = ((SelectionParameter) params[0]).getSelection();
        DEFAULTTARGETDIM = ((IntegerParameter) params[1]).getInteger();
        DEFAULTHIGHEMBEDDIM = ((IntegerParameter) params[2]).getInteger();
        DEFAULTSCALE = ((IntegerParameter) params[3]).getInteger();
        PIVOTLABELING = ((BooleanParameter) params[4]).getBoolean()
                .booleanValue();
        DIJKSTRA = ((BooleanParameter) params[5]).getBoolean().booleanValue();
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getParameters()
     */
    /**
     * @see org.graffiti.plugin.AbstractParametrizable#getAlgorithmParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {

        SelectionParameter selParam = new SelectionParameter("",
                "<html>The selection to work on.<p>If empty, "
                        + "the whole graph is used.</html>");
        selParam.setSelection(new Selection("_temp_"));

        IntegerParameter targetDimParam = new IntegerParameter(
                DEFAULTTARGETDIM, "Target dimensions",
                "the number of dimensions (or axis) to "
                        + "be computed for drawing output", TARGETDIMMIN,
                DIMMAX, TARGETDIMMIN, DIMMAX);

        IntegerParameter highEmbedDimParam = new IntegerParameter(
                DEFAULTHIGHEMBEDDIM, "High-Embedding dimensions",
                "the number of dimensions to embed the "
                        + "graph in order to scatter properly (interstep)",
                HIGHEMBEDDIMMIN, DIMMAX, HIGHEMBEDDIMMIN, DIMMAX);

        IntegerParameter graphScaleParam = new IntegerParameter(DEFAULTSCALE,
                new Integer(0), DIMMAX, "Graph scale",
                "the value the output graph is multiplied with"
                        + "to define final size");

        BooleanParameter pivotLabelingParam = new BooleanParameter(
                PIVOTLABELING, "Pivot-labeling", "Label Pivots ascendingly");

        BooleanParameter DijkstraParam = new BooleanParameter(DIJKSTRA,
                "Dijkstra", "use Dijkstra instead of BFS");

        return new Parameter[] { selParam, targetDimParam, highEmbedDimParam,
                graphScaleParam, pivotLabelingParam, DijkstraParam };
        // , doubleParam, doubleParam2 };
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        PreconditionException errors = new PreconditionException();

        this.Params = getAlgorithmParameters();
        int NumberOfNodes = this.graph.getNumberOfNodes();
        hEDParams = (IntegerParameter) Params[2];
        // high embedding dimension parameters
        tDParams = (IntegerParameter) Params[1]; // target dimension parameters
        gsParams = (IntegerParameter) Params[3]; // output scale parameters
        pvLabelParams = (BooleanParameter) Params[4];
        DijkstraParams = (BooleanParameter) Params[5];

        if (NumberOfNodes <= 0) {
            errors.add("The graph is empty.");
        } else {

            /** restrict minimum high-embedding dimensions by 1 (HIGHEMBEDDIM) */
            if (hEDParams.getInteger().compareTo(hEDParams.getMin()) < 0) {
                errors
                        .add("High-embedding dimensions may not be smaller than 1.");
            }

            /** restrict minimum target dimensions by 1 (TARGETDIM) */
            if (tDParams.getInteger().compareTo(tDParams.getMin()) < 0) {
                errors.add("Target dimensions may not be smaller than 1.");
            }

            /** restrict maximum high-embedding dimensions by number of nodes */
            if ((NumberOfNodes > 0)
                    && ((new Integer(this.graph.getNumberOfNodes()))
                            .compareTo(hEDParams.getInteger()) < 0)) {
                errors
                        .add("High-embedding dimensions may not be greater than number of nodes."
                                + "\n  Current maximum is "
                                + NumberOfNodes
                                + ".");
            }

            /** restrict maximum target dimensions by number of nodes */
            if ((NumberOfNodes > 0)
                    && ((new Integer(this.graph.getNumberOfNodes()))
                            .compareTo(tDParams.getInteger()) < 0)) {
                errors
                        .add("Target dimensions may not be greater than number of nodes."
                                + "\n  Current maximum is "
                                + NumberOfNodes
                                + ".");
            }

            /**
             * restrict maximum target dimensions by maximum high-embedding
             * dimensions
             */
            if (tDParams.getInteger().compareTo(hEDParams.getInteger()) > 0) {
                errors
                        .add("Target dimensions may not be greater than High-embedding dimensions.");
            }

            /** restrict minimum graph scale to nothing */
            if (gsParams.getInteger().intValue() < 0) {
                errors.add("Graph scale may not be smaller than 0.");
            }
        }

        if (!errors.isEmpty())
            throw errors;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() throws RuntimeException {

        long StartTime, EndTime;

        StartTime = System.currentTimeMillis();

        Node PivotNode1 = null; // start Pivot node for High-Embedding
        Node[] Pivots = new Node[hEDParams.getInteger().intValue()];
        // List of all chosen Pivot nodes

        remainingNodes = new LinkedList<Node>(this.graph.getNodes());

        if (selection.getNodes().isEmpty()) // if no nodes were selected
        {
            PivotNode1 = this.graph.getNodes().get(0);
        } else
        // if nodes were selected
        {
            PivotNode1 = selection.getNodes().get(0);
        }

        double[][] HighEmbMatrix;
        double[][] centeredHighEmbMatrix;
        int UsedGraphAmountNodes;

        HashMap<Node, Integer> NodeIds = new HashMap<Node, Integer>();
        Collection<Node> UsedNodes;

        /**
         * Starting the Algorithm Step I: 1) Pre-Sort and Identifying Nodes with
         * numbers for Matrix entry 2) BFS-iteration and minimum adaption 3)
         * maximum adaption 4) create high-dimensional embedding matrix
         */

        // graph.getListenerManager().transactionStarted(this);
        if (DijkstraParams.getBoolean().booleanValue() == false) {
            /*******************************************************************
             * /*********** B F S ********************
             * /**********************************************
             * 
             * /** 1) Presort per BFS beginning with selected Pivot-Node
             */

            bfs.reset();
            bfs.attach(this.graph);

            bfs.setSourceNode(PivotNode1);
            bfs.execute();
            bfs.postCheck(tDParams, hEDParams);

            /** Construction of the Arrays mwith minimum bfs distances */
            @SuppressWarnings("unchecked")
            Map<Node, Double> bfsNumsMin = (Map<Node, Double>) bfs.getResult()
                    .getResult().get("BfsDistances");

            /** Construction of Arrays to save to */
            Map<Node, Integer> bfsNums;

            @SuppressWarnings("unchecked")
            Collection<Node> bfsNodes = (Collection<Node>) bfs.getResult()
                    .getResult().get("BfsNodes");
            UsedNodes = bfsNodes;

            remainingNodes.removeAll(bfsNodes);

            UsedGraphAmountNodes = bfsNodes.size();
            // TempAmountNodes = UsedGraphAmountNodes;

            Node NewPivotNode = PivotNode1;

            Pivots[0] = PivotNode1;

            /** Embedding matrix */
            HighEmbMatrix = new double[hEDParams.getInteger().intValue()][UsedGraphAmountNodes];
            centeredHighEmbMatrix = new double[hEDParams.getInteger()
                    .intValue()][UsedGraphAmountNodes];
            /** fetch nodes from set calculated by bfs and appoint numbers */
            NodeIds = enumerateNodes(bfsNodes);
            NodeIdsGlob = NodeIds;

            /**
             * 2) BFS-Iteration and adaption of minimum bfs-numbers for
             * bottleneck-problem
             */

            /**
             * construct high-dimensional embedding matrix, appoint numbers to
             * every node and set the Minimum of Bfs distances
             */
            System.out.println("selecting " + hEDParams.getInteger().intValue()
                    + " Pivot nodes :\n");
            for (int m = 0; m < (hEDParams.getInteger().intValue()); m++) {
                Pivots[m] = NewPivotNode;

                // double sum = 0;
                double mean = 0;

                String MatrixPlusMean = new String("");

                bfs.setSourceNode(NewPivotNode);
                bfs.execute();

                @SuppressWarnings("unchecked")
                Map<Node, Integer> bfsNumsC = (Map<Node, Integer>) bfs
                        .getResult().getResult().get("BfsDistances");
                bfsNums = bfsNumsC;
                // CompNumbers"); // hole Knoten der bfs

                /** Calculating new nodes of embedding matrix */
                for (Iterator<Node> it = bfsNodes.iterator(); it.hasNext();) {
                    Node tempNode = it.next();

                    /** delete all Labels */
                    // String LABEL_PATH = GraphicAttributeConstants.LABEL;
                    // LabelAttribute labelAttr = (NodeLabelAttribute)
                    // tempNode.getAttribute(LABEL_PATH);
                    // labelAttr.setLabel("");
                    int NodeId = NodeIds.get(tempNode).intValue();

                    int tempNr = bfsNums.get(tempNode).intValue();
                    int minNr = bfsNumsMin.get(tempNode).intValue();

                    /** Construction of Matrix */
                    HighEmbMatrix[m][NodeId] = tempNr;
                    MatrixPlusMean = MatrixPlusMean.concat(tempNr + " ");

                    mean = mean
                            + ((1 / ((new Integer(UsedGraphAmountNodes))
                                    .doubleValue())) * tempNr);

                    /** update of minimum bfs list */
                    if (minNr > tempNr) {
                        bfsNumsMin.remove(tempNode);
                        bfsNumsMin.put(tempNode, (double) tempNr);
                    }
                }

                /** center Matrix by subtraction of average value */
                DecimalFormat format = new DecimalFormat();
                format.setMaximumFractionDigits(2);

                System.out.println(m + 1 + ". selected Pivot:\t"
                        + NodeIdsGlob.get(NewPivotNode).intValue()
                        + "\t\tMean:\t" + format.format(mean) + "\t\t"
                        + "HdE-Row\t" + m + ":\t" + MatrixPlusMean);

                for (int i = 0; i < UsedGraphAmountNodes; i++) {
                    centeredHighEmbMatrix[m][i] = HighEmbMatrix[m][i] - mean;
                }

                /** 3) calculating new pivot nodes by k-center problem approx. */
                NewPivotNode = calculateNewPivot(bfsNumsMin, bfsNodes);

                // NewPivotNode = calculateNewPivotperRandom();
                // NewPivotNode = calculateNewPivotperNgb();
            }

        } else {

            /*******************************************************************
             * /********** D I J K S T R A ***********
             * /*********************************************
             * 
             * 
             * /** 1) Presort per Dijkstra beginning with selected Pivot-Node
             */

            Dijkstra.reset();
            Dijkstra.attach(this.graph);

            Dijkstra.setSourceNode(PivotNode1);
            Dijkstra.check();
            Dijkstra.execute();
            Dijkstra.postCheck(tDParams, hEDParams);

            /** Construction of the Arrays mwith minimum bfs distances */
            @SuppressWarnings("unchecked")
            Map<Node, Double> DijkstraNumsMin = (Map<Node, Double>) Dijkstra
                    .getResult().getResult().get("DijkstraValues");

            /** ** Construction of the Array to save to */
            Map<Node, Double> DijkstraNums;
            @SuppressWarnings("unchecked")
            Collection<Node> DijkstraNodes = (Collection<Node>) Dijkstra
                    .getResult().getResult().get("DijkstraNodes");
            UsedNodes = DijkstraNodes;

            remainingNodes.removeAll(DijkstraNodes);

            UsedGraphAmountNodes = DijkstraNodes.size();
            // TempAmountNodes = UsedGraphAmountNodes;

            Node NewPivotNode = PivotNode1;

            Pivots[0] = PivotNode1;

            /** Embedding matrix */
            HighEmbMatrix = new double[hEDParams.getInteger().intValue()][UsedGraphAmountNodes];
            centeredHighEmbMatrix = new double[hEDParams.getInteger()
                    .intValue()][UsedGraphAmountNodes];

            /** fetch nodes from set calculated by Dijkstra and appoint numbers */
            NodeIds = enumerateNodes(DijkstraNodes);
            NodeIdsGlob = NodeIds;

            /**
             * 2) Dijkstra-Iteration and adaption of minimum Dijkstra-numbers
             * for bottleneck-problem
             */

            /**
             * construct high-dimensional embedding matrix, appoint numbers to
             * every node and set the Minimum of Dijkstra distances
             */
            System.out.println("selecting " + hEDParams.getInteger().intValue()
                    + " Pivot nodes :\n");
            for (int m = 0; m < (hEDParams.getInteger().intValue()); m++) {
                Pivots[m] = NewPivotNode;

                // double sum = 0;
                double mean = 0;

                String MatrixPlusMean = new String("");

                Dijkstra.setSourceNode(NewPivotNode);
                Dijkstra.execute();

                @SuppressWarnings("unchecked")
                Map<Node, Double> dijkstraNumsC = (Map<Node, Double>) Dijkstra
                        .getResult().getResult().get("DijkstraValues");
                DijkstraNums = dijkstraNumsC;
                // hole Knoten aus Dijkstra-Berechnung

                /** Calculate new coodrinates of embedding Matrix */
                for (Iterator<Node> it = DijkstraNodes.iterator(); it.hasNext();) {
                    Node tempNode = it.next();

                    int NodeId = NodeIds.get(tempNode).intValue();

                    double tempNr = DijkstraNums.get(tempNode);
                    double minNr = DijkstraNumsMin.get(tempNode);

                    /** Construction of the Matrix */
                    HighEmbMatrix[m][NodeId] = tempNr;
                    MatrixPlusMean = MatrixPlusMean.concat(tempNr + " ");

                    mean = mean
                            + ((1 / ((new Integer(UsedGraphAmountNodes))
                                    .doubleValue())) * tempNr);

                    /** update of minimum Dijkstra list */
                    if (minNr > tempNr) {
                        DijkstraNumsMin.remove(tempNode);
                        DijkstraNumsMin.put(tempNode, new Double(tempNr));
                    }
                }

                /** Centering Matrix */
                DecimalFormat format = new DecimalFormat();
                format.setMaximumFractionDigits(2);

                System.out.println(m + 1 + ". selected Pivot:\t"
                        + NodeIdsGlob.get(NewPivotNode).intValue()
                        + "\t\tMean:\t" + format.format(mean) + "\t\t"
                        + "HdE-Row\t" + m + ":\t" + MatrixPlusMean);

                for (int i = 0; i < UsedGraphAmountNodes; i++) {
                    centeredHighEmbMatrix[m][i] = HighEmbMatrix[m][i] - mean;
                }

                /** 3) Calculation of new nodes by k-center approx. */
                NewPivotNode = calculateNewPivot(DijkstraNumsMin, DijkstraNodes);

            }

        }

        /**
         * Step II: 1) create Matrix to the double[][]-array with Jama-Package
         * for easy use 2) calculate the covariance matrix 3) multiply with
         * 1/(number of nodes)
         * 
         * 4) compute the first k Eigenvectors to the corresponing greatest
         * Eigenvalues 5) find the first k Eigenvectors that correspond to the
         * greatest eigenvalues descending 6) reorder descending 7) norm the
         * eigenvectors
         */
        /** 1) - 3) */
        // Matrix HDEMatrix, tempMatrix, covarMatrix, transpMatrix;
        Matrix tempMatrix, covarMatrix, transpMatrix;

        // HDEMatrix =
        new Matrix(HighEmbMatrix);

        // int HDE_Rank = new SingularValueDecomposition(HDEMatrix).rank();

        System.out.println("\n\ncentered High-dimensional embedding Matrix : ");
        tempMatrix = new Matrix(centeredHighEmbMatrix);
        tempMatrix.print(6, 2);

        // int centeredHDE_Rank = new
        // SingularValueDecomposition(tempMatrix).rank();

        transpMatrix = tempMatrix.transpose();
        // System.out.println("\ntransposed centered High-dimensional embedding
        // Matrix : ");
        // transpMatrix.print(6,2);

        covarMatrix = tempMatrix.times(transpMatrix);
        // .timesEquals(1/((new Integer(UsedGraphAmountNodes)).doubleValue()));

        int matrDim = hEDParams.getInteger().intValue();
        System.out.println("\n" + matrDim + "x" + matrDim
                + " Covariance-Matrix:");
        covarMatrix.print(6, 2);

        /** 4) Eigenvector and -value computation */

        EigenvalueDecomposition EigenVs = new EigenvalueDecomposition(
                covarMatrix);
        Matrix EigenVectors, EigenValues;// , AEigVMatrix, TestMatrix;

        EigenValues = EigenVs.getD();
        EigenVectors = EigenVs.getV();

        double traceEigVals = EigenValues.trace();
        if (traceEigVals == 0) {
            traceEigVals = 1;
        }

        DecimalFormat punct = new DecimalFormat();
        punct.setMaximumFractionDigits(3);

        // AEigVMatrix =
        covarMatrix.times(EigenVectors);
        // TestMatrix =
        EigenVectors.times(EigenValues);

        /** 5) compute the first k Eigenvectors */

        Matrix TargetEigenVectors;
        TargetEigenVectors = EigenVectors.getMatrix(0, EigenVectors
                .getRowDimension() - 1, EigenVectors.getColumnDimension()
                - tDParams.getInteger().intValue(), EigenVectors
                .getColumnDimension() - 1);

        /**
         * 6) Re-order Eigenvecor-Matrix descending, starting with Eigenvector
         * corresponding to the greatest Eigenvalue
         */

        Matrix OppEinheitsMatrix = new Matrix(tDParams.getInteger().intValue(),
                tDParams.getInteger().intValue());

        int i2 = 0;
        for (int i1 = 0; i1 < tDParams.getInteger().intValue(); i1++) {
            i2 = tDParams.getInteger().intValue() - 1 - i1;

            OppEinheitsMatrix.set(i1, i2, 1);

        }

        Matrix OrderedTargetEigenVectors = TargetEigenVectors
                .times(OppEinheitsMatrix);

        System.out
                .println("\nordered target Eigenvector-Matrix (descending): ");
        OrderedTargetEigenVectors.print(6, 2);

        /**
         * Step III: 1) perform basis tranformation save new coordinates 2)
         * translate into gravisto origin 3) output
         */

        Matrix targetCoordsMatrix;
        // CoordinateAttribute coordAttr;

        /** 1) basis transformation */
        targetCoordsMatrix = transpMatrix.times(OrderedTargetEigenVectors);

        /** 2) translate coordinates into gravisto origin */
        double[][] targetCoordsArray = targetCoordsMatrix.getArray();
        double coordsMaximum = 0;
        // shows abs maximum of negative values in coordinates-matrix-column
        double positiveValueMin = 0;
        // shows minimum of positive values in coordinate-matrix-column
        double[] coordsMaxima = new double[targetCoordsMatrix
                .getColumnDimension()];

        /**
         * maximum computation of target coords in order to place graph in
         * gravisto window
         */
        boolean NegativeValues = false;
        for (int j = 0; j < tDParams.getInteger().intValue(); j++) {
            for (int i = 0; i < UsedGraphAmountNodes; i++) {
                positiveValueMin = Math.max(positiveValueMin,
                        targetCoordsArray[i][j]);

                if ((Math.abs(targetCoordsArray[i][j]) > coordsMaximum)
                        && (targetCoordsArray[i][j] < 0)) {
                    NegativeValues = true;
                    coordsMaximum = Math.abs(targetCoordsArray[i][j]);
                }
            }

            // if (NegativeValues = false) //TODO:
            if (!NegativeValues) {
                for (int i = 0; i < UsedGraphAmountNodes; i++) {
                    if ((Math.abs(targetCoordsArray[i][j]) < positiveValueMin)) {
                        positiveValueMin = targetCoordsArray[i][j];
                        coordsMaximum = -Math.abs(targetCoordsArray[i][j]);
                    }
                }
            }

            coordsMaxima[j] = coordsMaximum;
            coordsMaximum = 0;
            positiveValueMin = 0;
        }

        /**
         * Create Maxima Matrix of negative Values of the coordinate Matrix in
         * order to set layot to left upper area
         */

        System.out.println("\ntranslation values "
                + "(max. absolute negative / min positive values):\n");

        punct.setMaximumFractionDigits(2);

        Matrix TranslationMatrix = new Matrix(UsedGraphAmountNodes, tDParams
                .getInteger().intValue());

        for (int j = 0; j < tDParams.getInteger().intValue(); j++) {
            for (int i = 0; i < UsedGraphAmountNodes; i++) {
                TranslationMatrix.set(i, j, coordsMaxima[j]);
            }

            System.out.print("\t" + punct.format(coordsMaxima[j]));
        }

        targetCoordsMatrix.plusEquals(TranslationMatrix);
        System.out.println("\n\n\n" + UsedGraphAmountNodes + "x"
                + tDParams.getInteger().intValue()
                + " translated target-coordinates Matrix: ");
        targetCoordsMatrix.print(6, 2);

        /**
         * 3) adapt coordinates to every single node for output, label first
         * PivotNode
         */

        graph.getListenerManager().transactionStarted(this);

        double maxX = 0;
        double maxY = 0;

        double newX = 0;
        double newY = 0;

        int distanceFromRim = DistanceFromRim; // Distance to window rim

        CoordinateAttribute nodeCoord;

        for (Iterator<Node> it = UsedNodes.iterator(); it.hasNext();) {
            Node tempNode = it.next();
            nodeCoord = (CoordinateAttribute) tempNode
                    .getAttribute(GraphicAttributeConstants.COORD_PATH);

            int NodeId = NodeIds.get(tempNode).intValue();

            /** Adaption of coordinates */
            newX = targetCoordsMatrix.get(NodeId, 0)
                    * gsParams.getInteger().intValue() + distanceFromRim;

            nodeCoord.setX(newX);
            nodeCoord.setY(distanceFromRim);

            if (newX > maxX) {
                maxX = newX;
            }

            /** more than 1 target Dimension */
            if (tDParams.getInteger().intValue() > 1) {
                newY = targetCoordsMatrix.get(NodeId, 1)
                        * gsParams.getInteger().intValue() + distanceFromRim;
                nodeCoord.setY(newY);

                if (newY > maxY) {
                    maxY = newY;
                }
            }

        }

        /** move remaining nodes of non processed partial graphs * */
        /**
         * to avoid overlapping between processed graph and partial remaining
         * graphs*
         */
        double minX = 0;
        double minY = 0;

        boolean changeX = false;
        // boolean changeY = false;
        boolean change = false;

        double tempX = 0;
        double tempY = 0;

        for (Iterator<Node> it = remainingNodes.iterator(); it.hasNext();) {
            Node tempNode = it.next();
            nodeCoord = (CoordinateAttribute) tempNode
                    .getAttribute(GraphicAttributeConstants.COORD_PATH);

            /** check if coordinates overlap */

            tempX = nodeCoord.getX();
            tempY = nodeCoord.getY();

            if ((tempX - maxX <= -minX) && (tempY - maxY <= -minY)) {
                minX = maxX - tempX;
                minY = maxY - tempY;
                change = true;
            }

        }

        /**
         * select greater value to distinguish between moving in x- or
         * y-direction
         */
        if (minX > minY) {
            // changeY = true;
        } else {
            changeX = true;
        }

        /** if moving necessary */
        if (change == true) {
            for (Iterator<Node> it = remainingNodes.iterator(); it.hasNext();) {
                Node tempNode = it.next();
                nodeCoord = (CoordinateAttribute) tempNode
                        .getAttribute(GraphicAttributeConstants.COORD_PATH);

                /**
                 * adapt coordinates to old value plus length of overlapping
                 * distance (edges are not obeyed !!!)
                 */

                if (changeX == true) {
                    nodeCoord.setX(nodeCoord.getX() + minX + 10
                            * distanceFromRim);
                } else {
                    nodeCoord.setY(nodeCoord.getY() + minY + 10
                            * distanceFromRim);
                }
            }
        }

        /*
         * GraffitiSingleton.getInstance().getMainFrame().getActiveEditorSession(
         * ) .getActiveView().getComponentElementMap().clear();
         * GraffitiSingleton
         * .getInstance().getMainFrame().getActiveEditorSession()
         * .getActiveView().completeRedraw();
         */

        System.out.print("\nCreating View.......");

        graph.getListenerManager().transactionFinished(this);

        /** 4) label and fill pivots ascendingly if selected in main menu */

        // graph.getListenerManager().transactionStarted(this);
        if (pvLabelParams.getBoolean().booleanValue() == true) {

            bfs.setLabel(Pivots[0], "Pivot", new ColorAttribute("", 0, 0, 0,
                    255));

            // LabelAttribute nodeLabelAttr =
            new LabelAttribute("label");

            for (int l = 1; l < hEDParams.getInteger().intValue(); l++) {
                bfs.setLabel(Pivots[l], new Integer(l).toString(), colAttr);
                // setColor(Pivots[l], "graphics.fillcolor", 90, 150, 220, 255);
            }
        }

        // graph.getListenerManager().transactionFinished(this);
        /** End of Algorthm */

        EndTime = System.currentTimeMillis();

        /*
         * graph.getListenerManager().transactionStarted(this);
         * GraffitiSingleton
         * .getInstance().getMainFrame().getActiveEditorSession()
         * .getActiveView().getComponentElementMap().clear();
         * GraffitiSingleton.getInstance
         * ().getMainFrame().getActiveEditorSession()
         * .getActiveView().completeRedraw();
         * graph.getListenerManager().transactionFinished(this);
         */

        System.out.println("done\n");

        System.out.println("\n\nDuration:\t" + (EndTime - StartTime) + " ms");

        System.out
                .println("\n\n\nEigenvalues (ascending order)\t\t\toverall variance ratio:\n");
        for (int i = 0; i < EigenValues.getColumnDimension(); i++) {
            System.out.println(i + 1 + ":\t" + EigenValues.get(i, i)
                    + "\t\t\t\tratio: \t"
                    + punct.format(EigenValues.get(i, i) / traceEigVals * 100)
                    + " %");
        }

        System.out.println("\n\n\n");

    }

    /**
     * print the Matrix on System.out nad in an Exception (optional)
     * 
     * @param HighEmbMatrix
     *            2-D Matrix as a double[][]-array
     * @param m
     *            Dimension m of mxn-Matrix
     * @param n
     *            Dimension n of mxn-Matrix
     * @throws RuntimeException
     */
    public void printMatrix(String Name, double[][] HighEmbMatrix, int m,
            int n, int length) throws RuntimeException {
        DecimalFormat punct = new DecimalFormat();
        punct.setMaximumFractionDigits(length);

        String MatrixStr = new String(m + "x" + n + Name + ":\n\n");
        String outStr = new String("");
        System.out.println("");

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                String tempStr = (punct.format(new Double(HighEmbMatrix[i][j])))
                        .toString();
                outStr = outStr.concat(tempStr + "  ");
                MatrixStr = (MatrixStr.concat(tempStr) + "    ");
            }

            System.out.println("row " + i + ":   " + outStr);
            outStr = "";
            MatrixStr = (MatrixStr + "\n");
        }
    }

    /**
     * identifies the calculated nodes with an Integer
     * 
     * @param UsedNodes
     *            Collection that contains the calculated Dijkstra or BFS nodes
     * @return Hashmap with "key = node", "value = ID"
     * 
     */
    public HashMap<Node, Integer> enumerateNodes(Collection<Node> UsedNodes) {
        int i = 0;
        HashMap<Node, Integer> NodeIds = new HashMap<Node, Integer>();

        for (Iterator<Node> it = UsedNodes.iterator(); it.hasNext();) {
            Node tempNode = it.next();

            // coordAttr = (CoordinateAttribute)tempNode
            // .getAttribute(GraphicAttributeConstants.COORD_PATH);

            NodeIds.put(tempNode, new Integer(i));
            System.out.println("numbering nodes:\t " + i);
            // + "\t\t" + " coordinates:\t" + coordAttr.getX() + "\t" + " " +
            // coordAttr.getY());

            i++;

            bfs.setLabel(tempNode, "", colAttr);

            /*
             * String p = "graphics.fillcolor"; if (tempNode.getInteger(p +
             * ".red") == 90 && tempNode.getInteger(p + ".green") == 150 &&
             * tempNode.getInteger(p + ".blue") == 220) { setColor(tempNode, p,
             * 0, 0, 0, 0); }
             */

        }

        System.out.println("\n\n");
        return NodeIds;
    }

    /**
     * calculate new Pivots by regarding the maximum distance of the minimum bfs
     * or dijkstra-numbers ( ^= solve the k-center-problem approximation)
     * 
     * @param UsedNumsMin
     *            The map of minimum current bfs or Dijkstra-numbers
     * @param UsedNodes
     *            The Collection
     * @return the new pivot node
     */
    public Node calculateNewPivot(Map<Node, ? extends Number> UsedNumsMin,
            Collection<Node> UsedNodes) {
        int maxBFSDistNum = 0;
        double maxDijkstraDistNum = 0;

        Node returnNode = null;

        boolean tempBool = DijkstraParams.getBoolean().booleanValue();

        for (Iterator<Node> it2 = UsedNodes.iterator(); it2.hasNext();) {

            Node tempNode = it2.next();

            /** check wether BFS or Dijkstra */
            /** DIJKSTRA: */
            if (tempBool == true) {
                double tempDist = (UsedNumsMin.get(tempNode)).doubleValue();

                if (maxDijkstraDistNum <= tempDist) {
                    returnNode = tempNode;
                    maxDijkstraDistNum = tempDist;
                }
            }

            /** BFS: */
            else {
                int tempDist = UsedNumsMin.get(tempNode).intValue();

                if (maxBFSDistNum < tempDist) {
                    returnNode = tempNode;
                    maxBFSDistNum = tempDist;
                }
            }
        }

        return returnNode;
    }

    /***************************************************************************
     * set the labels of the nodes in graph/
     * 
     * @param Nodes
     *            Collection of Nodes to set labels
     * @param label
     *            The new label as String
     */
    public void setLabels(Collection<Node> Nodes, String label) {
        LabelAttribute labelAttr = new NodeLabelAttribute("label");

        graph.getListenerManager().preAttributeChanged(
                new AttributeEvent(labelAttr));

        for (Iterator<Node> it = Nodes.iterator(); it.hasNext();) {
            Node tempNode = it.next();
            labelAttr.setLabel(label);

            tempNode.addAttribute(labelAttr, "");

            graph.getListenerManager().postAttributeChanged(
                    new AttributeEvent(labelAttr));

        }
    }

    // /**
    // * Sets the color of the given node to the given color.
    // *
    // * @param n Node
    // * @param p
    // * @param r red
    // * @param g green
    // * @param b blue
    // * @param t transparency
    // */
    // private void setColor(Node n, String p, int r, int g, int b, int t)
    // {
    //
    // n.setInteger(p + ".transparency", t);
    // n.setInteger(p + ".red", r);
    // n.setInteger(p + ".green", g);
    // n.setInteger(p + ".blue", b);
    // }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
