import java.awt.Dimension;

import javax.swing.JFrame;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

/**
 * Using the JFreeChart Library, graphs data collected from the pulse sensor.
 * This class is used only for testing purposes.
 * 
 * @author Matthew Flynn
 * @version January 22nd, 2018
 */
class ReadingGraph extends JFrame {
    
    private ChartPanel content;
    
    /**
     * Creates a new graph, displaying the data given
     * 
     * @param  an array of results from the pulse sensor
     */
    public ReadingGraph(short[] data) {
        super("Heart Rate Graph");
        content = new ChartPanel(createChart(createDataset(data)));
        content.setPreferredSize(new Dimension(500, 270));
        setContentPane(content);
    }
    
    private static XYDataset createDataset(short[] data) {
        final XYSeries series = new XYSeries("data");
        for (int i = 0;i < data.length;i++) {
            series.add(i, data[i]);
        }
        final XYSeriesCollection ret = new XYSeriesCollection();
        ret.addSeries(series);
        return ret;
    }
    
    private static JFreeChart createChart(final XYDataset data) {
        final JFreeChart chart = ChartFactory.createXYLineChart(
            "Heart Rate",
            "Sample #",
            "Pulse Sensor Output",
            data,
            PlotOrientation.VERTICAL,
            false,
            true,
            false
       );
       XYPlot plot = (XYPlot)chart.getPlot();
       plot.getRangeAxis().setRange(0.0, 4100.0);
       return chart;
    }
    
}
