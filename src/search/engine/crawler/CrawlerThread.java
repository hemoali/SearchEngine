package search.engine.crawler;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;


public class CrawlerThread extends java.lang.Thread {

    private BlockingQueue<String> mWebURLs;
    private ConcurrentSkipListSet<String> mVisitedURLs;
    private ConcurrentHashMap<String, Integer> mBaseURLCnt;
    private static int mWebPagesCnt;
    private RobotsTextParser mRobotTxtParser;

    /**
     * CrawlerThread Constructor that takes the list of the current web urls and the visited ones
     *
     * @param toCrawl
     * @param crawled
     * @param robotManager
     */
    CrawlerThread(BlockingQueue<String> toCrawl, ConcurrentSkipListSet<String> crawled, RobotsTextManager robotManager, ConcurrentHashMap<String, Integer> baseUrlCnt, int webPagesCnt) {
        mWebURLs = toCrawl;
        mVisitedURLs = crawled;
        mRobotTxtParser = new RobotsTextParser(robotManager);
        mBaseURLCnt = baseUrlCnt;
        mWebPagesCnt = webPagesCnt;
    }

    /**
     * Gets the URL that is in front of the Queue and returns it
     * Throws exception if it couldn't poll any urls
     *
     * @return
     * @throws Exception
     */
    private String getNextUrl() throws Exception {
        String url = "";
        try {
            url = mWebURLs.poll(Constants.MAX_POLL_WAIT_TIME, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            Output.log(e.getMessage());
        }
        if (url == null) {
            throw new Exception("Cannot poll anymore URLS Exiting !");
        }
        return url;
    }

    /**
     * Adds the URL to the Queue and sets it as visited
     *
     * @param url
     */
    private void addUrl(String url) {
        mWebURLs.add(url);
        mVisitedURLs.add(url);

        try {
            URL x = new URL(url);
            Integer cnt = mBaseURLCnt.getOrDefault(URLUtilities.getBaseURL(x), 0);
            mBaseURLCnt.put(URLUtilities.getBaseURL(x), cnt + 1);
            mWebPagesCnt++;
        } catch (MalformedURLException e) {

        }

        Output.logURL(url);
    }

    /**
     * Checks if the maximum limit of crawling web pages is not reached
     * and checks if the url is not visited.
     * To be called exclusively.
     *
     * @param url
     * @return
     */
    private boolean isCrawlable(URL url) {
        String tmp = url.toString();

        return mWebPagesCnt < Constants.MAX_WEBPAGES_CNT
                && mBaseURLCnt.getOrDefault(URLUtilities.getBaseURL(url), 0) < Constants.MAX_BASE_URL_CNT
                && !mVisitedURLs.contains(tmp);
    }

    /**
     * returns true if the given URL doesn't violate the robots.txt rules
     *
     * @param url
     * @return
     */
    private boolean isAllowedByRobotsTxt(String url) {
        try {
            URL tmp = new URL(url);

            if (!mRobotTxtParser.isUrlAllowed(tmp)) {
                Output.log("Couldn't crawl url : " + url + " because of robots.txt !!!!!!");
                return false;
            }

            return true;
        } catch (MalformedURLException e) {
            Output.log(e.getMessage());
        }
        return false;
    }

    /**
     * Takes the HTML Document and inserts the URLs in the document into the Queue
     *
     * @param doc
     */
    private void extractURLS(Document doc) {
        Elements links = WebPageManager.processWebPage(doc);
        URL nextUrl;
        for (Element link : links) {
            try {
                nextUrl = new URL(link.attr("abs:href"));
            } catch (MalformedURLException e) {
                Output.log(e.getMessage());
                continue;
            }
            if (!isAllowedByRobotsTxt(nextUrl.toString()))
                continue;
            //lock the arrays and insert in them
            synchronized (mVisitedURLs) {
                synchronized (mWebURLs) {
                    synchronized (mBaseURLCnt) {
                        if (isCrawlable(nextUrl))
                            addUrl(nextUrl.toString());
                        else
                            Output.log("skipped: " + nextUrl);
                    }
                }
            }
        }
    }

    /**
     * When a URL is found to be a bad url which is when a URL is not allowed by robots.txt or it gives an error
     * during the connection remove it from the webpages limit
     *
     * @param url
     */
    private void removeURLFromCnt(String url) {
        synchronized (mBaseURLCnt) {
            try {
                URL x = new URL(url);
                Integer cnt = mBaseURLCnt.getOrDefault(URLUtilities.getBaseURL(x), 1);
                mWebPagesCnt--;
                mBaseURLCnt.put(URLUtilities.getBaseURL(x), cnt - 1);
            } catch (MalformedURLException e) {

            }
        }
    }

    /**
     * Crawler function
     * As long as there exists URLs to crawl the function would be running getting documents and new URLs
     */
    @Override
    public void run() {
        System.out.println("Crawler " + this.getName() + " started !");

        while (true) {
            String curUrl;

            try {
                curUrl = getNextUrl();
            } catch (Exception e) {
                Output.log(e.getMessage());
                return;
            }

            if (mWebPagesCnt > Constants.MAX_WEBPAGES_CNT)
                continue;


            Output.log("Crawling: " + curUrl);

            Document doc = URLUtilities.getWebPage(curUrl);

            if (doc == null) {
                removeURLFromCnt(curUrl);
                Output.logDisallowedURL(curUrl);
                continue;
            }

            //if (!doc.baseUri().equals(curUrl))
            //	Output.logVisitedURL(curUrl);

            Output.logVisitedURL(doc.baseUri());

            extractURLS(doc);
        }
    }
}