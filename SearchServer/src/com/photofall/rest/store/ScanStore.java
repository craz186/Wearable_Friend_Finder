package com.photofall.rest.store;

import com.datastax.driver.core.*;
import com.datastax.driver.core.utils.UUIDs;
import com.photofall.rest.model.Cache;
import com.photofall.rest.utils.EANScraper;
import com.photofall.rest.utils.ToJson;
import org.codehaus.jettison.json.JSONArray;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.*;

public class ScanStore
{
	private Cluster cluster;
	private Session session;

	String listStore="\"ListStore\"";
	String scanStore="\"ScanStore\"";
	String customerStore= "\"CustomerStore\"";
	
	public ScanStore() {
		cluster = Cluster.builder().addContactPoints("127.0.0.1").build();
		cluster.getConfiguration().getSocketOptions().setConnectTimeoutMillis(100000);
		session = cluster.connect();
		session.execute("USE \"shopScan\";"); //Have Constant here later
		System.out.println("ScanStore startup success");
	}

	public String add(String name, String price, String store) {
		PreparedStatement ps = session.prepare("insert into " + scanStore + " (barcode, descript1, descript2, descript3," +
				" item, price, store) values (?,?,?,?,?,?,?);");
		BoundStatement boundStatement = new BoundStatement(ps);
		session.execute(boundStatement.bind(0, "placeholder1", "placeholder2", "placeholder3", name, Double.parseDouble(price.substring(1)), store));
		return "Success";
	}
    public String addScan(String listID, String barcode) throws SQLException {

		//CHECK IF ITEM EXISTS
		String t = "SELECT * FROM "+scanStore+" WHERE barcode='"+barcode+"' ALLOW FILTERING";
		ResultSet rs =session.execute(t);
		if(rs.all().size()==0) {
			//IF NOT ADD TO SCANSTORE
			PreparedStatement ps = session.prepare("insert into " + scanStore + " (barcode, descript1, descript2, descript3," +
					" item, price, store) values (?,?,?,?,?,?,?);");
			BoundStatement boundStatement = new BoundStatement(ps);
			//GET NAME

			//GET PRICE

			session.execute(boundStatement.bind(barcode, "placeholder1", "placeholder2", "placeholder3", "item"+barcode, 0.11, "store"));
		}
		String name = "book 1";
		String[] words = name.split(" ");
		//AFTER ADD ITEM TO LIST
		t = "SELECT * FROM "+scanStore+" WHERE item CONTAINS '"+words[0]+"'";
		rs =session.execute(t);
		long currentPrice = 0;
		Row cheapest = null;
		for(Row r:rs) {
			if(!r.getString("item").equals(name)) {
				if(r.getLong("price")<currentPrice) {
					cheapest = r;
					currentPrice = r.getLong("price");
				}
			}
		}
		t = "SELECT * FROM "+listStore+" WHERE listID='"+listID+"'";
		rs =session.execute(t);
		String currentBarcode = "";
		for(Row r:rs.all()) {
			ColumnDefinitions rsmd = r.getColumnDefinitions();

			for(int i=0; i<rsmd.size(); i++) {
				String column_name = rsmd.getName(i);
				if(column_name.equals("barcodes")) {
					currentBarcode = r.getString(column_name);
				}
			}
		}
		PreparedStatement ps = session.prepare("insert into " + listStore + " (listID, barcodes, listNum, priceComp, shop" +
				") values (?,?,?,?,?);");
		BoundStatement boundStatement = new BoundStatement(ps);
		session.execute(boundStatement.bind(listID, currentBarcode+ " " +barcode, (long) 0, "", "Tescos"));
		LinkedList<Row> cheapestItem = new LinkedList<>();
		cheapestItem.add(cheapest);
	    return toJSON(new LinkedList<>(cheapestItem));
    }

	public String createList(String uid) throws SQLException{

		PreparedStatement ps2 = session.prepare("SELECT * FROM "+customerStore+" WHERE id=?;");
		BoundStatement boundStatement2 = new BoundStatement(ps2);
		ResultSet rs2 =session.execute(boundStatement2.bind(uid));

		Row r = rs2.one();
		long numLists = r.getLong("numLists");

		//GENERATE NEW LIST ID
		String listId = uid + numLists;
		//String t = "SELECT * FROM "+listStore+" WHERE userid='"+uid+"'";
		Set<String> listIDs = r.getSet("listIDs", String.class);

		if(listIDs.size() == 0)
			listIDs = new HashSet<>();

		listIDs.add(listId);

		//UPDATE PROFILE WITH NEW LIST ID
		PreparedStatement ps3 = session.prepare("insert into "+customerStore+" (name, id, password, listIDs, numLists" +
				") values (?,?,?,?,?);");
		BoundStatement boundStatement3 = new BoundStatement(ps3);
		ResultSet rs3 =session.execute(boundStatement3.bind(r.getString("name"),uid,r.getString("password"),listIDs,numLists+1));


		//ADD LIST TO LISTSTORE
		PreparedStatement ps = session.prepare("insert into "+listStore+" (listID, barcodes, listNum, priceComp, shop" +
				") values (?,?,?,?,?);");
		BoundStatement boundStatement = new BoundStatement(ps);

		ResultSet rs =session.execute(boundStatement.bind(listId, "", (long) 0, "", ""));
		return listId;
	}
    public String deleteList(String listId) {
        PreparedStatement ps = session.prepare("delete from "+listStore+" where cacheid=? and userid=?;");
        BoundStatement boundStatement = new BoundStatement(ps);
        //ResultSet rs =session.execute(boundStatement.bind(cacheId, userId));
        return "Success ";
    }
	public String createUser(String name, String password) throws SQLException {
		PreparedStatement ps = session.prepare(
				"insert into "+customerStore+" (name,id,password,listIDs,numLists) values(?,?,?,?,?);");
	    BoundStatement boundStatement = new BoundStatement(ps);
		String id = String.valueOf(UUID.nameUUIDFromBytes(name.getBytes()));
		session.execute(boundStatement.bind(name,id,password,null,(long)0));
		return id;
	}

	public String getLists(String uid) {
		PreparedStatement ps = session.prepare("SELECT * FROM "+customerStore+" WHERE id=?;");
		BoundStatement boundStatement = new BoundStatement(ps);
		ResultSet rs =session.execute(boundStatement.bind(uid));

		Row r = rs.one();
		Set<String> s = r.getSet("listIDs",String.class);

		LinkedList<Row> rows = new LinkedList<>();
		for(String listID:s) {
			PreparedStatement ps2 = session.prepare("SELECT * FROM "+listStore+" WHERE listID=?;");
			BoundStatement boundStatement2 = new BoundStatement(ps2);
			ResultSet rs2 =session.execute(boundStatement2.bind(listID));
			rows.add(rs2.one());
		}
		return(toJSON(rows));
	}

	public void updateData() throws SQLException {
        String t = "update " + listStore + " set category='sports', linkcounts=1 where key='user5'";
        session.execute(t);
    }
    public int getRows(){
        PreparedStatement ps = session.prepare(
                "SELECT * FROM "+listStore+";"
        );
        BoundStatement boundStatement = new BoundStatement(ps);
        ResultSet rs =session.execute(boundStatement);
        return rs.all().size();
    }
	public Cache getFirst() throws SQLException {

        PreparedStatement ps = session.prepare(
				"SELECT * FROM "+listStore+" LIMIT 1;"
				);
	    BoundStatement boundStatement = new BoundStatement(ps);
	    ResultSet rs =session.execute(boundStatement);
        System.out.println("Attempting to take out first cache");
        Row r = rs.one();
	    return(new Cache(r.getString("userId"),r.getString("cacheId"),r.getInt("expiration"),r.getString("message"),r.getBytes("photo")));
	}
	public String getData(String cacheId, String userId) throws SQLException {
	    String t = "SELECT * FROM "+listStore+" WHERE userid='"+userId+"' and cacheid='"+cacheId+"'";
	    ResultSet rs =session.execute(t);
	    return(toJSON(rs));
	}
	public void close(){
		session.close();
		cluster.close();
	}
	public String toJSON(ResultSet rs){
	    ToJson converter = new ToJson();
	    JSONArray json;
	    json = converter.toJSONArray(rs.all());
	    return(json.toString());
	}

	public String toJSON(List<Row> rows) {
		ToJson converter = new ToJson();
		JSONArray json;
		json = converter.toJSONArray(rows);
		return(json.toString());
	}
}