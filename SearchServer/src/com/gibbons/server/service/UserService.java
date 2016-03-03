package com.gibbons.server.service;

import com.datastax.driver.core.Row;
import com.gibbons.server.store.UserStore;
import com.gibbons.server.utils.Constants;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.http.HttpEntity;

import javax.annotation.Resource;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import java.io.IOException;
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

		}catch(SQLException | JSONException e){
			e.printStackTrace();
		}
		return null;
	}

	public Response notifyUser(String fromuid, String touid) throws IOException {
		String regID = userStore.getRegID(touid);
		//String regID = uid;
		HttpClient client = HttpClientBuilder.create().build();
		String url = Constants.serverUrl;
		String JSON_STRING = "{ \"data\": {\n" +
				"\"score\": \"5x1\",\n" +
				"\"uid\": \""+fromuid+"\"\n" +
				"},\n" +
				"\"to\" : \""+regID+"\"\n" +
				"}";
		try {
			JSONObject obj = new JSONObject(JSON_STRING);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		StringEntity requestEntity = new StringEntity(
				JSON_STRING,
				"application/json",
				"UTF-8");

		client.execute(RequestBuilder.post(url).setHeader(HttpHeaders.AUTHORIZATION, "key=AIzaSyAjdA8Q5lQzXfWvbtOTl8PF_zE42I8f96E")
				.setHeader(HttpHeaders.CONTENT_TYPE,"application/json").setEntity(requestEntity).build());

		return(Response.ok().build());
	}

	public Response notifyUser(String uid, String lat, String lon) throws IOException {
		String regID = userStore.getRegID(uid);
		//TODO send regID to GCM server
		//String regID = uid;
		HttpClient client = HttpClientBuilder.create().build();
		String url = Constants.serverUrl;
		String JSON_STRING = "{ \"data\": {\n" +
				"\"score\": \"5x1\",\n" +
				"\"uid\": \""+uid+"\"\n" +
				"\"latitude\": \""+lat+"\"\n" +
				"\"longitude\": \""+lon+"\"\n" +
				"},\n" +
				"\"to\" : \""+regID+"\"\n" +
				"}";
		try {
			JSONObject obj = new JSONObject(JSON_STRING);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		StringEntity requestEntity = new StringEntity(
				JSON_STRING,
				"application/json",
				"UTF-8");

		client.execute(RequestBuilder.post(url).setHeader(HttpHeaders.AUTHORIZATION, "key=AIzaSyAjdA8Q5lQzXfWvbtOTl8PF_zE42I8f96E")
				.setHeader(HttpHeaders.CONTENT_TYPE,"application/json").setEntity(requestEntity).build());

		return(Response.ok().build());
	}


	public Response appendRegID(String uid, String regid) {
		//TODO check uid exists
		return(Response.ok(userStore.appendRegID(uid,regid)).build());
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
