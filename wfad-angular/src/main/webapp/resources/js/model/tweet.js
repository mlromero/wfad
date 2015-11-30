app.factory('tweetService', function(){
	
	var tweetService = {};
	var tweets = []; //{session: session, body: body, createdAt: createdAt, createdBy: createdBy, flow: flws, userFlow: userFlow}
	
	tweetService.add = function(object){
		object['id'] = tweets.length;
		tweets.push(object);
		return (tweets.length-1);
	};
	
	tweetService.all = function(){
		return tweets;
	};
	
	return tweetService;
	
});


app.controller('tweetController', function($scope, tweetService, mapService){
	$scope.tweets = tweetService.all();	
	
	$scope.colors = ["green", "blue", "pink", "red", "yellow", "orange", "purple"];
	$scope.cBusy = {green:null, blue:null, pink:null, red:null, yellow:null, orange:null, purple:null};
	$scope.cCode = {green:"#00FF00", blue:"#0000FF", pink:"#E875E6", red:"#FF0000", yellow:"#FFFF00", orange:"#FBA106", purple:"#B206FB"};
	$scope.error = null;
	
	$scope.f;
	
	$scope.colorClass = {};	
	
	$scope.showOverlays = function(id){
		var overlays = mapService.getTweet(id);
		console.log(id);
		
		var i =0, color=null;
		for(i=0;i<$scope.colors.length && overlays.length > 0 ;i++){
			console.log($scope.colors[i]);
			if($scope.cBusy[$scope.colors[i]] == null && overlays[0].getVisible()==false){
				color = $scope.colors[i];				
				break;
			}else if($scope.cBusy[$scope.colors[i]]==id && overlays[0].getVisible()==true){
				color = $scope.colors[i];
				break;
			}
		}		
		console.log($scope.cBusy);
		console.log(color);
		if(color != null){
			
			
		}
		
		for(i=0;i<overlays.length && color!=null;i++){
			if(overlays[i].getVisible()){
				overlays[i].setVisible(false);
				$scope.cBusy[color] = null;	
				$scope.colorClass[id] = "";
			}else{					
				if(color == null){
					$scope.error="No es posible mostrar mas geolocalizaciones";
					return;
				}
				$scope.cBusy[color] = id;
				$scope.colorClass[id] = "efecto-borde-"+color;
				console.log(overlays);
				if(overlays[i] instanceof google.maps.Marker){
					overlays[i].icon = 'http://maps.google.com/intl/en_us/mapfiles/ms/micons/'+ color +'-dot.png';
				}else{
					overlays[i].strokeColor = $scope.cCode[color];
				}					
				overlays[i].setVisible(true);
			}
		}
	};
	
	
	
});

