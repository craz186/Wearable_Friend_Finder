package com.gibbons.server.utils;

import com.datastax.driver.core.DataType;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.Row;

import org.owasp.esapi.ESAPI;

import java.util.List;

/**
 * This utility will convert a database data into JSON format.
 * Note:  this java class requires the ESAPI 1.4.4 jar file
 * ESAPI is used to encode data
 * 
 * @author 308tube
 */
public class ToJson {

	/**
	 * This will convert database records into a JSON Array
	 * Simply pass in a ResultSet from a database connection and it
	 * loop return a JSON array.
	 * 
	 * It important to check to make sure that all DataType that are
	 * being used is properly encoding.
	 * 
	 * varchar is currently the only dataType that is being encode by ESAPI
	 * 
	 * @param  - database ResultSet
	 * @return - JSON array
	 * @throws Exception
	 */
	public JSONArray toJSONArray(List<Row> rows){

        JSONArray json = new JSONArray(); //JSON array that will be returned
        String temp = null;

        try {

        	 //we will need the column names, this will save the table meta-data like column nmae.


             //loop through the ResultSet
        	for(Row r:rows){
            	 ColumnDefinitions rsmd = r.getColumnDefinitions();
                 //each row in the ResultSet will be converted to a JSON Object
                 JSONObject obj = new JSONObject();
                 System.out.println("Convertering "+rsmd.toString());
                 //loop through all the columns and place them into the JSON Object
                 for(int i=0; i<rsmd.size(); i++) {
                     String column_name = rsmd.getName(i);
                     System.out.println(i+ " " + column_name);
             		 DataType.Name column_type = rsmd.getType(i).getName();
                     if(column_type==DataType.Name.LIST){
                    	 obj.put(column_name, r.getList(column_name,Object.class));
                    	 /*Debug*/ System.out.println("ToJson: ARRAY");
                     }
                     else if(column_type==DataType.Name.BIGINT){
                    	 obj.put(column_name, r.getLong(column_name));
                    	 /*Debug*/ System.out.println("ToJson: BIGINT");
                     }
                     else if(column_type==DataType.Name.BOOLEAN){
                    	 obj.put(column_name, r.getBool(column_name));
                    	 /*Debug*/ System.out.println("ToJson: BOOLEAN");
                     }
                     else if(column_type==DataType.Name.BLOB){
                    	 obj.put(column_name, r.getBytes(column_name));
                    	 /*Debug*/ System.out.println("ToJson: BLOB");
                     }
                     else if(column_type==DataType.Name.DOUBLE){
                    	 obj.put(column_name, r.getDouble(column_name));
                    	 /*Debug*/ System.out.println("ToJson: DOUBLE");
                     }
                     else if(column_type==DataType.Name.FLOAT){
                    	 obj.put(column_name, r.getFloat(column_name));
                    	 /*Debug*/ System.out.println("ToJson: FLOAT");
                     }
                     else if(column_type==DataType.Name.INT){
                    	 obj.put(column_name, r.getInt(column_name));
                    	 /*Debug*/ System.out.println("ToJson: INTEGER");
                     }
                     else if(column_type==DataType.Name.VARCHAR){

                    	 temp = r.getString(column_name); //saving column data to temp variable
                    	 temp = ESAPI.encoder().canonicalize(temp); //decoding data to base state
                    	 temp = ESAPI.encoder().encodeForHTML(temp); //encoding to be browser safe
                    	 obj.put(column_name, temp); //putting data into JSON object

                    	 //obj.put(column_name, rs.getString(column_name));
                    	 // /*Debug*/ System.out.println("ToJson: VARCHAR");
                     }
                     else if(column_type==DataType.Name.TIMESTAMP){
                    	 obj.put(column_name, r.getDate(column_name));
                    	 /*Debug*/ System.out.println("ToJson: TIMESTAMP");
                     }
                     else if(column_type==DataType.Name.DECIMAL){
                    	 obj.put(column_name, r.getDecimal(column_name));
                    	 // /*Debug*/ System.out.println("ToJson: NUMERIC");
                      }
                    }//end foreach

                 json.put(obj);

             }//end while

        } catch (Exception e) {
            e.printStackTrace();
        }

        return json; //return JSON array
	}
}
