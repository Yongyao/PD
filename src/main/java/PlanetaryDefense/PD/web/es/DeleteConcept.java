package PlanetaryDefense.PD.web.es;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import PlanetaryDefense.PD.ESdriver;

/**
 * Servlet implementation class DeleteConcept
 */
@WebServlet("/DeleteConcept")
public class DeleteConcept extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ESdriver esd = new ESdriver();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeleteConcept() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		String concept = request.getParameter("concept");
		
		QueryBuilder qb = QueryBuilders.queryStringQuery(concept); 
		esd.deleteByQuery(esd.vocabType, qb);
		PrintWriter out = response.getWriter();
		out.print("Your concept has been deleted successfully."); 
    	out.flush();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
