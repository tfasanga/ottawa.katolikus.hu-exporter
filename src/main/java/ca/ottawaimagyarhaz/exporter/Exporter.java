package ca.ottawaimagyarhaz.exporter;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Arrays;

class Exporter
{
    public void runWithArguments(String[] args) throws IOException
    {
        System.out.println("args = " + Arrays.toString(args));

        System.out.println("<!DOCTYPE html>");
        System.out.println("<html>");
        System.out.println("<body>");

        parseHirek();

        System.out.println("</body>");
        System.out.println("</html>");
    }

    public static String getSiteUrl()
    {
        return "http://www.ottawaimagyarhaz.ca";
    }

    private void parseHirek() throws IOException
    {
        final String lMainUrl = getSiteUrl();
        final String lEsemenyek2015Url = lMainUrl + "/" + "esemeacutenyek---2015.html";
        Connection lConnection = Jsoup.connect(lEsemenyek2015Url);
        Document lDocument = lConnection.get();

        Element lWsiteContent = lDocument.getElementById("wsite-content");

        int i = 0;
        Elements lMulticolElements = lWsiteContent.getElementsByClass("wsite-multicol");

        for (Element lMulticolElement : lMulticolElements)
        {
            Elements lColElements = lMulticolElement.getElementsByClass("wsite-multicol-col");
            for (Element lColElement : lColElements)
            {
                removeFormElements(lColElement);
                removeStyleElements(lColElement);
                do {} while (removeEmptyElements(lColElement));
                updateImgSrc(lColElement);
                if(!isEmptyDivElement(lColElement))
                {
                    String lHtml = lColElement.html();
                    System.out.println(lHtml);
                    i++;
                }
            }
            break;
        }
    }

    private void updateImgSrc(Element aInContent)
    {
        Elements lElements = aInContent.getElementsByTag("img");
        for (Element lElement : lElements)
        {
            String lSrc = lElement.attr("src");
            if(lSrc != null)
            {
                if(lSrc.startsWith("/"))
                {
                    String lNewSrc = getSiteUrl() + lSrc;
                    lElement.attr("src", lNewSrc);
                }
            }
        }
    }

    private boolean isEmptyDivElement(Element aInContent)
    {
        return aInContent.tagName().equals("div") && aInContent.childNodeSize() == 0;
    }


    private boolean removeEmptyElements(Element aInContent)
    {
        boolean lRemoved = false;
        Elements lElements = aInContent.getElementsByTag("div");
        for (Element lElement : lElements)
        {
            if(isEmptyDivElement(lElement))
            {
                lElement.remove();
                lRemoved = true;
            }
        }
        return lRemoved;
    }

    private void removeFormElements(Element aInContent)
    {
        Elements lFormElements = aInContent.getElementsByTag("form");
        for (Element lFormElement : lFormElements)
        {
            lFormElement.remove();
        }

        Elements lFormClasses = aInContent.getElementsByClass("wsite-form-field");
        for (Element lFormClass : lFormClasses)
        {
            lFormClass.remove();
        }
    }

    private void removeStyleElements(Element aInContent)
    {
        Elements lAllElements = aInContent.getAllElements();
        for (Element lAllElement : lAllElements)
        {
            lAllElement.removeAttr("style");
        }
    }
}

