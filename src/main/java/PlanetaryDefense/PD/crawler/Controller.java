package PlanetaryDefense.PD.crawler;

import java.util.concurrent.TimeUnit;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class Controller {

	public Controller() {
		// TODO Auto-generated constructor stub
	}

    public static void main(String[] args) throws Exception {
        String crawlStorageFolder = null;
        if (args.length > 0) {
        	crawlStorageFolder = args[0];
  	    } else {
  	    	crawlStorageFolder = "C:/crawlertest/root";
    	}
       
        int numberOfCrawlers = 5;

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setMaxDepthOfCrawling(1);
        config.setMaxPagesToFetch(18);
        config.setResumableCrawling(false);
        
        /*
         * IMPORTANT CONFIG OPTIONS
         * 
         * - setCrawlStorageFolder
         * - setMaxDepthOfCrawling
         * - setMaxPagesToFetch
         * - setResumableCrawling
         * - setBinaryContentInCrawling
         * - setMaxOutgoingLinksToFollow
         * - setMaxDownloadSize ?
         * 
         * NEED
         * 
         * - index name to store stuff in
         * - exclude_urls
         */

        /*
         * Instantiate the controller for this crawl.
         */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        /*
         * For each crawl, you need to add some seed urls. These are the first
         * URLs that are fetched and then the crawler starts following links
         * which are found in these pages
         */
        controller.addSeed("https://podaac.jpl.nasa.gov/AQUA");
        controller.addSeed("https://podaac.jpl.nasa.gov/GRACE");
        
        controller.addSeed("https://podaac.jpl.nasa.gov/ADEOS-II");
        controller.addSeed("https://podaac.jpl.nasa.gov/aquarius");
        controller.addSeed("https://podaac.jpl.nasa.gov/GEOS-3");
        controller.addSeed("https://podaac.jpl.nasa.gov/GHRSST");
        controller.addSeed("https://podaac.jpl.nasa.gov/ISS-RapidScat");
        controller.addSeed("https://podaac.jpl.nasa.gov/JASON1");
        controller.addSeed("https://podaac.jpl.nasa.gov/JASON3");
        controller.addSeed("https://podaac.jpl.nasa.gov/MEaSUREs");
        controller.addSeed("https://podaac.jpl.nasa.gov/MODIS");
        controller.addSeed("https://podaac.jpl.nasa.gov/NSCAT");
        controller.addSeed("https://podaac.jpl.nasa.gov/OSTM-JASON2");
        controller.addSeed("https://podaac.jpl.nasa.gov/QuikSCAT");
        controller.addSeed("https://podaac.jpl.nasa.gov/SeaSAT");
        controller.addSeed("https://podaac.jpl.nasa.gov/SPURS");
        controller.addSeed("https://podaac.jpl.nasa.gov/Terra");
        controller.addSeed("https://podaac.jpl.nasa.gov/TOPEX-POSEIDON");
        
        /*controller.addSeed("http://global.jaxa.jp/");
        controller.addSeed("http://neo.ssa.esa.int/");
        controller.addSeed("http://neocam.ipac.caltech.edu/");
        controller.addSeed("http://www.minorplanetcenter.net/iau/mpc.html");*/
       

        /*
         * Start the crawl. This is a blocking operation, meaning that your code
         * will reach the line after this only when crawling is finished.
         */
        controller.start(MyCrawler.class, numberOfCrawlers);
        System.out.println("Done");
       
        
        //print out full configuration
        //config.toString(); 
    }

}
