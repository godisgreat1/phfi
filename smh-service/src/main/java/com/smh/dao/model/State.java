package com.smh.dao.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "STATE")
public class State implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 2364196472687119178L;

  @Id
  @Column(name = "ID")
  private Long id;

  @Column(name = "NAME")
  private String name;

  @Column(name = "STATUS")
  private String status;

  @Column(name = "COUNTRY_ID")
  private Long countryId;

  /**
   * @return the countryId
   */
  public Long getCountryId() {
    return countryId;
  }

  /**
   * @param countryId
   *          the countryId to set
   */
  public void setCountryId(Long countryId) {
    this.countryId = countryId;
  }

  /**
   * @return the id
   */
  public Long getId() {
    return id;
  }

  /**
   * @param id
   *          the id to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name
   *          the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the status
   */
  public String getStatus() {
    return status;
  }

  /**
   * @param status
   *          the status to set
   */
  public void setStatus(String status) {
    this.status = status;
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer("State{");
    sb.append("[id:" + id + "],");
    sb.append("[name:" + name + "],");
    sb.append("}");
    return sb.toString();
  }

}
