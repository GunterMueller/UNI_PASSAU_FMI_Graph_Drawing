package org.graffiti.plugins.algorithms.cyclicLeveling.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringTokenizer;

/**
 * Creates a connection to a database and provides functionality to send SQL
 * statements to the specified database
 */

public class Connector {

    /**
     * Boolean driverLoaded specifies if the database driver is already loaded
     */
    private boolean driverLoaded = false;

    /** This will make SQL Queries more readable */
    private static final String KOMMA = ", ";

    /**
     * The current connection
     */
    private Connection currentConn;

    /**
     * Creates a new connection to the database
     * 
     * @return Connection Newly created connection
     */
    public Connection createConnection() {
        Connection conn = null;
        // Load the database driver if not already loaded
        if (!driverLoaded) {
            try {
                Class.forName(Config.dbDriver);
            } catch (ClassNotFoundException e) {
                System.out.println(e);
                System.exit(1);
            }
            driverLoaded = true;
        }
        // Create a connection
        try {
            conn = DriverManager.getConnection(Config.dbUrl, Config.dbUser,
                    Config.dbPassword);
        } catch (SQLException e1) {
            System.out.println("Could not create connection.\n" + e1);
            e1.printStackTrace();
            System.exit(1);
        }

        this.currentConn = conn;
        return conn;
    }

    /**
     * Closes the connection
     */
    public void closeConnection() {
        try {
            currentConn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inserts the result of an experiment into the database.
     * 
     * @param exp
     */
    public void addResults(ExperimentBean exp) {

        if (currentConn == null) {
            createConnection();
        }

        Statement stmt = null;
        try {
            stmt = currentConn.createStatement();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String graphID = exp.getGraphID();
        int numberOfNodes = exp.getNumberOfNodes();
        int numberOfEdges = exp.getNumberOfEdges();
        int numberOfLevels = exp.getNumberOfLevels();
        int width = exp.getWidth();
        String algorithm = exp.getAlgorithm();
        int sumOfEdges = exp.getSumOfEdges();
        long time = exp.getTime();
        String sourceNode = exp.getSourceNode();
        String spec1 = exp.getSpec1();
        String spec2 = exp.getSpec2();
        String reserve1 = exp.getReserve1();
        String reserve2 = exp.getReserve2();

        try {
            stmt
                    .executeUpdate("INSERT INTO cyclicleveling.results ("
                            + "graphID, numberOfNodes, numberOfEdges, numberOfLevels, width, algorithm, sumOfEdges, "
                            + "time, sourceNode, spec1, spec2, reserve1, reserve2) VALUES ("
                            + sqlString(graphID)
                            + KOMMA
                            + sqlString(Integer.toString(numberOfNodes))
                            + KOMMA
                            + sqlString(Integer.toString(numberOfEdges))
                            + KOMMA
                            + sqlString(Integer.toString(numberOfLevels))
                            + KOMMA
                            + sqlString(Integer.toString(width))
                            + KOMMA
                            + sqlString(algorithm)
                            + KOMMA
                            + sqlString(Integer.toString(sumOfEdges))
                            + KOMMA
                            + sqlString(Long.toString(time))
                            + KOMMA
                            + sqlString(sourceNode)
                            + KOMMA
                            + sqlString(spec1)
                            + KOMMA
                            + sqlString(spec2)
                            + KOMMA
                            + sqlString(reserve1)
                            + KOMMA
                            + sqlString(reserve2)
                            + ")");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            stmt.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Returns a string for usage in an SQL statement. This includes returning
     * empty string when value is null and wrapping strings with appropriate
     * quotation marks.
     * 
     * @param data
     *            String to use in an SQL statement
     * @return SQL Version of the string
     */
    private static String sqlString(String data) {
        if (data == null)
            return "''";
        else {
            String newString = "";
            StringTokenizer tokens = new StringTokenizer(data, "'", true);
            while (tokens.hasMoreTokens()) {
                String tok = tokens.nextToken();
                if (tok.equals("'")) {
                    newString += "''";
                } else {
                    newString += tok;
                }
            }
            return "'" + newString + "'";
        }
    }

    /**
     * Returns true if the results of the experiment with the given parameter
     * are already in the database
     * 
     * @param graph
     * @param alg
     * @param levels
     * @param width
     * @return true if the computation was already done
     */
    public boolean inDatabase(String graph, String alg, int levels, int width) {
        boolean inDatabase = false;

        if (currentConn == null) {
            createConnection();
        }

        Statement stmt = null;
        try {
            stmt = currentConn.createStatement();

            ResultSet res = stmt
                    .executeQuery("SELECT * FROM results WHERE graphID = "
                            + sqlString(graph) + " AND algorithm = "
                            + sqlString(alg) + " AND numberOfLevels = "
                            + levels + " AND width = " + width);

            if (res.next()) {
                inDatabase = true;
            }

            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return inDatabase;
    }

    /**
     * Returns true if the results of the experiment with the given parameter
     * are already in the database
     * 
     * @param graph
     * @param alg
     * @param levels
     * @param width
     * @return true if the computation was already done
     */
    public boolean inDatabase(String graph, String alg, int levels, int width,
            String sourceNode) {
        boolean inDatabase = false;

        if (currentConn == null) {
            createConnection();
        }

        Statement stmt = null;
        try {
            stmt = currentConn.createStatement();

            ResultSet res = stmt
                    .executeQuery("SELECT * FROM results WHERE graphID = "
                            + sqlString(graph) + " AND algorithm = "
                            + sqlString(alg) + " AND numberOfLevels = "
                            + levels + " AND width = " + width
                            + " AND sourceNode = " + sqlString(sourceNode));

            if (res.next()) {
                inDatabase = true;
            }

            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return inDatabase;
    }

    public static void main(String[] args) {
        new Connector();
    }

    public Connection getCurrentConn() {
        return currentConn;
    }

    public void setCurrentConn(Connection currentConn) {
        this.currentConn = currentConn;
    }

}
