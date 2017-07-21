package PlanetaryDefense.PD.web.auth;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import PlanetaryDefense.PD.driver.MysqlDriver;


/**
 * Servlet implementation class SigninWithGoogle
 */
@WebServlet("/SigninWithGoogle")
public class SigninWithGoogle extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String clientID = "139919091373-larqclqlkvs07c9m8abagn49hqm1q424.apps.googleusercontent.com";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SigninWithGoogle() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter out = response.getWriter();
		String idTokenString = request.getParameter("token");
    GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
        .setAudience(Arrays.asList(clientID.split(","))).setIssuer("accounts.google.com").build();

    GoogleIdToken idToken = null;
    try {
      idToken = verifier.verify(idTokenString);
    } catch (GeneralSecurityException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    if (idToken != null) {
      Payload payload = idToken.getPayload();
      // Print user identifier
      String userId = payload.getSubject();
      // Get profile information from payload
      String email = payload.getEmail();
      boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());

      MysqlDriver mysql = new MysqlDriver();
      mysql.getConnection();
      String user = mysql.getUserByEmail(email);

      if (!emailVerified || user==null) {
        return;
      } else {
        List<String> roles = mysql.getRoleByUser(user);

        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (String role : roles) {
          authorities.add(new SimpleGrantedAuthority(role));
        }

        SecurityContextHolder.clearContext();

        //what is 'xx'
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(user,
            "xx", true, true, true, true, authorities);

        org.springframework.security.core.userdetails.User springUser = new org.springframework.security.core.userdetails.User(
            user, "xx", true, true, true, true, authorities);
        Authentication tauthentication = new UsernamePasswordAuthenticationToken(springUser, null,
            userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(tauthentication);

        //null pointer
//        Message message = PhaseInterceptorChain.getCurrentMessage();
//        HttpServletRequest request1 = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);
        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
      }
      
      
      out.print("success");
    } else {
      System.out.println("Invalid ID token.");
    }
    return;
	}

}
