package search.engine.server;

import search.engine.utils.Constants;
import search.engine.utils.Utilities;
import spark.Request;
import spark.Response;

import java.util.ArrayList;

import static spark.Spark.get;
import static spark.SparkBase.port;

public class Server {
    /**
     * Start serving the clients
     */
    public static void serve() {
        // Server
        port(Constants.PORT);

        get("/search", (Request req, Response res) -> {
            String queryString = req.queryParams("q");

            // Process the query
            boolean isPhraseSearch = Utilities.checkIfPhraseSearch(queryString);
            ArrayList<ArrayList<String>> queriesList = Utilities.processQuery(queryString);

            // Call the indexer

            // Call the ranker

            return queryString;
        });
    }
}