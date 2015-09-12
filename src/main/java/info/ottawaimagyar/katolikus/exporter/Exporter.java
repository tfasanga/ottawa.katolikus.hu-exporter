package info.ottawaimagyar.katolikus.exporter;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Exporter
{
    public static String getSiteUrl()
    {
        return "http://ottawa.katolikus.ca";
    }

    public void runWithArguments(String[] args) throws IOException
    {
        System.out.println("args = " + Arrays.toString(args));

        parseHirek();
    }

    private void parseHirek() throws IOException
    {
        Collection<Image> lImageList = new ArrayList<>();

        final String lMainUrl = getSiteUrl();
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
        }

        LinkedList<String> lPageLinkList = new LinkedList<>();
        for (String lPageLink : lPageLinkSet)
        {
            lPageLinkList.add(lPageLink);
        }

        Collections.reverse(lPageLinkList);

        List<Post> lPosts = new ArrayList<>();

        for (String lPageLink : lPageLinkList)
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

        Iterator<Post> lIterator = lPosts.iterator();
        if(lIterator.hasNext())
        {
            Post lPost = lIterator.next();

            lPost.download(lConnection, lImageList);
        }

        if(!lImageList.isEmpty())
        {
            Map<String, Image> lImageMap = new LinkedHashMap<>();

            for (Image lImage : lImageList)
            {
                lImageMap.put(lImage.getUrl(), lImage);
            }

            try(PrintWriter writer = new PrintWriter("images.xml", "UTF-8");)
            {
                XmlWriter lXmlWriter = new XmlWriter(writer, 4);
                lXmlWriter.begin();

                lXmlWriter.start("images");
                for (Map.Entry<String, Image> lEntry : lImageMap.entrySet())
                {
                    Image lImage = lEntry.getValue();
                    String lUrl = lEntry.getKey();

                    lXmlWriter.contentTag("img-url", lUrl);
                }
                lXmlWriter.end();
            }
        }

    }
}
