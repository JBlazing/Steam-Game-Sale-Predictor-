import java.io.IOException;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToBinary;

@WebServlet("/NonSteam")
public class NonSteam extends HttpServlet{
	private static final long serialVersionUID = 2L;
	
	public NonSteam() {
		super();
	}
	
	ArrayList<String> classify(
			String publisher1,
			boolean indie1,
			boolean rpg1,
			boolean simulation1,
			boolean strategy1,
			boolean action1,
			boolean racing1,
			boolean mmo1,
			boolean adventure1,
			boolean sports1,
			boolean casual1,
			String releasedate1,
			float price1,
			float score1,
			float owners1)
	{
		ArrayList<String> result = new ArrayList<String>(12);
		try {
			NumericToBinary filter = new NumericToBinary();
			ArrayList<Attribute> attributeList = new ArrayList<>(15);
			
			Attribute publisher = new Attribute("PUBLISHER", true);
			Attribute indie = new Attribute("INDIE");
			Attribute rpg = new Attribute("RPG");
			Attribute simulation = new Attribute("SIMULATION");
			Attribute strategy = new Attribute("STRATEGY");
			Attribute action = new Attribute("ACTION");
			Attribute racing = new Attribute("RACING");
			Attribute mmo = new Attribute("MASSIVELYMULTIPLAYER");
			Attribute adventure = new Attribute("ADVENTURE");
			Attribute sports = new Attribute("SPORTS");
			Attribute casual = new Attribute("CASUAL");
			Attribute releasedate = new Attribute("RELEASEDATE", true);
			Attribute price = new Attribute("PRICE");
			Attribute score = new Attribute("SCORE");
			Attribute owners = new Attribute("OWNERS");
			
			attributeList.add(publisher);
			attributeList.add(indie);
			attributeList.add(rpg);
			attributeList.add(simulation);
			attributeList.add(strategy);
			attributeList.add(action);
			attributeList.add(racing);
			attributeList.add(mmo);
			attributeList.add(adventure);
			attributeList.add(sports);
			attributeList.add(casual);
			attributeList.add(releasedate);
			attributeList.add(price);
			attributeList.add(score);
			attributeList.add(owners);
			
			attributeList.add(new Attribute("@@type@@"));
			
			Instances data = new Instances("TestInstances", attributeList, 0);
			
			Instance inst_co = new DenseInstance(data.numAttributes());
			data.add(inst_co);
			inst_co.setDataset(data);
			
			inst_co.setValue(publisher, publisher1);
			inst_co.setValue(indie, indie1 ? 1 : 0);
			inst_co.setValue(rpg, rpg1 ? 1 : 0);
			inst_co.setValue(simulation, simulation1 ? 1 : 0);
			inst_co.setValue(strategy, strategy1 ? 1 : 0);
			inst_co.setValue(action, action1 ? 1 : 0);
			inst_co.setValue(racing, racing1 ? 1 : 0);
			inst_co.setValue(mmo, mmo1 ? 1 : 0);
			inst_co.setValue(adventure, adventure1 ? 1 : 0);
			inst_co.setValue(sports, sports1 ? 1 : 0);
			inst_co.setValue(casual, casual1 ? 1 : 0);
			inst_co.setValue(releasedate, releasedate1);
			inst_co.setValue(price, price1);
			inst_co.setValue(score, score1);
			inst_co.setValue(owners, owners1);
			
			filter.setAttributeIndicesArray(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
	        filter.setIgnoreClass(true);
			filter.setInputFormat(data);
	        data = Filter.useFilter(data, filter);
	        data.setClassIndex(data.numAttributes() - 1);
	     
	        System.out.println(data.toString());
			
			for(int i = 0; i < 12; i++)
			{
				RandomForest rf = (RandomForest)weka.core.SerializationHelper.read(FirstServlet.classifierPaths[i]);
				double result1 = rf.classifyInstance(data.firstInstance());
				result.add(Double.toString(result1));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html");

        request.getRequestDispatcher("JSP/NonSteam.jsp").forward(
                request, response);
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Form parameters
		String p_publisher, p_genre, p_releasedate, p_price, p_score, p_owners;
		p_publisher = request.getParameter("publisher");
		p_genre = request.getParameter("genre");
		p_releasedate = request.getParameter("releasedate");
		p_price = request.getParameter("price");
		p_score = request.getParameter("score");
		p_owners = request.getParameter("owners");
		
		Calendar cal = Calendar.getInstance();
        int currentMonth = cal.get(Calendar.MONTH);
		
		//Some type of validation of genre and publisher should go here
		
		//Make list of genres
		String[] temp_genres = p_genre.split(",");
		ArrayList<String> genres = new ArrayList<>();
		for(int i = 0; i < temp_genres.length; i++) genres.add(temp_genres[i].trim().toLowerCase());
		
		//Make release date
		/*Date releasedate = null;
		try {
			releasedate = DateFormat.getInstance().parse(p_releasedate);
		} catch (ParseException e) {
			e.printStackTrace();
		}*/
		
		ArrayList<String> classifications = this.classify(p_publisher,
				genres.contains("indie"), 
				genres.contains("rpg"), 
				genres.contains("simulation"), 
				genres.contains("strategy"), 
				genres.contains("action"), 
				genres.contains("racing"),
				genres.contains("mmo"),
				genres.contains("adventure"),
				genres.contains("sports"), 
				genres.contains("casual"), 
				p_releasedate, 
				Float.parseFloat(p_price), 
				Float.parseFloat(p_score), 
				Float.parseFloat(p_owners));
		
		int j = currentMonth;
		int saleCutoffVal = FirstServlet.saleCutoff;
		while(true)
		{
			if(Double.parseDouble(classifications.get(j)) > saleCutoffVal)
			{
				request.setAttribute("data", "The game will be on sale in " + new DateFormatSymbols().getMonths()[j]);
				break;
			}
			
			j++;
			if(j >= 12) j = 0;
			
			if(j == currentMonth) saleCutoffVal -= 1;
		}
		
		/*
		for(int i = 0 ; i < 12 ; i++ )
		{
			
			int c = Integer.valueOf(classifications.get(i));
			classifications.set(i, Integer.toString(c * 10) );
		}
		*/
		request.setAttribute("chartscript", 
				"<script>" +
		
		"var data = ["+classifications.get(0)+","+classifications.get(1)+","+classifications.get(2)+","+classifications.get(3)+","+classifications.get(4)+","+classifications.get(5)+","+
		classifications.get(6)+","+classifications.get(7)+","+classifications.get(8)+","+classifications.get(9)+","+classifications.get(10)+","+classifications.get(11)+"];"+
		
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
		
		request.getRequestDispatcher("JSP/NonSteam.jsp").forward(
                request, response);
	}
}
