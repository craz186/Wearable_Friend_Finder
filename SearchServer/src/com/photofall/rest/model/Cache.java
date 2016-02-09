package com.photofall.rest.model;

import java.nio.ByteBuffer;

public class Cache {
	String userId;
    String cacheId;
    int expiration;
	String message;
	ByteBuffer photo;

  	public Cache(String userId, String cacheId, int expiration, String message, ByteBuffer photo){
		this.userId= userId;
		this.cacheId= cacheId;
		this.expiration= expiration;
		this.message= message;
		this.photo = photo;
	}
    public String getUserId(){
		return userId;
	}

    public void setUserId(String userId){ this.userId=userId; }

    public String getCacheId(){
		return cacheId;
	}

    public void setCacheId(String cacheId){ this.cacheId=cacheId; }

    public long getExpiration(){
		return expiration;
	}

    public void setExpiration(int expiration){this.expiration=expiration;}

    public String getMessage(){
		return message;
	}

    public void setMessage(String message){this.message=message;}

    public ByteBuffer getPhoto(){
		return photo;
	}

    public void setPhoto(ByteBuffer photo){this.photo=photo;}
}
