package com.smh.dao.impl;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.expr.BooleanExpression;
import com.smh.constants.Constant;
import com.smh.dao.LoginSessionDetailsDao;
import com.smh.dao.model.LoginSessionDetails;
import com.smh.dao.model.QLoginSessionDetails;
import com.smh.dao.repository.LoginSessionDetailsRepository;
import com.smh.model.LoginSessionDetailsRequest;
import com.smh.util.CommonUtil;
import com.smh.util.StringUtil;

/**
*
* << Add Comments Here >>
*
* @author Shekhar Prasad
* @date 21-Dec-2015 10:46:11 AM
* @version 1.0
*/
@Repository("loginSessionDetailsDao")
public class LoginSessionDetailsDaoImpl  implements LoginSessionDetailsDao{
	
	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	LoginSessionDetailsRepository sessionDetailsRepository;
	
	@Override
	public LoginSessionDetails saveOrUpdateSessionDetails(LoginSessionDetails sessionDetails) throws DataAccessException {
		return sessionDetailsRepository.save(sessionDetails);
	}
	
	@Override
	public List<LoginSessionDetailsRequest> searchLoginSessionDetails( LoginSessionDetailsRequest loginSessionDetails) throws InstantiationException, IllegalAccessException {
		List<LoginSessionDetailsRequest> addLoginSessionDetails = new ArrayList<LoginSessionDetailsRequest>();
		
		JPAQuery query = new JPAQuery(entityManager);
		List<LoginSessionDetails> tupleList = query.from(QLoginSessionDetails.loginSessionDetails)
				.where(
						isLoginStatus(loginSessionDetails.getLoginStatus()),
						isPortalType(loginSessionDetails.getPortalType()),
						isSessionId(loginSessionDetails.getSessionId()),
						isUserId(loginSessionDetails.getUserId()))
						.orderBy(orderByLoginTimeDesc())
						.list(QLoginSessionDetails.loginSessionDetails);
		
		addLoginSessionDetails = CommonUtil.copyListBeanProperty(tupleList, LoginSessionDetailsRequest.class);

		return addLoginSessionDetails;
	}
	private BooleanExpression isUserId(Long userId){
	    return userId != null ? QLoginSessionDetails.loginSessionDetails.userId.eq(userId) : null;        
	}
	
	private BooleanExpression isSessionId(String sessionId){
	    return sessionId != null ? QLoginSessionDetails.loginSessionDetails.sessionId.eq(sessionId) : null;        
	}
	
	private BooleanExpression isPortalType(String portalType){
	    return portalType != null ? QLoginSessionDetails.loginSessionDetails.portalType.eq(portalType) : null;        
	}
	
	private BooleanExpression isLoginStatus(String loginStatus){
	    return loginStatus != null ? QLoginSessionDetails.loginSessionDetails.loginStatus.eq(loginStatus) : null;        
	}
	
	private OrderSpecifier<Timestamp> orderByLoginTimeDesc() {
		return QLoginSessionDetails.loginSessionDetails.loginTime.desc();
	}
	
	@Override
	public Boolean updateLoginSessionDetails() throws DataAccessException {
		Query qry = entityManager.createNativeQuery("UPDATE LOGIN_SESSION_DETAILS SET LOGIN_STATUS ='no' WHERE LOGIN_STATUS = 'yes' ");
		int count = qry.executeUpdate();
		return (count > 0 ? Boolean.TRUE: Boolean.FALSE);
	}
	
	@Override
	@Transactional
	public void loginSessionDetailsExpiry(Long expiryMinutes) throws DataAccessException {
		// Update all the login session if the last activity time is less than the expiryMinutes
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -expiryMinutes.intValue());
		Query qry = entityManager.createNativeQuery("UPDATE LOGIN_SESSION_DETAILS SET LOGIN_STATUS =?,LOGOUT_TIME=?  WHERE LOGIN_STATUS=? AND LAST_ACTIVITY_TIME<?");
		qry.setParameter(1, "no");
		qry.setParameter(2, new Timestamp(System.currentTimeMillis()));
		qry.setParameter(3, "yes");
		qry.setParameter(4, cal);
		qry.executeUpdate();
	}

	@Override
	public String getSessionId(LoginSessionDetailsRequest loginSessionDetails) throws DataAccessException, Exception {
		int offset = 0;
		int limit = 0;
		Integer totalRecords = loginSessionDetails.getNoOfRecords();

		if (loginSessionDetails.getPageIndex() == null
				|| loginSessionDetails.getPageIndex() == 1) {
			totalRecords = getTotalNumberOfRecordsSearchCard(loginSessionDetails);
			loginSessionDetails.setNoOfRecords(totalRecords);
		}

		if (loginSessionDetails.getPageIndex() == null
				&& loginSessionDetails.getPageSize() == null) {
			offset = 0;
			limit = Constant.DEFAULT_PAGE_SIZE;
		} else {
			offset = (loginSessionDetails.getPageIndex() - 1)
					* loginSessionDetails.getPageSize();
			limit = loginSessionDetails.getPageSize();
		}
		JPAQuery query = new JPAQuery(entityManager);
		List<String> datas = query.from(QLoginSessionDetails.loginSessionDetails)
				.where(
						isLoginStatus(loginSessionDetails.getLoginStatus()),
						isPortalType(loginSessionDetails.getPortalType()),
						isSessionId(loginSessionDetails.getSessionId()),
						isUserId(loginSessionDetails.getUserId()))
						.offset(offset).limit(limit).orderBy(orderByLoginTimeDesc())
						.list(QLoginSessionDetails.loginSessionDetails.sessionId);
		
		return StringUtil.isListNotNullNEmpty(datas) ? datas.get(0) : null;
	}
	
	private int getTotalNumberOfRecordsSearchCard(LoginSessionDetailsRequest loginSessionDetails) throws Exception {
		JPAQuery query = new JPAQuery(entityManager);
		List<Long> list = query
				.from(QLoginSessionDetails.loginSessionDetails)
				.where(
						isLoginStatus(loginSessionDetails.getLoginStatus()),
						isPortalType(loginSessionDetails.getPortalType()),
						isSessionId(loginSessionDetails.getSessionId()),
						isUserId(loginSessionDetails.getUserId()))
						.list(QLoginSessionDetails.loginSessionDetails.id);

		return (StringUtil.isListNotNullNEmpty(list) ? list.size() : 0);
	}
}
