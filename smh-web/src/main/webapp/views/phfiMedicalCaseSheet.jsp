<!DOCTYPE html>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>PHFI</title>
<!-- Bootstrap -->
<link href="../css_new/bootstrap.min.css" rel="stylesheet">
<link href="../css/style.css" rel="stylesheet">
<link href="../css_new/jquery.datetimepicker.css" rel="stylesheet"
	type="text/css" />
	
<style type="text/css">
td {
    border: solid 2px lightgrey;
}
</style>

<script type="text/javascript">
	function stopRKey(evt) {
		var evt = (evt) ? evt : ((event) ? event : null);
		var node = (evt.target) ? evt.target
				: ((evt.srcElement) ? evt.srcElement : null);
		if ((evt.keyCode == 13) && (node.type == "text")) {
			return false;
		}
	}

	document.onkeypress = stopRKey;
</script>
<script src="../js_new/jquery.min.js"></script>
</head>
<body>
	<!--Body Wrapper block Start -->
	<div id="wrapper">
		<!--Container block Start -->
		<div class="container-fluid">
			<!--Header Block Start -->
			<!-- <header class="col-sm-12 all-page-header">
			<!--Header Block End -->
			<!--Navigation Block Start -->
			<nav class="col-sm-12 nav-bar" id="main-navigation">
				<jsp:include page="navigation.jsp"></jsp:include>
			</nav>
			<!--Navigation Block Start -->
			<!--Article Block Start-->
			<article>
				<div class="col-xs-12 content-wrapper" id="exp-id">
					<!-- Breadcrumb start -->
					<br/>
					<br/>
					<div class="tab-header-container active-background">
						<a class="pull-right" href="show-phfi-registration-search">Back</a>
					</div>
					<div class="tab-header-container active-background">
						<a class="pull-right" style="cursor: pointer;" id="export-to-pdf">Export To PDF</a>
						<%-- <input type="submit" id="buttonCreate" class="button" value="Download" style="height: 25px" onclick="document.location.href='getCaseSheetDownload?wid='+${medicalCaseSheet.wid};">
						 --%>
					</div>
					<hr/>
					<!-- Breadcrumb End -->
					<!-- Tab Buttons Start -->
					<br/>
					<!-- Tab Buttons End -->
					<!-- Content Block Start -->
					<div class="main-content-holder padding0">
						<div>
						<fieldset class="col-sm-12" style="background-color: white;">
							<fieldset class="fieldset registration-content">
								<legend class="legend">Medical Case Sheet for the Doctor</legend>
								<table class="confirm-info-table" style="border: 2px solid grey; border-collapse: collapse; background-color: white;" id="report-tbl">
									<tr style="height: 45px;">
										<td colspan="4" style="text-align: center;">Report generated on <b> <% SimpleDateFormat sm = new SimpleDateFormat("dd-MM-yyyy"); Date myDate = new Date(); String todayDate = sm.format(myDate); out.print(todayDate);%></b> based on the details collected during the home visit by the ASHA on <b>${medicalCaseSheet.visitDate} </b></td>
									</tr>
									
									<tr style="height: 45px;">
										<td colspan="2" style=" text-align: center;"><b>${medicalCaseSheet.serverity}</b></td>
										<td colspan="2" style=" text-align: center;">${medicalCaseSheet.trimester}&nbsp; :&nbsp; ${medicalCaseSheet.obstetricScore}</td>
									</tr>
									<tr style="height: 45px;">
										<td colspan="4">&nbsp;&nbsp;System Generated Initial Assessment:&nbsp;${medicalCaseSheet.intialAsses}</td>
									</tr>
									<tr style="height: 45px;">
										<td colspan="2">&nbsp;&nbsp;LMP :&nbsp;${medicalCaseSheet.lmp}</td>
										<td colspan="2">&nbsp;&nbsp;EDD :&nbsp;${medicalCaseSheet.edd}</td>
									</tr>
									<tr style="height: 45px;">
										<td colspan="1">&nbsp;&nbsp;Name :&nbsp;<div class="womanName" style="display: inline;">${medicalCaseSheet.name}</div></td>
										<td colspan="1">&nbsp;&nbsp;Age  :&nbsp;${medicalCaseSheet.age}</td>
										<%-- <td colspan="1">&nbsp;&nbsp;Years:${medicalCaseSheet.years}</td> --%>
										<td colspan="1">&nbsp;&nbsp;WID  :&nbsp;${medicalCaseSheet.wid}</td>
									</tr>
									<tr style="height: 45px;">
										<td colspan="1">&nbsp;&nbsp;Village :&nbsp;<div class="villageName" style="display: inline;">${medicalCaseSheet.villageName}</div></td>
										<td colspan="1">&nbsp;&nbsp;Taluk :&nbsp;<div class="taluk" style="display: inline;">${medicalCaseSheet.takul}</div></td>
										<td colspan="2">&nbsp;&nbsp;District :&nbsp;<div class="district" style="display: inline;">${medicalCaseSheet.distric}</div></td>
									</tr>
									<tr style="height: 45px;">
										<td colspan="4">&nbsp;&nbsp;Current Address :&nbsp;<div class="currentAddress" style="display: inline;">${medicalCaseSheet.currentAddress}</div></td>
									</tr>
									<tr style="height: 45px;">
										<td colspan="4">&nbsp;&nbsp;Alternative Address :&nbsp;<div class="alternateAddress" style="display: inline;">${medicalCaseSheet.alternativeAddress}</div></td>
									</tr>
									
									<tr style="height: 45px;">
										<td colspan="4">&nbsp;&nbsp;Predisposing Factors &nbsp;: &nbsp;${medicalCaseSheet.predisposingFactor}</td>
									</tr>
									<tr style="height: 45px;">
										<td colspan="4">&nbsp;&nbsp;Past History &nbsp;: &nbsp;${medicalCaseSheet.pastHistory}</td>
									</tr>
									<tr style="height: 45px;">
										<td colspan="4">&nbsp;&nbsp;Details of Delivery &nbsp;: &nbsp;${medicalCaseSheet.deliveryDetails}</td>
									</tr>
									<tr style="height: 45px;">
										<td colspan="4">&nbsp;&nbsp;Current History:&nbsp;${medicalCaseSheet.currentHistory}</td>
									</tr>
									<tr style="height: 45px;">
										<td colspan="4">&nbsp;&nbsp;General Examination:&nbsp;${medicalCaseSheet.genralExamination}</td>
									</tr>
									<tr style="height: 45px;">
										<td colspan="4"> 
										<table>
											<tr><td rowspan="2" style="min-width: 135px">&nbsp;&nbsp;Vitals:</td><td style="min-width: 150px">Weight</td><td style="min-width: 600px">${medicalCaseSheet.weight}</td></tr>
											<tr><td style="min-width: 150px">BP</td><td style="min-width: 600px">${medicalCaseSheet.bp}</td></tr>
										</table></td>
									</tr>
									
									<c:choose>
									<c:when test="${!(fn:length(medicalCaseSheet.labTest) eq 0)}">
									<tr style="height: 45px;">
										<td colspan="1">&nbsp;&nbsp;Lab Tests:</td>
										<td colspan="3">
										<c:forEach items="${fn:split(medicalCaseSheet.labTest,',')}" var="labTest">
										${fn:replace(fn:replace(labTest,'[[',''),']]','')}
									
									</c:forEach>
									</td>
									</tr>
									</c:when>
									<c:otherwise>
									<tr>
										<td colspan="8" style="color: red;">Test Not Found</td>
									</tr>
									</c:otherwise>
									</c:choose>
									
								</table>
							</fieldset>
						</fieldset>	
						</div>
					</div>
						</div>
						</article>
					</div>
					<!-- Content Block End -->
				</div>
			<!--Article Block End-->
			<jsp:include page="footer.jsp"></jsp:include>
		<!--Container block End -->


	<!-- Include all compiled plugins (below), or include individual files as needed -->
	<!-- <script src="../js_new/bootstrap.min.js"></script> -->
	<script src="../js/bootstrap.min.js"></script>
	<script src="../js_new/jquery.datetimepicker.js"></script>
	<script src="../js_new/common-lib.js"></script>
	<script src="../js_new/chatak-ajax.js"></script>
	<script src="../js_new/messages.js"></script>
	
	<script src="../js_new/jspdf.debug.js"></script>
	<script src="../js_new/html2canvas.js"></script>

	<!--Body Wrapper block End -->
	<script type="text/javascript" src="../js_new/backbutton.js"></script>

	<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	
	<script type="text/javascript">
	
	$('body').on("click", "#export-to-pdf", function () { 
		
		var doc = new jsPDF('l', 'pt', 'letter');
		
		doc.addHTML($('#report-tbl')[0], function () {
			doc.save('Case-Sheet-Report.pdf');				
		});
	});
				
		$(document).ready(function(){
			$('.womanName').each(function() {  $(this).text(decodeURIComponent($(this).text()));  });
			$('.villageName').each(function() {  $(this).text(decodeURIComponent($(this).text()));  });
			$('.taluk').each(function() {  $(this).text(decodeURIComponent($(this).text()));  });
			$('.currentAddress').each(function() {  $(this).text(decodeURIComponent($(this).text()));  });
			$('.alternateAddress').each(function() {  $(this).text(decodeURIComponent($(this).text()));  });
			$('.district').each(function() {  $(this).text(decodeURIComponent($(this).text()));  });
			/* $('.slNo').each(function() {  $(this).text(decodeURIComponent($(this).text()));  });
			$('.slNo').each(function() {  $(this).text(decodeURIComponent($(this).text()));  });
			 */
		});
				
	</script>
</body>
</html>