package ca.ottawaimagyarhaz.exporter;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

        updateImgSrc(lWsiteContent);
        removeFormElements(lWsiteContent);
        removeStyleElements(lWsiteContent);

        final Path lOutFile = Paths.get("work", "test.html");
        String lHtml = lWsiteContent.toString();
        Files.write(lOutFile, lHtml.getBytes("utf-8"));
        System.out.println("Written to: " + lOutFile);

        parseContent(lWsiteContent);
    }

    private void parseContent(Element aInWsiteContent) throws IOException
    {

        int i = 0;
        Elements lMulticolElements = aInWsiteContent.getElementsByClass("wsite-multicol");

        for (Element lMulticolElement : lMulticolElements)
        {
            Element lParent = lMulticolElement.parent();
            Element lMainParent = lParent.parent();
            if(lMainParent.id().equals("wsite-content"))
            {
                StringBuilder sb = new StringBuilder();
                String lContent = lMulticolElement.toString();
                sb.append(lContent);
                sb.append("\n");
                i++;

                final Path lOutFile = Paths.get("work", "test_event_" + i + ".html");
                Files.write(lOutFile, sb.toString().getBytes("utf-8"));
                System.out.println("Written to: " + lOutFile);

                if( i >= 8)
                {
                    break;
                }
            }
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
        Elements lFormElements = aInContent.getElementsByClass("wsite-button");
        for (Element lFormElement : lFormElements)
        {
            lFormElement.remove();
        }

//        Elements lFormElements = aInContent.getElementsByTag("form");
//        for (Element lFormElement : lFormElements)
//        {
//            lFormElement.remove();
//        }
//
//        Elements lFormClasses = aInContent.getElementsByClass("wsite-form-field");
//        for (Element lFormClass : lFormClasses)
//        {
//            lFormClass.remove();
//        }
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

