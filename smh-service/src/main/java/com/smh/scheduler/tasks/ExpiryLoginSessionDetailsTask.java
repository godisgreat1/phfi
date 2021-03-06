package com.smh.scheduler.tasks;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.smh.dao.LoginSessionDetailsDao;
import com.smh.util.Properties;

public class ExpiryLoginSessionDetailsTask {

	@Autowired
	private LoginSessionDetailsDao loginSessionDetailsDao;

	private Logger logger = LogManager.getLogger(this.getClass());

	public void expireLoginSessionsDetails() {
		logger.debug("Entering:: ExpiryLoginSessionDetailsTask:: expireLoginSessionsDetails method");
		Long minutes = Long.parseLong(Properties.getProperty("chatak.scheduler.expireloginsessions.minutes"));
		try {
			loginSessionDetailsDao.loginSessionDetailsExpiry(minutes);
		} catch (Exception e) {
			logger.debug("Error :: ExpiryLoginSessionDetailsTask:: expireLoginSessionsDetails", e);
		}
		logger.debug("Exiting:: ExpiryLoginSessionDetailsTask:: expireLoginSessionsDetails method");
	}
}
