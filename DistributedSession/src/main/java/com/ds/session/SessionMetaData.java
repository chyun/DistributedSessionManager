package com.ds.session;

import java.io.Serializable;

/**
 * This class is used to record session's
 * meta data.
 * @author wlq
 *
 */
public class SessionMetaData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	/** session ID.*/
	private String id;

	/**session's create time. */
	private Long createTm;

	/**Max value of session's idle time.*/
	private Long maxIdle;

	/**the last access time of session.*/
	private Long lastAccessTm;

	/**Whether the session is validation or not.*/
	private Boolean validate = false;
	
	/**current version.*/
	private int version = 0;
	
	/**
	 * construct method.*/
	public SessionMetaData() {
		this.createTm = System.currentTimeMillis();
		this.lastAccessTm = this.createTm;
		this.validate = true;
	}

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param sid the id to set
     */
    public void setId(String sid) {
        this.id = sid;
    }

    /**
     * @return the createTm
     */
    public Long getCreateTm() {
        return createTm;
    }

    /**
     * @param createTime the createTm to set
     */
    public void setCreateTm(Long createTime) {
        this.createTm = createTime;
    }

    /**
     * @return the maxIdle
     */
    public Long getMaxIdle() {
        return maxIdle;
    }

    /**
     * @param maxIdleTime the maxIdle to set
     */
    public void setMaxIdle(Long maxIdleTime) {
        this.maxIdle = maxIdleTime;
    }

    /**
     * @return the lastAccessTm
     */
    public Long getLastAccessTm() {
        return lastAccessTm;
    }

    /**
     * @param lastAccessTime the lastAccessTm to set
     */
    public void setLastAccessTm(Long lastAccessTime) {
        this.lastAccessTm = lastAccessTime;
    }

    /**
     * @return the validate
     */
    public Boolean getValidate() {
        return validate;
    }

    /**
     * @param val the validate to set
     */
    public void setValidate(Boolean val) {
        this.validate = val;
    }

    /**
     * @return the version
     */
    public int getVersion() {
        return version;
    }

    /**
     * @param ver the version to set
     */
    public void setVersion(int ver) {
        this.version = ver;
    }

}
