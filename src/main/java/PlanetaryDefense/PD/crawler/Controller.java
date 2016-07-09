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
        String crawlStorageFolder = "C:/crawlertest/root";
        int numberOfCrawlers = 10;

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setMaxDepthOfCrawling(1000);
        config.setMaxPagesToFetch(1000000);
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
        controller.addSeed("http://neo.jpl.nasa.gov/");
        controller.addSeed("http://global.jaxa.jp/");
        controller.addSeed("http://neo.ssa.esa.int/");
        controller.addSeed("http://neocam.ipac.caltech.edu/");

        /*
         * Start the crawl. This is a blocking operation, meaning that your code
         * will reach the line after this only when crawling is finished.
         */
        controller.start(MyCrawler.class, numberOfCrawlers);
        MyCrawler.bulkProcessor.awaitClose(20, TimeUnit.MINUTES);
        MyCrawler.esd.closeES();
       
        
        //print out full configuration
        //config.toString(); 
    }

}
