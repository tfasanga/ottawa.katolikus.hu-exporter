package info.ottawaimagyar.katolikus.exporter;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Post
{
    private final String url;
    private String title;
    private String content;
    private String date;

    public Post(String aInUrl)
    {
        url = aInUrl;
    }

    @Override
    public String toString()
    {
        return url + ", " + title + ", " + date;
    }

    public void download(Connection aInConnection, Collection<Image> images) throws IOException
    {
        aInConnection.url(url);
        Document lDocument = aInConnection.get();
        Element lMain = lDocument.getElementById("main");
        Elements lContents = lMain.getElementsByClass("content");

        if(lContents.size() == 1)
        {
            StringBuilder sb = new StringBuilder();
            Element lContent = lContents.first();

            collectImages(lContent, images);

            Elements lLightboxElements = lContent.getElementsByClass("lightbox");
            for (Element lLightboxElement : lLightboxElements)
            {
                Collection<Node> lImageNodes = extractImageNodes(lLightboxElement);
                Element lParent = lLightboxElement.parent();
                int i = lLightboxElement.siblingIndex();
                lParent.insertChildren(i, lImageNodes);
                lLightboxElement.remove();
            }

            Elements lChildElements = lContent.children();
            for (Element lChildElement : lChildElements)
            {
                if(lChildElement.hasClass("clear"))
                {
                    // no more post content
                    break;
                }

                if(title == null && lChildElement.tagName().equals("h1"))
                {
                    // the first h1 header is the title
                    title = lChildElement.html();
                }
                else
                {
                    String lStr = lChildElement.toString();
                    sb.append(lStr);
                }
            }

            content = sb.toString();

            Elements lDateElements = lContent.getElementsByClass("date");
            date = lDateElements.first().html();
        }
        else
        {
            System.out.println("More than one content in main section of post page " + toString());
        }
    }

    private static void collectImages(Element aInContent, Collection<Image> aInImages)
    {
        Elements lImageElements = aInContent.getElementsByTag("img");
        if(!lImageElements.isEmpty())
        {
            for (Element lImageElement : lImageElements)
            {
                Image lImage = new Image(lImageElement);
                aInImages.add(lImage);
            }
        }
    }

    private static Collection<Node> extractImageNodes(Element aInContent)
    {
        Collection<Node> lImageNodes = new LinkedList<>();

        Elements lImageElements = aInContent.getElementsByTag("img");
        if(!lImageElements.isEmpty())
        {
            for (Element lImageElement : lImageElements)
            {
                Node lThisNode = toNode(lImageElement);
                lImageNodes.add(lThisNode.clone());
            }
        }

        return lImageNodes;
    }

    private static Node toNode(Element aInElement)
    {
        int i = aInElement.siblingIndex();
        Node lNode = aInElement.parent().childNode(i);
        if(!lNode.nodeName().equals(aInElement.tagName()))
        {
            throw new RuntimeException(lNode.nodeName() + " != " + aInElement.tagName());
        }
        return lNode;
    }
}
