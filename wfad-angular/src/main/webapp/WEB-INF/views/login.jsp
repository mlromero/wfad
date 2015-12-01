<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>

<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>W-adhoc</title>

<link rel="stylesheet" href="http://code.jquery.com/mobile/1.4.2/jquery.mobile-1.4.2.css"></link>
<link rel="stylesheet" type="text/css" href="resources/js/bootstrap/css/bootstrap.min.css"></link>
<link href="//maxcdn.bootstrapcdn.com/font-awesome/4.1.0/css/font-awesome.min.css" rel="stylesheet">
<link rel="stylesheet" type="text/css" href="resources/css/map.css" ></link>


<script src="//ajax.googleapis.com/ajax/libs/angularjs/1.2.16/angular.min.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/angularjs/1.2.16/angular-touch.min.js"></script> 
<script src="resources/js/jqm-angular/angular-jqm.js" type="text/javascript"></script> 
<script src="resources/js/ui-bootstrap/ui-bootstrap-0.11.2.js"></script>

<script type="text/javascript" src="http://code.jquery.com/jquery-2.1.1.js"></script> 

<style>
.error{
	color:red;
}

#register{
	color: #fff!important;
}


</style>



</head>
<body>



  <div id="login-overlay" class="modal-dialog">
      <div class="modal-content">
          <div class="modal-header">
              <!-- <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">�</span><span class="sr-only">Close</span></button> -->
              <h4 class="modal-title" id="myModalLabel">Welcome to W-adhoc!</h4>
          </div>
          <div class="modal-body">
              <div class="row">
                  <div class="col-xs-6">
                      <div class="well">
                          <form action="login" method="post">
                          <span class="error">${msg}</span>
                              <div class="form-group">
                                  <label for="username" class="control-label">Username</label>
                                  <input type="text" class="form-control" id="user" name="user" value="" required="true" title="Please enter you username" placeholder="username">
                                  <span class="help-block"></span>
                              </div>
                              <div class="form-group">
                                  <label for="password" class="control-label">Password</label>
                                  <input type="password" class="form-control" id="pass" name="pass" value="" required="true" title="Please enter your password" placeholder="password">
                                  <span class="help-block"></span>
                              </div>
                              <div id="loginErrorMsg" class="alert alert-error hide">Wrong username og password</div>
                              
                              <button type="submit" class="btn btn-success btn-block" id="login">Login</button>                              
                          </form>
                      </div>
                  </div>
                  <div class="col-xs-6">
                      <p class="lead">Register now for <span class="text-success">FREE</span></p>
                      <ul class="list-unstyled" style="line-height: 2">
                      	  <li><span class="fa fa-check text-success"></span> Add and manage all your process</li>
                          <li><span class="fa fa-check text-success"></span> Tweet a message as a flow(task)</li>
                          <li><span class="fa fa-check text-success"></span> Set geo-localizations to a flow</li>
                          <li><span class="fa fa-check text-success"></span> Complete your flows and process</li>
                          
                          
                      </ul>
                      	<script>
						console.log("Estoy en login2");
						</script>
                      	<p><a href="register" class="btn btn-info btn-block" id="register">Yes please, register now!</a></p>
                      
                  </div>
              </div>
          </div>
      </div>
  </div> 
</div>
	
<script language="javascript" type="text/javascript">

$(document).ready(function(){
	setTimeout(fu, 100);
	
});

function fu(){
	$('#login').removeClass( "ui-btn ui-shadow ui-corner-all" );
	$('#register').removeClass("ui-link");
	$("#register").css({"color":"white", "font-weight": "normal", "text-shadow":"none"});
	$("label[for='username']").css({"font-weight": "bold"});
	$("label[for='password']").css({"font-weight": "bold"});	
	$("div").removeClass("ui-input-text ui-body-inherit ui-corner-all ui-shadow-inset");	
}
    
</script>




	
		
	
	
</body>
</html>
