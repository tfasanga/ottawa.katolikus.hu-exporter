/**
 *
 */
package info.ottawaimagyar.katolikus.exporter;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Arrays;

public class Exporter
{
    public void runWithArguments(String[] args) throws IOException
    {
        System.out.println("args = " + Arrays.toString(args));

        parseHirek();
    }

    private void parseHirek() throws IOException
    {
        Connection lConnection = Jsoup.connect("http://ottawa.katolikus.ca/hirek");
        Document lDocument = lConnection.get();
        Elements lPagers = lDocument.getElementsByClass("pager");
        Elements lContents = lDocument.getElementsByClass("content");
    }
}
