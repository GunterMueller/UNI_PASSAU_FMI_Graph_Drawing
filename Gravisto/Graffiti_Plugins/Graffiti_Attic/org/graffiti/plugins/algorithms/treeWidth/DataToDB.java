// =============================================================================
//
//   DatetoDB.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$
package org.graffiti.plugins.algorithms.treeWidth;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

/**
 * Computes the upper bounds with the both Algorithms and writes in the
 * Database.
 * 
 * @author wangq
 * @version $Revision$ $Date$
 */
public class DataToDB {
    /** ENTER YOUR DATABASE DRIVER HERE */
    public static final String dbDriver = "org.postgresql.Driver";

    /** ENTER YOUR DATABASE HOST HERE */
    public static final String dbHost = "snickers.fmi.uni-passau.de";

    /** ENTER YOUR DATABASE NAME HERE */
    public static final String dbName = "wang";

    /** ENTER YOUR DATABASE USERNAME HERE */
    public static final String dbUser = "wang";

    /** ENTER YOUR DATABASE PASSWORD HERE */
    public static final String dbPassword = " ";

    /** The logger to inform and warn the user */
    private static final Logger logger = Logger.getLogger(DataToDB.class
            .getName());
    Connection conn = null;

    /**
     * Writes the output to the database specified by the class attributes
     * above. If necessary calls a method to create the tables.
     */
    public void writeToDB() {
        /** Load the database driver. */
        try {
            logger.info("Loading JDBC Driver");
            Class.forName(dbDriver);
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found");
            return;
        }

        /** Open a database connection. */
        try {
            logger.info("Opening Database Connection");
            conn = DriverManager.getConnection("jdbc:postgresql://" + dbHost
                    + "/" + dbName, dbUser, dbPassword);
            System.out.println(conn.toString());
            createTable(conn);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        /** Close the database connection in every case. */
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                logger.fine("Exception while closing Database Connection");
            }
        }
    }

    /**
     * Makes a "test"-SQL statement to check if the tables already exist. If not
     * creates them.
     * 
     * @param conn
     *            the database connection.
     * @throws SQLException
     *             for errors during the creation of the tables.
     */
    private static void createTable(Connection conn) throws SQLException

    {
        boolean tablesExist = true;
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://" + dbHost
                    + "/" + dbName, dbUser, dbPassword);
            Statement stmt = conn.createStatement();
            stmt.executeQuery("SELECT * FROM graphs;");
            stmt.executeQuery("DROP TABLE graphs;");
            stmt.close();
            logger.info("Tables already exist and therefore will not be "
                    + "created.");
        } catch (SQLException e) {
            Statement stmt = conn.createStatement();
            try {
                stmt.executeQuery("DROP TABLE graphs;");
                stmt.close();
            } catch (SQLException s) {
            }
            logger.fine("No proper tables exist in database. Tables will be "
                    + "created");
            tablesExist = false;
        }
        if (!tablesExist) {
            Statement stmt = conn.createStatement();

            stmt.executeUpdate("CREATE TABLE graphs ( "
                    + "  graphid INTEGER Primary Key, "
                    + "  numOfNodes INTEGER not null, "
                    + "  edge INTEGER not null, " + "  low INTEGER not null, "
                    + "  upper1 INTEGER not null, "
                    + "  time1 BIGINT not null, "
                    + "  upper2 INTEGER not null, "
                    + "  time2 BIGINT not null " + "  );");

            stmt.close();

        }
    }

    public void write(int id, int node, int edge, int low, int up1, long time1,
            int up2, long time2) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:postgresql://"
                + dbHost + "/" + dbName, dbUser, dbPassword);

        Statement stmt = conn.createStatement();

        stmt.executeUpdate("INSERT INTO graphs (graphid," + " numOfNodes,"
                + " edge," + " low," + " upper1," + " time1," + " upper2, "
                + "time2) values(" + id + "," + node + "," + edge + "," + low
                + "," + up1 + "," + time1 + "," + up2 + "," + time2 + ");");
        System.out.println("insert");
        stmt.close();

    }

    public void lesen() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:postgresql://"
                + dbHost + "/" + dbName, dbUser, dbPassword);
        Statement stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery("select * from graphs;");
        while (result.next()) {
            System.out.println(result.getInt(1) + " | " + result.getInt(2)
                    + " | " + result.getInt(3) + " | " + result.getInt(4)
                    + " | " + result.getInt(5) + " | " + result.getInt(6)
                    + " | " + result.getInt(7) + " | " + result.getInt(8));
        }
        stmt.close();

    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
