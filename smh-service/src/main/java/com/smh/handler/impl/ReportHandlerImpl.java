/**
 * 
 */
package com.smh.handler.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smh.constants.Condition;
import com.smh.constants.Constant;
import com.smh.constants.PhfiErrorCodes;
import com.smh.dao.DeliveryDao;
import com.smh.dao.RegistrationDao;
import com.smh.dao.ReportDao;
import com.smh.dao.SymptomDao;
import com.smh.dao.model.Delivery;
import com.smh.dao.model.Doctor;
import com.smh.dao.model.PostpartumVisit;
import com.smh.dao.model.PregnancyVisit;
import com.smh.dao.model.Registration;
import com.smh.dao.model.Symptom;
import com.smh.handler.ReportHandler;
import com.smh.model.DoctorReportResponse;
import com.smh.model.MedicalCaseSheetDTO;
import com.smh.model.PhfiDeliveryFormRequest;
import com.smh.model.PhfiDeliveryFormResponse;
import com.smh.model.PhfiDoctorFormRequest;
import com.smh.model.PhfiPostPartumVisitRequest;
import com.smh.model.PhfiPostPartumVisitResponse;
import com.smh.model.PhfiRegistrationRequest;
import com.smh.model.PhfiRegistrationResponse;
import com.smh.model.PhfiVisitRequest;
import com.smh.model.PhfiVisitResponse;
import com.smh.model.ReportRequest;
import com.smh.model.ReportResponse;
import com.smh.util.CommonUtil;
import com.smh.util.Properties;
import com.smh.util.StringUtil;

/**
 *
 * << Add Comments Here >>
 *
 * @author Shekhar Prasad
 * @date Jan 24, 2016 12:29:07 PM
 * @version 1.0
 */
@Service("reportHandler")
public class ReportHandlerImpl implements ReportHandler{

	public static Logger logger = Logger.getLogger(ReportHandlerImpl.class);
	
	@Autowired
	private ReportDao reportDao;
	
	@Autowired
	private SymptomDao symptomDao;
	
	@Autowired
	private RegistrationDao registrationDao;
	
	@Autowired
	private DeliveryDao deleviryDao;
	
	
	
	/**
	 * @param reportRequest
	 * @return ReportResponse
	 */
	
	
	
	@Override
	public ReportResponse getMasterReport(ReportRequest reportRequest) {
		logger.info("Entering :: RegistrationHandlerImpl :: createRegistration method");
		ReportResponse response = new ReportResponse();
		try {
			List<Registration> registrationList = reportDao.getReport(reportRequest);
			if(!StringUtil.isListNotNullNEmpty(registrationList)){
				response.setResponseCode(PhfiErrorCodes.REPORT_NOT_FOUND);
				response.setResponseMessage(Properties.getProperty(response.getResponseCode()));
				return response;
			}
			
			//get All Symptoms
			HashMap<String,String> symptom =new HashMap<String,String>();
			List<Symptom> symptomList = symptomDao.findAll();
			if(StringUtil.isListNotNullNEmpty(symptomList)){
				for(Symptom symptomData :symptomList){
					symptom.put(symptomData.getSymptomId(),symptomData.getSymptomPhrases());
				}
			}
			List<ReportRequest> reportList = new ArrayList<ReportRequest>();
			for(Registration registration :registrationList){
				
				ReportRequest request = new ReportRequest();
				request.setSlNo(registration.getId());
				request.setFullName(registration.getWomenFirstName()+" "+registration.getWomenSurname());
				request.setWid(registration.getUid());
				
				request.setDaysToDeliver(getDaysToDeliver(registration));
				request.setObstetricScore(getObstetricScore(registration));
				request.setNameOfAsha(registration.getAshaName());
				request.setAge(registration.getAge());
				request.setCast(registration.getCaste());
				request.setVillageName(registration.getVillageName());
				
				try {
					String findingAssessVisit[] = getFindings(registration,symptom).split(":");
					request.setFindings(findingAssessVisit[0]);
					request.setClinicalAssesment(findingAssessVisit[1]);
					request.setNoOfVisit(findingAssessVisit[2]);
					/*if(null !=registration.getStatus() && "" !=registration.getStatus()){
					request.setClinicalStatus(registration.getStatus());
					}else{
						request.setClinicalStatus(findingAssessVisit[8]);
					}
					if(null ==registration.getStatus() || "" ==registration.getStatus()){*/
					request.setClinicalStatus(findingAssessVisit[8]);
					registration.setStatus(findingAssessVisit[8]);
					registrationDao.createRegistration(registration);
					//}
				} catch (Exception e) {
					logger.error("Error :: RegistrationHandlerImpl :: getFindings  method", e);
				}
				
				reportList.add(request);
			}
			response.setReportRequest(reportList);
			response.setNoOfRecords(reportRequest.getNoOfRecords());
			response.setResponseCode(PhfiErrorCodes.PHFI_SUCCESS);
			response.setResponseMessage(Properties.getProperty(response.getResponseCode()));
		} catch (Exception e) {
			logger.error("Error :: RegistrationHandlerImpl :: createRegistration method", e);
			/*response.setResponseCode(PhfiErrorCodes.SYSTEM_ERROR);
			response.setResponseMessage(Properties.getProperty(PhfiErrorCodes.SYSTEM_ERROR));*/
		}
		logger.info("Exiting :: RegistrationHandlerImpl :: createRegistration method");

		return response;
	}

	/**
	 * @param registration
	 * @return
	 */
	private Integer getDaysToDeliver(Registration registration) {
		logger.info("Entering :: RegistrationHandlerImpl :: getDaysToDeliver method");
		Integer days=null;
		try {
		String lmp = registration.getLmp();
		Date date =new Date();
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		String currentDate = df.format(date);
		
			Date lmpDate = df.parse(lmp);
			Date sysDate = df.parse(currentDate);
			Long diff = (lmpDate.getTime() - sysDate.getTime())/(1000 * 60 * 60 * 24);
			 days = (int) (diff + 280);
		} catch (ParseException e) {
			logger.error("Error :: RegistrationHandlerImpl :: getDaysToDeliver method", e);
		}
		return days;
	}

	/**
	 * @param registration
	 * @return obstetricScore
	 */
	private String getObstetricScore(Registration registration) {
		logger.info("Entering :: RegistrationHandlerImpl :: getObstetricScore method");
		Delivery delivery = deleviryDao.getDeliveryDetailByWid(registration.getUid());
		
		String obstetricScore="";
		try {
			int g =0;
			int l =0;
			int a =0;
			if(null !=registration.getPregnancyCount()){
				g = registration.getPregnancyCount();
			}
			if(null !=registration.getNoOfChildren()){
				l = registration.getNoOfChildren();
			}
			if(null !=registration.getEarlyDelivery()){
				a = registration.getEarlyDelivery();
			}
			int p = g-a-1;
			if("Pregnant".equalsIgnoreCase(registration.getMaternityStatus())){
				obstetricScore = "G"+g+"P"+p+"A"+a+"L"+l;
			}
			List<PostpartumVisit> postpartumList = reportDao.findByPWid(registration.getUid());
			if(postpartumList.size()>0){
				p = g-a;
				obstetricScore = "P"+p+"A"+a+"L"+l;
			}
			if(null != delivery ){
				if (null != delivery.getPregnancyLast() && "" != delivery.getPregnancyLast()){
					if (Integer.parseInt(delivery.getPregnancyLast()) < 7) {
						p =g-a-1;
					}else if(Integer.parseInt(delivery.getPregnancyLast()) >= 7){
						p=g-a;
					}
					if (Integer.parseInt(delivery.getPregnancyLast()) >= 7 && Constant.Yes.equalsIgnoreCase(delivery.getIsBabyAlive())) {
						l = l+1;
					}
				}
				obstetricScore = "P"+p+"A"+a+"L"+l;
			}
			
			
			
		} catch (Exception e) {
			logger.error("Error :: RegistrationHandlerImpl :: getObstetricScore method", e);
		}
		return obstetricScore;
	}

	/**
	 * @param registration
	 * @param symptom 
	 * @return
	 */
	private String getFindings(Registration registration, HashMap<String, String> symptom) {
		
		logger.info("Entering :: RegistrationHandlerImpl :: getFindings method");
		StringBuilder findings = new StringBuilder();
		/*StringBuilder clinicalAsses = new StringBuilder();*/
		Set<String> clinicalAsses = new HashSet<>();
		List<String> testresult=null;
		String visitDate ="";
		String genralExamination = "";
		String bpHistory ="";
		String weightHistory = "";
		ArrayList<String> assesmentStatus = new ArrayList<String>();
		List<PregnancyVisit> pregnancyList = reportDao.findByWid(registration.getUid());
		
		List<PostpartumVisit> postpartumList = reportDao.findByPWid(registration.getUid());
		
		int noOfVisit =pregnancyList.size() + postpartumList.size();
		String visitType =Constant.PREGNANT;
		
		if(StringUtil.isListNotNullNEmpty(pregnancyList) && StringUtil.isListNotNullNEmpty(postpartumList)){
			String pregnancyVisitDate = pregnancyList.get(0).getVisitDate();
			String postpartumVistsDate = postpartumList.get(0).getVisitDate();
			
			visitDate = pregnancyVisitDate;
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			try {
				Date preDate = df.parse(pregnancyVisitDate);
				Date postDate = df.parse(postpartumVistsDate);
				if(postDate.after(preDate)){
					visitType =Constant.POSTPARTUM;
					visitDate = postpartumVistsDate;
				}
			} catch (ParseException e) {
				logger.error("Error :: RegistrationHandlerImpl :: getFindings method", e);
			}
		}else if(StringUtil.isListNotNullNEmpty(postpartumList)){
			visitType =Constant.POSTPARTUM;
			visitDate = postpartumList.get(0).getVisitDate();
		}else if(StringUtil.isListNotNullNEmpty(pregnancyList)){
			visitType =Constant.PREGNANT;
			visitDate =  pregnancyList.get(0).getVisitDate();
		}else{
			visitType="";
		}
		
		if(Constant.PREGNANT.equalsIgnoreCase(visitType)){
			PregnancyVisit pregnancyVisit = pregnancyList.get(0);
			//get all symptom phrases
			//check have fever
			if(Constant.Yes.equalsIgnoreCase(pregnancyVisit.getHaveFever())){
				if(Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsFeverAssocated())){
					if(Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsFeverComeAndGo())){
						findings.append(symptom.get("PHRASES1"));
						findings.append(", ");
					}else{
						findings.append(symptom.get("PHRASES2"));
						findings.append(", ");
					}
				}else if(Constant.No.equalsIgnoreCase(pregnancyVisit.getIsFeverAssocated())){
					
					if(Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsFeverComeAndGo())){
						findings.append(symptom.get("PHRASES3"));
						findings.append(", ");
					}else{
						findings.append(symptom.get("PHRASES4"));
						findings.append(", ");
					}
					
				}
				
			}/*else{
				findings.append(symptom.get("PHRASES5"));
				findings.append(", ");
			}*/
			//check feel tired
			if(Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsFeelTired())){
				findings.append(symptom.get("PHRASES6"));
				findings.append(", ");
			}/*else if(Constant.No.equalsIgnoreCase(pregnancyVisit.getIsFeelTired())){
				findings.append(symptom.get("PHRASES7"));
				findings.append(", ");
			}else{
				//TODO Nothing
			}*/
			//check have feet
			
			if(Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsFits())){
				findings.append(symptom.get("PHRASES8"));
				findings.append(", ");
			}/*else if(Constant.No.equalsIgnoreCase(pregnancyVisit.getIsFits())){
				findings.append(symptom.get("PHRASES9"));
				findings.append(", ");
			}else{
				//TODO Nothing
			}*/
			//check have consciousness
			if(Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsConsciousness())){
				findings.append("h/o loss of cosciousness");
				findings.append(", ");
			}/*else if(Constant.No.equalsIgnoreCase(pregnancyVisit.getIsConsciousness())){
				findings.append(symptom.get("PHRASES11"));
				findings.append(", ");
			}else{
				//TODO Nothing
			}*/
			//check have felt giddy
			if(Constant.Yes.equalsIgnoreCase(pregnancyVisit.getFeltGiddy())){
				findings.append(symptom.get("PHRASES12"));
				findings.append(", ");
			}/*else if(Constant.No.equalsIgnoreCase(pregnancyVisit.getFeltGiddy())){
				findings.append(symptom.get("PHRASES13"));
				findings.append(", ");
			}else{
				//TODO Nothing
			}*/
			
			//check have headaches
			if(Constant.Yes.equalsIgnoreCase(pregnancyVisit.getHaveHeadaches())){
				findings.append(symptom.get("PHRASES14"));
				findings.append(", ");
			}/*else if(Constant.No.equalsIgnoreCase(pregnancyVisit.getHaveHeadaches())){
				findings.append(symptom.get("PHRASES15"));
				findings.append(", ");
			}else{
				//TODO Nothing
			}
			*/
			//check have Blurred Vision
			if(Constant.Yes.equalsIgnoreCase(pregnancyVisit.getHaveBlurredVision())){
				findings.append(symptom.get("PHRASES16"));
				findings.append(", ");
			}/*else if(Constant.No.equalsIgnoreCase(pregnancyVisit.getHaveBlurredVision())){
				findings.append(symptom.get("PHRASES17"));
				findings.append(", ");
			}else{
				//TODO Nothing
			}*/
			
			//check have breathless
			if(Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsBreathless())){
				if(pregnancyVisit.getWhenBreathless().contains(Constant.SITTING)){
					findings.append(symptom.get("PHRASES18"));
					findings.append(", ");
				}
				if(pregnancyVisit.getWhenBreathless().contains(Constant.COOKING)){
					findings.append(symptom.get("PHRASES19"));
					findings.append(", ");
				}
				if(pregnancyVisit.getWhenBreathless().contains(Constant.CARRING_LOAD)){
					findings.append(symptom.get("PHRASES20"));
					findings.append(", ");
				}
				
			}/*else if(Constant.No.equalsIgnoreCase(pregnancyVisit.getIsBreathless())){
				findings.append(symptom.get("PHRASES21"));
				findings.append(", ");
			}else{
				//TODO Nothing
			}*/
			
			//check cough
			if(Constant.Yes.equalsIgnoreCase(pregnancyVisit.getHaveCough())){
				if(pregnancyVisit.getHowLongHaveCough().equalsIgnoreCase(Constant.LESS_THEN_THREE_WEEK)){
					findings.append(symptom.get("PHRASES22"));
					findings.append(", ");
				}else if(pregnancyVisit.getHowLongHaveCough().equalsIgnoreCase(Constant.MORE_THEN_THREE_WEEK)){
					findings.append(symptom.get("PHRASES23"));
					findings.append(", ");
				}else{
					//TODO nothing
				}
				
			}/*else if(Constant.No.equalsIgnoreCase(pregnancyVisit.getHaveCough())){
				findings.append(symptom.get("PHRASES23"));
				findings.append(", ");
			}*/else{
				//TODO nothing
			}
			
			//check for abnormal pain
			if(Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsAbdominalPain())){
				/*if(pregnancyVisit.getWherePain().equalsIgnoreCase(Constant.UPPER)){
					findings.append(symptom.get("PHRASES24"));
					findings.append(", ");
				}else*/
				if(pregnancyVisit.getWherePain().equalsIgnoreCase(Constant.UPPER)){
					findings.append(symptom.get("PHRASES25"));
					findings.append(", ");
				}else if(pregnancyVisit.getWherePain().equalsIgnoreCase(Constant.LOWER)){
					findings.append(symptom.get("PHRASES26"));
					findings.append(", ");
				}else if(pregnancyVisit.getWherePain().equalsIgnoreCase(Constant.ALL_OVER)){
					findings.append(symptom.get("PHRASES27"));
					findings.append(", ");
				}
			}/*else{
				findings.append(symptom.get("PHRASES27"));
				findings.append(", ");
			}*/
			int gestationalAge =getGestationalAgePregnancy(registration,pregnancyVisit);
			//check for baby move
			if(Constant.TWO_EIGHT <= gestationalAge){
				if(Constant.No.equalsIgnoreCase(pregnancyVisit.getBabyMove())){
					findings.append(symptom.get("PHRASES30"));
					findings.append(", ");
				}
			}
			/*else{
				findings.append(symptom.get("PHRASES29"));
				findings.append(", ");
			}*/
			//check for verginal discharge
			if(Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsVaginalDischarge())){
				findings.append(symptom.get("PHRASES31"));
				findings.append(", ");
			}/*else{
				findings.append(symptom.get("PHRASES31"));
				findings.append(", ");
			}*/
			
			//check for bleeding
			if(Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsBleeding())){
				if(Constant.SPOTTING.contains(pregnancyVisit.getKindOfBleeding())){
					findings.append(symptom.get("PHRASES33"));
					findings.append(", ");
				}else if(Constant.PERIOD_LIKE.contains(pregnancyVisit.getKindOfBleeding())){
					findings.append(symptom.get("PHRASES34"));
					findings.append(", ");
				}else if(Constant.TAP_LIKE.contains(pregnancyVisit.getKindOfBleeding())){
					findings.append(symptom.get("PHRASES35"));
					findings.append(", ");
				}else{
				//TODO Nothing	
				}
			}/*else{
				findings.append(symptom.get("PHRASES35"));
				findings.append(", ");
			}*/
			
			//check for water broken
			/*if(Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsWaterBroken())){
				findings.append(symptom.get("PHRASES36"));
				findings.append(", ");
			}else*/ 
			if(Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsWaterBroken())){
				findings.append(symptom.get("PHRASES37"));
				findings.append(", ");
			}else{
				//TODO Nothing
			}
			
			//check burning pain while unirating
			/*if(Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsBurningPain())){
				findings.append(symptom.get("PHRASES38"));
				findings.append(", ");
			}else*/ 
			if(Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsBurningPain())){
				findings.append(symptom.get("PHRASES39"));
				findings.append(", ");
			}else{
				//TODO Nothing
			}
			//check for toe ring tigher
			/*if(Constant.Yes.equalsIgnoreCase(pregnancyVisit.getToeRingsTighter())){
				findings.append(symptom.get("PHRASES40"));
				findings.append(", ");
			}else*/ 
			if(Constant.Yes.equalsIgnoreCase(pregnancyVisit.getToeRingsTighter())){
				findings.append(symptom.get("PHRASES41"));
				findings.append(", ");
			}else{
				//TODO Nothing
			}
			//check for bangless
			/*if(Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsBangles())){
				findings.append(symptom.get("PHRASES42"));
				findings.append(", ");
			}else*/ 
			if(Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsBangles())){
				findings.append(symptom.get("PHRASES43"));
				findings.append(", ");
			}else{
				//TODO Nothing
			}
			
			//check tailkin illogical
			/*if(Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsTalking())){
				findings.append(symptom.get("PHRASES44"));
				findings.append(", ");
			}else*/
			if(Constant.Yes.equalsIgnoreCase(pregnancyVisit.getTalkingIrrelevantly())){
				findings.append(symptom.get("PHRASES45"));
				findings.append(", ");
			}else{
				//TODO Nothing
			}
			
			//check out of breath
			//as disscussed with srinidhi sign phase should not print in finding
			/*
			if(Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsoutOfBreath())){
				findings.append(symptom.get("PHRASES47"));
				findings.append(", ");
			}*/
			
			//check for talking irrelevantly
			/*if(Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsTalking())){
				findings.append(symptom.get("PHRASES47"));
				findings.append(", ");
			}*/
			
			//check for talking irrelevantly
			/*
			if(Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsTalking())){
				findings.append(symptom.get("PHRASES48"));
				findings.append(", ");
			}*/
			
			//check for upper eye color
			/*if(Constant.YELLOW.equalsIgnoreCase(pregnancyVisit.getUpperEyeColor())){
				findings.append(symptom.get("PHRASES49"));
				findings.append(", ");
			}*/
			//check for lower eye color
			/*
			if(Constant.PALE.equalsIgnoreCase(pregnancyVisit.getLowerEyeColor())){
				findings.append(symptom.get("PHRASES50"));
				findings.append(", ");
			}*/
			
			//check for Ankle Depression
			/*
			if(Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsAnkleDepression())){
				findings.append(symptom.get("PHRASES51"));
				findings.append(", ");
			}*/
			//check for Eye Swelling
			/*
			if(Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsEyeSwelling())){
				findings.append(symptom.get("PHRASES52"));
			}
			*/
			// code added for clinical assessment
			
			//get Gestational Age in week
			
			
			if ((Constant.Yes.equalsIgnoreCase(pregnancyVisit.getHaveFever()) || Constant.Yes.equalsIgnoreCase(pregnancyVisit.getHaveHeadaches()))){
				if((Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsFits()) || Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsTalking()))) {
					clinicalAsses.add(Condition.CVT);
					assesmentStatus.add(Condition.EMERGENCY);
				}
			}
			
			String bp[] = pregnancyVisit.getFirstBp().split("/");
			Boolean bpCheck =false;
			if(bp.length ==2){
				if(Integer.parseInt(bp[0]) > 160 && Integer.parseInt(bp[1]) > 110 ){
					bpCheck =true;
				}
			}
			
			if (Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsAnkleDepression()) || !"Nil".equalsIgnoreCase(pregnancyVisit.getFirstUrine()) || bpCheck){
				if( Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsFits()) || Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsConsciousness()) ) {
				clinicalAsses.add(Condition.ECLAMPSIA);
				assesmentStatus.add(Condition.HIGH);
				// servrity high
				}
			}
			if (Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsAnkleDepression())|| "Traces".equalsIgnoreCase(pregnancyVisit.getFirstUrine()) ||
					"1+".equalsIgnoreCase(pregnancyVisit.getFirstUrine()) || "2+".equalsIgnoreCase(pregnancyVisit.getFirstUrine()) || "3+".equalsIgnoreCase(pregnancyVisit.getFirstUrine())){
					if(Constant.Yes.equalsIgnoreCase(pregnancyVisit.getHaveHeadaches())
					|| Constant.Yes.equalsIgnoreCase(pregnancyVisit.getHaveBlurredVision())
					|| Constant.UPPER.equalsIgnoreCase(pregnancyVisit.getWherePain())) {
				clinicalAsses.add(Condition.IMMINENT_ECLAMPSIA);
				assesmentStatus.add(Condition.HIGH);
					}
			}
			
			
			if(Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsEyeSwelling()) || "3+".equalsIgnoreCase(pregnancyVisit.getFirstUrine()) || bpCheck){
				clinicalAsses.add(Condition.IMMINENT_ECLAMPSIA);
				assesmentStatus.add(Condition.HIGH);
			}
			if(pregnancyVisit.getFirstBp()!=""){
				if( Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsAnkleDepression()) && Integer.parseInt(bp[0]) <= 160 && Integer.parseInt(bp[0]) >= 140 && Integer.parseInt(bp[1]) <= 110 && Integer.parseInt(bp[1]) >= 90 &&  !"Nil".equalsIgnoreCase(pregnancyVisit.getFirstUrine())){
					clinicalAsses.add(Condition.PRE_ECLAMPSIA);
					assesmentStatus.add(Condition.LOW);
				}
			}
			
			boolean servAnemiaFailureEmer = false;
			boolean servAnemiaFailureHigh = false;
			boolean anaemia =false;
			if (pregnancyVisit.getFirstHb() != "") {
				double hb = Double.parseDouble(pregnancyVisit.getFirstHb());

				if (hb < 11 && hb > 7 && Constant.PALE.equalsIgnoreCase(pregnancyVisit.getLowerEyeColor())) {
					/*clinicalAsses.add(Condition.ANAEMIA);
					assesmentStatus.add(Condition.LOW);*/
					anaemia=true;
				}

				if (Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsFeelTired()) || Constant.PALE.equalsIgnoreCase(pregnancyVisit.getLowerEyeColor())
						|| Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsAnkleDepression())){
						if(hb < 7 && Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsBreathless())){
							/*clinicalAsses.add(Condition.SEVERE_ANEMIA);
							assesmentStatus.add(Condition.EMERGENCY);*/
							servAnemiaFailureEmer = true;
						}else if(Constant.COOKING.equalsIgnoreCase(pregnancyVisit.getWhenBreathless())|| Constant.Yes.equalsIgnoreCase(pregnancyVisit.getHaveCough())) {
							/*clinicalAsses.add(Condition.SEVERE_ANEMIA);
							assesmentStatus.add(Condition.EMERGENCY);*/
							servAnemiaFailureEmer = true;
						}
					}
			}
			
			if (Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsFeelTired()) && Constant.PALE.equalsIgnoreCase(pregnancyVisit.getLowerEyeColor()) 
					&& Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsAnkleDepression()) && Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsBreathless())){
					/*clinicalAsses.add(Condition.SEVERE_ANEMIA);
					assesmentStatus.add(Condition.HIGH);*/
				servAnemiaFailureHigh = true;
				}else if(Constant.COOKING.equalsIgnoreCase(pregnancyVisit.getWhenBreathless())|| Constant.Yes.equalsIgnoreCase(pregnancyVisit.getHaveCough())) {
					/*clinicalAsses.add(Condition.SEVERE_ANEMIA);
					assesmentStatus.add(Condition.HIGH);*/
					servAnemiaFailureHigh = true;
			}
			
			
			if(Constant.No.equalsIgnoreCase(pregnancyVisit.getHaveFever()) && Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsBurningPain())){
				clinicalAsses.add(Condition.UTI);
				assesmentStatus.add(Condition.LOW);
				//low serverity
			}
			
			if(Constant.Yes.equalsIgnoreCase(pregnancyVisit.getHaveFever()) && Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsBurningPain())){
				clinicalAsses.add(Condition.UTI);
				assesmentStatus.add(Condition.HIGH);
				//high serverity
			}
			if (Constant.Yes.equalsIgnoreCase(pregnancyVisit.getHaveFever())
					&& Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsAbdominalPain())
					&& Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsTalking())) {
				clinicalAsses.add(Condition.SEPSIS);
				assesmentStatus.add(Condition.HIGH);
				// high serverity
			}
			
			if(Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsVaginalDischarge()) && Constant.No.equalsIgnoreCase(pregnancyVisit.getHaveFever()) && Constant.No.equalsIgnoreCase(pregnancyVisit.getIsAbdominalPain())){
				clinicalAsses.add(Condition.RTI);
				assesmentStatus.add(Condition.LOW);
			}
			
			
			
			boolean isEctopicAbortion = false;
			boolean isEctopicAbortionHigh = false;
			if(Constant.LOWER.equalsIgnoreCase(pregnancyVisit.getWherePain()) && Constant.SPOTTING.equalsIgnoreCase(pregnancyVisit.getKindOfBleeding())
					&& gestationalAge < Constant.FOURTEEN ){
				isEctopicAbortionHigh = true;
				/*clinicalAsses.add(Condition.ECTOPIC_PREGNANCY_OR_ABORTION);
				assesmentStatus.add(Condition.HIGH);*/
			}else if(Constant.LOWER.equalsIgnoreCase(pregnancyVisit.getWherePain()) && Constant.PERIOD_LIKE.equalsIgnoreCase(pregnancyVisit.getKindOfBleeding()) && gestationalAge < Constant.FOURTEEN ){
				isEctopicAbortionHigh = true;
				/*clinicalAsses.add(Condition.ECTOPIC_PREGNANCY_OR_ABORTION);
				assesmentStatus.add(Condition.HIGH);*/
			}
			
			if(Constant.LOWER.equalsIgnoreCase(pregnancyVisit.getWherePain()) && Constant.SPOTTING.equalsIgnoreCase(pregnancyVisit.getKindOfBleeding())
					&& gestationalAge < Constant.FOURTEEN  && Constant.Yes.equalsIgnoreCase(pregnancyVisit.getFeltGiddy())){
				isEctopicAbortion = true;
				/*clinicalAsses.add(Condition.ECTOPIC_PREGNANCY);
				assesmentStatus.add(Condition.EMERGENCY);*/
			}else if(Constant.LOWER.equalsIgnoreCase(pregnancyVisit.getWherePain()) && Constant.PERIOD_LIKE.equalsIgnoreCase(pregnancyVisit.getKindOfBleeding()) 
					&& gestationalAge < Constant.FOURTEEN  && Constant.Yes.equalsIgnoreCase(pregnancyVisit.getFeltGiddy())){
				isEctopicAbortion = true;
				/*clinicalAsses.add(Condition.ECTOPIC_PREGNANCY);
				assesmentStatus.add(Condition.EMERGENCY);*/
			}
			
			if(isEctopicAbortion){
				clinicalAsses.add(Condition.ECTOPIC_PREGNANCY);
				assesmentStatus.add(Condition.EMERGENCY);
			}else if(isEctopicAbortionHigh){
				clinicalAsses.add(Condition.ECTOPIC_PREGNANCY_OR_ABORTION);
				assesmentStatus.add(Condition.HIGH);
			}
			
			
			if(Constant.LOWER.equalsIgnoreCase(pregnancyVisit.getWherePain()) && Constant.SPOTTING.equalsIgnoreCase(pregnancyVisit.getKindOfBleeding())){
				clinicalAsses.add(Condition.ABORTION);
				assesmentStatus.add(Condition.HIGH);
			}else if(Constant.PERIOD_LIKE.equalsIgnoreCase(pregnancyVisit.getKindOfBleeding()) 
					&& gestationalAge >= Constant.FOURTEEN && gestationalAge <= Constant.TWO_EIGHT && Constant.Yes.equalsIgnoreCase(pregnancyVisit.getFeltGiddy())){
				clinicalAsses.add(Condition.ABORTION);
				assesmentStatus.add(Condition.HIGH);
			}
			
			if(Constant.SPOTTING.equalsIgnoreCase(pregnancyVisit.getKindOfBleeding())){
				clinicalAsses.add(Condition.ABRUPTIO_PLACENTA);
				assesmentStatus.add(Condition.EMERGENCY);
			}else if(Constant.PERIOD_LIKE.equalsIgnoreCase(pregnancyVisit.getKindOfBleeding()) 
					&& gestationalAge >= Constant.TWO_EIGHT && Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsAbdominalPain())){
				clinicalAsses.add(Condition.ABRUPTIO_PLACENTA);
				assesmentStatus.add(Condition.EMERGENCY);
			}

			if(Constant.SPOTTING.equalsIgnoreCase(pregnancyVisit.getKindOfBleeding())
					&& gestationalAge >= Constant.TWO_EIGHT && Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsAbdominalPain())
					&& Constant.No.equalsIgnoreCase(pregnancyVisit.getBabyMove())){
				clinicalAsses.add(Condition.ABRUPTIO_PLACENTA_AND_INTRAUTERINE_DEATH);
				assesmentStatus.add(Condition.EMERGENCY);
			}else if(Constant.PERIOD_LIKE.equalsIgnoreCase(pregnancyVisit.getKindOfBleeding()) 
					&& gestationalAge >= Constant.TWO_EIGHT && Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsAbdominalPain())
					&& Constant.No.equalsIgnoreCase(pregnancyVisit.getBabyMove())){
				clinicalAsses.add(Condition.ABRUPTIO_PLACENTA_AND_INTRAUTERINE_DEATH);
				assesmentStatus.add(Condition.EMERGENCY);
			}
			
			if(Constant.No.equalsIgnoreCase(pregnancyVisit.getBabyMove()) && gestationalAge > Constant.TWO_EIGHT){
				clinicalAsses.add(Condition.INTRAUTERINE_DEATH);
				assesmentStatus.add(Condition.HIGH);
				//servarity High
			}
			
			if(Constant.TAP_LIKE.equalsIgnoreCase(pregnancyVisit.getKindOfBleeding()) && Constant.No.equalsIgnoreCase(pregnancyVisit.getIsAbdominalPain()) ){
				clinicalAsses.add(Condition.PLACENTA_PREVIA);
				assesmentStatus.add(Condition.EMERGENCY);
			}
			
			
			if (gestationalAge < Constant.FOURTEEN && Constant.No.equalsIgnoreCase(pregnancyVisit.getIsAbdominalPain())
					&& Constant.PERIOD_LIKE.equalsIgnoreCase(pregnancyVisit.getKindOfBleeding())){
				clinicalAsses.add(Condition.FIRST_TRIMESTER_BLEEDING);
				assesmentStatus.add(Condition.LOW);
			}else if(Constant.SPOTTING.equalsIgnoreCase(pregnancyVisit.getKindOfBleeding()) && gestationalAge < Constant.FOURTEEN && 
					Constant.No.equalsIgnoreCase(pregnancyVisit.getIsAbdominalPain())) {
				clinicalAsses.add(Condition.FIRST_TRIMESTER_BLEEDING);
				assesmentStatus.add(Condition.LOW);
			}
			
			if (gestationalAge >= Constant.FOURTEEN && gestationalAge >= Constant.TWO_EIGHT && Constant.No.equalsIgnoreCase(pregnancyVisit.getIsAbdominalPain())
					&& Constant.PERIOD_LIKE.equalsIgnoreCase(pregnancyVisit.getKindOfBleeding())){
				clinicalAsses.add(Condition.MID_PREGNANCY_BLEEDING);
				assesmentStatus.add(Condition.LOW);
			}else if(Constant.SPOTTING.equalsIgnoreCase(pregnancyVisit.getKindOfBleeding()) && gestationalAge < Constant.FOURTEEN && gestationalAge >= Constant.TWO_EIGHT && 
					Constant.No.equalsIgnoreCase(pregnancyVisit.getIsAbdominalPain())) {
				clinicalAsses.add(Condition.MID_PREGNANCY_BLEEDING);
				assesmentStatus.add(Condition.LOW);
			}
			
			if (gestationalAge > Constant.TWO_EIGHT && Constant.No.equalsIgnoreCase(pregnancyVisit.getIsAbdominalPain())
					&& Constant.PERIOD_LIKE.equalsIgnoreCase(pregnancyVisit.getKindOfBleeding())){
				clinicalAsses.add(Condition.LATE_PREGNANCY_BLEEDING);
				assesmentStatus.add(Condition.LOW);
			}else if(Constant.SPOTTING.equalsIgnoreCase(pregnancyVisit.getKindOfBleeding()) && gestationalAge > Constant.TWO_EIGHT && 
					Constant.No.equalsIgnoreCase(pregnancyVisit.getIsAbdominalPain())) {
				clinicalAsses.add(Condition.LATE_PREGNANCY_BLEEDING);
				assesmentStatus.add(Condition.LOW);
			}
			
			if (Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsAbdominalPain())
					&& Constant.No.equalsIgnoreCase(pregnancyVisit.getIsBleeding())
					&& Constant.No.equalsIgnoreCase(pregnancyVisit.getIsAnkleDepression())) {
				clinicalAsses.add(Condition.ABDOMINAL_PAIN_FOR_EVALUATION);
				assesmentStatus.add(Condition.LOW);
				// servarity emergency
			}
			
			if (Constant.MORE_THEN_THREE_WEEK.equalsIgnoreCase(pregnancyVisit.getHowLongHaveCough())
					&& Constant.SPUTUM_TEST_POSITIVE.equalsIgnoreCase(pregnancyVisit.getSputumTest())) {
				clinicalAsses.add(Condition.TUBERCULOSIS);
				assesmentStatus.add(Condition.HIGH);
				// servrity high
			}
			
			if (Constant.MORE_THEN_THREE_WEEK.equalsIgnoreCase(pregnancyVisit.getHowLongHaveCough())){
					if(Constant.SPUTUM_TEST_NEGATIVE.equalsIgnoreCase(pregnancyVisit.getSputumTest()) || Constant.NOT_DONE.equalsIgnoreCase(pregnancyVisit.getSputum())) {
						clinicalAsses.add(Condition.TUBERCULOSIS1);
						assesmentStatus.add(Condition.LOW);
				// servrity Low
					}
			}
			
			
			if (Constant.Yes.equalsIgnoreCase(pregnancyVisit.getHaveFever())){
				if( Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsFeverAssocated()) || Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsFeverComeAndGo())){
				  if(Constant.SPUTUM_TEST_POSITIVE.equalsIgnoreCase(pregnancyVisit.getFirstMalaria())) {
				clinicalAsses.add(Condition.MALARIA);
				assesmentStatus.add(Condition.HIGH);
				  }
				}
				// servrity Low

			}
			if (Constant.Yes.equalsIgnoreCase(pregnancyVisit.getHaveFever())){
				if( Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsFeverAssocated()) || Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsFeverComeAndGo())){
				 if(Constant.NOT_DONE.equalsIgnoreCase(pregnancyVisit.getMalaria())) {
				clinicalAsses.add(Condition.MALARIA);
				assesmentStatus.add(Condition.HIGH);
				 }
				// servrity Low
				 
				}
			}
			if (Constant.Yes.equalsIgnoreCase(pregnancyVisit.getHaveFever())
					&& Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsFeverAssocated()) && Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsFeverComeAndGo())
					&& Constant.SPUTUM_TEST_NEGATIVE.equalsIgnoreCase(pregnancyVisit.getFirstMalaria())) {
				clinicalAsses.add(Condition.FEVER_FOR_EVALUATION);
				assesmentStatus.add(Condition.LOW);
				// servrity Low

			}
			
			if (Constant.Yes.equalsIgnoreCase(pregnancyVisit.getHaveFever())
					&& Constant.No.equalsIgnoreCase(pregnancyVisit.getIsFeverAssocated()) && Constant.No.equalsIgnoreCase(pregnancyVisit.getIsFeverComeAndGo())
					&& Constant.SPUTUM_TEST_NEGATIVE.equalsIgnoreCase(pregnancyVisit.getFirstMalaria())) {
				if(Constant.No.equalsIgnoreCase(pregnancyVisit.getHaveCough()) && Constant.No.equalsIgnoreCase(pregnancyVisit.getIsVaginalDischarge())
						&& Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsBurningPain())){
				clinicalAsses.add(Condition.FEVER_FOR_EVALUATION);
				assesmentStatus.add(Condition.LOW);
				// servrity Low
				}
			}
	
			
			
			
			if (pregnancyVisit.getFirstRbs() != "") {
				if (Double.parseDouble(pregnancyVisit.getFirstRbs()) > 140) {
					clinicalAsses.add(Condition.GESTATIONAL_DIABETES);

					assesmentStatus.add(Condition.LOW);
				}
			}
			
			if(Constant.YELLOW.equalsIgnoreCase(pregnancyVisit.getUpperEyeColor())){
				clinicalAsses.add(Condition.ICTERUS_EVALUATION);
				
				assesmentStatus.add(Condition.HIGH);
			}
			if(Constant.SPUTUM_TEST_POSITIVE.equalsIgnoreCase(pregnancyVisit.getSputumTest())){
				clinicalAsses.add(Condition.TUBERCULOSIS);
				assesmentStatus.add(Condition.LOW);
			}
			boolean isAnemiaLow=false;
			boolean isAnemiaHigh=false;
			if(Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsFeelTired()) && Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsAnkleDepression())){
				if( Constant.No.equalsIgnoreCase(pregnancyVisit.getIsBreathless()) || Constant.No.equalsIgnoreCase(pregnancyVisit.getIsoutOfBreath())){
						isAnemiaLow = true;
							
					}
			}
			if(Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsFeelTired()) && Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsAnkleDepression())){
				if( Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsBreathless()) || Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsoutOfBreath())){
						isAnemiaHigh = true;
					}
			}
			if(servAnemiaFailureEmer == true){
				clinicalAsses.add(Condition.SEVERE_ANEMIA);
				assesmentStatus.add(Condition.EMERGENCY);
			}else if(servAnemiaFailureHigh == true){
				clinicalAsses.add(Condition.SEVERE_ANEMIA);
				assesmentStatus.add(Condition.HIGH);
			}else if(pregnancyVisit.getFirstHb() !="" && Double.parseDouble(pregnancyVisit.getFirstHb()) < Constant.SEVEN ){
				clinicalAsses.add(Condition.SEVERE_ANAEMIA);
				assesmentStatus.add(Condition.HIGH);
			}else if(isAnemiaHigh){
				clinicalAsses.add("?anemia in heart failure");
				assesmentStatus.add(Condition.HIGH);
			}else if(pregnancyVisit.getFirstHb() !="" && Double.parseDouble(pregnancyVisit.getFirstHb()) < 10 ){
				clinicalAsses.add(Condition.ANAEMIA);
				assesmentStatus.add(Condition.LOW);
			}else if(anaemia){
				clinicalAsses.add(Condition.ANAEMIA);
				assesmentStatus.add(Condition.LOW);
			}else if(isAnemiaLow){
				clinicalAsses.add("? anemia");
				assesmentStatus.add(Condition.LOW);
			}
			
			
			genralExamination = getPregnenecyGeneralExamination(pregnancyVisit,symptom);
			bpHistory =getPregnencyBpDetails(pregnancyVisit);
			weightHistory = getWeightHistory(pregnancyVisit);
			testresult = getTestResult(pregnancyVisit);
		}else if(Constant.POSTPARTUM.equalsIgnoreCase(visitType)){
			PostpartumVisit postpartumVisit = postpartumList.get(0);
			
			if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getHaveFever())){
				if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsFeverAssocated())){
					if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsFeverComeAndGo())){
						findings.append(symptom.get("PHRASES1"));
						findings.append(", ");
					}else if(Constant.No.equalsIgnoreCase(postpartumVisit.getIsFeverComeAndGo())){
						findings.append(symptom.get("PHRASES2"));
						findings.append(", ");
					}
				}else if(Constant.No.equalsIgnoreCase(postpartumVisit.getIsFeverAssocated())){
					
					if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsFeverComeAndGo())){
						findings.append(symptom.get("PHRASES3"));
						findings.append(", ");
					}else if(Constant.No.equalsIgnoreCase(postpartumVisit.getIsFeverComeAndGo())){
						findings.append(symptom.get("PHRASES4"));
						findings.append(", ");
					}
					
				}/*else{
					findings.append(symptom.get("PHRASES5"));
					findings.append(", ");
				}*/
				
			}
			
			//check have feet
			if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsFits())){
				findings.append(symptom.get("PHRASES8"));
				findings.append(", ");
			}/*else if(Constant.No.equalsIgnoreCase(postpartumVisit.getIsFits())){
				findings.append(symptom.get("PHRASES9"));
				findings.append(", ");
			}else{
				//TODO Nothing
			}*/
			
			//check have consciousness
			if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsConsciousness())){
				findings.append(symptom.get("PHRASES10"));
				findings.append(", ");
			}/*else if(Constant.No.equalsIgnoreCase(postpartumVisit.getIsConsciousness())){
				findings.append(symptom.get("PHRASES11"));
				findings.append(", ");
			}else{
				//TODO Nothing
			}
			*/
			//check have headaches
			if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getHaveHeadaches())){
				findings.append(symptom.get("PHRASES14"));
				findings.append(", ");
			}/*else if(Constant.No.equalsIgnoreCase(postpartumVisit.getHaveHeadaches())){
				findings.append(symptom.get("PHRASES15"));
				findings.append(", ");
			}else{
				//TODO Nothing
			}*/
			//check have Blurred Vision
			if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getHaveBlurredVision())){
				findings.append(symptom.get("PHRASES16"));
				findings.append(", ");
			}/*else if(Constant.No.equalsIgnoreCase(postpartumVisit.getHaveBlurredVision())){
				findings.append(symptom.get("PHRASES17"));
				findings.append(", ");
			}else{
				//TODO Nothing
			}*/
			//check for fedding baby
			if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsDifficultToFeed())){
				if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getPainInBreast())){
					if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getLumpInBreast())){
						findings.append(symptom.get("PHRASES53"));
						findings.append(", ");
					}else if(Constant.No.equalsIgnoreCase(postpartumVisit.getLumpInBreast())){
						findings.append(symptom.get("PHRASES54"));
						findings.append(", ");
					}else{
						//TODO NOTHING
					}
				}else if(Constant.No.equalsIgnoreCase(postpartumVisit.getPainInBreast())){
					findings.append(symptom.get("PHRASES55"));
					findings.append(", ");
				}else{
					//TODO Nothing
				}
			}/*else if(Constant.No.equalsIgnoreCase(postpartumVisit.getIsDifficultToFeed())){
				findings.append(symptom.get("PHRASES56"));
				findings.append(", ");
			}else{
				//TODO Nothing
			}*/
			//check have breathless
			if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsBreathless())){
				if(postpartumVisit.getWhenBreathless().contains(Constant.SITTING)){
					findings.append(symptom.get("PHRASES18"));
					findings.append(", ");
				}
				if(postpartumVisit.getWhenBreathless().contains(Constant.COOKING)){
					findings.append(symptom.get("PHRASES19"));
					findings.append(", ");
				}
				if(postpartumVisit.getWhenBreathless().contains(Constant.CARRING_LOAD)){
					findings.append(symptom.get("PHRASES20"));
					findings.append(", ");
				}
				
			}/*else if(Constant.No.equalsIgnoreCase(postpartumVisit.getIsBreathless())){
				findings.append(symptom.get("PHRASES21"));
				findings.append(", ");
			}else{
				//TODO Nothing
			}*/
			//check cough
			if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getHaveCough())){
				if(postpartumVisit.getHowLongHaveCough().equalsIgnoreCase(Constant.LESS_THEN_THREE_WEEK)){
					findings.append(symptom.get("PHRASES22"));
					findings.append(", ");
				}else if(postpartumVisit.getHowLongHaveCough().equalsIgnoreCase(Constant.MORE_THEN_THREE_WEEK)){
					findings.append(symptom.get("PHRASES23"));
					findings.append(", ");
				}else{
					//TODO nothing
				}
				
			}/*else if(Constant.No.equalsIgnoreCase(postpartumVisit.getHaveCough())){
				findings.append(symptom.get("PHRASES23"));
				findings.append(", ");
			}else{
				//TODO nothing
			}*/
			//check for abnormal pain
			if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsAbdominalPain())){
				if(postpartumVisit.getWherePain().equalsIgnoreCase(Constant.UPPER)){
					findings.append(symptom.get("PHRASES25"));
					findings.append(", ");
				}else if(postpartumVisit.getWherePain().equalsIgnoreCase(Constant.LOWER)){
					findings.append(symptom.get("PHRASES26"));
					findings.append(", ");
				}else if(postpartumVisit.getWherePain().equalsIgnoreCase(Constant.ALL_OVER)){
					findings.append(symptom.get("PHRASES27"));
					findings.append(", ");
				}
			}/*else{
				findings.append(symptom.get("PHRASES28"));
				findings.append(", ");
			}*/
			//check for verginal discharge
			if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsVaginalDischarge())){
				findings.append(symptom.get("PHRASES31"));
				findings.append(", ");
			}/*else{
				findings.append(symptom.get("PHRASES31"));
				findings.append(", ");
			}*/
			//check for bleeding
			
			if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsBleeding())){
				if(null !=postpartumVisit.getNoDayAfterDel() && postpartumVisit.getNoDayAfterDel()!=""){
				if(Constant.NO_OF_DAY < Integer.parseInt(postpartumVisit.getNoDayAfterDel())){
					findings.append("h/o bleeding for <"+postpartumVisit.getNoDayAfterDel()+"> days after delivery");
					findings.append(", ");
				}/*else if(Constant.NO_OF_DAY > Integer.parseInt(postpartumVisit.getNoDayAfterDel())){
					findings.append(symptom.get("PHRASES58"));
					findings.append(", ");
				}else{
					//TODO Nothing
				}*/
				if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsPassClotBleeding())){
					findings.append(symptom.get("PHRASES59"));
					findings.append(", ");
				}
				if(null !=postpartumVisit.getNoOfClothes() && postpartumVisit.getNoOfClothes()!=""){
					if(Constant.THREE_OR_MORE < Integer.parseInt(postpartumVisit.getNoOfClothes())){
						
						findings.append("h/o changing "+postpartumVisit.getNoOfClothes()+" cloths");
						findings.append(", ");
					}
				}
				if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getHasBleedingIncrease())){
					findings.append(symptom.get("PHRASES63"));
					findings.append(", ");
				}
				
				}
				
			}	//check burning pain while unirating
			/*if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsBurningPain())){
				findings.append(symptom.get("PHRASES38"));
				findings.append(", ");
			}else*/
			if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsBurningPain())){
				findings.append(symptom.get("PHRASES39"));
				findings.append(", ");
			}else{
				//TODO Nothing
			}
			//check tailkin illogical
			if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getTalkingIrrelevantly())){
				findings.append(symptom.get("PHRASES45"));
				findings.append(", ");
			}/*else
			if(Constant.No.equalsIgnoreCase(postpartumVisit.getIsTalking())){
				findings.append(symptom.get("PHRASES45"));
				findings.append(", ");
			}else{
				//TODO Nothing
			}*/
			
			//check interest in carring baby
			if(Constant.No.equalsIgnoreCase(postpartumVisit.getCarringBabyAndHerself())){
				findings.append(symptom.get("PHRASES67"));
				findings.append(", ");
			}/*else
			if(Constant.No.equalsIgnoreCase(postpartumVisit.getCarringBabyAndHerself())){
				findings.append(symptom.get("PHRASES67"));
				findings.append(", ");
			}else{
				//TODO Nothing
			}
			
*/			//check Hearing imaginary sound
			if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsHearingImaginary())){
				findings.append(symptom.get("PHRASES68"));
				findings.append(", ");
			}/*else if(Constant.No.equalsIgnoreCase(postpartumVisit.getIsHearingImaginary())){
				findings.append(symptom.get("PHRASES69"));
				findings.append(", ");
			}else{
				//TODO Nothing
			}*/
			
			//check out of breath
			/*if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsoutOfBreath())){
				findings.append(symptom.get("PHRASES47"));
				findings.append(", ");
			}
			//check for talking irrelevantly
			if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsTalking())){
				findings.append(symptom.get("PHRASES48"));
				findings.append(", ");
			}*/
			
			//check for talking irrelevantly
			/*if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsTalking())){
				findings.append(symptom.get("PHRASES48"));
				findings.append(", ");
			}*/
			
			//check for upper eye color
			/*if(Constant.YELLOW.equalsIgnoreCase(postpartumVisit.getUpperEyeColor())){
				findings.append(symptom.get("PHRASES49"));
				findings.append(", ");
			}*/
			//check for lower eye color
			/*if(Constant.PALE.equalsIgnoreCase(postpartumVisit.getLowerEyeColor())){
				findings.append(symptom.get("PHRASES50"));
				findings.append(", ");
			}*/
			
			//check for Ankle Depression
			/*if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsAnkleDepression())){
				findings.append(symptom.get("PHRASES51"));
				findings.append(", ");
			}*/
			//check for Eye Swelling
			/*if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsEyeSwelling())){
				findings.append(symptom.get("PHRASES52"));
				findings.append(", ");
			}*/
			
			
			// code added for clinical assessment for postpartum woman
			
			
			//get Gestational Age in week
			int gestationalAge =getGestationalAgePostpartum(registration,postpartumVisit); 
			
			
			if ((Constant.Yes.equalsIgnoreCase(postpartumVisit.getHaveFever()) || Constant.Yes.equalsIgnoreCase(postpartumVisit.getHaveHeadaches()))){
				if((Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsFits()) || Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsTalking()))) {
				clinicalAsses.add(Condition.CVT);
				assesmentStatus.add(Condition.EMERGENCY);
				}
			}
			
			
			Boolean bpCheck =false;
			String bp[] = null;
			if(""!=postpartumVisit.getFirstBp() && null !=postpartumVisit.getFirstBp()){
				 bp = postpartumVisit.getFirstBp().split("/");
				if(bp.length ==2){
					if(Integer.parseInt(bp[0]) > 160 && Integer.parseInt(bp[1]) > 110 ){
						bpCheck =true;
					}
				}
			}
			
			if (Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsAnkleDepression()) || !"Nil".equalsIgnoreCase(postpartumVisit.getFirstUrine()) || bpCheck){
				if( Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsFits()) || Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsConsciousness()) ) {
				clinicalAsses.add(Condition.ECLAMPSIA);
				assesmentStatus.add(Condition.HIGH);
				// servrity high
				}
			}
				if (Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsAnkleDepression())|| "Traces".equalsIgnoreCase(postpartumVisit.getFirstUrine()) ||
						"1+".equalsIgnoreCase(postpartumVisit.getFirstUrine()) || "2+".equalsIgnoreCase(postpartumVisit.getFirstUrine()) || "3+".equalsIgnoreCase(postpartumVisit.getFirstUrine())){
			
					if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getHaveHeadaches())
					|| Constant.Yes.equalsIgnoreCase(postpartumVisit.getHaveBlurredVision())
					|| Constant.UPPER.equalsIgnoreCase(postpartumVisit.getWherePain())) {
				clinicalAsses.add(Condition.IMMINENT_ECLAMPSIA);
				assesmentStatus.add(Condition.HIGH);
					}
			}
			
			
			if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsEyeSwelling()) || "3+".equalsIgnoreCase(postpartumVisit.getFirstUrine()) || bpCheck){
				clinicalAsses.add(Condition.IMMINENT_ECLAMPSIA);
				assesmentStatus.add(Condition.HIGH);
			}
			if(postpartumVisit.getFirstBp()!=""){
				if( Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsAnkleDepression()) && Integer.parseInt(bp[0]) <= 160 && Integer.parseInt(bp[0]) >= 140 && Integer.parseInt(bp[1]) <= 110 && Integer.parseInt(bp[1]) >= 90 &&  !"Nil".equalsIgnoreCase(postpartumVisit.getFirstUrine())){
					clinicalAsses.add(Condition.PRE_ECLAMPSIA);
					assesmentStatus.add(Condition.LOW);
				}
			}
			boolean servAnemiaFailureEmer = false;
			boolean servAnemiaFailureHigh = false;
			boolean anaemia=false;
			if (postpartumVisit.getFirstHb() != "") {
				double hb = Double.parseDouble(postpartumVisit.getFirstHb());

				if (hb < 11 && hb > 7 && Constant.PALE.equalsIgnoreCase(postpartumVisit.getLowerEyeColor())) {
					/*clinicalAsses.add(Condition.ANAEMIA);
					assesmentStatus.add(Condition.LOW);*/
					anaemia=true;
				}

				if (Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsFeelTired()) || Constant.PALE.equalsIgnoreCase(postpartumVisit.getLowerEyeColor()) || Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsAnkleDepression())){
						if(hb < 7 && Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsBreathless())){
							/*clinicalAsses.add(Condition.SEVERE_ANAEMIA);
							assesmentStatus.add(Condition.EMERGENCY);*/
							servAnemiaFailureEmer = true;
						}else if(Constant.COOKING.equalsIgnoreCase(postpartumVisit.getWhenBreathless())|| Constant.Yes.equalsIgnoreCase(postpartumVisit.getHaveCough())) {
							/*clinicalAsses.add(Condition.SEVERE_ANAEMIA);
							assesmentStatus.add(Condition.EMERGENCY);*/
							servAnemiaFailureEmer= true;
						}
					}
			}
			
			if (Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsFeelTired()) && Constant.PALE.equalsIgnoreCase(postpartumVisit.getLowerEyeColor()) 
					&& Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsAnkleDepression()) && Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsBreathless())){
					/*clinicalAsses.add(Condition.SEVERE_ANAEMIA);
					assesmentStatus.add(Condition.EMERGENCY);*/
					servAnemiaFailureHigh = true;
				}else if(Constant.COOKING.equalsIgnoreCase(postpartumVisit.getWhenBreathless())|| Constant.Yes.equalsIgnoreCase(postpartumVisit.getHaveCough())) {
					/*clinicalAsses.add(Condition.SEVERE_ANAEMIA);
					assesmentStatus.add(Condition.HIGH);*/
					servAnemiaFailureHigh = true;
			}
			
			if(Constant.No.equalsIgnoreCase(postpartumVisit.getHaveFever()) && Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsBurningPain())){
				clinicalAsses.add(Condition.UTI);
				assesmentStatus.add(Condition.LOW);
				//low serverity
			}
			
			if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getHaveFever()) && Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsBurningPain())){
				clinicalAsses.add(Condition.UTI);
				assesmentStatus.add(Condition.HIGH);
				//high serverity
			}
			if (Constant.Yes.equalsIgnoreCase(postpartumVisit.getHaveFever())
					&& Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsAbdominalPain())
					&& Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsTalking())) {
				clinicalAsses.add(Condition.SEPSIS);
				assesmentStatus.add(Condition.HIGH);
				// high serverity
			}
			
			if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsVaginalDischarge()) && Constant.No.equalsIgnoreCase(postpartumVisit.getHaveFever()) && Constant.No.equalsIgnoreCase(postpartumVisit.getIsAbdominalPain())){
				clinicalAsses.add(Condition.RTI);
				assesmentStatus.add(Condition.LOW);
			}
			if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsVaginalDischarge()) && Constant.No.equalsIgnoreCase(postpartumVisit.getHaveFever()) && Constant.No.equalsIgnoreCase(postpartumVisit.getTalkingIrrelevantly())){
				clinicalAsses.add("Sepsis?");
				assesmentStatus.add(Condition.HIGH);
			}
			if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsVaginalDischarge()) && Constant.No.equalsIgnoreCase(postpartumVisit.getIsAbdominalPain()) && Constant.No.equalsIgnoreCase(postpartumVisit.getTalkingIrrelevantly())){
				clinicalAsses.add("Sepsis?");
				assesmentStatus.add(Condition.HIGH);
			}		
						
			if (Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsAbdominalPain())
					&& Constant.No.equalsIgnoreCase(postpartumVisit.getIsBleeding())
					&& Constant.No.equalsIgnoreCase(postpartumVisit.getIsAnkleDepression())) {
				clinicalAsses.add(Condition.ABDOMINAL_PAIN_FOR_EVALUATION);
				assesmentStatus.add(Condition.LOW);
				// servarity emergency
			}
			
			if (Constant.MORE_THEN_THREE_WEEK.equalsIgnoreCase(postpartumVisit.getHowLongHaveCough())
					&& Constant.SPUTUM_TEST_POSITIVE.equalsIgnoreCase(postpartumVisit.getSputumTest())) {
				clinicalAsses.add(Condition.TUBERCULOSIS);
				assesmentStatus.add(Condition.HIGH);
				// servrity high
			}
			
			if (Constant.MORE_THEN_THREE_WEEK.equalsIgnoreCase(postpartumVisit.getHowLongHaveCough())){
					if(Constant.SPUTUM_TEST_NEGATIVE.equalsIgnoreCase(postpartumVisit.getSputumTest()) || Constant.NOT_DONE.equalsIgnoreCase(postpartumVisit.getSputum())) {
						clinicalAsses.add(Condition.TUBERCULOSIS1);
						assesmentStatus.add(Condition.LOW);
				// servrity Low
					}
			}
			
			
			if (Constant.Yes.equalsIgnoreCase(postpartumVisit.getHaveFever())){
				if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsFeverAssocated()) || Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsFeverComeAndGo())){
					if(Constant.SPUTUM_TEST_POSITIVE.equalsIgnoreCase(postpartumVisit.getFirstMalaria())) {
						clinicalAsses.add(Condition.MALARIA);
						assesmentStatus.add(Condition.HIGH);
				// servrity Low
					}else if(Constant.NOT_DONE.equalsIgnoreCase(postpartumVisit.getMalaria())){
						clinicalAsses.add(Condition.MALARIA);
						assesmentStatus.add(Condition.HIGH);
					}
				}
			}
			/*if (Constant.Yes.equalsIgnoreCase(postpartumVisit.getHaveFever())
					&& Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsFeverAssocated()) && Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsFeverComeAndGo())
					&& Constant.No.equalsIgnoreCase(postpartumVisit.getFirstMalaria())) {
					clinicalAsses.add(Condition.MALARIA);
					assesmentStatus.add(Condition.HIGH);
				// servrity Low
			}*/
			if (Constant.Yes.equalsIgnoreCase(postpartumVisit.getHaveFever())
					&& Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsFeverAssocated()) && Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsFeverComeAndGo())
					&& Constant.SPUTUM_TEST_POSITIVE.equalsIgnoreCase(postpartumVisit.getFirstMalaria())) {
				clinicalAsses.add(Condition.FEVER_FOR_EVALUATION);
				assesmentStatus.add(Condition.LOW);
				// servrity Low

			}
			
			if (Constant.Yes.equalsIgnoreCase(postpartumVisit.getHaveFever())
					&& Constant.No.equalsIgnoreCase(postpartumVisit.getIsFeverAssocated()) && Constant.No.equalsIgnoreCase(postpartumVisit.getIsFeverComeAndGo())
					&& Constant.SPUTUM_TEST_NEGATIVE.equalsIgnoreCase(postpartumVisit.getFirstMalaria())) {
				if(Constant.No.equalsIgnoreCase(postpartumVisit.getHaveCough()) && Constant.No.equalsIgnoreCase(postpartumVisit.getIsVaginalDischarge())
						&& Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsBurningPain())){
				clinicalAsses.add(Condition.FEVER_FOR_EVALUATION);
				assesmentStatus.add(Condition.LOW);
				// servrity Low
				}
			}
			
			
			boolean isNoOfCloths = false;
			if(postpartumVisit.getNoOfClothes()!="" && Constant.TWO <Integer.parseInt(postpartumVisit.getNoOfClothes())){
				isNoOfCloths = true;
			}
			if(Constant.NO_OF_DAY.equals(postpartumVisit.getNoDayAfterDel()) || Constant.Yes.equalsIgnoreCase(postpartumVisit.getHasBleedingIncrease()) || isNoOfCloths ){
				clinicalAsses.add(Condition.PPH);
				assesmentStatus.add(Condition.EMERGENCY);
				//servrity Emergency
			}
			if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getPainInBreast()) && Constant.No.equalsIgnoreCase(postpartumVisit.getHaveFever())){
				clinicalAsses.add(Condition.MASTITIS_BREAST_ABSCESS);
				assesmentStatus.add(Condition.HIGH);
				//servrity High
			
			}
			if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getPainInBreast()) && Constant.Yes.equalsIgnoreCase(postpartumVisit.getHaveFever())){
				clinicalAsses.add(Condition.MASTITIS_BREAST_ABSCESS);
				assesmentStatus.add(Condition.HIGH);
				//servrity High
			
			}
			if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getHaveFever())
					&& Constant.Yes.equalsIgnoreCase(postpartumVisit.getLumpInBreast())){
				clinicalAsses.add(Condition.BREAST_ABSCESS);
				assesmentStatus.add(Condition.HIGH);
				//servrity High
			
			}
			if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsTalking()) && Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsHearingImaginary())){
				clinicalAsses.add(Condition.PSYCHOSIS);
				assesmentStatus.add(Condition.HIGH);
				//servrity High
			}
			if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getTalkingIrrelevantly()) || Constant.No.equalsIgnoreCase(postpartumVisit.getCarringBabyAndHerself())){
				/*if(Constant.No.equalsIgnoreCase(postpartumVisit.getIsHearingImaginary())){*/
				clinicalAsses.add(Condition.DEPRESSION_PSYHOSIS);
				assesmentStatus.add(Condition.LOW);
				//}
				//servrity low
			}
			//removed halicination condition
			if(Constant.No.equalsIgnoreCase(postpartumVisit.getIsTalking()) &&  Constant.No.equalsIgnoreCase(postpartumVisit.getCarringBabyAndHerself())){
				clinicalAsses.add(Condition.DEPRESSION);
				assesmentStatus.add(Condition.LOW);
				//servrity low
			}
			
			
			if(Constant.SPUTUM_TEST_POSITIVE.equalsIgnoreCase(postpartumVisit.getSputumTest())){
				clinicalAsses.add(Condition.TUBERCULOSIS);
				assesmentStatus.add(Condition.HIGH);
			}
			boolean isAnemiaLow=false;
			boolean isAnemiaHigh=false;
			if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsFeelTired()) && Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsAnkleDepression())){
				if( Constant.No.equalsIgnoreCase(postpartumVisit.getIsBreathless()) || Constant.No.equalsIgnoreCase(postpartumVisit.getIsoutOfBreath())){
						isAnemiaLow = true;
							
					}
			}
			if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsFeelTired()) && Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsAnkleDepression())){
				if( Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsBreathless()) || Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsoutOfBreath())){
						isAnemiaHigh = true;
					}
			}
			if(servAnemiaFailureEmer == true){
				clinicalAsses.add(Condition.SEVERE_ANEMIA);
				assesmentStatus.add(Condition.EMERGENCY);
			}else if(servAnemiaFailureHigh == true){
				clinicalAsses.add(Condition.SEVERE_ANEMIA);
				assesmentStatus.add(Condition.HIGH);
			}else if(postpartumVisit.getFirstHb() !="" && Double.parseDouble(postpartumVisit.getFirstHb()) < Constant.SEVEN ){
				clinicalAsses.add(Condition.SEVERE_ANAEMIA);
				assesmentStatus.add(Condition.HIGH);
			}else if(isAnemiaHigh){
				clinicalAsses.add("?anemia in heart failure");
				assesmentStatus.add(Condition.HIGH);
			}else if(postpartumVisit.getFirstHb() !="" && Double.parseDouble(postpartumVisit.getFirstHb()) < 10 ){
				clinicalAsses.add(Condition.ANAEMIA);
				assesmentStatus.add(Condition.LOW);
			}else if(anaemia){
				clinicalAsses.add(Condition.ANAEMIA);
				assesmentStatus.add(Condition.LOW);
			}else if(isAnemiaLow){
				clinicalAsses.add("? anemia");
				assesmentStatus.add(Condition.LOW);
			}
			genralExamination = getPostpartumGeneralExamination(postpartumVisit,symptom);
			bpHistory = getPostpartumBpDetails(postpartumVisit);
			testresult = getTestResult(postpartumVisit);
			
		}else{
			//TODO Nothing
	}
		Iterator<String> it = clinicalAsses.iterator();
		StringBuilder CliAssess = new StringBuilder();
		while(it.hasNext()){
			CliAssess.append(it.next());
			CliAssess.append(",");
		}
		
		String asses = getAssesmentStatus(assesmentStatus);
		return findings.toString()+":"+CliAssess.toString()+":"+noOfVisit+":"+visitDate+":"+genralExamination+":"+bpHistory+":"+weightHistory+":"+testresult+":"+asses;
}


	/**
	 * @param assesmentStatus
	 * @return
	 */
	private String getAssesmentStatus(ArrayList<String> assesmentStatus) {
		logger.info("Entering :: RegistrationHandlerImpl :: getAssesmentStatus method");
		String status ="";
		try {
			HashSet<String> set = new HashSet<String>();
			for(String set1 : assesmentStatus){
				set.add(set1);
			}
			
			Iterator<String> it = set.iterator();
			Map<String, Integer> map = new HashMap<String, Integer>();
			while (it.hasNext()) {
				int count = 0;
				String assesment = it.next();
				for (String status1 : assesmentStatus) {
						if(status1.equalsIgnoreCase(assesment)){
							count++;
						}
				}
				map.put(assesment, count);
			}
			
			
			
			if( map.containsKey(Condition.LOW) &&  map.get(Condition.LOW) <=3){
				status = Condition.LOW;
			}
			if(map.containsKey(Condition.HIGH) && map.get(Condition.HIGH) >=1 ||map.containsKey(Condition.LOW) && map.get(Condition.LOW) >3){
				status = Condition.HIGH;
			}
			if( map.containsKey(Condition.EMERGENCY) && map.get(Condition.EMERGENCY) >=1 || map.containsKey(Condition.HIGH) && map.get(Condition.HIGH) >=2 ){
				status = Condition.EMERGENCY;
			}
		} catch (Exception e) {
			logger.error("Error :: RegistrationHandlerImpl :: getAssesment method", e);
		}
		
		return status;
	}

	/**
	 * @param pregnancyVisit
	 * @return
	 */
	private List<String> getTestResult(PregnancyVisit pregnancyVisit) {
		List<String>testResult = new ArrayList<String>();
		try {
			if(Constant.DONE.equalsIgnoreCase(pregnancyVisit.getBp())){
				StringBuilder sb = new StringBuilder();
				sb.append("BP = ");
				if(null != pregnancyVisit.getFirstBp() && "" != pregnancyVisit.getFirstBp()){
					sb.append(pregnancyVisit.getFirstBp());
					sb.append(" (");
					sb.append(pregnancyVisit.getBpDateOne());
					sb.append(" );");
				}
				if(null != pregnancyVisit.getSecBp() && "" != pregnancyVisit.getSecBp()){
					sb.append(pregnancyVisit.getSecBp());
					sb.append(" (");
					sb.append(pregnancyVisit.getBpDateSec());
					sb.append(" );");
				}
				if(null != pregnancyVisit.getThirdBp() && "" != pregnancyVisit.getThirdBp()){
					sb.append(pregnancyVisit.getThirdBp());
					sb.append(" (");
					sb.append(pregnancyVisit.getBpDateThird());
					sb.append(" );");
				}
				if(null != pregnancyVisit.getFourBp() && "" != pregnancyVisit.getFourBp()){
					sb.append(pregnancyVisit.getFourBp());
					sb.append(" (");
					sb.append(pregnancyVisit.getBpDateFour());
					sb.append(" );");
				}
				testResult.add(sb.toString());
			}
		} catch (Exception e) {
			logger.error("Error :: RegistrationHandlerImpl :: testresult BP method", e);
		}
		try {
			if(Constant.DONE.equalsIgnoreCase(pregnancyVisit.getHb())){
				StringBuilder sb = new StringBuilder();
				sb.append("HB = ");
				if(null != pregnancyVisit.getFirstHb() && "" != pregnancyVisit.getFirstHb()){
					sb.append(pregnancyVisit.getFirstHb());
					sb.append(" (");
					sb.append(pregnancyVisit.getHbDateOne());
					sb.append(" );");
				}
				if(null != pregnancyVisit.getSecHb() && "" != pregnancyVisit.getSecHb()){
					sb.append(pregnancyVisit.getSecHb());
					sb.append(" (");
					sb.append(pregnancyVisit.getHbDateSec());
					sb.append(" );");
				}
				testResult.add(sb.toString());
			}
		} catch (Exception e) {
			logger.error("Error :: RegistrationHandlerImpl :: testresult BP method", e);
		}
		try {
			if(Constant.DONE.equalsIgnoreCase(pregnancyVisit.getUrine())){
				StringBuilder sb = new StringBuilder();
				sb.append("Urine albumin = ");
				if(null != pregnancyVisit.getFirstUrine() && "" != pregnancyVisit.getFirstUrine()){
					sb.append(pregnancyVisit.getFirstUrine());
					sb.append(" (");
					sb.append(pregnancyVisit.getUrineDateOne());
					sb.append(" );");
				}
				if(null != pregnancyVisit.getSecUrine() && "" != pregnancyVisit.getSecUrine()){
					sb.append(pregnancyVisit.getSecUrine());
					sb.append(" (");
					sb.append(pregnancyVisit.getUrineDateSec());
					sb.append(" );");
				}
				testResult.add(sb.toString());
			}
		} catch (Exception e) {
			logger.error("Error :: RegistrationHandlerImpl :: testresult Urine method", e);
		}
		try {
			if(Constant.DONE.equalsIgnoreCase(pregnancyVisit.getMalaria())){
				StringBuilder sb = new StringBuilder();
				sb.append("Test for Malaria = ");
				if(null != pregnancyVisit.getFirstMalaria() && "" != pregnancyVisit.getFirstMalaria()){
					sb.append(pregnancyVisit.getFirstMalaria());
					sb.append(" (");
					sb.append(pregnancyVisit.getMalariaDateOne());
					sb.append(" );");
				}
				if(null != pregnancyVisit.getSecMalaria() && "" != pregnancyVisit.getSecMalaria()){
					sb.append(pregnancyVisit.getSecMalaria());
					sb.append(" (");
					sb.append(pregnancyVisit.getMalariaDateSec());
					sb.append(" );");
				}
				testResult.add(sb.toString());
			}
		} catch (Exception e) {
			logger.error("Error :: RegistrationHandlerImpl :: test result Maleria method", e);
		}
		try {
			if(Constant.DONE.equalsIgnoreCase(pregnancyVisit.getSputum())){
				StringBuilder sb = new StringBuilder();
				sb.append("Sputum Test  = ");
				if(null != pregnancyVisit.getSputumTest() && "" != pregnancyVisit.getSputumTest()){
					sb.append(pregnancyVisit.getSputumTest());
					sb.append(" (");
					sb.append(pregnancyVisit.getSputumDate());
					sb.append(" );");
				}
				testResult.add(sb.toString());
			}
		} catch (Exception e) {
			logger.error("Error :: RegistrationHandlerImpl :: test result sputum method", e);
		}
		try {
			if(Constant.DONE.equalsIgnoreCase(pregnancyVisit.getUltrasound())){
				StringBuilder sb = new StringBuilder();
				sb.append("Ultrasound Scan = ");
				if(null != pregnancyVisit.getFirstUltrasound() && "" != pregnancyVisit.getFirstUltrasound()){
					sb.append(pregnancyVisit.getFirstUltrasound());
					sb.append(" (");
					sb.append(pregnancyVisit.getUltrasoundDateOne());
					sb.append(" );");
				}
				if(null != pregnancyVisit.getSecUltrasound() && "" != pregnancyVisit.getSecUltrasound()){
					sb.append(pregnancyVisit.getSecUltrasound());
					sb.append(" (");
					sb.append(pregnancyVisit.getUltrasoundDateSec());
					sb.append(" );");
				}
				testResult.add(sb.toString());
			}
		} catch (Exception e) {
			logger.error("Error :: RegistrationHandlerImpl :: test result ultrasound method", e);
		}
		try {
			if(Constant.DONE.equalsIgnoreCase(pregnancyVisit.getRbs())){
				StringBuilder sb = new StringBuilder();
				sb.append("RBS = ");
				if(null != pregnancyVisit.getFirstRbs() && "" != pregnancyVisit.getFirstRbs()){
					sb.append(pregnancyVisit.getFirstRbs());
					sb.append(" (");
					sb.append(pregnancyVisit.getRbsDateOne());
					sb.append(" );");
				}
				if(null != pregnancyVisit.getSecRbs() && "" != pregnancyVisit.getSecRbs()){
					sb.append(pregnancyVisit.getSecRbs());
					sb.append(" (");
					sb.append(pregnancyVisit.getRbsDateSec());
					sb.append(" );");
				}
				testResult.add(sb.toString());
			}
		} catch (Exception e) {
			logger.error("Error :: RegistrationHandlerImpl :: test result rbs method");
		}
		return testResult;
	}

	/**
	 * @param postpartumVisit
	 * @return
	 */
	private List<String> getTestResult(PostpartumVisit postpartumVisit) {
		List<String>testResult = new ArrayList<String>();
		try {
			if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getBp())){
				StringBuilder sb = new StringBuilder();
				sb.append("BP = ");
				if(null != postpartumVisit.getFirstBp() && "" != postpartumVisit.getFirstBp()){
					sb.append(postpartumVisit.getFirstBp());
					sb.append(" (");
					sb.append(postpartumVisit.getBpDateOne());
					sb.append(" );");
				}
				if(null != postpartumVisit.getSecBp() && "" != postpartumVisit.getSecBp()){
					sb.append(postpartumVisit.getSecBp());
					sb.append(" (");
					sb.append(postpartumVisit.getBpDateSec());
					sb.append(" );");
				}
				testResult.add(sb.toString());
			}
		} catch (Exception e) {
			logger.error("Error :: RegistrationHandlerImpl :: testresult BP method", e);
		}
		try {
			if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getHb())){
				StringBuilder sb = new StringBuilder();
				sb.append("HB = ");
				if(null != postpartumVisit.getFirstHb() && "" != postpartumVisit.getFirstHb()){
					sb.append(postpartumVisit.getFirstHb());
					sb.append(" (");
					sb.append(postpartumVisit.getHbDateOne());
					sb.append(" );");
				}
				if(null != postpartumVisit.getSecHb() && "" != postpartumVisit.getSecHb()){
					sb.append(postpartumVisit.getSecHb());
					sb.append(" (");
					sb.append(postpartumVisit.getHbDateSec());
					sb.append(" );");
				}
				testResult.add(sb.toString());
			}
		} catch (Exception e) {
			logger.error("Error :: RegistrationHandlerImpl :: getTestResult hb  method", e);
		}
		try {
			if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getUrine())){
				StringBuilder sb = new StringBuilder();
				sb.append("Urine albumin = ");
				if(null != postpartumVisit.getFirstUrine() && "" != postpartumVisit.getFirstUrine()){
					sb.append(postpartumVisit.getFirstUrine());
					sb.append(" (");
					sb.append(postpartumVisit.getUrineDateOne());
					sb.append(" );");
				}
				if(null != postpartumVisit.getSecUrine() && "" != postpartumVisit.getSecUrine()){
					sb.append(postpartumVisit.getSecUrine());
					sb.append(" (");
					sb.append(postpartumVisit.getUrineDateSec());
					sb.append(" );");
				}
				testResult.add(sb.toString());
			}
		} catch (Exception e) {
			logger.error("Error :: RegistrationHandlerImpl :: getTestResult Urine  method", e);
		}
		try {
			if(Constant.DONE.equalsIgnoreCase(postpartumVisit.getMalaria())){
				StringBuilder sb = new StringBuilder();
				sb.append("Test for Malaria = ");
				if(null != postpartumVisit.getFirstMalaria() && "" != postpartumVisit.getFirstMalaria()){
					sb.append(postpartumVisit.getFirstMalaria());
					sb.append(" (");
					sb.append(postpartumVisit.getMalariaDateOne());
					sb.append(" );");
				}
				if(null != postpartumVisit.getSecMalaria() && "" != postpartumVisit.getSecMalaria()){
					sb.append(postpartumVisit.getSecMalaria());
					sb.append(" (");
					sb.append(postpartumVisit.getMalariaDateSec());
					sb.append(" );");
				}
				testResult.add(sb.toString());
			}
		} catch (Exception e) {
			logger.error("Error :: RegistrationHandlerImpl :: getTestResult maleria  method", e);
		}
		try {
			if(Constant.DONE.equalsIgnoreCase(postpartumVisit.getSputum())){
				StringBuilder sb = new StringBuilder();
				sb.append("Sputum Test = ");
				if(null != postpartumVisit.getSputumTest() && "" != postpartumVisit.getSputumTest()){
					sb.append(postpartumVisit.getSputumTest());
					sb.append(" (");
					sb.append(postpartumVisit.getSputumDate());
					sb.append(" );");
				}
				testResult.add(sb.toString());
			}
		} catch (Exception e) {
			logger.error("Error :: RegistrationHandlerImpl :: getTestResult sputum  method", e);
		}
		
		
		
		return testResult;
	}

	/**
	 * @param pregnancyVisit
	 * @return
	 */
	private String getWeightHistory(PregnancyVisit pregnancyVisit) {
		StringBuilder sb = new StringBuilder();
		try {
			
			if(null != pregnancyVisit.getFirstWeight() && "" != pregnancyVisit.getFirstWeight()){
				sb.append(pregnancyVisit.getFirstWeight());
				sb.append(" (");
				sb.append(pregnancyVisit.getWeightDateOne());
				sb.append(" );");
			}
			if(null != pregnancyVisit.getSecWeight() && "" != pregnancyVisit.getSecWeight()){
				sb.append(pregnancyVisit.getSecWeight());
				sb.append(" (");
				sb.append(pregnancyVisit.getWeightDateOne());
				sb.append(" );");
			}
			if(null != pregnancyVisit.getWeightDateThird() && "" != pregnancyVisit.getWeightDateThird()){
				sb.append(pregnancyVisit.getThirdWeight());
				sb.append(" (");
				sb.append(pregnancyVisit.getWeightDateThird());
				sb.append(" );");
			}
			if(null != pregnancyVisit.getFourthWeight() && "" != pregnancyVisit.getFourthWeight()){
				sb.append(pregnancyVisit.getFourthWeight());
				sb.append(" (");
				sb.append(pregnancyVisit.getWeightDateFour());
				sb.append(" );");
			}
		} catch (Exception e) {
			logger.error("Error : reporthandlerimpl :: getweighthistory :method"+e);
		}
	
	return sb.toString();
	}

	/**
	 * @param pregnancyVisit
	 * @return
	 */
	private String getPregnencyBpDetails(PregnancyVisit pregnancyVisit) {	
		StringBuilder sb = new StringBuilder();
		try {
			if(null != pregnancyVisit.getFirstBp() && "" != pregnancyVisit.getFirstBp()){
				sb.append(pregnancyVisit.getFirstBp());
				sb.append(" (");
				sb.append(pregnancyVisit.getBpDateOne());
				sb.append(" );");
			}
			if(null != pregnancyVisit.getSecBp() && "" != pregnancyVisit.getSecBp()){
				sb.append(pregnancyVisit.getSecBp());
				sb.append(" (");
				sb.append(pregnancyVisit.getBpDateSec());
				sb.append(" );");
			}
			if(null != pregnancyVisit.getThirdBp() && "" != pregnancyVisit.getThirdBp()){
				sb.append(pregnancyVisit.getThirdBp());
				sb.append(" (");
				sb.append(pregnancyVisit.getBpDateThird());
				sb.append(" );");
			}
			if(null != pregnancyVisit.getFourBp() && "" != pregnancyVisit.getFourBp()){
				sb.append(pregnancyVisit.getFourBp());
				sb.append(" (");
				sb.append(pregnancyVisit.getBpDateFour());
				sb.append(" );");
			}
		} catch (Exception e) {
			logger.error("Error :: reporthandlerimpl : getPreganancyBPdetails method"+e);
		}
	
	return sb.toString();
	}

	/**
	 * @param postpartumVisit
	 * @return
	 */
	private String getPostpartumBpDetails(PostpartumVisit postpartumVisit) {
		StringBuilder sb = new StringBuilder();
		try {
			if(null != postpartumVisit.getFirstBp() && "" != postpartumVisit.getFirstBp()){
				sb.append(postpartumVisit.getFirstBp());
				sb.append(" (");
				sb.append(postpartumVisit.getBpDateOne());
				sb.append(" );");
			}
			if(null != postpartumVisit.getSecBp() && "" != postpartumVisit.getSecBp()){
				sb.append(postpartumVisit.getSecBp());
				sb.append(" (");
				sb.append(postpartumVisit.getBpDateSec());
				sb.append(" );");
			}
		} catch (Exception e) {
			logger.error("Error :: reporthandlerimpl : getPostpartumBPdetails method"+e);	
		}
		
		return sb.toString();
	}

	/**
	 * @param postpartumVisit
	 * @param symptom
	 * @return
	 */
	private String getPostpartumGeneralExamination(PostpartumVisit postpartumVisit, HashMap<String, String> symptom) {
		String generalExam ="";
		if(Constant.YELLOW.equalsIgnoreCase(postpartumVisit.getUpperEyeColor())){
			generalExam += symptom.get("PHRASES49")+", ";
		}
		if(Constant.PALE.equalsIgnoreCase(postpartumVisit.getLowerEyeColor())){
			generalExam += symptom.get("PHRASES50")+", ";
		}
		if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsoutOfBreath())){
			generalExam += symptom.get("PHRASES47")+", ";
		}
		if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsAnkleDepression())){
			generalExam += symptom.get("PHRASES51")+", ";
		}
		if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsEyeSwelling())){
			generalExam += symptom.get("PHRASES52")+", ";
		}
		if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getIsTalking())){
			generalExam += symptom.get("PHRASES48")+".";
		}
		return generalExam;
	}

	/**
	 * @param pregnancyVisit
	 * @param symptom 
	 * @return
	 */
	private String getPregnenecyGeneralExamination(PregnancyVisit pregnancyVisit, HashMap<String, String> symptom) {
		
		String generalExam ="";
		if(Constant.YELLOW.equalsIgnoreCase(pregnancyVisit.getUpperEyeColor())){
			generalExam += symptom.get("PHRASES49")+", ";
		}
		if(Constant.PALE.equalsIgnoreCase(pregnancyVisit.getLowerEyeColor())){
			generalExam += symptom.get("PHRASES50")+", ";
		}
		if(Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsoutOfBreath())){
			generalExam += symptom.get("PHRASES47")+", ";
		}
		if(Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsAnkleDepression())){
			generalExam += symptom.get("PHRASES51")+", ";
		}
		if(Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsEyeSwelling())){
			generalExam += symptom.get("PHRASES52")+", ";
		}
		if(Constant.Yes.equalsIgnoreCase(pregnancyVisit.getIsTalking())){
			generalExam += symptom.get("PHRASES48")+".";
		}
		return generalExam;
	}

	/**
	 * @param registration
	 * @return
	 */
	private int getGestationalAgePregnancy(Registration registration ,PregnancyVisit pregnancyVisit) {
		logger.info("Entering :: RegistrationHandlerImpl :: getGestationalAge method");
		Integer days=null;
		try {	
		String lmp = registration.getLmp();
		Date date =new Date();
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		String currentDate = df.format(date);
	
			Date lmpDate = df.parse(lmp);
			Date sysDate = df.parse(pregnancyVisit.getVisitDate());//take from postpartum an pregnancy
			long diff = (sysDate.getTime() - lmpDate.getTime())/(1000 * 60 * 60 * 24 * 7);
			 days = (int)diff;
		} catch (ParseException e) {
			logger.error("Error :: RegistrationHandlerImpl :: getGestationalAge method", e);
		}
		return days;
	}
	private int getGestationalAgePostpartum(Registration registration ,PostpartumVisit postpartumVisit) {
		logger.info("Entering :: RegistrationHandlerImpl :: getGestationalAge method");
		Integer days=null;
		try {	
		String lmp = registration.getLmp();
		Date date =new Date();
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		String currentDate = df.format(date);
	
			Date lmpDate = df.parse(lmp);
			Date sysDate = df.parse(postpartumVisit.getVisitDate());//take from postpartum an pregnancy
			long diff = (sysDate.getTime() - lmpDate.getTime())/(1000 * 60 * 60 * 24 * 7);
			 days = (int)diff;
		} catch (ParseException e) {
			logger.error("Error :: RegistrationHandlerImpl :: getGestationalAge method", e);
		}
		return days;
	}
	
	
	
	
	/**
	 * @param reportRequest
	 * @return
	 */
	@Override
	public MedicalCaseSheetDTO getCaseSheetReport(ReportRequest reportRequest) {

		logger.info("Entering :: RegistrationHandlerImpl :: createRegistration method");
		MedicalCaseSheetDTO response = new MedicalCaseSheetDTO();
		try {
			List<Registration> registrationList = reportDao.getReport(reportRequest);
			if(!StringUtil.isListNotNullNEmpty(registrationList)){
				response.setResponseCode(PhfiErrorCodes.REPORT_NOT_FOUND);
				response.setResponseMessage(Properties.getProperty(response.getResponseCode()));
				return response;
			}
			
			//get All Symptoms
			HashMap<String,String> symptom =new HashMap<String,String>();
			List<Symptom> symptomList = symptomDao.findAll();
			if(StringUtil.isListNotNullNEmpty(symptomList)){
				for(Symptom symptomData :symptomList){
					symptom.put(symptomData.getSymptomId(),symptomData.getSymptomPhrases());
				}
			}
			
			for(Registration registration :registrationList){
				
				try {
					response.setDeliveryDetails(getDeliveryDetails(registration));
					String findingAssessVisit[] = getFindings(registration,symptom).split(":");
					if(findingAssessVisit.length <= 10){
						response.setCurrentHistory(findingAssessVisit[0]);
						response.setIntialAsses(findingAssessVisit[1]);
						/*response.setNoOfVisit(findingAssessVisit[2]);*/
						response.setVisitDate(findingAssessVisit[3]);
						response.setGenralExamination(findingAssessVisit[4]);
						response.setBp(getBpDetails(registration));
						if(!Constant.POSTPARTUM.equalsIgnoreCase(registration.getMaternityStatus())){
						response.setWeight(getWeightDetails(registration));
					}
						response.setLabTest(getLabTestDetails(registration));
						if(findingAssessVisit.length>=7){
						response.setServerity(findingAssessVisit[8]);
						registration.setStatus(findingAssessVisit[8]);
						registrationDao.createRegistration(registration);
						}
						
					}
				} catch (Exception e) {
					logger.error("Error :: RegistrationHandlerImpl :: getCashSheetReport method", e);
				}
				//check for EDD
				String edd = getEDD(registration.getLmp());
				response.setEdd(edd);
				//check for trimester
				
				List<PregnancyVisit> pregnancyList = reportDao.findByWid(registration.getUid());
				
				List<PostpartumVisit> postpartumList = reportDao.findByPWid(registration.getUid());
				
				int noOfVisit =pregnancyList.size() + postpartumList.size();
				String visitType =Constant.PREGNANT;
				String visitDate ="";
				
				if(StringUtil.isListNotNullNEmpty(pregnancyList) && StringUtil.isListNotNullNEmpty(postpartumList)){
					String pregnancyVisitDate = pregnancyList.get(0).getVisitDate();
					String postpartumVistsDate = postpartumList.get(0).getVisitDate();
					
					visitDate = pregnancyVisitDate;
					DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
					try {
						Date preDate = df.parse(pregnancyVisitDate);
						Date postDate = df.parse(postpartumVistsDate);
						if(postDate.after(preDate)){
							visitType =Constant.POSTPARTUM;
							visitDate = postpartumVistsDate;
						}
					} catch (ParseException e) {
						logger.error("Error :: RegistrationHandlerImpl :: getCashSheetReport method", e);
					}
				}else if(StringUtil.isListNotNullNEmpty(postpartumList)){
					visitType =Constant.POSTPARTUM;
					visitDate = postpartumList.get(0).getVisitDate();
				}else if(StringUtil.isListNotNullNEmpty(pregnancyList)){
					visitType =Constant.PREGNANT;
					visitDate =  pregnancyList.get(0).getVisitDate();
				}else{
					visitType="";
				}
				
				int gestationAge =0;
				if(Constant.PREGNANT.equalsIgnoreCase(visitType)){
					gestationAge = getGestationalAgePregnancy(registration, pregnancyList.get(0));
				}else if(Constant.POSTPARTUM.equalsIgnoreCase(visitType)){
					gestationAge = getGestationalAgePostpartum(registration, postpartumList.get(0));
				}
				
				
				String trimester ="";
				if(gestationAge <= 12 ){
					trimester ="First Trimester";
				}else if(gestationAge >=13 && gestationAge <= 24){
					trimester = "Second Trimester";
				}else if(gestationAge >24){
					trimester ="Third Trimester";
				}else{
					
				}
				Delivery delivery = deleviryDao.getDeliveryDetailByWid(registration.getUid());
				
				if(null != delivery || Constant.POSTPARTUM.equalsIgnoreCase(registration.getMaternityStatus())){
					trimester = "Postpartum";
				}
			
				response.setName(registration.getWomenFirstName()+" "+registration.getWomenSurname());
				response.setWid(registration.getUid().toString());
				response.setObstetricScore(getObstetricScore(registration));
				response.setAge(null != registration.getAge()  ?registration.getAge().toString():null);
				response.setVillageName(registration.getVillageName());
				response.setTakul(registration.getTalukMarital());
				response.setLmp(registration.getLmp());
				response.setCurrentAddress(registration.getStreetMarital()+", "+registration.getVillageMarital()+", "+registration.getTalukMarital()+", "+
				registration.getDistrictMarital()+" ,"+registration.getLandmarkMarital());
				
				response.setAlternativeAddress(registration.getStreetNatal()+", "+registration.getVillageNatal()+", "+registration.getTalukNatal()+", "+
						registration.getDistrictNatal()+" ,"+registration.getLandmarkNatal());
				response.setTrimester(trimester);
				response.setDistric(registration.getDistrictMarital());
				response.setPredisposingFactor(getPredisposingFaction(registration));
				response.setPastHistory(getPastHistory(registration));
			}
		/*	response.setReportRequest(reportList);*/
			response.setNoOfRecords(1);
			response.setResponseCode(PhfiErrorCodes.PHFI_SUCCESS);
			response.setResponseMessage(Properties.getProperty(response.getResponseCode()));
		} catch (Exception e) {
			logger.error("Error :: RegistrationHandlerImpl :: createRegistration method", e);
			/*response.setResponseCode(PhfiErrorCodes.SYSTEM_ERROR);
			response.setResponseMessage(Properties.getProperty(PhfiErrorCodes.SYSTEM_ERROR));*/
		}
		logger.info("Exiting :: RegistrationHandlerImpl :: createRegistration method");

		return response;
	
	}

	private String getDeliveryDetails(Registration registration) {
		String deliveryDetails = "Not found";
		Delivery delivery = deleviryDao.getDeliveryDetailByWid(registration.getUid());
		if (null != delivery) {
			String babyStatus = "";
			if (Constant.Yes.equalsIgnoreCase(delivery.getIsBabyAlive())) {
				babyStatus = "live";
			}
			if (Constant.No.equalsIgnoreCase(delivery.getIsBabyAlive())) {
				babyStatus = "dead";
			}
			deliveryDetails ="Delivered a <b>" + babyStatus
					+ "</b> baby, weighing <b>" + delivery.getBabyWeight()
					+ "kg </b>, on <b>"
					+ delivery.getDeliveryDate() + "</b>. Type of delivery: <b>" + delivery.getDeliveryType()
					+ "</b>. Place: <b>" + delivery.getDeliveryPlace()
					+ "</b>. Conducted by: <b>"
					+ delivery.getDeliveryConductedBy() + "</b>";
					
		}
		return deliveryDetails;
	}

	private List<String> getLabTestDetails(Registration registration) {
		List<String>testResult = new ArrayList<String>();
		List<PregnancyVisit> pregnancyList = reportDao.findByWid(registration.getUid());
		List<PostpartumVisit> postpartumList = reportDao.findByPWid(registration.getUid());
		
		for(PostpartumVisit postpartumVisit :postpartumList ){
			/*try {
				if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getBp())){
					StringBuilder sb = new StringBuilder();
					sb.append("BP = ");
					if(null != postpartumVisit.getFirstBp() && "" != postpartumVisit.getFirstBp()){
						sb.append(postpartumVisit.getFirstBp());
						sb.append(" (");
						sb.append(postpartumVisit.getBpDateOne());
						sb.append(" );");
					}
					if(null != postpartumVisit.getSecBp() && "" != postpartumVisit.getSecBp()){
						sb.append(postpartumVisit.getSecBp());
						sb.append(" (");
						sb.append(postpartumVisit.getBpDateSec());
						sb.append(" );");
					}
					testResult.add(sb.toString());
				}
			} catch (Exception e) {
				logger.error("Error :: RegistrationHandlerImpl :: testresult BP method", e);
			}*/
			try {
				if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getHb())){
					StringBuilder sb = new StringBuilder();
					sb.append("HB = ");
					if(null != postpartumVisit.getFirstHb() && "" != postpartumVisit.getFirstHb()){
						sb.append(postpartumVisit.getFirstHb());
						sb.append(" (");
						sb.append(postpartumVisit.getHbDateOne());
						sb.append(" );");
					}
					if(null != postpartumVisit.getSecHb() && "" != postpartumVisit.getSecHb()){
						sb.append(postpartumVisit.getSecHb());
						sb.append(" (");
						sb.append(postpartumVisit.getHbDateSec());
						sb.append(" );");
					}
					testResult.add(sb.toString());
				}
			} catch (Exception e) {
				logger.error("Error :: RegistrationHandlerImpl :: getTestResult hb  method", e);
			}
			try {
				if(Constant.Yes.equalsIgnoreCase(postpartumVisit.getUrine())){
					StringBuilder sb = new StringBuilder();
					sb.append("Urine albumin = ");
					if(null != postpartumVisit.getFirstUrine() && "" != postpartumVisit.getFirstUrine()){
						sb.append(postpartumVisit.getFirstUrine());
						sb.append(" (");
						sb.append(postpartumVisit.getUrineDateOne());
						sb.append(" );");
					}
					if(null != postpartumVisit.getSecUrine() && "" != postpartumVisit.getSecUrine()){
						sb.append(postpartumVisit.getSecUrine());
						sb.append(" (");
						sb.append(postpartumVisit.getUrineDateSec());
						sb.append(" );");
					}
					testResult.add(sb.toString());
				}
			} catch (Exception e) {
				logger.error("Error :: RegistrationHandlerImpl :: getTestResult Urine  method", e);
			}
			try {
				if(Constant.DONE.equalsIgnoreCase(postpartumVisit.getMalaria())){
					StringBuilder sb = new StringBuilder();
					sb.append("Test for Malaria = ");
					if(null != postpartumVisit.getFirstMalaria() && "" != postpartumVisit.getFirstMalaria()){
						sb.append(postpartumVisit.getFirstMalaria());
						sb.append(" (");
						sb.append(postpartumVisit.getMalariaDateOne());
						sb.append(" );");
					}
					if(null != postpartumVisit.getSecMalaria() && "" != postpartumVisit.getSecMalaria()){
						sb.append(postpartumVisit.getSecMalaria());
						sb.append(" (");
						sb.append(postpartumVisit.getMalariaDateSec());
						sb.append(" );");
					}
					testResult.add(sb.toString());
				}
			} catch (Exception e) {
				logger.error("Error :: RegistrationHandlerImpl :: getTestResult maleria  method", e);
			}
			try {
				if(Constant.DONE.equalsIgnoreCase(postpartumVisit.getSputum())){
					StringBuilder sb = new StringBuilder();
					sb.append("Sputum Test = ");
					if(null != postpartumVisit.getSputumTest() && "" != postpartumVisit.getSputumTest()){
						sb.append(postpartumVisit.getSputumTest());
						sb.append(" (");
						sb.append(postpartumVisit.getSputumDate());
						sb.append(" );");
					}
					testResult.add(sb.toString());
				}
			} catch (Exception e) {
				logger.error("Error :: RegistrationHandlerImpl :: getTestResult sputum  method", e);
			}
		}
		
		for(PregnancyVisit pregnancyVisit : pregnancyList){
			/*try {
				if(Constant.DONE.equalsIgnoreCase(pregnancyVisit.getBp())){
					StringBuilder sb = new StringBuilder();
					sb.append("BP = ");
					if(null != pregnancyVisit.getFirstBp() && "" != pregnancyVisit.getFirstBp()){
						sb.append(pregnancyVisit.getFirstBp());
						sb.append(" (");
						sb.append(pregnancyVisit.getBpDateOne());
						sb.append(" );");
					}
					if(null != pregnancyVisit.getSecBp() && "" != pregnancyVisit.getSecBp()){
						sb.append(pregnancyVisit.getSecBp());
						sb.append(" (");
						sb.append(pregnancyVisit.getBpDateSec());
						sb.append(" );");
					}
					if(null != pregnancyVisit.getThirdBp() && "" != pregnancyVisit.getThirdBp()){
						sb.append(pregnancyVisit.getThirdBp());
						sb.append(" (");
						sb.append(pregnancyVisit.getBpDateThird());
						sb.append(" );");
					}
					if(null != pregnancyVisit.getFourBp() && "" != pregnancyVisit.getFourBp()){
						sb.append(pregnancyVisit.getFourBp());
						sb.append(" (");
						sb.append(pregnancyVisit.getBpDateFour());
						sb.append(" );");
					}
					testResult.add(sb.toString());
				}
			} catch (Exception e) {
				logger.error("Error :: RegistrationHandlerImpl :: testresult BP method", e);
			}*/
			try {
				if(Constant.DONE.equalsIgnoreCase(pregnancyVisit.getHb())){
					StringBuilder sb = new StringBuilder();
					sb.append("HB = ");
					if(null != pregnancyVisit.getFirstHb() && "" != pregnancyVisit.getFirstHb()){
						sb.append(pregnancyVisit.getFirstHb());
						sb.append(" (");
						sb.append(pregnancyVisit.getHbDateOne());
						sb.append(" );");
					}
					if(null != pregnancyVisit.getSecHb() && "" != pregnancyVisit.getSecHb()){
						sb.append(pregnancyVisit.getSecHb());
						sb.append(" (");
						sb.append(pregnancyVisit.getHbDateSec());
						sb.append(" );");
					}
					testResult.add(sb.toString());
				}
			} catch (Exception e) {
				logger.error("Error :: RegistrationHandlerImpl :: testresult BP method", e);
			}
			try {
				if(Constant.DONE.equalsIgnoreCase(pregnancyVisit.getUrine())){
					StringBuilder sb = new StringBuilder();
					sb.append("Urine albumin = ");
					if(null != pregnancyVisit.getFirstUrine() && "" != pregnancyVisit.getFirstUrine()){
						sb.append(pregnancyVisit.getFirstUrine());
						sb.append(" (");
						sb.append(pregnancyVisit.getUrineDateOne());
						sb.append(" );");
					}
					if(null != pregnancyVisit.getSecUrine() && "" != pregnancyVisit.getSecUrine()){
						sb.append(pregnancyVisit.getSecUrine());
						sb.append(" (");
						sb.append(pregnancyVisit.getUrineDateSec());
						sb.append(" );");
					}
					testResult.add(sb.toString());
				}
			} catch (Exception e) {
				logger.error("Error :: RegistrationHandlerImpl :: testresult Urine method", e);
			}
			try {
				if(Constant.DONE.equalsIgnoreCase(pregnancyVisit.getMalaria())){
					StringBuilder sb = new StringBuilder();
					sb.append("Test for Malaria = ");
					if(null != pregnancyVisit.getFirstMalaria() && "" != pregnancyVisit.getFirstMalaria()){
						sb.append(pregnancyVisit.getFirstMalaria());
						sb.append(" (");
						sb.append(pregnancyVisit.getMalariaDateOne());
						sb.append(" );");
					}
					if(null != pregnancyVisit.getSecMalaria() && "" != pregnancyVisit.getSecMalaria()){
						sb.append(pregnancyVisit.getSecMalaria());
						sb.append(" (");
						sb.append(pregnancyVisit.getMalariaDateSec());
						sb.append(" );");
					}
					testResult.add(sb.toString());
				}
			} catch (Exception e) {
				logger.error("Error :: RegistrationHandlerImpl :: test result Maleria method", e);
			}
			try {
				if(Constant.DONE.equalsIgnoreCase(pregnancyVisit.getSputum())){
					StringBuilder sb = new StringBuilder();
					sb.append("Sputum Test  = ");
					if(null != pregnancyVisit.getSputumTest() && "" != pregnancyVisit.getSputumTest()){
						sb.append(pregnancyVisit.getSputumTest());
						sb.append(" (");
						sb.append(pregnancyVisit.getSputumDate());
						sb.append(" );");
					}
					testResult.add(sb.toString());
				}
			} catch (Exception e) {
				logger.error("Error :: RegistrationHandlerImpl :: test result sputum method", e);
			}
			try {
				if(Constant.DONE.equalsIgnoreCase(pregnancyVisit.getUltrasound())){
					StringBuilder sb = new StringBuilder();
					sb.append("Ultrasound Scan = ");
					sb.append("USG done");
					if(null != pregnancyVisit.getUltrasoundDateOne() && "" != pregnancyVisit.getUltrasoundDateOne()){
						sb.append(" (");
						sb.append(pregnancyVisit.getUltrasoundDateOne());
						if(null != pregnancyVisit.getUltrasoundDateSec() && "" != pregnancyVisit.getUltrasoundDateSec()){
							sb.append(", ");
							sb.append(pregnancyVisit.getUltrasoundDateSec());
						}
						sb.append(" );");
					}
					testResult.add(sb.toString());
				}
			} catch (Exception e) {
				logger.error("Error :: RegistrationHandlerImpl :: test result ultrasound method", e);
			}
			try {
				if(Constant.DONE.equalsIgnoreCase(pregnancyVisit.getRbs())){
					StringBuilder sb = new StringBuilder();
					sb.append("RBS = ");
					if(null != pregnancyVisit.getFirstRbs() && "" != pregnancyVisit.getFirstRbs()){
						sb.append(pregnancyVisit.getFirstRbs());
						sb.append(" (");
						sb.append(pregnancyVisit.getRbsDateOne());
						sb.append(" );");
					}
					if(null != pregnancyVisit.getSecRbs() && "" != pregnancyVisit.getSecRbs()){
						sb.append(pregnancyVisit.getSecRbs());
						sb.append(" (");
						sb.append(pregnancyVisit.getRbsDateSec());
						sb.append(" );");
					}
					testResult.add(sb.toString());
				}
			} catch (Exception e) {
				logger.error("Error :: RegistrationHandlerImpl :: test result rbs method");
			}
		}
		return testResult;

	}

	private String getWeightDetails(Registration registration) {
		StringBuilder sb = new StringBuilder();
		List<PregnancyVisit> pregnancyList = reportDao.findByWid(registration.getUid());
		for (PregnancyVisit pregnancyVisit : pregnancyList) {
			try {

				if (null != pregnancyVisit.getFirstWeight()
						&& "" != pregnancyVisit.getFirstWeight()) {
					sb.append(pregnancyVisit.getFirstWeight());
					sb.append(" (");
					sb.append(pregnancyVisit.getWeightDateOne());
					sb.append(" );");
				}
				if (null != pregnancyVisit.getSecWeight()
						&& "" != pregnancyVisit.getSecWeight()) {
					sb.append(pregnancyVisit.getSecWeight());
					sb.append(" (");
					sb.append(pregnancyVisit.getWeightDateOne());
					sb.append(" );");
				}
				if (null != pregnancyVisit.getWeightDateThird()
						&& "" != pregnancyVisit.getWeightDateThird()) {
					sb.append(pregnancyVisit.getThirdWeight());
					sb.append(" (");
					sb.append(pregnancyVisit.getWeightDateThird());
					sb.append(" );");
				}
				if (null != pregnancyVisit.getFourthWeight()
						&& "" != pregnancyVisit.getFourthWeight()) {
					sb.append(pregnancyVisit.getFourthWeight());
					sb.append(" (");
					sb.append(pregnancyVisit.getWeightDateFour());
					sb.append(" );");
				}
			} catch (Exception e) {
				logger.error("Error : reporthandlerimpl :: getweighthistory :method"
						+ e);
			}
		}
	return sb.toString();
	}

	private String getBpDetails(Registration registration) {
		
		List<PregnancyVisit> pregnancyList = reportDao.findByWid(registration.getUid());
		
		List<PostpartumVisit> postpartumList = reportDao.findByPWid(registration.getUid());
		
		
		StringBuilder sb = new StringBuilder();
		for (PostpartumVisit postpartumVisit : postpartumList) {
			try {
				if (null != postpartumVisit.getFirstBp()
						&& "" != postpartumVisit.getFirstBp()) {
					sb.append(postpartumVisit.getFirstBp());
					sb.append(" (");
					sb.append(postpartumVisit.getBpDateOne());
					sb.append(" );");
				}
				if (null != postpartumVisit.getSecBp()
						&& "" != postpartumVisit.getSecBp()) {
					sb.append(postpartumVisit.getSecBp());
					sb.append(" (");
					sb.append(postpartumVisit.getBpDateSec());
					sb.append(" );");
				}
			} catch (Exception e) {
				logger.error("Error :: reporthandlerimpl : getPostpartumBPdetails method"
						+ e);
			}
		}
		
		for (PregnancyVisit pregnancyVisit : pregnancyList) {
			try {

				if (null != pregnancyVisit.getFirstBp()
						&& "" != pregnancyVisit.getFirstBp()) {
					sb.append(pregnancyVisit.getFirstBp());
					sb.append(" (");
					sb.append(pregnancyVisit.getBpDateOne());
					sb.append(" );");
				}
				if (null != pregnancyVisit.getSecBp()
						&& "" != pregnancyVisit.getSecBp()) {
					sb.append(pregnancyVisit.getSecBp());
					sb.append(" (");
					sb.append(pregnancyVisit.getBpDateSec());
					sb.append(" );");
				}
				if (null != pregnancyVisit.getThirdBp()
						&& "" != pregnancyVisit.getThirdBp()) {
					sb.append(pregnancyVisit.getThirdBp());
					sb.append(" (");
					sb.append(pregnancyVisit.getBpDateThird());
					sb.append(" );");
				}
				if (null != pregnancyVisit.getFourBp()
						&& "" != pregnancyVisit.getFourBp()) {
					sb.append(pregnancyVisit.getFourBp());
					sb.append(" (");
					sb.append(pregnancyVisit.getBpDateFour());
					sb.append(" );");
				}
			} catch (Exception e) {
				logger.error("Error :: reporthandlerimpl : getPreganancyBPdetails method"
						+ e);
			}
		}
	return sb.toString();
	}

	/**
	 * @param registration
	 * @return
	 */
	private String getPastHistory(Registration registration) {
		StringBuilder sb = new StringBuilder();
		try {
			if(Constant.Yes.equalsIgnoreCase(registration.getDiabetes())){
				sb.append(Condition.PASTHISTORY1+", ");
			}
			if(Constant.Yes.equalsIgnoreCase(registration.getHypertension())){
				sb.append(Condition.PASTHISTORY2+", ");
			}
			if(Constant.Yes.equalsIgnoreCase(registration.getHeartdisease())){
				sb.append(Condition.PASTHISTORY3+", ");
			}
			if(Constant.Yes.equalsIgnoreCase(registration.getAnaemia())){
				sb.append(Condition.PASTHISTORY4+", ");
			}
			if(Constant.Yes.equalsIgnoreCase(registration.getThyroidproblem())){
				sb.append(Condition.PASTHISTORY5+", ");
			}
			if(null !=registration.getCaesarean()){
			if(registration.getCaesarean()>0){
				sb.append("h/o previous lscs"+", ");
			}}
			if(Constant.Yes.equalsIgnoreCase(registration.getBleedexcessively())){
				sb.append("h/o previous PPH."+", ");
			}
			
			if(Constant.Yes.equalsIgnoreCase(registration.getBreathlessness())){
				sb.append("h/o breathlessness in last pregnancy"+", ");
			}
			if(Constant.Yes.equalsIgnoreCase(registration.getSeverepallor())){
				sb.append("h/o pallor in last pregnancy"+", ");
			}
			if(null != registration.getEarlyDelivery()){
				if(registration.getEarlyDelivery() >=1){
					sb.append("Bad obstetric history");
					
				}
			}
			
			
		} catch (Exception e) {
			logger.error("Error :: reporthandlerimpl : getpastHistory method"+e);
		}
		
		return sb.toString();
	}

	/**
	 * @param registration
	 * @return
	 */
	private String getPredisposingFaction(Registration registration) {
		StringBuilder sb = new StringBuilder();
		try {
			if(17 >= registration.getAge()){
				sb.append(Condition.FACTOR1+", ");
			}
			if(35 <= registration.getAge()){
				sb.append(Condition.FACTOR2 + ", ");
			}
			String recentDel = registration.getDateOfRecentDelivery();
			String lmp = registration.getLmp();
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

			Date d1 = null;
			Date d2 = null;
			int diffDate =0;
				try {
					if("" !=recentDel && "" !=lmp && null !=recentDel && null!=lmp){
					d1 = format.parse(recentDel);
					d2 = format.parse(lmp);
					DateTime dt1 = new DateTime(d1);
					DateTime dt2 = new DateTime(d2);
					diffDate =Years.yearsBetween(dt1, dt2).getYears();
					if(diffDate < 2){
						sb.append(Condition.FACTOR3+", ");
					}
					}
				} catch (ParseException e) {
					logger.error("Error :: RegistrationHandlerImpl :: getPredisposingFaction method", e);
				}
				
				int p = getP(registration);
				if(p >= 3){
					sb.append(Condition.FACTOR4+", ");
				}
				if( 140  > registration.getHeight()){
					sb.append(Condition.FACTOR5+", ");
				}
				List<PregnancyVisit> pregnancyList = reportDao.findByWid(registration.getUid());
			for (PregnancyVisit pregnancy : pregnancyList) {
				if (pregnancy.getFirstWeight() != "" && pregnancy.getFirstWeight() != null) {
					if (Integer.parseInt(pregnancy.getFirstWeight()) < 40) {
						sb.append(Condition.FACTOR6);
						break;
					}
				}
				if (pregnancy.getSecWeight() != "" && pregnancy.getSecWeight() != null) {
					if (Integer.parseInt(pregnancy.getFirstWeight()) < 40) {
						sb.append(Condition.FACTOR6);
						break;
					}
				}
				if (pregnancy.getThirdWeight() != "" && pregnancy.getThirdWeight() != null) {
					if (Integer.parseInt(pregnancy.getThirdWeight()) < 40) {
						sb.append(Condition.FACTOR6);
						break;
					}
				}
				if (pregnancy.getFourthWeight() != "" && pregnancy.getFourthWeight() != null) {
					if (Integer.parseInt(pregnancy.getFourthWeight()) < 40) {
						sb.append(Condition.FACTOR6);
						break;
					}
				}
			}
				
				
				
		} catch (Exception e) {
			logger.error("Error :: RegistrationHandlerImpl :: getPredispogingfactor method", e);
		}
			
		return sb.toString();
	}

	/**
	 * @param registration
	 * @return
	 */
	private int getP(Registration registration) {
		int g =0;
		int a = 0;//g-1;// total didnit cross 7 month
	
		Delivery delivery = deleviryDao.getDeliveryDetailByWid(registration.getUid());
		if(null !=registration.getPregnancyCount()){
			g = registration.getPregnancyCount();
		}
		if(null !=registration.getEarlyDelivery()){
			a = registration.getEarlyDelivery();
		}
		int p = g-a-1;
		 if("Postpartum".equalsIgnoreCase(registration.getMaternityStatus())){
			if (null != delivery.getPregnancyLast() && "" != delivery.getPregnancyLast()){
				if (Integer.parseInt(delivery.getPregnancyLast()) < 7) {
					p =g-a-1;
				}else if(Integer.parseInt(delivery.getPregnancyLast()) >= 7){
					p=g-a;
				}
			}
		 }
		return p;	
		
	}

	/**
	 * @param lmp
	 * @return
	 */
	private String getEDD(String lmp) {
	
		long totalSec =0;
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		try {
			Date lmpDate = df.parse(lmp);
			Long days = 1000*60*60*24*280L;
			totalSec = (lmpDate.getTime() + days);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Date d1 = new Date();
		d1.setTime(totalSec);
		 String edd = df.format(d1);
		return edd;
	}

	/**
	 * @return
	 */
	@Override
	public PhfiRegistrationResponse getMasterRawData() {
	logger.info("Entering :: RegistrationHandlerImpl :: createRegistration method");
	PhfiRegistrationResponse rawMasterData = new PhfiRegistrationResponse();
	try {
		List<Registration> registrationRawData = reportDao.getMasterRawData();
		if(!StringUtil.isListNotNullNEmpty(registrationRawData)){
			rawMasterData.setResponseCode(PhfiErrorCodes.PHFI_SUCCESS);
			rawMasterData.setResponseMessage(Properties.getProperty(rawMasterData.getResponseCode()));
		}
		List<PhfiRegistrationRequest> registrationData = CommonUtil.copyListBeanProperty(registrationRawData, PhfiRegistrationRequest.class);
		rawMasterData.setPhfiRegistrationRequest(registrationData);
		rawMasterData.setResponseCode(PhfiErrorCodes.PHFI_SUCCESS);
		rawMasterData.setResponseMessage(Properties.getProperty(rawMasterData.getResponseCode()));
	} catch (Exception e) {
		logger.error("Error :: RegistrationHandlerImpl :: createRegistration method", e);
		rawMasterData.setResponseCode(PhfiErrorCodes.SYSTEM_ERROR);
		rawMasterData.setResponseMessage(Properties.getProperty(PhfiErrorCodes.SYSTEM_ERROR));
	}
	logger.info("Exiting :: RegistrationHandlerImpl :: createRegistration method");

	return rawMasterData;
	}

	/**
	 * @return
	 */
	@Override
	public PhfiVisitResponse getPregnancyRawData() {
		logger.info("Entering :: RegistrationHandlerImpl :: createRegistration method");
		PhfiVisitResponse rawMasterData = new PhfiVisitResponse();
		try {
			List<PregnancyVisit> registrationRawData = reportDao.getPregnancyRawData();
			if(!StringUtil.isListNotNullNEmpty(registrationRawData)){
				rawMasterData.setResponseCode(PhfiErrorCodes.PHFI_SUCCESS);
				rawMasterData.setResponseMessage(Properties.getProperty(rawMasterData.getResponseCode()));
			}
			List<PhfiVisitRequest> registrationData = CommonUtil.copyListBeanProperty(registrationRawData, PhfiVisitRequest.class);
			rawMasterData.setPhfiVisitRequest(registrationData);
			rawMasterData.setResponseCode(PhfiErrorCodes.PHFI_SUCCESS);
			rawMasterData.setResponseMessage(Properties.getProperty(rawMasterData.getResponseCode()));
		} catch (Exception e) {
			logger.error("Error :: RegistrationHandlerImpl :: createRegistration method", e);
			rawMasterData.setResponseCode(PhfiErrorCodes.SYSTEM_ERROR);
			rawMasterData.setResponseMessage(Properties.getProperty(PhfiErrorCodes.SYSTEM_ERROR));
		}
		logger.info("Exiting :: RegistrationHandlerImpl :: createRegistration method");

		return rawMasterData;
	}

	/**
	 * @return
	 */
	@Override
	public PhfiPostPartumVisitResponse getPostpartumRawData() {
		logger.info("Entering :: RegistrationHandlerImpl :: createRegistration method");
		PhfiPostPartumVisitResponse rawMasterData = new PhfiPostPartumVisitResponse();
		try {
			List<PostpartumVisit> registrationRawData = reportDao.getPostpartumRawData();
			if(!StringUtil.isListNotNullNEmpty(registrationRawData)){
				rawMasterData.setResponseCode(PhfiErrorCodes.PHFI_SUCCESS);
				rawMasterData.setResponseMessage(Properties.getProperty(rawMasterData.getResponseCode()));
			}
			List<PhfiPostPartumVisitRequest> registrationData = CommonUtil.copyListBeanProperty(registrationRawData, PhfiPostPartumVisitRequest.class);
			rawMasterData.setPhfiPostPartumVisitRequest(registrationData);
			rawMasterData.setResponseCode(PhfiErrorCodes.PHFI_SUCCESS);
			rawMasterData.setResponseMessage(Properties.getProperty(rawMasterData.getResponseCode()));
		} catch (Exception e) {
			logger.error("Error :: RegistrationHandlerImpl :: createRegistration method", e);
			rawMasterData.setResponseCode(PhfiErrorCodes.SYSTEM_ERROR);
			rawMasterData.setResponseMessage(Properties.getProperty(PhfiErrorCodes.SYSTEM_ERROR));
		}
		logger.info("Exiting :: RegistrationHandlerImpl :: createRegistration method");

		return rawMasterData;
	}

	/**
	 * @return
	 */
	@Override
	public PhfiDeliveryFormResponse getDeliveryRawData() {
		logger.info("Entering :: RegistrationHandlerImpl :: createRegistration method");
		PhfiDeliveryFormResponse rawMasterData = new PhfiDeliveryFormResponse();
		try {
			List<Delivery> registrationRawData = reportDao.getDeliveryRawData();
			if(!StringUtil.isListNotNullNEmpty(registrationRawData)){
				rawMasterData.setResponseCode(PhfiErrorCodes.PHFI_SUCCESS);
				rawMasterData.setResponseMessage(Properties.getProperty(rawMasterData.getResponseCode()));
			}
			List<PhfiDeliveryFormRequest> registrationData = CommonUtil.copyListBeanProperty(registrationRawData, PhfiDeliveryFormRequest.class);
			rawMasterData.setPhfiDeliveryFormRequest(registrationData);
			rawMasterData.setResponseCode(PhfiErrorCodes.PHFI_SUCCESS);
			rawMasterData.setResponseMessage(Properties.getProperty(rawMasterData.getResponseCode()));
		} catch (Exception e) {
			logger.error("Error :: RegistrationHandlerImpl :: createRegistration method", e);
			rawMasterData.setResponseCode(PhfiErrorCodes.SYSTEM_ERROR);
			rawMasterData.setResponseMessage(Properties.getProperty(PhfiErrorCodes.SYSTEM_ERROR));
		}
		logger.info("Exiting :: RegistrationHandlerImpl :: createRegistration method");

		return rawMasterData;
	}
	
	/**
	 * @param reportRequest
	 * @return ReportResponse
	 */
	
	@Override
	public DoctorReportResponse getDoctorReport(PhfiDoctorFormRequest phfiDoctorFormRequest) {
		logger.info("Entering :: ReportHandlerImpl :: getDoctorReport method");
		DoctorReportResponse response = new DoctorReportResponse();
		try {
			ReportRequest reportRequest = new ReportRequest();
			reportRequest.setWid(phfiDoctorFormRequest.getWid());
			reportRequest.setAnmName(phfiDoctorFormRequest.getAnmName());
			reportRequest.setNameOfAsha(phfiDoctorFormRequest.getNameOfAsha());
			
			/*
			if(!StringUtil.isListNotNullNEmpty(doctorList)){
				response.setResponseCode(PhfiErrorCodes.REPORT_NOT_FOUND);
				response.setResponseMessage(Properties.getProperty(response.getResponseCode()));
				return response;
			}
			*/
			//get All Symptoms
			HashMap<String,String> symptom =new HashMap<String,String>();
			List<Symptom> symptomList = symptomDao.findAll();
			if(StringUtil.isListNotNullNEmpty(symptomList)){
				for(Symptom symptomData :symptomList){
					symptom.put(symptomData.getSymptomId(),symptomData.getSymptomPhrases());
				}
			}
			List<PhfiDoctorFormRequest> reportList = new ArrayList<PhfiDoctorFormRequest>();
			List<Registration> reportList1 = reportDao.getReport(reportRequest);
			for (Registration report : reportList1) {
				PhfiDoctorFormRequest phfiDoctorRequest = new PhfiDoctorFormRequest();
				phfiDoctorRequest.setWid(report.getUid());
				List<Doctor> doctorList = reportDao.getDoctorReport(phfiDoctorRequest);
				for (Doctor doctor : doctorList) {
					PhfiDoctorFormRequest request = new PhfiDoctorFormRequest();
					request.setSlNo(doctor.getId());
					request.setWomanName(report.getWomenFirstName()+" "+ report.getWomenSurname());
					request.setWid(report.getUid());
					request.setDaysToDeliver(getDaysToDeliver(report));
					request.setObstic(getObstetricScore(report));
					request.setNameOfAsha(report.getAshaName());
					request.setAge(String.valueOf(report.getAge()));
					/*request.setHistory(doctor.getHistory());
					request.setLabtest(doctor.getLabtest());*/
					request.setHealth(doctor.getHealth());
					request.setAdvice(doctor.getAdvice());
					request.setDiagonosis(doctor.getDiagonosis());
					request.setAssesmentstatus(doctor.getAssesmentstatus());
					request.setInvestigations(doctor.getInvestigations());
					request.setMedication(doctor.getMedication());
					reportList.add(request);
				}
			}
			response.setDoctorReportRequest(reportList);
			response.setNoOfRecords(reportList.size());
			response.setResponseCode(PhfiErrorCodes.PHFI_SUCCESS);
			response.setResponseMessage(Properties.getProperty(response.getResponseCode()));
		} catch (Exception e) {
			logger.error("Error :: ReportHandlerImpl :: getDoctorReport method", e);
			response.setResponseCode(PhfiErrorCodes.SYSTEM_ERROR);
			response.setResponseMessage(Properties.getProperty(PhfiErrorCodes.SYSTEM_ERROR));
		}
		logger.info("Exiting :: ReportHandlerImpl :: getDoctorReport method");

		return response;
	}



}
