package com.smh.util;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.log4j.Logger;

import com.smh.constants.PHFIWebConstant;
import com.smh.model.PhfiRegistrationRequest;

public class RegistrationMngtReport {
	

	private static final Logger logger  = Logger.getLogger(RegistrationMngtReport.class);
	/**
	 * @param phfiRegistrationRequest
	 * @param response
	 */
	public static void downloadRegistrationXl(List<PhfiRegistrationRequest> phfiRegistrationRequest,HttpServletResponse response) {
		logger.info("Entering :: RegistrationMngtReport :: downloadRegistrationXl method");
		response.setContentType("application/vnd.ms-excel");
		Date date = new Date();
		String dateString = new SimpleDateFormat(PHFIWebConstant.EXPORT_FILE_NAME_DATE_FORMAT).format(date);
		String headerDate = new SimpleDateFormat(PHFIWebConstant.EXPORT_HEADER_DATE_FORMAT ).format(date);
		String filename = "Woman_Details"+dateString+".xls";
		response.setHeader("Content-Disposition", "attachment;filename="
				+ filename);
		try {
			WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
			WritableSheet s = w.createSheet(Properties.getProperty("phfi.header.registration"), 0);
			 WritableCellFormat cellFormat = new WritableCellFormat();
			
			s.addCell(new Label(0, 0, Properties.getProperty("phfi.header.registration")));
			s.addCell(new Label(0, 2, "Report Date: " + headerDate,cellFormat));
			s.addCell(new Label(0, 4, "Registration Date",cellFormat));
			s.addCell(new Label(1, 4, "UID",cellFormat));
			s.addCell(new Label(2, 4, "Woman First Name",cellFormat));
			s.addCell(new Label(3, 4, "Woman Last Name",cellFormat));
			s.addCell(new Label(4, 4, "Date of recent delivery",cellFormat));
			s.addCell(new Label(5, 4, "Age",cellFormat));
			s.addCell(new Label(6, 4, "bloodgroup",cellFormat));
			s.addCell(new Label(7, 4, "LMP",cellFormat));
			int j = 5;
			for(PhfiRegistrationRequest registrationRequest : phfiRegistrationRequest){
				int i=0;
				s.addCell(new Label(i++, j, ""+((registrationRequest.getRegDate() != null) ? registrationRequest.getRegDate() : " ") + ""));
				s.addCell(new Label(i++, j, ""+((registrationRequest.getUid() != null) ? registrationRequest.getUid() : " ") + ""));
				s.addCell(new Label(i++, j, ""+((registrationRequest.getWomenFirstName()!= null) ? URLDecoder.decode(registrationRequest.getWomenFirstName(),"UTF-8")  : " ") + ""));
				s.addCell(new Label(i++, j, ""+((registrationRequest.getWomenSurname()!= null) ? URLDecoder.decode(registrationRequest.getWomenSurname(),"UTF-8") : " ") + ""));
				s.addCell(new Label(i++, j, ""+((registrationRequest.getDateOfRecentDelivery()!= null) ? URLDecoder.decode(registrationRequest.getDateOfRecentDelivery(),"UTF-8") : " ") + ""));
				s.addCell(new Label(i++, j, ""+((registrationRequest.getAge() != null) ? registrationRequest.getAge(): " ") + ""));
				s.addCell(new Label(i++, j, ""+((registrationRequest.getBloodgroup() != null) ? URLDecoder.decode(registrationRequest.getBloodgroup(),"UTF-8") : " ") + ""));
				s.addCell(new Label(i++, j, ""+((registrationRequest.getLmp()!= null) ? URLDecoder.decode(registrationRequest.getLmp(),"UTF-8") : " ") + ""));
				j = j+1;
			}
			
			w.write();
			w.close();
			response.getOutputStream().flush();
			response.getOutputStream().close();
		} catch (Exception e) {
			logger.error("Error :: RegistrationMngtReport :: downloadRegistrationXl method",e);
		}
		logger.info("Exiting :: RegistrationMngtReport :: downloadRegistrationXl method");
 
		
	}


}
