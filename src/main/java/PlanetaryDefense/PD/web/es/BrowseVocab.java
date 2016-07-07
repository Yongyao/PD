package PlanetaryDefense.PD.web.es;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import PlanetaryDefense.PD.driver.ESdriver;
/**
 * Servlet implementation class BrowseVocab
 */
@WebServlet("/BrowseVocab")
public class BrowseVocab extends HttpServlet {
	private static final long serialVersionUID = 1L;

       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BrowseVocab() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		/*response.setContentType("application/json");  
		response.setCharacterEncoding("UTF-8");
		
		ESdriver esd = (ESdriver) request.getServletContext().getAttribute("esd");
		String vocabList = esd.getVocabList();
		PrintWriter out = response.getWriter();
		out.print(vocabList); 
    	out.flush();*/
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
