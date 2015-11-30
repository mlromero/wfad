

var app = angular.module("workflow", ['google-maps'.ns(), 'ui.bootstrap', 'mentio']);//,  'ngRoute']);

app.filter('reverse', function() {
  return function(items) {
    return items.slice().reverse();
  };
});

 
app.controller('appController', function($scope, $http, $window, $q, ConstantsService, tweetService, mapService, sizeService){
	
	 
	
	$scope.selectedData = {}; 	
	//Cuadro de texto donde se ingresan los tweets
	$scope.tweet = {txt: ($window.currentProcess== null || $window.currentProcess == "")?'':'%1 #'+$window.currentProcess};	
	$scope.currentProcess = $window.currentProcess;	
	$scope.process = ($window.currentProcess == null || $window.currentProcess == "")? null:new Process($scope.currentProcess, $window.processState);
	$scope.username = $window.username;
	$scope.tooltip = {
			tweetButton: ($window.currentProcess == null || $window.currentProcess == "")?'Add a new process':'Add a new tweet',
			closeSession: "Logout",
			processes: ($window.currentProcess == null || $window.currentProcess =="")?"Enter into a process":"Add friend to current process or Enter into your processes",
			pattern: ($window.currentProcess == null || $window.currentProcess =="")?"Create a new Process or Enter into old processes":"Select a pattern",
			gear: "Your Process and friends",
			bars: "Tweets and Flows!",
			tweet: "Send tweet"
	};
	
	$scope.error = {}; //chargeProcess: 
	
	$scope.processes;
	$scope.currentUsers = [];
	
	
	$scope.selectedFriend = undefined;
	$scope.users = [];	
	$scope.flows = [];
	
	
	$scope.status = {
			oneAtATime: true,
			isFirstOpen: false,
			isFirstDisabled: false
	};	
	
	$scope.alertsFriend = [{  msg: 'Add a new friend!' }                
	              ];
	
	$scope.addAlert = function(t, m) {
		if(t != null)
			$scope.alertsFriend.push({ type: t, msg: m});
		else
			$scope.alertsFriend.push({msg: m});		
	};

	$scope.closeAlert = function(index) {
		$scope.alertsFriend.splice(index, 1);
	};
	
	//Hace aparecer la barra de dibujo
	$scope.setDrawing = function(value){		
		mapService.setDrawingControl(value);
	};
	
	
	
	//Dibujos. 
	$scope.overlaysTemp = {}; //variable de manejo temporal (antes de enviar)
	$scope.overlaysEncode = {}; //variable que contiene el formato como se enviaran las figuras
	
	//Estados y patrones.
	$scope.pattern = {"start": 4, "split":2, "joinA":3, "joinB":5, "sequence":1, "end":0 }; //Manejo a nivel de patron de workflow
	$scope.patternArray = ["11","1", "2", "3a","","3b"];
	$scope.processState = {"start": 1, "sequence":2, "split":3, "joinA":4, "joinB":5, "end":6}; //Manejo de nivel de proceso (en BD)
	
	$scope.cambioEstado = false;
	
	
	///-------------- ment.io -----------------------------
	/// http://jeff-collins.github.io/ment.io/#/ ----------
	
	$scope.products = [];
	$scope.people = [];	
	$scope.items = [];
	$scope.myIndexValue = "5";

    $scope.searchFlows = function(term) {
        var prodList = [];
        angular.forEach($scope.flows, function(item) {
            if (item.title.toUpperCase().indexOf(term.toUpperCase()) >= 0) {
                prodList.push(item);
            }
        });        
        $scope.products = prodList;
        $scope.items = prodList;
        return $q.when(prodList);         
       
    };
    
    $scope.searchUsers = function(term) {
        var peopleList = [];
        angular.forEach($scope.currentUsers, function(item) {
        	console.log(item);
            if (item.name.toUpperCase().indexOf(term.toUpperCase()) >= 0) {
                peopleList.push(item);
            }
        });
        $scope.people = peopleList;
        $scope.items = peopleList;
        return $q.when(peopleList);
        
    };
    
    $scope.getPeopleTextRaw = function(item) {
        return '@' + item.name;
    };
    
    $scope.getFlowsTextRaw = function(item) {
        return item.name;
    };
    
   
	
	////-----------typeahead-----------------------------------
	
	$scope.selectFriend = function(user){
		$scope.selectedFriend = user;		 
	};
	
	
	
	
	///------------Desde client a Servidor ------------------
	
	$scope.preAddTweet = function(){
		
		if($scope.process == null && $scope.tweet.pattern != 4)
			return;
		
		if($scope.tweet.txt.length <= 1)
			alert("Process's name is too short.");
		
		console.log("Pattern: "+$scope.tweet.pattern);
		
		switch($scope.tweet.pattern){
		case 1: //sequence
			$scope.setProcessState($scope.processState.sequence);
			if($scope.cambioEstado)
				$scope.addTweet();
			else
				$scope.cleanOverlaysTemp();
			break;
		case 2: //split
			$scope.setProcessState($scope.processState.split);
			if($scope.cambioEstado)
				$scope.addTweet();
			else
				$scope.cleanOverlaysTemp();
			break;
		case 3: //join a
			$scope.setProcessState($scope.processState.joinA);
			if($scope.cambioEstado){
				$scope.addTweet();
				$scope.closeSplit('a');
			}else
				$scope.cleanOverlaysTemp();
			break;
		case 5: // join b
			$scope.setProcessState($scope.processState.joinB);
			if($scope.cambioEstado){
				$scope.addTweet();
				$scope.closeSplit('b');
			}else
				$scope.cleanOverlaysTemp();
			break;
		case 4: // start
			
			var end = $scope.tweet.txt.indexOf(' ');
			if(end < 0)
				end = $scope.tweet.txt.length;
			$scope.session = $scope.tweet.txt.substr(1, end);
			$scope.newSession();
			$scope.addTweet();
			break;
		case 0: //finish
			$scope.setProcessState($scope.processState.end);
			if($scope.cambioEstado){
				$scope.addTweet();
				$scope.closeProcess();
			}else
				$scope.cleanOverlaysTemp();
			break;
		}
		
		if($scope.tweet.pattern == $scope.pattern.start)
			$scope.tweet.txt = "#"+$scope.currentProcess;
		else
			$scope.tweet.txt = "%"+ $scope.patternArray[$scope.tweet.pattern] + " #"+$scope.currentProcess;
	};
	
	
	$scope.addTweet = function(){
		var overlays = {};
		var s = 0;
		var size = sizeService.getSize();
		console.log("addTweet: "+ size);
		$scope.overlaysEncode = mapService.getOverlaysEncode();
		$scope.overlaysTemp = mapService.getOverlays();
		
		
		if(size > 0){
			var i = 0;
			
			
			for(i=0;i<size;i++){
				console.log("$scope.overlaysEncode[i]: "+$scope.overlaysEncode[i]);
				if($scope.overlaysEncode[i] != null){					
					overlays[s] = $scope.overlaysEncode[i];
					s++;
					$scope.overlaysTemp[i].setVisible(false);
					mapService.getInfowindows(i).close();
				}else
					console.log("NULL con id "+i);				
			}
		}		
		var overlaysJSON = JSON.stringify(overlays);
		console.log(overlaysJSON);
		if($scope.currentProcess==null || $scope.currentProcess=='')
			$scope.currentProcess = $scope.session;
		$scope.process.preAddTweet($scope.currentProcess, $scope.tweet.txt, $scope.username, overlaysJSON);	
		
		//limpiar dibujos del mapa		
		
		mapService.resetOverlays();
		sizeService.setSize(0);
	};
	
	$scope.cleanOverlaysTemp = function(){
		var size = sizeService.getSize();
		if(size > 0){
			var i = 0;			
			for(i=0;i<size;i++){
				console.log($scope.overlaysEncode[i]);
				if($scope.overlaysEncode[i] != null){	
					$scope.overlaysTemp[i].setVisible(false);
					mapService.getInfowindows(i).close();
					mapService.removeOverlay(i);
				}
			}
		}
		
		
	};
	
	$scope.closeSplit = function(type){
		
		$scope.process.preCloseSplit();
	};
	
	$scope.closeProcess = function(){
		
	};
	
	//------------ Desde Servidor OA a modelos -----------------
	
	$scope.addTweetToModel = function(session, body, createdAt, createdBy){	
		
		var flws = "";
		var index = body.indexOf('^');
		var flow = null;
		if(index > 0){
			var esp = body.substr(index).indexOf(' ');
			if(esp > 0)
				flow=body.substr(index, esp);				
			else
				flow=body.substr(index);
			
			flws = flow;
			//$scope.currentUsers.push({name: data[i], title: data[i], label: data[i]});
			if(buscarValor($scope.flows, flow) == false)
				$scope.flows.push({name: flow, title: flow, label: flow});
			
		}
		var userFlow = (flws != "")?createdBy+" "+flws:createdBy;
		var id = tweetService.add({session: session, body: body, createdAt: createdAt, createdBy: createdBy, flow: flws, userFlow: userFlow});	
		
		return id;
		
		
		
	};
	
	function buscarValor(arr, value){
		var i = 0;		
		for(i=0;i<arr.length;i++){
			if(arr[i].name == value){
				return true;
			}
		}		
		return false;
	}
	
	//-----------------------------------------------------------
	
	//observar el valor de un elemento del modelo	
	/*$scope.$watch(function() {
		  return $scope.tweet.txt;
	}, function(newValue, oldValue) {	
		console.log(newValue);
		if(newValue == '' || newValue == null || newValue=="")
			$scope.setDrawing(false);
		else
			$scope.setDrawing(true);
	}); */
	
	/*$scope.$watch(function() {
		  return $scope.tweet;
	}, function(newValue, oldValue) {	
		console.log('NEWCONTROL'+newValue);		
	}); */
	
	//------------ Comunicacion con BD de cliente ----------------
	

    $scope.setProcessState = function(state){
    	$scope.cambioEstado = false;
    	$.ajax({
			type: "POST",
			url: "changeProcessState",
			async: false, 
			data: {name: $scope.currentProcess, state: state, user: $scope.username},
			success: function(data){
				if(data == false)
					alert('Forbidden flow\'s action!');
				else{			
					$scope.process.state = state;				
					$scope.cambioEstado = data;
					
				}
			},
			 error:function(objXMLHttpRequest){
				 alert('error');
			}
		});
    	
    };
    
    $scope.getProcessState= function(){
    	$.ajax({
			type: "GET",
			url: "getProcessState/"+$scope.currentProcess,
			data: {name: $scope.currentProcess},
			success: function(data){
				
				alert(state);
			},
			 error:function(objXMLHttpRequest){
				 alert('error');
			}
		});
    };
    ////-----------Estado del proceso -------------------------
    
	
	$scope.getUsers = function(){
		$.ajax({
			type: "GET",
			url: "chargeUsers",
			data: {},
			success: function(data){
				console.log(data);
				$scope.users = data;
			},
			 error:function(objXMLHttpRequest){
				 alert('error');
			}
		});
	};	
	
	$scope.getUsersBySession = function(){
		if($scope.currentProcess == null || $scope.currentProcess =='')
			return;
		$.ajax({
			  type: "GET",
			  url: "usersByProcess/"+$scope.currentProcess,
			  data: {process: $scope.currentProcess},
			  success:function(data) {
				  
				  if(data != null){
					  $scope.currentUsers = [];
					  for(var i=0;i<data.length;i++){	
						 console.log(data.length);
						 $scope.currentUsers.push({name: data[i], title: data[i], label: data[i]}); 
					  }						  
				  }				  
		      },
			  error:function(objXMLHttpRequest){alert('error');}
			});	
	};
	
	//actualiza lista de procesos
	$scope.getProcessesByUser = function(){	
		jQuery.ajax({
			type : 'GET',
			data : {
				'name': $scope.username
			},
			success : function(data, textStatus, jqXHR) {
				$scope.processes = data;
				if(data.length == 0){
					$scope.status.isFirstDisabled = false;
					$scope.error['chargeProcess'] = "Yo haven't process right now.";
				}
				else{
					$scope.status.isFirstDisabled = false;
					$scope.error['chargeProcess'] = null;
				}
			},
			//async : async,
			url : "chargeProcess/"+$scope.username,
			error:function(objXMLHttpRequest){
				$scope.error['chargeProcess'] = "Processes are not ready. Please, try recharge tab again.";
			}
		});
	};	
	
	$scope.newSession = function(){		
		console.log($scope.session);
		if($scope.session == null || $scope.session == undefined || $scope.session == "")
			return;
		
		$.ajax({
			type: "POST",
			url: "newProcess",
			data: {user: $scope.username, process: $scope.session},
			
			success:function(data) {
				$scope.process = new Process($scope.session);
				$scope.currentProcess = $scope.process.session;
				var el = document.getElementById('newProcess');
				angular.element(el).trigger('click');
			},
			error:function(objXMLHttpRequest){alert('error');}
		});	        
	};
	
	$scope.addFriend = function(){
		console.log("selectedFriend: ");
		console.log($scope.currentProcess);
		$.ajax({
			type: "POST",
			url: "invite2",
			data: {session: $scope.currentProcess, user: $scope.selectedFriend},			
			success:function(data) {
				$scope.users.push($scope.selectedFriend);
				$scope.addAlert('success', 'Well done!  You successfully added to '+$scope.users[$scope.users.length-1]);
				$scope.$apply(function() {
					$scope.selectedFriend = "";
				});
			},
			error:function(objXMLHttpRequest){
				$scope.addAlert('danger', 'Oh snap! Change a few things up and try submitting again.');
			}
		});	 
	};
	
	//------------------------------------------------------------------
	
	angular.element(document).ready(function () {
		var dData = null;
		if($scope.currentProcess == null || $scope.currentProcess==""){
			ConstantsService.ddData[0].selected = true;
			$scope.tweet.pattern = ConstantsService.ddData[0].value;
			dData = ConstantsService.ddDataNoProcess;
		}else{
			ConstantsService.ddData[4].selected = true;
			$scope.tweet.pattern = ConstantsService.ddData[4].value;
			dData = ConstantsService.ddData;
		}
		$('#myDropdown').ddslick({
		    data: dData,
		    width:40,
		    selectText: "",
		    imagePosition:"left",
		    onSelected: function(selectedData){
		        //callback function: do something with selectedData;
		    	$scope.selectedData = selectedData.selectedData;
		    	$scope.tweet.pattern = $scope.selectedData.value;
		    	console.log($scope.selectedData);
		    	console.log($scope.tweet);
		    	var index = $scope.tweet.txt.indexOf('#');
		    	$scope.$apply(function() {
		    		$scope.tweet.txt = ($scope.tweet.pattern != $scope.pattern.start)?$scope.selectedData.description + " " + (($scope.currentProcess!=null && $scope.currentProcess!="")?"#"+$scope.currentProcess:$scope.tweet.txt.substr(index)):"#";
		    		console.log($scope.tweet.txt);
		    	});
	    			        
		    }   
		});	
		
		
		
		
	});
	
	
		
});


app.service("ConstantsService", function(){
	
	//Dropdown plugin data
	//Object {text: "Start process", value: 4, selected: false, description: "#", imageSrc: "resources/images/patterns/start.png"} 
	this.ddData = [
 	    {
 	        text: "Start process",
 	        value: 4,
 	        selected: false,
 	        description: "#",
 	        imageSrc: "resources/images/patterns/start.png"
 	    },
 	    {
 	        text: "Parallel split",
 	        value: 2,
 	        selected: false,
 	        description: "%2",
 	        imageSrc: "resources/images/patterns/flow-split.png"
 	    },
 	    {
 	        text: "Syncronization a",
 	        value: 3,
 	        selected: false,
 	        description: "%3a",
 	        imageSrc: "resources/images/patterns/flow-merge-a.png"
 	    },
 	    {
 	        text: "Syncronization b",
 	        value: 5,
 	        selected: false,
 	        description: "%3b",
 	        imageSrc: "resources/images/patterns/flow-merge-b.png"
 	    },
 	    {
 	        text: "Sequence",
 	        value: 1,
 	        selected: false,
 	        description: "%1",
 	        imageSrc: "resources/images/patterns/flow-sequence.png"
 	    },
 	    {
 	        text: "Implicit termination",
 	        value: 0,
 	        selected: false,
 	        description: "%11",
 	        imageSrc: "resources/images/patterns/finish.png"
 	    }	    
 	];
	
	this.ddDataNoProcess = [
	        	    {
	        	        text: "Start process",
	        	        value: 4,
	        	        selected: false,
	        	        description: "#",
	        	        imageSrc: "resources/images/patterns/start.png"
	        	    }];
	
	//Object { 'id': n, 'name':user,  'img':'', 'type':'' }
	
	
});

//------------ Utilizados por mentio ---------------------------------------

app.directive('contenteditable', ['$sce', function($sce) {
    return {
        restrict: 'A', // only activate on element attribute
        require: 'ngModel', // get a hold of NgModelController
        link: function(scope, element, attrs, ngModel) {
            function read() {
                var html = element.html();
                console.log("html:");
                console.log(html);
                // When we clear the content editable the browser leaves a <br> behind
                // If strip-br attribute is provided then we strip this out
                if (attrs.stripBr && html === '<br>') {
                    html = '';
                }
                ngModel.$setViewValue(html);
            }

            if(!ngModel) return; // do nothing if no ng-model

            // Specify how UI should be updated
            ngModel.$render = function() {
                if (ngModel.$viewValue !== element.html()) {
                    element.html($sce.getTrustedHtml(ngModel.$viewValue || ''));
                }
            };

            // Listen for change events to enable binding
            element.on('blur keyup change', function() {
                scope.$apply(read);
            });
            read(); // initialize
        }
    };
}]);

app.filter('words', function () {
    return function (input, words) {
        if (isNaN(words)) {
            return input;
        }
        if (words <= 0) {
            return '';
        }
        if (input) {
            var inputWords = input.split(/\s+/);
            if (inputWords.length > words) {
                input = inputWords.slice(0, words).join(' ') + '\u2026';
            }
        }
        return input;
    };
});

//---------------- Fin utilizados por mentio --------------------------------

// --------------- Validacion de texto ingresado  --------------------------

app.directive('tweetFormat', function (){ 
	   return {
		      require: 'ngModel',
		      link: function(scope, elem, attr, ngModel) {		         
		          //For DOM -> model validation
		          ngModel.$parsers.unshift(function(value) {
		        	  var condition = null;
		        	  var pattern = scope.tweet.pattern;
		        	  var processName = scope.currentProcess;
			          var valid = false;
			          
			          if(value == undefined){
			        	  scope.setDrawing(false);
				          ngModel.$setValidity('tweetFormat', false);
			        	  return "";
			          }
			          switch(pattern){
		         			case 1: //sequence
		         				condition = new RegExp("1 #"+processName+" .*"); 
		         				
		         				break;
		         			case 2: //split
		         				condition = new RegExp("2 #"+processName+" .*");
		         				break;
		         			case 3: //join a
		         				condition = new RegExp("3a #"+processName+" .*");;
		         				break; 
		         			case 5: // join b
		         				condition = new RegExp("3b #"+processName+" .*");;
		         				break;
		         			case 4: // start
		         				condition = /#.*/;
		         				
		         				break;
		         			case 0: //finish
		         				condition = new RegExp("11 #"+processName+" *");;
		         				break;
		         		}  
			          console.log("value: "+ value);
			          console.log("value: "+ value.match(condition));
			          if(value.match(condition)){
			        	  valid = true;
			        	  scope.error.tweetFormat = null;
			          }else{
			        	  valid = false;
			        	  var tried = (pattern == scope.pattern.start)?"#process_name some text":"%"+pattern+" #process_name some text or @username ^some_action and more text";
			        	  scope.error.tweetFormat = "Tweet format is wrong! Try write something like "+tried;
			        	 
			        	 
			          }
			          
			          scope.setDrawing(valid);
			           ngModel.$setValidity('tweetFormat', valid);
			           return value;
		          });
		          
		        

		          
		      }
		   };
		});
