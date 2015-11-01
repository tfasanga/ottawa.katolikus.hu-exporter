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

        parseHirek();
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
                i++;
                removeFormElements(lColElement);
                removeStyleElements(lColElement);
                removeEmptyElements(lColElement);

                String lHtml = lColElement.html();
                System.out.println();
                System.out.println("<!-- COL #" + i + " -->");
                System.out.println(lHtml);
            }
            break;
        }
    }

    private void removeEmptyElements(Element aInContent)
    {
        Elements lElements = aInContent.getElementsByTag("div");
        for (Element lElement : lElements)
        {

        }
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

