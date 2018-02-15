// =============================================================================
//
//   Evaluation.java
//
//   (c) 2007, Christian Bachmaier <chris@infosun.fim.uni-passau.de>
//
// =============================================================================
// $Id: $

package org.graffiti.plugins.algorithms.cyclicLeveling.test;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Christian Bachmaier
 * @version $revision$ $date${date}
 */
public class Evaluation {

    private static String sep = " ";

    private static void time7to500(Connection conn) throws Exception {
        FileOutputStream out = new FileOutputStream(
                "E:/auswertung/time7to500.csv");
        PrintStream prints = new PrintStream(out);

        PreparedStatement ps = conn
                .prepareStatement("SELECT h.numberOfNodes, AVG(h.time) AS avgTime, h.algorithm "
                        + "FROM (SELECT numberOfNodes, numberOfEdges, algorithm, numberOfLevels, time "
                        + "FROM `results` "
                        + "WHERE algorithm != 'optimal' "
                        + "AND numberOfNodes >= 7 "
                        + "AND numberOfLevels = ceil(sqrt(numberOfNodes) * sqrt(2)) AND width = numberOfLevels "
                        + "AND numberOfEdges = 5*numberOfNodes) h "
                        + "GROUP BY numberOfNodes, algorithm");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            prints.print(rs.getInt("numberOfNodes") + sep);

            for (int i = 0; i < 7; ++i) {
                prints.print(rs.getDouble("avgTime") + sep);

                if (i < 6) {
                    boolean check = rs.next();
                    assert check : "database inconsistency";
                }
            }

            prints.println();
        }

        out.close();
        ps.close();

        /* Compute optimal leveling for 7 to 14 nodes */
        FileOutputStream outStr = new FileOutputStream(
                "E:/auswertung/time7to500optimal.csv");
        PrintStream printStr = new PrintStream(outStr);

        PreparedStatement prepStatement = conn
                .prepareStatement("SELECT h.numberOfNodes, AVG(h.time) AS avgTime, h.algorithm "
                        + "FROM (SELECT numberOfNodes, numberOfEdges, algorithm, numberOfLevels, time "
                        + "FROM `results` "
                        + "WHERE algorithm = 'optimal' "
                        + "AND numberOfNodes >= 7 "
                        + "AND numberOfLevels = ceil(sqrt(numberOfNodes) * sqrt(2)) AND width = numberOfLevels "
                        + "AND numberOfEdges = 5*numberOfNodes) h "
                        + "GROUP BY numberOfNodes");
        ResultSet resultset = prepStatement.executeQuery();
        while (resultset.next()) {
            printStr.print(resultset.getInt("numberOfNodes") + sep);
            printStr.print(resultset.getDouble("avgTime") + sep);
            printStr.println();
        }

        outStr.close();
        prepStatement.close();
    }

    private static void time4to18(Connection conn) throws Exception {
        FileOutputStream out = new FileOutputStream(
                "E:/auswertung/time4to18.csv");
        PrintStream prints = new PrintStream(out);

        PreparedStatement ps = conn
                .prepareStatement("SELECT h.numberOfNodes, AVG(h.time) AS avgTime, h.algorithm "
                        + "FROM (SELECT numberOfNodes, numberOfEdges, algorithm, numberOfLevels, time "
                        + "FROM `results` "
                        + "WHERE numberOfNodes < 19 "
                        +
                        // "AND numberOfLevels = ceil(sqrt(numberOfNodes) * sqrt(2)) AND width = numberOfLevels "
                        // +
                        "AND numberOfEdges <= 2 * numberOfNodes) h "
                        + "GROUP BY numberOfNodes, algorithm");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            prints.print(rs.getInt("numberOfNodes") + sep);

            for (int i = 0; i < 8; ++i) {
                prints.print(rs.getDouble("avgTime") + sep);

                if (i < 7) {
                    boolean check = rs.next();
                    assert check : "database inconsistency";
                }
            }

            prints.println();
        }

        out.close();
        ps.close();
    }

    private static void sum4to18(Connection conn) throws Exception {
        FileOutputStream out = new FileOutputStream(
                "E:/auswertung/sum4to18.csv");
        PrintStream prints = new PrintStream(out);

        PreparedStatement ps = conn
                .prepareStatement("select h.numberOfNodes, avg(h.sumOfEdges) as sumOfEdges, h.algorithm "
                        + "from (SELECT numberOfNodes, algorithm, numberOfLevels, sumOfEdges "
                        + "FROM `results` "
                        + "where numberOfNodes < 19 "
                        + "AND numberOfLevels = ceil(sqrt(numberOfNodes) * sqrt(2)) AND width = numberOfLevels AND numberOfEdges = 2 * numberOfNodes) h "
                        + "group by numberOfNodes, algorithm");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            prints.print(rs.getInt("numberOfNodes") + sep);

            for (int i = 0; i < 8; ++i) {
                prints.print(rs.getDouble("sumOfEdges") + sep);

                if (i < 7) {
                    boolean check = rs.next();
                    assert check : "database inconsistency";
                }
            }

            prints.println();
        }

        out.close();
        ps.close();
    }

    private static void sum4to18best(Connection conn) throws Exception {
        FileOutputStream out = new FileOutputStream(
                "E:/auswertung/sum4to18best.csv");
        PrintStream prints = new PrintStream(out);

        PreparedStatement ps = conn
                .prepareStatement("SELECT h.numberOfNodes, avg( h.sumOfEdges ) as sumOfEdges, "
                        + "ALGORITHM FROM ( "
                        + "SELECT graphID, numberOfNodes, ALGORITHM , width, numberOfLevels, min( sumOfEdges ) AS sumOfEdges "
                        + "FROM results "
                        + "WHERE numberOfNodes < 19 "
                        + "AND numberOfLevels = ceil( sqrt( numberOfNodes ) * sqrt( 2 ) ) "
                        + "AND width = numberOfLevels "
                        + "AND numberOfEdges =2 * numberOfNodes "
                        + "GROUP BY graphID, numberOfLevels, width, ALGORITHM) h "
                        + "GROUP BY numberOfNodes, ALGORITHM");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            prints.print(rs.getInt("numberOfNodes") + sep);

            for (int i = 0; i < 8; ++i) {
                prints.print(rs.getDouble("sumOfEdges") + sep);

                if (i < 7) {
                    boolean check = rs.next();
                    assert check : "database inconsistency";
                }
            }

            prints.println();
        }

        out.close();
        ps.close();
    }

    private static void sum7to500(Connection conn) throws Exception {
        FileOutputStream out = new FileOutputStream(
                "E:/auswertung/sum7to500.csv");
        PrintStream prints = new PrintStream(out);

        PreparedStatement ps = conn
                .prepareStatement("select h.numberOfNodes, avg(h.sumOfEdges) as sumOfEdges, h.algorithm "
                        + "from (SELECT numberOfNodes, algorithm, numberOfLevels, sumOfEdges "
                        + "FROM `results` "
                        + "where numberOfNodes > 6 and algorithm != 'optimal' "
                        + "AND numberOfLevels = ceil(sqrt(numberOfNodes) * sqrt(2)) AND width = numberOfLevels AND numberOfEdges = 5 * numberOfNodes) h "
                        + "group by numberOfNodes, algorithm");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            prints.print(rs.getInt("numberOfNodes") + sep);

            for (int i = 0; i < 7; ++i) {
                prints.print(rs.getDouble("sumOfEdges") + sep);

                if (i < 6) {
                    boolean check = rs.next();
                    assert check : "database inconsistency";
                }
            }

            prints.println();
        }

        out.close();
        ps.close();
    }

    public static void main(String[] args) throws Exception {
        Connector connector = new Connector();
        connector.createConnection();
        Connection conn = connector.getCurrentConn();

        sum7to500(conn);
        sum4to18best(conn);
        time4to18(conn);
        time7to500(conn);
        sum4to18(conn);

        connector.closeConnection();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
