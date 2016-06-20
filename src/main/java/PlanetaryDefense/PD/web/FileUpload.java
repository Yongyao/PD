package PlanetaryDefense.PD.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import PlanetaryDefense.PD.driver.ESdriver;


/**
 * Servlet implementation class FileUpload
 */
@WebServlet("/FileUpload")
public class FileUpload extends HttpServlet {
	private static final long serialVersionUID = 1L;
	//private final String UPLOAD_DIRECTORY = "C:/Eclipse workspace2/PD/WebContent/Uploaded";
	//String UPLOAD_DIRECTORY = getServletContext().getInitParameter("PathToUpload");
	private ESdriver esd = new ESdriver();
	private ServletFileUpload uploader = null;


	
    /**
     * Default constructor. 
     */
    public FileUpload() {
        // TODO Auto-generated constructor stub
    	/*DiskFileItemFactory fileFactory = new DiskFileItemFactory();
    	File filesDir = (File) getServletContext().getAttribute("FILES_DIR_FILE");
    	fileFactory.setRepository(filesDir);
    	this.uploader = new ServletFileUpload(fileFactory);*/
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		String fileName = request.getParameter("fileName");
		if(fileName == null || fileName.equals("")){
		   throw new ServletException("File Name can't be null or empty");
		}
		File file = new File(request.getServletContext().getAttribute("FILES_DIR")+File.separator+fileName);
		if(!file.exists()){
		  throw new ServletException("File doesn't exists on server.");
		}
		System.out.println("File location on server::"+file.getAbsolutePath());
		ServletContext ctx = getServletContext();
		InputStream fis = new FileInputStream(file);
		String mimeType = ctx.getMimeType(file.getAbsolutePath());
		response.setContentType(mimeType != null? mimeType:"application/octet-stream");
		response.setContentLength((int) file.length());
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		ServletOutputStream os  = response.getOutputStream();
		byte[] bufferData = new byte[1024];
		int read=0;
		while((read = fis.read(bufferData))!= -1){
		    os.write(bufferData, 0, read);
		}
		os.flush();
		os.close();
		fis.close();
		System.out.println("File downloaded at client successfully");

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		if(!ServletFileUpload.isMultipartContent(request)){
			throw new ServletException("Content type is not multipart/form-data");
		}

		PrintWriter out = response.getWriter();
		try {
			//List<FileItem> fileItemsList = uploader.parseRequest(request);
			List<FileItem> fileItemsList = new ServletFileUpload(
                                                   new DiskFileItemFactory()).parseRequest(request);
			Iterator<FileItem> fileItemsIterator = fileItemsList.iterator();
			while(fileItemsIterator.hasNext()){
				FileItem fileItem = fileItemsIterator.next();
				/*System.out.println("FieldName="+fileItem.getFieldName());
				System.out.println("FileName="+fileItem.getName());
				System.out.println("ContentType="+fileItem.getContentType());
				System.out.println("Size in bytes="+fileItem.getSize());*/
				
				String fullName = fileItem.getName();
				long size = fileItem.getSize()/1024;
				String format = null;
				String shortName = null;
                int index = fullName.lastIndexOf(".");
                if(index > 0){
                               format = fullName.substring(index+1);
                               format = format.toLowerCase();
                               shortName = fullName.substring(0, index);
                }
                if(esd.indexNewFileInfo(fullName, size, format))
                {
                	File file = new File(request.getServletContext().getAttribute("FILES_DIR")+File.separator+fileItem.getName());
                	System.out.println("Absolute Path at server="+file.getAbsolutePath());
                	fileItem.write(file);
                	/*out.write("File "+fileItem.getName()+ " uploaded successfully.");
				out.write("<br>");
				out.write("<a href=\"UploadDownloadFileServlet?fileName="+fileItem.getName()+"\">Download "+fileItem.getName()+"</a>");*/
                	out.print("Your file has been uploaded successfully.");
                	esd.indexNewFileContentM(shortName, fullName, size, format, request.getServletContext().getAttribute("FILES_DIR")+File.separator+fileItem.getName());
                }else{
                	out.print("There is already a file called the same name.");
                }
			}
		} catch (FileUploadException e) {
			out.print("Exception in uploading file.");
		} catch (Exception e) {
			out.print("Exception in uploading file.");
		}
		out.flush();
		
	}
	
	
}
