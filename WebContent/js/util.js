function showAlert(holderID, message, alertType) {		
		$('#' + holderID).append('<div id="alertdiv" class="alert alert-' +  alertType.toLowerCase() + ' alert-fixed-top' + 
		'"><a class="close" data-dismiss="alert">&times;</a><strong>' + alertType + '! </strong><span>'+message+'</span></div>');
		
		$("#alertdiv").css("text-align", "center");
		
		setTimeout(function() { 
		  $("#alertdiv").remove();
		}, 5000);
}

$(document).ready(function(){
	        $("#changepwd").click(function() {				
				$('#pwdModal').modal('show');
		    });
			
			$("#changePwdSubmit").click(function() {
                if($("#oldpwd").val()!="" && $("#newpwd").val()!="")
				{
				    if($("#oldpwd").val()!= $("#newpwd").val())
					{
					$("#newpwdForm").removeClass("has-error");
				    $('#pwdBlock').text("");
					$('#pwdModal').modal('hide');				
					$.ajax({
						url : "Changepwd",
						data : {
									"username" : $("#welUsername").text().trim().substring(9),
									"oldpwd": $("#oldpwd").val(),
									"newpwd": $("#newpwd").val()
							   },
						success : function completeHandler(response) {
								if(response.exception!=null)
								{
                                showAlert("alert_placeholder", response.exception, "Danger");
								}else{
								showAlert("alert_placeholder", response.PDResults, "Success");
								window.location.href = "logout";
								}
						}
					});			  
					}else{
					   $("#newpwdForm").addClass("has-error");
					   $('#pwdBlock').text('Your new password is the same as your current one.');
					}
				}				
		    });
});

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