package com.ds.session;

import junit.framework.TestCase;

/**
 * 
 * Unit test for configuration.
 * @author wlq
 *
 */
public class ConfigurationTest extends TestCase {
	
	/**
     * Init Test.
     */
    public void testInit() {
    	
    	if ("127.0.0.1:2181".equals(Configuration.getServers())) {
    		//System.out.println(Configuration.getServers());
    		assertTrue(true);
    	}
    	
    	if (20 == Configuration.getMaxIdle()) {
    		//System.out.println(Configuration.getMaxIdle());
    		assertTrue(true);
    	}
    	
    	if (20 == Configuration.getMaxIdle()) {
    		assertTrue(true);
    	}
    	
    	if (20 == Configuration.getInitIdleCapacity()) {
    		assertTrue(true);
    	}
    	
    	if (1 == Configuration.getTimeout()) {
    		assertTrue(true);
    	}
    	
    	if (60 == Configuration.getConnectionTimeout()) {
    		assertTrue(true);
    	}
    	
    	if (1 == Configuration.getCookieExpiry()) {
    		assertTrue(true);
    	}
        
    }

}
