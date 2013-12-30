package com.ds.session.helper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to connect zookeeper server.
 * @author wlq
 *
 */
public class ConnectionWatcher implements Watcher {
	
	/** zookeeper timeout.*/
	private static final int SESSION_TIMEOUT = 5000;
	
	/** zookeeper client's max connect time.*/
	private static final int CONNECT_TIMEOUT = 30;
	
	/***/
	private CountDownLatch signal = new CountDownLatch(1);
	
	/**logger info.*/
	private static final Logger LOG = 
			LoggerFactory.getLogger(ConnectionWatcher.class);
	
	/**ZooKeeper client.*/
	private ZooKeeper zk;
	/**Construct method.
	 * @param servers zookeeper servers
	 * @return zookeeper client
	 */
	public ZooKeeper connection(String servers) {
		
		if (zk == null) {
			try {
				synchronized (this) {
					if (zk == null) {
						zk = new ZooKeeper(servers, SESSION_TIMEOUT, this);
						
						//it won't return the zk until the server pass 
						//an event to the client. Is it not too slow?
						boolean success = signal.await(CONNECT_TIMEOUT, TimeUnit.SECONDS);
						if (success) {
							return zk;
						}
					}
				}
			} catch (IOException e) {
				LOG.error(e.toString());
			} catch (InterruptedException e) {
				LOG.error(e.toString());
			}
		}
		
		return zk;
	}

	
	@Override
	public void process(WatchedEvent event) {
		KeeperState state = event.getState();
		if (state == KeeperState.SyncConnected) {
			signal.countDown();
		}
	}

}
