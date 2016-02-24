package com.gibbons.server.resource;

import com.gibbons.server.service.UserService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/users")
public class UserResource {

	UserService userService = new UserService();
	
	@POST
	@Path("/add/{username}/{pass}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addNewUser(@PathParam("username") String username,  @PathParam("pass") String password){
		return userService.createUser(username, password);
	}


	@GET
	@Path("/addFriend/{uid}/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addFriend(@PathParam("uid")String uid, @PathParam("name")String name) {
		System.out.println("Drop the bassss");
		return userService.addFriend(uid, name);
	}

	@GET
	@Path("/friends/{uid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response returnFriends(@PathParam("uid")String uid) {

		System.out.println("Drop the bassss");
		return userService.getFriends(uid);
	}

	@GET
	@Path("/remove/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeAUser(@PathParam("username") String username){
		return userService.deleteUser(username);
	}
	
	@GET
	@Path("/changepassword/{uid}/{old}/{new}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeAUser(@PathParam("uid") String uid, @PathParam("old") String oldPassword, @PathParam("new") String newPassword){
		return userService.modifyPassword(uid, oldPassword, newPassword);
	}
	
	@GET
	@Path("/updateuser/{fname}/{lname}/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response changeUser(@PathParam("fname") String fname, @PathParam("lname") String lname, @PathParam("username") String username){
		System.out.println("Drop the bassss");
//		return userService.updateUser(fname, lname, username);
		return null;
	}
	
	@GET
	@Path("/getuser/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getuser(@PathParam("username") String username){
		System.out.println("Drop the bassss");
//		return userService.retrieveUser(username);
		return null;
	}
}
