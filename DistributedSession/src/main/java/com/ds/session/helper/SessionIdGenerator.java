package com.ds.session.helper;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Session ID generator.
 * @author wlq
 *
 */
public final class SessionIdGenerator {
	/**
	 * Session ID generator instance.
	 */
	private static SessionIdGenerator instance = 
			new SessionIdGenerator();
	
	/**
	 * New Session ID.
	 */
	private static final String NEW_SESSION_ID = "dsid";
	
	/**
	 * 
	 */
	private static final String SESSION_ID_RANDOM_ALGORITHM = "SHA1PRNG";
	
	/**
	 * 
	 */
	private static final String SESSION_ID_RANDOM_ALGORITHM_ALT = "IBMSecureRandom";

	/**
	 * Logger info.
	 */
	private Logger log = LoggerFactory.getLogger(SessionIdGenerator.class);
	
	/**
	 * random.
	 */
	private Random random;
	
	/**
	 * whether or not the random is safe.
	 */
	private boolean weakRandom;
	
	/**
	 * 
	 */
	private static final int BITS = 32;
	
	/**
	 * 
	 */
	private static final int RADIX = 36;
	
	/**
	 * 
	 */
	private SessionIdGenerator() {
		if (random == null) {
			try {
				random = SecureRandom.getInstance(SESSION_ID_RANDOM_ALGORITHM);
			} catch (NoSuchAlgorithmException e) {
				try {
					random = SecureRandom.getInstance(SESSION_ID_RANDOM_ALGORITHM_ALT);
					weakRandom = false;
				} catch (NoSuchAlgorithmException e2) {
					log.warn("wrong when getting random generator", e);
                    random = new Random();
                    weakRandom = true;
				}
			}
			
		}
		
		random.setSeed(random.nextLong() ^ System.currentTimeMillis()
                ^ hashCode() ^ Runtime.getRuntime().freeMemory());
	}
	/**
	 * Get the Session ID generator instance.
	 * @return SessionIdGenerator
	 */
	public static SessionIdGenerator getInstance() {
		return instance;
	}
	
	/**
	 * Get a new session ID.
	 * @param req HttpServletRequest
	 * @return A new Session ID
	 */
	public String newSessionId(
			HttpServletRequest req) {
		
		// A request has a session ID already.
        String requestedId = req.getRequestedSessionId();
        if (requestedId != null) {
        	return requestedId;
        }
        
        // Else reuse any session ID already defined for this request.
        String newId = (String) req.getAttribute(NEW_SESSION_ID);
        if (newId != null) {
        	return newId;
        }
        
        //pick a new unique ID!
        String id = null;
        while (id == null || id.length() == 0) {
        	long r = weakRandom ? (hashCode()
                    ^ Runtime.getRuntime().freeMemory() ^ random.nextInt() ^ (((long) req
                    .hashCode()) << BITS)) : random.nextLong();
        	
        	r ^= System.currentTimeMillis();
        	if (req.getRemoteAddr() != null) {
                r ^= req.getRemoteAddr().hashCode();
            }
        	if (r < 0) {
                r = -r;
            }
            id = Long.toString(r, RADIX);
        }
        req.setAttribute(NEW_SESSION_ID, id);
		return id;
	}
	
}
