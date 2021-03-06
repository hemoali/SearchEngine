package search.engine.crawler;

import search.engine.utils.Constants;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Input {

    /**
     * Reads the URL seeds and fills the URLs queue and the visited URLs set.
     */
    public static void readSeed() {
        try {
            BufferedReader file = new BufferedReader(new FileReader(Constants.SEED_FILE_NAME));
            String url;

            while ((url = file.readLine()) != null) {
                if (!CrawlerThread.sVisitedURLs.contains(url)) {
                    CrawlerThread.sURLsQueue.add(url);
                    CrawlerThread.sVisitedURLs.add(url);
                }
            }

            file.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads the data of the previous runs in case of interruption.
     * Used to continue from the same state as before.
     */
    public static void readPreviousData() {
        try {
            CrawlerThread.sVisitedURLs.addAll(readVisitedURLs());
            CrawlerThread.sURLsQueue.addAll(readURLs());
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads the URLs that has been visited in the previous run.
     *
     * @return set of visited URLs.
     */
    private static Set<String> readVisitedURLs() throws Exception {
        Set<String> ret = new HashSet<>();
        BufferedReader file = new BufferedReader(new FileReader(Constants.VISITED_URLS_FILE_NAME));
        String url;

        while ((url = file.readLine()) != null) {
            ret.add(url);
        }

        file.close();
        return ret;
    }

    /**
     * Reads the URLs to be crawled.
     *
     * @return a list of URLs to be crawled
     */
    private static List<String> readURLs() throws Exception {
        List<String> ret = new ArrayList<>();
        BufferedReader file = new BufferedReader(new FileReader(Constants.URLS_FILE_NAME));
        String url;

        while ((url = file.readLine()) != null) {
            if (!CrawlerThread.sVisitedURLs.contains(url)) {
                ret.add(url);
                CrawlerThread.sVisitedURLs.add(url);
            }
        }

        file.close();
        return ret;
    }
}
