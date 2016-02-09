package com.photofall.rest.service;


import java.util.*;
import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.photofall.rest.store.*;
import com.photofall.rest.security.*;

/*---------------------------------------*/
//THIS PATH IS USED FOR METHOD TESTING PURPOSES//
/*----------TESTING ENVIRONMENT----------*/

@Path("/v1/test")
public class TestClass {
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String returnTitle(){
		return "<p>Test environment active</p>";
	}
	
	@Path("/addusertest")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String addusertest(){
		UserDataStore uds = new UserDataStore();
		try{
			uds.addUser("woodsa222","wood.zee@mail.dcu.ie","password");
		}catch(SQLException e){
			System.out.println("OH NO!!");
		}
		return "<p>Add user test</p>";
	}
	
	@Path("/removeusertest")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String removeusertest(){
		UserDataStore uds = new UserDataStore();
		try{
			uds.removeUser("woodsa222");
		}catch(SQLException e){
			System.out.println("OH NO!!");
		}
		return "<p>remove user test</p>";
	}
	
	@Path("/changepasswordtest")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String changepass(){
		UserDataStore uds = new UserDataStore();
		try{
			uds.changePassword("password2", "password", "woodsa22");
		}catch(SQLException e){
			System.out.println("OH NO!!");
		}
		return "<p>change password test</p>";
	}
	
	@Path("/updateusertest")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String updateuser(){
		UserDataStore uds = new UserDataStore();
		try{
			uds.updateUserData("Aaron","Woods","woodsa22");
		}catch(SQLException e){
			System.out.println("OH NO!!");
		}
		return "<p>update user test</p>";
	}
	
	/*@Path("/fetchuserdatatest")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String fetchuser(){
		UserDataStore uds = new UserDataStore();
		List<String> userData = new ArrayList<>();
		try{
			//userData = uds.retrieveUserData("woodsa22");
		}catch(SQLException e){
			System.out.println("OH NO!!");
		}
		return "<p>fetch user information test</p> \n"+
			"Username: " +userData.get(0) + "<br>" +
			"First name: " + userData.get(1) + "<br>" +
			"Last name: " + userData.get(2) + "<br>" +
			"Encrypted Password: " + userData.get(3) + "<br>" +
			"Decrypted Password: " + SecurityHandler.decrypt(userData.get(3));
	}*/
	
}
