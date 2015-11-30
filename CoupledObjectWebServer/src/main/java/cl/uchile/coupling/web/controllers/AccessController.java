package cl.uchile.coupling.web.controllers;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cl.uchile.coupling.access.UserData;
import cl.uchile.coupling.web.services.AccessService;
import cl.uchile.coupling.web.services.AccessService.Credentials;

@Controller
@RequestMapping("/session")
public class AccessController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Autowired
	private AccessService accessService;
	@Autowired
	private UserData userData;

	@RequestMapping(value = "join/{ssid}", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
	String joinSession(@RequestParam(required = false) String clientId,
			@PathVariable String ssid,
			@RequestParam(required = true) Boolean useeHTTPSession,
			@RequestParam(required = true) Boolean echo) {
		if (useeHTTPSession) {
			clientId = userData.getSessionClient();
		}
		Credentials c = accessService.joinSession(clientId,echo, ssid);
		if (useeHTTPSession) {
			userData.setSessionClient(c.getClientId());
		}
		return "{\"client_id\":" + c.getClientId() + ", \"session_id\":\""
				+ c.getSessionId() + "\", \"user_id\":"+userData.getUserId()+"}";
	}

	@RequestMapping(value = "leave/{ssid}", method = RequestMethod.POST)
	private @ResponseBody
	void leaveSession(@RequestParam(required = true) String clientId,
			@PathVariable String ssid) {

		accessService.leaveSession(clientId, ssid);
	}

}
