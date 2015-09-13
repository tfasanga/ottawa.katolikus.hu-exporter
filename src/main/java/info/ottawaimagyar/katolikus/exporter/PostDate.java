package info.ottawaimagyar.katolikus.exporter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PostDate
{
    private static final Map<String, String> dict;
    private static final Set<String> missing = new HashSet<>();
    private final String year;
    private final String month;
    private final String dayNum;
    private final String day;
    private final String hour;

    static
    {
        dict = new HashMap<>();
        dict.put("janu"+(char)225+"r", "Jan");
        dict.put("febru"+(char)225+"r", "Feb");
        dict.put("m"+(char)225+"rcius", "Mar");
        dict.put((char)225+"prilis", "Apr");
        dict.put("m"+(char)225+"jus", "May");
        dict.put("j"+(char)250+"nius", "Jun");
        dict.put("j"+(char)250+"lius", "Jul");
        dict.put("augusztus", "Aug");
        dict.put("szeptember", "Sep");
        dict.put("okt"+(char)243+"ber", "Oct");
        dict.put("november", "Nov");
        dict.put("december", "Dec");

        dict.put("h"+(char)233+"tf"+(char)337, "Mon");
        dict.put("kedd", "Tue");
        dict.put("szerda", "Wed");
        dict.put("cs"+(char)252+"t"+(char)246+"rt"+(char)246+"k", "Thu");
        dict.put("p"+(char)233+"ntek", "Fri");
        dict.put("szombat", "Sat");
        dict.put("vas"+(char)225+"rnap", "Sun");

        dict.put("Jan", "1");
        dict.put("Feb", "2");
        dict.put("Mar", "3");
        dict.put("Apr", "4");
        dict.put("May", "5");
        dict.put("Jun", "6");
        dict.put("Jul", "7");
        dict.put("Aug", "8");
        dict.put("Sep", "9");
        dict.put("Oct", "10");
        dict.put("Nov", "11");
        dict.put("Dec", "12");
    }

    // "2010. november 6. (szombat) 12:59"
    private String postDate;

    public PostDate(String aInPostDate)
    {
        postDate = aInPostDate;

        String[] lSplit = postDate.split(" ");
        year = lSplit[0].replace(".", "");
        String lHonap = lSplit[1];
        month = translate(lHonap);
        dayNum = lSplit[2].replace(".", "");
        String lNap = lSplit[3].replace("(", "").replace(")", "");
        day = translate(lNap);
        hour = lSplit[4] + ":00";
    }

    public String toRssPubDate()
    {
        // pubDate: Sun, 31 Oct 2010 23:28:57 +0000
        String lValue =
                day + ", " +
                dayNum + " " +
                month + " " +
                year + " " +
                hour + " " +
                "+0000";

        return lValue;
    }

    public String toWpPostDate()
    {
        // <wp:post_date>2010-10-31 19:28:57</wp:post_date>
        String lValue =
                year + "-" +
                translate(month) + "-" +
                dayNum + " " +
                hour;

        return lValue;
    }

    private static String translate(String aInValue)
    {
        String lDayName = dict.get(aInValue);
        if(lDayName == null)
        {
            if(missing.add(aInValue))
            {
                System.out.println("missing: \"" + aInValue + "\"");
            }

            lDayName = aInValue;
        }

        return lDayName;
    }
}
