<!DOCTYPE html>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page contentType="text/html;charset=UTF-8" %>

<html lang="en">
<head>
	<meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>PHFI</title>
    <link rel="icon" href="../images/favicon.png" type="image/png">
    <!-- Bootstrap -->
    <link href="../css/bootstrap.min.css" rel="stylesheet">
    <link href="../css/style.css" rel="stylesheet">
    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>
<body>
	<!--Body Wrapper block Start -->	
    <div id="wrapper">
		<!--Container block Start -->
		<div class="container-fluid">
			<!--Navigation Block Start --> 
			<jsp:include page="navigation.jsp"></jsp:include>
			<!--Navigation Block Start -->   			
			<!--Article Block Start-->
			<article>
				<div class="col-xs-12 content-wrapper">
					<!-- <h3>Dash Board (Static)</h3> 
					Content Block Start
						<img src="../images/dashboard.png" width="100%" height="400px">
					Content Block End	 -->
					<table id="serviceResults" class="table table-striped table-condensed ">
						<tr>
							<td class="buttonMain" bordercolor="#f8f8f8" height="30px" valign="middle"><p style="margin-top: 100px;margin-left: 50px; margin-bottom: 50px"  >
							<input type="submit" id="buttonCreate" class="button" value=" List of women" style="height: 25px" onclick="document.location.href='show-phfi-registration-search';">
							<!-- <input type="submit" id="buttonCreate" class="button"  value="ASHA feedback report" style="height: 25px;" onclick="return submitData()"> -->
							<!-- <input type="submit" id="buttonCreate" class="button" value="Medical Case Sheet: Basic" style="height: 25px" onclick="document.location.href='show-medical-case-sheet';"> -->
							<input type="submit" id="buttonCreate" class="button"  value="Master Report" style="height: 25px;" onclick="document.location.href='get-master-reports';">
							<input type="submit" id="buttonCreate" class="button" value="Master Raw Data" style="height: 25px" onclick="document.location.href='phfi-raw-data-dashboard';">
							<input type="submit" id="buttonCreate" class="button"  value="Doctor Pregnant Report" style="height: 25px;" onclick="document.location.href='get-doctor-ratified-reports?maternityStatus=Pregnant';">
							<input type="submit" id="buttonCreate" class="button"  value="Doctor Postpartum Report" style="height: 25px;" onclick="document.location.href='get-doctor-ratified-reports?maternityStatus=Postpartum';">
							<input type="submit" id="buttonCreate" class="button"  value="Asha Pregnant Report" style="height: 25px;" onclick="document.location.href='get-asha-feedback-reports?maternityStatus=Pregnant';">
							<input type="submit" id="buttonCreate" class="button"  value="Asha Postpartum Report" style="height: 25px;" onclick="document.location.href='get-asha-feedback-reports?maternityStatus=Postpartum';">
							<input type="submit" id="buttonCreate" class="button"  value="Doctor Retify Report" style="height: 25px" onclick="document.location.href='getDoctorFormReport';">
							</p></td>
						</tr>
						
					</table>
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
	<script>	
	$(document).ready(function() {
		$( "#navListId1" ).addClass( "active-background" );
		});
		/* Select li full area function Start */
		$("li").click(function(){
			window.location=$(this).find("a").attr("href"); 
			return false;
		});
		/* Select li full area function End */
		/* Common Navigation Include Start */		
		
		/* Common Navigation Include End */
	</script>
	
  </body>  
</html>

