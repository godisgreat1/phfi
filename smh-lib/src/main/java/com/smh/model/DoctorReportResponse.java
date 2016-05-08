/**
 * 
 */
package com.smh.model;

import java.util.List;

/**
 *
 * << Add Comments Here >>
 *
 * @author Shekhar Prasad
 * @date Jan 24, 2016 12:16:59 PM
 * @version 1.0
 */
public class DoctorReportResponse extends Response{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2437194397291647863L;

	List<PhfiDoctorFormRequest> doctorReportRequest;

	
	public List<PhfiDoctorFormRequest> getDoctorReportRequest() {
		return doctorReportRequest;
	}

	public void setDoctorReportRequest(
			List<PhfiDoctorFormRequest> doctorReportRequest) {
		this.doctorReportRequest = doctorReportRequest;
	}

	
}
