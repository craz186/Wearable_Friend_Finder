package com.photofall.rest.service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/user_resource")
public class UserResource {
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String returnTitle(){
		return "<p>User Resource here</p>";
	}
	
	UserService userService = new UserService();
	
	@GET
	@Path("/add/{username}/{mail}/{pass}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addNewUser(@PathParam("username") String username, @PathParam("mail") String mail, @PathParam("pass") String password){
		System.out.println("Drop the bassss");
		return userService.newUser(username, mail, password);
	}
	
	@GET
	@Path("/remove/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeAUser(@PathParam("username") String username){
		System.out.println("Drop the bassss");
		return userService.deleteUser(username);
	}
	
	@GET
	@Path("/changepass/{cpass}/{npass}/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeAUser(@PathParam("cpass") String cpass, @PathParam("npass") String npass, @PathParam("username") String username){
		System.out.println("Drop the bassss");
		return userService.modifyPassword(cpass, npass, username);
	}
	
	@GET
	@Path("/updateuser/{fname}/{lname}/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response changeUser(@PathParam("fname") String fname, @PathParam("lname") String lname, @PathParam("username") String username){
		System.out.println("Drop the bassss");
		return userService.updateUser(fname, lname, username);
	}
	
	@GET
	@Path("/getuser/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getuser(@PathParam("username") String username){
		System.out.println("Drop the bassss");
		return userService.retrieveUser(username);
	}
}
