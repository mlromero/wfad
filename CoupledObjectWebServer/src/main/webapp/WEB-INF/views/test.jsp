<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery-1.10.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery.json-2.3.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/sync.js"></script>
<script>
	var test=new Test($("#area"));
	$(function() {		
		$.adapter = new ClientAdapter("${pageContext.request.contextPath}/",true);
		$("#btn").click(function() {
			alert($('#ta').val());
		});
	});
	function coupleForm(ssid) {
		//$("#ta").val("");
		$.adapter.joinSession(ssid);
		alert("Ud. se ha unido a la sesion " + $.adapter.sessionId);
		var textAreaOptions = {
			getElementState : function() {
				var val = $(this).val();
				return {
					value : val
				};
			},
			setElementState : function(state) {
				$(this).val(state.value);
			},
			messageType : "STATE",
			bindings : [ 'keyup','init' ],
			cleanup : function() {
				$(this).val("");
			}
		};
		var buttonOptions = {
			bindings : [ 'click' ],
			messageType : "EVENT"
		};
		//$.adapter.coupleBinding("textarea", $("#ta"), textAreaOptions);
		 $("#ta").keyup(function(){
			test.escribir($(this).val());
		}); 
		$.adapter.coupleBinding("button", $("#btn"), buttonOptions);
		 $.adapter.coupleObject("test",test,{
			messageType:"EVENT",
			explicitMapping:["escribir"]
			
		});
		$.adapter.start();
	}
	
	function Test(area){
		this.id=$(area).attr("id");
		this.area=area;
		//this.init();
	}
	
	$.extend(Test.prototype,{
		escribir:function(texto){
			$("#area").html(texto);
		}
	
	});
</script>
<style>
.sessionform {
	background-color: lightGray;
	border: 1px solid gray;
	border-radius: 5px 5px 5px 5px;
	height: 86px;
	padding: 0;
	text-align: center;
	width: 300px;
}
</style>
</head>
<body>
	<div class="sessionform">
		<p>Unirse a una sesion y acoplar formulario</p>
		<input id="ssid" type="text" />
		<button onclick="coupleForm($('#ssid').val())">Unirse a
			sesion</button>
	</div>

	<textarea id="ta" rows="10" style="width: 500px"></textarea>
	<div id="area"></div>
	<button id="btn">Alert</button>
</body>
</html>