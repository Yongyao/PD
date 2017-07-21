<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
	<link rel="icon" href="images/NASALogo_burned.png">
	<title>PD Login Page</title>
	<link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script>
	<script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
	<!--google sign in-->
	<script src="https://apis.google.com/js/platform.js" async defer></script>
	<meta name="google-signin-client_id" content="139919091373-larqclqlkvs07c9m8abagn49hqm1q424.apps.googleusercontent.com">

	<style>
	.error {
		padding: 15px;
		margin-bottom: 20px;
		border: 1px solid transparent;
		border-radius: 4px;
		color: #a94442;
		background-color: #f2dede;
		border-color: #ebccd1;
	}

	.msg {
		padding: 15px;
		margin-bottom: 20px;
		border: 1px solid transparent;
		border-radius: 4px;
		color: #31708f;
		background-color: #d9edf7;
		border-color: #bce8f1;
	}   
	
	
	body {
	  padding-top: 40px;
	  padding-bottom: 40px;
	  background-color: #eee;
	  background-size: cover;
	}

	.form-signin {
	  max-width: 330px;
	  padding: 15px;
	  margin: 0 auto;
	}
	.form-signin .form-signin-heading,
	.form-signin .checkbox {
	  margin-bottom: 10px;
	}
	.form-signin .checkbox {
	  font-weight: normal;
	}
	.form-signin .form-control {
	  position: relative;
	  height: auto;
	  -webkit-box-sizing: border-box;
		 -moz-box-sizing: border-box;
			  box-sizing: border-box;
	  padding: 10px;
	  font-size: 16px;
	}
	.form-signin .form-control:focus {
	  z-index: 2;
	}
	.form-signin input[type="email"] {
	  margin-bottom: -1px;
	  border-bottom-right-radius: 0;
	  border-bottom-left-radius: 0;
	}
	.form-signin input[type="password"] {
	  margin-bottom: 10px;
	  border-top-left-radius: 0;
	  border-top-right-radius: 0;
	}

</style>
</head>
<body onload='document.loginForm.username.focus();' style = "background: url('images/background.jpg') no-repeat center center fixed;background-size:cover;overflow:hidden">
<div class="container">
		<form class="form-signin" name='loginForm' id='loginForm'
			action="<c:url value='/j_spring_security_check' />" method='POST'>
			<h2 class="form-signin-heading" style="color:#F8F8F8">Please sign in</h2>	
			<label for="inputEmail" class="sr-only">Email address</label> 
			<input id="inputEmail" class="form-control" name='username' placeholder="User name" required autofocus> 
			<label for="inputPassword" class="sr-only">Password</label> 
			<input id="inputPassword" class="form-control" name='password' type="password" placeholder="Password" required>
			<label>
            <input type="checkbox" value="remember-me"> <span style="color:#F8F8F8">Remember me</span>
            </label>
		</form>

		<input type="button" class="btn btn-lg btn-primary btn-block" type="submit" id="submit" style="width:300px;margin:auto" value="Login"></input>
		<br>
		<!--<button class="btn btn-lg btn-primary btn-block" type="submit" id="submit" style="width:300px;margin:auto">Sign in</button>-->
		<div class="g-signin2" data-onsuccess="onSignIn" style="width:300px;margin:auto"></div>
</div>

<nav class="navbar navbar-default navbar-fixed-bottom" style="padding:15px">
  <div class="container-fluid" style = "text-align:center;font-size:small">
   This system is funded by NASA <a href="https://www.nasa.gov/goddard">Goddard</a> and <a href="http://www.nsf.gov/">NSF</a> (NNG16PU001), and supported by NASA <a href="https://esto.nasa.gov/info_technologies_aist.html">AIST</a> (NNX15AM85G). Developed and hosted by 
		<a href="http://stcenter.net/stc/">NSF Spatiotemporal Innovation Center</a> on the <a href="http://cloud.gmu.edu">Hybrid Cloud Service</a>.
  </div>
</nav>
</body>
<script type="text/javascript">

$('#submit').click(function(e) {
    var login = $("#inputEmail").val();
    var pass = $("#inputPassword").val();
    $("#loginForm").submit(); 
});

/*function onSignIn(googleUser) {
  var profile = googleUser.getBasicProfile();
  console.log('ID: ' + profile.getId()); // Do not send to your backend! Use an ID token instead.
  console.log('Name: ' + profile.getName());
  console.log('Image URL: ' + profile.getImageUrl());
  console.log('Email: ' + profile.getEmail()); // This is null if the 'email' scope is not present.
} */
function onSignIn(googleUser) {
		// The ID token you need to pass to your backend:
		var id_token = googleUser.getAuthResponse().id_token;
		
		$.ajax({
			url : "SigninWithGoogle", 
			type : 'POST',
			success : function completeHandler(response) {
				console.log(response);
				if (response.indexOf('success') !== -1)
					window.location = "doc.jsp";						
			},
			data :{
				"token" : id_token
		   }
			
		});
		/* var xhr = new XMLHttpRequest();
		xhr.open('POST', 'googleSignIn');
		xhr.setRequestHeader('Content-Type',
				'application/x-www-form-urlencoded');
		xhr.onload = function() {
			if (xhr.responseText === 'Success')
				window.location = "/doc.jsp";
		};
		xhr.send(id_token); */
	}
	window.onbeforeunload = function(e) {
		// gapi.auth.signOut();
        gapi.auth2.getAuthInstance().signOut();
	};


</script>
</html>