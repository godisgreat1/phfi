/**
 * 
 */
package com.smh.dao.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * << Add Comments Here >>
 *
 * @author Shekhar Prasad
 * @date Mar 6, 2016 12:09:21 AM
 * @version 1.0
 */

@Entity
@Table(name = "DOCTOR")
public class Doctor implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3023045629908139581L;
	@Id
    @GeneratedValue(strategy =GenerationType.AUTO)
    @Column(name = "ID")
    private Integer id;
	
	@Column(name = "DOCTOR_DATE")
	private String date;
	
	@Column(name = "PLACE")
	private String place;
	
	@Column(name = "DOCTOR_NAME")
	private String doctorName;
	
	@Column(name = "QUALIFICATION")
	private String qualification;
	
	@Column(name = "WOMAN_NAME")
	private String womanName;
	
	@Column(name = "AGE")
	private String age;
	
	@Column(name = "YEARS")
	private String years;
	
	@Column(name = "WID")
	private Integer wid;
	
	@Column(name = "VILLAGE")
	private String village;
	
	@Column(name = "TALUK")
	private String taluk;
	
	@Column(name = "DISTRICT")
	private String district;
	
	@Column(name = "ADDRESS_MARTIAL")
	private String addressMartial;
	
	@Column(name = "ADDRESS_NATAL")
	private String addressnatal;
	
	@Column(name = "OBSTIC")
	private String obstic;
	
	@Column(name = "LMP")
	private String lmp;
	
	@Column(name = "DOD")
	private String dod;
	
	@Column(name = "HISTORY")
	private String history;
	
	@Column(name = "LAB_TEST")
	private String labtest;
	
	@Column(name = "DIAGONOSIS")
	private String diagonosis;
	
	@Column(name = "ASSESMENT_STATUS")
	private String assesmentstatus;
	
	@Column(name = "ADVICE")
	private String advice;
	
	@Column(name = "INVESTIGATIONS")
	private String investigations;
	
	@Column(name = "MEDICATION")
	private String medication;
	
	@Column(name = "HEALTH")
	private String health;
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}
	/**
	 * @return the place
	 */
	public String getPlace() {
		return place;
	}
	/**
	 * @param place the place to set
	 */
	public void setPlace(String place) {
		this.place = place;
	}
	/**
	 * @return the doctorName
	 */
	public String getDoctorName() {
		return doctorName;
	}
	/**
	 * @param doctorName the doctorName to set
	 */
	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}
	/**
	 * @return the qualification
	 */
	public String getQualification() {
		return qualification;
	}
	/**
	 * @param qualification the qualification to set
	 */
	public void setQualification(String qualification) {
		this.qualification = qualification;
	}
	/**
	 * @return the womanName
	 */
	public String getWomanName() {
		return womanName;
	}
	/**
	 * @param womanName the womanName to set
	 */
	public void setWomanName(String womanName) {
		this.womanName = womanName;
	}
	/**
	 * @return the age
	 */
	public String getAge() {
		return age;
	}
	/**
	 * @param age the age to set
	 */
	public void setAge(String age) {
		this.age = age;
	}
	/**
	 * @return the years
	 */
	public String getYears() {
		return years;
	}
	/**
	 * @param years the years to set
	 */
	public void setYears(String years) {
		this.years = years;
	}
	
	/**
	 * @return the village
	 */
	public String getVillage() {
		return village;
	}
	/**
	 * @param village the village to set
	 */
	public void setVillage(String village) {
		this.village = village;
	}
	/**
	 * @return the taluk
	 */
	public String getTaluk() {
		return taluk;
	}
	/**
	 * @param taluk the taluk to set
	 */
	public void setTaluk(String taluk) {
		this.taluk = taluk;
	}
	/**
	 * @return the district
	 */
	public String getDistrict() {
		return district;
	}
	/**
	 * @param district the district to set
	 */
	public void setDistrict(String district) {
		this.district = district;
	}
	/**
	 * @return the addressMartial
	 */
	public String getAddressMartial() {
		return addressMartial;
	}
	/**
	 * @param addressMartial the addressMartial to set
	 */
	public void setAddressMartial(String addressMartial) {
		this.addressMartial = addressMartial;
	}
	/**
	 * @return the addressnatal
	 */
	public String getAddressnatal() {
		return addressnatal;
	}
	/**
	 * @param addressnatal the addressnatal to set
	 */
	public void setAddressnatal(String addressnatal) {
		this.addressnatal = addressnatal;
	}
	/**
	 * @return the obstic
	 */
	public String getObstic() {
		return obstic;
	}
	/**
	 * @param obstic the obstic to set
	 */
	public void setObstic(String obstic) {
		this.obstic = obstic;
	}
	/**
	 * @return the lmp
	 */
	public String getLmp() {
		return lmp;
	}
	/**
	 * @param lmp the lmp to set
	 */
	public void setLmp(String lmp) {
		this.lmp = lmp;
	}
	/**
	 * @return the dod
	 */
	public String getDod() {
		return dod;
	}
	/**
	 * @param dod the dod to set
	 */
	public void setDod(String dod) {
		this.dod = dod;
	}
	/**
	 * @return the history
	 */
	public String getHistory() {
		return history;
	}
	/**
	 * @param history the history to set
	 */
	public void setHistory(String history) {
		this.history = history;
	}
	/**
	 * @return the labtest
	 */
	public String getLabtest() {
		return labtest;
	}
	/**
	 * @param labtest the labtest to set
	 */
	public void setLabtest(String labtest) {
		this.labtest = labtest;
	}
	/**
	 * @return the diagonosis
	 */
	public String getDiagonosis() {
		return diagonosis;
	}
	/**
	 * @param diagonosis the diagonosis to set
	 */
	public void setDiagonosis(String diagonosis) {
		this.diagonosis = diagonosis;
	}
	/**
	 * @return the assesmentstatus
	 */
	public String getAssesmentstatus() {
		return assesmentstatus;
	}
	/**
	 * @param assesmentstatus the assesmentstatus to set
	 */
	public void setAssesmentstatus(String assesmentstatus) {
		this.assesmentstatus = assesmentstatus;
	}
	/**
	 * @return the advice
	 */
	public String getAdvice() {
		return advice;
	}
	/**
	 * @param advice the advice to set
	 */
	public void setAdvice(String advice) {
		this.advice = advice;
	}
	/**
	 * @return the investigations
	 */
	public String getInvestigations() {
		return investigations;
	}
	/**
	 * @param investigations the investigations to set
	 */
	public void setInvestigations(String investigations) {
		this.investigations = investigations;
	}
	/**
	 * @return the medication
	 */
	public String getMedication() {
		return medication;
	}
	/**
	 * @param medication the medication to set
	 */
	public void setMedication(String medication) {
		this.medication = medication;
	}
	/**
	 * @return the health
	 */
	public String getHealth() {
		return health;
	}
	/**
	 * @param health the health to set
	 */
	public void setHealth(String health) {
		this.health = health;
	}
	public Integer getWid() {
		return wid;
	}
	public void setWid(Integer wid) {
		this.wid = wid;
	}

}
