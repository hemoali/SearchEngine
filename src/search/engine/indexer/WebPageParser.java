package search.engine.indexer;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;
import search.engine.utils.Constants;
import search.engine.utils.URLNormalizer;
import search.engine.utils.Utilities;
import search.engine.utils.WebUtilities;

import java.net.URL;
import java.util.*;


public class WebPageParser {

    //
    // Member variables
    //
    private StringBuilder sContent;
    private WebPage mPage;
    private SnowballStemmer mWordStemmer = new englishStemmer();


    /**
     * Parses the given web page document and returns a {@code WebPage} object
     * with the parsed data.
     *
     * @param url the web page URL object
     * @param doc the web page document to parse
     * @return a web page object constructed from the given document
     */
    public WebPage parse(URL url, Document doc) {
        // Initializing variables
        sContent = new StringBuilder();
        mPage = new WebPage();
        mPage.wordPosMap = new HashMap<>();
        mPage.stemScoreMap = new HashMap<>();
        mPage.stemWordsCount = new HashMap<>();

        // Assign page URL & title
        mPage.url = URLNormalizer.normalize(url);
        mPage.title = extractPageTitle(doc, url.getHost());

        // Parse to fill web page content and index
        dfs(doc.body(), "");

        // Assign words index variable
        mPage.content = sContent.toString().trim();

        return mPage;
    }

    /**
     * Extracts the web page title from the head tag.
     *
     * @param doc          the web page raw content
     * @param defaultTitle the web page default title if no title found in the web page document
     * @return the web page title
     */
    private String extractPageTitle(Document doc, String defaultTitle) {
        String ret = defaultTitle;
        String title = "";
        Elements titles = doc.head().select("title");

        if (titles.size() > 0) {
            title = titles.first().ownText().trim();
        }

        if (title.length() > 0) {
            ret = title;
            addToWordIndex(ret, "title");
        }

        return ret;
    }

    /**
     * Perform a depth-first search on the web page tags in order
     * to extract and parse the textual content.
     *
     * @param cur         current node in the DFS
     * @param previousTag the parent node tag name
     */
    private void dfs(Node cur, String previousTag) {
        // If its a text node then process its text
        if (cur instanceof TextNode) {
            TextNode node = (TextNode) cur;
            processText(node.text().trim(), previousTag);
            return;
        }

        // If it is an element node then recursively call the DFS function
        // with the children nodes of allowed tag
        if (cur instanceof Element) {
            Element element = (Element) cur;
            String tag = element.tagName();

            if (!Constants.ALLOWED_TAGS_SET.contains(tag)) {
                return;
            }

            for (Node child : cur.childNodes()) {
                dfs(child, tag);
            }
        }
    }

    /**
     * Processes the given string and constructs the words index.
     *
     * @param str the string to process
     * @param tag tag of the string
     */
    private void processText(String str, String tag) {
        if (str.isEmpty()) {
            return;
        }

        // Append the exact text to the page sContent variable
        sContent.append(str);
        sContent.append(" ");

        // Process the string and add it to the words index map
        addToWordIndex(str, tag);
    }

    /**
     * Process the string and add it to the words index map.
     *
     * @param str the string to process and add to index
     * @param tag the tag of the string
     */
    private void addToWordIndex(String str, String tag) {
        str = Utilities.processString(str);
        String words[] = str.split(" ");

        int tagScore = Constants.TAG_TO_SCORE_MAP.getOrDefault(tag, 1);

        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }

            //
            // Add position
            //
            mPage.wordPosMap.putIfAbsent(word, new ArrayList<>());
            mPage.wordPosMap.get(word).add(mPage.wordsCount++);

            //
            // Count stem and sum score
            //
            if (Utilities.stopWord(word)) {
                continue;
            }

            mWordStemmer.setCurrent(word);
            mWordStemmer.stem();

            String stem = mWordStemmer.getCurrent();

            int cnt = mPage.stemWordsCount.getOrDefault(stem, 0);
            int cur = mPage.stemScoreMap.getOrDefault(stem, 0);

            mPage.stemWordsCount.put(stem, cnt + 1);
            mPage.stemScoreMap.put(stem, cur + tagScore);
        }
    }

    /**
     * Extracts all out links from the given raw web page document
     * and adds them to {@code outLinks} list.
     *
     * @param doc the web page raw content
     */
    public static List<String> extractOutLinks(Document doc) {
        Set<String> outLinks = new HashSet<>();

        Elements links = doc.body().select("a[href]");

        for (Element element : links) {
            String link = element.attr("abs:href");

            if (WebUtilities.crawlable(link)) {
                try {
                    outLinks.add(URLNormalizer.normalize(new URL(link)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return new ArrayList<>(outLinks);
    }
}
