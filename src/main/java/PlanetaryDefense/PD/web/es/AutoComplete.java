package PlanetaryDefense.PD.web.es;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import PlanetaryDefense.PD.driver.ESdriver;
import PlanetaryDefense.PD.structure.AutoCompleteData;

/**
 * Servlet implementation class AutoComplete
 */
@WebServlet("/AutoComplete")
public class AutoComplete extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AutoComplete() {
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
		// TODO Auto-generated method stub
		//doGet(request, response);
		response.setContentType("application/json");  
		response.setCharacterEncoding("UTF-8");
		String chars = request.getParameter("chars");
		
		
		List<AutoCompleteData> result = new ArrayList<AutoCompleteData>();
		ESdriver esd = (ESdriver) request.getServletContext().getAttribute("esd");
		List<String> suggestList = esd.autoComplete(chars);
        for (final String item : suggestList) {
                result.add(new AutoCompleteData(item, item));
        }
        response.getWriter().write(new Gson().toJson(result));
		/*PrintWriter out = response.getWriter();
		out.print(fileList); 
    	out.flush();*/
	}

}
