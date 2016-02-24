package com.gibbons.server.service;

import com.datastax.driver.core.Row;
import com.gibbons.server.store.UserStore;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import java.sql.SQLException;

public class UserService {

	UserStore userStore = new UserStore();


	public Response createUser(String name, String password) {
		try{

			String r = userStore.selectUser(name);


			if(r != null) {
				JSONArray arr = new JSONArray(r);
				JSONObject obj = arr.getJSONObject(0);
				if(!(password.equals(obj.getString("password"))))
					return Response.status(Response.Status.CONFLICT).build();
				else
					return Response.ok().build();
			}
			return(Response.ok(userStore.createUser(name, password)).build());

		}catch(SQLException e){
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	public Response getFriends(String uid) {
		return(Response.ok(userStore.getFriends(uid)).build());

	}
	public Response addFriend(String currentId, String username) {
		//TODO Send notification to user
		return(Response.ok(userStore.addFriend(currentId, username)).build());
	}

	public Response deleteUser(String username) {
		return(Response.ok(userStore.removeUser(username)).build());
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
