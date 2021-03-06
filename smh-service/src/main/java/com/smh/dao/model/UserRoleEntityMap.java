package com.smh.dao.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "USER_ROLE_ENTITY_MAP")
public class UserRoleEntityMap implements Serializable {

	private static final long serialVersionUID = 3225239830872922494L;

	@Id
	@GeneratedValue(strategy =GenerationType.AUTO)
	@Column(name = "ID")
	private Long id;

	@Column(name = "USER_ROLE_ID")
	private Long userRoleId;

	@Column(name = "TOKENIZATION_ID")
	private Long tokenId;
	
	@Column(name = "HCE_ID")
	private Long hceId;
	
	@Column(name = "SPTSM_ID")
	private Long sptsmId;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the userRoleId
	 */
	public Long getUserRoleId() {
		return userRoleId;
	}

	/**
	 * @param userRoleId the userRoleId to set
	 */
	public void setUserRoleId(Long userRoleId) {
		this.userRoleId = userRoleId;
	}

	/**
	 * @return the tokenId
	 */
	public Long getTokenId() {
		return tokenId;
	}

	/**
	 * @param tokenId the tokenId to set
	 */
	public void setTokenId(Long tokenId) {
		this.tokenId = tokenId;
	}

	/**
	 * @return the hceId
	 */
	public Long getHceId() {
		return hceId;
	}

	/**
	 * @param hceId the hceId to set
	 */
	public void setHceId(Long hceId) {
		this.hceId = hceId;
	}

	/**
	 * @return the sptsmId
	 */
	public Long getSptsmId() {
		return sptsmId;
	}

	/**
	 * @param sptsmId the sptsmId to set
	 */
	public void setSptsmId(Long sptsmId) {
		this.sptsmId = sptsmId;
	}
	

}
