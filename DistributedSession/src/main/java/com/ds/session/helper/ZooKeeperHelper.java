package com.ds.session.helper;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ds.session.Configuration;
import com.ds.session.SessionMetaData;

/**
 * A utility class to handle the zookeeper.
 * 
 * @author wlq
 * 
 */
public class ZooKeeperHelper {

	/**
	 * info logger.
	 */
	private static final Logger LOG = 
			LoggerFactory.getLogger(ZooKeeperHelper.class);

	/**
	 * zookeeper servers.
	 */
	private static String hosts = Configuration.getServers();;

	/**
	 * Thred pool.
	 */
	private static ExecutorService pool = Executors.newCachedThreadPool();

	/**
	 * the parent node of all sessions.
	 */
	private static final String GROUP_NAME = "/SESSIONS";

	/**
	 * initialize.
	 * 
	 * @param config
	 *            Configuration
	 */
	/*public static void initialize(Configuration config) {
		hosts = Configuration.getServers();
	}*/

	/**
	 * destroy.
	 */
	public static void destroy() {
		ZooKeeper zk = connect();
		try {
			zk.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
			LOG.error("Close zookeeper client failed!");
		}
		if (pool != null) {
			pool.shutdown();
		}
	}

	/**
	 * Connect to servers.
	 * 
	 * @return ZooKeeper
	 */
	public static ZooKeeper connect() {
		ConnectionWatcher watch = new ConnectionWatcher();
		ZooKeeper zk = watch.connection(hosts);
		return zk;
	}

	/**
	 * Close a zookeeper client.
	 * 
	 * @param zk
	 *            ZooKeeper
	 */
	public static void close(ZooKeeper zk) {
	    //here is some problem
		if (zk != null) {
			try {
				zk.close();
			} catch (InterruptedException e) {
				LOG.error(e.toString());
			}
		}
	}

	/**
	 * Wether or not the Session ID is validation.
	 * @param id session ID
	 * @return validation
	 */
	public static boolean isValid(String id) {
		ZooKeeper zk = connect();
		if (zk != null) {
			return isValid(id, zk);
		}
		return false;
	}

	/**
	 * Wether or not the Session ID is validation.
	 * @param id session ID
	 * @param zk ZooKeeper
	 * @return validation
	 */
	public static boolean isValid(String id, ZooKeeper zk) {
		if (zk != null) {
			SessionMetaData metaData = getSessionMetaData(id, zk);
			if (metaData == null) {
				return false;
			}
			return metaData.getValidate();
		}
		return false;
	}
	
	/**
	 * Obtain SessionMetaData.
	 * @param id session ID
	 * @return SessionMetaData
	 */
	public static SessionMetaData getSessionMetaData(String id) {
		ZooKeeper zk = connect();
		if (zk != null) {
			return getSessionMetaData(id, zk);
		}
		return null;
	}

	/**
	 * Obtain SessionMetaData.
	 * @param id session ID
	 * @param zk ZooKeeper
	 * @return SessionMetaData
	 */
	public static SessionMetaData getSessionMetaData(String id, 
			ZooKeeper zk) {

		if (zk == null || id == null) {
			return null;
		}
		String path = GROUP_NAME + "/" + id;
		try {
			// Check wether or not the node is validation
			Stat stat = zk.exists(path, false);
			if (stat == null) {
				return null;
			}

			byte[] data = zk.getData(path, false, null);

			if (data != null) {
				// deserialize
				Object obj = SerializationUtils
						.deserialize(data);

				if (obj instanceof SessionMetaData) {
					SessionMetaData metadata = 
							(SessionMetaData) obj;

					metadata.setVersion(stat.getVersion());
					return metadata;
				}
			}
		} catch (KeeperException e) {
			LOG.error(e.toString());
		} catch (InterruptedException e) {
			LOG.error(e.toString());
		}
		return null;
	}

	/**
	 * Update the metaData on server.
	 * @param id session ID
	 * @return SessionMetaData after updated
	 */
	public static SessionMetaData updateSessionMetaData(String id) {
		ZooKeeper zk = connect();
		SessionMetaData metaData = getSessionMetaData(id, zk);
		if (metaData != null) {
			updateSessionMetaData(metaData, zk);
		}
			
		return metaData;
	}

	/**
	 * Update SessionMetaData.
	 * @param metadata SessionMetaData
	 * @param zk ZooKeeper
	 */
	public static void updateSessionMetaData(SessionMetaData metadata,
			ZooKeeper zk) {
		
		if (metadata == null || zk == null) {
			return;
		}
		try {
			String id = metadata.getId();
			Long now = System.currentTimeMillis();

			// check whether or not it's expired
			Long timeout = metadata.getLastAccessTm()
					+ metadata.getMaxIdle();

			if (timeout < now) {
				// expired
				// when will the expired node be deleted
				metadata.setValidate(false);
				LOG.debug("Session is expired[" + id + "]");
				//return;
			}

			metadata.setLastAccessTm(now);
			String path = GROUP_NAME + "/" + id;
			byte[] data = SerializationUtils.serialize(metadata);
			zk.setData(path, data, metadata.getVersion());
			LOG.debug("MetaData update finished[" + path + "]");
		} catch (KeeperException e) {
			LOG.error(e.toString());
		} catch (InterruptedException e) {
			LOG.error(e.toString());
		}
	}

	/**
	 * Obtain SessionMap.
	 * @param id Session ID
	 * @return SessionMap
	 */
	public static Map<String, Object> getSessionMap(String id) {
		ZooKeeper zk = connect();
		if (zk != null) {
			
			SessionMetaData metaData = 
					updateSessionMetaData(id);
			if (metaData == null 
					|| !metaData.getValidate()) {
				return null;
			}
			
			String path = GROUP_NAME + "/" + id;
			try {
				// obtain MetaData
				List<String> nodes = 
						zk.getChildren(path, false);
				Map<String, Object> sessionMap 
				    = new HashMap<String, Object>();
				for (String node : nodes) {
					String dataPath = path + "/" + node;
					Stat stat = zk.exists(dataPath, false);

					if (stat != null) {
						byte[] data = 
							zk.getData(dataPath, 
								false, null);
						if (data != null) {
							sessionMap.put(node, 
									SerializationUtils.deserialize(data));
						} else {
							sessionMap.put(node, null);
						}
					}
				}
				return sessionMap;
			} catch (KeeperException e) {
				LOG.error(e.toString());
			} catch (InterruptedException e) {
				LOG.error(e.toString());
			} 
		}
		return null;
	}

	/**
	 * Create the root node of sessions. 
	 */
	public static void createGroupNode() {
		ZooKeeper zk = connect();
		if (zk != null) {
			try {
				Stat stat = zk.exists(GROUP_NAME, false);

				// not exist
				if (stat == null) {
					zk.create(GROUP_NAME, null,
							Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
					LOG.debug("Create root node finished:[" + GROUP_NAME + "]");
				} else {
					LOG.debug("group node is already exist...");
				}
			} catch (KeeperException e) {
				LOG.error(e.toString());
			} catch (InterruptedException e) {
				LOG.error(e.toString());
			} 
		}
	}

	/**
	 * Create session node.
	 * @param metadata SessionMetaData
	 * @return the path of the session node 
	 */
	public static String createSessionNode(SessionMetaData metadata) {

		if (metadata == null) {
			return null;
		}
		ZooKeeper zk = connect();

		if (zk != null) {
			String path = GROUP_NAME + "/" + metadata.getId();
			try {
				Stat stat = zk.exists(path, false);
				if (stat == null) {
					// create the node
					String createPath = zk.create(path, null,
							Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
					LOG.debug("Create node finished[" + path + "]");

					zk.setData(createPath,
							SerializationUtils.serialize(metadata), -1);
					return createPath;
				}
			} catch (KeeperException e) {
				LOG.error(e.toString());
			} catch (InterruptedException e) {
				LOG.error(e.toString());
			}
		}
		return null;
	}

	/**
	 * Create the session Node asynchronously.
	 * @param metadata Session MetaData
	 * @param waitFor whether or not wait
	 * @return the path of the session node
	 */
	public static String asynCreateSessionNode(final SessionMetaData metadata,
			boolean waitFor) {
		Callable<String> task = new Callable<String>() {
			@Override
			public String call() throws Exception {
				return createSessionNode(metadata);
			}
		};

		try {
			Future<String> result = pool.submit(task);

			// if wait is true
			if (waitFor) {
				while (true) {
					if (result.isDone()) {
						return result.get();
					}
				}
			}
		} catch (Exception e) {
			LOG.error(e.toString());
		}

		return null;
	}

	/**
	 * Delete the session node.
	 * @param id Session ID
	 * @return true if the node exist and has been deleted
	 * successfully.
	 */
	public static boolean deleteSessionNode(String id) {

		ZooKeeper zk = connect();
		if (zk != null) {
			String path = GROUP_NAME + "/" + id;
			try {
				Stat stat = zk.exists(path, false);

				// if node exist
				if (stat != null) {
					// delete sub nodes
					List<String> nodes = zk.getChildren(path, false);
					if (nodes != null) {
						for (String node : nodes) {
							zk.delete(path + "/" + node, -1);
						}
					}

					zk.delete(path, -1);
					LOG.debug("Delete node finished[" + path + "]");
					return true;
				}
			} catch (KeeperException e) {
				LOG.error(e.toString());
			} catch (InterruptedException e) {
				LOG.error(e.toString());
			} 
		}
		return false;
	}

	/**
	 * Delete the session node asynchronously.
	 * @param sid Session ID
	 * @param waitFor whether or not wait
	 * @return true if waitFor is true and the
	 * node is deleted successfully otherwise
	 * it's false
	 */
	public static boolean asynDeleteSessionNode(final String sid,
			boolean waitFor) {

		Callable<Boolean> task = new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return deleteSessionNode(sid);
			}
		};

		try {
			Future<Boolean> result = pool.submit(task);
			if (waitFor) {
				while (true) {
					if (result.isDone()) {
						return result.get();
					}
				}
			}
		} catch (Exception e) {
			LOG.error(e.toString());
		}
		return false;
	}

	/**
	 * Set session data on the server according sessionID.
	 * @param sid SessionID
	 * @param name Attribute name
	 * @param value Attribute value
	 * @return true if the data is updated successfully.
	 */
	public static boolean setSessionData(String sid, 
			String name, Object value) {
		boolean result = false;
		ZooKeeper zk = connect();
		if (zk != null) {
			
			String path = GROUP_NAME + "/" + sid;
			try {
				Stat stat = zk.exists(path, false);
				if (stat != null) {
					String dataPath = path + "/" + name;
					stat = zk.exists(dataPath, false);
					if (stat == null) {
						zk.create(dataPath, null, Ids.OPEN_ACL_UNSAFE,
								CreateMode.PERSISTENT);
						LOG.debug("Create data node finished[" + dataPath + "]");
					}
					if (value instanceof Serializable) {
						int dataNodeVer = -1;
						if (stat != null) {
							dataNodeVer = stat.getVersion();
						}
						byte[] data = SerializationUtils
								.serialize((Serializable) value);
						stat = zk.setData(dataPath, data, dataNodeVer);
						LOG.debug("update data node's data finished["
								+ dataPath + "]");
						result = true;
					}
				}
			} catch (KeeperException e) {
				LOG.error(e.toString());
			} catch (InterruptedException e) {
				LOG.error(e.toString());
			}
		}
		return result;
	}

	/**
	 * Set session data on the server according sessionID.
	 *  asynchronously.
	 * @param sid SessionID
	 * @param name Attribute name
	 * @param value Attribute value
	 * @param waitFor waitFor whether or not wait
	 * @return true if waitFor is true and the data is updated 
	 *  successfully.
	 */
	public static boolean asynSetSessionData(final String sid,
			final String name,
			final Object value, boolean waitFor) {
		Callable<Boolean> task = new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return setSessionData(sid, name, value);
			}
		};
		try {
			Future<Boolean> result = pool.submit(task);
			if (waitFor) {
				while (true) {
					if (result.isDone()) {
						return result.get();
					}
				}
			}
		} catch (Exception e) {
			LOG.error(e.toString());
		}
		return false;
	}
	
	/**
	 * Get session data on the server according sessionID.
	 * @param sid SessionID
	 * @param name Attribute name
	 * @return Attribute value
	 */
	public static Object getSessionData(String sid, String name) {
        ZooKeeper zk = connect();
        if (zk != null) {
        	SessionMetaData  metaData = 
        			updateSessionMetaData(sid);
        	if (metaData == null || !metaData.getValidate()) {
        		return null;
        	}
            String path = GROUP_NAME + "/" + sid;
            try {
                String dataPath = path + "/" + name;
                Stat stat = zk.exists(dataPath, false);
                Object obj = null;
                if (stat != null) {
                	byte[] data = zk.getData(dataPath, false, null);
                	if (data != null) {
                		obj = SerializationUtils.deserialize(data);
                	}
                }
                return obj;
            } catch (KeeperException e) {
                LOG.error(e.toString());
            } catch (InterruptedException e) {
                LOG.error(e.toString());
            }
        }
        return null;
    }
	
	/**
	 * Remove session data on the server according sessionID.
	 * @param sid sessionID
	 * @param name Attribute name
	 */
	public static void removeSessionData(String sid, String name) {
        ZooKeeper zk = connect(); 
        if (zk != null) {
            String path = GROUP_NAME + "/" + sid;
            try {
                Stat stat = zk.exists(path, false);
                if (stat != null) {
                    String dataPath = path + "/" + name;
                    stat = zk.exists(dataPath, false);
                    if (stat != null) {
                        zk.delete(dataPath, -1);
                    }
                }
            } catch (KeeperException e) {
                LOG.error(e.toString());
            } catch (InterruptedException e) {
                LOG.error(e.toString());
            }
        }
    }

	/** Create root directory. */
    public static void createSessionRoot() {
        ZooKeeper zk = connect(); 
        if (zk != null) {
            String path = GROUP_NAME;
            try {
                Stat stat = zk.exists(path, false);
                if (stat == null) {
                    //String dataPath = path;
                    zk.create(path, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                    LOG.debug("Create root finished[" + path + "]");
                }
            } catch (KeeperException e) {
                LOG.error(e.toString());
            } catch (InterruptedException e) {
                LOG.error(e.toString());
            }
        }
        
    }
}
