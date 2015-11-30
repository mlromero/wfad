/*
 * Clases cliente objetos acolado para HTML5 
 * version 0.8
 */

function ClientAdapter(serverUrl, useeHTTPSession, echo) {
	this.serverUrl = serverUrl;
	this.handlers = {};
	this.sessionId = 0;
	this.clientId = 0;
	this.userId = 0;
	this.useeHTTPSession = useeHTTPSession || false;
	this.echo = echo || false;

};

jQuery.extend(ClientAdapter.prototype, {
	joinSession : function(ssid) {
		this.decoupleAll();
		var data = {
			op : "join",
			useeHTTPSession : this.useeHTTPSession,
			echo : this.echo
		};
		if (ssid) {
			data['ssid'] = ssid;
		}
		if (this.clientId) {
			data['clientId'] = this.clientId;
		}
		var _this = this;
		jQuery.ajax({
			async : false,
			type : 'POST',
			data : data,
			url : this.serverUrl + "session/join/" + ssid,
			success : function(data, textStatus, jqXHR) {
				_this.sessionId = data.session_id;
				_this.clientId = data.client_id;
				_this.userId = data.user_id;
			}
		});
	},
	leaveSession : function() {
		this.stop();
		var data = {
			op : "leave",
			clientId : this.clientId
		};
		var _this = this;
		jQuery.ajax({
			type : 'POST',
			data : data,
			url : this.serverUrl + "session/leave/" + _this.sessionId,
			dataType : 'json',
			success : function(data, textStatus, jqXHR) {
				_this.sessionId = 0;

			}
		});
	},
	couple : function(handler, options) {
		options = jQuery.extend({
			initState : options.getElementState || false,
			callback : function() {
			},
			cleanup : function() {
			},
		}, options);
		if (!this.handlers[handler.id]) {
			this.handlers[handler.id] = [];
		}
		if (typeof options.initState === 'function') {
			options.initState = options.initState.apply(handler.target, []);
		}
		this.handlers[handler.id].push(handler);
		handler.setAdapter(this);
		var _this = this;
		console.log("SENT couple to id " + handler.id);
		options.cleanup.apply(handler.target, []);
		var data = {
			'objectId' : handler.id,
			'clientId' : this.clientId
		};
		if (options.initState) {
			data['initState'] = $.toJSON(this._getStateMessage(handler.id,
					"init", options.initState));
		}
		jQuery.ajax({
			type : 'POST',
			data : data,
			url : this.serverUrl + "coupling/couple/" + this.sessionId,
			success : function() {
				options.callback.apply(_this, arguments);
			}
		});
	},
	decouple : function(id) {
		var toDetach = this.handlers[id];
		this.handlers[id] = [];
		jQuery.each(toDetach, function(index, handler) {
			handler._detach();
		});
		console.log("SENT decouple to id " + id);
		jQuery.ajax({
			type : 'POST',
			data : {
				'objectId' : id,
				'clientId' : this.clientId
			},
			url : this.serverUrl + "coupling/decouple/" + this.sessionId
		});
	},
	decoupleAll : function() {
		var context = this;
		jQuery.each(this.handlers, function(key, handlers) {
			context.decouple(key);
		});
	},
	coupleObject : function(id, target, options) {
		this.couple(new ObjectHandler(id, target, options), options);
	},
	coupleBinding : function(id, target, options) {
		this.couple(new BindingHandler(id, target, options), options);
	},
	sendToServer : function(type, async, id, value, callback) {
		if (type == 'STATE') {
			// Los estados no se validan por lo que no tiene sentido que sean
			// asyncronos.
			if (callback)
				callback();
			var state = value.handler.getElementState.apply(
					value.handler.target, value.args);
			value['state'] = state;
			return this._sendState(id, value);
		}
		if (type == 'EVENT') {
			return this._sendEvent.apply(this, Array.prototype.slice.call(
					arguments, 1));
		}
	},
	sendToClient : function(message) {
		console.log("Mensaje recibido");
		console.log(message);
		if (this.handlers[message.objectId]) {
			jQuery.each(this.handlers[message.objectId], function(index,
					handler) {
				if (handler.canHandleMessage(message)) {
					handler.handleMessage(message);
					return false;
				}
			});

		}
	},
	_sendEvent : function(async, id, name, callback) {

		var otherArgs = Array.prototype.slice.call(arguments, 4);
		// Si es asyncrona, es probable que retorne antes de que termine la
		// llamada, por lo que debe retornar true. Si no solo retorna true si la
		// llamada retorno con exito
		var ret = async;
		var event = this._getEventMessage(id, name, otherArgs);
		var json = $.toJSON(event);
		console.log("SENT: " + json);
		jQuery.ajax({
			type : 'POST',
			data : {
				'message' : json,
				'objectId' : id,
				'clientId' : this.clientId
			},
			success : function(data, textStatus, jqXHR) {
				if (callback) {
					callback();
				}
				ret = true;
			},
			async : async,
			url : this.serverUrl + "coupling/" + this.sessionId
		});
		return ret;
	},
	_getEventMessage : function(id, name, args) {
		return {
			"messageType" : "EVENT",
			"objectId" : id,
			"messageName" : name,
			"arguments" : args
		};
	},
	_sendState : function(id, value) {

		var event = this._getStateMessage(id, value.name, value.state);
		var json = $.toJSON(event);
		console.log("SENT: " + json);
		jQuery.ajax({
			type : 'POST',
			data : {
				'message' : json,
				'clientId' : this.clientId
			},
			url : this.serverUrl + "coupling/" + this.sessionId
		});
		return true;
	},
	_getStateMessage : function(id, name, state) {
		return {
			"messageType" : "STATE",
			"objectId" : id,
			"messageName" : name,
			"objectState" : state
		};

	},
	start : function(delay) {
		var context = this;
		if (!delay) {
			if (this.sessionId) {
				this.source = new EventSource(this.serverUrl + "coupling/"
						+ this.sessionId + "/" + this.clientId);
				this.source.addEventListener('message', function(e) {
					if (jQuery.trim(e.data) != "") {
						var messages = JSON.parse(e.data);
						jQuery.each(messages.messages,
								function(index, message) {
									context.sendToClient(message);

								});
					}
				}, false);
			} else {
				setTimeout(function() {
					context.start.apply(context, [ false ]);
				}, 5000);
			}
		} else {
			setTimeout(function() {
				context.start.apply(context, [ false ]);
			}, delay);
		}
	},
	stop : function() {
		if (this.source)
			this.source.close();
	}
});
// jQuery.adapter = new ClientAdapter(_serverURL);
function Handler(id, target, options) {
	options = options || {};
	this.id = id;
	this.target = target;
	this.explicitMapping = options.explicitMapping || [];
	this.mappingExeptions = options.mappingExeptions || [];
	this.messageType = options.messageType || 'EVENT';
	this._adapter = null;
	this.getElementState = options.getElementState || function() {
		console.log(this);
		if (this.getState && typeof this.getState === 'function') {
			return this.getState.apply(this, arguments);
		}
		return this;
	};
	this.setElementState = options.setElementState || function(state) {
		if (this.setState && typeof this.setState === 'function') {
			this.setState.apply(this, arguments);
		}
		jQuery.extend(this, state);
	};
	this._atach();

};

jQuery.extend(Handler.prototype, {
	setAdapter : function(adapter) {
		this._adapter = adapter;
	},
	handleMessage : function(message) {
	},
	canHandleMessage : function(message) {

	},
	_atach : function() {
	},
	_detach : function() {
	},
	_sendMessage : function(functionName, callback, async, args) {
		if (this._adapter == null)
			return;
		if (this.messageType == 'EVENT') {
			var newArgs = [ this.messageType, async, this.id, functionName,
					callback ];
			jQuery.merge(newArgs, args);
			var ret = this._adapter.sendToServer.apply(this._adapter, newArgs);
			return ret;
		} else if (this.messageType == 'STATE') {
			var newArgs = [ functionName ];
			jQuery.merge(newArgs, args);

			return this._adapter.sendToServer(this.messageType, async, this.id,
					{
						name : functionName,
						handler : this,
						args : args
					}, callback);
		}

	}

});
function ObjectHandler(id, target, options) {
	this.super = Handler;
	this.originalFunctions = {};
	this.super(id, target, options);

}
jQuery.extend(ObjectHandler.prototype, Handler.prototype);
jQuery
		.extend(
				ObjectHandler.prototype,
				{
					canHandleMessage : function(message) {
						var func = this.originalFunctions[message.messageName];
						if (func) {
							return true;
						}
					},
					_createUnatachedObject : function() {
						var unatached = $.extend(false, {}, this.target);
						unatached = $.extend(unatached, this.originalFunctions);
						return unatached;
					},
					_mergeUnatachedObject : function(unatached) {
						jQuery.each(unatached, function(name, func) {
							if (typeof func === 'function') {
								delete unatached[name];
							}
						});
						console.log(unatached);
						this.target = $.extend(this.target, unatached);

					},
					handleMessage : function(message) {
						if (message.messageType == "STATE") {
							var copy = this._createUnatachedObject();

							this.setElementState.apply(copy,
									[ message.objectState ]);

							this._mergeUnatachedObject(copy);
							return true;
						}
						var func = this.originalFunctions[message.messageName];
						if (func) {
							func.apply(this._createUnatachedObject(),
									message.arguments);
							return true;
						}
						return false;
					},
					_atach : function() {
						var context = this;
						if (this.explicitMapping
								&& this.explicitMapping.length > 0
								&& typeof this.explicitMapping === 'object') {

							jQuery.each(this.explicitMapping, function(index,
									value) {
								context._atachSyncronizedFunction(value);
							});
						} else {
							jQuery
									.each(
											this.target,
											function(name, func) {
												if (typeof func === "function") {
													var omit = false;
													jQuery
															.each(
																	context.mappingExeptions,
																	function(
																			index,
																			regexp) {
																		if (name
																				.match(regexp)) {
																			omit = true;
																			return false;
																		}
																	});
													if (!omit) {
														context
																._atachSyncronizedFunction(name);
													}
												}

											});
						}
					},
					_detach : function() {
						var context = this;
						if (this.explicitMapping
								&& this.explicitMapping.length > 0
								&& typeof this.explicitMapping === 'object') {

							jQuery
									.each(
											this.explicitMapping,
											function(index, value) {
												context.target[value] = context.originalFunctions[value];
											});
						} else {
							jQuery
									.each(
											this.target,
											function(name, func) {
												if (typeof func === "function") {
													var omit = false;
													jQuery
															.each(
																	context.mappingExeptions,
																	function(
																			index,
																			regexp) {
																		if (name
																				.match(regexp)) {
																			omit = true;
																			return false;
																		}
																	});
													if (!omit) {
														context.target[value] = context.originalFunctions[value];
													}
												}

											});
						}
					},
					_atachSyncronizedFunction : function(functionName) {
						var context = this;

						if (this.target[functionName]
								&& typeof this.target[functionName] === "function") {
							this.originalFunctions[functionName] = this.target[functionName];
							this.target[functionName] = function() {

								var args = arguments;
								var callback = function() {
									var unatached = context
											._createUnatachedObject();
									unatached[functionName].apply(unatached,
											args);
									context._mergeUnatachedObject(unatached);
								};

								context._sendMessage(functionName, callback,
										true, arguments);
							};
						}
					}
				});

function BindingHandler(id, target, options) {
	this.super = Handler;
	this.syncFunction = {};
	this.generator = 0;
	this.bindings = options.bindings || [];
	this.validate = options.validate || false;
	this.super(id, target, options);

}
jQuery.extend(BindingHandler.prototype, Handler.prototype);
jQuery.extend(BindingHandler.prototype, {
	canHandleMessage : function(message) {
		return (jQuery.inArray(message.messageName, this.bindings) >= 0);
	},
	handleMessage : function(message) {

		if (this.canHandleMessage(message)) {
			if (message.messageType == "STATE") {
				this.setElementState
						.apply(this.target, [ message.objectState ]);
				return true;
			}
			var events = $(this.target).data('events')[message.messageName];
			var eventGeneratedId = this.generator++;
			var target = this.target;
			jQuery.each(events, function(index, handler) {
				if (handler.namespace != 'attached') {
					var newFunc = function(evt) {

						handler.handler.apply(evt.target, Array.prototype.slice
								.call(arguments, 1));

					};
					$(target).bind(
							'tmp' + message.messageName + eventGeneratedId
									+ ".attached", newFunc);
				}
			});
			$(this.target).trigger(
					'tmp' + message.messageName + eventGeneratedId
							+ ".attached", message.arguments);

			$(this.target).unbind(
					'tmp' + message.messageName + eventGeneratedId
							+ ".attached");
			return true;
		}
		return false;
	},
	_atach : function() {
		for (var i = 0; i < this.bindings.length; ++i) {
			var bindName = this.bindings[i];
			if (this.messageType == 'STATE'
					|| $(this.target).data('events') == undefined) {
				$(this.target).unbind(bindName + ".attached");
				$(this.target).bind(bindName + ".attached",
						this._createSyncFunction(bindName));
			} else {
				this._bindAtFirst(bindName);
			}

		}

	},
	_detach : function() {
		$(this.target).unbind(".attached");

	},
	_bindAtFirst : function(bindName) {
		var context = this;
		var events = $(this.target).data('events')[bindName];
		var tempEvents = [];
		jQuery.each(events, function(index, event) {
			tempEvents.push({
				handler : event.handler,
				namespace : event.namespace
			});
		});
		$(this.target).unbind(bindName + ".*");
		$(this.target).unbind(bindName);
		$(this.target).bind(bindName + ".attached",
				this._createSyncFunction(bindName));

		jQuery.each(tempEvents, function(index, event) {
			if (event.namespace)
				$(context.target).bind(bindName + "." + event.namespace,
						event.handler);
			else
				$(context.target).bind(bindName, event.handler);

		});

	},
	_createSyncFunction : function(bindName) {
		var context = this;
		return function(evt) {
			// Evita referencias ciclicas
			var cpy = $.extend({}, evt);
			cpy.view = null; // Firefox y Chrome
			cpy.target = null; // Chrome
			cpy.currentTarget = null; // Chrome
			cpy.delegateTarget = null; // Chrome
			cpy.originalEvent = null; // Chrome
			cpy.srcElement = null; // Chrome
			cpy.toElement = null; // Chrome
			return context._sendMessage(bindName, null, false, [ cpy ]);
		};
	}
});
function MultiHandler(objectHandler, bindingHandler) {
	this.objectHandler = objectHandler;
	this.bindingHandler = bindingHandler;

}
jQuery.extend(MultiHandler.prototype, {
	canHandleMessage : function(message) {
		return objectHandler.canHandleMessage(message)
				|| bindingHandler.canHandleMessage(message);
	},
	handleMessage : function(message) {
		if (message.messageType == "STATE"
				|| objectHandler.canHandleMessage(message)) {
			return objectHandler.handleMessage(message);
		}
		return bindingHandler.handleMessage(message);
	}
});

/*
 * function PluginHandler(plugin, id, target, options) { this.super = Handler;
 * this.plugin = plugin; this.originalPlugin = jQuery.fn[plugin]; this.super(id,
 * target, options); } jQuery.extend(PluginHandler.prototype, { handleMessage :
 * function(event) { if (!this._isMethodHandled(event.messageName)) { return
 * false; } var args = [ event.messageName ]; jQuery.merge(args,
 * event.arguments); this.originalPlugin.apply(this.target, args); return true; },
 * _isMethodHandled:function(name){ if (this.explicitMapping.length>0 ) { return
 * jQuery.inArray(name, this.explicitMapping) >= 0; } for(var i=0;i<this.mappingExeptions.length;++i){
 * var regexp=this.mappingExeptions[i]; if (name.match(regexp)) { return false; } }
 * return true; }, _atach : function() { var context = this;
 * jQuery.fn[this.plugin] = function(method) { var retVal=
 * context.originalPlugin.apply(context.target, arguments); if (typeof options ==
 * 'string' && this==context.target) { if(context._isMethodHandled(method)){
 * context._sendEvent(method,Array.prototype.slice.call(arguments, 1)); } }
 * return retVal; }; }, _sendEvent : function(eventName,args) {
 * this._sendMessage(eventName,args); } });
 */