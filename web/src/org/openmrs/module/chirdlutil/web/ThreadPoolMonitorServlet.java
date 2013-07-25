package org.openmrs.module.chirdlutil.web;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.openmrs.module.chirdlutil.threadmgmt.ThreadPoolChartGenerator;

public class ThreadPoolMonitorServlet extends HttpServlet {
	private Log log = LogFactory.getLog(this.getClass());
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		genGraph(req, resp);
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		genGraph(req, resp);
	}
	
	public void genGraph(HttpServletRequest req, HttpServletResponse resp) {
		resp.setContentType("image/png");
		resp.setHeader("Cache-Control", "no-cache");
		resp.setDateHeader("Expires", 0);
		try {
			OutputStream out = resp.getOutputStream();
			ThreadPoolChartGenerator barChart = new ThreadPoolChartGenerator();
			JFreeChart chart = barChart.getChart();
			resp.setContentType("image/png");
		    ChartUtilities.writeChartAsPNG(out, chart, 625, 500);
		} catch (IOException e) {
			log.error("Error creating thread pool manager chart", e);
		}
	}
}
