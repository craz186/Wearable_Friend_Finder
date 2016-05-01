package com.gibbons.server.service;

import com.datastax.driver.core.Row;
import com.gibbons.server.store.UserStore;
import com.gibbons.server.utils.Constants;
import com.sun.jersey.api.core.InjectParam;
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
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

public class UserService {

	public UserStore userStore = new UserStore();


	public Response createUser(String name, String password) {
		try{
			String r = userStore.selectUser(name);

			if(r != null) {
				JSONArray arr = new JSONArray(r);
				JSONObject obj = arr.getJSONObject(0);
				if(!(password.equals(obj.getString("password"))))
					return Response.status(Response.Status.CONFLICT).build();
				else
					return Response.ok(userStore.getUid(name)).build();
			}
			return(Response.ok(userStore.createUser(name, password)).build());

		}catch(SQLException | JSONException e){
			e.printStackTrace();
		}
		return null;
	}

	public Response notifyUser(String fromuid, String touid){
		String regID = userStore.getRegID(touid);
		//String regID = uid;
		HttpClient client = HttpClientBuilder.create().build();
		String url = Constants.serverUrl;
		String JSON_STRING = "{ \"data\": {\n" +
				"\"score\": \"5x1\",\n" +
				"\"uid\": \""+fromuid+"\",\n" +
				"\"request\": \"GPSRequest\"\n" +
				"},\n" +
				"\"to\" : \""+regID+"\"\n" +
				"}";

		StringEntity requestEntity = null;
		try {
			requestEntity = new StringEntity(
                    JSON_STRING,
                    "application/json",
                    "UTF-8");
			client.execute(RequestBuilder.post(url).setHeader(HttpHeaders.AUTHORIZATION, "key=AIzaSyAjdA8Q5lQzXfWvbtOTl8PF_zE42I8f96E")
                    .setHeader(HttpHeaders.CONTENT_TYPE, "application/json").setEntity(requestEntity).build());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return(Response.ok().build());
	}

	public Response notifyUser(String uid, String lat, String lon) {
		String regID = userStore.getRegID(uid);
		//TODO send regID to GCM server
		//String regID = uid;
		HttpClient client = HttpClientBuilder.create().build();
		String url = Constants.serverUrl;
		String JSON_STRING = "{ \"data\": {\n" +
				"\"score\": \"5x1\",\n" +
				"\"uid\": \""+uid+"\",\n" +
				"\"latitude\": \""+lat+"\",\n" +
				"\"longitude\": \""+lon+"\",\n" +
				"\"request\": \"GPSInfo\"\n" +
				"},\n" +
				"\"to\" : \""+regID+"\"\n" +
				"}";
		StringEntity requestEntity = null;
		try {
			requestEntity = new StringEntity(
                    JSON_STRING,
                    "application/json",
                    "UTF-8");
			client.execute(RequestBuilder.post(url).setHeader(HttpHeaders.AUTHORIZATION, "key=AIzaSyAjdA8Q5lQzXfWvbtOTl8PF_zE42I8f96E")
                    .setHeader(HttpHeaders.CONTENT_TYPE,"application/json").setEntity(requestEntity).build());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return(Response.ok().build());
	}

	public Response notifyUserv2(String fromuid, String username) {
		String regID = userStore.getRegIDFromUsername(username);
		String fromUser = userStore.getUsernameFromUid(fromuid);
		//String regID = uid;
		HttpClient client = HttpClientBuilder.create().build();
		String url = Constants.serverUrl;
		String JSON_STRING = "{ \"data\": {\n" +
				"\"score\": \"5x1\",\n" +
				"\"uid\": \""+fromUser+"\",\n" +
				"\"request\": \"GPSRequest\"\n" +
				"},\n" +
				"\"to\" : \""+regID+"\"\n" +
				"}";

		StringEntity requestEntity = null;
		try {
			requestEntity = new StringEntity(
					JSON_STRING,
					"application/json",
					"UTF-8");
			client.execute(RequestBuilder.post(url).setHeader(HttpHeaders.AUTHORIZATION, "key=AIzaSyAjdA8Q5lQzXfWvbtOTl8PF_zE42I8f96E")
					.setHeader(HttpHeaders.CONTENT_TYPE, "application/json").setEntity(requestEntity).build());
		} catch (IOException e) {
			e.printStackTrace();
		}

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
		//TODO check user exists
		return(Response.ok(userStore.addFriendV2(currentId, username)).build());
	}

	public Response notifyFriend(String fromuid, String toUser) {
		String regID = userStore.getRegIDFromUsername(toUser);
		String username = userStore.getUsernameFromUid(fromuid);
		//TODO send regID to GCM server
		//String regID = uid;
		HttpClient client = HttpClientBuilder.create().build();
		String url = Constants.serverUrl;
		String JSON_STRING = "{ \"data\": {\n" +
				"\"score\": \"5x1\",\n" +
				"\"uid\": \""+username+"\",\n" +
				"\"request\": \"FriendRequest\"\n" +
				"},\n" +
				"\"to\" : \""+regID+"\"\n" +
				"}";
		StringEntity requestEntity = null;
		try {
			requestEntity = new StringEntity(
                    JSON_STRING,
                    "application/json",
                    "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		try {
			client.execute(RequestBuilder.post(url).setHeader(HttpHeaders.AUTHORIZATION, "key=AIzaSyAjdA8Q5lQzXfWvbtOTl8PF_zE42I8f96E")
                    .setHeader(HttpHeaders.CONTENT_TYPE,"application/json").setEntity(requestEntity).build());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return(Response.ok().build());
	}
}
