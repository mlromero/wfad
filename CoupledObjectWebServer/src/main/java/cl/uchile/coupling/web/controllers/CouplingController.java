package cl.uchile.coupling.web.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cl.uchile.coupling.web.services.CouplingService;

@Controller
@RequestMapping("/coupling")
public class CouplingController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory
			.getLogger(CouplingController.class);
	@Autowired
	private CouplingService couplingService;

	private long id = 1;

	@RequestMapping(value = "couple/{ssid}", produces = "application/json", method = RequestMethod.POST)
	public @ResponseBody
	void couple(@RequestParam(required = false) String initState,
			@PathVariable String ssid, @RequestParam String objectId,@RequestParam String clientId) {
		couplingService.couple(clientId,ssid, objectId, initState);

	}

	@RequestMapping(value = "decouple/{ssid}", produces = "application/json", method = RequestMethod.POST)
	public @ResponseBody
	void decouple(@PathVariable String ssid, @RequestParam String objectId,@RequestParam String clientId) {
		couplingService.decouple(clientId,ssid, objectId);
	}

	@RequestMapping(value = "{ssid}", method = RequestMethod.POST)
	public @ResponseBody
	void write(HttpServletRequest request, HttpServletResponse response,
			@PathVariable String ssid,
			@RequestParam(required = false) String objectId,@RequestParam String clientId) {
		couplingService.write(clientId,ssid, objectId, request.getParameter("message").toString());

	}

	@RequestMapping(value = "{ssid}/{clientId}", method = RequestMethod.GET)
	public @ResponseBody
	void read(HttpServletRequest request, HttpServletResponse response,
			@PathVariable String ssid,@PathVariable String clientId) throws IOException {

		
			response.addHeader("Content-Type",
					"text/event-stream; charset=utf-8");
			response.addHeader("Cache-Control", "no-cache");
			PrintWriter writer = response.getWriter();
			writer.println("retry: 500");
			writer.println("id: " + id++);
			writer.print("data: ");
			writer.print(couplingService.read(clientId,ssid));
			writer.print("\n\n");
			writer.flush();
	

	}
	
}
