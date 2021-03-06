<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page session="false"%>

<html lang="en" ng-app="workflow">
<head>
    <title>W-adhoc</title>
    <meta charset="utf-8">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">    
    <meta name="viewport" content="width=device-width, initial-scale=1">    
    
    <link rel="stylesheet" href="http://code.jquery.com/mobile/1.4.2/jquery.mobile-1.4.2.css"></link>    
    <link rel="stylesheet" type="text/css" href="resources/js/bootstrap/css/bootstrap.min.css"></link>
    <link href="//maxcdn.bootstrapcdn.com/font-awesome/4.1.0/css/font-awesome.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="resources/css/map.css" ></link>
    
    <script type="text/javascript" src="http://code.jquery.com/jquery-2.1.1.js"></script> 
	<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>	
	<script>
    	var jq = jQuery.noConflict();
	</script>
	<script type="text/javascript" src="http://code.jquery.com/mobile/1.4.3/jquery.mobile-1.4.3.js"></script>	
	
	<!-- dropdown con imagenes -->
	<script type="text/javascript" src="resources/js/dropdown/jquery.min.js"></script>
	<script type="text/javascript" src="resources/js/dropdown/jquery.ddslick.min.js"></script> 
	<!-- fin dropdown con imagenes --> 
	 
	<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?libraries=drawing,geometry,event"></script>
	<!-- <script src="http://maps.googleapis.com/maps/api/js?sensor=false"></script> -->
	<script src="resources/_assets/js/index.js"></script>	
	<script type="text/javascript" src="resources/js/jquery.json-2.3.min.js"></script>    
    <!-- <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.25/angular.min.js"></script> -->
      
    <script src="//ajax.googleapis.com/ajax/libs/angularjs/1.2.16/angular.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/angularjs/1.2.16/angular-touch.min.js"></script> 
    <script src="resources/js/jqm-angular/angular-jqm.js" type="text/javascript"></script> 
    <script src="resources/js/ui-bootstrap/ui-bootstrap-0.11.2.js"></script>   
     
    
   
    <script type="text/javascript" src="resources/js/googlemaps/lodash.underscore.js"></script>     
    <script type="text/javascript" src="resources/js/googlemaps/bluebird.js"></script>
    
    <script type="text/javascript" src="https://rawgit.com/angular-ui/angular-google-maps/2.0.6/dist/angular-google-maps.min.js"></script>
    
    <!-- <script type="text/javascript" src="resources/js/googlemaps/angular-google-maps.js"></script> -->
    
    
     <script src="resources/js/ment-io/ment.io.js"></script>
       
    <script type="text/javascript" src="resources/js/model/process.js"></script>   
    <script type="text/javascript" src="resources/js/model/app.js"></script>
    <script type="text/javascript" src="resources/js/model/map.js"></script>  
      
	<script type="text/javascript" src="resources/js/model/tweet.js"></script>
   <script type="text/javascript" src="/CoupledObjectWebServer/resources/js/sync.js"></script>
   
   <script>
   //$.mobile.ignoreContentEnabled =true;
	window.currentProcess = '${session}';
	window.username = '${name}';
	window.processState = '${state}';
	
	jq.mobile.ignoreContentEnabled =true;
	$(document).bind('mobileinit',function(){
		jq.mobile.selectmenu.prototype.options.nativeMenu = false;
	});
	
	
	
	</script>
    
</head>
<body  ng-controller="appController" >



<div id="main" data-role="page"  class="ui-responsive-panel">
    <div data-role="header">
    	<h1><div ng-show="currentProcess==null || currentProcess==''">Hi ${name}.</div>
        <div ng-show="currentProcess!=null && currentProcess!=''">Hi ${name}. Welcome to {{currentProcess}}.</div></h1>
        <!-- Boton panel lateral izquierdo <a href="#mypanel">Open panel</a> -->
		<a href="#mypanel" data-icon="gear" id="logout" data-iconpos="notext" ng-click="getProcessesByUser(); getUsers();" tooltip-html-unsafe="{{tooltip.gear}}" tooltip-trigger="mouseenter" tooltip-placement="right">Options</a>		
				
		<!-- Boton de panel lateral derecho -->
		<a href="#add-form" data-icon="bars" data-iconpos="notext" ng-click="getUsersBySession();" tooltip-html-unsafe="{{tooltip.bars}}" tooltip-trigger="mouseenter" tooltip-placement="left">Menu</a>         
    </div>
    
    <div data-role="content" ng-controller="mapController">       
        <ui-gmap-google-map 
        	id="map"
        	center="map.center" 
        	zoom="map.zoom" 
        	control="map.control"
        	events="map.events">
        	<ui-gmap-drawing-manager 
        		options="drawingManagerOptions"        		 
        		control="drawingManagerControl">
        	</ui-gmap-drawing-manager>
        		
    	</ui-gmap-google-map>
        
    </div>  
        
    <div data-role="footer">	
		<h3>@Version 1.2.0</h3>
	</div>
	
	<div data-role="panel" id="mypanel" data-theme="d">		
		<pre><i class="pull-left glyphicon" ng-class="{'glyphicon-user': true}" ></i> ${name} </pre>
		<pre><i class="pull-left glyphicon" ng-class="{'glyphicon-cog': true}"></i> Options </pre>
       
        <accordion close-others="status.oneAtATime"  tooltip-html-unsafe="{{tooltip.processes}}" tooltip-trigger="mouseenter">
        	<accordion-group ng-show="{{currentProcess != null && currentProcess !=''}}">
	        	<accordion-heading>
	            		Add friends <i class="pull-right glyphicon" ng-class="{'glyphicon-chevron-down': status.open, 'glyphicon-chevron-right': !status.open}"></i>
        		</accordion-heading>
        		<form name="addFriendForm" ng-submit="addFriend()" novalidate>
        			
					<alert ng-repeat="alert in alertsFriend" type="{{alert.type}}" close="closeAlert($index)">{{alert.msg}}</alert>
					    			
        			<input type="text" name="friend" ng-model="selectedFriend" typeahead="user for user in users | filter:$viewValue | limitTo:8" class="form-control" typeahead-on-select="selectFriend($item)"  required/>
        	
        			<button type="submit" class="btn btn-primary" data-mini="true" ng-disabled="!addFriendForm.$valid">Add Friend</button>
        		</form>
        	</accordion-group>
        	<accordion-group is-open="status.isFirstOpen" is-disabled="status.isFirstDisabled" >
        		<accordion-heading>
            		Processes <i class="pull-right glyphicon" ng-class="{'glyphicon-chevron-down': status.open, 'glyphicon-chevron-right': !status.open}"></i>
        		</accordion-heading>
        		
        		<alert type="danger" ng-show="error.chargeProcess != null" data-mini="true">{{error.chargeProcess}}</alert>
        		<form role="form" ng-repeat="proceso in processes" action='map' method='post' data-ajax='false'>
        			<input type='hidden' name='session' value='{{proceso}}'>
	        		<button type="submit" id="{{proceso}}" class="btn btn-primary" onclick="$(this).parents('form').prop({'action':'map'})">
				        {{proceso}}
				    </button> 
        		</form>          		      			
    		</accordion-group>
    	</accordion>
		
		<a href="<c:url value="/logout" />" tooltip-html-unsafe="{{tooltip.closeSession}}" tooltip-trigger="mouseenter" tooltip-placement="bottom" data-role="button" data-mini="true">
			<i class="pull-left glyphicon" ng-class="{'glyphicon-log-out': true}"></i>Logout
		</a>	 
		<!--  -->       
       
    </div>
    
    <div data-role="panel" id="add-form" data-position="right" data-swipe-close="false" data-dismissible="false">   
    	<pre><i class="pull-left glyphicon" ng-class="{'glyphicon-pencil': true}"></i> Process {{currentProcess}}</pre>
    	
    	<form name="tweetForm" ng-submit="preAddTweet()" novalidate> 
    	
    		<a href="#" tooltip-html-unsafe="{{tooltip.pattern}}" tooltip-trigger="mouseenter" tooltip-placement="top">  		
    			<div id="myDropdown" class="target tipped tipper-attached" ></div>
    		</a>   		   		
    		<alert ng-show="(tweetForm.txt.$error.tweetFormat || tweetForm.$valid) && error.tweetFormat != null" type="danger">{{error.tweetFormat}}</alert>
    		<textarea mentio
				name="txt"
				id="txt"
				ng-model="tweet.txt"				
	            
	            mentio-trigger-char="'@'"
	            mentio-items="people"
	            mentio-search="searchUsers(term)"
	            mentio-select="getPeopleTextRaw(item)"	            	
	            class="editor form-control"
	            mentio-id="'theTextArea'"
	            ng-trim="false"
	            rows="6"
	            tweet-format
	            required>
            </textarea> 
            <input id="estado" value="{{process.state}}" />
            <!-- 
            mentio-macros="macros"
            mentio-typed-term="typedTerm2"
             -->  		
            
           
            <mentio-menu
	            mentio-for="'theTextArea'"
	            mentio-trigger-char="'^'"
	            mentio-items="products"
	            mentio-search="searchFlows(term)"
	            mentio-select="getFLowsTextRaw(item)"></mentio-menu>
	            
	            
            <ul class="dropdown-menu scrollable-menu" style="display:block" ng-show="false">
			    <li mentio-menu-item="item" ng-repeat="item in items track by $index">
			        <a class="text-primary" ng-bind-html="item.label | mentioHighlight:typedTerm:'menu-highlighted' | unsafe"></a>
			    </li>
			</ul>    		   		
    		   		
    		<button type="submit" class="btn btn-warning" data-mini="true" id="tweet" ng-disabled="!tweetForm.$valid" tooltip-html-unsafe="{{tooltip.tweet}}" tooltip-trigger="mouseenter" tooltip-placement="bottom">Tweet</button>        
    	</form> 
    	
    	<div ng-controller="tweetController">  	
    		<div ng-show="tweets.length > 0">
    		<input type="search" ng-model="f" placeholder="filter by user or by flow..."  />
    		</div>
    		<div class="example-animate-container">
   				 <div  ng-repeat="tweet in tweets | filter: {userFlow: f} | reverse">    		
	    			<div class='div-msg toggler clickme noselect' ng-click="showOverlays(tweet.id)" ng-class="colorClass[tweet.id]" >
	    				<div class='ui-bar ui-bar-a strech' style='font-size:0.8em;'>
							<h3>@{{tweet.createdBy}} at {{tweet.createdAt}}</h3>
						</div>
						<div class='ui-body ui-body-a wrap strech' style='font-size:0.9em;'  >
							<p>{{tweet.body}}</p>
						</div>
	    			</div>
	    		</div>  
    			 <div class="animate-repeat" ng-if="results.length == 0">
			      <strong>No results found...</strong>
			    </div>			
    		</div>
    	  	
    </div>       
</div>


<form ng-show="false" action='map' method='post' data-ajax='false'>
	<input type='hidden' name='session' value="{{session}}">
 	<button type="submit" id="newProcess" class="btn btn-primary" onclick="$(this).parents('form').prop({'action':'map'})"></button> 
</form>  




</body>
</html>