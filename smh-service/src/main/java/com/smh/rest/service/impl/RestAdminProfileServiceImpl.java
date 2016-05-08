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
import com.smh.handler.AdminProfileHandler;
import com.smh.model.AdminForgotPasswordRequest;
import com.smh.model.AdminUserRequest;
import com.smh.model.AdminUserResponse;
import com.smh.model.ChangeAdminPasswordRequest;
import com.smh.model.Response;
import com.smh.model.SecurityQuestionRequest;
import com.smh.model.SecurityQuestionResponse;
import com.smh.rest.service.RestAdminProfileService;


@RestController
@RequestMapping(value = "/adminProfileService", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class RestAdminProfileServiceImpl implements RestAdminProfileService {

	private static Logger logger = Logger.getLogger(RestAdminProfileServiceImpl.class);
	
	@Autowired
	private AdminProfileHandler adminProfileHandler;
	
	@RequestMapping(value = "/forgotPassword", method = RequestMethod.POST)
	public Response forgotPassword(HttpServletRequest request, HttpServletResponse response, HttpSession session, @RequestBody AdminForgotPasswordRequest adminForgotPasswordRequest) throws BeaconServiceException {
		logger.info("Entering :: RestController :: RestAdminProfileServiceImpl :: createAccountProfile method");
	    Response responseData = adminProfileHandler.forgotPassword(adminForgotPasswordRequest);
	    logger.info("Exiting :: RestController :: RestAdminProfileServiceImpl :: createAccountProfile method");
	    return responseData;
	}
	
	@RequestMapping(value = "/changePassword", method = RequestMethod.POST)
	public Response changePassword(HttpServletRequest request, HttpServletResponse response, HttpSession session, @RequestBody ChangeAdminPasswordRequest changePasswordRequest) throws BeaconServiceException {
		logger.info("Entering :: RestController :: RestAdminProfileServiceImpl :: createAccountProfile method");
	    Response responseData = adminProfileHandler.changePassword(changePasswordRequest);
	    logger.info("Exiting :: RestController :: RestAdminProfileServiceImpl :: createAccountProfile method");
	    return responseData;
	}
	
	@RequestMapping(value = "/getSecurityQuestion", method = RequestMethod.POST)
	public SecurityQuestionResponse getSecurityQuestion(HttpServletRequest request, HttpServletResponse response, HttpSession session, @RequestBody SecurityQuestionRequest securityQuestionRequest) throws BeaconServiceException {
		logger.info("Entering :: RestController :: RestAdminProfileServiceImpl :: createAccountProfile method");
		SecurityQuestionResponse responseData = adminProfileHandler.getSecurityQuestion(securityQuestionRequest);
	    logger.info("Exiting :: RestController :: RestAdminProfileServiceImpl :: createAccountProfile method");
	    return responseData;
	}
	
	@RequestMapping(value = "/updateUserProfile", method = RequestMethod.POST)
	public Response updateUserProfile(HttpServletRequest request, HttpServletResponse response, HttpSession session, @RequestBody AdminUserRequest adminUserRequest) throws BeaconServiceException {
		logger.info("Entering :: RestController :: RestAdminProfileServiceImpl :: createAccountProfile method");
		Response responseData = adminProfileHandler.updateUserProfile(adminUserRequest);
	    logger.info("Exiting :: RestController :: RestAdminProfileServiceImpl :: createAccountProfile method");
	    return responseData;
	}

	@RequestMapping(value = "/findByAdminUserId", method = RequestMethod.POST)
	public AdminUserResponse findByAdminUserId(HttpServletRequest request, HttpServletResponse response, HttpSession session, @RequestBody Long adminUserId) throws BeaconServiceException {
		logger.info("Entering :: RestController :: RestAdminProfileServiceImpl :: findByAdminUserId method");
		AdminUserResponse responseData = adminProfileHandler.findByAdminUserId(adminUserId);
	    logger.info("Exiting :: RestController :: RestAdminProfileServiceImpl :: findByAdminUserId method");
	    return responseData;
	}
}
