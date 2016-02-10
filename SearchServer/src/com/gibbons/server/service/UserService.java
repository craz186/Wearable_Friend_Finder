package com.gibbons.server.service;

import com.gibbons.server.store.UserStore;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import java.sql.SQLException;

public class UserService {

	UserStore userStore = new UserStore();


	public Response createUser(String name, String password) {
		try{
			return(Response.ok(userStore.createUser(name, password)).build());

		}catch(SQLException e){
			e.printStackTrace();
		}
		return null;
	}
	public Response getFriends(String uid) {
		return(Response.ok(userStore.getFriends(uid)).build());

	}
	public Response addFriend(String currentId, String username) {
		System.out.println("println");
		return(Response.ok(userStore.addFriend(currentId, username)).build());
	}

	public Response deleteUser(String username) throws NoSuchMethodException {
		System.out.println("UserService removing user");
//		try{
//			return(Response.ok(userStore.removeUser(username)).build());
//		}catch(SQLException e){
//			e.printStackTrace();
//		}
		throw new NoSuchMethodException();
	}
	public Response modifyPassword(String uid, String oldPassword, String newPassword) {
		return(Response.ok(userStore.changePassword(uid, oldPassword, newPassword)).build());
	}
	public Response updateUser(String firstN, String lastN, String username) throws NoSuchMethodException{
		System.out.println("UserService updating user");
//		try{
//			return(Response.ok(userStore.updateUserData(firstN, lastN, username)).build());
//		}catch(SQLException e){
//			e.printStackTrace();
//		}
		throw new NoSuchMethodException();
	}
	public Response retrieveUser(String username) throws NoSuchMethodException{
		System.out.println("UserService fetching user data");
			//return(Response.ok(userStore.retrieveUserData(username)).build());
		throw new NoSuchMethodException();

	}
}
