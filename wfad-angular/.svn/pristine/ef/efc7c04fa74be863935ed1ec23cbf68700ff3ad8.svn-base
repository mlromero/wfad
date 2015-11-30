var init = /#.*/;
var sequence = /1 #.* \@.*/;
var finish_process = /11 #.*/;
var and_split = /2 #.* \^.* \@.*/; // \^.* \@.*/; 
var and_join_a = /3a #.* \^.*/;
var and_join_b = /3b #.* \^.* \@.*/;
		



//Validamos si la estructura corresponde a un tweet segun el patron seleccionado
function checkTweetFormat2(body, patNumber){
	
	if(body != null && body != ""){
		
		
		
		var patAux21 = /2 \^.*/, patAux22 = /2 \^.* \@.*/, patAux23 =  /2 \^.* \@.* \^.*/; 
		var patAux31 = /3 \^.*/;
		
		var lastState = estadoProceso.start;
		var flowFinish = false;
		if(app.currentSession != null && app.currentSession.process != null){
			lastState = app.currentSession.process.getLastState();
			flowsFinish = app.currentSession.process.isFlowsFinish(); 
		}
		
		if(body.indexOf('%')>-1){
			aux = body.substr(1, body.trim().indexOf(' ')).trim();
			pat = (aux == null)?body.substr(1):aux;
		}else if(body.indexOf('#')>-1)
			pat = '4';
		else
			pat = null;
		
		if(pat != patNumber)
			return false;
		
	
		
		if(pat == '1'){			
			if((body.trim().match(sequence) || body.match(finish_flow)) && (lastState == estadoProceso.start || flowsFinish) && lastState != estadoProceso.split){ 				
				console.log("Patron 1 ingresado");				
				return 1;
			}else if(body.match(finish_flow) && (lastState == estadoProceso.sequence || lastState == estadoProceso.split) && !flowsFinish){
				//terminar un flow poner finish : true				
				return 5;
			}else
				return false;						
		}else if(pat == '2'){ 			
			if((body.match(patAux21) || body.match(patAux22) || body.match(patAux23) || body.match(and_split)) && (flowsFinish && lastState != estadoProceso.split)){					
				console.log("Patron 2 ingresado");
				return 2;				
			}else 
				return false;
		}else if(pat == '3'){
			if((body.match(patAux31) || body.match(and_join_a)) && flowsFinish && lastState == estadoProceso.split){				
				console.log("Patron 3 ingresado");
				return 3;
			}else
				return false;
		}else if(pat == '4'){ 
			if(body.match(init) ){				
				return 4;
			}else{				
				return false;
			}
		}else if(pat == '11' && flowsFinish){
			if(body.match(finish_process)){				
				return 11;
			}else{				
				return false;
			}				
		}else{ //error			
			console.log("Error al ingresar patron con valor "+pat);
		}
		
	}	
}




//Validamos si la estructura corresponde a un tweet segun el patron seleccionado
function checkTweetFormat(body, patNumber){
	
	if(body != null && body != ""){		
		if(body.indexOf('%')>-1){
			aux = body.substr(1, body.trim().indexOf(' ')).trim();
			pat = (aux == null)?body.substr(1):aux;
		}else if(body.indexOf('#')>-1)
			pat = '4';
		else
			pat = null;
		
		if(pat != patNumber)
			return false;
		
		switch(app.currentPattern){
		case 1:
			if(body.trim().match(sequence)){
				console.log("Patron 1 ingresado");
				return 1;
			}else
				return false;
			break;
		case 2:
			if(body.match(and_split)){
				console.log("Patron 2 ingresado");
				return 2;
			}else
				return false;
			break;
		case 3:
			if(body.match(and_join_a)){
				console.log("Patron 3a ingresado");
				return 3;
			}else
				return false;
			break;
		case 5:
			if(body.match(and_join_b)){
				console.log("Patron 3b ingresado");
				return 5;
			}else
				return false;
			break;
		case 4:
			if(body.match(init)){
				console.log("Patron 4 ingresado");
				return 4;
			}else
				return false;
			break;
		case 0:
			if(body.match(finish)){
				console.log("Patron 11 ingresado");
				return 0;
			}else
				return false;
			break;			
		}		
	}	
}


function setPlaceHolder(){
	
}