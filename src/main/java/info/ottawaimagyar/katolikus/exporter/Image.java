package info.ottawaimagyar.katolikus.exporter;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

import java.io.IOException;

class Image
{
    private final int width;
    private final int height;
    private final String alt;
    private final String src;
    private final String title;
    private final String url;

    public Image(Element aInElement)
    {
        src = aInElement.attr("src");
        alt = aInElement.attr("alt");
        title = aInElement.attr("title");
        url = buildUrl(src);

        String lWidth = aInElement.attr("width");
        if(lWidth != null && !lWidth.isEmpty())
        {
            width = Integer.parseInt(lWidth);
        }
        else
        {
            width = 0;
        }

        String lHeight = aInElement.attr("height");
        if(lHeight != null && !lHeight.isEmpty())
        {
            height = Integer.parseInt(lHeight);
        }
        else
        {
            height = 0;
        }
    }

    @Override
    public String toString()
    {
        return "image url=" + url + " title=" + title;
    }

    static String buildUrl(String src)
    {
        String lImgUrl;
        if(src.startsWith("http:"))
        {
            lImgUrl = src;
        }
        else
        {
            String lSiteUrl = Exporter.getSiteUrl();
            lImgUrl = lSiteUrl + src;
        }

        return lImgUrl;
    }

    public String getUrl()
    {
        return url;
    }
}
