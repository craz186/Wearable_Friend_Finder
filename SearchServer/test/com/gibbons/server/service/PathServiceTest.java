package com.gibbons.server.service;

import org.junit.Test;

/**
 * Created by User on 4/10/2016.
 */
public class PathServiceTest {

    PathService service = new PathService();

    @Test
    public void testCalculatePath() {
        service.calculatePath("1","1");
    }
}
