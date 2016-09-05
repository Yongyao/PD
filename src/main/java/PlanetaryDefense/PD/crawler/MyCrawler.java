package PlanetaryDefense.PD.crawler;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;

import PlanetaryDefense.PD.driver.ESdriver;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class MyCrawler extends WebCrawler{
	//private int countURL = 0;
	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
            + "|png|mp3|mp3|zip|gz))$");
	
	 @Override
     public boolean shouldVisit(Page referringPage, WebURL url) {
         String href = url.getURL().toLowerCase();
         return !FILTERS.matcher(href).matches()
        		&& !href.contains("http://ssd.jpl.nasa.gov/sbdb.cgi?sstr=");
               // && href.startsWith("http://neo.jpl.nasa.gov/") && !href.contains("http://ssd.jpl.nasa.gov/sbdb.cgi?sstr=");
     }

     /**
      * This function is called when a page is fetched and ready
      * to be processed by your program.
      */
     @Override
     public void visit(Page page) {
         String url = page.getWebURL().getURL();
         //System.out.println("URL: " + url);

         if (page.getParseData() instanceof HtmlParseData) {
             HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
             String text = htmlParseData.getText();
            // text = text.replaceAll("[^\\S\\r\\n]+", " ").replaceAll("\\n+", " ").replaceAll("\\s+", " ");
             
            // String html = htmlParseData.getHtml();
            // Set<WebURL> links = htmlParseData.getOutgoingUrls();

             //System.out.println("Text length: " + text.length());
             //System.out.println("Title: " + htmlParseData.getTitle());
             //System.out.println("Html length: " + html.length());
             //System.out.println("Number of outgoing links: " + links.size());

            File file = new File("C:/Users/Yongyao/Desktop/reverb/data/" + UUID.randomUUID().toString());
 			
 			try {
				file.createNewFile();
				FileWriter fw = new FileWriter(file.getAbsoluteFile());
	 			BufferedWriter bw = new BufferedWriter(fw);
	 			bw.write(text);
	 			bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

 			

     		
         }
    }

	public MyCrawler() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
