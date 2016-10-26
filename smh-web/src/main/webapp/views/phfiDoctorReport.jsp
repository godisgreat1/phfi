<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page import="com.smh.constants.Constant"%>
<%@page import="com.smh.constants.JspConstants"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
   	<title>PHIF</title>
	<link rel="icon" href="../images/favicon.png" type="image/png">
    <!-- Bootstrap -->
    <link href="../css/bootstrap.min.css" rel="stylesheet">
    <link href="../css/style.css" rel="stylesheet">
    <link href="../css/jquery.datetimepicker.css" rel="stylesheet"
	type="text/css" />
     <script src="../js/jquery.min.js"></script>
</head>
<body>
	<!--Body Wrapper block Start -->	
    <div id="wrapper">
		<!--Container block Start -->
		<div class="container-fluid">
			<!--Navigation Block Start --> 
			<%@include file="navigation.jsp"%>
			<!--Navigation Block End -->    			
			<!--Article Block Start-->
			<article>
				<div class="col-xs-12 content-wrapper">					
					<!-- Breadcrumb start -->
					<div class="breadCrumb">
						<span class="breadcrumb-text">Doctor Report</span>
						 <span class="glyphicon glyphicon-play icon-font-size"></span>
						  <span class="breadcrumb-text">Search </span>					
					</div>
					<!-- Breadcrumb End -->
					<!-- Tab Buttons Start -->
						<div class="tab-header-container-first active-background">
							<a href="#">Search</a>
						</div>
					<!-- Tab Buttons End -->
					<!-- Content Block Start -->
					<div class="main-content-holder">
						<div class="row">
							<div class="col-sm-12">
								<!--Success and Failure Message Start-->
								<div class="col-xs-12">
									<div class="discriptionErrorMsg">
										<span class="green-error" id="sucessDiv">${success}</span>
										<span class="red-error" id="errorDiv">${error}</span>
									</div>
								</div>
								<form action="beacon-pagination" name="paginationForm" method="post">
									<input type="hidden" id="pageNumberId" name="pageNumber" />
									 <input type="hidden" id="totalRecordsId" name="totalRecords" />
								</form>
								<form action="beacon-mgmt-update" name="editBeacon" method="post">
									<input type="hidden" name="editId" id="editId" />
								</form>
								<form action="getBeaconReport" name="downloadReport" method="post">
									<input type="hidden" id="downloadPageNumberId" name="downLoadPageNumber" /> 
									<input type="hidden" id="downloadTypeId" name="downloadType" />
								</form>
								<!--Success and Failure Message End-->
								<!-- Page Form Start -->
								<form:form action="getDoctorFormReport" commandName="phfiDoctorFormRequest" method="GET">
									<div class="col-sm-12">
										<div class="row">
											<div class="field-element-row">
												<%-- <fieldset class="col-sm-4"> 
													<label class="control-label" for="">From Date</label>
													<div class="input-group testdate">
															<form:input cssClass="form-control testDate" path="fromDate" id="fromDate" />
															<span class="input-group-addon">
																<span class="glyphicon glyphicon-calendar"></span>
															</span>
													</div>
												</fieldset>
												<fieldset class="col-sm-4">
													<label class="control-label" for="">To Date</label>
													<div class="input-group testdate">
															<form:input cssClass="form-control testDate" path="toDate" id="toDate" />
															<span class="input-group-addon">
																<span class="glyphicon glyphicon-calendar"></span>
															</span>	
													</div>
												</fieldset> --%>											
												<fieldset class="col-sm-4"> 
													<label class="control-label" for="">WID</label>
													<form:input path="wid" title="WID" id="wid" cssClass="form-control"/> 
												</fieldset>
												<fieldset class="col-sm-4"> 
													<label class="control-label" for="">Asha Name</label>
													<form:input path="nameOfAsha" title="Asha Name" id="nameOfAsha" cssClass="form-control"/> 
												</fieldset>												
												<fieldset class="col-sm-4"> 
													<label class="control-label" for="">Village</label>
													<form:input path="village" title="Village" id="village" cssClass="form-control"/>
													 
												</fieldset>											
												<fieldset class="col-sm-4"> 
													<label class="control-label" for="">ANM Name</label>
													<form:input path="anmName" title="ANM Name" id="anmName" cssClass="form-control" />
													 
												</fieldset> 
											</div>
										</div>
										<!--Panel Action Button Start -->
										<div class="col-sm-12 form-action-buttons">
											<div class="col-sm-5"></div>
											<div class="col-sm-7">
												<input type="submit" class="form-control button pull-right" value="Search" >
												<a href="#" class="form-control button pull-right">Reset</a>
											</div>	
										</div>
										<!--Panel Action Button End -->
									</div>
								</form:form>
 								<!-- Page Form End -->	
							</div>
						</div>
					</div>
					<c:if test="${resultflag eq 'true'}">
					<div class="search-results-table">
						<table class="table table-striped table-bordered table-condensed marginBM1">
							<!-- Search Table Header Start -->
							<tr>
								<td class="search-table-header-column widthP80">
									<span class="glyphicon glyphicon-search search-table-icon-text"></span>									
									<span>Search</span>
								</td>
								<td class="search-table-header-column" style ="font-weight:bold;">Total Count : ${totalCount}</td>
							</tr>
							</table>
							<!-- Search Table Header End -->
							<!-- Search Table Content Start -->
							<table id="serviceResults" class="table table-striped table-bordered table-responsive table-condensed tablesorter marginBM1 common-table">
								<thead>
							 <tr>
								<th>SL NO.</th>
								<th>Women<br/> Name</th>
								<th width="5%">Age</th>
								<th width="5%">WID</th>
								<th>Days to<br/> Deliver</th>
								<th>Obstetric<br/> score</th>
								<th>Name of<br/> ASHA</th>
								<th>Doctor's <br/>assessment</th>
								<th>Doctor's <br/>assessment <br/>status</th>
								<th>Investigations</th>
								<th>Medication</th>
								<th>Iinstructions<br/> to ANM</th>
								<th>Woman next<br/> visit to<br/> Doctor </th>
							</tr>
							</thead>
								<c:choose>
									<c:when test="${!(fn:length(doctorReportList) eq 0)}">
										<c:forEach items="${doctorReportList}" var="doctorData">
											<tr>	
												<td data-title="SL NO."><div>${doctorData.slNo}</div></td>
												<td data-title="Woman Name"><div class="tableAllign womanName">${doctorData.womanName}</div></td>
												<td data-title="WID">${doctorData.age}</td>							
												<td data-title="WID">${doctorData.wid}</td>
												<td data-title="History">${doctorData.daysToDeliver}</td>
												<td data-title="Lab Tests">${doctorData.obstic}</td>
												<td data-title="Name of Asha"><div class="womanName">${doctorData.nameOfAsha}</div></td>
												<td data-title="Doctor's Diagnosis"><div class="tableAllign">${doctorData.diagonosis}</div></td>
												<td data-title="Doctor's Assessment Status"><div class="tableAllign">${doctorData.assesmentstatus}</div></td>
												<td data-title="Investigations"><div class="tableAllign">${doctorData.investigations}</div></td>
												<td data-title="Medication"><div class="tableAllign">${doctorData.medication}</div></td>
												<td data-title="Health Education"><div class="healthEducation">${doctorData.health}</div></td>
												<td data-title="Advice">${doctorData.advice}</td>
											</tr>
										</c:forEach>
									</c:when>
									<c:otherwise>
									<tr>
										<td colspan="12" style="color: red;">No Records Found</td>
									</tr>
									</c:otherwise>
								</c:choose>
							</table>
							<!-- Search Table Content End -->	
							<table class="table table-striped table-bordered table-condensed">
							<c:if test="${!(fn:length(doctorrReportList) eq 0) }">
							<tr class="table-footer-main">
								<td colspan="8" class="search-table-header-column">
									<div  class="col-sm-12">
										 <div class="col-sm-4">
											<div class="btn-toolbar" role="toolbar">
												<div class="btn-group custom-table-footer-button">
													<a id="export-to-excel" onclick="$('#serviceResults').tableExport({type:'excel',escape:'false',worksheetName:'Doctor Report'});">
													              <button type="button" class="btn btn-default"><img src="../images/excel.png"></button></a>
													<!-- <a id="export-to-pdf">
													             <button type="button" class="btn btn-default"><img src="../images/pdf.png" ></button></a> -->
												</div>
											</div>
										</div> 
											<div class="col-sm-8">
												<c:if test="${ !(fn:length(doctorReportList) eq 0)}">
													<ul class="pagination custom-table-footer-pagination">
														<c:if test="${portalListPageNumber gt 1}">
															<li><a href="javascript:getPortalOnPageWithRecords('1','${totalRecords}')">&laquo;</a></li>
															<li><a href="javascript:getPortalPrevPageWithRecords('${portalListPageNumber }', '${totalRecords}')">&lsaquo; </a></li>
														</c:if>

														<c:forEach var="page" begin="${beginPortalPage }"
															end="${endPortalPage}" step="1" varStatus="pagePoint">
															<c:if test="${portalListPageNumber == pagePoint.index}">
																<li class="${portalListPageNumber == pagePoint.index?'active':''}">
																	<a href="javascript:">${pagePoint.index}</a>
																</li>
															</c:if>
															<c:if test="${portalListPageNumber ne pagePoint.index}">
																<li class=""><a href="javascript:getPortalOnPageWithRecords('${pagePoint.index }','${totalRecords}')">${pagePoint.index}</a>
																</li>
															</c:if>
														</c:forEach>


														<c:if test="${portalListPageNumber lt portalPages}">
															<li><a href="javascript:getPortalNextPageWithRecords('${portalListPageNumber }', '${totalRecords}')">&rsaquo;</a></li>
															<li><a href="javascript:getPortalOnPageWithRecords('${portalPages }', '${totalRecords}')">&raquo;</a></li>
														</c:if>

													</ul>

												</c:if>
											</div>
										</div>								
								</td>
							</tr>
							</c:if>
							<!-- Search Table Content End -->	
						</table>
						</div>
						</c:if>
					<!-- Content Block End -->
				</div>
			</article>
			<!--Article Block End-->
			<footer class="footer">
			<jsp:include page="footer.jsp"></jsp:include>
			</footer>
		</div>
		<!--Container block End -->
	</div>
	
	<!--Body Wrapper block End -->	

    <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
    <script src="../js/jquery.min.js"></script>
    <!-- Include all compiled plugins (below), or include individual files as needed -->
    <script src="../js/bootstrap.min.js"></script>	
	<script src="../js/jquery.datetimepicker.js"></script>
	<!--common script for all pages-->
	<script src="../js/common-lib.js"></script>
	<script src="../js/beacon.js"></script>
	<script src="../js/jquery.popupoverlay.js"></script>
	<script src="../js/common-lib.js"></script>
	<script src="../js/jquery.maskedinput.js"></script>
	<script src="../js_new/jspdf.debug.js"></script>
	<script src="../js_new/html2canvas.js"></script>
	<script src="../js_new/tableExport.js"></script>
	<script src="../js_new/jquery.base64.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$('body').on("click", "#export-to-pdf", function () { 
				
				var doc = new jsPDF('l', 'pt', 'letter');
				
				doc.addHTML($('#serviceResults')[0], function () {
					doc.save('Case-Sheet-Report.pdf');				
				});
			});
		});
	</script>
	<script>	
	$(".testdate").click(function() {
		$(this).children('.testDate').focus();
	});
	$('.testDate').datetimepicker({
		timepicker : false,
	    showOtherMonths: true,
	    selectOtherMonths: true,
	    changeMonth: true,
	    changeYear: true,
		format : 'd/m/Y',
		formatDate : 'Y/m/d',
	});
	function closePopupActivate() {
		get('activateReason').value =null;
		$("#activateDiv").removeClass("has-error").removeClass("has-success");
		$("#activateErr").removeClass("visible");
		setDiv("activateErr", "");
		$('#beaconActivatePopDiv').popup("hide");
	}
	</script>
	<script>
	$('#beaconActivatePopDiv').popup({
		blur : false
	});
	</script>
	<script>	
		/* Select li full area function Start *
		$("li").click(function(){
			window.location=$(this).find("a").attr("href"); 
			return false;
		});
		/* Select li full area function End */		
		/* Common Navigation Include Start */		
		$(function(){
			$( "#main-navigation" ).load( "main-navigation.html" );
		});
		function highlightMainContent(){
			$( "#navListId3" ).addClass( "active-background" );
		}
		function closePopup() {
			setDiv("activateErr", "&nbsp;");
			$('#beaconActivatePopDiv').popup("hide");
		}
		/* Common Navigation Include End */
		$(document).ready(function(){
			$('.womanName').each(function() {  $(this).text(decodeURIComponent($(this).text()));  });
			$('.healthEducation').each(function() {  $(this).text(decodeURIComponent($(this).text()));  });
		});
		
		$("#beaconName").focus();
	</script>
  </body>  
</html>