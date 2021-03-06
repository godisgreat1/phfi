package com.smh.handler.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.smh.constants.PhfiErrorCodes;
import com.smh.constants.Constant;
import com.smh.dao.AdminUserDao;
import com.smh.dao.model.AdminUser;
import com.smh.enums.UserPasswordStatus;
import com.smh.exception.BeaconServiceException;
import com.smh.handler.AdminProfileHandler;
import com.smh.mailsender.service.impl.MailServiceManagementImpl;
import com.smh.model.AdminForgotPasswordRequest;
import com.smh.model.AdminUserRequest;
import com.smh.model.AdminUserResponse;
import com.smh.model.ChangeAdminPasswordRequest;
import com.smh.model.Response;
import com.smh.model.SecurityQuestionRequest;
import com.smh.model.SecurityQuestionResponse;
import com.smh.util.CommonUtil;
import com.smh.util.DateUtil;
import com.smh.util.EncryptionUtil;
import com.smh.util.PasswordHandler;
import com.smh.util.Properties;
import com.smh.util.StringUtil;
import com.smh.velocity.IVelocityTemplateCreator;
import com.smh.velocity.impl.VelocityTemplateCreatorImpl;


@Service("adminProfileHandler")
@SuppressWarnings({ "unchecked", "rawtypes" })
public class AdminProfileHandlerImpl implements AdminProfileHandler {
	
	public static Logger logger = Logger.getLogger(AdminProfileHandlerImpl.class);
	
	@Autowired
	private AdminUserDao adminUserDao;
	
	@Autowired
	MailServiceManagementImpl mailServiceManagementImpl;
	
	@Override
	public Response forgotPassword(AdminForgotPasswordRequest adminForgotPasswordRequest) throws BeaconServiceException {
		logger.info("AdminProfileHandlerImpl :::: method  :::: forgotPassword");
		Response response = new Response();
		try {
			List<AdminUser> adminUserList = adminUserDao.findByUserName(adminForgotPasswordRequest.getUserName().toLowerCase());
				if (!StringUtil.isListNotNullNEmpty(adminUserList)) {
					response.setResponseCode(PhfiErrorCodes.INVALID_USER_NAME);
					response.setResponseMessage(Properties.getProperty(response.getResponseCode()));
					return response;
				}
				AdminUser adminUser = adminUserList.get(0);
				 if (!adminForgotPasswordRequest.getSecurityAnswer().equals(EncryptionUtil.decrypt(adminUser.getSecurityAnswer()))) {
					    response.setResponseCode(PhfiErrorCodes.SECURITY_QESTN_INVALD);
						response.setResponseMessage(Properties.getProperty(response.getResponseCode()));
						return response;
				 } else {
					String password = PasswordHandler.getSystemGeneratedPassword(8);
					Timestamp currentDate = new Timestamp(System.currentTimeMillis());
					adminUser.setPassword(PasswordHandler.bCryptEncode(password));
					adminUser.setUpdatedDate(currentDate);
					adminUser.setLoginMode((long)UserPasswordStatus.RESETPASWORD.ordinal());
					logger.info("AdminProfileHandlerImpl :::: method  :::: forgotPassword::before changeAgentPassword");
					adminUserDao.saveOrUpdateAdminUser(adminUser);
					sendEmailNotification(adminUser.getEmail(), adminUser.getFirstName(), password);
					response.setResponseCode(PhfiErrorCodes.PHFI_SUCCESS);
		        	response.setResponseMessage(Properties.getProperty(response.getResponseCode()));
				}
			} catch (Exception e) {
					logger.error("ERROR: AdminProfileHandlerImpl:: forgotPassword method", e);
					response.setResponseCode(PhfiErrorCodes.SYSTEM_ERROR);
					response.setResponseMessage(Properties.getProperty(response.getResponseCode()));
			}
			return response;
	}
	
	@Override
	public Response changePassword(ChangeAdminPasswordRequest changePasswordRequest) throws BeaconServiceException {
		 logger.info("Entering:: AdminProfileHandlerImpl:: changePassword method: ");
		  Response response = new Response();
		 try {
	        	List<AdminUser> aminUserList = adminUserDao.findByUserName(changePasswordRequest.getUserName());
	        	AdminUser adminUser = aminUserList.get(0);
	        	if(adminUser == null) {
	        		response.setResponseCode(PhfiErrorCodes.ADMIN_USR_NOT_ACTIVE_TO_PROCESS);
	        		response.setResponseMessage(Properties.getProperty(response.getResponseCode()));
	        		logger.info("info:: AdminProfileHandlerImpl:::changePassword method: ");
	        		return response;
	        	} 
	        	
	        	if(adminUser.getStatus().equals(Constant.ACTIVE)) {
	        	
	        	if(!PasswordHandler.isValidPassword(changePasswordRequest.getOldPass(),adminUser.getPassword())) {
	            	response.setResponseCode(PhfiErrorCodes.CURRENT_PWD_NOT_VALID);
	            	response.setResponseMessage(Properties.getProperty(response.getResponseCode()));
	            	logger.info("info:: AdminProfileHandlerImpl::samePasswords ::changePassword method: ");
	            	return response;
	            }	
	        		
	        	if(changePasswordRequest.getNewPass().equals(changePasswordRequest.getOldPass())) {
	        		response.setResponseCode(PhfiErrorCodes.PASSWORD_SHOULD_NOT_BE_SAME);
	        		response.setResponseMessage(Properties.getProperty(response.getResponseCode()));
	        		logger.info("info:: AdminProfileHandlerImpl::samePasswords ::changePassword method: ");
	        		return response;
	        	} else if(adminUser != null  && changePasswordRequest.getNewPass().equals(adminUser.getPassword())) {
	        		response.setResponseCode(PhfiErrorCodes.PWD_SHOULD_NOT_BE_SAME_AS_LAST_PWD);
	        		response.setResponseMessage(Properties.getProperty(response.getResponseCode()));
	        		return response;
	        		}  else {
					Boolean flag = true;
					String previousPasswords = adminUser.getPreviousPasswords();
					if (!StringUtil.isNullEmpty(previousPasswords)) {
						StringTokenizer password = new StringTokenizer(previousPasswords, "|");
						while (password.hasMoreElements()) {
							if (PasswordHandler.isValidPassword(changePasswordRequest.getNewPass(), password.nextElement().toString())) {
								response.setResponseCode(PhfiErrorCodes.PWD_SHOULD_NOT_BE_SAME_AS_LAST_PWD);
				        		response.setResponseMessage(Properties.getProperty(response.getResponseCode()));
								flag=false;
								break;
							}
						}
					}
						while (flag) {
							if(!StringUtil.isNullEmpty(previousPasswords)){
								LimitedQueue<String> limitedQueue = new LimitedQueue(3);
								StringTokenizer passwords = new StringTokenizer(previousPasswords, "|");
								while (passwords.hasMoreElements()) {
									 limitedQueue.add(passwords.nextElement().toString());
								}
								limitedQueue.add(PasswordHandler.bCryptEncode(changePasswordRequest.getNewPass()));
								adminUser.setPreviousPasswords(StringUtils.join(limitedQueue, "|"));
							}else{
								LimitedQueue<String> limitedQueue = new LimitedQueue(3);
								limitedQueue.add(PasswordHandler.bCryptEncode(changePasswordRequest.getNewPass()));
								adminUser.setPreviousPasswords(StringUtils.join(limitedQueue, "|"));
							}
						
						if(!StringUtil.isNullEmpty(changePasswordRequest.getSecurityAnswer()) && !StringUtil.isNullEmpty(changePasswordRequest.getSecurityQuestion())) {
							adminUser.setSecurityQuestion(changePasswordRequest.getSecurityQuestion());
							adminUser.setSecurityAnswer(EncryptionUtil.encrypt(changePasswordRequest.getSecurityAnswer()));
						}
							//Timestamp currentDate = new Timestamp(System.currentTimeMillis());
							//update new password
				        	adminUser.setPassword(PasswordHandler.bCryptEncode(changePasswordRequest.getNewPass()));
				        	adminUser.setUpdatedBy(changePasswordRequest.getCreatedBy());
				        	adminUser.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
				        	adminUser.setLoginMode(Long.valueOf(UserPasswordStatus.ACTIVE.ordinal()));
				        	adminUserDao.saveOrUpdateAdminUser(adminUser);
				        	logger.info("info:: AdminProfileHandlerImpl::changePassword ::updating password::changePassword method: ");
				        	response.setResponseCode(PhfiErrorCodes.PHFI_SUCCESS);
				        	response.setResponseMessage(Properties.getProperty(response.getResponseCode()));
				        	break;
							}
						}
	        	 } else {
	        		 response.setResponseCode(PhfiErrorCodes.ACCOUNT_SUSPENDED);
	        		 response.setResponseMessage(Properties.getProperty(response.getResponseCode()));
					}
		 		} catch(Exception e) {
		 			logger.error("ERROR:: AdminProfileHandlerImpl:: changePassword ", e);
		 			response.setResponseCode(PhfiErrorCodes.SYSTEM_ERROR);
					response.setResponseMessage(Properties.getProperty(response.getResponseCode()));
		 		}
		 	return response;
	}
	
	@Override
	public SecurityQuestionResponse getSecurityQuestion(SecurityQuestionRequest securityQuestionRequest) throws BeaconServiceException {
		logger.info("Entering::AdminProfileHandlerImpl :::: method  :::: getSecurityQuestion");
		SecurityQuestionResponse response = new SecurityQuestionResponse();
			try {
				List<AdminUser>  adminUserList = adminUserDao.findByUserName(securityQuestionRequest.getUserName().toLowerCase());
				if(StringUtil.isListNotNullNEmpty(adminUserList)){
					response.setSecurityQuestion(adminUserList.get(0).getSecurityQuestion());
					response.setResponseCode(PhfiErrorCodes.PHFI_SUCCESS);
		        	response.setResponseMessage(Properties.getProperty(response.getResponseCode()));
				} else{
					response.setResponseCode(PhfiErrorCodes.SYSTEM_ERROR);
					response.setResponseMessage(Properties.getProperty(response.getResponseCode()));
				}
			} catch (Exception e) {
				logger.error("ERROR: AdminProfileHandlerImpl:: getSecurityQuestion method", e);
				response.setResponseCode(PhfiErrorCodes.SYSTEM_ERROR);
				response.setResponseMessage(Properties.getProperty(response.getResponseCode()));
			}
			return response;
	}
	
	private void sendEmailNotification(final String toMail, final String FirstName, final String password) {
		try {
			logger.debug("Sending Email to the registered Email: ");
			String subject = Properties.getProperty("chatak.email.admin.changepass.subject");
			String emailBody = readHtmlContent(Properties.getProperty("chatak.email.template.file.path") + File.separator + Properties.getProperty("chatak.email.customer.forgot.password.file"));

			String url = null;
			if (emailBody != null && !emailBody.equals("")) {
			    url = Properties.getProperty("chatak.admin.beacon.user.redirection.url");
				emailBody = MessageFormat.format(emailBody, FirstName, password);
			}
			IVelocityTemplateCreator iVelocityTemplateCreator = new VelocityTemplateCreatorImpl();
			Map<String,String> dataMap = new HashMap<String,String>();
			dataMap.put("firstName", FirstName);
			dataMap.put("tempPassword", password);
			dataMap.put("adminUserUrl", url);
			
			String fromAddress = Properties.getProperty("chatak.from.email.id");
			String mailBodyString = iVelocityTemplateCreator.createEmailTemplate(dataMap, Constant.CREATE_ADMIN_FORGOT_PASSWORD_TEMPLATE);
			mailServiceManagementImpl.sendMailHtml(fromAddress, mailBodyString, toMail, subject);
		} catch (FileNotFoundException e) {
			logger.error("Error in sending Email : ");
			logger.error("ERROR: AdminProfileHandlerImpl:: sendEmailNotification method", e);
		} catch (Exception e) {
			logger.error("Error in sending Email : ");
			logger.error("ERROR: AdminProfileHandlerImpl:: sendEmailNotification method", e);
		}
	}

	/**
	 * method to read the HTML content of the Email Templete
	 * 
	 * @param filePath
	 * @return
	 */
	private String readHtmlContent(String filePath) throws Exception {
		logger.info("Entering:: AdminProfileHandlerImpl:: readHtmlContent method: ");
		String content = null;
		ClassPathResource cpr = new ClassPathResource(filePath);
		InputStream is = cpr.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line;
		StringBuffer result = new StringBuffer();
		while ((line = br.readLine()) != null) {
			result.append(line);
			result.append("\n");
		}
		content = result.toString();
		br.close();
		is.close();
		return content;
	}
	
	class LimitedQueue<E> extends LinkedList<E> {
		private static final long serialVersionUID = -3629542769097625683L;
		private int limit;
	    public LimitedQueue(int limit) {
	        this.limit = limit;
	    }

	    @Override
	    public boolean add(E o) {
	        boolean added = super.add(o);
	        while (added && size() > limit) {
	           super.remove();
	        }
	        return added;
	    }
	}
	
	@Override
	public Response updateUserProfile(AdminUserRequest adminUserRequest) throws BeaconServiceException {
	  	  logger.info("Entering:: AdminProfileHandlerImpl:: updateUserProfile method: ");
	  	  Response response = new Response();
	      try {
	    	  List<AdminUser> adminUsersList = adminUserDao.findByUserEmailId(adminUserRequest.getEmail());
    	      if(StringUtil.isListNotNullNEmpty(adminUsersList)) {
    	        Boolean emailExists = Boolean.FALSE;
    	        for(AdminUser adminUserData : adminUsersList) {
    	          if(adminUserData.getAdminUserId().compareTo(adminUserRequest.getAdminUserId()) != 0) {
    	            emailExists = Boolean.TRUE;
    	            break;
    	          }
    	        }
    	        if(emailExists) {
    	          response.setResponseCode(PhfiErrorCodes.USER_EMAIL_ALRAEDY_EXIST);
    	          response.setResponseMessage(Properties.getProperty(response.getResponseCode()));
    	          logger.error("info:: AdminProfileHandlerImpl:: email already exist:: addUpdateAdminUser method: ");
    	          return response;
    	        }
    	      }
    		  
    		  AdminUser adminUserFromDB = adminUserDao.findByAdminUserId(adminUserRequest.getAdminUserId());
	    	  adminUserFromDB.setFirstName(adminUserRequest.getFirstName());
	    	  adminUserFromDB.setLastName(adminUserRequest.getLastName());
	    	  adminUserFromDB.setPhone(adminUserRequest.getPhone());
	    	  adminUserFromDB.setEmail(adminUserRequest.getEmail());
	    	  adminUserFromDB.setUpdatedDate(DateUtil.getCurrentTimestamp());
	    	  adminUserFromDB.setSecurityQuestion(adminUserRequest.getSecurityQuestion());
	    	  adminUserFromDB.setSecurityAnswer(EncryptionUtil.encrypt(adminUserRequest.getSecurityAnswer()));
	    	  adminUserDao.saveOrUpdateAdminUser(adminUserFromDB);
	    	  response.setResponseCode(PhfiErrorCodes.PHFI_SUCCESS);
	    	  response.setResponseMessage(Properties.getProperty(response.getResponseCode()));
	      } catch(Exception e) {
	    	  logger.error("ERROR: AdminProfileServiceImpl:: updateUserProfile method", e);
	    	  response.setResponseCode(PhfiErrorCodes.SYSTEM_ERROR);
	    	  response.setResponseMessage(Properties.getProperty(response.getResponseCode()));
	      }
	      return response;
	    }
	
	 @Override
		public AdminUserResponse findByAdminUserId(Long adminUserId) {
			logger.info("Entering :: AdminProfileHandlerImpl :: findByAdminUserId method");
			AdminUserResponse response = new AdminUserResponse();
			List<AdminUserRequest> responseList = new ArrayList<AdminUserRequest>();
			try{
				AdminUser adminUser = adminUserDao.findByAdminUserId(adminUserId);
				if(adminUser == null) {
					response.setResponseCode(PhfiErrorCodes.USR_DETAILS_NOT_FOUND);
					response.setResponseMessage(Properties.getProperty(response.getResponseCode()));
					return response;
				}
				AdminUserRequest adminUserRequest = CommonUtil.copyBeanProperties(adminUser, AdminUserRequest.class);
				adminUserRequest.setCreatedDate(null);
				adminUserRequest.setUpdatedDate(null);
				adminUserRequest.setLastPassWordChange(null);
				responseList.add(adminUserRequest);
				response.setAdminUserList(responseList);
				response.setResponseCode(PhfiErrorCodes.PHFI_SUCCESS);
	        	response.setResponseMessage(Properties.getProperty(response.getResponseCode()));
			} catch (Exception e) {
				logger.error("Error :: AdminProfileHandlerImpl :: findByAdminUserId method", e);
				response.setResponseCode(PhfiErrorCodes.SYSTEM_ERROR);
				response.setResponseMessage(Properties.getProperty(response.getResponseCode()));
			}
			logger.info("Exiting :: AdminProfileHandlerImpl :: findByAdminUserId method");
			
			return response;
		}   
}
