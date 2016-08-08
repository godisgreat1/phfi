function bpValidation(data, div_id) {
	var bpvalue=getVal(data);
	if (bpvalue != "") {
		var parts = bpvalue.split("/");
		var diastolic = parts[parts.length - 1];
		var systolic = parts[parts.length - 2];
		if ((typeof diastolic == "undefined")
				|| (typeof systolic == "undefined") || (diastolic == "")
				|| (systolic == "")) {

			setDiv(div_id,"Enter valid format:Eg:120/80");
			return false;
		} else {
			setDiv(div_id,"");

		}
		if (diastolic != "" && systolic != "") {
			if ((diastolic < 60 || diastolic > 300 || systolic < 40 || systolic > 140)){
				if (diastolic < 60 && systolic < 40) {
					setDiv(div_id,"The systolic/diastolic BP value you have entered seems very low");
				
				} else if (diastolic > 300 && systolic > 140) {
					setDiv(div_id,"The systolic/diastolic BP value you have entered seems very high");
				} else if (diastolic < 60) {
					setDiv(div_id,"The diastolic BP value you have entered seems very low");
				} else if (systolic < 40) {
					setDiv(div_id,"The systolic BP value you have entered seems very low");
				} else if (diastolic > 300) {
					setDiv(div_id,"The diastolic BP value you have entered seems very high");
				} else if (systolic > 140) {
					setDiv(div_id,"The systolic BP value you have entered seems very high");
				}
			} else {
				setDiv(div_id,"");
			}
		}
	}
}

function weightValidation(data, div_id){
	var weightvalue=getVal(data);
	var regx = /^[0-9]+$/;
	if(isEmpty(weightvalue)){
		setDiv(div_id, "This field is mandatory");
		return false;
	}
	if(!regx.test(weightvalue)){
		setDiv(div_id, "Enter numeric value only ");
		return false;
	}else if (!(weightvalue >= 30 && weightvalue <= 90) ){
		setDiv(div_id, "Enter between 30 and 90");
		return false;
	}
	else{
		setDiv(div_id, "");
		return true;
	}
}

function rbsValidation(data, div_id){
	var rbsvalue=getVal(data);
	var regx = /^[0-9]+$/;
	if(isEmpty(rbsvalue)){
		setDiv(div_id, "This field is mandatory");
		return false;
	}
	if(!regx.test(rbsvalue)){
		setDiv(div_id, "Enter numeric value only ");
		return false;
	}else if (!(rbsvalue >= 40 && rbsvalue <= 400) ){
		setDiv(div_id, "Enter between 40 and 400");
		return false;
	}
	else{
		setDiv(div_id, "");
		return true;
	}
}

function hbValidation(data, div_id){
	var hbvalue=getVal(data);
	var regx = /^[0-9\.]+$/;
	if(isEmpty(hbvalue)){
		setDiv(div_id, "This field is mandatory");
		return false;
	}
	if(!regx.test(hbvalue)){
		setDiv(div_id, "Enter numeric value only ");
		return false;
	}else if (!(hbvalue >= 2 && hbvalue <= 16) ){
		setDiv(div_id, "Enter between 2 and 16");
		return false;
	}
	else{
		setDiv(div_id, "");
		return true;
	}
}


function setAshaInfo(){
	setLable('confirmVigitDate', get('visitDate').value.trim());
	setLable('confirmWid', get('wid').value.trim());
	setLable('confirmWomanName', get('womanName').value.trim());
	return true;
}

function setAskTheWoman(){
	setLable('confirmHaveFeer', $('input[name=haveFever]:checked').val());
	setLable('confirmHadFit',$('input[name=isFits]:checked').val());
	setLable('confirmLostConsciousness',$('input[name=isConsciousness]:checked').val());
	/*setLable('confirmFeltGiddy', get('feltGiddy').value.trim());*/
	setLable('confirmHaveHeadaches',$('input[name=haveHeadaches]:checked').val());
	setLable('confirmBlurreVission',$('input[name=haveBlurredVision]:checked').val());
	setLable('confirmBreathless', $('input[name=isBreathless]:checked').val());
	setLable('confirmHaveCough', $('input[name=haveCough]:checked').val());
	setLable('confirmAbdominalPain',$('input[name=isAbdominalPain]:checked').val());
	/*setLable('confirmBlurreVission', get('babyMove').value.trim());*/
	setLable('confirmVaginalDischarge',$('input[name=isVaginalDischarge]:checked').val());
	setLable('confirmAnyBleeding', $('input[name=isBleeding]:checked').val());
	/*setLable('confirmHaveWaterBroken', get('isWaterBroken').value.trim());*/
	setLable('confirmBurningPain',$('input[name=isBurningPain]:checked').val());
	/*setLable('confirmRingTighter', get('toeRingsTighter').value.trim());*/
	/*setLable('confirmWearBangles', get('isBangles').value.trim());*/
	return true;
}

function setObserve(){
	setLable('confirmOutOFBreath', $('input[name=outOfBreath]:checked').val());
	setLable('confirmTakingIrrelevantly',$('input[name=isTalking]:checked').val());
	setLable('confirmUpperEye', $('input[name=upperEyeColor]:checked').val());
	setLable('confirmLowerEye', $('input[name=lowerEyeColor]:checked').val());
	setLable('confirmHisFeet',$('input[name=isAnkleDepression]:checked').val());
	setLable('confirmFace',$('input[name=isEyeSwelling]:checked').val());
	return true;
}

function setTestResult(){
	/*setLable('confirmWeightStatus', get('weight').value.trim());
	setLable('confirmFirstWeight', get('firstWeight').value.trim());
	setLable('confirmFirstWeightDate', get('weightDateOne').value.trim());
	setLable('confirmSecWeight', get('SecWeight').value.trim());
	setLable('confirmSecWeightDate', get('weightDateSec').value.trim());
	setLable('confirmThirdWeight', get('thirdWeight').value.trim());
	setLable('confirmThirdWeightDate', get('weightDateThird').value.trim());
	setLable('confirmFourthWeight', get('fourthWeight').value.trim());
	setLable('confirmFourthWeightDate', get('weightDateFour').value.trim());*/
	
	setLable('confirmBpStatus',$('input[name=bp]:checked').val());
	setLable('confirmFirstBp', get('firstBp').value.trim());
	setLable('confirmFirstBpDate', get('bpDateOne').value.trim());
	setLable('confirmSecBp', get('secBp').value.trim());
	setLable('confirmSecBpDate', get('bpDateSec').value.trim());
	/*setLable('confirmThirdBp', get('thirdBp').value.trim());
	setLable('confirmThirdBpDate', get('bpDateThird').value.trim());
	setLable('confirmFourthBp', get('fourBp').value.trim());
	setLable('confirmFourthBpDate', get('bpDateFour').value.trim());*/
	
	setLable('confirmHbStatus',$('input[name=hb]:checked').val());
	setLable('confirmFirstHb', get('firstHb').value.trim());
	setLable('confirmFirstHbDate', get('hbDateOne').value.trim());
	setLable('confirmSecHb', get('secHb').value.trim());
	setLable('confirmSecHbDate', get('hbDateSec').value.trim());
	/*setLable('confirmThirdHb', get('thirdHb').value.trim());
	setLable('confirmThirdHbDate', get('hbDateThird').value.trim());
	setLable('confirmFourthHb', get('fourHb').value.trim());
	setLable('confirmFourthHbDate', get('hbDateFour').value.trim());*/
	
	setLable('confirmUaStatus', $('input[name=urine]:checked').val());
	setLable('confirmFirstUa', get('firstUrine').value.trim());
	setLable('confirmFirstUaDate', get('urineDateOne').value.trim());
	setLable('confirmSecUa', get('secUrine').value.trim());
	setLable('confirmSecUaDate', get('urineDateSec').value.trim());
	
	/*
	setLable('confirmUsStatus', get('ultrasound').value.trim());
	setLable('confirmFirstUs', get('firstUltrasound').value.trim());
	setLable('confirmFirstUsDate', get('ultrasoundDateOne').value.trim());
	setLable('confirmSecUs', get('secUltrasound').value.trim());
	setLable('confirmSecUsDate', get('ultrasoundDateSec').value.trim());*/
	/*
	setLable('confirmRbsStatus', get('regDate').value.trim());
	setLable('confirmFirstRbs', get('villageId').value.trim());
	setLable('confirmFirstRbsDate', get('uid').value.trim());
	setLable('confirmSecRbs', get('regDate').value.trim());
	setLable('confirmSecRbsDate', get('villageId').value.trim());
	*/
	/*setLable('confirmRbsStatus', get('rbs').value.trim());
	setLable('confirmFirstRbs', get('firstRbs').value.trim());
	setLable('confirmFirstRbsDate', get('rbsDateOne').value.trim());
	setLable('confirmSecRbs', get('secRbs').value.trim());
	setLable('confirmSecRbsDate', get('rbsDateSec').value.trim());*/
	
	setLable('confirmMalariaStatus',$('input[name=malaria]:checked').val());
	setLable('confirmFirstMalaria', get('firstMalaria').value.trim());
	setLable('confirmFirstMalariaDate', get('malariaDateOne').value.trim());
	setLable('confirmSecMalaria', get('secMalaria').value.trim());
	setLable('confirmSecMalariaDate', get('malariaDateSec').value.trim());
	
	setLable('confirmSputumStatus',$('input[name=sputum]:checked').val());
	setLable('confirmFirstSputum', get('sputumTest').value.trim());
	setLable('confirmFirstSputumDate', get('sputumDate').value.trim());
	return true;
}
function setAskTheFamily(){
	setLable('confirmTalkingAndDisconnected',$('input[name=talkingIrrelevantly]:checked').val());
	setLable('confirmCaringBaby', $('input[name=carringBabyAndHerself]:checked').val());
	setLable('confirmImaginaryVoiceAndPeople',$('input[name=isHearingImaginary]:checked').val());
	return true;
}

function fetchWomenNameByID(){
	var wid = get("wid").value;
	
	if(wid == ""){
		return;
	}
	doAjaxFetchWomenNameByID(wid);
}
function doAjaxFetchWomenNameByID(wid){
	$.ajax({
		type : "GET",
		url : "doAjaxFetchWomenNameByID?wid=" + wid,
		async : true,
		success : function(response) {
			var obj = JSON.parse(response);
			document.getElementById("womanName").value = obj;
		},
		error : function(e) {
			
		}
		
	});
}



