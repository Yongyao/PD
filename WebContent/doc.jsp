<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@page session="true"%>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
	<link rel="icon" href="images/NASALogo_burned.png">

    <title>Planetary Defense</title>
	<link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script>
	<script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
    <!-- bt jasny -->
	<link rel="stylesheet" href="http://cdnjs.cloudflare.com/ajax/libs/jasny-bootstrap/3.1.3/css/jasny-bootstrap.min.css">
	<script src="http://cdnjs.cloudflare.com/ajax/libs/jasny-bootstrap/3.1.3/js/jasny-bootstrap.min.js"></script>
	<!-- bt table -->
	<link rel="stylesheet" href="http://cdnjs.cloudflare.com/ajax/libs/bootstrap-table/1.10.1/bootstrap-table.min.css">
	<script src="http://cdnjs.cloudflare.com/ajax/libs/bootstrap-table/1.10.1/bootstrap-table.min.js"></script>
    <link href="css/dashboard.css" rel="stylesheet">
	<script src="js/util.js"></script>

  </head>
  <script type="text/javascript">
    var fileList = [];
	$(document).ready(function(){			
			$("#uploadingMessage").hide();
	
			createFiletable();
			updateFileTable();
				
			$("#submit").click(function() {
				var upURL = "FileUpload";
				
				if($("#uploadedFileName").text() !="")
				{
				var formData = new FormData($('#myForm')[0]);
                $("#uploadingMessage").show();				
				$.ajax({
					url : upURL, 
					type : 'POST',
					success : function completeHandler(response) {
					    $("#uploadingMessage").hide();	
						
						/*BootstrapDialog.show({
							title: 'Confirmation',
							message: response
						});*/
						if(response == "There is already a file with the same name.")
						{
							addAlert(response);
						}
						else
						{
                           updateFileTable();
						}						
					},
					data : formData,
					cache : false,
					contentType : false,
					processData : false
				});
				}
			});	
	});
	
	function addAlert(message) {
    $('#alerts').append(
        '<div class="alert alert-warning alert-fixed-top" style = "text-align: center;">' +
            '<a class="close" data-dismiss="alert" aria-label="close">' +
            '&times;</a>' + message + '</div>');
    }


	function createFiletable() {
		var layout = {
			cache : false,
			pagination : true,
			pageSize : 15,
			//pageList : [ 11, 25, 50, 100, 200 ],
			sortName : "Time",
			sortOrder : "asc",
			search : true,
		    showRefresh : true,

			columns : [ {
				'title' : 'Name',
				'field' : 'Name',
				'formatter' : FileNameFormatter,
				sortable : true
			}, {
				'title' : 'Uploaded Time',
				'field' : 'Uploaded Time',
				"sorter" : Timesorter,
				sortable : true
			}, {
				'title' : 'Size (K)',
				'field' : 'Size',
				sortable : true

			}, {
				'title' : 'Format',
				'field' : 'Type',
				sortable : true

			} ]

		};

		$('#FileTable').bootstrapTable(layout);
	}

	function Timesorter(a, b) {
		var d1 = new Date(a);
		var d2 = new Date(b);
		var t = d1 - d2;

		if (t > 0)
			return -1;
		if (t < 0)
			return 1;
		return 0;
	}
	
	function FileNameFormatter(value, row) {
	    var url = "FileUpload?fileName="+encodeURIComponent(value);
        //url = encodeURIComponent(url);		
		return '<a href=' + url + ' target="_blank">' + value + '</a>'; 
    }
	
	function updateFileTable(){
		$.ajax({
				url : "BrowseFile",
				data : {},
				success : function completeHandler(response) {
				    if(response!=null)
					{
				    fileList = response.PDResults;
					$('#FileTable').bootstrapTable('load', fileList);
					}					
				}
			});		
	}
</script>

  <body>
  
    <div id = "alert_placeholder" style="width:80%;margin: 0 auto"></div>

    <nav class="navbar navbar-inverse navbar-fixed-top">
      <div class="container-fluid">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
		  <a href="javascript:;" class="pull-left"><img src="images/NASALogo_burned.png" height="50">
          <a href="javascript:;" class="navbar-brand">Document Exchange and Archive Portal for Planetary Defense</a>
        </div>
		
		<ul class="nav navbar-nav navbar-right">
        <!--<li><a href="javascript:;">Welcome, <sec:authentication property="principal.username" /></a></li>-->
        <li class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false" style="font-size:medium" id = "welUsername">
		  Welcome, <sec:authentication property="principal.username" />
		  <span class="caret"></span></a>
          <ul class="dropdown-menu">
            <li><a href="javascript:;" id = "changepwd">Change password</a></li>
            <li role="separator" class="divider"></li>
            <li><a href="logout"><strong> Logout</strong></a></li>
          </ul>
        </li>
      </ul>
      </div>
    </nav>

    <div class="container-fluid">
      <div class="row">
        <div class="col-sm-3 col-md-2 sidebar">
          <ul class="nav nav-sidebar">
            <li class="active"><a id= "searchNav" href="doc.jsp">Document archiving</a></li>
            <li><a href="search.jsp">Content search</a></li>
			
          </ul>
		  
		  <ul class="nav nav-sidebar">
            <li><a href="http://199.26.254.186/pdwiki/index.php/Main_Page" target="_blank">Wiki</a></li>
          </ul>
          
        </div>
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
          <h1 class="page-header">Document archiving</h1>
		  
		
		<p class="lead">Please note: the table below shows the latest 500 uploaded documents by default. Go to "<a href="search.jsp">content search</a>" if you would like to locate earlier ones.</p>
        
		<div id="upload">
			<div id= "uploadingMessage" style="width:80%;margin: 0 auto">
			<div class="alert alert-success alert-fixed-top" style = "text-align: center;">
			  <a class="close" data-dismiss="alert" aria-label="close">&times;</a>
			  <strong>Please wait</strong>, your file is being uploaded.<img src="images/loading.gif" height="22"> 
			</div>
			</div>
			
			<div id="alerts" style="width:80%;margin: 0 auto"></div>

			  <sec:authorize access="hasRole('ROLE_ADMIN')"> 
			  <h3>Upload file</h3>
			  <form method="post" id="myForm" enctype="multipart/form-data">
				  <div class="fileinput fileinput-new input-group" data-provides="fileinput">
					  <div class="form-control" data-trigger="fileinput"><i class="glyphicon glyphicon-file fileinput-exists"></i> <span id= "uploadedFileName" class="fileinput-filename"></span></div>
					  <span class="input-group-addon btn btn-default btn-file"><span class="fileinput-new">Select file</span><span class="fileinput-exists">Change</span><input type="file" name="..."></span>
					  <a href="#" class="input-group-addon btn btn-default fileinput-exists" data-dismiss="fileinput">Remove</a>
				  </div>
				  <button type="button" id="submit" class="btn btn-default">Submit</button>
			 </form>
			 </sec:authorize>

			 <div style = "margin-top:2%">
			 <table id="FileTable" class="table"></table>
			 </div>
		</div>
       
        </div>
      </div>
    </div>
	
	<div class="modal fade" id="pwdModal" tabindex="-1" role="dialog">
			  <div class="modal-dialog" role="document">
				<div class="modal-content">
				  <div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
					<h4 class="modal-title">Change password</h4>
				  </div>
				  <div class="modal-body">
					<form>
					  <div class="form-group">
						<label class="control-label">Current password:</label>
						<input type="password" class="form-control" id="oldpwd">
					  </div>
					  <div class="form-group" id = "newpwdForm">
						<label class="control-label">New password:</label>
						<input type="password" class="form-control" id="newpwd">
						<span id="pwdBlock" class="help-block"></span>
					  </div>
					</form>
				  </div>
				  <div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
					<button type="button" class="btn btn-primary" id = "changePwdSubmit">Submit</button>
				  </div>
				</div>
			  </div>
	</div>

	<footer class="footer">
      <div class="row" style = "margin:0">
		  <div class="col-md-2">
		  </div>
		  <div class="col-md-10">
		  <div class="container-fluid" style = "text-align:center;padding-top:20px;padding-bottom:5px;font-size:small">
		 This system is funded by NASA <a href="https://www.nasa.gov/goddard">Goddard</a> and <a href="http://www.nsf.gov/">NSF</a> (NNG16PU001), and supported by NASA <a href="https://esto.nasa.gov/info_technologies_aist.html">AIST</a> (NNX15AM85G). Developed and hosted by 
		<a href="http://stcenter.net/stc/">NSF Spatiotemporal Innovation Center</a> on the <a href="http://cloud.gmu.edu">Hybrid Cloud Service</a>.
        		
		  </div>
		  </div>
			
		  </div>
    </footer>
    
  </body>
</html>
