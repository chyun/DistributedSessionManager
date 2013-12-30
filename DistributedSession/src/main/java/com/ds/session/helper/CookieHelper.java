package com.ds.session.helper;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

/**
 * Cookie reader and writer.
 * @author wlq
 *
 */
public final class CookieHelper {
	
	/**Name of cookie for session ID.*/
	private static final String DT_SESSION_ID = "dsid";
	
	/**
	 * COnstruct method.
	 */
	private CookieHelper() {
	    
    }
	
	/**
	 * Find session ID from request.
	 * @param request HttpServletRequest
	 * @return Session ID
	 */
	public static String findSessionId(HttpServletRequest request) {
        return findCookieValue(DT_SESSION_ID, request);
    }
	
	/**
	 * Find the cookie with specific name.
	 * @param name Cookie name
	 * @param request HttpServletRequest
	 * @return Cookie
	 */
	public static Cookie findCookie(String name, HttpServletRequest request) {
	    
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (int i = 0, n = cookies.length; i < n; i++) {
            if (cookies[i].getName().equalsIgnoreCase(name)) {
                return cookies[i];
            }
        }
        return null;
    }
	
	/**
	 * Find the cookie value with specific name.
	 * @param name Cookie name
	 * @param request HttpServletRequest
	 * @return the value of coolie
	 */
	public static String findCookieValue(String name, HttpServletRequest request) {
        Cookie cookie = findCookie(name, request);
        if (cookie != null) {
            return cookie.getValue();
        }
        return null;
    }
	
	/**
	 * Write Session ID to cookie.
	 * @param id Session ID
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param expiry Cookie expiry
	 * @return Cookie
	 */
	public static Cookie writeSessionIdToCookie(String id, HttpServletRequest request,
            HttpServletResponse response, int expiry) {
    
        Cookie cookie = findCookie(DT_SESSION_ID, request);
        if (cookie == null) {
            cookie = new Cookie(DT_SESSION_ID, id);
        }
        cookie.setValue(id);
        if (expiry > 0) {
            cookie.setMaxAge(expiry);
        }
        cookie.setPath(StringUtils.isEmpty(request.getContextPath()) ? "/" : request.getContextPath());
        response.addCookie(cookie);
        return cookie;
    }

}
