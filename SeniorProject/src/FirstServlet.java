

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.experiment.InstanceQuery;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToBinary;
import weka.filters.unsupervised.attribute.NumericToNominal;
import weka.filters.unsupervised.attribute.Remove;

/**
 * Servlet implementation class FirstServlet
 */
@WebServlet("/FirstServlet")
public class FirstServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final String HTML_START="<html><body>";
	public static final String HTML_END="</body></html>";
	public static final int saleCutoff = 7;
	public static final String[] classifierPaths = {"Classifiers/RandomForestJan.model",
													"Classifiers/RandomForestFeb.model",
													"Classifiers/RandomForestMar.model",
													"Classifiers/RandomForestApr.model",
													"Classifiers/RandomForestMay.model",
													"Classifiers/RandomForestJun.model",
													"Classifiers/RandomForestJul.model",
													"Classifiers/RandomForestAug.model",
													"Classifiers/RandomForestSep.model",
													"Classifiers/RandomForestOct.model",
													"Classifiers/RandomForestNov.model",
													"Classifiers/RandomForestDec.model"};
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FirstServlet() {
        super();
        // TODO Auto-generated constructor stub

    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html");
		
		DateFormat dateFormat;
        try {
            dateFormat = new SimpleDateFormat(request.getParameter("format"));
        } catch (Exception e) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:MM:ss");
        }
        request.setAttribute("date", dateFormat.format(new Date()));
        request.setAttribute("info", getServletContext().getServerInfo());
        request.getRequestDispatcher("JSP/Index.jsp").forward(
                request, response);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//doGet(request, response);
		String searchParam = request.getParameter("gamename");
		String modifiedParam = searchParam.replaceAll("'", "''"); 
		NumericToBinary filter = new NumericToBinary();
		NumericToNominal classFilter = new NumericToNominal();
		int[] indices = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
		InstanceQuery query = null;
		try {
			query = new InstanceQuery();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Calendar cal = Calendar.getInstance();
        int currentMonth = cal.get(Calendar.MONTH);
        
        query.setQuery("SELECT PUBLISHER, INDIE, RPG, SIMULATION, STRATEGY, ACTION, RACING, MASSIVELYMULTIPLAYER, ADVENTURE, SPORTS, "
        		+ "CASUAL, RELEASEDATE, PRICE, SCORE, OWNERS, JANUARY FROM GAME WHERE TITLE='" + modifiedParam + "' OR APPID='" + modifiedParam + "'");
		
		Instances d2 = null;
		try {
			d2 = query.retrieveInstances();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		classFilter.setAttributeIndices("last");
		filter.setAttributeIndicesArray(indices);
        filter.setIgnoreClass(true);
        try {
        	filter.setInputFormat(d2);
        	classFilter.setInputFormat(d2);
				
	        d2 = Filter.useFilter(d2, filter);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
		d2.setClassIndex(d2.numAttributes() - 1);
		double[] classifications = new double[12];
		if(d2.numInstances() > 0)
		{
			for(int i = 0; i < classifierPaths.length; i++)
			{
				RandomForest rf = null;
				try {
					rf = (RandomForest)weka.core.SerializationHelper.read(classifierPaths[i]);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(rf != null)
				{
					for(int j = 0; j < d2.numInstances(); j++)
					{
						double classification = 0;
						try {
							classifications[i] = rf.classifyInstance(d2.instance(j));
							request.setAttribute(new DateFormatSymbols().getMonths()[i].toLowerCase(), classification);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
			
			int j = currentMonth;
			int saleCutoffVal = saleCutoff;
			while(true)
			{
				if(classifications[j] > saleCutoffVal)
				{
					request.setAttribute("data", searchParam + " will be on sale in " + new DateFormatSymbols().getMonths()[j]);
					break;
				}
				
				j++;
				if(j >= 12) j = 0;
				
				if(j == currentMonth) saleCutoffVal -= 1;
			}
			
			request.setAttribute("chartscript", 
					"<script>" +

			"var data = ["+classifications[0]+","+classifications[1]+","+classifications[2]+","+classifications[3]+","+classifications[4]+","+classifications[5]+","+
			classifications[6]+","+classifications[7]+","+classifications[8]+","+classifications[9]+","+classifications[10]+","+classifications[11]+"];"+

			"var margin = {top: 20, right: 30, bottom: 40, left: 90}," +
			"width = 1200 - margin.left - margin.right," +
			"height = 500 - margin.top - margin.bottom;" +

			"var x = d3.time.scale()" +
			".domain([new Date(2016, 0, 1), new Date(2016, 11, 31)])" +
			".range([0, width]);" +

			"var y = d3.scale.linear()" +
			".domain([0, 10])" +
			".range([height, 0]);" +

			"var xAxis = d3.svg.axis()" +
			".scale(x)" +
			".orient(\"bottom\")" +
			".ticks(d3.time.months)" +
			".tickSize(16, 0)" +
			".tickFormat(d3.time.format(\"%b\"));" +
			
			"var yAxis = d3.svg.axis().scale(y).orient(\"left\").tickFormat(function(d){return d*10 + \"%\"});" +

			"var chart = d3.select(\"svg.chart\")" +
				".attr(\"width\", width + margin.left + margin.right)" +
				".attr(\"height\", height + margin.top + margin.bottom)" +
			".append(\"g\")" +
				".attr(\"transform\", \"translate(\" + margin.left + \",\" + margin.top + \")\");" +

			
			"var barWidth = width / data.length;" +

			"chart.append(\"g\")" +
			".attr(\"class\", \"x axis\")" +
			".attr(\"transform\", \"translate(0,\" + height + \")\")" +
			".call(xAxis);" +
			
			"chart.append(\"g\").attr(\"class\", \"y axis\").attr(\"transform\", \"translate(-35, 0)\").call(yAxis);" +

			"var bar = chart.selectAll(\".bar\")"+
				".data(data)" +
			".enter().append(\"rect\")" +
				".attr(\"class\", \"bar\")" +
				".attr(\"x\", -30)" +
				".attr(\"y\", function(d) { return y(d); })" +
				".attr(\"height\", function(d) { return height - y(d);})" +
				".attr(\"width\", width / 12 - 10)" +
				".attr(\"transform\", function(d, i) { return \"translate(\" + i * barWidth + \",0)\"; });" +

			"bar.append(\"text\").attr(\"x\", barWidth / 2).attr(\"y\", function(d) { return y(d) + 3; }).attr(\"dy\", \".75em\").text(function(d) { return d; });" +
			
			"</script>"
					);
		}
		else
		{
			request.setAttribute("data", "Game " + searchParam + " not found");
		}

		/*
		if(d2.numInstances() > 0)
		{
			try {
				
				int j = currentMonth;
				do
				{
					RandomForest rf = (RandomForest)weka.core.SerializationHelper.read(classifierPaths[j]);
					if(d2.classAttribute().value((int)rf.classifyInstance(d2.firstInstance())) == "1")
					{
						request.setAttribute("data", searchParam + " will be on sale in " + new DateFormatSymbols().getMonths()[j]);
						break;
					}
					
					j++;
					if(j >= 12) j = 0;
					
				} while (j != currentMonth);
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			request.setAttribute("data", "Game " + searchParam + " not found");
		}
		*/
		
		request.getRequestDispatcher("JSP/Index.jsp").forward(
                request, response);
	}
}
