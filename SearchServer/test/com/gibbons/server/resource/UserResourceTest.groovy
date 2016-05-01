package com.gibbons.server.resource

import com.gibbons.server.service.UserService
import org.codehaus.jettison.json.JSONArray
import org.codehaus.jettison.json.JSONObject
import org.junit.Assert
import org.junit.Before
import org.junit.Test

import javax.ws.rs.core.Response

import static org.junit.Assert.*


class UserResourceTest {

    UserResource resource = new UserResource()
    UserService service = new UserService()
    final String NAME = "Sean"
    final String FRIEND = "Friend"

    @Before
    public void setup() {
        resource.userService.userStore.removeUser(NAME)
        resource.userService.userStore.removeUser(FRIEND)
    }

    @Test
    public void testAddNewUser() {
        addUser(NAME)
    }

    @Test
    public void testAddNewUserAlreadyExists() {
        String uid = addUser(NAME)
        Response res = resource.addNewUser(NAME,"Password")
        assertEquals(200,res.status)
        assertEquals(uid,res.entity)
    }

    @Test
    public void testAddNewUserAlreadyExistsDiffPassword() {
        addUser(NAME)
        Response res = resource.addNewUser(NAME, "Wrong")
        assertEquals(409, res.status)
        assertNull(res.entity)
    }

    @Test
    public void testAppendRegID() {
        String REGID = "REGID"
        String uid = addUser(NAME)
        resource.appendRegID(uid, REGID)
        assertEquals(REGID, resource.userService.userStore.getRegID(uid))
        assertEquals(REGID, resource.userService.userStore.getRegIDFromUsername(NAME))
    }

    @Test
    public void testAddFriend() {
        String uid = addUser(NAME)
        String friendUid = addUser(FRIEND)
        resource.addFriend(uid, FRIEND)
        JSONObject obj = new JSONArray((String)resource.returnFriends(uid).entity).get(0) as JSONObject
        assertEquals(obj.getString("name"),FRIEND)
        assertEquals(obj.getString("uid"),friendUid)
    }

    @Test
    public void testNotify() {
        //We send null here since we have no way of testing if a notification is recieved
        //so we send fake data and check that nothing goes wrong
        assertEquals(200,resource.notifyUser(null,null).status)
        assertEquals(200,resource.notifyUser(null,null,null).status)
        assertEquals(200,resource.notifyFriendRequest(null,null).status)
    }

    public String addUser(String name) {
        Response res = resource.addNewUser(name,"Password")
        String temp = resource.userService.userStore.getUsernameFromUid((String)res.entity)
        assertEquals(name,temp)
        return res.entity
    }
}
