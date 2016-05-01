package com.gibbons.server.resource;

import com.gibbons.server.service.UserService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/users")
public class UserResource {

	UserService userService = new UserService();

	@GET
	@Path("/test")
	@Produces(MediaType.APPLICATION_JSON)
	public Response test() {
		return Response.ok("Hi").build();
	}

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


	@POST
	@Path("/notifyFriend/{fromuid}/{toUser}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response notifyFriendRequest(@PathParam("fromuid") String fromuid, @PathParam("toUser") String toUser){
		return userService.notifyFriend(fromuid, toUser);
	}

	@GET
	@Path("/notify/{fromuid}/{touid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response notifyUser(@PathParam("fromuid") String fromuid, @PathParam("touid") String touid){
		return userService.notifyUser(fromuid, touid);
	}

	@GET
	@Path("/notify/{uid}/{lat}/{lon}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response notifyUser(@PathParam("uid") String uid, @PathParam("lat") String lat, @PathParam("lon") String lon){
		return userService.notifyUser(uid, lat, lon);
	}

	@GET
	@Path("/notifyv2/{username}/{touid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response notifyUserv2(@PathParam("username") String fromuid, @PathParam("touid") String username){
		return userService.notifyUserv2(fromuid, username);
	}
	@POST
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

}
