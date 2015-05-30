package com.ymarq.eu.ymarq;

import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.TestCase;

/**
 * Created by eu on 12/28/2014.
 */
public class SimpleTest2 extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @SmallTest
    public void numberAdder(){
        assertEquals(4, 3);
    }

    @Override
    protected void tearDown() throws Exception{
        super.tearDown();
    }
}
