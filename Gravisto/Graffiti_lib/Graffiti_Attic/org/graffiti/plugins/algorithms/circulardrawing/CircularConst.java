package org.graffiti.plugins.algorithms.circulardrawing;

/**
 * @author demirci Created on Jul 21, 2005 The class contains the constants and
 *         the methods.
 */
public class CircularConst {

    private static String SELECT_ALGORITHM_CIRCULAR = "1";

    private static String SELECT_ALGORITHM_CIRCULAR1 = "0";

    private static String SELECT_ALGORITHM_CIRCULAR2 = "0";

    private static String SELECT_ALGORITHM_DFSCIRCULAR = "0";

    public static int TEST = 0;

    private static int SELECT_CPP_CLOCKWISE = 0;

    private static int SELECT_CPP_ANTICLOCKWISE = 0;

    private static int SELECT_CPP_DESCANDING = 0;

    private static int SELECT_CPP_ASCANDING = 0;

    private static int SELECT_CPP_RANDOM = 0;

    private int SELECT_ALGORITHM;

    public static int SELECT_CPP = 0;

    private static int DFSTREE;

    public static int CIRCULAR_STATE = 0;

    public static int CIRCULARI_STATE = 0;

    public static int CIRCULARII_STATE = 0;

    public static int DFSCIRCULAR_STATE = 0;

    public static int PERMUTATION_STATE = 0;

    public static int PERMUTATION_ADJ_LIST = 0;

    public static int REM_NODES_STATE = 0;

    public static int PERMUTATION_REM_NODES = 0;

    public static int NODE_CATEGORIE_TABLE_STATE = 0;

    public static int PERMUTATION_CATEGORIE_TABLE = 0;

    public static int STATISTIC_FILE_STATE = 0;

    public static int GRAPH_STATE = 0;

    public static int SESSION_STATE = 0;

    public static String GRAPH_HASHCODE = "";

    public static Object[][] ststkData = new Object[6][4];

    /** DOCUMENT ME! */
    private static int runtime = 0;

    private static int crossEnum = 1;

    /**
     * Comment for <code>ALGO_RUNTIME</code>
     */
    public static Object[] ALGO_RUNTIME = { null, null, null };

    public static Object[] CPP_DATA = { null, null, null, null };

    public static Object[] CPPI_DATA = { null, null, null, null };

    public static Object[] CPPII_DATA = { null, null, null, null };

    /**
     * Konstruktur.
     */
    public CircularConst() {
        SELECT_ALGORITHM = 0;
    }

    /**
     * Initialize the statistic table
     */
    public static void initStstkData() {
        for (int i = 0; i < 6; i++) {
            System.out.print("    row " + i + ":");
            for (int j = 0; j < 4; j++) {
                ststkData[i][j] = null;
                System.out.print("    column " + j + ":");
                System.out.print("  " + ststkData[i][j]);
            }
            System.out.println();
        }
        System.out.println("--------------------------");
    }

    /**
     * @return
     */
    public int getFileState() {
        return STATISTIC_FILE_STATE;
    }

    /**
     * @param i
     */
    public void setFileState(int i) {
        STATISTIC_FILE_STATE = i;
    }

    /**
     * @return
     */
    public int getDfsTree() {
        return DFSTREE;
    }

    /**
     * Activate the dfs tree analyse.
     * 
     * @param i
     */
    public void setDfsTree(int i) {
        DFSTREE = i;
    }

    /**
     * @return
     */
    public int getRuntime() {
        return runtime;
    }

    /**
     * Activate the runtime analyse.
     */
    public void activateRuntime() {
        runtime = 1;
        crossEnum = 0;
    }

    /**
     * Deactivate the runtime analyse.
     */
    public void deactivateRuntime() {
        runtime = 0;
    }

    /**
     * Activate the permutation of the adjacency lists.
     */
    public void activatePermutation() {
        PERMUTATION_ADJ_LIST = 1;
    }

    /**
     * Deactivate the permutation of the adjacency lists.
     */
    public void deactivatePermutation() {
        PERMUTATION_ADJ_LIST = 0;
    }

    /**
     * Activate the permutation of the remaining nodes.
     */
    public void activatePermutationOfRemnodes() {
        PERMUTATION_REM_NODES = 1;
    }

    /**
     * Deactivate the permutation of the remaining nodes.
     */
    public void deactivatePermutationOfRemnodes() {
        PERMUTATION_REM_NODES = 0;
    }

    /**
     * Activate the permutation of the node categorie table.
     */
    public void activatePermutationOfNodeCategorieTable() {
        PERMUTATION_CATEGORIE_TABLE = 1;
    }

    /**
     * Deactivate the permutation of the node categorie table.
     */
    public void deactivatePermutationOfNodeCategorieTable() {
        PERMUTATION_CATEGORIE_TABLE = 0;
    }

    public int getPermutation() {
        return PERMUTATION_ADJ_LIST;
    }

    public int getCrossEnum() {
        return crossEnum;
    }

    /**
     * Activate the crossing enumeration.
     */
    public void activateCrossEnum() {
        crossEnum = 1;
    }

    /**
     * deactivate the crossing enumeration.
     */
    public void deactivateCrossEnum() {
        crossEnum = 0;
    }

    /**
     * @param i
     * @return
     */
    public String getAlgorithm(int i) {
        String SELECT_ALGORITHM = "-1";
        if (i == 0) {
            SELECT_ALGORITHM = SELECT_ALGORITHM_CIRCULAR;
        } else if (i == 1) {
            SELECT_ALGORITHM = SELECT_ALGORITHM_CIRCULAR1;
        } else if (i == 2) {
            SELECT_ALGORITHM = SELECT_ALGORITHM_CIRCULAR2;
        } else if (i == 3) {
            SELECT_ALGORITHM = SELECT_ALGORITHM_DFSCIRCULAR;
        } else {
            SELECT_ALGORITHM = SELECT_ALGORITHM_CIRCULAR;
        }
        return SELECT_ALGORITHM;
    }

    /**
     * @param selectAlgo
     */
    public void deactivateAlgorithm(String selectAlgo) {
        if (selectAlgo.equals("0")) {
            SELECT_ALGORITHM_CIRCULAR = "0";
        } else if (selectAlgo.equals("1")) {
            SELECT_ALGORITHM_CIRCULAR1 = "0";
        } else if (selectAlgo.equals("2")) {
            SELECT_ALGORITHM_CIRCULAR2 = "0";
        } else if (selectAlgo.equals("3")) {
            SELECT_ALGORITHM_DFSCIRCULAR = "0";
        }

    }

    /**
     * @param selectAlgo
     */
    public void activateAlgorithm(String selectAlgo) {
        if (selectAlgo.equals("0")) {
            SELECT_ALGORITHM_CIRCULAR = "1";
            SELECT_ALGORITHM = 0;
            deactivateAlgorithm("1");
            deactivateAlgorithm("2");
            deactivateAlgorithm("3");
        } else if (selectAlgo.equals("1")) {
            SELECT_ALGORITHM_CIRCULAR1 = "1";
            SELECT_ALGORITHM = 1;
            deactivateAlgorithm("0");
            deactivateAlgorithm("2");
            deactivateAlgorithm("3");
        } else if (selectAlgo.equals("2")) {
            SELECT_ALGORITHM_CIRCULAR2 = "1";
            SELECT_ALGORITHM = 2;
            deactivateAlgorithm("0");
            deactivateAlgorithm("1");
            deactivateAlgorithm("3");
        } else if (selectAlgo.equals("3")) {
            SELECT_ALGORITHM_DFSCIRCULAR = "1";
            SELECT_ALGORITHM = 3;
            deactivateAlgorithm("0");
            deactivateAlgorithm("1");
            deactivateAlgorithm("2");
        }
    }

    /**
     * @param selectAlgo
     */
    public void activateCpp(int cpp) {
        if (cpp == 0) {
            SELECT_CPP_CLOCKWISE = 1;
            SELECT_CPP_ANTICLOCKWISE = 0;
            SELECT_CPP_DESCANDING = 0;
            SELECT_CPP_ASCANDING = 0;
            SELECT_CPP_RANDOM = 0;
        } else if (cpp == 1) {
            SELECT_CPP_ANTICLOCKWISE = 1;
            SELECT_CPP_CLOCKWISE = 0;
            SELECT_CPP_DESCANDING = 0;
            SELECT_CPP_ASCANDING = 0;
            SELECT_CPP_RANDOM = 0;
        } else if (cpp == 2) {
            SELECT_CPP_DESCANDING = 1;
            SELECT_CPP_ANTICLOCKWISE = 0;
            SELECT_CPP_CLOCKWISE = 0;
            SELECT_CPP_ASCANDING = 0;
            SELECT_CPP_RANDOM = 0;
        } else if (cpp == 4) {
            SELECT_CPP_ASCANDING = 1;
            SELECT_CPP_CLOCKWISE = 0;
            SELECT_CPP_ANTICLOCKWISE = 0;
            SELECT_CPP_DESCANDING = 0;
            SELECT_CPP_RANDOM = 0;
        } else if (cpp == 5) {
            SELECT_CPP_RANDOM = 1;
            SELECT_CPP_CLOCKWISE = 0;
            SELECT_CPP_ANTICLOCKWISE = 0;
            SELECT_CPP_DESCANDING = 0;
            SELECT_CPP_ASCANDING = 0;
        }
    }

    /**
     * @param selectAlgo
     */
    public void deactivateCpp(int cpp) {
        if (cpp == 0) {
            SELECT_CPP_CLOCKWISE = 0;
        } else if (cpp == 1) {
            SELECT_CPP_ANTICLOCKWISE = 0;
        } else if (cpp == 2) {
            SELECT_CPP_DESCANDING = 0;
        } else if (cpp == 4) {
            SELECT_CPP_ASCANDING = 0;
        } else if (cpp == 5) {
            SELECT_CPP_RANDOM = 0;
        }
    }
}
