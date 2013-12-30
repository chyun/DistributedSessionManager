package com.ds.session;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;



/**
 * Session manager.
 * 
 * @author wlq
 * 
 */
public interface SessionManager {

	/** expired time of session. */
	public static final int COOKIE_EXPIRY = 365 * 24 * 60 * 60;

	/**
	 * get the HttpSession.
	 * 
	 * @param id Session ID
	 * @param request HttpRequest
	 * @return HttpSession
	 * */
	public HttpSession getHttpSession(String id, 
			HttpServletRequest request);

	/**
	 * get a new HttpSession.
	 * 
	 * @param request HttpRequest
	 * @param response HttpResponse
	 * @return HttpSession
	 */
	public HttpSession newHttpSession(HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * Get sessionID from request.
	 * 
	 * @param request
	 *            HttpRequest
	 * @return SessionID
	 */
	public String getRequestSessionId(HttpServletRequest request);

	/**
	 * add a new HttpSession into session container.
	 * 
	 * @param session DTSession
	 */
	public void addHttpSession(DTSession session);

	/**
	 * remove a HttpSession from session container.
	 * 
	 * @param session DTSession
	 */
	public void removeHttpSession(DTSession session);

	/**
	 * Get a new unique sessionId.
	 * 
	 * @param request HttpRequest
	 * @return sessionId
	 */
	public String getNewSessionId(HttpServletRequest request);

	/**
	 * Get ServletContext.
	 * 
	 * @return ServletContext
	 */
	public ServletContext getServletContext();

	/**
	 * Set ServletContext.
	 * 
	 * @param sc ServletContext
	 */
	public void setServletContext(ServletContext sc);

	/**
	 * Get SessionClient.
	 * 
	 * @return SessionClient
	 */
	//public SessionClient getSessionClient();

	/**
	 * Close the Sessioin Manager.
	 */
	public void close();
}
