/*==============================================================================
*
*   Evaluation.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: Evaluation.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.eval;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;
import org.visnacom.sugiyama.SugiyamaDrawingStyle;
import org.visnacom.sugiyama.model.SugiCompoundGraph;


/**
 * contains the analysis of the data in the database.   at the moment, the
 * simple tests are contained in myschema.test_result. the complicated are in
 * myschema.test_results2 and myschema.expand_times plus belonging view
 * myschema.all_plus_single_expand.
 */
public class Evaluation {
    //~ Static fields/initializers =============================================

    //    public static CPG.View.Sugiyama.eval.Profiler.TestResult currentTest;
    //da steht das komplizierte zeug drin, jedes expand einzeln gemessen
    private static String tableNameComplicated =
        " myschema.all_plus_single_expand ";

    //die einfache tabelle, nicht jedes einzelne expand wird gemessen.
    private static String tableNameSimple = " myschema.test_results ";

    //~ Methods ================================================================

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     *
     * @throws SQLException
     */
    public static void main(String[] args)
        throws SQLException {
        Profiler.turnOnDatabase();

        executeQueryFlaechenvergleich(Profiler.conn,
            restrictionClassicSimpleNew());
        executeQuerySchnittvergleich(Profiler.conn,
            restrictionClassicSimpleNew());

        //geht nur mit complicated
        executeQueryZeitSingleExpandVergleich(Profiler.conn);
        //kann nicht mit complicated funktionieren
        executeQueryZeitVergleich(Profiler.conn, restrictionClassicSimpleNew());
        executeQueryDummyknoten(Profiler.conn, restrictionClassicSimpleNew());
        //
        //        /* alternative Tests */
        executeQueryFlaechenvergleichAlternative(Profiler.conn,
            tableNameSimple, timeRestrictionAlternativeSimpleFirstPair());
        //        
        executeQueryFlaechenvergleichAlternative(Profiler.conn,
            tableNameSimple, timeRestrictionAlternativeSimpleSecondPair());

        executeQuerySchnittvergleichAlternative(Profiler.conn);

        executeQueryZeitVergleichAlternative(Profiler.conn);
        executeQueryZeitVergleichAlternative2(Profiler.conn);

        //executeQueryDummyknotenAlternative(Profiler.conn,timeRestrictionAlternativeSimpleFirstPair());
        //executeQueryDummyknotenAlternative(Profiler.conn,timeRestrictionAlternativeSimpleSecondPair());
        executeQueryZeitSingleExpandVergleichAlternative(Profiler.conn);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private static LineAndShapeRenderer createMyRenderer() {
        LineAndShapeRenderer lasrenderer =
            new LineAndShapeRenderer(false, true);
        Shape[] shapes = createShapes();
        for(int i = 0; i < shapes.length; i++) {
            lasrenderer.setSeriesShape(i, shapes[i]);
            lasrenderer.setSeriesShapesFilled(i, false);
        }

        //all series black
        lasrenderer.setPaint(Color.black);
        return lasrenderer;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private static XYItemRenderer createMyXYRenderer() {
        XYLineAndShapeRenderer lasrenderer =
            new XYLineAndShapeRenderer(false, true);
        Shape[] shapes = createShapes();
        for(int i = 0; i < shapes.length; i++) {
            lasrenderer.setSeriesShape(i, shapes[i]);
            lasrenderer.setSeriesShapesFilled(i, false);
        }

        //all series black
        lasrenderer.setPaint(Color.black);
        return lasrenderer;
    }

    /**
     *
     *
     * @return DOCUMENT ME!
     */
    private static Shape[] createShapes() {
        Shape[] shapes = new Shape[4];
        GeneralPath gp = new GeneralPath();
        gp.moveTo(-3, -3);
        gp.lineTo(3, 3);
        gp.moveTo(3, -3);
        gp.lineTo(-3, 3);
        shapes[0] = gp;
        shapes[1] = new Ellipse2D.Double(-3.0, -3, 6.0, 6);
        gp = new GeneralPath();
        gp.moveTo(-3, 0);
        gp.lineTo(3, 0);
        gp.moveTo(0, -3);
        gp.lineTo(0, 3);
        shapes[2] = gp;
        shapes[3] = new Rectangle(-3, -3, 6, 6);
        return shapes;
    }

    /**
     * makes all lines black. sets background white.
     *
     * @param xAxis
     * @param yAxis
     * @param plot
     * @param chart
     */
    private static void decorateDiagramm(Axis xAxis, Axis yAxis, Plot plot,
        JFreeChart chart) {
        xAxis.setTickLabelPaint(Color.black);
        xAxis.setAxisLinePaint(Color.black);
        xAxis.setTickMarkPaint(Color.black);

        yAxis.setTickLabelPaint(Color.black);
        yAxis.setAxisLinePaint(Color.black);
        yAxis.setTickMarkPaint(Color.black);

        plot.setOutlinePaint(Color.black);
        if(plot instanceof CategoryPlot) {
            ((CategoryPlot) plot).setDomainGridlinePaint(Color.black);
            ((CategoryPlot) plot).setRangeGridlinePaint(Color.black);
            ((CategoryPlot) plot).clearRangeMarkers();
        } else if(plot instanceof XYPlot) {
            ((XYPlot) plot).setDomainGridlinePaint(Color.black);
            ((XYPlot) plot).setRangeGridlinePaint(Color.black);
            ((XYPlot) plot).clearRangeMarkers();
        }

        chart.setBackgroundPaint(Color.white);

        LegendTitle lt = chart.getLegend();
        lt.setItemFont(new Font("Serif", Font.ITALIC, 12));
        lt.setBorder(BlockBorder.NONE);
        //        lt.setWidth(200);
        lt.setPadding(new RectangleInsets(0, 30, 0, 30));
    }

    /**
     * y-Achse: laufzeit, x-Achse: anzahl knoten; und nach Dichte gruppiert.
     *
     * @param conn DOCUMENT ME!
     *
     * @throws SQLException DOCUMENT ME!
     */
    private static void executeQuery1(Connection conn)
        throws SQLException {
        Statement statement = conn.createStatement();
        ResultSet rs =
            statement.executeQuery(
                "select num_nodes, density_def, AVG(time_static_layout/1000) "
                + "from  " + tableNameSimple + "  where "
                + " group by num_nodes, density_def "
                + "order by num_nodes, density_def");

        XYSeriesCollection seriescoll = new XYSeriesCollection();
        Map seriesMap = new TreeMap();
        while(rs.next()) {
            Integer numNodes = new Integer(rs.getInt(1));
            Float densityDef = new Float(rs.getFloat(2));
            if(!seriesMap.containsKey(densityDef)) {
                seriesMap.put(densityDef,
                    new XYSeries("Dichte=" + densityDef + " "));
            }

            XYSeries series = (XYSeries) seriesMap.get(densityDef);
            series.add(numNodes, (Number) rs.getObject(3));
        }

        for(Iterator it = seriesMap.values().iterator(); it.hasNext();) {
            XYSeries s = (XYSeries) it.next();
            seriescoll.addSeries(s);
        }

        rs.close();

        JFreeChart chart =
            ChartFactory.createXYLineChart("title", "Originalknotenanzahl",
                "Rechenzeit in [s]", seriescoll, PlotOrientation.VERTICAL,
                true, true, false);
        chart.setBackgroundPaint(Color.white);
        DummyPicture.show(chart);
        //        DummyPicture.write(chart, "diagramm1", "pdf");
    }

    /**
     * x-Achse Gesamtknotenzahl, y-Achse Zeit
     *
     * @param conn DOCUMENT ME!
     *
     * @throws SQLException DOCUMENT ME!
     */
    private static void executeQuery2(Connection conn)
        throws SQLException {
        Statement statement = conn.createStatement();
        ResultSet rs =
            statement.executeQuery("select num_dummy_nodes+num_nodes, "
                + "AVG(time_static_layout), num_nodes, density_def " + "from "
                + " " + tableNameSimple + "  where " + "group by "
                + "num_nodes, num_dummy_nodes, density_def, mean_complexity_def, seed");

        //        JDBCXYDataset dataset =
        //            new JDBCXYDataset(conn,
        Map seriesMap = new TreeMap();

        while(rs.next()) {
            Integer totalnumNodes = new Integer(rs.getInt(1));
            Float timeStatic = new Float(rs.getFloat(2));
            IntFloatTuple grouping =
                new IntFloatTuple(rs.getInt(3), rs.getFloat(4));

            //            Integer originalnumNodes = new Integer(rs.getInt(3));
            //            Float densityDef = new Float(rs.getFloat(4));
            if(!seriesMap.containsKey(grouping)) {
                seriesMap.put(grouping, new XYSeries(grouping.toString()));
            }

            XYSeries series = (XYSeries) seriesMap.get(grouping);
            series.add(totalnumNodes, timeStatic);
        }

        XYSeriesCollection seriescoll = new XYSeriesCollection();
        for(Iterator it = seriesMap.values().iterator(); it.hasNext();) {
            XYSeries s = (XYSeries) it.next();
            seriescoll.addSeries(s);
        }

        rs.close();

        //        plot.setDataset(dataset);
        NumberAxis xAxis = new NumberAxis("Gesamtknotenzahl");

        //        xAxis.setAutoRange(false);
        //        xAxis.setRange(0.0, 25000.0);
        NumberAxis yAxis = new NumberAxis("time in millis");

        //        yAxis.setAutoRange(false);
        //        yAxis.setRange(0.0, 250000);
        XYPlot plot =
            new XYPlot(seriescoll, xAxis, yAxis,
                new StandardXYItemRenderer(StandardXYItemRenderer.SHAPES));
        JFreeChart chart = new JFreeChart(plot);
        DummyPicture.show(chart);
        //        DummyPicture.write(chart, "diagramm2", "pdf");
    }

    /**
     * xAchse Breite; y-Achse Höhe; vergleich von static mit expand
     *
     * @param conn DOCUMENT ME!
     *
     * @throws SQLException DOCUMENT ME!
     */
    private static void executeQuery3(Connection conn)
        throws SQLException {
        Statement statement = conn.createStatement();
        ResultSet rs =
            statement.executeQuery("select AVG(expanded_area_width), "
                + "AVG(expanded_area_height), AVG(area_width), AVG(area_height) "
                + "from  " + tableNameSimple + "  where "
                + "group by num_nodes, density_def, mean_degree_def, seed");

        XYSeriesCollection seriescoll = new XYSeriesCollection();

        while(rs.next()) {
            AnnotatedInteger expwidth =
                new AnnotatedInteger(rs.getInt(1), "expand");
            AnnotatedInteger expheight =
                new AnnotatedInteger(rs.getInt(2), "expand");
            AnnotatedInteger staticwidth =
                new AnnotatedInteger(rs.getInt(3), "static");
            AnnotatedInteger staticheight =
                new AnnotatedInteger(rs.getInt(4), "static");
            XYSeries s = new XYSeries("");
            s.add(expwidth, expheight);
            s.add(staticwidth, staticheight);
            seriescoll.addSeries(s);
        }

        rs.close();
        statement.close();

        NumberAxis xAxis = new LogarithmicAxis("Breite");

        //        NumberAxis xAxis = new NumberAxis("Breite");
        //        xAxis.setAutoRange(false);
        //        xAxis.setRange(0.0, 20000.0);
        NumberAxis yAxis = new NumberAxis("Höhe");

        //        NumberAxis yAxis = new LogarithmicAxis("Höhe");
        //        yAxis.setAutoRange(false);
        //        yAxis.setRange(0.0, 2500);
        XYPlot plot =
            new XYPlot(seriescoll, xAxis, yAxis, new MyShapeRenderer(seriescoll));

        //plot.setDrawingSupplier(new SameColorSupplier(Color.red));
        JFreeChart chart = new JFreeChart("title", new Font(null), plot, false);
        chart.setBackgroundPaint(Color.white);
        DummyPicture.show(chart);
        //        DummyPicture.write(chart, "diagramm3","pdf");
    }

    /**
     * DOCUMENT ME!
     *
     * @param conn DOCUMENT ME!
     * @param restriction DOCUMENT ME!
     *
     * @throws SQLException DOCUMENT ME!
     */
    private static void executeQueryDummyknoten(Connection conn,
        String restriction) throws SQLException {
        Statement statement = conn.createStatement();
        String[] seriesNames = {"series1"};
        ResultSet rs;

        /* knotenanzahl pro Kante abhängig von Verzweigungsgrad */
        rs = statement.executeQuery("select  "
                + " AVG(cast(num_dummy_nodes as real) / num_edges_act), "
                + "mean_degree_def " + "from " + tableNameSimple + "   where "
                + restriction + " group by " + " mean_degree_def "
                + " order by " + " mean_degree_def ");
        showDiagrammAbsoluteGroupedBy(new NumberTickUnit(2), rs,
            "Verzweigungsgrad", "Dummyknoten", "", seriesNames, false,
            "diagrammDummyknotenProKante", false);
    }

    /**
     *
     *
     * @param conn DOCUMENT ME!
     * @param restriction DOCUMENT ME!
     *
     * @throws SQLException DOCUMENT ME!
     */
    private static void executeQueryDummyknotenAlternative(Connection conn,
        String restriction) throws SQLException {
        Statement statement = conn.createStatement();
        String[] seriesNames = {"series1"};
        ResultSet rs;
        rs = statement.executeQuery("select  "
                + " AVG(cast(num_dummy_nodes as real) / num_edges_act), "
                + "num_nodes " + " from " + tableNameSimple + "   where "
                + restriction + " group by " + " num_nodes "
                + " order by  num_nodes ");
        showDiagrammAbsoluteGroupedBy(new NumberTickUnit(0.1), rs,
            "Knotenanzahl", "Dummyknoten pro Kante", "", seriesNames, false,
            "diagrammDummyknotenProKante", false);
    }

    /**
     * Flächenvergleich x-Achse Testläufe. y-Achse Fläche.
     *
     * @param conn DOCUMENT ME!
     * @param restriction DOCUMENT ME!
     *
     * @throws SQLException DOCUMENT ME!
     */
    private static void executeQueryFlaechenvergleich(Connection conn,
        String restriction) throws SQLException {
        Statement statement = conn.createStatement();
        String selectPart =
            "select AVG(cast(expanded_area_width*expanded_area_height as real)/"
            + "cast(area_height*area_width as real)),";
        String[] seriesNames = {"series1"};
        double min = 0.5;
        double max = 1.2;
        NumberTickUnit tickunit =
            new NumberTickUnit(0.1, new DecimalFormat("0.0"));

        ResultSet rs =
            statement.executeQuery(selectPart + " num_nodes " + " from  "
                + tableNameSimple + "  where " + restriction
                + " group by num_nodes " + " order by num_nodes ");
        showDiagrammRelativGroupedBy(tickunit, min, max, rs, seriesNames,
            "Knotenanzahl", "diagrammRelFlaecheKnotenanzahl", false);

        rs.close();

        rs = statement.executeQuery(selectPart + "density_def " + " from  "
                + tableNameSimple + "  where " + restriction
                + " group by density_def " + " order by density_def ");

        showDiagrammRelativGroupedBy(tickunit, min, max, rs, seriesNames,
            "Dichte", "diagrammRelFlaecheDichte", false);
        rs.close();

        rs = statement.executeQuery(selectPart + " mean_degree_def "
                + " from  " + tableNameSimple + "  where " + restriction
                + " group by mean_degree_def " + " order by mean_degree_def ");
        showDiagrammRelativGroupedBy(tickunit, min, max, rs, seriesNames,
            "Verzweigungsgrad", "diagrammRelFlaecheVerzweigungsgrad", false);
        rs.close();

        /* breite und hoehe einzeln */
        rs = statement.executeQuery("select "
                + " AVG(cast(expanded_area_width as real) / "
                + " cast(area_width as real)) as rel_width "
                + ", AVG(cast(expanded_area_height as real) / "
                + " cast(area_height as real)) as rel_height " + " ,num_nodes "
                + "from " + tableNameSimple + "  where " + restriction
                + " group by num_nodes " + " order by num_nodes ");

        String[] seriesNames2 = {"Breite", "Höhe"};
        showDiagrammRelativGroupedBy(tickunit, min, max, rs, seriesNames2,
            "Knotenanzahl", "diagrammFlaecheBreiteHoehe", false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param conn DOCUMENT ME!
     * @param tableName DOCUMENT ME!
     * @param restriction DOCUMENT ME!
     *
     * @throws SQLException DOCUMENT ME!
     */
    private static void executeQueryFlaechenvergleichAlternative(
        Connection conn, String tableName, String restriction)
        throws SQLException {
        Statement statement = conn.createStatement();
        String selectPart =
            "select " + " AVG(cast(expanded_area_width as real) / "
            + " cast(area_width as real)) as rel_width "
            + ", AVG(cast(expanded_area_height as real) / "
            + " cast(area_height as real)) as rel_height " + " ,num_nodes "
            + "from " + tableName + " where " + restriction
            + " group by num_nodes " + " order by num_nodes ";
        ResultSet rs = statement.executeQuery(selectPart);
        String[] seriesNames = {"Breite", "Höhe"};
        showDiagrammRelativGroupedBy(DummyPicture.WIDER_DIAGRAMM_SIZE,
            new NumberTickUnit(0.1, new DecimalFormat("0.0")), 0.7, 1.2, rs,
            seriesNames, "Knotenanzahl", "diagrammFlaecheAlternative", false);
    }

    /**
     * verhältnis der schnitte expand/static
     *
     * @param conn DOCUMENT ME!
     * @param restriction DOCUMENT ME!
     *
     * @throws SQLException DOCUMENT ME!
     */
    private static void executeQuerySchnittvergleich(Connection conn,
        String restriction) throws SQLException {
        Statement statement = conn.createStatement();
        ResultSet rs;
        String selectPart =
            "select AVG( cast(expand_crossings as real)/ cast(static_crossings as real)),";
        String[] seriesNames = {"series1"};
        double min = 0.5;
        double max = 1.1;
        NumberTickUnit tickunit =
            new NumberTickUnit(0.1, new DecimalFormat("0.0"));

        rs = statement.executeQuery(selectPart + " num_nodes " + "from "
                + tableNameSimple + "  where " + restriction
                + "AND static_crossings > 0 "
                + " group by num_nodes order by num_nodes ");
        showDiagrammRelativGroupedBy(tickunit, min, max, rs, seriesNames,
            "Knotenanzahl", "diagrammRelSchnitteKnotenanzahl", false);
        rs.close();
        rs = statement.executeQuery(selectPart + " density_def " + "from "
                + tableNameSimple + "  where" + restriction
                + "AND static_crossings > 0"
                + " group by  density_def order by  density_def");
        showDiagrammRelativGroupedBy(tickunit, min, max, rs, seriesNames,
            "Dichte", "diagrammRelSchnitteDichte", false);
        rs.close();
        rs = statement.executeQuery(selectPart + " mean_degree_def " + "from "
                + tableNameSimple + "  where" + restriction
                + "AND static_crossings > 0"
                + " group by mean_degree_def order by mean_degree_def");
        showDiagrammRelativGroupedBy(tickunit, min, max, rs, seriesNames,
            "Verzweigungsgrad", "diagrammRelSchnitteVerzweigungsgrad", false);
        rs.close();
    }

    /**
     * DOCUMENT ME!
     *
     * @param conn DOCUMENT ME!
     *
     * @throws SQLException DOCUMENT ME!
     */
    private static void executeQuerySchnittvergleichAlternative(Connection conn)
        throws SQLException {
        Statement statement = conn.createStatement();
        ResultSet rs;

        rs = statement.executeQuery(
                "select d.pair_one, e.pair_two, num_nodes from ("
                + " select AVG( cast(expand_crossings as real)/ cast(static_crossings as real)) as pair_one, "
                + "num_nodes  from " + tableNameSimple + " where  "
                + timeRestrictionAlternativeSimpleFirstPair()
                + " AND static_crossings > 0 " + " group by num_nodes) as d "
                + "inner join (select AVG( cast(expand_crossings as real)/ cast(static_crossings as real)) as pair_two, "
                + " num_nodes from " + tableNameSimple + " where "
                + timeRestrictionAlternativeSimpleSecondPair()
                + " AND static_crossings > 0 group by num_nodes "
                + " ) as e using (num_nodes) " + "order by num_nodes");

        String[] seriesNames = {"(m,g) = (2.2,5)", "(m,g)=(2.3,15)"};

        //            "select AVG( cast(expand_crossings as real)/ "
        //            + "cast(static_crossings as real)),";
        //        String[] seriesNames = {"FirstPair"};
        //                selectPart + " num_nodes " + "from " + tableName
        //                + " where" + restriction
        //                + " AND static_crossings > 0"
        //                + " group by num_nodes order by num_nodes");
        showDiagrammRelativGroupedBy(DummyPicture.WIDER_DIAGRAMM_SIZE,
            new NumberTickUnit(0.1, new DecimalFormat("0.0")), 0.5, 1.2, rs,
            seriesNames, "Knotenanzahl", "diagrammSchnitteAlternative", true);
        rs.close();
    }

    /**
     * DOCUMENT ME!
     *
     * @param conn DOCUMENT ME!
     *
     * @throws SQLException DOCUMENT ME!
     */
    private static void executeQueryZeitSingleExpandVergleich(Connection conn)
        throws SQLException {
        /* relative zeit einzelnes expand */
        Statement statement = conn.createStatement();

        ResultSet rs;
        String[] seriesNames = {"series"};

        rs = statement.executeQuery("select AVG(relTime_single_expand),"
                + "num_nodes " + " from  " + tableNameComplicated + "  where "
                + timeRestrictionClassicComplicated() + " group by num_nodes "
                + " order by num_nodes");

        showDiagrammRelativGroupedBy(0, 1, rs, seriesNames, "Knotenanzahl",
            "diagrammRelZeitSingleExpandKnotenanzahl", false);
        rs.close();

        rs = statement.executeQuery("select AVG(relTime_single_expand),"
                + " density_def" + " from  " + tableNameComplicated
                + "  where " + timeRestrictionClassicComplicated()
                + " group by density_def " + " order by density_def");
        showDiagrammRelativGroupedBy(0, 1, rs, seriesNames, "Dichte",
            "diagrammRelZeitSingleExpandDichte", false);
        rs.close();

        //not used
        rs = statement.executeQuery("select AVG(relTime_single_expand),"
                + "mean_degree_def " + " from  " + tableNameComplicated
                + "  where " + timeRestrictionClassicComplicated()
                + " group by mean_degree_def " + " order by mean_degree_def");
        showDiagrammRelativGroupedBy(0, 1, rs, seriesNames, "Verzweigungsgrad",
            "diagrammRelZeitSingleExpandVerzweigungsgrad", false);
        rs.close();
    }

    /**
     * DOCUMENT ME!
     *
     * @param conn DOCUMENT ME!
     *
     * @throws SQLException DOCUMENT ME!
     */
    private static void executeQueryZeitSingleExpandVergleichAlternative(
        Connection conn) throws SQLException {
        Statement statement = conn.createStatement();

        ResultSet rs;
        rs = statement.executeQuery(
                " select d.pair_one, e.pair_two, num_nodes from ("
                + " select AVG(relTime_single_expand) as pair_one, "
                + "num_nodes  from " + tableNameComplicated + " where  "
                + timeRestrictionAlternativeComplicatedFirstPair()
                + " group by num_nodes) as d "
                + "inner join (select AVG(relTime_single_expand) as pair_two, "
                + " num_nodes from " + tableNameComplicated + " where "
                + timeRestrictionAlternativeComplicatedSecondPair()
                + " group by num_nodes " + " ) as e using (num_nodes) "
                + "order by num_nodes");

        String[] seriesNames = {"(m,g) = (2.2,5)", "(m,g)=(2.3,15)"};

        //                "select AVG(relTime_single_expand), num_nodes from "
        //                + tableNameComplicated + " where "
        //                + restriction
        //                + "group by num_nodes  order by num_nodes");
        //        String[] seriesNames = {"series"};
        showDiagrammRelativGroupedBy(DummyPicture.WIDER_DIAGRAMM_SIZE,
            new NumberTickUnit(0.1), 0, 0.6, rs, seriesNames, "Knotenanzahl",
            "diagrammZeitSingleExpandAlternative", false);
        rs.close();
    }

    /**
     * zeit vergleich
     *
     * @param conn DOCUMENT ME!
     * @param restriction DOCUMENT ME!
     *
     * @throws SQLException
     */
    private static void executeQueryZeitVergleich(Connection conn,
        String restriction) throws SQLException {
        Statement statement = conn.createStatement();
        ResultSet rs;

        //      unused  rs = statement.executeQuery(
        //                "select AVG(time_static_layout)/1000, AVG(time_expand)/1000 , "
        //                + "num_nodes " + "from " + tableNameSimple + "  where "
        //                + timeRestrictionClassicSimple() + " group by num_nodes "
        //                + " order by num_nodes");
        //
        //        String[] seriesNames = {"statischer Algorihtmus", "Updateschema"};
        //        showDiagrammAbsoluteGroupedBy(
        //            rs, "Knotenanzahl", "Zeit [s]", title, seriesNames, false,
        //            "diagrammZeitKnotenanzahl",new NumberTickUnit(10), false);
        //        rs.close();
        rs = statement.executeQuery(
                "select num_nodes, AVG(time_static_layout)/1000, 'Statisch' as category "
                + "from " + tableNameSimple + "  where " + restriction
                + " group by num_nodes " + " union " + "select num_nodes, "
                + "AVG(time_expand)/1000 , 'Update' as category " + "from "
                + tableNameSimple + "  where " + restriction
                + " group by num_nodes ");
        showDiagrammXYCategorized(null, new NumberTickUnit(2), rs,
            "Knotenanzahl", "Zeit in [s]", "diagrammZeitKnotenanzahl", false);
        rs.close();

        /* Relativwerte */
        String[] series2Names = {"series2"};
        double min = 0;
        double max = 4;
        NumberTickUnit unit = new NumberTickUnit(1.0, new DecimalFormat("0.0"));
        rs = statement.executeQuery(
                "select AVG(CAST(time_expand as real)/CAST(time_static_layout as real)), "
                + "num_nodes from  " + tableNameSimple + "  where "
                + restriction + " group by num_nodes " + " order by num_nodes");
        showDiagrammRelativGroupedBy(unit, min, max, rs, series2Names,
            "Knotenanzahl", "diagrammRelZeitKnotenanzahl", false);
        rs.close();
        rs = statement.executeQuery(
                "select AVG(CAST(time_expand as real)/CAST(time_static_layout as real)), "
                + "density_def " //, mean_degree_def "
                + "from  " + tableNameSimple + "  where " + restriction
                + " group by density_def " + " order by density_def");
        showDiagrammRelativGroupedBy(unit, min, max, rs, series2Names,
            "Dichte", "diagrammRelZeitDichte", false);

        rs.close();
        rs = statement.executeQuery(
                "select AVG(CAST(time_expand as real)/CAST(time_static_layout as real)), "
                + "mean_degree_def " + "from  " + tableNameSimple + "  where "
                + restriction + " group by mean_degree_def "
                + " order by mean_degree_def");
        showDiagrammRelativGroupedBy(unit, min, max, rs, series2Names,
            "Verzweigungsgrad", "diagrammRelZeitVerzweigungsgrad", false);
    }

    /**
     * relative time comparison
     *
     * @param conn DOCUMENT ME!
     *
     * @throws SQLException DOCUMENT ME!
     */
    private static void executeQueryZeitVergleichAlternative(Connection conn)
        throws SQLException {
        Statement statement = conn.createStatement();

        ResultSet rs;
        String[] seriesNames = {"series"};

       
        /*relative time */
        rs = statement.executeQuery(
                "select d.pair_one, e.pair_two, num_nodes from ("
                + " select AVG(CAST(time_expand as real)/CAST(time_static_layout as real)) as pair_one, "
                + "num_nodes  from " + tableNameSimple + " where  "
                + timeRestrictionAlternativeSimpleFirstPair()
                + " group by num_nodes) as d "
                + "inner join (select AVG(CAST(time_expand as real)/CAST(time_static_layout as real)) as pair_two, "
                + " num_nodes from " + tableNameSimple + " where "
                + timeRestrictionAlternativeSimpleSecondPair()
                + " group by num_nodes " + " ) as e using (num_nodes) "
                + "order by num_nodes");

        String[] seriesNames2 = {"(m,g) = (2.2,5)", "(m,g)=(2.3,15)"};

        showDiagrammRelativGroupedBy(DummyPicture.WIDER_DIAGRAMM_SIZE,
            new NumberTickUnit(2), 0, 10, rs, seriesNames2, "Knotenanzahl",
            "diagrammRelZeitAlternative", false);
        rs.close();
    }

    /**
     * absolute time comparison with x-Axis
     *
     * @param conn DOCUMENT ME!
     *
     * @throws SQLException DOCUMENT ME!
     */
    private static void executeQueryZeitVergleichAlternative2(Connection conn)
        throws SQLException {
        Statement statement = conn.createStatement();

        ResultSet rs;
        rs = statement.executeQuery(
                "select num_nodes, AVG(time_static_layout)/1000, 'statischer Alg., (2.2,5)' as category from "
                + tableNameSimple + "where "
                + timeRestrictionAlternativeSimpleFirstPair()
                + "group by num_nodes union "
                + "select num_nodes, AVG(time_expand)/1000, 'Updateschema, (2.2,5)' "
                + "as category from " + tableNameSimple + "where "
                + timeRestrictionAlternativeSimpleFirstPair()
                + "group by num_nodes union "
                + "select num_nodes, AVG(time_static_layout)/1000, 'statischer Alg., (2.3,15)' as category from "
                + tableNameSimple + "where "
                + timeRestrictionAlternativeSimpleSecondPair()
                + "group by num_nodes union "
                + "select num_nodes, AVG(time_expand)/1000, 'Updateschema, (2.3,15)' "
                + "as category from " + tableNameSimple + "where "
                + timeRestrictionAlternativeSimpleSecondPair()
                + " group by num_nodes");

        Rectangle size = new Rectangle(DummyPicture.WIDER_DIAGRAMM_SIZE);
        size.height += 100;
        showDiagrammXYCategorized(new NumberTickUnit(200), null, size, rs,
            "Knotenzahl", "Zeit [s]", "diagrammAbsoluteZeitAlternative", true);
        rs.close();
    }

    /**
     * DOCUMENT ME!
     *
     * @return the intervall for simple classic tests with suppressed metric
     *         layout
     */
    private static String restrictionClassicSimpleNew() {
        return " time_stamp >= timestamp '2005-09-11 13:43:00' AND "
        + " time_stamp <= timestamp '2005-09-11 18:49:00' ";
    }

    /**
     * DOCUMENT ME!
     *
     * @return the intervall for simple classic tests, old version
     */
    private static String restrictionClassicSimpleOld() {
        return " density_def < 0.35 AND ((time_stamp >= timestamp '2005-08-17 14:38:00' AND"
        + " time_stamp <= timestamp '2005-08-18 13:50:00') OR  "
        + "(time_stamp >= timestamp '2005-09-09 17:11:00' AND"
        + " time_stamp <= timestamp '2005-09-09 17:15:00')) ";
    }

    /**
     * example: seriesNames = {"expand", "static"} resultset rs = [[2.0, 3.0,
     * 20Knoten],[4.0,5.5, 50Knoten],...]
     *
     * @param unit tick unit for yAxis, may be null
     * @param rs
     * @param xAxisLabel String decoration
     * @param yAxisLabel String decoration
     * @param title
     * @param seriesNames
     * @param logarhithmic if true, yAxis is logarhithmic
     * @param filename
     * @param legend
     *
     * @throws SQLException
     */
    private static void showDiagrammAbsoluteGroupedBy(NumberTickUnit unit,
        ResultSet rs, String xAxisLabel, String yAxisLabel, String title,
        String[] seriesNames, boolean logarhithmic, String filename,
        boolean legend) throws SQLException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        while(rs.next()) {
            for(int i = 1; i <= seriesNames.length; i++) {
                Number value = (Number) rs.getObject(i);
                String category =
                    rs.getObject(seriesNames.length + 1).toString();
                dataset.addValue(value, seriesNames[i - 1], category);
            }
        }

        CategoryAxis xAxis = new CategoryAxis(xAxisLabel);
        NumberAxis yAxis;
        if(logarhithmic) {
            LogarithmicAxis yA = new LogarithmicAxis(yAxisLabel);
            yA.setLog10TickLabelsFlag(false);
            yA.setExpTickLabelsFlag(false);
            yAxis = yA;
        } else {
            yAxis = new NumberAxis(yAxisLabel);
        }

        if(unit != null) {
            yAxis.setTickUnit(unit, true, true);
        }

        CategoryPlot plot =
            new CategoryPlot(dataset, xAxis, yAxis, createMyRenderer());
        JFreeChart chart = new JFreeChart(title, new Font(null), plot, legend);

        decorateDiagramm(xAxis, yAxis, plot, chart);

        //xAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(1.22));
        //yAxis.setNumberFormatOverride(NumberFormat.getIntegerInstance());
        DummyPicture.show(chart);
        DummyPicture.write(chart, filename, "pdf",
            DummyPicture.DEFAULT_DIAGRAMM_SIZE);
    }

    /**
     * DOCUMENT ME!
     *
     * @param rs eine reihe von werten, als letztes die category
     * @param xAxisLabel
     * @param yAxisLabel
     * @param title
     * @param seriesNames bezeichnungen für die wertereihen
     * @param logarhithmic
     * @param filename
     * @param legend create a legend
     *
     * @throws SQLException
     */
    private static void showDiagrammAbsoluteGroupedBy(ResultSet rs,
        String xAxisLabel, String yAxisLabel, String title,
        String[] seriesNames, boolean logarhithmic, String filename,
        boolean legend) throws SQLException {
        showDiagrammAbsoluteGroupedBy(null, rs, xAxisLabel, yAxisLabel, title,
            seriesNames, logarhithmic, filename, legend);
    }

    /**
     * DOCUMENT ME!
     *
     * @param size DOCUMENT ME!
     * @param min DOCUMENT ME!
     * @param max DOCUMENT ME!
     * @param rs DOCUMENT ME!
     * @param seriesNames DOCUMENT ME!
     * @param xAxis DOCUMENT ME!
     * @param filename DOCUMENT ME!
     * @param legend DOCUMENT ME!
     *
     * @throws SQLException DOCUMENT ME!
     */
    private static void showDiagrammRelativGroupedBy(Rectangle size,
        double min, double max, ResultSet rs, String[] seriesNames,
        String xAxis, String filename, boolean legend)
        throws SQLException {
        showDiagrammRelativGroupedBy(size, null, min, max, rs, seriesNames,
            xAxis, filename, legend);
    }

    /**
     * DOCUMENT ME!
     *
     * @param min DOCUMENT ME!
     * @param max DOCUMENT ME!
     * @param rs DOCUMENT ME!
     * @param seriesNames DOCUMENT ME!
     * @param xAxisLabel DOCUMENT ME!
     * @param filename DOCUMENT ME!
     * @param legend DOCUMENT ME!
     *
     * @throws SQLException DOCUMENT ME!
     */
    private static void showDiagrammRelativGroupedBy(double min, double max,
        ResultSet rs, String[] seriesNames, String xAxisLabel, String filename,
        boolean legend) throws SQLException {
        showDiagrammRelativGroupedBy(DummyPicture.DEFAULT_DIAGRAMM_SIZE, min,
            max, rs, seriesNames, xAxisLabel, filename, legend);
    }

    /**
     * DOCUMENT ME!
     *
     * @param tickunit DOCUMENT ME!
     * @param min DOCUMENT ME!
     * @param max DOCUMENT ME!
     * @param rs DOCUMENT ME!
     * @param seriesNames DOCUMENT ME!
     * @param xAxisLabel DOCUMENT ME!
     * @param filename DOCUMENT ME!
     * @param legend DOCUMENT ME!
     *
     * @throws SQLException DOCUMENT ME!
     */
    private static void showDiagrammRelativGroupedBy(NumberTickUnit tickunit,
        double min, double max, ResultSet rs, String[] seriesNames,
        String xAxisLabel, String filename, boolean legend)
        throws SQLException {
        showDiagrammRelativGroupedBy(DummyPicture.DEFAULT_DIAGRAMM_SIZE,
            tickunit, min, max, rs, seriesNames, xAxisLabel, filename, legend);
    }

    /**
     * DOCUMENT ME!
     *
     * @param size DOCUMENT ME!
     * @param tickunit DOCUMENT ME!
     * @param min DOCUMENT ME!
     * @param max DOCUMENT ME!
     * @param rs all except the last column have to contain numbers, the last a
     *        category - usually a grouped parameter. the category is used at
     *        x-Axis, the numbers are the y-Values. each column is a series.
     * @param seriesNames DOCUMENT ME!
     * @param xAxisLabel DOCUMENT ME!
     * @param filename DOCUMENT ME!
     * @param legend
     *
     * @throws SQLException DOCUMENT ME!
     */
    private static void showDiagrammRelativGroupedBy(Rectangle size,
        NumberTickUnit tickunit, double min, double max, ResultSet rs,
        String[] seriesNames, String xAxisLabel, String filename, boolean legend)
        throws SQLException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        while(rs.next()) {
            for(int i = 1; i <= seriesNames.length; i++) {
                Number value = (Number) rs.getObject(i);
                String category =
                    rs.getObject(seriesNames.length + 1).toString();
                dataset.addValue(value, seriesNames[i - 1], category);
            }
        }

        CategoryAxis xAxis = new CategoryAxis(xAxisLabel);
        NumberAxis yAxis = new NumberAxis("Relative Werte");
        if(tickunit != null) {
            yAxis.setTickUnit(tickunit, true, true);
        }

        CategoryPlot plot =
            new CategoryPlot(dataset, xAxis, yAxis, createMyRenderer());

        JFreeChart chart = new JFreeChart("", new Font(null), plot, legend);
        decorateDiagramm(xAxis, yAxis, plot, chart);

        //        chart.getCategoryPlot().getDomainAxis().setTickMarksVisible(true);
        chart.getCategoryPlot().getRangeAxis().setRange(min, max);
        //               chart.getCategoryPlot().getDomainAxis().setTickLabelsVisible(false);
        DummyPicture.show(chart);
        DummyPicture.write(chart, filename, "pdf", size);
    }

    /**
     * DOCUMENT ME!
     *
     * @param xAxisUnit DOCUMENT ME!
     * @param yAxisUnit DOCUMENT ME!
     * @param rs DOCUMENT ME!
     * @param xAxisLabel DOCUMENT ME!
     * @param yAxisLabel DOCUMENT ME!
     * @param filename DOCUMENT ME!
     * @param legend DOCUMENT ME!
     *
     * @throws SQLException DOCUMENT ME!
     */
    private static void showDiagrammXYCategorized(NumberTickUnit xAxisUnit,
        NumberTickUnit yAxisUnit, ResultSet rs, String xAxisLabel,
        String yAxisLabel, String filename, boolean legend)
        throws SQLException {
        showDiagrammXYCategorized(xAxisUnit, yAxisUnit,
            DummyPicture.DEFAULT_DIAGRAMM_SIZE, rs, xAxisLabel, yAxisLabel,
            filename, legend);
    }

    /**
     * DOCUMENT ME!
     *
     * @param rs DOCUMENT ME!
     * @param xAxisLabel DOCUMENT ME!
     * @param yAxisLabel DOCUMENT ME!
     * @param filename DOCUMENT ME!
     * @param legend DOCUMENT ME!
     *
     * @throws SQLException DOCUMENT ME!
     */
    private static void showDiagrammXYCategorized(ResultSet rs,
        String xAxisLabel, String yAxisLabel, String filename, boolean legend)
        throws SQLException {
        showDiagrammXYCategorized(DummyPicture.DEFAULT_DIAGRAMM_SIZE, rs,
            xAxisLabel, yAxisLabel, filename, legend);
    }

    /**
     * DOCUMENT ME!
     *
     * @param size DOCUMENT ME!
     * @param rs DOCUMENT ME!
     * @param xAxisLabel DOCUMENT ME!
     * @param yAxisLabel DOCUMENT ME!
     * @param filename DOCUMENT ME!
     * @param legend DOCUMENT ME!
     *
     * @throws SQLException DOCUMENT ME!
     */
    private static void showDiagrammXYCategorized(Rectangle size, ResultSet rs,
        String xAxisLabel, String yAxisLabel, String filename, boolean legend)
        throws SQLException {
        showDiagrammXYCategorized(null, null, size, rs, xAxisLabel, yAxisLabel,
            filename, legend);
    }

    /**
     * first column ist interpreted as x value, second column as y Value, 3rd
     * column as string, that indicates the belonging series of the point.
     *
     * @param xAxisunit DOCUMENT ME!
     * @param yAxisunit DOCUMENT ME!
     * @param size DOCUMENT ME!
     * @param rs
     * @param xAxisLabel
     * @param yAxisLabel
     * @param filename
     * @param legend
     *
     * @throws SQLException
     */
    private static void showDiagrammXYCategorized(NumberTickUnit xAxisunit,
        NumberTickUnit yAxisunit, Rectangle size, ResultSet rs,
        String xAxisLabel, String yAxisLabel, String filename, boolean legend)
        throws SQLException {
        XYSeriesCollection seriescoll = new XYSeriesCollection();
        Map seriesMap = new TreeMap();
        while(rs.next()) {
            Number xValue = (Number) rs.getObject(1);
            Number yValue = (Number) rs.getObject(2);
            String category = rs.getObject(3).toString();

            if(!seriesMap.containsKey(category)) {
                seriesMap.put(category, new XYSeries(category));
            }

            XYSeries series = (XYSeries) seriesMap.get(category);
            series.add(xValue, yValue);
        }

        for(Iterator it = seriesMap.values().iterator(); it.hasNext();) {
            XYSeries s = (XYSeries) it.next();
            seriescoll.addSeries(s);
        }

        JFreeChart chart =
            ChartFactory.createXYLineChart("", xAxisLabel, yAxisLabel,
                seriescoll, PlotOrientation.VERTICAL, legend, false, false);

        XYPlot plot = chart.getXYPlot();
        plot.setRenderer(createMyXYRenderer());
        if(yAxisunit != null) {
            ((NumberAxis) plot.getRangeAxis()).setTickUnit(yAxisunit, true, true);
        }

        if(xAxisunit != null) {
            ((NumberAxis) plot.getDomainAxis()).setTickUnit(xAxisunit, true,
                true);
        }

        decorateDiagramm(plot.getDomainAxis(), plot.getRangeAxis(), plot, chart);

        DummyPicture.show(chart);
        DummyPicture.write(chart, filename, "pdf", size);
    }

    /**
     *
     *
     * @return DOCUMENT ME!
     */
    private static String timeRestrictionAlternativeComplicatedFirstPair() {
        return " time_stamp >= timestamp '2005-08-23 14:22:00' AND "
        + " time_stamp <= timestamp '2005-08-26 08:02:00' AND num_nodes > 20";
    }

    /**
     * for the pair (2.3,15)
     *
     * @return DOCUMENT ME!
     */
    private static String timeRestrictionAlternativeComplicatedSecondPair() {
        //die ersten 80: 2005-08-23 14:22:15 bis 2005-08-23 15:35:33
        //die restlichen 490 * 8 : 2005-08-23 16:07:54 bis 2005-08-26 08:01:55
        return " time_stamp >= timestamp '2005-09-19 16:15:00' AND "
        + " time_stamp <= timestamp '2005-09-20 14:35:00' AND num_nodes > 20 ";
    }

    /**
     *
     *
     * @return DOCUMENT ME!
     */
    private static String timeRestrictionAlternativeSimpleFirstPair() {
        return " time_stamp >= timestamp '2005-08-21 19:19:00' AND "
        + " time_stamp <= timestamp '2005-08-22 04:56:00' AND num_nodes > 20";
    }

    /**
     * for the pair (2.3,15)
     *
     * @return DOCUMENT ME!
     */
    private static String timeRestrictionAlternativeSimpleSecondPair() {
        return " time_stamp >= timestamp '2005-09-20 16:15:00' AND "
        + " time_stamp <= timestamp '2005-09-20 17:52:00' AND num_nodes > 20";
    }

    /**
     * this is the time intervall for the test, that was executed with the
     * usual creation algorithm. it was also done with measurement of each
     * single expand operation. the belonging tablenames are
     * myschema.test_results2 and myschema.expand_times. One can also refer to
     * the view myschema.all_plus_single_expand, which contains already the
     * join of the other two tables
     *
     * @return DOCUMENT ME!
     */
    private static String timeRestrictionClassicComplicated() {
        //excluding the framework
        return "time_stamp >= timestamp '2005-09-12 23:02:00' AND "
        + " time_stamp <= timestamp '2005-09-15 20:26:00' ";

        //old one
        //        return " time_stamp >= timestamp '2005-08-26 21:45:00' AND " +
        //        		" time_stamp <= timestamp '2005-09-10 11:03:00' AND " +
        //        		"density_def < 0.35 ";
        //klassischer Test: 6 von 10 gedachten testgraphen
        //        + "time_stamp <= timestamp '2005-09-06 05:02:00' ";
        //10 graphen mit dichte 0.01
        // time_stamp >=  timestamp '2005-09-09 15:32:00'  AND  time_stamp <= timestamp ' 2005-09-09 15:46:00'
        //09.09 ca. 18.00 bis ...?
    }

    //~ Inner Classes ==========================================================

    /**
     * DOCUMENT ME!
     */

    //    private static String timeRestrictionClassicSimple() {
    //        return " density_def < 0.35 AND ((time_stamp >= timestamp '2005-08-17 14:38:00' AND"
    //        + " time_stamp <= timestamp '2005-08-18 13:50:00') OR  " +
    //        		"(time_stamp >= timestamp '2005-09-09 17:11:00' AND"
    //        + " time_stamp <= timestamp '2005-09-09 17:15:00')) ";
    //        
    //   
    //    }

    /**
     *
     */
    public static class AnnotatedInteger extends Number implements Comparable {
        private Integer value;
        private String annotation;

        /**
         * Creates a new AnnotatedInteger object.
         *
         * @param value DOCUMENT ME!
         * @param annot DOCUMENT ME!
         */
        public AnnotatedInteger(int value, String annot) {
            super();
            this.value = new Integer(value);
            annotation = annot;
        }

        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public String getAnnotation() {
            return annotation;
        }

        /**
         * DOCUMENT ME!
         *
         * @param arg0 DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public int compareTo(Object arg0) {
            return this.value.compareTo(((AnnotatedInteger) arg0).value);
        }

        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public double doubleValue() {
            return value.doubleValue();
        }

        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public float floatValue() {
            return value.floatValue();
        }

        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public int intValue() {
            return value.intValue();
        }

        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public long longValue() {
            return value.longValue();
        }
    }

    /**
     *
     */
    public static class IntFloatTuple implements Comparable {
        private float f;
        private int i;

        /**
         * Creates a new IntFloatTuple object.
         *
         * @param i DOCUMENT ME!
         * @param f DOCUMENT ME!
         */
        public IntFloatTuple(int i, float f) {
            this.i = i;
            this.f = f;
        }

        /**
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        public int compareTo(Object arg0) {
            if(this.i < ((IntFloatTuple) arg0).i) {
                return -1;
            } else if(this.i > ((IntFloatTuple) arg0).i) {
                return 1;
            } else if(this.f < ((IntFloatTuple) arg0).f) {
                return -1;
            } else if(this.f > ((IntFloatTuple) arg0).f) {
                return 1;
            } else {
                return 0;
            }
        }

        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public boolean equals(Object arg0) {
            return this.i == ((IntFloatTuple) arg0).i
            && this.f == ((IntFloatTuple) arg0).f;
        }

        /**
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return "num_nodes =" + i + ", density=" + f;
        }
    }

    /**
     *
     */
    public static class MyShapeRenderer extends XYLineAndShapeRenderer {
        private XYDataset dataset;

        /**
         * Creates a new MyShapeRenderer object.
         *
         * @param dataset DOCUMENT ME!
         */
        public MyShapeRenderer(XYDataset dataset) {
            super();
            this.dataset = dataset;
        }

        /**
         * DOCUMENT ME!
         *
         * @param row DOCUMENT ME!
         * @param column DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        /**
         * @see org.jfree.chart.renderer.AbstractRenderer#getItemShape(int,
         *      int)
         */
        public Shape getItemShape(int row, int column) {
            //            if(column == 0) {
            if(((AnnotatedInteger) dataset.getX(row, column)).getAnnotation() == "expand") {
                assert ((AnnotatedInteger) dataset.getY(row, column))
                .getAnnotation() == "expand";

                return DefaultDrawingSupplier.createStandardSeriesShapes()[5];
            } else if(((AnnotatedInteger) dataset.getX(row, column))
                .getAnnotation() == "static") {
                assert ((AnnotatedInteger) dataset.getY(row, column))
                .getAnnotation() == "static";

                return DefaultDrawingSupplier.createStandardSeriesShapes()[6];
            } else {
                return null;
            }
        }
    }

    /**
     *
     */
    private static class SameColorSupplier implements DrawingSupplier {
        private Color color;
        private GeneralPath cross = new GeneralPath();
        private Stroke stroke = new BasicStroke();
        private boolean toggle = true;

        /**
         * Creates a new SameColorSupplier object.
         *
         * @param color DOCUMENT ME!
         */
        public SameColorSupplier(Color color) {
            this.color = color;
            cross.moveTo(-2, -2);
            cross.lineTo(2, 2);
            cross.moveTo(2, -2);
            cross.lineTo(-2, 2);
        }

        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public Paint getNextOutlinePaint() {
            return color;
        }

        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public Stroke getNextOutlineStroke() {
            return stroke;
        }

        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public Paint getNextPaint() {
            return color;
        }

        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public Shape getNextShape() {
            if(toggle) {
                toggle = false;
                return new Rectangle(-1, -1, 3, 3);
            } else {
                toggle = true;
                return cross;
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public Stroke getNextStroke() {
            return stroke;
        }
    }
}
