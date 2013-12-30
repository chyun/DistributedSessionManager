package com.ds.session.wrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ds.session.SessionManager;

/**
 * A HttpRequest wrapper.
 * 
 * @author wlq
 * 
 */
public class DTRequestWrapper extends HttpServletRequestWrapper {

	/**
	 * info logger.
	 */
	private static Logger log = LoggerFactory.getLogger(DTRequestWrapper.class);

	/**
	 * SessionManager.
	 */
	private SessionManager sessionManager;

	/**
	 * NULL_SESSION ID.
	 */
	//private static final String NULL_SESSION = "__NULL__";

	/**
	 * HttpServletResponse.
	 */
	private HttpServletResponse response;

	/**
	 * Attribute of name of session.
	 */
	//private static final String THIS_SESSION = "dt.thisSession";

	/**
	 * construct method.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param res HttpServletResponse
	 * @param sm sessionManager
	 */
	public DTRequestWrapper(HttpServletRequest request,
			HttpServletResponse res,
			SessionManager sm) {
		super(request);
		this.sessionManager = sm;
		this.response = res;
	}

	@Override
	public HttpSession getSession(boolean create) {
		if (sessionManager == null) {
			log.error("SessionManager not initialized...");
			throw new IllegalStateException("SessionManager not initialize");
		}

		HttpServletRequest request = (HttpServletRequest) getRequest();

		String sessionid = sessionManager.getRequestSessionId(request);
		
		if (!create && sessionid == null) {
			return null;
		}
		
		HttpSession session = null;

		//If sessionID is not null, take session from container
		if (sessionid != null) {
			session = sessionManager
					.getHttpSession(sessionid, request);
			
			if (session == null && !create) {
				/*request.setAttribute(
						THIS_SESSION, NULL_SESSION);*/
				return null;
			}
		}

		if (session == null && create) {
			session = sessionManager
					.newHttpSession(request, response);
			//request.setAttribute(THIS_SESSION, session.getId());
		}
		return session;
	}
	
	@Override
	public HttpSession getSession() {
		return getSession(true);
	}
	
	/**
	 * 
	 * @return HttpServletResponse
	 */
	public HttpServletResponse getResponse() {
		return response;
	}
	
	/**
	 * 
	 * @param res HttpServletResponse
	 */
	public void setResponse(HttpServletResponse res) {
	    
        this.response = res;
    }
}
