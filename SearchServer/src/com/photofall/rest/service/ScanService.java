package com.photofall.rest.service;

import com.photofall.rest.store.ScanStore;
import com.sun.jersey.api.core.InjectParam;

import javax.ws.rs.core.Response;
import java.nio.ByteBuffer;
import java.sql.SQLException;

public class ScanService {

    @InjectParam
	ScanStore scanStore;

    private boolean started = false;

	public Response addScan(String listID, String barcode){
		System.out.println("println");
		try{
			return(Response.ok(scanStore.addScan(listID,barcode)).build());
			
		}catch(SQLException e){
			e.printStackTrace();
		}
		return null;
	}
	public Response createList(String userId){
		try{
			return(Response.ok(scanStore.createList(userId)).build());
			
		}catch(SQLException e){
			e.printStackTrace();
		}
		return null;
		
	}

	public Response createUser(String name, String password) {
		try{
			return(Response.ok(scanStore.createUser(name, password)).build());

		}catch(SQLException e){
			e.printStackTrace();
		}
		return null;
	}

	public Response getLists(String uid) {
		return(Response.ok(scanStore.getLists(uid)).build());

	}

    public Response deleteCache(String cacheId, String userId){
        return(Response.ok().build());
    }
    public void setScanStore(ScanStore scanStore){
        this.scanStore = scanStore;
    }

    public Response getFirst(String cacheId, String userId){
        String response = "hello";
        System.out.println("THE RESPONSE: " + response);
        return(Response.ok(response).build());
    }

}
