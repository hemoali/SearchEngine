package search.engine.server;

import search.engine.utils.Constants;
import search.engine.utils.Utilities;
import spark.Request;
import spark.Response;

import java.util.List;

import static spark.Spark.get;
import static spark.SparkBase.port;


public class Server {

    /**
     * Starts serving the clients.
     */
    public static void serve() {
        // Server
        port(Constants.PORT);

        get("/search", (Request req, Response res) -> {
            String queryString = req.queryParams("q");
            String pageNumber = req.queryParams("page");

            // Process the query
            boolean isPhraseSearch = Utilities.isPhraseSearch(queryString);
            List<List<String>> queriesList = Utilities.processQuery(queryString);

            // Call the indexer

            // Call the ranker

            // ToDo: to be replaced with actual data

            String webpagesResponse = "app.webpagesCallBack({\"pages\":[ {\"title\":\"Google Inc.\", \"url\":\"http://www.google.com\", \"snippet\":\"Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine \"},{\"title\":\"Google Inc.\", \"url\":\"http://www.google.com\", \"snippet\":\"Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine \"},{\"title\":\"Google Inc.\", \"url\":\"http://www.google.com\", \"snippet\":\"Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine \"},{\"title\":\"Google Inc.\", \"url\":\"http://www.google.com\", \"snippet\":\"Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine \"},{\"title\":\"Google Inc.\", \"url\":\"http://www.google.com\", \"snippet\":\"Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine \"},{\"title\":\"Google Inc.\", \"url\":\"http://www.google.com\", \"snippet\":\"Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine \"},{\"title\":\"Google Inc.\", \"url\":\"http://www.google.com\", \"snippet\":\"Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine \"},{\"title\":\"Google Inc.\", \"url\":\"http://www.google.com\", \"snippet\":\"Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine \"},{\"title\":\"Google Inc.\", \"url\":\"http://www.google.com\", \"snippet\":\"Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine \"},{\"title\":\"Google Inc.\", \"url\":\"http://www.google.com\", \"snippet\":\"Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine \"},{\"title\":\"Google Inc.\", \"url\":\"http://www.google.com\", \"snippet\":\"Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine \"},{\"title\":\"Google Inc.\", \"url\":\"http://www.google.com\", \"snippet\":\"Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine \"},{\"title\":\"Google Inc.\", \"url\":\"http://www.google.com\", \"snippet\":\"Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine Google is a great search engine \"}], \"pagination\":{\"pages_count\": 10, \"current_page\": " + pageNumber + "}})";

            return webpagesResponse;
        });
    }
}
