package com.photofall.rest.store;

import com.datastax.driver.core.*;
import com.photofall.rest.security.SecurityHandler;
import com.photofall.rest.utils.ToJson;

import org.codehaus.jettison.json.JSONArray;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDataStore {
	private Cluster cluster;
	private Session session;
	String keyspace = "userdata";
	String table = "UserDataTable";
	
	public UserDataStore() { 
		cluster = Cluster.builder().addContactPoints("127.0.0.1").build();
		cluster.getConfiguration().getSocketOptions().setConnectTimeoutMillis(100000);
		session = cluster.connect(keyspace);
		System.out.println("Cassandra Connection Successful");
	}
	
	public String displayTableData() throws SQLException { //Visualise 
		String query = "SELECT * FROM UserDataTable;";          
	    ResultSet rs =session.execute(query);
	    Row r = rs.one();
	    while(r != null){ //work on this for testing, alternative select * in cqlsh
	    	System.out.println("Listing id's... "+ r.getString("userId"));
	    	r= rs.one();
	    }
	    close();
	    return toJSON(rs);
	}
     
    public String populateData() throws SQLException { //testing purposes, adding users
		
    	System.out.println("Attempting to add user");
	    PreparedStatement ps = session.prepare("INSERT INTO "+table+" (userId) VALUES (?);");
	    BoundStatement boundStatement = new BoundStatement(ps);
	    ResultSet rs =session.execute(boundStatement.bind(2));
	    close();
	    System.out.println("Added user with id 1");
	    return toJSON(rs);
    }
    
    public String addUser(String username, String mail, String password) throws SQLException { //works - tested

    	System.out.println("attempting to check username uniqueness");
    	PreparedStatement ps = session.prepare("SELECT * FROM UserDataTable WHERE username = ?;");
    	BoundStatement bs = new BoundStatement(ps);
    	ResultSet rs = session.execute(bs.bind(username));
    	Row r =rs.one();
    	if(r != null){
    		System.out.println("Found occurence of username");
    		close();
    		return toJSON(rs); //error code
    	}
    	else{ //add user to UserDataTable
    		System.out.println("No occurence of username - Attempting to add new user");
    		String encPass = SecurityHandler.encrypt(password);
    		PreparedStatement ps2 = session.prepare("INSERT INTO "+table+" (username, mail, password) VALUES (?,?,?);");
    		BoundStatement bs2 = new BoundStatement(ps2);
    		ResultSet rs2 =session.execute(bs2.bind(username, mail, encPass));
    		close();
    		System.out.println("Added user with user: "+ username);
    		System.out.println("User's encrypted pass is: "+ encPass);
    		return toJSON(rs2); //success code
    	}
    }
    
    public String removeUser(String username) throws SQLException { //works - tested
    	System.out.println("attempting remove user");
    	PreparedStatement ps = session.prepare("DELETE FROM "+table+" WHERE username = ?;");
    	BoundStatement bs = new BoundStatement(ps);
    	ResultSet rs = session.execute(bs.bind(username));
    	System.out.println(username + " removed");
    	close();
    	return toJSON(rs);
    }
    
    public String changePassword(String currentPass, String newPass, String username) throws SQLException { //works - tested
    	System.out.println("attempting to overwrite password");
    	PreparedStatement ps = session.prepare("SELECT * FROM UserDataTable WHERE username = ?;");
    	BoundStatement bs = new BoundStatement(ps);
    	ResultSet rs = session.execute(bs.bind(username));
    	Row r =rs.one();
    	if(r != null){

    		String storedPass = SecurityHandler.decrypt(r.getString("password"));
    		System.out.println("decrypted pass is: "+ storedPass); 
    		System.out.println("inputted pass is "+ currentPass);
    		
    		if(storedPass.equals(currentPass)){ //passwords matched. Override password
    			String newEncPass = SecurityHandler.encrypt(newPass);
    			System.out.println("Passwords matched. Overriding password.");
    	    	PreparedStatement ps2 = session.prepare("UPDATE "+table+" SET password = ? WHERE username = ?;");
    	    	BoundStatement bs2 = new BoundStatement(ps2);
    	    	ResultSet rs2 = session.execute(bs2.bind(newEncPass, username));
    	    	System.out.println("Password updated");
    	    	close();
    	    	return toJSON(rs2);
    		}
    		else{
    			//passwords didn't match. Do not override stored pass
    			System.out.println("Passwords did not match, aborting password change, try input correct pass");
    		   	close();
    			return toJSON(rs);
    		}
    		
    	}
    	else{
    		close();
    		return toJSON(rs);
    	}
 
    }
    
	
    public String updateUserData(String firstN, String lastN, String username) throws SQLException { //works - tested
    	System.out.println("Updating user information");
    	PreparedStatement ps2 = session.prepare("UPDATE "+table+" SET fname = ?, lname = ? WHERE username = ?;");
    	BoundStatement bs2 = new BoundStatement(ps2);
    	ResultSet rs2 = session.execute(bs2.bind(firstN, lastN, username));
    	System.out.println("update " + username + "'s profile");
    	close();
    	return toJSON(rs2);
    }
    
    //or have methods to update specific fields in UserDataTable
    
    public String retrieveUserData(String username) throws SQLException { //List<String>
    	List<String> userData = new ArrayList<>();
    	PreparedStatement ps = session.prepare("SELECT * FROM UserDataTable WHERE username = ?;");
    	BoundStatement bs = new BoundStatement(ps);
    	ResultSet rs = session.execute(bs.bind(username));
	    Row r = rs.one();
	    if(r != null){ //work on this for testing, alternative select * in cqlsh
	    	userData.add(r.getString("username")); //username
	    	userData.add(r.getString("fname")); //firstName
	    	userData.add(r.getString("lname")); //lastName
	    	userData.add(r.getString("password"));
	    	System.out.println("returned " + username + " details");
	    }
	    else{
	    	System.out.println("No user present with username: "+ username);
	    }
	    close();
	    return toJSON(rs);
	    //return userData;
	    
    }
    
    public void close(){
		session.close();
		cluster.close();
	}
	
	public String toJSON(ResultSet rs){
	    ToJson converter = new ToJson();
	    JSONArray json = new JSONArray();
	    json = converter.toJSONArray(rs.all());
	    return(json.toString());
	}
}
