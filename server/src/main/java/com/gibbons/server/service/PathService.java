package com.gibbons.server.service;

import com.gibbons.server.utils.Constants;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;

public class PathService {

    public Response calculatePath(String startGps, String endGps) {

        HttpClient client = HttpClientBuilder.create().build();
        String url = Constants.googleDirections + "json?origin="+startGps+"&destination="+endGps+"&key=AIzaSyAjdA8Q5lQzXfWvbtOTl8PF_zE42I8f96E";
        //String url = Constants.googleDirections + "json?origin=Toledo&destination=Madrid&region=es&key=AIzaSyAjdA8Q5lQzXfWvbtOTl8PF_zE42I8f96E";

        HttpResponse response;
        String path = "";
        String s = "";
        try {
            response = client.execute(RequestBuilder.get(url).build());
            InputStream stream = response.getEntity().getContent();
            int k;
            while((k =stream.read())!= -1) {
                s += (char)k;
            }
            JSONArray routes = new JSONObject(s).getJSONArray("routes");
            if(routes.length() == 0)
                return(Response.noContent().build());
            JSONObject obj = (JSONObject) routes.get(0);
            JSONArray arr = obj.getJSONArray("legs");
            arr = arr.getJSONObject(0).getJSONArray("steps");

            JSONObject end;

            for(int i=0; i<arr.length(); i++) {
                JSONObject ob = (JSONObject) arr.get(i);
                end = ob.getJSONObject("end_location");
                path += end.getString("lat")+ ","+ end.getString("lng")+ " ";

            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        System.out.println(path);
        return(Response.ok(path).build());

    }

}
