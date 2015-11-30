/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cl.uchile.workflow.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cl.uchile.workflow.persistance.ProcessDAO;
import cl.uchile.workflow.persistance.UsersDAO;
import cl.uchile.workflow.persistance.model.Estado;
import cl.uchile.workflow.persistance.model.Process;
import cl.uchile.workflow.persistance.model.User;


@Controller
public class HomeController {
	@Autowired
	public UsersDAO usersDAO;	
	@Autowired
	public ProcessDAO processDAO;
	
	public enum EstadoProceso{
		START(1), SEQUENCE(2), SPLIT(3), JOIN_A(4), JOIN_B(5), FINISH(6);
		private int id;
		private EstadoProceso(int id){
			this.id = id;
		}
		public int getId(){
			return id;
		}
	}	

	@Transactional
	@RequestMapping(value = { "/", "/login", "/newProcess", "/map", "/invite" }, method = RequestMethod.GET)
	public ModelAndView home(HttpServletRequest request,
			@RequestParam(required = false) String msg, @RequestParam(required = false) String session) {
		String userName = (String) request.getSession().getAttribute("user");		
		HashMap<String, Object> data = new HashMap<String, Object>();
		Process s = null;
		
		if (userName != null) {
			User user = usersDAO.byName(userName);
			if(session != null)
				s = this.usersDAO.byProcessID(session);
			else if(user.getLastProcess() != null){
				s = this.processDAO.getProcessByName(user.getLastProcess());
			}
			if(s != null){	
				data.put("sname", s.getName());
				data.put("users", s.getUsers());
				data.put("session", s.getProcessId());
				data.put("state", s.getEstado());
			}
			
			data.put("name", userName);
			data.put("sessions", user.getProcesses());
			
			data.put("msg", msg);	
			String path = request.getSession().getServletContext().getRealPath("/");
			data.put("webappRoot", path);
			return new ModelAndView("home", data);			
		}else
			data.put("login", true);
		
		return new ModelAndView("login", data);
	}	

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public String goRegister() {
		return "register";
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ModelAndView register(@RequestParam String user,
			@RequestParam String pass) {

		if (this.usersDAO.save(new User(user, pass))) {
			return new ModelAndView("login", "msg",
					"User registered successfully");
		}
		return new ModelAndView("register", "msg", "User already registered");
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ModelAndView login(HttpServletRequest request,
			@RequestParam String user, @RequestParam String pass) {

		if (this.usersDAO.login(user, pass)) {
			request.getSession().setAttribute("user", user);
			User usuario = usersDAO.byName(user);
			
			return this.map(request, usuario.getLastProcess());
		}		
		
		return new ModelAndView("login", "msg",
				"User and/or Password are invalid");
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public ModelAndView logout(HttpServletRequest request) {
		request.getSession().setAttribute("user", null);
		HttpSession session = request.getSession(false);
		if(session!=null)
			session.invalidate();
		return new ModelAndView("login");
		
		
	}
	
	/**
	 * Crea un nuevo proceso
	 * @param user
	 * @param process
	 * @return
	 */
	@RequestMapping(value = "/newProcess", method = RequestMethod.POST)
	public @ResponseBody Boolean newProcess(@RequestParam("user") String user,
			@RequestParam("process") String process){	
		Process p = new Process(process, user);
		Integer id = EstadoProceso.START.getId();
		Estado estado = this.processDAO.getEstadoById(id);
		p.setEstado(estado);
		boolean b = this.usersDAO.save(p, user);
		this.setLastProcess(user, process);
		return b;
	}	
	
	
	@RequestMapping(value = "/invite2", method = RequestMethod.POST)	
	public @ResponseBody String invite2(@RequestParam("session") String session, @RequestParam("user") String user) {
		if (usersDAO.joinUserToProcess(user, session)) {
			return user;
		}
		return null;
	}

	@RequestMapping(value = "/map", method = RequestMethod.POST)
	public ModelAndView map(HttpServletRequest request,
			@RequestParam String session) {
		
		String userName = (String) request.getSession().getAttribute("user");
		
		User user = usersDAO.byName(userName);
		for (Process sessiond : user.getProcesses()) {
			if (sessiond.getProcessId().equals(session)) {
				HashMap<String, Object> data = new HashMap<String, Object>();
				data.put("name", userName);
				data.put("session", session);
				data.put("sname", sessiond.getName());
				data.put("users", sessiond.getUsers());
				if(sessiond.getEstado() != null)
					data.put("state", sessiond.getEstado().getId());
				data.put("sessions", user.getProcesses());
				data.put("webappRoot", request.getSession().getServletContext().getRealPath("/"));
				
				this.setLastProcess(user.getUser(), session);				
				return new ModelAndView("home", data);
			}
		}
		return this.home(request, "Permission denied", null);
	}
	
	@RequestMapping(value = "/changeProcessState", method = RequestMethod.POST)
	public @ResponseBody Boolean changeProcessState(@RequestParam("name") String name, @RequestParam("state") Integer state, @RequestParam("user") String user){
		Process p = processDAO.getProcessByName(name);		
		if((p.getEstado().getId() == EstadoProceso.START.getId() || p.getEstado().getId() == EstadoProceso.SEQUENCE.getId()) &&  
				(state==EstadoProceso.SEQUENCE.getId() || state==EstadoProceso.SPLIT.getId())){
			if(state==EstadoProceso.SEQUENCE.getId())
				return true;
			p.setEstado(this.processDAO.getEstadoById(state));			
		}else if(p.getEstado().getId() == EstadoProceso.SPLIT.getId() && 
				state != EstadoProceso.SEQUENCE.getId() && state != EstadoProceso.START.getId() && state != EstadoProceso.FINISH.getId() && state!=EstadoProceso.SPLIT.getId())
			p.setEstado(this.processDAO.getEstadoById(state));
		else if((p.getEstado().getId() == EstadoProceso.JOIN_A.getId() || p.getEstado().getId() == EstadoProceso.JOIN_B.getId()) && 
				state !=EstadoProceso.START.getId() && state != EstadoProceso.FINISH.getId() && state!=EstadoProceso.JOIN_A.getId() && state!=EstadoProceso.JOIN_B.getId())
			p.setEstado(this.processDAO.getEstadoById(state));
		else if(state == EstadoProceso.FINISH.getId() && user.compareToIgnoreCase(p.getOwner())==0){
			p.setEstado(this.processDAO.getEstadoById(state));
		}else
			return false;		
		
		this.processDAO.update(p);
		return true;
	}
	

	
	
	@RequestMapping(value = "/chargeUsers", method = RequestMethod.GET)
	@ResponseBody
	public List<String> chargeUsers(){	
		List<User> users = this.usersDAO.getAllUsers();	
		List<String> userstring = new ArrayList<String>();
		if(users.size()> 0){
			for(User u:users)
				userstring.add(u.getUser());
			return userstring;
		}else
			return null;
		 	
	}
	
	
	
	
	@RequestMapping(value = "/usersByProcess/{process}", method = RequestMethod.GET)
	@ResponseBody
	public List<String> usersBySession(@RequestParam("process") String process) {	
		Process s = usersDAO.byProcessID(process);	
		List<String> users = new ArrayList<String>();
		if(s.getUsers()!=null && s.getUsers().size() > 0){
			for(User u: s.getUsers())
				users.add(u.getUser());
			return users;
		}else
			return null;
	}	
	
	
	@RequestMapping(value = "/chargeProcess/{name}", method = RequestMethod.GET)	   
	public @ResponseBody List<String> chargeProcess(@PathVariable("name") String name){		
		User user = usersDAO.byName(name);
		List<Process> ss = user.getProcesses();
		List<String> processNames = new ArrayList<String>();
		
		for(Process p : ss){
			processNames.add(p.getProcessId());
		}
		
		
		return processNames;
	}
	
	public void setLastProcess(String user, String lastProcess){
		User usuario = this.usersDAO.byName(user);
		usuario.setLastProcess(lastProcess);
		this.usersDAO.update(usuario);
	}
	
	
	@RequestMapping(value = "/getProcess/{name}", method = RequestMethod.GET)
	public @ResponseBody Process getProcessByName(@PathVariable("name") String name){
		Process p = processDAO.getProcessByName(name);
		return null;
		
		
	}	
	
	
	@RequestMapping(value = "/getProcessState/{name}", method = RequestMethod.GET)
	public @ResponseBody Integer getProcessState(@PathVariable("name") String name){
		Process p = this.getProcessByName(name);
		return p.getEstado().getId();
	}

}
