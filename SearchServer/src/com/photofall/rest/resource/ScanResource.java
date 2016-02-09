package com.photofall.rest.resource;

import com.photofall.rest.service.ScanService;
import com.sun.jersey.api.core.InjectParam;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.File;
import java.nio.ByteBuffer;

@Resource
@Path("/scan")
public class ScanResource {

    @InjectParam
	ScanService scanService;

	@GET
	@Path("/add/{listID}/{barcode}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response add(@PathParam("listID")String listID, @PathParam("barcode")String barcode){
		//replace this String with a suitable data structure when we have the xml defined better

		return scanService.addScan(listID,barcode);
	}
    @GET
    @Path("/create/{uid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createList(@PathParam("uid")String userId){
        return scanService.createList(userId);
    }

	@GET
	@Path("/createUser/{name}/{password}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createUser(@PathParam("name")String name, @PathParam("password")String password) {
		return scanService.createUser(name, password);
	}

	@GET
    @Path("/get/{uid}/lists")
    @Produces(MediaType.APPLICATION_JSON)
    public Response returnFirst(@PathParam("uid")String uid){
        return scanService.getLists(uid);
    }
	@GET
	@Path("/get")
	@Produces(MediaType.APPLICATION_JSON)
	public Response returnTitle(@PathParam("cacheId")String cacheId,@PathParam("userId")String userId){
		File fi = null;
		byte[] fileContent= null;
		ByteBuffer buffer = null;
		String imageHTML= "<p><img src=\"/photofall/image.png\"></p>";
		return new Response() {
			@Override
			public Object getEntity() {
				return null;
			}

			@Override
			public int getStatus() {
				return 0;
			}

			@Override
			public MultivaluedMap<String, Object> getMetadata() {
				return null;
			}
		};
		
	}
}
