package com.ds.session.helper;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

import com.ds.session.Configuration;

import junit.framework.TestCase;

/**
 * Unit test for ConnectionWatcher.
 * @author wlq
 *
 */
public class ConnectionWatcherTest extends TestCase {
	
	/**
	 * Test zookeeper server connection.
	 */
	public void testConnection() {
		ZooKeeper zk = new ConnectionWatcher().connection(Configuration.getServers());
		try {
			zk.create("/test", null, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
			Stat stat = zk.exists("/test", false);
			if (stat != null) {
				assertTrue(true);
			}
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
    }

}
