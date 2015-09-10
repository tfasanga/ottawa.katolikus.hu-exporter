/**
 *
 */
package info.ottawaimagyar.katolikus.exporter;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class Exporter
{
    public void runWithArguments(String[] args) throws IOException
    {
        System.out.println("args = " + Arrays.toString(args));

        parseHirek();
    }

    private void parseHirek() throws IOException
    {
        final String lMainUrl = "http://ottawa.katolikus.ca";
        final String lHirekUrl = lMainUrl + "/" + "hirek";
        Connection lConnection = Jsoup.connect(lHirekUrl);
        Document lDocument = lConnection.get();
        Set<String> lPageLinkSet = new LinkedHashSet<>();
        Elements lPagers = lDocument.getElementsByClass("pager");
        if(lPagers.size() > 0)
        {
            Element lFirstPager = lPagers.first();
            List<Node> lChildNodes = lFirstPager.childNodes();
            for (Node lChildNode : lChildNodes)
            {
                String lHref = lChildNode.attr("href");
                lPageLinkSet.add(lHref);
            }

            System.out.println("lPageLinkSet.size() = " + lPageLinkSet.size());

//            for (String lHref : lPageLinkSet)
//            {
//                System.out.println("lHref = " + lHref);
//            }
        }

        List<Post> lPosts = new ArrayList<>();

        for (String lPageLink : lPageLinkSet)
        {
            String lPageUrl = lHirekUrl + "/" + lPageLink;
            lConnection.url(lPageUrl);
            System.out.println("lPageUrl = " + lPageUrl);

            Document lPageDocument = lConnection.get();

            Element lMain = lPageDocument.getElementById("main");

            Elements lContents = lMain.getElementsByClass("content");
            System.out.println("Page / items = [" + lPageLink + "] -> " + lContents.size());

            for (Element lContent : lContents)
            {
                Elements lReadmoreElements = lContent.getElementsByClass("readmore");
                if(!lReadmoreElements.isEmpty())
                {
                    Element lFirst = lReadmoreElements.first();
                    String lPostLink = lFirst.attr("href");
                    String lPostUrl = lMainUrl + lPostLink;
                    Post lPost = new Post(lPostUrl);
                    lPosts.add(lPost);

                    Elements lDateElements = lContent.getElementsByClass("date");
                    Node lDateStr = lDateElements.first().childNode(0);
                    lPost.setDateStr(lDateStr);
                }
                else
                {
                    System.out.println("There is no ReadmoreElement");
                }
            }

            break;
        }

        for (Post lPost : lPosts)
        {
            System.out.println("lPost = " + lPost);
        }
    }
}
