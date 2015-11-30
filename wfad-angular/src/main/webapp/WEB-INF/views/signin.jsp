<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
	<head>
		<title>W-adhoc</title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no" charset="utf-8"/>
		<link rel="stylesheet"  href="resources/css/bootstrap.min.css" ></link>
   		<link rel="stylesheet" href="resources/css/ui-lightness/jquery-ui-1.10.3.custom.css">
   		<style type="text/css">
   		.loginTable{
			margin: auto;
			margin-top: 5%;
		}
   		</style>
	</head>
	<body>
	<div class="jumbotron">
	  	<div class="container">
	    <h1>W-adhoc!</h1>
	    <p>This is a Geo-Collaborative application based in Google Maps.</p>
	    <table class="loginTable">
			<tr>
				<td><form action="<c:url value="/signin/facebook" />" method="POST">
					    <button type="submit" class="btn btn-primary">
			  				<i class="icon-user icon-white"></i>Facebook</button>
					    <input type="hidden" name="scope" value="email,publish_stream,offline_access" />		    
				</form></td>
				<td><form action="<c:url value="/signin/twitter" />" method="POST">
					    <button type="submit" class="btn btn-info">
			  				<i class="icon-user icon-white"></i>Twitter</button>					    		    
				</form></td>
				<td><form action="<c:url value="/guest" />" method="GET">
					    <button type="submit" class="btn btn-success">
			  				<i class="icon-user icon-white"></i>Guest</button>					    		    
				</form></td>
			</tr>
		</table>		
	  	</div>
	</div>
	</body>
</html>
