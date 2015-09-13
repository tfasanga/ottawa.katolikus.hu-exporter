package info.ottawaimagyar.katolikus.exporter;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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

        List<String> lPageLinkList = getPageLinks(lDocument);

        PostList lPosts = new PostList(20);

        for (String lPageLink : lPageLinkList)
        {
            String lPageUrl = lHirekUrl + "/" + lPageLink;
            lConnection.url(lPageUrl);

            System.out.println("Getting Page, Url = " + lPageUrl);

            Document lPageDocument = lConnection.get();

            Element lMain = lPageDocument.getElementById("main");

            Elements lContents = lMain.getElementsByClass("content");

            int lPageNum = extractPageNum(lPageLink);

            System.out.println("Page #" + lPageNum + " has " + lContents.size() + " posts");

            for (Element lContent : lContents)
            {
                Elements lReadmoreElements = lContent.getElementsByClass("readmore");
                if (!lReadmoreElements.isEmpty())
                {
                    Element lFirst = lReadmoreElements.first();
                    String lPostLink = lFirst.attr("href");
                    String lPostUrl = lMainUrl + lPostLink;
                    Post lPost = new Post(lPostUrl);
                    lPosts.add(lPost);
                } else
                {
                    System.out.println("There is no ReadmoreElement");
                }
            }
            //break; // enable for test only
        }

        List<List<Post>> lBucketList = lPosts.getBucketList();

        System.out.println("Number of buckets: " + lBucketList.size());
        System.out.println("Number of posts: " + lPosts.size());

        int lNum = 1;
        for (List<Post> lPostList : lBucketList)
        {
            System.out.println("Starting downloading batch #" + lNum);

            for (Post lPost : lPostList)
            {
                lPost.download(lConnection, lImageList);
                System.out.println("Downloaded post: " + lPost);
            }

            savePostsAsWordpressRssXml(lPostList, lNum++);
        }

        saveImageLinks(lImageList);
    }

    private List<String> getPageLinks(Document aInDocument)
    {
        Set<String> lPageLinkSet = new LinkedHashSet<>();

        Elements lPagers = aInDocument.getElementsByClass("pager");
        if (lPagers.size() > 0)
        {
            Element lFirstPager = lPagers.first();
            List<Node> lChildNodes = lFirstPager.childNodes();
            for (Node lChildNode : lChildNodes)
            {
                String lHref = lChildNode.attr("href");
                lPageLinkSet.add(lHref);
            }
        }

        System.out.println("Number of pages: " + lPageLinkSet.size());

        LinkedList<String> lPageLinkList = new LinkedList<>();
        for (String lPageLink : lPageLinkSet)
        {
            lPageLinkList.add(lPageLink);
        }

        Collections.reverse(lPageLinkList);

        return lPageLinkList;
    }

    private static int extractPageNum(String lPageLink)
    {
        String[] lSplit = lPageLink.split("=");
        return Integer.parseInt(lSplit[1]);
    }

    private void saveImageLinks(Collection<Image> aInImageList)
            throws FileNotFoundException, UnsupportedEncodingException
    {
        Map<String, Image> lImageMap = new LinkedHashMap<>();

        for (Image lImage : aInImageList)
        {
            lImageMap.put(lImage.getUrl(), lImage);
        }

        try (PrintWriter lPrintWriter = new PrintWriter("images.xml", "UTF-8");)
        {
            XmlWriter lXmlWriter = new XmlWriter(lPrintWriter, 4);
            lXmlWriter.begin();

            lXmlWriter.start("images");
            for (String lUrl : lImageMap.keySet())
            {
                lXmlWriter.contentTag("img-url", lUrl);
            }
            lXmlWriter.end();
        }

        try (PrintWriter lPrintWriter = new PrintWriter("images.txt", "UTF-8");)
        {
            lImageMap.keySet().forEach(lPrintWriter::println);
        }
    }

    void savePostsAsWordpressRssXml(List<Post> aInPosts, int aInNumber) throws FileNotFoundException, UnsupportedEncodingException
    {
        String lFileName = "posts_" + aInNumber + ".xml";
        System.out.println("Saving post to file " + lFileName);

        try (PrintWriter lPrintWriter = new PrintWriter(lFileName, "UTF-8");)
        {
            XmlWriter lXmlWriter = new XmlWriter(lPrintWriter, 4);
            lXmlWriter.begin();

            XmlWriter.Attributes lRss = new XmlWriter.Attributes();
            lRss.add("version", "2.0");
            lRss.add("xmlns:excerpt", "http://wordpress.org/export/1.2/excerpt/");
            lRss.add("xmlns:content", "http://purl.org/rss/1.0/modules/content/");
            lRss.add("xmlns:wfw", "http://wellformedweb.org/CommentAPI/");
            lRss.add("xmlns:dc", "http://purl.org/dc/elements/1.1/");

            lXmlWriter.start("rss", lRss);

            lXmlWriter.start("channel");
            lXmlWriter.contentTag("title", "Ottawai Magyar Katolikus Közösség");
            lXmlWriter.contentTag("link", "http://ottawa.katolikus.ca");
            lXmlWriter.contentTag("description", "");
            lXmlWriter.contentTag("pubDate", "Sat, 12 Sep 2015 23:36:16 +0000");
            lXmlWriter.contentTag("language", "hu-HU");
            lXmlWriter.contentTag("generator", "http://wordpress.org/?v=4.3");

            for (Post lPost : aInPosts)
            {
                System.out.println("Generating RSS item for " + lPost.getTitle());

                lXmlWriter.start("item");

                lXmlWriter.contentTag("title", lPost.getTitle());
                lXmlWriter.contentTag("link", lPost.getUrl());
                PostDate lDate = lPost.getDate();
                if (lDate != null)
                {
                    lXmlWriter.contentTag("pubDate", lDate.toRssPubDate());
                } else
                {
                    System.out.println("Post has no date: " + lPost);
                }
                lXmlWriter.contentTag("guid", "isPermaLink", "false", lPost.getUrl());
                lXmlWriter.contentTag("description", "");
                lXmlWriter.contentTagCData("dc:creator", "katolikus");
                lXmlWriter.contentTagCData("content:encoded", lPost.getContent());
                lXmlWriter.contentTagCData("excerpt:encoded", lPost.getExcerpt());

                XmlWriter.Attributes lCategory = new XmlWriter.Attributes();
                lCategory.add("domain", "category");
                lCategory.add("nicename", "hirek");
                lXmlWriter.contentTagCData("category", lCategory, "Hírek");

                lXmlWriter.end(); // item
            }

            lXmlWriter.end(); // channel
            lXmlWriter.end(); // rss
        }
    }
}
