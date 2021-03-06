package search.engine.main;

import org.jsoup.nodes.Document;
import search.engine.crawler.Crawler;
import search.engine.indexer.Indexer;
import search.engine.indexer.WebPage;
import search.engine.indexer.WebPageParser;
import search.engine.ranker.PageRanker;
import search.engine.server.Server;
import search.engine.utils.Utilities;
import search.engine.utils.WebUtilities;

import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;


public class Main {

    private static Scanner scanner;

    /**
     * The main driver function.
     *
     * @param args External arguments passed from the operating system
     */
    public static void main(String[] args) {
        scanner = new Scanner(System.in);

        int choice = -1;

        while (choice == -1) {
            System.out.println("Please enter a function to run:");
            System.out.println("1. Start Crawler");
            System.out.println("2. Start Server");
            System.out.println("3. Clear Database");
            System.out.println("4. Testing");
            System.out.println("5. Exit");

            choice = scanner.nextInt();

            long startTime = System.nanoTime();

            switch (choice) {
                case 1:
                    startCrawler();
                    break;
                case 2:
                    startServer();
                    break;
                case 3:
                    clearDatabase();
                    break;
                case 4:
                    test();
                    break;
                case 5:
                    System.out.println("Bye!");
                    break;
                default:
                    System.out.println("Invalid choice");
                    choice = -1;
                    break;
            }

            long endTime = System.nanoTime();
            long millis = (endTime - startTime) / (long) 1e6;
            long secs = millis / 1000;

            System.out.printf("Elapsed Time: %d min : %d sec : %d ms\n", secs / 60, secs % 60, millis % 1000);
        }

        scanner.close();
    }

    /**
     * Start running the crawling process.
     */
    private static void startCrawler() {
        System.out.println("Please enter the number of crawler threads: ");
        int cnt = scanner.nextInt();

        Indexer indexer = new Indexer();
        Crawler crawler = new Crawler(indexer);
        crawler.start(cnt);

        PageRanker pageRanker = new PageRanker(indexer);
        pageRanker.start(true);

        // crawler.readPreviousData();
        //
        // while (true) {
        //     crawler.start(cnt);
        // }
    }

    /**
     * Start serving the search engine on port 8080.
     */
    private static void startServer() {
        // Server
        Server.serve();
        System.out.println("Server is running on port 8080...");
    }

    /**
     * Clears our database and recreate the indexes.
     */
    private static void clearDatabase() {
        System.out.println("Clearing search engine database...");
        Indexer.reset();
        System.out.println("Done!");
    }

    /**
     * Just for testing.
     */
    private static void test() {
        try {
            //testIndexer();
            testWebPageParser();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testIndexer() {
        Indexer indexer = new Indexer();

        indexer.searchByWord(
                Arrays.asList("google", "code", "jam", "hello", "world", "wikipedia"),
                Arrays.asList("googl", "cod", "jam", "hello", "world", "wikipedia")
        );
    }

    private static void testWebPageParser() throws Exception {
        URL url = new URL("https://policies.google.com/privacy?hl=ar");

        Document doc = WebUtilities.fetchWebPage(url.toString());

        System.out.println("Fetched");

        WebPageParser parser = new WebPageParser(url, doc);
        WebPage page = parser.getParsedWebPage();

        System.out.println(doc.text());
    }
}
