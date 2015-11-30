package cl.uchile.coupling.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cl.uchile.coupling.web.services.AccessService;

@Controller
@RequestMapping("/admin") 
public class AdminController {
	@Autowired
	private AccessService accessService;
	
	@RequestMapping(value = "test", method = RequestMethod.GET)
	public String  test(){
		return "test";
	}
	@RequestMapping(value = "sessions", method = {RequestMethod.GET,RequestMethod.POST})
	public String  sessionsAdmin(Model model){
		model.addAttribute("sessions", accessService.getSessionsList());
		return "sessions";
	}
	@RequestMapping(value = "clear", method = RequestMethod.POST)
	public String  clear(@RequestParam String ssid,Model model){
		accessService.clearSession(ssid);
		model.addAttribute("sessions", accessService.getSessionsList());
		return "redirect:sessions";
	}
}
