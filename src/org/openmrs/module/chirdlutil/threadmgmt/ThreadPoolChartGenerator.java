package org.openmrs.module.chirdlutil.threadmgmt;

import java.awt.Color;
import java.awt.GradientPaint;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * A simple demonstration application showing how to create a bar chart.
 *
 */
public class ThreadPoolChartGenerator {

    public JFreeChart getChart() {
    	final CategoryDataset dataset = createDataset();
        final JFreeChart chart = createChart(dataset);
        return chart;
    }

    private CategoryDataset createDataset() {
		ThreadManager threadManager = ThreadManager.getInstance();
		Map<String, Integer> threadStats = threadManager.getThreadPoolUsage();
		// Testing purposes only
//		Random generator = new Random();
//		threadStats.put(ThreadManager.MAIN_POOL, generator.nextInt(10));
//		threadStats.put("PCC", generator.nextInt(10));
//		threadStats.put("PEPS", generator.nextInt(10));
//		threadStats.put("BBPS", generator.nextInt(10));
//		threadStats.put("FMPS", generator.nextInt(10));
        
        // row keys...
        final String series1 = "Tasks";
        
        // create the dataset...
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        // put the main pool at the front of the list.
        Integer mainPoolSize = threadStats.get(ThreadManager.MAIN_POOL);
        if (mainPoolSize != null) {
        	dataset.addValue(mainPoolSize, series1, ThreadManager.MAIN_POOL);
        }
        
        threadStats.remove(ThreadManager.MAIN_POOL);
        Set<Entry<String, Integer>> entrySet = threadStats.entrySet();
		Iterator<Entry<String, Integer>> iter = entrySet.iterator();
		while (iter.hasNext()) {
			Entry<String, Integer> entry = iter.next();
			String location = entry.getKey();
			Integer count = entry.getValue();
			dataset.addValue(count, series1, location);
		}
        
        threadStats.clear();
        return dataset;
    }
    
    private JFreeChart createChart(final CategoryDataset dataset) {
        
        // create the chart...
        final JFreeChart chart = ChartFactory.createBarChart3D(
            "Thread Pool Monitor",    // chart title
            "Clinic",                 // domain axis label
            "# Tasks in Queue",       // range axis label
            dataset,                  // data
            PlotOrientation.VERTICAL, // orientation
            false,                    // include legend
            true,                     // tooltips?
            false                     // URLs?
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

        // set the background color for the chart...
        chart.setBackgroundPaint(Color.white);

        // get a reference to the plot for further customization...
        final CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        // set the range axis to display integers only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // disable bar outlines...
        final BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(true);
        
        // set up gradient paints for series...
        final GradientPaint gp0 = new GradientPaint(
            0.0f, 0.0f, Color.blue, 
            0.0f, 0.0f, Color.darkGray
        );
        renderer.setSeriesPaint(0, gp0);

        final CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(
            CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0)
        );
        // OPTIONAL CUSTOMISATION COMPLETED.
        
        return chart;
        
    }
}

