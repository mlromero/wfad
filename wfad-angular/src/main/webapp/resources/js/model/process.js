
	function Process(session, state){
		this.adapter = new ClientAdapter("/CoupledObjectWebServer/");
		this.session = session;
		this.adapter.joinSession(session);
		this.adapter.start(500);
		this.couple();
		this.state = state;
	}
		
	Process.prototype.preAddTweet = function(session, body, createBy, overlays){	
		if(this.session == null)
			return; 
		this.addTweet(this.session, body, this.getTime(), createBy, overlays);
	};
	
	Process.prototype.addTweet = function(session, body, createAt, createBy, overlays){
		//console.log("AcoPLANDO!!!" + session);
		var tweetId = null;
		var scope = angular.element($("#txt")).scope();
		scope.$apply(function(){
	        tweetId = scope.addTweetToModel(session, body, createAt, createBy);
	    });
		
		console.log("addTweetToModel: overlays>  ");
		console.log(overlays);
		
		var scopeMap = angular.element($("#map")).scope();
		scopeMap.$apply(function(){
			scopeMap.addTweetToModel(tweetId, overlays, null, body);
	    });
	};
	
	
	Process.prototype.preCloseSplit = function(){
		
	};
	
	Process.prototype.closeSplit = function(){
		
	};
	
	Process.prototype.preEndProcess = function(){
		
	};
	
	Process.prototype.endProcess = function(){
		
	};
	
	Process.prototype.couple= function(){
		this.adapter.coupleObject("session",this,{
			messageType:"EVENT",
			explicitMapping:["addTweet", "closeSplit", "endProcess"]
		});
	};
	
	
	Process.prototype.getTime = function(){
		var date=new Date();
		return date.getHours()+":"+date.getMinutes()+":"+date.getSeconds()+
		" "+date.getDate()+"/"+(date.getMonth()+1)+"/"+date.getFullYear();	
	};
	
	





