<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@page session="true"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <title>Planetary Defense</title>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="icon" href="images/NASALogo_burned.png">
  
  <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script>
  <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
  <!-- bt jasny -->
  <link rel="stylesheet" href="http://cdnjs.cloudflare.com/ajax/libs/jasny-bootstrap/3.1.3/css/jasny-bootstrap.min.css">
  <script src="http://cdnjs.cloudflare.com/ajax/libs/jasny-bootstrap/3.1.3/js/jasny-bootstrap.min.js"></script>

  <script src="//cdnjs.cloudflare.com/ajax/libs/jqueryui/1.11.2/jquery-ui.js"></script>
  
  <!-- Custom styles for this template -->
  <link href="dashboard.css" rel="stylesheet">
  <script src="js/util.js"></script>
  <style>
  .ui-autocomplete {
    position: absolute;
    z-index: 1000;
    cursor: default;
    padding: 0;
    margin-top: 2px;
    list-style: none;
    background-color: #ffffff;
    border: 1px solid #ccc
    -webkit-border-radius: 5px;
       -moz-border-radius: 5px;
            border-radius: 5px;
    -webkit-box-shadow: 0 5px 10px rgba(0, 0, 0, 0.2);
       -moz-box-shadow: 0 5px 10px rgba(0, 0, 0, 0.2);
            box-shadow: 0 5px 10px rgba(0, 0, 0, 0.2);
	}
	.ui-autocomplete > li {
	  padding: 3px 20px;
	}
	.ui-autocomplete > li.ui-state-focus {
	  background-color: #DDD;
	}
	.ui-helper-hidden-accessible {
	  display: none;
	}

  </style>  

</head>

<script type="text/javascript">
	$(document).ready(function(){
	
	$("#query").autocomplete({
            source: function(request, response) {
                $.ajax({
                    url: "AutoComplete",
                    type: "POST",
                    data: {chars: $("#query").val()},

                    dataType: "json",

                    success: function(data) {
					    response(data);
                    }
               });              
            }   
        });
	
            var query = getURLParameter('query');
			if(query==null)
			{			
				$("#searchResults").hide();
			}else{
				$("#searchResults").hide();
				$("#NotFound").hide();
				$("#query").val(query);
				search(query);
			
			}

			$("#query").keyup(function(event){
				if(event.keyCode == 13){
					$("#searchButton").click();
				}
			});		
			
			$("#searchButton").click(function() {				
				setGetParameter("query", $("#query").val());
		   });

	});
	
	function search(query){
	if($("#query").val()!="")
				{				
				$("#resultPanels").empty();
				
				$("#searchBox").append($("#searchGroup"));
				$("#searchjumbo").hide();
				$("#note").hide();
				$("#searchResults").show();
				$("#searchLoading").show();
				$.ajax({
					url : "SearchByQuery",
					data : {
								"query" : $("#query").val()
						   },
					success : function completeHandler(response) {
						if(response!=null)
						{
							$("#searchLoading").hide();
							console.log(response);
							var searchResults = response.PDResults;
							if(searchResults.length==0)
							{
							$("#NotFound").show();
							}else{
							for(var i=0; i<searchResults.length; i++)
							{
								var col_div = $("<div/>", {
											class : "col-md-12",
										}).appendTo('#resultPanels');
								
								var panel_div = $("<div/>", {
											class : "panel panel-default",
										}).appendTo(col_div);
										
								var heading_div = $("<div/>", {
											class : "panel-heading",
											html: "<strong>Name: </strong>" + FileNameFormatter(searchResults[i].Name)
										}).appendTo(panel_div);
										
								var body_div = $("<div/>", {
											class : "panel-body",
										}).appendTo(panel_div);
										
								fillPanelBody(body_div, searchResults[i]);
							}
							}
						}					
					}
				});		
		
			   }
	}
	
	function FileNameFormatter(value) {
	    var url = "FileUpload?fileName="+encodeURIComponent(value);
        //url = encodeURIComponent(url);		
		return '<a href=' + url + ' target="_blank">' + value + '</a>'; 
    }
	
	function fillPanelBody(body_div, result)
	{
	        var row_div = $("<div/>", {
								class : "row",
						}).appendTo(body_div);
			var col_div1 = $("<div/>", {
								class : "col-md-2",
						}).appendTo(row_div);
			/*var container = $("<div/>", {
								class : ".container-fluid",
						}).appendTo(col_div1);*/
			//fillMetaData(container, result);
			fillMetaData(col_div1, result);
			
			var col_div2 = $("<div/>", {
								class : "col-md-10",
								text : result.content
						}).appendTo(row_div);
			
	}
	
	function fillMetaData(div, result){
	      if(result.Type!=null)
		  {
		        var value = result.Type;
					var row = $("<div/>", {
								class : "row",
								html: "<strong>Type</strong>: " + value
								
						}).css("margin", 0).appendTo(div);
		  }
		  
		  if(result.Size!=null)
		  {
		        var value = result.Size;
					var row = $("<div/>", {
								class : "row",
								html: "<strong>Size(K)</strong>: " + value
								
						}).css("margin", 0).appendTo(div);
		  }
		  
		  if(result["Uploaded Time"]!=null)
		  {
		        var value = result["Uploaded Time"];
					var row = $("<div/>", {
								class : "row",
								html: "<strong>Uploaded time</strong>: " + value
								
						}).css("margin", 0).appendTo(div);
		  }
		  
	}
	
	function setGetParameter(paramName, paramValue)
	{
		var url = window.location.href;
		var hash = location.hash;
		url = url.replace(hash, '');
		if (url.indexOf(paramName + "=") >= 0)
		{
			var prefix = url.substring(0, url.indexOf(paramName));
			var suffix = url.substring(url.indexOf(paramName));
			suffix = suffix.substring(suffix.indexOf("=") + 1);
			suffix = (suffix.indexOf("&") >= 0) ? suffix.substring(suffix.indexOf("&")) : "";
			url = prefix + paramName + "=" + paramValue + suffix;
		}
		else
		{
		if (url.indexOf("?") < 0)
			url += "?" + paramName + "=" + paramValue;
		else
			url += "&" + paramName + "=" + paramValue;
		}
		window.location.href = url + hash;
	}
	
	function getURLParameter(name) {
	  return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search)||[,""])[1].replace(/\+/g, '%20'))||null;
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
		
		<!--<form class="navbar-form navbar-right">
            <div class="form-group">
              <input type="text" placeholder="Email" class="form-control">
            </div>
            <div class="form-group">
              <input type="password" placeholder="Password" class="form-control">
            </div>
            <button type="submit" class="btn btn-default">Sign in</button>
        </form>-->
		
      </div>
    </nav>
	
	<div class="container-fluid">
      <div class="row">
        <div class="col-sm-3 col-md-2 sidebar">
          <ul class="nav nav-sidebar">
            <li><a href="doc.jsp">Document archiving</a></li>
			<li class="active"><a id= "searchNav" href = "search.jsp">Content search</a></li>			
          </ul>
		  
		  <ul class="nav nav-sidebar">
            <li><a href="http://199.26.254.186/pdwiki/index.php/Main_Page" target="_blank">About (Wiki)</a></li>
          </ul>
          
        </div>
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
          <h1 class="page-header">Content search</h1>
		  <p class="lead" id = "note">This search engine currently supports most of the common document formats, as well as image and video formats. For a comprehensive list of supported
		  formats, please visit
		  <a href="https://tika.apache.org/1.12/formats.html" target="_blank">Apache Tika</a> project.</p>


		<div id= "searchjumbo" class="container" style = "width:84%;margin:7%">
	      <h2 style ="text-align:center">Planetary Defense Documents Discovery <img src="images/docs.png" height="45"></h2>
		  <p style ="text-align:center">Please search for archived documents by keywords.</p>
		  <div class="input-group" style="width:56%;;margin:0 auto" id = "searchGroup">
			   <input type="text" class="form-control" placeholder="Search text..." id="query" name="query" value="">
			   <div class="input-group-btn">
				   <button type="button" class="btn btn-success" id = "searchButton" ><span class="glyphicon glyphicon-search"></span></button>
				</div>
		  </div>

		  <p style="margin-left:22%;margin-top:1%"><a class="btn btn-primary" href="doc.jsp"><span class="glyphicon glyphicon-eye-open"></span> Browse latest documents</a></p>
	   </div>
	   
	   <div class="container" id = "searchResults" style="width:80%">
	     <div class="row" style = "border-bottom:1px solid #ddd; padding-bottom:10px;margin-bottom:10px;position:relative">
		   <div class="col-md-12" id = "searchBox">
		   </div>
         </div> 
		 <p id = "searchLoading" style="text-align:center; margin:10%"><img src="images/loading.gif" height="80"><br> Please wait while results are loading. </p>
         
		 <div class="row" id = "resultPanels"> 		   
         </div> 
		 
		 <div class="row" id = "NotFound" style = "font-size:medium">
			Your search did not match any documents. <br><strong>Suggestions</strong>:
			<ul>
			  <li>Check spelling.</li>
			  <li>Try different keywords.</li>
			  <li>Try fewer and more general keywords.</li>
			</ul> 
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
						<label for="concept-name" class="control-label">Current password:</label>
						<input type="password" class="form-control" id="oldpwd">
					  </div>
					  <div class="form-group">
						<label for="concept-name" class="control-label">New password:</label>
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