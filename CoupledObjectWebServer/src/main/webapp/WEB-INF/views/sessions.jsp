<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
	<title>Home</title>
</head>
<body>
<h1>
	Choose a session to clear from the list below
</h1>
<form action="clear" method="post">
<select size="5" name="ssid" style="width: 300px">
<c:forEach items="${sessions}" var="session">

<option value="${session}">${session}</option>
</c:forEach>
</select>
<button type="submit">Clear Selected Session</button>
</form>

</body>
</html>
