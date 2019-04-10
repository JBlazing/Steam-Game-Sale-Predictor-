<!DOCTYPE html>
<html>
  <head>
  <title>Steam Game Prediction</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/default.css">
  <!-- <script src="//d3js.org/d3.v3.min.js" charset="utf-8"></script> -->
  <script src="Javascript/d3.min.js"></script>
  
  </head>
  <body style="background: url(${pageContext.request.contextPath}/background.png) center top no-repeat #1b2838;">
    <div class="container">
      <center><p style="font-size: 20pt;">${data}</p></center>
      <center><svg class="chart"></svg></center>
      
      <div class="searchform">
      <form action = "FirstServlet" method = "post">
        <input type="text" name="gamename">
        <input type = "submit" value = "Submit" class = "big-button">
      </form>
      </div>
    </div>
    ${chartscript}
  </body>
</html>

 