package com.smh.rest.service.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.smh.exception.BeaconServiceException;
import com.smh.handler.UserHandler;
import com.smh.model.AdminUserRequest;
import com.smh.model.AdminUserResponse;
import com.smh.model.Response;
import com.smh.rest.service.RestUserService;


@RestController
@RequestMapping(value = "/userService", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class RestUserServiceImpl implements RestUserService {

	private static Logger logger = Logger.getLogger(RestUserServiceImpl.class);
	
	@Autowired
	private UserHandler userHandler;
	
	
	@RequestMapping(value = "/createOrUpdateUser", method = RequestMethod.POST)
	public Response createOrUpdateUser(HttpServletRequest request,
			HttpServletResponse response, HttpSession session,
			@RequestBody AdminUserRequest adminUserRequest) throws BeaconServiceException {
		logger.info("Entering :: RestController :: RestAdminProfileServiceImpl :: createAccountProfile method");
	    Response responseData = userHandler.createOrUpdateUser(adminUserRequest);
	    logger.info("Exiting :: RestController :: RestAdminProfileServiceImpl :: createAccountProfile method");
	    return responseData;
	}

	@RequestMapping(value = "/searchAdminUser", method = RequestMethod.POST)
	public AdminUserResponse searchAdminUser(HttpServletRequest request,
			HttpServletResponse response, HttpSession session,
			@RequestBody AdminUserRequest userRequestTemp) throws BeaconServiceException {
		logger.info("Entering :: RestController :: RestAdminProfileServiceImpl :: createAccountProfile method");
		AdminUserResponse responseData = userHandler.searchAdminUser(userRequestTemp);
	    logger.info("Exiting :: RestController :: RestAdminProfileServiceImpl :: createAccountProfile method");
	    return responseData;
	}

	@RequestMapping(value = "/deleteUser", method = RequestMethod.POST)
	public Response deleteUser(HttpServletRequest request,
			HttpServletResponse response, HttpSession session,@RequestBody Long userId)
			throws BeaconServiceException {
		logger.info("Entering :: RestController :: RestAdminProfileServiceImpl :: createAccountProfile method");
	    Response responseData = userHandler.deleteUser(userId);
	    logger.info("Exiting :: RestController :: RestAdminProfileServiceImpl :: createAccountProfile method");
	    return responseData;
	}
	
	@RequestMapping(value = "/updateUserProfile", method = RequestMethod.POST)
	public Response updateUserProfile(HttpServletRequest request, HttpServletResponse response, HttpSession session, @RequestBody AdminUserRequest adminUserRequest) throws BeaconServiceException {
		logger.info("Entering :: RestController :: RestAdminProfileServiceImpl :: createAccountProfile method");
		Response responseData = userHandler.updateUserProfile(adminUserRequest);
	    logger.info("Exiting :: RestController :: RestAdminProfileServiceImpl :: createAccountProfile method");
	    return responseData;
	}
	
	@RequestMapping(value = "/findByAdminUserId", method = RequestMethod.POST)
	public AdminUserResponse findByAdminUserId(HttpServletRequest request, HttpServletResponse response, HttpSession session, @RequestBody Long adminUserId) throws BeaconServiceException {
		logger.info("Entering :: RestController :: RestAdminProfileServiceImpl :: findByAdminUserId method");
		AdminUserResponse responseData = userHandler.findByAdminUserId(adminUserId);
	    logger.info("Exiting :: RestController :: RestAdminProfileServiceImpl :: findByAdminUserId method");
	    return responseData;
	}
	
}
