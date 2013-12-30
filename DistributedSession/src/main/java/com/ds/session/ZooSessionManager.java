package com.ds.session;


import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ds.session.helper.CookieHelper;
import com.ds.session.helper.SessionIdGenerator;
import com.ds.session.helper.ZooKeeperHelper;


/**
 * An implementation of sessionManager based on zookeeper.
 * @author wlq
 *
 */
public class ZooSessionManager implements SessionManager {
	
	/**
	 * info logger.
	 */
	private static final Logger LOG = LoggerFactory
			.getLogger(ZooSessionManager.class);
	
	/**Configuration.*/
	//private final Configuration config;
	
	/**
	 * local session container.
	 */
	//TODO add a cache between zookeeper an local
	//private Map<String, HttpSession> sessions;
	
	/**ServletContext.*/
	private ServletContext servletContext;
	
	/** seconds in one minute. */
	private static final int SECONDS = 60;
	
	/**millis in one second.*/
	private static final int MILLIS = 1000;
	
	/**
	 * Construct method.
	 * @param ctx ServletContext
	 */
	public ZooSessionManager(ServletContext ctx) {
		this.servletContext = ctx;
		createGroup();
		//config = Configuration.class.newInstance();
		//sessions = new ConcurrentHashMap<String, HttpSession>();
	}
	
	/**
	 * Default Construct method.
	 * @throws IllegalStateException
	 */
	public ZooSessionManager() {
	    createGroup();
		//Configuration.class;
		//sessions = new ConcurrentHashMap<String, HttpSession>();
	}

	/**
	 * Get the sessionID from request.
	 * @param request HttpServletRequest
	 * @return session ID
	 */
	public String getRequestSessionId(HttpServletRequest request) {
		return CookieHelper.findSessionId(request);
	}
	
	//private SessionClient client;
	//这里有一个一致性问题。当一个服务获取会话信息的时候，
	//在更新metadata之前，应当将节点锁住
	@Override
	public HttpSession getHttpSession(String id,
			HttpServletRequest request) {
		
		if (request == null || id == null) {
			return null;
		}
		
		/**
		 * Check if the node of id is validation, 
		 * the session may be already expired or 
		 * there is no this node
		 */
		boolean valid = ZooKeeperHelper.isValid(id);
		
		/**
		 * The sessionID is not validation.
		 * Remove it from local container.
		 */
		if (!valid) {
			//sessions.remove(id);
			return null;
		} else {
			//otherwise
			//update session MetaData remotely
			SessionMetaData  metaData = 
					ZooKeeperHelper.updateSessionMetaData(id);
			
			if (metaData == null) {
				return null;
			}
			
			//if session is already expired
			if (!metaData.getValidate()) {
				return null;
			}
			
			HttpSession session = new DTSession(this,
					metaData, id, Configuration.getConnectionTimeout(), request);
			
			return session;
		}
	}

	@Override
	public HttpSession newHttpSession(HttpServletRequest request,
			HttpServletResponse response) {
		if (request == null) {
			return null;
		}
		
		//Create a session remotely
		DTSession session = new DTSession(this, 
				Configuration.getConnectionTimeout(), request); 
		
		addHttpSession(session);
		String id = session.getId();
		
		Cookie cookie = CookieHelper.writeSessionIdToCookie(
				id, request, response, Configuration.getCookieExpiry());
		
		if (cookie != null) {
			LOG.debug("Wrote sid to Cookie,name:[" + cookie.getName() + "],value:["
	                      + cookie.getValue() + "]");
	    }
	    
	    return session;
	}

	/*private void addHttpSession(String id, DTSession session) {
		sessions.put(id, session);
	}*/

	@Override
	public void removeHttpSession(DTSession session) {

		if (session == null) {
			return;
		}
		ZooKeeperHelper.deleteSessionNode(session.getId());
	}

	@Override
	public String getNewSessionId(HttpServletRequest request) {
		return SessionIdGenerator.getInstance().newSessionId(request);
	}

	@Override
	public void close() {
		ZooKeeperHelper.destroy();
	}

	@Override
	public void addHttpSession(DTSession session) {
		String sid = session.getId();
		SessionMetaData metadata = new SessionMetaData();
	    metadata.setId(sid);
	    metadata.setMaxIdle((long) session.getMaxInactiveInterval());
	    //turn the minute into milliseconds
	    metadata.setMaxIdle(Configuration.getTimeout() * SECONDS * MILLIS); 
	    //metadata.setCreateTm(session.getCreationTime());
	    //metadata.setLastAccessTm(lastAccessTm);
	    metadata.setVersion(-1);
	    // turn it into millisecond
	    ZooKeeperHelper.createSessionNode(metadata);
	}

	@Override
	public ServletContext getServletContext() {
		return servletContext;
	}

	@Override
	public void setServletContext(ServletContext sc) {
		this.servletContext = sc;
	}
	
	/**
     * Create root directory.
     */
    private void createGroup() {
        ZooKeeperHelper.createSessionRoot();
    }

	/*@Override
	public SessionClient getSessionClient() {
		// TODO Auto-generated method stubD
		return null;
	}*/

}
