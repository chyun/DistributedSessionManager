package com.ds.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ds.session.helper.SessionIdGenerator;
import com.ds.session.helper.ZooKeeperHelper;

/**
 * HttpSession's proxy class. 
 * @author wlq
 *
 */
@SuppressWarnings("deprecation")
public class DTSession implements HttpSession {
	
	/** logger info.*/
	private static final Logger LOG =
			 LoggerFactory.getLogger(DTSession.class);
	
	/**sessionManager.*/
	private SessionManager sessionManager;
	
	/**session ID.*/
	private String id;
	
	/** session's create time.*/
	private long createTime;
	
	/** session's last access time.*/
	private long lastAccessTime;
	
	/** default maxInactiveInterval.*/
	private static final int DEFAULT_INTERVAL = 1000;
	
	/** max idle time.*/
	private int maxInactiveInterval = DEFAULT_INTERVAL;
	
	/** whether or not the session is new.*/
	private boolean isNew;
	
	/**HttpServletRequest.*/
	private HttpServletRequest request;
	
	/** local date cached. */
	//private WeakHashMap<HttpServletRequest, Map<String, Object>> cache;
	
	//private static final String NULL_VALUE = "__NULL__";
	
	/**
	 * Create a new session for a request without session.
	 * @param manager SessionManager
	 * @param interval maxInactiveInterval
	 * @param req HttpServletRequest
	 */
	public DTSession(SessionManager manager, int interval, 
		  HttpServletRequest req) {
		
		this.sessionManager = manager;
		this.createTime = System.currentTimeMillis();
		this.lastAccessTime = this.createTime;
		this.isNew = true;
		this.setRequest(req);
		this.id = SessionIdGenerator.getInstance().newSessionId(request);
		this.maxInactiveInterval = interval;
		/*this.cache = new WeakHashMap<HttpServletRequest, 
				Map<String, Object>>();*/
	}
	
	/**
	 * construct method.
	 * @param manager SessionManager
	 * @param createT create time
	 * @param sid JsessionId
	 * @param interval maxInactiveInterval
	 * @param req HttpServletRequest
	 */
	public DTSession(SessionManager manager, 
			long createT, String sid, int interval, HttpServletRequest req) {
		
		this.sessionManager = manager;
		this.createTime = createT;
		this.lastAccessTime = this.createTime;
		this.isNew = true;
		this.id = sid;
		this.maxInactiveInterval = interval;
		this.setRequest(req);
		/*this.cache = new WeakHashMap<HttpServletRequest, 
				Map<String, Object>>();*/
	}

	/**
	 * construct method.
	 * @param manager SessionManager
	 * @param metaData SessionMetaData
	 * @param sid Session ID
	 * @param interval maxInactiveInterval
	 * @param req HttpServletRequest
	 */
	public DTSession(SessionManager manager,
			SessionMetaData metaData, String sid, int interval, HttpServletRequest req) {

		this.sessionManager = manager;
		this.createTime = metaData.getCreateTm();
		this.lastAccessTime = metaData.getLastAccessTm();
		this.isNew = false;
		this.id = sid;
		this.maxInactiveInterval = interval;
		this.setRequest(req);
	}

	/**
	 * the function should be synchronized, but if I
	 * made the function is readonly, the performance
	 * would be faster.
	 * @param name Attribute name
	 * @return Attribute value
	 */
	@Override
	public Object getAttribute(String name) {
		
		if (null == name) {
			return null;
		}
		String sessionId = getId();
		
		if (StringUtils.isNotBlank(sessionId)) {
			
			return ZooKeeperHelper.getSessionData(sessionId, name);
        }
		return null;
		
	}

	@Override
	public Enumeration<String> getAttributeNames() {

		String sessionId = getId();
		
		if (StringUtils.isNotBlank(sessionId)) {
			return Collections.enumeration(
					ZooKeeperHelper.getSessionMap(sessionId).keySet());
        }
		return null;
	}

	@Override
	public long getCreationTime() {
		return createTime;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public long getLastAccessedTime() {
		return lastAccessTime;
	}

	@Override
	public int getMaxInactiveInterval() {
		return maxInactiveInterval;
	}

	@Override
	public ServletContext getServletContext() {
		return sessionManager.getServletContext();
	}

	@Override
	public HttpSessionContext getSessionContext() {
		return null;
	}

	@Override
	public Object getValue(String name) {
		return getAttribute(name);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public String[] getValueNames() {
		List<String> names = new ArrayList<String>();
		Enumeration n = getAttributeNames();
		while (n.hasMoreElements()) {
			names.add((String) n.nextElement());
		}
		return names.toArray(new String[] {});
	}

	@Override
	public void invalidate() {

		String sessionId = getId();
        if (StringUtils.isNotBlank(sessionId)) {
            ZooKeeperHelper.deleteSessionNode(sessionId);
        }
	}

	@Override
	public boolean isNew() {
		return isNew;
	}
	
	/**
     * Been accessed.
     */
    public void access() {
    
        this.isNew = false;
        ZooKeeperHelper.updateSessionMetaData(getId());
        //this.lastAccessTime = System.currentTimeMillis();
        
    }

	@Override
	public void putValue(String obj, Object val) {
		setAttribute(obj, val);
	}

	/**
	 * Remove the attribute of session.
	 * @param name Attribute name
	 */
	@Override
	public synchronized void removeAttribute(String name) {
		String sid = getId();
		if (StringUtils.isNotBlank(sid)) {
            ZooKeeperHelper.removeSessionData(sid, name);
        } 
	}

	@Override
	public void removeValue(String name) {

		removeAttribute(name);
	}

	@Override
	public synchronized void setAttribute(String name, Object value) {
		String sid = getId();
        if (StringUtils.isNotBlank(sid)) {
        	SessionMetaData metaData = ZooKeeperHelper.updateSessionMetaData(sid);
        	if (metaData == null || !metaData.getValidate()) {
        		LOG.warn("SetAttribute failed: [" + sid + "]");
        		return;
        	}
            ZooKeeperHelper.setSessionData(sid, name, value);
        }
	}

	@Override
	public void setMaxInactiveInterval(int interval) {
		this.maxInactiveInterval = interval;
	}

	/**
	 * Get HttpServletRequest.
	 * @return HttpServletRequest
	 */
	public HttpServletRequest getRequest() {
		return request;
	}

	/**
	 * Set HttpServletRequest.
	 * @param req HttpServletRequest
	 */
	public void setRequest(HttpServletRequest req) {
		this.request = req;
	}

}
