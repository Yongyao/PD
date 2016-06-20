package PlanetaryDefense.PD.web;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import PlanetaryDefense.PD.driver.ESdriver;

/**
 * Application Lifecycle Listener implementation class FileLocationContextListener
 *
 */
@WebListener
public class PDContextListener implements ServletContextListener {

    /**
     * Default constructor. 
     */
    public PDContextListener() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0)  { 
         // TODO Auto-generated method stub
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0)  { 
         // TODO Auto-generated method stub
    	
    	//String rootPath = System.getProperty("catalina.home");
    	String rootPath = "/usr";
    	
    	ServletContext ctx = arg0.getServletContext();
    	String relativePath = ctx.getInitParameter("PathToUpload");
    	File file = new File(rootPath + File.separator + relativePath);
    	if(!file.exists()) file.mkdirs();
    	ctx.setAttribute("FILES_DIR_FILE", file);
    	ctx.setAttribute("FILES_DIR", rootPath + File.separator + relativePath);
    	
    	ESdriver esd = new ESdriver();
    	ctx.setAttribute("esd", esd);

    }
	
}
