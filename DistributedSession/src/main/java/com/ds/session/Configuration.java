package com.ds.session;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Session Manager Configuration.
 * @author wlq
 *
 */
public final class Configuration {
	
	/**
	 * log info.
	 */
	private static final Logger LOG =
			LoggerFactory.getLogger(Configuration.class);
	
	/**
	 * Configuration file.
	 */
	private static final String CONF_FILE = "session.properties";
	
	/**
	 * servers.
	 */
	private static String servers;
	
	/**
	 * timeout.
	 */
	private static Long timeout;
	
	/**
	 * poolSize.
	 */
	private static int poolSize;
	
	/**Max idle threads.*/
	private static int maxIdle;
	
	/**initIdleCapacity.*/
	private static int initIdleCapacity;
	
	/**connectionTimeout.*/
	private static int connectionTimeout;
	
	/** cookie expiry. */
    private static int cookieExpiry;
    
    /**defaultMaxIdle.*/
    private static final int DEFAULTMAXIDLE = 8;
    
    /**defaultInitIdleCapacity.*/
    private static final int DEFAULTINITIDLECAPACITY = 4;
    
    /**defaultSessionTimeout.*/
    private static final int DEFAULTSESSIONTIMEOUT = 30;
    
    /**defaultConnectionTimeout.*/
    private static final int DEFAULTCONNECTIONTIMEOUT = 60;
    
    //private static final int defaultTimeoutCheckInterval = 30;
    /**defaultCookieExpiry.*/
    private static final int DEFAULTCOOKIEEXPIRY = 0;

    /**
     * Get max idle threads.
     * @return max idle threads
     */
    public static int getMaxIdle() {
		return maxIdle;
	}

    /**
     * Set max idle threads.
     * @param maxIdleThreads max idle threads
     */
	public static void setMaxIdle(int maxIdleThreads) {
		Configuration.maxIdle = maxIdleThreads;
	}

	/**
	 * Get initial idle thread Capacity.
	 * @return initial idle thread Capacity
	 */
	public static int getInitIdleCapacity() {
		return initIdleCapacity;
	}

	/**
	 * Set initial idle thread Capacity.
	 * @param iiC init Idle threads Capacity 
	 */
	public static void setInitIdleCapacity(int iiC) {
		Configuration.initIdleCapacity = iiC;
	}

	/**
	 * Get connection timeout.
	 * @return connection timeout
	 */
	public static int getConnectionTimeout() {
		return connectionTimeout;
	}

	/**
	 * Set connection timeout.
	 * @param cto connectionTimeout
	 */
	public static void setConnectionTimeout(int cto) {
		Configuration.connectionTimeout = cto;
	}

	/**
	 * Get cookie Expiry.
	 * @return cookie Expiry
	 */
	public static int getCookieExpiry() {
		return cookieExpiry;
	}

	/**
	 * Set cookie expiry.
	 * @param cookieExp cookieExpiry
	 */
	public static void setCookieExpiry(int cookieExp) {
		Configuration.cookieExpiry = cookieExp;
	}

	/**
	 * Get default max idle threads.
	 * @return default max idle threads
	 */
	public static int getDefaultmaxidle() {
		return DEFAULTMAXIDLE;
	}

	/**
	 * Get default initial idle threads.
	 * @return default initial idle threads
	 */
	public static int getDefaultinitidlecapacity() {
		return DEFAULTINITIDLECAPACITY;
	}

	/**
	 * Get default session timeout.
	 * @return default session timeout
	 */
	public static int getDefaultsessiontimeout() {
		return DEFAULTSESSIONTIMEOUT;
	}

	/**
	 * Get default connection timeout.
	 * @return default connection timeout
	 */
	public static int getDefaultconnectiontimeout() {
		return DEFAULTCONNECTIONTIMEOUT;
	}

	/**
	 * Get default cookie expiry.
	 * @return default cookie expiry
	 */
	public static int getDefaultcookieexpiry() {
		return DEFAULTCOOKIEEXPIRY;
	}

	/**
	 * Set session timeout.
	 * @param connectiontimeout connection timeout
	 */
	public static void setTimeout(Long connectiontimeout) {
		Configuration.timeout = connectiontimeout;
	}

	static {
        InputStream in = Configuration.class.getClassLoader().getResourceAsStream(CONF_FILE);
        //System.out.println(Configuration.class.getClassLoader().getResource(CONF_FILE).getFile());
        Properties props = new Properties();
        try {
            props.load(in);
            servers = props.getProperty("dt.session.servers");
            maxIdle = NumberUtils.toInt(props.getProperty("dt.session.max_idle"), DEFAULTMAXIDLE);
            initIdleCapacity = NumberUtils.toInt(props.getProperty("dt.session.init_idle_capacity"), DEFAULTINITIDLECAPACITY);
            timeout = (long) NumberUtils.toInt(props.getProperty("dt.session.session_timeout"), DEFAULTSESSIONTIMEOUT);
            connectionTimeout = NumberUtils.toInt(props.getProperty("dt.session.connection_timeout"), DEFAULTCONNECTIONTIMEOUT);
            //timeoutCheckInteval = NumberUtils.toInt(props.getProperty("tc.session.timeout_check_interval"), defaultTimeoutCheckInterval);
            cookieExpiry = NumberUtils.toInt(props.getProperty("dt.session.cookie_expiry"), DEFAULTCOOKIEEXPIRY);
        } catch (Exception e) {
        	LOG.error("Wrong when read configuration file...", e);
        }
    }

	/**
	 * Prevent it to be instanced outside.
	 */
    public Configuration() {
        //System.out.println("ss");
    }

	/**
	 * 
	 * @return servers
	 */
	public static String getServers() {
		return servers;
	}

	/**
	 * Set servers.
	 * @param servs zookeepers 
	 */
	public static void setServers(final String servs) {
		servers = servs;
	}
	
	/**
	 * get timeout.
	 * @return timeout
	 */
	public static Long getTimeout() {
		return timeout;
	}

	/**
	 * Set timeout.
	 * @param tout expired time
	 */
	public static void setTimeout(final long tout) {
		timeout = tout;
	}
	
	/**
	 * Get pool size.
	 * @return size
	 */
	public static int getPoolSize() {
		return poolSize;
	}

	/**
	 * Set pool size.
	 * @param ps pool size
	 */
	public static void setPoolSize(final int ps) {
		poolSize = ps;
	}
	
}
