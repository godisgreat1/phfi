function cancel() {
	window.location.href = 'registration-search';
}

function validateAge(){
		var age = getVal('age').trim();
		var ageInt = parseInt(age);
		var regx = /^[0-9]+$/;
		if(isEmpty(age)){
			setDiv("ageErrorDiv", "This field is mandatory");
			return false;
		}
		if(!regx.test(age)){
			setDiv("ageErrorDiv", "Enter numeric value only ");
			return false;
		}else if (!(ageInt > 11 && ageInt <51)) {
			setDiv("ageErrorDiv", "Enter age between 12 and 50");
			return false;
		}
		else{
			setDiv("ageErrorDiv", "");
			return true;
		}
}

function pregnancyCount(){
	var pregnancyCount = getVal('pregnancyCount').trim();
	var pregnancyCountInt = parseInt(pregnancyCount);
	var regx = /^[0-9]+$/;
	if(isEmpty(pregnancyCount)){
		setDiv("pregnancyCountErrorDiv", "This field is mandatory");
		return false;
	}
	if(!regx.test(pregnancyCount)){
		setDiv("pregnancyCountErrorDiv", "Enter numeric value only ");
		return false;
	}else if (!(pregnancyCountInt >= 0 && pregnancyCountInt < 10)) {
		setDiv("pregnancyCountErrorDiv", "Enter between 1 and 9");
		return false;
	}
	else{
		setDiv("pregnancyCountErrorDiv", "");
		return true;
	}
}

function livingChilden(){
	var livingChilden = getVal('noOfChildren').trim();
	var livingInt =parseInt(livingChilden);
	var pregnancyCount = parseInt(getVal('pregnancyCount').trim());
	var max = parseInt(pregnancyCount - 1)*2;
	var regx = /^[0-9]+$/;
	if(isEmpty(livingChilden)){
		setDiv("noOfChildrenErrorDiv", "This field is mandatory");
		return false;
	}
	if(!regx.test(livingChilden)){
		setDiv("noOfChildrenErrorDiv", "Enter numeric value only ");
		return false;
	}else if (!(livingInt >=0 && livingInt <= max) ){
		setDiv("noOfChildrenErrorDiv", "Enter between 0 and "+max);
		return false;
	}
	else{
		setDiv("noOfChildrenErrorDiv", "");
		return true;
	}
}
function validateEarlyDelivery(){
	var earlyDelivery = getVal('earlyDelivery').trim();
	var earlyDeliveryInt =parseInt(earlyDelivery);
	var pregnancyCount = getVal('pregnancyCount').trim();
	var max = parseInt(pregnancyCount - 1);
	var regx = /^[0-9]+$/;
	if(isEmpty(earlyDelivery)){
		setDiv("earlyDeliveryErrorDiv", "This field is mandatory");
		return false;
	}
	if(!regx.test(earlyDelivery)){
		setDiv("earlyDeliveryErrorDiv", "Enter numeric value only ");
		return false;
	}else if (!(earlyDeliveryInt >=0 && earlyDeliveryInt <= max)) {
		setDiv("earlyDeliveryErrorDiv", "Enter between 0 and "+max);
		return false;
	}
	else{
		setDiv("earlyDeliveryErrorDiv", "");
		return true;
	}
}


function validateCaesarean(){
	var caesarean = getVal('caesarean').trim();
	var caesareanInt =parseInt(caesarean);
	var pregnancyCount = getVal('pregnancyCount').trim();
	var max = parseInt(pregnancyCount - 1);
	var regx = /^[0-9]+$/;
	if(isEmpty(caesarean)){
		setDiv("caesareanErrorDiv", "This field is mandatory");
		return false;
	}
	if(!regx.test(caesarean)){
		setDiv("earlyDeliveryErrorDiv", "Enter numeric value only ");
		return false;
	}else if (!(caesareanInt >=0 && caesareanInt <= max)) {
		setDiv("caesareanErrorDiv", "Enter between 0 and "+max);
		return false;
	}
	else{
		setDiv("caesareanErrorDiv", "");
		return true;
	}
}

/*var canProceed = false;
function validateHeight(){
	if(!canProceed) {
		var height = getVal('height').trim();
		var heightInt = parseInt(height);
		var regx = /^[0-9]+$/;
		var flag = false;
		var mandatotyErrMsg = "";
		var rangeErrMsg = "";
		if(isEmpty(height)){
			mandatotyErrMsg = "This field is mandatory";
			setDiv("heightErrorDiv", mandatotyErrMsg);
		} else if(!regx.test(height)){
			rangeErrMsg = "Enter numeric value only";
			setDiv("heightErrorDiv", rangeErrMsg);
		} else if (!(heightInt >= 120 && heightInt <= 180) ){
			rangeErrMsg = "Enter between 120 and 180";
			setDiv("heightErrorDiv", rangeErrMsg);
		} else{
			setDiv("heightErrorDiv", "");
			flag = true;
		}
		if(!flag) {
			var errMsg = "";
			if(mandatotyErrMsg.length > 0) {
				errMsg += "\n Please Enter A Value.";
			} else {
				errMsg += "\n Please Verify And Re-enter.";
			}
			alert(errMsg); 
			$('#height').focus();
		}
		return flag;
	} else {
		return true;
	}
	
}*/


function resetAsha(){
		get('regDate').value = "";
		get('villageId').value = "";
		get('uid').value = "";
		get('villageName').value = "";
		get('phc').value = "";
		get('ashaName').value = "";
		get('anmName').value = "";
		get('ashaFacilitatorName').value = "";
}

function priliminaryReset(){
	get('womenFirstName').value = "";
	get('womenHusbandName').value = "";
	get('womenSurname').value = "";
	get('husbandFirstName').value = "";
	get('husbandFatherName').value = "";
	get('husbandSurname').value = "";
}

function resetContact(){
	 get('streetMarital').value = "";
	 get('landmarkMarital').value = "";
	 get('villageMarital').value = "";
	 get('talukMarital').value = "";
	 get('districtMarital').value = "";
	 get('phone1Marital').value = "";
	 get('phone2Marital').value = "";
	 get('streetNatal').value = "";
	 get('landmarkNatal').value = "";
	 get('villageNatal').value = "";
	 get('talukNatal').value = "";
	 get('districtNatal').value = "";
	 get('phone1Natal').value = "";
	 get('phone2Natal').value = "";
	 get('currentPlace').value = "";
}

function resetPersonal(){
	get('age').value = "";
	get('education').value = "";
	get('religion').value = "";
	get('caste').value = "";
	get('castcategory').value = "";
}

function resetObstetric(){
	get('pregnancyCount').value = "";
	get('noOfChildren').value = "";
	get('dateOfRecentDelivery').value = "";
	get('earlyDelivery').value = "";
	get('caesarean').value = "";
}

function resetOther(){
 get('lmp').value = "";
 get('height').value = "";
 get('bloodgroup').value = "";
}

function validateAsha(){
	
		setLable('confirmregDate', get('regDate').value.trim());
		setLable('confirmVillageId', get('villageId').value.trim());
		setLable('confirmUuid', get('uid').value.trim());
		setLable('confirmVillage', get('villageName').value.trim());
		setLable('confirmPhc', get('phc').value.trim());
		setLable('confirmAshaName', get('ashaName').value.trim());
		setLable('confirmANM', get('anmName').value.trim());
		setLable('confirmAshsaFacil', get('ashaFacilitatorName').value.trim());
	
		return true;
}


function validatePreliminary(){
	
		setLable('confirmWomenFirstName', get('womenFirstName').value.trim());
		setLable('confirmWomenHusbandname', get('womenHusbandName').value.trim());
		setLable('confirmWomenSurname', get('womenSurname').value.trim());
		setLable('confirmHusbandFirstName', get('husbandFirstName').value.trim());
		setLable('confirmFatherName', get('husbandFatherName').value.trim());
		setLable('confirmHusbandSurname', get('husbandSurname').value.trim());
		
		setLable('confirmMaternityStatus', $('input[name=maternityStatus]:checked').val().trim());
		setLable('confirmRegistrationDate', get('regDate').value.trim());
		
		return true;
}

function validateContact(){
	
		setLable('confirmStreetMarital', get('streetMarital').value.trim());
		setLable('confirmLandmarkMarital', get('landmarkMarital').value.trim());
		setLable('confirmVillageMarital', get('villageMarital').value.trim());
		setLable('confirmTalukMarital', get('talukMarital').value.trim());
		setLable('confirmDistrictMarital', get('districtMarital').value.trim());
		setLable('confirmPhone1Marital', get('phone1Marital').value.trim());
		setLable('confirmPhone2Marital', get('phone2Marital').value.trim());
		setLable('confirmStreetNatal', get('streetNatal').value.trim());
		setLable('confirmLandmarkNatal', get('landmarkNatal').value.trim());
		setLable('confirmVillageNatal', get('villageNatal').value.trim());
		setLable('confirmTalukNatal', get('talukNatal').value.trim());
		setLable('confirmDistrictNatal', get('districtNatal').value.trim());
		setLable('confirmPhone1Natal', get('phone1Natal').value.trim());
		setLable('confirmPhone2Natal', get('phone2Natal').value.trim());
		setLable('confirmCurrentPlace', get('currentPlace').value.trim());
		
		return true;
}




function validatePersonal(){

		setLable('confirmAge', get('age').value.trim());
		setLable('confirmEducation', get('education').value.trim());
		setLable('confirmRelegion', get('religion').value.trim());
		setLable('confirmCaste', get('caste').value.trim());
		setLable('confirmCasteCategory', get('castcategory').value.trim());
		
		return true;
}

function setPastHistory(){
	setLable('confirmdiabetes', $('input[name=diabetes]:checked').val());
	setLable('confirmhypertension', $('input[name=hypertension]:checked').val());
	setLable('confirmheartdisease', $('input[name=heartdisease]:checked').val());
	setLable('confirmAnaemia', $('input[name=anaemia]:checked').val());
	setLable('confirmThyroid', $('input[name=thyroidproblem]:checked').val());
	setLable('confirmAnyOtherProblem', $('input[name=anyotherproblem]:checked').val());
	return true;
}





function validateObstetric(){
	
		setLable('confirmPregnancyCount', get('pregnancyCount').value.trim());
		setLable('confirmNoOfChildrens', get('noOfChildren').value.trim());
		setLable('confirmdateOfRecentDelivery', get('dateOfRecentDelivery').value.trim());
		setLable('confirmEarlyDelDate', get('earlyDelivery').value.trim());
		setLable('confirmCaesareancount', get('caesarean').value.trim());
		setLable('confirmBreathlessness', $('input[name=breathlessness]:checked').val());
		setLable('confirmSeverepallor', $('input[name=severepallor]:checked').val());
		setLable('confirmBleedexcessively', $('input[name=bleedexcessively]:checked').val());
		return true;
}

function validateOther(){

		setLable('confirmLmp', get('lmp').value.trim());
		setLable('confirmHeight', get('height').value.trim());
		setLable('confirmBloodGroup', get('bloodgroup').value.trim());
		setLable('confirmUuid', get('uid').value.trim());
		setLable('confirmVillage', get('villageName').value.trim());
		setLable('confirmPhc', get('phc').value.trim());
		setLable('confirmAshaName', get('ashaName').value.trim());
		setLable('confirmANM', get('anmName').value.trim());
		setLable('confirmAshsaFacil', get('ashaFacilitatorName').value.trim());
		return true;
}

function validateConfirm(){
	var flag = true;
	 if(validatePreliminary()
	 | validateContact()
	 | validatePersonal()
	 | validateObstetric()
	 | validateOther()
	 | setPastHistory())
	 {
		
	 return flag;}
	 
}

function viewMedicalCaseSheet(uuid) {
	get('uuid').value = uuid;
	document.forms["viewCaseSheet"].submit();
}

