package com.gibbons.server.store;

import com.datastax.driver.core.*;
import com.gibbons.server.utils.ToJson;
import com.gibbons.server.security.SecurityHandler;

import org.codehaus.jettison.json.JSONArray;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class UserStore {
	private Cluster cluster;
	private Session session;
	String keyspace = "users";
	String table = "\"UserStore\"";
	
	public UserStore() {
		cluster = Cluster.builder().addContactPoints("127.0.0.1").build();
		cluster.getConfiguration().getSocketOptions().setConnectTimeoutMillis(100000);
		session = cluster.connect(keyspace);
		System.out.println("Cassandra Connection Successful");
	}

	public String removeUser(String name) {
		//This is used for testing purposes as each users list would then have to be updated to remove users that have
		//been deleted.
		PreparedStatement ps = session.prepare("Select * from " + table + " where name ='"+name+"' ALLOW FILTERING;");
		BoundStatement boundStatement = new BoundStatement(ps);
		ResultSet rs = session.execute(boundStatement);
		List<Row> rows = rs.all();
		if(rows.size() == 0) {
			return "No user with name " + name;
		}
		Row currentUser = rows.get(0);
		ps = session.prepare("Delete from " + table + " where name ='"+name+"' and uid ='"+currentUser.getString("uid")+"';");
		boundStatement = new BoundStatement(ps);
		session.execute(boundStatement);
		return "User successfully deleted";
	}

	public String addFriendV2(String currentId, String username) {

		PreparedStatement ps = session.prepare("Select * from " + table + " where uid ='"+currentId+"' ALLOW FILTERING;");
		BoundStatement boundStatement = new BoundStatement(ps);
		ResultSet rs = session.execute(boundStatement);
		List<Row> rows = rs.all();
		if(rows.size() == 0)
			return "Fail no uid exists for "+ currentId;
		//TODO if multiple users need to let user select which one
		Row currentUser = rows.get(0);
		if(currentUser.getString("friends").contains(username))
			return "User already added to friends list";
		ps = session.prepare("Select * from " + table + " where name ='"+username+"' ALLOW FILTERING;");
		boundStatement = new BoundStatement(ps);
		rs = session.execute(boundStatement);
		rows = rs.all();
		if(rows.size() == 0)
			return "Fail no user exists called "+ username;

		Row friendUser = rows.get(0);
		putFriend(currentUser, username);
		putFriend(friendUser, currentUser.getString("name"));
		//rows
		//add friend to users friends list.
		return "Successfully added user to friends list";
	}


	public void putFriend(Row currentUser, String username) {
		PreparedStatement ps = session.prepare("insert into "+table+" (uid,name,friends,password,regid) values(?,?,?,?,?)");
		String friends = currentUser.getString("friends");
		if(friends.equals(""))
			friends = username;
		else
			friends += " " + username;

		BoundStatement boundStatement = new BoundStatement(ps);
		session.execute(boundStatement.bind(currentUser.getString("uid"), currentUser.getString("name"),
				friends, currentUser.getString("password"), currentUser.getString("regid")));
	}

	public String getRegIDFromUsername(String name) {
		PreparedStatement ps = session.prepare("SELECT * FROM "+table+" WHERE name='"+name+"'ALLOW FILTERING;");
		BoundStatement boundStatement = new BoundStatement(ps);
		ResultSet rs =session.execute(boundStatement);
		Row r = rs.one();
		if(r == null)
			return "No user with name: "+ name;
		return(r.getString("regid"));
	}
	public String selectUser(String name) {

		PreparedStatement ps = session.prepare("SELECT * FROM "+table+" WHERE name='"+name+"' ALLOW FILTERING;");
		BoundStatement boundStatement = new BoundStatement(ps);
		ResultSet rs =session.execute(boundStatement);
		Row r = rs.one();
		if(r == null)
			return null;
		LinkedList<Row> rows = new LinkedList<Row>();
		rows.add(r);
		return toJSON(rows);
	}
	public String createUser(String name, String password) throws SQLException {
		PreparedStatement ps = session.prepare(
				"insert into "+table+" (uid,name,friends,password, regid) values(?,?,?,?,?);");
		BoundStatement boundStatement = new BoundStatement(ps);
		String id = String.valueOf(UUID.nameUUIDFromBytes(name.getBytes()));
		session.execute(boundStatement.bind(id,name,"",password, ""));
		return id;
	}

	public String getRegID(String uid) {
		PreparedStatement ps = session.prepare("SELECT * FROM "+table+" WHERE uid='"+uid+"';");
		BoundStatement boundStatement = new BoundStatement(ps);
		ResultSet rs =session.execute(boundStatement);
		Row r = rs.one();
		if(r == null)
			return "No user with id: "+ uid;
		return(r.getString("regid"));
	}

	public String appendRegID(String uid, String regid) {

		PreparedStatement ps = session.prepare("SELECT * FROM "+table+" WHERE uid='"+uid+"';");
		BoundStatement boundStatement = new BoundStatement(ps);
		ResultSet rs =session.execute(boundStatement);
		Row r = rs.one();
		if(r == null)
			return "No user with id: "+ uid;

		ps = session.prepare("insert into "+table+" (uid,name,friends,password,regid) values(?,?,?,?,?);");
		boundStatement = new BoundStatement(ps);
		boundStatement.bind(uid, r.getString("name"),
				r.getString("friends"), r.getString("password"), regid);
		session.execute(boundStatement);
		return("Success");
	}

	public String getFriends(String uid) {
		PreparedStatement ps = session.prepare("SELECT * FROM "+table+" WHERE uid='"+uid+"';");
		BoundStatement boundStatement = new BoundStatement(ps);
		ResultSet rs =session.execute(boundStatement);
		Row r = rs.one();
		if(r == null)
			return "No user with id: "+ uid;

		String[] friends = r.getString("friends").split(" ");
		//Set<String> s = r.getSet("listIDs",String.class);
		LinkedList<Row> rows = new LinkedList<>();
		for(String friend:friends) {
			if(!friend.equals("")) {
				PreparedStatement ps2 = session.prepare("SELECT * FROM " + table + " WHERE name='" + friend + "' ALLOW FILTERING;");
				BoundStatement boundStatement2 = new BoundStatement(ps2);
				ResultSet rs2 = session.execute(boundStatement2);
				rows.add(rs2.one());
			}
		}
		return(toJSON(rows));
	}

	public String changePassword(String uid, String oldPassword, String newPassword) {
		PreparedStatement ps = session.prepare("SELECT * FROM "+table+" WHERE uid='"+uid+"';");
		BoundStatement boundStatement = new BoundStatement(ps);
		ResultSet rs =session.execute(boundStatement);
		Row r = rs.one();
		if(r == null) {
			return "No user with id: " + uid;
		}

		if(r.getString("password").equals(oldPassword)) {
			ps = session.prepare(
					"insert into "+table+" (uid,name,friends,password,regid) values(?,?,?,?,?);");
			boundStatement = new BoundStatement(ps);
			session.execute(boundStatement.bind(r.getString("uid"), r.getString("name"), r.getString("friends"),newPassword,
					r.getString("regid")));
			return "Successfully updated password";
		}
		else {
			return "Incorrect password";
		}
	}

	public String toJSON(ResultSet rs){
	    ToJson converter = new ToJson();
	    JSONArray json = new JSONArray();
	    json = converter.toJSONArray(rs.all());
	    return(json.toString());
	}
	public String toJSON(List<Row> rows) {
		ToJson converter = new ToJson();
		JSONArray json;
		json = converter.toJSONArray(rows);
		return(json.toString());
	}

	public String getUsernameFromUid(String uid) {

		PreparedStatement ps = session.prepare("SELECT * FROM "+table+" WHERE uid='"+uid+"';");
		BoundStatement boundStatement = new BoundStatement(ps);
		ResultSet rs =session.execute(boundStatement);
		Row r = rs.one();
		if(r == null) {
			return "No user with id: " + uid;
		}
		return r.getString("name");
	}

	public Object getUid(String name) {

		PreparedStatement ps = session.prepare("SELECT * FROM "+table+" WHERE name='"+name+"'ALLOW FILTERING;");
		BoundStatement boundStatement = new BoundStatement(ps);
		ResultSet rs =session.execute(boundStatement);
		Row r = rs.one();
		if(r == null) {
			return "No user with name: " + name;
		}
		return r.getString("uid");
	}
}
