package com.gibbons.server.resource;

import com.gibbons.server.service.PathService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/path")
public class PathResource {

    PathService pathService = new PathService();

    //@GET
    @POST
    @Path("/calculate/{startGps}/{endGps}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response calculatePath(@PathParam("startGps")String startGps, @PathParam("endGps")String endGps){
        return pathService.calculatePath(startGps,endGps);
    }
}
