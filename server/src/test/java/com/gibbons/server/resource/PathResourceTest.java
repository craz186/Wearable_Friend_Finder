package com.gibbons.server.resource;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by User on 4/9/2016.
 */
public class PathResourceTest {

    PathResource resource = new PathResource();

    @Test
    public void testCalculatePath() {
        assertEquals(204, resource.calculatePath(null, null).getStatus());
    }

    @Test
    public void test() {
        assertEquals(1,1);
    }
}
