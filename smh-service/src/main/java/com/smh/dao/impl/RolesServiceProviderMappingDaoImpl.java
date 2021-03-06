package com.smh.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.smh.dao.BaseDao;
import com.smh.dao.RolesServiceProviderMappingDao;
import com.smh.exception.BeaconServiceException;
import com.smh.util.DateUtil;
import com.smh.util.StringUtil;

/**
*
* << Add Comments Here >>
*
* @author Shekhar Prasad
* @date 21-Dec-2015 10:46:11 AM
* @version 1.0
*/

@Repository("roleServiceMappingDao")
public class RolesServiceProviderMappingDaoImpl extends BaseDao implements RolesServiceProviderMappingDao {
  private  Logger logger = LogManager.getLogger(this.getClass());

 
@SuppressWarnings("deprecation")
@Override
  public Long addRoleServiceProvider(Long roleId, Long serviceProviderId, Long subServiceProviderId, String status,  String createdBy) throws DataAccessException,BeaconServiceException {
    isActiveSubServiceProvider(serviceProviderId, subServiceProviderId);
    logger.info("prepaidservice | RolesServiceProviderMappingDaoImpl | addRoleServiceProvider Entering");
    Long roleServiceProviderId = 0l;
    String sql = "select SEQ_MW_ROLE_SPID_MAP.NEXTVAL from dual";
    roleServiceProviderId = getJdbcTemplate().queryForLong(sql);
    sql = "insert into PREPAID_ROLE_SPID_MAP(ROLE_SPID_MAP_ID, ROLE_ID, SERVICE_PROVIDER_ID, SUBSERVICE_PROVIDER_ID, STATUS,"
          + "CREATED_DATE,UPDATED_DATE, CREATED_BY, UPDATED_BY) " + "values(?,?,?,?,?,?,?,?,?)";
    getJdbcTemplate().setQueryTimeout(99999999);
    int id = getJdbcTemplate().update(sql,
                                      new Object[] { roleServiceProviderId,
                                                    roleId,
                                                    serviceProviderId,
                                                    subServiceProviderId,
                                                    status,
                                                    DateUtil.getCurrentTimestamp(),
                                                    DateUtil.getCurrentTimestamp(),
                                                    createdBy,
                                                    createdBy

                                      });

    return (id > 0) ? roleServiceProviderId : null;
  }

  /*
   * Method to get the service provider details based on service provider
   * @see com.chatak.prepaid.service.dao.RolesServiceProviderMappingDao#
   * getServiceProviderOnRoleId(java.lang.Long)
   */
  @Override
  public List<String> getServiceProviderOnRoleId(Long roleId) throws DataAccessException {
    logger.info("prepaidservice | RolesServiceProviderMappingDaoImpl | getServiceProviderOnRoleId Entering");
    String sql = "select SERVICE_PROVIDER_ID from prepaid_role_spid_map where role_id=?";

    RowMapper<String> rowmapper = new RowMapper<String>() {
      public String mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rs.getString("SERVICE_PROVIDER_ID");
      }
    };
    List<String> serviceProviderList = getJdbcTemplate().query(sql.toString(), new Object[] { roleId }, rowmapper);
    return (StringUtil.isListNotNullNEmpty(serviceProviderList) ? serviceProviderList : null);
  }

  /*
   * (non-Javadoc)
   * @see
   * com.chatak.prepaid.service.dao.RolesServiceProviderMappingDao#getServiceProvider
   * (java.lang.Long)
   */
  @Override
  public Long getServiceProvider(Long roleId) throws DataAccessException {
    logger.info("prepaidservice | RolesServiceProviderMappingDaoImpl | getServiceProvider Entering");

    String sql = "select SERVICE_PROVIDER_ID from prepaid_role_spid_map where role_id=?";
    RowMapper<String> rowmapper = new RowMapper<String>() {
      public String mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rs.getString("SERVICE_PROVIDER_ID");
      }
    };
    List<String> serviceProviderList = getJdbcTemplate().query(sql, new Object[] { roleId }, rowmapper);
    logger.info("prepaidservice | RolesServiceProviderMappingDaoImpl | getServiceProvider Exit");
    return (StringUtil.isListNotNullNEmpty(serviceProviderList) ? Long.parseLong(serviceProviderList.get(0)) : null);

  }

  @Override
  public List<String> getSubServiceProviderOnRoleId(Long roleId) throws DataAccessException {
    logger.info("prepaidservice | RolesServiceProviderMappingDaoImpl | getSubServiceProviderOnRoleId Entering");
    StringBuilder sql = new StringBuilder("select SUBSERVICE_PROVIDER_ID from prepaid_role_spid_map where role_id="
                                          + roleId);
    RowMapper<String> rowmapper = new RowMapper<String>() {
      public String mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rs.getString("SUBSERVICE_PROVIDER_ID");
      }
    };
    List<String> subServiceProviderList = getJdbcTemplate().query(sql.toString(), new Object[] {}, rowmapper);
    logger.info("prepaidservice | RolesServiceProviderMappingDaoImpl | getSubServiceProviderOnRoleId Exiting");
    return (StringUtil.isListNotNullNEmpty(subServiceProviderList)) ? subServiceProviderList : null;
  }

  @Override
  public void deleteServiceProvidermappingData(Long roleId) throws DataAccessException {
    logger.info("prepaidservice | RolesServiceProviderMappingDaoImpl | deleteServiceProvidermappingData Entering");
    String sql = "DELETE FROM prepaid_role_spid_map WHERE role_id = ?";
    try {
      getJdbcTemplate().update(sql, new Object[] { roleId });
    }
    catch(DataAccessException e) {
      logger.error("prepaidservice  | RolesServiceProviderMappingDaoImpl | deleteServiceProvidermappingData |  Exception "
                   + e.getMessage());
    }
    logger.info("prepaidservice | RolesServiceProviderMappingDaoImpl | deleteServiceProvidermappingData Exiting");

  }

  @Override
  public List<String> getServiceProviderNameOnRoleId(Long roleId) throws DataAccessException {
    logger.info("prepaidservice | RolesServiceProviderMappingDaoImpl | getServiceProviderOnRoleId Entering");
    String sql = "select service.NAME as Spname  from prepaid_role_spid_map  sp_map, PREPAID_SERVICE_PROVIDER   service  where service.SERVICE_PROVIDER_ID=sp_map.SERVICE_PROVIDER_ID and sp_map.role_id=?";

    RowMapper<String> rowmapper = new RowMapper<String>() {
      public String mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rs.getString("Spname");
      }
    };
    List<String> serviceProviderList = getJdbcTemplate().query(sql.toString(), new Object[] { roleId }, rowmapper);
    return (StringUtil.isListNotNullNEmpty(serviceProviderList) ? serviceProviderList : null);
  }

  @Override
  public List<String> getSubServiceProviderNameOnRoleId(Long roleId) throws DataAccessException {
    logger.info("prepaidservice | RolesServiceProviderMappingDaoImpl | getServiceProviderOnRoleId Entering");
    String sql = "select service.NAME as Spname from prepaid_role_spid_map  sp_map, prepaid_subservice_provider   service  where service.SUBSERVICE_PROVIDER_ID=sp_map.SUBSERVICE_PROVIDER_ID and sp_map.role_id=?";

    RowMapper<String> rowmapper = new RowMapper<String>() {
      public String mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rs.getString("Spname");
      }
    };
    List<String> serviceProviderList = getJdbcTemplate().query(sql.toString(), new Object[] { roleId }, rowmapper);
    return (StringUtil.isListNotNullNEmpty(serviceProviderList) ? serviceProviderList : null);
  }

  @Override
  public List<String> getServiceProviderOnRoleIdHistory(Long roleId, String action) throws DataAccessException {
    logger.info("prepaidservice | RolesServiceProviderMappingDaoImpl | getServiceProviderOnRoleIdHistory Entering");
    String sql = "select SERVICE_PROVIDER_ID from prepaid_role_spid_map_h where ACTION ='" + action
                 + "' AND  role_id=?";

    RowMapper<String> rowmapper = new RowMapper<String>() {
      public String mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rs.getString("SERVICE_PROVIDER_ID");
      }
    };
    List<String> serviceProviderList = getJdbcTemplate().query(sql.toString(), new Object[] { roleId }, rowmapper);
    return (StringUtil.isListNotNullNEmpty(serviceProviderList) ? serviceProviderList : null);
  }

  @Override
  public Long getServiceProviderHistory(Long roleId, String action) throws DataAccessException {
    logger.info("prepaidservice | RolesServiceProviderMappingDaoImpl | getServiceProviderHistory Entering");

    String sql = "select SERVICE_PROVIDER_ID from prepaid_role_spid_map_h where ACTION ='" + action + "' AND role_id=?";
    RowMapper<String> rowmapper = new RowMapper<String>() {
      public String mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rs.getString("SERVICE_PROVIDER_ID");
      }
    };
    List<String> serviceProviderList = getJdbcTemplate().query(sql, new Object[] { roleId }, rowmapper);
    logger.info("prepaidservice | RolesServiceProviderMappingDaoImpl | getServiceProvider Exit");
    return (StringUtil.isListNotNullNEmpty(serviceProviderList) ? Long.parseLong(serviceProviderList.get(0)) : null);

  }

  @Override
  public List<String> getSubServiceProviderOnRoleIdHistory(Long roleId, String action) throws DataAccessException {
    logger.info("prepaidservice | RolesServiceProviderMappingDaoImpl | getSubServiceProviderOnRoleIdHistory Entering");
    StringBuilder sql = new StringBuilder("select SUBSERVICE_PROVIDER_ID from prepaid_role_spid_map_h where ACTION ='"
                                          + action + "' AND role_id=" + roleId);
    RowMapper<String> rowmapper = new RowMapper<String>() {
      public String mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rs.getString("SUBSERVICE_PROVIDER_ID");
      }
    };
    List<String> subServiceProviderList = getJdbcTemplate().query(sql.toString(), new Object[] {}, rowmapper);
    logger.info("prepaidservice | RolesServiceProviderMappingDaoImpl | getSubServiceProviderOnRoleId Exiting");
    return (StringUtil.isListNotNullNEmpty(subServiceProviderList)) ? subServiceProviderList : null;
  }

}
