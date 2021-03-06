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
import com.smh.model.ReportRequest;

public class MasterReportDownload {
	private static final Logger logger  = Logger.getLogger(MasterReportDownload.class);
	public static void downloadMasterReportXl(List<ReportRequest> reportRequest, HttpServletResponse response) {

		logger.info("Entering :: MasterReportDownload :: downloadMasterReportXl method");
		response.setContentType("application/vnd.ms-excel");
		Date date = new Date();
		String dateString = new SimpleDateFormat(PHFIWebConstant.EXPORT_FILE_NAME_DATE_FORMAT).format(date);
		String headerDate = new SimpleDateFormat(PHFIWebConstant.EXPORT_HEADER_DATE_FORMAT ).format(date);
		String filename = "Master_Report"+dateString+".xls";
		response.setHeader("Content-Disposition", "attachment;filename="
				+ filename);
		try {
			WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
			WritableSheet s = w.createSheet(Properties.getProperty("phfi.header.registration"), 0);
			 WritableCellFormat cellFormat = new WritableCellFormat();
			
			s.addCell(new Label(0, 0, Properties.getProperty("phfi.header.registration")));
			s.addCell(new Label(0, 2, "Report Date: " + headerDate,cellFormat));
			
			s.addCell(new Label(0, 4, "Woman Name",cellFormat));
			s.addCell(new Label(1, 4, "UID",cellFormat));
			s.addCell(new Label(2, 4, "Days To Deliver",cellFormat));
			s.addCell(new Label(3, 4, "Obstetric Score",cellFormat));
			s.addCell(new Label(4, 4, "Name Of Asha",cellFormat));
			s.addCell(new Label(5, 4, "Age",cellFormat));
			s.addCell(new Label(6, 4, "Caste",cellFormat));
			s.addCell(new Label(7, 4, "Findings",cellFormat));
			s.addCell(new Label(8, 4, "Initial Clinical Assessment",cellFormat));
			s.addCell(new Label(9, 4, "Assessment Status",cellFormat));
			s.addCell(new Label(10, 4, "No Of Visit",cellFormat));
			int j = 5;
			for(ReportRequest reportRequestd : reportRequest){
				int i=0;
				
				s.addCell(new Label(i++, j, ""+((reportRequestd.getFullName()!= null) ? URLDecoder.decode(reportRequestd.getFullName(),"UTF-8")  : " ") + ""));
				s.addCell(new Label(i++, j, ""+((reportRequestd.getWid() != null) ? reportRequestd.getWid() : " ") + ""));
				s.addCell(new Label(i++, j, ""+((reportRequestd.getDaysToDeliver()!= null) ? reportRequestd.getDaysToDeliver() : " ") + ""));
				s.addCell(new Label(i++, j, ""+((reportRequestd.getObstetricScore() != null) ? reportRequestd.getObstetricScore(): " ") + ""));
				s.addCell(new Label(i++, j, ""+((reportRequestd.getNameOfAsha()!= null) ? URLDecoder.decode(reportRequestd.getNameOfAsha(),"UTF-8")  : " ") + ""));
				s.addCell(new Label(i++, j, ""+((reportRequestd.getAge() != null) ? reportRequestd.getAge() : " ") + ""));
				s.addCell(new Label(i++, j, ""+((reportRequestd.getCast()!= null) ? URLDecoder.decode(reportRequestd.getCast(),"UTF-8")  : " ") + ""));
				s.addCell(new Label(i++, j, ""+((reportRequestd.getFindings() != null) ? reportRequestd.getFindings() : " ") + ""));
				s.addCell(new Label(i++, j, ""+((reportRequestd.getClinicalAssesment() != null) ? reportRequestd.getClinicalAssesment() : " ") + ""));
				s.addCell(new Label(i++, j, ""+((reportRequestd.getClinicalStatus() != null) ? reportRequestd.getClinicalStatus() : " ") + ""));
				s.addCell(new Label(i++, j, ""+((reportRequestd.getNoOfVisit() != null) ? reportRequestd.getNoOfVisit() : " ") + ""));
				j = j+1;
			}
			
			w.write();
			w.close();
			response.getOutputStream().flush();
			response.getOutputStream().close();
		} catch (Exception e) {
			logger.error("Error :: MasterReportDownload :: downloadMasterReportXl method",e);
		}
		logger.info("Exiting :: MasterReportDownload :: downloadMasterReportXl method");
 
	}

}
