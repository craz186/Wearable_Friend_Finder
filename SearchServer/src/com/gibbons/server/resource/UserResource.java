package com.gibbons.server.resource;

import com.gibbons.server.service.UserService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/users")
public class UserResource {

	UserService userService = new UserService();
	
	@POST
	@Path("/add/{username}/{pass}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addNewUser(@PathParam("username") String username,  @PathParam("pass") String password){
		return userService.createUser(username, password);
	}

	@PUT
	@Path("/add/{uid}/{regid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response appendRegID(@PathParam("uid") String uid,  @PathParam("regid") String regid){
		return userService.appendRegID(uid, regid);
	}

	@GET
	@Path("/notify/{fromuid}/{touid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response notifyUser(@PathParam("fromuid") String fromuid, @PathParam("touid") String touid){
		try {
			return userService.notifyUser(fromuid, touid);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@GET
	@Path("/notify/{uid}/{lat}/{lon}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response notifyUser(@PathParam("uid") String uid, @PathParam("lat") String lat, @PathParam("lon") String lon){
		try {
			return userService.notifyUser(uid, lat, lon);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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
