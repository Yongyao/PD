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
	
	<script src="http://rawgit.com/vitalets/x-editable/master/dist/bootstrap3-editable/js/bootstrap-editable.js"></script>
	<script src="lib/bootstrap-table-editable.js"></script>
	<script src="https://rawgit.com/mindmup/editable-table/master/mindmup-editabletable.js"></script>

	<!-- bt table -->
	
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap3-dialog/1.34.7/css/bootstrap-dialog.min.css">
	<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap3-dialog/1.34.7/js/bootstrap-dialog.min.js"></script>	

    <!-- Custom styles for this template -->
    <link href="dashboard.css" rel="stylesheet">
	<style>
	html {
	  position: relative;
	  min-height: 100%;
	}
	body {
	  /* Margin bottom by footer height */
	  margin-bottom: 50px;
	}
	.footer {
	  position: absolute;
	  bottom: 0;
	  width: 100%;
	  /* Set the fixed height of the footer here */
	  height: 50px;

	}
	</style>

  </head>
  <script type="text/javascript">
    var fileList = [];

	$(document).ready(function(){
			createVocabtable();
			//updateVocabTable();
			$("#addconceptSubmit").click(function() {				
				addConcept();
		   });
		    $("#deleteConceptButton").click(function() {				
				deleteConceptPre();
		   });
		   $("#deleteconceptSubmit").click(function() {				
				deleteConcept();
		   });
		   
		   
	});
	
	function deleteConceptPre(){
	      var selectedList = $('#vocabTable').bootstrapTable('getSelections');
		  if(selectedList.length!=0)
		  {
		    $('#deleteConceptModal').modal('show');
		  }
	}
	
	function deleteConcept(){
	      var selectedList = $('#vocabTable').bootstrapTable('getSelections');
		  $('#deleteConceptModal').modal('hide');
		  for(var i=0; i<selectedList.length; i++)
		  {
		     $.ajax({
					url : "DeleteConcept",
					data : {
							"concept" : selectedList[i].Concept
						   },
				    success : function completeHandler(response) {
							$('#vocabTable').bootstrapTable('refresh');					
					}
					
			});	
		  }
		  		
		  	
	}
	
	function addConcept(){
		if($("#concept-name").val()!="")
				{				
				$('#addConceptModal').modal('hide');
				$.ajax({
					url : "AddConcept",
					data : {
								"concept" : $("#concept-name").val(),
								"def": $("#defini-text").val()
						   },
					success : function completeHandler(response) {
							BootstrapDialog.show({
							title: 'Confirmation',
							message: response
						});	
						$('#vocabTable').bootstrapTable('refresh');					
					}
				});		
		
			   }
	}
	
	function detailFormatter(index, row) {
        /*var html = [];
        $.each(row, function (key, value) {
            html.push('<p><b>' + key + ':</b> ' + value + '</p>');
        });
        return html.join('');*/
		return "old definitions will be put here (under developing)";
    }


	function createVocabtable() {
		var layout = {
			cache : false,
			pagination : true,
			pageSize : 40,
			search : true,
		    showRefresh : true,
			detailView: true,
			detailFormatter: detailFormatter,
			icons: { paginationSwitchDown: 'glyphicon-collapse-down icon-chevron-down',
					 paginationSwitchUp: 'glyphicon-collapse-up icon-chevron-up',
					 refresh: 'glyphicon-refresh icon-refresh',
					 toggle: 'glyphicon-list-alt icon-list-alt',
					 columns: 'glyphicon-th icon-th',
					 detailOpen: 'glyphicon glyphicon-comment',
					 detailClose: 'glyphicon glyphicon-comment'
					},

			columns : [ 
			{
				field : 'state',
				checkbox : true
			},{
				'title' : 'Concept',
				'field' : 'Concept',
				sortable : true,
				//editable: true
			}, {
				'title' : 'Definition',
				'field' : 'Definition',
				//editable: true
			}],
			
			onPostBody: function () {
          	$('#vocabTable').editableTableWidget({editor: $('<textarea>')});
            }
			

			/*data: [{
				Concept: 'Item 1',
				Definition: "dfa",
				Comment: '$1'
			}, {
				Concept: 'Item 1',
				Definition: "dfa",
				Comment: '$1'
			}]*/

		};

		$('#vocabTable').bootstrapTable(layout);
	}

</script>

  <body>

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
		
        <span class="navbar-brand pull-right">Welcome, <sec:authentication property="principal.username" /><a href="logout"><strong> Logout</strong></a></span>
      </div>
    </nav>

    <div class="container-fluid">
      <div class="row">
        <div class="col-sm-3 col-md-2 sidebar">
          <ul class="nav nav-sidebar">
            <li><a id= "searchNav" href="doc.jsp">Document archiving</a></li>
            <li><a href="search.jsp">Content search</a></li>
			<li class="active"><a href="vocablist.jsp">Vocabulary list</a></li>
			
          </ul>
		  
		  <ul class="nav nav-sidebar">
            <li><a href="http://199.26.254.186/pdwiki/index.php/Main_Page" target="_blank">About (Wiki)</a></li>
          </ul>
          
        </div>
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
          <h1 class="page-header">Vocabulary list</h1>
        
		<div id="vocab">
			 <div style = "margin-top:2%">
			 <div id="toolbar" class="btn-group">
				<button type="button" class="btn btn-default" data-toggle="modal" data-target="#addConceptModal">
					<i class="glyphicon glyphicon-plus"></i>
				</button>
				
				<button type="button" class="btn btn-default" id = "deleteConceptButton">
					<i class="glyphicon glyphicon-trash"></i>
				</button>
			</div>
			
			 <table id="vocabTable" class="table" data-toolbar="#toolbar"
			  data-url="BrowseVocab"
			  data-show-toggle="true"
              data-show-columns="true"
			  data-id-field="id"
			  data-editable-emptytext="Default empty text."
			  
			  
			  ></table>
			 </div>
		</div>
        </div>
      </div>
    </div>
	
	<div class="modal fade" id="addConceptModal" tabindex="-1" role="dialog">
			  <div class="modal-dialog" role="document">
				<div class="modal-content">
				  <div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
					<h4 class="modal-title">Add concept</h4>
				  </div>
				  <div class="modal-body">
					<form>
					  <div class="form-group">
						<label for="concept-name" class="control-label">Concept:</label>
						<input type="text" class="form-control" id="concept-name">
					  </div>
					  <div class="form-group">
						<label for="defini-text" class="control-label">Definition:</label>
						<textarea class="form-control" id="defini-text"></textarea>
					  </div>
					</form>
				  </div>
				  <div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
					<button type="button" class="btn btn-primary" id = "addconceptSubmit">Submit</button>
				  </div>
				</div>
			  </div>
	</div>
	
	<div class="modal fade" id="deleteConceptModal" tabindex="-1" role="dialog">
			  <div class="modal-dialog" role="document">
				<div class="modal-content">
				  <div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
					<h4 class="modal-title">Delete concept</h4>
				  </div>
				  <div class="modal-body">
					<p>Are you sure you want to delete the selected concept(s)?</p>
				  </div>
				  <div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">No</button>
					<button type="button" class="btn btn-primary" id = "deleteconceptSubmit">Yes</button>
				  </div>
				</div>
			  </div>
	</div>
	

	<footer class="footer">
      <div class="row" style = "margin:0">
		  <div class="col-md-2">
		  </div>
		  <div class="col-md-10">
		  <div class="container-fluid" style = "text-align:center;padding-top:20px;padding-bottom:5px">
		 This system is funded by NASA <a href="https://www.nasa.gov/goddard">Goddard</a> and <a href="http://www.nsf.gov/">NSF</a> (NNG16PU001), and supported by NASA <a href="https://esto.nasa.gov/info_technologies_aist.html">AIST</a> (NNX15AM85G). Developed and hosted by 
		<a href="http://stcenter.net/stc/">NSF Spatiotemporal Innovation Center</a>. 
		  </div>
		  </div>
			
		  </div>
    </footer>
    
  </body>
</html>
