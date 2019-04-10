<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Non-Steam Game</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/default.css">
<script src="Javascript/d3.min.js"></script>
</head>
<body>
<center><p style="font-size: 20pt;">${data}</p></center>
<center><svg class="chart"></svg></center>
<div class="searchform">
	<form action = "NonSteam" method = "post">
	<div style="float:left; width: 40%;">
		Publisher:</br>
		<input type="text" name="publisher">
		Genres:</br>
		<input type="text" name="genre">
		Release Date:</br>
		<input type="date" name="releasedate">
		</div>
		<div style="float: right; width:40%;">
		Price:</br>
		<input type="number" step="0.01" min="0" name="price">
		User Score:</br>
		<input type="number" min="0" max="100" name="score">
		Total Sales:</br>
		<input type="number" min="0" name="owners">
		</div>
		<input type = "submit" value = "Submit" class = "big-button">
	</form>
</div>
${chartscript}
</body>
</html>