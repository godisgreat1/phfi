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

public class AshaReportDownload {

	private static final Logger logger  = Logger.getLogger(AshaReportDownload.class);
	public static void downloadAshaReportXl(List<ReportRequest> reportRequest, HttpServletResponse response) {

		logger.info("Entering :: AshaReportDownload :: downloadAshaReportXl method");
		response.setContentType("application/vnd.ms-excel");
		Date date = new Date();
		String dateString = new SimpleDateFormat(PHFIWebConstant.EXPORT_FILE_NAME_DATE_FORMAT).format(date);
		String headerDate = new SimpleDateFormat(PHFIWebConstant.EXPORT_HEADER_DATE_FORMAT ).format(date);
		String filename = "Asha_Feedback_Report"+dateString+".xls";
		response.setHeader("Content-Disposition", "attachment;filename="
				+ filename);
		try {
			WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
			WritableSheet s = w.createSheet(Properties.getProperty("phfi.header.asha.feedback.report"), 0);
			 WritableCellFormat cellFormat = new WritableCellFormat();
			
			s.addCell(new Label(0, 0, Properties.getProperty("phfi.header.asha.feedback.report")));
			s.addCell(new Label(0, 2, "Report Date: " + headerDate,cellFormat));
			
			s.addCell(new Label(0, 4, "Woman Name",cellFormat));
			s.addCell(new Label(1, 4, "UID",cellFormat));
			s.addCell(new Label(2, 4, "Days To Deliver",cellFormat));
			s.addCell(new Label(3, 4, "Findings",cellFormat));
			s.addCell(new Label(4, 4, "Initial Clinical Assessment",cellFormat));
			s.addCell(new Label(5, 4, "Assessment Status",cellFormat));
			int j = 5;
			for(ReportRequest reportRequestd : reportRequest){
				int i=0;
				
				s.addCell(new Label(i++, j, ""+((reportRequestd.getFullName()!= null) ? URLDecoder.decode(reportRequestd.getFullName(),"UTF-8")  : " ") + ""));
				s.addCell(new Label(i++, j, ""+((reportRequestd.getWid() != null) ? reportRequestd.getWid() : " ") + ""));
				s.addCell(new Label(i++, j, ""+((reportRequestd.getDaysToDeliver()!= null) ? reportRequestd.getDaysToDeliver() : " ") + ""));
				s.addCell(new Label(i++, j, ""+((reportRequestd.getFindings() != null) ? reportRequestd.getFindings() : " ") + ""));
				s.addCell(new Label(i++, j, ""+((reportRequestd.getClinicalAssesment() != null) ? reportRequestd.getClinicalAssesment() : " ") + ""));
				s.addCell(new Label(i++, j, ""+((reportRequestd.getClinicalStatus() != null) ? reportRequestd.getClinicalStatus() : " ") + ""));
				j = j+1;
			}
			
			w.write();
			w.close();
			response.getOutputStream().flush();
			response.getOutputStream().close();
		} catch (Exception e) {
			logger.error("Error :: AshaReportDownload :: downloadAshaReportXl method",e);
		}
		logger.info("Exiting :: AshaReportDownload :: downloadAshaReportXl method");
 
	}



}
